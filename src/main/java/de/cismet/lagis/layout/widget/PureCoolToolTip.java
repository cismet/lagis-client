/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.lagis.layout.widget;

import java.awt.Graphics;
import javax.swing.JToolTip;

/**
 * <p>
 * Class to render Tooltips in a manner that fits into the overall application.
 * It is disigned in the style of the PureCoolPanel and therefor intended to be
 * used with.
 * </p>
 * <p>
 * To set a non default ToolTip component for a JComponent, one has to override
 * the <code>createToolTip</code> method. A default implementation could be :
 * <pre>
 * <code>
 * public JToolTip createToolTip() {
        JToolTip tip = new JToolTip();
        tip.setComponent(this);
        return tip;
    }
 * </code>
 * </pre>
 * </p>
 *
 * @author mbrill
 */
public class PureCoolToolTip extends JToolTip {

    @Override
    public void paintComponent(Graphics g) {
        
    }

}
