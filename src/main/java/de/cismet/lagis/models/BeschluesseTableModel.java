/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * BeschluesseTableModel.java
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

import de.cismet.cids.custom.beans.lagis.BeschlussCustomBean;
import de.cismet.cids.custom.beans.lagis.BeschlussartCustomBean;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class BeschluesseTableModel extends CidsBeanTableModel_Lagis {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_NAMES = { "Beschlussart", "Datum" };

    private static final Class[] COLUMN_CLASSES = {
            BeschlussartCustomBean.class,
            Date.class
        };

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(BeschluesseTableModel.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of BeschluesseTableModel.
     */
    public BeschluesseTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES, BeschlussCustomBean.class);
    }

    /**
     * Creates a new BeschluesseTableModel object.
     *
     * @param  beschluesse  DOCUMENT ME!
     */
    public BeschluesseTableModel(final Collection<BeschlussCustomBean> beschluesse) {
        super(COLUMN_NAMES, COLUMN_CLASSES, beschluesse);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex     isEditable DOCUMENT ME!
     * @param   columnIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            final BeschlussCustomBean beschluss = (BeschlussCustomBean)getCidsBeans().get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return beschluss.getBeschlussart();
                }
                case 1: {
                    return beschluss.getDatum();
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
            final BeschlussCustomBean beschluss = (BeschlussCustomBean)getCidsBeans().get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    beschluss.setBeschlussart((BeschlussartCustomBean)aValue);
                    break;
                }
                case 1: {
                    beschluss.setDatum((Date)aValue);
                    break;
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
