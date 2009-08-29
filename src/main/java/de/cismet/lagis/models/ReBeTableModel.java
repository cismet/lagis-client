/*
 * ReBeTableModel.java
 *
 * Created on 25. April 2007, 09:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.models;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagisEE.entity.core.Geom;
import de.cismet.lagisEE.entity.core.ReBe;
import de.cismet.lagisEE.entity.core.hardwired.ReBeArt;

import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author Puhl
 */
public class ReBeTableModel extends AbstractTableModel {
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    Vector<ReBe> resBes;
    Vector<ReBeArt> reBeArten;
    private final static String[] COLUMN_HEADER = {"ist Recht","Art","Art des Rechts","Nummer","Eintragung Datum","Löschung Datum","Bemerkung"};
    private boolean isInEditMode = false;
    private boolean isReBeKindSwitchAllowed=true;
    /** Creates a new instance of ReBeTableModel */
    public ReBeTableModel() {
        resBes = new Vector<ReBe>();
    }
    
    public ReBeTableModel(Set<ReBe> reBe) {
        try {
            this.resBes = new Vector<ReBe>(reBe);
        } catch(Exception ex){
            log.error("Fehler beim anlegen des Models",ex);
            this.resBes = new Vector<ReBe>();
        }
    }
    
    public void setReBeArtenList(Set<ReBeArt> reBeArten){
        try {
            log.error("Versuche RebenArtenListe zu setzen");
            this.reBeArten = new Vector<ReBeArt>(reBeArten);
        } catch(Exception ex){
            log.error("Fehler beim anlegen des RebeArtenList",ex);
            this.reBeArten = new Vector<ReBeArt>();
        }
    }
    
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        try{
            ReBe value = resBes.get(rowIndex);
            switch(columnIndex){
                case 0:
                    return value.getIstRecht();
                case 1:
                    return value.getReBeArt();
                case 2:
                    return value.getBeschreibung();
                case 3:
                    return value.getNummer();
                case 4:
                    return value.getDatumEintragung();
                case 5:
                    return value.getDatumLoeschung();
                case 6:
                    return value.getBemerkung();
                default:
                    return "Spalte ist nicht definiert";
            }
        }catch(Exception ex){
            log.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: "+rowIndex+" Spalte"+columnIndex ,ex);
            return null;
        }
        //   }
//        try{
//            ReBe value = resBes.get(rowIndex);
//            switch(columnIndex){
//                case 0:
//                    return value.getIstRecht() == true ? "Recht" : "Belastung";
//                case 1:
//                    ReBeArt art = value.getArt();
//                    if(art != null){
//                        return art.getBezeichnung();
//                    } else {
//                        return null;
//                    }
//                case 2:
//                    return value.getBeschreibung();
//                case 3:
//                    return value.getNummer();
//                case 4:
//                    Date eintragung = value.getDatumEintragung();
//                    if(eintragung != null){
//                        return DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(eintragung);
//                    } else {
//                        return null;
//                    }
//                case 5:
//                    Date loeschung = value.getDatumLoeschung();
//                    if(loeschung != null){
//                        return DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(loeschung);
//                    } else {
//                        return null;
//                    }
//                default:
//                    return "Spalte ist nicht definiert";
//            }
//        }catch(Exception ex){
//            log.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: "+rowIndex+" Spalte"+columnIndex ,ex);
//            return null;
//        }
    }
    
    
    public void addReBe(ReBe reBe){
        resBes.add(reBe);
    }
    
    public ReBe getReBeAtRow(int rowIndex){
        return resBes.get(rowIndex);
    }
    
    public void removeReBe(int rowIndex){
        ReBe reBe = resBes.get(rowIndex);
        if(reBe != null && reBe.getGeometry() != null){
            LagisBroker.getInstance().getMappingComponent().getFeatureCollection().removeFeature(reBe);
        }
        resBes.remove(rowIndex);
    }
    
    public int getRowCount() {
        return resBes.size();
    }
    
    public int getColumnCount() {
        return COLUMN_HEADER.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return COLUMN_HEADER[column];
    }
    
//    @Override
//    public Class<?> getColumnClass(int columnIndex) {
//        log.fatal("TMP FATAL: getColumn Class: "+ columnIndex);
//        return getValueAt(0,columnIndex).getClass();
//    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnIndex ==0){
            return (COLUMN_HEADER.length > columnIndex)&&(resBes.size() >rowIndex) && isInEditMode && isReBeKindSwitchAllowed;
        } else {
            return (COLUMN_HEADER.length > columnIndex)&&(resBes.size() >rowIndex) && isInEditMode;
        }
        
    }
    
    public void setIsInEditMode(boolean isEditable){
        isInEditMode=isEditable;
    }
    
    public Vector<Feature> getAllReBeFeatures(){
        Vector<Feature> tmp = new Vector<Feature>();
        if(resBes != null){
            Iterator<ReBe> it = resBes.iterator();
            while(it.hasNext()){
                ReBe curReBe = it.next();
                if(curReBe.getGeometry() != null){
                    tmp.add(curReBe);
                }
            }
            return tmp;
        } else {
            return null;
        }
    }
    
    public void refreshTableModel(Set<ReBe> resBes){
        try{
            log.debug("Refresh des RebeTableModell");
            this.resBes = new Vector<ReBe>(resBes);
        }catch(Exception ex){
            log.error("Fehler beim refreshen des Models",ex);
            this.resBes = new Vector<ReBe>();
        }
        fireTableDataChanged();
    }
    
    public Vector<ReBe> getResBes(){
        return resBes;
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try{
            ReBe value = resBes.get(rowIndex);
            switch(columnIndex){
                case 0:
                    value.setIstRecht((Boolean)aValue);
                    break;
                case 1:
                    value.setReBeArt((ReBeArt)aValue);
                    break;
                case 2:
                    value.setBeschreibung((String)aValue);
                    break;
                case 3:
                    value.setNummer((String)aValue);
                    break;
                case 4:
                    if(aValue instanceof Date || aValue == null){
                        value.setDatumEintragung((Date)aValue);
                    }
                    break;
                case 5:
                    if(aValue instanceof Date || aValue == null){
                        value.setDatumLoeschung((Date)aValue);
                    }
                    break;
                case 6:
                    value.setBemerkung((String)aValue);
                    break;
           default:
                    log.warn("Keine Spalte für angegebenen Index vorhanden: "+columnIndex);
                    return;
            }
            fireTableDataChanged();
        }catch(Exception ex){
            log.error("Fehler beim setzen von Daten in dem Modell: Zeile: "+rowIndex+" Spalte"+columnIndex ,ex);
            
        }
//        ReBe reBe = resBes.get(rowIndex);
//        if(reBe != null){
//            switch(columnIndex){
//                case 0:
//                    if(aValue instanceof String){
//                        if(((String)(aValue)).equals("Recht")) reBe.setIstRecht(true);
//                        if(((String)(aValue)).equals("Belastung")) reBe.setIstRecht(false);
//                    }
//                    break;
//                case 1:
//                    if(aValue instanceof  String){
//                        //((String)(aValue)).equals("Recht")
//                        Iterator<ReBeArt> it = reBeArten.iterator();
//                        while(it.hasNext()){
//                            ReBeArt curRBA = it.next();
//                            if(curRBA.toString().equals(((String) aValue).trim())){
//                                reBe.setArt(curRBA);
//                                log.debug("Übereinstimmung gefunden, neuer Wert: "+curRBA);
//                            }
//                        }
//                    }
//                    break;
//                case 2:
//                    if(aValue instanceof  String){
//                        reBe.setBeschreibung((String)aValue);
//                    }
//                    break;
//                case 3:
//                    if(aValue instanceof  String){
//                        reBe.setNummer((String)(aValue));
//                    }
//                    break;
//                case 4:
//                    if(aValue instanceof  String){
//                        try{
//                            reBe.setDatumEintragung(LagisBroker.getDateFormatter().parse((String)(aValue)));
//                        }catch(Exception ex){
//
//                        }
//                    }
//                    break;
//                case 5:
//                    if(aValue instanceof  String){
//                        try{
//                            reBe.setDatumLoeschung(LagisBroker.getDateFormatter().parse((String)(aValue)));
//                        }catch(Exception ex){
//
//                        }
//                    }
//                    break;
//            }
//        }
//    }
    }
    
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex){
            case 0:
                return Boolean.class;
            case 1:
                return ReBeArt.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
            case 4:
                return Date.class;
            case 5:
                return Date.class;
            case 6:
                return String.class;
            default:
                log.warn("Die gewünschte Spalte exitiert nicht, es kann keine Klasse zurück geliefert werden");
                return null;
        }
    }
    
    public int getIndexOfReBe(ReBe rebe){
        return resBes.indexOf(rebe);
    }
    
    public void setIsReBeKindSwitchAllowed(boolean isReBeKindSwitchAllowed) {
        this.isReBeKindSwitchAllowed = isReBeKindSwitchAllowed;
    }
}
