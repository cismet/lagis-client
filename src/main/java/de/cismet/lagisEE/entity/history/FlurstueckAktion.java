/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagisEE.entity.history;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface FlurstueckAktion {

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
    String getBeschreibung();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setBeschreibung(final String val);
}
