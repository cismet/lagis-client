/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.lagis;

import org.openide.util.Exceptions;

import java.util.*;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.Exception.AddingOfBuchungNotPossibleException;
import de.cismet.lagis.Exception.BuchungNotInNutzungException;
import de.cismet.lagis.Exception.IllegalNutzungStateException;
import de.cismet.lagis.Exception.TerminateNutzungNotPossibleException;

import de.cismet.lagis.broker.CidsBroker;

import de.cismet.lagis.util.SortedList;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.Nutzung;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class NutzungCustomBean extends BasicEntity implements Nutzung {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NutzungCustomBean.class);
    public static final String TABLE = "nutzung";

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private FlurstueckCustomBean fk_flurstueck;
    private Collection<NutzungBuchungCustomBean> n_buchungen;
    private String[] PROPERTY_NAMES = new String[] { "id", "fk_flurstueck", "n_buchungen" };

    private List<NutzungBuchungCustomBean> sortedBuchungen = new SortedList<NutzungBuchungCustomBean>(
            new Comparator<NutzungBuchungCustomBean>() {

                @Override
                public int compare(final NutzungBuchungCustomBean o1, final NutzungBuchungCustomBean o2) {
                    final long comp = o1.getGueltig_von().getTime() - o2.getGueltig_von().getTime();
                    if (comp < 0) {
                        return -1;
                    } else if (comp > 0) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NutzungCustomBean object.
     */
    public NutzungCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   addDummyBuchung  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static NutzungCustomBean createNew(final boolean addDummyBuchung) {
        try {
            final NutzungCustomBean nutzung = (NutzungCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    CidsBroker.LAGIS_DOMAIN,
                    TABLE);

            if (addDummyBuchung) {
                final NutzungBuchungCustomBean buchung = NutzungBuchungCustomBean.createNew();
                buchung.setIstBuchwert(true);
                nutzung.addBuchung(buchung);
            }

            return nutzung;
        } catch (Exception ex) {
            LOG.error("error creating " + TABLE + " bean", ex);
            return null;
        }
    }

    /**
     * This Constructor creates Nutzung with an inital Buchung which is a Buchwert. This should be no Problem for
     * Hibernate because it will simply overwrite the settings done by setting the available Nutzungsbuchungen. A
     * important constraint of the class is that it is not possible to have nutzungs object either with a null
     * nutzungsset nor without a inital buchung.
     *
     * @return  DOCUMENT ME!
     */
    public static NutzungCustomBean createNew() {
        return NutzungCustomBean.createNew(true);
    }

    /**
     * This Constructor creates a Nutzung with a given inital buchwert. If the argument is null a empty default buchwert
     * is created like in the default constructor. All metainformation fields of the NutzungsBuchung are overwritten, in
     * order to guarantee this object has a valid Nutzungsbuchung list. If the Buchung is already contained in another
     * Nutzung a clone of the Buchung will be created.
     *
     * @param   initialBuchwert  the inital buchwert of the nutzung (first buchung)
     *
     * @return  DOCUMENT ME!
     *
     * @throws  AddingOfBuchungNotPossibleException  DOCUMENT ME!
     * @throws  IllegalNutzungStateException         DOCUMENT ME!
     */
    public static NutzungCustomBean createNew(NutzungBuchungCustomBean initialBuchwert)
            throws AddingOfBuchungNotPossibleException, IllegalNutzungStateException {
        final NutzungCustomBean bean = createNew(false);
        if (initialBuchwert == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Initial Buchwert is null... creating buchung.");
            }
            try {
                initialBuchwert = (NutzungBuchungCustomBean)CidsBean.createNewCidsBeanFromTableName(
                        CidsBroker.LAGIS_DOMAIN,
                        "nutzung_buchung");
            } catch (Exception ex) {
                LOG.error("error creating nutzung_buchung bean", ex);
            }
        }
        initialBuchwert.setIstBuchwert(true);
        bean.addBuchung(initialBuchwert);
        return bean;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Integer getId() {
        return this.id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setId(final Integer val) {
        this.id = val;

        this.propertyChangeSupport.firePropertyChange("id", null, this.id);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckCustomBean getFk_flurstueck() {
        return this.fk_flurstueck;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_flurstueck(final FlurstueckCustomBean val) {
        this.fk_flurstueck = val;

        this.propertyChangeSupport.firePropertyChange("fk_flurstueck", null, this.fk_flurstueck);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<NutzungBuchungCustomBean> getN_buchungen() {
        return this.n_buchungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setN_buchungen(final Collection<NutzungBuchungCustomBean> val) {
        this.n_buchungen = val;

        this.propertyChangeSupport.firePropertyChange("n_buchungen", null, this.n_buchungen);

        for (final NutzungBuchungCustomBean buchung : val) {
            // NOTE: this Nutzung is not persisted in NutzungBuchungCustomBean since this would cause
            // infinite recursive persistence calls on server side (problem with 1-n relations)
            buchung.setNutzung(this);
        }

        sortedBuchungen.clear();
        sortedBuchungen.addAll(val);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public void addBuchung(final NutzungBuchungCustomBean val) throws AddingOfBuchungNotPossibleException,
        IllegalNutzungStateException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("addBuchung");
        }
        if (val == null) {
            return;
        }
        if (hasNewBuchung()) {
            throw new AddingOfBuchungNotPossibleException(
                "Hinzufügen einer neuen Buchung nicht möglich. Die bereits vorhanden Änderungen müssen zuerst gespeichert werden.");
        }
        if (val.getNutzung() != null) {
            throw new AddingOfBuchungNotPossibleException(
                "Buchung kann nicht hinzugefügt werden, die Buchung gehört schon zu einer Nutzung.");
        }
        if (val.getId() != -1) {
            throw new AddingOfBuchungNotPossibleException(
                "Buchung kann nicht hinzugefügt werden, die Buchung wurde schon einmal gespeichert");
        }
        val.setSollGeloeschtWerden(false);
        val.setGueltigbis(null);
        final Date bookingDate = new Date();
        val.setGueltigvon(bookingDate);
        if (getBuchungsCount() == 0) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Nutzung vorhanden");
            }
            val.setIstBuchwert(true);
            val.setNutzung(this);

            this.n_buchungen.add(val);
            getNutzungsBuchungen().add(val);

            return;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Checke Buchwert");
            }
            getBuchwert();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Ende getBuchwert");
        }
        if (isTerminated()) {
            throw new AddingOfBuchungNotPossibleException(
                "Die Nutzung ist Terminiert, hinzufügen neuer Nutzungen nicht möglich");
        }
        final NutzungBuchungCustomBean lastBuchung = getOpenBuchung();
        lastBuchung.setGueltigbis(bookingDate);
        val.setNutzung(this);
        this.n_buchungen.add(val);
        getNutzungsBuchungen().add(val);
    }

    @Override
    public void flipBuchungsBuchwertValue(final NutzungBuchungCustomBean val) throws IllegalNutzungStateException,
        BuchungNotInNutzungException {
        if (val == null) {
            return;
        }
        if (!isBuchungInNutzung(val)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Buchung gehört nicht zu dieser Nutzung");
            }
            throw new BuchungNotInNutzungException();
        }
        if (getBuchungsCount() < 1) {
            throw new IllegalNutzungStateException("Keine Buchungen vorhanden.");
        }
        if (getNutzungsBuchungen().indexOf(val) == 0) {
            throw new IllegalNutzungStateException("Erste Buchung muss immer Buchwert sein");
        }
        if (val.getIstBuchwert() == true) {
            // check if another buchwert exists
            final NutzungBuchungCustomBean otherBuchwert = null;
            // if there is only one buchung and this buchung is true, flipping is not possible
            if (getBuchungsCount() == 1) {
                throw new IllegalNutzungStateException("Letzer Buchwert, kann nicht geflipped werden.");
            } else {
                for (final NutzungBuchungCustomBean currentBuchung : getNutzungsBuchungen()) {
                    if (!currentBuchung.equals(val) && currentBuchung.getIstBuchwert()) {
                        // Another Buchwert does exist this buchung can be flipped.
                        val.setIstBuchwert(false);
                        return;
                    }
                }
                throw new IllegalNutzungStateException("Letzer Buchwert, kann nicht geflipped werden.");
            }
        } else {
            val.setIstBuchwert(true);
        }
    }

    @Override
    public NutzungBuchungCustomBean getBuchungForDate(final Date val) {
        if (getBuchungsCount() > 0) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Nutzungen vorhanden");
            }
            if (val == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ziel Datum ist null --> nur aktuelle Nutzungen");
                }
                return getOpenBuchung();
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Zieldatum vorhanden Nutzung wird gesucht");
                }
                NutzungBuchungCustomBean foundNutzung = null;
                for (final NutzungBuchungCustomBean curNutzung : getNutzungsBuchungen()) {
                    if ((curNutzung.getGueltigvon() != null) && (curNutzung.getGueltigvon().compareTo(val) <= 0)
                                && (((curNutzung.getGueltigbis() == null)
                                        && ((new Date()).compareTo(val) >= 0))
                                    || ((curNutzung.getGueltigbis() != null)
                                        && (curNutzung.getGueltigbis().compareTo(val) >= 0)))) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Passende Nutzung mit gewünschtem Zeitbereich gefunden");
                        }
                        foundNutzung = curNutzung;
                    }
                }
                if (foundNutzung == null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Keine passende Nutzung gefunden");
                    }
                }
                return foundNutzung;
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Nutzungen vorhanden");
            }
            return null;
        }
    }

    @Override
    public Collection<NutzungBuchungCustomBean> getBuchungForDay(final Date val) {
        Date date = val;
        final Set<NutzungBuchungCustomBean> result = new HashSet<NutzungBuchungCustomBean>();
        if (getBuchungsCount() > 0) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Nutzungen vorhanden");
            }
            if (date == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ziel Datum ist null --> nur aktuelle Nutzungen");
                }
                final NutzungBuchungCustomBean openBuchung = getOpenBuchung();
                if (openBuchung != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Offene Buchung hinzugefügt");
                    }
                    result.add(openBuchung);
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Keine Offene Buchung vorhanden");
                    }
                }
            } else {
                date = getDateWithoutTime(date);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Zieldatum vorhanden Nutzung wird gesucht");
                }
                for (final NutzungBuchungCustomBean curBuchung : getNutzungsBuchungen()) {
                    final Date gueltigVon = getDateWithoutTime(curBuchung.getGueltigvon());
                    Date gueltigBis = getDateWithoutTime(curBuchung.getGueltigbis());
                    if (gueltigBis == null) {
                        gueltigBis = getDateWithoutTime(new Date());
                    }
                    // ToDo better use before() after()
                    if ((gueltigVon != null) && (gueltigVon.compareTo(date) <= 0)
                                && (((gueltigBis == null) && ((new Date()).compareTo(date) >= 0))
                                    || ((gueltigBis != null)
                                        && (gueltigBis.compareTo(date) >= 0)))) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Passende Nutzung mit gewünschtem Zeitbereich gefunden");
                        }
                        result.add(curBuchung);
                    }
                }
                if (result.isEmpty()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Keine passende Nutzung gefunden");
                    }
                }
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Nutzungen vorhanden");
            }
        }
        return result;
    }

    @Override
    public int getBuchungsCount() {
        if (getNutzungsBuchungen() != null) {
            return getNutzungsBuchungen().size();
        } else {
            return 0;
        }
    }

    @Override
    public int getBuchungsNummerForBuchung(final NutzungBuchungCustomBean val) {
        if ((val != null) && (getBuchungsCount() > 0) && (val.getNutzung() != null)
                    && val.getNutzung().equals(this)) {
            return getNutzungsBuchungen().indexOf(val) + 1;
        }
        return -1;
    }

    @Override
    public NutzungBuchungCustomBean getBuchwert() throws IllegalNutzungStateException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getBuchwert()");
        }
        if (getBuchungsCount() > 0) {
            try {
                return getBuchwert(getNutzungsBuchungen().get(getNutzungsBuchungen().size() - 1));
            } catch (Exception silent) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("error getting Buchwert", silent);
                }
            }
        }
        throw new IllegalNutzungStateException("Kein Buchwert");
    }

    @Override
    public NutzungBuchungCustomBean getBuchwert(final NutzungBuchungCustomBean val) throws IllegalNutzungStateException,
        NullPointerException,
        BuchungNotInNutzungException {
        if (val == null) {
            throw new NullPointerException();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("getBuchwert(NutzungsBuchung)");
        }
        if (getBuchungsCount() > 0) {
            if (!isBuchungInNutzung(val)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Buchung gehört nicht zu Nutzung");
                }
                throw new BuchungNotInNutzungException();
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Nutzungen vorhanden");
            }
            int index = -1;
            if ((getNutzungsBuchungen() != null) && ((index = getNutzungsBuchungen().indexOf(val)) != -1)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Startindex ist: " + index);
                }
                final ListIterator<NutzungBuchungCustomBean> buchungsItr = getNutzungsBuchungen().listIterator(index);
                final NutzungBuchungCustomBean lastListValue = buchungsItr.next();
                if (lastListValue.getIstBuchwert()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Letzter Eintrag ist Buchwert");
                    }
                    return lastListValue;
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Letzte Buchung ist nicht buchwert");
                }
                NutzungBuchungCustomBean lastBuchwert = null;
                while (buchungsItr.hasPrevious()) {
                    final NutzungBuchungCustomBean curBuchwert = buchungsItr.previous();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Prüfe Nutzung: " + curBuchwert.getId());
                    }
                    if (curBuchwert.getIstBuchwert()) {
                        lastBuchwert = curBuchwert;
                        break;
                    }
                }
                if (lastBuchwert != null) {
                    return lastBuchwert;
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Kein Buchwert gefunden");
                }
            }
        }
        throw new IllegalNutzungStateException("Kein Buchwert");
    }

    @Override
    public Double getBuchwertBetrag() throws IllegalNutzungStateException {
        final NutzungBuchungCustomBean buchwert = getBuchwert();
        if ((buchwert != null) && (buchwert.getGesamtpreis() != null)) {
            return buchwert.getGesamtpreis();
        } else {
            return null;
        }
    }

    @Override
    public Double getBuchwertDifference() throws IllegalNutzungStateException {
        if (getBuchungsCount() == 1) {
            if (getBuchwert() != null) {
                return 0.0;
            }
        } else if (getBuchungsCount() > 1) {
            final Double bookValue = getBuchwertBetrag();
            final Double currentValue = getNutzungsBuchungen().get(getNutzungsBuchungen().size() - 1).getGesamtpreis();
            if ((bookValue != null) & (currentValue != null)) {
                return currentValue - bookValue;
            }
        }
        return null;
    }

    @Override
    public Collection<NutzungBuchungCustomBean.NUTZUNG_BUCHUNG_FIELDS> getDifferenceBetweenLastBuchung() {
        if (getBuchungsCount() > 1) {
            final NutzungBuchungCustomBean lastBuchung = getNutzungsBuchungen().get(getNutzungsBuchungen().size() - 1);
            final NutzungBuchungCustomBean previousBuchung = getNutzungsBuchungen().get(getNutzungsBuchungen().size()
                            - 2);
            return NutzungBuchungCustomBean.NUTZUNG_HISTORY_EQUALATOR.determineUnequalFields(
                    lastBuchung,
                    previousBuchung);
        } else {
            return new HashSet<NutzungBuchungCustomBean.NUTZUNG_BUCHUNG_FIELDS>();
        }
    }

    @Override
    public Double getDifferenceToPreviousBuchung() {
        if (getBuchungsCount() > 1) {
            final NutzungBuchungCustomBean currentBuchung = getNutzungsBuchungen().get(getNutzungsBuchungen().size()
                            - 1);
            final NutzungBuchungCustomBean previousBuchung = getNutzungsBuchungen().get(getNutzungsBuchungen().size()
                            - 2);
            if ((previousBuchung != null) && (currentBuchung != null) && (previousBuchung.getGesamtpreis() != null)
                        && (currentBuchung.getGesamtpreis() != null)) {
                return currentBuchung.getGesamtpreis() - previousBuchung.getGesamtpreis();
            }
        }
        return null;
    }

    @Override
    public List<NutzungBuchungCustomBean> getNutzungsBuchungen() {
        return sortedBuchungen;
    }

    @Override
    public Collection<NUTZUNG_STATES> getNutzungsState() throws IllegalNutzungStateException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Bestimme Status der Nutzungskette");
        }
        final Set<NUTZUNG_STATES> nutzungStates = new HashSet<NUTZUNG_STATES>();
        if ((getId() == null) || (getId() == -1)) {
            nutzungStates.add(NUTZUNG_STATES.NUTZUNG_CREATED);
        }
        if (getBuchungsCount() > 0) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Es sind Buchungen vorhanden");
            }
            if (mustBeTerminated()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Nutzung soll terminiert werden");
                }
                nutzungStates.add(NUTZUNG_STATES.NUTZUNG_TERMINATED);
            }
            if (getBuchungsCount() == 1) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es ist genau eine Buchung vorhanden");
                }

                final Integer id = getNutzungsBuchungen().get(0).getId();
                if ((id == null) || (id == -1)) {
                    nutzungStates.add(NUTZUNG_STATES.BUCHUNG_CREATED);
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Mehr als eine Buchung vorhanden");
                }
                if (hasNewBuchung()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Neue Buchung vorhanden.");
                        LOG.debug("Prüfe Feldänderungen...");
                    }
                    final Collection<NutzungBuchungCustomBean.NUTZUNG_BUCHUNG_FIELDS> changeSet =
                        getDifferenceBetweenLastBuchung();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Neue Buchungen vorhanden");
                    }
                    nutzungStates.add(NUTZUNG_STATES.BUCHUNG_CREATED);
                    if (changeSet.size() == 0) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Keine Benutzer Änderungen");
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Nutzung Changed");
                        }
                        nutzungStates.add(NUTZUNG_STATES.NUTZUNG_CHANGED);
                        if (changeSet.contains(NutzungBuchungCustomBean.NUTZUNG_BUCHUNG_FIELDS.NUTZUNGSART)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Nutzungsart changed");
                            }
                            nutzungStates.add(NUTZUNG_STATES.NUTZUNGSART_CHANGED);
                        }
                        final int distanceBWToLastBuchung = getDistanceFromLastBuchungToBuchwert();
                        if (distanceBWToLastBuchung != -1) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Prüfe Stille Reserve...");
                            }
                            if ((changeSet.contains(NutzungBuchungCustomBean.NUTZUNG_BUCHUNG_FIELDS.FLAECHE)
                                            || changeSet.contains(
                                                NutzungBuchungCustomBean.NUTZUNG_BUCHUNG_FIELDS.QUADRADMETERPREIS))) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Fläche oder Preis hat sich geändert");
                                }
                                if (distanceBWToLastBuchung == 0) {
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("Sollte nicht vorkommen Fall schon abgefangen");
                                    }
                                } else if (distanceBWToLastBuchung == 1) {
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("Die Buchung folgt direkt auf den Buchwert");
                                    }
                                    final Double deltaLastBuchung = getDifferenceToPreviousBuchung();
                                    if (deltaLastBuchung != null) {
                                        if (deltaLastBuchung > 0) {
                                            if (LOG.isDebugEnabled()) {
                                                LOG.debug("Stille Reserve gebildet");
                                            }
                                            nutzungStates.add(NUTZUNG_STATES.STILLE_RESERVE_CREATED);
                                            nutzungStates.add(NUTZUNG_STATES.POSITIVE_BUCHUNG);
                                        } else {
                                            if (LOG.isDebugEnabled()) {
                                                LOG.debug("Negative Buchung keine Stille Reserve");
                                            }
                                            nutzungStates.add(NUTZUNG_STATES.NEGATIVE_BUCHUNG);
                                        }
                                    }
                                } else {
                                    final Double deltaBuchwert = getBuchwertDifference();
                                    final Double deltaLastBuchung = getDifferenceToPreviousBuchung();
                                    if (deltaLastBuchung != null) {
                                        if (deltaLastBuchung > 0) {
                                            nutzungStates.add(NUTZUNG_STATES.POSITIVE_BUCHUNG);
                                        } else {
                                            nutzungStates.add(NUTZUNG_STATES.NEGATIVE_BUCHUNG);
                                        }
                                    }
                                    if (deltaBuchwert != null) {
                                        if (LOG.isDebugEnabled()) {
                                            // ToDo Muss ausgiebig getestet werden
                                            LOG.debug("delta Buchwert: " + deltaBuchwert);
                                        }
                                        if (deltaBuchwert > 0.0) {
                                            if (LOG.isDebugEnabled()) {
                                                LOG.debug("Stille Reserve vorhanden");
                                            }
                                            if (deltaLastBuchung != null) {
                                                if (LOG.isDebugEnabled()) {
                                                    LOG.debug("deltaLastBuchung: " + deltaLastBuchung);
                                                }
                                                if (deltaLastBuchung >= deltaBuchwert) {
                                                    if (LOG.isDebugEnabled()) {
                                                        LOG.debug("Stille Reserve wurde angelegt");
                                                    }
                                                    nutzungStates.add(NUTZUNG_STATES.STILLE_RESERVE_CREATED);
                                                } else if (deltaLastBuchung > 0) {
                                                    if (LOG.isDebugEnabled()) {
                                                        LOG.debug("Stille Reserve wurde erhöt");
                                                    }
                                                    nutzungStates.add(NUTZUNG_STATES.STILLE_RESERVE_INCREASED);
                                                    nutzungStates.add(NUTZUNG_STATES.STILLE_RESERVE_EXISTING);
                                                } else if (deltaLastBuchung < 0) {
                                                    if (LOG.isDebugEnabled()) {
                                                        LOG.debug("Stille Reserve wurde vermindert");
                                                    }
                                                    nutzungStates.add(NUTZUNG_STATES.STILLE_RESERVE_DECREASED);
                                                    nutzungStates.add(NUTZUNG_STATES.STILLE_RESERVE_EXISTING);
                                                }
                                            }
                                        } else {
                                            if (LOG.isDebugEnabled()) {
                                                LOG.debug("Keine Stille Reserve vorhanden");
                                            }
                                            if (deltaLastBuchung < deltaBuchwert) {
                                                if (LOG.isDebugEnabled()) {
                                                    LOG.debug("Stille Reserve wurde aufgelöst");
                                                }
                                                nutzungStates.add(NUTZUNG_STATES.STILLE_RESERVE_DISOLVED);
                                            } else if (deltaLastBuchung > 0) {
                                                if (LOG.isDebugEnabled()) {
                                                    LOG.debug("Positive Buchung");
                                                }
                                            } else {
                                                if (LOG.isDebugEnabled()) {
                                                    LOG.debug("Negative Buchung");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Keine neue Buchung vorhanden");
                    }
                }
            }
        }
        return nutzungStates;
    }

    @Override
    public NutzungBuchungCustomBean getOpenBuchung() {
        if ((getBuchungsCount() > 0) && !isTerminated()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("nutzungen available, not terminated");
            }
            return getNutzungsBuchungen().get(getNutzungsBuchungen().size() - 1);
        }
        return null;
    }

    @Override
    public NutzungBuchungCustomBean getPredecessorBuchung(final NutzungBuchungCustomBean val) {
        if ((val != null) && (getBuchungsCount() > 1)) {
            final int successorIndex = getNutzungsBuchungen().indexOf(val);
            if ((successorIndex != -1) && (successorIndex > 0)) {
                return getNutzungsBuchungen().get(successorIndex - 1);
            }
        }
        return null;
    }

    @Override
    public NutzungBuchungCustomBean getPreviousBuchung() {
        if (getBuchungsCount() > 1) {
            return getNutzungsBuchungen().get(getNutzungsBuchungen().size() - 2);
        }
        return null;
    }

    @Override
    public Double getStilleReserve() throws IllegalNutzungStateException {
        final Double bookValueDifference = getBuchwertDifference();
        return ((bookValueDifference != null) && (bookValueDifference > 0.0)) ? bookValueDifference : 0.0;
    }

    @Override
    public Double getStilleReserveForBuchung(final NutzungBuchungCustomBean val) throws IllegalNutzungStateException,
        BuchungNotInNutzungException {
        if ((val != null) && (getBuchungsCount() > 0) && (val.getGesamtpreis() != null)) {
            if (!isBuchungInNutzung(val)) {
                throw new BuchungNotInNutzungException();
            }
            final NutzungBuchungCustomBean buchwert = getBuchwert(val);
            if ((buchwert != null) && (buchwert.getGesamtpreis() != null)) {
                final double difference = val.getGesamtpreis() - buchwert.getGesamtpreis();
                if (difference >= 0.0) {
                    return difference;
                } else {
                    return 0.0;
                }
            }
        }
        return null;
    }

    @Override
    public NutzungBuchungCustomBean getTerminalBuchung() {
        if ((getBuchungsCount() > 0) && isTerminated()) {
            return getNutzungsBuchungen().get(getNutzungsBuchungen().size() - 1);
        }
        return null;
    }

    @Override
    public boolean hasNewBuchung() {
        if (getBuchungsCount() > 0) {
            final Integer id = getNutzungsBuchungen().get(getNutzungsBuchungen().size() - 1).getId();
            return (id == null) || (id == -1);
        }
        return false;
    }

    @Override
    public boolean isBuchungFlippable(final NutzungBuchungCustomBean val) {
        if (val == null) {
            return false;
        }
        if ((getBuchungsCount() < 1) || !isBuchungInNutzung(val)) {
            return false;
        }
        if (getNutzungsBuchungen().indexOf(val) == 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isTerminated() {
        if (getBuchungsCount() > 0) {
            final NutzungBuchungCustomBean lastBuchung = getNutzungsBuchungen().get(getNutzungsBuchungen().size() - 1);
            if (lastBuchung.getGueltigbis() != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mustBeTerminated() {
        final NutzungBuchungCustomBean terminalBuchung = getTerminalBuchung();
        return (terminalBuchung != null) && terminalBuchung.getSollGeloeschtWerden();
    }

    @Override
    public boolean removeOpenNutzung() {
        if ((getBuchungsCount() > 1) && hasNewBuchung()) {
            getPreviousBuchung().setGueltigbis(null);
            getNutzungsBuchungen().remove(getNutzungsBuchungen().size() - 1);
            return true;
        }
        return false;
    }

    @Override
    public void setNutzungsBuchungen(final List<NutzungBuchungCustomBean> val) {
    }

    @Override
    public void terminateNutzung(final Date val) throws TerminateNutzungNotPossibleException {
        if (val == null) {
            throw new TerminateNutzungNotPossibleException("Terminierung nicht möglich datum ist Null");
        } else if (getOpenBuchung() == null) {
            throw new TerminateNutzungNotPossibleException("Nutzung hat keine offene Buchung");
        }
        final NutzungBuchungCustomBean terminalBuchung = getOpenBuchung();
        terminalBuchung.setGueltigbis(val);
        terminalBuchung.setSollGeloeschtWerden(true);
    }

    @Override
    public String toString() {
        return "de.cismet.lagisEE.entity.core.Nutzung[id=" + getId() + "]";
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        try {
            if (getBuchungsCount() > 0) {
                final NutzungBuchungCustomBean[] nutzungBuchungen = getNutzungsBuchungen().toArray(
                        new NutzungBuchungCustomBean[0]);
                final NutzungCustomBean clone = createNew((NutzungBuchungCustomBean)nutzungBuchungen[0].clone());
                for (int i = 1; i < nutzungBuchungen.length; i++) {
                    try {
                        clone.addBuchung((NutzungBuchungCustomBean)nutzungBuchungen[i].clone());
                    } catch (Exception ex) {
                        throw new CloneNotSupportedException();
                    }
                }
                return clone;
            }
        } catch (AddingOfBuchungNotPossibleException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Clonen nicht möglich --> Problem beim hinzufügen", ex);
            }
        } catch (IllegalNutzungStateException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Clonen nicht möglich --> Kein Buchwert", ex);
            }
        }
        throw new CloneNotSupportedException();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   buchung  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isBuchungInNutzung(final NutzungBuchungCustomBean buchung) {
        if (getBuchungsCount() > 0) {
            return getNutzungsBuchungen().contains(buchung);
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   date  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Date getDateWithoutTime(final Date date) {
        if (date == null) {
            return null;
        }
        final GregorianCalendar calender = new GregorianCalendar();
        calender.setTime(date);
        calender.set(GregorianCalendar.HOUR, 0);
        calender.set(GregorianCalendar.MINUTE, 0);
        calender.set(GregorianCalendar.SECOND, 0);
        calender.set(GregorianCalendar.MILLISECOND, 0);
        calender.set(GregorianCalendar.AM_PM, GregorianCalendar.AM);
        return calender.getTime();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalNutzungStateException  DOCUMENT ME!
     */
    private int getDistanceFromLastBuchungToBuchwert() throws IllegalNutzungStateException {
        if (getBuchungsCount() > 0) {
            final NutzungBuchungCustomBean buchwert = getBuchwert();
            final NutzungBuchungCustomBean lastBuchung = getNutzungsBuchungen().get(getNutzungsBuchungen().size() - 1);
            final int buchwertIndex = getNutzungsBuchungen().indexOf(buchwert);
            final int lastBuchungIndex = getNutzungsBuchungen().indexOf(lastBuchung);
            if ((buchwertIndex != -1) && (lastBuchungIndex != -1)) {
                if (buchwertIndex == lastBuchungIndex) {
                    return 0;
                } else {
                    return (lastBuchungIndex - buchwertIndex);
                }
            }
        }
        return -1;
    }
}
