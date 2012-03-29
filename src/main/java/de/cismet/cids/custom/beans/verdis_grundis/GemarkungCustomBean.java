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
import de.cismet.lagisEE.entity.core.hardwired.Gemarkung;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class GemarkungCustomBean extends BasicEntity implements Gemarkung {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GemarkungCustomBean.class);
    public static final String TABLE = "gemarkung";

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private String bezeichnung;
    private Integer schluessel;
    private String[] PROPERTY_NAMES = new String[] { "id", "bezeichnung", "schluessel" };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GemarkungCustomBean object.
     */
    public GemarkungCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static GemarkungCustomBean createNew() {
        try {
            return (GemarkungCustomBean)CidsBean.createNewCidsBeanFromTableName(CidsBroker.LAGIS_DOMAIN, TABLE);
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
    @Override
    public String getBezeichnung() {
        return this.bezeichnung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setBezeichnung(final String val) {
        this.bezeichnung = val;

        this.propertyChangeSupport.firePropertyChange("bezeichnung", null, this.bezeichnung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Integer getSchluessel() {
        return this.schluessel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setSchluessel(final Integer val) {
        this.schluessel = val;

        this.propertyChangeSupport.firePropertyChange("schluessel", null, this.schluessel);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public String toString() {
        return (((getBezeichnung() == null) && (getSchluessel() != null)) ? getSchluessel().toString()
                                                                          : getBezeichnung());
    }

    // TODO UGLY AND FALSE
    @Override
    public int compareTo(final Object value) {
        if (value instanceof GemarkungCustomBean) {
            final GemarkungCustomBean other = (GemarkungCustomBean)value;
            if ((other != null) && (other.toString() != null) && (toString() != null)) {
                return toString().compareTo(other.toString());
            } else if (toString() == null) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }
}
