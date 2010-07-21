/*
 * VerwaltungsPanel.java
 *
 * Created on 16. März 2007, 12:24
 */
package de.cismet.lagis.gui.panels;

import com.vividsolutions.jts.geom.Geometry;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollectionEvent;
import de.cismet.cismap.commons.features.FeatureCollectionListener;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.editor.FlaecheEditor;
import de.cismet.lagis.interfaces.FeatureSelectionChangedListener;
import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.FlurstueckSaver;
import de.cismet.lagis.interfaces.GeometrySlotProvider;
import de.cismet.lagis.models.VerwaltungsTableModel;
import de.cismet.lagis.models.documents.SimpleDocumentModel;
import de.cismet.lagis.renderer.FlaecheRenderer;
import de.cismet.lagis.renderer.VerwaltungsgebrauchRenderer;
import de.cismet.lagis.thread.BackgroundUpdateThread;
import de.cismet.lagis.thread.WFSRetrieverFactory;
import de.cismet.lagis.utillity.GeometrySlotInformation;
import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.ValidationStateChangedListener;
import de.cismet.lagis.validation.Validator;
import de.cismet.lagis.widget.AbstractWidget;
import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.lagisEE.entity.core.ReBe;
import de.cismet.lagisEE.entity.core.Verwaltungsbereich;
import de.cismet.lagisEE.entity.core.hardwired.FlurstueckArt;
import de.cismet.lagisEE.entity.core.hardwired.VerwaltendeDienststelle;
import de.cismet.lagisEE.entity.core.hardwired.Verwaltungsgebrauch;
import de.cismet.tools.CurrentStackTrace;
import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.NoWriteError;
import de.cismet.tools.gui.historybutton.DefaultHistoryModel;
import de.cismet.tools.gui.historybutton.HistoryModelListener;
import de.cismet.tools.gui.historybutton.JHistoryButton;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.text.BadLocationException;
import javax.swing.text.DateFormatter;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.SortOrder;
import org.jdom.Element;

/**
 *
 * @author  Puhl
 */
public class VerwaltungsPanel extends AbstractWidget implements MouseListener, GeometrySlotProvider, FlurstueckSaver, FlurstueckChangeListener, FeatureSelectionChangedListener, ListSelectionListener, Configurable, ValidationStateChangedListener, FeatureCollectionListener, HistoryModelListener {

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private final Icon icoWFSSizeGood = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/wfs_green.png"));
    private final Icon icoWFSSizeBad = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/wfs_red.png"));
    private final Icon icoWFSSizeTolerated = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/wfs_yellow.png"));
    private final Icon icoWFSLoad = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/exec.png"));
    private final Icon icoWFSWarn = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/16warn.png"));
    private Flurstueck currentFlurstueck = null;
    private Validator valTxtBemerkung;
    private SimpleDocumentModel bemerkungDocumentModel;
    private VerwaltungsTableModel tableModel = new VerwaltungsTableModel();
    private boolean isInEditMode = false;
    private static final String PROVIDER_NAME = "Verwaltende Dienstelle";
    private static final String WIDGET_NAME = "Verwaltungspanel";
    private BackgroundUpdateThread<Flurstueck> updateThread;
    private VerwaltungsgebrauchRenderer vgRenderer = new VerwaltungsgebrauchRenderer();
    private JComboBox cboVD;
    private WFSRetrieverFactory.WFSWorkerThread currentWFSRetriever;
    private Geometry currentGeometry = null;
    //hierüber kann man ausfindig machen was bei AbteilungIX oder Städtisch passiert (falls gerefactored wird)
    private boolean isFlurstueckEditable = true;
    private JLabel lblLastModification;
    private JHistoryButton hbBack;
    private JHistoryButton hbFwd;
    private DefaultHistoryModel historyModel = new DefaultHistoryModel();
    protected boolean historyEnabled = true;

    /**
     * Creates new form VerwaltungsPanel
     */
    public VerwaltungsPanel() {
        setIsCoreWidget(true);
        initComponents();
        //tNutzung.setModel(new VerwaltungsTableModel());
        configureTable();
        valTxtBemerkung = new Validator(txtBemerkung);
        initModels();
        configBackgroundThread();
        lblLastModification = new JLabel();
        lblLastModification.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/goto.png")));
        lblLastModification.setOpaque(false);
        historyModel.addHistoryModelListener(this);
        configureButtons();
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
                    if (getCurrentObject().getFlurstueckSchluessel().getLetzter_bearbeiter() != null && getCurrentObject().getFlurstueckSchluessel().getLetzte_bearbeitung() != null) {
                        lblLastModification.setToolTipText(getCurrentObject().getFlurstueckSchluessel().getLetzter_bearbeiter() + " am " + LagisBroker.getDateFormatter().format(getCurrentObject().getFlurstueckSchluessel().getLetzte_bearbeitung()));
                    }else  if (getCurrentObject().getFlurstueckSchluessel().getLetzter_bearbeiter() != null) {
                        lblLastModification.setToolTipText(getCurrentObject().getFlurstueckSchluessel().getLetzter_bearbeiter());
                    } else {
                        lblLastModification.setToolTipText("Unbekannt");
                    }
                    if (getCurrentObject().getFlurstueckSchluessel() != null) {                        
                        historyModel.addToHistory(getCurrentObject());                                                    
                    }
                    FlurstueckArt flurstueckArt = getCurrentObject().getFlurstueckSchluessel().getFlurstueckArt();
                    if (flurstueckArt != null && flurstueckArt.getBezeichnung().equals(FlurstueckArt.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                        log.debug("Flurstück ist städtisch und kann editiert werden");
                        isFlurstueckEditable = true;
//                        cbKind.setIcon(icoStaedtisch);
//                        cbKind.setVisible(true);

                    } else if (flurstueckArt != null && flurstueckArt.getBezeichnung().equals(FlurstueckArt.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX)) {
                        log.debug("Flurstück ist nicht städtisch und kann nicht editiert werden (Abteilung IX)");
                        isFlurstueckEditable = false;
//                        cbKind.setIcon(icoAbteilungIX);
//                        cbKind.setVisible(true);
                    } else {
                        log.debug("Flurstück ist nicht städtisch und kann nicht editiert werden");
                        isFlurstueckEditable = false;
                    //cbKind.setVisible(false);
                    }
                    if (getCurrentObject().getVerwaltungsbereiche() != null || (getCurrentObject().getVerwaltungsbereiche() != null && getCurrentObject().getVerwaltungsbereiche().size() == 0)) {
                        lblWFSInfo.setIcon(icoWFSSizeGood);
                        lblWFSInfo.setToolTipText("Keine Verwaltungsbereiche vorhanden");
                    }
                    //Soll als Abteilungs IX Flurstück nicht angezeigt werden
                    if (isFlurstueckEditable) {
                        tNutzung.setVisible(true);
                    } else {
                        tNutzung.setVisible(false);
                    }

                    refreshReBeIcons();
                    if (currentWFSRetriever != null && !currentWFSRetriever.isDone()) {
                        currentWFSRetriever.cancel(true);                        currentWFSRetriever = null;
                        currentGeometry = null;
                    }
                    currentWFSRetriever = (WFSRetrieverFactory.WFSWorkerThread) WFSRetrieverFactory.getInstance().getWFSRetriever(getCurrentObject().getFlurstueckSchluessel(), null, null);
                    currentWFSRetriever.addPropertyChangeListener(new PropertyChangeListener() {

                        public void propertyChange(PropertyChangeEvent evt) {
                            if (evt.getSource() instanceof WFSRetrieverFactory.WFSWorkerThread && evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                                log.debug("Gemarkungsretriever done --> setzte geometrie");
                                try {
                                    currentGeometry = currentWFSRetriever.get();
                                    if (currentGeometry == null) {
                                        lblWFSInfo.setIcon(icoWFSWarn);
                                        lblWFSInfo.setToolTipText("Keine WFS Geometrie vorhanden");
                                        tableModel.setCurrentWFSSize(0);
                                    }
                                    tNutzung.repaint();
                                } catch (Exception ex) {
                                    log.error("Fehler beim abrufen der Geometrie", ex);
                                    currentGeometry = null;
                                    lblWFSInfo.setIcon(icoWFSWarn);
                                    lblWFSInfo.setToolTipText("Fehler beim vergleichen der Flächen");
                                    tableModel.setCurrentWFSSize(0);
                                }
                            }
                        }
                    });
                    currentWFSRetriever.execute();
                    String bemerkung = getCurrentObject().getBemerkung();
                    if (bemerkung != null) {
                        //txtBemerkung.setText(bemerkung);
                        bemerkungDocumentModel.insertString(0, bemerkung, null);
                    }
                    //datamodell refactoring 22.10.07
                    Boolean isGesperrt = getCurrentObject().getFlurstueckSchluessel().getIstGesperrt();
                    if (isGesperrt != null) {
                        cbSperre.setSelected(isGesperrt);
                        String sperrentext = getCurrentObject().getFlurstueckSchluessel().getBemerkungSperre();
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
                    log.debug("Anzahl verwaltungsbereiche: " + getCurrentObject().getVerwaltungsbereiche().size());
                    tableModel.refreshTableModel(getCurrentObject().getVerwaltungsbereiche());
                    if (isUpdateAvailable()) {
                        cleanup();
                        return;
                    }
                    //Wenn Flurstück nicht städtisch ist werden keine Geometrien der Karte hinzugefügt
                    if (isFlurstueckEditable) {
                        EventQueue.invokeLater(new Runnable() {

                            public void run() {
                                Vector<Feature> features = tableModel.getAllVerwaltungsFeatures();
                                if (features != null) {
                                    for (Feature currentFeature : features) {
                                        if (currentFeature != null) {
                                            if (isWidgetReadOnly()) {
                                                ((Verwaltungsbereich) currentFeature).setModifiable(false);
                                            }

                                            LagisBroker.getInstance().getMappingComponent().getFeatureCollection().addFeature(currentFeature);
                                        }
                                    }
                                }
                            }
                        });
                    }
                    LagisBroker.getInstance().flurstueckChangeFinished(VerwaltungsPanel.this);
                } catch (Exception ex) {
                    log.error("Fehler im refresh thread: ", ex);
                    LagisBroker.getInstance().flurstueckChangeFinished(VerwaltungsPanel.this);
                }
            }

            protected void cleanup() {
            }
        };
        updateThread.setPriority(Thread.NORM_PRIORITY);
        updateThread.start();
    }

    public Vector<JComponent> getCustomButtons() {
        Vector<JComponent> tmp = new Vector<JComponent>();
        tmp.add(lblLastModification);
        tmp.add(hbBack);
        tmp.add(hbFwd);
        return tmp;
    }

    //Inserting in Interface functionalty (also VERDIS)
    private void configureButtons() {
        hbBack = JHistoryButton.getDefaultJHistoryButton(JHistoryButton.DIRECTION_BACKWARD, JHistoryButton.ICON_SIZE_16, historyModel);
        hbFwd = JHistoryButton.getDefaultJHistoryButton(JHistoryButton.DIRECTION_FORWARD, JHistoryButton.ICON_SIZE_16, historyModel);

        hbBack.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        hbBack.setOpaque(false);


        hbFwd.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        hbFwd.setOpaque(false);        
    }

    public void backStatusChanged() {        
    }

    public void forwardStatusChanged() {   
    }

    public void historyActionPerformed() {        
    }

    public void historyChanged() {
        if (historyModel != null && historyModel.getCurrentElement() != null) {
            log.debug("historyChanged:" + historyModel.getCurrentElement().toString());
            if (historyModel.getCurrentElement() != null && (!(historyModel.getCurrentElement().equals(LagisBroker.getInstance().getCurrentFlurstueck())))) {
                //historyEnabled=false;
                LagisBroker.getInstance().loadFlurstueck(((Flurstueck) historyModel.getCurrentElement()).getFlurstueckSchluessel());
            }
        }
    }

    private void configureTable() {
        //TODO NUllSAVe
        //tableModel.setVerwaltendenDienstellenList(allVerwaltendeDienstellen);
        //bleModel.setVerwaltungsGebrauchList(allVerwaltungsgebraeuche);
        tNutzung.setModel(tableModel);
        cboVD = new JComboBox(new Vector<VerwaltendeDienststelle>(EJBroker.getInstance().getAllVerwaltendeDienstellen()));
        tNutzung.setDefaultRenderer(Verwaltungsgebrauch.class, vgRenderer);
        tNutzung.setDefaultEditor(VerwaltendeDienststelle.class, new DefaultCellEditor(cboVD));
        tNutzung.setDefaultRenderer(Integer.class, new FlaecheRenderer());
        tNutzung.setDefaultEditor(Integer.class, new FlaecheEditor());
        JComboBox cboVG = new JComboBox(new Vector<Verwaltungsgebrauch>(EJBroker.getInstance().getAllVerwaltenungsgebraeuche()));
        cboVG.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cboVGActionPerformed();
            }
        });
        //JComboBox cboVG = new JComboBox(new Vector<Verwaltungsgebrauch>(allVerwaltungsgebraeuche));
        cboVG.setEditable(true);
        ComboBoxCellEditor cellEditor = new ComboBoxCellEditor(cboVG);
        //AutoCompleteDecorator.decorate(cboVG);
        //tNutzung.setDefaultEditor(Verwaltungsgebrauch.class,new ComboBoxCellEditor(cboVG));
        tNutzung.getColumnModel().getColumn(1).setCellEditor(new ComboBoxCellEditor(cboVG));
        tNutzung.addMouseListener(this);
        //(LagisBroker.grey, null, 0, -1)
        HighlightPredicate noGeometryPredicate = new HighlightPredicate() {

            public boolean isHighlighted(Component renderer, ComponentAdapter componentAdapter) { 
                try {
                    int displayedIndex = componentAdapter.row;
                    int modelIndex = ((JXTable) tNutzung).getFilters().convertRowIndexToModel(displayedIndex);
                    Verwaltungsbereich g = tableModel.getVerwaltungsbereichAtRow(modelIndex);
                    //TODO warum muss g != null sein muss nicht geodert werden? 
                    return ((g == null || (g != null && g.getGeometry() == null)) && (tableModel.getVerwaltungsbereiche() != null && tableModel.getVerwaltungsbereiche().size() != 1));
                } catch (Exception ex) {
                    log.error("Fehler beim Highlighting test noGeometry", ex);
                    return false;
                }
            }
        };
        
        Highlighter noGeometryHighlighter = new ColorHighlighter(noGeometryPredicate, LagisBroker.grey, null);

        //TODO logging entfernen
        //(LagisBroker.ERROR_COLOR, null, 0, -1) {
        HighlightPredicate geometrySizeDifferentPredicate = new HighlightPredicate() {

            public boolean isHighlighted(Component renderer, ComponentAdapter componentAdapter) { 
                try {
                    log.debug("Teste Geometriegröße");
                    double currentGeometrySize;
                    if (currentGeometry == null) {
                        if (!lblWFSInfo.getIcon().equals(icoWFSWarn)) {
                            lblWFSInfo.setIcon(icoWFSLoad);
                            lblWFSInfo.setToolTipText("WFS Geometrie wird geladen");
                            tableModel.setCurrentWFSSize(0);
                        }
                        return true;
                    } else {
                        //currentGeometrySize= (int)Math.round(currentGeometry.getArea());
                        currentGeometrySize = currentGeometry.getArea();
                    }
                    log.debug("Größe WFS Geometrie: " + currentGeometrySize);
                    double geomSum = 0;
                    int counter = 0;
                    for (Feature currentFeature : tableModel.getAllVerwaltungsFeatures()) {
                        Geometry tmpGeometry = currentFeature.getGeometry();
                        if (currentGeometry != null) {
                            geomSum += tmpGeometry.getArea();
                        }
                        counter++;
                    }
                    if (counter < 1) {
                        log.debug("es ist nur ein Verwaltungsbereich vorhanden --> automatische WFS größe --> keine Überprüfung");
                        tableModel.setCurrentWFSSize(currentGeometrySize);
                        lblWFSInfo.setIcon(icoWFSSizeGood);
                        lblWFSInfo.setToolTipText("Automatische Zuordnung");
                        return false;
                    }
                    log.debug("Summe aller Verwaltungsbereiche: " + geomSum);
                    double diff = ((int) (Math.abs(currentGeometrySize - geomSum) * 100.0)) / 100.0;
                    log.debug("Differenz = " + diff);
                    if (diff == 0.0) {
                        log.debug("Flächen sind gleich");
                        lblWFSInfo.setIcon(icoWFSSizeGood);
                        lblWFSInfo.setToolTipText("Summe der Angelegten Flächen, sind gleich der WFS Fläche");
                        return false;
                    } else if (diff <= 1.0) {
                        log.debug("Flächen sind fast gleich");
                        lblWFSInfo.setIcon(icoWFSSizeTolerated);
                        lblWFSInfo.setToolTipText("Summe der Angelegten Flächen, sind fast gleich der WFS Fläche. Differenz <= 1 m²: " + diff);
                        return false;
                    } else {
                        log.debug("Flächen sind nicht gleich");
                        lblWFSInfo.setIcon(icoWFSSizeBad);
                        lblWFSInfo.setToolTipText("Unterschiedliche Flächen. WFS: " + (int) Math.round(currentGeometrySize) + "m² Verwaltungsbereiche: " + (int) Math.round(geomSum) + "m²");
                        return true;
                    }
                } catch (Exception ex) {
                    log.error("Fehler beim Highlight test geometrySize", ex);
                    lblWFSInfo.setIcon(icoWFSWarn);
                    lblWFSInfo.setToolTipText("Fehler beim vergleichen der Flächen");
                    return true;
                }
            }
        };
        Highlighter geometrySizeDifferentHighlighter = new ColorHighlighter(geometrySizeDifferentPredicate, LagisBroker.ERROR_COLOR, null);
        //HighlighterPipeline hPipline = new HighlighterPipeline(new Highlighter[]{LagisBroker.ALTERNATE_ROW_HIGHLIGHTER, noGeometryHighlighter, geometrySizeDifferent});
        ((JXTable) tNutzung).setHighlighters(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER,noGeometryHighlighter,geometrySizeDifferentHighlighter);
        ((JXTable) tNutzung).setSortOrder(0, SortOrder.ASCENDING);
        tNutzung.getSelectionModel().addListSelectionListener(this);
        ((JXTable) tNutzung).packAll();
    }

    private void initModels() {
        bemerkungDocumentModel = new SimpleDocumentModel() {

            public void assignValue(String newValue) {
                log.debug("Bemerkung assigned");
                log.debug("new Value: " + newValue);
                valueToCheck = newValue;
                fireValidationStateChanged(this);
                if (currentFlurstueck != null && getStatus() == Validatable.VALID || getStatus() == Validatable.WARNING) {
                    log.info("Entität wirklich geändert");
                    currentFlurstueck.setBemerkung(newValue);
                }
            }
        };
        txtBemerkung.setDocument(bemerkungDocumentModel);
        valTxtBemerkung.reSetValidator((Validatable) bemerkungDocumentModel);
        validators.add(valTxtBemerkung);
    }

    //private Thread panelRefresherThread;
    public void flurstueckChanged(Flurstueck newFlurstueck) {
        try {
            log.info("FlurstueckChanged");
            currentFlurstueck = newFlurstueck;
            updateThread.notifyThread(currentFlurstueck);
        } catch (Exception ex) {
            log.error("Fehler beim Flurstückswechsel: ", ex);
            LagisBroker.getInstance().flurstueckChangeFinished(VerwaltungsPanel.this);
        }
    }

    private void refreshReBeIcons() {
        try {
            Set<ReBe> reBe = currentFlurstueck.getRechteUndBelastungen();
            Iterator<ReBe> it = reBe.iterator();
            while (it.hasNext()) {
                Boolean curRebe = it.next().getIstRecht();
                if (curRebe != null && curRebe.booleanValue() == true) {
                    log.debug(lblRechte);
                    log.debug("aktuelle ReBe ist recht");
                    lblRechte.setVisible(true);
                } else {
                    log.debug(lblBelastungen);
                    lblBelastungen.setVisible(true);
                    log.debug("aktuelle ReBe ist Belastung");
                }
            }
        } catch (Exception ex) {
            log.warn("Fehler beim setzen der Rebe Icons", ex);
            clearReBeIcons();
        }
    }

    private void clearReBeIcons() {
        lblRechte.setVisible(false);
        lblBelastungen.setVisible(false);
    }

    public void setComponentEditable(boolean isEditable) {
        if (isFlurstueckEditable) {
            log.debug("Verwaltung --> setComponentEditable");
            TableCellEditor currentEditor = tNutzung.getCellEditor();
            if (currentEditor != null) {
                currentEditor.cancelCellEditing();
            }
            isInEditMode = isEditable;
            txtBemerkung.setEditable(isEditable);
            cbSperre.setEnabled(isEditable);
            btnAddVerwaltung.setEnabled(isEditable);
            if (isEditable && tNutzung.getSelectedRow() != -1) {
                btnRemoveVerwaltung.setEnabled(true);
            } else if (!isEditable) {
                btnRemoveVerwaltung.setEnabled(false);
            }
            //tNutzung.setEnabled(isEditable);
            tableModel.setIsInEditMode(isEditable);
//        HighlighterPipeline pipeline = ((JXTable)tNutzung).getHighlighters();
//        if(isEditable){
//        pipeline.removeHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_DEFAULT);
//        pipeline.addHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_EDIT,false);
//        } else {
//        pipeline.removeHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_EDIT);
//        pipeline.addHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_DEFAULT,false);
//        }
            log.debug("Verwaltung --> setComponentEditable finished");
        } else {
            log.debug("Flurstück ist nicht städtisch Verwaltungen können nicht editiert werden");
        }
    }

    public synchronized void clearComponent() {

        log.debug("Clear Verwaltungspanel");
        clearReBeIcons();
        lblLastModification.setToolTipText(null);
        //TODOTabelle löschen wenn model vorhanden
        cbSperre.setSelected(false);
        try {
            //txtBemerkung.setText("");
            bemerkungDocumentModel.clear(0, bemerkungDocumentModel.getLength());
        } catch (BadLocationException ex) {
            log.warn("Fehler beim cleanen der Komponente", ex);
        }
        lblBemSperre.setText("");
        tableModel.refreshTableModel(new HashSet<Verwaltungsbereich>());
        log.debug("Clear Verwaltungspanel beendet");
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
            log.debug("Mit maus auf Verwaltungstabelle geklickt");
            int selecetdRow = tNutzung.getSelectedRow();
            if (selecetdRow != -1 && isInEditMode) {
                //if(isInEditMode){
                btnRemoveVerwaltung.setEnabled(true);
            //}
            } else {
                btnRemoveVerwaltung.setEnabled(false);
            }
        }
    }

    public Vector<GeometrySlotInformation> getSlotInformation() {
        //VerwaltungsTableModel tmp = (VerwaltungsTableModel) tNutzung.getModel();
        final Vector<GeometrySlotInformation> result = new Vector<GeometrySlotInformation>();
        if (isWidgetReadOnly()) {
            return result;
        } else {
            int rowCount = tableModel.getRowCount();
            if (rowCount == 1 || !isFlurstueckEditable) {
                return result;
            }
            for (int i = 0; i < rowCount; i++) {
                Verwaltungsbereich currentBereich = tableModel.getVerwaltungsbereichAtRow(i);

                if (currentBereich != null && currentBereich.getGeometry() == null) {
                    Object idValue1 = tableModel.getValueAt(i, 0);
                    Object idValue2 = tableModel.getValueAt(i, 1);
                    String identifer;
                    if (idValue1 != null && idValue2 != null) {
                        identifer = idValue1.toString() + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR() + idValue2.toString();
                    } else if (idValue1 != null) {
                        identifer = idValue1.toString() + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR() + "Kein Verwaltungsgebrauch";
                    } else if (idValue2 != null) {
                        identifer = "Kein Ressort" + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR() + idValue2.toString();
                    } else {
                        identifer = "Kein Ressort" + GeometrySlotInformation.getSLOT_IDENTIFIER_SEPARATOR() + "Kein Verwaltungsgebrauch";
                    }
                    result.add(new GeometrySlotInformation(getProviderName(), identifer, currentBereich, this));
                }
            }
            return result;
        }
    }

    public String getProviderName() {
        return PROVIDER_NAME;
    }

    public void refresh(Object refreshObject) {
        log.debug("Verwaltungsbereich refreshed");
        final VerwaltungsTableModel model = ((VerwaltungsTableModel) tNutzung.getModel());
        //model.updateAreaInformation(null);
//        EventQueue.invokeLater(new Runnable() {
//            public void run() {
        model.fireTableDataChanged();
        tNutzung.repaint();
    }

    public void updateFlurstueckForSaving(Flurstueck flurstueck) {
        //tableModel.updateAreaInformation(null);
        Set<Verwaltungsbereich> vBereiche = flurstueck.getVerwaltungsbereiche();
        if (vBereiche != null) {
            vBereiche.clear();
            vBereiche.addAll(tableModel.getVerwaltungsbereiche());
        } else {
            HashSet newSet = new HashSet();
            newSet.addAll(tableModel.getVerwaltungsbereiche());
            flurstueck.setVerwaltungsbereiche(newSet);
        }
    }

    public String getWidgetName() {
        return WIDGET_NAME;
    }

    //TODO multiple Selection
    //TODO refactor code --> poor style
    synchronized public void featureSelectionChanged(Collection<Feature> features) {
        try {
            log.debug("FeatureSelection Changed");
            //tNutzung.getSelectionModel().removeListSelectionListener(this);
            if (features.size() == 0) {
                return;
            }
            log.debug("Features Selected :" + features.size());
            for (Feature feature : features) {
                if (feature instanceof Verwaltungsbereich) {
                    log.debug("Feature ist Verwaltungsbereich");
                    //TODO Refactor Name
                    int index = tableModel.getIndexOfVerwaltungsbereich((Verwaltungsbereich) feature);
                    int displayedIndex = ((JXTable) tNutzung).getFilters().convertRowIndexToView(index);
                    if (index != -1 && LagisBroker.getInstance().getMappingComponent().getFeatureCollection().isSelected(feature)) {
                        //tNutzung.changeSelection(((JXTable)tNutzung).getFilters().convertRowIndexToView(index),0,false,false);
                        log.debug("Ist EDT: " + EventQueue.isDispatchThread());
                        log.debug("displayed index: " + displayedIndex);
                        tNutzung.getSelectionModel().addSelectionInterval(displayedIndex, displayedIndex);
                        Rectangle tmp = tNutzung.getCellRect(displayedIndex, 0, true);
                        if (tmp != null) {
                            tNutzung.scrollRectToVisible(tmp);
                        }
                    } else {
                        tNutzung.getSelectionModel().removeSelectionInterval(displayedIndex, displayedIndex);
                    }
                } else {
                    tNutzung.clearSelection();
                }
            }
        } catch (Exception ex) {
            log.error("Fehler beim featurechanged: ", ex);
        }
        //tNutzung.getSelectionModel().addListSelectionListener(this);
        tNutzung.repaint();
    }

    //TODO WHAT IS IT GOOD FOR
    public void stateChanged(ChangeEvent e) {
    }

    //ToDo multiple Selection
    synchronized public void valueChanged(ListSelectionEvent e) {
        log.debug("SelectionChanged", new CurrentStackTrace());
        log.debug("EventSource: " + e.getSource());
        MappingComponent mappingComp = LagisBroker.getInstance().getMappingComponent();
        if (tNutzung.getSelectedRow() != -1) {
            if (isInEditMode) {
                btnRemoveVerwaltung.setEnabled(true);
            } else {
                btnRemoveVerwaltung.setEnabled(false);
            }
            int index = ((JXTable) tNutzung).getFilters().convertRowIndexToModel(tNutzung.getSelectedRow());
            if (index != -1 && tNutzung.getSelectedRowCount() <= 1) {
                Verwaltungsbereich selectedVerwaltungsbereich = tableModel.getVerwaltungsbereichAtRow(index);
                if (selectedVerwaltungsbereich != null && selectedVerwaltungsbereich.getGeometry() != null && !mappingComp.getFeatureCollection().isSelected(selectedVerwaltungsbereich)) {
                    mappingComp.getFeatureCollection().select(selectedVerwaltungsbereich);
                }
            }
        } else {
            btnRemoveVerwaltung.setEnabled(false);
            return;
        }
        tNutzung.repaint();
    }

    public Element getConfiguration() throws NoWriteError {
        return null;
    }

    public void masterConfigure(Element parent) {
        try {
            Element htmlTooltip = parent.getChild("HTMLTooltips");
            List<Element> tooltips = htmlTooltip.getChildren();
            Iterator<Element> it = tooltips.iterator();
            while (it.hasNext()) {
                Element tmpTooltip = it.next();
                if (tmpTooltip.getChild("id").getText().equals("Verwaltungsgebrauch")) {
                    Element copy = (Element) tmpTooltip.clone();
                    copy.detach();
                    vgRenderer.setHTMLTooltip(copy);
                }
            }

        } catch (Exception ex) {
            log.warn("Fehler beim lesen der htmlTooltips", ex);
        }
    }

    public void configure(Element parent) {
    }

    //TODO USE
    @Override()
    public Icon getWidgetIcon() {
        return null;
    }
    //ToDo Comboboxen selbst in Validatoren stecken und auswerten
    private Vector<Validator> validators = new Vector<Validator>();

    @Override()
    public int getStatus() {
        try {
            if (tNutzung.getCellEditor() != null) {
                validationMessage = "Bitte vollenden Sie alle Änderungen bei den Verwaltungsbereichen.";
                return Validatable.ERROR;
            }
            Iterator<Validator> it = validators.iterator();
            while (it.hasNext()) {
                Validator current = it.next();
                if (current.getValidationState() != Validatable.VALID) {
                    validationMessage = current.getValidationMessage();
                    return Validatable.ERROR;
                }
            }
            Vector<Verwaltungsbereich> allVerwaltung = tableModel.getVerwaltungsbereiche();
            Iterator<Verwaltungsbereich> itVerwaltung = allVerwaltung.iterator();
            while (itVerwaltung.hasNext()) {
                Verwaltungsbereich current = itVerwaltung.next();
                if (allVerwaltung.size() == 1 && (current != null && current.getGeometry() != null)) {
                    validationMessage = "Wenn ein Verwaltungsbereich vorhanden ist, dann darf\n" +
                            "diesem keine Geometrie zugeordnet sein.";
                    return Validatable.ERROR;
                }
                if (current.getDienststelle() == null || current.getGebrauch() == null) {
                    //TODO use validator
                    validationMessage = "Für jeden Verwaltungsbereich müssen Dienstelle und Verwaltungsgebrauch festgelegt werden.";
                    return Validatable.ERROR;
                }
                if (allVerwaltung.size() > 1 && (current == null || current.getGeometry() == null)) {
                    validationMessage = "Wenn mehr als zwei Verwaltungsbereiche vorhanden sind,\n" +
                            "müssen allen Bereichen Geometrien zugeordnet werden.";
                    return Validatable.ERROR;
                }
            }
            return Validatable.VALID;
        } catch (Exception ex) {
            log.error("Fehler beim validieren des Verwaltungsbereichs");
            validationMessage = "Es ist ein Fehler beim validieren der Verwaltungsbereiche aufgetreten.";
            return Validatable.ERROR;
        }
    }
    //TODO Massive TRAFFIC perhaps checking

    public void validationStateChanged(Object validatedObject) {
        fireValidationStateChanged(validatedObject);
        tNutzung.repaint();
    }

    public void featureCollectionChanged() {
        tNutzung.repaint();
    }

    public void featuresRemoved(FeatureCollectionEvent fce) {
        log.debug("Features Removed");
        tNutzung.repaint();
    }

    public void featuresChanged(FeatureCollectionEvent fce) {
        log.debug("FeaturesChanges Verwaltung");
        tNutzung.repaint();
    }

    public void featuresAdded(FeatureCollectionEvent fce) {
        tNutzung.repaint();
    }

    public void featureSelectionChanged(FeatureCollectionEvent fce) {
        tNutzung.repaint();
    }

    public void featureReconsiderationRequested(FeatureCollectionEvent fce) {
        tNutzung.repaint();
    }

    public void allFeaturesRemoved(FeatureCollectionEvent fce) {
        tNutzung.repaint();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        cbSperre = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtBemerkung = new javax.swing.JTextArea();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        lblRechte = new javax.swing.JLabel();
        lblBelastungen = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tNutzung = new JXTable();
        lblBemSperre = new javax.swing.JLabel();
        btnAddVerwaltung = new javax.swing.JButton();
        btnRemoveVerwaltung = new javax.swing.JButton();
        lblWFSInfo = new javax.swing.JLabel();

        cbSperre.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbSperre.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbSperre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSperreActionPerformed(evt);
            }
        });

        jLabel1.setText("Bemerkung:");

        jLabel4.setText("Sperre:");

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        txtBemerkung.setColumns(20);
        txtBemerkung.setLineWrap(true);
        txtBemerkung.setRows(1);
        jScrollPane2.setViewportView(txtBemerkung);

        jSeparator1.setMinimumSize(new java.awt.Dimension(50, 1));
        jSeparator1.setPreferredSize(new java.awt.Dimension(50, 1));

        lblRechte.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/recht.png")));
        lblRechte.setToolTipText("Es sind Rechte vorhanden");

        lblBelastungen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/belastung.png")));
        lblBelastungen.setToolTipText("Es sind Belastungen vorhanden");

        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(100, 100));
        tNutzung.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        tNutzung.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        tNutzung.setToolTipText("");
        tNutzung.setMinimumSize(new java.awt.Dimension(225, 48));
        jScrollPane1.setViewportView(tNutzung);

        btnAddVerwaltung.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/add.png")));
        btnAddVerwaltung.setBorder(null);
        btnAddVerwaltung.setOpaque(false);
        btnAddVerwaltung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddVerwaltungActionPerformed(evt);
            }
        });

        btnRemoveVerwaltung.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/remove.png")));
        btnRemoveVerwaltung.setBorder(null);
        btnRemoveVerwaltung.setOpaque(false);
        btnRemoveVerwaltung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveVerwaltungActionPerformed(evt);
            }
        });

        lblWFSInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/wfs_green.png")));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(16, 16, 16)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(jLabel4)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbSperre)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(lblBemSperre, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(lblBelastungen)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(lblRechte)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(lblWFSInfo))
                            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel1))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(btnAddVerwaltung, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnRemoveVerwaltung, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnAddVerwaltung, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnRemoveVerwaltung))
                .add(7, 7, 7)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(9, 9, 9)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel4)
                            .add(cbSperre)
                            .add(lblBemSperre, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblWFSInfo)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, lblRechte)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, lblBelastungen))))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {btnAddVerwaltung, btnRemoveVerwaltung}, org.jdesktop.layout.GroupLayout.VERTICAL);

    }// </editor-fold>//GEN-END:initComponents
    private void btnRemoveVerwaltungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveVerwaltungActionPerformed
        int currentRow = tNutzung.getSelectedRow();
        if (currentRow != -1) {
            //VerwaltungsTableModel currentModel = (VerwaltungsTableModel)tNutzung.getModel();
            tableModel.removeVerwaltungsbereich(((JXTable) tNutzung).convertRowIndexToModel(currentRow));
            tableModel.fireTableDataChanged();
        }
    }//GEN-LAST:event_btnRemoveVerwaltungActionPerformed

    private void btnAddVerwaltungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddVerwaltungActionPerformed
        //VerwaltungsTableModel currentModel = (VerwaltungsTableModel)tNutzung.getModel();
        Verwaltungsbereich tmp = new Verwaltungsbereich();
        log.debug("Verwalungsbereich Gebrauch: " + tmp.getGebrauch());
        tableModel.addVerwaltungsbereich(tmp);
        tableModel.fireTableDataChanged();
    }//GEN-LAST:event_btnAddVerwaltungActionPerformed

    private void cbSperreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSperreActionPerformed
// TODO add your handling code here:
        if (currentFlurstueck != null) {
            boolean isGesperrt = cbSperre.isSelected();
            if (isGesperrt) {
                //TODO Länge begrenzen
                String answer = null;
                while (answer == null || answer.trim().length() == 0) {
                    answer = JOptionPane.showInputDialog(this, "Bitte eine Bemerkung zur Sperre angeben.");
                    if (answer == null) {
                        log.debug("Sperre setzen abgebrochen");
                        cbSperre.setSelected(false);
                        return;
                    }
                }
                //datamodell refactoring 22.10.07
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
            log.error("Kann Sperre nicht setzen Flurstueck ist null");
        }
    }//GEN-LAST:event_cbSperreActionPerformed

    private void cboVGActionPerformed() {
        log.debug("cboVerwaltungActionPerformed");
//        int index = ((JXTable)tNutzung).getFilters().convertRowIndexToModel(tNutzung.getSelectedRow());
//        if(index != -1){
//            log.debug("Zeile : "+tNutzung.getSelectedRow()+" wurde ausgewählt");
//            LagisBroker.getInstance().getMappingComponent().getFeatureCollection().reconsiderFeature(tableModel.getVerwaltungsbereichAtRow(index));
//        }
        TableCellEditor currentEditor = tNutzung.getCellEditor();
        if (currentEditor != null) {
            currentEditor.stopCellEditing();
        }
        for (Feature feature : (Collection<Feature>) LagisBroker.getInstance().getMappingComponent().getFeatureCollection().getSelectedFeatures()) {
            LagisBroker.getInstance().getMappingComponent().getFeatureCollection().reconsiderFeature(feature);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddVerwaltung;
    private javax.swing.JButton btnRemoveVerwaltung;
    private javax.swing.JCheckBox cbSperre;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblBelastungen;
    private javax.swing.JLabel lblBemSperre;
    private javax.swing.JLabel lblRechte;
    private javax.swing.JLabel lblWFSInfo;
    private javax.swing.JTable tNutzung;
    private javax.swing.JTextArea txtBemerkung;
    // End of variables declaration//GEN-END:variables
}
