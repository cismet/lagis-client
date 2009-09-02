/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.lagis.layout.actionprovider;

import de.cismet.lagis.layout.widget.FlurstueckHistoryWidget;
import java.awt.Point;
import java.util.Iterator;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author mbrill
 */
public class FlurstueckSelectProvider implements SelectProvider {

    private GraphScene scene;

    public FlurstueckSelectProvider(GraphScene scene) {
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

        Iterator<Widget> it = scene.getChildren().iterator();
        
        while(it.hasNext()) {
            Widget next = it.next();
            if(next instanceof FlurstueckHistoryWidget)
                ((FlurstueckHistoryWidget) next).setSelected(false);
        }

        if(widget instanceof FlurstueckHistoryWidget)
            ((FlurstueckHistoryWidget) widget).setSelected(true);

        scene.paint();
    }

}
