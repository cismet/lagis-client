/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * VerwaltungsbreicheTableModel.java
 *
 * Created on 23. April 2007, 09:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import de.cismet.cids.custom.beans.verdis_grundis.*;

import de.cismet.cismap.commons.features.Feature;

import de.cismet.lagis.broker.LagisBroker;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class VerwaltungsTableModel extends AbstractTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_HEADER = { "Dienststelle", "Gebrauch", "Fläche m²" };

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Vector<VerwaltungsbereichCustomBean> verwaltungsbereiche;
    private Vector<VerwaltungsgebrauchCustomBean> verwaltungsgebraeuche;
    private Vector<VerwaltendeDienststelleCustomBean> verwaltendeDienstellen;
    private boolean isInEditMode = false;
    private double currentWFSSize = 0;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VerwaltungsTableModel object.
     */
    public VerwaltungsTableModel() {
        verwaltungsbereiche = new Vector<VerwaltungsbereichCustomBean>();
    }

    /**
     * Creates a new instance of VerwaltungsbreicheTableModel.
     *
     * @param  verwaltungsbereiche  DOCUMENT ME!
     */
    public VerwaltungsTableModel(final Set<VerwaltungsbereichCustomBean> verwaltungsbereiche) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Initialisierung des VerwaltungsbereichTableModell");
            }
            this.verwaltungsbereiche = new Vector<VerwaltungsbereichCustomBean>(verwaltungsbereiche);
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            this.verwaltungsbereiche = new Vector<VerwaltungsbereichCustomBean>();
        }
    }

    //~ Methods ----------------------------------------------------------------

// public void setVerwaltungsGebrauchList(Set<Verwaltungsgebrauch> verwaltungsgebraeuche){
// try{
// log.debug("Initialisierung der VerwaltungsGebrauchList");
// this.verwaltungsgebraeuche = new Vector<Verwaltungsgebrauch>(verwaltungsgebraeuche);
// }catch(Exception ex){
// log.error("Fehler beim anlegen der VerwaltungsGebrauchList",ex);
// this.verwaltungsgebraeuche = new Vector<Verwaltungsgebrauch>();
// }
// }
// public void setVerwaltendenDienstellenList(Set<VerwaltendeDienststelle> verwaltendeDienstellen){
// try{
// log.debug("Initialisierung der VerwaltendenDienstellenList");
// this.verwaltendeDienstellen = new Vector<VerwaltendeDienststelle>(verwaltendeDienstellen);
// }catch(Exception ex){
// log.error("Fehler beim anlegen der VerwaltendenDienstellenList",ex);
// this.verwaltendeDienstellen = new Vector<VerwaltendeDienststelle>();
// }
// }
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("ausgewählte zeile/spalte" + rowIndex + "/" + columnIndex);
            }
            final VerwaltungsbereichCustomBean vBereich = verwaltungsbereiche.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return vBereich.getDienststelle();
                }

                case 1: {
                    if (log.isDebugEnabled()) {
                        log.debug("aktueller Gebrauch: " + vBereich.getGebrauch());
                    }
                    return vBereich.getGebrauch();
                }

                case 2: {
                    // if there is only one VerwaltungsbereichCustomBean & the WFS Geometry is used
                    if (verwaltungsbereiche.size() == 1) {
                        return (int)Math.round(currentWFSSize);
                    } else {
                        return vBereich.getFlaeche();
                    }
                }
                default: {
                    return "Spalte ist nicht definiert";
                }
            }
        } catch (Exception ex) {
            log.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  verwaltungsbereiche  DOCUMENT ME!
     */
    public void refreshTableModel(final Collection<VerwaltungsbereichCustomBean> verwaltungsbereiche) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Refresh des VerwaltungsbereichTableModell");
            }
            this.verwaltungsbereiche = new Vector<VerwaltungsbereichCustomBean>(verwaltungsbereiche);
            // updateAreaInformation(null);
        } catch (Exception ex) {
            log.error("Fehler beim refreshen des Models", ex);
            this.verwaltungsbereiche = new Vector<VerwaltungsbereichCustomBean>();
        }
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return verwaltungsbereiche.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_HEADER.length;
    }

    @Override
    public String getColumnName(final int column) {
        return COLUMN_HEADER[column];
    }

//    @Override
//    public Class<?> getColumnClass(int columnIndex) {
//        return getValueAt(0,columnIndex).getClass();
//    }
    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return ((COLUMN_HEADER.length - 1) > columnIndex) && (verwaltungsbereiche.size() > rowIndex) && isInEditMode;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isEditable  DOCUMENT ME!
     */
    public void setIsInEditMode(final boolean isEditable) {
        isInEditMode = isEditable;
    }

    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        try {
            final VerwaltungsbereichCustomBean vBereich = verwaltungsbereiche.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    vBereich.setDienststelle((VerwaltendeDienststelleCustomBean)aValue);
                    break;
                }
                case 1: {
                    if (log.isDebugEnabled()) {
                        log.debug("Setze Wert: " + aValue);
                    }
                    vBereich.setGebrauch((VerwaltungsgebrauchCustomBean)aValue);
                    break;
                }
//                case 2:
//                    vBereich.setFlaeche((Integer)aValue);
//                    break;
                default: {
                    log.warn("Keine Spalte für angegebenen Index vorhanden: " + columnIndex);
                    return;
                }
            }
            fireTableDataChanged();
        } catch (Exception ex) {
            log.error("Fehler beim setzen von Daten in dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
        }
//        VerwaltungsbereichCustomBean vBereich = verwaltungsbereiche.get(rowIndex);
//        if(vBereich != null){
//            switch(columnIndex){
//                case 0:
//                    if(aValue instanceof VerwaltendeDienststelleCustomBean ){
//                        log.debug("Dienstelle gesetzt");
//                    } else if(aValue instanceof String){
//                        log.debug("Versuche VerwaltendeDienstelle zu setzen");
//                        Iterator<VerwaltendeDienststelle> it = verwaltendeDienstellen.iterator();
//                        while(it.hasNext()){
//                            VerwaltendeDienststelleCustomBean curVD = it.next();
//                            if(curVD.toString().equals(((String) aValue).trim())){
//                                vBereich.setDienststelle(curVD);
//                                log.debug("Übereinstimmung gefunden, neuer Wert: "+curVD);
//                            }
//                        }
//                    }
//                    break;
//                case 1:
//                    if(aValue instanceof VerwaltungsgebrauchCustomBean){
//                        log.debug("VerwaltungsgebrauchCustomBean gesetzt");
//                    } else if(aValue instanceof  String){
//                        log.debug("Versuche VerwaltungsgebrauchCustomBean zu setzen");
//                        //TODO ugly
//                        Iterator<Verwaltungsgebrauch> it = verwaltungsgebraeuche.iterator();
//                        while(it.hasNext()){
//                            VerwaltungsgebrauchCustomBean curVG = it.next();
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
    /**
     * public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
     * int row, int column) { JLabel label = new JLabel(); if(value instanceof VerwaltendeDienststelleCustomBean){
     * VerwaltendeDienststelleCustomBean dienststelle = (VerwaltendeDienststelleCustomBean) value;
     * label.setText(dienststelle.toString()); } else if(value instanceof VerwaltungsgebrauchCustomBean){
     * VerwaltungsgebrauchCustomBean nutzung = (VerwaltungsgebrauchCustomBean) value; label.setText(nutzung.toString());
     * } else{ log.debug("Object ist vom Typ: "+value.getClass()); } return label; }
     *
     * @return  DOCUMENT ME!
     */
    public Vector<Feature> getAllVerwaltungsFeatures() {
        final Vector<Feature> tmp = new Vector<Feature>();
        if (verwaltungsbereiche != null) {
            final Iterator<VerwaltungsbereichCustomBean> it = verwaltungsbereiche.iterator();
            while (it.hasNext()) {
                final VerwaltungsbereichCustomBean curVB = it.next();
                if (curVB.getGeometry() != null) {
                    tmp.add(curVB);
                }
            }
            return tmp;
        } else {
            return tmp;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  vBereich  DOCUMENT ME!
     */
    public void addVerwaltungsbereich(final VerwaltungsbereichCustomBean vBereich) {
        verwaltungsbereiche.add(vBereich);
        fireTableDataChanged();
        // updateAreaInformation(null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public VerwaltungsbereichCustomBean getVerwaltungsbereichAtRow(final int rowIndex) {
        if (rowIndex < verwaltungsbereiche.size()) {
            return verwaltungsbereiche.get(rowIndex);
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rowIndex  DOCUMENT ME!
     */
    public void removeVerwaltungsbereich(final int rowIndex) {
        final VerwaltungsbereichCustomBean vBereich = verwaltungsbereiche.get(rowIndex);
        if ((vBereich != null) && (vBereich.getGeometry() != null)) {
            LagisBroker.getInstance().getMappingComponent().getFeatureCollection().removeFeature(vBereich);
        }
        verwaltungsbereiche.remove(rowIndex);
        fireTableDataChanged();
        // updateAreaInformation(null);
    }
    /**
     * //TODO CHECK FOR BETTER Solution public synchronized void updateAreaInformation(final Double singleSizeUpdate){
     * final int verwaltungsbereicheSize = verwaltungsbereiche.size(); log.debug("Flächeninformation wird geupdated");
     * log.debug("Anzahl Verwaltungsbereiche: "+verwaltungsbereicheSize); try{ Iterator<Verwaltungsbereich> it =
     * verwaltungsbereiche.iterator(); if(verwaltungsbereicheSize == 1 && singleSizeUpdate != null){ log.debug("Nur ein
     * VerwaltungsbereichCustomBean vorhanden"); VerwaltungsbereichCustomBean currentVerwaltungsbereich = it.next();
     * if(currentVerwaltungsbereich != null && (currentVerwaltungsbereich.getFlaeche() ==null ||
     * !currentVerwaltungsbereich.getFlaeche().equals(singleSizeUpdate.intValue()))){ log.debug("Fläche hat sich
     * geändert"); currentVerwaltungsbereich.setFlaeche(singleSizeUpdate.intValue()); fireTableDataChanged(); //TODO
     * setSelection on new Entry } else { log.debug("Fläche hat sich nicht geändert"); } } else
     * if(verwaltungsbereicheSize == 1){ log.debug("Nur ein VerwaltungsbereichCustomBean vorhanden"); log.warn("Es war
     * nicht möglich die Fläche zu updaten weil keine Größe mitgeliefert wurde"); VerwaltungsbereichCustomBean
     * currentVerwaltungsbereich = it.next(); if(currentVerwaltungsbereich != null &&
     * (currentVerwaltungsbereich.getFlaeche() ==null || !currentVerwaltungsbereich.getFlaeche().equals(0))){
     * log.debug("Fläche hat sich geändert"); currentVerwaltungsbereich.setFlaeche(0); fireTableDataChanged(); //TODO
     * setSelection on new Entry } else { log.debug("Fläche hat sich nicht geändert"); } return; }else {
     * log.debug("mehrere Verwaltungsbereiche vorhanden"); while(it.hasNext()){ VerwaltungsbereichCustomBean curBereich
     * = it.next(); if(curBereich.getGeometry() != null){ log.debug("VerwaltungsbereichCustomBean: "+curBereich+" hat
     * eine Fläche --> wird geupdated"); final int area = (int)Math.round(curBereich.getGeometry().getArea());
     * if(curBereich.getFlaeche() ==null || !curBereich.getFlaeche().equals(area)){ log.debug("Fläche hat sich
     * geändert"); curBereich.setFlaeche(area); fireTableDataChanged(); //TODO setSelection on new Entry } else {
     * log.debug("Fläche hat sich nicht geändert"); }} else if(curBereich.getGeometry() == null) {
     * log.debug("VerwaltungsbereichCustomBean: "+curBereich+" hat keine Fläche --> wird geupdated");
     * if(curBereich.getFlaeche() ==null || !curBereich.getFlaeche().equals(0)){ log.debug("Fläche hat sich geändert");
     * curBereich.setFlaeche(0); fireTableDataChanged(); //TODO setSelection on new Entry } else { log.debug("Fläche hat
     * sich nicht geändert"); } } else { log.warn("Keiner der Fälle trifft zu"); } } } }catch(Exception ex){
     * log.error("Fehler beim updaten der Flächeninformation",ex); } }
     *
     * @return  DOCUMENT ME!
     */
    public Vector<VerwaltungsbereichCustomBean> getVerwaltungsbereiche() {
        return verwaltungsbereiche;
    }
    /**
     * public void selectVerwaltungsbereich(VerwaltungsbereichCustomBean vBereich){ }.
     *
     * @param   vBereich  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getIndexOfVerwaltungsbereich(final VerwaltungsbereichCustomBean vBereich) {
        return verwaltungsbereiche.indexOf(vBereich);
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        switch (columnIndex) {
            case 0: {
                return VerwaltendeDienststelleCustomBean.class;
            }
            case 1: {
                return VerwaltungsgebrauchCustomBean.class;
            }
            case 2: {
                return Integer.class;
            }
            default: {
                log.warn("Die gewünschte Spalte exitiert nicht, es kann keine Klasse zurück geliefert werden");
                return null;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  currentWFSSize  DOCUMENT ME!
     */
    public void setCurrentWFSSize(final double currentWFSSize) {
        this.currentWFSSize = currentWFSSize;
        fireTableDataChanged();
    }
}
