/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.lagis.layout.actionprovider;

import java.awt.Rectangle;
import org.netbeans.api.visual.action.TwoStateHoverProvider;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.Widget;

/**
 * Class thet implements a behaviour when the user hovers the mouse over a
 * FlurstueckHistoryWidget.
 *
 * @author mbrill
 */
public class FlurstueckWidgetHoverProvider implements TwoStateHoverProvider {

    private Rectangle preferredBounds;
    private GraphScene scene;

    public FlurstueckWidgetHoverProvider(GraphScene scene) {
        super();
        this.scene = scene;
    }

    /**
     * Method is called when the mouse cursor exits a FlurstueckHistoryWidget
     * @param w Widget wich was pointed on
     */
    @Override
    public void unsetHovering(Widget w) {
        scene.getSceneAnimator().animatePreferredBounds(w, preferredBounds);
    }

    /**
     * Method is called when the mouse cursor enters a FlurstueckHistoryWidget
     * @param w Widget which is pointed on 
     */
    @Override
    public void setHovering(Widget w) {
        preferredBounds = w.getBounds();
        Rectangle newBounds = new Rectangle(preferredBounds.x -10, preferredBounds.y -10,
                preferredBounds.width + 10, preferredBounds.height + 10);
        scene.getSceneAnimator().animatePreferredBounds(w, newBounds);
    }

}
