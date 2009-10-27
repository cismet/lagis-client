
package de.cismet.lagis.layout.widget;

import com.jhlabs.composite.ColorComposite;
import de.cismet.lagisEE.entity.core.Flurstueck;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import javax.swing.JToolTip;
import org.jdesktop.swingx.image.GaussianBlurFilter;

/**
 * This panel is used to render "pseudo" Flurstuecke. Pseudo Flurstuecke are
 * virtual, so they don't carry information. Therefor the Panel only draws a
 * Diamond like shape with some graphical features to make it look nifty.
 *
 * @author mbrill
 */
public class PseudoFlurstueckPanel extends AbstractFlurstueckNodePanel {

    private static final String TOOL_TIP_TEXT = "<html><b>Flurstücke wurden in einem Schritt <br>" +
            " zusammengeführt und wieder geteilt</b></html>";

    /**
     * width of the representation object
     */
    private int shapeWidth;

    /**
     * height of the representation object
     */
    private int shapeHeight;

    /**
     * Constructor takes a Flurstueck instance and creates a representation for a
     * pseudo flurstueck. This constructor initialises the representation
     * object with a default size of 40x40
     *
     * @param flurstueck Flurstueck to display
     */
    public PseudoFlurstueckPanel(Flurstueck flurstueck) {
        this(flurstueck, 40, 40);
    }

    /**
     * Constructor takes a Flurstueck instance and creates a representation for a
     * pseudo flurstueck. Additionally a size for the representation object
     * can be given
     *
     * @param flurstueck Flurstueck to display
     * @param width Width of the representation object
     * @param height Height of the representation object
     */
    public PseudoFlurstueckPanel(Flurstueck flurstueck, int width, int height) {
        super(flurstueck);
        setDoubleBuffered(false);
        setPreferredSize(new Dimension(width, height));

        shapeWidth = width - 4;
        shapeHeight = height - 4;
        setToolTipText(TOOL_TIP_TEXT);
    }

    /**
     *
     * To create custom toolTips for a specific component, this method needs to be
     * overridden. The body of this method is usually quite equal to this implementation.
     *
     * @return JToolTip the tooltip component which is displayed
     */
    @Override
    public JToolTip createToolTip() {
        JToolTip tip = new PureCoolToolTip();
        tip.setComponent(this);
        return tip;
    }

    /**
     * Paint method draws a black to white gradient on a diamond with dropshadow.
     * @param g Graphics Object
     */
    @Override
    public void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        Paint oldPaint = g2d.getPaint();

        GradientPaint grad = new GradientPaint(0, 0, Color.BLACK,
                getWidth(), getHeight(), Color.WHITE);

        BufferedImage image = new BufferedImage(getWidth(), getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D imageGraphic = image.createGraphics();

        // draw diamond with a gradient paint
        imageGraphic.setPaint(grad);

        GeneralPath gp = new GeneralPath();
        gp.moveTo(shapeWidth / 2, 0);
        gp.lineTo(shapeWidth, shapeHeight / 2);
        gp.lineTo(shapeWidth / 2, shapeHeight);
        gp.lineTo(0, shapeHeight / 2);
        gp.closePath();

        imageGraphic.fill(gp);

        BufferedImage shadow = new BufferedImage(getWidth() + 3, getHeight() + 5,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D shadowGraphics = shadow.createGraphics();
        shadowGraphics.setColor(Color.BLACK);
        shadowGraphics.setComposite(new ColorComposite(0.5f));
        shadowGraphics.fill(gp);

        GaussianBlurFilter blurFilter = new GaussianBlurFilter(3);
        shadow = blurFilter.filter(shadow, null);

        g2d.drawImage(shadow, 3, 5, this);
        g2d.drawImage(image, 0, 0, this);

    }


}
