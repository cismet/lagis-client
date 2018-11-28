/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagisEE.entity.core;

import de.cismet.cids.custom.beans.lagis.GeomCustomBean;
import de.cismet.cids.custom.beans.lagis.VerwaltendeDienststelleCustomBean;

import de.cismet.cismap.commons.features.StyledFeature;

import de.cismet.lagisEE.interfaces.GeometrySlot;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface Verwaltungsbereich extends GeometrySlot, StyledFeature, Cloneable {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Integer getId();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setId(final Integer val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isModifiable();

    /**
     * DOCUMENT ME!
     *
     * @param  modifiable  DOCUMENT ME!
     */
    void setModifiable(final boolean modifiable);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Integer getFlaeche();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    VerwaltendeDienststelleCustomBean getDienststelle();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setDienststelle(final VerwaltendeDienststelleCustomBean val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    GeomCustomBean getGeometrie();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setGeometrie(final GeomCustomBean val);
}
