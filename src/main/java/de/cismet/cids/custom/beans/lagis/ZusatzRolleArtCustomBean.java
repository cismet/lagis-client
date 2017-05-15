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
public class ZusatzRolleArtCustomBean extends BasicEntity {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            ZusatzRolleArtCustomBean.class);

    private static final String[] PROPERTY_NAMES = new String[] {
            "id",
            "name",
            "schluessel"
        };

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private String name;
    private String schluessel;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ZusatzRolleArtCustomBean object.
     */
    public ZusatzRolleArtCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ZusatzRolleArtCustomBean createNew() {
        try {
            return (ZusatzRolleArtCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    LagisConstants.DOMAIN_LAGIS,
                    LagisMetaclassConstants.MC_ZUSATZ_ROLLE);
        } catch (Exception ex) {
            LOG.error("error creating " + LagisMetaclassConstants.MC_ZUSATZ_ROLLE + " bean", ex);
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
     * @param  name  DOCUMENT ME!
     */
    public void setName(final String name) {
        final Object old = this.name;
        this.name = name;
        this.propertyChangeSupport.firePropertyChange("name", old, this.name);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getName() {
        return this.name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  schluessel  DOCUMENT ME!
     */
    public void setSchluessel(final String schluessel) {
        final Object old = this.schluessel;
        this.schluessel = schluessel;
        this.propertyChangeSupport.firePropertyChange("schluessel", old, this.schluessel);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSchluessel() {
        return this.schluessel;
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }
}
