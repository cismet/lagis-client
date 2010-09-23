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
import de.cismet.lagisEE.entity.core.Nutzung;
import de.cismet.lagisEE.entity.core.NutzungsBuchung;
import de.cismet.lagisEE.entity.core.hardwired.Anlageklasse;
import de.cismet.lagisEE.entity.core.hardwired.Bebauung;
import de.cismet.lagisEE.entity.core.hardwired.Flaechennutzung;

import de.cismet.lagisEE.entity.core.hardwired.Nutzungsart;

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
    private ArrayList<Nutzung> selectedNutzungen;
    //private Vector<Nutzung> nutzungenHistorisch;
    private final static String[] COLUMN_HEADER = {"Nutzungs Nr.", "Anlageklasse", "Nutzungsartenschlüssel", "Nutzungsart", "Flächennutzungsplan", "Bebauungsplan", "Fläche m²", "Quadratmeterpreis", "Gesamtpreis", "Stille Reserve", "Buchwert", "Bemerkung"};
    private DecimalFormat df = LagisBroker.getCurrencyFormatter();
    private boolean isInEditMode = false;
    private Date currentDate = null;

    /** Creates a new instance of NKFTableModel */
    public NKFTableModel() {
        allNutzungen = new ArrayList<Nutzung>();
        selectedNutzungen = new ArrayList<Nutzung>();
        //nutzungenHistorisch = new Vector<Nutzung>();
    }

    public NKFTableModel(Set<Nutzung> nutzungen) {
        try {
            allNutzungen = new ArrayList<Nutzung>(nutzungen);
            selectedNutzungen = new ArrayList<Nutzung>();
//            Iterator<NutzungsBuchung> it = allNutzungen.iterator();
//            while (it.hasNext()) {
//                NutzungsBuchung curNutzung = it.next();
//                if (curNutzung.getGueltigbis() == null) {
//                    selection.add(curNutzung);
//                }
//            }
            log.debug("Anzahl aktueller Nutzungen: " + selectedNutzungen.size());
            log.debug("Anzahl aller Nutzungen: " + allNutzungen.size());
//            this.nutzungenHistorisch = new Vector<Nutzung>();
//            Iterator<Nutzung> it = nutzungen.iterator();
//            while(it.hasNext()){
//                Nutzung curNutzung = it.next();
//                if(curNutzung.getGueltigbis() != null){
//                    nutzungenHistorisch.add(curNutzung);
//                }
//            }
            //nutzungen.removeAll(nutzungenHistorisch);
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            this.allNutzungen = new ArrayList<Nutzung>();
            this.selectedNutzungen = new ArrayList<Nutzung>();
            //this.nutzungenHistorisch = new Vector<Nutzung>();
        }
    }

    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {            
            final Nutzung nutzung = selectedNutzungen.get(rowIndex);
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
                    if(stilleReserve != null){
                        return selectedBuchung.getGesamtpreis()-stilleReserve;
                    } else {
                        //ToDo NKF
                        return selectedBuchung.getGesamtpreis();
                    }
                case 9:
                    //ToDo NKF
                    if(stilleReserve != null){
                        return stilleReserve;
                    } else {
                        return 0.0;
                    }
                case 10:
                    //ToDo gibt so wenig Buchwerte extra Spalte dafür ?
                    if(selectedBuchung.getIstBuchwert()){
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
        return selectedNutzungen.size();
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
            return (COLUMN_HEADER.length > columnIndex) && (selectedNutzungen.size() > rowIndex) && isInEditMode && selectedNutzungen.get(rowIndex).getBuchungForDate(currentDate).getGueltigbis() == null;
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
            final Nutzung selectedNutzung = selectedNutzungen.get(rowIndex);                        
            NutzungsBuchung selectedBuchung = selectedNutzung.getBuchungForDate(currentDate);
            NutzungsBuchung oldBuchung = null;            
            if (selectedBuchung.getId() != null) {
                log.debug("neue Buchung wird angelegt");                                
                final NutzungsBuchung newBuchung = selectedBuchung.cloneBuchung();                               
                selectedNutzung.addBuchung(newBuchung);
                oldBuchung=selectedBuchung;
                selectedBuchung = newBuchung;
            } else {                                
                oldBuchung=selectedNutzung.getPreviousBuchung();
            }            
            switch (columnIndex) {
                case 1:
                    selectedBuchung.setAnlageklasse((Anlageklasse) aValue);
                    break;
                case 2:
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
                    selectedBuchung.setBemerkung((String) aValue);
                    fireTableRowsUpdated(rowIndex, rowIndex);
                    break;
                default:
                    log.warn("Keine Spalte für angegebenen Index vorhanden: " + columnIndex);
                    return;
            }          
            if(selectedBuchung != null && oldBuchung != null && selectedBuchung.getId() == null){
                log.debug("Prüfe ob die Nutzung sich wirklich verändert hat");                
                if(NutzungsBuchung.NUTZUNG_HISTORY_EQUALATOR.pedanticEquals(oldBuchung, selectedBuchung)){
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
            if (nutzung.getBuchungForDate(currentDate) != null) {
                selectedNutzungen.add(nutzung);
            }
            fireTableDataChanged();
        } else {
            log.debug("Nutzung kann nicht hinzugefügt werden ist null.");
        }
    }

    public NutzungsBuchung getNutzungAtRow(int rowIndex) {
        return selectedNutzungen.get(rowIndex).getBuchungForDate(currentDate);
    }

    public boolean removeNutzung(int rowIndex) {
        // Nutzung nutzung = nutzungen.get(rowIndex);
//        if(nutzung != null && nutzung.getGeometrie() != null && nutzung.getGeometrie().getGeometry() != null ){
//            LagisBroker.getInstance().getMappingComponent().getFeatureCollection().removeFeature(nutzung.getGeometrie());
//        }
        //allNutzungen.get
        Nutzung nutzung = selectedNutzungen.get(rowIndex);
        if (nutzung != null) {
            log.debug("Nutzung die entfernt werden soll ist in Modell vorhanden.");
            if (nutzung.getId() == null) {
                log.debug("Nutzung die Entfernt wurde war noch nicht in Datenbank");
                selectedNutzungen.remove(rowIndex);
                allNutzungen.remove(rowIndex);
                fireTableDataChanged();
                return true;
            } else {
                System.out.println("Nutzung in Datenbank gesetzt wird historisch gesetzt");
                final Nutzung nutzungToRemove = selectedNutzungen.get(rowIndex);
                selectedNutzungen.remove(rowIndex);                
                    NutzungsBuchung buchungToTerminate = nutzung.getBuchungForDate(null);
                    buchungToTerminate.setGueltigbis(new Date());
                    buchungToTerminate.setSollGeloeschtWerden(true);
                    setModelToHistoryDate(buchungToTerminate.getGueltigbis());
                //TODO NKF EXCEPTION;
                //alles ohne if try-catch
            }
        }
        return true;
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

//    //TODO SAME AS NUTZUNG
//    class DateRetriever extends SwingWorker<Date, Void> {
//        //private static final int MODE_ADD_NUTZUNG=0;
//        private static final int MODE_SET_NUTZUNG_HISTORIC = 1;
//        private int currentMode = 0;
//        private NutzungsBuchung nutzung;
//        private int resultIndex;
//
//        //TODO Knaup
//        DateRetriever(int mode, NutzungsBuchung nutzung, int resultIndex) {
//            super();
//            currentMode = mode;
//            this.nutzung = nutzung;
//            this.resultIndex = resultIndex;
//        }
//
//        protected Date doInBackground() throws Exception {
//            try {
//                return EJBroker.getInstance().getCurrentDate();
//            } catch (Exception ex) {
//                log.error("Fehler beim abrufen des Datums vom Server", ex);
//                return null;
//            }
//        }
//
//        protected void done() {
//            super.done();
//            if (isCancelled()) {
//                log.warn("Swing Worker wurde abgebrochen");
//                return;
//            }
//            try {
//                Date serverDate = get();
//                if (serverDate != null) {
//                    switch (currentMode) {
////                        case MODE_ADD_NUTZUNG:
////                            final Nutzung tmp = new Nutzung();
////                            tmp.setGueltigvon(serverDate);
////                            tableModel.addNutzung(tmp);
////                            break;
//                        case MODE_SET_NUTZUNG_HISTORIC:
//                            allNutzungen.get(resultIndex).setGueltigbis(serverDate);
//                            allNutzungen.get(resultIndex).setSollGeloeschtWerden(true);
//                            selectedNutzungen.remove(nutzung);
//                            fireTableDataChanged();
//                            break;
//                        default:
//                            log.warn("Mode is unbekannt tue nichts");
//                    }
//                } else {
//                    log.warn("Es konnte kein Datum vom Server abgerufen werden");
//                }
//            } catch (Exception ex) {
//                log.error("Fehler beim verarbeiten des Results vom DateRetriever (SwingWorker)", ex);
//                return;
//            }
//        }
//    }
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
            this.selectedNutzungen = new ArrayList<Nutzung>();
            this.allNutzungen = new ArrayList<Nutzung>();
        }
    }

    public ArrayList<NutzungsBuchung> getselectedBuchungen() {
        log.debug("Anzahl aktueller Nutzungen: " + selectedNutzungen.size());
        final ArrayList<NutzungsBuchung> selectedBuchungen = new ArrayList<NutzungsBuchung>();
        for (Nutzung curNutzung : selectedNutzungen) {
            NutzungsBuchung curBuchung = curNutzung.getBuchungForDate(currentDate);
            if (curBuchung != null) {
                selectedBuchungen.add(curBuchung);
            }
        }
        return selectedBuchungen;
    }

    public ArrayList<NutzungsBuchung> getCurrentBuchungen() {
        log.debug("Anzahl aktueller Nutzungen: " + selectedNutzungen.size());
        final ArrayList<NutzungsBuchung> selectedBuchungen = new ArrayList<NutzungsBuchung>();
        for (Nutzung curNutzung : selectedNutzungen) {
            NutzungsBuchung curBuchung = curNutzung.getOpenBuchung();
            if (curBuchung != null) {
                selectedBuchungen.add(curBuchung);
            }
        }
        return selectedBuchungen;
    }

    public ArrayList<Nutzung> getSelectedNutzungen() {
        return selectedNutzungen;
    }

    public ArrayList<Nutzung> getAllNutzungen() {
        return allNutzungen;
    }

    public void setModelToHistoryDate(Date historyDate) {
        log.debug("setModelToHistoryDate: "+historyDate);
        currentDate=historyDate;
        selectedNutzungen.clear();
        for (Nutzung curNutzung : allNutzungen) {
            if (curNutzung.getBuchungForDate(historyDate) != null) {
                log.debug("Nutzung für Datum gefunden");
                selectedNutzungen.add(curNutzung);
            }
        }
        fireTableDataChanged();
    }

    public Date getCurrentDate(){
        return currentDate;
    }

    public int getIndexOfNutzung(NutzungsBuchung nutzung) {
        return selectedNutzungen.indexOf(nutzung);
    }

    public void refreshTableModel(){
        setModelToHistoryDate(currentDate);
    }
}
