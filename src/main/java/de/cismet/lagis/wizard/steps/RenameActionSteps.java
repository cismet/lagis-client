/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * RenameActionSteps.java
 *
 * Created on 9. September 2007, 17:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.wizard.steps;

import org.apache.log4j.Logger;

import org.netbeans.spi.wizard.*;

import java.awt.EventQueue;

import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

import de.cismet.lagis.Exception.ActionNotSuccessfulException;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.wizard.panels.RenameActionPanel;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class RenameActionSteps extends WizardPanelProvider {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of RenameActionSteps.
     */
    public RenameActionSteps() {
        super(
            "Flurstück umbenennen...",
            new String[] { "Flurstück auswählen" },
            new String[] { "Auswahl des Flurstücks" });
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected JComponent createPanel(final WizardController wizardController, final String id, final Map wizardData) {
        return new RenameActionPanel(wizardController, wizardData);
    }

    @Override
    public boolean cancel(final Map settings) {
        // return true;
        final boolean dialogShouldClose = JOptionPane.showConfirmDialog(LagisBroker.getInstance().getParentComponent(),
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
                log.debug("WizardFinisher: Flurstueck renamen: ");
            }
            assert !EventQueue.isDispatchThread();
            final FlurstueckSchluesselCustomBean createdKey = (FlurstueckSchluesselCustomBean)wizardData.get(
                    RenameActionPanel.KEY_CREATE_CANDIDATE);
            final FlurstueckSchluesselCustomBean renamedKey = (FlurstueckSchluesselCustomBean)wizardData.get(
                    RenameActionPanel.KEY_RENAME_CANDIDATE);
            if (log.isDebugEnabled()) {
                log.debug("Flurstück das umbenannt werden soll: " + renamedKey.getKeyString());
            }
            if (log.isDebugEnabled()) {
                log.debug("Flurstück in das umbenannt werden soll: " + createdKey.getKeyString());
            }
            try {
                progress.setBusy("Flurstück wird angelegt");
                // CidsBroker.getInstance().createFlurstueck(key);
                CidsBroker.getInstance()
                        .renameFlurstueck(renamedKey, createdKey, LagisBroker.getInstance().getAccountName());
                // TODO schlechte Postion verwirrt den Benutzer wäre besser wenn sie ganz zum Schluss käme

                if ((LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null)
                            && (FlurstueckSchluesselCustomBean.FLURSTUECK_EQUALATOR.pedanticEquals(
                                    LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),
                                    renamedKey)
                                || FlurstueckSchluesselCustomBean.FLURSTUECK_EQUALATOR.pedanticEquals(
                                    LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),
                                    createdKey))) {
                    if (log.isDebugEnabled()) {
                        log.debug("Aktuelles flurstück wurde umbenannt --> update");
                    }
                    try {
                        // TODO nötig?
                        renamedKey.setId(null);
                        renamedKey.setFlurstueckArt(null);
                        //
                        if (FlurstueckSchluesselCustomBean.FLURSTUECK_EQUALATOR.pedanticEquals(
                                        LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),
                                        renamedKey)) {
                            LagisBroker.getInstance().loadFlurstueck(renamedKey);
                        } else if (FlurstueckSchluesselCustomBean.FLURSTUECK_EQUALATOR.pedanticEquals(
                                        LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),
                                        createdKey)) {
                            LagisBroker.getInstance().loadFlurstueck(createdKey);
                        }
                    } catch (Exception ex) {
                        if (log.isDebugEnabled()) {
                            log.debug("Fehler beim updaten/laden der FlurstueckSchluessel/Flurstücks", ex);
                        }
                    }
                }

                if (
                    !FlurstueckSchluesselCustomBean.FLURSTUECK_EQUALATOR.pedanticEquals(
                                LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),
                                createdKey)) {
                    final boolean changeFlurstueck = JOptionPane.showConfirmDialog(LagisBroker.getInstance()
                                    .getParentComponent(),
                            "Möchten Sie zu dem neuangelegten Flurstück wechseln?",
                            "Flurstückwechsel",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                if (changeFlurstueck) {
                                    LagisBroker.getInstance().loadFlurstueck(createdKey);
                                } else {
                                    LagisBroker.getInstance().reloadFlurstueckKeys();
                                }
                            }
                        });
                }
                final Summary summary = Summary.create("Flurstück: \n\n\t" + "\"" + renamedKey.getKeyString()
                                + "\" \n\nkonnte erfolgreich in \n\n\t" + createdKey.getKeyString()
                                + "\n\numbenannt werden.",
                        createdKey);
                progress.finished(summary);
                if (log.isDebugEnabled()) {
                    log.debug("Flurstück konnte erfolgreich umbenannt werden: ");
                }
            } catch (final Exception e) {
                log.error("Fehler beim renamen eines Flurstücks: ", e);
                final StringBuffer buffer = new StringBuffer("Flurstück: \n\t\"" + renamedKey.getKeyString()
                                + "\" \n\nkonnte nicht in\n\t" + createdKey.getKeyString()
                                + "\n\numbenannt werden. Fehler:\n");
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
