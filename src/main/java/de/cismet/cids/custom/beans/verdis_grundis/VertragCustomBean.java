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

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.broker.CidsBroker;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.Vertrag;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VertragCustomBean extends BasicEntity implements Vertrag {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VertragCustomBean.class);
    public static final String TABLE = "vertrag";

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private String aktenzeichen;
    private String vertragspartner;
    private Timestamp datum_auflassung;
    private Timestamp datum_eintragung;
    private String bemerkung;
    private Double quadratmeterpreis;
    private Double gesamtpreis;
    private VertragsartCustomBean fk_vertragsart;
    private Collection<BeschlussCustomBean> n_beschluesse;
    private Collection<KostenCustomBean> n_kosten;
    private String[] PROPERTY_NAMES = new String[] {
            "id",
            "aktenzeichen",
            "vertragspartner",
            "datum_auflassung",
            "datum_eintragung",
            "bemerkung",
            "quadratmeterpreis",
            "gesamtpreis",
            "fk_vertragsart",
            "n_beschluesse",
            "n_kosten"
        };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VertragCustomBean object.
     */
    public VertragCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static VertragCustomBean createNew() {
        try {
            return (VertragCustomBean)CidsBean.createNewCidsBeanFromTableName(CidsBroker.LAGIS_DOMAIN, TABLE);
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
    public String getAktenzeichen() {
        return this.aktenzeichen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setAktenzeichen(final String val) {
        this.aktenzeichen = val;

        this.propertyChangeSupport.firePropertyChange("aktenzeichen", null, this.aktenzeichen);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getVertragspartner() {
        return this.vertragspartner;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setVertragspartner(final String val) {
        this.vertragspartner = val;

        this.propertyChangeSupport.firePropertyChange("vertragspartner", null, this.vertragspartner);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Timestamp getDatum_auflassung() {
        return this.datum_auflassung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setDatum_auflassung(final Timestamp val) {
        this.datum_auflassung = val;

        this.propertyChangeSupport.firePropertyChange("datum_auflassung", null, this.datum_auflassung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Timestamp getDatum_eintragung() {
        return this.datum_eintragung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setDatum_eintragung(final Timestamp val) {
        this.datum_eintragung = val;

        this.propertyChangeSupport.firePropertyChange("datum_eintragung", null, this.datum_eintragung);
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
    @Override
    public Double getGesamtpreis() {
        return this.gesamtpreis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setGesamtpreis(final Double val) {
        this.gesamtpreis = val;

        this.propertyChangeSupport.firePropertyChange("gesamtpreis", null, this.gesamtpreis);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public VertragsartCustomBean getFk_vertragsart() {
        return this.fk_vertragsart;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_vertragsart(final VertragsartCustomBean val) {
        this.fk_vertragsart = val;

        this.propertyChangeSupport.firePropertyChange("fk_vertragsart", null, this.fk_vertragsart);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BeschlussCustomBean> getN_beschluesse() {
        return this.n_beschluesse;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setN_beschluesse(final Collection<BeschlussCustomBean> val) {
        this.n_beschluesse = val;

        this.propertyChangeSupport.firePropertyChange("n_beschluesse", null, this.n_beschluesse);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<KostenCustomBean> getN_kosten() {
        return this.n_kosten;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  paramObservableList DOCUMENT ME!
     */
    public void setN_kosten(final Collection<KostenCustomBean> val) {
        this.n_kosten = val;

        this.propertyChangeSupport.firePropertyChange("n_kosten", null, this.n_kosten);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public Collection<BeschlussCustomBean> getBeschluesse() {
        return getN_beschluesse();
    }

    @Override
    public void setBeschluesse(final Collection<BeschlussCustomBean> val) {
        setN_beschluesse(val);
    }

    @Override
    public Date getDatumAuflassung() {
        return getDatum_auflassung();
    }

    @Override
    public void setDatumAuflassung(final Date val) {
        if (val == null) {
            setDatum_auflassung(null);
        } else {
            setDatum_auflassung(new Timestamp(val.getTime()));
        }
    }

    @Override
    public Date getDatumEintragung() {
        return getDatum_eintragung();
    }

    @Override
    public void setDatumEintragung(final Date val) {
        if (val == null) {
            setDatum_eintragung(null);
        } else {
            setDatum_eintragung(new Timestamp(val.getTime()));
        }
    }

    @Override
    public Collection<KostenCustomBean> getKosten() {
        return getN_kosten();
    }

    @Override
    public void setKosten(final Collection<KostenCustomBean> val) {
        setN_kosten(val);
    }

    @Override
    public VertragsartCustomBean getVertragsart() {
        return getFk_vertragsart();
    }

    @Override
    public void setVertragsart(final VertragsartCustomBean val) {
        setFk_vertragsart(val);
    }
}
