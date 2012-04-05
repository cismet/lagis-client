/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.lagis;

import com.vividsolutions.jts.geom.Geometry;

import java.awt.Color;
import java.awt.Paint;

import java.sql.Timestamp;

import java.util.Collection;
import java.util.Date;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;

import de.cismet.lagis.broker.CidsBroker;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.extension.vermietung.MiPa;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class MipaCustomBean extends BasicEntity implements MiPa {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MipaCustomBean.class);
    public static final String TABLE = "mipa";

    //~ Instance fields --------------------------------------------------------

    private Boolean isEditable = false;
    private transient Boolean isHidden = false;
    private Boolean modifiable = true;

    private Integer id;
    private String nutzer;
    private String lage;
    private String nutzung;
    private Timestamp vertragsbeginn;
    private Timestamp vertragsende;
    private Double flaeche;
    private String bemerkung;
    private GeomCustomBean fk_geom;
    private String aktenzeichen;
    private MipaNutzungCustomBean fk_mipa_nutzung;
    private Collection<MipaMerkmalCustomBean> ar_mipa_merkmale;
    private String[] PROPERTY_NAMES = new String[] {
            "id",
            "nutzer",
            "lage",
            "nutzung",
            "vertragsbeginn",
            "vertragsende",
            "flaeche",
            "bemerkung",
            "fk_geom",
            "aktenzeichen",
            "fk_mipa_nutzung",
            "ar_mipa_merkmale"
        };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MipaCustomBean object.
     */
    public MipaCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static MipaCustomBean createNew() {
        try {
            return (MipaCustomBean)CidsBean.createNewCidsBeanFromTableName(CidsBroker.LAGIS_DOMAIN, TABLE);
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
     * @param  paramInteger  DOCUMENT ME!
     */
    @Override
    public void setId(final Integer paramInteger) {
        this.id = paramInteger;

        this.propertyChangeSupport.firePropertyChange("id", null, this.id);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getNutzer() {
        return this.nutzer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setNutzer(final String val) {
        this.nutzer = val;

        this.propertyChangeSupport.firePropertyChange("nutzer", null, this.nutzer);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getLage() {
        return this.lage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setLage(final String val) {
        this.lage = val;

        this.propertyChangeSupport.firePropertyChange("lage", null, this.lage);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getNutzung() {
        return this.nutzung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setNutzung(final String val) {
        this.nutzung = val;

        this.propertyChangeSupport.firePropertyChange("nutzung", null, this.nutzung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Timestamp getVertragsbeginn() {
        return this.vertragsbeginn;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setVertragsbeginn(final Timestamp val) {
        this.vertragsbeginn = val;

        this.propertyChangeSupport.firePropertyChange("vertragsbeginn", null, this.vertragsbeginn);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Timestamp getVertragsende() {
        return this.vertragsende;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setVertragsende(final Timestamp val) {
        this.vertragsende = val;

        this.propertyChangeSupport.firePropertyChange("vertragsende", null, this.vertragsende);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Double getFlaeche() {
        return this.flaeche;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setFlaeche(final Double val) {
        this.flaeche = val;

        this.propertyChangeSupport.firePropertyChange("flaeche", null, this.flaeche);
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
    public GeomCustomBean getFk_geom() {
        return this.fk_geom;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_geom(final GeomCustomBean val) {
        this.fk_geom = val;

        this.propertyChangeSupport.firePropertyChange("fk_geom", null, this.fk_geom);
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
    public MipaNutzungCustomBean getFk_mipa_nutzung() {
        return this.fk_mipa_nutzung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_mipa_nutzung(final MipaNutzungCustomBean val) {
        this.fk_mipa_nutzung = val;

        this.propertyChangeSupport.firePropertyChange("fk_mipa_nutzung", null, this.fk_mipa_nutzung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<MipaMerkmalCustomBean> getAr_mipa_merkmale() {
        return this.ar_mipa_merkmale;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setAr_mipa_merkmale(final Collection<MipaMerkmalCustomBean> val) {
        this.ar_mipa_merkmale = val;

        this.propertyChangeSupport.firePropertyChange("ar_mipa_merkmale", null, this.ar_mipa_merkmale);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public Boolean isModifiable() {
        return modifiable;
    }

    @Override
    public void setModifiable(final Boolean val) {
        modifiable = val;
    }

    @Override
    public Collection<MipaMerkmalCustomBean> getMiPaMerkmal() {
        return getAr_mipa_merkmale();
    }

    @Override
    public void setMiPaMerkmal(final Collection<MipaMerkmalCustomBean> val) {
        setAr_mipa_merkmale(val);
    }

    @Override
    public MipaNutzungCustomBean getMiPaNutzung() {
        return getFk_mipa_nutzung();
    }

    @Override
    public void setMiPaNutzung(final MipaNutzungCustomBean val) {
        setFk_mipa_nutzung(val);
    }

    @Override
    public String getLaufendeNummer() {
        if (getId() != null) {
            return "VV-" + getId();
        } else {
            return "";
        }
    }

    @Override
    public GeomCustomBean getGeometrie() {
        return getFk_geom();
    }

    @Override
    public void setGeometrie(final GeomCustomBean val) {
        setFk_geom(val);
    }

    @Override
    public void setVertragsbeginn(final Date val) {
        if (val == null) {
            setVertragsbeginn(null);
        } else {
            setVertragsbeginn(new Timestamp(val.getTime()));
        }
    }

    @Override
    public void setVertragsende(final Date val) {
        if (val == null) {
            setVertragsende(null);
        } else {
            setVertragsende(new Timestamp(val.getTime()));
        }
    }

    @Override
    public Geometry getGeometry() {
        final GeomCustomBean geomBean = getGeometrie();
        if (geomBean == null) {
            return null;
        }
        return geomBean.getGeomField();
    }

    @Override
    public void setGeometry(final Geometry geom) {
        GeomCustomBean geomBean = getGeometrie();
        if (getGeometrie() == null) {
            try {
                geomBean = (GeomCustomBean)CidsBean.createNewCidsBeanFromTableName(CidsBroker.LAGIS_DOMAIN, "geom");
            } catch (Exception ex) {
                LOG.error("error creating geom bean", ex);
            }
            setGeometrie(geomBean);
        }
        geomBean.setGeomField(geom);
    }

    @Override
    public boolean canBeSelected() {
        return true;
    }

    @Override
    public void setCanBeSelected(final boolean canBeSelected) {
    }

    @Override
    public boolean isEditable() {
        if (!isModifiable()) {
            return false;
        }
        if (isEditable != null) {
            return isEditable;
        } else {
            return false;
        }
    }

    @Override
    public void setEditable(final boolean editable) {
        isEditable = editable;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void hide(final boolean hiding) {
        isHidden = hiding;
    }

    @Override
    public Paint getLinePaint() {
        return Color.BLACK;
    }

    @Override
    public void setLinePaint(final Paint linePaint) {
    }

    @Override
    public int getLineWidth() {
        return 1;
    }

    @Override
    public void setLineWidth(final int width) {
    }

    @Override
    public Paint getFillingPaint() {
        return Color.CYAN;
    }

    @Override
    public void setFillingPaint(final Paint fillingStyle) {
    }

    @Override
    public float getTransparency() {
        return 0.5f;
    }

    @Override
    public void setTransparency(final float transparrency) {
    }

    @Override
    public FeatureAnnotationSymbol getPointAnnotationSymbol() {
        return null;
    }

    @Override
    public void setPointAnnotationSymbol(final FeatureAnnotationSymbol featureAnnotationSymbol) {
    }

    @Override
    public boolean isHighlightingEnabled() {
        return true;
    }

    @Override
    public void setHighlightingEnabled(final boolean enabled) {
    }

    @Override
    public String toString() {
        return "de.cismet.lagisEE.entity.extension.vermietung.MiePa[id=" + getId() + "]";
    }
}
