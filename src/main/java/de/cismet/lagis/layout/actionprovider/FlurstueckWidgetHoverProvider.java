/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.lagis.layout.actionprovider;

import java.awt.Color;
import org.netbeans.api.visual.action.TwoStateHoverProvider;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.visual.border.EmptyBorder;

/**
 * Class thet implements a behaviour when the user hovers the mouse over a
 * FlurstueckHistoryWidget.
 *
 * @author mbrill
 */
public class FlurstueckWidgetHoverProvider implements TwoStateHoverProvider {

    private Border currentBorder;
    private GraphScene scene;
    private Color borderColor = new Color(0, 0, 255, 0.5f);

    public FlurstueckWidgetHoverProvider(GraphScene scene, Widget w) {
        super();
        this.scene = scene;
    }

    /**
     * Method is called when the mouse cursor exits a FlurstueckHistoryWidget
     * @param w Widget wich was pointed on
     */
    @Override
    public void unsetHovering(Widget w) {
//        scene.getSceneAnimator().animatePreferredBounds(w, preferredBounds);

        w.setBorder(currentBorder);
    }

    /**
     * Method is called when the mouse cursor enters a FlurstueckHistoryWidget
     * @param w Widget which is pointed on 
     */
    @Override
    public void setHovering(Widget w) {
//        Rectangle newBounds = new Rectangle(preferredBounds.x -10, preferredBounds.y -10,
//                preferredBounds.width + 20, preferredBounds.height + 20);
//
//        scene.getSceneAnimator().animatePreferredBounds(w, newBounds);
    
        currentBorder = w.getBorder();

        if(currentBorder == null || currentBorder instanceof EmptyBorder ) {
            w.setBorder(BorderFactory.createLineBorder(10, borderColor));
        }

    }

}
