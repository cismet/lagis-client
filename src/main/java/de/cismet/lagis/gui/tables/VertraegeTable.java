/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.tables;

import org.apache.log4j.Logger;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import de.cismet.lagis.models.VertraegeTableModel;
import de.cismet.lagis.models.documents.VertragDocumentModelContainer;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public class VertraegeTable extends AbstractCidsBeanTable_Lagis {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(VertraegeTable.class);

    //~ Instance fields --------------------------------------------------------

    private VertragDocumentModelContainer documentContainer;
    private RemoveActionHelper removeActionHelper;

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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public RemoveActionHelper getRemoveActionHelper() {
        return removeActionHelper;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  removeActionHelper  DOCUMENT ME!
     */
    public void setRemoveActionHelper(final RemoveActionHelper removeActionHelper) {
        this.removeActionHelper = removeActionHelper;
    }

    @Override
    protected void addNewItem() {
        documentContainer.addNewVertrag();
    }

    @Override
    protected void execAfterItemAdded() {
        final MouseEvent me = new MouseEvent(this, 0, 0, 0, 100, 100, 1, false);
        for (final MouseListener ml : this.getMouseListeners()) {
            ml.mouseClicked(me);
        }
    }

    @Override
    protected void removeItem(final int row) {
        ((VertraegeTableModel)getModel()).removeCidsBean(this.getFilters().convertRowIndexToModel(row));
        removeActionHelper.duringRemoveAction(this);
    }

    @Override
    protected void execAfterItemRemoved() {
        removeActionHelper.afterRemoveAction(this);
    }
}
