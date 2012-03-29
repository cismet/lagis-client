/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.verdis_grundis;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.broker.CidsBroker;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.UrlBase;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class UrlBaseCustomBean extends BasicEntity implements UrlBase {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UrlBaseCustomBean.class);
    public static final String TABLE = "url_base";

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private String prot_prefix;
    private String server;
    private String path;
    private String[] PROPERTY_NAMES = new String[] { "id", "prot_prefix", "server", "path" };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new UrlBaseCustomBean object.
     */
    public UrlBaseCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static UrlBaseCustomBean createNew() {
        try {
            return (UrlBaseCustomBean)CidsBean.createNewCidsBeanFromTableName(CidsBroker.LAGIS_DOMAIN, TABLE);
        } catch (Exception ex) {
            LOG.error("error creating " + TABLE + " bean", ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Integer getId() {
        return this.id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  paramInteger  DOCUMENT ME!
     */
    @Override
    public void setId(final Integer paramInteger) {
        this.id = paramInteger;

        this.propertyChangeSupport.firePropertyChange("id", null, this.id);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getProt_prefix() {
        return this.prot_prefix;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  paramString  DOCUMENT ME!
     */
    public void setProt_prefix(final String paramString) {
        this.prot_prefix = paramString;

        this.propertyChangeSupport.firePropertyChange("prot_prefix", null, this.prot_prefix);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getServer() {
        return this.server;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  paramString  DOCUMENT ME!
     */
    @Override
    public void setServer(final String paramString) {
        this.server = paramString;

        this.propertyChangeSupport.firePropertyChange("server", null, this.server);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getPath() {
        return this.path;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  paramString  DOCUMENT ME!
     */
    public void setPath(final String paramString) {
        this.path = paramString;

        this.propertyChangeSupport.firePropertyChange("path", null, this.path);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public String getProtPrefix() {
        return getProt_prefix();
    }

    @Override
    public void setProtPrefix(final String val) {
        setProt_prefix(val);
    }

    @Override
    public String getPfad() {
        return getPath();
    }

    @Override
    public void setPfad(final String val) {
        setPath(val);
    }
}
