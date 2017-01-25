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

import de.cismet.cids.custom.beans.lagis.NutzungBuchungCustomBean;
import de.cismet.cids.custom.beans.lagis.NutzungCustomBean;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.models.NKFTableModel;

import de.cismet.tools.gui.StaticSwingTools;

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
        fireItemAdded();
    }

    @Override
    protected void removeItem(final int modelRow) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Selektierte Nutzung gefunden in Zeile: " + modelRow + "selectedRow: "
                        + this.getSelectedRow());
        }
        // removes a Nutzung
        // ((NKFTableModel)getModel()).removeNutzung(modelRow);
        final NKFTableModel model = (NKFTableModel)getModel();
        final NutzungCustomBean nutzung = ((NutzungBuchungCustomBean)model.getCidsBeanAtRow(modelRow)).getNutzung();
        boolean completeRemoval = false;
        boolean performRemove = true;
        if (LagisBroker.getInstance().isNkfAdminPermission()) {
            final int result = showRemoveHistoricalNutzungDialog(nutzung.isTerminated());

            if (result == NKFRemoveNutzungDialog.REMOVE_WITHOUT_HISTORY_OPTION) {
                completeRemoval = true;
            } else if (result == NKFRemoveNutzungDialog.REMOVE_WITH_HISTORY_OPTION) {
                // do nothing
            } else if (result == NKFRemoveNutzungDialog.CANCEL_OPTION) {
                performRemove = false;
            }
        }
        if (performRemove) {
            model.removeNutzungBuchung(modelRow, completeRemoval);
        }

        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    NKFTable.this.clearSelection();
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @param   isNutzungTerminated  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private int showRemoveHistoricalNutzungDialog(final boolean isNutzungTerminated) {
        final NKFRemoveNutzungDialog d = new NKFRemoveNutzungDialog(isNutzungTerminated);
        StaticSwingTools.showDialog(this, d, true);
        return d.getSelectedValue();
    }
}
