/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.verdis_grundis;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.history.FlurstueckHistorie;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FlurstueckHistorieCustomBean extends BasicEntity implements FlurstueckHistorie {

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private Integer index;
    private FlurstueckAktionCustomBean fk_flurstueck_aktion;
    private FlurstueckCustomBean fk_vorgaenger;
    private FlurstueckCustomBean fk_nachfolger;
    private String[] PROPERTY_NAMES = new String[] {
            "id",
            "index",
            "fk_flurstueck_aktion",
            "fk_vorgaenger",
            "fk_nachfolger"
        };

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
    @Override
    public Integer getIndex() {
        return this.index;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setIndex(final Integer val) {
        this.index = val;

        this.propertyChangeSupport.firePropertyChange("index", null, this.index);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckAktionCustomBean getFk_flurstueck_aktion() {
        return this.fk_flurstueck_aktion;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_flurstueck_aktion(final FlurstueckAktionCustomBean val) {
        this.fk_flurstueck_aktion = val;

        this.propertyChangeSupport.firePropertyChange("fk_flurstueck_aktion", null, this.fk_flurstueck_aktion);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckCustomBean getFk_vorgaenger() {
        return this.fk_vorgaenger;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_vorgaenger(final FlurstueckCustomBean val) {
        this.fk_vorgaenger = val;

        this.propertyChangeSupport.firePropertyChange("fk_vorgaenger", null, this.fk_vorgaenger);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckCustomBean getFk_nachfolger() {
        return this.fk_nachfolger;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_nachfolger(final FlurstueckCustomBean val) {
        this.fk_nachfolger = val;

        this.propertyChangeSupport.firePropertyChange("fk_nachfolger", null, this.fk_nachfolger);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public FlurstueckCustomBean getNachfolger() {
        return getFk_nachfolger();
    }

    @Override
    public void setNachfolger(final FlurstueckCustomBean val) {
        setFk_nachfolger(val);
    }

    @Override
    public FlurstueckCustomBean getVorgaenger() {
        return getFk_vorgaenger();
    }

    @Override
    public void setVorgaenger(final FlurstueckCustomBean val) {
        setFk_vorgaenger(val);
    }

    @Override
    public FlurstueckAktionCustomBean getAktion() {
        return getFk_flurstueck_aktion();
    }

    @Override
    public void setAktion(final FlurstueckAktionCustomBean val) {
        setFk_flurstueck_aktion(val);
    }
}
