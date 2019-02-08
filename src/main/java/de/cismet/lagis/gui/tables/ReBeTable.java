/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.tables;

import org.apache.log4j.Logger;

import de.cismet.cids.custom.beans.lagis.GeomCustomBean;
import de.cismet.cids.custom.beans.lagis.RebeCustomBean;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.StyledFeatureGroupWrapper;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.models.ReBeTableModel;

import static de.cismet.lagis.gui.panels.VerwaltungsPanel.PROVIDER_NAME;

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
            final GeomCustomBean tmpGeom = GeomCustomBean.createNew();
            tmpGeom.setGeo_field(LagisBroker.getInstance().getCurrentWFSGeometry());
            tmpReBe.setGeometrie(tmpGeom);

            // TODO check if isInAbteilungIXModus. model.isReBeKindSwitchAllowed and panel.isInAbteilungIXModus seem to
            // have always the opposite value.  is this correct?
            if (!((ReBeTableModel)getModel()).isIsReBeKindSwitchAllowed()) {
                tmpReBe.setIstRecht(true);
            }

            ((ReBeTableModel)getModel()).addCidsBean(tmpReBe);

            final Feature feature = new StyledFeatureGroupWrapper(tmpReBe, PROVIDER_NAME, PROVIDER_NAME);
            LagisBroker.getInstance().getMappingComponent().getFeatureCollection().addFeature(feature);

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
