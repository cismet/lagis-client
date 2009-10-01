/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.lagis.layout.widget;

import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JToolTip;

/**
 *
 * @author mbrill
 */
public class PureCoolToolTip extends JToolTip {

    private Color backgroundColor = new Color(0, 0, 0, 0);

    public PureCoolToolTip(final ImageIcon icon) {

        setForeground(Color.WHITE);
        setBackground(backgroundColor);
        setUI(new PureCoolToolTipUI(icon));
        setOpaque(false);
    }

}

