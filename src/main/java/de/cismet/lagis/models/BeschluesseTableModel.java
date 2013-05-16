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
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import de.cismet.cids.custom.beans.lagis.BeschlussCustomBean;
import de.cismet.cids.custom.beans.lagis.BeschlussartCustomBean;

import de.cismet.cids.dynamics.CidsBean;

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

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
//    private Vector<BeschlussCustomBean> beschluesse;
//    private boolean isInEditMode = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of BeschluesseTableModel.
     */
    public BeschluesseTableModel() {
        // beschluesse = new Vector<BeschlussCustomBean>();
        super(COLUMN_NAMES, COLUMN_CLASSES);
        setCidsBeans(new ArrayList<BeschlussCustomBean>());
    }

    /**
     * Creates a new BeschluesseTableModel object.
     *
     * @param  beschluesse  DOCUMENT ME!
     */
    public BeschluesseTableModel(final Collection<BeschlussCustomBean> beschluesse) {
        super(COLUMN_NAMES, COLUMN_CLASSES);
        try {
            // this.beschluesse = new Vector<BeschlussCustomBean>(beschluesse);
            setCidsBeans(new ArrayList<CidsBean>(beschluesse));
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            // this.beschluesse = new Vector<BeschlussCustomBean>();
            setCidsBeans(new ArrayList<CidsBean>());
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * public void setBeschluesseModelData(Set<Beschluss> beschluesse) { try{ this.beschluesse = new
     * Vector<Beschluss>(beschluesse); }catch(Exception ex){ log.error("Fehler beim aktualisieren der Modelldaten",ex);
     * this.beschluesse = new Vector<Beschluss>(); } }.
     *
     * @param  beschluesse  DOCUMENT ME!
     */
    public void refreshTableModel(final Collection<BeschlussCustomBean> beschluesse) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Refresh des BeschlussTableModell");
            }
            if (beschluesse != null) {
                // this.beschluesse = new Vector<BeschlussCustomBean>(beschluesse);
                setCidsBeans(new ArrayList<CidsBean>(beschluesse));
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Beschlüssevektor == null --> Erstelle Vektor.");
                }
                // this.beschluesse = new Vector<BeschlussCustomBean>();
                setCidsBeans(new ArrayList<CidsBean>());
            }
        } catch (Exception ex) {
            log.error("Fehler beim refreshen des Models", ex);
            // this.beschluesse = new Vector<BeschlussCustomBean>();
            setCidsBeans(new ArrayList<CidsBean>());
        }
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex     isEditable DOCUMENT ME!
     * @param   columnIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
// public void setIsInEditMode(final boolean isEditable) {
// isInEditMode = isEditable;
// }
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            final BeschlussCustomBean beschluss = (BeschlussCustomBean)getCidsBeans().get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    // BeschlussartCustomBean art = beschluss.getBeschlussart();
                    // return art != null ? art.getBezeichnung() : null;
                    return beschluss.getBeschlussart();
                }
                case 1: {
                    // Date datum = beschluss.getDatum(); return datum != null ?
                    // DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(datum) : null;
                    return beschluss.getDatum();
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

//    @Override
//    public int getRowCount() {
//        return beschluesse.size();
//    }
//
//    @Override
//    public int getColumnCount() {
//        return COLUMN_HEADER.length;
//    }
//    @Override
//    public String getColumnName(final int column) {
//        return COLUMN_HEADER[column];
//    }
//    @Override
//    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
//        return (COLUMN_HEADER.length > columnIndex) && (beschluesse.size() > rowIndex) && isInEditMode;
//    }
//    @Override
//    public Class<?> getColumnClass(final int columnIndex) {
//        switch (columnIndex) {
//            case 0: {
//                return BeschlussartCustomBean.class;
//            }
//            case 1: {
//                return Date.class;
//            }
//            default: {
//                log.warn("Die gewünschte Spalte exitiert nicht, es kann keine Klasse zurück geliefert werden");
//                return null;
//            }
//        }
//    }

    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        try {
            final BeschlussCustomBean beschluss = (BeschlussCustomBean)getCidsBeans().get(rowIndex);
            switch (columnIndex) {
                case 0: {
                    // BeschlussartCustomBean art = beschluss.getBeschlussart();
                    // return art != null ? art.getBezeichnung() : null;
                    beschluss.setBeschlussart((BeschlussartCustomBean)aValue);
                    break;
                }
                case 1: {
                    // Date datum = beschluss.getDatum(); return datum != null ?
                    // DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(datum) : null;
                    beschluss.setDatum((Date)aValue);
                    break;
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
     * @param   rowIndex  beschluss DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
// public Vector<BeschlussCustomBean> getBeschluesse() {
// return beschluesse;
// }
    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex  beschluss DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
// public void addCidsBean(final BeschlussCustomBean beschluss) {
// ((List<BeschlussCustomBean>)getCidsBeans()).add(beschluss);
// }

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
// public BeschlussCustomBean getCidsBeanAtRow(final int rowIndex) {
// return (BeschlussCustomBean)getCidsBeans().get(rowIndex);
// }

    /**
     * DOCUMENT ME!
     *
     * @param  rowIndex  DOCUMENT ME!
     */
// public void removeCidsBean(final int rowIndex) {
// getCidsBeans().remove(rowIndex);
// }
}
