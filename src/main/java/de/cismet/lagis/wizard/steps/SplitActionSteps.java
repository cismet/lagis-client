/*
 * SplitActionSteps.java
 *
 * Created on 10. September 2007, 11:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.wizard.steps;

import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.wizard.panels.ResultingPanel;
import de.cismet.lagis.wizard.panels.SplitActionChoosePanel;
import de.cismet.lagisEE.bean.Exception.ActionNotSuccessfulException;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Iterator;
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
public class SplitActionSteps  extends WizardPanelProvider {
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    /** Creates a new instance of SplitActionSteps */
    public SplitActionSteps() {
        super("Flurstück umbenennen...",
                new String[] { "Teilung","Ergebnis"},
                new String[] { "Auswahl des Flurstücks","Flurstücke anlegen"});
    }
    
    
    private ResultingPanel resultingPanel;
    protected JComponent createPanel(WizardController wizardController, String id, Map wizardData) {
        switch ( indexOfStep( id ) ) {
            case 0 :
                return new SplitActionChoosePanel(wizardController, wizardData,SplitActionChoosePanel.SPLIT_ACTION_MODE);
            case 1 :
                resultingPanel =new ResultingPanel(wizardController, wizardData, ResultingPanel.SPLIT_ACTION_MODE);
                return resultingPanel;
            default :
                throw new IllegalArgumentException( id );
        }
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
    
    protected void recycleExistingPanel(String id, WizardController controller, Map wizardData, JComponent panel) {
        log.debug("Recycle existing panel: "+id);
        if(resultingPanel != null){
            resultingPanel.refreshCount();
        }
    }
    
    
    
    static class BackgroundResultCreator extends DeferredWizardResult{
        private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
        public void start(Map wizardData, ResultProgressHandle progress) {
            log.debug("WizardFinisher: Flurstueck splitten: ");
            assert !EventQueue.isDispatchThread();
            final FlurstueckSchluessel splitCandidate = (FlurstueckSchluessel)wizardData.get(SplitActionChoosePanel.KEY_SPLIT_CANDIDATE);
            final ArrayList<FlurstueckSchluessel> splitKeys = (ArrayList)wizardData.get(ResultingPanel.KEY_SPLIT_KEYS);
            log.debug("Flurstück das gesplittet werden soll: "+splitCandidate.getKeyString());
            log.debug("Flurstück in entstehen sollen: "+splitKeys);
            try {
                progress.setBusy("Flurstück wird geteilt");
                //EJBroker.getInstance().createFlurstueck(key);
                for(FlurstueckSchluessel current:splitKeys){
                    //setzte bei den gesplitteten Flurstück die art des ursprünglichen
                    current.setFlurstueckArt(splitCandidate.getFlurstueckArt());
                }
                EJBroker.getInstance().splitFlurstuecke(splitCandidate,splitKeys,LagisBroker.getInstance().getAccountName());
                //TODO schlechte Postion verwirrt den Benutzer wäre besser wenn sie ganz zum Schluss käme
                StringBuffer resultString = new StringBuffer("Flurstück: \n\t"+"\""+splitCandidate.getKeyString()+"\" \n\nkonnte erfolgreich in die Flurstücke\n");
                Iterator<FlurstueckSchluessel>  it = splitKeys.iterator();
                while(it.hasNext()){
                    resultString.append("\n\t\""+it.next().getKeyString()+"\"");
                }
                resultString.append("\n\n aufgeteilt werden");
                EventQueue.invokeLater(new Runnable() {
                    public void run() {                        
                        if(LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null && FlurstueckSchluessel.FLURSTUECK_EQUALATOR.pedanticEquals(LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),splitCandidate)){                                                                                    
                                log.debug("Das aktuelle Flurstück wurde geändert --> lade Flurstueck neu");
                                LagisBroker.getInstance().loadFlurstueck(splitCandidate);                            
                                return;
                        }
                        for(FlurstueckSchluessel current:splitKeys){
                            if(LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null && FlurstueckSchluessel.FLURSTUECK_EQUALATOR.pedanticEquals(LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),current)){                                                                                    
                                log.debug("Das aktuelle Flurstück wurde geändert --> lade Flurstueck neu");
                                LagisBroker.getInstance().loadFlurstueck(current);                            
                                return;
                            }
                        }
                        LagisBroker.getInstance().reloadFlurstueckKeys();
                    }
                });
                Summary summary = Summary.create(resultString.toString(),splitKeys);
                progress.finished(summary);
            } catch (Exception e) {
                //TODO ACTIONNOTSUCCESSFULL
                StringBuffer resultString = new StringBuffer("Flurstück: \n\t"+"\""+splitCandidate.getKeyString()+"\" \n\nkonnte nicht in die Flurstücke\n");
                Iterator<FlurstueckSchluessel>  it = splitKeys.iterator();
                while(it.hasNext()){
                    resultString.append("\n\t\""+it.next().getKeyString()+"\"");
                }
                resultString.append("\n\n aufgeteilt werden. Fehler:\n");
                if(e instanceof ActionNotSuccessfulException){
                    ActionNotSuccessfulException reason = (ActionNotSuccessfulException) e;
                    if(reason.hasNestedExceptions()){
                        log.error("Nested split Exceptions: ",reason.getNestedExceptions());
                    }
                    resultString.append(reason.getMessage());
                } else {
                    log.error("Unbekannter Fehler: ",e);
                    resultString.append("Unbekannter Fehler bitte wenden Sie sich an Ihren Systemadministrator");
                }
                progress.failed(resultString.toString(), false);
            }
        }
    }
    
}
