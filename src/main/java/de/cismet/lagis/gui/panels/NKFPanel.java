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

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableCellEditor;

import de.cismet.cids.custom.beans.lagis.*;

import de.cismet.lagis.Exception.BuchungNotInNutzungException;
import de.cismet.lagis.Exception.IllegalNutzungStateException;
import de.cismet.lagis.Exception.TerminateNutzungNotPossibleException;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.editor.EuroEditor;
import de.cismet.lagis.editor.FlaecheEditor;
import de.cismet.lagis.editor.PlanEditor;

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

import de.cismet.lagis.validation.Validatable;

import de.cismet.lagis.widget.AbstractWidget;

import de.cismet.tools.CurrentStackTrace;

import de.cismet.tools.configuration.Configurable;

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
    ChangeListener,
    ListSelectionListener,
    Configurable {

    //~ Static fields/initializers ---------------------------------------------

    private static final String WIDGET_NAME = "NKF Datenpanel";
    private static final String FIND_PREDECESSOR_MENU_NAME = "Vorgänger finden";
    private static final int YEAR_SCALE = 1;
    private static final int MONTH_SCALE = 2;
    private static final int DAY_SCALE = 3;

    //~ Instance fields --------------------------------------------------------

    // perhaps not good
    ArrayList<Date> dateToTicks;
    ArrayList<NutzungBuchungCustomBean> historicNutzungenDayClasses;
    boolean isOnlyHistoric = false;
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private FlurstueckCustomBean currentFlurstueck;
    private final NKFTableModel tableModel = new NKFTableModel();
    private boolean isInEditMode = false;
    private BackgroundUpdateThread<FlurstueckCustomBean> updateThread;
    private boolean isFlurstueckEditable = true;
    private Icon icoHistoricIcon = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/nutzung/history64.png"));
    private Icon icoHistoricIconDummy = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/nutzung/emptyDummy64.png"));
    private Icon icoBooked = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/nutzung/booked.png"));
    private Icon icoNotBooked = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/nutzung/notBooked.png"));
    private final ArrayList<NutzungCustomBean> copyPasteList = new ArrayList();
    private JPopupMenu predecessorPopup;
    private Date currentDate;
    private ArrayList<NutzungBuchungCustomBean> sortedNutzungen;
    private int counter = 0;
    private int mode;
    private Date first;
    private Date last;
    private NutzungBuchungCustomBean currentPopupNutzung = null;
    private int previously_sorted_column_index = 0;
    private SortOrder previously_used_sort_order = SortOrder.ASCENDING;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddNutzung;
    private javax.swing.JButton btnCopyNutzung;
    private javax.swing.JButton btnFlipBuchung;
    private javax.swing.JButton btnPasteNutzung;
    private javax.swing.JButton btnRemoveNutzung;
    private javax.swing.JCheckBox cbxChanges;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCurrentHistoryPostion;
    private javax.swing.JLabel lblHistoricIcon;
    private javax.swing.JSlider slrHistory;
    private javax.swing.JTable tNutzung;
    private javax.swing.JToggleButton tbtnSort;
    // End of variables declaration//GEN-END:variables
    // TODO nicht die Methode überschreiben sondern ein Feld in der Superklasse anlegen und dieses Feld in der erbenden
    // Klasse überschreiben

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form RechteTabellenPanel.
     */
    public NKFPanel() {
        setIsCoreWidget(true);
        initComponents();
        slrHistory.addChangeListener(this);
        slrHistory.setMajorTickSpacing(5);
        slrHistory.setMinorTickSpacing(1);
        slrHistory.setSnapToTicks(true);
        slrHistory.setPaintTicks(true);
        slrHistory.setEnabled(false);
        cbxChanges.setEnabled(false);
        btnRemoveNutzung.setEnabled(false);
        btnAddNutzung.setEnabled(false);
        btnFlipBuchung.setEnabled(false);
        configureTable();
        configBackgroundThread();
        configurePopupMenue();
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
                        final Collection<NutzungCustomBean> newNutzungen = getCurrentObject().getNutzungen();
                        tableModel.refreshTableModel(newNutzungen);
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        if (newNutzungen != null) {
                            if (log.isDebugEnabled()) {
                                log.debug("Es sind Nutzungen vorhanden: " + newNutzungen.size());
                            }
                        }
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        updateSlider();
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        LagisBroker.getInstance().flurstueckChangeFinished(NKFPanel.this);
                    } catch (Exception ex) {
                        log.error("Fehler im refresh thread: ", ex);
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
        if (log.isDebugEnabled()) {
            log.debug("ActionEvent: " + e.getActionCommand());
        }
        if (currentPopupNutzung != null) {
            if (log.isDebugEnabled()) {
                log.debug("currentPopupNutzung vorhanden");
            }
            jumpToPredecessorNutzung(currentPopupNutzung);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void configureTable() {
        tNutzung.setModel(tableModel);
        tNutzung.getSelectionModel().addListSelectionListener(this);
        tableModel.addTableModelListener(this);
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
                            final NutzungBuchungCustomBean n = tableModel.getBuchungAtRow(modelIndex);
                            // NO Geometry & more than one Verwaltungsbereich
                            return (n != null) && !n.getIstBuchwert();
                        } else {
                            return false;
                        }
                    } catch (Exception ex) {
                        log.error("Fehler beim Highlighting des Buchwerts vorhanden", ex);
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
                            final NutzungBuchungCustomBean n = tableModel.getBuchungAtRow(modelIndex);
                            // NO Geometry & more than one Verwaltungsbereich
                            return ((n != null) && n.getSollGeloeschtWerden());
                        } else {
                            return false;
                        }
                    } catch (Exception ex) {
                        log.error("Fehler beim Highlighting test wurde gelöscht vorhanden", ex);
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
            LagisBroker.getInstance().flurstueckChangeFinished(NKFPanel.this);
        }
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
        if (isFlurstueckEditable) {
            if (log.isDebugEnabled()) {
                log.debug("NKFPanel --> setComponentEditable");
            }
            isInEditMode = isEditable;
            tableModel.setIsInEditMode(isEditable);
            if (isEditable) {
                if (!slrHistory.isEnabled()) {
                    btnAddNutzung.setEnabled(true);
                } else if (slrHistory.getValue() == slrHistory.getMaximum()) {
                    btnAddNutzung.setEnabled(true);
                } else {
                    btnAddNutzung.setEnabled(false);
                }
                if (tNutzung.getSelectedRow() != -1) {
                    btnCopyNutzung.setEnabled(true);
                    final int index = ((JXTable)tNutzung).convertRowIndexToModel(tNutzung.getSelectedRow());
                    final NutzungBuchungCustomBean selectedBuchung = tableModel.getBuchungAtRow(index);

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
                btnAddNutzung.setEnabled(false);
                final TableCellEditor currentEditor = tNutzung.getCellEditor();
                if (currentEditor != null) {
                    currentEditor.cancelCellEditing();
                }
                btnRemoveNutzung.setEnabled(false);
                btnCopyNutzung.setEnabled(false);
            }
//        }
            if (log.isDebugEnabled()) {
                log.debug("NKFPanel --> setComponentEditable finished");
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Flurstück ist nicht städtisch Verwaltungen können nicht editiert werden");
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tNutzung = new JXTable();
        jLabel1 = new javax.swing.JLabel();
        btnAddNutzung = new javax.swing.JButton();
        btnRemoveNutzung = new javax.swing.JButton();
        slrHistory = new JSlider(JSlider.HORIZONTAL,0,15,15);
        jLabel2 = new javax.swing.JLabel();
        lblCurrentHistoryPostion = new javax.swing.JLabel();
        cbxChanges = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblHistoricIcon = new javax.swing.JLabel();
        btnPasteNutzung = new javax.swing.JButton();
        btnCopyNutzung = new javax.swing.JButton();
        btnFlipBuchung = new javax.swing.JButton();
        tbtnSort = new javax.swing.JToggleButton();

        tNutzung.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        tNutzung.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"21-510", "Straße", "k0211100", "Grünfläche,Grünfläche,Wohnbaufläche", "", "842", "1€", "842€"},
                {"21-740", "Gehölz", "k0211100", null, "", "2325", "1€", "2325"}
            },
            new String [] {
                "Nutzungsartenschlüssel", "Nutzungsart", "Anlageklasse", "Flächennutzungsplan", "Bebauungsplan", "Fläche m²", "Preis qm²", "Berechnung"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tNutzung);

        jLabel1.setText("Nutzungen:");

        btnAddNutzung.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/add.png"))); // NOI18N
        btnAddNutzung.setBorder(null);
        btnAddNutzung.setBorderPainted(false);
        btnAddNutzung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNutzungActionPerformed(evt);
            }
        });

        btnRemoveNutzung.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/remove.png"))); // NOI18N
        btnRemoveNutzung.setBorder(null);
        btnRemoveNutzung.setBorderPainted(false);
        btnRemoveNutzung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveNutzungActionPerformed(evt);
            }
        });

        slrHistory.setMaximum(0);

        jLabel2.setText("NKF Historie:");

        lblCurrentHistoryPostion.setText("Keine Historie vorhanden");

        cbxChanges.setText(" Nach Änderungen");
        cbxChanges.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbxChanges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxChangesActionPerformed(evt);
            }
        });

        jLabel4.setText("Filter");

        lblHistoricIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/nutzung/emptyDummy64.png"))); // NOI18N

        btnPasteNutzung.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/pasteNu.png"))); // NOI18N
        btnPasteNutzung.setToolTipText("Buchung einfügen");
        btnPasteNutzung.setBorderPainted(false);
        btnPasteNutzung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPasteNutzungActionPerformed(evt);
            }
        });

        btnCopyNutzung.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/copyNu.png"))); // NOI18N
        btnCopyNutzung.setToolTipText("Buchung kopieren");
        btnCopyNutzung.setBorderPainted(false);
        btnCopyNutzung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopyNutzungActionPerformed(evt);
            }
        });

        btnFlipBuchung.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/nutzung/booked.png"))); // NOI18N
        btnFlipBuchung.setToolTipText("Buchwert / kein Buchwert");
        btnFlipBuchung.setBorderPainted(false);
        btnFlipBuchung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFlipBuchungActionPerformed(evt);
            }
        });

        tbtnSort.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/sort.png"))); // NOI18N
        tbtnSort.setToolTipText("Sortierung An / Aus");
        tbtnSort.setBorderPainted(false);
        tbtnSort.setContentAreaFilled(false);
        tbtnSort.setMaximumSize(new java.awt.Dimension(56, 32));
        tbtnSort.setMinimumSize(new java.awt.Dimension(56, 32));
        tbtnSort.setPreferredSize(new java.awt.Dimension(56, 32));
        tbtnSort.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/sort_selected.png"))); // NOI18N
        tbtnSort.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tbtnSortItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(tbtnSort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnFlipBuchung, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnCopyNutzung, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnPasteNutzung, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnAddNutzung, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnRemoveNutzung, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, slrHistory, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(cbxChanges)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel5))
                            .add(layout.createSequentialGroup()
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(lblCurrentHistoryPostion))
                            .add(jLabel4))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 261, Short.MAX_VALUE)
                        .add(lblHistoricIcon)))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {btnAddNutzung, btnRemoveNutzung}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel1)
                        .add(btnAddNutzung, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(btnRemoveNutzung, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, btnPasteNutzung, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, btnCopyNutzung, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, btnFlipBuchung, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, tbtnSort, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(15, 15, 15)
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cbxChanges)
                            .add(jLabel5))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(lblCurrentHistoryPostion)))
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lblHistoricIcon)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(slrHistory, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {btnAddNutzung, btnRemoveNutzung}, org.jdesktop.layout.GroupLayout.VERTICAL);

    }// </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbxChangesActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxChangesActionPerformed
        updateSlider();
    }//GEN-LAST:event_cbxChangesActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveNutzungActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveNutzungActionPerformed
        if (log.isDebugEnabled()) {
            log.debug("Remove Nutzung");
        }
        final int currentRow = ((JXTable)tNutzung).getFilters().convertRowIndexToModel(tNutzung.getSelectedRow());
        if (currentRow != -1) {
            if (log.isDebugEnabled()) {
                log.debug("Selektierte Nutzung gefunden in Zeile: " + currentRow + "selectedRow: "
                            + tNutzung.getSelectedRow());
            }
            try {
                tableModel.removeNutzung(currentRow);
            } catch (TerminateNutzungNotPossibleException ex) {
                log.error("Eine Nutzung konnte nicht entfernt werden", ex);
                final int result = JOptionPane.showConfirmDialog(LagisBroker.getInstance().getParentComponent(),
                        "Die Buchung konnte nicht entfernt werden, bitte wenden Sie \n"
                                + "sich an den Systemadministrator",
                        "Fehler beim löschen einer Buchung",
                        JOptionPane.OK_OPTION);
            }
        }
    }//GEN-LAST:event_btnRemoveNutzungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddNutzungActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNutzungActionPerformed
        tbtnSort.setSelected(true);                                                   // this disables the sort of the
                                                                                      // table
        tableModel.addNutzung(NutzungCustomBean.createNew());
        log.info("New Nutzung added to Model");
    }//GEN-LAST:event_btnAddNutzungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCopyNutzungActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopyNutzungActionPerformed
        copyPasteList.clear();
        if (tNutzung.getSelectedRow() != -1) {
            final int[] selectedRows = tNutzung.getSelectedRows();
            for (int i = 0; i < selectedRows.length; i++) {
                tNutzung.getSelectedRow();
                final int index = ((JXTable)tNutzung).convertRowIndexToModel(selectedRows[i]);
                final NutzungBuchungCustomBean curNutzungToCopy = tableModel.getBuchungAtRow(index);
                if (curNutzungToCopy != null) {
                    try {
                        copyPasteList.add(NutzungCustomBean.createNew(curNutzungToCopy.cloneBuchung()));
                    } catch (Exception ex) {
                        log.error("Fehler beim kopieren einer Buchung: ", ex);
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
    }//GEN-LAST:event_btnCopyNutzungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnPasteNutzungActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasteNutzungActionPerformed
        if (copyPasteList.size() > 0) {
            NutzungCustomBean lastNutzung = null;
            for (final NutzungCustomBean curNutzung : copyPasteList) {
                tableModel.addNutzung(curNutzung);
                lastNutzung = curNutzung;
            }
            selectNutzungInHistory(lastNutzung.getNutzungsBuchungen().get(0));
        }
    }//GEN-LAST:event_btnPasteNutzungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFlipBuchungActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFlipBuchungActionPerformed
        if (log.isDebugEnabled()) {
            log.debug("Flippe Buchung");
        }
        final int index = ((JXTable)tNutzung).convertRowIndexToModel(tNutzung.getSelectedRow());
        if (index != -1) {
            final NutzungBuchungCustomBean selectedBuchung = tableModel.getBuchungAtRow(index);
            if (selectedBuchung.isBuchwertFlippable()) {
                try {
                    selectedBuchung.flipBuchungsBuchwert();
                    tableModel.fireTableDataChanged();
                    tNutzung.repaint();
                } catch (IllegalNutzungStateException ex) {
                    log.error("Buchwert kann nicht geflipped werden, Nutzung in illegalem Zustand: ", ex);
                } catch (BuchungNotInNutzungException ex) {
                    log.error(
                        "Buchwert kann nicht geflipped werden, Die Buchung ist nicht in der Nutzung vorhanden: ",
                        ex);
                }
            }
        } else {
            log.warn("Keine Buchung selektiert, sollte nicht möglich sein");
        }
    }//GEN-LAST:event_btnFlipBuchungActionPerformed
    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tbtnSortItemStateChanged(final java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tbtnSortItemStateChanged
        if (tbtnSort.isSelected()) {                                            // disable sort
            previously_sorted_column_index = ((JXTable)tNutzung).getSortedColumn().getModelIndex();
            previously_used_sort_order = ((JXTable)tNutzung).getSortOrder(previously_sorted_column_index);
            ((JXTable)tNutzung).setSortable(false);
        } else {                                                                // sort the table
            ((JXTable)tNutzung).setSortable(true);
            ((JXTable)tNutzung).setSortOrder(previously_sorted_column_index, previously_used_sort_order);
        }
    }//GEN-LAST:event_tbtnSortItemStateChanged

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
        if (log.isDebugEnabled()) {
            log.debug("MouseClicked");
        }
        // falls es NutzungCustomBean eine Stille Reserve besitzt zu der entsprechenden NutzungCustomBean springen
        if (source instanceof JXTable) {
            if (log.isDebugEnabled()) {
                log.debug("Mit maus auf NKFTabelle geklickt");
            }
            final int selecetdRow = tNutzung.getSelectedRow();
            if (selecetdRow != -1) {
                final NutzungBuchungCustomBean nutzung = tableModel.getBuchungAtRow(((JXTable)tNutzung)
                                .convertRowIndexToModel(
                                    selecetdRow));
                if (cbxChanges.isSelected() && (nutzung != null) && (e.getClickCount() == 2)
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
        if (log.isDebugEnabled()) {
            log.debug("Versuche zu Vorgängernutzung zu springen: ");
        }
        if (tNutzung.getCellEditor() != null) {
            tNutzung.getCellEditor().cancelCellEditing();
        }
        NutzungBuchungCustomBean vorgaenger = null;
        if ((buchung != null) && (buchung.getNutzung() != null)
                    && ((vorgaenger = buchung.getNutzung().getPredecessorBuchung(buchung)) != null)) {
            if (log.isDebugEnabled()) {
                log.debug("Vorgänger Nutzung gefunden");
            }
            selectNutzungInHistory(vorgaenger);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Es gibt keinen Vorgänger für die Nutzung: " + buchung.getId());
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  nutzung  DOCUMENT ME!
     */
    private void selectNutzungInHistory(final NutzungBuchungCustomBean nutzung) {
        final int tickToJump = getTickForNutzung(nutzung);
        if (tickToJump != -1) {
            if (log.isDebugEnabled()) {
                log.debug("Es wurde ein Tick gefunden zu dem gesprungen werden kann: " + tickToJump);
            }
            slrHistory.setValue(tickToJump);
            final int index = tableModel.getIndexOfBuchung(nutzung);
            if (log.isDebugEnabled()) {
                log.debug("index: " + index);
            }
            final int displayedIndex = ((JXTable)tNutzung).getFilters().convertRowIndexToView(index);
            if (index != -1) {
                if (log.isDebugEnabled()) {
                    log.debug("DisplayedIndex: " + displayedIndex);
                    log.debug("Tablemodel rowCount: " + tableModel.getRowCount());
                }
                tNutzung.getSelectionModel().clearSelection();
                tNutzung.getSelectionModel().addSelectionInterval(displayedIndex, displayedIndex);
                final Rectangle tmp = tNutzung.getCellRect(displayedIndex, 0, true);
                if (tmp != null) {
                    tNutzung.scrollRectToVisible(tmp);
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Keine passende Nutzung im TableModel gefunden");
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Kein Tick gefunden zu dem gesprungen werden kann");
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
            final NutzungBuchungCustomBean selectedBuchung = tableModel.getBuchungAtRow(index);
            if (selectedBuchung == null) {
                if (log.isDebugEnabled()) {
                    log.debug("selectedBuchung nicht länger verfügbar lösche selektierung");
                }
                tNutzung.clearSelection();
            }
        }
        if (log.isDebugEnabled()) {
            // TODO CHECK FOR REFACTORING
            log.debug("tableChanged");
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
                final NutzungBuchungCustomBean selectedBuchung = tableModel.getBuchungAtRow(index);
                if (selectedBuchung.getIstBuchwert() == true) {
                    btnFlipBuchung.setIcon(icoNotBooked);
                } else {
                    btnFlipBuchung.setIcon(icoBooked);
                }
            }
        }
    }

    // ToDo refactorn viel zu kompliziert??
    // ToDo SliderStateChanged
    // private boolean wasRemovedEnabled = false;
    @Override
    public void stateChanged(final ChangeEvent e) {
        if (cbxChanges.isSelected()) {
            if (historicNutzungenDayClasses != null) {
                try {
                    final JSlider source = (JSlider)e.getSource();
                    if (log.isDebugEnabled()) {
                        log.debug("Aktuelle Slider position: " + source.getValue());
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Counter: " + counter);
                    }
                    if (source.getValue() < counter) {
                        lblCurrentHistoryPostion.setText(LagisBroker.getDateFormatter().format(
                                historicNutzungenDayClasses.get(source.getValue()).getGueltigbis()));
                    } else {
                        lblCurrentHistoryPostion.setText("Aktuelle Nutzungen");
                    }
                    if (!source.getValueIsAdjusting()) {
                        if (source.getValue() < counter) {
                            tableModel.setModelToHistoryDate(historicNutzungenDayClasses.get(source.getValue())
                                        .getGueltigbis());
                            lblHistoricIcon.setIcon(icoHistoricIcon);
                            if (isInEditMode) {
                                btnAddNutzung.setEnabled(false);
                                // wasRemovedEnabled = btnRemoveNutzung.isEnabled();
                                btnRemoveNutzung.setEnabled(false);
                            }
                        } else {
                            lblHistoricIcon.setIcon(icoHistoricIconDummy);
                            tableModel.setModelToHistoryDate(null);
                            if (isInEditMode) {
                                btnAddNutzung.setEnabled(true);
                                // TODO WHY DOES THIS NOT WORK
                                // btnRemoveNutzung.setEnabled(wasRemovedEnabled);
                                btnRemoveNutzung.setEnabled(false);
                            }
                        }
                    }
                } catch (Exception ex) {
                    log.error("Fehler beim updaten des Slider labels: ", ex);
                }
            }
        } else {
            final JSlider source = (JSlider)e.getSource();
            if (log.isDebugEnabled()) {
                log.debug("Aktuelle Slider position: " + source.getValue());
            }
            final int currentValue = source.getValue();
            final GregorianCalendar calender = new GregorianCalendar();
            if (currentValue == slrHistory.getMaximum()) {
                lblCurrentHistoryPostion.setText("Aktuelle Nutzungen");
                currentDate = last;
            } else if (currentValue == (slrHistory.getMaximum() - 1)) {
                lblCurrentHistoryPostion.setText(LagisBroker.getDateFormatter().format(last));
                currentDate = last;
            } else if (mode == DAY_SCALE) {
                calender.setTime(first);
                // TRY DAYS
                calender.add(calender.HOUR, currentValue * 24);
                currentDate = calender.getTime();
                lblCurrentHistoryPostion.setText(LagisBroker.getDateFormatter().format(currentDate));
            } else if (mode == MONTH_SCALE) {
                calender.setTime(first);
                calender.add(calender.MONTH, currentValue);
                currentDate = calender.getTime();
                lblCurrentHistoryPostion.setText(LagisBroker.getDateFormatter().format(currentDate));
            } else if (mode == YEAR_SCALE) {
                calender.setTime(first);
                calender.add(calender.YEAR, currentValue);
                currentDate = calender.getTime();
                lblCurrentHistoryPostion.setText(LagisBroker.getDateFormatter().format(currentDate));
            }

            if (!source.getValueIsAdjusting()) {
                if (currentDate != null) {
                    if (currentValue == slrHistory.getMaximum()) {
                        tableModel.setModelToHistoryDate(null);
                        lblHistoricIcon.setIcon(icoHistoricIconDummy);
                        if (isInEditMode) {
                            btnAddNutzung.setEnabled(true);
                            // TODO WHY DOES THIS NOT WORK
                            // btnRemoveNutzung.setEnabled(wasRemovedEnabled);
                            btnRemoveNutzung.setEnabled(false);
                        }
                        return;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("currentDate: " + currentDate);
                        log.debug("lastDate: " + last);
                    }
                    final Date tmpDate = LagisBroker.getDateWithoutTime(last);
                    final Date tmpDate2 = LagisBroker.getDateWithoutTime(currentDate);
                    if (!tmpDate.equals(tmpDate2)) {
                        if (log.isDebugEnabled()) {
                            log.debug("current == last");
                        }
                        tableModel.setModelToHistoryDate(currentDate);
                        // TODO THIS CAUSE IS IMPOSSIBLE BECAUSE NO EDIT MODE FOR HISTORIC FLURstück
                        if (isOnlyHistoric) {
                            lblHistoricIcon.setIcon(icoHistoricIcon);
                            btnAddNutzung.setEnabled(false);
                            btnRemoveNutzung.setEnabled(false);
                        } else {
                            lblHistoricIcon.setIcon(icoHistoricIcon);
                            if (isInEditMode) {
                                btnAddNutzung.setEnabled(false);
                                // wasRemovedEnabled = btnRemoveNutzung.isEnabled();
                                btnRemoveNutzung.setEnabled(false);
                            }
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("current != last");
                        }
                        if (!isOnlyHistoric) {
                            if (log.isDebugEnabled()) {
                                log.debug("nicht nur historische");
                            }
                            tableModel.setModelToHistoryDate(last);
                            lblHistoricIcon.setIcon(icoHistoricIconDummy);
                            if (isInEditMode) {
                                btnAddNutzung.setEnabled(true);
                                // TODO WHY DOES THIS NOT WORK
                                // btnRemoveNutzung.setEnabled(wasRemovedEnabled);
                                btnRemoveNutzung.setEnabled(false);
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("nur historische");
                            }
                            tableModel.setModelToHistoryDate(currentDate);
                            lblHistoricIcon.setIcon(icoHistoricIcon);
                            if (isInEditMode) {
                                btnAddNutzung.setEnabled(false);
                                btnRemoveNutzung.setEnabled(false);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * ToDo refactor.
     */
    public synchronized void updateSlider() {
        if (log.isDebugEnabled()) {
            log.debug("update Slider", new CurrentStackTrace());
        }
        if (cbxChanges.isSelected()) {
            if (log.isDebugEnabled()) {
                log.debug("nach Änderungen");
            }
            try {
                slrHistory.setSnapToTicks(true);
                slrHistory.setMajorTickSpacing(5);
                slrHistory.setMinorTickSpacing(1);
                sortedNutzungen = tableModel.getAllBuchungen();
                final ArrayList<NutzungBuchungCustomBean> sortedHistoricNutzungen =
                    new ArrayList<NutzungBuchungCustomBean>();
                if (sortedNutzungen != null) {
                    // sortedNutzungen = (Vector) tableModel.getAllNutzungen().clone();
                    counter = 0;
                    if (sortedNutzungen.size() >= 1) {
                        for (final NutzungBuchungCustomBean curBuchung : sortedNutzungen) {
                            if (curBuchung.getGueltigbis() == null) {
                                break;
                            }
                            sortedHistoricNutzungen.add(curBuchung);
                            counter++;
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("Anzahl historischer NKF Einträge: " + counter);
                        }
                        if (counter != 0) {
                            if (counter > 1) {
                                historicNutzungenDayClasses = new ArrayList<NutzungBuchungCustomBean>();
                                dateToTicks = new ArrayList<Date>();
                                counter = 0;
                                final Iterator<NutzungBuchungCustomBean> it = sortedHistoricNutzungen.iterator();
                                // TODO what if exactly one element is historic;
                                NutzungBuchungCustomBean nutzungToTest = it.next();
                                // boolean lastAreEquals = true;
                                // NutzungCustomBean curNutzung = null;
                                while (it.hasNext()) {
                                    final NutzungBuchungCustomBean curNutzung = it.next();
                                    final Date curGueltigBis = LagisBroker.getDateWithoutTime(
                                            curNutzung.getGueltigbis());
                                    final Date gueltigBisToTest = LagisBroker.getDateWithoutTime(
                                            nutzungToTest.getGueltigbis());
                                    if (log.isDebugEnabled()) {
                                        log.debug("aktuell zu testende historische Nutzung: " + curGueltigBis
                                                    + " millis: " + curGueltigBis.getTime());
                                    }
                                    if (log.isDebugEnabled()) {
                                        log.debug("test historische Nutzung: " + gueltigBisToTest + " millis: "
                                                    + gueltigBisToTest.getTime());
                                    }
                                    if (log.isDebugEnabled()) {
                                        log.debug("Sind Nutzungen am gleichen Tag?: "
                                                    + curGueltigBis.equals(gueltigBisToTest));
                                    }
                                    if (!curGueltigBis.equals(gueltigBisToTest)) {
                                        if (log.isDebugEnabled()) {
                                            log.debug("Nutzungen sind nicht gleichen Tag");
                                        }
                                        counter++;
                                        historicNutzungenDayClasses.add(nutzungToTest);
                                        dateToTicks.add(gueltigBisToTest);
                                        nutzungToTest = curNutzung;
                                    }
                                    // if(curNutzung.getGueltigbis(). )
                                }
                                historicNutzungenDayClasses.add(nutzungToTest);
                                dateToTicks.add(LagisBroker.getDateWithoutTime(nutzungToTest.getGueltigbis()));
                                counter++;
                            } else {
                                dateToTicks = new ArrayList<Date>();
                                historicNutzungenDayClasses = sortedHistoricNutzungen;
                                dateToTicks.add(LagisBroker.getDateWithoutTime(
                                        sortedHistoricNutzungen.get(0).getGueltigbis()));
                            }
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("Anzahl historischer NKF Einträge an unterschiedlichen Tagen: " + counter);
                        }
                        // counter++;
                        // log.debug("Anzahl Historischer NKF Einträge: "+counter);
                        if (counter == 0) {
                            slrHistory.setMinimum(0);
                            slrHistory.setMaximum(0);
                            slrHistory.setEnabled(false);
                            cbxChanges.setEnabled(false);
                            lblCurrentHistoryPostion.setText("Keine Historie vorhanden");
                            sortedNutzungen = null;
                            historicNutzungenDayClasses = null;
                            dateToTicks = null;
                        } else {
                            slrHistory.setMinimum(0);
                            // TODO WARUM?
                            slrHistory.setMaximum(counter);
//                            if (setToLatestHistoric) {
//                                slrHistory.setValue(counter - 1);
//                            } else {
                            slrHistory.setValue(counter);
//                            }
                            slrHistory.setEnabled(true);
                            cbxChanges.setEnabled(true);
                        }
                    } else {
                        slrHistory.setMinimum(0);
                        slrHistory.setMaximum(0);
                        slrHistory.setEnabled(false);
                        cbxChanges.setEnabled(false);
                        lblCurrentHistoryPostion.setText("Keine Historie vorhanden");
                        sortedNutzungen = null;
                        historicNutzungenDayClasses = null;
                        dateToTicks = null;
                    }
                } else {
                    slrHistory.setMinimum(0);
                    slrHistory.setMaximum(0);
                    slrHistory.setEnabled(false);
                    cbxChanges.setEnabled(false);
                    lblCurrentHistoryPostion.setText("Keine Historie vorhanden");
                    sortedNutzungen = null;
                    historicNutzungenDayClasses = null;
                    dateToTicks = null;
                }
            } catch (Exception ex) {
                log.error("Fehler beim updaten des NKF History Sliders (Change Filter)", ex);
            }
        } else {
//            log.debug("nach zeitstrahl. Set to last historic: " + setToLatestHistoric);
            try {
                isOnlyHistoric = false;
                slrHistory.setSnapToTicks(false);
                sortedNutzungen = (ArrayList)tableModel.getAllBuchungen();
                final ArrayList<NutzungBuchungCustomBean> sortedHistoricNutzungen =
                    new ArrayList<NutzungBuchungCustomBean>();
                first = null;
                last = null;
                if (sortedNutzungen != null) {
                    counter = 0;
                    if (sortedNutzungen.size() >= 1) {
                        // ToDO NKF Comparator
                        Collections.sort(sortedNutzungen, NutzungBuchungCustomBean.DATE_COMPARATOR);
                        final Iterator<NutzungBuchungCustomBean> it = sortedNutzungen.iterator();
                        while (it.hasNext()) {
                            final NutzungBuchungCustomBean curNutzung = it.next();
                            // log.debug("current NutzungCustomBean gueltigBis: "+curNutzung.getGueltigbis());
                            // curNutzung.getGueltigbis().getTime();
                            if (curNutzung.getGueltigbis() == null) {
                                break;
                            }
                            counter++;
                            sortedHistoricNutzungen.add(curNutzung);
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("Anzahl historischer NKF Einträge: " + counter);
                        }
                        if (counter != 0) {
                            first = sortedHistoricNutzungen.get(0).getGueltigvon();
                            last = sortedHistoricNutzungen.get(sortedHistoricNutzungen.size() - 1).getGueltigbis();
                            if (log.isDebugEnabled()) {
                                log.debug("last in millis: " + last.getTime());
                            }
                            if (log.isDebugEnabled()) {
                                log.debug("first: " + first + " last: " + last);
                            }
                            // TODO OPTIMIZE for example over isHistoric
                            long between;
                            if (sortedNutzungen.size() == sortedHistoricNutzungen.size()) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Nur historische Nutzungen");
                                }
                                between = last.getTime() - first.getTime();
                                isOnlyHistoric = true;
                            } else {
                                last = new Date();
                                if (log.isDebugEnabled()) {
                                    log.debug("Last gets new time: " + last.getTime());
                                }
                                between = last.getTime() - first.getTime();
                            }
                            if (log.isDebugEnabled()) {
                                // TODO ROUND
                                log.debug("Millisekunden zwischen den Nutzungen: " + between);
                            }
                            final int days = (int)(Math.round(between / 1000 / 60 / 60 / 24));
                            if (log.isDebugEnabled()) {
                                log.debug("Tage zwischen den Nutzungen: " + days);
                            }
                            final int months = (int)(Math.round(between / 1000 / 60 / 60 / 24 / 30));
                            if (log.isDebugEnabled()) {
                                log.debug("Monate zwischen den Nutzungen: " + months);
                            }
                            final int years = (int)(Math.round(between / 1000 / 60 / 60 / 24 / 30 / 12));
                            if (log.isDebugEnabled()) {
                                log.debug("Jahre zwischen den Nutzungen: " + years);
                            }

                            if (days > 365) {
                                if (months > 360) {
                                    slrHistory.setMinimum(0);
                                    slrHistory.setMaximum(years + 1);
                                    slrHistory.setMinorTickSpacing(5);
                                    slrHistory.setMajorTickSpacing(10);
                                    mode = YEAR_SCALE;
//                                    if (setToLatestHistoric) {
//                                        slrHistory.setValue(years);
//                                    } else {
                                    slrHistory.setValue(years + 1);
//                                    }
                                    slrHistory.setEnabled(true);
                                    cbxChanges.setEnabled(true);
                                } else {
                                    slrHistory.setMinimum(0);
                                    slrHistory.setMinorTickSpacing(12);
                                    slrHistory.setMaximum(months + 1);
                                    mode = MONTH_SCALE;
//                                    if (setToLatestHistoric) {
//                                        slrHistory.setValue(months);
//                                    } else {
                                    slrHistory.setValue(months + 1);
//                                    }
                                    slrHistory.setEnabled(true);
                                    cbxChanges.setEnabled(true);
                                }
                            } else {
                                slrHistory.setMinimum(0);
                                slrHistory.setMinorTickSpacing(30);
                                slrHistory.setMaximum(days + 1);
                                mode = DAY_SCALE;
//                                if (setToLatestHistoric) {
//                                    slrHistory.setValue(days);
//                                } else {
                                slrHistory.setValue(days + 1);
//                                }
                                slrHistory.setEnabled(true);
                                cbxChanges.setEnabled(true);
                            }
                        } else {
                            slrHistory.setMinimum(0);
                            slrHistory.setMaximum(0);
                            slrHistory.setEnabled(false);
                            cbxChanges.setEnabled(false);
                            lblCurrentHistoryPostion.setText("Keine Historie vorhanden");
                            sortedNutzungen = null;
                            historicNutzungenDayClasses = null;
                            dateToTicks = null;
                        }
                    } else {
                        // TODO NO HISTORY FUNCTION OR RETURN DUPLICATE CODE !!!
                        slrHistory.setMinimum(0);
                        slrHistory.setMaximum(0);
                        slrHistory.setEnabled(false);
                        cbxChanges.setEnabled(false);
                        lblCurrentHistoryPostion.setText("Keine Historie vorhanden");
                        sortedNutzungen = null;
                        historicNutzungenDayClasses = null;
                        dateToTicks = null;
                    }
                } else {
                    slrHistory.setMinimum(0);
                    slrHistory.setMaximum(0);
                    slrHistory.setEnabled(false);
                    cbxChanges.setEnabled(false);
                    lblCurrentHistoryPostion.setText("Keine Historie vorhanden");
                    sortedNutzungen = null;
                }
            } catch (Exception ex) {
                log.error("Fehler beim updaten des NKF History Sliders (Date Filter)", ex);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("tablemodel rowcount: " + tableModel.getRowCount());
        }
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
        if (tNutzung.getSelectedRow() != -1) {
            final int index = ((JXTable)tNutzung).convertRowIndexToModel(tNutzung.getSelectedRow());
            // if(index != -1 && tableModel.getcurrentNutzungen().get(index).getId() == null && isInEditMode){
            if (index != -1) {
                final NutzungBuchungCustomBean selectedBuchung = tableModel.getBuchungAtRow(index);
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
                    if (selectedBuchung.getGueltigbis() == null) {
                        btnRemoveNutzung.setEnabled(true);
                    } else {
                        btnRemoveNutzung.setEnabled(false);
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

    /**
     * DOCUMENT ME!
     *
     * @param   current  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private int getTickForNutzung(final NutzungBuchungCustomBean current) {
        if (cbxChanges.isSelected()) {
            if (log.isDebugEnabled()) {
                log.debug("Tick wird für Buchung: " + current.getId() + " im Änderungsmodus ermittelt");
            }
            if (log.isDebugEnabled()) {
                log.debug("dateToTicks: " + dateToTicks);
            }
            return dateToTicks.indexOf(LagisBroker.getDateWithoutTime(current.getGueltigbis()));
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Tick wird für Buchung: " + current.getId() + " im Zeitstrahlmodus ermittelt");
            }
            if (current.getGueltigbis() == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Buchung ist nicht historisch, springe zu aktuellen Buchungen");
                }
                return slrHistory.getMaximum();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Buchung ist historisch");
                }
                final long between = current.getGueltigbis().getTime() - first.getTime();
                if (log.isDebugEnabled()) {
                    log.debug("Zeit zwischen erster Buchung und ausgewählter: " + between);
                }
                if (mode == DAY_SCALE) {
                    if (log.isDebugEnabled()) {
                        log.debug("DayScale");
                    }
                    return (int)(Math.round(between / 1000 / 60 / 60 / 24));
                } else if (mode == MONTH_SCALE) {
                    if (log.isDebugEnabled()) {
                        log.debug("MonthScale");
                    }
                    return (int)(Math.round(between / 1000 / 60 / 60 / 24 / 30));
                } else if (mode == YEAR_SCALE) {
                    if (log.isDebugEnabled()) {
                        log.debug("YearScale");
                    }
                    return (int)(Math.round(between / 1000 / 60 / 60 / 24 / 30 / 12));
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Unknown Scale");
            }
            return -1;
        }
    }

    // TODO USE
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

            final boolean existingBufferDisolves = false;
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
                        if (log.isDebugEnabled()) {
                            log.debug("Neuer Preis: "
                                        + (currentBuchung.getFlaeche() * currentBuchung.getQuadratmeterpreis()));
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Neuer Preis kann nicht berechnet werden");
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
        try {
            if (log.isDebugEnabled()) {
                log.debug("configure NKFPanel");
            }
            cbxChanges.setSelected(Boolean.valueOf(
                    parent.getChild("nkfConfiguration").getAttributeValue("displayedByChanges")));
        } catch (Exception ex) {
            log.warn("Fehler beim konfigurieren des NKFPanels: ", ex);
            cbxChanges.setSelected(true);
        }
    }

    @Override
    public Element getConfiguration() {
        final Element ret = new Element("nkfConfiguration");
        ret.setAttribute("displayedByChanges", String.valueOf(cbxChanges.isSelected()));
        return ret;
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
            if (log.isDebugEnabled()) {
                log.debug("showPopup");
            }
            if (e.isPopupTrigger()) {
                if (log.isDebugEnabled()) {
                    // ToDo funktioniert nicht unter linux
                    log.debug("popup triggered");
                }
                final int rowAtPoint = tNutzung.rowAtPoint(new Point(e.getX(), e.getY()));
                NutzungBuchungCustomBean selectedNutzung = null;
                if ((rowAtPoint != -1)
                            && ((selectedNutzung = tableModel.getBuchungAtRow(
                                            ((JXTable)tNutzung).getFilters().convertRowIndexToModel(rowAtPoint)))
                                != null)
                            && (selectedNutzung.getNutzung() != null)
                            && (selectedNutzung.getNutzung().getPredecessorBuchung(selectedNutzung) != null)) {
                    if (log.isDebugEnabled()) {
                        log.debug("nutzung found");
                    }
                    currentPopupNutzung = selectedNutzung;
                    predecessorPopup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }
    }
}
