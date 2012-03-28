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

import java.io.Serializable;

import java.util.Collection;

import de.cismet.cids.custom.beans.verdis_grundis.MipaKategorieAuspraegungCustomBean;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface MiPaKategorie extends Serializable {

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
    Boolean getHatNummerAlsAuspraegung();

    /**
     * DOCUMENT ME!
     *
     * @param  hatNummerAlsAuspraegung  DOCUMENT ME!
     */
    void setHatNummerAlsAuspraegung(final Boolean hatNummerAlsAuspraegung);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<MipaKategorieAuspraegungCustomBean> getKategorieAuspraegungen();

    /**
     * DOCUMENT ME!
     *
     * @param  kategorieAuspraegungen  DOCUMENT ME!
     */
    void setKategorieAuspraegungen(final Collection<MipaKategorieAuspraegungCustomBean> kategorieAuspraegungen);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getBezeichnung();

    /**
     * DOCUMENT ME!
     *
     * @param  bezeichnung  DOCUMENT ME!
     */
    void setBezeichnung(final String bezeichnung);
}
