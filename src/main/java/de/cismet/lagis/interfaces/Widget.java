/*
 * Widget.java
 *
 * Created on 1. Mai 2007, 09:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.interfaces;

import de.cismet.lagis.validation.Validatable;
import javax.swing.Icon;

/**
 *
 * @author Puhl
 */
public interface Widget extends ChangeListener,Resettable,Editable,Validatable,Refreshable,EntitySaver {    
    
    public String getWidgetName();
    public Icon getWidgetIcon();
    public boolean isWidgetReadOnly();
}
