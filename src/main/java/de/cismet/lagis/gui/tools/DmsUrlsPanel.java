/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.gui.tools;

import org.apache.log4j.Logger;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JOptionPane;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.URLSplitter;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class DmsUrlsPanel extends javax.swing.JPanel implements DropTargetListener, ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(DmsUrlsPanel.class);

    //~ Instance fields --------------------------------------------------------

    private final Collection<DmsUrlPanel> allPanels = new ArrayList<>();
    private Collection<CidsBean> dmsUrls;
    private ConnectionContext connectionContext;
    private final String domain;

    //~ Constructors -----------------------------------------------------------

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    /**
     * Creates a new DmsUrlsPanel object.
     */
    public DmsUrlsPanel() {
        this(null);
    }

    /**
     * Creates new form DmsUrlsPanel.
     *
     * @param  domain  DOCUMENT ME!
     */
    public DmsUrlsPanel(final String domain) {
        this.domain = domain;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDomain() {
        return domain;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url          DOCUMENT ME!
     * @param   description  DOCUMENT ME!
     * @param   typ          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private CidsBean createNewDmsUrl(final String url, final String description, final int typ) throws Exception {
        final URLSplitter splitter = new URLSplitter(url);

        final CidsBean dmsUrlBean = CidsBean.createNewCidsBeanFromTableName(
                getDomain(),
                "dms_url",
                getConnectionContext());
        final CidsBean urlBaseBean = CidsBean.createNewCidsBeanFromTableName(
                getDomain(),
                "url_base",
                getConnectionContext());
        final CidsBean urlBean = CidsBean.createNewCidsBeanFromTableName(getDomain(), "url", getConnectionContext());

        dmsUrlBean.setProperty("name", description);
        dmsUrlBean.setProperty("typ", typ);

        urlBaseBean.setProperty("path", splitter.getPath());
        urlBaseBean.setProperty("prot_prefix", splitter.getProt_prefix());
        urlBaseBean.setProperty("server", splitter.getServer());

        urlBean.setProperty("object_name", splitter.getObject_name());

        urlBean.setProperty("url_base_id", urlBaseBean);
        dmsUrlBean.setProperty("url", urlBean);
        return dmsUrlBean;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  dmsUrls  DOCUMENT ME!
     */
    public void setDmsUrls(final Collection<CidsBean> dmsUrls) {
        this.dmsUrls = dmsUrls;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<CidsBean> getDmsUrls() {
        return dmsUrls;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   dmsUrlBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DmsUrlPanel addDmsUrl(final CidsBean dmsUrlBean) {
        final DmsUrlPanel dp = new DmsUrlPanel(dmsUrlBean);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Typ des neuen DocPanels: " + dp.getTyp());
        }
        dp.setDeletable(true);
        dp.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (e.getSource() instanceof DmsUrlPanel) {
                        final DmsUrlPanel docPanel = (DmsUrlPanel)e.getSource();
                        if (e.getActionCommand().equals(DmsUrlPanel.DELETE_ACTION_COMMAND)) {
                            DmsUrlsPanel.this.remove(docPanel);
                            allPanels.remove(docPanel);
                            DmsUrlsPanel.this.revalidate();
                            repaint();
                        }
                    }
                }
            });
        this.add(dp);
        revalidate();
        return dp;
    }

    @Override
    public void drop(final DropTargetDropEvent dtde) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY);
        final String url = StaticSwingTools.getLinkFromDropEvent(dtde);
        if (url != null) {
            final String description = JOptionPane.showInputDialog(
                    this,
                    "Welche Beschriftung soll der Link haben?",
                    url);
            if (description != null) {
                try {
                    final DmsUrlPanel dp = addDmsUrl(createNewDmsUrl(
                                DmsUrlPathMapper.getInstance().replaceLocalPath(url),
                                description,
                                1));
                    allPanels.add(dp);
                    this.repaint();
                } catch (final Exception ex) {
                    LOG.error(ex, ex);
                }
            }
        }
    }

    @Override
    public void dragExit(final DropTargetEvent dte) {
    }

    @Override
    public void dropActionChanged(final DropTargetDragEvent dtde) {
    }

    @Override
    public void dragOver(final DropTargetDragEvent dtde) {
    }

    @Override
    public void dragEnter(final DropTargetDragEvent dtde) {
        if (!isEnabled()) {
            dtde.rejectDrag();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void refresh() {
        allPanels.clear();
        if (dmsUrls != null) {
            for (final CidsBean dmsUrl : dmsUrls) {
                try {
                    allPanels.add(addDmsUrl(dmsUrl));
                } catch (Exception e) {
                    LOG.error("Fehler beim laden eines Dokumentes", e);
                }
            }
        }
        repaint();
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();
        final DropTarget dt = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
