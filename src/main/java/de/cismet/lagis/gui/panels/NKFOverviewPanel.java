/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * NKFPanel.java
 *
 * Created on 16. März 2007, 11:58
 */
package de.cismet.lagis.gui.panels;

import org.jdesktop.swingx.JXTable;
//import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.SortOrder;

import org.openide.util.Exceptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.lagis.NutzungCustomBean;

import de.cismet.lagis.Exception.ActionNotSuccessfulException;
import de.cismet.lagis.Exception.IllegalNutzungStateException;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.gui.tables.NKFOverviewTable;
import de.cismet.lagis.gui.tables.NKFTable;

import de.cismet.lagis.interfaces.FlurstueckChangeListener;

import de.cismet.lagis.models.NKFOverviewTableModel;

import de.cismet.lagis.thread.BackgroundUpdateThread;

import de.cismet.lagis.util.NutzungsContainer;

import de.cismet.lagis.widget.AbstractWidget;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class NKFOverviewPanel extends AbstractWidget implements FlurstueckChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final String WIDGET_NAME = "NKF Übersicht";
    private static final NKFOverviewPanel instance = new NKFOverviewPanel();
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NKFOverviewPanel.class);

    //~ Instance fields --------------------------------------------------------

    private FlurstueckCustomBean currentFlurstueck;
    private NKFOverviewTableModel tableModel = new NKFOverviewTableModel();
    private BackgroundUpdateThread<FlurstueckCustomBean> updateThread;
    private Icon icoHistoricIcon = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/nutzung/history.png"));
    private Icon icoHistoricIconDummy = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/nutzung/emptyDummy22.png"));
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuchen;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblHistoricIcon;
    private javax.swing.JLabel lblStilleReserven;
    private javax.swing.JLabel lblStilleReservenBetrag;
    private javax.swing.JTable tSummeNutzungen;
    private javax.swing.JToggleButton tbtnSort;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form NKFPanel.
     */
    private NKFOverviewPanel() {
        setIsCoreWidget(true);
        initComponents();
        tSummeNutzungen.setModel(tableModel);
        final JComboBox box = new JComboBox();
        // HighlighterPipeline hPipline = new HighlighterPipeline(new
        // Highlighter[]{LagisBroker.ALTERNATE_ROW_HIGHLIGHTER});
        ((JXTable)tSummeNutzungen).setHighlighters(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER);
        ((JXTable)tSummeNutzungen).setSortOrder(0, SortOrder.ASCENDING);
        ((JXTable)tSummeNutzungen).packAll();
        ((NKFOverviewTable)tSummeNutzungen).setSortButton(tbtnSort);
        configBackgroundThread();
        btnBuchen.setEnabled(false);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JTable getSummeNutzungenTable() {
        return tSummeNutzungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static NKFOverviewPanel getInstance() {
        return instance;
    }

    /**
     * DOCUMENT ME!
     */
    private void configBackgroundThread() {
        updateThread = new BackgroundUpdateThread<FlurstueckCustomBean>() {

                @Override
                protected void update() {
                    try {
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        clearComponent();
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        tableModel.setCurrentDate(null);
                        tableModel.refreshModel(getCurrentObject().getNutzungen());
                        updateStilleReservenBetrag();
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        LagisBroker.getInstance().flurstueckChangeFinished(NKFOverviewPanel.this);
                    } catch (Exception ex) {
                        LOG.error("Fehler im refresh thread: ", ex);
                        LagisBroker.getInstance().flurstueckChangeFinished(NKFOverviewPanel.this);
                    }
                }

                @Override
                protected void cleanup() {
                }
            };
        updateThread.setPriority(Thread.NORM_PRIORITY);
        updateThread.start();
    }
    // private Thread panelRefresherThread;

    @Override
    public synchronized void flurstueckChanged(final FlurstueckCustomBean newFlurstueck) {
        try {
            LOG.info("FlurstueckChanged");
            currentFlurstueck = newFlurstueck;
            updateThread.notifyThread(currentFlurstueck);
        } catch (Exception ex) {
            LOG.error("Fehler beim Flurstückswechsel: ", ex);
            LagisBroker.getInstance().flurstueckChangeFinished(NKFOverviewPanel.this);
        }
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
//        HighlighterPipeline pipeline = ((JXTable)tSummeNutzungen).getHighlighters();
//        if(LagisBroker.getInstance().isCurrentFlurstueckLockedByUser()){
//            pipeline.removeHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_DEFAULT);
//            pipeline.removeHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_EDIT);
//            pipeline.addHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_,false);
//        } else if(isEditable){
//        pipeline.removeHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_DEFAULT);
//        pipeline.addHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_EDIT,false);
//        } else {
//        pipeline.removeHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_EDIT);
//        pipeline.addHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_DEFAULT,false);
//        }
        if (isEditable) {
            final String stilleReserven = lblStilleReservenBetrag.getText();
            if (stilleReserven != null) {
                try {
                    final Number amount = LagisBroker.getCurrencyFormatter().parse(stilleReserven);
//ToDo NKF
                    // if (amount.doubleValue() != 0.0 && !tableModel.containsHistoricNutzung()) {
//                        btnBuchen.setEnabled(isEditable);
//                    }
                } catch (Exception silent) {
                }
            } else {
                btnBuchen.setEnabled(isEditable);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("NKFOverview --> setComponentEditable finished");
        }
    }

    @Override
    public synchronized void clearComponent() {
        tableModel.setCurrentDate(null);
        tableModel.refreshModel(new ArrayList<NutzungCustomBean>());
        updateStilleReservenBetrag();
        btnBuchen.setEnabled(false);
    }

    @Override
    public synchronized void refresh(final Object refreshObject) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Refresh NKFPanel");
        }
        if ((refreshObject != null) && (refreshObject instanceof NutzungsContainer)) {
            final NutzungsContainer container = (NutzungsContainer)refreshObject;
            tableModel.setCurrentDate(container.getCurrentDate());
            tableModel.refreshModel(container.getNutzungen());
            updateStilleReservenBetrag();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tSummeNutzungen = new NKFOverviewTable();
        jPanel1 = new javax.swing.JPanel();
        lblStilleReserven = new javax.swing.JLabel();
        lblStilleReservenBetrag = new javax.swing.JLabel();
        btnBuchen = new javax.swing.JButton();
        lblHistoricIcon = new javax.swing.JLabel();
        tbtnSort = new javax.swing.JToggleButton();

        setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tSummeNutzungen.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        tSummeNutzungen.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    { null, null },
                    { null, null },
                    { null, null },
                    { null, null },
                    { null, null }
                },
                new String[] { "Title 1", "Title 2" }));
        jScrollPane1.setViewportView(tSummeNutzungen);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 50;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jScrollPane1, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        lblStilleReserven.setText("Stille Reserven:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(lblStilleReserven, gridBagConstraints);

        lblStilleReservenBetrag.setText("0,0 €");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        jPanel1.add(lblStilleReservenBetrag, gridBagConstraints);

        btnBuchen.setText("Buchen");
        btnBuchen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnBuchenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel1.add(btnBuchen, gridBagConstraints);

        lblHistoricIcon.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/nutzung/emptyDummy22.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        jPanel1.add(lblHistoricIcon, gridBagConstraints);

        tbtnSort.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/sort.png")));          // NOI18N
        tbtnSort.setBorderPainted(false);
        tbtnSort.setContentAreaFilled(false);
        tbtnSort.setMaximumSize(new java.awt.Dimension(25, 25));
        tbtnSort.setMinimumSize(new java.awt.Dimension(25, 25));
        tbtnSort.setPreferredSize(new java.awt.Dimension(25, 25));
        tbtnSort.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/sort_selected.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel1.add(tbtnSort, gridBagConstraints);
        tbtnSort.addItemListener(((NKFOverviewTable)tSummeNutzungen).getSortItemListener());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        add(jPanel2, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents
    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBuchenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnBuchenActionPerformed
        // TODO add your handling code here:
        final int answer = JOptionPane.showConfirmDialog(LagisBroker.getInstance().getParentComponent(),
                "Wollen Sie alle Stillen Reserven des Flurstücks buchen?",
                "Stille Reserven buchen",
                JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            try {
                LagisBroker.getInstance().acceptChanges();
                if (LagisBroker.getInstance().isInEditMode()) {
                    LOG.warn("Stille Reserven konnten nicht gebucht werden, immernoch im Editmodus");
                    JOptionPane.showMessageDialog(LagisBroker.getInstance().getParentComponent(),
                        "Es war nicht möglich aus dem Editiermodus herauszuwechseln.",
                        "Stille Reserven",
                        JOptionPane.ERROR_MESSAGE);
                } else {
                    // TODO Locking problem
                    CidsBroker.getInstance()
                            .bookNutzungenForFlurstueck(currentFlurstueck.getFlurstueckSchluessel(),
                                LagisBroker.getInstance().getAccountName());
                }
                // CidsBroker.getInstance().bookNutzungenForFlurstueck(currentFlurstueck.getFlurstueckSchluessel());
            } catch (Exception ex) {
                // TODO ActionNotSuccessfull Exception
                final StringBuffer resultString = new StringBuffer(
                        "Es war nicht möglich die Stillen Reserven des Flurstücks zu buchen. Fehler: \n");
                if (ex instanceof ActionNotSuccessfulException) {
                    final ActionNotSuccessfulException reason = (ActionNotSuccessfulException)ex;
                    if (reason.hasNestedExceptions()) {
                        LOG.error("Nested Rename Exceptions: ", reason.getNestedExceptions());
                    }
                    resultString.append(reason.getMessage());
                } else {
                    LOG.error("Unbekannter Fehler: ", ex);
                    resultString.append("Unbekannter Fehler bitte wenden Sie sich an Ihren Systemadministrator");
                }
                JOptionPane.showMessageDialog(LagisBroker.getInstance().getParentComponent(),
                    resultString.toString(),
                    "Stille Reserven",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    } //GEN-LAST:event_btnBuchenActionPerformed

    @Override
    public String getWidgetName() {
        return WIDGET_NAME;
    }

    // TODO USE
    @Override
    public Icon getWidgetIcon() {
        return null;
    }

    /**
     * DOCUMENT ME!
     */
    private void updateStilleReservenBetrag() {
        final double stilleReserve = tableModel.getStilleReserve();
        lblStilleReservenBetrag.setText(LagisBroker.getCurrencyFormatter().format(stilleReserve));
        if ((stilleReserve > 0.0) && LagisBroker.getInstance().isInEditMode()) {
//            btnBuchen.setEnabled(true);
        }
        if (tableModel.getCurrentDate() != null) {
            lblHistoricIcon.setIcon(icoHistoricIcon);
        } else {
            lblHistoricIcon.setIcon(icoHistoricIconDummy);
        }
    }
}
