/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * SplitActionChoosePanel.java
 *
 * Created on 10. September 2007, 11:21
 */
package de.cismet.lagis.wizard.panels;

import org.apache.log4j.Logger;

import org.netbeans.spi.wizard.WizardController;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;

import java.util.Map;

import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.beans.lagis.SperreCustomBean;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.ValidationStateChangedListener;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class SplitActionChoosePanel extends javax.swing.JPanel implements ChangeListener,
    ValidationStateChangedListener {

    //~ Static fields/initializers ---------------------------------------------

    public static final String KEY_SPLIT_CANDIDATE = "splitCandidate";
    public static final String KEY_SPLIT_COUNT = "splitCount";
    public static final String SPLIT_ACTION_MODE = "splitAction";
    public static final String SPLIT_JOIN_ACTION_MODE = "splitJoinAction";

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private WizardController wizardController;
    private Map wizardData;
    private SpinnerNumberModel spinnerModel;
    private String mode;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblSplitResult;
    private de.cismet.lagis.gui.panels.FlurstueckChooser panSplit;
    private javax.swing.JSpinner spnSplitCount;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form SplitActionChoosePanel.
     *
     * @param  wizardController  DOCUMENT ME!
     * @param  wizardData        DOCUMENT ME!
     * @param  mode              DOCUMENT ME!
     */
    public SplitActionChoosePanel(final WizardController wizardController, final Map wizardData, final String mode) {
        initComponents();
        this.mode = mode;
        this.wizardController = wizardController;
        this.wizardData = wizardData;
        spinnerModel = new SpinnerNumberModel(2, 2, 100, 1);
        spnSplitCount.setModel(spinnerModel);
        if (mode.equals(SPLIT_ACTION_MODE)) {
            wizardController.setProblem("Bitte wählen Sie das Flurstück aus, das gesplittet werden soll");
            panSplit.addValidationStateChangedListener(this);
        } else if (mode.equals(SPLIT_JOIN_ACTION_MODE)) {
            validationStateChanged(this);
            // enableChildren(panSplit,false);
            panSplit.setVisible(false);
            jLabel1.setVisible(false);
            remove(panSplit);
            remove(jLabel1);
        }
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    LagisBroker.getInstance().reloadFlurstueckKeys();
                }
            });
        // txtSplitCount.getDocument().addDocumentListener(this);
        spnSplitCount.addChangeListener(this);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  wizardData  DOCUMENT ME!
     */
    public void refresh(final Map wizardData) {
        this.wizardData = wizardData;
        this.validationStateChanged(this);
    }

    @Override
    public void validationStateChanged(final Object validatedObject) {
        int splitCount = 0;

        if (mode.equals(SPLIT_ACTION_MODE)) {
            if (!(panSplit.getStatus() == Validatable.VALID)) {
                wizardController.setProblem(panSplit.getValidationMessage());
                return;
            }
        }
        final String result = String.valueOf(spnSplitCount.getValue());
        try {
            splitCount = Integer.parseInt(result);
            if (splitCount < 2) {
                wizardController.setProblem("Es müssen mindestens zwei neue Flurstücke entstehen");
                return;
            }
        } catch (NumberFormatException ex) {
            if (log.isDebugEnabled()) {
                log.debug("Eingabe ist keine Zahl");
            }
            wizardController.setProblem("Bitte geben Sie die Anzahl der neuen Flurstücke");
            return;
        }
        if (mode.equals(SPLIT_ACTION_MODE)) {
            if (panSplit.getStatus() == Validatable.VALID) {
                final SperreCustomBean sperre = CidsBroker.getInstance()
                            .isLocked(panSplit.getCurrentFlurstueckSchluessel());
                if (sperre != null) {
                    // TODO nicht ganz sichtbar
                    wizardController.setProblem("Ausgewähltes Flurstück ist gesperrt von Benutzer: "
                                + sperre.getBenutzerkonto());
                    return;
                } else {
                    wizardData.put(KEY_SPLIT_CANDIDATE, panSplit.getCurrentFlurstueckSchluessel());
                    wizardData.put(KEY_SPLIT_COUNT, splitCount);
                    wizardController.setProblem(null);
                    wizardController.setForwardNavigationMode(wizardController.MODE_CAN_CONTINUE);
                }
            }
        } else if (mode.equals(SPLIT_JOIN_ACTION_MODE)) {
            wizardData.put(KEY_SPLIT_COUNT, splitCount);
            wizardController.setProblem(null);
            wizardController.setForwardNavigationMode(wizardController.MODE_CAN_CONTINUE);
        }
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        validationStateChanged(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  container  DOCUMENT ME!
     * @param  isEnabled  DOCUMENT ME!
     */
    private void enableChildren(final Container container, final boolean isEnabled) {
        // get an arry of all the components in this container
        final Component[] components = container.getComponents();
        // for each element in the container enable/disable it
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof Container) {
                enableChildren(((Container)components[i]), isEnabled);
            }
            components[i].setEnabled(isEnabled);
        }
    }

//    public void removeUpdate(DocumentEvent e) {
//        validationStateChanged(this);
//    }
//
//    public void insertUpdate(DocumentEvent e) {
//        validationStateChanged(this);
//    }
//
//    public void changedUpdate(DocumentEvent e) {
//        validationStateChanged(this);
//    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel2 = new javax.swing.JPanel();
        lblSplitResult = new javax.swing.JLabel();
        spnSplitCount = new javax.swing.JSpinner();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        panSplit = new de.cismet.lagis.gui.panels.FlurstueckChooser();

        final javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                381,
                Short.MAX_VALUE));
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                58,
                Short.MAX_VALUE));

        lblSplitResult.setText("Anzahl neuer Flurst\u00fccke:");

        jLabel1.setText("Flurst\u00fcck:");

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                jPanel2,
                javax.swing.GroupLayout.PREFERRED_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(lblSplitResult).addGap(234, 234, 234))
                        .addGroup(
                            layout.createSequentialGroup().addContainerGap().addComponent(jLabel1).addContainerGap(
                                324,
                                Short.MAX_VALUE)).addGroup(
                layout.createSequentialGroup().addGap(30, 30, 30).addComponent(
                    spnSplitCount,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    120,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(231, Short.MAX_VALUE)).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(
                    jSeparator1,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    361,
                    Short.MAX_VALUE).addContainerGap()).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(
                    panSplit,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    355,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(16, Short.MAX_VALUE)));
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addComponent(
                    jPanel2,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(lblSplitResult).addGap(
                    14,
                    14,
                    14).addComponent(
                    spnSplitCount,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addGap(14, 14, 14).addComponent(
                    jSeparator1,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    10,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel1).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    panSplit,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE)));
    } // </editor-fold>//GEN-END:initComponents
}
