/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.lagis;

import java.util.Collection;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.broker.CidsBroker;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.extension.vermietung.MiPaKategorie;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class MipaKategorieCustomBean extends BasicEntity implements MiPaKategorie {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MipaCustomBean.class);
    public static final String TABLE = "mipa_kategorie";

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private String bezeichnung;
    private Boolean hat_nummer_als_auspraegung;
    private Collection<MipaKategorieAuspraegungCustomBean> ar_kategorie_auspraegungen;
    private String[] PROPERTY_NAMES = new String[] {
            "id",
            "bezeichnung",
            "hat_nummer_als_auspraegung",
            "ar_kategorie_auspraegungen"
        };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MipaKategorieCustomBean object.
     */
    public MipaKategorieCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static MipaKategorieCustomBean createNew() {
        try {
            return (MipaKategorieCustomBean)CidsBean.createNewCidsBeanFromTableName(CidsBroker.LAGIS_DOMAIN, TABLE);
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
    public Boolean isHat_nummer_als_auspraegung() {
        return this.hat_nummer_als_auspraegung;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getHat_nummer_als_auspraegung() {
        return this.hat_nummer_als_auspraegung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setHat_nummer_als_auspraegung(final Boolean val) {
        this.hat_nummer_als_auspraegung = val;

        this.propertyChangeSupport.firePropertyChange(
            "hat_nummer_als_auspraegung",
            null,
            this.hat_nummer_als_auspraegung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<MipaKategorieAuspraegungCustomBean> getAr_kategorie_auspraegungen() {
        return this.ar_kategorie_auspraegungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setAr_kategorie_auspraegungen(
            final Collection<MipaKategorieAuspraegungCustomBean> val) {
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
    public boolean getHatNummerAlsAuspraegung() {
        final Boolean bool = getHat_nummer_als_auspraegung();
        return (bool == null) ? false : bool;
    }

    @Override
    public void setHatNummerAlsAuspraegung(final boolean val) {
        setHat_nummer_als_auspraegung(val);
    }

    @Override
    public Collection<MipaKategorieAuspraegungCustomBean> getKategorieAuspraegungen() {
        return getAr_kategorie_auspraegungen();
    }

    @Override
    public void setKategorieAuspraegungen(final Collection<MipaKategorieAuspraegungCustomBean> val) {
        setAr_kategorie_auspraegungen(val);
    }

    @Override
    public String toString() {
        return getBezeichnung();
    }
}
