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

import java.io.Serializable;

import javax.ejb.ApplicationException;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
@ApplicationException(rollback = true)
public class DataInconsistencyException extends Exception implements Serializable {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DataInconsistencyException object.
     */
    public DataInconsistencyException() {
        super();
    }

    /**
     * Creates a new DataInconsistencyException object.
     *
     * @param  message  DOCUMENT ME!
     */
    public DataInconsistencyException(final String message) {
        super(message);
    }
}
