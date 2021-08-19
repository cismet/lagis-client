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

import Sirius.navigator.DefaultNavigatorExceptionHandler;
import Sirius.navigator.connection.Connection;
import Sirius.navigator.connection.ConnectionFactory;
import Sirius.navigator.connection.ConnectionInfo;
import Sirius.navigator.connection.ConnectionSession;
import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.connection.proxy.ConnectionProxy;
import Sirius.navigator.event.CatalogueSelectionListener;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.plugin.interfaces.FloatingPluginUI;
import Sirius.navigator.resource.PropertyManager;
import Sirius.navigator.types.treenode.RootTreeNode;
import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.DescriptionPane;
import Sirius.navigator.ui.DescriptionPaneFS;
import Sirius.navigator.ui.LayoutedContainer;
import Sirius.navigator.ui.MutableMenuBar;
import Sirius.navigator.ui.MutablePopupMenu;
import Sirius.navigator.ui.MutableToolBar;
import Sirius.navigator.ui.attributes.AttributeViewer;
import Sirius.navigator.ui.attributes.editor.AttributeEditor;
import Sirius.navigator.ui.tree.MetaCatalogueTree;
import Sirius.navigator.ui.tree.ResultNodeListener;
import Sirius.navigator.ui.tree.SearchResultsTree;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.middleware.types.Node;
import Sirius.server.newuser.User;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

import net.infonode.docking.*;
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
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.auth.DefaultUserNameStore;
import org.jdesktop.swingx.auth.LoginService;
import org.jdesktop.swingx.error.ErrorInfo;

import org.jdom.Element;

import org.netbeans.api.wizard.WizardDisplayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.URI;
import java.net.URL;

import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.Set;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;

import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;
import de.cismet.cids.custom.commons.gui.BaulastSuchDialog;
import de.cismet.cids.custom.commons.gui.ObjectRendererDialog;
import de.cismet.cids.custom.commons.gui.VermessungsrissSuchDialog;
import de.cismet.cids.custom.commons.searchgeometrylistener.BaulastblattNodesSearchCreateSearchGeometryListener;
import de.cismet.cids.custom.commons.searchgeometrylistener.FlurstueckNodesSearchCreateSearchGeometryListener;
import de.cismet.cids.custom.commons.searchgeometrylistener.NodesSearchCreateSearchGeometryListener;
import de.cismet.cids.custom.commons.searchgeometrylistener.RissNodesSearchCreateSearchGeometryListener;
import de.cismet.cids.custom.navigatorstartuphooks.MotdStartUpHook;
import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisUtils;
import de.cismet.cids.custom.wunda_blau.startuphooks.MotdWundaStartupHook;
import de.cismet.cids.custom.wunda_blau.toolbaritem.TestSetMotdAction;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.navigatorstartuphooks.CidsServerMessageStartUpHook;

import de.cismet.cids.servermessage.CidsServerMessageNotifier;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListener;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListenerEvent;

import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.ClipboardWaitDialog;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.CreateGeometryListener;
import de.cismet.cismap.commons.gui.printing.Scale;
import de.cismet.cismap.commons.gui.statusbar.StatusBar;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.wfsforms.AbstractWFSForm;
import de.cismet.cismap.commons.wfsforms.WFSFormFactory;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.gui.copypaste.Copyable;
import de.cismet.lagis.gui.copypaste.FlurstueckInfoClipboard;
import de.cismet.lagis.gui.copypaste.Pasteable;
import de.cismet.lagis.gui.dialogs.LagisFortfuehrungsanlaesseDialog;
import de.cismet.lagis.gui.panels.*;
import de.cismet.lagis.gui.panels.DMSPanel;
import de.cismet.lagis.gui.tables.NKFTable;

import de.cismet.lagis.interfaces.FeatureSelectionChangedListener;
import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.Widget;

import de.cismet.lagis.report.printing.ReportPrintingWidget;

import de.cismet.lagis.thread.WFSRetrieverFactory;

import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.ValidationStateChangedListener;

import de.cismet.lagis.widget.AbstractWidget;
import de.cismet.lagis.widget.RessortFactory;

import de.cismet.lagis.wizard.ContinuationWizard;

import de.cismet.lookupoptions.gui.OptionsClient;
import de.cismet.lookupoptions.gui.OptionsDialog;

import de.cismet.netutil.Proxy;
import de.cismet.netutil.ProxyHandler;
import de.cismet.netutil.ProxyProperties;

import de.cismet.remote.RESTRemoteControlStarter;

import de.cismet.tools.Static2DTools;
import de.cismet.tools.StaticDebuggingTools;
import de.cismet.tools.StaticDecimalTools;

import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.ConfigurationManager;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.historybutton.HistoryModelListener;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import de.cismet.tools.gui.startup.StaticStartupTools;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class LagisApp extends javax.swing.JFrame implements FloatingPluginUI,
    Configurable,
    WindowListener,
    HistoryModelListener,
    Widget,
    FlurstueckChangeListener,
    FeatureSelectionChangedListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(LagisApp.class);
    private static final Image IMAGE_MAIN = new javax.swing.ImageIcon(LagisApp.class.getResource(
                "/de/cismet/lagis/ressource/icons/main.png")).getImage();

    private static final String FILENAME_LAGIS_CONFIGURATION = "defaultLagisProperties.xml";
    private static final String FILENAME_LOCAL_LAGIS_CONFIGURATION = "lagisProperties.xml";
    private static final String CLASSPATH_LAGIS_CONFIGURATION = "/de/cismet/lagis/configuration/";

    private static final String DIRECTORYPATH_HOME = System.getProperty("user.home");
    private static final String DIRECTORYEXTENSION = System.getProperty("directory.extension");
    private static final String FILESEPARATOR = System.getProperty("file.separator");

    private static final String DIRECTORYNAME_LAGISHOME = ".lagis"
                + ((DIRECTORYEXTENSION != null) ? DIRECTORYEXTENSION : "");

    private static final String DIRECTORYPATH_LAGIS = DIRECTORYPATH_HOME + FILESEPARATOR + DIRECTORYNAME_LAGISHOME;

    private static final String FILEPATH_DEFAULT_LAYOUT = DIRECTORYPATH_LAGIS + FILESEPARATOR + "lagis.layout";
    private static final String FILEPATH_SCREEN = DIRECTORYPATH_LAGIS + FILESEPARATOR + "lagis.screen";

    private static JFrame SPLASH;

    private static String onlineHelpURL;
    private static String newsURL;

    private static final String WIDGET_NAME = "Lagis Mainframe";
    private static final Image BANNER = new javax.swing.ImageIcon(LagisApp.class.getResource(
                "/de/cismet/lagis/ressource/image/login.png")).getImage();

    private static final String HEADER_ERR_MSG = "Fehler";

    //~ Instance fields --------------------------------------------------------

    private final ConfigurationManager configManager = new ConfigurationManager();

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
    private BaulastenPanel pBaulasten;
    private KassenzeichenPanel pKassenzeichen;

    // Views
    private View vFlurstueck;
    private View vVertraege;
    private View vNKFOverview;
    private View vDMS;
    private View vKarte;
    private View vNKF;
    private View vReBe;
    private View vHistory;
    private View vBaulasten;
    private View vKassenzeichen;
    private WFSFormFactory wfsFormFactory;
    private final Set<View> wfsFormViews = new HashSet<>();
    private final List<View> wfs = new ArrayList<>();
    private DockingWindow[] wfsViews;
    // private View vAktenzeichenSuche;
    private JDialog searchByVertragAktenzeichenDialog;
    private JDialog searchByMipaAktenzeichenDialog;
    private final Icon icoKarte = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/titlebar/karte.png"));
    private final Icon icoDMS = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/titlebar/docs.png"));
    private final Icon icoRessort = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/titlebar/ressort.png"));
    private final Icon icoNKF = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/titlebar/sum.png"));
    private final Icon icoRechteDetail = new javax.swing.ImageIcon(getClass().getResource(

                // "/de/cismet/lagis/ressource/icons/titlebar/findgreen.png"));
                "/de/cismet/lagis/ressource/icons/rebe.png"));
    private final Icon icoVerwaltungsbereich = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/verwaltungsbereich.png"));
    private final Icon icoDokumente = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/titlebar/documents.png"));
    private final Icon icoBaulasten = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/cids/custom/commons/gui/Baulast.png"));
    private final Icon icoKassenzeichen = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/info.png"));
    private final Icon miniBack = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/menue/miniBack.png"));
    private final Icon current = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/menue/current.png"));
    private final Icon miniForward = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/menue/miniForward.png"));
    // ICON ÄNDERN
    private MappingComponent mapComponent;
    private ClipboardWaitDialog clipboarder;
    private final StringViewMap viewMap = new StringViewMap();
    private String fortfuehrungLinkFormat;

    // TODO Jean
// private EJBAccessor<KassenzeichenFacadeRemote> verdisCrossoverAccessor;
    // FIXME ugly winning
    private final ActiveLayerModel mappingModel = new ActiveLayerModel();
    private final List<Widget> widgets = new ArrayList<>();
    private boolean isInit = true;
    // Ressort
    private final Set<View> ressortViews = new HashSet<>();
    private DockingWindow[] ressortDockingWindow;
    // Plugin Navigator
    private final ArrayList<JMenuItem> menues = new ArrayList<>();
    // Configurable
    private Dimension windowSize = null;
    private Point windowLocation = null;
    // Validation
    private final ArrayList<ValidationStateChangedListener> validationListeners = new ArrayList<>();
    private final String validationMessage = "Die Komponente ist valide";
    private FlurstueckInfoClipboard fsInfoClipboard;
    private ObjectRendererDialog alkisRendererDialog;

    private DescriptionPane descriptionPane;
    private SearchResultsTree searchResultsTree;

    private boolean listenerEnabled = true;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAcceptChanges;
    private javax.swing.JButton btnAktenzeichenSuche;
    private javax.swing.JButton btnAktenzeichenSuche1;
    private javax.swing.JButton btnDiscardChanges;
    private javax.swing.JButton btnOpenWizard;
    private javax.swing.JButton btnReloadFlurstueck;
    private javax.swing.JButton btnSwitchInEditmode;
    private javax.swing.JButton btnVerdisCrossover;
    private javax.swing.JButton cmdFortfuehrung;
    private javax.swing.JButton cmdPrint;
    private javax.swing.JButton cmdSearchBaulasten;
    private javax.swing.JButton cmdSearchRisse;
    private javax.swing.JButton jButton1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JPopupMenu.Separator jSeparator15;
    private javax.swing.JPopupMenu.Separator jSeparator16;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
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
    private javax.swing.JMenuItem mniAddNutzung;
    private javax.swing.JMenuItem mniBack;
    private javax.swing.JMenuItem mniBaulasten;
    private javax.swing.JMenuItem mniBookmarkManager;
    private javax.swing.JMenuItem mniBookmarkSidebar;
    private javax.swing.JMenuItem mniClippboard;
    private javax.swing.JMenuItem mniClose;
    private javax.swing.JMenuItem mniDMS;
    private javax.swing.JMenuItem mniFlurstueckassistent;
    private javax.swing.JMenuItem mniForward;
    private javax.swing.JMenuItem mniGotoPoint;
    private javax.swing.JMenuItem mniHistory;
    private javax.swing.JMenuItem mniHistorySidebar;
    private javax.swing.JMenuItem mniHome;
    private javax.swing.JMenuItem mniKassenzeichenInformation;
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
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LagisApp object.
     */
    private LagisApp() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public WFSFormFactory getWfsFormFactory() {
        return wfsFormFactory;
    }

    /**
     * DOCUMENT ME!
     */
    private void init() {
        try {
            LOG.info("Starten der LaGIS Applikation");

            UIManager.put(
                "wizard.sidebar.image",
                ImageIO.read(getClass().getResource("/de/cismet/lagis/ressource/image/wizard.png")));
            // TODO FIX
            this.addWindowListener(this);

            LOG.info("Laden der Lagis Konfiguration");
            configManager.setDefaultFileName(FILENAME_LAGIS_CONFIGURATION);
            configManager.setFileName(FILENAME_LOCAL_LAGIS_CONFIGURATION);
            configManager.setClassPathFolder(CLASSPATH_LAGIS_CONFIGURATION);
            configManager.setFolder(DIRECTORYNAME_LAGISHOME);

            initCismetCommonsComponents();

            if (LagisBroker.getInstance().getSession() != null) {
                configManager.addConfigurable(this);

                configManager.configure(this);
            } else {
                LOG.fatal("Es ist kein ordentlich angemeldeter usernamen vorhanden LagIS wird beendet");
                System.exit(0);
            }

            configManager.addConfigurable(OptionsClient.getInstance());
            configManager.configure(OptionsClient.getInstance());

            configManager.configure(LagisBroker.getInstance());

            initComponents();

            ((NKFTable)NKFPanel.getInstance().getNutzungTable()).getAddAction()
                    .addPropertyChangeListener(new PropertyChangeListener() {

                            @Override
                            public void propertyChange(final PropertyChangeEvent evt) {
                                if ("enabled".equals(evt.getPropertyName())) {
                                    mniAddNutzung.setEnabled((Boolean)evt.getNewValue());
                                }
                            }
                        });

            initComponentRegistry(this);
            alkisRendererDialog = new ObjectRendererDialog(this, false, descriptionPane);
            alkisRendererDialog.setSize(1000, 800);

//            // added manually as the GuiBuilder conflicts could not be resolved
//            configureReportButton();
//            configureCopyPasteFlurstueckInfoComponents();

            if (!LagisBroker.getInstance().isCoreReadOnlyMode()) {
                btnOpenWizard.setEnabled(true);
            } else {
                btnOpenWizard.setEnabled(false);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Konfiguriere ALBListener");
            }

            menues.add(menFile);
            menues.add(menEdit);
            menues.add(menHistory);
            menues.add(menBookmarks);
            menues.add(menExtras);
            menues.add(menWindow);

            // added manually as the GuiBuilder conflicts could not be resolved
            configureReportButton();
            configureCopyPasteFlurstueckInfoComponents();

            initRessortPanels();
            initDefaultPanels();

            // TODO LAGISBROKER KNOWS THAT THIS FLURstück IS A REQUESTER
            // OVER INTERFACE
            // LagisBroker.getInstance().setRequester(pFlurstueckSearch);
            LagisBroker.getInstance().setRequester(pFlurstueckChooser);
            btnAcceptChanges.setEnabled(false);
            btnDiscardChanges.setEnabled(false);
            btnSwitchInEditmode.setEnabled(false);

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
            doLayoutInfoNode();
            doConfigKeystrokes();
            panMain.add(rootWindow, BorderLayout.CENTER);
            // ToDo if the setting of the windowsize is not successful pack should be used ?
            // pack();
            initKeySearchComponents();
            mapComponent.getFeatureCollection().addFeatureCollectionListener(pFlurstueck);
            //
            setWindowSize();
            mapComponent.setInternalLayerWidgetAvailable(true);
            for (final Scale s : mapComponent.getScales()) {
                if (s.getDenominator() > 0) {
                    menExtras.add(getScaleMenuItem(s.getText(), s.getDenominator()));
                }
            }

            loadLayout(FILEPATH_DEFAULT_LAYOUT);

            // }
            configManager.addConfigurable(pFlurstueck);
            configManager.configure(pFlurstueck);

            configManager.addConfigurable(pNKF);
            configManager.configure(pNKF);

            configManager.addConfigurable(pKarte);
            configManager.configure(pKarte);

            configManager.addConfigurable(this.pFlurstueckChooser);
            configManager.configure(this.pFlurstueckChooser);

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
            LagisBroker.getInstance().setTitle("LagIS");

            // TODO GEHT SCHIEF WENN ES SCHON DER PARENTFRAME IST
            clipboarder = new ClipboardWaitDialog(StaticSwingTools.getParentFrame(this), true);
            final StatusBar statusBar = new StatusBar(mapComponent);
            DefaultNavigatorExceptionHandler.getInstance().addListener(statusBar.getExceptionHandlerListener());
            LagisBroker.getInstance().setStatusBar(statusBar);
            mapComponent.getFeatureCollection().addFeatureCollectionListener(statusBar);
            CismapBroker.getInstance().addStatusListener(statusBar);
            panStatusbar.add(statusBar);

            final KeyStroke configLoggerKeyStroke = KeyStroke.getKeyStroke('L', InputEvent.CTRL_MASK);
            final Action configAction = new AbstractAction() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        java.awt.EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    Log4JQuickConfig.getSingletonInstance().setVisible(true);
                                }
                            });
                    }
                };
            getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(configLoggerKeyStroke, "CONFIGLOGGING");
            getRootPane().getActionMap().put("CONFIGLOGGING", configAction);

            isInit = false;

            pKarte.setInteractionMode();
            if (SPLASH != null) {
                SPLASH.dispose();
            }
            SPLASH = null;
//            CidsBroker.setMainframe(this);
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

            // ----
            this.fsInfoClipboard.addCopyListener(pRechteDetail);
            this.fsInfoClipboard.addPasteListener(pRechteDetail);

            this.fsInfoClipboard.addCopyListener(pFlurstueck);
            this.fsInfoClipboard.addPasteListener(pFlurstueck);

            final TestSetMotdAction testSetMotdAction = new TestSetMotdAction();
            if (testSetMotdAction.isVisible()) {
                toolbar.add(testSetMotdAction);
            }

            final PropertyChangeListener propChangeListener = new PropertyChangeListener() {

                    @Override
                    public void propertyChange(final PropertyChangeEvent evt) {
                        final String propName = evt.getPropertyName();

                        if (NodesSearchCreateSearchGeometryListener.ACTION_SEARCH_STARTED.equals(propName)) {
                            ComponentRegistry.getRegistry().getSearchResultsTree().clear();
                        } else if (NodesSearchCreateSearchGeometryListener.ACTION_SEARCH_DONE.equals(propName)) {
                            final Node[] nodes = (Node[])evt.getNewValue();
                            if ((nodes == null) || (nodes.length == 0)) {
                                JOptionPane.showMessageDialog(
                                    LagisApp.this,
                                    "<html>Es wurden in dem markierten Bereich<br/>keine Objekte gefunden.",
                                    "Keine Risse gefunden.",
                                    JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                showRenderer(nodes);
                            }
                        } else if (NodesSearchCreateSearchGeometryListener.ACTION_SEARCH_FAILED.equals(propName)) {
                            LOG.error("error while searching", (Exception)evt.getNewValue());
                        }
                    }
                };

            ComponentRegistry.getRegistry().getSearchResultsTree().addResultNodeListener(new ResultNodeListener() {

                    @Override
                    public void resultNodesChanged() {
                        if (!alkisRendererDialog.isVisible()) {
                            StaticSwingTools.showDialog(alkisRendererDialog);
                        }
                    }

                    @Override
                    public void resultNodesCleared() {
                    }

                    @Override
                    public void resultNodesFiltered() {
                    }
                });

            final RissNodesSearchCreateSearchGeometryListener rissCreateSearchGeomListener =
                new RissNodesSearchCreateSearchGeometryListener(LagisBroker.getInstance().getMappingComponent(),
                    propChangeListener,
                    ConnectionContext.createDeprecated());
            final FlurstueckNodesSearchCreateSearchGeometryListener flurstueckCreateSearchGeomListener =
                new FlurstueckNodesSearchCreateSearchGeometryListener(LagisBroker.getInstance().getInstance()
                            .getMappingComponent(),
                    propChangeListener,
                    ConnectionContext.createDeprecated());
            LagisBroker.getInstance()
                    .getMappingComponent()
                    .addCustomInputListener(
                        FlurstueckNodesSearchCreateSearchGeometryListener.NAME,
                        flurstueckCreateSearchGeomListener);
            LagisBroker.getInstance()
                    .getMappingComponent()
                    .putCursor(
                        FlurstueckNodesSearchCreateSearchGeometryListener.NAME,
                        new Cursor(Cursor.CROSSHAIR_CURSOR));
            flurstueckCreateSearchGeomListener.setMode(CreateGeometryListener.POINT);
            LagisBroker.getInstance()
                    .getMappingComponent()
                    .addCustomInputListener(
                        RissNodesSearchCreateSearchGeometryListener.NAME,
                        rissCreateSearchGeomListener);
            LagisBroker.getInstance()
                    .getMappingComponent()
                    .putCursor(RissNodesSearchCreateSearchGeometryListener.NAME, new Cursor(Cursor.CROSSHAIR_CURSOR));
            rissCreateSearchGeomListener.setMode(CreateGeometryListener.POINT);

            final BaulastblattNodesSearchCreateSearchGeometryListener baulastblattCreateSearchGeomListener =
                new BaulastblattNodesSearchCreateSearchGeometryListener(LagisBroker.getInstance().getMappingComponent(),
                    propChangeListener,
                    ConnectionContext.createDeprecated());
            LagisBroker.getInstance()
                    .getMappingComponent()
                    .addCustomInputListener(
                        BaulastblattNodesSearchCreateSearchGeometryListener.NAME,
                        baulastblattCreateSearchGeomListener);
            LagisBroker.getInstance()
                    .getMappingComponent()
                    .putCursor(
                        BaulastblattNodesSearchCreateSearchGeometryListener.NAME,
                        new Cursor(Cursor.CROSSHAIR_CURSOR));
            baulastblattCreateSearchGeomListener.setMode(CreateGeometryListener.POINT);

            initTotd();
            initStartupHooks();
        } catch (final Exception exception) {
            final ErrorInfo errorInfo = new ErrorInfo(
                    "LaGIS kann nicht gestartet werden",
                    "Fehler beim konstruieren des LaGIS Objektes",
                    null,
                    "",
                    exception,
                    null,
                    null);
            JXErrorPane.showDialog(this, errorInfo);

            LOG.fatal("Fehler beim konstruieren des LaGIS Objektes", exception);
            System.exit(1);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getFortfuehrungLinkFormat() {
        return fortfuehrungLinkFormat;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   frame  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void initComponentRegistry(final JFrame frame) throws Exception {
        PropertyManager.getManager().setEditable(true);

        searchResultsTree = new SearchResultsTree() {

                @Override
                public void setResultNodes(final Node[] nodes,
                        final boolean append,
                        final PropertyChangeListener listener,
                        final boolean simpleSort,
                        final boolean sortActive) {
                    super.setResultNodes(nodes, append, listener, simpleSort, false);
                }
            };

        descriptionPane = new DescriptionPaneFS();
        final MutableToolBar toolBar = new MutableToolBar();
        final MutableMenuBar menuBar = new MutableMenuBar();
        final LayoutedContainer container = new LayoutedContainer(toolBar, menuBar, true);
        final AttributeViewer attributeViewer = new AttributeViewer();
        final AttributeEditor attributeEditor = new AttributeEditor();
        final MutablePopupMenu popupMenu = new MutablePopupMenu();

        final RootTreeNode rootTreeNode = new RootTreeNode(new Node[0]);
        final MetaCatalogueTree metaCatalogueTree = new MetaCatalogueTree(
                rootTreeNode,
                PropertyManager.getManager().isEditable(),
                true,
                PropertyManager.getManager().getMaxConnections());

        final CatalogueSelectionListener catalogueSelectionListener = new CatalogueSelectionListener(
                attributeViewer,
                descriptionPane);
        searchResultsTree.addTreeSelectionListener(catalogueSelectionListener);

        ComponentRegistry.registerComponents(
            frame,
            container,
            menuBar,
            toolBar,
            popupMenu,
            metaCatalogueTree,
            searchResultsTree,
            null,
            attributeViewer,
            attributeEditor,
            descriptionPane);
    }

    /**
     * DOCUMENT ME!
     */
    private void configureReportButton() {
        final JButton reportButton = new JButton();
        reportButton.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/report.png")));
        reportButton.setToolTipText("Report Flurstück-Details");
        reportButton.setBorderPainted(false);
        reportButton.setFocusable(false);
        reportButton.setPreferredSize(new java.awt.Dimension(23, 23));

        reportButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent ae) {
                    final ReportPrintingWidget widget = new ReportPrintingWidget(LagisApp.this, true);
                    StaticSwingTools.showDialog(widget);
                }
            });
        toolbar.add(reportButton);
    }

    /**
     * DOCUMENT ME!
     */
    private void configureCopyPasteFlurstueckInfoComponents() {
        final JButton copyButton = new JButton();
        copyButton.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/fs_info_copy.png")));
        copyButton.setBorderPainted(false);
        copyButton.setFocusable(false);
        copyButton.setPreferredSize(new java.awt.Dimension(23, 23));

        // ---------------------
        final JButton pasteButton = new JButton();
        pasteButton.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/fs_info_paste.png")));
        pasteButton.setBorderPainted(false);
        pasteButton.setFocusable(false);
        pasteButton.setPreferredSize(new java.awt.Dimension(23, 23));

        // ---------------------
        final JSeparator sep = new JSeparator();
        sep.setOrientation(javax.swing.SwingConstants.VERTICAL);
        sep.setMaximumSize(new java.awt.Dimension(2, 32767));
        sep.setMinimumSize(new java.awt.Dimension(2, 25));
        sep.setPreferredSize(new java.awt.Dimension(2, 23));

        // ---------------------
        this.toolbar.add(sep);
        this.toolbar.add(copyButton);
        this.toolbar.add(pasteButton);

        this.fsInfoClipboard = new FlurstueckInfoClipboard(this, copyButton, pasteButton);
    }

//    @Override
//    public void setVisible(final boolean visible) {
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("Plugin setVisible ignoriert: " + visible);
//        }
//    }

    /**
     * DOCUMENT ME!
     */
    private static void handleLogin() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Intialisiere Loginframe");
        }

        // TODO VERDIS COPY
        final DefaultUserNameStore usernames = new DefaultUserNameStore();
        final Preferences appPrefs = Preferences.userNodeForPackage(LagisApp.class);
        usernames.setPreferences(appPrefs.node("login"));
        final LagisApp.WundaAuthentification wa = new LagisApp.WundaAuthentification(
                LagisBroker.getInstance().getDomain(),
                LagisBroker.getInstance().getCallserverUrl(),
                LagisBroker.getInstance().getConnectionClass(),
                LagisBroker.getInstance().isCompressionEnabled(),
                LagisBroker.getInstance().getProxyProperties());

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
        final JFrame dummy = null;
        final JXLoginPane.JXLoginFrame d = new JXLoginPane.JXLoginFrame(login);

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
        d.setIconImage(IMAGE_MAIN);

        login.setPassword("".toCharArray());
        try {
            ((JXPanel)((JXPanel)login.getComponent(1)).getComponent(1)).getComponent(3).requestFocus();
        } catch (Exception skip) {
        }
        d.setIconImage(IMAGE_MAIN);
        d.setAlwaysOnTop(true);
        d.setVisible(true);
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

        // First local configuration than serverconfiguration
        configManager.configure(mappingModel);
        mapComponent.preparationSetMappingModel(mappingModel);
        configManager.configure(mapComponent);
        mapComponent.setMappingModel(mappingModel);
        configManager.configure(WFSRetrieverFactory.getInstance());
        LagisBroker.getInstance().checkNKFAdminPermissionsOnServer();
    }

    /**
     * DOCUMENT ME!
     */
    private void initCismetCommonsComponents() {
        mapComponent = new MappingComponent();
        mapComponent.addHistoryModelListener(this);
        CismapBroker.getInstance().setMappingComponent(mapComponent);
        LagisBroker.getInstance().setMappingComponent(mapComponent);
        wfsFormFactory = WFSFormFactory.getInstance(mapComponent);
    }

    /**
     * DOCUMENT ME!
     */
    private static void initLog4J() {
        // CUSTOM LOG4J
        try {
            if (StaticDebuggingTools.checkHomeForFile("cismetCustomLog4JConfigurationInDotLagis")) {
                try {
                    org.apache.log4j.PropertyConfigurator.configure(DIRECTORYPATH_LAGIS + FILESEPARATOR
                                + "custom.log4j.properties");
                    LOG.info("CustomLoggingOn");
                } catch (Exception ex) {
                    org.apache.log4j.PropertyConfigurator.configure(ClassLoader.getSystemResource(
                            "log4j.properties"));
                }
            } else {
                PropertyConfigurator.configure(LagisApp.class.getResource(
                        "/de/cismet/lagis/configuration/log4j.properties"));
                LOG.info("Log4J System erfolgreich konfiguriert");
            }
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Fehler bei Log4J-Config", e);
            }
            System.err.println("Fehler bei Log4J Initialisierung");
            e.printStackTrace();
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
                final Collection<String> keySet = ressorts.keySet();
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

                        if (ressort instanceof Copyable) {
                            this.fsInfoClipboard.addCopyListener((Copyable)ressort);
                        }

                        if (ressort instanceof Pasteable) {
                            this.fsInfoClipboard.addPasteListener((Pasteable)ressort);
                        }

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

                int counter = 0;
                for (final View view : ressortViews) {
                    ressortDockingWindow[counter] = view;
                    counter++;
                }
                // TODO for Navigator
// menues.remove(menHelp);
// menues.add(wfsFormsMenu);
                menues.add(ressortMenue);
                menues.add(menHelp);
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
        pFlurstueck = VerwaltungsPanel.getInstance();
        pVertraege = new VertraegePanel();
        pNKFOverview = NKFOverviewPanel.getInstance();
        pDMS = new DMSPanel();
        pKarte = new KartenPanel();
        pNKF = NKFPanel.getInstance();
        pRechteDetail = new ReBePanel();
        boolean isJavaFxAvailable;
        try {
            LagisApp.class.getClassLoader().loadClass("javafx.embed.swing.JFXPanel");
            isJavaFxAvailable = true;
        } catch (ClassNotFoundException e) {
            isJavaFxAvailable = false;
        }
        if (isJavaFxAvailable) {
            pHistory = new HistoryPanel();
            configManager.addConfigurable(pHistory);
            configManager.configure(pHistory);
        } else {
            LOG.error("Error. No Histroy Component available");
        }
        pBaulasten = new BaulastenPanel();
        pKassenzeichen = KassenzeichenPanel.getInstance();

        if (pHistory != null) {
            widgets.add(pHistory);
        }
        widgets.add(pFlurstueck);
        widgets.add(pVertraege);
        widgets.add(pNKFOverview);
        widgets.add(pDMS);
        widgets.add(pKarte);
        widgets.add(pNKF);
        widgets.add(pRechteDetail);
        widgets.add(pBaulasten);
        widgets.add(pKassenzeichen);
        widgets.add(pFlurstueckChooser);

        LOG.info("Referenz auf die mainApplikation: " + this);
        widgets.add(this);
        LagisBroker.getInstance().addWidgets(widgets);
        LagisBroker.getInstance().resetWidgets();
    }

    /**
     * DOCUMENT ME!
     */
    private void initInfoNode() {
        vFlurstueck = new View("Verwaltungsbereiche", icoVerwaltungsbereich, pFlurstueck);
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
        if (pHistory != null) {
            vHistory = new View("Historie", icoRessort, pHistory);
        } else {
            final JPanel p = new JPanel(new BorderLayout());
            p.add(new JLabel("... no History for you ..."), BorderLayout.CENTER);
            vHistory = new View("Historie", icoRessort, p);
        }
        viewMap.addView("Historie", vHistory);
        vBaulasten = new View("Baulasten", icoBaulasten, pBaulasten);
        viewMap.addView("Baulasten", vBaulasten);

        vKassenzeichen = new View("Kassenzeicheninformation", icoKassenzeichen, pKassenzeichen);
        viewMap.addView("Kassenzeicheninformation", vKassenzeichen);

        rootWindow = DockingUtil.createRootWindow(viewMap, true);
        LagisBroker.getInstance().setRootWindow(rootWindow);

        // InfoNode configuration
        rootWindow.addTabMouseButtonListener(DockingWindowActionMouseButtonListener.MIDDLE_BUTTON_CLOSE_LISTENER);
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

        LagisBroker.getInstance().setTitleBarComponentpainter(LagisBroker.DEFAULT_MODE_COLOR);
        rootWindow.getRootWindowProperties()
                .getTabWindowProperties()
                .getTabbedPanelProperties()
                .setPaintTabAreaShadow(true);
        rootWindow.getRootWindowProperties().getTabWindowProperties().getTabbedPanelProperties().setShadowSize(10);
        rootWindow.getRootWindowProperties()
                .getTabWindowProperties()
                .getTabbedPanelProperties()
                .setShadowStrength(0.8f);
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
                                    new DockingWindow[] { vDMS, vBaulasten, vKassenzeichen }))),
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
                                    new DockingWindow[] { vDMS, vBaulasten, vKassenzeichen }))),
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
                                    new DockingWindow[] { vDMS, vBaulasten, vKassenzeichen }))),
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
                                    new DockingWindow[] { vDMS, vBaulasten, vKassenzeichen }))),
                        new TabWindow(
                            new DockingWindow[] { vKarte, vReBe, vVertraege, vNKF, vHistory })));
            }
        }

        for (int i = 0; i < wfsViews.length; i++) {
            wfsViews[i].close();
        }

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
    }

    /**
     * DOCUMENT ME!
     */
    private void initKeySearchComponents() {
        try {
        } catch (Exception ex) {
            LOG.error("Fehler beim initalisieren der Flurstueck Comboboxen: ", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initTotd() {
        try {
            if (SessionManager.getConnection().hasConfigAttr(
                            SessionManager.getSession().getUser(),
                            "csm://"
                            + MotdWundaStartupHook.MOTD_MESSAGE_TOTD)) {
                CidsServerMessageNotifier.getInstance()
                        .subscribe(new CidsServerMessageNotifierListener() {

                                @Override
                                public void messageRetrieved(final CidsServerMessageNotifierListenerEvent event) {
                                    try {
                                        final String totd = (String)event.getMessage().getContent();
                                        LagisBroker.getInstance().setTotd(totd);
                                    } catch (final Exception ex) {
                                        LOG.warn(ex, ex);
                                    }
                                }
                            },
                            MotdWundaStartupHook.MOTD_MESSAGE_TOTD);
            }
        } catch (final ConnectionException ex) {
            LOG.warn("Konnte Rechte an csm://" + MotdWundaStartupHook.MOTD_MESSAGE_TOTD
                        + " nicht abfragen. Keine Titleleiste des Tages !",
                ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initStartupHooks() {
        try {
            new MotdStartUpHook().applicationStarted();
            new CidsServerMessageStartUpHook().applicationStarted();
        } catch (Exception ex) {
            LOG.error("Fehler beim Ausführen der StartupHooks: ", ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jSeparator5 = new javax.swing.JSeparator();
        toolbar = new javax.swing.JToolBar();
        pFlurstueckChooser = new FlurstueckChooser(FlurstueckChooser.Mode.SEARCH);
        jSeparator1 = new javax.swing.JSeparator();
        btnSwitchInEditmode = new javax.swing.JButton();
        btnDiscardChanges = new javax.swing.JButton();
        btnAcceptChanges = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jButton1 = new javax.swing.JButton();
        cmdSearchBaulasten = new javax.swing.JButton();
        cmdSearchRisse = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        cmdPrint = new javax.swing.JButton();
        btnReloadFlurstueck = new javax.swing.JButton();
        btnOpenWizard = new javax.swing.JButton();
        btnAktenzeichenSuche = new javax.swing.JButton();
        btnAktenzeichenSuche1 = new javax.swing.JButton();
        btnVerdisCrossover = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        cmdFortfuehrung = new javax.swing.JButton();
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
        mniFlurstueckassistent = new javax.swing.JMenuItem();
        mniAddNutzung = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JPopupMenu.Separator();
        mniGotoPoint = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JSeparator();
        mniScale = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JPopupMenu.Separator();
        menWindow = new javax.swing.JMenu();
        mniMap = new javax.swing.JMenuItem();
        mniVerwaltungsbereich = new javax.swing.JMenuItem();
        mniVorgaenge = new javax.swing.JMenuItem();
        mniNKFOverview = new javax.swing.JMenuItem();
        mniNutzung = new javax.swing.JMenuItem();
        mniReBe = new javax.swing.JMenuItem();
        mniDMS = new javax.swing.JMenuItem();
        mniHistory = new javax.swing.JMenuItem();
        mniBaulasten = new javax.swing.JMenuItem();
        mniKassenzeichenInformation = new javax.swing.JMenuItem();
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

        jButton1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/alk.png"))); // NOI18N
        jButton1.setToolTipText("Alkis Renderer");
        jButton1.setEnabled(false);
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        toolbar.add(jButton1);

        cmdSearchBaulasten.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/baulastsuche.png"))); // NOI18N
        cmdSearchBaulasten.setToolTipText("Baulast-Suche");
        cmdSearchBaulasten.setFocusPainted(false);
        cmdSearchBaulasten.setFocusable(false);
        cmdSearchBaulasten.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdSearchBaulasten.setMaximumSize(new java.awt.Dimension(28, 28));
        cmdSearchBaulasten.setMinimumSize(new java.awt.Dimension(28, 28));
        cmdSearchBaulasten.setPreferredSize(new java.awt.Dimension(28, 28));
        cmdSearchBaulasten.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdSearchBaulasten.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSearchBaulastenActionPerformed(evt);
                }
            });
        toolbar.add(cmdSearchBaulasten);
        cmdSearchBaulasten.setVisible(LagisBroker.getInstance().checkPermissionBaulasten());

        cmdSearchRisse.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/vermessungsrisssuche.png"))); // NOI18N
        cmdSearchRisse.setToolTipText("Vermessungsriss-Suche");
        cmdSearchRisse.setFocusPainted(false);
        cmdSearchRisse.setFocusable(false);
        cmdSearchRisse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdSearchRisse.setMaximumSize(new java.awt.Dimension(28, 28));
        cmdSearchRisse.setMinimumSize(new java.awt.Dimension(28, 28));
        cmdSearchRisse.setPreferredSize(new java.awt.Dimension(28, 28));
        cmdSearchRisse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdSearchRisse.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSearchRisseActionPerformed(evt);
                }
            });
        toolbar.add(cmdSearchRisse);
        cmdSearchRisse.setVisible(LagisBroker.getInstance().checkPermissionRisse());

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator4.setMaximumSize(new java.awt.Dimension(2, 32767));
        jSeparator4.setMinimumSize(new java.awt.Dimension(2, 25));
        jSeparator4.setPreferredSize(new java.awt.Dimension(2, 23));
        toolbar.add(jSeparator4);

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
        btnAktenzeichenSuche.setToolTipText("Suche Flurstücke nach Aktenzeichen (Vertrag)...");
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

        btnAktenzeichenSuche1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/Aktenzeichensuche4.png"))); // NOI18N
        btnAktenzeichenSuche1.setToolTipText("Suche Flurstücke nach Aktenzeichen (Vermietung/Verpachtung)...");
        btnAktenzeichenSuche1.setBorderPainted(false);
        btnAktenzeichenSuche1.setFocusable(false);
        btnAktenzeichenSuche1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAktenzeichenSuche1.setPreferredSize(new java.awt.Dimension(23, 23));
        btnAktenzeichenSuche1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAktenzeichenSuche1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAktenzeichenSuche1ActionPerformed(evt);
                }
            });
        toolbar.add(btnAktenzeichenSuche1);

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

        cmdFortfuehrung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/fortfuehrung.png"))); // NOI18N
        cmdFortfuehrung.setToolTipText("Fortführung");
        cmdFortfuehrung.setBorderPainted(false);
        cmdFortfuehrung.setFocusable(false);
        cmdFortfuehrung.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdFortfuehrung.setPreferredSize(new java.awt.Dimension(23, 23));
        cmdFortfuehrung.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdFortfuehrung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdFortfuehrungActionPerformed(evt);
                }
            });
        try {
            cmdFortfuehrung.setVisible(SessionManager.getConnection().getConfigAttr(
                    SessionManager.getSession().getUser(),
                    "lagis.fortfuehrungsanlaesse.dialog") != null);
        } catch (final Exception ex) {
            LOG.error("error while checking for grundis.fortfuehrungsanlaesse.dialog", ex);
            cmdFortfuehrung.setVisible(false);
        }
        toolbar.add(cmdFortfuehrung);

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
        mniOptions.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniOptionsActionPerformed(evt);
                }
            });
        menExtras.add(mniOptions);

        jSeparator12.setEnabled(false);
        menExtras.add(jSeparator12);

        mniFlurstueckassistent.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_F,
                java.awt.event.InputEvent.SHIFT_MASK
                        | java.awt.event.InputEvent.CTRL_MASK));
        mniFlurstueckassistent.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/wizardicon.png"))); // NOI18N
        mniFlurstueckassistent.setText("Flurstücksassistent");
        mniFlurstueckassistent.setToolTipText("Flurstücksassistent öffnen");

        final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                btnOpenWizard,
                org.jdesktop.beansbinding.ELProperty.create("${enabled}"),
                mniFlurstueckassistent,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        mniFlurstueckassistent.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniFlurstueckassistentActionPerformed(evt);
                }
            });
        menExtras.add(mniFlurstueckassistent);

        mniAddNutzung.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_N,
                java.awt.event.InputEvent.SHIFT_MASK
                        | java.awt.event.InputEvent.CTRL_MASK));
        mniAddNutzung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/menue/addNutzung.png"))); // NOI18N
        mniAddNutzung.setText("Nutzung hinzufügen");
        mniAddNutzung.setToolTipText("Nutzung hinzufügen");
        mniAddNutzung.setEnabled(false);
        mniAddNutzung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniAddNutzungActionPerformed(evt);
                }
            });
        menExtras.add(mniAddNutzung);
        menExtras.add(jSeparator15);

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
        menExtras.add(jSeparator16);

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

        mniBaulasten.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_0,
                java.awt.event.InputEvent.CTRL_MASK));
        mniBaulasten.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/Baulast.png")));
        mniBaulasten.setText("Baulasten");
        mniBaulasten.setToolTipText("Dem Flurstück zugehörige Baulasten");
        mniBaulasten.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniBaulastenActionPerformed(evt);
                }
            });
        menWindow.add(mniBaulasten);

        mniKassenzeichenInformation.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/titlebar/info.png"))); // NOI18N
        mniKassenzeichenInformation.setText("Kassenzeicheninformation");
        mniKassenzeichenInformation.setToolTipText("Informationen zum aktuellen Flurstück");
        mniKassenzeichenInformation.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniKassenzeichenInformationActionPerformed(evt);
                }
            });
        menWindow.add(mniKassenzeichenInformation);

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

        bindingGroup.bind();

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnOpenWizardActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnOpenWizardActionPerformed
        WizardDisplayer.showWizard(new ContinuationWizard().createWizard(),
            new Rectangle(20, 20, 600, 400));
    }                                                                                 //GEN-LAST:event_btnOpenWizardActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniNewsActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniNewsActionPerformed
        openUrlInExternalBrowser(newsURL);
    }                                                                           //GEN-LAST:event_mniNewsActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniOnlineHelpActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniOnlineHelpActionPerformed
        openUrlInExternalBrowser(onlineHelpURL);
    }                                                                                 //GEN-LAST:event_mniOnlineHelpActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniHistoryActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniHistoryActionPerformed
        showOrHideView(vHistory);
    }                                                                              //GEN-LAST:event_mniHistoryActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniDMSActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniDMSActionPerformed
        showOrHideView(vDMS);
    }                                                                          //GEN-LAST:event_mniDMSActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniReBeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniReBeActionPerformed
        showOrHideView(vReBe);
    }                                                                           //GEN-LAST:event_mniReBeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniNutzungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniNutzungActionPerformed
        showOrHideView(vNKF);
    }                                                                              //GEN-LAST:event_mniNutzungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniNKFOverviewActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniNKFOverviewActionPerformed
        showOrHideView(vNKFOverview);
    }                                                                                  //GEN-LAST:event_mniNKFOverviewActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniVorgaengeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniVorgaengeActionPerformed
        showOrHideView(vVertraege);
    }                                                                                //GEN-LAST:event_mniVorgaengeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniVerwaltungsbereichActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniVerwaltungsbereichActionPerformed
        showOrHideView(vFlurstueck);
    }                                                                                         //GEN-LAST:event_mniVerwaltungsbereichActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniMapActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniMapActionPerformed
        showOrHideView(vKarte);
    }                                                                          //GEN-LAST:event_mniMapActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniGotoPointActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniGotoPointActionPerformed
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
    }                                                                                //GEN-LAST:event_mniGotoPointActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniScaleActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniScaleActionPerformed
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
    }                                                                            //GEN-LAST:event_mniScaleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniHomeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniHomeActionPerformed
        if (mapComponent != null) {
            mapComponent.gotoInitialBoundingBox();
        }
    }                                                                           //GEN-LAST:event_mniHomeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniForwardActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniForwardActionPerformed
        if ((mapComponent != null) && mapComponent.isForwardPossible()) {
            mapComponent.forward(true);
        }
    }                                                                              //GEN-LAST:event_mniForwardActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniBackActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniBackActionPerformed
        if ((mapComponent != null) && mapComponent.isBackPossible()) {
            mapComponent.back(true);
        }
    }                                                                           //GEN-LAST:event_mniBackActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniRefreshActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniRefreshActionPerformed
        LagisBroker.getInstance().reloadFlurstueck();
    }                                                                              //GEN-LAST:event_mniRefreshActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniClippboardActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniClippboardActionPerformed
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
    } //GEN-LAST:event_mniClippboardActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniResetWindowLayoutActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniResetWindowLayoutActionPerformed
        doLayoutInfoNode();
    }                                                                                        //GEN-LAST:event_mniResetWindowLayoutActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniSaveLayoutActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniSaveLayoutActionPerformed
        final JFileChooser fc = new JFileChooser(DIRECTORYPATH_LAGIS);
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
    } //GEN-LAST:event_mniSaveLayoutActionPerformed

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
    private void mniCloseActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniCloseActionPerformed
        this.cleanUp();
        this.dispose();
    }                                                                            //GEN-LAST:event_mniCloseActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniLoadLayoutActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniLoadLayoutActionPerformed
        final JFileChooser fc = new JFileChooser(DIRECTORYPATH_LAGIS);
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
    } //GEN-LAST:event_mniLoadLayoutActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  file  DOCUMENT ME!
     */
    // TODO add to configure method
    public void saveLayout(final String file) {
        LagisBroker.getInstance().setTitleBarComponentpainter(LagisBroker.DEFAULT_MODE_COLOR);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Saving Layout.. to " + file);
        }
        final File layoutFile = new File(file);
        try {
            if (!layoutFile.exists()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Saving Layout.. File '" + file + "' does not exit");
                }
                layoutFile.createNewFile();
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Saving Layout.. File '" + file + "' does exit");
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
            LOG.error("A failure occured during writing the layout file " + file, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnReloadFlurstueckActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnReloadFlurstueckActionPerformed
        LagisBroker.getInstance().reloadFlurstueck();
    }                                                                                       //GEN-LAST:event_btnReloadFlurstueckActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnDiscardChangesActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnDiscardChangesActionPerformed
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
            if (LagisBroker.getInstance().releaseLocks()) {
                // datamodell refactoring 22.10.07e
                if ((LagisBroker.getInstance().getCurrentFlurstueck() != null)
                            && LagisBroker.getInstance().getCurrentFlurstueck().getFlurstueckSchluessel()
                            .isGesperrt()) {
                    LagisBroker.getInstance().setTitleBarComponentpainter(LagisBroker.LOCK_MODE_COLOR);
                } else {
                    LagisBroker.getInstance().setTitleBarComponentpainter(LagisBroker.DEFAULT_MODE_COLOR);
                }
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
    } //GEN-LAST:event_btnDiscardChangesActionPerformed

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
    private void btnAcceptChangesActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAcceptChangesActionPerformed
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
                    btnAcceptChanges.setEnabled(false);
                    btnDiscardChanges.setEnabled(false);
                    btnSwitchInEditmode.setEnabled(false);
                    // TODO TEST IT!!!!
                    LagisBroker.getInstance().getMappingComponent().setReadOnly(true);
                    if (LagisBroker.getInstance().releaseLocks()) {
                        // datamodell refactoring 22.10.07
                        if ((LagisBroker.getInstance().getCurrentFlurstueck() != null)
                                    && LagisBroker.getInstance().getCurrentFlurstueck().getFlurstueckSchluessel()
                                    .isGesperrt()) {
                            LagisBroker.getInstance().setTitleBarComponentpainter(LagisBroker.LOCK_MODE_COLOR);
                        } else {
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
    } //GEN-LAST:event_btnAcceptChangesActionPerformed

    /**
     * boolean isInEditMode = false;
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnSwitchInEditmodeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnSwitchInEditmodeActionPerformed
        if (LOG.isDebugEnabled()) {
            LOG.debug("Versuche in Editiermodus zu wechseln: ");
        }
        if (LagisBroker.getInstance().acquireLock()) {
            if (LagisBroker.getInstance().isCurrentFlurstueckLockedByUser()) {
                LagisBroker.getInstance()
                        .setTitleBarComponentpainter(LagisBroker.LOCK_MODE_COLOR, LagisBroker.EDIT_MODE_COLOR);
            } else {
                LagisBroker.getInstance().setTitleBarComponentpainter(LagisBroker.EDIT_MODE_COLOR);
            }
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
    } //GEN-LAST:event_btnSwitchInEditmodeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniLockLayoutActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniLockLayoutActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_mniLockLayoutActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniOptionsActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniOptionsActionPerformed
        final OptionsDialog od = new OptionsDialog(this, true);
        od.setLocationRelativeTo(this);
        od.setVisible(true);
    }                                                                              //GEN-LAST:event_mniOptionsActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAktenzeichenSucheActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAktenzeichenSucheActionPerformed
        if (searchByVertragAktenzeichenDialog == null) {
            final FlurstueckeByAktenzeichenSearchPanel searchPanel = new FlurstueckeByAktenzeichenSearchPanel(
                    FlurstueckeByAktenzeichenSearchPanel.By.VERTRAG);
            searchByVertragAktenzeichenDialog = new JDialog(this,
                    searchPanel.getTitle(),
                    false);
            searchByVertragAktenzeichenDialog.add(searchPanel);
            searchByVertragAktenzeichenDialog.pack();
            searchByVertragAktenzeichenDialog.setIconImage(((ImageIcon)searchPanel.getIcon()).getImage());
            StaticSwingTools.showDialog(searchByVertragAktenzeichenDialog);
        } else {
            if (searchByVertragAktenzeichenDialog.isVisible()) {
                searchByVertragAktenzeichenDialog.setVisible(false);
            } else {
                StaticSwingTools.showDialog(searchByVertragAktenzeichenDialog);
            }
        }
    }                                                                                        //GEN-LAST:event_btnAktenzeichenSucheActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniBaulastenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniBaulastenActionPerformed
        showOrHideView(vBaulasten);
    }                                                                                //GEN-LAST:event_mniBaulastenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdPrintActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdPrintActionPerformed
        mapComponent.showPrintingSettingsDialog();
    }                                                                            //GEN-LAST:event_cmdPrintActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniPrintActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniPrintActionPerformed
        cmdPrintActionPerformed(evt);
    }                                                                            //GEN-LAST:event_mniPrintActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        final MetaObject mo = getCurrentFlurstueckMO();
        if (mo != null) {
            showRenderer(mo);
        } else {
            showErrorMessage("<html>Es wurde kein entsprechendes Alkis Flurstück gefunden");
        }
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private MetaObject getCurrentFlurstueckMO() {
        final FlurstueckCustomBean flurstueckBean = LagisBroker.getInstance().getCurrentFlurstueck();
        if (flurstueckBean == null) {
            LOG.warn("es wurde versucht den alkisrenderer aufzurufen, obwohl kein flurstueck selektiert ist");
            // kann nicht passieren, da das Button disabled ist wenn kein Flurstück selektiert ist
            return null;
        }
        final FlurstueckSchluesselCustomBean flurstueckSchluesselBean = flurstueckBean.getFlurstueckSchluessel();
        if (flurstueckSchluesselBean == null) {
            LOG.warn(
                "es wurde versucht den alkisrenderer von einen flurstueck aufzurufen, der keinen schluessel besitzt");
            // sollte nicht passieren können, jedes flurstueck hat einen schluessel
            return null;
        }
        final String wundaDomain = "WUNDA_BLAU";
        final MetaClass metaclass = ClassCacheMultiple.getMetaClass(wundaDomain, "alkis_landparcel");
        if (metaclass != null) {
            final int land = 5;
            try {
                final int gemarkung = flurstueckSchluesselBean.getGemarkung().getSchluessel();
                final int flur = flurstueckSchluesselBean.getFlur();
                final int zaehler = flurstueckSchluesselBean.getFlurstueckZaehler();
                final int nenner = flurstueckSchluesselBean.getFlurstueckNenner();
                final String alkisCode = (nenner > 0)
                    ? AlkisUtils.generateLandparcelCode(land, gemarkung, flur, zaehler, nenner)
                    : AlkisUtils.generateLandparcelCode(land, gemarkung, flur, zaehler);
                final String query = "SELECT "
                            + metaclass.getID() + ", "
                            + metaclass.getTableName() + ".id "
                            + "FROM " + metaclass.getTableName() + " "
                            + "WHERE " + metaclass.getTableName() + ".alkis_id like '" + alkisCode + "';";
                final MetaObject[] mos = CidsBroker.getInstance().getMetaObject(query, wundaDomain);
                if ((mos != null) && (mos.length > 0)) {
                    return mos[0];
                } else {
                    return null;
                }
            } catch (final Exception ex) {
                LOG.error("fehler beim suchen des alkis flurstücks", ex);
                showErrorMessage("<html>Es ist ein Fehler beim Suchen des Alkis Flurstücks aufgetreten.<br/>"
                            + ex.getMessage());
            }
        } else {
            showErrorMessage(
                "<html>Die Meta-Klasse 'alkis_landparcel' konnte nicht geladen werden<br/>Überprüfen Sie die Benutzerrechte.");
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  nodes  DOCUMENT ME!
     */
    public void showRenderer(final Node[] nodes) {
        try {
            alkisRendererDialog.setNodes(nodes);
        } catch (Exception ex) {
            // TODO fehlerdialog
            LOG.error("error while loading renderer", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  metaObject  DOCUMENT ME!
     */
    public void showRenderer(final MetaObject metaObject) {
        try {
            alkisRendererDialog.setNodes(Arrays.asList(new MetaObjectNode(metaObject.getBean())).toArray(
                    new MetaObjectNode[0]));
            if (!alkisRendererDialog.isVisible()) {
                StaticSwingTools.showDialog(alkisRendererDialog);
            }
        } catch (Exception ex) {
            // TODO fehlerdialog
            LOG.error("error while loading renderer", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniFlurstueckassistentActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniFlurstueckassistentActionPerformed
        btnOpenWizardActionPerformed(evt);
    }                                                                                          //GEN-LAST:event_mniFlurstueckassistentActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniAddNutzungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniAddNutzungActionPerformed
        ((NKFTable)NKFPanel.getInstance().getNutzungTable()).getAddAction().actionPerformed(evt);
    }                                                                                 //GEN-LAST:event_mniAddNutzungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniKassenzeichenInformationActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniKassenzeichenInformationActionPerformed
        showOrHideView(vKassenzeichen);
    }                                                                                               //GEN-LAST:event_mniKassenzeichenInformationActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSearchRisseActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSearchRisseActionPerformed
        StaticSwingTools.showDialog(new VermessungsrissSuchDialog(this, false, ConnectionContext.createDeprecated()));
    }                                                                                  //GEN-LAST:event_cmdSearchRisseActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSearchBaulastenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSearchBaulastenActionPerformed
        StaticSwingTools.showDialog(new BaulastSuchDialog(this, false, ConnectionContext.createDeprecated()));
    }                                                                                      //GEN-LAST:event_cmdSearchBaulastenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdFortfuehrungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdFortfuehrungActionPerformed
        StaticSwingTools.showDialog(LagisFortfuehrungsanlaesseDialog.getInstance());
    }                                                                                   //GEN-LAST:event_cmdFortfuehrungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAktenzeichenSuche1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAktenzeichenSuche1ActionPerformed
        if (searchByMipaAktenzeichenDialog == null) {
            final FlurstueckeByAktenzeichenSearchPanel searchPanel = new FlurstueckeByAktenzeichenSearchPanel(
                    FlurstueckeByAktenzeichenSearchPanel.By.MIPA);
            searchByMipaAktenzeichenDialog = new JDialog(this,
                    searchPanel.getTitle(),
                    false);
            searchByMipaAktenzeichenDialog.add(searchPanel);
            searchByMipaAktenzeichenDialog.pack();
            searchByMipaAktenzeichenDialog.setIconImage(((ImageIcon)searchPanel.getIcon()).getImage());
            StaticSwingTools.showDialog(searchByMipaAktenzeichenDialog);
        } else {
            if (searchByMipaAktenzeichenDialog.isVisible()) {
                searchByMipaAktenzeichenDialog.setVisible(false);
            } else {
                StaticSwingTools.showDialog(searchByMipaAktenzeichenDialog);
            }
        }
    }                                                                                         //GEN-LAST:event_btnAktenzeichenSuche1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnVerdisCrossoverActionPerformed(final java.awt.event.ActionEvent evt) {
        try {
            final JDialog dialog = new JDialog(this, "", true);
            final VerdisCrossoverPanel vcp = new VerdisCrossoverPanel();
            dialog.add(vcp);
            dialog.pack();
            dialog.setIconImage(new javax.swing.ImageIcon(
                    getClass().getResource("/de/cismet/lagis/ressource/icons/verdis.png")).getImage());
            dialog.setTitle("Kassenzeichen in VerdIS öffnen.");
            vcp.startSearch();
            StaticSwingTools.showDialog(dialog);
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
        Thread.setDefaultUncaughtExceptionHandler(DefaultNavigatorExceptionHandler.getInstance());

        java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    try {
                        final Options options = new Options();
                        options.addOption("f", true, "ConfigFile");
                        options.addOption("p", true, "ProxyFile");
                        options.addOption("u", true, "CallserverUrl");
                        options.addOption("z", true, "CompressionEnabled");
                        options.addOption("c", true, "ConnectionClass");
                        options.addOption("d", true, "Domain");
                        final PosixParser parser = new PosixParser();
                        final CommandLine cmd = parser.parse(options, args);
                        final String cfgProxy;
                        if (cmd.hasOption("f")) {
                            final String cfgFile = cmd.getOptionValue("f");
                            final AppProperties appProperties = new AppProperties(getInputStreamFrom(cfgFile));

                            final String cfgFileName = Paths.get(new URI(cfgFile).getPath()).getFileName().toString();
                            final String cfgDirname = cfgFile.substring(0, cfgFile.lastIndexOf(cfgFileName));
                            final String proxyConfig = appProperties.getProxyConfig();
                            cfgProxy = (proxyConfig != null) ? (cfgDirname + proxyConfig) : null;

                            if (appProperties.getCallserverUrl() != null) {
                                LagisBroker.getInstance().setCallserverUrl(appProperties.getCallserverUrl());
                            } else {
                                LOG.warn("Kein Callserverhost spezifiziert.");
                                System.exit(1);
                            }
                            try {
                                LagisBroker.getInstance().setCompressionEnabled(appProperties.isCompressionEnabled());
                            } catch (final Exception ex) {
                                LOG.warn("Kein CompressionEnabled spezifiziert.");
                                System.exit(1);
                            }
                            if (appProperties.getConnectionClass() != null) {
                                LagisBroker.getInstance().setConnectionClass(appProperties.getConnectionClass());
                            } else {
                                LOG.warn("Keine ConnectionClass spezifiziert");
                                System.exit(1);
                            }
                            if (appProperties.getDomain() != null) {
                                LagisBroker.getInstance().setDomain(appProperties.getDomain());
                            } else {
                                LOG.error("Keine Domain spezifiziert, bitte mit -d setzen.");
                                System.exit(1);
                            }
                        } else {
                            cfgProxy = cmd.hasOption("p") ? cmd.getOptionValue("p") : null;

                            if (cmd.hasOption("u")) {
                                LagisBroker.getInstance().setCallserverUrl(cmd.getOptionValue("u"));
                            } else {
                                LOG.warn("Kein Callserverhost spezifiziert, bitte mit -u setzen.");
                                System.exit(1);
                            }
                            if (cmd.hasOption("z")) {
                                LagisBroker.getInstance()
                                        .setCompressionEnabled(Boolean.parseBoolean(cmd.getOptionValue("z")));
                            }
                            if (cmd.hasOption("c")) {
                                LagisBroker.getInstance().setConnectionClass(cmd.getOptionValue("c"));
                            } else {
                                LOG.warn("Keine ConnectionClass spezifiziert, bitte mit -c setzen.");
                                System.exit(1);
                            }
                            if (cmd.hasOption("d")) {
                                LagisBroker.getInstance().setDomain(cmd.getOptionValue("d"));
                            } else {
                                LOG.error("Keine Domain spezifiziert, bitte mit -d setzen.");
                                System.exit(1);
                            }
                        }
                        if ((cfgProxy != null) && !cfgProxy.isEmpty()) {
                            final ProxyProperties proxyProperties = new ProxyProperties();
                            proxyProperties.load(getInputStreamFrom(cfgProxy));
                            LagisBroker.getInstance().setProxyProperties(proxyProperties);
                        }
                    } catch (Exception ex) {
                        LOG.error("Fehler beim auslesen der Kommandozeilen Parameter", ex);
                        System.exit(1);
                    }
                    try {
                        final PlasticXPLookAndFeel lf = new PlasticXPLookAndFeel();
                        javax.swing.UIManager.setLookAndFeel(lf);
                    } catch (Exception ex) {
                        LOG.error("Fehler beim setzen des Look & Feels", ex);
                    }
                    initLog4J();
                    try {
                        SPLASH = StaticStartupTools.showGhostFrame(FILEPATH_SCREEN, "lagis [Startup]");
                    } catch (Exception e) {
                        LOG.warn("Problem beim Darstellen des Pre-Loading-Frame", e);
                    }
                    try {
                        handleLogin();
                    } catch (Exception ex) {
                        LOG.error("Fehler beim Loginframe", ex);
                        System.exit(0);
                    }
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @param   from  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static InputStream getInputStreamFrom(final String from) throws Exception {
        if ((from.indexOf("http://") == 0) || (from.indexOf("https://") == 0)
                    || (from.indexOf("file:/") == 0)) {
            return new URL(from).openStream();
        } else {
            return new BufferedInputStream(new FileInputStream(from));
        }
    }
    /* Implemented methods
     *
     *
     */
    // HistoryModelListener
    @Override
    public void backStatusChanged() {
        mniBack.setEnabled(mapComponent.isBackPossible());
    }

    @Override
    public void forwardStatusChanged() {
        mniForward.setEnabled(mapComponent.isForwardPossible());
    }

    @Override
    public void historyChanged() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("HistoryChanged");
        }
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
                    }
                    final JMenuItem currentItem = new JMenuItem(mapComponent.getCurrentElement().toString());
                    currentItem.setEnabled(false);

                    currentItem.setIcon(current);
                    menHistory.add(currentItem);
                    counter = 0;
                    for (int index = forwPos.size() - 1; index >= 0; --index) {
                        final Object elem = forwPos.get(index);
                        final JMenuItem item = new JMenuItem(elem.toString());

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
        if (prefs == null) {
            LOG.warn("there is no local configuration 'cismapPluginUIPreferences'");
        } else {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("setting windowsize of application");
                }
                final Element window = prefs.getChild("window");
                if (window == null) {
                    LOG.warn("there is no 'window' configuration in 'cismapPluginUIPreferences'");
                } else {
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
                }
            } catch (Exception t) {
                // TODO defaults
                LOG.error("Error while setting windowsize", t);
            }
        }
    }
    // TODO optimize

    @Override
    public void masterConfigure(final Element parent) {
        try {
            // ToDo if it fails all fail better place in the single try catch
            final Element urls = parent.getChild("urls");
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("OnlineHilfeUrl: " + urls.getChildText("onlineHelp"));
                }
                onlineHelpURL = urls.getChildText("onlineHelp");
            } catch (final Exception ex) {
                LOG.warn("Fehler beim lesen der OnlineHilfe URL", ex);
            }
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("News Url: " + urls.getChildText("onlineHelp"));
                }
                newsURL = urls.getChildText("news");
            } catch (final Exception ex) {
                LOG.warn("Fehler beim lesen der News Url", ex);
            }
            try {
                final Element crossoverPrefs = parent.getChild("CrossoverConfiguration");
                final LagisBroker broker = LagisBroker.getInstance();
                try {
                    final String crossoverServerPort = crossoverPrefs.getChildText("ServerPort");
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Crossover: Crossover port: " + crossoverServerPort);
                    }
                    initCrossoverServer(Integer.parseInt(crossoverServerPort));
                } catch (final Exception ex) {
                    LOG.warn("Crossover: Error while starting Server", ex);
                }
                try {
                    broker.setVerdisCrossoverPort(Integer.parseInt(crossoverPrefs.getChildText("VerdisCrossoverPort")));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Crossover: verdisCrossoverPort: " + broker.getVerdisCrossoverPort());
                    }
                } catch (final Exception ex) {
                    LOG.warn("Crossover: Error beim setzen des verdis servers", ex);
                }
                try {
                    final double kassenzeichenBuffer = Double.parseDouble(crossoverPrefs.getChildText(
                                "KassenzeichenBuffer"));
                    broker.setKassenzeichenBuffer(kassenzeichenBuffer);
                } catch (final Exception ex) {
                    LOG.error("Fehler beim setzen den buffers für die Kassenzeichenabfrage", ex);
                }
                try {
                    final double kassenzeichenBuffer100 = Double.parseDouble(crossoverPrefs.getChildText(
                                "KassenzeichenBuffer100"));
                    broker.setKassenzeichenBuffer100(kassenzeichenBuffer100);
                } catch (final Exception ex) {
                    LOG.error(
                        "Fehler beim setzen den buffers für die Kassenzeichenabfrage bei flurstücken größer 100m",
                        ex);
                }
                try {
                    final double rebeBuffer = Double.parseDouble(crossoverPrefs.getChildText(
                                "RebeBuffer"));
                    broker.setRebeBuffer(rebeBuffer);
                } catch (final Exception ex) {
                    LOG.error(
                        "Fehler beim setzen den buffers für die Abfrage der Rechte und Blastungen",
                        ex);
                }
                try {
                    final double mipaBuffer = Double.parseDouble(crossoverPrefs.getChildText(
                                "MipaBuffer"));
                    broker.setMipaBuffer(mipaBuffer);
                } catch (final Exception ex) {
                    LOG.error(
                        "Fehler beim setzen den buffers für die Abfrage der Vermietungen und Verpachtungen",
                        ex);
                }
            } catch (final Exception ex) {
                LOG.error("Crossover: Fehler beim Konfigurieren.", ex);
            }

            final Element conf = parent.getChild("fortfuehrung");
            if (conf != null) {
                final Element child = conf.getChild("linkFormat");
                fortfuehrungLinkFormat = (child != null) ? conf.getChild("linkFormat").getText() : null;
            }

            wfsFormFactory.masterConfigure(parent);
        } catch (final Exception ex) {
            LOG.error("Fehler beim konfigurieren der Lagis Applikation: ", ex);
        }
    }

    @Override
    public Element getConfiguration() {
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

    @Override
    public void dispose() {
        try {
            StaticStartupTools.saveScreenshotOfFrame(this, FILEPATH_SCREEN);
        } catch (Exception ex) {
            LOG.fatal("Fehler beim Capturen des App-Inhaltes", ex);
        }

        setVisible(false);
        LOG.info("Dispose(): Lagis wird heruntergefahren");

//        this.saveAppData(FILEPATH_DEFAULT_APP_DATA);
        configManager.writeConfiguration();
        saveLayout(FILEPATH_DEFAULT_LAYOUT);

        super.dispose();
        System.exit(0);
    }

    @Override
    public void flurstueckChanged(final FlurstueckCustomBean currentFlurstueck) {
        LOG.info("Flurstueck Changed");

        try {
            clearComponent();
            try {
                final FlurstueckSchluesselCustomBean flurstueckSchluesselBean =
                    currentFlurstueck.getFlurstueckSchluessel();
                final FlurstueckChooser.Status status = FlurstueckChooser.identifyStatus(
                        flurstueckSchluesselBean);
                if (status != null) {
                    pFlurstueckChooser.setStatus(status);
                }
            } catch (Exception ex) {
                LOG.error("Fehler beim bestimmen der Flurstücksart", ex);
            }

            if (currentFlurstueck.getFlurstueckSchluessel().isGesperrt()
                        && (currentFlurstueck.getFlurstueckSchluessel().getGueltigBis() == null)) {
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
            } else if (currentFlurstueck.getFlurstueckSchluessel().getGueltigBis() != null) {
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
        } catch (Exception ex) {
            LOG.error("Fehler im refresh thread: ", ex);
        } finally {
            LagisBroker.getInstance().flurstueckChangeFinished(LagisApp.this);
        }

        jButton1.setEnabled(currentFlurstueck != null);
        fsInfoClipboard.flurstueckChanged(currentFlurstueck);
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
        btnReloadFlurstueck.setEnabled(!isEditable);
        mniRefresh.setEnabled(!isEditable);
        if (this.fsInfoClipboard != null) {
            this.fsInfoClipboard.setActive(isEditable);
        }
    }

    @Override
    public synchronized void clearComponent() {
        // TODO ugly geknaupt
        if (LagisBroker.getInstance().isUnkownFlurstueck()) {
            btnSwitchInEditmode.setEnabled(false);
        }
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
                RESTRemoteControlStarter.initRestRemoteControlMethods(defaultServerPort);
            } else {
                RESTRemoteControlStarter.initRestRemoteControlMethods(crossoverServerPort);
            }
        } catch (Exception ex) {
            LOG.error("Crossover: Error while creating crossover server on port: " + crossoverServerPort, ex);
            if (!defaultServerPortUsed) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Crossover: Trying to create server with defaultPort: " + defaultServerPort);
                }
                defaultServerPortUsed = true;
                try {
                    RESTRemoteControlStarter.initRestRemoteControlMethods(defaultServerPort);
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
     * @param  v  DOCUMENT ME!
     */
    private void showOrHideView(final View v) {
        ///irgendwas besser als Closable ??
        // Problem wenn floating --> close -> open  (muss zweimal open)

        if (v.isClosable()) {
            if (v.isShowing()) {
                v.close();
            } else {
                v.restoreFocus();
            }
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
            de.cismet.tools.BrowserLauncher.openURL(url);
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
                if (LagisBroker.getInstance().releaseLocks()) {
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
//        configManager.writeConfiguration();
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
        return BANNER;
    }

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

            LagisBroker.getInstance().setLoggedIn(true);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Login erfolgreich");
            }
            final LagisApp app = LagisApp.getInstance();
            app.init();
            app.setVisible(true);
            app.getMapComponent().unlock();
        } else {
            // Should never gets executed
            LOG.warn("Login fehlgeschlagen");
            System.exit(0);
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
        pFlurstueckChooser.setStatus(FlurstueckChooser.Status.UNKNOWN_FLURSTUECK);
    }

    @Override
    public boolean isWidgetReadOnly() {
        return false;
    }

    @Override
    public void featureSelectionChanged(final Collection<Feature> features) {
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private MappingComponent getMapComponent() {
        return mapComponent;
    }

    @Override
    public boolean isFeatureSelectionChangedEnabled() {
        return listenerEnabled;
    }

    @Override
    public void setFeatureSelectionChangedEnabled(final boolean listenerEnabled) {
        this.listenerEnabled = listenerEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static LagisApp getInstance() {
        return LazyInitialiser.INSTANCE;
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

        public static final String CONNECTION_PROXY_CLASS =
            "Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler";

        //~ Instance fields ----------------------------------------------------

        private final Logger log = org.apache.log4j.Logger.getLogger(WundaAuthentification.class);
        private final String standaloneDomain;
        private final String callserverUrl;
        private final String connectionClass;
        private final boolean compressionEnabled;
        private final ProxyProperties proxyProperties;
        private String userString;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new WundaAuthentification object.
         *
         * @param  standaloneDomain    DOCUMENT ME!
         * @param  callserverUrl       DOCUMENT ME!
         * @param  connectionClass     DOCUMENT ME!
         * @param  compressionEnabled  DOCUMENT ME!
         * @param  proxyProperties     DOCUMENT ME!
         */
        public WundaAuthentification(final String standaloneDomain,
                final String callserverUrl,
                final String connectionClass,
                final boolean compressionEnabled,
                final ProxyProperties proxyProperties) {
            this.standaloneDomain = standaloneDomain;
            this.callserverUrl = callserverUrl;
            this.connectionClass = connectionClass;
            this.compressionEnabled = compressionEnabled;
            this.proxyProperties = proxyProperties;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean authenticate(final String name, final char[] password, final String server) throws Exception {
            if (log.isDebugEnabled()) {
                log.debug("Authentication:");
            }
            final String username = name.split("@")[0];
            final String usergroup = name.split("@")[1];
            LagisBroker.getInstance().setAccountName(name);
            final String domain = standaloneDomain;
            userString = name;
            if (log.isDebugEnabled()) {
                log.debug("full qualified username: " + userString + "@" + standaloneDomain);
            }
            try {
                final Proxy proxy = ProxyHandler.getInstance().init(proxyProperties);

                final Connection connection = ConnectionFactory.getFactory()
                            .createConnection(connectionClass, callserverUrl, proxy, compressionEnabled);

                final ConnectionInfo connectionInfo = new ConnectionInfo();
                connectionInfo.setCallserverURL(callserverUrl);
                connectionInfo.setPassword(new String(password));
                connectionInfo.setUserDomain(domain);
                connectionInfo.setUsergroup(usergroup);
                connectionInfo.setUsergroupDomain(domain);
                connectionInfo.setUsername(username);

                final ConnectionSession session = ConnectionFactory.getFactory()
                            .createSession(connection, connectionInfo, true);
                final ConnectionProxy connectionProxy = ConnectionFactory.getFactory()
                            .createProxy(CONNECTION_PROXY_CLASS, session);
                SessionManager.init(connectionProxy);

                LagisBroker.getInstance().setSession(session);

                final User user = session.getUser();

                LagisBroker.getInstance().setSession(session);
                final String userString = user.getName() + "@" + user.getUserGroup().getName();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("userstring: " + userString);
                }

                LagisBroker.getInstance().setAccountName(userString);
                if (SessionManager.getProxy().hasConfigAttr(user, "lagis.permission.readwrite")) {
                    LagisBroker.getInstance().setCoreReadOnlyMode(false);
                    LagisBroker.getInstance().setFullReadOnlyMode(false);
                    return true;
                } else if (SessionManager.getProxy().hasConfigAttr(user, "lagis.permission.read")) {
                    if (log.isDebugEnabled()) {
                        log.debug("Authentication successfull user has granted readonly access");
                    }
                    return true;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("authentication else false: no permission available");
                    }
                    return false;
                }
            } catch (final Throwable t) {
                log.error("call server url: " + callserverUrl);
                log.error("Fehler beim Anmelden ", t);
                return false;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final LagisApp INSTANCE = new LagisApp();
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class AppProperties extends PropertyResourceBundle {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new AppProperties object.
         *
         * @param   is  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        public AppProperties(final InputStream is) throws Exception {
            super(is);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getCallserverUrl() {
            return getString("callserverUrl");
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public boolean isCompressionEnabled() {
            return Boolean.parseBoolean(getString("compressionEnabled"));
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getProxyConfig() {
            return getString("proxy.config");
        }
        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getConnectionClass() {
            return getString("connectionClass");
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getDomain() {
            return getString("domain");
        }
    }
}
