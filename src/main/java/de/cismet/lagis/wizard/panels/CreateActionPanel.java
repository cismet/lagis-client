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

import org.apache.log4j.Logger;

import org.netbeans.spi.wizard.WizardController;

import java.util.Map;

import javax.swing.Icon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.beans.lagis.SperreCustomBean;

import de.cismet.lagis.broker.CidsBroker;

import de.cismet.lagis.gui.panels.FlurstueckChooser;

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

    public static final String KEY_CREATE_CANDIDATE = "createCandidate";
    public static final String KEY_IS_STAEDTISCH = "isStaedtisch";

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private WizardController wizardController;
    private Map wizardData;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
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
        if (log.isDebugEnabled()) {
            log.debug("Create Action Panel wird angelegt");
        }
        initComponents();
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
            final SperreCustomBean sperre = CidsBroker.getInstance()
                        .isLocked(panCreate.getCurrentFlurstueckSchluessel());
            if (sperre != null) {
                // TODO nicht ganz sichtbar
                wizardController.setProblem("Ausgewähltes Flurstück ist gesperrt von Benutzer: "
                            + sperre.getBenutzerkonto());
                return;
            } else {
                wizardData.put(KEY_CREATE_CANDIDATE, panCreate.getCurrentFlurstueckSchluessel());
                wizardData.put(KEY_IS_STAEDTISCH, isStaedtisch);
                wizardController.setProblem(null);
                wizardController.setForwardNavigationMode(wizardController.MODE_CAN_FINISH);
            }
        } else {
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
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        rbGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        panCreate = new FlurstueckChooser(FlurstueckChooser.CREATION_MODE);
        pKind = new javax.swing.JPanel();
        rbStaedtisch = new javax.swing.JRadioButton();
        rbAbteilungIX = new javax.swing.JRadioButton();
        lblAbteilungIX = new javax.swing.JLabel();
        lblStaedtisch = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        final javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                381,
                Short.MAX_VALUE));
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                58,
                Short.MAX_VALUE));

        jLabel1.setText("Neues Flurst\u00fcck");

        final javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                jPanel2Layout.createSequentialGroup().addContainerGap().addGroup(
                    jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        jLabel1).addComponent(
                        panCreate,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        351,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(jLabel1).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panCreate,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        rbStaedtisch.setText("St\u00e4dtisch");
        rbStaedtisch.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbStaedtisch.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbStaedtisch.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    rbStaedtischActionPerformed(evt);
                }
            });

        rbAbteilungIX.setText("Abteilung IX");
        rbAbteilungIX.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbAbteilungIX.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbAbteilungIX.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    rbAbteilungIXActionPerformed(evt);
                }
            });

        lblAbteilungIX.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/historic_abteilungIX.png")));

        lblStaedtisch.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/current.png")));

        jLabel2.setText("Art des Flurst\u00fccks:");

        final javax.swing.GroupLayout pKindLayout = new javax.swing.GroupLayout(pKind);
        pKind.setLayout(pKindLayout);
        pKindLayout.setHorizontalGroup(
            pKindLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                pKindLayout.createSequentialGroup().addContainerGap().addGroup(
                    pKindLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel2)
                                .addGroup(
                                    pKindLayout.createSequentialGroup().addComponent(lblStaedtisch).addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                        rbStaedtisch,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        70,
                                        javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                        lblAbteilungIX).addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                        rbAbteilungIX,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        97,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))).addContainerGap(
                    138,
                    Short.MAX_VALUE)));
        pKindLayout.setVerticalGroup(
            pKindLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                pKindLayout.createSequentialGroup().addComponent(jLabel2).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    pKindLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addGroup(
                        pKindLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                            rbAbteilungIX,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            22,
                            javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(lblAbteilungIX)).addComponent(
                        rbStaedtisch,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        20,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(lblStaedtisch)).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        jPanel1,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).addGroup(
                        layout.createSequentialGroup().addContainerGap().addComponent(
                            pKind,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE)).addGroup(
                        layout.createSequentialGroup().addContainerGap().addComponent(
                            jPanel2,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE))).addContainerGap()));
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addComponent(
                    jPanel1,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    pKind,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    jPanel2,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap()));
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
