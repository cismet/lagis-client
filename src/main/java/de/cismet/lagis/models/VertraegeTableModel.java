/*
 * VertraegeTableModel.java
 *
 * Created on 25. April 2007, 13:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.models;

import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagisEE.entity.core.Vertrag;
import de.cismet.lagisEE.entity.core.hardwired.Vertragsart;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author Puhl
 */
public class VertraegeTableModel extends AbstractTableModel {
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Vector<Vertrag> vertraege;
    private final static String[] COLUMN_HEADER = {"Vertragsart","Aktenzeichen","Quadratmeterpreis","Kaufpreis (i. NK)"};
    private DecimalFormat df = LagisBroker.getCurrencyFormatter();    
    //Models
    
    /** Creates a new instance of VertraegeTableModel */
    public VertraegeTableModel() {
        vertraege = new Vector<Vertrag>();        
    }
    
    
    public VertraegeTableModel(Set<Vertrag> vertraege ) {
        try{
            this.vertraege = new Vector<Vertrag>(vertraege);
            //log.fatal("Voreigentuemer: "+this.vertraege.get(0).getVoreigentuemer());
        }catch(Exception ex){
            log.error("Fehler beim anlegen des Models",ex);
            this.vertraege = new Vector<Vertrag>();    
            HashSet test = new HashSet();                        
        }
    }
    
    
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        try{
        Vertrag vertrag = vertraege.get(rowIndex);        
            switch(columnIndex) {
                case 0:
                    Vertragsart art = vertrag.getVertragsart();
                    if(art != null){
                    return art.getBezeichnung();
                    } else {
                    return null;    
                    }                    
                case 1:
                    return vertrag.getAktenzeichen();
                case 2:
                    Double qPreis = vertrag.getQuadratmeterpreis();                    
                    return  qPreis != null ? df.format(qPreis) : null;
                case 3:
                    Double gPreis = vertrag.getGesamtpreis();
                    return gPreis != null ? df.format(gPreis) : null;                                                     
                default:
                    return "Spalte ist nicht definiert";
            }  
        }catch(Exception ex){
            log.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: "+rowIndex+" Spalte"+columnIndex ,ex);
            return null;
        }
    }
    
    public int getRowCount() {
        return vertraege.size();
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
        return false;
    }
    
    public Vertrag getVertragAtRow(int rowIndex){
        return vertraege.get(rowIndex);
    }
    
    public void addVertrag(Vertrag vertrag){
        vertraege.add(vertrag);
        fireTableDataChanged();
    }
            
    public void removeVertrag(int rowIndex){                
        vertraege.remove(rowIndex);
        fireTableDataChanged();
    }
    
    public Vector<Vertrag> getVertraege(){
        return vertraege;
    }
    
    public void refreshTableModel(Set<Vertrag> vertraege){
        try{
            log.debug("Refresh des VertraegeTableModell");
            this.vertraege = new Vector<Vertrag>(vertraege);
        }catch(Exception ex){
            log.error("Fehler beim refreshen des Models",ex);
            this.vertraege = new Vector<Vertrag>();
        }
        fireTableDataChanged();
    }        
}
