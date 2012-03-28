/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagisEE.entity.history;

import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckAktionCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckCustomBean;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface FlurstueckHistorie {

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
    FlurstueckCustomBean getNachfolger();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setNachfolger(final FlurstueckCustomBean val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    FlurstueckCustomBean getVorgaenger();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setVorgaenger(final FlurstueckCustomBean val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    FlurstueckAktionCustomBean getAktion();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setAktion(final FlurstueckAktionCustomBean val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Integer getIndex();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setIndex(final Integer val);
}
