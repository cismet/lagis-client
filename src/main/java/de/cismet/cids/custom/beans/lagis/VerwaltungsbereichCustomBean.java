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

import de.cismet.lagis.commons.LagisConstants;
import de.cismet.lagis.commons.LagisMetaclassConstants;

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
    private static final String[] PROPERTY_NAMES = new String[] {
            "id",
            "fk_verwaltende_dienststelle",
            "fk_verwaltungsbereiche_eintrag",
            "flaeche",
            "fk_geom"
        };

    //~ Instance fields --------------------------------------------------------

    private boolean modifiable;
    private boolean isEditable;

    private Integer id;
    private VerwaltendeDienststelleCustomBean fk_verwaltende_dienststelle;
    private VerwaltungsbereicheEintragCustomBean fk_verwaltungsbereiche_eintrag;
    private GeomCustomBean fk_geom;
    private Integer flaeche;

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
                    LagisConstants.DOMAIN_LAGIS,
                    LagisMetaclassConstants.VERWALTUNGSBEREICH);
            return bean;
        } catch (Exception ex) {
            LOG.error("error creating " + LagisMetaclassConstants.VERWALTUNGSBEREICH + " bean", ex);
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
     * @param  id  DOCUMENT ME!
     */
    @Override
    public void setId(final Integer id) {
        final Object old = this.id;
        this.id = id;
        this.propertyChangeSupport.firePropertyChange("id", old, this.id);
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
     * @param  fk_verwaltende_dienststelle  DOCUMENT ME!
     */
    public void setFk_verwaltende_dienststelle(final VerwaltendeDienststelleCustomBean fk_verwaltende_dienststelle) {
        final Object old = this.fk_verwaltende_dienststelle;
        this.fk_verwaltende_dienststelle = fk_verwaltende_dienststelle;
        this.propertyChangeSupport.firePropertyChange(
            "fk_verwaltende_dienststelle",
            old,
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
     * @param  fk_geom  DOCUMENT ME!
     */
    public void setFk_geom(final GeomCustomBean fk_geom) {
        final Object old = this.fk_geom;
        this.fk_geom = fk_geom;
        this.propertyChangeSupport.firePropertyChange("fk_geom", old, this.fk_geom);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public VerwaltungsbereicheEintragCustomBean getFk_verwaltungsbereiche_eintrag() {
        return fk_verwaltungsbereiche_eintrag;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fk_verwaltungsbereiche_eintrag  DOCUMENT ME!
     */
    public void setFk_verwaltungsbereiche_eintrag(
            final VerwaltungsbereicheEintragCustomBean fk_verwaltungsbereiche_eintrag) {
        final Object old = this.fk_verwaltungsbereiche_eintrag;
        this.fk_verwaltungsbereiche_eintrag = fk_verwaltungsbereiche_eintrag;
        this.propertyChangeSupport.firePropertyChange(
            "fk_verwaltungsbereiche_eintrag",
            old,
            this.fk_verwaltungsbereiche_eintrag);
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
    public Integer getFlaeche() {
        return flaeche;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flaeche  DOCUMENT ME!
     */
    public void setFlaeche(final Integer flaeche) {
        final Object old = this.flaeche;
        this.flaeche = flaeche;
        this.propertyChangeSupport.firePropertyChange("flaeche", old, this.flaeche);
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
                geomBean = (GeomCustomBean)CidsBean.createNewCidsBeanFromTableName(LagisConstants.DOMAIN_LAGIS, "geom");
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
        final Color c = Color.BLACK;
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() / 2);
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
        final Color c;
        if ((getDienststelle() != null) && ((farben = getDienststelle().getFarben()) != null)) {
            final Iterator<FarbeCustomBean> it = farben.iterator();
            final FarbeCustomBean farbe;
            if (it.hasNext() && ((farbe = it.next()) != null)) {
                c = new Color(farbe.getRgbFarbwert());
            } else {
                c = Color.BLACK;
            }
        } else {
            c = Color.BLACK;
        }
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() / 2);
    }

    @Override
    public void setFillingPaint(final Paint fillingStyle) {
    }

    @Override
    public float getTransparency() {
        return 1f;
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
        return "D: " + getDienststelle();
    }
}
