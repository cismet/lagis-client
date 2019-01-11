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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.lagis.Exception.ActionNotSuccessfulException;
import de.cismet.lagis.Exception.ErrorInNutzungProcessingException;

import de.cismet.lagis.commons.LagisConstants;
import de.cismet.lagis.commons.LagisMetaclassConstants;

import de.cismet.lagis.server.search.FlurstueckHistorieGraphSearch;
import de.cismet.lagis.server.search.FlurstueckHistorieGraphSearchResultItem;

import de.cismet.lagis.wizard.panels.HistoricNoSucessorDialog;

import de.cismet.lagisEE.interfaces.Key;

import de.cismet.lagisEE.util.FlurKey;

import de.cismet.tools.gui.StaticSwingTools;

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
            return CidsBean.getMetaClassFromTableName(domain, tablename);
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
            mos = proxy.getMetaObjectByQuery(user, query, domain);
        } catch (ConnectionException ex) {
            LOG.error("error retrieving metaobject by query", ex);
        }
        return mos;
    }
}
