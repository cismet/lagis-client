/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.gui.tables;

import de.cismet.cids.custom.beans.lagis.GeomCustomBean;
import de.cismet.cids.custom.beans.lagis.MipaCustomBean;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.StyledFeatureGroupWrapper;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.models.MiPaModel;

import static de.cismet.lagis.gui.panels.VerwaltungsPanel.PROVIDER_NAME;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public class MipaTable extends AbstractCidsBeanTable_Lagis {

    //~ Instance fields --------------------------------------------------------

    private RemoveActionHelper removeActionHelper;

    //~ Methods ----------------------------------------------------------------

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
        final MipaCustomBean tmpMiPa = MipaCustomBean.createNew();
        final GeomCustomBean tmpGeom = GeomCustomBean.createNew();
        tmpGeom.setGeo_field(LagisBroker.getInstance().getCurrentWFSGeometry());
        tmpMiPa.setGeometrie(tmpGeom);
        tmpMiPa.setFlaeche(tmpGeom.getGeo_field().getArea());
        ((MiPaModel)getModel()).addCidsBean(tmpMiPa);

        final Feature feature = new StyledFeatureGroupWrapper(tmpMiPa, PROVIDER_NAME, PROVIDER_NAME);
        LagisBroker.getInstance().getMappingComponent().getFeatureCollection().addFeature(feature);

        fireItemAdded();
    }

    @Override
    protected void removeItem(final int row) {
        ((MiPaModel)getModel()).removeCidsBean((this.convertRowIndexToModel(getSelectedRow())));
        removeActionHelper.duringRemoveAction(this);
    }
}
