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
package de.cismet.lagisEE.entity.extension.baum;

import java.io.Serializable;

import java.util.Collection;

import de.cismet.cids.custom.beans.lagis.BaumKategorieAuspraegungCustomBean;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface BaumKategorie extends Serializable {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Long getId();

    /**
     * DOCUMENT ME!
     *
     * @param  id  DOCUMENT ME!
     */
    void setId(final Long id);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<BaumKategorieAuspraegungCustomBean> getKategorieAuspraegungen();

    /**
     * DOCUMENT ME!
     *
     * @param  kategorieAuspraegungen  DOCUMENT ME!
     */
    void setKategorieAuspraegungen(final Collection<BaumKategorieAuspraegungCustomBean> kategorieAuspraegungen);

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
