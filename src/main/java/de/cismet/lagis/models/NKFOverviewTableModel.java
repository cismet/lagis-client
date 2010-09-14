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
import de.cismet.lagisEE.entity.core.Nutzung;
import de.cismet.lagisEE.entity.core.NutzungsBuchung;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.openide.util.Exceptions;

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

    /** Creates a new instance of NKFOverviewTableModel */
    public NKFOverviewTableModel() {
        nutzungen = new ArrayList<Nutzung>();
    }

    public NKFOverviewTableModel(Set<Nutzung> nutzungen) {
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

    public Object getValueAt(int rowIndex, int columnIndex) {
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
        data = new ArrayList<AnlagenklasseSumme>();
        for (Nutzung currentNutzung : nutzungen) {
            if (!currentNutzung.isTerminated()) {
                NutzungsBuchung currentBuchung = currentNutzung.getCurrentBuchung();
                if (currentBuchung.getGueltigbis() == null) {
                    if (currentBuchung.getAnlageklasse() != null && currentBuchung.getGesamtpreis() != null) {
                        int index = 0;
                        Iterator<AnlagenklasseSumme> itAS = data.iterator();
                        //System.out.println(data.size());
                        //System.out.println("currentNutz:"+currentNutzung.getAnlageklasse().getSchluessel());
                        boolean isAlreadyInVector = false;
                        while (itAS.hasNext()) {
                            AnlagenklasseSumme curSumme = itAS.next();
                            if (curSumme.equals(currentBuchung.getAnlageklasse())) {
                                log.debug("Element der anlagensumme vorhanden");

                                //ToDo NKF muss behandelt werden und dem Benutzer mitgeteilt werden
                                try {
                                    Double stilleReserve = currentNutzung.getStilleReserve();
                                    if (stilleReserve == null) {
                                        stilleReserve = 0.0;
                                        curSumme.setSumme(curSumme.getSumme() + (currentBuchung.getGesamtpreis() - stilleReserve));
                                    }
                                } catch (IllegalNutzungStateException ex) {
                                    log.error("Stille Reserve konnte nicht berechnet werden");
                                }


                                isAlreadyInVector = true;
                            }
                            if (!isAlreadyInVector) {
                                log.debug("Element der anlagensumme hinzugefügt");
                                AnlagenklasseSumme tmp = new AnlagenklasseSumme(currentBuchung.getAnlageklasse());
                                try {
                                    Double stilleReserve = currentNutzung.getStilleReserve();
                                    if (stilleReserve == null) {
                                        stilleReserve = 0.0;
                                        tmp.setSumme((currentBuchung.getGesamtpreis()) - stilleReserve);
                                    }
                                } catch (IllegalNutzungStateException ex) {
                                    log.error("Stille Reserve konnte nicht berechnet werden");
                                }
                                data.add(tmp);
                            }
                        }

                    }
                }
            }
        }
        Collections.sort((ArrayList) data);
    }


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
    public String getColumnName(int column) {
        return COLUMN_HEADER[column];
    }

    public ArrayList<Nutzung> getAllNutzungen() {
        return nutzungen;
    }
//    @Override
//    public Class<?> getColumnClass(int columnIndex) {
//        return getValueAt(0,columnIndex).getClass();
//    }
}
