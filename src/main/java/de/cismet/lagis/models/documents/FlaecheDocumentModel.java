/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
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
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class FlaecheDocumentModel extends SimpleDocumentModel {

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of FlaecheDocumentModel.
     */
    public FlaecheDocumentModel() {
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
     * @param  flaeche  DOCUMENT ME!
     */
    public void assignValue(final Integer flaeche) {
    }

    @Override
    public int getStatus() {
        if (valueToCheck != null) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("ValueToCheck: " + valueToCheck);
                }
                final Integer flaeche = Integer.parseInt(valueToCheck);
                statusDescription = "";
                assignValue(flaeche);
                return VALID;
            } catch (Exception ex) {
                if (valueToCheck.length() == 0) {
                    statusDescription = "";
                    final Integer nullDouble = 0;
                    assignValue(nullDouble);
                    return VALID;
                }
                log.error("Fehler  parsen: ", ex);
                statusDescription = "Unkorrektes Format. Bitte geben sie eine Fläche nach folgendem Format ein #.##";
                return ERROR;
            }
        } else if (valueToCheck == null) {
            final Integer tmp = 0;
            assignValue(tmp);
            statusDescription = "";
            return VALID;
        }
        return ERROR;
    }
}
