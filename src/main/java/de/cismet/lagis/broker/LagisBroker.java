/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * LagisBroker.java
 *
 * Created on 20. April 2007, 13:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.broker;

import Sirius.navigator.connection.ConnectionSession;

import com.vividsolutions.jts.geom.Geometry;

import net.infonode.docking.RootWindow;
import net.infonode.gui.componentpainter.GradientComponentPainter;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import org.jdom.Element;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.image.RenderedImage;

import java.net.URL;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.beans.lagis.*;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.statusbar.StatusBar;

import de.cismet.lagis.Exception.ActionNotSuccessfulException;

import de.cismet.lagis.gui.main.LagisApp;

import de.cismet.lagis.interfaces.*;

import de.cismet.lagis.utillity.EmailConfig;
import de.cismet.lagis.utillity.GeometrySlotInformation;
import de.cismet.lagis.utillity.Message;

import de.cismet.lagis.validation.Validatable;

import de.cismet.lagisEE.interfaces.GeometrySlot;

import de.cismet.tools.CurrentStackTrace;

import de.cismet.tools.configuration.Configurable;

import de.cismet.tools.gui.StaticSwingTools;

import static de.cismet.lagis.gui.panels.VerdisCrossoverPanel.createQuery;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class LagisBroker implements FlurstueckChangeObserver, Configurable {

    //~ Static fields/initializers ---------------------------------------------

    private static LagisBroker broker = null;
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LagisBroker.class);
    private static Vector<Resettable> clearAndDisableListeners = new Vector<Resettable>();
    private static DecimalFormat currencyFormatter = new DecimalFormat(",##0.00 \u00A4");
    // private static DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
    private static DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private static Vector<Widget> widgets = new Vector<Widget>();
    private static FlurstueckCustomBean currentFlurstueck = null;
    private static SperreCustomBean currentSperre = null;
    // COLORS
    private static final Color yellow = new Color(231, 223, 84);
    public static final Color red = new Color(219, 96, 96);
    private static final Color blue = new Color(124, 160, 221);
    // public  static final Color grey = new Color(225,226,225);
    public static final Color grey = Color.LIGHT_GRAY;
    // JXTable
    public static final int alphaValue = 255;
    // TODO Perhaps a bit (blasser) brighter public static Color ODD_ROW_DEFAULT_COLOR = new
    // Color(blue.getRed()+119,blue.getGreen()+88,blue.getBlue()+33,alphaValue);
    public static Color ODD_ROW_DEFAULT_COLOR = new Color(blue.getRed() + 113,
            blue.getGreen()
                    + 79,
            blue.getBlue()
                    + 14,
            alphaValue);
    // public static final Color ODD_ROW_DEFAULT_COLOR = new Color(,,,alphaValue); public static final Color
    // ODD_ROW_DEFAULT_COLOR = new Color(blue.getRed()+119,blue.getGreen()+82,blue.getBlue()+34,alphaValue); public
    // static Color ODD_ROW_EDIT_COLOR = new Color(red.getRed()+36,red.getGreen()+146,red.getBlue()+152,alphaValue);
    public static Color ODD_ROW_EDIT_COLOR = new Color(red.getRed() + 25,
            red.getGreen()
                    + 143,
            red.getBlue()
                    + 143,
            alphaValue);
    public static Color ODD_ROW_LOCK_COLOR = new Color(yellow.getRed() + 23,
            yellow.getGreen()
                    + 31,
            yellow.getBlue()
                    + 134,
            alphaValue);
    // FlurstueckSearch
    public static final Color ERROR_COLOR = red;
    public static final Color ACCEPTED_COLOR = Color.WHITE;
    public static final Color UNKOWN_COLOR = ODD_ROW_LOCK_COLOR;
    public static final Color SUCCESSFUL_COLOR = new Color(113, 220, 109);
    // public static final Color SUCCESSFUL_COLOR = new Color(89,184,73);
    public static final Color INITIAL_COLOR = Color.WHITE;
    // WFS Geometry Color
    public static final Color STADT_FILLING_COLOR = new Color(43, 106, 21, 150);
    public static final Color ABTEILUNG_IX_FILLING_COLOR = new Color(100, 40, 106, 150);
    public static final Color UNKNOWN_FILLING_COLOR = UNKOWN_COLOR;
    public static final Color HISTORIC_FLURSTUECK_COLOR = Color.DARK_GRAY;
    // public static final Color ODD_ROW_COLOR = new Color(252,84,114,120);
    public static final Color EVEN_ROW_COLOR = Color.WHITE;
    public static final Color FOREGROUND_ROW_COLOR = Color.BLACK;
    // public static final AlternateRowHighlighter ALTERNATE_ROW_HIGHLIGHTER = new
    // AlternateRowHighlighter(ODD_ROW_DEFAULT_COLOR, EVEN_ROW_COLOR, LagisBroker.FOREGROUND_ROW_COLOR);
    public static Highlighter ALTERNATE_ROW_HIGHLIGHTER = HighlighterFactory.createAlternateStriping(
            ODD_ROW_DEFAULT_COLOR,
            EVEN_ROW_COLOR);
    // public static final AlternateRowHighlighter ALTERNATE_ROW_HIGHLIGHTER_EDIT = new
    // AlternateRowHighlighter(ODD_ROW_EDIT_COLOR,EVEN_ROW_COLOR, LagisBroker.FOREGROUND_ROW_COLOR); TitleColors
    public static final Color EDIT_MODE_COLOR = red;
    public static final Color LOCK_MODE_COLOR = yellow;
    public static final Color HISTORY_MODE_COLOR = grey;
    public static final Color DEFAULT_MODE_COLOR = blue;
    // resolving Gemarkungen
    private static HashMap<Integer, GemarkungCustomBean> gemarkungsHashMap;
    private static GregorianCalendar calender = new GregorianCalendar();

    //~ Instance fields --------------------------------------------------------

    HighlighterFactory highlighterFac = new HighlighterFactory();
    Vector<FlurstueckChangeListener> observedFlurstueckChangedListeners = new Vector<FlurstueckChangeListener>();

    private String title;
    private String totd;

    private boolean loggedIn = false;
    private MappingComponent mappingComponent;
    private RootWindow rootWindow;
    private FlurstueckSchluesselCustomBean currentFlurstueckSchluessel = null;
    // private static String accountName = "sebastian.puhl@cismet.de";
    // private String username;
    // private String group;
    // private String domain;
    private String callserverUrl;
    private String domain;
    private String connectionClass;
    private String account;
    private FlurstueckRequester requester;
    private JFrame parentComponent;
    private boolean isInWfsMode = false;
    // Permissions
    private boolean isFullReadOnlyMode = true;
    private boolean isCoreReadOnlyMode = true;
    private HashMap<String, Boolean> permissions = new HashMap<String, Boolean>();
    /** Creates a new instance of LagisBroker. */
    private StatusBar statusBar;
    private ExecutorService execService = null;
    private int verdisCrossoverPort = -1;
    // TODO Jean
    // private KassenzeichenFacadeRemote verdisServer;
    private Geometry currentWFSGeometry;
    private double kassenzeichenBuffer = -0.2;
    private double kassenzeichenBuffer100 = -0.5;
    private boolean skipSecurityCheckFlurstueckAssistent = false;

    private boolean nkfAdminPermission = false;

    private transient ConnectionSession session;
    private String currentValidationErrorMessage = null;
    // TODO optimize ugly code in my opinion old/new terror
    private Vector<Message> messages = new Vector<Message>();
    private boolean flustueckChangeInProgress = false;
    private boolean isUnkown = false;
    private EmailConfig emailConfig;
    private Vector<String> developerMailaddresses;
    private Vector<String> nkfMailaddresses;
    private Vector<String> maintenanceMailAddresses;
    // if you don't use the vectors delete them
    private StringBuffer nkfRecipients;
    private StringBuffer developerRecipients;
    private StringBuffer maintenanceRecipients;

    private RenderedImage historyImage;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LagisBroker object.
     */
    private LagisBroker() {
        execService = Executors.newCachedThreadPool();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ConnectionSession getSession() {
        return session;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  session  DOCUMENT ME!
     */
    public void setSession(final ConnectionSession session) {
        this.session = session;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static LagisBroker getInstance() {
        if (broker == null) {
            broker = new LagisBroker();
        }
        return broker;
    }

    /**
     * ToDo place query generation in VerdisCrossover. Give key get Query.
     *
     * @param  bean  e bean DOCUMENT ME!
     */
    public void openKassenzeichenInVerdis(final CidsBean bean) {
        if (bean != null) {
            if ((verdisCrossoverPort < 0) || (verdisCrossoverPort > 65535)) {
                log.warn("Crossover: verdisCrossoverPort ist ungültig: " + verdisCrossoverPort);
            } else {
                // ToDo Thread
                final URL verdisQuery = createQuery(verdisCrossoverPort, bean);
                if (verdisQuery != null) {
                    final SwingWorker<Void, Void> openKassenzeichen = new SwingWorker<Void, Void>() {

                            @Override
                            protected Void doInBackground() throws Exception {
                                verdisQuery.openStream();
                                return null;
                            }

                            @Override
                            protected void done() {
                                try {
                                    get();
                                } catch (Exception ex) {
                                    log.error("Fehler beim öffnen des Kassenzeichens", ex);
                                    // ToDo message to user;
                                }
                            }
                        };
                    LagisBroker.getInstance().execute(openKassenzeichen);
                } else {
                    log.warn("Crossover: konnte keine Query anlegen. Kein Abruf der Kassenzeichen möglich.");
                }
            }
        } else {
            log.warn("Crossover: Kann angebenes Flurstück nicht öffnwen");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public RenderedImage getHistoryImage() {
        return historyImage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  historyImage  DOCUMENT ME!
     */
    public void setHistoryImage(final RenderedImage historyImage) {
        this.historyImage = historyImage;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isNkfAdminPermission() {
        return nkfAdminPermission;
    }

    /**
     * DOCUMENT ME!
     */
    public void checkNKFAdminPermissionsOnServer() {
        try {
            nkfAdminPermission = getSession().getConnection()
                        .hasConfigAttr(getSession().getUser(), "lagis.perm.nkf.admin");
            log.info("NKF Admin Recht wurde gesetzt: " + nkfAdminPermission);
        } catch (Exception ex) {
            log.error(
                "Fehler beim setzen der NKF Admin Rechte. Rechte wurden nicht richtig gesetzt und deshalb deaktiviert.",
                ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  widget  DOCUMENT ME!
     */
    public void addWidget(final Widget widget) {
        widgets.add(widget);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  widgets  DOCUMENT ME!
     */
    public void addWidgets(final Vector widgets) {
        final Iterator<Widget> it = widgets.iterator();
        while (it.hasNext()) {
            this.widgets.add(it.next());
        }
        // widgets.;
    }
    /**
     * //TODO perhaps a better way than hardwired connection to parenframe public void setEditModeActivatable(boolean
     * isActivatable){ ((LagisApp)getParentFrame()).setEditModeButtonEnabled(isActivatable); } public void
     * addResettable(Resettable resettable) { widgets.add(resettable); } public void addResettables(Vector components) {
     * Iterator it = components.iterator(); while(it.hasNext()){ Object tmp = it.next(); if(tmp instanceof Resettable){
     * clearAndDisableListeners.add((Resettable) tmp); } } } public void removeResettable(Resettable component) {
     * clearAndDisableListeners.remove(component); }.
     */
    public void resetWidgets() {
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        if (log.isDebugEnabled()) {
                            log.debug("Lagis Broker : Reset widgets");
                        }
//         Iterator<Resettable> it = clearAndDisableListeners.iterator();
//         while(it.hasNext()){
//             Resettable tmp = it.next();
//             tmp.clearComponent();
//             tmp.setComponentEditable(false);
//         }
                        final Iterator<Widget> it = widgets.iterator();
                        while (it.hasNext()) {
                            final Widget tmp = it.next();
                            tmp.clearComponent();
                            tmp.setComponentEditable(false);
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("Lagis Broker : Reset widgets durch");
                        }
                    }
                });
        }
        if (log.isDebugEnabled()) {
            log.debug("Lagis Broker : Reset widgets");
        }
//         Iterator<Resettable> it = clearAndDisableListeners.iterator();
//         while(it.hasNext()){
//             Resettable tmp = it.next();
//             tmp.clearComponent();
//             tmp.setComponentEditable(false);
//         }
        final Iterator<Widget> it = widgets.iterator();
        while (it.hasNext()) {
            final Widget tmp = it.next();
            tmp.clearComponent();
            tmp.setComponentEditable(false);
        }
        if (log.isDebugEnabled()) {
            log.debug("Lagis Broker : Reset widgets durch");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isEditable  DOCUMENT ME!
     */
    public synchronized void setWidgetsEditable(final boolean isEditable) {
        if (log.isDebugEnabled()) {
            log.debug("Setze Widgets editable: " + isEditable);
        }
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        final Iterator<Widget> it = widgets.iterator();
                        while (it.hasNext()) {
                            if (isCurrentFlurstueckLockedByUser()) {
                                // ALTERNATE_ROW_HIGHLIGHTER =
                                // HighlighterFactory.createAlternateStriping(ODD_ROW_LOCK_COLOR, EVEN_ROW_COLOR);
                                ((ColorHighlighter)
                                    (((CompoundHighlighter)ALTERNATE_ROW_HIGHLIGHTER).getHighlighters()[0]))
                                        .setBackground(ODD_ROW_LOCK_COLOR);
                            } else if (isEditable) {
                                // ALTERNATE_ROW_HIGHLIGHTER =
                                // HighlighterFactory.createAlternateStriping(ODD_ROW_EDIT_COLOR, EVEN_ROW_COLOR);
                                ((ColorHighlighter)
                                    (((CompoundHighlighter)ALTERNATE_ROW_HIGHLIGHTER).getHighlighters()[0]))
                                        .setBackground(ODD_ROW_EDIT_COLOR);
                                // ALTERNATE_ROW_HIGHLIGHTER.setOddRowBackground(ODD_ROW_EDIT_COLOR);
                            } else {
                                // ALTERNATE_ROW_HIGHLIGHTER =
                                // HighlighterFactory.createAlternateStriping(ODD_ROW_DEFAULT_COLOR, EVEN_ROW_COLOR);
                                // ALTERNATE_ROW_HIGHLIGHTER.setOddRowBackground(ODD_ROW_DEFAULT_COLOR);
                                ((ColorHighlighter)
                                    (((CompoundHighlighter)ALTERNATE_ROW_HIGHLIGHTER).getHighlighters()[0]))
                                        .setBackground(ODD_ROW_DEFAULT_COLOR);
                            }
                            if (isEditable) {
                                final Widget currentWidget = it.next();
//                            ///overdozed it doesn't change at runtime
//                            HashMap<Widget, Boolean> ressortPermissions = RessortFactory.getInstance().getRessortPermissions();
//                            if (ressortPermissions != null) {
//                                log.debug("Widget Ressortpermissions vorhanden : " + ressortPermissions);
//                                Boolean isReadOnly = ressortPermissions.get(currentWidget);
//                                if (isReadOnly != null) {
//                                    log.debug("Widget Ressortpermissions vorhanden.: " + isReadOnly);
//                                    if (!isReadOnly) {
//                                        currentWidget.setComponentEditable(isEditable);
//                                    } else {
//                                        log.debug("Widget" + currentWidget + " wird kann nicht editiert werden: RessortWidget ist readonly");
//                                    }
//                                } else {
//                                    log.debug("Keine Ressortpermission für Widget vorhanden vorhanden.");
//                                    if (!isCoreReadOnlyMode()) {
//                                        currentWidget.setComponentEditable(isEditable);
//                                    } else {
//                                        log.debug("Widget" + currentWidget + " wird kann nicht editiert werden: BasisWidgets sind nur readonly");
//                                    }
//                                }
//                            } else {
//                                log.debug("Keine Widget Ressortpermissions vorhanden.");
//                                if (!isCoreReadOnlyMode()) {
//                                    currentWidget.setComponentEditable(isEditable);
//                                } else {
//                                    log.debug("Widget" + currentWidget + " wird kann nicht editiert werden: BasisWidgets sind nur readonly");
//                                }
//                            }
                                if (!currentWidget.isWidgetReadOnly()) {
                                    currentWidget.setComponentEditable(isEditable);
                                }
                            } else {
                                it.next().setComponentEditable(isEditable);
                            }
                        }
                    }
                });
        } else {
            final Iterator<Widget> it = widgets.iterator();
            while (it.hasNext()) {
                if (isCurrentFlurstueckLockedByUser()) {
                    // ALTERNATE_ROW_HIGHLIGHTER = HighlighterFactory.createAlternateStriping(ODD_ROW_LOCK_COLOR,
                    // EVEN_ROW_COLOR);
                    ((ColorHighlighter)(((CompoundHighlighter)ALTERNATE_ROW_HIGHLIGHTER).getHighlighters()[0]))
                            .setBackground(ODD_ROW_LOCK_COLOR);
                } else if (isEditable) {
                    // ALTERNATE_ROW_HIGHLIGHTER = HighlighterFactory.createAlternateStriping(ODD_ROW_EDIT_COLOR,
                    // EVEN_ROW_COLOR);
                    ((ColorHighlighter)(((CompoundHighlighter)ALTERNATE_ROW_HIGHLIGHTER).getHighlighters()[0]))
                            .setBackground(ODD_ROW_EDIT_COLOR);
                    // ALTERNATE_ROW_HIGHLIGHTER.setOddRowBackground(ODD_ROW_EDIT_COLOR);
                } else {
                    // ALTERNATE_ROW_HIGHLIGHTER = HighlighterFactory.createAlternateStriping(ODD_ROW_DEFAULT_COLOR,
                    // EVEN_ROW_COLOR); ALTERNATE_ROW_HIGHLIGHTER.setOddRowBackground(ODD_ROW_DEFAULT_COLOR);
                    ((ColorHighlighter)(((CompoundHighlighter)ALTERNATE_ROW_HIGHLIGHTER).getHighlighters()[0]))
                            .setBackground(ODD_ROW_DEFAULT_COLOR);
                }
                if (isEditable) {
                    final Widget currentWidget = it.next();
//                    HashMap<Widget, Boolean> ressortPermissions = RessortFactory.getInstance().getRessortPermissions();
//                    if (ressortPermissions != null) {
//                        log.debug("Widget permissions vorhanden : " + ressortPermissions);
//                        Boolean isReadOnly = ressortPermissions.get(currentWidget);
//                        if (isReadOnly != null) {
//                            log.debug("Widget permissions vorhanden.: " + isReadOnly);
//                            if (!isReadOnly) {
//                                currentWidget.setComponentEditable(isEditable);
//                            } else {
//                                log.debug("Widget" + currentWidget + " wird kann nicht editiert werden: RessortWidget ist readonly");
//                            }
//
//                        } else {
//                            log.debug("Keine Ressortpermission für Widget vorhanden vorhanden.");
//                            if (!isCoreReadOnlyMode()) {
//                                currentWidget.setComponentEditable(isEditable);
//                            } else {
//                                log.debug("Widget" + currentWidget + " wird kann nicht editiert werden: BasisWidgets sind nur readonly");
//                            }
//                        }
//                    } else {
//                        log.debug("Keine Widget Ressortpermissions vorhanden.");
//                        if (!isCoreReadOnlyMode()) {
//                            currentWidget.setComponentEditable(isEditable);
//                        } else {
//                            log.debug("Widget" + currentWidget + " wird kann nicht editiert werden: BasisWidgets sind nur readonly");
//                        }
//                    }
                    if (!currentWidget.isWidgetReadOnly()) {
                        currentWidget.setComponentEditable(isEditable);
                    }
                } else {
                    it.next().setComponentEditable(isEditable);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geom  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public GeometrySlotInformation assignGeometry(final Geometry geom) {
        final GeometrySlotInformation[] openSlots = collectGeometrySlots();
        switch (openSlots.length) {
            case 0: {
                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(mappingComponent),
                    "Es ist kein Element vorhanden dem eine Fläche zugeordnet werden kann\noder die entsprechenden Rechte sind nicht ausreichend",
                    "Geometrie zuordnen",
                    JOptionPane.INFORMATION_MESSAGE);
                return null;
            }
            case 1: {
                final int anwser = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(mappingComponent),
                        "Es ist genau ein Element vorhanden, dem eine Fläche zugeordnet werden kann:\n\n"
                                + "    "
                                + openSlots[0]
                                + "\n\nSoll die Geometrie diesem dem Element hinzugefügt werden ?",
                        "Geometrie zuordnen",
                        JOptionPane.YES_NO_OPTION);
                if (anwser == JOptionPane.YES_OPTION) {
                    final GeometrySlot slotGeom = openSlots[0].getOpenSlot();
                    if (slotGeom != null) {
                        slotGeom.setGeometry(geom);
                    } else {
                        // TODO create concept how to determine the color of geomentities
                        // slotGeom = new Geom();
                        slotGeom.setGeometry(geom);
                        // openSlots[0].getOpenSlot().setGeometrie(slotGeom);
                    }
                    return openSlots[0];
                } else {
                    return null;
                }
            }
            default: {
                final GeometrySlotInformation selectedSlot = (GeometrySlotInformation)JOptionPane.showInputDialog(
                        StaticSwingTools.getParentFrame(mappingComponent),
                        "Bitte wählen Sie das Element, dem Sie die Geometrie zuordnen möchten:\n",
                        "Geometrie zuordnen",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        openSlots,
                        openSlots[0]);
                if (selectedSlot != null) {
                    final GeometrySlot slotGeom = selectedSlot.getOpenSlot();
                    if (slotGeom != null) {
                        slotGeom.setGeometry(geom);
                    } else {
                        // TODO create concept how to determine the color of geomentities
                        // slotGeom = new Geom();
                        slotGeom.setGeometry(geom);
                        // selectedSlot.getOpenSlot().setGeometrie(slotGeom);
                    }
                    return selectedSlot;
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private GeometrySlotInformation[] collectGeometrySlots() {
        final Vector<GeometrySlotInformation> openSlots = new Vector<GeometrySlotInformation>();
        final Iterator<Widget> it = widgets.iterator();
        while (it.hasNext()) {
            final Widget curWidget = it.next();
            if (curWidget instanceof GeometrySlotProvider) {
                openSlots.addAll(((GeometrySlotProvider)curWidget).getSlotInformation());
            }
        }
        return openSlots.toArray(new GeometrySlotInformation[openSlots.size()]);
    }
    /**
     * public void addFlurstueckChangedListener(ChangeListener listener){ flurstueckChangedListeners.add(listener); }
     * public void addFlurstueckChangedListener(Vector listeners){ Iterator it = listeners.iterator();
     * while(it.hasNext()){ Object tmp = it.next(); if(tmp instanceof ChangeListener){
     * flurstueckChangedListeners.add((ChangeListener)tmp); } } } public void
     * removeFlurstueckChangedListener(ChangeListener listener){ flurstueckChangedListeners.remove(listener); } TODO
     * REFACTOR real event Implement proper events not direkt Collection.
     *
     * @param  event  DOCUMENT ME!
     */
    public void fireChangeEvent(final Object event) {
        final Iterator<Widget> it = widgets.iterator();
        while (it.hasNext()) {
            final Widget curWidget = it.next();
            // TODO HARDCORE UGLY
            if ((curWidget instanceof FeatureSelectionChangedListener)
                        && ((FeatureSelectionChangedListener)curWidget).isFeatureSelectionChangedEnabled()
                        && (event instanceof Collection)) {
                ((FeatureSelectionChangedListener)curWidget).featureSelectionChanged((Collection<Feature>)event);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static DecimalFormat getCurrencyFormatter() {
        return currencyFormatter;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static DateFormat getDateFormatter() {
        return dateFormatter;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MappingComponent getMappingComponent() {
        return mappingComponent;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aMappingComponent  DOCUMENT ME!
     */
    public void setMappingComponent(final MappingComponent aMappingComponent) {
        mappingComponent = aMappingComponent;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean acquireLock() {
        try {
            if ((currentFlurstueck != null) && (currentSperre == null)) {
                final SperreCustomBean newSperre = SperreCustomBean.createNew();
                // datamodell refactoring 22.10.07
                newSperre.setFlurstueckSchluessel(currentFlurstueck.getFlurstueckSchluessel().getId());
                newSperre.setBenutzerkonto(getAccountName());
                newSperre.setZeitstempel(new Date());
                final SperreCustomBean result = CidsBroker.getInstance().createLock(newSperre);
                if (result != null) {
                    if (result.getBenutzerkonto().equals(getAccountName())
                                && result.getZeitstempel().equals(newSperre.getZeitstempel())) {
                        currentSperre = result;
                        if (log.isDebugEnabled()) {
                            log.debug("Sperre konnte erfolgreich angelegt werden");
                        }
                        setWidgetsEditable(true);
                        for (final Feature feature
                                    : (Collection<Feature>)getMappingComponent().getFeatureCollection()
                                    .getSelectedFeatures()) {
                            getMappingComponent().getFeatureCollection().select(feature);
                        }
                        return true;
                    } else {
                        log.info("Sperre für flurstueck " + currentFlurstueck.getId()
                                    + " bereitsvorhanden von Benutzer " + result.getBenutzerkonto());
                        JOptionPane.showMessageDialog(
                            parentComponent,
                            "Der Datensatz ist schon vom Benutzer "
                                    + result.getBenutzerkonto()
                                    + " zum Verändern gesperrt",
                            "Kein Editieren möglich",
                            JOptionPane.INFORMATION_MESSAGE);
                        return false;
                    }
                } else {
                    log.info("Es konnte keine Sperre angelegt werden ?? warum");
                    return false;
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Sperre Flurstueck ist null oder eine Sperre ist bereits vorhanden: \nSperre: "
                                + currentSperre + "\nFlursuteck: " + currentFlurstueck);
                }
                return false;
            }
        } catch (Exception ex) {
            log.error("Fehler beim anlegen der Sperre", ex);
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean releaseLock() {
        try {
            if ((currentFlurstueck != null) && (currentSperre != null)) {
                final boolean result = CidsBroker.getInstance().releaseLock(currentSperre);
                if (result) {
                    if (log.isDebugEnabled()) {
                        log.debug("Sperre erfolgreich gelöst");
                    }
                    currentSperre = null;
                    setWidgetsEditable(false);
                    return true;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Sperre konnte nicht entfernt werden ?? warum todo");
                    }
                    return false;
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Sperre Flurstueck ist null oder eine Sperre ist bereits vorhanden: \nSperre: "
                                + currentSperre + "\nFlursuteck: " + currentFlurstueck);
                }
                return false;
            }
        } catch (Exception ex) {
            log.error("Fehler beim lösen der Sperre", ex);
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isInEditMode() {
        return currentSperre != null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean validateWidgets() {
        final Iterator<Widget> it = widgets.iterator();
        while (it.hasNext()) {
            final Widget currentWidget = it.next();
            if (currentWidget.getStatus() == Validatable.ERROR) {
                currentValidationErrorMessage = currentWidget.getValidationMessage();
                if (currentValidationErrorMessage == null) {
                    currentValidationErrorMessage = "Kein Fehlertext vorhanden";
                }
                return false;
            }
        }
        return true;
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void reloadFlurstueck() {
        if (currentFlurstueck != null) {
            log.info("reloadFlurstueck");
            resetWidgets();
            loadFlurstueck(currentFlurstueck.getFlurstueckSchluessel());
        } else {
            log.info("can't reload flurstueck == null");
        }
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void reloadFlurstueckKeys() {
        log.info("updateFlurstueckKeys");
        requester.updateFlurstueckKeys();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key  DOCUMENT ME!
     */
    public synchronized void loadFlurstueck(final FlurstueckSchluesselCustomBean key) {
        // requester.requestFlurstueck(key);
        // requester.requestNewFlurstueck(key);
        if (isInEditMode()) {
            JOptionPane.showMessageDialog(LagisBroker.getInstance().getParentComponent(),
                "Das Flurstück kann nur gewechselt werden wenn alle Änderungen gespeichert oder verworfen worden sind.",
                "Wechseln nicht möglich",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        log.info("loadFlurstueck");
        resetWidgets();
        requester.requestFlurstueck(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckRequester getRequester() {
        return requester;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  requester  DOCUMENT ME!
     */
    public void setRequester(final FlurstueckRequester requester) {
        this.requester = requester;
    }
    /**
     * Emails in Server auslagern.
     */
    public void saveCurrentFlurstueck() {
        try {
            messages = new Vector<Message>();
            if (currentFlurstueck != null) {
                final Iterator<Widget> it = widgets.iterator();
                while (it.hasNext()) {
                    final Widget curWidget = it.next();
                    if (curWidget instanceof FlurstueckSaver) {
                        if (log.isDebugEnabled()) {
                            log.debug("Daten von: " + curWidget.getWidgetName() + " werden gespeichert");
                        }
                        ((FlurstueckSaver)curWidget).updateFlurstueckForSaving(currentFlurstueck);
                    }
                }
                // TODO check if flurstück is changed at all
                try {
                    final FlurstueckCustomBean origFlurstueck = CidsBroker.getInstance()
                                .retrieveFlurstueck(currentFlurstueck.getFlurstueckSchluessel());

                    // Checks the Dienstellen for changes
                    final Collection<VerwaltungsbereichCustomBean> oldBereiche =
                        origFlurstueck.getVerwaltungsbereiche();
                    final Collection<VerwaltungsbereichCustomBean> newBereiche =
                        currentFlurstueck.getVerwaltungsbereiche();
                    if (((oldBereiche == null) || (oldBereiche.size() == 0))
                                && ((newBereiche == null) || (newBereiche.size() == 0))) {
                        log.info("Es existieren keine Verwaltungsbereiche --> keine Veränderung");
                    } else if (((oldBereiche == null) || (oldBereiche.size() == 0))) {
                        log.info("Es wurden nur neue Verwaltungsbereiche angelegt: " + newBereiche.size());
                        for (final VerwaltungsbereichCustomBean currentBereich : newBereiche) {
                            try {
//                                Message newMessage = new Message();
//                                newMessage.setMessageReceiver(Message.RECEIVER_VERWALTUNGSSTELLE);
//                                newMessage.setMessageType(Message.VERWALTUNGSBEREICH_NEW);
//                                Vector messageObjects = new Vector();
//                                messageObjects.add(currentBereich);
//                                newMessage.setMessageObjects(messageObjects);
                                // TODO duplicated code see checkofdifferences
                                final VerwaltendeDienststelleCustomBean currentDienstelle =
                                    currentBereich.getDienststelle();
                                if (currentDienstelle != null) {
                                    messages.add(Message.createNewMessage(
                                            Message.RECEIVER_VERWALTUNGSSTELLE,
                                            Message.VERWALTUNGSBEREICH_NEW,
                                            currentDienstelle));
                                } else {
                                    if (log.isDebugEnabled()) {
                                        log.debug("neuer Verwaltungsbereich angelegt ohne Dienstellenzuordnung");
                                    }
                                }
                            } catch (Exception ex) {
                                log.error("Fehler beim prüfen eines neuen Verwaltungsbereichs", ex);
                                messages.add(Message.createNewMessage(
                                        Message.RECEIVER_ADMIN,
                                        Message.VERWALTUNGSBEREICH_ERROR,
                                        "Es wurden nur neue Flurstücke angelegt. Fehler beim Prüfen eines Verwaltungsgebrauchs",
                                        ex,
                                        currentBereich));
                                // TODO Nachricht an Benutzer
                            }
                        }
                    } else if (((newBereiche == null) || (newBereiche.size() == 0))) {
                        log.info("Es wurden alle alten Verwaltungsbereiche gelöscht: " + oldBereiche.size());
                        for (final VerwaltungsbereichCustomBean currentBereich : oldBereiche) {
                            try {
//                                Message newMessage = new Message();
//                                newMessage.setMessageReceiver(Message.RECEIVER_VERWALTUNGSSTELLE);
//                                newMessage.setMessageType(Message.VERWALTUNGSBEREICH_DELETED);
//                                Vector messageObjects = new Vector();
//                                messageObjects.add(currentBereich);
//                                newMessage.setMessageObjects(messageObjects);
                                messages.add(Message.createNewMessage(
                                        Message.RECEIVER_VERWALTUNGSSTELLE,
                                        Message.VERWALTUNGSBEREICH_DELETED,
                                        currentBereich.getDienststelle()));
                            } catch (Exception ex) {
                                log.error("Fehler beim prüfen eines alten Verwaltungsbereichs", ex);
                                messages.add(Message.createNewMessage(
                                        Message.RECEIVER_ADMIN,
                                        Message.VERWALTUNGSBEREICH_ERROR,
                                        "Es wurden alle Verwaltungsbereiche gelöscht. Fehler beim erzeugen der Benutzernachrichten",
                                        ex,
                                        currentBereich));
                                // TODO Nachricht an Benutzer
                            }
                        }
                    } else {
                        log.info("Es exitieren sowohl alte wie neue Verwaltungsbereiche -> abgleich");
                        final Vector modDienststellen = new Vector();
                        final Vector addedDienststellen = new Vector();
                        final Vector deletedDienststellen = new Vector();
                        final Vector<VerwaltungsbereichCustomBean> oldBereicheVector = new Vector(oldBereiche);
                        final Vector<VerwaltungsbereichCustomBean> newBereicheVector = new Vector(newBereiche);
                        for (final VerwaltungsbereichCustomBean currentBereich : newBereiche) {
                            try {
                                if (((currentBereich.getId() == null)
                                                || (currentBereich.getId() == -1))
                                            && !oldBereiche.contains(currentBereich)) {
                                    log.info("Es wurden ein neuer Verwaltungsbereich angelegt: " + currentBereich);
                                    // TODO duplicated code see checkofdifferences
                                    final VerwaltendeDienststelleCustomBean currentDienstelle =
                                        currentBereich.getDienststelle();
                                    if (currentDienstelle != null) {
                                        addedDienststellen.add(Message.createNewMessage(
                                                Message.RECEIVER_VERWALTUNGSSTELLE,
                                                Message.VERWALTUNGSBEREICH_NEW,
                                                currentDienstelle));
                                    } else {
                                        if (log.isDebugEnabled()) {
                                            log.debug("neuer Verwaltungsbereich angelegt ohne Dienstellenzuordnung");
                                        }
                                    }
                                } else if ((currentBereich.getId() != null)
                                            && (currentBereich.getId() != -1)
                                            && oldBereiche.contains(currentBereich)) {
                                    final int index = oldBereicheVector.indexOf(currentBereich);
                                    log.info("Verwaltungsbereich war schon in Datenbank: " + currentBereich
                                                + " index in altem Datenbestand=" + index);
                                    final VerwaltungsbereichCustomBean oldBereich = oldBereicheVector.get(index);
                                    final VerwaltendeDienststelleCustomBean oldDienststelle =
                                        oldBereich.getDienststelle();
                                    final VerwaltendeDienststelleCustomBean newDienststelle =
                                        currentBereich.getDienststelle();
                                    if ((oldDienststelle != null) && (newDienststelle != null)) {
                                        if (log.isDebugEnabled()) {
                                            log.debug("AlteDienstelle=" + oldDienststelle + " NeueDienststelle="
                                                        + newDienststelle);
                                        }
                                        if (oldDienststelle.equals(newDienststelle)) {
                                            if (log.isDebugEnabled()) {
                                                log.debug("Dienstelle des Verwaltungsbereich ist gleich geblieben");
                                            }
                                        } else {
                                            if (log.isDebugEnabled()) {
                                                log.debug("Dienstelle des Verwaltungsbereichs hat sich geändert");
                                            }
                                            modDienststellen.add(Message.createNewMessage(
                                                    Message.RECEIVER_VERWALTUNGSSTELLE,
                                                    Message.VERWALTUNGSBEREICH_CHANGED,
                                                    oldDienststelle,
                                                    newDienststelle));
                                        }
                                    } else if (oldDienststelle == null) {
                                        if (log.isDebugEnabled()) {
                                            log.debug(
                                                "Einem vorhandenen Verwaltungsbereich wurde eine Dienstelle zugeordnet");
                                        }
                                        addedDienststellen.add(Message.createNewMessage(
                                                Message.RECEIVER_VERWALTUNGSSTELLE,
                                                Message.VERWALTUNGSBEREICH_NEW,
                                                newDienststelle));
                                    } else {
                                        if (log.isDebugEnabled()) {
                                            log.debug("Eine vorhandene Dienstellenzuordnung wurde entfernt");
                                        }
                                        deletedDienststellen.add(Message.createNewMessage(
                                                Message.RECEIVER_VERWALTUNGSSTELLE,
                                                Message.VERWALTUNGSBEREICH_DELETED,
                                                oldDienststelle));
                                    }
                                    oldBereicheVector.remove(currentBereich);
                                } else if ((currentBereich.getId() != null) && (currentBereich.getId() != -1)) {
                                    log.error(
                                        "Verwaltungsbereich hat eine ID, existiert aber nicht in altem Datenbestand --> equals funktioniert nicht");
                                    messages.add(Message.createNewMessage(
                                            Message.RECEIVER_ADMIN,
                                            Message.VERWALTUNGSBEREICH_ERROR,
                                            "Verwaltungsbereich hat eine ID, existiert aber nicht in altem Datenbestand",
                                            currentBereich));
                                    // TODO Nachricht an Benutzer
                                } else {
                                    log.fatal("nichtbehandelter fall currentBereich: " + currentBereich);
                                    messages.add(Message.createNewMessage(
                                            Message.RECEIVER_ADMIN,
                                            Message.VERWALTUNGSBEREICH_ERROR,
                                            "Ein bei der automatischen Generierung von Emails nicht behandelter Fall ist aufgetreten",
                                            currentBereich));
                                    // TODO Nachricht an Benutzer
                                }
                            } catch (Exception ex) {
                                log.error(
                                    "Fehler beim abgeleich von alten und neuen Verwaltungsbereichen für die emailbenachrichtigung",
                                    ex);
                                messages.add(Message.createNewMessage(
                                        Message.RECEIVER_ADMIN,
                                        Message.VERWALTUNGSBEREICH_ERROR,
                                        "Es gab einen Fehler beim abgleichen alter und neuer Verwaltungsbereiche",
                                        ex,
                                        currentBereich));
                                // TODO Nachricht an Benutzer
                            }
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("gelöschte Verwaltungsbereiche erfassen");
                        }
                        for (final VerwaltungsbereichCustomBean currentBereich : oldBereicheVector) {
                            try {
                                if (!newBereiche.contains(currentBereich)) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Verwaltungsbereich existiert nicht mehr in neuem Datenbestand: "
                                                    + currentBereich);
                                    }
                                    final VerwaltendeDienststelleCustomBean oldDienststelle =
                                        currentBereich.getDienststelle();
                                    if (oldDienststelle == null) {
                                        if (log.isDebugEnabled()) {
                                            log.debug("Für Verwaltungsbereich wurde keine Dienstelle zugeordnet");
                                        }
                                    } else {
                                        if (log.isDebugEnabled()) {
                                            log.debug("Verwaltungsbereich hatte eine Dienstelle");
                                        }
                                        deletedDienststellen.add(Message.createNewMessage(
                                                Message.RECEIVER_VERWALTUNGSSTELLE,
                                                Message.VERWALTUNGSBEREICH_DELETED,
                                                oldDienststelle));
                                    }
                                }
                            } catch (Exception ex) {
                                messages.add(Message.createNewMessage(
                                        Message.RECEIVER_ADMIN,
                                        Message.VERWALTUNGSBEREICH_ERROR,
                                        "Es gab einen Fehler beim ermitteln, welche Verwaltungsbereiche gelöscht wurden",
                                        ex,
                                        currentBereich));
                            }
                        }
                        messages.addAll(addedDienststellen);
                        messages.addAll(modDienststellen);
                        messages.addAll(deletedDienststellen);
                        if (log.isDebugEnabled()) {
                            log.debug("Nachrichten insgesamt: " + messages.size() + "davon sind neue Dienstellen="
                                        + addedDienststellen.size() + " gelöschte=" + deletedDienststellen.size()
                                        + " modifizierte=" + modDienststellen.size());
                        }
                    }
                } catch (Exception ex) {
                    // TODO what doing by generall failure sending the other and the failure ?
                    log.fatal("Fehler bei der email benachrichtigung", ex);
                    messages.add(Message.createNewMessage(
                            Message.RECEIVER_ADMIN,
                            Message.GENERAL_ERROR,
                            "LagIS - Fehler beim erstellen der automatischen Emails",
                            ex));
                    // TODO Nachricht an Benutzer
                }

                // modifyFlurstueck() does also save the current state of the Flurstueck (or throws an exception)
                CidsBroker.getInstance().modifyFlurstueck(currentFlurstueck);
            }
        } catch (Exception ex) {
            final StringBuffer buffer = new StringBuffer("Das Flurstück konnte nicht gespeichert werden.\nFehler: ");
            if (ex instanceof ActionNotSuccessfulException) {
                final ActionNotSuccessfulException reason = (ActionNotSuccessfulException)ex;
                if (reason.hasNestedExceptions()) {
                    log.error("Nested changeKind Exceptions: ", reason.getNestedExceptions());
                }
                buffer.append(reason.getMessage());
            } else {
                log.error("Unbekannter Fehler: ", ex);
                buffer.append("Unbekannter Fehler bitte wenden Sie sich an Ihren Systemadministrator");
            }
            log.error("Fehler beim speichern des aktuellen Flurstücks", ex);
            JOptionPane.showMessageDialog(
                parentComponent,
                buffer.toString(),
                "Fehler beim speichern",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * TODO if you leave this method --> you doesn't need the class EmailConfig, just configure auth etc directly TODO
     * NO HARDCODING !!!!!!!!! / MORE INFORMATIONS WHAT FLURSTÜCK, WHAT VERWALTUNGSGEBRAUCH
     *
     * @return  DOCUMENT ME!
     */
// private void sendMessages() {
// // TODO extra Thread nötig ??
// // HOT FIX Geht wahrscheinlich nicht bekomme mal keine Emails !!!
// final Thread t = new Thread() {
//
// @Override
// public void run() {
// try {
// if (log.isDebugEnabled()) {
// log.debug("send Messages()");
// }
// if (messages != null) {
// if (messages.size() > 0) {
// final MailAuthenticator auth = new MailAuthenticator(emailConfig.getUsername(),
// emailConfig.getPassword());
// final Properties properties = new Properties();
// properties.put("mail.smtp.host", emailConfig.getSmtpServer());
// final Session session = Session.getDefaultInstance(properties, auth);
// for (final Message currentMessage : messages) {
// try {
// final javax.mail.Message msg = new MimeMessage(session);
// msg.setFrom(new InternetAddress(emailConfig.getSenderAddress()));
// // TODO OPTIMIZATION OVERALL CATEGORY
// if (currentMessage.getMessageType() == Message.VERWALTUNGSBEREICH_CHANGED) {
// final Vector messageObjects = currentMessage.getMessageObjects();
// final VerwaltendeDienststelleCustomBean oldDienststelle =
// (VerwaltendeDienststelleCustomBean)messageObjects.get(0);
// final VerwaltendeDienststelleCustomBean newDienststelle =
// (VerwaltendeDienststelleCustomBean)messageObjects.get(1);
// // TODO OPTIMIZE
// if ((oldDienststelle.getEmailAdresse() == null)
// || (newDienststelle.getEmailAdresse() == null)) {
// throw new Exception(
// "Eine Emailaddresse eines Verwaltungsbereichs ist nicht gesetzt: "
// + oldDienststelle.getEmailAdresse()
// + " "
// + newDienststelle.getEmailAdresse());
// }
// msg.setRecipients(
// javax.mail.Message.RecipientType.TO,
// InternetAddress.parse(
// oldDienststelle.getEmailAdresse()
// + ","
// + newDienststelle.getEmailAdresse(),
// false));
// msg.setSubject("Lagis - Änderung Zuständigkeitsbereiche");
// // TODO mit replacements arbeiten config file msg.setText("Bei dieser Mail
// // handelt es sich um eine automatisch von LagIS erstellte
// // Benachrichtigung.\n\n" + "Folgendener Fehler ist zur Laufzeit
// // aufgetreten:\n\n" + messageObjects.get(0)+"n\n" + "Zugehöriger
// // Stacktrace:\n\n" + messageObjects.get(1));
// msg.setText("Bei dem Flurstück:\n"
// + currentFlurstueck + "\n"
// + "wurde die Zuordnung zur unterhaltenden Dienststelle geändert.");
// } else if (currentMessage.getMessageType() == Message.VERWALTUNGSBEREICH_NEW) {
// final Vector messageObjects = currentMessage.getMessageObjects();
// final VerwaltendeDienststelleCustomBean newDienststelle =
// (VerwaltendeDienststelleCustomBean)messageObjects.get(0);
// // TODO OPTIMIZE
// if (newDienststelle.getEmailAdresse() == null) {
// throw new Exception(
// "Eine Emailaddresse eines Verwaltungsbereichs ist nicht gesetzt: "
// + newDienststelle.getEmailAdresse());
// }
// msg.setRecipients(
// javax.mail.Message.RecipientType.TO,
// InternetAddress.parse(newDienststelle.getEmailAdresse(), false));
// msg.setSubject("Lagis - Änderung Zuständigkeitsbereiche");
// msg.setText("Bei dem Flurstück:\n"
// + currentFlurstueck + "\n"
// + "wurde die Zuordnung zur unterhaltenden Dienststelle hinzugefügt.");
// } else if (currentMessage.getMessageType()
// == Message.VERWALTUNGSBEREICH_DELETED) {
// final Vector messageObjects = currentMessage.getMessageObjects();
// final VerwaltendeDienststelleCustomBean oldDienststelle =
// (VerwaltendeDienststelleCustomBean)messageObjects.get(0);
// // TODO OPTIMIZE
// if (oldDienststelle.getEmailAdresse() == null) {
// throw new Exception(
// "Eine Emailaddresse eines Verwaltungsbereichs ist nicht gesetzt: "
// + oldDienststelle.getEmailAdresse());
// }
// msg.setRecipients(
// javax.mail.Message.RecipientType.TO,
// InternetAddress.parse(oldDienststelle.getEmailAdresse(), false));
// msg.setSubject("Lagis - Änderung Zuständigkeitsbereiche");
// msg.setText("Dienststelle deleted");
// msg.setText("Bei dem Flurstück:\n"
// + currentFlurstueck + "\n"
// + "wurde die Zuordnung zur unterhaltenden Dienststelle entfernt.");
// } else if (currentMessage.getMessageType()
// == Message.VERWALTUNGSBEREICH_ERROR) {
// msg.setRecipients(
// javax.mail.Message.RecipientType.TO,
// InternetAddress.parse(
// developerRecipients
// + ","
// + maintenanceMailAddresses,
// false));
// final Vector messageObjects = currentMessage.getMessageObjects();
// msg.setSubject("Lagis - Fehler beim Abgleichen von Verwaltungsbereichen");
// msg.setText(
// "Bei dieser Mail handelt es sich um eine automatisch von LagIS erstellte Fehlermeldung.\n\n"
// + "Folgendener Fehler ist zur Laufzeit aufgetreten:\n\n"
// + messageObjects.get(0)
// + "n\n"
// + "Zugehöriger Stacktrace:\n\n"
// + messageObjects.get(1));
// } else if (currentMessage.getMessageType() == Message.GENERAL_ERROR) {
// msg.setRecipients(
// javax.mail.Message.RecipientType.TO,
// InternetAddress.parse(
// developerRecipients
// + ","
// + maintenanceMailAddresses,
// false));
// final Vector messageObjects = currentMessage.getMessageObjects();
// msg.setSubject((String)messageObjects.get(0));
// msg.setText(
// "Bei dieser Mail handelt es sich um eine automatisch von LagIS erstellte Fehlermeldung.\n\n"
// + "Folgendener Fehler ist zur Laufzeit aufgetreten:\n\n"
// + "Eine oder Mehrere Emails konnten nicht erstellt werden\n\n"
// + "Zugehöriger Stacktrace:\n\n"
// + messageObjects.get(1));
// }
// // Hier lassen sich HEADER-Informationen hinzufügen
// // msg.setHeader("Test", "Test");
// msg.setSentDate(new Date());
// Transport.send(msg);
// } catch (Exception ex) {
// log.fatal("Fehler beim senden einer Emails: ", ex);
// // TODO Benutzer benachrichtigen
// }
// }
// } else {
// log.warn("Keine Meldungen zum versenden vorhanden == 0");
// }
// } else {
// log.warn("Keine Meldungen zum versenden vorhanden == null");
// }
// } catch (Exception ex) {
// log.fatal("Fehler beim senden von Emails: ", ex);
// // TODO Benutzer benachrichtigen
// }
// if (log.isDebugEnabled()) {
// log.debug("sendMessage() end");
// }
// }
// };
// t.setPriority(Thread.NORM_PRIORITY);
// t.start();
// }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckCustomBean getCurrentFlurstueck() {
        return currentFlurstueck;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isCurrentFlurstueckLockedByUser() {
        if (currentFlurstueck != null) {
            // datamodell refactoring 22.10.07
            return currentFlurstueck.getFlurstueckSchluessel().isGesperrt();
        } else {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JFrame getParentComponent() {
        return parentComponent;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  parentComponent  DOCUMENT ME!
     */
    public void setParentComponent(final JFrame parentComponent) {
        this.parentComponent = parentComponent;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isInWfsMode() {
        return isInWfsMode;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isIsInWfsMode() {
        return isInWfsMode;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isInWfsMode  DOCUMENT ME!
     */
    public void setIsInWfsMode(final boolean isInWfsMode) {
        this.isInWfsMode = isInWfsMode;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  color  DOCUMENT ME!
     */
    public void setTitleBarComponentpainter(final Color color) {
        getRootWindow().getRootWindowProperties()
                .getViewProperties()
                .getViewTitleBarProperties()
                .getNormalProperties()
                .getShapedPanelProperties()
                .setComponentPainter(new GradientComponentPainter(
                        color,
                        new Color(236, 233, 216),
                        color,
                        new Color(236, 233, 216)));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  left   DOCUMENT ME!
     * @param  right  DOCUMENT ME!
     */
    public void setTitleBarComponentpainter(final Color left, final Color right) {
        getRootWindow().getRootWindowProperties()
                .getViewProperties()
                .getViewTitleBarProperties()
                .getNormalProperties()
                .getShapedPanelProperties()
                .setComponentPainter(new GradientComponentPainter(left, right, left, right));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   refreshClass  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Refreshable getRefreshableByClass(final Class<?> refreshClass) {
        final Iterator<Widget> it = widgets.iterator();
        while (it.hasNext()) {
            final Refreshable curRefreshable = it.next();
            if (curRefreshable.getClass().equals(refreshClass)) {
                if (log.isDebugEnabled()) {
                    log.debug("ein Refreshable gefunden");
                }
                return curRefreshable;
            }
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     */
    public void refreshWidgets() {
        final Iterator<Widget> it = widgets.iterator();
        while (it.hasNext()) {
            final Refreshable curRefreshable = it.next();
            curRefreshable.refresh(null);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public RootWindow getRootWindow() {
        return rootWindow;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rootWindow  DOCUMENT ME!
     */
    public void setRootWindow(final RootWindow rootWindow) {
        this.rootWindow = rootWindow;
    }

    @Override
    public boolean isFlurstueckChangeInProgress() {
        return flustueckChangeInProgress;
    }

    // TODO REFACTOR --> gerneralize
    @Override
    public synchronized void fireFlurstueckChanged(final FlurstueckCustomBean newFlurstueck) {
        getMappingComponent().getFeatureCollection().unselectAll();
        if (log.isDebugEnabled()) {
            log.debug("FlurstueckChangeEvent");
        }
        warnIfThreadIsNotEDT();
//        Iterator<ChangeListener> it = flurstueckChangedListeners.iterator();
//        while(it.hasNext()){
//            it.next().FlurstueckChanged(newFlurstueck);
//        }
        resetWidgets();
        getMappingComponent().getFeatureCollection().removeAllFeatures();
        if (newFlurstueck != null) {
            if (log.isDebugEnabled()) {
                log.debug("neues Flurstück != null");
            }
            observedFlurstueckChangedListeners.clear();
            for (final Widget widget : widgets) {
                if (widget instanceof FlurstueckChangeListener) {
                    observedFlurstueckChangedListeners.add((FlurstueckChangeListener)widget);
                }
            }
            flustueckChangeInProgress = true;
            currentFlurstueck = newFlurstueck;
            setCurrentFlurstueckSchluessel(newFlurstueck.getFlurstueckSchluessel(), false);
            setWidgetsEditable(false);
            final Iterator<Widget> it = widgets.iterator();
            while (it.hasNext()) {
                final Widget curWidget = it.next();
                if (curWidget instanceof FlurstueckChangeListener) {
                    ((FlurstueckChangeListener)curWidget).flurstueckChanged(newFlurstueck);
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("neues Flurstück == null");
            }
            observedFlurstueckChangedListeners.clear();
            setWidgetsEditable(false);
            currentFlurstueck = newFlurstueck;
            setCurrentFlurstueckSchluessel(null, true);
            flustueckChangeInProgress = true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isUnkown  DOCUMENT ME!
     */
    public void setIsUnkownFlurstueck(final boolean isUnkown) {
        if (isUnkownFlurstueck() == true) {
            if (log.isDebugEnabled()) {
                log.debug("FlurstückSchlüssel ist unbekannt: " + isUnkown);
            }
            log.info("setze currentFlurstück=null");
            if ((getParentComponent() != null) && (getParentComponent() instanceof LagisApp)) {
                ((LagisApp)getParentComponent()).setFlurstueckUnkown();
            }
        }
        this.isUnkown = isUnkown;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueckSchluessel  DOCUMENT ME!
     * @param  isUnkown              DOCUMENT ME!
     */
    public void setCurrentFlurstueckSchluessel(final FlurstueckSchluesselCustomBean flurstueckSchluessel,
            final boolean isUnkown) {
        if (log.isDebugEnabled()) {
            log.debug("setCurrentFlurstueckSchluessel");
        }
        if ((currentFlurstueck != null) && !isUnkown) {
            if (log.isDebugEnabled()) {
                log.debug("CurrentFlurstueckSchluessel ist ein bekanntes Flurstück");
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("CurrentFlurstueckSchluessel ist ein unbekanntes Flurstück");
            }
        }
        setIsUnkownFlurstueck(isUnkown);
        currentFlurstueckSchluessel = flurstueckSchluessel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckSchluesselCustomBean getCurrentFlurstueckSchluessel() {
        return currentFlurstueckSchluessel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isUnkownFlurstueck() {
        return this.isUnkown;
    }

    @Override
    public void flurstueckChangeFinished(final FlurstueckChangeListener fcListener) {
        if (log.isDebugEnabled()) {
            log.debug("FlurstueckChangeListener hat update beendet: " + fcListener);
        }
        observedFlurstueckChangedListeners.remove(fcListener);
        if (observedFlurstueckChangedListeners.isEmpty() && (flustueckChangeInProgress || isUnkown)) {
            if (isUnkown) {
                if (log.isDebugEnabled()) {
                    log.debug("Flurstueck is unkown");
                }
            }
            flustueckChangeInProgress = false;
            if (log.isDebugEnabled()) {
                // log.debug("setting isUnknown = false");
                // isUnkown=false;
                log.debug("Alle FlurstueckChangeListener sind fertig --> zoom");
            }
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        mappingComponent.zoomToFeatureCollection();
                    }
                });
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Anzahl restlicher Listener: " + observedFlurstueckChangedListeners.size());
            }
            if (log.isDebugEnabled()) {
                log.debug("Anzahl restlicher Listener: " + observedFlurstueckChangedListeners);
                log.debug("flurstueckChange in progress: " + flustueckChangeInProgress);
                log.debug("isUnkown " + isUnkown);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   date  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Date getDateWithoutTime(final Date date) {
        calender.setTime(date);
        calender.set(GregorianCalendar.HOUR, 0);
        calender.set(GregorianCalendar.MINUTE, 0);
        calender.set(GregorianCalendar.SECOND, 0);
        calender.set(GregorianCalendar.MILLISECOND, 0);
        calender.set(GregorianCalendar.AM_PM, GregorianCalendar.AM);
        return calender.getTime();
    }
    /**
     * TODO nächsten 6 methoden Sinnvoll ?? public String getUsername() { return username; } public void
     * setUsername(String aUsername) { username = aUsername; } public String getGroup() { return group; } public void
     * setGroup(String aGroup) { group = aGroup; } public String getDomain() { return domain; } public void
     * setDomain(String aDomain) { domain = aDomain; } TODO is fullqualified username
     *
     * @return  DOCUMENT ME!
     */
    public String getAccountName() {
        if (account == null) {
            log.fatal("Benutzername unvollständig: " + account);
        }
        return account;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aAccount  DOCUMENT ME!
     */
    public void setAccountName(final String aAccount) {
        account = aAccount;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isFullReadOnlyMode() {
        return isFullReadOnlyMode;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isFullReadOnlyMode  DOCUMENT ME!
     */
    public void setFullReadOnlyMode(final boolean isFullReadOnlyMode) {
        this.isFullReadOnlyMode = isFullReadOnlyMode;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isCoreReadOnlyMode() {
        return isCoreReadOnlyMode;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isCoreReadOnlyMode  DOCUMENT ME!
     */
    public void setCoreReadOnlyMode(final boolean isCoreReadOnlyMode) {
        this.isCoreReadOnlyMode = isCoreReadOnlyMode;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public HashMap<String, Boolean> getPermissions() {
        return permissions;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  permissions  DOCUMENT ME!
     */
    public void setPermissions(final HashMap<String, Boolean> permissions) {
        this.permissions = permissions;
    }

    @Override
    public Element getConfiguration() {
        return null;
    }

    @Override
    public void masterConfigure(final Element parent) {
        /*
         * <emailConfiguration username="" password="" senderAddress="sebastian.puhl@cismet.de"
         * smtpHost="smtp.uni-saarland.de"> <neuesKommunalesFinanzmanagement>
         * <receiver>sebastian.puhl@cismet.de</receiver> </neuesKommunalesFinanzmanagement> <failures>
         * <receiver>sebastian.puhl@cismet.de</receiver> </failures> </emailConfiguration>
         */
        try {
            final Element email = parent.getChild("emailConfiguration");
            developerMailaddresses = new Vector<String>();
            nkfMailaddresses = new Vector<String>();
            maintenanceMailAddresses = new Vector<String>();
            nkfRecipients = new StringBuffer();
            developerRecipients = new StringBuffer();
            maintenanceRecipients = new StringBuffer();
            try {
                emailConfig = new EmailConfig();
                emailConfig.setUsername(email.getAttributeValue("username"));
                emailConfig.setPassword(email.getAttributeValue("password"));
                emailConfig.setSenderAddress(email.getAttributeValue("senderAddress"));
                emailConfig.setSmtpServer(email.getAttributeValue("smtpHost"));
                for (final Element nkfReveiver
                            : (List<Element>)email.getChild(Message.MAIL_ADDRESSES_NKF).getChildren()) {
                    nkfMailaddresses.add(nkfReveiver.getText());
                    nkfRecipients.append(nkfReveiver.getText() + ",");
                }
                for (final Element developerReveiver
                            : (List<Element>)email.getChild(Message.MAIL_ADDRESSES_DEVELOPER).getChildren()) {
                    developerMailaddresses.add(developerReveiver.getText());
                    developerRecipients.append(developerReveiver.getText() + ",");
                }
                for (final Element maintenanceReveiver
                            : (List<Element>)email.getChild(Message.MAIL_ADDRESSES_MAINTENANCE).getChildren()) {
                    maintenanceMailAddresses.add(maintenanceReveiver.getText());
                    maintenanceRecipients.append(maintenanceReveiver.getText() + ",");
                }
                developerRecipients.deleteCharAt(developerRecipients.length() - 1);
                nkfRecipients.deleteCharAt(nkfRecipients.length() - 1);
                maintenanceRecipients.deleteCharAt(maintenanceRecipients.length() - 1);
                if (log.isDebugEnabled()) {
                    log.debug("Emails werden von: " + emailConfig + " verschickt");
                    log.debug("Empfänger vorhanden: nkf=" + nkfMailaddresses.size() + " admin="
                                + developerMailaddresses.size() + " maintenance=" + maintenanceMailAddresses.size());
                }
                if (log.isDebugEnabled()) {
                    log.debug("Empfänger vorhanden: nkf=" + nkfRecipients.toString() + " admin="
                                + developerRecipients.toString() + " maintenance=" + developerRecipients.toString());
                }
                if ((nkfMailaddresses.size() == 0) || (developerMailaddresses.size() == 0)
                            || (maintenanceMailAddresses.size() == 0)) {
                    throw new Exception("Eine oder mehrere Emailadressen sind nicht konfiguriert");
                }
            } catch (Exception ex) {
                log.fatal(
                    "Fehler beim konfigurieren der Emaileinstellungen, es können keine Emails versand werden.",
                    ex);
                emailConfig = null;
                // TODO Benutzerinformation Applikation beenden?
            }
        } catch (Exception ex) {
            log.error("Fehler beim konfigurieren des Lagis Brokers: ", ex);
        }
        try {
            final HashMap<String, Boolean> perms = new HashMap<String, Boolean>();
            final Element userPermissions = parent.getChild("permissions");
            final List<Element> xmlPermissions = userPermissions.getChildren();
            for (final Element currentPermission : xmlPermissions) {
                try {
                    final String isReadWriteAllowedString = currentPermission.getChildText("readWrite");
                    boolean isReadWriteAllowed = false;
                    if (isReadWriteAllowedString != null) {
                        if (isReadWriteAllowedString.equals("true")) {
                            isReadWriteAllowed = true;
                        }
                    }
                    final String userGroup = currentPermission.getChildText("userGroup");
                    final String userDomain = currentPermission.getChildText("userDomain");
                    final String permissionString = userGroup + "@" + userDomain;
                    log.info("Permissions für: login=*@" + permissionString + " readWriteAllowed=" + isReadWriteAllowed
                                + "(boolean)/" + isReadWriteAllowedString + "(String)");
                    if (permissionString != null) {
                        perms.put(permissionString.toLowerCase(), isReadWriteAllowed);
                    }
                } catch (Exception ex) {
                    log.fatal("Fehler beim lesen eines Userechtes", ex);
                }
            }
            setPermissions(perms);
        } catch (Exception ex) {
            log.fatal("Fehler beim lesen der Userrechte (Permissions)", ex);
            setPermissions(new HashMap<String, Boolean>());
            // TODO wenigstens den Nutzer benachrichtigen sonst ist es zu hard oder nur lesen modus --> besser!!!
            // System.exit(1);
        }
        final Element prefsCids = parent.getChild("cidsAppBackend");
        if (prefsCids != null) {
            try {
                LagisBroker.getInstance().setDomain(prefsCids.getChildText("domain"));
                LagisBroker.getInstance().setCallserverUrl(prefsCids.getChildText("callserverurl"));
                LagisBroker.getInstance().setConnectionClass(prefsCids.getChildText("connectionclass"));
            } catch (Exception ex) {
                log.fatal("Fehler beim lesen der cidsAppBackendcSettings", ex);
                System.exit(1);
            }
        }
    }

    @Override
    public void configure(final Element parent) {
    }

    // TODO: Jean
// /**
// * DOCUMENT ME!
// *
// * @param  verdisServer  DOCUMENT ME!
// */
// public void setVerdisServer(final KassenzeichenFacadeRemote verdisServer) {
// this.verdisServer = verdisServer;
// }
//
// /**
// * DOCUMENT ME!
// *
// * @return  DOCUMENT ME!
// */
// public KassenzeichenFacadeRemote getVerdisServer() {
// return verdisServer;
// }

    /**
     * DOCUMENT ME!
     *
     * @param  geom  DOCUMENT ME!
     */
    public void setCurrentWFSGeometry(final Geometry geom) {
        this.currentWFSGeometry = geom;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Geometry getCurrentWFSGeometry() {
        return currentWFSGeometry;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getCurrentValidationErrorMessage() {
        return currentValidationErrorMessage;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public StatusBar getStatusBar() {
        return statusBar;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  statusBar  DOCUMENT ME!
     */
    public void setStatusBar(final StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public GemarkungCustomBean getGemarkungForKey(final Integer key) {
        GemarkungCustomBean resolvedGemarkung = null;
        if (gemarkungsHashMap != null) {
            resolvedGemarkung = gemarkungsHashMap.get(key);
        } else {
            gemarkungsHashMap = CidsBroker.getInstance().getGemarkungsHashMap();
            if (gemarkungsHashMap != null) {
                resolvedGemarkung = gemarkungsHashMap.get(key);
            }
        }
        return resolvedGemarkung;
    }
    /**
     * TODO configurieren ob es ausgeführt werden soll oder nicht z.B. boolean
     */
    public static void warnIfThreadIsNotEDT() {
        if (!EventQueue.isDispatchThread()) {
            log.fatal("current Thread is not EDT, but should be --> look", new CurrentStackTrace());
        }
    }

    /**
     * DOCUMENT ME!
     */
    public static void warnIfThreadIsEDT() {
        if (EventQueue.isDispatchThread()) {
            log.fatal("current Thread is EDT, but should not --> look", new CurrentStackTrace());
        }
    }
    // TODO what if error during saving

    /**
     * DOCUMENT ME!
     */
    public void acceptChanges() {
        if (parentComponent instanceof LagisApp) {
            ((LagisApp)parentComponent).acceptChanges();
        } else {
            log.warn("Parent Component ist keine LagisApp Klasse");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  workerThread  DOCUMENT ME!
     */
    public void execute(final SwingWorker workerThread) {
        try {
            execService.submit(workerThread);
            if (log.isDebugEnabled()) {
                log.debug("SwingWorker an Threadpool übermittelt");
            }
        } catch (Exception ex) {
            log.fatal("Fehler beim starten eines Swingworkers", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  port  DOCUMENT ME!
     */
    public void setVerdisCrossoverPort(final int port) {
        this.verdisCrossoverPort = port;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getVerdisCrossoverPort() {
        return verdisCrossoverPort;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichenBuffer  DOCUMENT ME!
     */
    public void setKassenzeichenBuffer(final double kassenzeichenBuffer) {
        this.kassenzeichenBuffer = kassenzeichenBuffer;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getKassenzeichenBuffer() {
        return kassenzeichenBuffer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichenBuffer100  DOCUMENT ME!
     */
    public void setKassenzeichenBuffer100(final double kassenzeichenBuffer100) {
        this.kassenzeichenBuffer100 = kassenzeichenBuffer100;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getKassenzeichenBuffer100() {
        return kassenzeichenBuffer100;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  loggedIn  DOCUMENT ME!
     */
    public void setLoggedIn(final boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getConnectionClass() {
        return connectionClass;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  connectionClass  DOCUMENT ME!
     */
    public void setConnectionClass(final String connectionClass) {
        this.connectionClass = connectionClass;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getCallserverUrl() {
        return callserverUrl;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  callserverUrl  DOCUMENT ME!
     */
    public void setCallserverUrl(final String callserverUrl) {
        this.callserverUrl = callserverUrl;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDomain() {
        return domain;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  domain  DOCUMENT ME!
     */
    public void setDomain(final String domain) {
        this.domain = domain;
    }

    /**
     * /** * DOCUMENT ME! * * @version $Revision$, $Date$
     *
     * @return  DOCUMENT ME!
     */
    public boolean isSkipSecurityCheckFlurstueckAssistent() {
        return skipSecurityCheckFlurstueckAssistent;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  skipSecurityCheckFlurstueckAssistent  DOCUMENT ME!
     */
    public void setSkipSecurityCheckFlurstueckAssistent(final boolean skipSecurityCheckFlurstueckAssistent) {
        this.skipSecurityCheckFlurstueckAssistent = skipSecurityCheckFlurstueckAssistent;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean checkFlurstueckWizardUserWantsToFinish() {
        if (!isSkipSecurityCheckFlurstueckAssistent()) {
            if (
                JOptionPane.showConfirmDialog(
                            LagisBroker.getInstance().getParentComponent(),
                            "<html>Möchten Sie die Aktion wirklich abschließen ?",
                            "Sicherheitsabfrage",
                            JOptionPane.WARNING_MESSAGE)
                        != JOptionPane.YES_OPTION) {
                return false;
            }
        }
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  totd  DOCUMENT ME!
     */
    public void setTotd(final String totd) {
        this.totd = totd;
        refreshAppTitle();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  title  DOCUMENT ME!
     */
    public void setTitle(final String title) {
        this.title = title;
        refreshAppTitle();
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshAppTitle() {
        if (SwingUtilities.isEventDispatchThread()) {
            final LagisApp lagisApp = (LagisApp)getParentComponent();
            if ((totd == null) || totd.trim().isEmpty()) {
                lagisApp.setTitle(title);
            } else {
                lagisApp.setTitle(title + " - " + totd);
            }
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        refreshAppTitle();
                    }
                });
        }
    }
}
