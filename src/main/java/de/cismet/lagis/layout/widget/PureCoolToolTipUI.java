/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.layout.widget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ToolTipUI;

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

    private Color backgroundColor = new Color(0, 0, 0, 80);
    private Color colorGlossy = new Color(255, 255, 255, 255);
    private Image tooltipIcon = null;
    private int height = 5;
    private int width = 5;
    int textStartY = 20;
    int textOffset = 3;

    public PureCoolToolTipUI(final ImageIcon tooltipIcon) {
        if (tooltipIcon != null) {
            this.tooltipIcon = tooltipIcon.getImage();
        }
        
    }

    @Override
    public void paint(final Graphics g, final JComponent c) {


        String tipText = ((JToolTip) c).getTipText();
        final FontMetrics metrics = c.getFontMetrics(c.getFont());
        int metricsHeight = metrics.getHeight();
        int textHeight = 0;

        if (tipText == null) {
            tipText = "";
        }

        String[] splitTipText = tipText.split("<\\s*/?\\s*br\\s*/?\\s*>");

        g.setColor(c.getForeground());

        if (tooltipIcon != null) {

            textHeight += tooltipIcon.getHeight(c) + textStartY;

            for (int i = 0; i < splitTipText.length; i++) {

                String current = splitTipText[i];
                current.trim();
                if (current.startsWith("<html>")) {
                    current = current.substring(current.indexOf("<html>") + 6);
                    splitTipText[i] = current;
                }

                if (current.endsWith("</html>")) {
                    current = current.substring(0, current.indexOf("</html>"));
                    splitTipText[i] = current;
                }

                if (splitTipText[i].matches(".*<\\s*b\\s*>.*") &&
                        splitTipText[i].matches(".*<\\s*/?\\s*b\\s*>.*")) {

                    int boldStart = splitTipText[i].indexOf("<b>");
                    int boldEnd = splitTipText[i].indexOf("</b>");
                    String boldText = splitTipText[i].substring(boldStart + 3, boldEnd);
                    String beforeBold = splitTipText[i].substring(0, boldStart);
                    String afterBold = splitTipText[i].substring(boldEnd + 4, splitTipText[i].length());

                    int lineSize = 3;

                    g.drawString(beforeBold, lineSize, textHeight);

                    lineSize += SwingUtilities.computeStringWidth(metrics, beforeBold) + 2;
                    Font currentFont = g.getFont();
                    g.setFont(new Font(currentFont.getName(), Font.BOLD, currentFont.getSize()));
                    g.drawString(boldText, lineSize, textHeight);

                    lineSize += SwingUtilities.computeStringWidth(metrics, boldText) + 8;
                    g.setFont(currentFont);
                    g.drawString(afterBold, lineSize, textHeight);

                    lineSize += SwingUtilities.computeStringWidth(metrics, afterBold);

                    width = lineSize;

                } else {

                    g.drawString(splitTipText[i], 3, textHeight);

                    int lineWidth = SwingUtilities.computeStringWidth(metrics, splitTipText[i]);
                    if (lineWidth > width) {
                        width = lineWidth;
                    }

                }

                textHeight += metricsHeight + 3;
            }

            g.drawImage(tooltipIcon, width - tooltipIcon.getWidth(c) - 5, 5, c);

        } else {
            textHeight += 3;

            for (int i = 0; i < splitTipText.length; i++) {

                String current = splitTipText[i];
                current.trim();
                if (current.startsWith("<html>")) {
                    current = current.substring(current.indexOf("<html>") + 6);
                    splitTipText[i] = current;
                }

                if (current.endsWith("</html>")) {
                    current = current.substring(0, current.indexOf("</html>"));
                    splitTipText[i] = current;
                }

                g.drawString(splitTipText[i], 3, textHeight);
                textHeight += metricsHeight + 3;

                int lineWidth = SwingUtilities.computeStringWidth(metrics, splitTipText[i]);

                if (lineWidth > width) {
                    width = lineWidth;
                }
            }
        }

        height = textHeight;
    }

    /**
     * @see javax.swing.plaf.ComponentUI#getPreferredSize(javax.swing.JComponent)
     */
    @Override
    public Dimension getPreferredSize(final JComponent c) {
        final FontMetrics metrics = c.getFontMetrics(c.getFont());
        String tipText = ((JToolTip) c).getTipText();
        String[] splitTipText = tipText.split("<\\s*/?\\s*br\\s*/?\\s*>");

        if (splitTipText == null) {
            splitTipText = new String[1];
            splitTipText[0] = "";
        }

        if (tooltipIcon != null) {
            height = textStartY + tooltipIcon.getHeight(c);
            width = tooltipIcon.getWidth(c);
        }

        for (int i = 0; i < splitTipText.length; i++) {

            String current = splitTipText[i];
            current.trim();
            if (current.startsWith("<html>")) {
                current = current.substring(current.indexOf("<html>") + 6);
                splitTipText[i] = current;
            }

            if (current.endsWith("</html>")) {
                current = current.substring(0, current.indexOf("</html>"));
                splitTipText[i] = current;
            }

            int lineWidth = SwingUtilities.computeStringWidth(metrics, splitTipText[i]);

            if (splitTipText[i].matches(".*<\\s*b\\s*>.*") &&
                    splitTipText[i].matches(".*<\\s*/?\\s*b\\s*>.*")) {
                lineWidth -= 40;
            }

            if (lineWidth > width) {
                width = lineWidth;
            }

            height += metrics.getHeight() + textOffset;
        }

        width += 5;

        return new Dimension(width, height);
    }
}


