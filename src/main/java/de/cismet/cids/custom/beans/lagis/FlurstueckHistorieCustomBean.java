/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.lagis;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.commons.LagisConstants;
import de.cismet.lagis.commons.LagisMetaclassConstants;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.history.FlurstueckHistorie;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FlurstueckHistorieCustomBean extends BasicEntity implements FlurstueckHistorie {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            FlurstueckHistorieCustomBean.class);
    private static final String[] PROPERTY_NAMES = new String[] {
            "id",
            "index",
            "fk_flurstueck_aktion",
            "fk_vorgaenger",
            "fk_nachfolger"
        };

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private Integer index;
    private FlurstueckAktionCustomBean fk_flurstueck_aktion;
    private FlurstueckCustomBean fk_vorgaenger;
    private FlurstueckCustomBean fk_nachfolger;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FlurstueckHistorieCustomBean object.
     */
    public FlurstueckHistorieCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static FlurstueckHistorieCustomBean createNew() {
        try {
            return (FlurstueckHistorieCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    LagisConstants.DOMAIN_LAGIS,
                    LagisMetaclassConstants.FLURSTUECK_HISTORIE);
        } catch (Exception ex) {
            LOG.error("error creating " + LagisMetaclassConstants.FLURSTUECK_HISTORIE + " bean", ex);
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
