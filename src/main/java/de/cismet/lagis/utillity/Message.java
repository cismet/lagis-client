/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
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
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class Message {

    //~ Static fields/initializers ---------------------------------------------

    // Receiver

    public static final int RECEIVER_NKF = 0;
    public static final int RECEIVER_ADMIN = 1;
    public static final int RECEIVER_LAGERBUCH = 2;
    public static final int RECEIVER_VERWALTUNGSSTELLE = 3;

    // Types
    // TODO write TYPES behind ?? better readability in code
    public static final int VERWALTUNGSBEREICH_NEW = 0;
    public static final int VERWALTUNGSBEREICH_DELETED = 1;
    public static final int VERWALTUNGSBEREICH_CHANGED = 2;
    public static final int VERWALTUNGSBEREICH_ERROR = 5;

    // Configurationfile ids
    public static final String MAIL_ADDRESSES_DEVELOPER = "developerMailaddresses";
    public static final String MAIL_ADDRESSES_NKF = "nkfMailAddresses";
    public static final String MAIL_ADDRESSES_MAINTENANCE = "maintenanceMailaddresses";

    public static final int GENERAL_ERROR = 7;

    public static final int NUTZUNG_EVALUATION = 3;
    public static final int NUTZUNG_BOOKING = 4;
    public static final int NUTZUNG_ERROR = 6;

    //~ Instance fields --------------------------------------------------------

    private int messageReceiver;
    private int messageType;
    private Vector messageObjects;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of Message.
     */
    public Message() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getMessageReceiver() {
        return messageReceiver;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  messageReceiver  DOCUMENT ME!
     */
    public void setMessageReceiver(final int messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getMessageType() {
        return messageType;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  messageType  DOCUMENT ME!
     */
    public void setMessageType(final int messageType) {
        this.messageType = messageType;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector getMessageObjects() {
        return messageObjects;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  messageObjects  DOCUMENT ME!
     */
    public void setMessageObjects(final Vector messageObjects) {
        this.messageObjects = messageObjects;
    }
    /**
     * TODO nice idea check if there are other places to use.
     *
     * @param   messageReceiver  DOCUMENT ME!
     * @param   messageType      DOCUMENT ME!
     * @param   messageObjects   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Message createNewMessage(final int messageReceiver,
            final int messageType,
            final Object... messageObjects) {
        final Message tmp = new Message();
        tmp.setMessageReceiver(messageReceiver);
        tmp.setMessageType(messageType);
        tmp.setMessageObjects(new Vector(Arrays.asList(messageObjects)));
        return tmp;
    }
}
