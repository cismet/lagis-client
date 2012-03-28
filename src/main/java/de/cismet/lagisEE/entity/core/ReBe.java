/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagisEE.entity.core;

import java.awt.Stroke;

import java.util.Date;

import de.cismet.cids.custom.beans.verdis_grundis.GeomCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.RebeArtCustomBean;

import de.cismet.cismap.commons.features.StyledFeature;

import de.cismet.lagisEE.interfaces.GeometrySlot;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface ReBe extends GeometrySlot, StyledFeature {

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
    RebeArtCustomBean getReBeArt();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setReBeArt(final RebeArtCustomBean val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getDatumEintragung();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setDatumEintragung(final Date val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getDatumLoeschung();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setDatumLoeschung(final Date val);

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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getNummer();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setNummer(final String val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Boolean getIstRecht();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setIstRecht(final Boolean val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Boolean isRecht();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getBeschreibung();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setBeschreibung(final String val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getBemerkung();

    /**
     * DOCUMENT ME!
     *
     * @param  bemerkung  DOCUMENT ME!
     */
    void setBemerkung(final String bemerkung);
}
