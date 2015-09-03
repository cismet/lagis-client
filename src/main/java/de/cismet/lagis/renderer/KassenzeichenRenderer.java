/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * PlanRenderer.java
 *
 * Created on 17. Mai 2007, 16:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.renderer;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class KassenzeichenRenderer extends DefaultTableCellRenderer {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of KassenzeichenRenderer.
     */
    public KassenzeichenRenderer() {
        setHorizontalAlignment(SwingConstants.RIGHT);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void setValue(final Object value) {
        if (value != null) {
            setText(Integer.toString((Integer)value));
        } else {
            setText("");
        }
    }
}
