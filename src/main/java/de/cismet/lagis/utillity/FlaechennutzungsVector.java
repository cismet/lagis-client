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
 *
 * @author Puhl
 */
//TODO UGLY BETTER SOLUTION THAN DIFFERENCIATING OVER INHERITANCE ??? 
public class FlaechennutzungsVector extends Vector {
    public FlaechennutzungsVector(Collection<?> c){
        super(c);
    }
    
    public FlaechennutzungsVector(){
        super();
    }
    
}
