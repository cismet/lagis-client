/*
 * NKFTableModel.java
 *
 * Created on 25. April 2007, 11:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.utillity.BebauungsVector;
import de.cismet.lagis.utillity.FlaechennutzungsVector;
import de.cismet.lagisEE.entity.core.Nutzung;
import de.cismet.lagisEE.entity.core.hardwired.Anlageklasse;
import de.cismet.lagisEE.entity.core.hardwired.Bebauung;
import de.cismet.lagisEE.entity.core.hardwired.Flaechennutzung;

import de.cismet.lagisEE.entity.core.hardwired.Nutzungsart;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
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
    private Vector<Nutzung> allNutzungen;
    private Vector<Nutzung> currentNutzungen;
    //private Vector<Nutzung> nutzungenHistorisch;
    private final static String[] COLUMN_HEADER = {"Anlageklasse", "Nutzungsartenschlüssel", "Nutzungsart", "Flächennutzungsplan", "Bebauungsplan", "Fläche m²", "Quadratmeterpreis", "Gesamtpreis", "Stille Reserve", "Buchungsstatus", "Bemerkung"};
    private DecimalFormat df = LagisBroker.getCurrencyFormatter();
    private boolean isInEditMode = false;

    /** Creates a new instance of NKFTableModel */
    public NKFTableModel() {
        allNutzungen = new Vector<Nutzung>();
        currentNutzungen = new Vector<Nutzung>();
    //nutzungenHistorisch = new Vector<Nutzung>();
    }

    public NKFTableModel(Set<Nutzung> nutzungen) {
        try {
            this.allNutzungen = new Vector<Nutzung>(nutzungen);
            currentNutzungen = new Vector<Nutzung>();
            Iterator<Nutzung> it = allNutzungen.iterator();
            while (it.hasNext()) {
                Nutzung curNutzung = it.next();
                if (curNutzung.getGueltigbis() == null) {
                    currentNutzungen.add(curNutzung);
                }
            }
            log.debug("Anzahl aktueller Nutzungen: " + currentNutzungen.size());
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
            this.allNutzungen = new Vector<Nutzung>();
            this.currentNutzungen = new Vector<Nutzung>();
        //this.nutzungenHistorisch = new Vector<Nutzung>();
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            Nutzung nutzung = currentNutzungen.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return nutzung.getAnlageklasse();
                case 1:
                    return nutzung.getNutzungsart();
                case 2:
                    if (nutzung.getNutzungsart() != null && nutzung.getNutzungsart().getBezeichnung() != null) {
                        return nutzung.getNutzungsart().getBezeichnung();
                    } else {
                        return null;
                    }
                case 3:
                    //TODO Special GUI for editing
                    if (nutzung.getFlaechennutzung() != null) {
                        return new FlaechennutzungsVector(nutzung.getFlaechennutzung());
                    } else {
                        return new FlaechennutzungsVector();
                    }
                case 4:
                    if (nutzung.getBebauung() != null) {
                        return new BebauungsVector(nutzung.getBebauung());
                    } else {
                        return new BebauungsVector();
                    }
//                    if(nutzung.getBebauung() != null){
//                        Iterator<Bebauung> it = nutzung.getBebauung().iterator();
//                        String result = new String();
//                        while(it.hasNext()){
//                            result+=it.next().getBezeichnung();
//                            result+=", ";
//                        }
//                        return result.length()>0 ? result.substring(0,result.length()-2) : null;
//                    } else {
//                        return null;
//                    }
//                    if(plan != null && plan.getBebauung() != null && plan.getBebauung().getArt() != null){
//                        return nutzung.getPlan().getBebauung().getArt();
//                    } else {
//                        return null;
//                    }
                case 5:
                    return nutzung.getFlaeche();
                case 6:
                    return nutzung.getQuadratmeterpreis();
                case 7:
                    Double preis = nutzung.getQuadratmeterpreis();
                    Integer flaeche = nutzung.getFlaeche();

                    if (nutzung.getAlterGesamtpreis() != null && nutzung.getStilleReserve() != null && nutzung.getStilleReserve() != 0.0 && flaeche != null && preis != null) {
                        double diff = ((nutzung.getFlaeche() * nutzung.getQuadratmeterpreis()) - nutzung.getAlterGesamtpreis());
                        if (diff > 0.0) {
                            log.debug("erhöhung");
                            return (preis * flaeche) - nutzung.getStilleReserve() - diff;
                        } else {
                            log.debug("Verminderung");
                            if ((nutzung.getStilleReserve() + diff) > 0) {
                                log.debug("Stille Reserve reicht aus");
                                return (preis * flaeche) - (nutzung.getStilleReserve() + diff);
                            } else {
                                log.debug("Stille Reserve reicht nicht aus");
                                return nutzung.getAlterGesamtpreis() - nutzung.getStilleReserve();
                            }
                        }
                    } else if (nutzung.getAlterGesamtpreis() != null && nutzung.getFlaeche() != null && nutzung.getQuadratmeterpreis() != null && (nutzung.getAlterGesamtpreis() - (nutzung.getFlaeche() * nutzung.getQuadratmeterpreis())) < 0.0) {
                        log.debug("Gesamtpreis wurde erhöht");
                        return nutzung.getAlterGesamtpreis();
                    } else if (preis != null && flaeche != null) {
                        if (nutzung.getStilleReserve() != null && nutzung.getStilleReserve() != 0.0) {
                            return (preis * flaeche) - nutzung.getStilleReserve();
                        } else {
                            return (preis * flaeche);
                        }
                    } else {
                        return null;
                    }
                case 8:
                    //if((stilleReserve=nutzung.getStilleReserve()) != null && stilleReserve.getQuadratmeterpreis() != null && stilleReserve.getFlaeche() != null && nutzung.getFlaeche() != null && nutzung.getQuadratmeterpreis() != null){
                    if (nutzung.getStilleReserve() != null && nutzung.getStilleReserve() != 0.0 && nutzung.getAlterGesamtpreis() != null && nutzung.getFlaeche() != null && nutzung.getQuadratmeterpreis() != null) {
                        double result = nutzung.getStilleReserve() + ((nutzung.getFlaeche() * nutzung.getQuadratmeterpreis()) - nutzung.getAlterGesamtpreis());
//                        if(result < 0.0){
//                            return 0.0;
//                        } else {
//                            return result;
//                        }                        
                        return result;
                    } else if (nutzung.getAlterGesamtpreis() != null && nutzung.getFlaeche() != null && nutzung.getQuadratmeterpreis() != null && (nutzung.getAlterGesamtpreis() - (nutzung.getFlaeche() * nutzung.getQuadratmeterpreis())) < 0.0) {
                        log.debug("Gesamtpreis wurde erhöht");
                        return Math.abs((nutzung.getAlterGesamtpreis() - (nutzung.getFlaeche() * nutzung.getQuadratmeterpreis())));
                    } else if (nutzung.getStilleReserve() != null) {
                        return nutzung.getStilleReserve();
                    } else {
                        return 0.0;
                    }
                case 9:
                    if (nutzung.getIstGebucht() == null) {
                        return statusUnknown;
                    } else if (nutzung.getIstGebucht()) {
                        return booked;
                    } else {
                        return notBooked;
                    }
                case 10:
                    return nutzung.getBemerkung();
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
        if (columnIndex == 2 || columnIndex == 7 || columnIndex == 8 || columnIndex == 9) {
            return false;
        } else {
            return (COLUMN_HEADER.length > columnIndex) && (currentNutzungen.size() > rowIndex) && isInEditMode && currentNutzungen.get(rowIndex).getGueltigbis() == null;
        }

    }

    public void setIsInEditMode(boolean isEditable) {
        isInEditMode = isEditable;
    }

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Anlageklasse.class;
            case 1:
                return Nutzungsart.class;
            case 2:
                return String.class;
            case 3:
                return Vector.class;
            case 4:
                return Vector.class;
            case 5:
                return Integer.class;
            case 6:
                return Double.class;
            case 7:
                return Double.class;
            case 8:
                return Double.class;
            case 9:
                return ImageIcon.class;
            case 10:
                return String.class;
            default:
                log.warn("Die gewünschte Spalte exitiert nicht, es kann keine Klasse zurück geliefert werden");
                return null;
        }
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            Nutzung nutzung = currentNutzungen.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    nutzung.setAnlageklasse((Anlageklasse) aValue);
                    break;
                case 1:
                    nutzung.setNutzungsart((Nutzungsart) aValue);
                    fireTableRowsUpdated(rowIndex, rowIndex);
                    break;
                case 3:
                    Set<Flaechennutzung> tmpNutz = nutzung.getFlaechennutzung();
                    if (tmpNutz != null) {
                        tmpNutz.clear();
                    } else {
                        nutzung.setFlaechennutzung(new HashSet<Flaechennutzung>());
                        tmpNutz = nutzung.getFlaechennutzung();
                    }
                    //tmpNutz.addAll((Collection<Flaechennutzung>) aValue);
                    Iterator<Flaechennutzung> itF = ((Collection<Flaechennutzung>) aValue).iterator();
                    while (itF.hasNext()) {
                        Flaechennutzung fNutzung = itF.next();
                        if (fNutzung.getBezeichnung() != null) {
                            tmpNutz.add(fNutzung);
                        }
                    }
                    nutzung.setFlaechennutzung(tmpNutz);
                    break;
                case 4:
                    Set<Bebauung> tmpBebauung = nutzung.getBebauung();
                    if (tmpBebauung != null) {
                        tmpBebauung.clear();
                    } else {
                        nutzung.setBebauung(new HashSet<Bebauung>());
                        tmpBebauung = nutzung.getBebauung();
                    }
                    //tmpNutz.addAll((Collection<Flaechennutzung>) aValue);
                    Iterator<Bebauung> itB = ((Collection<Bebauung>) aValue).iterator();
                    while (itB.hasNext()) {
                        Bebauung bebauung = itB.next();
                        if (bebauung.getBezeichnung() != null) {
                            tmpBebauung.add(bebauung);
                        }
                    }
                    nutzung.setBebauung(tmpBebauung);
                    break;
                case 5:
//                    if (nutzung.getFlaeche() != null && nutzung.getQuadratmeterpreis() != null) {
//                        nutzung.setAlterGesamtpreis(nutzung.getFlaeche() * nutzung.getQuadratmeterpreis());
//                    }
                    nutzung.setFlaeche((Integer) aValue);
                    fireTableRowsUpdated(rowIndex, rowIndex);
                    break;
                case 6:
//                    if (nutzung.getFlaeche() != null && nutzung.getQuadratmeterpreis() != null) {
//                        nutzung.setAlterGesamtpreis(nutzung.getFlaeche() * nutzung.getQuadratmeterpreis());
//                    }
                    nutzung.setQuadratmeterpreis((Double) aValue);
                    fireTableRowsUpdated(rowIndex, rowIndex);
                    break;
                case 10:
                    nutzung.setBemerkung((String) aValue);
                    fireTableRowsUpdated(rowIndex, rowIndex);
                    break;
                default:
                    log.warn("Keine Spalte für angegebenen Index vorhanden: " + columnIndex);
                    return;
            }
            fireTableDataChanged();
        } catch (Exception ex) {
            log.error("Fehler beim setzen von Daten in dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
        }
    }

    public void addNutzung(Nutzung nutzung) {
        currentNutzungen.add(nutzung);
        allNutzungen.add(nutzung);
        fireTableDataChanged();
    }

    public Nutzung getNutzungAtRow(int rowIndex) {
        return currentNutzungen.get(rowIndex);
    }

    public boolean removeNutzung(int rowIndex) {
        // Nutzung nutzung = nutzungen.get(rowIndex);
//        if(nutzung != null && nutzung.getGeometrie() != null && nutzung.getGeometrie().getGeometry() != null ){
//            LagisBroker.getInstance().getMappingComponent().getFeatureCollection().removeFeature(nutzung.getGeometrie());
//        }
        //allNutzungen.get
        Nutzung nutzung = currentNutzungen.get(rowIndex);
        if (nutzung != null) {
            if (nutzung.getId() == null) {
                log.debug("Nutzung die Entfernt wurde war noch nicht in Datenbank");
                currentNutzungen.remove(rowIndex);
                allNutzungen.remove(rowIndex);
                fireTableDataChanged();
                return true;
            } else {
                log.debug("Nutzung die entfernt werden sollte war schon in Datenbank --> historisch");
                int result = allNutzungen.indexOf(nutzung);
                if (result != -1) {
                    DateRetriever dateRetriever = new DateRetriever(DateRetriever.MODE_SET_NUTZUNG_HISTORIC, nutzung, result);
                    dateRetriever.execute();
                    return false;
                } else {
                    log.warn("Nutzung in currentNutzung vorhanden aber nicht in allNutzungen");
                    currentNutzungen.remove(nutzung);
                    fireTableDataChanged();
                    return true;
                }
            }

        }
        return true;
    }

    //TODO SAME AS NUTZUNG    
    class DateRetriever extends SwingWorker<Date, Void> {
        //private static final int MODE_ADD_NUTZUNG=0;
        private static final int MODE_SET_NUTZUNG_HISTORIC = 1;
        private int currentMode = 0;
        private Nutzung nutzung;
        private int resultIndex;

        //TODO Knaup
        DateRetriever(int mode, Nutzung nutzung, int resultIndex) {
            super();
            currentMode = mode;
            this.nutzung = nutzung;
            this.resultIndex = resultIndex;
        }

        protected Date doInBackground() throws Exception {
            try {
                return EJBroker.getInstance().getCurrentDate();
            } catch (Exception ex) {
                log.error("Fehler beim abrufen des Datums vom Server", ex);
                return null;
            }
        }

        protected void done() {
            super.done();
            if (isCancelled()) {
                log.warn("Swing Worker wurde abgebrochen");
                return;
            }
            try {
                Date serverDate = get();
                if (serverDate != null) {
                    switch (currentMode) {
//                        case MODE_ADD_NUTZUNG:
//                            final Nutzung tmp = new Nutzung();
//                            tmp.setGueltigvon(serverDate);
//                            tableModel.addNutzung(tmp);
//                            break;
                        case MODE_SET_NUTZUNG_HISTORIC:
                            allNutzungen.get(resultIndex).setGueltigbis(serverDate);
                            allNutzungen.get(resultIndex).setSollGeloeschtWerden(true);
                            currentNutzungen.remove(nutzung);
                            fireTableDataChanged();
                            break;
                        default:
                            log.warn("Mode is unbekannt tue nichts");
                    }
                } else {
                    log.warn("Es konnte kein Datum vom Server abgerufen werden");
                }
            } catch (Exception ex) {
                log.error("Fehler beim verarbeiten des Results vom DateRetriever (SwingWorker)", ex);
                return;
            }
        }
    }

    public void refreshTableModel(Set<Nutzung> nutzungen) {
        try {
            log.debug("Refresh des NKFTableModell");

            if (nutzungen != null) {
                this.allNutzungen = new Vector<Nutzung>(nutzungen);
            } else {
                log.debug("Nutzungsvektor == null --> Erstelle Vektor.");
                this.allNutzungen = new Vector<Nutzung>();
            }
            currentNutzungen = new Vector<Nutzung>();
            Iterator<Nutzung> it = allNutzungen.iterator();
            while (it.hasNext()) {
                Nutzung curNutzung = it.next();
                if (curNutzung.getGueltigbis() == null) {
                    currentNutzungen.add(curNutzung);
                }
            }
            log.debug("Anzahl aktueller Nutzungen: " + currentNutzungen.size());
            log.debug("Anzahl aller Nutzungen: " + allNutzungen.size());
        } catch (Exception ex) {
            log.error("Fehler beim refreshen des Models", ex);
            this.currentNutzungen = new Vector<Nutzung>();
            this.allNutzungen = new Vector<Nutzung>();
        }
        fireTableDataChanged();
    }

    public Vector<Nutzung> getcurrentNutzungen() {
        log.debug("Anzahl aktueller Nutzungen: " + currentNutzungen.size());
        return currentNutzungen;
    }

    public Vector<Nutzung> getAllNutzungen() {
        return allNutzungen;
    }

    public void setModelToHistoryDate(Date historyDate) {
        currentNutzungen = new Vector<Nutzung>();
        //Display current Nutzungen
        if (historyDate == null) {
            Iterator<Nutzung> it = allNutzungen.iterator();
            while (it.hasNext()) {
                Nutzung curNutzung = it.next();
                if (curNutzung.getGueltigbis() == null) {
                    currentNutzungen.add(curNutzung);
                }
            }
        //Display Nutzungen by date
        } else {
            Iterator<Nutzung> it = allNutzungen.iterator();
            while (it.hasNext()) {
                Nutzung curNutzung = it.next();
                //GregorianCalendar calender = new GregorianCalendar();
                Date curGueltigbis = curNutzung.getGueltigbis();
                Date curGueltigvon = curNutzung.getGueltigvon();
                //TODO workaround try to do nicer
                if (curGueltigbis != null && curGueltigvon != null) {
//                    calender.setTime(curGueltigbis);
//                    calender.set(GregorianCalendar.HOUR,0);
//                    calender.set(GregorianCalendar.MINUTE,0);
//                    calender.set(GregorianCalendar.SECOND,0);
//                    calender.set(GregorianCalendar.MILLISECOND,0);
                    curGueltigbis = LagisBroker.getDateWithoutTime(curGueltigbis);
                    log.debug("gueltigbis: " + curGueltigbis + " millis: " + curGueltigbis.getTime());
//                    calender.setTime(curGueltigvon);
//                    calender.set(GregorianCalendar.HOUR,0);
//                    calender.set(GregorianCalendar.MINUTE,0);
//                    calender.set(GregorianCalendar.SECOND,0);
//                    calender.set(GregorianCalendar.MILLISECOND,0);
                    curGueltigvon = LagisBroker.getDateWithoutTime(curGueltigvon);
                    log.debug("gueltigvon: " + curGueltigvon + " millis: " + curGueltigvon.getTime());
//                    calender.setTime(historyDate);
//                    calender.set(GregorianCalendar.HOUR,0);
//                    calender.set(GregorianCalendar.MINUTE,0);
//                    calender.set(GregorianCalendar.SECOND,0);
//                      calender.set(GregorianCalendar.MILLISECOND,0);
                    historyDate = LagisBroker.getDateWithoutTime(historyDate);
                    log.debug("historyDate: " + historyDate + " millis: " + historyDate.getTime());
                    int compareGueltigVon = curGueltigvon.compareTo(historyDate);
                    int compareGueltigBis = curGueltigbis.compareTo(historyDate);
                    log.debug("compareGueltigbis " + compareGueltigBis);
                    log.debug("compareGueltigvon " + compareGueltigVon);
                    if (compareGueltigBis >= 0 && compareGueltigVon <= 0) {
                        currentNutzungen.add(curNutzung);
                    }
                }
                if (curGueltigbis == null && curGueltigvon != null) {
                    curGueltigvon = LagisBroker.getDateWithoutTime(curGueltigvon);
                    log.debug("gueltigvon: " + curGueltigvon + " millis: " + curGueltigvon.getTime());
                    historyDate = LagisBroker.getDateWithoutTime(historyDate);
                    log.debug("historyDate: " + historyDate + " millis: " + historyDate.getTime());
                    int compareGueltigVon = curGueltigvon.compareTo(historyDate);
                    log.debug("compareGueltigvon " + compareGueltigVon);
                    if (compareGueltigVon < 0) {
                        currentNutzungen.add(curNutzung);
                    }
                }
            }
        }
        fireTableDataChanged();
    }

    public int getIndexOfNutzung(Nutzung nutzung) {
        return currentNutzungen.indexOf(nutzung);
    }
}
