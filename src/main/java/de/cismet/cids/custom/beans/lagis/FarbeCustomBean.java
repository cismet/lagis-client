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
import de.cismet.lagisEE.entity.core.hardwired.Farbe;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FarbeCustomBean extends BasicEntity implements Farbe {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FarbeCustomBean.class);
    private static final String[] PROPERTY_NAMES = new String[] {
            "id",
            "rgb_farbwert",
            "fk_stil"
        };

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private Integer rgb_farbwert;
    private StilCustomBean fk_stil;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FarbeCustomBean object.
     */
    public FarbeCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static FarbeCustomBean createNew() {
        try {
            return (FarbeCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    LagisConstants.DOMAIN_LAGIS,
                    LagisMetaclassConstants.FARBE);
        } catch (Exception ex) {
            LOG.error("error creating " + LagisMetaclassConstants.FARBE + " bean", ex);
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

    @Override
    public String[] getPropertyNames() {
        return PROPERTY_NAMES;
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
