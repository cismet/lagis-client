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
import java.util.Date;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;

import de.cismet.lagis.broker.CidsBroker;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.extension.baum.Baum;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BaumCustomBean extends BasicEntity implements Baum {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            BaumCustomBean.class);
    public static final String TABLE = "baum";

    //~ Instance fields --------------------------------------------------------

    private boolean isEditable = true;
    private boolean modifiable = false;

    private Integer id;
    private String lage;
    private String auftragnehmer;
    private String baumnummer;
    private String alte_nutzung;
    private Date erfassungsdatum;
    private Date faelldatum;
    private Double flaeche;
    private String bemerkung;
    private GeomCustomBean fk_geom;
    private BaumNutzungCustomBean fk_baum_nutzung;
    private Collection<BaumMerkmalCustomBean> ar_baum_merkmale;
    private String[] PROPERTY_NAMES = new String[] {
            "id",
            "lage",
            "auftragnehmer",
            "baumnummer",
            "alte_nutzung",
            "erfassungsdatum",
            "faelldatum",
            "flaeche",
            "bemerkung",
            "fk_geom",
            "fk_baum_nutzung",
            "ar_baum_merkmale"
        };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumCustomBean object.
     */
    public BaumCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BaumCustomBean createNew() {
        try {
            final BaumCustomBean bean = (BaumCustomBean)CidsBean.createNewCidsBeanFromTableName(
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
    public String getAuftragnehmer() {
        return this.auftragnehmer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setAuftragnehmer(final String val) {
        this.auftragnehmer = val;

        this.propertyChangeSupport.firePropertyChange("auftragnehmer", null, this.auftragnehmer);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getBaumnummer() {
        return this.baumnummer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setBaumnummer(final String val) {
        this.baumnummer = val;

        this.propertyChangeSupport.firePropertyChange("baumnummer", null, this.baumnummer);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAlte_nutzung() {
        return this.alte_nutzung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setAlte_nutzung(final String val) {
        this.alte_nutzung = val;

        this.propertyChangeSupport.firePropertyChange("alte_nutzung", null, this.alte_nutzung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Date getErfassungsdatum() {
        return this.erfassungsdatum;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setErfassungsdatum(final Date val) {
        this.erfassungsdatum = val;

        this.propertyChangeSupport.firePropertyChange("erfassungsdatum", null, this.erfassungsdatum);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Date getFaelldatum() {
        return this.faelldatum;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setFaelldatum(final Date val) {
        this.faelldatum = val;

        this.propertyChangeSupport.firePropertyChange("faelldatum", null, this.faelldatum);
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
    public BaumNutzungCustomBean getFk_baum_nutzung() {
        return this.fk_baum_nutzung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_baum_nutzung(final BaumNutzungCustomBean val) {
        this.fk_baum_nutzung = val;

        this.propertyChangeSupport.firePropertyChange("fk_baum_nutzung", null, this.fk_baum_nutzung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BaumMerkmalCustomBean> getAr_baum_merkmale() {
        return this.ar_baum_merkmale;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setAr_baum_merkmale(final Collection<BaumMerkmalCustomBean> val) {
        this.ar_baum_merkmale = val;

        this.propertyChangeSupport.firePropertyChange("ar_baum_merkmale", null, this.ar_baum_merkmale);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public Collection<BaumMerkmalCustomBean> getBaumMerkmal() {
        return getAr_baum_merkmale();
    }

    @Override
    public void setBaumMerkmal(final Collection<BaumMerkmalCustomBean> val) {
        setAr_baum_merkmale(val);
    }

    @Override
    public BaumNutzungCustomBean getBaumNutzung() {
        return getFk_baum_nutzung();
    }

    @Override
    public void setBaumNutzung(final BaumNutzungCustomBean baumNutzung) {
        setFk_baum_nutzung(baumNutzung);
    }

    @Override
    public String getAlteNutzung() {
        return getAlte_nutzung();
    }

    @Override
    public void setAlteNutzung(final String val) {
        setAlte_nutzung(val);
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
    public boolean isModifiable() {
        return modifiable;
    }

    @Override
    public void setModifiable(final boolean val) {
        modifiable = val;
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
    public void hide(final boolean val) {
    }

    @Override
    public Paint getLinePaint() {
        return Color.BLACK;
    }

    @Override
    public void setLinePaint(final Paint val) {
    }

    @Override
    public int getLineWidth() {
        return 1;
    }

    @Override
    public void setLineWidth(final int val) {
    }

    @Override
    public Paint getFillingPaint() {
        return Color.CYAN;
    }

    @Override
    public void setFillingPaint(final Paint val) {
    }

    @Override
    public float getTransparency() {
        return 0.5f;
    }

    @Override
    public void setTransparency(final float val) {
    }

    @Override
    public FeatureAnnotationSymbol getPointAnnotationSymbol() {
        return null;
    }

    @Override
    public void setPointAnnotationSymbol(final FeatureAnnotationSymbol val) {
    }

    @Override
    public boolean isHighlightingEnabled() {
        return true;
    }

    @Override
    public void setHighlightingEnabled(final boolean val) {
    }

    @Override
    public String toString() {
        return "Baumnummer " + getBaumnummer();
    }
}
