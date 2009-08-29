/*
 * FlurstueckChangeListener.java
 *
 * Created on 14. Mai 2007, 10:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.interfaces;

import de.cismet.lagis.interfaces.ChangeListener;
import de.cismet.lagisEE.entity.core.Flurstueck;

/**
 *
 * @author Puhl
 */
public interface FlurstueckChangeListener extends ChangeListener {
    public void flurstueckChanged(Flurstueck newFlurstueck);    
}
