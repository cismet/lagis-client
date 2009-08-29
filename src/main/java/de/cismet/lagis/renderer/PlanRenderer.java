/*
 * PlanRenderer.java
 *
 * Created on 17. Mai 2007, 16:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.renderer;

import de.cismet.lagisEE.interfaces.Plan;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Puhl
 */
public class PlanRenderer extends DefaultTableCellRenderer {
    
    /** Creates a new instance of PlanRenderer */
    public PlanRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    protected void setValue(Object value) {        
        Vector<Plan> plaene = (Vector<Plan>) value;
        if(plaene != null){
            Iterator<Plan> it = plaene.iterator();
            StringBuffer result = new StringBuffer();
            while(it.hasNext()){
                result.append(it.next().getBezeichnung());
                if(it.hasNext()){
                    result.append(", ");
                }
            }
            setText(result.toString());
        } else {
            setText("");
        }
    }
}
