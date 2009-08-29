/*
 * NKFPanel.java
 *
 * Created on 16. März 2007, 11:58
 */
package de.cismet.lagis.gui.panels;

import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.models.NKFOverviewTableModel;
import de.cismet.lagis.thread.BackgroundUpdateThread;
import de.cismet.lagis.widget.AbstractWidget;
import de.cismet.lagisEE.bean.Exception.ActionNotSuccessfulException;
import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.lagisEE.entity.core.Nutzung;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Highlighter;
//import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.SortOrder;

/**
 *
 * @author  Puhl
 */
public class NKFOverviewPanel extends AbstractWidget implements FlurstueckChangeListener {

    private Flurstueck currentFlurstueck;
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private static final String WIDGET_NAME = "NKF Übersicht";
    private NKFOverviewTableModel tableModel = new NKFOverviewTableModel();
    private BackgroundUpdateThread<Flurstueck> updateThread;
    
    private Icon icoHistoricIcon = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/nutzung/history.png"));
    private Icon icoHistoricIconDummy = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/nutzung/emptyDummy22.png"));

    /** Creates new form NKFPanel */
    public NKFOverviewPanel() {
        setIsCoreWidget(true);
        initComponents();
        tSummeNutzungen.setModel(tableModel);
        JComboBox box = new JComboBox();
        //HighlighterPipeline hPipline = new HighlighterPipeline(new Highlighter[]{LagisBroker.ALTERNATE_ROW_HIGHLIGHTER});
        ((JXTable) tSummeNutzungen).setHighlighters(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER);
        ((JXTable) tSummeNutzungen).setSortOrder(0, SortOrder.ASCENDING);
        ((JXTable) tSummeNutzungen).packAll();
        configBackgroundThread();        
        btnBuchen.setEnabled(false);
    }

    private void configBackgroundThread() {
        updateThread = new BackgroundUpdateThread<Flurstueck>() {

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
                    tableModel.refreshModel(getCurrentObject().getNutzungen());
                    updateStilleReservenBetrag(getCurrentObject().getNutzungen());
                    if (isUpdateAvailable()) {
                        cleanup();
                        return;
                    }
                    LagisBroker.getInstance().flurstueckChangeFinished(NKFOverviewPanel.this);
                } catch (Exception ex) {
                    log.error("Fehler im refresh thread: ", ex);
                    LagisBroker.getInstance().flurstueckChangeFinished(NKFOverviewPanel.this);
                }
            }
            protected void cleanup() {
            }
        };
        updateThread.setPriority(Thread.NORM_PRIORITY);
        updateThread.start();
    }
    //private Thread panelRefresherThread;
    public synchronized void flurstueckChanged(final Flurstueck newFlurstueck) {
        try {
            log.info("FlurstueckChanged");
            currentFlurstueck = newFlurstueck;
            updateThread.notifyThread(currentFlurstueck);
        } catch (Exception ex) {
            log.error("Fehler beim Flurstückswechsel: ", ex);
            LagisBroker.getInstance().flurstueckChangeFinished(NKFOverviewPanel.this);
        }
    }

    public void setComponentEditable(boolean isEditable) {
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
            String stilleReserven = lblStilleReservenBetrag.getText();
            if (stilleReserven != null) {
                try {
                    Number amount = LagisBroker.getCurrencyFormatter().parse(stilleReserven);
                    if (amount.doubleValue() != 0.0 && !tableModel.containsHistoricNutzung()) {
                        btnBuchen.setEnabled(isEditable);
                    }
                } catch (Exception silent) {

                }
            } else {
                btnBuchen.setEnabled(isEditable);
            }
        }
        log.debug("NKFOverview --> setComponentEditable finished");
    }

    public synchronized void clearComponent() {
        tableModel.refreshModel(new Vector<Nutzung>());
        updateStilleReservenBetrag(null);
        btnBuchen.setEnabled(false);
    }

    public synchronized void refresh(Object refreshObject) {
        log.debug("Refresh NKFPanel");
        if (refreshObject != null && refreshObject instanceof Vector) {
            tableModel.refreshModel((Vector<Nutzung>) refreshObject);
            updateStilleReservenBetrag((Vector<Nutzung>) refreshObject);
        }
    }

    //TODO better in NKFOVERVIEWTABLEMODEL ? YES doppeltgemoppelt
    private synchronized void updateStilleReservenBetrag(Collection<Nutzung> nutzungen) {
        boolean containsHistoricNutzung = false;
        if (nutzungen != null) {
            Iterator<Nutzung> it = nutzungen.iterator();
            double stilleReservenSumme = 0.0;
            while (it.hasNext()) {
                Nutzung currentNutzung = it.next();
                if (tableModel.hasNutzungSuccessor(currentNutzung)) {
                    log.debug("Nutzung hat einen Nachfolger und wird für Stille Reserve nicht berücksichtigt");
                    continue;
                } else {
                    log.debug("Nutzung hat keinen Nachfolger --> wird für Stille Reserve berücksichtigt");
                }
                if (currentNutzung.getStilleReserve() != null) {
                    stilleReservenSumme += currentNutzung.getStilleReserve();
                }
                if (currentNutzung.getGueltigbis() != null) {
                    containsHistoricNutzung = true;                    
                }
            }
//            if (containsHistoricNutzung) {
//                lblStilleReservenBetrag.setText("-/-");
//                btnBuchen.setEnabled(false);
//            } else {
            lblStilleReservenBetrag.setText(LagisBroker.getCurrencyFormatter().format(stilleReservenSumme));
            if (stilleReservenSumme != 0.0 && LagisBroker.getInstance().isInEditMode()) {
                if (!containsHistoricNutzung) {
                    btnBuchen.setEnabled(true);                    
                } else {
                    btnBuchen.setEnabled(false);
                }
            } else {
                btnBuchen.setEnabled(false);
            }
//            }
            if(containsHistoricNutzung){
                lblHistoricIcon.setIcon(icoHistoricIcon);
            } else {
                lblHistoricIcon.setIcon(icoHistoricIconDummy);
            }
        } else {
            lblStilleReservenBetrag.setText(LagisBroker.getCurrencyFormatter().format(0.0));
            btnBuchen.setEnabled(false);
        }        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tSummeNutzungen = new JXTable();
        lblStilleReserven = new javax.swing.JLabel();
        lblStilleReservenBetrag = new javax.swing.JLabel();
        btnBuchen = new javax.swing.JButton();
        lblHistoricIcon = new javax.swing.JLabel();

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tSummeNutzungen.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        tSummeNutzungen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        jScrollPane1.setViewportView(tSummeNutzungen);

        lblStilleReserven.setText("Stille Reserven:");

        lblStilleReservenBetrag.setText("0,0 €");

        btnBuchen.setText("Buchen");
        btnBuchen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuchenActionPerformed(evt);
            }
        });

        lblHistoricIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/nutzung/emptyDummy22.png"))); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, btnBuchen, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, lblStilleReserven, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblHistoricIcon)
                            .add(lblStilleReservenBetrag))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblStilleReservenBetrag)
                    .add(lblStilleReserven))
                .add(5, 5, 5)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnBuchen)
                    .add(lblHistoricIcon))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    private void btnBuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuchenActionPerformed
        // TODO add your handling code here:
        int answer = JOptionPane.showConfirmDialog(LagisBroker.getInstance().getParentComponent(), "Wollen Sie alle Stillen Reserven des Flurstücks buchen?", "Stille Reserven buchen", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            try {
                LagisBroker.getInstance().acceptChanges();
                if (LagisBroker.getInstance().isInEditMode()) {
                    log.warn("Stille Reserven konnten nicht gebucht werden, immernoch im Editmodus");
                    JOptionPane.showMessageDialog(LagisBroker.getInstance().getParentComponent(), "Es war nicht möglich aus dem Editiermodus herauszuwechseln.", "Stille Reserven", JOptionPane.ERROR_MESSAGE);
                } else {
                    //TODO Locking problem
                    EJBroker.getInstance().bookNutzungenForFlurstueck(currentFlurstueck.getFlurstueckSchluessel(), LagisBroker.getInstance().getAccountName());
                }
            //EJBroker.getInstance().bookNutzungenForFlurstueck(currentFlurstueck.getFlurstueckSchluessel());
            } catch (Exception ex) {
                //TODO ActionNotSuccessfull Exception
                final StringBuffer resultString = new StringBuffer("Es war nicht möglich die Stillen Reserven des Flurstücks zu buchen. Fehler: \n");
                if (ex instanceof ActionNotSuccessfulException) {
                    ActionNotSuccessfulException reason = (ActionNotSuccessfulException) ex;
                    if (reason.hasNestedExceptions()) {
                        log.error("Nested Rename Exceptions: ", reason.getNestedExceptions());
                    }
                    resultString.append(reason.getMessage());
                } else {
                    log.error("Unbekannter Fehler: ", ex);
                    resultString.append("Unbekannter Fehler bitte wenden Sie sich an Ihren Systemadministrator");
                }
                JOptionPane.showMessageDialog(LagisBroker.getInstance().getParentComponent(), resultString.toString(), "Stille Reserven", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnBuchenActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuchen;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblHistoricIcon;
    private javax.swing.JLabel lblStilleReserven;
    private javax.swing.JLabel lblStilleReservenBetrag;
    private javax.swing.JTable tSummeNutzungen;
    // End of variables declaration//GEN-END:variables
    public String getWidgetName() {
        return WIDGET_NAME;
    }

    

    //TODO USE
    public Icon getWidgetIcon() {
        return null;
    }
}
