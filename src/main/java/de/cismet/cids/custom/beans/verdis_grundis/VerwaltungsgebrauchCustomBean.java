/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.verdis_grundis;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.broker.CidsBroker;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.hardwired.Verwaltungsgebrauch;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VerwaltungsgebrauchCustomBean extends BasicEntity implements Verwaltungsgebrauch {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            VerwaltungsgebrauchCustomBean.class);
    public static final String TABLE = "verwaltungsgebrauch";

    private static final String SEPARATOR = "/";

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private String bezeichnung;
    private String abkuerzung;
    private String unterabschnitt;
    private KategorieCustomBean fk_kategorie;
    private Collection<FarbeCustomBean> n_farben;
    private String[] PROPERTY_NAMES = new String[] {
            "id",
            "bezeichnung",
            "abkuerzung",
            "unterabschnitt",
            "fk_kategorie",
            "n_farben"
        };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VerwaltungsgebrauchCustomBean object.
     */
    public VerwaltungsgebrauchCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static VerwaltungsgebrauchCustomBean createNew() {
        try {
            return (VerwaltungsgebrauchCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    CidsBroker.LAGIS_DOMAIN,
                    TABLE);
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
    public String getAbkuerzung() {
        return this.abkuerzung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setAbkuerzung(final String val) {
        this.abkuerzung = val;

        this.propertyChangeSupport.firePropertyChange("abkuerzung", null, this.abkuerzung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getUnterabschnitt() {
        return this.unterabschnitt;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setUnterabschnitt(final String val) {
        this.unterabschnitt = val;

        this.propertyChangeSupport.firePropertyChange("unterabschnitt", null, this.unterabschnitt);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public KategorieCustomBean getFk_kategorie() {
        return this.fk_kategorie;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_kategorie(final KategorieCustomBean val) {
        this.fk_kategorie = val;

        this.propertyChangeSupport.firePropertyChange("fk_kategorie", null, this.fk_kategorie);
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
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public Collection<FarbeCustomBean> getFarben() {
        return getN_farben();
    }

    @Override
    public void setFarben(final Collection<FarbeCustomBean> val) {
        setFarben(val);
    }

    @Override
    public String getUnterAbschnitt() {
        return getUnterabschnitt();
    }

    @Override
    public void setUnterAbschnitt(final String val) {
        setUnterabschnitt(val);
    }

    @Override
    public KategorieCustomBean getKategorie() {
        return getFk_kategorie();
    }

    @Override
    public void setKategorie(final KategorieCustomBean val) {
        setFk_kategorie(val);
    }

    @Override
    public String toString() {
        if ((getKategorie() != null) && (getKategorie().getAbkuerzung() != null)
                    && (getKategorie().getOberkategorie() != null)
                    && (getKategorie().getOberkategorie().getAbkuerzung() != null)
                    && (abkuerzung != null)) {
            return getKategorie().getOberkategorie().getAbkuerzung() + SEPARATOR + getAbkuerzung() + SEPARATOR
                        + getUnterAbschnitt();
        } else {
//            log.warn("Ein Teil oder alles von der Kategorie ist null\n"+
//                    "Kategorie: "+getKategorie()+
            // "Oberkategorie: "+(getKategorie() != null ? getKategorie().getOberkategorie(): "null")+
//                    "UnterKategorie: "+getAbkuerzung()+
//                    "UnterAbschnitt: "+getUnterAbschnitt());
            return new String();
        }
    }
}
