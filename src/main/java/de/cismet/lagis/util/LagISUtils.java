/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.util;

import org.apache.log4j.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import de.cismet.tools.CurrentStackTrace;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class LagISUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger log = org.apache.log4j.Logger.getLogger(LagISUtils.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  element  DOCUMENT ME!
     */
    public static void logXML(final Element element) {
        final Document doc = new Document();
        // is this the right way
        doc.setRootElement((Element)element.clone());
        final XMLOutputter out = new XMLOutputter();
        final String postString = out.outputString(doc);
        if (log.isDebugEnabled()) {
            log.debug("logXML :" + postString, new CurrentStackTrace());
        }
    }
}
