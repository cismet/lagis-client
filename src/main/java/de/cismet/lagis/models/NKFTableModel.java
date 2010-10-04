/*
 * NKFTableModel.java
 *
 * Created on 25. April 2007, 11:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.utillity.BebauungsVector;
import de.cismet.lagis.utillity.FlaechennutzungsVector;
import de.cismet.lagisEE.bean.Exception.IllegalNutzungStateException;
import de.cismet.lagisEE.bean.Exception.TerminateNutzungNotPossibleException;
import de.cismet.lagisEE.entity.core.Nutzung;
import de.cismet.lagisEE.entity.core.NutzungsBuchung;
import de.cismet.lagisEE.entity.core.hardwired.Anlageklasse;
import de.cismet.lagisEE.entity.core.hardwired.Bebauung;
import de.cismet.lagisEE.entity.core.hardwired.Flaechennutzung;

import de.cismet.lagisEE.entity.core.hardwired.Nutzungsart;

import de.cismet.tools.CurrentStackTrace;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author Puhl
 */
public class NKFTableModel extends AbstractTableModel {

    private Icon booked = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/nutzung/booked.png"));
    private Icon notBooked = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/nutzung/notBooked.png"));
    private Icon statusUnknown = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/nutzung/statusUnknown.png"));
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private ArrayList<Nutzung> allNutzungen;
    //ToDo Selection über Datum noch nicht ganz optimal weil sehr oft im EDT benutzt und kostspielig
    private ArrayList<Nutzung> currentNutzungen;
    //private Vector<Nutzung> nutzungenHistorisch;
    private final static String[] COLUMN_HEADER = {"Nutzungs Nr.", "Anlageklasse", "Nutzungsartenschlüssel", "Nutzungsart", "Flächennutzungsplan", "Bebauungsplan", "Fläche m²", "Quadratmeterpreis", "Gesamtpreis", "Stille Reserve", "Buchwert", "Bemerkung"};
    private DecimalFormat df = LagisBroker.getCurrencyFormatter();
    private boolean isInEditMode = false;
    private Date currentDate = null;

    /** Creates a new instance of NKFTableModel */
    public NKFTableModel() {
        allNutzungen = new ArrayList<Nutzung>();
        currentNutzungen = new ArrayList<Nutzung>();
        //nutzungenHistorisch = new Vector<Nutzung>();
    }

    public NKFTableModel(Set<Nutzung> nutzungen) {
        try {
            allNutzungen = new ArrayList<Nutzung>(nutzungen);
            log.debug("Anzahl aller Nutzungen: " + allNutzungen.size());
            currentNutzungen = new ArrayList<Nutzung>();
            currentDate=null;
            refreshTableModel();
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            this.allNutzungen = new ArrayList<Nutzung>();
            this.currentNutzungen = new ArrayList<Nutzung>();
            //this.nutzungenHistorisch = new Vector<Nutzung>();
        }
    }

    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            final Nutzung nutzung = currentNutzungen.get(rowIndex);
            final NutzungsBuchung selectedBuchung = nutzung.getBuchungForDate(currentDate);
            final Double stilleReserve = nutzung.getStilleReserveForBuchung(selectedBuchung);
            switch (columnIndex) {
                case 0:
                    return nutzung.getId();
                case 1:
                    return selectedBuchung.getAnlageklasse();
                case 2:
                    return selectedBuchung.getNutzungsart();
                case 3:
                    if (selectedBuchung.getNutzungsart() != null && selectedBuchung.getNutzungsart().getBezeichnung() != null) {
                        return selectedBuchung.getNutzungsart().getBezeichnung();
                    } else {
                        return null;
                    }
                case 4:
                    //TODO Special GUI for editing
                    if (selectedBuchung.getFlaechennutzung() != null) {
                        return new FlaechennutzungsVector(selectedBuchung.getFlaechennutzung());
                    } else {
                        return new FlaechennutzungsVector();
                    }
                case 5:
                    if (selectedBuchung.getBebauung() != null) {
                        return new BebauungsVector(selectedBuchung.getBebauung());
                    } else {
                        return new BebauungsVector();
                    }
                case 6:
                    return selectedBuchung.getFlaeche();
                case 7:
                    return selectedBuchung.getQuadratmeterpreis();
                case 8:
                    if (stilleReserve != null) {
                        return selectedBuchung.getGesamtpreis() - stilleReserve;
                    } else {
                        //ToDo NKF
                        return selectedBuchung.getGesamtpreis();
                    }
                case 9:
                    //ToDo NKF
                    if (stilleReserve != null) {
                        return stilleReserve;
                    } else {
                        return 0.0;
                    }
                case 10:
                    //ToDo gibt so wenig Buchwerte extra Spalte dafür ?
                    if (selectedBuchung.getIstBuchwert()) {
                        return booked;
                    } else {
                        return notBooked;
                    }
                case 11:
                    return selectedBuchung.getBemerkung();
                default:
                    return "Spalte ist nicht definiert";
            }
        } catch (Exception ex) {
            log.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
            return null;
        }

    }

    public int getRowCount() {
        return currentNutzungen.size();
    }

    public int getColumnCount() {
        return COLUMN_HEADER.length;
    }

    @Override
    public String getColumnName(
            int column) {
        return COLUMN_HEADER[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 3 || columnIndex == 8 || columnIndex == 9 || columnIndex == 10) {
            return false;
        } else {
            return (COLUMN_HEADER.length > columnIndex) && (currentNutzungen.size() > rowIndex) && isInEditMode && currentNutzungen.get(rowIndex).getBuchungForDate(currentDate).getGueltigbis() == null;
        }

    }

    public void setIsInEditMode(boolean isEditable) {
        isInEditMode = isEditable;
    }

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Integer.class;
            case 1:
                return Anlageklasse.class;
            case 2:
                return Nutzungsart.class;
            case 3:
                return String.class;
            case 4:
                return Vector.class;
            case 5:
                return Vector.class;
            case 6:
                return Integer.class;
            case 7:
                return Double.class;
            case 8:
                return Double.class;
            case 9:
                return Double.class;
            case 10:
                return ImageIcon.class;
            case 11:
                return String.class;
            default:
                log.warn("Die gewünschte Spalte exitiert nicht, es kann keine Klasse zurück geliefert werden");
                return null;
        }
    }

    //ToDo beseitigen wenn abgebrochen wird ?? wird aber glaube ich neu geladen 
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            //ToDo NKF gibt nur eine Nutzung spezialfall
            final Nutzung selectedNutzung = currentNutzungen.get(rowIndex);
            NutzungsBuchung selectedBuchung = selectedNutzung.getBuchungForDate(currentDate);
            NutzungsBuchung oldBuchung = null;
            if (selectedBuchung.getId() != null) {
                log.debug("neue Buchung wird angelegt");
                final NutzungsBuchung newBuchung = selectedBuchung.cloneBuchung();
                selectedNutzung.addBuchung(newBuchung);
                oldBuchung = selectedBuchung;
                selectedBuchung = newBuchung;
            } else {
                oldBuchung = selectedNutzung.getPreviousBuchung();
            }
            switch (columnIndex) {
                case 1:
                    if (aValue != null && aValue instanceof String) {
                        selectedBuchung.setAnlageklasse(null);
                        break;
                    }
                    selectedBuchung.setAnlageklasse((Anlageklasse) aValue);
                    break;
                case 2:
                    if (aValue != null && aValue instanceof String) {
                        selectedBuchung.setNutzungsart(null);
                        break;
                    }
                    selectedBuchung.setNutzungsart((Nutzungsart) aValue);
                    fireTableRowsUpdated(rowIndex, rowIndex);
                    break;
                case 4:
                    Set<Flaechennutzung> tmpNutz = selectedBuchung.getFlaechennutzung();
                    if (tmpNutz != null) {
                        tmpNutz.clear();
                    } else {
                        selectedBuchung.setFlaechennutzung(new HashSet<Flaechennutzung>());
                        tmpNutz = selectedBuchung.getFlaechennutzung();
                    }
                    //tmpNutz.addAll((Collection<Flaechennutzung>) aValue);
                    Iterator<Flaechennutzung> itF = ((Collection<Flaechennutzung>) aValue).iterator();
                    while (itF.hasNext()) {
                        Flaechennutzung fNutzung = itF.next();
                        if (fNutzung.getBezeichnung() != null) {
                            tmpNutz.add(fNutzung);
                        }
                    }
                    selectedBuchung.setFlaechennutzung(tmpNutz);
                    break;
                case 5:
                    Set<Bebauung> tmpBebauung = selectedBuchung.getBebauung();
                    if (tmpBebauung != null) {
                        tmpBebauung.clear();
                    } else {
                        selectedBuchung.setBebauung(new HashSet<Bebauung>());
                        tmpBebauung = selectedBuchung.getBebauung();
                    }
                    //tmpNutz.addAll((Collection<Flaechennutzung>) aValue);
                    Iterator<Bebauung> itB = ((Collection<Bebauung>) aValue).iterator();
                    while (itB.hasNext()) {
                        Bebauung bebauung = itB.next();
                        if (bebauung.getBezeichnung() != null) {
                            tmpBebauung.add(bebauung);
                        }
                    }
                    selectedBuchung.setBebauung(tmpBebauung);
                    break;
                case 6:
//                    if (nutzung.getFlaeche() != null && nutzung.getQuadratmeterpreis() != null) {
//                        nutzung.setAlterGesamtpreis(nutzung.getFlaeche() * nutzung.getQuadratmeterpreis());
//                    }
                    selectedBuchung.setFlaeche((Integer) aValue);
                    fireTableRowsUpdated(rowIndex, rowIndex);
                    break;
                case 7:
//                    if (nutzung.getFlaeche() != null && nutzung.getQuadratmeterpreis() != null) {
//                        nutzung.setAlterGesamtpreis(nutzung.getFlaeche() * nutzung.getQuadratmeterpreis());
//                    }
                    selectedBuchung.setQuadratmeterpreis((Double) aValue);
                    fireTableRowsUpdated(rowIndex, rowIndex);
                    break;
                case 11:
                    if (aValue != null && aValue instanceof String) {
                        selectedBuchung.setBemerkung(null);
                        return;
                    }
                    selectedBuchung.setBemerkung((String) aValue);
                    fireTableRowsUpdated(rowIndex, rowIndex);
                    break;
                default:
                    log.warn("Keine Spalte für angegebenen Index vorhanden: " + columnIndex);
                    return;
            }
            if (selectedBuchung != null && oldBuchung != null && selectedBuchung.getId() == null) {
                log.debug("Prüfe ob die Nutzung sich wirklich verändert hat");
                if (NutzungsBuchung.NUTZUNG_HISTORY_EQUALATOR.pedanticEquals(oldBuchung, selectedBuchung)) {
                    log.debug("Nutzungen sind gleich muss keine neue angelegt werden");
                    selectedNutzung.removeOpenNutzung();
                }
            }
            fireTableDataChanged();
        } catch (Exception ex) {
            log.error("Fehler beim setzen von Daten in dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
        }
    }

    public void addNutzung(Nutzung nutzung) {
        if (nutzung != null) {
            allNutzungen.add(nutzung);            
            refreshTableModel();
        } else {
            log.debug("Nutzung kann nicht hinzugefügt werden ist null.");
        }
    }

    public NutzungsBuchung getNutzungAtRow(int rowIndex) {
        return currentNutzungen.get(rowIndex).getBuchungForDate(currentDate);
    }

    public void removeNutzung(int rowIndex) throws TerminateNutzungNotPossibleException {        
        final Nutzung nutzungToRemove = currentNutzungen.get(rowIndex);
        final NutzungsBuchung selectedBuchung = nutzungToRemove.getBuchungForDate(currentDate);
        if (nutzungToRemove != null) {
            log.debug("Nutzung die entfernt werden soll ist in Modell vorhanden.");
            if (nutzungToRemove.getId() == null) {
                log.debug("Nutzung die Entfernt wurde war noch nicht in Datenbank");
                allNutzungen.remove(nutzungToRemove);            
            } else {
                log.debug("Nutzung ist in Datenbank vorhanden");
                if (selectedBuchung != null && selectedBuchung.getId() == null) {
                    log.debug("Die Betroffene Buchung ist neu und kann gelöscht werden");
                    nutzungToRemove.removeOpenNutzung();
                } else {
                    log.debug("Die Betroffene Buchung ist in der Datenbank gespeichert. Komplette Nutzung wird historisch gesetzt");                                        
                    final Date terminationDate = new Date();
                    log.debug("Termination date: "+terminationDate);
                    nutzungToRemove.terminateNutzung(terminationDate);
                    log.debug("Terminated Nutzung Date: "+nutzungToRemove.getTerminalBuchung().getGueltigbis());
                    log.debug("Termination id: "+nutzungToRemove.getTerminalBuchung().getId());
                    log.debug("BuchungForDate: "+nutzungToRemove.getBuchungForDate(terminationDate));
                }
            }            
        }
        refreshTableModel();
    }

    public ArrayList<NutzungsBuchung> getAllBuchungen() {
        final ArrayList<NutzungsBuchung> sortedNutzungen = new ArrayList<NutzungsBuchung>();
        for (Nutzung curNutzung : allNutzungen) {
            if (curNutzung.getBuchungsCount() > 0) {
                for (NutzungsBuchung curBuchung : curNutzung.getNutzungsBuchungen()) {
                    sortedNutzungen.add(curBuchung);
                }
            }
        }
        if (sortedNutzungen.size() > 0) {
            //ToDO NKF Comparator
            Collections.sort(sortedNutzungen, NutzungsBuchung.DATE_COMPARATOR);
        }
        return sortedNutzungen;
    }

    public void refreshTableModel(Set<Nutzung> nutzungen) {
        try {
            log.debug("Refresh des NKFTableModell");

            if (nutzungen != null) {
                this.allNutzungen = new ArrayList<Nutzung>(nutzungen);

            } else {
                allNutzungen.clear();
            }
            setModelToHistoryDate(currentDate);
        } catch (Exception ex) {
            log.error("Fehler beim refreshen des Models", ex);
            this.currentNutzungen = new ArrayList<Nutzung>();
            this.allNutzungen = new ArrayList<Nutzung>();
        }
    }

    public ArrayList<NutzungsBuchung> getselectedBuchungen() {
        log.debug("Anzahl aktueller Nutzungen: " + currentNutzungen.size());
        final ArrayList<NutzungsBuchung> selectedBuchungen = new ArrayList<NutzungsBuchung>();
        for (Nutzung curNutzung : currentNutzungen) {
            NutzungsBuchung curBuchung = curNutzung.getBuchungForDate(currentDate);
            if (curBuchung != null) {
                selectedBuchungen.add(curBuchung);
            }
        }
        return selectedBuchungen;
    }

    public ArrayList<NutzungsBuchung> getCurrentBuchungen() {
        log.debug("Anzahl aller Nutzungen: " + allNutzungen.size());
        final ArrayList<NutzungsBuchung> selectedBuchungen = new ArrayList<NutzungsBuchung>();
        for (Nutzung curNutzung : allNutzungen) {
            NutzungsBuchung curBuchung = curNutzung.getOpenBuchung();
            if (curBuchung != null) {
                selectedBuchungen.add(curBuchung);
            }
        }
        log.debug("Anzahl offener Nutzungen: " + selectedBuchungen.size());
        return selectedBuchungen;
    }

    public ArrayList<Nutzung> getSelectedNutzungen() {
        return currentNutzungen;
    }

    public ArrayList<Nutzung> getAllNutzungen() {
        return allNutzungen;
    }

    public void setModelToHistoryDate(final Date historyDate) {
        log.debug("setModelToHistoryDate: " + historyDate, new CurrentStackTrace());
        log.debug("anzahl rows: "+getRowCount());
        log.debug("AnzahlNutzungen: "+allNutzungen.size());
        currentDate = historyDate;
        currentNutzungen.clear();
        for (Nutzung curNutzung : allNutzungen) {
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
            if (curNutzung.getBuchungForDate(historyDate) != null) {
                log.debug("Nutzung für Datum gefunden");
                currentNutzungen.add(curNutzung);
            }
        }
        log.debug("anzahl rows: "+getRowCount());
        fireTableDataChanged();
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public int getIndexOfNutzung(NutzungsBuchung nutzung) {
        return currentNutzungen.indexOf(nutzung);
    }

    public void refreshTableModel() {
        setModelToHistoryDate(currentDate);
    }
}
