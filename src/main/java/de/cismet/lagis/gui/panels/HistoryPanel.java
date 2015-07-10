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

import att.grappa.*;

import javafx.application.Platform;

import netscape.javascript.JSObject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Toolkit;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.*;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;

import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckHistorieCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.interfaces.FlurstueckChangeListener;

import de.cismet.lagis.thread.BackgroundUpdateThread;

import de.cismet.lagis.widget.AbstractWidget;

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
    private static final String DEFAULT_DOT_HEADER = "digraph G{\n";

    private static HistoryPanel instance;

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Timer levelTimer = new Timer();
    private HashMap<String, FlurstueckSchluesselCustomBean> nodeToKeyMap =
        new HashMap<String, FlurstueckSchluesselCustomBean>();
    private HashMap<String, String> pseudoKeys = new HashMap<String, String>();
    private String dotGraphRepresentation = "";
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
    private javax.swing.JComboBox cbxHistoryType;
    private javax.swing.JCheckBox ckxHoldFlurstueck;
    private javax.swing.JCheckBox ckxScaleToFit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblDatumEnt;
    private javax.swing.JLabel lblDatumEntWert;
    private javax.swing.JLabel lblDatumHist;
    private javax.swing.JLabel lblDatumHistWert;
    private javax.swing.JLabel lblVisulaization;
    private javax.swing.JPanel panHistInfo;
    private javax.swing.JPanel panInformation;
    private javax.swing.JPanel panOptions;
    private javax.swing.JPanel panPlaceholder;
    private javax.swing.JSpinner sprLevels;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new HistoryPanel object.
     */
    public HistoryPanel() {
        setIsCoreWidget(true);
        initComponents();

        final SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 100, 1);
        sprLevels.setModel(model);
        sprLevels.setEnabled(false);

        add(webViewPanel, BorderLayout.CENTER);
        try {
            new Thread() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ex) {
                        }

                        System.out.println("init FXWexxxbViewPanel");
                        webView = new FXWebViewPanel();
                        System.out.println("FXWebViewPanel inited");

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
        } catch (Throwable t) {
            log.fatal(t, t);
        }

        updateThread = new BackgroundUpdateThread<FlurstueckCustomBean>() {

                @Override
                protected void update() {
                    try {
                        try {
                            final String script = "loading();";
                            System.out.println(script);
                            if (!callBackInited) {
                                Thread.sleep(1000);

                                Platform.runLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            try {
                                                final JSObject jsobj = (JSObject)webView.getWebEngine()
                                                            .executeScript("window");
                                                jsobj.setMember("java", HistoryPanel.this);
                                                System.out.println("Callback steht zur Verfügung");
                                                callBackInited = true;
                                            } catch (Throwable t) {
                                                t.printStackTrace();
                                                System.out.println("Callback steht nicht zur Verfügung");
                                            }
                                        }
                                    });
                            }
                            Platform.runLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        System.out.println("XXX");
                                        try {
                                            webView.getWebEngine().executeScript(script);
                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                        }
                                        System.out.println("yyy");
                                    }
                                });
                        } catch (Throwable t) {
                            t.printStackTrace();
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
                        CidsBroker.HistoryLevel level;
                        int levelCount = 0;
                        if (cbxHistoryOptions.getSelectedItem().equals("Direkte Vorgänger/Nachfolger")) {
                            level = CidsBroker.HistoryLevel.DIRECT_RELATIONS;
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
                            if (log.isDebugEnabled()) {
                                log.debug("Alle Levels");
                            }
                        }
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
                        nodeToKeyMap = new HashMap<String, FlurstueckSchluesselCustomBean>();

                        final String dotGraphRepresentation = CidsBroker.getInstance()
                                    .getHistoryGraph(getCurrentObject(),
                                        level,
                                        type,
                                        levelCount, nodeToKeyMap);

                        try {
                            final String rawscript = "var graphInput='" + dotGraphRepresentation
                                        + "'; draw(graphInput);";
                            final String script = rawscript.replaceAll("\n", "");
                            System.out.println(script);
                            Platform.runLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        System.out.println("XXX");
                                        try {
                                            webView.getWebEngine().executeScript(script);
                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                        }
                                        System.out.println("yyy");
                                    }
                                });
                        } catch (Throwable t) {
                            t.printStackTrace();
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
        resetDotGraph();
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

    /**
     * DOCUMENT ME!
     */
    private void resetDotGraph() {
        dotGraphRepresentation = DEFAULT_DOT_HEADER;
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
            resetDotGraph();
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
        panHistInfo = new javax.swing.JPanel();
        panOptions = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        ckxScaleToFit = new javax.swing.JCheckBox();
        ckxHoldFlurstueck = new javax.swing.JCheckBox();
        lblVisulaization = new javax.swing.JLabel();
        cbxHistoryType = new javax.swing.JComboBox();
        panPlaceholder = new javax.swing.JPanel();
        cbxHistoryOptions = new javax.swing.JComboBox();
        sprLevels = new javax.swing.JSpinner();
        panInformation = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblDatumEnt = new javax.swing.JLabel();
        lblDatumEntWert = new javax.swing.JLabel();
        lblDatumHist = new javax.swing.JLabel();
        lblDatumHistWert = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        panOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setText("Optionen:");

        ckxScaleToFit.setSelected(true);
        ckxScaleToFit.setText("an Bildschimgröße anpassen");
        ckxScaleToFit.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    ckxScaleToFitActionPerformed(evt);
                }
            });

        ckxHoldFlurstueck.setText("Historie halten");
        ckxHoldFlurstueck.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    ckxHoldFlurstueckActionPerformed(evt);
                }
            });

        lblVisulaization.setText("Darstellung:");

        cbxHistoryType.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "Alle Flurstücke", "Nur Nachfolger", "Nur Vorgänger" }));
        cbxHistoryType.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbxHistoryTypeActionPerformed(evt);
                }
            });

        cbxHistoryOptions.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "Direkte Vorgänger/Nachfolger", "Vollständig", "Begrenzte Tiefe" }));
        cbxHistoryOptions.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbxHistoryOptionsActionPerformed(evt);
                }
            });

        sprLevels.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    sprLevelsStateChanged(evt);
                }
            });

        final org.jdesktop.layout.GroupLayout panPlaceholderLayout = new org.jdesktop.layout.GroupLayout(
                panPlaceholder);
        panPlaceholder.setLayout(panPlaceholderLayout);
        panPlaceholderLayout.setHorizontalGroup(
            panPlaceholderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panPlaceholderLayout.createSequentialGroup().addContainerGap().add(
                    cbxHistoryOptions,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(4, 4, 4).add(sprLevels).addContainerGap()));
        panPlaceholderLayout.setVerticalGroup(
            panPlaceholderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panPlaceholderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                    sprLevels,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                    cbxHistoryOptions,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));

        final org.jdesktop.layout.GroupLayout panOptionsLayout = new org.jdesktop.layout.GroupLayout(panOptions);
        panOptions.setLayout(panOptionsLayout);
        panOptionsLayout.setHorizontalGroup(
            panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panOptionsLayout.createSequentialGroup().add(
                    panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jLabel1).add(
                        panOptionsLayout.createSequentialGroup().addContainerGap().add(
                            ckxScaleToFit,
                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                            218,
                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).add(
                        panOptionsLayout.createSequentialGroup().addContainerGap().add(ckxHoldFlurstueck)))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        lblVisulaization).add(
                        panOptionsLayout.createSequentialGroup().add(12, 12, 12).add(
                            panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                                panOptionsLayout.createSequentialGroup().add(12, 12, 12).add(
                                    cbxHistoryType,
                                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                    227,
                                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap()).add(
                                panPlaceholder,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE))))));
        panOptionsLayout.setVerticalGroup(
            panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panOptionsLayout.createSequentialGroup().add(
                    panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                        lblVisulaization).add(jLabel1)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        panPlaceholder,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(ckxScaleToFit)).add(
                    panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        panOptionsLayout.createSequentialGroup().addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.UNRELATED).add(ckxHoldFlurstueck)).add(
                        panOptionsLayout.createSequentialGroup().add(5, 5, 5).add(
                            cbxHistoryType,
                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))).addContainerGap(13, Short.MAX_VALUE)));

        panInformation.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel3.setText("Informationen");

        lblDatumEnt.setText("Datum Entstehung:");

        lblDatumEntWert.setText("Keine Angabe");

        lblDatumHist.setText("Datum Historisch seit:");

        lblDatumHistWert.setText("Keine Angabe");

        final org.jdesktop.layout.GroupLayout panInformationLayout = new org.jdesktop.layout.GroupLayout(
                panInformation);
        panInformation.setLayout(panInformationLayout);
        panInformationLayout.setHorizontalGroup(
            panInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                panInformationLayout.createSequentialGroup().addContainerGap(12, Short.MAX_VALUE).add(
                    panInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false).add(
                        org.jdesktop.layout.GroupLayout.LEADING,
                        panInformationLayout.createSequentialGroup().add(lblDatumHist).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE).add(lblDatumHistWert)).add(
                        org.jdesktop.layout.GroupLayout.LEADING,
                        panInformationLayout.createSequentialGroup().add(lblDatumEnt).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE).add(lblDatumEntWert))).addContainerGap()).add(
                panInformationLayout.createSequentialGroup().add(jLabel3).addContainerGap(171, Short.MAX_VALUE)));
        panInformationLayout.setVerticalGroup(
            panInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panInformationLayout.createSequentialGroup().addContainerGap().add(jLabel3).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                        lblDatumEnt).add(lblDatumEntWert)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                        lblDatumHist).add(lblDatumHistWert)).addContainerGap(23, Short.MAX_VALUE)));

        final org.jdesktop.layout.GroupLayout panHistInfoLayout = new org.jdesktop.layout.GroupLayout(panHistInfo);
        panHistInfo.setLayout(panHistInfoLayout);
        panHistInfoLayout.setHorizontalGroup(
            panHistInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panHistInfoLayout.createSequentialGroup().addContainerGap().add(
                    panOptions,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panInformation,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE).add(174, 174, 174)));
        panHistInfoLayout.setVerticalGroup(
            panHistInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panHistInfoLayout.createSequentialGroup().addContainerGap().add(
                    panHistInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        panInformation,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).add(
                        panOptions,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addContainerGap()));

        add(panHistInfo, java.awt.BorderLayout.SOUTH);
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
                    } catch (Throwable t) {
                        t.printStackTrace();
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
        if (cbxHistoryOptions.getSelectedItem().equals("Begrenzte Tiefe")) {
            if (log.isDebugEnabled()) {
                log.debug("Begrentzte Tiefe ausgewählt");
            }
            levelTimer = new Timer();
            levelTimer.schedule(new delayedRefresh(), 2000);
            sprLevels.setEnabled(true);
        } else {
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
        refresh();
    }                                                                                  //GEN-LAST:event_cbxHistoryTypeActionPerformed

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
