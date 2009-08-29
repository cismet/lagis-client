/*
 * PlanEditor.java
 *
 * Created on 17. Mai 2007, 15:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.editor;

import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.gui.panels.PlanPanel;
import de.cismet.lagis.models.PlanTableModel;
import de.cismet.lagis.utillity.FlaechennutzungsVector;
import de.cismet.tools.gui.StaticSwingTools;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import org.apache.log4j.Logger;

/**
 *
 * @author Puhl
 */
public class PlanEditor extends AbstractCellEditor
        implements TableCellEditor,
        ActionListener {
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    Color currentColor = Color.WHITE;
    Vector plaene;
    JButton button;
    JDialog dialog;
    protected static final String EDIT = "edit";
    
    public PlanEditor() {
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);
        //Set up the dialog that the button brings up.
//        colorChooser = new JColorChooser();
//        dialog = JColorChooser.createDialog(button,
//                                        "Pick a Color",
//                                        true,  //modal
//                                        colorChooser,
//                                        this,  //OK button handler
//                                        null); //no CANCEL button handler
    }
    
    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
            //The user has clicked the cell, so
            //bring up the dialog.
            button.setBackground(currentColor);            
            if( plaene instanceof FlaechennutzungsVector){                
                dialog = new JDialog(LagisBroker.getInstance().getParentComponent(),"Flächennutzungsplan",true);                
            } else {
                dialog = new JDialog(LagisBroker.getInstance().getParentComponent(),"Bebauungsplan",true);                
            }                        
            dialog.add(new PlanPanel(new PlanTableModel(plaene)));            
            dialog.pack();
            dialog.setLocationRelativeTo(LagisBroker.getInstance().getParentComponent());
            dialog.setVisible(true);
            fireEditingStopped(); //Make the renderer reappear.            
        } else { //User pressed dialog's "OK" button.
            //   currentColor = colorChooser.getColor();
            //System.out.println(e);
        }
    }
    
    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
        //return currentColor;
        return plaene;
    }
    
    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table,
            Object value,
            boolean isSelected,
            int row,
            int column) {
        plaene = (Vector)value;
        log.debug("CelleditorComponent: plaene: "+plaene);
        return button;
    }
    
}
