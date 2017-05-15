/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.tables;

import org.apache.log4j.Logger;

import de.cismet.cids.custom.beans.lagis.ZusatzRolleCustomBean;

import de.cismet.lagis.models.ZusatzRolleTableModel;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public class ZusatzRolleTable extends AbstractCidsBeanTable_Lagis {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(ZusatzRolleTable.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void addNewItem() {
        try {
            final ZusatzRolleCustomBean zusatzRolle = ZusatzRolleCustomBean.createNew();
            ((ZusatzRolleTableModel)getModel()).addCidsBean(zusatzRolle);
            fireItemAdded();
        } catch (Exception ex) {
            LOG.error("error creating bean for zusatzrolle", ex);
        }
    }

    @Override
    protected void removeItem(final int modelRow) {
        ((ZusatzRolleTableModel)getModel()).removeCidsBean(modelRow);
    }
}
