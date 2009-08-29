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
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.SpinnerNumberModel;
import org.apache.log4j.Logger;
//import org.jgraph.JGraph;
//import org.jgraph.graph.DefaultCellViewFactory;
//import org.jgraph.graph.DefaultEdge;
//import org.jgraph.graph.DefaultGraphCell;
//import org.jgraph.graph.DefaultGraphModel;
//import org.jgraph.graph.DefaultPort;
//import org.jgraph.graph.GraphConstants;
//import org.jgraph.graph.GraphLayoutCache;
//import org.jgraph.graph.GraphModel;

/**
 *
 * @author  hell
 */
public class HistoryPanel extends AbstractWidget implements FlurstueckChangeListener, MultiClickListener, Configurable {

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    //TODO Auslagern in ConfigFile
    private static final String DEFAULT_DOT_HEADER = "digraph G{\n";
    private static final String DEFAULT_DOT_FOOTER = "}";
    //TODO UGLY WINNING --> better find encoding problem or use other framework
    private static final String UE_UPPER_CASE_REPLACEMENT = "<cismap:UE>";
    private static final String UE_LOWER_CASE_REPLACEMENT = "<cismap:ue>";
    private static final String SS_LOWER_CASE_REPLACEMENT = "<cismap:ss>";
    private static final String AE_UPPER_CASE_REPLACEMENT = "<cismap:AE>";
    private static final String AE_LOWER_CASE_REPLACEMENT = "<cismap:ae>";
    private static final String OE_UPPER_CASE_REPLACEMENT = "<cismap:OE>";
    private static final String OE_LOWER_CASE_REPLACEMENT = "<cismap:oe>";
    private StringBuffer dotGraphRepresentation = new StringBuffer();
    private String encodedDotGraphRepresentation;
    private String decodedDotGraphRepresentation;
    private Timer levelTimer = new Timer();
    private URL historyServerUrl = null;

    /** Creates new form HistoryPanel */
    public HistoryPanel() {
        setIsCoreWidget(true);
        initComponents();
        gp = new GrappaPanel(graph);
        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 100, 1);
        sprLevels.setModel(model);
        sprLevels.setEnabled(false);
        GrappaAdapter adapter = new GrappaAdapter();
        adapter.addMultiClickListener(this);
        gp.addGrappaListener(adapter);
        gp.setScaleToFit(true);
        graph.setEditable(false);
        currentSP = new JScrollPane(gp);
        add(currentSP, BorderLayout.CENTER);
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
                    log.info("Konstruiere Flurstückhistoriengraph");
                    nodeToKeyMap = new HashMap<String, FlurstueckSchluessel>();
                    pseudoKeys = new HashMap<String, String>();
                    if (isUpdateAvailable()) {
                        cleanup();
                        return;
                    }
                    //ToDo remove Strings
                    log.debug("Erstelle Historien Anfrage:");
                    HistoryLevel level;
                    int levelCount = 0;
                    if (cbxHistoryOptions.getSelectedItem().equals("Direkte Vorgänger/Nachfolger")) {
                        level = HistoryLevel.DIRECT_RELATIONS;
                        log.debug("nur angrenzendte Flurstücke");
                    } else if (cbxHistoryOptions.getSelectedItem().equals("Begrenzte Tiefe")) {
                        level = HistoryLevel.CUSTOM;
                        levelCount = ((Number) sprLevels.getValue()).intValue();
                        log.debug("begrentze Tiefe mit " + levelCount + " Stufen");
                    } else {
                        level = HistoryLevel.All;
                        log.debug("Alle Levels");
                    }
                    HistoryType type;
                    if (cbxHistoryType.getSelectedItem().equals("Nur Nachfolger")) {
                        type = HistoryType.SUCCESSOR;
                        log.debug("nur Nachfolger");
                    } else if (cbxHistoryType.getSelectedItem().equals("Nur Vorgänger")) {
                        type = HistoryType.PREDECESSOR;
                        log.debug("nur Vorgänger");
                    } else {
                        type = HistoryType.BOTH;
                        log.debug("Vorgänger/Nachfolger");
                    }


                    Set<FlurstueckHistorie> allEdges = EJBroker.getInstance().getHistoryEntries(getCurrentObject().getFlurstueckSchluessel(), level, type, levelCount);

                    if (allEdges != null && allEdges.size() > 0) {
                        log.debug("Historie Graph hat: " + allEdges.size() + " Kanten");
                        Iterator<FlurstueckHistorie> it = allEdges.iterator();
                        while (it.hasNext()) {
                            if (isUpdateAvailable()) {
                                cleanup();
                                return;
                            }
                            FlurstueckHistorie currentEdge = it.next();
                            String currentVorgaenger = currentEdge.getVorgaenger().toString();
                            String currentNachfolger = currentEdge.getNachfolger().toString();
                            if (currentVorgaenger.startsWith("pseudo")) {
                                pseudoKeys.put(currentVorgaenger, "    ");
                            }
                            if (currentNachfolger.startsWith("pseudo")) {
                                pseudoKeys.put(currentNachfolger, "    ");
                            }
                            dotGraphRepresentation.append("\"" + currentVorgaenger + "\"" + "->" + "\"" + currentNachfolger + "\"" + ";\n");
                            nodeToKeyMap.put("\"" + currentEdge.getVorgaenger().toString() + "\"", currentEdge.getVorgaenger().getFlurstueckSchluessel());
                            nodeToKeyMap.put("\"" + currentEdge.getNachfolger().toString() + "\"", currentEdge.getNachfolger().getFlurstueckSchluessel());
                        }
                        if (isUpdateAvailable()) {
                            cleanup();
                            return;
                        }
                    } else {
                        log.debug("Historie Graph ist < 1 --> keine Historie");
                        dotGraphRepresentation.append("\"" + getCurrentObject() + "\"" + ";\n");
                        nodeToKeyMap.put("\"" + getCurrentObject() + "\"", getCurrentObject().getFlurstueckSchluessel());
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
                        for (String key : pseudoKeys.keySet()) {
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
                    log.debug("Erzeugte Dot Graph Darstellung: \n" + encodedDotGraphRepresentation);
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
                Parser program = new Parser(new StringReader(encodedDotGraphRepresentation), new PrintWriter(System.err));
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
                log.info("The graph contains " + graph.countOfElements(Grappa.NODE | Grappa.EDGE | Grappa.SUBGRAPH) + " elements.");
                //JScrollPane jsp = new JScrollPane();
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
                GraphEnumeration ge = gp.getSubgraph().elements(Subgraph.NODE);
                while (ge.hasMoreElements()) {
                    Element curNode = ge.nextGraphElement();
                    log.debug("Aktueller Graphknoten: " + curNode);
                    FlurstueckSchluessel curUserObjectForNode = nodeToKeyMap.get(curNode.toString());
                    log.debug("UserObjektForNode: " + curUserObjectForNode);
                    if (curUserObjectForNode.equals(getCurrentObject().getFlurstueckSchluessel())) {
                        log.debug("aktuelles Flurstück");
                        curNode.highlight &= ~curNode.HIGHLIGHT_MASK;
                    }
                    log.debug("Schlüssel für aktuellen Knoten: " + curUserObjectForNode.getId());
                    curNode.setUserObject(curUserObjectForNode);
                }
                gp.repaint();
                log.info("Graph ist gelayoutet");
            }

            public void doURLLayout() {
                Object connector = null;
                if (connector == null) {
                    try {
                        //connector = (new URL("http://www.research.att.com/~john/cgi-bin/format-graph")).openConnection();
                        //TODO Config file
                        connector = historyServerUrl.openConnection();
                        URLConnection urlConn = (URLConnection) connector;
                        urlConn.setDoInput(true);
                        urlConn.setDoOutput(true);
                        urlConn.setUseCaches(false);
                        urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    } catch (Exception ex) {
                        System.err.println("Exception while setting up URLConnection: " + ex.getMessage() + "\nLayout not performed.");
                        connector = null;
                    }
                }
                if (connector != null) {
                    if (!GrappaSupport.filterGraph(graph, connector)) {
                        log.error("somewhere in filtergraph");
                    }
                    if (connector instanceof Process) {
                        try {
                            int code = ((Process) connector).waitFor();
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

            protected void cleanup() {
            }
        };
        updateThread.setPriority(Thread.NORM_PRIORITY);
        updateThread.start();
    //jsp.getViewport().setBackingStoreEnabled(true);
    }

    public void refresh(Object refreshObject) {
    }

    public void refresh() {
        if (currentFlurstueck == null) {
            return;
        }
        updateInformation();
        resetDotGraph();
        try {
            //TODO FALSE
            //cancel active running thread
            updateThread.notifyThread(currentFlurstueck);
        } catch (Exception ex) {
            log.error("Fehler beim laden der FlurstücksHistory");
        }
    }

    public void setComponentEditable(boolean isEditable) {
        log.debug("FlurstueckSearchPanel --> setComponentEditable finished");
    }

    public String getWidgetName() {
        return "History Panel";
    }

    public synchronized void clearComponent() {
        gp.setVisible(false);
    }

    private void resetDotGraph() {
        dotGraphRepresentation = new StringBuffer();
        dotGraphRepresentation.append(DEFAULT_DOT_HEADER);
    }
    private HashMap<String, FlurstueckSchluessel> nodeToKeyMap = new HashMap<String, FlurstueckSchluessel>();
    private HashMap<String, String> pseudoKeys = new HashMap<String, String>();
    private Flurstueck currentFlurstueck;
    //private Thread panelRefresherThread;
    private BackgroundUpdateThread<Flurstueck> updateThread;
    //TODO THREAD

    public void flurstueckChanged(final Flurstueck newFlurstueck) {
        levelTimer.cancel();
        currentFlurstueck = newFlurstueck;
        updateInformation();
        if (nodeToKeyMap.get("\"" + newFlurstueck + "\"") != null && ckxHoldFlurstueck.isSelected()) {
            log.debug("Flurstück ist bereits in der Historie vorhanden und hold ist aktiviert --> kein update");
            gp.setVisible(true);
            LagisBroker.getInstance().flurstueckChangeFinished(HistoryPanel.this);
        } else {
            log.debug("Flurstückchanged HistoryPanel");
            resetDotGraph();
            try {
                //TODO FALSE
                //cancel active running thread
                updateThread.notifyThread(currentFlurstueck);
                LagisBroker.getInstance().flurstueckChangeFinished(HistoryPanel.this);

            } catch (Exception ex) {
                //TODO FALSE
                LagisBroker.getInstance().flurstueckChangeFinished(HistoryPanel.this);
                log.error("Fehler beim laden der FlurstücksHistory");
            }
        }
    //ugly better solution ?? --> because how should a programmer  know that he must use this method if he uses the interface ??

    }

    private void updateInformation() {
        if (currentFlurstueck != null && currentFlurstueck.getFlurstueckSchluessel() != null) {
            FlurstueckSchluessel currentKey = currentFlurstueck.getFlurstueckSchluessel();
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
    private static double CELL_WITDH = 200;
    private static double CELL_HEIGHT = 20;
    private static double DEFAULT_GAP_X = 20;
    private static double DEFAULT_GAP_Y = 20;
    private double currentCellXCoordinate = 100;
    private double graphStartPosition = DEFAULT_GAP_Y;
    private double currentCellYCoordinate = DEFAULT_GAP_Y;
    private double maxBreadth = 1;
    private Map changeMap = new Hashtable();
    private Hashtable<Integer, Integer> nodePerLevel = new Hashtable<Integer, Integer>();
    //TODO NOT DIRECTLY OUTPUT THE ERRORS ON ERR
    //private double cellxcoordinate =
    private Graph graph = new Graph("Flurstück Historie");
    private GrappaPanel gp;
    private JScrollPane currentSP;

    public void multipleClicksPerformed(Object actionObject) {
        log.debug("MutliClick on Graphobject: " + actionObject);
        if (actionObject != null && actionObject instanceof FlurstueckSchluessel) {
            log.debug("Flurstück wurde aus Historie ausgewählt");
            final FlurstueckSchluessel newKey = (FlurstueckSchluessel) actionObject;
            if (!currentFlurstueck.getFlurstueckSchluessel().equals(newKey) && newKey != null && newKey.toString() != null && newKey.isEchterSchluessel()) {
                log.debug("Neuer Schlüssel ist != null");
                LagisBroker.getInstance().loadFlurstueck(newKey);
            } else {
                log.debug("Neuer Schlüssel == null oder gleich oder toString == null");
            }
        }
    }

    //TODO USE
    public Icon getWidgetIcon() {
        return null;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
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
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ckxScaleToFitActionPerformed(evt);
            }
        });

        ckxHoldFlurstueck.setText("Historie halten");
        ckxHoldFlurstueck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ckxHoldFlurstueckActionPerformed(evt);
            }
        });

        lblVisulaization.setText("Darstellung:");

        cbxHistoryType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Alle Flurstücke", "Nur Nachfolger", "Nur Vorgänger" }));
        cbxHistoryType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxHistoryTypeActionPerformed(evt);
            }
        });

        cbxHistoryOptions.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Direkte Vorgänger/Nachfolger", "Vollständig", "Begrenzte Tiefe" }));
        cbxHistoryOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxHistoryOptionsActionPerformed(evt);
            }
        });

        sprLevels.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sprLevelsStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout panPlaceholderLayout = new org.jdesktop.layout.GroupLayout(panPlaceholder);
        panPlaceholder.setLayout(panPlaceholderLayout);
        panPlaceholderLayout.setHorizontalGroup(
            panPlaceholderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panPlaceholderLayout.createSequentialGroup()
                .addContainerGap()
                .add(cbxHistoryOptions, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(4, 4, 4)
                .add(sprLevels)
                .addContainerGap())
        );
        panPlaceholderLayout.setVerticalGroup(
            panPlaceholderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panPlaceholderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(sprLevels, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(cbxHistoryOptions, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout panOptionsLayout = new org.jdesktop.layout.GroupLayout(panOptions);
        panOptions.setLayout(panOptionsLayout);
        panOptionsLayout.setHorizontalGroup(
            panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panOptionsLayout.createSequentialGroup()
                .add(panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(panOptionsLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(ckxScaleToFit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 218, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(panOptionsLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(ckxHoldFlurstueck)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblVisulaization)
                    .add(panOptionsLayout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(panOptionsLayout.createSequentialGroup()
                                .add(12, 12, 12)
                                .add(cbxHistoryType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 227, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                            .add(panPlaceholder, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
        );
        panOptionsLayout.setVerticalGroup(
            panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panOptionsLayout.createSequentialGroup()
                .add(panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblVisulaization)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panPlaceholder, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ckxScaleToFit))
                .add(panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panOptionsLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(ckxHoldFlurstueck))
                    .add(panOptionsLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(cbxHistoryType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        panInformation.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel3.setText("Informationen");

        lblDatumEnt.setText("Datum Entstehung:");

        lblDatumEntWert.setText("Keine Angabe");

        lblDatumHist.setText("Datum Historisch seit:");

        lblDatumHistWert.setText("Keine Angabe");

        org.jdesktop.layout.GroupLayout panInformationLayout = new org.jdesktop.layout.GroupLayout(panInformation);
        panInformation.setLayout(panInformationLayout);
        panInformationLayout.setHorizontalGroup(
            panInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, panInformationLayout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .add(panInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, panInformationLayout.createSequentialGroup()
                        .add(lblDatumHist)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(lblDatumHistWert))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, panInformationLayout.createSequentialGroup()
                        .add(lblDatumEnt)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(lblDatumEntWert)))
                .addContainerGap())
            .add(panInformationLayout.createSequentialGroup()
                .add(jLabel3)
                .addContainerGap(171, Short.MAX_VALUE))
        );
        panInformationLayout.setVerticalGroup(
            panInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panInformationLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblDatumEnt)
                    .add(lblDatumEntWert))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panInformationLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblDatumHist)
                    .add(lblDatumHistWert))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout panHistInfoLayout = new org.jdesktop.layout.GroupLayout(panHistInfo);
        panHistInfo.setLayout(panHistInfoLayout);
        panHistInfoLayout.setHorizontalGroup(
            panHistInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panHistInfoLayout.createSequentialGroup()
                .addContainerGap()
                .add(panOptions, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panInformation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(174, 174, 174))
        );
        panHistInfoLayout.setVerticalGroup(
            panHistInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panHistInfoLayout.createSequentialGroup()
                .addContainerGap()
                .add(panHistInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panInformation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(panOptions, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        add(panHistInfo, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

private void ckxScaleToFitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ckxScaleToFitActionPerformed
    try {
        if (ckxScaleToFit.isSelected()) {
            graph.setSynchronizePaint(true);
            log.debug("Scale Checkbox wurde selektiert");
            gp.setScaleToFit(true);
            gp.refresh(graph);
        } else {
            log.debug("Scale Checkbox wurde deselektiert");
            //gp.setVisible(false);
            gp.setScaleToFit(false);
            gp.resetZoom();
            gp.paintImmediately(gp.getBounds());
        }
    } catch (Throwable t) {
        log.fatal("OUCH!!! " + t);
    } finally {
    }
}//GEN-LAST:event_ckxScaleToFitActionPerformed

private void cbxHistoryOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxHistoryOptionsActionPerformed
    if (cbxHistoryOptions.getSelectedItem().equals("Begrenzte Tiefe")) {
        log.debug("Begrentzte Tiefe ausgewählt");
        levelTimer = new Timer();
        levelTimer.schedule(new delayedRefresh(), 2000);
        sprLevels.setEnabled(true);
    } else {
        sprLevels.setEnabled(false);
        levelTimer.cancel();
        refresh();
    }
}//GEN-LAST:event_cbxHistoryOptionsActionPerformed

private void cbxHistoryTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxHistoryTypeActionPerformed
    refresh();
}//GEN-LAST:event_cbxHistoryTypeActionPerformed

private void sprLevelsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sprLevelsStateChanged
    levelTimer.cancel();
    levelTimer = new Timer();
    levelTimer.schedule(new delayedRefresh(), 1500);
}//GEN-LAST:event_sprLevelsStateChanged

private void ckxHoldFlurstueckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ckxHoldFlurstueckActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_ckxHoldFlurstueckActionPerformed

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

    private class delayedRefresh extends TimerTask {

        public void run() {
            EventQueue.invokeLater(new Runnable() {

                public void run() {
                    refresh();
                }
            });

        }
    }

    public void configure(org.jdom.Element parent) {
    }

    public org.jdom.Element getConfiguration() throws NoWriteError {
        return null;
    }

    public void masterConfigure(org.jdom.Element parent) {
        org.jdom.Element prefs = parent.getChild("HistoryServer");
        try {
            log.debug("HistoryServerUrl: " + prefs.getChildText("url"));
            historyServerUrl = new URL(prefs.getChildText("url"));
        } catch (Exception ex) {
            log.warn("Fehler beim lesen der HistoryServerURL. Benutze default URL", ex);            
            try {
                historyServerUrl = new URL("http://s10221:8099/verdis/cgi-bin/format-graph");
            } catch (MalformedURLException ex1) {
            }
        }
    }
}



