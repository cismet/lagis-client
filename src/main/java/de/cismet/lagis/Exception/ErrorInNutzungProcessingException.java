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
public class ErrorInNutzungProcessingException extends Exception {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ErrorInNutzungProcessingException object.
     */
    public ErrorInNutzungProcessingException() {
    }

    /**
     * Creates a new ErrorInNutzungProcessingException object.
     *
     * @param  cause  DOCUMENT ME!
     */
    public ErrorInNutzungProcessingException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new ErrorInNutzungProcessingException object.
     *
     * @param  message  DOCUMENT ME!
     */
    public ErrorInNutzungProcessingException(final String message) {
        super(message);
    }

    /**
     * Creates a new ErrorInNutzungProcessingException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     */
    public ErrorInNutzungProcessingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
