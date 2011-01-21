/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * DateDocumentModel.java
 *
 * Created on 27. April 2007, 11:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models.documents;

import org.apache.log4j.Logger;

import java.text.DateFormat;

import java.util.Date;

import de.cismet.lagis.broker.LagisBroker;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class DateDocumentModel extends SimpleDocumentModel {

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private DateFormat dateFormatter = LagisBroker.getDateFormatter();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of DateDocumentModel.
     */
    public DateDocumentModel() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void assignValue(final String newValue) {
        if (log.isDebugEnabled()) {
            log.debug("new Value: " + newValue);
        }
        valueToCheck = newValue;
        fireValidationStateChanged(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  date  DOCUMENT ME!
     */
    public void assignValue(final Date date) {
    }

    @Override
    public int getStatus() {
        if ((valueToCheck != null) && (valueToCheck != "") && (valueToCheck.length() != 0)) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("ValueToCheck: " + valueToCheck + " StringLänge: " + valueToCheck.length());
                }
                final Date date = dateFormatter.parse(valueToCheck);
                statusDescription = "";
                assignValue(date);
                return VALID;
            } catch (Exception ex) {
                if (valueToCheck.length() == 0) {
                    statusDescription = "";
                    final Date nullDate = null;
                    assignValue(nullDate);
                    return VALID;
                }
                log.error("Fehler date parsen: ", ex);
                statusDescription = "Unkorrektes Format. Bitte geben sie ein Datum nach folgendem Format ein TT.MM.JJ";
                return ERROR;
            }
        } else if ((valueToCheck == null) || (valueToCheck == "") || (valueToCheck.length() == 0)) {
            final Date tmp = null;
            assignValue(tmp);
            statusDescription = "";
            return VALID;
        }
        return ERROR;
    }
}
