/*
 * KindChangeActionSteps.java
 *
 * Created on 1. Februar 2008, 14:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.wizard.steps;

import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.wizard.panels.ChangeKindActionPanel;
import de.cismet.lagisEE.bean.Exception.ActionNotSuccessfulException;
import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.core.hardwired.FlurstueckArt;
import de.cismet.lagisEE.entity.locking.Sperre;
import java.awt.EventQueue;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Summary;
import org.netbeans.spi.wizard.WizardController;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPanelProvider;

/**
 *
 * @author Sebastian Puhl
 */
public class ChangeKindActionSteps extends WizardPanelProvider{
    
    /** Creates a new instance of KindChangeActionSteps */
    public ChangeKindActionSteps() {
        super("Flurstückart ändern...",
                new String[] { "Flurstück auswählen"},
                new String[] { "Auswahl des Flurstücks"});
    }
    
    protected JComponent createPanel(WizardController wizardController, String id, Map wizardData) {
        return new ChangeKindActionPanel(wizardController, wizardData);
    }
    
    @Override
    public boolean cancel(Map settings) {
        //return true;
        //TODO FEHLER sollte von Wizard abhängig sein
        boolean dialogShouldClose = JOptionPane.showConfirmDialog(LagisBroker.getInstance().getParentComponent(),
                "Möchten Sie den Bearbeitungsvorgang beenden?") == JOptionPane.OK_OPTION;
        return dialogShouldClose;
    }
    
    @Override
    protected Object finish(Map settings) throws WizardException {
        return new BackgroundResultCreator();
    }
    
    static class BackgroundResultCreator extends DeferredWizardResult{
        private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
        public void start(Map wizardData, ResultProgressHandle progress) {
            log.debug("WizardFinisher: Flurstueckart ändern: ");
            assert !EventQueue.isDispatchThread();
            final FlurstueckSchluessel key = (FlurstueckSchluessel)wizardData.get(ChangeKindActionPanel.KEY_CHANGE_CANDIDATE);
            final FlurstueckArt newArt = (FlurstueckArt)wizardData.get(ChangeKindActionPanel.KEY_NEW_KIND);
            Sperre sperre =null;
            try {
                //TODO besser alles in Server
                Sperre other = EJBroker.getInstance().isLocked(key);
                if(other == null){
                    sperre = EJBroker.getInstance().createLock(new Sperre(key,LagisBroker.getInstance().getAccountName()));
                    if(sperre != null){
                        progress.setBusy("Flurstückart wird geändert");
                        key.setFlurstueckArt(newArt);
                        EJBroker.getInstance().modifyFlurstueckSchluessel(key);
                        final Flurstueck changedFlurstueck = EJBroker.getInstance().retrieveFlurstueck(key);
                        if(changedFlurstueck.getRechteUndBelastungen() != null){
                            changedFlurstueck.getRechteUndBelastungen().clear();
                            EJBroker.getInstance().modifyFlurstueck(changedFlurstueck);
                        }
                        EJBroker.getInstance().releaseLock(sperre);
                        //TODO schlechte Postion verwirrt den Benutzer wäre besser wenn sie ganz zum Schluss käme
                        if(LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null && FlurstueckSchluessel.FLURSTUECK_EQUALATOR.pedanticEquals(LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),key)){
                            log.debug("Art des aktuellen flurstücks wurde geändert --> update");
                            try{
                                LagisBroker.getInstance().loadFlurstueck(key);
                            }catch(Exception ex){
                                log.debug("Fehler beim updaten/laden der FlurstueckSchluessel/Flurstücks", ex);
                            }
                        } else {
                            final boolean changeFlurstueck = JOptionPane.showConfirmDialog(LagisBroker.getInstance().getParentComponent(),
                                    "Möchten Sie zu dem geänderten Flurstück wechseln?","Flurstückwechsel",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
                            try{
                                EventQueue.invokeLater(new Runnable() {
                                    public void run() {
                                        if(changeFlurstueck){
                                            LagisBroker.getInstance().loadFlurstueck(key);
                                        } else{
                                            LagisBroker.getInstance().reloadFlurstueckKeys();
                                        }
                                    }
                                });
                            }catch(Exception ex){
                                log.debug("Fehler beim updaten/laden der FlurstueckSchluessel/Flurstücks", ex);
                            }
                        }
                        Summary summary = Summary.create("Die Art des Flurstück: \n\t"+"\""+key.getKeyString()+"\" \n\nkonnte erfolgreich auf "+"\""+newArt.getBezeichnung()+"\""+" geändert werden",key);
                        progress.finished(summary);
                    } else {
                        progress.failed("Es war nicht möglich die Art des Flurstücks:\n\t\""+key.getKeyString()+"\"\nzu ändern, es konnte keine Sperre angelegt werden.",false);
                    }
                } else {
                    progress.failed("Es war nicht möglich die Art des Flurstücks:\n\t\""+key.getKeyString()+"\"\nzu ändern, es ist von einem anderen Benutzer gesperrt: "+other.getBenutzerkonto(),false);
                }
            } catch (Exception e) {
                try{
                    EJBroker.getInstance().releaseLock(sperre);
                }catch(Exception ex){
                    log.error("Fehler beim lösen der Sperre",ex);
                }
                final StringBuffer buffer = new StringBuffer("Die Art des Flurstücks: \n\t\""+key.getKeyString()+"\" \n\nkonnte nicht geändert werden. Fehler:");
                if(e instanceof ActionNotSuccessfulException){
                    ActionNotSuccessfulException reason = (ActionNotSuccessfulException) e;
                    if(reason.hasNestedExceptions()){
                        log.error("Nested changeKind Exceptions: ",reason.getNestedExceptions());
                    }
                    buffer.append(reason.getMessage());
                } else {
                    log.error("Unbekannter Fehler: ",e);
                    buffer.append("Unbekannter Fehler bitte wenden Sie sich an Ihren Systemadministrator");
                }
                progress.failed(buffer.toString(), false);
            }
        }
    }
    
}
