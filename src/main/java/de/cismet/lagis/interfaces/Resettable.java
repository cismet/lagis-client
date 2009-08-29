/*
 * Resettable.java
 *
 * Created on 20. April 2007, 13:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.interfaces;

/**
 *
 * @author Puhl
 */
public interface Resettable extends Editable {
    //Methoden getrennt weil die enableMethode später wieder zum enablen benutzt wird
    public void clearComponent();    
}
