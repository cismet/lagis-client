/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * PlanTableModel.java
 *
 * Created on 17. Mai 2007, 15:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import de.cismet.lagis.utillity.BebauungsVector;
import de.cismet.lagis.utillity.FlaechennutzungsVector;

import de.cismet.lagisEE.entity.core.Beschluss;
import de.cismet.lagisEE.entity.core.hardwired.Beschlussart;

import de.cismet.lagisEE.interfaces.Plan;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class PlanTableModel extends AbstractTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_HEADER = { "Pläne" };

    //~ Instance fields --------------------------------------------------------

    public int TABLE_MODEL_KIND;

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
//    private FlaechennutzungsVector fPlan;
//    private BebauungsVector bPlan;
    private Vector<Plan> plaene;
    private boolean isInEditMode = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PlanTableModel object.
     *
     * @param  plaene  DOCUMENT ME!
     */
    public PlanTableModel(final Vector plaene) {
        if (plaene instanceof FlaechennutzungsVector) {
            TABLE_MODEL_KIND = Plan.PLAN_FLAECHENNUTZUNG;
            // System.out.println("ist flaechenutzung");
            // fPlan = (FlaechennutzungsVector) plaene;
        } else {
            TABLE_MODEL_KIND = Plan.PLAN_BEBAUUNG;
            // System.out.println("ist Bebauung");
            // bPlan = (BebauungsVector) plaene;
        }
        this.plaene = (Vector<Plan>)plaene;
    }

    //~ Methods ----------------------------------------------------------------

// public void refreshTableModel(Set plaene){
// try{
// log.debug("Refresh des PlaeneTableModell");
// this.plaene = new Vector(plaene);
// }catch(Exception ex){
// log.error("Fehler beim refreshen des Models",ex);
// this.plaene = new Vector();
// }
// fireTableDataChanged();
// }
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            switch (columnIndex) {
                case 0: {
                    // Beschlussart art = beschluss.getBeschlussart();
                    // return art != null ? art.getBezeichnung() : null;
                    return plaene.get(rowIndex);
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
        return plaene.size();
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
        return (COLUMN_HEADER.length > columnIndex) && (plaene.size() > rowIndex);
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        switch (columnIndex) {
            case 0: {
                return Plan.class;
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
            switch (columnIndex) {
                case 0: {
                    // Beschlussart art = beschluss.getBeschlussart();
                    // return art != null ? art.getBezeichnung() : null;
                    plaene.set(rowIndex, (Plan)aValue);
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
     * @return  DOCUMENT ME!
     */
    public Vector<Plan> getPlaene() {
        return plaene;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  plan  DOCUMENT ME!
     */
    public void addPlan(final Plan plan) {
        plaene.add(plan);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Plan getPlanAtRow(final int rowIndex) {
        return plaene.get(rowIndex);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rowIndex  DOCUMENT ME!
     */
    public void removePlan(final int rowIndex) {
        plaene.remove(rowIndex);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isFnpModel() {
        return TABLE_MODEL_KIND == Plan.PLAN_FLAECHENNUTZUNG;
    }
}
