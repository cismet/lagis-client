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

import de.cismet.lagis.commons.LagisConstants;
import de.cismet.lagis.commons.LagisMetaclassConstants;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.ReBe;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public final class RebeCustomBean extends BasicEntity implements ReBe {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RebeCustomBean.class);
    private static final String[] PROPERTY_NAMES = new String[] {
            "id",
            "datum_eintragung",
            "datum_loeschung",
            "nummer",
            "ist_recht",
            "beschreibung",
            "fk_rebe_art",
            "fk_geom",
            "bemerkung"
        };

    //~ Instance fields --------------------------------------------------------

    private boolean isEditable;
    private boolean modifiable;

    private Integer id;
    private Timestamp datum_eintragung;
    private Timestamp datum_loeschung;
    private String nummer;
    private Boolean ist_recht;
    private String beschreibung;
    private RebeArtCustomBean fk_rebe_art;
    private GeomCustomBean fk_geom;
    private String bemerkung;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RebeCustomBean object.
     */
    public RebeCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static RebeCustomBean createNew() {
        try {
            final RebeCustomBean rebe = (RebeCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    LagisConstants.DOMAIN_LAGIS,
                    LagisMetaclassConstants.REBE);
            // property change mechanism does not work in constructor
            rebe.setIstRecht(Boolean.FALSE);
            return rebe;
        } catch (Exception ex) {
            LOG.error("error creating " + LagisMetaclassConstants.REBE + " bean", ex);
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
    public Timestamp getDatum_loeschung() {
        return this.datum_loeschung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setDatum_loeschung(final Timestamp val) {
        this.datum_loeschung = val;

        this.propertyChangeSupport.firePropertyChange("datum_loeschung", null, this.datum_loeschung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getNummer() {
        return this.nummer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setNummer(final String val) {
        this.nummer = val;

        this.propertyChangeSupport.firePropertyChange("nummer", null, this.nummer);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isIst_recht() {
        return this.ist_recht;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getIst_recht() {
        return this.ist_recht;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setIst_recht(final Boolean val) {
        this.ist_recht = val;

        this.propertyChangeSupport.firePropertyChange("ist_recht", null, this.ist_recht);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getBeschreibung() {
        return this.beschreibung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setBeschreibung(final String val) {
        this.beschreibung = val;

        this.propertyChangeSupport.firePropertyChange("beschreibung", null, this.beschreibung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public RebeArtCustomBean getFk_rebe_art() {
        return this.fk_rebe_art;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_rebe_art(final RebeArtCustomBean val) {
        this.fk_rebe_art = val;

        this.propertyChangeSupport.firePropertyChange("fk_rebe_art", null, this.fk_rebe_art);
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

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public boolean isModifiable() {
        return modifiable;
    }

    @Override
    public void setModifiable(final boolean val) {
        modifiable = val;
    }

    @Override
    public RebeArtCustomBean getReBeArt() {
        return getFk_rebe_art();
    }

    @Override
    public void setReBeArt(final RebeArtCustomBean val) {
        setFk_rebe_art(val);
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
    public Date getDatumLoeschung() {
        return getDatum_loeschung();
    }

    @Override
    public void setDatumLoeschung(final Date val) {
        if (val == null) {
            setDatum_loeschung(null);
        } else {
            setDatum_loeschung(new Timestamp(val.getTime()));
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
    public boolean getIstRecht() {
        final Boolean bool = getIst_recht();
        return (bool == null) ? false : bool;
    }

    @Override
    public void setIstRecht(final boolean val) {
        setIst_recht(val);
    }

    @Override
    public boolean isRecht() {
        final Boolean bool = getIst_recht();
        return (bool == null) ? false : bool;
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
                geomBean = (GeomCustomBean)CidsBean.createNewCidsBeanFromTableName(LagisConstants.DOMAIN_LAGIS, "geom");
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
        return isEditable;
    }

    @Override
    public void setEditable(final boolean editable) {
        isEditable = editable;
        this.setModifiable(editable);
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void hide(final boolean hiding) {
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
        final Collection<FarbeCustomBean> farben;
        if (isRecht()) {
            return Color.GREEN;
        } else {
            return Color.ORANGE;
        }
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
        return "Nummer " + getNummer();
    }
}
