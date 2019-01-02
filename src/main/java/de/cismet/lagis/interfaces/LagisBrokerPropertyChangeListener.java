/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * FlurstueckChangeListener.java
 *
 * Created on 14. Mai 2007, 10:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.interfaces;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public interface LagisBrokerPropertyChangeListener extends PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    String PROP__CURRENT_WFS_GEOMETRY = "currentWFSGeometry";
    String PROP__CURRENT_WFS_GEOMETRY_ERROR = "currentWFSGeometryError";
    String PROP__CURRENT_REBES = "currentRebes";

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    @Override
    void propertyChange(PropertyChangeEvent evt);
}
