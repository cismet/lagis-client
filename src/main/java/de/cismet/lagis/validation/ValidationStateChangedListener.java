/*
 * ValidationStateChangedListener.java
 *
 * Created on 1. Februar 2005, 16:02
 */

package de.cismet.lagis.validation;

/**
 *
 * @author hell
 */
public interface ValidationStateChangedListener {
    //better validationEvent    
    public void validationStateChanged(Object validatedObject);
}
