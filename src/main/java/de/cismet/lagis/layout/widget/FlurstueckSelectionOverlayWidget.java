/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.lagis.layout.widget;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author mbrill
 */
public class FlurstueckSelectionOverlayWidget extends Widget {

    private Color overlayColor = new Color(1, 1, 1, 0.3f);

    public FlurstueckSelectionOverlayWidget(Scene scene) {
        super(scene);
        this.setOpaque(false);
    }

    @Override
    protected void paintWidget() {
        Graphics2D g2d = this.getGraphics();

        int width = this.getPreferredBounds().width;
        int height = this.getPreferredBounds().height;

        BufferedImage overlay = new BufferedImage(width,
                height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D overlayGraphics = overlay.createGraphics();
        overlayGraphics.setPaint(overlayColor);
        overlayGraphics.drawRoundRect(0, 0, width, height, 30, 30);

        g2d.drawImage(overlay, null, 0, 0);
    }

}
