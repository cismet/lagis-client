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

import Sirius.navigator.ui.widget.CheckBoxComboBox;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.InputStream;

import java.util.HashMap;
import java.util.logging.Level;

import javax.swing.JCheckBox;

import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.lagis.report.datasource.ADataSource;
import de.cismet.lagis.report.datasource.BaumDateiDataSource;
import de.cismet.lagis.report.datasource.EmptyDataSource;
import de.cismet.lagis.report.datasource.MiPaDataSource;
import de.cismet.lagis.report.datasource.NutzungenDataSource;
import de.cismet.lagis.report.datasource.ReBeDataSource;
import de.cismet.lagis.report.datasource.VorgaengeDataSource;

import de.cismet.lagis.widget.AbstractWidget;
import de.cismet.lagis.widget.RessortFactory;

import de.cismet.tools.CismetThreadPool;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public final class ReportPrintingWidget extends javax.swing.JDialog {

    //~ Static fields/initializers ---------------------------------------------

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

    private static final String TARGET_FILE;
    private static final String TARGET_FILE_URL;

    static {
        final String home = System.getProperty("user.home");    // NOI18N
        final String fs = System.getProperty("file.separator"); // NOI18N
        TARGET_FILE = home + fs + "lagis.pdf";                  // TODO//NOI18N
        String file = TARGET_FILE.replaceAll("\\\\", "/");      // NOI18N
        file = file.replaceAll(" ", "%20");                     // NOI18N
        TARGET_FILE_URL = "file:///" + file;                    // NOI18N
    }

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ReportPrintingWidget.class);

    //~ Instance fields --------------------------------------------------------

    PDFCreatingWaitDialog pdfWait;

    private final Component parentComponent;

    private final HashMap<String, String> paramMap;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox baumdateiCheckBox;
    private javax.swing.JButton cmdCancel;
    private javax.swing.JButton cmdOk;
    private javax.swing.JCheckBox historieCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl2;
    private javax.swing.JCheckBox mipaCheckBox;
    private javax.swing.JCheckBox notizenCheckBox;
    private javax.swing.JTextPane notizenTextArea;
    private javax.swing.JCheckBox nutzungenCheckBox;
    private javax.swing.JPanel panDesc;
    private javax.swing.JPanel panLoadAndInscribe;
    private javax.swing.JCheckBox rebeCheckBox;
    private javax.swing.JScrollPane scpLoadingStatus;
    private javax.swing.JTextField txt1;
    private javax.swing.JTextField txt2;
    private javax.swing.JCheckBox vorgaengeCheckBox;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form PrintingWidget.
     *
     * @param  component  mappingComponent DOCUMENT ME!
     * @param  modal      DOCUMENT ME!
     */
    public ReportPrintingWidget(final Component component, final boolean modal) {
        super(StaticSwingTools.getParentFrame(component), modal);

        parentComponent = component;

        pdfWait = new PDFCreatingWaitDialog(StaticSwingTools.getParentFrame(this), true);
        initComponents();
        panDesc.setBackground(new Color(216, 228, 248));

        this.setLocationRelativeTo(parentComponent);

        getRootPane().setDefaultButton(cmdOk);
        this.notizenTextArea.requestFocus();

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
    }

    //~ Methods ----------------------------------------------------------------

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
    private void handlePermission(final JCheckBox checkBox, final boolean hasPermission) {
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
    private void handleDetail(final String param, final JCheckBox checkBox, final boolean hasData) {
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
    public ReportPrintingWidget cloneWithNewParent(final boolean modal, final Component component) {
        final ReportPrintingWidget newWidget = new ReportPrintingWidget(component, modal);
        return newWidget;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lbl1 = new javax.swing.JLabel();
        txt1 = new javax.swing.JTextField();
        lbl2 = new javax.swing.JLabel();
        txt2 = new javax.swing.JTextField();
        panDesc = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        cmdOk = new javax.swing.JButton();
        cmdCancel = new javax.swing.JButton();
        panLoadAndInscribe = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        nutzungenCheckBox = new javax.swing.JCheckBox();
        rebeCheckBox = new javax.swing.JCheckBox();
        vorgaengeCheckBox = new javax.swing.JCheckBox();
        mipaCheckBox = new javax.swing.JCheckBox();
        baumdateiCheckBox = new javax.swing.JCheckBox();
        historieCheckBox = new javax.swing.JCheckBox();
        notizenCheckBox = new javax.swing.JCheckBox();
        jSeparator4 = new javax.swing.JSeparator();
        scpLoadingStatus = new javax.swing.JScrollPane();
        notizenTextArea = new javax.swing.JTextPane();

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
        setResizable(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {

                @Override
                public void componentShown(final java.awt.event.ComponentEvent evt) {
                    formComponentShown(evt);
                }
            });

        panDesc.setBackground(java.awt.SystemColor.inactiveCaptionText);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.jLabel1.text")); // NOI18N

        jLabel5.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cismap/commons/gui/res/frameprint.png"))); // NOI18N

        final org.jdesktop.layout.GroupLayout panDescLayout = new org.jdesktop.layout.GroupLayout(panDesc);
        panDesc.setLayout(panDescLayout);
        panDescLayout.setHorizontalGroup(
            panDescLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panDescLayout.createSequentialGroup().addContainerGap().add(
                    panDescLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        panDescLayout.createSequentialGroup().add(
                            jSeparator2,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            205,
                            Short.MAX_VALUE).addContainerGap()).add(
                        panDescLayout.createSequentialGroup().add(jLabel1).add(175, 175, 175)))).add(
                jSeparator3,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                229,
                Short.MAX_VALUE).add(
                panDescLayout.createSequentialGroup().addContainerGap().add(jLabel5).addContainerGap(
                    89,
                    Short.MAX_VALUE)));
        panDescLayout.setVerticalGroup(
            panDescLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panDescLayout.createSequentialGroup().addContainerGap().add(jLabel1).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    jSeparator2,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    2,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED,
                    109,
                    Short.MAX_VALUE).add(jLabel5).add(18, 18, 18).add(
                    jSeparator3,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));

        cmdOk.setMnemonic('O');
        cmdOk.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.cmdOk.text")); // NOI18N
        cmdOk.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdOkActionPerformed(evt);
                }
            });

        cmdCancel.setMnemonic('A');
        cmdCancel.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.cmdCancel.text")); // NOI18N
        cmdCancel.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdCancelActionPerformed(evt);
                }
            });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel6.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.jLabel6.text")); // NOI18N

        nutzungenCheckBox.setSelected(true);
        nutzungenCheckBox.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.nutzungenCheckBox.text")); // NOI18N
        nutzungenCheckBox.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    nutzungenCheckBoxActionPerformed(evt);
                }
            });

        rebeCheckBox.setSelected(true);
        rebeCheckBox.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.rebeCheckBox.text")); // NOI18N
        rebeCheckBox.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    rebeCheckBoxActionPerformed(evt);
                }
            });

        vorgaengeCheckBox.setSelected(true);
        vorgaengeCheckBox.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.vorgaengeCheckBox.text")); // NOI18N
        vorgaengeCheckBox.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    vorgaengeCheckBoxActionPerformed(evt);
                }
            });

        mipaCheckBox.setSelected(true);
        mipaCheckBox.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.mipaCheckBox.text")); // NOI18N
        mipaCheckBox.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mipaCheckBoxActionPerformed(evt);
                }
            });

        baumdateiCheckBox.setSelected(true);
        baumdateiCheckBox.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.baumdateiCheckBox.text")); // NOI18N
        baumdateiCheckBox.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    baumdateiCheckBoxActionPerformed(evt);
                }
            });

        historieCheckBox.setSelected(true);
        historieCheckBox.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.historieCheckBox.text")); // NOI18N
        historieCheckBox.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    historieCheckBoxActionPerformed(evt);
                }
            });

        notizenCheckBox.setSelected(true);
        notizenCheckBox.setText(org.openide.util.NbBundle.getMessage(
                ReportPrintingWidget.class,
                "ReportPrintingWidget.notizenCheckBox.text")); // NOI18N
        notizenCheckBox.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    notizenCheckBoxActionPerformed(evt);
                }
            });

        notizenTextArea.setBackground(java.awt.SystemColor.text);
        scpLoadingStatus.setViewportView(notizenTextArea);

        final org.jdesktop.layout.GroupLayout panLoadAndInscribeLayout = new org.jdesktop.layout.GroupLayout(
                panLoadAndInscribe);
        panLoadAndInscribe.setLayout(panLoadAndInscribeLayout);
        panLoadAndInscribeLayout.setHorizontalGroup(
            panLoadAndInscribeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                panLoadAndInscribeLayout.createSequentialGroup().add(
                    panLoadAndInscribeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING).add(
                        jSeparator4,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        337,
                        Short.MAX_VALUE).add(
                        org.jdesktop.layout.GroupLayout.LEADING,
                        panLoadAndInscribeLayout.createSequentialGroup().add(24, 24, 24).add(
                            panLoadAndInscribeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                                panLoadAndInscribeLayout.createSequentialGroup().add(
                                    panLoadAndInscribeLayout.createParallelGroup(
                                        org.jdesktop.layout.GroupLayout.LEADING).add(rebeCheckBox).add(
                                        vorgaengeCheckBox).add(nutzungenCheckBox)).add(18, 18, 18).add(
                                    panLoadAndInscribeLayout.createParallelGroup(
                                        org.jdesktop.layout.GroupLayout.LEADING).add(historieCheckBox).add(
                                        baumdateiCheckBox).add(mipaCheckBox))).add(notizenCheckBox).add(
                                org.jdesktop.layout.GroupLayout.TRAILING,
                                scpLoadingStatus,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                313,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))).add(
                        org.jdesktop.layout.GroupLayout.LEADING,
                        jLabel6).add(
                        jSeparator1,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        337,
                        Short.MAX_VALUE)).addContainerGap()));
        panLoadAndInscribeLayout.setVerticalGroup(
            panLoadAndInscribeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panLoadAndInscribeLayout.createSequentialGroup().addContainerGap().add(jLabel6).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    jSeparator1,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    6,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panLoadAndInscribeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        panLoadAndInscribeLayout.createSequentialGroup().add(nutzungenCheckBox).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.UNRELATED).add(rebeCheckBox).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.UNRELATED).add(vorgaengeCheckBox).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED,
                            23,
                            Short.MAX_VALUE).add(notizenCheckBox)).add(
                        panLoadAndInscribeLayout.createSequentialGroup().add(mipaCheckBox).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.UNRELATED).add(baumdateiCheckBox).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.UNRELATED).add(historieCheckBox))).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    scpLoadingStatus,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    85,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(25, 25, 25).add(
                    jSeparator4,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));

        final org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().add(
                    panDesc,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    173,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panLoadAndInscribe,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap()).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                layout.createSequentialGroup().addContainerGap(247, Short.MAX_VALUE).add(
                    cmdCancel,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    125,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(18, 18, 18).add(
                    cmdOk,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    126,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(24, 24, 24)));
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().add(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false).add(
                        panDesc,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                        panLoadAndInscribe,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(cmdOk).add(cmdCancel))
                            .addContainerGap()));

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
        final Runnable r = new Runnable() {

                @Override
                public void run() {
                    java.awt.EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                pdfWait.setLocationRelativeTo(parentComponent);
                                pdfWait.setVisible(true);
                            }
                        });

                    try {
                        if (notizenCheckBox.isSelected()) {
                            paramMap.put(PARAM_NOTIZEN, notizenTextArea.getText());
                        }

                        final InputStream in = getClass().getResourceAsStream(REPORT_MASTER);
                        final JasperReport jasperReport = (JasperReport)JRLoader.loadObject(in);
                        final JasperPrint jasperPrint = JasperFillManager.fillReport(
                                jasperReport,
                                paramMap,
                                new EmptyDataSource(1));

                        final File f = new File(TARGET_FILE);
                        JasperExportManager.exportReportToPdfFile(jasperPrint, f.toString());

                        log.info("try to open pdf:" + TARGET_FILE_URL); // NOI18N
                        de.cismet.tools.BrowserLauncher.openURL(TARGET_FILE_URL);

                        java.awt.EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    pdfWait.dispose();
                                }
                            });
                    } catch (final Exception tt) {
                        log.error("Error during Jaspern", tt); // NOI18N

                        final ErrorInfo ei = new ErrorInfo(

                                java.util.ResourceBundle.getBundle("de/cismet/lagis/report/printing/Bundle").getString(
                                    "ReportPrintingWidget.cmdOKActionPerformed(ActionEvent).ErrorInfo.title"),

                                java.util.ResourceBundle.getBundle("de/cismet/lagis/report/printing/Bundle").getString(
                                    "ReportPrintingWidget.cmdOKActionPerformed(ActionEvent).ErrorInfo.message"),
                                null,
                                null,
                                tt,
                                Level.ALL,
                                null);
                        JXErrorPane.showDialog(ReportPrintingWidget.this.parentComponent, ei);

                        if (pdfWait.isVisible()) {
                            pdfWait.dispose();
                        }
                    }
                }
            };
        CismetThreadPool.execute(r);
        dispose();
    } //GEN-LAST:event_cmdOkActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void vorgaengeCheckBoxActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_vorgaengeCheckBoxActionPerformed
        this.handleParamMap(PARAM_VORGAENGE, this.vorgaengeCheckBox.isSelected());
    }                                                                                     //GEN-LAST:event_vorgaengeCheckBoxActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void nutzungenCheckBoxActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_nutzungenCheckBoxActionPerformed
        this.handleParamMap(PARAM_NUTZUNGEN, this.nutzungenCheckBox.isSelected());
    }                                                                                     //GEN-LAST:event_nutzungenCheckBoxActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void rebeCheckBoxActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_rebeCheckBoxActionPerformed
        this.handleParamMap(PARAM_REBE, this.rebeCheckBox.isSelected());
    }                                                                                //GEN-LAST:event_rebeCheckBoxActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void notizenCheckBoxActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_notizenCheckBoxActionPerformed
        final boolean isSelected = this.notizenCheckBox.isSelected();

        this.notizenTextArea.setEnabled(isSelected);
        if (isSelected) {
            this.notizenTextArea.setBackground(Color.WHITE);
        } else {
            this.notizenTextArea.setBackground(this.getBackground());
        }

        this.handleParamMap(PARAM_NOTIZEN, isSelected);
    } //GEN-LAST:event_notizenCheckBoxActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mipaCheckBoxActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mipaCheckBoxActionPerformed
        this.handleParamMap(PARAM_MIPA, this.mipaCheckBox.isSelected());
    }                                                                                //GEN-LAST:event_mipaCheckBoxActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void baumdateiCheckBoxActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_baumdateiCheckBoxActionPerformed
        this.handleParamMap(PARAM_BAUMDATEI, this.baumdateiCheckBox.isSelected());
    }                                                                                     //GEN-LAST:event_baumdateiCheckBoxActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void historieCheckBoxActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_historieCheckBoxActionPerformed
        this.handleParamMap(PARAM_HISTORY, this.historieCheckBox.isSelected());
    }                                                                                    //GEN-LAST:event_historieCheckBoxActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  args  the command line arguments
     */
    public static void main(final String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    new ReportPrintingWidget(new javax.swing.JFrame(), true).setVisible(true);
                }
            });
    }
}
