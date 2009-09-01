/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.lagis.layout.actionprovider;

import org.netbeans.api.visual.action.TwoStateHoverProvider;
import org.netbeans.api.visual.widget.Widget;

/**
 * Class thet implements a behaviour when the user hovers the mouse over a
 * FlurstueckHistoryWidget.
 *
 * @author mbrill
 */
public class FlurstueckWidgetHoverProvider implements TwoStateHoverProvider {

    /**
     * Method is called when the mouse cursor exits a FlurstueckHistoryWidget
     * @param w Widget wich was pointed on
     */
    public void unsetHovering(Widget w) {
        System.out.println("Hovering finished" + w);
    }

    /**
     * Method is called when the mouse cursor enters a FlurstueckHistoryWidget
     * @param w Widget which is pointed on 
     */
    public void setHovering(Widget w) {
        System.out.println("Hovering Widget" + w);
    }

}
