/*
 * Validatable.java
 *
 * Created on 23. Januar 2005, 14:09
 */

package de.cismet.lagis.validation;

import java.awt.Component;

/**
 *
 * @author HP
 */
public interface Validatable {
    public static final int VALID=0;
    public static final int WARNING=1;
    public static final int ERROR=2;
    public void addValidationStateChangedListener(ValidationStateChangedListener l);
    public void removeValidationStateChangedListener(ValidationStateChangedListener l);
    public int getStatus();
    public void showAssistent(Component parent);    
    //needen to cascadeValidation
    public void fireValidationStateChanged(Object validatedObject);
    //public String getFormatExample();
    //public String getDescription();
    public String getValidationMessage();
}
