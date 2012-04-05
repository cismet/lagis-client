/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * FlurstueckSchluesselRenderer.java
 *
 * Created on 30. August 2007, 16:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class FlurstueckSchluesselRenderer extends JLabel implements ListCellRenderer {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of FlurstueckSchluesselRenderer.
     */
    public FlurstueckSchluesselRenderer() {
        setOpaque(true);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Component getListCellRendererComponent(final JList list,
            final Object value,
            final int index,
            final boolean isSelected,
            final boolean cellHasFocus) {
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        if ((value != null) && (value instanceof FlurstueckSchluesselCustomBean)) {
            final FlurstueckSchluesselCustomBean key = (FlurstueckSchluesselCustomBean)value;
            setFont(list.getFont());
            if (key.getGemarkung() != null) {
                setText(key.getGemarkung().getBezeichnung() + " " + key.getFlur() + " " + key.getFlurstueckZaehler()
                            + "/" + key.getFlurstueckNenner());
            } else {
                setText("Schlüssel ist unvollständig");
            }
        } else {
            setText("Unbekanntes Objekt");
        }
        return this;
    }
}
