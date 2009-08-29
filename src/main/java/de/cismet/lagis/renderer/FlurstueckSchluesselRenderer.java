/*
 * FlurstueckSchluesselRenderer.java
 *
 * Created on 30. August 2007, 16:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.renderer;

import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.core.hardwired.Gemarkung;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Sebastian Puhl
 */
public class FlurstueckSchluesselRenderer extends JLabel implements ListCellRenderer {
    
    
    /** Creates a new instance of FlurstueckSchluesselRenderer */
    public FlurstueckSchluesselRenderer() {
        setOpaque(true);        
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if(isSelected){
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
        if(value != null && value instanceof FlurstueckSchluessel){
            FlurstueckSchluessel key = (FlurstueckSchluessel) value;
            setFont(list.getFont());
            if(key.getGemarkung() != null){
                setText(key.getGemarkung().getBezeichnung()+" "+key.getFlur()+" "+key.getFlurstueckZaehler()+"/"+key.getFlurstueckNenner());                
            } else {
                setText("Schlüssel ist unvollständig");
            }
        } else {
            setText("Unbekanntes Objekt");
        }
        return this;
    }                
}
