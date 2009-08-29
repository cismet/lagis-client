/*
 * AmountDocumentModel.java
 *
 * Created on 27. April 2007, 09:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.models.documents;

import java.text.NumberFormat;
import java.util.Locale;
import org.apache.log4j.Logger;

/**
 *
 * @author Puhl
 */
public class AmountDocumentModel extends SimpleDocumentModel {
    private static final String EURO="\u20AC";
    NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.GERMANY);
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    Double currentAmount;
    /**
     * Creates a new instance of AmountDocumentModel
     */
    public AmountDocumentModel() {
    }
    
    //TODO in SimpleDocumentModelimplementieren
    public void assignValue(String newValue) {
        log.debug("new Value: "+ newValue);
        valueToCheck=newValue;
        fireValidationStateChanged(this);        
    }
    
    public void assignValue(Double amount){
        currentAmount = amount;
    }
    
    public Double getCurrentAmount(){
        return currentAmount;
    }
    
    public int getStatus() {
        if(valueToCheck != null){
            //valueToCheck.matches("^[1-9][0-9]{2}?(\\.[0-9]{3})*+,.*"
            if(valueToCheck.matches(".*(\\.\\.\\.*).*") || valueToCheck.matches(".*(\\...?\\.).*") || valueToCheck.matches(".*(\\.,).*") || valueToCheck.matches("^\\.")){
                statusDescription="Es dürfen nicht mehrere Punke unmittelbar aufeinander folgen!";                
                return ERROR;
            }
            
            try {
                Number betrag = nf.parse(valueToCheck);
                statusDescription="";                
                assignValue(betrag.doubleValue());
                return VALID;
            } catch (Exception ex1) {
                log.error("Fehler Betrag parsen: Betrag hat nicht die Form ##0,00 € ",ex1);
                try{
                    Number betrag = nf.parse(valueToCheck.trim()+" "+EURO);
                    statusDescription="";
                    assignValue(betrag.doubleValue());
                    return VALID;
                }catch (Exception ex2) {
                    log.error("Fehler Betrag parsen: Betrag hat nicht die Form ##0,00",ex2);
                }
//
                if(valueToCheck.length() == 0){
                    statusDescription="";
                    Double nullDouble = null;
                    assignValue(nullDouble);
                    log.error("Betrag ist null");
                    return VALID;
                }
                //TODO GOOD Sentence
                statusDescription="Falsches Format";
                return ERROR;
            }
        } else if(valueToCheck == null){
            statusDescription="";
            return VALID;
        } else{
            statusDescription="Falsches Format: Bitte einen Betrag in der Form 1.222,00 € eingeben";
            return ERROR;
        }
    }
    
    
    
}
