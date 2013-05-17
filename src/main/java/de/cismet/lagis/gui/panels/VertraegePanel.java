/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * RechteBelastungenPanel.java
 *
 * Created on 30. März 2007, 14:03
 */
package de.cismet.lagis.gui.panels;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.SortOrder;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;

import de.cismet.cids.custom.beans.lagis.*;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.editor.DateEditor;
import de.cismet.lagis.editor.EuroEditor;

import de.cismet.lagis.gui.tables.BeschluesseTable;

import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.FlurstueckSaver;

import de.cismet.lagis.models.DefaultUniqueListModel;
import de.cismet.lagis.models.KostenTableModel;
import de.cismet.lagis.models.VertraegeTableModel;
import de.cismet.lagis.models.documents.VertragDocumentModelContainer;

import de.cismet.lagis.renderer.DateRenderer;
import de.cismet.lagis.renderer.EuroRenderer;
import de.cismet.lagis.renderer.FlurstueckSchluesselRenderer;

import de.cismet.lagis.thread.BackgroundUpdateThread;

import de.cismet.lagis.util.LagISUtils;

import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.Validator;

import de.cismet.lagis.widget.AbstractWidget;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class VertraegePanel extends AbstractWidget implements FlurstueckChangeListener,
    FlurstueckSaver,
    ListSelectionListener,
    MouseListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final String WIDGET_NAME = "Verträge Panel";

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private FlurstueckCustomBean currentFlurstueck = null;
    private VertraegeTableModel vTableModel = new VertraegeTableModel();
    // private BeschluesseTableModel bTableModel = new BeschluesseTableModel();
    private KostenTableModel kTableModel = new KostenTableModel();
    private VertragCustomBean currentSelectedVertrag;
    private VertragDocumentModelContainer documentContainer;
    private Validator valTxtVoreigentuemer;
    private Validator valTxtAuflassung;
    private Validator valTxtKaufpreis;
    private Validator valTxtQuadPreis;
    private Validator valTxtAktenzeichen;
    private Validator valTxtBemerkung;
    private Validator valTxtEintragung;
    private Vector<Validator> validators = new Vector<Validator>();
    private BackgroundUpdateThread<FlurstueckCustomBean> updateThread;
    private ImageIcon icoExistingContract = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/contract.png"));
    private boolean isInEditMode = false;
    private boolean isFlurstueckEditable = true;

// private DefaultComboBoxModel vertragsartComboBoxModel;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddBeschluss;
    private javax.swing.JButton btnAddExitingContract;
    private javax.swing.JButton btnAddKosten;
    private javax.swing.JButton btnAddVertrag;
    private javax.swing.JButton btnRemoveBeschluss;
    private javax.swing.JButton btnRemoveKosten;
    private javax.swing.JButton btnRemoveVertrag;
    private javax.swing.JComboBox cboVertragsart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JLabel lblAktenzeichen;
    private javax.swing.JLabel lblAuflassung;
    private javax.swing.JLabel lblBemerkung;
    private javax.swing.JLabel lblEintragung;
    private javax.swing.JLabel lblKaufpreis;
    private javax.swing.JLabel lblQuadPreis;
    private javax.swing.JLabel lblVertragsart;
    private javax.swing.JLabel lblVoreigentuemer;
    private javax.swing.JList lstCrossRefs;
    private javax.swing.JPanel panBemerkung;
    private javax.swing.JPanel panBeschluss;
    private javax.swing.JPanel panData;
    private javax.swing.JPanel panKosten;
    private javax.swing.JPanel panQuerverweise;
    private javax.swing.JPanel panTab;
    private javax.swing.JPanel panVertraege;
    private javax.swing.JPanel pnlBeschluesseControls;
    private javax.swing.JPanel pnlDetail;
    private javax.swing.JPanel pnlKostenControls;
    private javax.swing.JPanel pnlKostenControls1;
    private javax.swing.JTabbedPane tabKB;
    private javax.swing.JTable tblBeschluesse;
    private javax.swing.JTable tblKosten;
    private javax.swing.JTable tblVertraege;
    private javax.swing.JToggleButton tbtnSort;
    private javax.swing.JTextField txtAktenzeichen;
    private javax.swing.JTextField txtAuflassung;
    private javax.swing.JTextArea txtBemerkung;
    private javax.swing.JTextField txtEintragung;
    private javax.swing.JTextField txtKaufpreis;
    private javax.swing.JTextField txtQuadPreis;
    private javax.swing.JTextField txtVoreigentuemer;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RechteBelastungenPanel.
     */
    public VertraegePanel() {
        setIsCoreWidget(true);
        initComponents();
        // tblBeschluesse.setModel(bTableModel);
        tblKosten.setModel(kTableModel);
        tblVertraege.setModel(vTableModel);
        // tblVertraege.addMouseListener(this);
        documentContainer = new VertragDocumentModelContainer(vTableModel);
        tblVertraege.addMouseListener(documentContainer);
        // log.debug("AmountDocumentModel"+((VertraegeTableModel)tblVertraege.getModel()).getKaufpreisModel());

        txtKaufpreis.setDocument(documentContainer.getKaufpreisDocumentModel());
        valTxtKaufpreis = new Validator(txtKaufpreis);
        valTxtKaufpreis.reSetValidator((Validatable)documentContainer.getKaufpreisDocumentModel());

        txtAuflassung.setDocument(documentContainer.getAuflassungDocumentModel());
        valTxtAuflassung = new Validator(txtAuflassung);
        valTxtAuflassung.reSetValidator((Validatable)documentContainer.getAuflassungDocumentModel());

        txtVoreigentuemer.setDocument(documentContainer.getVoreigentuemerDocumentModel());
        valTxtVoreigentuemer = new Validator(txtVoreigentuemer);
        valTxtVoreigentuemer.reSetValidator((Validatable)documentContainer.getVoreigentuemerDocumentModel());

        txtQuadPreis.setDocument(documentContainer.getQuadPreisDocumentModel());
        valTxtQuadPreis = new Validator(txtQuadPreis);
        valTxtQuadPreis.reSetValidator((Validatable)documentContainer.getQuadPreisDocumentModel());

        txtAktenzeichen.setDocument(documentContainer.getAktenzeichenDocumentModel());
        valTxtAktenzeichen = new Validator(txtAktenzeichen);
        valTxtAktenzeichen.reSetValidator((Validatable)documentContainer.getAktenzeichenDocumentModel());

        txtBemerkung.setDocument(documentContainer.getBemerkungDocumentModel());
        valTxtBemerkung = new Validator(txtBemerkung);
        valTxtBemerkung.reSetValidator((Validatable)documentContainer.getBemerkungDocumentModel());

        txtEintragung.setDocument(documentContainer.getEintragungDocumentModel());
        valTxtEintragung = new Validator(txtEintragung);
        valTxtEintragung.reSetValidator((Validatable)documentContainer.getEintragungDocumentModel());

        cboVertragsart.setModel(documentContainer.getVertragsartComboBoxModel());
        cboVertragsart.addActionListener(documentContainer);

        tblKosten.setModel(documentContainer.getKostenTableModel());
        ((BeschluesseTable)tblBeschluesse).setDocumentContainer(documentContainer);
        tblBeschluesse.setModel(documentContainer.getBeschluesseTableModel());
        // Set vertragsarten = null;
// if(vertragsarten != null){
// vertragsartComboBoxModel = new DefaultComboBoxModel(new Vector(vertragsarten));
// } else {
// vertragsartComboBoxModel = new DefaultComboBoxModel();
// }
// cboVertragsart.setModel(vertragsartComboBoxModel);
        validators.add(valTxtAktenzeichen);
        validators.add(valTxtAuflassung);
        validators.add(valTxtBemerkung);
        validators.add(valTxtEintragung);
        validators.add(valTxtKaufpreis);
        validators.add(valTxtQuadPreis);
        validators.add(valTxtVoreigentuemer);

        final JComboBox cboBA = new JComboBox(new Vector<BeschlussartCustomBean>(
                    CidsBroker.getInstance().getAllBeschlussarten()));
        final JComboBox cboKA = new JComboBox(new Vector<KostenartCustomBean>(
                    CidsBroker.getInstance().getAllKostenarten()));
        tblBeschluesse.setDefaultEditor(BeschlussartCustomBean.class, new DefaultCellEditor(cboBA));
        tblKosten.setDefaultEditor(KostenartCustomBean.class, new DefaultCellEditor(cboKA));
        // tblBeschluesse.addMouseListener(this);
        // tblKosten.addMouseListener(this);
        tblKosten.setDefaultEditor(Double.class, new EuroEditor());
        tblKosten.setDefaultRenderer(Double.class, new EuroRenderer());
        tblKosten.getSelectionModel().addListSelectionListener(this);
        tblVertraege.getSelectionModel().addListSelectionListener(this);
        tblBeschluesse.getSelectionModel().addListSelectionListener(this);

        tblKosten.setDefaultEditor(Date.class, new DateEditor());
        tblKosten.setDefaultRenderer(Date.class, new DateRenderer());

        tblBeschluesse.setDefaultEditor(Date.class, new DateEditor());
        tblBeschluesse.setDefaultRenderer(Date.class, new DateRenderer());

        // HighlighterPipeline hPipline = new HighlighterPipeline(new
        // Highlighter[]{LagisBroker.ALTERNATE_ROW_HIGHLIGHTER});
        ((JXTable)tblVertraege).setHighlighters(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER);
        ((JXTable)tblBeschluesse).setHighlighters(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER);
        ((JXTable)tblKosten).setHighlighters(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER);
        ((JXTable)tblVertraege).setSortOrder(0, SortOrder.ASCENDING);
        ((JXTable)tblBeschluesse).setSortOrder(1, SortOrder.ASCENDING);
        ((JXTable)tblKosten).setSortOrder(2, SortOrder.ASCENDING);
        enableSlaveFlieds(false);
        btnRemoveBeschluss.setEnabled(false);
        btnRemoveKosten.setEnabled(false);
        tblVertraege.addMouseListener(this);
        lstCrossRefs.setCellRenderer(new FlurstueckSchluesselRenderer());
        lstCrossRefs.setModel(new DefaultUniqueListModel());
        lstCrossRefs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstCrossRefs.addMouseListener(this);
        ((JXTable)tblBeschluesse).packAll();
        ((JXTable)tblKosten).packAll();
        ((JXTable)tblVertraege).packAll();
        configBackgroundThread();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void configBackgroundThread() {
        updateThread = new BackgroundUpdateThread<FlurstueckCustomBean>() {

                @Override
                protected void update() {
                    try {
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        clearComponent();
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        final FlurstueckArtCustomBean flurstueckArt = getCurrentObject().getFlurstueckSchluessel()
                                    .getFlurstueckArt();
                        if ((flurstueckArt != null)
                                    && flurstueckArt.getBezeichnung().equals(
                                        FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                            if (log.isDebugEnabled()) {
                                log.debug("Flurstück ist städtisch und kann editiert werden");
                            }
                            isFlurstueckEditable = true;
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("Flurstück ist nicht städtisch und kann nicht editiert werden");
                            }
                            isFlurstueckEditable = false;
                        }
                        vTableModel.refreshTableModel(getCurrentObject().getVertraege());
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        documentContainer.updateTableModel(vTableModel);
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        final Collection<FlurstueckSchluesselCustomBean> crossRefs = getCurrentObject()
                                    .getVertraegeQuerverweise();
                        if ((crossRefs != null) && (crossRefs.size() > 0)) {
                            lstCrossRefs.setModel(new DefaultUniqueListModel(crossRefs));
                            tabKB.setForegroundAt(0, Color.RED);
                        } else {
                            tabKB.setForegroundAt(0, null);
                        }
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        LagisBroker.getInstance().flurstueckChangeFinished(VertraegePanel.this);
                    } catch (Exception ex) {
                        log.error("Fehler im refresh thread: ", ex);
                        LagisBroker.getInstance().flurstueckChangeFinished(VertraegePanel.this);
                    }
                }

                @Override
                protected void cleanup() {
                }
            };
        updateThread.setPriority(Thread.NORM_PRIORITY);
        updateThread.start();
    }
    // private Thread panelRefresherThread;
    @Override
    public void flurstueckChanged(final FlurstueckCustomBean newFlurstueck) {
        try {
            log.info("FlurstueckChanged");
            currentFlurstueck = newFlurstueck;
            updateThread.notifyThread(currentFlurstueck);
        } catch (Exception ex) {
            log.error("Fehler beim Flurstückswechsel: ", ex);
            LagisBroker.getInstance().flurstueckChangeFinished(VertraegePanel.this);
        }
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
        if (isFlurstueckEditable) {
            if (log.isDebugEnabled()) {
                log.debug("Vertrag --> setComponentEditable");
            }
            isInEditMode = isEditable;
            lstCrossRefs.setEnabled(!isEditable);
            if (isEditable && (tblVertraege.getSelectedRow() != -1)) {
                btnRemoveVertrag.setEnabled(true);
            } else if (!isEditable) {
                btnRemoveVertrag.setEnabled(false);
            }

            if (tblVertraege.getSelectedRow() != -1) {
                enableSlaveFlieds(isEditable);
            } else if (!isEditable) {
                enableSlaveFlieds(isEditable);
                btnRemoveBeschluss.setEnabled(false);
                btnRemoveKosten.setEnabled(false);
            }
            final TableCellEditor currentKostenEditor = tblKosten.getCellEditor();
            if (currentKostenEditor != null) {
                currentKostenEditor.cancelCellEditing();
            }
            final TableCellEditor currentBeschlussEditor = tblVertraege.getCellEditor();
            if (currentBeschlussEditor != null) {
                currentBeschlussEditor.cancelCellEditing();
            }
            btnAddExitingContract.setEnabled(isEditable);
            btnAddVertrag.setEnabled(isEditable);
            tbtnSort.setEnabled(isEditable);
            documentContainer.getBeschluesseTableModel().setIsInEditMode(isEditable);
            documentContainer.getKostenTableModel().setIsInEditMode(isEditable);
            tblKosten.setEnabled(isEditable);
            tblBeschluesse.setEnabled(isEditable);
            if (log.isDebugEnabled()) {
//        HighlighterPipeline pipeline1 = ((JXTable)tblVertraege).getHighlighters();
//        HighlighterPipeline pipeline2 = ((JXTable)tblKosten).getHighlighters();
//        HighlighterPipeline pipeline3 = ((JXTable)tblBeschluesse).getHighlighters();
//        if(isEditable){
//            pipeline1.removeHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_DEFAULT);
//            pipeline1.addHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_EDIT,false);
//            pipeline2.removeHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_DEFAULT);
//            pipeline2.addHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_EDIT,false);
//            pipeline3.removeHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_DEFAULT);
//            pipeline3.addHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_EDIT,false);
//        } else {
//            pipeline1.removeHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_EDIT);
//            pipeline1.addHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_DEFAULT,false);
//            pipeline2.removeHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_EDIT);
//            pipeline2.addHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_DEFAULT,false);
//            pipeline3.removeHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_EDIT);
//            pipeline3.addHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_DEFAULT,false);
//        }
                log.debug("Vertrag --> setComponentEditable finished");
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Flurstück ist nicht städtisch Verwaltungen können nicht editiert werden");
            }
        }
    }

    @Override
    public synchronized void clearComponent() {
//tblBeschluesse.setModel(new BeschluesseTableModel());
        // tblKosten.setModel(new KostenTableModel());
        // tblVertraege.setModel(new VertraegeTableModel());
        // vTableModel = new VertraegeTableModel();
        vTableModel.refreshTableModel(null);
        documentContainer.clearComponents();
        // ocumentContainer.updateTableModel(vTableModel);
        // tblVertraege.setModel(vTableModel);
        cboVertragsart.setSelectedItem(null);
        lstCrossRefs.setModel(new DefaultUniqueListModel());
        // txtKaufpreis.setDocument(kaufpreisModel);
        // txtAktenzeichen.setText("");
        // txtAuflassung.setText("");
        // txtBemerkung.setText("");
        // txtEintragung.setText("");
        // txtKaufpreis.setText("");
        // txtQuadPreis.setText("");
        // txtVertragsart.setText("");
    }

    @Override
    public int getStatus() {
        if ((tblBeschluesse.getCellEditor() != null) || (tblKosten.getCellEditor() != null)) {
            validationMessage = "Bitte vollenden Sie alle Änderungen bei den Kosten/Beschlüssen.";
            return Validatable.ERROR;
        }
        final Iterator<Validator> it = validators.iterator();
        while (it.hasNext()) {
            final Validator current = it.next();
            if (current.getValidationState() != Validatable.VALID) {
                validationMessage = current.getValidationMessage();
                return Validatable.ERROR;
            }
        }
        final Vector<VertragCustomBean> alleVertraege = vTableModel.getVertraege();
        if (alleVertraege != null) {
            for (final VertragCustomBean currentVertrag : alleVertraege) {
                if ((currentVertrag != null) && (currentVertrag.getVertragsart() == null)) {
                    validationMessage = "Bei allen Verträgen muss eine Vertragsart ausgewählt werden";
                    return Validatable.ERROR;
                }

                if ((currentVertrag != null) && (currentVertrag.getBeschluesse() != null)) {
                    for (final BeschlussCustomBean currentBeschluss : currentVertrag.getBeschluesse()) {
                        if (currentBeschluss.getBeschlussart() == null) {
                            validationMessage = "Bei allen Beschlüssen muss eine Beschlussart ausgewählt werden";
                            return Validatable.ERROR;
                        }
                    }
                }

                if ((currentVertrag != null) && (currentVertrag.getKosten() != null)) {
                    for (final KostenCustomBean currentKosten : currentVertrag.getKosten()) {
                        if (currentKosten.getKostenart() == null) {
                            validationMessage = "Bei allen Kosten muss eine Kostenart ausgewählt werden";
                            return Validatable.ERROR;
                        }
                    }
                }
            }
        }
        return Validatable.VALID;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  validatedObject  DOCUMENT ME!
     */
    public void validationStateChanged(final Object validatedObject) {
        fireValidationStateChanged(validatedObject);
    }

    @Override
    public void refresh(final Object refreshObject) {
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlDetail = new javax.swing.JPanel();
        panVertraege = new javax.swing.JPanel();
        pnlKostenControls1 = new javax.swing.JPanel();
        btnAddVertrag = new javax.swing.JButton();
        btnRemoveVertrag = new javax.swing.JButton();
        btnAddExitingContract = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVertraege = new JXTable();
        panBemerkung = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtBemerkung = new javax.swing.JTextArea();
        lblBemerkung = new javax.swing.JLabel();
        panData = new javax.swing.JPanel();
        lblEintragung = new javax.swing.JLabel();
        txtEintragung = new javax.swing.JTextField();
        lblAktenzeichen = new javax.swing.JLabel();
        txtAktenzeichen = new javax.swing.JTextField();
        lblVertragsart = new javax.swing.JLabel();
        cboVertragsart = new javax.swing.JComboBox();
        lblQuadPreis = new javax.swing.JLabel();
        txtQuadPreis = new javax.swing.JTextField();
        lblKaufpreis = new javax.swing.JLabel();
        txtKaufpreis = new javax.swing.JTextField();
        lblAuflassung = new javax.swing.JLabel();
        txtAuflassung = new javax.swing.JTextField();
        txtVoreigentuemer = new javax.swing.JTextField();
        lblVoreigentuemer = new javax.swing.JLabel();
        panTab = new javax.swing.JPanel();
        tabKB = new javax.swing.JTabbedPane();
        panQuerverweise = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstCrossRefs = new javax.swing.JList();
        panKosten = new javax.swing.JPanel();
        pnlKostenControls = new javax.swing.JPanel();
        btnAddKosten = new javax.swing.JButton();
        btnRemoveKosten = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblKosten = new JXTable();
        panBeschluss = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblBeschluesse = new BeschluesseTable();
        pnlBeschluesseControls = new javax.swing.JPanel();
        btnAddBeschluss = new javax.swing.JButton();
        btnRemoveBeschluss = new javax.swing.JButton();
        tbtnSort = ((BeschluesseTable)tblBeschluesse).getTbtnSort();

        setLayout(new java.awt.BorderLayout());

        panVertraege.setMinimumSize(new java.awt.Dimension(300, 0));

        btnAddVertrag.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/add.png"))); // NOI18N
        btnAddVertrag.setBorder(null);
        btnAddVertrag.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddVertragActionPerformed(evt);
                }
            });

        btnRemoveVertrag.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/remove.png"))); // NOI18N
        btnRemoveVertrag.setBorder(null);
        btnRemoveVertrag.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveVertragActionPerformed(evt);
                }
            });

        btnAddExitingContract.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/contract.png"))); // NOI18N
        btnAddExitingContract.setBorder(null);
        btnAddExitingContract.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddExitingContractActionPerformed(evt);
                }
            });

        final org.jdesktop.layout.GroupLayout pnlKostenControls1Layout = new org.jdesktop.layout.GroupLayout(
                pnlKostenControls1);
        pnlKostenControls1.setLayout(pnlKostenControls1Layout);
        pnlKostenControls1Layout.setHorizontalGroup(
            pnlKostenControls1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                pnlKostenControls1Layout.createSequentialGroup().add(
                    btnAddExitingContract,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    29,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    btnAddVertrag,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    29,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    btnRemoveVertrag,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    29,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));
        pnlKostenControls1Layout.setVerticalGroup(
            pnlKostenControls1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                btnRemoveVertrag,
                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                0,
                Short.MAX_VALUE).add(
                btnAddVertrag,
                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                0,
                Short.MAX_VALUE).add(
                btnAddExitingContract,
                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                0,
                Short.MAX_VALUE));

        jLabel1.setText("Verträge:");

        jScrollPane1.setPreferredSize(new java.awt.Dimension(0, 0));

        tblVertraege.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        tblVertraege.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    { "Verkauf", "105.12-147545", "14€", "8.946€" }
                },
                new String[] { "Vertragsart", "Aktenzeichen", "Quadratmeterpreis", "Kaufpreis (i. NK)" }) {

                Class[] types = new Class[] {
                        java.lang.String.class,
                        java.lang.String.class,
                        java.lang.String.class,
                        java.lang.String.class
                    };
                boolean[] canEdit = new boolean[] { false, false, false, false };

                @Override
                public Class getColumnClass(final int columnIndex) {
                    return types[columnIndex];
                }

                @Override
                public boolean isCellEditable(final int rowIndex, final int columnIndex) {
                    return canEdit[columnIndex];
                }
            });
        tblVertraege.setPreferredSize(new java.awt.Dimension(100, 16));
        jScrollPane1.setViewportView(tblVertraege);

        final org.jdesktop.layout.GroupLayout panVertraegeLayout = new org.jdesktop.layout.GroupLayout(panVertraege);
        panVertraege.setLayout(panVertraegeLayout);
        panVertraegeLayout.setHorizontalGroup(
            panVertraegeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panVertraegeLayout.createSequentialGroup().addContainerGap().add(
                    panVertraegeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        panVertraegeLayout.createSequentialGroup().add(jLabel1).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE).add(
                            pnlKostenControls1,
                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap()).add(
                        org.jdesktop.layout.GroupLayout.TRAILING,
                        panVertraegeLayout.createSequentialGroup().add(
                            jScrollPane1,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE).add(10, 10, 10)))));
        panVertraegeLayout.setVerticalGroup(
            panVertraegeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panVertraegeLayout.createSequentialGroup().addContainerGap().add(
                    panVertraegeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false).add(
                        pnlKostenControls1,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).add(
                        jLabel1,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        24,
                        Short.MAX_VALUE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
                    jScrollPane1,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    220,
                    Short.MAX_VALUE).addContainerGap()));

        jScrollPane4.setPreferredSize(new java.awt.Dimension(0, 0));

        txtBemerkung.setColumns(20);
        txtBemerkung.setLineWrap(true);
        txtBemerkung.setRows(5);
        txtBemerkung.setPreferredSize(new java.awt.Dimension(0, 0));
        jScrollPane4.setViewportView(txtBemerkung);

        lblBemerkung.setText("Bemerkung:");

        final org.jdesktop.layout.GroupLayout panBemerkungLayout = new org.jdesktop.layout.GroupLayout(panBemerkung);
        panBemerkung.setLayout(panBemerkungLayout);
        panBemerkungLayout.setHorizontalGroup(
            panBemerkungLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panBemerkungLayout.createSequentialGroup().addContainerGap().add(
                    panBemerkungLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        lblBemerkung).add(
                        jScrollPane4,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addContainerGap()));
        panBemerkungLayout.setVerticalGroup(
            panBemerkungLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panBemerkungLayout.createSequentialGroup().addContainerGap().add(
                    lblBemerkung,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    24,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    jScrollPane4,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    88,
                    Short.MAX_VALUE).addContainerGap()));

        lblEintragung.setText("Eintragung");

        txtEintragung.setText("16.03.05");
        txtEintragung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtEintragungActionPerformed(evt);
                }
            });

        lblAktenzeichen.setText("Aktenzeichen");

        txtAktenzeichen.setText("21.06.04");

        lblVertragsart.setText("Vertragsart");

        cboVertragsart.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Kauf" }));
        cboVertragsart.setMinimumSize(new java.awt.Dimension(6, 20));
        cboVertragsart.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboVertragsartActionPerformed(evt);
                }
            });

        lblQuadPreis.setText("Quadradmeterpreis");

        txtQuadPreis.setText("14,00€");

        lblKaufpreis.setText("Kaufpreis (inkl. Nebenkosten)");

        txtKaufpreis.setText("8.946€");

        lblAuflassung.setText("Auflassung");

        txtAuflassung.setText("04.03.05");

        txtVoreigentuemer.setText("Stadgemeinde Wuppertal");

        lblVoreigentuemer.setText("Voreigentümer");

        final org.jdesktop.layout.GroupLayout panDataLayout = new org.jdesktop.layout.GroupLayout(panData);
        panData.setLayout(panDataLayout);
        panDataLayout.setHorizontalGroup(
            panDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panDataLayout.createSequentialGroup().addContainerGap().add(
                    panDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        txtVoreigentuemer,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        252,
                        Short.MAX_VALUE).add(
                        lblVoreigentuemer,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        83,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                        panDataLayout.createSequentialGroup().add(
                            panDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING).add(
                                org.jdesktop.layout.GroupLayout.LEADING,
                                txtQuadPreis,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                161,
                                Short.MAX_VALUE).add(
                                org.jdesktop.layout.GroupLayout.LEADING,
                                txtKaufpreis,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                161,
                                Short.MAX_VALUE).add(
                                org.jdesktop.layout.GroupLayout.LEADING,
                                lblQuadPreis,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                161,
                                Short.MAX_VALUE).add(
                                org.jdesktop.layout.GroupLayout.LEADING,
                                lblKaufpreis,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                161,
                                Short.MAX_VALUE).add(
                                org.jdesktop.layout.GroupLayout.LEADING,
                                lblVertragsart,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                161,
                                Short.MAX_VALUE).add(
                                org.jdesktop.layout.GroupLayout.LEADING,
                                cboVertragsart,
                                0,
                                161,
                                Short.MAX_VALUE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
                            panDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                                lblAuflassung,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                85,
                                Short.MAX_VALUE).add(
                                txtAuflassung,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                85,
                                Short.MAX_VALUE).add(
                                lblEintragung,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                85,
                                Short.MAX_VALUE).add(
                                txtEintragung,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                85,
                                Short.MAX_VALUE).add(
                                lblAktenzeichen,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                85,
                                Short.MAX_VALUE).add(
                                txtAktenzeichen,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                85,
                                Short.MAX_VALUE)))).addContainerGap()));
        panDataLayout.setVerticalGroup(
            panDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panDataLayout.createSequentialGroup().addContainerGap().add(
                    lblVoreigentuemer,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    22,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    txtVoreigentuemer,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    20,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                        lblAuflassung,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        22,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                        lblKaufpreis,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        22,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                        txtAuflassung,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                        txtKaufpreis,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                        lblEintragung,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        22,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                        lblQuadPreis,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        24,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                        txtEintragung,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                        txtQuadPreis,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                        lblAktenzeichen,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        22,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                        lblVertragsart,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panDataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                        txtAktenzeichen,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        22,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                        cboVertragsart,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        21,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addContainerGap()));

        jScrollPane2.setViewportView(lstCrossRefs);

        final org.jdesktop.layout.GroupLayout panQuerverweiseLayout = new org.jdesktop.layout.GroupLayout(
                panQuerverweise);
        panQuerverweise.setLayout(panQuerverweiseLayout);
        panQuerverweiseLayout.setHorizontalGroup(
            panQuerverweiseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panQuerverweiseLayout.createSequentialGroup().addContainerGap().add(
                    jScrollPane2,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    370,
                    Short.MAX_VALUE).addContainerGap()));
        panQuerverweiseLayout.setVerticalGroup(
            panQuerverweiseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                panQuerverweiseLayout.createSequentialGroup().addContainerGap().add(
                    jScrollPane2,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    161,
                    Short.MAX_VALUE).addContainerGap()));

        tabKB.addTab("Querverweise", panQuerverweise);

        btnAddKosten.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/add.png"))); // NOI18N
        btnAddKosten.setBorder(null);
        btnAddKosten.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddKostenActionPerformed(evt);
                }
            });

        btnRemoveKosten.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/remove.png"))); // NOI18N
        btnRemoveKosten.setBorder(null);
        btnRemoveKosten.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveKostenActionPerformed(evt);
                }
            });

        final org.jdesktop.layout.GroupLayout pnlKostenControlsLayout = new org.jdesktop.layout.GroupLayout(
                pnlKostenControls);
        pnlKostenControls.setLayout(pnlKostenControlsLayout);
        pnlKostenControlsLayout.setHorizontalGroup(
            pnlKostenControlsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                pnlKostenControlsLayout.createSequentialGroup().add(
                    btnAddKosten,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    29,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE).add(
                    btnRemoveKosten,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    29,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));
        pnlKostenControlsLayout.setVerticalGroup(
            pnlKostenControlsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                pnlKostenControlsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE, false).add(
                    btnRemoveKosten,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    27,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                    btnAddKosten,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    27,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));

        jScrollPane5.setPreferredSize(new java.awt.Dimension(0, 0));

        tblKosten.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        tblKosten.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    { "Notar", "     120€", "27.10.03" },
                    { "Wiederbeschaffungskosten", "20.000€", "30.05.04" }
                },
                new String[] { "Kostenart", "Betrag", "Datum" }) {

                Class[] types = new Class[] { java.lang.String.class, java.lang.String.class, java.lang.String.class };

                @Override
                public Class getColumnClass(final int columnIndex) {
                    return types[columnIndex];
                }
            });
        jScrollPane5.setViewportView(tblKosten);

        final org.jdesktop.layout.GroupLayout panKostenLayout = new org.jdesktop.layout.GroupLayout(panKosten);
        panKosten.setLayout(panKostenLayout);
        panKostenLayout.setHorizontalGroup(
            panKostenLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                panKostenLayout.createSequentialGroup().addContainerGap().add(
                    panKostenLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING).add(
                        jScrollPane5,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        370,
                        Short.MAX_VALUE).add(
                        pnlKostenControls,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addContainerGap()));
        panKostenLayout.setVerticalGroup(
            panKostenLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panKostenLayout.createSequentialGroup().addContainerGap().add(
                    pnlKostenControls,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    jScrollPane5,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    128,
                    Short.MAX_VALUE).addContainerGap()));

        tabKB.addTab("Beschlüsse", panKosten);

        jScrollPane6.setPreferredSize(new java.awt.Dimension(0, 0));

        tblBeschluesse.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        tblBeschluesse.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    { "Rat der Stadt", "04.03.05" }
                },
                new String[] { "Beschlussart", "Datum" }) {

                Class[] types = new Class[] { java.lang.String.class, java.lang.String.class };

                @Override
                public Class getColumnClass(final int columnIndex) {
                    return types[columnIndex];
                }
            });
        jScrollPane6.setViewportView(tblBeschluesse);

        btnAddBeschluss.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/add.png"))); // NOI18N
        btnAddBeschluss.setBorder(null);
        btnAddBeschluss.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddBeschlussActionPerformed(evt);
                }
            });

        btnRemoveBeschluss.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/remove.png"))); // NOI18N
        btnRemoveBeschluss.setBorder(null);
        btnRemoveBeschluss.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveBeschlussActionPerformed(evt);
                }
            });

        tbtnSort.setMaximumSize(new java.awt.Dimension(16, 16));
        tbtnSort.setMinimumSize(new java.awt.Dimension(16, 16));
        tbtnSort.setPreferredSize(new java.awt.Dimension(16, 16));

        final org.jdesktop.layout.GroupLayout pnlBeschluesseControlsLayout = new org.jdesktop.layout.GroupLayout(
                pnlBeschluesseControls);
        pnlBeschluesseControls.setLayout(pnlBeschluesseControlsLayout);
        pnlBeschluesseControlsLayout.setHorizontalGroup(
            pnlBeschluesseControlsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                pnlBeschluesseControlsLayout.createSequentialGroup().add(120, 120, 120).add(
                    tbtnSort,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    34,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    btnAddBeschluss,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    29,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    btnRemoveBeschluss,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    29,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));
        pnlBeschluesseControlsLayout.setVerticalGroup(
            pnlBeschluesseControlsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                pnlBeschluesseControlsLayout.createSequentialGroup().add(
                    pnlBeschluesseControlsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                .add(
                                    org.jdesktop.layout.GroupLayout.TRAILING,
                                    btnRemoveBeschluss,
                                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                    27,
                                    Short.MAX_VALUE).add(
                        org.jdesktop.layout.GroupLayout.TRAILING,
                        btnAddBeschluss,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        27,
                        Short.MAX_VALUE).add(
                        tbtnSort,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addContainerGap()));

        final org.jdesktop.layout.GroupLayout panBeschlussLayout = new org.jdesktop.layout.GroupLayout(panBeschluss);
        panBeschluss.setLayout(panBeschlussLayout);
        panBeschlussLayout.setHorizontalGroup(
            panBeschlussLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                panBeschlussLayout.createSequentialGroup().addContainerGap().add(
                    panBeschlussLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING).add(
                        org.jdesktop.layout.GroupLayout.LEADING,
                        jScrollPane6,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        370,
                        Short.MAX_VALUE).add(
                        pnlBeschluesseControls,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addContainerGap()));
        panBeschlussLayout.setVerticalGroup(
            panBeschlussLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panBeschlussLayout.createSequentialGroup().addContainerGap().add(
                    pnlBeschluesseControls,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    26,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    jScrollPane6,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    129,
                    Short.MAX_VALUE).addContainerGap()));

        tabKB.addTab("Kosten", panBeschluss);

        final org.jdesktop.layout.GroupLayout panTabLayout = new org.jdesktop.layout.GroupLayout(panTab);
        panTab.setLayout(panTabLayout);
        panTabLayout.setHorizontalGroup(
            panTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                panTabLayout.createSequentialGroup().addContainerGap().add(tabKB).addContainerGap()));
        panTabLayout.setVerticalGroup(
            panTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panTabLayout.createSequentialGroup().addContainerGap().add(
                    tabKB,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    0,
                    Short.MAX_VALUE).addContainerGap()));

        tabKB.getAccessibleContext().setAccessibleName("Beschluesse");

        final org.jdesktop.layout.GroupLayout pnlDetailLayout = new org.jdesktop.layout.GroupLayout(pnlDetail);
        pnlDetail.setLayout(pnlDetailLayout);
        pnlDetailLayout.setHorizontalGroup(
            pnlDetailLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                pnlDetailLayout.createSequentialGroup().addContainerGap().add(
                    pnlDetailLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING).add(
                        org.jdesktop.layout.GroupLayout.LEADING,
                        panBemerkung,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).add(
                        org.jdesktop.layout.GroupLayout.LEADING,
                        pnlDetailLayout.createSequentialGroup().add(
                            panData,
                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED).add(
                            panTab,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE)).add(
                        org.jdesktop.layout.GroupLayout.LEADING,
                        panVertraege,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addContainerGap()));
        pnlDetailLayout.setVerticalGroup(
            pnlDetailLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                pnlDetailLayout.createSequentialGroup().addContainerGap().add(
                    panVertraege,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
                    pnlDetailLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        panTab,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).add(
                        panData,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panBemerkung,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap()));

        add(pnlDetail, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtEintragungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_txtEintragungActionPerformed
// TODO add your handling code here:
    } //GEN-LAST:event_txtEintragungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboVertragsartActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboVertragsartActionPerformed
        final Object selectedItem = cboVertragsart.getSelectedItem();
        if ((selectedItem != null) && (selectedItem instanceof VertragsartCustomBean)) {
            final VertragsartCustomBean art = (VertragsartCustomBean)selectedItem;
            switch (art.getId()) {
                case 1: {
                    lblVoreigentuemer.setText("Voreigentümer");
                    break;
                }
                case 2: {
                    lblVoreigentuemer.setText("Erweber");
                    break;
                }
                default: {
                    lblVoreigentuemer.setText("Vertragspartner (Vertragsart nicht definiert)");
                }
            }
        }
    }                                                                                  //GEN-LAST:event_cboVertragsartActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddExitingContractActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddExitingContractActionPerformed
        final JDialog dialog = new JDialog(LagisBroker.getInstance().getParentComponent(), "", true);
        dialog.add(new AddExistingVorgangPanel(currentFlurstueck, vTableModel, lstCrossRefs.getModel()));
        dialog.pack();
        dialog.setIconImage(icoExistingContract.getImage());
        dialog.setTitle("Vorhandener Vertrag hinzufügen...");
        StaticSwingTools.showDialog(dialog);
    }                                                                                         //GEN-LAST:event_btnAddExitingContractActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveBeschlussActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveBeschlussActionPerformed
        final int currentRow = tblBeschluesse.getSelectedRow();
        if (currentRow != -1) {
            // VerwaltungsTableModel currentModel = (VerwaltungsTableModel)tNutzung.getModel();
            documentContainer.removeBeschluss(((JXTable)tblBeschluesse).getFilters().convertRowIndexToModel(
                    currentRow));
        }
    } //GEN-LAST:event_btnRemoveBeschlussActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddBeschlussActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddBeschlussActionPerformed
        // ((JXTable)tblBeschluesse).setSortable(false);
        documentContainer.addNewBeschluss();
        // ((JXTable)tblBeschluesse).setSortable(true);
    } //GEN-LAST:event_btnAddBeschlussActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveKostenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveKostenActionPerformed
        final int currentRow = tblKosten.getSelectedRow();
        if (currentRow != -1) {
            // VerwaltungsTableModel currentModel = (VerwaltungsTableModel)tNutzung.getModel();
            // documentContainer.getKostenTableModel().removeKosten(currentRow);
            // documentContainer.removeKosten(((JXTable)tblVertraege).getFilters().convertRowIndexToModel(currentRow));
            documentContainer.removeKosten(((JXTable)tblKosten).getFilters().convertRowIndexToModel(currentRow));
        }
    } //GEN-LAST:event_btnRemoveKostenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddKostenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddKostenActionPerformed
        // ((JXTable)tblKosten).setSortable(false);
        // KostenCustomBean newKosten = new KostenCustomBean();
// documentContainer.getCurrentSelectedVertrag().get
// documentContainer.getKostenTableModel().addKosten(newKosten));
        documentContainer.addNewKosten();
        // ((JXTable)tblKosten).setSortable(true);
    } //GEN-LAST:event_btnAddKostenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveVertragActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveVertragActionPerformed
        final int currentRow = tblVertraege.getSelectedRow();
        if (currentRow != -1) {
            // VerwaltungsTableModel currentModel = (VerwaltungsTableModel)tNutzung.getModel();
            vTableModel.removeVertrag(((JXTable)tblVertraege).getFilters().convertRowIndexToModel(currentRow));
            updateCrossRefs();
            // vTableModel.fireTableDataChanged();
        }
        documentContainer.clearComponents();
        enableSlaveFlieds(false);
    } //GEN-LAST:event_btnRemoveVertragActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddVertragActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddVertragActionPerformed
        try {
            // ((JXTable)tblVertraege).setSortable(false);
            final VertragCustomBean newVertrag = VertragCustomBean.createNew();
            newVertrag.setVertragsart((VertragsartCustomBean)cboVertragsart.getItemAt(0));
            vTableModel.addVertrag(newVertrag);
            // vTableModel.fireTableDataChanged();
            // vTableModel.fireTableDataChanged();
        } catch (Exception ex) {
            log.error("error creating vertrag bean", ex);
        }
    } //GEN-LAST:event_btnAddVertragActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void updateCrossRefs() {
        if (log.isDebugEnabled()) {
            log.debug("Update der Querverweise");
        }
        final Collection<FlurstueckSchluesselCustomBean> crossRefs = CidsBroker.getInstance()
                    .getCrossreferencesForVertraege(new HashSet(vTableModel.getVertraege()));
        final DefaultUniqueListModel newModel = new DefaultUniqueListModel();
        if (crossRefs != null) {
            if (log.isDebugEnabled()) {
                log.debug("Es sind Querverweise auf Verträg vorhanden");
            }
            if (crossRefs.size() > 0) {
                tabKB.setForegroundAt(0, Color.RED);
            } else {
                tabKB.setForegroundAt(0, null);
            }

            currentFlurstueck.setVertraegeQuerverweise(crossRefs);
            final Iterator<FlurstueckSchluesselCustomBean> it = crossRefs.iterator();
            while (it.hasNext()) {
                if (log.isDebugEnabled()) {
                    log.debug("Ein Querverweis hinzugefügt");
                }
                newModel.addElement(it.next());
            }
            newModel.removeElement(currentFlurstueck.getFlurstueckSchluessel());
        }
        lstCrossRefs.setModel(newModel);
    }
    // End of variables declaration
    @Override
    public String getWidgetName() {
        return WIDGET_NAME;
    }

//    public void mouseReleased(MouseEvent e) {
//    }
//
//    public void mousePressed(MouseEvent e) {
//    }
//
//    public void mouseExited(MouseEvent e) {
//    }
//
//    public void mouseEntered(MouseEvent e) {
//    }
//
//    public void mouseClicked(MouseEvent e) {
//        Object source = e.getSource();
//        if(source instanceof JXTable){
//
//            JXTable currentTable = (JXTable) source;
//            if(currentTable.getModel().equals(vTableModel)){
//
//            } else if(currentTable.getModel().equals(documentContainer.getBeschluesseTableModel())){
//                log.debug("Mit maus auf Beschlusstabelle geklickt");
//
//            } else if(currentTable.getModel().equals(documentContainer.getKostenTableModel())) {
//                log.debug("Mit maus auf Kostentabelle geklickt");
//
//            }
//        }
//    }
    @Override
    public void updateFlurstueckForSaving(final FlurstueckCustomBean flurstueck) {
        final Collection<VertragCustomBean> vertraege = flurstueck.getVertraege();
        if (vertraege != null) {
            LagISUtils.makeCollectionContainSameAsOtherCollection(vertraege, vTableModel.getVertraege());
        } else { // TODO kann das überhaupt noch passieren seid der Umstellung auf cids ?!
            final HashSet newSet = new HashSet();
            newSet.addAll(vTableModel.getVertraege());
            flurstueck.setVertraege(newSet);
        }
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
        if (log.isDebugEnabled()) {
            log.debug("geht generell: " + e.getSource());
        }
        final DefaultListSelectionModel source = (DefaultListSelectionModel)e.getSource();

        if (source.equals(tblVertraege.getSelectionModel())) {
            if (log.isDebugEnabled()) {
                log.debug("Verträge Tabelle");
                log.debug("Mit maus auf Verträgetabelle geklickt");
            }
            final int selecetdRow = tblVertraege.getSelectedRow();
            if ((selecetdRow != -1) && isInEditMode) {
                btnRemoveVertrag.setEnabled(true);
            } else {
                btnRemoveVertrag.setEnabled(false);
            }
        } else if (source.equals(tblKosten.getSelectionModel())) {
            if (log.isDebugEnabled()) {
                log.debug("Kosten Tabelle");
            }
            final int selecetdRow = tblKosten.getSelectedRow();
            if ((selecetdRow != -1) && isInEditMode) {
                btnRemoveKosten.setEnabled(true);
            } else {
                btnRemoveKosten.setEnabled(false);
            }
        } else if (source.equals(tblBeschluesse.getSelectionModel())) {
            if (log.isDebugEnabled()) {
                log.debug("Beschlüsse Tabelle");
            }
            final int selecetdRow = tblBeschluesse.getSelectedRow();
            if ((selecetdRow != -1) && isInEditMode) {
                btnRemoveBeschluss.setEnabled(true);
            } else {
                btnRemoveBeschluss.setEnabled(false);
            }
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        final Object source = e.getSource();
        if (log.isDebugEnabled()) {
            log.debug("source: " + source);
        }
        if (source instanceof JXTable) {
            final JXTable table = (JXTable)source;
            final int currentRow = table.getSelectedRow();
            if (log.isDebugEnabled()) {
                log.debug("Row: " + currentRow);
            }
            if ((currentRow != -1) && isInEditMode) {
                enableSlaveFlieds(true);
            } else {
                enableSlaveFlieds(false);
            }
        } else if (source instanceof JList) {
            if (e.getClickCount() > 1) {
                final FlurstueckSchluesselCustomBean key = (FlurstueckSchluesselCustomBean)
                    lstCrossRefs.getSelectedValue();
                if (key != null) {
                    LagisBroker.getInstance().loadFlurstueck(key);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isEnabled  DOCUMENT ME!
     */
    private void enableSlaveFlieds(final boolean isEnabled) {
        txtAktenzeichen.setEditable(isEnabled);
        txtAuflassung.setEditable(isEnabled);
        txtBemerkung.setEditable(isEnabled);
        txtEintragung.setEditable(isEnabled);
        txtKaufpreis.setEditable(isEnabled);
        txtQuadPreis.setEditable(isEnabled);
        cboVertragsart.setEnabled(isEnabled);
        txtVoreigentuemer.setEditable(isEnabled);
        cboVertragsart.setEnabled(isEnabled);
        btnAddKosten.setEnabled(isEnabled);
        btnAddBeschluss.setEnabled(isEnabled);
    }

    // TODO USE
    @Override
    public Icon getWidgetIcon() {
        return null;
    }
}
