/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * JoinSplitActionSteps.java
 *
 * Created on 11. September 2007, 14:53
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
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.wizard.panels.JoinActionChoosePanel;
import de.cismet.lagis.wizard.panels.ResultingPanel;
import de.cismet.lagis.wizard.panels.SplitActionChoosePanel;

import de.cismet.lagisEE.bean.Exception.ActionNotSuccessfulException;

import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class JoinSplitActionSteps extends WizardPanelProvider {

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    private ResultingPanel resultingPanel;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of JoinSplitActionSteps.
     */
    public JoinSplitActionSteps() {
        super(
            "Flurstück zusammenlegen/teilen...",
            new String[] { "Zusammenlegen", "Teilen", "Ergebnis" },
            new String[] { "Zusammenlegen", "Teilen", "Anlegen" });
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected JComponent createPanel(final WizardController wizardController, final String id, final Map wizardData) {
        switch (indexOfStep(id)) {
            case 0: {
                return new JoinActionChoosePanel(wizardController, wizardData);
            }
            case 1: {
                return new SplitActionChoosePanel(
                        wizardController,
                        wizardData,
                        SplitActionChoosePanel.SPLIT_JOIN_ACTION_MODE);
            }
            case 2: {
                resultingPanel = new ResultingPanel(
                        wizardController,
                        wizardData,
                        ResultingPanel.SPLIT_JOIN_ACTION_MODE);
                return resultingPanel;
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
        if (resultingPanel != null) {
            resultingPanel.refreshCount();
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
            if (log.isDebugEnabled()) {
                log.debug("WizardFinisher: Flurstueck joinen/splitten: ");
            }
            assert !EventQueue.isDispatchThread();
            final ArrayList<FlurstueckSchluessel> joinKeys = (ArrayList)wizardData.get(
                    JoinActionChoosePanel.KEY_JOIN_KEYS);
            final ArrayList<FlurstueckSchluessel> splitKeys = (ArrayList)wizardData.get(ResultingPanel.KEY_SPLIT_KEYS);
            if (log.isDebugEnabled()) {
                log.debug("Flurstücke die zusammengelegt werden sollen: " + joinKeys);
                log.debug("Flurstück die geteilt werden sollen: " + splitKeys);
            }
            try {
                progress.setBusy("Flurstück wird geteilt");
                // EJBroker.getInstance().createFlurstueck(key);
                for (final FlurstueckSchluessel current : splitKeys) {
                    // setzte bei den gesplitteten Flurstück die art eines der ursprünglichen
                    current.setFlurstueckArt(joinKeys.get(0).getFlurstueckArt());
                }
                EJBroker.getInstance()
                        .joinSplitFlurstuecke(joinKeys, splitKeys, LagisBroker.getInstance().getAccountName());
                final StringBuffer resultString = new StringBuffer("Die Flurstücke:");
                // \n\t"+"\""+splitCandidate.getKeyString()+"\" \n\nkonnte erfolgreich in die Flurstücke\n");
                final Iterator<FlurstueckSchluessel> joinIt = joinKeys.iterator();
                while (joinIt.hasNext()) {
                    resultString.append("\n\t\"" + joinIt.next().getKeyString() + "\"");
                }
                resultString.append("\n\nkonnten erfolgreich in die Flurstücke\n\n");
                final Iterator<FlurstueckSchluessel> splitIt = splitKeys.iterator();
                while (splitIt.hasNext()) {
                    resultString.append("\n\t\"" + splitIt.next().getKeyString() + "\"");
                }
                resultString.append("\n\n aufgeteilt werden");
                boolean isCurrentFlurstueckChanged = false;
                FlurstueckSchluessel tmp = null;
                for (final FlurstueckSchluessel current : joinKeys) {
                    if ((LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null)
                                && FlurstueckSchluessel.FLURSTUECK_EQUALATOR.pedanticEquals(
                                    LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),
                                    current)) {
                        if (log.isDebugEnabled()) {
                            log.debug("Das aktuelle Flurstück gehört zu den zusammengelegten Flurstücken");
                        }
                        isCurrentFlurstueckChanged = true;
                        tmp = current;
                        break;
                    }
                }
                if (!isCurrentFlurstueckChanged) {
                    for (final FlurstueckSchluessel current : splitKeys) {
                        if ((LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null)
                                    && FlurstueckSchluessel.FLURSTUECK_EQUALATOR.pedanticEquals(
                                        LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),
                                        current)) {
                            if (log.isDebugEnabled()) {
                                log.debug("Das aktuelle Flurstück gehört zu den gesplitteten Flurstücken");
                            }
                            isCurrentFlurstueckChanged = true;
                            tmp = current;
                            break;
                        }
                    }
                }
                final FlurstueckSchluessel keyToReload = tmp;
                if (isCurrentFlurstueckChanged) {
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                LagisBroker.getInstance().loadFlurstueck(keyToReload);
                            }
                        });
                } else {
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                LagisBroker.getInstance().reloadFlurstueckKeys();
                            }
                        });
                }
                final Summary summary = Summary.create(resultString.toString(), splitKeys);
                progress.finished(summary);
            } catch (final Exception e) {
                log.error("Fehler beim joinSplit von Flurstücken: ", e);
                final StringBuffer buffer = new StringBuffer("Die Flurstücke:");
                final Iterator<FlurstueckSchluessel> joinIt = joinKeys.iterator();
                while (joinIt.hasNext()) {
                    buffer.append("\n\t\"" + joinIt.next().getKeyString() + "\"");
                }
                buffer.append("\n\nkonnten nicht in die Flurstücke\n\n");
                final Iterator<FlurstueckSchluessel> splitIt = splitKeys.iterator();
                while (splitIt.hasNext()) {
                    buffer.append("\n\t\"" + splitIt.next().getKeyString() + "\"");
                }
                buffer.append("\n\n aufgeteilt werden. Fehler:\n ");
                if (e instanceof ActionNotSuccessfulException) {
                    final ActionNotSuccessfulException reason = (ActionNotSuccessfulException)e;
                    if (reason.hasNestedExceptions()) {
                        log.error("Nested joinSplit Exceptions: ", reason.getNestedExceptions());
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
