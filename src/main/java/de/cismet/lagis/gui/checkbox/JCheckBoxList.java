/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.lagis.gui.checkbox;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Sebastian Puhl
 */
public class JCheckBoxList extends JList
{
    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
 
    public JCheckBoxList()
    {
        setCellRenderer(new CheckBoxCellRenderer());
 
        addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                int index = locationToIndex(e.getPoint());
                Rectangle test = null;
                if (index != -1 && (test = getCellBounds(index, index)) != null && test.contains(e.getPoint()) && isEnabled())
                {                    
                    JCheckBox checkbox = (JCheckBox) getModel().getElementAt(index);
                    checkbox.setSelected(!checkbox.isSelected());
                    repaint();
                }
            }
        });
 
        addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_SPACE)
                {
                    int index = getSelectedIndex();
                    if (index != -1 && isEnabled())
                    {
                        JCheckBox checkbox = (JCheckBox) getModel().getElementAt(index);
                        checkbox.setSelected(!checkbox.isSelected());
                        repaint();
                    }
                }
            }
        });
 
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
 
    protected class CheckBoxCellRenderer implements ListCellRenderer
    {
        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus)
        {
            JCheckBox checkbox = (JCheckBox) value;
            //checkbox.setBackground(isSelected && isEnabled() ? getSelectionBackground() : getBackground());
            //checkbox.setForeground(isSelected && isEnabled() ? getSelectionForeground() : getForeground());
 
            checkbox.setEnabled(isEnabled());
            checkbox.setFont(getFont());
            //checkbox.setFocusPainted(false);
 
            //checkbox.setBorderPainted(true);
            //checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
 
            return checkbox;
        }
    }
}
