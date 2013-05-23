/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.tables;

import de.cismet.lagis.models.documents.VertragDocumentModelContainer;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public class VertraegeTable extends AbstractCidsBeanTable_Lagis {

    //~ Instance fields --------------------------------------------------------

    private VertragDocumentModelContainer documentContainer;

    //~ Methods ----------------------------------------------------------------

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
    @Override
    protected void addNewItem() {
        documentContainer.addNewVertrag();
    }

    @Override
    protected void removeItem(final int row) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
