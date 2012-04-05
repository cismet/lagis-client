/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * KostenTableModel.java
 *
 * Created on 25. April 2007, 13:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import org.apache.log4j.Logger;

import java.text.DecimalFormat;

import java.util.Collection;
import java.util.Date;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import de.cismet.cids.custom.beans.lagis.KostenCustomBean;
import de.cismet.cids.custom.beans.lagis.KostenartCustomBean;

import de.cismet.lagis.broker.LagisBroker;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class KostenTableModel extends AbstractTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_HEADER = { "Kostenart", "Betrag", "Anweisung" };

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Vector<KostenCustomBean> kosten;
    private DecimalFormat df = LagisBroker.getCurrencyFormatter();
    private boolean isInEditMode = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of KostenTableModel.
     */
    public KostenTableModel() {
        kosten = new Vector<KostenCustomBean>();
    }

    /**
     * Creates a new KostenTableModel object.
     *
     * @param  kosten  DOCUMENT ME!
     */
    public KostenTableModel(final Collection<KostenCustomBean> kosten) {
        try {
            this.kosten = new Vector<KostenCustomBean>(kosten);
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            this.kosten = new Vector<KostenCustomBean>();
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * public void setKostenModelData(Set<Kosten> kosten){ try{ this.kosten = new Vector<Kosten>(kosten);
     * }catch(Exception ex){ log.error("Fehler beim aktualisieren der Modelldaten",ex); this.kosten = new
     * Vector<Kosten>(); } }.
     *
     * @param  kosten  DOCUMENT ME!
     */
    public void refreshTableModel(final Collection<KostenCustomBean> kosten) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Refresh des KostenTableModell");
            }
            if (kosten != null) {
                this.kosten = new Vector<KostenCustomBean>(kosten);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Kostenvektor == null --> Erstelle Vektor.");
                }
                this.kosten = new Vector<KostenCustomBean>();
            }
        } catch (Exception ex) {
            log.error("Fehler beim refreshen des Models", ex);
            this.kosten = new Vector<KostenCustomBean>();
        }
        fireTableDataChanged();
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            final KostenCustomBean value = kosten.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return value.getKostenart();
                }
                case 1: {
                    return value.getBetrag();
                }
                case 2: {
                    return value.getDatum();
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
        return kosten.size();
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
        return (COLUMN_HEADER.length > columnIndex) && (kosten.size() > rowIndex) && isInEditMode;
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
    public Class<?> getColumnClass(final int columnIndex) {
        switch (columnIndex) {
            case 0: {
                return KostenartCustomBean.class;
            }
            case 1: {
                return Double.class;
            }
            case 2: {
                return Date.class;
            }
            default: {
                log.warn("Die gewünschte Spalte exitiert nicht, es kann keine Klasse zurück geliefert werden");
                return null;
            }
        }
    }

    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        try {
            final KostenCustomBean value = kosten.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    value.setKostenart((KostenartCustomBean)aValue);
                    break;
                }
                case 1: {
                    value.setBetrag((Double)aValue);
                }
                case 2: {
                    // Date datum = beschluss.getDatum(); return datum != null ?
                    // DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(datum) : null;
                    value.setDatum((Date)aValue);
                }
                default: {
                    log.warn("Keine Spalte für angegebenen Index vorhanden: " + columnIndex);
                    return;
                }
            }
            fireTableDataChanged();
        } catch (Exception ex) {
            log.error("Fehler beim setzem der Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector<KostenCustomBean> getKosten() {
        return kosten;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  beschluss  DOCUMENT ME!
     */
    public void addKosten(final KostenCustomBean beschluss) {
        kosten.add(beschluss);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public KostenCustomBean getKostenAtRow(final int rowIndex) {
        return kosten.get(rowIndex);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rowIndex  DOCUMENT ME!
     */
    public void removeKosten(final int rowIndex) {
        kosten.remove(rowIndex);
    }
}
