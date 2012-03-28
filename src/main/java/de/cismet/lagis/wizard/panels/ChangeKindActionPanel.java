/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * KindChangeActionSteps.java
 *
 * Created on 1. Februar 2008, 14:05
 */
package de.cismet.lagis.wizard.panels;

import org.apache.log4j.Logger;

import org.netbeans.spi.wizard.WizardController;

import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.beans.verdis_grundis.*;

import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.gui.panels.FlurstueckChooser;

import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.ValidationStateChangedListener;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class ChangeKindActionPanel extends javax.swing.JPanel implements ValidationStateChangedListener,
    ChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    public static final String KEY_CHANGE_CANDIDATE = "changeCandidate";
    public static final String KEY_NEW_KIND = "newKind";

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private WizardController wizardController;
    private Map wizardData;
    private boolean isStaedtisch = true;
    private ButtonGroup rbGroup;
    private boolean isDeactivated = true;
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblAbteilungIX;
    private javax.swing.JLabel lblStaedtisch;
    private de.cismet.lagis.gui.panels.FlurstueckChooser panChangeKind;
    private javax.swing.JRadioButton rbAbteilungIX;
    private javax.swing.JRadioButton rbStaedtisch;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form KindChangeActionSteps.
     *
     * @param  wizardController  DOCUMENT ME!
     * @param  wizardData        DOCUMENT ME!
     */
    public ChangeKindActionPanel(final WizardController wizardController, final Map wizardData) {
        initComponents();
        this.wizardController = wizardController;
        this.wizardData = wizardData;
        wizardController.setProblem("Bitte wählen Sie das Flurstück aus, dessen Art geändert werden soll");
        panChangeKind.addValidationStateChangedListener(this);
        rbGroup = new ButtonGroup();
        rbGroup.add(rbStaedtisch);
        rbGroup.add(rbAbteilungIX);
        rbStaedtisch.getModel().addChangeListener(this);
        rbAbteilungIX.getModel().addChangeListener(this);
        rbStaedtisch.setEnabled(false);
        rbAbteilungIX.setEnabled(false);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void validationStateChanged(final Object validatedObject) {
        if (!(panChangeKind.getStatus() == Validatable.VALID)) {
            wizardController.setProblem(panChangeKind.getValidationMessage());
            rbStaedtisch.setEnabled(false);
            rbAbteilungIX.setEnabled(false);
            isDeactivated = true;
            return;
        }

        try {
            if (isDeactivated || ((validatedObject != null) && (validatedObject instanceof FlurstueckChooser))) {
                isDeactivated = false;
                rbStaedtisch.setEnabled(true);
                rbAbteilungIX.setEnabled(true);
                if (panChangeKind.getCurrentFlurstueckSchluessel().getFlurstueckArt().getBezeichnung().equals(
                                FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                    rbAbteilungIX.setSelected(true);
                } else {
                    rbStaedtisch.setSelected(true);
                }
            }

            if (rbStaedtisch.isSelected() || rbAbteilungIX.isSelected()) {
                FlurstueckArtCustomBean newArt = null;
                if (rbStaedtisch.getModel().equals(rbGroup.getSelection())) {
                    if (panChangeKind.getCurrentFlurstueckSchluessel().getFlurstueckArt().getBezeichnung().equals(
                                    FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                        if (log.isDebugEnabled()) {
                            log.debug("Flurstück ist städtisch");
                        }
                        wizardController.setProblem("Flurstück ist bereits städtisch");
                        return;
                    } else {
                        for (final FlurstueckArtCustomBean currentArt : EJBroker.getInstance().getAllFlurstueckArten()) {
                            if (currentArt.getBezeichnung().equals(
                                            FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                                newArt = currentArt;
                            }
                        }
                    }
                } else if (rbAbteilungIX.getModel().equals(rbGroup.getSelection())) {
                    if (panChangeKind.getCurrentFlurstueckSchluessel().getFlurstueckArt().getBezeichnung().equals(
                                    FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX)) {
                        if (log.isDebugEnabled()) {
                            log.debug("Flurstück ist Abteilung IX zugeordnet");
                        }
                        wizardController.setProblem("Flurstück ist bereits Abteilung IX zugeordnet");
                        return;
                    } else {
                        for (final FlurstueckArtCustomBean currentArt : EJBroker.getInstance().getAllFlurstueckArten()) {
                            if (currentArt.getBezeichnung().equals(
                                            FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX)) {
                                newArt = currentArt;
                            }
                        }
                    }
                }
                if (newArt == null) {
                    wizardController.setProblem("Gewählte Art kommt in der Datenbank nicht vor");
                    return;
                }
                final SperreCustomBean sperre = EJBroker.getInstance()
                            .isLocked(panChangeKind.getCurrentFlurstueckSchluessel());
                if (sperre != null) {
                    // TODO nicht ganz sichtbar
                    wizardController.setProblem("Ausgewähltes Flurstück ist gesperrt von Benutzer: "
                                + sperre.getBenutzerkonto());
                    return;
                } else {
                    wizardData.put(KEY_CHANGE_CANDIDATE, panChangeKind.getCurrentFlurstueckSchluessel());
                    wizardData.put(KEY_NEW_KIND, newArt);
                    wizardController.setProblem(null);
                    wizardController.setForwardNavigationMode(wizardController.MODE_CAN_CONTINUE);
                    return;
                }
            } else {
                wizardController.setProblem("Bitte wählen Sie die neue Art des Flurstücks aus");
            }
        } catch (NullPointerException ex) {
            log.error("Flurstück besitzt keine Art", ex);
            wizardController.setProblem("Flurstück besitzt keine Art");
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
        panChangeKind = new de.cismet.lagis.gui.panels.FlurstueckChooser();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        rbStaedtisch = new javax.swing.JRadioButton();
        rbAbteilungIX = new javax.swing.JRadioButton();
        lblStaedtisch = new javax.swing.JLabel();
        lblAbteilungIX = new javax.swing.JLabel();

        final javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                381,
                Short.MAX_VALUE));
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                58,
                Short.MAX_VALUE));

        jLabel1.setText("Flurst\u00fcck:");

        jLabel2.setText("Neue Zuordnung:");

        rbStaedtisch.setText("St\u00e4dtisch");
        rbStaedtisch.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbStaedtisch.setMargin(new java.awt.Insets(0, 0, 0, 0));

        rbAbteilungIX.setText("Abteilung IX");
        rbAbteilungIX.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbAbteilungIX.setMargin(new java.awt.Insets(0, 0, 0, 0));

        lblStaedtisch.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/historic.png")));

        lblAbteilungIX.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/historic_abteilungIX.png")));

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        jPanel2,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(
                        layout.createSequentialGroup().addContainerGap().addComponent(jLabel1)).addComponent(
                        panChangeKind,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        355,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(
                        layout.createSequentialGroup().addContainerGap().addComponent(jLabel2)).addGroup(
                        layout.createSequentialGroup().addContainerGap().addComponent(lblStaedtisch).addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                            rbStaedtisch,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            71,
                            javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(lblAbteilungIX)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                            rbAbteilungIX,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            97,
                            javax.swing.GroupLayout.PREFERRED_SIZE))).addContainerGap(15, Short.MAX_VALUE)));
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addComponent(
                    jPanel2,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel1).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panChangeKind,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel2).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        layout.createSequentialGroup().addGap(1, 1, 1).addGroup(
                            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                                lblStaedtisch).addComponent(
                                rbStaedtisch,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                22,
                                javax.swing.GroupLayout.PREFERRED_SIZE))).addGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(
                            rbAbteilungIX,
                            javax.swing.GroupLayout.Alignment.TRAILING,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE).addComponent(
                            lblAbteilungIX,
                            javax.swing.GroupLayout.Alignment.TRAILING))).addContainerGap(129, Short.MAX_VALUE)));
    } // </editor-fold>//GEN-END:initComponents
}
