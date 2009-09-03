/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.layout.actionprovider;

import de.cismet.lagis.layout.model.HistoryPanelModel;
import de.cismet.lagis.layout.widget.CoolBorder;
import de.cismet.lagis.layout.widget.FlurstueckHistoryWidget;
import java.awt.Color;
import java.awt.Point;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author mbrill
 */
public class FlurstueckSelectProvider implements SelectProvider {

    private HistoryPanelModel scene;
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    public FlurstueckSelectProvider(HistoryPanelModel scene) {
        this.scene = scene;
    }

    @Override
    public boolean isAimingAllowed(Widget widget, Point localLocation, boolean invertSelection) {
        return false;
    }

    @Override
    public boolean isSelectionAllowed(Widget widget, Point localLocation, boolean invertSelection) {
        return true;
    }

    @Override
    public void select(Widget widget, Point localLocation, boolean invertSelection) {

        log.debug("Flurstueck selected");
        Iterator<Widget> it = scene.getChildren().iterator();
        while (it.hasNext()) {
            Widget layer = it.next();
            Iterator<Widget> ot = layer.getChildren().iterator();
            while (ot.hasNext()) {
                Widget w = ot.next();
                if (w instanceof FlurstueckHistoryWidget) {
                    FlurstueckHistoryWidget fhw = (FlurstueckHistoryWidget) w;

                    fhw.setBorder(BorderFactory.createEmptyBorder());
                }
            }
        }

//        scene.getOverlayLayer().removeChildren();

        if (widget instanceof FlurstueckHistoryWidget) {
            log.debug("Is FlurstueckHistoryWidget");
            FlurstueckHistoryWidget fhw = (FlurstueckHistoryWidget) widget;
//            fhw.setBorder(new CoolBorder(6, Color.BLUE));
            fhw.setBorder(BorderFactory.createLineBorder(6, Color.BLUE));


//            FlurstueckSelectionOverlayWidget overlay = new FlurstueckSelectionOverlayWidget(scene);
//            overlay.setPreferredLocation(fhw.getLocation());
//            overlay.setPreferredBounds(fhw.getBounds());
//            overlay.bringToFront();
//            scene.getOverlayLayer().addChild(overlay);
        }

        log.debug("Selection done");

    }
}
