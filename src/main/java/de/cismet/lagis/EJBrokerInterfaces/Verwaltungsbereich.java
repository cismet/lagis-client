/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.EJBrokerInterfaces;

import java.awt.Stroke;

import de.cismet.cids.custom.beans.verdis_grundis.GeomCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.VerwaltendeDienststelleCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.VerwaltungsgebrauchCustomBean;

import de.cismet.cismap.commons.features.StyledFeature;

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
    Boolean isModifiable();

    /**
     * DOCUMENT ME!
     *
     * @param  modifiable  DOCUMENT ME!
     */
    void setModifiable(final Boolean modifiable);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    VerwaltungsgebrauchCustomBean getGebrauch();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setGebrauch(final VerwaltungsgebrauchCustomBean val);

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
