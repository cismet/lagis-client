/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * AktenzeichenSearch.java
 *
 * Created on 21. November 2008, 17:29
 */
package de.cismet.lagis.gui.panels;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXTable;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Collection;
import java.util.HashSet;

import javax.swing.Icon;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.models.FlurstueckeTableModel;

import de.cismet.lagis.renderer.FlurstueckSchluesselCellRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl This Panel enables the user to perform a Aktenzeichen search. The result are the Flurstuecke
 *           which have the entered Aktenzeichen. The Class supports regular expressions.
 * @version  $Revision$, $Date$
 */
public class FlurstueckeByAktenzeichenSearchPanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    /** the apache log4j logger. */
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(FlurstueckeByAktenzeichenSearchPanel.class);
    /** The result count string. */
    private static final String ANZAHL = "Anzahl:";
    private static final Icon ICON_VERTRAG = new javax.swing.ImageIcon(FlurstueckeByAktenzeichenSearchPanel.class
                    .getResource(
                        "/de/cismet/lagis/ressource/icons/toolbar/Aktenzeichensuche3.png"));
    private static final Icon ICON_MIPA = new javax.swing.ImageIcon(FlurstueckeByAktenzeichenSearchPanel.class
                    .getResource(
                        "/de/cismet/lagis/ressource/icons/toolbar/Aktenzeichensuche4.png"));

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum By {

        //~ Enum constants -----------------------------------------------------

        VERTRAG, MIPA
    }

    //~ Instance fields --------------------------------------------------------

    /** the table model which holds the founded Flurstueckschluessel of the search. */
    private final FlurstueckeTableModel tableModel = new FlurstueckeTableModel();
    /** the current searcher thread which handles the search and updates the GUI. */
    private FlurstueckSchluesselSearcher currentSearcher = null;
    // ToDo properties File
    private final By by;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblAnzahl;
    private javax.swing.JPanel panStatus;
    private javax.swing.JProgressBar pbrSearchProgress;
    private javax.swing.JTable tblAktenzeichen;
    private javax.swing.JTextField tfdAktenzeichen;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AktenzeichenSearch and configures the Component.
     */
    public FlurstueckeByAktenzeichenSearchPanel() {
        this(By.VERTRAG);
    }

    /**
     * Creates a new FlurstueckeByAktenzeichenSearchPanel object.
     *
     * @param  by  DOCUMENT ME!
     */
    public FlurstueckeByAktenzeichenSearchPanel(final By by) {
        this.by = by;
        initComponents();
        configureComponent();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTitle() {
        switch (by) {
            case VERTRAG: {
                return "Suche Flurstücke nach Aktenzeichen (Vertrag)";
            }
            case MIPA: {
                return "Suche Flurstücke nach Aktenzeichen (Vermietung/Verpachtung)";
            }
            default: {
                return "Suche Flurstücke nach Aktenzeichen";
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Icon getIcon() {
        switch (by) {
            case VERTRAG: {
                return ICON_VERTRAG;
            }
            case MIPA: {
                return ICON_MIPA;
            }
            default: {
                return null;
            }
        }
    }

    /**
     * This method sets all startup configuration of this component.
     */
    private void configureComponent() {
        setSearchActive(false);
        tblAktenzeichen.setModel(tableModel);
        // ToDo funktioniert nicht
        // tblAktenzeichen.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblAktenzeichen.addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(final MouseEvent e) {
                    final Object source = e.getSource();
                    if (e.getClickCount() > 1) {
                        final FlurstueckSchluesselCustomBean key = tableModel.getFlurstueckSchluesselAtRow(
                                ((JXTable)tblAktenzeichen).convertRowIndexToModel(tblAktenzeichen.getSelectedRow()));
                        if (key != null) {
                            LagisBroker.getInstance().loadFlurstueck(key);
                        }
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                }

                @Override
                public void mouseEntered(final MouseEvent e) {
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                }
            });
        tblAktenzeichen.setDefaultRenderer(
            FlurstueckSchluesselCustomBean.class,
            new FlurstueckSchluesselCellRenderer());
        ((JXTable)tblAktenzeichen).getColumnModel().getColumn(0).setPreferredWidth(50);
        ((JXTable)tblAktenzeichen).getColumnModel().getColumn(0).setMaxWidth(50);
        ((JXTable)tblAktenzeichen).getColumnModel().getColumn(0).setMinWidth(50);
        // ((JXTable) tblAktenzeichen).getColumnModel().getColumn(1).setResizable(true);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        panStatus = new javax.swing.JPanel();
        pbrSearchProgress = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        lblAnzahl = new javax.swing.JLabel();
        tfdAktenzeichen = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAktenzeichen = new JXTable();
        btnCancel = new javax.swing.JButton();

        pbrSearchProgress.setBorder(null);
        pbrSearchProgress.setBorderPainted(false);

        lblAnzahl.setText("Anzahl:");
        lblAnzahl.setMaximumSize(new java.awt.Dimension(47, 20));
        lblAnzahl.setMinimumSize(new java.awt.Dimension(47, 20));
        lblAnzahl.setPreferredSize(new java.awt.Dimension(47, 22));

        final javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                jPanel1Layout.createSequentialGroup().addGap(6, 6, 6).addComponent(
                    lblAnzahl,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    68,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(44, Short.MAX_VALUE)));
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                jPanel1Layout.createSequentialGroup().addComponent(
                    lblAnzahl,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        final javax.swing.GroupLayout panStatusLayout = new javax.swing.GroupLayout(panStatus);
        panStatus.setLayout(panStatusLayout);
        panStatusLayout.setHorizontalGroup(
            panStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panStatusLayout.createSequentialGroup().addComponent(
                    jPanel1,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(265, Short.MAX_VALUE)).addGroup(
                panStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                    javax.swing.GroupLayout.Alignment.TRAILING,
                    panStatusLayout.createSequentialGroup().addContainerGap(228, Short.MAX_VALUE).addComponent(
                        pbrSearchProgress,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        155,
                        javax.swing.GroupLayout.PREFERRED_SIZE))));
        panStatusLayout.setVerticalGroup(
            panStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                jPanel1,
                javax.swing.GroupLayout.PREFERRED_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(
                panStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                    panStatusLayout.createSequentialGroup().addGap(2, 2, 2).addComponent(
                        pbrSearchProgress,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        14,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(18, Short.MAX_VALUE))));

        tfdAktenzeichen.setMinimumSize(new java.awt.Dimension(150, 27));
        tfdAktenzeichen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    tfdAktenzeichenActionPerformed(evt);
                }
            });

        jLabel1.setText("Aktenzeichen:");

        btnSearch.setText("Start");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnSearchActionPerformed(evt);
                }
            });

        jLabel2.setText("Ergebnis: ");

        tblAktenzeichen.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] { "Art", "Flurstück" }));
        jScrollPane1.setViewportView(tblAktenzeichen);
        if (tblAktenzeichen.getColumnModel().getColumnCount() > 0) {
            tblAktenzeichen.getColumnModel().getColumn(0).setMinWidth(50);
            tblAktenzeichen.getColumnModel().getColumn(0).setPreferredWidth(50);
            tblAktenzeichen.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        btnCancel.setText("Abbrechen");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCancelActionPerformed(evt);
                }
            });

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        jScrollPane1,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        383,
                        Short.MAX_VALUE).addGroup(
                        javax.swing.GroupLayout.Alignment.TRAILING,
                        layout.createSequentialGroup().addComponent(
                            btnSearch,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            65,
                            javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                            btnCancel,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            91,
                            javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(
                        layout.createSequentialGroup().addComponent(jLabel1).addGap(6, 6, 6).addComponent(
                            tfdAktenzeichen,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            287,
                            Short.MAX_VALUE)).addComponent(
                        jSeparator1,
                        javax.swing.GroupLayout.Alignment.TRAILING,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        383,
                        Short.MAX_VALUE).addComponent(jLabel2).addComponent(
                        panStatus,
                        javax.swing.GroupLayout.Alignment.TRAILING,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addContainerGap()));

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] { btnCancel, btnSearch });

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel1)
                                .addComponent(
                                    tfdAktenzeichen,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(btnSearch)
                                .addComponent(btnCancel)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    jSeparator1,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    9,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel2).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    jScrollPane1,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    188,
                    Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panStatus,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    22,
                    javax.swing.GroupLayout.PREFERRED_SIZE)));
    } // </editor-fold>//GEN-END:initComponents

    /**
     * This method is called when the search is activated.
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnSearchActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnSearchActionPerformed
        setSearchActive(true);
        currentSearcher = new FlurstueckSchluesselSearcher(tfdAktenzeichen.getText().trim());
        LagisBroker.getInstance().execute(currentSearcher);
    }                                                                             //GEN-LAST:event_btnSearchActionPerformed

    /**
     * This method is called when the current active search is canceled.
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCancelActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCancelActionPerformed
        setSearchActive(false);
        currentSearcher.cancel(true);
    }                                                                             //GEN-LAST:event_btnCancelActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tfdAktenzeichenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_tfdAktenzeichenActionPerformed
        setSearchActive(true);
        currentSearcher = new FlurstueckSchluesselSearcher(tfdAktenzeichen.getText().trim());
        LagisBroker.getInstance().execute(currentSearcher);
    }                                                                                   //GEN-LAST:event_tfdAktenzeichenActionPerformed

    /**
     * The method configures the GUI when the search is started or canceled. The method for example starts and stops the
     * progress bar or controls the result count
     *
     * @param  isSearchActive  the parameter specifies whether the search is activated or canceled
     */
    private void setSearchActive(final boolean isSearchActive) {
        tableModel.removeAllFlurstueckSchluessel();
        pbrSearchProgress.setIndeterminate(isSearchActive);
        btnSearch.setEnabled(!isSearchActive);
        btnCancel.setEnabled(isSearchActive);
        tfdAktenzeichen.setEditable(!isSearchActive);
        setResultCount(-1);
        // ToDo Focus bei enter auslösen
        if (isSearchActive) {
            btnCancel.requestFocusInWindow();
        } else {
            btnSearch.requestFocusInWindow();
        }
    }

    /**
     * the Method which handles the display of the search result count.
     *
     * @param  count  if the count is less than 0 the result count will not be displayed. Otherwise the result of the
     *                search will be prompted in the status bar
     */
    private void setResultCount(final int count) {
        if (count < 0) {
            lblAnzahl.setText(ANZAHL);
        } else {
            lblAnzahl.setText(ANZAHL + count);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * The Swing Worker which handles the search and updates the GUI according to the results.
     *
     * @version  $Revision$, $Date$
     */
    class FlurstueckSchluesselSearcher extends SwingWorker<Collection<FlurstueckSchluesselCustomBean>, Void> {

        //~ Instance fields ----------------------------------------------------

        /** the aktenzeichen after which should be searched. */
        private final String aktenzeichenSearchPattern;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new FlurstueckSchluesselSearcher object.
         *
         * @param  aktenzeichenSearchPattern  DOCUMENT ME!
         */
        public FlurstueckSchluesselSearcher(final String aktenzeichenSearchPattern) {
            this.aktenzeichenSearchPattern = aktenzeichenSearchPattern;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * This method does the search.
         *
         * @return  the set of founded FlurstueckSchluesselCustomBean
         *
         * @throws  Exception  java.lang.Exception
         */
        @Override
        protected Collection<FlurstueckSchluesselCustomBean> doInBackground() throws Exception {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Suche nach Flurstücken mit dem Aktenzeichen: " + aktenzeichenSearchPattern);
            }

            final Collection<FlurstueckSchluesselCustomBean> result;
            switch (by) {
                case VERTRAG: {
                    result = LagisBroker.getInstance()
                                .getFlurstueckSchluesselByVertragAktenzeichen(aktenzeichenSearchPattern);
                }
                break;
                case MIPA: {
                    result = LagisBroker.getInstance()
                                .getFlurstueckSchluesselByMipaAktenzeichen(aktenzeichenSearchPattern);
                }
                break;
                default: {
                    return new HashSet<>();
                }
            }

            if ((result != null) && (result.size() > 0)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Suche brachte ein Ergebnis. Anzahl FlurstueckSchluessel: " + result.size());
                }
                return result;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Suche brachte kein Ergebnis");
                }
                return new HashSet<>();
            }
        }

        /**
         * After the search is finished this method is called to refresh the GUI with the results.
         */
        @Override
        protected void done() {
            try {
                setSearchActive(false);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("FlurstueckSchluesselSearcher done()");
                }
                // lstResults.setModel(new DefaultUniqueListModel(get()));
                tableModel.refreshTableModel(get());
                setResultCount(tableModel.getFlurstueckSchluesselCount());
            } catch (Exception ex) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Fehler bei der Suche nach Flurstücken mit dem Aktenzeichen: "
                                + aktenzeichenSearchPattern,
                        ex);
                }
            }
        }
    }
}
