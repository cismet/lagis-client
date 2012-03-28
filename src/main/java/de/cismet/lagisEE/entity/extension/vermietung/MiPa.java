/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagisEE.entity.extension.vermietung;

import java.awt.Stroke;

import java.io.Serializable;

import java.util.Collection;
import java.util.Date;

import de.cismet.cids.custom.beans.verdis_grundis.GeomCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.MipaMerkmalCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.MipaNutzungCustomBean;

import de.cismet.cismap.commons.features.StyledFeature;

import de.cismet.lagisEE.interfaces.GeometrySlot;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface MiPa extends GeometrySlot, StyledFeature, Serializable {

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
    String getBemerkung();

    /**
     * DOCUMENT ME!
     *
     * @param  bemerkung  DOCUMENT ME!
     */
    void setBemerkung(final String bemerkung);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getLage();

    /**
     * DOCUMENT ME!
     *
     * @param  miPaLage  DOCUMENT ME!
     */
    void setLage(final String miPaLage);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getAktenzeichen();

    /**
     * DOCUMENT ME!
     *
     * @param  aktenzeichen  DOCUMENT ME!
     */
    void setAktenzeichen(final String aktenzeichen);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<MipaMerkmalCustomBean> getMiPaMerkmal();

    /**
     * DOCUMENT ME!
     *
     * @param  miPaMerkmal  DOCUMENT ME!
     */
    void setMiPaMerkmal(final Collection<MipaMerkmalCustomBean> miPaMerkmal);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    MipaNutzungCustomBean getMiPaNutzung();

    /**
     * DOCUMENT ME!
     *
     * @param  miPaNutzung  DOCUMENT ME!
     */
    void setMiPaNutzung(final MipaNutzungCustomBean miPaNutzung);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getNutzung();

    /**
     * DOCUMENT ME!
     *
     * @param  nutzung  DOCUMENT ME!
     */
    void setNutzung(final String nutzung);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getNutzer();

    /**
     * DOCUMENT ME!
     *
     * @param  nutzer  DOCUMENT ME!
     */
    void setNutzer(final String nutzer);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getLaufendeNummer();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    GeomCustomBean getGeometrie();

    /**
     * DOCUMENT ME!
     *
     * @param  geometrie  DOCUMENT ME!
     */
    void setGeometrie(final GeomCustomBean geometrie);

    /**
     * DOCUMENT ME!
     *
     * @param  flaeche  DOCUMENT ME!
     */
    void setFlaeche(final Double flaeche);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Double getFlaeche();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getVertragsbeginn();

    /**
     * DOCUMENT ME!
     *
     * @param  vertragsbeginn  DOCUMENT ME!
     */
    void setVertragsbeginn(final Date vertragsbeginn);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getVertragsende();

    /**
     * DOCUMENT ME!
     *
     * @param  vertragsende  DOCUMENT ME!
     */
    void setVertragsende(final Date vertragsende);
}
