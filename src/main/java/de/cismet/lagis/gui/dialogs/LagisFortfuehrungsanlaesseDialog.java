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
import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.alkisfortfuehrung.FortfuehrungItem;
import de.cismet.cids.custom.alkisfortfuehrung.FortfuehrungsanlaesseDialog;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;
import de.cismet.cids.custom.beans.lagis.GemarkungCustomBean;
import de.cismet.cids.custom.wunda_blau.search.server.BufferingGeosearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.cismap.commons.CrsTransformer;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.commons.FortfuehrungPropertyConstants;
import de.cismet.lagis.commons.LagisConstants;
import de.cismet.lagis.commons.LagisMetaclassConstants;

import de.cismet.lagis.gui.main.LagisApp;

import de.cismet.lagis.server.search.LagisFortfuehrungItemSearch;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class LagisFortfuehrungsanlaesseDialog extends FortfuehrungsanlaesseDialog {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(LagisFortfuehrungsanlaesseDialog.class);

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
        super(parent, modal);
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

    /**
     * DOCUMENT ME!
     */
    private void gotoSelectedFlurstueck() {
        final String alkisId = (String)lstFlurstuecke.getSelectedValue();

        final String[] alkisIdParts = alkisId.split("-");
        final String gemarkung = alkisIdParts[0].substring(2);
        final String flur = alkisIdParts[1];
        final String[] zaehlerNennerParts = alkisIdParts[2].split("/");
        final String zaehler = zaehlerNennerParts[0];
        final String nenner = (zaehlerNennerParts.length > 1) ? zaehlerNennerParts[1] : null;
        if ((gemarkung != null) && (flur != null) && (zaehler != null)) {
            GemarkungCustomBean resolvedGemarkung = LagisBroker.getInstance()
                        .getGemarkungForKey(Integer.parseInt(gemarkung));
            if (resolvedGemarkung == null) {
                resolvedGemarkung = GemarkungCustomBean.createNew();
                resolvedGemarkung.setSchluessel(Integer.parseInt(gemarkung));
            }

            final FlurstueckSchluesselCustomBean key = FlurstueckSchluesselCustomBean.createNew();
            key.setGemarkung(resolvedGemarkung);
            key.setFlur(Integer.parseInt(flur));
            key.setFlurstueckZaehler(Integer.parseInt(zaehler));
            if (nenner != null) {
                key.setFlurstueckNenner(Integer.parseInt(nenner));
            } else {
                key.setFlurstueckNenner(0);
            }
            LagisBroker.getInstance().loadFlurstueck(key);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  alkisIds  DOCUMENT ME!
     */
    @Override
    protected void setObjects(final Collection alkisIds) {
        if (alkisIds == null) {
            cbxAbgearbeitet.setSelected(false);
        } else {
            final DefaultListModel flurstueckListModel = (DefaultListModel)lstFlurstuecke.getModel();
            flurstueckListModel.removeAllElements();

            for (final String alkisId : (Collection<String>)alkisIds) {
                flurstueckListModel.addElement(alkisId);
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
            new SwingWorker<Collection<String>, Void>() {

                    @Override
                    protected Collection<String> doInBackground() throws Exception {
                        final int currentSrid = CrsTransformer.getCurrentSrid();
                        final Geometry searchGeom = geom.buffer(0);
                        searchGeom.setSRID(currentSrid);
                        final BufferingGeosearch search = new BufferingGeosearch();
                        final MetaClass mc = CidsBean.getMetaClassFromTableName("WUNDA_BLAU", "alkis_landparcel");
                        search.setValidClasses(Arrays.asList(mc));
                        search.setGeometry(searchGeom);

                        final Collection<MetaObjectNode> res = (Collection<MetaObjectNode>)SessionManager
                                    .getProxy().customServerSearch(SessionManager.getSession().getUser(), search);

                        final List<String> alkisIds = new ArrayList<>();
                        for (final MetaObjectNode mon : res) {
                            alkisIds.add(mon.toString());
                        }
                        return alkisIds;
                    }

                    @Override
                    protected void done() {
                        try {
                            final FortfuehrungItem selectedFortfuehrungItem = getSelectedFortfuehrungItem();

                            final Collection<String> alkisIds = get();
                            setDetailEnabled(true);
                            setObjects(alkisIds);

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
}
