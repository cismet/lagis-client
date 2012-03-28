/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagisEE.entity.core;

import java.util.Date;

import de.cismet.cids.custom.beans.verdis_grundis.BeschlussartCustomBean;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface Beschluss {

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
    BeschlussartCustomBean getBeschlussart();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setBeschlussart(final BeschlussartCustomBean val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getDatum();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setDatum(final Date val);
}
