/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagisEE.entity.core;

import java.awt.Color;
import java.awt.Paint;

import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.StyledFeatureGroupWrapper;
import de.cismet.cismap.commons.gui.piccolo.CustomSelectionStyleFeature;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class CustomSelectionStyledFeatureGroupWrapper extends StyledFeatureGroupWrapper
        implements CustomSelectionStyleFeature {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CustomSelectionStyledFeatureGroupWrapper object.
     *
     * @param  feature    DOCUMENT ME!
     * @param  groupId    DOCUMENT ME!
     * @param  groupName  DOCUMENT ME!
     */
    public CustomSelectionStyledFeatureGroupWrapper(final StyledFeature feature,
            final String groupId,
            final String groupName) {
        super(feature, groupId, groupName);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Paint getSelectionLinePaint() {
        final Color c = ((Color)getLinePaint()).darker();
        return new Color(c.getRed(), c.getGreen(), c.getBlue());
    }

    @Override
    public int getSelectionLineWidth() {
        return getLineWidth() * 2;
    }

    @Override
    public Paint getSelectionFillingPaint() {
        final Color color = ((Color)getFillingPaint());
        return color.brighter();
    }
}
