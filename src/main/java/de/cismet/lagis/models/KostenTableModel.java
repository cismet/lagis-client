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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import de.cismet.cids.custom.beans.lagis.KostenCustomBean;
import de.cismet.cids.custom.beans.lagis.KostenartCustomBean;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class KostenTableModel extends CidsBeanTableModel_Lagis {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_NAMES = { "Kostenart", "Betrag", "Anweisung" };
    private static final Class[] COLUMN_CLASSES = {
            KostenartCustomBean.class,
            Double.class,
            Date.class
        };

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(KostenTableModel.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of KostenTableModel.
     */
    public KostenTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES, KostenCustomBean.class);
    }

    /**
     * Creates a new KostenTableModel object.
     *
     * @param  kosten  DOCUMENT ME!
     */
    public KostenTableModel(final Collection<KostenCustomBean> kosten) {
        super(COLUMN_NAMES, COLUMN_CLASSES, kosten);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            final KostenCustomBean value = (KostenCustomBean)getCidsBeans().get(rowIndex);
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
            LOG.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
            return null;
        }
    }

    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        try {
            final KostenCustomBean value = (KostenCustomBean)getCidsBeans().get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    value.setKostenart((KostenartCustomBean)aValue);
                    break;
                }
                case 1: {
                    value.setBetrag((Double)aValue);
                }
                case 2: {
                    value.setDatum((Date)aValue);
                }
                default: {
                    LOG.warn("Keine Spalte für angegebenen Index vorhanden: " + columnIndex);
                    return;
                }
            }
            fireTableDataChangedAndKeepSelection();
        } catch (Exception ex) {
            LOG.error("Fehler beim setzem der Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
        }
    }
}
