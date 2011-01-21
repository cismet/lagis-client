/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * DMSPanel.java
 *
 * Created on 16. März 2007, 12:00
 */
package de.cismet.lagis.gui.panels;

import org.apache.log4j.Logger;

import java.applet.AppletContext;

import java.awt.FlowLayout;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.gui.tools.DocPanel;

import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.FlurstueckSaver;
import de.cismet.lagis.interfaces.Widget;

import de.cismet.lagis.thread.BackgroundUpdateThread;

import de.cismet.lagis.widget.AbstractWidget;

import de.cismet.lagisEE.entity.core.DmsUrl;
import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.lagisEE.entity.core.Url;
import de.cismet.lagisEE.entity.core.UrlBase;

import de.cismet.tools.URLSplitter;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class DMSPanel extends AbstractWidget implements DropTargetListener, FlurstueckChangeListener, FlurstueckSaver {

    //~ Static fields/initializers ---------------------------------------------

    private static final String WIDGET_NAME = "Dokumenten Panel";

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Flurstueck currentFlurstueck;
    private java.applet.AppletContext ac = null;
    private Collection<DmsUrl> dmsUrls;
    // TODO
    private boolean inEditMode = true;
    private Vector newLinks = new Vector();
    private Vector removedLinks = new Vector();
    private Vector allPanels = new Vector();

    // private Thread panelRefresherThread;
    private BackgroundUpdateThread<Flurstueck> updateThread;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form DMSPanel.
     */
    public DMSPanel() {
        setIsCoreWidget(true);
        initComponents();
        final DropTarget dt = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        updateThread = new BackgroundUpdateThread<Flurstueck>() {

                //J-
                protected void clear() {
                    allPanels.clear();
                    DMSPanel.this.removeAll();
                }
                //J+

                @Override
                protected void update() {
                    try {
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        clearComponent();
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        setCursor(java.awt.Cursor.getDefaultCursor());
                        dmsUrls = getCurrentObject().getDokumente();
                        for (final DmsUrl elem : dmsUrls) {
                            if (this.isInterrupted()) {
                                return;
                            }
                            try {
                                final Url urlEntity = elem.getUrl();
                                final UrlBase urlBase = urlEntity.getUrlBase();
                                final String url = urlBase.getProtPrefix() + urlBase.getServer() + urlBase.getPfad()
                                            + urlEntity.getObjektname();
                                final int typ = elem.getTyp();
                                allPanels.add(addNewDocPanel(ac, elem.getName(), url, typ, elem));
                            } catch (Exception e) {
                                log.error("Fehler beim laden eines Dokumentes", e);
                            }
                        }
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        LagisBroker.getInstance().flurstueckChangeFinished(DMSPanel.this);
                    } catch (Exception ex) {
                        log.error("Fehler im refresh thread: ", ex);
                        LagisBroker.getInstance().flurstueckChangeFinished(DMSPanel.this);
                    }
                }

                @Override
                protected void cleanup() {
                }
            };
        updateThread.setPriority(Thread.NORM_PRIORITY);
        updateThread.start();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void flurstueckChanged(final Flurstueck newFlurstueck) {
        try {
            currentFlurstueck = newFlurstueck;
//            if(panelRefresherThread != null && panelRefresherThread.isAlive()){
//                panelRefresherThread.interrupt();
//            }
            updateThread.notifyThread(currentFlurstueck);
//            panelRefresherThread=new Thread() {
//                public void run() {
//                    clearComponent();
//                    setCursor(java.awt.Cursor.getDefaultCursor());
//                    dmsUrls = newFlurstueck.getDokumente();
//                    for (DmsUrl elem : dmsUrls) {
//                        if(this.isInterrupted()){
//                            return;
//                        }
//                        try{
//                            Url urlEntity = elem.getUrl();
//                            UrlBase urlBase = urlEntity.getUrlBase();
//                            String url =  urlBase.getProtPrefix()+urlBase.getServer()+urlBase.getPfad()+urlEntity.getObjektname();
//                            int typ=elem.getTyp();
//                            addNewDocPanel(ac,elem.getName(),url,typ,elem);
//                        }catch(Exception e){
//                            log.error("Fehler beim laden eines Dokumentes",e);
//                        }
//                    }
//                    if(this.isInterrupted()){
//                        return;
//                    }
//                    //disableComponent(false);
//                    LagisBroker.getInstance().flurstueckChangeFinished(DMSPanel.this);
//                }
//            };
//            panelRefresherThread.setPriority(Thread.NORM_PRIORITY);
//            panelRefresherThread.start();
        } catch (Exception ex) {
            log.error("Fehler beim Flurstueckswechsel im FlurstueckPanel: ", ex);
            LagisBroker.getInstance().flurstueckChangeFinished(DMSPanel.this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ac           DOCUMENT ME!
     * @param   description  DOCUMENT ME!
     * @param   u            DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DocPanel addNewDocPanel(final AppletContext ac, final String description, final String u) {
        if (log.isDebugEnabled()) {
            log.debug("addNewDocPanel Method");
        }
        final DmsUrl dmsUrlEntity = new DmsUrl();
        final Url url = new Url();
        final UrlBase base = new UrlBase();
        url.setUrlBase(base);
        if (log.isDebugEnabled()) {
            log.debug("UrlEntity(dokPanek): " + url);
        }
        dmsUrlEntity.setUrl(url);
        if (log.isDebugEnabled()) {
            log.debug("UrlEntity(dokPanek) ?ber Entity: " + dmsUrlEntity.getUrl());
        }
        return addNewDocPanel(ac, description, u, 1, dmsUrlEntity);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ac           DOCUMENT ME!
     * @param   description  DOCUMENT ME!
     * @param   u            DOCUMENT ME!
     * @param   typ          DOCUMENT ME!
     * @param   dms_URL      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DocPanel addNewDocPanel(final AppletContext ac,
            final String description,
            final String u,
            final int typ,
            final DmsUrl dms_URL) {
        final String url = u;
        log.info("AddNewDocPanel: " + url);
        ImageIcon ic = null;
        boolean deletable = true;
        if (typ == 0) {
            // Setze WMS Icon und h?nge Kassenzeichen an
            ic = new javax.swing.ImageIcon(getClass().getResource(
                        "/de/cismet/lagis/ressource/icons/filetypes/dms_default.png"));
            // TODO
            // url=url+kz;
            deletable = false;
        }
        if (typ == 1) {
            if (log.isDebugEnabled()) {
                // Setze das Icon nach der Dateiendung
                log.debug("suche nach Bild für link");
            }
            final int pPos = url.lastIndexOf(".");
            final String type = url.substring(pPos + 1, url.length()).toLowerCase();
            final String filename = "" + type + ".png";
            if (log.isDebugEnabled()) {
                log.debug("Filename für Bild: " + filename);
            }
            try {
                ic = new javax.swing.ImageIcon(getClass().getResource(
                            "/de/cismet/lagis/ressource/icons/filetypes/"
                                    + filename));
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("Fehler beim Suchen des Icons:" + type);
                }
                ic = new javax.swing.ImageIcon(getClass().getResource(
                            "/de/cismet/lagis/ressource/icons/filetypes/dms_default.png"));
            }
        }

        final DocPanel dp = new DocPanel(dms_URL);
        dp.setAplettContext(ac);
        dp.setDesc(description);
        dp.setGotoUrl(url);
        dp.setIcon(ic);
        dp.setTyp(typ);
        if (log.isDebugEnabled()) {
            log.debug("Typ des neuen DocPanels: " + dp.getTyp());
        }
        // dp.setKassenzeichen(kz);
        dp.setDeletable(true);
        // dp.setDms_url_id(dms_url_id);
        // dp.setDms_urls_id(dms_urls_id);
        // dp.setUrl_id(url_id);
        // dp.setUrl_base_id(url_base_id);
        // dp.setDMSUrlEntity(dmsUrlEntity);
        dp.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (e.getSource() instanceof DocPanel) {
                        if (e.getActionCommand().equals(DocPanel.DELETE_ACTION_COMMAND)) {
                            DMSPanel.this.remove((DocPanel)e.getSource());
                            if (!newLinks.contains((DocPanel)e.getSource())) {
                                removedLinks.add(e.getSource());
                            } else {
                                newLinks.remove((DocPanel)e.getSource());
                            }
                            allPanels.remove(e.getSource());
                            DMSPanel.this.revalidate();
                            repaint();
                        }
                    }
                }
            });
        // EventQueue.invokeLater(new Runnable(){
        // public void run() {
        // panDocs.add(dp);
        // revalidate();
        // }
        // });
        this.add(dp);
        revalidate();
        return dp;
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
        inEditMode = isEditable;
    }

    @Override
    public synchronized void clearComponent() {
        allPanels.clear();
        this.removeAll();
        this.repaint();
    }
    /**
     * Only for compatibility to verdis DokumentenPanel.
     */
    private void emptyPanel() {
        clearComponent();
    }

    @Override
    public void drop(final DropTargetDropEvent dtde) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY);
        final String link = StaticSwingTools.getLinkFromDropEvent(dtde);
        if (link != null) {
            final String description = JOptionPane.showInputDialog(
                    this,
                    "Welche Beschriftung soll der Link haben?",
                    link);
            if (description != null) {
                // TODO
                final DocPanel dp = addNewDocPanel(ac, description, link);
                newLinks.add(dp);
                allPanels.add(dp);
                this.repaint();
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
        if (!inEditMode) {
            dtde.rejectDrag();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        final org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(0, 400, Short.MAX_VALUE));
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(0, 229, Short.MAX_VALUE));
    } // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    /**
     * DOCUMENT ME!
     *
     * @param  ac  DOCUMENT ME!
     */
    public void setAppletContext(final java.applet.AppletContext ac) {
        this.ac = ac;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isInEditMode() {
        return inEditMode;
    }

    @Override
    public void refresh(final Object refreshObject) {
    }

    @Override
    public String getWidgetName() {
        return WIDGET_NAME;
    }

    @Override
    public void updateFlurstueckForSaving(final Flurstueck flurstueck) {
        if (log.isDebugEnabled()) {
            log.debug("Dokumente werden gespeichert");
        }
        final Set<DmsUrl> vDMSUrls = flurstueck.getDokumente();
        if (vDMSUrls != null) {
            vDMSUrls.clear();
            if (log.isDebugEnabled()) {
                // TODO Duplicated code
                log.debug("allPanels size: " + allPanels.size());
            }
            for (final Object next : allPanels) {
                if (next instanceof DocPanel) {
                    try {
                        final DocPanel doc = (DocPanel)next;
                        vDMSUrls.add(doc.getDMSUrlEntity());
//                        DmsUrl tmpDMS = new DmsUrl();
//                        tmpDMS.setName(doc.getDesc());
//                        tmpDMS.setTyp(1);
//                        UrlBase tmpBase = new UrlBase();
//                        URLSplitter splitter=new URLSplitter(doc.getGotoUrl());
//                        tmpBase.setPfad(splitter.getPath().replaceAll("\\\\","\\\\\\\\"));
//                        tmpBase.setProtPrefix(splitter.getProt_prefix().replaceAll("\\\\","\\\\\\\\"));
//                        tmpBase.setServer(splitter.getServer().replaceAll("\\\\","\\\\\\\\"));
//                        Url tmpUrl = new Url();
//                        tmpUrl.setUrlBase(tmpBase);
//                        tmpUrl.setObjektname(splitter.getObject_name().replaceAll("\\\\","\\\\\\\\"));
//                        tmpDMS.setUrl(tmpUrl);
//                        vDMSUrls.add(tmpDMS);
                    } catch (Exception ex) {
                        log.warn("Fehler beim speichern eines Dokumentes", ex);
                    }
                }
            }
//             for(Object next:removedLinks){
//                if(next instanceof DocPanel){
//                     try{
//                        DocPanel doc = (DocPanel)next;
//                        vDMSUrls.remove(doc.getDMSUrlEntity());
//                     }catch(Exception ex){
//                        log.warn("Fehler beim löschen eines Dokumentes",ex);
//                    }
//                }
//                }
        } else {
            final HashSet newSet = new HashSet();
            for (final Object next : allPanels) {
                if (next instanceof DocPanel) {
                    try {
                        final DocPanel doc = (DocPanel)next;
                        vDMSUrls.add(doc.getDMSUrlEntity());
//                        DmsUrl tmpDMS = new DmsUrl();
//                        tmpDMS.setName(doc.getDesc());
//                        tmpDMS.setTyp(1);
//                        UrlBase tmpBase = new UrlBase();
//                        URLSplitter splitter=new URLSplitter(doc.getGotoUrl());
//                        tmpBase.setPfad(splitter.getPath().replaceAll("\\\\","\\\\\\\\"));
//                        tmpBase.setProtPrefix(splitter.getProt_prefix().replaceAll("\\\\","\\\\\\\\"));
//                        tmpBase.setServer(splitter.getServer().replaceAll("\\\\","\\\\\\\\"));
//                        Url tmpUrl = new Url();
//                        tmpUrl.setUrlBase(tmpBase);
//                        tmpUrl.setObjektname(splitter.getObject_name().replaceAll("\\\\","\\\\\\\\"));
//                        tmpDMS.setUrl(tmpUrl);
//                        newSet.add(tmpDMS);
                    } catch (Exception ex) {
                        log.warn("Fehler beim speichern eines Dokumentes", ex);
                    }
                }
            }
            flurstueck.setVerwaltungsbereiche(newSet);
        }
        allPanels = new Vector();
    }

    // TODO USE
    @Override
    public Icon getWidgetIcon() {
        return null;
    }
}
