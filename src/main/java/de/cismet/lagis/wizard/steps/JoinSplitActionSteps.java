/*
 * JoinSplitActionSteps.java
 *
 * Created on 11. September 2007, 14:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.wizard.steps;

import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.wizard.panels.JoinActionChoosePanel;
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
public class JoinSplitActionSteps extends WizardPanelProvider {
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    /** Creates a new instance of JoinSplitActionSteps */
    public JoinSplitActionSteps() {
        super("Flurstück zusammenlegen/teilen...",
                new String[] { "Zusammenlegen","Teilen","Ergebnis"},
                new String[] { "Zusammenlegen","Teilen","Anlegen"});
    }
    
    private ResultingPanel resultingPanel;
    protected JComponent createPanel(WizardController wizardController, String id, Map wizardData) {
        switch ( indexOfStep( id ) ) {
            case 0 :
                return new JoinActionChoosePanel(wizardController, wizardData);
            case 1 :
                return new SplitActionChoosePanel(wizardController, wizardData,SplitActionChoosePanel.SPLIT_JOIN_ACTION_MODE);
            case 2 :
                resultingPanel =new ResultingPanel(wizardController, wizardData, ResultingPanel.SPLIT_JOIN_ACTION_MODE);
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
            log.debug("WizardFinisher: Flurstueck joinen/splitten: ");
            assert !EventQueue.isDispatchThread();
            ArrayList<FlurstueckSchluessel> joinKeys =  (ArrayList)wizardData.get(JoinActionChoosePanel.KEY_JOIN_KEYS);
            ArrayList<FlurstueckSchluessel> splitKeys = (ArrayList)wizardData.get(ResultingPanel.KEY_SPLIT_KEYS);
            log.debug("Flurstücke die zusammengelegt werden sollen: "+joinKeys);
            log.debug("Flurstück die geteilt werden sollen: "+splitKeys);
            try {
                progress.setBusy("Flurstück wird geteilt");
                //EJBroker.getInstance().createFlurstueck(key);
                for(FlurstueckSchluessel current:splitKeys){
                    //setzte bei den gesplitteten Flurstück die art eines der ursprünglichen
                    current.setFlurstueckArt(joinKeys.get(0).getFlurstueckArt());
                }
                EJBroker.getInstance().joinSplitFlurstuecke(joinKeys,splitKeys,LagisBroker.getInstance().getAccountName());
                StringBuffer resultString = new StringBuffer("Die Flurstücke:");
                //\n\t"+"\""+splitCandidate.getKeyString()+"\" \n\nkonnte erfolgreich in die Flurstücke\n");
                Iterator<FlurstueckSchluessel> joinIt = joinKeys.iterator();
                while(joinIt.hasNext()){
                    resultString.append("\n\t\""+joinIt.next().getKeyString()+"\"");
                }
                resultString.append("\n\nkonnten erfolgreich in die Flurstücke\n\n");
                Iterator<FlurstueckSchluessel>  splitIt = splitKeys.iterator();
                while(splitIt.hasNext()){
                    resultString.append("\n\t\""+splitIt.next().getKeyString()+"\"");
                }
                resultString.append("\n\n aufgeteilt werden");
                boolean isCurrentFlurstueckChanged=false;
                FlurstueckSchluessel tmp=null;
                for(FlurstueckSchluessel current:joinKeys){
                    if(LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null && FlurstueckSchluessel.FLURSTUECK_EQUALATOR.pedanticEquals(LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),current)){
                        log.debug("Das aktuelle Flurstück gehört zu den zusammengelegten Flurstücken");
                        isCurrentFlurstueckChanged=true;
                        tmp=current;
                        break;
                    }
                }
                if(!isCurrentFlurstueckChanged){
                    for(FlurstueckSchluessel current:splitKeys){
                        if(LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null && FlurstueckSchluessel.FLURSTUECK_EQUALATOR.pedanticEquals(LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),current)){
                            log.debug("Das aktuelle Flurstück gehört zu den gesplitteten Flurstücken");
                            isCurrentFlurstueckChanged=true;
                            tmp=current;
                            break;
                        }
                    }
                }
                final FlurstueckSchluessel keyToReload = tmp;
                if(isCurrentFlurstueckChanged){
                    EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        LagisBroker.getInstance().loadFlurstueck(keyToReload);
                    }
                });
                } else {
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            LagisBroker.getInstance().reloadFlurstueckKeys();
                        }
                    });
                }
                Summary summary = Summary.create(resultString.toString(),splitKeys);
                progress.finished(summary);
            } catch (final Exception e) {
                log.error("Fehler beim joinSplit von Flurstücken: ",e);
                final StringBuffer buffer = new StringBuffer("Die Flurstücke:");
                Iterator<FlurstueckSchluessel> joinIt = joinKeys.iterator();
                while(joinIt.hasNext()){
                    buffer.append("\n\t\""+joinIt.next().getKeyString()+"\"");
                }
                buffer.append("\n\nkonnten nicht in die Flurstücke\n\n");
                Iterator<FlurstueckSchluessel>  splitIt = splitKeys.iterator();
                while(splitIt.hasNext()){
                    buffer.append("\n\t\""+splitIt.next().getKeyString()+"\"");
                }
                buffer.append("\n\n aufgeteilt werden. Fehler:\n ");
                if(e instanceof ActionNotSuccessfulException){
                    ActionNotSuccessfulException reason = (ActionNotSuccessfulException) e;
                    if(reason.hasNestedExceptions()){
                        log.error("Nested joinSplit Exceptions: ",reason.getNestedExceptions());
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
