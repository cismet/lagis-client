/*
 * JoinActionSteps.java
 *
 * Created on 10. September 2007, 15:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.wizard.steps;

import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.wizard.panels.JoinActionChoosePanel;
import de.cismet.lagis.wizard.panels.ResultingPanel;
import de.cismet.lagisEE.bean.Exception.ActionNotSuccessfulException;
import de.cismet.lagisEE.entity.core.Flurstueck;
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
public class JoinActionSteps extends WizardPanelProvider{
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    /** Creates a new instance of JoinActionSteps */
    public JoinActionSteps() {
        super("Flurstück umbenennen...",
                new String[] { "Zusammenlegen","Ergebnis"},
                new String[] { "Auswahl der Flurstücke","Flurstück anlegen"});
    }
    
    
    private ResultingPanel resultingPanel;
    protected JComponent createPanel(WizardController wizardController, String id, Map wizardData) {
        switch ( indexOfStep( id ) ) {
            case 0 :
                return new JoinActionChoosePanel(wizardController, wizardData);
            case 1 :
                resultingPanel =new ResultingPanel(wizardController, wizardData, ResultingPanel.JOIN_ACTION_MODE);
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
    
    static class BackgroundResultCreator extends DeferredWizardResult{
        private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
        public void start(final Map wizardData,final ResultProgressHandle progress) {
            log.debug("WizardFinisher: Flurstueck joinen: ");
            assert !EventQueue.isDispatchThread();
            final FlurstueckSchluessel joinKey = (FlurstueckSchluessel)wizardData.get(ResultingPanel.KEY_JOIN_KEY);
            final ArrayList<FlurstueckSchluessel> joinKeys = (ArrayList)wizardData.get(JoinActionChoosePanel.KEY_JOIN_KEYS);
            log.debug("Flurstücke die gejoined werden sollen: "+joinKeys);
            log.debug("Flurstück das entsteht : "+joinKey.getKeyString());
            try {
                progress.setBusy("Flurstück wird gejoined");
                //EJBroker.getInstance().createFlurstueck(key);
                //setzte bei dem gejointen Flurstück die art der anderen
                joinKey.setFlurstueckArt(joinKeys.get(0).getFlurstueckArt());
                final Flurstueck newFlurstueck = EJBroker.getInstance().joinFlurstuecke(joinKeys,joinKey,LagisBroker.getInstance().getAccountName());
                //TODO schlechte Postion verwirrt den Benutzer wäre besser wenn sie ganz zum Schluss käme
                StringBuffer resultString = new StringBuffer("Die Flurstücke:\n");
                Iterator<FlurstueckSchluessel>  it = joinKeys.iterator();
                while(it.hasNext()){
                    resultString.append("\n\t\""+it.next().getKeyString()+"\"");
                }
                resultString.append("\n\nkonnten erfolgreich zu dem Flurstück:\n\n\t\""+joinKey.getKeyString()+"\" \n\n vereinigt werden");
                
                
                if(LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null && FlurstueckSchluessel.FLURSTUECK_EQUALATOR.pedanticEquals(LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),joinKey)){
                    log.debug("Das aktuelle Flurstück ist == dem zusammengelegetn");
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            LagisBroker.getInstance().loadFlurstueck(joinKey);
                        }
                    });
                } else {
                    boolean isCurrentFlurstueckChanged=false;
                    for(FlurstueckSchluessel current:joinKeys){
                        if(LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null && FlurstueckSchluessel.FLURSTUECK_EQUALATOR.pedanticEquals(LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),current)){
                            log.debug("Das aktuelle Flurstück gehört zu den zusammengelegten Flurstücken");
                            isCurrentFlurstueckChanged=true;
                        }
                    }
                    
                    if(isCurrentFlurstueckChanged){
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                for(FlurstueckSchluessel key:joinKeys){
                                    if(LagisBroker.getInstance().getCurrentFlurstueckSchluessel() != null && FlurstueckSchluessel.FLURSTUECK_EQUALATOR.pedanticEquals(LagisBroker.getInstance().getCurrentFlurstueckSchluessel(),key)){                                        
                                            LagisBroker.getInstance().loadFlurstueck(key);                                        
                                    }
                                }
                            }
                        });
                    }
                    
                    final boolean changeFlurstueck = JOptionPane.showConfirmDialog(LagisBroker.getInstance().getParentComponent(),
                            "Möchten Sie zu dem neuangelegten Flurstück wechseln?","Flurstückwechsel",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            if(changeFlurstueck){
                                LagisBroker.getInstance().loadFlurstueck(joinKey);
                            } else {
                                LagisBroker.getInstance().reloadFlurstueckKeys();
                            }
                        }
                    });
                }
                
                final Summary summary = Summary.create(resultString.toString(),joinKeys);
                progress.finished(summary);
            } catch (final Exception ex) {
                //TODO ActionNotSuccessfull Exception
                final StringBuffer resultString = new StringBuffer("Die Flurstücke:");
                final Iterator<FlurstueckSchluessel>  it = joinKeys.iterator();                 //
                while(it.hasNext()){
                    resultString.append("\n\t\""+it.next().getKeyString()+"\"");
                }
                resultString.append("\nkonnten nicht erfolgreich zu dem Flurstück:\n\t\""+joinKey.getKeyString()+"\" \n\n vereinigt werden. Fehler:\n");
                if(ex instanceof ActionNotSuccessfulException){
                    ActionNotSuccessfulException reason = (ActionNotSuccessfulException) ex;
                    if(reason.hasNestedExceptions()){
                        log.error("Nested Rename Exceptions: ",reason.getNestedExceptions());
                    }
                    resultString.append(reason.getMessage());
                } else {
                    log.error("Unbekannter Fehler: ",ex);
                    resultString.append("Unbekannter Fehler bitte wenden Sie sich an Ihren Systemadministrator");
                }
                progress.failed(resultString.toString(), false);
            }
        }
    }
    
}
