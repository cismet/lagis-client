/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import org.apache.log4j.Logger;

import java.util.Set;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.core.hardwired.FlurstueckArt;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
public class FlurstueckeTableModel extends AbstractTableModel {

    //~ Static fields/initializers ---------------------------------------------

    // ToDo umlaut entfernen
    private static final String[] COLUMN_HEADER = { "Art", "Flurstück" };

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Vector<FlurstueckSchluessel> flurstueckSchluessel;
    private boolean isInEditMode = false;
    // ToDo in eigene Klasse auslagern
    private final Icon icoStaedtisch = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/current.png"));
    private final Icon icoStaedtischHistoric = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/historic.png"));
    private final Icon icoAbteilungIX = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/abteilungIX.png"));
    private final Icon icoAbteilungIXHistoric = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/historic_abteilungIX.png"));
    private final Icon icoUnknownFlurstueck = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/unkownFlurstueck.png"));

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FlurstueckeTableModel object.
     */
    public FlurstueckeTableModel() {
        flurstueckSchluessel = new Vector<FlurstueckSchluessel>();
    }

    /**
     * Creates a new FlurstueckeTableModel object.
     *
     * @param  flurstueckSchluessel  DOCUMENT ME!
     */
    public FlurstueckeTableModel(final Set<FlurstueckSchluessel> flurstueckSchluessel) {
        try {
            this.flurstueckSchluessel = new Vector<FlurstueckSchluessel>(flurstueckSchluessel);
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            this.flurstueckSchluessel = new Vector<FlurstueckSchluessel>();
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueckSchluessel  DOCUMENT ME!
     */
    public void refreshTableModel(final Set<FlurstueckSchluessel> flurstueckSchluessel) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Refresh des FlurstueckTableModell");
            }
            this.flurstueckSchluessel = new Vector<FlurstueckSchluessel>(flurstueckSchluessel);
        } catch (Exception ex) {
            log.error("Fehler beim refreshen des Models", ex);
            this.flurstueckSchluessel = new Vector<FlurstueckSchluessel>();
        }
        fireTableDataChanged();
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            final FlurstueckSchluessel schluessel = flurstueckSchluessel.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    final FlurstueckArt art = schluessel.getFlurstueckArt();
                    if ((art != null) && (art.getBezeichnung() != null)
                                && art.getBezeichnung().equals(FlurstueckArt.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                        if (schluessel.getGueltigBis() != null) {
                            return icoStaedtischHistoric;
                        } else {
                            return icoStaedtisch;
                        }
                    } else if ((art != null) && (art.getBezeichnung() != null)
                                && art.getBezeichnung().equals(FlurstueckArt.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX)) {
                        if (schluessel.getGueltigBis() != null) {
                            return icoAbteilungIXHistoric;
                        } else {
                            return icoAbteilungIX;
                        }
                    } else {
                        return icoUnknownFlurstueck;
                    }
                }

                case 1: {
                    return schluessel;
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

    @Override
    public int getRowCount() {
        return flurstueckSchluessel.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_HEADER.length;
    }

    @Override
    public String getColumnName(final int column) {
        return COLUMN_HEADER[column];
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        switch (columnIndex) {
            case 0: {
                return Icon.class;
            }
            case 1: {
                // return Date.class;
                return FlurstueckSchluessel.class;
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
     * @return  DOCUMENT ME!
     */
    public Vector<FlurstueckSchluessel> getFlurstueckSchluessel() {
        return flurstueckSchluessel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  schluessel  DOCUMENT ME!
     */
    public void addFlurstueckSchluessel(final FlurstueckSchluessel schluessel) {
        flurstueckSchluessel.add(schluessel);
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckSchluessel getFlurstueckSchluesselAtRow(final int rowIndex) {
        return flurstueckSchluessel.get(rowIndex);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rowIndex  DOCUMENT ME!
     */
    public void removeFlurstueckSchluessel(final int rowIndex) {
        flurstueckSchluessel.remove(rowIndex);
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     */
    public void removeAllFlurstueckSchluessel() {
        flurstueckSchluessel.clear();
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getFlurstueckSchluesselCount() {
        return flurstueckSchluessel.size();
    }
}
