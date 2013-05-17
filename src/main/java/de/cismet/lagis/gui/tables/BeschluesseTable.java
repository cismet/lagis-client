/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.tables;

import org.jdesktop.swingx.JXTable;

import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;

import de.cismet.lagis.models.documents.VertragDocumentModelContainer;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public class BeschluesseTable extends AbstractCidsBeanTable_Lagis {

    //~ Instance fields --------------------------------------------------------

    private VertragDocumentModelContainer documentContainer;

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void btnAddActionPerformed(final ActionEvent evt) {
        getSortButton().setSelected(true);
        this.setSortable(false);
        documentContainer.addNewBeschluss();
        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final BeschluesseTable table = BeschluesseTable.this;
                    table.setRowSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
                    table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));
                }
            });
    }

    @Override
    protected void btnRemoveActionPerformed(final ActionEvent evt) {
        final int currentRow = this.getSelectedRow();
        if (currentRow != -1) {
            documentContainer.removeBeschluss(this.getFilters().convertRowIndexToModel(
                    currentRow));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public VertragDocumentModelContainer getDocumentContainer() {
        return documentContainer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  documentContainer  DOCUMENT ME!
     */
    public void setDocumentContainer(final VertragDocumentModelContainer documentContainer) {
        this.documentContainer = documentContainer;
    }
}
