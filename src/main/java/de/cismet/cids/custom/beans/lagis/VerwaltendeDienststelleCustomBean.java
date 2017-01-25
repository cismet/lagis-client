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

import de.cismet.lagis.commons.LagisConstants;
import de.cismet.lagis.commons.LagisMetaclassConstants;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.hardwired.VerwaltendeDienststelle;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VerwaltendeDienststelleCustomBean extends BasicEntity implements VerwaltendeDienststelle {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            VerwaltendeDienststelleCustomBean.class);

    private static final String SEPARATOR = ".";
    private static final String[] PROPERTY_NAMES = new String[] {
            "id",
            "abkuerzung_abteilung",
            "bezeichnung_abteilung",
            "email_adresse",
            "fk_ressort",
            "n_farben"
        };

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private String abkuerzung_abteilung;
    private String bezeichnung_abteilung;
    private String email_adresse;
    private RessortCustomBean fk_ressort;
    private Collection<FarbeCustomBean> n_farben;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VerwaltendeDienststelleCustomBean object.
     */
    public VerwaltendeDienststelleCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static VerwaltendeDienststelleCustomBean createNew() {
        try {
            return (VerwaltendeDienststelleCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    LagisConstants.DOMAIN_LAGIS,
                    LagisMetaclassConstants.VERWALTENDE_DIENSTSTELLE);
        } catch (Exception ex) {
            LOG.error("error creating " + LagisMetaclassConstants.VERWALTENDE_DIENSTSTELLE + " bean", ex);
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
    public String getAbkuerzung_abteilung() {
        return this.abkuerzung_abteilung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setAbkuerzung_abteilung(final String val) {
        this.abkuerzung_abteilung = val;

        this.propertyChangeSupport.firePropertyChange("abkuerzung_abteilung", null, this.abkuerzung_abteilung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getBezeichnung_abteilung() {
        return this.bezeichnung_abteilung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setBezeichnung_abteilung(final String val) {
        this.bezeichnung_abteilung = val;

        this.propertyChangeSupport.firePropertyChange("bezeichnung_abteilung", null, this.bezeichnung_abteilung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getEmail_adresse() {
        return this.email_adresse;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setEmail_adresse(final String val) {
        this.email_adresse = val;

        this.propertyChangeSupport.firePropertyChange("email_adresse", null, this.email_adresse);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public RessortCustomBean getFk_ressort() {
        return this.fk_ressort;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_ressort(final RessortCustomBean val) {
        this.fk_ressort = val;

        this.propertyChangeSupport.firePropertyChange("fk_ressort", null, this.fk_ressort);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public RessortCustomBean getRessort() {
        return getFk_ressort();
    }

    @Override
    public void setRessort(final RessortCustomBean val) {
        setFk_ressort(val);
    }

    @Override
    public String getAbkuerzungAbteilung() {
        return getAbkuerzung_abteilung();
    }

    @Override
    public void setAbkuerzungAbteilung(final String val) {
        setAbkuerzung_abteilung(val);
    }

    @Override
    public String getBezeichnungAbteilung() {
        return getBezeichnung_abteilung();
    }

    @Override
    public void setBezeichnungAbteilung(final String val) {
        setBezeichnung_abteilung(val);
    }

    @Override
    public String getEmailAdresse() {
        return getEmail_adresse();
    }

    @Override
    public void setEmailAdresse(final String val) {
        setEmail_adresse(val);
    }

    @Override
    public String toString() {
        if ((getRessort() != null) && (getBezeichnungAbteilung() != null) && (getAbkuerzungAbteilung() != null)) {
            return getRessort().getAbkuerzung() + SEPARATOR + getAbkuerzungAbteilung();
        } else if (getRessort() != null) {
            // log.debug("Keine Abkürzung für das gewählte Ressort vorhanden");
            return getRessort().getAbkuerzung();
        }
//        log.warn("Ein Teol oder alles von dem Ressortnamen ist null\n"+
//                "Ressort: "+getRessort()+
//                "Abteilung:"+getBezeichnungAbteilung() +" "+ getAbkuerzungAbteilung());
        return new String();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FarbeCustomBean> getN_farben() {
        return this.n_farben;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setN_farben(final Collection<FarbeCustomBean> val) {
//        Collections.sort((List<FarbeCustomBean>)val, new Comparator<FarbeCustomBean>() {
//
//                @Override
//                public int compare(final FarbeCustomBean o1, final FarbeCustomBean o2) {
//                    return (int)(o1.getId() - o2.getId());
//                }
//            });
        this.n_farben = val;

        this.propertyChangeSupport.firePropertyChange("n_farben", null, this.n_farben);
    }

    @Override
    public Collection<FarbeCustomBean> getFarben() {
        return getN_farben();
    }

    @Override
    public void setFarben(final Collection<FarbeCustomBean> val) {
        setFarben(val);
    }
}
