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

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.cids.dynamics.CidsBean;

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

    /**
     * DOCUMENT ME!
     *
     * @param  cidsColl   DOCUMENT ME!
     * @param  otherColl  DOCUMENT ME!
     */
    public static void makeCollectionContainSameAsOtherCollection(final Collection cidsColl,
            final Collection otherColl) {
        // ueberschuessige entfernen
        final Collection<CidsBean> nutzungenToDelete = new ArrayList<CidsBean>(cidsColl);
        nutzungenToDelete.removeAll(otherColl);

        // neue hinzufuegen
        final Collection<CidsBean> nutzungenToAdd = new ArrayList<CidsBean>(otherColl);
        nutzungenToAdd.removeAll(cidsColl);

        cidsColl.removeAll(nutzungenToDelete);
        cidsColl.addAll(nutzungenToAdd);
    }
}
