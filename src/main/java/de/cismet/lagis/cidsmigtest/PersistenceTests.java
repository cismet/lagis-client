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

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;

import java.util.List;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.beans.lagis.*;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagisEE.entity.core.Vertrag;

import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class PersistenceTests {

    //~ Static fields/initializers ---------------------------------------------

    public static final String CONNECTION_CLASS = RESTfulConnection.class.getCanonicalName(); // "Sirius.navigator.connection.RMIConnection";
    public static final String CONNECTION_PROXY_CLASS =
        "Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler";
    public static final String CALLSERVER_URL = "http://localhost:9917/callserver/binary";
    public static final String CALLSERVER_DOMAIN = "LAGIS";
    public static final String CALLSERVER_USER = "admin";
    public static final String CALLSERVER_PASSWORD = "cismet";
    public static final String CALLSERVER_GROUP = "Administratoren";

    private static final Logger LOG = Logger.getLogger(PersistenceTests.class);

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
            Log4JQuickConfig.configure4LumbermillOnLocalhost();
            initLagisBroker();

            final PersistenceTests test = new PersistenceTests();
            test.vertragTest();
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            System.exit(-1);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private void rebeTest() throws Exception {
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
        rebe.setBemerkung("test");
        rebe.setNummer("Abt. II, lfd. Nr. 5");

        // setting up Flurstueck
        final FlurstueckCustomBean flurstueckBean = (FlurstueckCustomBean)createCB("flurstueck", 612);
        flurstueckBean.getRechteUndBelastungen().add(rebe);

        LOG.info("Persisting Flurstueck...");
        flurstueckBean.persist();
        LOG.info("Flurstueck has been persisted successfully");
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private void vertragTest() throws Exception {
        VertragCustomBean vertrag = VertragCustomBean.createNew();
        vertrag.setBemerkung("Jean vertragTest");

        // vor dem persisten des neuen masters schon was einfügen
        final BeschlussCustomBean beschluss1 = BeschlussCustomBean.createNew();
        final BeschlussCustomBean beschluss2 = BeschlussCustomBean.createNew();
        vertrag.getBeschluesse().add(beschluss1);
        vertrag.getBeschluesse().add(beschluss2);

        // persisten
        vertrag = (VertragCustomBean)vertrag.persist();

        // nach dem persisten nochmal was einfügen
        final BeschlussCustomBean beschluss3 = BeschlussCustomBean.createNew();
        final BeschlussCustomBean beschluss4 = BeschlussCustomBean.createNew();
        vertrag.getBeschluesse().add(beschluss3);
        vertrag.getBeschluesse().add(beschluss4);

        // persisten
        vertrag = (VertragCustomBean)vertrag.persist();

        // aus der liste objekte rauslöschen
        final List<BeschlussCustomBean> list = (List<BeschlussCustomBean>)vertrag.getBeschluesse();
        list.remove(0);
        list.remove(list.size() - 1);

        // persisten
        vertrag = (VertragCustomBean)vertrag.persist();

        // vertrag löschen
        vertrag.delete();

        vertrag.persist();
    }
}
