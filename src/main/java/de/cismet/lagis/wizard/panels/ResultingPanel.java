/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * ResultingPanel.java
 *
 * Created on 10. September 2007, 11:24
 */
package de.cismet.lagis.wizard.panels;

import org.apache.log4j.Logger;

import org.netbeans.spi.wizard.WizardController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

import de.cismet.lagis.gui.panels.FlurstueckChooser;

import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.ValidationStateChangedListener;

import de.cismet.lagis.wizard.GeometryAreaChecker;
import de.cismet.lagis.wizard.GeometryWorker;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class ResultingPanel extends javax.swing.JPanel implements ValidationStateChangedListener {

    //~ Static fields/initializers ---------------------------------------------

// public static final String KEY_RESULT = "result";
    public static final String JOIN_ACTION_MODE = "joinAction";
    public static final String SPLIT_ACTION_MODE = "splitAction";
    public static final String SPLIT_JOIN_ACTION_MODE = "splitJoinAction";
    public static final String KEY_SPLIT_KEYS = "splitKeys";
    public static final String KEY_JOIN_KEY = "joinKey";

    private static final Logger log = org.apache.log4j.Logger.getLogger(ResultingPanel.class);

    //~ Instance fields --------------------------------------------------------

    private WizardController wizardController;
    private Map wizardData;
    private String mode;
    private final ArrayList<FlurstueckChooser> resultCandidates = new ArrayList<FlurstueckChooser>();
    private final ArrayList<FlurstueckSchluesselCustomBean> splitKeys = new ArrayList<FlurstueckSchluesselCustomBean>();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblErgebnis;
    private javax.swing.JPanel panAction;
    private javax.swing.JScrollPane spAction;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form ResultingPanel.
     *
     * @param  wizardController  DOCUMENT ME!
     * @param  wizardData        DOCUMENT ME!
     * @param  resultMode        DOCUMENT ME!
     */
    public ResultingPanel(final WizardController wizardController, final Map wizardData, final String resultMode) {
        if (log.isDebugEnabled()) {
            log.debug("Result Panel wird angelegt");
        }
        this.wizardController = wizardController;
        this.wizardData = wizardData;
        initComponents();
        mode = resultMode;
        FlurstueckSchluesselCustomBean splitCandidate = (FlurstueckSchluesselCustomBean)wizardData.get(
                SplitActionChoosePanel.KEY_SPLIT_CANDIDATE);
        if (splitCandidate == null) {
            try {
                final ArrayList<FlurstueckSchluesselCustomBean> joinKeys = (ArrayList<FlurstueckSchluesselCustomBean>)
                    wizardData.get(
                        JoinActionChoosePanel.KEY_JOIN_KEYS);
                splitCandidate = joinKeys.get(joinKeys.size() - 1);
            } catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Keine JoinKeys vorhanden", ex);
                }
                splitCandidate = null;
            }
        }
        if (mode.equals(SPLIT_ACTION_MODE) || mode.equals(SPLIT_JOIN_ACTION_MODE)) {
            if (log.isDebugEnabled()) {
                log.debug("Resultart: Splitaction");
            }
            final int splitCount = (Integer)wizardData.get(SplitActionChoosePanel.KEY_SPLIT_COUNT);
            if (log.isDebugEnabled()) {
                log.debug("Anzahl neu erstellter Flurstücke: " + splitCount);
            }

            for (int i = 0; i < splitCount; i++) {
                final FlurstueckChooser tmpFlst = new FlurstueckChooser(FlurstueckChooser.CREATION_MODE);
                tmpFlst.addValidationStateChangedListener(this);
                if (splitCandidate != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Vorauswahl für Chooser wird gesetzt");
                        log.debug("zu setzender Key: " + splitCandidate);
                    }
                    tmpFlst.doAutomaticRequest(
                        FlurstueckChooser.AutomaticFlurstueckRetriever.COPY_CONTENT_MODE,
                        splitCandidate);
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Vorauswahl für Chooser wird nicht gesetzt");
                    }
                }
                resultCandidates.add(tmpFlst);
                panAction.add(tmpFlst);
            }
            spAction.repaint();
            spAction.getViewport().repaint();
            spAction.revalidate();
        } else if (mode.equals(JOIN_ACTION_MODE)) {
            if (log.isDebugEnabled()) {
                log.debug("Resultart: JoinAction");
            }
            final FlurstueckChooser tmpFlst = new FlurstueckChooser(FlurstueckChooser.CREATION_MODE);
            tmpFlst.addValidationStateChangedListener(this);
            if (splitCandidate != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Vorauswahl für Chooser wird gesetzt");
                    log.debug("zu setzender Key: " + splitCandidate);
                }
                tmpFlst.doAutomaticRequest(
                    FlurstueckChooser.AutomaticFlurstueckRetriever.COPY_CONTENT_MODE,
                    splitCandidate);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Vorauswahl für Chooser wird nicht gesetzt");
                }
            }
            resultCandidates.add(tmpFlst);
            panAction.add(tmpFlst);
            spAction.repaint();
            spAction.getViewport().repaint();
            spAction.revalidate();
        }
        wizardController.setProblem("Bitte vervollständigen Sie alle Flurstücke");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public WizardController getWizardController() {
        return this.wizardController;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  wizardData  DOCUMENT ME!
     */
    public synchronized void refresh(final Map wizardData) {
        this.wizardData = wizardData;

        if (mode.equals(SPLIT_ACTION_MODE) || mode.equals(SPLIT_JOIN_ACTION_MODE)) {
            if (log.isDebugEnabled()) {
                log.debug("Resultart: refresh Count");
            }

            final Integer splitCount = (Integer)wizardData.get(SplitActionChoosePanel.KEY_SPLIT_COUNT);

            if ((splitCount != null) && (resultCandidates != null) && (splitCount != resultCandidates.size())) {
                if (log.isDebugEnabled()) {
                    log.debug("Anzahl neu erstellter Flurstücke: " + splitCount);
                }

                panAction.removeAll();
                resultCandidates.clear();
                splitKeys.clear();
                for (int i = 0; i < splitCount; i++) {
                    final FlurstueckChooser tmpFlst = new FlurstueckChooser(FlurstueckChooser.CREATION_MODE);
                    tmpFlst.addValidationStateChangedListener(this);
                    resultCandidates.add(tmpFlst);
                    panAction.add(tmpFlst);
                }
                spAction.repaint();
                spAction.getViewport().repaint();
                spAction.revalidate();
            }
        }

        this.validationStateChanged(this);
    }

    @Override
    public void validationStateChanged(final Object validatedObject) {
        // TODO SCHLECHTER NAME WEIL ES AUCH EIN JOIN SEIN KANN
        splitKeys.clear();

        final Iterator<FlurstueckChooser> splitMembers = resultCandidates.iterator();
        while (splitMembers.hasNext()) {
            final FlurstueckChooser curSplitMember = splitMembers.next();

            if (curSplitMember.getStatus() == Validatable.ERROR) {
                if (log.isDebugEnabled()) {
                    log.debug("Mindestens ein Flurstück ,dass aus dem Split entsteht, ist nicht valide");
                }
                wizardController.setProblem(curSplitMember.getValidationMessage());
                return;
            }

            splitKeys.add(curSplitMember.getCurrentFlurstueckSchluessel());
        }

        if (checkForDuplicatedFlurstuecke(resultCandidates)) {
            wizardController.setProblem("Es darf kein Flurstück doppelt ausgewählt werden.");
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("Alle Flurstücke für Result sind valide");
        }

        if (JOIN_ACTION_MODE.equals(this.mode)) {
            this.splitKeys.addAll((ArrayList<FlurstueckSchluesselCustomBean>)wizardData.get(
                    JoinActionChoosePanel.KEY_JOIN_KEYS));

            if (this.checkGeometryAreas(this.splitKeys.get(0), this.splitKeys)) {
                this.wizardData.put(KEY_JOIN_KEY, this.splitKeys.get(0));
                this.wizardController.setProblem(null);
                this.wizardController.setForwardNavigationMode(WizardController.MODE_CAN_CONTINUE);
            }
        } else if (SPLIT_ACTION_MODE.equals(this.mode)) {
            final Object splitCandObj = this.wizardData.get(SplitActionChoosePanel.KEY_SPLIT_CANDIDATE);

            if (splitCandObj instanceof FlurstueckSchluesselCustomBean) {
                final FlurstueckSchluesselCustomBean targetKey = (FlurstueckSchluesselCustomBean)splitCandObj;

                final ArrayList<FlurstueckSchluesselCustomBean> fsKeys = new ArrayList<FlurstueckSchluesselCustomBean>(
                        this.splitKeys);
                fsKeys.add(targetKey);

                if (this.checkGeometryAreas(targetKey, fsKeys)) {
                    this.wizardData.put(KEY_SPLIT_KEYS, this.splitKeys);
                    this.wizardController.setProblem(null);
                    this.wizardController.setForwardNavigationMode(WizardController.MODE_CAN_CONTINUE);
                }
            } else {
                log.error("Split candidate has not type FlurstueckSchluessel: " + splitCandObj);
                this.wizardController.setProblem(
                    "Ein interner Fehler ist aufgetreten. Bitte benachrichtigen Sie den Support.");
            }
        } else if (SPLIT_JOIN_ACTION_MODE.equals(this.mode)) {
            final Object joinData = this.wizardData.get(JoinActionChoosePanel.KEY_JOIN_KEYS);
            if (!(joinData instanceof List)) {
                log.error("Join data does not have type 'List': " + joinData);
                this.wizardController.setProblem(
                    "Ein interner Fehler ist aufgetreten. Bitte benachrichtigen Sie den Support.");
                return;
            }
            final List<FlurstueckSchluesselCustomBean> joinKeys = (List)joinData;

            final ArrayList<FlurstueckSchluesselCustomBean> allKeys = new ArrayList<FlurstueckSchluesselCustomBean>();
            allKeys.addAll(splitKeys);
            allKeys.addAll(joinKeys);

            if (this.checkGeometryAreas(joinKeys, allKeys)) {
                this.wizardData.put(JoinActionChoosePanel.KEY_JOIN_KEYS, joinKeys);
                this.wizardData.put(KEY_SPLIT_KEYS, splitKeys);
                this.wizardController.setProblem(null);
                this.wizardController.setForwardNavigationMode(WizardController.MODE_CAN_CONTINUE);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   targetKeys  DOCUMENT ME!
     * @param   fsKeys      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean checkGeometryAreas(final List<FlurstueckSchluesselCustomBean> targetKeys,
            final List<FlurstueckSchluesselCustomBean> fsKeys) {
        final GeometryWorker gw = new GeometryWorker(fsKeys);
        final GeometryAreaChecker checker = new GeometryAreaChecker(targetKeys, this, this.wizardController);
        gw.addPreExecutionListener(checker);
        gw.addPostExecutionListener(checker);
        gw.call();

        this.wizardData.put(SummaryPanel.GEOM_AREA_CHECKER, checker);

        return !checker.hasProblem();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   targetKey  DOCUMENT ME!
     * @param   fsKeys     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean checkGeometryAreas(final FlurstueckSchluesselCustomBean targetKey,
            final List<FlurstueckSchluesselCustomBean> fsKeys) {
        final ArrayList<FlurstueckSchluesselCustomBean> targetKeys = new ArrayList<FlurstueckSchluesselCustomBean>(1);
        targetKeys.add(targetKey);
        return this.checkGeometryAreas(targetKeys, fsKeys);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstuecke  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean checkForDuplicatedFlurstuecke(final ArrayList<FlurstueckChooser> flurstuecke) {
        try {
            if ((flurstuecke != null) && (flurstuecke.size() > 1)) {
                if (log.isDebugEnabled()) {
                    log.debug("Anzahl Flurstücke die zu prüfen sind > 1");
                }
                int counter = 0;
                for (final FlurstueckChooser flurstueckToTest : flurstuecke) {
                    // flurstuecke.remove(flurstueckToTest);
                    counter = 0;
                    for (final FlurstueckChooser curFlurstueck : flurstuecke) {
                        final FlurstueckSchluesselCustomBean schluesselToTest =
                            flurstueckToTest.getCurrentFlurstueckSchluessel();
                        final FlurstueckSchluesselCustomBean curSchluessel =
                            curFlurstueck.getCurrentFlurstueckSchluessel();
                        if ((schluesselToTest != null) && (curSchluessel != null)) {
                            if (log.isDebugEnabled()) {
                                log.debug("Zu testende Schluessel sind != null");
                            }
                            if (FlurstueckSchluesselCustomBean.FLURSTUECK_EQUALATOR.pedanticEquals(
                                            schluesselToTest,
                                            curSchluessel)) {
                                counter++;
                                if (counter > 1) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Duplikat gefunden");
                                    }
                                    return true;
                                }
                            }
                            if (log.isDebugEnabled()) {
                                log.debug("Schlüssel sind nicht gleich");
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("Einer der beiden zu vergleichenden Schlüssel ist == null");
                            }
                        }
                    }
                    // flurstuecke.add(flurstueckToTest);
                }
                return false;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Flurstücke null oder Anzahl <2");
                }
                return false;
            }
        } catch (Exception ex) {
            log.error("Fehler beim Überprüfen von doppelten Einträgen: ", ex);
            return false;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblErgebnis = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        spAction = new javax.swing.JScrollPane();
        panAction = new javax.swing.JPanel();

        lblErgebnis.setText("Ergebnis Flurst\u00fccke");

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

        panAction.setLayout(new java.awt.GridLayout(0, 1));

        spAction.setViewportView(panAction);

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                jPanel2,
                javax.swing.GroupLayout.PREFERRED_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(lblErgebnis)).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(
                    spAction,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    353,
                    javax.swing.GroupLayout.PREFERRED_SIZE)));
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addComponent(
                    jPanel2,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(lblErgebnis).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    spAction,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    136,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(80, Short.MAX_VALUE)));
    } // </editor-fold>//GEN-END:initComponents
}
