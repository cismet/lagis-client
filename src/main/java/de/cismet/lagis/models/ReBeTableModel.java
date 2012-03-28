/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * ReBeTableModel.java
 *
 * Created on 25. April 2007, 09:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import de.cismet.cids.custom.beans.verdis_grundis.RebeArtCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.RebeCustomBean;

import de.cismet.cismap.commons.features.Feature;

import de.cismet.lagis.broker.LagisBroker;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class ReBeTableModel extends AbstractTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_HEADER = {
            "ist Recht",
            "Art",
            "Art des Rechts",
            "Nummer",
            "Eintragung Datum",
            "Löschung Datum",
            "Bemerkung"
        };

    //~ Instance fields --------------------------------------------------------

    Vector<RebeCustomBean> resBes;
    Vector<RebeArtCustomBean> reBeArten;
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private boolean isInEditMode = false;
    private boolean isReBeKindSwitchAllowed = true;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of ReBeTableModel.
     */
    public ReBeTableModel() {
        resBes = new Vector<RebeCustomBean>();
    }

    /**
     * Creates a new ReBeTableModel object.
     *
     * @param  reBe  DOCUMENT ME!
     */
    public ReBeTableModel(final Collection<RebeCustomBean> reBe) {
        try {
            this.resBes = new Vector<RebeCustomBean>(reBe);
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            this.resBes = new Vector<RebeCustomBean>();
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  reBeArten  DOCUMENT ME!
     */
    public void setReBeArtenList(final Collection<RebeArtCustomBean> reBeArten) {
        try {
            log.error("Versuche RebenArtenListe zu setzen");
            this.reBeArten = new Vector<RebeArtCustomBean>(reBeArten);
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des RebeArtenList", ex);
            this.reBeArten = new Vector<RebeArtCustomBean>();
        }
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            final RebeCustomBean value = resBes.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return value.getIstRecht();
                }
                case 1: {
                    return value.getReBeArt();
                }
                case 2: {
                    return value.getBeschreibung();
                }
                case 3: {
                    return value.getNummer();
                }
                case 4: {
                    return value.getDatumEintragung();
                }
                case 5: {
                    return value.getDatumLoeschung();
                }
                case 6: {
                    return value.getBemerkung();
                }
                default: {
                    return "Spalte ist nicht definiert";
                }
            }
        } catch (Exception ex) {
            log.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
            return null;
        }
        // }
// try{
// RebeCustomBean value = resBes.get(rowIndex);
// switch(columnIndex){
// case 0:
// return value.getIstRecht() == true ? "Recht" : "Belastung";
// case 1:
// RebeArtCustomBean art = value.getArt();
// if(art != null){
// return art.getBezeichnung();
// } else {
// return null;
// }
// case 2:
// return value.getBeschreibung();
// case 3:
// return value.getNummer();
// case 4:
// Date eintragung = value.getDatumEintragung();
// if(eintragung != null){
// return DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(eintragung);
// } else {
// return null;
// }
// case 5:
// Date loeschung = value.getDatumLoeschung();
// if(loeschung != null){
// return DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(loeschung);
// } else {
// return null;
// }
// default:
// return "Spalte ist nicht definiert";
// }
// }catch(Exception ex){
// log.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: "+rowIndex+" Spalte"+columnIndex ,ex);
// return null;
// }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  reBe  DOCUMENT ME!
     */
    public void addReBe(final RebeCustomBean reBe) {
        resBes.add(reBe);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public RebeCustomBean getReBeAtRow(final int rowIndex) {
        return resBes.get(rowIndex);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rowIndex  DOCUMENT ME!
     */
    public void removeReBe(final int rowIndex) {
        final RebeCustomBean reBe = resBes.get(rowIndex);
        if ((reBe != null) && (reBe.getGeometry() != null)) {
            LagisBroker.getInstance().getMappingComponent().getFeatureCollection().removeFeature(reBe);
        }
        resBes.remove(rowIndex);
    }

    @Override
    public int getRowCount() {
        return resBes.size();
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
//        log.fatal("TMP FATAL: getColumn Class: "+ columnIndex);
//        return getValueAt(0,columnIndex).getClass();
//    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        if (columnIndex == 0) {
            return (COLUMN_HEADER.length > columnIndex) && (resBes.size() > rowIndex) && isInEditMode
                        && isReBeKindSwitchAllowed;
        } else {
            return (COLUMN_HEADER.length > columnIndex) && (resBes.size() > rowIndex) && isInEditMode;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isEditable  DOCUMENT ME!
     */
    public void setIsInEditMode(final boolean isEditable) {
        isInEditMode = isEditable;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector<Feature> getAllReBeFeatures() {
        final Vector<Feature> tmp = new Vector<Feature>();
        if (resBes != null) {
            final Iterator<RebeCustomBean> it = resBes.iterator();
            while (it.hasNext()) {
                final RebeCustomBean curReBe = it.next();
                if (curReBe.getGeometry() != null) {
                    tmp.add(curReBe);
                }
            }
            return tmp;
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  resBes  DOCUMENT ME!
     */
    public void refreshTableModel(final Collection<RebeCustomBean> resBes) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Refresh des RebeTableModell");
            }
            this.resBes = new Vector<RebeCustomBean>(resBes);
        } catch (Exception ex) {
            log.error("Fehler beim refreshen des Models", ex);
            this.resBes = new Vector<RebeCustomBean>();
        }
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector<RebeCustomBean> getResBes() {
        return resBes;
    }

    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        try {
            final RebeCustomBean value = resBes.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    value.setIstRecht((Boolean)aValue);
                    break;
                }
                case 1: {
                    value.setReBeArt((RebeArtCustomBean)aValue);
                    break;
                }
                case 2: {
                    value.setBeschreibung((String)aValue);
                    break;
                }
                case 3: {
                    value.setNummer((String)aValue);
                    break;
                }
                case 4: {
                    if ((aValue instanceof Date) || (aValue == null)) {
                        value.setDatumEintragung((Date)aValue);
                    }
                    break;
                }
                case 5: {
                    if ((aValue instanceof Date) || (aValue == null)) {
                        value.setDatumLoeschung((Date)aValue);
                    }
                    break;
                }
                case 6: {
                    value.setBemerkung((String)aValue);
                    break;
                }
                default: {
                    log.warn("Keine Spalte für angegebenen Index vorhanden: " + columnIndex);
                    return;
                }
            }
            fireTableDataChanged();
        } catch (Exception ex) {
            log.error("Fehler beim setzen von Daten in dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
        }
//        RebeCustomBean reBe = resBes.get(rowIndex);
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
//                            RebeArtCustomBean curRBA = it.next();
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

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        switch (columnIndex) {
            case 0: {
                return Boolean.class;
            }
            case 1: {
                return RebeArtCustomBean.class;
            }
            case 2: {
                return String.class;
            }
            case 3: {
                return String.class;
            }
            case 4: {
                return Date.class;
            }
            case 5: {
                return Date.class;
            }
            case 6: {
                return String.class;
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
     * @param   rebe  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getIndexOfReBe(final RebeCustomBean rebe) {
        return resBes.indexOf(rebe);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isReBeKindSwitchAllowed  DOCUMENT ME!
     */
    public void setIsReBeKindSwitchAllowed(final boolean isReBeKindSwitchAllowed) {
        this.isReBeKindSwitchAllowed = isReBeKindSwitchAllowed;
    }
}
