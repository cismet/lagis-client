/*
 * BeschluesseTableModel.java
 *
 * Created on 25. April 2007, 13:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import de.cismet.lagisEE.entity.core.Beschluss;
import de.cismet.lagisEE.entity.core.hardwired.Beschlussart;
import de.cismet.lagisEE.entity.core.hardwired.VerwaltendeDienststelle;
import de.cismet.lagisEE.entity.core.hardwired.Verwaltungsgebrauch;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author Puhl
 */
public class BeschluesseTableModel extends AbstractTableModel {

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Vector<Beschluss> beschluesse;
    private final static String[] COLUMN_HEADER = {"Beschlussart", "Datum"};
    private boolean isInEditMode = false;

    /** Creates a new instance of BeschluesseTableModel */
    public BeschluesseTableModel() {
        beschluesse = new Vector<Beschluss>();
    }

    public BeschluesseTableModel(Set<Beschluss> beschluesse) {
        try {
            this.beschluesse = new Vector<Beschluss>(beschluesse);
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            this.beschluesse = new Vector<Beschluss>();
        }
    }

//    public void setBeschluesseModelData(Set<Beschluss> beschluesse) {
//        try{
//            this.beschluesse = new Vector<Beschluss>(beschluesse);
//        }catch(Exception ex){
//            log.error("Fehler beim aktualisieren der Modelldaten",ex);
//            this.beschluesse = new Vector<Beschluss>();
//        }
//    }
    public void refreshTableModel(Set<Beschluss> beschluesse) {
        try {
            log.debug("Refresh des BeschlussTableModell");
            this.beschluesse = new Vector<Beschluss>(beschluesse);
        } catch (Exception ex) {
            log.error("Fehler beim refreshen des Models", ex);
            this.beschluesse = new Vector<Beschluss>();
        }
        fireTableDataChanged();
    }

    public void setIsInEditMode(boolean isEditable) {
        isInEditMode = isEditable;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            Beschluss beschluss = beschluesse.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    //Beschlussart art = beschluss.getBeschlussart();
                    //return art != null ? art.getBezeichnung() : null;
                    return beschluss.getBeschlussart();
                case 1:
                    //Date datum = beschluss.getDatum();
                    //return datum != null ? DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(datum) : null;
                    return beschluss.getDatum();
                default:
                    return "Spalte ist nicht definiert";
                }
        } catch (Exception ex) {
            log.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
            return null;
        }
    }

    public int getRowCount() {
        return beschluesse.size();
    }

    public int getColumnCount() {
        return COLUMN_HEADER.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_HEADER[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (COLUMN_HEADER.length > columnIndex) && (beschluesse.size() > rowIndex) && isInEditMode;
    }

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Beschlussart.class;
            case 1:
                return Date.class;
            default:
                log.warn("Die gewünschte Spalte exitiert nicht, es kann keine Klasse zurück geliefert werden");
                return null;
        }
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            Beschluss beschluss = beschluesse.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    //Beschlussart art = beschluss.getBeschlussart();
                    //return art != null ? art.getBezeichnung() : null;
                    beschluss.setBeschlussart((Beschlussart) aValue);
                    break;
                case 1:
                    //Date datum = beschluss.getDatum();
                    //return datum != null ? DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(datum) : null;
                    beschluss.setDatum((Date) aValue);
                    break;
                default:
                    log.warn("Keine Spalte für angegebenen Index vorhanden: "+columnIndex);
                    return;                    
            }
            fireTableDataChanged();
        } catch (Exception ex) {
            log.error("Fehler beim setzem der Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
        }
    }

    public Vector<Beschluss> getBeschluesse() {
        return beschluesse;
    }

    public void addBeschluss(Beschluss beschluss) {
        beschluesse.add(beschluss);
    }

    public Beschluss getBeschlussAtRow(int rowIndex) {
        return beschluesse.get(rowIndex);
    }

    public void removeBeschluss(int rowIndex) {
        beschluesse.remove(rowIndex);
    }
}
