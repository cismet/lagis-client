/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.lagis.layout.widget;

import de.cismet.lagisEE.entity.core.Flurstueck;
import javax.swing.JPanel;

/**
 * This abstract class offers an interface for Swing panels that display
 * flurstueck information.
 *
 * @author mbrill
 */
public abstract class AbstractFlurstueckNodePanel extends JPanel {

    protected Flurstueck flurstueck = null;

    /**
     * Constructor requires a flurstueck which will be displayed. In addition
     * it disables doubleBuffering for this component by default since this
     * leads to repaint bugs in the NetBeans Visual API. Never the less it has
     * to be ensured that any component used inside this container als disables
     * double buffering.
     *
     * @param flurstueck The Flurstueck to display is attribute to this abstract class
     */
    protected AbstractFlurstueckNodePanel(Flurstueck flurstueck) {
        this.flurstueck = flurstueck;

        setDoubleBuffered(false);
    }

    /**
     * Getter for the dispalyed Flurstueck
     * @return Flurstueck 
     */
    public Flurstueck getFlurstueck() {
        return flurstueck;
    }


    public abstract void setSelected(boolean selection);

}