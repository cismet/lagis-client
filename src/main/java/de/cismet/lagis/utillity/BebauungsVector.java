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
 *
 * @author Puhl
 */
public class BebauungsVector extends Vector {
    public BebauungsVector(){
        super();                
    }
    
    public BebauungsVector(Collection<?> c){
        super(c);                
    }
    
    /** Creates a new instance of BebauungsVector */
}
