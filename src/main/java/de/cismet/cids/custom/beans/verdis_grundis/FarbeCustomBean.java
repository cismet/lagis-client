/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.verdis_grundis;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.hardwired.Farbe;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FarbeCustomBean extends BasicEntity implements Farbe {

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private Integer rgb_farbwert;
    private StilCustomBean fk_stil;
    private VerwaltungsgebrauchCustomBean fk_verwaltungsgebrauch;
    private String[] PROPERTY_NAMES = new String[] { "id", "rgb_farbwert", "fk_stil", "fk_verwaltungsgebrauch" };

    //~ Methods ----------------------------------------------------------------

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
    public Integer getRgb_farbwert() {
        return this.rgb_farbwert;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setRgb_farbwert(final Integer val) {
        this.rgb_farbwert = val;

        this.propertyChangeSupport.firePropertyChange("rgb_farbwert", null, this.rgb_farbwert);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public StilCustomBean getFk_stil() {
        return this.fk_stil;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_stil(final StilCustomBean val) {
        this.fk_stil = val;

        this.propertyChangeSupport.firePropertyChange("fk_stil", null, this.fk_stil);
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

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public void setStil(final StilCustomBean val) {
        setFk_stil(val);
    }

    @Override
    public StilCustomBean getStil() {
        return getFk_stil();
    }

    @Override
    public Integer getRgbFarbwert() {
        return getRgb_farbwert();
    }

    @Override
    public void setRgbFarbwert(final Integer val) {
        setRgb_farbwert(val);
    }
}
