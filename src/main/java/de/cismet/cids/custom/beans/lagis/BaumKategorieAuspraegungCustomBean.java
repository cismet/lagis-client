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

import de.cismet.lagisEE.entity.extension.baum.BaumKategorieAuspraegung;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BaumKategorieAuspraegungCustomBean extends CidsBean implements BaumKategorieAuspraegung {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            BaumKategorieAuspraegungCustomBean.class);
    private static final String[] PROPERTY_NAMES = new String[] { "id", "bezeichnung" };

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private String bezeichnung;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumKategorieAuspraegungCustomBean object.
     */
    public BaumKategorieAuspraegungCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BaumKategorieAuspraegungCustomBean createNew() {
        try {
            return (BaumKategorieAuspraegungCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    LagisConstants.DOMAIN_LAGIS,
                    LagisMetaclassConstants.BAUM_KATEGORIE_AUSPRAEGUNG);
        } catch (Exception ex) {
            LOG.error("error creating " + LagisMetaclassConstants.BAUM_KATEGORIE_AUSPRAEGUNG + " bean", ex);
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

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += ((getId() != null) ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BaumKategorieAuspraegungCustomBean)) {
            return false;
        }
        final BaumKategorieAuspraegungCustomBean other = (BaumKategorieAuspraegungCustomBean)object;
        if (((this.id == null) && (other.getId() != null))
                    || ((this.getId() != null) && !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getBezeichnung();
    }
}
