/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * FlurstueckDetailsReportDialog.java
 *
 * Created on Sep 13, 2011, 11:36:05 AM
 */
package de.cismet.lagis.report;

import java.util.HashMap;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class FlurstueckDetailsReportDialog extends javax.swing.JDialog {

    //~ Static fields/initializers ---------------------------------------------

    private static final String PARAM_NUTZUNGEN = "param_nutzungen";
    private static final String PARAM_REBE = "param_rebe";
    private static final String PARAM_VORGAENGE = "param_vorgaenge";
    private static final String PARAM_HISTORY = "param_history";
    private static final String PARAM_MIPA = "param_mipa";
    private static final String PARAM_BAUMDATEI = "param_baumdatei";
    private static final String PARAM_NOTIZEN = "param_notizen";

    //~ Instance fields --------------------------------------------------------

    private final HashMap<String, Object> paramMap;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox baumdateiCheckBox;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel centerPanelBox;
    private javax.swing.JCheckBox historieCheckBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JCheckBox notizenCheckBox;
    private javax.swing.JTextArea notizenTextArea;
    private javax.swing.JCheckBox nutzungenCheckBox;
    private javax.swing.JButton okButton;
    private javax.swing.JCheckBox rebeCheckBox;
    private javax.swing.JCheckBox vermietungenCheckBox;
    private javax.swing.JCheckBox vorgaengeCheckBox;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form FlurstueckDetailsReportDialog.
     *
     * @param  parent  DOCUMENT ME!
     * @param  modal   DOCUMENT ME!
     */
    public FlurstueckDetailsReportDialog(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);
        initComponents();

        this.paramMap = new HashMap<String, Object>(7);

        this.nutzungenCheckBox.setSelected(true);
        this.rebeCheckBox.setSelected(true);
        this.historieCheckBox.setSelected(true);
        this.baumdateiCheckBox.setSelected(true);
        this.vorgaengeCheckBox.setSelected(true);
        this.vermietungenCheckBox.setSelected(true);
        this.notizenCheckBox.setSelected(true);

        this.handleParamMap(PARAM_NUTZUNGEN, true);
        this.handleParamMap(PARAM_REBE, true);
        this.handleParamMap(PARAM_HISTORY, true);
        this.handleParamMap(PARAM_BAUMDATEI, true);
        this.handleParamMap(PARAM_VORGAENGE, true);
        this.handleParamMap(PARAM_MIPA, true);
        this.handleParamMap(PARAM_NOTIZEN, true);

        this.notizenTextArea.setEditable(true);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        notizenTextArea = new javax.swing.JTextArea();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        centerPanelBox = new javax.swing.JPanel();
        nutzungenCheckBox = new javax.swing.JCheckBox();
        rebeCheckBox = new javax.swing.JCheckBox();
        vorgaengeCheckBox = new javax.swing.JCheckBox();
        historieCheckBox = new javax.swing.JCheckBox();
        vermietungenCheckBox = new javax.swing.JCheckBox();
        baumdateiCheckBox = new javax.swing.JCheckBox();
        notizenCheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(
                FlurstueckDetailsReportDialog.class,
                "FlurstueckDetailsReportDialog.title")); // NOI18N
        setResizable(false);

        notizenTextArea.setColumns(20);
        notizenTextArea.setEditable(false);
        notizenTextArea.setRows(5);
        notizenTextArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        notizenTextArea.setEnabled(false);
        jScrollPane1.setViewportView(notizenTextArea);

        okButton.setText(org.openide.util.NbBundle.getMessage(
                FlurstueckDetailsReportDialog.class,
                "FlurstueckDetailsReportDialog.okButton.text")); // NOI18N
        okButton.setMaximumSize(new java.awt.Dimension(55, 29));
        okButton.setMinimumSize(new java.awt.Dimension(55, 29));
        okButton.setPreferredSize(new java.awt.Dimension(55, 29));
        okButton.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    okButtonActionPerformed(evt);
                }
            });

        cancelButton.setText(org.openide.util.NbBundle.getMessage(
                FlurstueckDetailsReportDialog.class,
                "FlurstueckDetailsReportDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cancelButtonActionPerformed(evt);
                }
            });

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        centerPanelBox.setLayout(new javax.swing.BoxLayout(centerPanelBox, javax.swing.BoxLayout.Y_AXIS));

        nutzungenCheckBox.setText(org.openide.util.NbBundle.getMessage(
                FlurstueckDetailsReportDialog.class,
                "FlurstueckDetailsReportDialog.nutzungenCheckBox.text")); // NOI18N
        nutzungenCheckBox.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    nutzungenCheckBoxActionPerformed(evt);
                }
            });
        centerPanelBox.add(nutzungenCheckBox);

        rebeCheckBox.setText(org.openide.util.NbBundle.getMessage(
                FlurstueckDetailsReportDialog.class,
                "FlurstueckDetailsReportDialog.rebeCheckBox.text")); // NOI18N
        rebeCheckBox.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    rebeCheckBoxActionPerformed(evt);
                }
            });
        centerPanelBox.add(rebeCheckBox);

        vorgaengeCheckBox.setText(org.openide.util.NbBundle.getMessage(
                FlurstueckDetailsReportDialog.class,
                "FlurstueckDetailsReportDialog.vorgaengeCheckBox.text")); // NOI18N
        vorgaengeCheckBox.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    vorgaengeCheckBoxActionPerformed(evt);
                }
            });
        centerPanelBox.add(vorgaengeCheckBox);

        historieCheckBox.setText(org.openide.util.NbBundle.getMessage(
                FlurstueckDetailsReportDialog.class,
                "FlurstueckDetailsReportDialog.historieCheckBox.text")); // NOI18N
        historieCheckBox.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    historieCheckBoxActionPerformed(evt);
                }
            });
        centerPanelBox.add(historieCheckBox);

        vermietungenCheckBox.setText(org.openide.util.NbBundle.getMessage(
                FlurstueckDetailsReportDialog.class,
                "FlurstueckDetailsReportDialog.vermietungenCheckBox.text")); // NOI18N
        vermietungenCheckBox.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    vermietungenCheckBoxActionPerformed(evt);
                }
            });
        centerPanelBox.add(vermietungenCheckBox);

        baumdateiCheckBox.setText(org.openide.util.NbBundle.getMessage(
                FlurstueckDetailsReportDialog.class,
                "FlurstueckDetailsReportDialog.baumdateiCheckBox.text")); // NOI18N
        baumdateiCheckBox.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    baumdateiCheckBoxActionPerformed(evt);
                }
            });
        centerPanelBox.add(baumdateiCheckBox);

        notizenCheckBox.setText(org.openide.util.NbBundle.getMessage(
                FlurstueckDetailsReportDialog.class,
                "FlurstueckDetailsReportDialog.notizenCheckBox.text")); // NOI18N
        notizenCheckBox.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    notizenCheckBoxActionPerformed(evt);
                }
            });
        centerPanelBox.add(notizenCheckBox);

        jPanel2.add(centerPanelBox);

        final javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(
                    jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        javax.swing.GroupLayout.Alignment.TRAILING,
                        jPanel1Layout.createSequentialGroup().addComponent(
                            okButton,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18).addComponent(cancelButton)
                                    .addGap(222, 222, 222)).addGroup(
                        jPanel1Layout.createSequentialGroup().addGroup(
                            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(
                                            jPanel2,
                                            javax.swing.GroupLayout.Alignment.LEADING,
                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                            509,
                                            Short.MAX_VALUE)).addContainerGap(10, Short.MAX_VALUE)))));
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                jPanel1Layout.createSequentialGroup().addContainerGap(13, Short.MAX_VALUE).addComponent(
                    jPanel2,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    164,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(
                    jScrollPane1,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    89,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18).addGroup(
                    jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        cancelButton).addComponent(
                        okButton,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap()));

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    } // </editor-fold>//GEN-END:initComponents

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
    private void baumdateiCheckBoxActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_baumdateiCheckBoxActionPerformed
        this.handleParamMap(PARAM_BAUMDATEI, this.baumdateiCheckBox.isSelected());
    }                                                                                     //GEN-LAST:event_baumdateiCheckBoxActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void vermietungenCheckBoxActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_vermietungenCheckBoxActionPerformed
        this.handleParamMap(PARAM_MIPA, this.vermietungenCheckBox.isSelected());
    }                                                                                        //GEN-LAST:event_vermietungenCheckBoxActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void notizenCheckBoxActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_notizenCheckBoxActionPerformed

        final boolean isSelected = this.notizenCheckBox.isSelected();
        this.notizenTextArea.setEnabled(isSelected);
        this.notizenTextArea.setEditable(isSelected);
        this.handleParamMap(PARAM_NOTIZEN, isSelected);
    } //GEN-LAST:event_notizenCheckBoxActionPerformed

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
    private void historieCheckBoxActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_historieCheckBoxActionPerformed
        this.handleParamMap(PARAM_HISTORY, this.historieCheckBox.isSelected());
    }                                                                                    //GEN-LAST:event_historieCheckBoxActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cancelButtonActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cancelButtonActionPerformed
        this.close();
    }                                                                                //GEN-LAST:event_cancelButtonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void okButtonActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_okButtonActionPerformed
        if (this.notizenCheckBox.isSelected()) {
            this.paramMap.put(PARAM_NOTIZEN, this.notizenTextArea.getText());
        }

        FlurstueckDetailsReport.showReport(this.paramMap);
        this.close();
    } //GEN-LAST:event_okButtonActionPerformed

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
     */
    private void close() {
        super.setVisible(false);
        super.dispose();
    }

    /**
     * DOCUMENT ME!
     */
    public static void showDialog() {
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final FlurstueckDetailsReportDialog dialog = new FlurstueckDetailsReportDialog(
                            LagisBroker.getInstance().getParentComponent(),
                            true);

                    StaticSwingTools.showDialog(dialog);
                }
            });
    }
}
