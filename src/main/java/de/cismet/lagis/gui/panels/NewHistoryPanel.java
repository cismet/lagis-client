/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * HistoryPanel.java
 *
 * Created on 22.04.2009, 07:49:58
 */
package de.cismet.lagis.gui.panels;

import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.layout.SugiyamaLayout;
import de.cismet.lagis.layout.model.HistoryPanelEdge;
import de.cismet.lagis.layout.model.HistoryPanelModel;
import de.cismet.lagis.layout.widget.HistoryLegendPanel;
import de.cismet.lagis.layout.widget.InfiniteProgressPanel;
import de.cismet.lagis.thread.BackgroundUpdateThread;
import de.cismet.lagis.widget.AbstractWidget;
import de.cismet.lagisEE.bean.LagisServerBean.HistoryLevel;
import de.cismet.lagisEE.bean.LagisServerBean.HistoryType;
import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.core.hardwired.Gemarkung;
import de.cismet.lagisEE.entity.history.FlurstueckHistorie;
import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.NoWriteError;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JViewport;
import org.apache.log4j.Logger;

import org.jdom.Element;
import org.netbeans.api.visual.graph.layout.GraphLayout;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.api.visual.widget.BirdViewController;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author mbrill
 */
public class NewHistoryPanel extends AbstractWidget
        implements FlurstueckChangeListener, Configurable, PropertyChangeListener {

    //-------------------------------------------------------------------------
    //          Attributes
    //-------------------------------------------------------------------------
    /**
     * The flurstueck currently selected in lagis
     */
    private Flurstueck currentFlurstueck;

    /**
     * The NBV Scene object rendering the graph
     */
    private HistoryPanelModel graphScene;

    /**
     * The scene layout algorithm instance. In this case instanceof SugiyamaLayout
     */
    private SceneLayout sceneLayout;
    private Logger log;

    /**
     * Variable holding the user selection of how many levels of the graph
     * are to display
     */
    private HistoryLevel level;

    /**
     * Variable holding the user selection of wether all, only predecessing or
     * only successing nodes relative to the current flurstueck are to display
     */
    private HistoryType type;

    /**
     * Variable holding the user selection of how many graph levels are to display
     * in case the user chose to display a custom graph depth
     */
    private int depth;

    /**
     * NBV Magnifying glass
     */
    private BirdViewController birdViewController;

    /**
     * JComponent version of the NBV Scene which is used to integrate NBV into Swing
     */
    private JComponent view;

    /**
     * Thread which updates the history graph asynchronously
     */
    private GraphUpdateThread<Flurstueck> updateThread;

    /**
     * Panel indicating a progress. In this case the panel is shown instead of the
     * NBV view to indicate that the graph is currently updating
     */
    private InfiniteProgressPanel progressPanel;

    /**
     * Dateformatter to display any Date in the format dd.MM.yyyy
     */
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    /** Creates new form HistoryPanel */
    public NewHistoryPanel() {

        initComponents();

        log = Logger.getLogger(this.getClass());

        setWidgetName("History Panel");
        setIsCoreWidget(true);

        progressPanel = new InfiniteProgressPanel("Der Graph wird berechnet");


        graphScene = new HistoryPanelModel();
        graphScene.getSelectProvider().addPropertyChangeListener(this);

        view = graphScene.createView();
        view.setDoubleBuffered(false);
        view.setBackground(Color.WHITE);

        JComponent satellite = graphScene.createSatelliteView();
        satellite.setPreferredSize(new Dimension(250, 250));

        satellitePanel.add(satellite, BorderLayout.CENTER);
        graphPane.setViewportView(view);

        GraphLayout<Flurstueck, HistoryPanelEdge> layout = new SugiyamaLayout(graphScene);

        sceneLayout = LayoutFactory.createSceneGraphLayout(graphScene, layout);

        legendBasePanel.add("Center", new HistoryLegendPanel());

        birdViewController = graphScene.createBirdView();
        legendOverviewMainPanel.setVisible(overViewCHB.isSelected());

        updateThread = new GraphUpdateThread();

        updateThread.addPropertyChangeListener(this);
        updateThread.setPriority(Thread.NORM_PRIORITY);
        updateThread.start();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        historyControlPanel = new javax.swing.JPanel();
        historyOptionPanel = new javax.swing.JPanel();
        optionPanel = new javax.swing.JPanel();
        magnifyerCHB = new javax.swing.JCheckBox();
        holdHistoryCHB = new javax.swing.JCheckBox();
        overViewCHB = new javax.swing.JCheckBox();
        graphDepthPanel = new javax.swing.JPanel();
        flurstueckChoserCB = new javax.swing.JComboBox();
        depthChoserCB = new javax.swing.JComboBox();
        depthSP = new javax.swing.JSpinner();
        historyInformationPanel = new javax.swing.JPanel();
        creationDateLabel = new javax.swing.JLabel();
        historicSinceLabel = new javax.swing.JLabel();
        creationDateInfoLabel = new javax.swing.JLabel();
        historicSinceInfoLabel = new javax.swing.JLabel();
        graphPanel = new javax.swing.JPanel();
        graphPane = new javax.swing.JScrollPane();
        legendOverviewMainPanel = new javax.swing.JPanel();
        satellitePanel = new javax.swing.JPanel();
        legendBasePanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        historyControlPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        historyControlPanel.setPreferredSize(new java.awt.Dimension(498, 140));

        historyOptionPanel.setBorder(null);

        optionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Werkzeuge"));
        optionPanel.setLayout(new java.awt.GridBagLayout());

        magnifyerCHB.setText("Bildschirmlupe");
        magnifyerCHB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                magnifyerCHBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        optionPanel.add(magnifyerCHB, gridBagConstraints);

        holdHistoryCHB.setText("Historie halten");
        holdHistoryCHB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                holdHistoryCHBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        optionPanel.add(holdHistoryCHB, gridBagConstraints);

        overViewCHB.setText("Übersicht");
        overViewCHB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overViewCHBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        optionPanel.add(overViewCHB, gridBagConstraints);

        graphDepthPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Darstellung"));
        graphDepthPanel.setLayout(new java.awt.GridBagLayout());

        flurstueckChoserCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Alle Flurstücke", "Nur Nachfolger", "Nur Vorgänger" }));
        flurstueckChoserCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flurstueckChoserCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        graphDepthPanel.add(flurstueckChoserCB, gridBagConstraints);

        depthChoserCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Direkte Vorgänger/Nachfolger", "Vollständig", "Begrenzte Tiefe" }));
        depthChoserCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                depthChoserCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        graphDepthPanel.add(depthChoserCB, gridBagConstraints);

        depthSP.setModel(new javax.swing.SpinnerNumberModel());
        depthSP.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                depthSPStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        graphDepthPanel.add(depthSP, gridBagConstraints);

        javax.swing.GroupLayout historyOptionPanelLayout = new javax.swing.GroupLayout(historyOptionPanel);
        historyOptionPanel.setLayout(historyOptionPanelLayout);
        historyOptionPanelLayout.setHorizontalGroup(
            historyOptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(historyOptionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(optionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphDepthPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        historyOptionPanelLayout.setVerticalGroup(
            historyOptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, historyOptionPanelLayout.createSequentialGroup()
                .addGroup(historyOptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(graphDepthPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                    .addComponent(optionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE))
                .addContainerGap())
        );

        historyInformationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Informationen"));
        historyInformationPanel.setLayout(new java.awt.GridBagLayout());

        creationDateLabel.setText("Datum Entstehung:");
        historyInformationPanel.add(creationDateLabel, new java.awt.GridBagConstraints());

        historicSinceLabel.setText("Historisch seit:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        historyInformationPanel.add(historicSinceLabel, gridBagConstraints);

        creationDateInfoLabel.setText("keine Angaben");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        historyInformationPanel.add(creationDateInfoLabel, gridBagConstraints);

        historicSinceInfoLabel.setText("keine Angaben");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        historyInformationPanel.add(historicSinceInfoLabel, gridBagConstraints);

        javax.swing.GroupLayout historyControlPanelLayout = new javax.swing.GroupLayout(historyControlPanel);
        historyControlPanel.setLayout(historyControlPanelLayout);
        historyControlPanelLayout.setHorizontalGroup(
            historyControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(historyControlPanelLayout.createSequentialGroup()
                .addComponent(historyOptionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(historyInformationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                .addContainerGap())
        );
        historyControlPanelLayout.setVerticalGroup(
            historyControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(historyOptionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(historyControlPanelLayout.createSequentialGroup()
                .addComponent(historyInformationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        add(historyControlPanel, java.awt.BorderLayout.SOUTH);

        graphPane.setBackground(new java.awt.Color(254, 254, 254));

        legendOverviewMainPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        legendOverviewMainPanel.setMaximumSize(new java.awt.Dimension(245, 32767));

        satellitePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Übersicht"));
        satellitePanel.setLayout(new java.awt.BorderLayout());

        legendBasePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Legende"));
        legendBasePanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout legendOverviewMainPanelLayout = new javax.swing.GroupLayout(legendOverviewMainPanel);
        legendOverviewMainPanel.setLayout(legendOverviewMainPanelLayout);
        legendOverviewMainPanelLayout.setHorizontalGroup(
            legendOverviewMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(legendBasePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
            .addComponent(satellitePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
        );
        legendOverviewMainPanelLayout.setVerticalGroup(
            legendOverviewMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(legendOverviewMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(satellitePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(legendBasePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout graphPanelLayout = new javax.swing.GroupLayout(graphPanel);
        graphPanel.setLayout(graphPanelLayout);
        graphPanelLayout.setHorizontalGroup(
            graphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, graphPanelLayout.createSequentialGroup()
                .addComponent(graphPane, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(legendOverviewMainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        graphPanelLayout.setVerticalGroup(
            graphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, graphPanelLayout.createSequentialGroup()
                .addGroup(graphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(graphPane, javax.swing.GroupLayout.DEFAULT_SIZE, 755, Short.MAX_VALUE)
                    .addComponent(legendOverviewMainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        add(graphPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void overViewCHBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overViewCHBActionPerformed
        legendOverviewMainPanel.setVisible(overViewCHB.isSelected());
        graphScene.revalidate();
        view.repaint();
}//GEN-LAST:event_overViewCHBActionPerformed

    private void magnifyerCHBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_magnifyerCHBActionPerformed
        if (magnifyerCHB.isSelected()) {
            birdViewController.show();
        } else {
            birdViewController.hide();
        }
}//GEN-LAST:event_magnifyerCHBActionPerformed

    private void holdHistoryCHBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_holdHistoryCHBActionPerformed
        if (holdHistoryCHB.isSelected()) {
            depthChoserCB.setEnabled(false);
            flurstueckChoserCB.setEnabled(false);
            depthSP.setEnabled(false);
        } else {
            depthChoserCB.setEnabled(true);
            flurstueckChoserCB.setEnabled(true);
            depthSP.setEnabled(true);
        }
    }//GEN-LAST:event_holdHistoryCHBActionPerformed

    private void depthChoserCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_depthChoserCBActionPerformed
        if (currentFlurstueck != null) {
            updateThread.notifyThread(currentFlurstueck);
        }
    }//GEN-LAST:event_depthChoserCBActionPerformed

    private void flurstueckChoserCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flurstueckChoserCBActionPerformed
        if (currentFlurstueck != null) {
            updateThread.notifyThread(currentFlurstueck);
        }
    }//GEN-LAST:event_flurstueckChoserCBActionPerformed

    private void depthSPStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_depthSPStateChanged
        if (currentFlurstueck != null && ((String) depthChoserCB.getSelectedItem()).equals("Begrenzte Tiefe")) {
            updateThread.notifyThread(currentFlurstueck);
        }
    }//GEN-LAST:event_depthSPStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel creationDateInfoLabel;
    private javax.swing.JLabel creationDateLabel;
    private javax.swing.JComboBox depthChoserCB;
    private javax.swing.JSpinner depthSP;
    private javax.swing.JComboBox flurstueckChoserCB;
    private javax.swing.JPanel graphDepthPanel;
    private javax.swing.JScrollPane graphPane;
    private javax.swing.JPanel graphPanel;
    private javax.swing.JLabel historicSinceInfoLabel;
    private javax.swing.JLabel historicSinceLabel;
    private javax.swing.JPanel historyControlPanel;
    private javax.swing.JPanel historyInformationPanel;
    private javax.swing.JPanel historyOptionPanel;
    private javax.swing.JCheckBox holdHistoryCHB;
    private javax.swing.JPanel legendBasePanel;
    private javax.swing.JPanel legendOverviewMainPanel;
    private javax.swing.JCheckBox magnifyerCHB;
    private javax.swing.JPanel optionPanel;
    private javax.swing.JCheckBox overViewCHB;
    private javax.swing.JPanel satellitePanel;
    // End of variables declaration//GEN-END:variables

    //-------------------------------------------------------------------------
    //          Überschriebene Methoden
    //-------------------------------------------------------------------------
    /**
     * <code>clearComponent</code> empties the sceneGraph datastructure and therefor
     * removes any graph representation from the visible component.
     */
    @Override
    public void clearComponent() {

        if (!holdHistoryCHB.isSelected()) {

            log.info("Cleaning HistoryPanel");
            graphScene.setVisible(false);

            ArrayList<Flurstueck> nodes = new ArrayList<Flurstueck>(graphScene.getNodes());
            ArrayList<HistoryPanelEdge> edges = new ArrayList<HistoryPanelEdge>(graphScene.getEdges());

            for (HistoryPanelEdge edge : edges) {
                graphScene.removeEdge(edge);
            }

            for (Flurstueck node : nodes) {
                graphScene.removeNode(node);
            }
        }
    }

    /**
     * This method implements the abstract LagIS Widget. It calls the
     * {@link #updateGraph() } method on a refresh request.
     *
     * @param refreshObject
     */
    @Override
    public void refresh(Object refreshObject) {
        updateGraph();
    }

    /**
     * 
     * @param isEditable
     */
    @Override
    public void setComponentEditable(boolean isEditable) {
        log.warn("Unsupported operation called : setComponentEditable");
    }

    /**
     * Method implements the FlurstueckChangeListener Interface. If a new Flurstueck
     * is selected in LagIS it updates the graph representation.
     *
     * @param newFlurstueck The new Flurstueck selected by the user
     */
    @Override
    public void flurstueckChanged(Flurstueck newFlurstueck) {

        if (newFlurstueck != null) {
            if (!holdHistoryCHB.isSelected()) {
           
                currentFlurstueck = newFlurstueck;
                updateThread.notifyThread(newFlurstueck);
                LagisBroker.getInstance().flurstueckChangeFinished(this);
            }
        } else {
            log.error("FlurstückChangeListener HistoryPanel - given" +
                    " Flurstück is null. No Panel update performed");
        }
    }

    //-------------------------------------------------------------------------
    //          KlassenMethoden
    //-------------------------------------------------------------------------
    /**
     * This method is intended to be used when there is no LagIS system available
     * giving the information which Flurstueck is currently set. It asks the
     * EJBBroker for a Flurstueck instance by Gemarkung, Flur, Flurstuecknenner and
     * Flurstueckzaehler.
     *
     * @param gemID Gemarkung of the requested Flurstueck
     * @param flur Flur of the requested Flurstueck
     * @param zaehler Flurstueckzaehler of the requested Flurstueck
     * @param nenner Flurstuecknenner of the requested Flurstueck
     */
    public void loadFlurstueck(int gemID, int flur, int zaehler, int nenner) {

        FlurstueckSchluessel key = new FlurstueckSchluessel();
        key.setFlur(flur);
        key.setFlurstueckNenner(nenner);
        key.setFlurstueckZaehler(zaehler);

        HashMap<Integer, Gemarkung> gems = EJBroker.getInstance().getGemarkungsHashMap();
        Gemarkung gemarkung = gems.get(gemID);

        gemarkung = EJBroker.getInstance().completeGemarkung(gemarkung);

        key.setGemarkung(gemarkung);

        key = EJBroker.getInstance().completeFlurstueckSchluessel(key);

        currentFlurstueck = EJBroker.getInstance().retrieveFlurstueck(key);

        if (currentFlurstueck == null) {
            log.error("Flurstueck not found : " + gemID + " " + flur + " " +
                    zaehler + "/" + nenner);
        } else {
            log.info("Flurstueck found : " + currentFlurstueck.getFlurstueckSchluessel().getKeyString());
            updateGraph();
        }


    }

    /**
     * This method performs any operation neccessary to update the historyPanel on
     * a change. Therefor the method asks the EJBBroker for a set of history entries
     * depending on the settings of the control panel. The returned set is put into
     * the graphScene, which generates the representing Widgets for nodes and edges.
     */
    private void updateGraph() {

        if (!holdHistoryCHB.isSelected()) {

            log.debug("updateGraph called");
            clearComponent();

            try {
                if (currentFlurstueck != null) {

                    getHistorySettings();

                    Set<FlurstueckHistorie> history;

                    if (level.equals(HistoryLevel.CUSTOM) && depth == 0) {
                        history = new HashSet<FlurstueckHistorie>();

                    } else {
                        history = EJBroker.getInstance().
                                getHistoryEntries(currentFlurstueck.getFlurstueckSchluessel(),
                                level, type, depth);
                    }


                    log.debug("History entries : " + history.size());

                    if (history.size() == 0) {
                        if (graphScene.findStoredObject(currentFlurstueck) == null) {
                            graphScene.addNode(currentFlurstueck);
                            log.debug("adding node " + currentFlurstueck);
                        } else {
                            log.debug("current flurstueck already in graph");
                        }
                    }

                    for (FlurstueckHistorie h : history) {

                        Flurstueck vorgaenger = h.getVorgaenger();
                        Flurstueck nachfolger = h.getNachfolger();
                        HistoryPanelEdge edge = new HistoryPanelEdge(vorgaenger, nachfolger);

                        if (graphScene.findStoredObject(vorgaenger) == null) {
                            graphScene.addNode(vorgaenger);
                            log.debug("adding node " + vorgaenger);
                        }

                        if (graphScene.findStoredObject(nachfolger) == null) {
                            graphScene.addNode(nachfolger);
                            log.debug("adding node " + nachfolger);
                        }

                        if (graphScene.findStoredObject(edge) == null) {
                            graphScene.addEdge(edge);
                            graphScene.setEdgeSource(edge, vorgaenger);
                            graphScene.setEdgeTarget(edge, nachfolger);
                            log.debug("adding edge " + edge);
                        }


                    }

                    sceneLayout.invokeLayout();
                    graphScene.validate();



                } else {
                    log.warn("currentFlurstueck : " + currentFlurstueck);
                }

                log.debug("updateGraph finished");

            } catch (Exception ex) {
                log.error("Error while updating Graph", ex);
            }
        }

        graphScene.setVisible(true);
        graphScene.revalidate(true);
//        view.repaint();

    }

    /**
     * Method checks the values of depthChooserCB and flurstueckChooserCB and sets values
     * for the EJBBroker to retrieve history entries.
     */
    private void getHistorySettings() {

        switch (depthChoserCB.getSelectedIndex()) {
            case 0:
                level = HistoryLevel.DIRECT_RELATIONS;
                break;
            case 1:
                level = HistoryLevel.All;
                break;
            case 2:
                level = HistoryLevel.CUSTOM;
                break;
        }

        switch (flurstueckChoserCB.getSelectedIndex()) {
            case 0:
                type = HistoryType.BOTH;
                break;
            case 1:
                type = HistoryType.SUCCESSOR;
                break;
            case 2:
                type = HistoryType.PREDECESSOR;
                break;
        }

        depth = (Integer) depthSP.getValue();
    }

    //-------------------------------------------------------------------------
    //          Getter & Setter - Beans compliance
    //-------------------------------------------------------------------------
    public Flurstueck getCurrentFlurstueck() {
        return currentFlurstueck;
    }

    public void setCurrentFlurstueck(Flurstueck currentFlurstueck) {
        this.currentFlurstueck = currentFlurstueck;
    }

    public HistoryPanelModel getGraphScene() {
        return graphScene;
    }

    public void setGraphScene(HistoryPanelModel graphScene) {
        this.graphScene = graphScene;
    }

    public JPanel getHistoryControlPanel() {
        return historyControlPanel;
    }

    public void setHistoryControlPanel(JPanel jPanel1) {
        this.historyControlPanel = jPanel1;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public JLabel getCreationDateInfoLabel() {
        return creationDateInfoLabel;
    }

    public void setCreationDateInfoLabel(JLabel creationDateInfoLabel) {
        this.creationDateInfoLabel = creationDateInfoLabel;
    }

    public JLabel getCreationDateLabel() {
        return creationDateLabel;
    }

    public void setCreationDateLabel(JLabel creationDateLabel) {
        this.creationDateLabel = creationDateLabel;
    }

    public JComboBox getDepthChoserCB() {
        return depthChoserCB;
    }

    public void setDepthChoserCB(JComboBox depthChoserCB) {
        this.depthChoserCB = depthChoserCB;
    }

    public JSpinner getDepthSP() {
        return depthSP;
    }

    public void setDepthSP(JSpinner depthSP) {
        this.depthSP = depthSP;
    }

    public JCheckBox getFitToViewportCHB() {
        return overViewCHB;
    }

    public void setFitToViewportCHB(JCheckBox fitToViewportCHB) {
        this.overViewCHB = fitToViewportCHB;
    }

    public JComboBox getFlurstueckChoserCB() {
        return flurstueckChoserCB;
    }

    public void setFlurstueckChoserCB(JComboBox flurstueckChoserCB) {
        this.flurstueckChoserCB = flurstueckChoserCB;
    }

    public JLabel getHistoricSinceInfoLabel() {
        return historicSinceInfoLabel;
    }

    public void setHistoricSinceInfoLabel(JLabel historicSinceInfoLabel) {
        this.historicSinceInfoLabel = historicSinceInfoLabel;
    }

    public JLabel getHistoricSinceLabel() {
        return historicSinceLabel;
    }

    public void setHistoricSinceLabel(JLabel historicSinceLabel) {
        this.historicSinceLabel = historicSinceLabel;
    }

    public JPanel getHistoryInformationPanel() {
        return historyInformationPanel;
    }

    public void setHistoryInformationPanel(JPanel historyInformationPanel) {
        this.historyInformationPanel = historyInformationPanel;
    }

    public JPanel getHistoryOptionPanel() {
        return historyOptionPanel;
    }

    public void setHistoryOptionPanel(JPanel historyOptionPanel) {
        this.historyOptionPanel = historyOptionPanel;
    }

    public JCheckBox getHoldHistoryCHB() {
        return holdHistoryCHB;
    }

    public void setHoldHistoryCHB(JCheckBox holdHistoryCHB) {
        this.holdHistoryCHB = holdHistoryCHB;
    }

    public HistoryLevel getLevel() {
        return level;
    }

    public void setLevel(HistoryLevel level) {
        this.level = level;
    }

    public SceneLayout getSceneLayout() {
        return sceneLayout;
    }

    public void setSceneLayout(SceneLayout sceneLayout) {
        this.sceneLayout = sceneLayout;
    }

    @Override
    public void configure(org.jdom.Element parent) {
    }

    @Override
    public org.jdom.Element getConfiguration() throws NoWriteError {
        return null;
    }

    @Override
    public void masterConfigure(Element arg0) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("flurstueck_selected")) {
            Flurstueck f = (Flurstueck) evt.getNewValue();

            Date creationDate = f.getFlurstueckSchluessel().getEntstehungsDatum();
            Date historicSince = f.getFlurstueckSchluessel().getGueltigBis();

            if (creationDate != null) {
                creationDateInfoLabel.setText(sdf.format(creationDate));
            } else {
                creationDateInfoLabel.setText("keine Angaben");
            }

            if (historicSince != null) {
                historicSinceInfoLabel.setText(sdf.format(historicSince));
            } else {
                historicSinceInfoLabel.setText("Flurstück ist aktuell");
            }

        } else if (evt.getPropertyName().equals("startUpdate")) {

            graphPane.setViewportView(progressPanel);
            progressPanel.setSize(graphPane.getSize());
            progressPanel.start();
            
            
        } else if (evt.getPropertyName().equals("endUpdate")) {

            progressPanel.stop();
            graphPane.setViewportView(view);

            Widget currentFlurstueckWidget = graphScene.findWidget(currentFlurstueck);
            Point location = currentFlurstueckWidget.getPreferredLocation();
            
            JViewport vp = graphPane.getViewport();
            Rectangle viewRect = vp.getViewRect();
            
            Point viewPosition = new Point();
            Rectangle widgetDim = currentFlurstueckWidget.getBounds();
            viewPosition.x = location.x + widgetDim.width/2 - viewRect.width/2;
            viewPosition.y = location.y + widgetDim.height/2 - viewRect.height/2;

            if(viewPosition.x < 0)
                viewPosition.x = 0;

            if(viewPosition.y < 0)
                viewPosition.y = 0;
            
            vp.setViewPosition(viewPosition);

        }
    }

    private class GraphUpdateThread<T> extends BackgroundUpdateThread<T> {

        private PropertyChangeSupport propSup = new PropertyChangeSupport(this);

        @Override
        protected void update() {

            propSup.firePropertyChange("startUpdate", 0, 1);
            updateGraph();
            propSup.firePropertyChange("endUpdate", 1, 0);
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            propSup.addPropertyChangeListener(listener);
        }
    }
}
