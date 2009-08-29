/*
 * FlaecheEditor.java
 *
 * Created on 11. Januar 2008, 16:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.editor;

import de.cismet.lagis.models.documents.DateDocumentModel;
import de.cismet.lagis.models.documents.FlaecheDocumentModel;
import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.Validator;
import java.text.ParseException;
import java.util.Date;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.DateFormatter;

/**
 *
 * @author Sebastian Puhl
 */
public class FlaecheEditor extends DefaultCellEditor {
    //JFormattedTextField ftf;
    JTextField txtFlaeche;
    //NumberFormat integerFormat;
    //DateFormat dateFormat = LagisBroker.getDateFormatter();
    private Integer minimum, maximum;
    private boolean DEBUG = false;
    private Validator validator;
    
    
    /** Creates a new instance of FlaecheEditor */
    public FlaecheEditor() {
        super(new JTextField());
        delegate = new EditorDelegate() {
            public void setValue(Object value) {
                if(value instanceof Integer){
                    txtFlaeche.setText(value.toString());
                } else {
                    txtFlaeche.setText((value != null) ? value.toString() : "0");
                }
            }
            
            public Object getCellEditorValue() {
                return txtFlaeche.getText();
            }
        };
        txtFlaeche = (JTextField)getComponent();
        txtFlaeche.setDocument(new FlaecheDocumentModel());
        validator = new Validator(txtFlaeche);
        validator.reSetValidator((Validatable)txtFlaeche.getDocument());
        
        //Set up the editor for the integer cells.
        //integerFormat = NumberFormat.getIntegerInstance();
        //NumberFormatter intFormatter = new NumberFormatter(integerFormat);        
        
        
//        ftf.setFormatterFactory(
//                new DefaultFormatterFactory(dateFormatter));
        //ftf.setValue(dateFormat.format());
        txtFlaeche.setHorizontalAlignment(SwingConstants.CENTER);
        //txtDate.setFocusLostBehavior(JFormattedTextField.PERSIST);
        
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
    
      //Override to ensure that the value remains an Integer.
    public Object getCellEditorValue() {
        JTextField txtFlaeche = (JTextField)getComponent();
        String t = txtFlaeche.getText();
//        if (o instanceof Date) {
//            return o;        
//        } else {            
            try {
                if(validator.getValidationState() == Validatable.VALID){
                    return Integer.parseInt(t);
                } else {                    
                    return t;
                }                
            } catch (NumberFormatException exc) {
                //System.err.println("getCellEditorValue: can't parse o: " + t);
                //exc.printStackTrace();
                return t;
            }
       // }
    }
    
     public int getValidationState(){
        return validator.getValidationState();
    }
    
     public String getText(){
        return txtFlaeche.getText();
    }
    
}
