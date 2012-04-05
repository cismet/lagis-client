/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * FlurstueckRequester.java
 *
 * Created on 2. Mai 2007, 10:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.interfaces;

import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public interface FlurstueckRequester {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  key  DOCUMENT ME!
     */
    void requestFlurstueck(FlurstueckSchluesselCustomBean key);
    /**
     * DOCUMENT ME!
     */
    void updateFlurstueckKeys();
}
