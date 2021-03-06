/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * BaumRessortWidget.java
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
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
import de.cismet.lagis.gui.main.LagisApp;
import de.cismet.lagis.gui.tables.RemoveActionHelper;

import de.cismet.lagis.interfaces.FeatureSelectionChangedListener;
import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.FlurstueckSaver;
import de.cismet.lagis.interfaces.GeometrySlotProvider;

import de.cismet.lagis.models.DefaultUniqueListModel;

import de.cismet.lagis.renderer.DateRenderer;
import de.cismet.lagis.renderer.FlurstueckSchluesselRenderer;

import de.cismet.lagis.util.TableSelectionUtils;

import de.cismet.lagis.utillity.GeometrySlotInformation;

import de.cismet.lagis.validation.Validatable;

import de.cismet.lagis.widget.AbstractWidget;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.CustomSelectionStyledFeatureGroupWrapper;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.core.hardwired.FlurstueckArt;
import de.cismet.lagisEE.entity.extension.baum.Baum;
import de.cismet.lagisEE.entity.extension.baum.BaumKategorie;
import de.cismet.lagisEE.entity.extension.baum.BaumKategorieAuspraegung;
import de.cismet.lagisEE.entity.extension.baum.BaumNutzung;

import de.cismet.tools.CurrentStackTrace;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class BaumPanel extends AbstractWidget implements FlurstueckChangeListener,
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
    RemoveActionHelper {

    //~ Static fields/initializers ---------------------------------------------

    private static final String PROVIDER_NAME = "Baum";

    private static final String WIDGET_ICON = "/de/cismet/lagis/ressource/icons/baum.png";

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(BaumPanel.class);

    //~ Instance fields --------------------------------------------------------

    private boolean isFlurstueckEditable = true;
    private boolean isInEditMode = false;
    private final BaumModel baumModel = new BaumModel();
    private final ImageIcon icoExistingContract = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/contract.png"));
    private final JComboBox cbxAuspraegung = new JComboBox();
    private final Icon copyDisplayIcon;

    private boolean listenerEnabled = true;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddBaum;
    private javax.swing.JButton btnAddExitingBaum;
    private javax.swing.JButton btnRemoveBaum;
    private javax.swing.JButton btnUndo;
    private javax.swing.JScrollPane cpBaum;
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
    private javax.swing.JTable tblBaum;
    private javax.swing.JToggleButton tbtnSort;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumRessortWidget object.
     *
     * @param  widgetName  DOCUMENT ME!
     */
    public BaumPanel(final String widgetName) {
        this(widgetName, WIDGET_ICON);
    }

    /**
     * Creates new form BaumRessortWidget.
     *
     * @param  widgetName  DOCUMENT ME!
     * @param  iconPath    DOCUMENT ME!
     */
    public BaumPanel(final String widgetName, final String iconPath) {
        initComponents();
        setWidgetName(widgetName);
        setWidgetIcon(iconPath);
        configureComponents();
        setOpaqueRecursive(jPanel1.getComponents());
        this.copyDisplayIcon = new ImageIcon(this.getClass().getResource(WIDGET_ICON));
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public List<BasicEntity> getCopyData() {
        final ArrayList<BaumCustomBean> allBaeume = (ArrayList<BaumCustomBean>)this.baumModel.getCidsBeans();
        final ArrayList<BasicEntity> result = new ArrayList<>(allBaeume.size());

        BaumCustomBean tmp;
        for (final BaumCustomBean baum : allBaeume) {
            tmp = BaumCustomBean.createNew();

            tmp.setAlteNutzung(baum.getAlteNutzung());
            tmp.setAuftragnehmer(baum.getAuftragnehmer());
            tmp.setBaumMerkmal(baum.getBaumMerkmal());
            tmp.setBaumNutzung(baum.getBaumNutzung());
            tmp.setBemerkung(baum.getBemerkung());
            tmp.setCanBeSelected(baum.canBeSelected());
            tmp.setEditable(baum.isEditable());
            tmp.setErfassungsdatum(baum.getErfassungsdatum());
            tmp.setFaelldatum(baum.getFaelldatum());
            tmp.setFillingPaint(baum.getFillingPaint());
            tmp.setFlaeche(baum.getFlaeche());

            final Geometry geom = baum.getGeometry();
            if (geom != null) {
                tmp.setGeometry((Geometry)geom.clone());
            }

            tmp.setHighlightingEnabled(baum.isHighlightingEnabled());
            tmp.setLage(baum.getLage());
            tmp.setLinePaint(baum.getLinePaint());
            tmp.setLineWidth(baum.getLineWidth());
            tmp.setModifiable(baum.isModifiable());
            tmp.setPointAnnotationSymbol(baum.getPointAnnotationSymbol());
            tmp.setTransparency(baum.getTransparency());
            tmp.hide(baum.isHidden());

            result.add(tmp);
        }

        return result;
    }

    @Override
    public void paste(final BasicEntity item) {
        if (item == null) {
            throw new NullPointerException("Given data item must not be null");
        }

        if (item instanceof Baum) {
            final ArrayList<BaumCustomBean> residentBaeume = (ArrayList<BaumCustomBean>)this.baumModel.getCidsBeans();

            if (residentBaeume.contains(item)) {
                LOG.warn("Baum " + item + " does already exist in Flurstück "
                            + LagisBroker.getInstance().getCurrentFlurstueck());
            } else {
                this.baumModel.addCidsBean((BaumCustomBean)item);

                final MappingComponent mc = LagisBroker.getInstance().getMappingComponent();
                final Feature f = new CustomSelectionStyledFeatureGroupWrapper((StyledFeature)item,
                        PROVIDER_NAME,
                        PROVIDER_NAME);
                mc.getFeatureCollection().addFeature(f);
                mc.setGroupLayerVisibility(PROVIDER_NAME, true);
            }
        }
    }

    @Override
    public void pasteAll(final List<BasicEntity> dataList) {
        if (dataList == null) {
            throw new NullPointerException("Given list of Baum items must not be null");
        }

        if (dataList.isEmpty()) {
            return;
        }

        final ArrayList<BaumCustomBean> residentBaeume = (ArrayList<BaumCustomBean>)this.baumModel.getCidsBeans();
        final int rowCountBefore = this.baumModel.getRowCount();

        final MappingComponent mc = LagisBroker.getInstance().getMappingComponent();
        final FeatureCollection fc = mc.getFeatureCollection();

        StyledFeatureGroupWrapper wrapper;
        for (final BasicEntity entity : dataList) {
            if (entity instanceof Baum) {
                if (residentBaeume.contains(entity)) {
                    LOG.warn("Verwaltungsbereich " + entity + " does already exist in Flurstück "
                                + LagisBroker.getInstance().getCurrentFlurstueck());
                } else {
                    this.baumModel.addCidsBean((BaumCustomBean)entity);
                    wrapper = new CustomSelectionStyledFeatureGroupWrapper((StyledFeature)entity,
                            PROVIDER_NAME,
                            PROVIDER_NAME);
                    fc.addFeature(wrapper);
                }
            }
        }

        if (rowCountBefore == this.baumModel.getRowCount()) {
            LOG.warn("No Baum items were added from input list " + dataList);
        } else {
            this.baumModel.fireTableDataChanged();
            mc.setGroupLayerVisibility(PROVIDER_NAME, true);
        }
    }

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
        TableSelectionUtils.crossReferenceModelAndTable(baumModel, (BaumTable)tblBaum);
        tblBaum.setDefaultEditor(Date.class, new DateEditor());
        tblBaum.setDefaultRenderer(Date.class, new DateRenderer());
        tblBaum.addMouseListener(this);

        final HighlightPredicate noGeometryPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedIndex = componentAdapter.row;
                    final int modelIndex = ((JXTable)tblBaum).convertRowIndexToModel(displayedIndex);
                    final Baum mp = baumModel.getCidsBeanAtRow(modelIndex);
                    return (mp != null) && (mp.getGeometry() == null);
                }
            };

        final Highlighter noGeometryHighlighter = new ColorHighlighter(noGeometryPredicate, LagisBroker.GREY, null);
        final HighlightPredicate contractExpiredPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    final int displayedIndex = componentAdapter.row;
                    final int modelIndex = ((JXTable)tblBaum).convertRowIndexToModel(displayedIndex);
                    final Baum mp = baumModel.getCidsBeanAtRow(modelIndex);
                    return (mp != null) && (mp.getFaelldatum() != null) && (mp.getErfassungsdatum() != null)
                                && (mp.getFaelldatum().getTime() < System.currentTimeMillis());
                }
            };

        final Highlighter contractExpiredHighlighter = new ColorHighlighter(
                contractExpiredPredicate,
                LagisBroker.SUCCESSFUL_COLOR,
                null);

        ((JXTable)tblBaum).setHighlighters(
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
        ((JXTable)tblBaum).getColumnExt(BaumModel.FAELLDATUM_COLUMN).setComparator(dateComparator);
        ((JXTable)tblBaum).setSortOrder(BaumModel.FAELLDATUM_COLUMN, SortOrder.ASCENDING);
        tblBaum.getSelectionModel().addListSelectionListener(this);
        ((JXTable)tblBaum).setColumnControlVisible(true);
        ((JXTable)tblBaum).setHorizontalScrollEnabled(true);
        TableColumn tc = tblBaum.getColumnModel().getColumn(BaumModel.BAUMBESTAND_COLUMN);
        // Kategorien EditorCombobox
        final JComboBox combo = new JComboBox();
        combo.setBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0));
        combo.setEditable(true);
        final Collection<BaumKategorieCustomBean> alleKategorien = LagisBroker.getInstance().getAllBaumKategorien();
        for (final BaumKategorie currentKategorie : alleKategorien) {
            combo.addItem(currentKategorie);
        }
        combo.addItem("");
        org.jdesktop.swingx.autocomplete.AutoCompleteDecorator.decorate(combo);
        combo.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent event) {
                }
            });
        tc.setCellEditor(new org.jdesktop.swingx.autocomplete.ComboBoxCellEditor(combo));

        tc = tblBaum.getColumnModel().getColumn(BaumModel.AUSPRAEGUNG_COLUMN);
        cbxAuspraegung.setBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0));
        cbxAuspraegung.setEditable(true);
        org.jdesktop.swingx.autocomplete.AutoCompleteDecorator.decorate(cbxAuspraegung);
        tc.setCellEditor(new org.jdesktop.swingx.autocomplete.ComboBoxCellEditor(cbxAuspraegung));

        ((JXTable)tblBaum).packAll();

        taBemerkung.setDocument(baumModel.getBemerkungDocumentModel());

        enableSlaveComponents(false);
        // lstMerkmale.setm
        final Collection<BaumMerkmalCustomBean> baumMerkmal = LagisBroker.getInstance().getAllBaumMerkmale();
        final Vector<BaumMerkmalCheckBox> merkmalCheckBoxes = new Vector<>();
        if ((baumMerkmal != null) && (baumMerkmal.size() > 0)) {
            for (final BaumMerkmalCustomBean currentMerkmal : baumMerkmal) {
                if ((currentMerkmal != null) && (currentMerkmal.getBezeichnung() != null)) {
                    final BaumMerkmalCheckBox newMerkmalCheckBox = new BaumMerkmalCheckBox(currentMerkmal);
                    setOpaqueRecursive(newMerkmalCheckBox.getComponents());
                    newMerkmalCheckBox.setOpaque(false);
                    newMerkmalCheckBox.addItemListener(this);
                    merkmalCheckBoxes.add(newMerkmalCheckBox);
                }
            }
        }
        lstMerkmale.setListData(merkmalCheckBoxes);

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
                    String s;
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
        LagisBroker.getInstance().getMappingComponent().getFeatureCollection().addFeatureCollectionListener(this);
        baumModel.addTableModelListener(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mp  DOCUMENT ME!
     */
    private void updateCbxAuspraegung(final Baum mp) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Update der Ausprägungen");
        }
        cbxAuspraegung.removeAllItems();
        if ((mp != null) && (mp.getBaumNutzung() != null) && (mp.getBaumNutzung().getBaumKategorie() != null)
                    && (mp.getBaumNutzung().getBaumKategorie().getKategorieAuspraegungen() != null)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Ausprägungen sind vorhanden");
            }
            final Collection<BaumKategorieAuspraegungCustomBean> auspraegungen = mp.getBaumNutzung()
                        .getBaumKategorie()
                        .getKategorieAuspraegungen();
            for (final BaumKategorieAuspraegung currentAuspraegung : auspraegungen) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("currentAusprägung: " + currentAuspraegung);
                }
                cbxAuspraegung.addItem(currentAuspraegung);
            }
            cbxAuspraegung.addItem("");
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
        baumModel.clearSlaveComponents();
        deselectAllListEntries();
        baumModel.refreshTableModel(new HashSet<BaumCustomBean>());
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
            final BaumMerkmalCheckBox currentCheckBox = (BaumMerkmalCheckBox)lstMerkmale.getModel().getElementAt(i);
            currentCheckBox.removeItemListener(this);
            currentCheckBox.setSelected(false);
            currentCheckBox.addItemListener(this);
        }
        lstMerkmale.repaint();
    }

    @Override
    public void refresh(final Object arg0) {
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
        if (isFlurstueckEditable) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("BaumRessortWidget --> setComponentEditable");
            }
            isInEditMode = isEditable;
            baumModel.setInEditMode(isEditable);
            final TableCellEditor currentEditor = tblBaum.getCellEditor();
            if (currentEditor != null) {
                currentEditor.cancelCellEditing();
            }

            if (isEditable && (tblBaum.getSelectedRow() != -1)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Editable und TabellenEintrag ist gewählt");
                }
                btnRemoveBaum.setEnabled(true);
                enableSlaveComponents(isEditable);
            } else if (!isEditable) {
                deselectAllListEntries();
                enableSlaveComponents(isEditable);
                btnRemoveBaum.setEnabled(isEditable);
            }

            btnAddExitingBaum.setEnabled(isEditable);
            btnAddBaum.setEnabled(isEditable);
            btnUndo.setEnabled(false);
            if (LOG.isDebugEnabled()) {
                LOG.debug("BaumRessortWidget --> setComponentEditable finished");
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Flurstück ist nicht städtisch Bäume können nicht editiert werden");
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
            baumModel.refreshTableModel(newFlurstueck.getBaeume());
            final Collection<FlurstueckSchluesselCustomBean> crossRefs = newFlurstueck.getBaeumeQuerverweise();
            if ((crossRefs != null) && (crossRefs.size() > 0)) {
                lstCrossRefs.setModel(new DefaultUniqueListModel(crossRefs));
            }
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        final ArrayList<Feature> features = baumModel.getAllBaumFeatures();
                        final MappingComponent mappingComp = LagisBroker.getInstance().getMappingComponent();
                        final FeatureCollection featureCollection = mappingComp.getFeatureCollection();

                        if (features != null) {
                            for (Feature currentFeature : features) {
                                if (currentFeature != null) {
                                    if (isWidgetReadOnly()) {
                                        ((Baum)currentFeature).setModifiable(false);
                                    }

                                    currentFeature = new CustomSelectionStyledFeatureGroupWrapper(
                                            (StyledFeature)currentFeature,
                                            PROVIDER_NAME,
                                            PROVIDER_NAME);
                                    featureCollection.addFeature(currentFeature);
                                }
                            }
                        }
                        ((JXTable)tblBaum).packAll();
                    }
                });
        } catch (Exception ex) {
            LOG.error("Fehler im refresh thread: ", ex);
        } finally {
            LagisBroker.getInstance().flurstueckChangeFinished(BaumPanel.this);
        }
    }

    @Override
    public void updateFlurstueckForSaving(final FlurstueckCustomBean flurstueck) {
        final Collection<BaumCustomBean> baeume = flurstueck.getBaeume();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Anzahl baeume aktuell gespeichert im Flurstück");
            LOG.debug("Anzahl Baueme im tablemodel: " + baumModel.getRowCount());
        }
        if (baeume != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("warens schon baueme vorhanden");
            }
            baeume.clear();
            baeume.addAll((ArrayList<BaumCustomBean>)baumModel.getCidsBeans());
            if (LOG.isDebugEnabled()) {
                LOG.debug("baueme im Flurstueck Set: " + baeume.size());
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Waren noch keine Baueme vorhanden");
            }
            final HashSet newSet = new HashSet();
            newSet.addAll(baumModel.getCidsBeans());
            flurstueck.setBaeume(newSet);
        }
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        final Object source = e.getSource();
        if (source instanceof JXTable) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Mit maus auf BaumTaeble geklickt");
            }
            final int selecetdRow = tblBaum.getSelectedRow();
            if (selecetdRow != -1) {
                if (isInEditMode) {
                    enableSlaveComponents(true);
                    btnRemoveBaum.setEnabled(true);
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
                btnRemoveBaum.setEnabled(false);
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
            LOG.debug("SelectionChanged Baum");
        }
        final int viewIndex = tblBaum.getSelectedRow();
        if (viewIndex != -1) {
            if (isInEditMode) {
                btnRemoveBaum.setEnabled(true);
            } else {
                btnRemoveBaum.setEnabled(false);
            }

            final int index = ((JXTable)tblBaum).convertRowIndexToModel(viewIndex);
            if ((index != -1) && (tblBaum.getSelectedRowCount() <= 1)) {
                final Baum selectedBaum = baumModel.getCidsBeanAtRow(index);
                baumModel.setCurrentSelectedBaum(selectedBaum);
                if (selectedBaum != null) {
                    updateCbxAuspraegung(selectedBaum);
                    if (selectedBaum.getGeometry() == null) {
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
                final Collection<BaumMerkmalCustomBean> merkmale = selectedBaum.getBaumMerkmal();
                if (merkmale != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Merkmale vorhanden");
                    }
                    for (int i = 0; i < lstMerkmale.getModel().getSize(); i++) {
                        final BaumMerkmalCheckBox currentCheckBox = (BaumMerkmalCheckBox)lstMerkmale.getModel()
                                    .getElementAt(i);
                        if ((currentCheckBox != null) && (currentCheckBox.getBaumMerkmal() != null)
                                    && merkmale.contains(currentCheckBox.getBaumMerkmal())) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Merkmal ist in Baum vorhanden");
                            }
                            currentCheckBox.removeItemListener(this);
                            currentCheckBox.setSelected(true);
                            currentCheckBox.addItemListener(this);
                            lstMerkmale.repaint();
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Merkmal ist nicht in Baum vorhanden");
                            }
                            currentCheckBox.removeItemListener(this);
                            currentCheckBox.setSelected(false);
                            currentCheckBox.addItemListener(this);
                            lstMerkmale.repaint();
                        }
                    }
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Keine Merkmale vorhanden");
                    }
                    deselectAllListEntries();
                }
                if (isInEditMode) {
                    enableSlaveComponents(isInEditMode);
                } else {
                    enableSlaveComponents(isInEditMode);
                }
            }
            ((BaumTable)tblBaum).valueChanged_updateFeatures(this, e);
        } else {
            btnRemoveBaum.setEnabled(false);
            deselectAllListEntries();
            baumModel.clearSlaveComponents();
            enableSlaveComponents(false);
            return;
        }
        ((JXTable)tblBaum).packAll();
    }

    @Override
    public int getStatus() {
        if (tblBaum.getCellEditor() != null) {
            validationMessage = "Bitte vollenden Sie alle Änderungen bei den Baumeinträgen.";
            return Validatable.ERROR;
        }

        final ArrayList<BaumCustomBean> baeume = (ArrayList<BaumCustomBean>)baumModel.getCidsBeans();
        if ((baeume != null) || (baeume.size() > 0)) {
            for (final Baum currentBaum : baeume) {
                if ((currentBaum != null)
                            && ((currentBaum.getBaumNutzung() == null)
                                || (currentBaum.getBaumNutzung().getBaumKategorie() == null))) {
                    validationMessage = "Alle Baueme müssen einen Baumbestand (Kategorie) enthalten";
                    return Validatable.ERROR;
                }
                if ((currentBaum != null)
                            && ((currentBaum.getAuftragnehmer() == null) || currentBaum.getAuftragnehmer().equals(
                                    ""))) {
                    validationMessage = "Alle Baueme müssen einen Auftragnehmer besitzen.";
                    return Validatable.ERROR;
                }
                if ((currentBaum != null) && (currentBaum.getLage() == null)) {
                    validationMessage = "Alle Baueme müssen eine Lage besitzen.";
                    return Validatable.ERROR;
                }
                if ((currentBaum != null) && (currentBaum.getBaumnummer() == null)) {
                    validationMessage = "Alle Baueme müssen ein Baumnummer besitzen.";
                    return Validatable.ERROR;
                }
                if ((currentBaum != null) && (currentBaum.getErfassungsdatum() != null)
                            && (currentBaum.getFaelldatum() != null)
                            && (currentBaum.getErfassungsdatum().compareTo(currentBaum.getFaelldatum()) > 0)) {
                    validationMessage = "Das Datum der Erfassung muss vor dem Datum des Fällens liegen.";
                    return Validatable.ERROR;
                }
            }
        }
        return Validatable.VALID;
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Item State of BaumMerkmal Changed " + e);
        }
        // TODO use Constants from Java
        final BaumMerkmalCheckBox checkBox = (BaumMerkmalCheckBox)e.getSource();
        if (tblBaum.getSelectedRow() != -1) {
            final BaumCustomBean baum = baumModel.getCidsBeanAtRow(((JXTable)tblBaum).convertRowIndexToModel(
                        tblBaum.getSelectedRow()));
            if (baum != null) {
                Collection<BaumMerkmalCustomBean> merkmale = baum.getBaumMerkmal();
                if (merkmale == null) {
                    LOG.info("neues Hibernateset für Merkmale angelegt");
                    merkmale = new HashSet<BaumMerkmalCustomBean>();
                    baum.setBaumMerkmal(merkmale);
                }

                if (e.getStateChange() == 1) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Checkbox wurde selektiert --> füge es zum Set hinzu");
                    }
                    merkmale.add(checkBox.getBaumMerkmal());
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Checkbox wurde deselektiert --> lösche es aus Set");
                    }
                    merkmale.remove(checkBox.getBaumMerkmal());
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

    @Override
    public Vector<GeometrySlotInformation> getSlotInformation() {
        // VerwaltungsTableModel tmp = (VerwaltungsTableModel) tNutzung.getModel();
        final Vector<GeometrySlotInformation> result = new Vector<GeometrySlotInformation>();
        if (isWidgetReadOnly()) {
            return result;
        } else {
            final int rowCount = baumModel.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                final Baum currentBaum = baumModel.getCidsBeanAtRow(i);
                // Geom geom;
                if (currentBaum.getGeometry() == null) {
                    result.add(new GeometrySlotInformation(
                            getProviderName(),
                            this.getIdentifierString(currentBaum),
                            currentBaum,
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
        ((BaumTable)tblBaum).featureSelectionChanged(this, features, BaumCustomBean.class);
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
        cpBaum = new javax.swing.JScrollPane();
        tblBaum = new BaumTable();
        jPanel3 = new javax.swing.JPanel();
        btnAddBaum = new javax.swing.JButton();
        btnRemoveBaum = new javax.swing.JButton();
        btnAddExitingBaum = new javax.swing.JButton();
        btnUndo = new javax.swing.JButton();
        tbtnSort = new javax.swing.JToggleButton();
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

        cpBaum.setBorder(null);

        tblBaum.setModel(new javax.swing.table.DefaultTableModel(
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
        ((BaumTable)tblBaum).setSortButton(tbtnSort);
        ((BaumTable)tblBaum).setUndoButton(btnUndo);
        cpBaum.setViewportView(tblBaum);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(cpBaum, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        btnAddBaum.setAction(((BaumTable)tblBaum).getAddAction());
        btnAddBaum.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/add.png"))); // NOI18N
        btnAddBaum.setBorder(null);
        btnAddBaum.setBorderPainted(false);
        btnAddBaum.setFocusPainted(false);
        btnAddBaum.setMaximumSize(new java.awt.Dimension(25, 25));
        btnAddBaum.setMinimumSize(new java.awt.Dimension(25, 25));
        btnAddBaum.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel3.add(btnAddBaum, gridBagConstraints);

        btnRemoveBaum.setAction(((BaumTable)tblBaum).getRemoveAction());
        btnRemoveBaum.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/remove.png"))); // NOI18N
        btnRemoveBaum.setBorder(null);
        btnRemoveBaum.setBorderPainted(false);
        btnRemoveBaum.setFocusPainted(false);
        btnRemoveBaum.setMaximumSize(new java.awt.Dimension(25, 25));
        btnRemoveBaum.setMinimumSize(new java.awt.Dimension(25, 25));
        btnRemoveBaum.setPreferredSize(new java.awt.Dimension(25, 25));
        btnRemoveBaum.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveBaumActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel3.add(btnRemoveBaum, gridBagConstraints);

        btnAddExitingBaum.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/contract.png"))); // NOI18N
        btnAddExitingBaum.setBorder(null);
        btnAddExitingBaum.setBorderPainted(false);
        btnAddExitingBaum.setFocusPainted(false);
        btnAddExitingBaum.setMaximumSize(new java.awt.Dimension(25, 25));
        btnAddExitingBaum.setMinimumSize(new java.awt.Dimension(25, 25));
        btnAddExitingBaum.setPreferredSize(new java.awt.Dimension(25, 25));
        btnAddExitingBaum.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddExitingBaumActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel3.add(btnAddExitingBaum, gridBagConstraints);

        btnUndo.setAction(((BaumTable)tblBaum).getUndoAction());
        btnUndo.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/undo.png"))); // NOI18N
        btnUndo.setToolTipText("Rückgängig machen");
        btnUndo.setBorder(null);
        btnUndo.setBorderPainted(false);
        btnUndo.setFocusPainted(false);
        btnUndo.setMaximumSize(new java.awt.Dimension(25, 25));
        btnUndo.setMinimumSize(new java.awt.Dimension(25, 25));
        btnUndo.setPreferredSize(new java.awt.Dimension(25, 25));
        btnUndo.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnUndoActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel3.add(btnUndo, gridBagConstraints);

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
        tbtnSort.addItemListener(((BaumTable)tblBaum).getSortItemListener());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 2);
        jPanel4.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
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
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel1.add(panBemerkung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 12, 12);
        add(jPanel1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnUndoActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnUndoActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_btnUndoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddExitingBaumActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddExitingBaumActionPerformed
        final JDialog dialog = new JDialog(LagisApp.getInstance(), "", true);
        dialog.add(new AddExistingBaumPanel(
                LagisBroker.getInstance().getCurrentFlurstueck(),
                baumModel,
                lstCrossRefs.getModel()));
        dialog.pack();
        dialog.setIconImage(icoExistingContract.getImage());
        dialog.setTitle("Vorhandener Vertrag hinzufügen...");
        StaticSwingTools.showDialog(dialog);
    }                                                                                     //GEN-LAST:event_btnAddExitingBaumActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveBaumActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveBaumActionPerformed
        final int currentRow = tblBaum.getSelectedRow();
        if (currentRow != -1) {
            // VerwaltungsTableModel currentModel = (VerwaltungsTableModel)tNutzung.getModel();
            baumModel.removeCidsBean(((JXTable)tblBaum).convertRowIndexToModel(currentRow));
            baumModel.fireTableDataChanged();
            updateCrossRefs();
            enableSlaveComponents(false);
            deselectAllListEntries();
            if (LOG.isDebugEnabled()) {
                LOG.debug("liste ausgeschaltet");
            }
        }
    } //GEN-LAST:event_btnRemoveBaumActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void updateCrossRefs() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Update der Querverweise");
        }
        final Collection<FlurstueckSchluesselCustomBean> crossRefs = LagisBroker.getInstance()
                    .getCrossreferencesForBaeume(new HashSet(baumModel.getCidsBeans()));
        final DefaultUniqueListModel newModel = new DefaultUniqueListModel();
        if (crossRefs != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Es sind Querverweise auf Baeume vorhanden");
            }
            LagisBroker.getInstance().getCurrentFlurstueck().setVertraegeQuerverweise(crossRefs);
            final Iterator<FlurstueckSchluesselCustomBean> it = crossRefs.iterator();
            while (it.hasNext()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ein Querverweis hinzugefügt");
                }
                newModel.addElement(it.next());
            }
            newModel.removeElement(LagisBroker.getInstance().getCurrentFlurstueck().getFlurstueckSchluessel());
        }
        lstCrossRefs.setModel(newModel);
    }

    /**
     * DOCUMENT ME!
     */
    private void updateWidgetUi() {
        tblBaum.repaint();
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
        ((JXTable)tblBaum).packAll();
    }

    @Override
    public String getDisplayName(final BasicEntity entity) {
        if (entity instanceof Baum) {
            final Baum baum = (Baum)entity;
            return this.getProviderName()
                        + GeometrySlotInformation.SLOT_IDENTIFIER_SEPARATOR
                        + this.getIdentifierString(baum);
        }

        return Copyable.UNKNOWN_ENTITY;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   baum  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getIdentifierString(final Baum baum) {
        final String idValue1 = baum.getLage();
        final BaumNutzung idValue2 = baum.getBaumNutzung();

        final StringBuffer identifier = new StringBuffer();

        if (idValue1 != null) {
            identifier.append(idValue1);
        } else {
            identifier.append("keine Lage");
        }

        if ((idValue2 != null) && (idValue2.getBaumKategorie() != null)) {
            identifier.append(GeometrySlotInformation.SLOT_IDENTIFIER_SEPARATOR + idValue2.getBaumKategorie());
        } else {
            identifier.append(GeometrySlotInformation.SLOT_IDENTIFIER_SEPARATOR + "keine Nutzung");
        }

        if ((idValue2 != null) && (idValue2.getAusgewaehlteAuspraegung() != null)) {
            identifier.append(GeometrySlotInformation.SLOT_IDENTIFIER_SEPARATOR
                        + idValue2.getAusgewaehlteAuspraegung());
        } else {
            identifier.append(GeometrySlotInformation.SLOT_IDENTIFIER_SEPARATOR + "keine Ausprägung");
        }

        return identifier.toString();
    }

    @Override
    public Icon getDisplayIcon() {
        return this.copyDisplayIcon;
    }

    @Override
    public boolean knowsDisplayName(final BasicEntity entity) {
        return entity instanceof Baum;
    }

    @Override
    public void duringRemoveAction(final Object source) {
        updateCrossRefs();
        enableSlaveComponents(false);
        deselectAllListEntries();
        if (LOG.isDebugEnabled()) {
            LOG.debug("liste ausgeschaltet");
        }
    }

    @Override
    public void afterRemoveAction(final Object source) {
        // is not used at the moment
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
