/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * VerwaltungsPanel.java
 *
 * Created on 16. März 2007, 12:24
 */
package de.cismet.lagis.gui.panels;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;
import org.jdesktop.swingx.decorator.*;

import org.jdom.Element;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.text.BadLocationException;

import de.cismet.cids.custom.beans.lagis.FlurstueckArtCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.lagis.GeomCustomBean;
import de.cismet.cids.custom.beans.lagis.RebeCustomBean;
import de.cismet.cids.custom.beans.lagis.VerwaltendeDienststelleCustomBean;
import de.cismet.cids.custom.beans.lagis.VerwaltungsbereichCustomBean;
import de.cismet.cids.custom.beans.lagis.ZusatzRolleArtCustomBean;
import de.cismet.cids.custom.beans.lagis.ZusatzRolleCustomBean;

import de.cismet.cismap.commons.features.*;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.StyledFeatureGroupWrapper;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.editor.FlaecheEditor;

import de.cismet.lagis.gui.copypaste.Copyable;
import de.cismet.lagis.gui.copypaste.Pasteable;
import de.cismet.lagis.gui.dialogs.VerwaltungsbereicheHistorieDialog;
import de.cismet.lagis.gui.tables.VerwaltungsTable;
import de.cismet.lagis.gui.tables.ZusatzRolleTable;

import de.cismet.lagis.interfaces.FeatureSelectionChangedListener;
import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.FlurstueckSaver;
import de.cismet.lagis.interfaces.GeometrySlotProvider;

import de.cismet.lagis.models.VerwaltungsTableModel;
import de.cismet.lagis.models.ZusatzRolleTableModel;
import de.cismet.lagis.models.documents.SimpleDocumentModel;

import de.cismet.lagis.renderer.FlaecheRenderer;
import de.cismet.lagis.renderer.VerwaltendeDienststelleRenderer;

import de.cismet.lagis.thread.BackgroundUpdateThread;
import de.cismet.lagis.thread.WFSRetrieverFactory;

import de.cismet.lagis.util.TableSelectionUtils;

import de.cismet.lagis.utillity.GeometrySlotInformation;

import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.ValidationStateChangedListener;
import de.cismet.lagis.validation.Validator;

import de.cismet.lagis.widget.AbstractWidget;

import de.cismet.lagisEE.entity.basic.BasicEntity;

import de.cismet.tools.CismetThreadPool;

import de.cismet.tools.configuration.Configurable;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.historybutton.DefaultHistoryModel;
import de.cismet.tools.gui.historybutton.HistoryModelListener;
import de.cismet.tools.gui.historybutton.JHistoryButton;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class VerwaltungsPanel extends AbstractWidget implements GeometrySlotProvider,
    FlurstueckSaver,
    FlurstueckChangeListener,
    FeatureSelectionChangedListener,
    ListSelectionListener,
    Configurable,
    ValidationStateChangedListener,
    FeatureCollectionListener,
    HistoryModelListener,
    Copyable,
    Pasteable {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROVIDER_NAME = "Verwaltende Dienstelle";
    private static final String WIDGET_NAME = "Verwaltungspanel";

    private static final String COPY_DISPLAY_ICON = "/de/cismet/lagis/ressource/icons/verwaltungsbereich16.png";
    private static final VerwaltungsPanel INSTANCE = new VerwaltungsPanel();
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(VerwaltungsPanel.class);

    //~ Instance fields --------------------------------------------------------

    protected boolean historyEnabled = true;
    private final Icon icoWFSSizeGood = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/FlurstueckPanel/wfs_green.png"));
    private final Icon icoWFSSizeBad = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/FlurstueckPanel/wfs_red.png"));
    private final Icon icoWFSSizeTolerated = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/FlurstueckPanel/wfs_yellow.png"));
    private final Icon icoWFSLoad = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/FlurstueckPanel/exec.png"));
    private final Icon icoWFSWarn = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/FlurstueckPanel/16warn.png"));
    private final Icon icoBelastung = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/FlurstueckPanel/belastung.png"));
    private final Icon icoRecht = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/FlurstueckPanel/recht.png"));
    private final Icon icoRebeExpired = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/FlurstueckPanel/rebeExpired.png"));

    private FlurstueckCustomBean currentFlurstueck = null;
    private Validator valTxtBemerkung;
    private SimpleDocumentModel bemerkungDocumentModel;
    private VerwaltungsTableModel verwaltungsTableModel = new VerwaltungsTableModel();
    private ZusatzRolleTableModel zusatzRolleTableModel = new ZusatzRolleTableModel();
    private boolean isInEditMode = false;
    private BackgroundUpdateThread<FlurstueckCustomBean> updateThread;
    private VerwaltendeDienststelleRenderer vdRenderer = new VerwaltendeDienststelleRenderer();
    private WFSRetrieverFactory.WFSWorkerThread currentWFSRetriever;
    private Geometry currentGeometry = null;
    // hierüber kann man ausfindig machen was bei AbteilungIX oder Städtisch passiert (falls gerefactored wird)
    private boolean isFlurstueckEditable = true;
    private JLabel lblLastModification;
    private JHistoryButton hbBack;
    private JHistoryButton hbFwd;
    private DefaultHistoryModel historyModel = new DefaultHistoryModel();
    // ToDo Comboboxen selbst in Validatoren stecken und auswerten
    private Vector<Validator> validators = new Vector<Validator>();
    private final Icon copyDisplayIcon;

    private boolean listenerEnabled = true;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddVerwaltung;
    private javax.swing.JButton btnAddZusatzRolle;
    private javax.swing.JButton btnHistorie;
    private javax.swing.JButton btnRemoveVerwaltung;
    private javax.swing.JButton btnRemoveZusatzRolle;
    private javax.swing.JButton btnUndo;
    private javax.swing.JCheckBox cbSperre;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblBelastungen;
    private javax.swing.JLabel lblBemSperre;
    private javax.swing.JLabel lblRechte;
    private javax.swing.JLabel lblWFSInfo;
    private javax.swing.JTable tNutzung;
    private javax.swing.JTable tZusatzRolle;
    private javax.swing.JToggleButton tbtnSort;
    private javax.swing.JTextArea txtBemerkung;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form VerwaltungsPanel.
     */
    private VerwaltungsPanel() {
        this.copyDisplayIcon = new ImageIcon(this.getClass().getResource(COPY_DISPLAY_ICON));
        setIsCoreWidget(true);
        initComponents();
        // tNutzung.setModel(new VerwaltungsTableModel());
        configureTable();
        valTxtBemerkung = new Validator(txtBemerkung);
        initModels();
        configBackgroundThread();
        lblLastModification = new JLabel();
        lblLastModification.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/goto.png")));
        lblLastModification.setOpaque(false);
        historyModel.addHistoryModelListener(this);
        configureButtons();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static VerwaltungsPanel getInstance() {
        return INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JTable getNutzungTable() {
        return tNutzung;
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
                        if ((getCurrentObject().getFlurstueckSchluessel().getLetzter_bearbeiter() != null)
                                    && (getCurrentObject().getFlurstueckSchluessel().getLetzte_bearbeitung() != null)) {
                            lblLastModification.setToolTipText(getCurrentObject().getFlurstueckSchluessel()
                                        .getLetzter_bearbeiter() + " am "
                                        + LagisBroker.getDateFormatter().format(
                                            getCurrentObject().getFlurstueckSchluessel().getLetzte_bearbeitung()));
                        } else if (getCurrentObject().getFlurstueckSchluessel().getLetzter_bearbeiter() != null) {
                            lblLastModification.setToolTipText(getCurrentObject().getFlurstueckSchluessel()
                                        .getLetzter_bearbeiter());
                        } else {
                            lblLastModification.setToolTipText("Unbekannt");
                        }
                        if (getCurrentObject().getFlurstueckSchluessel() != null) {
                            historyModel.addToHistory(getCurrentObject());
                        }
                        final FlurstueckArtCustomBean flurstueckArt = getCurrentObject().getFlurstueckSchluessel()
                                    .getFlurstueckArt();
                        if ((flurstueckArt != null)
                                    && flurstueckArt.getBezeichnung().equals(
                                        FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Flurstück ist städtisch und kann editiert werden");
                            }
                            isFlurstueckEditable = true;
//                        cbKind.setIcon(icoStaedtisch);
//                        cbKind.setVisible(true);
                        } else if ((flurstueckArt != null)
                                    && flurstueckArt.getBezeichnung().equals(
                                        FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                    "Flurstück ist nicht städtisch und kann nicht editiert werden (Abteilung IX)");
                            }
                            isFlurstueckEditable = false;
//                        cbKind.setIcon(icoAbteilungIX);
//                        cbKind.setVisible(true);
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Flurstück ist nicht städtisch und kann nicht editiert werden");
                            }
                            isFlurstueckEditable = false;
                            // cbKind.setVisible(false);
                        }
                        if ((getCurrentObject().getVerwaltungsbereiche() != null)
                                    || ((getCurrentObject().getVerwaltungsbereiche() != null)
                                        && (getCurrentObject().getVerwaltungsbereiche().size() == 0))) {
                            lblWFSInfo.setIcon(icoWFSSizeGood);
                            lblWFSInfo.setToolTipText("Keine Verwaltungsbereiche vorhanden");
                        }
                        // Soll als Abteilungs IX Flurstück nicht angezeigt werden
                        if (isFlurstueckEditable) {
                            tNutzung.setVisible(true);
                        } else {
                            tNutzung.setVisible(false);
                        }

                        refreshReBeIcons();
                        if ((currentWFSRetriever != null) && !currentWFSRetriever.isDone()) {
                            currentWFSRetriever.cancel(true);
                            currentWFSRetriever = null;
                            currentGeometry = null;
                        }
                        currentWFSRetriever = (WFSRetrieverFactory.WFSWorkerThread)WFSRetrieverFactory
                                    .getInstance()
                                    .getWFSRetriever(getCurrentObject().getFlurstueckSchluessel(), null, null);
                        currentWFSRetriever.addPropertyChangeListener(new PropertyChangeListener() {

                                @Override
                                public void propertyChange(final PropertyChangeEvent evt) {
                                    if ((evt.getSource() instanceof WFSRetrieverFactory.WFSWorkerThread)
                                                && evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("Gemarkungsretriever done --> setzte geometrie");
                                        }
                                        try {
                                            final SwingWorker worker = new SwingWorker<Geometry, Void>() {

                                                    @Override
                                                    protected Geometry doInBackground() throws Exception {
                                                        return currentWFSRetriever.get();
                                                    }

                                                    @Override
                                                    protected void done() {
                                                        try {
                                                            final Geometry result = get();
                                                            currentGeometry = result;
                                                            if (result == null) {
                                                                lblWFSInfo.setIcon(icoWFSWarn);
                                                                lblWFSInfo.setToolTipText(
                                                                    "Keine WFS Geometrie vorhanden");
                                                                verwaltungsTableModel.setCurrentWFSSize(0);
                                                            } else {
                                                                verwaltungsTableModel.setCurrentWFSSize(
                                                                    currentGeometry.getArea());
                                                            }
                                                        } catch (Exception e) {
                                                            LOG.error("Exception in Background Thread", e);
                                                        }
                                                    }
                                                };
                                            CismetThreadPool.execute(worker);
                                        } catch (Exception ex) {
                                            LOG.error("Fehler beim abrufen der Geometrie", ex);
                                            currentGeometry = null;
                                            lblWFSInfo.setIcon(icoWFSWarn);
                                            lblWFSInfo.setToolTipText("Fehler beim vergleichen der Flächen");
                                            verwaltungsTableModel.setCurrentWFSSize(0);
                                        }
                                    }
                                }
                            });
                        currentWFSRetriever.execute();
                        final String bemerkung = getCurrentObject().getBemerkung();
                        if (bemerkung != null) {
                            // txtBemerkung.setText(bemerkung);
                            bemerkungDocumentModel.insertString(0, bemerkung, null);
                        }
                        // datamodell refactoring 22.10.07
                        final Boolean isGesperrt = getCurrentObject().getFlurstueckSchluessel().getIstGesperrt();
                        if (isGesperrt != null) {
                            cbSperre.setSelected(isGesperrt);
                            final String sperrentext = getCurrentObject().getFlurstueckSchluessel()
                                        .getBemerkungSperre();
                            if (sperrentext != null) {
                                lblBemSperre.setText(sperrentext);
                            } else {
                                lblBemSperre.setText("");
                            }
                        } else {
                            cbSperre.setSelected(false);
                        }

                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Anzahl verwaltungsbereiche: "
                                        + getCurrentObject().getVerwaltungsbereiche().size());
                        }
                        final Collection<VerwaltungsbereichCustomBean> verwaltungsbereiche =
                            new ArrayList<VerwaltungsbereichCustomBean>();
                        for (final VerwaltungsbereichCustomBean verwaltungsbereich
                                    : getCurrentObject().getVerwaltungsbereiche()) {
                            final GeomCustomBean geomBean;
                            if (verwaltungsbereich.getFk_geom() == null) {
                                geomBean = null;
                            } else {
                                geomBean = GeomCustomBean.createNew();
                                geomBean.setGeo_field(verwaltungsbereich.getFk_geom().getGeo_field());
                            }

                            final VerwaltungsbereichCustomBean newVerwaltungsbereich = VerwaltungsbereichCustomBean
                                        .createNew();
                            newVerwaltungsbereich.setFk_verwaltende_dienststelle(
                                verwaltungsbereich.getFk_verwaltende_dienststelle());
                            newVerwaltungsbereich.setFk_geom(geomBean);
                            verwaltungsbereiche.add(newVerwaltungsbereich);
                        }
                        verwaltungsTableModel.refreshTableModel(verwaltungsbereiche);

                        zusatzRolleTableModel.refreshTableModel((Collection<ZusatzRolleCustomBean>)getCurrentObject()
                                    .getN_zusatz_rollen());

                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        // Wenn Flurstück nicht städtisch ist werden keine Geometrien der Karte hinzugefügt
                        if (isFlurstueckEditable) {
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        final ArrayList<Feature> features =
                                            verwaltungsTableModel.getAllVerwaltungsFeatures();
                                        if (features != null) {
                                            for (final Feature currentFeature : features) {
                                                if (currentFeature != null) {
                                                    if (isWidgetReadOnly()) {
                                                        ((VerwaltungsbereichCustomBean)currentFeature).setModifiable(
                                                            false);
                                                    }

                                                    final Feature tmp = new StyledFeatureGroupWrapper(
                                                            (StyledFeature)currentFeature,
                                                            PROVIDER_NAME,
                                                            PROVIDER_NAME);

                                                    LagisBroker.getInstance()
                                                            .getMappingComponent()
                                                            .getFeatureCollection()
                                                            .addFeature(tmp);
                                                }
                                            }
                                        }
                                    }
                                });
                        }
                        LagisBroker.getInstance().flurstueckChangeFinished(VerwaltungsPanel.this);
                    } catch (Exception ex) {
                        LOG.error("Fehler im refresh thread: ", ex);
                        LagisBroker.getInstance().flurstueckChangeFinished(VerwaltungsPanel.this);
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
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public List<BasicEntity> getCopyData() {
        final ArrayList<VerwaltungsbereichCustomBean> allVBs = (ArrayList<VerwaltungsbereichCustomBean>)this
                    .verwaltungsTableModel.getCidsBeans();
        final ArrayList<BasicEntity> result = new ArrayList<>(allVBs.size());

        for (final VerwaltungsbereichCustomBean vb : allVBs) {
            try {
                final VerwaltungsbereichCustomBean tmp = VerwaltungsbereichCustomBean.createNew();

                tmp.setDienststelle(vb.getDienststelle());

                final Geometry geom = vb.getGeometry();
                if (geom != null) {
                    tmp.setGeometry((Geometry)geom.clone());
                }

                tmp.setEditable(vb.isEditable());
                tmp.hide(vb.isHidden());
                tmp.setModifiable(vb.isModifiable());

                result.add(tmp);
            } catch (Exception ex) {
                LOG.error("error creating bean for verwaltungsbereiche", ex);
            }
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   item  vb DOCUMENT ME!
     *
     * @throws  NullPointerException  DOCUMENT ME!
     */
    @Override
    public void paste(final BasicEntity item) {
        if (item == null) {
            throw new NullPointerException("Given data item must not be null");
        }

        if (item instanceof VerwaltungsbereichCustomBean) {
            final ArrayList<VerwaltungsbereichCustomBean> residentVBs = (ArrayList<VerwaltungsbereichCustomBean>)this
                        .verwaltungsTableModel.getCidsBeans();

            if (residentVBs.contains(item)) {
                LOG.warn("Verwaltungsbereich " + item + " does already exist in Flurstück " + this.currentFlurstueck);
            } else {
                this.verwaltungsTableModel.addCidsBean((VerwaltungsbereichCustomBean)item);
                this.verwaltungsTableModel.fireTableDataChanged();

                final MappingComponent mc = LagisBroker.getInstance().getMappingComponent();
                final Feature f = new StyledFeatureGroupWrapper((StyledFeature)item, PROVIDER_NAME, PROVIDER_NAME);
                mc.getFeatureCollection().addFeature(f);
                mc.setGroupLayerVisibility(PROVIDER_NAME, true);
//                this.featureCollectionChanged();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   dataList  vbList DOCUMENT ME!
     *
     * @throws  NullPointerException  DOCUMENT ME!
     */
    @Override
    public void pasteAll(final List<BasicEntity> dataList) {
        if (dataList == null) {
            throw new NullPointerException("Given list of Verwaltungsbereich items must not be null");
        }

        if (dataList.isEmpty()) {
            return;
        }

        final ArrayList<VerwaltungsbereichCustomBean> residentVBs = (ArrayList<VerwaltungsbereichCustomBean>)this
                    .verwaltungsTableModel.getCidsBeans();
        final int rowCountBefore = this.verwaltungsTableModel.getRowCount();

        Feature f;
        final MappingComponent mc = LagisBroker.getInstance().getMappingComponent();
        final FeatureCollection featCollection = mc.getFeatureCollection();
        for (final BasicEntity entity : dataList) {
            if (entity instanceof VerwaltungsbereichCustomBean) {
                if (residentVBs.contains(entity)) {
                    LOG.warn("Verwaltungsbereich " + entity + " does already exist in Flurstück "
                                + this.currentFlurstueck);
                } else {
                    this.verwaltungsTableModel.addCidsBean((VerwaltungsbereichCustomBean)entity);
                    f = new StyledFeatureGroupWrapper((StyledFeature)entity, PROVIDER_NAME, PROVIDER_NAME);
                    featCollection.addFeature(f);
                }
            }
        }

        if (rowCountBefore == this.verwaltungsTableModel.getRowCount()) {
            LOG.warn("No Verwaltungsbereich items were added from input list " + dataList);
        } else {
            this.verwaltungsTableModel.fireTableDataChanged();
            mc.setGroupLayerVisibility(PROVIDER_NAME, true);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<JComponent> getCustomButtons() {
        final ArrayList<JComponent> tmp = new ArrayList<JComponent>();
        tmp.add(lblLastModification);
        tmp.add(hbBack);
        tmp.add(hbFwd);
        return tmp;
    }

    /**
     * Inserting in Interface functionalty (also VERDIS).
     */
    private void configureButtons() {
        hbBack = JHistoryButton.getDefaultJHistoryButton(
                JHistoryButton.DIRECTION_BACKWARD,
                JHistoryButton.ICON_SIZE_16,
                historyModel);
        hbFwd = JHistoryButton.getDefaultJHistoryButton(
                JHistoryButton.DIRECTION_FORWARD,
                JHistoryButton.ICON_SIZE_16,
                historyModel);

        hbBack.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        hbBack.setOpaque(false);

        hbFwd.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        hbFwd.setOpaque(false);
    }

    @Override
    public void backStatusChanged() {
    }

    @Override
    public void forwardStatusChanged() {
    }

    @Override
    public void historyActionPerformed() {
    }

    @Override
    public void historyChanged() {
        if ((historyModel != null) && (historyModel.getCurrentElement() != null)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("historyChanged:" + historyModel.getCurrentElement().toString());
            }
            if ((historyModel.getCurrentElement() != null)
                        && (!(historyModel.getCurrentElement().equals(
                                    LagisBroker.getInstance().getCurrentFlurstueck())))) {
                // historyEnabled=false;
                LagisBroker.getInstance()
                        .loadFlurstueck(((FlurstueckCustomBean)historyModel.getCurrentElement())
                            .getFlurstueckSchluessel());
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void configureTable() {
        // TODO NUllSAVe
        // tableModel.setVerwaltendenDienstellenList(allVerwaltendeDienstellen);
        // bleModel.setVerwaltungsGebrauchList(allVerwaltungsgebraeuche);
        TableSelectionUtils.crossReferenceModelAndTable(verwaltungsTableModel, (VerwaltungsTable)tNutzung);
        final JComboBox cboVD = new JComboBox(new Vector<VerwaltendeDienststelleCustomBean>(
                    CidsBroker.getInstance().getAllVerwaltendeDienstellen()));
        cboVD.setEditable(true);
        cboVD.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    cboVDActionPerformed();
                }
            });

        tNutzung.setDefaultRenderer(VerwaltendeDienststelleCustomBean.class, vdRenderer);
        tNutzung.setDefaultRenderer(Integer.class, new FlaecheRenderer());
        tNutzung.setDefaultEditor(VerwaltendeDienststelleCustomBean.class, new ComboBoxCellEditor(cboVD));
        tNutzung.setDefaultEditor(Integer.class, new FlaecheEditor());
        tNutzung.getSelectionModel().addListSelectionListener(this);

        TableSelectionUtils.crossReferenceModelAndTable(zusatzRolleTableModel, (ZusatzRolleTable)tZusatzRolle);
        final JComboBox cboZRD = new JComboBox(CidsBroker.getInstance().getAllVerwaltendeDienstellen().toArray(
                    new VerwaltendeDienststelleCustomBean[0]));
        cboZRD.setEditable(true);
        cboZRD.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    cboZRDActionPerformed();
                }
            });

        final JComboBox cboZRA = new JComboBox(new Vector<ZusatzRolleArtCustomBean>(
                    CidsBroker.getInstance().getAllZusatzRolleArten()));
        cboZRA.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    cboZRAActionPerformed();
                }
            });
        tZusatzRolle.setDefaultRenderer(VerwaltendeDienststelleCustomBean.class, vdRenderer);
        tZusatzRolle.setDefaultRenderer(ZusatzRolleArtCustomBean.class, new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(final JTable table,
                        final Object value,
                        final boolean isSelected,
                        final boolean hasFocus,
                        final int row,
                        final int column) {
                    final JLabel component = (JLabel)super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column);
                    component.setEnabled(true);
                    return component;
                }
            });
        tZusatzRolle.setDefaultEditor(VerwaltendeDienststelleCustomBean.class, new ComboBoxCellEditor(cboZRD));
        tZusatzRolle.setDefaultEditor(ZusatzRolleArtCustomBean.class, new ComboBoxCellEditor(cboZRA));
        tZusatzRolle.getSelectionModel().addListSelectionListener(this);

        final HighlightPredicate noGeometryPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    try {
                        final int displayedIndex = componentAdapter.row;
                        final int modelIndex = ((JXTable)tNutzung).convertRowIndexToModel(displayedIndex);
                        final VerwaltungsbereichCustomBean g = verwaltungsTableModel.getCidsBeanAtRow(modelIndex);
                        // TODO warum muss g != null sein muss nicht geodert werden?
                        return (((g == null) || ((g != null) && (g.getGeometry() == null)))
                                        && ((verwaltungsTableModel.getCidsBeans() != null)
                                            && (verwaltungsTableModel.getRowCount() != 1)));
                    } catch (Exception ex) {
                        LOG.error("Fehler beim Highlighting test noGeometry", ex);
                        return false;
                    }
                }
            };

        final Highlighter noGeometryHighlighter = new ColorHighlighter(noGeometryPredicate, LagisBroker.grey, null);

        // TODO logging entfernen
        // (LagisBroker.ERROR_COLOR, null, 0, -1) {
        final HighlightPredicate geometrySizeDifferentPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    try {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Teste Geometriegröße");
                        }
                        final double currentGeometrySize;
                        if (currentGeometry == null) {
                            if (!lblWFSInfo.getIcon().equals(icoWFSWarn)) {
                                lblWFSInfo.setIcon(icoWFSLoad);
                                lblWFSInfo.setToolTipText("WFS Geometrie wird geladen");
                                // tableModel.setCurrentWFSSize(0);//auskommentiert weil dadurch eine endlosschleife
                                // entsteht (wird schon im PropertyChangeListener in dieser klasse zeile 318 gemacht)
                            }
                            return true;
                        } else {
                            // currentGeometrySize= (int)Math.round(currentGeometry.getArea());
                            currentGeometrySize = currentGeometry.getArea();
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Größe WFS Geometrie: " + currentGeometrySize);
                        }
                        double geomSum = 0;
                        int counter = 0;
                        for (final Feature currentFeature : verwaltungsTableModel.getAllVerwaltungsFeatures()) {
                            final Geometry tmpGeometry = currentFeature.getGeometry();
                            if (currentGeometry != null) {
                                geomSum += tmpGeometry.getArea();
                            }
                            counter++;
                        }
                        if (counter < 1) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                    "es ist nur ein Verwaltungsbereich vorhanden --> automatische WFS größe --> keine Überprüfung");
                            }
                            // tableModel.setCurrentWFSSize(currentGeometrySize); //auskommentiert weil dadurch eine
                            // endlosschleife entsteht (wird schon im PropertyChangeListener in dieser klasse zeile 318
                            // gemacht)
                            lblWFSInfo.setIcon(icoWFSSizeGood);
                            lblWFSInfo.setToolTipText("Automatische Zuordnung");
                            return false;
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Summe aller Verwaltungsbereiche: " + geomSum);
                        }
                        final double diff = ((int)(Math.abs(currentGeometrySize - geomSum) * 100.0)) / 100.0;
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Differenz = " + diff);
                        }
                        if (diff == 0.0) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Flächen sind gleich");
                            }
                            lblWFSInfo.setIcon(icoWFSSizeGood);
                            lblWFSInfo.setToolTipText("Summe der Angelegten Flächen, sind gleich der WFS Fläche");
                            return false;
                        } else if (diff <= 1.0) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Flächen sind fast gleich");
                            }
                            lblWFSInfo.setIcon(icoWFSSizeTolerated);
                            lblWFSInfo.setToolTipText(
                                "Summe der Angelegten Flächen, sind fast gleich der WFS Fläche. Differenz <= 1 m²: "
                                        + diff);
                            return false;
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Flächen sind nicht gleich");
                            }
                            lblWFSInfo.setIcon(icoWFSSizeBad);
                            lblWFSInfo.setToolTipText("Unterschiedliche Flächen. WFS: "
                                        + (int)Math.round(currentGeometrySize) + "m² Verwaltungsbereiche: "
                                        + (int)Math.round(geomSum) + "m²");
                            return true;
                        }
                    } catch (Exception ex) {
                        LOG.error("Fehler beim Highlight test geometrySize", ex);
                        lblWFSInfo.setIcon(icoWFSWarn);
                        lblWFSInfo.setToolTipText("Fehler beim vergleichen der Flächen");
                        return true;
                    }
                }
            };

        final Highlighter geometrySizeDifferentHighlighter = new ColorHighlighter(
                geometrySizeDifferentPredicate,
                LagisBroker.ERROR_COLOR,
                null);
        // HighlighterPipeline hPipline = new HighlighterPipeline(new
        // Highlighter[]{LagisBroker.ALTERNATE_ROW_HIGHLIGHTER, noGeometryHighlighter, geometrySizeDifferent});
        ((JXTable)tNutzung).setHighlighters(
            LagisBroker.ALTERNATE_ROW_HIGHLIGHTER,
            noGeometryHighlighter,
            geometrySizeDifferentHighlighter);
        ((JXTable)tNutzung).setSortOrder(0, SortOrder.ASCENDING);
        ((JXTable)tNutzung).packAll();
        ((VerwaltungsTable)tNutzung).setSortButton(tbtnSort);
        ((VerwaltungsTable)tNutzung).setUndoButton(btnUndo);
        ((JXTable)tZusatzRolle).setHighlighters(
            LagisBroker.ALTERNATE_ROW_HIGHLIGHTER);
        ((JXTable)tZusatzRolle).setSortOrder(0, SortOrder.ASCENDING);
        ((JXTable)tZusatzRolle).packAll();
    }

    /**
     * DOCUMENT ME!
     */
    private void initModels() {
        bemerkungDocumentModel = new SimpleDocumentModel() {

                @Override
                public void assignValue(final String newValue) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Bemerkung assigned");
                        LOG.debug("new Value: " + newValue);
                    }
                    valueToCheck = newValue;
                    fireValidationStateChanged(this);
                    if (((currentFlurstueck != null) && (getStatus() == Validatable.VALID))
                                || (getStatus() == Validatable.WARNING)) {
                        LOG.info("Entität wirklich geändert");
                        currentFlurstueck.setBemerkung(newValue);
                    }
                }
            };
        txtBemerkung.setDocument(bemerkungDocumentModel);
        valTxtBemerkung.reSetValidator((Validatable)bemerkungDocumentModel);
        validators.add(valTxtBemerkung);
    }

    // private Thread panelRefresherThread;
    @Override
    public void flurstueckChanged(final FlurstueckCustomBean newFlurstueck) {
        try {
            LOG.info("FlurstueckChanged");
            currentFlurstueck = newFlurstueck;
            zusatzRolleTableModel.setCidsBeans((currentFlurstueck != null)
                    ? (List)currentFlurstueck.getN_zusatz_rollen() : null);
            btnHistorie.setEnabled(!currentFlurstueck.getVerwaltungsbereicheHistorie().isEmpty());
            updateThread.notifyThread(currentFlurstueck);
        } catch (Exception ex) {
            LOG.error("Fehler beim Flurstückswechsel: ", ex);
            LagisBroker.getInstance().flurstueckChangeFinished(VerwaltungsPanel.this);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshReBeIcons() {
        try {
            final Collection<RebeCustomBean> reBe = currentFlurstueck.getRechteUndBelastungen();
            final Iterator<RebeCustomBean> it = reBe.iterator();
            boolean allRechteExpired = true;
            boolean oneRechtExisiting = false;
            boolean allBelastungenExpired = true;
            boolean oneBelastungExisiting = false;
            final Date currentDate = new Date();
            while (it.hasNext()) {
                final RebeCustomBean curReBe = it.next();
                final boolean curReBeArt = curReBe.getIstRecht();
                if (curReBeArt) {
                    if ((curReBe.getDatumLoeschung() == null)
                                || ((curReBe.getDatumLoeschung() != null)
                                    && (currentDate.compareTo(curReBe.getDatumLoeschung()) <= 0))) {
                        allRechteExpired = false;
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(lblRechte);
                        LOG.debug("aktuelle ReBe ist recht");
                    }
                    oneRechtExisiting = true;
                } else {
                    if ((curReBe.getDatumLoeschung() == null)
                                || ((curReBe.getDatumLoeschung() != null)
                                    && (currentDate.compareTo(curReBe.getDatumLoeschung()) <= 0))) {
                        allBelastungenExpired = false;
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(lblBelastungen);
                    }
                    oneBelastungExisiting = true;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("aktuelle ReBe ist Belastung");
                    }
                }
            }
            if (allBelastungenExpired) {
                lblBelastungen.setIcon(this.icoRebeExpired);
                lblBelastungen.setToolTipText("Alle Belastungen sind gelöscht");
            } else {
                lblBelastungen.setIcon(icoBelastung);
                lblBelastungen.setToolTipText("Es sind Belastungen vorhanden");
            }
            if (allRechteExpired) {
                lblRechte.setIcon(this.icoRebeExpired);
                lblRechte.setToolTipText("Alle Rechte sind gelöscht");
            } else {
                lblRechte.setIcon(icoRecht);
                lblRechte.setToolTipText("Es sind Rechte vorhanden");
            }
            if (oneBelastungExisiting) {
                lblBelastungen.setVisible(true);
            }
            if (oneRechtExisiting) {
                lblRechte.setVisible(true);
            }
        } catch (Exception ex) {
            LOG.warn("Fehler beim setzen der Rebe Icons", ex);
            clearReBeIcons();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void clearReBeIcons() {
        lblRechte.setVisible(false);
        lblBelastungen.setVisible(false);
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
        if (isFlurstueckEditable) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Verwaltung --> setComponentEditable");
            }

            isInEditMode = isEditable;
            txtBemerkung.setEditable(isEditable);
            cbSperre.setEnabled(isEditable);

            final TableCellEditor currentNutzungEditor = tNutzung.getCellEditor();
            if (currentNutzungEditor != null) {
                currentNutzungEditor.cancelCellEditing();
            }
            btnAddVerwaltung.setEnabled(isEditable);
            if (isEditable && (tNutzung.getSelectedRow() != -1)) {
                btnRemoveVerwaltung.setEnabled(true);
            } else if (!isEditable) {
                btnRemoveVerwaltung.setEnabled(false);
            }
            verwaltungsTableModel.setInEditMode(isEditable);

            final TableCellEditor currentRolleEditor = tZusatzRolle.getCellEditor();
            if (currentRolleEditor != null) {
                currentRolleEditor.cancelCellEditing();
            }
            tZusatzRolle.setEnabled(isEditable);

            btnAddZusatzRolle.setEnabled(isEditable);
            if (isEditable && (tZusatzRolle.getSelectedRow() != -1)) {
                btnRemoveZusatzRolle.setEnabled(true);
            } else if (!isEditable) {
                btnRemoveZusatzRolle.setEnabled(false);
            }
            zusatzRolleTableModel.setInEditMode(isEditable);

            btnUndo.setEnabled(false);
            if (LOG.isDebugEnabled()) {
//        HighlighterPipeline pipeline = ((JXTable)tNutzung).getHighlighters();
//        if(isEditable){
//        pipeline.removeHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_DEFAULT);
//        pipeline.addHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_EDIT,false);
//        } else {
//        pipeline.removeHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_EDIT);
//        pipeline.addHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_DEFAULT,false);
//        }
                LOG.debug("Verwaltung --> setComponentEditable finished");
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Flurstück ist nicht städtisch Verwaltungen können nicht editiert werden");
            }
        }
    }

    @Override
    public synchronized void clearComponent() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Clear Verwaltungspanel");
        }
        clearReBeIcons();
        lblLastModification.setToolTipText(null);
        // TODOTabelle löschen wenn model vorhanden
        cbSperre.setSelected(false);
        try {
            // txtBemerkung.setText("");
            bemerkungDocumentModel.clear(0, bemerkungDocumentModel.getLength());
        } catch (BadLocationException ex) {
            LOG.warn("Fehler beim cleanen der Komponente", ex);
        }
        lblBemSperre.setText("");
        verwaltungsTableModel.refreshTableModel(new HashSet<VerwaltungsbereichCustomBean>());
        zusatzRolleTableModel.refreshTableModel(new HashSet<ZusatzRolleCustomBean>());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Clear Verwaltungspanel beendet");
        }
    }

    @Override
    public Vector<GeometrySlotInformation> getSlotInformation() {
        // VerwaltungsTableModel tmp = (VerwaltungsTableModel) tNutzung.getModel();
        final Vector<GeometrySlotInformation> result = new Vector<GeometrySlotInformation>();
        if (isWidgetReadOnly()) {
            return result;
        } else {
            final int rowCount = verwaltungsTableModel.getRowCount();
            if ((rowCount == 1) || !isFlurstueckEditable) {
                return result;
            }
            for (int i = 0; i < rowCount; i++) {
                final VerwaltungsbereichCustomBean currentBereich = verwaltungsTableModel.getCidsBeanAtRow(i);

                if ((currentBereich != null) && (currentBereich.getGeometry() == null)) {
                    final Object idValue1 = verwaltungsTableModel.getValueAt(i, 0);
                    final Object idValue2 = verwaltungsTableModel.getValueAt(i, 1);
                    String identifer;
                    if ((idValue1 != null) && (idValue2 != null)) {
                        identifer = idValue1.toString() + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR()
                                    + idValue2.toString();
                    } else if (idValue1 != null) {
                        identifer = idValue1.toString() + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR()
                                    + "Kein Verwaltungsgebrauch";
                    } else if (idValue2 != null) {
                        identifer = "Kein Ressort" + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR()
                                    + idValue2.toString();
                    } else {
                        identifer = "Kein Ressort" + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR()
                                    + "Kein Verwaltungsgebrauch";
                    }
                    result.add(new GeometrySlotInformation(getProviderName(), identifer, currentBereich, this));
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
    public void refresh(final Object refreshObject) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Verwaltungsbereich refreshed");
        }
        final VerwaltungsTableModel model = ((VerwaltungsTableModel)tNutzung.getModel());
        // model.updateAreaInformation(null);
// EventQueue.invokeLater(new Runnable() {
// public void run() {
        model.fireTableDataChanged();
        tNutzung.repaint();
    }

    @Override
    public void updateFlurstueckForSaving(final FlurstueckCustomBean flurstueck) {
        verwaltungsTableModel.fillFlaechen();
        flurstueck.setVerwaltungsbereiche((Collection<VerwaltungsbereichCustomBean>)
            verwaltungsTableModel.getCidsBeans());

        final Collection<ZusatzRolleCustomBean> oldRollen = new ArrayList<>(flurstueck.getN_zusatz_rollen());
        final Collection<ZusatzRolleCustomBean> newRollen = (List<ZusatzRolleCustomBean>)
            zusatzRolleTableModel.getCidsBeans();

        // alle Rollen hinzufügen die vorher noch nicht existiert haben
        for (final ZusatzRolleCustomBean newRolle : newRollen) {
            if (!oldRollen.contains(newRolle)) {
                flurstueck.getN_zusatz_rollen().add(newRolle);
                oldRollen.remove(newRolle);
            }
        }

        // alle Rollen entfernen die nicht mehr exisiteren
        for (final ZusatzRolleCustomBean oldRolle : oldRollen) {
            if (!newRollen.contains(oldRolle)) {
                flurstueck.getN_zusatz_rollen().remove(oldRolle);
            }
        }
    }

    @Override
    public String getWidgetName() {
        return WIDGET_NAME;
    }

    // TODO multiple Selection
    // TODO refactor code --> poor style
    @Override
    public synchronized void featureSelectionChanged(final Collection<Feature> features) {
        ((VerwaltungsTable)tNutzung).featureSelectionChanged(this, features, VerwaltungsbereichCustomBean.class);
    }

    // TODO WHAT IS IT GOOD FOR
    @Override
    public void stateChanged(final ChangeEvent e) {
    }

    // ToDo multiple Selection
    @Override
    public synchronized void valueChanged(final ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == true) {
            return;
        }
        if (e.getSource().equals(tNutzung.getSelectionModel())) {
            btnRemoveVerwaltung.setEnabled((tNutzung.getSelectedRow() != -1) && isInEditMode);
            if (tNutzung.getSelectedRow() != -1) {
                ((VerwaltungsTable)tNutzung).valueChanged_updateFeatures(this, e);
            }
            this.setFeatureSelectionChangedEnabled(true);
        } else if (e.getSource().equals(tZusatzRolle.getSelectionModel())) {
            btnRemoveZusatzRolle.setEnabled((tZusatzRolle.getSelectedRow() != -1) && isInEditMode);
            this.setFeatureSelectionChangedEnabled(true);
        }
    }

    @Override
    public Element getConfiguration() {
        return null;
    }

    @Override
    public void masterConfigure(final Element parent) {
        try {
            final Element htmlTooltip = parent.getChild("HTMLTooltips");
            final List<Element> tooltips = htmlTooltip.getChildren();
            final Iterator<Element> it = tooltips.iterator();
            while (it.hasNext()) {
                final Element tmpTooltip = it.next();
                if (tmpTooltip.getChild("id").getText().equals("Verwaltendedienststelle")) {
                    final Element copy = (Element)tmpTooltip.clone();
                    copy.detach();
                    vdRenderer.setHTMLTooltip(copy);
                }
            }
        } catch (Exception ex) {
            LOG.warn("Fehler beim lesen der htmlTooltips", ex);
        }
    }

    @Override
    public void configure(final Element parent) {
    }

    // TODO USE
    @Override
    public Icon getWidgetIcon() {
        return null;
    }

    @Override
    public int getStatus() {
        try {
            if (tNutzung.getCellEditor() != null) {
                validationMessage = "Bitte vollenden Sie alle Änderungen bei den Verwaltungsbereichen.";
                return Validatable.ERROR;
            }
            final Iterator<Validator> it = validators.iterator();
            while (it.hasNext()) {
                final Validator current = it.next();
                if (current.getValidationState() != Validatable.VALID) {
                    validationMessage = current.getValidationMessage();
                    return Validatable.ERROR;
                }
            }
            final ArrayList<VerwaltungsbereichCustomBean> allVerwaltung = (ArrayList<VerwaltungsbereichCustomBean>)
                verwaltungsTableModel.getCidsBeans();
            final Iterator<VerwaltungsbereichCustomBean> itVerwaltung = allVerwaltung.iterator();
            while (itVerwaltung.hasNext()) {
                final VerwaltungsbereichCustomBean current = itVerwaltung.next();
                if ((allVerwaltung.size() == 1) && ((current != null) && (current.getGeometry() != null))) {
                    validationMessage = "Wenn ein Verwaltungsbereich vorhanden ist, dann darf\n"
                                + "diesem keine Geometrie zugeordnet sein.";
                    return Validatable.ERROR;
                }
                if ((current.getDienststelle() == null)) {
                    // TODO use validator
                    validationMessage = "Für jeden Verwaltungsbereich muss die Dienstellefestgelegt werden.";
                    return Validatable.ERROR;
                }
                if ((allVerwaltung.size() > 1) && ((current == null) || (current.getGeometry() == null))) {
                    validationMessage = "Wenn mehr als zwei Verwaltungsbereiche vorhanden sind,\n"
                                + "müssen allen Bereichen Geometrien zugeordnet werden.";
                    return Validatable.ERROR;
                }
            }
            return Validatable.VALID;
        } catch (Exception ex) {
            LOG.error("Fehler beim validieren des Verwaltungsbereichs");
            validationMessage = "Es ist ein Fehler beim validieren der Verwaltungsbereiche aufgetreten.";
            return Validatable.ERROR;
        }
    }
    // TODO Massive TRAFFIC perhaps checking

    @Override
    public void validationStateChanged(final Object validatedObject) {
        fireValidationStateChanged(validatedObject);
        tNutzung.repaint();
    }

    @Override
    public void featureCollectionChanged() {
        tNutzung.repaint();
    }

    @Override
    public void featuresRemoved(final FeatureCollectionEvent fce) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Features Removed");
        }
        tNutzung.repaint();
    }

    @Override
    public void featuresChanged(final FeatureCollectionEvent fce) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("FeaturesChanges Verwaltung");
        }
        tNutzung.repaint();
    }

    @Override
    public void featuresAdded(final FeatureCollectionEvent fce) {
        tNutzung.repaint();
    }

    @Override
    public void featureSelectionChanged(final FeatureCollectionEvent fce) {
        tNutzung.repaint();
    }

    @Override
    public void featureReconsiderationRequested(final FeatureCollectionEvent fce) {
        tNutzung.repaint();
    }

    @Override
    public void allFeaturesRemoved(final FeatureCollectionEvent fce) {
        tNutzung.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btnAddVerwaltung = new javax.swing.JButton();
        btnRemoveVerwaltung = new javax.swing.JButton();
        btnUndo = new javax.swing.JButton();
        tbtnSort = new javax.swing.JToggleButton();
        btnHistorie = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tNutzung = new VerwaltungsTable();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tZusatzRolle = new de.cismet.lagis.gui.tables.ZusatzRolleTable();
        jPanel8 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        btnAddZusatzRolle = new javax.swing.JButton();
        btnRemoveZusatzRolle = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtBemerkung = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        lblBemSperre = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cbSperre = new javax.swing.JCheckBox();
        lblBelastungen = new javax.swing.JLabel();
        lblWFSInfo = new javax.swing.JLabel();
        lblRechte = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jPanel5.setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel3.setMaximumSize(new java.awt.Dimension(10, 10));

        final org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(0, 0, Short.MAX_VALUE));
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jPanel3, gridBagConstraints);

        btnAddVerwaltung.setAction(((VerwaltungsTable)tNutzung).getAddAction());
        btnAddVerwaltung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/add.png"))); // NOI18N
        btnAddVerwaltung.setBorder(null);
        btnAddVerwaltung.setBorderPainted(false);
        btnAddVerwaltung.setMaximumSize(new java.awt.Dimension(25, 25));
        btnAddVerwaltung.setMinimumSize(new java.awt.Dimension(25, 25));
        btnAddVerwaltung.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel2.add(btnAddVerwaltung, gridBagConstraints);

        btnRemoveVerwaltung.setAction(((VerwaltungsTable)tNutzung).getRemoveAction());
        btnRemoveVerwaltung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/remove.png"))); // NOI18N
        btnRemoveVerwaltung.setBorder(null);
        btnRemoveVerwaltung.setBorderPainted(false);
        btnRemoveVerwaltung.setMaximumSize(new java.awt.Dimension(25, 25));
        btnRemoveVerwaltung.setMinimumSize(new java.awt.Dimension(25, 25));
        btnRemoveVerwaltung.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel2.add(btnRemoveVerwaltung, gridBagConstraints);

        btnUndo.setAction(((VerwaltungsTable)tNutzung).getUndoAction());
        btnUndo.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/undo.png"))); // NOI18N
        btnUndo.setToolTipText("Rückgängig machen");
        btnUndo.setBorderPainted(false);
        btnUndo.setMaximumSize(new java.awt.Dimension(25, 25));
        btnUndo.setMinimumSize(new java.awt.Dimension(25, 25));
        btnUndo.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel2.add(btnUndo, gridBagConstraints);

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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        jPanel2.add(tbtnSort, gridBagConstraints);
        tbtnSort.addItemListener(((VerwaltungsTable)tNutzung).getSortItemListener());

        btnHistorie.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/history.png"))); // NOI18N
        btnHistorie.setToolTipText("Historie der Verwaltungsbereiche");
        btnHistorie.setBorderPainted(false);
        btnHistorie.setContentAreaFilled(false);
        btnHistorie.setMaximumSize(new java.awt.Dimension(25, 25));
        btnHistorie.setMinimumSize(new java.awt.Dimension(25, 25));
        btnHistorie.setPreferredSize(new java.awt.Dimension(25, 25));
        btnHistorie.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnHistorieActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        jPanel2.add(btnHistorie, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel5.add(jPanel2, gridBagConstraints);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tNutzung.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        tNutzung.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] { "Title 1", "Title 2", "Title 3" }));
        jScrollPane1.setViewportView(tNutzung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jPanel5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 5, 0);
        jPanel4.add(jSeparator3, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("zusätzliche Rollen:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel1.add(jLabel2, gridBagConstraints);

        jScrollPane3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tZusatzRolle.setAutoCreateRowSorter(true);
        tZusatzRolle.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        tZusatzRolle.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {},
                new String[] { "Title 1", "Title 2" }));
        jScrollPane3.setViewportView(tZusatzRolle);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPane3, gridBagConstraints);

        jPanel8.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel8.add(jLabel3, gridBagConstraints);

        btnAddZusatzRolle.setAction(((de.cismet.lagis.gui.tables.ZusatzRolleTable)tZusatzRolle).getAddAction());
        btnAddZusatzRolle.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/add.png"))); // NOI18N
        btnAddZusatzRolle.setBorder(null);
        btnAddZusatzRolle.setBorderPainted(false);
        btnAddZusatzRolle.setMaximumSize(new java.awt.Dimension(25, 25));
        btnAddZusatzRolle.setMinimumSize(new java.awt.Dimension(25, 25));
        btnAddZusatzRolle.setPreferredSize(new java.awt.Dimension(25, 25));
        btnAddZusatzRolle.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddZusatzRolleActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel8.add(btnAddZusatzRolle, gridBagConstraints);

        btnRemoveZusatzRolle.setAction(((de.cismet.lagis.gui.tables.ZusatzRolleTable)tZusatzRolle).getRemoveAction());
        btnRemoveZusatzRolle.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/remove.png"))); // NOI18N
        btnRemoveZusatzRolle.setBorder(null);
        btnRemoveZusatzRolle.setBorderPainted(false);
        btnRemoveZusatzRolle.setMaximumSize(new java.awt.Dimension(25, 25));
        btnRemoveZusatzRolle.setMinimumSize(new java.awt.Dimension(25, 25));
        btnRemoveZusatzRolle.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel8.add(btnRemoveZusatzRolle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel1.add(jPanel8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 5, 0);
        jPanel4.add(jSeparator2, gridBagConstraints);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        txtBemerkung.setColumns(20);
        txtBemerkung.setLineWrap(true);
        txtBemerkung.setRows(1);
        jScrollPane2.setViewportView(txtBemerkung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel6.add(jScrollPane2, gridBagConstraints);

        jLabel1.setText("Bemerkung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(jLabel1, gridBagConstraints);

        jPanel7.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        jPanel7.add(lblBemSperre, gridBagConstraints);

        jLabel4.setText("Sperre:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        jPanel7.add(jLabel4, gridBagConstraints);

        cbSperre.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbSperre.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbSperreActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel7.add(cbSperre, gridBagConstraints);

        lblBelastungen.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/belastung.png"))); // NOI18N
        lblBelastungen.setToolTipText("Es sind Belastungen vorhanden");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel7.add(lblBelastungen, gridBagConstraints);

        lblWFSInfo.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/wfs_green.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel7.add(lblWFSInfo, gridBagConstraints);

        lblRechte.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/recht.png"))); // NOI18N
        lblRechte.setToolTipText("Es sind Rechte vorhanden");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel7.add(lblRechte, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel6.add(jPanel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jPanel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        add(jPanel4, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbSperreActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbSperreActionPerformed
// TODO add your handling code here:
        if (currentFlurstueck != null) {
            final boolean isGesperrt = cbSperre.isSelected();
            if (isGesperrt) {
                // TODO Länge begrenzen
                String answer = null;
                while ((answer == null) || (answer.trim().length() == 0)) {
                    answer = JOptionPane.showInputDialog(this, "Bitte eine Bemerkung zur Sperre angeben.");
                    if (answer == null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Sperre setzen abgebrochen");
                        }
                        cbSperre.setSelected(false);
                        return;
                    }
                }
                // datamodell refactoring 22.10.07
                currentFlurstueck.getFlurstueckSchluessel().setIstGesperrt(isGesperrt);
                currentFlurstueck.getFlurstueckSchluessel().setBemerkungSperre(answer);
                lblBemSperre.setText(answer);
            } else {
                lblBemSperre.setText("");
                currentFlurstueck.getFlurstueckSchluessel().setIstGesperrt(isGesperrt);
                currentFlurstueck.getFlurstueckSchluessel().setBemerkungSperre("");
            }
            currentFlurstueck.getFlurstueckSchluessel().setIstGesperrt(isGesperrt);
        } else {
            LOG.error("Kann Sperre nicht setzen Flurstueck ist null");
        }
    } //GEN-LAST:event_cbSperreActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnHistorieActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnHistorieActionPerformed
        final VerwaltungsbereicheHistorieDialog verwaltungsHistorieDialog = new VerwaltungsbereicheHistorieDialog(
                currentFlurstueck);
        verwaltungsHistorieDialog.pack();
        StaticSwingTools.showDialog(verwaltungsHistorieDialog);
    }                                                                               //GEN-LAST:event_btnHistorieActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddZusatzRolleActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddZusatzRolleActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_btnAddZusatzRolleActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void cboVDActionPerformed() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("cboVerwaltungActionPerformed");
        }
        final TableCellEditor currentEditor = tNutzung.getCellEditor();
        if (currentEditor != null) {
            currentEditor.stopCellEditing();
        }
        for (final Feature feature
                    : (Collection<Feature>)LagisBroker.getInstance().getMappingComponent().getFeatureCollection()
                    .getSelectedFeatures()) {
            LagisBroker.getInstance().getMappingComponent().getFeatureCollection().reconsiderFeature(feature);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void cboZRDActionPerformed() {
        final TableCellEditor currentEditor = tZusatzRolle.getCellEditor();
        if (currentEditor != null) {
            currentEditor.stopCellEditing();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void cboZRAActionPerformed() {
        final TableCellEditor currentEditor = tZusatzRolle.getCellEditor();
        if (currentEditor != null) {
            currentEditor.stopCellEditing();
        }
    }

    @Override
    public String getDisplayName(final BasicEntity entity) {
        if (entity instanceof VerwaltungsbereichCustomBean) {
            final VerwaltungsbereichCustomBean vb = (VerwaltungsbereichCustomBean)entity;
            return "Verwaltende Dienststelle - "
                        + vb.getDienststelle().toString()
                        + " - "
                        + vb.getFlaeche() + "m²";
        }

        return Copyable.UNKNOWN_ENTITY;
    }

    @Override
    public Icon getDisplayIcon() {
        return this.copyDisplayIcon;
    }

    @Override
    public boolean knowsDisplayName(final BasicEntity entity) {
        return entity instanceof VerwaltungsbereichCustomBean;
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
