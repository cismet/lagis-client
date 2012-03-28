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
package de.cismet.lagis.Exception;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
public class BuchungNotInNutzungException extends Exception {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BuchungNotInNutzungException object.
     */
    public BuchungNotInNutzungException() {
    }

    /**
     * Creates a new BuchungNotInNutzungException object.
     *
     * @param  cause  DOCUMENT ME!
     */
    public BuchungNotInNutzungException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new BuchungNotInNutzungException object.
     *
     * @param  message  DOCUMENT ME!
     */
    public BuchungNotInNutzungException(final String message) {
        super(message);
    }

    /**
     * Creates a new BuchungNotInNutzungException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     */
    public BuchungNotInNutzungException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
