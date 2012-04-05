/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagisEE.entity.core;

import java.io.Serializable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.cismet.cids.custom.beans.lagis.NutzungBuchungCustomBean;

import de.cismet.lagis.Exception.AddingOfBuchungNotPossibleException;
import de.cismet.lagis.Exception.BuchungNotInNutzungException;
import de.cismet.lagis.Exception.IllegalNutzungStateException;
import de.cismet.lagis.Exception.TerminateNutzungNotPossibleException;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface Nutzung extends Serializable, Cloneable {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static enum NUTZUNG_STATES {

        //~ Enum constants -----------------------------------------------------

        NUTZUNG_CREATED, BUCHUNG_CREATED, NUTZUNG_CHANGED, NUTZUNG_TERMINATED, STILLE_RESERVE_CREATED,
        STILLE_RESERVE_EXISTING, STILLE_RESERVE_INCREASED, STILLE_RESERVE_DECREASED, STILLE_RESERVE_DISOLVED,
        NUTZUNGSART_CHANGED, POSITIVE_BUCHUNG, NEGATIVE_BUCHUNG
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static enum TIME_SCALE {

        //~ Enum constants -----------------------------------------------------

        DAY, MONTH, YEAR
    }

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
    NutzungBuchungCustomBean getOpenBuchung();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    NutzungBuchungCustomBean getPreviousBuchung();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalNutzungStateException  DOCUMENT ME!
     */
    NutzungBuchungCustomBean getBuchwert() throws IllegalNutzungStateException;

    /**
     * DOCUMENT ME!
     *
     * @param   buchung  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalNutzungStateException  DOCUMENT ME!
     * @throws  NullPointerException          DOCUMENT ME!
     * @throws  BuchungNotInNutzungException  DOCUMENT ME!
     */
    NutzungBuchungCustomBean getBuchwert(final NutzungBuchungCustomBean buchung) throws IllegalNutzungStateException,
        NullPointerException,
        BuchungNotInNutzungException;

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalNutzungStateException  DOCUMENT ME!
     */
    Double getBuchwertBetrag() throws IllegalNutzungStateException;

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalNutzungStateException  DOCUMENT ME!
     */
    Double getBuchwertDifference() throws IllegalNutzungStateException;

    /**
     * DOCUMENT ME!
     *
     * @param   buchung  DOCUMENT ME!
     *
     * @throws  AddingOfBuchungNotPossibleException  DOCUMENT ME!
     * @throws  IllegalNutzungStateException         DOCUMENT ME!
     */
    void addBuchung(final NutzungBuchungCustomBean buchung) throws AddingOfBuchungNotPossibleException,
        IllegalNutzungStateException;

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalNutzungStateException  DOCUMENT ME!
     */
    Double getStilleReserve() throws IllegalNutzungStateException;

    /**
     * DOCUMENT ME!
     *
     * @param   buchung  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalNutzungStateException  DOCUMENT ME!
     * @throws  BuchungNotInNutzungException  DOCUMENT ME!
     */
    Double getStilleReserveForBuchung(final NutzungBuchungCustomBean buchung) throws IllegalNutzungStateException,
        BuchungNotInNutzungException;

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Boolean hasNewBuchung();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Boolean mustBeTerminated();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    NutzungBuchungCustomBean getTerminalBuchung();

    /**
     * DOCUMENT ME!
     *
     * @param   bookingDate  DOCUMENT ME!
     *
     * @throws  TerminateNutzungNotPossibleException  DOCUMENT ME!
     */
    void terminateNutzung(final Date bookingDate) throws TerminateNutzungNotPossibleException;

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isTerminated();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Double getDifferenceToPreviousBuchung();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    int getBuchungsCount();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalNutzungStateException  DOCUMENT ME!
     */
    Collection<NUTZUNG_STATES> getNutzungsState() throws IllegalNutzungStateException;

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Boolean removeOpenNutzung();

    /**
     * DOCUMENT ME!
     *
     * @param   targetDay  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<NutzungBuchungCustomBean> getBuchungForDay(Date targetDay);

    /**
     * DOCUMENT ME!
     *
     * @param   targetDate  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    NutzungBuchungCustomBean getBuchungForDate(final Date targetDate);

    /**
     * DOCUMENT ME!
     *
     * @param   successor  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    NutzungBuchungCustomBean getPredecessorBuchung(final NutzungBuchungCustomBean successor);

    /**
     * DOCUMENT ME!
     *
     * @param   buchungToFlip  DOCUMENT ME!
     *
     * @throws  IllegalNutzungStateException  DOCUMENT ME!
     * @throws  BuchungNotInNutzungException  DOCUMENT ME!
     */
    void flipBuchungsBuchwertValue(final NutzungBuchungCustomBean buchungToFlip) throws IllegalNutzungStateException,
        BuchungNotInNutzungException;

    /**
     * DOCUMENT ME!
     *
     * @param   buchungToFlip  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isBuchungFlippable(final NutzungBuchungCustomBean buchungToFlip);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<NutzungBuchungCustomBean.NUTZUNG_BUCHUNG_FIELDS> getDifferenceBetweenLastBuchung();

    /**
     * DOCUMENT ME!
     *
     * @param   buchung  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    int getBuchungsNummerForBuchung(final NutzungBuchungCustomBean buchung);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    List<NutzungBuchungCustomBean> getNutzungsBuchungen();

    /**
     * DOCUMENT ME!
     *
     * @param  nutzungsBuchungen  DOCUMENT ME!
     */
    void setNutzungsBuchungen(final List<NutzungBuchungCustomBean> nutzungsBuchungen);
}
