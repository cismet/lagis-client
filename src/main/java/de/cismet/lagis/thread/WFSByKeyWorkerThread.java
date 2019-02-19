/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.thread;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.deegree.model.spatialschema.JTSAdapter;

import org.jdom.Element;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import java.util.HashMap;

import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

import de.cismet.lagis.interfaces.DoneDelegate;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class WFSByKeyWorkerThread extends WFSWorkerThread<FlurstueckSchluesselCustomBean, Geometry> {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(WFSByKeyWorkerThread.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WFSWorkerThread object.
     *
     * @param  key           DOCUMENT ME!
     * @param  doneDelegate  DOCUMENT ME!
     * @param  properties    DOCUMENT ME!
     */
    public WFSByKeyWorkerThread(final FlurstueckSchluesselCustomBean key,
            final DoneDelegate doneDelegate,
            final HashMap<Integer, Boolean> properties) {
        super(key, doneDelegate, properties);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckSchluesselCustomBean getFlurstueckKey() {
        return getKey();
    }

    @Override
    protected Element getQuery() {
        return WFSRetrieverFactory.getInstance().getWFSQuery(getFlurstueckKey());
    }

    @Override
    protected String getHostname() {
        return WFSRetrieverFactory.getInstance().getHostname();
    }

    @Override
    protected Geometry doInBackground() throws Exception {
        try(final InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(exec()))) {
            if (isCancelled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("doInBackground (WFSRetriever) is canceled");
                }
                return null;
            }

            final org.deegree.model.feature.FeatureCollection featuresCollection = parse(reader);
            if (isCancelled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("doInBackground (WFSRetriever) is canceled");
                }
                return null;
            }
            if (featuresCollection == null) {
                LOG.info("WFS Single Request brachte kein Ergebnis");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("FeatureCollection : " + featuresCollection);
                }
                return null;
            } else {
                final int featureSize = featuresCollection.size();
                if (featureSize == 0) {
                    LOG.info("Feature Collection ist leer");
                    // TODO INFORM USER THAT THERE IS NO WFS GEOMETRY AVAILABLE
                    hadErrors = true;
                    errorMessage = "Es wurden keine Geometrien zu dem angegebenen Schlüssel gefunden";
                    return null;
                } else if (featureSize == 1) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("WFS Request erbrachte genau ein Ergebnis");
                    }

                    final Geometry geom = JTSAdapter.export(featuresCollection.getFeature(0)
                                    .getDefaultGeometryPropertyValue());
                    return geom;
                } else {
                    LOG.info("WFS lieferte mehr als ein Ergebnis zurück: " + featureSize);
                    hadErrors = true;
                    errorMessage = "Der WFS lieferte mehrere Geometrien zurück";
                    return null;
                }
            }
        } catch (final Exception ex) {
            LOG.error("Fehler beim abrufen der WFS Geometrie ", ex);
        }
        return null;
    }
}
