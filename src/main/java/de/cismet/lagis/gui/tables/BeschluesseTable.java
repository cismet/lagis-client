/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.tables;

import java.awt.event.ActionEvent;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public class BeschluesseTable extends AbstractCidsBeanTable_Lagis {

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void btnAddActionPerformed(final ActionEvent evt) {
        documentContainer.addNewBeschluss();
    }
}
