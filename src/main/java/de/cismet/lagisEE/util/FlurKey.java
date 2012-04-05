/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * FlurstueckFlur.java
 *
 * Created on 19. April 2007, 09:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagisEE.util;

import java.io.Serializable;

import de.cismet.cids.custom.beans.lagis.GemarkungCustomBean;

import de.cismet.lagisEE.interfaces.Key;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class FlurKey implements Key, Serializable, Comparable {

    //~ Instance fields --------------------------------------------------------

    private GemarkungCustomBean gemarkung;
    private boolean historicFilterEnabled = false;
    private boolean currentFilterEnabled = false;
    private boolean abteilungXIFilterEnabled = false;
    private boolean staedtischFilterEnabled = false;
    private Integer gemarkungsId;
    private Integer flurId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of FlurstueckFlur.
     *
     * @param  gemarkung  DOCUMENT ME!
     * @param  flurId     DOCUMENT ME!
     */
    public FlurKey(final GemarkungCustomBean gemarkung, final Integer flurId) {
        this.gemarkung = gemarkung;
        gemarkungsId = gemarkung.getSchluessel();
        this.flurId = flurId;
    }

    /**
     * Creates a new instance of FlurstueckFlur.
     *
     * @param  gemarkung  DOCUMENT ME!
     * @param  flurId     DOCUMENT ME!
     */
    // Ugly Winning
    public FlurKey(final Integer gemarkung, final Integer flurId) {
        gemarkungsId = gemarkung;
        this.flurId = flurId;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public GemarkungCustomBean getGemarkung() {
        return gemarkung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gemarkung  DOCUMENT ME!
     */
    public void setGemarkung(final GemarkungCustomBean gemarkung) {
        this.gemarkung = gemarkung;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getGemarkungsId() {
        return gemarkungsId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gemarkungsId  DOCUMENT ME!
     */
    public void setGemarkungsId(final Integer gemarkungsId) {
        this.gemarkungsId = gemarkungsId;
    }
    /**
     * TODO bad name --> better Flurschluessel.
     *
     * @return  DOCUMENT ME!
     */
    public Integer getFlurId() {
        return flurId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurId  DOCUMENT ME!
     */
    public void setFlurId(final Integer flurId) {
        this.flurId = flurId;
    }

//    public String getPrintableKey() {
//        if(flurId != null){
//            return flurId.toString();
//        } else {
//            return null;
//        }
//    }

    @Override
    public String toString() {
        return flurId.toString();
    }

    @Override
    public int compareTo(final Object value) {
        if (value instanceof FlurKey) {
            final FlurKey other = (FlurKey)value;
            if ((other != null) && (other.toString() != null) && (toString() != null)) {
                try {
                    return (new Integer(toString())).compareTo(new Integer(other.toString()));
                } catch (Exception ex) {
                    return toString().compareTo(other.toString());
                }
            } else if (toString() == null) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(final Object obj) {
        try {
            if ((obj != null) && (obj instanceof FlurKey)) {
                final FlurKey other = (FlurKey)obj;
                if (((gemarkungsId != null) && (other.getGemarkungsId() != null)
                                && gemarkungsId.equals(other.getGemarkungsId()))
                            || ((gemarkungsId == null) && (other.getGemarkungsId() == null))) {
                    System.out.println("Gemarkung stimmt überein");
                    if (((flurId != null) && (other.getFlurId() != null) && flurId.equals(other.getFlurId()))
                                || ((flurId == null) && (other.getFlurId() == null))) {
                        System.out.println("Alle Felder stimmen überein --> equals");
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception ex) {
            System.out.println("Fehler in equals Flurkey");
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isHistoricFilterEnabled() {
        return historicFilterEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  historicFilterEnabled  DOCUMENT ME!
     */
    public void setHistoricFilterEnabled(final boolean historicFilterEnabled) {
        this.historicFilterEnabled = historicFilterEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isCurrentFilterEnabled() {
        return currentFilterEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  currentFilterEnabled  DOCUMENT ME!
     */
    public void setCurrentFilterEnabled(final boolean currentFilterEnabled) {
        this.currentFilterEnabled = currentFilterEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isStaedtischFilterEnabled() {
        return staedtischFilterEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  staedtischFilterEnabled  DOCUMENT ME!
     */
    public void setStaedtischFilterEnabled(final boolean staedtischFilterEnabled) {
        this.staedtischFilterEnabled = staedtischFilterEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isAbteilungXIFilterEnabled() {
        return abteilungXIFilterEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  abteilungXIFilterEnabled  DOCUMENT ME!
     */
    public void setAbteilungXIFilterEnabled(final boolean abteilungXIFilterEnabled) {
        this.abteilungXIFilterEnabled = abteilungXIFilterEnabled;
    }
}
