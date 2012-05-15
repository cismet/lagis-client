/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * AmountDocumentModel.java
 *
 * Created on 27. April 2007, 09:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models.documents;

import org.apache.log4j.Logger;

import java.text.NumberFormat;

import java.util.Locale;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class AmountDocumentModel extends SimpleDocumentModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final String EURO = "\u20AC";

    //~ Instance fields --------------------------------------------------------

    NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.GERMANY);
    Double currentAmount;
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of AmountDocumentModel.
     */
    public AmountDocumentModel() {
    }

    //~ Methods ----------------------------------------------------------------

    // TODO in SimpleDocumentModelimplementieren
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
     * @param  amount  DOCUMENT ME!
     */
    public void assignValue(final Double amount) {
        currentAmount = amount;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Double getCurrentAmount() {
        return currentAmount;
    }

    @Override
    public int getStatus() {
        if (valueToCheck != null) {
            // valueToCheck.matches("^[1-9][0-9]{2}?(\\.[0-9]{3})*+,.*"
            if (valueToCheck.matches(".*(\\.\\.\\.*).*") || valueToCheck.matches(".*(\\...?\\.).*")
                        || valueToCheck.matches(".*(\\.,).*") || valueToCheck.matches("^\\.")) {
                statusDescription = "Es dürfen nicht mehrere Punke unmittelbar aufeinander folgen!";
                return ERROR;
            }

            try {
                final Number betrag = nf.parse(valueToCheck);
                statusDescription = "";
                assignValue(betrag.doubleValue());
                return VALID;
            } catch (Exception ex1) {
                log.warn("Fehler Betrag parsen: Betrag hat nicht die Form ##0,00 € ", ex1);
                try {
                    final Number betrag = nf.parse(valueToCheck.trim() + " " + EURO);
                    statusDescription = "";
                    assignValue(betrag.doubleValue());
                    return VALID;
                } catch (Exception ex2) {
                    log.warn("Fehler Betrag parsen: Betrag hat nicht die Form ##0,00", ex2);
                }
//
                if (valueToCheck.length() == 0) {
                    statusDescription = "";
                    final Double nullDouble = null;
                    assignValue(nullDouble);
                    log.warn("Betrag ist null");
                    return VALID;
                }
                // TODO GOOD Sentence
                statusDescription = "Falsches Format";
                return ERROR;
            }
        } else if (valueToCheck == null) {
            statusDescription = "";
            return VALID;
        } else {
            statusDescription = "Falsches Format: Bitte einen Betrag in der Form 1.222,00 € eingeben";
            return ERROR;
        }
    }
}
