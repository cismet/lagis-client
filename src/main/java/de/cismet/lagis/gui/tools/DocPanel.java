/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * DocPanel.java
 *
 * Created on 16. Dezember 2004, 15:39
 */
package de.cismet.lagis.gui.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import de.cismet.lagis.gui.panels.DMSPanel;

import de.cismet.lagisEE.entity.core.DmsUrl;
import de.cismet.lagisEE.entity.core.Url;
import de.cismet.lagisEE.entity.core.UrlBase;

import de.cismet.tools.URLSplitter;

/**
 * Klasse zum Anzeigen von Links und zugehörigen Icons in einer Anwendung.<br>
 * Bei Klick wird die Url im Webbrowser geöffnet.
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class DocPanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    public static final int MAX_DESCRIPTION_LENGTH = 12;
    public static final String DELETE_ACTION_COMMAND = "DELETE_ACTION";

    //~ Instance fields --------------------------------------------------------

    Vector actionListeners = new Vector();
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Icon icon;
    // private String desc;
    private String gotoUrl;
    private java.applet.AppletContext appletContext = null;
    private boolean deletable = false;
    // private int dms_urls_id=-1;
    // private int dms_url_id=-1;
    // private int url_id=-1;
    // private int url_base_id=-1;
    // private String kassenzeichen="";
    private DmsUrl dmsUrlEntity;
//    public String getToolTipText(MouseEvent e) {
//
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblDescr;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JMenuItem mniDelete;
    private javax.swing.JPopupMenu pmnLink;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form DocPanel.
     *
     * @param  dmsUrlEntity  DOCUMENT ME!
     */
    public DocPanel(final DmsUrl dmsUrlEntity) {
        this.dmsUrlEntity = dmsUrlEntity;
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  dmsUrlEntity  DOCUMENT ME!
     */
    public void setDMSUrlEntity(final DmsUrl dmsUrlEntity) {
        this.dmsUrlEntity = dmsUrlEntity;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  typ  DOCUMENT ME!
     */
    public void setTyp(final Integer typ) {
        dmsUrlEntity.setTyp(typ);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getTyp() {
        return dmsUrlEntity.getTyp();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DmsUrl getDMSUrlEntity() {
        return dmsUrlEntity;
    }

    /**
     * Setzt den Appletkontext.<br>
     * Wird dann benötigt falls DocPanel in einem Applett benutzt wird
     *
     * @param  appletContext  Appletkontext
     */
    public void setAplettContext(final java.applet.AppletContext appletContext) {
        this.appletContext = appletContext;
    }

    /**
     * Liefert das dargestellte Symbol zurück.
     *
     * @return  Icon
     */
    public Icon getIcon() {
        return lblIcon.getIcon();
    }

    /**
     * Setzt das dargestellte Symbol.
     *
     * @param  icon  Dargestelltes Symbol
     */
    public void setIcon(final Icon icon) {
        lblIcon.setIcon(icon);
    }

    /**
     * Liefert die Beschreibung.
     *
     * @return  Beschreibung
     */
// public String getDesc() {
// return this.desc;
// }
    public String getDesc() {
        return dmsUrlEntity.getName();
    }

    /**
     * Setzt die Beschreibung.
     *
     * @param  desc  Beschreibung
     */
// public void setDesc(String desc) {
// this.desc=desc;
// if (desc.length()>MAX_DESCRIPTION_LENGTH) {
// this.lblDescr.setText(desc.substring(0,MAX_DESCRIPTION_LENGTH)+"...");
// this.lblDescr.setToolTipText(desc);
// }
// else {
// this.lblDescr.setText(desc);
// }
// }
    public void setDesc(final String desc) {
        dmsUrlEntity.setName(desc);
        if (desc.length() > MAX_DESCRIPTION_LENGTH) {
            this.lblDescr.setText(desc.substring(0, MAX_DESCRIPTION_LENGTH) + "...");
            this.lblDescr.setToolTipText(desc);
        } else {
            this.lblDescr.setText(desc);
        }
    }

    /**
     * Liefert die verknüpfte Url.
     *
     * @return  Url
     */
// public String getGotoUrl() {
// return gotoUrl;
//
// }
    public String getGotoUrl() {
        return gotoUrl;
    }

    /**
     * setzt die verknüpfte Url.
     *
     * @param  gotoUrl  Url
     */
    public void setGotoUrl(final String gotoUrl) {
        final URLSplitter splitter = new URLSplitter(gotoUrl);
        final Url urlEntity = dmsUrlEntity.getUrl();
        log.info("UrlEntity: " + urlEntity);
        final UrlBase urlBase = urlEntity.getUrlBase();
        urlBase.setPfad(splitter.getPath());
        urlBase.setProtPrefix(splitter.getProt_prefix());
        urlBase.setServer(splitter.getServer());
        urlEntity.setUrlBase(urlBase);
        urlEntity.setObjektname(splitter.getObject_name());
        dmsUrlEntity.setUrl(urlEntity);
        this.gotoUrl = gotoUrl;
        lblIcon.setToolTipText(gotoUrl);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        pmnLink = new javax.swing.JPopupMenu();
        mniDelete = new javax.swing.JMenuItem();
        lblIcon = new javax.swing.JLabel();
        lblDescr = new javax.swing.JLabel();

        mniDelete.setText("Link entfernen");
        mniDelete.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    mniDeleteActionPerformed(evt);
                }
            });

        pmnLink.add(mniDelete);

        setLayout(new java.awt.BorderLayout());

        setMaximumSize(new java.awt.Dimension(100, 100));
        lblIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIcon.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/filetypes/dms_default.png")));
        lblIcon.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mousePressed(final java.awt.event.MouseEvent evt) {
                    lblIconMousePressed(evt);
                }
            });

        add(lblIcon, java.awt.BorderLayout.CENTER);

        lblDescr.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDescr.setText("Beschreibung");
        lblDescr.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

                @Override
                public void mouseMoved(final java.awt.event.MouseEvent evt) {
                    lblDescrMouseMoved(evt);
                }
            });
        lblDescr.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblDescrMouseClicked(evt);
                }
                @Override
                public void mouseEntered(final java.awt.event.MouseEvent evt) {
                    lblDescrMouseEntered(evt);
                }
                @Override
                public void mouseExited(final java.awt.event.MouseEvent evt) {
                    lblDescrMouseExited(evt);
                }
            });

        add(lblDescr, java.awt.BorderLayout.SOUTH);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblIconMousePressed(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblIconMousePressed
        if (log.isDebugEnabled()) {
            log.debug("mouse pressed");
        }
        if ((evt.getButton() == evt.BUTTON3) && isDeletable()) {
            if (log.isDebugEnabled()) {
                log.debug("button3 && isDeletable");
            }
            // TODO WARUM NUR EIN PANEL;
            if (this.getParent() instanceof DMSPanel) {
                if (log.isDebugEnabled()) {
                    log.debug("isDMSPANEL");
                }
                if (((DMSPanel)(getParent())).isInEditMode()) {
                    pmnLink.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        }
    } //GEN-LAST:event_lblIconMousePressed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void mniDeleteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_mniDeleteActionPerformed
        fireDeleteActionPerformed();
    }                                                                             //GEN-LAST:event_mniDeleteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblDescrMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblDescrMouseClicked
        if (gotoUrl == null) {
            JOptionPane.showMessageDialog(this, "Es wurde keine Url hinterlegt!", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (appletContext == null) {
                de.cismet.tools.BrowserLauncher.openURL(gotoUrl);
            } else {
                final java.net.URL u = new java.net.URL(gotoUrl);
                appletContext.showDocument(u, "cismetDocPanelFrame");
            }
        } catch (Exception e) {
            log.warn("Fehler beim öffnen von:" + gotoUrl + "\nNeuer Versuch", e);
            // Nochmal zur Sicherheit mit dem BrowserLauncher probieren
            try {
                de.cismet.tools.BrowserLauncher.openURL(gotoUrl);
            } catch (Exception e2) {
                try {
                    gotoUrl = gotoUrl.replaceAll("\\\\", "/");
                    gotoUrl = gotoUrl.replaceAll(" ", "%20");
                    log.warn("Auch das 2te Mal ging schief.Fehler beim öffnen von:" + gotoUrl + "\nLetzter Versuch",
                        e2);
                    de.cismet.tools.BrowserLauncher.openURL("file:///" + gotoUrl);
                } catch (Exception e3) {
                    log.error("Auch das 3te Mal ging schief.Fehler beim öffnen von:file://" + gotoUrl, e3);
                }
            }
        }
    } //GEN-LAST:event_lblDescrMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblDescrMouseExited(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblDescrMouseExited
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lblDescr.setForeground(java.awt.Color.BLACK);
    }                                                                       //GEN-LAST:event_lblDescrMouseExited

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblDescrMouseEntered(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblDescrMouseEntered
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblDescr.setForeground(java.awt.Color.BLUE);
    }                                                                        //GEN-LAST:event_lblDescrMouseEntered

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblDescrMouseMoved(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblDescrMouseMoved
    }                                                                      //GEN-LAST:event_lblDescrMouseMoved
    /**
     * End of variables declaration.
     *
     * @param  al  DOCUMENT ME!
     */
    public void addActionListener(final ActionListener al) {
        actionListeners.add(al);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  al  DOCUMENT ME!
     */
    public void removeActionListener(final ActionListener al) {
        actionListeners.remove(al);
    }

    /**
     * DOCUMENT ME!
     */
    public void fireDeleteActionPerformed() {
        final Iterator it = actionListeners.iterator();
        final ActionEvent event = new ActionEvent(this, 0, DELETE_ACTION_COMMAND);
        while (it.hasNext()) {
            final Object elem = (Object)it.next();
            if (elem instanceof ActionListener) {
                ((ActionListener)elem).actionPerformed(event);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isDeletable() {
        return deletable;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  deletable  DOCUMENT ME!
     */
    public void setDeletable(final boolean deletable) {
        this.deletable = deletable;
    }
//     public void addDeleteStatements(Vector container) {
//        SimpleDbAction sdba=new SimpleDbAction();
//        sdba.setDescription("Link in >>DMS_URLS<< löschen");
//        sdba.setType(sdba.DELETE);
//        sdba.setStatement("delete from dms_urls where id="+dms_urls_id);
//        container.add(sdba);
//        sdba=new SimpleDbAction();
//        sdba.setDescription("Link in >>DMS_URL<< löschen");
//        sdba.setType(sdba.DELETE);
//        sdba.setStatement("delete from dms_url where id="+dms_url_id);
//        container.add(sdba);
//        sdba=new SimpleDbAction(){
//            public void executeAction(Connection conn) throws SQLException{
//                Statement checker=conn.createStatement();
//                ResultSet check=checker.executeQuery("SELECT count(*) FROM dms_url where url_id="+url_id);
//                check.next();
//                int counter=check.getInt(1);
//                if (counter==0) {
//                    super.executeAction(conn);
//                }
//            }
//
//        };
//        sdba.setDescription("Link in >>URL_BASE<< löschen");
//        sdba.setType(sdba.DELETE);
//        sdba.setStatement("delete from url_base where id="+url_base_id);
//        container.add(sdba);
//        sdba=new SimpleDbAction(){
//            public void executeAction(Connection conn) throws SQLException{
//                Statement checker=conn.createStatement();
//                ResultSet check=checker.executeQuery("SELECT count(*) FROM url where url_base_id="+url_base_id);
//                check.next();
//                int counter=check.getInt(1);
//                if (counter==0) {
//                    super.executeAction(conn);
//                }
//            }
//
//        };
//        sdba.setDescription("Link in >>Url<< löschen");
//        sdba.setType(sdba.DELETE);
//        sdba.setStatement("delete from url where id="+url_id);
//        container.add(sdba);
//    }
//
//     public void addNewStatements(Vector container) {
//        URLSplitter splitter=new URLSplitter(gotoUrl);
//        SimpleDbAction sdba=new SimpleDbAction();
//        sdba.setDescription("LINK in DMS_URLS eintragen");
//        sdba.setType(sdba.INSERT);
//        sdba.setStatement("INSERT INTO dms_urls " +
//                "(id,dms_url,kassenzeichen_reference)" +
//                "VALUES("+
//                "nextval('DMS_URLS_SEQ')" +
//                ","+"nextval('DMS_URL_SEQ')" +
//                ","+kassenzeichen+
//                ")");
//        container.add(sdba);
//
//        sdba=new SimpleDbAction();
//        sdba.setDescription("LINK in DMS_URL eintragen");
//        sdba.setType(sdba.INSERT);
//        sdba.setStatement("INSERT INTO dms_url " +
//                "(id,typ,name,url_id)" +
//                "VALUES("+
//                "currval('DMS_URL_SEQ')" +
//                ",1"+
//                ",'"+this.getDesc()+"'"+
//                ","+"nextval('URL_SEQ')"+
//                ")");
//        container.add(sdba);
//
//        sdba=new SimpleDbAction();
//        sdba.setDescription("LINK in Url eintragen");
//        sdba.setType(sdba.INSERT);
//        sdba.setStatement("INSERT INTO url " +
//                "(id,url_base_id,object_name)" +
//                "VALUES("+
//                "currval('URL_SEQ')" +
//                ",nextval('URL_BASE_SEQ')"+
//                ",'"+splitter.getObject_name().replaceAll("\\\\","\\\\\\\\")+"'"+
//                ")");
//        container.add(sdba);
//        sdba=new SimpleDbAction();
//        sdba.setDescription("LINK in Url eintragen");
//        sdba.setType(sdba.INSERT);
//        sdba.setStatement("INSERT INTO url_base " +
//                "(id,prot_prefix,server,path)" +
//                "VALUES("+
//                "currval('URL_BASE_SEQ')" +
//                ",'"+splitter.getProt_prefix().replaceAll("\\\\","\\\\\\\\")+"'"+
//                ",'"+splitter.getServer().replaceAll("\\\\","\\\\\\\\")+"'"+
//                ",'"+splitter.getPath().replaceAll("\\\\","\\\\\\\\")+"'"+
//                ")");
//        container.add(sdba);
//
//
//     }
//    private void add2Container(Vector container,SimpleDbAction sdba) {
//        if (sdba!=null) {
//            container.add(sdba);
//        }
//    }
//
//    public int getDms_urls_id() {
//        //return dms_urls_id;
//        return dmsUrlEntity.getId();
//    }
//
//    public void setDms_urls_id(int dms_urls_id) {
//        dmsUrlEntity.setId(dms_urls_id);
//    }
//
//    public int getDms_url_id() {
//        //return dms_url_id;
//        return dmsUrlEntity.getId();
//    }
//
//    public void setDms_url_id(int dms_url_id) {
//        //this.dms_url_id = dms_url_id;
//        dmsUrlEntity.setId(dms_url_id);
//    }
//
//    public int getUrl_id() {
//        //return url_id;
//        return dmsUrlEntity.getUrl().getId();
//    }
//
//    public void setUrl_id(int url_id) {
//        //this.url_id = url_id;
//        dmsUrlEntity.getUrl().setId(url_id);
//    }
//
//    public int getUrl_base_id() {
//        //return url_base_id;
//        return dmsUrlEntity.getUrl().getUrlBase().getId();
//    }
//
//    public void setUrl_base_id(int url_base_id) {
//        //this.url_base_id = url_base_id;
//        dmsUrlEntity.getUrl().getUrlBase().setId(url_base_id);
//    }

//    public void setKassenzeichen(String kassenzeichen) {
//        this.kassenzeichen = kassenzeichen;
//    }
//    public String getKassenzeichen() {
//        return kassenzeichen;
//    }
}
