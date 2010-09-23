/*
 * NKFOverviewTableModel.java
 *
 * Created on 24. April 2007, 11:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.utillity.AnlagenklasseSumme;
import de.cismet.lagisEE.bean.Exception.IllegalNutzungStateException;
import de.cismet.lagisEE.entity.core.BuchungNotInNutzungException;
import de.cismet.lagisEE.entity.core.Nutzung;
import de.cismet.lagisEE.entity.core.NutzungsBuchung;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author Puhl
 */
public class NKFOverviewTableModel extends AbstractTableModel {

    private ArrayList<Nutzung> nutzungen = new ArrayList<Nutzung>();
    private static final String[] COLUMN_HEADER = {"Anlageklasse", "Summe"};
    private ArrayList<AnlagenklasseSumme> data = new ArrayList<AnlagenklasseSumme>();
    private DecimalFormat df = LagisBroker.getCurrencyFormatter();
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Date currentDate = null;
    private double stilleReserve = 0.0;

    /** Creates a new instance of NKFOverviewTableModel */
    public NKFOverviewTableModel() {
        nutzungen = new ArrayList<Nutzung>();
    }

    public NKFOverviewTableModel(ArrayList<Nutzung> nutzungen) {
        try {
            log.debug("Konstruktor Nutzungen");
            this.nutzungen = new ArrayList<Nutzung>(nutzungen);
            calculateSum();
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            this.nutzungen = new ArrayList<Nutzung>();
        }
    }

    public synchronized void refreshModel(Set<Nutzung> nutzungen) {
        if (nutzungen != null) {
            refreshModel(new ArrayList<Nutzung>(nutzungen));
        } else {
            refreshModel(new ArrayList());
        }

    }

    public synchronized void refreshModel(ArrayList<Nutzung> nutzungen) {
        try {
            log.debug("Refresh Nutzungen");
            this.nutzungen = new ArrayList<Nutzung>(nutzungen);
            calculateSum();

        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            this.nutzungen = new ArrayList<Nutzung>();
        }

        fireTableDataChanged();
    }

    public Object getValueAt(
            int rowIndex, int columnIndex) {
        try {
            AnlagenklasseSumme summe = data.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return summe.getAnlageklasse().getSchluessel();
                case 1:
                    return df.format(summe.getSumme());
                default:
                    return "Spalte ist nicht definiert";
            }

        } catch (Exception ex) {
            log.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
            return null;
        }

    }

    public int getRowCount() {
        return data.size();
    }

    public int getColumnCount() {
        return COLUMN_HEADER.length;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        super.setValueAt(aValue, rowIndex, columnIndex);
    }

    private synchronized void calculateSum() {
        log.debug("Calculate Sum");
        stilleReserve=0.0;
        data = new ArrayList<AnlagenklasseSumme>();
        for (Nutzung currentNutzung : nutzungen) {
            log.debug("curNutzung:"+currentNutzung);
            log.debug("tableModelDate:"+currentDate);
                NutzungsBuchung currentBuchung = currentNutzung.getBuchungForDate(currentDate);
                if (currentBuchung != null) {
                    log.debug("currentBuchung: "+currentBuchung);
                    if (currentBuchung.getAnlageklasse() != null && currentBuchung.getGesamtpreis() != null) {
                        log.debug("Anlageklasse & Gesamtpreis != null");
                        int index = 0;
                        Iterator<AnlagenklasseSumme> itAS = data.iterator();
                        //System.out.println(data.size());
                        //System.out.println("currentNutz:"+currentNutzung.getAnlageklasse().getSchluessel());
                        boolean isAlreadyInVector = false;
                        //Das hier ist Käse Code dupliziert;
                        while (itAS.hasNext()) {
                            log.debug("vektor nicht leer");
                            AnlagenklasseSumme curSumme = itAS.next();
                            if(curSumme.equals(Double.NaN)){
                                log.debug("Bei der Berechnung der Summen ist ein Fehler aufgetreten: Keine weitere Berechnung");
                                continue;
                            }
                            if (curSumme.equals(currentBuchung.getAnlageklasse())) {
                                log.debug("Element der anlagensumme vorhanden");

                                //ToDo NKF muss behandelt werden und dem Benutzer mitgeteilt werden
                                try {
                                    Double curStilleReseve = currentNutzung.getStilleReserveForBuchung(currentBuchung);
                                    if (curStilleReseve != null) {
                                        stilleReserve+=curStilleReseve;                                        
                                        curSumme.setSumme(curSumme.getSumme() + (currentBuchung.getGesamtpreis() - curStilleReseve));
                                    }
                                }   catch (BuchungNotInNutzungException ex) {
                                    log.error("Stille Reserve konnte nicht berechnet werden: Fehlerhalfte Buchung");
                                    stilleReserve = Double.NaN;
                                    curSumme.setSumme(Double.NaN);

                                } catch (IllegalNutzungStateException ex) {
                                    log.error("Stille Reserve konnte nicht berechnet werden: Kein Buchwert");
                                    stilleReserve = Double.NaN;
                                    curSumme.setSumme(Double.NaN);
                                } 

                                isAlreadyInVector = true;
                            }
                        }
                        log.debug("nach while");
                            if (!isAlreadyInVector) {
                                log.debug("Element der anlagensumme hinzugefügt");
                                AnlagenklasseSumme tmp = new AnlagenklasseSumme(currentBuchung.getAnlageklasse());
                                try {
                                    Double curStilleReseve = currentNutzung.getStilleReserveForBuchung(currentBuchung);
                                    //ToDo NKF
                                    if (curStilleReseve != null) {
                                        stilleReserve+=curStilleReseve;                                        
                                        tmp.setSumme((currentBuchung.getGesamtpreis()) - curStilleReseve);
                                    }

                                 }   catch (BuchungNotInNutzungException ex) {
                                    log.error("Stille Reserve konnte nicht berechnet werden: Fehlerhalfte Buchung");
                                    tmp.setSumme(Double.NaN);                                    
                                } catch (IllegalNutzungStateException ex) {
                                    log.error("Stille Reserve konnte nicht berechnet werden: Kein Buchwert");
                                    tmp.setSumme(Double.NaN);                                    
                                }

                                data.add(tmp);
                            }

                    }                
            }
        }
        Collections.sort((ArrayList) data);
    }

    public double getStilleReserve() {
        return stilleReserve;
    }
//    private synchronized void updateStilleReservenBetrag(NutzungsContainer nutzungen) {
//        boolean containsHistoricNutzung = false;
//        if (nutzungen != null) {
//            Iterator<Nutzung> it = nutzungen.iterator();
//            double stilleReservenSumme = 0.0;
//            while (it.hasNext()) {
//                Nutzung currentNutzung = it.next();
//                //TODO NKF Benutzer muss benachrichtigt werden
//                try {
////                if (tableModel.hasNutzungSuccessor(currentNutzung)) {
////                    log.debug("Nutzung hat einen Nachfolger und wird für Stille Reserve nicht berücksichtigt");
////                    continue;
////                } else {
////                    log.debug("Nutzung hat keinen Nachfolger --> wird für Stille Reserve berücksichtigt");
////                }
//                    if (currentNutzung.getStilleReserve() != null) {
//                        stilleReservenSumme += currentNutzung.getStilleReserve();
//                    }
//                } catch (IllegalNutzungStateException ex) {
//                    log.error("Stille Reserve konnte nicht berechnet werden.",ex);
//                }
//                //ToDo NKF
////                if (currentNutzung.getGueltigbis() != null) {
////                    containsHistoricNutzung = true;
////                }
//            }
////            if (containsHistoricNutzung) {
////                lblStilleReservenBetrag.setText("-/-");
////                btnBuchen.setEnabled(false);
////            } else {
//            lblStilleReservenBetrag.setText(LagisBroker.getCurrencyFormatter().format(stilleReservenSumme));
//            if (stilleReservenSumme != 0.0 && LagisBroker.getInstance().isInEditMode()) {
//                if (!containsHistoricNutzung) {
//                    btnBuchen.setEnabled(true);
//                } else {
//                    btnBuchen.setEnabled(false);
//                }
//            } else {
//                btnBuchen.setEnabled(false);
//            }
////            }
//            if(containsHistoricNutzung){
//                lblHistoricIcon.setIcon(icoHistoricIcon);
//            } else {
//                lblHistoricIcon.setIcon(icoHistoricIconDummy);
//            }
//        } else {
//            lblStilleReservenBetrag.setText(LagisBroker.getCurrencyFormatter().format(0.0));
//            btnBuchen.setEnabled(false);
//        }
//    }

//    public boolean containsHistoricNutzung() {
//        Iterator<Nutzung> it = nutzungen.iterator();
//        while (it.hasNext()) {
//            Nutzung currentNutzung = it.next();
//            if (currentNutzung.getGueltigbis() != null) {
//                System.out.println("Nutzung hat einen Nachfolger");
//                return true;
//            }
//        }
//        return false;
//    }
    @Override
    public String getColumnName(
            int column) {
        return COLUMN_HEADER[column];
    }

    public ArrayList<Nutzung> getAllNutzungen() {
        return nutzungen;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public Date getCurrentDate() {
        return currentDate;
    }
//    @Override
//    public Class<?> getColumnClass(int columnIndex) {
//        return getValueAt(0,columnIndex).getClass();
//    }
}
