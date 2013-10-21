/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.tables;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
//TODO mit Jean bereden
public interface RemoveActionHelper {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  source  DOCUMENT ME!
     */
    void duringRemoveAction(Object source);

    /**
     * DOCUMENT ME!
     *
     * @param  source  DOCUMENT ME!
     */
    void afterRemoveAction(Object source);
}
