/*
 * PlanCheckBox.java
 *
 * Created on July 17, 2007, 2:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.gui.checkbox;

import de.cismet.lagisEE.interfaces.Plan;
import javax.swing.JCheckBox;

/**
 *
 * @author hell
 */
public class PlanCheckBox extends JCheckBox {
    
    Plan plan;
    /** Creates a new instance of PlanCheckBox */
    public PlanCheckBox(Plan plan) {
        super(plan.getBezeichnung());
        this.plan = plan;        
    }

    //TODO bad style to allow equality with other objects
    public boolean equals(Object obj) {
        if(obj instanceof PlanCheckBox){
            PlanCheckBox other = (PlanCheckBox) obj;
            return plan.equals(other);
        } if(obj instanceof Plan){
            Plan other = (Plan) obj;
            return plan.equals(other);
        } else {
            return false;
        }
                
    }
    
}
