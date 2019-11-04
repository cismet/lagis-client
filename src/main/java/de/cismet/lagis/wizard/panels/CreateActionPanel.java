/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * CreateActionPanel.java
 *
 * Created on 8. September 2007, 13:51
 */
package de.cismet.lagis.wizard.panels;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.netbeans.spi.wizard.WizardController;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.gui.panels.FlurstueckChooser;

import de.cismet.lagis.interfaces.DoneDelegate;

import de.cismet.lagis.thread.ExtendedSwingWorker;
import de.cismet.lagis.thread.WFSRetrieverFactory;

import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.ValidationStateChangedListener;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class CreateActionPanel extends javax.swing.JPanel implements ValidationStateChangedListener, ChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(CreateActionPanel.class);
    public static final String KEY_CREATE_CANDIDATE = "createCandidate";
    public static final String KEY_IS_STAEDTISCH = "isStaedtisch";

    //~ Instance fields --------------------------------------------------------

    private final WizardController wizardController;
    private final Map wizardData;
    private boolean isStaedtisch = true;
    private final Icon icoStaedtisch = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/current.png"));
    private final Icon icoAbteilungIX = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/abteilungIX.png"));
    private final Icon icoStaedtischHistoric = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/historic.png"));
    private final Icon icoAbteilungIXHistoric = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/historic_abteilungIX.png"));

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lblAbteilungIX;
    private javax.swing.JLabel lblStaedtisch;
    private javax.swing.JPanel pKind;
    private de.cismet.lagis.gui.panels.FlurstueckChooser panCreate;
    private javax.swing.JRadioButton rbAbteilungIX;
    private javax.swing.ButtonGroup rbGroup;
    private javax.swing.JRadioButton rbStaedtisch;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form CreateActionPanel.
     *
     * @param  wizardController  DOCUMENT ME!
     * @param  wizardData        DOCUMENT ME!
     */
    public CreateActionPanel(final WizardController wizardController, final Map wizardData) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create Action Panel wird angelegt");
        }
        initComponents();
        jLabel3.setVisible(false);
        jLabel4.setVisible(false);
        rbGroup.add(rbStaedtisch);
        rbGroup.add(rbAbteilungIX);
        rbStaedtisch.setSelected(true);
        rbStaedtisch.getModel().addChangeListener(this);
        rbAbteilungIX.getModel().addChangeListener(this);
        this.wizardController = wizardController;
        this.wizardData = wizardData;
        wizardController.setProblem("Bitte geben Sie den neuen Flurstücksschlüssel ein");
        panCreate.addValidationStateChangedListener(this);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void validationStateChanged(final Object validatedObject) {
        if (panCreate.getStatus() == Validatable.VALID) {
            final FlurstueckSchluesselCustomBean flurstueckSchluessel = panCreate.getCurrentFlurstueckSchluessel();
            final CidsBean sperre = LagisBroker.getInstance().isLocked(flurstueckSchluessel);
            if (sperre != null) {
                // TODO nicht ganz sichtbar
                wizardController.setProblem("Ausgewähltes Flurstück ist gesperrt von Benutzer: "
                            + (String)sperre.getProperty("user_string"));
                return;
            } else {
                final SwingWorker currentWFSRetriever = WFSRetrieverFactory.getInstance()
                            .getWFSRetriever(flurstueckSchluessel,
                                new DoneDelegate<Geometry, Void>() {

                                    @Override
                                    public void jobDone(final ExtendedSwingWorker<Geometry, Void> worker,
                                            final HashMap<Integer, Boolean> properties) {
                                        final Geometry result;
                                        try {
                                            result = worker.get();
                                            jLabel3.setVisible(result == null);
                                            jLabel4.setVisible(result == null);
                                            if (result == null) {
                                                revalidate();
                                                repaint();
                                            }
                                            wizardData.put(KEY_CREATE_CANDIDATE, flurstueckSchluessel);
                                            wizardData.put(KEY_IS_STAEDTISCH, isStaedtisch);
                                            wizardController.setProblem(null);
                                            wizardController.setForwardNavigationMode(wizardController.MODE_CAN_FINISH);
                                        } catch (final Exception ex) {
                                            wizardController.setProblem(
                                                "Suche nach Geometry des ausgewählten Flurstücks gescheitert.");
                                            LOG.error(ex, ex);
                                        }
                                    }
                                }, null);
                LagisBroker.getInstance().execute(currentWFSRetriever);
            }
        } else {
            jLabel3.setVisible(false);
            jLabel4.setVisible(false);
            wizardController.setProblem(panCreate.getValidationMessage());
        }
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        if (e.getSource().equals(rbStaedtisch.getModel())) {
            if (rbStaedtisch.isSelected()) {
                lblStaedtisch.setIcon(icoStaedtisch);
            } else {
                lblStaedtisch.setIcon(icoStaedtischHistoric);
            }
            validationStateChanged(this);
        } else {
            if (rbAbteilungIX.isSelected()) {
                lblAbteilungIX.setIcon(icoAbteilungIX);
            } else {
                lblAbteilungIX.setIcon(icoAbteilungIXHistoric);
            }
            validationStateChanged(this);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        rbGroup = new javax.swing.ButtonGroup();
        jPanel5 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        panCreate = new FlurstueckChooser(FlurstueckChooser.Mode.CREATION);
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        pKind = new javax.swing.JPanel();
        rbStaedtisch = new javax.swing.JRadioButton();
        rbAbteilungIX = new javax.swing.JRadioButton();
        lblAbteilungIX = new javax.swing.JLabel();
        lblStaedtisch = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();

        final javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                100,
                Short.MAX_VALUE));
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                100,
                Short.MAX_VALUE));

        setLayout(new java.awt.GridBagLayout());

        final javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Neues Flurstück");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        jPanel2.add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        jPanel2.add(panCreate, gridBagConstraints);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/warn.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        jPanel2.add(jLabel3, gridBagConstraints);

        jLabel4.setText(
            "<html><b color=\"red\">Achtung: Es konnte keine ALKIS-Geometrie zu diesem Flurstück gefunden werden. Überprüfen Sie bitte Ihre Flurstückseingabe.<br><br>Befindet sich das Flurstück außerhalb von Wuppertal, dann kann diese Warnung ignoriert werden.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        jPanel2.add(jLabel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 12);
        add(jPanel2, gridBagConstraints);

        pKind.setLayout(new java.awt.GridBagLayout());

        rbStaedtisch.setText("Städtisch");
        rbStaedtisch.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbStaedtisch.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    rbStaedtischActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 12, 0);
        pKind.add(rbStaedtisch, gridBagConstraints);

        rbAbteilungIX.setText("Abteilung IX");
        rbAbteilungIX.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbAbteilungIX.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    rbAbteilungIXActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 12, 0);
        pKind.add(rbAbteilungIX, gridBagConstraints);

        lblAbteilungIX.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/historic_abteilungIX.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 12, 0);
        pKind.add(lblAbteilungIX, gridBagConstraints);

        lblStaedtisch.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/current.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 12, 0);
        pKind.add(lblStaedtisch, gridBagConstraints);

        jLabel2.setText("Art des Flurstücks:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        pKind.add(jLabel2, gridBagConstraints);

        final javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                105,
                Short.MAX_VALUE));
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                57,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pKind.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 12);
        add(pKind, gridBagConstraints);

        final javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        add(jPanel3, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void rbStaedtischActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_rbStaedtischActionPerformed
        if (rbStaedtisch.isSelected()) {
            isStaedtisch = true;
        }
    }                                                                                //GEN-LAST:event_rbStaedtischActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void rbAbteilungIXActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_rbAbteilungIXActionPerformed
        if (rbAbteilungIX.isSelected()) {
            isStaedtisch = false;
        }
    }                                                                                 //GEN-LAST:event_rbAbteilungIXActionPerformed
}
