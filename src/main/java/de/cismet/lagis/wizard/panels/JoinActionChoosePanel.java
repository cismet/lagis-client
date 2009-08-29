/*
 * JoinActionChoosePanel.java
 *
 * Created on 10. September 2007, 15:47
 */
package de.cismet.lagis.wizard.panels;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.CoordinateSequenceComparator;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryComponentFilter;
import com.vividsolutions.jts.geom.GeometryFilter;
import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.gui.panels.FlurstueckChooser;
import de.cismet.lagis.thread.ExtendedSwingWorker;
import de.cismet.lagis.thread.WFSRetrieverFactory;
import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.ValidationStateChangedListener;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.core.hardwired.FlurstueckArt;
import de.cismet.lagisEE.entity.locking.Sperre;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.netbeans.spi.wizard.WizardController;

/**
 *
 * @author  Sebastian Puhl
 */
public class JoinActionChoosePanel extends javax.swing.JPanel implements ValidationStateChangedListener {

    public static final String KEY_JOIN_KEYS = "joinCandidates";
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private WizardController wizardController;
    private Map wizardData;
    private final ArrayList<FlurstueckChooser> joinCandidates = new ArrayList<FlurstueckChooser>();
    private ArrayList<FlurstueckSchluessel> joinKeys;
    private SwingWorker currentGeometryChecker = null;

    /** Creates new form JoinActionChoosePanel */
    public JoinActionChoosePanel(WizardController wizardController, Map wizardData) {
        initComponents();
        this.wizardController = wizardController;
        this.wizardData = wizardData;
        wizardController.setProblem("Bitte wählen Sie die Flurstücke aus, die zusammengelegt werden soll");
        btnRemoveJoinMember.setEnabled(false);
    }

    public void validationStateChanged(final Object validatedObject) {
        if (currentGeometryChecker != null && !currentGeometryChecker.isDone()) {
            currentGeometryChecker.cancel(false);
            currentGeometryChecker = null;
        }
        Iterator<FlurstueckChooser> joinMembers = joinCandidates.iterator();
        joinKeys = new ArrayList<FlurstueckSchluessel>();
        while (joinMembers.hasNext()) {
            FlurstueckChooser curJoinMember = joinMembers.next();
            if (curJoinMember.getStatus() == Validatable.ERROR) {
                log.debug("Mindestens ein Flurstück ,dass gejoined werden soll, ist nicht valide");
                wizardController.setProblem(curJoinMember.getValidationMessage());
                return;
            }
            Sperre sperre = EJBroker.getInstance().isLocked(curJoinMember.getCurrentFlurstueckSchluessel());
            if (sperre != null) {
                wizardController.setProblem("Ausgewähltes Flurstück ist gesperrt von Benutzer: " + sperre.getBenutzerkonto());
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
            log.debug("keine Duplicate vorhanden");
        }

        FlurstueckArt firstArt = null;
        for (FlurstueckSchluessel current : joinKeys) {
            if (firstArt == null) {
                firstArt = current.getFlurstueckArt();
                continue;
            }
            log.debug("Flurstückart ist == " + FlurstueckArt.FLURSTUECK_ART_EQUALATOR.pedanticEquals(current.getFlurstueckArt(), firstArt));
            if (!FlurstueckArt.FLURSTUECK_ART_EQUALATOR.pedanticEquals(current.getFlurstueckArt(), firstArt)) {
                wizardController.setProblem("Alle Flurstücke müssen dieselbe Art haben.");
                return;
            }
        }
        log.debug("Alle Flurstücke haben dieselbe Art");
//        currentGeometryChecker = new GeometryChecker(joinKeys);
//        currentGeometryChecker.execute();
        wizardData.put(KEY_JOIN_KEYS, joinKeys);
        wizardController.setProblem(null);
        wizardController.setForwardNavigationMode(wizardController.MODE_CAN_CONTINUE);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
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

        btnAddJoinMember.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/add.png")));
        btnAddJoinMember.setBorder(null);
        btnAddJoinMember.setOpaque(false);
        btnAddJoinMember.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddJoinMemberActionPerformed(evt);
            }
        });

        btnRemoveJoinMember.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/remove.png")));
        btnRemoveJoinMember.setBorder(null);
        btnRemoveJoinMember.setOpaque(false);
        btnRemoveJoinMember.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveJoinMemberActionPerformed(evt);
            }
        });

        jLabel1.setText("Flurst\u00fccke");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 381, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 46, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnAddJoinMember)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnRemoveJoinMember, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(13, 13, 13))
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(spJoinMembers, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAddJoinMember, btnRemoveJoinMember});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnAddJoinMember)
                        .addComponent(btnRemoveJoinMember, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spJoinMembers, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(75, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAddJoinMember, btnRemoveJoinMember});

    }// </editor-fold>//GEN-END:initComponents
    private void btnRemoveJoinMemberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveJoinMemberActionPerformed
        Component[] components = panJoinMembers.getComponents();
        log.debug("Anzahl JoinMembers: " + components.length);
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
    }//GEN-LAST:event_btnRemoveJoinMemberActionPerformed

    private void btnAddJoinMemberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddJoinMemberActionPerformed
        FlurstueckChooser tmp = new FlurstueckChooser(FlurstueckChooser.CONTINUATION_MODE);
        if (joinCandidates.size() > 0) {
            FlurstueckChooser lastChooser = joinCandidates.get(joinCandidates.size() - 1);
            if (lastChooser != null) {
                log.debug("Letzter Chooser ist != null");
                FlurstueckSchluessel currentKey = lastChooser.getCurrentFlurstueckSchluessel();
                if (currentKey != null) {
                    log.debug("Neuer FlurstückChooser wird nach letztem gesetzt");
                    tmp.doAutomaticRequest(FlurstueckChooser.AutomaticFlurstueckRetriever.COPY_CONTENT_MODE, currentKey);
                } else {
                    log.debug("FlurstückChooser kann nicht gesetzt werden");
                }
            } else {
                log.debug("letzter Chooser ist == null");
            }
        } else {
            log.debug("weniger als 1 Chooser vorhanden");
        }
        tmp.addValidationStateChangedListener(this);
        panJoinMembers.add(tmp);
        joinCandidates.add(tmp);
        btnRemoveJoinMember.setEnabled(true);
        spJoinMembers.repaint();
        spJoinMembers.getViewport().repaint();
        spJoinMembers.revalidate();
        validationStateChanged(null);
    }//GEN-LAST:event_btnAddJoinMemberActionPerformed

    class GeometryChecker extends ExtendedSwingWorker<Boolean, Void> {

        private final ArrayList<FlurstueckSchluessel> joinCandidates;
        private final ArrayList<WFSRetrieverFactory.WFSWorkerThread> wfsRetriever = new ArrayList<WFSRetrieverFactory.WFSWorkerThread>();
        private final ArrayList<Geometry> geometries = new ArrayList<Geometry>();
        boolean isFinished = false;

        public GeometryChecker(ArrayList<FlurstueckSchluessel> joinCandidates) {
            super(joinCandidates);
            this.joinCandidates = joinCandidates;
        }

        protected Boolean doInBackground() throws Exception {
            try {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        wizardController.setBusy(true);
                        enableChildren(JoinActionChoosePanel.this, false);
                        wizardController.setProblem("prüfe Benachbarung...");
                    }
                });
                log.debug("GeometryChecker started");
                if (isCancelled()) {
                    log.debug("doInBackground (GeometryChecker) is canceled");
                    return false;
                }

                if (joinCandidates != null && joinCandidates.size() > 1) {
                    for (FlurstueckSchluessel currentKey : joinCandidates) {
                        log.debug("WFSRequest gestellt");
                        WFSRetrieverFactory.WFSWorkerThread currentWorker = (WFSRetrieverFactory.WFSWorkerThread) WFSRetrieverFactory.getInstance().getWFSRetriever(currentKey, null, null);
                        wfsRetriever.add(currentWorker);
                        currentWorker.execute();
                    }
                } else {
                    return true;
                }

                if (isCancelled()) {
                    log.debug("doInBackground (GeometryChecker) is canceled --> cancele alle WFSRequests");
                    for (WFSRetrieverFactory.WFSWorkerThread currentWorker : wfsRetriever) {
                        currentWorker.cancel(false);
                    }
                    return false;
                }

                while (!isCancelled() && !isFinished) {
                    if (isCancelled()) {
                        log.debug("doInBackground (GeometryChecker) is canceled --> cancele alle WFSRequests");
                        for (WFSRetrieverFactory.WFSWorkerThread currentWorker : wfsRetriever) {
                            currentWorker.cancel(false);
                        }
                        return false;
                    }
                    Thread.currentThread().sleep(100);
                    for (WFSRetrieverFactory.WFSWorkerThread currentWorker : wfsRetriever) {
                        log.debug("Checke ob alle worker fertig sind");
                        if (!currentWorker.isDone()) {
                            log.debug("Nicht alle Worker sind Fertig (GeometryChecker) --> gehe schlafen");
                            break;
                        }
                    }
                    isFinished = true;
                }
                double areaSum = 0.0;
                Geometry joinGeometry = null;
                log.debug("Prüfe geometrien");
                for (WFSRetrieverFactory.WFSWorkerThread currentWorker : wfsRetriever) {
                    Geometry currentGeom = currentWorker.get();
                    if (currentGeom == null) {
                        log.debug("Eine Geometrie == null");
                        return false;
                    }
                    areaSum += currentGeom.getArea();
                    log.debug("Gegenwärtige Gesamtfläche: " + areaSum);
                    geometries.add(currentGeom);
                    log.debug("Gegenwärtige Geometrie ist ein: " + currentGeom.getClass());
                    log.debug("Anzahl Geometrien: " + currentGeom.getNumGeometries());
                    if (joinGeometry == null) {
                        joinGeometry = currentGeom;
                        if (joinGeometry.getNumGeometries() > 1) {
                            log.debug("Multipolygon mit mehr als einer Geometrie kann nicht zusammengelegt werden");
                            return false;
                        }
                    } else {
                        if (currentGeom.getNumGeometries() > 1) {
                            log.debug("Multipolygon mit mehr als einer Geometrie kann nicht zusammengelegt werden");
                            return false;
                        } else {
                            log.debug("Versuche Geometrien zu Vereinigen");
                            joinGeometry = joinGeometry.union(currentGeom);
                            log.debug("Joinen der Geometry erfolgreich");
                            if (joinGeometry.getNumGeometries() > 1) {
                                log.debug("Neue Geometrie -->hat mehr als einer Geometrie kann nicht zusammengelegt werden");
                                return false;
                            } else {
                                log.debug("Neue Geometrie --> hat nicht mehr als eine Geometrie");
                            }
                            log.debug("Gejoinedte Flächengröße: " + joinGeometry.getArea());
                            long epsilonSumme = (long) (areaSum * 1000);
                            long epsilonJoin = (long) (joinGeometry.getArea() * 1000);
                            log.debug("EpsilonSumme: " + epsilonSumme);
                            log.debug("EpsilonJoin: " + epsilonJoin);
                            if (epsilonSumme != epsilonJoin) {
                                log.debug("Flächengrößen sind ungleich!");
                                return false;
                            } else {
                                log.debug("Flächengrößen sind gleich!");

                            }
                        }
                    }
                }
                log.debug("Alle Geometrien sind != null GesamteFläche: " + areaSum);
                return true;
            } catch (Exception ex) {
                log.error("Fehler beim checken der Geometry: ", ex);
                hadErrors = true;
                errorMessage = "Fehler beim prüfen der Geometrien";
                return false;
            }
        }

        protected void done() {
            try {
                log.debug("GeometryChecker done");
                wizardController.setBusy(false);
                enableChildren(JoinActionChoosePanel.this, true);
                if (isCancelled()) {
                    log.debug("GeometryChecker was canceled (done)");
                    return;
                }
                if (hadErrors) {
                    log.debug("Es gab einen Fehler Geometrien konnten nicht geprüft werden");
                    wizardController.setProblem(errorMessage);

                    return;
                }
                if (get()) {
                    wizardData.put(KEY_JOIN_KEYS, joinKeys);
                    wizardController.setProblem(null);

                    wizardController.setForwardNavigationMode(wizardController.MODE_CAN_CONTINUE);
                } else {
                    wizardController.setProblem("Ausgewählte Flurstücke sind nicht benachbart");

                }
            } catch (Exception ex) {
                log.error("Fehler beim checken der Geometrie (done)");

                wizardController.setProblem("Fehler beim prüfen der Geometrien");
            }
        }
    }

    private void enableChildren(Container container, boolean isEnabled) {
        // get an arry of all the components in this container
        Component[] components = container.getComponents();
        // for each element in the container enable/disable it
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof Container) {
                enableChildren(((Container) components[i]), isEnabled);
            }
            components[i].setEnabled(isEnabled);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddJoinMember;
    private javax.swing.JButton btnRemoveJoinMember;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel panJoinMembers;
    private javax.swing.JScrollPane spJoinMembers;
    // End of variables declaration//GEN-END:variables
}
