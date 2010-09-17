/*
 * RechteTabellenPanel.java
 *
 * Created on 16. März 2007, 12:02
 */
package de.cismet.lagis.gui.panels;

import de.cismet.lagis.broker.EJBroker;
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
import de.cismet.lagis.util.NutzungsContainer;
import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.widget.AbstractWidget;
import de.cismet.lagisEE.bean.Exception.IllegalNutzungStateException;
import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.lagisEE.entity.core.Nutzung;
import de.cismet.lagisEE.entity.core.NutzungsBuchung;
import de.cismet.lagisEE.entity.core.hardwired.Anlageklasse;
import de.cismet.lagisEE.entity.core.hardwired.FlurstueckArt;
import de.cismet.lagisEE.entity.core.hardwired.Nutzungsart;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.SortOrder;
import org.openide.util.Exceptions;

/**
 *
 * @author  Puhl
 */
public class NKFPanel extends AbstractWidget implements MouseListener, FlurstueckChangeListener, FlurstueckSaver, TableModelListener, ChangeListener, ListSelectionListener {

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Flurstueck currentFlurstueck;
    private static final String WIDGET_NAME = "NKF Datenpanel";
    private final NKFTableModel tableModel = new NKFTableModel();
    private boolean isInEditMode = false;
    private BackgroundUpdateThread<Flurstueck> updateThread;
    private boolean isFlurstueckEditable = true;
    private Icon icoHistoricIcon = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/nutzung/history64.png"));
    private Icon icoHistoricIconDummy = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/nutzung/emptyDummy64.png"));
    private final ArrayList<Nutzung> copyPasteList = new ArrayList();

    /** Creates new form RechteTabellenPanel */
    public NKFPanel() {
        setIsCoreWidget(true);
        initComponents();
        //slrHistory.putClientProperty("JSlider.isFilled",Boolean.TRUE);
        slrHistory.addChangeListener(this);
        slrHistory.setMajorTickSpacing(5);
        slrHistory.setMinorTickSpacing(1);
        slrHistory.setSnapToTicks(true);
        slrHistory.setPaintTicks(true);
        slrHistory.setEnabled(false);
        cbxChanges.setEnabled(false);
        btnRemoveNutzung.setEnabled(false);
        btnAddNutzung.setEnabled(false);
        configureTable();
        configBackgroundThread();
        configurePopupMenue();
    }

    private void configBackgroundThread() {
        updateThread = new BackgroundUpdateThread<Flurstueck>() {

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
                    FlurstueckArt flurstueckArt = getCurrentObject().getFlurstueckSchluessel().getFlurstueckArt();
                    if (flurstueckArt != null && flurstueckArt.getBezeichnung().equals(FlurstueckArt.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                        log.debug("Flurstück ist städtisch und kann editiert werden");
                        isFlurstueckEditable = true;
                    } else {
                        log.debug("Flurstück ist nicht städtisch und kann nicht editiert werden");
                        isFlurstueckEditable = false;
                    }
                    final Set<Nutzung> newNutzungen = getCurrentObject().getNutzungen();
//                    printNutzungen(newNutzungen);
                    tableModel.refreshTableModel(newNutzungen);
                    if (isUpdateAvailable()) {
                        cleanup();
                        return;
                    }
                    if (newNutzungen != null) {
                        log.debug("Es sind Nutzungen vorhanden: " + newNutzungen.size());
//                        Iterator<Nutzung> it = newNutzungen.iterator();
//                        while (it.hasNext()) {
//                            if (isUpdateAvailable()) {
//                                cleanup();
//                                return;
//                            }
////                            Nutzung curNutzung = it.next();
////                            log.debug("Nutzung: " + curNutzung.getId());
////                            log.debug("Gueltig von: " + curNutzung.getGueltigvon());
////                            log.debug("Gueltig bis: " + curNutzung.getGueltigbis());
////                            log.debug("Quadratmeterpreis: " + curNutzung.getQuadratmeterpreis());
////                            log.debug("Fläche: " + curNutzung.getFlaeche());
//                        }
                    }
                    if (isUpdateAvailable()) {
                        cleanup();
                        return;
                    }                    
                    updateSlider(false);
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

            protected void cleanup() {
            }
//            private void printNutzungen(Set<Nutzung> newNutzungen) {
//                if (newNutzungen != null){
//                    for(Nutzung nutz:newNutzungen){
//                        if(nutz != null && nutz.getGueltigbis() == null){
//                             printNutzungRecursive(nutz);
//                        } else {
//                            log.debug("NKFImpl: Nutzung entweder Null oder Historisch --> ignoriert");
//                        }
//                    }
//                } else {
//                    log.debug("NKFImpl: Nutzungen sind leer");
//                }
//            }
//
//            private void printNutzungRecursive(NutzungsBuchung nutzung){
//                if(nutzung != null){
//                   if(nutzung.getVorgaenger_todo() != null){
//                       log.debug("NKFImpl: Rekursion: "+nutzung.getVorgaenger_todo());
//                       printNutzungRecursive(nutzung.getVorgaenger_todo());
//                   } else {
//                       log.debug("NKFImpl: Nutzung hat keinen Vorgänger mehr.");
//                   }
//                   log.debug("NKFImpl: Nutzung: "+nutzung);
//                } else {
//                    log.debug("NKFImpl: Nutzung ist null");
//                }
//            }
        };
        updateThread.setPriority(Thread.NORM_PRIORITY);
        updateThread.start();
    }
    private JPopupMenu predecessorPopup;
    private static final String FIND_PREDECESSOR_MENU_NAME = "Vorgänger finden";

    private void configurePopupMenue() {
        predecessorPopup = new JPopupMenu();
        JMenuItem findPredecessor = new JMenuItem(FIND_PREDECESSOR_MENU_NAME);
        findPredecessor.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                findPredecessorForNutzung(e);
            }
        });
        predecessorPopup.add(findPredecessor);
    }

    private void findPredecessorForNutzung(ActionEvent e) {
        log.debug("ActionEvent: " + e.getActionCommand());
        if (currentPopupNutzung != null) {
            log.debug("currentPopupNutzung vorhanden");
            jumpToPredecessorNutzung(currentPopupNutzung);
        }
    }

    private void configureTable() {
        tNutzung.setModel(tableModel);
        tNutzung.getSelectionModel().addListSelectionListener(this);
        tableModel.addTableModelListener(this);
        //TODO NUllSAVe
        //tableModel.setVerwaltendenDienstellenList(allVerwaltendeDienstellen);
        //bleModel.setVerwaltungsGebrauchList(allVerwaltungsgebraeuche);
        //.setModel();
        final JComboBox cboAK = new JComboBox(new Vector<Anlageklasse>(EJBroker.getInstance().getAllAnlageklassen()));
//        //tNutzung.setDefaultRenderer(Verwaltungsgebrauch.class,new VerwaltungsgebrauchRenderer());
//        tNutzung.setDefaultEditor(VerwaltendeDienststelle.class,new DefaultCellEditor(cboVD));
//        JComboBox cboVG = new JComboBox(new Vector<Verwaltungsgebrauch>(allVerwaltungsgebraeuche));
//        //JComboBox cboVG = new JComboBox(new Vector<Verwaltungsgebrauch>(allVerwaltungsgebraeuche));
//        cboVG.setEditable(true);
//        ComboBoxCellEditor cellEditor = new ComboBoxCellEditor(cboVG);
//        //AutoCompleteDecorator.decorate(cboVG);
        tNutzung.setDefaultEditor(Anlageklasse.class, new DefaultCellEditor(cboAK));
        tNutzung.setDefaultRenderer(Integer.class, new FlaecheRenderer());
        tNutzung.setDefaultEditor(Integer.class, new FlaecheEditor());
        final Vector<Nutzungsart> nutzungsarten = new Vector<Nutzungsart>(EJBroker.getInstance().getAllNutzungsarten());
        Collections.sort(nutzungsarten);
        JComboBox cboNA = new JComboBox(nutzungsarten);
//        tNutzung.getColumnModel().getColumn(1).setCellEditor(new ComboBoxCellEditor(cboVG));
        cboNA.setEditable(true);
        tNutzung.setDefaultEditor(Nutzungsart.class, new ComboBoxCellEditor(cboNA));
        tNutzung.setDefaultEditor(Vector.class, new PlanEditor());
        tNutzung.setDefaultRenderer(Vector.class, new PlanRenderer());
        tNutzung.setDefaultEditor(Double.class, new EuroEditor());
        tNutzung.setDefaultRenderer(Double.class, new EuroRenderer());
        tNutzung.addMouseListener(this);
        tNutzung.addMouseListener(new PopupListener());
        tableModel.addTableModelListener(this);
        //(LagisBroker.UNKOWN_COLOR, null, 0, -1)
        HighlightPredicate buchungsStatusPredicate = new HighlightPredicate() {

            public boolean isHighlighted(Component renderer, ComponentAdapter componentAdapter) {
                try {
                    int displayedIndex = componentAdapter.row;
                    int modelIndex = ((JXTable) tNutzung).getFilters().convertRowIndexToModel(displayedIndex);
                    NutzungsBuchung n = tableModel.getNutzungAtRow(modelIndex);
                    //NO Geometry & more than one Verwaltungsbereich                    
                    return (n != null && n.getIstBuchwert() == null);
                } catch (Exception ex) {
                    log.error("Fehler beim Highlighting des Buchwerts vorhanden", ex);
                    return false;
                }
            }
        };

        Highlighter buchungsStatusHighlighter = new ColorHighlighter(buchungsStatusPredicate, LagisBroker.UNKOWN_COLOR, null);
        //(LagisBroker.grey, null, 0, -1)
        HighlightPredicate geloeschtPredicate = new HighlightPredicate() {

            public boolean isHighlighted(Component renderer, ComponentAdapter componentAdapter) {
                try {
                    int displayedIndex = componentAdapter.row;
                    int modelIndex = ((JXTable) tNutzung).getFilters().convertRowIndexToModel(displayedIndex);
                    NutzungsBuchung n = tableModel.getNutzungAtRow(modelIndex);
                    //NO Geometry & more than one Verwaltungsbereich                    
                    return (n != null && (n.getSollGeloeschtWerden() != null && n.getSollGeloeschtWerden()));
                } catch (Exception ex) {
                    log.error("Fehler beim Highlighting test wurde gelöscht vorhanden", ex);
                    return false;
                }
            }
        };
        Highlighter geloeschtHighlighter = new ColorHighlighter(geloeschtPredicate, LagisBroker.grey, null);
        //final HighlighterPipeline hPipline = new HighlighterPipeline(new Highlighter[]{LagisBroker.ALTERNATE_ROW_HIGHLIGHTER, buchungsStatusHighlighter, geloeschtHighlighter});
        ((JXTable) tNutzung).setHighlighters(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER, buchungsStatusHighlighter, geloeschtHighlighter);
        ((JXTable) tNutzung).setSortOrder(0, SortOrder.ASCENDING);
        ((JXTable) tNutzung).setColumnControlVisible(true);
        ((JXTable) tNutzung).setHorizontalScrollEnabled(true);
        ((JXTable) tNutzung).packAll();
    }
    //private Thread panelRefresherThread;

    public void flurstueckChanged(final Flurstueck newFlurstueck) {
        try {
            log.info("FlurstueckChanged");
            currentFlurstueck = newFlurstueck;
            updateThread.notifyThread(currentFlurstueck);
        } catch (Exception ex) {
            log.error("Fehler beim Flurstückswechsel: ", ex);
            LagisBroker.getInstance().flurstueckChangeFinished(NKFPanel.this);
        }
    }

    public void setComponentEditable(final boolean isEditable) {
        if (isFlurstueckEditable) {
            log.debug("NKFPanel --> setComponentEditable");
            isInEditMode = isEditable;
            tableModel.setIsInEditMode(isEditable);
//        btnAddNutzung.setEnabled(isEditable);
//        if(isEditable && tNutzung.getSelectedRow() != -1){
//            int index = ((JXTable)tNutzung).convertRowIndexToModel(tNutzung.getSelectedRow());
//            if(index != -1 && tableModel.getcurrentNutzungen().get(index).getId() == null){
//               btnRemoveNutzung.setEnabled(true);
//            } else {
//               btnRemoveNutzung.setEnabled(false);
//            }
//        } else if(!isEditable){
//            btnRemoveNutzung.setEnabled(false);
//        }
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
                    final int index = ((JXTable) tNutzung).convertRowIndexToModel(tNutzung.getSelectedRow());
                    if (index != -1) {
                        //TODO NKF Testen
                        if (tableModel.getNutzungAtRow(index).getGueltigbis() == null) {
                            btnRemoveNutzung.setEnabled(true);
                        }
                    }
                }
                if (copyPasteList.size() > 0) {
                    btnPasteNutzung.setEnabled(isEditable);
                }
            } else {
                btnPasteNutzung.setEnabled(isEditable);
                btnCopyNutzung.setEnabled(false);
                btnAddNutzung.setEnabled(false);
                TableCellEditor currentEditor = tNutzung.getCellEditor();
                if (currentEditor != null) {
                    currentEditor.cancelCellEditing();
                }
                btnRemoveNutzung.setEnabled(false);
                btnCopyNutzung.setEnabled(false);
            }


//        HighlighterPipeline pipeline = ((JXTable)tNutzung).getHighlighters();
//        if(isEditable){
//        pipeline.removeHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_DEFAULT);
//        pipeline.addHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_EDIT,false);
//        } else {
//        pipeline.removeHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_EDIT);
//        pipeline.addHighlighter(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER_DEFAULT,false);
//        }
            log.debug("NKFPanel --> setComponentEditable finished");
        } else {
            log.debug("Flurstück ist nicht städtisch Verwaltungen können nicht editiert werden");
        }
    }

    public synchronized void clearComponent() {

        tableModel.refreshTableModel(null);
    }

    //TODO validate the single cell of the tables
    public void refresh(Object refreshObject) {
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
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
        btnPasteNutzung.setToolTipText("Nutzung einfügen");
        btnPasteNutzung.setBorderPainted(false);
        btnPasteNutzung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPasteNutzungActionPerformed(evt);
            }
        });

        btnCopyNutzung.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/copyNu.png"))); // NOI18N
        btnCopyNutzung.setToolTipText("Nutzung kopieren");
        btnCopyNutzung.setBorderPainted(false);
        btnCopyNutzung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopyNutzungActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 375, Short.MAX_VALUE)
                        .add(btnCopyNutzung, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnPasteNutzung, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnAddNutzung, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnRemoveNutzung, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, slrHistory, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
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
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel1)
                        .add(btnAddNutzung, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(btnRemoveNutzung, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(btnPasteNutzung)
                        .add(btnCopyNutzung)))
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
    private void cbxChangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxChangesActionPerformed
        updateSlider(false);
    }//GEN-LAST:event_cbxChangesActionPerformed

    private void btnRemoveNutzungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveNutzungActionPerformed
        final int currentRow = tNutzung.getSelectedRow();
        if (currentRow != -1) {
            //VerwaltungsTableModel currentModel = (VerwaltungsTableModel)tNutzung.getModel();
            if (!tableModel.removeNutzung(((JXTable) tNutzung).getFilters().convertRowIndexToModel(currentRow))) {
                updateSlider(true);
            }
            //tableModel.fireTableDataChanged();
        }

    }//GEN-LAST:event_btnRemoveNutzungActionPerformed

    private void btnAddNutzungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNutzungActionPerformed
        NutzungsCreator nurtungsCreator = new NutzungsCreator(NutzungsCreator.MODE_ADD_NUTZUNG);
        nurtungsCreator.execute();
    }//GEN-LAST:event_btnAddNutzungActionPerformed

    private void btnCopyNutzungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopyNutzungActionPerformed
        copyPasteList.clear();
        if (tNutzung.getSelectedRow() != -1) {
            final int[] selectedRows = tNutzung.getSelectedRows();
            for (int i = 0; i < selectedRows.length; i++) {
                tNutzung.getSelectedRow();
                final int index = ((JXTable) tNutzung).convertRowIndexToModel(selectedRows[i]);
                final NutzungsBuchung curNutzungToCopy = tableModel.getNutzungAtRow(index);
                if (curNutzungToCopy != null) {
                    copyPasteList.add(Nutzung.createNewNutzung(curNutzungToCopy));
                }
            }
        }
        NutzungsCreator nurtungsCreator = new NutzungsCreator(NutzungsCreator.MODE_COPY_NUTZUNG);
        nurtungsCreator.execute();
    }//GEN-LAST:event_btnCopyNutzungActionPerformed

    private void btnPasteNutzungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasteNutzungActionPerformed
        if (copyPasteList.size() > 0) {
            Nutzung lastNutzung = null;
            for (Nutzung curNutzung : copyPasteList) {
                tableModel.addNutzung(curNutzung);
                lastNutzung = curNutzung;
            }
            selectNutzungInHistory(lastNutzung.getNutzungsBuchungen().get(0));
        }
    }//GEN-LAST:event_btnPasteNutzungActionPerformed

    //TODO SAME AS NKFTABLEMODEL
    //ToDo problem if server is not responding not the best concept
    class NutzungsCreator extends SwingWorker<Date, Void> {

        private static final int MODE_ADD_NUTZUNG = 0;
        private static final int MODE_COPY_NUTZUNG = 1;
        //private static final int MODE_SET_NUTZUNG_HISTORIC=1;
        private int currentMode = 0;

        NutzungsCreator(int mode) {
            super();
            currentMode = mode;
        }

        protected Date doInBackground() throws Exception {
            try {

                return EJBroker.getInstance().getCurrentDate();


            } catch (Exception ex) {
                log.error("Fehler beim abrufen des Datums vom Server", ex);
                return null;
            }
        }

        protected void done() {
            super.done();
            if (isCancelled()) {
                log.warn("Swing Worker wurde abgebrochen");
                return;
            }
            try {
                Date serverDate = get();
                if (serverDate != null) {
                    switch (currentMode) {
                        case MODE_ADD_NUTZUNG:
                            final NutzungsBuchung tmp = new NutzungsBuchung();
                            tmp.setGueltigvon(serverDate);
                            tableModel.addNutzung(Nutzung.createNewNutzung(serverDate));
                            log.info("New Nutzung added to Model");
                            break;
                        case MODE_COPY_NUTZUNG:
                            if (copyPasteList.size() > 0) {
                                for (Nutzung curNutzung : copyPasteList) {
                                    curNutzung.getNutzungsBuchungen().get(0).setGueltigvon(serverDate);
                                }
                                if (isInEditMode) {
                                    btnPasteNutzung.setEnabled(true);
                                }
                            } else {
                                copyNotPossible();
                            }
                            break;
                        default:
                            log.warn("Mode is unbekannt tue nichts");
                    }
                } else {
                    log.warn("Es konnte keine Datum geliefert werden.");
                    if (currentMode == MODE_COPY_NUTZUNG) {
                        copyNotPossible();
                    }
                }
            } catch (Exception ex) {
                log.error("Fehler beim verarbeiten des Results vom NutzungsCreator (SwingWorker)", ex);
                if (currentMode == MODE_COPY_NUTZUNG) {
                    copyNotPossible();
                }
                return;
            }
        }

        private void copyNotPossible() {
            log.warn("Kopieren nicht möglich");
            copyPasteList.clear();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddNutzung;
    private javax.swing.JButton btnCopyNutzung;
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
    // End of variables declaration//GEN-END:variables
    //TODO nicht die Methode überschreiben sondern ein Feld in der Superklasse anlegen und dieses Feld in der erbenden Klasse überschreiben

    @Override
    public String getWidgetName() {
        return WIDGET_NAME;
    }

    public void mouseReleased(final MouseEvent e) {
    }

    public void mousePressed(final MouseEvent e) {
    }

    public void mouseExited(final MouseEvent e) {
    }

    public void mouseEntered(final MouseEvent e) {
    }

    public void mouseClicked(final MouseEvent e) {
        Object source = e.getSource();
//        if(source instanceof JXTable){
//            log.debug("Mit maus auf NKFTabelle geklickt");
//            int selecetdRow = tNutzung.getSelectedRow();
//            if(selecetdRow != -1){
//                if(isInEditMode){
//                    Nutzung nutzung = tableModel.getNutzungAtRow(((JXTable)tNutzung).convertRowIndexToModel(selecetdRow));
//                    if(nutzung != null && nutzung.getGueltigbis() != null){
//                        btnRemoveNutzung.setEnabled(false);
//                    } else {
//                        btnRemoveNutzung.setEnabled(true);
//                    }
//                }
//            } else {
//                wasRemovedEnabled=false;
//                btnRemoveNutzung.setEnabled(false);
//            }
//        }
        log.debug("MouseClicked");
        // falls es Nutzung eine Stille Reserve besitzt zu der entsprechenden Nutzung springen
        if (source instanceof JXTable) {
            log.debug("Mit maus auf NKFTabelle geklickt");
            int selecetdRow = tNutzung.getSelectedRow();
            if (selecetdRow != -1) {
                NutzungsBuchung nutzung = tableModel.getNutzungAtRow(((JXTable) tNutzung).convertRowIndexToModel(selecetdRow));
                if (nutzung != null && e.getClickCount() == 2 && (!isInEditMode || tNutzung.getSelectedColumn() == 9)) {
                    jumpToPredecessorNutzung(nutzung);
                }
            }
        }
    }

    private void jumpToPredecessorNutzung(NutzungsBuchung buchung) {
        log.debug("Versuche zu Vorgängernutzung zu springen: ");
        if (tNutzung.getCellEditor() != null) {
            tNutzung.getCellEditor().cancelCellEditing();
        }
        NutzungsBuchung vorgaenger = null;
        if (buchung != null && buchung.getNutzung() != null && (vorgaenger = buchung.getNutzung().getPredecessorBuchung(buchung)) != null) {
            log.debug("Vorgänger Nutzung gefunden");
            selectNutzungInHistory(vorgaenger);
        } else {
            log.fatal("Es gibt keinen Vorgänger für die Nutzung: " + buchung.getId());
        }
//        int tickToJump = getTickForNutzung(vorgaenger);
//        if (tickToJump != -1) {
//            log.debug("Es wurde ein Tick gefunden zu dem gesprungen werden kann: " + tickToJump);
//            slrHistory.setValue(tickToJump);
//            int index = tableModel.getIndexOfNutzung(vorgaenger);
//            log.debug("index: " + index);
//            int displayedIndex = ((JXTable) tNutzung).getFilters().convertRowIndexToView(index);
//            if (index != -1) {
//                log.debug("DisplayedIndex: " + displayedIndex);
//                log.debug("Tablemodel rowCount: " + tableModel.getRowCount());
//                tNutzung.getSelectionModel().clearSelection();
//                tNutzung.getSelectionModel().addSelectionInterval(displayedIndex, displayedIndex);
//                Rectangle tmp = tNutzung.getCellRect(displayedIndex, 0, true);
//                if (tmp != null) {
//                    tNutzung.scrollRectToVisible(tmp);
//                }
//            } else {
//                log.debug("Keine passende Nutzung im TableModel gefunden");
//            }
//        } else {
//            log.debug("Kein Tick gefunden zu dem gesprungen werden kann");
//        }
    }

    private void selectNutzungInHistory(NutzungsBuchung nutzung) {
        int tickToJump = getTickForNutzung(nutzung);
        if (tickToJump != -1) {
            log.debug("Es wurde ein Tick gefunden zu dem gesprungen werden kann: " + tickToJump);
            slrHistory.setValue(tickToJump);
            int index = tableModel.getIndexOfNutzung(nutzung);
            log.debug("index: " + index);
            int displayedIndex = ((JXTable) tNutzung).getFilters().convertRowIndexToView(index);
            if (index != -1) {
                log.debug("DisplayedIndex: " + displayedIndex);
                log.debug("Tablemodel rowCount: " + tableModel.getRowCount());
                tNutzung.getSelectionModel().clearSelection();
                tNutzung.getSelectionModel().addSelectionInterval(displayedIndex, displayedIndex);
                Rectangle tmp = tNutzung.getCellRect(displayedIndex, 0, true);
                if (tmp != null) {
                    tNutzung.scrollRectToVisible(tmp);
                }
            } else {
                log.debug("Keine passende Nutzung im TableModel gefunden");
            }
        } else {
            log.debug("Kein Tick gefunden zu dem gesprungen werden kann");
        }
    }
    private NutzungsBuchung currentPopupNutzung = null;

    class PopupListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            showPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            showPopup(e);
        }

        private void showPopup(MouseEvent e) {
            log.debug("showPopup");
            if (e.isPopupTrigger()) {
                //ToDo funktioniert nicht unter linux
                log.debug("popup triggered");
                int rowAtPoint = tNutzung.rowAtPoint(new Point(e.getX(), e.getY()));
                NutzungsBuchung selectedNutzung = null;
                if (rowAtPoint != -1 && (selectedNutzung = tableModel.getNutzungAtRow(((JXTable) tNutzung).getFilters().convertRowIndexToModel(rowAtPoint))) != null && selectedNutzung.getNutzung() != null && selectedNutzung.getNutzung().getPredecessorBuchung(selectedNutzung) != null) {
                    log.debug("nutzung found");
                    currentPopupNutzung = selectedNutzung;
                    predecessorPopup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }
    }

    public void updateFlurstueckForSaving(final Flurstueck flurstueck) {
        final Set<Nutzung> vNutzungen = flurstueck.getNutzungen();
        if (vNutzungen != null) {
            vNutzungen.clear();
            vNutzungen.addAll(tableModel.getAllNutzungen());
        } else {
            final HashSet newSet = new HashSet();
            newSet.addAll(tableModel.getAllNutzungen());
            flurstueck.setVerwaltungsbereiche(newSet);
        }
    }

    public void tableChanged(final TableModelEvent e) {
        //TODO CHECK FOR REFACTORING
        log.debug("tableChanged");
        final Refreshable refresh = LagisBroker.getInstance().getRefreshableByClass(NKFOverviewPanel.class);
        if (refresh != null) {            
            refresh.refresh(new NutzungsContainer(tableModel.getSelectedNutzungen(), tableModel.getCurrentDate()));
        }
        ((JXTable) tNutzung).packAll();
    }

    //ToDo refactorn viel zu kompliziert??
    //ToDo SliderStateChanged
    //private boolean wasRemovedEnabled = false;
    public void stateChanged(final ChangeEvent e) {
        if (cbxChanges.isSelected()) {
            if (historicNutzungenDayClasses != null) {
                try {

                    final JSlider source = (JSlider) e.getSource();
                    log.debug("Aktuelle Slider position: " + source.getValue());
                    log.debug("Counter: " + counter);
                    if (source.getValue() < counter) {
                        lblCurrentHistoryPostion.setText(LagisBroker.getDateFormatter().format(historicNutzungenDayClasses.get(source.getValue()).getGueltigbis()));

                    } else {
                        lblCurrentHistoryPostion.setText("Aktuelle Nutzungen");
                    }
                    if (!source.getValueIsAdjusting()) {
                        if (source.getValue() < counter) {
                            tableModel.setModelToHistoryDate(historicNutzungenDayClasses.get(source.getValue()).getGueltigbis());
                            lblHistoricIcon.setIcon(icoHistoricIcon);
                            if (isInEditMode) {
                                btnAddNutzung.setEnabled(false);

                                //wasRemovedEnabled = btnRemoveNutzung.isEnabled();
                                btnRemoveNutzung.setEnabled(false);
                            }
                        } else {
                            lblHistoricIcon.setIcon(icoHistoricIconDummy);
                            tableModel.setModelToHistoryDate(null);
                            if (isInEditMode) {
                                btnAddNutzung.setEnabled(true);
                                //TODO WHY DOES THIS NOT WORK
                                //btnRemoveNutzung.setEnabled(wasRemovedEnabled);
                                btnRemoveNutzung.setEnabled(false);
                            }

                        }
                    }
                } catch (Exception ex) {
                    log.error("Fehler beim updaten des Slider labels: ", ex);
                }
            }
        } else {
            final JSlider source = (JSlider) e.getSource();
            log.debug("Aktuelle Slider position: " + source.getValue());
            int currentValue = source.getValue();
            final GregorianCalendar calender = new GregorianCalendar();
            if (currentValue == slrHistory.getMaximum()) {
                lblCurrentHistoryPostion.setText("Aktuelle Nutzungen");
                currentDate = last;
            } else if (currentValue == slrHistory.getMaximum() - 1) {
                lblCurrentHistoryPostion.setText(LagisBroker.getDateFormatter().format(last));
                currentDate = last;
            } else if (mode == DAY_SCALE) {
                calender.setTime(first);
                //TRY DAYS
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
                            //TODO WHY DOES THIS NOT WORK
                            //btnRemoveNutzung.setEnabled(wasRemovedEnabled);
                            btnRemoveNutzung.setEnabled(false);
                        }
                        return;
                    }
                    log.debug("currentDate: " + currentDate);
                    log.debug("lastDate: " + last);
                    final Date tmpDate = LagisBroker.getDateWithoutTime(last);
                    final Date tmpDate2 = LagisBroker.getDateWithoutTime(currentDate);
                    if (!tmpDate.equals(tmpDate2)) {
                        log.debug("current == last");
                        tableModel.setModelToHistoryDate(currentDate);
                        //TODO THIS CAUSE IS IMPOSSIBLE BECAUSE NO EDIT MODE FOR HISTORIC FLURstück
                        if (isOnlyHistoric) {
                            lblHistoricIcon.setIcon(icoHistoricIcon);
                            btnAddNutzung.setEnabled(false);
                            btnRemoveNutzung.setEnabled(false);
                        } else {
                            lblHistoricIcon.setIcon(icoHistoricIcon);
                            if (isInEditMode) {
                                btnAddNutzung.setEnabled(false);
                                //wasRemovedEnabled = btnRemoveNutzung.isEnabled();
                                btnRemoveNutzung.setEnabled(false);
                            }
                        }
                    } else {
                        log.debug("current != last");
                        if (!isOnlyHistoric) {
                            log.debug("nicht nur historische");
                            tableModel.setModelToHistoryDate(last);
                            lblHistoricIcon.setIcon(icoHistoricIconDummy);
                            if (isInEditMode) {
                                btnAddNutzung.setEnabled(true);
                                //TODO WHY DOES THIS NOT WORK
                                //btnRemoveNutzung.setEnabled(wasRemovedEnabled);
                                btnRemoveNutzung.setEnabled(false);
                            }
                        } else {
                            log.debug("nur historische");
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
    private Date currentDate;
    private ArrayList<NutzungsBuchung> sortedNutzungen;
    //perhaps not good
    ArrayList<Date> dateToTicks;
    ArrayList<NutzungsBuchung> historicNutzungenDayClasses;
    private int counter = 0;
    private int mode;
    private static final int YEAR_SCALE = 1;
    private static final int MONTH_SCALE = 2;
    private static final int DAY_SCALE = 3;
    private Date first;
    private Date last;
    boolean isOnlyHistoric = false;

    //ToDo refactor
    public synchronized void updateSlider(final boolean setToLatestHistoric) {
        log.debug("update Slider");
        if (cbxChanges.isSelected()) {
            log.debug("nach Änderungen");
            try {
                slrHistory.setSnapToTicks(true);
                slrHistory.setMajorTickSpacing(5);
                slrHistory.setMinorTickSpacing(1);
                final ArrayList<Nutzung> nutzungen = tableModel.getAllNutzungen();
                final ArrayList<NutzungsBuchung> sortedHistoricNutzungen = new ArrayList<NutzungsBuchung>();
                if (nutzungen != null) {
                    //sortedNutzungen = (Vector) tableModel.getAllNutzungen().clone();
                    sortedNutzungen = tableModel.getAllBuchungen();
                    counter = 0;
                    if (sortedNutzungen.size() >= 1) {
                        for (NutzungsBuchung curBuchung : sortedNutzungen) {
                            if (curBuchung.getGueltigbis() == null) {
                                break;
                            }
                            sortedHistoricNutzungen.add(curBuchung);
                            counter++;
                        }
                        log.debug("Anzahl historischer NKF Einträge: " + counter);
                        if (counter != 0) {
                            if (counter > 1) {
                                historicNutzungenDayClasses = new ArrayList<NutzungsBuchung>();
                                dateToTicks = new ArrayList<Date>();
                                counter = 0;
                                Iterator<NutzungsBuchung> it = sortedHistoricNutzungen.iterator();
                                //TODO what if exactly one element is historic;
                                NutzungsBuchung nutzungToTest = it.next();
                                //boolean lastAreEquals = true;
                                //Nutzung curNutzung = null;
                                while (it.hasNext()) {
                                    NutzungsBuchung curNutzung = it.next();
                                    final Date curGueltigBis = LagisBroker.getDateWithoutTime(curNutzung.getGueltigbis());
                                    final Date gueltigBisToTest = LagisBroker.getDateWithoutTime(nutzungToTest.getGueltigbis());
                                    log.debug("aktuell zu testende historische Nutzung: " + curGueltigBis + " millis: " + curGueltigBis.getTime());
                                    log.debug("test historische Nutzung: " + gueltigBisToTest + " millis: " + gueltigBisToTest.getTime());
                                    log.debug("Sind Nutzungen am gleichen Tag?: " + curGueltigBis.equals(gueltigBisToTest));
                                    if (!curGueltigBis.equals(gueltigBisToTest)) {
                                        log.debug("Nutzungen sind nicht gleichen Tag");
                                        counter++;
                                        historicNutzungenDayClasses.add(nutzungToTest);
                                        dateToTicks.add(gueltigBisToTest);
                                        nutzungToTest = curNutzung;
                                    }
                                    //if(curNutzung.getGueltigbis(). )
                                }
                                historicNutzungenDayClasses.add(nutzungToTest);
                                dateToTicks.add(LagisBroker.getDateWithoutTime(nutzungToTest.getGueltigbis()));
                                counter++;
                            } else {
                                dateToTicks = new ArrayList<Date>();
                                historicNutzungenDayClasses = sortedHistoricNutzungen;
                                dateToTicks.add(LagisBroker.getDateWithoutTime(sortedHistoricNutzungen.get(0).getGueltigbis()));
                            }
                        }

                        log.debug("Anzahl historischer NKF Einträge an unterschiedlichen Tagen: " + counter);
                        //counter++;
                        //log.debug("Anzahl Historischer NKF Einträge: "+counter);
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
                            //TODO WARUM?
                            slrHistory.setMaximum(counter);
                            if (setToLatestHistoric) {
                                slrHistory.setValue(counter - 1);
                            } else {
                                slrHistory.setValue(counter);
                            }
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
            log.debug("nach zeitstrahl");
            try {
                isOnlyHistoric = false;
                slrHistory.setSnapToTicks(false);
                sortedNutzungen = (ArrayList) tableModel.getAllBuchungen();
                final ArrayList<NutzungsBuchung> sortedHistoricNutzungen = new ArrayList<NutzungsBuchung>();
                first = null;
                last = null;
                if (sortedNutzungen != null) {
                    counter = 0;
                    if (sortedNutzungen.size() >= 1) {
                        Collections.sort(sortedNutzungen, NutzungsBuchung.DATE_COMPARATOR);
                        final Iterator<NutzungsBuchung> it = sortedNutzungen.iterator();
                        while (it.hasNext()) {
                            final NutzungsBuchung curNutzung = it.next();
                            //log.debug("current Nutzung gueltigBis: "+curNutzung.getGueltigbis());
                            //curNutzung.getGueltigbis().getTime();
                            if (curNutzung.getGueltigbis() == null) {
                                break;
                            }
                            counter++;
                            sortedHistoricNutzungen.add(curNutzung);
                        }
                        log.debug("Anzahl historischer NKF Einträge: " + counter);
                        if (counter != 0) {
                            first = sortedHistoricNutzungen.get(0).getGueltigvon();
                            last = sortedHistoricNutzungen.get(sortedHistoricNutzungen.size() - 1).getGueltigbis();

                            //TODO OPTIMIZE for example over isHistoric
                            long between;
                            if (sortedNutzungen.size() == sortedHistoricNutzungen.size()) {
                                log.debug("Nur historische Nutzungen");
                                between = last.getTime() - first.getTime();
                                isOnlyHistoric = true;
                            } else {
                                last = new Date();
                                between = last.getTime() - first.getTime();
                            }
                            //TODO ROUND
                            log.debug("Millisekunden zwischen den Nutzungen: " + between);
                            final int days = (int) (Math.round(between / 1000 / 60 / 60 / 24));
                            log.debug("Tage zwischen den Nutzungen: " + days);
                            final int months = (int) (Math.round(between / 1000 / 60 / 60 / 24 / 30));
                            log.debug("Monate zwischen den Nutzungen: " + months);
                            final int years = (int) (Math.round(between / 1000 / 60 / 60 / 24 / 30 / 12));
                            log.debug("Jahre zwischen den Nutzungen: " + years);

                            if (days > 365) {
                                if (months > 360) {
                                    slrHistory.setMinimum(0);
                                    slrHistory.setMaximum(years + 1);
                                    slrHistory.setMinorTickSpacing(5);
                                    slrHistory.setMajorTickSpacing(10);
                                    mode = YEAR_SCALE;
                                    slrHistory.setValue(years + 1);
                                    slrHistory.setEnabled(true);
                                    cbxChanges.setEnabled(true);
                                } else {
                                    slrHistory.setMinimum(0);
                                    slrHistory.setMinorTickSpacing(12);
                                    slrHistory.setMaximum(months + 1);
                                    mode = MONTH_SCALE;
                                    slrHistory.setValue(months + 1);
                                    slrHistory.setEnabled(true);
                                    cbxChanges.setEnabled(true);
                                }
                            } else {
                                slrHistory.setMinimum(0);
                                slrHistory.setMinorTickSpacing(30);
                                slrHistory.setMaximum(days + 1);
                                mode = DAY_SCALE;
                                slrHistory.setValue(days + 1);
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
                        //TODO NO HISTORY FUNCTION OR RETURN DUPLICATE CODE !!!
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
    }

    public void valueChanged(final ListSelectionEvent e) {
        if (tNutzung.getSelectedRow() != -1) {
            final int index = ((JXTable) tNutzung).convertRowIndexToModel(tNutzung.getSelectedRow());
            //if(index != -1 && tableModel.getcurrentNutzungen().get(index).getId() == null && isInEditMode){
            if (index != -1) {
                btnCopyNutzung.setEnabled(true);
                if (isInEditMode) {
                    if (tableModel.getNutzungAtRow(index).getGueltigbis() == null) {
                        btnRemoveNutzung.setEnabled(true);
                    } else {
                        btnRemoveNutzung.setEnabled(false);
                    }

                } else {
                    btnRemoveNutzung.setEnabled(false);
                }

            } else {
                btnCopyNutzung.setEnabled(false);
                btnRemoveNutzung.setEnabled(false);
            }

        } else {
            btnRemoveNutzung.setEnabled(false);
            btnCopyNutzung.setEnabled(false);
        }
    }

    private int getTickForNutzung(NutzungsBuchung current) {
        if (cbxChanges.isSelected()) {
            log.debug("Tick wird für Nutzung: " + current.getId() + " im Änderungsmodus ermittelt");
            log.debug("dateToTicks: " + dateToTicks);
            return dateToTicks.indexOf(LagisBroker.getDateWithoutTime(current.getGueltigbis()));
        } else {
            log.debug("Tick wird für Nutzung: " + current.getId() + " im Zeitstrahlmodus ermittelt");
            if (current.getGueltigbis() == null) {
                log.debug("Nutzung ist nicht historisch, springe zu aktuellen Nutzungen");
                return slrHistory.getMaximum();
            } else {
                log.debug("Nutzung ist historisch");
                final GregorianCalendar calender = new GregorianCalendar();
                final long between = current.getGueltigbis().getTime() - first.getTime();
                log.debug("Zeit zwischen erster Nutzung und ausgewählter: " + between);
                if (mode == DAY_SCALE) {
                    log.debug("DayScale");
                    return (int) (Math.round(between / 1000 / 60 / 60 / 24));
                } else if (mode == MONTH_SCALE) {
                    log.debug("MonthScale");
                    return (int) (Math.round(between / 1000 / 60 / 60 / 24 / 30));
                } else if (mode == YEAR_SCALE) {
                    log.debug("YearScale");
                    return (int) (Math.round(between / 1000 / 60 / 60 / 24 / 30 / 12));
                }
            }

            log.debug("Unknown Scale");
            return -1;
        }
    }

    //TODO USE
    @Override
    public Icon getWidgetIcon() {
        return null;
    }

    @Override
    public int getStatus() {
        if (isFlurstueckEditable) {
            if (tNutzung.getCellEditor() != null) {
                validationMessage = "Bitte vollenden Sie alle Änderungen bei den Nutzungen.";
                return Validatable.ERROR;
            }

            boolean existingBufferDisolves = false;
            boolean existingValidCurrentNutzung = false;
            boolean existingUnbookedDeletedNutzung = false;

            ArrayList<Nutzung> currentNutzungen = tableModel.getSelectedNutzungen();
            ArrayList<NutzungsBuchung> currentBuchungen = tableModel.getCurrentBuchungen();

            if (currentNutzungen != null || currentNutzungen.size() > 0) {
                for (NutzungsBuchung currentBuchung : currentBuchungen) {
                    if (currentBuchung != null && currentBuchung.getNutzungsart() != null) {
                        //return Validatable.VALID;
                        existingValidCurrentNutzung = true;
                    }
                    if (currentBuchung.getFlaeche() != null && currentBuchung.getQuadratmeterpreis() != null) {
                        log.debug("Neuer Preis: " + (currentBuchung.getFlaeche() * currentBuchung.getQuadratmeterpreis()));
                    } else {
                        log.debug("Neuer Preis kann nicht berechnet werden");
                    }
                    try {
                        final Set<Nutzung.NUTZUNG_STATES> nutzung_states;
                        nutzung_states = currentBuchung.getNutzung().getNutzungsState();
                        if (nutzung_states.contains(Nutzung.NUTZUNG_STATES.STILLE_RESERVE_DISOLVED)) {
                            existingBufferDisolves = true;
                        }
                    } catch (IllegalNutzungStateException ex) {
                        log.warn("Nutzungszustand konnte nicht ermittelt werden",ex);
                        validationMessage = "Die Änderungen können nicht gespeichert werden,\n weil der Zustand einer Nutzung nicht ermittelt werden konnte.\n Nutzungsnummer: "+currentBuchung.getNutzung().getId();
                        return Validatable.ERROR;
                    }
                }
            }
//            Vector<NutzungsBuchung> oldNutzungen = tableModel.getAllNutzungen();
//ToDO NKF Nutzung
//            if (oldNutzungen != null || oldNutzungen.size() > 0) {
//                for (NutzungsBuchung oldNutzung : oldNutzungen) {
//                    if (oldNutzung != null && oldNutzung.getSollGeloeschtWerden() != null && oldNutzung.getSollGeloeschtWerden()) {
//                        //ToDo Dirk fragen
//                        existingUnbookedDeletedNutzung = true;
//                    }
//                }
//            }
            if (!existingValidCurrentNutzung) {
                validationMessage = "Es muss mindestens eine aktuelle Nutzung mit Nutzungsart angelegt sein,\num das Flurstück speichern zu können.";
                return Validatable.ERROR;
            } else if (existingBufferDisolves) {
                int result = JOptionPane.showConfirmDialog(LagisBroker.getInstance().getParentComponent(), "Bei mindestens einer Nutzung reicht die Stille Reserve nicht aus,\n" +
                        "um die Verminderung des Gesamtbetrags zu decken. Alle zu dieser Nutzung\n" +
                        "gehörenden offenen Buchungen müssen gebucht werden.\n\n" +
                        "Möchten Sie diese Nutzungen jetzt buchen?\n", "Stille Reserve aufgelöst", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    return Validatable.VALID;
                } else {
                    validationMessage = "Die Änderungen können nicht gespeichert werden, weil die offenen Buchungen nicht gebucht wurden.";
                    return Validatable.ERROR;
                }
            } else if (existingUnbookedDeletedNutzung) {
                int result = JOptionPane.showConfirmDialog(LagisBroker.getInstance().getParentComponent(), "Mindestens eine Nutzung mit Stiller Reserve soll gelöscht werden,\n" +
                        "dafür müssen alle dazugehörigen offenen Buchungen gebucht werden.\n\n" + "Möchten Sie dieses Nutzungen jetzt buchen?\n", "Gelöschte Nutzung", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    return Validatable.VALID;
                } else {
                    validationMessage = "Die Änderungen können nicht gespeichert werden, weil die offenen Buchungen nicht gebucht wurden.";
                    return Validatable.ERROR;
                }
            } else {
                return Validatable.VALID;
            }
        } else {
            return Validatable.VALID;
        }
    }
}
