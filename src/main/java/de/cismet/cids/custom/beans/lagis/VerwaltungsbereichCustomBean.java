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

import java.util.Collection;
import java.util.Iterator;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;

import de.cismet.lagis.broker.CidsBroker;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.Verwaltungsbereich;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VerwaltungsbereichCustomBean extends BasicEntity implements Verwaltungsbereich {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            VerwaltungsbereichCustomBean.class);
    public static final String TABLE = "verwaltungsbereich";

    //~ Instance fields --------------------------------------------------------

    private boolean modifiable;
    private boolean isEditable;

    private Integer id;
    private VerwaltungsgebrauchCustomBean fk_verwaltungsgebrauch;
    private VerwaltendeDienststelleCustomBean fk_verwaltende_dienststelle;
    private GeomCustomBean fk_geom;
    private FlurstueckCustomBean fk_flurstueck;
    private String[] PROPERTY_NAMES = new String[] {
            "id",
            "fk_verwaltungsgebrauch",
            "fk_verwaltende_dienststelle",
            "fk_geom",
            "fk_flurstueck"
        };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VerwaltungsbereichCustomBean object.
     */
    public VerwaltungsbereichCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static VerwaltungsbereichCustomBean createNew() {
        try {
            final VerwaltungsbereichCustomBean bean;
            bean = (VerwaltungsbereichCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    CidsBroker.LAGIS_DOMAIN,
                    TABLE);
            return bean;
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
    public VerwaltungsgebrauchCustomBean getFk_verwaltungsgebrauch() {
        return this.fk_verwaltungsgebrauch;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_verwaltungsgebrauch(final VerwaltungsgebrauchCustomBean val) {
        this.fk_verwaltungsgebrauch = val;

        this.propertyChangeSupport.firePropertyChange("fk_verwaltungsgebrauch", null, this.fk_verwaltungsgebrauch);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public VerwaltendeDienststelleCustomBean getFk_verwaltende_dienststelle() {
        return this.fk_verwaltende_dienststelle;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_verwaltende_dienststelle(final VerwaltendeDienststelleCustomBean val) {
        this.fk_verwaltende_dienststelle = val;

        this.propertyChangeSupport.firePropertyChange(
            "fk_verwaltende_dienststelle",
            null,
            this.fk_verwaltende_dienststelle);
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
    public FlurstueckCustomBean getFk_flurstueck() {
        return this.fk_flurstueck;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_flurstueck(final FlurstueckCustomBean val) {
        this.fk_flurstueck = val;

        this.propertyChangeSupport.firePropertyChange("fk_flurstueck", null, this.fk_flurstueck);
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
    public VerwaltungsgebrauchCustomBean getGebrauch() {
        return getFk_verwaltungsgebrauch();
    }

    @Override
    public void setGebrauch(final VerwaltungsgebrauchCustomBean val) {
        setFk_verwaltungsgebrauch(val);
    }

    @Override
    public Integer getFlaeche() {
        final Geometry tmp = getGeometry();
        if (tmp != null) {
            return (int)Math.round(tmp.getArea());
        } else {
            return 0;
        }
    }

    @Override
    public VerwaltendeDienststelleCustomBean getDienststelle() {
        return getFk_verwaltende_dienststelle();
    }

    @Override
    public void setDienststelle(final VerwaltendeDienststelleCustomBean val) {
        setFk_verwaltende_dienststelle(val);
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
    public Geometry getGeometry() {
        final GeomCustomBean geomBean = getGeometrie();
        if (geomBean == null) {
            return null;
        }
        return geomBean.getGeomField();
    }

    @Override
    public void setGeometry(final Geometry val) {
        GeomCustomBean geomBean = getGeometrie();
        if (getGeometrie() == null) {
            try {
                geomBean = (GeomCustomBean)CidsBean.createNewCidsBeanFromTableName(CidsBroker.LAGIS_DOMAIN, "geom");
            } catch (Exception ex) {
                LOG.error("error creating geom bean", ex);
            }
            setGeometrie(geomBean);
        }
        geomBean.setGeomField(val);
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
    public void setEditable(final boolean val) {
        isEditable = val;
        this.setModifiable(val);
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
        if ((getGebrauch() != null) && ((farben = getGebrauch().getFarben()) != null)) {
            final Iterator<FarbeCustomBean> it = farben.iterator();
            final FarbeCustomBean farbe;
            if (it.hasNext() && ((farbe = it.next()) != null)) {
                return new Color(farbe.getRgbFarbwert());
            } else {
                return Color.BLACK;
            }
        } else {
            return Color.BLACK;
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
}
