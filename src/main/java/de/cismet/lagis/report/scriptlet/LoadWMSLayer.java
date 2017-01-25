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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

import net.sf.jasperreports.engine.JRDefaultScriptlet;

import java.awt.Image;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

import de.cismet.cismap.commons.HeadlessMapProvider;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.wizard.GeometryWorker;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class LoadWMSLayer extends JRDefaultScriptlet {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LoadWMSLayer.class);

    private static final String GETMAP_REQUEST = "http://s10221.wuppertal-intra.de:7098/alkis/services?"
                + "&VERSION=1.1.1"
                + "&REQUEST=GetMap"
                + "&WIDTH=<cismap:width>"
                + "&HEIGHT=<cismap:height>"
                + "&BBOX=<cismap:boundingBox>"
                + "&SRS=EPSG:25832"
                + "&FORMAT=image/png"
                + "&TRANSPARENT=TRUE"
                + "&BGCOLOR=0xF0F0F0"
                + "&EXCEPTIONS=application/vnd.ogc.se_xml"
                + "&LAYERS=alkomf"
                + "&STYLES=default";

    private static final int MAP_DPI = 300;
    private static final int IMG_HEIGHT = 296;
    private static final int IMG_WIDTH = 542;

    //~ Instance fields --------------------------------------------------------

    private Polygon bbox;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    protected void retrieveData() {
        final FlurstueckCustomBean currentFlurstueck = LagisBroker.getInstance().getCurrentFlurstueck();
        final FlurstueckSchluesselCustomBean fsKey = currentFlurstueck.getFlurstueckSchluessel();

        final ArrayList<FlurstueckSchluesselCustomBean> fsList = new ArrayList<FlurstueckSchluesselCustomBean>(1);
        fsList.add(fsKey);

        final GeometryWorker worker = new GeometryWorker(fsList);
        final Map<FlurstueckSchluesselCustomBean, Geometry> result = worker.call();
        final Geometry currentGeom = result.get(fsKey);

        this.bbox = (Polygon)currentGeom.getEnvelope();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Image generateMap() {
        this.retrieveData();

        final Lock lock = new ReentrantLock();

        final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(GETMAP_REQUEST));

        final HeadlessMapProvider mapProvider = new HeadlessMapProvider();
        mapProvider.addLayer(swms);
        mapProvider.setCenterMapOnResize(true);

        final XBoundingBox boundingBox = new XBoundingBox(bbox);
        mapProvider.setBoundingBox(boundingBox);
        final Future<Image> f = mapProvider.getImage(72, MAP_DPI, IMG_WIDTH, IMG_HEIGHT);
        lock.lock();
        try {
            return f.get();
        } catch (final Exception t) {
            LOG.error("Error occurred while retrieving WMS image", t);
        } finally {
            lock.unlock();
        }
        return null;
    }
}
