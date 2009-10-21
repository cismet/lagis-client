/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.layout.widget;

import com.jhlabs.composite.ColorComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
 * It is disigned in the style of the PureCoolPanel and therefor intended to be
 * used with.
 * </p>
 * <p>
 * To set a non default ToolTip component for a JComponent, one has to override
 * the <code>createToolTip</code> method. A default implementation could be :
 * <pre>
 * <code>
 * public JToolTip createToolTip() {
JToolTip tip = new PureCoolToolTip();
tip.setComponent(this);
return tip;
}
 * </code>
 * </pre>
 * </p>
 *
 * @author mbrill
 */
public class PureCoolToolTipUI extends ToolTipUI {

    private JToolTip tip;
    private Color backgroundColor = new Color(0, 0, 0, 120);
    private Color clearColor = new Color(0, 0, 0, 255);
    private ImageIcon tooltipIcon = null;
    private int textOffsetY = 25;
    private int textOffsetBottom = 6;
    private int offsetTop = 10;

    private int iconOffsetTop = 8;
    private int iconOffsetRight = 8;
    private JLabel offscreenLabel;

    public PureCoolToolTipUI(final ImageIcon tooltipIcon) {
        if (tooltipIcon != null) {
            this.tooltipIcon = tooltipIcon;
            offsetTop += tooltipIcon.getIconHeight() + iconOffsetTop;
        }

        offscreenLabel = new JLabel();
        offscreenLabel.setForeground(Color.WHITE);
        offscreenLabel.setOpaque(false);

    }

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

    @Override
    public void update(final Graphics g, final JComponent c) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension componentDim = c.getSize();
        int centerX = (int) componentDim.getWidth() / 2;

        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 15, 15);

        drawBackgroundImage(g, c);

        paint(g, c);
    }

    private void drawBackgroundImage(final Graphics g, final JComponent c) {
        if (tooltipIcon != null) {

            Graphics2D g2d = (Graphics2D) g;

            BufferedImage background = new BufferedImage(c.getWidth(), c.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);

            Graphics2D backgroundGraphics = background.createGraphics();
//            backgroundGraphics.setComposite(new ColorComposite(0.5f));

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


