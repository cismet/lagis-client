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

import de.cismet.cids.custom.beans.verdis_grundis.MipaKategorieAuspraegungCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.MipaKategorieCustomBean;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface MiPaNutzung extends Serializable {

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
    MipaKategorieAuspraegungCustomBean getAusgewaehlteAuspraegung();

    /**
     * DOCUMENT ME!
     *
     * @param  ausgewaehlteAuspraegung  DOCUMENT ME!
     */
    void setAusgewaehlteAuspraegung(final MipaKategorieAuspraegungCustomBean ausgewaehlteAuspraegung);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    MipaKategorieCustomBean getMiPaKategorie();

    /**
     * DOCUMENT ME!
     *
     * @param  MiPaKategorie  DOCUMENT ME!
     */
    void setMiPaKategorie(final MipaKategorieCustomBean MiPaKategorie);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Integer getAusgewaehlteNummer();

    /**
     * DOCUMENT ME!
     *
     * @param  ausgewaehlteNummer  DOCUMENT ME!
     */
    void setAusgewaehlteNummer(final Integer ausgewaehlteNummer);
}
