/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * ExtendedSwingWorker.java
 *
 * Created on 10. Januar 2008, 11:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.thread;

import javax.swing.SwingWorker;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public abstract class ExtendedSwingWorker<T, V> extends SwingWorker<T, V> {

    //~ Instance fields --------------------------------------------------------

    protected boolean hadErrors;
    protected String errorMessage;
    protected Object keyObject;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of ExtendedSwingWorker.
     *
     * @param  keyObject  DOCUMENT ME!
     */
    public ExtendedSwingWorker(final Object keyObject) {
        super();
        this.keyObject = keyObject;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean hadErrors() {
        return hadErrors;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  hadErrors  DOCUMENT ME!
     */
    public void setHadErrors(final boolean hadErrors) {
        this.hadErrors = hadErrors;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  errorMessage  DOCUMENT ME!
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object getKeyObject() {
        return keyObject;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  keyObject  DOCUMENT ME!
     */
    public void setKeyObject(final Object keyObject) {
        this.keyObject = keyObject;
    }
}
