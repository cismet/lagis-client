/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * HistoricActionSteps.java
 *
 * Created on 10. September 2007, 10:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.wizard.steps;

import org.apache.log4j.Logger;

import org.netbeans.spi.wizard.*;

import java.awt.EventQueue;

import java.util.Date;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;
import de.cismet.cids.custom.beans.lagis.SperreCustomBean;

import de.cismet.lagis.Exception.ActionNotSuccessfulException;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.wizard.panels.HistoricActionPanel;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class HistoricActionSteps extends WizardPanelProvider {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of HistoricActionSteps.
     */
    public HistoricActionSteps() {
        super(
            "Flurstück historisch setzen...",
            new String[] { "Flurstück auswählen" },
            new String[] { "Auswahl des Flurstücks" });
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected JComponent createPanel(final WizardController wizardController, final String id, final Map wizardData) {
        return new HistoricActionPanel(wizardController, wizardData);
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
                log.debug("WizardFinisher: Flurstueck historisch setzen: ");
            }
            assert !EventQueue.isDispatchThread();
            final FlurstueckSchluesselCustomBean historicKey = (FlurstueckSchluesselCustomBean)wizardData.get(
                    HistoricActionPanel.KEY_HISTORIC_CANDIDATE);
            final Date histDate = (Date)wizardData.get(
                    HistoricActionPanel.KEY_HISTORIC_DATE);
            if (log.isDebugEnabled()) {
                log.debug("Flurstück das historisch gesetzt werden soll: " + historicKey.getKeyString());
            }

            SperreCustomBean sperre = null;
            try {
                progress.setBusy("Flurstück wird historisch gesetzt");
                // CidsBroker.getInstance().createFlurstueck(key);
                // HistoricResult result = CidsBroker.getInstance().setFlurstueckHistoric(historicKey);
                // TODO schlechte Postion verwirrt den Benutzer wäre besser wenn sie ganz zum Schluss käme
                // TODO besser setHistoric mit sperre versehen als immer die sperre vorher zu setzen
                final SperreCustomBean other = CidsBroker.getInstance().isLocked(historicKey);
                if (other == null) {
                    sperre = CidsBroker.getInstance()
                                .createLock(SperreCustomBean.createNew(
                                            historicKey,
                                            LagisBroker.getInstance().getAccountName()));
                    if (sperre != null) {
                        System.out.println("datum:" + histDate);
                        CidsBroker.getInstance().setFlurstueckHistoric(historicKey, histDate, true);
                        final Summary summary;
                        CidsBroker.getInstance().releaseLock(sperre);
                        if ((LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null)
                                    && FlurstueckSchluesselCustomBean.FLURSTUECK_EQUALATOR.pedanticEquals(
                                        LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),
                                        historicKey)) {
                            if (log.isDebugEnabled()) {
                                log.debug("Aktuelles flurstück wurde historisch --> update");
                            }
                            try {
                                LagisBroker.getInstance().loadFlurstueck(historicKey);
                            } catch (Exception ex) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Fehler beim updaten/laden der FlurstueckSchluessel/Flurstücks", ex);
                                }
                            }
                        } else {
                            final boolean changeFlurstueck = JOptionPane.showConfirmDialog(LagisBroker.getInstance()
                                            .getParentComponent(),
                                    "Möchten Sie zu dem Flurstück wechseln?",
                                    "Flurstückwechsel",
                                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (changeFlurstueck) {
                                            LagisBroker.getInstance().loadFlurstueck(historicKey);
                                        } else {
                                            LagisBroker.getInstance().reloadFlurstueckKeys();
                                        }
                                    }
                                });
                        }
                        summary = Summary.create("Flurstück: \n\t" + "\"" + historicKey.getKeyString()
                                        + "\" \n\nkonnte erfolgreich historisch gesetzt werden",
                                historicKey);
                        progress.finished(summary);
                    } else {
                        progress.failed("Es war nicht möglich das Flurstück:\n\t\"" + historicKey.getKeyString()
                                    + "\"\nhistorisch zu setzen, es konnte keine Sperre angelegt werden.",
                            false);
                    }
                } else {
                    progress.failed("Es war nicht möglich das Flurstück:\n\t\"" + historicKey.getKeyString()
                                + "\"\nhistorisch zu setzen, es ist von einem anderen Benutzer gesperrt: "
                                + other.getBenutzerkonto(),
                        false);
                }
            } catch (Exception e) {
                log.error("Fehler beim historischsetzen eines Flurstücks: ", e);
                try {
                    CidsBroker.getInstance().releaseLock(sperre);
                } catch (Exception ex) {
                    log.error("Fehler beim lösen der Sperre", ex);
                }
                final StringBuffer buffer = new StringBuffer("Es war nicht möglich das Flurstück:\n\t\""
                                + historicKey.getKeyString() + "\"\nhistorisch zu setzen bzw. zu löschen. Fehler:\n");
                if (e instanceof ActionNotSuccessfulException) {
                    final ActionNotSuccessfulException reason = (ActionNotSuccessfulException)e;
                    if (reason.hasNestedExceptions()) {
                        log.error("Nested setFlurstueckHistoric Exceptions: ", reason.getNestedExceptions());
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
