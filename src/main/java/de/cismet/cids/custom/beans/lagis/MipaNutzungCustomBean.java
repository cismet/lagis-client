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
import de.cismet.lagisEE.entity.extension.vermietung.MiPaNutzung;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class MipaNutzungCustomBean extends BasicEntity implements MiPaNutzung {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MipaNutzungCustomBean.class);
    private static final String[] PROPERTY_NAMES = new String[] {
            "id",
            "ausgewaehlte_nummer",
            "fk_mipa_kategorie"
        };

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private Integer ausgewaehlte_nummer;
    private MipaKategorieCustomBean fk_mipa_kategorie;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MipaNutzungCustomBean object.
     */
    public MipaNutzungCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static MipaNutzungCustomBean createNew() {
        try {
            return (MipaNutzungCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    LagisConstants.DOMAIN_LAGIS,
                    LagisMetaclassConstants.MIPA_NUTZUNG);
        } catch (Exception ex) {
            LOG.error("error creating " + LagisMetaclassConstants.MIPA_NUTZUNG + " bean", ex);
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
    public Integer getAusgewaehlte_nummer() {
        return this.ausgewaehlte_nummer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setAusgewaehlte_nummer(final Integer val) {
        this.ausgewaehlte_nummer = val;

        this.propertyChangeSupport.firePropertyChange("ausgewaehlte_nummer", null, this.ausgewaehlte_nummer);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MipaKategorieCustomBean getFk_mipa_kategorie() {
        return this.fk_mipa_kategorie;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_mipa_kategorie(final MipaKategorieCustomBean val) {
        this.fk_mipa_kategorie = val;

        this.propertyChangeSupport.firePropertyChange("fk_mipa_kategorie", null, this.fk_mipa_kategorie);
    }

    @Override
    public String[] getPropertyNames() {
        return PROPERTY_NAMES;
    }

    @Override
    public MipaKategorieCustomBean getMiPaKategorie() {
        return getFk_mipa_kategorie();
    }

    @Override
    public void setMiPaKategorie(final MipaKategorieCustomBean val) {
        setFk_mipa_kategorie(val);
    }

    @Override
    public Integer getAusgewaehlteNummer() {
        return getAusgewaehlte_nummer();
    }

    @Override
    public void setAusgewaehlteNummer(final Integer val) {
        setAusgewaehlte_nummer(val);
    }

    @Override
    public String toString() {
        return "de.cismet.lagisEE.entity.extension.vermietung.Nutzung[id=" + getId() + "]";
    }
}
