/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.verdis_grundis;

import java.sql.Timestamp;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.Exception.BuchungNotInNutzungException;
import de.cismet.lagis.Exception.IllegalNutzungStateException;

import de.cismet.lagis.broker.CidsBroker;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.NutzungsBuchung;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class NutzungBuchungCustomBean extends BasicEntity implements NutzungsBuchung {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            NutzungBuchungCustomBean.class);
    public static final String TABLE = "nutzung_buchung";

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private String bemerkung;
    private Integer flaeche;
    private Timestamp gueltig_bis;
    private Timestamp gueltig_von;
    private Boolean ist_buchwert;
    private Double quadratmeterpreis;
    private AnlageklasseCustomBean fk_anlageklasse;
    private NutzungCustomBean fk_nutzung;
    private NutzungsartCustomBean fk_nutzungsart;
    private Collection<BebauungCustomBean> ar_bebauungen;
    private Collection<FlaechennutzungCustomBean> ar_flaechennutzungen;
    private String[] PROPERTY_NAMES = new String[] {
            "id",
            "bemerkung",
            "flaeche",
            "gueltig_bis",
            "gueltig_von",
            "ist_buchwert",
            "quadratmeterpreis",
            "fk_anlageklasse",
            "fk_nutzung",
            "fk_nutzungsart",
            "ar_bebauungen",
            "ar_flaechennutzungen"
        };

    // TODO Jean: wirklich false initiieren ?!
    private Boolean sollGeloeschtWerden = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NutzungBuchungCustomBean object.
     */
    public NutzungBuchungCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static NutzungBuchungCustomBean createNew() {
        try {
            return (NutzungBuchungCustomBean)CidsBean.createNewCidsBeanFromTableName(
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
    public String getBemerkung() {
        return this.bemerkung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setBemerkung(final String val) {
        this.bemerkung = val;

        this.propertyChangeSupport.firePropertyChange("bemerkung", null, this.bemerkung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Integer getFlaeche() {
        return this.flaeche;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setFlaeche(final Integer val) {
        this.flaeche = val;

        this.propertyChangeSupport.firePropertyChange("flaeche", null, this.flaeche);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Timestamp getGueltig_bis() {
        return this.gueltig_bis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setGueltig_bis(final Timestamp val) {
        this.gueltig_bis = val;

        this.propertyChangeSupport.firePropertyChange("gueltig_bis", null, this.gueltig_bis);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Timestamp getGueltig_von() {
        return this.gueltig_von;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setGueltig_von(final Timestamp val) {
        this.gueltig_von = val;

        this.propertyChangeSupport.firePropertyChange("gueltig_von", null, this.gueltig_von);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isIst_buchwert() {
        return this.ist_buchwert;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getIst_buchwert() {
        return this.ist_buchwert;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setIst_buchwert(final Boolean val) {
        this.ist_buchwert = val;

        this.propertyChangeSupport.firePropertyChange("ist_buchwert", null, this.ist_buchwert);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Double getQuadratmeterpreis() {
        return this.quadratmeterpreis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setQuadratmeterpreis(final Double val) {
        this.quadratmeterpreis = val;

        this.propertyChangeSupport.firePropertyChange("quadratmeterpreis", null, this.quadratmeterpreis);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public AnlageklasseCustomBean getFk_anlageklasse() {
        return this.fk_anlageklasse;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_anlageklasse(final AnlageklasseCustomBean val) {
        this.fk_anlageklasse = val;

        this.propertyChangeSupport.firePropertyChange("fk_anlageklasse", null, this.fk_anlageklasse);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public NutzungCustomBean getFk_nutzung() {
        return this.fk_nutzung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_nutzung(final NutzungCustomBean val) {
        this.fk_nutzung = val;

        this.propertyChangeSupport.firePropertyChange("fk_nutzung", null, this.fk_nutzung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public NutzungsartCustomBean getFk_nutzungsart() {
        return this.fk_nutzungsart;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_nutzungsart(final NutzungsartCustomBean val) {
        this.fk_nutzungsart = val;

        this.propertyChangeSupport.firePropertyChange("fk_nutzungsart", null, this.fk_nutzungsart);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BebauungCustomBean> getAr_bebauungen() {
        return this.ar_bebauungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setAr_bebauungen(final Collection<BebauungCustomBean> val) {
        this.ar_bebauungen = val;

        this.propertyChangeSupport.firePropertyChange("ar_bebauungen", null, this.ar_bebauungen);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlaechennutzungCustomBean> getAr_flaechennutzungen() {
        return this.ar_flaechennutzungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setAr_flaechennutzungen(final Collection<FlaechennutzungCustomBean> val) {
        this.ar_flaechennutzungen = val;

        this.propertyChangeSupport.firePropertyChange("ar_flaechennutzungen", null, this.ar_flaechennutzungen);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public AnlageklasseCustomBean getAnlageklasse() {
        return getFk_anlageklasse();
    }

    @Override
    public void setAnlageklasse(final AnlageklasseCustomBean val) {
        setFk_anlageklasse(val);
    }

    @Override
    public NutzungsartCustomBean getNutzungsart() {
        return getFk_nutzungsart();
    }

    @Override
    public void setNutzungsart(final NutzungsartCustomBean val) {
        setFk_nutzungsart(val);
    }

    @Override
    public Date getGueltigbis() {
        return getGueltig_bis();
    }

    @Override
    public void setGueltigbis(final Date val) {
        if (val == null) {
            setGueltig_bis(null);
        } else {
            setGueltig_bis(new Timestamp(val.getTime()));
        }
    }

    @Override
    public Date getGueltigvon() {
        return getGueltig_von();
    }

    @Override
    public void setGueltigvon(final Date val) {
        if (val == null) {
            setGueltig_von(null);
        } else {
            setGueltig_von(new Timestamp(val.getTime()));
        }
    }

    @Override
    public Collection<FlaechennutzungCustomBean> getFlaechennutzung() {
        return getAr_flaechennutzungen();
    }

    @Override
    public void setFlaechennutzung(final Collection<FlaechennutzungCustomBean> val) {
        setAr_flaechennutzungen(val);
    }

    @Override
    public Collection<BebauungCustomBean> getBebauung() {
        return getAr_bebauungen();
    }

    @Override
    public void setBebauung(final Collection<BebauungCustomBean> val) {
        setAr_bebauungen(val);
    }

    @Override
    public Double getGesamtpreis() {
        if ((getQuadratmeterpreis() != null) && (getFlaeche() != null)) {
            return getQuadratmeterpreis() * getFlaeche();
        } else {
            return null;
        }
    }

    @Override
    public void flipBuchungsBuchwert() throws IllegalNutzungStateException, BuchungNotInNutzungException {
        if (getNutzung() != null) {
            getNutzung().flipBuchungsBuchwertValue(this);
        } else {
            throw new IllegalNutzungStateException("Buchung gehört zu keiner Nutzung.");
        }
    }

    @Override
    public boolean isBuchwertFlippable() {
        if (getNutzung() != null) {
            return getNutzung().isBuchungFlippable(this);
        } else {
            return false;
        }
    }

    @Override
    public String getPrettyString() {
        final StringBuffer result = new StringBuffer();
        result.append("Anlageklasse: ");
        if (getAnlageklasse() != null) {
            final StringBuffer append = result.append(getAnlageklasse()).append("\n");
        } else {
            result.append("Keine Anlageklasse vorhanden\n");
        }
        result.append("Nutzungsart: ");
        if (getNutzungsart() != null) {
            result.append(getNutzungsart().getBezeichnung())
                    .append("-")
                    .append(getNutzungsart().getSchluessel())
                    .append("\n");
        } else {
            result.append("Keine Nutzungsart vorhanden\n");
        }
        result.append("Nutzungsart: ");
        if (getFlaeche() != null) {
            result.append(+getFlaeche()).append("m²\n");
        } else {
            result.append("Keine Fläche vorhanden\n");
        }
        result.append("Quadratmeterpreis: ");
        if (getQuadratmeterpreis() != null) {
            result.append(+getQuadratmeterpreis()).append("€\n");
        } else {
            result.append("Keine Quadratmeterpreis vorhanden\n");
        }
        result.append("Gesamtpreis: ");
        if (getGesamtpreis() != null) {
            result.append(+getGesamtpreis()).append("€\n");
        } else {
            result.append("Keine Gesamtpreis vorhanden\n");
        }
        return result.toString();
    }

    @Override
    public NutzungBuchungCustomBean cloneBuchung() {
        final NutzungBuchungCustomBean newBuchung = new NutzungBuchungCustomBean();
        newBuchung.setAnlageklasse(getAnlageklasse());
        newBuchung.setNutzungsart(getNutzungsart());
        if (getFlaeche() != null) {
            newBuchung.setFlaeche(new Integer(getFlaeche()));
        }
        if (getBebauung() != null) {
            newBuchung.setBebauung(new HashSet<BebauungCustomBean>(getBebauung()));
        }
        if (getFlaechennutzung() != null) {
            newBuchung.setFlaechennutzung(new HashSet<FlaechennutzungCustomBean>(getFlaechennutzung()));
        }
        if (getBemerkung() != null) {
            newBuchung.setBemerkung(getBemerkung());
        }
        if (getQuadratmeterpreis() != null) {
            newBuchung.setQuadratmeterpreis(new Double(getQuadratmeterpreis()));
        }
        return newBuchung;
    }

    @Override
    public Boolean getIstBuchwert() {
        return getIst_buchwert();
    }

    @Override
    public void setIstBuchwert(final Boolean val) {
        setIst_buchwert(val);
    }

    @Override
    public Boolean getSollGeloeschtWerden() {
        return sollGeloeschtWerden;
    }

    @Override
    public void setSollGeloeschtWerden(final Boolean val) {
        this.sollGeloeschtWerden = val;
    }

    @Override
    public NutzungCustomBean getNutzung() {
        return getFk_nutzung();
    }

    @Override
    public void setNutzung(final NutzungCustomBean val) {
        setFk_nutzung(val);
    }

    @Override
    public String toString() {
        return "de.cismet.lagisEE.entity.core.NutzunBuchungg[id=" + getId() + "]";
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        final NutzungBuchungCustomBean clone = cloneBuchung();
        clone.setIstBuchwert(getIstBuchwert());
        clone.setGueltigbis(getGueltigbis());
        clone.setGueltigvon(getGueltigvon());
        clone.setId(null);
        clone.setSollGeloeschtWerden(getSollGeloeschtWerden());
        clone.setNutzung(null);
        return clone;
    }
}
