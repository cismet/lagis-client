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

import org.apache.log4j.Logger;

import org.jdom.Element;
import org.jdom.Namespace;

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

    private static WFSRetrieverFactory INSTANCE;

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(WFSRetrieverFactory.class);
    private static final Namespace OGC_NAMESPACE = Namespace.getNamespace("", "http://www.opengis.net/ogc");
    private static final Namespace WFS_NAMESPACE = Namespace.getNamespace("wfs", "http://www.opengis.net/wfs");

    //~ Instance fields --------------------------------------------------------

    // configured via configfile
    private Element gemarkung;
    private Element flur;
    private Element flurstZaehler;
    private Element flurstNenner;
    private Element wfsQuery;
    private String hostname;

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
        if (INSTANCE == null) {
            INSTANCE = new WFSRetrieverFactory();
        }
        return INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getHostname() {
        return hostname;
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
        return new WFSByKeyWorkerThread(key, doneDelegate, properties);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Element getWFSQuery(final FlurstueckSchluesselCustomBean key) {
        gemarkung.setText(key.getGemarkung().getSchluessel().toString());
        flur.setText(key.getFlur().toString());
        flurstZaehler.setText(key.getFlurstueckZaehler().toString());
        flurstNenner.setText(key.getFlurstueckNenner().toString());
        return (Element)wfsQuery.clone();
    }

    @Override
    public Element getConfiguration() {
        return null;
    }

    @Override
    public void masterConfigure(final Element parent) {
        try {
            hostname = parent.getChild("WFSRequest").getChild("Hostname").getText();
            wfsQuery = (Element)parent.getChild("WFSRequest").getChild("Query").getChild("GetFeature", WFS_NAMESPACE)
                        .clone();
            final List byKeyChilds = wfsQuery.getChild("Query", WFS_NAMESPACE)
                        .getChild("Filter", OGC_NAMESPACE)
                        .getChild("And", OGC_NAMESPACE)
                        .getChildren("PropertyIsEqualTo", OGC_NAMESPACE);
            if ((byKeyChilds != null) && (byKeyChilds.size() > 0)) {
                final Iterator<Element> it = byKeyChilds.iterator();
                while (it.hasNext()) {
                    final Element currentElement = it.next();
                    final Element name = currentElement.getChild("PropertyName", OGC_NAMESPACE);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Name: " + name.getText());
                    }
                    if (name.getText().equals("app:gem")) {
                        gemarkung = currentElement.getChild("Literal", OGC_NAMESPACE);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Gemarkung Literal gesetzt: " + gemarkung);
                        }
                    } else if (name.getText().equals("app:flur")) {
                        flur = currentElement.getChild("Literal", OGC_NAMESPACE);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Flur Literal gesetzt: " + flur);
                        }
                    } else if (name.getText().equals("app:flurstz")) {
                        flurstZaehler = currentElement.getChild("Literal", OGC_NAMESPACE);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Flur Zähler Literal gesetzt: " + flurstZaehler);
                        }
                    } else if (name.getText().equals("app:flurstn")) {
                        flurstNenner = currentElement.getChild("Literal", OGC_NAMESPACE);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Flur Nenner Literal gesetzt: " + flurstNenner);
                        }
                    } else {
                        LOG.warn("Unbekanntes Literal");
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error("Fehler bei der Konfiguration der WFSQuery/Request", ex);
        }
    }

    @Override
    public void configure(final Element parent) {
    }
}
