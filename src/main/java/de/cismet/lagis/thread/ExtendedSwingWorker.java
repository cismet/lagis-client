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
 *
 * @author Sebastian Puhl
 */
public abstract class ExtendedSwingWorker<T, V> extends SwingWorker<T, V> {
    
    protected boolean hadErrors;
    protected String errorMessage;
    protected Object keyObject;
    
    /** Creates a new instance of ExtendedSwingWorker */
    public ExtendedSwingWorker(Object keyObject) {
        super();
        this.keyObject = keyObject;                
    }

    public boolean hadErrors() {
        return hadErrors;
    }

    public void setHadErrors(boolean hadErrors) {
        this.hadErrors = hadErrors;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Object getKeyObject() {
        return keyObject;
    }

    public void setKeyObject(Object keyObject) {
        this.keyObject = keyObject;
    }
    
}
