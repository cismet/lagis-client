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

import java.awt.FlowLayout;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.beans.lagis.DmsUrlCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.lagis.UrlBaseCustomBean;
import de.cismet.cids.custom.beans.lagis.UrlCustomBean;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.gui.optionspanels.DmsUrlPathMapper;
import de.cismet.lagis.gui.tools.DocPanel;

import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.FlurstueckSaver;

import de.cismet.lagis.util.LagISUtils;

import de.cismet.lagis.widget.AbstractWidget;

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
    private Collection<DmsUrlCustomBean> dmsUrls;
    // TODO
    private boolean inEditMode = true;
    private final Collection<DocPanel> newLinks = new ArrayList<>();
    private final Collection<DocPanel> removedLinks = new ArrayList<>();
    private final Collection<DocPanel> allPanels = new ArrayList<>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form DMSPanel.
     */
    public DMSPanel() {
        setIsCoreWidget(true);
        initComponents();
        final DropTarget dt = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void flurstueckChanged(final FlurstueckCustomBean newFlurstueck) {
        try {
            allPanels.clear();
            removeAll();
            clearComponent();
            setCursor(java.awt.Cursor.getDefaultCursor());
            dmsUrls = newFlurstueck.getDokumente();
            for (final DmsUrlCustomBean elem : dmsUrls) {
                try {
                    allPanels.add(addNewDocPanel(elem));
                } catch (Exception e) {
                    log.error("Fehler beim laden eines Dokumentes", e);
                }
            }

//            panelRefresherThread=new Thread() {
//                public void run() {
//                    clearComponent();
//                    setCursor(java.awt.Cursor.getDefaultCursor());
//                    dmsUrls = newFlurstueck.getDokumente();
//                    for (DmsUrlCustomBean elem : dmsUrls) {
//                        if(this.isInterrupted()){
//                            return;
//                        }
//                        try{
//                            UrlCustomBean urlEntity = elem.getUrl();
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
        } catch (final Exception ex) {
            log.error("Fehler beim Flurstueckswechsel im FlurstueckPanel: ", ex);
        } finally {
            LagisBroker.getInstance().flurstueckChangeFinished(DMSPanel.this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url          DOCUMENT ME!
     * @param   description  DOCUMENT ME!
     * @param   typ          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static DmsUrlCustomBean createNewDmsUrl(final String url, final String description, final int typ) {
        final URLSplitter splitter = new URLSplitter(url);

        final DmsUrlCustomBean dmsUrlBean = DmsUrlCustomBean.createNew();
        final UrlBaseCustomBean urlBaseBean = UrlBaseCustomBean.createNew();
        final UrlCustomBean urlBean = UrlCustomBean.createNew();

        dmsUrlBean.setName(description);
        dmsUrlBean.setTyp(typ);

        urlBaseBean.setPfad(splitter.getPath());
        urlBaseBean.setProtPrefix(splitter.getProt_prefix());
        urlBaseBean.setServer(splitter.getServer());

        urlBean.setObjektname(splitter.getObject_name());

        urlBean.setUrlBase(urlBaseBean);
        dmsUrlBean.setUrl(urlBean);
        return dmsUrlBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   dmsUrlEntity  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DocPanel addNewDocPanel(final DmsUrlCustomBean dmsUrlEntity) {
        final DocPanel dp = new DocPanel(dmsUrlEntity);
        if (log.isDebugEnabled()) {
            log.debug("Typ des neuen DocPanels: " + dp.getTyp());
        }
        dp.setDeletable(true);
        dp.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (e.getSource() instanceof DocPanel) {
                        final DocPanel docPanel = (DocPanel)e.getSource();
                        if (e.getActionCommand().equals(DocPanel.DELETE_ACTION_COMMAND)) {
                            DMSPanel.this.remove(docPanel);
                            if (!newLinks.contains(docPanel)) {
                                removedLinks.add(docPanel);
                            } else {
                                newLinks.remove(docPanel);
                            }
                            allPanels.remove(docPanel);
                            DMSPanel.this.revalidate();
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
        final String url = StaticSwingTools.getLinkFromDropEvent(dtde);
        if (url != null) {
            final String description = JOptionPane.showInputDialog(
                    this,
                    "Welche Beschriftung soll der Link haben?",
                    url);
            if (description != null) {
                final DocPanel dp = addNewDocPanel(createNewDmsUrl(
                            DmsUrlPathMapper.getInstance().replaceLocalPath(url),
                            description,
                            1));
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
    public void updateFlurstueckForSaving(final FlurstueckCustomBean flurstueck) {
        if (log.isDebugEnabled()) {
            log.debug("Dokumente werden gespeichert");
        }
        final Collection<DmsUrlCustomBean> vDMSUrls = flurstueck.getDokumente();
        final Collection<DmsUrlCustomBean> panColl = new ArrayList<DmsUrlCustomBean>(allPanels.size());
        for (final Object next : allPanels) {
            if (next instanceof DocPanel) {
                final DocPanel doc = (DocPanel)next;
                final DmsUrlCustomBean urlBean = doc.getDMSUrlEntity();
                panColl.add(urlBean);
            }
        }
        LagISUtils.makeCollectionContainSameAsOtherCollection(vDMSUrls, panColl);
        allPanels.clear();
    }

    // TODO USE
    @Override
    public Icon getWidgetIcon() {
        return null;
    }
}
