/*
 * PlanTableModel.java
 *
 * Created on 17. Mai 2007, 15:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import de.cismet.lagis.utillity.BebauungsVector;
import de.cismet.lagis.utillity.FlaechennutzungsVector;
import de.cismet.lagisEE.entity.core.Beschluss;
import de.cismet.lagisEE.entity.core.hardwired.Beschlussart;
import de.cismet.lagisEE.interfaces.Plan;
import java.util.Date;
import java.util.Set;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author Puhl
 */
public class PlanTableModel extends AbstractTableModel {

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    public int TABLE_MODEL_KIND;
//    private FlaechennutzungsVector fPlan;
//    private BebauungsVector bPlan;
    private Vector<Plan> plaene;
    private final static String[] COLUMN_HEADER = {"Pläne"};
    private boolean isInEditMode = false;

    public PlanTableModel(Vector plaene) {
        if (plaene instanceof FlaechennutzungsVector) {
            TABLE_MODEL_KIND = Plan.PLAN_FLAECHENNUTZUNG;
        //System.out.println("ist flaechenutzung");
        //fPlan = (FlaechennutzungsVector) plaene;
        } else {
            TABLE_MODEL_KIND = Plan.PLAN_BEBAUUNG;
        //System.out.println("ist Bebauung");
        //bPlan = (BebauungsVector) plaene;
        }
        this.plaene = (Vector<Plan>) plaene;
    }

//    public void refreshTableModel(Set plaene){
//        try{
//            log.debug("Refresh des PlaeneTableModell");
//            this.plaene = new Vector(plaene);
//        }catch(Exception ex){
//            log.error("Fehler beim refreshen des Models",ex);
//            this.plaene = new Vector();
//        }
//        fireTableDataChanged();
//    }
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            switch (columnIndex) {
                case 0:
                    //Beschlussart art = beschluss.getBeschlussart();
                    //return art != null ? art.getBezeichnung() : null;
                    return plaene.get(rowIndex);
                default:
                    return "Spalte ist nicht definiert";
            }
        } catch (Exception ex) {
            log.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
            return null;
        }
    }

    public int getRowCount() {
        return plaene.size();
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
        return (COLUMN_HEADER.length > columnIndex) && (plaene.size() > rowIndex);
    }

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Plan.class;
            default:
                log.warn("Die gewünschte Spalte exitiert nicht, es kann keine Klasse zurück geliefert werden");
                return null;
        }
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            switch (columnIndex) {
                case 0:
                    //Beschlussart art = beschluss.getBeschlussart();
                    //return art != null ? art.getBezeichnung() : null;
                    plaene.set(rowIndex, (Plan) aValue);
                    break;
                default:
                    log.warn("Keine Spalte für angegebenen Index vorhanden: "+columnIndex);
                    return;
            }
            fireTableDataChanged();
        } catch (Exception ex) {
            log.error("Fehler beim setzem der Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
        }
    }

    public Vector<Plan> getPlaene() {
        return plaene;
    }

    public void addPlan(Plan plan) {
        plaene.add(plan);
    }

    public Plan getPlanAtRow(int rowIndex) {
        return plaene.get(rowIndex);
    }

    public void removePlan(int rowIndex) {
        plaene.remove(rowIndex);
    }

    public boolean isFnpModel() {
        return TABLE_MODEL_KIND == Plan.PLAN_FLAECHENNUTZUNG;
    }
}
