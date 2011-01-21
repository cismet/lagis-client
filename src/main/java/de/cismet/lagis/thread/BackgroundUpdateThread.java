/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * UpdateThread.java
 *
 * Created on 9. Oktober 2007, 11:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.thread;

import org.apache.log4j.Logger;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class BackgroundUpdateThread<T> extends Thread {

    //~ Static fields/initializers ---------------------------------------------

    private static boolean IS_RUNNING = true;

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private T newObject;
    private T currentObject;
    private Boolean updateAvailable = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of UpdateThread.
     */
    public BackgroundUpdateThread() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public final void run() {
        while (IS_RUNNING) {
            if (isConsumeAvailableUpdate()) {
                update();
                // }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    log.error("Thread konnte nicht unterbrochen werden", ex);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    protected void update() {
    }

    /**
     * DOCUMENT ME!
     */
    protected void cleanup() {
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public final T getCurrentObject() {
        return currentObject;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  updateObject  DOCUMENT ME!
     */
    public final synchronized void notifyThread(final T updateObject) {
        updateAvailable = true;
        newObject = updateObject;
        // notifyAll();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private synchronized boolean isConsumeAvailableUpdate() {
        if (updateAvailable) {
            updateAvailable = false;
            currentObject = newObject;
            return true;
        } else {
//            try {
//            //wait();
//             } catch (InterruptedException ex) {
//                    log.error("Thread konnte nicht warten geschickt werden",ex);
//             }
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected final synchronized boolean isUpdateAvailable() {
        return updateAvailable;
    }
}
