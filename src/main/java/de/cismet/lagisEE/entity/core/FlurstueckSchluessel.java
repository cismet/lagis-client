/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * FlurstueckSchluessel.java
 *
 * Created on 18. April 2007, 14:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagisEE.entity.core;

import java.io.Serializable;

import java.util.Date;

import de.cismet.cids.custom.beans.lagis.FlurstueckArtCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;
import de.cismet.cids.custom.beans.lagis.GemarkungCustomBean;

import de.cismet.lagisEE.interfaces.Equalator;
import de.cismet.lagisEE.interfaces.Key;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface FlurstueckSchluessel extends Key, Serializable, Comparable {

    //~ Instance fields --------------------------------------------------------

    Equalator<FlurstueckSchluesselCustomBean> FLURSTUECK_EQUALATOR = new Equalator<FlurstueckSchluesselCustomBean>() {

            @Override
            public boolean pedanticEquals(final FlurstueckSchluesselCustomBean f1,
                    final FlurstueckSchluesselCustomBean f2) {
                System.out.println("FlurstueckSchluessel pedanticEquals(): aufgerufen");
                if ((((f1.getGemarkung() != null) && (f2.getGemarkung() != null)
                                    && f1.getGemarkung().equals(f2.getGemarkung()))
                                || ((f1.getGemarkung() == null) && (f2.getGemarkung() == null)))
                            && (((f1.getFlur() != null) && (f2.getFlur() != null) && f1.getFlur().equals(f2.getFlur()))
                                || ((f1.getFlur() == null) && (f2.getFlur() == null)))
                            && (((f1.getFlurstueckZaehler() != null) && (f2.getFlurstueckZaehler() != null)
                                    && f1.getFlurstueckZaehler().equals(f2.getFlurstueckZaehler()))
                                || ((f1.getFlurstueckZaehler() == null) && (f2.getFlurstueckZaehler() == null)))
                            && (((f1.getFlurstueckNenner() != null) && (f2.getFlurstueckNenner() != null)
                                    && f1.getFlurstueckNenner().equals(f2.getFlurstueckNenner()))
                                || ((f1.getFlurstueckNenner() == null) && (f2.getFlurstueckNenner() == null)))) {
                    System.out.println("FlurstueckSchluessel pedanticEquals(): Alle Felder sind gleich --> equals");
                    return true;
                } else {
                    System.out.println("FlurstueckSchluessel pedanticEquals(): FlurstueckSchluessel sind nicht gleich");
                    return false;
                }
            }
        };

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
    GemarkungCustomBean getGemarkung();

    /**
     * DOCUMENT ME!
     *
     * @param  gemarkung  DOCUMENT ME!
     */
    void setGemarkung(final GemarkungCustomBean gemarkung);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Integer getFlur();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setFlur(final Integer val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Integer getFlurstueckZaehler();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setFlurstueckZaehler(final Integer val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Integer getFlurstueckNenner();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setFlurstueckNenner(final Integer val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getKeyString();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Boolean isEchterSchluessel();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Boolean getIstGesperrt();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setIstGesperrt(final Boolean val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isGesperrt();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getBemerkungSperre();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setBemerkungSperre(final String val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getEntstehungsDatum();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setEntstehungsDatum(final Date val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getGueltigBis();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setGueltigBis(final Date val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    FlurstueckArtCustomBean getFlurstueckArt();

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueckArt  DOCUMENT ME!
     */
    void setFlurstueckArt(final FlurstueckArtCustomBean flurstueckArt);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Boolean getWarStaedtisch();

    /**
     * DOCUMENT ME!
     *
     * @param  warStaedtisch  DOCUMENT ME!
     */
    void setWarStaedtisch(final Boolean warStaedtisch);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getDatumLetzterStadtbesitz();

    /**
     * DOCUMENT ME!
     *
     * @param  datumLetzterStadtbesitz  DOCUMENT ME!
     */
    void setDatumLetzterStadtbesitz(final Date datumLetzterStadtbesitz);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getLetzter_bearbeiter();

    /**
     * DOCUMENT ME!
     *
     * @param  letzter_bearbeiter  DOCUMENT ME!
     */
    void setLetzter_bearbeiter(final String letzter_bearbeiter);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getLetzte_bearbeitung();

    /**
     * DOCUMENT ME!
     *
     * @param  letzte_bearbeitung  DOCUMENT ME!
     */
    void setLetzte_bearbeitung(final Date letzte_bearbeitung);
}
