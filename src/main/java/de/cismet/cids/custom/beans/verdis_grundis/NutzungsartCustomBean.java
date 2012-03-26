/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.verdis_grundis;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.EJBrokerInterfaces.Nutzungsart;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class NutzungsartCustomBean extends CidsBean implements Nutzungsart {

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private String schluessel;
    private String bezeichnung;
    private String[] PROPERTY_NAMES = new String[] { "id", "schluessel", "bezeichnung" };

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
    @Override
    public String getSchluessel() {
        return this.schluessel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setSchluessel(final String val) {
        this.schluessel = val;

        this.propertyChangeSupport.firePropertyChange("schluessel", null, this.schluessel);
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

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public String getPrettyString() {
        return getBezeichnung() + "-" + getSchluessel();
    }

    @Override
    public String toString() {
        return getSchluessel();
    }

    @Override
    public int compareTo(final Object value) {
        if (value instanceof de.cismet.lagisEE.entity.core.hardwired.Nutzungsart) {
            final NutzungsartCustomBean other = (NutzungsartCustomBean)value;
            if ((other != null) && (other.getSchluessel() != null) && (getSchluessel() != null)) {
                return getSchluessel().compareTo(other.getSchluessel());
            } else if (toString() == null) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return 1;
        }
    }
}
