/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.tables;

import java.awt.event.ActionEvent;

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
        documentContainer.addNewBeschluss();
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
