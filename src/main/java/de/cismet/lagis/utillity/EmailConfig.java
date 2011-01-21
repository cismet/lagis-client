/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * EmailConfig.java
 *
 * Created on 28. November 2007, 16:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.utillity;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class EmailConfig {

    //~ Instance fields --------------------------------------------------------

    private String username;
    private String password;
    private String smtpServer;
    private String senderAddress;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of EmailConfig.
     */
    public EmailConfig() {
    }

    /**
     * Creates a new EmailConfig object.
     *
     * @param  username       DOCUMENT ME!
     * @param  password       DOCUMENT ME!
     * @param  smtpServer     DOCUMENT ME!
     * @param  senderAddress  DOCUMENT ME!
     */
    public EmailConfig(final String username,
            final String password,
            final String smtpServer,
            final String senderAddress) {
        this.username = username;
        this.password = password;
        this.setSmtpServer(smtpServer);
        this.senderAddress = senderAddress;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getUsername() {
        return username;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  username  DOCUMENT ME!
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getPassword() {
        return password;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  password  DOCUMENT ME!
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSenderAddress() {
        return senderAddress;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  senderAddress  DOCUMENT ME!
     */
    public void setSenderAddress(final String senderAddress) {
        this.senderAddress = senderAddress;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSmtpServer() {
        return smtpServer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  smtpServer  DOCUMENT ME!
     */
    public void setSmtpServer(final String smtpServer) {
        this.smtpServer = smtpServer;
    }

    @Override
    public String toString() {
        return senderAddress + " " + smtpServer;
    }
}
