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
import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.AbstractAttributeRepresentationFormater;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.newuser.User;

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

import java.beans.PropertyChangeEvent;

import java.net.URL;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.beans.lagis.AnlageklasseCustomBean;
import de.cismet.cids.custom.beans.lagis.BaumCustomBean;
import de.cismet.cids.custom.beans.lagis.BaumKategorieCustomBean;
import de.cismet.cids.custom.beans.lagis.BaumMerkmalCustomBean;
import de.cismet.cids.custom.beans.lagis.BeschlussartCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckArtCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckHistorieCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;
import de.cismet.cids.custom.beans.lagis.GemarkungCustomBean;
import de.cismet.cids.custom.beans.lagis.KostenartCustomBean;
import de.cismet.cids.custom.beans.lagis.MipaCustomBean;
import de.cismet.cids.custom.beans.lagis.MipaKategorieCustomBean;
import de.cismet.cids.custom.beans.lagis.MipaMerkmalCustomBean;
import de.cismet.cids.custom.beans.lagis.NutzungCustomBean;
import de.cismet.cids.custom.beans.lagis.NutzungsartCustomBean;
import de.cismet.cids.custom.beans.lagis.RebeArtCustomBean;
import de.cismet.cids.custom.beans.lagis.RebeCustomBean;
import de.cismet.cids.custom.beans.lagis.VertragCustomBean;
import de.cismet.cids.custom.beans.lagis.VertragsartCustomBean;
import de.cismet.cids.custom.beans.lagis.VerwaltendeDienststelleCustomBean;
import de.cismet.cids.custom.beans.lagis.VerwaltungsbereichCustomBean;
import de.cismet.cids.custom.beans.lagis.ZusatzRolleArtCustomBean;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.statusbar.StatusBar;

import de.cismet.lagis.Exception.ActionNotSuccessfulException;
import de.cismet.lagis.Exception.ErrorInNutzungProcessingException;

import de.cismet.lagis.commons.LagisConstants;
import de.cismet.lagis.commons.LagisMetaclassConstants;

import de.cismet.lagis.gui.main.LagisApp;

import de.cismet.lagis.interfaces.FeatureSelectionChangedListener;
import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.FlurstueckChangeObserver;
import de.cismet.lagis.interfaces.FlurstueckRequester;
import de.cismet.lagis.interfaces.FlurstueckSaver;
import de.cismet.lagis.interfaces.GeometrySlotProvider;
import de.cismet.lagis.interfaces.LagisBrokerPropertyChangeListener;
import de.cismet.lagis.interfaces.Refreshable;
import de.cismet.lagis.interfaces.Resettable;
import de.cismet.lagis.interfaces.Widget;

import de.cismet.lagis.server.search.FlurstueckHistorieGraphSearch;
import de.cismet.lagis.server.search.FlurstueckHistorieGraphSearchResultItem;
import de.cismet.lagis.server.search.ReBeGeomSearch;

import de.cismet.lagis.utillity.EmailConfig;
import de.cismet.lagis.utillity.GeometrySlotInformation;
import de.cismet.lagis.utillity.Message;

import de.cismet.lagis.validation.Validatable;

import de.cismet.lagis.wizard.panels.HistoricNoSucessorDialog;

import de.cismet.lagisEE.interfaces.GeometrySlot;
import de.cismet.lagisEE.interfaces.Key;

import de.cismet.lagisEE.util.FlurKey;

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

    private static LagisBroker INSTANCE = null;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LagisBroker.class);
    private static final String DEFAULT_DOT_HEADER = "digraph G{\n";

    private static final Vector<Resettable> clearAndDisableListeners = new Vector<Resettable>();
    private static final DecimalFormat currencyFormatter = new DecimalFormat(",##0.00 \u00A4");
    // private static DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
    private static final DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private static final Vector<Widget> widgets = new Vector<Widget>();
    private static FlurstueckCustomBean currentFlurstueck = null;
    private static CidsBean currentSperre = null;
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
    public static final Color ODD_ROW_DEFAULT_COLOR = new Color(blue.getRed() + 113,
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
    public static final Color ODD_ROW_LOCK_COLOR = new Color(yellow.getRed() + 23,
            yellow.getGreen()
                    + 31,
            yellow.getBlue()
                    + 134,
            alphaValue);
    public static final Color ERROR_COLOR = red;
    public static final Color ACCEPTED_COLOR = Color.WHITE;
    public static final Color UNKOWN_COLOR = ODD_ROW_LOCK_COLOR;
    public static final Color SUCCESSFUL_COLOR = new Color(113, 220, 109);
    // public static final Color SUCCESSFUL_COLOR = new Color(89,184,73);
    public static final Color INITIAL_COLOR = Color.WHITE;
    // WFS Geometry Color
    public static final Color STADT_FILLING_COLOR = new Color(43, 106, 21);
    public static final Color ABTEILUNG_IX_FILLING_COLOR = new Color(100, 40, 106);
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
    private static GregorianCalendar CALENDAR = new GregorianCalendar();

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum HistoryLevel {

        //~ Enum constants -----------------------------------------------------

        DIRECT_RELATIONS, All, CUSTOM
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum HistorySibblingLevel {

        //~ Enum constants -----------------------------------------------------

        NONE, SIBBLING_ONLY, FULL, CUSTOM
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum HistoryType {

        //~ Enum constants -----------------------------------------------------

        SUCCESSOR, PREDECESSOR, BOTH
    }

    //~ Instance fields --------------------------------------------------------

    private final Vector<FlurstueckChangeListener> observedFlurstueckChangedListeners = new Vector<>();

    private String title;
    private String totd;

    private boolean loggedIn = false;
    private MappingComponent mappingComponent;
    private RootWindow rootWindow;
    private FlurstueckSchluesselCustomBean currentFlurstueckSchluessel = null;
    private String callserverUrl;
    private String domain;
    private String connectionClass;
    private boolean compressionEnabled = false;
    private String account;
    private FlurstueckRequester requester;
    private JFrame parentComponent;
    private boolean isInWfsMode = false;
    // Permissions
    private boolean isFullReadOnlyMode = true;
    private boolean isCoreReadOnlyMode = true;
    /** Creates a new instance of LagisBroker. */
    private StatusBar statusBar;
    private ExecutorService execService = null;
    private int verdisCrossoverPort = -1;
    // TODO Jean
    // private KassenzeichenFacadeRemote verdisServer;
    private Geometry currentWFSGeometry;
    private double rebeBuffer = -0.2;
    private double kassenzeichenBuffer = -0.2;
    private double kassenzeichenBuffer100 = -0.5;
    private boolean skipSecurityCheckFlurstueckAssistent = false;

    private boolean nkfAdminPermission = false;

    private transient ConnectionSession session;
    private String currentValidationErrorMessage = null;
    // TODO optimize ugly code in my opinion old/new terror
    private Vector<Message> messages = new Vector<>();
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
    private final Collection<LagisBrokerPropertyChangeListener> wfsFlurstueckChangeListeners = new ArrayList<>();

    private List<RebeCustomBean> currentRebes = null;

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
        if (INSTANCE == null) {
            INSTANCE = new LagisBroker();
        }
        return INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isCompressionEnabled() {
        return compressionEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  compressionEnabled  DOCUMENT ME!
     */
    public void setCompressionEnabled(final boolean compressionEnabled) {
        this.compressionEnabled = compressionEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueckGeometry  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<RebeCustomBean> getRechteUndBelastungen(final Geometry flurstueckGeometry) {
        try {
            final List<RebeCustomBean> rebes = new ArrayList<>();
            final Geometry bufferedGeom = flurstueckGeometry.buffer(rebeBuffer);
            bufferedGeom.setSRID(flurstueckGeometry.getSRID());
            final Collection<MetaObjectNode> mons = CidsBroker.getInstance().search(new ReBeGeomSearch(bufferedGeom));
            if (mons != null) {
                for (final MetaObjectNode mon : mons) {
                    if (mon != null) {
                        final MetaObject mo = CidsBroker.getInstance()
                                    .getMetaObject(mon.getObjectId(), mon.getClassId(), mon.getDomain());
                        final CidsBean cidsBean = mo.getBean();
                        if (cidsBean instanceof RebeCustomBean) {
                            rebes.add((RebeCustomBean)cidsBean);
                        }
                    }
                }
            }
            return rebes;
        } catch (final ConnectionException ex) {
            LOG.fatal(ex, ex);
            return null;
        }
    }

    /**
     * ToDo place query generation in VerdisCrossover. Give key get Query.
     *
     * @param  bean  e bean DOCUMENT ME!
     */
    public void openKassenzeichenInVerdis(final CidsBean bean) {
        if (bean != null) {
            if ((verdisCrossoverPort < 0) || (verdisCrossoverPort > 65535)) {
                LOG.warn("Crossover: verdisCrossoverPort ist ungültig: " + verdisCrossoverPort);
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
                                    LOG.error("Fehler beim öffnen des Kassenzeichens", ex);
                                    // ToDo message to user;
                                }
                            }
                        };
                    LagisBroker.getInstance().execute(openKassenzeichen);
                } else {
                    LOG.warn("Crossover: konnte keine Query anlegen. Kein Abruf der Kassenzeichen möglich.");
                }
            }
        } else {
            LOG.warn("Crossover: Kann angebenes Flurstück nicht öffnwen");
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
            LOG.info("NKF Admin Recht wurde gesetzt: " + nkfAdminPermission);
        } catch (Exception ex) {
            LOG.error(
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
    public void addWidgets(final List<Widget> widgets) {
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
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Lagis Broker : Reset widgets");
                        }
                        final Iterator<Widget> it = widgets.iterator();
                        while (it.hasNext()) {
                            final Widget tmp = it.next();
                            tmp.clearComponent();
                            tmp.setComponentEditable(false);
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Lagis Broker : Reset widgets durch");
                        }
                    }
                });
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Lagis Broker : Reset widgets");
        }
        final Iterator<Widget> it = widgets.iterator();
        while (it.hasNext()) {
            final Widget tmp = it.next();
            tmp.clearComponent();
            tmp.setComponentEditable(false);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Lagis Broker : Reset widgets durch");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isEditable  DOCUMENT ME!
     */
    public synchronized void setWidgetsEditable(final boolean isEditable) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setze Widgets editable: " + isEditable);
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
                final CidsBean newSperre = createNewFlurstueckSchluesselLock(
                        currentFlurstueck.getFlurstueckSchluessel());
                final CidsBean result = createLock(newSperre);
                if (result != null) {
                    if (result.getProperty("user_string").equals(getAccountName())
                                && result.getProperty("additional_info").equals(
                                    newSperre.getProperty("additional_info"))) {
                        currentSperre = result;
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Sperre konnte erfolgreich angelegt werden");
                        }
                        setWidgetsEditable(true);
                        for (final Feature feature
                                    : (Collection<Feature>)getMappingComponent().getFeatureCollection()
                                    .getSelectedFeatures()) {
                            getMappingComponent().getFeatureCollection().select(feature);
                        }
                        return true;
                    } else {
                        LOG.info("Sperre für flurstueck " + currentFlurstueck.getId()
                                    + " bereitsvorhanden von Benutzer " + (String)result.getProperty("user_string"));
                        JOptionPane.showMessageDialog(
                            parentComponent,
                            "Der Datensatz ist schon vom Benutzer "
                                    + (String)result.getProperty("user_string")
                                    + " zum Verändern gesperrt",
                            "Kein Editieren möglich",
                            JOptionPane.INFORMATION_MESSAGE);
                        return false;
                    }
                } else {
                    LOG.info("Es konnte keine Sperre angelegt werden ?? warum");
                    return false;
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Sperre Flurstueck ist null oder eine Sperre ist bereits vorhanden: \nSperre: "
                                + currentSperre + "\nFlursuteck: " + currentFlurstueck);
                }
                return false;
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim anlegen der Sperre", ex);
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean releaseLocks() {
        final boolean success = releaseLocks(Arrays.asList(currentSperre));
        if (success) {
            currentSperre = null;
        }
        return success;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   locks  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean releaseLocks(final List<CidsBean> locks) {
        if ((locks != null) && !locks.isEmpty()) {
            for (final CidsBean lock : locks) {
                if (!releaseLock(lock)) {
                    return false;
                }
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("keine Sperre zum Lösen vorhanden");
            }
            return false;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Alle Sperren wurden erfolgreich gelöst");
        }
        setWidgetsEditable(false);
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   lock  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean releaseLock(final CidsBean lock) {
        try {
            if (lock != null) {
                lock.delete();
                lock.persist();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Sperre erfolgreich gelöst");
                }
            } else {
                LOG.warn("Sperre war null.");
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim lösen der Sperre", ex);
            return false;
        }
        return true;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean isLocked(final FlurstueckSchluesselCustomBean key) {
        if (key != null) {
            final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("sperre");
            if (metaclass == null) {
                return null;
            }
            final String query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " "
                        + "FROM " + metaclass.getTableName() + " "
                        + "WHERE " + metaclass.getTableName() + ".fk_flurstueck_schluessel = " + key.getId();
            final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);
            if ((mos != null) && (mos.length > 0)) {
                final CidsBean sperre = mos[0].getBean();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es ist eine Sperre vorhanden und wird von: " + (String)sperre.getProperty(
                                    "user_string")
                                + " gehalten");
                }
                return sperre;
            }
        } else {
            if (LOG.isDebugEnabled()) {
                // TODO EXCEPTIOn !!!!!!! KNAUP
                LOG.debug("Flurstückkey == null");
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Es ist keine Sperre für das angegebne Flurstück vorhanden");
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueckSchluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public CidsBean createNewFlurstueckSchluesselLock(final FlurstueckSchluesselCustomBean flurstueckSchluessel)
            throws Exception {
        final CidsBean lockBean = CidsBean.createNewCidsBeanFromTableName(LagisConstants.DOMAIN_LAGIS, "cs_locks");
        lockBean.setProperty("class_id", flurstueckSchluessel.getMetaObject().getClassID());
        lockBean.setProperty("object_id", flurstueckSchluessel.getId());
        lockBean.setProperty("user_string", getAccountName());
        lockBean.setProperty("additional_info", new Date().toString());
        return createLock(lockBean);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   oldFlurstueckSchluessel  DOCUMENT ME!
     * @param   newFlurstueckSchluessel  DOCUMENT ME!
     * @param   benutzerkonto            DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public FlurstueckCustomBean renameFlurstueck(final FlurstueckSchluesselCustomBean oldFlurstueckSchluessel,
            final FlurstueckSchluesselCustomBean newFlurstueckSchluessel,
            final String benutzerkonto) throws ActionNotSuccessfulException {
        oldFlurstueckSchluessel.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        newFlurstueckSchluessel.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        oldFlurstueckSchluessel.setLetzte_bearbeitung(getCurrentDate());
        newFlurstueckSchluessel.setLetzte_bearbeitung(getCurrentDate());
        CidsBean lock = null;
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Rename Flurstück");
            }
            final FlurstueckCustomBean oldFlurstueck = retrieveFlurstueck(oldFlurstueckSchluessel);
            FlurstueckCustomBean newFlurstueck;

            if (oldFlurstueck != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("AltesFlurstück existiert");
                }
                // TODO ugly
                // checks either there is a lock for the specific flurstück or not
                if (isLocked(oldFlurstueck.getFlurstueckSchluessel()) == null) {
                    lock = createNewFlurstueckSchluesselLock(oldFlurstueck.getFlurstueckSchluessel());
                    if (lock == null) {
                        // TODO throw new EJBException(new ActionNotSuccessfulException("Anlegen einer SperreCustomBean
                        // nicht möglich"));
                        throw new ActionNotSuccessfulException(
                            "Anlegen einer Sperre für das alte Flurstück nicht möglich");
                    }
                } else {
                    // TODO throw new EJBException(new ActionNotSuccessfulException("Es exisitert bereits eine
                    // SperreCustomBean"));
                    throw new ActionNotSuccessfulException("Es exisitert bereits eine Sperre für das alte Flurstück");
                }
//HistoricResult result = ;

                setFlurstueckHistoric(oldFlurstueckSchluessel);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Flurstück wurde Historisch gesetzt");
                }
                // TODO Better FlurstückHistoryEntry??
                // FlurstueckHistorie fHistorie = new FlurstueckHistorieCustomBean();
                // TODO Flurstückaktion/Historie
                // TODO NO UNIQUE RESULT EXCEPTION --> möglich ?
                // FlurstueckHistorie fHistorie = new FlurstueckHistorieCustomBean();
                if (!existHistoryEntry(oldFlurstueck)) {
                    newFlurstueck = createFlurstueck(newFlurstueckSchluessel);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Es exitieren kein History Eintrag --> keine Kante zu einem anderen Flurstück");
                        LOG.debug("Kein nachfolger für das Flurstück vorhanden --> Lege neues Flurstueck an");
                        LOG.debug("Erzeuge History Eintrag für altes Flurstück");
                    }
                    createHistoryEdge(oldFlurstueck, newFlurstueck);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Neuer History Eintrag für Flurstück erzeugt");
                    }
                } else {
                    if (LOG.isDebugEnabled()) {
                        // Exception ex =  new ActionNotSuccessfulException("Flurstück");
                        LOG.debug("Renamen des Flurstücks nicht möglich");
                    }
                    releaseLock(lock);
                    throw new ActionNotSuccessfulException(
                        "Es existieren bereits Historieneinträge für dieses Flurstück");
                }

//                    if(historyEntry != null){
//
//                    } else {
//
//                    }
                if (newFlurstueck != null) {
                    if (LOG.isDebugEnabled()) {
//                        System.out.println("Das Flurstück wurde erfogreich angelegt --> Setze Nachfolger des Alten Flurstücks");
//                        historyEntry.setNachfolger(newFlurstueck);
//                        em.merge(historyEntry);
//                        System.out.println("Erzeuge History Eintrag für neues Flurstück");
//                        historyEntry = new FlurstueckHistorieCustomBean();
//                        historyEntry.setVorgaenger(oldFlurstueck);
//                        historyEntry.setFlurstueck(newFlurstueck);
//                        createFlurstueckHistoryEntry(historyEntry);
                        LOG.debug("Alle Aktionen für das umbenennen erfolgreich abgeschlossen.");
                    }

                    final User user = SessionManager.getSession().getUser();
                    final MetaClass mcDmsUrl = ClassCacheMultiple.getMetaClass(
                            LagisConstants.DOMAIN_LAGIS,
                            LagisMetaclassConstants.DMS_URL);
                    final MetaClass mcNutzung = ClassCacheMultiple.getMetaClass(
                            LagisConstants.DOMAIN_LAGIS,
                            LagisMetaclassConstants.NUTZUNG);
                    final MetaClass mcRebe = ClassCacheMultiple.getMetaClass(
                            LagisConstants.DOMAIN_LAGIS,
                            LagisMetaclassConstants.REBE);
                    final MetaClass mcVerwaltungsbereichEintrag = ClassCacheMultiple.getMetaClass(
                            LagisConstants.DOMAIN_LAGIS,
                            LagisMetaclassConstants.VERWALTUNGSBEREICHE_EINTRAG);

                    final String queryDmsUrl = "SELECT " + mcDmsUrl.getID() + ", " + mcDmsUrl.getPrimaryKey()
                                + " FROM " + mcDmsUrl.getTableName() + " WHERE " + " fk_flurstueck = "
                                + oldFlurstueck.getId().toString();
                    final String queryNutzung = "SELECT " + mcNutzung.getID() + ", " + mcNutzung.getPrimaryKey()
                                + " FROM " + mcNutzung.getTableName() + " WHERE " + " fk_flurstueck = "
                                + oldFlurstueck.getId().toString();
                    final String queryRebe = "SELECT " + mcRebe.getID() + ", " + mcRebe.getPrimaryKey() + " FROM "
                                + mcRebe.getTableName() + " WHERE " + " fk_flurstueck = "
                                + oldFlurstueck.getId().toString();
                    final String queryVerwaltungsbereichEintrag = "SELECT " + mcVerwaltungsbereichEintrag.getID()
                                + ", "
                                + mcVerwaltungsbereichEintrag.getPrimaryKey() + " FROM "
                                + mcVerwaltungsbereichEintrag.getTableName() + " WHERE " + " fk_flurstueck = "
                                + oldFlurstueck.getId().toString();

                    newFlurstueck.getAr_baeume().addAll(oldFlurstueck.getAr_baeume());
                    oldFlurstueck.getAr_baeume().clear();

                    newFlurstueck.getAr_mipas().addAll(oldFlurstueck.getAr_mipas());
                    oldFlurstueck.getAr_mipas().clear();

                    newFlurstueck.getAr_vertraege().addAll(oldFlurstueck.getAr_vertraege());
                    oldFlurstueck.getAr_vertraege().clear();

                    for (final MetaObject moDmsUrl
                                : SessionManager.getProxy().getMetaObjectByQuery(user, queryDmsUrl)) {
                        moDmsUrl.getBean().setProperty("fk_flurstueck", newFlurstueck);
                        moDmsUrl.getBean().persist();
                    }

                    for (final MetaObject moNutzung
                                : SessionManager.getProxy().getMetaObjectByQuery(user, queryNutzung)) {
                        moNutzung.getBean().setProperty("fk_flurstueck", newFlurstueck);
                        moNutzung.getBean().persist();
                    }

                    for (final MetaObject moRebe : SessionManager.getProxy().getMetaObjectByQuery(user, queryRebe)) {
                        moRebe.getBean().setProperty("fk_flurstueck", newFlurstueck);
                        moRebe.getBean().persist();
                    }

                    for (final MetaObject moVerwaltungsbereichEintrag
                                : SessionManager.getProxy().getMetaObjectByQuery(
                                    user,
                                    queryVerwaltungsbereichEintrag)) {
                        moVerwaltungsbereichEintrag.getBean().setProperty("fk_flurstueck", newFlurstueck);
                        moVerwaltungsbereichEintrag.getBean().persist();
                    }

                    newFlurstueck.setFk_spielplatz(oldFlurstueck.getFk_spielplatz());
                    newFlurstueck.setBemerkung(oldFlurstueck.getBemerkung());
                    newFlurstueck.setIn_stadtbesitz(oldFlurstueck.getIn_stadtbesitz());
                    newFlurstueck = (FlurstueckCustomBean)newFlurstueck.persist();
                    oldFlurstueck.persist();
                } else {
                    if (LOG.isDebugEnabled()) {
                        // TODO IF THIS CASE IS POSSIBLE ROLLBACK TRANSACTION
                        LOG.debug("Das neue Flurstück konnte nicht angelegt werden.");
                    }
                    releaseLock(lock);
                    throw new ActionNotSuccessfulException("Das neue Flurstück konnte nicht angelegt werden.");
                }

                releaseLock(lock);
                return newFlurstueck;
            } else {
                throw new ActionNotSuccessfulException("Altes Flurstück existiert nicht.");
            }
        } catch (final ActionNotSuccessfulException ex) {
            throw ex;
        } catch (final Exception ex) {
            LOG.error("Unbekannter Fehler beim renamen des Flurstücks.", ex);
//            ActionNotSuccessfulException tmpEx;
//            if(ex instanceof ActionNotSuccessfulException){
//                //tmpEx=(ActionNotSuccessfulException)
//            } else {
//                tmpEx=new ActionNotSuccessfulException("Flurstück konnte nicht umbennant werden");
//                tmpEx.setStackTrace(ex.getStackTrace());
//            }
//            throw tmpEx;
            releaseLock(lock);
            // TODO set nestedException
            throw new ActionNotSuccessfulException(
                "Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator",
                ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueckHistorie  DOCUMENT ME!
     * @param  allEdges            DOCUMENT ME!
     * @param  direction           DOCUMENT ME!
     */
    private void replacePseudoFlurstuecke(final Collection<FlurstueckHistorieCustomBean> flurstueckHistorie,
            final Collection<FlurstueckHistorieCustomBean> allEdges,
            final HistoryType direction) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("replacePseudoFlurstuecke: direction=" + direction + " Kanten=" + flurstueckHistorie);
        }
        if (flurstueckHistorie != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("es existieren Kanten");
            }
            final Iterator<FlurstueckHistorieCustomBean> itr = flurstueckHistorie.iterator();
            final ArrayList<FlurstueckHistorieCustomBean> pseudoKeysToRemove = new ArrayList<>();
            final Collection<FlurstueckHistorieCustomBean> realNeighbours = new HashSet<>();
            while (itr.hasNext()) {
                final FlurstueckHistorieCustomBean currentFlurstueckHistorie = itr.next();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("" + currentFlurstueckHistorie.getNachfolger());
                }
                if ((direction == HistoryType.PREDECESSOR)
                            && (currentFlurstueckHistorie.getNachfolger() != null)
                            && (currentFlurstueckHistorie.getVorgaenger() != null)
                            && (currentFlurstueckHistorie.getVorgaenger().getFlurstueckSchluessel() != null)
                            && !currentFlurstueckHistorie.getVorgaenger().getFlurstueckSchluessel()
                            .isEchterSchluessel()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "Vorgänger ist ein PseudoFlurstück und besitzt vorgänge --> suche heraus und ersetze");
                    }
                    allEdges.add(currentFlurstueckHistorie);
                    pseudoKeysToRemove.add(currentFlurstueckHistorie);
                    final Collection<FlurstueckHistorieCustomBean> result = getHistoryPredecessors(
                            currentFlurstueckHistorie.getVorgaenger().getFlurstueckSchluessel());
                    if (result != null) {
                        realNeighbours.addAll(result);
                    }
                } else if ((direction == HistoryType.SUCCESSOR)
                            && (currentFlurstueckHistorie.getNachfolger() != null)
                            && (currentFlurstueckHistorie.getVorgaenger() != null)
                            && (currentFlurstueckHistorie.getNachfolger().getFlurstueckSchluessel() != null)
                            && !currentFlurstueckHistorie.getNachfolger().getFlurstueckSchluessel()
                            .isEchterSchluessel()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "Vorgänger ist ein PseudoFlurstück und besitzt vorgänge --> suche heraus und ersetze");
                    }
                    allEdges.add(currentFlurstueckHistorie);
                    pseudoKeysToRemove.add(currentFlurstueckHistorie);
                    final Collection<FlurstueckHistorieCustomBean> result = getHistorySuccessor(
                            currentFlurstueckHistorie.getNachfolger().getFlurstueckSchluessel());
                    if (result != null) {
                        realNeighbours.addAll(result);
                    }
                }
            }
            flurstueckHistorie.removeAll(pseudoKeysToRemove);
            flurstueckHistorie.addAll(realNeighbours);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Pseudoflurstücke zum ersetzen");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueckSchluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<FlurstueckHistorieCustomBean> getHistoryPredecessors(
            final FlurstueckSchluesselCustomBean flurstueckSchluessel) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suche Vorgänger für Flurstück");
            LOG.debug("ID des Schluessels ist: " + flurstueckSchluessel);
        }

        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("flurstueck_historie");
        if (metaclass == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + metaclass.getID() + ", "
                    + "   " + metaclass.getTableName() + "." + metaclass.getPrimaryKey() + " "
                    + "FROM "
                    + "   " + metaclass.getTableName() + ", "
                    + "   flurstueck "
                    + "WHERE "
                    + "   " + metaclass.getTableName() + ".fk_nachfolger = flurstueck.id "
                    + "   AND flurstueck.fk_flurstueck_schluessel = " + flurstueckSchluessel.getId();

        final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);
        final Collection<FlurstueckHistorieCustomBean> historyEntries = new HashSet<FlurstueckHistorieCustomBean>();
        for (final MetaObject metaObject : mos) {
            historyEntries.add((FlurstueckHistorieCustomBean)metaObject.getBean());
        }
        if (historyEntries != null) {
            if (historyEntries.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ergebnisliste ist leer");
                }
                return null;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Suche lieferte mindestens ein Ergebnis zurück");
                }
            }

//                while(it.hasNext()){
//                    FlurstueckHistorieCustomBean curHistoryEntry = it.next();
//                    //TODO possible that a key is null (inconsitence) ??
//                    if(curHistoryEntry != null && curHistoryEntry.getVorgaenger() != null){
//                        System.out.println("Jetziger HistoryEintrag != null und Vorgänger != null");
//                        result.add(curHistoryEntry.getVorgaenger().getFlurstueckSchluessel());
//                    } else {
//                        //TODO EXCEPTION
//                        System.out.println("Jetziger HistoryEintrag oder Vorgänger == null");
//                    }
//                }
            return historyEntries;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Suche lieferte kein Ergebnis zurück");
            }
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   newSperre  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private CidsBean createLock(final CidsBean newSperre) {
        if (newSperre != null) {
            final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("cs_locks");
            if (metaclass == null) {
                return null;
            }
            final String query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " "
                        + "FROM " + metaclass.getTableName() + " "
                        + "WHERE " + metaclass.getTableName() + ".object_id = " + newSperre.getProperty("object_id")
                        + " AND " + metaclass.getTableName() + ".class_id = " + newSperre.getProperty("class_id");
            final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);

            if ((mos == null) || (mos.length == 0)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Keine Sperre für das angegebene Flurstueck vorhanden, es wird versucht eine anzulegen");
                }
                try {
                    return newSperre.persist();
                } catch (Exception ex) {
                    LOG.error("Fehler beim Anlegen der Sperre", ex);
                    return null;
                }
            } else if (mos.length == 1) {
                final CidsBean sperre = mos[0].getBean();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es ist eine Sperre vorhanden und wird von: " + (String)sperre.getProperty(
                                    "user_string") + " gehalten");
                }
                return sperre;
            } else if (mos.length > 1) {
                final CidsBean sperre = mos[0].getBean();
                LOG.error("Es sind mehrere Sperren vorhanden");
                // TODO Jean: hier wurde vorher null zurückgegeben, denke aber nicht dass das richtig war
                return sperre;
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Die Sperre die anglegt werden soll ist null");
            }
            return null;
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   gem  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public GemarkungCustomBean completeGemarkung(final GemarkungCustomBean gem) {
        try {
            if ((gem != null) && (gem.getBezeichnung() != null)) {
                final Collection<GemarkungCustomBean> gemarkungen = getGemarkungsKeys();
                if (gemarkungen != null) {
                    final Iterator<GemarkungCustomBean> it = gemarkungen.iterator();
                    while (it.hasNext()) {
                        final GemarkungCustomBean tmp = it.next();
                        if (tmp.getBezeichnung().equals(gem.getBezeichnung())) {
                            return tmp;
                        }
                    }
                    return null;
                } else {
                    return null;
                }
            } else if ((gem != null) && (gem.getSchluessel() != null)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Schlüssel != null");
                }
                final Collection<GemarkungCustomBean> gemarkungen = getGemarkungsKeys();
                if (gemarkungen != null) {
                    final Iterator<GemarkungCustomBean> it = gemarkungen.iterator();
                    while (it.hasNext()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("checke schlüssel durch");
                        }
                        final GemarkungCustomBean tmp = it.next();
                        if (tmp.getSchluessel().intValue() == gem.getSchluessel().intValue()) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Schlüssel gefunden");
                            }
                            return tmp;
                        }
                    }
                    return null;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Fehler beim Kompletieren einer Gemarkung: " + gem, ex);
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getCurrentDate() {
        return new Date();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueck  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void modifyFlurstueck(final FlurstueckCustomBean flurstueck) throws ActionNotSuccessfulException {
        flurstueck.getFlurstueckSchluessel().setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        flurstueck.getFlurstueckSchluessel().setLetzte_bearbeitung(getCurrentDate());
        try {
            processNutzungen(flurstueck.getNutzungen(), flurstueck.getFlurstueckSchluessel().getKeyString());
            checkIfFlurstueckWasStaedtisch(flurstueck.getFlurstueckSchluessel(), null);
            flurstueck.persist();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Flurstück gespeichert");
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim speichern der Entität", ex);
            throw new ActionNotSuccessfulException("Fehler beim speichern eines vorhandenen Flurstücks", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key  DOCUMENT ME!
     */
    public void modifyFlurstueckSchluessel(final FlurstueckSchluesselCustomBean key) {
        key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        key.setLetzte_bearbeitung(getCurrentDate());
        try {
            final FlurstueckSchluesselCustomBean oldKey = completeFlurstueckSchluessel(key);
            FlurstueckArtCustomBean oldArt = null;
            if (oldKey != null) {
                oldArt = oldKey.getFlurstueckArt();
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Alterschlüssel ist == null");
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(("Art " + oldArt) != null);
                LOG.debug(("Bezeichnung " + oldArt.getBezeichnung()) != null);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Alter war staedtich "
                            + FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                                oldArt.getBezeichnung()));
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Art hat sich geändert "
                            + !FlurstueckArtCustomBean.FLURSTUECK_ART_EQUALATOR.pedanticEquals(
                                oldArt,
                                key.getFlurstueckArt()));
            }
            if ((oldArt != null) && (oldArt.getBezeichnung() != null)
                        && FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(oldArt.getBezeichnung())
                        && !FlurstueckArtCustomBean.FLURSTUECK_ART_EQUALATOR.pedanticEquals(
                            oldArt,
                            key.getFlurstueckArt())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                        "Die Art eines städtischen Flurstücks wurde auf eine andere geändert update lettzer Stadtbestizt Datum");
                }
                key.setWarStaedtisch(true);
                key.setDatumLetzterStadtbesitz(new Date());
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                        "Die Art eines Städtischen Flurstücks wurde nicht auf eine andere geändert --> checkIfFlurstueckWasStaedtisch");
                }
                checkIfFlurstueckWasStaedtisch(key, null);
            }
            key.persist();
        } catch (final Throwable t) {
            LOG.error("Fehler beim speichern der Entität", t);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueck  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void deleteFlurstueck(final FlurstueckCustomBean flurstueck) throws ActionNotSuccessfulException {
        boolean illegalDelete = false;
        try {
            if (!illegalDelete) {
                for (final VerwaltungsbereichCustomBean current : flurstueck.getVerwaltungsbereiche()) {
                    if (current != null) {
                        illegalDelete = true;
                        break;
                    }
                }
            }
            if (!illegalDelete) {
                for (final NutzungCustomBean current : flurstueck.getNutzungen()) {
                    if (current != null) {
                        illegalDelete = true;
                        break;
                    }
                }
            }
            if (!illegalDelete) {
                for (final VertragCustomBean current : flurstueck.getVertraege()) {
                    if (current != null) {
                        illegalDelete = true;
                        break;
                    }
                }
            }
            if (!illegalDelete) {
                // ToDo check if successor are also interesting
                for (final FlurstueckHistorieCustomBean current
                            : getAllHistoryEntries(flurstueck.getFlurstueckSchluessel())) {
                    if (current != null) {
                        illegalDelete = true;
                        break;
                    }
                }
            }
            if (illegalDelete) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es sind daten für das Flurstück vorhanden es kann nicht gelöscht werden");
                }
                throw new ActionNotSuccessfulException(
                    "Es sind Daten für das Flurstück vorhanden, es kann nicht gelöscht werden");
            } else {
                flurstueck.delete();
                flurstueck.persist();
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim löschen eines Flurstücks: " + flurstueck, ex);
            if (ex instanceof ActionNotSuccessfulException) {
                throw (ActionNotSuccessfulException)ex;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<GemarkungCustomBean> getGemarkungsKeys() {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("gemarkung");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsBroker.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " " + "FROM " + metaclass.getTableName());
        final Collection<GemarkungCustomBean> beans = new HashSet<GemarkungCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((GemarkungCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public HashMap<Integer, GemarkungCustomBean> getGemarkungsHashMap() {
        final Collection<GemarkungCustomBean> gemarkungen = getGemarkungsKeys();
        if (gemarkungen != null) {
            final HashMap<Integer, GemarkungCustomBean> result = new HashMap<Integer, GemarkungCustomBean>();
            for (final GemarkungCustomBean gemarkung : gemarkungen) {
                if ((gemarkung != null) && (gemarkung.getBezeichnung() != null)
                            && (gemarkung.getSchluessel() != null)) {
                    result.put(gemarkung.getSchluessel(), gemarkung);
                }
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueckHistorie  DOCUMENT ME!
     */
    public void createFlurstueckHistoryEntry(final FlurstueckHistorieCustomBean flurstueckHistorie) {
        try {
            flurstueckHistorie.persist();
        } catch (Exception ex) {
            LOG.error("Fehler beim anlegen der Flurstueckshistorie: " + flurstueckHistorie, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<Key> getDependingKeysForKey(final Key key) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("GetDependingKeysForKey");
        }
        try {
            if (key != null) {
                if (key instanceof GemarkungCustomBean) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Key ist Gemarkung");
                    }
                    final GemarkungCustomBean currentGemarkung = (GemarkungCustomBean)key;
                    if ((currentGemarkung.getSchluessel() != null)) {
                        // TODO Duplicated code --> extract

                        final MetaClass metaclass = CidsBroker.getInstance()
                                    .getLagisMetaClass(LagisMetaclassConstants.FLURSTUECK_SCHLUESSEL);
                        if (metaclass == null) {
                            return null;
                        }
                        final String query = "SELECT DISTINCT "
                                    + "   min(" + metaclass.getTableName() + "." + metaclass.getPrimaryKey()
                                    + ") AS id, "
                                    + "   min(" + metaclass.getTableName() + ".flur) AS flur "
                                    + "FROM "
                                    + "   " + metaclass.getTableName() + ", "
                                    + "   gemarkung "
                                    + "WHERE "
                                    + "    " + metaclass.getTableName() + ".fk_gemarkung = gemarkung.id "
                                    + "    AND " + metaclass.getTableName() + ".fk_flurstueck_art != 3 "
                                    + "    AND gemarkung.schluessel = " + currentGemarkung.getSchluessel() + " "
                                    + "GROUP BY " + metaclass.getTableName() + ".flur";

                        final MetaObject[] mos = CidsBroker.getInstance()
                                    .getLagisLWMetaObjects(
                                        metaclass.getTableName(),
                                        query,
                                        new String[] { "id", "flur" },
                                        new AbstractAttributeRepresentationFormater() {

                                            @Override
                                            public String getRepresentation() {
                                                return String.valueOf(getAttribute("flur"));
                                            }
                                        });

                        if (mos != null) {
                            final Collection flurKeys = new HashSet();
                            for (final MetaObject mo : mos) {
                                final Integer flur = Integer.parseInt(mo.toString());
                                flurKeys.add(new FlurKey(currentGemarkung, flur));
                            }
                            return flurKeys;
                        } else {
                            return new HashSet();
                        }
                    } else if ((currentGemarkung.getBezeichnung() != null)) {
                        final GemarkungCustomBean completed = completeGemarkung(currentGemarkung);
                        if (completed != null) {
                            final MetaClass metaclass = CidsBroker.getInstance()
                                        .getLagisMetaClass(LagisMetaclassConstants.FLURSTUECK_SCHLUESSEL);
                            if (metaclass == null) {
                                return null;
                            }
                            final String query = "SELECT DISTINCT "
                                        + "   min(" + metaclass.getTableName() + "." + metaclass.getPrimaryKey()
                                        + ") AS id, "
                                        + "   min(" + metaclass.getTableName() + ".flur) AS flur "
                                        + "FROM "
                                        + "   " + metaclass.getTableName() + ", "
                                        + "   gemarkung "
                                        + "WHERE "
                                        + "    " + metaclass.getTableName() + ".fk_gemarkung = gemarkung.id "
                                        + "    AND " + metaclass.getTableName() + ".fk_flurstueck_art != 3 "
                                        + "    AND gemarkung.schluessel = " + completed.getSchluessel() + " "
                                        + "GROUP BY " + metaclass.getTableName() + ".flur";

                            final MetaObject[] mos = CidsBroker.getInstance()
                                        .getLagisLWMetaObjects(
                                            metaclass.getTableName(),
                                            query,
                                            new String[] { "id", "flur" },
                                            new AbstractAttributeRepresentationFormater() {

                                                @Override
                                                public String getRepresentation() {
                                                    return String.valueOf(getAttribute("flur"));
                                                }
                                            });
                            if (mos != null) {
                                final Collection flurKeys = new HashSet();
                                for (final MetaObject mo : mos) {
                                    final Integer flur = Integer.parseInt(mo.toString());
                                    flurKeys.add(new FlurKey(currentGemarkung, flur));
                                }
                                return flurKeys;
                            } else {
                                return new HashSet();
                            }
                        } else {
                            return new HashSet();
                        }
                    } else {
                        return new HashSet();
                    }
                } else if (key instanceof FlurKey) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Key ist Flur");
                    }
                    final FlurKey currentFlur = (FlurKey)key;

                    final MetaClass metaclass = CidsBroker.getInstance()
                                .getLagisMetaClass(LagisMetaclassConstants.FLURSTUECK_SCHLUESSEL);
                    if (metaclass == null) {
                        return null;
                    }
                    String query = null;

                    // TODDO WHY INTEGER
                    if (!currentFlur.isCurrentFilterEnabled() && !currentFlur.isHistoricFilterEnabled()
                                && !currentFlur.isAbteilungXIFilterEnabled()
                                && !currentFlur.isStaedtischFilterEnabled()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Kein Filter für Flur Aktiviert");
                        }
                        query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                                    + metaclass.getPrimaryKey() + " "
                                    + "FROM "
                                    + "   " + metaclass.getTableName() + ", "
                                    + "   gemarkung "
                                    + "WHERE "
                                    + "   " + metaclass.getTableName() + ".fk_gemarkung = gemarkung.id "
                                    + "   AND " + metaclass.getTableName() + ".flur = " + currentFlur.getFlurId() + " "
                                    + "   AND " + metaclass.getTableName() + ".fk_flurstueck_art != 3 "
                                    + "   AND gemarkung.schluessel = " + currentFlur.getGemarkungsId();
                    } else if (currentFlur.isCurrentFilterEnabled()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Filter nur aktuelle Flurstücke: Aktiviert");
                        }
                        query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                                    + metaclass.getPrimaryKey() + " "
                                    + "FROM "
                                    + "   " + metaclass.getTableName() + ", "
                                    + "   gemarkung "
                                    + "WHERE "
                                    + "   " + metaclass.getTableName() + ".fk_gemarkung = gemarkung.id "
                                    + "   AND " + metaclass.getTableName() + ".flur = " + currentFlur.getFlurId() + " "
                                    + "   AND " + metaclass.getTableName() + ".fk_flurstueck_art != 3 "
                                    + "   AND " + metaclass.getTableName() + ".gueltig_bis IS NULL "
                                    + "   AND gemarkung.schluessel = " + currentFlur.getGemarkungsId();
                    } else if (currentFlur.isHistoricFilterEnabled()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Filter nur historische Flurstücke: Aktiviert");
                        }
                        query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                                    + metaclass.getPrimaryKey() + " "
                                    + "FROM "
                                    + "   " + metaclass.getTableName() + ", "
                                    + "   gemarkung "
                                    + "WHERE "
                                    + "   " + metaclass.getTableName() + ".fk_gemarkung = gemarkung.id "
                                    + "   AND " + metaclass.getTableName() + ".flur = " + currentFlur.getFlurId() + " "
                                    + "   AND " + metaclass.getTableName() + ".fk_flurstueck_art != 3 "
                                    + "   AND " + metaclass.getTableName() + ".gueltig_bis IS NOT NULL "
                                    + "   AND gemarkung.schluessel = " + currentFlur.getGemarkungsId();
                    } else if (currentFlur.isAbteilungXIFilterEnabled()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Filter nur Abteilung IX Flurstücke: Aktiviert");
                        }
                        query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                                    + metaclass.getPrimaryKey() + " "
                                    + "FROM "
                                    + "   " + metaclass.getTableName() + ", "
                                    + "   gemarkung "
                                    + "WHERE "
                                    + "   " + metaclass.getTableName() + ".fk_gemarkung = gemarkung.id "
                                    + "   AND " + metaclass.getTableName() + ".flur = " + currentFlur.getFlurId() + " "
                                    + "   AND " + metaclass.getTableName() + ".fk_flurstueck_art = 2 "
                                    + "   AND gemarkung.schluessel = " + currentFlur.getGemarkungsId();
                    } else if (currentFlur.isStaedtischFilterEnabled()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Filter nur staedtische Flurstücke: Aktiviert");
                        }
                        query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                                    + metaclass.getPrimaryKey() + " "
                                    + "FROM "
                                    + "   " + metaclass.getTableName() + ", "
                                    + "   gemarkung "
                                    + "WHERE "
                                    + "   " + metaclass.getTableName() + ".fk_gemarkung = gemarkung.id "
                                    + "   AND " + metaclass.getTableName() + ".flur = " + currentFlur.getFlurId() + " "
                                    + "   AND " + metaclass.getTableName() + ".fk_flurstueck_art = 1 "
                                    + "   AND gemarkung.schluessel = " + currentFlur.getGemarkungsId();
                    }
                    if (query != null) {
                        final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);
                        final Collection<FlurstueckSchluesselCustomBean> flurstuecke =
                            new HashSet<FlurstueckSchluesselCustomBean>();
                        for (final MetaObject metaObject : mos) {
                            flurstuecke.add((FlurstueckSchluesselCustomBean)metaObject.getBean());
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Ergebnisse für Abfrage vorhanden: " + flurstuecke.size());
                        }
                        return new HashSet(flurstuecke);
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Keine Ergebnisse für Abfrage vorhanden");
                        }
                        return new HashSet();
                    }
                }
            } else {
                return new HashSet();
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim abfragen eines Keys: " + key + " Class: " + ((key != null) ? key.getClass() : null),
                ex);
        }
        return new HashSet();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckCustomBean retrieveFlurstueck(final FlurstueckSchluesselCustomBean key) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Finde Flurstuck: ");
                LOG.debug("Id       : " + key.getId());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Gemarkung: " + key.getGemarkung());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Flur     : " + key.getFlur());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Zaehler  : " + key.getFlurstueckZaehler());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Nenner   : " + key.getFlurstueckNenner());
            }

            final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("flurstueck");
            if (metaclass == null) {
                return null;
            }

            final Integer flur = key.getFlur();
            final Integer fsZaehler = key.getFlurstueckZaehler();
            final Integer fsNenner = key.getFlurstueckNenner();
            final Integer gemarkung = (key.getGemarkung() == null) ? null : key.getGemarkung().getId();

            final MetaObject[] mos;
            if ((flur != null) && (fsZaehler != null) && (fsNenner != null) && (gemarkung != null)) {
                mos = CidsBroker.getInstance()
                            .getLagisMetaObject(
                                    "SELECT "
                                    + metaclass.getID()
                                    + ", "
                                    + metaclass.getTableName()
                                    + "."
                                    + metaclass.getPrimaryKey()
                                    + " "
                                    + " FROM "
                                    + metaclass.getTableName()
                                    + ", flurstueck_schluessel fk"
                                    + " WHERE "
                                    + metaclass.getTableName()
                                    + ".fk_flurstueck_schluessel = fk.id "
                                    + " AND fk.flur = "
                                    + key.getFlur()
                                    + " AND fk.fk_gemarkung = "
                                    + key.getGemarkung().getId()
                                    + " AND fk.flurstueck_zaehler = "
                                    + key.getFlurstueckZaehler()
                                    + " AND fk.flurstueck_nenner  = "
                                    + key.getFlurstueckNenner());
            } else {
                mos = CidsBroker.getInstance()
                            .getLagisMetaObject(
                                    "SELECT "
                                    + metaclass.getID()
                                    + ", "
                                    + metaclass.getTableName()
                                    + "."
                                    + metaclass.getPrimaryKey()
                                    + " "
                                    + " FROM "
                                    + metaclass.getTableName()
                                    + ", flurstueck_schluessel fk"
                                    + " WHERE "
                                    + metaclass.getTableName()
                                    + ".fk_flurstueck_schluessel = fk.id "
                                    + " AND fk.id = "
                                    + key.getId()
                                    + " AND fk.flur is NULL "
                                    + " AND fk.fk_gemarkung is NULL "
                                    + " AND fk.flurstueck_zaehler is NULL "
                                    + " AND fk.flurstueck_nenner  is NULL ");
            }

            if ((mos != null) && (mos.length > 0)) {
                if (mos.length > 1) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Anzahl Flurstuecke: " + mos.length);
                    }
                    throw new Exception("Multiple Flurstuecke should only be one");
                } else {
                    final FlurstueckCustomBean result = (FlurstueckCustomBean)mos[0].getBean();

                    final Collection<VertragCustomBean> vertrage = result.getVertraege();
                    if ((vertrage != null) && (vertrage.size() > 0)) {
                        final Collection<FlurstueckSchluesselCustomBean> resultKeys = getCrossreferencesForVertraege(
                                vertrage);
                        if (resultKeys != null) {
                            resultKeys.remove(result.getFlurstueckSchluessel());
                        }
                        result.setVertraegeQuerverweise(resultKeys);
                    }

                    final Collection<MipaCustomBean> miPas = result.getMiPas();
                    if ((miPas != null) && (miPas.size() > 0)) {
                        final Collection<FlurstueckSchluesselCustomBean> resultKeys = getCrossreferencesForMiPas(miPas);
                        if (resultKeys != null) {
                            resultKeys.remove(result.getFlurstueckSchluessel());
                        }
                        result.setMiPasQuerverweise(resultKeys);
                    }

                    final Collection<BaumCustomBean> baueme = result.getBaeume();
                    if ((baueme != null) && (baueme.size() > 0)) {
                        final Collection<FlurstueckSchluesselCustomBean> resultKeys = getCrossreferencesForBaeume(
                                baueme);
                        if (resultKeys != null) {
                            resultKeys.remove(result.getFlurstueckSchluessel());
                        }
                        result.setBaeumeQuerverweise(resultKeys);
                    }

                    return result;
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim abfragen des Flurstuecks: " + key, ex);
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   currentFlurstueck   DOCUMENT ME!
     * @param   level               allEdges level DOCUMENT ME!
     * @param   levelLimit          DOCUMENT ME!
     * @param   sibblingLevel       DOCUMENT ME!
     * @param   sibblingLevelLimit  DOCUMENT ME!
     * @param   type                DOCUMENT ME!
     * @param   nodeToKeyMapIn      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public String getHistoryGraph(final FlurstueckCustomBean currentFlurstueck,
            final HistoryLevel level,
            final int levelLimit,
            final HistorySibblingLevel sibblingLevel,
            final int sibblingLevelLimit,
            final HistoryType type,
            final HashMap<String, Integer> nodeToKeyMapIn) throws ActionNotSuccessfulException {
        final StringBuilder dotGraphRepresentation = new StringBuilder(DEFAULT_DOT_HEADER);

        try {
            final boolean followPredecessors = HistoryType.BOTH.equals(type) || HistoryType.PREDECESSOR.equals(type);
            final boolean followSuccessors = HistoryType.BOTH.equals(type) || HistoryType.SUCCESSOR.equals(type);

            final int predecessorLevelCount;
            final int successorLevelCount;
            switch (level) {
                case All: {
                    predecessorLevelCount = followPredecessors ? Integer.MIN_VALUE : 1;
                    successorLevelCount = followSuccessors ? Integer.MAX_VALUE : 0;
                }
                break;
                case DIRECT_RELATIONS: {
                    predecessorLevelCount = followPredecessors ? -1 : Integer.MAX_VALUE;
                    successorLevelCount = followSuccessors ? 1 : Integer.MIN_VALUE;
                }
                break;
                case CUSTOM: {
                    predecessorLevelCount = followPredecessors ? -levelLimit : Integer.MAX_VALUE;
                    successorLevelCount = followSuccessors ? levelLimit : Integer.MIN_VALUE;
                }
                break;
                default: {
                    predecessorLevelCount = Integer.MAX_VALUE; // disabled
                    successorLevelCount = Integer.MIN_VALUE;   // disabled
                }
            }

            final int sibblingLevelCount;
            switch (sibblingLevel) {
                case FULL: {
                    sibblingLevelCount = Integer.MAX_VALUE;
                }
                break;
                case SIBBLING_ONLY: {
                    sibblingLevelCount = 0;
                }
                break;
                case CUSTOM: {
                    sibblingLevelCount = sibblingLevelLimit;
                }
                break;
                case NONE:
                default: {
                    sibblingLevelCount = Integer.MIN_VALUE;
                }
            }

            final FlurstueckHistorieGraphSearch search = new FlurstueckHistorieGraphSearch(
                    currentFlurstueck.getId(),
                    predecessorLevelCount,
                    successorLevelCount,
                    sibblingLevelCount);
            final Collection<FlurstueckHistorieGraphSearchResultItem> allEdges = CidsBroker.getInstance()
                        .search(search);
            final HashMap<String, String> pseudoKeys = new HashMap<>();

            final HashMap<String, Integer> nodeToKeyMap = (nodeToKeyMapIn == null) ? new HashMap<String, Integer>()
                                                                                   : nodeToKeyMapIn;

            if ((allEdges != null) && (allEdges.size() > 0)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Historie Graph hat: " + allEdges.size() + " Kanten");
                }
                for (final FlurstueckHistorieGraphSearchResultItem currentEdge : allEdges) {
                    final String currentVorgaenger = currentEdge.getVorgaengerName();
                    final String currentNachfolger = currentEdge.getNachfolgerName();

                    if (currentVorgaenger.startsWith("pseudo")) {
                        pseudoKeys.put(currentVorgaenger, "    ");
                    }
                    if (currentNachfolger.startsWith("pseudo")) {
                        pseudoKeys.put(currentNachfolger, "    ");
                    }
                    dotGraphRepresentation.append("\"")
                            .append(currentVorgaenger)
                            .append("\"->\"")
                            .append(currentNachfolger)
                            .append("\" [lineInterpolate=\"linear\"];\n"); // additional options:
                    // e.g.: basis, linear – Normal line (jagged). step-before – a stepping graph alternating between
                    // vertical and horizontal segments. step-after - a stepping graph alternating between horizontal
                    // and vertical segments. basis - a B-spline, with control point duplication on the ends (that's the
                    // one above). basis-open - an open B-spline; may not intersect the start or end. basis-closed - a
                    // closed B-spline, with the start and the end closed in a loop. bundle - equivalent to basis,
                    // except a separate tension parameter is used to straighten the spline. This could be really cool
                    // with varying tension. cardinal - a Cardinal spline, with control point duplication on the ends.
                    // It looks slightly more 'jagged' than basis. cardinal-open - an open Cardinal spline; may not
                    // intersect the start or end, but will intersect other control points. So kind of shorter than
                    // 'cardinal'. cardinal-closed - a closed Cardinal spline, looped back on itself. monotone - cubic
                    // interpolation that makes the graph only slightly smoother.
                    nodeToKeyMap.put(currentEdge.getVorgaengerName(), currentEdge.getVorgaengerSchluesselId());
                    nodeToKeyMap.put(currentEdge.getNachfolgerName(), currentEdge.getNachfolgerSchluesselId());
                }
                dotGraphRepresentation.append("\"")
                        .append(currentFlurstueck)
                        .append("\"  [style=\"fill: #eee; font-weight: bold\"];\n");
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Historie Graph ist < 1 --> keine Historie");
                }
                dotGraphRepresentation.append("\"")
                        .append(currentFlurstueck)
                        .append("\"  [style=\"fill: #eee; font-weight: bold\"]" + ";\n");
                nodeToKeyMap.put(currentFlurstueck.toString(), currentFlurstueck.getId());
            }

            if (pseudoKeys.size() > 0) {
                for (final String key : pseudoKeys.keySet()) {
                    dotGraphRepresentation.append("\"").append(key).append("\" [label=\"    \"]");
                }
            }
            dotGraphRepresentation.append("}");
        } catch (final Exception ex) {
            throw new ActionNotSuccessfulException("error while searching historie for " + currentFlurstueck, ex);
        }
        return dotGraphRepresentation.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VertragCustomBean> getVertraegeForKey(final FlurstueckSchluesselCustomBean key) {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("vertrag");
        if (metaclass == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + metaclass.getID() + ", "
                    + "   jt_flurstueck_vertrag.fk_vertrag "
                    + "FROM "
                    + "   flurstueck, "
                    + "   jt_flurstueck_vertrag "
                    + "WHERE "
                    + "   jt_flurstueck_vertrag.fk_flurstueck = flurstueck.id "
                    + "   AND flurstueck.fk_flurstueck_schluessel = " + key.getId();

        final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);
        final Collection<VertragCustomBean> beans = new HashSet<VertragCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((VertragCustomBean)metaObject.getBean());
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Anzahl Vertraege ist: " + beans.size());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   vertrag  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossReferencesForVertrag(final VertragCustomBean vertrag) {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("flurstueck_schluessel");
        if (metaclass == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + metaclass.getID() + ", "
                    + "   flurstueck.fk_flurstueck_schluessel "
                    + "FROM "
                    + "   public.flurstueck, "
                    + "   public.jt_flurstueck_vertrag "
                    + "WHERE "
                    + "   public.flurstueck.ar_vertraege = public.jt_flurstueck_vertrag.fk_flurstueck  "
                    + "   AND public.jt_flurstueck_vertrag.fk_vertrag = " + vertrag.getId();

        final MetaObject[] mosVertrag = CidsBroker.getInstance().getLagisMetaObject(query);
        final Collection<FlurstueckSchluesselCustomBean> keys = new HashSet<FlurstueckSchluesselCustomBean>();
        for (final MetaObject metaObject : mosVertrag) {
            keys.add((FlurstueckSchluesselCustomBean)metaObject.getBean());
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Anzahl FlurstueckSchluessel ist: " + keys.size());
        }
        return keys;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   vertraege  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossreferencesForVertraege(
            final Collection<VertragCustomBean> vertraege) {
        if ((vertraege != null) && (vertraege.size() > 0)) {
            final Collection<FlurstueckSchluesselCustomBean> result = new HashSet<FlurstueckSchluesselCustomBean>();
            final Iterator<VertragCustomBean> it = vertraege.iterator();
            while (it.hasNext()) {
                final Collection<FlurstueckSchluesselCustomBean> curKeys = getCrossReferencesForVertrag(it.next());
                if ((curKeys != null) && (curKeys.size() > 0)) {
                    result.addAll(curKeys);
                }
            }
            return result;
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   aktenzeichen  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getFlurstueckSchluesselByAktenzeichen(final String aktenzeichen) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suche nach Flurstücken(Schluesseln) mit dem Aktenzeichen: " + aktenzeichen);
        }

        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("flurstueck_schluessel");
        if (metaclass == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + metaclass.getID() + ", "
                    + "    flurstueck.fk_flurstueck_schluessel "
                    + "FROM "
                    + "    flurstueck, "
                    + "    jt_flurstueck_vertrag, "
                    + "    vertrag "
                    + "WHERE "
                    + "    flurstueck.ar_vertraege = jt_flurstueck_vertrag.fk_flurstueck "
                    + "    AND jt_flurstueck_vertrag.fk_vertrag = vertrag.id "
                    + "    AND vertrag.aktenzeichen LIKE '%" + aktenzeichen + "%'";

        final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);
        final Collection<FlurstueckSchluesselCustomBean> flurstueckSchluessel =
            new HashSet<FlurstueckSchluesselCustomBean>();
        for (final MetaObject metaObject : mos) {
            flurstueckSchluessel.add((FlurstueckSchluesselCustomBean)metaObject.getBean());
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Anzahl Flurststückschlüssel für das Aktenzeichen ist: " + flurstueckSchluessel.size());
        }
        return flurstueckSchluessel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   miPa  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossReferencesForMiPa(final MipaCustomBean miPa) {
        final MetaClass mcFlurstueckSchluessel = CidsBroker.getInstance()
                    .getLagisMetaClass(LagisMetaclassConstants.FLURSTUECK_SCHLUESSEL);
        if (mcFlurstueckSchluessel == null) {
            return null;
        }

        final String query = "SELECT "
                    + "   " + mcFlurstueckSchluessel.getID() + ", "
                    + "   flurstueck.fk_flurstueck_schluessel "
                    + "FROM "
                    + "   public.flurstueck, "
                    + "   public.jt_flurstueck_mipa "
                    + "WHERE "
                    + "   public.flurstueck.ar_mipas = public.jt_flurstueck_mipa.fk_flurstueck  "
                    + "   AND public.jt_flurstueck_mipa.fk_mipa = " + miPa.getId();

        final MetaObject[] mosMipa = CidsBroker.getInstance().getLagisMetaObject(query);
        final Collection<FlurstueckSchluesselCustomBean> keys = new HashSet<FlurstueckSchluesselCustomBean>();
        for (final MetaObject metaObject : mosMipa) {
            keys.add((FlurstueckSchluesselCustomBean)metaObject.getBean());
        }

        if (keys != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl FlurstueckSchluessel ist: " + keys.size());
            }
            return keys;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Flurstückreferenzen für MiPa vorhanden");
            }
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   miPas  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossreferencesForMiPas(
            final Collection<MipaCustomBean> miPas) {
        if ((miPas != null) && (miPas.size() > 0)) {
            final Collection<FlurstueckSchluesselCustomBean> result = new HashSet<FlurstueckSchluesselCustomBean>();
            final Iterator<MipaCustomBean> it = miPas.iterator();
            while (it.hasNext()) {
                final Collection<FlurstueckSchluesselCustomBean> curKeys = getCrossReferencesForMiPa(it.next());
                if ((curKeys != null) && (curKeys.size() > 0)) {
                    result.addAll(curKeys);
                }
            }
            return result;
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<MipaCustomBean> getMiPaForKey(final FlurstueckSchluesselCustomBean key) {
        final MetaClass mcMipa = CidsBroker.getInstance().getLagisMetaClass("mipa");
        if (mcMipa == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + mcMipa.getID() + ", "
                    + "   jt_flurstueck_mipa.fk_mipa "
                    + "FROM "
                    + "   flurstueck, "
                    + "   jt_flurstueck_mipa "
                    + "WHERE "
                    + "   jt_flurstueck_mipa.fk_flurstueck = flurstueck.id "
                    + "   AND flurstueck.fk_flurstueck_schluessel = " + key.getId();

        final MetaObject[] mosMipa = CidsBroker.getInstance().getLagisMetaObject(query);
        final Collection<MipaCustomBean> mipas = new HashSet<MipaCustomBean>();
        for (final MetaObject metaObject : mosMipa) {
            mipas.add((MipaCustomBean)metaObject.getBean());
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Anzahl MiPas ist: " + mipas.size());
        }
        return mipas;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BaumCustomBean> getBaumForKey(final FlurstueckSchluesselCustomBean key) {
        final MetaClass mcBaum = CidsBroker.getInstance().getLagisMetaClass("baum");
        if (mcBaum == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + mcBaum.getID() + ", "
                    + "   jt_flurstueck_baum.fk_baum "
                    + "FROM "
                    + "   flurstueck, "
                    + "   jt_flurstueck_baum "
                    + "WHERE "
                    + "   jt_flurstueck_baum.fk_flurstueck = flurstueck.id "
                    + "   AND flurstueck.fk_flurstueck_schluessel = " + key.getId();

        final MetaObject[] mosBaum = CidsBroker.getInstance().getLagisMetaObject(query);
        final Collection<BaumCustomBean> baeume = new HashSet<BaumCustomBean>();
        for (final MetaObject metaObject : mosBaum) {
            baeume.add((BaumCustomBean)metaObject.getBean());
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Anzahl Baueme ist: " + baeume.size());
        }
        return baeume;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   baum  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossReferencesForBaum(final BaumCustomBean baum) {
        final MetaClass mcFlurstueckSchluessel = CidsBroker.getInstance()
                    .getLagisMetaClass(LagisMetaclassConstants.FLURSTUECK_SCHLUESSEL);
        if (mcFlurstueckSchluessel == null) {
            return null;
        }

        final String query = "SELECT "
                    + "   " + mcFlurstueckSchluessel.getID() + ", "
                    + "   flurstueck.fk_flurstueck_schluessel "
                    + "FROM "
                    + "   public.flurstueck, "
                    + "   public.jt_flurstueck_baum "
                    + "WHERE "
                    + "   public.flurstueck.ar_baeume = public.jt_flurstueck_baum.fk_flurstueck  "
                    + "   AND public.jt_flurstueck_baum.fk_baum = " + baum.getId();

        final MetaObject[] mosBaum = CidsBroker.getInstance().getLagisMetaObject(query);
        final Collection<FlurstueckSchluesselCustomBean> keys = new HashSet<FlurstueckSchluesselCustomBean>();
        for (final MetaObject metaObject : mosBaum) {
            keys.add((FlurstueckSchluesselCustomBean)metaObject.getBean());
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Anzahl FlurstueckSchluessel ist: " + keys.size());
        }
        return keys;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   baeume  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossreferencesForBaeume(
            final Collection<BaumCustomBean> baeume) {
        if ((baeume != null) && (baeume.size() > 0)) {
            final Collection<FlurstueckSchluesselCustomBean> result = new HashSet<FlurstueckSchluesselCustomBean>();
            final Iterator<BaumCustomBean> it = baeume.iterator();
            while (it.hasNext()) {
                final Collection<FlurstueckSchluesselCustomBean> curKeys = getCrossReferencesForBaum(it.next());
                if ((curKeys != null) && (curKeys.size() > 0)) {
                    result.addAll(curKeys);
                }
            }
            return result;
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void setFlurstueckHistoric(final FlurstueckSchluesselCustomBean key) throws ActionNotSuccessfulException {
        key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        key.setLetzte_bearbeitung(getCurrentDate());
        setFlurstueckHistoric(key, new Date());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key   DOCUMENT ME!
     * @param   date  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void setFlurstueckHistoric(final FlurstueckSchluesselCustomBean key, final Date date)
            throws ActionNotSuccessfulException {
        setFlurstueckHistoric(key, date, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key          DOCUMENT ME!
     * @param   date         DOCUMENT ME!
     * @param   delRebeMipa  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void setFlurstueckHistoric(final FlurstueckSchluesselCustomBean key,
            final Date date,
            final boolean delRebeMipa) throws ActionNotSuccessfulException {
        key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        key.setLetzte_bearbeitung(getCurrentDate());

        try {
            if (key.getWarStaedtisch()) {
                final FlurstueckCustomBean flurstueck = retrieveFlurstueck(key);
                flurstueck.setFlurstueckSchluessel(key);

                if (delRebeMipa) {
//                    final Collection<FlurstueckHistorieCustomBean> historieSucessor = CidsBroker.getInstance()
//                                .getHistorySuccessor(key);
//                    boolean hasNachfolger = false;
//                    if (historieSucessor != null) {
//                        for (final FlurstueckHistorieCustomBean historie : historieSucessor) {
//                            if ((historie != null) && (historie.getNachfolger() != null)) {
//                                hasNachfolger = true;
//                                break;
//                            }
//                        }
//                    }
                    final List<RebeCustomBean> rechteUndBelastungen = LagisBroker.getInstance().getCurrentRebes();
                    final boolean hasReBe = !rechteUndBelastungen.isEmpty();
                    final boolean hasMiPa = !flurstueck.getMiPas().isEmpty();

                    final Date rebeLoeschDatum;
                    final Date mipaVertragsendeDatum;
                    if ((hasReBe || hasMiPa) /* && !hasNachfolger*/) {
                        HistoricNoSucessorDialog.getInstance().setHistorischDatum(date);
                        StaticSwingTools.showDialog(HistoricNoSucessorDialog.getInstance());
                        if (HistoricNoSucessorDialog.getInstance().isAbort()) {
                            throw new ActionNotSuccessfulException("Die Aktion wurde vom Benutzer abgebrochen.");
                        }
                        rebeLoeschDatum = HistoricNoSucessorDialog.getInstance().getRebeLoeschDatum();
                        mipaVertragsendeDatum = HistoricNoSucessorDialog.getInstance().getMipaVertragsendeDatum();
                    } else {
                        rebeLoeschDatum = null;
                        mipaVertragsendeDatum = null;
                    }

                    if (rebeLoeschDatum != null) {
                        for (final MipaCustomBean mipa : flurstueck.getMiPas()) {
                            mipa.setVertragsende(mipaVertragsendeDatum);
                        }
                        for (final RebeCustomBean rebe : rechteUndBelastungen) {
                            rebe.setDatumLoeschung(rebeLoeschDatum);
                        }
                    }
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Flurstueck war schon mal staedtisch wird historisch gesetzt");
                }
                // if (flurstueck.getGueltigBis() == null) {
                if (key.getGueltigBis() == null) {
                    if (
                        !FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                                    key.getFlurstueckArt().getBezeichnung())) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Flurstueck ist nicht städtisch");
                        }
                        FlurstueckArtCustomBean abteilungIX = null;
                        for (final FlurstueckArtCustomBean current : getAllFlurstueckArten()) {
                            if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX.equals(
                                            current.getBezeichnung())) {
                                abteilungIX = current;
                            }
                        }
                        if (abteilungIX == null) {
                            throw new ActionNotSuccessfulException(
                                "Flurstücksart AbteilungIX konnte nicht gefunden werden.");
                        }

                        if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX.equals(
                                        key.getFlurstueckArt().getBezeichnung())) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Flurstück ist Abteilung IX  --> alle Rechte werden entfernt");
                            }
                            flurstueck.getFlurstueckSchluessel().setFlurstueckArt(abteilungIX);
                            if (flurstueck.getFlurstueckSchluessel().getDatumLetzterStadtbesitz() != null) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Setze Gueltigbis Datum des Flurstueks auf letzten Stadtbesitz");
                                }
                                flurstueck.getFlurstueckSchluessel()
                                        .setGueltigBis(flurstueck.getFlurstueckSchluessel()
                                            .getDatumLetzterStadtbesitz());
                            } else {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Achtung war schon in Stadtbesitz hat aber kein Datum");
                                }
                                throw new ActionNotSuccessfulException(
                                    "Das Flurstück war schon mal in Stadtbesitz, aber es existiert kein Datum wann");
                            }
                            flurstueck.persist();
                        } else {
                            throw new ActionNotSuccessfulException("Die Flurstückart "
                                        + FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH
                                        + " ist nicht in der Datenbank");
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Flurstück ist städtisch und wird historisch gesetzt");
                        }
                        key.setDatumLetzterStadtbesitz(date);
                        key.setGueltigBis(date);
                        flurstueck.persist();
                    }
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Flurstueck war noch nie staedtisch wird historisch gesetzt");
                }
                key.setGueltigBis(date);
                key.persist();
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim historisch setzen eines Flurstücks", ex);
            if (ex instanceof ActionNotSuccessfulException) {
                throw (ActionNotSuccessfulException)ex;
            } else {
                throw new ActionNotSuccessfulException(
                    "Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator.",
                    ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueckSchluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public boolean hasFlurstueckSucccessors(final FlurstueckSchluesselCustomBean flurstueckSchluessel)
            throws ActionNotSuccessfulException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suche Nachfolger für Flurstück");
            LOG.debug("ID des Schluessels ist: " + flurstueckSchluessel.getId());
        }

        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("flurstueck_historie");
        if (metaclass == null) {
            return false;
        }
        final String query = "SELECT "
                    + "   " + metaclass.getID() + ", "
                    + "   " + metaclass.getTableName() + "." + metaclass.getPrimaryKey() + " "
                    + "FROM "
                    + "   " + metaclass.getTableName() + ", "
                    + "   flurstueck "
                    + "WHERE "
                    + "   " + metaclass.getTableName() + ".fk_vorgaenger = flurstueck.id "
                    + "   AND flurstueck.fk_flurstueck_schluessel = " + flurstueckSchluessel.getId();

        final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);
        if (mos != null) {
            if (mos.length == 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ergebnisliste ist leer");
                }
                return false;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Suche lieferte mindestens ein Ergebnis zurück");
                }
                return true;
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Suche lieferte kein Ergebnis zurück");
            }
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public boolean setFlurstueckActive(final FlurstueckSchluesselCustomBean key) throws ActionNotSuccessfulException {
        key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        key.setLetzte_bearbeitung(getCurrentDate());

        try {
            if (key.getGueltigBis() != null) {
                if (!hasFlurstueckSucccessors(key)) {
                    if ((key.getFlurstueckArt() == null) || (key.getFlurstueckArt().getBezeichnung() == null)) {
                        throw new ActionNotSuccessfulException(
                            "Das Flurstück kann nicht aktiviert werden, weil es keine Flurstücksart besitzt");
                    }

                    if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                                    key.getFlurstueckArt().getBezeichnung())) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Städtisches Flurstück wurde reactiviert");
                        }
                        final Date currentDate = new Date();
                        key.setEntstehungsDatum(currentDate);
                        key.setDatumLetzterStadtbesitz(currentDate);
                    }

                    key.setGueltigBis(null);
                    key.persist();
                    return true;
                } else {
                    throw new ActionNotSuccessfulException(
                        "Das Flurstück kann nicht aktiviert werden, weil es Nachfolger hat");
                }
            } else {
                throw new ActionNotSuccessfulException("Das Flurstück war aktiv");
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim historisch setzen eines Flurstücks", ex);
            throw new ActionNotSuccessfulException(
                "Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator.",
                ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key       DOCUMENT ME!
     * @param   username  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void bookNutzungenForFlurstueck(final FlurstueckSchluesselCustomBean key, final String username)
            throws ActionNotSuccessfulException {
        key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        key.setLetzte_bearbeitung(getCurrentDate());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<AnlageklasseCustomBean> getAllAnlageklassen() {
        return (Collection<AnlageklasseCustomBean>)getAllOf("anlageklasse");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VertragsartCustomBean> getAllVertragsarten() {
        return (Collection<VertragsartCustomBean>)getAllOf("vertragsart");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<MipaKategorieCustomBean> getAllMiPaKategorien() {
        return (Collection<MipaKategorieCustomBean>)getAllOf("mipa_kategorie");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BaumKategorieCustomBean> getAllBaumKategorien() {
        return (Collection<BaumKategorieCustomBean>)getAllOf("baum_kategorie");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VerwaltendeDienststelleCustomBean> getAllVerwaltendeDienstellen() {
        return (Collection<VerwaltendeDienststelleCustomBean>)getAllOf("verwaltende_dienststelle");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<ZusatzRolleArtCustomBean> getAllZusatzRolleArten() {
        return (Collection<ZusatzRolleArtCustomBean>)getAllOf("zusatz_rolle_art");
    }

    /**
     * DOCUMENT ME!
     *
     * @param   metaClassName  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection getAllOf(final String metaClassName) {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass(metaClassName);
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsBroker.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<CidsBean> beans = new HashSet<>();
        for (final MetaObject metaObject : mos) {
            beans.add(metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<RebeArtCustomBean> getAllRebeArten() {
        return (Collection<RebeArtCustomBean>)getAllOf("rebe_art");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<NutzungsartCustomBean> getAllNutzungsarten() {
        return (Collection<NutzungsartCustomBean>)getAllOf("nutzungsart");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BeschlussartCustomBean> getAllBeschlussarten() {
        return (Collection<BeschlussartCustomBean>)getAllOf("beschlussart");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<KostenartCustomBean> getAllKostenarten() {
        return (Collection<KostenartCustomBean>)getAllOf("kostenart");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<MipaMerkmalCustomBean> getAllMiPaMerkmale() {
        return (Collection<MipaMerkmalCustomBean>)getAllOf("mipa_merkmal");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BaumMerkmalCustomBean> getAllBaumMerkmale() {
        return (Collection<BaumMerkmalCustomBean>)getAllOf("baum_merkmal");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckArtCustomBean> getAllFlurstueckArten() {
        return (Collection<FlurstueckArtCustomBean>)getAllOf("flurstueck_art");
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueckSchluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckSchluesselCustomBean completeFlurstueckSchluessel(
            final FlurstueckSchluesselCustomBean flurstueckSchluessel) {
        return FlurstueckSchluesselCustomBean.createNewByFsKey(flurstueckSchluessel);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckCustomBean createFlurstueck(final FlurstueckSchluesselCustomBean key) {
        key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        key.setLetzte_bearbeitung(getCurrentDate());
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("createFlurstueck: key ist != null");
            }

            final FlurstueckSchluesselCustomBean checkedKey = this.completeFlurstueckSchluessel(key);
            if (checkedKey != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("createFlurstueck: Vervollständigter key ist == null");
                }
                return null;
            }

//                final Integer keyId = key.getId();
//
//                if ((keyId == null) || (keyId == -1)) {
//                    if (LOG.isDebugEnabled()) {
//                        LOG.debug("createFlurstueck: Vervollständigter key ist != null");
//                    }
//                    return null;
//                }
//                }
//                else {
//                    checkedKey = FlurstueckSchluesselCustomBean.createNewById(key.getId());
//                }
            final FlurstueckCustomBean newFlurstueck = FlurstueckCustomBean.createNew();
            // datamodell refactoring 22.10.07
            final Date datumEntstehung = new Date();
            key.setEntstehungsDatum(datumEntstehung);
            key.setIstGesperrt(false);
            newFlurstueck.setFlurstueckSchluessel(key);
            // newFlurstueck.setEntstehungsDatum(new Date());
            // newFlurstueck.setIstGesperrt(false);
            checkIfFlurstueckWasStaedtisch(key, datumEntstehung);
            newFlurstueck.persist();
            if (LOG.isDebugEnabled()) {
                // edit(newFlurstueck);
                LOG.debug("createFlurstueck: neues Flurstück erzeugt");
            }
            return retrieveFlurstueck(key);
        } catch (Exception ex) {
            LOG.error("Fehler beim anlegen des Flurstücks", ex);
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isFlurstueckHistoric(final FlurstueckSchluesselCustomBean key) {
        // Flurstueck flurstueck = retrieveFlurstueck(key);
        // if(flurstueck.getGueltigBis() != null){
        if (key.getGueltigBis() != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueckSchluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckHistorieCustomBean> getHistorySuccessor(
            final FlurstueckSchluesselCustomBean flurstueckSchluessel) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suche Nachfolger für Flurstück");
        }

        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("flurstueck_historie");
        if (metaclass == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + metaclass.getID() + ", "
                    + "   " + metaclass.getTableName() + "." + metaclass.getPrimaryKey() + " "
                    + "FROM "
                    + "   " + metaclass.getTableName() + ", "
                    + "   flurstueck "
                    + "WHERE "
                    + "   " + metaclass.getTableName() + ".fk_vorgaenger = flurstueck.id "
                    + "   AND flurstueck.fk_flurstueck_schluessel = " + flurstueckSchluessel.getId();

        final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);
        final Collection<FlurstueckHistorieCustomBean> historyEntries = new HashSet<>();
        for (final MetaObject metaObject : mos) {
            historyEntries.add((FlurstueckHistorieCustomBean)metaObject.getBean());
        }

        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("ID des Schluessels ist: " + flurstueckSchluessel);
            }
            if (historyEntries != null) {
                if (historyEntries.isEmpty()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Ergebnisliste ist leer");
                    }
                    return null;
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Suche lieferte mindestens ein Ergebnis zurück");
                    }
                }

//                while(it.hasNext()){
//                    FlurstueckHistorieCustomBean curHistoryEntry = it.next();
//                    //TODO possible that a key is null (inconsitence) ??
//                    if(curHistoryEntry != null && curHistoryEntry.getVorgaenger() != null){
//                        System.out.println("Jetziger HistoryEintrag != null und Vorgänger != null");
//                        result.add(curHistoryEntry.getVorgaenger().getFlurstueckSchluessel());
//                    } else {
//                        //TODO EXCEPTION
//                        System.out.println("Jetziger HistoryEintrag oder Vorgänger == null");
//                    }
//                }
                return historyEntries;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Suche lieferte kein Ergebnis zurück");
                }
                return null;
            }
        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Fehler beim suchen der Nachfolger eines Flurstücks", ex);
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key      DOCUMENT ME!
     * @param  useDate  DOCUMENT ME!
     */
    private void checkIfFlurstueckWasStaedtisch(final FlurstueckSchluesselCustomBean key, final Date useDate) {
        final FlurstueckArtCustomBean art = key.getFlurstueckArt();
        if (!key.getWarStaedtisch()) {
            // for(FlurstueckArtCustomBean current:getAllFlurstueckArten()){
            // TODO Checken ob korrekt mit Dirk absprechen
            if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(art.getBezeichnung())) {
                if (LOG.isDebugEnabled()) {
                    // if(FlurstueckArtCustomBean.FLURSTUECK_ART_EQUALATOR.pedanticEquals(art,current)){
                    LOG.debug("Flurstück ist Städtisch Datum letzter Stadtbesitz wird geupdated");
                }
                key.setWarStaedtisch(true);
                if (useDate != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Flurstück wurde neu angelegt und ist städtisch");
                    }
                    key.setDatumLetzterStadtbesitz(useDate);
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Flurstück war noch nie in Stadtbesitz und wird jetzt hinzugefügt");
                    }
                    final Date currentDate = new Date();
                    key.setDatumLetzterStadtbesitz(currentDate);
                    key.setEntstehungsDatum(currentDate);
                }
            }
            // }
        } else {
            if ((key.getFlurstueckArt() != null) && (key.getFlurstueckArt().getBezeichnung() != null)
                        && FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                            key.getFlurstueckArt().getBezeichnung())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Flurstück war und ist Städtisch --> Datum wird geupdated");
                }
                final Date currentDate = new Date();
                if (useDate != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Dieser Fall sollte nicht vorkommen");
                    }
                    key.setDatumLetzterStadtbesitz(useDate);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Datum letzter Stadt_besitz geupdated");
                    }
                } else {
                    key.setDatumLetzterStadtbesitz(currentDate);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Datum letzter Stadt_besitz geupdated");
                    }
                }
//TODO wird im Moment nur die Entstehung und gueltig_bis vom aktuellen Flurstück gespeichert

                final FlurstueckSchluesselCustomBean oldSchluessel = (FlurstueckSchluesselCustomBean)CidsBroker
                            .getInstance()
                            .getLagisMetaObject(key.getId(),
                                    CidsBroker.getInstance().getLagisMetaClass(
                                        LagisMetaclassConstants.FLURSTUECK_SCHLUESSEL).getId())
                            .getBean();

                if ((oldSchluessel != null) && (oldSchluessel.getFlurstueckArt() != null)
                            && (oldSchluessel.getFlurstueckArt().getBezeichnung() != null)
                            && !FlurstueckArtCustomBean.FLURSTUECK_ART_EQUALATOR.pedanticEquals(
                                oldSchluessel.getFlurstueckArt(),
                                key.getFlurstueckArt())
                            && FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                                key.getFlurstueckArt().getBezeichnung())) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Flurstück kommt erneut in den Stadtbesitz --> entstehungsDatum wird geupdated");
                    }
                    if (useDate != null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("sollte nicht vorkkommen");
                        }
                        key.setEntstehungsDatum(useDate);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Datum Entstehung geupdated");
                        }
                    } else {
                        key.setEntstehungsDatum(currentDate);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Datum Entstehung geupdated");
                        }
                    }
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Kein wechsel von irgendeiner Flurstücksart nach städtisch --> kein Update");
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueckToCheck  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean existHistoryEntry(final FlurstueckCustomBean flurstueckToCheck) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suche History Einträge");
        }
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("flurstueck_historie");
        if (metaclass == null) {
            return false;
        }
        final String query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                    + metaclass.getPrimaryKey() + " "
                    + "FROM " + metaclass.getTableName() + " "
                    + "WHERE " + metaclass.getTableName() + ".fk_vorgaenger = " + flurstueckToCheck.getId();

        final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);
        if ((mos != null) && (mos.length > 0)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Es existiert ein History Eintrag");
                LOG.debug("Es gibt schon einen Nachfolger");
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks in which way the Nutzungen have changed and reacts according to that. At the moment only the states
     * NUTZUNG_CHANGED and NUTZUNG_TERMINATED require further treatment.
     *
     * @param   nutzungen      DOCUMENT ME!
     * @param   flurstueckKey  not used at them moment
     *
     * @throws  ErrorInNutzungProcessingException  DOCUMENT ME!
     *
     * @see     processNutzungen_old()
     */
    private void processNutzungen(final Collection<NutzungCustomBean> nutzungen, final String flurstueckKey)
            throws ErrorInNutzungProcessingException {
        try {
            if ((nutzungen != null) && (nutzungen.size() > 0)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Anzahl Ketten in aktuellem Flurstück: " + nutzungen.size());
                }
                final Date bookingDate = new Date();
                for (final NutzungCustomBean curNutzung : nutzungen) {
                    final Collection<NutzungCustomBean.NUTZUNG_STATES> nutzungsState = curNutzung.getNutzungsState();
                    if (nutzungsState.isEmpty()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Keine Änderung");
                        }
                    } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.NUTZUNG_CREATED)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Neue Nutzung angelegt.");
                        }
                        curNutzung.getBuchwert().setGueltigvon(bookingDate);
                    } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.NUTZUNG_CHANGED)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Nutzungskette wurde modifiziert "
                                        + Arrays.deepToString(nutzungsState.toArray()));
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Setzte Datum für die letzten beiden Buchungen");
                        }
                        curNutzung.getOpenBuchung().setGueltigvon(bookingDate);
                        curNutzung.getPreviousBuchung().setGueltigbis(bookingDate);
                        if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.NUTZUNGSART_CHANGED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Nutzungsart wurde geändert.");
                            }
                        }
                        if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.STILLE_RESERVE_CREATED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Stille Reserve wurde gebildet.");
                            }
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.STILLE_RESERVE_INCREASED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Vorhandene Stille Reserve wurde erhöht.");
                            }
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.STILLE_RESERVE_DECREASED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Vorhandene Stille Reserve wurde vermindert.");
                            }
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.STILLE_RESERVE_DISOLVED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                    "Vorhandene Stille Reserve wurde vollständig aufgebraucht.");
                            }
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.POSITIVE_BUCHUNG)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Positive Buchung ohne Stille Reserve.");
                            }
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.NEGATIVE_BUCHUNG)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Negative Buchung ohne Stille Reserve.");
                            }
                        }
                    } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.NUTZUNG_TERMINATED)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Nutzungskette wurde terminiert, setze Buchungsdatum");
                        }
                        // ToDo Nachricht an Zuständige ?? gab es bisher
                        // curNutzung.terminateNutzung(bookingDate);
                        curNutzung.getTerminalBuchung().setGueltigbis(bookingDate);
                        // ToDo letzter Wert zum Buchwert setzen ?
                    } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.BUCHUNG_CREATED)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("neue Buchung . Nachricht an Zuständige");
                        }
                    } else {
                        throw new Exception("Kein Fall trifft auf Stati zu: "
                                    + Arrays.toString(nutzungsState.toArray()));
                    }
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Flurstück besitzt keine Nutzungen.");
                }
            }
        } catch (Exception ex) {
            throw new ErrorInNutzungProcessingException("Nutzungen konnten nicht verarbeitet werden", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  oldFlurstueck  DOCUMENT ME!
     * @param  newFlurstueck  DOCUMENT ME!
     */
    private void createHistoryEdge(final FlurstueckCustomBean oldFlurstueck, final FlurstueckCustomBean newFlurstueck) {
        final FlurstueckHistorieCustomBean historyEntry = FlurstueckHistorieCustomBean.createNew();
        historyEntry.setVorgaenger(oldFlurstueck);
        historyEntry.setNachfolger(newFlurstueck);
        createFlurstueckHistoryEntry(historyEntry);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckHistorieCustomBean> getAllHistoryEntries(
            final FlurstueckSchluesselCustomBean schluessel) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Sammle Alle Knoten (Rekursiv) für: " + schluessel);
        }
        final Collection<FlurstueckHistorieCustomBean> allEdges = new HashSet<>();
        try {
            Collection<FlurstueckHistorieCustomBean> childEdges = getHistoryPredecessors(schluessel);
            if (childEdges != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es gibt Kanten zu diesem Knoten");
                }
                allEdges.addAll(childEdges);
                final Iterator<FlurstueckHistorieCustomBean> it = childEdges.iterator();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Rufe Methode Rekursiv auf für alle Gefundenen Knoten");
                }
                while (it.hasNext()) {
                    childEdges = getAllHistoryEntries(it.next().getVorgaenger().getFlurstueckSchluessel());
                    if (childEdges != null) {
                        allEdges.addAll(childEdges);
                    }
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es gibt keine Kanten zu diesem Knoten");
                }
                return allEdges;
            }

            return allEdges;
        } catch (Exception ex) {
            LOG.error("Fehler beim sammeln aller Kanten", ex);
        }

        return allEdges;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   joinMembers              DOCUMENT ME!
     * @param   newFlurstueckSchluessel  DOCUMENT ME!
     * @param   benutzerkonto            DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public FlurstueckCustomBean joinFlurstuecke(final ArrayList<FlurstueckSchluesselCustomBean> joinMembers,
            final FlurstueckSchluesselCustomBean newFlurstueckSchluessel,
            final String benutzerkonto) throws ActionNotSuccessfulException {
        for (final FlurstueckSchluesselCustomBean key : joinMembers) {
            key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
            key.setLetzte_bearbeitung(getCurrentDate());
        }
        newFlurstueckSchluessel.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        newFlurstueckSchluessel.setLetzte_bearbeitung(getCurrentDate());
        final ArrayList<CidsBean> locks = new ArrayList<>();
        try {
            if (joinMembers != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es sind joinMember vorhanden.");
                }
                Iterator<FlurstueckSchluesselCustomBean> it = joinMembers.iterator();
                while (it.hasNext()) {
                    final FlurstueckSchluesselCustomBean currentKey = it.next();
                    CidsBean tmpLock;

                    if ((tmpLock = isLocked(currentKey)) == null) {
                        tmpLock = createNewFlurstueckSchluesselLock(currentKey);
                        if (tmpLock == null) {
                            if (LOG.isDebugEnabled()) {
                                // TODO throw new EJBException(new ActionNotSuccessfulException("Anlegen einer
                                // SperreCustomBean nicht möglich"));
                                LOG.debug("Anlegen einer Sperre für das Flurstück nicht möglich "
                                            + currentKey.getKeyString() + ".");
                            }
                            releaseLocks(locks);
                            throw new ActionNotSuccessfulException("Anlegen einer Sperre für das Flurstück "
                                        + currentKey.getKeyString() + " nicht möglich.");
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Sperre für Flurstück " + currentKey.getKeyString()
                                            + " Erfolgreich angelegt.");
                            }
                            locks.add(tmpLock);
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            // TODO throw new EJBException(new ActionNotSuccessfulException("Es exisitert bereits eine
                            // SperreCustomBean"));
                            LOG.debug("Es exisitert bereits eine Sperre für das Flurstück " + currentKey.getKeyString()
                                        + " und wird von dem Benutzer " + (String)tmpLock.getProperty("user_string")
                                        + " gehalten.");
                        }
                        releaseLocks(locks);
                        throw new ActionNotSuccessfulException("Es exisitert bereits eine Sperre für das Flurstück "
                                    + currentKey.getKeyString() + " und wird von dem Benutzer "
                                    + (String)tmpLock.getProperty("user_string") + " gehalten.");
                    }
                }
                it = joinMembers.iterator();
                final FlurstueckCustomBean newFlurstueck = createFlurstueck(newFlurstueckSchluessel);
                if (newFlurstueck != null) {
                    while (it.hasNext()) {
                        final FlurstueckCustomBean oldFlurstueck = retrieveFlurstueck(it.next());
                        // HistoricResult result = setFlurstueckHistoric(oldFlurstueck.getFlurstueckSchluessel());
                        setFlurstueckHistoric(oldFlurstueck.getFlurstueckSchluessel());
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Flurstück wurde Historisch gesetzt.");
                        }
                        // TODO IS THIS CASE POSSIBLE ?? --> MEANS ACTIVE FLURSTUECK
                        if (!existHistoryEntry(oldFlurstueck)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                    "Es exitieren kein History Eintrag --> keine Kante zu einem anderen Flurstück.");
                                LOG.debug("Erzeuge History Eintrag für alte Flurstücke.");
                            }
                            createHistoryEdge(oldFlurstueck, newFlurstueck);
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Neuer History Eintrag für Flurstück erzeugt.");
                            }
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Es sind bereits Historieneinträge für das Flurstück "
                                            + oldFlurstueck.getFlurstueckSchluessel().getKeyString()
                                            + " vorhanden.");
                            }
                            releaseLocks(locks);
                            throw new ActionNotSuccessfulException(
                                "Es sind bereits Historieneinträge für das Flurstück "
                                        + oldFlurstueck.getFlurstueckSchluessel().getKeyString()
                                        + " vorhanden.");
                        }
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Flurstücke Erfolgreich gejoined");
                    }
                    releaseLocks(locks);
                    return newFlurstueck;
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Das Anlegen des neuen Flurstücks " + newFlurstueckSchluessel.getKeyString()
                                    + " schlug fehl.");
                    }
                    releaseLocks(locks);
                    throw new ActionNotSuccessfulException("Das Anlegen des neuen Flurstücks "
                                + newFlurstueckSchluessel.getKeyString() + " schlug fehl.");
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es wurden keine Flurstücke angeben für die Zusammenlegung.");
                }
                throw new ActionNotSuccessfulException("Es wurden keine Flurstücke angeben für die Zusammenlegung.");
            }
        } catch (final ActionNotSuccessfulException ex) {
            throw ex;
        } catch (final Exception ex) {
            LOG.error("Unbekannter Fehler beim joinen von Flurstücken.", ex);
            releaseLocks(locks);
            throw new ActionNotSuccessfulException(
                "Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator.",
                ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   oldFlurstueckSchluessel  DOCUMENT ME!
     * @param   splitMembers             DOCUMENT ME!
     * @param   benutzerkonto            DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void splitFlurstuecke(final FlurstueckSchluesselCustomBean oldFlurstueckSchluessel,
            final ArrayList<FlurstueckSchluesselCustomBean> splitMembers,
            final String benutzerkonto) throws ActionNotSuccessfulException {
        for (final FlurstueckSchluesselCustomBean key : splitMembers) {
            key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
            key.setLetzte_bearbeitung(getCurrentDate());
        }
        oldFlurstueckSchluessel.setLetzter_bearbeiter(benutzerkonto);
        oldFlurstueckSchluessel.setLetzte_bearbeitung(getCurrentDate());
        CidsBean lock = null;
        try {
            final ArrayList<CidsBean> locks = new ArrayList<>();
            if (splitMembers != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es sind Flurstücke zum splitten vorhanden");
                }

                if (isLocked(oldFlurstueckSchluessel) == null) {
                    lock = createNewFlurstueckSchluesselLock(oldFlurstueckSchluessel);
                    if (lock == null) {
                        // TODO throw new EJBException(new ActionNotSuccessfulException("Anlegen einer SperreCustomBean
                        // nicht möglich"));
                        throw new ActionNotSuccessfulException(
                            "Anlegen einer Sperre für das alte Flurstück nicht möglich");
                    }
                } else {
                    // TODO throw new EJBException(new ActionNotSuccessfulException("Es exisitert bereits eine
                    // SperreCustomBean"));
                    throw new ActionNotSuccessfulException(
                        "Es exisitert bereits eine Sperre für das alte Flurstück, das gesplittet werden soll");
                }
                final Iterator<FlurstueckSchluesselCustomBean> it = splitMembers.iterator();
                final FlurstueckCustomBean oldFlurstueck = retrieveFlurstueck(oldFlurstueckSchluessel);
                // HistoricResult result = setFlurstueckHistoric(oldFlurstueck.getFlurstueckSchluessel());
                setFlurstueckHistoric(oldFlurstueck.getFlurstueckSchluessel());
                if (!existHistoryEntry(oldFlurstueck)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Es exitieren kein History Eintrag --> keine Kante zu einem anderen Flurstück");
                    }
                } else {
                    releaseLock(lock);
                    throw new ActionNotSuccessfulException(
                        "Spliten des Flurstücks nicht möglich, es gibt schon einen Nachfolger");
                }

                while (it.hasNext()) {
                    final FlurstueckCustomBean newFlurstueck = createFlurstueck(it.next());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Neus Flurstück aus Split erzeugt");
                    }
                    if (newFlurstueck != null) {
                        createHistoryEdge(oldFlurstueck, newFlurstueck);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Neuer History Eintrag für Flurstück erzeugt");
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Fehler beim anlegen eines Flurstücks");
                        }
                        releaseLock(lock);
                        return;
                    }
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Splitten der Flurstücke erforgreich");
                }
                releaseLock(lock);
                return;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("split der Flurstücke nicht erfolgreich");
            }
            releaseLock(lock);
        } catch (Exception ex) {
            LOG.error("Fehler beim splitten von Flurstücken", ex);
            if (ex instanceof ActionNotSuccessfulException) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Eine Aktion ging schief Exception wird weitergereicht");
                }
                releaseLock(lock);
                throw (ActionNotSuccessfulException)ex;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Unbekannte Excepiton");
                }
                releaseLock(lock);
                throw new ActionNotSuccessfulException(
                    "Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator",
                    ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   joinMembers    DOCUMENT ME!
     * @param   splitMembers   DOCUMENT ME!
     * @param   benutzerkonto  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void joinSplitFlurstuecke(final ArrayList<FlurstueckSchluesselCustomBean> joinMembers,
            final ArrayList<FlurstueckSchluesselCustomBean> splitMembers,
            final String benutzerkonto) throws ActionNotSuccessfulException {
        for (final FlurstueckSchluesselCustomBean key : joinMembers) {
            key.setLetzter_bearbeiter(benutzerkonto);
            key.setLetzte_bearbeitung(getCurrentDate());
        }
        for (final FlurstueckSchluesselCustomBean key : splitMembers) {
            key.setLetzter_bearbeiter(benutzerkonto);
            key.setLetzte_bearbeitung(getCurrentDate());
        }
        // TODO ROLLBACK IF ONE OF THE METHODS FAILS
        try {
            FlurstueckSchluesselCustomBean dummySchluessel = FlurstueckSchluesselCustomBean.createNew();
            // dummySchluessel.setWarStaedtisch(true);
            // UGLY minimum Konstante aus der jeweiligen Klasse benutzen
            for (final FlurstueckArtCustomBean current : getAllFlurstueckArten()) {
                if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_PSEUDO.equals(current.getBezeichnung())) {
                    dummySchluessel.setFlurstueckArt(current);
                    break;
                }
            }

            dummySchluessel = (FlurstueckSchluesselCustomBean)dummySchluessel.persist();
//            createFlurstueckSchluessel(dummySchluessel);

            joinFlurstuecke(joinMembers, dummySchluessel, benutzerkonto);
            // TODO problem first have to check all keys
            splitFlurstuecke(dummySchluessel, splitMembers, benutzerkonto);
        } catch (final Exception ex) {
            if (ex instanceof ActionNotSuccessfulException) {
                LOG.error("Eine ActionSchlug fehl", ex);
                throw (ActionNotSuccessfulException)ex;
            }
            LOG.error("Fehler beim joinSplit", ex);
            throw new ActionNotSuccessfulException(
                "Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator",
                ex);
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
            LOG.info("reloadFlurstueck");
            resetWidgets();
            loadFlurstueck(currentFlurstueck.getFlurstueckSchluessel());
        } else {
            LOG.info("can't reload flurstueck == null");
        }
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void reloadFlurstueckKeys() {
        LOG.info("updateFlurstueckKeys");
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
        LOG.info("loadFlurstueck");
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
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Daten von: " + curWidget.getWidgetName() + " werden gespeichert");
                        }
                        ((FlurstueckSaver)curWidget).updateFlurstueckForSaving(currentFlurstueck);
                    }
                }
                // TODO check if flurstück is changed at all
                try {
                    final FlurstueckCustomBean origFlurstueck = retrieveFlurstueck(
                            currentFlurstueck.getFlurstueckSchluessel());

                    // Checks the Dienstellen for changes
                    final Collection<VerwaltungsbereichCustomBean> oldBereiche =
                        origFlurstueck.getVerwaltungsbereiche();
                    final Collection<VerwaltungsbereichCustomBean> newBereiche =
                        currentFlurstueck.getVerwaltungsbereiche();
                    if (((oldBereiche == null) || (oldBereiche.size() == 0))
                                && ((newBereiche == null) || (newBereiche.size() == 0))) {
                        LOG.info("Es existieren keine Verwaltungsbereiche --> keine Veränderung");
                    } else if (((oldBereiche == null) || (oldBereiche.size() == 0))) {
                        LOG.info("Es wurden nur neue Verwaltungsbereiche angelegt: " + newBereiche.size());
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
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("neuer Verwaltungsbereich angelegt ohne Dienstellenzuordnung");
                                    }
                                }
                            } catch (Exception ex) {
                                LOG.error("Fehler beim prüfen eines neuen Verwaltungsbereichs", ex);
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
                        LOG.info("Es wurden alle alten Verwaltungsbereiche gelöscht: " + oldBereiche.size());
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
                                LOG.error("Fehler beim prüfen eines alten Verwaltungsbereichs", ex);
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
                        LOG.info("Es exitieren sowohl alte wie neue Verwaltungsbereiche -> abgleich");
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
                                    LOG.info("Es wurden ein neuer Verwaltungsbereich angelegt: " + currentBereich);
                                    // TODO duplicated code see checkofdifferences
                                    final VerwaltendeDienststelleCustomBean currentDienstelle =
                                        currentBereich.getDienststelle();
                                    if (currentDienstelle != null) {
                                        addedDienststellen.add(Message.createNewMessage(
                                                Message.RECEIVER_VERWALTUNGSSTELLE,
                                                Message.VERWALTUNGSBEREICH_NEW,
                                                currentDienstelle));
                                    } else {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("neuer Verwaltungsbereich angelegt ohne Dienstellenzuordnung");
                                        }
                                    }
                                } else if ((currentBereich.getId() != null)
                                            && (currentBereich.getId() != -1)
                                            && oldBereiche.contains(currentBereich)) {
                                    final int index = oldBereicheVector.indexOf(currentBereich);
                                    LOG.info("Verwaltungsbereich war schon in Datenbank: " + currentBereich
                                                + " index in altem Datenbestand=" + index);
                                    final VerwaltungsbereichCustomBean oldBereich = oldBereicheVector.get(index);
                                    final VerwaltendeDienststelleCustomBean oldDienststelle =
                                        oldBereich.getDienststelle();
                                    final VerwaltendeDienststelleCustomBean newDienststelle =
                                        currentBereich.getDienststelle();
                                    if ((oldDienststelle != null) && (newDienststelle != null)) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("AlteDienstelle=" + oldDienststelle + " NeueDienststelle="
                                                        + newDienststelle);
                                        }
                                        if (oldDienststelle.equals(newDienststelle)) {
                                            if (LOG.isDebugEnabled()) {
                                                LOG.debug("Dienstelle des Verwaltungsbereich ist gleich geblieben");
                                            }
                                        } else {
                                            if (LOG.isDebugEnabled()) {
                                                LOG.debug("Dienstelle des Verwaltungsbereichs hat sich geändert");
                                            }
                                            modDienststellen.add(Message.createNewMessage(
                                                    Message.RECEIVER_VERWALTUNGSSTELLE,
                                                    Message.VERWALTUNGSBEREICH_CHANGED,
                                                    oldDienststelle,
                                                    newDienststelle));
                                        }
                                    } else if (oldDienststelle == null) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug(
                                                "Einem vorhandenen Verwaltungsbereich wurde eine Dienstelle zugeordnet");
                                        }
                                        addedDienststellen.add(Message.createNewMessage(
                                                Message.RECEIVER_VERWALTUNGSSTELLE,
                                                Message.VERWALTUNGSBEREICH_NEW,
                                                newDienststelle));
                                    } else {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("Eine vorhandene Dienstellenzuordnung wurde entfernt");
                                        }
                                        deletedDienststellen.add(Message.createNewMessage(
                                                Message.RECEIVER_VERWALTUNGSSTELLE,
                                                Message.VERWALTUNGSBEREICH_DELETED,
                                                oldDienststelle));
                                    }
                                    oldBereicheVector.remove(currentBereich);
                                } else if ((currentBereich.getId() != null) && (currentBereich.getId() != -1)) {
                                    LOG.error(
                                        "Verwaltungsbereich hat eine ID, existiert aber nicht in altem Datenbestand --> equals funktioniert nicht");
                                    messages.add(Message.createNewMessage(
                                            Message.RECEIVER_ADMIN,
                                            Message.VERWALTUNGSBEREICH_ERROR,
                                            "Verwaltungsbereich hat eine ID, existiert aber nicht in altem Datenbestand",
                                            currentBereich));
                                    // TODO Nachricht an Benutzer
                                } else {
                                    LOG.fatal("nichtbehandelter fall currentBereich: " + currentBereich);
                                    messages.add(Message.createNewMessage(
                                            Message.RECEIVER_ADMIN,
                                            Message.VERWALTUNGSBEREICH_ERROR,
                                            "Ein bei der automatischen Generierung von Emails nicht behandelter Fall ist aufgetreten",
                                            currentBereich));
                                    // TODO Nachricht an Benutzer
                                }
                            } catch (Exception ex) {
                                LOG.error(
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
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("gelöschte Verwaltungsbereiche erfassen");
                        }
                        for (final VerwaltungsbereichCustomBean currentBereich : oldBereicheVector) {
                            try {
                                if (!newBereiche.contains(currentBereich)) {
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("Verwaltungsbereich existiert nicht mehr in neuem Datenbestand: "
                                                    + currentBereich);
                                    }
                                    final VerwaltendeDienststelleCustomBean oldDienststelle =
                                        currentBereich.getDienststelle();
                                    if (oldDienststelle == null) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("Für Verwaltungsbereich wurde keine Dienstelle zugeordnet");
                                        }
                                    } else {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("Verwaltungsbereich hatte eine Dienstelle");
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
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Nachrichten insgesamt: " + messages.size() + "davon sind neue Dienstellen="
                                        + addedDienststellen.size() + " gelöschte=" + deletedDienststellen.size()
                                        + " modifizierte=" + modDienststellen.size());
                        }
                    }
                } catch (Exception ex) {
                    // TODO what doing by generall failure sending the other and the failure ?
                    LOG.fatal("Fehler bei der email benachrichtigung", ex);
                    messages.add(Message.createNewMessage(
                            Message.RECEIVER_ADMIN,
                            Message.GENERAL_ERROR,
                            "LagIS - Fehler beim erstellen der automatischen Emails",
                            ex));
                    // TODO Nachricht an Benutzer
                }

                // modifyFlurstueck() does also save the current state of the Flurstueck (or throws an exception)
                modifyFlurstueck(currentFlurstueck);
            }
        } catch (Exception ex) {
            final StringBuffer buffer = new StringBuffer("Das Flurstück konnte nicht gespeichert werden.\nFehler: ");
            if (ex instanceof ActionNotSuccessfulException) {
                final ActionNotSuccessfulException reason = (ActionNotSuccessfulException)ex;
                if (reason.hasNestedExceptions()) {
                    LOG.error("Nested changeKind Exceptions: ", reason.getNestedExceptions());
                }
                buffer.append(reason.getMessage());
            } else {
                LOG.error("Unbekannter Fehler: ", ex);
                buffer.append("Unbekannter Fehler bitte wenden Sie sich an Ihren Systemadministrator");
            }
            LOG.error("Fehler beim speichern des aktuellen Flurstücks", ex);
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
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ein Refreshable gefunden");
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
//
//    /**
//     * DOCUMENT ME!
//     *
//     * @return  DOCUMENT ME!
//     */
//    public WFSRetrieverFactory.WFSWorkerThread getCurrentWFSRetriever() {
//        return currentWFSRetriever;
//    }

    /**
     * DOCUMENT ME!
     *
     * @param  listener  DOCUMENT ME!
     */
    public void addWfsFlurstueckGeometryChangeListener(final LagisBrokerPropertyChangeListener listener) {
        wfsFlurstueckChangeListeners.add(listener);
    }

    // TODO REFACTOR --> gerneralize
    @Override
    public synchronized void fireFlurstueckChanged(final FlurstueckCustomBean newFlurstueck) {
        getMappingComponent().getFeatureCollection().unselectAll();
        if (LOG.isDebugEnabled()) {
            LOG.debug("FlurstueckChangeEvent");
        }
        warnIfThreadIsNotEDT();
//
//        if ((currentWFSRetriever != null) && !currentWFSRetriever.isDone()) {
//            currentWFSRetriever.cancel(true);
//            currentWFSRetriever = null;
//        }

        resetWidgets();
        getMappingComponent().getFeatureCollection().removeAllFeatures();
        if (newFlurstueck != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("neues Flurstück != null");
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
            if (LOG.isDebugEnabled()) {
                LOG.debug("neues Flurstück == null");
            }
            observedFlurstueckChangedListeners.clear();
            setWidgetsEditable(false);
            currentFlurstueck = newFlurstueck;
            setCurrentFlurstueckSchluessel(null, true);
            flustueckChangeInProgress = true;
        }
//        if (newFlurstueck != null) {
//                final WFSRetrieverFactory.WFSWorkerThread wfsRetriever = (WFSRetrieverFactory.WFSWorkerThread)
//                    WFSRetrieverFactory.getInstance().getWFSRetriever(newFlurstueck.getFlurstueckSchluessel(), null, null);
//                currentWFSRetriever = wfsRetriever;
//                currentWFSRetriever.addPropertyChangeListener(new PropertyChangeListener() {
//
//                        @Override
//                        public void propertyChange(final PropertyChangeEvent evt) {
//                            for (final WfsFlurstueckGeometryChangeListener listener : wfsFlurstueckChangeListeners) {
//                                try {
//                                    listener.wfsFlurstueckGeometryChanged(evt);
//                                } catch (final Exception ex) {
//                                    LOG.error("Exception in Background Thread", ex);
//                                }
//                            }
//                        }
//                    });
//                currentWFSRetriever.execute();
//        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isUnkown  DOCUMENT ME!
     */
    public void setIsUnkownFlurstueck(final boolean isUnkown) {
        if (isUnkownFlurstueck() == true) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("FlurstückSchlüssel ist unbekannt: " + isUnkown);
            }
            LOG.info("setze currentFlurstück=null");
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("setCurrentFlurstueckSchluessel");
        }
        if ((currentFlurstueck != null) && !isUnkown) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("CurrentFlurstueckSchluessel ist ein bekanntes Flurstück");
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("CurrentFlurstueckSchluessel ist ein unbekanntes Flurstück");
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("FlurstueckChangeListener hat update beendet: " + fcListener);
        }
        observedFlurstueckChangedListeners.remove(fcListener);
        if (observedFlurstueckChangedListeners.isEmpty() && (flustueckChangeInProgress || isUnkown)) {
            if (isUnkown) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Flurstueck is unkown");
                }
            }
            flustueckChangeInProgress = false;
            if (LOG.isDebugEnabled()) {
                // log.debug("setting isUnknown = false");
                // isUnkown=false;
                LOG.debug("Alle FlurstueckChangeListener sind fertig --> zoom");
            }
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        mappingComponent.zoomToFeatureCollection();
                    }
                });
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl restlicher Listener: " + observedFlurstueckChangedListeners.size());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl restlicher Listener: " + observedFlurstueckChangedListeners);
                LOG.debug("flurstueckChange in progress: " + flustueckChangeInProgress);
                LOG.debug("isUnkown " + isUnkown);
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
        CALENDAR.setTime(date);
        CALENDAR.set(GregorianCalendar.HOUR, 0);
        CALENDAR.set(GregorianCalendar.MINUTE, 0);
        CALENDAR.set(GregorianCalendar.SECOND, 0);
        CALENDAR.set(GregorianCalendar.MILLISECOND, 0);
        CALENDAR.set(GregorianCalendar.AM_PM, GregorianCalendar.AM);
        return CALENDAR.getTime();
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
            LOG.fatal("Benutzername unvollständig: " + account);
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

    @Override
    public Element getConfiguration() {
        return null;
    }

    @Override
    public void masterConfigure(final Element parent) {
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
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Emails werden von: " + emailConfig + " verschickt");
                    LOG.debug("Empfänger vorhanden: nkf=" + nkfMailaddresses.size() + " admin="
                                + developerMailaddresses.size() + " maintenance=" + maintenanceMailAddresses.size());
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Empfänger vorhanden: nkf=" + nkfRecipients.toString() + " admin="
                                + developerRecipients.toString() + " maintenance=" + developerRecipients.toString());
                }
                if ((nkfMailaddresses.size() == 0) || (developerMailaddresses.size() == 0)
                            || (maintenanceMailAddresses.size() == 0)) {
                    throw new Exception("Eine oder mehrere Emailadressen sind nicht konfiguriert");
                }
            } catch (Exception ex) {
                LOG.fatal(
                    "Fehler beim konfigurieren der Emaileinstellungen, es können keine Emails versand werden.",
                    ex);
                emailConfig = null;
                // TODO Benutzerinformation Applikation beenden?
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim konfigurieren des Lagis Brokers: ", ex);
        }
    }

    @Override
    public void configure(final Element parent) {
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<RebeCustomBean> getCurrentRebes() {
        return currentRebes;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geom  DOCUMENT ME!
     */
    public void setCurrentWFSGeometry(final Geometry geom) {
        final Geometry oldWFSGeometry = geom;
        this.currentWFSGeometry = geom;

        for (final LagisBrokerPropertyChangeListener listener : wfsFlurstueckChangeListeners) {
            try {
                listener.propertyChange(new PropertyChangeEvent(
                        this,
                        LagisBrokerPropertyChangeListener.PROP__CURRENT_WFS_GEOMETRY,
                        oldWFSGeometry,
                        currentWFSGeometry));
            } catch (final Exception ex) {
                LOG.error("Exception in PropertyChange propagation", ex);
            }
        }

        new SwingWorker<List, Void>() {

                @Override
                protected List doInBackground() throws Exception {
                    if (currentWFSGeometry != null) {
                        return LagisBroker.getInstance().getRechteUndBelastungen(currentWFSGeometry);
                    } else {
                        return null;
                    }
                }

                @Override
                protected void done() {
                    final Collection oldRebes = currentRebes;
                    try {
                        currentRebes = get();
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                        currentRebes = null;
                    } finally {
                        for (final LagisBrokerPropertyChangeListener listener : wfsFlurstueckChangeListeners) {
                            try {
                                listener.propertyChange(new PropertyChangeEvent(
                                        this,
                                        LagisBrokerPropertyChangeListener.PROP__CURRENT_REBES,
                                        oldRebes,
                                        currentRebes));
                            } catch (final Exception ex) {
                                LOG.error("Exception in PropertyChange propagation", ex);
                            }
                        }
                    }
                }
            }.execute();
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
            gemarkungsHashMap = getGemarkungsHashMap();
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
            LOG.fatal("current Thread is not EDT, but should be --> look", new CurrentStackTrace());
        }
    }

    /**
     * DOCUMENT ME!
     */
    public static void warnIfThreadIsEDT() {
        if (EventQueue.isDispatchThread()) {
            LOG.fatal("current Thread is EDT, but should not --> look", new CurrentStackTrace());
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
            LOG.warn("Parent Component ist keine LagisApp Klasse");
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
            if (LOG.isDebugEnabled()) {
                LOG.debug("SwingWorker an Threadpool übermittelt");
            }
        } catch (Exception ex) {
            LOG.fatal("Fehler beim starten eines Swingworkers", ex);
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
     * @param  rebeBuffer  DOCUMENT ME!
     */
    public void setRebeBuffer(final double rebeBuffer) {
        this.rebeBuffer = rebeBuffer;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getRebeBuffer() {
        return rebeBuffer;
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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean checkPermissionBaulasten() {
        MetaClass mc = null;
        try {
            mc = CidsBean.getMetaClassFromTableName("WUNDA_BLAU", "alb_baulastblatt");
        } catch (Exception ex) {
            LOG.info("exception while getting metaclass alb_baulastblatt", ex);
        }
        return mc != null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean checkPermissionRisse() {
        MetaClass mc = null;
        try {
            mc = CidsBean.getMetaClassFromTableName("WUNDA_BLAU", "vermessung_riss");
        } catch (Exception ex) {
            LOG.info("exception while getting metaclass vermessung_riss", ex);
        }
        return mc != null;
    }
}
