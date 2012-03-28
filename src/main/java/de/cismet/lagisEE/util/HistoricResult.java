/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * HistoricResult.java
 *
 * Created on 6. Februar 2008, 15:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagisEE.util;

import java.io.Serializable;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class HistoricResult implements Serializable {

    //~ Instance fields --------------------------------------------------------

    private boolean wasSuccessFul = true;
    private boolean wasDeleted = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of HistoricResult.
     *
     * @param  wasSuccessFul  DOCUMENT ME!
     * @param  wasDeleted     DOCUMENT ME!
     */
    public HistoricResult(final boolean wasSuccessFul, final boolean wasDeleted) {
        setWasSuccessFul(wasSuccessFul);
        setWasDeleted(wasDeleted);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean wasSuccessFul() {
        return wasSuccessFul;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  wasSuccessFul  DOCUMENT ME!
     */
    public void setWasSuccessFul(final boolean wasSuccessFul) {
        this.wasSuccessFul = wasSuccessFul;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean wasDeleted() {
        return wasDeleted;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  wasDeleted  DOCUMENT ME!
     */
    public void setWasDeleted(final boolean wasDeleted) {
        this.wasDeleted = wasDeleted;
    }
}
