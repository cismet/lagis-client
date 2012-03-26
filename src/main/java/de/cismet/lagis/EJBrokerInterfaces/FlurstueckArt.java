/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.EJBrokerInterfaces;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface FlurstueckArt {

    //~ Instance fields --------------------------------------------------------

    String FLURSTUECK_ART_BEZEICHNUNG_PSEUDO = "pseudo";
    String FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH = "städtisch";
    String FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX = "Abteilung IX";

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
}
