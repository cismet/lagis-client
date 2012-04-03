/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagisEE.entity.core.hardwired;

import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckArtCustomBean;

import de.cismet.lagisEE.interfaces.Equalator;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface FlurstueckArt {

    //~ Instance fields --------------------------------------------------------

    Equalator<FlurstueckArtCustomBean> FLURSTUECK_ART_EQUALATOR = new Equalator<FlurstueckArtCustomBean>() {

            @Override
            public boolean pedanticEquals(final FlurstueckArtCustomBean f1, final FlurstueckArtCustomBean f2) {
                System.out.println("FlurstueckArt pedanticEquals(): aufgerufen");
                if ((((f1.getBezeichnung() != null) && (f2.getBezeichnung() != null)
                                    && f1.getBezeichnung().equals(f2.getBezeichnung()))
                                || ((f1.getBezeichnung() == null) && (f2.getBezeichnung() == null)))) {
                    System.out.println("FlurstueckArt pedanticEquals(): Flurstücksarten sind gleich");
                    return true;
                }
                System.out.println("FlurstueckArt pedanticEquals(): Flurstücksarten sind nicht gleich");
                return false;
            }
        };

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
