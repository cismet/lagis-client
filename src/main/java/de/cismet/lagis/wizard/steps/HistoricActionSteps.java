/*
 * HistoricActionSteps.java
 *
 * Created on 10. September 2007, 10:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.wizard.steps;

import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.wizard.panels.HistoricActionPanel;
import de.cismet.lagisEE.bean.Exception.ActionNotSuccessfulException;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
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
public class HistoricActionSteps extends WizardPanelProvider {
    
    /** Creates a new instance of HistoricActionSteps */
    public HistoricActionSteps() {
        super("Flurstück historisch setzen...",
                new String[] { "Flurstück auswählen"},
                new String[] { "Auswahl des Flurstücks"});
    }
    
    protected JComponent createPanel(WizardController wizardController, String id, Map wizardData) {
        return new HistoricActionPanel(wizardController, wizardData);
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
        public void start(Map wizardData, ResultProgressHandle progress) {
            log.debug("WizardFinisher: Flurstueck historisch setzen: ");
            assert !EventQueue.isDispatchThread();
            final FlurstueckSchluessel historicKey = (FlurstueckSchluessel)wizardData.get(HistoricActionPanel.KEY_HISTORIC_CANDIDATE);
            log.debug("Flurstück das historisch gesetzt werden soll: "+historicKey.getKeyString());
            Sperre sperre=null;
            try {
                progress.setBusy("Flurstück wird historisch gesetzt");
                //EJBroker.getInstance().createFlurstueck(key);
                //HistoricResult result = EJBroker.getInstance().setFlurstueckHistoric(historicKey);
                //TODO schlechte Postion verwirrt den Benutzer wäre besser wenn sie ganz zum Schluss käme
                //TODO besser setHistoric mit sperre versehen als immer die sperre vorher zu setzen
                Sperre other = EJBroker.getInstance().isLocked(historicKey);
                if(other == null){
                    sperre = EJBroker.getInstance().createLock(new Sperre(historicKey,LagisBroker.getInstance().getAccountName()));
                    if(sperre != null){
                        if(EJBroker.getInstance().setFlurstueckHistoric(historicKey)){
                            Summary summary;
                            EJBroker.getInstance().releaseLock(sperre);
                            if(LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null && FlurstueckSchluessel.FLURSTUECK_EQUALATOR.pedanticEquals(LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),historicKey)){
                                log.debug("Aktuelles flurstück wurde historisch --> update");
                                try{
                                    LagisBroker.getInstance().loadFlurstueck(historicKey);
                                }catch(Exception ex){
                                    log.debug("Fehler beim updaten/laden der FlurstueckSchluessel/Flurstücks", ex);
                                }
                            } else {
                                final boolean changeFlurstueck = JOptionPane.showConfirmDialog(LagisBroker.getInstance().getParentComponent(),
                                        "Möchten Sie zu dem Flurstück wechseln?","Flurstückwechsel",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
                                EventQueue.invokeLater(new Runnable() {
                                    public void run() {
                                        if(changeFlurstueck){
                                            LagisBroker.getInstance().loadFlurstueck(historicKey);
                                        } else {
                                            LagisBroker.getInstance().reloadFlurstueckKeys();
                                        }
                                    }
                                });
                            }                            
                            summary = Summary.create("Flurstück: \n\t"+"\""+historicKey.getKeyString()+"\" \n\nkonnte erfolgreich historisch gesetzt werden",historicKey);
                            progress.finished(summary);
                        } else {
                            progress.failed("Es war nicht möglich das Flurstück:\n\t\""+historicKey.getKeyString()+"\"\nhistorisch zu setzen, bitte wenden Sie sich an Ihren Systemadministrator",false);
                        }
                    } else {
                        progress.failed("Es war nicht möglich das Flurstück:\n\t\""+historicKey.getKeyString()+"\"\nhistorisch zu setzen, es konnte keine Sperre angelegt werden.",false);
                    }
                } else {
                    progress.failed("Es war nicht möglich das Flurstück:\n\t\""+historicKey.getKeyString()+"\"\nhistorisch zu setzen, es ist von einem anderen Benutzer gesperrt: "+other.getBenutzerkonto(),false);
                }
            } catch (Exception e) {
                log.error("Fehler beim historischsetzen eines Flurstücks: ",e);
                try{
                    EJBroker.getInstance().releaseLock(sperre);
                }catch(Exception ex){
                    log.error("Fehler beim lösen der Sperre",ex);
                }
                final StringBuffer buffer = new StringBuffer("Es war nicht möglich das Flurstück:\n\t\""+historicKey.getKeyString()+"\"\nhistorisch zu setzen bzw. zu löschen. Fehler:\n");
                if(e instanceof ActionNotSuccessfulException){
                    ActionNotSuccessfulException reason = (ActionNotSuccessfulException) e;
                    if(reason.hasNestedExceptions()){
                        log.error("Nested setFlurstueckHistoric Exceptions: ",reason.getNestedExceptions());
                    }
                    buffer.append(reason.getMessage());
                } else {
                    log.error("Unbekannter Fehler: ",e);
                    buffer.append("Unbekannter Fehler bitte wenden Sie sich an Ihren Systemadministrator");
                }
                progress.failed(buffer.toString(),false);
            }
        }
    }
    
}
