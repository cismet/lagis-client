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
 *
 * @author Sebastian Puhl
 */
public class EmailConfig {
    
    private String username;
    private String password;
    private String smtpServer;
    private String senderAddress;
    
    /** Creates a new instance of EmailConfig */
     public EmailConfig() {
         
     }
     
    public EmailConfig(String username,String password,String smtpServer,String senderAddress) {
        this.username=username;
        this.password=password;
        this.setSmtpServer(smtpServer);
        this.senderAddress=senderAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public String toString() {
        return senderAddress+" "+smtpServer;
    }
}
