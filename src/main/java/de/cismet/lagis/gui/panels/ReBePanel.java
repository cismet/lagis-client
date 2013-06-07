/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * RechtenDetailPanel.java
 *
 * Created on 16. März 2007, 12:05
 */
package de.cismet.lagis.gui.panels;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.*;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.SortOrder;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.*;

import java.util.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;

import de.cismet.cids.custom.beans.lagis.FlurstueckArtCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.lagis.RebeArtCustomBean;
import de.cismet.cids.custom.beans.lagis.RebeCustomBean;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollection;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.StyledFeatureGroupWrapper;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.editor.DateEditor;

import de.cismet.lagis.gui.copypaste.Copyable;
import de.cismet.lagis.gui.copypaste.Pasteable;
import de.cismet.lagis.gui.tables.ReBeTable;

import de.cismet.lagis.interfaces.FeatureSelectionChangedListener;
import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.FlurstueckSaver;
import de.cismet.lagis.interfaces.GeometrySlotProvider;

import de.cismet.lagis.models.ReBeTableModel;

import de.cismet.lagis.renderer.DateRenderer;

import de.cismet.lagis.thread.BackgroundUpdateThread;

import de.cismet.lagis.util.LagISUtils;
import de.cismet.lagis.util.TableSelectionUtils;

import de.cismet.lagis.utillity.GeometrySlotInformation;

import de.cismet.lagis.validation.Validatable;

import de.cismet.lagis.widget.AbstractWidget;

import de.cismet.lagisEE.entity.basic.BasicEntity;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class ReBePanel extends AbstractWidget implements MouseListener,
    FlurstueckChangeListener,
    GeometrySlotProvider,
    FlurstueckSaver,
    FeatureSelectionChangedListener,
    ListSelectionListener,
    Copyable,
    Pasteable {

    //~ Static fields/initializers ---------------------------------------------

    private static final String WIDGET_NAME = "Rechte & Belastungen Panel";
    public static final String PROVIDER_NAME = "ReBe";

    // Konstanten die zum Setzen von Standardwerten nach Auswahl von
    // DEF_REBE_TRIGGER_ART benötigt werden
    private static final String DEF_REBE_TRIGGER_ART = "Dienstbarkeit";
    private static final String DEF_TARGET_COL = "Nummer";
    private static final String DEF_COL_VALUE = "Abt. II, lfd. Nr. ";

    private static final String COPY_DISPLAY_ICON = "/de/cismet/lagis/ressource/icons/rebe.png";

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private ReBeTableModel tableModel = new ReBeTableModel();
    private boolean isInEditMode = false;
    private BackgroundUpdateThread<FlurstueckCustomBean> updateThread;
    private final Icon copyDisplayIcon;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddReBe;
    private javax.swing.JButton btnRemoveReBe;
    private javax.swing.JButton btnUndo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tReBe;
    private javax.swing.JToggleButton tbtnSort;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RechtenDetailPanel.
     */
    public ReBePanel() {
        this.copyDisplayIcon = new ImageIcon(this.getClass().getResource(COPY_DISPLAY_ICON));
        setIsCoreWidget(true);
        initComponents();
        btnRemoveReBe.setEnabled(false);
        configureTable();
        configBackgroundThread();
    }

    //~ Methods ----------------------------------------------------------------

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
                        final FlurstueckArtCustomBean flurstueckArt = getCurrentObject().getFlurstueckSchluessel()
                                    .getFlurstueckArt();
                        if ((flurstueckArt != null)
                                    && flurstueckArt.getBezeichnung().equals(
                                        FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                            if (log.isDebugEnabled()) {
                                log.debug("Flurstück ist nicht Abteilung IX");
                            }
                            tableModel.setIsReBeKindSwitchAllowed(true);
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("Flurstück ist Abteilung IX");
                            }
                            tableModel.setIsReBeKindSwitchAllowed(false);
                        }

                        tableModel.refreshTableModel(getCurrentObject().getRechteUndBelastungen());
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    // LagisBroker.getInstance().getMappingComponent().getFeatureCollection().addFeatures(tableModel.getAllReBeFeatures());
                                    final ArrayList<Feature> features = tableModel.getAllReBeFeatures();
                                    if (features != null) {
                                        for (final Feature currentFeature : features) {
                                            if (currentFeature != null) {
                                                if (isWidgetReadOnly()) {
                                                    ((RebeCustomBean)currentFeature).setModifiable(false);
                                                }

                                                final StyledFeature sf = new StyledFeatureGroupWrapper(
                                                        (StyledFeature)currentFeature,
                                                        PROVIDER_NAME,
                                                        PROVIDER_NAME);

                                                LagisBroker.getInstance()
                                                        .getMappingComponent()
                                                        .getFeatureCollection()
                                                        .addFeature(sf);
//                                                        .addFeature(currentFeature);
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

                @Override
                protected void cleanup() {
                }
            };
        updateThread.setPriority(Thread.NORM_PRIORITY);
        updateThread.start();
    }

    /**
     * DOCUMENT ME!
     */
    private void configureTable() {
        TableSelectionUtils.crossReferenceModelAndTable(tableModel, (ReBeTable)tReBe);
        final Collection<RebeArtCustomBean> reBeArten = CidsBroker.getInstance().getAllRebeArten();
//        //TODO what if null
        if (reBeArten != null) {
            final JComboBox cboRebeArt = new JComboBox(new Vector<RebeArtCustomBean>(reBeArten));
            tReBe.setDefaultEditor(RebeArtCustomBean.class, new DefaultCellEditor(cboRebeArt));

            cboRebeArt.addItemListener(new ItemListener() {

                    @Override
                    public void itemStateChanged(final ItemEvent e) {
                        handleCboRebeArtItemStateChanged(e);
                    }
                });
        }

        // tReBe.getDefaultEditor(Boolean.class).addCellEditorListener();
        // TableCellEditor editor = tReBe.getDefaultEditor(Boolean.class);
        final JCheckBox cboReBe = new JCheckBox();
        ((JCheckBox)((JXTable.BooleanEditor)tReBe.getDefaultEditor(Boolean.class)).getComponent()).addActionListener(
            new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    cboBooleanEditorActionPerformed();
                }
            });
        cboReBe.setIcon(new javax.swing.ImageIcon());
        cboReBe.setSelectedIcon(new javax.swing.ImageIcon());
        cboReBe.setRolloverIcon(new javax.swing.ImageIcon());
        cboReBe.setPressedIcon(new javax.swing.ImageIcon());
        cboReBe.setDisabledIcon(new javax.swing.ImageIcon());
        cboReBe.setDisabledSelectedIcon(new javax.swing.ImageIcon());
        cboReBe.setHorizontalAlignment(SwingConstants.CENTER);

        tReBe.setDefaultEditor(Date.class, new DateEditor());
        tReBe.setDefaultRenderer(Date.class, new DateRenderer());
        tReBe.addMouseListener(this);

        final HighlightPredicate noGeometryPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedIndex = componentAdapter.row;
                    final int modelIndex = ((JXTable)tReBe).getFilters().convertRowIndexToModel(displayedIndex);
                    final RebeCustomBean r = tableModel.getCidsBeanAtRow(modelIndex);
                    return (r != null) && (r.getGeometry() == null);
                }
            };

        final Highlighter noGeometryHighlighter = new ColorHighlighter(noGeometryPredicate, LagisBroker.grey, null);
        ((JXTable)tReBe).setHighlighters(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER, noGeometryHighlighter);
        ((JXTable)tReBe).setSortOrder(0, SortOrder.ASCENDING);

        tReBe.getSelectionModel().addListSelectionListener(this);
        ((JXTable)tReBe).packAll();
        ((ReBeTable)tReBe).setSortButton(tbtnSort);
        ((ReBeTable)tReBe).setUndoButton(btnUndo);
    }

    /**
     * Umsetzung von Issue 2181: Wird in dem Dropdown-Menü des Feldes "Art" der Eintrag "Dienstbarkeit" ausgewählt, wird
     * als Standard der Wert "Abt II, lfd. Nr." vorgegeben und der Cursor steht direkt dahinter, damit nur noch die
     * eigentliche Nummer eingegeben werden muss. Diese Aktion wird nur dann ausgeführt, wenn noch kein Wert angegeben
     * wurde. Diese Methode wird zur Umsetzung der Logik des enstprechenden ItemListeners von der ComboBox cboRebeArt
     * verwendet.
     *
     * @param  e  event
     */
    private void handleCboRebeArtItemStateChanged(final ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            final String rebeArt = e.getItem().toString();

            if (DEF_REBE_TRIGGER_ART.equals(rebeArt)) {
                final JXTable rebeJXTable = (JXTable)this.tReBe;

                final int colIndex = this.tableModel.findColumn(DEF_TARGET_COL);
                final int rowIndex = this.tReBe.getSelectedRow();

                final String currentValueObj = rebeJXTable.getStringAt(rowIndex, colIndex);

                if ((currentValueObj == null) || currentValueObj.trim().isEmpty()) {
                    rebeJXTable.setValueAt(DEF_COL_VALUE, rowIndex, colIndex);

                    final TableCellEditor cellEditor = tReBe.getCellEditor(rowIndex, colIndex);
                    final Component c = cellEditor.getTableCellEditorComponent(
                            this.tReBe,
                            null,
                            true,
                            rowIndex,
                            colIndex);

                    rebeJXTable.editCellAt(rowIndex, colIndex);

                    final JTextField txtField = (JTextField)c;
                    SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                txtField.requestFocus();
                            }
                        });
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public List<BasicEntity> getCopyData() {
        final List<RebeCustomBean> allReBe = (List<RebeCustomBean>)this.tableModel.getCidsBeans();
        final ArrayList<BasicEntity> result = new ArrayList<BasicEntity>(allReBe.size());

        for (final RebeCustomBean rebe : allReBe) {
            try {
                final RebeCustomBean tmp = RebeCustomBean.createNew();

                final Date dateEintragung = rebe.getDatumEintragung();
                final Date dateLoeschung = rebe.getDatumLoeschung();

                tmp.setDatumEintragung((dateEintragung == null) ? null : (Date)dateEintragung.clone());
                tmp.setDatumLoeschung((dateLoeschung == null) ? null : (Date)dateLoeschung.clone());
                tmp.setBemerkung(rebe.getBemerkung());

                final Geometry geom = rebe.getGeometry();
                if (geom != null) {
                    tmp.setGeometry((Geometry)geom.clone());
                }

                tmp.setEditable(rebe.isEditable());
                tmp.hide(rebe.isHidden());
                tmp.setModifiable(rebe.isModifiable());
                tmp.setNummer(rebe.getNummer());
                tmp.setIstRecht(rebe.getIstRecht());
                tmp.setBeschreibung(rebe.getBeschreibung());

                result.add(tmp);
            } catch (Exception ex) {
                log.error("error creating rebe bean");
            }
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   entity  rebe DOCUMENT ME!
     *
     * @throws  NullPointerException  DOCUMENT ME!
     */
    @Override
    public void paste(final BasicEntity entity) {
        if (entity == null) {
            throw new NullPointerException("Entity must not be null");
        }

        if (entity instanceof RebeCustomBean) {
            final List<RebeCustomBean> residentReBe = (List<RebeCustomBean>)this.tableModel.getCidsBeans();
            if (residentReBe.contains(entity)) {
                log.warn("ReBe " + entity + " does already exist -> ignored");
            } else {
                this.tableModel.addCidsBean((RebeCustomBean)entity);

                final StyledFeatureGroupWrapper wrapper = new StyledFeatureGroupWrapper((StyledFeature)entity,
                        PROVIDER_NAME,
                        PROVIDER_NAME);

                final MappingComponent mc = LagisBroker.getInstance().getMappingComponent();
                final FeatureCollection fc = mc.getFeatureCollection();
                fc.addFeature(wrapper);

                this.tableModel.fireTableDataChanged();
                mc.setGroupLayerVisibility(PROVIDER_NAME, true);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   dataList  rebeList DOCUMENT ME!
     *
     * @throws  NullPointerException  DOCUMENT ME!
     */
    @Override
    public void pasteAll(final List<BasicEntity> dataList) {
        if (dataList == null) {
            throw new NullPointerException("Given list of data items must not be null");
        }

        if (dataList.isEmpty()) {
            return;
        }

        final List<RebeCustomBean> residentReBe = (List<RebeCustomBean>)this.tableModel.getCidsBeans();
        final int rowCountBefore = this.tableModel.getRowCount();

        final MappingComponent mc = LagisBroker.getInstance().getMappingComponent();
        final FeatureCollection fc = mc.getFeatureCollection();

        StyledFeatureGroupWrapper wrapper;
        for (final BasicEntity entity : dataList) {
            if (entity instanceof RebeCustomBean) {
                if (residentReBe.contains(entity)) {
                    log.warn("ReBe " + entity + " does already exist -> ignored");
                } else {
                    this.tableModel.addCidsBean((RebeCustomBean)entity);
                    wrapper = new StyledFeatureGroupWrapper((StyledFeature)entity, PROVIDER_NAME, PROVIDER_NAME);
                    fc.addFeature(wrapper);
                }
            }
        }

        if (rowCountBefore == this.tableModel.getRowCount()) {
            if (log.isDebugEnabled()) {
                log.debug("No ReBe items were added from input list " + dataList);
            }
        } else {
            this.tableModel.fireTableDataChanged();
            mc.setGroupLayerVisibility(PROVIDER_NAME, true);
        }
    }

    @Override
    public void flurstueckChanged(final FlurstueckCustomBean newFlurstueck) {
        try {
            log.info("FlurstueckChanged");
            updateThread.notifyThread(newFlurstueck);
        } catch (Exception ex) {
            log.error("Fehler beim Flurstückswechsel: ", ex);
            LagisBroker.getInstance().flurstueckChangeFinished(ReBePanel.this);
        }
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
        if (log.isDebugEnabled()) {
            log.debug("ReBe --> setComponentEditable");
        }
        isInEditMode = isEditable;
        final TableCellEditor currentEditor = tReBe.getCellEditor();
        if (currentEditor != null) {
            currentEditor.cancelCellEditing();
        }
        // tReBe.setEnabled();
        if (isEditable && (tReBe.getSelectedRow() != -1)) {
            btnRemoveReBe.setEnabled(true);
        } else if (!isEditable) {
            btnRemoveReBe.setEnabled(isEditable);
        }

        btnAddReBe.setEnabled(isEditable);
        tableModel.setInEditMode(isEditable);
        btnUndo.setEnabled(false);
        if (log.isDebugEnabled()) {
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
    }

    @Override
    public synchronized void clearComponent() {
//tReBe.setModel(new ReBeTableModel());
        tableModel.refreshTableModel(new HashSet());
    }

    // TODO validate the single cell of the tables
    @Override
    public void refresh(final Object refreshObject) {
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        tReBe = new ReBeTable();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnRemoveReBe = new javax.swing.JButton();
        btnAddReBe = new javax.swing.JButton();
        tbtnSort = new javax.swing.JToggleButton();
        btnUndo = new javax.swing.JButton();

        tReBe.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        tReBe.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    { "Recht", "Baulast", "1", "Wegerecht", "12AZ7095", "28.12.03", "", null },
                    {
                        "Belastung",
                        "Persönliche Dienstbarkeit",
                        "1",
                        "Leitungsrecht",
                        "12HU9994",
                        "09.05.03",
                        "",
                        null
                    }
                },
                new String[] {
                    "Recht/Belastung",
                    "Art",
                    "Nummer",
                    "Beschreibung",
                    "Eintragung AZ",
                    "Eintragung Datum",
                    "Löschung AZ",
                    "Löschung Datum"
                }) {

                Class[] types = new Class[] {
                        java.lang.String.class,
                        java.lang.String.class,
                        java.lang.String.class,
                        java.lang.String.class,
                        java.lang.String.class,
                        java.lang.String.class,
                        java.lang.String.class,
                        java.lang.String.class
                    };

                @Override
                public Class getColumnClass(final int columnIndex) {
                    return types[columnIndex];
                }
            });
        jScrollPane1.setViewportView(tReBe);

        jLabel1.setText("Rechte und Belastungen:");

        jPanel1.setLayout(new java.awt.GridBagLayout());

        btnRemoveReBe.setAction(((ReBeTable)tReBe).getRemoveAction());
        btnRemoveReBe.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/remove.png"))); // NOI18N
        btnRemoveReBe.setBorder(null);
        btnRemoveReBe.setBorderPainted(false);
        btnRemoveReBe.setMaximumSize(new java.awt.Dimension(25, 25));
        btnRemoveReBe.setMinimumSize(new java.awt.Dimension(25, 25));
        btnRemoveReBe.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 4, 3);
        jPanel1.add(btnRemoveReBe, gridBagConstraints);

        btnAddReBe.setAction(((ReBeTable)tReBe).getAddAction());
        btnAddReBe.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/add.png"))); // NOI18N
        btnAddReBe.setBorder(null);
        btnAddReBe.setBorderPainted(false);
        btnAddReBe.setMaximumSize(new java.awt.Dimension(25, 25));
        btnAddReBe.setMinimumSize(new java.awt.Dimension(25, 25));
        btnAddReBe.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 4, 3);
        jPanel1.add(btnAddReBe, gridBagConstraints);

        tbtnSort.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/sort.png")));          // NOI18N
        tbtnSort.setToolTipText("Sortierung An / Aus");
        tbtnSort.setBorderPainted(false);
        tbtnSort.setContentAreaFilled(false);
        tbtnSort.setMaximumSize(new java.awt.Dimension(25, 25));
        tbtnSort.setMinimumSize(new java.awt.Dimension(25, 25));
        tbtnSort.setPreferredSize(new java.awt.Dimension(25, 25));
        tbtnSort.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/sort_selected.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 4, 3);
        jPanel1.add(tbtnSort, gridBagConstraints);
        tbtnSort.addItemListener(((ReBeTable)tReBe).getSortItemListener());

        btnUndo.setAction(((ReBeTable)tReBe).getUndoAction());
        btnUndo.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/undo.png"))); // NOI18N
        btnUndo.setBorderPainted(false);
        btnUndo.setMaximumSize(new java.awt.Dimension(25, 25));
        btnUndo.setMinimumSize(new java.awt.Dimension(25, 25));
        btnUndo.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 3, 4, 3);
        jPanel1.add(btnUndo, gridBagConstraints);

        final org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().addContainerGap().add(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        layout.createSequentialGroup().add(jLabel1).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE).add(
                            jPanel1,
                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).add(
                        jScrollPane1,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        631,
                        Short.MAX_VALUE)).addContainerGap()));
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().addContainerGap().add(
                    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jLabel1).add(
                        jPanel1,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    jScrollPane1,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    243,
                    Short.MAX_VALUE).addContainerGap()));
    } // </editor-fold>//GEN-END:initComponents

    // End of variables declaration
    @Override
    public String getWidgetName() {
        return WIDGET_NAME;
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        final Object source = e.getSource();
        if (source instanceof JXTable) {
            if (log.isDebugEnabled()) {
                log.debug("Mit maus auf ReBeTabelle geklickt");
            }
            final int selecetdRow = tReBe.getSelectedRow();
            if (selecetdRow != -1) {
                // currentSelectedRebe = tableModel.getReBeAtRow(selecetdRow);
                if (isInEditMode) {
                    btnRemoveReBe.setEnabled(true);
                }
            } else {
                // currentSelectedRebe = null;
                btnRemoveReBe.setEnabled(false);
            }
        }
    }

    @Override
    public Vector<GeometrySlotInformation> getSlotInformation() {
        // VerwaltungsTableModel tmp = (VerwaltungsTableModel) tNutzung.getModel();
        final Vector<GeometrySlotInformation> result = new Vector<GeometrySlotInformation>();
        if (isWidgetReadOnly()) {
            return result;
        } else {
            final int rowCount = tableModel.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                final RebeCustomBean currentReBe = tableModel.getCidsBeanAtRow(i);
                // Geom geom;
                if (currentReBe.getGeometry() == null) {
                    final Object idValue1 = tableModel.getValueAt(i, 0);
                    final Object idValue2 = tableModel.getValueAt(i, 3);
                    String identifer;
                    if ((idValue1 != null) && (idValue2 != null)) {
                        if (((Boolean)idValue1).booleanValue()) {
                            identifer = "Recht" + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR()
                                        + idValue2.toString();
                        } else {
                            identifer = "Belastung" + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR()
                                        + idValue2.toString();
                        }
                    } else if (idValue1 != null) {
                        if (((Boolean)idValue1).booleanValue()) {
                            identifer = "Recht" + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR()
                                        + "Keine Nummer";
                        } else {
                            identifer = "Belastung" + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR()
                                        + "Keine Nummer";
                        }
                    } else if (idValue2 != null) {
                        identifer = "Belastung" + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR()
                                    + idValue2.toString();
                    } else {
                        identifer = "Belastung" + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR()
                                    + "Kein Nummer";
                    }
                    result.add(new GeometrySlotInformation(getProviderName(), identifer, currentReBe, this));
                }
            }
            return result;
        }
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public void updateFlurstueckForSaving(final FlurstueckCustomBean flurstueck) {
        final Collection<RebeCustomBean> resBes = flurstueck.getRechteUndBelastungen();
        if (resBes != null) {
            LagISUtils.makeCollectionContainSameAsOtherCollection(resBes, tableModel.getCidsBeans());
        } else {
            // TODO kann das überhaupt noch passieren seid der Umstellung auf cids ?!
        }
    }

    // TODO multiple Selection
    // HINT If there are problems try to remove/add Listselectionlistener at start/end of Method
    @Override
    public synchronized void featureSelectionChanged(final Collection<Feature> features) {
        if (features.isEmpty()) {
            return;
        }

        for (final Feature feature : features) {
            if (feature instanceof RebeCustomBean) {
                // TODO Refactor Name
                final int index = tableModel.getIndexOfCidsBean((RebeCustomBean)feature);
                final int displayedIndex = ((JXTable)tReBe).getFilters().convertRowIndexToView(index);
                if ((index != -1)
                            && LagisBroker.getInstance().getMappingComponent().getFeatureCollection().isSelected(
                                feature)) {
                    // tReBe.changeSelection(((JXTable)tReBe).getFilters().convertRowIndexToView(index),0,false,false);
                    tReBe.getSelectionModel().addSelectionInterval(displayedIndex, displayedIndex);
                    final Rectangle tmp = tReBe.getCellRect(displayedIndex, 0, true);
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

    // TODO WHAT IS IT GOOD FOR
    @Override
    public void stateChanged(final ChangeEvent e) {
    }

    // ToDo multiple Selection
    @Override
    public synchronized void valueChanged(final ListSelectionEvent e) {
        final MappingComponent mappingComp = LagisBroker.getInstance().getMappingComponent();
        if (tReBe.getSelectedRow() != -1) {
            if (isInEditMode) {
                btnRemoveReBe.setEnabled(true);
            } else {
                btnRemoveReBe.setEnabled(false);
            }
            final int index = ((JXTable)tReBe).getFilters().convertRowIndexToModel(tReBe.getSelectedRow());
            if ((index != -1) && (tReBe.getSelectedRowCount() <= 1)) {
                final RebeCustomBean selectedReBe = tableModel.getCidsBeanAtRow(index);
                if ((selectedReBe.getGeometry() != null)
                            && !mappingComp.getFeatureCollection().isSelected(selectedReBe)) {
                    mappingComp.getFeatureCollection().select(selectedReBe);
                }
            }
        } else {
            btnRemoveReBe.setEnabled(false);
            return;
        }
    }

    // TODO USE
    @Override
    public Icon getWidgetIcon() {
        return null;
    }

    @Override
    public int getStatus() {
        if (tReBe.getCellEditor() != null) {
            validationMessage = "Bitte vollenden Sie alle Änderungen bei den Rechten und Belastungen.";
            return Validatable.ERROR;
        }
        return Validatable.VALID;
    }

    /**
     * DOCUMENT ME!
     */
    private void cboBooleanEditorActionPerformed() {
        if (log.isDebugEnabled()) {
            log.debug("rechtCheckboxAction");
        }
        final TableCellEditor currentEditor = tReBe.getCellEditor();
        if (currentEditor != null) {
            currentEditor.stopCellEditing();
        }
        for (final Feature feature
                    : (Collection<Feature>)LagisBroker.getInstance().getMappingComponent().getFeatureCollection()
                    .getSelectedFeatures()) {
            LagisBroker.getInstance().getMappingComponent().getFeatureCollection().reconsiderFeature(feature);
        }
    }

    @Override
    public String getDisplayName(final BasicEntity entity) {
        if (entity instanceof RebeCustomBean) {
            final RebeCustomBean rebe = (RebeCustomBean)entity;
            return "ReBe - "
                        + (rebe.isRecht() ? "Recht" : "Belastung")
                        + " - "
                        + rebe.getNummer();
        }

        return Copyable.UNKNOWN_ENTITY;
    }

    @Override
    public Icon getDisplayIcon() {
        return this.copyDisplayIcon;
    }

    @Override
    public boolean knowsDisplayName(final BasicEntity entity) {
        return entity instanceof RebeCustomBean;
    }
}
