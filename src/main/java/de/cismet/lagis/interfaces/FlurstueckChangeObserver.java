/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * ProgressManger.java
 *
 * Created on July 3, 2007, 11:18 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.interfaces;

import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckCustomBean;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public interface FlurstueckChangeObserver {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  newFlurstueck  DOCUMENT ME!
     */
    void fireFlurstueckChanged(FlurstueckCustomBean newFlurstueck);
    /**
     * DOCUMENT ME!
     *
     * @param  fcListener  DOCUMENT ME!
     */
    void flurstueckChangeFinished(FlurstueckChangeListener fcListener);
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isFlurstueckChangeInProgress();
}
