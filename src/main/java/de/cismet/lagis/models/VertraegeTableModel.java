/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * VertraegeTableModel.java
 *
 * Created on 25. April 2007, 13:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import org.apache.log4j.Logger;

import java.text.DecimalFormat;

import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import de.cismet.cids.custom.beans.lagis.VertragCustomBean;
import de.cismet.cids.custom.beans.lagis.VertragsartCustomBean;

import de.cismet.lagis.broker.LagisBroker;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class VertraegeTableModel extends AbstractTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_HEADER = {
            "Vertragsart",
            "Aktenzeichen",
            "Quadratmeterpreis",
            "Kaufpreis (i. NK)"
        };

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Vector<VertragCustomBean> vertraege;
    private DecimalFormat df = LagisBroker.getCurrencyFormatter();
    // Models

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of VertraegeTableModel.
     */
    public VertraegeTableModel() {
        vertraege = new Vector<VertragCustomBean>();
    }

    /**
     * Creates a new VertraegeTableModel object.
     *
     * @param  vertraege  DOCUMENT ME!
     */
    public VertraegeTableModel(final Collection<VertragCustomBean> vertraege) {
        try {
            this.vertraege = new Vector<VertragCustomBean>(vertraege);
            // log.fatal("Voreigentuemer: "+this.vertraege.get(0).getVoreigentuemer());
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            this.vertraege = new Vector<VertragCustomBean>();
            final HashSet test = new HashSet();
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            final VertragCustomBean vertrag = vertraege.get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    final VertragsartCustomBean art = vertrag.getVertragsart();
                    if (art != null) {
                        return art.getBezeichnung();
                    } else {
                        return null;
                    }
                }
                case 1: {
                    return vertrag.getAktenzeichen();
                }
                case 2: {
                    final Double qPreis = vertrag.getQuadratmeterpreis();
                    return (qPreis != null) ? df.format(qPreis) : null;
                }
                case 3: {
                    final Double gPreis = vertrag.getGesamtpreis();
                    return (gPreis != null) ? df.format(gPreis) : null;
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
        return vertraege.size();
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

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public VertragCustomBean getVertragAtRow(final int rowIndex) {
        return vertraege.get(rowIndex);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  vertrag  DOCUMENT ME!
     */
    public void addVertrag(final VertragCustomBean vertrag) {
        vertraege.add(vertrag);
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rowIndex  DOCUMENT ME!
     */
    public void removeVertrag(final int rowIndex) {
        vertraege.remove(rowIndex);
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector<VertragCustomBean> getVertraege() {
        return vertraege;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  vertraege  DOCUMENT ME!
     */
    public void refreshTableModel(final Collection<VertragCustomBean> vertraege) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Refresh des VertraegeTableModell");
            }
            if (vertraege != null) {
                this.vertraege = new Vector<VertragCustomBean>(vertraege);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Vertraege == null --> Erstelle neuer Vektor.");
                }
                this.vertraege = new Vector<VertragCustomBean>();
            }
        } catch (Exception ex) {
            log.error("Fehler beim refreshen des Models", ex);
            this.vertraege = new Vector<VertragCustomBean>();
        }
        fireTableDataChanged();
    }
}
