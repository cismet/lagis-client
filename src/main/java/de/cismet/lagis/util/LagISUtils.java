/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.util;

import de.cismet.tools.CurrentStackTrace;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author Sebastian Puhl
 */
public class LagISUtils {

    private final static Logger log = org.apache.log4j.Logger.getLogger(LagISUtils.class);

    public static void logXML(Element element) {
        Document doc = new Document();
        //is this the right way
        doc.setRootElement((Element) element.clone());
        XMLOutputter out = new XMLOutputter();
        String postString = out.outputString(doc);
        log.debug("logXML :" + postString, new CurrentStackTrace());
    }
}
