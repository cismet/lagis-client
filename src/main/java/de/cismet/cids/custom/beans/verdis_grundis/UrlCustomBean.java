/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.verdis_grundis;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.EJBrokerInterfaces.Url;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class UrlCustomBean extends CidsBean implements Url {

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private UrlBaseCustomBean url_base_id;
    private String object_name;
    private String[] PROPERTY_NAMES = new String[] { "id", "url_base_id", "object_name" };

    //~ Methods ----------------------------------------------------------------

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
    public UrlBaseCustomBean getUrl_base_id() {
        return this.url_base_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  paramCidsBean  DOCUMENT ME!
     */
    public void setUrl_base_id(final UrlBaseCustomBean paramCidsBean) {
        this.url_base_id = paramCidsBean;

        this.propertyChangeSupport.firePropertyChange("url_base_id", null, this.url_base_id);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getObject_name() {
        return this.object_name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  paramString  DOCUMENT ME!
     */
    public void setObject_name(final String paramString) {
        this.object_name = paramString;

        this.propertyChangeSupport.firePropertyChange("object_name", null, this.object_name);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public UrlBaseCustomBean getUrlBase() {
        return getUrl_base_id();
    }

    @Override
    public void setUrlBase(final UrlBaseCustomBean val) {
        setUrl_base_id(val);
    }

    @Override
    public String getObjektname() {
        return getObject_name();
    }

    @Override
    public void setObjektname(final String val) {
        setObject_name(val);
    }
}
