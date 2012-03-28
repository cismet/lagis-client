/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagisEE.bean;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import java.text.DecimalFormat;

import java.util.*;

import javax.annotation.Resource;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import de.cismet.cids.custom.beans.verdis_grundis.*;

import de.cismet.lagis.Exception.ActionNotSuccessfulException;
import de.cismet.lagis.Exception.ErrorInNutzungProcessingException;

import de.cismet.lagis.cidsmigtest.BrokerTester;
import de.cismet.lagis.cidsmigtest.CidsAppBackend;

import de.cismet.lagisEE.crossover.entity.WfsFlurstuecke;

import de.cismet.lagisEE.interfaces.Key;

import de.cismet.lagisEE.util.FlurKey;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class LagisServer {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LagisServer.class);

    private static final String ERROR_MESSAGE_GENERATION_ERROR = "Fehler beim Erzeugen einer Emailnachricht: ";

    // ToDo CompleteKey sollte direkt von den entsprechenden Methoden ausgeführt werden;
    // Idea using different manager with different context (name of unit) +
    // idea 2 @PersistenceContext(type = PersistenceContextType.EXTENDED) ??
    // Attention transactions for second ejb are disabled.
// private EntityManager em;
    private static Vector<Session> nkfSessions = new Vector<Session>();
    private static DecimalFormat currencyFormatter = new DecimalFormat(",##0.00 \u00A4");

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum HistoryLevel {

        //~ Enum constants -----------------------------------------------------

        DIRECT_RELATIONS, All, CUSTOM
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum HistoryType {

        //~ Enum constants -----------------------------------------------------

        SUCCESSOR, PREDECESSOR, BOTH
    }

    //~ Instance fields --------------------------------------------------------

// //ToDo muss nachvollziebar im logging sein
// //ToDo aufsplitten in mehrere Methoden NutzungChangedCheck, StilleReserveCheck etc.
// public void modifyFlurstueck(FlurstueckCustomBean flurstueck) throws ActionNotSuccessfulException {
// try {
// try {
// FlurstueckCustomBean databaseFlurstueck = retrieveFlurstueck(flurstueck.getFlurstueckSchluessel());
// if (databaseFlurstueck != null && (flurstueck.getNutzungen() != null && databaseFlurstueck.getNutzungen() != null)) {
//                    Set<Nutzung> currentNutzungen = flurstueck.getNutzungen();
//                    System.out.println("Anzahl Ketten in aktuellem Flurstück: " + currentNutzungen.size());
//                    Date currentDate = new Date();
////                    Vector<Integer> modifiedNutzungen = new Vector<Integer>();
////                    Vector<Nutzung> createdNutzungen = new Vector<Nutzung>();
////                    //TODO removed is not deleted --> confusing
//                    Vector<Nutzung> removedNutzungen = new Vector<Nutzung>();
////                    Vector<Integer> notModifiedNutzungen = new Vector<Integer>();
//                    Vector<Nutzung> nutzungenToDelete = new Vector<Nutzung>();
//                    for (NutzungCustomBean curNutzung : currentNutzungen) {
//                        if (curNutzung.getGueltigbis() != null) {
//                            if (curNutzung.getSollGeloeschtWerden()) {
//                                System.out.println("Zu prüfende NutzungCustomBean soll gelöscht werden");
//                                //nutzungenToDelete.add(curNutzung);
//                                curNutzung.setGueltigbis(currentDate);
//                                curNutzung.setWurdeGeloescht(true);
//                                Set<Nutzung> bookedNutzungen = new HashSet();
//                                if (curNutzung.getIstGebucht() == null || !curNutzung.getIstGebucht()) {
//                                    //TODO über Buchungsmethode
//                                    System.out.println("Die zu löschende NutzungCustomBean ist noch nicht gebucht");
//                                    curNutzung.setBuchungsDatum(currentDate);
//                                    curNutzung.setIstGebucht(true);
//                                    bookedNutzungen.add(curNutzung);
//                                }
//                                System.out.println("Überprüfe ob es ungebuchte Vorgänger gibt...");
//                                final Set<Nutzung> predecessors = curNutzung.getAllPredecessors();
//                                for (NutzungCustomBean predecessor : predecessors) {
//                                    if (predecessor.getIstGebucht() == null || !curNutzung.getIstGebucht()) {
//                                        bookNutzung(predecessor, currentDate);
//                                        bookedNutzungen.add(predeces
//                                    } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.STILLE_RESERVE_DECREASED)) {
//                                        sor)
//
//
//
//                                ;
//                                    }
//                                }
////DONE
//                                //                                System.out.println("Vorgänger wurden überprüft." + bookedNutzungen.size() + " Nutzungen wurden gebucht.");
////                                System.out.println("Benachrichtigung der Verantwortlichen Stelle über die Veränderung...");
////                                StringBuffer message = new StringBuffer();
////                                message.append("Bei dem Flurstück ");
////                                message.append(flurstueck.getFlurstueckSchluessel().getKeyString());
////                                message.append(" wurde die NutzungCustomBean: \n\n");
////                                message.append(curNutzung);
////                                message.append("\n\n");
////                                if (bookedNutzungen.size() > 0) {
////                                    message.append("gelöscht. Alle offenen Buchungen der NutzungCustomBean(" + bookedNutzungen.size() + ") wurden gebucht");
////                                } else {
////                                    message.append("gelöscht. Es waren keine offene Buchungen vorhanden");
////                                }
////                                sendEmail("Lagis - NutzungCustomBean wurde gelöscht", message.toString(), nkfSessions);
//                            } else {
//                                System.out.println("Zu prüfende NutzungCustomBean ist historsich");
//                            }
//                        } else if (curNutzung.getId() == null) {
//                            System.out.println("Zu prüfende NutzungCustomBean ist neu. Prüfe ob vorgänger vorhanden sind");
//                            NutzungCustomBean vorgaenger;
//                            if ((vorgaenger = curNutzung.getVorgaenger_todo()) != null) {
//                                System.out.println("Zu prüfende NutzungCustomBean wurde modifiziert.");
//                                Set<Nutzung.NUTZUNG_FIELDS> changedFields;
//                                if ((changedFields = NutzungCustomBean.NUTZUNG_HISTORY_EQUALATOR.determineUnequalFields(curNutzung, vorgaenger)).size() > 0) {
//                                    System.out.println("Folgende Felder haben sich geändert: " + Arrays.toString(changedFields.toArray()));
//                                    if (changedFields.contains(NutzungCustomBean.NUTZUNG_FIELDS.NUTZUNGSART)) {
//                                        System.out.println("Die NutzungsartCustomBean beider Nutzungen sind unterschiedlich");
//                                        if (curNutzung.getNutzungsart() != null && vorgaenger.getNutzungsart() != null) {
//                                            System.out.println("Beide Nutzungen enthalten eine NutzungsartCustomBean --> abgleich");
//                                            if (curNutzung.getNutzungsart().getId() != null && vorgaenger.getNutzungsart().getId() != null && !vorgaenger.getNutzungsart().getId().equals(curNutzung.getNutzungsart().getId())) {
//                                                System.out.println("NutzungsartCustomBean hat sich verändert --> email an Verantwortliche");
//                                                //TODO zusätzlich zu email logging
//                                                sendEmail("Lagis - Änderung einer NutzungsartCustomBean", createNutzungsartChangedMessage(flurstueck, vorgaenger, curNutzung), nkfSessions);
//                                            } else if (curNutzung.getNutzungsart() != null) {
//                                                System.out.println("NutzungsartCustomBean wurde angelegt");
//                                            } else if (vorgaenger.getNutzungsart() != null) {
//                                                System.out.println("NutzungsartCustomBean wurde gelöscht");
//                                            } else {
//                                                System.out.println("NutzungCustomBean hat keine NutzungsartCustomBean");
//                                            }
//                                            System.out.println("Prüfe ob Stille Reserve oder nicht:");
//                                            Double vorgaengerStilleReserve = null;
//                                            Double curNutzungStilleReserve = null;
//                                            if ((curNutzungStilleReserve = curNutzung.getStilleReserve()) != null && (vorgaengerStilleReserve = vorgaenger.getStilleReserve()) != null) {
//
//
//
//                                                if (curNutzungStilleReserve > 0.0) {
//                                                    System.out.println("Stille Reserve vorhanden");
//                                                    if (curNutzungStilleReserve > vorgaengerStilleReserve) {
//                                                        System.out.println("Stille Reserve erhöt");
//                                                    } else if (curNutzungStilleReserve < vorgaengerStilleReserve) {
//                                                        System.out.println("Stille Reserve vermindert");
//                                                    } else {
//                                                        System.out.println("Stille Reserve unverändert");
//                                                    }
//                                                    //keine Stille Reserve vorhanden, prüfe ob eine aufgelöst wurde
//                                                } else if (vorgaengerStilleReserve > 0) {
//                                                }
//
//                                                if ((curNutzungStilleReserve + vorgaengerStilleReserve) > 0.0) {
//
//                                                    if (curNutzungStilleReserve.equals(vorgaengerStilleReserve)) {
//                                                        System.out.println("Stille Reserve unverändert");
//                                                    } else if (curNutzungStilleReserve > 0 && vorgaengerStilleReserve > 0 && curNutzungStilleReserve > vorgaengerStilleReserve) {
//                                                        System.out.println("Stille Reserve erhöt");
//                                                    } else if (curNutzungStilleReserve > 0 && vorgaengerStilleReserve > 0 && curNutzungStilleReserve < vorgaengerStilleReserve) {
//                                                        System.out.println("Stille Reserve vermindert");
//
//                                                    }
//                                                } else if (vorgaengerStilleReserve <= 0.0 && curNutzungStilleReserve > 0.0) {
//                                                    System.out.println("Stille Reserve wurde gebildet");
//                                                } else if ((curNutzungStilleReserve + vorgaengerStilleReserve) <= 0.0) {
//                                                    System.out.println("Keine Stille Reserve vorhanden");
//                                                } else {
//                                                    System.out.println("Fall unbekannt");
//                                                    //ToDo Exception
//                                                }
//
//                                                if (vorgaenger.getIstGebucht() != null && !vorgaenger.getIstGebucht()) {
//                                                    System.out.println("Bei einer NutzungCustomBean ist eine Stille Reserve vorhanden");
//                                                    //TODO generell vorher machen --> redudanter code
//                                                    double gesamtpreisOld = curOldNutzung.getFlaeche() * curOldNutzung.getQuadratmeterpreis();
//                                                    double gesamtpreisNew = curNutzung.getFlaeche() * curNutzung.getQuadratmeterpreis();
//                                                    double diff = gesamtpreisNew - gesamtpreisOld;
//                                                    if (diff == 0.0) {
//                                                        System.out.println("Gesamtpreis hat sich nicht verändert");
//                                                        newNutzung.setStilleReserve(curOldNutzung.getStilleReserve());
//                                                        newNutzung.setIstGebucht(false);
//                                                    } else if (diff > 0.0) {
////Done
//                                                        //                                                        System.out.println("Gesamtpreis hat sich erhöht --> Stille Reserve wird einfach erhöht");
////                                                        newNutzung.setStilleReserve(curOldNutzung.getStilleReserve() + diff);
////                                                        newNutzung.setIstGebucht(false);
////                                                        StringBuffer message = new StringBuffer();
////                                                        message.append("Bei dem Flurstück ");
////                                                        message.append(flurstueck.getFlurstueckSchluessel().getKeyString());
////                                                        message.append(" wurde die NutzungCustomBean: \n\n");
////                                                        message.append(curOldNutzung);
////                                                        message.append("\nwie folgt abgeändert:\n\n");
////                                                        message.append(newNutzung);
////                                                        message.append("\nDie vorhandene Stille Reserve wurde um " + currencyFormatter.format(diff) + " erhöht.");
////                                                        sendEmail("Lagis - Stille Reserve wurde erhöht", message.toString(), nkfSessions);
//                                                        //Email an dirk ? StringBuffer message = new StringBuffer();
////                                                        message.append("Bei dem Flurstück ");
////                                                        message.append(flurstueck.getFlurstueckSchluessel().getKeyString());
////                                                        message.append(" wurde die NutzungCustomBean: \n\n");
////                                                        message.append(curOldNutzung);
////                                                        message.append("\nwie folgt abgeändert:\n\n");
////                                                        message.append(newNutzung);
////                                                        message.append("\nEs ist keine Stille Reserve vorhanden. Der Gesamtpreis wurde um " + currencyFormatter.format(Math.abs(diff)) + " reduziert.");
////                                                        sendEmail("Lagis - Gesamtpreis einer NutzungCustomBean hat sich vermindert", message.toString(), nkfSessions);
//                                                        } else {
//                                                        System.out.println("Gesamtpreis hat sich vermindert wird mit Stiller Reserve Verrechnet");
//                                                        if (Math.abs(diff) > curOldNutzung.getStilleReserve()) {
//                                                            double stilleReserveForEmail = curOldNutzung.getStilleReserve();
//                                                            //TODO über Buchungsmethode
//                                                            //hier muss gebucht werden direkt gebucht werden
//                                                            List<Nutzung> nutzungenToBook = new Vector<Nutzung>();
//                                                            //nutzungenToBook.add(newNutzung);
//                                                            newNutzung.setStilleReserve(0.0);
//
//                                                            newNutzung.setBuchungsDatum(currentDate);
//                                                            newNutzung.setIstGebucht(true);
//                                                            //curOldNutzung is not parent --> nessecary for recursion
//                                                            NutzungCustomBean parent = curOldNutzung;
//                                                            nutzungenToBook.add(curOldNutzung);
//                                                            while ((parent = getUnbookedParent(parent, currentNutzungen)) != null) {
//                                                                System.out.println("Vorgaenger ist eine ungebuchte NutzungCustomBean");
//                                                                nutzungenToBook.add(parent);
//                                                            }
//                                                            System.out.println("Keine weiteren ungebuchten Nutzungen vorhanden --> buche");
//                                                            System.out.println("Anzahl zu buchender Nutzungen: " + nutzungenToBook.size());
//                                                            //ToDO brauch ich spezialfall ?
//                                                            if (nutzungenToBook.size() > 0) {
//                                                                System.out.println("Mehr als eine ungebuchte NutzungCustomBean --> buche");
//                                                                ListIterator<Nutzung> nutzungItr = nutzungenToBook.listIterator(nutzungenToBook.size());
//                                                                while (nutzungItr.hasPrevious()) {
//                                                                    NutzungCustomBean currentNutzung = nutzungItr.previous();
//                                                                    System.out.println("Buche NutzungCustomBean: " + currentNutzung.getId());
//                                                                    currentNutzung.setBuchungsDatum(currentDate);
//                                                                    currentNutzung.setIstGebucht(true);
//                                                                    currentNutzung.setStilleReserve(0.0);
//                                                                    if (!nutzungItr.hasPrevious()) {
//                                                                    }
//                                                                }
//// Done
////                                                                StringBuffer message = new StringBuffer();
////                                                                message.append("Bei dem Flurstück ");
////                                                                message.append(flurstueck.getFlurstueckSchluessel().getKeyString());
////                                                                message.append(" wurde die NutzungCustomBean: \n\n");
////                                                                message.append(curOldNutzung);
////                                                                message.append("\nwie folgt abgeändert:\n\n");
////                                                                message.append(newNutzung);
////                                                                message.append("\nEine NutzungCustomBean des Flurstücks wurde reduziert. Die vorhandene Stille Reserve von " + currencyFormatter.format(stilleReserveForEmail) + " reicht nicht aus um den Differenzbetrag von " + currencyFormatter.format(Math.abs(diff)) + " auszugleichen. ");
////                                                                message.append("Die Stille Reserve wird aufgelöst und alle offenen Buchungen(" + nutzungenToBook.size() + ") werden gebucht.");
////                                                                sendEmail("Lagis - Stille Reserve wurde aufgelöst", message.toString(), nkfSessions);
//                                                            }
//                                                            //                                                        } else {
////                                                            System.out.println("Nur eine ungebuchte NutzungCustomBean --> buche");
////                                                            //TODO redundant
////                                                            //TODO mail generieren das Stille Reserve aufgelöst wurde
////                                                            sendEmail("Lagis - Stille Reserve wurde aufgelöst (einfach)", "Test Email",nkfSessions);
////                                                        }
//                                                            } else {
//                                                            //
//                                                            //Done
////                                                            newNutzung.setStilleReserve(curOldNutzung.getStilleReserve() + diff);
////                                                            newNutzung.setIstGebucht(false);
////                                                            StringBuffer message = new StringBuffer();
////                                                            message.append("Bei dem Flurstück ");
////                                                            message.append(flurstueck.getFlurstueckSchluessel().getKeyString());
////                                                            message.append(" wurde die NutzungCustomBean: \n\n");
////                                                            message.append(curOldNutzung);
////                                                            message.append("\nwie folgt abgeändert:\n\n");
////                                                            message.append(newNutzung);
////                                                            message.append("\nDie vorhandene Stille Reserve wurde um " + currencyFormatter.format(Math.abs(diff)) + " reduziert.");
////                                                            sendEmail("Lagis - Stille Reserve wurde reduziert", message.toString(), nkfSessions);
//                                                        }
//
//                                                        //TODO --> Email
//                                                        }
//                                                    //warten auf Dirk (Spezial Fall)
//                                                    } else if (curOldNutzung.getStilleReserve() != null && curOldNutzung.getIstGebucht() == null) {
//                                                    System.out.println("Stille Reserve vorhanden aber istGebucht=true oder istGebucht==null");
//                                                    throw new DataInconsistencyException("Eine NutzungCustomBean hat eine Stille Reserve und der Buchungstatus ist unbekannt.");
//                                                } else {
//                                                    System.out.println("Keine früheren Stillen Reserven vorhanden --> prüfe ob jetzt eine ensteht");
//                                                    double gesamtpreisOld = curOldNutzung.getFlaeche() * curOldNutzung.getQuadratmeterpreis();
//                                                    double gesamtpreisNew = curNutzung.getFlaeche() * curNutzung.getQuadratmeterpreis();
//                                                    double diff = gesamtpreisNew - gesamtpreisOld;
//                                                    if (diff == 0.0) {
//                                                        System.out.println("Gesamtpreis hat sich nicht verändert");
//                                                    } else if (diff > 0.0) {
//                                                        System.out.println("Eine Stille Reserve wurde gebildet");
//                                                        newNutzung.setStilleReserve(diff);
//                                                        newNutzung.setIstGebucht(false);
//                                                        System.out.println("istGebucht: " + newNutzung.getIstGebucht());
////Done
////                                                        StringBuffer message = new StringBuffer();
////                                                        message.append("Bei dem Flurstück ");
////                                                        message.append(flurstueck.getFlurstueckSchluessel().getKeyString());
////                                                        message.append(" wurde die NutzungCustomBean: \n\n");
////                                                        message.append(curOldNutzung);
////                                                        message.append("\nwie folgt abgeändert:\n\n");
////                                                        message.append(newNutzung);
////                                                        message.append("\nEs wurde eine Stille Reserve in Höhe von: " + currencyFormatter.format(newNutzung.getStilleReserve()) + " gebildet.");
////                                                        sendEmail("Lagis - Stille Reserve wurde gebildet", message.toString(), nkfSessions);
//
//                                                        //Email an dirk ?
//                                                        } else {
//                                                        System.out.println("Keine Stille Reserve, Gesamtpreis hat sich vermindert");
////Done
//                                                        //                                                        StringBuffer message = new StringBuffer();
////                                                        message.append("Bei dem Flurstück ");
////                                                        message.append(flurstueck.getFlurstueckSchluessel().getKeyString());
////                                                        message.append(" wurde die NutzungCustomBean: \n\n");
////                                                        message.append(curOldNutzung);
////                                                        message.append("\nwie folgt abgeändert:\n\n");
////                                                        message.append(newNutzung);
////                                                        message.append("\nEs ist keine Stille Reserve vorhanden. Der Gesamtpreis wurde um " + currencyFormatter.format(Math.abs(diff)) + " reduziert.");
////                                                        sendEmail("Lagis - Gesamtpreis einer NutzungCustomBean hat sich vermindert", message.toString(), nkfSessions);
//                                                    }
//                                                }
//                                            } else {
//                                                //TODO Mögliche Dateninkonsistenz
//                                                System.out.println("Stille Reserve kann nicht geprüft werden midestends eine Fläche/Quadratmeterpeis ist null");
//                                                //Todo -- Exception/Benachrichtigung
//                                            }
//                                        } else {
//                                            System.out.println("Nutzungsarten sind gleich oder mind. eine enthält keine ID");
//                                        }
//                                    }
//                                } else {
//                                    System.out.println("Keine Änderung zwischen zu prüfender NutzungCustomBean und Vorgänger. Neue NutzungCustomBean wird zum löschen vorgemerkt");
//                                    removedNutzungen.add(curNutzung);
//                                }
//                            } else {
//                                System.out.println("Zu prüfende NutzungCustomBean wurde neu angelegt");
//                                curNutzung.setIstGebucht(true);
//                                curNutzung.setGueltigvon(currentDate);
//                                //ToDo nichts zu tun wird einfach gespeichert.
//                                }
//                        } else if (curNutzung.getGueltigbis() == null) {
//                            System.out.println("Zu prüfende NutzungCustomBean ist aktuell ohne Änderung.");
//                        } else {
//                            System.out.println("NutzungCustomBean ist weder: historisch,gelöscht,neu noch aktuell. Dieser Fall darf nicht vorkommen. NutzungCustomBean: " + curNutzung);
//                            //ToDo Inkonsistenz
//                            }
//                    }
//                    if (removedNutzungen.size() > 0) {
//                        System.out.println("Es gibt " + removedNutzungen.size() + " nutzungen die Entfernt werden müssen.");
//                        currentNutzungen.removeAll(removedNutzungen);
//                    }
//
//
//
//                    System.out.println("Anzahl nicht modifizierter Nutzungen: " + notModifiedNutzungen.size());
//                    Iterator<Nutzung> it2 = removedNutzungen.iterator();
//                    for (NutzungCustomBean createdNutzung : createdNutzungen) {
//                        if (it2.hasNext()) {
//                            currentNutzungen.remove(it2.next());
//                        }
//                        currentNutzungen.add(createdNutzung);
//                    }
//                    for (NutzungCustomBean curDatabaseNutzung : databaseNutzungen) {
//                        int result = modifiedNutzungen.indexOf(curDatabaseNutzung.getId());
//                        if (result != -1) {
//                            System.out.println("NutzungCustomBean: " + curDatabaseNutzung.getId() + " wird historisch gesetzt");
//                            curDatabaseNutzung.setGueltigbis(currentDate);
//                            currentNutzungen.add(curDatabaseNutzung);
//                            //ToDo check if this leads to an exception
//                            databaseNutzungen.remove(curDatabaseNutzung);
//                            continue;
//                        }
//                        result = notModifiedNutzungen.indexOf(curDatabaseNutzung.getId());
//                        if (result != -1) {
//                            System.out.println("NutzungCustomBean: " + curDatabaseNutzung.getId() + " wurde nicht modifiziert");
//                            databaseNutzungen.remove(curDatabaseNutzung);
//                        }
//                    }
//                    System.out.println("Anzahl gelöschter Nutzungen: " + nutzungenToDelete.size());
//                    for (NutzungCustomBean curNutzungToDelete : nutzungenToDelete) {
//                        curNutzungToDelete.setGueltigbis(currentDate);
//                        curNutzungToDelete.setWurdeGeloescht(true);
//
//                        if (curNutzungToDelete.getIstGebucht() == null || !curNutzungToDelete.getIstGebucht()) {
//                            //TODO über Buchungsmethode
//                            System.out.println("NutzungCustomBean ist noch nicht gebucht");
//                            curNutzungToDelete.setBuchungsDatum(currentDate);
//                            curNutzungToDelete.setIstGebucht(true);
//                            curNutzungToDelete.setStilleReserve(0.0);
//                            List<Nutzung> nutzungenToBook = new Vector<Nutzung>();
//                            //nutzungenToBook.add(newNutzung);
//                            //curOldNutzung is not parent --> nessecary for recursion
//                            NutzungCustomBean parent = curNutzungToDelete;
//                            nutzungenToBook.add(curNutzungToDelete);
//                            while ((parent = getUnbookedParent(parent, currentNutzungen)) != null) {
//                                System.out.println("Vorgaenger ist eine ungebuchte NutzungCustomBean");
//                                nutzungenToBook.add(parent);
//                            }
//                            System.out.println("Keine weiteren ungebuchten Nutzungen vorhanden --> buche");
//                            System.out.println("Anzahl zu buchender Nutzungen: " + nutzungenToBook.size());
//                            //ToDO brauch ich spezialfall ?
//                            if (nutzungenToBook.size() > 0) {
//                                System.out.println("Mehr als eine ungebuchte NutzungCustomBean --> buche");
//                                ListIterator<Nutzung> nutzungItr = nutzungenToBook.listIterator(nutzungenToBook.size());
//                                while (nutzungItr.hasPrevious()) {
//                                    NutzungCustomBean currentNutzung = nutzungItr.previous();
//                                    System.out.println("Buche NutzungCustomBean: " + currentNutzung.getId());
//                                    currentNutzung.setBuchungsDatum(currentDate);
//                                    currentNutzung.setIstGebucht(true);
//                                    currentNutzung.setStilleReserve(0.0);
//                                    if (!nutzungItr.hasPrevious()) {
//                                    }
//                                }
//                                StringBuffer message = new StringBuffer();
//                                message.append("Bei dem Flurstück ");
//                                message.append(flurstueck.getFlurstueckSchluessel().getKeyString());
//                                message.append(" wurde die NutzungCustomBean: \n\n");
//                                message.append(curNutzungToDelete);
//                                message.append("\n\n");
//                                message.append("gelöscht. Alle offenen Buchungen der NutzungCustomBean(" + nutzungenToBook.size() + ") wurden gebucht");
//                                sendEmail("Lagis - NutzungCustomBean mit offenen Buchungen wurde gelöscht", message.toString(), nkfSessions);
//                            }
//                        } else {
//                            System.out.println("keine offenen Buchungen --> nichts mehr zu tun");
//                            StringBuffer message = new StringBuffer();
//                            message.append("Bei dem Flurstück ");
//                            message.append(flurstueck.getFlurstueckSchluessel().getKeyString());
//                            message.append(" wurde die NutzungCustomBean: \n\n");
//                            message.append(curNutzungToDelete);
//                            message.append("\n\n");
//                            message.append("gelöscht. Es waren keine offene Buchungen vorhanden");
//                            sendEmail("Lagis - NutzungCustomBean wurde gelöscht", message.toString(), nkfSessions);
//                        }
//                    }
//                    System.out.println("Anzahl nicht verarbeiteter Nutzungen: " + databaseNutzungen.size());
//                } else {
//                    /**ToDo databaseflurstueck == null
//                     *
//                     * Was ist wenn eine der Nutzungen Null ist
//                     */
//                }
//            } catch (BookingNotPossibleException ex) {
//                //ToDo Fall bearbeiten
//                } catch (Exception ex) {
//                System.out.println("Fehler beim Nutzungs abgleich und Historie");
//                ex.printStackTrace();
//                throw new ActionNotSuccessfulException("Fehler beim modifizieren eines Flurstücks", ex);
//            }
//
//            checkIfFlurstueckWasStaedtisch(flurstueck.getFlurstueckSchluessel(),
//                    null);
//            FlurstueckCustomBean merged = em.merge(flurstueck);
//            em.flush();
//            System.out.println(
//                    "Flurstück gespeichert");
////            System.out.println("Speichern der NKFHistorie einträge");
////            Iterator<NKFHistorie> nkfHistoryIterator = nkfHistoryEntries.iterator();
////            while (nkfHistoryIterator.hasNext()) {
////                NKFHistorie current = nkfHistoryIterator.next();
////                //em.refresh(current.getNachfolger());
////                //em.refresh(current.getVorgaenger());
////                em.persist(current);
////                System.out.println("NKFHistoryEntry gespeichert");
////            }
//            //ONLY FOR TESTING
//        } catch (Exception ex) {
//            System.out.println("Fehler beim speichern der Entität");
//            ex.printStackTrace();
//            throw new ActionNotSuccessfulException("Fehler beim modifizieren eines Flurstücks", ex);
//        }
//
//    }
//    private void bookNutzung(NutzungCustomBean nutzungToBook, Date bookingDate) throws BookingNotPossibleException {
//        try {
//            if (bookingDate == null) {
//                throw new NullPointerException("Datum darf nicht null sein");
//            }
//            System.out.println("Buche NutzungCustomBean: " + nutzungToBook.getId());
//            nutzungToBook.setBuchungsDatum(bookingDate);
//            nutzungToBook.setIstGebucht(true);
//        } catch (NullPointerException ex) {
//            throw new BookingNotPossibleException("Eingabe Parameter dürfen nicht null sein", ex);
//        }
//    }
//    private String createNutzungsartChangedMessage(FlurstueckCustomBean flurstueck, NutzungCustomBean oldNutzung, NutzungCustomBean newNutzung) {
//        try {
//
//            return message.toString();
//        } catch (NullPointerException ex) {
//            //ToDo logging
//            return ERROR_MESSAGE_GENERATION_ERROR + "Änderung der NutzungsartCustomBean";
//        }
//    }
//
//    private NutzungCustomBean getUnbookedParent(final NutzungCustomBean nutzung,
//            final Set<Nutzung> allNutzungen) throws DataInconsistencyException {
//        NutzungCustomBean predecessor = getPredecessor(nutzung, allNutzungen);
//        if (predecessor != null) {
//            System.out.println("Vorgänger vorhanden");
//            if (predecessor.getIstGebucht() != null) {
//                if (!predecessor.getIstGebucht()) {
//                    System.out.println("Vorgänger ist noch nicht gebucht");
//                    return predecessor;
//                } else {
//                    System.out.println("Vorgänger ist schon gebucht");
//                    return null;
//                }
//
//            } else {
//                throw new DataInconsistencyException("Ein vorhergende Buchung hat keine Buchungsinformationen (istGebucht==null)");
//            }
//
//        } else {
//            return null;
//        }
//
//    }
//    private NutzungCustomBean getPredecessor(final NutzungCustomBean nutzung,
//            final Set<Nutzung> allNutzungen) {
//        if (nutzung != null && allNutzungen != null && allNutzungen.size() > 0) {
//            System.out.println("Suche Vorgänger mit ID: " + nutzung.getVorgaenger());
//            if (nutzung.getVorgaenger() != null) {
//                Iterator<Nutzung> it = allNutzungen.iterator();
//                while (it.hasNext()) {
//                    NutzungCustomBean current = it.next();
//                    if (current.getId() != null && current.getId().equals(nutzung.getVorgaenger())) {
//                        System.out.println("Vorgänger gefunden");
//                        return current;
//                    }
//                }
//                System.out.println("Kein Vorgänger gefunden");
//                return null;
//            } else {
//                System.out.println("Kein Vorgänger vorhanden");
//                return null;
//            }
//
//        } else {
//            System.out.println("Keine NutzungCustomBean/Nutzungen für Vorgänger abgleich vorhanden");
//            return null;
//        }
//
//    }
    @Resource(name = "mail/nkf_mailaddress")
    private Session nkfMailer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of Serverbean.
     */
    public LagisServer() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * TODO ACTION NOT SUPPORTED EXCEPTION.
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckCustomBean createFlurstueck(final FlurstueckSchluesselCustomBean key) {
        try {
            if (key != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("createFlurstueck: key ist != null");
                }
                final FlurstueckSchluesselCustomBean checkedKey = completeFlurstueckSchluessel(key);
                if (checkedKey != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("createFlurstueck: Vervollständigter key ist != null");
                    }
                    return null;
                }
                final FlurstueckCustomBean newFlurstueck = new FlurstueckCustomBean();
                // datamodell refactoring 22.10.07
                final Date datumEntstehung = new Date();
                key.setEntstehungsDatum(datumEntstehung);
                key.setIstGesperrt(false);
                newFlurstueck.setFlurstueckSchluessel(key);
                // newFlurstueck.setEntstehungsDatum(new Date());
                // newFlurstueck.setIstGesperrt(false);
                checkIfFlurstueckWasStaedtisch(key, datumEntstehung);
                newFlurstueck.persist();
                if (LOG.isDebugEnabled()) {
                    // edit(newFlurstueck);
                    LOG.debug("createFlurstueck: neues Flurstück erzeugt");
                }
                return retrieveFlurstueck(key);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("createFlurstueck: key ist == null");
                }
                return null;
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim anlegen des Flurstücks", ex);
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueck  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void modifyFlurstueck(final FlurstueckCustomBean flurstueck) throws ActionNotSuccessfulException {
        try {
            processNutzungen(flurstueck.getNutzungen(), flurstueck.getFlurstueckSchluessel().getKeyString());
            checkIfFlurstueckWasStaedtisch(flurstueck.getFlurstueckSchluessel(), null);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Flurstück gespeichert");
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim speichern der Entität", ex);
            throw new ActionNotSuccessfulException("Fehler beim speichern eines vorhandenen Flurstücks", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   nutzungen      DOCUMENT ME!
     * @param   flurstueckKey  DOCUMENT ME!
     *
     * @throws  ErrorInNutzungProcessingException  DOCUMENT ME!
     */
    private void processNutzungen(final Collection<NutzungCustomBean> nutzungen, final String flurstueckKey)
            throws ErrorInNutzungProcessingException {
        try {
            // ToDo NKF Neue Kette Angelegt Mail etc ?? Testen
            if ((nutzungen != null) && (nutzungen.size() > 0)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Anzahl Ketten in aktuellem Flurstück: " + nutzungen.size());
                }
                final Date bookingDate = new Date();
                for (final NutzungCustomBean curNutzung : nutzungen) {
                    StringBuffer emailMessage = null;
                    String emailSubject = null;
                    final Collection<NutzungCustomBean.NUTZUNG_STATES> nutzungsState = curNutzung.getNutzungsState();
                    if (nutzungsState.isEmpty()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Keine Änderung");
                        }
                    } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.NUTZUNG_CREATED)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Neue Nutzung angelegt. Nachricht an Zuständige");
                        }
                        curNutzung.getBuchwert().setGueltigvon(bookingDate);
                        final StringBuilder message = new StringBuilder();
                        message.append("Bei dem Flurstück: ");
                        message.append(flurstueckKey);
                        message.append(" wurde eine neue Nutzung angelegt: \n\n");
                        message.append(curNutzung.getOpenBuchung().getPrettyString());
                        sendEmail("Lagis - Neue Nutzung", message.toString(), nkfSessions);
                    } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.NUTZUNG_CHANGED)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Nutzungskette wurde modifiziert "
                                        + Arrays.deepToString(nutzungsState.toArray()));
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Setzte Datum für die letzten beiden Buchungen");
                        }
                        curNutzung.getOpenBuchung().setGueltigvon(bookingDate);
                        curNutzung.getPreviousBuchung().setGueltigbis(bookingDate);
                        if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.NUTZUNGSART_CHANGED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Nutzungsart wurde geändert. Nachricht an Zuständige");
                            }
                            final StringBuilder message = new StringBuilder();
                            message.append("Bei dem Flurstück ");
                            message.append(flurstueckKey);
                            message.append(" wurde die Nutzungsart der Nutzung: \n\n");
                            message.append(curNutzung.getPreviousBuchung().getPrettyString());
                            message.append("\nwie folgt geändert:\n\n");
                            message.append(curNutzung.getOpenBuchung().getNutzungsart().getPrettyString());
                            sendEmail("Lagis - Änderung einer Nutzungsart", message.toString(), nkfSessions);
                        }
                        if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.STILLE_RESERVE_CREATED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Stille Reserve wurde gebildet. Nachricht an Zuständige");
                            }
                            emailMessage = new StringBuffer();
                            emailMessage.append("Bei dem Flurstück ");
                            emailMessage.append(flurstueckKey);
                            emailMessage.append(" wurde die Nutzung: \n\n");
                            emailMessage.append(curNutzung.getPreviousBuchung().getPrettyString());
                            emailMessage.append("\nwie folgt abgeändert:\n\n");
                            emailMessage.append(curNutzung.getOpenBuchung().getPrettyString());
                            emailMessage.append("\nEs wurde eine Stille Reserve in Höhe von: ")
                                    .append(currencyFormatter.format(curNutzung.getStilleReserve()))
                                    .append(" gebildet.");
                            emailSubject = "Lagis - Stille Reserve wurde gebildet";
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.STILLE_RESERVE_INCREASED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Vorhandene Stille Reserve wurde erhöht. Nachricht an Zuständige");
                            }
                            emailMessage = new StringBuffer();
                            emailMessage.append("Bei dem Flurstück ");
                            emailMessage.append(flurstueckKey);
                            emailMessage.append(" wurde die Nutzung: \n\n");
                            emailMessage.append(curNutzung.getPreviousBuchung().getPrettyString());
                            emailMessage.append("\nwie folgt abgeändert:\n\n");
                            emailMessage.append(curNutzung.getOpenBuchung().getPrettyString());
                            emailMessage.append("\nDie vorhandene Stille Reserve wurde um ")
                                    .append(currencyFormatter.format(curNutzung.getDifferenceToPreviousBuchung()))
                                    .append(" erhöht.");
                            emailSubject = "Lagis - Stille Reserve wurde erhöht";
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.STILLE_RESERVE_DECREASED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Vorhandene Stille Reserve wurde vermindert. Nachricht an Zuständige");
                            }
                            emailMessage = new StringBuffer();
                            emailMessage.append("Bei dem Flurstück ");
                            emailMessage.append(flurstueckKey);
                            emailMessage.append(" wurde die Nutzung: \n\n");
                            emailMessage.append(curNutzung.getPreviousBuchung().getPrettyString());
                            emailMessage.append("\nwie folgt abgeändert:\n\n");
                            emailMessage.append(curNutzung.getOpenBuchung().getPrettyString());
                            emailMessage.append("\nDie vorhandene Stille Reserve wurde um ")
                                    .append(currencyFormatter.format(
                                                Math.abs(curNutzung.getDifferenceToPreviousBuchung())))
                                    .append(" reduziert.");
                            emailSubject = "Lagis - Stille Reserve wurde reduziert";
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.STILLE_RESERVE_DISOLVED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                    "Vorhandene Stille Reserve wurde vollständig aufgebraucht. Nachricht an Zuständige");
                            }
                            emailMessage = new StringBuffer();
                            emailMessage.append("Bei dem Flurstück ");
                            emailMessage.append(flurstueckKey);
                            emailMessage.append(" wurde die Nutzung: \n\n");
                            emailMessage.append(curNutzung.getPreviousBuchung().getPrettyString());
                            emailMessage.append("\nwie folgt abgeändert:\n\n");
                            emailMessage.append(curNutzung.getOpenBuchung().getPrettyString());
                            emailMessage.append(
                                "\nEine Nutzung des Flurstücks wurde reduziert, die vorhandene Stille Reserve reicht nicht aus um die Differenz auszugleichen. ");
                            emailMessage.append("Die Stille Reserve wird aufgelöst.");
                            emailSubject = "Lagis - Stille Reserve wurde aufgelöst";
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.POSITIVE_BUCHUNG)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Positive Buchung ohne Stille Reserve. Nachricht an Zuständige");
                            }
                            emailMessage = new StringBuffer();
                            emailMessage.append("Bei dem Flurstück ");
                            emailMessage.append(flurstueckKey);
                            emailMessage.append(" wurde die Nutzung: \n\n");
                            emailMessage.append(curNutzung.getPreviousBuchung().getPrettyString());
                            emailMessage.append("\nwie folgt abgeändert:\n\n");
                            emailMessage.append(curNutzung.getOpenBuchung().getPrettyString());
                            emailMessage.append(
                                    "\nEs ist keine Stille Reserve vorhanden und es wurde keine gebildet.\n Der Gesamtpreis wurde um ")
                                    .append(currencyFormatter.format(curNutzung.getDifferenceToPreviousBuchung()))
                                    .append(" erhöht.");
                            emailSubject = "Lagis - Gesamtpreis einer Nutzung hat sich erhöht";
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.NEGATIVE_BUCHUNG)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Negative Buchung ohne Stille Reserve. Nachricht an Zuständige");
                            }
                            emailMessage = new StringBuffer();
                            emailMessage.append("Bei dem Flurstück ");
                            emailMessage.append(flurstueckKey);
                            emailMessage.append(" wurde die Nutzung: \n\n");
                            emailMessage.append(curNutzung.getPreviousBuchung().getPrettyString());
                            emailMessage.append("\nwie folgt abgeändert:\n\n");
                            emailMessage.append(curNutzung.getOpenBuchung().getPrettyString());
                            final StringBuffer append = emailMessage.append(
                                        "\nEs ist keine Stille Reserve vorhanden. Der Gesamtpreis wurde um ")
                                        .append(currencyFormatter.format(curNutzung.getDifferenceToPreviousBuchung()))
                                        .append(" reduziert.");
                            emailSubject = "Lagis - Gesamtpreis einer Nutzung hat sich vermindert";
                        }
                    } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.NUTZUNG_TERMINATED)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Nutzungskette wurde terminiert, setze Buchungsdatum");
                            LOG.debug("Benachrichtigung der Verantwortlichen Stelle über die Veränderung...");
                        }
                        emailMessage = new StringBuffer();
                        emailMessage.append("Bei dem Flurstück ");
                        emailMessage.append(flurstueckKey);
                        emailMessage.append(" wurde die Nutzung: \n\n");
                        emailMessage.append(curNutzung.getTerminalBuchung().getPrettyString());
                        emailMessage.append("\n\n");
                        emailMessage.append(
                            "abgeschlossen. Es sind keine weiteren Buchungen mehr möglich auf dieser Nutzung.");
                        emailSubject = "Lagis - Nutzung wurde gelöscht";
                        // ToDo Nachricht an Zuständige ?? gab es bisher
                        // curNutzung.terminateNutzung(bookingDate);
                        curNutzung.getTerminalBuchung().setGueltigbis(bookingDate);
                        // ToDo letzter Wert zum Buchwert setzen ?
                    } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.BUCHUNG_CREATED)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("neue Buchung . Nachricht an Zuständige");
                        }
                        emailMessage = new StringBuffer();
                        emailMessage.append("Bei dem Flurstück ");
                        emailMessage.append(flurstueckKey);
                        emailMessage.append(" wurde die Nutzung: \n\n");
                        emailMessage.append(curNutzung.getPreviousBuchung().getPrettyString());
                        emailMessage.append("\nwie folgt abgeändert:\n\n");
                        emailMessage.append(curNutzung.getOpenBuchung().getPrettyString());
                        emailSubject = "Lagis - Buchung wurde erzeugt";
                    } else {
                        throw new Exception("Kein Fall trifft auf Stati zu: "
                                    + Arrays.toString(nutzungsState.toArray()));
                    }
                    // Nur Stille Reserve --> NutzungsartCustomBean Email weiter oben
                    if (emailMessage != null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Es wird eine Nachricht an die zuständige Behörde geschickt");
                        }
                        sendEmail(emailSubject, emailMessage.toString(), nkfSessions);
                    }
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Flurstück besitzt keine Nutzungen.");
                }
            }
        } catch (Exception ex) {
            throw new ErrorInNutzungProcessingException("Nutzungen konnten nicht verarbeitet werden", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  subject   DOCUMENT ME!
     * @param  message   DOCUMENT ME!
     * @param  sessions  DOCUMENT ME!
     */
    private void sendEmail(final String subject,
            final String message, final Vector<Session> sessions) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Sende Email benachrichtigung");
        }
        try {
            final Properties lagisEEproperties = new Properties();
            // lagisEEproperties.load(getClass().getResourceAsStream("/de/cismet/lagis/eeServer/defaultLagisEEServer.properties"));

            // System.out.println("NKF EMAIL Address: "+lagisEEproperties.getProperty("nkf_Mailaddress"));
            // System.out.println("System Property test: "+System.getProperty("lagis.test"));
            // System.out.println("Session properties: "+nkfMailer.getProperties()); System.out.println("Session
            // Property: "+nkfMailer.getProperty("mail.lagis.test")); if
            // (lagisEEproperties.getProperty("nkf_Mailaddress") != null) {
            // System.out.println("Benachrichtigungsmailadresse gesetzt: " +
            // lagisEEproperties.getProperty("nkf_Mailaddress")); } else {
            // System.out.println("Benachrichtigungsmailadresse nicht gesetzt --> keine Mail"); return; }
            if (System.getProperty("lagis.nkfmail") != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Benachrichtigungsmailadresse gesetzt: " + System.getProperty("lagis.nkfmail"));
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Benachrichtigungsmailadresse nicht gesetzt --> keine Mail");
                }
                return;
            }
            // Problem with different auth --> see if multiple email addresses are needed
// MailAuthenticator auth = new MailAuthenticator("", "");

            final Iterator<Session> sessionItr = sessions.iterator();
            // if (sessions != null && sessions.size() > 0) {
            // while (sessionItr.hasNext()) {
            // sessionItr.next();
            // Properties properties = new Properties();
            // properties.put("mail.smtp.host", "smtp.uni-saarland.de");
            // Session mailer = Session.getDefaultInstance(properties, null);
            final javax.mail.Message msg = new MimeMessage(nkfMailer);
            msg.setFrom(new InternetAddress("sebastian.puhl@cismet.de"));
            // TODO Surround with try catch
            msg.setRecipients(
                javax.mail.Message.RecipientType.TO,
                InternetAddress.parse(System.getProperty("lagis.nkfmail"), false));
            msg.setSubject(subject);
            msg.setText(message);
            msg.setSentDate(new Date());
            Transport.send(msg);
            // }
            // } else {
            // System.out.println("Keine Session vorhanden");
            // }
        } catch (Exception ex) {
            LOG.error("Fehler beim senden einer email", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getCurrentDate() {
        return new Date();
    }
    /**
     * ToDo schlechter Namen.
     *
     * @param  key      DOCUMENT ME!
     * @param  useDate  DOCUMENT ME!
     */
    private void checkIfFlurstueckWasStaedtisch(final FlurstueckSchluesselCustomBean key, final Date useDate) {
        final FlurstueckArtCustomBean art = key.getFlurstueckArt();
        if (!key.getWarStaedtisch()) {
            // for(FlurstueckArtCustomBean current:getAllFlurstueckArten()){
            // TODO Checken ob korrekt mit Dirk absprechen
            if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(art.getBezeichnung())) {
                if (LOG.isDebugEnabled()) {
                    // if(FlurstueckArtCustomBean.FLURSTUECK_ART_EQUALATOR.pedanticEquals(art,current)){
                    LOG.debug("Flurstück ist Städtisch Datum letzter Stadtbesitz wird geupdated");
                }
                key.setWarStaedtisch(true);
                if (useDate != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Flurstück wurde neu angelegt und ist städtisch");
                    }
                    key.setDatumLetzterStadtbesitz(useDate);
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Flurstück war noch nie in Stadtbesitz und wird jetzt hinzugefügt");
                    }
                    final Date currentDate = new Date();
                    key.setDatumLetzterStadtbesitz(currentDate);
                    key.setEntstehungsDatum(currentDate);
                }
            }
            // }
        } else {
            if ((key.getFlurstueckArt() != null) && (key.getFlurstueckArt().getBezeichnung() != null)
                        && FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                            key.getFlurstueckArt().getBezeichnung())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Flurstück war und ist Städtisch --> Datum wird geupdated");
                }
                final Date currentDate = new Date();
                if (useDate != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Dieser Fall sollte nicht vorkommen");
                    }
                    key.setDatumLetzterStadtbesitz(useDate);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Datum letzter Stadt_besitz geupdated");
                    }
                } else {
                    key.setDatumLetzterStadtbesitz(currentDate);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Datum letzter Stadt_besitz geupdated");
                    }
                }
//TODO wird im Moment nur die Entstehung und gueltig_bis vom aktuellen Flurstück gespeichert

                final FlurstueckSchluesselCustomBean oldSchluessel = (FlurstueckSchluesselCustomBean)BrokerTester
                            .createCB(CidsAppBackend.CLASS__FLURSTUECK_SCHLUESSEL, key.getId());

                if ((oldSchluessel != null) && (oldSchluessel.getFlurstueckArt() != null)
                            && (oldSchluessel.getFlurstueckArt().getBezeichnung() != null)
                            && !FlurstueckArtCustomBean.FLURSTUECK_ART_EQUALATOR.pedanticEquals(
                                oldSchluessel.getFlurstueckArt(),
                                key.getFlurstueckArt())
                            && FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                                key.getFlurstueckArt().getBezeichnung())) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Flurstück kommt erneut in den Stadtbesitz --> entstehungsDatum wird geupdated");
                    }
                    if (useDate != null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("sollte nicht vorkkommen");
                        }
                        key.setEntstehungsDatum(useDate);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Datum Entstehung geupdated");
                        }
                    } else {
                        key.setEntstehungsDatum(currentDate);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Datum Entstehung geupdated");
                        }
                    }
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Kein wechsel von irgendeiner Flurstücksart nach städtisch --> kein Update");
                    }
                }
            }
        }
    }
    /**
     * Beim ändern einer Flurstück art wird zuerst diese Methode aufgerufen und dann die modify Flurstück --> deswegen
     * unterschiedliche Daten bei Entstehung und letzter Stadtbesizt.
     *
     * @param  key  DOCUMENT ME!
     */
    public void modifyFlurstueckSchluessel(final FlurstueckSchluesselCustomBean key) {
        try {
            final FlurstueckSchluesselCustomBean oldKey = completeFlurstueckSchluessel(key);
            FlurstueckArtCustomBean oldArt = null;
            if (oldKey != null) {
                oldArt = oldKey.getFlurstueckArt();
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Alterschlüssel ist == null");
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(("Art " + oldArt) != null);
                LOG.debug(("Bezeichnung " + oldArt.getBezeichnung()) != null);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Alter war staedtich "
                            + FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                                oldArt.getBezeichnung()));
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Art hat sich geändert "
                            + !FlurstueckArtCustomBean.FLURSTUECK_ART_EQUALATOR.pedanticEquals(
                                oldArt,
                                key.getFlurstueckArt()));
            }
            if ((oldArt != null) && (oldArt.getBezeichnung() != null)
                        && FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(oldArt.getBezeichnung())
                        && !FlurstueckArtCustomBean.FLURSTUECK_ART_EQUALATOR.pedanticEquals(
                            oldArt,
                            key.getFlurstueckArt())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                        "Die Art eines städtischen Flurstücks wurde auf eine andere geändert update lettzer Stadtbestizt Datum");
                }
                key.setWarStaedtisch(true);
                key.setDatumLetzterStadtbesitz(new Date());
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                        "Die Art eines Städtischen Flurstücks wurde nicht auf eine andere geändert --> checkIfFlurstueckWasStaedtisch");
                }
                checkIfFlurstueckWasStaedtisch(key, null);
            }
        } catch (final Throwable t) {
            LOG.error("Fehler beim speichern der Entität", t);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueck  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void deleteFlurstueck(final FlurstueckCustomBean flurstueck) throws ActionNotSuccessfulException {
        boolean illegalDelete = false;
        try {
            if (!illegalDelete) {
                for (final VerwaltungsbereichCustomBean current : flurstueck.getVerwaltungsbereiche()) {
                    if (current != null) {
                        illegalDelete = true;
                        break;
                    }
                }
            }
            if (!illegalDelete) {
                for (final NutzungCustomBean current : flurstueck.getNutzungen()) {
                    if (current != null) {
                        illegalDelete = true;
                        break;
                    }
                }
            }
            if (!illegalDelete) {
                for (final VertragCustomBean current : flurstueck.getVertraege()) {
                    if (current != null) {
                        illegalDelete = true;
                        break;
                    }
                }
            }
            if (!illegalDelete) {
                // ToDo check if successor are also interesting
                for (final FlurstueckHistorieCustomBean current
                            : getAllHistoryEntries(flurstueck.getFlurstueckSchluessel())) {
                    if (current != null) {
                        illegalDelete = true;
                        break;
                    }
                }
            }
            if (illegalDelete) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es sind daten für das Flurstück vorhanden es kann nicht gelöscht werden");
                }
                throw new ActionNotSuccessfulException(
                    "Es sind Daten für das Flurstück vorhanden, es kann nicht gelöscht werden");
            } else {
                flurstueck.delete();
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim löschen eines Flurstücks: " + flurstueck, ex);
            if (ex instanceof ActionNotSuccessfulException) {
                throw (ActionNotSuccessfulException)ex;
            }
        }
    }
    /**
     * Good? --> why not inline ???
     *
     * @param  newSchluessel  DOCUMENT ME!
     */
    private void createFlurstueckSchluessel(final FlurstueckSchluesselCustomBean newSchluessel) {
        try {
            newSchluessel.persist();
        } catch (Exception ex) {
            LOG.error("Fehler beim anlegen des Flurstückschlüssels: " + newSchluessel, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckCustomBean retrieveFlurstueck(final FlurstueckSchluesselCustomBean key) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Finde Flurstuck: ");
                LOG.debug("Id       : " + key.getId());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Gemarkung: " + key.getGemarkung());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Flur     : " + key.getFlur());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Zaehler  : " + key.getFlurstueckZaehler());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Nenner   : " + key.getFlurstueckNenner());
            }

            final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("flurstueck");
            if (metaclass == null) {
                return null;
            }
            final MetaObject[] mos = CidsAppBackend.getInstance()
                        .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                            + metaclass.getPrimaryKey() + " "
                            + "FROM " + metaclass.getTableName() + " "
                            + "WHERE flursteck.fk_flurstueck_schluessel = " + key.getId());

            if ((mos != null) && (mos.length > 0)) {
                if (mos.length > 1) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Anzahl Flurstuecke: " + mos.length);
                    }
                    throw new Exception("Multiple Flurstuecke should only be one");
                } else {
                    final FlurstueckCustomBean result = (FlurstueckCustomBean)mos[0];

                    final Collection<VertragCustomBean> vertrage = result.getVertraege();
                    if ((vertrage != null) && (vertrage.size() > 0)) {
                        final Collection<FlurstueckSchluesselCustomBean> resultKeys = getCrossreferencesForVertraege(
                                vertrage);
                        if (resultKeys != null) {
                            resultKeys.remove(result.getFlurstueckSchluessel());
                        }
                        result.setVertraegeQuerverweise(resultKeys);
                    }

                    final Collection<MipaCustomBean> miPas = result.getMiPas();
                    if ((miPas != null) && (miPas.size() > 0)) {
                        final Collection<FlurstueckSchluesselCustomBean> resultKeys = getCrossreferencesForMiPas(miPas);
                        if (resultKeys != null) {
                            resultKeys.remove(result.getFlurstueckSchluessel());
                        }
                        result.setMiPasQuerverweise(resultKeys);
                    }

                    final Collection<BaumCustomBean> baueme = result.getBaeume();
                    if ((baueme != null) && (baueme.size() > 0)) {
                        final Collection<FlurstueckSchluesselCustomBean> resultKeys = getCrossreferencesForBaeume(
                                baueme);
                        if (resultKeys != null) {
                            resultKeys.remove(result.getFlurstueckSchluessel());
                        }
                        result.setBaeumeQuerverweise(resultKeys);
                    }

                    return result;
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim abfragen des Flurstuecks: " + key, ex);
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isFlurstueckHistoric(final FlurstueckSchluesselCustomBean key) {
        // datamodell refactoring 22.10.07
        // Flurstueck flurstueck = retrieveFlurstueck(key);
        // if(flurstueck.getGueltigBis() != null){
        if (key.getGueltigBis() != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueckHistorie  DOCUMENT ME!
     */
    public void createFlurstueckHistoryEntry(final FlurstueckHistorieCustomBean flurstueckHistorie) {
        try {
            flurstueckHistorie.persist();
        } catch (Exception ex) {
            LOG.error("Fehler beim anlegen der Flurstueckshistorie: " + flurstueckHistorie, ex);
        }
    }
    // TODO ActionNotSuccessfulException
    // MUss Fehler her z.b. Wenn nicht gelöscht werden kann
    // TODO locking

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public boolean setFlurstueckHistoric(final FlurstueckSchluesselCustomBean key) throws ActionNotSuccessfulException {
        return setFlurstueckHistoric(key, new Date());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key   DOCUMENT ME!
     * @param   date  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public boolean setFlurstueckHistoric(final FlurstueckSchluesselCustomBean key, final Date date)
            throws ActionNotSuccessfulException {
        // datamodell refactoring 22.10.07
        try {
            if (key.getWarStaedtisch()) {
                if (LOG.isDebugEnabled()) {
                    // TODO hier muss wieder städtisch gesetzt werden und die ReBe gelöscht werden
                    LOG.debug("Flurstueck war schon mal staedtisch wird historisch gesetzt");
                }
                // Flurstueck flurstueck = retrieveFlurstueck(key);
                // if(flurstueck.getGueltigBis() == null){
                if (key.getGueltigBis() == null) {
                    if (
                        !FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                                    key.getFlurstueckArt().getBezeichnung())) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Flurstueck ist nicht städtisch");
                        }
                        FlurstueckArtCustomBean abteilungIX = null;
                        for (final FlurstueckArtCustomBean current : getAllFlurstueckArten()) {
                            if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX.equals(
                                            current.getBezeichnung())) {
                                abteilungIX = current;
                            }
                        }
                        if (abteilungIX == null) {
                            throw new ActionNotSuccessfulException(
                                "Flurstücksart AbteilungIX konnte nicht gefunden werden.");
                        }

                        if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX.equals(
                                        key.getFlurstueckArt().getBezeichnung())) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Flurstück ist Abteilung IX  --> alle Rechte werden entfernt");
                            }
                            final FlurstueckCustomBean flurstueck = retrieveFlurstueck(key);
                            if (flurstueck.getRechteUndBelastungen() != null) {
                                flurstueck.getRechteUndBelastungen().clear();
                            }

                            flurstueck.getFlurstueckSchluessel().setFlurstueckArt(abteilungIX);
                            if (flurstueck.getFlurstueckSchluessel().getDatumLetzterStadtbesitz() != null) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Setze Gueltigbis Datum des Flurstueks auf letzten Stadtbesitz");
                                }
                                flurstueck.getFlurstueckSchluessel()
                                        .setGueltigBis(flurstueck.getFlurstueckSchluessel()
                                            .getDatumLetzterStadtbesitz());
                            } else {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Achtung war schon in Stadtbesitz hat aber kein Datum");
                                }
                                throw new ActionNotSuccessfulException(
                                    "Das Flurstück war schon mal in Stadtbesitz, aber es existiert kein Datum wann");
                            }
//flurstueck.getFlurstueckSchluessel().setGueltigBis(new Date());
                            // return new HistoricResult(true,false);
                            return true;
                                // }
                        }
                        throw new ActionNotSuccessfulException("Die Flurstückart "
                                    + FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH
                                    + " ist nicht in der Datenbank");
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Flurstück ist städtisch und wird historisch gesetzt");
                        }
                        key.setDatumLetzterStadtbesitz(date);
                        key.setGueltigBis(date);
                        final FlurstueckCustomBean flurstueck = retrieveFlurstueck(key);
                        if (flurstueck != null) {
//TODO Nutzungsrefactoring
                            // setCurrentNutzungenHistoric(flurstueck.getNutzungen(), currentDate);
                        }
                        flurstueck.setFlurstueckSchluessel(key);
                        // return new HistoricResult(true,false);
                        return true;
                    }
                } else {
                    // return new HistoricResult(false,false);
                    return true;
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Flurstueck war noch nie staedtisch wird historisch gesetzt");
                }
                // deleteFlurstueck(retrieveFlurstueck(key));
                key.setGueltigBis(date);
                // return new HistoricResult(true,true);
                return true;
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim historisch setzen eines Flurstücks", ex);
//            ActionNotSuccessfulException tmpEx = new ActionNotSuccessfulException("Flurstück konnte nicht historisch gesetzt werden");
//            tmpEx.setStackTrace(ex.getStackTrace());
//            throw tmpEx;
            if (ex instanceof ActionNotSuccessfulException) {
                throw (ActionNotSuccessfulException)ex;
            } else {
                throw new ActionNotSuccessfulException(
                    "Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator.",
                    ex);
            }
        }
    }
    /**
     * public void setCurrentNutzungenHistoric(final Set<Nutzung> nutzungen, final Date gueltigBis) {
     * System.out.println("Setze aktuelle Flurstücknutzungen historisch"); if (nutzungen != null) { for
     * (NutzungCustomBean curNutzung : nutzungen) { if (curNutzung != null && curNutzung.getGueltigbis() == null) {
     * System.out.println("Gegenwärtige NutzungCustomBean ist eine aktuelle NutzungCustomBean --> wird historisch
     * gesetzt: " + curNutzung); curNutzung.setGueltigbis(gueltigBis); } else { System.out.println("Gegenwärtige
     * NutzungCustomBean entweder null oder nicht aktuell: " + curNutzung); } } } else { System.out.println("Nutzungen
     * == null"); } }
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public boolean setFlurstueckActive(final FlurstueckSchluesselCustomBean key) throws ActionNotSuccessfulException {
        try {
            if (key.getGueltigBis() != null) {
                if (!hasFlurstueckSucccessors(key)) {
                    if ((key.getFlurstueckArt() == null) || (key.getFlurstueckArt().getBezeichnung() == null)) {
                        throw new ActionNotSuccessfulException(
                            "Das Flurstück kann nicht aktiviert werden, weil es keine Flurstücksart besitzt");
                    }

                    if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                                    key.getFlurstueckArt().getBezeichnung())) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Städtisches Flurstück wurde reactiviert");
                        }
                        final Date currentDate = new Date();
                        key.setEntstehungsDatum(currentDate);
                        key.setDatumLetzterStadtbesitz(currentDate);
                    }

                    key.setGueltigBis(null);
                    return true;
                } else {
                    throw new ActionNotSuccessfulException(
                        "Das Flurstück kann nicht aktiviert werden, weil es Nachfolger hat");
                }
            } else {
                throw new ActionNotSuccessfulException("Das Flurstück war aktiv");
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim historisch setzen eines Flurstücks", ex);
            throw new ActionNotSuccessfulException(
                "Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator.",
                ex);
        }
    }
    /**
     * TODO Muss man prüfen ob ein Eintrag schon historisch ist ?
     *
     * @param  oldFlurstueck  DOCUMENT ME!
     * @param  newFlurstueck  DOCUMENT ME!
     */
    private void createHistoryEdge(final FlurstueckCustomBean oldFlurstueck, final FlurstueckCustomBean newFlurstueck) {
        final FlurstueckHistorieCustomBean historyEntry = new FlurstueckHistorieCustomBean();
        historyEntry.setVorgaenger(oldFlurstueck);
        historyEntry.setNachfolger(newFlurstueck);
        createFlurstueckHistoryEntry(historyEntry);
    }
//TODO Selbe wie successor
//TODO BADNAME --> Dachte würde irgeneine Kante auspucken nicht nur wo der Vorgänger flurstueckToCheck ist --> rename

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueckToCheck  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean existHistoryEntry(final FlurstueckCustomBean flurstueckToCheck) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suche History Einträge");
        }
        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("flurstueck_historie");
        if (metaclass == null) {
            return false;
        }
        final String query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                    + metaclass.getTableName() + "." + metaclass.getPrimaryKey() + " "
                    + "FROM " + metaclass.getTableName() + " "
                    + "WHERE flurstueck_historie.fk_vorgaenger = " + flurstueckToCheck.getId();

        final MetaObject[] mos = CidsAppBackend.getInstance().getLagisMetaObject(query);
        if ((mos != null) && (mos.length > 0)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Es existiert ein History Eintrag");
                LOG.debug("Es gibt schon einen Nachfolger");
            }
            return true;
        } else {
            return false;
        }
    }

    // ToDo change to Flurstueckschluessel
    /**
     * Delivers all Flurstuecke which are predecessors to the given Flurstueckschluessel.
     *
     * @param   flurstueckSchluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<FlurstueckHistorieCustomBean> getHistoryAccessors(
            final FlurstueckSchluesselCustomBean flurstueckSchluessel) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suche Vorgänger für Flurstück");
            LOG.debug("ID des Schluessels ist: " + flurstueckSchluessel);
        }

        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("flurstueck_historie");
        if (metaclass == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + metaclass.getID() + ", "
                    + "   " + metaclass.getTableName() + "." + metaclass.getPrimaryKey() + " "
                    + "FROM "
                    + "   " + metaclass.getTableName() + ", "
                    + "   flurstueck "
                    + "WHERE "
                    + "   flurstueck_historie.fk_nachfolger = flurstueck.id "
                    + "   AND flurstueck.fk_flurstueck_schluessel = " + flurstueckSchluessel.getId();

        final MetaObject[] mos = CidsAppBackend.getInstance().getLagisMetaObject(query);
        final Collection<FlurstueckHistorieCustomBean> historyEntries = new HashSet<FlurstueckHistorieCustomBean>();
        for (final MetaObject metaObject : mos) {
            historyEntries.add((FlurstueckHistorieCustomBean)metaObject.getBean());
        }
        if (historyEntries != null) {
            if (historyEntries.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ergebnisliste ist leer");
                }
                return null;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Suche lieferte mindestens ein Ergebnis zurück");
                }
            }

//                while(it.hasNext()){
//                    FlurstueckHistorieCustomBean curHistoryEntry = it.next();
//                    //TODO possible that a key is null (inconsitence) ??
//                    if(curHistoryEntry != null && curHistoryEntry.getVorgaenger() != null){
//                        System.out.println("Jetziger HistoryEintrag != null und Vorgänger != null");
//                        result.add(curHistoryEntry.getVorgaenger().getFlurstueckSchluessel());
//                    } else {
//                        //TODO EXCEPTION
//                        System.out.println("Jetziger HistoryEintrag oder Vorgänger == null");
//                    }
//                }
            return historyEntries;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Suche lieferte kein Ergebnis zurück");
            }
            return null;
        }
    }

//ToDo change to Flurstueckschluessel
    /**
     * Delivers all Flurstuecke which are successor to the given Flurstueckschluessel.
     *
     * @param   flurstueckSchluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<FlurstueckHistorieCustomBean> getHistorySuccessor(
            final FlurstueckSchluesselCustomBean flurstueckSchluessel) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suche Nachfolger für Flurstück");
        }

        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("flurstueck_historie");
        if (metaclass == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + metaclass.getID() + ", "
                    + "   " + metaclass.getTableName() + "." + metaclass.getPrimaryKey() + " "
                    + "FROM "
                    + "   " + metaclass.getTableName() + ", "
                    + "   flurstueck "
                    + "WHERE "
                    + "   flurstueck_historie.fk_nachfolger = flurstueck.id "
                    + "   AND flurstueck.fk_flurstueck_schluessel = " + flurstueckSchluessel.getId();

        final MetaObject[] mos = CidsAppBackend.getInstance().getLagisMetaObject(query);
        final Collection<FlurstueckHistorieCustomBean> historyEntries = new HashSet<FlurstueckHistorieCustomBean>();
        for (final MetaObject metaObject : mos) {
            historyEntries.add((FlurstueckHistorieCustomBean)metaObject.getBean());
        }

        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("ID des Schluessels ist: " + flurstueckSchluessel);
            }
            if (historyEntries != null) {
                if (historyEntries.isEmpty()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Ergebnisliste ist leer");
                    }
                    return null;
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Suche lieferte mindestens ein Ergebnis zurück");
                    }
                }

//                while(it.hasNext()){
//                    FlurstueckHistorieCustomBean curHistoryEntry = it.next();
//                    //TODO possible that a key is null (inconsitence) ??
//                    if(curHistoryEntry != null && curHistoryEntry.getVorgaenger() != null){
//                        System.out.println("Jetziger HistoryEintrag != null und Vorgänger != null");
//                        result.add(curHistoryEntry.getVorgaenger().getFlurstueckSchluessel());
//                    } else {
//                        //TODO EXCEPTION
//                        System.out.println("Jetziger HistoryEintrag oder Vorgänger == null");
//                    }
//                }
                return historyEntries;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Suche lieferte kein Ergebnis zurück");
                }
                return null;
            }
        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Fehler beim suchen der Nachfolger eines Flurstücks", ex);
            }
        }

        return null;
    }
    /**
     * selbe wie existHistoryEntry.
     *
     * @param   flurstueckSchluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public boolean hasFlurstueckSucccessors(final FlurstueckSchluesselCustomBean flurstueckSchluessel)
            throws ActionNotSuccessfulException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suche Nachfolger für Flurstück");
            LOG.debug("ID des Schluessels ist: " + flurstueckSchluessel.getId());
        }

        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("flurstueck_historie");
        if (metaclass == null) {
            return false;
        }
        final String query = "SELECT "
                    + "   " + metaclass.getID() + ", "
                    + "   " + metaclass.getTableName() + "." + metaclass.getPrimaryKey() + " "
                    + "FROM "
                    + "   " + metaclass.getTableName() + ", "
                    + "   flurstueck "
                    + "WHERE "
                    + "   flurstueck_historie.fk_vorgaenger = flurstueck.id "
                    + "   AND flurstueck.fk_flurstueck_schluessel = " + flurstueckSchluessel.getId();

        final MetaObject[] mos = CidsAppBackend.getInstance().getLagisMetaObject(query);
        if (mos != null) {
            if (mos.length > 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ergebnisliste ist leer");
                }
                return false;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Suche lieferte mindestens ein Ergebnis zurück");
                }
                return true;
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Suche lieferte kein Ergebnis zurück");
            }
            return false;
        }
    }
    /**
     * ToDo parametrize method level of predecessor/successsor ToDo mode predecessor/successor/both ToDo --> replace by
     * parametrized method.
     *
     * @param   schluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckHistorieCustomBean> getAllHistoryEntries(
            final FlurstueckSchluesselCustomBean schluessel) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Sammle Alle Knoten (Rekursiv) für: " + schluessel);
        }
        final Collection<FlurstueckHistorieCustomBean> allEdges = new HashSet<FlurstueckHistorieCustomBean>();
        try {
            Collection<FlurstueckHistorieCustomBean> childEdges = getHistoryAccessors(schluessel);
            if (childEdges != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es gibt Kanten zu diesem Knoten");
                }
                allEdges.addAll(childEdges);
                final Iterator<FlurstueckHistorieCustomBean> it = childEdges.iterator();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Rufe Methode Rekursiv auf für alle Gefundenen Knoten");
                }
                while (it.hasNext()) {
                    childEdges = getAllHistoryEntries(it.next().getVorgaenger().getFlurstueckSchluessel());
                    if (childEdges != null) {
                        allEdges.addAll(childEdges);
                    }
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es gibt keine Kanten zu diesem Knoten");
                }
                return allEdges;
            }

            return allEdges;
        } catch (Exception ex) {
            LOG.error("Fehler beim sammeln aller Kanten", ex);
        }

        return allEdges;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<GemarkungCustomBean> getGemarkungsKeys() {
        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("gemarkung");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsAppBackend.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " " + "FROM " + metaclass.getTableName());
        final Collection<GemarkungCustomBean> beans = new HashSet<GemarkungCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((GemarkungCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key         DOCUMENT ME!
     * @param   level       DOCUMENT ME!
     * @param   type        DOCUMENT ME!
     * @param   levelCount  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public Collection<FlurstueckHistorieCustomBean> getHistoryEntries(
            final FlurstueckSchluesselCustomBean key,
            final HistoryLevel level,
            final HistoryType type,
            int levelCount) throws ActionNotSuccessfulException {
        final Collection<FlurstueckHistorieCustomBean> allEdges = new HashSet<FlurstueckHistorieCustomBean>();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Historiensuche mit Parametern: HistoryType=" + type + " Level=" + level + " LevelCount="
                        + levelCount);
        }
        try {
            switch (type) {
                case BOTH: {
                    switch (level) {
                        case DIRECT_RELATIONS: {
                            levelCount = 1;
                            addHistoryEntriesForNeighbours(key, level, HistoryType.SUCCESSOR, levelCount, allEdges);
                            addHistoryEntriesForNeighbours(key, level, HistoryType.PREDECESSOR, levelCount, allEdges);
                            break;
                        }
                        case CUSTOM: {
                            addHistoryEntriesForNeighbours(key, level, HistoryType.PREDECESSOR, levelCount, allEdges);
                            addHistoryEntriesForNeighbours(key, level, HistoryType.SUCCESSOR, levelCount, allEdges);
                            break;
                        }
                        default: {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Sammle Alle Knoten Vorgänger/Nachfolger (Rekursiv) für: " + key);
                            }
                            addHistoryEntriesForNeighbours(key, level, HistoryType.PREDECESSOR, -1, allEdges);
                            addHistoryEntriesForNeighbours(key, level, HistoryType.SUCCESSOR, -1, allEdges);
                        }
                    }
                    break;
                }
                case SUCCESSOR: {
                    switch (level) {
                        case DIRECT_RELATIONS: {
                            levelCount = 1;
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Sammle m Knoten Nachfolger (Rekursiv) für: " + key);
                            }
                            addHistoryEntriesForNeighbours(key, level, type, levelCount, allEdges);
                            break;
                        }
                        case CUSTOM: {
                            addHistoryEntriesForNeighbours(key, level, type, levelCount, allEdges);
                            break;
                        }
                        default: {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Sammle Alle/Custom Knoten Nachfolger (Rekursiv) für: " + key);
                            }
                            addHistoryEntriesForNeighbours(key, level, type, -1, allEdges);
                        }
                    }
                    break;
                }
                case PREDECESSOR: {
                    switch (level) {
                        case DIRECT_RELATIONS: {
                            levelCount = 1;
                            addHistoryEntriesForNeighbours(key, level, type, levelCount, allEdges);
                            break;
                        }
                        case CUSTOM: {
                            addHistoryEntriesForNeighbours(key, level, type, levelCount, allEdges);
                            break;
                        }
                        default: {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Sammle Alle/Custom Knoten Vorgänger (Rekursiv) für: " + key);
                            }
                            addHistoryEntriesForNeighbours(key, level, type, -1, allEdges);
                        }
                    }

                    break;
                }
                default: {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("HistoryType is not defined");
                    }
                    throw new ActionNotSuccessfulException("Fehler beim abfragen der Historieneinträge.");
                }
            }
        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failure during querying of history entries", ex);
            }
            throw new ActionNotSuccessfulException("Fehler beim abfragen der Historieneinträge.", ex);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Historien Suche abgeschlossen");
        }
        return allEdges;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key         DOCUMENT ME!
     * @param   level       DOCUMENT ME!
     * @param   type        DOCUMENT ME!
     * @param   levelCount  DOCUMENT ME!
     * @param   allEdges    DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    private void addHistoryEntriesForNeighbours(final FlurstueckSchluesselCustomBean key,
            final HistoryLevel level,
            final HistoryType type,
            int levelCount,
            final Collection<FlurstueckHistorieCustomBean> allEdges) throws ActionNotSuccessfulException {
        Collection<FlurstueckHistorieCustomBean> foundEdges;
        Collection<FlurstueckHistorieCustomBean> neighbours;

        if (type == HistoryType.PREDECESSOR) {
            neighbours = getHistoryAccessors(key);
        } else {
            neighbours = getHistorySuccessor(key);
        }

        if ((neighbours != null) && (type == HistoryType.PREDECESSOR)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Es gibt Vorgängerkanten zu diesem Knoten");
            }
            replacePseudoFlurstuecke(neighbours, allEdges, type);
            allEdges.addAll(neighbours);
        } else if ((neighbours != null) && (type == HistoryType.SUCCESSOR)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Es gibt Nachfoglerkanten zu diesem Knoten");
            }
            replacePseudoFlurstuecke(neighbours, allEdges, type);
            allEdges.addAll(neighbours);
        } else if (type == HistoryType.SUCCESSOR) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Es gibt keine Nachfolgerkanten zu diesem Knoten");
            }
            return;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Es gibt keine Vorgängerkanten zu diesem Knoten");
            }
            return;
        }

        if (level != HistoryLevel.All) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("HistoryLevel = " + level);
            }
            if (levelCount > 0) {
                levelCount = --levelCount;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("LevelCount " + (levelCount + 1) + " wurde um eins reduziert auf " + levelCount);
                }
                if (levelCount == 0) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Anzahl zu suchender Levels erschöpft kehre zurück");
                    }
                    return;
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Anzahl zu suchender Levels erschöpft kehre zurück");
                }
                return;
            }
        }

        final Iterator<FlurstueckHistorieCustomBean> itr = neighbours.iterator();

        while (itr.hasNext()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Suche rekursiv nach weiteren Vorgängern");
            }
            if (type == HistoryType.PREDECESSOR) {
                foundEdges = getHistoryEntries(itr.next().getVorgaenger().getFlurstueckSchluessel(),
                        level,
                        type,
                        levelCount);
            } else {
                foundEdges = getHistoryEntries(itr.next().getNachfolger().getFlurstueckSchluessel(),
                        level,
                        type,
                        levelCount);
            }
            if (foundEdges != null) {
                allEdges.addAll(foundEdges);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Rekursive suche brachte keine Ergebnisse");
                }
            }
        }

        if (level != HistoryLevel.All) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("HistoryLevel = " + level);
            }
            if (levelCount > 0) {
                levelCount = --levelCount;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("LevelCount " + (levelCount + 1) + " wurde um eins reduziert auf " + levelCount);
                }
                if (levelCount == 0) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Anzahl zu suchender Levels erschöpft kehre zurück");
                    }
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Anzahl zu suchender Levels erschöpft kehre zurück");
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueckHistorie  DOCUMENT ME!
     * @param  allEdges            DOCUMENT ME!
     * @param  direction           DOCUMENT ME!
     */
    private void replacePseudoFlurstuecke(final Collection<FlurstueckHistorieCustomBean> flurstueckHistorie,
            final Collection<FlurstueckHistorieCustomBean> allEdges,
            final HistoryType direction) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("replacePseudoFlurstuecke: direction=" + direction + " Kanten=" + flurstueckHistorie);
        }
        if (flurstueckHistorie != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("es existieren Kanten");
            }
            final Iterator<FlurstueckHistorieCustomBean> itr = flurstueckHistorie.iterator();
            final ArrayList<FlurstueckHistorieCustomBean> pseudoKeysToRemove =
                new ArrayList<FlurstueckHistorieCustomBean>();
            final Collection<FlurstueckHistorieCustomBean> realNeighbours = new HashSet<FlurstueckHistorieCustomBean>();
            while (itr.hasNext()) {
                final FlurstueckHistorieCustomBean currentFlurstueckHistorie = itr.next();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("" + currentFlurstueckHistorie.getNachfolger());
                }
                if ((direction == HistoryType.PREDECESSOR) && (currentFlurstueckHistorie.getNachfolger() != null)
                            && (currentFlurstueckHistorie.getVorgaenger() != null)
                            && (currentFlurstueckHistorie.getVorgaenger().getFlurstueckSchluessel() != null)
                            && !currentFlurstueckHistorie.getVorgaenger().getFlurstueckSchluessel()
                            .isEchterSchluessel()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "Vorgänger ist ein PseudoFlurstück und besitzt vorgänge --> suche heraus und ersetze");
                    }
                    allEdges.add(currentFlurstueckHistorie);
                    pseudoKeysToRemove.add(currentFlurstueckHistorie);
                    final Collection<FlurstueckHistorieCustomBean> result = getHistoryAccessors(
                            currentFlurstueckHistorie.getVorgaenger().getFlurstueckSchluessel());
                    if (result != null) {
                        realNeighbours.addAll(result);
                    }
                } else if ((direction == HistoryType.SUCCESSOR) && (currentFlurstueckHistorie.getNachfolger() != null)
                            && (currentFlurstueckHistorie.getVorgaenger() != null)
                            && (currentFlurstueckHistorie.getNachfolger().getFlurstueckSchluessel() != null)
                            && !currentFlurstueckHistorie.getNachfolger().getFlurstueckSchluessel()
                            .isEchterSchluessel()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "Vorgänger ist ein PseudoFlurstück und besitzt vorgänge --> suche heraus und ersetze");
                    }
                    allEdges.add(currentFlurstueckHistorie);
                    pseudoKeysToRemove.add(currentFlurstueckHistorie);
                    final Collection<FlurstueckHistorieCustomBean> result = getHistorySuccessor(
                            currentFlurstueckHistorie.getNachfolger().getFlurstueckSchluessel());
                    if (result != null) {
                        realNeighbours.addAll(result);
                    }
                }
            }
            flurstueckHistorie.removeAll(pseudoKeysToRemove);
            flurstueckHistorie.addAll(realNeighbours);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Pseudoflurstücke zum ersetzen");
            }
        }
    }
    /**
     * TODO Refactor method --> should not be so big.
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<Key> getDependingKeysForKey(final Key key) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("GetDependingKeysForKey");
        }
        try {
            if (key != null) {
                if (key instanceof GemarkungCustomBean) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Key ist Gemarkung");
                    }
                    final GemarkungCustomBean currentGemarkung = (GemarkungCustomBean)key;
                    if ((currentGemarkung != null) && (currentGemarkung.getSchluessel() != null)) {
                        // TODO Duplicated code --> extract

                        final MetaClass metaclass = CidsAppBackend.getInstance()
                                    .getLagisMetaClass("flurstueck_schluessel");
                        if (metaclass == null) {
                            return null;
                        }
                        final String query = "SELECT DISTINCT "
                                    + "   " + metaclass.getID() + ", "
                                    + "   " + metaclass.getTableName() + "." + metaclass.getPrimaryKey() + " "
                                    + "FROM "
                                    + "   " + metaclass.getTableName() + ", "
                                    + "   gemarkung "
                                    + "WHERE "
                                    + "    flurstueck_schluessel.fk_gemarkung = gemarkung.id "
                                    + "    AND flurstueck_schluessel.fk_flurstueck_art != 3 "
                                    + "    AND gemarkung.schluessel = " + currentGemarkung.getSchluessel();
                        final MetaObject[] mos = CidsAppBackend.getInstance().getLagisMetaObject(query);
                        if (mos != null) {
                            final Collection flurKeys = new HashSet();
                            for (final MetaObject mo : mos) {
                                flurKeys.add(new FlurKey(currentGemarkung, mo.getId()));
                            }
                        } else {
                            return new HashSet();
                        }
                    } else if ((currentGemarkung != null) && (currentGemarkung.getBezeichnung() != null)) {
                        final GemarkungCustomBean completed = completeGemarkung(currentGemarkung);
                        if (completed != null) {
                            final MetaClass metaclass = CidsAppBackend.getInstance()
                                        .getLagisMetaClass("flustueck_schluessel");
                            if (metaclass == null) {
                                return null;
                            }
                            final String query = "SELECT DISTINCT "
                                        + "   " + metaclass.getID() + ", "
                                        + "   " + metaclass.getTableName() + "." + metaclass.getPrimaryKey() + " "
                                        + "FROM "
                                        + "   " + metaclass.getTableName() + ", "
                                        + "   gemarkung "
                                        + "WHERE "
                                        + "    flurstueck_schluessel.fk_gemarkung = gemarkung.id "
                                        + "    AND flurstueck_schluessel.fk_flurstueck_art != 3 "
                                        + "    AND gemarkung.schluessel = " + completed.getSchluessel();
                            final MetaObject[] mos = CidsAppBackend.getInstance().getLagisMetaObject(query);
                            if (mos != null) {
                                final Collection flurKeys = new HashSet();
                                for (final MetaObject mo : mos) {
                                    flurKeys.add(new FlurKey(currentGemarkung, mo.getId()));
                                }
                            } else {
                                return new HashSet();
                            }
                        } else {
                            return new HashSet();
                        }
                    } else {
                        return new HashSet();
                    }
                } else if (key instanceof FlurKey) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Key ist Flur");
                    }
                    final FlurKey currentFlur = (FlurKey)key;

                    final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("sperre");
                    if (metaclass == null) {
                        return null;
                    }
                    String query = null;

                    // TODDO WHY INTEGER
                    if (!currentFlur.isCurrentFilterEnabled() && !currentFlur.isHistoricFilterEnabled()
                                && !currentFlur.isAbteilungXIFilterEnabled()
                                && !currentFlur.isStaedtischFilterEnabled()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Kein Filter für Flur Aktiviert");
                        }
                        query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                                    + metaclass.getPrimaryKey() + " "
                                    + "FROM " + metaclass.getTableName() + " "
                                    + "WHERE "
                                    + "   flurstueck_schluessel.fk_gemarkung = :gId "
                                    + "   AND flurstueck_schluessel.flur = :fId "
                                    + "   AND flurstueck_schluessel.fk_flurstueck_art != 3";
                    } else if (currentFlur.isCurrentFilterEnabled()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Filter nur aktuelle Flurstücke: Aktiviert");
                        }
                        query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                                    + metaclass.getPrimaryKey() + " "
                                    + "FROM " + metaclass.getTableName() + " "
                                    + "WHERE "
                                    + "   flurstueck_schluessel.fk_gemarkung = :gId "
                                    + "   AND flurstueck_schluessel.flur = :fId "
                                    + "   AND flurstueck_schluessel.fk_flurstueck_art != 3 "
                                    + "   AND flurstueck_schluessel.gueltig_bis IS NULL";
                    } else if (currentFlur.isHistoricFilterEnabled()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Filter nur historische Flurstücke: Aktiviert");
                        }
                        query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                                    + metaclass.getPrimaryKey() + " "
                                    + "FROM " + metaclass.getTableName() + " "
                                    + "WHERE "
                                    + "   flurstueck_schluessel.fk_gemarkung = :gId "
                                    + "   AND flurstueck_schluessel.flur = :fId "
                                    + "   AND flurstueck_schluessel.fk_flurstueck_art != 3 "
                                    + "   AND flurstueck_schluessel.gueltig_bis IS NOT NULL";
                    } else if (currentFlur.isAbteilungXIFilterEnabled()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Filter nur Abteilung IX Flurstücke: Aktiviert");
                        }
                        query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                                    + metaclass.getPrimaryKey() + " "
                                    + "FROM " + metaclass.getTableName() + " "
                                    + "WHERE "
                                    + "   flurstueck_schluessel.fk_gemarkung = :gId "
                                    + "   AND flurstueck_schluessel.flur = :fId "
                                    + "   AND flurstueck_schluessel.fk_flurstueck_art != 2";
                    } else if (currentFlur.isStaedtischFilterEnabled()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Filter nur staedtische Flurstücke: Aktiviert");
                        }
                        query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                                    + metaclass.getPrimaryKey() + " "
                                    + "FROM " + metaclass.getTableName() + " "
                                    + "WHERE "
                                    + "   flurstueck_schluessel.fk_gemarkung = :gId "
                                    + "   AND flurstueck_schluessel.flur = :fId "
                                    + "   AND flurstueck_schluessel.fk_flurstueck_art != 1";
                    }
                    if (query != null) {
                        final MetaObject[] mos = CidsAppBackend.getInstance().getLagisMetaObject(query);
                        final Collection<FlurstueckSchluesselCustomBean> flurstuecke =
                            new HashSet<FlurstueckSchluesselCustomBean>();
                        for (final MetaObject metaObject : mos) {
                            flurstuecke.add((FlurstueckSchluesselCustomBean)metaObject.getBean());
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Ergebnisse für Abfrage vorhanden: " + flurstuecke.size());
                        }
                        return new HashSet(flurstuecke);
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Keine Ergebnisse für Abfrage vorhanden");
                        }
                        return new HashSet();
                    }
                }
            } else {
                return new HashSet();
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim abfragen eines Keys: " + key + " Class: " + ((key != null) ? key.getClass() : null),
                ex);
        }
        return new HashSet();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SperreCustomBean isLocked(final FlurstueckSchluesselCustomBean key) {
        if (key != null) {
            if (key != null) {
                final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("sperre");
                if (metaclass == null) {
                    return null;
                }
                final String query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                            + metaclass.getPrimaryKey() + " "
                            + "FROM " + metaclass.getTableName() + " "
                            + "WHERE sperre.fk_flurstueck_schluessel = " + key.getId();
                final MetaObject[] mos = CidsAppBackend.getInstance().getLagisMetaObject(query);
                if ((mos != null) && (mos.length > 0)) {
                    final SperreCustomBean sperre = (SperreCustomBean)mos[0].getBean();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Es ist eine Sperre vorhanden und wird von: " + sperre.getBenutzerkonto()
                                    + " gehalten");
                    }
                    return sperre;
                }
            }
        } else {
            if (LOG.isDebugEnabled()) {
                // TODO EXCEPTIOn !!!!!!! KNAUP
                LOG.debug("Flurstückkey == null");
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Es ist keine Sperre für das angegebne Flurstück vorhanden");
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   gem  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public GemarkungCustomBean completeGemarkung(final GemarkungCustomBean gem) {
        try {
            if ((gem != null) && (gem.getBezeichnung() != null)) {
                final Collection<GemarkungCustomBean> gemarkungen = getGemarkungsKeys();
                if (gemarkungen != null) {
                    final Iterator<GemarkungCustomBean> it = gemarkungen.iterator();
                    while (it.hasNext()) {
                        final GemarkungCustomBean tmp = it.next();
                        if (tmp.getBezeichnung().equals(gem.getBezeichnung())) {
                            return tmp;
                        }
                    }
                    return null;
                } else {
                    return null;
                }
            } else if ((gem != null) && (gem.getSchluessel() != null)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Schlüssel != null");
                }
                final Collection<GemarkungCustomBean> gemarkungen = getGemarkungsKeys();
                if (gemarkungen != null) {
                    final Iterator<GemarkungCustomBean> it = gemarkungen.iterator();
                    while (it.hasNext()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("checke schlüssel durch");
                        }
                        final GemarkungCustomBean tmp = it.next();
                        if (tmp.getSchluessel().intValue() == gem.getSchluessel().intValue()) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Schlüssel gefunden");
                            }
                            return tmp;
                        }
                    }
                    return null;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Fehler beim Kompletieren einer Gemarkung: " + gem, ex);
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fs  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckSchluesselCustomBean completeFlurstueckSchluessel(final FlurstueckSchluesselCustomBean fs) {
        // TODO Jean ?!?!?! try { FlurstueckSchluesselCustomBean schluessel = (FlurstueckSchluesselCustomBean)
        // em.createNamedQuery("findOneFlurstueckSchluessel").setParameter("gId",
        // fs.getGemarkung().getSchluessel()).setParameter("fId", fs.getFlur()).setParameter("fZaehler",
        // fs.getFlurstueckZaehler()).setParameter("fNenner", fs.getFlurstueckNenner()).getSingleResult(); return
        // schluessel; } catch (Exception ex) { //System.out.println("GemarkungCustomBean: "+fs.getGemarkung().getId()+"
        // Schluessel: "+fs.getGemarkung().getSchluessel()); LOG.debug("Fehler beim Kompletieren eines
        // Flurstückschluessels: " + fs.getKeyString(), ex); }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wfsFlurstueck  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public FlurstueckSchluesselCustomBean getFlurstueckSchluesselForWFSFlurstueck(final WfsFlurstuecke wfsFlurstueck)
            throws ActionNotSuccessfulException {
        try {
            if (wfsFlurstueck != null) {
                final FlurstueckSchluesselCustomBean fkey = new FlurstueckSchluesselCustomBean();
                final GemarkungCustomBean gem = new GemarkungCustomBean();
                gem.setSchluessel(wfsFlurstueck.getGem());
                fkey.setGemarkung(gem);
                fkey.setFlur(wfsFlurstueck.getFlur());
                fkey.setFlurstueckZaehler(wfsFlurstueck.getFlurstz());
                fkey.setFlurstueckNenner(wfsFlurstueck.getFlurstn());
                return completeFlurstueckSchluessel(fkey);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("WfsFlurstueck == null. Kann korrespondierenden Flurstueckschluessel nicht abrufen");
                }
                return null;
            }
        } catch (Exception ex) {
            final String errorMessage =
                "Fehler beim Kompletieren eines Flurstückschluessels. Flurstueck vielleicht nicht vorhanden ";
            if (LOG.isDebugEnabled()) {
                LOG.debug(errorMessage + wfsFlurstueck, ex);
            }
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wfsFlurstuecke  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getFlurstueckSchluesselForWFSFlurstueck(
            final Collection<WfsFlurstuecke> wfsFlurstuecke) throws ActionNotSuccessfulException {
        final Collection<FlurstueckSchluesselCustomBean> result = new HashSet<FlurstueckSchluesselCustomBean>();
        try {
            if ((wfsFlurstuecke != null) && (wfsFlurstuecke.size() > 0)) {
                for (final WfsFlurstuecke curWfsFlurstueck : wfsFlurstuecke) {
                    final FlurstueckSchluesselCustomBean curFlurstueckSchluessel =
                        getFlurstueckSchluesselForWFSFlurstueck(curWfsFlurstueck);
                    if (curFlurstueckSchluessel == null) {
                        if (LOG.isDebugEnabled()) {
                            // throw new ActionNotSuccessfulException("FlurstueckSchluesselCustomBean abfrage nicht
                            // erfolgreich. Kein Gegenstück zu WfSFlurstuecke vorhanden.");
                            LOG.debug(
                                "FlurstueckSchluessel abfrage nicht erfolgreich. Kein Gegenstück zu WfSFlurstuecke vorhanden.");
                        }
                        continue;
                    }
                    result.add(curFlurstueckSchluessel);
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                        "WfsFlurstueck == null oder leer. Kann korrespondierenden Flurstueckschluessel nicht abrufen");
                }
                return result;
            }
        } catch (Exception ex) {
            final String errorMessage = "Fehler beim Kompletieren eines Flurstückschluessels: ";
            if (LOG.isDebugEnabled()) {
                LOG.debug(errorMessage, ex);
            }
            throw new ActionNotSuccessfulException(errorMessage, ex);
        }
        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   newSperre  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SperreCustomBean createLock(final SperreCustomBean newSperre) {
        if (newSperre != null) {
            final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("sperre");
            if (metaclass == null) {
                return null;
            }
            final String query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " "
                        + "FROM " + metaclass.getTableName() + " "
                        + "WHERE sperre.fk_flurstueck_schluessel = " + newSperre.getFlurstueckSchluessel();
            final MetaObject[] mos = CidsAppBackend.getInstance().getLagisMetaObject(query);

            if ((mos == null) || (mos.length == 0)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Keine Sperre für das angegebene Flurstueck vorhanden, es wird versucht eine anzulegen");
                }
                try {
                    newSperre.persist();
                } catch (Exception ex) {
                    LOG.error("Fehler beim Anlegen der Sperre", ex);
                    return null;
                }
                return newSperre;
            } else if (mos.length == 1) {
                final SperreCustomBean sperre = (SperreCustomBean)mos[0];
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es ist eine Sperre vorhanden und wird von: " + sperre.getBenutzerkonto() + " gehalten");
                }
                return sperre;
            } else if (mos.length > 1) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es sind mehrere Sperren vorhanden");
                }
                return null;
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Die Sperre die anglegt werden soll ist null");
            }
            return null;
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   sperre  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean releaseLock(final SperreCustomBean sperre) {
        try {
            if (sperre != null) {
                sperre.delete();
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Fehler beim löschen einer Sperre", ex);
            }
            return false;
        }
    }
    /**
     * TODO Still necessary??
     *
     * @return  DOCUMENT ME!
     */
    public Collection<AnlageklasseCustomBean> getAllAnlageklassen() {
        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("anlageklasse");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsAppBackend.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<AnlageklasseCustomBean> beans = new HashSet<AnlageklasseCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((AnlageklasseCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckArtCustomBean> getAllFlurstueckArten() {
        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("flurstueck_art");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsAppBackend.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<FlurstueckArtCustomBean> beans = new HashSet<FlurstueckArtCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((FlurstueckArtCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VertragsartCustomBean> getAllVertragsarten() {
        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("vertragsart");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsAppBackend.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<VertragsartCustomBean> beans = new HashSet<VertragsartCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((VertragsartCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<MipaKategorieCustomBean> getAllMiPaKategorien() {
        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("mipa_kategorie");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsAppBackend.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<MipaKategorieCustomBean> beans = new HashSet<MipaKategorieCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((MipaKategorieCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BaumKategorieCustomBean> getAllBaumKategorien() {
        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("baum_kategorie");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsAppBackend.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<BaumKategorieCustomBean> beans = new HashSet<BaumKategorieCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((BaumKategorieCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VerwaltendeDienststelleCustomBean> getAllVerwaltendeDienstellen() {
        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("verwaltende_dienststelle");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsAppBackend.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<VerwaltendeDienststelleCustomBean> beans = new HashSet<VerwaltendeDienststelleCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((VerwaltendeDienststelleCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VerwaltungsgebrauchCustomBean> getAllVerwaltenungsgebraeuche() {
        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("verwaltungsgebrauch");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsAppBackend.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<VerwaltungsgebrauchCustomBean> beans = new HashSet<VerwaltungsgebrauchCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((VerwaltungsgebrauchCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<RebeArtCustomBean> getAllRebeArten() {
        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("rebe_art");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsAppBackend.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<RebeArtCustomBean> beans = new HashSet<RebeArtCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((RebeArtCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<NutzungsartCustomBean> getAllNutzungsarten() {
        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("nutzungsart");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsAppBackend.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<NutzungsartCustomBean> beans = new HashSet<NutzungsartCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((NutzungsartCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BeschlussartCustomBean> getAllBeschlussarten() {
        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("beschlussart");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsAppBackend.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<BeschlussartCustomBean> beans = new HashSet<BeschlussartCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((BeschlussartCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<KostenCustomBean> getAllKostenarten() {
        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("kosten");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsAppBackend.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<KostenCustomBean> beans = new HashSet<KostenCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((KostenCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BebauungCustomBean> getAllBebauungen() {
        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("bebauung");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsAppBackend.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<BebauungCustomBean> beans = new HashSet<BebauungCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((BebauungCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlaechennutzungCustomBean> getAllFlaechennutzungen() {
        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("flaechennutzung");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsAppBackend.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<FlaechennutzungCustomBean> beans = new HashSet<FlaechennutzungCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((FlaechennutzungCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<MipaMerkmalCustomBean> getAllMiPaMerkmale() {
        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("mipa_merkmal");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsAppBackend.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<MipaMerkmalCustomBean> beans = new HashSet<MipaMerkmalCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((MipaMerkmalCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BaumMerkmalCustomBean> getAllBaumMerkmale() {
        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("baum_merkmal");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsAppBackend.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<BaumMerkmalCustomBean> beans = new HashSet<BaumMerkmalCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((BaumMerkmalCustomBean)metaObject.getBean());
        }
        return beans;
    }
    /**
     * TODO MüSSEN DIE NEUEN FLURSTüCKE GESPERRT WERDEN ? TODO INSERT EXCEPTIONS SO THERE IS A ROLLBACK TODO better
     * Exceptions than boolean or return values;
     *
     * @param   oldFlurstueckSchluessel  DOCUMENT ME!
     * @param   newFlurstueckSchluessel  DOCUMENT ME!
     * @param   username                 DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public FlurstueckCustomBean renameFlurstueck(
            final FlurstueckSchluesselCustomBean oldFlurstueckSchluessel,
            final FlurstueckSchluesselCustomBean newFlurstueckSchluessel,
            final String username) throws ActionNotSuccessfulException {
        SperreCustomBean lock = null;
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Rename Flurstück");
            }
            final FlurstueckCustomBean oldFlurstueck = retrieveFlurstueck(oldFlurstueckSchluessel);
            final FlurstueckCustomBean newFlurstueck;

            if (oldFlurstueck != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("AltesFlurstück existiert");
                }
                // TODO ugly
                // checks either there is a lock for the specific flurstück or not
                if (isLocked(oldFlurstueck.getFlurstueckSchluessel()) == null) {
                    lock = createLock(new SperreCustomBean(oldFlurstueck.getFlurstueckSchluessel(), username));
                    if (lock == null) {
                        // TODO throw new EJBException(new ActionNotSuccessfulException("Anlegen einer SperreCustomBean
                        // nicht möglich"));
                        throw new ActionNotSuccessfulException(
                            "Anlegen einer Sperre für das alte Flurstück nicht möglich");
                    }
                } else {
                    // TODO throw new EJBException(new ActionNotSuccessfulException("Es exisitert bereits eine
                    // SperreCustomBean"));
                    throw new ActionNotSuccessfulException("Es exisitert bereits eine Sperre für das alte Flurstück");
                }
//HistoricResult result = ;

                if (setFlurstueckHistoric(oldFlurstueckSchluessel)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Flurstück wurde Historisch gesetzt");
                    }
                    // TODO Better FlurstückHistoryEntry??
                    // FlurstueckHistorie fHistorie = new FlurstueckHistorieCustomBean();
                    // TODO Flurstückaktion/Historie
                    // TODO NO UNIQUE RESULT EXCEPTION --> möglich ?
                    // FlurstueckHistorie fHistorie = new FlurstueckHistorieCustomBean();
                    if (!existHistoryEntry(oldFlurstueck)) {
                        newFlurstueck = createFlurstueck(newFlurstueckSchluessel);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Es exitieren kein History Eintrag --> keine Kante zu einem anderen Flurstück");
                            LOG.debug("Kein nachfolger für das Flurstück vorhanden --> Lege neues Flurstueck an");
                            LOG.debug("Erzeuge History Eintrag für altes Flurstück");
                        }
                        createHistoryEdge(oldFlurstueck, newFlurstueck);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Neuer History Eintrag für Flurstück erzeugt");
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            // Exception ex =  new ActionNotSuccessfulException("Flurstück");
                            LOG.debug("Renamen des Flurstücks nicht möglich");
                        }
                        releaseLock(lock);
                        throw new ActionNotSuccessfulException(
                            "Es existieren bereits Historieneinträge für dieses Flurstück");
                    }

//                    if(historyEntry != null){
//
//                    } else {
//
//                    }
                    if (newFlurstueck != null) {
                        if (LOG.isDebugEnabled()) {
//                        System.out.println("Das Flurstück wurde erfogreich angelegt --> Setze Nachfolger des Alten Flurstücks");
//                        historyEntry.setNachfolger(newFlurstueck);
//                        em.merge(historyEntry);
//                        System.out.println("Erzeuge History Eintrag für neues Flurstück");
//                        historyEntry = new FlurstueckHistorieCustomBean();
//                        historyEntry.setVorgaenger(oldFlurstueck);
//                        historyEntry.setFlurstueck(newFlurstueck);
//                        createFlurstueckHistoryEntry(historyEntry);
                            LOG.debug("Alle Aktionen für das umbenennen erfolgreich abgeschlossen.");
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            // TODO IF THIS CASE IS POSSIBLE ROLLBACK TRANSACTION
                            LOG.debug("Das neue Flurstück konnte nicht angelegt werden.");
                        }
                        releaseLock(lock);
                        throw new ActionNotSuccessfulException("Das neue Flurstück konnte nicht angelegt werden.");
                    }

                    releaseLock(lock);
                    return newFlurstueck;
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Flurstück konnte nicht historisch gesetzt werden.");
                    }
                    releaseLock(lock);
                    throw new ActionNotSuccessfulException("Flurstück konnte nicht historisch gesetzt werden.");
                }
            } else {
                throw new ActionNotSuccessfulException("Altes Flurstück existiert nicht.");
            }
        } catch (final ActionNotSuccessfulException ex) {
            throw ex;
        } catch (final Exception ex) {
            LOG.error("Unbekannter Fehler beim renamen des Flurstücks.", ex);
//            ActionNotSuccessfulException tmpEx;
//            if(ex instanceof ActionNotSuccessfulException){
//                //tmpEx=(ActionNotSuccessfulException)
//            } else {
//                tmpEx=new ActionNotSuccessfulException("Flurstück konnte nicht umbennant werden");
//                tmpEx.setStackTrace(ex.getStackTrace());
//            }
//            throw tmpEx;
            releaseLock(lock);
            // TODO set nestedException
            throw new ActionNotSuccessfulException(
                "Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator",
                ex);
        }
    }
    /**
     * TODO Lockmode true false for combined ? TODO INSERT EXCEPTIONS SO THERE IS A ROLLBACK
     *
     * @param   joinMembers              DOCUMENT ME!
     * @param   newFlurstueckSchluessel  DOCUMENT ME!
     * @param   username                 DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public FlurstueckCustomBean joinFlurstuecke(
            final ArrayList<FlurstueckSchluesselCustomBean> joinMembers,
            final FlurstueckSchluesselCustomBean newFlurstueckSchluessel,
            final String username) throws ActionNotSuccessfulException {
        final ArrayList<SperreCustomBean> locks = new ArrayList<SperreCustomBean>();
        try {
            if (joinMembers != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es sind joinMember vorhanden.");
                }
                Iterator<FlurstueckSchluesselCustomBean> it = joinMembers.iterator();
                while (it.hasNext()) {
                    final FlurstueckSchluesselCustomBean currentKey = it.next();
                    SperreCustomBean tmpLock;

                    if ((tmpLock = isLocked(currentKey)) == null) {
                        tmpLock = createLock(new SperreCustomBean(currentKey, username));
                        if (tmpLock == null) {
                            if (LOG.isDebugEnabled()) {
                                // TODO throw new EJBException(new ActionNotSuccessfulException("Anlegen einer
                                // SperreCustomBean nicht möglich"));
                                LOG.debug("Anlegen einer Sperre für das Flurstück nicht möglich "
                                            + currentKey.getKeyString() + ".");
                            }
                            releaseLocks(locks);
                            throw new ActionNotSuccessfulException("Anlegen einer Sperre für das Flurstück "
                                        + currentKey.getKeyString() + " nicht möglich.");
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Sperre für Flurstück " + currentKey.getKeyString()
                                            + " Erfolgreich angelegt.");
                            }
                            locks.add(tmpLock);
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            // TODO throw new EJBException(new ActionNotSuccessfulException("Es exisitert bereits eine
                            // SperreCustomBean"));
                            LOG.debug("Es exisitert bereits eine Sperre für das Flurstück " + currentKey.getKeyString()
                                        + " und wird von dem Benutzer " + tmpLock.getBenutzerkonto() + " gehalten.");
                        }
                        releaseLocks(locks);
                        throw new ActionNotSuccessfulException("Es exisitert bereits eine Sperre für das Flurstück "
                                    + currentKey.getKeyString() + " und wird von dem Benutzer "
                                    + tmpLock.getBenutzerkonto() + " gehalten.");
                    }
                }
                it = joinMembers.iterator();
                final FlurstueckCustomBean newFlurstueck = createFlurstueck(newFlurstueckSchluessel);
                if (newFlurstueck != null) {
                    while (it.hasNext()) {
                        final FlurstueckCustomBean oldFlurstueck = retrieveFlurstueck(it.next());
                        // HistoricResult result = setFlurstueckHistoric(oldFlurstueck.getFlurstueckSchluessel());
                        if (setFlurstueckHistoric(oldFlurstueck.getFlurstueckSchluessel())) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Flurstück wurde Historisch gesetzt.");
                            }
                            // TODO IS THIS CASE POSSIBLE ?? --> MEANS ACTIVE FLURSTUECK
                            if (!existHistoryEntry(oldFlurstueck)) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug(
                                        "Es exitieren kein History Eintrag --> keine Kante zu einem anderen Flurstück.");
                                    LOG.debug("Erzeuge History Eintrag für alte Flurstücke.");
                                }
                                createHistoryEdge(oldFlurstueck, newFlurstueck);
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Neuer History Eintrag für Flurstück erzeugt.");
                                }
                            } else {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Es sind bereits Historieneinträge für das Flurstück "
                                                + oldFlurstueck.getFlurstueckSchluessel().getKeyString()
                                                + " vorhanden.");
                                }
                                releaseLocks(locks);
                                throw new ActionNotSuccessfulException(
                                    "Es sind bereits Historieneinträge für das Flurstück "
                                            + oldFlurstueck.getFlurstueckSchluessel().getKeyString()
                                            + " vorhanden.");
                            }
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Flurstück " + oldFlurstueck.getFlurstueckSchluessel().getKeyString()
                                            + " konnte nicht historisch gesetzt werden.");
                            }
                            releaseLocks(locks);
                            throw new ActionNotSuccessfulException("Flurstück "
                                        + oldFlurstueck.getFlurstueckSchluessel().getKeyString()
                                        + " konnte nicht historisch gesetzt werden.");
                        }
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Flurstücke Erfolgreich gejoined");
                    }
                    releaseLocks(locks);
                    return newFlurstueck;
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Das Anlegen des neuen Flurstücks " + newFlurstueckSchluessel.getKeyString()
                                    + " schlug fehl.");
                    }
                    releaseLocks(locks);
                    throw new ActionNotSuccessfulException("Das Anlegen des neuen Flurstücks "
                                + newFlurstueckSchluessel.getKeyString() + " schlug fehl.");
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es wurden keine Flurstücke angeben für die Zusammenlegung.");
                }
                throw new ActionNotSuccessfulException("Es wurden keine Flurstücke angeben für die Zusammenlegung.");
            }
        } catch (final ActionNotSuccessfulException ex) {
            throw ex;
        } catch (final Exception ex) {
            LOG.error("Unbekannter Fehler beim joinen von Flurstücken.", ex);
            releaseLocks(locks);
            throw new ActionNotSuccessfulException(
                "Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator.",
                ex);
        }
    }
    /**
     * TODO INSERT EXCEPTIONS SO THERE IS A ROLLBACK.
     *
     * @param   oldFlurstueckSchluessel  DOCUMENT ME!
     * @param   splitMembers             DOCUMENT ME!
     * @param   username                 DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void splitFlurstuecke(final FlurstueckSchluesselCustomBean oldFlurstueckSchluessel,
            final ArrayList<FlurstueckSchluesselCustomBean> splitMembers,
            final String username) throws ActionNotSuccessfulException {
        SperreCustomBean lock = null;
        try {
            final ArrayList<SperreCustomBean> locks = new ArrayList<SperreCustomBean>();
            if (splitMembers != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es sind Flurstücke zum splitten vorhanden");
                }

                if (isLocked(oldFlurstueckSchluessel) == null) {
                    lock = createLock(new SperreCustomBean(oldFlurstueckSchluessel, username));
                    if (lock == null) {
                        // TODO throw new EJBException(new ActionNotSuccessfulException("Anlegen einer SperreCustomBean
                        // nicht möglich"));
                        throw new ActionNotSuccessfulException(
                            "Anlegen einer Sperre für das alte Flurstück nicht möglich");
                    }
                } else {
                    // TODO throw new EJBException(new ActionNotSuccessfulException("Es exisitert bereits eine
                    // SperreCustomBean"));
                    throw new ActionNotSuccessfulException(
                        "Es exisitert bereits eine Sperre für das alte Flurstück, das gesplittet werden soll");
                }
                final Iterator<FlurstueckSchluesselCustomBean> it = splitMembers.iterator();
                final FlurstueckCustomBean oldFlurstueck = retrieveFlurstueck(oldFlurstueckSchluessel);
                // HistoricResult result = setFlurstueckHistoric(oldFlurstueck.getFlurstueckSchluessel());
                if (setFlurstueckHistoric(oldFlurstueck.getFlurstueckSchluessel())) {
                    if (!existHistoryEntry(oldFlurstueck)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Es exitieren kein History Eintrag --> keine Kante zu einem anderen Flurstück");
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Spliten des Flurstücks nicht möglich, es gibt schon einen Nachfolger");
                        }
                        releaseLock(lock);
                        return;
//TODO Exception!!!!! sonst sagt der Wizard alles Erfogreich
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Flurstück konnte nicht historisch gesetzt werden");
                    }
                    releaseLock(lock);
                    return;
//TODO Exception
                }
                while (it.hasNext()) {
                    final FlurstueckCustomBean newFlurstueck = createFlurstueck(it.next());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Neus Flurstück aus Split erzeugt");
                    }
                    if (newFlurstueck != null) {
                        createHistoryEdge(oldFlurstueck, newFlurstueck);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Neuer History Eintrag für Flurstück erzeugt");
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Fehler beim anlegen eines Flurstücks");
                        }
                        releaseLock(lock);
                        return;
                    }
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Splitten der Flurstücke erforgreich");
                }
                releaseLock(lock);
                return;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("split der Flurstücke nicht erfolgreich");
            }
            releaseLock(lock);
        } catch (Exception ex) {
            LOG.error("Fehler beim splitten von Flurstücken", ex);
            if (ex instanceof ActionNotSuccessfulException) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Eine Aktion ging schief Exception wird weitergereicht");
                }
                releaseLock(lock);
                throw (ActionNotSuccessfulException)ex;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Unbekannte Excepiton");
                }
                releaseLock(lock);
                throw new ActionNotSuccessfulException(
                    "Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator",
                    ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  locks  DOCUMENT ME!
     */
    private void releaseLocks(final ArrayList<SperreCustomBean> locks) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("release Locks " + locks);
        }
        if (locks != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Liste ist vorhanden anzahl: " + locks.size());
            }
            final Iterator<SperreCustomBean> it = locks.iterator();
            while (it.hasNext()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Entferene Sperre...");
                }
                releaseLock(it.next());
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("locks == null --> keine Aktion");
            }
        }
    }
    /**
     * spelling.
     *
     * @param   joinMembers   DOCUMENT ME!
     * @param   splitMembers  DOCUMENT ME!
     * @param   username      DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void joinSplitFlurstuecke(final ArrayList<FlurstueckSchluesselCustomBean> joinMembers,
            final ArrayList<FlurstueckSchluesselCustomBean> splitMembers,
            final String username) throws ActionNotSuccessfulException {
        // TODO ROLLBACK IF ONE OF THE METHODS FAILS
        try {
            final FlurstueckSchluesselCustomBean dummySchluessel = new FlurstueckSchluesselCustomBean();
            // dummySchluessel.setWarStaedtisch(true);
            // UGLY minimum Konstante aus der jeweiligen Klasse benutzen
            for (final FlurstueckArtCustomBean current : getAllFlurstueckArten()) {
                if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_PSEUDO.equals(current.getBezeichnung())) {
                    dummySchluessel.setFlurstueckArt(current);
                    break;
                }
            }
            createFlurstueckSchluessel(dummySchluessel);
            // Flurstueck dummyFlurstueck = createFlurstueck(dummySchluessel);
            joinFlurstuecke(joinMembers, dummySchluessel, username);
            // TODO problem first have to check all keys
            splitFlurstuecke(dummySchluessel, splitMembers, username);
        } catch (final Exception ex) {
            if (ex instanceof ActionNotSuccessfulException) {
                LOG.error("Eine ActionSchlug fehl", ex);
                throw (ActionNotSuccessfulException)ex;
            }
            LOG.error("Fehler beim joinSplit", ex);
            throw new ActionNotSuccessfulException(
                "Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator",
                ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VertragCustomBean> getVertraegeForKey(final FlurstueckSchluesselCustomBean key) {
        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("vertrag");
        if (metaclass == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + metaclass.getID() + ", "
                    + "   jt_flurstueck_vertrag.fk_vertrag "
                    + "FROM "
                    + "   flurstueck, "
                    + "   jt_flurstueck_vertrag "
                    + "WHERE "
                    + "   jt_flurstueck_vertrag.fk_flurstueck = flurstueck.id "
                    + "   AND flurstueck.fk_flurstueck_schluessel = " + key.getId();

        final MetaObject[] mos = CidsAppBackend.getInstance().getLagisMetaObject(query);
        final Collection<VertragCustomBean> beans = new HashSet<VertragCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((VertragCustomBean)metaObject.getBean());
        }

        if (beans != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl Vertraege ist: " + beans.size());
            }
            return beans;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Vertraege für Flurstück vorhanden");
            }
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   aktenzeichen  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getFlurstueckSchluesselByAktenzeichen(final String aktenzeichen) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suche nach Flurstücken(Schluesseln) mit dem Aktenzeichen: " + aktenzeichen);
        }

        final MetaClass metaclass = CidsAppBackend.getInstance().getLagisMetaClass("flurstueck_schluessel");
        if (metaclass == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + metaclass.getID() + ", "
                    + "    flurstueck.fk_flurstueck_schluessel "
                    + "FROM "
                    + "    flurstueck, "
                    + "    jt_flurstueck_vertrag, "
                    + "    vertrag "
                    + "WHERE "
                    + "    flurstueck.ar_vertraege = jt_flurstueck_vertrag.fk_flurstueck "
                    + "    AND jt_flurstueck_vertrag.fk_vertrag = vertrag.id "
                    + "    AND vertrag.aktenzeichen LIKE '%" + aktenzeichen + "%'";

        final MetaObject[] mos = CidsAppBackend.getInstance().getLagisMetaObject(query);
        final Collection<FlurstueckSchluesselCustomBean> flurstueckSchluessel =
            new HashSet<FlurstueckSchluesselCustomBean>();
        for (final MetaObject metaObject : mos) {
            flurstueckSchluessel.add((FlurstueckSchluesselCustomBean)metaObject.getBean());
        }

        if (flurstueckSchluessel != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl Flurststückschlüssel für das Aktenzeichen ist: " + flurstueckSchluessel.size());
            }
            return flurstueckSchluessel;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Flurstückschlüssel mit dem angegebenen Aktenzeichen vorhanden");
            }
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   vertrag  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossReferencesForVertrag(final VertragCustomBean vertrag) {
        final MetaClass mcFlurstueckSchluessel = CidsAppBackend.getInstance()
                    .getLagisMetaClass("flurstueck_schluessel");
        if (mcFlurstueckSchluessel == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + mcFlurstueckSchluessel.getID() + ", "
                    + "   flurstueck.fk_flurstueck_schluessel "
                    + "FROM "
                    + "   public.flurstueck, "
                    + "   public.jt_flurstueck_vertrag "
                    + "WHERE "
                    + "   public.flurstueck.ar_vertraege = public.jt_flurstueck_vertrag.fk_flurstueck  "
                    + "   AND public.jt_flurstueck_vertrag.fk_vertrag = " + vertrag.getId();

        final MetaObject[] mosVertrag = CidsAppBackend.getInstance().getLagisMetaObject(query);
        final Collection<FlurstueckSchluesselCustomBean> keys = new HashSet<FlurstueckSchluesselCustomBean>();
        for (final MetaObject metaObject : mosVertrag) {
            keys.add((FlurstueckSchluesselCustomBean)metaObject.getBean());
        }
        if (keys != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl FlurstueckSchluessel ist: " + keys.size());
            }
            return keys;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Flurstückreferenzen für den Vertrag vorhanden");
            }
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   vertraege  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossreferencesForVertraege(
            final Collection<VertragCustomBean> vertraege) {
        if ((vertraege != null) && (vertraege.size() > 0)) {
            final Collection<FlurstueckSchluesselCustomBean> result = new HashSet<FlurstueckSchluesselCustomBean>();
            final Iterator<VertragCustomBean> it = vertraege.iterator();
            while (it.hasNext()) {
                final Collection<FlurstueckSchluesselCustomBean> curKeys = getCrossReferencesForVertrag(it.next());
                if ((curKeys != null) && (curKeys.size() > 0)) {
                    result.addAll(curKeys);
                }
            }
            return result;
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<MipaCustomBean> getMiPaForKey(final FlurstueckSchluesselCustomBean key) {
        final MetaClass mcMipa = CidsAppBackend.getInstance().getLagisMetaClass("mipa");
        if (mcMipa == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + mcMipa.getID() + ", "
                    + "   jt_flurstueck_mipa.fk_mipa "
                    + "FROM "
                    + "   flurstueck, "
                    + "   jt_flurstueck_mipa "
                    + "WHERE "
                    + "   jt_flurstueck_mipa.fk_flurstueck = flurstueck.id "
                    + "   AND flurstueck.fk_flurstueck_schluessel = " + key.getId();

        final MetaObject[] mosMipa = CidsAppBackend.getInstance().getLagisMetaObject(query);
        final Collection<MipaCustomBean> mipas = new HashSet<MipaCustomBean>();
        for (final MetaObject metaObject : mosMipa) {
            mipas.add((MipaCustomBean)metaObject.getBean());
        }

        if (mipas != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl MiPas ist: " + mipas.size());
            }
            return mipas;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine MiPas für Flurstück vorhanden");
            }
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BaumCustomBean> getBaumForKey(final FlurstueckSchluesselCustomBean key) {
        final MetaClass mcBaum = CidsAppBackend.getInstance().getLagisMetaClass("baum");
        if (mcBaum == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + mcBaum.getID() + ", "
                    + "   jt_flurstueck_baum.fk_baum "
                    + "FROM "
                    + "   flurstueck, "
                    + "   jt_flurstueck_baum "
                    + "WHERE "
                    + "   jt_flurstueck_baum.fk_flurstueck = flurstueck.id "
                    + "   AND flurstueck.fk_flurstueck_schluessel = " + key.getId();

        final MetaObject[] mosBaum = CidsAppBackend.getInstance().getLagisMetaObject(query);
        final Collection<BaumCustomBean> baeume = new HashSet<BaumCustomBean>();
        for (final MetaObject metaObject : mosBaum) {
            baeume.add((BaumCustomBean)metaObject.getBean());
        }

        if (baeume != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl Baueme ist: " + baeume.size());
            }
            return baeume;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Baueme für Flurstück vorhanden");
            }
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   miPa  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossReferencesForMiPa(final MipaCustomBean miPa) {
        final MetaClass mcFlurstueckSchluessel = CidsAppBackend.getInstance()
                    .getLagisMetaClass(CidsAppBackend.CLASS__FLURSTUECK_SCHLUESSEL);
        if (mcFlurstueckSchluessel == null) {
            return null;
        }

        final String query = "SELECT "
                    + "   " + mcFlurstueckSchluessel.getID() + ", "
                    + "   flurstueck.fk_flurstueck_schluessel "
                    + "FROM "
                    + "   public.flurstueck, "
                    + "   public.jt_flurstueck_mipa "
                    + "WHERE "
                    + "   public.flurstueck.ar_mipas = public.jt_flurstueck_mipa.fk_flurstueck  "
                    + "   AND public.jt_flurstueck_mipa.fk_mipa = " + miPa.getId();

        final MetaObject[] mosMipa = CidsAppBackend.getInstance().getLagisMetaObject(query);
        final Collection<FlurstueckSchluesselCustomBean> keys = new HashSet<FlurstueckSchluesselCustomBean>();
        for (final MetaObject metaObject : mosMipa) {
            keys.add((FlurstueckSchluesselCustomBean)metaObject.getBean());
        }

        if (keys != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl FlurstueckSchluessel ist: " + keys.size());
            }
            return keys;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Flurstückreferenzen für MiPa vorhanden");
            }
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   baum  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossReferencesForBaum(final BaumCustomBean baum) {
        final MetaClass mcFlurstueckSchluessel = CidsAppBackend.getInstance()
                    .getLagisMetaClass(CidsAppBackend.CLASS__FLURSTUECK_SCHLUESSEL);
        if (mcFlurstueckSchluessel == null) {
            return null;
        }

        final String query = "SELECT "
                    + "   " + mcFlurstueckSchluessel.getID() + ", "
                    + "   flurstueck.fk_flurstueck_schluessel "
                    + "FROM "
                    + "   public.flurstueck, "
                    + "   public.jt_flurstueck_baum "
                    + "WHERE "
                    + "   public.flurstueck.ar_baeume = public.jt_flurstueck_baum.fk_flurstueck  "
                    + "   AND public.jt_flurstueck_baum.fk_baum = " + baum.getId();

        final MetaObject[] mosBaum = CidsAppBackend.getInstance().getLagisMetaObject(query);
        final Collection<FlurstueckSchluesselCustomBean> keys = new HashSet<FlurstueckSchluesselCustomBean>();
        for (final MetaObject metaObject : mosBaum) {
            keys.add((FlurstueckSchluesselCustomBean)metaObject.getBean());
        }

        if (keys != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl FlurstueckSchluessel ist: " + keys.size());
            }
            return keys;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Flurstückreferenzen für Baum vorhanden");
            }
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   baeume  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossreferencesForBaeume(
            final Collection<BaumCustomBean> baeume) {
        if ((baeume != null) && (baeume.size() > 0)) {
            final Collection<FlurstueckSchluesselCustomBean> result = new HashSet<FlurstueckSchluesselCustomBean>();
            final Iterator<BaumCustomBean> it = baeume.iterator();
            while (it.hasNext()) {
                final Collection<FlurstueckSchluesselCustomBean> curKeys = getCrossReferencesForBaum(it.next());
                if ((curKeys != null) && (curKeys.size() > 0)) {
                    result.addAll(curKeys);
                }
            }
            return result;
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   miPas  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossreferencesForMiPas(
            final Collection<MipaCustomBean> miPas) {
        if ((miPas != null) && (miPas.size() > 0)) {
            final Collection<FlurstueckSchluesselCustomBean> result = new HashSet<FlurstueckSchluesselCustomBean>();
            final Iterator<MipaCustomBean> it = miPas.iterator();
            while (it.hasNext()) {
                final Collection<FlurstueckSchluesselCustomBean> curKeys = getCrossReferencesForMiPa(it.next());
                if ((curKeys != null) && (curKeys.size() > 0)) {
                    result.addAll(curKeys);
                }
            }
            return result;
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key       DOCUMENT ME!
     * @param   username  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void bookNutzungenForFlurstueck(final FlurstueckSchluesselCustomBean key, final String username)
            throws ActionNotSuccessfulException {
//        if (key != null) {
//            SperreCustomBean tmpLock;
//            if ((tmpLock = isLocked(key)) == null) {
//                tmpLock = createLock(new SperreCustomBean(key, username));
//                if (tmpLock == null) {
//                    //TODO
//                    //throw new EJBException(new ActionNotSuccessfulException("Anlegen einer SperreCustomBean nicht möglich"));
//                    System.out.println("Anlegen einer SperreCustomBean für das Flurstück nicht möglich " + key.getKeyString() + ".");
//                    throw new ActionNotSuccessfulException("Anlegen einer SperreCustomBean für das Flurstück " + key.getKeyString() + " nicht möglich.");
//                } else {
//                    System.out.println("SperreCustomBean für Flurstück " + key.getKeyString() + " Erfolgreich angelegt.");
//                }
//            } else {
//                //TODO
//                //throw new EJBException(new ActionNotSuccessfulException("Es exisitert bereits eine SperreCustomBean"));
//                System.out.println("Es exisitert bereits eine SperreCustomBean für das Flurstück " + key.getKeyString() + " und wird von dem Benutzer " + tmpLock.getBenutzerkonto() + " gehalten.");
//                throw new ActionNotSuccessfulException("Es exisitert bereits eine SperreCustomBean für das Flurstück " + key.getKeyString() + " und wird von dem Benutzer " + tmpLock.getBenutzerkonto() + " gehalten.");
//            }
//            try {
//                FlurstueckCustomBean flurstueck = retrieveFlurstueck(key);
//                if (flurstueck == null) {
//                    System.out.println("Die Stillen Reserven des Flurstücks " + key.getKeyString() + " konnten nicht gebucht werden, weil kein Flurstück in der Datenbank gefunden wurde.");
//                    throw new ActionNotSuccessfulException("Die Stillen Reserven des Flurstücks " + key.getKeyString() + " konnten nicht gebucht werden, weil kein Flurstück in der Datenbank gefunden wurde.");
//                } else {
//                    StringBuffer message = new StringBuffer();
//                    message.append("Bei dem Flurstück ");
//                    message.append(flurstueck.getFlurstueckSchluessel().getKeyString());
//                    message.append(" wurden die Stillen Reserven der Nutzungen: \n\n");
//
//                    Set<Nutzung> nutzungen = null;
//                    if ((nutzungen = flurstueck.getNutzungen()) == null) {
//                        System.out.println("Keine Nutzungen vorhanden die gebucht werden müssten");
//                    } else {
//                        Iterator<Nutzung> it = nutzungen.iterator();
//                        Vector<Nutzung> bookedNutzungen = new Vector<Nutzung>();
//                        Date buchungsdatum = new Date();
//                        while (it.hasNext()) {
//                            NutzungCustomBean current = it.next();
//                            if (current.getIstGebucht() == null) {
//                                System.out.println("Ein Buchungstatus einer NutzungCustomBean ist unbekannt. ID: " + current.getId());
//                                throw new ActionNotSuccessfulException("Der Buchungsstatus einer NutzungCustomBean ist unbekannt");
//                            } else {
//                                if (current.getIstGebucht()) {
//                                    System.out.println("NutzungCustomBean ist bereits gebucht");
//                                } else {
//                                    System.out.println("Buche NutzungCustomBean: " + current.getId());
//                                    message.append(current + "\n");
//                                    current.setBuchungsDatum(buchungsdatum);
//                                    current.setIstGebucht(true);
//                                    current.setStilleReserve(0.0);
//                                    bookedNutzungen.add(current);
//                                }
//                            }
//                        }
//                        //Vector im Moment unnötig
//                        if (bookedNutzungen.size() < 1) {
//                            System.out.println("Es wurden keine Nutzungen gebucht");
//                        } else {
//                            System.out.println("Es wurden " + bookedNutzungen.size() + " Nutzungen gebucht, sende Emailbenachrichtigungen");
//                            it = bookedNutzungen.iterator();
//                            message.append("gebucht.\n\n");
//                            sendEmail("Lagis - Stille Reserven wurden gebucht", message.toString(), nkfSessions);
//                        }
//                    }
//                }
//                releaseLock(tmpLock);
//            } catch (Exception ex) {
//                System.out.println("Ein Fehler ist aufgetreten während dem buchen der Stillen Reserven --> löse SperreCustomBean");
//                ex.printStackTrace();
//                releaseLock(tmpLock);
//                if (ex instanceof ActionNotSuccessfulException) {
//                    throw (ActionNotSuccessfulException) ex;
//                } else {
//                    throw new ActionNotSuccessfulException("Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator.", ex);
//                }
//            }
//        } else {
//            throw new ActionNotSuccessfulException("Kein gültiger Flurstückschlüssel angegeben");
//        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * ToDo problem Error allocating connection : [Error in allocating a connection. Cause:
     * java.lang.IllegalStateException: Local transaction already has 1 non-XA Resource: cannot add more resources. ]
     * public Set<FlurstueckSchluessel> getIntersectingFlurstueckSchluessel(Geometry geom) throws
     * ActionNotSuccessfulException { Set<FlurstueckSchluessel> result = new HashSet<FlurstueckSchluessel>(); if (geom
     * != null) { try { Set<WfsFlurstuecke> wfsFlurstuecke = lagisCrossover.getIntersectingFlurstuecke(geom); if
     * (wfsFlurstuecke != null && wfsFlurstuecke.size() > 0) { System.out.println("There are " + wfsFlurstuecke.size() +
     * " intersecting wfsFlurstuecke."); return getFlurstueckSchluesselForWFSFlurstueck(wfsFlurstuecke); } else {
     * System.out.println("There are no intersecting wfsFlurstuecke."); } } catch (Exception ex) { final String
     * errorMessage="Error while retrieving intersecting flurstuecke"; System.out.println(errorMessage);
     * ex.printStackTrace(); throw new ActionNotSuccessfulException(errorMessage, ex); } } else { System.out.println("No
     * Geometry for intersection available."); } return result; } public Set<WfsFlurstuecke>
     * getIntersectingFlurstueckSchluessel(Geometry geom) throws ActionNotSuccessfulException { Set<WfsFlurstuecke>
     * result = new HashSet<WfsFlurstuecke>(); if (geom != null) { try { Set<WfsFlurstuecke> wfsFlurstuecke =
     * lagisCrossover.getIntersectingFlurstuecke(geom); if (wfsFlurstuecke != null && wfsFlurstuecke.size() > 0) {
     * System.out.println("There are " + wfsFlurstuecke.size() + " intersecting wfsFlurstuecke.");
     * result.addAll(wfsFlurstuecke); return result; } else { System.out.println("There are no intersecting
     * wfsFlurstuecke."); } } catch (Exception ex) { final String errorMessage = "Error while retrieving intersecting
     * flurstuecke"; System.out.println(errorMessage); ex.printStackTrace(); throw new
     * ActionNotSuccessfulException(errorMessage, ex); } } else { System.out.println("No Geometry for intersection
     * available."); } return result; } TODO implementieren für löschen, verrechnung und alle buchen private void
     * bookNutzungenForFlurstueck(Vector<Nutzung> nutzungToBook) { }
     *
     * @version  $Revision$, $Date$
     */
    class MailAuthenticator extends Authenticator {

        //~ Instance fields ----------------------------------------------------

        /**
         * Ein String, der den Usernamen nach der Erzeugung eines Objektes<br>
         * dieser Klasse enthalten wird.
         */
        private final String user;
        /**
         * Ein String, der das Passwort nach der Erzeugung eines Objektes<br>
         * dieser Klasse enthalten wird.
         */
        private final String password;

        //~ Constructors -------------------------------------------------------

        /**
         * Der Konstruktor erzeugt ein MailAuthenticator Objekt<br>
         * aus den beiden Parametern user und passwort.
         *
         * @param  user      String, der Username fuer den Mailaccount.
         * @param  password  String, das Passwort fuer den Mailaccount.
         */
        public MailAuthenticator(final String user, final String password) {
            this.user = user;
            this.password = password;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * Diese Methode gibt ein neues PasswortAuthentication Objekt zurueck.
         *
         * @return  DOCUMENT ME!
         *
         * @see     javax.mail.Authenticator#getPasswordAuthentication()
         */
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(this.user, this.password);
        }
    }
}
