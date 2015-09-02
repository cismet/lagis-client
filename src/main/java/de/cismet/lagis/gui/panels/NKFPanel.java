/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * RechteTabellenPanel.java
 *
 * Created on 16. März 2007, 12:02
 */
package de.cismet.lagis.gui.panels;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;
import org.jdesktop.swingx.decorator.*;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.SortOrder;

import org.jdom.Element;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;

import java.text.SimpleDateFormat;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableCellEditor;

import de.cismet.cids.custom.beans.lagis.*;

import de.cismet.lagis.Exception.BuchungNotInNutzungException;
import de.cismet.lagis.Exception.IllegalNutzungStateException;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.editor.EuroEditor;
import de.cismet.lagis.editor.FlaecheEditor;
import de.cismet.lagis.editor.PlanEditor;

import de.cismet.lagis.gui.tables.NKFTable;

import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.FlurstueckSaver;
import de.cismet.lagis.interfaces.Refreshable;

import de.cismet.lagis.models.NKFTableModel;

import de.cismet.lagis.renderer.EuroRenderer;
import de.cismet.lagis.renderer.FlaecheRenderer;
import de.cismet.lagis.renderer.PlanRenderer;

import de.cismet.lagis.thread.BackgroundUpdateThread;

import de.cismet.lagis.util.LagISUtils;
import de.cismet.lagis.util.NutzungsContainer;
import de.cismet.lagis.util.TableSelectionUtils;

import de.cismet.lagis.validation.Validatable;

import de.cismet.lagis.widget.AbstractWidget;

import de.cismet.tools.CurrentStackTrace;

import de.cismet.tools.configuration.Configurable;

import de.cismet.tools.gui.jbands.JBand;
import de.cismet.tools.gui.jbands.PlainBand;
import de.cismet.tools.gui.jbands.SimpleBandModel;
import de.cismet.tools.gui.jbands.SimpleTextSection;
import de.cismet.tools.gui.jbands.interfaces.BandMemberSelectable;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class NKFPanel extends AbstractWidget implements MouseListener,
    FlurstueckChangeListener,
    FlurstueckSaver,
    TableModelListener,
    ListSelectionListener,
    Configurable {

    //~ Static fields/initializers ---------------------------------------------

    private static final String WIDGET_NAME = "NKF Datenpanel";
    private static final String FIND_PREDECESSOR_MENU_NAME = "Vorgänger finden";
    private static final NKFPanel instance = new NKFPanel();
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(NKFPanel.class);

    //~ Instance fields --------------------------------------------------------

    // perhaps not good
    boolean isOnlyHistoric = false;
    private FlurstueckCustomBean currentFlurstueck;
    private final NKFTableModel tableModel = new NKFTableModel();
    private boolean isInEditMode = false;
    private BackgroundUpdateThread<FlurstueckCustomBean> updateThread;
    private boolean isFlurstueckEditable = true;
    private final Icon icoHistoricIcon = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/nutzung/history64.png"));
    private final Icon icoHistoricIconDummy = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/nutzung/emptyDummy64.png"));
    private final Icon icoBooked = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/nutzung/booked.png"));
    private final Icon icoNotBooked = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/nutzung/notBooked.png"));
    private final ArrayList<NutzungCustomBean> copyPasteList = new ArrayList();
    private JPopupMenu predecessorPopup;

    private NutzungBuchungCustomBean currentPopupNutzung = null;

    private final PlainBand bandNutzungen;
    private final PlainBand bandMonth;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddNutzung;
    private javax.swing.JButton btnCopyNutzung;
    private javax.swing.JButton btnFlipBuchung;
    private javax.swing.JButton btnPasteNutzung;
    private javax.swing.JButton btnRemoveNutzung;
    private de.cismet.tools.gui.jbands.JBand jBand1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCurrentHistoryPostion;
    private javax.swing.JLabel lblHistoricIcon;
    private javax.swing.JTable tNutzung;
    private javax.swing.JToggleButton tbtnSort;
    // End of variables declaration//GEN-END:variables
    // TODO nicht die Methode überschreiben sondern ein Feld in der Superklasse anlegen und dieses Feld in der erbenden
    // Klasse überschreiben

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NKFPanel object.
     */
    private NKFPanel() {
        setIsCoreWidget(true);
        initComponents();
        final SimpleBandModel bandModel = new SimpleBandModel();
        bandNutzungen = new PlainBand();
        bandMonth = new PlainBand();
//                final PlainBand bandYear = new PlainBand();

//                bandModel.addBand(bandYear);
        bandModel.addBand(bandMonth);
        bandModel.addBand(bandNutzungen);
        jBand1.setModel(bandModel);

        btnRemoveNutzung.setEnabled(false);
        ((NKFTable)tNutzung).getAddAction().setEnabled(false);
        btnFlipBuchung.setEnabled(false);
        configureTable();
        configBackgroundThread();
        configurePopupMenue();

        jBand1.setSelectionMode(JBand.SelectionMode.SINGLE_SELECTION);
        jBand1.setHideEmptyPrePostfix(true);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JTable getNutzungTable() {
        return tNutzung;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static NKFPanel getInstance() {
        return instance;
    }

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
                        final Collection<NutzungCustomBean> newNutzungen = getCurrentObject().getNutzungen();
                        tableModel.refreshTableModel(newNutzungen);
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        if (newNutzungen != null) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Es sind Nutzungen vorhanden: " + newNutzungen.size());
                            }
                        }
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        updateBands();
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        LagisBroker.getInstance().flurstueckChangeFinished(NKFPanel.this);
                    } catch (Exception ex) {
                        LOG.error("Fehler im refresh thread: ", ex);
                        LagisBroker.getInstance().flurstueckChangeFinished(NKFPanel.this);
                    }
                }

                @Override
                protected void cleanup() {
                }
            };
        updateThread.setPriority(Thread.NORM_PRIORITY);
        updateThread.start();
    }

    /**
     * TODO Forbid if time bar mode is active.
     */
    private void configurePopupMenue() {
        predecessorPopup = new JPopupMenu();
        final JMenuItem findPredecessor = new JMenuItem(FIND_PREDECESSOR_MENU_NAME);
        findPredecessor.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    findPredecessorForNutzung(e);
                }
            });
        predecessorPopup.add(findPredecessor);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    private void findPredecessorForNutzung(final ActionEvent e) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ActionEvent: " + e.getActionCommand());
        }
        if (currentPopupNutzung != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("currentPopupNutzung vorhanden");
            }
            jumpToPredecessorNutzung(currentPopupNutzung);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void configureTable() {
        TableSelectionUtils.crossReferenceModelAndTable(tableModel, (NKFTable)tNutzung);
        tNutzung.getSelectionModel().addListSelectionListener(this);
        final JComboBox cboAK = new JComboBox(new Vector<AnlageklasseCustomBean>(
                    CidsBroker.getInstance().getAllAnlageklassen()));
        cboAK.addItem("");
        tNutzung.setDefaultEditor(AnlageklasseCustomBean.class, new DefaultCellEditor(cboAK));
        tNutzung.setDefaultRenderer(Integer.class, new FlaecheRenderer());
        tNutzung.setDefaultEditor(Integer.class, new FlaecheEditor());
        final Vector<NutzungsartCustomBean> nutzungsarten = new Vector<NutzungsartCustomBean>(CidsBroker.getInstance()
                        .getAllNutzungsarten());
        Collections.sort(nutzungsarten);
        final JComboBox cboNA = new JComboBox(nutzungsarten);
        cboNA.addItem("");
        cboNA.setEditable(true);
        tNutzung.setDefaultEditor(NutzungsartCustomBean.class, new ComboBoxCellEditor(cboNA));
        tNutzung.setDefaultEditor(Vector.class, new PlanEditor());
        tNutzung.setDefaultRenderer(Vector.class, new PlanRenderer());
        tNutzung.setDefaultEditor(Double.class, new EuroEditor());
        tNutzung.setDefaultRenderer(Double.class, new EuroRenderer());
        tNutzung.addMouseListener(this);
        tNutzung.addMouseListener(new PopupListener());
        tableModel.addTableModelListener(this);
        final HighlightPredicate buchungsStatusPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    try {
                        if (componentAdapter.getRowCount() > 0) {
                            final int displayedIndex = componentAdapter.row;
                            final int modelIndex = ((JXTable)tNutzung).getFilters()
                                        .convertRowIndexToModel(displayedIndex);
                            final NutzungBuchungCustomBean n = tableModel.getCidsBeanAtRow(modelIndex);
                            // NO Geometry & more than one Verwaltungsbereich
                            return (n != null) && !n.getIstBuchwert();
                        } else {
                            return false;
                        }
                    } catch (Exception ex) {
                        LOG.error("Fehler beim Highlighting des Buchwerts vorhanden", ex);
                        return false;
                    }
                }
            };

        final Highlighter buchungsStatusHighlighter = new ColorHighlighter(
                buchungsStatusPredicate,
                LagisBroker.UNKOWN_COLOR,
                null);
        // (LagisBroker.grey, null, 0, -1)
        final HighlightPredicate geloeschtPredicate = new HighlightPredicate() {

                @Override
                public boolean isHighlighted(final Component renderer, final ComponentAdapter componentAdapter) {
                    try {
                        if (componentAdapter.getRowCount() > 0) {
                            final int displayedIndex = componentAdapter.row;
                            final int modelIndex = ((JXTable)tNutzung).getFilters()
                                        .convertRowIndexToModel(displayedIndex);
                            final NutzungBuchungCustomBean n = tableModel.getCidsBeanAtRow(modelIndex);
                            // NO Geometry & more than one Verwaltungsbereich
                            return ((n != null) && n.getSollGeloeschtWerden());
                        } else {
                            return false;
                        }
                    } catch (Exception ex) {
                        LOG.error("Fehler beim Highlighting test wurde gelöscht vorhanden", ex);
                        return false;
                    }
                }
            };

        final Highlighter geloeschtHighlighter = new ColorHighlighter(geloeschtPredicate, LagisBroker.grey, null);
        ((JXTable)tNutzung).setHighlighters(
            LagisBroker.ALTERNATE_ROW_HIGHLIGHTER,
            buchungsStatusHighlighter,
            geloeschtHighlighter);
        ((JXTable)tNutzung).setSortOrder(0, SortOrder.ASCENDING);
        ((JXTable)tNutzung).setColumnControlVisible(true);
        ((JXTable)tNutzung).setHorizontalScrollEnabled(true);
        ((JXTable)tNutzung).packAll();
        ((NKFTable)tNutzung).setSortButton(tbtnSort);
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
            LagisBroker.getInstance().flurstueckChangeFinished(NKFPanel.this);
        }
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
        if (isFlurstueckEditable) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("NKFPanel --> setComponentEditable");
            }
            isInEditMode = isEditable;
            tableModel.setInEditMode(isEditable);
            if (isEditable) {
                memberUnselect();
                ((NKFTable)tNutzung).getAddAction().setEnabled(true);

                if (tNutzung.getSelectedRow() != -1) {
                    btnCopyNutzung.setEnabled(true);
                    final int index = ((JXTable)tNutzung).convertRowIndexToModel(tNutzung.getSelectedRow());
                    final NutzungBuchungCustomBean selectedBuchung = tableModel.getCidsBeanAtRow(index);

                    if (selectedBuchung.isBuchwertFlippable() && LagisBroker.getInstance().isNkfAdminPermission()) {
                        btnFlipBuchung.setEnabled(true);
                    }
                    if (index != -1) {
                        // TODO NKF Testen
                        if (selectedBuchung.getGueltigbis() == null) {
                            btnRemoveNutzung.setEnabled(true);
                        }
                    }
                }
                if (copyPasteList.size() > 0) {
                    btnPasteNutzung.setEnabled(isEditable);
                }
            } else {
                btnFlipBuchung.setEnabled(false);
                btnPasteNutzung.setEnabled(isEditable);
                btnCopyNutzung.setEnabled(false);
                ((NKFTable)tNutzung).getAddAction().setEnabled(false);
                final TableCellEditor currentEditor = tNutzung.getCellEditor();
                if (currentEditor != null) {
                    currentEditor.cancelCellEditing();
                }
                btnRemoveNutzung.setEnabled(false);
                btnCopyNutzung.setEnabled(false);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("NKFPanel --> setComponentEditable finished");
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Flurstück ist nicht städtisch Verwaltungen können nicht editiert werden");
            }
        }
    }

    @Override
    public synchronized void clearComponent() {
        tableModel.refreshTableModel(null);
    }

    // TODO validate the single cell of the tables
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

        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tNutzung = new NKFTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblCurrentHistoryPostion = new javax.swing.JLabel();
        lblHistoricIcon = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnAddNutzung = new javax.swing.JButton();
        btnRemoveNutzung = new javax.swing.JButton();
        btnPasteNutzung = new javax.swing.JButton();
        btnCopyNutzung = new javax.swing.JButton();
        btnFlipBuchung = new javax.swing.JButton();
        tbtnSort = new javax.swing.JToggleButton();
        jBand1 = new de.cismet.tools.gui.jbands.JBand(true);
        jComboBox1 = new javax.swing.JComboBox();

        setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        tNutzung.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        tNutzung.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    { "21-510", "Straße", "k0211100", "Grünfläche,Grünfläche,Wohnbaufläche", "", "842", "1€", "842€" },
                    { "21-740", "Gehölz", "k0211100", null, "", "2325", "1€", "2325" }
                },
                new String[] {
                    "Nutzungsartenschlüssel",
                    "Nutzungsart",
                    "Anlageklasse",
                    "Flächennutzungsplan",
                    "Bebauungsplan",
                    "Fläche m²",
                    "Preis qm²",
                    "Berechnung"
                }) {

                Class[] types = new Class[] {
                        java.lang.String.class,
                        java.lang.String.class,
                        java.lang.String.class,
                        java.lang.String.class,
                        java.lang.String.class,
                        java.lang.String.class,
                        java.lang.String.class,
                        java.lang.String.class
                    };

                @Override
                public Class getColumnClass(final int columnIndex) {
                    return types[columnIndex];
                }
            });
        jScrollPane1.setViewportView(tNutzung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel2.add(jScrollPane1, gridBagConstraints);

        jLabel1.setText("Nutzungen:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(jLabel1, gridBagConstraints);

        jLabel2.setText("NKF Historie:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        jPanel2.add(jLabel2, gridBagConstraints);

        lblCurrentHistoryPostion.setText("Keine Historie vorhanden");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 6, 0, 0);
        jPanel2.add(lblCurrentHistoryPostion, gridBagConstraints);

        lblHistoricIcon.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/nutzung/emptyDummy64.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        jPanel2.add(lblHistoricIcon, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        btnAddNutzung.setAction(((NKFTable)tNutzung).getAddAction());
        btnAddNutzung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/add.png"))); // NOI18N
        btnAddNutzung.setBorder(null);
        btnAddNutzung.setBorderPainted(false);
        btnAddNutzung.setMaximumSize(new java.awt.Dimension(25, 25));
        btnAddNutzung.setMinimumSize(new java.awt.Dimension(25, 25));
        btnAddNutzung.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel1.add(btnAddNutzung, gridBagConstraints);

        btnRemoveNutzung.setAction(((NKFTable)tNutzung).getRemoveAction());
        btnRemoveNutzung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/remove.png"))); // NOI18N
        btnRemoveNutzung.setBorder(null);
        btnRemoveNutzung.setBorderPainted(false);
        btnRemoveNutzung.setMaximumSize(new java.awt.Dimension(25, 25));
        btnRemoveNutzung.setMinimumSize(new java.awt.Dimension(25, 25));
        btnRemoveNutzung.setPreferredSize(new java.awt.Dimension(25, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel1.add(btnRemoveNutzung, gridBagConstraints);

        btnPasteNutzung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/pasteNu.png"))); // NOI18N
        btnPasteNutzung.setToolTipText("Buchung einfügen");
        btnPasteNutzung.setBorderPainted(false);
        btnPasteNutzung.setMaximumSize(new java.awt.Dimension(25, 25));
        btnPasteNutzung.setMinimumSize(new java.awt.Dimension(25, 25));
        btnPasteNutzung.setPreferredSize(new java.awt.Dimension(25, 25));
        btnPasteNutzung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnPasteNutzungActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel1.add(btnPasteNutzung, gridBagConstraints);

        btnCopyNutzung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/copyNu.png"))); // NOI18N
        btnCopyNutzung.setToolTipText("Buchung kopieren");
        btnCopyNutzung.setBorderPainted(false);
        btnCopyNutzung.setMaximumSize(new java.awt.Dimension(25, 25));
        btnCopyNutzung.setMinimumSize(new java.awt.Dimension(25, 25));
        btnCopyNutzung.setPreferredSize(new java.awt.Dimension(25, 25));
        btnCopyNutzung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCopyNutzungActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel1.add(btnCopyNutzung, gridBagConstraints);

        btnFlipBuchung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/nutzung/booked.png"))); // NOI18N
        btnFlipBuchung.setToolTipText("Buchwert / kein Buchwert");
        btnFlipBuchung.setBorderPainted(false);
        btnFlipBuchung.setMaximumSize(new java.awt.Dimension(25, 25));
        btnFlipBuchung.setMinimumSize(new java.awt.Dimension(25, 25));
        btnFlipBuchung.setPreferredSize(new java.awt.Dimension(25, 25));
        btnFlipBuchung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnFlipBuchungActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel1.add(btnFlipBuchung, gridBagConstraints);

        tbtnSort.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/sort.png")));          // NOI18N
        tbtnSort.setToolTipText("Sortierung An / Aus");
        tbtnSort.setBorderPainted(false);
        tbtnSort.setContentAreaFilled(false);
        tbtnSort.setMaximumSize(new java.awt.Dimension(25, 25));
        tbtnSort.setMinimumSize(new java.awt.Dimension(25, 25));
        tbtnSort.setPreferredSize(new java.awt.Dimension(25, 25));
        tbtnSort.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/sort_selected.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        jPanel1.add(tbtnSort, gridBagConstraints);
        tbtnSort.addItemListener(((NKFTable)tNutzung).getSortItemListener());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jPanel1, gridBagConstraints);

        jBand1.setMinimumSize(new java.awt.Dimension(23, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jBand1, gridBagConstraints);

        jComboBox1.setRenderer(new DateComboBoxRenderer());
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jComboBox1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 0, 0);
        jPanel2.add(jComboBox1, gridBagConstraints);
        jComboBox1.setVisible(true);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        add(jPanel2, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCopyNutzungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCopyNutzungActionPerformed
        copyPasteList.clear();
        if (tNutzung.getSelectedRow() != -1) {
            final int[] selectedRows = tNutzung.getSelectedRows();
            for (int i = 0; i < selectedRows.length; i++) {
                tNutzung.getSelectedRow();
                final int index = ((JXTable)tNutzung).convertRowIndexToModel(selectedRows[i]);
                final NutzungBuchungCustomBean curNutzungToCopy = tableModel.getCidsBeanAtRow(index);
                if (curNutzungToCopy != null) {
                    try {
                        copyPasteList.add(NutzungCustomBean.createNew(curNutzungToCopy.cloneBuchung()));
                    } catch (Exception ex) {
                        LOG.error("Fehler beim kopieren einer Buchung: ", ex);
                        JOptionPane.showMessageDialog(LagisBroker.getInstance().getParentComponent(),
                            "Die Buchung konnte nicht kopiert werden, da die zu \n"
                                    + "kopierende Buchung Fehler enthält",
                            "Fehler beim kopieren einer Buchung",
                            JOptionPane.OK_OPTION);
                        return;
                    }
                }
            }
        }
        if (isInEditMode) {
            btnPasteNutzung.setEnabled(true);
        }
    }                                                                                  //GEN-LAST:event_btnCopyNutzungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnPasteNutzungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnPasteNutzungActionPerformed
        if (copyPasteList.size() > 0) {
            NutzungCustomBean lastNutzung = null;
            for (final NutzungCustomBean curNutzung : copyPasteList) {
                tableModel.addNutzung(curNutzung);
                lastNutzung = curNutzung;
            }
            selectNutzungInHistory(lastNutzung.getNutzungsBuchungen().get(0));
        }
    }                                                                                   //GEN-LAST:event_btnPasteNutzungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFlipBuchungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnFlipBuchungActionPerformed
        if (LOG.isDebugEnabled()) {
            LOG.debug("Flippe Buchung");
        }
        final int index = ((JXTable)tNutzung).convertRowIndexToModel(tNutzung.getSelectedRow());
        if (index != -1) {
            final NutzungBuchungCustomBean selectedBuchung = tableModel.getCidsBeanAtRow(index);
            if (selectedBuchung.isBuchwertFlippable()) {
                try {
                    selectedBuchung.flipBuchungsBuchwert();
                    tableModel.fireTableDataChanged();
                    tNutzung.repaint();
                } catch (IllegalNutzungStateException ex) {
                    LOG.error("Buchwert kann nicht geflipped werden, Nutzung in illegalem Zustand: ", ex);
                } catch (BuchungNotInNutzungException ex) {
                    LOG.error(
                        "Buchwert kann nicht geflipped werden, Die Buchung ist nicht in der Nutzung vorhanden: ",
                        ex);
                }
            }
        } else {
            LOG.warn("Keine Buchung selektiert, sollte nicht möglich sein");
        }
    }                                                                                  //GEN-LAST:event_btnFlipBuchungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jComboBox1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jComboBox1ActionPerformed
        final Date date = (Date)jComboBox1.getSelectedItem();
        tableModel.setModelToHistoryDate(date);
    }                                                                              //GEN-LAST:event_jComboBox1ActionPerformed

    @Override
    public String getWidgetName() {
        return WIDGET_NAME;
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
            LOG.debug("MouseClicked");
        }
        // falls es NutzungCustomBean eine Stille Reserve besitzt zu der entsprechenden NutzungCustomBean springen
        if (source instanceof JXTable) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Mit maus auf NKFTabelle geklickt");
            }
            final int selecetdRow = tNutzung.getSelectedRow();
            if (selecetdRow != -1) {
                final NutzungBuchungCustomBean nutzung = tableModel.getCidsBeanAtRow(((JXTable)tNutzung)
                                .convertRowIndexToModel(
                                    selecetdRow));
                if ((nutzung != null) && (e.getClickCount() == 2)
                            && (!isInEditMode
                                || ((tNutzung.getSelectedColumn() == 1) || (tNutzung.getSelectedColumn() == 9)
                                    || (tNutzung.getSelectedColumn() == 10)
                                    || (tNutzung.getSelectedColumn() == 0)
                                    || (tNutzung.getSelectedColumn() == 4)
                                    || (tNutzung.getSelectedColumn() == 11)))) {
                    jumpToPredecessorNutzung(nutzung);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  buchung  DOCUMENT ME!
     */
    private void jumpToPredecessorNutzung(final NutzungBuchungCustomBean buchung) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Versuche zu Vorgängernutzung zu springen: ");
        }
        if (tNutzung.getCellEditor() != null) {
            tNutzung.getCellEditor().cancelCellEditing();
        }
        NutzungBuchungCustomBean vorgaenger = null;
        if ((buchung != null) && (buchung.getNutzung() != null)
                    && ((vorgaenger = buchung.getNutzung().getPredecessorBuchung(buchung)) != null)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Vorgänger Nutzung gefunden");
            }
            selectNutzungInHistory(vorgaenger);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Es gibt keinen Vorgänger für die Nutzung: " + buchung.getId());
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  nutzung  DOCUMENT ME!
     */
    private void selectNutzungInHistory(final NutzungBuchungCustomBean nutzung) {
        // TODO richtigen Member identifiezieren
        jBand1.setSelectedMember((BandMemberSelectable)null);
        final int index = tableModel.getIndexOfCidsBean(nutzung);
        final int displayedIndex = ((JXTable)tNutzung).getFilters().convertRowIndexToView(index);
        if (index != -1) {
            tNutzung.getSelectionModel().clearSelection();
            tNutzung.getSelectionModel().addSelectionInterval(displayedIndex, displayedIndex);
            final Rectangle tmp = tNutzung.getCellRect(displayedIndex, 0, true);
            if (tmp != null) {
                tNutzung.scrollRectToVisible(tmp);
            }
        }
    }

    @Override
    public void updateFlurstueckForSaving(final FlurstueckCustomBean flurstueck) {
        final Collection<NutzungCustomBean> vNutzungen = flurstueck.getNutzungen();
        if (vNutzungen != null) {
            LagISUtils.makeCollectionContainSameAsOtherCollection(vNutzungen, tableModel.getAllNutzungen());
        } else { // TODO kann das überhaupt noch passieren seid der Umstellung auf cids ?!
            final HashSet newSet = new HashSet();
            newSet.addAll(tableModel.getAllNutzungen());
            flurstueck.setNutzungen(newSet);
        }
    }

    @Override
    public void tableChanged(final TableModelEvent e) {
        // check if selection is still valid
        if (tNutzung.getSelectedRow() != -1) {
            final int index = ((JXTable)tNutzung).convertRowIndexToModel(tNutzung.getSelectedRow());
            final NutzungBuchungCustomBean selectedBuchung = tableModel.getCidsBeanAtRow(index);
            if (selectedBuchung == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("selectedBuchung nicht länger verfügbar lösche selektierung");
                }
                tNutzung.clearSelection();
            }
        }
        if (LOG.isDebugEnabled()) {
            // TODO CHECK FOR REFACTORING
            LOG.debug("tableChanged");
        }
        final Refreshable refresh = LagisBroker.getInstance().getRefreshableByClass(NKFOverviewPanel.class);
        if (refresh != null) {
            refresh.refresh(new NutzungsContainer(tableModel.getAllNutzungen(), tableModel.getCurrentDate()));
        }
//        if (tableModel.getRowCount() != 0) {
//            log.debug("Rowcount ist: "+tableModel.getRowCount());
//            ((JXTable) tNutzung).packAll();
//        }
        if (tNutzung.getSelectedRow() != -1) {
            final int index = ((JXTable)tNutzung).convertRowIndexToModel(tNutzung.getSelectedRow());
            if (index != -1) {
                final NutzungBuchungCustomBean selectedBuchung = tableModel.getCidsBeanAtRow(index);
                if (selectedBuchung.getIstBuchwert() == true) {
                    btnFlipBuchung.setIcon(icoNotBooked);
                } else {
                    btnFlipBuchung.setIcon(icoBooked);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void memberUnselect() {
        boolean isSelected = false;
        int i;
        for (i = 0; i < bandNutzungen.getNumberOfMembers(); i++) {
            final NKFBandMember bm = (NKFBandMember)bandNutzungen.getMember(i);
            if (bm.isSelected()) {
                isSelected = true;
                break;
            }
        }
        if (!isSelected && (bandNutzungen.getNumberOfMembers() > 0)) {
            // select last one
            jBand1.setSelectedMember((NKFBandMember)bandNutzungen.getMember(i - 1));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bandMember  DOCUMENT ME!
     */
    public void memberSelect(final NKFBandMember bandMember) {
        try {
            jBand1.scrollToBandMember(bandMember);
            final Date date = bandMember.getDate();
            jComboBox1.removeAllItems();
            if (date != null) {
                lblCurrentHistoryPostion.setText(LagisBroker.getDateFormatter().format(date));

                final HashSet<Date> dates = new HashSet<Date>();
                for (final NutzungCustomBean nutzung_ : currentFlurstueck.getNutzungen()) {
                    dates.addAll(nutzung_.getDatesForDay(bandMember.getDate()));
                }

                for (final Date date_ : dates) {
                    jComboBox1.addItem(date_);
                }
//                tableModel.setModelToHistoryDate(dates.iterator().next());
                lblHistoricIcon.setIcon(icoHistoricIcon);
                if (isInEditMode) {
                    ((NKFTable)tNutzung).getAddAction().setEnabled(false);
                    btnRemoveNutzung.setEnabled(false);
                }
            } else {
                lblCurrentHistoryPostion.setText("Aktuelle Nutzungen");
                lblHistoricIcon.setIcon(icoHistoricIconDummy);

                jComboBox1.addItem(null);

//                tableModel.setModelToHistoryDate(null);
                if (isInEditMode) {
                    ((NKFTable)tNutzung).getAddAction().setEnabled(true);
                    // TODO WHY DOES THIS NOT WORK
                    // btnRemoveNutzung.setEnabled(wasRemovedEnabled);
                    btnRemoveNutzung.setEnabled(false);
                }
            }
            jComboBox1.setSelectedIndex(0);
            jComboBox1.setVisible(jComboBox1.getItemCount() > 1);
            lblCurrentHistoryPostion.setVisible(jComboBox1.getItemCount() <= 1);
        } catch (Exception ex) {
            LOG.error("Fehler beim updaten des Slider labels: ", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   from  DOCUMENT ME!
     * @param   to    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private int diffInDays(final Date from, final Date to) {
        return (int)((from.getTime() - to.getTime()) / (24 * 60 * 60 * 1000));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   sortedNutzungen  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private List<NutzungBuchungCustomBean> getSortedHistoricsortedNutzungen(
            final List<NutzungBuchungCustomBean> sortedNutzungen) {
        final ArrayList<NutzungBuchungCustomBean> sortedHistoricNutzungen = new ArrayList<NutzungBuchungCustomBean>();
        for (final NutzungBuchungCustomBean curBuchung : sortedNutzungen) {
            if (curBuchung.getGueltigbis() == null) {
                break;
            }
            sortedHistoricNutzungen.add(curBuchung);
        }
        return sortedHistoricNutzungen;
    }

    /**
     * ToDo refactor.
     */
    public synchronized void updateBands() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("update Slider", new CurrentStackTrace());
        }
        final ArrayList<NutzungBuchungCustomBean> sortedNutzungen = tableModel.getAllBuchungen();

        if (LOG.isDebugEnabled()) {
            LOG.debug("nach Änderungen");
        }
        try {
            bandNutzungen.removeAllMember();
            bandMonth.removeAllMember();

            for (int i = 0; i < bandNutzungen.getNumberOfMembers(); i++) {
                final NKFBandMember nkfMember = (NKFBandMember)bandNutzungen.getMember(i);
                nkfMember.removeAllListeners();
            }

            final List<NutzungBuchungCustomBean> sortedHistoricNutzungen = getSortedHistoricsortedNutzungen(
                    sortedNutzungen);

            if (!sortedHistoricNutzungen.isEmpty()) {
                final Iterator<NutzungBuchungCustomBean> it = sortedHistoricNutzungen.iterator();

                final Calendar c = Calendar.getInstance();
                final NutzungBuchungCustomBean firstNutzung = it.next();

                final int minPreDays = 5;
                final int minPostDays = 5;

                c.setTime(firstNutzung.getGueltigbis());
                c.set(Calendar.DAY_OF_MONTH, 1);
                int preOffset = diffInDays(firstNutzung.getGueltigbis(), c.getTime());
                if (preOffset < minPreDays) {
                    c.add(Calendar.MONTH, -1);
                    preOffset = diffInDays(firstNutzung.getGueltigbis(), c.getTime());
                }

                c.setTime(firstNutzung.getGueltigbis());
                c.add(Calendar.DATE, -preOffset);
                final Date firstDate = c.getTime();

                int previousDayDiff = preOffset;
                final NKFBandMember firstNkfBandMember = new NKFBandMember(
                        0,
                        previousDayDiff,
                        firstNutzung.getGueltigbis());
                bandNutzungen.addMember(firstNkfBandMember);
                firstNkfBandMember.addListener(new NKFBandMember.Listener() {

                        @Override
                        public void memberSelected(final boolean selected) {
                            if (selected) {
                                memberSelect(firstNkfBandMember);
                            } else {
                                SwingUtilities.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            memberUnselect();
                                        }
                                    });
                            }
                        }
                    });

                NutzungBuchungCustomBean previousNutzung = firstNutzung;
                while (it.hasNext()) {
                    final NutzungBuchungCustomBean curNutzung = it.next();
                    final Date curGueltigBis = LagisBroker.getDateWithoutTime(curNutzung.getGueltigbis());
                    final Date preGueltigBis = LagisBroker.getDateWithoutTime(
                            previousNutzung.getGueltigbis());
                    if (!curGueltigBis.equals(preGueltigBis)) {
                        final int curDayDiff = previousDayDiff + diffInDays(curGueltigBis, preGueltigBis);
                        final NKFBandMember nkfBandMember = new NKFBandMember(
                                previousDayDiff,
                                curDayDiff,
                                curGueltigBis);
                        nkfBandMember.addListener(new NKFBandMember.Listener() {

                                @Override
                                public void memberSelected(final boolean selected) {
                                    if (selected) {
                                        memberSelect(nkfBandMember);
                                    } else {
                                        SwingUtilities.invokeLater(new Runnable() {

                                                @Override
                                                public void run() {
                                                    memberUnselect();
                                                }
                                            });
                                    }
                                }
                            });
                        bandNutzungen.addMember(nkfBandMember);
                        previousNutzung = curNutzung;
                        previousDayDiff = curDayDiff;
                    }
                }
                final NutzungBuchungCustomBean lastNutzung = previousNutzung;

                c.setTime(lastNutzung.getGueltigbis());
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.add(Calendar.MONTH, 1);
                int postOffset = diffInDays(c.getTime(), lastNutzung.getGueltigbis());
                if (postOffset < minPostDays) {
                    c.add(Calendar.MONTH, 1);
                    postOffset = diffInDays(c.getTime(), lastNutzung.getGueltigbis());
                }

                final int endDayDiff = previousDayDiff + postOffset;
                final NKFBandMember bandMember = new NKFBandMember(
                        previousDayDiff,
                        endDayDiff,
                        null);
                bandMember.addListener(new NKFBandMember.Listener() {

                        @Override
                        public void memberSelected(final boolean selected) {
                            if (selected) {
                                memberSelect(bandMember);
                            } else {
                                SwingUtilities.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            memberUnselect();
                                        }
                                    });
                            }
                        }
                    });
                bandNutzungen.addMember(bandMember);

                // MONTHS
                Date previousDate = firstDate;
                previousDayDiff = 0;
                while (previousDayDiff < endDayDiff) {
                    c.setTime(previousDate);

                    final String prevDateMonth = c.getDisplayName(
                            Calendar.MONTH,
                            Calendar.LONG,
                            Locale.getDefault());
                    final String prevDateYear = Integer.toString(c.get(Calendar.YEAR));

                    c.set(Calendar.DAY_OF_MONTH, 1);
                    c.add(Calendar.MONTH, 1);
                    final Date curDate = c.getTime();

                    final int curDayDiff = previousDayDiff + diffInDays(curDate, previousDate);
                    bandMonth.addMember(new SimpleTextSection(
                            prevDateMonth
                                    + " "
                                    + prevDateYear,
                            previousDayDiff,
                            curDayDiff,
                            false,
                            false));

                    previousDate = curDate;
                    previousDayDiff = curDayDiff;
                }

//                            // YEARS
//                            previousDate = firstDate;
//                            previousDayDiff = 0;
//                            while (previousDayDiff < endDayDiff) {
//                                c.setTime(previousDate);
//
//                                final boolean firstOfYear = c.get(Calendar.DAY_OF_YEAR) == 1;
//
//                                final String curYear = Integer.toString(c.get(Calendar.YEAR));
//                                c.add(Calendar.YEAR, 1);
//                                c.set(Calendar.DAY_OF_YEAR, 1);
//                                final Date curDate = c.getTime();
//
//                                final int curDayDiff = previousDayDiff + diffInDays(curDate, previousDate);
//                                final boolean lastOfYear = curDayDiff < endDayDiff;
//                                bandYear.addMember(new SimpleTextSection(
//                                        curYear,
//                                        previousDayDiff,
//                                        (curDayDiff <= endDayDiff) ? curDayDiff : endDayDiff,
//                                        !firstOfYear,
//                                        !lastOfYear));
//
//                                previousDate = curDate;
//                                previousDayDiff = curDayDiff;
//                            }

                if (endDayDiff > 75) {
                    jBand1.setZoomFactor(endDayDiff / 75d);
                } else {
                    jBand1.setZoomFactor(1);
                }
                SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            memberUnselect();
                        }
                    });
                jBand1.setVisible(true);
            } else {
                lblCurrentHistoryPostion.setText("Keine Historie vorhanden");
                lblCurrentHistoryPostion.setVisible(true);
                jComboBox1.setVisible(false);
                jBand1.setVisible(false);
            }
            jBand1.setModel(jBand1.getModel());
        } catch (Exception ex) {
            LOG.error("Fehler beim updaten des NKF History Sliders (Change Filter)", ex);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("tablemodel rowcount: " + tableModel.getRowCount());
        }
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
        if (tNutzung.getSelectedRow() != -1) {
            final int index = ((JXTable)tNutzung).convertRowIndexToModel(tNutzung.getSelectedRow());
            // if(index != -1 && tableModel.getcurrentNutzungen().get(index).getId() == null && isInEditMode){
            if (index != -1) {
                final NutzungBuchungCustomBean selectedBuchung = tableModel.getCidsBeanAtRow(index);
                btnCopyNutzung.setEnabled(true);
                if (selectedBuchung.getIstBuchwert() == true) {
                    btnFlipBuchung.setIcon(icoNotBooked);
                } else {
                    btnFlipBuchung.setIcon(icoBooked);
                }
                if (isInEditMode) {
                    if (selectedBuchung.isBuchwertFlippable() && LagisBroker.getInstance().isNkfAdminPermission()) {
                        btnFlipBuchung.setEnabled(true);
                    } else {
                        btnFlipBuchung.setEnabled(false);
                    }
                    if (LagisBroker.getInstance().isNkfAdminPermission()) {
                        // enable the the Remove Button, if the chronologically last Buchung is selected. This Buchung
                        // can be the current Buchung or a historical Buchung.
                        if (selectedBuchung == selectedBuchung.getNutzung().getLastBuchung()) {
                            btnRemoveNutzung.setEnabled(true);
                        } else {
                            btnRemoveNutzung.setEnabled(false);
                        }
                    } else {
                        // enable the the Remove Button, if the current Buchung is selected
                        if (selectedBuchung.getGueltigbis() == null) {
                            btnRemoveNutzung.setEnabled(true);
                        } else {
                            btnRemoveNutzung.setEnabled(false);
                        }
                    }
                } else {
                    btnRemoveNutzung.setEnabled(false);
                    btnFlipBuchung.setEnabled(false);
                }
            } else {
                btnCopyNutzung.setEnabled(false);
                btnRemoveNutzung.setEnabled(false);
                btnFlipBuchung.setEnabled(false);
            }
        } else {
            btnRemoveNutzung.setEnabled(false);
            btnCopyNutzung.setEnabled(false);
            btnFlipBuchung.setEnabled(false);
        }
    }

    @Override
    public Icon getWidgetIcon() {
        return null;
    }

    // ToDo NKF die meldungen/überprüfungen müssen angepasst werden
    @Override
    public int getStatus() {
        if (isFlurstueckEditable) {
            if (tNutzung.getCellEditor() != null) {
                validationMessage = "Bitte vollenden Sie alle Änderungen bei den Nutzungen.";
                return Validatable.ERROR;
            }

            boolean existingUnvalidCurrentNutzung = false;
            boolean existsAtLeastOneValidCurrentNutzung = false;
//            boolean existingUnbookedDeletedNutzung = false;

            final ArrayList<NutzungCustomBean> currentNutzungen = tableModel.getAllNutzungen();
            final ArrayList<NutzungBuchungCustomBean> currentBuchungen = tableModel.getOpenBuchungen();

            if ((currentNutzungen != null) || (currentNutzungen.size() > 0)) {
                for (final NutzungBuchungCustomBean currentBuchung : currentBuchungen) {
                    if ((currentBuchung != null) && (currentBuchung.getNutzungsart() == null)) {
                        // return Validatable.VALID;
                        existingUnvalidCurrentNutzung = true;
                    }
                    if ((currentBuchung != null) && (currentBuchung.getNutzungsart() != null)) {
                        existsAtLeastOneValidCurrentNutzung = true;
                    }
                    if ((currentBuchung.getFlaeche() != null) && (currentBuchung.getQuadratmeterpreis() != null)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Neuer Preis: "
                                        + (currentBuchung.getFlaeche() * currentBuchung.getQuadratmeterpreis()));
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Neuer Preis kann nicht berechnet werden");
                        }
                    }
                }
            }
            if (existingUnvalidCurrentNutzung) {
                validationMessage = "Alle Nutzungen müssen eine Nutzungsart haben.";
                return Validatable.ERROR;
            } else if (!existsAtLeastOneValidCurrentNutzung && !LagisBroker.getInstance().isNkfAdminPermission()) {
                validationMessage =
                    "Es muss mindestens eine aktuelle Nutzung mit Nutzungsart angelegt sein,\num das Flurstück speichern zu können.";
                return Validatable.ERROR;
            } else {
                return Validatable.VALID;
            }
        } else {
            return Validatable.VALID;
        }
    }

    @Override
    public void configure(final Element parent) {
    }

    @Override
    public Element getConfiguration() {
        return null;
    }

    @Override
    public void masterConfigure(final Element parent) {
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class PopupListener extends MouseAdapter {

        //~ Methods ------------------------------------------------------------

        @Override
        public void mouseClicked(final MouseEvent e) {
            showPopup(e);
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            showPopup(e);
        }

        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
        private void showPopup(final MouseEvent e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("showPopup");
            }
            if (e.isPopupTrigger()) {
                if (LOG.isDebugEnabled()) {
                    // ToDo funktioniert nicht unter linux
                    LOG.debug("popup triggered");
                }
                final int rowAtPoint = tNutzung.rowAtPoint(new Point(e.getX(), e.getY()));
                NutzungBuchungCustomBean selectedNutzung = null;
                if ((rowAtPoint != -1)
                            && ((selectedNutzung = tableModel.getCidsBeanAtRow(
                                            ((JXTable)tNutzung).getFilters().convertRowIndexToModel(rowAtPoint)))
                                != null)
                            && (selectedNutzung.getNutzung() != null)
                            && (selectedNutzung.getNutzung().getPredecessorBuchung(selectedNutzung) != null)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("nutzung found");
                    }
                    currentPopupNutzung = selectedNutzung;
                    predecessorPopup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public class DateComboBoxRenderer extends DefaultListCellRenderer {

        //~ Instance fields ----------------------------------------------------

        private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value != null) {
                ((JLabel)comp).setText(sdf.format((Date)value));
            } else {
                ((JLabel)comp).setText("Aktuelle Nutzungen");
            }
            return comp;
        }
    }
}
