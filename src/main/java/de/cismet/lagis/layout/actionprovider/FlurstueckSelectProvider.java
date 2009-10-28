
package de.cismet.lagis.layout.actionprovider;

import de.cismet.lagis.layout.model.HistoryPanelModel;
import de.cismet.lagis.layout.widget.FlurstueckHistoryWidget;
import de.cismet.lagis.layout.widget.HighlightWidget;
import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.apache.log4j.Logger;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.widget.Widget;

/**
 * FlurstueckSelectProvider takes care about selecting flurstueck nodes in the 
 * NetBeans Visual environment. It causes the scene to display a highlight widget
 * for the selected node and informs any observer about the selection. Currently
 * the only observer is the main class of the history module, which on selection displays
 * additional information about a flurstueck.
 *
 * @author mbrill
 */
public class FlurstueckSelectProvider implements SelectProvider {

    /**
     * Scene of the graph. The scene is saved for further reference when setting
     * and deleting highlight widgets
     */
    private HistoryPanelModel scene;
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    /**
     * PropertyChangeSupport enables this class to inform observers in form of
     * PropertyChangeEvents
     */
    private PropertyChangeSupport propChangeSupport;

    /**
     * Constructor sets the scene and initialises the PropertyChangeSupport
     * @param scene
     */
    public FlurstueckSelectProvider(HistoryPanelModel scene) {
        this.scene = scene;
        propChangeSupport = new PropertyChangeSupport(this);
    }

    /**
     * Called to check whether aiming is allowe
     * @param widget the aimed widget
     * @param localLocation the local location of a mouse cursor while aiming is invoked by an user
     * @param invertSelection if true, then the invert selection is invoked by an user.
     * @return false
     */
    @Override
    public boolean isAimingAllowed(Widget widget, Point localLocation, boolean invertSelection) {
        return false;
    }

    /**
     * Called to check whether the selection is allowed.
     * @param widget the selected widget
     * @param localLocation the local location of a mouse cursor while selection is invoked by an user
     * @param invertSelection if true, then the invert selection is invoked by an user.
     * @return true
     */
    @Override
    public boolean isSelectionAllowed(Widget widget, Point localLocation, boolean invertSelection) {
        return true;
    }

    /**
     * Called to perform the selection.
     * @param widget the selected widget
     * @param localLocation the local location of a mouse cursor while selection is invoked by an user
     * @param invertSelection if true, then the invert selection is invoked by an user.
     */
    @Override
    public void select(Widget widget, Point localLocation, boolean invertSelection) {

        scene.getHighlightLayer().removeChildren();

        if (widget instanceof FlurstueckHistoryWidget) {
            log.debug("Flurstueck selected");
            FlurstueckHistoryWidget fhw = (FlurstueckHistoryWidget) widget;

            propChangeSupport.firePropertyChange("flurstueck_selected",
                    null, fhw.getFlurstueck());

            HighlightWidget hw = new HighlightWidget(scene, fhw);
            
            scene.getHighlightLayer().addChild(hw);
            

        }

        scene.revalidate(true);
        log.debug("Selection done");

    }

    /**
     * Method to attach observers to the PropertyChangeSupport
     *
     * @param listener Observer to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propChangeSupport.addPropertyChangeListener(listener);
    }

}
