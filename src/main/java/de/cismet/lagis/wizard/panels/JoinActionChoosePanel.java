/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * JoinActionChoosePanel.java
 *
 * Created on 10. September 2007, 15:47
 */
package de.cismet.lagis.wizard.panels;

import org.apache.log4j.Logger;

import org.netbeans.spi.wizard.WizardController;

import java.awt.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import de.cismet.cids.custom.beans.lagis.FlurstueckArtCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;
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
public class JoinActionChoosePanel extends javax.swing.JPanel implements ValidationStateChangedListener {

    //~ Static fields/initializers ---------------------------------------------

    public static final String KEY_JOIN_KEYS = "joinCandidates";

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private WizardController wizardController;
    private Map wizardData;
    private final ArrayList<FlurstueckChooser> joinCandidates = new ArrayList<FlurstueckChooser>();
    private ArrayList<FlurstueckSchluesselCustomBean> joinKeys;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddJoinMember;
    private javax.swing.JButton btnRemoveJoinMember;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel panJoinMembers;
    private javax.swing.JScrollPane spJoinMembers;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form JoinActionChoosePanel.
     *
     * @param  wizardController  DOCUMENT ME!
     * @param  wizardData        DOCUMENT ME!
     */
    public JoinActionChoosePanel(final WizardController wizardController, final Map wizardData) {
        initComponents();
        this.wizardController = wizardController;
        this.wizardData = wizardData;
        wizardController.setProblem("Bitte wählen Sie die Flurstücke aus, die zusammengelegt werden soll");
        btnRemoveJoinMember.setEnabled(false);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  wizardData  DOCUMENT ME!
     */
    public void refresh(final Map wizardData) {
        this.wizardData = wizardData;
        this.validationStateChanged(this);
    }

    @Override
    public void validationStateChanged(final Object validatedObject) {
        final Iterator<FlurstueckChooser> joinMembers = joinCandidates.iterator();
        joinKeys = new ArrayList<FlurstueckSchluesselCustomBean>();
        while (joinMembers.hasNext()) {
            final FlurstueckChooser curJoinMember = joinMembers.next();
            if (curJoinMember.getStatus() == Validatable.ERROR) {
                if (log.isDebugEnabled()) {
                    log.debug("Mindestens ein Flurstück ,dass gejoined werden soll, ist nicht valide");
                }
                wizardController.setProblem(curJoinMember.getValidationMessage());
                return;
            }
            final SperreCustomBean sperre = CidsBroker.getInstance()
                        .isLocked(curJoinMember.getCurrentFlurstueckSchluessel());
            if (sperre != null) {
                wizardController.setProblem("Ausgewähltes Flurstück ist gesperrt von Benutzer: "
                            + sperre.getBenutzerkonto());
                return;
            }
            joinKeys.add(curJoinMember.getCurrentFlurstueckSchluessel());
        }
        if (joinKeys.size() == 0) {
            wizardController.setProblem("Bitte wählen Sie die Flurstücke aus, die zusammengelegt werden soll");
            return;
        } else if (joinKeys.size() < 2) {
            wizardController.setProblem("Es müssen mindestens zwei Flurstücke ausgewählt werden");
            return;
        }
        if (ResultingPanel.checkForDuplicatedFlurstuecke(joinCandidates)) {
            wizardController.setProblem("Es darf kein Flurstück doppelt ausgewählt werden.");
            return;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("keine Duplicate vorhanden");
            }
        }

        FlurstueckArtCustomBean firstArt = null;
        for (final FlurstueckSchluesselCustomBean current : joinKeys) {
            if (firstArt == null) {
                firstArt = current.getFlurstueckArt();
                continue;
            }
            if (log.isDebugEnabled()) {
                log.debug("Flurstückart ist == "
                            + FlurstueckArtCustomBean.FLURSTUECK_ART_EQUALATOR.pedanticEquals(
                                current.getFlurstueckArt(),
                                firstArt));
            }
            if (!FlurstueckArtCustomBean.FLURSTUECK_ART_EQUALATOR.pedanticEquals(
                            current.getFlurstueckArt(),
                            firstArt)) {
                wizardController.setProblem("Alle Flurstücke müssen dieselbe Art haben.");
                return;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Alle Flurstücke haben dieselbe Art");
        }

        wizardData.put(KEY_JOIN_KEYS, joinKeys);

        wizardController.setProblem(null);
        wizardController.setForwardNavigationMode(wizardController.MODE_CAN_CONTINUE);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        spJoinMembers = new javax.swing.JScrollPane();
        panJoinMembers = new javax.swing.JPanel();
        btnAddJoinMember = new javax.swing.JButton();
        btnRemoveJoinMember = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();

        panJoinMembers.setLayout(new java.awt.GridLayout(0, 1));

        spJoinMembers.setViewportView(panJoinMembers);

        btnAddJoinMember.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/add.png")));
        btnAddJoinMember.setBorder(null);
        btnAddJoinMember.setOpaque(false);
        btnAddJoinMember.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddJoinMemberActionPerformed(evt);
                }
            });

        btnRemoveJoinMember.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/remove.png")));
        btnRemoveJoinMember.setBorder(null);
        btnRemoveJoinMember.setOpaque(false);
        btnRemoveJoinMember.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveJoinMemberActionPerformed(evt);
                }
            });

        jLabel1.setText("Flurst\u00fccke");

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
                46,
                Short.MAX_VALUE));

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addGroup(
                            javax.swing.GroupLayout.Alignment.TRAILING,
                            layout.createSequentialGroup().addContainerGap().addComponent(jLabel1).addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE).addComponent(btnAddJoinMember).addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                btnRemoveJoinMember,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                28,
                                javax.swing.GroupLayout.PREFERRED_SIZE).addGap(13, 13, 13)).addComponent(
                            jPanel2,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(
                        layout.createSequentialGroup().addGap(10, 10, 10).addComponent(
                            spJoinMembers,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            359,
                            javax.swing.GroupLayout.PREFERRED_SIZE))).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        layout.linkSize(
            javax.swing.SwingConstants.HORIZONTAL,
            new java.awt.Component[] { btnAddJoinMember, btnRemoveJoinMember });

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addComponent(
                    jPanel2,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                            btnAddJoinMember).addComponent(
                            btnRemoveJoinMember,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            28,
                            javax.swing.GroupLayout.PREFERRED_SIZE)).addComponent(jLabel1)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    spJoinMembers,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    139,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(75, Short.MAX_VALUE)));

        layout.linkSize(
            javax.swing.SwingConstants.VERTICAL,
            new java.awt.Component[] { btnAddJoinMember, btnRemoveJoinMember });
    } // </editor-fold>//GEN-END:initComponents
    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveJoinMemberActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveJoinMemberActionPerformed
        Component[] components = panJoinMembers.getComponents();
        if (log.isDebugEnabled()) {
            log.debug("Anzahl JoinMembers: " + components.length);
        }
        if (components.length > 0) {
            panJoinMembers.remove(components[components.length - 1]);
            joinCandidates.remove(joinCandidates.get(joinCandidates.size() - 1));
        }
        components = panJoinMembers.getComponents();
        if (components.length == 0) {
            btnRemoveJoinMember.setEnabled(false);
        }
        spJoinMembers.repaint();
        spJoinMembers.getViewport().repaint();
        spJoinMembers.revalidate();
        validationStateChanged(null);
    }                                                                                       //GEN-LAST:event_btnRemoveJoinMemberActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddJoinMemberActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddJoinMemberActionPerformed
        final FlurstueckChooser tmp = new FlurstueckChooser(FlurstueckChooser.Mode.CONTINUATION);

        if (joinCandidates.size() > 0) {
            final FlurstueckChooser lastChooser = joinCandidates.get(joinCandidates.size() - 1);
            if (lastChooser != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Letzter Chooser ist != null");
                }
                final FlurstueckSchluesselCustomBean currentKey = lastChooser.getCurrentFlurstueckSchluessel();
                if (currentKey != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Neuer FlurstückChooser wird nach letztem gesetzt");
                    }
                    tmp.doAutomaticRequest(
                        FlurstueckChooser.AutomaticFlurstueckRetriever.COPY_CONTENT_MODE,
                        currentKey);
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("FlurstückChooser kann nicht gesetzt werden");
                    }
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("letzter Chooser ist == null");
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("weniger als 1 Chooser vorhanden");
            }
        }
        tmp.addValidationStateChangedListener(this);
        panJoinMembers.add(tmp);
        joinCandidates.add(tmp);
        btnRemoveJoinMember.setEnabled(true);
        spJoinMembers.repaint();
        spJoinMembers.getViewport().repaint();
        spJoinMembers.revalidate();
        validationStateChanged(null);
    } //GEN-LAST:event_btnAddJoinMemberActionPerformed
}
