/*
 * VerwaltungsbreicheTableModel.java
 *
 * Created on 23. April 2007, 09:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagisEE.entity.core.Geom;
import de.cismet.lagisEE.entity.core.Verwaltungsbereich;
import de.cismet.lagisEE.entity.core.hardwired.VerwaltendeDienststelle;
import de.cismet.lagisEE.entity.core.hardwired.Verwaltungsgebrauch;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author Puhl
 */
public class VerwaltungsTableModel extends AbstractTableModel {

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Vector<Verwaltungsbereich> verwaltungsbereiche;
    private Vector<Verwaltungsgebrauch> verwaltungsgebraeuche;
    private Vector<VerwaltendeDienststelle> verwaltendeDienstellen;
    private final static String[] COLUMN_HEADER = {"Dienststelle", "Gebrauch", "Fläche m²"};
    private boolean isInEditMode = false;
    private double currentWFSSize = 0;

    /**
     * Creates a new instance of VerwaltungsbreicheTableModel
     */
    public VerwaltungsTableModel(Set<Verwaltungsbereich> verwaltungsbereiche) {
        try {
            log.debug("Initialisierung des VerwaltungsbereichTableModell");
            this.verwaltungsbereiche = new Vector<Verwaltungsbereich>(verwaltungsbereiche);
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            this.verwaltungsbereiche = new Vector<Verwaltungsbereich>();
        }
    }

    public VerwaltungsTableModel() {
        verwaltungsbereiche = new Vector<Verwaltungsbereich>();
    }

//    public void setVerwaltungsGebrauchList(Set<Verwaltungsgebrauch> verwaltungsgebraeuche){
//        try{
//            log.debug("Initialisierung der VerwaltungsGebrauchList");
//            this.verwaltungsgebraeuche = new Vector<Verwaltungsgebrauch>(verwaltungsgebraeuche);
//        }catch(Exception ex){
//            log.error("Fehler beim anlegen der VerwaltungsGebrauchList",ex);
//            this.verwaltungsgebraeuche = new Vector<Verwaltungsgebrauch>();
//        }
//    }
//    public void setVerwaltendenDienstellenList(Set<VerwaltendeDienststelle> verwaltendeDienstellen){
//        try{
//            log.debug("Initialisierung der VerwaltendenDienstellenList");
//            this.verwaltendeDienstellen = new Vector<VerwaltendeDienststelle>(verwaltendeDienstellen);
//        }catch(Exception ex){
//            log.error("Fehler beim anlegen der VerwaltendenDienstellenList",ex);
//            this.verwaltendeDienstellen = new Vector<VerwaltendeDienststelle>();
//        }
//    }
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            log.debug("ausgewählte zeile/spalte" + rowIndex + "/" + columnIndex);
            Verwaltungsbereich vBereich = verwaltungsbereiche.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return vBereich.getDienststelle();

                case 1:
                    log.debug("aktueller Gebrauch: " + vBereich.getGebrauch());
                    return vBereich.getGebrauch();

                case 2:
                    //if there is only one Verwaltungsbereich & the WFS Geometry is used
                    if (verwaltungsbereiche.size() == 1) {
                        return (int) Math.round(currentWFSSize);
                    } else {
                        return vBereich.getFlaeche();
                    }
                default:
                    return "Spalte ist nicht definiert";
            }
        } catch (Exception ex) {
            log.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
            return null;
        }
    }

    public void refreshTableModel(Set<Verwaltungsbereich> verwaltungsbereiche) {
        try {
            log.debug("Refresh des VerwaltungsbereichTableModell");
            this.verwaltungsbereiche = new Vector<Verwaltungsbereich>(verwaltungsbereiche);
        //updateAreaInformation(null);            
        } catch (Exception ex) {
            log.error("Fehler beim refreshen des Models", ex);
            this.verwaltungsbereiche = new Vector<Verwaltungsbereich>();
        }
        fireTableDataChanged();
    }

    public int getRowCount() {
        return verwaltungsbereiche.size();
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
//        return getValueAt(0,columnIndex).getClass();
//    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (COLUMN_HEADER.length - 1 > columnIndex) && (verwaltungsbereiche.size() > rowIndex) && isInEditMode;
    }

    public void setIsInEditMode(boolean isEditable) {
        isInEditMode = isEditable;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            Verwaltungsbereich vBereich = verwaltungsbereiche.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    vBereich.setDienststelle((VerwaltendeDienststelle) aValue);
                    break;
                case 1:
                    log.debug("Setze Wert: " + aValue);
                    vBereich.setGebrauch((Verwaltungsgebrauch) aValue);
                    break;
//                case 2:
//                    vBereich.setFlaeche((Integer)aValue);
//                    break;
                default:
                    log.warn("Keine Spalte für angegebenen Index vorhanden: "+columnIndex);
                    return;
            }
            fireTableDataChanged();
        } catch (Exception ex) {
            log.error("Fehler beim setzen von Daten in dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
        }
//        Verwaltungsbereich vBereich = verwaltungsbereiche.get(rowIndex);
//        if(vBereich != null){
//            switch(columnIndex){
//                case 0:
//                    if(aValue instanceof VerwaltendeDienststelle ){
//                        log.debug("Dienstelle gesetzt");
//                    } else if(aValue instanceof String){
//                        log.debug("Versuche VerwaltendeDienstelle zu setzen");
//                        Iterator<VerwaltendeDienststelle> it = verwaltendeDienstellen.iterator();
//                        while(it.hasNext()){
//                            VerwaltendeDienststelle curVD = it.next();
//                            if(curVD.toString().equals(((String) aValue).trim())){
//                                vBereich.setDienststelle(curVD);
//                                log.debug("Übereinstimmung gefunden, neuer Wert: "+curVD);
//                            }
//                        }
//                    }
//                    break;
//                case 1:
//                    if(aValue instanceof Verwaltungsgebrauch){
//                        log.debug("Verwaltungsgebrauch gesetzt");
//                    } else if(aValue instanceof  String){
//                        log.debug("Versuche Verwaltungsgebrauch zu setzen");
//                        //TODO ugly
//                        Iterator<Verwaltungsgebrauch> it = verwaltungsgebraeuche.iterator();
//                        while(it.hasNext()){
//                            Verwaltungsgebrauch curVG = it.next();
//                            if(curVG.toString().equals(((String) aValue).trim())){
//                                vBereich.setGebrauch(curVG);
//                                log.debug("Übereinstimmung gefunden, neuer Wert: "+curVG);
//                            }
//                        }
//                    }
//                    break;
//                case 2:
//                    if(aValue instanceof Integer){
//                        log.debug("flaeche Gesetzt");
//                    } else if(aValue instanceof String){
//                        log.debug("Fläche wird Manuell gesetzt");
//                        try{
//                            vBereich.setFlaeche(Integer.parseInt((String)aValue));
//                        }catch(NumberFormatException ex){
//
//                        }
//                        break;
//                    }
//            }
//
//        }
    }

//    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//        JLabel label = new JLabel();
//        if(value instanceof VerwaltendeDienststelle){
//            VerwaltendeDienststelle dienststelle = (VerwaltendeDienststelle) value;
//            label.setText(dienststelle.toString());
//        } else if(value instanceof Verwaltungsgebrauch){
//            Verwaltungsgebrauch  nutzung = (Verwaltungsgebrauch) value;
//            label.setText(nutzung.toString());
//        } else{
//            log.debug("Object ist vom Typ: "+value.getClass());
//        }
//        return label;
//    }
    public Vector<Feature> getAllVerwaltungsFeatures() {
        Vector<Feature> tmp = new Vector<Feature>();
        if (verwaltungsbereiche != null) {
            Iterator<Verwaltungsbereich> it = verwaltungsbereiche.iterator();
            while (it.hasNext()) {
                Verwaltungsbereich curVB = it.next();
                if (curVB.getGeometry() != null) {
                    tmp.add(curVB);
                }
            }
            return tmp;
        } else {
            return tmp;
        }
    }

    public void addVerwaltungsbereich(final Verwaltungsbereich vBereich) {
        verwaltungsbereiche.add(vBereich);
        fireTableDataChanged();
    //updateAreaInformation(null);
    }

    public Verwaltungsbereich getVerwaltungsbereichAtRow(final int rowIndex) {
        if (rowIndex < verwaltungsbereiche.size()) {
            return verwaltungsbereiche.get(rowIndex);
        } else {
            return null;
        }
    }

    public void removeVerwaltungsbereich(final int rowIndex) {
        Verwaltungsbereich vBereich = verwaltungsbereiche.get(rowIndex);
        if (vBereich != null && vBereich.getGeometry() != null) {
            LagisBroker.getInstance().getMappingComponent().getFeatureCollection().removeFeature(vBereich);
        }
        verwaltungsbereiche.remove(rowIndex);
        fireTableDataChanged();
    //updateAreaInformation(null);        
    }

//    //TODO CHECK FOR BETTER Solution
//    public synchronized void updateAreaInformation(final Double singleSizeUpdate){
//        final int verwaltungsbereicheSize = verwaltungsbereiche.size();
//        log.debug("Flächeninformation wird geupdated");
//        log.debug("Anzahl Verwaltungsbereiche: "+verwaltungsbereicheSize);
//        try{
//            Iterator<Verwaltungsbereich> it = verwaltungsbereiche.iterator();
//            if(verwaltungsbereicheSize == 1 && singleSizeUpdate != null){
//                log.debug("Nur ein Verwaltungsbereich vorhanden");
//                Verwaltungsbereich currentVerwaltungsbereich = it.next();
//                if(currentVerwaltungsbereich != null && (currentVerwaltungsbereich.getFlaeche() ==null || !currentVerwaltungsbereich.getFlaeche().equals(singleSizeUpdate.intValue()))){
//                    log.debug("Fläche hat sich geändert");
//                    currentVerwaltungsbereich.setFlaeche(singleSizeUpdate.intValue());
//                    fireTableDataChanged();
//                    //TODO setSelection on new Entry
//                } else {
//                    log.debug("Fläche hat sich nicht geändert");
//                }
//            } else if(verwaltungsbereicheSize == 1){
//                log.debug("Nur ein Verwaltungsbereich vorhanden");
//                log.warn("Es war nicht möglich die Fläche zu updaten weil keine Größe mitgeliefert wurde");
//                Verwaltungsbereich currentVerwaltungsbereich = it.next();
//                if(currentVerwaltungsbereich != null && (currentVerwaltungsbereich.getFlaeche() ==null || !currentVerwaltungsbereich.getFlaeche().equals(0))){
//                    log.debug("Fläche hat sich geändert");
//                    currentVerwaltungsbereich.setFlaeche(0);
//                    fireTableDataChanged();
//                    //TODO setSelection on new Entry
//                } else {
//                    log.debug("Fläche hat sich nicht geändert");
//                }
//                return;
//            }else {
//                log.debug("mehrere Verwaltungsbereiche vorhanden");
//                while(it.hasNext()){
//                    Verwaltungsbereich curBereich = it.next();
//                    if(curBereich.getGeometry() != null){
//                        log.debug("Verwaltungsbereich: "+curBereich+" hat eine Fläche --> wird geupdated");                        
//                        final int area = (int)Math.round(curBereich.getGeometry().getArea());
//                        if(curBereich.getFlaeche() ==null || !curBereich.getFlaeche().equals(area)){
//                            log.debug("Fläche hat sich geändert");
//                            curBereich.setFlaeche(area);
//                            fireTableDataChanged();
//                            //TODO setSelection on new Entry
//                        } else {
//                            log.debug("Fläche hat sich nicht geändert");
//                        }
//                    } else if(curBereich.getGeometry() == null) {
//                        log.debug("Verwaltungsbereich: "+curBereich+" hat keine Fläche --> wird geupdated");                        
//                        if(curBereich.getFlaeche() ==null || !curBereich.getFlaeche().equals(0)){
//                            log.debug("Fläche hat sich geändert");
//                            curBereich.setFlaeche(0);
//                            fireTableDataChanged();
//                            //TODO setSelection on new Entry
//                        } else {
//                            log.debug("Fläche hat sich nicht geändert");
//                        }
//                    } else {
//                        log.warn("Keiner der Fälle trifft zu");
//                    }
//                }
//            }
//        }catch(Exception ex){
//            log.error("Fehler beim updaten der Flächeninformation",ex);
//        }
//    }
    public Vector<Verwaltungsbereich> getVerwaltungsbereiche() {
        return verwaltungsbereiche;
    }

//    public void selectVerwaltungsbereich(Verwaltungsbereich vBereich){
//
//    }
    public int getIndexOfVerwaltungsbereich(Verwaltungsbereich vBereich) {
        return verwaltungsbereiche.indexOf(vBereich);
    }

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return VerwaltendeDienststelle.class;
            case 1:
                return Verwaltungsgebrauch.class;
            case 2:
                return Integer.class;
            default:
                log.warn("Die gewünschte Spalte exitiert nicht, es kann keine Klasse zurück geliefert werden");
                return null;
        }
    }

    public void setCurrentWFSSize(double currentWFSSize) {
        this.currentWFSSize = currentWFSSize;
    }
}
