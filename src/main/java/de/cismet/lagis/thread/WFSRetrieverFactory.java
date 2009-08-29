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
import de.cismet.lagis.interfaces.DoneDelegate;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.NoWriteError;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingWorker;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.deegree2.model.feature.GMLFeatureCollectionDocument;
import org.deegree2.model.spatialschema.JTSAdapter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;



// TODO Alternative wäre Die Klasse WFS... direkt zubenutzen und über einen Configurator zu konfigurieren
//IDEE einen Pool anlegen damit es schneller geht
//sollte genereller sein
// $Name: not supported by cvs2svn $
// $Id: WFSRetrieverFactory.java,v 1.1.1.1 2009-08-29 17:26:29 spuhl Exp $
// $RCSfile: WFSRetrieverFactory.java,v $
// $Revision: 1.1.1.1 $
// $Source: /home/spuhl/cvs/maven/LagisClient/src/main/java/de/cismet/lagis/thread/WFSRetrieverFactory.java,v $
// $State: Exp $
//
//
//
//
//
//hässlich zu benutzen wegen der inneren Klasse siehe JoinActionPanel
public class WFSRetrieverFactory implements Configurable{
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    
    
    //configured via configfile
    private static Element wfsRequest;
    private static Element gemarkung;
    private static Element flur;
    private static Element flurstZaehler;
    private static Element flurstNenner;
    private static Element query;
    private static String hostname;
                
    private static WFSRetrieverFactory instance;
    
    private WFSRetrieverFactory(){
        
    }
    
    public static WFSRetrieverFactory getInstance(){
        if(instance == null){
            instance = new WFSRetrieverFactory();
        }
        return instance;
    }
    
    public SwingWorker getWFSRetriever(FlurstueckSchluessel key,DoneDelegate doneDelegate,HashMap<Integer,Boolean> properties){
        return new WFSWorkerThread(key,doneDelegate,properties);
    }
            
    public Element getConfiguration() throws NoWriteError {
        return null;
    }
    
    public void masterConfigure(Element parent) {
        try{
            wfsRequest = (Element)parent.getChild("WFSRequest").clone();
            wfsRequest.detach();
            XMLOutputter serializer = new XMLOutputter();
            serializer.setEncoding("ISO-8859-1");
            log.debug("WFSRequest: "+serializer.outputString(wfsRequest));
            log.debug("Child availaible: "+parent.getChild("WFSRequest").getChild("Query").getChild("GetFeature",Namespace.getNamespace("wfs","http://www.opengis.net/wfs")));
            query = (Element) parent.getChild("WFSRequest").getChild("Query").getChild("GetFeature",Namespace.getNamespace("wfs","http://www.opengis.net/wfs")).clone();
            List childs = query.getChild("Query",Namespace.getNamespace("wfs","http://www.opengis.net/wfs")).getChild("Filter",Namespace.getNamespace("","http://www.opengis.net/ogc")).getChild("And",Namespace.getNamespace("","http://www.opengis.net/ogc")).getChildren("PropertyIsEqualTo",Namespace.getNamespace("","http://www.opengis.net/ogc"));
            hostname = parent.getChild("WFSRequest").getChild("Hostname").getText();
            log.debug("WFSHostname: "+hostname);
            log.debug("Child list: "+childs);
            if(childs != null && childs.size() > 0){
                Iterator<Element> it = childs.iterator();
                while(it.hasNext()){
                    Element currentElement = it.next();
                    Element name = currentElement.getChild("PropertyName",Namespace.getNamespace("","http://www.opengis.net/ogc"));
                    log.debug("Name: "+name.getText());
                    if(name.getText().equals("app:gem")){
                        gemarkung = currentElement.getChild("Literal",Namespace.getNamespace("","http://www.opengis.net/ogc"));
                        log.debug("Gemarkung Literal gesetzt: "+gemarkung);
                    } else if(name.getText().equals("app:flur")){
                        flur = currentElement.getChild("Literal",Namespace.getNamespace("","http://www.opengis.net/ogc"));
                        log.debug("Flur Literal gesetzt: "+flur);
                    } else if(name.getText().equals("app:flurstz")){
                        flurstZaehler = currentElement.getChild("Literal",Namespace.getNamespace("","http://www.opengis.net/ogc"));
                        log.debug("Flur Zähler Literal gesetzt: "+flurstZaehler);
                    } else if(name.getText().equals("app:flurstn")){
                        flurstNenner = currentElement.getChild("Literal",Namespace.getNamespace("","http://www.opengis.net/ogc"));
                        log.debug("Flur Nenner Literal gesetzt: "+ flurstNenner);
                    } else {
                        log.warn("Unbekanntes Literal");
                    }
                }
            }
        }catch(Exception ex){
            log.error("Fehler bei der Konfiguration der WFSQuery/Request",ex);
        }
    }
                
    public void configure(Element parent) {
    }
    
    public class WFSWorkerThread extends ExtendedSwingWorker<Geometry,Void> {
        private FlurstueckSchluessel key;
        private DoneDelegate doneDelegate;
        private HashMap<Integer,Boolean> properties;
        //private int mode;
        private FlurstueckSchluessel flurstueckKey;
        private org.deegree2.model.feature.FeatureCollection featuresCollection;
        //private boolean hasManyVerwal
        //private boolean hasManyVerwaltungsbereiche;
        //private boolean isNoGeometryAssigned=true;
        //private boolean wasFeatureAdded;
        //private Feature currentFeature;    
        //private boolean emptyResult=false;
        
        
        public WFSWorkerThread(FlurstueckSchluessel key,DoneDelegate doneDelegate,HashMap<Integer,Boolean> properties){
            super(key);
            this.flurstueckKey = key;            
            this.doneDelegate = doneDelegate;
            this.properties = properties;
        }
        
        protected Geometry doInBackground() throws Exception {
            try{
                if(flurstueckKey ==  null){
                    log.debug("WFS retrieval unterbrochen Schlüssel null");
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
                Document doc = new Document();
                gemarkung.setText(flurstueckKey.getGemarkung().getSchluessel().toString());
                flur.setText(flurstueckKey.getFlur().toString());
                flurstZaehler.setText(flurstueckKey.getFlurstueckZaehler().toString());
                flurstNenner.setText(flurstueckKey.getFlurstueckNenner().toString());
                if(isCancelled()){
                    log.debug("doInBackground (WFSRetriever) is canceled");
                    return null;
                }
                //is this the right way
                doc.setRootElement((Element)query.clone());
                XMLOutputter out = new XMLOutputter();
                //out.setEncoding(encoding);
                String postString  = out.outputString(doc);
                log.debug("PostString für WFS :"+postString);
                HttpClient client = new HttpClient();
                String proxySet = System.getProperty("proxySet");
                if(proxySet != null && proxySet.equals("true")){
                    log.debug("proxyIs Set");
                    log.debug("ProxyHost:"+System.getProperty("http.proxyHost"));
                    log.debug("ProxyPort:"+System.getProperty("http.proxyPort"));
                    try {
                        client.getHostConfiguration().setProxy(System.getProperty("http.proxyHost"), Integer.parseInt(System.getProperty("http.proxyPort")));
                    } catch(Exception e){
                        log.error("Problem while setting proxy",e);
                    }
                }
                if(isCancelled()){
                    log.debug("doInBackground (WFSRetriever) is canceled");
                    return null;
                }
                PostMethod httppost = new PostMethod(hostname);
                httppost.setRequestEntity(new StringRequestEntity(postString));
                if(isCancelled()){
                    log.debug("doInBackground (WFSRetriever) is canceled");
                    return null;
                }
                long start = System.currentTimeMillis();
                if(isCancelled()){
                    log.debug("doInBackground (WFSRetriever) is canceled");
                    return null;
                }
                client.executeMethod(httppost);
                if(isCancelled()){
                    log.debug("doInBackground (WFSRetriever) is canceled");
                    return null;
                }
                long stop = System.currentTimeMillis();
                if(log.isEnabledFor(Priority.INFO)) log.info(((stop-start)/1000.0)+" Sekunden dauerte das getFeature Request ");
                int code = httppost.getStatusCode();
                if (code == HttpStatus.SC_OK) {
                    if(isCancelled()){
                        log.debug("doInBackground (WFSRetriever) is canceled");
                        httppost.releaseConnection();
                        return null;
                    }
                    InputStreamReader reader = new InputStreamReader(new BufferedInputStream(httppost.getResponseBodyAsStream()));
                    if(isCancelled()){
                        log.debug("doInBackground (WFSRetriever) is canceled");
                        httppost.releaseConnection();
                        return null;
                    }
                    featuresCollection = parse(reader);
                    if(isCancelled()){
                        log.debug("doInBackground (WFSRetriever) is canceled");
                        httppost.releaseConnection();
                        return null;
                    }
                    if(featuresCollection == null){
                        log.info("WFS Single Request brachte kein Ergebnis");
                        httppost.releaseConnection();
                        reader.close();
                        log.debug("FeatureCollection : "+featuresCollection);
                        //emptyResult=true;
                        return null;
                    } else {
                        httppost.releaseConnection();
                        reader.close();
                        int featureSize = featuresCollection.size();
                        if(featureSize == 0){
                            log.info("Feature Collection ist leer");
                            //TODO INFORM USER THAT THERE IS NO WFS GEOMETRY AVAILABLE
                            hadErrors=true;
                            errorMessage="Es wurden keine Geometrien zu dem angegebenen Schlüssel gefunden";
                            return null;
                        } else if(featureSize == 1){
                            log.debug("WFS Request erbrachte genau ein Ergebnis");
                            //TODO
//                        final DefaultStyledFeature feature = new DefaultStyledFeature() {
//                            public boolean isEditable() {
//                                return false;
//                            }
//
//                            public boolean canBeSelected() {
//                                return false;
//                            }
//
//                            public Paint getFillingStyle(){
//                                return new java.awt.Color(43,106,21,150);
//                            }
//                        };
//                            //TODO Duplicated code perhaps one ifblock is enough
//                            if(!hasManyVerwaltungsbereiche && isNoGeometryAssigned){
//                                log.debug("Weniger als 2 Verwaltungsbereiche & keine Geometrie zugeordnet");
//                                log.debug("Es wird eine nicht veränderbare WfsGeometrie in die Karte eingefügt");
//                                DefaultStyledFeature tmpFeature = new DefaultStyledFeature();
//                                tmpFeature.setEditable(false);
//                                tmpFeature.setCanBeSelected(false);
//                                tmpFeature.setFillingStyle(new Color(43,106,21,150));
//                                //log.fatal("FeatureCollection"+featuresCollection);
//                                //log.fatal("hoffentlich 2: "+featuresCollection.getFeature(0).getGeometryPropertyValues().length);
//                                tmpFeature.setGeometry(JTSAdapter.export(featuresCollection.getFeature(0).getDefaultGeometryPropertyValue()));
//                                
//                                if(isCancelled()){
//                                    log.debug("doInBackground (WFSRetriever) is canceled");
//                                    httppost.releaseConnection();
//                                    return null;
//                                }
//                                return tmpFeature;
//                            } else if(hasManyVerwaltungsbereiche && isNoGeometryAssigned){
//                                log.debug("Mehr als 2 Verwaltungsbereiche & keine Geometrien zugeordnet");
//                                log.debug("Es wird eine neue Geometrie zum zuordnen in die Karte eingefügt");
//                                PureNewFeature tmpFeature = new PureNewFeature(JTSAdapter.export(featuresCollection.getFeature(0).getDefaultGeometryPropertyValue()));
//                                tmpFeature.setEditable(true);
//                                tmpFeature.setCanBeSelected(true);
//                                if(isCancelled()){
//                                    log.debug("doInBackground (WFSRetriever) is canceled");
//                                    httppost.releaseConnection();
//                                    return null;
//                                }
//                                return tmpFeature;
//                            } else {
//                                log.warn("Nicht vorgesehner Fall !! --> Der Karte wird nichts hinzugefügt!");
//                                return null;
//                            }
                            Geometry geom = JTSAdapter.export(featuresCollection.getFeature(0).getDefaultGeometryPropertyValue());
                            return geom;
                        } else {
                            log.info("WFS lieferte mehr als ein Ergebnis zurück: "+featureSize);
                            hadErrors=true;
                            errorMessage="Der WFS lieferte mehrere Geometrien zurück";
                            return null;
                        }
                    }
                } else {
                    log.debug("HTTP statuscode != ok: "+code);
                    httppost.releaseConnection();
                }
            }catch(final Exception ex){
                log.error("Fehler beim abrufen der WFS Geometrie ",ex);
            }
            return null;
        }
        
        public org.deegree2.model.feature.FeatureCollection parse(InputStreamReader reader){
            try {
                log.debug("start parsing");
                long start = System.currentTimeMillis();
                //funktioniert weil nach der Methode wieder ein Cancel kommt;
                if(isCancelled()){
                    log.debug("parse() (WFSRetriever) is canceled");
                    return null;
                }
                GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument();
                if(isCancelled()){
                    log.debug("parse()(WFSRetriever) is canceled");
                    return null;
                }
                doc.load(reader,"http://dummyID");
                if(isCancelled()){
                    log.debug("parse()(WFSRetriever) is canceled");
                    return null;
                }
                log.debug("resultString :"+doc.toString());
                org.deegree2.model.feature.FeatureCollection tmp = doc.parse();
                if(isCancelled()){
                    log.debug("parse()(WFSRetriever) is canceled");
                    return null;
                }
                long stop = System.currentTimeMillis();
                log.info(((stop-start)/1000.0)+" Sekunden dauerte das parsen");
                return tmp;
            } catch (Exception e) {
                log.error("Fehler beim parsen der Features.",e);
                
            }
            return null;
        }
        
        protected void done() {
            log.debug("Job is Done calling Delegate");
            if(doneDelegate != null){
                doneDelegate.jobDone(this,properties);
            } else {
                log.warn("Delegate == null kann Job nicht beenden");
            }
        }                
    }
}