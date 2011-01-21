/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * PlanCheckBox.java
 *
 * Created on July 17, 2007, 2:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.gui.checkbox;

import javax.swing.JCheckBox;

import de.cismet.lagisEE.interfaces.Plan;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class PlanCheckBox extends JCheckBox {

    //~ Instance fields --------------------------------------------------------

    Plan plan;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of PlanCheckBox.
     *
     * @param  plan  DOCUMENT ME!
     */
    public PlanCheckBox(final Plan plan) {
        super(plan.getBezeichnung());
        this.plan = plan;
    }

    //~ Methods ----------------------------------------------------------------

    // TODO bad style to allow equality with other objects
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof PlanCheckBox) {
            final PlanCheckBox other = (PlanCheckBox)obj;
            return plan.equals(other);
        }
        if (obj instanceof Plan) {
            final Plan other = (Plan)obj;
            return plan.equals(other);
        } else {
            return false;
        }
    }
}
