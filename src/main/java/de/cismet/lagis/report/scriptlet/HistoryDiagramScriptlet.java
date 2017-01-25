/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2010 jweintraut
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.lagis.report.scriptlet;

import net.sf.jasperreports.engine.JRDefaultScriptlet;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

import java.util.Hashtable;

import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;

import de.cismet.lagis.broker.LagisBroker;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class HistoryDiagramScriptlet extends JRDefaultScriptlet {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            HistoryDiagramScriptlet.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new HistoryDiagramScriptlet object.
     */
    public HistoryDiagramScriptlet() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void doURLLayout() {
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Image loadHistoryImage() {
        final RenderedImage img = LagisBroker.getInstance().getHistoryImage();
        if (img instanceof BufferedImage) {
            return (BufferedImage)img;
        }
        final ColorModel cm = img.getColorModel();
        final int width = img.getWidth();
        final int height = img.getHeight();
        final WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
        final boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        final Hashtable<String, Object> properties = new Hashtable<String, Object>();
        final String[] keys = img.getPropertyNames();
        if (keys != null) {
            for (int i = 0; i < keys.length; i++) {
                properties.put(keys[i], img.getProperty(keys[i]));
            }
        }
        final BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);

        img.copyData(raster);
        return result;
    }
}
