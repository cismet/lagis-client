/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * NKFTableModel.java
 *
 * Created on 25. April 2007, 11:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import org.apache.log4j.Logger;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.utillity.BebauungsVector;
import de.cismet.lagis.utillity.FlaechennutzungsVector;

import de.cismet.lagisEE.bean.Exception.TerminateNutzungNotPossibleException;

import de.cismet.lagisEE.entity.core.Nutzung;
import de.cismet.lagisEE.entity.core.NutzungsBuchung;
import de.cismet.lagisEE.entity.core.hardwired.Anlageklasse;
import de.cismet.lagisEE.entity.core.hardwired.Bebauung;
import de.cismet.lagisEE.entity.core.hardwired.Flaechennutzung;
import de.cismet.lagisEE.entity.core.hardwired.Nutzungsart;

import de.cismet.tools.CurrentStackTrace;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class NKFTableModel extends AbstractTableModel {

    //~ Static fields/initializers ---------------------------------------------

    // private Vector<Nutzung> nutzungenHistorisch;
    private static final String[] COLUMN_HEADER = {
            "Nutzungs Nr.",
            "Buchungsnummer",
            "Anlageklasse",
            "Nutzungsartenschlüssel",
            "Nutzungsart",
            "Flächennutzungsplan",
            "Bebauungsplan",
            "Fläche m²",
            "Quadratmeterpreis",
            "Gesamtpreis",
            "Stille Reserve",
            "Buchwert",
            "Bemerkung"
        };

    //~ Instance fields --------------------------------------------------------

    private Icon booked = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/nutzung/booked.png"));
    private Icon notBooked = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/nutzung/notBooked.png"));
    private Icon statusUnknown = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/nutzung/statusUnknown.png"));
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private ArrayList<Nutzung> allNutzungen;
    // ToDo Selection über Datum noch nicht ganz optimal weil sehr oft im EDT benutzt und kostspielig
    private ArrayList<NutzungsBuchung> currentBuchungen;
    private DecimalFormat df = LagisBroker.getCurrencyFormatter();
    private boolean isInEditMode = false;
    private Date currentDate = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of NKFTableModel.
     */
    public NKFTableModel() {
        allNutzungen = new ArrayList<Nutzung>();
        currentBuchungen = new ArrayList<NutzungsBuchung>();
        // nutzungenHistorisch = new Vector<Nutzung>();
    }

    /**
     * Creates a new NKFTableModel object.
     *
     * @param  nutzungen  DOCUMENT ME!
     */
    public NKFTableModel(final Set<Nutzung> nutzungen) {
        try {
            allNutzungen = new ArrayList<Nutzung>(nutzungen);
            if (log.isDebugEnabled()) {
                log.debug("Anzahl aller Nutzungen: " + allNutzungen.size());
            }
            currentBuchungen = new ArrayList<NutzungsBuchung>();
            currentDate = null;
            refreshTableModel();
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            this.allNutzungen = new ArrayList<Nutzung>();
            this.currentBuchungen = new ArrayList<NutzungsBuchung>();
            // this.nutzungenHistorisch = new Vector<Nutzung>();
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            final NutzungsBuchung selectedBuchung = currentBuchungen.get(rowIndex);
//            final NutzungsBuchung selectedBuchung = nutzung.getBuchungForExactDate(currentDate);
            final Double stilleReserve = selectedBuchung.getNutzung().getStilleReserveForBuchung(selectedBuchung);
            switch (columnIndex) {
                case 0: {
                    if (selectedBuchung.getNutzung() != null) {
                        return selectedBuchung.getNutzung().getId();
                    } else {
                        return null;
                    }
                }
                case 1: {
                    if (selectedBuchung.getNutzung() != null) {
                        return selectedBuchung.getNutzung().getBuchungsNummerForBuchung(selectedBuchung);
                    } else {
                        return null;
                    }
                }
                case 2: {
                    return selectedBuchung.getAnlageklasse();
                }
                case 3: {
                    return selectedBuchung.getNutzungsart();
                }
                case 4: {
                    if ((selectedBuchung.getNutzungsart() != null)
                                && (selectedBuchung.getNutzungsart().getBezeichnung() != null)) {
                        return selectedBuchung.getNutzungsart().getBezeichnung();
                    } else {
                        return null;
                    }
                }
                case 5: {
                    // TODO Special GUI for editing
                    if (selectedBuchung.getFlaechennutzung() != null) {
                        return new FlaechennutzungsVector(selectedBuchung.getFlaechennutzung());
                    } else {
                        return new FlaechennutzungsVector();
                    }
                }
                case 6: {
                    if (selectedBuchung.getBebauung() != null) {
                        return new BebauungsVector(selectedBuchung.getBebauung());
                    } else {
                        return new BebauungsVector();
                    }
                }
                case 7: {
                    return selectedBuchung.getFlaeche();
                }
                case 8: {
                    return selectedBuchung.getQuadratmeterpreis();
                }
                case 9: {
                    if (stilleReserve != null) {
                        return selectedBuchung.getGesamtpreis() - stilleReserve;
                    } else {
                        // ToDo NKF
                        return selectedBuchung.getGesamtpreis();
                    }
                }
                case 10: {
                    // ToDo NKF
                    if (stilleReserve != null) {
                        return stilleReserve;
                    } else {
                        return 0.0;
                    }
                }
                case 11: {
                    // ToDo gibt so wenig Buchwerte extra Spalte dafür ?
                    if (selectedBuchung.getIstBuchwert()) {
                        return booked;
                    } else {
                        return notBooked;
                    }
                }
                case 12: {
                    return selectedBuchung.getBemerkung();
                }
                default: {
                    return "Spalte ist nicht definiert";
                }
            }
        } catch (Exception ex) {
            log.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
            return null;
        }
    }

    @Override
    public int getRowCount() {
        return currentBuchungen.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_HEADER.length;
    }

    @Override
    public String getColumnName(final int column) {
        return COLUMN_HEADER[column];
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        if ((columnIndex == 0) || (columnIndex == 1) || (columnIndex == 4) || (columnIndex == 9) || (columnIndex == 10)
                    || (columnIndex == 11)) {
            return false;
        } else {
            return (COLUMN_HEADER.length > columnIndex)
                        && (currentBuchungen.size() > rowIndex)
                        && isInEditMode;
                // && (LagisBroker.getInstance().isNkfAdminPermission());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isEditable  DOCUMENT ME!
     */
    public void setIsInEditMode(final boolean isEditable) {
        isInEditMode = isEditable;
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        switch (columnIndex) {
            case 0: {
                return Integer.class;
            }
            case 1: {
                return Integer.class;
            }
            case 2: {
                return Anlageklasse.class;
            }
            case 3: {
                return Nutzungsart.class;
            }
            case 4: {
                return String.class;
            }
            case 5: {
                return Vector.class;
            }
            case 6: {
                return Vector.class;
            }
            case 7: {
                return Integer.class;
            }
            case 8: {
                return Double.class;
            }
            case 9: {
                return Double.class;
            }
            case 10: {
                return Double.class;
            }
            case 11: {
                return ImageIcon.class;
            }
            case 12: {
                return String.class;
            }
            default: {
                log.warn("Die gewünschte Spalte exitiert nicht, es kann keine Klasse zurück geliefert werden");
                return null;
            }
        }
    }

    // ToDo beseitigen wenn abgebrochen wird ?? wird aber glaube ich neu geladen
    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        try {
            // ToDo NKF gibt nur eine Nutzung spezialfall
            final Nutzung selectedNutzung = currentBuchungen.get(rowIndex).getNutzung();
            NutzungsBuchung selectedBuchung = currentBuchungen.get(rowIndex);
            NutzungsBuchung oldBuchung = null;
            if ((selectedBuchung.getGueltigbis() == null) && !LagisBroker.getInstance().isNkfAdminPermission()) {
                if (selectedBuchung.getId() != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("neue Buchung wird angelegt");
                    }
                    final NutzungsBuchung newBuchung = selectedBuchung.cloneBuchung();
                    selectedNutzung.addBuchung(newBuchung);
                    oldBuchung = selectedBuchung;
                    selectedBuchung = newBuchung;
                } else {
                    oldBuchung = selectedNutzung.getPreviousBuchung();
                }
            } else if (selectedBuchung.getIstBuchwert() == true) {
                if (log.isDebugEnabled()) {
                    log.debug("historischer Buchwert wurde editiert");
                }
            }
            switch (columnIndex) {
                case 2: {
                    if ((aValue != null) && (aValue instanceof String)) {
                        selectedBuchung.setAnlageklasse(null);
                        break;
                    }
                    selectedBuchung.setAnlageklasse((Anlageklasse)aValue);
                    break;
                }
                case 3: {
                    if ((aValue != null) && (aValue instanceof String)) {
                        selectedBuchung.setNutzungsart(null);
                        break;
                    }
                    selectedBuchung.setNutzungsart((Nutzungsart)aValue);
                    break;
                }
                case 5: {
                    Set<Flaechennutzung> tmpNutz = selectedBuchung.getFlaechennutzung();
                    if (tmpNutz != null) {
                        tmpNutz.clear();
                    } else {
                        selectedBuchung.setFlaechennutzung(new HashSet<Flaechennutzung>());
                        tmpNutz = selectedBuchung.getFlaechennutzung();
                    }
                    // tmpNutz.addAll((Collection<Flaechennutzung>) aValue);
                    final Iterator<Flaechennutzung> itF = ((Collection<Flaechennutzung>)aValue).iterator();
                    while (itF.hasNext()) {
                        final Flaechennutzung fNutzung = itF.next();
                        if (fNutzung.getBezeichnung() != null) {
                            tmpNutz.add(fNutzung);
                        }
                    }
                    selectedBuchung.setFlaechennutzung(tmpNutz);
                    break;
                }
                case 6: {
                    Set<Bebauung> tmpBebauung = selectedBuchung.getBebauung();
                    if (tmpBebauung != null) {
                        tmpBebauung.clear();
                    } else {
                        selectedBuchung.setBebauung(new HashSet<Bebauung>());
                        tmpBebauung = selectedBuchung.getBebauung();
                    }
                    // tmpNutz.addAll((Collection<Flaechennutzung>) aValue);
                    final Iterator<Bebauung> itB = ((Collection<Bebauung>)aValue).iterator();
                    while (itB.hasNext()) {
                        final Bebauung bebauung = itB.next();
                        if (bebauung.getBezeichnung() != null) {
                            tmpBebauung.add(bebauung);
                        }
                    }
                    selectedBuchung.setBebauung(tmpBebauung);
                    break;
                }
                case 7: {
//                    if (nutzung.getFlaeche() != null && nutzung.getQuadratmeterpreis() != null) {
//                        nutzung.setAlterGesamtpreis(nutzung.getFlaeche() * nutzung.getQuadratmeterpreis());
//                    }
                    selectedBuchung.setFlaeche((Integer)aValue);
                    break;
                }
                case 8: {
//                    if (nutzung.getFlaeche() != null && nutzung.getQuadratmeterpreis() != null) {
//                        nutzung.setAlterGesamtpreis(nutzung.getFlaeche() * nutzung.getQuadratmeterpreis());
//                    }
                    selectedBuchung.setQuadratmeterpreis((Double)aValue);
                    break;
                }
                case 12: {
                    if ((aValue != null) && (aValue instanceof String) && (((String)aValue).length() == 0)) {
                        selectedBuchung.setBemerkung(null);
                        return;
                    }
                    selectedBuchung.setBemerkung((String)aValue);
                    break;
                }
                default: {
                    log.warn("Keine Spalte für angegebenen Index vorhanden: " + columnIndex);
                    return;
                }
            }
            if ((selectedBuchung != null) && (oldBuchung != null) && (selectedBuchung.getId() == null)) {
                if (log.isDebugEnabled()) {
                    log.debug("Prüfe ob die Nutzung sich wirklich verändert hat");
                }
                if (NutzungsBuchung.NUTZUNG_HISTORY_EQUALATOR.pedanticEquals(oldBuchung, selectedBuchung)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Nutzungen sind gleich muss keine neue angelegt werden");
                    }
                    selectedNutzung.removeOpenNutzung();
                }
            }
            refreshTableModel();
        } catch (Exception ex) {
            log.error("Fehler beim setzen von Daten in dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  nutzung  DOCUMENT ME!
     */
    public void addNutzung(final Nutzung nutzung) {
        if (nutzung != null) {
            allNutzungen.add(nutzung);
            refreshTableModel();
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Nutzung kann nicht hinzugefügt werden ist null.");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public NutzungsBuchung getBuchungAtRow(final int rowIndex) {
        try {
            return currentBuchungen.get(rowIndex);
        } catch (IndexOutOfBoundsException ex) {
            log.warn("Achtung der abgefragete wert ist nicht in dem Modell vorhanden", ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex  DOCUMENT ME!
     *
     * @throws  TerminateNutzungNotPossibleException  DOCUMENT ME!
     */
    public void removeNutzung(final int rowIndex) throws TerminateNutzungNotPossibleException {
        final NutzungsBuchung selectedBuchung = currentBuchungen.get(rowIndex);
        final Nutzung nutzungToRemove = currentBuchungen.get(rowIndex).getNutzung();
        if (nutzungToRemove != null) {
            if (log.isDebugEnabled()) {
                log.debug("Nutzung die entfernt werden soll ist in Modell vorhanden.");
            }
            if (nutzungToRemove.getId() == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Nutzung die Entfernt wurde war noch nicht in Datenbank");
                }
                allNutzungen.remove(nutzungToRemove);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Nutzung ist in Datenbank vorhanden");
                }
                if ((selectedBuchung != null) && (selectedBuchung.getId() == null)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Die Betroffene Buchung ist neu und kann gelöscht werden");
                    }
                    nutzungToRemove.removeOpenNutzung();
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug(
                            "Die Betroffene Buchung ist in der Datenbank gespeichert. Komplette Nutzung wird historisch gesetzt");
                    }
                    final Date terminationDate = new Date();
                    if (log.isDebugEnabled()) {
                        log.debug("Termination date: " + terminationDate);
                    }
                    nutzungToRemove.terminateNutzung(terminationDate);
                }
            }
        }
        refreshTableModel();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<NutzungsBuchung> getAllBuchungen() {
        final ArrayList<NutzungsBuchung> sortedNutzungen = new ArrayList<NutzungsBuchung>();
        for (final Nutzung curNutzung : allNutzungen) {
            if (curNutzung.getBuchungsCount() > 0) {
                for (final NutzungsBuchung curBuchung : curNutzung.getNutzungsBuchungen()) {
                    sortedNutzungen.add(curBuchung);
                }
            }
        }
        if (sortedNutzungen.size() > 0) {
            // ToDO NKF Comparator
            Collections.sort(sortedNutzungen, NutzungsBuchung.DATE_COMPARATOR);
        }
        return sortedNutzungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  nutzungen  DOCUMENT ME!
     */
    public void refreshTableModel(final Set<Nutzung> nutzungen) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Refresh des NKFTableModell");
            }

            if (nutzungen != null) {
                this.allNutzungen = new ArrayList<Nutzung>(nutzungen);
            } else {
                allNutzungen.clear();
            }
            setModelToHistoryDate(currentDate);
        } catch (Exception ex) {
            log.error("Fehler beim refreshen des Models", ex);
            this.currentBuchungen = new ArrayList<NutzungsBuchung>();
            this.allNutzungen = new ArrayList<Nutzung>();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<NutzungsBuchung> getCurrentBuchungen() {
        return currentBuchungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<NutzungsBuchung> getOpenBuchungen() {
        if (log.isDebugEnabled()) {
            log.debug("Anzahl aller Nutzungen: " + allNutzungen.size());
        }
        final ArrayList<NutzungsBuchung> selectedBuchungen = new ArrayList<NutzungsBuchung>();
        for (final Nutzung curNutzung : allNutzungen) {
            final NutzungsBuchung curBuchung = curNutzung.getOpenBuchung();
            if (curBuchung != null) {
                selectedBuchungen.add(curBuchung);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Anzahl offener Nutzungen: " + selectedBuchungen.size());
        }
        return selectedBuchungen;
    }
    /**
     * public Set<Nutzung> getCurrentNutzungen() { final Set<Nutzung> currentNutzungen = new HashSet<Nutzung>(); for
     * (NutzungsBuchung curBuchung : currentBuchungen) { Nutzung curNutzung = curBuchung.getNutzung(); if (curNutzung !=
     * null) { currentNutzungen.add(curNutzung); } } return currentNutzungen; }
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<Nutzung> getAllNutzungen() {
        return allNutzungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  historyDate  DOCUMENT ME!
     */
    public void setModelToHistoryDate(final Date historyDate) {
        if (log.isDebugEnabled()) {
            log.debug("setModelToHistoryDate: " + historyDate, new CurrentStackTrace());
            log.debug("anzahl rows: " + getRowCount());
        }
        if (log.isDebugEnabled()) {
            log.debug("AnzahlNutzungen: " + allNutzungen.size());
        }
        currentDate = historyDate;
        currentBuchungen.clear();
        for (final Nutzung curNutzung : allNutzungen) {
//            log.debug("historydate"+historyDate);
//            log.debug("currentDate"+currentDate);
//            System.out.println("historydate"+historyDate);
//            System.out.println("currentDate"+currentDate);
//            final NutzungsBuchung open = curNutzung.getOpenBuchung();
//            log.debug("openBuchung: "+open+"");
//            final NutzungsBuchung terminated = curNutzung.getTerminalBuchung();
//            log.debug("getTerminated"+terminated);
//            if(terminated != null){
//                log.debug("id: "+terminated+" gueltig_bis: "+terminated.getGueltigbis());
//                log.debug("Terminated Nutzung Date: "+curNutzung.getTerminalBuchung().getGueltigbis());
//                log.debug("millis"+curNutzung.getTerminalBuchung().getGueltigbis().getTime());
//                if(historyDate != null){
//                log.debug("millis"+historyDate.getTime());
//                }
//                log.debug("Termination id: "+curNutzung.getTerminalBuchung().getId());
//                log.debug("curNutzung size"+curNutzung.getBuchungsCount());
//                log.debug("BuchungForDate: "+curNutzung.getBuchungForDate(curNutzung.getTerminalBuchung().getGueltigbis()));
//                System.out.println("marker: "+curNutzung.getBuchungForDate(historyDate));
//            }
//            log.debug("historydate"+historyDate);
//            log.debug("currentDate"+currentDate);
//            System.out.println("historydate"+historyDate);
//            System.out.println("currentDate"+currentDate);
            final Set buchungenForDay = curNutzung.getBuchungForDay(historyDate);
            if (log.isDebugEnabled()) {
                log.debug("Anzahl buchungen: " + buchungenForDay.size());
            }
            currentBuchungen.addAll(buchungenForDay);
        }
        if (log.isDebugEnabled()) {
            log.debug("anzahl rows: " + getRowCount());
        }
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getCurrentDate() {
        return currentDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   buchung  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getIndexOfBuchung(final NutzungsBuchung buchung) {
        return currentBuchungen.indexOf(buchung);
    }

    /**
     * DOCUMENT ME!
     */
    public void refreshTableModel() {
        setModelToHistoryDate(currentDate);
    }
}
