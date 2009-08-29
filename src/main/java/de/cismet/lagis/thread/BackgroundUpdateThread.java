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
 *
 * @author Sebastian Puhl
 */
public class BackgroundUpdateThread<T> extends Thread{
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    
    private static boolean IS_RUNNING = true;
    private T newObject;
    private T currentObject;
    private Boolean updateAvailable = false;
    
    /** Creates a new instance of UpdateThread */
    public BackgroundUpdateThread() {
    }

    public final void run() {
        while(IS_RUNNING){
           if(isConsumeAvailableUpdate()){               
               update(); 
           //}
           } else {
                try {
                    Thread.sleep(100);                    
                } catch (InterruptedException ex) {
                    log.error("Thread konnte nicht unterbrochen werden",ex);
                }
           }
        }
    }
    
    protected void update(){
          
    }
    
    protected  void cleanup(){
        
    }
    
    public final T getCurrentObject(){
        return currentObject;
    }
    
    public final synchronized void notifyThread(T updateObject){
        updateAvailable = true;
        newObject = updateObject;
        //notifyAll();
    }
    
    private final synchronized boolean isConsumeAvailableUpdate(){
        if(updateAvailable){
            updateAvailable=false;
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
    
    protected final synchronized boolean isUpdateAvailable(){
        return updateAvailable;
    }
    
}
