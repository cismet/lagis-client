/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.beans.lagis;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.commons.LagisConstants;
import de.cismet.lagis.commons.LagisMetaclassConstants;

import de.cismet.lagisEE.entity.basic.BasicEntity;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class StrassenfrontCustomBean extends BasicEntity {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StrassenfrontCustomBean.class);
    private static final String[] PROPERTY_NAMES = new String[] {
            "id",
            "laenge",
            "strassenname",
        };

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private Double laenge;
    private String strassenname;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ZusatzRolleCustomBean object.
     */
    public StrassenfrontCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static StrassenfrontCustomBean createNew() {
        try {
            return (StrassenfrontCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    LagisConstants.DOMAIN_LAGIS,
                    LagisMetaclassConstants.STRASSENFRONT);
        } catch (Exception ex) {
            LOG.error("error creating " + LagisMetaclassConstants.STRASSENFRONT + " bean", ex);
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
     * @param  laenge  DOCUMENT ME!
     */
    public void setLaenge(final Double laenge) {
        final Object old = this.laenge;
        this.laenge = laenge;
        this.propertyChangeSupport.firePropertyChange("laenge", old, this.laenge);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Double getLaenge() {
        return this.laenge;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  strassenname  DOCUMENT ME!
     */
    public void setStrassenname(final String strassenname) {
        final Object old = this.laenge;
        this.strassenname = strassenname;
        this.propertyChangeSupport.firePropertyChange("strassenname", old, this.strassenname);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getStrassenname() {
        return this.strassenname;
    }

    @Override
    public String[] getPropertyNames() {
        return PROPERTY_NAMES;
    }
}
