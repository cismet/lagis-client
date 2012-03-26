/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.EJBrokerInterfaces;

import java.util.Collection;

import de.cismet.cids.custom.beans.verdis_grundis.*;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface Flurstueck {

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
    FlurstueckSchluesselCustomBean getFlurstueckSchluessel();

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueckSchluessel  DOCUMENT ME!
     */
    void setFlurstueckSchluessel(final FlurstueckSchluesselCustomBean flurstueckSchluessel);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getBemerkung();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<MipaCustomBean> getMiPas();

    /**
     * DOCUMENT ME!
     *
     * @param  miPas  DOCUMENT ME!
     */
    void setMiPas(final Collection<MipaCustomBean> miPas);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<BaumCustomBean> getBaeume();

    /**
     * DOCUMENT ME!
     *
     * @param  baeume  DOCUMENT ME!
     */
    void setBaeume(final Collection<BaumCustomBean> baeume);

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
    Collection<VerwaltungsbereichCustomBean> getVerwaltungsbereiche();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setVerwaltungsbereiche(final Collection<VerwaltungsbereichCustomBean> val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<DmsUrlCustomBean> getDokumente();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setDokumente(final Collection<DmsUrlCustomBean> val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<NutzungCustomBean> getNutzungen();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setNutzungen(final Collection<NutzungCustomBean> val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<RebeCustomBean> getRechteUndBelastungen();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setRechteUndBelastungen(final Collection<RebeCustomBean> val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<VertragCustomBean> getVertraege();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setVertraege(final Collection<VertragCustomBean> val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<FlurstueckSchluesselCustomBean> getVertraegeQuerverweise();

    /**
     * DOCUMENT ME!
     *
     * @param  querverweise  DOCUMENT ME!
     */
    void setVertraegeQuerverweise(final Collection<FlurstueckSchluesselCustomBean> querverweise);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<FlurstueckSchluesselCustomBean> getMiPasQuerverweise();

    /**
     * DOCUMENT ME!
     *
     * @param  miPasQuerverweise  DOCUMENT ME!
     */
    void setMiPasQuerverweise(final Collection<FlurstueckSchluesselCustomBean> miPasQuerverweise);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<FlurstueckSchluesselCustomBean> getBaeumeQuerverweise();

    /**
     * DOCUMENT ME!
     *
     * @param  baeumeQuerverweise  DOCUMENT ME!
     */
    void setBaeumeQuerverweise(final Collection<FlurstueckSchluesselCustomBean> baeumeQuerverweise);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    SpielplatzCustomBean getSpielplatz();

    /**
     * DOCUMENT ME!
     *
     * @param  spielplatz  DOCUMENT ME!
     */
    void setSpielplatz(final SpielplatzCustomBean spielplatz);
}
