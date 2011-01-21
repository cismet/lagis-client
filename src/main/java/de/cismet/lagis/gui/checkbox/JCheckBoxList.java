/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
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
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class JCheckBoxList extends JList {

    //~ Static fields/initializers ---------------------------------------------

    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new JCheckBoxList object.
     */
    public JCheckBoxList() {
        setCellRenderer(new CheckBoxCellRenderer());

        addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(final MouseEvent e) {
                    final int index = locationToIndex(e.getPoint());
                    Rectangle test = null;
                    if ((index != -1) && ((test = getCellBounds(index, index)) != null) && test.contains(e.getPoint())
                                && isEnabled()) {
                        final JCheckBox checkbox = (JCheckBox)getModel().getElementAt(index);
                        checkbox.setSelected(!checkbox.isSelected());
                        repaint();
                    }
                }
            });

        addKeyListener(new KeyAdapter() {

                @Override
                public void keyPressed(final KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        final int index = getSelectedIndex();
                        if ((index != -1) && isEnabled()) {
                            final JCheckBox checkbox = (JCheckBox)getModel().getElementAt(index);
                            checkbox.setSelected(!checkbox.isSelected());
                            repaint();
                        }
                    }
                }
            });

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected class CheckBoxCellRenderer implements ListCellRenderer {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final JCheckBox checkbox = (JCheckBox)value;
            // checkbox.setBackground(isSelected && isEnabled() ? getSelectionBackground() : getBackground());
            // checkbox.setForeground(isSelected && isEnabled() ? getSelectionForeground() : getForeground());

            checkbox.setEnabled(isEnabled());
            checkbox.setFont(getFont());
            // checkbox.setFocusPainted(false);

            // checkbox.setBorderPainted(true);
            // checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

            return checkbox;
        }
    }
}
