/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * HistoricActionPanel.java
 *
 * Created on 10. September 2007, 10:35
 */
package de.cismet.lagis.wizard.panels;

import org.apache.log4j.Logger;

import org.netbeans.spi.wizard.WizardController;

import java.util.Date;
import java.util.Map;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.ValidationStateChangedListener;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class HistoricActionPanel extends javax.swing.JPanel implements ValidationStateChangedListener {

    //~ Static fields/initializers ---------------------------------------------

    public static final String KEY_HISTORIC_CANDIDATE = "historicCandidate";
    public static final String KEY_HISTORIC_DATE = "historicDate";

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private final WizardController wizardController;
    private final Map wizardData;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private org.jdesktop.swingx.JXDatePicker jxdHistorischDatum;
    private de.cismet.lagis.gui.panels.FlurstueckChooser panHistoric;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form HistoricActionPanel.
     *
     * @param  wizardController  DOCUMENT ME!
     * @param  wizardData        DOCUMENT ME!
     */
    public HistoricActionPanel(final WizardController wizardController, final Map wizardData) {
        if (log.isDebugEnabled()) {
            log.debug("Historic Action Panel wird angelegt");
        }
        initComponents();
        this.wizardController = wizardController;
        this.wizardData = wizardData;
        wizardController.setProblem("Bitte wählen Sie das Flurstück aus das historisch gesetzt werden soll");
        panHistoric.addValidationStateChangedListener(this);
        jxdHistorischDatum.setDate(new Date());
        panHistoric.requestFlurstueck(LagisBroker.getInstance().getCurrentFlurstueckSchluessel());
    }

    //~ Methods ----------------------------------------------------------------

    // TODO HISTORIC CHECK
    @Override
    public void validationStateChanged(final Object validatedObject) {
        if (panHistoric.getStatus() == Validatable.VALID) {
            final CidsBean sperre = LagisBroker.getInstance().isLocked(panHistoric.getCurrentFlurstueckSchluessel());
            if (sperre != null) {
                // TODO nicht ganz sichtbar
                wizardController.setProblem("Ausgewähltes Flurstück ist gesperrt von Benutzer: "
                            + (String)sperre.getProperty("user_string"));
                return;
            } else {
                wizardData.put(KEY_HISTORIC_CANDIDATE, panHistoric.getCurrentFlurstueckSchluessel());

                wizardController.setProblem(null);
                wizardController.setForwardNavigationMode(wizardController.MODE_CAN_FINISH);
            }
        } else {
            wizardController.setProblem(panHistoric.getValidationMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        panHistoric = new de.cismet.lagis.gui.panels.FlurstueckChooser();
        jxdHistorischDatum = new org.jdesktop.swingx.JXDatePicker();
        jLabel2 = new javax.swing.JLabel();

        final javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                381,
                Short.MAX_VALUE));
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                58,
                Short.MAX_VALUE));

        jLabel1.setText("Flurstück");

        jxdHistorischDatum.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 5));
        jxdHistorischDatum.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

                @Override
                public void propertyChange(final java.beans.PropertyChangeEvent evt) {
                    jxdHistorischDatumPropertyChange(evt);
                }
            });

        jLabel2.setText("historisch seit:");

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        jPanel1,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(
                        layout.createSequentialGroup().addContainerGap().addComponent(jLabel1)).addGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addGroup(
                            layout.createSequentialGroup().addContainerGap().addComponent(jLabel2).addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                jxdHistorischDatum,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(
                            javax.swing.GroupLayout.Alignment.LEADING,
                            layout.createSequentialGroup().addContainerGap().addComponent(
                                panHistoric,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                349,
                                javax.swing.GroupLayout.PREFERRED_SIZE)))).addContainerGap(136, Short.MAX_VALUE)));
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addComponent(
                    jPanel1,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel1).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panHistoric,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jxdHistorischDatum,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel2)).addContainerGap(
                    82,
                    Short.MAX_VALUE)));
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxdHistorischDatumPropertyChange(final java.beans.PropertyChangeEvent evt) { //GEN-FIRST:event_jxdHistorischDatumPropertyChange
        wizardData.put(KEY_HISTORIC_DATE, jxdHistorischDatum.getDate());
    }                                                                                         //GEN-LAST:event_jxdHistorischDatumPropertyChange
}
