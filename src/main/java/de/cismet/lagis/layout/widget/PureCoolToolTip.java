
package de.cismet.lagis.layout.widget;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JToolTip;

/**
 * PureCoolToolTip is an implementation of a custom JToolTip. Using a custom
 * ToolTipUI, it is able to display html formatted Strings as well as a tooltip Icon.
 * PureCoolToolTip draws a rounded rectangle with a black, semi transparent color
 * on which the content is displayed.
 *
 * To use a customised tooltip with a swing component, the swing components
 * {@link javax.swing.JComponent#createToolTip() } method must be overridden in the
 * following manner:
 *
 * <code>
 *   public JToolTip createToolTip() {
 *      JToolTip tip = new PureCoolToolTip();
 *      tip.setComponent(this);
 *      return tip;
 *   }
 * </code>
 *
 * @author mbrill
 */
public class PureCoolToolTip extends JToolTip {

    private ResourceBundle icon_bundle = ResourceBundle.getBundle(
            "de/cismet/lagis/ressource/history/icon", new Locale("de", "DE"));

    /**
     * Constructs a new instance of JToolTip using PureCoolToolTipUI and a
     * icon  from a resourceBundle.
     */
    public PureCoolToolTip() {

        ImageIcon icon = new ImageIcon(this.getClass().getResource(
                icon_bundle.getString("icon_tooltip")));
        setUI(new PureCoolToolTipUI(icon));
        setOpaque(false);

    }

}

