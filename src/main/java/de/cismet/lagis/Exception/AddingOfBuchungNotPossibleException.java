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
public class AddingOfBuchungNotPossibleException extends Exception {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AddingOfBuchungNotPossibleException object.
     */
    public AddingOfBuchungNotPossibleException() {
    }

    /**
     * Creates a new AddingOfBuchungNotPossibleException object.
     *
     * @param  cause  DOCUMENT ME!
     */
    public AddingOfBuchungNotPossibleException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new AddingOfBuchungNotPossibleException object.
     *
     * @param  message  DOCUMENT ME!
     */
    public AddingOfBuchungNotPossibleException(final String message) {
        super(message);
    }

    /**
     * Creates a new AddingOfBuchungNotPossibleException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     */
    public AddingOfBuchungNotPossibleException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
