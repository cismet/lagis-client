/*
 * FlurstueckRequester.java
 *
 * Created on 2. Mai 2007, 10:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.interfaces;

import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;



/**
 *
 * @author Puhl
 */
public interface FlurstueckRequester {
     void requestFlurstueck(FlurstueckSchluessel key);
     void updateFlurstueckKeys();
}
