/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * AnlagenklasseSumme.java
 *
 * Created on 24. April 2007, 13:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.utillity;

import org.apache.log4j.Logger;

import de.cismet.lagisEE.entity.core.hardwired.Anlageklasse;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class AnlagenklasseSumme implements Comparable {

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    private Anlageklasse anlageklasse;
    private double summe;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of AnlagenklasseSumme.
     *
     * @param  anlageklasse  DOCUMENT ME!
     */
    public AnlagenklasseSumme(final Anlageklasse anlageklasse) {
        this.anlageklasse = anlageklasse;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getSumme() {
        return summe;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  summe  DOCUMENT ME!
     */
    public void setSumme(final double summe) {
        this.summe = summe;
    }

    @Override
    public boolean equals(final Object obj) {
        if (log.isDebugEnabled()) {
            log.debug("equals called");
        }
        if (obj instanceof AnlagenklasseSumme) {
            log.info("ist Anlagensummenklasse");
            final AnlagenklasseSumme other = (AnlagenklasseSumme)obj;
            if ((anlageklasse != null)
                        && (other.getAnlageklasse() != null)) {
                log.info("1 auswertung");
                log.info("1 ausertung: "
                            + ((other.getAnlageklasse().getId().equals(anlageklasse.getId()))
                                && (summe == other.getSumme())));
                return (other.getAnlageklasse().getId().equals(anlageklasse.getId())) && (summe == other.getSumme());
            } else if ((anlageklasse == null) && (other.getAnlageklasse() == null)) {
                log.info("2 auswertung");
                log.info("2 ausertung: " + (summe == other.getSumme()));
                return summe == other.getSumme();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("3 auswertung");
                }
                return false;
            }
        } else if (obj instanceof Anlageklasse) {
            log.info("ist Anlageklasse");
            final Anlageklasse other = (Anlageklasse)obj;
            if ((anlageklasse != null) && (other != null)) {
                log.info("1 auswertung");
                log.info("1 auswertung" + (anlageklasse.getId().equals(other.getId())));
                return anlageklasse.getId().equals(other.getId());
            } else if ((anlageklasse == null) && (other == null)) {
                log.info("2 auswertung");
                return true;
            } else {
                log.info("3 auswertung");
                return false;
            }
        } else {
            log.warn("ist gar nix");
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Anlageklasse getAnlageklasse() {
        return anlageklasse;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  anlageklasse  DOCUMENT ME!
     */
    public void setAnlageklasse(final Anlageklasse anlageklasse) {
        this.anlageklasse = anlageklasse;
    }

    @Override
    public int compareTo(final Object o) {
        if (o instanceof AnlagenklasseSumme) {
            final AnlagenklasseSumme as = (AnlagenklasseSumme)o;
            return anlageklasse.getSchluessel().compareTo(as.getAnlageklasse().getSchluessel());
        } else {
            return anlageklasse.getSchluessel().compareTo(o.toString());
        }
    }
}
