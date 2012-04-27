/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.panels;

import Sirius.navigator.connection.Connection;
import Sirius.navigator.connection.SessionManager;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.cismet.cids.custom.beans.lagis.*;

import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.StyledFeatureGroupWrapper;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.interfaces.DoneDelegate;
import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.FlurstueckRequester;

import de.cismet.lagis.models.KeyComboboxModel;

import de.cismet.lagis.thread.ExtendedSwingWorker;
import de.cismet.lagis.thread.WFSRetrieverFactory;

import de.cismet.lagis.validation.Validatable;

import de.cismet.lagis.widget.AbstractWidget;

import de.cismet.lagisEE.interfaces.Key;

import de.cismet.lagisEE.util.FlurKey;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class FlurstueckChooser extends AbstractWidget implements FlurstueckChangeListener, FlurstueckRequester {

    //~ Static fields/initializers ---------------------------------------------

    public static final String FEATURE_GRP = "Flurstück";
    private static final String WIDGET_NAME = "Flurstueck Suchpanel";
    private static final String TOOLBAR_RESPATH = "/de/cismet/lagis/ressource/icons/toolbar/";
    private static final String FILEPATH_FILTERICON_ALL = TOOLBAR_RESPATH + "filter_all.png";
    private static final String FILEPATH_FILTERICON_CURRENT = TOOLBAR_RESPATH + "filter_current.png";
    private static final String FILEPATH_FILTERICON_HISTORIC = TOOLBAR_RESPATH + "filter_historic.png";
    private static final String FILEPATH_FILTERICON_STAEDTISCH = TOOLBAR_RESPATH + "filter_staedtisch.png";
    private static final String FILEPATH_ICON_CURRENT = TOOLBAR_RESPATH + "current.png";
    private static final String FILEPATH_ICON_ABTEILUNGIX = TOOLBAR_RESPATH + "abteilungIX.png";
    private static final String FILEPATH_ICON_ABTEILUNGIX_HISTORIC = TOOLBAR_RESPATH + "historic_abteilungIX.png";
    private static final String FILEPATH_ICON_HISTORIC = TOOLBAR_RESPATH + "historic.png";
    private static final String FILEPATH_ICON_UNKNOWNFLURSTUECK = TOOLBAR_RESPATH + "unkownFlurstueck.png";
    private static final String FILEPATH_ICON_WFSWARN = "/de/cismet/lagis/ressource/icons/FlurstueckPanel/16warn.png";
    // filters
    private static final String FILTER_CURRENT_NAME = "nur aktuelle";
    private static final String FILTER_HISTORIC_NAME = "nur historische";
    private static final String FILTER_ALL_NAME = "alle Flurstücke";
    private static final String FILTER_ABTEILUNG_IX = "nur Abteilung IX";
    private static final String FILTER_STAEDTISCH = "nur städtische";
    // modes
    private static final Logger LOG = Logger.getLogger(FlurstueckChooser.class);

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Mode {

        //~ Enum constants -----------------------------------------------------

        SEARCH, CONTINUATION, CREATION, CONTINUATION_HISTORIC
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Status {

        //~ Enum constants -----------------------------------------------------

        STAEDTISCH_HISTORIC, STAEDTISCH, ABTEILUNG_IX_HISTORIC, ABTEILUNG_IX, UNKNOWN_FLURSTUECK, WFS_WARN
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private enum GemarkungRetrieverMode {

        //~ Enum constants -----------------------------------------------------

        RETRIEVE_GEMARKUNGEN, RETRIEVE_FLURE, RETRIEVE_WITH_RESOLVED, RETRIEVE_AUTOMATIC
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private enum Filter {

        //~ Enum constants -----------------------------------------------------

        ALL, HISTORIC, ABTEILUNG_IX, STAEDTISCH, CURRENT
    }

    //~ Instance fields --------------------------------------------------------

    // listenerLocker
    private boolean cboGemarkungListenerEnabled = true;
    private boolean cboFlurListenerEnabled = true;
    private boolean cboFlurstueckListenerEnabled = true;
    private final Icon icoStaedtisch = new ImageIcon(getClass().getResource(FILEPATH_ICON_CURRENT));
    private final Icon icoStaedtischHistoric = new ImageIcon(getClass().getResource(FILEPATH_ICON_HISTORIC));
    private final Icon icoAbteilungIX = new ImageIcon(getClass().getResource(FILEPATH_ICON_ABTEILUNGIX));
    private final Icon icoAbteilungIXHistoric = new ImageIcon(getClass().getResource(
                FILEPATH_ICON_ABTEILUNGIX_HISTORIC));
    private final Icon icoFilterAll = new ImageIcon(getClass().getResource(FILEPATH_FILTERICON_ALL));
    private final Icon icoFilterCurrent = new ImageIcon(getClass().getResource(FILEPATH_FILTERICON_CURRENT));
    private final Icon icoFilterHistoric = new ImageIcon(getClass().getResource(FILEPATH_FILTERICON_HISTORIC));
    private final Icon icoFilterStaedtisch = new ImageIcon(getClass().getResource(FILEPATH_FILTERICON_STAEDTISCH));
    private final Icon icoCurrent = new ImageIcon(getClass().getResource(FILEPATH_ICON_CURRENT));
    private final Icon icoHistoric = new ImageIcon(getClass().getResource(FILEPATH_ICON_HISTORIC));
    private final Icon icoUnknownFlurstueck = new ImageIcon(getClass().getResource(FILEPATH_ICON_UNKNOWNFLURSTUECK));
    private final Icon icoWFSWarn = new ImageIcon(getClass().getResource(FILEPATH_ICON_WFSWARN));
    // retrievers
    private GemarkungRetriever currentGemarkungsRetriever = null;
    private FlurRetriever currentFlurRetriever = null;
    private FlurstueckRetriever currentFlurstueckRetriever = null;
    private SwingWorker currentWFSRetriever = null;
    private AutomaticFlurstueckRetriever currentAutomaticRetriever = null;
    private FlurstueckChecker currentFlurstueckChecker = null;
    private Thread currentGemarkungsWaiter = null;
    private final ReentrantLock automaticRequestLock = new ReentrantLock();
    private Filter filter;
    private Mode currentMode = Mode.SEARCH;
    private final Collection<FlurstueckSchluesselCustomBean> removeFilter =
        new ArrayList<FlurstueckSchluesselCustomBean>();
    private boolean isFullInitialized = false;
    private FlurstueckCustomBean currentFlurstueckBean;
    private FlurstueckSchluesselCustomBean currentlyCreatedFlurstueckSchluesselBean;
    private Color currentColor;
    // must be locked
    private boolean isFlurstueckCandidateValide = false;
    // also lock
    private Boolean isFlurstueckCreateable = false;
    // must be locked
    private final ReentrantLock validationMessageLock = new ReentrantLock();
    private String creationValidationMessage = "Bitte vervollständigen Sie alle Flurstücke";
    // Variables declaration - do not modify
    private javax.swing.JButton btnAction;
    private javax.swing.JComboBox cboFilter;
    private javax.swing.JComboBox cboFlur;
    private javax.swing.JComboBox cboFlurstueck;
    private javax.swing.JComboBox cboGemarkung;
    private javax.swing.JPanel panFilter;
    private javax.swing.JPanel panFlur;
    private javax.swing.JPanel panFlurstueck;
    private javax.swing.JPanel panGemarkung;
    private javax.swing.JPanel panTxtFlurstueck;
    private javax.swing.JProgressBar pbFilter;
    private javax.swing.JProgressBar pbFlur;
    private javax.swing.JProgressBar pbFlurstueck;
    private javax.swing.JProgressBar pbGemarkung;
    private javax.swing.JProgressBar pbTxtFlurstueck;
    private javax.swing.JTextField txtFlurstueck;
    // End of variables declaration

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FlurstueckChooser object.
     */
    public FlurstueckChooser() {
        this(Mode.CONTINUATION);
    }

    /**
     * Creates a new FlurstueckChooser object.
     *
     * @param  mode  DOCUMENT ME!
     */
    public FlurstueckChooser(final Mode mode) {
        try {
            currentMode = mode;
            initComponents();
            initRenderer();
            configureComponents();
        } catch (Exception e) {
            LOG.error("Could not Create FlurstueckChooser", e);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        panTxtFlurstueck = new javax.swing.JPanel();
        txtFlurstueck = new javax.swing.JTextField();
        pbTxtFlurstueck = new javax.swing.JProgressBar();
        btnAction = new javax.swing.JButton();
        panGemarkung = new javax.swing.JPanel();
        cboGemarkung = new javax.swing.JComboBox();
        pbGemarkung = new javax.swing.JProgressBar();
        panFlur = new javax.swing.JPanel();
        cboFlur = new javax.swing.JComboBox();
        pbFlur = new javax.swing.JProgressBar();
        panFlurstueck = new javax.swing.JPanel();
        cboFlurstueck = new javax.swing.JComboBox();
        pbFlurstueck = new javax.swing.JProgressBar();
        panFilter = new javax.swing.JPanel();
        cboFilter = new javax.swing.JComboBox();
        pbFilter = new javax.swing.JProgressBar();

        panTxtFlurstueck.setMaximumSize(new java.awt.Dimension(100, 28));
        panTxtFlurstueck.setMinimumSize(new java.awt.Dimension(100, 28));
        panTxtFlurstueck.setPreferredSize(new java.awt.Dimension(100, 28));
        panTxtFlurstueck.setLayout(new java.awt.BorderLayout());

        txtFlurstueck.setToolTipText("Flurstück");
        txtFlurstueck.setEnabled(false);
        txtFlurstueck.setMaximumSize(new java.awt.Dimension(100, 23));
        txtFlurstueck.setMinimumSize(new java.awt.Dimension(100, 23));
        txtFlurstueck.setPreferredSize(new java.awt.Dimension(100, 23));
        panTxtFlurstueck.add(txtFlurstueck, java.awt.BorderLayout.NORTH);

        pbTxtFlurstueck.setBorderPainted(false);
        pbTxtFlurstueck.setMaximumSize(new java.awt.Dimension(100, 5));
        pbTxtFlurstueck.setMinimumSize(new java.awt.Dimension(100, 5));
        pbTxtFlurstueck.setPreferredSize(new java.awt.Dimension(100, 5));
        panTxtFlurstueck.add(pbTxtFlurstueck, java.awt.BorderLayout.SOUTH);

        setMaximumSize(new java.awt.Dimension(483, 35));

        btnAction.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/flurstueck.png"))); // NOI18N
        btnAction.setBorder(null);
        add(btnAction);

        panGemarkung.setMaximumSize(new java.awt.Dimension(100, 28));
        panGemarkung.setMinimumSize(new java.awt.Dimension(100, 28));
        panGemarkung.setPreferredSize(new java.awt.Dimension(100, 28));
        panGemarkung.setLayout(new java.awt.BorderLayout());

        cboGemarkung.setEditable(true);
        cboGemarkung.setToolTipText("Gemarkung");
        cboGemarkung.setEnabled(false);
        cboGemarkung.setMaximumSize(new java.awt.Dimension(100, 23));
        cboGemarkung.setMinimumSize(new java.awt.Dimension(100, 23));
        cboGemarkung.setName("cboGemarkung"); // NOI18N
        cboGemarkung.setPreferredSize(new java.awt.Dimension(100, 23));
        cboGemarkung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboGemarkungActionPerformed(evt);
                }
            });
        panGemarkung.add(cboGemarkung, java.awt.BorderLayout.CENTER);

        pbGemarkung.setBorderPainted(false);
        pbGemarkung.setMaximumSize(new java.awt.Dimension(90, 5));
        pbGemarkung.setMinimumSize(new java.awt.Dimension(90, 5));
        pbGemarkung.setPreferredSize(new java.awt.Dimension(90, 5));
        panGemarkung.add(pbGemarkung, java.awt.BorderLayout.SOUTH);

        add(panGemarkung);

        panFlur.setMaximumSize(new java.awt.Dimension(100, 28));
        panFlur.setLayout(new java.awt.BorderLayout());

        cboFlur.setEditable(true);
        cboFlur.setToolTipText("Flur");
        cboFlur.setEnabled(false);
        cboFlur.setMaximumSize(new java.awt.Dimension(100, 23));
        cboFlur.setMinimumSize(new java.awt.Dimension(100, 23));
        cboFlur.setName("cboFlur"); // NOI18N
        cboFlur.setPreferredSize(new java.awt.Dimension(100, 23));
        cboFlur.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboFlurActionPerformed(evt);
                }
            });
        panFlur.add(cboFlur, java.awt.BorderLayout.CENTER);

        pbFlur.setBorderPainted(false);
        pbFlur.setMaximumSize(new java.awt.Dimension(90, 5));
        pbFlur.setMinimumSize(new java.awt.Dimension(90, 5));
        pbFlur.setPreferredSize(new java.awt.Dimension(90, 5));
        panFlur.add(pbFlur, java.awt.BorderLayout.SOUTH);

        add(panFlur);

        panFlurstueck.setMaximumSize(new java.awt.Dimension(100, 23));
        panFlurstueck.setLayout(new java.awt.BorderLayout());

        cboFlurstueck.setEditable(true);
        cboFlurstueck.setToolTipText("Flurstück");
        cboFlurstueck.setEnabled(false);
        cboFlurstueck.setMaximumSize(new java.awt.Dimension(100, 23));
        cboFlurstueck.setMinimumSize(new java.awt.Dimension(100, 23));
        cboFlurstueck.setName("cboFlurstueck"); // NOI18N
        cboFlurstueck.setPreferredSize(new java.awt.Dimension(100, 23));
        cboFlurstueck.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboFlurstueckActionPerformed(evt);
                }
            });
        panFlurstueck.add(cboFlurstueck, java.awt.BorderLayout.CENTER);

        pbFlurstueck.setBorderPainted(false);
        pbFlurstueck.setMaximumSize(new java.awt.Dimension(90, 5));
        pbFlurstueck.setMinimumSize(new java.awt.Dimension(90, 5));
        pbFlurstueck.setPreferredSize(new java.awt.Dimension(90, 5));
        panFlurstueck.add(pbFlurstueck, java.awt.BorderLayout.SOUTH);

        add(panFlurstueck);

        panFilter.setLayout(new java.awt.BorderLayout());

        cboFilter.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] {
                    "alle Flurstücke",
                    "nur historische",
                    "nur aktuelle",
                    "nur Abteilung IX",
                    "nur städtische"
                }));
        cboFilter.setToolTipText("Filter: Alle Flurstücke, nur aktuelle Flurstücke, nur historische Flurstücke");
        cboFilter.setMaximumSize(new java.awt.Dimension(130, 23));
        cboFilter.setMinimumSize(new java.awt.Dimension(130, 23));
        cboFilter.setPreferredSize(new java.awt.Dimension(130, 23));
        cboFilter.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboFilterActionPerformed(evt);
                }
            });
        panFilter.add(cboFilter, java.awt.BorderLayout.CENTER);

        pbFilter.setBorderPainted(false);
        pbFilter.setMaximumSize(new java.awt.Dimension(90, 5));
        pbFilter.setMinimumSize(new java.awt.Dimension(90, 5));
        pbFilter.setPreferredSize(new java.awt.Dimension(90, 5));
        panFilter.add(pbFilter, java.awt.BorderLayout.SOUTH);

        add(panFilter);
    } // </editor-fold>

    /**
     * DOCUMENT ME!
     */
    private void configureComponents() {
        AutoCompleteDecorator.decorate(cboGemarkung);
        AutoCompleteDecorator.decorate(cboFlur);
        AutoCompleteDecorator.decorate(cboFlurstueck);

        final FocusListener focusListener = new FocusListenerImpl();
        cboGemarkung.getEditor().getEditorComponent().addFocusListener(focusListener);
        cboFlur.getEditor().getEditorComponent().addFocusListener(focusListener);
        cboFlurstueck.getEditor().getEditorComponent().addFocusListener(focusListener);

        switch (currentMode) {
            case SEARCH: {
                // Muss nichts gemacht werden
            }
            break;
            case CREATION: {
                this.remove(panFilter);
                filter = Filter.CURRENT;
                this.remove(panFlurstueck);
                txtFlurstueck.getDocument().addDocumentListener(new DocumentListenerImpl());
                panTxtFlurstueck.setVisible(true);
                add(panTxtFlurstueck);
            }
            break;
            case CONTINUATION:
            case CONTINUATION_HISTORIC: {
                this.remove(panFilter);
                // Damit nur aktuelle Flurstücke beim teilen und zusammenfügen angezeigt werden
                if (currentMode == Mode.CONTINUATION) {
                    filter = Filter.CURRENT;
                } else {
                    filter = Filter.HISTORIC;
                }
            }
            break;
        }

        txtFlurstueck.setEnabled(false);
        currentGemarkungsRetriever = new GemarkungRetriever(GemarkungRetrieverMode.RETRIEVE_GEMARKUNGEN);
        // currentGemarkungsRetriever.execute();

        // if statement fix GUI-Builder warning cannot load component FlurstueckChooser...
        final Connection c = SessionManager.getConnection();
        if (c.isConnected()) {
            LagisBroker.getInstance().execute(currentGemarkungsRetriever);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initRenderer() {
        cboFilter.setRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    final JLabel label = (JLabel)super.getListCellRendererComponent(
                            list,
                            value,
                            index,
                            isSelected,
                            cellHasFocus);
                    if (value.equals(FILTER_CURRENT_NAME)) {
                        label.setIcon(icoFilterCurrent);
                    } else if (value.equals(FILTER_HISTORIC_NAME)) {
                        label.setIcon(icoFilterHistoric);
                    } else if (value.equals(FILTER_STAEDTISCH)) {
                        label.setIcon(icoFilterStaedtisch);
                    } else if (value.equals(FILTER_ABTEILUNG_IX)) {
                        label.setIcon(icoAbteilungIX);
                    } else {
                        label.setIcon(icoFilterAll);
                    }
                    return label;
                }
            });

        cboFlurstueck.setRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    final JLabel label = (JLabel)super.getListCellRendererComponent(
                            list,
                            value,
                            index,
                            isSelected,
                            cellHasFocus);
                    if (value instanceof FlurstueckSchluesselCustomBean) {
                        final FlurstueckSchluesselCustomBean valueBean = (FlurstueckSchluesselCustomBean)value;
                        if (valueBean != null) {
                            if (valueBean.getGueltigBis() == null) {
                                final FlurstueckArtCustomBean flurstueckArt = valueBean.getFlurstueckArt();
                                if ((flurstueckArt != null) && (flurstueckArt.getBezeichnung() != null)
                                            && flurstueckArt.getBezeichnung().equals(
                                                FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                                    label.setIcon(icoCurrent);
                                } else {
                                    label.setIcon(icoAbteilungIX);
                                }
                            } else {
                                label.setIcon(icoHistoric);
                            }
                            return label;
                        }
                    }
                    label.setIcon(icoUnknownFlurstueck);
                    return label;
                }
            });
    }

    @Override
    public void clearComponent() {
    }

    @Override
    public void refresh(final Object refreshObject) {
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
        LagisBroker.warnIfThreadIsNotEDT();

        if (EventQueue.isDispatchThread()) {
            setEditable(isEditable);
        } else {
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        setEditable(isEditable);
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isEditable  DOCUMENT ME!
     */
    private void setEditable(final boolean isEditable) {
        if (isEditable) {
            setHighlightColor(Color.WHITE);
        }
        // ATTENTION UGLY WINNING Wenn in Editmodus oder wenn nicht und die GemarkungCustomBean ist
        // disabled (kommt nur vor wenn aus dem Editmodus heraus gewechselt wird)
        if (LagisBroker.getInstance().isInEditMode()
                    || (!LagisBroker.getInstance().isInEditMode() && !cboGemarkung.isEnabled()
                        && isFullInitialized)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("enable/disable comboboxes for editmode");
            }
            cboGemarkung.setEnabled(!isEditable);
            cboFlur.setEnabled(!isEditable);
            cboFlurstueck.setEnabled(!isEditable);
            cboFilter.setEnabled(!isEditable);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("not switching for editmode");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboFilterActionPerformed(final java.awt.event.ActionEvent evt) {
        final String value = (String)cboFilter.getSelectedItem();
        if (value.equals(FILTER_ALL_NAME)) {
            LOG.info("Filter alle Flurstücke ausgewählt");
            filter = Filter.ALL;
        } else if (value.equals(FILTER_HISTORIC_NAME)) {
            LOG.info("Filter nur historische ausgewählt");
            filter = Filter.HISTORIC;
        } else if (value.equals(FILTER_ABTEILUNG_IX)) {
            LOG.info("Filter nur Abteilung IX");
            filter = Filter.ABTEILUNG_IX;
        } else if (value.equals(FILTER_STAEDTISCH)) {
            LOG.info("Filter nur staedtische");
            filter = Filter.STAEDTISCH;
        } else {
            LOG.info("Filter nur aktuelle ausgewählt");
            filter = Filter.CURRENT;
        }
        final FlurstueckCustomBean currentFlurstueck = LagisBroker.getInstance().getCurrentFlurstueck();
        // TODO what if it is unkown
        if (currentFlurstueck != null) {
            doAutomaticRequest(
                AutomaticFlurstueckRetriever.FILTER_ACTION_MODE,
                currentFlurstueck.getFlurstueckSchluessel());
        } else {
            doAutomaticRequest(AutomaticFlurstueckRetriever.SET_BOXES_ACCORDING_TO_CONTENT_MODE, null);
            // TODO what todo if no Flurstück is selected
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  worker  DOCUMENT ME!
     */
    private void setPropertyChangeListener(final SwingWorker worker) {
        try {
            automaticRequestLock.lock();
            if ((currentAutomaticRetriever != null) && (worker != null)) {
                worker.addPropertyChangeListener(currentAutomaticRetriever);
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim setzen des PropertyChangeListeners", ex);
        } finally {
            automaticRequestLock.unlock();
        }
    }

    @Override
    public void flurstueckChanged(final FlurstueckCustomBean newFlurstueck) {
        try {
            boolean isNoGeometryAssigned = true;
            boolean hasManyVerwaltungsbereiche = false;
            // TODO Doppelt gemoppelt --> wird hier geprüft und im Thread;
            // TODO wird geprüft ob Flurstück nicht städtisch ist
            if ((newFlurstueck.getVerwaltungsbereiche() == null)
                        || ((newFlurstueck.getVerwaltungsbereiche() != null)
                            && (newFlurstueck.getVerwaltungsbereiche().size() < 2))
                        || (newFlurstueck.getFlurstueckSchluessel() != null)
                        || (newFlurstueck.getFlurstueckSchluessel().getFlurstueckArt() != null)
                        // TODO Jean equals falsch ?!
                        || !newFlurstueck.getFlurstueckSchluessel().getFlurstueckArt().equals(
                            FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                LOG.info("Keine Verwaltungsgeometrien oder weniger als 2 vorhanden --> WFS");
                // TODO UGLY
                if ((newFlurstueck.getVerwaltungsbereiche() != null)
                            && (newFlurstueck.getVerwaltungsbereiche().size() == 1)) {
                    final VerwaltungsbereichCustomBean verwaltungsbereich = newFlurstueck.getVerwaltungsbereiche()
                                .iterator()
                                .next();
                    if ((verwaltungsbereich != null) && (verwaltungsbereich.getGeometry() != null)) {
                        LagisBroker.getInstance().flurstueckChangeFinished(FlurstueckChooser.this);
                        return;
                    }
                }
            } else if (newFlurstueck.getVerwaltungsbereiche() != null) {
                hasManyVerwaltungsbereiche = true;
                LOG.info("mehr als 2 Verwaltungsbereiche");
                isNoGeometryAssigned = true;
                for (final VerwaltungsbereichCustomBean currentBereich : newFlurstueck.getVerwaltungsbereiche()) {
                    if (currentBereich.getGeometry() != null) {
                        isNoGeometryAssigned = false;
                    }
                    // TODO HOTFIX UMSTELLEN EXTREM SCHLECHT --> sollte nicht geknaupt abgefragt werden
                    if (isNoGeometryAssigned == false) {
                        LagisBroker.getInstance().flurstueckChangeFinished(FlurstueckChooser.this);
                        return;
                    }
                }
            } else {
                LOG.error("Nicht definierter Fall");
                LagisBroker.getInstance().flurstueckChangeFinished(FlurstueckChooser.this);
            }

            if ((currentWFSRetriever != null) && !currentWFSRetriever.isDone()) {
                currentWFSRetriever.cancel(false);
                currentWFSRetriever = null;
            }
            final HashMap<Integer, Boolean> properties = new HashMap<Integer, Boolean>();
            properties.put(WFSRequestJobDone.HAS_MANY_VERWALTUNGSBEREICHE, hasManyVerwaltungsbereiche);
            properties.put(WFSRequestJobDone.IS_NO_GEOMETRY_ASSIGNED, isNoGeometryAssigned);
            currentWFSRetriever = WFSRetrieverFactory.getInstance()
                        .getWFSRetriever(newFlurstueck.getFlurstueckSchluessel(), new WFSRequestJobDone(), properties);
            // currentWFSRetriever = new
            // WFSRetriever(newFlurstueck.getFlurstueckSchluessel(),hasManyVerwaltungsbereiche,isNoGeometryAssigned);
            LagisBroker.getInstance().execute(currentWFSRetriever);
        } catch (Exception ex) {
            LOG.error("Fehler beim Flurstückchange FlurstueckChooser --> Keine Geometrie wird abgerufen", ex);
            LagisBroker.getInstance().flurstueckChangeFinished(FlurstueckChooser.this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  color  DOCUMENT ME!
     */
    private synchronized void setHighlightColor(final Color color) {
        LagisBroker.warnIfThreadIsNotEDT();
        currentColor = color;
        cboGemarkung.getEditor().getEditorComponent().setBackground(color);
        cboFlur.getEditor().getEditorComponent().setBackground(color);
        cboFlurstueck.getEditor().getEditorComponent().setBackground(color);
        txtFlurstueck.setBackground(color);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mode  DOCUMENT ME!
     * @param  key   DOCUMENT ME!
     */
    public void doAutomaticRequest(final int mode, final FlurstueckSchluesselCustomBean key) {
        LagisBroker.warnIfThreadIsNotEDT();
        final Color oldColor = currentColor;
        if ((currentAutomaticRetriever != null) && !currentAutomaticRetriever.isDone()) {
            currentAutomaticRetriever.cancel(false);
            currentAutomaticRetriever = null;
        }
        if ((AutomaticFlurstueckRetriever.FILTER_ACTION_MODE == mode)
                    || (AutomaticFlurstueckRetriever.COPY_CONTENT_MODE == mode)
                    || ((AutomaticFlurstueckRetriever.FLURSTUECK_REQUEST_MODE == mode) && (key != null))) {
            try {
                cboGemarkungListenerEnabled = false;
                cboFlurListenerEnabled = false;
                cboFlurstueckListenerEnabled = false;

                automaticRequestLock.lock();
                try {
                    currentAutomaticRetriever = new AutomaticFlurstueckRetriever(mode, key, oldColor);
                    LagisBroker.getInstance().execute(currentAutomaticRetriever);
                } finally {
                    automaticRequestLock.unlock();
                }

                if ((currentGemarkungsRetriever != null) && !currentGemarkungsRetriever.isDone()
                            && (currentGemarkungsRetriever.getMode() != GemarkungRetrieverMode.RETRIEVE_GEMARKUNGEN)) {
                    currentGemarkungsWaiter.interrupt();
                    currentGemarkungsWaiter = null;
                    currentGemarkungsRetriever.cancel(false);
                    currentGemarkungsRetriever = null;
                } else if ((currentGemarkungsRetriever != null) && !currentGemarkungsRetriever.isDone()
                            && (currentGemarkungsRetriever.getMode() == GemarkungRetrieverMode.RETRIEVE_GEMARKUNGEN)) {
                    if (currentGemarkungsWaiter != null) {
                        currentGemarkungsWaiter.interrupt();
                        currentGemarkungsWaiter = null;
                    }
                    currentGemarkungsWaiter = new Thread() {

                            @Override
                            public void run() {
                                while (!currentGemarkungsRetriever.isDone()) {
                                    if (isInterrupted()) {
                                        return;
                                    }
                                    try {
                                        sleep(100);
                                    } catch (InterruptedException ex) {
                                        return;
                                    }
                                }
                                if (isInterrupted()) {
                                    return;
                                }

                                EventQueue.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            cboGemarkung.setSelectedItem(key.getGemarkung());
                                            currentGemarkungsRetriever = null;
                                            currentGemarkungsRetriever = new GemarkungRetriever(
                                                    GemarkungRetrieverMode.RETRIEVE_AUTOMATIC,
                                                    null,
                                                    key.getGemarkung());
                                            setPropertyChangeListener(currentGemarkungsRetriever);
                                            LagisBroker.getInstance().execute(currentGemarkungsRetriever);
                                        }
                                    });
                            }
                        };
                    currentGemarkungsWaiter.start();
                    return;
                }
            } catch (Exception ex) {
                LOG.error("Fehler in AutomaticRequest FilterAcion --> setze Listener", ex);
                cboGemarkungListenerEnabled = true;
                cboFlurListenerEnabled = true;
                cboFlurstueckListenerEnabled = true;
            }
            cboGemarkung.setSelectedItem(key.getGemarkung());
            currentGemarkungsRetriever = new GemarkungRetriever(
                    GemarkungRetrieverMode.RETRIEVE_AUTOMATIC,
                    null,
                    key.getGemarkung());
            setPropertyChangeListener(currentGemarkungsRetriever);
            LagisBroker.getInstance().execute(currentGemarkungsRetriever);
        } else if ((AutomaticFlurstueckRetriever.SET_BOXES_ACCORDING_TO_CONTENT_MODE == mode) && (key == null)) {
            if (cboFlur.isEnabled()) {
                // TODO WHAT IF Flurstück is UNKNOWN
                cboFlur.setSelectedItem(cboFlur.getSelectedItem());
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckSchluesselCustomBean getCurrentFlurstueckSchluessel() {
        if ((currentMode == Mode.CREATION) && (currentlyCreatedFlurstueckSchluesselBean != null)) {
            return currentlyCreatedFlurstueckSchluesselBean;
        }
        if ((currentFlurstueckBean != null) && (currentFlurstueckBean.getFlurstueckSchluessel() != null)) {
            return currentFlurstueckBean.getFlurstueckSchluessel();
        }

        final Object selectedFlurstueckSchluessel = cboFlurstueck.getSelectedItem();
        if ((selectedFlurstueckSchluessel != null)
                    && (selectedFlurstueckSchluessel instanceof FlurstueckSchluesselCustomBean)) {
            final FlurstueckSchluesselCustomBean selectedFlurstueckSchluesselBean = (FlurstueckSchluesselCustomBean)
                selectedFlurstueckSchluessel;
            final FlurstueckSchluesselCustomBean key = FlurstueckSchluesselCustomBean.createNew();
            key.setGemarkung(selectedFlurstueckSchluesselBean.getGemarkung());
            key.setFlur(selectedFlurstueckSchluesselBean.getFlur());
            key.setFlurstueckZaehler(selectedFlurstueckSchluesselBean.getFlurstueckZaehler());
            key.setFlurstueckNenner(selectedFlurstueckSchluesselBean.getFlurstueckNenner());
            key.setId(selectedFlurstueckSchluesselBean.getId());
            return key;
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Mode getMode() {
        return currentMode;
    }

    @Override
    public void requestFlurstueck(final FlurstueckSchluesselCustomBean key) {
        doAutomaticRequest(AutomaticFlurstueckRetriever.FLURSTUECK_REQUEST_MODE, key);
    }

    @Override
    public void updateFlurstueckKeys() {
        final FlurstueckCustomBean currentFlurstueck = LagisBroker.getInstance().getCurrentFlurstueck();
        final Object currentKey = cboFlurstueck.getSelectedItem();
        if (currentFlurstueck != null) {
            doAutomaticRequest(
                AutomaticFlurstueckRetriever.FILTER_ACTION_MODE,
                currentFlurstueck.getFlurstueckSchluessel());
        } else if ((currentKey != null) && (currentKey instanceof FlurstueckSchluesselCustomBean)) {
            doAutomaticRequest(
                AutomaticFlurstueckRetriever.FILTER_ACTION_MODE,
                (FlurstueckSchluesselCustomBean)currentKey);
        } else {
            doAutomaticRequest(AutomaticFlurstueckRetriever.SET_BOXES_ACCORDING_TO_CONTENT_MODE, null);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key  DOCUMENT ME!
     */
    public void addRemoveFilter(final FlurstueckSchluesselCustomBean key) {
        removeFilter.add(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key  DOCUMENT ME!
     */
    public void removeRemoveFilter(final FlurstueckSchluesselCustomBean key) {
        removeFilter.remove(key);
    }

    @Override
    public String getWidgetName() {
        return WIDGET_NAME;
    }

    @Override
    public String getValidationMessage() {
        if (currentMode == Mode.SEARCH) {
            if (currentFlurstueckBean != null) {
                return "Es wurde ein Flurstück ausgewäählt";
            } else {
                return "Es wurde kein Flurstück ausgewählt";
            }
        } else if ((currentMode == Mode.CONTINUATION) || (currentMode == Mode.CONTINUATION_HISTORIC)) {
            if (currentFlurstueckBean != null) {
                return "Aktuell ausgewähltes Flurstück vollständig.";
            } else {
                return "Bitte vervollständigen Sie alle Flurstücke";
            }
        } else if (currentMode == Mode.CREATION) {
            return creationValidationMessage;
        } else {
            return "Unbekannter Modus";
        }
    }

    @Override
    public int getStatus() {
        if (currentMode == Mode.SEARCH) {
            if (currentFlurstueckBean != null) {
                return Validatable.VALID;
            } else {
                return Validatable.ERROR;
            }
        } else if ((currentMode == Mode.CONTINUATION) || (currentMode == Mode.CONTINUATION_HISTORIC)) {
            if (currentFlurstueckBean != null) {
                return Validatable.VALID;
            } else {
                return Validatable.ERROR;
            }
        } else if (currentMode == Mode.CREATION) {
            if (isFlurstueckCreateable && isFlurstueckCandidateValide) {
                return Validatable.VALID;
            } else {
                return Validatable.ERROR;
            }
        } else {
            return Validatable.ERROR;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key                 DOCUMENT ME!
     * @param  isFlurstueckValide  DOCUMENT ME!
     */
    private void checkIfFlurstueckIsAlreadyInDatabase(final FlurstueckSchluesselCustomBean key,
            final boolean isFlurstueckValide) {
        if ((currentFlurstueckChecker != null) && !currentFlurstueckChecker.isDone()) {
            currentFlurstueckChecker.cancel(false);
            currentFlurstueckChecker = null;
        }
        currentFlurstueckChecker = new FlurstueckChecker(key, isFlurstueckValide);
        LagisBroker.getInstance().execute(currentFlurstueckChecker);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboGemarkungActionPerformed(final java.awt.event.ActionEvent evt) {
        if (cboGemarkungListenerEnabled) {
            final Object selectedItem = cboGemarkung.getSelectedItem();
            if ((currentGemarkungsRetriever != null) && !currentGemarkungsRetriever.isDone()) {
                currentGemarkungsRetriever.cancel(false);
                currentGemarkungsRetriever = null;
            } else if ((currentGemarkungsRetriever != null) && currentGemarkungsRetriever.isDone()) {
                // ugly Winning
                if (currentGemarkungsRetriever.wasResolved) {
                    currentGemarkungsRetriever = new GemarkungRetriever(
                            GemarkungRetrieverMode.RETRIEVE_WITH_RESOLVED,
                            evt,
                            currentGemarkungsRetriever.selectedGemarkung);
                    LagisBroker.getInstance().execute(currentGemarkungsRetriever);
                    return;
                }
            }
            currentGemarkungsRetriever = new GemarkungRetriever(
                    GemarkungRetrieverMode.RETRIEVE_FLURE,
                    evt,
                    selectedItem);
            LagisBroker.getInstance().execute(currentGemarkungsRetriever);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboFlurActionPerformed(final java.awt.event.ActionEvent evt) {
        if (cboFlurListenerEnabled) {
            final Object selectedItem = cboFlur.getSelectedItem();
            if ((currentFlurRetriever != null) && !currentFlurRetriever.isDone()) {
                currentFlurRetriever.cancel(false);
                currentFlurRetriever = null;
            }
            currentFlurRetriever = new FlurRetriever(FlurRetriever.RETRIEVE_NORMAL_MODE, evt, selectedItem);
            LagisBroker.getInstance().execute(currentFlurRetriever);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboFlurstueckActionPerformed(final java.awt.event.ActionEvent evt) {
        if (cboFlurstueckListenerEnabled) {
            final Object selectedItem = cboFlurstueck.getSelectedItem();
            if ((currentFlurstueckRetriever != null) && !currentFlurstueckRetriever.isDone()) {
                currentFlurstueckRetriever.cancel(false);
                currentFlurstueckRetriever = null;
            }
            if ((currentWFSRetriever != null) && !currentWFSRetriever.isDone()) {
                currentWFSRetriever.cancel(false);
                currentWFSRetriever = null;
            }
            currentFlurstueckRetriever = new FlurstueckRetriever(
                    FlurstueckRetriever.RETRIEVE_NORMAL_MODE,
                    evt,
                    selectedItem);
            LagisBroker.getInstance().execute(currentFlurstueckRetriever);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  listener  DOCUMENT ME!
     */
    public void addComboBoxListener(final ActionListener listener) {
        cboGemarkung.addActionListener(listener);
        cboFlur.addActionListener(listener);
        cboFlurstueck.addActionListener(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  listener  DOCUMENT ME!
     */
    public void removeActionListener(final ActionListener listener) {
        cboGemarkung.removeActionListener(listener);
        cboFlur.removeActionListener(listener);
        cboFlurstueck.removeActionListener(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  status  DOCUMENT ME!
     */
    public void setStatus(final Status status) {
        switch (status) {
            case STAEDTISCH_HISTORIC: {
                btnAction.setIcon(icoStaedtischHistoric);
            }
            break;
            case STAEDTISCH: {
                btnAction.setIcon(icoStaedtisch);
            }
            break;
            case ABTEILUNG_IX_HISTORIC: {
                btnAction.setIcon(icoAbteilungIXHistoric);
            }
            break;
            case ABTEILUNG_IX: {
                btnAction.setIcon(icoAbteilungIX);
            }
            break;
            case UNKNOWN_FLURSTUECK: {
                btnAction.setIcon(icoUnknownFlurstueck);
            }
            break;
            case WFS_WARN: {
                btnAction.setIcon(icoWFSWarn);
            }
            break;
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class GemarkungRetriever extends SwingWorker<List<Key>, Void> {

        //~ Instance fields ----------------------------------------------------

        private GemarkungRetrieverMode mode;
        private ActionEvent event;
        private Object selectedItem;
        private GemarkungCustomBean selectedGemarkung;
        private boolean wasResolved = false;
        private boolean hadErrors = false;
        private boolean isAutoComplete = false;
        private String errorMessage = null;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new GemarkungRetriever object.
         *
         * @param  mode  DOCUMENT ME!
         */
        public GemarkungRetriever(final GemarkungRetrieverMode mode) {
            this(mode, null, null);
        }

        /**
         * Creates a new GemarkungRetriever object.
         *
         * @param  mode          DOCUMENT ME!
         * @param  event         DOCUMENT ME!
         * @param  selectedItem  DOCUMENT ME!
         */
        public GemarkungRetriever(final GemarkungRetrieverMode mode,
                final ActionEvent event,
                final Object selectedItem) {
            this.mode = mode;
            this.event = event;
            this.selectedItem = selectedItem;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected List<Key> doInBackground() throws Exception {
            try {
                if (mode == GemarkungRetrieverMode.RETRIEVE_GEMARKUNGEN) {
                    // Abrufen aller Gemarkungen
                    if (isCancelled()) {
                        return null;
                    }
                    final Collection<GemarkungCustomBean> gemKeys = CidsBroker.getInstance().getGemarkungsKeys();
                    if (isCancelled()) {
                        return null;
                    }
                    List<Key> gemKeyList = null;
                    if (gemKeys != null) {
                        gemKeyList = new ArrayList<Key>(gemKeys);
                        if (isCancelled()) {
                            return null;
                        }
                        Collections.sort(gemKeyList);
                    }
                    if (gemKeyList == null) {
                        gemKeyList = new ArrayList<Key>();
                    }
                    return gemKeyList;
                } else if ((((mode == GemarkungRetrieverMode.RETRIEVE_FLURE)
                                    || (GemarkungRetrieverMode.RETRIEVE_WITH_RESOLVED == mode))
                                && (event != null))
                            || (mode == GemarkungRetrieverMode.RETRIEVE_AUTOMATIC)) {
                    // GemarkungCustomBean wurde ausgewählt
                    if ((mode == GemarkungRetrieverMode.RETRIEVE_AUTOMATIC)
                                || (event.getSource() instanceof JComboBox)) {
                        if ((mode == GemarkungRetrieverMode.RETRIEVE_AUTOMATIC)
                                    || event.getActionCommand().equals("comboBoxChanged")
                                    || (mode == GemarkungRetrieverMode.RETRIEVE_WITH_RESOLVED)) {
                            if (((mode == GemarkungRetrieverMode.RETRIEVE_AUTOMATIC)
                                            || ((event.getModifiers() & InputEvent.BUTTON1_MASK) != 0))
                                        || ((event.getModifiers() != 0)
                                            && ((event.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0))
                                        || (mode == GemarkungRetrieverMode.RETRIEVE_WITH_RESOLVED)) {
                                if (selectedItem instanceof GemarkungCustomBean) {
                                    selectedGemarkung = (GemarkungCustomBean)selectedItem;
                                    if (isCancelled()) {
                                        return null;
                                    }
                                    EventQueue.invokeLater(new Runnable() {

                                            @Override
                                            public void run() {
                                                pbGemarkung.setIndeterminate(true);
                                            }
                                        });

                                    final Collection<Key> flurKeys = CidsBroker.getInstance()
                                                .getDependingKeysForKey(selectedGemarkung);
                                    if (isCancelled()) {
                                        return null;
                                    }
                                    if (flurKeys != null) {
                                        if (isCancelled()) {
                                            return null;
                                        }
                                        final List<Key> flurKeyList = new ArrayList<Key>(flurKeys);
                                        if (isCancelled()) {
                                            return null;
                                        }
                                        Collections.sort(flurKeyList);
                                        return flurKeyList;
                                    } else {
                                        return new ArrayList<Key>();
                                    }
                                } else {
                                    selectedGemarkung = null;
                                    return null;
                                }
                            } else {
                                isAutoComplete = true;
                                selectedGemarkung = null;
                                return null;
                            }
                        } else if (event.getActionCommand().equals("comboBoxEdited")) {
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        pbGemarkung.setIndeterminate(true);
                                    }
                                });

                            final String gemInput = ((JComboBox)event.getSource()).getEditor().getItem().toString();
                            try {
                                if (isCancelled()) {
                                    return null;
                                }
                                selectedGemarkung = LagisBroker.getInstance()
                                            .getGemarkungForKey(Integer.parseInt(gemInput));
                                if (isCancelled()) {
                                    return null;
                                }
                            } catch (NumberFormatException ex) {
                                if ((gemInput != null) && gemInput.equals("")) {
                                    // Eingabe ist Leerstring;
                                    selectedGemarkung = null;
                                    return null;
                                } else {
                                    selectedGemarkung = GemarkungCustomBean.createNew();
                                    selectedGemarkung.setBezeichnung(gemInput);
                                    if (isCancelled()) {
                                        return null;
                                    }
                                    selectedGemarkung = CidsBroker.getInstance().completeGemarkung(selectedGemarkung);
                                }
                            }
                            if ((selectedGemarkung != null) && (selectedGemarkung.getId() != null)) {
                                wasResolved = true;
                                return null;
                            } else {
                                hadErrors = true;
                                errorMessage = "Unbekannte Gemarkung";
                                selectedGemarkung = null;
                                return null;
                            }
                        } else {
                            selectedGemarkung = null;
                            return null;
                        }
                    } else {
                        LOG.warn("Eventsource nicht bekannt");
                        selectedGemarkung = null;
                        return null;
                    }
                } else {
                    LOG.warn("unkown mode oder Event == null oder selectedItem == null");
                    selectedGemarkung = null;
                    return null;
                }
            } catch (Exception ex) {
                LOG.error("Fehler beim Abrufen der Gemarkungen/Flure", ex);
                return null;
            }
        }

        @Override
        protected void done() {
            try {
                pbGemarkung.setIndeterminate(false);
                if (isAutoComplete) {
                    cboFlur.setModel(new KeyComboboxModel());
                    cboFlur.setEnabled(false);
                    return;
                }
                if (isCancelled()) {
                    return;
                }
                if (GemarkungRetrieverMode.RETRIEVE_GEMARKUNGEN == mode) {
                    // Setzen der Gemarkungen
                    try {
                        cboGemarkung.setModel(new KeyComboboxModel(get()));
//                if(currentMode != CONTINUATION_MODE || (isDynamicCreated)){
                        cboGemarkung.setEnabled(true);

                        // }
                        isFullInitialized = true;
                        if (currentGemarkungsWaiter == null) {
                            cboGemarkung.requestFocus();
                        }
                    } catch (final Exception ex) {
                        LOG.error("Fehler beim setzten der Gemarkungen", ex);
                    }
                } else if ((mode == GemarkungRetrieverMode.RETRIEVE_FLURE)
                            || (mode == GemarkungRetrieverMode.RETRIEVE_WITH_RESOLVED)
                            || (mode == GemarkungRetrieverMode.RETRIEVE_AUTOMATIC)) {
                    if (selectedGemarkung != null) {
                        if (!wasResolved) {
                            setHighlightColor(LagisBroker.ACCEPTED_COLOR);
                            cboFlur.setModel(new KeyComboboxModel(get()));
                            cboFlur.setEnabled(true);
                            if (mode != GemarkungRetrieverMode.RETRIEVE_AUTOMATIC) {
                                cboFlur.requestFocus();
                            }
                        } else {
                            // ToDO betterway ?
                            cboGemarkungListenerEnabled = false;
                            cboGemarkung.setSelectedItem(selectedGemarkung);
                            cboGemarkungListenerEnabled = true;
                            setHighlightColor(LagisBroker.ACCEPTED_COLOR);
                            cboGemarkungActionPerformed(event);
                        }
                    } else {
                        if (hadErrors) {
                            btnAction.setToolTipText(errorMessage);
                            setHighlightColor(LagisBroker.ERROR_COLOR);
                        } else {
                            setHighlightColor(LagisBroker.ACCEPTED_COLOR);
                            btnAction.setToolTipText("");
                        }
                        cboFlur.setModel(new KeyComboboxModel());
                        cboFlur.setEnabled(false);
                    }
                    // GemarkungCustomBean wurde ausgewählt
                } else {
                    LOG.warn("unkown mode");
                }
            } catch (final Exception ex) {
                LOG.error("Fehler beim setzten der Gemarkung/Flure (done)", ex);
                setHighlightColor(LagisBroker.ERROR_COLOR);
                cboFlur.setModel(new KeyComboboxModel());
                cboFlur.setEnabled(false);
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public GemarkungRetrieverMode getMode() {
            return mode;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class FlurRetriever extends SwingWorker<List<Key>, Void> {

        //~ Static fields/initializers -----------------------------------------

        private static final int RETRIEVE_NORMAL_MODE = 0;
        private static final int RETRIEVE_AUTOMATIC_MODE = 1;

        //~ Instance fields ----------------------------------------------------

        private int mode;
        private boolean isAutoComplete = false;
        private FlurKey selectedFlur;
        private String errorMessage = null;
        private boolean hadErrors = false;
        private final Object selectedItem;
        private final ActionEvent event;
        private boolean flurWasCreated = false;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new FlurRetriever object.
         *
         * @param  mode          DOCUMENT ME!
         * @param  event         DOCUMENT ME!
         * @param  selectedItem  DOCUMENT ME!
         */
        public FlurRetriever(final int mode, final ActionEvent event, final Object selectedItem) {
            this.mode = mode;
            this.event = event;
            this.selectedItem = selectedItem;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected List<Key> doInBackground() throws Exception {
            try {
                if ((RETRIEVE_AUTOMATIC_MODE == mode) || (event.getSource() instanceof JComboBox)) {
                    if ((RETRIEVE_AUTOMATIC_MODE == mode) || event.getActionCommand().equals("comboBoxChanged")) {
                        if ((RETRIEVE_AUTOMATIC_MODE == mode) || ((event.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
                                    || ((event.getModifiers() != 0)
                                        && ((event.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0))) {
                            if (selectedItem instanceof FlurKey) {
                                selectedFlur = (FlurKey)selectedItem;
                                if (isCancelled()) {
                                    return null;
                                }
                            } else {
                                selectedFlur = null;
                                return null;
                            }
                        } else {
                            isAutoComplete = true;
                            selectedFlur = null;
                            return null;
                        }
                    } else if (event.getActionCommand().equals("comboBoxEdited")) {
                        final String flurInput = ((JComboBox)event.getSource()).getEditor().getItem().toString();
                        try {
                            if ((flurInput != null) && flurInput.equals("")) {
                                selectedFlur = null;
                                return null;
                            }
                            final GemarkungCustomBean currentGemarkung = (GemarkungCustomBean)
                                cboGemarkung.getSelectedItem();
                            if (currentGemarkung != null) {
                                selectedFlur = new FlurKey(currentGemarkung, Integer.parseInt(flurInput));
                                final KeyComboboxModel flurModel = (KeyComboboxModel)cboFlur.getModel();
                                if ((currentMode == Mode.SEARCH) || (currentMode == Mode.CREATION)) {
                                    if (!flurModel.contains(selectedFlur)) {
                                        flurWasCreated = true;
                                    }
                                } else if (flurModel.contains(selectedFlur)) {
                                } else {
                                    hadErrors = true;
                                    errorMessage = "Der eingegebene Flur existiert nicht";
                                    return null;
                                }
                            } else {
                                selectedFlur = null;
                                return null;
                            }
                        } catch (Exception ex) {
                            LOG.error(
                                "Fehler beim erstellen des FlurKeys --> keine Möglichkeit Flurstücke zu bestimmen",
                                ex);
                            hadErrors = true;
                            errorMessage = "Fehlerhaftes Format des Flurs.";
                            selectedFlur = null;
                            return null;
                        }
                        if (isCancelled()) {
                            return null;
                        }
                    } else {
                        selectedFlur = null;
                        return null;
                    }
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                pbFlur.setIndeterminate(true);
                            }
                        });
                    // TODO Implement Filter
                    selectedFlur.setCurrentFilterEnabled(filter == Filter.CURRENT);
                    selectedFlur.setHistoricFilterEnabled(filter == Filter.HISTORIC);
                    selectedFlur.setAbteilungXIFilterEnabled(filter == Filter.ABTEILUNG_IX);
                    selectedFlur.setStaedtischFilterEnabled(filter == Filter.STAEDTISCH);
                    final Collection<Key> flurKeys = CidsBroker.getInstance().getDependingKeysForKey(selectedFlur);
                    if (isCancelled()) {
                        return null;
                    }
                    if (flurKeys != null) {
                        if (isCancelled()) {
                            return null;
                        }
                        final List<Key> flurKeyList = new ArrayList<Key>(flurKeys);
                        if (isCancelled()) {
                            return null;
                        }
                        Collections.sort(flurKeyList);
                        // TODO RemoveFilter

                        return flurKeyList;
                    } else {
                        return new ArrayList<Key>();
                    }
                } else {
                    LOG.warn("Eventsource nicht bekannt");
                    selectedFlur = null;
                    return null;
                }
            } catch (Exception ex) {
                LOG.error("Fehler beim Abrufen der Flurstücke", ex);
            }
            selectedFlur = null;
            return null;
        }

        @Override
        protected void done() {
            try {
                pbFlur.setIndeterminate(false);
                if (isAutoComplete) {
                    cboFlurstueck.setModel(new KeyComboboxModel());
                    synchronized (isFlurstueckCreateable) {
                        isFlurstueckCreateable = false;
                    }
                    setHighlightColor(LagisBroker.ACCEPTED_COLOR);
                    txtFlurstueck.setText("");
                    cboFlurstueck.setEnabled(false);
                    txtFlurstueck.setEnabled(false);
                    return;
                }
                if (isCancelled()) {
                    return;
                }
                if ((selectedFlur != null) && !hadErrors) {
                    final KeyComboboxModel flurModel = (KeyComboboxModel)cboFlur.getModel();
                    if ((currentMode == Mode.CREATION) && flurWasCreated) {
                        flurModel.addElement(selectedFlur);
                    }
                    setHighlightColor(LagisBroker.ACCEPTED_COLOR);
                    final KeyComboboxModel model = new KeyComboboxModel(get());
                    final Iterator<FlurstueckSchluesselCustomBean> it = removeFilter.iterator();
                    while (it.hasNext()) {
                        model.removeElement(it.next());
                    }
                    setHighlightColor(LagisBroker.ACCEPTED_COLOR);
                    cboFlurstueck.setModel(model);
                    cboFlurstueck.setEnabled(true);
                    txtFlurstueck.setEnabled(true);
                    if ((currentAutomaticRetriever == null) || currentAutomaticRetriever.isDone()
                                || currentAutomaticRetriever.isCancelled()) {
                        if (currentMode == Mode.CREATION) {
                            txtFlurstueck.requestFocus();
                        } else {
                            cboFlurstueck.requestFocus();
                        }
                    } else if ((currentAutomaticRetriever != null)
                                && (currentAutomaticRetriever.getMode()
                                    == AutomaticFlurstueckRetriever.COPY_CONTENT_MODE)) {
                        cboFlurstueck.requestFocus();
                    }
                } else {
                    if (hadErrors) {
                        setHighlightColor(LagisBroker.ERROR_COLOR);
                        btnAction.setToolTipText(errorMessage);
                    } else {
                        setHighlightColor(LagisBroker.ACCEPTED_COLOR);
                        btnAction.setToolTipText("");
                    }
                    synchronized (isFlurstueckCreateable) {
                        isFlurstueckCreateable = false;
                    }
                    txtFlurstueck.setText("");
                    cboFlurstueck.setModel(new KeyComboboxModel());
                    cboFlurstueck.setEnabled(false);
                    txtFlurstueck.setEnabled(false);
                }
            } catch (final Exception ex) {
                LOG.error("Fehler beim setzten der Flurstücke (done)", ex);
                setHighlightColor(LagisBroker.ERROR_COLOR);
                synchronized (isFlurstueckCreateable) {
                    isFlurstueckCreateable = false;
                }
                txtFlurstueck.setText("");
                cboFlurstueck.setModel(new KeyComboboxModel());
                cboFlurstueck.setEnabled(false);
                txtFlurstueck.setEnabled(false);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class FlurstueckRetriever extends SwingWorker<FlurstueckCustomBean, Void> {

        //~ Static fields/initializers -----------------------------------------

        private static final int RETRIEVE_NORMAL_MODE = 0;
        private static final int RETRIEVE_AUTOMATIC_MODE = 1;

        //~ Instance fields ----------------------------------------------------

        private int mode;
        private boolean isAutoComplete = false;
        private FlurstueckSchluesselCustomBean selectedFlurstueck;
        private String errorMessage = null;
        private boolean hadErrors = false;
        private boolean isFlurstueckInDatabase = true;
        private final Object selectedItem;
        private final ActionEvent event;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new FlurstueckRetriever object.
         *
         * @param  mode          DOCUMENT ME!
         * @param  event         DOCUMENT ME!
         * @param  selectedItem  DOCUMENT ME!
         */
        public FlurstueckRetriever(final int mode, final ActionEvent event, final Object selectedItem) {
            this.mode = mode;
            this.event = event;
            this.selectedItem = selectedItem;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected FlurstueckCustomBean doInBackground() throws Exception {
            try {
                if ((mode == RETRIEVE_AUTOMATIC_MODE) || (event.getSource() instanceof JComboBox)) {
                    if ((mode == RETRIEVE_AUTOMATIC_MODE) || event.getActionCommand().equals("comboBoxChanged")) {
                        if ((mode == RETRIEVE_AUTOMATIC_MODE) || ((event.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
                                    || ((event.getModifiers() != 0)
                                        && ((event.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0))) {
                            if (selectedItem instanceof FlurstueckSchluesselCustomBean) {
                                selectedFlurstueck = (FlurstueckSchluesselCustomBean)selectedItem;
                                if (isCancelled()) {
                                    return null;
                                }
                            } else {
                                selectedFlurstueck = null;
                                return null;
                            }
                        } else {
                            isAutoComplete = true;
                            selectedFlurstueck = null;
                            return null;
                        }
                    } else if (event.getActionCommand().equals("comboBoxEdited")) {
                        selectedFlurstueck = FlurstueckSchluesselCustomBean.createNew();
                        final Object tmpFlur = cboFlur.getSelectedItem();
                        final FlurKey currentFlur;
                        if (tmpFlur instanceof FlurKey) {
                            currentFlur = (FlurKey)tmpFlur;
                        } else if (tmpFlur instanceof String) {
                            currentFlur = new FlurKey((GemarkungCustomBean)cboGemarkung.getSelectedItem(),
                                    Integer.parseInt((String)tmpFlur));
                        } else {
                            LOG.warn("Unbekanntes Objekt in cboFlur");
                            currentFlur = null;
                        }
                        if (currentFlur == null) {
                            selectedFlurstueck = null;
                            return null;
                        }
                        selectedFlurstueck.setGemarkung(currentFlur.getGemarkung());
                        selectedFlurstueck.setFlur(currentFlur.getFlurId());

                        final String flurstueck = cboFlurstueck.getEditor().getItem().toString();
                        if (isCancelled()) {
                            return null;
                        }
                        if ((flurstueck != null) && (flurstueck.length() > 0)) {
                            try {
                                final String[] tokens = flurstueck.split("/");
                                // TODO the user input is not validated
                                switch (tokens.length) {
                                    case 1: {
                                        selectedFlurstueck.setFlurstueckZaehler(Integer.parseInt(tokens[0]));
                                        selectedFlurstueck.setFlurstueckNenner(0);
                                        break;
                                    }
                                    case 2: {
                                        selectedFlurstueck.setFlurstueckZaehler(Integer.parseInt(tokens[0]));
                                        selectedFlurstueck.setFlurstueckNenner(Integer.parseInt(tokens[1]));
                                        break;
                                    }
                                    default: {
                                        LOG.warn(
                                            "Falsche Eingabe erwarted wird ein Flurstueck ohne oder mit Nenner z.B. 10\n");
                                        selectedFlurstueck = null;
                                        return null;
                                    }
                                }
                            } catch (NumberFormatException ex) {
                                LOG.error("Fehler beim parsen des FlurstückSchlüssels", ex);
                                hadErrors = true;
                                errorMessage = "Format des Flurstückszähler/-nenner stimmt nicht (Zahl/Zahl)";
                                selectedFlurstueck = null;
                                return null;
                            }
                        } else {
                            if ((flurstueck != null) && flurstueck.equals("")) {
                                selectedFlurstueck = null;
                                return null;
                            } else {
                                LOG.warn("Unbekannter Fall");
                                selectedFlurstueck = null;
                                return null;
                            }
                        }
                    } else {
                        selectedFlurstueck = null;
                        return null;
                    }
                    if (isCancelled()) {
                        return null;
                    }
                    final FlurstueckSchluesselCustomBean tmpKey = CidsBroker.getInstance()
                                .completeFlurstueckSchluessel(selectedFlurstueck);
                    if (isCancelled()) {
                        return null;
                    }
                    // Ist Flurstück in Datenbank ??
                    if (tmpKey != null) {
                        selectedFlurstueck = tmpKey;
                    }

                    if (selectedFlurstueck.getId() != null) {
                        if (currentMode == Mode.SEARCH) {
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        pbFlurstueck.setIndeterminate(true);
                                        pbTxtFlurstueck.setIndeterminate(true);
                                    }
                                });

                            if (!LagisBroker.getInstance().isInEditMode()) {
                                // wfsUpdater.notifyThread(null);
                                if (isCancelled()) {
                                    return null;
                                }
                                final FlurstueckCustomBean flurstueck = CidsBroker.getInstance()
                                            .retrieveFlurstueck(selectedFlurstueck);
                                if (isCancelled()) {
                                    return null;
                                }
                                // TODO notice user if there is no flurstueck
                                if (flurstueck != null) {
                                    return flurstueck;
                                } else {
                                    selectedFlurstueck = null;
                                    return null;
                                }
                            } else {
                                if (isCancelled()) {
                                    return null;
                                }
                                JOptionPane.showMessageDialog(LagisBroker.getInstance().getParentComponent(),
                                    "Das Flurstück kann nur gewechselt werden wenn alle Änderungen gespeichert oder verworfen worden sind.",
                                    "Wechseln nicht möglich",
                                    JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            final FlurstueckCustomBean flurstueck = CidsBroker.getInstance()
                                        .retrieveFlurstueck(selectedFlurstueck);
                            if (flurstueck != null) {
                                return flurstueck;
                            } else {
                                selectedFlurstueck = null;
                                return null;
                            }
                        }
                    } else {
                        if ((currentMode == Mode.CONTINUATION) || (currentMode == Mode.CONTINUATION_HISTORIC)) {
                            hadErrors = true;
                            errorMessage = "Das eingegebene Flurstück existiert nicht";
                            return null;
                        } else {
                            final FlurstueckCustomBean container = FlurstueckCustomBean.createNew();
                            container.setFlurstueckSchluessel(selectedFlurstueck);
                            isFlurstueckInDatabase = false;
                            return container;
                        }
                    }
                } else {
                    LOG.warn("Eventsource nicht bekannt");
                    selectedFlurstueck = null;
                    return null;
                }
            } catch (Exception ex) {
                LOG.error("Fehler beim Abrufen des Flurstücks", ex);
                hadErrors = true;
                errorMessage = "Flurstück konnte nicht abgerufen werden";
            }
            selectedFlurstueck = null;
            return null;
        }

        @Override
        protected void done() {
            try {
                pbFlurstueck.setIndeterminate(false);
                pbTxtFlurstueck.setIndeterminate(false);
                if (isAutoComplete) {
                    return;
                }
                if (isCancelled()) {
                    return;
                }

                if ((selectedFlurstueck != null) && !hadErrors) {
                    final FlurstueckCustomBean result = get();
                    currentFlurstueckBean = result;
                    if (result != null) {
                        if (isFlurstueckInDatabase) {
                            setHighlightColor(LagisBroker.SUCCESSFUL_COLOR);
                            if (currentMode == Mode.SEARCH) {
                                LagisBroker.getInstance().fireFlurstueckChanged(result);
                            }
                        } else {
                            if (currentMode == Mode.SEARCH) {
                                setHighlightColor(LagisBroker.UNKOWN_COLOR);
                                LagisBroker.getInstance()
                                        .setCurrentFlurstueckSchluessel(result.getFlurstueckSchluessel(), true);
                                LagisBroker.getInstance().resetWidgets();
                                LagisBroker.getInstance()
                                        .getMappingComponent()
                                        .getFeatureCollection()
                                        .removeAllFeatures();
                                if ((currentWFSRetriever != null) && !currentWFSRetriever.isDone()) {
                                    currentWFSRetriever.cancel(false);
                                    currentWFSRetriever = null;
                                }
                                final HashMap<Integer, Boolean> properties = new HashMap<Integer, Boolean>();
                                properties.put(WFSRequestJobDone.HAS_MANY_VERWALTUNGSBEREICHE, false);
                                properties.put(WFSRequestJobDone.IS_NO_GEOMETRY_ASSIGNED, true);
                                currentWFSRetriever = WFSRetrieverFactory.getInstance()
                                            .getWFSRetriever(result.getFlurstueckSchluessel(),
                                                    new WFSRequestJobDone(),
                                                    properties);
                                // currentWFSRetriever = new WFSRetriever(result.getFlurstueckSchluessel(),false,true);

                                LagisBroker.getInstance().execute(currentWFSRetriever);
                            }
                        }
                    }
                } else {
                    if (hadErrors) {
                        setHighlightColor(LagisBroker.ERROR_COLOR);
                        btnAction.setToolTipText(errorMessage);
                    } else {
                        btnAction.setToolTipText("");
                    }
                }
            } catch (final Exception ex) {
                LOG.error("Fehler beim abrufen des Flurstücks (done)", ex);
                setHighlightColor(LagisBroker.ERROR_COLOR);
            }
            fireValidationStateChanged(FlurstueckChooser.this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class FlurstueckChecker extends SwingWorker<Boolean, Void> {

        //~ Instance fields ----------------------------------------------------

        private FlurstueckSchluesselCustomBean keyToCheck;
        private boolean isFlurstueckCandidateValide;
        private boolean hadErrors;
        private String errorMessage;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new FlurstueckChecker object.
         *
         * @param  keyToCheck                   DOCUMENT ME!
         * @param  isFlurstueckCandidateValide  DOCUMENT ME!
         */
        public FlurstueckChecker(final FlurstueckSchluesselCustomBean keyToCheck,
                final boolean isFlurstueckCandidateValide) {
            this.keyToCheck = keyToCheck;
            this.isFlurstueckCandidateValide = isFlurstueckCandidateValide;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Boolean doInBackground() throws Exception {
            try {
                if (isCancelled()) {
                    return null;
                }
                if (isFlurstueckCandidateValide) {
                    keyToCheck = CidsBroker.getInstance().completeFlurstueckSchluessel(keyToCheck);
                    return keyToCheck != null;
                } else {
                    return false;
                }
            } catch (Exception ex) {
                LOG.error("Fehler beim checken des Flurstuecks: " + ex);
                hadErrors = true;
                errorMessage = "Fehler beim prüfen des Flurstücks";
                return false;
            }
        }

        @Override
        protected void done() {
            try {
                if (isCancelled()) {
                    return;
                }
                if (hadErrors) {
                    txtFlurstueck.setToolTipText(errorMessage);
                    isFlurstueckCreateable = false;
                    return;
                }
                if (isFlurstueckCandidateValide) {
                    final Boolean keyAlreadyExisiting = get();
                    if (keyAlreadyExisiting) {
                        setHighlightColor(LagisBroker.ERROR_COLOR);
                        txtFlurstueck.setToolTipText("Flurstück ist bereits vorhanden");
                        isFlurstueckCreateable = false;
                        creationValidationMessage = txtFlurstueck.getToolTipText();
                        fireValidationStateChanged(this);
                    } else {
                        setHighlightColor(LagisBroker.SUCCESSFUL_COLOR);
                        txtFlurstueck.setToolTipText("Flurstück ist noch nicht vorhanden und kann angelegt werden");
                        creationValidationMessage = txtFlurstueck.getToolTipText();
                        isFlurstueckCreateable = true;
                        fireValidationStateChanged(this);
                    }
                } else {
                    txtFlurstueck.setToolTipText("Der Flurstücksschlüssel ist nicht valide");
                    creationValidationMessage = txtFlurstueck.getToolTipText();
                    isFlurstueckCreateable = false;
                    fireValidationStateChanged(this);
                }
            } catch (Exception ex) {
                LOG.error("Fehler beim checken des Flurstücks (done)", ex);
                isFlurstueckCreateable = false;
                fireValidationStateChanged(this);
            }
            validationMessageLock.lock();
            try {
                creationValidationMessage = txtFlurstueck.getToolTipText();
            } finally {
                validationMessageLock.unlock();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class WFSRequestJobDone implements DoneDelegate<Geometry, Void> {

        //~ Static fields/initializers -----------------------------------------

        private static final int HAS_MANY_VERWALTUNGSBEREICHE = 0;
        private static final int IS_NO_GEOMETRY_ASSIGNED = 1;

        //~ Methods ------------------------------------------------------------

        @Override
        public void jobDone(final ExtendedSwingWorker<Geometry, Void> worker,
                final HashMap<Integer, Boolean> properties) {
            try {
                if (worker.isCancelled()) {
                    LagisBroker.getInstance().flurstueckChangeFinished(FlurstueckChooser.this);
                    return;
                }

                final Geometry result = worker.get();
                result.setSRID(25832);
                LagisBroker.getInstance().setCurrentWFSGeometry(result);
                LOG.info("CurrentWFSGeometry SRS=" + ((result != null) ? result.getSRID() : "?") + " " + result);
                if (worker.hadErrors()) {
                    btnAction.setToolTipText(worker.getErrorMessage());
                    setHighlightColor(LagisBroker.ERROR_COLOR);
                    setStatus(Status.WFS_WARN);
                    LagisBroker.getInstance().flurstueckChangeFinished(FlurstueckChooser.this);
                    return;
                } else {
                    btnAction.setToolTipText("");
                }

                if ((worker.getKeyObject() != null)
                            && (worker.getKeyObject() instanceof FlurstueckSchluesselCustomBean)) {
                    final FlurstueckSchluesselCustomBean flurstueckKey = (FlurstueckSchluesselCustomBean)
                        worker.getKeyObject();
                    if (result != null) {
                        // TODO GUESSING ACHTUNG FALLS FEHLER
                        if (flurstueckKey.getId() == null) {
                            LagisBroker.getInstance().setCurrentFlurstueckSchluessel(flurstueckKey, true);
                        }
                        Feature tmpFeature = null;
                        if (properties != null) {
                            final boolean hasManyVerwaltungsbereiche =
                                (properties.get(HAS_MANY_VERWALTUNGSBEREICHE) != null)
                                ? properties.get(HAS_MANY_VERWALTUNGSBEREICHE) : false;
                            final boolean isNoGeometryAssigned = (properties.get(IS_NO_GEOMETRY_ASSIGNED) != null)
                                ? properties.get(IS_NO_GEOMETRY_ASSIGNED) : false;
                            if (!hasManyVerwaltungsbereiche && isNoGeometryAssigned) {
                                tmpFeature = new DefaultStyledFeature();
                                tmpFeature.setEditable(false);
                                ((DefaultStyledFeature)tmpFeature).setCanBeSelected(false);

                                final FlurstueckArtCustomBean flurstueckArt = flurstueckKey.getFlurstueckArt();
                                final DefaultStyledFeature styledFeature = (DefaultStyledFeature)tmpFeature;
                                final String flurstueckArtBez = (flurstueckArt != null) ? flurstueckArt
                                                .getBezeichnung() : null;
                                final Date gueltigBis = flurstueckKey.getGueltigBis();

                                if (flurstueckArt == null) {
                                    styledFeature.setFillingPaint(LagisBroker.UNKNOWN_FILLING_COLOR);
                                } else if (
                                    FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                                                flurstueckArtBez)
                                            && (gueltigBis == null)) {
                                    styledFeature.setFillingPaint(LagisBroker.STADT_FILLING_COLOR);
                                } else if ((gueltigBis != null)
                                            && (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                                                    flurstueckArtBez)
                                                || FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX
                                                .equals(flurstueckArtBez))) {
                                    styledFeature.setFillingPaint(LagisBroker.HISTORIC_FLURSTUECK_COLOR);
                                } else if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX.equals(
                                                flurstueckArtBez)) {
                                    styledFeature.setFillingPaint(LagisBroker.ABTEILUNG_IX_FILLING_COLOR);
                                } else {
                                    styledFeature.setFillingPaint(LagisBroker.UNKNOWN_FILLING_COLOR);
                                }
                                tmpFeature.setGeometry(result);
                                tmpFeature = new StyledFeatureGroupWrapper((StyledFeature)tmpFeature,
                                        FEATURE_GRP,
                                        FEATURE_GRP);
                            } else if (hasManyVerwaltungsbereiche && isNoGeometryAssigned) {
                                tmpFeature = new PureNewFeature(result);
                                tmpFeature.setEditable(true);
                                ((PureNewFeature)tmpFeature).setCanBeSelected(true);
                            } else {
                                LOG.warn("Nicht vorgesehner Fall !! --> Der Karte wird nichts hinzugefügt!");
                            }
                        } else {
                            LOG.error("Properties sind null --> kann kein Feature hinzufügen");
                        }
                        if (tmpFeature != null) {
                            LagisBroker.getInstance()
                                    .getMappingComponent()
                                    .getFeatureCollection()
                                    .addFeature(tmpFeature);
                        }
                    } else {
                        LagisBroker.getInstance().setCurrentWFSGeometry(null);
                    }
                } else {
                    LagisBroker.getInstance().setCurrentWFSGeometry(null);
                }
            } catch (final Exception ex) {
                LOG.error("Fehler beim abrufen der WFS Geometrie (done)", ex);
                LagisBroker.getInstance().setCurrentWFSGeometry(null);
            }
            LagisBroker.getInstance().flurstueckChangeFinished(FlurstueckChooser.this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public class AutomaticFlurstueckRetriever extends SwingWorker<Void, Void> implements PropertyChangeListener {

        //~ Static fields/initializers -----------------------------------------

        public static final int FLURSTUECK_REQUEST_MODE = 0;
        public static final int FILTER_ACTION_MODE = 1;
        public static final int SET_BOXES_ACCORDING_TO_CONTENT_MODE = 2;
        public static final int COPY_CONTENT_MODE = 3;

        //~ Instance fields ----------------------------------------------------

        private final Color initialColor;
        private int mode;
        private FlurstueckSchluesselCustomBean key;
        private boolean isFinished = false;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new AutomaticFlurstueckRetriever object.
         *
         * @param  mode  DOCUMENT ME!
         * @param  key   DOCUMENT ME!
         */
        public AutomaticFlurstueckRetriever(final int mode, final FlurstueckSchluesselCustomBean key) {
            this(mode, key, null);
        }

        /**
         * Creates a new AutomaticFlurstueckRetriever object.
         *
         * @param  mode          DOCUMENT ME!
         * @param  key           DOCUMENT ME!
         * @param  initialColor  DOCUMENT ME!
         */
        public AutomaticFlurstueckRetriever(final int mode,
                final FlurstueckSchluesselCustomBean key,
                final Color initialColor) {
            this.key = key;
            this.mode = mode;
            this.initialColor = initialColor;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Void doInBackground() throws Exception {
            try {
                if (key == null) {
                    return null;
                }
                while (!isCancelled() && !isFinished) {
                    Thread.currentThread().sleep(100);
                }
            } catch (Exception e) {
                LOG.error("Fehler beim automatischen abrufen eines Flurstücks.", e);
            }
            return null;
        }

        @Override
        protected void done() {
            if (isCancelled()) {
                return;
            } else if ((key != null) && !isFinished) {
                LOG.warn("Fehler beim automatischen Request (done)");
            }
            cboGemarkungListenerEnabled = true;
            cboFlurListenerEnabled = true;
            cboFlurstueckListenerEnabled = true;
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            if ((evt.getSource() instanceof GemarkungRetriever)
                        && evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                // cboFlur.setSelectedItem(new FlurKey(key.getGemarkung(),key.getFlur()));
                if ((currentFlurRetriever != null) && !currentFlurRetriever.isDone()) {
                    currentFlurRetriever.cancel(false);
                    currentFlurRetriever = null;
                }

                final FlurKey newFlur = new FlurKey(key.getGemarkung(), key.getFlur());
                cboFlur.setSelectedItem(newFlur);
                currentFlurRetriever = new FlurRetriever(FlurRetriever.RETRIEVE_AUTOMATIC_MODE, null, newFlur);
                setPropertyChangeListener(currentFlurRetriever);
                LagisBroker.getInstance().execute(currentFlurRetriever);
            } else if ((evt.getSource() instanceof FlurRetriever)
                        && evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                if ((AutomaticFlurstueckRetriever.FLURSTUECK_REQUEST_MODE == mode)
                            || (((filter != Filter.CURRENT) && (filter != Filter.HISTORIC))
                                || ((filter == Filter.CURRENT) && (key.getGueltigBis() == null))
                                || ((filter == Filter.HISTORIC) && (key.getGueltigBis() != null)))) {
                    if ((currentFlurstueckRetriever != null) && !currentFlurstueckRetriever.isDone()) {
                        currentFlurstueckRetriever.cancel(false);
                        currentFlurstueckRetriever = null;
                    }
                    if ((currentWFSRetriever != null) && !currentWFSRetriever.isDone()) {
                        currentWFSRetriever.cancel(false);
                        currentWFSRetriever = null;
                    }
                    cboFlurstueck.setSelectedItem(key);
                    cboGemarkung.setSelectedItem(key.getGemarkung());

                    if (AutomaticFlurstueckRetriever.FLURSTUECK_REQUEST_MODE == mode) {
                        currentFlurstueckRetriever = new FlurstueckRetriever(
                                FlurstueckRetriever.RETRIEVE_AUTOMATIC_MODE,
                                null,
                                key);
                        setPropertyChangeListener(currentFlurstueckRetriever);
                        LagisBroker.getInstance().execute(currentFlurstueckRetriever);
                    } else {
                        if (mode == COPY_CONTENT_MODE) {
                            cboFlurstueck.requestFocus();
                        }
                        if (initialColor != null) {
                            setHighlightColor(initialColor);
                        } else {
                            LOG.warn("Kann die Farbe nicht mehr darstellen == null");
                        }
                        this.isFinished = true;
                    }
                } else {
                    LagisBroker.getInstance().fireFlurstueckChanged(null);
                    this.isFinished = true;
                }
            } else if ((evt.getSource() instanceof FlurstueckRetriever)
                        && evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                this.isFinished = true;
            } else {
                LOG.warn("Kein Propertychange auf das gehört wird");
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public int getMode() {
            return mode;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class FocusListenerImpl implements FocusListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void focusLost(final FocusEvent e) {
//            final Component eventComponent = e.getComponent();
//                 if((currentAutomaticRetriever == null || currentAutomaticRetriever.isDone() ||
//                 currentAutomaticRetriever.isCancelled()) && (currentFlurRetriever == null ||
//                 currentFlurRetriever.isDone() || currentFlurRetriever.isCancelled()) && (currentFlurstueckRetriever
//                 == null || currentFlurstueckRetriever.isDone() || currentFlurstueckRetriever.isCancelled()) &&
//                 (currentGemarkungsRetriever == null || currentGemarkungsRetriever.isDone() ||
//                 currentGemarkungsRetriever.isCancelled()) && (currentWFSRetriever == null ||
//                 currentWFSRetriever.isDone() || currentWFSRetriever.isCancelled()) ){ log.debug("Kein Thread am
//                 laufen --> keine Aktion"); if(eventComponent.equals(cboGemarkung.getEditor().getEditorComponent())){
//                 log.debug("Gemarkungscomboboxeditor hat den Fokus verloren"); } else
//                 if(eventComponent.equals(cboFlur.getEditor().getEditorComponent())){ log.debug("Flurcomboboxeditor
//                 hat den Fokus verloren"); } else
//                 if(eventComponent.equals(cboFlurstueck.getEditor().getEditorComponent())){
//                 log.debug("Flurstueckcomboboxeditor hat den Fokus verloren"); } } else { log.debug("focus lost aber
//                 Aktion im gange"); }
        }

        @Override
        public void focusGained(final FocusEvent e) {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class DocumentListenerImpl implements DocumentListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void removeUpdate(final DocumentEvent e) {
            currentlyCreatedFlurstueckSchluesselBean = FlurstueckSchluesselCustomBean.createNew();
            final Object currentFlur = cboFlur.getSelectedItem();
            if ((currentFlur != null) && (currentFlur instanceof FlurKey)) {
                currentlyCreatedFlurstueckSchluesselBean.setGemarkung(((FlurKey)currentFlur).getGemarkung());
                currentlyCreatedFlurstueckSchluesselBean.setFlur(((FlurKey)currentFlur).getFlurId());
                final String text = txtFlurstueck.getText();
                if (text != null) {
                    if (text.length() != 0) {
                        final String[] tokens = text.split("/");
                        // TODO the user input is not validated
                        try {
                            switch (tokens.length) {
                                case 1: {
                                    currentlyCreatedFlurstueckSchluesselBean.setFlurstueckZaehler(Integer.parseInt(
                                            tokens[0]));
                                    currentlyCreatedFlurstueckSchluesselBean.setFlurstueckNenner(0);
                                    setHighlightColor(LagisBroker.ACCEPTED_COLOR);
                                    txtFlurstueck.setToolTipText("Flurstück");
                                    creationValidationMessage = "Flurstück ist valide";
                                    isFlurstueckCandidateValide = true;
                                    checkIfFlurstueckIsAlreadyInDatabase(
                                        currentlyCreatedFlurstueckSchluesselBean,
                                        true);
                                    break;
                                }
                                case 2: {
                                    currentlyCreatedFlurstueckSchluesselBean.setFlurstueckZaehler(Integer.parseInt(
                                            tokens[0]));
                                    currentlyCreatedFlurstueckSchluesselBean.setFlurstueckNenner(Integer.parseInt(
                                            tokens[1]));
                                    setHighlightColor(LagisBroker.ACCEPTED_COLOR);
                                    txtFlurstueck.setToolTipText("Flurstück");
                                    creationValidationMessage = "Flurstück ist valide";
                                    isFlurstueckCandidateValide = true;
                                    checkIfFlurstueckIsAlreadyInDatabase(
                                        currentlyCreatedFlurstueckSchluesselBean,
                                        true);
                                    break;
                                }
                                default: {
                                    LOG.warn(
                                        "Falsche Eingabe erwarted wird ein Flurstueck ohne oder mit Nenner z.B. 10\n");
                                    setHighlightColor(LagisBroker.ERROR_COLOR);
                                    txtFlurstueck.setToolTipText("Es ist nur ein Teiler / erlaubt");
                                    creationValidationMessage = txtFlurstueck.getToolTipText();
                                    isFlurstueckCandidateValide = false;
                                    fireValidationStateChanged(this);
                                }
                            }
                        } catch (Exception ex) {
                            LOG.error("Fehler beim parsen des Flurstück Zähler/Nenner", ex);
                            txtFlurstueck.setToolTipText(
                                "Kein gültiger Flurstücksname. Gültige Namen sind z.B. 3, 3/0 , 3/15");
                            creationValidationMessage = txtFlurstueck.getToolTipText();
                            setHighlightColor(LagisBroker.ERROR_COLOR);
                            isFlurstueckCandidateValide = false;
                            fireValidationStateChanged(this);
                        }
                    } else {
                        setHighlightColor(LagisBroker.ERROR_COLOR);
                        txtFlurstueck.setToolTipText("Bitte geben Sie ein Flurstück ein.");
                        creationValidationMessage = txtFlurstueck.getToolTipText();
                        isFlurstueckCreateable = false;
                        fireValidationStateChanged(this);
                    }
                } else {
                }
            } else {
                isFlurstueckCandidateValide = false;
            }
        }

        @Override
        public void insertUpdate(final DocumentEvent e) {
            removeUpdate(e);
        }

        @Override
        public void changedUpdate(final DocumentEvent e) {
        }
    }
}
