/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.lagis.layout.actionprovider;

import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.layout.widget.FlurstueckHistoryWidget;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import org.netbeans.api.visual.action.EditProvider;
import org.netbeans.api.visual.widget.Widget;

/**
 * This class implements the org.netbeans.api.visual.action.EditProvider interface.
 * It initiates the change of the current Flurstueck for the complete LagIS
 * application.
 * 
 * @author mbrill
 */
public class FlurstueckWidgetEditProvider implements EditProvider {

    /**
     * Method called on double cklick on a FlurstueckHistoryWidget. It initiates the
     * change of the current Flurstueck for LagIS.
     *
     * @param w The Widget which was double clicked
     */
    @Override
    public void edit(Widget w) {
        if(w instanceof FlurstueckHistoryWidget) {
            FlurstueckHistoryWidget fhw = (FlurstueckHistoryWidget) w;

            FlurstueckSchluessel key = fhw.getFlurstueck().getFlurstueckSchluessel();


//            LagisBroker.getInstance().loadFlurstueck(key);
        }
    }
}
