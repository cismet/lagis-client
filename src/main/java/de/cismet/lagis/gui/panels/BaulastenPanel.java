/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * InformationPanel.java
 *
 * Created on 16. Februar 2009, 10:50
 */
package de.cismet.lagis.gui.panels;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObjectNode;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisUtils;
import de.cismet.cids.custom.wunda_blau.search.server.BaulastSearchInfo;
import de.cismet.cids.custom.wunda_blau.search.server.CidsBaulastSearchStatement;
import de.cismet.cids.custom.wunda_blau.search.server.FlurstueckInfo;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.gui.main.LagisApp;

import de.cismet.lagis.interfaces.FlurstueckChangeListener;

import de.cismet.lagis.widget.AbstractWidget;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
public class BaulastenPanel extends AbstractWidget implements FlurstueckChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(BaulastenPanel.class);

    //~ Instance fields --------------------------------------------------------

    private MetaClass mcBaulast;
    private final MetaObjectNode loadingMon = new MetaObjectNode(
            "WUNDA_BLAU",
            -1,
            -1,
            "<html><i>wird geladen...",
            null,
            null);
    private final MetaObjectNode errorMon = new MetaObjectNode(
            "WUNDA_BLAU",
            -1,
            -1,
            "<html><i>Fehler beim Laden !",
            null,
            null);

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JList<MetaObjectNode> jList1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form InformationPanel.
     */
    public BaulastenPanel() {
        setIsCoreWidget(true);
        initComponents();
        loadBaulasten(null);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void clearComponent() {
    }

    @Override
    public void refresh(final Object refreshObject) {
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
    }

    @Override
    public void flurstueckChanged(final FlurstueckCustomBean newFlurstueck) {
        loadBaulasten(newFlurstueck);

        LagisBroker.getInstance().flurstueckChangeFinished(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueck  DOCUMENT ME!
     */
    private void loadBaulasten(final FlurstueckCustomBean flurstueck) {
        final DefaultListModel<MetaObjectNode> listModel = ((DefaultListModel<MetaObjectNode>)jList1.getModel());
        listModel.clear();
        listModel.addElement(loadingMon);
        if (flurstueck != null) {
            new SwingWorker<Collection<MetaObjectNode>, Void>() {

                    @Override
                    protected Collection<MetaObjectNode> doInBackground() throws Exception {
                        final CidsBean flurstueckSchluessel = (CidsBean)flurstueck.getProperty(
                                "fk_flurstueck_schluessel");

                        final BaulastSearchInfo searchInfo = new BaulastSearchInfo();

                        final String alkisId = AlkisUtils.generateLandparcelCode(
                                5,
                                (Integer)flurstueckSchluessel.getProperty("fk_gemarkung.schluessel"),
                                (Integer)flurstueckSchluessel.getProperty("flur"),
                                (Integer)flurstueckSchluessel.getProperty("flurstueck_zaehler"),
                                (Integer)flurstueckSchluessel.getProperty("flurstueck_nenner"));

                        final String[] alkisIdParts = alkisId.split("-");
                        final Integer gemarkung = Integer.parseInt(alkisIdParts[0].substring(2));
                        final String flur = alkisIdParts[1];
                        final String[] zaehlerNennerParts = alkisIdParts[2].split("/");
                        final String zaehler = Integer.toString(Integer.parseInt(zaehlerNennerParts[0]));
                        final String nenner = (zaehlerNennerParts.length > 1)
                            ? Integer.toString(Integer.parseInt(zaehlerNennerParts[1])) : "0";

                        final FlurstueckInfo fsi = new FlurstueckInfo(gemarkung, flur, zaehler, nenner);
                        searchInfo.setFlurstuecke(Arrays.asList(fsi));
                        searchInfo.setResult(CidsBaulastSearchStatement.Result.BAULAST);
                        searchInfo.setBelastet(true);
                        searchInfo.setBeguenstigt(true);
                        searchInfo.setBlattnummer("");
                        searchInfo.setArt("");

                        if (mcBaulast == null) {
                            mcBaulast = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", "alb_baulast");
                        }

                        final CidsBaulastSearchStatement search = new CidsBaulastSearchStatement(
                                searchInfo,
                                mcBaulast.getId(),
                                -1);

                        final Collection<MetaObjectNode> baulastenMons = SessionManager.getProxy()
                                    .customServerSearch(search);
                        return baulastenMons;
                    }

                    @Override
                    protected void done() {
                        try {
                            listModel.clear();
                            final Collection<MetaObjectNode> baulastenMons = get();
                            for (final MetaObjectNode baulastMon : baulastenMons) {
                                listModel.addElement(baulastMon);
                            }
                            jButton1.setEnabled(!listModel.isEmpty());
                        } catch (final Exception ex) {
                            LOG.error("error while executing baulasten search", ex);
                            listModel.addElement(errorMon);
                            jButton1.setEnabled(false);
                        }
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

        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jButton1 = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jList1.setModel(new DefaultListModel<MetaObjectNode>());
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    jList1MouseClicked(evt);
                }
            });
        jScrollPane1.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jScrollPane1, gridBagConstraints);

        jButton1.setText("Renderer anzeigen");
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel4.add(jButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        add(jPanel4, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        showRenderer();
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jList1MouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jList1MouseClicked
        if (evt.getClickCount() == 2) {
            showRenderer();
        }
    }                                                                      //GEN-LAST:event_jList1MouseClicked

    /**
     * DOCUMENT ME!
     */
    private void showRenderer() {
        if (jButton1.isEnabled()) {
            final Collection<MetaObjectNode> baulastMons = new ArrayList<MetaObjectNode>();
            final DefaultListModel<MetaObjectNode> listModel = (DefaultListModel<MetaObjectNode>)jList1.getModel();
            for (int index = 0; index < listModel.getSize(); index++) {
                if (index >= 0) {
                    final MetaObjectNode baulastMon = listModel.get(index);
                    baulastMons.add(baulastMon);
                }
            }
            if (baulastMons.isEmpty()) {
            } else if (baulastMons.size() == 1) {
                final MetaObjectNode baulastMon = baulastMons.iterator().next();
                if (baulastMon.getClassId() != -1) {
                    LagisApp.getInstance().showRenderer(baulastMons.toArray(new MetaObjectNode[0]));
                }
            } else {
                LagisApp.getInstance().showRenderer(baulastMons.toArray(new MetaObjectNode[0]));
            }
        }
    }
}
