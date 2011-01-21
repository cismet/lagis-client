/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * ContinuationWizard.java
 *
 * Created on 8. September 2007, 12:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.wizard.steps;

import org.apache.log4j.Logger;

import org.netbeans.spi.wizard.WizardController;
import org.netbeans.spi.wizard.WizardPanelProvider;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JRadioButton;

import de.cismet.lagis.wizard.panels.ChoiceActionPanel;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class InitialStep extends WizardPanelProvider {

    //~ Static fields/initializers ---------------------------------------------

    public static final String KEY_ACTION = "choseAction";
    public static final String VALUE_CREATE = "create";
    public static final String VALUE_RENAME = "rename";
    public static final String VALUE_HISTORIC = "historic";
    public static final String VALUE_ACTIVATE = "activate";
    public static final String VALUE_SPLIT = "split";
    public static final String VALUE_JOIN = "join";
    public static final String VALUE_SPLIT_JOIN = "split/join";
    public static final String VALUE_CHANGE_KIND = "changeKind";
    private static final String STEP_0_PROBLEM = "Bitte wählen Sie eine der obigen Aktionen aus";

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Map wizardData;
    private WizardController wizardController;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of ContinuationWizard.
     */
    public InitialStep() {
        // TODO besserer Titel
        super("Flurstück Assistent", new String[] { KEY_ACTION },
            new String[] { "Aktion wählen" });
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected JComponent createPanel(final WizardController wizardController, final String id, final Map wizardData) {
        switch (indexOfStep(id)) {
            case 0: {
                this.wizardData = wizardData;
                this.wizardController = wizardController;
                wizardController.setProblem(STEP_0_PROBLEM);

                final ChoiceActionPanel result = new ChoiceActionPanel();
                result.cboCreateFlurstueck.putClientProperty(KEY_ACTION, VALUE_CREATE);
                result.cboJoinFlurstueck.putClientProperty(KEY_ACTION, VALUE_JOIN);
                result.cboRenameFlurstueck.putClientProperty(KEY_ACTION, VALUE_RENAME);
                result.cboSetFlurstueckHistoric.putClientProperty(KEY_ACTION, VALUE_HISTORIC);
                result.cboSplitFlurstueck.putClientProperty(KEY_ACTION, VALUE_SPLIT);
                result.cboSplitJoinFlurstueck.putClientProperty(KEY_ACTION, VALUE_SPLIT_JOIN);
                result.cboChangeKind.putClientProperty(KEY_ACTION, VALUE_CHANGE_KIND);
                result.cboSetFlurstueckActive.putClientProperty(KEY_ACTION, VALUE_ACTIVATE);

                final ActionListener listener = new ActionListener() {

                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            actionSelected(e);
                        }
                    };

                result.cboCreateFlurstueck.addActionListener(listener);
                result.cboJoinFlurstueck.addActionListener(listener);
                result.cboRenameFlurstueck.addActionListener(listener);
                result.cboSetFlurstueckHistoric.addActionListener(listener);
                result.cboSplitFlurstueck.addActionListener(listener);
                result.cboSplitJoinFlurstueck.addActionListener(listener);
                result.cboChangeKind.addActionListener(listener);
                result.cboSetFlurstueckActive.addActionListener(listener);
//                result.addActionListener( new ActionListener(  ) {
//                        public void actionPerformed( ActionEvent ae ) {
//                            if ( result.isRadioButtonSelected() ) {
//                                wizardController.setProblem( null );
//                            } else {
//                                wizardController.setProblem( STEP_0_PROBLEM );
//                            }
//                        }
//                    } );
//                wizardController.setProblem( STEP_0_PROBLEM );
//                return result;
                return result;
            }
            case 1:
//                //return new SpeciesPanel ( controller, data );
//
            default: {
                throw new IllegalArgumentException(id);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void actionSelected(final ActionEvent evt) {
        if (log.isDebugEnabled()) {
            log.debug("action command: " + evt.getActionCommand());
        }
        final JRadioButton button = (JRadioButton)evt.getSource();
        if (button.isSelected()) {
            wizardData.put(KEY_ACTION, button.getClientProperty(KEY_ACTION));
            wizardController.setProblem(null);
            wizardController.setForwardNavigationMode(wizardController.MODE_CAN_CONTINUE);
        }
    }
}
