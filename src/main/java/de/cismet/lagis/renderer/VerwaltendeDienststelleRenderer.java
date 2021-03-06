/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * VerwaltendeDienststelleRenderer.java
 *
 * Created on 15. Mai 2007, 11:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.renderer;

import org.apache.log4j.Logger;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import java.util.Collection;

import javax.swing.Icon;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import de.cismet.cids.custom.beans.lagis.FarbeCustomBean;
import de.cismet.cids.custom.beans.lagis.VerwaltendeDienststelleCustomBean;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class VerwaltendeDienststelleRenderer extends DefaultTableCellRenderer {

    //~ Instance fields --------------------------------------------------------

    Element htmlTooltip;
    private XMLOutputter serializer = new XMLOutputter();
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
//    private static final Icon VIEW_ICON = new Icon() {
//
//        public int getIconHeight() {
//            return ICON_SIZE;
//        }
//
//        public int getIconWidth() {
//            return ICON_SIZE;
//        }
//
//        public void paintIcon(Component c, Graphics g, int x, int y) {
//            Color oldColor = g.getColor();
//
//            g.setColor(new Color(70, 70, 70));
//            g.fillRect(x, y, ICON_SIZE, ICON_SIZE);
//
//            g.setColor(new Color(100, 230, 100));
//            g.fillRect(x + 1, y + 1, ICON_SIZE - 2, ICON_SIZE - 2);
//
//            g.setColor(oldColor);
//        }
//    };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of VerwaltendeDienststelleRenderer.
     */
    public VerwaltendeDienststelleRenderer() {
        super();
        final Format format = Format.getPrettyFormat();
        // TODO: WHY NOT USING UTF-8
        format.setEncoding("ISO-8859-1");
        serializer.setFormat(format);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  htmlTooltip  DOCUMENT ME!
     */
    public void setHTMLTooltip(final Element htmlTooltip) {
        if (log.isDebugEnabled()) {
            log.debug("htmltooltipset:" + htmlTooltip);
        }
        this.htmlTooltip = htmlTooltip;
    }

    @Override
    public void setValue(final Object value) {
        try {
            if (value instanceof VerwaltendeDienststelleCustomBean) {
                final VerwaltendeDienststelleCustomBean tmp = (VerwaltendeDienststelleCustomBean)value;
                final Collection<FarbeCustomBean> farben;
                final FarbeCustomBean farbe;
                if (tmp != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("VerwaltendeDienststelleCustomBean != null");
                    }
                    if (((farben = tmp.getFarben()) != null) && !farben.isEmpty()) {
                        farbe = farben.iterator().next();
                        setIcon(new ColorIcon(new Color(farbe.getRgbFarbwert())));
                    } else {
                        setIcon(new ColorIcon(Color.black));
                    }

                    setText((value == null) ? "" : value.toString());
                    setIconTextGap(5);
                    setBorder(new EmptyBorder(0, 5, 0, 0));

                    try {
                        final Element html = htmlTooltip.getChild("HTML");
                        html.getChild("Abteilung").setText(tmp.getAbkuerzungAbteilung());
                        html.getChild("Ressort").setText(tmp.getRessort().getAbkuerzung());
                        final String htmlString = serializer.outputString(html);
                        if (log.isDebugEnabled()) {
                            log.debug("setting htmltooltip: " + htmlString);
                        }
                        setToolTipText(htmlString);
                    } catch (Exception silent) {
                        setToolTipText("Objektnutzung");
                    }
                } else {
                    setIcon(new ColorIcon(Color.black));
                    setIconTextGap(5);
                    setBorder(new EmptyBorder(0, 5, 0, 0));
                    setText("");
                    setToolTipText("Objektnutzung");
                }
            } else if (value == null) {
                setIcon(null);
                setText("");
                setBorder(null);
                setToolTipText("Objektnutzung");
            }
        } catch (Exception ex) {
            log.warn("Fehler beim Rendern einer VerwaltendenDienststelle", ex);
            setIcon(new ColorIcon(Color.black));
            setIconTextGap(5);
            setBorder(new EmptyBorder(0, 5, 0, 0));
            setText((value == null) ? "" : value.toString());
            setToolTipText("Objektnutzung");
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class ColorIcon implements Icon {

        //~ Static fields/initializers -----------------------------------------

        private static final int ICON_SIZE = 8;

        //~ Instance fields ----------------------------------------------------

        private Color color;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ColorIcon object.
         *
         * @param  color  DOCUMENT ME!
         */
        public ColorIcon(final Color color) {
            this.color = color;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public int getIconWidth() {
            return ICON_SIZE;
        }

        @Override
        public int getIconHeight() {
            return ICON_SIZE;
        }

        @Override
        public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
            final Color oldColor = g.getColor();

            g.setColor(new Color(70, 70, 70));
            g.fillRect(x, y, ICON_SIZE, ICON_SIZE);

            g.setColor(color);
            g.fillRect(x + 1, y + 1, ICON_SIZE - 2, ICON_SIZE - 2);
            g.setColor(oldColor);
        }
    }
}
