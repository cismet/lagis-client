/*
 * DateDocumentModel.java
 *
 * Created on 27. April 2007, 11:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.models.documents;

import de.cismet.lagis.broker.LagisBroker;
import java.text.DateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author Puhl
 */
public class DateDocumentModel extends SimpleDocumentModel {
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private DateFormat dateFormatter = LagisBroker.getDateFormatter();    
    /** Creates a new instance of DateDocumentModel */
    public DateDocumentModel() {
    }
    
    public void assignValue(String newValue) {
        log.debug("new Value: "+ newValue);
        valueToCheck=newValue;
        fireValidationStateChanged(this);
    }
    
    public void assignValue(Date date){
        
    }
    
    public int getStatus() {
        if(valueToCheck != null && valueToCheck != "" && valueToCheck.length() != 0){            
            try {
                log.debug("ValueToCheck: "+valueToCheck+" StringLänge: "+valueToCheck.length());
                Date date = dateFormatter.parse(valueToCheck) ;
                statusDescription="";
                assignValue(date);                
                return VALID;
            } catch (Exception ex) {
                if(valueToCheck.length() == 0){
                statusDescription="";
                Date nullDate = null;
                assignValue(nullDate);                
                return VALID;                
                }
                log.error("Fehler date parsen: ",ex);
                statusDescription="Unkorrektes Format. Bitte geben sie ein Datum nach folgendem Format ein TT.MM.JJ";
                return ERROR;
            }
        } else if(valueToCheck == null || valueToCheck == "" || valueToCheck.length() == 0){            
            Date tmp = null;
            assignValue(tmp);
            statusDescription="";
            return VALID;
        }       
        return ERROR;
    }
    
    
}
