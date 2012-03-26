/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.verdis_grundis;

import com.vividsolutions.jts.geom.Geometry;

import org.openide.util.Exceptions;

import java.sql.SQLException;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.jtsgeometryfactories.PostGisGeometryFactory;

import de.cismet.lagis.EJBrokerInterfaces.Geom;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class GeomCustomBean extends CidsBean implements Geom {

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private com.vividsolutions.jts.geom.Geometry geo_field;
    private String[] PROPERTY_NAMES = new String[] { "id", "geo_field" };

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
        setGeo_field(val);
    }
}
