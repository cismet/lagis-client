/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.verdis_grundis;

import java.util.Collection;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.EJBrokerInterfaces.BaumKategorie;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BaumKategorieCustomBean extends CidsBean implements BaumKategorie {

    //~ Instance fields --------------------------------------------------------

    private Long id;
    private String bezeichnung;
    private Collection<BaumKategorieAuspraegungCustomBean> ar_kategorie_auspraegungen;
    private String[] PROPERTY_NAMES = new String[] { "id", "bezeichnung", "ar_kategorie_auspraegungen" };

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Long getId() {
        return this.id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setId(final Long val) {
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
    public Collection<BaumKategorieAuspraegungCustomBean> getAr_kategorie_auspraegungen() {
        return this.ar_kategorie_auspraegungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setAr_kategorie_auspraegungen(
            final Collection<BaumKategorieAuspraegungCustomBean> val) {
        this.ar_kategorie_auspraegungen = val;

        this.propertyChangeSupport.firePropertyChange(
            "ar_kategorie_auspraegungen",
            null,
            this.ar_kategorie_auspraegungen);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public Collection<BaumKategorieAuspraegungCustomBean> getKategorieAuspraegungen() {
        return getAr_kategorie_auspraegungen();
    }

    @Override
    public void setKategorieAuspraegungen(final Collection<BaumKategorieAuspraegungCustomBean> kategorieAuspraegungen) {
        setKategorieAuspraegungen(kategorieAuspraegungen);
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
        if (!(object instanceof de.cismet.lagisEE.entity.extension.baum.BaumKategorie)) {
            return false;
        }
        final BaumKategorieCustomBean other = (BaumKategorieCustomBean)object;
        if (((this.getId() == null) && (other.getId() != null))
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
