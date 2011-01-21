/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * DateEditor.java
 *
 * Created on 17. Mai 2007, 13:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.editor;

import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Date;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.DateFormatter;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.models.documents.DateDocumentModel;

import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.Validator;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class DateEditor extends DefaultCellEditor {

    //~ Instance fields --------------------------------------------------------

    // JFormattedTextField ftf;
    JTextField txtDate;
    // NumberFormat integerFormat;
    DateFormat dateFormat = LagisBroker.getDateFormatter();
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Integer minimum;
    private Integer maximum;
    private boolean DEBUG = false;
    private Validator validator;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DateEditor object.
     */
    public DateEditor() {
        super(new JTextField());
        delegate = new EditorDelegate() {

                @Override
                public void setValue(final Object value) {
                    if (log.isDebugEnabled()) {
                        log.debug("setValue");
                    }
                    if (value instanceof Date) {
                        txtDate.setText((value != null) ? dateFormat.format((Date)value) : "");
                    } else {
                        txtDate.setText((value != null) ? value.toString() : "");
                    }
                }

                @Override
                public Object getCellEditorValue() {
                    return txtDate.getText();
                }
            };
        txtDate = (JTextField)getComponent();
        txtDate.setDocument(new DateDocumentModel());
        validator = new Validator(txtDate);
        validator.reSetValidator((Validatable)txtDate.getDocument());

        // Set up the editor for the integer cells.
        // integerFormat = NumberFormat.getIntegerInstance();
        // NumberFormatter intFormatter = new NumberFormatter(integerFormat);
        final DateFormatter dateFormatter = new DateFormatter();
        dateFormatter.setFormat(dateFormat);

//        ftf.setFormatterFactory(
//                new DefaultFormatterFactory(dateFormatter));
        // ftf.setValue(dateFormat.format());
        txtDate.setHorizontalAlignment(SwingConstants.CENTER);
        // txtDate.setFocusLostBehavior(JFormattedTextField.PERSIST);

        // React when the user presses Enter while the editor is
        // active.  (Tab is handled as specified by
        // JFormattedTextField's focusLostBehavior property.)
// ftf.getInputMap().put(KeyStroke.getKeyStroke(
// KeyEvent.VK_ENTER, 0),
// "check");
// ftf.getActionMap().put("check", new AbstractAction() {
// public void actionPerformed(ActionEvent e) {
// if (!ftf.isEditValid()) { //The text is invalid.
// if (userSaysRevert()) { //reverted
// ftf.postActionEvent(); //inform the editor
// }
// } else try {              //The text is valid,
// ftf.commitEdit();     //so use it.
// ftf.postActionEvent(); //stop editing
// } catch (java.text.ParseException exc) { }
// }
// });
    }

    //~ Methods ----------------------------------------------------------------

    // Override to invoke setValue on the formatted text field.
// public Component getTableCellEditorComponent(JTable table,
// Object value, boolean isSelected,
// int row, int column) {
// JTextField txtDate =
// (JTextField)super.getTableCellEditorComponent(
// table, value, isSelected, row, column);
// ftf.setValue(value);
// return ftf;
// }

    @Override
    public Object getCellEditorValue() {
        if (log.isDebugEnabled()) {
            log.debug("getCellEditorValue");
        }
        final JTextField txtDate = (JTextField)getComponent();
        final String t = txtDate.getText();
//        if (o instanceof Date) {
//            return o;
//        } else {
        try {
            if ((validator.getValidationState() == Validatable.VALID) && (t != null) && (t.length() != 0)) {
                return dateFormat.parse(t);
            } else {
                return null;
            }
        } catch (ParseException exc) {
            log.warn("Fehler beim Parsen des Datum: ", exc);
            // System.err.println("getCellEditorValue: can't parse o: " + t);
            // exc.printStackTrace();
            return t;
        }
        // }
    }

    // Override to check whether the edit is valid,
    // setting the value if it is and complaining if
    // it isn't.  If it's OK for the editor to go
    // away, we need to invoke the superclass's version
    // of this method so that everything gets cleaned up.
// public boolean stopCellEditing() {
// JFormattedTextField ftf = (JFormattedTextField)getComponent();
// if (ftf.isEditValid()) {
// try {
// ftf.commitEdit();
// } catch (java.text.ParseException exc) { }
//
// } else { //text is invalid
// if (!userSaysRevert()) { //user wants to edit
// return false; //don't let the editor go away
// }
// }
// return super.stopCellEditing();
// }
    /**
     * Lets the user know that the text they entered is bad. Returns true if the user elects to revert to the last good
     * value. Otherwise, returns false, indicating that the user wants to continue editing.
     *
     * @return  DOCUMENT ME!
     */
// protected boolean userSaysRevert() {
// Toolkit.getDefaultToolkit().beep();
// ftf.selectAll();
// Object[] options = {"Edit",
// "Revert"};
// int answer = JOptionPane.showOptionDialog(
// SwingUtilities.getWindowAncestor(ftf),
// "The value must be an integer between "
// + minimum + " and "
// + maximum + ".\n"
// + "You can either continue editing "
// + "or revert to the last valid value.",
// "Invalid Text Entered",
// JOptionPane.YES_NO_OPTION,
// JOptionPane.ERROR_MESSAGE,
// null,
// options,
// options[1]);
//
// if (answer == 1) { //Revert!
// ftf.setValue(ftf.getValue());
// return true;
// }
// return false;
// }
    public int getValidationState() {
        return validator.getValidationState();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getText() {
        return txtDate.getText();
    }
}
