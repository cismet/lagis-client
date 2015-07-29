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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.beans.lagis.DmsUrlCustomBean;

import de.cismet.lagis.gui.optionspanels.DmsUrlOptionsPanel;
import de.cismet.lagis.gui.optionspanels.DmsUrlPathMapper;
import de.cismet.lagis.gui.panels.DMSPanel;

/**
 * Klasse zum Anzeigen von Links und zugehörigen Icons in einer Anwendung.<br>
 * Bei Klick wird die UrlCustomBean im Webbrowser geöffnet.
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class DocPanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    public static final int MAX_DESCRIPTION_LENGTH = 12;
    public static final String DELETE_ACTION_COMMAND = "DELETE_ACTION";
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocPanel.class);

    //~ Instance fields --------------------------------------------------------

    private final Collection<ActionListener> actionListeners = new ArrayList<ActionListener>();
    private boolean deletable = false;
    private DmsUrlCustomBean dmsUrlEntity;

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
    public DocPanel(final DmsUrlCustomBean dmsUrlEntity) {
        initComponents();

        this.dmsUrlEntity = dmsUrlEntity;

        initDescription(dmsUrlEntity.getName());
        initIcon(makeIcon(dmsUrlEntity), dmsUrlEntity.getUrlString());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   dmsUrlEntity  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static ImageIcon makeIcon(final DmsUrlCustomBean dmsUrlEntity) {
        final ImageIcon icon;
        switch (dmsUrlEntity.getTyp()) {
            case 0: {
                // Collectionze WMS Icon und h?nge Kassenzeichen an
                icon = new javax.swing.ImageIcon(DocPanel.class.getResource(
                            "/de/cismet/lagis/ressource/icons/filetypes/dms_default.png"));
            }
            break;
            case 1: {
                if (LOG.isDebugEnabled()) {
                    // Collectionze das Icon nach der Dateiendung
                    LOG.debug("suche nach Bild für link");
                }
                final String url = dmsUrlEntity.getUrlString();
                final int pPos = url.lastIndexOf(".");
                final String type = url.substring(pPos + 1, url.length()).toLowerCase();
                final String filename = "" + type + ".png";
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Filename für Bild: " + filename);
                }
                ImageIcon tryIcon;
                try {
                    tryIcon = new javax.swing.ImageIcon(DocPanel.class.getResource(
                                "/de/cismet/lagis/ressource/icons/filetypes/"
                                        + filename));
                } catch (Exception e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Fehler beim Suchen des Icons:" + type);
                    }
                    tryIcon = new javax.swing.ImageIcon(DocPanel.class.getResource(
                                "/de/cismet/lagis/ressource/icons/filetypes/dms_default.png"));
                }
                icon = tryIcon;
            }
            break;
            default: {
                icon = null;
            }
        }
        return icon;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dmsUrlEntity  DOCUMENT ME!
     */
    public void setDMSUrlEntity(final DmsUrlCustomBean dmsUrlEntity) {
        this.dmsUrlEntity = dmsUrlEntity;
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
    public DmsUrlCustomBean getDMSUrlEntity() {
        return dmsUrlEntity;
    }

    /**
     * Setzt das dargestellte Symbol.
     *
     * @param  icon     Dargestelltes Symbol
     * @param  tooltip  DOCUMENT ME!
     */
    private void initIcon(final Icon icon, final String tooltip) {
        lblIcon.setIcon(icon);
        lblIcon.setToolTipText(tooltip);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  desc  DOCUMENT ME!
     */
    private void initDescription(final String desc) {
        if (desc.length() > MAX_DESCRIPTION_LENGTH) {
            this.lblDescr.setText(desc.substring(0, MAX_DESCRIPTION_LENGTH) + "...");
            this.lblDescr.setToolTipText(desc);
        } else {
            this.lblDescr.setText(desc);
        }
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("mouse pressed");
        }
        if ((evt.getButton() == evt.BUTTON3) && isDeletable()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("button3 && isDeletable");
            }
            // TODO WARUM NUR EIN PANEL;
            if (this.getParent() instanceof DMSPanel) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("isDMSPANEL");
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
        final String urlString = dmsUrlEntity.getUrlString();
        if (urlString == null) {
            JOptionPane.showMessageDialog(this, "Es wurde keine Url hinterlegt!", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final String gotoUrl = DmsUrlPathMapper.getInstance().replaceNetworkPath(urlString);
        try {
            de.cismet.tools.BrowserLauncher.openURL(gotoUrl);
        } catch (Exception e) {
            LOG.warn("Fehler beim öffnen von:" + gotoUrl + "\nNeuer Versuch", e);
            // Nochmal zur Sicherheit mit dem BrowserLauncher probieren
            try {
                de.cismet.tools.BrowserLauncher.openURL(gotoUrl);
            } catch (final Exception e2) {
                final String newGotoUrl = gotoUrl.replaceAll("\\\\", "/").replaceAll(" ", "%20");
                try {
                    LOG.warn("Auch das 2te Mal ging schief.Fehler beim öffnen von:" + newGotoUrl + "\nLetzter Versuch",
                        e2);
                    de.cismet.tools.BrowserLauncher.openURL("file:///" + newGotoUrl);
                } catch (Exception e3) {
                    LOG.error("Auch das 3te Mal ging schief.Fehler beim öffnen von:file://" + newGotoUrl, e3);
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
}
