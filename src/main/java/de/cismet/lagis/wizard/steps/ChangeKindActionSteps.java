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
 * Created on 1. Februar 2008, 14:03
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

import de.cismet.cids.custom.beans.lagis.FlurstueckArtCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.Exception.ActionNotSuccessfulException;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.gui.main.LagisApp;

import de.cismet.lagis.wizard.panels.ChangeKindActionPanel;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class ChangeKindActionSteps extends WizardPanelProvider {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of KindChangeActionSteps.
     */
    public ChangeKindActionSteps() {
        super(
            "Flurstückart ändern...",
            new String[] { "Flurstück auswählen" },
            new String[] { "Auswahl des Flurstücks" });
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected JComponent createPanel(final WizardController wizardController, final String id, final Map wizardData) {
        return new ChangeKindActionPanel(wizardController, wizardData);
    }

    @Override
    public boolean cancel(final Map settings) {
        // return true;
        // TODO FEHLER sollte von Wizard abhängig sein
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
                log.debug("WizardFinisher: Flurstueckart ändern: ");
            }
            assert !EventQueue.isDispatchThread();
            final FlurstueckSchluesselCustomBean key = (FlurstueckSchluesselCustomBean)wizardData.get(
                    ChangeKindActionPanel.KEY_CHANGE_CANDIDATE);
            final FlurstueckArtCustomBean newArt = (FlurstueckArtCustomBean)wizardData.get(
                    ChangeKindActionPanel.KEY_NEW_KIND);
            CidsBean sperre = null;
            try {
                // TODO besser alles in Server
                final CidsBean other = LagisBroker.getInstance().isLocked(key);
                if (other == null) {
                    sperre = LagisBroker.getInstance().createFlurstueckSchluesselLock(key);
                    if (sperre != null) {
                        progress.setBusy("Flurstückart wird geändert");
                        key.setFlurstueckArt(newArt);
                        LagisBroker.getInstance().modifyFlurstueckSchluessel(key);
                        LagisBroker.getInstance().releaseLock(sperre);
                        // TODO schlechte Postion verwirrt den Benutzer wäre besser wenn sie ganz zum Schluss käme
                        if ((LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null)
                                    && FlurstueckSchluesselCustomBean.FLURSTUECK_EQUALATOR.pedanticEquals(
                                        LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),
                                        key)) {
                            if (log.isDebugEnabled()) {
                                log.debug("Art des aktuellen flurstücks wurde geändert --> update");
                            }
                            try {
                                LagisBroker.getInstance().loadFlurstueck(key);
                            } catch (Exception ex) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Fehler beim updaten/laden der FlurstueckSchluessel/Flurstücks", ex);
                                }
                            }
                        } else {
                            final boolean changeFlurstueck = JOptionPane.showConfirmDialog(LagisApp.getInstance(),
                                    "Möchten Sie zu dem geänderten Flurstück wechseln?",
                                    "Flurstückwechsel",
                                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
                            try {
                                EventQueue.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            if (changeFlurstueck) {
                                                LagisBroker.getInstance().loadFlurstueck(key);
                                            } else {
                                                LagisBroker.getInstance().reloadFlurstueckKeys();
                                            }
                                        }
                                    });
                            } catch (Exception ex) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Fehler beim updaten/laden der FlurstueckSchluessel/Flurstücks", ex);
                                }
                            }
                        }
                        final Summary summary = Summary.create("Die Art des Flurstück: \n\t" + "\""
                                        + key.getKeyString() + "\" \n\nkonnte erfolgreich auf " + "\""
                                        + newArt.getBezeichnung() + "\"" + " geändert werden",
                                key);
                        progress.finished(summary);
                    } else {
                        progress.failed("Es war nicht möglich die Art des Flurstücks:\n\t\"" + key.getKeyString()
                                    + "\"\nzu ändern, es konnte keine Sperre angelegt werden.",
                            false);
                    }
                } else {
                    progress.failed("Es war nicht möglich die Art des Flurstücks:\n\t\"" + key.getKeyString()
                                + "\"\nzu ändern, es ist von einem anderen Benutzer gesperrt: "
                                + (String)other.getProperty("user_string"),
                        false);
                }
            } catch (Exception e) {
                try {
                    LagisBroker.getInstance().releaseLock(sperre);
                } catch (Exception ex) {
                    log.error("Fehler beim lösen der Sperre", ex);
                }
                final StringBuffer buffer = new StringBuffer("Die Art des Flurstücks: \n\t\"" + key.getKeyString()
                                + "\" \n\nkonnte nicht geändert werden. Fehler:");
                if (e instanceof ActionNotSuccessfulException) {
                    final ActionNotSuccessfulException reason = (ActionNotSuccessfulException)e;
                    if (reason.hasNestedExceptions()) {
                        log.error("Nested changeKind Exceptions: ", reason.getNestedExceptions());
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
