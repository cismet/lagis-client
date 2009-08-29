/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.core.hardwired.FlurstueckArt;
import java.util.Set;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author spuhl
 */
public class FlurstueckeTableModel extends AbstractTableModel {

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Vector<FlurstueckSchluessel> flurstueckSchluessel;
    //ToDo umlaut entfernen
    private final static String[] COLUMN_HEADER = {"Art", "Flurstück"};
    private boolean isInEditMode = false;
    //ToDo in eigene Klasse auslagern
    private final Icon icoStaedtisch = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/current.png"));
    private final Icon icoStaedtischHistoric = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/historic.png"));
    private final Icon icoAbteilungIX = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/abteilungIX.png"));
    private final Icon icoAbteilungIXHistoric = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/historic_abteilungIX.png"));
    private final Icon icoUnknownFlurstueck = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/unkownFlurstueck.png"));

    public FlurstueckeTableModel() {
        flurstueckSchluessel = new Vector<FlurstueckSchluessel>();
    }

    public FlurstueckeTableModel(Set<FlurstueckSchluessel> flurstueckSchluessel) {
        try {
            this.flurstueckSchluessel = new Vector<FlurstueckSchluessel>(flurstueckSchluessel);
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            this.flurstueckSchluessel = new Vector<FlurstueckSchluessel>();
        }        
    }

    public void refreshTableModel(Set<FlurstueckSchluessel> flurstueckSchluessel) {
        try {
            log.debug("Refresh des FlurstueckTableModell");
            this.flurstueckSchluessel = new Vector<FlurstueckSchluessel>(flurstueckSchluessel);
        } catch (Exception ex) {
            log.error("Fehler beim refreshen des Models", ex);
            this.flurstueckSchluessel = new Vector<FlurstueckSchluessel>();
        }
        fireTableDataChanged();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            FlurstueckSchluessel schluessel = flurstueckSchluessel.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    FlurstueckArt art = schluessel.getFlurstueckArt();
                    if (art != null && art.getBezeichnung() != null && art.getBezeichnung().equals(FlurstueckArt.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                        if (schluessel.getGueltigBis() != null) {
                            return icoStaedtischHistoric;
                        } else {
                            return icoStaedtisch;
                        }
                    } else if (art != null && art.getBezeichnung() != null && art.getBezeichnung().equals(FlurstueckArt.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX)) {
                        if (schluessel.getGueltigBis() != null) {
                            return icoAbteilungIXHistoric;
                        } else {
                            return icoAbteilungIX;
                        }
                    } else {
                        return icoUnknownFlurstueck;
                    }

                case 1:
                    return schluessel;
                default:
                    return "Spalte ist nicht definiert";
            }
        } catch (Exception ex) {
            log.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
            return null;
        }
    }

    public int getRowCount() {
        return flurstueckSchluessel.size();
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

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Icon.class;
            case 1:
                //return Date.class;
                return FlurstueckSchluessel.class;
            default:
                log.warn("Die gewünschte Spalte exitiert nicht, es kann keine Klasse zurück geliefert werden");
                return null;
        }
    }

    public Vector<FlurstueckSchluessel> getFlurstueckSchluessel() {
        return flurstueckSchluessel;
    }

    public void addFlurstueckSchluessel(FlurstueckSchluessel schluessel) {
        flurstueckSchluessel.add(schluessel);
        fireTableDataChanged();
    }

    public FlurstueckSchluessel getFlurstueckSchluesselAtRow(int rowIndex) {
        return flurstueckSchluessel.get(rowIndex);
    }

    public void removeFlurstueckSchluessel(int rowIndex) {
        flurstueckSchluessel.remove(rowIndex);
        fireTableDataChanged();
    }

    public void removeAllFlurstueckSchluessel() {
        flurstueckSchluessel.clear();
        fireTableDataChanged();
    }
    
    public int getFlurstueckSchluesselCount(){
        return flurstueckSchluessel.size();
    }
}
