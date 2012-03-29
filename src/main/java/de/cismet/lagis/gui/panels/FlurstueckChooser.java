/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * FlurstueckChooser.java
 *
 * Created on 31. Dezember 2007, 12:19
 */
package de.cismet.lagis.gui.panels;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.*;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.cismet.cids.custom.beans.verdis_grundis.*;

import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.FeatureGroupWrapper;
import de.cismet.cismap.commons.gui.StyledFeatureGroupWrapper;

import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.interfaces.DoneDelegate;
import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.FlurstueckRequester;

import de.cismet.lagis.models.KeyComboboxModel;

import de.cismet.lagis.thread.ExtendedSwingWorker;
import de.cismet.lagis.thread.WFSRetrieverFactory;

import de.cismet.lagis.validation.Validatable;

import de.cismet.lagis.widget.AbstractWidget;

import de.cismet.lagisEE.interfaces.Key;

import de.cismet.lagisEE.util.FlurKey;

import de.cismet.tools.CurrentStackTrace;

import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.NoWriteError;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class FlurstueckChooser extends AbstractWidget implements FlurstueckChangeListener,
    Configurable,
    FocusListener,
    FlurstueckRequester,
    DocumentListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final String WIDGET_NAME = "Flurstueck Suchpanel";
    public static final String FEATURE_GRP = "Flurstück";

    private static final String FILTER_CURRENT_NAME = "nur aktuelle";
    private static final String FILTER_HISTORIC_NAME = "nur historische";
    private static final String FILTER_ALL_NAME = "alle Flurstücke";
    private static final String FILTER_ABTEILUNG_IX = "nur Abteilung IX";
    private static final String FILTER_STAEDTISCH = "nur städtische";

    // modes
    public static final int SEARCH_MODE = 0;
    // TODO good name ?
    public static final int CONTINUATION_MODE = 1;
    public static final int CREATION_MODE = 2;
    public static final int CONTINUATION_HISTORIC_MODE = 3;

    private static final Logger log = Logger.getLogger(FlurstueckChooser.class);

    //~ Instance fields --------------------------------------------------------

    private GemarkungRetriever currentGemarkungsRetriever = null;

    ActionListener gemarkungListener = new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                cboGemarkungActionPerformed(evt);
            }
        };

    private FlurRetriever currentFlurRetriever = null;

    ActionListener flurListener = new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                cboFlurActionPerformed(evt);
            }
        };

    private FlurstueckRetriever currentFlurstueckRetriever = null;
    private SwingWorker currentWFSRetriever = null;

    ActionListener flurstueckListener = new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                cboFlurstueckActionPerformed(evt);
            }
        };

    private AutomaticFlurstueckRetriever currentAutomaticRetriever = null;
    private FlurstueckChecker currentFlurstueckChecker = null;
    private Thread currentGemarkungsWaiter = null;

    private boolean isAutomaticRequestInProgress = false;
    private final ReentrantLock automaticRequestLock = new ReentrantLock();

    // configured via configfile
    private Element wfsRequest;
    private Element gemarkung;
    private Element flur;
    private Element flurstZaehler;
    private Element flurstNenner;
    private Element query;
    private String hostname;

    private final Icon icoFilterAll = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/filter_all.png"));
    private final Icon icoFilterCurrent = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/filter_current.png"));
    private final Icon icoFilterHistoric = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/filter_historic.png"));
    private final Icon icoFilterStaedtisch = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/filter_staedtisch.png"));
    private final Icon icoCurrent = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/current.png"));
    private final Icon icoAbteilungIX = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/abteilungIX.png"));
    private final Icon icoHistoric = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/historic.png"));
    // private final Icon icoUnknown = new
    // javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/unknown.png"));
    private final Icon icoWFSWarn = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/FlurstueckPanel/16warn.png"));
    private final Icon icoUnknownFlurstueck = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/toolbar/unkownFlurstueck.png"));

    private final DefaultListCellRenderer defaultListCellRendererFilter = new DefaultListCellRenderer();
    private final DefaultListCellRenderer defaultListCellRendererFlurstueck = new DefaultListCellRenderer();

    private boolean isOnlyHistoricFilterEnabled = false;
    private boolean isOnlyCurrentFilterEnabled = false;
    private final Vector<FlurstueckSchluesselCustomBean> removeFilter = new Vector<FlurstueckSchluesselCustomBean>();
    private boolean isFullInitialized = false;

    private final javax.swing.JTextField txtFlurstueck = new JTextField();
    private final JProgressBar pbTxtFlurstueck = new JProgressBar();
    private final JPanel panTxtFlurstueck = new JPanel();

    private FlurstueckCustomBean currentFlurstueck;
    private FlurstueckSchluesselCustomBean currentyCreatedFlurstueckSchluessel;
    private Color currentColor;
    // Validation
    // must be locked
    private boolean isFlurstueckCandidateValide = false;
    // also lock
    private Boolean isFlurstueckCreateable = false;
    // must be locked
    private final ReentrantLock validationMessageLock = new ReentrantLock();
    private String creationValidationMessage = "Bitte vervollständigen Sie alle Flurstücke";
    private int currentMode = SEARCH_MODE;

    private boolean isOnlyAbteilungIXFilterEnabled = false;
    private boolean isOnlyStaedtischFilterEnabled = false;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAction;
    private javax.swing.JComboBox cboFilter;
    private javax.swing.JComboBox cboFlur;
    private javax.swing.JComboBox cboFlurstueck;
    private javax.swing.JComboBox cboGemarkung;
    private javax.swing.JPanel panFilter;
    private javax.swing.JPanel panFlur;
    private javax.swing.JPanel panFlurstueck;
    private javax.swing.JPanel panGemarkung;
    private javax.swing.JProgressBar pbFilter;
    private javax.swing.JProgressBar pbFlur;
    private javax.swing.JProgressBar pbFlurstueck;
    private javax.swing.JProgressBar pbGemarkung;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FlurstueckChooser object.
     */
    public FlurstueckChooser() {
        this(CONTINUATION_MODE);
    }

    /**
     * Creates new form FlurstueckChooser.
     *
     * @param  mode  DOCUMENT ME!
     */
    public FlurstueckChooser(final int mode) {
        try {
            currentMode = mode;
            initComponents();
            initRenderer();
            configureComponents();
        } catch (Exception e) {
            log.error("Could not Create FlurstueckChooser", e);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void configureComponents() {
        cboFlur.setToolTipText("Flur");
        cboFlurstueck.setToolTipText("Flurstück");
        cboGemarkung.setToolTipText("Gemarkung");

        AutoCompleteDecorator.decorate(cboGemarkung);
        AutoCompleteDecorator.decorate(cboFlur);
        AutoCompleteDecorator.decorate(cboFlurstueck);

        cboGemarkung.addActionListener(gemarkungListener);
        cboGemarkung.getEditor().getEditorComponent().addFocusListener(this);
        cboFlur.addActionListener(flurListener);
        cboFlur.getEditor().getEditorComponent().addFocusListener(this);
        cboFlurstueck.addActionListener(flurstueckListener);
        cboFlurstueck.getEditor().getEditorComponent().addFocusListener(this);

        if (currentMode == SEARCH_MODE) {
            if (log.isDebugEnabled()) {
                // Muss nichts gemacht werden
                log.debug("SEARCH INSTANZ VON FLURSTUECKCHOOSER");
            }
        } else if (currentMode == CREATION_MODE) {
            if (log.isDebugEnabled()) {
                log.debug("CREATION INSTANZ VON FLURSTUECKCHOOSER");
            }
            this.remove(panFilter);
            isOnlyCurrentFilterEnabled = true;
            this.remove(panFlurstueck);
            panTxtFlurstueck.setLayout(new java.awt.BorderLayout());
            panTxtFlurstueck.setMaximumSize(new java.awt.Dimension(100, 28));
            panTxtFlurstueck.setMinimumSize(new java.awt.Dimension(100, 28));
            panTxtFlurstueck.setPreferredSize(new java.awt.Dimension(100, 28));
            txtFlurstueck.setMaximumSize(new java.awt.Dimension(100, 23));
            txtFlurstueck.setMinimumSize(new java.awt.Dimension(100, 23));
            txtFlurstueck.setPreferredSize(new java.awt.Dimension(100, 23));
            txtFlurstueck.setToolTipText("Flurstück");
            txtFlurstueck.setEnabled(false);
            txtFlurstueck.getDocument().addDocumentListener(this);
            panTxtFlurstueck.add(txtFlurstueck, java.awt.BorderLayout.NORTH);
            pbTxtFlurstueck.setLayout(new java.awt.BorderLayout());
            pbTxtFlurstueck.setPreferredSize(new java.awt.Dimension(100, 5));
            pbTxtFlurstueck.setMaximumSize(new java.awt.Dimension(100, 5));
            pbTxtFlurstueck.setMinimumSize(new java.awt.Dimension(100, 5));
            pbTxtFlurstueck.setBorderPainted(false);
            panTxtFlurstueck.add(pbTxtFlurstueck, java.awt.BorderLayout.SOUTH);
            add(panTxtFlurstueck);
            panTxtFlurstueck.setVisible(true);
        } else if ((currentMode == CONTINUATION_MODE) || (currentMode == CONTINUATION_HISTORIC_MODE)) {
            if (log.isDebugEnabled()) {
                log.debug("CONTINUATION INSTANZ VON FLURSTUECKCHOOSER");
            }
            this.remove(panFilter);
            // Damit nur aktuelle Flurstücke beim teilen und zusammenfügen angezeigt werden
            if (currentMode == CONTINUATION_MODE) {
                isOnlyCurrentFilterEnabled = true;
            } else {
                isOnlyHistoricFilterEnabled = true;
            }
        }

        cboGemarkung.setEnabled(false);
        cboFlur.setEnabled(false);
        cboFlurstueck.setEnabled(false);
        txtFlurstueck.setEnabled(false);
        currentGemarkungsRetriever = new GemarkungRetriever(GemarkungRetriever.RETRIEVE_GEMARKUNGEN_MODE);
        // currentGemarkungsRetriever.execute();
        LagisBroker.getInstance().execute(currentGemarkungsRetriever);
    }

    /**
     * DOCUMENT ME!
     */
    private void initRenderer() {
        cboFilter.setRenderer(new ListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    final JLabel label = (JLabel)defaultListCellRendererFilter.getListCellRendererComponent(
                            list,
                            value,
                            index,
                            isSelected,
                            cellHasFocus);
                    if (value.equals(FILTER_CURRENT_NAME)) {
                        label.setIcon(icoFilterCurrent);
                    } else if (value.equals(FILTER_HISTORIC_NAME)) {
                        label.setIcon(icoFilterHistoric);
                    } else if (value.equals(FILTER_STAEDTISCH)) {
                        label.setIcon(icoFilterStaedtisch);
                    } else if (value.equals(FILTER_ABTEILUNG_IX)) {
                        label.setIcon(icoAbteilungIX);
                    } else {
                        label.setIcon(icoFilterAll);
                    }
                    return label;
                }
            });

        cboFlurstueck.setRenderer(new ListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    final JLabel label = (JLabel)defaultListCellRendererFlurstueck.getListCellRendererComponent(
                            list,
                            value,
                            index,
                            isSelected,
                            cellHasFocus);
                    if (value instanceof FlurstueckSchluesselCustomBean) {
                        final FlurstueckSchluesselCustomBean tmpKey = (FlurstueckSchluesselCustomBean)value;
                        if (tmpKey != null) {
                            if (tmpKey.getGueltigBis() == null) {
                                final FlurstueckArtCustomBean flurstueckArt = tmpKey.getFlurstueckArt();
                                if ((flurstueckArt != null) && (flurstueckArt.getBezeichnung() != null)
                                            && flurstueckArt.getBezeichnung().equals(
                                                FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                                    label.setIcon(icoCurrent);
                                } else {
                                    label.setIcon(icoAbteilungIX);
                                }
                            } else {
                                label.setIcon(icoHistoric);
                            }
                            return label;
                        }
                    }
                    label.setIcon(icoUnknownFlurstueck);
                    return label;
                }
            });
        // Schöneres Icon cboGemarkung.setRenderer(new ListCellRenderer() { public Component
        // getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // JLabel label=(JLabel)defaultListCellRendererGemarkung.getListCellRendererComponent(list, value, index,
        // isSelected, cellHasFocus); //TODO Strings können auch bekannt sein if(value instanceof  GemarkungCustomBean){
        // label.setIcon(icoGemarkung); return label; } label.setIcon(icoUnknown); return label; } });
        // cboFlur.setRenderer(new ListCellRenderer() { public Component getListCellRendererComponent(JList list, Object
        // value, int index, boolean isSelected, boolean cellHasFocus) { JLabel
        // label=(JLabel)defaultListCellRendererFlur.getListCellRendererComponent(list, value, index, isSelected,
        // cellHasFocus); //TODO Strings können auch bekannt sein if(value instanceof  FlurKey){ label.setIcon(icoFlur);
        // return label; } label.setIcon(icoUnknown); return label; } });
        // ((JTextField)cboFlurstueck.getEditor().getEditorComponent()).
    }

    @Override
    public void clearComponent() {
    }

    @Override
    public void refresh(final Object refreshObject) {
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
        if (log.isDebugEnabled()) {
            log.debug("FlurstueckSearchPanel --> setComponentEditable");
        }
        LagisBroker.warnIfThreadIsNotEDT();
        // ToDO duplicated Code
        if (EventQueue.isDispatchThread()) {
            if (isEditable) {
                setHighlightColor(Color.WHITE);
            }
            // ATTENTION UGLY WINNING Wenn in Editmodus oder wenn nicht und die GemarkungCustomBean ist disabled (komm
            // nur vor wenn aus dem Editmodus heraus gewechselt wird)
            if (LagisBroker.getInstance().isInEditMode()
                        || (!LagisBroker.getInstance().isInEditMode() && !cboGemarkung.isEnabled()
                            && isFullInitialized)) {
                if (log.isDebugEnabled()) {
                    log.debug("enable/disable comboboxes for editmode");
                }
                cboGemarkung.setEnabled(!isEditable);
                cboFlur.setEnabled(!isEditable);
                cboFlurstueck.setEnabled(!isEditable);
                cboFilter.setEnabled(!isEditable);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("not switching for editmode");
                }
            }
        } else {
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        if (isEditable) {
                            setHighlightColor(Color.WHITE);
                        }
                        // ATTENTION UGLY WINNING Wenn in Editmodus oder wenn nicht und die GemarkungCustomBean ist
                        // disabled (komm nur vor wenn aus dem Editmodus heraus gewechselt wird)
                        if (LagisBroker.getInstance().isInEditMode()
                                    || (!LagisBroker.getInstance().isInEditMode() && !cboGemarkung.isEnabled()
                                        && isFullInitialized)) {
                            if (log.isDebugEnabled()) {
                                log.debug("enable/disable comboboxes for editmode");
                            }
                            cboGemarkung.setEnabled(!isEditable);
                            cboFlur.setEnabled(!isEditable);
                            cboFlurstueck.setEnabled(!isEditable);
                            cboFilter.setEnabled(!isEditable);
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("not switching for editmode");
                            }
                        }
                    }
                });
        }
        if (log.isDebugEnabled()) {
            log.debug("FlurstueckSearchPanel --> setComponentEditable finished");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        btnAction = new javax.swing.JButton();
        panGemarkung = new javax.swing.JPanel();
        cboGemarkung = new javax.swing.JComboBox();
        pbGemarkung = new javax.swing.JProgressBar();
        panFlur = new javax.swing.JPanel();
        cboFlur = new javax.swing.JComboBox();
        pbFlur = new javax.swing.JProgressBar();
        panFlurstueck = new javax.swing.JPanel();
        cboFlurstueck = new javax.swing.JComboBox();
        pbFlurstueck = new javax.swing.JProgressBar();
        panFilter = new javax.swing.JPanel();
        cboFilter = new javax.swing.JComboBox();
        pbFilter = new javax.swing.JProgressBar();

        setMaximumSize(new java.awt.Dimension(483, 35));
        btnAction.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/flurstueck.png")));
        btnAction.setBorder(null);
        btnAction.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnActionActionPerformed(evt);
                }
            });

        add(btnAction);

        panGemarkung.setLayout(new java.awt.BorderLayout());

        panGemarkung.setMaximumSize(new java.awt.Dimension(100, 28));
        panGemarkung.setMinimumSize(new java.awt.Dimension(100, 28));
        panGemarkung.setPreferredSize(new java.awt.Dimension(100, 28));
        cboGemarkung.setEditable(true);
        cboGemarkung.setMaximumSize(new java.awt.Dimension(100, 23));
        cboGemarkung.setMinimumSize(new java.awt.Dimension(100, 23));
        cboGemarkung.setName("cboGemarkung");
        cboGemarkung.setPreferredSize(new java.awt.Dimension(100, 23));
        panGemarkung.add(cboGemarkung, java.awt.BorderLayout.CENTER);

        pbGemarkung.setBorderPainted(false);
        pbGemarkung.setMaximumSize(new java.awt.Dimension(90, 5));
        pbGemarkung.setMinimumSize(new java.awt.Dimension(90, 5));
        pbGemarkung.setPreferredSize(new java.awt.Dimension(90, 5));
        panGemarkung.add(pbGemarkung, java.awt.BorderLayout.SOUTH);

        add(panGemarkung);

        panFlur.setLayout(new java.awt.BorderLayout());

        panFlur.setMaximumSize(new java.awt.Dimension(100, 28));
        cboFlur.setEditable(true);
        cboFlur.setMaximumSize(new java.awt.Dimension(100, 23));
        cboFlur.setMinimumSize(new java.awt.Dimension(100, 23));
        cboFlur.setName("cboFlur");
        cboFlur.setPreferredSize(new java.awt.Dimension(100, 23));
        panFlur.add(cboFlur, java.awt.BorderLayout.CENTER);

        pbFlur.setBorderPainted(false);
        pbFlur.setMaximumSize(new java.awt.Dimension(90, 5));
        pbFlur.setMinimumSize(new java.awt.Dimension(90, 5));
        pbFlur.setPreferredSize(new java.awt.Dimension(90, 5));
        panFlur.add(pbFlur, java.awt.BorderLayout.SOUTH);

        add(panFlur);

        panFlurstueck.setLayout(new java.awt.BorderLayout());

        panFlurstueck.setMaximumSize(new java.awt.Dimension(100, 23));
        cboFlurstueck.setEditable(true);
        cboFlurstueck.setMaximumSize(new java.awt.Dimension(100, 23));
        cboFlurstueck.setMinimumSize(new java.awt.Dimension(100, 23));
        cboFlurstueck.setName("cboFlurstueck");
        cboFlurstueck.setPreferredSize(new java.awt.Dimension(100, 23));
        panFlurstueck.add(cboFlurstueck, java.awt.BorderLayout.CENTER);

        pbFlurstueck.setBorderPainted(false);
        pbFlurstueck.setMaximumSize(new java.awt.Dimension(90, 5));
        pbFlurstueck.setMinimumSize(new java.awt.Dimension(90, 5));
        pbFlurstueck.setPreferredSize(new java.awt.Dimension(90, 5));
        panFlurstueck.add(pbFlurstueck, java.awt.BorderLayout.SOUTH);

        add(panFlurstueck);

        panFilter.setLayout(new java.awt.BorderLayout());

        cboFilter.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] {
                    "alle Flurst\u00fccke",
                    "nur historische",
                    "nur aktuelle",
                    "nur Abteilung IX",
                    "nur st\u00e4dtische"
                }));
        cboFilter.setToolTipText(
            "Filter: Alle Flurst\u00fccke, nur aktuelle Flurst\u00fccke, nur historische Flurst\u00fccke");
        cboFilter.setMaximumSize(new java.awt.Dimension(130, 23));
        cboFilter.setMinimumSize(new java.awt.Dimension(130, 23));
        cboFilter.setPreferredSize(new java.awt.Dimension(130, 23));
        cboFilter.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboFilterActionPerformed(evt);
                }
            });

        panFilter.add(cboFilter, java.awt.BorderLayout.CENTER);

        pbFilter.setBorderPainted(false);
        pbFilter.setMaximumSize(new java.awt.Dimension(90, 5));
        pbFilter.setMinimumSize(new java.awt.Dimension(90, 5));
        pbFilter.setPreferredSize(new java.awt.Dimension(90, 5));
        panFilter.add(pbFilter, java.awt.BorderLayout.SOUTH);

        add(panFilter);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboFilterActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboFilterActionPerformed
        final String value = (String)cboFilter.getSelectedItem();
        if (value.equals(FILTER_ALL_NAME)) {
            log.info("Filter alle Flurstücke ausgewählt");
            isOnlyCurrentFilterEnabled = false;
            isOnlyHistoricFilterEnabled = false;
            isOnlyStaedtischFilterEnabled = false;
            isOnlyAbteilungIXFilterEnabled = false;
        } else if (value.equals(FILTER_HISTORIC_NAME)) {
            log.info("Filter nur historische ausgewählt");
            isOnlyCurrentFilterEnabled = false;
            isOnlyHistoricFilterEnabled = true;
            isOnlyStaedtischFilterEnabled = false;
            isOnlyAbteilungIXFilterEnabled = false;
        } else if (value.equals(FILTER_ABTEILUNG_IX)) {
            log.info("Filter nur Abteilung IX");
            isOnlyStaedtischFilterEnabled = false;
            isOnlyAbteilungIXFilterEnabled = true;
            isOnlyCurrentFilterEnabled = false;
            isOnlyHistoricFilterEnabled = false;
        } else if (value.equals(FILTER_STAEDTISCH)) {
            log.info("Filter nur staedtische");
            isOnlyStaedtischFilterEnabled = true;
            isOnlyAbteilungIXFilterEnabled = false;
            isOnlyCurrentFilterEnabled = false;
            isOnlyHistoricFilterEnabled = false;
        } else {
            log.info("Filter nur aktuelle ausgewählt");
            isOnlyCurrentFilterEnabled = true;
            isOnlyHistoricFilterEnabled = false;
            isOnlyStaedtischFilterEnabled = false;
            isOnlyAbteilungIXFilterEnabled = false;
        }
        final FlurstueckCustomBean currentFlurstueck = LagisBroker.getInstance().getCurrentFlurstueck();
        // TODO what if it is unkown
        if (currentFlurstueck != null) {
            doAutomaticRequest(
                AutomaticFlurstueckRetriever.FILTER_ACTION_MODE,
                currentFlurstueck.getFlurstueckSchluessel());
        } else {
            doAutomaticRequest(AutomaticFlurstueckRetriever.SET_BOXES_ACCORDING_TO_CONTENT_MODE, null);
            // TODO what todo if no Flurstück is selected
        }
    } //GEN-LAST:event_cboFilterActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboGemarkungActionPerformed(final java.awt.event.ActionEvent evt) {
        final boolean wasResolved;
        final Object selectedItem = cboGemarkung.getSelectedItem();
        if ((currentGemarkungsRetriever != null) && !currentGemarkungsRetriever.isDone()) {
            currentGemarkungsRetriever.cancel(false);
            currentGemarkungsRetriever = null;
        } else if ((currentGemarkungsRetriever != null) && currentGemarkungsRetriever.isDone()) {
            // ugly Winning
            if (currentGemarkungsRetriever.wasResolved) {
                if (log.isDebugEnabled()) {
                    log.debug("Gemarkung konnte geresolved werden, abrufen der Flure mit neuer Gemarkung");
                }
                currentGemarkungsRetriever = new GemarkungRetriever(
                        GemarkungRetriever.RETRIEVE_WITH_RESOLVED_MODE,
                        evt,
                        currentGemarkungsRetriever.selectedGemarkung);
                LagisBroker.getInstance().execute(currentGemarkungsRetriever);
                return;
            }
        }
        currentGemarkungsRetriever = new GemarkungRetriever(GemarkungRetriever.RETRIEVE_FLURE_MODE, evt, selectedItem);
        LagisBroker.getInstance().execute(currentGemarkungsRetriever);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  worker  DOCUMENT ME!
     */
    private void setPropertyChangeListener(final SwingWorker worker) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("automaticRequestLock locked");
            }
            automaticRequestLock.lock();
            if ((currentAutomaticRetriever != null) && (worker != null)) {
                if (log.isDebugEnabled()) {
                    log.debug("Listener set");
                }
                worker.addPropertyChangeListener(currentAutomaticRetriever);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Listener not set");
                }
            }
            automaticRequestLock.unlock();
            if (log.isDebugEnabled()) {
                log.debug("automaticRequestLock unlocked");
            }
        } catch (Exception ex) {
            log.error("Fehler beim setzen des PropertyChangeListeners --> Unlocke");
            automaticRequestLock.unlock();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboFlurActionPerformed(final java.awt.event.ActionEvent evt) {
        final Object selectedItem = getCboFlur().getSelectedItem();
        if ((currentFlurRetriever != null) && !currentFlurRetriever.isDone()) {
            currentFlurRetriever.cancel(false);
            currentFlurRetriever = null;
        }
        currentFlurRetriever = new FlurRetriever(currentFlurRetriever.RETRIEVE_NORMAL_MODE, evt, selectedItem);
        LagisBroker.getInstance().execute(currentFlurRetriever);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboFlurstueckActionPerformed(final java.awt.event.ActionEvent evt) {
        final Object selectedItem = cboFlurstueck.getSelectedItem();
        if ((currentFlurstueckRetriever != null) && !currentFlurstueckRetriever.isDone()) {
            currentFlurstueckRetriever.cancel(false);
            currentFlurstueckRetriever = null;
        }
        if ((currentWFSRetriever != null) && !currentWFSRetriever.isDone()) {
            currentWFSRetriever.cancel(false);
            currentWFSRetriever = null;
        }
        currentFlurstueckRetriever = new FlurstueckRetriever(
                currentFlurstueckRetriever.RETRIEVE_NORMAL_MODE,
                evt,
                selectedItem);
        LagisBroker.getInstance().execute(currentFlurstueckRetriever);
    }

    @Override
    public void flurstueckChanged(final FlurstueckCustomBean newFlurstueck) {
        try {
            boolean isNoGeometryAssigned = true;
            boolean hasManyVerwaltungsbereiche = false;
            // TODO Doppelt gemoppelt --> wird hier geprüft und im Thread;
            // TODO wird geprüft ob Flurstück nicht städtisch ist
            if (((newFlurstueck.getVerwaltungsbereiche()) == null)
                        || ((newFlurstueck.getVerwaltungsbereiche() != null)
                            && (newFlurstueck.getVerwaltungsbereiche().size() < 2))
                        || (newFlurstueck.getFlurstueckSchluessel() != null)
                        || (newFlurstueck.getFlurstueckSchluessel().getFlurstueckArt() != null)
                        || !newFlurstueck.getFlurstueckSchluessel().getFlurstueckArt().equals(
                            FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                log.info("Keine Verwaltungsgeometrien oder weniger als 2 vorhanden --> WFS");
                // TODO UGLY
                if ((newFlurstueck.getVerwaltungsbereiche() != null)
                            && (newFlurstueck.getVerwaltungsbereiche().size() == 1)) {
                    final VerwaltungsbereichCustomBean verwaltungsbereich = newFlurstueck.getVerwaltungsbereiche()
                                .iterator()
                                .next();
                    if ((verwaltungsbereich != null) && (verwaltungsbereich.getGeometry() != null)) {
                        if (log.isDebugEnabled()) {
                            log.debug("Es ist mindestens eine Geometrie zugeordnet --> es wird nichts hinzugefügt");
                        }
                        LagisBroker.getInstance().flurstueckChangeFinished(FlurstueckChooser.this);
                        return;
                    }
                }
            } else if (newFlurstueck.getVerwaltungsbereiche() != null) {
                hasManyVerwaltungsbereiche = true;
                log.info("mehr als 2 Verwaltungsbereiche");
                isNoGeometryAssigned = true;
                for (final VerwaltungsbereichCustomBean currentBereich : newFlurstueck.getVerwaltungsbereiche()) {
                    if (currentBereich.getGeometry() != null) {
                        isNoGeometryAssigned = false;
                    }
                    // TODO HOTFIX UMSTELLEN EXTREM SCHLECHT --> sollte nicht geknaupt abgefragt werden
                    if (isNoGeometryAssigned == false) {
                        if (log.isDebugEnabled()) {
                            log.debug("Es ist mindestens eine Geometrie zugeordnet --> es wird nichts hinzugefügt");
                        }
                        LagisBroker.getInstance().flurstueckChangeFinished(FlurstueckChooser.this);
                        return;
                    }
                }
            } else {
                log.error("Nicht definierter Fall");
                LagisBroker.getInstance().flurstueckChangeFinished(FlurstueckChooser.this);
            }

            if ((currentWFSRetriever != null) && !currentWFSRetriever.isDone()) {
                currentWFSRetriever.cancel(false);
                currentWFSRetriever = null;
            }
            final HashMap<Integer, Boolean> properties = new HashMap<Integer, Boolean>();
            properties.put(WFSRequestJobDone.HAS_MANY_VERWALTUNGSBEREICHE, hasManyVerwaltungsbereiche);
            properties.put(WFSRequestJobDone.IS_NO_GEOMETRY_ASSIGNED, isNoGeometryAssigned);
            currentWFSRetriever = WFSRetrieverFactory.getInstance()
                        .getWFSRetriever(newFlurstueck.getFlurstueckSchluessel(), new WFSRequestJobDone(), properties);
            // currentWFSRetriever = new
            // WFSRetriever(newFlurstueck.getFlurstueckSchluessel(),hasManyVerwaltungsbereiche,isNoGeometryAssigned);
            LagisBroker.getInstance().execute(currentWFSRetriever);
        } catch (Exception ex) {
            log.error("Fehler beim Flurstückchange FlurstueckChooser --> Keine Geometrie wird abgerufen");
            LagisBroker.getInstance().flurstueckChangeFinished(FlurstueckChooser.this);
        }
    }

    @Override
    public Element getConfiguration() {
        return null;
    }

    @Override
    public void masterConfigure(final Element parent) {
        try {
            wfsRequest = (Element)parent.getChild("WFSRequest").clone();
            wfsRequest.detach();

            final Format format = Format.getPrettyFormat();
            // TODO: WHY NOT USING UTF-8
            format.setEncoding("ISO-8859-1"); // NOI18N
            final XMLOutputter serializer = new XMLOutputter(format);
            if (log.isDebugEnabled()) {
                log.debug("WFSRequest: " + serializer.outputString(wfsRequest));
            }
            if (log.isDebugEnabled()) {
                log.debug("Child availaible: "
                            + parent.getChild("WFSRequest").getChild("Query").getChild(
                                "GetFeature",
                                Namespace.getNamespace("wfs", "http://www.opengis.net/wfs")));
            }
            query = (Element)parent.getChild("WFSRequest")
                        .getChild("Query")
                        .getChild("GetFeature", Namespace.getNamespace("wfs", "http://www.opengis.net/wfs"))
                        .clone();
            final List childs = query.getChild("Query", Namespace.getNamespace("wfs", "http://www.opengis.net/wfs"))
                        .getChild("Filter", Namespace.getNamespace("", "http://www.opengis.net/ogc"))
                        .getChild("And", Namespace.getNamespace("", "http://www.opengis.net/ogc"))
                        .getChildren("PropertyIsEqualTo", Namespace.getNamespace("", "http://www.opengis.net/ogc"));
            hostname = parent.getChild("WFSRequest").getChild("Hostname").getText();
            if (log.isDebugEnabled()) {
                log.debug("WFSHostname: " + hostname);
                log.debug("Child list: " + childs);
            }
            if ((childs != null) && (childs.size() > 0)) {
                final Iterator<Element> it = childs.iterator();
                while (it.hasNext()) {
                    final Element currentElement = it.next();
                    final Element name = currentElement.getChild(
                            "PropertyName",
                            Namespace.getNamespace("", "http://www.opengis.net/ogc"));
                    if (log.isDebugEnabled()) {
                        log.debug("Name: " + name.getText());
                    }
                    if (name.getText().equals("app:gem")) {
                        gemarkung = currentElement.getChild(
                                "Literal",
                                Namespace.getNamespace("", "http://www.opengis.net/ogc"));
                        if (log.isDebugEnabled()) {
                            log.debug("Gemarkung Literal gesetzt: " + gemarkung);
                        }
                    } else if (name.getText().equals("app:flur")) {
                        flur = currentElement.getChild(
                                "Literal",
                                Namespace.getNamespace("", "http://www.opengis.net/ogc"));
                        if (log.isDebugEnabled()) {
                            log.debug("Flur Literal gesetzt: " + flur);
                        }
                    } else if (name.getText().equals("app:flurstz")) {
                        flurstZaehler = currentElement.getChild(
                                "Literal",
                                Namespace.getNamespace("", "http://www.opengis.net/ogc"));
                        if (log.isDebugEnabled()) {
                            log.debug("Flur Zähler Literal gesetzt: " + flurstZaehler);
                        }
                    } else if (name.getText().equals("app:flurstn")) {
                        flurstNenner = currentElement.getChild(
                                "Literal",
                                Namespace.getNamespace("", "http://www.opengis.net/ogc"));
                        if (log.isDebugEnabled()) {
                            log.debug("Flur Nenner Literal gesetzt: " + flurstNenner);
                        }
                    } else {
                        log.warn("Unbekanntes Literal");
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Fehler bei der Konfiguration der WFSQuery/Request", ex);
        }
    }

    @Override
    public void configure(final Element parent) {
    }

    /**
     * funktioniert nicht weil die Worker null sein können private void startSwingWorker(final SwingWorker
     * swingWorker,final boolean mayInterruptIfRunning,ActionEvent event,Object ActionObject){ SwingWorker worker;
     * if(swingWorker != null && swingWorker instanceof FlurRetriever){ if(currentFlurRetriever != null &&
     * !currentFlurRetriever.isDone()){ currentFlurRetriever.cancel(mayInterruptIfRunning); currentFlurRetriever = null;
     * } Object selectedItem = cboFlur.getSelectedItem(); currentFlurRetriever = new FlurRetriever(event,selectedItem);
     * worker = currentFlurstueckRetriever; } else if(swingWorker instanceof FlurRetriever){ Object selectedItem =
     * cboFlur.getSelectedItem(); currentFlurRetriever = new FlurRetriever(event,selectedItem); worker =
     * currentFlurstueckRetriever; } else if(swingWorker != null && swingWorker instanceof FlurstueckRetriever){
     * if(currentFlurstueckRetriever != null && !currentFlurstueckRetriever.isDone()){
     * currentFlurstueckRetriever.cancel(mayInterruptIfRunning); currentFlurstueckRetriever = null; } Object
     * selectedItem = cboFlurstueck.getSelectedItem(); currentFlurstueckRetriever = new
     * FlurstueckRetriever(event,selectedItem); worker = currentFlurstueckRetriever; } else if(swingWorker instanceof
     * FlurstueckRetriever){ Object selectedItem = cboFlurstueck.getSelectedItem(); currentFlurstueckRetriever = new
     * FlurstueckRetriever(event,selectedItem); worker = currentFlurstueckRetriever; } else { log.warn("Unbekannter
     * SwingWorker "+swingWorker); return; } worker.execute(); }.
     *
     * @param  color  DOCUMENT ME!
     */
    private synchronized void setHighlightColor(final Color color) {
        currentColor = color;
        LagisBroker.warnIfThreadIsNotEDT();
        cboGemarkung.getEditor().getEditorComponent().setBackground(color);
        cboFlur.getEditor().getEditorComponent().setBackground(color);
        cboFlurstueck.getEditor().getEditorComponent().setBackground(color);
        txtFlurstueck.setBackground(color);
    }

    @Override
    public void focusLost(final FocusEvent e) {
        if (log.isDebugEnabled()) {
            log.debug("Focus lost: ");
        }
        final Component eventComponent = e.getComponent();
        if (log.isDebugEnabled()) {
            log.debug("Component: " + eventComponent);
//        if((currentAutomaticRetriever == null || currentAutomaticRetriever.isDone() || currentAutomaticRetriever.isCancelled()) &&
//            (currentFlurRetriever == null || currentFlurRetriever.isDone() || currentFlurRetriever.isCancelled()) &&
//                (currentFlurstueckRetriever == null || currentFlurstueckRetriever.isDone() || currentFlurstueckRetriever.isCancelled()) &&
//                (currentGemarkungsRetriever == null || currentGemarkungsRetriever.isDone() || currentGemarkungsRetriever.isCancelled()) &&
//                (currentWFSRetriever == null || currentWFSRetriever.isDone() || currentWFSRetriever.isCancelled())
//        ){
//             log.debug("Kein Thread am laufen --> keine Aktion");
//            if(eventComponent.equals(cboGemarkung.getEditor().getEditorComponent())){
//                log.debug("Gemarkungscomboboxeditor hat den Fokus verloren");
//            } else if(eventComponent.equals(cboFlur.getEditor().getEditorComponent())){
//                log.debug("Flurcomboboxeditor hat den Fokus verloren");
//            } else if(eventComponent.equals(cboFlurstueck.getEditor().getEditorComponent())){
//                log.debug("Flurstueckcomboboxeditor hat den Fokus verloren");
//            }
//        } else {
//            log.debug("focus lost aber Aktion im gange");
//        }
            log.debug("isTemporary : " + e.isTemporary());
        }
    }

    @Override
    public void focusGained(final FocusEvent e) {
        if (log.isDebugEnabled()) {
            log.debug("Focus gained: ");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mode  DOCUMENT ME!
     * @param  key   DOCUMENT ME!
     */
    public void doAutomaticRequest(final int mode, final FlurstueckSchluesselCustomBean key) {
        final Color oldColor = currentColor;
        if (log.isDebugEnabled()) {
            log.debug("oldColor = " + oldColor);
            log.debug("doAutomatic Request");
            log.debug("mode: " + mode);
        }
        LagisBroker.warnIfThreadIsNotEDT();
        if ((currentAutomaticRetriever != null) && !currentAutomaticRetriever.isDone()) {
            currentAutomaticRetriever.cancel(false);
            currentAutomaticRetriever = null;
        }
        if ((AutomaticFlurstueckRetriever.FILTER_ACTION_MODE == mode)
                    || (AutomaticFlurstueckRetriever.COPY_CONTENT_MODE == mode)
                    || ((AutomaticFlurstueckRetriever.FLURSTUECK_REQUEST_MODE == mode) && (key != null))) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Entferne ComboboxListener");
                }
                cboGemarkung.removeActionListener(gemarkungListener);
                cboFlur.removeActionListener(flurListener);
                cboFlurstueck.removeActionListener(flurstueckListener);
                if (log.isDebugEnabled()) {
                    log.debug("automaticRequestLock locked");
                }
                automaticRequestLock.lock();
                currentAutomaticRetriever = new AutomaticFlurstueckRetriever(mode, key, oldColor);
                LagisBroker.getInstance().execute(currentAutomaticRetriever);
                automaticRequestLock.unlock();
                if (log.isDebugEnabled()) {
                    log.debug("automaticRequestLock unlocked");
                    log.debug("Automatic request with key");
                }

                if ((currentGemarkungsRetriever != null) && !currentGemarkungsRetriever.isDone()
                            && (currentGemarkungsRetriever.getMode()
                                != currentGemarkungsRetriever.RETRIEVE_GEMARKUNGEN_MODE)) {
                    currentGemarkungsWaiter.interrupt();
                    currentGemarkungsWaiter = null;
                    currentGemarkungsRetriever.cancel(false);
                    currentGemarkungsRetriever = null;
                } else if ((currentGemarkungsRetriever != null) && !currentGemarkungsRetriever.isDone()
                            && (currentGemarkungsRetriever.getMode()
                                == currentGemarkungsRetriever.RETRIEVE_GEMARKUNGEN_MODE)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Gemarkungsretrieval ist am laufen warte");
                    }
                    if (currentGemarkungsWaiter != null) {
                        currentGemarkungsWaiter.interrupt();
                        currentGemarkungsWaiter = null;
                    }
                    currentGemarkungsWaiter = new Thread() {

                            @Override
                            public void run() {
                                if (log.isDebugEnabled()) {
                                    log.debug("Waiter started");
                                }
                                while (!currentGemarkungsRetriever.isDone()) {
                                    if (isInterrupted()) {
                                        if (log.isDebugEnabled()) {
                                            log.debug("GemarkungsWaiter wurde Unterbrochen");
                                        }
                                        return;
                                    }
                                    try {
                                        sleep(100);
                                    } catch (InterruptedException ex) {
                                        if (log.isDebugEnabled()) {
                                            log.debug("GemarkungsWaiter wurde Unterbrochen");
                                        }
                                        return;
                                    }
                                }
                                if (isInterrupted()) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("GemarkungsWaiter wurde Unterbrochen");
                                    }
                                    return;
                                }
                                if (log.isDebugEnabled()) {
                                    log.debug("Gemarkungsretrieval ist beendet starte Automatic");
                                }
                                EventQueue.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            cboGemarkung.setSelectedItem(key.getGemarkung());
                                            currentGemarkungsRetriever = null;
                                            currentGemarkungsRetriever = new GemarkungRetriever(
                                                    GemarkungRetriever.RETRIEVE_AUTOMATIC_MODE,
                                                    null,
                                                    key.getGemarkung());
                                            setPropertyChangeListener(currentGemarkungsRetriever);
                                            LagisBroker.getInstance().execute(currentGemarkungsRetriever);
                                        }
                                    });
                            }
                        };
                    currentGemarkungsWaiter.start();
                    return;
                }
            } catch (Exception ex) {
                log.error("Fehler in AutomaticRequest FilterAcion --> setze Listener", ex);
                cboGemarkung.addActionListener(gemarkungListener);
                cboFlur.addActionListener(flurListener);
                cboFlurstueck.addActionListener(flurstueckListener);
            }
            cboGemarkung.setSelectedItem(key.getGemarkung());
            currentGemarkungsRetriever = new GemarkungRetriever(
                    GemarkungRetriever.RETRIEVE_AUTOMATIC_MODE,
                    null,
                    key.getGemarkung());
            setPropertyChangeListener(currentGemarkungsRetriever);
            LagisBroker.getInstance().execute(currentGemarkungsRetriever);
        } else if ((AutomaticFlurstueckRetriever.SET_BOXES_ACCORDING_TO_CONTENT_MODE == mode) && (key == null)) {
            if (log.isDebugEnabled()) {
                log.debug("automatic request with key == null");
            }
            if (cboFlur.isEnabled()) {
                if (log.isDebugEnabled()) {
                    log.debug("Flurcombobox enabled --> reset");
                }
                // TODO WHAT IF Flurstück is UNKNOWN
                cboFlur.setSelectedItem(cboFlur.getSelectedItem());
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Flurcombobox disabled --> nothing todo");
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Unknown mode");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckSchluesselCustomBean getCurrentFlurstueckSchluessel() {
        if (log.isDebugEnabled()) {
            log.debug("getCurrentFlurstueckSchluessel()", new CurrentStackTrace());
        }
        final FlurstueckSchluesselCustomBean key = FlurstueckSchluesselCustomBean.createNew();
        if ((currentMode == CREATION_MODE) && (currentyCreatedFlurstueckSchluessel != null)) {
            return currentyCreatedFlurstueckSchluessel;
        }
        if ((currentFlurstueck != null) && (currentFlurstueck.getFlurstueckSchluessel() != null)) {
            if (log.isDebugEnabled()) {
                log.debug("Aktuelles Flurstück kommt aus Datenbank und besitzt einen Schlüssel");
            }
            return currentFlurstueck.getFlurstueckSchluessel();
        } else {
            if (log.isDebugEnabled()) {
                log.debug("aktuelles Flurstueck besitzt keinen Schlüssel versuche zu konstruieren");
            }
        }
        final Object flurstueck = cboFlurstueck.getSelectedItem();
        if ((flurstueck != null) && (flurstueck instanceof FlurstueckSchluesselCustomBean)) {
            final FlurstueckSchluesselCustomBean tmpKey = ((FlurstueckSchluesselCustomBean)flurstueck);
            key.setGemarkung(tmpKey.getGemarkung());
            key.setFlur(tmpKey.getFlur());
            key.setFlurstueckZaehler(tmpKey.getFlurstueckZaehler());
            key.setFlurstueckNenner(tmpKey.getFlurstueckNenner());
            key.setId(tmpKey.getId());
        } else {
            return null;
        }
        return key;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getMode() {
        return currentMode;
    }

    @Override
    public void requestFlurstueck(final FlurstueckSchluesselCustomBean key) {
        if (log.isDebugEnabled()) {
            log.debug("Flurstück Request with key");
        }
        doAutomaticRequest(AutomaticFlurstueckRetriever.FLURSTUECK_REQUEST_MODE, key);
    }

    @Override
    public void updateFlurstueckKeys() {
        if (log.isDebugEnabled()) {
            log.debug("updateFlurstückKeys");
        }
        final FlurstueckCustomBean currentFlurstueck = LagisBroker.getInstance().getCurrentFlurstueck();
        final Object currentKey = cboFlurstueck.getSelectedItem();
        if (currentFlurstueck != null) {
            if (log.isDebugEnabled()) {
                log.debug("currentFlurstück != null --> Keys werden geupdated " + currentFlurstueck);
            }
            doAutomaticRequest(
                AutomaticFlurstueckRetriever.FILTER_ACTION_MODE,
                currentFlurstueck.getFlurstueckSchluessel());
        } else if ((currentKey != null) && (currentKey instanceof FlurstueckSchluesselCustomBean)) {
            if (log.isDebugEnabled()) {
                log.debug("currentFlurstück == null --> Flurstück unbekannt update Keys");
            }
            doAutomaticRequest(
                AutomaticFlurstueckRetriever.FILTER_ACTION_MODE,
                (FlurstueckSchluesselCustomBean)currentKey);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Kein Schlüssel vorhanden --> resete Keys");
            }
            doAutomaticRequest(AutomaticFlurstueckRetriever.SET_BOXES_ACCORDING_TO_CONTENT_MODE, null);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key  DOCUMENT ME!
     */
    public void addRemoveFilter(final FlurstueckSchluesselCustomBean key) {
        removeFilter.add(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key  DOCUMENT ME!
     */
    public void removeRemoveFilter(final FlurstueckSchluesselCustomBean key) {
        removeFilter.remove(key);
    }

    @Override
    public void removeUpdate(final DocumentEvent e) {
        if (log.isDebugEnabled()) {
            log.debug("Insert/Remove Update");
        }
        currentyCreatedFlurstueckSchluessel = FlurstueckSchluesselCustomBean.createNew();
        final Object currentFlur = cboFlur.getSelectedItem();
        if (log.isDebugEnabled()) {
            log.debug("Flur: " + currentFlur);
            log.debug("Object Class: " + currentFlur.getClass());
        }
        if ((currentFlur != null) && (currentFlur instanceof FlurKey)) {
            currentyCreatedFlurstueckSchluessel.setGemarkung(((FlurKey)currentFlur).getGemarkung());
            currentyCreatedFlurstueckSchluessel.setFlur(((FlurKey)currentFlur).getFlurId());
            final String text = txtFlurstueck.getText();
            if (log.isDebugEnabled()) {
                log.debug("Flurstück String changed: " + text);
            }
            if (text != null) {
                if (text.length() != 0) {
                    final String[] tokens = text.split("/");
                    if (log.isDebugEnabled()) {
                        // TODO the user input is not validated
                        log.debug("Anzahl teile der Flurstücksid: " + tokens.length);
                    }
                    try {
                        switch (tokens.length) {
                            case 1: {
                                if (log.isDebugEnabled()) {
                                    log.debug("Eine Zahl");
                                }
                                currentyCreatedFlurstueckSchluessel.setFlurstueckZaehler(Integer.parseInt(tokens[0]));
                                currentyCreatedFlurstueckSchluessel.setFlurstueckNenner(0);
                                setHighlightColor(LagisBroker.ACCEPTED_COLOR);
                                txtFlurstueck.setToolTipText("Flurstück");
                                creationValidationMessage = "Flurstück ist valide";
                                isFlurstueckCandidateValide = true;
                                checkIfFlurstueckIsAlreadyInDatabase(currentyCreatedFlurstueckSchluessel, true);
                                break;
                            }
                            case 2: {
                                if (log.isDebugEnabled()) {
                                    log.debug("Zwei Zahlen");
                                }
                                currentyCreatedFlurstueckSchluessel.setFlurstueckZaehler(Integer.parseInt(tokens[0]));
                                currentyCreatedFlurstueckSchluessel.setFlurstueckNenner(Integer.parseInt(tokens[1]));
                                setHighlightColor(LagisBroker.ACCEPTED_COLOR);
                                txtFlurstueck.setToolTipText("Flurstück");
                                creationValidationMessage = "Flurstück ist valide";
                                isFlurstueckCandidateValide = true;
                                checkIfFlurstueckIsAlreadyInDatabase(currentyCreatedFlurstueckSchluessel, true);
                                break;
                            }
                            default: {
                                log.warn("Falsche Eingabe erwarted wird ein Flurstueck ohne oder mit Nenner z.B. 10\n");
                                setHighlightColor(LagisBroker.ERROR_COLOR);
                                txtFlurstueck.setToolTipText("Es ist nur ein Teiler / erlaubt");
                                creationValidationMessage = txtFlurstueck.getToolTipText();
                                isFlurstueckCandidateValide = false;
                                fireValidationStateChanged(this);
                            }
                        }
                    } catch (Exception ex) {
                        log.error("Fehler beim parsen des Flurstück Zähler/Nenner", ex);
                        txtFlurstueck.setToolTipText(
                            "Kein gültiger Flurstücksname. Gültige Namen sind z.B. 3, 3/0 , 3/15");
                        creationValidationMessage = txtFlurstueck.getToolTipText();
                        setHighlightColor(LagisBroker.ERROR_COLOR);
                        isFlurstueckCandidateValide = false;
                        fireValidationStateChanged(this);
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Länge des eingegebenen Strings ist = 0");
                    }
                    setHighlightColor(LagisBroker.ERROR_COLOR);
                    txtFlurstueck.setToolTipText("Bitte geben Sie ein Flurstück ein.");
                    creationValidationMessage = txtFlurstueck.getToolTipText();
                    isFlurstueckCreateable = false;
                    fireValidationStateChanged(this);
                }
            } else {
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Flur==null || !instanceof FlurKey");
            }
            isFlurstueckCandidateValide = false;
        }
    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
        removeUpdate(e);
    }

    @Override
    public void changedUpdate(final DocumentEvent e) {
    }

    @Override
    public String getWidgetName() {
        return WIDGET_NAME;
    }

    @Override
    public String getValidationMessage() {
        if (currentMode == SEARCH_MODE) {
            if (currentFlurstueck != null) {
                return "Es wurde ein Flurstück ausgewäählt";
            } else {
                return "Es wurde kein Flurstück ausgewählt";
            }
        } else if ((currentMode == CONTINUATION_MODE) || (currentMode == CONTINUATION_HISTORIC_MODE)) {
            if (currentFlurstueck != null) {
                return "Aktuell ausgewähltes Flurstück vollständig.";
            } else {
                return "Bitte vervollständigen Sie alle Flurstücke";
            }
        } else if (currentMode == CREATION_MODE) {
            return creationValidationMessage;
        } else {
            return "Unbekannter Modus";
        }
    }

    @Override
    public int getStatus() {
        if (log.isDebugEnabled()) {
            log.debug("getStatus()");
        }
        if (currentMode == SEARCH_MODE) {
            if (log.isDebugEnabled()) {
                log.debug("SEARCHMODE");
            }
            if (currentFlurstueck != null) {
                return Validatable.VALID;
            } else {
                return Validatable.ERROR;
            }
        } else if ((currentMode == CONTINUATION_MODE) || (currentMode == CONTINUATION_HISTORIC_MODE)) {
            if (log.isDebugEnabled()) {
                log.debug("CONTINUATION_MODE");
            }
            if (currentFlurstueck != null) {
                return Validatable.VALID;
            } else {
                return Validatable.ERROR;
            }
        } else if (currentMode == CREATION_MODE) {
            if (log.isDebugEnabled()) {
                log.debug("CREATION_MODE");
            }
            if (isFlurstueckCreateable && isFlurstueckCandidateValide) {
                return Validatable.VALID;
            } else {
                return Validatable.ERROR;
            }
        } else {
            return Validatable.ERROR;
        }
    }
    /**
     * TODO TIMER with delay private Thread checker;
     *
     * @param  key                 DOCUMENT ME!
     * @param  isFlurstueckValide  DOCUMENT ME!
     */
    private void checkIfFlurstueckIsAlreadyInDatabase(final FlurstueckSchluesselCustomBean key,
            final boolean isFlurstueckValide) {
        if ((currentFlurstueckChecker != null) && !currentFlurstueckChecker.isDone()) {
            currentFlurstueckChecker.cancel(false);
            currentFlurstueckChecker = null;
        }
        currentFlurstueckChecker = new FlurstueckChecker(key, isFlurstueckValide);
        LagisBroker.getInstance().execute(currentFlurstueckChecker);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnActionActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnActionActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_btnActionActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  listener  DOCUMENT ME!
     */
    public void addComboBoxListener(final ActionListener listener) {
        cboGemarkung.addActionListener(listener);
        cboFlur.addActionListener(listener);
        cboFlurstueck.addActionListener(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  listener  DOCUMENT ME!
     */
    public void removeActionListener(final ActionListener listener) {
        cboGemarkung.removeActionListener(listener);
        cboFlur.removeActionListener(listener);
        cboFlurstueck.removeActionListener(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public javax.swing.JComboBox getCboGemarkung() {
        return cboGemarkung;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public javax.swing.JComboBox getCboFlur() {
        return cboFlur;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JComboBox getCboFlurstueck() {
        return this.cboFlurstueck;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Icon getStatusIcon() {
        return btnAction.getIcon();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  icon  DOCUMENT ME!
     */
    public void setStatusIcon(final Icon icon) {
        btnAction.setIcon(icon);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class GemarkungRetriever extends SwingWorker<Vector<Key>, Void> {

        //~ Static fields/initializers -----------------------------------------

        private static final int RETRIEVE_GEMARKUNGEN_MODE = 0;
        private static final int RETRIEVE_FLURE_MODE = 1;
        private static final int RETRIEVE_WITH_RESOLVED_MODE = 2;
        private static final int RETRIEVE_AUTOMATIC_MODE = 3;

        //~ Instance fields ----------------------------------------------------

        private int mode;
        private ActionEvent event;
        private Object selectedItem;
        private GemarkungCustomBean selectedGemarkung;
        private boolean wasResolved = false;
        private boolean hadErrors = false;
        private boolean isAutoComplete = false;
        private String errorMessage = null;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new GemarkungRetriever object.
         *
         * @param  mode  DOCUMENT ME!
         */
        public GemarkungRetriever(final int mode) {
            this(mode, null, null);
        }

        /**
         * Creates a new GemarkungRetriever object.
         *
         * @param  mode          DOCUMENT ME!
         * @param  event         DOCUMENT ME!
         * @param  selectedItem  DOCUMENT ME!
         */
        public GemarkungRetriever(final int mode, final ActionEvent event, final Object selectedItem) {
            this.mode = mode;
            this.event = event;
            this.selectedItem = selectedItem;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Vector<Key> doInBackground() throws Exception {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("GemarkungsRetrieverWorker started");
                }
                if (RETRIEVE_GEMARKUNGEN_MODE == mode) {
                    // Abrufen aller Gemarkungen
                    if (isCancelled()) {
                        if (log.isDebugEnabled()) {
                            log.debug("doInBackground (Gemarkung) is canceled");
                        }
                        return null;
                    }
                    final Collection gemKeys = EJBroker.getInstance().getGemarkungsKeys();
                    if (isCancelled()) {
                        if (log.isDebugEnabled()) {
                            log.debug("doInBackground (Gemarkung) is canceled");
                        }
                        return null;
                    }
                    Vector<Key> gemKeyList = null;
                    if (gemKeys != null) {
                        if (log.isDebugEnabled()) {
                            log.debug("Gemarkungen vorhanden: " + gemKeys.size());
                        }
                        gemKeyList = new Vector(gemKeys);
                        if (isCancelled()) {
                            if (log.isDebugEnabled()) {
                                log.debug("doInBackground (Gemarkung) is canceled");
                            }
                            return null;
                        }
                        Collections.sort((Vector)gemKeyList);
                        if (log.isDebugEnabled()) {
                            log.debug("Collection sortiert");
                        }
                    }
                    if (gemKeyList == null) {
                        if (log.isDebugEnabled()) {
                            log.debug("gemKeyList == null");
                        }
                        gemKeyList = new Vector();
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("return from Backgroung");
                    }
                    return gemKeyList;
                } else if ((((RETRIEVE_FLURE_MODE == mode) || (RETRIEVE_WITH_RESOLVED_MODE == mode))
                                && (event != null))
                            || (RETRIEVE_AUTOMATIC_MODE == mode)) {
                    // GemarkungCustomBean wurde ausgewählt
                    if ((RETRIEVE_AUTOMATIC_MODE == mode) || (event.getSource() instanceof JComboBox)) {
                        if ((RETRIEVE_AUTOMATIC_MODE == mode) || event.getActionCommand().equals("comboBoxChanged")
                                    || (RETRIEVE_WITH_RESOLVED_MODE == mode)) {
                            if (event != null) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Gemarkungsmodifier: " + event.getModifiers());
                                }
                            }
                            if (((RETRIEVE_AUTOMATIC_MODE == mode)
                                            || ((event.getModifiers() & InputEvent.BUTTON1_MASK) != 0))
                                        || ((event.getModifiers() != 0)
                                            && ((event.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0))
                                        || (RETRIEVE_WITH_RESOLVED_MODE == mode)) {
                                if (selectedItem instanceof GemarkungCustomBean) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Instanz ist eine Gemarkung");
                                    }
                                    selectedGemarkung = (GemarkungCustomBean)selectedItem;
                                    if (isCancelled()) {
                                        if (log.isDebugEnabled()) {
                                            log.debug("doInBackground (Gemarkung) is canceled");
                                        }
                                        return null;
                                    }
                                    if (log.isDebugEnabled()) {
                                        log.debug("Vor EJB aufruf");
                                        log.debug("Gemarkung");
                                        log.debug("Bezeichnung: " + selectedGemarkung.getBezeichnung());
                                    }
                                    if (log.isDebugEnabled()) {
                                        log.debug("Id: " + selectedGemarkung.getId());
                                    }
                                    if (log.isDebugEnabled()) {
                                        log.debug("Schluessel: " + selectedGemarkung.getSchluessel());
                                    }
                                    EventQueue.invokeLater(new Runnable() {

                                            @Override
                                            public void run() {
                                                pbGemarkung.setIndeterminate(true);
                                            }
                                        });

                                    final Collection flurKeys = EJBroker.getInstance()
                                                .getDependingKeysForKey(selectedGemarkung);
                                    if (isCancelled()) {
                                        if (log.isDebugEnabled()) {
                                            log.debug("doInBackground (Gemarkung) is canceled");
                                        }
                                        return null;
                                    }
                                    if (log.isDebugEnabled()) {
                                        log.debug("Nach EJB aufruf");
                                    }
                                    if (flurKeys != null) {
                                        if (log.isDebugEnabled()) {
                                            log.debug("Flure vorhanden: " + flurKeys.size());
                                        }
                                        if (isCancelled()) {
                                            if (log.isDebugEnabled()) {
                                                log.debug("doInBackground (Gemarkung) is canceled");
                                            }
                                            return null;
                                        }
                                        final Vector flurKeyList = new Vector(flurKeys);
                                        if (isCancelled()) {
                                            if (log.isDebugEnabled()) {
                                                log.debug("doInBackground (Gemarkung) is canceled");
                                            }
                                            return null;
                                        }
                                        Collections.sort(flurKeyList);
                                        return flurKeyList;
                                    } else {
                                        if (log.isDebugEnabled()) {
                                            log.debug("Keine Flure vorhanden");
                                        }
                                        return new Vector<Key>();
                                    }
                                } else {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Instanz ist keine Gemarkung");
                                    }
                                    selectedGemarkung = null;
                                    return null;
                                }
                            } else {
                                if (log.isDebugEnabled()) {
                                    log.debug("Autocomplete Gemarkung");
                                }
                                isAutoComplete = true;
                                selectedGemarkung = null;
                                return null;
                            }
                        } else if (event.getActionCommand().equals("comboBoxEdited")) {
                            if (log.isDebugEnabled()) {
                                log.debug("Gemarkung wurde über Combobox editiert, versuche zu resolven");
                            }
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        pbGemarkung.setIndeterminate(true);
                                    }
                                });

                            final String gemInput = ((JComboBox)event.getSource()).getEditor().getItem().toString();
                            try {
                                if (isCancelled()) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("doInBackground (Gemarkung) is canceled");
                                    }
                                    return null;
                                }
                                selectedGemarkung = LagisBroker.getInstance()
                                            .getGemarkungForKey(Integer.parseInt(gemInput));
                                if (isCancelled()) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("doInBackground (Gemarkung) is canceled");
                                    }
                                    return null;
                                }
                            } catch (NumberFormatException ex) {
                                if ((gemInput != null) && gemInput.equals("")) {
                                    // Eingabe ist Leerstring;
                                    selectedGemarkung = null;
                                    return null;
                                } else {
                                    selectedGemarkung = GemarkungCustomBean.createNew();
                                    selectedGemarkung.setBezeichnung(gemInput);
                                    if (isCancelled()) {
                                        if (log.isDebugEnabled()) {
                                            log.debug("doInBackground (Gemarkung) is canceled");
                                        }
                                        return null;
                                    }
                                    selectedGemarkung = EJBroker.getInstance().completeGemarkung(selectedGemarkung);
                                }
                            }
                            if ((selectedGemarkung != null) && (selectedGemarkung.getId() != null)) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Gemarkung konnte vervollständigt werden");
                                }
                                wasResolved = true;
                                return null;
                            } else {
                                if (log.isDebugEnabled()) {
                                    log.debug("Gemarkung konnte nicht vervollständingt werden");
                                }
                                hadErrors = true;
                                errorMessage = "Unbekannte Gemarkung";
                                selectedGemarkung = null;
                                return null;
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("Unbekanntes Actionkommando");
                            }
                            selectedGemarkung = null;
                            return null;
                        }
                    } else {
                        log.warn("Eventsource nicht bekannt");
                        selectedGemarkung = null;
                        return null;
                    }
                } else {
                    log.warn("unkown mode oder Event == null oder selectedItem == null");
                    selectedGemarkung = null;
                    return null;
                }
            } catch (Exception ex) {
                log.error("Fehler beim Abrufen der Gemarkungen/Flure", ex);
                return null;
            }
        }

        @Override
        protected void done() {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("GemarkungRetriever done()");
                }
                pbGemarkung.setIndeterminate(false);
                if (isAutoComplete) {
                    if (log.isDebugEnabled()) {
                        log.debug("Autocomplete Gemarkung done");
                    }
                    cboFlur.setModel(new KeyComboboxModel());
                    cboFlur.setEnabled(false);
                    return;
                }
                if (isCancelled()) {
                    if (log.isDebugEnabled()) {
                        log.debug("GemarkungRetriever was canceled (done)");
                    }
                    return;
                }
                if (mode == RETRIEVE_GEMARKUNGEN_MODE) {
                    if (log.isDebugEnabled()) {
                        log.debug("Retrieve Gemarkung done");
                    }
                    // Setzen der Gemarkungen
                    try {
                        cboGemarkung.setModel(new KeyComboboxModel(get()));
//                if(currentMode != CONTINUATION_MODE || (isDynamicCreated)){
                        cboGemarkung.setEnabled(true);

                        // }
                        if (log.isDebugEnabled()) {
                            log.debug("IsFullInitialized");
                        }
                        isFullInitialized = true;
                        if (currentGemarkungsWaiter == null) {
                            cboGemarkung.requestFocus();
                        }
                    } catch (final Exception ex) {
                        log.error("Fehler beim setzten der Gemarkungen", ex);
                    }
                } else if ((RETRIEVE_FLURE_MODE == mode) || (RETRIEVE_WITH_RESOLVED_MODE == mode)
                            || (RETRIEVE_AUTOMATIC_MODE == mode)) {
                    if (selectedGemarkung != null) {
                        if (!wasResolved) {
                            setHighlightColor(LagisBroker.ACCEPTED_COLOR);
                            cboFlur.setModel(new KeyComboboxModel(get()));
                            cboFlur.setEnabled(true);
                            if (RETRIEVE_AUTOMATIC_MODE != mode) {
                                cboFlur.requestFocus();
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("resolving finished (done)");
                            }
                            // ToDO betterway ?
                            cboGemarkung.removeActionListener(gemarkungListener);
                            cboGemarkung.setSelectedItem(selectedGemarkung);
                            cboGemarkung.addActionListener(gemarkungListener);
                            setHighlightColor(LagisBroker.ACCEPTED_COLOR);
                            cboGemarkungActionPerformed(event);
                        }
                    } else {
                        if (hadErrors) {
                            if (log.isDebugEnabled()) {
                                log.debug("Es gab bei der Eingabe einen Fehler");
                            }
                            btnAction.setToolTipText(errorMessage);
                            setHighlightColor(LagisBroker.ERROR_COLOR);
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("Selected Gemarkung == null");
                            }
                            setHighlightColor(LagisBroker.ACCEPTED_COLOR);
                            btnAction.setToolTipText("");
                        }
                        cboFlur.setModel(new KeyComboboxModel());
                        cboFlur.setEnabled(false);
                    }
                    // GemarkungCustomBean wurde ausgewählt
                } else {
                    log.warn("unkown mode");
                }
            } catch (final Exception ex) {
                log.error("Fehler beim setzten der Gemarkung/Flure (done)", ex);
                setHighlightColor(LagisBroker.ERROR_COLOR);
                cboFlur.setModel(new KeyComboboxModel());
                cboFlur.setEnabled(false);
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public int getMode() {
            return mode;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class FlurRetriever extends SwingWorker<Vector<Key>, Void> {

        //~ Static fields/initializers -----------------------------------------

        private static final int RETRIEVE_NORMAL_MODE = 0;
        private static final int RETRIEVE_AUTOMATIC_MODE = 1;

        //~ Instance fields ----------------------------------------------------

        private int mode;
        private boolean isAutoComplete = false;
        private FlurKey selectedFlur;
        private String errorMessage = null;
        private boolean hadErrors = false;
        private final Object selectedItem;
        private final ActionEvent event;
        private boolean flurWasCreated = false;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new FlurRetriever object.
         *
         * @param  mode          DOCUMENT ME!
         * @param  event         DOCUMENT ME!
         * @param  selectedItem  DOCUMENT ME!
         */
        public FlurRetriever(final int mode, final ActionEvent event, final Object selectedItem) {
            this.mode = mode;
            this.event = event;
            this.selectedItem = selectedItem;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Vector<Key> doInBackground() throws Exception {
            try {
                if ((RETRIEVE_AUTOMATIC_MODE == mode) || (event.getSource() instanceof JComboBox)) {
                    if ((RETRIEVE_AUTOMATIC_MODE == mode) || event.getActionCommand().equals("comboBoxChanged")) {
                        if ((RETRIEVE_AUTOMATIC_MODE == mode) || ((event.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
                                    || ((event.getModifiers() != 0)
                                        && ((event.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0))) {
                            if (selectedItem instanceof FlurKey) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Instanz ist eine FlurKey");
                                }
                                selectedFlur = (FlurKey)selectedItem;
                                if (isCancelled()) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("doInBackground (Flur) is canceled");
                                    }
                                    return null;
                                }
                            } else {
                                if (log.isDebugEnabled()) {
                                    log.debug("Instanz ist kein Flur");
                                }
                                selectedFlur = null;
                                return null;
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("Autocomplete Flur");
                            }
                            isAutoComplete = true;
                            selectedFlur = null;
                            return null;
                        }
                    } else if (event.getActionCommand().equals("comboBoxEdited")) {
                        if (log.isDebugEnabled()) {
                            log.debug("Flur wurde über Combobox editiert");
                        }
                        final String flurInput = ((JComboBox)event.getSource()).getEditor().getItem().toString();
                        final FlurKey tmp;
                        try {
                            if ((flurInput != null) && flurInput.equals("")) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Flur wurde auf \"\" gesetzt");
                                }
                                selectedFlur = null;
                                return null;
                            }
                            final GemarkungCustomBean currentGemarkung = (GemarkungCustomBean)
                                cboGemarkung.getSelectedItem();
                            if ((currentGemarkung != null) && (currentGemarkung instanceof GemarkungCustomBean)) {
                                selectedFlur = new FlurKey(currentGemarkung, Integer.parseInt(flurInput));
                                final KeyComboboxModel flurModel = ((KeyComboboxModel)cboFlur.getModel());
                                if ((currentMode == SEARCH_MODE) || (currentMode == CREATION_MODE)) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Flur unbekannt --> wird angelegt");
                                    }
                                    if (!flurModel.contains(selectedFlur)) {
                                        flurWasCreated = true;
                                    }
                                } else if (flurModel.contains(selectedFlur)) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("geparster Flur existiert --> rufe Flurstücke ab");
                                    }
                                } else {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Flur unbekannt --> abruf nicht möglich");
                                    }
                                    hadErrors = true;
                                    errorMessage = "Der eingegebene Flur existiert nicht";
                                    return null;
                                }
                            } else {
                                if (log.isDebugEnabled()) {
                                    log.debug("kann keinen FlurKey erstellen Gemarkung == null");
                                }
                                selectedFlur = null;
                                return null;
                            }
                        } catch (Exception ex) {
                            if (log.isDebugEnabled()) {
                                log.debug(
                                    "Fehler beim erstellen des FlurKeys --> keine Möglichkeit Flurstücke zu bestimmen",
                                    ex);
                            }
                            hadErrors = true;
                            errorMessage = "Fehlerhaftes Format des Flurs.";
                            selectedFlur = null;
                            return null;
                        }
                        if (isCancelled()) {
                            if (log.isDebugEnabled()) {
                                log.debug("doInBackground (Flur) is canceled");
                            }
                            return null;
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Unbekanntes Actionkommando");
                        }
                        selectedFlur = null;
                        return null;
                    }
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                pbFlur.setIndeterminate(true);
                            }
                        });
                    if (log.isDebugEnabled()) {
                        log.debug("Vor EJB aufruf");
                        log.debug("Flur");
                        log.debug("Gemarkung: " + selectedFlur.getGemarkung());
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Id: " + selectedFlur.getFlurId());
                    }
                    if (log.isDebugEnabled()) {
                        // TODO Implement Filter
                        log.debug("Filter aktuelle Flurstücke: " + isOnlyCurrentFilterEnabled);
                        log.debug("Filter historic Flurstücke: " + isOnlyHistoricFilterEnabled);
                    }
                    selectedFlur.setCurrentFilterEnabled(isOnlyCurrentFilterEnabled);
                    selectedFlur.setHistoricFilterEnabled(isOnlyHistoricFilterEnabled);
                    selectedFlur.setAbteilungXIFilterEnabled(isOnlyAbteilungIXFilterEnabled);
                    selectedFlur.setStaedtischFilterEnabled(isOnlyStaedtischFilterEnabled);
                    final Collection flurKeys = EJBroker.getInstance().getDependingKeysForKey(selectedFlur);
                    if (isCancelled()) {
                        if (log.isDebugEnabled()) {
                            log.debug("doInBackground (Flur) is canceled");
                        }
                        return null;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Nach EJB aufruf");
                    }
                    if (flurKeys != null) {
                        if (log.isDebugEnabled()) {
                            log.debug("Flurstücke vorhanden: " + flurKeys.size());
                        }
                        if (isCancelled()) {
                            if (log.isDebugEnabled()) {
                                log.debug("doInBackground (Flur) is canceled");
                            }
                            return null;
                        }
                        final Vector flurKeyList = new Vector(flurKeys);
                        if (isCancelled()) {
                            if (log.isDebugEnabled()) {
                                log.debug("doInBackground (Flur) is canceled");
                            }
                            return null;
                        }
                        Collections.sort(flurKeyList);
                        // TODO RemoveFilter

                        return flurKeyList;
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Keine Flurstücke vorhanden");
                        }
                        return new Vector<Key>();
                    }
                } else {
                    log.warn("Eventsource nicht bekannt");
                    selectedFlur = null;
                    return null;
                }
            } catch (Exception ex) {
                log.error("Fehler beim Abrufen der Flurstücke", ex);
            }
            selectedFlur = null;
            return null;
        }

        @Override
        protected void done() {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("FlurRetriever done()");
                }
                pbFlur.setIndeterminate(false);
                if (isAutoComplete) {
                    if (log.isDebugEnabled()) {
                        log.debug("Autocomplete Flur (done)");
                    }
                    cboFlurstueck.setModel(new KeyComboboxModel());
                    synchronized (isFlurstueckCreateable) {
                        isFlurstueckCreateable = false;
                    }
                    setHighlightColor(LagisBroker.ACCEPTED_COLOR);
                    txtFlurstueck.setText("");
                    cboFlurstueck.setEnabled(false);
                    txtFlurstueck.setEnabled(false);
                    return;
                }
                if (isCancelled()) {
                    if (log.isDebugEnabled()) {
                        log.debug("FlurRetriever was canceled (done)");
                    }
                    return;
                }
                if ((selectedFlur != null) && !hadErrors) {
                    if (log.isDebugEnabled()) {
                        log.debug("selectedFlur != null");
                    }
                    final KeyComboboxModel flurModel = ((KeyComboboxModel)cboFlur.getModel());
                    if ((currentMode == CREATION_MODE) && flurWasCreated) {
                        if (log.isDebugEnabled()) {
                            log.debug("nue angelegter Flur wird zum Model hinzugefügt");
                        }
                        flurModel.addElement(selectedFlur);
                    }
                    setHighlightColor(LagisBroker.ACCEPTED_COLOR);
                    final KeyComboboxModel model = new KeyComboboxModel(get());
                    final Iterator<FlurstueckSchluesselCustomBean> it = removeFilter.iterator();
                    while (it.hasNext()) {
                        model.removeElement(it.next());
                    }
                    setHighlightColor(LagisBroker.ACCEPTED_COLOR);
                    cboFlurstueck.setModel(model);
                    cboFlurstueck.setEnabled(true);
                    txtFlurstueck.setEnabled(true);
                    if ((currentAutomaticRetriever == null) || currentAutomaticRetriever.isDone()
                                || currentAutomaticRetriever.isCancelled()) {
                        if (log.isDebugEnabled()) {
                            log.debug("Kein AutomaticRequest fordere Focus");
                        }
                        if (currentMode == CREATION_MODE) {
                            txtFlurstueck.requestFocus();
                        } else {
                            cboFlurstueck.requestFocus();
                        }
                    } else if ((currentAutomaticRetriever != null)
                                && (currentAutomaticRetriever.getMode()
                                    == AutomaticFlurstueckRetriever.COPY_CONTENT_MODE)) {
                        if (log.isDebugEnabled()) {
                            log.debug("AutomaticRequest mit Focus");
                        }
                        cboFlurstueck.requestFocus();
                    }
                } else {
                    if (hadErrors) {
                        if (log.isDebugEnabled()) {
                            log.debug("Es gab bei der Eingabe einen Fehler");
                        }
                        setHighlightColor(LagisBroker.ERROR_COLOR);
                        btnAction.setToolTipText(errorMessage);
                    } else {
                        setHighlightColor(LagisBroker.ACCEPTED_COLOR);
                        btnAction.setToolTipText("");
                        if (log.isDebugEnabled()) {
                            log.debug("Selected Flur == null");
                        }
                    }
                    synchronized (isFlurstueckCreateable) {
                        isFlurstueckCreateable = false;
                    }
                    txtFlurstueck.setText("");
                    cboFlurstueck.setModel(new KeyComboboxModel());
                    cboFlurstueck.setEnabled(false);
                    txtFlurstueck.setEnabled(false);
                }
            } catch (final Exception ex) {
                log.error("Fehler beim setzten der Flurstücke (done)", ex);
                setHighlightColor(LagisBroker.ERROR_COLOR);
                synchronized (isFlurstueckCreateable) {
                    isFlurstueckCreateable = false;
                }
                txtFlurstueck.setText("");
                cboFlurstueck.setModel(new KeyComboboxModel());
                cboFlurstueck.setEnabled(false);
                txtFlurstueck.setEnabled(false);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class FlurstueckRetriever extends SwingWorker<FlurstueckCustomBean, Void> {

        //~ Static fields/initializers -----------------------------------------

        private static final int RETRIEVE_NORMAL_MODE = 0;
        private static final int RETRIEVE_AUTOMATIC_MODE = 1;

        //~ Instance fields ----------------------------------------------------

        private int mode;
        private boolean isAutoComplete = false;
        private FlurstueckSchluesselCustomBean selectedFlurstueck;
        private String errorMessage = null;
        private boolean hadErrors = false;
        private boolean isFlurstueckInDatabase = true;
        private final Object selectedItem;
        private final ActionEvent event;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new FlurstueckRetriever object.
         *
         * @param  mode          DOCUMENT ME!
         * @param  event         DOCUMENT ME!
         * @param  selectedItem  DOCUMENT ME!
         */
        public FlurstueckRetriever(final int mode, final ActionEvent event, final Object selectedItem) {
            this.mode = mode;
            this.event = event;
            this.selectedItem = selectedItem;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected FlurstueckCustomBean doInBackground() throws Exception {
            try {
                if ((RETRIEVE_AUTOMATIC_MODE == mode) || (event.getSource() instanceof JComboBox)) {
                    if (RETRIEVE_AUTOMATIC_MODE != mode) {
                        if (log.isDebugEnabled()) {
                            log.debug("modifiers: " + event.getModifiers());
                        }
                    }
                    if ((RETRIEVE_AUTOMATIC_MODE == mode) || event.getActionCommand().equals("comboBoxChanged")) {
                        if ((RETRIEVE_AUTOMATIC_MODE == mode) || ((event.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
                                    || ((event.getModifiers() != 0)
                                        && ((event.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0))) {
                            if (selectedItem instanceof FlurstueckSchluesselCustomBean) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Instanz ist eine FlurstueckSchluessel");
                                }
                                selectedFlurstueck = (FlurstueckSchluesselCustomBean)selectedItem;
                                if (isCancelled()) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("doInBackground (FlurstueckRetriever) is canceled");
                                    }
                                    return null;
                                }
                            } else {
                                if (log.isDebugEnabled()) {
                                    log.debug("Instanz ist keie Flurstückschlüssel");
                                }
                                selectedFlurstueck = null;
                                return null;
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("Autocomplete Flurstück");
                            }
                            isAutoComplete = true;
                            selectedFlurstueck = null;
                            return null;
                        }
                    } else if (event.getActionCommand().equals("comboBoxEdited")) {
                        if (log.isDebugEnabled()) {
                            log.debug("Flurstück wurde über Combobox editiert");
                            log.debug("FlurstueckKey != null");
                        }
                        selectedFlurstueck = FlurstueckSchluesselCustomBean.createNew();
                        final Object tmpFlur = cboFlur.getSelectedItem();
                        final FlurKey currentFlur;
                        if (tmpFlur instanceof FlurKey) {
                            currentFlur = (FlurKey)tmpFlur;
                        } else if (tmpFlur instanceof String) {
                            currentFlur = new FlurKey((GemarkungCustomBean)cboGemarkung.getSelectedItem(),
                                    Integer.parseInt((String)tmpFlur));
                        } else {
                            log.warn("Unbekanntes Objekt in cboFlur");
                            currentFlur = null;
                        }
                        if (currentFlur == null) {
                            if (log.isDebugEnabled()) {
                                log.debug("FlurstückSchlüssel kann nicht konstruiert werden --> Flur == null");
                            }
                            selectedFlurstueck = null;
                            return null;
                        }
                        selectedFlurstueck.setGemarkung(currentFlur.getGemarkung());

                        selectedFlurstueck.setFlur(currentFlur.getFlurId());
                        final String flurstueck = cboFlurstueck.getEditor().getItem().toString();
                        if (log.isDebugEnabled()) {
                            log.debug("Flurstueck String: " + flurstueck);
                        }
                        if (isCancelled()) {
                            if (log.isDebugEnabled()) {
                                log.debug("doInBackground (FlurstueckRetriever) is canceled");
                            }
                            return null;
                        }
                        if ((flurstueck != null) && (flurstueck.length() > 0)) {
                            try {
                                final String[] tokens = flurstueck.split("/");
                                if (log.isDebugEnabled()) {
                                    // TODO the user input is not validated
                                    log.debug("Anzahl teile der Flurstücksid: " + tokens.length);
                                }
                                switch (tokens.length) {
                                    case 1: {
                                        if (log.isDebugEnabled()) {
                                            log.debug("Eine Zahl");
                                        }
                                        selectedFlurstueck.setFlurstueckZaehler(Integer.parseInt(tokens[0]));
                                        selectedFlurstueck.setFlurstueckNenner(0);
                                        break;
                                    }
                                    case 2: {
                                        if (log.isDebugEnabled()) {
                                            log.debug("Zwei Zahlen");
                                        }
                                        selectedFlurstueck.setFlurstueckZaehler(Integer.parseInt(tokens[0]));
                                        selectedFlurstueck.setFlurstueckNenner(Integer.parseInt(tokens[1]));
                                        break;
                                    }
                                    default: {
                                        log.warn(
                                            "Falsche Eingabe erwarted wird ein Flurstueck ohne oder mit Nenner z.B. 10\n");
                                        selectedFlurstueck = null;
                                        return null;
                                    }
                                }
                            } catch (NumberFormatException ex) {
                                log.error("Fehler beim parsen des FlurstückSchlüssels", ex);
                                hadErrors = true;
                                errorMessage = "Format des Flurstückszähler/-nenner stimmt nicht (Zahl/Zahl)";
                                selectedFlurstueck = null;
                                return null;
                            }
                        } else {
                            if ((flurstueck != null) && flurstueck.equals("")) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Flurstück wird auf \"\" gesetzt");
                                }
                                selectedFlurstueck = null;
                                return null;
                            } else {
                                log.warn("Unbekannter Fall");
                                selectedFlurstueck = null;
                                return null;
                            }
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Unbekanntes Actionkommando");
                        }
                        selectedFlurstueck = null;
                        return null;
                    }
                    if (isCancelled()) {
                        if (log.isDebugEnabled()) {
                            log.debug("doInBackground (FlurstueckRetriever) is canceled");
                        }
                        return null;
                    }
                    final FlurstueckSchluesselCustomBean tmpKey = EJBroker.getInstance()
                                .completeFlurstueckSchluessel(selectedFlurstueck);
                    if (isCancelled()) {
                        if (log.isDebugEnabled()) {
                            log.debug("doInBackground (FlurstueckRetriever) is canceled");
                        }
                        return null;
                    }
                    // Ist Flurstück in Datenbank ??
                    if (tmpKey != null) {
                        if (log.isDebugEnabled()) {
                            log.debug("Flurstück ist in der Datenbank vorhanden");
                        }
                        selectedFlurstueck = tmpKey;
                    }

                    if (selectedFlurstueck.getId() != null) {
                        if (currentMode == SEARCH_MODE) {
                            if (log.isDebugEnabled()) {
                                log.debug("SearchMode --> Frage Flurstück ab");
                            }
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        pbFlurstueck.setIndeterminate(true);
                                        pbTxtFlurstueck.setIndeterminate(true);
                                    }
                                });

                            if (!LagisBroker.getInstance().isInEditMode()) {
                                if (log.isDebugEnabled()) {
                                    log.debug("nich in editmode");
                                    log.debug("breche WFSretrieval ab");
                                }
                                // wfsUpdater.notifyThread(null);
                                if (isCancelled()) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("doInBackground (FlurstueckRetriever) is canceled");
                                    }
                                    return null;
                                }
                                if (log.isDebugEnabled()) {
                                    log.debug("rufe Flurstück vom Server ab");
                                }
                                final FlurstueckCustomBean flurstueck = EJBroker.getInstance()
                                            .retrieveFlurstueck(selectedFlurstueck);
                                if (isCancelled()) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("doInBackground (FlurstueckRetriever) is canceled");
                                    }
                                    return null;
                                }
                                if (log.isDebugEnabled()) {
                                    log.debug("Flurstueck vom Server erhalten:\n " + flurstueck);
                                }
                                // TODO notice user if there is no flurstueck
                                if (flurstueck != null) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Flurstück abruf erfolgreich");
                                    }
                                    return flurstueck;
                                } else {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Abgefragtes Flurstueck ist null");
                                    }
                                    selectedFlurstueck = null;
                                    return null;
                                }
                            } else {
                                if (isCancelled()) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("doInBackground (FlurstueckRetriever) is canceled");
                                    }
                                    return null;
                                }
                                if (log.isDebugEnabled()) {
                                    log.debug("LagIS ist in Editmode --> Flurstück kann nicht gewechselt werden");
                                }
                                JOptionPane.showMessageDialog(LagisBroker.getInstance().getParentComponent(),
                                    "Das Flurstück kann nur gewechselt werden wenn alle Änderungen gespeichert oder verworfen worden sind.",
                                    "Wechseln nicht möglich",
                                    JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug(
                                    "FlurstueckChooser ist nicht im SearchMode --> Flurstück wird nur abgefragt");
                            }
                            final FlurstueckCustomBean flurstueck = EJBroker.getInstance()
                                        .retrieveFlurstueck(selectedFlurstueck);
                            if (flurstueck != null) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Flurstück abruf erfolgreich");
                                }
                                return flurstueck;
                            } else {
                                if (log.isDebugEnabled()) {
                                    log.debug("Abgefragtes Flurstueck ist null");
                                }
                                selectedFlurstueck = null;
                                return null;
                            }
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Es wird nach einem in der Datenbank nicht vorhandenen Flurstück gesucht");
                        }
                        if ((currentMode == CONTINUATION_MODE) || (currentMode == CONTINUATION_HISTORIC_MODE)) {
                            if (log.isDebugEnabled()) {
                                log.debug("Continuation Mode  --> ungültige Eingabe");
                            }
                            hadErrors = true;
                            errorMessage = "Das eingegebene Flurstück existiert nicht";
                            return null;
                        } else {
                            final FlurstueckCustomBean container = FlurstueckCustomBean.createNew();
                            container.setFlurstueckSchluessel(selectedFlurstueck);
                            isFlurstueckInDatabase = false;
                            return container;
                        }
                    }
                } else {
                    log.warn("Eventsource nicht bekannt");
                    selectedFlurstueck = null;
                    return null;
                }
            } catch (Exception ex) {
                log.error("Fehler beim Abrufen des Flurstücks", ex);
                hadErrors = true;
                errorMessage = "Flurstück konnte nicht abgerufen werden";
            }
            selectedFlurstueck = null;
            return null;
        }

        @Override
        protected void done() {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("FlurstueckRetriever done()");
                }
                pbFlurstueck.setIndeterminate(false);
                pbTxtFlurstueck.setIndeterminate(false);
                if (isAutoComplete) {
                    if (log.isDebugEnabled()) {
                        log.debug("Autocomplete Flurstueck (done)");
                    }
                    return;
                }
                if (isCancelled()) {
                    if (log.isDebugEnabled()) {
                        log.debug("FlurRetriever was canceled (done)");
                    }
                    return;
                }

                if ((selectedFlurstueck != null) && !hadErrors) {
                    final FlurstueckCustomBean result = get();
                    currentFlurstueck = result;
                    if (result != null) {
                        if (isFlurstueckInDatabase) {
                            setHighlightColor(LagisBroker.SUCCESSFUL_COLOR);
                            if (currentMode == SEARCH_MODE) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Flurstück konnte erfolgreich abgerufen werden wird gesetzt");
                                }
                                LagisBroker.getInstance().fireFlurstueckChanged(result);
                            } else if ((currentMode == CONTINUATION_MODE)
                                        || (currentMode == CONTINUATION_HISTORIC_MODE)) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Flurstück konnte erfolgreich abgerufen werden --> nichts passiert");
                                }
                            }
                        } else {
                            if (currentMode == SEARCH_MODE) {
                                if (log.isDebugEnabled()) {
                                    log.debug("starte WFSThread für unbekanntes Flurstück zu suchen (done)");
                                }
                                setHighlightColor(LagisBroker.UNKOWN_COLOR);
                                LagisBroker.getInstance()
                                        .setCurrentFlurstueckSchluessel(result.getFlurstueckSchluessel(), true);
                                LagisBroker.getInstance().resetWidgets();
                                LagisBroker.getInstance()
                                        .getMappingComponent()
                                        .getFeatureCollection()
                                        .removeAllFeatures();
                                if ((currentWFSRetriever != null) && !currentWFSRetriever.isDone()) {
                                    currentWFSRetriever.cancel(false);
                                    currentWFSRetriever = null;
                                }
                                final HashMap<Integer, Boolean> properties = new HashMap<Integer, Boolean>();
                                properties.put(WFSRequestJobDone.HAS_MANY_VERWALTUNGSBEREICHE, false);
                                properties.put(WFSRequestJobDone.IS_NO_GEOMETRY_ASSIGNED, true);
                                currentWFSRetriever = WFSRetrieverFactory.getInstance()
                                            .getWFSRetriever(result.getFlurstueckSchluessel(),
                                                    new WFSRequestJobDone(),
                                                    properties);
                                // currentWFSRetriever = new WFSRetriever(result.getFlurstueckSchluessel(),false,true);

                                LagisBroker.getInstance().execute(currentWFSRetriever);
                            }
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Flurstück konnte nicht abgerufen werden --> unbekannt");
                        }
                    }
                } else {
                    if (hadErrors) {
                        setHighlightColor(LagisBroker.ERROR_COLOR);
                        if (log.isDebugEnabled()) {
                            log.debug("Es gab bei der Eingabe einen Fehler");
                        }
                        btnAction.setToolTipText(errorMessage);
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Selected Flurstueck == null");
                        }
                        btnAction.setToolTipText("");
                    }
                }
            } catch (final Exception ex) {
                log.error("Fehler beim abrufen des Flurstücks (done)", ex);
                setHighlightColor(LagisBroker.ERROR_COLOR);
            }
            fireValidationStateChanged(FlurstueckChooser.this);
        }
    }

    /**
     * class WFSRetriever extends SwingWorker<Feature,Void>{ private int mode; private FlurstueckSchluesselCustomBean
     * flurstueckKey; private org.deegree2.model.feature.FeatureCollection featuresCollection; //private boolean
     * hasManyVerwal private boolean hasManyVerwaltungsbereiche; private boolean isNoGeometryAssigned=true; private
     * boolean wasFeatureAdded; private Feature currentFeature; private String errorMessage=null; private boolean
     * hadErrors=false; private boolean emptyResult=false; public WFSRetriever(final FlurstueckSchluesselCustomBean
     * flurstueckKey, final boolean hasManyVerwaltungsbereiche,final boolean isNoGeometryAssigned){ this.flurstueckKey =
     * flurstueckKey; this.hasManyVerwaltungsbereiche = hasManyVerwaltungsbereiche; this.isNoGeometryAssigned
     * =isNoGeometryAssigned; } protected Feature doInBackground() throws Exception { try{ if(flurstueckKey == null){
     * log.debug("WFS retrieval unterbrochen Schlüssel null"); return null; } if(!hasManyVerwaltungsbereiche &&
     * !isNoGeometryAssigned){ log.warn("Weniger als 2 Verwaltungsbereiche & Geometrie zugeordnet --> darf an dieser
     * Stelle nicht vorkommen"); log.debug("Es wird keine Geometrie in die Karte eingefügt"); return null; } else
     * if(hasManyVerwaltungsbereiche && !isNoGeometryAssigned){ log.warn("Mehr als 2 Verwaltungsbereiche & keine
     * Geometrien zugeordnet --> darf an dieser Stelle nicht vorkommen"); log.debug("Es wird keine Geometrie in die
     * Karte eingefügt"); return null; } Document doc = new Document();
     * gemarkung.setText(flurstueckKey.getGemarkung().getSchluessel().toString());
     * flur.setText(flurstueckKey.getFlur().toString());
     * flurstZaehler.setText(flurstueckKey.getFlurstueckZaehler().toString());
     * flurstNenner.setText(flurstueckKey.getFlurstueckNenner().toString()); if(isCancelled()){
     * log.debug("doInBackground (WFSRetriever) is canceled"); return null; } //is this the right way
     * doc.setRootElement((Element)query.clone()); XMLOutputter out = new XMLOutputter(); //out.setEncoding(encoding);
     * String postString = out.outputString(doc); log.debug("PostString für WFS :"+postString); HttpClient client = new
     * HttpClient(); String proxySet = System.getProperty("proxySet"); if(proxySet != null && proxySet.equals("true")){
     * log.debug("proxyIs Set"); log.debug("ProxyHost:"+System.getProperty("http.proxyHost"));
     * log.debug("ProxyPort:"+System.getProperty("http.proxyPort")); try {
     * client.getHostConfiguration().setProxy(System.getProperty("http.proxyHost"),
     * Integer.parseInt(System.getProperty("http.proxyPort"))); } catch(Exception e){ log.error("Problem while setting
     * proxy",e); } } if(isCancelled()){ log.debug("doInBackground (WFSRetriever) is canceled"); return null; }
     * PostMethod httppost = new PostMethod(hostname); httppost.setRequestEntity(new StringRequestEntity(postString));
     * if(isCancelled()){ log.debug("doInBackground (WFSRetriever) is canceled"); return null; } long start =
     * System.currentTimeMillis(); if(isCancelled()){ log.debug("doInBackground (WFSRetriever) is canceled"); return
     * null; } client.executeMethod(httppost); if(isCancelled()){ log.debug("doInBackground (WFSRetriever) is
     * canceled"); return null; } long stop = System.currentTimeMillis(); if(log.isEnabledFor(Priority.INFO))
     * log.info(((stop-start)/1000.0)+" Sekunden dauerte das getFeature Request "); int code = httppost.getStatusCode();
     * if (code == HttpStatus.SC_OK) { if(isCancelled()){ log.debug("doInBackground (WFSRetriever) is canceled");
     * httppost.releaseConnection(); return null; } InputStreamReader reader = new InputStreamReader(new
     * BufferedInputStream(httppost.getResponseBodyAsStream())); if(isCancelled()){ log.debug("doInBackground
     * (WFSRetriever) is canceled"); httppost.releaseConnection(); return null; } featuresCollection = parse(reader);
     * if(isCancelled()){ log.debug("doInBackground (WFSRetriever) is canceled"); httppost.releaseConnection(); return
     * null; } if(featuresCollection == null){ log.info("WFS Single Request brachte kein Ergebnis");
     * httppost.releaseConnection(); reader.close(); log.debug("FeatureCollection : "+featuresCollection);
     * emptyResult=true; } else { httppost.releaseConnection(); reader.close(); int featureSize =
     * featuresCollection.size(); if(featureSize == 0){ log.info("Feature Collection ist leer"); //TODO INFORM USER THAT
     * THERE IS NO WFS GEOMETRY AVAILABLE hadErrors=true; errorMessage="Es wurden keine Flurstücke zu dem angegebenen
     * Schlüssel gefunden"; return null; } else if(featureSize == 1){ log.debug("WFS Request erbrachte genau ein
     * Ergebnis"); //TODO // final DefaultStyledFeature feature = new DefaultStyledFeature() { // public boolean
     * isEditable() { // return false; // } // // public boolean canBeSelected() { // return false; // } // // public
     * Paint getFillingStyle(){ // return new java.awt.Color(43,106,21,150); // } // }; //TODO Duplicated code perhaps
     * one ifblock is enough if(!hasManyVerwaltungsbereiche && isNoGeometryAssigned){ log.debug("Weniger als 2
     * Verwaltungsbereiche & keine Geometrie zugeordnet"); log.debug("Es wird eine nicht veränderbare WfsGeometrie in
     * die Karte eingefügt"); DefaultStyledFeature tmpFeature = new DefaultStyledFeature();
     * tmpFeature.setEditable(false); tmpFeature.setCanBeSelected(false); tmpFeature.setFillingStyle(new
     * Color(43,106,21,150)); //log.fatal("FeatureCollection"+featuresCollection); //log.fatal("hoffentlich 2:
     * "+featuresCollection.getFeature(0).getGeometryPropertyValues().length);
     * tmpFeature.setGeometry(JTSAdapter.export(featuresCollection.getFeature(0).getDefaultGeometryPropertyValue()));
     * if(isCancelled()){ log.debug("doInBackground (WFSRetriever) is canceled"); httppost.releaseConnection(); return
     * null; } return tmpFeature; } else if(hasManyVerwaltungsbereiche && isNoGeometryAssigned){ log.debug("Mehr als 2
     * Verwaltungsbereiche & keine Geometrien zugeordnet"); log.debug("Es wird eine neue Geometrie zum zuordnen in die
     * Karte eingefügt"); PureNewFeature tmpFeature = new
     * PureNewFeature(JTSAdapter.export(featuresCollection.getFeature(0).getDefaultGeometryPropertyValue()));
     * tmpFeature.setEditable(true); tmpFeature.setCanBeSelected(true); if(isCancelled()){ log.debug("doInBackground
     * (WFSRetriever) is canceled"); httppost.releaseConnection(); return null; } return tmpFeature; } else {
     * log.warn("Nicht vorgesehner Fall !! --> Der Karte wird nichts hinzugefügt!"); return null; } } else {
     * log.info("WFS lieferte mehr als ein Ergebnis zurück: "+featureSize); hadErrors=true; errorMessage="Der WFS
     * lieferte mehrere Geometrien zurück"; return null; } } } else { log.debug("HTTP statuscode != ok: "+code);
     * httppost.releaseConnection(); } }catch(final Exception ex){ log.error("Fehler beim abrufen der WFS Geometrie
     * ",ex); } return null; } public org.deegree2.model.feature.FeatureCollection parse(InputStreamReader reader){ try
     * { log.debug("start parsing"); long start = System.currentTimeMillis(); //funktioniert weil nach der Methode
     * wieder ein Cancel kommt; if(isCancelled()){ log.debug("parse() (WFSRetriever) is canceled"); return null; }
     * GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument(); if(isCancelled()){
     * log.debug("parse()(WFSRetriever) is canceled"); return null; } doc.load(reader,"http://dummyID");
     * if(isCancelled()){ log.debug("parse()(WFSRetriever) is canceled"); return null; } log.debug("resultString
     * :"+doc.toString()); org.deegree2.model.feature.FeatureCollection tmp = doc.parse(); if(isCancelled()){
     * log.debug("parse()(WFSRetriever) is canceled"); return null; } long stop = System.currentTimeMillis();
     * log.info(((stop-start)/1000.0)+" Sekunden dauerte das parsen"); return tmp; } catch (Exception e) {
     * log.error("Fehler beim parsen der Features.",e); } return null; } protected void done() { } }
     *
     * @version  $Revision$, $Date$
     */
    class FlurstueckChecker extends SwingWorker<Boolean, Void> {

        //~ Instance fields ----------------------------------------------------

        private FlurstueckSchluesselCustomBean keyToCheck;
        private boolean isFlurstueckCandidateValide;
        private boolean hadErrors;
        private String errorMessage;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new FlurstueckChecker object.
         *
         * @param  keyToCheck                   DOCUMENT ME!
         * @param  isFlurstueckCandidateValide  DOCUMENT ME!
         */
        public FlurstueckChecker(final FlurstueckSchluesselCustomBean keyToCheck,
                final boolean isFlurstueckCandidateValide) {
            this.keyToCheck = keyToCheck;
            this.isFlurstueckCandidateValide = isFlurstueckCandidateValide;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Boolean doInBackground() throws Exception {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("createFlurstückCheck");
                }
                if (isCancelled()) {
                    if (log.isDebugEnabled()) {
                        log.debug("doInBackground (FlurstueckChecker) is canceled");
                    }
                    return null;
                }
                if (isFlurstueckCandidateValide) {
                    if (log.isDebugEnabled()) {
                        log.debug("Flurstückcandidate ist valide --> prüfe schlüssel");
                    }
                    keyToCheck = EJBroker.getInstance().completeFlurstueckSchluessel(keyToCheck);
                    if (keyToCheck != null) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Flurstückcandidate ist nicht valide --> kann nichts machen");
                    }
                    return false;
                }
            } catch (Exception ex) {
                log.error("Fehler beim checken des Flurstuecks: " + ex);
                hadErrors = true;
                errorMessage = "Fehler beim prüfen des Flurstücks";
                return false;
            }
        }

        @Override
        protected void done() {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("FlurstueckChecker done");
                }
                if (isCancelled()) {
                    if (log.isDebugEnabled()) {
                        log.debug("FlurstueckChecker was canceled (done)");
                    }
                    return;
                }
                if (hadErrors) {
                    if (log.isDebugEnabled()) {
                        log.debug("Es gab einen Fehler flurstück konnte nicht geprüft werden");
                    }
                    txtFlurstueck.setToolTipText(errorMessage);
                    isFlurstueckCreateable = false;
                    return;
                }
                if (isFlurstueckCandidateValide) {
                    final Boolean keyAlreadyExisiting = get();
                    if (keyAlreadyExisiting) {
                        if (log.isDebugEnabled()) {
                            log.debug("Flurstück ist bereits vorhanden");
                        }
                        setHighlightColor(LagisBroker.ERROR_COLOR);
                        txtFlurstueck.setToolTipText("Flurstück ist bereits vorhanden");
                        isFlurstueckCreateable = false;
                        creationValidationMessage = txtFlurstueck.getToolTipText();
                        fireValidationStateChanged(this);
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Flurstück ist noch nicht vorhanden und kann angelegt werden");
                        }
                        setHighlightColor(LagisBroker.SUCCESSFUL_COLOR);
                        txtFlurstueck.setToolTipText("Flurstück ist noch nicht vorhanden und kann angelegt werden");
                        creationValidationMessage = txtFlurstueck.getToolTipText();
                        isFlurstueckCreateable = true;
                        fireValidationStateChanged(this);
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Der Flurstücksschlüssel ist nicht valide");
                    }
                    txtFlurstueck.setToolTipText("Der Flurstücksschlüssel ist nicht valide");
                    creationValidationMessage = txtFlurstueck.getToolTipText();
                    isFlurstueckCreateable = false;
                    fireValidationStateChanged(this);
                }
            } catch (Exception ex) {
                log.error("Fehler beim checken des Flurstücks (done)", ex);
                isFlurstueckCreateable = false;
                fireValidationStateChanged(this);
            }
            if (log.isDebugEnabled()) {
                log.debug("ValidationMessage lock");
            }
            validationMessageLock.lock();
            creationValidationMessage = txtFlurstueck.getToolTipText();
            validationMessageLock.unlock();
            if (log.isDebugEnabled()) {
                log.debug("ValidationMessage unlock");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class WFSRequestJobDone implements DoneDelegate<Geometry, Void> {

        //~ Static fields/initializers -----------------------------------------

        private static final int HAS_MANY_VERWALTUNGSBEREICHE = 0;
        private static final int IS_NO_GEOMETRY_ASSIGNED = 1;

        //~ Methods ------------------------------------------------------------

        @Override
        public void jobDone(final ExtendedSwingWorker<Geometry, Void> worker,
                final HashMap<Integer, Boolean> properties) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("WFSRetriever done()");
                }
                if (worker.isCancelled()) {
                    if (log.isDebugEnabled()) {
                        log.debug("FlurRetriever was canceled (done)");
                    }
                    LagisBroker.getInstance().flurstueckChangeFinished(FlurstueckChooser.this);
                    return;
                }

                final Geometry result = worker.get();
                result.setSRID(25832);
                LagisBroker.getInstance().setCurrentWFSGeometry(result);
                log.info("CurrentWFSGeometry SRS=" + ((result != null) ? result.getSRID() : "?") + " " + result);
                if (worker.hadErrors()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Es gab Fehler: " + worker.getErrorMessage());
                    }
                    btnAction.setToolTipText(worker.getErrorMessage());
                    setHighlightColor(LagisBroker.ERROR_COLOR);
                    setStatusIcon(icoWFSWarn);
                    LagisBroker.getInstance().flurstueckChangeFinished(FlurstueckChooser.this);
                    return;
                } else {
                    btnAction.setToolTipText("");
                }

                if ((worker.getKeyObject() != null)
                            && (worker.getKeyObject() instanceof FlurstueckSchluesselCustomBean)) {
                    final FlurstueckSchluesselCustomBean flurstueckKey = (FlurstueckSchluesselCustomBean)
                        worker.getKeyObject();
                    if (log.isDebugEnabled()) {
                        log.debug("FlurstueckKey != null (done)");
                    }
                    if (result != null) {
                        if (log.isDebugEnabled()) {
                            log.debug("result != null");
                        }
//                        if(LagisBroker.getInstance().isUnkownFlurstueck()){
//                            LagisBroker.getInstance().getMappingComponent().getFeatureCollection().removeAllFeatures();
//                        } else {
//                            if(currentWfsFeature != null){
//                                LagisBroker.getInstance().getMappingComponent().getFeatureCollection().removeFeature(currentWfsFeature);
//                            }
//                        }
//                        if(hasManyVerwaltungsbereiche){
//                            currentWfsFeature = null;
//                        } else {
//                            currentWfsFeature = result;
//                        }
                        // TODO GUESSING ACHTUNG FALLS FEHLER
                        if (flurstueckKey.getId() == null) {
                            LagisBroker.getInstance().setCurrentFlurstueckSchluessel(flurstueckKey, true);
                        }
                        Feature tmpFeature = null;
                        if (properties != null) {
                            final boolean hasManyVerwaltungsbereiche =
                                (properties.get(HAS_MANY_VERWALTUNGSBEREICHE) != null)
                                ? properties.get(HAS_MANY_VERWALTUNGSBEREICHE) : false;
                            final boolean isNoGeometryAssigned = (properties.get(IS_NO_GEOMETRY_ASSIGNED) != null)
                                ? properties.get(IS_NO_GEOMETRY_ASSIGNED) : false;
                            if (!hasManyVerwaltungsbereiche && isNoGeometryAssigned) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Weniger als 2 Verwaltungsbereiche & keine Geometrie zugeordnet");
                                    log.debug("Es wird eine nicht veränderbare WfsGeometrie in die Karte eingefügt");
                                }
                                tmpFeature = new DefaultStyledFeature();
                                tmpFeature.setEditable(false);
                                ((DefaultStyledFeature)tmpFeature).setCanBeSelected(false);

                                final FlurstueckArtCustomBean flurstueckArt = flurstueckKey.getFlurstueckArt();
                                final DefaultStyledFeature styledFeature = (DefaultStyledFeature)tmpFeature;
                                final String flurstueckArtBez = (flurstueckArt != null) ? flurstueckArt
                                                .getBezeichnung() : null;
                                final Date gueltigBis = flurstueckKey.getGueltigBis();

                                if (flurstueckArt == null) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Flurstück ist unbekannt");
                                    }

                                    styledFeature.setFillingPaint(LagisBroker.UNKNOWN_FILLING_COLOR);
                                } else if (
                                    FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                                                flurstueckArtBez)
                                            && (gueltigBis == null)) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Flurstück ist städtisch");
                                    }

                                    styledFeature.setFillingPaint(LagisBroker.STADT_FILLING_COLOR);
                                } else if ((gueltigBis != null)
                                            && (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                                                    flurstueckArtBez)
                                                || FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX
                                                .equals(
                                                    flurstueckArtBez))) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Flurstück ist städtisch und historisch");
                                    }
                                    styledFeature.setFillingPaint(LagisBroker.HISTORIC_FLURSTUECK_COLOR);
                                } else if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX.equals(
                                                flurstueckArtBez)) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Flurstück ist nicht städtisch (Abteilung XI");
                                    }
                                    styledFeature.setFillingPaint(LagisBroker.ABTEILUNG_IX_FILLING_COLOR);
                                } else {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Flurstück ist unbekannt");
                                    }

                                    styledFeature.setFillingPaint(LagisBroker.UNKNOWN_FILLING_COLOR);
                                }

                                // log.fatal("FeatureCollection"+featuresCollection); log.fatal("hoffentlich 2:
                                // "+featuresCollection.getFeature(0).getGeometryPropertyValues().length);
                                tmpFeature.setGeometry(result);

                                tmpFeature = new StyledFeatureGroupWrapper((StyledFeature)tmpFeature,
                                        FEATURE_GRP,
                                        FEATURE_GRP);
                            } else if (hasManyVerwaltungsbereiche && isNoGeometryAssigned) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Mehr als 2 Verwaltungsbereiche & keine Geometrien zugeordnet");
                                    log.debug("Es wird eine neue Geometrie zum zuordnen in die Karte eingefügt");
                                }
                                tmpFeature = new PureNewFeature(result);
                                tmpFeature.setEditable(true);
                                ((PureNewFeature)tmpFeature).setCanBeSelected(true);

//                                tmpFeature = new FeatureGroupWrapper(
//                                        tmpFeature,
//                                        "VerwaltungsbereichCustomBean",
//                                        "VerwaltungsbereichCustomBean");
//
//                                log.fatal("created FeatureGroupWrapper");

                            } else {
                                log.warn("Nicht vorgesehner Fall !! --> Der Karte wird nichts hinzugefügt!");
                            }
                        } else {
                            log.error("Properties sind null --> kann kein Feature hinzufügen");
                        }
                        if (tmpFeature != null) {
                            LagisBroker.getInstance()
                                    .getMappingComponent()
                                    .getFeatureCollection()
                                    .addFeature(tmpFeature);
                            if (log.isDebugEnabled()) {
                                log.debug("WFS Feature oder VerwaltungsbereichsFeatuer added");
                            }
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("result == null (Bedingung nicht erfüllt) --> keine Geometrie vorhanden");
                        }
                        LagisBroker.getInstance().setCurrentWFSGeometry(null);
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("FlurstueckKey == null  oder keine FlurstueckSchluesselInstantz(done)");
                    }
                    LagisBroker.getInstance().setCurrentWFSGeometry(null);
                }
            } catch (final Exception ex) {
                log.error("Fehler beim abrufen der WFS Geometrie (done)", ex);
                LagisBroker.getInstance().setCurrentWFSGeometry(null);
            }
            if (log.isDebugEnabled()) {
                log.debug("FlurstueckChangeFinished WFSRetriever (done)");
            }
            LagisBroker.getInstance().flurstueckChangeFinished(FlurstueckChooser.this);
            // repaint();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public class AutomaticFlurstueckRetriever extends SwingWorker<Void, Void> implements PropertyChangeListener {

        //~ Static fields/initializers -----------------------------------------

        public static final int FLURSTUECK_REQUEST_MODE = 0;
        public static final int FILTER_ACTION_MODE = 1;
        public static final int SET_BOXES_ACCORDING_TO_CONTENT_MODE = 2;
        public static final int COPY_CONTENT_MODE = 3;

        //~ Instance fields ----------------------------------------------------

        private final Color initialColor;
        private int mode;
        private FlurstueckSchluesselCustomBean key;
        private boolean isFinished = false;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new AutomaticFlurstueckRetriever object.
         *
         * @param  mode  DOCUMENT ME!
         * @param  key   DOCUMENT ME!
         */
        public AutomaticFlurstueckRetriever(final int mode, final FlurstueckSchluesselCustomBean key) {
            this(mode, key, null);
        }

        /**
         * Creates a new AutomaticFlurstueckRetriever object.
         *
         * @param  mode          DOCUMENT ME!
         * @param  key           DOCUMENT ME!
         * @param  initialColor  DOCUMENT ME!
         */
        public AutomaticFlurstueckRetriever(final int mode,
                final FlurstueckSchluesselCustomBean key,
                final Color initialColor) {
            this.key = key;
            this.mode = mode;
            this.initialColor = initialColor;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Void doInBackground() throws Exception {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Automatic Request started");
                }
                if (key == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("WFS retrieval unterbrochen Schlüssel null");
                    }
                    return null;
                }
                while (!isCancelled() && !isFinished) {
                    if (log.isDebugEnabled()) {
                        log.debug("Automatic request läuft noch");
                        log.debug("isFinished: " + isFinished);
                    }
                    Thread.currentThread().sleep(100);
                }
                if (log.isDebugEnabled()) {
                    log.debug("doInBackground fertig");
                }
            } catch (Exception e) {
                log.error("Fehler beim automatischen abrufen eines Flurstücks.", e);
            }
            return null;
        }

        @Override
        protected void done() {
            if (log.isDebugEnabled()) {
                log.debug("Automatic Request (done)");
            }
            if (isCancelled()) {
                if (log.isDebugEnabled()) {
                    log.debug("automatic Request wurde gecanceled (done)");
                }
                return;
            } else if (key == null) {
                if (log.isDebugEnabled()) {
                    log.debug("FlurstueckSchluessel ist null flurstück konnte nicht abgerufen werden (done)");
                }
            } else if (isFinished) {
                if (log.isDebugEnabled()) {
                    log.debug("automatic Request finished (done)");
                }
            } else {
                log.warn("Fehler beim automatischen Request (done)");
            }
            if (log.isDebugEnabled()) {
                log.debug("Setze die Comboxenlistener wieder scharf (done)");
            }
            cboGemarkung.addActionListener(gemarkungListener);
            cboFlur.addActionListener(flurListener);
            cboFlurstueck.addActionListener(flurstueckListener);
            // repaint();
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            if (isCancelled()) {
                if (log.isDebugEnabled()) {
                    log.debug("AutomaticRequest is canceled");
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("PropertyChange AutomaticFlurstueckRetriever");
                log.debug("evt source " + evt.getSource());
            }
            if (log.isDebugEnabled()) {
                log.debug("Property name: " + evt.getPropertyName());
            }
            if (log.isDebugEnabled()) {
                log.debug("Property oldValue: " + evt.getOldValue());
            }
            if (log.isDebugEnabled()) {
                log.debug("Property newValue: " + evt.getNewValue());
            }
            if ((evt.getSource() instanceof GemarkungRetriever)
                        && evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                if (log.isDebugEnabled()) {
                    log.debug("Gemarkung ist automatisch gesetzt worden --> setze Flur");
                }
                // cboFlur.setSelectedItem(new FlurKey(key.getGemarkung(),key.getFlur()));
                if ((currentFlurRetriever != null) && !currentFlurRetriever.isDone()) {
                    currentFlurRetriever.cancel(false);
                    currentFlurRetriever = null;
                }

                final FlurKey newFlur = new FlurKey(key.getGemarkung(), key.getFlur());
                cboFlur.setSelectedItem(newFlur);
                currentFlurRetriever = new FlurRetriever(FlurRetriever.RETRIEVE_AUTOMATIC_MODE, null, newFlur);
                setPropertyChangeListener(currentFlurRetriever);
                LagisBroker.getInstance().execute(currentFlurRetriever);
            } else if ((evt.getSource() instanceof FlurRetriever)
                        && evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                if (log.isDebugEnabled()) {
                    log.debug("Flur ist automatisch gesetzt worden --> setze Flurstueck ?");
                }

                if ((AutomaticFlurstueckRetriever.FLURSTUECK_REQUEST_MODE == mode)
                            || ((!isOnlyCurrentFilterEnabled && !isOnlyHistoricFilterEnabled)
                                || (isOnlyCurrentFilterEnabled && (key.getGueltigBis() == null))
                                || (isOnlyHistoricFilterEnabled && (key.getGueltigBis() != null)))) {
                    if ((currentFlurstueckRetriever != null) && !currentFlurstueckRetriever.isDone()) {
                        currentFlurstueckRetriever.cancel(false);
                        currentFlurstueckRetriever = null;
                    }
                    if ((currentWFSRetriever != null) && !currentWFSRetriever.isDone()) {
                        currentWFSRetriever.cancel(false);
                        currentWFSRetriever = null;
                    }
                    cboFlurstueck.setSelectedItem(key);
                    cboGemarkung.setSelectedItem(key.getGemarkung());

                    if (AutomaticFlurstueckRetriever.FLURSTUECK_REQUEST_MODE == mode) {
                        if (log.isDebugEnabled()) {
                            log.debug("rufe Flurstueck ab");
                        }
                        currentFlurstueckRetriever = new FlurstueckRetriever(
                                FlurstueckRetriever.RETRIEVE_AUTOMATIC_MODE,
                                null,
                                key);

                        setPropertyChangeListener(currentFlurstueckRetriever);
                        LagisBroker.getInstance().execute(currentFlurstueckRetriever);
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Flurstueck muss nicht mehr abgerufen werden nur Filteraktion");
                        }
                        if (mode == COPY_CONTENT_MODE) {
                            if (log.isDebugEnabled()) {
                                log.debug("Fordere Focus für cboFlurstück");
                            }
                            cboFlurstueck.requestFocus();
                        }
                        if (initialColor != null) {
                            if (log.isDebugEnabled()) {
                                log.debug("Setze Farbe zurück auf ursprungswert: " + initialColor);
                            }
                            setHighlightColor(initialColor);
                        } else {
                            log.warn("Kann die Farbe nicht mehr darstellen == null");
                        }
                        this.isFinished = true;
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("aktuelles Flurstueck entspricht nicht dem Filterkriterum");
                        log.debug("Flurstueck kann nicht gesetzt --> automatic request is done");
                    }
                    LagisBroker.getInstance().fireFlurstueckChanged(null);
                    this.isFinished = true;
                }
            } else if ((evt.getSource() instanceof FlurstueckRetriever)
                        && evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                if (log.isDebugEnabled()) {
                    log.debug("Flurstueck ist gesetzt --> automatic request is done");
                }
                this.isFinished = true;
            } else {
                log.warn("Kein Propertychange auf das gehört wird");
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public int getMode() {
            return mode;
        }
    }
}
