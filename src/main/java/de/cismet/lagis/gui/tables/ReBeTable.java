/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.tables;

import org.apache.log4j.Logger;

import de.cismet.cids.custom.beans.lagis.RebeCustomBean;

import de.cismet.lagis.models.ReBeTableModel;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public class ReBeTable extends AbstractCidsBeanTable_Lagis {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(ReBeTable.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void addNewItem() {
        try {
            final RebeCustomBean tmpReBe = RebeCustomBean.createNew();

            // TODO check if isInAbteilungIXModus. model.isReBeKindSwitchAllowed and panel.isInAbteilungIXModus seem to
            // have always the opposite value.  is this correct?
            if (!((ReBeTableModel)getModel()).isIsReBeKindSwitchAllowed()) {
                tmpReBe.setIstRecht(true);
            }

            ((ReBeTableModel)getModel()).addCidsBean(tmpReBe);
            fireItemAdded();
        } catch (Exception ex) {
            LOG.error("error creating rebe bean", ex);
        }
    }

    @Override
    protected void removeItem(final int modelRow) {
        ((ReBeTableModel)getModel()).removeCidsBean(modelRow);
    }
}
