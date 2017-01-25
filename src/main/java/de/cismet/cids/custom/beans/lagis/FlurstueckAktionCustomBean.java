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
import de.cismet.lagisEE.entity.history.FlurstueckAktion;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FlurstueckAktionCustomBean extends BasicEntity implements FlurstueckAktion {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            FlurstueckAktionCustomBean.class);
    private static final String[] PROPERTY_NAMES = new String[] { "id", "beschreibung" };

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private String beschreibung;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FlurstueckAktionCustomBean object.
     */
    public FlurstueckAktionCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static FlurstueckAktionCustomBean createNew() {
        try {
            return (FlurstueckAktionCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    LagisConstants.DOMAIN_LAGIS,
                    LagisMetaclassConstants.FLURSTUECK_AKTION);
        } catch (Exception ex) {
            LOG.error("error creating " + LagisMetaclassConstants.FLURSTUECK_AKTION + " bean", ex);
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

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }
}
