/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.cidsmigtest;

/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/

import Sirius.navigator.connection.*;
import Sirius.navigator.connection.proxy.ConnectionProxy;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import java.io.*;

import java.util.*;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;
import de.cismet.cids.custom.beans.lagis.GemarkungCustomBean;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagisEE.interfaces.Key;

import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

/*
 * Copyright (C) 2012 cismet GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BrokerTester {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BrokerTester.class);

    public static final String CONNECTION_CLASS = "Sirius.navigator.connection.RMIConnection";
    public static final String CONNECTION_PROXY_CLASS =
        "Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler";

    public static final String CALLSERVER_URL = "rmi://localhost/callServer";
    public static final String CALLSERVER_DOMAIN = "LAGIS";
    public static final String CALLSERVER_USER = "admin";
    public static final String CALLSERVER_PASSWORD = "*";
    public static final String CALLSERVER_GROUP = "Administratoren";

    public static final String ORB_SERVER = "cubert";
    public static final String ORB_PORT = "3700";
    private static PrintStream printStream;

    //~ Instance fields --------------------------------------------------------

    private Map<Integer, String> ejbFlurstueckStrings = new HashMap<Integer, String>();
    private Map<Integer, String> mosFlurstueckStrings = new HashMap<Integer, String>();

    private final Set<Key> allFlurstueckKeys = new HashSet<Key>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BrokerTester object.
     */
    public BrokerTester() {
        // initLagisBroker();
        initEJBroker();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            Log4JQuickConfig.configure4LumbermillOnLocalhost();

            final BrokerTester tester = new BrokerTester();
            final CidsBroker broker = CidsBroker.getInstance();

            tester.createCB("flurstueck", 0);

//            final FlurstueckCustomBean bean = (FlurstueckCustomBean)tester.createCB("flurstueck", 20562);
//            bean.getVertraegeQuerverweise();

//            tester.prepareAllFlurstueckKeys(broker);
//
//            final float execTimeEjb = tester.testPerfEjb(broker);
////            tester.print("ejb in " + execTimeEjb);
//            LOG.fatal("ejb in " + execTimeEjb);
//
//            final float execTimeMos = tester.testPerfMos();
////            tester.print("mos in " + execTimeMos);
//            LOG.fatal("mos in " + execTimeMos);
//
//            tester.compareStrings();
        } catch (Exception ex) {
            LOG.fatal(ex, ex);
        } finally {
            System.exit(0);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void compareStrings() {
        final Set<Integer> set = ejbFlurstueckStrings.keySet();
        final List<Integer> list = Arrays.asList((Integer[])set.toArray(new Integer[0]));
        Collections.sort(list, new Comparator<Integer>() {

                @Override
                public int compare(final Integer o1, final Integer o2) {
                    return o1 - o2;
                }
            });

        int count = 0;
        int notEquals = 0;
        for (final Integer key : list) {
            count++;
            final String ejbString = ejbFlurstueckStrings.get(key);
            final String mosString = mosFlurstueckStrings.get(key);
            if (!ejbString.equals(mosString)) {
                LOG.fatal(count + "/" + ejbFlurstueckStrings.size() + ": not equals (" + ++notEquals + ")");
                setSoutToFile("/home/jruiz/LagisTest/" + key + "_EJB.txt");
                print(ejbString);
                setSoutToFile("/home/jruiz/LagisTest/" + key + "_MOS.txt");
                print(mosString);
            } else {
                LOG.fatal(count + "/" + ejbFlurstueckStrings.size() + ": equals");
            }
        }
    }

    /**
     * /** * DOCUMENT ME!
     *
     * @param  broker  DOCUMENT ME!
     */
    private void prepareAllFlurstueckKeys(final CidsBroker broker) {
        LOG.fatal("vorbereiten, sammeln der Flurstuecksschluessel");

        allFlurstueckKeys.clear();

        int count = 0;

        final Collection<GemarkungCustomBean> gemarkungsKeys = broker.getGemarkungsKeys();
        for (final Key gemarkungsKey : gemarkungsKeys) {
//        final Key gemarkungsKey = gemarkungsKeys.toArray(new Key[0])[0];
            LOG.fatal("gemarkungsKey: " + gemarkungsKey);
            final Collection<Key> flurKeys = broker.getDependingKeysForKey(gemarkungsKey);
            for (final Key flurKey : flurKeys) {
                LOG.fatal("flurKey: " + flurKey);
                final Collection<Key> flurstueckKeys = broker.getDependingKeysForKey(flurKey);
                for (final Key flurstueckKey : flurstueckKeys) {
                    count++;
                    LOG.fatal(allFlurstueckKeys.size() + ": " + flurstueckKey);
                    final int from = 0;
                    final int to = 10; // > 20333 für alle
                    if ((count > from) && (count <= to)) {
                        allFlurstueckKeys.add(flurstueckKey);
                    }
                    if (count >= (to - from)) {
                        return;
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   broker  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private float testPerfEjb(final CidsBroker broker) {
        LOG.fatal("performance Start EJB");

        final long startTimeEjb = System.nanoTime();

        for (final Key flurstueckKey : allFlurstueckKeys) {
            final FlurstueckCustomBean flurstueck = broker.retrieveFlurstueck((FlurstueckSchluesselCustomBean)
                    flurstueckKey);
            final String flurstueckString = EjbObjectsToStringTester.getStringOf(flurstueck);
            LOG.fatal(ejbFlurstueckStrings.size() + 1 + "/" + allFlurstueckKeys.size() + ": " + flurstueckKey);
            ejbFlurstueckStrings.put(flurstueck.getId(), flurstueckString);
//            print(flurstueckString);
        }

        final long stopTimeEjb = System.nanoTime();

        return (stopTimeEjb - startTimeEjb) / 1000000000f;
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
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     */
    private void initLagisBroker() {
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

            LOG.fatal("Session created");
        } catch (Exception ex) {
            LOG.fatal(ex, ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initEJBroker() {
        CidsBroker.getInstance();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  string  DOCUMENT ME!
     */
    private void print(final String string) {
        LOG.error(string, new Exception());
        System.out.println(string);
        printStream.println(string);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  exception  DOCUMENT ME!
     */
    private void print(final Exception exception) {
        LOG.error(exception.getMessage(), exception);
        // System.err.print(exception.getStackTrace());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  file  DOCUMENT ME!
     */
    private static void setSoutToFile(final String file) {
        try {
            printStream = new PrintStream(
                    new BufferedOutputStream(new FileOutputStream(
                            new File(file))),
                    true);
        } catch (FileNotFoundException ex) {
            LOG.fatal("error setting sout to " + file, ex);
        }
    }
}
