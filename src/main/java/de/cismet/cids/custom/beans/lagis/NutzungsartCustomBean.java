/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.lagis;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.commons.LagisConstants;
import de.cismet.lagis.commons.LagisMetaclassConstants;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.hardwired.Nutzungsart;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class NutzungsartCustomBean extends BasicEntity implements Nutzungsart {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NutzungsartCustomBean.class);
    private static final String[] PROPERTY_NAMES = new String[] { "id", "schluessel", "bezeichnung" };

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private String schluessel;
    private String bezeichnung;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NutzungsartCustomBean object.
     */
    public NutzungsartCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static NutzungsartCustomBean createNew() {
        try {
            return (NutzungsartCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    LagisConstants.DOMAIN_LAGIS,
                    LagisMetaclassConstants.NUTZUNGSART);
        } catch (Exception ex) {
            LOG.error("error creating " + LagisMetaclassConstants.NUTZUNGSART + " bean", ex);
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
        if (value instanceof NutzungsartCustomBean) {
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
