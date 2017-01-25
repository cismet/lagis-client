/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * SplitActionSteps.java
 *
 * Created on 10. September 2007, 11:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.wizard.steps;

import org.apache.log4j.Logger;

import org.netbeans.spi.wizard.*;

import java.awt.EventQueue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

import de.cismet.lagis.Exception.ActionNotSuccessfulException;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.wizard.panels.ResultingPanel;
import de.cismet.lagis.wizard.panels.SplitActionChoosePanel;
import de.cismet.lagis.wizard.panels.SummaryPanel;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class SplitActionSteps extends WizardPanelProvider {

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    private SplitActionChoosePanel splitPanel;
    private ResultingPanel resultingPanel;
    private SummaryPanel summaryPanel;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of SplitActionSteps.
     */
    public SplitActionSteps() {
        super(
            "Flurstück umbenennen...",
            new String[] { "Teilung", "Ergebnis", "Zusammenfasusung" },
            new String[] { "Auswahl des Flurstücks", "Flurstücke anlegen", "Zusammenfassung" });
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected JComponent createPanel(final WizardController wizardController, final String id, final Map wizardData) {
        switch (indexOfStep(id)) {
            case 0: {
                this.splitPanel = new SplitActionChoosePanel(
                        wizardController,
                        wizardData,
                        SplitActionChoosePanel.SPLIT_ACTION_MODE);
                return this.splitPanel;
            }
            case 1: {
                resultingPanel = new ResultingPanel(wizardController,
                        wizardData,
                        ResultingPanel.SPLIT_ACTION_MODE);
                return resultingPanel;
            }
            case 2: {
                this.summaryPanel = new SummaryPanel();
                this.summaryPanel.refresh(wizardData);
                return this.summaryPanel;
            }
            default: {
                throw new IllegalArgumentException(id);
            }
        }
    }

    @Override
    public boolean cancel(final Map settings) {
        // return true;
        final boolean dialogShouldClose = JOptionPane.showConfirmDialog(LagisBroker.getInstance().getParentComponent(),
                "Möchten Sie den Bearbeitungsvorgang beenden?") == JOptionPane.OK_OPTION;
        return dialogShouldClose;
    }

    @Override
    protected Object finish(final Map wizardData) throws WizardException {
        return new BackgroundResultCreator();
    }

    @Override
    protected void recycleExistingPanel(final String id,
            final WizardController controller,
            final Map wizardData,
            final JComponent panel) {
        if (log.isDebugEnabled()) {
            log.debug("Recycle existing panel: " + id);
        }

        controller.setProblem(null);
        controller.setBusy(false);

        if (this.splitPanel == panel) {
            this.splitPanel.refresh(wizardData);
        } else if (resultingPanel == panel) {
            resultingPanel.refresh(wizardData);
        } else if (this.summaryPanel == panel) {
            this.summaryPanel.refresh(wizardData);
        } else {
            log.warn("recycleExistingPanel(): Unknown panel " + panel);
        }
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
                log.debug("WizardFinisher: Flurstueck splitten: ");
            }
            assert !EventQueue.isDispatchThread();
            final FlurstueckSchluesselCustomBean splitCandidate = (FlurstueckSchluesselCustomBean)wizardData.get(
                    SplitActionChoosePanel.KEY_SPLIT_CANDIDATE);
            final ArrayList<FlurstueckSchluesselCustomBean> splitKeys = (ArrayList)wizardData.get(
                    ResultingPanel.KEY_SPLIT_KEYS);

            if (log.isDebugEnabled()) {
                log.debug("Flurstück das gesplittet werden soll: " + splitCandidate.getKeyString());
            }
            if (log.isDebugEnabled()) {
                log.debug("Flurstück in entstehen sollen: " + splitKeys);
            }
            try {
                progress.setBusy("Flurstück wird geteilt");
                // CidsBroker.getInstance().createFlurstueck(key);
                for (final FlurstueckSchluesselCustomBean current : splitKeys) {
                    // setzte bei den gesplitteten Flurstück die art des ursprünglichen
                    current.setFlurstueckArt(splitCandidate.getFlurstueckArt());
                }
                CidsBroker.getInstance()
                        .splitFlurstuecke(splitCandidate, splitKeys, LagisBroker.getInstance().getAccountName());
                // TODO schlechte Postion verwirrt den Benutzer wäre besser wenn sie ganz zum Schluss käme
                final StringBuffer resultString = new StringBuffer("Flurstück: \n\t" + "\""
                                + splitCandidate.getKeyString() + "\" \n\nkonnte erfolgreich in die Flurstücke\n");
                final Iterator<FlurstueckSchluesselCustomBean> it = splitKeys.iterator();
                while (it.hasNext()) {
                    resultString.append("\n\t\"").append(it.next().getKeyString()).append("\"");
                }
                resultString.append("\n\n aufgeteilt werden");
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if ((LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null)
                                        && FlurstueckSchluesselCustomBean.FLURSTUECK_EQUALATOR.pedanticEquals(
                                            LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),
                                            splitCandidate)) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Das aktuelle Flurstück wurde geändert --> lade Flurstueck neu");
                                }
                                LagisBroker.getInstance().loadFlurstueck(splitCandidate);
                                return;
                            }
                            for (final FlurstueckSchluesselCustomBean current : splitKeys) {
                                if ((LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null)
                                            && FlurstueckSchluesselCustomBean.FLURSTUECK_EQUALATOR.pedanticEquals(
                                                LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),
                                                current)) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Das aktuelle Flurstück wurde geändert --> lade Flurstueck neu");
                                    }
                                    LagisBroker.getInstance().loadFlurstueck(current);
                                    return;
                                }
                            }
                            LagisBroker.getInstance().reloadFlurstueckKeys();
                        }
                    });

                final Summary summary = Summary.create(resultString.toString(), splitKeys);
                progress.finished(summary);
            } catch (Exception e) {
                // TODO ACTIONNOTSUCCESSFULL
                final StringBuffer resultString = new StringBuffer("Flurstück: \n\t" + "\""
                                + splitCandidate.getKeyString() + "\" \n\nkonnte nicht in die Flurstücke\n");
                final Iterator<FlurstueckSchluesselCustomBean> it = splitKeys.iterator();
                while (it.hasNext()) {
                    resultString.append("\n\t\"").append(it.next().getKeyString()).append("\"");
                }
                resultString.append("\n\n aufgeteilt werden. Fehler:\n");
                if (e instanceof ActionNotSuccessfulException) {
                    final ActionNotSuccessfulException reason = (ActionNotSuccessfulException)e;
                    if (reason.hasNestedExceptions()) {
                        log.error("Nested split Exceptions: ", reason.getNestedExceptions());
                    }
                    resultString.append(reason.getMessage());
                } else {
                    log.error("Unbekannter Fehler: ", e);
                    resultString.append("Unbekannter Fehler bitte wenden Sie sich an Ihren Systemadministrator");
                }
                progress.failed(resultString.toString(), false);
            }
        }
    }
}
