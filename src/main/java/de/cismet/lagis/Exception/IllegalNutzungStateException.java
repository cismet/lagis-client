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
//ToDo rename in no Buchwert Exception ??
public class IllegalNutzungStateException extends Exception {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new IllegalNutzungStateException object.
     */
    public IllegalNutzungStateException() {
    }

    /**
     * Creates a new IllegalNutzungStateException object.
     *
     * @param  cause  DOCUMENT ME!
     */
    public IllegalNutzungStateException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new IllegalNutzungStateException object.
     *
     * @param  message  DOCUMENT ME!
     */
    public IllegalNutzungStateException(final String message) {
        super(message);
    }

    /**
     * Creates a new IllegalNutzungStateException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     */
    public IllegalNutzungStateException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
