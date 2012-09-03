/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * EuroEditor.java
 *
 * Created on 27. Mai 2007, 12:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.editor;

import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.util.Locale;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.models.documents.AmountDocumentModel;

import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.Validator;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class EuroEditor extends DefaultCellEditor {

    //~ Instance fields --------------------------------------------------------

    NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.GERMANY);
    DecimalFormat df = LagisBroker.getCurrencyFormatter();

    JTextField txtEuro;
    DecimalFormat euroFormat = LagisBroker.getCurrencyFormatter();

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Integer minimum;
    private Integer maximum;
    private boolean DEBUG = false;
    private Validator validator;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EuroEditor object.
     */
    public EuroEditor() {
        super(new JTextField());
        delegate = new EditorDelegate() {

                @Override
                public void setValue(final Object value) {
                    if (value instanceof Number) {
                        txtEuro.setText((value != null) ? nf.format((Number)value) : "");
                    } else {
                        txtEuro.setText((value != null) ? value.toString() : "");
                    }
                }

                @Override
                public Object getCellEditorValue() {
                    return txtEuro.getText();
                }
            };
        txtEuro = (JTextField)getComponent();
        txtEuro.setDocument(new AmountDocumentModel());
        validator = new Validator(txtEuro);
        validator.reSetValidator((Validatable)txtEuro.getDocument());

        // Set up the editor for the integer cells.
        final NumberFormatter euroFormatter = new NumberFormatter();
        euroFormatter.setFormat(euroFormat);

        txtEuro.setHorizontalAlignment(SwingConstants.CENTER);
    }

    //~ Methods ----------------------------------------------------------------

    // Override to ensure that the value remains an Integer.
    @Override
    public Object getCellEditorValue() {
        final JTextField txtEuro = (JTextField)getComponent();
        final String t = txtEuro.getText();
        validator.validationStateChanged(this);
        try {
            if (validator.getValidationState() == Validatable.VALID) {
                return ((AmountDocumentModel)txtEuro.getDocument()).getCurrentAmount();
            } else {
                return t;
            }
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug("getCellEditorValue: can't parse o: " + t);
            }
            return t;
        }
    }

    /**
     * Lets the user know that the text they entered is bad. Returns true if the user elects to revert to the last good
     * value. Otherwise, returns false, indicating that the user wants to continue editing.
     *
     * @return  DOCUMENT ME!
     */
    public int getValidationState() {
        return validator.getValidationState();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getText() {
        return txtEuro.getText();
    }
}
