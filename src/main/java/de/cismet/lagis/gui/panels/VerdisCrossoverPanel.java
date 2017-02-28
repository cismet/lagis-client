/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * VerdisCrossoverPanel.java
 *
 * Created on 03.09.2009, 16:48:33
 */
package de.cismet.lagis.gui.panels;

import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXTable;

import java.awt.Point;
import java.awt.event.*;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.gui.tables.KassenzeichenTable;

import de.cismet.layout.FadingCardLayout;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
public class VerdisCrossoverPanel extends javax.swing.JPanel implements MouseListener, ListSelectionListener {

    //~ Static fields/initializers ---------------------------------------------

    // ToDo defaults für Panel ?
    private static final Logger log = org.apache.log4j.Logger.getLogger(VerdisCrossoverPanel.class);
    private static final String server = "http://localhost:";
    private static final String request = "/gotoKassenzeichen?";
    // ToDo perhaps place in VerdisCrossover
    // Problem: would be the the only dependency to verdis
    // http://localhost:18000/verdis/gotoKassenzeichen?kassenzeichen=6000442
    public static final NameValuePair PARAMETER_KASSENZEICHEN = new NameValuePair("kassenzeichen", "");
    private static final String PROGRESS_CARD_NAME = "progress";
    private static final String CONTENT_CARD_NAME = "content";
    private static final String MESSAGE_CARD_NAME = "message";
    private static final String SWITCH_TO_MENU_NAME = "Zu Kassenzeichen wechseln";

    //~ Instance fields --------------------------------------------------------

    // TODO Jean
    private final KassenzeichenTableModel tableModel = new KassenzeichenTableModel();
    private FadingCardLayout layout = new FadingCardLayout();
    private JPopupMenu switchToKassenzeichenPopup;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnLoadSelectedKassenzeichen;
    private javax.swing.JButton btnLoadSelectedKassenzeichen1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JPanel panAll;
    private javax.swing.JPanel panContent;
    private javax.swing.JPanel panContentMessage;
    private javax.swing.JPanel panContentProgress;
    private javax.swing.JPanel panControl;
    private javax.swing.JProgressBar pgbProgress;
    private javax.swing.JTable tblKassenzeichen;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form VerdisCrossoverPanel.
     */
    public VerdisCrossoverPanel() {
        initComponents();
        configurePopupMenue();
        panAll.setLayout(layout);
        panAll.removeAll();
        panAll.add(panContentProgress, PROGRESS_CARD_NAME);
        panAll.add(panContent, CONTENT_CARD_NAME);
        panAll.add(panContentMessage, MESSAGE_CARD_NAME);
        // TODO Jean
        tblKassenzeichen.setModel(tableModel);
        tblKassenzeichen.addMouseListener(this);
        tblKassenzeichen.addMouseListener(new PopupListener());
        tblKassenzeichen.getSelectionModel().addListSelectionListener(this);
        pgbProgress.setIndeterminate(true);
//        this.add(panContentProgress, BorderLayout.CENTER);
        layout.show(panAll, PROGRESS_CARD_NAME);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void startSearch() {
        // TODO Jean
        try {
            LagisBroker.getInstance().execute(new KassenzeichenRetriever());
        } catch (Exception ex) {
            log.error("Fehler während dem suchen der Kassenzeichen: ", ex);
            // ToDo Nachricht an benutzer
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panControl = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnLoadSelectedKassenzeichen = new javax.swing.JButton();
        btnLoadSelectedKassenzeichen1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        panAll = new javax.swing.JPanel();
        panContentProgress = new javax.swing.JPanel();
        pgbProgress = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        panContentMessage = new javax.swing.JPanel();
        lblMessage = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        panContent = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKassenzeichen = new JXTable();

        setPreferredSize(new java.awt.Dimension(500, 200));
        setLayout(new java.awt.GridBagLayout());

        panControl.setMinimumSize(new java.awt.Dimension(50, 50));
        panControl.setPreferredSize(new java.awt.Dimension(300, 50));
        panControl.setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        btnClose.setText(org.openide.util.NbBundle.getMessage(
                VerdisCrossoverPanel.class,
                "VerdisCrossoverPanel.btnClose.text")); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCloseActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 2, 6);
        jPanel1.add(btnClose, gridBagConstraints);

        btnLoadSelectedKassenzeichen.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/postion.png"))); // NOI18N
        btnLoadSelectedKassenzeichen.setText(org.openide.util.NbBundle.getMessage(
                VerdisCrossoverPanel.class,
                "VerdisCrossoverPanel.btnLoadSelectedKassenzeichen.text"));                       // NOI18N
        btnLoadSelectedKassenzeichen.setToolTipText(org.openide.util.NbBundle.getMessage(
                VerdisCrossoverPanel.class,
                "VerdisCrossoverPanel.btnLoadSelectedKassenzeichen.toolTipText"));                // NOI18N
        btnLoadSelectedKassenzeichen.setEnabled(false);
        btnLoadSelectedKassenzeichen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnLoadSelectedKassenzeichenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 2, 6);
        jPanel1.add(btnLoadSelectedKassenzeichen, gridBagConstraints);

        btnLoadSelectedKassenzeichen1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/kassenzeicheninfo_add.png"))); // NOI18N
        btnLoadSelectedKassenzeichen1.setText(org.openide.util.NbBundle.getMessage(
                VerdisCrossoverPanel.class,
                "VerdisCrossoverPanel.btnLoadSelectedKassenzeichen1.text"));                                    // NOI18N
        btnLoadSelectedKassenzeichen1.setToolTipText(org.openide.util.NbBundle.getMessage(
                VerdisCrossoverPanel.class,
                "VerdisCrossoverPanel.btnLoadSelectedKassenzeichen1.toolTipText"));                             // NOI18N
        btnLoadSelectedKassenzeichen1.setEnabled(false);
        btnLoadSelectedKassenzeichen1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnLoadSelectedKassenzeichen1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 2, 6);
        jPanel1.add(btnLoadSelectedKassenzeichen1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        panControl.add(jPanel1, gridBagConstraints);

        final javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panControl.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(panControl, gridBagConstraints);

        panAll.setPreferredSize(new java.awt.Dimension(400, 251));
        panAll.setLayout(new java.awt.CardLayout());

        panContentProgress.setPreferredSize(new java.awt.Dimension(250, 140));

        jLabel1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/searching.png"))); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(
                VerdisCrossoverPanel.class,
                "VerdisCrossoverPanel.jLabel2.text")); // NOI18N

        final javax.swing.GroupLayout panContentProgressLayout = new javax.swing.GroupLayout(panContentProgress);
        panContentProgress.setLayout(panContentProgressLayout);
        panContentProgressLayout.setHorizontalGroup(
            panContentProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panContentProgressLayout.createSequentialGroup().addContainerGap().addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panContentProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(
                                    pgbProgress,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    384,
                                    Short.MAX_VALUE).addComponent(jLabel2)).addContainerGap()));
        panContentProgressLayout.setVerticalGroup(
            panContentProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                panContentProgressLayout.createSequentialGroup().addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE).addGroup(
                    panContentProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addGroup(
                        panContentProgressLayout.createSequentialGroup().addComponent(jLabel2).addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                            pgbProgress,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE).addGap(12, 12, 12)).addComponent(
                        jLabel1,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        67,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap()));

        panAll.add(panContentProgress, "card3");

        panContentMessage.setPreferredSize(new java.awt.Dimension(250, 140));

        lblMessage.setText(org.openide.util.NbBundle.getMessage(
                VerdisCrossoverPanel.class,
                "VerdisCrossoverPanel.lblMessage.text")); // NOI18N

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/warn.png"))); // NOI18N
        jLabel3.setText(org.openide.util.NbBundle.getMessage(
                VerdisCrossoverPanel.class,
                "VerdisCrossoverPanel.jLabel3.text"));                                                                   // NOI18N

        final javax.swing.GroupLayout panContentMessageLayout = new javax.swing.GroupLayout(panContentMessage);
        panContentMessage.setLayout(panContentMessageLayout);
        panContentMessageLayout.setHorizontalGroup(
            panContentMessageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                panContentMessageLayout.createSequentialGroup().addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE).addComponent(
                    jLabel3,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    54,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18).addComponent(
                    lblMessage,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    388,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap()));
        panContentMessageLayout.setVerticalGroup(
            panContentMessageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                panContentMessageLayout.createSequentialGroup().addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE).addGroup(
                    panContentMessageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(
                                    jLabel3,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    67,
                                    javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                        lblMessage,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        59,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap()));

        panAll.add(panContentMessage, "card2");

        tblKassenzeichen.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    { null, null, null, null },
                    { null, null, null, null },
                    { null, null, null, null },
                    { null, null, null, null }
                },
                new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
        jScrollPane1.setViewportView(tblKassenzeichen);

        final javax.swing.GroupLayout panContentLayout = new javax.swing.GroupLayout(panContent);
        panContent.setLayout(panContentLayout);
        panContentLayout.setHorizontalGroup(
            panContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panContentLayout.createSequentialGroup().addContainerGap().addComponent(
                    jScrollPane1,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    460,
                    Short.MAX_VALUE).addContainerGap()));
        panContentLayout.setVerticalGroup(
            panContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panContentLayout.createSequentialGroup().addContainerGap().addComponent(
                    jScrollPane1,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    120,
                    Short.MAX_VALUE).addContainerGap()));

        panAll.add(panContent, "card4");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panAll, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCloseActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCloseActionPerformed
        closeDialog();
    }                                                                            //GEN-LAST:event_btnCloseActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnLoadSelectedKassenzeichenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnLoadSelectedKassenzeichenActionPerformed
        loadSelectedKassenzeichen();
    }                                                                                                //GEN-LAST:event_btnLoadSelectedKassenzeichenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnLoadSelectedKassenzeichen1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnLoadSelectedKassenzeichen1ActionPerformed
        addSelectedKassenzeichenToList();
    }                                                                                                 //GEN-LAST:event_btnLoadSelectedKassenzeichen1ActionPerformed
    /**
     * ToDo ugly.
     */
    private void closeDialog() {
        ((JDialog)getParent().getParent().getParent().getParent()).dispose();
    }

    /**
     * ToDo make commons.
     */
    private void configurePopupMenue() {
        switchToKassenzeichenPopup = new JPopupMenu();
        final JMenuItem switchToKassenZeichenItem = new JMenuItem(SWITCH_TO_MENU_NAME);
        switchToKassenZeichenItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (log.isDebugEnabled()) {
                        log.debug("action performed");
                    }
                    loadSelectedKassenzeichen();
                }
            });
        switchToKassenzeichenPopup.add(switchToKassenZeichenItem);
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        // TODO Jean
        if (log.isDebugEnabled()) {
            log.debug("Crossover: mouse clicked");
            log.debug("tableModelsize: "
                        + tableModel.getRowCount());
        }
        if (log.isDebugEnabled()) {
            log.debug("tableModel content: "
                        + tableModel.getAllKassenzeichen());
        }
        final Object source = e.getSource();
        if (source instanceof JXTable) {
            if (e.getClickCount() > 1) {
                loadSelectedKassenzeichen();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Crossover: Kein Multiclick");
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Crossover:Mouselistner nicht für JXTable");
            }
        }
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
    }

    /**
     * DOCUMENT ME!
     */
    // TODO Jean
    private void loadSelectedKassenzeichen() {
        try {
            final int selectedRow = tblKassenzeichen.getSelectedRow();
            if (selectedRow != -1) {
                final int modelIndex = ((JXTable)tblKassenzeichen).convertRowIndexToModel(selectedRow);
                if (modelIndex != -1) {
                    final CidsBean selectedKassenzeichen = tableModel.getKassenzeichenAtIndex(modelIndex);
                    if (selectedKassenzeichen != null) {
                        LagisBroker.getInstance().openKassenzeichenInVerdis(selectedKassenzeichen);
                    } else {
                        log.warn("Crossover: Kein Kassenzeichen zu angebenen Index.");
                    }
                } else {
                    log.warn("Crossover: Kein ModelIndex zu angebenen ViewIndex.");
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Crossover: Keine Tabellenzeile selektiert.");
                }
            }
        } catch (Exception ex) {
            log.error("Fehler beim laden des selektierten Kasssenzeichens", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void addSelectedKassenzeichenToList() {
        try {
            final int selectedRow = tblKassenzeichen.getSelectedRow();
            if (selectedRow != -1) {
                final int modelIndex = ((JXTable)tblKassenzeichen).convertRowIndexToModel(selectedRow);
                if (modelIndex != -1) {
                    final CidsBean selectedKassenzeichen = tableModel.getKassenzeichenAtIndex(modelIndex);
                    if (selectedKassenzeichen != null) {
                        ((KassenzeichenTable)KassenzeichenPanel.getInstance().getTable()).addNewKassenzeichen((Integer)
                            selectedKassenzeichen.getProperty("kassenzeichennummer8"));
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Fehler beim hinzufügen des selektierten Kasssenzeichens", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   port  e port DOCUMENT ME!
     * @param   bean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    // TODO Jean
    public static URL createQuery(final int port, final CidsBean bean) {
        if ((port < 0) || (port > 65535)) {
            log.warn("Crossover: verdisCrossoverPort ist ungültig: " + port);
        } else {
            try {
                // ToDo ugly because is static
                PARAMETER_KASSENZEICHEN.setValue(String.valueOf(bean.getProperty("kassenzeichennummer8"))); // kz.getId().toString());
                final GetMethod tmp = new GetMethod(server + port + request);
                tmp.setQueryString(new NameValuePair[] { PARAMETER_KASSENZEICHEN });
                if (log.isDebugEnabled()) {
                    log.debug("Crossover: verdisCrossOverQuery: " + tmp.getURI().toString());
                }
                return new URL(tmp.getURI().toString());
            } catch (Exception ex) {
                log.error("Crossover: Fehler beim fernsteuern von VerdIS.", ex);
            }
        }
        return null;
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
        if (tblKassenzeichen.getSelectedRowCount() > 0) {
            btnLoadSelectedKassenzeichen.setEnabled(true);
            btnLoadSelectedKassenzeichen1.setEnabled(LagisBroker.getInstance().isInEditMode());
        } else {
            btnLoadSelectedKassenzeichen.setEnabled(false);
            btnLoadSelectedKassenzeichen1.setEnabled(false);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class PopupListener extends MouseAdapter {

        //~ Methods ------------------------------------------------------------

        @Override
        public void mouseClicked(final MouseEvent e) {
            // TODO Jean
            showPopup(e);
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            // TODO Jean
            showPopup(e);
        }

        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
        // TODO Jean
        private void showPopup(final MouseEvent e) {
            if (log.isDebugEnabled()) {
                log.debug("showPopup");
            }
            if (e.isPopupTrigger()) {
                if (log.isDebugEnabled()) {
                    // ToDo funktioniert nicht unter linux
                    log.debug("popup triggered");
                }
                final int rowAtPoint = tblKassenzeichen.rowAtPoint(new Point(e.getX(), e.getY()));
                if ((rowAtPoint != -1)
                            && ((tableModel.getKassenzeichenAtIndex(
                                        ((JXTable)tblKassenzeichen).convertRowIndexToModel(
                                            rowAtPoint))) != null)) {
                    if (log.isDebugEnabled()) {
                        log.debug("KassenzeichenEntity found");
                    }
                    switchToKassenzeichenPopup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    // TODO Jean
    public class KassenzeichenTableModel extends AbstractTableModel {

        //~ Instance fields ----------------------------------------------------

// ~ Instance fields ----------------------------------------------------
        private final String[] COLUMN_HEADER = { "Kassenzeichen" };
        private final ArrayList<CidsBean> data = new ArrayList<CidsBean>();

        //~ Methods ------------------------------------------------------------

        // ~ Methods ------------------------------------------------------------
        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            final CidsBean value = data.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return value.getProperty("kassenzeichennummer8");
                }
                default: {
                    return "Spalte ist nicht definiert";
                }
            }
        }

        /**
         * * DOCUMENT ME!
         *
         * @param  newData  DOCUMENT ME!
         */
        public void updateTableModel(final Set newData) {
            data.clear();
            if (newData
                        != null) {
                data.addAll(newData);
            }
            fireTableDataChanged();
        }

        /**
         * * DOCUMENT ME!
         *
         * @param   index  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public CidsBean getKassenzeichenAtIndex(final int index) {
            return data.get(index);
        }

        /**
         * * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public ArrayList getAllKassenzeichen() {
            return data;
        }

        @Override
        public String getColumnName(final int column) {
            return COLUMN_HEADER[column];
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    // TODO Jean
    class KassenzeichenRetriever extends SwingWorker<Set<CidsBean>, Void> {

        //~ Methods ------------------------------------------------------------

// ~ Methods  ------------------------------------------------------------
        @Override
        protected Set<CidsBean> doInBackground() throws Exception {
            final FlurstueckSchluesselCustomBean currentKey = LagisBroker.getInstance()
                        .getCurrentFlurstueckSchluessel();
            if (currentKey != null) {
                final Geometry flurstueckGeom = LagisBroker.getInstance().getInstance().getCurrentWFSGeometry();
                if (flurstueckGeom != null) {
                    final double buffer = (flurstueckGeom.getArea() > 100)
                        ? LagisBroker.getInstance().getKassenzeichenBuffer100()
                        : LagisBroker.getInstance().getKassenzeichenBuffer();

                    final String query = "SELECT 11, k.id\n"
                                + "FROM  kassenzeichen k, kassenzeichen_geometrie kg, geom\n"
                                + "WHERE k.id = kg.kassenzeichen AND kg.geometrie = geom.id\n"
                                + "AND not isEmpty(geom.geo_field)\n"
                                + "AND intersects(geom.geo_field,st_buffer(st_buffer(geometryfromtext('"
                                + flurstueckGeom.toString() + "',25832), "
                                + buffer + "), 0))";

                    if (log.isDebugEnabled()) {
                        log.debug(query);
                    }

                    if (isCancelled()) {
                        return null;
                    }

                    final MetaObject[] result = CidsBroker.getInstance().getMetaObject(query, "VERDIS_GRUNDIS");
                    final HashSet<CidsBean> kassenzeichen = new HashSet<CidsBean>((result == null) ? 0 : result.length);

                    if (result != null) {
                        for (int i = 0; i < result.length; i++) {
                            kassenzeichen.add(result[i].getBean());
                        }
                    }

//                        kassenzeichen = verdisServer.getIntersectingKassenzeichen(flurstueckGeom,
//                                LagisBroker.getInstance().getKassenzeichenBuffer());

                    if ((kassenzeichen != null) && (kassenzeichen.size() > 0)) {
                        if (log.isDebugEnabled()) {
                            log.debug("Crossover: Anzahl Kassenzeichen: " + kassenzeichen.size());
                        }
                    } else {
                        log.info("Crossover:Keine geschnittenen Kassenzeichen gefunden."); // ToDo Meldung an benutzer
                    }
                    return kassenzeichen;
                } else {                                                                   // ToDo user message !
                    lblMessage.setText(
                        "<html>Keine Flurstücksgeometrie vorhanden,<br/>bestimmen der Kasssenzeichen nicht möglich.</html>");
                    log.warn("Crossover: Keine Geometrie vorhanden zum bestimmen der Kassenzeichen");
                }
            } else {
                // ToDo user message !
                lblMessage.setText(
                    "<html>Bitte wählen Sie ein Flurstück aus,<br/>damit Kassenzeichen bestimmt werden können.</html > ");
                log.warn("Crossover: Kein  Flurstück ausgewählt kann Lagis Kassenzeichen nicht bestimmen");
            }
            return null;
        }

        @Override
        protected void done() {
            if (log.isDebugEnabled()) {
                log.debug("KassenzeichenRetriever done.");
            }
            super.done();
            if (isCancelled()) {
                if (log.isDebugEnabled()) {
                    log.debug("Kassenzeichen retriever canceled.Nothing to do {}");
                }
            }
            try {
                Set<CidsBean> results = get();
                if (results == null) {
                    results = new HashSet<CidsBean>();
                    tableModel.updateTableModel(results);
                    layout.show(panAll, MESSAGE_CARD_NAME);
                } else {
                    tableModel.updateTableModel(results);
                    layout.show(panAll, CONTENT_CARD_NAME);
                }
            } catch (Exception ex) {
                log.error("Fehler beim verarbeiten der Ergebnisse: ", ex);
                tableModel.updateTableModel(new HashSet<CidsBean>());
                lblMessage.setText("<html>Fehler beim abfragen<br/>der Kassenzeichen.< /html >");
                layout.show(panAll, MESSAGE_CARD_NAME);
            }
            VerdisCrossoverPanel.this.revalidate();
            VerdisCrossoverPanel.this.repaint();
            ((JDialog)getParent().getParent().getParent().getParent()).repaint();
        }
    }
}
