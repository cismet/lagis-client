/*
 * WFSUpdateContainer.java
 *
 * Created on 18. Dezember 2007, 12:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.utillity;

import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;

/**
 *
 * @author Sebastian Puhl
 */
public class WFSUpdateContainer {
    
    private FlurstueckSchluessel flurstueckSchluessel;
    private boolean noGeometryAssigned=true;
    private boolean manyVerwaltungsbereiche;
    
    /** Creates a new instance of WFSUpdateContainer */
    public WFSUpdateContainer() {
    }

    public FlurstueckSchluessel getFlurstueckSchluessel() {
        return flurstueckSchluessel;
    }

    public void setFlurstueckSchluessel(FlurstueckSchluessel flurstueckSchluessel) {
        this.flurstueckSchluessel = flurstueckSchluessel;
    }

    public boolean isNoGeometryAssigned() {
        return noGeometryAssigned;
    }

    public void setNoGeometryAssigned(boolean noGeometryAssigned) {
        this.noGeometryAssigned = noGeometryAssigned;
    }

    public boolean hasManyVerwaltungsbereiche() {
        return manyVerwaltungsbereiche;
    }

    public void setManyVerwaltungsbereiche(boolean manyVerwaltungsbereiche) {
        this.manyVerwaltungsbereiche = manyVerwaltungsbereiche;
    }
    
}
