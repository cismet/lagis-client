/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * BebauungsVector.java
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
public class BebauungsVector extends Vector {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BebauungsVector object.
     */
    public BebauungsVector() {
        super();
    }

    /**
     * Creates a new BebauungsVector object.
     *
     * @param  c  DOCUMENT ME!
     */
    public BebauungsVector(final Collection<?> c) {
        super(c);
    }

    /** Creates a new instance of BebauungsVector */
}
