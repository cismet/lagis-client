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
public class TerminateNutzungNotPossibleException extends Exception {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TerminateNutzungNotPossibleException object.
     */
    public TerminateNutzungNotPossibleException() {
    }

    /**
     * Creates a new TerminateNutzungNotPossibleException object.
     *
     * @param  cause  DOCUMENT ME!
     */
    public TerminateNutzungNotPossibleException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new TerminateNutzungNotPossibleException object.
     *
     * @param  message  DOCUMENT ME!
     */
    public TerminateNutzungNotPossibleException(final String message) {
        super(message);
    }

    /**
     * Creates a new TerminateNutzungNotPossibleException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     */
    public TerminateNutzungNotPossibleException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
