/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.tables;

import org.apache.log4j.Logger;

import de.cismet.cids.custom.beans.lagis.VerwaltungsbereichCustomBean;

import de.cismet.lagis.models.VerwaltungsTableModel;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public class VerwaltungsTable extends AbstractCidsBeanTable_Lagis {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(VerwaltungsTable.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void addNewItem() {
        try {
            // VerwaltungsTableModel currentModel = (VerwaltungsTableModel)tNutzung.getModel();
            final VerwaltungsbereichCustomBean tmp = VerwaltungsbereichCustomBean.createNew();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Verwalungsbereich Gebrauch: " + tmp.getGebrauch());
            }

            ((VerwaltungsTableModel)getModel()).addCidsBean(tmp);
        } catch (Exception ex) {
            LOG.error("error creating bean for verwaltungsbereiche", ex);
        }
    }

    @Override
    protected void removeItem(final int row) {
        ((VerwaltungsTableModel)getModel()).removeCidsBean(this.convertRowIndexToModel(row));
    }
}
