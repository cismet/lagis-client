/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * WFSRetriever.java
 *
 * Created on 10. Januar 2008, 10:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.thread;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.deegree.model.feature.GMLFeatureCollectionDocument;
import org.deegree.model.spatialschema.JTSAdapter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingWorker;

import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

import de.cismet.lagis.interfaces.DoneDelegate;

import de.cismet.tools.configuration.Configurable;
/**
 * TODO Alternative wäre Die Klasse WFS... direkt zubenutzen und über einen Configurator zu konfigurieren IDEE einen
 * Pool anlegen damit es schneller geht sollte genereller sein $Name: not supported by cvs2svn $ $Id:
 * WFSRetrieverFactory.java,v 1.1.1.1 2009-08-29 17:26:29 spuhl Exp $ $RCSfile: WFSRetrieverFactory.java,v $ $Revision:
 * 1.1.1.1 $ $Source: /home/spuhl/cvs/maven/LagisClient/src/main/java/de/cismet/lagis/thread/WFSRetrieverFactory.java,v
 * $ $State: Exp $ hässlich zu benutzen wegen der inneren Klasse siehe JoinActionPanel
 *
 * @version  $Revision$, $Date$
 */
public class WFSRetrieverFactory implements Configurable {

    //~ Static fields/initializers ---------------------------------------------

    // configured via configfile
    private static Element wfsRequest;
    private static Element gemarkung;
    private static Element flur;
    private static Element flurstZaehler;
    private static Element flurstNenner;
    private static Element query;
    private static String hostname;
    private static WFSRetrieverFactory instance;

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WFSRetrieverFactory object.
     */
    private WFSRetrieverFactory() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static WFSRetrieverFactory getInstance() {
        if (instance == null) {
            instance = new WFSRetrieverFactory();
        }
        return instance;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key           DOCUMENT ME!
     * @param   doneDelegate  DOCUMENT ME!
     * @param   properties    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SwingWorker getWFSRetriever(final FlurstueckSchluesselCustomBean key,
            final DoneDelegate doneDelegate,
            final HashMap<Integer, Boolean> properties) {
        return new WFSWorkerThread(key, doneDelegate, properties);
    }

    @Override
    public Element getConfiguration() {
        return null;
    }

    @Override
    public void masterConfigure(final Element parent) {
        try {
            wfsRequest = (Element)parent.getChild("WFSRequest").clone();
            wfsRequest.detach();
            final Format format = Format.getPrettyFormat();
            // TODO: WHY NOT USING UTF-8
            format.setEncoding("ISO-8859-1");
            final XMLOutputter serializer = new XMLOutputter(format);
            if (log.isDebugEnabled()) {
                log.debug("WFSRequest: " + serializer.outputString(wfsRequest));
            }
            if (log.isDebugEnabled()) {
                log.debug("Child availaible: "
                            + parent.getChild("WFSRequest").getChild("Query").getChild(
                                "GetFeature",
                                Namespace.getNamespace("wfs", "http://www.opengis.net/wfs")));
            }
            query = (Element)parent.getChild("WFSRequest").getChild("Query")
                        .getChild("GetFeature", Namespace.getNamespace("wfs", "http://www.opengis.net/wfs"))
                        .clone();
            final List childs = query.getChild("Query", Namespace.getNamespace("wfs", "http://www.opengis.net/wfs"))
                        .getChild("Filter", Namespace.getNamespace("", "http://www.opengis.net/ogc"))
                        .getChild("And", Namespace.getNamespace("", "http://www.opengis.net/ogc"))
                        .getChildren("PropertyIsEqualTo", Namespace.getNamespace("", "http://www.opengis.net/ogc"));
            hostname = parent.getChild("WFSRequest").getChild("Hostname").getText();
            if (log.isDebugEnabled()) {
                log.debug("WFSHostname: " + hostname);
                log.debug("Child list: " + childs);
            }
            if ((childs != null) && (childs.size() > 0)) {
                final Iterator<Element> it = childs.iterator();
                while (it.hasNext()) {
                    final Element currentElement = it.next();
                    final Element name = currentElement.getChild(
                            "PropertyName",
                            Namespace.getNamespace("", "http://www.opengis.net/ogc"));
                    if (log.isDebugEnabled()) {
                        log.debug("Name: " + name.getText());
                    }
                    if (name.getText().equals("app:gem")) {
                        gemarkung = currentElement.getChild(
                                "Literal",
                                Namespace.getNamespace("", "http://www.opengis.net/ogc"));
                        if (log.isDebugEnabled()) {
                            log.debug("Gemarkung Literal gesetzt: " + gemarkung);
                        }
                    } else if (name.getText().equals("app:flur")) {
                        flur = currentElement.getChild(
                                "Literal",
                                Namespace.getNamespace("", "http://www.opengis.net/ogc"));
                        if (log.isDebugEnabled()) {
                            log.debug("Flur Literal gesetzt: " + flur);
                        }
                    } else if (name.getText().equals("app:flurstz")) {
                        flurstZaehler = currentElement.getChild(
                                "Literal",
                                Namespace.getNamespace("", "http://www.opengis.net/ogc"));
                        if (log.isDebugEnabled()) {
                            log.debug("Flur Zähler Literal gesetzt: " + flurstZaehler);
                        }
                    } else if (name.getText().equals("app:flurstn")) {
                        flurstNenner = currentElement.getChild(
                                "Literal",
                                Namespace.getNamespace("", "http://www.opengis.net/ogc"));
                        if (log.isDebugEnabled()) {
                            log.debug("Flur Nenner Literal gesetzt: " + flurstNenner);
                        }
                    } else {
                        log.warn("Unbekanntes Literal");
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Fehler bei der Konfiguration der WFSQuery/Request", ex);
        }
    }

    @Override
    public void configure(final Element parent) {
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public class WFSWorkerThread extends ExtendedSwingWorker<Geometry, Void> {

        //~ Instance fields ----------------------------------------------------

        private DoneDelegate doneDelegate;
        private HashMap<Integer, Boolean> properties;
        // private int mode;
        private FlurstueckSchluesselCustomBean flurstueckKey;
        private org.deegree.model.feature.FeatureCollection featuresCollection;
        // private boolean hasManyVerwal private boolean hasManyVerwaltungsbereiche; private boolean
        // isNoGeometryAssigned=true; private boolean wasFeatureAdded; private Feature currentFeature; private boolean
        // emptyResult=false;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new WFSWorkerThread object.
         *
         * @param  key           DOCUMENT ME!
         * @param  doneDelegate  DOCUMENT ME!
         * @param  properties    DOCUMENT ME!
         */
        public WFSWorkerThread(final FlurstueckSchluesselCustomBean key,
                final DoneDelegate doneDelegate,
                final HashMap<Integer, Boolean> properties) {
            super(key);
            this.flurstueckKey = key;
            this.doneDelegate = doneDelegate;
            this.properties = properties;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public FlurstueckSchluesselCustomBean getFlurstueckKey() {
            return flurstueckKey;
        }

        @Override
        protected Geometry doInBackground() throws Exception {
            try {
                if (flurstueckKey == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("WFS retrieval unterbrochen Schlüssel null");
                    }
                    return null;
                }
//                if(!hasManyVerwaltungsbereiche && !isNoGeometryAssigned){
//                    log.warn("Weniger als 2 Verwaltungsbereiche & Geometrie zugeordnet --> darf an dieser Stelle nicht vorkommen");
//                    log.debug("Es wird keine Geometrie in die Karte eingefügt");
//                    return null;
//                } else if(hasManyVerwaltungsbereiche && !isNoGeometryAssigned){
//                    log.warn("Mehr als 2 Verwaltungsbereiche & keine Geometrien zugeordnet --> darf an dieser Stelle nicht vorkommen");
//                    log.debug("Es wird keine Geometrie in die Karte eingefügt");
//                    return null;
//                }
                final Document doc = new Document();
                gemarkung.setText(flurstueckKey.getGemarkung().getSchluessel().toString());
                flur.setText(flurstueckKey.getFlur().toString());
                flurstZaehler.setText(flurstueckKey.getFlurstueckZaehler().toString());
                flurstNenner.setText(flurstueckKey.getFlurstueckNenner().toString());
                if (isCancelled()) {
                    if (log.isDebugEnabled()) {
                        log.debug("doInBackground (WFSRetriever) is canceled");
                    }
                    return null;
                }
                // is this the right way
                doc.setRootElement((Element)query.clone());
                final XMLOutputter out = new XMLOutputter();
                // out.setEncoding(encoding);
                final String postString = out.outputString(doc);
                if (log.isDebugEnabled()) {
                    log.debug("PostString für WFS :" + postString);
                }
                final HttpClient client = new HttpClient();
                final String proxySet = System.getProperty("proxySet");
                if ((proxySet != null) && proxySet.equals("true")) {
                    if (log.isDebugEnabled()) {
                        log.debug("proxyIs Set");
                        log.debug("ProxyHost:" + System.getProperty("http.proxyHost"));
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("ProxyPort:" + System.getProperty("http.proxyPort"));
                    }
                    try {
                        client.getHostConfiguration()
                                .setProxy(System.getProperty("http.proxyHost"),
                                    Integer.parseInt(System.getProperty("http.proxyPort")));
                    } catch (Exception e) {
                        log.error("Problem while setting proxy", e);
                    }
                }
                if (isCancelled()) {
                    if (log.isDebugEnabled()) {
                        log.debug("doInBackground (WFSRetriever) is canceled");
                    }
                    return null;
                }
                final PostMethod httppost = new PostMethod(hostname);
                httppost.setRequestEntity(new StringRequestEntity(postString));
                if (isCancelled()) {
                    if (log.isDebugEnabled()) {
                        log.debug("doInBackground (WFSRetriever) is canceled");
                    }
                    return null;
                }
                final long start = System.currentTimeMillis();
                if (isCancelled()) {
                    if (log.isDebugEnabled()) {
                        log.debug("doInBackground (WFSRetriever) is canceled");
                    }
                    return null;
                }

                log.info("doInBackground: start communication with host " + httppost.getHostConfiguration());

                client.executeMethod(httppost);

                if (isCancelled()) {
                    if (log.isDebugEnabled()) {
                        log.debug("doInBackground (WFSRetriever) is canceled");
                    }
                    return null;
                }
                final long stop = System.currentTimeMillis();
                if (log.isEnabledFor(Priority.INFO)) {
                    log.info(((stop - start) / 1000.0) + " Sekunden dauerte das getFeature Request ");
                }
                final int code = httppost.getStatusCode();
                if (code == HttpStatus.SC_OK) {
                    if (isCancelled()) {
                        if (log.isDebugEnabled()) {
                            log.debug("doInBackground (WFSRetriever) is canceled");
                        }
                        httppost.releaseConnection();
                        return null;
                    }
                    final InputStreamReader reader = new InputStreamReader(new BufferedInputStream(
                                httppost.getResponseBodyAsStream()));
                    if (isCancelled()) {
                        if (log.isDebugEnabled()) {
                            log.debug("doInBackground (WFSRetriever) is canceled");
                        }
                        httppost.releaseConnection();
                        return null;
                    }
                    featuresCollection = parse(reader);
                    if (isCancelled()) {
                        if (log.isDebugEnabled()) {
                            log.debug("doInBackground (WFSRetriever) is canceled");
                        }
                        httppost.releaseConnection();
                        return null;
                    }
                    if (featuresCollection == null) {
                        log.info("WFS Single Request brachte kein Ergebnis");
                        httppost.releaseConnection();
                        reader.close();
                        if (log.isDebugEnabled()) {
                            log.debug("FeatureCollection : " + featuresCollection);
                        }
                        // emptyResult=true;
                        return null;
                    } else {
                        httppost.releaseConnection();
                        reader.close();
                        final int featureSize = featuresCollection.size();
                        if (featureSize == 0) {
                            log.info("Feature Collection ist leer");
                            // TODO INFORM USER THAT THERE IS NO WFS GEOMETRY AVAILABLE
                            hadErrors = true;
                            errorMessage = "Es wurden keine Geometrien zu dem angegebenen Schlüssel gefunden";
                            return null;
                        } else if (featureSize == 1) {
                            if (log.isDebugEnabled()) {
                                log.debug("WFS Request erbrachte genau ein Ergebnis");
                            }
                            // TODO final DefaultStyledFeature feature = new DefaultStyledFeature() { public boolean
                            // isEditable() { return false; }
                            //
                            // public boolean canBeSelected() { return false; }
                            //
                            // public Paint getFillingStyle(){ return new java.awt.Color(43,106,21,150); } }; //TODO
                            // Duplicated code perhaps one ifblock is enough if(!hasManyVerwaltungsbereiche &&
                            // isNoGeometryAssigned){ log.debug("Weniger als 2 Verwaltungsbereiche & keine Geometrie
                            // zugeordnet"); log.debug("Es wird eine nicht veränderbare WfsGeometrie in die Karte
                            // eingefügt"); DefaultStyledFeature tmpFeature = new DefaultStyledFeature();
                            // tmpFeature.setEditable(false); tmpFeature.setCanBeSelected(false);
                            // tmpFeature.setFillingStyle(new Color(43,106,21,150));
                            // //log.fatal("FeatureCollection"+featuresCollection); //log.fatal("hoffentlich 2:
                            // "+featuresCollection.getFeature(0).getGeometryPropertyValues().length);
                            // tmpFeature.setGeometry(JTSAdapter.export(featuresCollection.getFeature(0).getDefaultGeometryPropertyValue()));
                            // if(isCancelled()){ log.debug("doInBackground (WFSRetriever) is canceled");
                            // httppost.releaseConnection(); return null; } return tmpFeature; } else
                            // if(hasManyVerwaltungsbereiche && isNoGeometryAssigned){ log.debug("Mehr als 2
                            // Verwaltungsbereiche & keine Geometrien zugeordnet"); log.debug("Es wird eine neue
                            // Geometrie zum zuordnen in die Karte eingefügt"); PureNewFeature tmpFeature = new
                            // PureNewFeature(JTSAdapter.export(featuresCollection.getFeature(0).getDefaultGeometryPropertyValue()));
                            // tmpFeature.setEditable(true); tmpFeature.setCanBeSelected(true); if(isCancelled()){
                            // log.debug("doInBackground (WFSRetriever) is canceled"); httppost.releaseConnection();
                            // return null; } return tmpFeature; } else { log.warn("Nicht vorgesehner Fall !! --> Der
                            // Karte wird nichts hinzugefügt!"); return null; }
                            final Geometry geom = JTSAdapter.export(featuresCollection.getFeature(0)
                                            .getDefaultGeometryPropertyValue());
                            return geom;
                        } else {
                            log.info("WFS lieferte mehr als ein Ergebnis zurück: " + featureSize);
                            hadErrors = true;
                            errorMessage = "Der WFS lieferte mehrere Geometrien zurück";
                            return null;
                        }
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("HTTP statuscode != ok: " + code);
                    }
                    httppost.releaseConnection();
                }
            } catch (final Exception ex) {
                log.error("Fehler beim abrufen der WFS Geometrie ", ex);
            }
            return null;
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
                if (log.isDebugEnabled()) {
                    log.debug("start parsing");
                }
                final long start = System.currentTimeMillis();
                // funktioniert weil nach der Methode wieder ein Cancel kommt;
                if (isCancelled()) {
                    if (log.isDebugEnabled()) {
                        log.debug("parse() (WFSRetriever) is canceled");
                    }
                    return null;
                }
                final GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument();
                if (isCancelled()) {
                    if (log.isDebugEnabled()) {
                        log.debug("parse()(WFSRetriever) is canceled");
                    }
                    return null;
                }
                doc.load(reader, "http://dummyID");
                if (isCancelled()) {
                    if (log.isDebugEnabled()) {
                        log.debug("parse()(WFSRetriever) is canceled");
                    }
                    return null;
                }
                if (log.isDebugEnabled()) {
                    log.debug("resultString :" + doc.toString());
                }
                final org.deegree.model.feature.FeatureCollection tmp = doc.parse();
                if (isCancelled()) {
                    if (log.isDebugEnabled()) {
                        log.debug("parse()(WFSRetriever) is canceled");
                    }
                    return null;
                }
                final long stop = System.currentTimeMillis();
                log.info(((stop - start) / 1000.0) + " Sekunden dauerte das parsen");
                return tmp;
            } catch (Exception e) {
                log.error("Fehler beim parsen der Features.", e);
            }
            return null;
        }

        @Override
        protected void done() {
            if (log.isDebugEnabled()) {
                log.debug("Job is Done calling Delegate");
            }
            if (doneDelegate != null) {
                doneDelegate.jobDone(this, properties);
            } else {
                log.warn("Delegate == null kann Job nicht beenden");
            }
        }
    }
}
