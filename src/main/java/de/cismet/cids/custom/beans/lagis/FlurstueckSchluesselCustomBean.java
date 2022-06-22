/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.lagis;

import Sirius.server.middleware.types.MetaObject;

import java.sql.Timestamp;

import java.util.Date;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.broker.CidsBroker;

import de.cismet.lagis.commons.LagisConstants;
import de.cismet.lagis.commons.LagisMetaclassConstants;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FlurstueckSchluesselCustomBean extends BasicEntity implements FlurstueckSchluessel {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            FlurstueckSchluesselCustomBean.class);


    private static final String BASE_QUERY = String.format(" SELECT (SELECT id FROM cs_class WHERE table_name ILIKE '%1$s'), id FROM %1$s", LagisMetaclassConstants.FLURSTUECK_SCHLUESSEL);
    
    private static final String[] PROPERTY_NAMES = new String[] {
            "id",
            "flur",
            "flurstueck_zaehler",
            "flurstueck_nenner",
            "ist_gesperrt",
            "bemerkung_sperre",
            "datum_entstehung",
            "gueltig_bis",
            "fk_gemarkung",
            "datum_letzter_stadtbesitz",
            "war_staedtisch",
            "fk_flurstueck_art",
            "letzter_bearbeiter",
            "letzte_bearbeitung"
        };

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private Integer flur;
    private Integer flurstueck_zaehler;
    private Integer flurstueck_nenner;
    private Boolean ist_gesperrt;
    private String bemerkung_sperre;
    private Timestamp datum_entstehung;
    private Timestamp gueltig_bis;
    private GemarkungCustomBean fk_gemarkung;
    private Timestamp datum_letzter_stadtbesitz;
    private Boolean war_staedtisch;
    private FlurstueckArtCustomBean fk_flurstueck_art;
    private String letzter_bearbeiter;
    private Timestamp letzte_bearbeitung;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FlurstueckSchluesselCustomBean object.
     */
    public FlurstueckSchluesselCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static FlurstueckSchluesselCustomBean createNew() {
        try {
            final FlurstueckSchluesselCustomBean newBean = (FlurstueckSchluesselCustomBean)CidsBean
                        .createNewCidsBeanFromTableName(
                            LagisConstants.DOMAIN_LAGIS,
                            LagisMetaclassConstants.FLURSTUECK_SCHLUESSEL);
            newBean.setIstGesperrt(false);
            return newBean;
        } catch (Exception ex) {
            LOG.error("error creating " + LagisMetaclassConstants.FLURSTUECK_SCHLUESSEL + " bean", ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   query  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static FlurstueckSchluesselCustomBean retrieve(final String query) {
        final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);

        if ((mos != null) && (mos.length > 0)) {
            if (mos.length > 1) {
                LOG.warn("Multiple FlurstueckSchluessel -> should only be one but was " + mos.length + ". query: "
                            + query);
                return null;
            } else {
                return (FlurstueckSchluesselCustomBean)mos[0].getBean();
            }
        } else {
            LOG.warn("could not find FlurstueckSchluessel with query: " + query);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   id  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NullPointerException      DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static FlurstueckSchluesselCustomBean createNewById(final Integer id) {
        if (id == null) {
            throw new NullPointerException("Given id must not be null");
        }

        if (id == -1) {
            throw new IllegalArgumentException("ID -1 is not valid");
        }

        final String query = BASE_QUERY + " WHERE id = " + id;
        return retrieve(query);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   other  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NullPointerException  DOCUMENT ME!
     */
    public static FlurstueckSchluesselCustomBean createNewByFsKey(final FlurstueckSchluessel other) {
        if (other == null) {
            throw new NullPointerException("Given FlurstueckSchluessel must not be null");
        }

        final Integer flur = other.getFlur();
        final Integer fsZaehler = other.getFlurstueckZaehler();
        final Integer fsNenner = other.getFlurstueckNenner();

        Integer gemarkungsId = null;

        final GemarkungCustomBean gemarkung = other.getGemarkung();
        if (gemarkung != null) {
            gemarkungsId = gemarkung.getId();
        }

        if ((flur == null) && (fsZaehler == null) && (fsNenner == null) && (gemarkungsId == null)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(other + " is a Pseudo FlurstueckSchluessel -> return null");
            }
            return null;
        }

        final String query = BASE_QUERY
                    + " WHERE flur " + ((flur == null) ? " is NULL " : (" = " + flur))
                    + " and   flurstueck_zaehler " + ((fsZaehler == null) ? " is NULL " : (" = " + fsZaehler))
                    + " AND   flurstueck_nenner " + ((fsNenner == null) ? " is NULL " : (" = " + fsNenner))
                    + " AND   fk_gemarkung "
                    + (((gemarkungsId == null)) ? " is NULL " : (" = " + gemarkungsId));

        return retrieve(query);
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
     * @param  id  DOCUMENT ME!
     */
    @Override
    public void setId(final Integer id) {
        this.id = id;

        this.propertyChangeSupport.firePropertyChange("id", null, this.id);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Integer getFlur() {
        return this.flur;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setFlur(final Integer val) {
        this.flur = val;
        this.propertyChangeSupport.firePropertyChange("flur", null, this.flur);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getFlurstueck_zaehler() {
        return this.flurstueck_zaehler;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFlurstueck_zaehler(final Integer val) {
        this.flurstueck_zaehler = val;
        this.propertyChangeSupport.firePropertyChange("flurstueck_zaehler", null, this.flurstueck_zaehler);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getFlurstueck_nenner() {
        return this.flurstueck_nenner;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFlurstueck_nenner(final Integer val) {
        this.flurstueck_nenner = val;

        this.propertyChangeSupport.firePropertyChange("flurstueck_nenner", null, this.flurstueck_nenner);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isIst_gesperrt() {
        return this.ist_gesperrt;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getIst_gesperrt() {
        return this.ist_gesperrt;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setIst_gesperrt(final Boolean val) {
        this.ist_gesperrt = val;

        this.propertyChangeSupport.firePropertyChange("ist_gesperrt", null, this.ist_gesperrt);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getBemerkung_sperre() {
        return this.bemerkung_sperre;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setBemerkung_sperre(final String val) {
        this.bemerkung_sperre = val;

        this.propertyChangeSupport.firePropertyChange("bemerkung_sperre", null, this.bemerkung_sperre);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Timestamp getDatum_entstehung() {
        return this.datum_entstehung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setDatum_entstehung(final Timestamp val) {
        this.datum_entstehung = val;

        this.propertyChangeSupport.firePropertyChange("datum_entstehung", null, this.datum_entstehung);
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
    public GemarkungCustomBean getFk_gemarkung() {
        return this.fk_gemarkung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_gemarkung(final GemarkungCustomBean val) {
        this.fk_gemarkung = val;

        this.propertyChangeSupport.firePropertyChange("fk_gemarkung", null, this.fk_gemarkung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Timestamp getDatum_letzter_stadtbesitz() {
        return this.datum_letzter_stadtbesitz;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setDatum_letzter_stadtbesitz(final Timestamp val) {
        this.datum_letzter_stadtbesitz = val;

        this.propertyChangeSupport.firePropertyChange(
            "datum_letzter_stadtbesitz",
            null,
            this.datum_letzter_stadtbesitz);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isWar_staedtisch() {
        return this.war_staedtisch;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getWar_staedtisch() {
        return this.war_staedtisch;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setWar_staedtisch(final Boolean val) {
        this.war_staedtisch = val;

        this.propertyChangeSupport.firePropertyChange("war_staedtisch", null, this.war_staedtisch);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckArtCustomBean getFk_flurstueck_art() {
        return this.fk_flurstueck_art;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_flurstueck_art(final FlurstueckArtCustomBean val) {
        this.fk_flurstueck_art = val;

        this.propertyChangeSupport.firePropertyChange("fk_flurstueck_art", null, this.fk_flurstueck_art);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getLetzter_bearbeiter() {
        return this.letzter_bearbeiter;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setLetzter_bearbeiter(final String val) {
        this.letzter_bearbeiter = val;

        this.propertyChangeSupport.firePropertyChange("letzter_bearbeiter", null, this.letzter_bearbeiter);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Timestamp getLetzte_bearbeitung() {
        return this.letzte_bearbeitung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setLetzte_bearbeitung(final Timestamp val) {
        this.letzte_bearbeitung = val;

        this.propertyChangeSupport.firePropertyChange("letzte_bearbeitung", null, this.letzte_bearbeitung);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public GemarkungCustomBean getGemarkung() {
        return getFk_gemarkung();
    }

    @Override
    public void setGemarkung(final GemarkungCustomBean val) {
        setFk_gemarkung(val);
    }

    @Override
    public Integer getFlurstueckZaehler() {
        return getFlurstueck_zaehler();
    }

    @Override
    public void setFlurstueckZaehler(final Integer val) {
        setFlurstueck_zaehler(val);
    }

    @Override
    public Integer getFlurstueckNenner() {
        return getFlurstueck_nenner();
    }

    @Override
    public void setFlurstueckNenner(final Integer val) {
        setFlurstueck_nenner(val);
    }

    @Override
    public String getKeyString() {
        try {
            if (isEchterSchluessel()) {
                if (getFlurstueckNenner() != null) {
                    return getGemarkung().getBezeichnung() + " " + getFlur() + " " + getFlurstueckZaehler() + "/"
                                + getFlurstueckNenner();
                } else {
                    return getGemarkung().getBezeichnung() + " " + getFlur() + " " + getFlurstueckZaehler();
                }
            } else {
                return "pseudo Schluessel" + getId();
            }
        } catch (Exception ex) {
            final String msg = "Eine oder mehrere Felder der Entität sind null";
            LOG.error(msg, ex);
            return msg;
        }
    }

    @Override
    public boolean isEchterSchluessel() {
        if (getFlurstueckArt() != null) {
            if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_PSEUDO.equals(getFlurstueckArt().getBezeichnung())) {
                return false;
            } else {
                return true;
            }
        } else {
            System.out.println("Warnung eine Flurstücksart ist nicht gesetzt");
            return true;
        }
    }

    @Override
    public boolean getIstGesperrt() {
        final Boolean bool = getIst_gesperrt();
        return (bool == null) ? false : bool;
    }

    @Override
    public void setIstGesperrt(final boolean val) {
        setIst_gesperrt(val);
    }

    @Override
    public boolean isGesperrt() {
        return getIst_gesperrt();
    }

    @Override
    public String getBemerkungSperre() {
        return getBemerkung_sperre();
    }

    @Override
    public void setBemerkungSperre(final String val) {
        setBemerkung_sperre(val);
    }

    @Override
    public Date getEntstehungsDatum() {
        return getDatum_entstehung();
    }

    @Override
    public void setEntstehungsDatum(final Date val) {
        if (val == null) {
            setDatum_entstehung(null);
        } else {
            setDatum_entstehung(new Timestamp(val.getTime()));
        }
    }

    @Override
    public Date getGueltigBis() {
        return getGueltig_bis();
    }

    @Override
    public void setGueltigBis(final Date val) {
        if (val == null) {
            setGueltig_bis(null);
        } else {
            setGueltig_bis(new Timestamp(val.getTime()));
        }
    }

    @Override
    public FlurstueckArtCustomBean getFlurstueckArt() {
        return getFk_flurstueck_art();
    }

    @Override
    public void setFlurstueckArt(final FlurstueckArtCustomBean val) {
        setFk_flurstueck_art(val);
    }

    @Override
    public boolean getWarStaedtisch() {
        final Boolean bool = getWar_staedtisch();
        return (bool == null) ? false : bool;
    }

    @Override
    public void setWarStaedtisch(final boolean var) {
        setWar_staedtisch(var);
    }

    @Override
    public Date getDatumLetzterStadtbesitz() {
        return getDatum_letzter_stadtbesitz();
    }

    @Override
    public void setDatumLetzterStadtbesitz(final Date val) {
        if (val == null) {
            setDatum_letzter_stadtbesitz(null);
        } else {
            setDatum_letzter_stadtbesitz(new Timestamp(val.getTime()));
        }
    }

    @Override
    public void setLetzte_bearbeitung(final Date val) {
        if (val == null) {
            setLetzte_bearbeitung(null);
        } else {
            setLetzte_bearbeitung(new Timestamp(val.getTime()));
        }
    }

    @Override
    public int compareTo(final Object value) {
        if (value instanceof FlurstueckSchluesselCustomBean) {
            final FlurstueckSchluesselCustomBean other = (FlurstueckSchluesselCustomBean)value;
            if ((other != null) && (other.toString() != null) && (toString() != null)) {
                if (other.getFlurstueckZaehler() > getFlurstueckZaehler()) {
                    return -1;
                } else if (other.getFlurstueckZaehler() < getFlurstueckZaehler()) {
                    return 1;
                } else {
                    if ((other.getFlurstueckNenner() != null) && (getFlurstueckNenner() != null)) {
                        if (other.getFlurstueckNenner() > getFlurstueckNenner()) {
                            return -1;
                        } else if (other.getFlurstueckNenner() < getFlurstueckNenner()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    } else if (other.getFlurstueckNenner() != null) {
                        return 1;
                    } else {
                        // Should not be possible --> means duplicated entry in database
                        return 0;
                    }
                }
                // return toString().compareTo(other.toString());
            } else if (toString() == null) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        final Integer z = getFlurstueckZaehler();
        final Integer n = getFlurstueckNenner();
        if ((z != null) && (n != null)) {
            return z.toString() + "/" + n.toString();
        } else if (z != null) {
            return z.toString();
        } else {
            // ToDo could raise problems because it was null before introducing the pseudo keys
            if (!isEchterSchluessel()) {
                return "pseudo" + getId();
            } else {
                // ToDo Exception
                return null;
            }
        }
    }
}
