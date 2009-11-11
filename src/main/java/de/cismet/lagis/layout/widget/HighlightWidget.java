
package de.cismet.lagis.layout.widget;

import com.jhlabs.composite.ColorComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import org.jdesktop.swingx.image.GaussianBlurFilter;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 * This widget is used to highlight an selected FlurstueckNodePanel.
 * It simply provides a paint method which draws a rounded rectangle on the
 * given graphic context with a predefined color.
 *
 * @author mbrill
 */
public class HighlightWidget extends Widget {


    /**
     * actual width of the HighlightWidget
     */
    private int width;

    /**
     * actual height of the HighlightWidget
     */
    private int height;

    /**
     * size that will be added to the size of the highlighted widget. If set >= 0
     * the highlight widget seems to shine from behind the foregroundWidget
     * (otherwise it can not be seen). This attribute is to read as the number of
     * pixels, the Highlight widget is created bigger in each direction than the
     * widget to highlight.
     */
    private int addSize = 1;

    /**
     * Prefered location of an instance of this class. This is
     * <pre>
     *      foregroundWidget.x - addSize
     *      foregroundWidget.y - addSize
     * </pre>
     */
    private Point prefLocation;

    /**
     * Overall color of an instance of this class
     */
    private Color highlightColor = new Color(0, 0, 255, 255);


    /**
     * The constructor thakes the scene and the selected widget as argument
     * and calculates the ideal size and position for this widget.
     *
     * @param scene The scene this widget is displayed in (see
     * {@link org.netbeans.api.visual.widget.Widget})
     * @param foregroundWidget The widget which was selected
     */
    public HighlightWidget(Scene scene, Widget foregroundWidget) {
        super(scene);

        Rectangle baseBounds = foregroundWidget.getBounds();
        Dimension highlightDim = new Dimension(baseBounds.width + (addSize * 2),
                baseBounds.height + (addSize * 2));
        setPreferredSize(highlightDim);

        this.width = highlightDim.width;
        this.height = highlightDim.height;

        prefLocation = foregroundWidget.getPreferredLocation();
        prefLocation.x -= addSize;
        prefLocation.y -= addSize + 2;

        this.setPreferredLocation(prefLocation);
    }

    /**
     * The paint method simply draws a filled, rounded rectangle with the size and
     * position calculated from the widget given in the constructor and applies a
     * blur filter.
     */
    @Override
    protected void paintWidget() {

        Graphics2D g2d = getGraphics();

        BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D bufferGraphics = buffer.createGraphics();

        bufferGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        bufferGraphics.setPaint(highlightColor);
        bufferGraphics.fillRoundRect(0, 0,
                width, height, 30, 30);


        g2d.drawImage(buffer, 0, 0, null);

        bufferGraphics.dispose();
        buffer.flush();

    }

}
