/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagisEE.interfaces;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface Equalator<T> {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   obj1  DOCUMENT ME!
     * @param   obj2  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean pedanticEquals(T obj1, T obj2);
}
