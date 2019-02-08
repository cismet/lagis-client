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
package de.cismet.lagis.broker;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
public class LockAlreadyExistsException extends Exception {

    //~ Instance fields --------------------------------------------------------

    private final Collection<CidsBean> alreadyExisingLocks = new ArrayList<CidsBean>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LockAlreadyExistsException object.
     *
     * @param  message               DOCUMENT ME!
     * @param  alreadyExistingLocks  DOCUMENT ME!
     */
    public LockAlreadyExistsException(final String message,
            final Collection<CidsBean> alreadyExistingLocks) {
        super(message);
        if (alreadyExistingLocks != null) {
            alreadyExisingLocks.addAll(alreadyExistingLocks);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<CidsBean> getAlreadyExisingLocks() {
        return alreadyExisingLocks;
    }
}
