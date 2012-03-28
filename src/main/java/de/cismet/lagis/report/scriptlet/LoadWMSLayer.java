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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckSchluesselCustomBean;

import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;
import de.cismet.cismap.commons.retrieval.RetrievalEvent;
import de.cismet.cismap.commons.retrieval.RetrievalListener;

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

    private static final int SCALE_FACTOR = 3;

    /*
     * Wait time for image retrieval in seconds
     */
    private static final int RETRIEVAL_WAIT_TIME = 300;

    private static final int IMG_HEIGHT = 296 * SCALE_FACTOR;
    private static final int IMG_WIDTH = 542 * SCALE_FACTOR;

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
        final Condition waitForImageRetrieval = lock.newCondition();

        final BoundingBox boundingBox = new BoundingBox(bbox);
        final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(GETMAP_REQUEST));
        swms.setName("Flurstück");

        swms.setBoundingBox(boundingBox);
        swms.setSize(IMG_HEIGHT, IMG_WIDTH);

        final SignallingRetrievalListener listener = new SignallingRetrievalListener(lock, waitForImageRetrieval);
        swms.addRetrievalListener(listener);

        lock.lock();
        try {
            swms.retrieve(true);
            waitForImageRetrieval.await(RETRIEVAL_WAIT_TIME, TimeUnit.SECONDS);
        } catch (final Throwable t) {
            LOG.error("Error occurred while retrieving WMS image", t);
        } finally {
            lock.unlock();
        }

        return listener.getRetrievedImage();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public class SignallingRetrievalListener implements RetrievalListener {

        //~ Instance fields ----------------------------------------------------

        private BufferedImage image = null;
        private Lock lock;
        private Condition condition;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new SignallingRetrievalListener object.
         *
         * @param  lock       DOCUMENT ME!
         * @param  condition  DOCUMENT ME!
         */
        public SignallingRetrievalListener(final Lock lock, final Condition condition) {
            this.lock = lock;
            this.condition = condition;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void retrievalStarted(final RetrievalEvent e) {
        }

        @Override
        public void retrievalProgress(final RetrievalEvent e) {
        }

        @Override
        public void retrievalComplete(final RetrievalEvent e) {
            if (e.getRetrievedObject() instanceof Image) {
                final Image retrievedImage = (Image)e.getRetrievedObject();
                image = new BufferedImage(
                        retrievedImage.getWidth(null),
                        retrievedImage.getHeight(null),
                        BufferedImage.TYPE_INT_RGB);
                final Graphics2D g = (Graphics2D)image.getGraphics();
                g.drawImage(retrievedImage, 0, 0, null);
                g.dispose();
            }
            signalAll();
        }

        @Override
        public void retrievalAborted(final RetrievalEvent e) {
            signalAll();
        }

        @Override
        public void retrievalError(final RetrievalEvent e) {
            signalAll();
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public BufferedImage getRetrievedImage() {
            return image;
        }

        /**
         * DOCUMENT ME!
         */
        private void signalAll() {
            lock.lock();
            try {
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
}
