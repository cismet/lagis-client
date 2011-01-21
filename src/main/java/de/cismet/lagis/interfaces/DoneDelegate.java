/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * JobDone.java
 *
 * Created on 10. Januar 2008, 11:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.interfaces;

import java.util.HashMap;

import de.cismet.lagis.thread.ExtendedSwingWorker;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public interface DoneDelegate<T, V> {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  worker      DOCUMENT ME!
     * @param  properties  DOCUMENT ME!
     */
    void jobDone(ExtendedSwingWorker<T, V> worker, HashMap<Integer, Boolean> properties);
}
