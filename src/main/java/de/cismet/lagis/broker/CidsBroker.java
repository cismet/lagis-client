/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.broker;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.connection.proxy.ConnectionProxy;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.AbstractAttributeRepresentationFormater;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;

import java.util.Collection;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;


import de.cismet.lagis.commons.LagisConstants;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public final class CidsBroker implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static CidsBroker instance = null;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CidsBroker.class);

    //~ Instance fields --------------------------------------------------------

    private final ConnectionContext connectionContext = ConnectionContext.create(
            AbstractConnectionContext.Category.OTHER,
            getClass().getCanonicalName());

    private ConnectionProxy proxy = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of CidsBroker.
     */
    private CidsBroker() {
        try {
            setProxy(SessionManager.getProxy());
            if (!SessionManager.isInitialized()) {
                SessionManager.init(getProxy());
                ClassCacheMultiple.setInstance(LagisConstants.DOMAIN_LAGIS);
            }
        } catch (Throwable e) {
            LOG.fatal("no connection to the cids server possible. too bad.", e);
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ConnectionProxy getProxy() {
        return proxy;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  proxy  DOCUMENT ME!
     */
    private void setProxy(final ConnectionProxy proxy) {
        this.proxy = proxy;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static synchronized CidsBroker getInstance() {
        if (instance == null) {
            instance = new CidsBroker();
        }
        return instance;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   tabName    DOCUMENT ME!
     * @param   query      DOCUMENT ME!
     * @param   fields     DOCUMENT ME!
     * @param   formatter  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaObject[] getLagisLWMetaObjects(final String tabName,
            final String query,
            final String[] fields,
            AbstractAttributeRepresentationFormater formatter) {
        if (formatter == null) {
            formatter = new AbstractAttributeRepresentationFormater() {

                    @Override
                    public String getRepresentation() {
                        final StringBuffer sb = new StringBuffer();
                        for (final String attribute : fields) {
                            sb.append(getAttribute(attribute.toLowerCase())).append(" ");
                        }
                        return sb.toString().trim();
                    }
                };
        }
        try {
            final User user = SessionManager.getSession().getUser();
            final MetaClass mc = getLagisMetaClass(tabName);
            final ConnectionProxy proxy = getProxy();
            if (mc != null) {
                return proxy.getLightweightMetaObjectsByQuery(mc.getID(), user, query, fields, formatter);
            } else {
                LOG.error("Can not find MetaClass for Tablename: " + tabName);
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        return new MetaObject[0];
    }

    /**
     * DOCUMENT ME!
     *
     * @param   search  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public Collection executeSearch(final CidsServerSearch search) throws ConnectionException {
        return proxy.customServerSearch(search);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   tablename  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaClass getLagisMetaClass(final String tablename) {
        return getMetaClass(tablename, LagisBroker.getInstance().getDomain());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   tablename  DOCUMENT ME!
     * @param   domain     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaClass getMetaClass(final String tablename, final String domain) {
        try {
            return CidsBean.getMetaClassFromTableName(domain, tablename, getConnectionContext());
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
        return getMetaObject(objectId, classtId, LagisBroker.getInstance().getDomain());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   objectId  DOCUMENT ME!
     * @param   classtId  DOCUMENT ME!
     * @param   domain    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaObject getMetaObject(final int objectId, final int classtId, final String domain) {
        try {
            final ConnectionProxy proxy = getProxy();
            return proxy.getMetaObject(objectId, classtId, domain);
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
        return getMetaObject(query, LagisBroker.getInstance().getDomain());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   query   DOCUMENT ME!
     * @param   domain  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaObject[] getMetaObject(final String query, final String domain) {
        MetaObject[] mos = null;
        try {
            final User user = SessionManager.getSession().getUser();
            final ConnectionProxy proxy = getProxy();
            mos = proxy.getMetaObjectByQuery(user, query, domain, getConnectionContext());
        } catch (ConnectionException ex) {
            LOG.error("error retrieving metaobject by query", ex);
        }
        return mos;
    }
}
