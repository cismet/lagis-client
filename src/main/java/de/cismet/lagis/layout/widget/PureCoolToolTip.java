/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.lagis.layout.widget;

import java.awt.Color;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JToolTip;

/**
 *
 * @author mbrill
 */
public class PureCoolToolTip extends JToolTip {

    private ResourceBundle icon_bundle = ResourceBundle.getBundle(
            "de/cismet/lagis/ressource/history/icon", new Locale("de", "DE"));
    private Color backgroundColor = new Color(0, 0, 0, 0);

    public PureCoolToolTip() {


        ImageIcon icon = new ImageIcon(this.getClass().getResource(icon_bundle.getString("icon_tooltip")));
        setUI(new PureCoolToolTipUI(icon));
        setOpaque(false);

    }

}

