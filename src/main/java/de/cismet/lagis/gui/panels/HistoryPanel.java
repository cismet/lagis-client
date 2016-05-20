/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * HistoryPanel.java
 *
 * Created on August 19, 2007, 12:22 PM
 */
package de.cismet.lagis.gui.panels;

import javafx.application.Platform;

import netscape.javascript.JSObject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Toolkit;

import java.util.*;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;

import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.interfaces.FlurstueckChangeListener;

import de.cismet.lagis.thread.BackgroundUpdateThread;

import de.cismet.lagis.widget.AbstractWidget;

import de.cismet.tools.StaticDebuggingTools;

import de.cismet.tools.configuration.Configurable;

import de.cismet.tools.gui.FXWebViewPanel;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class HistoryPanel extends AbstractWidget implements FlurstueckChangeListener, Configurable {

    //~ Static fields/initializers ---------------------------------------------

    // TODO Auslagern in ConfigFile
    private static HistoryPanel instance;

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Timer levelTimer = new Timer();
    private HashMap<String, FlurstueckSchluesselCustomBean> nodeToKeyMap =
        new HashMap<String, FlurstueckSchluesselCustomBean>();
    private FlurstueckCustomBean currentFlurstueck;
    // private Thread panelRefresherThread;
    private final BackgroundUpdateThread<FlurstueckCustomBean> updateThread;
    // TODO THREAD
    // TODO NOT DIRECTLY OUTPUT THE ERRORS ON ERR
    // private double cellxcoordinate =
    private boolean callBackInited = false;
    private JPanel webViewPanel = new JPanel(new BorderLayout());
    private FXWebViewPanel webView;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbxHistoryOptions;
    private javax.swing.JComboBox cbxHistoryOptions1;
    private javax.swing.JComboBox cbxHistoryType;
    private javax.swing.JCheckBox ckxHoldFlurstueck;
    private javax.swing.JCheckBox ckxScaleToFit;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lblDatumEnt;
    private javax.swing.JLabel lblDatumEntWert;
    private javax.swing.JLabel lblDatumHist;
    private javax.swing.JLabel lblDatumHistWert;
    private javax.swing.JLabel lblVisulaization;
    private javax.swing.JPanel panHistInfo;
    private javax.swing.JPanel panInformation;
    private javax.swing.JPanel panOptions;
    private javax.swing.JSpinner sprLevels;
    private javax.swing.JSpinner sprLevels1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new HistoryPanel object.
     */
    public HistoryPanel() {
        setIsCoreWidget(true);
        initComponents();

        final boolean sibblingOptionsEnabled = StaticDebuggingTools.checkHomeForFile(
                "cismetLagisSibblingOptionsEnabled");

        sprLevels1.setVisible(sibblingOptionsEnabled);
        cbxHistoryOptions1.setVisible(sibblingOptionsEnabled);

        final SpinnerNumberModel sprLevelModel = new SpinnerNumberModel(1, 1, 100, 1);
        sprLevels.setModel(sprLevelModel);
        sprLevels.setEnabled(false);
        final SpinnerNumberModel sprLevelModel1 = new SpinnerNumberModel(1, 1, 100, 1);
        sprLevels1.setModel(sprLevelModel1);
        sprLevels1.setEnabled(false);

        add(webViewPanel, BorderLayout.CENTER);
        try {
            new Thread() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ex) {
                        }
                        webView = new FXWebViewPanel();
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    webViewPanel.add(webView, BorderLayout.CENTER);
                                    try {
                                        final String s = IOUtils.toString(
                                                this.getClass().getResourceAsStream("dagreTemplate.html"));
                                        webView.loadContent(s);
                                    } catch (Exception e) {
                                        log.fatal(e, e);
                                    }
                                }
                            });
                    }
                }.start();
        } catch (Exception e) {
            log.error("Error during initialization of HistoryPanel", e);
        }

        updateThread = new BackgroundUpdateThread<FlurstueckCustomBean>() {

                @Override
                protected void update() {
                    try {
                        try {
                            final String script = "loading();";
                            if (!callBackInited) {
                                Thread.sleep(1000);

                                Platform.runLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            try {
                                                final JSObject jsobj = (JSObject)webView.getWebEngine()
                                                            .executeScript("window");
                                                jsobj.setMember("java", HistoryPanel.this);
                                                log.info("Callback steht zur Verfügung");
                                                callBackInited = true;
                                            } catch (Exception e) {
                                                log.error("Callback steht nicht zur Verfügung", e);
                                            }
                                        }
                                    });
                            }
                            Platform.runLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            webView.getWebEngine().executeScript(script);
                                        } catch (Exception e) {
                                            log.error("Error when executing script " + script, e);
                                        }
                                    }
                                });
                        } catch (Exception e) {
                            log.error("Error in Backgroundthread", e);
                        }
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        clearComponent();
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        log.info("Konstruiere Flurstückhistoriengraph");
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        if (log.isDebugEnabled()) {
                            // ToDo remove Strings
                            log.debug("Erstelle Historien Anfrage:");
                        }

                        // -----

                        CidsBroker.HistoryLevel level;
                        final int levelCount;
                        if (cbxHistoryOptions.getSelectedItem().equals("Direkte Vorgänger/Nachfolger")) {
                            level = CidsBroker.HistoryLevel.DIRECT_RELATIONS;
                            levelCount = 0;
                            if (log.isDebugEnabled()) {
                                log.debug("nur angrenzendte Flurstücke");
                            }
                        } else if (cbxHistoryOptions.getSelectedItem().equals("Begrenzte Tiefe")) {
                            level = CidsBroker.HistoryLevel.CUSTOM;
                            levelCount = ((Number)sprLevels.getValue()).intValue();
                            if (log.isDebugEnabled()) {
                                log.debug("begrentze Tiefe mit " + levelCount + " Stufen");
                            }
                        } else {
                            level = CidsBroker.HistoryLevel.All;
                            levelCount = 0;
                            if (log.isDebugEnabled()) {
                                log.debug("Alle Levels");
                            }
                        }

                        // -----

                        CidsBroker.HistorySibblingLevel levelSibbling;
                        final int levelSibblingCount;
                        if (cbxHistoryOptions1.getSelectedItem().equals("Geschwister")) {
                            levelSibbling = CidsBroker.HistorySibblingLevel.SIBBLING_ONLY;
                            levelSibblingCount = 0;
                        } else if (cbxHistoryOptions1.getSelectedItem().equals("Geschwister, vollständig")) {
                            levelSibbling = CidsBroker.HistorySibblingLevel.FULL;
                            levelSibblingCount = 0;
                        } else if (cbxHistoryOptions1.getSelectedItem().equals("Geschwister, begrenzte Tiefe")) {
                            levelSibbling = CidsBroker.HistorySibblingLevel.CUSTOM;
                            levelSibblingCount = ((Number)sprLevels1.getValue()).intValue();
                        } else { // keine Geschwister
                            levelSibbling = CidsBroker.HistorySibblingLevel.NONE;
                            levelSibblingCount = 0;
                        }

                        // -----

                        CidsBroker.HistoryType type;
                        if (cbxHistoryType.getSelectedItem().equals("Nur Nachfolger")) {
                            type = CidsBroker.HistoryType.SUCCESSOR;
                            if (log.isDebugEnabled()) {
                                log.debug("nur Nachfolger");
                            }
                        } else if (cbxHistoryType.getSelectedItem().equals("Nur Vorgänger")) {
                            type = CidsBroker.HistoryType.PREDECESSOR;
                            if (log.isDebugEnabled()) {
                                log.debug("nur Vorgänger");
                            }
                        } else {
                            type = CidsBroker.HistoryType.BOTH;
                            if (log.isDebugEnabled()) {
                                log.debug("Vorgänger/Nachfolger");
                            }
                        }

                        // -----

                        nodeToKeyMap = new HashMap<String, FlurstueckSchluesselCustomBean>();

                        final String dotGraphRepresentation = CidsBroker.getInstance()
                                    .getHistoryGraph(
                                        getCurrentObject(),
                                        level,
                                        levelCount,
                                        levelSibbling,
                                        levelSibblingCount,
                                        type,
                                        nodeToKeyMap);

                        try {
                            final String rawscript = "var graphInput='" + dotGraphRepresentation
                                        + "'; draw(graphInput);";
                            final String script = rawscript.replaceAll("\n", "");
                            log.info("script to run:" + script);
                            Platform.runLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            webView.getWebEngine().executeScript(script);
                                        } catch (Exception e) {
                                            log.error("Error when executing script " + script, e);
                                        }
                                    }
                                });
                        } catch (Exception e) {
                            log.error("Error in Backgroundthread", e);
                        }
                    } catch (Exception ex) {
                        log.error("Fehler im refresh thread: ", ex);
                    }
                }

                @Override
                protected void cleanup() {
                }
            };
        updateThread.setPriority(Thread.NORM_PRIORITY);
        updateThread.start();

        instance = this;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  info  DOCUMENT ME!
     */
    public void fstckClicked(final String info) {
        Toolkit.getDefaultToolkit().beep();

        final FlurstueckSchluesselCustomBean hit = nodeToKeyMap.get("\"" + info + "\"");
        if ((hit != null) && !currentFlurstueck.getFlurstueckSchluessel().equals(hit)
                    && (hit.toString() != null) && hit.isEchterSchluessel()) {
            if (log.isDebugEnabled()) {
                log.debug("Neuer Schlüssel ist != null");
            }
            LagisBroker.getInstance().loadFlurstueck(hit);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Neuer Schlüssel == null oder gleich oder toString == null");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static HistoryPanel getInstance() {
        if (instance == null) {
            instance = new HistoryPanel();
        }

        return instance;
    }

//    /**
//     * DOCUMENT ME!
//     *
//     * @return  DOCUMENT ME!
//     */
//    public Image getImage() {
//        final int width = this.gp.getWidth();
//        final int height = this.gp.getHeight();
//
//        final BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//
////        final Graphics imgGraphics = img.getGraphics();
////        imgGraphics.setColor(gp.getBackground());
////        imgGraphics.fillRect(0, 0, img.getWidth(), img.getHeight());
//
//        gp.print(img.getGraphics());
//
//
//
//
//        return img;
//    }
    @Override
    public void refresh(final Object refreshObject) {
    }

    /**
     * DOCUMENT ME!
     */
    public void refresh() {
        if (currentFlurstueck == null) {
            return;
        }
        updateInformation();
        try {
            // TODO FALSE
            // cancel active running thread
            updateThread.notifyThread(currentFlurstueck);
        } catch (Exception ex) {
            log.error("Fehler beim laden der FlurstücksHistory");
        }
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
        if (log.isDebugEnabled()) {
            log.debug("FlurstueckSearchPanel --> setComponentEditable finished");
        }
    }

    @Override
    public String getWidgetName() {
        return "History Panel";
    }

    @Override
    public synchronized void clearComponent() {
    }

    @Override
    public void flurstueckChanged(final FlurstueckCustomBean newFlurstueck) {
        levelTimer.cancel();
        currentFlurstueck = newFlurstueck;
        updateInformation();
        if ((nodeToKeyMap.get("\"" + newFlurstueck + "\"") != null) && ckxHoldFlurstueck.isSelected()) {
            if (log.isDebugEnabled()) {
                log.debug("Flurstück ist bereits in der Historie vorhanden und hold ist aktiviert --> kein update");
            }
            LagisBroker.getInstance().flurstueckChangeFinished(HistoryPanel.this);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Flurstückchanged HistoryPanel");
            }
            try {
                // TODO FALSE
                // cancel active running thread
                updateThread.notifyThread(currentFlurstueck);
                LagisBroker.getInstance().flurstueckChangeFinished(HistoryPanel.this);
            } catch (Exception ex) {
                // TODO FALSE
                LagisBroker.getInstance().flurstueckChangeFinished(HistoryPanel.this);
                log.error("Fehler beim laden der FlurstücksHistory");
            }
        }
        // ugly better solution ?? --> because how should a programmer  know that he must use this method if he uses the
        // interface ??
    }

    /**
     * DOCUMENT ME!
     */
    private void updateInformation() {
        if ((currentFlurstueck != null) && (currentFlurstueck.getFlurstueckSchluessel() != null)) {
            final FlurstueckSchluesselCustomBean currentKey = currentFlurstueck.getFlurstueckSchluessel();
            if (currentKey.getDatumLetzterStadtbesitz() != null) {
//                lblDatumLSBWert.setText(LagisBroker.getDateFormatter().format(currentKey.getDatumLetzterStadtbesitz()));
//                lblDatumLSBWert.setToolTipText(currentKey.getDatumLetzterStadtbesitz().toString());
            } else {
//                lblDatumLSBWert.setText("Keine Angabe");
//                lblDatumLSBWert.setToolTipText("");
            }

            if (currentKey.getGueltigBis() != null) {
                lblDatumHistWert.setText(LagisBroker.getDateFormatter().format(currentKey.getGueltigBis()));
                lblDatumHistWert.setToolTipText(currentKey.getGueltigBis().toString());
            } else {
                lblDatumHistWert.setText("Keine Angabe");
                lblDatumHistWert.setToolTipText("");
            }

            if (currentKey.getEntstehungsDatum() != null) {
                lblDatumEntWert.setText(LagisBroker.getDateFormatter().format(currentKey.getEntstehungsDatum()));
                lblDatumEntWert.setToolTipText(currentKey.getEntstehungsDatum().toString());
            } else {
                lblDatumEntWert.setText("Keine Angabe");
                lblDatumEntWert.setToolTipText("");
            }
        } else {
            log.warn("Flurstückschlüssel ist == null");
            lblDatumEntWert.setText("Keine Angabe");
            lblDatumEntWert.setToolTipText("");
//            lblDatumLSBWert.setText("Keine Angabe");
//            lblDatumLSBWert.setToolTipText("");
            lblDatumHistWert.setText("Keine Angabe");
            lblDatumHistWert.setToolTipText("");
        }
    }

    // TODO USE
    @Override
    public Icon getWidgetIcon() {
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel5 = new javax.swing.JPanel();
        panHistInfo = new javax.swing.JPanel();
        panOptions = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblVisulaization = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        cbxHistoryOptions = new javax.swing.JComboBox();
        sprLevels = new javax.swing.JSpinner();
        cbxHistoryType = new javax.swing.JComboBox();
        cbxHistoryOptions1 = new javax.swing.JComboBox();
        sprLevels1 = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        ckxScaleToFit = new javax.swing.JCheckBox();
        ckxHoldFlurstueck = new javax.swing.JCheckBox();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        panInformation = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblDatumEnt = new javax.swing.JLabel();
        lblDatumEntWert = new javax.swing.JLabel();
        lblDatumHist = new javax.swing.JLabel();
        lblDatumHistWert = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));

        setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.GridBagLayout());

        panHistInfo.setLayout(new java.awt.GridBagLayout());

        panOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        panOptions.setLayout(new java.awt.GridBagLayout());

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Optionen:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 0, 0);
        jPanel3.add(jLabel1, gridBagConstraints);

        lblVisulaization.setText("Darstellung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 0, 0);
        jPanel3.add(lblVisulaization, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        cbxHistoryOptions.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "Direkte Vorgänger/Nachfolger", "Vollständig", "Begrenzte Tiefe" }));
        cbxHistoryOptions.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbxHistoryOptionsActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(cbxHistoryOptions, gridBagConstraints);

        sprLevels.setMinimumSize(new java.awt.Dimension(60, 1));
        sprLevels.setPreferredSize(new java.awt.Dimension(60, 1));
        sprLevels.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    sprLevelsStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel1.add(sprLevels, gridBagConstraints);

        cbxHistoryType.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "Alle Flurstücke", "Nur Nachfolger", "Nur Vorgänger" }));
        cbxHistoryType.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbxHistoryTypeActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel1.add(cbxHistoryType, gridBagConstraints);

        cbxHistoryOptions1.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] {
                    "keine Geschwister",
                    "Geschwister",
                    "Geschwister, vollständig",
                    "Geschwister, begrenzte Tiefe"
                }));
        cbxHistoryOptions1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbxHistoryOptions1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel1.add(cbxHistoryOptions1, gridBagConstraints);

        sprLevels1.setMinimumSize(new java.awt.Dimension(60, 1));
        sprLevels1.setPreferredSize(new java.awt.Dimension(60, 1));
        sprLevels1.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    sprLevels1StateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 4, 0, 0);
        jPanel1.add(sprLevels1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        jPanel3.add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        ckxScaleToFit.setSelected(true);
        ckxScaleToFit.setText("an Bildschimgröße anpassen");
        ckxScaleToFit.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    ckxScaleToFitActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = -5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel2.add(ckxScaleToFit, gridBagConstraints);

        ckxHoldFlurstueck.setText("Historie halten");
        ckxHoldFlurstueck.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    ckxHoldFlurstueckActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel2.add(ckxHoldFlurstueck, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(filler4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        jPanel3.add(jPanel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(filler3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        panOptions.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panHistInfo.add(panOptions, gridBagConstraints);

        panInformation.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        panInformation.setLayout(new java.awt.GridBagLayout());

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel3.setText("Informationen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel4.add(jLabel3, gridBagConstraints);

        lblDatumEnt.setText("Datum Entstehung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        jPanel4.add(lblDatumEnt, gridBagConstraints);

        lblDatumEntWert.setText("Keine Angabe");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        jPanel4.add(lblDatumEntWert, gridBagConstraints);

        lblDatumHist.setText("Datum Historisch seit:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        jPanel4.add(lblDatumHist, gridBagConstraints);

        lblDatumHistWert.setText("Keine Angabe");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        jPanel4.add(lblDatumHistWert, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(filler1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(filler2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        panInformation.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        panHistInfo.add(panInformation, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        jPanel5.add(panHistInfo, gridBagConstraints);

        add(jPanel5, java.awt.BorderLayout.SOUTH);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void ckxScaleToFitActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_ckxScaleToFitActionPerformed

        Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    try {
                        webView.getWebEngine().executeScript("setFitToScreen(" + ckxScaleToFit.isSelected() + ");");
                    } catch (Exception e) {
                        log.error("Error during executing setFitToScreen ", e);
                    }
                }
            });
    } //GEN-LAST:event_ckxScaleToFitActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbxHistoryOptionsActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbxHistoryOptionsActionPerformed
        if ("Begrenzte Tiefe".equals(cbxHistoryOptions.getSelectedItem())) {
            if (log.isDebugEnabled()) {
                log.debug("Begrentzte Tiefe ausgewählt");
            }
            levelTimer = new Timer();
            levelTimer.schedule(new delayedRefresh(), 2000);
            sprLevels.setEnabled(true);
        } else {
            sprLevels.setValue(1);
            sprLevels.setEnabled(false);
            levelTimer.cancel();
            refresh();
        }
    }                                                                                     //GEN-LAST:event_cbxHistoryOptionsActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbxHistoryTypeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbxHistoryTypeActionPerformed
        final boolean nurNachfolger = "Nur Nachfolger".equals(cbxHistoryType.getSelectedItem());

        cbxHistoryOptions1.setEnabled(!nurNachfolger);
        sprLevels1.setEnabled(nurNachfolger);

        if (nurNachfolger) {
            cbxHistoryOptions1.setSelectedItem("keine Geschwister");
            sprLevels1.setValue(1);
        }
        refresh();
    } //GEN-LAST:event_cbxHistoryTypeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void sprLevelsStateChanged(final javax.swing.event.ChangeEvent evt) { //GEN-FIRST:event_sprLevelsStateChanged
        levelTimer.cancel();
        levelTimer = new Timer();
        levelTimer.schedule(new delayedRefresh(), 1500);
    }                                                                             //GEN-LAST:event_sprLevelsStateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void ckxHoldFlurstueckActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_ckxHoldFlurstueckActionPerformed
// TODO add your handling code here:
    } //GEN-LAST:event_ckxHoldFlurstueckActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbxHistoryOptions1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbxHistoryOptions1ActionPerformed
        if (cbxHistoryOptions1.getSelectedItem().equals("Geschwister, begrenzte Tiefe")) {
            if (log.isDebugEnabled()) {
                log.debug("Geschwister, begrenzte Tiefe Tiefe ausgewählt");
            }
            levelTimer = new Timer();
            levelTimer.schedule(new delayedRefresh(), 2000);
            sprLevels1.setEnabled(true);
        } else {
            sprLevels1.setEnabled(false);
            sprLevels1.setValue(1);
            levelTimer.cancel();
            refresh();
        }
    }                                                                                      //GEN-LAST:event_cbxHistoryOptions1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void sprLevels1StateChanged(final javax.swing.event.ChangeEvent evt) { //GEN-FIRST:event_sprLevels1StateChanged
        levelTimer.cancel();
        levelTimer = new Timer();
        levelTimer.schedule(new delayedRefresh(), 1500);
    }                                                                              //GEN-LAST:event_sprLevels1StateChanged

    @Override
    public void configure(final org.jdom.Element parent) {
    }

    @Override
    public org.jdom.Element getConfiguration() {
        return null;
    }

    @Override
    public void masterConfigure(final org.jdom.Element parent) {
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class delayedRefresh extends TimerTask {

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        refresh();
                    }
                });
        }
    }
}
