/*
 * ContinuationWizard.java
 *
 * Created on 8. September 2007, 13:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.wizard;
import de.cismet.lagis.wizard.steps.ActivateActionSteps;
import de.cismet.lagis.wizard.steps.ChangeKindActionSteps;
import de.cismet.lagis.wizard.steps.CreateActionSteps;
import de.cismet.lagis.wizard.steps.HistoricActionSteps;
import de.cismet.lagis.wizard.steps.InitialStep;
import de.cismet.lagis.wizard.steps.JoinActionSteps;
import de.cismet.lagis.wizard.steps.JoinSplitActionSteps;
import de.cismet.lagis.wizard.steps.RenameActionSteps;
import de.cismet.lagis.wizard.steps.SplitActionSteps;
import java.util.Map;
import org.apache.log4j.Logger;
import org.netbeans.spi.wizard.WizardBranchController;
import org.netbeans.spi.wizard.WizardPanelProvider;

/**
 *
 * @author Sebastian Puhl
 */
public class ContinuationWizard extends WizardBranchController {
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    /** Creates a new instance of ContinuationWizard */
    public ContinuationWizard() {
        super(new InitialStep());
    }
    
    private WizardPanelProvider createActionSteps;
    private WizardPanelProvider renameActionSteps;
    private WizardPanelProvider historicActionSteps;
    private WizardPanelProvider splitActionSteps;
    private WizardPanelProvider joinActionSteps;
    private WizardPanelProvider joinSplitActionSteps;
    private WizardPanelProvider changeKindActionSteps;
    private WizardPanelProvider activateActionSteps;
    
    protected WizardPanelProvider getPanelProviderForStep(String step, Map collectedData) {
        //There's only one branch point, so we don't need to test the
        //value of step
        //log.debug("Aktueller Step: "+step);
        Object action = collectedData.get(InitialStep.KEY_ACTION);        
        if (InitialStep.VALUE_CREATE.equals(action)) {
            //log.debug("Create Aktion ausgewühlt");
            return getCreateActionSteps();
        } else if (InitialStep.VALUE_RENAME.equals(action)) {
            //log.debug("Rename Aktion ausgewühlt");
            return getRenameActionSteps();
        }else if (InitialStep.VALUE_HISTORIC.equals(action)) {
            //log.debug("Historic Aktion ausgewählt");
            return getHistoricActionSteps();
        }else if (InitialStep.VALUE_SPLIT.equals(action)) {
            log.debug("Split Aktion ausgewählt");
            return getSplitActionSteps();
        }else if (InitialStep.VALUE_JOIN.equals(action)) {
            //log.debug("Join Aktion ausgewählt");
            return getJoinActionSteps();
        }else if (InitialStep.VALUE_SPLIT_JOIN.equals(action)) {
            //log.debug("Split/Join Aktion ausgewählt");
            return getJoinSplitActionSteps();
        }else if (InitialStep.VALUE_CHANGE_KIND.equals(action)) {            
            return getChangeKindActionSteps();
        }else if (InitialStep.VALUE_ACTIVATE.equals(action)) {            
            return getActivateActionSteps();
        } else {
            return null;
        }
    }
    
    private WizardPanelProvider getCreateActionSteps(){
        if(createActionSteps == null){
            createActionSteps = new CreateActionSteps();
        }
        return createActionSteps;
    }
    
     private WizardPanelProvider getRenameActionSteps(){
        if(renameActionSteps == null){
            renameActionSteps = new RenameActionSteps();
        }
        return renameActionSteps;
    }
     
      private WizardPanelProvider getHistoricActionSteps(){
        if(historicActionSteps == null){
            historicActionSteps = new HistoricActionSteps();
        }
        return historicActionSteps;
    }
      
      private WizardPanelProvider getSplitActionSteps(){
        if(splitActionSteps == null){
            splitActionSteps = new SplitActionSteps();
        }
        return splitActionSteps;
    }
    
    private WizardPanelProvider getJoinActionSteps(){
        if(joinActionSteps == null){
            joinActionSteps = new JoinActionSteps();
        }
        return joinActionSteps;
    }
    
    private WizardPanelProvider getJoinSplitActionSteps(){
        if(joinSplitActionSteps == null){
            joinSplitActionSteps = new JoinSplitActionSteps();
        }
        return joinSplitActionSteps;
    }
    
    private WizardPanelProvider getChangeKindActionSteps(){
        if(changeKindActionSteps == null){
            changeKindActionSteps = new ChangeKindActionSteps();
        }
        return changeKindActionSteps;
    }
    
    private WizardPanelProvider getActivateActionSteps(){
        if(activateActionSteps == null){
            activateActionSteps = new ActivateActionSteps();
        }
        return activateActionSteps;
    }
    
}
