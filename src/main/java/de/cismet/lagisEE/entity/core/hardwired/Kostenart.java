/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagisEE.entity.core.hardwired;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface Kostenart {

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
    boolean getIstNebenkostenart();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setIstNebenkostenart(final boolean val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isNebenkostenart();
}
