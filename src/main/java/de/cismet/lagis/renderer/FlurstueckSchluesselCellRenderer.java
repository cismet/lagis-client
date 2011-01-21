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
package de.cismet.lagis.renderer;

import java.awt.Toolkit;

import java.util.Date;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
public class FlurstueckSchluesselCellRenderer extends DefaultTableCellRenderer {

    //~ Instance fields --------------------------------------------------------

    JLabel iconContainer = new JLabel();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FlurstueckSchluesselCellRenderer object.
     */
    public FlurstueckSchluesselCellRenderer() {
        super();
        iconContainer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconContainer.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        iconContainer.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
        this.setLayout(new java.awt.BorderLayout());
        this.add(iconContainer, java.awt.BorderLayout.EAST);
        iconContainer.setVisible(true);
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
        } else if (value instanceof FlurstueckSchluessel) {
            final FlurstueckSchluessel key = (FlurstueckSchluessel)value;
            // setFont(list.getFont());
            if (key.getGemarkung() != null) {
                setText(key.getGemarkung().getBezeichnung() + " " + key.getFlur() + " " + key.getFlurstueckZaehler()
                            + "/" + key.getFlurstueckNenner());
            } else {
                setText("Schlüssel ist unvollständig");
            }
        } else {
            setText("");
        }
    }
}
