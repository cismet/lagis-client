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

import de.cismet.cids.custom.beans.lagis.KostenCustomBean;
import de.cismet.cids.custom.beans.lagis.KostenartCustomBean;

import java.util.ArrayList;

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

    //~ Instance fields --------------------------------------------------------

    private static final  Logger LOG = org.apache.log4j.Logger.getLogger(KostenTableModel.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of KostenTableModel.
     */
    public KostenTableModel() {
        //kosten = new Vector<KostenCustomBean>();
        super(COLUMN_NAMES, COLUMN_CLASSES);
        setCidsBeans(new ArrayList<KostenCustomBean>());
    }

    /**
     * Creates a new KostenTableModel object.
     *
     * @param  kosten  DOCUMENT ME!
     */
    public KostenTableModel(final Collection<KostenCustomBean> kosten) {
        super(COLUMN_NAMES, COLUMN_CLASSES);
        try {
            setCidsBeans(new ArrayList<KostenCustomBean>(kosten));
        } catch (Exception ex) {
            LOG.error("Fehler beim anlegen des Models", ex);
            setCidsBeans(new ArrayList<KostenCustomBean>());
        }
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
            fireTableDataChanged();
        } catch (Exception ex) {
            LOG.error("Fehler beim setzem der Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
        }
    }
}
