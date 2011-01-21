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

import bean.KassenzeichenFacadeRemote;

import com.vividsolutions.jts.geom.Geometry;

import entity.KassenzeichenEntity;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXTable;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.IOException;

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

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;

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
    private static final String request = "/verdis/gotoKassenzeichen?";
    // ToDo perhaps place in VerdisCrossover
    // Problem: would be the the only dependency to verdis
    // http://localhost:18000/verdis/gotoKassenzeichen?kassenzeichen=6000442
    public static final NameValuePair PARAMETER_KASSENZEICHEN = new NameValuePair("kassenzeichen", "");
    private static final String PROGRESS_CARD_NAME = "progress";
    private static final String CONTENT_CARD_NAME = "content";
    private static final String MESSAGE_CARD_NAME = "message";
    private static final String SWITCH_TO_MENU_NAME = "Zu Kassenzeichen wechseln";

    //~ Instance fields --------------------------------------------------------

    private final KassenzeichenTableModel tableModel = new KassenzeichenTableModel();
    private int verdisCrossoverPort = -1;
    private FadingCardLayout layout = new FadingCardLayout();
    private JPopupMenu switchToKassenzeichenPopup;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnLoadSelectedKassenzeichen;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
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
     *
     * @param  verdisCrossoverPort  DOCUMENT ME!
     */
    public VerdisCrossoverPanel(final int verdisCrossoverPort) {
        initComponents();
        configurePopupMenue();
        panAll.setLayout(layout);
        panAll.removeAll();
        panAll.add(panContentProgress, PROGRESS_CARD_NAME);
        panAll.add(panContent, CONTENT_CARD_NAME);
        panAll.add(panContentMessage, MESSAGE_CARD_NAME);
        tblKassenzeichen.setModel(tableModel);
        tblKassenzeichen.addMouseListener(this);
        tblKassenzeichen.addMouseListener(new PopupListener());
        tblKassenzeichen.getSelectionModel().addListSelectionListener(this);
        this.verdisCrossoverPort = verdisCrossoverPort;
        pgbProgress.setIndeterminate(true);
        // this.add(panContentProgress, BorderLayout.CENTER);
        layout.show(panAll, PROGRESS_CARD_NAME);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void startSearch() {
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
        panControl = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnLoadSelectedKassenzeichen = new javax.swing.JButton();
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

        panControl.setMinimumSize(new java.awt.Dimension(50, 50));
        panControl.setPreferredSize(new java.awt.Dimension(300, 50));

        btnClose.setText(org.openide.util.NbBundle.getMessage(
                VerdisCrossoverPanel.class,
                "VerdisCrossoverPanel.btnClose.text")); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCloseActionPerformed(evt);
                }
            });

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

        final javax.swing.GroupLayout panControlLayout = new javax.swing.GroupLayout(panControl);
        panControl.setLayout(panControlLayout);
        panControlLayout.setHorizontalGroup(
            panControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                panControlLayout.createSequentialGroup().addContainerGap(378, Short.MAX_VALUE).addComponent(
                    btnLoadSelectedKassenzeichen).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnClose).addContainerGap()));
        panControlLayout.setVerticalGroup(
            panControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panControlLayout.createSequentialGroup().addContainerGap().addGroup(
                    panControlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        btnClose).addComponent(btnLoadSelectedKassenzeichen)).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        panControlLayout.linkSize(
            javax.swing.SwingConstants.VERTICAL,
            new java.awt.Component[] { btnClose, btnLoadSelectedKassenzeichen });

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
                                    400,
                                    Short.MAX_VALUE).addComponent(jLabel2)).addContainerGap()));
        panContentProgressLayout.setVerticalGroup(
            panContentProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                panContentProgressLayout.createSequentialGroup().addContainerGap(49, Short.MAX_VALUE).addGroup(
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
                panContentMessageLayout.createSequentialGroup().addContainerGap(28, Short.MAX_VALUE).addComponent(
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
                panContentMessageLayout.createSequentialGroup().addContainerGap(49, Short.MAX_VALUE).addGroup(
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
                    476,
                    Short.MAX_VALUE).addContainerGap()));
        panContentLayout.setVerticalGroup(
            panContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panContentLayout.createSequentialGroup().addContainerGap().addComponent(
                    jScrollPane1,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    104,
                    Short.MAX_VALUE).addContainerGap()));

        panAll.add(panContent, "card4");

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                panAll,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                500,
                Short.MAX_VALUE).addComponent(
                panControl,
                javax.swing.GroupLayout.Alignment.TRAILING,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                500,
                Short.MAX_VALUE));
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addComponent(
                    panAll,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    128,
                    Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panControl,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    54,
                    javax.swing.GroupLayout.PREFERRED_SIZE)));
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
        if (log.isDebugEnabled()) {
            log.debug("Crossover: mouse clicked");
            log.debug("tableModelsize: " + tableModel.getRowCount());
        }
        if (log.isDebugEnabled()) {
            log.debug("tableModel content: " + tableModel.getAllKassenzeichen());
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
                log.debug("Crossover: Mouselistner nicht für JXTable");
            }
        }
    }
    /**
     * ToDo place query generation in VerdisCrossover. Give key get Query.
     *
     * @param  kz  DOCUMENT ME!
     */
    private void openKassenzeichenInVerdis(final KassenzeichenEntity kz) {
        if (kz != null) {
            if ((verdisCrossoverPort < 0) || (verdisCrossoverPort > 65535)) {
                log.warn("Crossover: verdisCrossoverPort ist ungültig: " + verdisCrossoverPort);
            } else {
                // ToDo Thread
                final URL verdisQuery = createQuery(verdisCrossoverPort, kz);
                if (verdisQuery != null) {
                    final SwingWorker<Void, Void> openKassenzeichen = new SwingWorker<Void, Void>() {

                            @Override
                            protected Void doInBackground() throws Exception {
                                verdisQuery.openStream();
                                return null;
                            }

                            @Override
                            protected void done() {
                                try {
                                    get();
                                } catch (Exception ex) {
                                    log.error("Fehler beim öffnen des Kassenzeichens", ex);
                                    // ToDo message to user;
                                }
                            }
                        };
                    LagisBroker.getInstance().execute(openKassenzeichen);
                } else {
                    log.warn("Crossover: konnte keine Query anlegen. Kein Abruf der Kassenzeichen möglich.");
                }
            }
        } else {
            log.warn("Crossover: Kann angebenes Flurstück nicht öffnwen");
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
    private void loadSelectedKassenzeichen() {
        try {
            final int selectedRow = tblKassenzeichen.getSelectedRow();
            if (selectedRow != -1) {
                final int modelIndex = ((JXTable)tblKassenzeichen).convertRowIndexToModel(selectedRow);
                if (modelIndex != -1) {
                    final KassenzeichenEntity selectedKassenzeichen = tableModel.getKassenzeichenAtIndex(modelIndex);
                    if (selectedKassenzeichen != null) {
                        openKassenzeichenInVerdis(selectedKassenzeichen);
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
     *
     * @param   port  DOCUMENT ME!
     * @param   kz    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static URL createQuery(final int port, final KassenzeichenEntity kz) {
        if ((port < 0) || (port > 65535)) {
            log.warn("Crossover: verdisCrossoverPort ist ungültig: " + port);
        } else {
            try {
                // ToDo ugly because is static
                PARAMETER_KASSENZEICHEN.setValue(kz.getId().toString());
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
        } else {
            btnLoadSelectedKassenzeichen.setEnabled(false);
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
            showPopup(e);
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            showPopup(e);
        }

        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
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
                KassenzeichenEntity selectedKassenzeichen = null;
                if ((rowAtPoint != -1)
                            && ((selectedKassenzeichen = tableModel.getKassenzeichenAtIndex(
                                            ((JXTable)tblKassenzeichen).getFilters().convertRowIndexToModel(
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
    public class KassenzeichenTableModel extends AbstractTableModel {

        //~ Instance fields ----------------------------------------------------

        private final String[] COLUMN_HEADER = { "Kassenzeichen" };
        private final ArrayList<KassenzeichenEntity> data = new ArrayList<KassenzeichenEntity>();

        //~ Methods ------------------------------------------------------------

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
            final KassenzeichenEntity value = data.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return value.getId();
                }
                default: {
                    return "Spalte ist nicht definiert";
                }
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param  newData  DOCUMENT ME!
         */
        public void updateTableModel(final Set newData) {
            data.clear();
            if (newData != null) {
                data.addAll(newData);
            }
            fireTableDataChanged();
        }

        /**
         * DOCUMENT ME!
         *
         * @param   index  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public KassenzeichenEntity getKassenzeichenAtIndex(final int index) {
            return data.get(index);
        }

        /**
         * DOCUMENT ME!
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
    class KassenzeichenRetriever extends SwingWorker<Set<KassenzeichenEntity>, Void> {

        //~ Methods ------------------------------------------------------------

        @Override
        protected Set<KassenzeichenEntity> doInBackground() throws Exception {
            final FlurstueckSchluessel currentKey = LagisBroker.getInstance()
                        .getInstance()
                        .getCurrentFlurstueckSchluessel();
            if (currentKey != null) {
                final Geometry flurstueckGeom = LagisBroker.getInstance().getInstance().getCurrentWFSGeometry();
                if (flurstueckGeom != null) {
                    log.info("Crossover: Geometrie zum bestimmen der Kassenzeichen: " + flurstueckGeom);
                    final KassenzeichenFacadeRemote verdisServer = LagisBroker.getInstance().getVerdisServer();
                    if (verdisServer != null) {
                        final Set<KassenzeichenEntity> kassenzeichen;

                        if (isCancelled()) {
                            return null;
                        }
                        kassenzeichen = verdisServer.getIntersectingKassenzeichen(
                                flurstueckGeom,
                                LagisBroker.getInstance().getKassenzeichenBuffer());

                        if ((kassenzeichen != null) && (kassenzeichen.size() > 0)) {
                            if (log.isDebugEnabled()) {
                                log.debug("Crossover: Anzahl Kassenzeichen: " + kassenzeichen.size());
                            }
                        } else {
                            log.info("Crossover: Keine geschnittenen Kassenzeichen gefunden.");
                            // ToDo Meldung an benutzer
                        }
                        return kassenzeichen;
                    } else {
                        lblMessage.setText(
                            "<html>Die Verbindung zum Verdisserver<br/>ist nicht richtig konfiguriert.</html>");
                        log.warn(
                            "Crossover: Kann die Kassenzeichen nicht bestimmen, weil die Verbindung zum server nicht richtig konfiguriert ist.");
                        log.warn("Crossover: lagisCrossover=" + verdisServer);
                    }
                } else {
                    // ToDo user message !
                    lblMessage.setText(
                        "<html>Keine Flurstücksgeometrie vorhanden,<br/>bestimmen der Kasssenzeichen nicht möglich.</html>");
                    log.warn("Crossover: Keine Geometrie vorhanden zum bestimmen der Kassenzeichen");
                }
            } else {
                // ToDo user message !
                lblMessage.setText(
                    "<html>Bitte wählen Sie ein Flurstück aus,<br/>damit Kassenzeichen bestimmt werden können.</html>");
                log.warn("Crossover: Kein Flurstück ausgewählt kann Lagis Kassenzeichen nicht bestimmen");
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
                    log.debug("Kassenzeichen retriever canceled. Nothing to do");
                }
            }
            try {
                Set<KassenzeichenEntity> results = get();
                if (results == null) {
                    results = new HashSet<KassenzeichenEntity>();
                    tableModel.updateTableModel(results);
                    layout.show(panAll, MESSAGE_CARD_NAME);
                } else {
                    tableModel.updateTableModel(results);
                    layout.show(panAll, CONTENT_CARD_NAME);
                }
            } catch (Exception ex) {
                log.error("Fehler beim verarbeiten der Ergebnisse: ", ex);
                tableModel.updateTableModel(new HashSet<KassenzeichenEntity>());
                lblMessage.setText("<html>Fehler beim abfragen<br/>der Kassenzeichen.</html>");
                layout.show(panAll, MESSAGE_CARD_NAME);
            }
            // VerdisCrossoverPanel.this.revalidate();
            // VerdisCrossoverPanel.this.repaint();
            // ((JDialog) getParent().getParent().getParent().getParent()).repaint();
        }
    }
}
