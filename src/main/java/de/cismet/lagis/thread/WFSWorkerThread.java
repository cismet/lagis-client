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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.deegree.model.feature.GMLFeatureCollectionDocument;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import java.io.InputStreamReader;

import java.util.HashMap;

import de.cismet.lagis.interfaces.DoneDelegate;

/**
 * DOCUMENT ME!
 *
 * @param    <K>
 * @param    <R>
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class WFSWorkerThread<K, R> extends ExtendedSwingWorker<R, Void> {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(WFSWorkerThread.class);

    //~ Instance fields --------------------------------------------------------

    private final DoneDelegate doneDelegate;
    private final HashMap<Integer, Boolean> properties;
    private final K key;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WFSWorkerThread object.
     *
     * @param  key           DOCUMENT ME!
     * @param  doneDelegate  DOCUMENT ME!
     * @param  properties    DOCUMENT ME!
     */
    public WFSWorkerThread(final K key, final DoneDelegate doneDelegate, final HashMap<Integer, Boolean> properties) {
        super(key);
        this.key = key;
        this.doneDelegate = doneDelegate;
        this.properties = properties;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected K getKey() {
        return key;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract Element getQuery();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract String getHostname();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected byte[] exec() throws Exception {
        try {
            if (key == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("WFS retrieval unterbrochen Schlüssel null");
                }
                return null;
            }

            final Document doc = new Document();
            if (isCancelled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("doInBackground (WFSRetriever) is canceled");
                }
                return null;
            }
            doc.setRootElement(getQuery());
            final XMLOutputter out = new XMLOutputter();
            final String postString = out.outputString(doc);
            if (LOG.isDebugEnabled()) {
                LOG.debug("PostString für WFS :" + postString);
            }
            final HttpClient client = new HttpClient();
            final String proxySet = System.getProperty("proxySet");
            if ((proxySet != null) && proxySet.equals("true")) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("proxyIs Set");
                    LOG.debug("ProxyHost:" + System.getProperty("http.proxyHost"));
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ProxyPort:" + System.getProperty("http.proxyPort"));
                }
                try {
                    client.getHostConfiguration()
                            .setProxy(System.getProperty("http.proxyHost"),
                                Integer.parseInt(System.getProperty("http.proxyPort")));
                } catch (final Exception e) {
                    LOG.error("Problem while setting proxy", e);
                }
            }
            if (isCancelled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("doInBackground (WFSRetriever) is canceled");
                }
                return null;
            }
            final PostMethod httppost = new PostMethod(getHostname());
            httppost.setRequestEntity(new StringRequestEntity(postString));
            if (isCancelled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("doInBackground (WFSRetriever) is canceled");
                }
                return null;
            }
            final long start = System.currentTimeMillis();
            if (isCancelled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("doInBackground (WFSRetriever) is canceled");
                }
                return null;
            }

            LOG.info("doInBackground: start communication with host " + httppost.getHostConfiguration());

            client.executeMethod(httppost);

            if (isCancelled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("doInBackground (WFSRetriever) is canceled");
                }
                return null;
            }
            final long stop = System.currentTimeMillis();
            if (LOG.isEnabledFor(Priority.INFO)) {
                LOG.info(((stop - start) / 1000.0) + " Sekunden dauerte das getFeature Request ");
            }
            final int code = httppost.getStatusCode();
            if (code == HttpStatus.SC_OK) {
                if (isCancelled()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("doInBackground (WFSRetriever) is canceled");
                    }
                    httppost.releaseConnection();
                    return null;
                }
                return IOUtils.toByteArray(httppost.getResponseBodyAsStream());
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("HTTP statuscode != ok: " + code);
                }
                httppost.releaseConnection();
            }
        } catch (final Exception ex) {
            LOG.error("Fehler beim abrufen der WFS Geometrie ", ex);
        }
        return null;
    }

    @Override
    protected void done() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Job is Done calling Delegate");
        }
        if (doneDelegate != null) {
            doneDelegate.jobDone(this, properties);
        } else {
            LOG.warn("Delegate == null kann Job nicht beenden");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   reader  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public org.deegree.model.feature.FeatureCollection parse(final InputStreamReader reader) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("start parsing");
            }
            final long start = System.currentTimeMillis();
            // funktioniert weil nach der Methode wieder ein Cancel kommt;
            if (isCancelled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("parse() (WFSRetriever) is canceled");
                }
                return null;
            }
            final GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument();
            if (isCancelled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("parse()(WFSRetriever) is canceled");
                }
                return null;
            }
            doc.load(reader, "http://dummyID");
            if (isCancelled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("parse()(WFSRetriever) is canceled");
                }
                return null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("resultString :" + doc.toString());
            }
            final org.deegree.model.feature.FeatureCollection tmp = doc.parse();
            if (isCancelled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("parse()(WFSRetriever) is canceled");
                }
                return null;
            }
            final long stop = System.currentTimeMillis();
            LOG.info(((stop - start) / 1000.0) + " Sekunden dauerte das parsen");
            return tmp;
        } catch (final Exception e) {
            LOG.error("Fehler beim parsen der Features.", e);
        }
        return null;
    }
}
