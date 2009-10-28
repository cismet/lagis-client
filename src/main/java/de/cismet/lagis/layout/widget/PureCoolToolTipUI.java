/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.layout.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.plaf.ToolTipUI;
import org.jdesktop.swingx.image.GaussianBlurFilter;

/**
 * <p>
 * Class to render Tooltips in a manner that fits into the overall cismet applications.
 * It is designed in the style of the PureCoolPanel (black, transparency, icon)
 * and therefor intended to be used with.
 * </p>
 * <p>
 * To set a non default ToolTip component for a JComponent, one has to override
 * the <code>createToolTip</code> method. A default implementation could be :
 * <pre>
 * <code>
 * public JToolTip createToolTip() {
 *  JToolTip tip = new PureCoolToolTip();
 *  tip.setComponent(this);
 *  return tip;
 * }
 * </code>
 * </pre>
 * </p>
 *
 * In order to be able to render HTML strings, this class uses an offscreen label
 * which is rendered into the graphics context of the component using this UI.
 * This became nesseccary, because there is no easy way to convert a HTML formatted
 * String into the equivalent plain text representation, but the SwingUtils2 utility,
 * which is used e.g. in {@link javax.swing.JLabel } or the
 * {@link javax.swing.plaf.metal.MetalToolTipUI }. Since SwingUtils2 is sun proprietary
 * code which can change from one dot dot release to the other, this class delegates
 * the task of rendering HTML formatted strings to JLabel.
 *
 * @author mbrill
 */
public class PureCoolToolTipUI extends ToolTipUI {

    /**
     * The tooltip instance this UI was instantiated for
     */
    private JToolTip tip;

    /**
     * The color of the background
     */
    private Color backgroundColor = new Color(0, 0, 0, 120);

    /**
     * An Alpha only color, used to draw an invisible panel
     */
    private Color clearColor = new Color(0, 0, 0, 255);

    /**
     * Icon to be drawn on the background of the tooltip
     */
    private ImageIcon tooltipIcon = null;

    /**
     * vertical offset from the top to the beginning of the text
     */
    private int textOffsetY = 25;

    /**
     * vertical offset from the bottom to the end of the text
     */
    private int textOffsetBottom = 6;

    /**
     * Size of the upper inset of the offscreen JLabel
     */
    private int offsetTop = 10;

    /**
     * vertical offset of the icon from the top
     */
    private int iconOffsetTop = 8;

    /**
     * horizontal offset of the icon from the right
     */
    private int iconOffsetRight = 8;

    /**
     * JLabel used to render HTML formatted strings
     */
    private JLabel offscreenLabel;

    /**
     * Constructs a new ToolTipUI and initialises an offscreen label, which is
     * used to render html strings.
     * @param tooltipIcon
     */
    public PureCoolToolTipUI(final ImageIcon tooltipIcon) {
        if (tooltipIcon != null) {
            this.tooltipIcon = tooltipIcon;
            offsetTop += tooltipIcon.getIconHeight() + iconOffsetTop;
        }

        offscreenLabel = new JLabel();
        offscreenLabel.setForeground(Color.WHITE);
        offscreenLabel.setOpaque(false);

    }

    /**
     * This paint method simply causes the offscreent JLabel of this class
     * to draw itself into the given graphics context with a specific offset
     * from the maximum bounds. This is because the background is drawn as
     * a rounded rectangle hence the JLabel may render itself on a curve of the
     * background if no insets were given.
     *
     * @param g Graphics context to draw in
     * @param c Specific component to draw on
     */
    @Override
    public void paint(final Graphics g, final JComponent c) {

        if (c instanceof JToolTip) {
            tip = (JToolTip) c;

            offscreenLabel.setText(tip.getTipText());


            offscreenLabel.setBorder(BorderFactory.createEmptyBorder(offsetTop,
                    textOffsetY, textOffsetBottom, textOffsetY));

            JPanel pan = new JPanel();
            pan.setBackground(clearColor);
            pan.setForeground(clearColor);
            pan.setOpaque(false);
            pan.setLayout(new BorderLayout());
            pan.add(offscreenLabel, BorderLayout.CENTER);
            pan.setSize(offscreenLabel.getPreferredSize());
            pan.doLayout();

            offscreenLabel.paint(g);
        }
    }

    /**
     * Method to determine the preferred size of a JToolTip component which is
     * to be drawn. This is done by creating the offscreen JLabel with the information
     * given (tooltipText, icon, insets) and query the preferred size of this component.
     *
     * @param c Specific component to draw on
     * @return java.awt.Dimension The Dimension of the offscreen JLabel which is the size
     * of the overall tooltip
     */
    @Override
    public Dimension getPreferredSize(final JComponent c) {
        if (c instanceof JToolTip) {
            tip = (JToolTip) c;
        }

        offscreenLabel.setText(tip.getTipText());

        offscreenLabel.setBorder(BorderFactory.createEmptyBorder(offsetTop,
                textOffsetY, textOffsetBottom, textOffsetY));

        return offscreenLabel.getPreferredSize();

    }

    /**
     * The Update method of a UI is used to draw a component rather than any
     * content. In this case one can think of the update method as the way to
     * draw the tooltips background. It simply draws a rounded rectangle with
     * the position and size of the tooltip component and an image in the
     * upper right corner if one exists.
     *
     * @param g Graphics context to draw in
     * @param c Specific component to draw on
     */
    @Override
    public void update(final Graphics g, final JComponent c) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 15, 15);

        drawBackgroundImage(g, c);

        paint(g, c);
    }

    /**
     * This method draws a backgroundimage with a blur effect on the tooltip if one exists.
     *
     * @param g Graphics context to draw in
     * @param c Specific component to draw on
     */
    private void drawBackgroundImage(final Graphics g, final JComponent c) {
        if (tooltipIcon != null) {

            Graphics2D g2d = (Graphics2D) g;

            BufferedImage background = new BufferedImage(c.getWidth(), c.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);

            Graphics2D backgroundGraphics = background.createGraphics();

            backgroundGraphics.drawImage(tooltipIcon.getImage(),
                    c.getWidth() - tooltipIcon.getIconWidth() - iconOffsetRight,
                    iconOffsetTop, c);

            GaussianBlurFilter blurFilter = new GaussianBlurFilter(3);
            background = blurFilter.filter(background, null);

            g2d.drawImage(background, 0, 0, c);

            backgroundGraphics.dispose();
            background.flush();

        }
    }
}


