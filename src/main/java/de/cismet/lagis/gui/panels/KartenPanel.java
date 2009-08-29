/*
 * KartenPanel.java
 *
 * Created on 16. März 2007, 12:04
 */
package de.cismet.lagis.gui.panels;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import de.cismet.cismap.commons.features.DefaultWFSFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollectionEvent;
import de.cismet.cismap.commons.features.FeatureCollectionListener;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.AttachFeatureListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.CreateGeometryListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.DeleteFeatureListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.FeatureMoveListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.JoinPolygonsListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.SelectionListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.SimpleMoveListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.SplitPolygonListener;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.NoPermissionsWidget;
import de.cismet.lagis.utillity.GeometrySlotInformation;
import de.cismet.lagis.widget.AbstractWidget;
import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.core.Geom;
import de.cismet.lagisEE.entity.core.ReBe;
import de.cismet.lagisEE.entity.core.Verwaltungsbereich;
import de.cismet.lagisEE.entity.core.hardwired.Gemarkung;
import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.NoWriteError;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.historybutton.JHistoryButton;
import edu.umd.cs.piccolo.PNode;

import edu.umd.cs.piccolox.event.PNotification;
import edu.umd.cs.piccolox.event.PNotificationCenter;
import edu.umd.cs.piccolox.event.PSelectionEventHandler;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import org.jdom.Element;

/**
 *
 * @author  Puhl
 */
public class KartenPanel extends AbstractWidget implements FlurstueckChangeListener, FeatureCollectionListener, Configurable, NoPermissionsWidget {

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private MappingComponent mappingComponent;
    private static final String WIDGET_NAME = "Karten Panel";
    private boolean isSnappingEnabled = false;
    private String gemarkungIdentifier = null;
    private String flurIdentifier = null;
    private String flurstueckZaehlerIdentifier = null;
    private String flurstueckNennerIdentifier = null;

    /** Creates new form KartenPanel */
    public KartenPanel() {
        setIsCoreWidget(true);        
        initComponents();
        mappingComponent = CismapBroker.getInstance().getMappingComponent();
        mappingComponent.getFeatureCollection().addFeatureCollectionListener(this);
        mappingComponent.setBackgroundEnabled(true);
        PNotificationCenter.defaultCenter().addListener(this,
                "attachFeatureRequested",
                AttachFeatureListener.ATTACH_FEATURE_NOTIFICATION,
                mappingComponent.getInputListener(MappingComponent.ATTACH_POLYGON_TO_ALPHADATA));
        PNotificationCenter.defaultCenter().addListener(this,
                "selectionChanged",
                SplitPolygonListener.SELECTION_CHANGED,
                mappingComponent.getInputListener(MappingComponent.SPLIT_POLYGON));
        PNotificationCenter.defaultCenter().addListener(this,
                "splitPolygon",
                SplitPolygonListener.SPLIT_FINISHED,
                mappingComponent.getInputListener(MappingComponent.SPLIT_POLYGON));
        PNotificationCenter.defaultCenter().addListener(this,
                "featureDeleteRequested",
                DeleteFeatureListener.FEATURE_DELETE_REQUEST_NOTIFICATION,
                mappingComponent.getInputListener(MappingComponent.REMOVE_POLYGON));
        PNotificationCenter.defaultCenter().addListener(this,
                "joinPolygons",
                JoinPolygonsListener.FEATURE_JOIN_REQUEST_NOTIFICATION,
                mappingComponent.getInputListener(MappingComponent.JOIN_POLYGONS));
        PNotificationCenter.defaultCenter().addListener(this,
                "selectionChanged",
                PSelectionEventHandler.SELECTION_CHANGED_NOTIFICATION,
                mappingComponent.getInputListener(MappingComponent.SELECT));
        PNotificationCenter.defaultCenter().addListener(this,
                "selectionChanged",
                FeatureMoveListener.SELECTION_CHANGED_NOTIFICATION,
                mappingComponent.getInputListener(MappingComponent.MOVE_POLYGON));
        PNotificationCenter.defaultCenter().addListener(this,
                "coordinatesChanged",
                SimpleMoveListener.COORDINATES_CHANGED,
                mappingComponent.getInputListener(MappingComponent.MOTION));
        ((JHistoryButton) cmdForward).setDirection(JHistoryButton.DIRECTION_FORWARD);
        ((JHistoryButton) cmdBack).setDirection(JHistoryButton.DIRECTION_BACKWARD);
        ((JHistoryButton) cmdForward).setHistoryModel(mappingComponent);
        ((JHistoryButton) cmdBack).setHistoryModel(mappingComponent);
        mappingComponent.setBackground(getBackground());
        mappingComponent.setBackgroundEnabled(true);
        this.add(BorderLayout.CENTER, mappingComponent);
    //TODO make enumartion for InteractionModes
    }

    public void setInteractionMode() {
        String currentInteractionMode = mappingComponent.getInteractionMode();
        if (currentInteractionMode != null) {

            if (currentInteractionMode.equals(MappingComponent.SELECT)) {
                log.debug("InteractionMode set to SELCET");
                cmdSelectActionPerformed(null);
            } else if (currentInteractionMode.equals(MappingComponent.CUSTOM_FEATUREINFO)) {
                log.debug("InteractionMode set to CUSTOM_FEATUREINFO");
                //cmdALB.setSelected(true);
                cmdALBActionPerformed(null);
            } else if (currentInteractionMode.equals(MappingComponent.PAN)) {
                log.debug("InteractionMode set to PAN");
                cmdPanActionPerformed(null);
            } else if (currentInteractionMode.equals(MappingComponent.NEW_POLYGON)) {
                log.debug("InteractionMode set to NEW_POLYGON");
                cmdNewPolygonActionPerformed(null);
            } else if (currentInteractionMode.equals(MappingComponent.ZOOM)) {
                log.debug("InteractionMode set to ZOOM");
                cmdZoomActionPerformed(null);
            } else {
                log.debug("Unknown Interactionmode: " + currentInteractionMode);
            }
        } else {
            log.debug("InteractionMode == null");
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        cmdFullPoly = new javax.swing.JButton();
        cmdFullPoly1 = new javax.swing.JButton();
        cmdBack = new JHistoryButton();
        cmdForward = new JHistoryButton();
        jSeparator4 = new javax.swing.JSeparator();
        cmdWmsBackground = new javax.swing.JButton();
        cmdForeground = new javax.swing.JButton();
        cmdSnap = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        cmdZoom = new javax.swing.JButton();
        cmdPan = new javax.swing.JButton();
        cmdSelect = new javax.swing.JButton();
        cmdALB = new javax.swing.JButton();
        cmdMovePolygon = new javax.swing.JButton();
        cmdNewPolygon = new javax.swing.JButton();
        cmdNewPoint = new javax.swing.JButton();
        cmdRaisePolygon = new javax.swing.JButton();
        cmdRemovePolygon = new javax.swing.JButton();
        cmdAttachPolyToAlphadata = new javax.swing.JButton();
        cmdJoinPoly = new javax.swing.JButton();
        cmdSplitPoly = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JSeparator();
        cmdMoveHandle = new javax.swing.JButton();
        cmdAddHandle = new javax.swing.JButton();
        cmdRemoveHandle = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        cmdAdd = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jToolBar1.setMaximumSize(new java.awt.Dimension(32769, 32769));
        jToolBar1.setMinimumSize(new java.awt.Dimension(300, 25));
        jToolBar1.setPreferredSize(new java.awt.Dimension(300, 28));

        cmdFullPoly.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/fullPoly.png"))); // NOI18N
        cmdFullPoly.setToolTipText("Zeige alle Flächen");
        cmdFullPoly.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdFullPoly.setIconTextGap(8);
        cmdFullPoly.setMargin(new java.awt.Insets(10, 14, 10, 14));
        cmdFullPoly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdFullPolyActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdFullPoly);

        cmdFullPoly1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/fullSelPoly.png"))); // NOI18N
        cmdFullPoly1.setToolTipText("Zoom zur ausgewählten Fläche");
        cmdFullPoly1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdFullPoly1.setIconTextGap(8);
        cmdFullPoly1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdFullPoly1ActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdFullPoly1);

        cmdBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/back2.png"))); // NOI18N
        cmdBack.setToolTipText("Zurück");
        cmdBack.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdBackActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdBack);

        cmdForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/fwd.png"))); // NOI18N
        cmdForward.setToolTipText("Vor");
        cmdForward.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdForwardActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdForward);

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator4.setMaximumSize(new java.awt.Dimension(2, 32767));
        jSeparator4.setMinimumSize(new java.awt.Dimension(2, 10));
        jSeparator4.setPreferredSize(new java.awt.Dimension(2, 10));
        jToolBar1.add(jSeparator4);

        cmdWmsBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/map.png"))); // NOI18N
        cmdWmsBackground.setToolTipText("Hintergrund an/aus");
        cmdWmsBackground.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdWmsBackground.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/map_on.png"))); // NOI18N
        cmdWmsBackground.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdWmsBackgroundActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdWmsBackground);

        cmdForeground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/foreground.png"))); // NOI18N
        cmdForeground.setToolTipText("Vordergrund an/aus");
        cmdForeground.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdForeground.setFocusable(false);
        cmdForeground.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdForeground.setSelected(true);
        cmdForeground.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/foreground_on.png"))); // NOI18N
        cmdForeground.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdForeground.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdForegroundActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdForeground);

        cmdSnap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/snap.png"))); // NOI18N
        cmdSnap.setToolTipText("Snapping an/aus");
        cmdSnap.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdSnap.setSelected(true);
        cmdSnap.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/snap_selected.png"))); // NOI18N
        cmdSnap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSnapActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdSnap);

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator5.setMaximumSize(new java.awt.Dimension(2, 32767));
        jSeparator5.setMinimumSize(new java.awt.Dimension(2, 10));
        jSeparator5.setPreferredSize(new java.awt.Dimension(2, 10));
        jToolBar1.add(jSeparator5);

        cmdZoom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/zoom.png"))); // NOI18N
        cmdZoom.setToolTipText("Zoomen");
        cmdZoom.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdZoom.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/zoom_selected.png"))); // NOI18N
        cmdZoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdZoomActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdZoom);

        cmdPan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/move2.png"))); // NOI18N
        cmdPan.setToolTipText("Verschieben");
        cmdPan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdPan.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/move2_selected.png"))); // NOI18N
        cmdPan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPanActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdPan);

        cmdSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/select.png"))); // NOI18N
        cmdSelect.setToolTipText("Auswählen");
        cmdSelect.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdSelect.setSelected(true);
        cmdSelect.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/select_selected.png"))); // NOI18N
        cmdSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdSelect);

        cmdALB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/alb.png"))); // NOI18N
        cmdALB.setToolTipText("Auswählen");
        cmdALB.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cmdALB.setFocusPainted(false);
        cmdALB.setFocusable(false);
        cmdALB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdALB.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/alb_selected.png"))); // NOI18N
        cmdALB.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/alb_selected.png"))); // NOI18N
        cmdALB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdALB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdALBActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdALB);

        cmdMovePolygon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/movePoly.png"))); // NOI18N
        cmdMovePolygon.setToolTipText("Polygon verschieben");
        cmdMovePolygon.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdMovePolygon.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/movePoly_selected.png"))); // NOI18N
        cmdMovePolygon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdMovePolygonActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdMovePolygon);

        cmdNewPolygon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/newPoly.png"))); // NOI18N
        cmdNewPolygon.setToolTipText("neues Polygon");
        cmdNewPolygon.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdNewPolygon.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/newPoly_selected.png"))); // NOI18N
        cmdNewPolygon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdNewPolygonActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdNewPolygon);

        cmdNewPoint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/newPoint.png"))); // NOI18N
        cmdNewPoint.setToolTipText("neuer Punkt");
        cmdNewPoint.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdNewPoint.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/newPoint_selected.png"))); // NOI18N
        cmdNewPoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdNewPointActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdNewPoint);

        cmdRaisePolygon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/raisePoly.png"))); // NOI18N
        cmdRaisePolygon.setToolTipText("Polygon hochholen");
        cmdRaisePolygon.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdRaisePolygon.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/raisePoly_selected.png"))); // NOI18N
        cmdRaisePolygon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRaisePolygonActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdRaisePolygon);

        cmdRemovePolygon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/removePoly.png"))); // NOI18N
        cmdRemovePolygon.setToolTipText("Polygon entfernen");
        cmdRemovePolygon.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdRemovePolygon.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/removePoly_selected.png"))); // NOI18N
        cmdRemovePolygon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRemovePolygonActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdRemovePolygon);

        cmdAttachPolyToAlphadata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/polygonAttachment.png"))); // NOI18N
        cmdAttachPolyToAlphadata.setToolTipText("Polygon zuordnen");
        cmdAttachPolyToAlphadata.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdAttachPolyToAlphadata.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/polygonAttachment_selected.png"))); // NOI18N
        cmdAttachPolyToAlphadata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAttachPolyToAlphadataActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdAttachPolyToAlphadata);

        cmdJoinPoly.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/joinPoly.png"))); // NOI18N
        cmdJoinPoly.setToolTipText("Polygone zusammenfassen");
        cmdJoinPoly.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdJoinPoly.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/joinPoly_selected.png"))); // NOI18N
        cmdJoinPoly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdJoinPolyActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdJoinPoly);

        cmdSplitPoly.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/splitPoly.png"))); // NOI18N
        cmdSplitPoly.setToolTipText("Polygon splitten");
        cmdSplitPoly.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdSplitPoly.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/splitPoly_selected.png"))); // NOI18N
        cmdSplitPoly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSplitPolyActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdSplitPoly);

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator6.setMaximumSize(new java.awt.Dimension(2, 32767));
        jSeparator6.setMinimumSize(new java.awt.Dimension(2, 10));
        jSeparator6.setPreferredSize(new java.awt.Dimension(2, 10));
        jToolBar1.add(jSeparator6);

        cmdMoveHandle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/moveHandle.png"))); // NOI18N
        cmdMoveHandle.setToolTipText("Handle verschieben");
        cmdMoveHandle.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdMoveHandle.setSelected(true);
        cmdMoveHandle.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/moveHandle_selected.png"))); // NOI18N
        cmdMoveHandle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdMoveHandleActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdMoveHandle);

        cmdAddHandle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/addHandle.png"))); // NOI18N
        cmdAddHandle.setToolTipText("Handle hinzufügen");
        cmdAddHandle.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdAddHandle.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/addHandle_selected.png"))); // NOI18N
        cmdAddHandle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddHandleActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdAddHandle);

        cmdRemoveHandle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/removeHandle.png"))); // NOI18N
        cmdRemoveHandle.setToolTipText("Handle entfernen");
        cmdRemoveHandle.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdRemoveHandle.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/removeHandle_selected.png"))); // NOI18N
        cmdRemoveHandle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRemoveHandleActionPerformed(evt);
            }
        });
        jToolBar1.add(cmdRemoveHandle);

        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator7.setMaximumSize(new java.awt.Dimension(2, 32767));
        jSeparator7.setMinimumSize(new java.awt.Dimension(2, 10));
        jSeparator7.setPreferredSize(new java.awt.Dimension(2, 10));
        jToolBar1.add(jSeparator7);

        add(jToolBar1, java.awt.BorderLayout.NORTH);

        jPanel1.setMinimumSize(new java.awt.Dimension(50, 100));
        jPanel1.setPreferredSize(new java.awt.Dimension(50, 30));

        cmdAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/layersman.png"))); // NOI18N
        cmdAdd.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        cmdAdd.setBorderPainted(false);
        cmdAdd.setFocusPainted(false);
        cmdAdd.setMinimumSize(new java.awt.Dimension(25, 25));
        cmdAdd.setPreferredSize(new java.awt.Dimension(25, 25));
        cmdAdd.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/layersman.png"))); // NOI18N
        cmdAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(599, Short.MAX_VALUE)
                .add(cmdAdd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(cmdAdd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel1, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents
    private void cmdMovePolygonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdMovePolygonActionPerformed
        removeMainGroupSelection();
        cmdMovePolygon.setSelected(true);
        mappingComponent.setInteractionMode(MappingComponent.MOVE_POLYGON);
    }//GEN-LAST:event_cmdMovePolygonActionPerformed
    
private void cmdAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddActionPerformed
    mappingComponent.showInternalLayerWidget(!mappingComponent.isInternalLayerWidgetVisible(), 500);
}//GEN-LAST:event_cmdAddActionPerformed

private void cmdRemoveHandleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRemoveHandleActionPerformed
    removeHandleGroupSelection();
    cmdRemoveHandle.setSelected(true);
    mappingComponent.setHandleInteractionMode(MappingComponent.REMOVE_HANDLE);
}//GEN-LAST:event_cmdRemoveHandleActionPerformed

private void cmdAddHandleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddHandleActionPerformed
    removeHandleGroupSelection();
    cmdAddHandle.setSelected(true);
    mappingComponent.setHandleInteractionMode(MappingComponent.ADD_HANDLE);
}//GEN-LAST:event_cmdAddHandleActionPerformed

private void cmdMoveHandleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdMoveHandleActionPerformed
    removeHandleGroupSelection();
    cmdMoveHandle.setSelected(true);
    mappingComponent.setHandleInteractionMode(MappingComponent.MOVE_HANDLE);
}//GEN-LAST:event_cmdMoveHandleActionPerformed

private void cmdSplitPolyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSplitPolyActionPerformed
    removeMainGroupSelection();
    cmdSplitPoly.setSelected(true);
    mappingComponent.setInteractionMode(MappingComponent.SPLIT_POLYGON );
}//GEN-LAST:event_cmdSplitPolyActionPerformed

private void cmdJoinPolyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdJoinPolyActionPerformed
    removeMainGroupSelection();
    cmdJoinPoly.setSelected(true);
    mappingComponent.setInteractionMode(MappingComponent.JOIN_POLYGONS );
}//GEN-LAST:event_cmdJoinPolyActionPerformed

private void cmdAttachPolyToAlphadataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAttachPolyToAlphadataActionPerformed
    removeMainGroupSelection();
    cmdAttachPolyToAlphadata.setSelected(true);
    mappingComponent.setInteractionMode(MappingComponent.ATTACH_POLYGON_TO_ALPHADATA );
}//GEN-LAST:event_cmdAttachPolyToAlphadataActionPerformed

private void cmdRemovePolygonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRemovePolygonActionPerformed
    removeMainGroupSelection();
    cmdRemovePolygon.setSelected(true);
    mappingComponent.setInteractionMode(MappingComponent.REMOVE_POLYGON );
}//GEN-LAST:event_cmdRemovePolygonActionPerformed

private void cmdRaisePolygonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRaisePolygonActionPerformed
    removeMainGroupSelection();
    cmdRaisePolygon.setSelected(true);
    mappingComponent.setInteractionMode(MappingComponent.RAISE_POLYGON );
}//GEN-LAST:event_cmdRaisePolygonActionPerformed

private void cmdNewPointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdNewPointActionPerformed
    removeMainGroupSelection();
    //TODO READING THE SNAPENAB --> FAILURE
    //boolean snapEnab=cismapPrefs.getGlobalPrefs().isSnappingEnabled();
    //boolean snapVizEnab=cismapPrefs.getGlobalPrefs().isSnappingPreviewEnabled();
    boolean snapEnab= true;
    boolean snapVizEnab=true;
    mappingComponent.setSnappingEnabled(snapEnab);
    mappingComponent.setVisualizeSnappingEnabled(snapVizEnab);
    cmdNewPoint.setSelected(true);
    mappingComponent.setInteractionMode(MappingComponent.NEW_POLYGON );
    ((CreateGeometryListener)mappingComponent.getInputListener(MappingComponent.NEW_POLYGON)).setMode(CreateGeometryListener.POINT);
}//GEN-LAST:event_cmdNewPointActionPerformed

private void cmdNewPolygonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdNewPolygonActionPerformed
    removeMainGroupSelection();
    //TODO READING THE SNAPENAB --> FAILURE
    //boolean snapEnab=cismapPrefs.getGlobalPrefs().isSnappingEnabled();
    //boolean snapVizEnab=cismapPrefs.getGlobalPrefs().isSnappingPreviewEnabled();
    boolean snapEnab= true;
    boolean snapVizEnab=true;
    mappingComponent.setSnappingEnabled(snapEnab);
    mappingComponent.setVisualizeSnappingEnabled(snapVizEnab);
    cmdNewPolygon.setSelected(true);
    mappingComponent.setInteractionMode(MappingComponent.NEW_POLYGON );
    ((CreateGeometryListener)mappingComponent.getInputListener(MappingComponent.NEW_POLYGON)).setMode(CreateGeometryListener.POLYGON);
}//GEN-LAST:event_cmdNewPolygonActionPerformed

private void cmdSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSelectActionPerformed
    removeMainGroupSelection();
    cmdSelect.setSelected(true);
    mappingComponent.setInteractionMode(MappingComponent.SELECT);
    cmdMoveHandleActionPerformed(null);
}//GEN-LAST:event_cmdSelectActionPerformed

private void removeHandleGroupSelection() {
    cmdRemoveHandle.setSelected(false);
    cmdAddHandle.setSelected(false);
    cmdMoveHandle.setSelected(false);
}

private void cmdPanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPanActionPerformed
    removeMainGroupSelection();
    cmdPan.setSelected(true);
    mappingComponent.setInteractionMode(MappingComponent.PAN);
}//GEN-LAST:event_cmdPanActionPerformed

private void removeMainGroupSelection() {
    cmdSelect.setSelected(false);
    cmdPan.setSelected(false);
    cmdALB.setSelected(false);
    cmdZoom.setSelected(false);
    cmdMovePolygon.setSelected(false);
    cmdNewPolygon.setSelected(false);
    cmdNewPoint.setSelected(false);
    cmdRemovePolygon.setSelected(false);
    cmdAttachPolyToAlphadata.setSelected(false);
    cmdSplitPoly.setSelected(false);
    cmdJoinPoly.setSelected(false);
    cmdRaisePolygon.setSelected(false);
    
    if (mappingComponent.isReadOnly()) {
        log.info("Ist Readonly snapping wird diseabled");
        mappingComponent.setSnappingEnabled(false);
        mappingComponent.setVisualizeSnappingEnabled(false);
    }
}

public void flurstueckChanged(Flurstueck newFlurstueck) {
    LagisBroker.getInstance().flurstueckChangeFinished(this);
}
private boolean isEditable=true;
public synchronized void setComponentEditable(final boolean isEditable) {
    if(this.isEditable == isEditable){
        return;
    }
    this.isEditable = isEditable;
    log.debug("MapPanel --> setComponentEditable");
    if(EventQueue.isDispatchThread()){
        mappingComponent.setReadOnly(!isEditable);
        //TODO only change if the actualMode is not allowed
        if(!isEditable){
            //mappingComponent.setInteractionMode(mappingComponent.PAN);
            //TODO is it really the best default mode ?
            //TODO look how to easily create events (or common)
            removeMainGroupSelection();
            cmdSelect.setSelected(true);
            mappingComponent.setInteractionMode(MappingComponent.SELECT);
            cmdMoveHandleActionPerformed(null);
        }
        log.debug("Anzahl Features in FeatureCollection:"+mappingComponent.getFeatureCollection().getFeatureCount());
        //((DefaultFeatureCollection)mappingComponent.getFeatureCollection()).setAllFeaturesEditable(isEditable);
        //TODO TEST IT!!!!
        LagisBroker.getInstance().getMappingComponent().setReadOnly(!isEditable);
        cmdMovePolygon.setVisible(isEditable);
        //this.cmdNewPolygon.setVisible(b);
        cmdRemovePolygon.setVisible(isEditable);
        cmdAttachPolyToAlphadata.setVisible(isEditable);
        cmdJoinPoly.setVisible(isEditable);
        jSeparator6.setVisible(isEditable);
        cmdMoveHandle.setVisible(isEditable);
        cmdAddHandle.setVisible(isEditable);
        cmdRemoveHandle.setVisible(isEditable);
        cmdSplitPoly.setVisible(isEditable);
        cmdRaisePolygon.setVisible(isEditable);
        jSeparator6.setVisible(isEditable);
    }else {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                mappingComponent.setReadOnly(!isEditable);
                //TODO only change if the actualMode is not allowed
                if(!isEditable){
                    //mappingComponent.setInteractionMode(mappingComponent.PAN);
                    //TODO is it really the best default mode ?
                    //TODO look how to easily create events (or common)
                    removeMainGroupSelection();
                    cmdSelect.setSelected(true);
                    mappingComponent.setInteractionMode(MappingComponent.SELECT);
                    cmdMoveHandleActionPerformed(null);
                }
                log.debug("Anzahl Features in FeatureCollection:"+mappingComponent.getFeatureCollection().getFeatureCount());
                //((DefaultFeatureCollection)mappingComponent.getFeatureCollection()).setAllFeaturesEditable(isEditable);
                //TODO TEST IT!!!!
                LagisBroker.getInstance().getMappingComponent().setReadOnly(!isEditable);
                cmdMovePolygon.setVisible(isEditable);
                //this.cmdNewPolygon.setVisible(b);
                cmdRemovePolygon.setVisible(isEditable);
                cmdAttachPolyToAlphadata.setVisible(isEditable);
                cmdJoinPoly.setVisible(isEditable);
                jSeparator6.setVisible(isEditable);
                cmdMoveHandle.setVisible(isEditable);
                cmdAddHandle.setVisible(isEditable);
                cmdRemoveHandle.setVisible(isEditable);
                cmdSplitPoly.setVisible(isEditable);
                cmdRaisePolygon.setVisible(isEditable);
                jSeparator6.setVisible(isEditable);
            }
        });
    }
    log.debug("MapPanel --> setComponentEditable finished");
}

public synchronized void clearComponent() {
}

private void cmdZoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdZoomActionPerformed
    removeMainGroupSelection();
    cmdZoom.setSelected(true);
    mappingComponent.setInteractionMode(MappingComponent.ZOOM);
}//GEN-LAST:event_cmdZoomActionPerformed

private void cmdSnapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSnapActionPerformed
    log.debug("Set snapping Enabled: "+cmdSnap.isSelected());
    cmdSnap.setSelected(!cmdSnap.isSelected());
    //TODO CHANGE CONFIG FILE ACTION
    //cismapPrefs.getGlobalPrefs().setSnappingEnabled(cmdSnap.isSelected());
    //cismapPrefs.getGlobalPrefs().setSnappingPreviewEnabled(cmdSnap.isSelected());
    mappingComponent.setSnappingEnabled(!mappingComponent.isReadOnly()&&cmdSnap.isSelected());
    mappingComponent.setVisualizeSnappingEnabled(!mappingComponent.isReadOnly()&&cmdSnap.isSelected());
    mappingComponent.setInGlueIdenticalPointsMode(cmdSnap.isSelected());
}//GEN-LAST:event_cmdSnapActionPerformed

    private void cmdWmsBackgroundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdWmsBackgroundActionPerformed
        if (mappingComponent.isBackgroundEnabled()) {
            mappingComponent.setBackgroundEnabled(false);
            cmdWmsBackground.setSelected(false);
        } else {
            mappingComponent.setBackgroundEnabled(true);
            cmdWmsBackground.setSelected(true);
            mappingComponent.queryServices();
        }
}//GEN-LAST:event_cmdWmsBackgroundActionPerformed
    
    
private void cmdForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdForwardActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_cmdForwardActionPerformed

private void cmdBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBackActionPerformed
    
}//GEN-LAST:event_cmdBackActionPerformed

private void cmdFullPoly1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdFullPoly1ActionPerformed
    mappingComponent.zoomToSelectedNode();
}//GEN-LAST:event_cmdFullPoly1ActionPerformed

private void cmdFullPolyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdFullPolyActionPerformed
    mappingComponent.zoomToFullFeatureCollectionBounds();
}//GEN-LAST:event_cmdFullPolyActionPerformed

private void cmdALBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdALBActionPerformed
    log.info("ALB");
    removeMainGroupSelection();
    cmdALB.setSelected(true);
    mappingComponent.setInteractionMode(MappingComponent.CUSTOM_FEATUREINFO);
}//GEN-LAST:event_cmdALBActionPerformed

private void cmdForegroundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdForegroundActionPerformed
    if (mappingComponent.isFeatureCollectionVisible()) {
            mappingComponent.setFeatureCollectionVisibility(false);
            cmdForeground.setSelected(false);
        } else {
            mappingComponent.setFeatureCollectionVisibility(true);
            cmdForeground.setSelected(true);            
        }
}//GEN-LAST:event_cmdForegroundActionPerformed


public void featureDeleteRequested(PNotification notfication) {
    try{
        Object o=notfication.getObject();
        if (o instanceof DeleteFeatureListener) {
            DeleteFeatureListener dfl=(DeleteFeatureListener)o;
            PFeature pf=dfl.getFeatureRequestedForDeletion();
            pf.getFeature().setGeometry(null);
            if(pf.getFeature() instanceof Verwaltungsbereich){
                log.debug("Verwaltungsbereichsgeometrie wurde gelöscht setze Flächee = 0");                
            }
        }
    }catch(Exception ex){
        log.warn("Fehler beim featuredeleteRequest",ex);
    }
}

public void joinPolygons(PNotification notification) {
    PFeature one,two;
    one=mappingComponent.getSelectedNode();
    two=null;
    log.debug("");
    Object o=notification.getObject();
    
    if (o instanceof JoinPolygonsListener) {
        JoinPolygonsListener listener=((JoinPolygonsListener)o);
        PFeature joinCandidate=listener.getFeatureRequestedForJoin();
        if (joinCandidate.getFeature() instanceof StyledFeature || joinCandidate.getFeature() instanceof PureNewFeature) {
            int CTRL_MASK=2; //TODO: HIer noch eine korrekte Konstante verwenden
            if ((listener.getModifier()&CTRL_MASK)!=0) {
                
                if (one!=null && joinCandidate!=one) {
                    if (one.getFeature() instanceof PureNewFeature && joinCandidate.getFeature() instanceof StyledFeature) {
                        two=one;
                        
                        one=joinCandidate;
                        one.setSelected(true);
                        two.setSelected(false);
                        mappingComponent.getFeatureCollection().select(one.getFeature());
                        //tableModel.setSelectedFlaeche((Flaeche)one.getFeature());
                        //TODO implement or erase
                        //fireAuswahlChanged(one.getFeature());
                    } else {
                        two=joinCandidate;
                    }
                    try {
                        
                        Geometry backup=one.getFeature().getGeometry();
                        Geometry newGeom=one.getFeature().getGeometry().union(two.getFeature().getGeometry());
                        if (newGeom.getGeometryType().equalsIgnoreCase("Multipolygon")) {
                            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),"Es können nur Polygone zusammengefasst werden, die aneinander angrenzen oder sich überlappen.","Zusammenfassung nicht möglich",JOptionPane.WARNING_MESSAGE,null );
                            return;
                        }
                        if (newGeom.getGeometryType().equalsIgnoreCase("Polygon")&&((Polygon)newGeom).getNumInteriorRing()>0) {
                            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),"Polygone können nur dann zusammengefasst werden, wenn dadurch kein Loch entsteht.","Zusammenfassung nicht möglich",JOptionPane.WARNING_MESSAGE,null );
                            return;
                        }
                        if (one!=null&&two!=null&&one.getFeature() instanceof StyledFeature &&two.getFeature() instanceof StyledFeature) {
                            StyledFeature fOne=(StyledFeature)one.getFeature();
                            StyledFeature fTwo=(StyledFeature)two.getFeature();
                            
                            if((fOne instanceof Verwaltungsbereich && !(fTwo instanceof Verwaltungsbereich)) || (fTwo instanceof Verwaltungsbereich && !(fOne instanceof Verwaltungsbereich))){
                                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),"Flächeen können nur zusammengefasst werden, wenn die Flächeenart gleich ist.","Zusammenfassung nicht möglich",JOptionPane.WARNING_MESSAGE,null );
                                return;
                            }
                            
                            if((fOne instanceof ReBe && !(fTwo instanceof ReBe)) || (fTwo instanceof ReBe && !(fOne instanceof ReBe))){
                                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),"Flächeen können nur zusammengefasst werden, wenn die Flächeenart gleich ist.","Zusammenfassung nicht möglich",JOptionPane.WARNING_MESSAGE,null );
                                return;
                            }
                            
//                            if (fOne.getArt()!=fTwo.getArt()||fOne.getGrad()!=fTwo.getGrad()) {
//                                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),"Flächeen können nur zusammengefasst werden, wenn Flächeenart und Anschlussgrad gleich sind.","Zusammenfassung nicht möglich",JOptionPane.WARNING_MESSAGE,null );
//                                return;
//                            }
//                            //Check machen ob eine Flächee eine Teilflächee ist
//                            if (fOne.getAnteil()!=null || fTwo.getAnteil()!=null) {
//                                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),"Flächeen die von Teileigentum betroffen sind können nicht zusammengefasst werden.","Zusammenfassung nicht möglich",JOptionPane.WARNING_MESSAGE,null );
//                                return;
//                            }
                            two.getFeature().setGeometry(null);
                            //tableModel.removeFlaeche(fTwo);
                            //TODO größe updaten
                            
                            //TODO make it right
                            //fOne.setGr_grafik(new Integer((int)(newGeom.getArea())));
//                            if (fOne.getBemerkung()!=null && fOne.getBemerkung().trim().length()>0) {
//                                fOne.setBemerkung(fOne.getBemerkung()+"\n");
//                            }
//                            fOne.setBemerkung(fTwo.getJoinBackupString());
//                            if (!fOne.isSperre()&&fTwo.isSperre()) {
//                                fOne.setSperre(true);
//                                fOne.setBem_sperre("JOIN::"+fTwo.getBem_sperre());
//                            }
//                            fOne.sync();
//                            //tableModel.fireSelectionChanged(); TODO
//                            fireAuswahlChanged(fOne);
                        }
                        if (one.getFeature() instanceof Verwaltungsbereich) {
                            //Eine vorhandene Flächee und eine neuangelegt wurden gejoint                            
                            //((Flaeche)(one.getFeature())).sync();
                            //tableModel.fireSelectionChanged(); TODO
                            //fireAuswahlChanged((Flaeche)(one.getFeature()));
                        }
                        
                        log.debug("newGeom ist vom Typ:"+newGeom.getGeometryType());
                        one.getFeature().setGeometry(newGeom);
                        if (!(one.getFeature().getGeometry().equals(backup))) {
                            two.removeFromParent();
                            two=null;
                        }
                        one.visualize();
                        
                    } catch (Exception e) {
                        log.error("one: "+one+"\n two: "+two,e);
                    }
                    return;
                }
            } else {
                PFeature pf=joinCandidate;
                if (one!=null) one.setSelected(false);
                one=pf;
                mappingComponent.selectPFeatureManually(one);
                if (one.getFeature() instanceof StyledFeature) {
                    StyledFeature f=(StyledFeature)one.getFeature();
                    mappingComponent.getFeatureCollection().select(f);
                    //tableModel.setSelectedFlaeche(f);
                    //fireAuswahlChanged(f);
                    try {
                        //TODO
                        //makeRowVisible(this.jxtOverview,jxtOverview.getFilters().convertRowIndexToView(tableModel.getIndexOfFlaeche((Flaeche)f)));
                    } catch (Exception e) {
                        log.debug("Fehler beim Scrollen der Tabelle",e);
                    }
                } else {
                    //tableModel.setSelectedFlaeche(null);
                    mappingComponent.getFeatureCollection().unselectAll();
                    //fireAuswahlChanged(null);
                }
            }
        }
    }
}

//TODO MEssage to the user if a area could not be attached for example wfs areas
public void attachFeatureRequested(PNotification notification) {
    Object o=notification.getObject();
    log.info("Try to attach Geometry");
    AttachFeatureListener afl=(AttachFeatureListener)o;
    PFeature pf=afl.getFeatureToAttach();
    if(pf.getFeature() instanceof PureNewFeature){
        Geometry g=pf.getFeature().getGeometry();
        GeometrySlotInformation slotInfo = LagisBroker.getInstance().assignGeometry(g);
        if(slotInfo != null){
            slotInfo.getRefreshable().refresh(null);
            mappingComponent.getFeatureCollection().removeFeature(pf.getFeature());
            log.debug("Geometrie: "+slotInfo.getOpenSlot().getGeometry()+" wird hinzugefügt");
            slotInfo.getOpenSlot().setEditable(true);
            mappingComponent.getFeatureCollection().addFeature(slotInfo.getOpenSlot());
            log.debug("Geometrie wurde an element: "+slotInfo.getSlotIdentifier()+" attached");
        } else {
            log.debug("Geometrie wurde nicht attached");
        }
    } else if(pf.getFeature() instanceof Geom){
        
    }
}

public void splitPolygon(PNotification notification) {
    Object o=notification.getObject();
    if (o instanceof SplitPolygonListener) {
        SplitPolygonListener l=(SplitPolygonListener)o;
        PFeature pf=l.getFeatureClickedOn();
        if (pf.isSplittable()) {
            log.debug("Split");
            ((StyledFeature)pf.getFeature()).setGeometry(null);
            Feature[] f_arr=pf.split();
            mappingComponent.getFeatureCollection().removeFeature(pf.getFeature());
            f_arr[0].setEditable(true);
            f_arr[1].setEditable(true);
            mappingComponent.getFeatureCollection().addFeature(f_arr[0]);
            mappingComponent.getFeatureCollection().addFeature(f_arr[1]);
            cmdAttachPolyToAlphadataActionPerformed(null);
        }
    }
}


//ToDo implement
public void coordinatesChanged(PNotification notification) {
//    Object o=notification.getObject();
//    if (o instanceof SimpleMoveListener) {
//        double x=((SimpleMoveListener)o).getXCoord();
//        double y=((SimpleMoveListener)o).getYCoord();
//        double scale=((SimpleMoveListener)o).getCurrentOGCScale();
//
//        //double test= mappingComp.getWtst().getSourceX(36)-this.mappingComp.getWtst().getSourceX(0))/mappingComp.getCamera().getViewScale();
//        //scale +" ... "+
//        //setgetlblCoord.setText(MappingComponent.getCoordinateString(x,y));
//
//    }
    
////        PFeature pf=((SimpleMoveListener)o).getUnderlyingPFeature();
////
////        if (pf!=null&&pf.getFeature() instanceof DefaultFeatureServiceFeature &&pf.getVisible()==true&&pf.getParent()!=null&&pf.getParent().getVisible()==true) {
////            lblInfo.setText(((DefaultFeatureServiceFeature)pf.getFeature()).getObjectName());
////        } else if (pf!=null&&pf.getFeature() instanceof Flaeche) {
////            String name="Kassenzeichen: "+((Flaeche)pf.getFeature()).getKassenzeichen()+"::"+((Flaeche)pf.getFeature()).getBezeichnung();
////            lblInfo.setText(name);
////        } else {
////            lblInfo.setText("");
//        }
    //}
}


public void selectionChanged(PNotification notfication) {
    Object o=notfication.getObject();
    if (o instanceof SelectionListener||o instanceof FeatureMoveListener||o instanceof SplitPolygonListener) {
        PNode p=null;
        PFeature pf=null;
        if (o instanceof SelectionListener) {
            pf=((SelectionListener)o).getSelectedPFeature();
            //
            //                if (pf!=null && pf.getFeature() instanceof Flaeche|| pf.getFeature() instanceof PureNewFeature) {
            //                    if (((DefaultFeatureCollection)mappingComp.getFeatureCollection()).isSelected(pf.getFeature())) {
            //                        if (((DefaultFeatureCollection)mappingComp.getFeatureCollection()).getSelectedFeatures().size()>1) {
            //                            int index=sorter.getSortedPosition(getTableModel().getIndexOfFlaeche((Flaeche)pf.getFeature()));
            //                            tblOverview.getSelectionModel().addSelectionInterval(index,index);
            //                        } else {
            //                            int index=sorter.getSortedPosition(getTableModel().getIndexOfFlaeche((Flaeche)pf.getFeature()));
            //                            tblOverview.getSelectionModel().setSelectionInterval(index,index);
            //                        }
            //                    }
            //                    else {
            //                        int index=sorter.getSortedPosition(getTableModel().getIndexOfFlaeche((Flaeche)pf.getFeature()));
            //                        tblOverview.getSelectionModel().removeSelectionInterval(index,index);
            //                    }
            //                } else
            if (cmdSelect.isSelected() && ((SelectionListener)o).getClickCount()>1 && pf.getFeature() instanceof DefaultWFSFeature) {
            log.debug("DefaultWFSFeature selected");
            //log.debug("test"+((DefaultWFSFeature)pf.getFeature()).getProperties());
                DefaultWFSFeature dwf =((DefaultWFSFeature)pf.getFeature());
                    if(LagisBroker.getInstance().isInEditMode()){
                        log.debug("Flurstück kann nicht gewechselt werden --> Editmode");
                        JOptionPane.showMessageDialog(LagisBroker.getInstance().getParentComponent(),"Das Flurstück kann nur gewechselt werden, wenn alle Änderungen gespeichert oder verworfen worden sind.","Wechseln nicht möglich",JOptionPane.WARNING_MESSAGE);
                        return;
                    }
//                    
                    HashMap props = dwf.getProperties();
                    log.debug("WFSFeature properties: "+props);
                    try{
                        if(props != null && checkIfIdentifiersAreSetProperly()){
                            String gem = (String)props.get(gemarkungIdentifier);
                            String flur =  (String)props.get(flurIdentifier);
                            String flurstz= (String)props.get(flurstueckZaehlerIdentifier);
                            String flurstn= (String)props.get(flurstueckNennerIdentifier);
                            if(gem != null && flur != null && flurstz != null){
                                Gemarkung resolvedGemarkung = LagisBroker.getInstance().getGemarkungForKey(Integer.parseInt(gem));
                                //TODO if this case happens it leads to bug XXX
                                if(resolvedGemarkung==null){
                                    log.debug("Gemarkung konnte nicht entschlüsselt werden");
                                    resolvedGemarkung = new Gemarkung();
                                    resolvedGemarkung.setSchluessel(Integer.parseInt(gem));
                                }else{
                                    log.debug("Gemarkung konnte entschlüsselt werden");
                                }
                                //Gemarkung cplGemarkung = EJBroker.getInstance().completeGemarkung(gemarkung);
//                        if (cplGemarkung != null){
//                            log.debug("gemarkung bekannt");
//                            gemarkung = cplGemarkung;
//                        }
                                FlurstueckSchluessel key = new FlurstueckSchluessel();
                                key.setGemarkung(resolvedGemarkung);
                                key.setFlur(Integer.parseInt(flur));
                                key.setFlurstueckZaehler(Integer.parseInt(flurstz));
                                if(flurstn != null){
                                    key.setFlurstueckNenner(Integer.parseInt(flurstn));
                                } else {
                                    key.setFlurstueckNenner(0);
                                }
                                log.debug("Schlüssel konnte konstruiert werden");
                                LagisBroker.getInstance().loadFlurstueck(key);
                            } else {
                                log.debug("Mindestens ein Property == null Flurstueck kann nicht ausgewählt werden");
                            }
                        } else {
                            log.error("Properties == null Flurstueck oder Identifier im Konfigfile nicht richtig gesetzt --> kann nicht ausgewählt werden");
                        }
                    }catch(final Exception ex){
                        log.error("Fehler beim laden des ausgewählten Flurstücks",ex);
                    }
            }
        }
    }
}

public void refresh(Object refreshObject) {
}


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdALB;
    private javax.swing.JButton cmdAdd;
    private javax.swing.JButton cmdAddHandle;
    private javax.swing.JButton cmdAttachPolyToAlphadata;
    private javax.swing.JButton cmdBack;
    private javax.swing.JButton cmdForeground;
    private javax.swing.JButton cmdForward;
    private javax.swing.JButton cmdFullPoly;
    private javax.swing.JButton cmdFullPoly1;
    private javax.swing.JButton cmdJoinPoly;
    private javax.swing.JButton cmdMoveHandle;
    private javax.swing.JButton cmdMovePolygon;
    private javax.swing.JButton cmdNewPoint;
    private javax.swing.JButton cmdNewPolygon;
    private javax.swing.JButton cmdPan;
    private javax.swing.JButton cmdRaisePolygon;
    private javax.swing.JButton cmdRemoveHandle;
    private javax.swing.JButton cmdRemovePolygon;
    private javax.swing.JButton cmdSelect;
    private javax.swing.JButton cmdSnap;
    private javax.swing.JButton cmdSplitPoly;
    private javax.swing.JButton cmdWmsBackground;
    private javax.swing.JButton cmdZoom;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
    @Override
    public String getWidgetName() {
        return WIDGET_NAME;
    }
   
    
    
    private boolean checkIfIdentifiersAreSetProperly(){
        return (gemarkungIdentifier != null && flurIdentifier != null && flurstueckZaehlerIdentifier != null && flurstueckNennerIdentifier != null);
    }
    
    
    public void masterConfigure(Element parent) {
        log.debug("MasterConfigure: "+this.getClass());
        try{
        Element identifier = parent.getChild("flurstueckXMLIdentifier");
        gemarkungIdentifier = identifier.getChildText("gemarkungIdentifier");
        log.debug("GemarkungsIdentifier: "+ gemarkungIdentifier);
        flurIdentifier = identifier.getChildText("flurIdentifier");
        log.debug("FlurIdentifier: "+ flurIdentifier);
        flurstueckZaehlerIdentifier = identifier.getChildText("flurstueckZaehlerIdentifier");
        log.debug("FlurstueckZaehlerIdentifier: "+ flurstueckZaehlerIdentifier);
        flurstueckNennerIdentifier = identifier.getChildText("flurstueckNennerIdentifier");
        log.debug("FlurstueckNennerIdentifier: "+ flurstueckNennerIdentifier);
        log.debug("MasterConfigure: "+this.getClass()+" erfolgreich");
        }catch(Exception ex){
            log.error("Fehler beim masterConfigure von: "+this.getClass(),ex);
        }
    }
    
    //to
    public Element getConfiguration() throws NoWriteError {
        return null;
    }
    
    public void configure(Element parent) {
//        log.debug("Configure: "+this.getClass());
//        Element prefs=parent.getChild("cismapMappingPreferences");
//        try{
//            try {
//                isSnappingEnabled=prefs.getAttribute("snapping").getBooleanValue();
//                if(isSnappingEnabled){
//                    cmdSnap.setSelected(isSnappingEnabled);
//                    //TODO CHANGE CONFIG FILE ACTION
//                    //cismapPrefs.getGlobalPrefs().setSnappingEnabled(cmdSnap.isSelected());
//                    //cismapPrefs.getGlobalPrefs().setSnappingPreviewEnabled(cmdSnap.isSelected());
//                    mappingComponent.setSnappingEnabled(!mappingComponent.isReadOnly()&&cmdSnap.isSelected());
//                    mappingComponent.setVisualizeSnappingEnabled(!mappingComponent.isReadOnly()&&cmdSnap.isSelected());
//                    mappingComponent.setInGlueIdenticalPointsMode(cmdSnap.isSelected());
//                }
//                
//            } catch (Exception ex) {
//                log.warn("Fehler beim setzen des Snapping",ex);
//            }
//            
//        } catch(Exception ex){
//            log.error("Fehler beim konfigurieren des Kartenpanels: ",ex);
//        }
    }
    
    public void featuresRemoved(FeatureCollectionEvent fce) {
    }
    
    public void featuresChanged(FeatureCollectionEvent fce) {
        // try{
        log.debug("FeatureChanged");
//        Collection<Feature> features =  fce.getEventFeatures();
//        if(features != null){
//            for(Feature currentFeature:features){
//                if(currentFeature instanceof Verwaltungsbereich){
//                    ((Verwaltungsbereich)currentFeature).setFlaeche((int)currentFeature.getGeometry().getArea());
//                }
//            }
//        }
//        }catch(Exception ex){
//            log.warn("Fehler beim featureChanged");
//        }
    }
    
    public void featuresAdded(FeatureCollectionEvent fce) {
        
    }
    
    public void featureSelectionChanged(FeatureCollectionEvent fce) {
        log.debug("FeatureSelection Changed");
        Collection<Feature> features = fce.getEventFeatures();
        LagisBroker.getInstance().fireChangeEvent(features);
    }
    
    public void featureReconsiderationRequested(FeatureCollectionEvent fce) {
    }
    
    public void allFeaturesRemoved(FeatureCollectionEvent fce) {
    }
    
    
    
    //TODO USE
    @Override
    public Icon getWidgetIcon() {
        return null;
    }
    
    public void featureCollectionChanged() {
    }
}
