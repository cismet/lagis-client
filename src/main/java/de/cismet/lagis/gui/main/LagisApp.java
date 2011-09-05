/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * Lagis.java
 *
 * Created on 16. März 2007, 12:10
 */
package de.cismet.lagis.gui.main;

import Sirius.navigator.connection.Connection;
import Sirius.navigator.connection.ConnectionFactory;
import Sirius.navigator.connection.ConnectionInfo;
import Sirius.navigator.connection.ConnectionSession;
import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.connection.proxy.ConnectionProxy;
import Sirius.navigator.plugin.context.PluginContext;
import Sirius.navigator.plugin.interfaces.FloatingPluginUI;
import Sirius.navigator.plugin.interfaces.PluginMethod;
import Sirius.navigator.plugin.interfaces.PluginProperties;
import Sirius.navigator.plugin.interfaces.PluginSupport;
import Sirius.navigator.plugin.interfaces.PluginUI;

import Sirius.server.newuser.User;

import bean.KassenzeichenFacadeRemote;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

import com.sun.jersey.api.container.ContainerFactory;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import com.vividsolutions.jts.geom.Geometry;

import java_cup.parser;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.mouse.DockingWindowActionMouseButtonListener;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DeveloperUtil;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.PropertiesUtil;
import net.infonode.docking.util.StringViewMap;
import net.infonode.gui.componentpainter.AlphaGradientComponentPainter;
import net.infonode.gui.componentpainter.GradientComponentPainter;
import net.infonode.util.Direction;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.auth.DefaultUserNameStore;
import org.jdesktop.swingx.auth.LoginService;

import org.jdom.Element;

import org.netbeans.api.wizard.WizardDisplayer;

import java.applet.AppletContext;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.InetSocketAddress;

import java.rmi.Remote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;

import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.gui.ClipboardWaitDialog;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.CustomFeatureInfoListener;
import de.cismet.cismap.commons.gui.printing.Scale;
import de.cismet.cismap.commons.gui.statusbar.StatusBar;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.wfsforms.AbstractWFSForm;
import de.cismet.cismap.commons.wfsforms.WFSFormFactory;

import de.cismet.ee.EJBAccessor;

import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.broker.LagisCrossover;

import de.cismet.lagis.gui.panels.AktenzeichenSearch;
import de.cismet.lagis.gui.panels.DMSPanel;
import de.cismet.lagis.gui.panels.FlurstueckChooser;
import de.cismet.lagis.gui.panels.HistoryPanel;
import de.cismet.lagis.gui.panels.InformationPanel;
import de.cismet.lagis.gui.panels.KartenPanel;
import de.cismet.lagis.gui.panels.NKFOverviewPanel;
import de.cismet.lagis.gui.panels.NKFPanel;
import de.cismet.lagis.gui.panels.ReBePanel;
import de.cismet.lagis.gui.panels.VerdisCrossoverPanel;
import de.cismet.lagis.gui.panels.VertraegePanel;
import de.cismet.lagis.gui.panels.VerwaltungsPanel;

import de.cismet.lagis.interfaces.FeatureSelectionChangedListener;
import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.Widget;

import de.cismet.lagis.thread.BackgroundUpdateThread;
import de.cismet.lagis.thread.WFSRetrieverFactory;

import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.ValidationStateChangedListener;

import de.cismet.lagis.widget.AbstractWidget;
import de.cismet.lagis.widget.RessortFactory;

import de.cismet.lagis.wizard.ContinuationWizard;

import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.core.hardwired.FlurstueckArt;

import de.cismet.lagisEE.util.FlurKey;

import de.cismet.tools.StaticDecimalTools;

import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.ConfigurationManager;
import de.cismet.tools.configuration.NoWriteError;

import de.cismet.tools.gui.Static2DTools;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.historybutton.HistoryModelListener;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class LagisApp extends javax.swing.JFrame implements PluginSupport,
    FloatingPluginUI,
    Configurable,
    WindowListener,
    HistoryModelListener,
    Widget,
    FlurstueckChangeListener,
    FeatureSelectionChangedListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(LagisApp.class);
    // private FloatingWindow aktenzeichenFloatingWindow;
    // Icons & Image
    private static Image imgMain = new javax.swing.ImageIcon(LagisApp.class.getResource(
                "/de/cismet/lagis/ressource/icons/main.png")).getImage();
    // private final Icon icoUnknown = new
    // javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/unknown.png")); TODO
    // sollte eigentlich alles in den LagisBroker ?? Configuration
    private static ConfigurationManager configManager = new ConfigurationManager();
    private static final String LAGIS_CONFIGURATION_FILE = "defaultLagisProperties.xml";
    private static final String LOCAL_LAGIS_CONFIGURATION_FILE = "lagisProperties.xml";
    private static final String LAGIS_CONFIGURATION_CLASSPATH = "/de/cismet/lagis/configuration/";
    private static final String LAGIS_LOCAL_CONFIGURATION_FOLDER = ".lagis";
    private static final String USER_HOME_DIRECTORY = System.getProperty("user.home");
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    // userdependingConfiguration
    // TODO Auslagern in configFile
    private static final String LAGIS_CONFIGURATION_FOLDER = USER_HOME_DIRECTORY + FILE_SEPARATOR + ".lagis";
    private static final String DEFAULT_LAYOUT_PATH = LAGIS_CONFIGURATION_FOLDER + "/lagis.layout";
    private static final String PLUGIN_LAYOUT_PATH = LAGIS_CONFIGURATION_FOLDER + "/pluginLagis.layout";

    private static final String DEFAULT_APP_DATA_PATH = LAGIS_CONFIGURATION_FOLDER + "/lagis.data";

    private static String onlineHelpURL;
    private static String newsURL;
    private static String userAcount = null;

    static {
        configManager.setDefaultFileName(LAGIS_CONFIGURATION_FILE);
        configManager.setFileName(LOCAL_LAGIS_CONFIGURATION_FILE);

        // if (!plugin) {
        // configManager.setFileName("configuration.xml");
        //
        // } else {
        // configManager.setFileName("configurationPlugin.xml");
        // configManager.addConfigurable(metaSearch);
        // }
        configManager.setClassPathFolder(LAGIS_CONFIGURATION_CLASSPATH);
        configManager.setFolder(LAGIS_LOCAL_CONFIGURATION_FOLDER);
    }

    // End of variables declaration
    private static final int ICON_SIZE = 8;
    private static final Icon VIEW_ICON = new Icon() {

            @Override
            public int getIconHeight() {
                return ICON_SIZE;
            }

            @Override
            public int getIconWidth() {
                return ICON_SIZE;
            }

            @Override
            public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
                final Color oldColor = g.getColor();

                g.setColor(new Color(70, 70, 70));
                g.fillRect(x, y, ICON_SIZE, ICON_SIZE);

                g.setColor(new Color(100, 230, 100));
                g.fillRect(x + 1, y + 1, ICON_SIZE - 2, ICON_SIZE - 2);

                g.setColor(oldColor);
            }
        };

    private static final String WIDGET_NAME = "Lagis Mainframe";
    // TODO VERDIS COPY
    private static Image banner = new javax.swing.ImageIcon(LagisApp.class.getResource(
                "/de/cismet/lagis/ressource/image/login.png")).getImage();

    private static final String HEADER_ERR_MSG = "Fehler";

    //~ Instance fields --------------------------------------------------------

    private RootWindow rootWindow;
    // Panels
    private VerwaltungsPanel pFlurstueck;
    private NKFOverviewPanel pNKFOverview;
    private DMSPanel pDMS;
    private KartenPanel pKarte;
    private HistoryPanel pHistory;
    private NKFPanel pNKF;
    private ReBePanel pRechteDetail;
    private VertraegePanel pVertraege;
    private InformationPanel pInfromation;
    // Views
    private View vFlurstueck;
    private View vFlurstueckTable;
    private View vVertraege;
    private View vNKFOverview;
    private View vDMS;
    private View vBelastungDetail;
    private View vBelastungTable;
    private View vKarte;
    private View vNKF;
    private View vReBe;
    private View vHistory;
    private View vInformation;
    private WFSFormFactory wfsFormFactory = WFSFormFactory.getInstance(LagisBroker.getInstance().getMappingComponent());
    private Set<View> wfsFormViews = new HashSet<View>();
    private Vector<View> wfs = new Vector<View>();
    private DockingWindow[] wfsViews;
    // private View vAktenzeichenSuche;
    private JDialog aktenzeichenDialog;
    private Icon icoKarte = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/titlebar/karte.png"));
    private Icon icoRechteTable = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/titlebar/tablegreen.png"));
    private Icon icoBelastungenTable = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/titlebar/tablered.png"));
    private Icon icoFlurstueckTable = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/titlebar/tablebase.png"));
    private Icon icoDMS = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/titlebar/docs.png"));
    private Icon icoRessort = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/titlebar/ressort.png"));
    private Icon icoAktenzeichenSuche = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/Aktenzeichensuche3.png"));
    private Icon icoNKF = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/titlebar/sum.png"));
    private Icon icoFlurstueckDetail = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/titlebar/findbase.png"));
    private Icon icoBelastungenDetail = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/titlebar/findred.png"));
    private Icon icoRechteDetail = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/titlebar/findgreen.png"));
    private Icon icoFlurstrueck = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/titlebar/flurstueck.png"));
    private Icon icoDokumente = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/titlebar/documents.png"));
    private Icon icoInformation = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/info.png"));
    private ImageIcon wizardImage;
    private Icon miniBack = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/menue/miniBack.png"));
    private Icon current = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/menue/current.png"));
    private Icon miniForward = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/menue/miniForward.png"));
    // Warum nicht in FlurstueckChooser
    private final Icon icoStaedtisch = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/current.png"));
    private final Icon icoStaedtischHistoric = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/historic.png"));
    private final Icon icoAbteilungIX = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/abteilungIX.png"));
    private final Icon icoAbteilungIXHistoric = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/historic_abteilungIX.png"));
    // ICON ÄNDERN
    private final Icon icoUnknownFlurstueck = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/unkownFlurstueck.png"));
    private MappingComponent mapComponent;
    private ClipboardWaitDialog clipboarder;
    private AppletContext appletContext;
    private StringViewMap viewMap = new StringViewMap();
    // clipboard
    private Object clipboard = null;
    private boolean clipboardPasted = true; // wegen des ersten mals
    private final ArrayList<Feature> copiedFeatures = new ArrayList<Feature>();
    private EJBAccessor<KassenzeichenFacadeRemote> verdisCrossoverAccessor;
    // FIXME ugly winning
    private ActiveLayerModel mappingModel = new ActiveLayerModel();
    private Vector widgets = new Vector();
    private boolean isInit = true;
    // Ressort
    private Set<View> ressortViews = new HashSet<View>();
    private DockingWindow[] ressortDockingWindow;
    // Plugin Navigator
    private final PluginContext context;
    private ArrayList<JMenuItem> menues = new ArrayList<JMenuItem>();
    private boolean isPlugin = false;
    // the main thread should wait till the result of the login is computated
    private boolean loginWasSuccessful = false;
    private Thread sleepingMainThread = null;
    private String albURL;
    // Configurable
    private Dimension windowSize = null;
    private Point windowLocation = null;
    // private Thread refresherThread;
    private BackgroundUpdateThread<Flurstueck> updateThread;
    private Flurstueck currentFlurstueck;
    // Validation
    private final ArrayList<ValidationStateChangedListener> validationListeners =
        new ArrayList<ValidationStateChangedListener>();
    private String validationMessage = "Die Komponente ist valide";
//        public static void main(String args[]) {
//        Thread t=new Thread(){
//            public void run() {
//            final LagisApp app = new LagisApp();
//                SwingUtilities.invokeLater(new Runnable() {
//                    public void run() {
//                        ((JFrame)app).setVisible(true);
//                    }
//                });
//            }
//        };
//        t.setPriority(Thread.NORM_PRIORITY);
//        t.start();
//    }
    // Variables declaration - do not modify
    private javax.swing.JButton btnAcceptChanges;
    private javax.swing.JButton btnAktenzeichenSuche;
    private javax.swing.JButton btnDiscardChanges;
    private javax.swing.JButton btnOpenWizard;
    private javax.swing.JButton btnReloadFlurstueck;
    private javax.swing.JButton btnSwitchInEditmode;
    private javax.swing.JButton btnVerdisCrossover;
    private javax.swing.JButton cmdCopyFlaeche;
    private javax.swing.JButton cmdPasteFlaeche;
    private javax.swing.JButton cmdPrint;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JMenu menBookmarks;
    private javax.swing.JMenu menEdit;
    private javax.swing.JMenu menExtras;
    private javax.swing.JMenu menFile;
    private javax.swing.JMenu menHelp;
    private javax.swing.JMenu menHistory;
    private javax.swing.JMenu menWindow;
    private javax.swing.JMenuItem mniAbout;
    private javax.swing.JMenuItem mniAddBookmark;
    private javax.swing.JMenuItem mniBack;
    private javax.swing.JMenuItem mniBookmarkManager;
    private javax.swing.JMenuItem mniBookmarkSidebar;
    private javax.swing.JMenuItem mniClippboard;
    private javax.swing.JMenuItem mniClose;
    private javax.swing.JMenuItem mniDMS;
    private javax.swing.JMenuItem mniForward;
    private javax.swing.JMenuItem mniGotoPoint;
    private javax.swing.JMenuItem mniHistory;
    private javax.swing.JMenuItem mniHistorySidebar;
    private javax.swing.JMenuItem mniHome;
    private javax.swing.JMenuItem mniInformation;
    private javax.swing.JMenuItem mniLisences;
    private javax.swing.JMenuItem mniLoadLayout;
    private javax.swing.JMenuItem mniLockLayout;
    private javax.swing.JMenuItem mniMap;
    private javax.swing.JMenuItem mniNKFOverview;
    private javax.swing.JMenuItem mniNews;
    private javax.swing.JMenuItem mniNutzung;
    private javax.swing.JMenuItem mniOnlineHelp;
    private javax.swing.JMenuItem mniOptions;
    private javax.swing.JMenuItem mniPrint;
    private javax.swing.JMenuItem mniReBe;
    private javax.swing.JMenuItem mniRefresh;
    private javax.swing.JMenuItem mniResetWindowLayout;
    private javax.swing.JMenuItem mniSaveLayout;
    private javax.swing.JMenuItem mniScale;
    private javax.swing.JMenuItem mniVersions;
    private javax.swing.JMenuItem mniVerwaltungsbereich;
    private javax.swing.JMenuItem mniVorgaenge;
    private javax.swing.JMenuBar mnuBar;
    private de.cismet.lagis.gui.panels.FlurstueckChooser pFlurstueckChooser;
    private javax.swing.JPanel panAll;
    private javax.swing.JPanel panMain;
    private javax.swing.JPanel panStatusbar;
    private javax.swing.JSeparator sepAfterPos;
    private javax.swing.JSeparator sepBeforePos;
    private javax.swing.JToolBar toolbar;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LagisApp object.
     */
    public LagisApp() {
        this(null);
    }

    /**
     * Creates new form Lagis.
     *
     * @param  context  DOCUMENT ME!
     */
    public LagisApp(final PluginContext context) {
//        final String serverPort = "amy";
//        System.out.println("server: " + serverPort);
//        final String iiopPort = "61744";
//        System.out.println("IIOP Port: " + iiopPort);
//        try {
//            final KassenzeichenFacadeRemote verdisServer = EJBAccessor.createEJBAccessor(serverPort, iiopPort, KassenzeichenFacadeRemote.class).getEjbInterface();
//        } catch (NamingException ex) {
//            Exceptions.printStackTrace(ex);
//        }
        this.context = context;
        try {
            EJBroker.setMainframe(this);
            isPlugin = !(context == null);
            setTitle("LagIS");
            // setUndecorated(true);
            // TODO no hardcoding
            ///DoesentWork
            System.setProperty("com.sun.corba.ee.transport.ORBTCPConnectTimeouts", "250:3000:100:2000");
//            System.setProperty("org.omg.CORBA.ORBInitialHost","172.16.20.221");
//            System.setProperty("org.omg.CORBA.ORBInitialPort","48622");
//            System.setProperty("org.omg.CORBA.ORBInitialHost","192.168.100.2");
//            System.setProperty("org.omg.CORBA.ORBInitialPort","3700");
//            if (context == null) {
//                initLog4J();
//            }

            LOG.info("Starten der LaGIS Applikation");
            if (LOG.isDebugEnabled()) {
                LOG.debug("Ist Plugin: " + isPlugin);
                // System.out.println(System.getProperty("user.dir")); File test; test = new File("lagis.log");
                // System.out.println(test.getAbsolutePath()); wizardImage = new
                // javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/image/wizard.png"));
            }

            try {
                if ((context != null) && (context.getEnvironment() != null)
                            && this.context.getEnvironment().isProgressObservable()) {
                    this.context.getEnvironment().getProgressObserver().setProgress(0, "LagIS Plugin laden...");
                }
            } catch (Exception e) {
                System.err.print("Keine Progressmeldung");
                e.printStackTrace();
            }
            // System.setProperty("wizard.sidebar.image",wizardImage.toString());
            UIManager.put(
                "wizard.sidebar.image",
                ImageIO.read(getClass().getResource("/de/cismet/lagis/ressource/image/wizard.png")));
            if ((context != null) && (context.getEnvironment() != null)
                        && this.context.getEnvironment().isProgressObservable()) {
                this.context.getEnvironment().getProgressObserver().setProgress(50, "Konfiguriere Wizard...");
            }
            // TODO FIX
            this.addWindowListener(this);
            LOG.info("Laden der Lagis Konfiguration");
            if (LOG.isDebugEnabled()) {
                LOG.debug("Name des Lagis Server Konfigurationsfiles: " + LAGIS_CONFIGURATION_FILE);
            }

            configManager.addConfigurable(this);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Konfiguriere Karten Widget");
            }

            if (isPlugin) {
                loginWasSuccessful = true;
                try {
                    final ConnectionSession session = SessionManager.getSession();
                    final User user = session.getUser();

                    LagisBroker.getInstance().setSession(session);
                    final String userString = user.getName() + "@" + user.getUserGroup().getName();
                    // usergroup.toString is most likely not what the usergroup represents within the system but a
                    // human readeable representation suited for logging purposes
                    // usergroup.getName is probably what you want
                    final String userGroup = user.getUserGroup().toString();

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("userstring: " + userString);
                        LOG.debug("userGroup: " + userGroup);
                    }

                    LagisBroker.getInstance().setAccountName(userString);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("full qualified username: " + userString + "@" + user.getUserGroup().getDomain());
                    }
                    // TODO: I don't get why there are those numerous calls to configure all over the code.
                    // configuration should be done once (!) and especially the leaking 'this' in this case is higly
                    // error prone because the configuration manager uses 'this' (currently being built) object. Thus
                    // the configuration stuff is done on a partially constructed object. VERY NASTY
                    configManager.configure(LagisApp.this);
                    final Boolean permission = LagisBroker.getInstance().getPermissions().get(userGroup.toLowerCase());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Permissions Hashmap: " + LagisBroker.getInstance().getPermissions());
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Permission: " + permission);
                    }
                    if ((permission != null) && permission) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Authentication successfull user has granted readwrite access");
                        }
                        // TODO strange names
                        LagisBroker.getInstance().setCoreReadOnlyMode(false);
                        LagisBroker.getInstance().setFullReadOnlyMode(false);
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Authentication successfull user has granted readonly access");
                        }
                    }
                    // TODOTest
                    // pDMS.setAppletContext(context.getEnvironment().getAppletContext());
                    // java.lang.Runtime.getRuntime().addShutdownHook(hook)
                } catch (Throwable t) {
                    LOG.error("Fehler im PluginKonstruktor", t);
                }
            } else {
                setIconImage(imgMain);
//                handleLogin();
//                if (!loginWasSuccessful) {
//                    log.warn("Login fehlgeschlagen --> beende LagIS");
//                    System.exit(0);
//                }
            }

            if (LagisBroker.getInstance().getSession() != null) {
                // error prone !!! see above
                configManager.configure(this);
            } else {
                LOG.fatal("Es ist kein ordentlich angemeldeter usernamen vorhanden LagIS wird beendet");
                System.exit(0);
            }

            // TODO !!!Blocker
            // while(!loginWasSuccessful);
            // if there is a userdepending config file, the configuration will be overwritten
            if ((context != null) && (context.getEnvironment() != null)
                        && this.context.getEnvironment().isProgressObservable()) {
                this.context.getEnvironment()
                        .getProgressObserver()
                        .setProgress(100, "Aufbau der Verbindung zum LagisServer...");
            }
            configManager.configure(LagisBroker.getInstance());
            if ((context != null) && (context.getEnvironment() != null)
                        && this.context.getEnvironment().isProgressObservable()) {
                this.context.getEnvironment()
                        .getProgressObserver()
                        .setProgress(200, "Initialisieren der graphischen Oberfläche...");
            }
            initComponents();
            initCismetCommonsComponents();
            if (!LagisBroker.getInstance().isCoreReadOnlyMode()) {
                btnOpenWizard.setEnabled(true);
            } else {
                btnOpenWizard.setEnabled(false);
            }
            if ((context != null) && (context.getEnvironment() != null)
                        && this.context.getEnvironment().isProgressObservable()) {
                this.context.getEnvironment()
                        .getProgressObserver()
                        .setProgress(250, "Laden und konfigurieren der Ressorterweiterugnen...");
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Konfiguriere ALBListener");
            }
            final CustomFeatureInfoListener cfil = (CustomFeatureInfoListener)mapComponent.getInputListener(
                    MappingComponent.CUSTOM_FEATUREINFO);
            cfil.setFeatureInforetrievalUrl(albURL);

            if (isPlugin) {
                menues.add(menFile);
                menues.add(menEdit);
                menues.add(menHistory);
                menues.add(menBookmarks);
                menues.add(menExtras);
                menues.add(menWindow);
            }
            initRessortPanels();
            if ((context != null) && (context.getEnvironment() != null)
                        && this.context.getEnvironment().isProgressObservable()) {
                this.context.getEnvironment()
                        .getProgressObserver()
                        .setProgress(300, "Laden und konfigurieren der Standardfenster...");
            }
            initDefaultPanels();

            // TODO LAGISBROKER KNOWS THAT THIS FLURstück IS A REQUESTER
            // OVER INTERFACE
            // LagisBroker.getInstance().setRequester(pFlurstueckSearch);
            LagisBroker.getInstance().setRequester(pFlurstueckChooser);
            btnAcceptChanges.setEnabled(false);
            btnDiscardChanges.setEnabled(false);
            btnSwitchInEditmode.setEnabled(false);
            if ((context != null) && (context.getEnvironment() != null)
                        && this.context.getEnvironment().isProgressObservable()) {
                this.context.getEnvironment()
                        .getProgressObserver()
                        .setProgress(350, "Layout der graphischen Benutzeroberflächee");
            }

            configManager.addConfigurable(wfsFormFactory);
            configManager.configure(wfsFormFactory);

            final Set<String> keySet = wfsFormFactory.getForms().keySet();
            final JMenu wfsFormsMenu = new JMenu("Finde & Zeichne");
            if (LOG.isDebugEnabled()) {
                LOG.debug("configuriere WFSForms");
            }
            for (final String key : keySet) {
                // View
                final AbstractWFSForm form = wfsFormFactory.getForms().get(key);
                form.setMappingComponent(LagisBroker.getInstance().getMappingComponent());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("WFSForms: key,form" + key + "," + form);
                }
                final View formView = new View(form.getTitle(),
                        Static2DTools.borderIcon(form.getIcon(), 0, 3, 0, 1),
                        form);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("WFSForms: formView" + formView);
                }
                viewMap.addView(form.getId(), formView);
                wfsFormViews.add(formView);
                wfs.add(formView);
                // Menu
                final JMenuItem menuItem = new JMenuItem(form.getMenuString());
                menuItem.setIcon(form.getIcon());
                menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("showOrHideView:" + formView);
                            }
                            showOrHideView(formView);
                        }
                    });
                wfsFormsMenu.add(menuItem);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("wfsFormView.size: " + wfsFormViews.size());
            }
            wfsViews = new DockingWindow[wfsFormViews.size()];
            for (int i = 0; i < wfsViews.length; i++) {
                wfsViews[i] = wfs.get(i);
            }

            if (keySet.size() > 0) {
                menues.remove(menHelp);
                menues.add(wfsFormsMenu);
                menues.add(menHelp);

                mnuBar.remove(menHelp);
                mnuBar.add(wfsFormsMenu);
                mnuBar.add(menHelp);
            }

            initInfoNode();
            loadLagisConfiguration();
//            java.awt.EventQueue.invokeLater(new Runnable() {
//                    public void run() {
            doLayoutInfoNode();
//                    }
//                });
            doConfigKeystrokes();
            panMain.add(rootWindow, BorderLayout.CENTER);
            // ToDo if the setting of the windowsize is not successful pack should be used ?
            // pack();
            initKeySearchComponents();
            mapComponent.getFeatureCollection().addFeatureCollectionListener(pFlurstueck);
            //
            // TODO is it wise to change or switch to the inital boundingbox after setting the layout --> more requests
            if ((context != null) && (context.getEnvironment() != null)
                        && this.context.getEnvironment().isProgressObservable()) {
                this.context.getEnvironment()
                        .getProgressObserver()
                        .setProgress(550, "Konfigurieren der Kartenkomponente...");
            }
            setWindowSize();
            // mapComponent.gotoInitialBoundingBox();
            mapComponent.setInternalLayerWidgetAvailable(true);
            // vKarte.doLayout();
            validateTree();
            // mapComponent.unlock();
// JDialog test = new JDialog(this,"Testdialog",true);
// test.add(new JTextField());
// test.add(new JTextField());
// test.add(new JTextField());
// test.pack();
// test.setVisible(true);
            // TODO + shutdownhook setactive etc
            // if (plugin) {
            // DockingManager.setDefaultPersistenceKey("pluginPerspectives.xml");
            // loadLayout(pluginPathname);

            // } else {
            // DockingManager.setDefaultPersistenceKey("cismapPerspectives.xml");
            for (final Scale s : mapComponent.getScales()) {
                if (s.getDenominator() > 0) {
                    menExtras.add(getScaleMenuItem(s.getText(), s.getDenominator()));
                }
            }

            if (isPlugin) {
                loadLayout(PLUGIN_LAYOUT_PATH);
            } else {
                loadLayout(DEFAULT_LAYOUT_PATH);
            }

            // }
            if ((context != null) && (context.getEnvironment() != null)
                        && this.context.getEnvironment().isProgressObservable()) {
                this.context.getEnvironment()
                        .getProgressObserver()
                        .setProgress(650, "Konfigurieren der Suchkomponente...");
            }
            configManager.addConfigurable(pFlurstueck);
            configManager.configure(pFlurstueck);

            configManager.addConfigurable(pNKF);
            configManager.configure(pNKF);

            configManager.addConfigurable(pKarte);
            configManager.configure(pKarte);

            LOG.info("Konstruktion des LaGIS Objektes erfolgreich");
            Runtime.getRuntime().addShutdownHook(new Thread() {

                    @Override
                    public void run() {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("ShutdownHook gestartet");
                        }
                        cleanUp();
                    }
                });
            LagisBroker.getInstance().setParentComponent(this);
            // TODO GEHT SCHIEF WENN ES SCHON DER PARENTFRAME IST
            clipboarder = new ClipboardWaitDialog(StaticSwingTools.getParentFrame(this), true);
            final StatusBar statusBar = new StatusBar(mapComponent);
            LagisBroker.getInstance().setStatusBar(statusBar);
            mapComponent.getFeatureCollection().addFeatureCollectionListener(statusBar);
            CismapBroker.getInstance().addStatusListener(statusBar);
            panStatusbar.add(statusBar);
            if ((context != null) && (context.getEnvironment() != null)
                        && this.context.getEnvironment().isProgressObservable()) {
                this.context.getEnvironment()
                        .getProgressObserver()
                        .setProgress(850, "Initialisieren und Starten des Hintergrundthreads...");
            }
            updateThread = new BackgroundUpdateThread<Flurstueck>() {

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
                            try {
                                final FlurstueckArt flurstuecksArt = getCurrentObject().getFlurstueckSchluessel()
                                            .getFlurstueckArt();
                                if (flurstuecksArt.getBezeichnung().equals(
                                                FlurstueckArt.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("Art des Flurstücks ist Städtisch");
                                    }
                                    if (getCurrentObject().getFlurstueckSchluessel().getGueltigBis() != null) {
                                        pFlurstueckChooser.setStatusIcon(icoStaedtischHistoric);
                                    } else {
                                        pFlurstueckChooser.setStatusIcon(icoStaedtisch);
                                    }
                                } else if (flurstuecksArt.getBezeichnung().equals(
                                                FlurstueckArt.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX)) {
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("Art des Flurstücks ist Abteilung IX");
                                    }
                                    if (getCurrentObject().getFlurstueckSchluessel().getGueltigBis() != null) {
                                        pFlurstueckChooser.setStatusIcon(icoAbteilungIXHistoric);
                                    } else {
                                        pFlurstueckChooser.setStatusIcon(icoAbteilungIX);
                                    }
                                } else {
                                    LOG.warn("Art des Flurstücks nicht bekannt");
                                }
                            } catch (Exception ex) {
                                LOG.error("Fehler beim bestimmen der Flurstücksart");
                            }
                            // datamodell refactoring 22.10.07
                            if (getCurrentObject().getFlurstueckSchluessel().isGesperrt()
                                        && (getCurrentObject().getFlurstueckSchluessel().getGueltigBis() == null)) {
                                LOG.info("Flurstück ist gesperrt");
                                rootWindow.getRootWindowProperties()
                                        .getViewProperties()
                                        .getViewTitleBarProperties()
                                        .getNormalProperties()
                                        .getShapedPanelProperties()
                                        .setComponentPainter(new GradientComponentPainter(
                                                LagisBroker.LOCK_MODE_COLOR,
                                                new Color(236, 233, 216),
                                                LagisBroker.LOCK_MODE_COLOR,
                                                new Color(236, 233, 216)));
                                if (!LagisBroker.getInstance().isFullReadOnlyMode()) {
                                    btnSwitchInEditmode.setEnabled(true);
                                    if (!LagisBroker.getInstance().isCoreReadOnlyMode()) {
                                        btnOpenWizard.setEnabled(true);
                                    }
                                }
                                // datamodell refactoring 22.10.07
                            } else if (getCurrentObject().getFlurstueckSchluessel().getGueltigBis() != null) {
                                LOG.info("Flurstück ist historisch");
                                rootWindow.getRootWindowProperties()
                                        .getViewProperties()
                                        .getViewTitleBarProperties()
                                        .getNormalProperties()
                                        .getShapedPanelProperties()
                                        .setComponentPainter(new GradientComponentPainter(
                                                LagisBroker.HISTORY_MODE_COLOR,
                                                new Color(236, 233, 216),
                                                LagisBroker.HISTORY_MODE_COLOR,
                                                new Color(236, 233, 216)));

                                btnSwitchInEditmode.setEnabled(LagisBroker.getInstance().isNkfAdminPermission());

                                if (!LagisBroker.getInstance().isCoreReadOnlyMode()) {
                                    btnOpenWizard.setEnabled(true);
                                }
                            } else {
                                LOG.info("Flurstück ist normal");
                                rootWindow.getRootWindowProperties()
                                        .getViewProperties()
                                        .getViewTitleBarProperties()
                                        .getNormalProperties()
                                        .getShapedPanelProperties()
                                        .setComponentPainter(new GradientComponentPainter(
                                                LagisBroker.DEFAULT_MODE_COLOR,
                                                new Color(236, 233, 216),
                                                LagisBroker.DEFAULT_MODE_COLOR,
                                                new Color(236, 233, 216)));
                                if (!LagisBroker.getInstance().isFullReadOnlyMode()) {
                                    btnSwitchInEditmode.setEnabled(true);
                                    if (!LagisBroker.getInstance().isCoreReadOnlyMode()) {
                                        btnOpenWizard.setEnabled(true);
                                    }
                                }
                            }
                            if (isUpdateAvailable()) {
                                cleanup();
                                return;
                            }
                            LagisBroker.getInstance().flurstueckChangeFinished(LagisApp.this);
                        } catch (Exception ex) {
                            LOG.error("Fehler im refresh thread: ", ex);
                            LagisBroker.getInstance().flurstueckChangeFinished(LagisApp.this);
                        }
                    }

                    @Override
                    protected void cleanup() {
                    }
                };
            updateThread.setPriority(Thread.NORM_PRIORITY);
            updateThread.start();

            isInit = false;
            if ((context != null) && (context.getEnvironment() != null)
                        && this.context.getEnvironment().isProgressObservable()) {
                this.context.getEnvironment()
                        .getProgressObserver()
                        .setProgress(1000, "LagIS Initalisierung abgeschlossen");
            }
            if ((context != null) && (context.getEnvironment() != null)
                        && context.getEnvironment().isProgressObservable()) {
                this.context.getEnvironment().getProgressObserver().setFinished(true);
            }

//            //if there are no editable widgets --> set editable is set to false
//            if(LagisBroker.getInstance().getPermissions().keySet().size() > 0){
//                for(String name:LagisBroker.getInstance().getPermissions().keySet()){
//                    Boolean isWritable = LagisBroker.getInstance().getPermissions().get(name);
//                    if(isWritable != null && isWritable){
//                        log.debug("");
//                        LagisBroker.getInstance().setFullReadOnlyMode(false);
//                        break;
//                    }
//                }
//            }
            pKarte.setInteractionMode();
            setVisible(true);
            EJBroker.setMainframe(this);
            // check if there ist at least one editable Widget
            if (LagisBroker.getInstance().isCoreReadOnlyMode()
                        && (RessortFactory.getInstance().getRessortPermissions() != null)) {
                final HashMap<Widget, Boolean> ressortPermission = RessortFactory.getInstance().getRessortPermissions();
                if (ressortPermission != null) {
                    for (final Widget tmp : ressortPermission.keySet()) {
                        final Boolean isReadOnly = ressortPermission.get(tmp);
                        if ((isReadOnly != null) && !isReadOnly) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Mindestens ein Widget kann editiert werden");
                            }
                            LagisBroker.getInstance().setFullReadOnlyMode(false);
                            break;
                        }
                    }
                }
            }
            mapComponent.unlock();
        } catch (Exception ex) {
            LOG.fatal("Fehler beim konstruieren des LaGIS Objektes", ex);
        }

        this.loadAppData(DEFAULT_APP_DATA_PATH);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setVisible(final boolean visible) {
        if (isPlugin) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Plugin setVisible ignoriert: " + visible);
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Kein Plugin super.setVisible: " + visible);
            }
            super.setVisible(visible);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private static void handleLogin() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Intialisiere Loginframe");
        }

        // TODO VERDIS COPY
        // Thread t=new Thread(new Runnable(){
        // public void run() {
        final DefaultUserNameStore usernames = new DefaultUserNameStore();
        final Preferences appPrefs = Preferences.userNodeForPackage(LagisApp.class);
        usernames.setPreferences(appPrefs.node("login"));
        // TODO: parse and validate arguments --> cli tools recommended
        final LagisApp.WundaAuthentification wa = new LagisApp.WundaAuthentification(LagisBroker.getInstance()
                        .getDomain(),
                LagisBroker.getInstance().getCallserverHost());

        final JXLoginPane login = new JXLoginPane(wa, null, usernames) {

                @Override
                protected Image createLoginBanner() {
                    return getBannerImage();
                }
            };

        String u = null;
        try {
            u = usernames.getUserNames()[usernames.getUserNames().length - 1];
        } catch (Exception skip) {
        }
        if (u != null) {
            login.setUserName(u);
        }
        // final JXLoginPanel.JXLoginDialog d=new JXLoginPanel.JXLoginDialog(LagisApp.this,login);
        final JFrame dummy = null;
        final JXLoginPane.JXLoginFrame d = new JXLoginPane.JXLoginFrame(login);
        // final JXLoginPanel.JXLoginDialog d = new JXLoginPanel.JXLoginDialog(dummy,login);

        d.addComponentListener(new ComponentAdapter() {

                @Override
                public void componentHidden(final ComponentEvent e) {
                    handleLoginStatus(d.getStatus(), usernames, login);
                }
            });
        d.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosed(final WindowEvent e) {
                    handleLoginStatus(d.getStatus(), usernames, login);
                }
            });
        d.setIconImage(imgMain);
//                    SwingUtilities.invokeLater(new Runnable() {

        login.setPassword("".toCharArray());
        // d.setLocationRelativeTo(LagisApp.this);
        try {
            ((JXPanel)((JXPanel)login.getComponent(1)).getComponent(1)).getComponent(3).requestFocus();
        } catch (Exception skip) {
        }
        d.setIconImage(imgMain);
        d.setAlwaysOnTop(true);
        d.setVisible(true);
//        final Thread loginThread = new Thread(new Runnable() {
//
//            public void run() {
//                while (d.isVisible()) {
//                    try {
//                        Thread.currentThread().sleep(100);
//                    } catch (InterruptedException ex) {
//                        log.fatal("Thread wurde interrupted", ex);
//                    }
//                }
//            }
//        });

//        try {
////            EventQueue.
////            d.setVisible(true);
////            loginThread.start();
////            loginThread.join();
//        } catch (InterruptedException ex) {
//            log.fatal("Fehler !", ex);
//        } catch (InvocationTargetException ex){
//            log.fatal("Fehler !", ex);
//        }
        // while(true);
//                        }
        // });

        // }
        // });
        // t.setPriority(Thread.NORM_PRIORITY);
        // t.start();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   t  DOCUMENT ME!
     * @param   d  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JMenuItem getScaleMenuItem(final String t, final int d) {
        final JMenuItem jmi = new JMenuItem(t);
        jmi.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    mapComponent.gotoBoundingBoxWithHistory(mapComponent.getBoundingBoxFromScale(d));
                }
            });
        return jmi;
    }

    /**
     * DOCUMENT ME!
     */
    private void loadLagisConfiguration() {
        mappingModel.setInitalLayerConfigurationFromServer(true);
        configManager.addConfigurable((ActiveLayerModel)mappingModel);
        configManager.addConfigurable(mapComponent);
        // configManager.addConfigurable(pFlurstueckSearch);
        configManager.addConfigurable(pFlurstueckChooser);

        try {
            validateTree();
        } catch (final Throwable t) {
            java.awt.EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        LOG.warn("Fehler in validateTree()", t);
                        validateTree();
                    }
                });
        }
        final boolean serverFirst;
        // if (serverFirst) {
        // configManager.configureFromClasspath();
        // } else {
        // First local configuration than serverconfiguration
        configManager.configure(mappingModel);
        mapComponent.preparationSetMappingModel(mappingModel);
        configManager.configure(mapComponent);
        mapComponent.setMappingModel(mappingModel);
        // configManager.configure(pFlurstueckSearch);
        // configManager.configure(pFlurstueckChooser);
        configManager.configure(WFSRetrieverFactory.getInstance());
        LagisBroker.getInstance().checkNKFAdminPermissionsOnServer();
        // }
        // setButtonSelectionAccordingToMappingComponent();
    }

    /**
     * DOCUMENT ME!
     */
    private void initCismetCommonsComponents() {
        mapComponent = new MappingComponent();
        mapComponent.addHistoryModelListener(this);
        CismapBroker.getInstance().setMappingComponent(mapComponent);
        LagisBroker.getInstance().setMappingComponent(mapComponent);
        // mapComponent.setReadOnly(false);
        // pKarte.add(mapComponent);
    }

    /**
     * DOCUMENT ME!
     */
    private static void initLog4J() {
        try {
            // System.out.println();
            // PropertyConfigurator.configure(ClassLoader.getSystemResource("de/cismet/lagis/ressource/log4j/log4j.properties"));
            PropertyConfigurator.configure(LagisApp.class.getResource(
                    "/de/cismet/lagis/configuration/log4j.properties"));
            LOG.info("Log4J System erfolgreich konfiguriert");
        } catch (Exception ex) {
            System.err.println("Fehler bei Log4J Initialisierung");
            ex.printStackTrace();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initRessortPanels() {
        // init ressort
        try {
            final RessortFactory ressortFactory = RessortFactory.getInstance();
            // TODO warum gibt es addConfigurabe und configure
            // warum wird das beim configure nicht geadded ??? nachschauen
            configManager.addConfigurable(ressortFactory);
            configManager.configure(ressortFactory);
            final HashMap<String, AbstractWidget> ressorts = ressortFactory.getRessorts();
            if (ressorts.size() > 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Anzahl Ressort Widget: " + ressorts.size());
                }
                final JMenu ressortMenue = new JMenu("Ressorts");
                final Set<String> keySet = ressorts.keySet();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ressort Keyset: " + keySet);
                }
                for (final String key : keySet) {
                    try {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Aktueller Key " + key);
                        }
                        final AbstractWidget ressort = ressorts.get(key);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Aktueller Name des RessortWidgets " + ressort.getWidgetName());
                        }
                        final View ressortView = new View(ressort.getWidgetName(),
                                Static2DTools.borderIcon(ressort.getWidgetIcon(), 0, 3, 0, 1),
                                ressort);
                        viewMap.addView(ressort.getWidgetName(), ressortView);
                        ressortViews.add(ressortView);
                        widgets.add(ressort);
                        // TODO Does I need a vector ??
                        // wfs.add(formView);
                        // Menu
                        final JMenuItem menuItem = new JMenuItem(ressort.getWidgetName());
                        menuItem.setIcon(ressort.getWidgetIcon());
                        menuItem.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    showOrHideView(ressortView);
                                }
                            });
                        ressortMenue.add(menuItem);
                    } catch (Exception ex) {
                        LOG.warn("Fehler beim Configurieren eines RessortWidgets: " + ex);
                    }
                }
                ressortDockingWindow = new DockingWindow[ressortViews.size()];
//            for(int i=0;i<ressortDockingWindow.length;i++){
//                wfsViews[i] = ressortViews..get(i);
//            }
                int counter = 0;
                for (final View view : ressortViews) {
                    ressortDockingWindow[counter] = view;
                    counter++;
                }
                // TODO for Navigator
// menues.remove(menHelp);
// menues.add(wfsFormsMenu);
// menues.add(menHelp);
                if (isPlugin) {
                    menues.add(ressortMenue);
                    menues.add(menHelp);
                } else {
                    menues.add(menHelp);
                }
                mnuBar.remove(menHelp);
                mnuBar.add(ressortMenue);
                mnuBar.add(menHelp);
            } else {
                ressortDockingWindow = new DockingWindow[0];
                LOG.info("Es existieren keine Ressort Widgets");
            }
        } catch (Exception ex) {
            LOG.warn("Fehler beim Configurieren der RessortWidgets: " + ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initDefaultPanels() {
        LOG.info("Initialisieren der einzelnen Komponenten");
        pFlurstueck = new VerwaltungsPanel();
        pVertraege = new VertraegePanel();
        pNKFOverview = new NKFOverviewPanel();
        pDMS = new DMSPanel();
        pKarte = new KartenPanel();
        pNKF = new NKFPanel();
        pRechteDetail = new ReBePanel();
        pHistory = new HistoryPanel();
        configManager.addConfigurable(pHistory);
        configManager.configure(pHistory);
        pInfromation = new InformationPanel();

        widgets.add(pHistory);
        widgets.add(pFlurstueck);
        widgets.add(pVertraege);
        widgets.add(pNKFOverview);
        widgets.add(pDMS);
        widgets.add(pKarte);
        widgets.add(pNKF);
        widgets.add(pRechteDetail);
        widgets.add(pInfromation);
        // widgets.add(pFlurstueckSearch);
        widgets.add(pFlurstueckChooser);

        LOG.info("Referenz auf die mainApplikation: " + this);
        widgets.add(this);
        // LagisBroker.getInstance().addResettables(widgets);
        LagisBroker.getInstance().addWidgets(widgets);
        LagisBroker.getInstance().resetWidgets();
    }

    /**
     * DOCUMENT ME!
     */
    private void initInfoNode() {
        vFlurstueck = new View("Verwaltungsbereiche", icoFlurstrueck, pFlurstueck);
        vFlurstueck.getCustomTitleBarComponents().addAll(pFlurstueck.getCustomButtons());
        viewMap.addView("Verwaltungsbereiche", vFlurstueck);

        vVertraege = new View("Vorgänge", icoDokumente, pVertraege);

        viewMap.addView("Vorgänge", vVertraege);
        vNKFOverview = new View("NKF Übersicht", icoNKF, pNKFOverview);
        viewMap.addView("NKF Übersicht", vNKFOverview);
        vDMS = new View("DMS", icoDMS, pDMS);
        viewMap.addView("DMS", vDMS);

        vKarte = new View("Karte", icoKarte, pKarte);
        viewMap.addView("Karte", vKarte);

        vNKF = new View("Nutzung", icoNKF, pNKF);
        viewMap.addView("Nutzung", vNKF);

        vReBe = new View("Rechte und Belastungen", icoRechteDetail, pRechteDetail);
        viewMap.addView("Rechte und Belastungen", vReBe);

        // TODO ICON
        vHistory = new View("Historie", icoRessort, pHistory);
        viewMap.addView("Historie", vHistory);

        //
        vInformation = new View("Information", icoInformation, pInfromation);
        viewMap.addView("Information", vInformation);

        // vAktenzeichenSuche = new View("AktenzeichenSuche",icoAktenzeichenSuche,new AktenzeichenSearch());
        // viewMap.addView("AktenzeichenSuche",vAktenzeichenSuche);
        // ToDo here wfsForms

        rootWindow = DockingUtil.createRootWindow(viewMap, true);
        LagisBroker.getInstance().setRootWindow(rootWindow);

        // InfoNode configuration
        rootWindow.addTabMouseButtonListener(DockingWindowActionMouseButtonListener.MIDDLE_BUTTON_CLOSE_LISTENER);
        // DockingWindowsTheme theme = new
        // ShapedGradientDockingTheme(0f,0.5f,UIManagerColorProvider.TABBED_PANE_DARK_SHADOW,new
        // FixedColorProvider(Color.BLUE),true);
        final DockingWindowsTheme theme = new ShapedGradientDockingTheme();
        rootWindow.getRootWindowProperties().addSuperObject(
            theme.getRootWindowProperties());

        final RootWindowProperties titleBarStyleProperties = PropertiesUtil.createTitleBarStyleRootWindowProperties();

        rootWindow.getRootWindowProperties().addSuperObject(
            titleBarStyleProperties);

        rootWindow.getRootWindowProperties().getDockingWindowProperties().setUndockEnabled(true);
        final AlphaGradientComponentPainter x = new AlphaGradientComponentPainter(
                java.awt.SystemColor.inactiveCaptionText,
                java.awt.SystemColor.activeCaptionText,
                java.awt.SystemColor.activeCaptionText,
                java.awt.SystemColor.inactiveCaptionText);
        rootWindow.getRootWindowProperties().getDragRectangleShapedPanelProperties().setComponentPainter(x);

        //
        // rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().getShapedPanelProperties().setComponentPainter(new
        // GradientComponentPainter(new Color(124,160,221),new Color(236,233,216),new Color(124,160,221),new
        // Color(236,233,216)));
        LagisBroker.getInstance().setTitleBarComponentpainter(LagisBroker.DEFAULT_MODE_COLOR);
        // rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().setOrientation(Direction.DOWN);
        // rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().setIconVisible(false);
        // rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().setTitleVisible(false);
        // rootWindow.getRootWindowProperties().getTabWindowProperties().getTabbedPanelProperties().getTabAreaProperties().getShapedPanelProperties().setDirection(Direction.DOWN);
        // rootWindow.getRootWindowProperties().getTabWindowProperties().getTabbedPanelProperties().setTabAreaOrientation(Direction.RIGHT);rootWindow.getRootWindowProperties().getTabWindowProperties().getTabbedPanelProperties().setTabAreaOrientation(Direction.RIGHT);
        // rootWindow.getRootWindowProperties().getTabWindowProperties().getTabbedPanelProperties().getDefaultProperties().getTabAreaComponentsProperties().
        rootWindow.getRootWindowProperties()
                .getTabWindowProperties()
                .getTabbedPanelProperties()
                .setPaintTabAreaShadow(true);
        rootWindow.getRootWindowProperties().getTabWindowProperties().getTabbedPanelProperties().setShadowSize(10);
        rootWindow.getRootWindowProperties()
                .getTabWindowProperties()
                .getTabbedPanelProperties()
                .setShadowStrength(0.8f);
        //
        // rootWindow.getRootWindowProperties().getTabWindowProperties().getTabbedPanelProperties().getContentPanelProperties().getComponentProperties().setBorder(new
        // DropShadowBorder(Color.BLACK,5,5,0.5f,12,true,true,false,true));
        // rootWindow.getRootWindowProperties().getTabWindowProperties().getTabbedPanelProperties().getContentPanelProperties().getComponentProperties().setBorder();
    }
    // TODO REFACTOR DEFAULT LAYOUT

    /**
     * DOCUMENT ME!
     */
    public void doLayoutInfoNode() {
        if (wfsViews.length != 0) {
            if (ressortDockingWindow.length != 0) {
                rootWindow.setWindow(new SplitWindow(
                        true,
                        0.22901994f,
                        new SplitWindow(
                            false,
                            0.38277513f,
                            vFlurstueck,
                            new SplitWindow(
                                false,
                                0.4300518f,
                                vNKFOverview,
                                new TabWindow(
                                    new DockingWindow[] { vDMS, vInformation }))),
                        new SplitWindow(
                            false,
                            0.21391752f,
                            new SplitWindow(false, 0.33f,
                                new TabWindow(wfsViews),
                                new TabWindow(ressortDockingWindow)),
                            new TabWindow(
                                new DockingWindow[] { vKarte, vReBe, vVertraege, vNKF, vHistory }))));
            } else {
                rootWindow.setWindow(new SplitWindow(
                        true,
                        0.22901994f,
                        new SplitWindow(
                            false,
                            0.38277513f,
                            vFlurstueck,
                            new SplitWindow(
                                false,
                                0.4300518f,
                                vNKFOverview,
                                new TabWindow(
                                    new DockingWindow[] { vDMS, vInformation }))),
                        new TabWindow(
                            new DockingWindow[] { vKarte, vReBe, vVertraege, vNKF, vHistory })));
            }
        } else {
            if (ressortDockingWindow.length != 0) {
                rootWindow.setWindow(new SplitWindow(
                        true,
                        0.22901994f,
                        new SplitWindow(
                            false,
                            0.38277513f,
                            vFlurstueck,
                            new SplitWindow(
                                false,
                                0.4300518f,
                                vNKFOverview,
                                new TabWindow(
                                    new DockingWindow[] { vDMS, vInformation }))),
                        new SplitWindow(
                            false,
                            0.21391752f,
                            new TabWindow(ressortDockingWindow),
                            new TabWindow(
                                new DockingWindow[] { vKarte, vReBe, vVertraege, vNKF, vHistory }))));
            } else {
                rootWindow.setWindow(new SplitWindow(
                        true,
                        0.22901994f,
                        new SplitWindow(
                            false,
                            0.38277513f,
                            vFlurstueck,
                            new SplitWindow(
                                false,
                                0.4300518f,
                                vNKFOverview,
                                new TabWindow(
                                    new DockingWindow[] { vDMS, vInformation }))),
                        new TabWindow(
                            new DockingWindow[] { vKarte, vReBe, vVertraege, vNKF, vHistory })));
            }
        }

        for (int i = 0; i < wfsViews.length; i++) {
            wfsViews[i].close();
        }

        // aktenzeichenFloatingWindow = rootWindow.createFloatingWindow(new Point(406, 175), new Dimension(300, 613),
        // vAktenzeichenSuche);
        vDMS.restoreFocus();
        vKarte.restoreFocus();
    }

    /**
     * DOCUMENT ME!
     */
    public void doConfigKeystrokes() {
        final KeyStroke showLayoutKeyStroke = KeyStroke.getKeyStroke('D', InputEvent.CTRL_MASK);
        final Action showLayoutAction = new AbstractAction() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    java.awt.EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                DeveloperUtil.createWindowLayoutFrame("Momentanes Layout", rootWindow).setVisible(true);
                            }
                        });
                }
            };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(showLayoutKeyStroke, "SHOW_LAYOUT");
        getRootPane().getActionMap().put("SHOW_LAYOUT", showLayoutAction);
//        KeyStroke layoutKeyStroke = KeyStroke.getKeyStroke('R',InputEvent.CTRL_MASK);
//        Action layoutAction = new AbstractAction(){
//            public void actionPerformed(ActionEvent e) {
//                java.awt.EventQueue.invokeLater(new Runnable() {
//                    public void run() {
//                        doLayoutInfoNode();
//                    }
//                });
//            }
//        };
//        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(layoutKeyStroke, "RESET_LAYOUT");
//        getRootPane().getActionMap().put("RESET_LAYOUT", layoutAction);
//
//        KeyStroke saveLayoutKeyStroke = KeyStroke.getKeyStroke('S',InputEvent.CTRL_MASK);
//        Action saveLayoutAction = new AbstractAction(){
//            public void actionPerformed(final ActionEvent e) {
//                java.awt.EventQueue.invokeLater(new Runnable() {
//                    public void run() {
//                        mniSaveLayoutActionPerformed(e);
//                    }
//                });
//            }
//        };
//        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(saveLayoutKeyStroke, "SAVE_LAYOUT");
//        getRootPane().getActionMap().put("SAVE_LAYOUT", saveLayoutAction);
//
//        KeyStroke loadLayoutKeyStroke = KeyStroke.getKeyStroke('O',InputEvent.CTRL_MASK);
//        Action loadLayoutAction = new AbstractAction(){
//            public void actionPerformed(final ActionEvent e) {
//                java.awt.EventQueue.invokeLater(new Runnable() {
//                    public void run() {
//                        mniLoadLayoutActionPerformed(e);
//                    }
//                });
//            }
//        };
//        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(loadLayoutKeyStroke, "LOAD_LAYOUT");
//        getRootPane().getActionMap().put("LOAD_LAYOUT", loadLayoutAction);
//
//        KeyStroke clippboardKeyStroke = KeyStroke.getKeyStroke('C',InputEvent.CTRL_MASK);
//        Action clippboardAction = new AbstractAction(){
//            public void actionPerformed(final ActionEvent e) {
//                java.awt.EventQueue.invokeLater(new Runnable() {
//                    public void run() {
//                        mniClippboardActionPerformed(e);
//                    }
//                });
//            }
//        };
//        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(clippboardKeyStroke, "CLIPPBOARD");
//        getRootPane().getActionMap().put("CLIPPBOARD", clippboardAction);
//
//        KeyStroke closeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F4,InputEvent.ALT_MASK);
//        Action closeAction = new AbstractAction(){
//            public void actionPerformed(final ActionEvent e) {
//                java.awt.EventQueue.invokeLater(new Runnable() {
//                    public void run() {
//                        mniCloseActionPerformed(e);
//                    }
//                });
//            }
//        };
//        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(closeKeyStroke, "CLOSE");
//        getRootPane().getActionMap().put("CLOSE", closeAction);
//
//        KeyStroke refreshKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F5,0);
//        Action refreshAction = new AbstractAction(){
//            public void actionPerformed(final ActionEvent e) {
//                java.awt.EventQueue.invokeLater(new Runnable() {
//                    public void run() {
//                        mniRefreshActionPerformed(e);
//                    }
//                });
//            }
//        };
//        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(refreshKeyStroke, "REFRESH");
//        getRootPane().getActionMap().put("REFRESH", refreshAction);
//
//        KeyStroke clippboardKeyStroke = KeyStroke.getKeyStroke('C',InputEvent.CTRL_MASK);
//        Action clippboardAction = new AbstractAction(){
//            public void actionPerformed(final ActionEvent e) {
//                java.awt.EventQueue.invokeLater(new Runnable() {
//                    public void run() {
//                        mniClippboardActionPerformed(e);
//                    }
//                });
//            }
//        };
//        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(clippboardKeyStroke, "CLIPPBOARD");
//        getRootPane().getActionMap().put("CLIPPBOARD", clippboardAction);

//        //TODO disable
//        KeyStroke colorKeyStroke = KeyStroke.getKeyStroke('E',InputEvent.CTRL_MASK);
//        Action colorAction = new AbstractAction(){
//            public void actionPerformed(ActionEvent e) {
//                java.awt.EventQueue.invokeLater(new Runnable() {
//                    public void run() {
//                        Color choosedColor = (new JColorChooser()).showDialog(LagisApp.this,"Bitte Farbe wählen für alle ungeraden Tabellenzeilen (Nur für Betaversion gedacht)", Color.WHITE);
//                        LagisBroker.ODD_ROW_DEFAULT_COLOR = choosedColor;
//                        LagisBroker.ODD_ROW_EDIT_COLOR = choosedColor;
//                        LagisBroker.ODD_ROW_LOCK_COLOR = choosedColor;
//                        log.debug("Gewählte farbe ist: "+choosedColor);
//                    }
//                });
//            }
//        };
//        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(colorKeyStroke, "CHOOSE_COLOR");
//        getRootPane().getActionMap().put("CHOOSE_COLOR", colorAction);
//        Set<Verwaltungsgebrauch> vg = EJBroker.getInstance().getAllVerwaltenungsgebraeuche();
//        for(Verwaltungsgebrauch current: vg){
//            Farbe currentFarbe = current.getFarben().iterator().next();
//            Color tmpColor = new Color(currentFarbe.getRgbFarbwert());
//            System.out.println(currentFarbe.getRgbFarbwert()+ " "+tmpColor.getRed()+","+tmpColor.getGreen()+","+tmpColor.getBlue());
//        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initKeySearchComponents() {
        try {
//        cboGemarkung.setModel(new KeyComboboxModel());
        } catch (Exception ex) {
            LOG.error("Fehler beim initalisieren der Flurstueck Comboboxen: ", ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        toolbar = new javax.swing.JToolBar();
        pFlurstueckChooser = new FlurstueckChooser(FlurstueckChooser.SEARCH_MODE);
        jSeparator1 = new javax.swing.JSeparator();
        btnSwitchInEditmode = new javax.swing.JButton();
        btnDiscardChanges = new javax.swing.JButton();
        btnAcceptChanges = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        cmdCopyFlaeche = new javax.swing.JButton();
        cmdPasteFlaeche = new javax.swing.JButton();
        cmdPrint = new javax.swing.JButton();
        btnReloadFlurstueck = new javax.swing.JButton();
        btnOpenWizard = new javax.swing.JButton();
        btnAktenzeichenSuche = new javax.swing.JButton();
        btnVerdisCrossover = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        panAll = new javax.swing.JPanel();
        panMain = new javax.swing.JPanel();
        panStatusbar = new javax.swing.JPanel();
        mnuBar = new javax.swing.JMenuBar();
        menFile = new javax.swing.JMenu();
        mniSaveLayout = new javax.swing.JMenuItem();
        mniLoadLayout = new javax.swing.JMenuItem();
        mniLockLayout = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        mniClippboard = new javax.swing.JMenuItem();
        mniPrint = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JSeparator();
        mniClose = new javax.swing.JMenuItem();
        menEdit = new javax.swing.JMenu();
        mniRefresh = new javax.swing.JMenuItem();
        menHistory = new javax.swing.JMenu();
        mniBack = new javax.swing.JMenuItem();
        mniForward = new javax.swing.JMenuItem();
        mniHome = new javax.swing.JMenuItem();
        sepBeforePos = new javax.swing.JSeparator();
        sepAfterPos = new javax.swing.JSeparator();
        mniHistorySidebar = new javax.swing.JMenuItem();
        menBookmarks = new javax.swing.JMenu();
        mniAddBookmark = new javax.swing.JMenuItem();
        mniBookmarkManager = new javax.swing.JMenuItem();
        mniBookmarkSidebar = new javax.swing.JMenuItem();
        menExtras = new javax.swing.JMenu();
        mniOptions = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JSeparator();
        mniGotoPoint = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JSeparator();
        mniScale = new javax.swing.JMenuItem();
        menWindow = new javax.swing.JMenu();
        mniMap = new javax.swing.JMenuItem();
        mniVerwaltungsbereich = new javax.swing.JMenuItem();
        mniVorgaenge = new javax.swing.JMenuItem();
        mniNKFOverview = new javax.swing.JMenuItem();
        mniNutzung = new javax.swing.JMenuItem();
        mniReBe = new javax.swing.JMenuItem();
        mniDMS = new javax.swing.JMenuItem();
        mniHistory = new javax.swing.JMenuItem();
        mniInformation = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JSeparator();
        mniResetWindowLayout = new javax.swing.JMenuItem();
        menHelp = new javax.swing.JMenu();
        mniOnlineHelp = new javax.swing.JMenuItem();
        mniNews = new javax.swing.JMenuItem();
        mniVersions = new javax.swing.JMenuItem();
        mniLisences = new javax.swing.JMenuItem();
        mniAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        toolbar.setRollover(true);
        toolbar.setMinimumSize(new java.awt.Dimension(496, 33));
        toolbar.add(pFlurstueckChooser);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setMaximumSize(new java.awt.Dimension(5, 32767));
        jSeparator1.setPreferredSize(new java.awt.Dimension(2, 23));
        toolbar.add(jSeparator1);

        btnSwitchInEditmode.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/editmode.png"))); // NOI18N
        btnSwitchInEditmode.setToolTipText("Editormodus");
        btnSwitchInEditmode.setBorderPainted(false);
        btnSwitchInEditmode.setFocusable(false);
        btnSwitchInEditmode.setPreferredSize(new java.awt.Dimension(23, 23));
        btnSwitchInEditmode.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnSwitchInEditmodeActionPerformed(evt);
                }
            });
        toolbar.add(btnSwitchInEditmode);

        btnDiscardChanges.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/cancel.png"))); // NOI18N
        btnDiscardChanges.setToolTipText("Änderungen Abbrechen");
        btnDiscardChanges.setBorderPainted(false);
        btnDiscardChanges.setFocusable(false);
        btnDiscardChanges.setPreferredSize(new java.awt.Dimension(23, 23));
        btnDiscardChanges.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnDiscardChangesActionPerformed(evt);
                }
            });
        toolbar.add(btnDiscardChanges);

        btnAcceptChanges.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/ok.png"))); // NOI18N
        btnAcceptChanges.setToolTipText("Änderungen annehmen");
        btnAcceptChanges.setBorderPainted(false);
        btnAcceptChanges.setFocusable(false);
        btnAcceptChanges.setPreferredSize(new java.awt.Dimension(23, 23));
        btnAcceptChanges.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAcceptChangesActionPerformed(evt);
                }
            });
        toolbar.add(btnAcceptChanges);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator3.setMaximumSize(new java.awt.Dimension(2, 32767));
        jSeparator3.setMinimumSize(new java.awt.Dimension(2, 23));
        jSeparator3.setPreferredSize(new java.awt.Dimension(2, 23));
        toolbar.add(jSeparator3);

        cmdCopyFlaeche.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/copyFl.png"))); // NOI18N
        cmdCopyFlaeche.setToolTipText("Fläche kopieren");
        cmdCopyFlaeche.setBorderPainted(false);
        cmdCopyFlaeche.setEnabled(false);
        cmdCopyFlaeche.setFocusPainted(false);
        cmdCopyFlaeche.setFocusable(false);
        cmdCopyFlaeche.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdCopyFlaeche.setPreferredSize(new java.awt.Dimension(23, 23));
        cmdCopyFlaeche.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdCopyFlaeche.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdCopyFlaecheActionPerformed(evt);
                }
            });
        toolbar.add(cmdCopyFlaeche);

        cmdPasteFlaeche.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/pasteFl.png"))); // NOI18N
        cmdPasteFlaeche.setToolTipText("Fläche einfügen");
        cmdPasteFlaeche.setBorderPainted(false);
        cmdPasteFlaeche.setEnabled(false);
        cmdPasteFlaeche.setFocusPainted(false);
        cmdPasteFlaeche.setFocusable(false);
        cmdPasteFlaeche.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdPasteFlaeche.setPreferredSize(new java.awt.Dimension(23, 23));
        cmdPasteFlaeche.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdPasteFlaeche.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdPasteFlaecheActionPerformed(evt);
                }
            });
        toolbar.add(cmdPasteFlaeche);

        cmdPrint.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/frameprint.png"))); // NOI18N
        cmdPrint.setToolTipText("Drucken");
        cmdPrint.setBorderPainted(false);
        cmdPrint.setFocusable(false);
        cmdPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdPrint.setPreferredSize(new java.awt.Dimension(23, 23));
        cmdPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdPrint.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdPrintActionPerformed(evt);
                }
            });
        toolbar.add(cmdPrint);

        btnReloadFlurstueck.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/reload.gif"))); // NOI18N
        btnReloadFlurstueck.setToolTipText("Flurstück neu laden");
        btnReloadFlurstueck.setBorderPainted(false);
        btnReloadFlurstueck.setFocusable(false);
        btnReloadFlurstueck.setPreferredSize(new java.awt.Dimension(23, 23));
        btnReloadFlurstueck.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnReloadFlurstueckActionPerformed(evt);
                }
            });
        toolbar.add(btnReloadFlurstueck);

        btnOpenWizard.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/wizardicon.png"))); // NOI18N
        btnOpenWizard.setToolTipText("Flurstücksassistent öffnen");
        btnOpenWizard.setBorderPainted(false);
        btnOpenWizard.setFocusable(false);
        btnOpenWizard.setPreferredSize(new java.awt.Dimension(23, 23));
        btnOpenWizard.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnOpenWizardActionPerformed(evt);
                }
            });
        toolbar.add(btnOpenWizard);

        btnAktenzeichenSuche.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/Aktenzeichensuche3.png"))); // NOI18N
        btnAktenzeichenSuche.setToolTipText("Aktenzeichen Suche...");
        btnAktenzeichenSuche.setBorderPainted(false);
        btnAktenzeichenSuche.setFocusable(false);
        btnAktenzeichenSuche.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAktenzeichenSuche.setPreferredSize(new java.awt.Dimension(23, 23));
        btnAktenzeichenSuche.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAktenzeichenSuche.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAktenzeichenSucheActionPerformed(evt);
                }
            });
        toolbar.add(btnAktenzeichenSuche);

        btnVerdisCrossover.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/verdis.png"))); // NOI18N
        btnVerdisCrossover.setToolTipText("Kassenzeichen in VerdIS öffnen.");
        btnVerdisCrossover.setBorderPainted(false);
        btnVerdisCrossover.setFocusable(false);
        btnVerdisCrossover.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVerdisCrossover.setPreferredSize(new java.awt.Dimension(23, 23));
        btnVerdisCrossover.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnVerdisCrossover.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnVerdisCrossoverActionPerformed(evt);
                }
            });
        toolbar.add(btnVerdisCrossover);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setMaximumSize(new java.awt.Dimension(2, 32767));
        jSeparator2.setMinimumSize(new java.awt.Dimension(2, 25));
        jSeparator2.setPreferredSize(new java.awt.Dimension(2, 23));
        toolbar.add(jSeparator2);

        getContentPane().add(toolbar, java.awt.BorderLayout.NORTH);

        panAll.setLayout(new java.awt.BorderLayout());

        panMain.setAutoscrolls(true);
        panMain.setLayout(new java.awt.BorderLayout());
        panAll.add(panMain, java.awt.BorderLayout.CENTER);

        panStatusbar.setLayout(new java.awt.BorderLayout());
        panAll.add(panStatusbar, java.awt.BorderLayout.SOUTH);

        getContentPane().add(panAll, java.awt.BorderLayout.CENTER);

        menFile.setMnemonic('D');
        menFile.setText("Datei");

        mniSaveLayout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_S,
                java.awt.event.InputEvent.CTRL_MASK));
        mniSaveLayout.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/menue/layout.png"))); // NOI18N
        mniSaveLayout.setText("Aktuelles Layout speichern");
        mniSaveLayout.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniSaveLayoutActionPerformed(evt);
                }
            });
        menFile.add(mniSaveLayout);

        mniLoadLayout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_O,
                java.awt.event.InputEvent.CTRL_MASK));
        mniLoadLayout.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/menue/layout.png"))); // NOI18N
        mniLoadLayout.setText("Layout laden");
        mniLoadLayout.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniLoadLayoutActionPerformed(evt);
                }
            });
        menFile.add(mniLoadLayout);

        mniLockLayout.setText("Layout sperren");
        mniLockLayout.setEnabled(false);
        mniLockLayout.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniLockLayoutActionPerformed(evt);
                }
            });
        menFile.add(mniLockLayout);

        jSeparator8.setEnabled(false);
        menFile.add(jSeparator8);

        mniClippboard.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_C,
                java.awt.event.InputEvent.CTRL_MASK));
        mniClippboard.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/menue/clipboard16.png"))); // NOI18N
        mniClippboard.setText("Bild der Karte in die Zwischenablage kopieren");
        mniClippboard.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniClippboardActionPerformed(evt);
                }
            });
        menFile.add(mniClippboard);

        mniPrint.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_P,
                java.awt.event.InputEvent.CTRL_MASK));
        mniPrint.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/menue/frameprint16.png"))); // NOI18N
        mniPrint.setText("Drucken");
        mniPrint.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniPrintActionPerformed(evt);
                }
            });
        menFile.add(mniPrint);

        jSeparator9.setEnabled(false);
        menFile.add(jSeparator9);

        mniClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_F4,
                java.awt.event.InputEvent.ALT_MASK));
        mniClose.setText("Beenden");
        mniClose.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniCloseActionPerformed(evt);
                }
            });
        menFile.add(mniClose);

        mnuBar.add(menFile);

        menEdit.setMnemonic('B');
        menEdit.setText("Bearbeiten");

        mniRefresh.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        mniRefresh.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/menue/reload16.gif"))); // NOI18N
        mniRefresh.setText("Neu laden");
        mniRefresh.setEnabled(false);
        mniRefresh.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniRefreshActionPerformed(evt);
                }
            });
        menEdit.add(mniRefresh);

        mnuBar.add(menEdit);

        menHistory.setMnemonic('C');
        menHistory.setText("Chronik");

        mniBack.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_LEFT,
                java.awt.event.InputEvent.CTRL_MASK));
        mniBack.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/menue/back16.png"))); // NOI18N
        mniBack.setText("Zurück");
        mniBack.setEnabled(false);
        mniBack.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniBackActionPerformed(evt);
                }
            });
        menHistory.add(mniBack);

        mniForward.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_RIGHT,
                java.awt.event.InputEvent.CTRL_MASK));
        mniForward.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/menue/forward16.png"))); // NOI18N
        mniForward.setText("Vor");
        mniForward.setEnabled(false);
        mniForward.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniForwardActionPerformed(evt);
                }
            });
        menHistory.add(mniForward);

        mniHome.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_HOME, 0));
        mniHome.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/menue/home16.gif"))); // NOI18N
        mniHome.setText("Home");
        mniHome.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniHomeActionPerformed(evt);
                }
            });
        menHistory.add(mniHome);

        sepBeforePos.setEnabled(false);
        menHistory.add(sepBeforePos);

        sepAfterPos.setEnabled(false);
        menHistory.add(sepAfterPos);

        mniHistorySidebar.setText("In eigenem Fenster anzeigen");
        mniHistorySidebar.setEnabled(false);
        menHistory.add(mniHistorySidebar);

        mnuBar.add(menHistory);

        menBookmarks.setMnemonic('L');
        menBookmarks.setText("Lesezeichen");

        mniAddBookmark.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/menue/bookmark_add.png"))); // NOI18N
        mniAddBookmark.setText("Lesezeichen hinzufügen");
        mniAddBookmark.setEnabled(false);
        menBookmarks.add(mniAddBookmark);

        mniBookmarkManager.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/menue/bookmark_folder.png"))); // NOI18N
        mniBookmarkManager.setText("Lesezeichen Manager");
        mniBookmarkManager.setEnabled(false);
        menBookmarks.add(mniBookmarkManager);

        mniBookmarkSidebar.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/menue/bookmark.png"))); // NOI18N
        mniBookmarkSidebar.setText("Lesezeichen in eigenem Fenster öffnen");
        mniBookmarkSidebar.setEnabled(false);
        menBookmarks.add(mniBookmarkSidebar);

        mnuBar.add(menBookmarks);

        menExtras.setMnemonic('E');
        menExtras.setText("Extras");

        mniOptions.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/menue/tooloptions.png"))); // NOI18N
        mniOptions.setText("Optionen");
        mniOptions.setEnabled(false);
        mniOptions.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniOptionsActionPerformed(evt);
                }
            });
        menExtras.add(mniOptions);

        jSeparator12.setEnabled(false);
        menExtras.add(jSeparator12);

        mniGotoPoint.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_G,
                java.awt.event.InputEvent.CTRL_MASK));
        mniGotoPoint.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/menue/goto.png"))); // NOI18N
        mniGotoPoint.setText("Gehe zu ...");
        mniGotoPoint.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniGotoPointActionPerformed(evt);
                }
            });
        menExtras.add(mniGotoPoint);

        jSeparator13.setEnabled(false);
        menExtras.add(jSeparator13);

        mniScale.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/menue/scale.png"))); // NOI18N
        mniScale.setText("Maßstab verändern");
        mniScale.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniScaleActionPerformed(evt);
                }
            });
        menExtras.add(mniScale);

        mnuBar.add(menExtras);

        menWindow.setMnemonic('F');
        menWindow.setText("Fenster");

        mniMap.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_1,
                java.awt.event.InputEvent.CTRL_MASK));
        mniMap.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/karte.png"))); // NOI18N
        mniMap.setText("Karte");
        mniMap.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniMapActionPerformed(evt);
                }
            });
        menWindow.add(mniMap);

        mniVerwaltungsbereich.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_2,
                java.awt.event.InputEvent.CTRL_MASK));
        mniVerwaltungsbereich.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/flurstueck.png"))); // NOI18N
        mniVerwaltungsbereich.setText("Verwaltungsbereiche");
        mniVerwaltungsbereich.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniVerwaltungsbereichActionPerformed(evt);
                }
            });
        menWindow.add(mniVerwaltungsbereich);

        mniVorgaenge.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_3,
                java.awt.event.InputEvent.CTRL_MASK));
        mniVorgaenge.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/documents.png"))); // NOI18N
        mniVorgaenge.setText("Vorgänge");
        mniVorgaenge.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniVorgaengeActionPerformed(evt);
                }
            });
        menWindow.add(mniVorgaenge);

        mniNKFOverview.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_4,
                java.awt.event.InputEvent.CTRL_MASK));
        mniNKFOverview.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/sum.png"))); // NOI18N
        mniNKFOverview.setText("NKF Übersicht");
        mniNKFOverview.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniNKFOverviewActionPerformed(evt);
                }
            });
        menWindow.add(mniNKFOverview);

        mniNutzung.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_5,
                java.awt.event.InputEvent.CTRL_MASK));
        mniNutzung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/sum.png"))); // NOI18N
        mniNutzung.setText("Nutzung");
        mniNutzung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniNutzungActionPerformed(evt);
                }
            });
        menWindow.add(mniNutzung);

        mniReBe.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_6,
                java.awt.event.InputEvent.CTRL_MASK));
        mniReBe.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/findgreen.png"))); // NOI18N
        mniReBe.setText("Rechte und Belastungen");
        mniReBe.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniReBeActionPerformed(evt);
                }
            });
        menWindow.add(mniReBe);

        mniDMS.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_8,
                java.awt.event.InputEvent.CTRL_MASK));
        mniDMS.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/docs.png"))); // NOI18N
        mniDMS.setText("DMS");
        mniDMS.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniDMSActionPerformed(evt);
                }
            });
        menWindow.add(mniDMS);

        mniHistory.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_9,
                java.awt.event.InputEvent.CTRL_MASK));
        mniHistory.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/ressort.png"))); // NOI18N
        mniHistory.setText("Historie");
        mniHistory.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniHistoryActionPerformed(evt);
                }
            });
        menWindow.add(mniHistory);

        mniInformation.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_0,
                java.awt.event.InputEvent.CTRL_MASK));
        mniInformation.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/info.png"))); // NOI18N
        mniInformation.setText("Information");
        mniInformation.setToolTipText("Informationen zum aktuellen Flurstück");
        mniInformation.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniInformationActionPerformed(evt);
                }
            });
        menWindow.add(mniInformation);

        jSeparator14.setEnabled(false);
        menWindow.add(jSeparator14);

        mniResetWindowLayout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_R,
                java.awt.event.InputEvent.CTRL_MASK));
        mniResetWindowLayout.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/menue/layout.png"))); // NOI18N
        mniResetWindowLayout.setText("Fensteranordnung zurücksetzen");
        mniResetWindowLayout.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniResetWindowLayoutActionPerformed(evt);
                }
            });
        menWindow.add(mniResetWindowLayout);

        mnuBar.add(menWindow);

        menHelp.setMnemonic('H');
        menHelp.setText("Hilfe");

        mniOnlineHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        mniOnlineHelp.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/menue/help.png"))); // NOI18N
        mniOnlineHelp.setText("Online Hilfe");
        mniOnlineHelp.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniOnlineHelpActionPerformed(evt);
                }
            });
        menHelp.add(mniOnlineHelp);

        mniNews.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/menue/news.png"))); // NOI18N
        mniNews.setText("News");
        mniNews.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniNewsActionPerformed(evt);
                }
            });
        menHelp.add(mniNews);

        mniVersions.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_V,
                java.awt.event.InputEvent.ALT_MASK
                        | java.awt.event.InputEvent.CTRL_MASK));
        mniVersions.setText("Versionsinformationen");
        mniVersions.setEnabled(false);
        menHelp.add(mniVersions);

        mniLisences.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_L,
                java.awt.event.InputEvent.ALT_MASK
                        | java.awt.event.InputEvent.CTRL_MASK));
        mniLisences.setText("Lizenzinformationen");
        mniLisences.setEnabled(false);
        menHelp.add(mniLisences);

        mniAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_A,
                java.awt.event.InputEvent.ALT_MASK
                        | java.awt.event.InputEvent.CTRL_MASK));
        mniAbout.setText("Über LaGIS");
        mniAbout.setEnabled(false);
        menHelp.add(mniAbout);

        mnuBar.add(menHelp);

        setJMenuBar(mnuBar);

        pack();
    } // </editor-fold>

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnOpenWizardActionPerformed(final java.awt.event.ActionEvent evt) {
        WizardDisplayer.showWizard(new ContinuationWizard().createWizard(),
            new Rectangle(20, 20, 600, 400));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniNewsActionPerformed(final java.awt.event.ActionEvent evt) {
        openUrlInExternalBrowser(newsURL);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniOnlineHelpActionPerformed(final java.awt.event.ActionEvent evt) {
        openUrlInExternalBrowser(onlineHelpURL);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniHistoryActionPerformed(final java.awt.event.ActionEvent evt) {
        showOrHideView(vHistory);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniDMSActionPerformed(final java.awt.event.ActionEvent evt) {
        showOrHideView(vDMS);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniReBeActionPerformed(final java.awt.event.ActionEvent evt) {
        showOrHideView(vReBe);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniNutzungActionPerformed(final java.awt.event.ActionEvent evt) {
        showOrHideView(vNKF);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniNKFOverviewActionPerformed(final java.awt.event.ActionEvent evt) {
        showOrHideView(vNKFOverview);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniVorgaengeActionPerformed(final java.awt.event.ActionEvent evt) {
        showOrHideView(vVertraege);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniVerwaltungsbereichActionPerformed(final java.awt.event.ActionEvent evt) {
        showOrHideView(vFlurstueck);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniMapActionPerformed(final java.awt.event.ActionEvent evt) {
        showOrHideView(vKarte);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniGotoPointActionPerformed(final java.awt.event.ActionEvent evt) {
        final BoundingBox c = mapComponent.getCurrentBoundingBox();
        final double x = (c.getX1() + c.getX2()) / 2;
        final double y = (c.getY1() + c.getY2()) / 2;
        final String s = JOptionPane.showInputDialog(
                this,
                "Zentriere auf folgendem Punkt: x,y",
                StaticDecimalTools.round(x)
                        + ","
                        + StaticDecimalTools.round(y));
        try {
            final String[] sa = s.split(",");
            final Double gotoX = new Double(sa[0]);
            final Double gotoY = new Double(sa[1]);
            final BoundingBox bb = new BoundingBox(gotoX, gotoY, gotoX, gotoY);
            mapComponent.gotoBoundingBox(bb, true, false, mapComponent.getAnimationDuration());
        } catch (Exception skip) {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniScaleActionPerformed(final java.awt.event.ActionEvent evt) {
        final String s = JOptionPane.showInputDialog(
                this,
                "Maßstab_manuell_auswählen",
                ((int)mapComponent.getScaleDenominator())
                        + "");
        try {
            final Integer i = new Integer(s);
            mapComponent.gotoBoundingBoxWithHistory(mapComponent.getBoundingBoxFromScale(i));
        } catch (Exception skip) {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniHomeActionPerformed(final java.awt.event.ActionEvent evt) {
        if (mapComponent != null) {
            mapComponent.gotoInitialBoundingBox();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniForwardActionPerformed(final java.awt.event.ActionEvent evt) {
        if ((mapComponent != null) && mapComponent.isForwardPossible()) {
            mapComponent.forward(true);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniBackActionPerformed(final java.awt.event.ActionEvent evt) {
        if ((mapComponent != null) && mapComponent.isBackPossible()) {
            mapComponent.back(true);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniRefreshActionPerformed(final java.awt.event.ActionEvent evt) {
        LagisBroker.getInstance().reloadFlurstueck();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniClippboardActionPerformed(final java.awt.event.ActionEvent evt) {
        final Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    clipboarder.setLocationRelativeTo(LagisApp.this);
                                    clipboarder.setVisible(true);
                                }
                            });

                        final ImageSelection imgSel = new ImageSelection(mapComponent.getImage());
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
                        SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    clipboarder.dispose();
                                }
                            });
                    }
                });
        t.start();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniResetWindowLayoutActionPerformed(final java.awt.event.ActionEvent evt) {
        doLayoutInfoNode();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniSaveLayoutActionPerformed(final java.awt.event.ActionEvent evt) {
        final JFileChooser fc = new JFileChooser(LAGIS_CONFIGURATION_FOLDER);
        fc.setFileFilter(new FileFilter() {

                @Override
                public boolean accept(final File f) {
                    return f.getName().toLowerCase().endsWith(".layout");
                }

                @Override
                public String getDescription() {
                    return "Layout";
                }
            });
        fc.setMultiSelectionEnabled(false);
        final int state = fc.showSaveDialog(this);
        if (LOG.isDebugEnabled()) {
            LOG.debug("state:" + state);
        }
        if (state == JFileChooser.APPROVE_OPTION) {
            final File file = fc.getSelectedFile();
            if (LOG.isDebugEnabled()) {
                LOG.debug("file:" + file);
            }
            String name = file.getAbsolutePath();
            name = name.toLowerCase();
            if (name.endsWith(".layout")) {
                saveLayout(name);
            } else {
                saveLayout(name + ".layout");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  message  DOCUMENT ME!
     */
    private void showErrorMessage(final String message) {
        JOptionPane.showMessageDialog(this,
            message,
            HEADER_ERR_MSG,
            JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * DEFAULT_APP_DATA_PATH.
     *
     * @param   fileName  DOCUMENT ME!
     *
     * @throws  NullPointerException      DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public void loadAppData(final String fileName) {
        if (fileName == null) {
            throw new NullPointerException("File name must not be NULL");
        }

        if (fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name must not be empty");
        }

        final File file = new File(fileName);
        if (file.exists()) {
            if (file.canRead()) {
                FileInputStream fIn = null;
                BufferedInputStream bIn = null;
                ObjectInputStream oIn = null;

                try {
                    fIn = new FileInputStream(file);
                    bIn = new BufferedInputStream(fIn);
                    oIn = new ObjectInputStream(bIn);

                    final Object obj = oIn.readObject();
                    if (obj instanceof FlurstueckSchluessel) {
                        final FlurstueckSchluessel fs = (FlurstueckSchluessel)obj;
                        this.pFlurstueckChooser.requestFlurstueck(fs);
                    } else {
                        this.showErrorMessage("Daten aus letzter Sitzung haben "
                                    + "falschen Typ: "
                                    + obj.getClass().getName());
                    }
                } catch (final Exception e) {
                    LOG.error("An error occured while loading data file "
                                + fileName
                                + ": " + e);

                    this.showErrorMessage("1 Ein interner Fehler ist aufgetreten. " + e);
                } finally {
                    try {
                        if (oIn != null) {
                            oIn.close();
                        }
                    } catch (final Exception e2) {
                        LOG.error("An error occured while closing input stream: "
                                    + e2);

                        this.showErrorMessage("2 Ein interner Fehler ist aufgetreten." + e2);
                    }
                }
            } else {
                this.showErrorMessage("Die Daten aus der letzten Sitzung konnten"
                            + " nicht gelesen werden.");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  file  DOCUMENT ME!
     */
    public void loadLayout(final String file) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Load Layout.. from " + file);
        }
        final File layoutFile = new File(file);

        if (layoutFile.exists()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Layout File exists");
            }
            try {
                final FileInputStream layoutInput = new FileInputStream(layoutFile);
                final ObjectInputStream in = new ObjectInputStream(layoutInput);
                rootWindow.read(in);
                in.close();
                rootWindow.getWindowBar(Direction.LEFT).setEnabled(true);
                rootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);
                if (isInit) {
                    final int count = viewMap.getViewCount();
                    for (int i = 0; i < count; i++) {
                        final View current = viewMap.getViewAtIndex(i);
                        if (current.isUndocked()) {
                            current.dock();
                        }
                    }
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Loading Layout successfull");
                }
            } catch (IOException ex) {
                LOG.error("Layout File IO Exception --> loading default Layout", ex);
                if (isInit) {
                    JOptionPane.showMessageDialog(
                        this,
                        "W\u00E4hrend dem Laden des Layouts ist ein Fehler aufgetreten.\n Das Layout wird zur\u00FCckgesetzt.",
                        "Fehler",
                        JOptionPane.INFORMATION_MESSAGE);
                    doLayoutInfoNode();
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "W\u00E4hrend dem Laden des Layouts ist ein Fehler aufgetreten.\n Das Layout wird zur\u00FCckgesetzt.",
                        "Fehler",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else {
            if (isInit) {
                LOG.warn("Datei exitstiert nicht --> default layout (init)");
                SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            // UGLY WINNING --> Gefixed durch IDW Version 1.5
                            // setupDefaultLayout();
                            // DeveloperUtil.createWindowLayoutFrame("nach setup1",rootWindow).setVisible(true);
                            doLayoutInfoNode();
                            // DeveloperUtil.createWindowLayoutFrame("nach setup2",rootWindow).setVisible(true);
                        }
                    });
            } else {
                LOG.warn("Datei exitstiert nicht)");
                JOptionPane.showMessageDialog(
                    this,
                    "Das angegebene Layout konnte nicht gefunden werden.",
                    "Fehler",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniCloseActionPerformed(final java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniLoadLayoutActionPerformed(final java.awt.event.ActionEvent evt) {
        final JFileChooser fc = new JFileChooser(LAGIS_CONFIGURATION_FOLDER);
        fc.setFileFilter(new FileFilter() {

                @Override
                public boolean accept(final File f) {
                    return f.getName().toLowerCase().endsWith(".layout");
                }

                @Override
                public String getDescription() {
                    return "Layout";
                }
            });
        fc.setMultiSelectionEnabled(false);
        final int state = fc.showOpenDialog(this);
        if (state == JFileChooser.APPROVE_OPTION) {
            final File file = fc.getSelectedFile();
            String name = file.getAbsolutePath();
            name = name.toLowerCase();
            if (name.endsWith(".layout")) {
                loadLayout(name);
            } else {
                // TODO Schwachsinn
                JOptionPane.showMessageDialog(
                    this,
                    "Das gew\u00E4hlte Dateiformat wird nicht unterst\u00FCtzt.\nBitte w\u00E4hlen Sie eine Datei mit der Endung .layout",
                    "Fehler",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fileName  DOCUMENT ME!
     *
     * @throws  NullPointerException      DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public void saveAppData(final String fileName) {
        final LagisBroker lb = LagisBroker.getInstance();
        final FlurstueckSchluessel fs = lb.getCurrentFlurstueckSchluessel();

        if (fs == null) {
            // nothing to do
            LOG.warn("Current FlurstueckSchluessel is null");
            return;
        }

        if (fileName == null) {
            throw new NullPointerException("File name must not be NULL");
        }

        if (fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name must not be empty");
        }

        final File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (final Exception e) {
                LOG.error("An error occured while creating a new app data file: "
                            + e);

                this.showErrorMessage("Es konnte keine Datei erstellt werden um "
                            + "die Daten aus der letzten Sitzung zu "
                            + "speichern");
            }
        }

        FileOutputStream fOut = null;
        BufferedOutputStream bOut = null;
        ObjectOutputStream oOut = null;

        try {
            fOut = new FileOutputStream(file);
            bOut = new BufferedOutputStream(fOut);
            oOut = new ObjectOutputStream(bOut);

            oOut.writeObject(fs);
        } catch (final Exception e) {
            LOG.error("An error occured while writing app data: " + e);
            this.showErrorMessage("Konnte Daten zu der letzten Sitzung nicht "
                        + "schreiben.");
        } finally {
            try {
                if (oOut != null) {
                    oOut.close();
                }
            } catch (final Exception e2) {
                LOG.error("An error occured while closing output stream: " + e2);
                this.showErrorMessage("Ein interner Fehler ist aufgetreten.");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  file  DOCUMENT ME!
     */
    public void saveLayout(final String file) {
        LagisBroker.getInstance().setTitleBarComponentpainter(LagisBroker.DEFAULT_MODE_COLOR);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Saving Layout.. to " + file);
        }
        final File layoutFile = new File(file);
        try {
            if (!layoutFile.exists()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Saving Layout.. File does not exit");
                }
                layoutFile.createNewFile();
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Saving Layout.. File does exit");
                }
            }
            final FileOutputStream layoutOutput = new FileOutputStream(layoutFile);
            final ObjectOutputStream out = new ObjectOutputStream(layoutOutput);
            rootWindow.write(out);
            out.flush();
            out.close();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Saving Layout.. to " + file + " successfull");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                this,
                "W\u00E4hrend dem Speichern des Layouts ist ein Fehler aufgetreten.",
                "Fehler",
                JOptionPane.INFORMATION_MESSAGE);
            LOG.error("A failure occured during writing the layout file", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnReloadFlurstueckActionPerformed(final java.awt.event.ActionEvent evt) {
        LagisBroker.getInstance().reloadFlurstueck();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnDiscardChangesActionPerformed(final java.awt.event.ActionEvent evt) {
        if (LagisBroker.getInstance().isInEditMode()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Versuche aus Editiermodus heraus zu wechseln: ");
            }
            final int answer = JOptionPane.showConfirmDialog(
                    this,
                    "Wollen Sie die gemachten Änderungen verwerfen?",
                    "Lagis Änderungen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.NO_OPTION) {
                return;
            }
            btnAcceptChanges.setEnabled(false);
            btnDiscardChanges.setEnabled(false);
            btnSwitchInEditmode.setEnabled(false);
            if (LagisBroker.getInstance().releaseLock()) {
                // datamodell refactoring 22.10.07e
                if ((LagisBroker.getInstance().getCurrentFlurstueck() != null)
                            && LagisBroker.getInstance().getCurrentFlurstueck().getFlurstueckSchluessel()
                            .isGesperrt()) {
                    //
                    // rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().getShapedPanelProperties().setComponentPainter(new
                    // GradientComponentPainter(Color.YELLOW,new Color(236,233,216),Color.YELLOW,new
                    // Color(236,233,216)));
                    LagisBroker.getInstance().setTitleBarComponentpainter(LagisBroker.LOCK_MODE_COLOR);
                } else {
                    //
                    // rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().getShapedPanelProperties().setComponentPainter(new
                    // GradientComponentPainter(new Color(124,160,221),new Color(236,233,216),new
                    // Color(124,160,221),new Color(236,233,216)));
                    LagisBroker.getInstance().setTitleBarComponentpainter(LagisBroker.DEFAULT_MODE_COLOR);
                }
                // ((DefaultFeatureCollection)LagisBroker.getInstance().getMappingComponent().getFeatureCollection()).setAllFeaturesEditable(false);
                // TODO TEST IT!!!!
                // TODO EDT
                LagisBroker.getInstance().getMappingComponent().setReadOnly(true);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Fehler beim lösen der Sperre des Flurstuecks");
                }
            }
            if (!LagisBroker.getInstance().isCoreReadOnlyMode()) {
                btnOpenWizard.setEnabled(true);
            }
            LagisBroker.getInstance().reloadFlurstueck();
            if (LOG.isDebugEnabled()) {
                LOG.debug("ist im Editiermodus: " + LagisBroker.getInstance().isInEditMode());
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void acceptChanges() {
        btnAcceptChangesActionPerformed(null);
    }

    /**
     * ToDO why not in LagisBroker??
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAcceptChangesActionPerformed(final java.awt.event.ActionEvent evt) {
        try {
            if (LagisBroker.getInstance().isInEditMode()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Versuche aus Editiermodus heraus zu wechseln: ");
                }
                final boolean isValid = LagisBroker.getInstance().validateWidgets();
                if (isValid) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Alle Änderungen sind valide: " + isValid);
                    }
                    final int answer = JOptionPane.showConfirmDialog(
                            this,
                            "Wollen Sie die gemachten Änderungen speichern?",
                            "Lagis Änderungen",
                            JOptionPane.YES_NO_OPTION);
                    if (answer == JOptionPane.YES_OPTION) {
                        LagisBroker.getInstance().saveCurrentFlurstueck();
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("info speichern wurde gecanceled --> weiter im Editmodus");
                        }
                        return;
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Änderungen wurden gespeichert");
                    }
                    //
                    // rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().getShapedPanelProperties().setComponentPainter(new
                    // GradientComponentPainter(new Color(124,160,221),new Color(236,233,216),new
                    // Color(124,160,221),new Color(236,233,216)));
                    btnAcceptChanges.setEnabled(false);
                    btnDiscardChanges.setEnabled(false);
                    btnSwitchInEditmode.setEnabled(false);
                    // ((DefaultFeatureCollection)LagisBroker.getInstance().getMappingComponent().getFeatureCollection()).setAllFeaturesEditable(false);
                    // TODO TEST IT!!!!
                    LagisBroker.getInstance().getMappingComponent().setReadOnly(true);
                    if (LagisBroker.getInstance().releaseLock()) {
                        // datamodell refactoring 22.10.07
                        if ((LagisBroker.getInstance().getCurrentFlurstueck() != null)
                                    && LagisBroker.getInstance().getCurrentFlurstueck().getFlurstueckSchluessel()
                                    .isGesperrt()) {
                            //
                            // rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().getShapedPanelProperties().setComponentPainter(new
                            // GradientComponentPainter(Color.YELLOW,new Color(236,233,216),Color.YELLOW,new
                            // Color(236,233,216)));
                            LagisBroker.getInstance().setTitleBarComponentpainter(LagisBroker.LOCK_MODE_COLOR);
                        } else {
                            //
                            // rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().getShapedPanelProperties().setComponentPainter(new
                            // GradientComponentPainter(new Color(124,160,221),new Color(236,233,216),new
                            // Color(124,160,221),new Color(236,233,216)));
                            LagisBroker.getInstance().setTitleBarComponentpainter(LagisBroker.DEFAULT_MODE_COLOR);
                        }
                    } else {
                        JOptionPane.showMessageDialog(
                            this,
                            "Die Sperre für das Flurstueck konnte nicht aufgehoben werden",
                            "Fehler",
                            JOptionPane.WARNING_MESSAGE);
                    }
                    LagisBroker.getInstance().reloadFlurstueck();
                } else {
                    final String reason = LagisBroker.getInstance().getCurrentValidationErrorMessage();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "Flurstueck kann nicht gespeichert werden, da nicht alle Komponenten valide sind. Grund:\n"
                                    + reason);
                    }
                    JOptionPane.showMessageDialog(
                        this,
                        "Änderungen können nur gespeichert werden, wenn alle Inhalte korrekt sind:\n\n"
                                + reason
                                + "\n\nBitte berichtigen Sie die Inhalte oder machen Sie die jeweiligen Änderungen rückgängig.",
                        "Fehler",
                        JOptionPane.WARNING_MESSAGE);
                }
                if (!LagisBroker.getInstance().isCoreReadOnlyMode()) {
                    btnOpenWizard.setEnabled(true);
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("ist im Editiermodus: " + LagisBroker.getInstance().isInEditMode());
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim akzeptieren von Änderungen: ", ex);
            if (!LagisBroker.getInstance().isCoreReadOnlyMode()) {
                btnOpenWizard.setEnabled(true);
            }
        }
    }
    /**
     * boolean isInEditMode = false;
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnSwitchInEditmodeActionPerformed(final java.awt.event.ActionEvent evt) {
        if (LOG.isDebugEnabled()) {
//        if(isInEditMode){
//        Sperre sperre = new Sperre();
//        sperre.setFlurstueckId(15);
//        sperre.setBenutzerkonto("Sebastian");
//        boolean bool = EJBroker.getInstance().releaseFlurstueckSperre(sperre);
//        System.out.println(bool);
//        isInEditMode=false;
//        } else{
//        Sperre sperre = new Sperre();
//        sperre.setFlurstueckId(15);
//        sperre.setBenutzerkonto("Sebastian");
//        sperre = EJBroker.getInstance().createNewFlurstueckSperre(sperre);
//        log.fatal(sperre);
//        isInEditMode=true;
//        }
//        if(LagisBroker.getInstance().isInEditMode()){
//            log.debug("Versuche aus Editiermodus heraus zu wechseln: ");
//            if(LagisBroker.getInstance().releaseLock()){
//                rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().getShapedPanelProperties().setComponentPainter(new GradientComponentPainter(new Color(124,160,221),new Color(236,233,216),new Color(124,160,221),new Color(236,233,216)));
//            } else {
//
//            }
//            log.debug("ist im Editiermodus: "+LagisBroker.getInstance().isInEditMode());
//        } else {
            LOG.debug("Versuche in Editiermodus zu wechseln: ");
        }
        if (LagisBroker.getInstance().acquireLock()) {
            //
            // rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().getShapedPanelProperties().setComponentPainter(new
            // GradientComponentPainter(new Color(245,3,3),new Color(236,233,216),new Color(245,3,3),new
            // Color(236,233,216)));
            if (LagisBroker.getInstance().isCurrentFlurstueckLockedByUser()) {
                // TODOWHY NOT DIRECTLY CHANGE IN THIS CLASS LIKE IN THE FLURstück CHANGED METHOD ??
                LagisBroker.getInstance()
                        .setTitleBarComponentpainter(LagisBroker.LOCK_MODE_COLOR, LagisBroker.EDIT_MODE_COLOR);
            } else {
                LagisBroker.getInstance().setTitleBarComponentpainter(LagisBroker.EDIT_MODE_COLOR);
            }
            // ((DefaultFeatureCollection)LagisBroker.getInstance().getMappingComponent().getFeatureCollection()).setAllFeaturesEditable(true);
            // TODO TEST IT!!!!
            LagisBroker.getInstance().getMappingComponent().setReadOnly(false);
            btnSwitchInEditmode.setEnabled(false);
            btnAcceptChanges.setEnabled(true);
            btnDiscardChanges.setEnabled(true);
            btnOpenWizard.setEnabled(false);
        } else {
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("ist im Editiermodus: " + LagisBroker.getInstance().isInEditMode());
        }
//        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniLockLayoutActionPerformed(final java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniOptionsActionPerformed(final java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAktenzeichenSucheActionPerformed(final java.awt.event.ActionEvent evt) {
//    if(vAktenzeichenSuche.isUndocked()){
//        log.debug("AktenzeichenSuche ist undocked");
//        if(aktenzeichenFloatingWindow.getTopLevelAncestor().isVisible()){
//            log.debug("Aktenzeichensuche ist sichtbar");
//            aktenzeichenFloatingWindow.getTopLevelAncestor().setVisible(false);
//        } else {
//            log.debug("Aktenzeichensuche ist nicht sichtbar");
//            aktenzeichenFloatingWindow.getTopLevelAncestor().setVisible(true);
//        }
//    } else {
//        log.debug("Aktenzeichensuche ist nicht undocked");
//        if(vAktenzeichenSuche.isRestorable()){
//            log.debug("Aktenzeichensuche kann restored werden");
//            //aktenzeichenFloatingWindow = rootWindow.createFloatingWindow(new Point(406, 175), new Dimension(300, 613), vAktenzeichenSuche);
//            //aktenzeichenFloatingWindow.getTopLevelAncestor().setVisible(true);
//            vAktenzeichenSuche.restore();
//        } else {
//            log.debug("Aktenzeichensuche kann nicht restored werden");
//            if(vAktenzeichenSuche.isClosable()){
//                log.debug("Aktenzeichensuche kann geschlossen werden");
//                vAktenzeichenSuche.close();
//            } else {
//                log.debug("Aktenzeichensuche kann nicht geschlossen werden");
//            }
//        }
////        log.debug("Aktenzeichensuche ist gedocked ");
////        Point frameLocation = this.getLocation();
////        vAktenzeichenSuche.undock(new Point(frameLocation.x+(int)this.getBounds().getWidth()/2,frameLocation.y+(int)this.getBounds().getHeight()/2));
//    }
        if (aktenzeichenDialog == null) {
            aktenzeichenDialog = new JDialog(LagisBroker.getInstance().getParentComponent(),
                    "Suche nach Aktenzeichen",
                    false);
            aktenzeichenDialog.add(new AktenzeichenSearch());
            aktenzeichenDialog.pack();
            aktenzeichenDialog.setIconImage(((ImageIcon)icoAktenzeichenSuche).getImage());
            aktenzeichenDialog.setLocationRelativeTo(LagisBroker.getInstance().getParentComponent());
            aktenzeichenDialog.setVisible(true);
        } else {
            if (aktenzeichenDialog.isVisible()) {
                aktenzeichenDialog.setVisible(false);
            } else {
                aktenzeichenDialog.setVisible(true);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniInformationActionPerformed(final java.awt.event.ActionEvent evt) {
        showOrHideView(vInformation);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdPrintActionPerformed(final java.awt.event.ActionEvent evt) {
        final String oldMode = mapComponent.getInteractionMode();
        if (LOG.isDebugEnabled()) {
            LOG.debug("oldInteractionMode:" + oldMode);
        }
        // Enumeration en = cmdGroupPrimaryInteractionMode.getElements();
        // togInvisible.setSelected(true);
        mapComponent.showPrintingSettingsDialog(oldMode);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniPrintActionPerformed(final java.awt.event.ActionEvent evt) {
        cmdPrintActionPerformed(evt);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdCopyFlaecheActionPerformed(final java.awt.event.ActionEvent evt) {
        final int answer = JOptionPane.YES_OPTION;
//    if (!clipboardPasted) {
//        answer = JOptionPane.showConfirmDialog(this, "In der LagIS-Zwischenablage befinden sich noch Daten.\nSollen die Daten verworfen und die ausgew\u00E4hlte Selektion kopiert werden ?", "Ausschneiden", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
//    }
        copiedFeatures.clear();
        clipboard = LagisBroker.getInstance().getMappingComponent().getFeatureCollection().getSelectedFeatures();
        if (answer == JOptionPane.YES_OPTION) {
            if (clipboard != null) {
//        if (clipboard instanceof Flaeche) {
//            Flaeche clipboardFlaeche = (Flaeche) clipboard;
//            if (clipboardFlaeche.getClipboardStatus() == Flaeche.CUTTED) {
//                flPanel.pasteFlaeche(clipboardFlaeche);
//            //                    clipboard=null;
//            //                    cmdPasteFlaeche.setEnabled(false);
//            //siehe unten
//            } else {
//                flPanel.pasteFlaeche(clipboardFlaeche);
//                clipboardPasted = true;
//            }
                // } else if (clipboard instanceof Collection) {
                if (clipboard instanceof Collection) {
                    final Iterator it = ((Collection)clipboard).iterator();
                    final boolean cutting = false;
                    //////////////////////////////////
                    while (it.hasNext()) {
                        final Feature clipboardFlaeche = (Feature)it.next();
//                if (clipboardFlaeche.getClipboardStatus() == Flaeche.CUTTED) {
//                    /////////////////////////////////////////////////////////////////////////////////////
//
//                    flPanel.pasteFlaecheWithoutRefresh((Flaeche) clipboardFlaeche.clone());
//                    //((Vector)clipboard).remove(clipboardFlaeche);
//                    cutting = true;
//                } else {
                        final PureNewFeature newFeature = new PureNewFeature((Geometry)clipboardFlaeche.getGeometry()
                                        .clone());
                        newFeature.setCanBeSelected(true);
                        newFeature.setEditable(true);
                        copiedFeatures.add(newFeature);
                        // flPanel.pasteFlaecheWithoutRefresh((Flaeche) clipboardFlaeche.clone()); }
                        // flPanel.refreshTableAndMapAfterPaste((Flaeche) clipboardFlaeche.clone());
                    }
                }
            }

            // storeClipboardBackup();
            if (copiedFeatures.size() > 0) {
                clipboardPasted = false;
                this.cmdPasteFlaeche.setEnabled(true);
            } else {
                this.cmdPasteFlaeche.setEnabled(false);
            }
        }
    }
    /**
     * public Object getSelectedGeometries() { if
     * (LagisBroker.getInstance().getMappingComponent().getFeatureCollection().getSelectedFeatures() != null &&
     * LagisBroker.getInstance().getMappingComponent().getFeatureCollection().getSelectedFeatures().size() > 0) { //
     * Vector clipboard = new Vector(); // int[] rows = flOverviewPanel.getJxtOverview().getSelectedRows(); // for (int
     * i = 0; i < rows.length; ++i) { // int modelIndex =
     * flOverviewPanel.getJxtOverview().getFilters().convertRowIndexToModel(rows[i]); // Flaeche f =
     * flOverviewPanel.getTableModel().getFlaechebyIndex(modelIndex); // f.setClipboardStatus(Flaeche.COPIED); //
     * Flaeche c = (Flaeche) f.clone(); // clipboard.add(c); // } // return clipboard; return new Vector(Vector
     * clipboard = new Vector()); } else { Flaeche sf = flOverviewPanel.getModel().getSelectedFlaeche();
     * sf.setClipboardStatus(Flaeche.COPIED); Flaeche c = (Flaeche) sf.clone(); c.setNewFlaeche(true); return c; } }.
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdPasteFlaecheActionPerformed(final java.awt.event.ActionEvent evt) {
        if (copiedFeatures.size() > 0) {
            LagisBroker.getInstance().getMappingComponent().getFeatureCollection().unselectAll();
            final Iterator it = copiedFeatures.iterator();
            final boolean cutting = false;
            while (it.hasNext()) {
                final Feature clipboardFlaeche = (Feature)it.next();
                final PureNewFeature newFeature = new PureNewFeature((Geometry)clipboardFlaeche.getGeometry().clone());
                newFeature.setCanBeSelected(true);
                newFeature.setEditable(true);
                LagisBroker.getInstance().getMappingComponent().getFeatureCollection().addFeature(newFeature);
            }
            clipboardPasted = true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnVerdisCrossoverActionPerformed(final java.awt.event.ActionEvent evt) {
        try {
            final JDialog dialog = new JDialog(this, "", true);
            final VerdisCrossoverPanel vcp = new VerdisCrossoverPanel(LagisBroker.getInstance()
                            .getVerdisCrossoverPort());
            dialog.add(vcp);
            dialog.pack();
            dialog.setIconImage(new javax.swing.ImageIcon(
                    getClass().getResource("/de/cismet/lagis/ressource/icons/verdis.png")).getImage());
            dialog.setTitle("Kassenzeichen in VerdIS öffnen.");
            dialog.setLocationRelativeTo(this);
            vcp.startSearch();
            dialog.setVisible(true);
        } catch (Exception ex) {
            LOG.error("Crossover: Fehler im VerdIS Crossover", ex);
            // ToDo Meldung an Benutzer
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  the command line arguments
     */
    public static void main(final String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

                @Override
                public void uncaughtException(final Thread t, final Throwable e) {
                    LOG.error("Uncaught Exception in " + t, e);
                }
            });

        java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    try {
                        final Options options = new Options();
                        options.addOption("h", true, "callserver host");
                        options.addOption("d", true, "Domain");
                        options.addOption("g", true, "Glassfish host");
                        options.addOption("p", true, "Glassfish port");
                        final PosixParser parser = new PosixParser();
                        final CommandLine cmd = parser.parse(options, args);
                        if (cmd.hasOption("h")) {
                            LagisBroker.getInstance().setCallserverHost(cmd.getOptionValue("h"));
                        } else {
                            System.out.println("Kein Callserverhost spezifiziert. Versuche localhost...");
                            LagisBroker.getInstance().setCallserverHost("localhost");
                        }
                        if (cmd.hasOption("d")) {
                            LagisBroker.getInstance().setDomain(cmd.getOptionValue("d"));
                        } else {
                            System.err.println("Keine Domain spezifiziert, bitte mit -d setzen");
                            System.exit(1);
                        }
                        if (cmd.hasOption("g")) {
                            EJBroker.setServer(cmd.getOptionValue("g"));
                            System.out.println(
                                "Glassfish Server wurder über Kommandozeile gesetzt, dieser "
                                        + "Wert überschreibt den Konfigurationswert");
                        }
                        if (cmd.hasOption("p")) {
                            EJBroker.setOrbPort(cmd.getOptionValue("p"));
                            System.out.println(
                                "Glassfish Server Port wurder über Kommandozeile gesetzt, dieser "
                                        + "Wert überschreibt den Konfigurationswert");
                        }
                    } catch (Exception ex) {
                        System.err.println("Fehler beim auslesen der Kommandozeilen Parameter");
                        ex.printStackTrace();
                        System.exit(1);
                    }
                    try {
                        final PlasticXPLookAndFeel lf = new PlasticXPLookAndFeel();
                        // TODO NACH MESSE
                        // Plastic3DLookAndFeel lf = new Plastic3DLookAndFeel();
                        // lf.set3DEnabled(true);
                        javax.swing.UIManager.setLookAndFeel(lf);
                    } catch (Exception ex) {
                        LOG.error("Fehler beim setzen des Look & Feels");
                    }
                    initLog4J();
                    try {
                        handleLogin();
                    } catch (Exception ex) {
                        LOG.error("Fehler beim Loginframe", ex);
                        System.exit(0);
                    }
                }
            });
        // LagisApp lagis = new LagisApp();
    }

    /* Implemented methods
     *
     *
     */
    // HistoryModelListener
    @Override
    public void backStatusChanged() {
        // throw new UnsupportedOperationException("Not supported yet.");
        mniBack.setEnabled(mapComponent.isBackPossible());
    }

    @Override
    public void forwardStatusChanged() {
        // throw new UnsupportedOperationException("Not supported yet.");
        mniForward.setEnabled(mapComponent.isForwardPossible());
    }

    @Override
    public void historyChanged() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("HistoryChanged");
        }
        // throw new UnsupportedOperationException("Not supported yet.");
        try {
            if ((mapComponent != null) && (mapComponent.getCurrentElement() != null)) {
                final Vector backPos = mapComponent.getBackPossibilities();
                final Vector forwPos = mapComponent.getForwardPossibilities();
                if (menHistory != null) {
                    menHistory.removeAll();
                    menHistory.add(mniBack);
                    menHistory.add(mniForward);
                    menHistory.add(mniHome);
                    menHistory.add(sepBeforePos);
                    int counter = 0;

                    int start = 0;
                    if ((backPos.size() - 10) > 0) {
                        start = backPos.size() - 10;
                    }

                    for (int index = start; index < backPos.size(); ++index) {
                        final Object elem = backPos.get(index);
                        final JMenuItem item = new JMenuItem(elem.toString()); // +" :"+new
                        // Integer(backPos.size()-1-index));

                        item.setIcon(miniBack);
                        final int pos = backPos.size() - 1 - index;
                        item.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    for (int i = 0; i < pos; ++i) {
                                        mapComponent.back(false);
                                    }
                                    mapComponent.back(true);
                                }
                            });
                        menHistory.add(item);
//                if (counter++>15) break;
                    }
                    final JMenuItem currentItem = new JMenuItem(mapComponent.getCurrentElement().toString());
                    currentItem.setEnabled(false);

                    currentItem.setIcon(current);
                    menHistory.add(currentItem);
                    counter = 0;
                    for (int index = forwPos.size() - 1; index >= 0; --index) {
                        final Object elem = forwPos.get(index);
                        final JMenuItem item = new JMenuItem(elem.toString()); // +":"+new
                        // Integer(forwPos.size()-1-index));

                        item.setIcon(miniForward);
                        final int pos = forwPos.size() - 1 - index;
                        item.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    for (int i = 0; i < pos; ++i) {
                                        mapComponent.forward(false);
                                    }
                                    mapComponent.forward(true);
                                }
                            });

                        menHistory.add(item);
                        if (counter++ > 10) {
                            break;
                        }
                    }
                    menHistory.add(sepAfterPos);
                    menHistory.add(mniHistorySidebar);
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("CurrentElement == null --> No History change");
                }
            }
        } catch (Exception ex) {
            LOG.error("Fehler in Historychanged", ex);
        }
    }

    @Override
    public void historyActionPerformed() {
        // throw new UnsupportedOperationException("Not supported yet.");
        LOG.info("historyActionPerformed");
    }

    /**
     * DOCUMENT ME!
     */
    private void setWindowSize() {
        if ((windowSize != null) && (windowLocation != null)) {
            this.setSize(windowSize);
            this.setLocation(windowLocation);
        } else {
            this.pack();
        }
    }

    @Override
    public void configure(final Element parent) {
        final Element prefs = parent.getChild("cismapPluginUIPreferences");
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("setting windowsize of application");
            }
            final Element window = prefs.getChild("window");
            final int windowHeight = window.getAttribute("height").getIntValue();
            final int windowWidth = window.getAttribute("width").getIntValue();
            final int windowX = window.getAttribute("x").getIntValue();
            final int windowY = window.getAttribute("y").getIntValue();
            final boolean windowMaximised = window.getAttribute("max").getBooleanValue();
            windowSize = new Dimension(windowWidth, windowHeight);
            windowLocation = new Point(windowX, windowY);
            if (LOG.isDebugEnabled()) {
                LOG.debug("windowSize: width " + windowWidth + " heigth " + windowHeight);
            }
            // TODO why is this not working
            // mapComponent.formComponentResized(null);
            if (windowMaximised) {
                this.setExtendedState(MAXIMIZED_BOTH);
            } else {
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("setting of window successful");
            }
        } catch (Throwable t) {
            // TODO defaults
            LOG.error("Error while setting windowsize", t);
        }
    }
    // TODO optimize

    @Override
    public void masterConfigure(final Element parent) {
        try {
            // ToDo if it fails all fail better place in the single try catch
            final Element prefs = parent.getChild("glassfishSetup");
            final Element urls = parent.getChild("urls");
            final Element userPermissions = parent.getChild("permissions");
            final Element albConfiguration = parent.getChild("albConfiguration");
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("OnlineHilfeUrl: " + urls.getChildText("onlineHelp"));
                }
                onlineHelpURL = urls.getChildText("onlineHelp");
            } catch (Exception ex) {
                LOG.warn("Fehler beim lesen der OnlineHilfe URL", ex);
            }
            try {
                albURL = albConfiguration.getChildText("albURL");
                if (albURL != null) {
                    albURL = albURL.trim();
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ALBURL: " + albURL.trim());
                }
            } catch (Exception ex) {
                LOG.warn("Fehler beim lesen der ALB Konfiguration", ex);
            }
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("News Url: " + urls.getChildText("onlineHelp"));
                }
                newsURL = urls.getChildText("news");
            } catch (Exception ex) {
                LOG.warn("Fehler beim lesen der News Url", ex);
            }
            try {
                if (EJBroker.getServer() == null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Glassfishhost: " + prefs.getChildText("host"));
                    }
                    // System.setProperty("org.omg.CORBA.ORBInitialHost", prefs.getChildText("host"));
                    EJBroker.setServer(prefs.getChildText("host"));
                } else {
                    LOG.info("Glassfish Server wurde bereits über die Kommandozeile gesetzt. Keine Konfiguration");
                }
            } catch (Exception ex) {
                LOG.warn("Fehler beim lesen des Glassfish Hosts", ex);
            }
            try {
                if (EJBroker.getOrbPort() == null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Glassfisport: " + prefs.getChildText("orbPort"));
                    }
                    // System.setProperty("org.omg.CORBA.ORBInitialPort", prefs.getChildText("orbPort"));
                    EJBroker.setOrbPort(prefs.getChildText("orbPort"));
                } else {
                    LOG.info("Glassfish Serverport wurde bereits über die Kommandozeile gesetzt. Keine Konfiguration");
                }
            } catch (Exception ex) {
                LOG.warn("Fehler beim lesen des Glassfish Ports", ex);
            }
            EJBroker.getInstance();
            try {
                final Element crossoverPrefs = parent.getChild("CrossoverConfiguration");
                final String crossoverServerPort = crossoverPrefs.getChildText("ServerPort");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Crossover: Crossover port: " + crossoverServerPort);
                }
                initCrossoverServer(Integer.parseInt(crossoverServerPort));
            } catch (Exception ex) {
                LOG.warn("Crossover: Error while starting Server", ex);
            }
            try {
                final Element crossoverPrefs = parent.getChild("CrossoverConfiguration");
                final String verdisHost = crossoverPrefs.getChild("VerdisConfiguration").getChildText("Host");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Crossover: verdisHost: " + verdisHost);
                }
                final String verdisORBPort = crossoverPrefs.getChild("VerdisConfiguration").getChildText("ORBPort");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Crossover: verdisORBPort: " + verdisORBPort);
                }
                LagisBroker.getInstance()
                        .setVerdisCrossoverPort(Integer.parseInt(
                                crossoverPrefs.getChild("VerdisConfiguration").getChildText("VerdisCrossoverPort")));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Crossover: verdisCrossoverPort: " + LagisBroker.getInstance().getVerdisCrossoverPort());
                }
                final KassenzeichenFacadeRemote verdisServer = EJBAccessor.createEJBAccessor(
                            verdisHost,
                            verdisORBPort,
                            KassenzeichenFacadeRemote.class)
                            .getEjbInterface();
                LagisBroker.getInstance().setVerdisServer(verdisServer);
            } catch (Exception ex) {
                LOG.warn("Crossover: Error beim setzen des verdis servers", ex);
            }
            try {
                final Element crossoverPrefs = parent.getChild("CrossoverConfiguration");
                final double kassenzeichenBuffer = Double.parseDouble(crossoverPrefs.getChildText(
                            "KassenzeichenBuffer"));
                LagisBroker.getInstance().setKassenzeichenBuffer(kassenzeichenBuffer);
            } catch (Exception ex) {
                LOG.error("Crossover: Fehler beim setzen den buffers für die Kassenzeichenabfrage", ex);
            }
//            try {
//                log.debug("Userdomain: " + login.getAttribute("userdomainname").getValue());
//                standaloneDomain = login.getAttribute("userdomainname").getValue();
//            } catch (Exception ex) {
//                log.warn("Fehler beim lesen der Userdomainname", ex);
//            }
//            try {
//                log.debug("Callserverhost: " + login.getAttribute("callserverhost").getValue());
//                callserverhost = login.getAttribute("callserverhost").getValue();
//            } catch (Exception ex) {
//                log.warn("Fehler beim lesen des callserverhost", ex);
//            }
            /*
             * <userDependingConfigurationProperties> <classpathfolder>/de/cismet/lagis/configuration/</classpathfolder>
             * <file>userDependingConfiguration.properties</file> </userDependingConfigurationProperties>
             */
//            try {
//                userDependingConfigurationFile = userDep.getChildText("file");
//                String userDependingConfigurationClasspathfolder = userDep.getChildText("classpathfolder");
//                log.debug("UserDependingConfiguration: file=" + userDependingConfigurationFile + " classpathfolder=" + userDependingConfigurationClasspathfolder);
//                configManager.setUserDependingConfigurationClasspath(userDependingConfigurationClasspathfolder);
//                configManager.setUserDependingConfigurationFile(userDependingConfigurationFile);
//            } catch (Exception ex) {
//                log.warn("Fehler beim lesen des userconfigurationfiles", ex);
//            }
            /*
             * <permissions> <permission> <readWrite>true</readWrite> <userGroup>lagISTest</userGroup>
             * <userDomain>VERDIS</userDomain> </permission> </permissions>
             */
//            try {
//                HashMap<String, Boolean> permissions = new HashMap<String, Boolean>();
//                List<Element> xmlPermissions = userPermissions.getChildren();
//                for (Element currentPermission : xmlPermissions) {
//                    try {
//                        String isReadWriteAllowedString = currentPermission.getChildText("readWrite");
//                        boolean isReadWriteAllowed = false;
//                        if (isReadWriteAllowedString != null) {
//                            if (isReadWriteAllowedString.equals("true")) {
//                                isReadWriteAllowed = true;
//                            }
//                        }
//                        String userGroup = currentPermission.getChildText("userGroup");
//                        String userDomain = currentPermission.getChildText("userDomain");
//                        String permissionString = userGroup + "@" + userDomain;
//                        log.info("Permissions für: login=*@" + permissionString + " readWriteAllowed=" + isReadWriteAllowed + "(boolean)/" + isReadWriteAllowedString + "(String)");
//                        if (permissionString != null) {
//                            permissions.put(permissionString.toLowerCase(), isReadWriteAllowed);
//                        }
//                    } catch (Exception ex) {
//                        log.fatal("Fehler beim lesen eines Userechtes", ex);
//                    }
//                }
//                LagisBroker.getInstance().setPermissions(permissions);
//            } catch (Exception ex) {
//                log.fatal("Fehler beim lesen der Userrechte (Permissions)", ex);
//                LagisBroker.getInstance().setPermissions(new HashMap<String, Boolean>());
//            //TODO wenigstens den Nutzer benachrichtigen sonst ist es zu hard oder nur lesen modus --> besser!!!
//            //System.exit(1);
//            }
        } catch (Exception ex) {
            LOG.error("Fehler beim konfigurieren der Lagis Applikation: ", ex);
        }
    }

    @Override
    public Element getConfiguration() throws NoWriteError {
        final Element ret = new Element("cismapPluginUIPreferences");
        final Element window = new Element("window");
        final int windowHeight = this.getHeight();
        final int windowWidth = this.getWidth();
        final int windowX = (int)this.getLocation().getX();
        final int windowY = (int)this.getLocation().getY();
        final boolean windowMaximised = (this.getExtendedState() == MAXIMIZED_BOTH);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Windowsize: width " + windowWidth + " height " + windowHeight);
        }
        window.setAttribute("height", "" + windowHeight);
        window.setAttribute("width", "" + windowWidth);
        window.setAttribute("x", "" + windowX);
        window.setAttribute("y", "" + windowY);
        window.setAttribute("max", "" + windowMaximised);
        ret.addContent(window);
        return ret;
    }

    // awt.Window
    @Override
    public void dispose() {
        setVisible(false);
        LOG.info("Dispose(): Lagis wird heruntergefahren");
        saveLayout(DEFAULT_LAYOUT_PATH);

        this.saveAppData(DEFAULT_APP_DATA_PATH);

        // TODO
        // configurationManager.writeConfiguration();
        // CismapBroker.getInstance().writePropertyFile();
        // CismapBroker.getInstance().cleanUpSystemRegistry();
        super.dispose();
        System.exit(0);
    }

    @Override
    public void flurstueckChanged(final Flurstueck newFlurstueck) {
        // mapComponent
        currentFlurstueck = newFlurstueck;
        LOG.info("Flurstueck Changed");
        updateThread.notifyThread(newFlurstueck);
//        if(refresherThread != null && refresherThread.isAlive()){
//            refresherThread.interrupt();
//        }
//        refresherThread = new Thread(){
//            public void run() {
//                //TODO Fehler falls es nicht erlaubt ist ein durch einen Benutzer gesperrtes Flurstück fortzuführen
//                if(newFlurstueck.isGesperrt() && newFlurstueck.getGueltigBis() == null){
//                    log.fatal("Flurstück ist gesperrt");
//                    rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().getShapedPanelProperties().setComponentPainter(new GradientComponentPainter(LagisBroker.LOCK_MODE_COLOR,new Color(236,233,216),LagisBroker.LOCK_MODE_COLOR,new Color(236,233,216)));
//                    btnSwitchInEditmode.setEnabled(true);
//                } else if(newFlurstueck.getGueltigBis() != null){
//                    log.fatal("Flurstück ist historisch");
//                    rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().getShapedPanelProperties().setComponentPainter(new GradientComponentPainter(LagisBroker.HISTORY_MODE_COLOR,new Color(236,233,216),LagisBroker.HISTORY_MODE_COLOR,new Color(236,233,216)));
//                    btnSwitchInEditmode.setEnabled(false);
//                } else {
//                    log.fatal("Flurstück ist normal");
//                    rootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().getNormalProperties().getShapedPanelProperties().setComponentPainter(new GradientComponentPainter(LagisBroker.DEFAULT_MODE_COLOR,new Color(236,233,216),LagisBroker.DEFAULT_MODE_COLOR,new Color(236,233,216)));
//                    btnSwitchInEditmode.setEnabled(true);
//                }
//                if(this.isInterrupted()){
//                    return;
//                }
//                LagisBroker.getInstance().flurstueckChangeFinished(LagisApp.this);
//            }
//        };
//        refresherThread.setPriority(Thread.NORM_PRIORITY);
//        refresherThread.start();
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
        btnReloadFlurstueck.setEnabled(!isEditable);
        mniRefresh.setEnabled(!isEditable);
        if (!isEditable) {
            cmdPasteFlaeche.setEnabled(isEditable);
            cmdCopyFlaeche.setEnabled(isEditable);
        } else {
            if (clipboard != null) {
                cmdPasteFlaeche.setEnabled(isEditable);
            }
        }
    }

    @Override
    public synchronized void clearComponent() {
        // TODO ugly geknaupt
        if (LagisBroker.getInstance().isUnkownFlurstueck()) {
            btnSwitchInEditmode.setEnabled(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public EJBAccessor<KassenzeichenFacadeRemote> getVerdisCrossoverAccessor() {
        return verdisCrossoverAccessor;
    }

    @Override
    public void refresh(final Object refreshObject) {
    }

    @Override
    public String getWidgetName() {
        return WIDGET_NAME;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  crossoverServerPort  DOCUMENT ME!
     */
    private void initCrossoverServer(final int crossoverServerPort) {
        final int defaultServerPort = 19000;
        boolean defaultServerPortUsed = false;
        try {
            if ((crossoverServerPort < 0) || (crossoverServerPort > 65535)) {
                LOG.warn("Crossover: Invalid Crossover serverport: " + crossoverServerPort
                            + ". Going to use default port: " + defaultServerPort);
                defaultServerPortUsed = true;
                initCrossoverServerImpl(defaultServerPort);
            } else {
                initCrossoverServerImpl(crossoverServerPort);
            }
        } catch (Exception ex) {
            LOG.error("Crossover: Error while creating crossover server on port: " + crossoverServerPort, ex);
            if (!defaultServerPortUsed) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Crossover: Trying to create server with defaultPort: " + defaultServerPort);
                }
                defaultServerPortUsed = true;
                try {
                    initCrossoverServerImpl(defaultServerPort);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Crossover: Server started at port: " + defaultServerPort);
                    }
                } catch (Exception ex1) {
                    LOG.error("Crossover: Failed to initialize Crossover server on defaultport: " + defaultServerPort
                                + ". No Server is started");
                    btnVerdisCrossover.setEnabled(false);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   crossoverServerPort  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private void initCrossoverServerImpl(final int crossoverServerPort) throws Exception {
        final HttpHandler handler = ContainerFactory.createContainer(HttpHandler.class, LagisCrossover.class);
        final HttpServer server = HttpServer.create(new InetSocketAddress(crossoverServerPort), 0);
        server.createContext("/", handler);
        server.setExecutor(null);
        server.start();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  v  DOCUMENT ME!
     */
    private void showOrHideView(final View v) {
        ///irgendwas besser als Closable ??
        // Problem wenn floating --> close -> open  (muss zweimal open)

        if (v.isClosable()) {
            v.close();
        } else {
            v.restore();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  url  DOCUMENT ME!
     */
    private void openUrlInExternalBrowser(final String url) {
        try {
            if (appletContext == null) {
                de.cismet.tools.BrowserLauncher.openURL(url);
            } else {
                final java.net.URL u = new java.net.URL(url);
                appletContext.showDocument(u, "cismetBrowser");
            }
        } catch (Exception e) {
            LOG.warn("Fehler beim \u00D6ffnen von:" + url + "\\nNeuer Versuch", e);
            // Nochmal zur Sicherheit mit dem BrowserLauncher probieren
            try {
                de.cismet.tools.BrowserLauncher.openURL(url);
            } catch (Exception e2) {
                LOG.warn("Auch das 2te Mal ging schief.Fehler beim \u00D6ffnen von:" + "\\nLetzter Versuch", e2);
                try {
                    de.cismet.tools.BrowserLauncher.openURL("file://" + url);
                } catch (Exception e3) {
                    LOG.error("Auch das 3te Mal ging schief.Fehler beim \u00D6ffnen von:" + url, e3);
                }
            }
        }
    }

    // TODO use
    @Override
    public Icon getWidgetIcon() {
        return null;
    }

    // TODO VERDIS COPY
// public void setUserString(String userString) {
// this.userString = userString;
// this.setTitle("LagIS [" + userString + "]");
// //LagisBroker.getInstance().get;
// }
    @Override
    public void windowOpened(final WindowEvent e) {
    }

    @Override
    public void windowIconified(final WindowEvent e) {
    }

    @Override
    public void windowDeiconified(final WindowEvent e) {
    }

    @Override
    public void windowDeactivated(final WindowEvent e) {
    }

    @Override
    public void windowClosing(final WindowEvent e) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("windowClosing():");
            LOG.debug("windowClosing(): Checke ob noch eine Sperre vorhanden ist.");
        }
        cleanUp();
        dispose();
    }

    /**
     * DOCUMENT ME!
     */
    private void cleanUp() {
        if (LagisBroker.getInstance().isInEditMode()) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Versuche aus Editiermodus heraus zu wechseln: ");
                }
                final int answer = JOptionPane.showConfirmDialog(
                        this,
                        "Wollen Sie die gemachten Änderungen speichern",
                        "Lagis Änderungen",
                        JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    final boolean isValid = LagisBroker.getInstance().validateWidgets();
                    if (isValid) {
                        if (LOG.isDebugEnabled()) {
                            // TODO Progressbar
                            LOG.debug("Alle Änderungen sind valide: " + isValid);
                        }
                        LagisBroker.getInstance().saveCurrentFlurstueck();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Änderungen wurden gespeichert");
                        }
                    }
                    // ToDo bescheid sagen und warten wenn Änderungen nicht valide sind
                }
            } catch (Exception ex) {
                if (LOG.isDebugEnabled()) {
                    // TODO saveCurrentFlurstueck wirft keine Exception, prüfen an welchen Stellen die Methode
                    // benutzt wird und sicherstellen das keine Probleme durch eine geworfene Exception auftreten
                    LOG.debug("Es ist ein Fehler wärend dem abspeichern des Flurstuecks aufgetreten", ex);
                }
                JOptionPane.showMessageDialog(
                    this,
                    "Es traten Fehler beim abspeichern des Flurstuecks auf",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
            }
            while (true) {
                // TODO Progressbar & !!! Regeneriert sich nicht nach einem Server neustart
                if (LagisBroker.getInstance().releaseLock()) {
                    break;
                } else {
                    final int answer = JOptionPane.showConfirmDialog(
                            this,
                            "Sperre konnte nicht entfernt werden. Möchten Sie es erneut probieren?",
                            "Lagis Änderungen",
                            JOptionPane.YES_NO_OPTION);
                    if (answer == JOptionPane.NO_OPTION) {
                        break;
                    }
                }
            }
        }
        configManager.writeConfiguration();
    }

    @Override
    public void windowClosed(final WindowEvent e) {
    }

    @Override
    public void windowActivated(final WindowEvent e) {
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Image getBannerImage() {
        return banner;
    }
    // TODO VERDIS COPY
    // obsolete because for failed logins --> only for saving the username

    /**
     * DOCUMENT ME!
     *
     * @param  status     DOCUMENT ME!
     * @param  usernames  DOCUMENT ME!
     * @param  login      DOCUMENT ME!
     */
    private static void handleLoginStatus(final JXLoginPane.Status status,
            final DefaultUserNameStore usernames,
            final JXLoginPane login) {
        if (status == JXLoginPane.Status.SUCCEEDED) {
            // Damit wird sichergestellt, dass dieser als erstes vorgeschlagen wird
            usernames.removeUserName(login.getUserName());
            usernames.saveUserNames();
            usernames.addUserName((login.getUserName()));
            usernames.saveUserNames();
            // Added for RM Plugin functionalty 22.07.2007 Sebastian Puhl
            LagisBroker.getInstance().setLoggedIn(true);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Login erfolgreich");
            }
            new LagisApp();
            // loginWasSuccessful = true;
        } else {
            // Should never gets executed
            LOG.warn("Login fehlgeschlagen");
            System.exit(0);
        }
    }

    // Navigator Methods
    @Override
    public Iterator getUIs() {
        final LinkedList ll = new LinkedList();
        ll.add(this);
        return ll.iterator();
    }

    @Override
    public PluginProperties getProperties() {
        return null;
    }

    @Override
    public Iterator getMethods() {
        final LinkedList ll = new LinkedList();
        return ll.iterator();
    }

    @Override
    public PluginUI getUI(final String id) {
        return this;
    }

    @Override
    public PluginMethod getMethod(final String id) {
        return null;
    }

    @Override
    public void setActive(final boolean active) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("setActive:" + active);
        }
        if (!active) {
            cleanUp();
            // CismapBroker.getInstance().cleanUpSystemRegistry();
            saveLayout(PLUGIN_LAYOUT_PATH);
        }
    }
    // FloatingPluginUI

    @Override
    public void shown() {
    }

    @Override
    public void resized() {
    }

    @Override
    public void moved() {
    }

    @Override
    public void hidden() {
    }

    @Override
    public Collection getMenus() {
        return menues;
    }

    @Override
    public String getId() {
        return "lagis";
    }

    @Override
    public JComponent getComponent() {
        return panAll;
    }

    @Override
    public Collection getButtons() {
        return Arrays.asList(this.toolbar.getComponents());
    }

    @Override
    public void floatingStopped() {
    }

    @Override
    public void floatingStarted() {
    }

    @Override
    public String getValidationMessage() {
        return validationMessage;
    }

    @Override
    public int getStatus() {
        return Validatable.VALID;
    }

    @Override
    public void fireValidationStateChanged(final Object validatedObject) {
        for (final ValidationStateChangedListener listener : validationListeners) {
            listener.validationStateChanged(null);
        }
    }

    @Override
    public void removeValidationStateChangedListener(final ValidationStateChangedListener l) {
        validationListeners.remove(l);
    }

    @Override
    public void addValidationStateChangedListener(final ValidationStateChangedListener l) {
        validationListeners.add(l);
    }

    @Override
    public void showAssistent(final Component parent) {
    }

    /**
     * DOCUMENT ME!
     */
    public void setFlurstueckUnkown() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Art des Flurstücks ist unbekannt (privat)");
        }
        pFlurstueckChooser.setStatusIcon(icoUnknownFlurstueck);
    }

    @Override
    public boolean isWidgetReadOnly() {
        return false;
    }

    @Override
    public void featureSelectionChanged(final Collection<Feature> features) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("FeatureSelectionChanged LagisApp: ");
        }
        if (LagisBroker.getInstance().isInEditMode() && (features != null) && (features.size() > 0)) {
            final Iterator<Feature> it = features.iterator();
            while (it.hasNext()) {
                final Feature curFeature = it.next();
                if (curFeature.canBeSelected()
                            && LagisBroker.getInstance().getMappingComponent().getFeatureCollection().isSelected(
                                curFeature)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("In edit modus, mindestens ein feature selectiert: " + curFeature);
                    }
                    cmdCopyFlaeche.setEnabled(true);
                    return;
                }
            }
            cmdCopyFlaeche.setEnabled(false);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("disable copy nicht alle vorraussetzungen erfüllt");
            }
            cmdCopyFlaeche.setEnabled(false);
        }
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class ImageSelection implements Transferable {

        //~ Instance fields ----------------------------------------------------

        private Image image;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ImageSelection object.
         *
         * @param  image  DOCUMENT ME!
         */
        public ImageSelection(final Image image) {
            this.image = image;
        }

        //~ Methods ------------------------------------------------------------

        // Returns supported flavors
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { DataFlavor.imageFlavor };
        }

        // Returns true if flavor is supported
        @Override
        public boolean isDataFlavorSupported(final DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        // Returns image
        @Override
        public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (!DataFlavor.imageFlavor.equals(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return image;
        }
    }

    /**
     * TODO VERDIS COPY.
     *
     * @version  $Revision$, $Date$
     */
    public static class WundaAuthentification extends LoginService {

        //~ Static fields/initializers -----------------------------------------

        // TODO steht auch so in VERDIS schlecht für ÄNDERUNGEN !!!!!
        public static final String CONNECTION_CLASS = "Sirius.navigator.connection.RMIConnection";
        public static final String CONNECTION_PROXY_CLASS =
            "Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler";

        //~ Instance fields ----------------------------------------------------

        private final Logger log = org.apache.log4j.Logger.getLogger(WundaAuthentification.class);
        private final String standaloneDomain;
        private final String callserverhost;
        private String userString;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new WundaAuthentification object.
         *
         * @param  standaloneDomain  DOCUMENT ME!
         * @param  callserverhost    DOCUMENT ME!
         */
        public WundaAuthentification(final String standaloneDomain, final String callserverhost) {
            this.standaloneDomain = standaloneDomain;
            this.callserverhost = callserverhost;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean authenticate(final String name, final char[] password, final String server) throws Exception {
            if (log.isDebugEnabled()) {
                log.debug("Authentication:");
            }
            System.setProperty("sun.rmi.transport.connectionTimeout", "15");
            final String user = name.split("@")[0];
            final String group = name.split("@")[1];
            LagisBroker.getInstance().setAccountName(name);
            final String callServerURL = "rmi://" + callserverhost + "/callServer";
            if (log.isDebugEnabled()) {
                log.debug("callServerUrl:" + callServerURL);
            }
            final String domain = standaloneDomain;
            userString = name;
            if (log.isDebugEnabled()) {
                log.debug("full qualified username: " + userString + "@" + standaloneDomain);
            }
            final Remote r = null;
            try {
                final Connection connection = ConnectionFactory.getFactory()
                            .createConnection(CONNECTION_CLASS, callServerURL);
                ConnectionSession session = null;
                ConnectionProxy proxy = null;
                if (log.isDebugEnabled()) {
                    log.debug("call server url: " + callServerURL);
                    log.debug("call server host: " + callserverhost);
                }

                final ConnectionInfo connectionInfo = new ConnectionInfo();
                connectionInfo.setCallserverURL(callServerURL);
                connectionInfo.setPassword(new String(password));
                connectionInfo.setUserDomain(domain);
                connectionInfo.setUsergroup(group);
                connectionInfo.setUsergroupDomain(domain);
                connectionInfo.setUsername(user);

                session = ConnectionFactory.getFactory().createSession(connection, connectionInfo, true);
                proxy = ConnectionFactory.getFactory().createProxy(CONNECTION_PROXY_CLASS, session);
                // proxy = ConnectionFactory.getFactory().createProxy(CONNECTION_CLASS,CONNECTION_PROXY_CLASS,
                // connectionInfo,false);
                SessionManager.init(proxy);
                LagisBroker.getInstance().setSession(SessionManager.getSession());
                final String tester = (group + "@" + domain).toLowerCase();
                if (log.isDebugEnabled()) {
                    log.debug("authentication: tester = " + tester);
                    log.debug("authentication: name = " + name);
                    log.debug("authentication: RM Plugin key = " + name + "@" + domain);
                    // setUserString(name); TODO update Configuration depending on username --> formaly after the
                    // handlelogin method --> test if its work!!!!
                }

                // zweimal wegen userdepending konfiguration
                configManager.addConfigurable(LagisBroker.getInstance());
                configManager.configure(LagisBroker.getInstance());
                final Boolean permission = LagisBroker.getInstance().getPermissions().get(tester);
                if (log.isDebugEnabled()) {
                    log.debug("Permissions Hashmap: " + LagisBroker.getInstance().getPermissions());
                }
                if (log.isDebugEnabled()) {
                    log.debug("Permission: " + permission);
                }
                if ((permission != null) && permission) {
                    if (log.isDebugEnabled()) {
                        log.debug("Authentication successfull user has granted readwrite access");
                    }
                    LagisBroker.getInstance().setCoreReadOnlyMode(false);
                    LagisBroker.getInstance().setFullReadOnlyMode(false);
                    // loginWasSuccessful = true;
                    return true;
                } else if (permission != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Authentication successfull user has granted readonly access");
                    }
                    // loginWasSuccessful = true;
                    return true;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("authentication else false: no permission available");
                    }
                    // loginWasSuccessful = false;
                    return false;
                }
//                if (prefs.getRwGroups().contains(tester)) {
//                    //Main.this.readonly=false;
//                    setUserString(name);
//                    //log.debug("RMPlugin: wird initialisiert (VerdisStandalone)");
//                    //log.debug("RMPlugin: Mainframe "+Main.this);
//                    //log.debug("RMPlugin: PrimaryPort "+prefs.getPrimaryPort());
//                    //log.debug("RMPlugin: SecondaryPort "+prefs.getSecondaryPort());
//                    //log.debug("RMPlugin: Username "+(name+"@"+prefs.getStandaloneDomainname()));
//                    //log.debug("RMPlugin: RegistryPath "+prefs.getRmRegistryServerPath());
//                    //rmPlugin = new RMPlugin(Main.this,prefs.getPrimaryPort(),prefs.getSecondaryPort(),prefs.getRmRegistryServerPath(),name+"@"+prefs.getStandaloneDomainname());
//                    //log.debug("RMPlugin: erfolgreich initialisiert (VerdisStandalone)");
//                    return true;
//                } else if (prefs.getUsergroups().contains(tester)) {
//                    //Main.this.readonly=true;
//                    setUserString(name);
//                    //rmPlugin = new RMPlugin(Main.this,prefs.getPrimaryPort(),prefs.getSecondaryPort(),prefs.getRmRegistryServerPath(),name+"@"+prefs.getStandaloneDomainname());
//                    return true;
//                } else {
//                    log.debug("authentication else false");
//                    return false;
//                }
            } catch (Throwable t) {
                log.error("call server url: " + callServerURL);
                log.error("call server host: " + callserverhost);
                log.error("Fehler beim Anmelden ", t);
                return false;
            }
        }
    }
}
