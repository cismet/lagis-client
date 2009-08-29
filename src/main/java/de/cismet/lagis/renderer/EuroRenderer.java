/*
 * EuroRenderer.java
 *
 * Created on 27. Mai 2007, 12:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.renderer;

import de.cismet.lagis.broker.LagisBroker;
import java.awt.Toolkit;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Puhl
 */
public class EuroRenderer extends DefaultTableCellRenderer{
    
    //TODO IF THE VALIDATOR IS CHANGEND THIS CLASS WILL NOT BE SYNCHRON (CODE IS COPIED)
    JLabel iconContainer=new JLabel();
    javax.swing.ImageIcon valid=new javax.swing.ImageIcon(this.getClass().getResource("/de/cismet/lagis/ressource/icons/validation/green.png"));
    javax.swing.ImageIcon warning=new javax.swing.ImageIcon(this.getClass().getResource("/de/cismet/lagis/ressource/icons/validation/orange.png"));
    javax.swing.ImageIcon error=new javax.swing.ImageIcon(this.getClass().getResource("/de/cismet/lagis/ressource/icons/validation/red.png"));
    
    /** Creates a new instance of EuroRenderer */
    public EuroRenderer() {
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
        if(value == null){
            setText("");
        }else if(value instanceof Number ){            
            Number newBetrag = (Number) value;
            setText(LagisBroker.getCurrencyFormatter().format(newBetrag));
            //return this;
        }else {
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
}
