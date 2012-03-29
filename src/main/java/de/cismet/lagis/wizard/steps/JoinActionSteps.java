/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * JoinActionSteps.java
 *
 * Created on 10. September 2007, 15:47
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.beans.verdis_grundis.*;

import de.cismet.lagis.Exception.ActionNotSuccessfulException;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.wizard.panels.JoinActionChoosePanel;
import de.cismet.lagis.wizard.panels.ResultingPanel;
import de.cismet.lagis.wizard.panels.SummaryPanel;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class JoinActionSteps extends WizardPanelProvider {

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    private ResultingPanel resultingPanel;
    private SummaryPanel summaryPanel;
    private JoinActionChoosePanel joinPanel;

    private final Map wizardData;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of JoinActionSteps.
     */
    public JoinActionSteps() {
        super(
            "Flurstück umbenennen...",
            new String[] { "Zusammenlegen", "Ergebnis", "Zusammenfassung" },
            new String[] { "Auswahl der Flurstücke", "Flurstück anlegen", "Zusammenfassung" });

        wizardData = new HashMap();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected JComponent createPanel(final WizardController wizardController, final String id, final Map wizardData) {
        switch (indexOfStep(id)) {
            case 0: {
                this.joinPanel = new JoinActionChoosePanel(wizardController, this.wizardData);
                return this.joinPanel;
            }
            case 1: {
                resultingPanel = new ResultingPanel(wizardController, this.wizardData, ResultingPanel.JOIN_ACTION_MODE);
                return resultingPanel;
            }
            case 2: {
                this.summaryPanel = new SummaryPanel();
                this.summaryPanel.refresh(this.wizardData);
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
    protected Object finish(final Map settings) throws WizardException {
        return new BackgroundResultCreator(this.wizardData);
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

        if (this.joinPanel == panel) {
            this.joinPanel.refresh(this.wizardData);
        } else if (resultingPanel == panel) {
            resultingPanel.refresh(this.wizardData);
        } else if (this.summaryPanel == panel) {
            this.summaryPanel.refresh(this.wizardData);
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

        private final Map wizardData;

        private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new BackgroundResultCreator object.
         *
         * @param  wizardData  DOCUMENT ME!
         */
        public BackgroundResultCreator(final Map wizardData) {
            this.wizardData = wizardData;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void start(Map wizardData, final ResultProgressHandle progress) {
            wizardData = this.wizardData;

            if (log.isDebugEnabled()) {
                log.debug("WizardFinisher: Flurstueck joinen: ");
            }
            assert !EventQueue.isDispatchThread();
            final FlurstueckSchluesselCustomBean joinKey = (FlurstueckSchluesselCustomBean)wizardData.get(
                    ResultingPanel.KEY_JOIN_KEY);
            final ArrayList<FlurstueckSchluesselCustomBean> joinKeys = (ArrayList)wizardData.get(
                    JoinActionChoosePanel.KEY_JOIN_KEYS);
            if (log.isDebugEnabled()) {
                log.debug("Flurstücke die gejoined werden sollen: " + joinKeys);
                log.debug("Flurstück das entsteht : " + ((joinKey == null) ? "null" : joinKey.getKeyString()));
            }
            try {
                progress.setBusy("Flurstück wird gejoined");
                // CidsBroker.getInstance().createFlurstueck(key);
                // setzte bei dem gejointen Flurstück die art der anderen
                joinKey.setFlurstueckArt(joinKeys.get(0).getFlurstueckArt());
                final FlurstueckCustomBean newFlurstueck = CidsBroker.getInstance()
                            .joinFlurstuecke(joinKeys, joinKey, LagisBroker.getInstance().getAccountName());
                // TODO schlechte Postion verwirrt den Benutzer wäre besser wenn sie ganz zum Schluss käme
                final StringBuffer resultString = new StringBuffer("Die Flurstücke:\n");
                final Iterator<FlurstueckSchluesselCustomBean> it = joinKeys.iterator();
                while (it.hasNext()) {
                    resultString.append("\n\t\"").append(it.next().getKeyString()).append("\"");
                }
                resultString.append("\n\nkonnten erfolgreich zu dem Flurstück:\n\n\t\"")
                        .append(joinKey.getKeyString())
                        .append("\" \n\n vereinigt werden");

                if ((LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null)
                            && FlurstueckSchluesselCustomBean.FLURSTUECK_EQUALATOR.pedanticEquals(
                                LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),
                                joinKey)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Das aktuelle Flurstück ist == dem zusammengelegetn");
                    }
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                LagisBroker.getInstance().loadFlurstueck(joinKey);
                            }
                        });
                } else {
                    boolean isCurrentFlurstueckChanged = false;
                    for (final FlurstueckSchluesselCustomBean current : joinKeys) {
                        if ((LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null)
                                    && FlurstueckSchluesselCustomBean.FLURSTUECK_EQUALATOR.pedanticEquals(
                                        LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),
                                        current)) {
                            if (log.isDebugEnabled()) {
                                log.debug("Das aktuelle Flurstück gehört zu den zusammengelegten Flurstücken");
                            }
                            isCurrentFlurstueckChanged = true;
                        }
                    }

                    if (isCurrentFlurstueckChanged) {
                        EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    for (final FlurstueckSchluesselCustomBean key : joinKeys) {
                                        if ((LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null)
                                                    && FlurstueckSchluesselCustomBean.FLURSTUECK_EQUALATOR
                                                    .pedanticEquals(
                                                        LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),
                                                        key)) {
                                            LagisBroker.getInstance().loadFlurstueck(key);
                                        }
                                    }
                                }
                            });
                    }

                    final boolean changeFlurstueck = JOptionPane.showConfirmDialog(LagisBroker.getInstance()
                                    .getParentComponent(),
                            "Möchten Sie zu dem neuangelegten Flurstück wechseln?",
                            "Flurstückwechsel",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                if (changeFlurstueck) {
                                    LagisBroker.getInstance().loadFlurstueck(joinKey);
                                } else {
                                    LagisBroker.getInstance().reloadFlurstueckKeys();
                                }
                            }
                        });
                }

                final Summary summary = Summary.create(resultString.toString(), joinKeys);
                progress.finished(summary);
            } catch (final Exception ex) {
                // TODO ActionNotSuccessfull Exception
                final StringBuffer resultString = new StringBuffer("Die Flurstücke:");
                final Iterator<FlurstueckSchluesselCustomBean> it = joinKeys.iterator(); //
                while (it.hasNext()) {
                    resultString.append("\n\t\"").append(it.next().getKeyString()).append("\"");
                }
                resultString.append("\nkonnten nicht erfolgreich zu dem Flurstück:\n\t\"")
                        .append(joinKey.getKeyString())
                        .append("\" \n\n vereinigt werden. Fehler:\n");

                if (ex instanceof ActionNotSuccessfulException) {
                    final ActionNotSuccessfulException reason = (ActionNotSuccessfulException)ex;
                    if (reason.hasNestedExceptions()) {
                        log.error("Nested Rename Exceptions: ", reason.getNestedExceptions());
                    }
                    resultString.append(reason.getMessage());
                } else {
                    log.error("Unbekannter Fehler: ", ex);
                    resultString.append("Unbekannter Fehler bitte wenden Sie sich an Ihren Systemadministrator");
                }
                progress.failed(resultString.toString(), false);
            }
        }
    }
}
