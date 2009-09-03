/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.layout.widget;

import com.jhlabs.composite.ColorComposite;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import org.jdesktop.swingx.image.GaussianBlurFilter;
import org.netbeans.api.visual.border.Border;

/**
 *
 * @author mbrill
 */
public class CoolBorder implements Border {

    private int thickness = 5;
    private int arcWidth = 30;
    private int arcHeight = 30;
    private Color baseColor;

    public CoolBorder(int thickness, Color baseColor) {
        this.baseColor = baseColor;
        this.thickness = thickness;
    }

    public CoolBorder(int thickness, int arcWidth, int arcHeight, Color baseColor) {
        this.thickness = thickness;
        this.arcHeight = arcHeight;
        this.arcWidth = arcWidth;
        this.baseColor = baseColor;
    }

    @Override
    public Insets getInsets() {
        return new Insets(5, 5, 5, 5);
    }

    @Override
    public void paint(Graphics2D gr, Rectangle bounds) {

        Shape innerBorderShape = new RoundRectangle2D.Double(bounds.x, bounds.y,
                bounds.width, bounds.height, arcWidth, arcHeight);

        Shape outerBorderShape = new RoundRectangle2D.Double(bounds.x -thickness,
                bounds.y - thickness, bounds.width + (2*thickness),
                bounds.height + (2*thickness), arcWidth, arcHeight);


        BufferedImage shadow = new BufferedImage(bounds.width + (2*thickness),
                bounds.height + (2*thickness), BufferedImage.TYPE_INT_ARGB);

        Graphics2D shadowGraphics = shadow.createGraphics();
        shadowGraphics.setColor(baseColor);
        shadowGraphics.setComposite(new ColorComposite(0.5f));
        shadowGraphics.draw(outerBorderShape);

        GaussianBlurFilter blurFilter = new GaussianBlurFilter(3);
        shadow = blurFilter.filter(shadow, null);

        gr.drawImage(shadow, null, bounds.x -thickness, bounds.y - thickness);
        gr.setComposite(AlphaComposite.Clear);
        gr.draw(innerBorderShape);

    }

    @Override
    public boolean isOpaque() {
        return true;
    }
}
