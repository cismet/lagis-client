/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.tables;

import org.apache.log4j.Logger;

import javax.swing.SwingUtilities;

import de.cismet.cids.custom.beans.lagis.NutzungCustomBean;

import de.cismet.lagis.models.NKFTableModel;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public class NKFTable extends AbstractCidsBeanTable_Lagis {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(NKFTable.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void addNewItem() {
        ((NKFTableModel)getModel()).addNutzung(NutzungCustomBean.createNew());
        LOG.info("New Nutzung added to Model");
    }

    @Override
    protected void removeItem(final int modelRow) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Selektierte Nutzung gefunden in Zeile: " + modelRow + "selectedRow: "
                        + this.getSelectedRow());
        }
        // removes a Nutzung
        ((NKFTableModel)getModel()).removeNutzung(modelRow);

        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    NKFTable.this.clearSelection();
                }
            });
    }
}
