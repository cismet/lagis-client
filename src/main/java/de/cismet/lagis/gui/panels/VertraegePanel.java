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
import de.cismet.lagis.gui.tables.KostenTable;
import de.cismet.lagis.gui.tables.RemoveActionHelper;
import de.cismet.lagis.gui.tables.VertraegeTable;

import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.FlurstueckSaver;

import de.cismet.lagis.models.DefaultUniqueListModel;
import de.cismet.lagis.models.VertraegeTableModel;
import de.cismet.lagis.models.documents.VertragDocumentModelContainer;

import de.cismet.lagis.renderer.DateRenderer;
import de.cismet.lagis.renderer.EuroRenderer;
import de.cismet.lagis.renderer.FlurstueckSchluesselRenderer;

import de.cismet.lagis.thread.BackgroundUpdateThread;

import de.cismet.lagis.util.LagISUtils;
import de.cismet.lagis.util.TableSelectionUtils;

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
    MouseListener,
    RemoveActionHelper {

    //~ Static fields/initializers ---------------------------------------------

    private static final String WIDGET_NAME = "Verträge Panel";

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(VertraegePanel.class);

    //~ Instance fields --------------------------------------------------------

    private FlurstueckCustomBean currentFlurstueck = null;
    private VertraegeTableModel vTableModel = new VertraegeTableModel();
    // private BeschluesseTableModel bTableModel = new BeschluesseTableModel();
    // private KostenTableModel kTableModel = new KostenTableModel();
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
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
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
    private javax.swing.JToggleButton tbtnSortBeschluss;
    private javax.swing.JToggleButton tbtnSortKosten;
    private javax.swing.JToggleButton tbtnSortVertrag;
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
        // tblKosten.setModel(kTableModel);
        TableSelectionUtils.crossReferenceModelAndTable(vTableModel, (VertraegeTable)tblVertraege);
        // tblVertraege.addMouseListener(this);
        documentContainer = new VertragDocumentModelContainer(vTableModel);
        ((VertraegeTable)tblVertraege).setDocumentContainer(documentContainer);
        ((VertraegeTable)tblVertraege).setRemoveActionHelper(this);
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

        ((KostenTable)tblKosten).setDocumentContainer(documentContainer);
        TableSelectionUtils.crossReferenceModelAndTable(documentContainer.getKostenTableModel(),
            (KostenTable)tblKosten);
        ((BeschluesseTable)tblBeschluesse).setDocumentContainer(documentContainer);
        TableSelectionUtils.crossReferenceModelAndTable(documentContainer.getBeschluesseTableModel(),
            (BeschluesseTable)tblBeschluesse);
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
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Flurstück ist städtisch und kann editiert werden");
                            }
                            isFlurstueckEditable = true;
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Flurstück ist nicht städtisch und kann nicht editiert werden");
                            }
                            isFlurstueckEditable = false;
                        }
                        vTableModel.refreshTableModel(getCurrentObject().getVertraege());
                        documentContainer.select(getCurrentObject().getVertraege().isEmpty() ? -1 : 0);
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
                        LOG.error("Fehler im refresh thread: ", ex);
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
            LOG.info("FlurstueckChanged");
            currentFlurstueck = newFlurstueck;
            updateThread.notifyThread(currentFlurstueck);
        } catch (Exception ex) {
            LOG.error("Fehler beim Flurstückswechsel: ", ex);
            LagisBroker.getInstance().flurstueckChangeFinished(VertraegePanel.this);
        }
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
        if (isFlurstueckEditable) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Vertrag --> setComponentEditable");
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
            tbtnSortBeschluss.setEnabled(isEditable);
            tbtnSortKosten.setEnabled(isEditable);
            tbtnSortVertrag.setEnabled(isEditable);
            documentContainer.getBeschluesseTableModel().setInEditMode(isEditable);
            documentContainer.getKostenTableModel().setInEditMode(isEditable);
            tblKosten.setEnabled(isEditable);
            tblBeschluesse.setEnabled(isEditable);
            if (LOG.isDebugEnabled()) {
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
                LOG.debug("Vertrag --> setComponentEditable finished");
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Flurstück ist nicht städtisch Verwaltungen können nicht editiert werden");
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
        final ArrayList<VertragCustomBean> alleVertraege = (ArrayList<VertragCustomBean>)vTableModel.getCidsBeans();
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
        java.awt.GridBagConstraints gridBagConstraints;

        pnlDetail = new javax.swing.JPanel();
        panVertraege = new javax.swing.JPanel();
        pnlKostenControls1 = new javax.swing.JPanel();
        btnAddVertrag = new javax.swing.JButton();
        btnRemoveVertrag = new javax.swing.JButton();
        tbtnSortVertrag = new javax.swing.JToggleButton();
        btnAddExitingContract = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVertraege = new de.cismet.lagis.gui.tables.VertraegeTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
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
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        panTab = new javax.swing.JPanel();
        tabKB = new javax.swing.JTabbedPane();
        panQuerverweise = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstCrossRefs = new javax.swing.JList();
        panKosten = new javax.swing.JPanel();
        pnlKostenControls = new javax.swing.JPanel();
        btnAddKosten = new javax.swing.JButton();
        btnRemoveKosten = new javax.swing.JButton();
        tbtnSortKosten = new javax.swing.JToggleButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblKosten = new KostenTable();
        panBeschluss = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblBeschluesse = new BeschluesseTable();
        pnlBeschluesseControls = new javax.swing.JPanel();
        btnAddBeschluss = new javax.swing.JButton();
        btnRemoveBeschluss = new javax.swing.JButton();
        tbtnSortBeschluss = new javax.swing.JToggleButton();

        setLayout(new java.awt.BorderLayout());

        pnlDetail.setLayout(new java.awt.GridBagLayout());

        panVertraege.setMinimumSize(new java.awt.Dimension(10, 120));
        panVertraege.setPreferredSize(new java.awt.Dimension(10, 140));
        panVertraege.setLayout(new java.awt.GridBagLayout());

        pnlKostenControls1.setLayout(new java.awt.GridBagLayout());

        btnAddVertrag.setAction(((VertraegeTable)tblVertraege).getAddAction());
        btnAddVertrag.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/add.png"))); // NOI18N
        btnAddVertrag.setBorder(null);
        btnAddVertrag.setBorderPainted(false);
        btnAddVertrag.setMaximumSize(new java.awt.Dimension(25, 25));
        btnAddVertrag.setMinimumSize(new java.awt.Dimension(25, 25));
        btnAddVertrag.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlKostenControls1.add(btnAddVertrag, gridBagConstraints);

        btnRemoveVertrag.setAction(((VertraegeTable)tblVertraege).getRemoveAction());
        btnRemoveVertrag.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/remove.png"))); // NOI18N
        btnRemoveVertrag.setBorder(null);
        btnRemoveVertrag.setBorderPainted(false);
        btnRemoveVertrag.setMaximumSize(new java.awt.Dimension(25, 25));
        btnRemoveVertrag.setMinimumSize(new java.awt.Dimension(25, 25));
        btnRemoveVertrag.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        pnlKostenControls1.add(btnRemoveVertrag, gridBagConstraints);

        tbtnSortVertrag.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/sort.png")));          // NOI18N
        tbtnSortVertrag.setToolTipText("Sortierung An / Aus");
        tbtnSortVertrag.setBorder(null);
        tbtnSortVertrag.setBorderPainted(false);
        tbtnSortVertrag.setContentAreaFilled(false);
        tbtnSortVertrag.setMaximumSize(new java.awt.Dimension(25, 25));
        tbtnSortVertrag.setMinimumSize(new java.awt.Dimension(25, 25));
        tbtnSortVertrag.setPreferredSize(new java.awt.Dimension(25, 25));
        tbtnSortVertrag.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/sort_selected.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        pnlKostenControls1.add(tbtnSortVertrag, gridBagConstraints);
        tbtnSortVertrag.addItemListener(((de.cismet.lagis.gui.tables.VertraegeTable)tblVertraege)
                    .getSortItemListener());

        btnAddExitingContract.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/contract.png"))); // NOI18N
        btnAddExitingContract.setBorder(null);
        btnAddExitingContract.setBorderPainted(false);
        btnAddExitingContract.setMaximumSize(new java.awt.Dimension(25, 25));
        btnAddExitingContract.setMinimumSize(new java.awt.Dimension(25, 25));
        btnAddExitingContract.setPreferredSize(new java.awt.Dimension(25, 25));
        btnAddExitingContract.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddExitingContractActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlKostenControls1.add(btnAddExitingContract, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panVertraege.add(pnlKostenControls1, gridBagConstraints);

        jLabel1.setText("Verträge:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panVertraege.add(jLabel1, gridBagConstraints);

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
        tblVertraege.setPreferredSize(new java.awt.Dimension(150, 18));
        ((VertraegeTable)tblVertraege).setSortButton(tbtnSortVertrag);
        jScrollPane1.setViewportView(tblVertraege);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panVertraege.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 6, 12);
        pnlDetail.add(panVertraege, gridBagConstraints);

        jScrollPane3.setBorder(null);
        jScrollPane3.setViewportBorder(null);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        panBemerkung.setLayout(new java.awt.GridBagLayout());

        txtBemerkung.setColumns(20);
        txtBemerkung.setLineWrap(true);
        txtBemerkung.setRows(5);
        jScrollPane4.setViewportView(txtBemerkung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBemerkung.add(jScrollPane4, gridBagConstraints);

        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panBemerkung.add(lblBemerkung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        jPanel1.add(panBemerkung, gridBagConstraints);

        panData.setLayout(new java.awt.GridBagLayout());

        lblEintragung.setText("Eintragung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 12, 0, 0);
        panData.add(lblEintragung, gridBagConstraints);

        txtEintragung.setText("16.03.05");
        txtEintragung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtEintragungActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        panData.add(txtEintragung, gridBagConstraints);

        lblAktenzeichen.setText("Aktenzeichen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        panData.add(lblAktenzeichen, gridBagConstraints);

        txtAktenzeichen.setText("21.06.04");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        panData.add(txtAktenzeichen, gridBagConstraints);

        lblVertragsart.setText("Vertragsart");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 0, 0, 0);
        panData.add(lblVertragsart, gridBagConstraints);

        cboVertragsart.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Kauf" }));
        cboVertragsart.setMinimumSize(new java.awt.Dimension(6, 20));
        cboVertragsart.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboVertragsartActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        panData.add(cboVertragsart, gridBagConstraints);

        lblQuadPreis.setText("Quadradmeterpreis");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        panData.add(lblQuadPreis, gridBagConstraints);

        txtQuadPreis.setText("14,00€");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        panData.add(txtQuadPreis, gridBagConstraints);

        lblKaufpreis.setText("Kaufpreis (inkl. Nebenkosten)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        panData.add(lblKaufpreis, gridBagConstraints);

        txtKaufpreis.setText("8.946€");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        panData.add(txtKaufpreis, gridBagConstraints);

        lblAuflassung.setText("Auflassung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        panData.add(lblAuflassung, gridBagConstraints);

        txtAuflassung.setText("04.03.05");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        panData.add(txtAuflassung, gridBagConstraints);

        txtVoreigentuemer.setText("Stadgemeinde Wuppertal");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        panData.add(txtVoreigentuemer, gridBagConstraints);

        lblVoreigentuemer.setText("Voreigentümer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panData.add(lblVoreigentuemer, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        panData.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        jPanel1.add(panData, gridBagConstraints);

        panTab.setLayout(new java.awt.GridBagLayout());

        tabKB.setToolTipText("Sortierung An / Aus");

        panQuerverweise.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setViewportView(lstCrossRefs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        panQuerverweise.add(jScrollPane2, gridBagConstraints);

        tabKB.addTab("Querverweise", panQuerverweise);

        panKosten.setLayout(new java.awt.GridBagLayout());

        pnlKostenControls.setLayout(new java.awt.GridBagLayout());

        btnAddKosten.setAction(((KostenTable)tblKosten).getAddAction());
        btnAddKosten.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/add.png"))); // NOI18N
        btnAddKosten.setBorder(null);
        btnAddKosten.setBorderPainted(false);
        btnAddKosten.setMaximumSize(new java.awt.Dimension(25, 25));
        btnAddKosten.setMinimumSize(new java.awt.Dimension(25, 25));
        btnAddKosten.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlKostenControls.add(btnAddKosten, gridBagConstraints);

        btnRemoveKosten.setAction(((KostenTable)tblKosten).getRemoveAction());
        btnRemoveKosten.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/remove.png"))); // NOI18N
        btnRemoveKosten.setBorder(null);
        btnRemoveKosten.setBorderPainted(false);
        btnRemoveKosten.setMaximumSize(new java.awt.Dimension(25, 25));
        btnRemoveKosten.setMinimumSize(new java.awt.Dimension(25, 25));
        btnRemoveKosten.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        pnlKostenControls.add(btnRemoveKosten, gridBagConstraints);

        tbtnSortKosten.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/sort.png")));          // NOI18N
        tbtnSortKosten.setToolTipText("Sortierung An / Aus");
        tbtnSortKosten.setBorder(null);
        tbtnSortKosten.setBorderPainted(false);
        tbtnSortKosten.setContentAreaFilled(false);
        tbtnSortKosten.setMaximumSize(new java.awt.Dimension(25, 25));
        tbtnSortKosten.setMinimumSize(new java.awt.Dimension(25, 25));
        tbtnSortKosten.setPreferredSize(new java.awt.Dimension(25, 25));
        tbtnSortKosten.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/sort_selected.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        pnlKostenControls.add(tbtnSortKosten, gridBagConstraints);
        tbtnSortKosten.addItemListener(((KostenTable)tblKosten).getSortItemListener());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 12);
        panKosten.add(pnlKostenControls, gridBagConstraints);

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
        ((KostenTable)tblKosten).setSortButton(tbtnSortKosten);
        jScrollPane5.setViewportView(tblKosten);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 12, 12);
        panKosten.add(jScrollPane5, gridBagConstraints);

        tabKB.addTab("Kosten", panKosten);

        panBeschluss.setLayout(new java.awt.GridBagLayout());

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
        ((BeschluesseTable)tblBeschluesse).setSortButton(tbtnSortBeschluss);
        jScrollPane6.setViewportView(tblBeschluesse);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 12, 12);
        panBeschluss.add(jScrollPane6, gridBagConstraints);

        pnlBeschluesseControls.setLayout(new java.awt.GridBagLayout());

        btnAddBeschluss.setAction(((BeschluesseTable)tblBeschluesse).getAddAction());
        btnAddBeschluss.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/add.png"))); // NOI18N
        btnAddBeschluss.setBorder(null);
        btnAddBeschluss.setBorderPainted(false);
        btnAddBeschluss.setMaximumSize(new java.awt.Dimension(25, 25));
        btnAddBeschluss.setMinimumSize(new java.awt.Dimension(25, 25));
        btnAddBeschluss.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        pnlBeschluesseControls.add(btnAddBeschluss, gridBagConstraints);

        btnRemoveBeschluss.setAction(((BeschluesseTable)tblBeschluesse).getRemoveAction());
        btnRemoveBeschluss.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/remove.png"))); // NOI18N
        btnRemoveBeschluss.setBorder(null);
        btnRemoveBeschluss.setBorderPainted(false);
        btnRemoveBeschluss.setMaximumSize(new java.awt.Dimension(25, 25));
        btnRemoveBeschluss.setMinimumSize(new java.awt.Dimension(25, 25));
        btnRemoveBeschluss.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        pnlBeschluesseControls.add(btnRemoveBeschluss, gridBagConstraints);

        tbtnSortBeschluss.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/sort.png")));          // NOI18N
        tbtnSortBeschluss.setBorder(null);
        tbtnSortBeschluss.setBorderPainted(false);
        tbtnSortBeschluss.setContentAreaFilled(false);
        tbtnSortBeschluss.setMaximumSize(new java.awt.Dimension(25, 25));
        tbtnSortBeschluss.setMinimumSize(new java.awt.Dimension(25, 25));
        tbtnSortBeschluss.setPreferredSize(new java.awt.Dimension(25, 25));
        tbtnSortBeschluss.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/sort_selected.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        pnlBeschluesseControls.add(tbtnSortBeschluss, gridBagConstraints);
        tbtnSortBeschluss.addItemListener(((BeschluesseTable)tblBeschluesse).getSortItemListener());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 12);
        panBeschluss.add(pnlBeschluesseControls, gridBagConstraints);

        tabKB.addTab("Beschlüsse", panBeschluss);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panTab.add(tabKB, gridBagConstraints);
        tabKB.getAccessibleContext().setAccessibleName("Beschluesse");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel1.add(panTab, gridBagConstraints);

        jScrollPane3.setViewportView(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 12, 12);
        pnlDetail.add(jScrollPane3, gridBagConstraints);

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
                    lblVoreigentuemer.setText("Erwerber");
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
     */
    private void updateCrossRefs() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Update der Querverweise");
        }
        final Collection<FlurstueckSchluesselCustomBean> crossRefs = CidsBroker.getInstance()
                    .getCrossreferencesForVertraege(new HashSet(vTableModel.getCidsBeans()));
        final DefaultUniqueListModel newModel = new DefaultUniqueListModel();
        if (crossRefs != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Es sind Querverweise auf Verträg vorhanden");
            }
            if (crossRefs.size() > 0) {
                tabKB.setForegroundAt(0, Color.RED);
            } else {
                tabKB.setForegroundAt(0, null);
            }

            currentFlurstueck.setVertraegeQuerverweise(crossRefs);
            final Iterator<FlurstueckSchluesselCustomBean> it = crossRefs.iterator();
            while (it.hasNext()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ein Querverweis hinzugefügt");
                }
                newModel.addElement(it.next());
            }
            newModel.removeElement(currentFlurstueck.getFlurstueckSchluessel());
        }
        lstCrossRefs.setModel(newModel);
    }

    @Override
    public String getWidgetName() {
        return WIDGET_NAME;
    }

    @Override
    public void updateFlurstueckForSaving(final FlurstueckCustomBean flurstueck) {
        final Collection<VertragCustomBean> vertraege = flurstueck.getVertraege();
        if (vertraege != null) {
            LagISUtils.makeCollectionContainSameAsOtherCollection(vertraege, vTableModel.getCidsBeans());
        } else { // TODO kann das überhaupt noch passieren seid der Umstellung auf cids ?!
            final HashSet newSet = new HashSet();
            newSet.addAll(vTableModel.getCidsBeans());
            flurstueck.setVertraege(newSet);
        }
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("geht generell: " + e.getSource());
        }
        final DefaultListSelectionModel source = (DefaultListSelectionModel)e.getSource();

        if (source.equals(tblVertraege.getSelectionModel())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Verträge Tabelle");
                LOG.debug("Mit maus auf Verträgetabelle geklickt");
            }
            final int selecetdRow = tblVertraege.getSelectedRow();
            if ((selecetdRow != -1) && isInEditMode) {
                btnRemoveVertrag.setEnabled(true);
            } else {
                btnRemoveVertrag.setEnabled(false);
            }
        } else if (source.equals(tblKosten.getSelectionModel())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Kosten Tabelle");
            }
            final int selecetdRow = tblKosten.getSelectedRow();
            if ((selecetdRow != -1) && isInEditMode) {
                btnRemoveKosten.setEnabled(true);
            } else {
                btnRemoveKosten.setEnabled(false);
            }
        } else if (source.equals(tblBeschluesse.getSelectionModel())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Beschlüsse Tabelle");
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("source: " + source);
        }
        if (source instanceof JXTable) {
            final JXTable table = (JXTable)source;
            final int currentRow = table.getSelectedRow();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Row: " + currentRow);
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

    @Override
    public void duringRemoveAction(final Object source) {
        updateCrossRefs();
    }

    @Override
    public void afterRemoveAction(final Object source) {
        documentContainer.clearComponents();
        enableSlaveFlieds(false);
    }
}
