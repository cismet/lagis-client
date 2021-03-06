/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * ActivateActionSteps.java
 *
 * Created on 6. Februar 2008, 10:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.wizard.steps;

import org.apache.log4j.Logger;

import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Summary;
import org.netbeans.spi.wizard.WizardController;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPanelProvider;

import java.awt.EventQueue;

import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.Exception.ActionNotSuccessfulException;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.gui.main.LagisApp;

import de.cismet.lagis.wizard.panels.ActivateActionPanel;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class ActivateActionSteps extends WizardPanelProvider {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of ActivateActionSteps.
     */
    public ActivateActionSteps() {
        super(
            "Flurstück aktivieren...",
            new String[] { "Flurstück auswählen" },
            new String[] { "Auswahl des Flurstücks" });
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected JComponent createPanel(final WizardController wizardController, final String id, final Map wizardData) {
        return new ActivateActionPanel(wizardController, wizardData);
    }

    @Override
    public boolean cancel(final Map settings) {
        // return true;
        final boolean dialogShouldClose = JOptionPane.showConfirmDialog(LagisApp.getInstance(),
                "Möchten Sie den Bearbeitungsvorgang beenden?") == JOptionPane.OK_OPTION;
        return dialogShouldClose;
    }
    @Override
    protected Object finish(final Map settings) throws WizardException {
        return new BackgroundResultCreator();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static class BackgroundResultCreator extends DeferredWizardResult {

        //~ Instance fields ----------------------------------------------------

        private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

        //~ Methods ------------------------------------------------------------

        @Override
        public void start(final Map wizardData, final ResultProgressHandle progress) {
            if (!LagisBroker.getInstance().checkFlurstueckWizardUserWantsToFinish()) {
                progress.failed("Die Aktion wurde durch den Benutzer abgebrochen.", true);
                return;
            }
            if (log.isDebugEnabled()) {
                log.debug("WizardFinisher: Flurstueck aktivieren: ");
            }
            assert !EventQueue.isDispatchThread();
            final FlurstueckSchluesselCustomBean activationCandidate = (FlurstueckSchluesselCustomBean)wizardData.get(
                    ActivateActionPanel.KEY_ACTIVATE_CANDIDATE);
            if (log.isDebugEnabled()) {
                log.debug("Flurstück das aktiviert werden soll: " + activationCandidate.getKeyString());
            }
            CidsBean sperre = null;
            try {
                final CidsBean other = LagisBroker.getInstance().isLocked(activationCandidate);
                if (other == null) {
                    sperre = LagisBroker.getInstance().createFlurstueckSchluesselLock(activationCandidate);
                    if (sperre != null) {
                        progress.setBusy("Flurstück wird aktiviert");
                        // CidsBroker.getInstance().createFlurstueck(key);
                        LagisBroker.getInstance().setFlurstueckActive(activationCandidate);
                        // TODO schlechte Postion verwirrt den Benutzer wäre besser wenn sie ganz zum Schluss käme
                        LagisBroker.getInstance().releaseLock(sperre);
                        if ((LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null)
                                    && FlurstueckSchluesselCustomBean.FLURSTUECK_EQUALATOR.pedanticEquals(
                                        LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),
                                        activationCandidate)) {
                            if (log.isDebugEnabled()) {
                                log.debug("Aktuelles flurstück wurde aktiviert --> update");
                            }
                            try {
                                LagisBroker.getInstance().loadFlurstueck(activationCandidate);
                            } catch (Exception ex) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Fehler beim updaten/laden der FlurstueckSchluessel/Flurstücks", ex);
                                }
                            }
                        } else {
                            final boolean changeFlurstueck = JOptionPane.showConfirmDialog(LagisApp.getInstance(),
                                    "Möchten Sie zu dem aktivierten Flurstück wechseln?",
                                    "Flurstückwechsel",
                                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (changeFlurstueck) {
                                            LagisBroker.getInstance().loadFlurstueck(activationCandidate);
                                        } else {
                                            LagisBroker.getInstance().reloadFlurstueckKeys();
                                        }
                                    }
                                });
                        }
                        final Summary summary = Summary.create("Flurstück: \n" + "\""
                                        + activationCandidate.getKeyString() + " konnte erfolgreich aktiviert werden",
                                activationCandidate);
                        progress.finished(summary);
                        if (log.isDebugEnabled()) {
                            log.debug("Flurstück konnte erfolgreich aktiviert werden: ");
                        }
                    } else {
                        progress.failed("Es war nicht möglich die Art des Flurstücks:\n\t\""
                                    + activationCandidate.getKeyString()
                                    + "\"\nzu ändern, es konnte keine Sperre angelegt werden.",
                            false);
                    }
                } else {
                    progress.failed("Es war nicht möglich die Art des Flurstücks:\n\t\""
                                + activationCandidate.getKeyString()
                                + "\"\nzu ändern, es ist von einem anderen Benutzer gesperrt: "
                                + (String)other.getProperty("user_string"),
                        false);
                }
            } catch (final Exception e) {
                log.error("Fehler beim renamen eines Flurstücks: ", e);
                try {
                    LagisBroker.getInstance().releaseLock(sperre);
                } catch (Exception ex) {
                    log.error("Fehler beim lösen der Sperre", ex);
                }
                final StringBuffer buffer = new StringBuffer("Flurstück: \n" + "\""
                                + activationCandidate.getKeyString() + " konnte nicht aktiviert werden. Fehler:\n");
                if (e instanceof ActionNotSuccessfulException) {
                    final ActionNotSuccessfulException reason = (ActionNotSuccessfulException)e;
                    if (reason.hasNestedExceptions()) {
                        log.error("Nested Rename Exceptions: ", reason.getNestedExceptions());
                    }
                    buffer.append(reason.getMessage());
                } else {
                    log.error("Unbekannter Fehler: ", e);
                    buffer.append("Unbekannter Fehler bitte wenden Sie sich an Ihren Systemadministrator");
                }
                progress.failed(buffer.toString(), false);
            }
        }
    }
}
