/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * FlaechennutzungsVector.java
 *
 * Created on 18. Mai 2007, 11:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.utillity;

import java.util.Collection;
import java.util.Vector;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
//TODO UGLY BETTER SOLUTION THAN DIFFERENCIATING OVER INHERITANCE ???
public class FlaechennutzungsVector extends Vector {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FlaechennutzungsVector object.
     */
    public FlaechennutzungsVector() {
        super();
    }
    /**
     * Creates a new FlaechennutzungsVector object.
     *
     * @param  c  DOCUMENT ME!
     */
    public FlaechennutzungsVector(final Collection<?> c) {
        super(c);
    }
}
