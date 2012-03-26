/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.EJBrokerInterfaces;

import java.util.Collection;
import java.util.Date;

import de.cismet.cids.custom.beans.verdis_grundis.BeschlussCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.KostenCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.VertragsartCustomBean;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface Vertrag {

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
    String getAktenzeichen();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setAktenzeichen(final String val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getVertragspartner();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setVertragspartner(final String val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<BeschlussCustomBean> getBeschluesse();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setBeschluesse(final Collection<BeschlussCustomBean> val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getDatumAuflassung();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setDatumAuflassung(final Date val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getDatumEintragung();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setDatumEintragung(final Date val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getBemerkung();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setBemerkung(final String val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<KostenCustomBean> getKosten();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setKosten(final Collection<KostenCustomBean> val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    VertragsartCustomBean getVertragsart();

    /**
     * DOCUMENT ME!
     *
     * @param  vertragsart  DOCUMENT ME!
     */
    void setVertragsart(final VertragsartCustomBean vertragsart);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Double getQuadratmeterpreis();

    /**
     * DOCUMENT ME!
     *
     * @param  quadratmeterpreis  DOCUMENT ME!
     */
    void setQuadratmeterpreis(final Double quadratmeterpreis);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Double getGesamtpreis();

    /**
     * DOCUMENT ME!
     *
     * @param  gesamtpreis  DOCUMENT ME!
     */
    void setGesamtpreis(final Double gesamtpreis);
}
