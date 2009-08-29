/*
 * FlaecheDocumentModel.java
 *
 * Created on 11. Januar 2008, 16:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.models.documents;

import org.apache.log4j.Logger;

/**
 *
 * @author Sebastian Puhl
 */
public class FlaecheDocumentModel extends SimpleDocumentModel {
   private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    
    
    /** Creates a new instance of FlaecheDocumentModel */
    public FlaecheDocumentModel() {
    }
    
    public void assignValue(String newValue) {
        log.debug("new Value: "+ newValue);
        valueToCheck=newValue;
        fireValidationStateChanged(this);
    }
    
    public void assignValue(Integer flaeche){
        
    }
    
     public int getStatus() {
        if(valueToCheck != null){
            try {
                log.debug("ValueToCheck: "+valueToCheck);
                Integer flaeche = Integer.parseInt(valueToCheck);
                statusDescription="";
                assignValue(flaeche);                
                return VALID;
            } catch (Exception ex) {
                if(valueToCheck.length() == 0){
                statusDescription="";               
                Integer nullDouble = 0;
                assignValue(nullDouble);                
                return VALID;                
                }
                log.error("Fehler  parsen: ",ex);
                statusDescription="Unkorrektes Format. Bitte geben sie eine Fläche nach folgendem Format ein #.##";
                return ERROR;
            }
        } else if(valueToCheck == null){
            Integer tmp = 0;
            assignValue(tmp);
            statusDescription="";
            return VALID;
        }
        return ERROR;
    }
    
}
