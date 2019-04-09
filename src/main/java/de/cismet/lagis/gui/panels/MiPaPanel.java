/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * MiPaRessortWidget.java
 *
 * Created on 23. April 2008, 11:26
 */
package de.cismet.lagis.gui.panels;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolo.PCanvas;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.beans.PropertyChangeEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import de.cismet.cids.custom.beans.lagis.*;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollection;
import de.cismet.cismap.commons.features.FeatureCollectionEvent;
import de.cismet.cismap.commons.features.FeatureCollectionListener;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.StyledFeatureGroupWrapper;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.editor.DateEditor;

import de.cismet.lagis.gui.checkbox.JCheckBoxList;
import de.cismet.lagis.gui.copypaste.Copyable;
import de.cismet.lagis.gui.copypaste.Pasteable;
import de.cismet.lagis.gui.tables.MipaTable;
import de.cismet.lagis.gui.tables.RemoveActionHelper;

import de.cismet.lagis.interfaces.FeatureSelectionChangedListener;
import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.FlurstueckSaver;
import de.cismet.lagis.interfaces.GeometrySlotProvider;
import de.cismet.lagis.interfaces.LagisBrokerPropertyChangeListener;

import de.cismet.lagis.models.DefaultUniqueListModel;
import de.cismet.lagis.models.MiPaModel;

import de.cismet.lagis.renderer.DateRenderer;
import de.cismet.lagis.renderer.FlurstueckSchluesselRenderer;

import de.cismet.lagis.util.TableSelectionUtils;

import de.cismet.lagis.utillity.GeometrySlotInformation;

import de.cismet.lagis.validation.Validatable;

import de.cismet.lagis.widget.AbstractWidget;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.core.hardwired.FlurstueckArt;
import de.cismet.lagisEE.entity.extension.vermietung.MiPa;
import de.cismet.lagisEE.entity.extension.vermietung.MiPaKategorie;
import de.cismet.lagisEE.entity.extension.vermietung.MiPaKategorieAuspraegung;
import de.cismet.lagisEE.entity.extension.vermietung.MiPaNutzung;

import de.cismet.tools.CurrentStackTrace;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class MiPaPanel extends AbstractWidget implements FlurstueckChangeListener,
    FlurstueckSaver,
    MouseListener,
    ListSelectionListener,
    ItemListener,
    GeometrySlotProvider,
    FeatureSelectionChangedListener,
    FeatureCollectionListener,
    TableModelListener,
    Copyable,
    Pasteable,
    RemoveActionHelper,
    LagisBrokerPropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final String PROVIDER_NAME = "MiPa";

    private static final String WIDGET_ICON = "/de/cismet/lagis/ressource/icons/mipa.png";

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(MiPaPanel.class);

    //~ Instance fields --------------------------------------------------------

    private boolean isFlurstueckEditable = true;
    private boolean isInEditMode = false;
    private final MiPaModel tableModel = new MiPaModel();
    private final JComboBox cbxAuspraegung = new JComboBox();
    private final Icon copyDisplayIcon;

    private boolean listenerEnabled = true;
    private Map<MipaCustomBean, Collection<FlurstueckSchluesselCustomBean>> crossreferences;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddMiPa;
    private javax.swing.JButton btnRemoveMiPa;
    private javax.swing.JButton btnUndo;
    private javax.swing.JScrollPane cpMiPa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JList lstCrossRefs;
    private javax.swing.JList lstMerkmale;
    private javax.swing.JPanel panBemerkung;
    private javax.swing.JPanel panBemerkungTitled;
    private javax.swing.JPanel panMerkmale;
    private javax.swing.JPanel panMerkmaleTitled;
    private javax.swing.JPanel panQuerverweise;
    private javax.swing.JPanel panQuerverweiseTitled;
    private javax.swing.JScrollPane spBemerkung;
    private javax.swing.JScrollPane spMerkmale;
    private javax.swing.JTextArea taBemerkung;
    private javax.swing.JTable tblMipa;
    private javax.swing.JToggleButton tbtnSort;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MiPaRessortWidget object.
     *
     * @param  widgetName  DOCUMENT ME!
     */
    public MiPaPanel(final String widgetName) {
        this(widgetName, WIDGET_ICON);
    }

    /**
     * Creates a new MiPaRessortWidget object.
     *
     * @param  widgetName  DOCUMENT ME!
     * @param  iconPath    DOCUMENT ME!
     */
    public MiPaPanel(final String widgetName, final String iconPath) {
        initComponents();
        setWidgetName(widgetName);
        setWidgetIcon(iconPath);
        configureComponents();
        setOpaqueRecursive(jPanel1.getComponents());

        this.copyDisplayIcon = new ImageIcon(this.getClass().getResource(WIDGET_ICON));
        LagisBroker.getInstance().addWfsFlurstueckGeometryChangeListener(this);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  components  DOCUMENT ME!
     */
    private void setOpaqueRecursive(final Component[] components) {
        for (final Component currentComp : components) {
            if (currentComp instanceof Container) {
                setOpaqueRecursive(((Container)currentComp).getComponents());
            }
            if (currentComp instanceof JComponent) {
                ((JComponent)currentComp).setOpaque(false);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void configureComponents() {
        TableSelectionUtils.crossReferenceModelAndTable(tableModel, (MipaTable)tblMipa);
        tblMipa.setDefaultEditor(Date.class, new DateEditor());
        tblMipa.setDefaultRenderer(Date.class, new DateRenderer());
        tblMipa.addMouseListener(this);

        final HighlightPredicate noGeometryPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedIndex = componentAdapter.row;
                    final int modelIndex = ((JXTable)tblMipa).convertRowIndexToModel(displayedIndex);
                    final MiPa mp = tableModel.getCidsBeanAtRow(modelIndex);
                    return (mp != null) && (mp.getGeometry() == null);
                }
            };

        final Highlighter noGeometryHighlighter = new ColorHighlighter(noGeometryPredicate, LagisBroker.GREY, null);

        final HighlightPredicate contractExpiredPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedIndex = componentAdapter.row;
                    final int modelIndex = ((JXTable)tblMipa).convertRowIndexToModel(displayedIndex);
                    final MiPa mp = tableModel.getCidsBeanAtRow(modelIndex);
                    return (mp != null) && (mp.getVertragsende() != null) && (mp.getVertragsbeginn() != null)
                                && (mp.getVertragsende().getTime() < System.currentTimeMillis());
                }
            };

        final Highlighter contractExpiredHighlighter = new ColorHighlighter(
                contractExpiredPredicate,
                LagisBroker.SUCCESSFUL_COLOR,
                null);
        ((JXTable)tblMipa).setHighlighters(
            LagisBroker.ALTERNATE_ROW_HIGHLIGHTER,
            contractExpiredHighlighter,
            noGeometryHighlighter);

        final Comparator dateComparator = new Comparator() {

                @Override
                public int compare(final Object o1, final Object o2) {
                    if ((o1 == null) && (o2 == null)) {
                        return 0;
                    } else if (o1 == null) {
                        return 1;
                    } else if (o2 == null) {
                        return -1;
                    } else {
                        return -1 * ((Date)o1).compareTo((Date)o2);
                    }
                }
            };
        ((JXTable)tblMipa).getColumnExt(MiPaModel.VERTRAGS_ENDE_COLUMN).setComparator(dateComparator);
        ((JXTable)tblMipa).setSortOrder(MiPaModel.VERTRAGS_ENDE_COLUMN, SortOrder.ASCENDING);
        tblMipa.getSelectionModel().addListSelectionListener(this);
        ((JXTable)tblMipa).setColumnControlVisible(true);
        ((JXTable)tblMipa).setHorizontalScrollEnabled(true);
//        TableColumnExt id = ((JXTable) tblMipa).getColumnExt(0);
//        id.setVisible(false);
        TableColumn tc = tblMipa.getColumnModel().getColumn(MiPaModel.NUTZUNG_COLUMN);
        // Kategorien EditorCombobox
        final JComboBox combo = new JComboBox();
        combo.setBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0));
        combo.setEditable(true);

        final Collection<MipaKategorieCustomBean> alleKategorien = LagisBroker.getInstance().getAllMiPaKategorien();
        for (final MiPaKategorie currentKategorie : alleKategorien) {
            combo.addItem(currentKategorie);
        }
        org.jdesktop.swingx.autocomplete.AutoCompleteDecorator.decorate(combo);

        tc.setCellEditor(new org.jdesktop.swingx.autocomplete.ComboBoxCellEditor(combo));

        tc = tblMipa.getColumnModel().getColumn(tableModel.AUSPRAEGUNG_COLUMN);
        cbxAuspraegung.setBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0));
        cbxAuspraegung.setEditable(true);
        org.jdesktop.swingx.autocomplete.AutoCompleteDecorator.decorate(cbxAuspraegung);
        tc.setCellEditor(new org.jdesktop.swingx.autocomplete.ComboBoxCellEditor(cbxAuspraegung));

        ((JXTable)tblMipa).packAll();

        taBemerkung.setDocument(tableModel.getBemerkungDocumentModel());

        enableSlaveComponents(false);
        // lstMerkmale.setm
        final Collection<MipaMerkmalCustomBean> miPaMerkmale = LagisBroker.getInstance().getAllMiPaMerkmale();
        final Collection<MiPaMerkmalCheckBox> merkmalCheckBoxes = new ArrayList<>();
        if ((miPaMerkmale != null) && (miPaMerkmale.size() > 0)) {
            for (final MipaMerkmalCustomBean currentMerkmal : miPaMerkmale) {
                if ((currentMerkmal != null) && (currentMerkmal.getBezeichnung() != null)) {
                    final MiPaMerkmalCheckBox newMerkmalCheckBox = new MiPaMerkmalCheckBox(currentMerkmal);
                    setOpaqueRecursive(newMerkmalCheckBox.getComponents());
                    newMerkmalCheckBox.setOpaque(false);
                    newMerkmalCheckBox.addItemListener(this);
                    merkmalCheckBoxes.add(newMerkmalCheckBox);
                }
            }
        }
        lstMerkmale.setListData(merkmalCheckBoxes.toArray());

        lstCrossRefs.setCellRenderer(new FlurstueckSchluesselRenderer());
        lstCrossRefs.setModel(new DefaultUniqueListModel());
        lstCrossRefs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstCrossRefs.addMouseListener(this);
        lstCrossRefs.setCellRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    String s = null;
                    if (value instanceof FlurstueckSchluessel) {
                        s = ((FlurstueckSchluessel)value).getKeyString();
                    } else {
                        s = value.toString();
                    }

                    setText(s);
                    setOpaque(false);

                    setEnabled(list.isEnabled());
                    setFont(list.getFont());
                    return this;
                }
            });

        final PCanvas pc = LagisBroker.getInstance().getMappingComponent().getSelectedObjectPresenter();
        pc.setBackground(this.getBackground());
        // ((SimpleBackgroundedJPanel) this.panBackground).setPCanvas(pc);
        // ((SimpleBackgroundedJPanel) this.panBackground).setTranslucency(0.5f);
        // ((SimpleBackgroundedJPanel) this.panBackground).setBackgroundEnabled(true);
        LagisBroker.getInstance().getMappingComponent().getFeatureCollection().addFeatureCollectionListener(this);

//      disabled because of the background transparency
//        taBemerkung.setDocument(miPaModel.getBemerkungDocumentModel());
//        valTxtBemerkung = new Validator(taBemerkung);
//        valTxtBemerkung.reSetValidator((Validatable) miPaModel.getBemerkungDocumentModel());
        tableModel.addTableModelListener(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mp  DOCUMENT ME!
     */
    private void updateCbxAuspraegung(final MiPa mp) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Update der Ausprägungen");
        }
        cbxAuspraegung.removeAllItems();
        final int maxNumericEntries = 100;
        if ((mp != null) && (mp.getMiPaNutzung() != null)
                    && (mp.getMiPaNutzung().getMiPaKategorie() != null)
                    && mp.getMiPaNutzung().getMiPaKategorie().getHatNummerAlsAuspraegung()) {
            if (cbxAuspraegung.getItemCount() != maxNumericEntries) {
                for (int i = 1; i <= maxNumericEntries; i++) {
                    cbxAuspraegung.addItem(i);
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Kein Update nötig Zahlen sind schon in der Combobox");
                }
            }
        } else if ((mp != null) && (mp.getMiPaNutzung() != null) && (mp.getMiPaNutzung().getMiPaKategorie() != null)
                    && (mp.getMiPaNutzung().getMiPaKategorie().getKategorieAuspraegungen() != null)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Ausprägungen sind vorhanden");
            }
            final Collection<MipaKategorieAuspraegungCustomBean> auspraegungen = mp.getMiPaNutzung()
                        .getMiPaKategorie()
                        .getKategorieAuspraegungen();
            for (final MiPaKategorieAuspraegung currentAuspraegung : auspraegungen) {
                cbxAuspraegung.addItem(currentAuspraegung);
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Ausprägungen vorhanden");
            }
        }
        cbxAuspraegung.validate();
        cbxAuspraegung.repaint();
        cbxAuspraegung.updateUI();
    }

    @Override
    public void clearComponent() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("clearComponent", new CurrentStackTrace());
        }
        tableModel.clearSlaveComponents();
        deselectAllListEntries();
        tableModel.refreshTableModel(new HashSet<MipaCustomBean>());
        lstCrossRefs.setModel(new DefaultUniqueListModel());
        if (EventQueue.isDispatchThread()) {
            lstCrossRefs.updateUI();
            lstCrossRefs.repaint();
        } else {
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        lstCrossRefs.updateUI();
                        lstCrossRefs.repaint();
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void deselectAllListEntries() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("deselect all entries", new CurrentStackTrace());
        }
        for (int i = 0; i < lstMerkmale.getModel().getSize(); i++) {
            final MiPaMerkmalCheckBox currentCheckBox = (MiPaMerkmalCheckBox)lstMerkmale.getModel().getElementAt(i);
            currentCheckBox.removeItemListener(this);
            currentCheckBox.setSelected(false);
            currentCheckBox.addItemListener(this);
        }
    }

    @Override
    public void refresh(final Object arg0) {
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
        if (isFlurstueckEditable) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("MiPARessortWidget --> setComponentEditable");
            }
            isInEditMode = isEditable;
            tableModel.setInEditMode(isEditable);
            final TableCellEditor currentEditor = tblMipa.getCellEditor();
            if (currentEditor != null) {
                currentEditor.cancelCellEditing();
            }

            if (isEditable && (tblMipa.getSelectedRow() != -1)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Editable und TabellenEintrag ist gewählt");
                }
                btnRemoveMiPa.setEnabled(true);
                enableSlaveComponents(isEditable);
            } else if (!isEditable) {
                deselectAllListEntries();
                enableSlaveComponents(isEditable);
                btnRemoveMiPa.setEnabled(isEditable);
            }

            btnAddMiPa.setEnabled(isEditable);
            tableModel.setInEditMode(isEditable);
            btnUndo.setEnabled(false);
            if (LOG.isDebugEnabled()) {
                LOG.debug("MiPARessortWidget --> setComponentEditable finished");
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Flurstück ist nicht städtisch Vermietung & Verpachtungen können nicht editiert werden");
            }
        }
    }

    @Override
    public void flurstueckChanged(final FlurstueckCustomBean newFlurstueck) {
        try {
            LOG.info("FlurstueckChanged");

            clearComponent();
            final FlurstueckArt flurstueckArt = newFlurstueck.getFlurstueckSchluessel().getFlurstueckArt();
            if ((flurstueckArt != null)
                        && flurstueckArt.getBezeichnung().equals(
                            FlurstueckArt.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Flurstück ist städtisch und kann editiert werden");
                }
                isFlurstueckEditable = true;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Flurstück ist nicht städtisch und kann nicht editiert werden");
                }
                isFlurstueckEditable = false;
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim Flurstückswechsel: ", ex);
            LagisBroker.getInstance().flurstueckChangeFinished(MiPaPanel.this);
        } finally {
            LagisBroker.getInstance().flurstueckChangeFinished(MiPaPanel.this);
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (LagisBrokerPropertyChangeListener.PROP__CURRENT_MIPAS.equals(evt.getPropertyName())) {
            final Collection<MipaCustomBean> mipas = (Collection)evt.getNewValue();
            tableModel.refreshTableModel(mipas);
            final Collection<Feature> features = tableModel.getAllMiPaFeatures();
            final MappingComponent mappingComp = LagisBroker.getInstance().getMappingComponent();
            final FeatureCollection featureCollection = mappingComp.getFeatureCollection();
            if (features != null) {
                for (Feature currentFeature : features) {
                    if (currentFeature != null) {
                        if (isWidgetReadOnly()) {
                            ((MiPa)currentFeature).setModifiable(false);
                        }

                        currentFeature = new StyledFeatureGroupWrapper((StyledFeature)currentFeature,
                                PROVIDER_NAME,
                                PROVIDER_NAME);

                        featureCollection.addFeature(currentFeature);
                    }
                }
            }
            ((JXTable)tblMipa).packAll();

            final DefaultListModel loadingModel = new DefaultListModel();
            loadingModel.addElement("werden geladen...");
            lstCrossRefs.setModel(loadingModel);
            new SwingWorker<Map<MipaCustomBean, Collection<FlurstueckSchluesselCustomBean>>, Void>() {

                    @Override
                    protected Map<MipaCustomBean, Collection<FlurstueckSchluesselCustomBean>> doInBackground()
                            throws Exception {
                        return LagisBroker.getInstance().getCrossreferencesForMiPas(mipas);
                    }

                    @Override
                    protected void done() {
                        try {
                            crossreferences = get();
                            final int selectedIndex = tblMipa.getSelectedRow();
                            if ((crossreferences != null) && (crossreferences.size() > 0) && (selectedIndex >= 0)) {
                                final MipaCustomBean selectedMiPa = tableModel.getCidsBeanAtRow(((JXTable)tblMipa)
                                                .convertRowIndexToModel(selectedIndex));
                                final Collection<FlurstueckSchluesselCustomBean> keys = crossreferences.get(
                                        selectedMiPa);
                                if (keys != null) {
                                    lstCrossRefs.setModel(new DefaultUniqueListModel(keys));
                                }
                            } else {
                                lstCrossRefs.setModel(new DefaultUniqueListModel());
                            }
                            ((JXTable)tblMipa).packAll();
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                            lstCrossRefs.setModel(null);
                        }
                    }
                }.execute();
        }
    }

    @Override
    public void updateFlurstueckForSaving(final FlurstueckCustomBean flurstueck) {
        final Collection<MipaCustomBean> mipas = LagisBroker.getInstance().getCurrentMipas();
        for (final MipaCustomBean mipa : (List<MipaCustomBean>)tableModel.getCidsBeans()) {
            if (!mipas.contains(mipa)) {
                mipas.add(mipa);
            }
        }
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        final Object source = e.getSource();
        if (source instanceof JXTable) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Mit maus auf MiPaTabelle geklickt");
            }
            final int selecetdRow = tblMipa.getSelectedRow();
            if (selecetdRow != -1) {
                if (isInEditMode) {
                    enableSlaveComponents(true);
                    btnRemoveMiPa.setEnabled(true);
                } else {
                    enableSlaveComponents(false);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Liste ausgeschaltet");
                    }
                    if (selecetdRow == -1) {
                        deselectAllListEntries();
                    }
                }
            } else {
                // currentSelectedRebe = null;
                btnRemoveMiPa.setEnabled(false);
            }
        } else if (source instanceof JList) {
            if (e.getClickCount() > 1) {
                final FlurstueckSchluesselCustomBean key = (FlurstueckSchluesselCustomBean)
                    lstCrossRefs.getSelectedValue();
                if (key != null) {
                    LagisBroker.getInstance().loadFlurstueck(key);
                }
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
        mouseClicked(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isEnabled  DOCUMENT ME!
     */
    private void enableSlaveComponents(final boolean isEnabled) {
        taBemerkung.setEditable(isEnabled);
        lstMerkmale.setEnabled(isEnabled);
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("SelectionChanged MiPa");
        }
        lstCrossRefs.setModel(new DefaultUniqueListModel());
        final int viewIndex = tblMipa.getSelectedRow();
        if (viewIndex != -1) {
            if (isInEditMode) {
                btnRemoveMiPa.setEnabled(true);
            } else {
                btnRemoveMiPa.setEnabled(false);
            }

            final int index = ((JXTable)tblMipa).convertRowIndexToModel(viewIndex);
            if ((index != -1) && (tblMipa.getSelectedRowCount() <= 1)) {
                final MipaCustomBean selectedMiPa = tableModel.getCidsBeanAtRow(index);
                tableModel.setCurrentSelectedMipa(selectedMiPa);
                if (selectedMiPa != null) {
                    if (crossreferences != null) {
                        final Collection<FlurstueckSchluesselCustomBean> keys = crossreferences.get(selectedMiPa);
                        if (keys != null) {
                            lstCrossRefs.setModel(new DefaultUniqueListModel(keys));
                        }
                    }
                    updateCbxAuspraegung(selectedMiPa);
                    if (selectedMiPa.getGeometry() == null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("SetBackgroundEnabled abgeschaltet: ", new CurrentStackTrace());
                        }
                        // ((SimpleBackgroundedJPanel) this.panBackground).setBackgroundEnabled(false);
                    } else {
                        // if (!((SimpleBackgroundedJPanel) this.panBackground).isBackgroundEnabled()) {
// ((SimpleBackgroundedJPanel) this.panBackground).setBackgroundEnabled(true);
                        // }
                    }
                }
                final Collection<MipaMerkmalCustomBean> merkmale = selectedMiPa.getMiPaMerkmal();
                if (merkmale != null) {
                    for (int i = 0; i < lstMerkmale.getModel().getSize(); i++) {
                        final MiPaMerkmalCheckBox currentCheckBox = (MiPaMerkmalCheckBox)lstMerkmale.getModel()
                                    .getElementAt(i);
                        if ((currentCheckBox != null) && (currentCheckBox.getMiPaMerkmal() != null)
                                    && merkmale.contains(currentCheckBox.getMiPaMerkmal())) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Merkmal ist in MiPa vorhanden");
                            }
                            currentCheckBox.removeItemListener(this);
                            currentCheckBox.setSelected(true);
                            currentCheckBox.addItemListener(this);
                            lstMerkmale.repaint();
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Merkmal ist nicht in MiPa vorhanden");
                            }
                            currentCheckBox.removeItemListener(this);
                            currentCheckBox.setSelected(false);
                            currentCheckBox.addItemListener(this);
                            lstMerkmale.repaint();
                        }
                    }
                }
                if (isInEditMode) {
                    enableSlaveComponents(isInEditMode);
                } else {
                    enableSlaveComponents(isInEditMode);
                }
            }
            ((MipaTable)tblMipa).valueChanged_updateFeatures(this, e);
        } else {
            btnRemoveMiPa.setEnabled(false);
            deselectAllListEntries();
            tableModel.clearSlaveComponents();
            enableSlaveComponents(false);
            return;
        }
        ((JXTable)tblMipa).packAll();
    }

    @Override
    public int getStatus() {
        if (tblMipa.getCellEditor() != null) {
            validationMessage = "Bitte vollenden Sie alle Änderungen bei den Vermietungen und Verpachtungen.";
            return Validatable.ERROR;
        }
        final ArrayList<MipaCustomBean> miPas = (ArrayList<MipaCustomBean>)tableModel.getCidsBeans();
        if ((miPas != null) || (miPas.size() > 0)) {
            for (final MiPa currentMiPa : miPas) {
                if ((currentMiPa != null)
                            && ((currentMiPa.getMiPaNutzung() == null)
                                || (currentMiPa.getMiPaNutzung().getMiPaKategorie() == null))) {
                    validationMessage = "Alle Vermietungen und Verpachtungen müssen eine Nutzung (Kategorie) enthalten";
                    return Validatable.ERROR;
                }
                if ((currentMiPa != null)
                            && ((currentMiPa.getNutzer() == null) || currentMiPa.getNutzer().equals(""))) {
                    validationMessage = "Alle Vermietungen und Verpachtungen müssen einen Nutzer besitzen.";
                    return Validatable.ERROR;
                }
                if ((currentMiPa != null) && (currentMiPa.getVertragsbeginn() == null)) {
                    validationMessage = "Alle Vermietungen und Verpachtungen müssen ein Vertragsbeginn besitzen.";
                    return Validatable.ERROR;
                }
                if ((currentMiPa != null) && (currentMiPa.getLage() == null)) {
                    validationMessage = "Alle Vermietungen und Verpachtungen müssen eine Lage besitzen.";
                    return Validatable.ERROR;
                }
                if ((currentMiPa != null) && (currentMiPa.getAktenzeichen() == null)) {
                    validationMessage = "Alle Vermietungen und Verpachtungen müssen ein Aktenzeichen besitzen.";
                    return Validatable.ERROR;
                }
                if ((currentMiPa != null) && (currentMiPa.getVertragsbeginn() != null)
                            && (currentMiPa.getVertragsende() != null)
                            && (currentMiPa.getVertragsbeginn().compareTo(currentMiPa.getVertragsende()) > 0)) {
                    validationMessage = "Das Datum des Vertragsbeginns muss vor dem Datum des Vertragsende liegen.";
                    return Validatable.ERROR;
                }
            }
        }
        return Validatable.VALID;
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Item State of MiPAMerkmal Changed " + e);
        }
        // TODO use Constants from Java
        final MiPaMerkmalCheckBox checkBox = (MiPaMerkmalCheckBox)e.getSource();
        if (tblMipa.getSelectedRow() != -1) {
            final MiPa miPa = tableModel.getCidsBeanAtRow(((JXTable)tblMipa).convertRowIndexToModel(
                        tblMipa.getSelectedRow()));
            if (miPa != null) {
                Collection<MipaMerkmalCustomBean> merkmale = miPa.getMiPaMerkmal();
                if (merkmale == null) {
                    LOG.info("neues Hibernateset für Merkmale angelegt");
                    merkmale = new HashSet<MipaMerkmalCustomBean>();
                }

                if (e.getStateChange() == 1) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Checkbox wurde selektiert --> füge es zum Set hinzu");
                    }
                    merkmale.add(checkBox.getMiPaMerkmal());
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Checkbox wurde deselektiert --> lösche es aus Set");
                    }
                    merkmale.remove(checkBox.getMiPaMerkmal());
                }
            } else {
                LOG.warn(
                    "Kann merkmalsänderung nicht speichern da kein Eintrag unter diesem Index im Modell vorhanden ist");
            }
        } else {
            LOG.warn("Kann merkmalsänderung nicht speichern da kein Eintrag selektiert ist");
        }
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   mipa  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getIdentifierString(final MiPa mipa) {
        final String idValue1 = mipa.getLage();
        final MiPaNutzung idValue2 = mipa.getMiPaNutzung();

        final StringBuffer identifier = new StringBuffer();

        if (idValue1 != null) {
            identifier.append(idValue1);
        } else {
            identifier.append("keine Lage");
        }

        if ((idValue2 != null) && (idValue2.getMiPaKategorie() != null)) {
            identifier.append(GeometrySlotInformation.SLOT_IDENTIFIER_SEPARATOR + idValue2.getMiPaKategorie());
        } else {
            identifier.append(GeometrySlotInformation.SLOT_IDENTIFIER_SEPARATOR + "keine Nutzung");
        }

        if ((idValue2 != null)
                    && ((idValue2.getAusgewaehlteNummer() != null) || (idValue2.getAusgewaehlteAuspraegung() != null))) {
            if (idValue2.getAusgewaehlteNummer() != null) {
                identifier.append(GeometrySlotInformation.SLOT_IDENTIFIER_SEPARATOR + "Nr. "
                            + idValue2.getAusgewaehlteNummer());
            } else {
                identifier.append(GeometrySlotInformation.SLOT_IDENTIFIER_SEPARATOR
                            + idValue2.getAusgewaehlteAuspraegung());
            }
        } else {
            identifier.append(GeometrySlotInformation.SLOT_IDENTIFIER_SEPARATOR + "keine Ausprägung");
        }

        return identifier.toString();
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
                final MiPa currentMiPa = tableModel.getCidsBeanAtRow(i);
                // Geom geom;
                if (currentMiPa.getGeometry() == null) {
                    result.add(new GeometrySlotInformation(
                            getProviderName(),
                            this.getIdentifierString(currentMiPa),
                            currentMiPa,
                            this));
                }
            }
            return result;
        }
    }

    // TODO multiple Selection
    // HINT If there are problems try to remove/add Listselectionlistener at start/end of Method
    @Override
    public void featureSelectionChanged(final Collection<Feature> features) {
        ((MipaTable)tblMipa).featureSelectionChanged(this, features, MipaCustomBean.class);
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPanel2 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        cpMiPa = new javax.swing.JScrollPane();
        tblMipa = new MipaTable();
        jPanel3 = new javax.swing.JPanel();
        btnAddMiPa = new javax.swing.JButton();
        btnRemoveMiPa = new javax.swing.JButton();
        tbtnSort = new javax.swing.JToggleButton();
        btnUndo = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        panQuerverweise = new javax.swing.JPanel();
        panQuerverweiseTitled = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstCrossRefs = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        panMerkmale = new javax.swing.JPanel();
        panMerkmaleTitled = new javax.swing.JPanel();
        spMerkmale = new javax.swing.JScrollPane();
        lstMerkmale = new JCheckBoxList();
        jLabel1 = new javax.swing.JLabel();
        panBemerkung = new javax.swing.JPanel();
        panBemerkungTitled = new javax.swing.JPanel();
        spBemerkung = new javax.swing.JScrollPane();
        taBemerkung = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();

        final javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(
                    jTabbedPane2,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    148,
                    Short.MAX_VALUE).addContainerGap()));
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(
                    jTabbedPane2,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    126,
                    Short.MAX_VALUE).addContainerGap()));

        setLayout(new java.awt.GridBagLayout());

        jPanel4.setLayout(new java.awt.GridBagLayout());

        cpMiPa.setBorder(null);

        tblMipa.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    { null, null, null, null, null, null, null },
                    { null, null, null, null, null, null, null },
                    { null, null, null, null, null, null, null },
                    { null, null, null, null, null, null, null },
                    { null, null, null, null, null, null, null },
                    { null, null, null, null, null, null, null },
                    { null, null, null, null, null, null, null },
                    { null, null, null, null, null, null, null },
                    { null, null, null, null, null, null, null },
                    { null, null, null, null, null, null, null },
                    { null, null, null, null, null, null, null },
                    { null, null, null, null, null, null, null }
                },
                new String[] { "Nummer", "Lage", "Fläche m²", "Nutzung", "Nutzer", "Vertragsbeginn", "Vertragsende" }));
        cpMiPa.setViewportView(tblMipa);
        ((MipaTable)tblMipa).setSortButton(tbtnSort);
        ((MipaTable)tblMipa).setUndoButton(btnUndo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(cpMiPa, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        btnAddMiPa.setAction(((MipaTable)tblMipa).getAddAction());
        btnAddMiPa.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/add.png"))); // NOI18N
        btnAddMiPa.setBorder(null);
        btnAddMiPa.setBorderPainted(false);
        btnAddMiPa.setMaximumSize(new java.awt.Dimension(25, 25));
        btnAddMiPa.setMinimumSize(new java.awt.Dimension(25, 25));
        btnAddMiPa.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel3.add(btnAddMiPa, gridBagConstraints);

        btnRemoveMiPa.setAction(((MipaTable)tblMipa).getRemoveAction());
        btnRemoveMiPa.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/remove.png"))); // NOI18N
        btnRemoveMiPa.setBorder(null);
        btnRemoveMiPa.setBorderPainted(false);
        btnRemoveMiPa.setMaximumSize(new java.awt.Dimension(25, 25));
        btnRemoveMiPa.setMinimumSize(new java.awt.Dimension(25, 25));
        btnRemoveMiPa.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel3.add(btnRemoveMiPa, gridBagConstraints);

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
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        jPanel3.add(tbtnSort, gridBagConstraints);
        tbtnSort.addItemListener(((MipaTable)tblMipa).getSortItemListener());

        btnUndo.setAction(((MipaTable)tblMipa).getUndoAction());
        btnUndo.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/undo.png"))); // NOI18N
        btnUndo.setBorder(null);
        btnUndo.setBorderPainted(false);
        btnUndo.setFocusPainted(false);
        btnUndo.setMaximumSize(new java.awt.Dimension(25, 25));
        btnUndo.setMinimumSize(new java.awt.Dimension(25, 25));
        btnUndo.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel3.add(btnUndo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 2);
        jPanel4.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 6, 12);
        add(jPanel4, gridBagConstraints);

        jPanel1.setMinimumSize(new java.awt.Dimension(24, 100));
        jPanel1.setPreferredSize(new java.awt.Dimension(314, 100));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        panQuerverweise.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panQuerverweise.setOpaque(false);
        panQuerverweise.setLayout(new java.awt.GridBagLayout());

        panQuerverweiseTitled.setOpaque(false);

        jScrollPane1.setBorder(null);
        jScrollPane1.setOpaque(false);

        lstCrossRefs.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        lstCrossRefs.setOpaque(false);
        jScrollPane1.setViewportView(lstCrossRefs);

        final javax.swing.GroupLayout panQuerverweiseTitledLayout = new javax.swing.GroupLayout(panQuerverweiseTitled);
        panQuerverweiseTitled.setLayout(panQuerverweiseTitledLayout);
        panQuerverweiseTitledLayout.setHorizontalGroup(
            panQuerverweiseTitledLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                jScrollPane1,
                javax.swing.GroupLayout.PREFERRED_SIZE,
                0,
                Short.MAX_VALUE));
        panQuerverweiseTitledLayout.setVerticalGroup(
            panQuerverweiseTitledLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                jScrollPane1,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                61,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        panQuerverweise.add(panQuerverweiseTitled, gridBagConstraints);

        jLabel2.setText("Querverweise:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        panQuerverweise.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        jPanel1.add(panQuerverweise, gridBagConstraints);

        panMerkmale.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panMerkmale.setOpaque(false);
        panMerkmale.setLayout(new java.awt.GridBagLayout());

        panMerkmaleTitled.setOpaque(false);

        spMerkmale.setBorder(null);
        spMerkmale.setOpaque(false);

        lstMerkmale.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        lstMerkmale.setOpaque(false);
        spMerkmale.setViewportView(lstMerkmale);

        final javax.swing.GroupLayout panMerkmaleTitledLayout = new javax.swing.GroupLayout(panMerkmaleTitled);
        panMerkmaleTitled.setLayout(panMerkmaleTitledLayout);
        panMerkmaleTitledLayout.setHorizontalGroup(
            panMerkmaleTitledLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                spMerkmale,
                javax.swing.GroupLayout.PREFERRED_SIZE,
                0,
                Short.MAX_VALUE));
        panMerkmaleTitledLayout.setVerticalGroup(
            panMerkmaleTitledLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                spMerkmale,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                61,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        panMerkmale.add(panMerkmaleTitled, gridBagConstraints);

        jLabel1.setText("Merkmale:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        panMerkmale.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        jPanel1.add(panMerkmale, gridBagConstraints);

        panBemerkung.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panBemerkung.setOpaque(false);
        panBemerkung.setLayout(new java.awt.GridBagLayout());

        panBemerkungTitled.setOpaque(false);

        spBemerkung.setOpaque(false);

        taBemerkung.setColumns(20);
        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(3);
        taBemerkung.setWrapStyleWord(true);

        final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                taBemerkung,
                org.jdesktop.beansbinding.ELProperty.create("${editable}"),
                taBemerkung,
                org.jdesktop.beansbinding.BeanProperty.create("opaque"));
        bindingGroup.addBinding(binding);

        spBemerkung.setViewportView(taBemerkung);

        final javax.swing.GroupLayout panBemerkungTitledLayout = new javax.swing.GroupLayout(panBemerkungTitled);
        panBemerkungTitled.setLayout(panBemerkungTitledLayout);
        panBemerkungTitledLayout.setHorizontalGroup(
            panBemerkungTitledLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                spBemerkung,
                javax.swing.GroupLayout.PREFERRED_SIZE,
                0,
                Short.MAX_VALUE));
        panBemerkungTitledLayout.setVerticalGroup(
            panBemerkungTitledLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                spBemerkung,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                61,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        panBemerkung.add(panBemerkungTitled, gridBagConstraints);

        jLabel3.setText("Bemerkung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        panBemerkung.add(jLabel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel1.add(panBemerkung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 11, 12);
        add(jPanel1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     */
    private void updateWidgetUi() {
        tblMipa.repaint();
        lstCrossRefs.repaint();
        lstMerkmale.repaint();
    }

    @Override
    public void allFeaturesRemoved(final FeatureCollectionEvent fce) {
        updateWidgetUi();
    }

    @Override
    public void featureCollectionChanged() {
        updateWidgetUi();
    }

    @Override
    public void featureReconsiderationRequested(final FeatureCollectionEvent fce) {
        updateWidgetUi();
    }

    @Override
    public void featureSelectionChanged(final FeatureCollectionEvent fce) {
        updateWidgetUi();
    }

    @Override
    public void featuresAdded(final FeatureCollectionEvent fce) {
        updateWidgetUi();
    }

    @Override
    public void featuresChanged(final FeatureCollectionEvent fce) {
        updateWidgetUi();
    }

    @Override
    public void featuresRemoved(final FeatureCollectionEvent fce) {
        updateWidgetUi();
    }

    @Override
    public void tableChanged(final TableModelEvent e) {
        ((JXTable)tblMipa).packAll();
    }

    @Override
    public List<BasicEntity> getCopyData() {
        final ArrayList<MipaCustomBean> allMiPas = (ArrayList<MipaCustomBean>)this.tableModel.getCidsBeans();
        final ArrayList<BasicEntity> result = new ArrayList<>(allMiPas.size());

        MipaCustomBean tmp;
        for (final MipaCustomBean mipa : allMiPas) {
            tmp = MipaCustomBean.createNew();

            tmp.setAktenzeichen(mipa.getAktenzeichen());
            tmp.setBemerkung(mipa.getBemerkung());
            tmp.setCanBeSelected(mipa.canBeSelected());
            tmp.setEditable(mipa.isEditable());
            tmp.setFillingPaint(mipa.getFillingPaint());
            tmp.setFlaeche(mipa.getFlaeche());

            final Geometry geom = mipa.getGeometry();
            if (geom != null) {
                tmp.setGeometry((Geometry)geom.clone());
            }

            tmp.setHighlightingEnabled(mipa.isHighlightingEnabled());
            tmp.setLage(mipa.getLage());
            tmp.setLinePaint(mipa.getLinePaint());
            tmp.setLineWidth(mipa.getLineWidth());
            tmp.setMiPaMerkmal(mipa.getMiPaMerkmal());
            tmp.setMiPaNutzung(mipa.getMiPaNutzung());
            tmp.setModifiable(mipa.isModifiable());
            tmp.setNutzer(mipa.getNutzer());
            tmp.setPointAnnotationSymbol(mipa.getPointAnnotationSymbol());
            tmp.setTransparency(mipa.getTransparency());
            tmp.setVertragsbeginn(mipa.getVertragsbeginn());
            tmp.setVertragsende(mipa.getVertragsende());
            tmp.hide(mipa.isHidden());

            result.add(tmp);
        }

        return result;
    }

    @Override
    public void paste(final BasicEntity item) {
        if (item == null) {
            throw new NullPointerException("Given data item must not be null");
        }

        if (item instanceof MiPa) {
            final Collection<MipaCustomBean> residentMiPas = (Collection<MipaCustomBean>)this.tableModel.getCidsBeans();

            if (residentMiPas.contains(item)) {
                LOG.warn("MiPa " + item + " does already exist in Flurstück "
                            + LagisBroker.getInstance().getCurrentFlurstueck());
            } else {
                this.tableModel.addCidsBean((MipaCustomBean)item);

                final MappingComponent mc = LagisBroker.getInstance().getMappingComponent();
                final Feature f = new StyledFeatureGroupWrapper((StyledFeature)item, PROVIDER_NAME, PROVIDER_NAME);
                mc.getFeatureCollection().addFeature(f);
                mc.setGroupLayerVisibility(PROVIDER_NAME, true);

                this.tableModel.fireTableDataChanged();
            }
        }
    }

    @Override
    public void pasteAll(final List<BasicEntity> dataList) {
        if (dataList == null) {
            throw new NullPointerException("Given list of MiPa items must not be null");
        }

        if (dataList.isEmpty()) {
            return;
        }

        final ArrayList<MipaCustomBean> residentMiPas = (ArrayList<MipaCustomBean>)this.tableModel.getCidsBeans();
        final int rowCountBefore = this.tableModel.getRowCount();

        Feature f;
        final MappingComponent mc = LagisBroker.getInstance().getMappingComponent();
        final FeatureCollection featCollection = mc.getFeatureCollection();

        for (final BasicEntity entity : dataList) {
            if (entity instanceof MiPa) {
                if (residentMiPas.contains(entity)) {
                    LOG.warn("Verwaltungsbereich " + entity + " does already exist in Flurstück "
                                + LagisBroker.getInstance().getCurrentFlurstueck());
                } else {
                    this.tableModel.addCidsBean((MipaCustomBean)entity);
                    f = new StyledFeatureGroupWrapper((StyledFeature)entity, PROVIDER_NAME, PROVIDER_NAME);
                    featCollection.addFeature(f);
                }
            }
        }

        if (rowCountBefore == this.tableModel.getRowCount()) {
            LOG.warn("No MiPa items were added from input list " + dataList);
        } else {
            this.tableModel.fireTableDataChanged();
            mc.setGroupLayerVisibility(PROVIDER_NAME, true);
        }
    }

    @Override
    public String getDisplayName(final BasicEntity entity) {
        if (entity instanceof MiPa) {
            final MiPa mipa = (MiPa)entity;

            return this.getProviderName()
                        + GeometrySlotInformation.SLOT_IDENTIFIER_SEPARATOR
                        + this.getIdentifierString(mipa);
        }

        return Copyable.UNKNOWN_ENTITY;
    }

    @Override
    public Icon getDisplayIcon() {
        return this.copyDisplayIcon;
    }

    @Override
    public boolean knowsDisplayName(final BasicEntity entity) {
        return entity instanceof MiPa;
    }

    @Override
    public void duringRemoveAction(final Object source) {
        enableSlaveComponents(false);
        deselectAllListEntries();
        if (LOG.isDebugEnabled()) {
            LOG.debug("liste ausgeschaltet");
        }
    }

    @Override
    public void afterRemoveAction(final Object source) {
        // not used at the moment
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public boolean isFeatureSelectionChangedEnabled() {
        return listenerEnabled;
    }

    @Override
    public void setFeatureSelectionChangedEnabled(final boolean listenerEnabled) {
        this.listenerEnabled = listenerEnabled;
    }
}
