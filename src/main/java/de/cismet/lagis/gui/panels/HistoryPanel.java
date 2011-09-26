/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * HistoryPanel.java
 *
 * Created on August 19, 2007, 12:22 PM
 */
package de.cismet.lagis.gui.panels;

import att.grappa.Element;
import att.grappa.Graph;
import att.grappa.GraphEnumeration;
import att.grappa.Grappa;
import att.grappa.GrappaAdapter;
import att.grappa.GrappaPanel;
import att.grappa.GrappaSupport;
import att.grappa.MultiClickListener.MultiClickListener;
import att.grappa.Parser;
import att.grappa.Subgraph;

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.PrintWriter;
import java.io.StringReader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.SpinnerNumberModel;

import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.interfaces.FlurstueckChangeListener;

import de.cismet.lagis.thread.BackgroundUpdateThread;

import de.cismet.lagis.widget.AbstractWidget;

import de.cismet.lagisEE.bean.LagisServerBean.HistoryLevel;
import de.cismet.lagisEE.bean.LagisServerBean.HistoryType;

import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.history.FlurstueckHistorie;

import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.NoWriteError;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class HistoryPanel extends AbstractWidget implements FlurstueckChangeListener, MultiClickListener, Configurable {

    //~ Static fields/initializers ---------------------------------------------

    // TODO Auslagern in ConfigFile
    private static final String DEFAULT_DOT_HEADER = "digraph G{\n";

    private static HistoryPanel instance;

    //~ Instance fields --------------------------------------------------------

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private StringBuffer dotGraphRepresentation = new StringBuffer();
    private String encodedDotGraphRepresentation;
    private Timer levelTimer = new Timer();
    private URL historyServerUrl = null;
    private HashMap<String, FlurstueckSchluessel> nodeToKeyMap = new HashMap<String, FlurstueckSchluessel>();
    private HashMap<String, String> pseudoKeys = new HashMap<String, String>();
    private Flurstueck currentFlurstueck;
    // private Thread panelRefresherThread;
    private BackgroundUpdateThread<Flurstueck> updateThread;
    // TODO THREAD
    // TODO NOT DIRECTLY OUTPUT THE ERRORS ON ERR
    // private double cellxcoordinate =
    private Graph graph = new Graph("Flurstück Historie");
    private GrappaPanel gp;
    private JScrollPane currentSP;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbxHistoryOptions;
    private javax.swing.JComboBox cbxHistoryType;
    private javax.swing.JCheckBox ckxHoldFlurstueck;
    private javax.swing.JCheckBox ckxScaleToFit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblDatumEnt;
    private javax.swing.JLabel lblDatumEntWert;
    private javax.swing.JLabel lblDatumHist;
    private javax.swing.JLabel lblDatumHistWert;
    private javax.swing.JLabel lblVisulaization;
    private javax.swing.JPanel panHistInfo;
    private javax.swing.JPanel panInformation;
    private javax.swing.JPanel panOptions;
    private javax.swing.JPanel panPlaceholder;
    private javax.swing.JSpinner sprLevels;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form HistoryPanel.
     */
    public HistoryPanel() {
        setIsCoreWidget(true);
        initComponents();
        gp = new GrappaPanel(graph);
        final SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 100, 1);
        sprLevels.setModel(model);
        sprLevels.setEnabled(false);
        final GrappaAdapter adapter = new GrappaAdapter();
        adapter.addMultiClickListener(this);
        gp.addGrappaListener(adapter);
        gp.setScaleToFit(true);
        graph.setEditable(false);
        currentSP = new JScrollPane(gp);
        add(currentSP, BorderLayout.CENTER);
        updateThread = new BackgroundUpdateThread<Flurstueck>() {

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
                        log.info("Konstruiere Flurstückhistoriengraph");
                        nodeToKeyMap = new HashMap<String, FlurstueckSchluessel>();
                        pseudoKeys = new HashMap<String, String>();
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        if (log.isDebugEnabled()) {
                            // ToDo remove Strings
                            log.debug("Erstelle Historien Anfrage:");
                        }
                        HistoryLevel level;
                        int levelCount = 0;
                        if (cbxHistoryOptions.getSelectedItem().equals("Direkte Vorgänger/Nachfolger")) {
                            level = HistoryLevel.DIRECT_RELATIONS;
                            if (log.isDebugEnabled()) {
                                log.debug("nur angrenzendte Flurstücke");
                            }
                        } else if (cbxHistoryOptions.getSelectedItem().equals("Begrenzte Tiefe")) {
                            level = HistoryLevel.CUSTOM;
                            levelCount = ((Number)sprLevels.getValue()).intValue();
                            if (log.isDebugEnabled()) {
                                log.debug("begrentze Tiefe mit " + levelCount + " Stufen");
                            }
                        } else {
                            level = HistoryLevel.All;
                            if (log.isDebugEnabled()) {
                                log.debug("Alle Levels");
                            }
                        }
                        HistoryType type;
                        if (cbxHistoryType.getSelectedItem().equals("Nur Nachfolger")) {
                            type = HistoryType.SUCCESSOR;
                            if (log.isDebugEnabled()) {
                                log.debug("nur Nachfolger");
                            }
                        } else if (cbxHistoryType.getSelectedItem().equals("Nur Vorgänger")) {
                            type = HistoryType.PREDECESSOR;
                            if (log.isDebugEnabled()) {
                                log.debug("nur Vorgänger");
                            }
                        } else {
                            type = HistoryType.BOTH;
                            if (log.isDebugEnabled()) {
                                log.debug("Vorgänger/Nachfolger");
                            }
                        }

                        final Set<FlurstueckHistorie> allEdges = EJBroker.getInstance()
                                    .getHistoryEntries(getCurrentObject().getFlurstueckSchluessel(),
                                        level,
                                        type,
                                        levelCount);

                        if ((allEdges != null) && (allEdges.size() > 0)) {
                            if (log.isDebugEnabled()) {
                                log.debug("Historie Graph hat: " + allEdges.size() + " Kanten");
                            }
                            final Iterator<FlurstueckHistorie> it = allEdges.iterator();
                            while (it.hasNext()) {
                                if (isUpdateAvailable()) {
                                    cleanup();
                                    return;
                                }
                                final FlurstueckHistorie currentEdge = it.next();
                                final String currentVorgaenger = currentEdge.getVorgaenger().toString();
                                final String currentNachfolger = currentEdge.getNachfolger().toString();
                                if (currentVorgaenger.startsWith("pseudo")) {
                                    pseudoKeys.put(currentVorgaenger, "    ");
                                }
                                if (currentNachfolger.startsWith("pseudo")) {
                                    pseudoKeys.put(currentNachfolger, "    ");
                                }
                                dotGraphRepresentation.append("\"" + currentVorgaenger + "\"" + "->" + "\""
                                            + currentNachfolger + "\"" + ";\n");
                                nodeToKeyMap.put("\"" + currentEdge.getVorgaenger().toString() + "\"",
                                    currentEdge.getVorgaenger().getFlurstueckSchluessel());
                                nodeToKeyMap.put("\"" + currentEdge.getNachfolger().toString() + "\"",
                                    currentEdge.getNachfolger().getFlurstueckSchluessel());
                            }
                            if (isUpdateAvailable()) {
                                cleanup();
                                return;
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("Historie Graph ist < 1 --> keine Historie");
                            }
                            dotGraphRepresentation.append("\"" + getCurrentObject() + "\"" + ";\n");
                            nodeToKeyMap.put("\"" + getCurrentObject() + "\"",
                                getCurrentObject().getFlurstueckSchluessel());
                            if (isUpdateAvailable()) {
                                cleanup();
                                return;
                            }
                        }
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        if (pseudoKeys.size() > 0) {
                            for (final String key : pseudoKeys.keySet()) {
                                dotGraphRepresentation.append("\"" + key + "\" [label=\"    \"]");
                            }
                            if (isUpdateAvailable()) {
                                cleanup();
                                return;
                            }
                        }
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                        dotGraphRepresentation.append("}");
                        encodedDotGraphRepresentation = GrappaSupport.encodeString(dotGraphRepresentation.toString());
                        if (log.isDebugEnabled()) {
                            log.debug("Erzeugte Dot Graph Darstellung: \n" + encodedDotGraphRepresentation);
                        }
                        log.info("Graph wurde erfogreich Konstruiert");
                        layoutGraph();
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                    } catch (Exception ex) {
                        log.error("Fehler im refresh thread: ", ex);
                    }
                }

                private void layoutGraph() {
                    log.info("Graph wird gelayoutet");
                    final Parser program = new Parser(new StringReader(encodedDotGraphRepresentation),
                            new PrintWriter(System.err));
                    try {
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }

                        program.parse();
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                    } catch (Exception ex) {
                        log.error("Fehler beim parsen des Graphen: ", ex);
                    }
                    graph = program.getGraph();
                    if (isUpdateAvailable()) {
                        cleanup();
                        return;
                    }
                    graph.setEditable(false);
                    log.info("The graph contains " + graph.countOfElements(Grappa.NODE | Grappa.EDGE | Grappa.SUBGRAPH)
                                + " elements.");
                    // JScrollPane jsp = new JScrollPane();
                    //
                    if (isUpdateAvailable()) {
                        cleanup();
                        return;
                    }

                    doURLLayout();
                    if (isUpdateAvailable()) {
                        cleanup();
                        return;
                    }

                    gp.refresh(graph);
                    final GraphEnumeration ge = gp.getSubgraph().elements(Subgraph.NODE);
                    while (ge.hasMoreElements()) {
                        final Element curNode = ge.nextGraphElement();
                        if (log.isDebugEnabled()) {
                            log.debug("Aktueller Graphknoten: " + curNode);
                        }
                        final FlurstueckSchluessel curUserObjectForNode = nodeToKeyMap.get(curNode.toString());
                        if (log.isDebugEnabled()) {
                            log.debug("UserObjektForNode: " + curUserObjectForNode);
                        }
                        if (curUserObjectForNode.equals(getCurrentObject().getFlurstueckSchluessel())) {
                            if (log.isDebugEnabled()) {
                                log.debug("aktuelles Flurstück");
                            }
                            curNode.highlight &= ~curNode.HIGHLIGHT_MASK;
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("Schlüssel für aktuellen Knoten: " + curUserObjectForNode.getId());
                        }
                        curNode.setUserObject(curUserObjectForNode);
                    }
                    gp.repaint();
                    log.info("Graph ist gelayoutet");
                }

                //J-
                public void doURLLayout() {
                    Object connector = null;
                    if (connector == null) {
                        try {
                            // connector = (new
                            // URL("http://www.research.att.com/~john/cgi-bin/format-graph")).openConnection(); TODO
                            // Config file
                            connector = historyServerUrl.openConnection();
                            final URLConnection urlConn = (URLConnection)connector;
                            urlConn.setDoInput(true);
                            urlConn.setDoOutput(true);
                            urlConn.setUseCaches(false);
                            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        } catch (Exception ex) {
                            System.err.println("Exception while setting up URLConnection: " + ex.getMessage()
                                        + "\nLayout not performed.");
                            connector = null;
                        }
                    }
                    if (connector != null) {
                        if (!GrappaSupport.filterGraph(graph, connector)) {
                            log.error("somewhere in filtergraph");
                        }
                        if (connector instanceof Process) {
                            try {
                                final int code = ((Process)connector).waitFor();
                                if (code != 0) {
                                    log.error("proc exit code is: " + code);
                                }
                            } catch (InterruptedException ex) {
                                log.error("Exception while closing down proc: " + ex.getMessage());
                            }
                        }
                    }
                    if (isUpdateAvailable()) {
                        cleanup();
                        return;
                    }

                    connector = null;
                    graph.repaint();
                    gp.setVisible(true);
                }
                //J+

                @Override
                protected void cleanup() {
                }
            };
        updateThread.setPriority(Thread.NORM_PRIORITY);
        updateThread.start();

        instance = this;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static HistoryPanel getInstance() {
        if (instance == null) {
            instance = new HistoryPanel();
        }

        return instance;
    }

//    /**
//     * DOCUMENT ME!
//     *
//     * @return  DOCUMENT ME!
//     */
//    public Image getImage() {
//        final int width = this.gp.getWidth();
//        final int height = this.gp.getHeight();
//
//        final BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//
////        final Graphics imgGraphics = img.getGraphics();
////        imgGraphics.setColor(gp.getBackground());
////        imgGraphics.fillRect(0, 0, img.getWidth(), img.getHeight());
//
//        gp.print(img.getGraphics());
//
//
//
//
//        return img;
//    }

    @Override
    public void refresh(final Object refreshObject) {
    }

    /**
     * DOCUMENT ME!
     */
    public void refresh() {
        if (currentFlurstueck == null) {
            return;
        }
        updateInformation();
        resetDotGraph();
        try {
            // TODO FALSE
            // cancel active running thread
            updateThread.notifyThread(currentFlurstueck);
        } catch (Exception ex) {
            log.error("Fehler beim laden der FlurstücksHistory");
        }
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
        if (log.isDebugEnabled()) {
            log.debug("FlurstueckSearchPanel --> setComponentEditable finished");
        }
    }

    @Override
    public String getWidgetName() {
        return "History Panel";
    }

    @Override
    public synchronized void clearComponent() {
        gp.setVisible(false);
    }

    /**
     * DOCUMENT ME!
     */
    private void resetDotGraph() {
        dotGraphRepresentation = new StringBuffer();
        dotGraphRepresentation.append(DEFAULT_DOT_HEADER);
    }

    @Override
    public void flurstueckChanged(final Flurstueck newFlurstueck) {
        levelTimer.cancel();
        currentFlurstueck = newFlurstueck;
        updateInformation();
        if ((nodeToKeyMap.get("\"" + newFlurstueck + "\"") != null) && ckxHoldFlurstueck.isSelected()) {
            if (log.isDebugEnabled()) {
                log.debug("Flurstück ist bereits in der Historie vorhanden und hold ist aktiviert --> kein update");
            }
            gp.setVisible(true);
            LagisBroker.getInstance().flurstueckChangeFinished(HistoryPanel.this);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Flurstückchanged HistoryPanel");
            }
            resetDotGraph();
            try {
                // TODO FALSE
                // cancel active running thread
                updateThread.notifyThread(currentFlurstueck);
                LagisBroker.getInstance().flurstueckChangeFinished(HistoryPanel.this);
            } catch (Exception ex) {
                // TODO FALSE
                LagisBroker.getInstance().flurstueckChangeFinished(HistoryPanel.this);
                log.error("Fehler beim laden der FlurstücksHistory");
            }
        }
        // ugly better solution ?? --> because how should a programmer  know that he must use this method if he uses the
        // interface ??
    }

    /**
     * DOCUMENT ME!
     */
    private void updateInformation() {
        if ((currentFlurstueck != null) && (currentFlurstueck.getFlurstueckSchluessel() != null)) {
            final FlurstueckSchluessel currentKey = currentFlurstueck.getFlurstueckSchluessel();
            if (currentKey.getDatumLetzterStadtbesitz() != null) {
//                lblDatumLSBWert.setText(LagisBroker.getDateFormatter().format(currentKey.getDatumLetzterStadtbesitz()));
//                lblDatumLSBWert.setToolTipText(currentKey.getDatumLetzterStadtbesitz().toString());
            } else {
//                lblDatumLSBWert.setText("Keine Angabe");
//                lblDatumLSBWert.setToolTipText("");
            }

            if (currentKey.getGueltigBis() != null) {
                lblDatumHistWert.setText(LagisBroker.getDateFormatter().format(currentKey.getGueltigBis()));
                lblDatumHistWert.setToolTipText(currentKey.getGueltigBis().toString());
            } else {
                lblDatumHistWert.setText("Keine Angabe");
                lblDatumHistWert.setToolTipText("");
            }

            if (currentKey.getEntstehungsDatum() != null) {
                lblDatumEntWert.setText(LagisBroker.getDateFormatter().format(currentKey.getEntstehungsDatum()));
                lblDatumEntWert.setToolTipText(currentKey.getEntstehungsDatum().toString());
            } else {
                lblDatumEntWert.setText("Keine Angabe");
                lblDatumEntWert.setToolTipText("");
            }
        } else {
            log.warn("Flurstückschlüssel ist == null");
            lblDatumEntWert.setText("Keine Angabe");
            lblDatumEntWert.setToolTipText("");
//            lblDatumLSBWert.setText("Keine Angabe");
//            lblDatumLSBWert.setToolTipText("");
            lblDatumHistWert.setText("Keine Angabe");
            lblDatumHistWert.setToolTipText("");
        }
    }

    @Override
    public void multipleClicksPerformed(final Object actionObject) {
        if (log.isDebugEnabled()) {
            log.debug("MutliClick on Graphobject: " + actionObject);
        }
        if ((actionObject != null) && (actionObject instanceof FlurstueckSchluessel)) {
            if (log.isDebugEnabled()) {
                log.debug("Flurstück wurde aus Historie ausgewählt");
            }
            final FlurstueckSchluessel newKey = (FlurstueckSchluessel)actionObject;
            if (!currentFlurstueck.getFlurstueckSchluessel().equals(newKey) && (newKey != null)
                        && (newKey.toString() != null) && newKey.isEchterSchluessel()) {
                if (log.isDebugEnabled()) {
                    log.debug("Neuer Schlüssel ist != null");
                }
                LagisBroker.getInstance().loadFlurstueck(newKey);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Neuer Schlüssel == null oder gleich oder toString == null");
                }
            }
        }
    }

    // TODO USE
    @Override
    public Icon getWidgetIcon() {
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        panHistInfo = new javax.swing.JPanel();
        panOptions = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        ckxScaleToFit = new javax.swing.JCheckBox();
        ckxHoldFlurstueck = new javax.swing.JCheckBox();
        lblVisulaization = new javax.swing.JLabel();
        cbxHistoryType = new javax.swing.JComboBox();
        panPlaceholder = new javax.swing.JPanel();
        cbxHistoryOptions = new javax.swing.JComboBox();
        sprLevels = new javax.swing.JSpinner();
        panInformation = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblDatumEnt = new javax.swing.JLabel();
        lblDatumEntWert = new javax.swing.JLabel();
        lblDatumHist = new javax.swing.JLabel();
        lblDatumHistWert = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        panOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setText("Optionen:");

        ckxScaleToFit.setSelected(true);
        ckxScaleToFit.setText("an Bildschimgröße anpassen");
        ckxScaleToFit.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    ckxScaleToFitActionPerformed(evt);
                }
            });

        ckxHoldFlurstueck.setText("Historie halten");
        ckxHoldFlurstueck.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    ckxHoldFlurstueckActionPerformed(evt);
                }
            });

        lblVisulaization.setText("Darstellung:");

        cbxHistoryType.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "Alle Flurstücke", "Nur Nachfolger", "Nur Vorgänger" }));
        cbxHistoryType.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbxHistoryTypeActionPerformed(evt);
                }
            });

        cbxHistoryOptions.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "Direkte Vorgänger/Nachfolger", "Vollständig", "Begrenzte Tiefe" }));
        cbxHistoryOptions.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbxHistoryOptionsActionPerformed(evt);
                }
            });

        sprLevels.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    sprLevelsStateChanged(evt);
                }
            });

        final org.jdesktop.layout.GroupLayout panPlaceholderLayout = new org.jdesktop.layout.GroupLayout(
                panPlaceholder);
        panPlaceholder.setLayout(panPlaceholderLayout);
        panPlaceholderLayout.setHorizontalGroup(
            panPlaceholderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panPlaceholderLayout.createSequentialGroup().addContainerGap().add(
                    cbxHistoryOptions,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(4, 4, 4).add(sprLevels).addContainerGap()));
        panPlaceholderLayout.setVerticalGroup(
            panPlaceholderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panPlaceholderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                    sprLevels,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                    cbxHistoryOptions,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));

        final org.jdesktop.layout.GroupLayout panOptionsLayout = new org.jdesktop.layout.GroupLayout(panOptions);
        panOptions.setLayout(panOptionsLayout);
        panOptionsLayout.setHorizontalGroup(
            panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panOptionsLayout.createSequentialGroup().add(
                    panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jLabel1).add(
                        panOptionsLayout.createSequentialGroup().addContainerGap().add(
                            ckxScaleToFit,
                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                            218,
                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).add(
                        panOptionsLayout.createSequentialGroup().addContainerGap().add(ckxHoldFlurstueck)))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        lblVisulaization).add(
                        panOptionsLayout.createSequentialGroup().add(12, 12, 12).add(
                            panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                                panOptionsLayout.createSequentialGroup().add(12, 12, 12).add(
                                    cbxHistoryType,
                                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                    227,
                                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap()).add(
                                panPlaceholder,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE))))));
        panOptionsLayout.setVerticalGroup(
            panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panOptionsLayout.createSequentialGroup().add(
                    panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                        lblVisulaization).add(jLabel1)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        panPlaceholder,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(ckxScaleToFit)).add(
                    panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        panOptionsLayout.createSequentialGroup().addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.UNRELATED).add(ckxHoldFlurstueck)).add(
                        panOptionsLayout.createSequentialGroup().add(5, 5, 5).add(
                            cbxHistoryType,
                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))).addContainerGap(13, Short.MAX_VALUE)));

        panInformation.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel3.setText("Informationen");

        lblDatumEnt.setText("Datum Entstehung:");

        lblDatumEntWert.setText("Keine Angabe");

        lblDatumHist.setText("Datum Historisch seit:");

        lblDatumHistWert.setText("Keine Angabe");

        final org.jdesktop.layout.GroupLayout panInformationLayout = new org.jdesktop.layout.GroupLayout(
                panInformation);
        panInformation.setLayout(panInformationLayout);
        panInformationLayout.setHorizontalGroup(
            panInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                panInformationLayout.createSequentialGroup().addContainerGap(12, Short.MAX_VALUE).add(
                    panInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false).add(
                        org.jdesktop.layout.GroupLayout.LEADING,
                        panInformationLayout.createSequentialGroup().add(lblDatumHist).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE).add(lblDatumHistWert)).add(
                        org.jdesktop.layout.GroupLayout.LEADING,
                        panInformationLayout.createSequentialGroup().add(lblDatumEnt).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE).add(lblDatumEntWert))).addContainerGap()).add(
                panInformationLayout.createSequentialGroup().add(jLabel3).addContainerGap(171, Short.MAX_VALUE)));
        panInformationLayout.setVerticalGroup(
            panInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panInformationLayout.createSequentialGroup().addContainerGap().add(jLabel3).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                        lblDatumEnt).add(lblDatumEntWert)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                        lblDatumHist).add(lblDatumHistWert)).addContainerGap(23, Short.MAX_VALUE)));

        final org.jdesktop.layout.GroupLayout panHistInfoLayout = new org.jdesktop.layout.GroupLayout(panHistInfo);
        panHistInfo.setLayout(panHistInfoLayout);
        panHistInfoLayout.setHorizontalGroup(
            panHistInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panHistInfoLayout.createSequentialGroup().addContainerGap().add(
                    panOptions,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
                    panInformation,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE).add(174, 174, 174)));
        panHistInfoLayout.setVerticalGroup(
            panHistInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                panHistInfoLayout.createSequentialGroup().addContainerGap().add(
                    panHistInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        panInformation,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE).add(
                        panOptions,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addContainerGap()));

        add(panHistInfo, java.awt.BorderLayout.SOUTH);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void ckxScaleToFitActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_ckxScaleToFitActionPerformed
        try {
            if (ckxScaleToFit.isSelected()) {
                graph.setSynchronizePaint(true);
                if (log.isDebugEnabled()) {
                    log.debug("Scale Checkbox wurde selektiert");
                }
                gp.setScaleToFit(true);
                gp.refresh(graph);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Scale Checkbox wurde deselektiert");
                }
                // gp.setVisible(false);
                gp.setScaleToFit(false);
                gp.resetZoom();
                gp.paintImmediately(gp.getBounds());
            }
        } catch (Throwable t) {
            log.fatal("OUCH!!! " + t);
        } finally {
        }
    } //GEN-LAST:event_ckxScaleToFitActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbxHistoryOptionsActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbxHistoryOptionsActionPerformed
        if (cbxHistoryOptions.getSelectedItem().equals("Begrenzte Tiefe")) {
            if (log.isDebugEnabled()) {
                log.debug("Begrentzte Tiefe ausgewählt");
            }
            levelTimer = new Timer();
            levelTimer.schedule(new delayedRefresh(), 2000);
            sprLevels.setEnabled(true);
        } else {
            sprLevels.setEnabled(false);
            levelTimer.cancel();
            refresh();
        }
    }                                                                                     //GEN-LAST:event_cbxHistoryOptionsActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbxHistoryTypeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbxHistoryTypeActionPerformed
        refresh();
    }                                                                                  //GEN-LAST:event_cbxHistoryTypeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void sprLevelsStateChanged(final javax.swing.event.ChangeEvent evt) { //GEN-FIRST:event_sprLevelsStateChanged
        levelTimer.cancel();
        levelTimer = new Timer();
        levelTimer.schedule(new delayedRefresh(), 1500);
    }                                                                             //GEN-LAST:event_sprLevelsStateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void ckxHoldFlurstueckActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_ckxHoldFlurstueckActionPerformed
// TODO add your handling code here:
    } //GEN-LAST:event_ckxHoldFlurstueckActionPerformed

    @Override
    public void configure(final org.jdom.Element parent) {
    }

    @Override
    public org.jdom.Element getConfiguration() throws NoWriteError {
        return null;
    }

    @Override
    public void masterConfigure(final org.jdom.Element parent) {
        final org.jdom.Element prefs = parent.getChild("HistoryServer");
        try {
            if (log.isDebugEnabled()) {
                log.debug("HistoryServerUrl: " + prefs.getChildText("url"));
            }
            historyServerUrl = new URL(prefs.getChildText("url"));
        } catch (Exception ex) {
            log.warn("Fehler beim lesen der HistoryServerURL. Benutze default URL", ex);
            try {
                historyServerUrl = new URL("http://s10221:8099/verdis/cgi-bin/format-graph");
            } catch (MalformedURLException ex1) {
            }
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class delayedRefresh extends TimerTask {

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        refresh();
                    }
                });
        }
    }
}
