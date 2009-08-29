/*
 * GeometrySlotProvider.java
 *
 * Created on 12. Mai 2007, 19:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.interfaces;

import de.cismet.lagis.utillity.GeometrySlotInformation;
import java.util.Vector;

/**
 *
 * @author Puhl
 */
public interface GeometrySlotProvider  {    
    public Vector<GeometrySlotInformation> getSlotInformation();    
    public String getProviderName();    
}
