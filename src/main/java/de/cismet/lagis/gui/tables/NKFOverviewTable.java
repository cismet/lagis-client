/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.tables;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public class NKFOverviewTable extends AbstractCidsBeanTable_Lagis {

    //~ Methods ----------------------------------------------------------------

    /**
     * Not implemented as the user can not add elements to this Table.
     *
     * @throws  UnsupportedOperationException  DOCUMENT ME!
     */
    @Override
    protected void addNewItem() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Not implemented as the user can not add elements to this Table.
     *
     * @param   row  DOCUMENT ME!
     *
     * @throws  UnsupportedOperationException  DOCUMENT ME!
     */
    @Override
    protected void removeItem(final int row) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
