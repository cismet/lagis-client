/*
 * Message.java
 *
 * Created on 27. November 2007, 15:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.utillity;

import java.util.Arrays;
import java.util.Vector;

/**
 *
 * @author Sebastian Puhl
 */
public class Message {
    
    //Receiver
    
    public final static int RECEIVER_NKF = 0;
    public final static int RECEIVER_ADMIN = 1;
    public final static int RECEIVER_LAGERBUCH = 2;
    public final static int RECEIVER_VERWALTUNGSSTELLE = 3;    
    
    //Types    
    //TODO write TYPES behind ?? better readability in code
    public final static int VERWALTUNGSBEREICH_NEW = 0;
    public final static int VERWALTUNGSBEREICH_DELETED = 1;
    public final static int VERWALTUNGSBEREICH_CHANGED = 2;
    public final static int VERWALTUNGSBEREICH_ERROR =5;
    
    //Configurationfile ids
    public final static String MAIL_ADDRESSES_DEVELOPER = "developerMailaddresses";
    public final static String MAIL_ADDRESSES_NKF = "nkfMailAddresses";
    public final static String MAIL_ADDRESSES_MAINTENANCE = "maintenanceMailaddresses";
    
    public final static int GENERAL_ERROR =7;
   
    public final static int NUTZUNG_EVALUATION = 3;
    public final static int NUTZUNG_BOOKING = 4;
    public final static int NUTZUNG_ERROR = 6;
    
    private int messageReceiver;
    private int messageType;
    private Vector messageObjects;
    
    /** Creates a new instance of Message */
    public Message() {
    }
    
    public int getMessageReceiver() {
        return messageReceiver;
    }
    
    public void setMessageReceiver(int messageReceiver) {
        this.messageReceiver = messageReceiver;
    }
    
    public int getMessageType() {
        return messageType;
    }
    
    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
    
    public Vector getMessageObjects() {
        return messageObjects;
    }
    
    public void setMessageObjects(Vector messageObjects) {
        this.messageObjects = messageObjects;
    }
    
    //TODO nice idea check if there are other places to use
    public static Message createNewMessage(int messageReceiver,int messageType,Object... messageObjects){
        Message tmp = new Message();
        tmp.setMessageReceiver(messageReceiver);
        tmp.setMessageType(messageType);
        tmp.setMessageObjects(new Vector(Arrays.asList(messageObjects)));
        return tmp;
    }
    
}
