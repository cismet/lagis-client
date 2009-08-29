/*
 * EuroEditor.java
 *
 * Created on 27. Mai 2007, 12:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.editor;

import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.models.documents.AmountDocumentModel;
import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.Validator;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;
import org.apache.log4j.Logger;

/**
 *
 * @author Puhl
 */
public class EuroEditor extends DefaultCellEditor {
    
    NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.GERMANY);
    DecimalFormat df = LagisBroker.getCurrencyFormatter();
    
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    
    //JFormattedTextField ftf;
    JTextField txtEuro;
    //NumberFormat integerFormat;
    DecimalFormat euroFormat = LagisBroker.getCurrencyFormatter();
    private Integer minimum, maximum;
    private boolean DEBUG = false;
    private Validator validator;
    
    public EuroEditor() {
        super(new JTextField());
        delegate = new EditorDelegate() {
            public void setValue(Object value) {
                if(value instanceof Number){
                    txtEuro.setText((value != null) ? nf.format((Number) value) : "");
                } else {
                    txtEuro.setText((value != null) ? value.toString() : "");
                }
            }
            
            public Object getCellEditorValue() {
                return txtEuro.getText();
            }
        };
        txtEuro = (JTextField)getComponent();
        txtEuro.setDocument(new AmountDocumentModel());
        validator = new Validator(txtEuro);
        validator.reSetValidator((Validatable)txtEuro.getDocument());
        
        //Set up the editor for the integer cells.
        //integerFormat = NumberFormat.getIntegerInstance();
        //NumberFormatter intFormatter = new NumberFormatter(integerFormat);
        NumberFormatter euroFormatter = new NumberFormatter();
        euroFormatter.setFormat(euroFormat);
        
        
//        ftf.setFormatterFactory(
//                new DefaultFormatterFactory(dateFormatter));
        //ftf.setValue(dateFormat.format());
        txtEuro.setHorizontalAlignment(SwingConstants.CENTER);
        //txtEuro.setFocusLostBehavior(JFormattedTextField.PERSIST);
        
        //React when the user presses Enter while the editor is
        //active.  (Tab is handled as specified by
        //JFormattedTextField's focusLostBehavior property.)
//        ftf.getInputMap().put(KeyStroke.getKeyStroke(
//                                        KeyEvent.VK_ENTER, 0),
//                                        "check");
//        ftf.getActionMap().put("check", new AbstractAction() {
//            public void actionPerformed(ActionEvent e) {
//		if (!ftf.isEditValid()) { //The text is invalid.
//                    if (userSaysRevert()) { //reverted
//		        ftf.postActionEvent(); //inform the editor
//		    }
//                } else try {              //The text is valid,
//                    ftf.commitEdit();     //so use it.
//                    ftf.postActionEvent(); //stop editing
//                } catch (java.text.ParseException exc) { }
//            }
//        });
    }
    
    //Override to invoke setValue on the formatted text field.
//    public Component getTableCellEditorComponent(JTable table,
//            Object value, boolean isSelected,
//            int row, int column) {
//        JTextField txtDate =
//            (JTextField)super.getTableCellEditorComponent(
//                table, value, isSelected, row, column);
//        ftf.setValue(value);
//        return ftf;
//    }
//    private Double parseEuro(String valueToCheck){
//        if(valueToCheck != null){
//            //valueToCheck.matches("^[1-9][0-9]{2}?(\\.[0-9]{3})*+,.*"
//            if(valueToCheck.matches(".*(\\.\\.\\.*).*") || valueToCheck.matches(".*(\\...?\\.).*") || valueToCheck.matches(".*(\\.,).*") || valueToCheck.matches("^\\.")){
//                return Double.NaN;
//            }
//
//            try {
//                Number betrag = nf.parse(valueToCheck);
//                return betrag.doubleValue();
//            } catch (Exception ex1) {
//                log.error("Fehler Betrag parsen: Betrag hat nicht die Form ##0,00 € ",ex1);
//                try{
//                    Number betrag = nf.parse(valueToCheck.trim()+" €");
//                    return betrag.doubleValue();
//                }catch (Exception ex2) {
//                    log.error("Fehler Betrag parsen: Betrag hat nicht die Form ##0,00",ex2);
//                }
////
//                if(valueToCheck.length() == 0){
//                    Double nullDouble = null;
//                    log.error("Betrag ist null");
//                    return nullDouble;
//                }
//                //TODO GOOD Sentence
//                return null;
//            }
//        } else if(valueToCheck == null){
//            return null;
//        } else{
//            return null;
//        }
//    }
    
    //Override to ensure that the value remains an Integer.
    public Object getCellEditorValue() {
        JTextField txtEuro = (JTextField)getComponent();
        String t = txtEuro.getText();
        validator.validationStateChanged(this);
//        if (o instanceof Date) {
//            return o;
//        } else {
        try {
            if(validator.getValidationState() == Validatable.VALID){
//                try{
//                    return new Double(nf.parse(t).doubleValue());
//                }catch(Exception ex){
//                    log.debug("nf formatter erfolglos");
//                };
//                try{
//                    return new Double(df.parse(t).doubleValue());
//                }catch(Exception ex){
//                    log.debug("df formatter erfolglos");
//                };
//                return new Double(Double.parseDouble(t));
                return ((AmountDocumentModel)txtEuro.getDocument()).getCurrentAmount();
            } else {
                return t;
            }
        } catch (Exception ex) {
            log.debug("getCellEditorValue: can't parse o: " + t);
            //exc.printStackTrace();
            return t;
        }
        // }
    }
    
    //Override to check whether the edit is valid,
    //setting the value if it is and complaining if
    //it isn't.  If it's OK for the editor to go
    //away, we need to invoke the superclass's version
    //of this method so that everything gets cleaned up.
//    public boolean stopCellEditing() {
//        JFormattedTextField ftf = (JFormattedTextField)getComponent();
//        if (ftf.isEditValid()) {
//            try {
//                ftf.commitEdit();
//            } catch (java.text.ParseException exc) { }
//
//        } else { //text is invalid
//            if (!userSaysRevert()) { //user wants to edit
//	        return false; //don't let the editor go away
//	    }
//        }
//        return super.stopCellEditing();
//    }
    
    /**
     * Lets the user know that the text they entered is
     * bad. Returns true if the user elects to revert to
     * the last good value.  Otherwise, returns false,
     * indicating that the user wants to continue editing.
     */
//    protected boolean userSaysRevert() {
//        Toolkit.getDefaultToolkit().beep();
//        ftf.selectAll();
//        Object[] options = {"Edit",
//                            "Revert"};
//        int answer = JOptionPane.showOptionDialog(
//            SwingUtilities.getWindowAncestor(ftf),
//            "The value must be an integer between "
//            + minimum + " and "
//            + maximum + ".\n"
//            + "You can either continue editing "
//            + "or revert to the last valid value.",
//            "Invalid Text Entered",
//            JOptionPane.YES_NO_OPTION,
//            JOptionPane.ERROR_MESSAGE,
//            null,
//            options,
//            options[1]);
//
//        if (answer == 1) { //Revert!
//            ftf.setValue(ftf.getValue());
//	    return true;
//        }
//	return false;
//    }
    
    public int getValidationState(){
        return validator.getValidationState();
    }
    
    public String getText(){
        return txtEuro.getText();
    }
    
    
}