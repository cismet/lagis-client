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
import de.cismet.lagisEE.entity.core.Nutzung;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author Puhl
 */
public class NKFOverviewTableModel extends AbstractTableModel {

    private Vector<Nutzung> nutzungen = new Vector<Nutzung>();
    private static final String[] COLUMN_HEADER = {"Anlageklasse", "Summe"};
    private Vector<AnlagenklasseSumme> data = new Vector<AnlagenklasseSumme>();
    private DecimalFormat df = LagisBroker.getCurrencyFormatter();
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    /** Creates a new instance of NKFOverviewTableModel */
    public NKFOverviewTableModel() {
        nutzungen = new Vector<Nutzung>();
    }

    public NKFOverviewTableModel(Set<Nutzung> nutzungen) {
        try {
            log.debug("Konstruktor Nutzungen");
            this.nutzungen = new Vector<Nutzung>(nutzungen);
            calculateSum();
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            this.nutzungen = new Vector<Nutzung>();
        }
    }

    public synchronized void refreshModel(Set<Nutzung> nutzungen) {
        try {
            log.debug("Refresh Nutzungen");
            this.nutzungen = new Vector<Nutzung>(nutzungen);
            calculateSum();
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            this.nutzungen = new Vector<Nutzung>();
        }
        fireTableDataChanged();
    }

    public synchronized void refreshModel(Vector<Nutzung> nutzungen) {
        try {
            log.debug("Refresh Nutzungen");
            this.nutzungen = new Vector<Nutzung>(nutzungen);
            calculateSum();
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            this.nutzungen = new Vector<Nutzung>();
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
        data = new Vector<AnlagenklasseSumme>();
        Iterator<Nutzung> it = nutzungen.iterator();
        while (it.hasNext()) {
            Nutzung currentNutzung = it.next();
            if (hasNutzungSuccessor(currentNutzung)) {
                log.debug("Nutzung hat einen Nachfolger und wird für NKF nicht berücksichtigt");
                continue;
            } else {
                log.debug("Nutzung hat keinen Nachfolger --> wird für NKF berücksichtigt");
            }
            if (currentNutzung.getAnlageklasse() != null && currentNutzung.getQuadratmeterpreis() != null && currentNutzung.getFlaeche() != null) {
                int index = 0;
                Iterator<AnlagenklasseSumme> itAS = data.iterator();
                //System.out.println(data.size());
                //System.out.println("currentNutz:"+currentNutzung.getAnlageklasse().getSchluessel());
                boolean isAlreadyInVector = false;
                while (itAS.hasNext()) {
                    AnlagenklasseSumme curSumme = itAS.next();
                    if (curSumme.equals(currentNutzung.getAnlageklasse())) {
                        log.debug("Element der anlagensumme vorhanden");
                        if (currentNutzung.getStilleReserve() != null) {
                            curSumme.setSumme(curSumme.getSumme() + ((currentNutzung.getQuadratmeterpreis() * currentNutzung.getFlaeche()) - currentNutzung.getStilleReserve()));
                        } else {
                            curSumme.setSumme(curSumme.getSumme() + (currentNutzung.getQuadratmeterpreis() * currentNutzung.getFlaeche()));
                        }
                        isAlreadyInVector = true;
                    }
                }
                if (!isAlreadyInVector) {
                    log.debug("Element der anlagensumme hinzugefügt");
                    AnlagenklasseSumme tmp = new AnlagenklasseSumme(currentNutzung.getAnlageklasse());
                    if (currentNutzung.getStilleReserve() != null) {
                        tmp.setSumme((currentNutzung.getQuadratmeterpreis() * currentNutzung.getFlaeche()) - currentNutzung.getStilleReserve());
                    } else {
                        tmp.setSumme(currentNutzung.getQuadratmeterpreis() * currentNutzung.getFlaeche());
                    }
                    data.add(tmp);
                }
            }
        }
        Collections.sort((Vector) data);
    }

    public boolean hasNutzungSuccessor(Nutzung nutzung) {
        log.debug("hat Nutzung: " + nutzung.getId() + " einen Nachfolger ? (anzahl Nutzungen: "+nutzungen.size()+")");
        if (nutzung != null) {
            Iterator<Nutzung> it = nutzungen.iterator();
            while (it.hasNext()) {
                Nutzung currentNutzung = it.next();
                log.debug("Aktuelle Nutzung gueltigbis: "+ currentNutzung.getGueltigbis());
                log.debug("Aktuelle Nutzung vorgänger: "+ currentNutzung.getVorgaenger());
                log.debug("zu prüfende Nutzung id: "+nutzung.getId());
                if (nutzung.getGueltigbis() != null && currentNutzung.getVorgaenger() != null && nutzung.getId() != null && currentNutzung.getVorgaenger().equals(nutzung.getId())) {
                    log.debug("Nutzung hat einen Nachfolger");
                    return true;
                }
            }
            log.debug("Nutzung hat keinen Nachfolger");
            return false;
        } else {
            log.debug("Nutzung hat keinen Nachfolger");
            return false;
        }
    }

    public boolean containsHistoricNutzung() {
        Iterator<Nutzung> it = nutzungen.iterator();
        while (it.hasNext()) {
            Nutzung currentNutzung = it.next();
            if (currentNutzung.getGueltigbis() != null) {
                System.out.println("Nutzung hat einen Nachfolger");
                return true;
            }
        }
        return false;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_HEADER[column];
    }

    public Vector<Nutzung> getAllNutzungen() {
        return nutzungen;
    }
//    @Override
//    public Class<?> getColumnClass(int columnIndex) {
//        return getValueAt(0,columnIndex).getClass();
//    }
}
