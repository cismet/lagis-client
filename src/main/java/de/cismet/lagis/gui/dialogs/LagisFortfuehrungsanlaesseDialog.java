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
package de.cismet.lagis.gui.dialogs;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import java.awt.Component;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.alkisfortfuehrung.FortfuehrungItem;
import de.cismet.cids.custom.alkisfortfuehrung.FortfuehrungenTableModel;
import de.cismet.cids.custom.alkisfortfuehrung.FortfuehrungsanlaesseDialog;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;
import de.cismet.cids.custom.beans.lagis.GemarkungCustomBean;
import de.cismet.cids.custom.wunda_blau.search.server.BufferingGeosearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.cismap.commons.CrsTransformer;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.commons.FortfuehrungPropertyConstants;
import de.cismet.lagis.commons.LagisConstants;
import de.cismet.lagis.commons.LagisMetaclassConstants;

import de.cismet.lagis.gui.main.LagisApp;
import de.cismet.lagis.gui.panels.FlurstueckChooser;

import de.cismet.lagis.interfaces.DoneDelegate;

import de.cismet.lagis.server.search.LagisFortfuehrungItemSearch;

import de.cismet.lagis.thread.ExtendedSwingWorker;
import de.cismet.lagis.thread.WFSRetrieverFactory;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class LagisFortfuehrungsanlaesseDialog extends FortfuehrungsanlaesseDialog {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(LagisFortfuehrungsanlaesseDialog.class);
    private static final HashMap<Integer, GemarkungCustomBean> GEMARKUNGEN_MAP = CidsBroker.getInstance()
                .getGemarkungsHashMap();
    private static final Map<Geometry, Collection<FlurstueckSchluesselCustomBean>> geomBeansMap = new HashMap<>();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbxAbgearbeitet;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList lstFlurstuecke;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form VerdisFortfuehrungsanlaesseDialog.
     *
     * @param  parent  DOCUMENT ME!
     * @param  modal   DOCUMENT ME!
     */
    public LagisFortfuehrungsanlaesseDialog(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal, ConnectionContext.createDeprecated());
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected JPanel getObjectsPanel() {
        if (jPanel1 == null) {
            initComponents();
        }
        return jPanel1;
    }

    @Override
    protected String getLinkFormat() {
        return LagisApp.getInstance().getFortfuehrungLinkFormat();
    }

    @Override
    protected void refreshFortfuehrungsList() {
        geomBeansMap.clear();
        super.refreshFortfuehrungsList();
    }

    /**
     * DOCUMENT ME!
     */
    private void gotoSelectedFlurstueck() {
        final FlurstueckSchluesselCustomBean flurstueckSchluessel = (FlurstueckSchluesselCustomBean)
            lstFlurstuecke.getSelectedValue();
        LagisBroker.getInstance().loadFlurstueck(flurstueckSchluessel);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueckSchluessels  DOCUMENT ME!
     */
    @Override
    protected void setObjects(final Collection flurstueckSchluessels) {
        if (flurstueckSchluessels == null) {
            cbxAbgearbeitet.setSelected(false);
        } else {
            final DefaultListModel flurstueckSchluesselListModel = (DefaultListModel)lstFlurstuecke.getModel();
            flurstueckSchluesselListModel.removeAllElements();

            for (final FlurstueckSchluesselCustomBean alkisId
                        : (Collection<FlurstueckSchluesselCustomBean>)flurstueckSchluessels) {
                flurstueckSchluesselListModel.addElement(alkisId);
            }
        }
    }

    @Override
    protected void setDetailEnabled(final boolean enabled) {
        lstFlurstuecke.setEnabled(enabled);
        cbxAbgearbeitet.setEnabled(enabled);
    }

    @Override
    protected FortfuehrungItem createFortfuehrungItem(final Object[] rawItem) {
        return new FortfuehrungItem((Integer)rawItem[LagisFortfuehrungItemSearch.FIELD_ID],
                (String)rawItem[LagisFortfuehrungItemSearch.FIELD_FFN],
                (String)rawItem[LagisFortfuehrungItemSearch.FIELD_ANLASSNAME],
                (Date)rawItem[LagisFortfuehrungItemSearch.FIELD_BEGINN],
                (String)rawItem[LagisFortfuehrungItemSearch.FIELD_FS_ALT],
                (String)rawItem[LagisFortfuehrungItemSearch.FIELD_FS_NEU],
                (Integer)rawItem[LagisFortfuehrungItemSearch.FIELD_FORTFUEHRUNG_ID]);
    }

    @Override
    protected void searchObjects(final Geometry geom) {
        if (geom == null) {
            lstFlurstuecke.setEnabled(false);
            cbxAbgearbeitet.setEnabled(false);
        } else {
            final FortfuehrungItem selectedFortfuehrungItem = getSelectedFortfuehrungItem();

            new SwingWorker<Collection<FlurstueckSchluesselCustomBean>, Void>() {

                    @Override
                    protected Collection<FlurstueckSchluesselCustomBean> doInBackground() throws Exception {
                        if (!geomBeansMap.containsKey(geom)) {
                            final int currentSrid = CrsTransformer.getCurrentSrid();
                            final Geometry searchGeom = geom.buffer(0);
                            searchGeom.setSRID(currentSrid);
                            final BufferingGeosearch search = new BufferingGeosearch();
                            final MetaClass mc = CidsBean.getMetaClassFromTableName("WUNDA_BLAU", "alkis_landparcel");
                            search.setValidClasses(Arrays.asList(mc));
                            search.setGeometry(searchGeom);

                            final Collection<MetaObjectNode> res = (Collection<MetaObjectNode>)CidsBroker
                                        .getInstance().search(search);

                            final List<String> alkisIds = new ArrayList<>();

                            final FortfuehrungenTableModel model = getTableModel();
                            for (int index = 0; index < model.getRowCount(); index++) {
                                final FortfuehrungItem item = model.getItem(index);
                                if ((item != null) && (item.getFfn() != null)
                                            && item.getFfn().equals(selectedFortfuehrungItem.getFfn())) {
                                    final String flurstueckAlt = item.getFlurstueckAlt().replaceAll("_", "0");
                                    final String gemarkungAlt = flurstueckAlt.substring(0, 6);
                                    final String flurAlt = flurstueckAlt.substring(6, 9);
                                    final String zaehlerAlt = flurstueckAlt.substring(9, 14);
                                    final String nennerAlt = flurstueckAlt.substring(14);
                                    alkisIds.add(gemarkungAlt + "-" + flurAlt + "-" + zaehlerAlt
                                                + ((Integer.parseInt(nennerAlt) > 0) ? ("/" + nennerAlt) : ""));
                                }
                            }

                            for (final MetaObjectNode mon : res) {
                                alkisIds.add(mon.toString());
                            }

                            final Set<FlurstueckSchluesselCustomBean> flurstueckSchluesselBeans = new HashSet<>();

                            for (final String alkisId : alkisIds) {
                                final String[] alkisIdParts = alkisId.split("-");
                                final Integer gemarkung = Integer.parseInt(alkisIdParts[0].substring(2));
                                final Integer flur = Integer.parseInt(alkisIdParts[1]);
                                final String[] zaehlerNennerParts = alkisIdParts[2].split("/");
                                final Integer zaehler = Integer.parseInt(zaehlerNennerParts[0]);
                                final Integer nenner = (zaehlerNennerParts.length > 1)
                                    ? Integer.parseInt(zaehlerNennerParts[1]) : 0;

                                final MetaClass metaClass = CidsBroker.getInstance()
                                            .getLagisMetaClass("flurstueck_schluessel");
                                final String query = "SELECT "
                                            + "   " + metaClass.getID() + ", "
                                            + "   " + metaClass.getTableName() + ".id "
                                            + "FROM " + metaClass.getTableName() + " "
                                            + "WHERE "
                                            + "   " + metaClass.getTableName()
                                            + ".fk_gemarkung = (SELECT id FROM gemarkung WHERE schluessel = "
                                            + gemarkung
                                            + ") AND "
                                            + "   " + metaClass.getTableName() + ".flur = " + flur + " AND "
                                            + "   " + metaClass.getTableName() + ".flurstueck_zaehler = " + zaehler
                                            + " AND "
                                            + "   " + metaClass.getTableName() + ".flurstueck_nenner = " + nenner + ""
                                            + ";";
                                final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);

                                if ((mos != null) && (mos.length > 0)) {
                                    flurstueckSchluesselBeans.add((FlurstueckSchluesselCustomBean)mos[0].getBean());
                                } else {
                                    final FlurstueckSchluesselCustomBean flurstueckSchluesselBean =
                                        FlurstueckSchluesselCustomBean.createNew();
                                    flurstueckSchluesselBean.setGemarkung(GEMARKUNGEN_MAP.get(gemarkung));
                                    flurstueckSchluesselBean.setFlur(flur);
                                    flurstueckSchluesselBean.setFlurstueckZaehler(zaehler);
                                    flurstueckSchluesselBean.setFlurstueckNenner(nenner);

                                    final SwingWorker sw = WFSRetrieverFactory.getInstance()
                                                .getWFSRetriever(
                                                    flurstueckSchluesselBean,
                                                    new DoneDelegate() {

                                                        @Override
                                                        public void jobDone(final ExtendedSwingWorker worker,
                                                                final HashMap properties) {
                                                            flurstueckSchluesselBeans.add(
                                                                (FlurstueckSchluesselCustomBean)worker.getKeyObject());
                                                        }
                                                    },
                                                    new HashMap<Integer, Boolean>());
                                    sw.execute();
                                    sw.get();
                                }
                            }

                            geomBeansMap.put(geom, flurstueckSchluesselBeans);
                        }

                        return geomBeansMap.get(geom);
                    }

                    @Override
                    protected void done() {
                        try {
                            final Collection<FlurstueckSchluesselCustomBean> flurstueckSchluessels = new ArrayList<>();
                            flurstueckSchluessels.addAll(get());
                            setDetailEnabled(true);
                            setObjects(flurstueckSchluessels);

                            cbxAbgearbeitet.setSelected(selectedFortfuehrungItem.isIst_abgearbeitet());

                            final String ffn = selectedFortfuehrungItem.getFfn();
                            final Calendar cal = new GregorianCalendar();
                            cal.setTime(selectedFortfuehrungItem.getBeginn());
                            final int year = cal.get(Calendar.YEAR);

                            final String urlFormat = getLinkFormat();
                            final String urlString = String.format(
                                    urlFormat,
                                    year,
                                    ffn.substring(2, 6),
                                    ffn.substring(6, 11));
                            setDokumentLink(urlString);
                        } catch (final Exception ex) {
                            setObjects(null);
                            cbxAbgearbeitet.setSelected(false);
                            LOG.fatal("", ex);
                        }
                        lstFlurstuecke.setEnabled(true);
                        cbxAbgearbeitet.setEnabled(true);
                        searchDone();
                    }
                }.execute();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstFlurstuecke = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        cbxAbgearbeitet = new javax.swing.JCheckBox();

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    LagisFortfuehrungsanlaesseDialog.class,
                    "LagisFortfuehrungsanlaesseDialog.jPanel4.border.title"))); // NOI18N
        jPanel4.setLayout(new java.awt.GridBagLayout());

        lstFlurstuecke.setModel(new DefaultListModel());
        lstFlurstuecke.setCellRenderer(new FlurstueckSchluesselListCellRenderer());
        lstFlurstuecke.setEnabled(false);
        lstFlurstuecke.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lstFlurstueckeMouseClicked(evt);
                }
            });
        lstFlurstuecke.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstFlurstueckeValueChanged(evt);
                }
            });
        jScrollPane2.setViewportView(lstFlurstuecke);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel4.add(jScrollPane2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                LagisFortfuehrungsanlaesseDialog.class,
                "LagisFortfuehrungsanlaesseDialog.jButton1.text")); // NOI18N
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        jPanel4.add(jButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel4, gridBagConstraints);
        jPanel4.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        LagisFortfuehrungsanlaesseDialog.class,
                        "LagisFortfuehrungsanlaesseDialog.jPanel4.AccessibleContext.accessibleName")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(
            cbxAbgearbeitet,
            org.openide.util.NbBundle.getMessage(
                LagisFortfuehrungsanlaesseDialog.class,
                "LagisFortfuehrungsanlaesseDialog.cbxAbgearbeitet.text")); // NOI18N
        cbxAbgearbeitet.setEnabled(false);
        cbxAbgearbeitet.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbxAbgearbeitetActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel1.add(cbxAbgearbeitet, gridBagConstraints);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstFlurstueckeMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstFlurstueckeMouseClicked
        if (evt.getClickCount() == 2) {
            if (lstFlurstuecke.getSelectedValue() != null) {
                gotoSelectedFlurstueck();
            }
        }
    }                                                                              //GEN-LAST:event_lstFlurstueckeMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstFlurstueckeValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstFlurstueckeValueChanged
        jButton1.setEnabled(!lstFlurstuecke.getSelectionModel().isSelectionEmpty());
    }                                                                                         //GEN-LAST:event_lstFlurstueckeValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        gotoSelectedFlurstueck();
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbxAbgearbeitetActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbxAbgearbeitetActionPerformed
        final FortfuehrungItem selectedFortfuehrungItem = getSelectedFortfuehrungItem();

        final boolean istAbgearbeitet = cbxAbgearbeitet.isSelected();
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        if (istAbgearbeitet) {
                            final CidsBean fortfuehrungBean = CidsBean.createNewCidsBeanFromTableName(
                                    LagisConstants.DOMAIN_LAGIS,
                                    LagisMetaclassConstants.MC_FORTFUEHRUNG);
                            fortfuehrungBean.setProperty(
                                FortfuehrungPropertyConstants.PROP__ALKIS_FFN_ID,
                                selectedFortfuehrungItem.getAnlassId());
                            fortfuehrungBean.setProperty(
                                FortfuehrungPropertyConstants.PROP__ALKIS_FFN,
                                selectedFortfuehrungItem.getFfn());
                            fortfuehrungBean.setProperty(
                                FortfuehrungPropertyConstants.PROP__ABGEARBEITET_AM,
                                new Timestamp(new Date().getTime()));
                            fortfuehrungBean.setProperty(
                                FortfuehrungPropertyConstants.PROP__ABGEARBEITET_VON,
                                SessionManager.getSession().getUser().getName());
                            final CidsBean persisted = fortfuehrungBean.persist();
                            selectedFortfuehrungItem.setFortfuehrungId(persisted.getMetaObject().getId());
                        } else {
                            final MetaClass mc = CidsBroker.getInstance()
                                        .getLagisMetaClass(LagisMetaclassConstants.MC_FORTFUEHRUNG);
                            final CidsBean fortfuehrungBean = CidsBroker.getInstance()
                                        .getLagisMetaObject(selectedFortfuehrungItem.getFortfuehrungId(), mc.getId())
                                        .getBean();
                            fortfuehrungBean.delete();
                            fortfuehrungBean.persist();
                            selectedFortfuehrungItem.setFortfuehrungId(null);
                        }
                    } catch (Exception ex) {
                        LOG.error("fehler beim setzen von ist_abgearbeitet", ex);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    refreshFortfuehrungsList();
                }
            }.execute();
    } //GEN-LAST:event_cbxAbgearbeitetActionPerformed

    @Override
    protected CidsServerSearch createFortfuehrungItemSearch(final Date fromDate, final Date toDate) {
        return new LagisFortfuehrungItemSearch(fromDate, toDate);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static LagisFortfuehrungsanlaesseDialog getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final LagisFortfuehrungsanlaesseDialog INSTANCE = new LagisFortfuehrungsanlaesseDialog(LagisApp
                        .getInstance(),
                false);
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class FlurstueckSchluesselListCellRenderer extends DefaultListCellRenderer {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList<?> list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final FlurstueckSchluesselCustomBean flurstueckSchluessel = (FlurstueckSchluesselCustomBean)value;
            final JLabel component = (JLabel)super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus);
            component.setIcon(FlurstueckChooser.getIcon(FlurstueckChooser.identifyStatus(flurstueckSchluessel)));
            component.setText(flurstueckSchluessel.getKeyString());
            return component;
        }
    }
}
