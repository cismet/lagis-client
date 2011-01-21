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

import java.util.Iterator;
import java.util.Vector;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import de.cismet.lagisEE.interfaces.Plan;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class PlanRenderer extends DefaultTableCellRenderer {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of PlanRenderer.
     */
    public PlanRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void setValue(final Object value) {
        final Vector<Plan> plaene = (Vector<Plan>)value;
        if (plaene != null) {
            final Iterator<Plan> it = plaene.iterator();
            final StringBuffer result = new StringBuffer();
            while (it.hasNext()) {
                result.append(it.next().getBezeichnung());
                if (it.hasNext()) {
                    result.append(", ");
                }
            }
            setText(result.toString());
        } else {
            setText("");
        }
    }
}
