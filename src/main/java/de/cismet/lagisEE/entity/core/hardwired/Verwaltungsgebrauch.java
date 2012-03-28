/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagisEE.entity.core.hardwired;

import java.util.Collection;

import de.cismet.cids.custom.beans.verdis_grundis.FarbeCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.KategorieCustomBean;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface Verwaltungsgebrauch {

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
    String getBezeichnung();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setBezeichnung(final String val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<FarbeCustomBean> getFarben();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setFarben(final Collection<FarbeCustomBean> val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getAbkuerzung();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setAbkuerzung(final String val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getUnterAbschnitt();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setUnterAbschnitt(final String val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    KategorieCustomBean getKategorie();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setKategorie(final KategorieCustomBean val);
}
