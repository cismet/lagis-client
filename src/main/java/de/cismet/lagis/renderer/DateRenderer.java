/*
 * DateRenderer.java
 *
 * Created on 15. Mai 2007, 11:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.renderer;

import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.editor.DateEditor;
import de.cismet.lagis.validation.Validatable;
import java.awt.Component;
import java.awt.Toolkit;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Puhl
 */
public class DateRenderer extends DefaultTableCellRenderer{
    
    //private DateEditor dateEditor;
    
    //TODO IF THE VALIDATOR IS CHANGEND THIS CLASS WILL NOT BE SYNCHRON (CODE IS COPIED)
    JLabel iconContainer=new JLabel();
    javax.swing.ImageIcon valid=new javax.swing.ImageIcon(this.getClass().getResource("/de/cismet/lagis/ressource/icons/validation/green.png"));
    javax.swing.ImageIcon warning=new javax.swing.ImageIcon(this.getClass().getResource("/de/cismet/lagis/ressource/icons/validation/orange.png"));
    javax.swing.ImageIcon error=new javax.swing.ImageIcon(this.getClass().getResource("/de/cismet/lagis/ressource/icons/validation/red.png"));
    
    /** Creates a new instance of DateRenderer */
    public DateRenderer() {
        super();
        iconContainer.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        iconContainer.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        iconContainer.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
        this.setLayout(new java.awt.BorderLayout());
        this.add(iconContainer,java.awt.BorderLayout.EAST);
        iconContainer.setVisible(true);
        iconContainer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                iconContainerMouseClicked(evt);
            }
        });
        setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    
    protected void setValue(Object value) {
        //System.out.println("setValue Called");
        //System.out.println(value);
        iconContainer.setVisible(true);
        if(value == null){
            setText("");
            //return this;
        }else if(value instanceof Date ){
            Date newDate = (Date) value;
            iconContainer.setIcon(valid);
            iconContainer.putClientProperty("state","VALID");
            setText(LagisBroker.getDateFormatter().format(newDate));
            //return this;
        } else {
            Toolkit.getDefaultToolkit().beep();
            iconContainer.setIcon(error);
            iconContainer.putClientProperty("state","ERROR");
            setText("Ungültige Eingabe");
            //return this;
        }
    }
    
    public void iconContainerMouseClicked(java.awt.event.MouseEvent evt){
        //if (evt.getClickCount()>1 && evt.getButton()==evt.BUTTON1 && vali!=null) {
        if (evt.getClickCount()>1 && evt.getButton()==evt.BUTTON1) {
            //System.out.println("hello");
        }
    }

//    public DateEditor getDateEditor() {
//        return dateEditor;
//    }
//
//    public void setDateEditor(DateEditor dateEditor) {
//        this.dateEditor = dateEditor;
//    }
    
    
    
    
}
