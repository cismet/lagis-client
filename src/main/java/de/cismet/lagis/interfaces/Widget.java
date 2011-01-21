/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * Widget.java
 *
 * Created on 1. Mai 2007, 09:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.interfaces;

import javax.swing.Icon;

import de.cismet.lagis.validation.Validatable;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public interface Widget extends ChangeListener, Resettable, Editable, Validatable, Refreshable, EntitySaver {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getWidgetName();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Icon getWidgetIcon();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isWidgetReadOnly();
}
