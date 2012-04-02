/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * ActionNotSuccessfullException.java
 *
 * Created on 23. Oktober 2007, 13:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.Exception;

import java.io.Serializable;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class ActionNotSuccessfulException extends Exception implements Serializable {

    //~ Instance fields --------------------------------------------------------

    private Exception nestedExceptions;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of ActionNotSuccessfullException.
     */
    public ActionNotSuccessfulException() {
        super();
    }

    /**
     * Creates a new ActionNotSuccessfulException object.
     *
     * @param  message  DOCUMENT ME!
     */
    public ActionNotSuccessfulException(final String message) {
        this(message, null);
    }

    /**
     * Creates a new ActionNotSuccessfulException object.
     *
     * @param  message           DOCUMENT ME!
     * @param  nestedExceptions  DOCUMENT ME!
     */
    public ActionNotSuccessfulException(final String message, final Exception nestedExceptions) {
        super(message);
        this.nestedExceptions = nestedExceptions;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Exception getNestedExceptions() {
        return nestedExceptions;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  nestedExceptions  DOCUMENT ME!
     */
    public void setNestedExceptions(final Exception nestedExceptions) {
        this.nestedExceptions = nestedExceptions;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean hasNestedExceptions() {
        return nestedExceptions != null;
    }
}
