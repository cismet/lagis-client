/*
 * SimpleDocumentModel.java
 *
 * Created on 24. Januar 2005, 16:26
 */

package de.cismet.lagis.models.documents;
import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.ValidationStateChangedListener;
import java.awt.Component;
import javax.swing.text.*;
/**
 *
 * @author hell
 */
public class SimpleDocumentModel extends PlainDocument implements Validatable{
    java.util.Vector listeners=new java.util.Vector();
    protected String statusDescription="";
    protected String valueToCheck=null;
    //Zum Überschreiben
    public boolean acceptChanges(String newValue) {
        return true;
    }
    
    public void assignValue(String newValue) {
        
    }
    
    public void insertNewString(String string, AttributeSet attributes) throws BadLocationException {
        if (string==null) {
            return;
        }
        super.remove(0,getLength());
        insertString(0,string,null);
    }
    
    
    
    public void insertString(int offset,String string, AttributeSet attributes) throws BadLocationException {
        if (string==null) {
            return;
        } else {
            String newValue;
            int length = getLength();
            if (length == 0) {
                newValue = string;
            } else {
                String currentContent = getText(0, length);
                StringBuffer currentBuffer = new StringBuffer(currentContent);
                currentBuffer.insert(offset, string);
                newValue = currentBuffer.toString();
            }
            
            if (acceptChanges(newValue)) {
                assignValue(newValue);
                super.insertString(offset,string,attributes);
            }
        }
    }
    public void remove(int offs,int len) throws BadLocationException {
        StringBuffer currentBuffer = new StringBuffer(getText(0, getLength()));
        currentBuffer.delete(offs,offs+len);
        String newValue=currentBuffer.toString();
        if (acceptChanges(newValue)) {
            assignValue(newValue);
            super.remove(offs,len);
        }
    }
    
    public void clear(int offs,int len) throws BadLocationException{
        StringBuffer currentBuffer = new StringBuffer(getText(0, getLength()));
        currentBuffer.delete(offs,offs+len);
        String newValue=currentBuffer.toString();
        if (acceptChanges(newValue)) {
            super.remove(offs,len);
        }
    }
    
    public void removeValidationStateChangedListener(ValidationStateChangedListener l) {
        listeners.remove(l);
    }
    
    public void addValidationStateChangedListener(ValidationStateChangedListener l) {
        listeners.add(l);
    }
    
    public String getValidationMessage(){
        return statusDescription;
    }
    
    public int getStatus() {
        if(valueToCheck != null && valueToCheck.length() <= 255){
            statusDescription="";
            return Validatable.VALID;
        } else if(valueToCheck == null) {
            statusDescription="";
            return Validatable.VALID;
        } else {
            statusDescription="Ein Text muss weniger als 255 Zeichen haben";
            return Validatable.ERROR;
        }
    }
    
    
    public void fireValidationStateChanged(Object validatedObject) {
        java.util.Iterator it=listeners.iterator();
        while (it.hasNext()) {
            ValidationStateChangedListener v=(ValidationStateChangedListener)it.next();
            v.validationStateChanged(this);
        }
    }
    
    public void showAssistent(Component parent) {
        
    }
    
    
    
}
