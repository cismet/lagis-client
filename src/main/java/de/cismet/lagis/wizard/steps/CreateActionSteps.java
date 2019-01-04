/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * CreateActionSteps.java
 *
 * Created on 8. September 2007, 13:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.wizard.steps;

import org.apache.log4j.Logger;

import org.netbeans.spi.wizard.*;

import java.awt.EventQueue;

import java.util.Collection;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.beans.lagis.FlurstueckArtCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.gui.main.LagisApp;

import de.cismet.lagis.wizard.panels.CreateActionPanel;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class CreateActionSteps extends WizardPanelProvider {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of CreateActionSteps.
     */
    public CreateActionSteps() {
        super(
            "Flurstück anlegen...",
            new String[] { "Flurstück auswählen" },
            new String[] { "Auswahl des Flurstücks" });
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected JComponent createPanel(final WizardController wizardController, final String id, final Map wizardData) {
        return new CreateActionPanel(wizardController, wizardData);
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
                log.debug("WizardFinisher: Flurstueck anlegen: ");
            }
            assert !EventQueue.isDispatchThread();
            final FlurstueckSchluesselCustomBean key = (FlurstueckSchluesselCustomBean)wizardData.get(
                    CreateActionPanel.KEY_CREATE_CANDIDATE);
            final boolean isStaedtisch = (Boolean)wizardData.get(CreateActionPanel.KEY_IS_STAEDTISCH);
            try {
                progress.setBusy("Flurstück wird angelegt");
                final Collection<FlurstueckArtCustomBean> flurstueckArten = LagisBroker.getInstance()
                            .getAllFlurstueckArten();
                if (isStaedtisch) {
                    for (final FlurstueckArtCustomBean art : flurstueckArten) {
                        if (art.getBezeichnung().equals(
                                        FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                            key.setFlurstueckArt(art);
                        }
                    }
                } else {
                    for (final FlurstueckArtCustomBean art : flurstueckArten) {
                        if (art.getBezeichnung().equals(
                                        FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX)) {
                            key.setFlurstueckArt(art);
                        }
                    }
                }
                if (key.getFlurstueckArt() == null) {
                    throw new Exception("Die Flurstücksart des Servers passte nicht");
                }
                LagisBroker.getInstance().createFlurstueck(key);
                // TODO schlechte Postion verwirrt den Benutzer wäre besser wenn sie ganz zum Schluss käme

                if ((LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null)
                            && FlurstueckSchluesselCustomBean.FLURSTUECK_EQUALATOR.pedanticEquals(
                                LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),
                                key)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Aktuelles flurstück wurde eingepflegt --> update");
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
                            "Möchten Sie zu dem neuangelegten Flurstück wechseln?",
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
                final Summary summary = Summary.create("Flurstück: \n\t" + "\"" + key.getKeyString()
                                + "\" \n\nkonnte erfolgreich angelegt werden",
                        key);
                progress.finished(summary);
            } catch (Exception e) {
                progress.failed("Flurstück: \n\t\"" + key.getKeyString() + "\" \n\nkonnte nicht angelegt werden",
                    false);
                log.warn("Flurstück konnte nicht durch Wizard angelegt werden", e);
            }
        }
    }
}
