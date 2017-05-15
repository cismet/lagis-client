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
public class ZusatzRolleCustomBean extends BasicEntity {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            ZusatzRolleCustomBean.class);
    private static final String[] PROPERTY_NAMES = new String[] {
            "id",
            "fk_dienststelle",
            "fk_art",
            "fk_geom"
        };

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private VerwaltendeDienststelleCustomBean fk_dienststelle;
    private ZusatzRolleArtCustomBean fk_art;
    private GeomCustomBean fk_geom;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ZusatzRolleCustomBean object.
     */
    public ZusatzRolleCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ZusatzRolleCustomBean createNew() {
        try {
            return (ZusatzRolleCustomBean)CidsBean.createNewCidsBeanFromTableName(
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
     * @param  fk_dienststelle  DOCUMENT ME!
     */
    public void setFk_dienststelle(final VerwaltendeDienststelleCustomBean fk_dienststelle) {
        final Object old = this.fk_dienststelle;
        this.fk_dienststelle = fk_dienststelle;
        this.propertyChangeSupport.firePropertyChange("fk_dienststelle", old, this.fk_dienststelle);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public VerwaltendeDienststelleCustomBean getFk_dienststelle() {
        return this.fk_dienststelle;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fk_art  DOCUMENT ME!
     */
    public void setFk_art(final ZusatzRolleArtCustomBean fk_art) {
        final Object old = this.fk_art;
        this.fk_art = fk_art;
        this.propertyChangeSupport.firePropertyChange("fk_art", old, this.fk_art);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ZusatzRolleArtCustomBean getFk_art() {
        return this.fk_art;
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
    public GeomCustomBean getFk_geom() {
        return this.fk_geom;
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }
}
