/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagisEE.entity.core;

import java.util.Collection;

import de.cismet.cids.custom.beans.lagis.BaumCustomBean;
import de.cismet.cids.custom.beans.lagis.DmsUrlCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;
import de.cismet.cids.custom.beans.lagis.MipaCustomBean;
import de.cismet.cids.custom.beans.lagis.NutzungCustomBean;
import de.cismet.cids.custom.beans.lagis.SpielplatzCustomBean;
import de.cismet.cids.custom.beans.lagis.VertragCustomBean;
import de.cismet.cids.custom.beans.lagis.VerwaltungsbereichCustomBean;

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

//    /**
//     * DOCUMENT ME!
//     *
//     * @return  DOCUMENT ME!
//     */
//    Collection<FlurstueckSchluesselCustomBean> getMiPasQuerverweise();
//
//    /**
//     * DOCUMENT ME!
//     *
//     * @param  miPasQuerverweise  DOCUMENT ME!
//     */
//    void setMiPasQuerverweise(final Collection<FlurstueckSchluesselCustomBean> miPasQuerverweise);

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
