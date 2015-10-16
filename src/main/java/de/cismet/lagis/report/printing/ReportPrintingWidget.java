/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * PrintingWidget.java
 *
 * Created on 10. Juli 2006, 17:55
 */
package de.cismet.lagis.report.printing;

import javafx.application.Platform;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.concurrent.Worker;

import javafx.embed.swing.SwingFXUtils;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;

import net.sf.jasperreports.engine.JRDataSource;

import netscape.javascript.JSObject;

import org.apache.commons.io.IOUtils;

import org.openide.util.Exceptions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.RenderedImage;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingWorker;

import de.cismet.cismap.commons.gui.printing.JasperReportDownload;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.gui.checkbox.IconCheckBox;

import de.cismet.lagis.report.datasource.ADataSource;
import de.cismet.lagis.report.datasource.BaumDateiDataSource;
import de.cismet.lagis.report.datasource.EmptyDataSource;
import de.cismet.lagis.report.datasource.MiPaDataSource;
import de.cismet.lagis.report.datasource.NutzungenDataSource;
import de.cismet.lagis.report.datasource.ReBeDataSource;
import de.cismet.lagis.report.datasource.VorgaengeDataSource;

import de.cismet.lagis.widget.AbstractWidget;
import de.cismet.lagis.widget.RessortFactory;

import de.cismet.tools.gui.FXWebViewPanel;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public final class ReportPrintingWidget extends javax.swing.JDialog {

    //~ Static fields/initializers ---------------------------------------------

    private static final double DPI_FACTOR = 600d / 72d;
    private static final int HISTORY_IMAGE_WIDTH = 545;
    private static final int HISTORY_IMAGE_HEIGHT = 211;

    private static final String PERM_KEY_BAUM = "Baumdatei";              // NOI18N
    private static final String PERM_KEY_MIPA = "Vermietung/Verpachtung"; // NOI18N

    private static final String PARAM_NUTZUNGEN = "param_nutzungen"; // NOI18N
    private static final String PARAM_REBE = "param_rebe";           // NOI18N
    private static final String PARAM_VORGAENGE = "param_vorgaenge"; // NOI18N
    private static final String PARAM_HISTORY = "param_history";     // NOI18N
    private static final String PARAM_MIPA = "param_mipa";           // NOI18N
    private static final String PARAM_BAUMDATEI = "param_baumdatei"; // NOI18N
    private static final String PARAM_NOTIZEN = "param_notizen";     // NOI18N

    private static final String REPORT_MASTER = "/de/cismet/lagis/reports/FlurstueckDetailsReport.jasper"; // NOI18N
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportPrintingWidget.class);

    private static final String TEMPLATE;

    static {
        String tmp = null;
        try {
            tmp = IOUtils.toString(ReportPrintingWidget.class.getResourceAsStream("dagreReportingTemplate.html"));
        } catch (IOException ex) {
            LOG.error(ex, ex);
        }
        TEMPLATE = tmp;
    }

    //~ Instance fields --------------------------------------------------------

    private PDFCreatingWaitDialog pdfWait;
    private FXWebViewPanel myWeb = null;
    private final String graphContent;

    private final Component parentComponent;

    private final HashMap<String, String> paramMap;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.lagis.gui.checkbox.IconCheckBox baumdateiCheckBox;
    private javax.swing.JButton cmdCancel;
    private javax.swing.JButton cmdOk;
    private javax.swing.Box.Filler filler1;
    private de.cismet.lagis.gui.checkbox.IconCheckBox historieCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl2;
    private de.cismet.lagis.gui.checkbox.IconCheckBox mipaCheckBox;
    private de.cismet.lagis.gui.checkbox.IconCheckBox notizenCheckBox;
    private javax.swing.JTextPane notizenTextArea;
    private de.cismet.lagis.gui.checkbox.IconCheckBox nutzungenCheckBox;
    private javax.swing.JPanel panDesc;
    private javax.swing.JPanel panLoadAndInscribe;
    private de.cismet.lagis.gui.checkbox.IconCheckBox rebeCheckBox;
    private javax.swing.JScrollPane scpLoadingStatus;
    private javax.swing.JTextField txt1;
    private javax.swing.JTextField txt2;
    private de.cismet.lagis.gui.checkbox.IconCheckBox vorgaengeCheckBox;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form PrintingWidget.
     *
     * @param  frame  component mappingComponent DOCUMENT ME!
     * @param  modal  DOCUMENT ME!
     */
    public ReportPrintingWidget(final Frame frame, final boolean modal) {
        super(frame, modal);

        parentComponent = frame;

        pdfWait = new PDFCreatingWaitDialog(StaticSwingTools.getParentFrame(this), true);
        initComponents();
        panDesc.setBackground(new Color(216, 228, 248));

        getRootPane().setDefaultButton(cmdOk);
        this.notizenTextArea.requestFocus();

        this.initCheckBoxes();

        super.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosed(final WindowEvent e) {
                    close();
                }
            });

        this.paramMap = new HashMap<String, String>(7);

        this.handleParamMap(PARAM_HISTORY, true);
        this.handleParamMap(PARAM_NOTIZEN, true);

        this.checkDetailAvailability();
        initHistory();

        graphContent = getGraphContent();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String getGraphContent() {
        String graphString;
        try {
            graphString = CidsBroker.getInstance()
                        .getHistoryGraph(LagisBroker.getInstance().getCurrentFlurstueck(),
                                CidsBroker.HistoryLevel.DIRECT_RELATIONS,
                                CidsBroker.HistoryType.BOTH,
                                0,
                                null);
        } catch (final Exception ex) {
            graphString = "digraph G{\"Fehler beim Ermitteln der Historie\"}";
            LOG.error("Error when craeting Historygraph", ex);
        }
        return TEMPLATE.replaceAll("__graphString__", graphString.replaceAll("\n", ""));
    }

    /**
     * DOCUMENT ME!
     */
    private void initHistory() {
        try {
            new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        myWeb = new FXWebViewPanel();
                        LOG.info("FXWebViewPanel inited");
                        return null;
                    }

                    @Override
                    protected void done() {
                        final Dimension dim = new Dimension((int)(HISTORY_IMAGE_WIDTH * DPI_FACTOR),
                                (int)(HISTORY_IMAGE_HEIGHT * DPI_FACTOR));
                        myWeb.setVisible(true);
                        myWeb.setSize(dim);
                        Platform.runLater(new Runnable() {

                                @Override
                                public void run() {
                                    myWeb.getWebEngine()
                                            .getLoadWorker()
                                            .stateProperty()
                                            .addListener(new ChangeListener<javafx.concurrent.Worker.State>() {

                                                    @Override
                                                    public void changed(
                                                            final ObservableValue<? extends Worker.State> observable,
                                                            final Worker.State oldValue,
                                                            final Worker.State newValue) {
                                                        if (newValue == Worker.State.SUCCEEDED) {
                                                            final JSObject jsobj = (JSObject)myWeb.getWebEngine()
                                                                .executeScript("window");
                                                            jsobj.setMember("java", ReportPrintingWidget.this);
                                                        }
                                                    }
                                                });
                                    myWeb.loadContent(graphContent);
                                }
                            });
                    }
                }.execute();
        } catch (final Exception exception) {
            LOG.error("Error while filling template", exception);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  zoomlevel  DOCUMENT ME!
     */
    public void pageRendered(final String zoomlevel) {
        final double zl = Double.parseDouble(zoomlevel);
        Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    if (zl < 1) {
                        myWeb.setSize(
                            new Dimension(
                                (int)(HISTORY_IMAGE_WIDTH / DPI_FACTOR / zl),
                                (int)(HISTORY_IMAGE_HEIGHT / DPI_FACTOR / zl)));
                        myWeb.loadContent(graphContent);
                    } else {
                        final SnapshotParameters params = new SnapshotParameters();
                        final WritableImage snapshot = myWeb.getWebView().snapshot(params, null);
                        final RenderedImage renderedImage = SwingFXUtils.fromFXImage(snapshot, null);

                        LagisBroker.getInstance().setHistoryImage(renderedImage);
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    cmdOk.setEnabled(true);
                                }
                            });
                    }
                }
            });
    }

    /**
     * DOCUMENT ME!
     */
    private void initCheckBoxes() {
        this.baumdateiCheckBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent ae) {
                    baumdateiCheckBoxActionPerformed(ae);
                }
            });

        this.historieCheckBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent ae) {
                    historieCheckBoxActionPerformed(ae);
                }
            });

        this.mipaCheckBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent ae) {
                    mipaCheckBoxActionPerformed(ae);
                }
            });

        this.notizenCheckBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent ae) {
                    notizenCheckBoxActionPerformed(ae);
                }
            });

        this.nutzungenCheckBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent ae) {
                    nutzungenCheckBoxActionPerformed(ae);
                }
            });

        this.rebeCheckBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent ae) {
                    rebeCheckBoxActionPerformed(ae);
                }
            });

        this.vorgaengeCheckBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent ae) {
                    vorgaengeCheckBoxActionPerformed(ae);
                }
            });
    }

    /**
     * DOCUMENT ME!
     */
    private void checkDetailAvailability() {
        final RessortFactory ressortFactory = RessortFactory.getInstance();
        final HashMap<String, AbstractWidget> ressorts = ressortFactory.getRessorts();

        ADataSource ds = new BaumDateiDataSource();
        this.handleDetail(PARAM_BAUMDATEI, this.baumdateiCheckBox, ds.hasData());
        this.handlePermission(this.baumdateiCheckBox, ressorts.containsKey(PERM_KEY_BAUM));

        ds = new MiPaDataSource();
        this.handleDetail(PARAM_MIPA, this.mipaCheckBox, ds.hasData());
        this.handlePermission(this.mipaCheckBox, ressorts.containsKey(PERM_KEY_MIPA));

        ds = new NutzungenDataSource();
        this.handleDetail(PARAM_NUTZUNGEN, this.nutzungenCheckBox, ds.hasData());

        ds = new ReBeDataSource();
        this.handleDetail(PARAM_REBE, this.rebeCheckBox, ds.hasData());

        ds = new VorgaengeDataSource();
        this.handleDetail(PARAM_VORGAENGE, this.vorgaengeCheckBox, ds.hasData());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  checkBox       DOCUMENT ME!
     * @param  hasPermission  DOCUMENT ME!
     */
    private void handlePermission(final IconCheckBox checkBox, final boolean hasPermission) {
        final boolean released = checkBox.isSelected() && hasPermission;

        checkBox.setEnabled(released);
        checkBox.setSelected(released);

        if (!hasPermission) {
            checkBox.setToolTipText(
                java.util.ResourceBundle.getBundle("de/cismet/lagis/report/printing/Bundle").getString(
                    "ReportPrintingWidget.handleDetail(String,JCheckBox,enabled).checkBox.toolTipText.nopermission")); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  param     DOCUMENT ME!
     * @param  checkBox  DOCUMENT ME!
     * @param  hasData   DOCUMENT ME!
     */
    private void handleDetail(final String param, final IconCheckBox checkBox, final boolean hasData) {
        this.handleParamMap(param, hasData);
        checkBox.setEnabled(hasData);
        checkBox.setSelected(hasData);

        if (!hasData) {
            checkBox.setToolTipText(
                java.util.ResourceBundle.getBundle("de/cismet/lagis/report/printing/Bundle").getString(
                    "ReportPrintingWidget.handleDetail(String,JCheckBox,enabled).checkBox.toolTipText.nodata")); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  param       DOCUMENT ME!
     * @param  isSelected  DOCUMENT ME!
     */
    private void handleParamMap(final String param, final boolean isSelected) {
        if (isSelected) {
            this.paramMap.put(param, param);
        } else {
            this.paramMap.remove(param);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void close() {
        super.setVisible(false);
        super.dispose();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   modal      DOCUMENT ME!
     * @param   component  mappingComponent DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ReportPrintingWidget cloneWithNewParent(final boolean modal, final Frame component) {
        final ReportPrintingWidget newWidget = new ReportPrintingWidget(component, modal);
        return newWidget;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lbl1 = new javax.swing.JLabel();
        txt1 = new javax.swing.JTextField();
        lbl2 = new javax.swing.JLabel();
        txt2 = new javax.swing.JTextField();
        panDesc = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        panLoadAndInscribe = new javax.swing.JPanel();
        scpLoadingStatus = new javax.swing.JScrollPane();
        notizenTextArea = new javax.swing.JTextPane();
        nutzungenCheckBox = new de.cismet.lagis.gui.checkbox.IconCheckBox();
        rebeCheckBox = new de.cismet.lagis.gui.checkbox.IconCheckBox();
        vorgaengeCheckBox = new de.cismet.lagis.gui.checkbox.IconCheckBox();
        mipaCheckBox = new de.cismet.lagis.gui.checkbox.IconCheckBox();
        baumdateiCheckBox = new de.cismet.lagis.gui.checkbox.IconCheckBox();
        historieCheckBox = new de.cismet.lagis.gui.checkbox.IconCheckBox();
        notizenCheckBox = new de.cismet.lagis.gui.checkbox.IconCheckBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        cmdCancel = new javax.swing.JButton();
        cmdOk = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();

        lbl1.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.lbl1.text")); // NOI18N

        txt1.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.txt1.text")); // NOI18N

        lbl2.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.lbl2.text")); // NOI18N

        txt2.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.txt2.text")); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(ReportPrintingWidget.class, "ReportPrintingWidget.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(720, 0));
        setResizable(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {

                @Override
                public void componentShown(final java.awt.event.ComponentEvent evt) {
                    formComponentShown(evt);
                }
            });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panDesc.setBackground(java.awt.SystemColor.inactiveCaptionText);
        panDesc.setMaximumSize(new java.awt.Dimension(32767, 240));
        panDesc.setPreferredSize(new java.awt.Dimension(160, 240));
        panDesc.setLayout(new java.awt.BorderLayout());

        jPanel2.setOpaque(false);

        jLabel5.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cismap/commons/gui/res/frameprint.png"))); // NOI18N

        final org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel2Layout.createSequentialGroup().add(23, 23, 23).add(jLabel5).addContainerGap(
                    39,
                    Short.MAX_VALUE)));
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                jPanel2Layout.createSequentialGroup().addContainerGap(89, Short.MAX_VALUE).add(jLabel5).add(
                    23,
                    23,
                    23)));

        panDesc.add(jPanel2, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        getContentPane().add(panDesc, gridBagConstraints);

        panLoadAndInscribe.setMaximumSize(new java.awt.Dimension(32767, 240));
        panLoadAndInscribe.setPreferredSize(new java.awt.Dimension(450, 240));
        panLoadAndInscribe.setLayout(new java.awt.GridBagLayout());

        scpLoadingStatus.setMinimumSize(new java.awt.Dimension(26, 29));
        scpLoadingStatus.setPreferredSize(new java.awt.Dimension(8, 29));

        notizenTextArea.setMinimumSize(new java.awt.Dimension(0, 50));
        notizenTextArea.setPreferredSize(new java.awt.Dimension(6, 50));
        scpLoadingStatus.setViewportView(notizenTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
        gridBagConstraints.weighty = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(7, 5, 0, 5);
        panLoadAndInscribe.add(scpLoadingStatus, gridBagConstraints);

        nutzungenCheckBox.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/sum.png"))); // NOI18N
        nutzungenCheckBox.setSelected(true);
        nutzungenCheckBox.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.nutzungenCheckBox.text"));                               // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        panLoadAndInscribe.add(nutzungenCheckBox, gridBagConstraints);

        rebeCheckBox.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/findgreen.png"))); // NOI18N
        rebeCheckBox.setSelected(true);
        rebeCheckBox.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.rebeCheckBox.text"));                                          // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        panLoadAndInscribe.add(rebeCheckBox, gridBagConstraints);

        vorgaengeCheckBox.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/documents.png"))); // NOI18N
        vorgaengeCheckBox.setSelected(true);
        vorgaengeCheckBox.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.vorgaengeCheckBox.text"));                                     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        panLoadAndInscribe.add(vorgaengeCheckBox, gridBagConstraints);

        mipaCheckBox.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/ressort.png"))); // NOI18N
        mipaCheckBox.setSelected(true);
        mipaCheckBox.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.mipaCheckBox.text"));                                        // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.4;
        panLoadAndInscribe.add(mipaCheckBox, gridBagConstraints);

        baumdateiCheckBox.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/ressort.png"))); // NOI18N
        baumdateiCheckBox.setSelected(true);
        baumdateiCheckBox.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.baumdateiCheckBox.text"));                                   // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.4;
        panLoadAndInscribe.add(baumdateiCheckBox, gridBagConstraints);

        historieCheckBox.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/ressort.png"))); // NOI18N
        historieCheckBox.setSelected(true);
        historieCheckBox.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.historieCheckBox.text"));                                    // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.4;
        panLoadAndInscribe.add(historieCheckBox, gridBagConstraints);

        notizenCheckBox.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/note_edit.png"))); // NOI18N
        notizenCheckBox.setSelected(true);
        notizenCheckBox.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget1.notizenCheckBox.text"));                                      // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(22, 0, 0, 0);
        panLoadAndInscribe.add(notizenCheckBox, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weighty = 0.3;
        panLoadAndInscribe.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(panLoadAndInscribe, gridBagConstraints);

        jSeparator3.setMinimumSize(new java.awt.Dimension(510, 6));
        jSeparator3.setPreferredSize(new java.awt.Dimension(100, 6));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(jSeparator3, gridBagConstraints);

        jSeparator4.setMinimumSize(new java.awt.Dimension(165, 6));
        jSeparator4.setPreferredSize(new java.awt.Dimension(174, 6));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jSeparator4, gridBagConstraints);

        jPanel1.setMinimumSize(new java.awt.Dimension(400, 39));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        cmdCancel.setMnemonic('A');
        cmdCancel.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.cmdCancel.text")); // NOI18N
        cmdCancel.setMaximumSize(new java.awt.Dimension(100, 29));
        cmdCancel.setMinimumSize(new java.awt.Dimension(100, 29));
        cmdCancel.setPreferredSize(new java.awt.Dimension(100, 29));
        cmdCancel.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdCancelActionPerformed(evt);
                }
            });
        jPanel1.add(cmdCancel);

        cmdOk.setMnemonic('O');
        cmdOk.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.cmdOk.text")); // NOI18N
        cmdOk.setEnabled(false);
        cmdOk.setMaximumSize(new java.awt.Dimension(100, 29));
        cmdOk.setMinimumSize(new java.awt.Dimension(100, 29));
        cmdOk.setPreferredSize(new java.awt.Dimension(100, 29));
        cmdOk.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdOkActionPerformed(evt);
                }
            });
        jPanel1.add(cmdOk);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_TRAILING;
        gridBagConstraints.insets = new java.awt.Insets(0, 9, 0, 9);
        getContentPane().add(jPanel1, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.jLabel6.text"));       // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        getContentPane().add(jLabel6, gridBagConstraints);

        jSeparator1.setMinimumSize(new java.awt.Dimension(200, 6));
        jSeparator1.setPreferredSize(new java.awt.Dimension(200, 6));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        getContentPane().add(jSeparator1, gridBagConstraints);

        jSeparator2.setBackground(new java.awt.Color(216, 228, 248));
        jSeparator2.setMaximumSize(new java.awt.Dimension(160, 32767));
        jSeparator2.setMinimumSize(new java.awt.Dimension(160, 6));
        jSeparator2.setOpaque(true);
        jSeparator2.setPreferredSize(new java.awt.Dimension(160, 6));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        getContentPane().add(jSeparator2, gridBagConstraints);

        jLabel1.setBackground(new java.awt.Color(216, 228, 248));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.jLabel1.text"));       // NOI18N
        jLabel1.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weightx = 0.3;
        getContentPane().add(jLabel1, gridBagConstraints);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void formComponentShown(final java.awt.event.ComponentEvent evt) { //GEN-FIRST:event_formComponentShown
    }                                                                          //GEN-LAST:event_formComponentShown

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdCancelActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdCancelActionPerformed
        close();
    }                                                                             //GEN-LAST:event_cmdCancelActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdOkActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdOkActionPerformed
        // RenderedImage in den Broker setzen
        // Dann den JAsper kram machen und im Scriptlet den Broker nach dem IMage fragen
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final JasperReportDownload.JasperReportDataSourceGenerator dataSourceGenerator =
                        new JasperReportDownload.JasperReportDataSourceGenerator() {

                            @Override
                            public JRDataSource generateDataSource() {
                                return new EmptyDataSource(1);
                            }
                        };

                    final JasperReportDownload.JasperReportParametersGenerator parametersGenerator =
                        new JasperReportDownload.JasperReportParametersGenerator() {

                            @Override
                            public Map generateParamters() {
                                if (notizenCheckBox.isSelected()) {
                                    paramMap.put(PARAM_NOTIZEN, notizenTextArea.getText());
                                }
                                return paramMap;
                            }
                        };

                    if (DownloadManagerDialog.showAskingForUserTitle((Frame)parentComponent)) {
                        final String jobname = DownloadManagerDialog.getJobname();
                        DownloadManager.instance()
                                .add(
                                    new JasperReportDownload(
                                        REPORT_MASTER,
                                        parametersGenerator,
                                        dataSourceGenerator,
                                        jobname,
                                        "Lagis-Druck",
                                        "lagis_flurstueck_details"));
                    }

                    setVisible(false);
                }
            });
    } //GEN-LAST:event_cmdOkActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void vorgaengeCheckBoxActionPerformed(final java.awt.event.ActionEvent evt) {
        this.handleParamMap(PARAM_VORGAENGE, this.vorgaengeCheckBox.isSelected());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void nutzungenCheckBoxActionPerformed(final java.awt.event.ActionEvent evt) {
        this.handleParamMap(PARAM_NUTZUNGEN, this.nutzungenCheckBox.isSelected());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void rebeCheckBoxActionPerformed(final java.awt.event.ActionEvent evt) {
        this.handleParamMap(PARAM_REBE, this.rebeCheckBox.isSelected());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void notizenCheckBoxActionPerformed(final java.awt.event.ActionEvent evt) {
        final boolean isSelected = this.notizenCheckBox.isSelected();

        this.notizenTextArea.setEnabled(isSelected);
        if (isSelected) {
            this.notizenTextArea.setBackground(Color.WHITE);
        } else {
            this.notizenTextArea.setBackground(this.getBackground());
        }

        this.handleParamMap(PARAM_NOTIZEN, isSelected);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mipaCheckBoxActionPerformed(final java.awt.event.ActionEvent evt) {
        this.handleParamMap(PARAM_MIPA, this.mipaCheckBox.isSelected());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void baumdateiCheckBoxActionPerformed(final java.awt.event.ActionEvent evt) {
        this.handleParamMap(PARAM_BAUMDATEI, this.baumdateiCheckBox.isSelected());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void historieCheckBoxActionPerformed(final java.awt.event.ActionEvent evt) {
        this.handleParamMap(PARAM_HISTORY, this.historieCheckBox.isSelected());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  the command line arguments
     */
    public static void main(final String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final ReportPrintingWidget rpw = new ReportPrintingWidget(new javax.swing.JFrame(), true);
                    rpw.pack();
                    rpw.setVisible(true);
                }
            });
    }
}
