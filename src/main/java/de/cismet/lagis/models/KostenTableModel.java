/*
 * KostenTableModel.java
 *
 * Created on 25. April 2007, 13:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.models;

import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagisEE.entity.core.Kosten;
import de.cismet.lagisEE.entity.core.hardwired.Kostenart;
import java.text.DateFormat;
import java.text.DecimalFormat;
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
public class KostenTableModel extends AbstractTableModel {
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Vector<Kosten> kosten;
    private final static String[] COLUMN_HEADER = {"Kostenart","Betrag","Anweisung"};
    private DecimalFormat df = LagisBroker.getCurrencyFormatter();
    private boolean isInEditMode=false;
    
    /** Creates a new instance of KostenTableModel */
    public KostenTableModel() {
        kosten = new Vector<Kosten>();
    }
    
    public KostenTableModel(Set<Kosten> kosten) {
        try{
            this.kosten = new Vector<Kosten>(kosten);
        }catch(Exception ex){
            log.error("Fehler beim anlegen des Models",ex);
            this.kosten = new Vector<Kosten>();
        }
    }
    
//    public void setKostenModelData(Set<Kosten> kosten){
//        try{
//            this.kosten = new Vector<Kosten>(kosten);
//        }catch(Exception ex){
//            log.error("Fehler beim aktualisieren der Modelldaten",ex);
//            this.kosten = new Vector<Kosten>();
//        }
//    }
    
    public void refreshTableModel(Set<Kosten>  kosten){
        try{
            log.debug("Refresh des KostenTableModell");
            this.kosten = new Vector<Kosten>(kosten);
        }catch(Exception ex){
            log.error("Fehler beim refreshen des Models",ex);
            this.kosten = new Vector<Kosten>();
        }
        fireTableDataChanged();
    }        
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        try{
            Kosten value = kosten.get(rowIndex);
            switch(columnIndex) {
                case 0:                    
                    return value.getKostenart();
                case 1:                    
                    return value.getBetrag();
                case 2:                    
                    return value.getDatum();
                default:
                    return "Spalte ist nicht definiert";
            }
        }catch(Exception ex){
            log.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: "+rowIndex+" Spalte"+columnIndex ,ex);
            return null;
        }
    }
    
    public int getRowCount() {
        return kosten.size();
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
        return (COLUMN_HEADER.length > columnIndex)&&(kosten.size() >rowIndex) && isInEditMode;
    }
    public void setIsInEditMode(boolean isEditable){
        isInEditMode=isEditable;
    }
    
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex){
            case 0:
                return Kostenart.class;
            case 1:
                return Double.class;           
            case 2:
                return Date.class;           
            default:
                log.warn("Die gewünschte Spalte exitiert nicht, es kann keine Klasse zurück geliefert werden");
                return null;
        }
    }
    
     public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
         try{
            Kosten value = kosten.get(rowIndex);
                switch(columnIndex) {
                    case 0:                        
                        value.setKostenart((Kostenart)aValue);
                        break;
                    case 1:
                        value.setBetrag((Double)aValue);
                    case 2:
                        //Date datum = beschluss.getDatum();
                        //return datum != null ? DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(datum) : null;
                        value.setDatum((Date)aValue);                    
                default:
                    log.warn("Keine Spalte für angegebenen Index vorhanden: "+columnIndex);
                    return;
            }
            fireTableDataChanged();
        }catch(Exception ex){
            log.error("Fehler beim setzem der Daten aus dem Modell: Zeile: "+rowIndex+" Spalte"+columnIndex ,ex);            
        }
    }
     
      public Vector<Kosten> getKosten(){
        return kosten;
    }
     
      public void addKosten(Kosten beschluss){
        kosten.add(beschluss);
    }
    
    public Kosten getKostenAtRow(int rowIndex){
        return kosten.get(rowIndex);
    }
    
    public void removeKosten(int rowIndex){             
        kosten.remove(rowIndex);
    }
}
