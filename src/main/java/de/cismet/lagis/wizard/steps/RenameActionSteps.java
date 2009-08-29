/*
 * RenameActionSteps.java
 *
 * Created on 9. September 2007, 17:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.wizard.steps;

import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.wizard.panels.CreateActionPanel;
import de.cismet.lagis.wizard.panels.RenameActionPanel;
import de.cismet.lagisEE.bean.Exception.ActionNotSuccessfulException;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
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
public class RenameActionSteps extends WizardPanelProvider {
    
    /** Creates a new instance of RenameActionSteps */
    public RenameActionSteps() {
        super("Flurstück umbenennen...",
                new String[] { "Flurstück auswählen"},
                new String[] { "Auswahl des Flurstücks"});
    }
    
    protected JComponent createPanel(WizardController wizardController, String id, Map wizardData) {
        return new RenameActionPanel(wizardController, wizardData);
    }
    
    public boolean cancel(Map settings) {
        //return true;
        boolean dialogShouldClose = JOptionPane.showConfirmDialog(LagisBroker.getInstance().getParentComponent(),
                "Möchten Sie den Bearbeitungsvorgang beenden?") == JOptionPane.OK_OPTION;
        return dialogShouldClose;
    }
    
    protected Object finish(Map settings) throws WizardException {
        return new BackgroundResultCreator();
    }
    
    
    static class BackgroundResultCreator extends DeferredWizardResult{
        private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
        public void start(final Map wizardData,final ResultProgressHandle progress) {
            log.debug("WizardFinisher: Flurstueck renamen: ");
            assert !EventQueue.isDispatchThread();
            final FlurstueckSchluessel createdKey = (FlurstueckSchluessel)wizardData.get(RenameActionPanel.KEY_CREATE_CANDIDATE);
            final FlurstueckSchluessel renamedKey = (FlurstueckSchluessel)wizardData.get(RenameActionPanel.KEY_RENAME_CANDIDATE);
            log.debug("Flurstück das umbenannt werden soll: "+renamedKey.getKeyString());
            log.debug("Flurstück in das umbenannt werden soll: "+createdKey.getKeyString());
            try {
                progress.setBusy("Flurstück wird angelegt");
                //EJBroker.getInstance().createFlurstueck(key);
                EJBroker.getInstance().renameFlurstueck(renamedKey,createdKey,LagisBroker.getInstance().getAccountName());
                //TODO schlechte Postion verwirrt den Benutzer wäre besser wenn sie ganz zum Schluss käme
                
                if(LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null && (FlurstueckSchluessel.FLURSTUECK_EQUALATOR.pedanticEquals(LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),renamedKey) || FlurstueckSchluessel.FLURSTUECK_EQUALATOR.pedanticEquals(LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),createdKey))){
                    log.debug("Aktuelles flurstück wurde umbenannt --> update");
                    try{
                        //TODO nötig?
                        renamedKey.setId(null);
                        renamedKey.setFlurstueckArt(null);
                        //
                        if(FlurstueckSchluessel.FLURSTUECK_EQUALATOR.pedanticEquals(LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),renamedKey)){                            
                            LagisBroker.getInstance().loadFlurstueck(renamedKey);
                        } else if(FlurstueckSchluessel.FLURSTUECK_EQUALATOR.pedanticEquals(LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),createdKey)) {                            
                            LagisBroker.getInstance().loadFlurstueck(createdKey);
                        }
                    }catch(Exception ex){
                        log.debug("Fehler beim updaten/laden der FlurstueckSchluessel/Flurstücks", ex);
                    }
                }
                
                if(!FlurstueckSchluessel.FLURSTUECK_EQUALATOR.pedanticEquals(LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),createdKey)){
                    final boolean changeFlurstueck = JOptionPane.showConfirmDialog(LagisBroker.getInstance().getParentComponent(),
                            "Möchten Sie zu dem neuangelegten Flurstück wechseln?","Flurstückwechsel",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            if(changeFlurstueck){
                                LagisBroker.getInstance().loadFlurstueck(createdKey);
                            } else {
                                LagisBroker.getInstance().reloadFlurstueckKeys();
                            }
                        }
                    });
                }
                final Summary summary = Summary.create("Flurstück: \n\n\t"+"\""+renamedKey.getKeyString()+"\" \n\nkonnte erfolgreich in \n\n\t"+createdKey.getKeyString()+"\n\numbenannt werden.",createdKey);
                progress.finished(summary);
                log.debug("Flurstück konnte erfolgreich umbenannt werden: ");
            } catch (final Exception e) {
                log.error("Fehler beim renamen eines Flurstücks: ",e);
                final StringBuffer buffer = new StringBuffer("Flurstück: \n\t\""+renamedKey.getKeyString()+"\" \n\nkonnte nicht in\n\t"+createdKey.getKeyString()+"\n\numbenannt werden. Fehler:\n");
                if(e instanceof ActionNotSuccessfulException){
                    ActionNotSuccessfulException reason = (ActionNotSuccessfulException) e;
                    if(reason.hasNestedExceptions()){
                        log.error("Nested Rename Exceptions: ",reason.getNestedExceptions());
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
