/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.lagis.layout.widget;

import com.jhlabs.composite.ColorComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import org.jdesktop.swingx.image.GaussianBlurFilter;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author mbrill
 */
public class HighlightWidget extends Widget {

    Widget foregroundWidget;
    int width;
    int height;
    int addSize = 20;
    Point prefLocation;
//    Color highlightColor = new Color(133, 218, 221, 255);
    Color highlightColor = new Color(5, 5, 255, 80);


    public HighlightWidget(Scene scene, Widget foregroundWidget) {
        super(scene);

        this.foregroundWidget = foregroundWidget;
        Rectangle baseBounds = foregroundWidget.getBounds();
        Dimension highlightDim = new Dimension(baseBounds.width + (addSize * 2),
                baseBounds.height + (addSize * 2));
        setPreferredSize(highlightDim);

        this.width = highlightDim.width;
        this.height = highlightDim.height;

        prefLocation = foregroundWidget.getPreferredLocation();
        prefLocation.x -= addSize;
        prefLocation.y -= addSize;

        this.setPreferredLocation(prefLocation);
        System.out.println(baseBounds);
        System.out.println(getPreferredLocation());
    }

    @Override
    protected void paintWidget() {

        Graphics2D g2d = getGraphics();

        BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D bufferGraphics = buffer.createGraphics();

        bufferGraphics.setPaint(highlightColor);
        bufferGraphics.fillRoundRect(12, 10,
                width - (addSize + 5), height - (addSize + 5), 30, 30);

        bufferGraphics.setComposite(new ColorComposite(0.5f));

        GaussianBlurFilter blurFilter = new GaussianBlurFilter(8);
        buffer = blurFilter.filter(buffer, null);

        g2d.drawImage(buffer, 0, 0, null);

        bufferGraphics.dispose();
        buffer.flush();

    }

}
