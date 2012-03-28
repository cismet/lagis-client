/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * FlurstueckSaver.java
 *
 * Created on 14. Mai 2007, 10:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.interfaces;

import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckCustomBean;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public interface FlurstueckSaver extends EntitySaver {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueck  DOCUMENT ME!
     */
    void updateFlurstueckForSaving(FlurstueckCustomBean flurstueck);
}
