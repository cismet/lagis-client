/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.layout.widget;

import de.cismet.lagisEE.entity.core.Flurstueck;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.Scene;

/**
 * Wrapper class to enable the use of Swing components to display Flurstuecke
 *
 * @author mbrill
 */
public class FlurstueckHistoryWidget extends ComponentWidget {

     AbstractFlurstueckNodePanel nodePanel;

    /**
     * Constructor
     * @param scene Scene the widget is placed in
     * @param nodePanel JComponent used to render Flurstueck
     */
    public FlurstueckHistoryWidget(Scene scene, AbstractFlurstueckNodePanel nodePanel) {

        super(scene, nodePanel);
        setLayout(LayoutFactory.createVerticalFlowLayout());

        this.nodePanel = nodePanel;

    }

    public Flurstueck getFlurstueck() {
        return nodePanel.getFlurstueck();
    }
}
