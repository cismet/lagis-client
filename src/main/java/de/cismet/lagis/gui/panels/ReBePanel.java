/*
 * RechtenDetailPanel.java
 *
 * Created on 16. März 2007, 12:05
 */
package de.cismet.lagis.gui.panels;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.editor.DateEditor;
import de.cismet.lagis.interfaces.FeatureSelectionChangedListener;
import de.cismet.lagis.renderer.DateRenderer;
import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.FlurstueckSaver;
import de.cismet.lagis.interfaces.GeometrySlotProvider;
import de.cismet.lagis.models.ReBeTableModel;
import de.cismet.lagis.thread.BackgroundUpdateThread;
import de.cismet.lagis.utillity.GeometrySlotInformation;
import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.widget.AbstractWidget;
import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.lagisEE.entity.core.ReBe;
import de.cismet.lagisEE.entity.core.hardwired.FlurstueckArt;
import de.cismet.lagisEE.entity.core.hardwired.ReBeArt;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.SortOrder;

/**
 *
 * @author  Puhl
 */
public class ReBePanel extends AbstractWidget implements MouseListener, FlurstueckChangeListener, GeometrySlotProvider, FlurstueckSaver, FeatureSelectionChangedListener, ListSelectionListener {

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Flurstueck currentFlurstueck = null;
    private ReBeTableModel tableModel = new ReBeTableModel();
    private static final String WIDGET_NAME = "Rechte & Belastungen Panel";
    private static final String PROVIDER_NAME = "ReBe";
    private boolean isInEditMode = false;
    private BackgroundUpdateThread<Flurstueck> updateThread;
    //private ReBe currentSelectedRebe = null;
    private boolean isInAbteilungIXModus = false;

    /** Creates new form RechtenDetailPanel */
    public ReBePanel() {
        setIsCoreWidget(true);
        initComponents();
        btnRemoveReBe.setEnabled(false);
        configureTable();
        configBackgroundThread();
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
                    FlurstueckArt flurstueckArt = getCurrentObject().getFlurstueckSchluessel().getFlurstueckArt();
                    if (flurstueckArt != null && flurstueckArt.getBezeichnung().equals(FlurstueckArt.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                        log.debug("Flurstück ist nicht Abteilung IX");
                        isInAbteilungIXModus = false;
                        tableModel.setIsReBeKindSwitchAllowed(true);
                    } else {
                        log.debug("Flurstück ist Abteilung IX");
                        isInAbteilungIXModus = true;
                        tableModel.setIsReBeKindSwitchAllowed(false);
                    }

                    tableModel.refreshTableModel(getCurrentObject().getRechteUndBelastungen());
                    if (isUpdateAvailable()) {
                        cleanup();
                        return;
                    }
                    EventQueue.invokeLater(new Runnable() {

                        public void run() {
                            //LagisBroker.getInstance().getMappingComponent().getFeatureCollection().addFeatures(tableModel.getAllReBeFeatures());                             
                            Vector<Feature> features = tableModel.getAllReBeFeatures();
                            if (features != null) {
                                for (Feature currentFeature : features) {
                                    if (currentFeature != null) {
                                        if (isWidgetReadOnly()) {
                                            ((ReBe)currentFeature).setModifiable(false);
                                        }
                                        LagisBroker.getInstance().getMappingComponent().getFeatureCollection().addFeature(currentFeature);
                                    }
                                }
                            }
                        }
                    });
                    if (isUpdateAvailable()) {
                        cleanup();
                        return;
                    }
                    LagisBroker.getInstance().flurstueckChangeFinished(ReBePanel.this);
                } catch (Exception ex) {
                    log.error("Fehler im refresh thread: ", ex);
                    LagisBroker.getInstance().flurstueckChangeFinished(ReBePanel.this);
                }
            }

            protected void cleanup() {
            }
        };
        updateThread.setPriority(Thread.NORM_PRIORITY);
        updateThread.start();
    }

    private void configureTable() {
        tReBe.setModel(tableModel);
        Set<ReBeArt> reBeArten = EJBroker.getInstance().getAllRebeArten();
//        //TODO what if null
        if (reBeArten != null) {
            JComboBox cboRebeArt = new JComboBox(new Vector<ReBeArt>(reBeArten));
            tReBe.setDefaultEditor(ReBeArt.class, new DefaultCellEditor(cboRebeArt));
        }
        //tReBe.getDefaultEditor(Boolean.class).addCellEditorListener();
        //TableCellEditor editor = tReBe.getDefaultEditor(Boolean.class);
        JCheckBox cboReBe = new JCheckBox();
        ((JCheckBox) ((JXTable.BooleanEditor) tReBe.getDefaultEditor(Boolean.class)).getComponent()).addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cboBooleanEditorActionPerformed();
            }
        });
        //cboReBe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/belastung.png")));
        //cboReBe.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/recht.png")));
        //Not right
        //cboReBe.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/recht.png")));
        //cboReBe.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/recht.png")));
        //cboReBe.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/belastung.png")));
        //cboReBe.setDisabledSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/recht.png")));
        cboReBe.setIcon(new javax.swing.ImageIcon());
        cboReBe.setSelectedIcon(new javax.swing.ImageIcon());
        cboReBe.setRolloverIcon(new javax.swing.ImageIcon());
        cboReBe.setPressedIcon(new javax.swing.ImageIcon());
        cboReBe.setDisabledIcon(new javax.swing.ImageIcon());
        cboReBe.setDisabledSelectedIcon(new javax.swing.ImageIcon());
        cboReBe.setHorizontalAlignment(SwingConstants.CENTER);
        //tReBe.setDefaultEditor(Boolean.class,new DefaultCellEditor(cboReBe));
        //tReBe.setDefaultRenderer(Boolean.class,new ReBeCboRenderer());
        tReBe.setDefaultEditor(Date.class, new DateEditor());
        tReBe.setDefaultRenderer(Date.class, new DateRenderer());
        tReBe.addMouseListener(this);
        
        HighlightPredicate noGeometryPredicate = new HighlightPredicate() {

           public boolean isHighlighted(Component renderer, ComponentAdapter componentAdapter) {
                int displayedIndex = componentAdapter.row;
                int modelIndex = ((JXTable) tReBe).getFilters().convertRowIndexToModel(displayedIndex);
                ReBe r = tableModel.getReBeAtRow(modelIndex);
                return r != null && r.getGeometry() == null;
            }
        };
        Highlighter noGeometryHighlighter = new ColorHighlighter(noGeometryPredicate, LagisBroker.grey, null);
        //HighlighterPipeline hPipline = new HighlighterPipeline(new Highlighter[]{LagisBroker.ALTERNATE_ROW_HIGHLIGHTER, noGeometryHighlighter});
        ((JXTable) tReBe).setHighlighters(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER,noGeometryHighlighter);
        ((JXTable) tReBe).setSortOrder(0, SortOrder.ASCENDING);


//        //Test
//        DateDocumentModel eintragungDocumentModel = new  DateDocumentModel(){
//            public void assignValue(Date date) {
//                log.debug("Date assinged");
//            }
//        };
//
//        ((JTextField)tReBe.getColumnModel().getColumn(4).getCellEditor()).setDocument(eintragungDocumentModel);
//        Validator valTxtEintragung = new Validator((JTextField)tReBe.getColumnModel().getColumn(1).getCellEditor());
//        valTxtEintragung.reSetValidator((Validatable)eintragungDocumentModel);
        tReBe.getSelectionModel().addListSelectionListener(this);
        ((JXTable) tReBe).packAll();
    }

    //private Thread panelRefresherThread;
    public void flurstueckChanged(final Flurstueck newFlurstueck) {
        try {
            log.info("FlurstueckChanged");
            updateThread.notifyThread(newFlurstueck);
        } catch (Exception ex) {
            log.error("Fehler beim Flurstückswechsel: ", ex);
            LagisBroker.getInstance().flurstueckChangeFinished(ReBePanel.this);
        }
    }

    public void setComponentEditable(boolean isEditable) {
        log.debug("ReBe --> setComponentEditable");
        isInEditMode = isEditable;
        TableCellEditor currentEditor = tReBe.getCellEditor();
        if (currentEditor != null) {
            currentEditor.cancelCellEditing();
        }
        //tReBe.setEnabled();
        if (isEditable && tReBe.getSelectedRow() != -1) {
            btnRemoveReBe.setEnabled(true);
        } else if (!isEditable) {
            btnRemoveReBe.setEnabled(isEditable);
        }

        btnAddReBe.setEnabled(isEditable);
        tableModel.setIsInEditMode(isEditable);
//        HighlighterPipeline pipeline = ((JXTable)tReBe).getHighlighters();
//        if(isEditable){
//        pipeline.removeHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_DEFAULT);
//        pipeline.addHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_EDIT,false);
//        } else {
//        pipeline.removeHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_EDIT);
//        pipeline.addHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_DEFAULT,false);
//        }
        log.debug("ReBe --> setComponentEditable finished");
    }

    public synchronized void clearComponent() {

//tReBe.setModel(new ReBeTableModel());
        tableModel.refreshTableModel(new HashSet());
    }

    //TODO validate the single cell of the tables
    public void refresh(Object refreshObject) {
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tReBe = new JXTable();
        btnAddReBe = new javax.swing.JButton();
        btnRemoveReBe = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        tReBe.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        tReBe.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Recht", "Baulast", "1", "Wegerecht", "12AZ7095", "28.12.03", "", null},
                {"Belastung", "Persönliche Dienstbarkeit", "1", "Leitungsrecht", "12HU9994", "09.05.03", "", null}
            },
            new String [] {
                "Recht/Belastung", "Art", "Nummer", "Beschreibung", "Eintragung AZ", "Eintragung Datum", "Löschung AZ", "Löschung Datum"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tReBe);

        btnAddReBe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/add.png"))); // NOI18N
        btnAddReBe.setBorder(null);
        btnAddReBe.setOpaque(false);
        btnAddReBe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddReBeActionPerformed(evt);
            }
        });

        btnRemoveReBe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/remove.png"))); // NOI18N
        btnRemoveReBe.setBorder(null);
        btnRemoveReBe.setOpaque(false);
        btnRemoveReBe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveReBeActionPerformed(evt);
            }
        });

        jLabel1.setText("Rechte und Belastungen:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 372, Short.MAX_VALUE)
                        .add(btnAddReBe, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnRemoveReBe, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {btnAddReBe, btnRemoveReBe}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(btnRemoveReBe)
                    .add(btnAddReBe, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {btnAddReBe, btnRemoveReBe}, org.jdesktop.layout.GroupLayout.VERTICAL);

    }// </editor-fold>//GEN-END:initComponents
    private void btnRemoveReBeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveReBeActionPerformed
        int currentRow = tReBe.getSelectedRow();
        if (currentRow != -1) {
            //VerwaltungsTableModel currentModel = (VerwaltungsTableModel)tNutzung.getModel();
            tableModel.removeReBe(((JXTable) tReBe).getFilters().convertRowIndexToModel(currentRow));
            tableModel.fireTableDataChanged();
        }
    }//GEN-LAST:event_btnRemoveReBeActionPerformed

    private void btnAddReBeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddReBeActionPerformed
        final ReBe tmpReBe = new ReBe();
        if (isInAbteilungIXModus) {
            tmpReBe.setIstRecht(true);
        }
        tableModel.addReBe(tmpReBe);
        tableModel.fireTableDataChanged();
    }//GEN-LAST:event_btnAddReBeActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddReBe;
    private javax.swing.JButton btnRemoveReBe;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tReBe;
    // End of variables declaration//GEN-END:variables
    public String getWidgetName() {
        return WIDGET_NAME;
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
        Object source = e.getSource();
        if (source instanceof JXTable) {
            log.debug("Mit maus auf ReBeTabelle geklickt");
            int selecetdRow = tReBe.getSelectedRow();
            if (selecetdRow != -1) {
                //currentSelectedRebe = tableModel.getReBeAtRow(selecetdRow);
                if (isInEditMode) {
                    btnRemoveReBe.setEnabled(true);
                }
            } else {
                //currentSelectedRebe = null;
                btnRemoveReBe.setEnabled(false);
            }
        }
    }

    public Vector<GeometrySlotInformation> getSlotInformation() {
        //VerwaltungsTableModel tmp = (VerwaltungsTableModel) tNutzung.getModel();
        Vector<GeometrySlotInformation> result = new Vector<GeometrySlotInformation>();
        if (isWidgetReadOnly()) {
            return result;
        } else {
            int rowCount = tableModel.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                ReBe currentReBe = tableModel.getReBeAtRow(i);
                //Geom geom;
                if (currentReBe.getGeometry() == null) {
                    Object idValue1 = tableModel.getValueAt(i, 0);
                    Object idValue2 = tableModel.getValueAt(i, 3);
                    String identifer;
                    if (idValue1 != null && idValue2 != null) {
                        if (((Boolean) idValue1).booleanValue()) {
                            identifer = "Recht" + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR() + idValue2.toString();
                        } else {
                            identifer = "Belastung" + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR() + idValue2.toString();
                        }
                    } else if (idValue1 != null) {
                        if (((Boolean) idValue1).booleanValue()) {
                            identifer = "Recht" + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR() + "Keine Nummer";
                        } else {
                            identifer = "Belastung" + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR() + "Keine Nummer";
                        }

                    } else if (idValue2 != null) {
                        identifer = "Belastung" + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR() + idValue2.toString();
                    } else {
                        identifer = "Belastung" + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR() + "Kein Nummer";
                    }
                    result.add(new GeometrySlotInformation(getProviderName(), identifer, currentReBe, this));
                }
            }
            return result;
        }
    }

    public String getProviderName() {
        return PROVIDER_NAME;
    }

    public void updateFlurstueckForSaving(Flurstueck flurstueck) {
        Set<ReBe> resBes = flurstueck.getRechteUndBelastungen();
        if (resBes != null) {
            resBes.clear();
            resBes.addAll(tableModel.getResBes());
        } else {
            HashSet newSet = new HashSet();
            newSet.addAll(tableModel.getResBes());
            flurstueck.setRechteUndBelastungen(newSet);
        }
    }

    //TODO multiple Selection
    //HINT If there are problems try to remove/add Listselectionlistener at start/end of Method
    synchronized public void featureSelectionChanged(Collection<Feature> features) {
        if (features.size() == 0) {
            return;
        }
        for (Feature feature : features) {
            if (feature instanceof ReBe) {
                //TODO Refactor Name
                int index = tableModel.getIndexOfReBe((ReBe) feature);
                int displayedIndex = ((JXTable) tReBe).getFilters().convertRowIndexToView(index);
                if (index != -1 && LagisBroker.getInstance().getMappingComponent().getFeatureCollection().isSelected(feature)) {
                    //tReBe.changeSelection(((JXTable)tReBe).getFilters().convertRowIndexToView(index),0,false,false);
                    tReBe.getSelectionModel().addSelectionInterval(displayedIndex, displayedIndex);
                    Rectangle tmp = tReBe.getCellRect(displayedIndex, 0, true);
                    if (tmp != null) {
                        tReBe.scrollRectToVisible(tmp);
                    }
                } else {
                    tReBe.getSelectionModel().removeSelectionInterval(displayedIndex, displayedIndex);
                }
            } else {
                tReBe.clearSelection();
            }
        }
    }

    //TODO WHAT IS IT GOOD FOR
    public void stateChanged(ChangeEvent e) {
    }

    //ToDo multiple Selection
    synchronized public void valueChanged(ListSelectionEvent e) {
        MappingComponent mappingComp = LagisBroker.getInstance().getMappingComponent();
        if (tReBe.getSelectedRow() != -1) {
            if (isInEditMode) {
                btnRemoveReBe.setEnabled(true);
            } else {
                btnRemoveReBe.setEnabled(false);
            }
            int index = ((JXTable) tReBe).getFilters().convertRowIndexToModel(tReBe.getSelectedRow());
            if (index != -1 && tReBe.getSelectedRowCount() <= 1) {
                ReBe selectedReBe = tableModel.getReBeAtRow(index);
                if (selectedReBe.getGeometry() != null && !mappingComp.getFeatureCollection().isSelected(selectedReBe)) {
                    mappingComp.getFeatureCollection().select(selectedReBe);
                }
            }
        } else {
            btnRemoveReBe.setEnabled(false);
            return;
        }
    }

    

    //TODO USE
    public Icon getWidgetIcon() {
        return null;
    }

    public int getStatus() {
        if (tReBe.getCellEditor() != null) {
            validationMessage = "Bitte vollenden Sie alle Änderungen bei den Rechten und Belastungen.";
            return Validatable.ERROR;
        }
        return Validatable.VALID;
    }

    private void cboBooleanEditorActionPerformed() {
        log.debug("rechtCheckboxAction");
        TableCellEditor currentEditor = tReBe.getCellEditor();
        if (currentEditor != null) {
            currentEditor.stopCellEditing();
        }
        for (Feature feature : (Collection<Feature>) LagisBroker.getInstance().getMappingComponent().getFeatureCollection().getSelectedFeatures()) {
            LagisBroker.getInstance().getMappingComponent().getFeatureCollection().reconsiderFeature(feature);
        }
    }
}
