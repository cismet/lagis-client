/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.lagis;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.broker.CidsBroker;

import de.cismet.lagisEE.entity.extension.baum.BaumNutzung;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BaumNutzungCustomBean extends CidsBean implements BaumNutzung {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BaumNutzungCustomBean.class);
    public static final String TABLE = "baum_nutzung";

    //~ Instance fields --------------------------------------------------------

    private Long id;
    private BaumKategorieAuspraegungCustomBean fk_ausgewaehlte_auspraegung;
    private BaumKategorieCustomBean fk_baum_kategorie;
    private String[] PROPERTY_NAMES = new String[] { "id", "fk_ausgewaehlte_auspraegung", "fk_baum_kategorie" };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumNutzungCustomBean object.
     */
    public BaumNutzungCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BaumNutzungCustomBean createNew() {
        try {
            return (BaumNutzungCustomBean)CidsBean.createNewCidsBeanFromTableName(CidsBroker.LAGIS_DOMAIN, TABLE);
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
    public BaumKategorieAuspraegungCustomBean getFk_ausgewaehlte_auspraegung() {
        return this.fk_ausgewaehlte_auspraegung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_ausgewaehlte_auspraegung(final BaumKategorieAuspraegungCustomBean val) {
        this.fk_ausgewaehlte_auspraegung = val;

        this.propertyChangeSupport.firePropertyChange(
            "fk_ausgewaehlte_auspraegung",
            null,
            this.fk_ausgewaehlte_auspraegung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BaumKategorieCustomBean getFk_baum_kategorie() {
        return this.fk_baum_kategorie;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_baum_kategorie(final BaumKategorieCustomBean val) {
        this.fk_baum_kategorie = val;

        this.propertyChangeSupport.firePropertyChange("fk_baum_kategorie", null, this.fk_baum_kategorie);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public BaumKategorieAuspraegungCustomBean getAusgewaehlteAuspraegung() {
        return getFk_ausgewaehlte_auspraegung();
    }

    @Override
    public void setAusgewaehlteAuspraegung(final BaumKategorieAuspraegungCustomBean val) {
        setFk_ausgewaehlte_auspraegung(val);
    }

    @Override
    public BaumKategorieCustomBean getBaumKategorie() {
        return getFk_baum_kategorie();
    }

    @Override
    public void setBaumKategorie(final BaumKategorieCustomBean val) {
        setFk_baum_kategorie(val);
    }

    @Override
    public String toString() {
        return "de.cismet.lagisEE.entity.extension.baum.BaumNutzung[id=" + getId() + "]";
    }
}
