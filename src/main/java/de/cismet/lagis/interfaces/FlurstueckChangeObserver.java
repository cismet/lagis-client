/*
 * ProgressManger.java
 *
 * Created on July 3, 2007, 11:18 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.interfaces;

import de.cismet.lagisEE.entity.core.Flurstueck;

/**
 *
 * @author hell
 */
public interface FlurstueckChangeObserver {
    public void fireFlurstueckChanged(Flurstueck newFlurstueck);    
    public void flurstueckChangeFinished(FlurstueckChangeListener fcListener);
    public boolean isFlurstueckChangeInProgress();
}
