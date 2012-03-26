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
package de.cismet.lagis.EJBrokerInterfaces;

import java.io.Serializable;

import java.util.Collection;
import java.util.Date;

import de.cismet.cids.custom.beans.verdis_grundis.BaumMerkmalCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.BaumNutzungCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.GeomCustomBean;

import de.cismet.cismap.commons.features.StyledFeature;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface Baum extends GeometrySlot, StyledFeature, Serializable {

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
     * @param  id  DOCUMENT ME!
     */
    void setId(final Integer id);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getAuftragnehmer();

    /**
     * DOCUMENT ME!
     *
     * @param  auftragnehmer  DOCUMENT ME!
     */
    void setAuftragnehmer(final String auftragnehmer);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<BaumMerkmalCustomBean> getBaumMerkmal();

    /**
     * DOCUMENT ME!
     *
     * @param  baumMerkmal  DOCUMENT ME!
     */
    void setBaumMerkmal(final Collection<BaumMerkmalCustomBean> baumMerkmal);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    BaumNutzungCustomBean getBaumNutzung();

    /**
     * DOCUMENT ME!
     *
     * @param  baumNutzung  DOCUMENT ME!
     */
    void setBaumNutzung(final BaumNutzungCustomBean baumNutzung);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getAlteNutzung();

    /**
     * DOCUMENT ME!
     *
     * @param  alte_nutzung  DOCUMENT ME!
     */
    void setAlteNutzung(final String alte_nutzung);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getBaumnummer();

    /**
     * DOCUMENT ME!
     *
     * @param  baumnummer  DOCUMENT ME!
     */
    void setBaumnummer(final String baumnummer);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getErfassungsdatum();

    /**
     * DOCUMENT ME!
     *
     * @param  erfassungsdatum  DOCUMENT ME!
     */
    void setErfassungsdatum(final Date erfassungsdatum);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getFaelldatum();

    /**
     * DOCUMENT ME!
     *
     * @param  faelldatum  DOCUMENT ME!
     */
    void setFaelldatum(final Date faelldatum);

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
     * @return  DOCUMENT ME!
     */
    String getLage();

    /**
     * DOCUMENT ME!
     *
     * @param  lage  DOCUMENT ME!
     */
    void setLage(final String lage);

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
    Boolean isModifiable();

    /**
     * DOCUMENT ME!
     *
     * @param  modifiable  DOCUMENT ME!
     */
    void setModifiable(final Boolean modifiable);
}
