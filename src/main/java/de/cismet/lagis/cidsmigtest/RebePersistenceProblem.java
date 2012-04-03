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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import org.openide.util.Exceptions;

import java.util.HashSet;
import java.util.Properties;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckSchluesselCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.RebeArtCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.RebeCustomBean;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class RebePersistenceProblem {

    //~ Static fields/initializers ---------------------------------------------

    public static final String CONNECTION_CLASS = "Sirius.navigator.connection.RMIConnection";
    public static final String CONNECTION_PROXY_CLASS =
        "Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler";

    public static final String CALLSERVER_URL = "rmi://localhost/callServer";
    public static final String CALLSERVER_DOMAIN = "VERDIS_GRUNDIS";
    public static final String CALLSERVER_USER = "admin";
    public static final String CALLSERVER_PASSWORD = "cismet";
    public static final String CALLSERVER_GROUP = "Administratoren";

    public static final String ORB_SERVER = "cubert";
    public static final String ORB_PORT = "3700";

    private static final Logger LOG = Logger.getLogger(RebePersistenceProblem.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private static void initLagisBroker() {
        try {
            final Connection connection = ConnectionFactory.getFactory()
                        .createConnection(CONNECTION_CLASS, CALLSERVER_URL);

            final ConnectionInfo connectionInfo = new ConnectionInfo();
            connectionInfo.setCallserverURL(CALLSERVER_URL);
            connectionInfo.setPassword(CALLSERVER_PASSWORD);
            connectionInfo.setUserDomain(CALLSERVER_DOMAIN);
            connectionInfo.setUsergroup(CALLSERVER_GROUP);
            connectionInfo.setUsergroupDomain(CALLSERVER_DOMAIN);
            connectionInfo.setUsername(CALLSERVER_USER);

            final ConnectionSession session = ConnectionFactory.getFactory()
                        .createSession(connection, connectionInfo, true);
            final ConnectionProxy proxy = ConnectionFactory.getFactory().createProxy(CONNECTION_PROXY_CLASS, session);

            SessionManager.init(proxy);

            LagisBroker.getInstance().setSession(session);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   tablename  DOCUMENT ME!
     * @param   id         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static CidsBean createCB(final String tablename, final int id) {
        try {
            final CidsBean cidsBean = DevelopmentTools.createCidsBeanFromRMIConnectionOnLocalhost(
                    CALLSERVER_DOMAIN,
                    CALLSERVER_GROUP,
                    CALLSERVER_USER,
                    CALLSERVER_PASSWORD,
                    tablename,
                    id);
            return cidsBean;
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            final Properties p = new Properties();
            p.put("log4j.appender.Remote", "org.apache.log4j.net.SocketAppender");
            p.put("log4j.appender.Remote.remoteHost", "localhost");
            p.put("log4j.appender.Remote.port", "4445");
            p.put("log4j.appender.Remote.locationInfo", "true");
            p.put("log4j.rootLogger", "ALL,Remote");
            org.apache.log4j.PropertyConfigurator.configure(p);

            initLagisBroker();
            LOG.info("LagisBroker has been initialized successfully");

            final RebeCustomBean rebe = RebeCustomBean.createNew();
            LOG.info("initial: rebe.getIstRecht()  = " + rebe.getIstRecht());
            LOG.info("initial: rebe.getIst_recht() = " + rebe.getIst_recht());
            LOG.info("initial: rebe.isIst_recht()  = " + rebe.isIst_recht());
            LOG.info("initial: rebe.isRecht()      = " + rebe.isRecht());

            rebe.setIstRecht(null);
            LOG.info("after rebe.setIstRecht(null): rebe.getIstRecht()  = " + rebe.getIstRecht());
            LOG.info("after rebe.setIstRecht(null): rebe.getIst_recht() = " + rebe.getIst_recht());
            LOG.info("after rebe.setIstRecht(null): rebe.isIst_recht()  = " + rebe.isIst_recht());
            LOG.info("after rebe.setIstRecht(null): rebe.isRecht()      = " + rebe.isRecht());

            rebe.setIst_recht(null);
            LOG.info("after rebe.setIst_recht(null): rebe.getIstRecht()  = " + rebe.getIstRecht());
            LOG.info("after rebe.setIst_recht(null): rebe.getIst_recht() = " + rebe.getIst_recht());
            LOG.info("after rebe.setIst_recht(null): rebe.isIst_recht()  = " + rebe.isIst_recht());
            LOG.info("after rebe.setIst_recht(null): rebe.isRecht()      = " + rebe.isRecht());

            // setting up ReBe
            final RebeArtCustomBean rebeArtBean = (RebeArtCustomBean)createCB("rebe_art", 2);
            rebe.setReBeArt(rebeArtBean);
            rebe.setNummer("Abt. II, lfd. Nr. 5");

            // setting up Flurstueck
            final FlurstueckCustomBean flurstueckBean = (FlurstueckCustomBean)createCB("flurstueck", 612);
            flurstueckBean.getRechteUndBelastungen().add(rebe);

            LOG.info("Persisting Flurstueck...");
            flurstueckBean.persist();
            LOG.info("Flurstueck has been persisted successfully");
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            System.exit(-1);
        }
    }
}
