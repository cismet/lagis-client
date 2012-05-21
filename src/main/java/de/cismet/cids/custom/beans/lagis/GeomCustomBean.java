/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.lagis;

import com.vividsolutions.jts.geom.Geometry;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.broker.CidsBroker;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.Geom;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class GeomCustomBean extends BasicEntity implements Geom {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeomCustomBean.class);
    public static final String TABLE = "geom";

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private com.vividsolutions.jts.geom.Geometry geo_field;
    private String[] PROPERTY_NAMES = new String[] { "id", "geo_field" };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeomCustomBean object.
     */
    public GeomCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static GeomCustomBean createNew() {
        try {
            return (GeomCustomBean)CidsBean.createNewCidsBeanFromTableName(CidsBroker.LAGIS_DOMAIN, TABLE);
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
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setId(final Integer val) {
        this.id = val;

        this.propertyChangeSupport.firePropertyChange("id", null, this.id);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Geometry getGeo_field() {
        return this.geo_field;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setGeo_field(final Geometry val) {
        this.geo_field = val;

        this.propertyChangeSupport.firePropertyChange("geo_field", null, this.geo_field);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public Geometry getGeomField() {
        return getGeo_field();
    }

    @Override
    public void setGeomField(final Geometry val) {
        if (val != null) {
            val.setSRID(25832);
        }

        setGeo_field(val);
    }
}
