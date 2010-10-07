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
import bean.KassenzeichenFacadeRemote;
import com.vividsolutions.jts.geom.Geometry;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.statusbar.StatusBar;
import de.cismet.lagis.gui.main.LagisApp;
import de.cismet.lagis.interfaces.FlurstueckChangeObserver;
import de.cismet.lagis.interfaces.FeatureSelectionChangedListener;
import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.FlurstueckRequester;
import de.cismet.lagis.interfaces.FlurstueckSaver;
import de.cismet.lagis.interfaces.GeometrySlotProvider;
import de.cismet.lagis.interfaces.Refreshable;
import de.cismet.lagis.interfaces.Widget;
import de.cismet.lagis.interfaces.Resettable;
import de.cismet.lagis.utillity.EmailConfig;
import de.cismet.lagis.utillity.GeometrySlotInformation;
import de.cismet.lagis.utillity.Message;
import de.cismet.lagis.validation.Validatable;
import de.cismet.lagisEE.bean.Exception.ActionNotSuccessfulException;
import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.core.Verwaltungsbereich;
import de.cismet.lagisEE.entity.core.hardwired.Gemarkung;
import de.cismet.lagisEE.entity.core.hardwired.VerwaltendeDienststelle;
import de.cismet.lagisEE.entity.locking.Sperre;
import de.cismet.lagisEE.interfaces.GeometrySlot;
import de.cismet.tools.CurrentStackTrace;
import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.NoWriteError;

import de.cismet.tools.gui.StaticSwingTools;
import java.awt.Color;
import java.awt.EventQueue;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import net.infonode.docking.RootWindow;
import net.infonode.gui.componentpainter.GradientComponentPainter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdom.Element;

/**
 *
 * @author Puhl
 */
public class LagisBroker implements FlurstueckChangeObserver, Configurable {

   private boolean loggedIn = false;
    HighlighterFactory highlighterFac = new HighlighterFactory();
    private static LagisBroker broker = null;
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LagisBroker.class);
    private static Vector<Resettable> clearAndDisableListeners = new Vector<Resettable>();
    private static DecimalFormat currencyFormatter = new DecimalFormat(",##0.00 \u00A4");
    //private static DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
    private static DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private MappingComponent mappingComponent;
    private static Vector<Widget> widgets = new Vector<Widget>();
    private RootWindow rootWindow;
    private static Flurstueck currentFlurstueck = null;
    private FlurstueckSchluessel currentFlurstueckSchluessel = null;
    private static Sperre currentSperre = null;
    //private static String accountName = "sebastian.puhl@cismet.de";
    //private String username;
    //private String group;
    //private String domain;
    private String account;
    private FlurstueckRequester requester;
    private JFrame parentComponent;
    private boolean isInWfsMode = false;
    //COLORS
    private static final Color yellow = new Color(231, 223, 84);
    public static final Color red = new Color(219, 96, 96);
    private static final Color blue = new Color(124, 160, 221);
    //public  static final Color grey = new Color(225,226,225);
    public static final Color grey = Color.LIGHT_GRAY;
    //JXTable
    public static final int alphaValue = 255;
    //TODO Perhaps a bit (blasser) brighter
    //public static Color ODD_ROW_DEFAULT_COLOR = new Color(blue.getRed()+119,blue.getGreen()+88,blue.getBlue()+33,alphaValue);
    public static Color ODD_ROW_DEFAULT_COLOR = new Color(blue.getRed() + 113, blue.getGreen() + 79, blue.getBlue() + 14, alphaValue);
    //public static final Color ODD_ROW_DEFAULT_COLOR = new Color(,,,alphaValue);
    //public static final Color ODD_ROW_DEFAULT_COLOR = new Color(blue.getRed()+119,blue.getGreen()+82,blue.getBlue()+34,alphaValue);
    //public static Color ODD_ROW_EDIT_COLOR = new Color(red.getRed()+36,red.getGreen()+146,red.getBlue()+152,alphaValue);
    public static Color ODD_ROW_EDIT_COLOR = new Color(red.getRed() + 25, red.getGreen() + 143, red.getBlue() + 143, alphaValue);
    public static Color ODD_ROW_LOCK_COLOR = new Color(yellow.getRed() + 23, yellow.getGreen() + 31, yellow.getBlue() + 134, alphaValue);
    //FlurstueckSearch
    public static final Color ERROR_COLOR = red;
    public static final Color ACCEPTED_COLOR = Color.WHITE;
    public static final Color UNKOWN_COLOR = ODD_ROW_LOCK_COLOR;
    public static final Color SUCCESSFUL_COLOR = new Color(113, 220, 109);
    //public static final Color SUCCESSFUL_COLOR = new Color(89,184,73);
    public static final Color INITIAL_COLOR = Color.WHITE;
    //WFS Geometry Color
    public static final Color STADT_FILLING_COLOR = new Color(43, 106, 21, 150);
    public static final Color ABTEILUNG_IX_FILLING_COLOR = new Color(100, 40, 106, 150);
    public static final Color UNKNOWN_FILLING_COLOR = UNKOWN_COLOR;
    public static final Color HISTORIC_FLURSTUECK_COLOR = Color.DARK_GRAY;
    //public static final Color ODD_ROW_COLOR = new Color(252,84,114,120);
    public static final Color EVEN_ROW_COLOR = Color.WHITE;
    public static final Color FOREGROUND_ROW_COLOR = Color.BLACK;
    //public static final AlternateRowHighlighter ALTERNATE_ROW_HIGHLIGHTER = new AlternateRowHighlighter(ODD_ROW_DEFAULT_COLOR, EVEN_ROW_COLOR, LagisBroker.FOREGROUND_ROW_COLOR);
    public static Highlighter ALTERNATE_ROW_HIGHLIGHTER = HighlighterFactory.createAlternateStriping(ODD_ROW_DEFAULT_COLOR, EVEN_ROW_COLOR);
    //public static final AlternateRowHighlighter ALTERNATE_ROW_HIGHLIGHTER_EDIT = new AlternateRowHighlighter(ODD_ROW_EDIT_COLOR,EVEN_ROW_COLOR, LagisBroker.FOREGROUND_ROW_COLOR);
    //TitleColors
    public static final Color EDIT_MODE_COLOR = red;
    public static final Color LOCK_MODE_COLOR = yellow;
    public static final Color HISTORY_MODE_COLOR = grey;
    public static final Color DEFAULT_MODE_COLOR = blue;
    //Permissions
    private boolean isFullReadOnlyMode = true;
    private boolean isCoreReadOnlyMode = true;
    private HashMap<String, Boolean> permissions = new HashMap<String, Boolean>();
    //resolving Gemarkungen
    private static HashMap<Integer, Gemarkung> gemarkungsHashMap;
    /** Creates a new instance of LagisBroker */
    private StatusBar statusBar;
    private ExecutorService execService = null;
    private int verdisCrossoverPort=-1;
    private KassenzeichenFacadeRemote verdisServer;
    private Geometry currentWFSGeometry;
    private double kassenzeichenBuffer=-0.5;

    private transient ConnectionSession session;

    public ConnectionSession getSession()
    {
        return session;
    }

    public void setSession(final ConnectionSession session)
    {
        this.session = session;
    }

    private LagisBroker() {
        execService = Executors.newCachedThreadPool();
    }

    public static LagisBroker getInstance() {
        if (broker == null) {
            broker = new LagisBroker();
        }
        return broker;
    }

    public void addWidget(Widget widget) {
        widgets.add(widget);
    }

    public void addWidgets(Vector widgets) {
        Iterator<Widget> it = widgets.iterator();
        while (it.hasNext()) {
            this.widgets.add(it.next());
        }
    //widgets.;
    }

//    //TODO perhaps a better way than hardwired connection to parenframe
//    public void setEditModeActivatable(boolean isActivatable){
//        ((LagisApp)getParentFrame()).setEditModeButtonEnabled(isActivatable);
//    }
//     public void addResettable(Resettable resettable) {
//         widgets.add(resettable);
//     }
//
//     public void addResettables(Vector components) {
//         Iterator it = components.iterator();
//         while(it.hasNext()){
//             Object tmp = it.next();
//             if(tmp instanceof Resettable){
//             clearAndDisableListeners.add((Resettable) tmp);
//             }
//         }
//     }
//
//     public void removeResettable(Resettable component) {
//         clearAndDisableListeners.remove(component);
//     }
    public void resetWidgets() {
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(new Runnable() {

                public void run() {
                    log.debug("Lagis Broker : Reset widgets");
//         Iterator<Resettable> it = clearAndDisableListeners.iterator();
//         while(it.hasNext()){
//             Resettable tmp = it.next();
//             tmp.clearComponent();
//             tmp.setComponentEditable(false);
//         }
                    Iterator<Widget> it = widgets.iterator();
                    while (it.hasNext()) {
                        Widget tmp = it.next();
                        tmp.clearComponent();
                        tmp.setComponentEditable(false);
                    }
                    log.debug("Lagis Broker : Reset widgets durch");
                }
            });
        }
        log.debug("Lagis Broker : Reset widgets");
//         Iterator<Resettable> it = clearAndDisableListeners.iterator();
//         while(it.hasNext()){
//             Resettable tmp = it.next();
//             tmp.clearComponent();
//             tmp.setComponentEditable(false);
//         }
        Iterator<Widget> it = widgets.iterator();
        while (it.hasNext()) {
            Widget tmp = it.next();
            tmp.clearComponent();
            tmp.setComponentEditable(false);
        }
        log.debug("Lagis Broker : Reset widgets durch");
    }

    public synchronized void setWidgetsEditable(final boolean isEditable) {
        log.debug("Setze Widgets editable: " + isEditable);
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(new Runnable() {

                public void run() {
                    Iterator<Widget> it = widgets.iterator();
                    while (it.hasNext()) {
                        if (isCurrentFlurstueckLockedByUser()) {
                            //ALTERNATE_ROW_HIGHLIGHTER = HighlighterFactory.createAlternateStriping(ODD_ROW_LOCK_COLOR, EVEN_ROW_COLOR);
                            ((ColorHighlighter)(((CompoundHighlighter) ALTERNATE_ROW_HIGHLIGHTER).getHighlighters()[0])).setBackground(ODD_ROW_LOCK_COLOR);
                        } else if (isEditable) {
                            //ALTERNATE_ROW_HIGHLIGHTER = HighlighterFactory.createAlternateStriping(ODD_ROW_EDIT_COLOR, EVEN_ROW_COLOR);
                            ((ColorHighlighter)(((CompoundHighlighter) ALTERNATE_ROW_HIGHLIGHTER).getHighlighters()[0])).setBackground(ODD_ROW_EDIT_COLOR);
                        //ALTERNATE_ROW_HIGHLIGHTER.setOddRowBackground(ODD_ROW_EDIT_COLOR);
                        } else {
                            //ALTERNATE_ROW_HIGHLIGHTER = HighlighterFactory.createAlternateStriping(ODD_ROW_DEFAULT_COLOR, EVEN_ROW_COLOR);
                            //ALTERNATE_ROW_HIGHLIGHTER.setOddRowBackground(ODD_ROW_DEFAULT_COLOR);
                            ((ColorHighlighter)(((CompoundHighlighter) ALTERNATE_ROW_HIGHLIGHTER).getHighlighters()[0])).setBackground(ODD_ROW_DEFAULT_COLOR);
                        }
                        if (isEditable) {
                            Widget currentWidget = it.next();
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
            Iterator<Widget> it = widgets.iterator();
            while (it.hasNext()) {
                if (isCurrentFlurstueckLockedByUser()) {
                    //ALTERNATE_ROW_HIGHLIGHTER = HighlighterFactory.createAlternateStriping(ODD_ROW_LOCK_COLOR, EVEN_ROW_COLOR);
                    ((ColorHighlighter)(((CompoundHighlighter) ALTERNATE_ROW_HIGHLIGHTER).getHighlighters()[0])).setBackground(ODD_ROW_LOCK_COLOR);
                } else if (isEditable) {
                    //ALTERNATE_ROW_HIGHLIGHTER = HighlighterFactory.createAlternateStriping(ODD_ROW_EDIT_COLOR, EVEN_ROW_COLOR);
                    ((ColorHighlighter)(((CompoundHighlighter) ALTERNATE_ROW_HIGHLIGHTER).getHighlighters()[0])).setBackground(ODD_ROW_EDIT_COLOR);
                //ALTERNATE_ROW_HIGHLIGHTER.setOddRowBackground(ODD_ROW_EDIT_COLOR);
                } else {
                    //ALTERNATE_ROW_HIGHLIGHTER = HighlighterFactory.createAlternateStriping(ODD_ROW_DEFAULT_COLOR, EVEN_ROW_COLOR);
                    //ALTERNATE_ROW_HIGHLIGHTER.setOddRowBackground(ODD_ROW_DEFAULT_COLOR);
                    ((ColorHighlighter)(((CompoundHighlighter) ALTERNATE_ROW_HIGHLIGHTER).getHighlighters()[0])).setBackground(ODD_ROW_DEFAULT_COLOR);
                }
                if (isEditable) {
                    Widget currentWidget = it.next();
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

    public GeometrySlotInformation assignGeometry(Geometry geom) {
        GeometrySlotInformation[] openSlots = collectGeometrySlots();
        switch (openSlots.length) {
            case 0:
                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(mappingComponent), "Es ist kein Element vorhanden dem eine Fläche zugeordnet werden kann\noder die entsprechenden Rechte sind nicht ausreichend", "Geometrie zuordnen", JOptionPane.INFORMATION_MESSAGE);
                return null;
            case 1:
                int anwser = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(mappingComponent), "Es ist genau ein Element vorhanden, dem eine Fläche zugeordnet werden kann:\n\n" + "    " + openSlots[0] + "\n\nSoll die Geometrie diesem dem Element hinzugefügt werden ?", "Geometrie zuordnen", JOptionPane.YES_NO_OPTION);
                if (anwser == JOptionPane.YES_OPTION) {
                    GeometrySlot slotGeom = openSlots[0].getOpenSlot();
                    if (slotGeom != null) {
                        slotGeom.setGeometry(geom);
                    } else {
                        //TODO create concept how to determine the color of geomentities
                        //slotGeom = new Geom();
                        slotGeom.setGeometry(geom);
                    //openSlots[0].getOpenSlot().setGeometrie(slotGeom);
                    }
                    return openSlots[0];
                } else {
                    return null;
                }
            default:
                GeometrySlotInformation selectedSlot = (GeometrySlotInformation) JOptionPane.showInputDialog(
                        StaticSwingTools.getParentFrame(mappingComponent),
                        "Bitte wählen Sie das Element, dem Sie die Geometrie zuordnen möchten:\n",
                        "Geometrie zuordnen",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        openSlots,
                        openSlots[0]);
                if (selectedSlot != null) {
                    GeometrySlot slotGeom = selectedSlot.getOpenSlot();
                    if (slotGeom != null) {
                        slotGeom.setGeometry(geom);
                    } else {
                        //TODO create concept how to determine the color of geomentities
                        //slotGeom = new Geom();
                        slotGeom.setGeometry(geom);
                    //selectedSlot.getOpenSlot().setGeometrie(slotGeom);
                    }
                    return selectedSlot;
                } else {
                    return null;
                }
        }
    }

    private GeometrySlotInformation[] collectGeometrySlots() {
        Vector<GeometrySlotInformation> openSlots = new Vector<GeometrySlotInformation>();
        Iterator<Widget> it = widgets.iterator();
        while (it.hasNext()) {
            Widget curWidget = it.next();
            if (curWidget instanceof GeometrySlotProvider) {
                openSlots.addAll(((GeometrySlotProvider) curWidget).getSlotInformation());
            }
        }
        return openSlots.toArray(new GeometrySlotInformation[openSlots.size()]);
    }

//    public void addFlurstueckChangedListener(ChangeListener listener){
//        flurstueckChangedListeners.add(listener);
//    }
//
//    public void addFlurstueckChangedListener(Vector listeners){
//        Iterator it = listeners.iterator();
//        while(it.hasNext()){
//            Object tmp = it.next();
//            if(tmp instanceof ChangeListener){
//                flurstueckChangedListeners.add((ChangeListener)tmp);
//            }
//        }
//    }
//
//    public void removeFlurstueckChangedListener(ChangeListener listener){
//        flurstueckChangedListeners.remove(listener);
//    }
    //TODO REFACTOR real event
    //Implement proper events not direkt Collection
    public void fireChangeEvent(Object event) {
        Iterator<Widget> it = widgets.iterator();
        while (it.hasNext()) {
            Widget curWidget = it.next();
            //TODO HARDCORE UGLY
            if (curWidget instanceof FeatureSelectionChangedListener && event instanceof Collection) {
                ((FeatureSelectionChangedListener) curWidget).featureSelectionChanged((Collection<Feature>) event);
            }
        }
    }

    public static DecimalFormat getCurrencyFormatter() {
        return currencyFormatter;
    }

    public static DateFormat getDateFormatter() {
        return dateFormatter;
    }

    public MappingComponent getMappingComponent() {
        return mappingComponent;
    }

    public void setMappingComponent(MappingComponent aMappingComponent) {
        mappingComponent = aMappingComponent;
    }

    public boolean acquireLock() {
        try {
            if (currentFlurstueck != null && currentSperre == null) {
                Sperre newSperre = new Sperre();
                //datamodell refactoring 22.10.07
                newSperre.setFlurstueckSchluessel(currentFlurstueck.getFlurstueckSchluessel().getId());
                newSperre.setBenutzerkonto(getAccountName());
                newSperre.setZeitstempel(new Date());
                Sperre result = EJBroker.getInstance().createLock(newSperre);
                if (result != null) {
                    if (result.getBenutzerkonto().equals(getAccountName()) && result.getZeitstempel().equals(newSperre.getZeitstempel())) {
                        currentSperre = result;
                        log.debug("Sperre konnte erfolgreich angelegt werden");
                        setWidgetsEditable(true);
                        for (Feature feature : (Collection<Feature>) getMappingComponent().getFeatureCollection().getSelectedFeatures()) {
                            getMappingComponent().getFeatureCollection().select(feature);
                        }
                        return true;
                    } else {
                        log.info("Sperre für flurstueck " + currentFlurstueck.getId() + " bereitsvorhanden von Benutzer " + result.getBenutzerkonto());
                        JOptionPane.showMessageDialog(parentComponent, "Der Datensatz ist schon vom Benutzer " + result.getBenutzerkonto() + " zum Verändern gesperrt", "Kein Editieren möglich", JOptionPane.INFORMATION_MESSAGE);
                        return false;
                    }
                } else {
                    log.info("Es konnte keine Sperre angelegt werden ?? warum");
                    return false;
                }
            } else {
                log.debug("Sperre Flurstueck ist null oder eine Sperre ist bereits vorhanden: \nSperre: " + currentSperre + "\nFlursuteck: " + currentFlurstueck);
                return false;
            }
        } catch (Exception ex) {
            log.error("Fehler beim anlegen der Sperre", ex);
            return false;
        }
    }

    public boolean releaseLock() {
        try {
            if (currentFlurstueck != null && currentSperre != null) {
                boolean result = EJBroker.getInstance().releaseLock(currentSperre);
                if (result) {
                    log.debug("Sperre erfolgreich gelöst");
                    currentSperre = null;
                    setWidgetsEditable(false);
                    return true;
                } else {
                    log.debug("Sperre konnte nicht entfernt werden ?? warum todo");
                    return false;
                }
            } else {
                log.debug("Sperre Flurstueck ist null oder eine Sperre ist bereits vorhanden: \nSperre: " + currentSperre + "\nFlursuteck: " + currentFlurstueck);
                return false;
            }
        } catch (Exception ex) {
            log.error("Fehler beim lösen der Sperre", ex);
            return false;
        }
    }

    public boolean isInEditMode() {
        return currentSperre != null;
    }
    private String currentValidationErrorMessage = null;

    public boolean validateWidgets() {
        Iterator<Widget> it = widgets.iterator();
        while (it.hasNext()) {
            Widget currentWidget = it.next();
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

    public synchronized void reloadFlurstueck() {
        if (currentFlurstueck != null) {
            log.info("reloadFlurstueck");
            resetWidgets();
            loadFlurstueck(currentFlurstueck.getFlurstueckSchluessel());
        } else {
            log.info("can't reload flurstueck == null");
        }
    }

    public synchronized void reloadFlurstueckKeys() {
        log.info("updateFlurstueckKeys");
        requester.updateFlurstueckKeys();
    }

    public synchronized void loadFlurstueck(FlurstueckSchluessel key) {
        //requester.requestFlurstueck(key);
        //requester.requestNewFlurstueck(key);
        if(isInEditMode()){
            JOptionPane.showMessageDialog(LagisBroker.getInstance().getParentComponent(),"Das Flurstück kann nur gewechselt werden wenn alle Änderungen gespeichert oder verworfen worden sind.","Wechseln nicht möglich",JOptionPane.WARNING_MESSAGE);
            return;
        }
        log.info("loadFlurstueck");
        resetWidgets();
        requester.requestFlurstueck(key);
    }

    public FlurstueckRequester getRequester() {
        return requester;
    }

    public void setRequester(FlurstueckRequester requester) {
        this.requester = requester;
    }
    //TODO optimize ugly code in my opinion old/new terror
    private Vector<Message> messages = new Vector<Message>();

    //Emails in Server auslagern
    public void saveCurrentFlurstueck() {
        try {
            messages = new Vector<Message>();
            if (currentFlurstueck != null) {
                Iterator<Widget> it = widgets.iterator();
                while (it.hasNext()) {
                    Widget curWidget = it.next();
                    if (curWidget instanceof FlurstueckSaver) {
                        log.debug("Daten von: " + curWidget.getWidgetName() + " werden gespeichert");
                        ((FlurstueckSaver) curWidget).updateFlurstueckForSaving(currentFlurstueck);
                    }
                }
                //TODO check if flurstück is changed at all
                try {
                    Flurstueck origFlurstueck = EJBroker.getInstance().retrieveFlurstueck(currentFlurstueck.getFlurstueckSchluessel());



                    //Checks the Dienstellen for changes
                    Set<Verwaltungsbereich> oldBereiche = origFlurstueck.getVerwaltungsbereiche();
                    Set<Verwaltungsbereich> newBereiche = currentFlurstueck.getVerwaltungsbereiche();
                    if ((oldBereiche == null || oldBereiche.size() == 0) && (newBereiche == null || newBereiche.size() == 0)) {
                        log.info("Es existieren keine Verwaltungsbereiche --> keine Veränderung");
                    } else if ((oldBereiche == null || oldBereiche.size() == 0)) {
                        log.info("Es wurden nur neue Verwaltungsbereiche angelegt: " + newBereiche.size());
                        for (Verwaltungsbereich currentBereich : newBereiche) {
                            try {
//                                Message newMessage = new Message();
//                                newMessage.setMessageReceiver(Message.RECEIVER_VERWALTUNGSSTELLE);
//                                newMessage.setMessageType(Message.VERWALTUNGSBEREICH_NEW);
//                                Vector messageObjects = new Vector();
//                                messageObjects.add(currentBereich);
//                                newMessage.setMessageObjects(messageObjects);
                                //TODO duplicated code see checkofdifferences
                                VerwaltendeDienststelle currentDienstelle = currentBereich.getDienststelle();
                                if (currentDienstelle != null) {
                                    messages.add(Message.createNewMessage(Message.RECEIVER_VERWALTUNGSSTELLE, Message.VERWALTUNGSBEREICH_NEW, currentDienstelle));
                                } else {
                                    log.debug("neuer Verwaltungsbereich angelegt ohne Dienstellenzuordnung");
                                }
                            } catch (Exception ex) {
                                log.error("Fehler beim prüfen eines neuen Verwaltungsbereichs", ex);
                                messages.add(Message.createNewMessage(Message.RECEIVER_ADMIN, Message.VERWALTUNGSBEREICH_ERROR, "Es wurden nur neue Flurstücke angelegt. Fehler beim Prüfen eines Verwaltungsgebrauchs", ex, currentBereich));
                            //TODO Nachricht an Benutzer
                            }
                        }
                    } else if ((newBereiche == null || newBereiche.size() == 0)) {
                        log.info("Es wurden alle alten Verwaltungsbereiche gelöscht: " + oldBereiche.size());
                        for (Verwaltungsbereich currentBereich : oldBereiche) {
                            try {
//                                Message newMessage = new Message();
//                                newMessage.setMessageReceiver(Message.RECEIVER_VERWALTUNGSSTELLE);
//                                newMessage.setMessageType(Message.VERWALTUNGSBEREICH_DELETED);
//                                Vector messageObjects = new Vector();
//                                messageObjects.add(currentBereich);
//                                newMessage.setMessageObjects(messageObjects);
                                messages.add(Message.createNewMessage(Message.RECEIVER_VERWALTUNGSSTELLE, Message.VERWALTUNGSBEREICH_DELETED, currentBereich.getDienststelle()));
                            } catch (Exception ex) {
                                log.error("Fehler beim prüfen eines alten Verwaltungsbereichs", ex);
                                messages.add(Message.createNewMessage(Message.RECEIVER_ADMIN, Message.VERWALTUNGSBEREICH_ERROR, "Es wurden alle Verwaltungsbereiche gelöscht. Fehler beim erzeugen der Benutzernachrichten", ex, currentBereich));
                            //TODO Nachricht an Benutzer
                            }
                        }
                    } else {
                        log.info("Es exitieren sowohl alte wie neue Verwaltungsbereiche -> abgleich");
                        Vector modDienststellen = new Vector();
                        Vector addedDienststellen = new Vector();
                        Vector deletedDienststellen = new Vector();
                        Vector<Verwaltungsbereich> oldBereicheVector = new Vector(oldBereiche);
                        Vector<Verwaltungsbereich> newBereicheVector = new Vector(newBereiche);
                        for (Verwaltungsbereich currentBereich : newBereiche) {
                            try {
                                if (currentBereich.getId() == null && !oldBereiche.contains(currentBereich)) {
                                    log.info("Es wurden ein neuer Verwaltungsbereich angelegt: " + currentBereich);
                                    //TODO duplicated code see checkofdifferences
                                    VerwaltendeDienststelle currentDienstelle = currentBereich.getDienststelle();
                                    if (currentDienstelle != null) {
                                        addedDienststellen.add(Message.createNewMessage(Message.RECEIVER_VERWALTUNGSSTELLE, Message.VERWALTUNGSBEREICH_NEW, currentDienstelle));
                                    } else {
                                        log.debug("neuer Verwaltungsbereich angelegt ohne Dienstellenzuordnung");
                                    }
                                } else if (currentBereich.getId() != null && oldBereiche.contains(currentBereich)) {
                                    int index = oldBereicheVector.indexOf(currentBereich);
                                    log.info("Verwaltungsbereich war schon in Datenbank: " + currentBereich + " index in altem Datenbestand=" + index);
                                    Verwaltungsbereich oldBereich = oldBereicheVector.get(index);
                                    VerwaltendeDienststelle oldDienststelle = oldBereich.getDienststelle();
                                    VerwaltendeDienststelle newDienststelle = currentBereich.getDienststelle();
                                    if (oldDienststelle != null && newDienststelle != null) {
                                        log.debug("AlteDienstelle=" + oldDienststelle + " NeueDienststelle=" + newDienststelle);
                                        if (oldDienststelle.equals(newDienststelle)) {
                                            log.debug("Dienstelle des Verwaltungsbereich ist gleich geblieben");
                                        } else {
                                            log.debug("Dienstelle des Verwaltungsbereichs hat sich geändert");
                                            modDienststellen.add(Message.createNewMessage(Message.RECEIVER_VERWALTUNGSSTELLE, Message.VERWALTUNGSBEREICH_CHANGED, oldDienststelle, newDienststelle));
                                        }
                                    } else if (oldDienststelle == null) {
                                        log.debug("Einem vorhandenen Verwaltungsbereich wurde eine Dienstelle zugeordnet");
                                        addedDienststellen.add(Message.createNewMessage(Message.RECEIVER_VERWALTUNGSSTELLE, Message.VERWALTUNGSBEREICH_NEW, newDienststelle));
                                    } else {
                                        log.debug("Eine vorhandene Dienstellenzuordnung wurde entfernt");
                                        deletedDienststellen.add(Message.createNewMessage(Message.RECEIVER_VERWALTUNGSSTELLE, Message.VERWALTUNGSBEREICH_DELETED, oldDienststelle));
                                    }
                                    oldBereicheVector.remove(currentBereich);
                                } else if (currentBereich.getId() != null) {
                                    log.error("Verwaltungsbereich hat eine ID, existiert aber nicht in altem Datenbestand --> equals funktioniert nicht");
                                    messages.add(Message.createNewMessage(Message.RECEIVER_ADMIN, Message.VERWALTUNGSBEREICH_ERROR, "Verwaltungsbereich hat eine ID, existiert aber nicht in altem Datenbestand", currentBereich));
                                //TODO Nachricht an Benutzer
                                } else {
                                    log.fatal("nichtbehandelter fall currentBereich: " + currentBereich);
                                    messages.add(Message.createNewMessage(Message.RECEIVER_ADMIN, Message.VERWALTUNGSBEREICH_ERROR, "Ein bei der automatischen Generierung von Emails nicht behandelter Fall ist aufgetreten", currentBereich));
                                //TODO Nachricht an Benutzer
                                }
                            } catch (Exception ex) {
                                log.error("Fehler beim abgeleich von alten und neuen Verwaltungsbereichen für die emailbenachrichtigung", ex);
                                messages.add(Message.createNewMessage(Message.RECEIVER_ADMIN, Message.VERWALTUNGSBEREICH_ERROR, "Es gab einen Fehler beim abgleichen alter und neuer Verwaltungsbereiche", ex, currentBereich));
                            //TODO Nachricht an Benutzer
                            }
                        }
                        log.debug("gelöschte Verwaltungsbereiche erfassen");
                        for (Verwaltungsbereich currentBereich : oldBereicheVector) {
                            try {
                                if (!newBereiche.contains(currentBereich)) {
                                    log.debug("Verwaltungsbereich existiert nicht mehr in neuem Datenbestand: " + currentBereich);
                                    VerwaltendeDienststelle oldDienststelle = currentBereich.getDienststelle();
                                    if (oldDienststelle == null) {
                                        log.debug("Für Verwaltungsbereich wurde keine Dienstelle zugeordnet");
                                    } else {
                                        log.debug("Verwaltungsbereich hatte eine Dienstelle");
                                        deletedDienststellen.add(Message.createNewMessage(Message.RECEIVER_VERWALTUNGSSTELLE, Message.VERWALTUNGSBEREICH_DELETED, oldDienststelle));
                                    }
                                }
                            } catch (Exception ex) {
                                messages.add(Message.createNewMessage(Message.RECEIVER_ADMIN, Message.VERWALTUNGSBEREICH_ERROR, "Es gab einen Fehler beim ermitteln, welche Verwaltungsbereiche gelöscht wurden", ex, currentBereich));
                            }
                        }
                        messages.addAll(addedDienststellen);
                        messages.addAll(modDienststellen);
                        messages.addAll(deletedDienststellen);
                        log.debug("Nachrichten insgesamt: " + messages.size() + "davon sind neue Dienstellen=" + addedDienststellen.size() + " gelöschte=" + deletedDienststellen.size() + " modifizierte=" + modDienststellen.size());
                    }
                } catch (Exception ex) {
                    //TODO what doing by generall failure sending the other and the failure ?
                    log.fatal("Fehler bei der email benachrichtigung", ex);
                    messages.add(Message.createNewMessage(Message.RECEIVER_ADMIN, Message.GENERAL_ERROR, "LagIS - Fehler beim erstellen der automatischen Emails", ex));
                //TODO Nachricht an Benutzer
                }
                EJBroker.getInstance().modifyFlurstueck(currentFlurstueck);
                //TODO only sending if the flurstück is saved definetly

                sendMessages();
                log.debug("sendMessages() returned");
            }
        } catch (Exception ex) {
            final StringBuffer buffer = new StringBuffer("Das Flurstück konnte nicht gespeichert werden.\nFehler: ");
            if (ex instanceof ActionNotSuccessfulException) {
                ActionNotSuccessfulException reason = (ActionNotSuccessfulException) ex;
                if (reason.hasNestedExceptions()) {
                    log.error("Nested changeKind Exceptions: ", reason.getNestedExceptions());
                }
                buffer.append(reason.getMessage());
            } else {
                log.error("Unbekannter Fehler: ", ex);
                buffer.append("Unbekannter Fehler bitte wenden Sie sich an Ihren Systemadministrator");
            }
            log.error("Fehler beim speichern des aktuellen Flurstücks", ex);
            JOptionPane.showMessageDialog(parentComponent, buffer.toString(), "Fehler beim speichern", JOptionPane.ERROR_MESSAGE);
        }
    }

    //TODO if you leave this method --> you doesn't need the class EmailConfig, just configure auth etc directly
    //TODO NO HARDCODING !!!!!!!!!
    /// MORE INFORMATIONS WHAT FLURSTÜCK, WHAT VERWALTUNGSGEBRAUCH
    private void sendMessages() {
        //TODO extra Thread nötig ??
        //HOT FIX Geht wahrscheinlich nicht bekomme mal keine Emails !!!
        Thread t = new Thread() {

            public void run() {
                try {
                    log.debug("send Messages()");
                    if (messages != null) {
                        if (messages.size() > 0) {
                            MailAuthenticator auth = new MailAuthenticator(emailConfig.getUsername(), emailConfig.getPassword());
                            Properties properties = new Properties();
                            properties.put("mail.smtp.host", emailConfig.getSmtpServer());
                            Session session = Session.getDefaultInstance(properties, auth);
                            for (Message currentMessage : messages) {
                                try {
                                    javax.mail.Message msg = new MimeMessage(session);
                                    msg.setFrom(new InternetAddress(emailConfig.getSenderAddress()));
                                    //TODO OPTIMIZATION OVERALL CATEGORY
                                    if (currentMessage.getMessageType() == Message.VERWALTUNGSBEREICH_CHANGED) {
                                        Vector messageObjects = currentMessage.getMessageObjects();
                                        VerwaltendeDienststelle oldDienststelle = (VerwaltendeDienststelle) messageObjects.get(0);
                                        VerwaltendeDienststelle newDienststelle = (VerwaltendeDienststelle) messageObjects.get(1);
                                        //TODO OPTIMIZE
                                        if (oldDienststelle.getEmailAdresse() == null || newDienststelle.getEmailAdresse() == null) {
                                            throw new Exception("Eine Emailaddresse eines Verwaltungsbereichs ist nicht gesetzt: " + oldDienststelle.getEmailAdresse() + " " + newDienststelle.getEmailAdresse());
                                        }
                                        msg.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(oldDienststelle.getEmailAdresse() + "," + newDienststelle.getEmailAdresse(), false));
                                        msg.setSubject("Lagis - Änderung Zuständigkeitsbereiche");
                                        //TODO mit replacements arbeiten config file
//                                msg.setText("Bei dieser Mail handelt es sich um eine automatisch von LagIS erstellte Benachrichtigung.\n\n" +
//                                        "Folgendener Fehler ist zur Laufzeit aufgetreten:\n\n" +
//                                        messageObjects.get(0)+"n\n" +
//                                        "Zugehöriger Stacktrace:\n\n" +
//                                        messageObjects.get(1));
                                        msg.setText("Bei dem Flurstück:\n" +
                                                currentFlurstueck + "\n" +
                                                "wurde die Zuordnung zur unterhaltenden Dienststelle geändert.");
                                    } else if (currentMessage.getMessageType() == Message.VERWALTUNGSBEREICH_NEW) {
                                        Vector messageObjects = currentMessage.getMessageObjects();
                                        VerwaltendeDienststelle newDienststelle = (VerwaltendeDienststelle) messageObjects.get(0);
                                        //TODO OPTIMIZE
                                        if (newDienststelle.getEmailAdresse() == null) {
                                            throw new Exception("Eine Emailaddresse eines Verwaltungsbereichs ist nicht gesetzt: " + newDienststelle.getEmailAdresse());
                                        }
                                        msg.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(newDienststelle.getEmailAdresse(), false));
                                        msg.setSubject("Lagis - Änderung Zuständigkeitsbereiche");
                                        msg.setText("Bei dem Flurstück:\n" +
                                                currentFlurstueck + "\n" +
                                                "wurde die Zuordnung zur unterhaltenden Dienststelle hinzugefügt.");
                                    } else if (currentMessage.getMessageType() == Message.VERWALTUNGSBEREICH_DELETED) {
                                        Vector messageObjects = currentMessage.getMessageObjects();
                                        VerwaltendeDienststelle oldDienststelle = (VerwaltendeDienststelle) messageObjects.get(0);
                                        //TODO OPTIMIZE
                                        if (oldDienststelle.getEmailAdresse() == null) {
                                            throw new Exception("Eine Emailaddresse eines Verwaltungsbereichs ist nicht gesetzt: " + oldDienststelle.getEmailAdresse());
                                        }
                                        msg.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(oldDienststelle.getEmailAdresse(), false));
                                        msg.setSubject("Lagis - Änderung Zuständigkeitsbereiche");
                                        msg.setText("Dienststelle deleted");
                                        msg.setText("Bei dem Flurstück:\n" +
                                                currentFlurstueck + "\n" +
                                                "wurde die Zuordnung zur unterhaltenden Dienststelle entfernt.");
                                    } else if (currentMessage.getMessageType() == Message.VERWALTUNGSBEREICH_ERROR) {
                                        msg.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(developerRecipients + "," + maintenanceMailAddresses, false));
                                        Vector messageObjects = currentMessage.getMessageObjects();
                                        msg.setSubject("Lagis - Fehler beim Abgleichen von Verwaltungsbereichen");
                                        msg.setText("Bei dieser Mail handelt es sich um eine automatisch von LagIS erstellte Fehlermeldung.\n\n" +
                                                "Folgendener Fehler ist zur Laufzeit aufgetreten:\n\n" +
                                                messageObjects.get(0) + "n\n" +
                                                "Zugehöriger Stacktrace:\n\n" +
                                                messageObjects.get(1));
                                    } else if (currentMessage.getMessageType() == Message.GENERAL_ERROR) {
                                        msg.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(developerRecipients + "," + maintenanceMailAddresses, false));
                                        Vector messageObjects = currentMessage.getMessageObjects();
                                        msg.setSubject((String) messageObjects.get(0));
                                        msg.setText("Bei dieser Mail handelt es sich um eine automatisch von LagIS erstellte Fehlermeldung.\n\n" +
                                                "Folgendener Fehler ist zur Laufzeit aufgetreten:\n\n" +
                                                "Eine oder Mehrere Emails konnten nicht erstellt werden\n\n" +
                                                "Zugehöriger Stacktrace:\n\n" +
                                                messageObjects.get(1));
                                    }
                                    // Hier lassen sich HEADER-Informationen hinzufügen
                                    //msg.setHeader("Test", "Test");
                                    msg.setSentDate(new Date());
                                    Transport.send(msg);
                                } catch (Exception ex) {
                                    log.fatal("Fehler beim senden einer Emails: ", ex);
                                //TODO Benutzer benachrichtigen
                                }
                            }
                        } else {
                            log.warn("Keine Meldungen zum versenden vorhanden == 0");
                        }
                    } else {
                        log.warn("Keine Meldungen zum versenden vorhanden == null");
                    }
                } catch (Exception ex) {
                    log.fatal("Fehler beim senden von Emails: ", ex);
                //TODO Benutzer benachrichtigen
                }
                log.debug("sendMessage() end");
            }
        };
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }

    public Flurstueck getCurrentFlurstueck() {
        return currentFlurstueck;
    }

    public boolean isCurrentFlurstueckLockedByUser() {
        if (currentFlurstueck != null) {
            //datamodell refactoring 22.10.07
            return currentFlurstueck.getFlurstueckSchluessel().isGesperrt();
        } else {
            return false;
        }
    }

    public JFrame getParentComponent() {
        return parentComponent;
    }

    public void setParentComponent(JFrame parentComponent) {
        this.parentComponent = parentComponent;
    }

    public boolean isInWfsMode() {
        return isInWfsMode;
    }

    public boolean isIsInWfsMode() {
        return isInWfsMode;
    }

    public void setIsInWfsMode(boolean isInWfsMode) {
        this.isInWfsMode = isInWfsMode;
    }

    public void setTitleBarComponentpainter(Color color) {
        getRootWindow().getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().getShapedPanelProperties().setComponentPainter(new GradientComponentPainter(color, new Color(236, 233, 216), color, new Color(236, 233, 216)));
    }

    public void setTitleBarComponentpainter(Color left, Color right) {
        getRootWindow().getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().getShapedPanelProperties().setComponentPainter(new GradientComponentPainter(left, right, left, right));
    }

    public Refreshable getRefreshableByClass(Class<?> refreshClass) {
        Iterator<Widget> it = widgets.iterator();
        while (it.hasNext()) {
            Refreshable curRefreshable = it.next();
            if (curRefreshable.getClass().equals(refreshClass)) {
                log.debug("ein Refreshable gefunden");
                return curRefreshable;
            }
        }
        return null;
    }

    public void refreshWidgets() {
        Iterator<Widget> it = widgets.iterator();
        while (it.hasNext()) {
            Refreshable curRefreshable = it.next();
            curRefreshable.refresh(null);
        }
    }

    public RootWindow getRootWindow() {
        return rootWindow;
    }

    public void setRootWindow(RootWindow rootWindow) {
        this.rootWindow = rootWindow;
    }
    private boolean flustueckChangeInProgress = false;
    Vector<FlurstueckChangeListener> observedFlurstueckChangedListeners = new Vector<FlurstueckChangeListener>();

    public boolean isFlurstueckChangeInProgress() {
        return flustueckChangeInProgress;
    }

    //TODO REFACTOR --> gerneralize
    public synchronized void fireFlurstueckChanged(Flurstueck newFlurstueck) {
        getMappingComponent().getFeatureCollection().unselectAll();
        log.debug("FlurstueckChangeEvent");
        warnIfThreadIsNotEDT();
//        Iterator<ChangeListener> it = flurstueckChangedListeners.iterator();
//        while(it.hasNext()){
//            it.next().FlurstueckChanged(newFlurstueck);
//        }        
        resetWidgets();
        getMappingComponent().getFeatureCollection().removeAllFeatures();
        if (newFlurstueck != null) {
            log.debug("neues Flurstück != null");
            observedFlurstueckChangedListeners.clear();
            for (Widget widget : widgets) {
                if (widget instanceof FlurstueckChangeListener) {
                    observedFlurstueckChangedListeners.add((FlurstueckChangeListener) widget);
                }
            }
            flustueckChangeInProgress = true;
            currentFlurstueck = newFlurstueck;
            setCurrentFlurstueckSchluessel(newFlurstueck.getFlurstueckSchluessel(), false);
            setWidgetsEditable(false);
            Iterator<Widget> it = widgets.iterator();
            while (it.hasNext()) {
                Widget curWidget = it.next();
                if (curWidget instanceof FlurstueckChangeListener) {
                    ((FlurstueckChangeListener) curWidget).flurstueckChanged(newFlurstueck);
                }
            }
        } else {
            log.debug("neues Flurstück == null");
            observedFlurstueckChangedListeners.clear();
            setWidgetsEditable(false);
            currentFlurstueck = newFlurstueck;
            setCurrentFlurstueckSchluessel(null, true);
            flustueckChangeInProgress = true;
        }
    }
    private boolean isUnkown = false;

    public void setIsUnkownFlurstueck(boolean isUnkown) {
        if (isUnkownFlurstueck() == true) {
            log.debug("FlurstückSchlüssel ist unbekannt: " + isUnkown);
            log.info("setze currentFlurstück=null");
            if (getParentComponent() != null && getParentComponent() instanceof LagisApp) {
                ((LagisApp) getParentComponent()).setFlurstueckUnkown();
            }
        }
        this.isUnkown = isUnkown;
    }

    public void setCurrentFlurstueckSchluessel(final FlurstueckSchluessel flurstueckSchluessel, boolean isUnkown) {
        log.debug("setCurrentFlurstueckSchluessel");
        if (currentFlurstueck != null && !isUnkown) {
            log.debug("CurrentFlurstueckSchluessel ist ein bekanntes Flurstück");
        } else {
            log.debug("CurrentFlurstueckSchluessel ist ein unbekanntes Flurstück");
        }
        setIsUnkownFlurstueck(isUnkown);
        currentFlurstueckSchluessel = flurstueckSchluessel;
    }

    public FlurstueckSchluessel getCurrentFlurstueckSchluessel() {
        return currentFlurstueckSchluessel;
    }

    public boolean isUnkownFlurstueck() {
        return this.isUnkown;
    }

    public void flurstueckChangeFinished(FlurstueckChangeListener fcListener) {
        log.debug("FlurstueckChangeListener hat update beendet: " + fcListener);
        observedFlurstueckChangedListeners.remove(fcListener);
        if (observedFlurstueckChangedListeners.isEmpty() && (flustueckChangeInProgress || isUnkown)) {
            if (isUnkown) {
                log.debug("Flurstueck is unkown");
            }
            flustueckChangeInProgress = false;
            //log.debug("setting isUnknown = false");
            //isUnkown=false;
            log.debug("Alle FlurstueckChangeListener sind fertig --> zoom");
            EventQueue.invokeLater(new Runnable() {

                public void run() {
                    mappingComponent.zoomToFeatureCollection();
                }
            });
        } else {
            log.debug("Anzahl restlicher Listener: " + observedFlurstueckChangedListeners.size());
            log.debug("Anzahl restlicher Listener: " + observedFlurstueckChangedListeners);
            log.debug("flurstueckChange in progress: " + flustueckChangeInProgress);
            log.debug("isUnkown " + isUnkown);
        }
    }
    private static GregorianCalendar calender = new GregorianCalendar();

    public static Date getDateWithoutTime(Date date) {
        calender.setTime(date);
        calender.set(GregorianCalendar.HOUR, 0);
        calender.set(GregorianCalendar.MINUTE, 0);
        calender.set(GregorianCalendar.SECOND, 0);
        calender.set(GregorianCalendar.MILLISECOND, 0);
        calender.set(GregorianCalendar.AM_PM, GregorianCalendar.AM);
        return calender.getTime();
    }

    //TODO nächsten 6 methoden Sinnvoll ??
//    public String getUsername() {
//        return username;
//    }
//
//    public  void setUsername(String aUsername) {
//        username = aUsername;
//    }
//
//    public String getGroup() {
//        return group;
//    }
//
//    public void setGroup(String aGroup) {
//        group = aGroup;
//    }
//
//    public String getDomain() {
//        return domain;
//    }
//
//    public void setDomain(String aDomain) {
//        domain = aDomain;
//    }
    //TODO is fullqualified username
    public String getAccountName() {
        if (account == null) {
            log.fatal("Benutzername unvollständig: " + account);
        }
        return account;
    }

    public void setAccountName(String aAccount) {
        account = aAccount;
    }

    public boolean isFullReadOnlyMode() {
        return isFullReadOnlyMode;
    }

    public void setFullReadOnlyMode(boolean isFullReadOnlyMode) {
        this.isFullReadOnlyMode = isFullReadOnlyMode;
    }

    public boolean isCoreReadOnlyMode() {
        return isCoreReadOnlyMode;
    }

    public void setCoreReadOnlyMode(boolean isCoreReadOnlyMode) {
        this.isCoreReadOnlyMode = isCoreReadOnlyMode;
    }

    public HashMap<String, Boolean> getPermissions() {
        return permissions;
    }

    public void setPermissions(HashMap<String, Boolean> permissions) {
        this.permissions = permissions;
    }

    public Element getConfiguration() throws NoWriteError {
        return null;
    }
    private EmailConfig emailConfig;
    private Vector<String> developerMailaddresses;
    private Vector<String> nkfMailaddresses;
    private Vector<String> maintenanceMailAddresses;
    //if you don't use the vectors delete them
    private StringBuffer nkfRecipients;
    private StringBuffer developerRecipients;
    private StringBuffer maintenanceRecipients;

    public void masterConfigure(Element parent) {
        /*
        <emailConfiguration username="" password="" senderAddress="sebastian.puhl@cismet.de" smtpHost="smtp.uni-saarland.de">
        <neuesKommunalesFinanzmanagement>
        <receiver>sebastian.puhl@cismet.de</receiver>
        </neuesKommunalesFinanzmanagement>
        <failures>
        <receiver>sebastian.puhl@cismet.de</receiver>
        </failures>
        </emailConfiguration>
         */
        try {
            Element email = parent.getChild("emailConfiguration");
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
                for (Element nkfReveiver : (List<Element>) email.getChild(Message.MAIL_ADDRESSES_NKF).getChildren()) {
                    nkfMailaddresses.add(nkfReveiver.getText());
                    nkfRecipients.append(nkfReveiver.getText() + ",");
                }
                for (Element developerReveiver : (List<Element>) email.getChild(Message.MAIL_ADDRESSES_DEVELOPER).getChildren()) {
                    developerMailaddresses.add(developerReveiver.getText());
                    developerRecipients.append(developerReveiver.getText() + ",");
                }
                for (Element maintenanceReveiver : (List<Element>) email.getChild(Message.MAIL_ADDRESSES_MAINTENANCE).getChildren()) {
                    maintenanceMailAddresses.add(maintenanceReveiver.getText());
                    maintenanceRecipients.append(maintenanceReveiver.getText() + ",");
                }
                developerRecipients.deleteCharAt(developerRecipients.length() - 1);
                nkfRecipients.deleteCharAt(nkfRecipients.length() - 1);
                maintenanceRecipients.deleteCharAt(maintenanceRecipients.length() - 1);
                log.debug("Emails werden von: " + emailConfig + " verschickt");
                log.debug("Empfänger vorhanden: nkf=" + nkfMailaddresses.size() + " admin=" + developerMailaddresses.size() + " maintenance=" + maintenanceMailAddresses.size());
                log.debug("Empfänger vorhanden: nkf=" + nkfRecipients.toString() + " admin=" + developerRecipients.toString() + " maintenance=" + developerRecipients.toString());
                if (nkfMailaddresses.size() == 0 || developerMailaddresses.size() == 0 || maintenanceMailAddresses.size() == 0) {
                    throw new Exception("Eine oder mehrere Emailadressen sind nicht konfiguriert");
                }
            } catch (Exception ex) {
                log.fatal("Fehler beim konfigurieren der Emaileinstellungen, es können keine Emails versand werden.", ex);
                emailConfig = null;
            //TODO Benutzerinformation Applikation beenden?
            }
        } catch (Exception ex) {
            log.error("Fehler beim konfigurieren des Lagis Brokers: ", ex);
        }
        try {
                HashMap<String, Boolean> perms = new HashMap<String, Boolean>();
                Element userPermissions = parent.getChild("permissions");
                List<Element> xmlPermissions = userPermissions.getChildren();
                for (Element currentPermission : xmlPermissions) {
                    try {
                        String isReadWriteAllowedString = currentPermission.getChildText("readWrite");
                        boolean isReadWriteAllowed = false;
                        if (isReadWriteAllowedString != null) {
                            if (isReadWriteAllowedString.equals("true")) {
                                isReadWriteAllowed = true;
                            }
                        }
                        String userGroup = currentPermission.getChildText("userGroup");
                        String userDomain = currentPermission.getChildText("userDomain");
                        String permissionString = userGroup + "@" + userDomain;
                        log.info("Permissions für: login=*@" + permissionString + " readWriteAllowed=" + isReadWriteAllowed + "(boolean)/" + isReadWriteAllowedString + "(String)");
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
                //TODO wenigstens den Nutzer benachrichtigen sonst ist es zu hard oder nur lesen modus --> besser!!!
                //System.exit(1);
            }
    }

    public void configure(final Element parent) {
    }

    public void setVerdisServer(final KassenzeichenFacadeRemote verdisServer) {
        this.verdisServer = verdisServer;
    }

    public KassenzeichenFacadeRemote getVerdisServer() {
        return verdisServer;
    }

    public void setCurrentWFSGeometry(final Geometry geom) {
        this.currentWFSGeometry = geom;
    }

    public Geometry getCurrentWFSGeometry() {
        return currentWFSGeometry;
    }

    class MailAuthenticator extends Authenticator {

        /**
         * Ein String, der den Usernamen nach der Erzeugung eines
         * Objektes<br>
         * dieser Klasse enthalten wird.
         */
        private final String user;
        /**
         * Ein String, der das Passwort nach der Erzeugung eines
         * Objektes<br>
         * dieser Klasse enthalten wird.
         */
        private final String password;

        /**
         * Der Konstruktor erzeugt ein MailAuthenticator Objekt<br>
         * aus den beiden Parametern user und passwort.
         *
         * @param user
         *            String, der Username fuer den Mailaccount.
         * @param password
         *            String, das Passwort fuer den Mailaccount.
         */
        public MailAuthenticator(String user, String password) {
            this.user = user;
            this.password = password;
        }

        /**
         * Diese Methode gibt ein neues PasswortAuthentication
         * Objekt zurueck.
         *
         * @see javax.mail.Authenticator#getPasswordAuthentication()
         */
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(this.user, this.password);
        }
    }

    public String getCurrentValidationErrorMessage() {
        return currentValidationErrorMessage;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    public void setStatusBar(StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    public Gemarkung getGemarkungForKey(final Integer key) {
        Gemarkung resolvedGemarkung = null;
        if (gemarkungsHashMap != null) {
            resolvedGemarkung = gemarkungsHashMap.get(key);
        } else {
            gemarkungsHashMap = EJBroker.getInstance().getGemarkungsHashMap();
            if (gemarkungsHashMap != null) {
                resolvedGemarkung = gemarkungsHashMap.get(key);
            }
        }
        return resolvedGemarkung;
    }

    //TODO configurieren ob es ausgeführt werden soll oder nicht z.B. boolean
    public static void warnIfThreadIsNotEDT() {
        if (!EventQueue.isDispatchThread()) {
            log.fatal("current Thread is not EDT, but should be --> look", new CurrentStackTrace());
        }
    }

    public static void warnIfThreadIsEDT() {
        if (EventQueue.isDispatchThread()) {
            log.fatal("current Thread is EDT, but should not --> look", new CurrentStackTrace());
        }
    }
    //TODO what if error during saving

    public void acceptChanges() {
        if (parentComponent instanceof LagisApp) {
            ((LagisApp) parentComponent).acceptChanges();
        } else {
            log.warn("Parent Component ist keine LagisApp Klasse");
        }
    }

    public void execute(SwingWorker workerThread) {
        try {
            execService.submit(workerThread);
            log.debug("SwingWorker an Threadpool übermittelt");
        } catch (Exception ex) {
            log.fatal("Fehler beim starten eines Swingworkers", ex);
        }
    }
       
     public void setVerdisCrossoverPort(int port) {
        this.verdisCrossoverPort = port;
    }

    public int getVerdisCrossoverPort() {
        return verdisCrossoverPort;
    }


    public  void setKassenzeichenBuffer(double kassenzeichenBuffer) {
        this.kassenzeichenBuffer=kassenzeichenBuffer;
    }

    public double getKassenzeichenBuffer() {
        return kassenzeichenBuffer;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
