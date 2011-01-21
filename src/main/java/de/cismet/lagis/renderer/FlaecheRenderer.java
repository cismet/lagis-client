/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * FlaecheRenderer.java
 *
 * Created on 11. Januar 2008, 16:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.renderer;

import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class FlaecheRenderer extends DefaultTableCellRenderer {

    //~ Instance fields --------------------------------------------------------

    // TODO IF THE VALIDATOR IS CHANGEND THIS CLASS WILL NOT BE SYNCHRON (CODE IS COPIED)
    JLabel iconContainer = new JLabel();
    javax.swing.ImageIcon valid = new javax.swing.ImageIcon(this.getClass().getResource(
                "/de/cismet/lagis/ressource/icons/validation/green.png"));
    javax.swing.ImageIcon warning = new javax.swing.ImageIcon(this.getClass().getResource(
                "/de/cismet/lagis/ressource/icons/validation/orange.png"));
    javax.swing.ImageIcon error = new javax.swing.ImageIcon(this.getClass().getResource(
                "/de/cismet/lagis/ressource/icons/validation/red.png"));

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of FlaecheRenderer.
     */
    public FlaecheRenderer() {
        super();
        iconContainer.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        iconContainer.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        iconContainer.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
        this.setLayout(new java.awt.BorderLayout());
        this.add(iconContainer, java.awt.BorderLayout.EAST);
        iconContainer.setVisible(true);
        iconContainer.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    iconContainerMouseClicked(evt);
                }
            });
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void setValue(final Object value) {
        // System.out.println("setValue Called");
        // System.out.println(value);
        iconContainer.setVisible(true);
        if (value == null) {
            setText("");
            // return this;
        } else if (value instanceof Integer) {
            iconContainer.setIcon(valid);
            iconContainer.putClientProperty("state", "VALID");
            setText(((Integer)value).toString());
            // return this;
        } else {
            Toolkit.getDefaultToolkit().beep();
            iconContainer.setIcon(error);
            iconContainer.putClientProperty("state", "ERROR");
            setText("Ungültige Eingabe");
            // return this;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    public void iconContainerMouseClicked(final java.awt.event.MouseEvent evt) {
        // if (evt.getClickCount()>1 && evt.getButton()==evt.BUTTON1 && vali!=null) {
        if ((evt.getClickCount() > 1) && (evt.getButton() == evt.BUTTON1)) {
            // System.out.println("hello");
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
