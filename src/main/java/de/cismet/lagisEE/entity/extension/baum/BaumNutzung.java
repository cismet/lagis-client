/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagisEE.entity.extension.baum;

import java.io.Serializable;

import de.cismet.cids.custom.beans.verdis_grundis.BaumKategorieAuspraegungCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.BaumKategorieCustomBean;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface BaumNutzung extends Serializable {

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
    BaumKategorieAuspraegungCustomBean getAusgewaehlteAuspraegung();

    /**
     * DOCUMENT ME!
     *
     * @param  ausgewaehlteAuspraegung  DOCUMENT ME!
     */
    void setAusgewaehlteAuspraegung(final BaumKategorieAuspraegungCustomBean ausgewaehlteAuspraegung);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    BaumKategorieCustomBean getBaumKategorie();

    /**
     * DOCUMENT ME!
     *
     * @param  MiPaKategorie  DOCUMENT ME!
     */
    void setBaumKategorie(final BaumKategorieCustomBean MiPaKategorie);
}
