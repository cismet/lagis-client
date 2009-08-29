/*
 * FlurstueckSaver.java
 *
 * Created on 14. Mai 2007, 10:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.interfaces;

import de.cismet.lagisEE.entity.core.Flurstueck;

/**
 *
 * @author Puhl
 */
public interface FlurstueckSaver extends EntitySaver {
    public void updateFlurstueckForSaving(Flurstueck flurstueck);
}
