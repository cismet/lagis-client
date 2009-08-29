/*
 * JobDone.java
 *
 * Created on 10. Januar 2008, 11:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.interfaces;

import de.cismet.lagis.thread.ExtendedSwingWorker;
import java.util.HashMap;

/**
 *
 * @author Sebastian Puhl
 */
public interface DoneDelegate<T,V> {    
    public abstract void jobDone(ExtendedSwingWorker<T,V> worker,HashMap<Integer,Boolean> properties);
}
