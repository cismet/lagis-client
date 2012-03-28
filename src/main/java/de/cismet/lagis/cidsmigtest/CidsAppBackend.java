/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.cidsmigtest;

import Sirius.navigator.connection.*;
import Sirius.navigator.connection.proxy.ConnectionProxy;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.UserException;

import org.jdesktop.swingx.auth.LoginService;

import org.openide.util.Exceptions;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.gui.main.LagisApp;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class CidsAppBackend {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            CidsAppBackend.class);

    public static final String CLASS__FLURSTUECK_SCHLUESSEL = "flurstueck_schluessel";
    private static CidsAppBackend instance = null;

    public static final String LAGIS_DOMAIN = "VERDIS_GRUNDIS";

    //~ Instance fields --------------------------------------------------------

    private ConnectionProxy proxy = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CidsAppBackend object.
     */
    private CidsAppBackend() {
        try {
            this.proxy = SessionManager.getProxy();
            if (!SessionManager.isInitialized()) {
                SessionManager.init(proxy);
                ClassCacheMultiple.setInstance(LAGIS_DOMAIN);
            }
        } catch (Throwable e) {
            LOG.fatal("no connection to the cids server possible. too bad.", e);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public static synchronized CidsAppBackend getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Backend is not inited. Please call init(AppPreferences prefs) first.");
        }
        return instance;
    }

    /**
     * DOCUMENT ME!
     */
    public static synchronized void init() {
        instance = new CidsAppBackend();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ConnectionProxy getProxy() {
        return proxy;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ConnectionSession getSession() {
        return proxy.getSession();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   tablename  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaClass getLagisMetaClass(final String tablename) {
        try {
            return CidsBean.getMetaClassFromTableName(LAGIS_DOMAIN, tablename);
        } catch (Exception exception) {
            LOG.error("couldn't load metaclass for " + tablename, exception);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   objectId  DOCUMENT ME!
     * @param   classtId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaObject getLagisMetaObject(final int objectId, final int classtId) {
        try {
            return proxy.getMetaObject(objectId, classtId, LAGIS_DOMAIN);
        } catch (ConnectionException ex) {
            LOG.error("error in retrieving the metaobject " + objectId + " of classid " + classtId, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   query  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaObject[] getLagisMetaObject(final String query) {
        MetaObject[] mos = null;
        try {
            mos = proxy.getMetaObjectByQuery(query, 0);
        } catch (ConnectionException ex) {
            LOG.error("error retrieving metaobject by query", ex);
        }
        return mos;
    }

//    /**
//     * DOCUMENT ME!
//     */
//    public static void init() {
//        try {
//            final Connection connection = ConnectionFactory.getFactory()
//                        .createConnection("Sirius.navigator.connection.RMIConnection", "rmi://localhost/callServer");
//            final ConnectionInfo connectionInfo = new ConnectionInfo();
//            connectionInfo.setCallserverURL("rmi://localhost/callServer");
//            connectionInfo.setPassword("cismet");
//            connectionInfo.setUserDomain(LAGIS_DOMAIN);
//            connectionInfo.setUsergroup("Administratoren");
//            connectionInfo.setUsergroupDomain(LAGIS_DOMAIN);
//            connectionInfo.setUsername("admin");
//            final ConnectionSession session = ConnectionFactory.getFactory()
//                        .createSession(connection, connectionInfo, true);
//            CidsAppBackend.init(ConnectionFactory.getFactory().createProxy(
//                    "Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler",
//                    session));
//        } catch (UserException ex) {
//            Exceptions.printStackTrace(ex);
//        } catch (ConnectionException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            final Connection connection = ConnectionFactory.getFactory()
                        .createConnection("Sirius.navigator.connection.RMIConnection", "rmi://localhost/callServer");
            final ConnectionInfo connectionInfo = new ConnectionInfo();
            connectionInfo.setCallserverURL("rmi://localhost/callServer");
            connectionInfo.setPassword("cismet");
            connectionInfo.setUserDomain(LAGIS_DOMAIN);
            connectionInfo.setUsergroup("Administratoren");
            connectionInfo.setUsergroupDomain(LAGIS_DOMAIN);
            connectionInfo.setUsername("admin");
            final ConnectionSession session = ConnectionFactory.getFactory()
                        .createSession(connection, connectionInfo, true);
            SessionManager.init(ConnectionFactory.getFactory().createProxy(
                    "Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler",
                    session));
            CidsAppBackend.init();
            EJBroker.getInstance().getGemarkungsKeys();
        } catch (UserException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ConnectionException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            System.exit(0);
        }
    }

//    /**
//     * DOCUMENT ME!
//     *
//     * @version  $Revision$, $Date$
//     */
//    class CidsAuthentification extends LoginService {
//
//        //~ Static fields/initializers -----------------------------------------
//
//        public static final String CONNECTION_CLASS = "Sirius.navigator.connection.RMIConnection";
//
//        public static final String CALLSERVER_URL = "rmi://localhost/callServer";
////        public static final String CALLSERVER_USER = "admin";
////        public static final String CALLSERVER_PASSWORD = "cismet";
////        public static final String CALLSERVER_GROUP = "Administratoren";
//        public static final String CONNECTION_PROXY_CLASS =
//            "Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler";
//
//        //~ Methods ------------------------------------------------------------
//
//        @Override
//        public boolean authenticate(final String name, final char[] password, final String server) throws Exception {
//            System.setProperty("sun.rmi.transport.connectionTimeout", "15");
//            final String user = name.split("@")[0];
//            final String group = name.split("@")[1];
//
////            final String callServerURL = prefs.getAppbackendCallserverurl();
//            final String callServerURL = CALLSERVER_URL;
//            if (LOG.isDebugEnabled()) {
//                LOG.debug("callServerUrl:" + callServerURL);
//            }
////            final String domain = prefs.getAppbackendDomain();
//            final String domain = LAGIS_DOMAIN;
////            final String connectionclass = prefs.getAppbackendConnectionclass();
//            final String connectionclass = CONNECTION_CLASS;
//
//            try {
//                final Connection connection = ConnectionFactory.getFactory()
//                            .createConnection(connectionclass, callServerURL);
//                final ConnectionInfo connectionInfo = new ConnectionInfo();
//                connectionInfo.setCallserverURL(callServerURL);
//                connectionInfo.setPassword(new String(password));
//                connectionInfo.setUserDomain(domain);
//                connectionInfo.setUsergroup(group);
//                connectionInfo.setUsergroupDomain(domain);
//                connectionInfo.setUsername(user);
//                final ConnectionSession session = ConnectionFactory.getFactory()
//                            .createSession(connection, connectionInfo, true);
//                final ConnectionProxy proxy = ConnectionFactory.getFactory()
//                            .createProxy(CONNECTION_PROXY_CLASS, session);
//                CidsAppBackend.init(proxy);
//
//                final String tester = (group + "@" + domain).toLowerCase();
//                if (LOG.isDebugEnabled()) {
//                    LOG.debug("authentication: tester = :" + tester);
//                    LOG.debug("authentication: name = :" + name);
//                    LOG.debug("authentication: RM Plugin key = :" + name + "@" + domain);
//                }
////                if (prefs.getRwGroups().contains(tester)) {
////                    Main.this.readonly = false;
////                    setUserString(name);
////                    if (LOG.isDebugEnabled()) {
////                        LOG.debug("RMPlugin: wird initialisiert (VerdisStandalone)");
////                        LOG.debug("RMPlugin: Mainframe " + Main.this);
////                        LOG.debug("RMPlugin: PrimaryPort " + prefs.getPrimaryPort());
////                    }
////                    if (LOG.isDebugEnabled()) {
////                        LOG.debug("RMPlugin: SecondaryPort " + prefs.getSecondaryPort());
////                    }
////                    if (LOG.isDebugEnabled()) {
////                        LOG.debug("RMPlugin: Username " + (name + "@" + prefs.getStandaloneDomainname()));
////                    }
////                    if (LOG.isDebugEnabled()) {
////                        LOG.debug("RMPlugin: RegistryPath " + prefs.getRmRegistryServerPath());
////                    }
////
////                    if (prefs.getRmRegistryServerPath() != null) {
////                        rmPlugin = new RMPlugin(
////                                Main.this,
////                                prefs.getPrimaryPort(),
////                                prefs.getSecondaryPort(),
////                                prefs.getRmRegistryServerPath(),
////                                name
////                                + "@"
////                                + prefs.getStandaloneDomainname());
////                        if (LOG.isDebugEnabled()) {
////                            LOG.debug("RMPlugin: erfolgreich initialisiert (VerdisStandalone)");
////                        }
////                    }
////                    return true;
////                } else if (prefs.getUsergroups().contains(tester)) {
////                    readonly = true;
////                    setUserString(name);
////                    if (prefs.getRmRegistryServerPath() != null) {
////                        rmPlugin = new RMPlugin(
////                                Main.this,
////                                prefs.getPrimaryPort(),
////                                prefs.getSecondaryPort(),
////                                prefs.getRmRegistryServerPath(),
////                                name
////                                + "@"
////                                + prefs.getStandaloneDomainname());
////                    }
////                    return true;
////                } else {
////                    if (LOG.isDebugEnabled()) {
////                        LOG.debug("authentication else false");
////                    }
////                    return false;
////                }
//                return true;
//            } catch (Throwable t) {
//                LOG.error("Fehler beim Anmelden", t);
//                return false;
//            }
//        }
//    }
}
