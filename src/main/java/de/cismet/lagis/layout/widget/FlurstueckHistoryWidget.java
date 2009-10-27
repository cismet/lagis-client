
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

     private AbstractFlurstueckNodePanel nodePanel;

    /**
     * Constructor makes shure that the panel given is of type {@link AbstractFlurstueckNodePanel}
     * which holds instances of flurstueck
     * 
     * @param scene Scene the widget is placed in
     * @param nodePanel JComponent used to render Flurstueck
     */
    public FlurstueckHistoryWidget(Scene scene, AbstractFlurstueckNodePanel nodePanel) {

        super(scene, nodePanel);
        setLayout(LayoutFactory.createVerticalFlowLayout());

        this.nodePanel = nodePanel;
        setOpaque(false);

    }

    public Flurstueck getFlurstueck() {
        return nodePanel.getFlurstueck();
    }


    public AbstractFlurstueckNodePanel getNodePanel() {
        return nodePanel;
    }

    
}
