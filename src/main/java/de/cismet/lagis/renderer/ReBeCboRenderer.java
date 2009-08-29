/*
 * ReBeCboRenderer.java
 *
 * Created on 15. Mai 2007, 16:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.renderer;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Puhl
 */
public class ReBeCboRenderer extends DefaultTableCellRenderer{ 
    
    /** Creates a new instance of ReBeCboRenderer */
    public ReBeCboRenderer() {
        super();        
        setHorizontalAlignment(SwingConstants.CENTER);
    }
    
     protected void setValue(Object value) {
        if(value != null && value instanceof Boolean){            
            if((Boolean)value == true){
                setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/recht.png")));
                setText("  Recht  ");
            } else if((Boolean)value == false) {
                setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/FlurstueckPanel/belastung.png")));
                setText("Belastung");
            }
        }
    }
}
