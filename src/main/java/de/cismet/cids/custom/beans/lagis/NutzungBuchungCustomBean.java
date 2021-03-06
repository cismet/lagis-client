/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.lagis;

import java.sql.Timestamp;

import java.util.Date;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.Exception.BuchungNotInNutzungException;
import de.cismet.lagis.Exception.IllegalNutzungStateException;

import de.cismet.lagis.commons.LagisConstants;
import de.cismet.lagis.commons.LagisMetaclassConstants;

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
    private static final String[] PROPERTY_NAMES = new String[] {
            "id",
            "bemerkung",
            "flaeche",
            "gueltig_bis",
            "gueltig_von",
            "ist_buchwert",
            "quadratmeterpreis",
            "fk_anlageklasse",
            "fk_nutzung",
            "fk_nutzungsart"
        };

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

    private boolean sollGeloeschtWerden = false;

    private NutzungCustomBean nutzung;

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
                    LagisConstants.DOMAIN_LAGIS,
                    LagisMetaclassConstants.NUTZUNG_BUCHUNG);
        } catch (Exception ex) {
            LOG.error("error creating " + LagisMetaclassConstants.NUTZUNG_BUCHUNG + " bean", ex);
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

    @Override
    public String[] getPropertyNames() {
        return PROPERTY_NAMES;
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
        final NutzungBuchungCustomBean newBuchung = NutzungBuchungCustomBean.createNew();
        newBuchung.setAnlageklasse(getAnlageklasse());
        newBuchung.setNutzungsart(getNutzungsart());
        if (getFlaeche() != null) {
            newBuchung.setFlaeche(getFlaeche());
        }
        if (getBemerkung() != null) {
            newBuchung.setBemerkung(getBemerkung());
        }
        if (getQuadratmeterpreis() != null) {
            newBuchung.setQuadratmeterpreis(getQuadratmeterpreis());
        }
        return newBuchung;
    }

    @Override
    public boolean getIstBuchwert() {
        final Boolean bool = getIst_buchwert();
        return (bool == null) ? false : bool;
    }

    @Override
    public void setIstBuchwert(final boolean val) {
        setIst_buchwert(val);
    }

    @Override
    public boolean getSollGeloeschtWerden() {
        return sollGeloeschtWerden;
    }

    @Override
    public void setSollGeloeschtWerden(final boolean val) {
        this.sollGeloeschtWerden = val;
    }

    @Override
    public NutzungCustomBean getNutzung() {
        // NOTE: Nutzung is not intended to be persisted -> causes infinite recursive persistence calls on server side
        // (problem with 1-n relations)
        return this.nutzung;
    }

    @Override
    public void setNutzung(final NutzungCustomBean val) {
        // NOTE: Nutzung is not intended to be persisted -> causes infinite recursive persistence calls on server side
        // (problem with 1-n relations)
        this.nutzung = val;
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
