/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * FlurstueckChangeListener.java
 *
 * Created on 14. Mai 2007, 10:32
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
public interface FlurstueckChangeListener extends ChangeListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  newFlurstueck  DOCUMENT ME!
     */
    void flurstueckChanged(FlurstueckCustomBean newFlurstueck);
}
