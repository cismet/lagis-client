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

    JTextField txtDate;
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

        final DateFormatter dateFormatter = new DateFormatter();
        dateFormatter.setFormat(dateFormat);

        txtDate.setHorizontalAlignment(SwingConstants.CENTER);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Object getCellEditorValue() {
        if (log.isDebugEnabled()) {
            log.debug("getCellEditorValue");
        }
        final JTextField txtDate = (JTextField)getComponent();
        final String t = txtDate.getText();

        try {
            if ((validator.getValidationState() == Validatable.VALID) && (t != null) && (t.length() != 0)) {
                return dateFormat.parse(t);
            } else {
                return null;
            }
        } catch (ParseException exc) {
            log.warn("Fehler beim Parsen des Datum: ", exc);
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
        return txtDate.getText();
    }
}
