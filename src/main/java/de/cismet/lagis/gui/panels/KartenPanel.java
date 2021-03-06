/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * KartenPanel.java
 *
 * Created on 16. März 2007, 12:04
 */
package de.cismet.lagis.gui.panels;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

import edu.umd.cs.piccolox.event.PNotification;
import edu.umd.cs.piccolox.event.PNotificationCenter;
import edu.umd.cs.piccolox.event.PSelectionEventHandler;

import org.jdom.Element;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;

import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;
import de.cismet.cids.custom.beans.lagis.GemarkungCustomBean;
import de.cismet.cids.custom.beans.lagis.RebeCustomBean;
import de.cismet.cids.custom.beans.lagis.VerwaltungsbereichCustomBean;
import de.cismet.cids.custom.commons.searchgeometrylistener.BaulastblattNodesSearchCreateSearchGeometryListener;
import de.cismet.cids.custom.commons.searchgeometrylistener.FlurstueckNodesSearchCreateSearchGeometryListener;
import de.cismet.cids.custom.commons.searchgeometrylistener.RissNodesSearchCreateSearchGeometryListener;

import de.cismet.cismap.cidslayer.CidsLayerFeature;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollectionEvent;
import de.cismet.cismap.commons.features.FeatureCollectionListener;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.FeatureGroupMember;
import de.cismet.cismap.commons.gui.FeatureLayerTransparencyButton;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.StyledFeatureGroupWrapper;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.AttachFeatureListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.CreateGeometryListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.DeleteFeatureListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.FeatureMoveListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.JoinPolygonsListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.SelectionListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.SimpleMoveListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.SplitPolygonListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.actions.CustomAction;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.interaction.memento.MementoInterface;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.gui.main.LagisApp;

import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.NoPermissionsWidget;

import de.cismet.lagis.utillity.GeometrySlotInformation;

import de.cismet.lagis.widget.AbstractWidget;
import de.cismet.lagis.widget.RessortFactory;

import de.cismet.lagisEE.entity.core.CustomSelectionStyledFeatureGroupWrapper;

import de.cismet.tools.configuration.Configurable;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.historybutton.JHistoryButton;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class KartenPanel extends AbstractWidget implements FlurstueckChangeListener,
    FeatureCollectionListener,
    Configurable,
    NoPermissionsWidget,
    Observer {

    //~ Static fields/initializers ---------------------------------------------

    private static final String WIDGET_NAME = "Karten Panel";
    private static final String PROVIDER_BAUM = "Baum";                   // NOI18N
    private static final String PROVIDER_MIPA = "MiPa";                   // NOI18N
    private static final String PERM_KEY_BAUM = "Baumdatei";              // NOI18N
    private static final String PERM_KEY_MIPA = "Vermietung/Verpachtung"; // NOI18N

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KartenPanel.class);

    //~ Instance fields --------------------------------------------------------

    private final MappingComponent mappingComponent;
    private boolean isEditable = true;
    private final JButton cmdCopyFlaeche = new JButton();
    private final JButton cmdPasteFlaeche = new JButton();
    private Object clipboard = null;
    private final ArrayList<Feature> copiedFeatures = new ArrayList<>();
    private final Map<String, FeatureGroupActionListener> featureGroupButtonListenerMap;
    private final JLabel lblInfo;
    private Object lastOverFeature;

    // Variables declaration - do not modify
    // NOI18N
    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JButton cmdRedo;
    private javax.swing.JButton cmdRemoveHandle;
    private javax.swing.JButton cmdRemovePolygon;
    private javax.swing.JToggleButton cmdSearchAlkisLandparcel;
    private javax.swing.JToggleButton cmdSearchBaulasten;
    private javax.swing.JToggleButton cmdSearchVermessungRiss;
    private javax.swing.JButton cmdSelect;
    private javax.swing.JButton cmdSnap;
    private javax.swing.JButton cmdSplitPoly;
    private javax.swing.JButton cmdUndo;
    private javax.swing.JButton cmdWmsBackground;
    private javax.swing.JButton cmdZoom;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form KartenPanel.
     */
    public KartenPanel() {
        setIsCoreWidget(true);
        initComponents();
        mappingComponent = CismapBroker.getInstance().getMappingComponent();
        mappingComponent.getFeatureCollection().addFeatureCollectionListener(this);
        mappingComponent.setBackgroundEnabled(true);
        PNotificationCenter.defaultCenter()
                .addListener(
                    this,
                    "attachFeatureRequested",
                    AttachFeatureListener.ATTACH_FEATURE_NOTIFICATION,
                    mappingComponent.getInputListener(MappingComponent.ATTACH_POLYGON_TO_ALPHADATA));
        PNotificationCenter.defaultCenter()
                .addListener(
                    this,
                    "selectionChanged",
                    SplitPolygonListener.SELECTION_CHANGED,
                    mappingComponent.getInputListener(MappingComponent.SPLIT_POLYGON));
        PNotificationCenter.defaultCenter()
                .addListener(
                    this,
                    "splitPolygon",
                    SplitPolygonListener.SPLIT_FINISHED,
                    mappingComponent.getInputListener(MappingComponent.SPLIT_POLYGON));
        PNotificationCenter.defaultCenter()
                .addListener(
                    this,
                    "featureDeleteRequested",
                    DeleteFeatureListener.FEATURE_DELETE_REQUEST_NOTIFICATION,
                    mappingComponent.getInputListener(MappingComponent.REMOVE_POLYGON));
        PNotificationCenter.defaultCenter()
                .addListener(
                    this,
                    "joinPolygons",
                    JoinPolygonsListener.FEATURE_JOIN_REQUEST_NOTIFICATION,
                    mappingComponent.getInputListener(MappingComponent.JOIN_POLYGONS));
        PNotificationCenter.defaultCenter()
                .addListener(
                    this,
                    "selectionChanged",
                    PSelectionEventHandler.SELECTION_CHANGED_NOTIFICATION,
                    mappingComponent.getInputListener(MappingComponent.SELECT));
        PNotificationCenter.defaultCenter()
                .addListener(
                    this,
                    "selectionChanged",
                    FeatureMoveListener.SELECTION_CHANGED_NOTIFICATION,
                    mappingComponent.getInputListener(MappingComponent.MOVE_POLYGON));
        PNotificationCenter.defaultCenter()
                .addListener(
                    this,
                    "coordinatesChanged",
                    SimpleMoveListener.COORDINATES_CHANGED,
                    mappingComponent.getInputListener(MappingComponent.MOTION));
        ((JHistoryButton)cmdForward).setDirection(JHistoryButton.DIRECTION_FORWARD);
        ((JHistoryButton)cmdBack).setDirection(JHistoryButton.DIRECTION_BACKWARD);
        ((JHistoryButton)cmdForward).setHistoryModel(mappingComponent);
        ((JHistoryButton)cmdBack).setHistoryModel(mappingComponent);
        mappingComponent.setBackground(getBackground());
        mappingComponent.setBackgroundEnabled(true);
        this.add(BorderLayout.CENTER, mappingComponent);
        // TODO make enumartion for InteractionModes

        this.configureCopyPaste();

        this.featureGroupButtonListenerMap = new HashMap<>();

        this.addFeatureGroupButton(
            FlurstueckChooser.FEATURE_GRP,
            "Flurstück",
            "/de/cismet/lagis/ressource/icons/layer_flurstueck.png",
            "/de/cismet/lagis/ressource/icons/layer_flurstueck_sw.png");

        this.addFeatureGroupButton(
            VerwaltungsPanel.PROVIDER_NAME,
            "Verwaltungsbereiche",
            "/de/cismet/lagis/ressource/icons/layer_vb.png",
            "/de/cismet/lagis/ressource/icons/layer_vb_sw.png");

        this.addFeatureGroupButton(
            ReBePanel.PROVIDER_NAME,
            "ReBe",
            "/de/cismet/lagis/ressource/icons/layer_rebe.png",
            "/de/cismet/lagis/ressource/icons/layer_rebe_sw.png");

        final RessortFactory ressortFactory = RessortFactory.getInstance();
        final HashMap<String, AbstractWidget> ressorts = ressortFactory.getRessorts();

        if (ressorts.containsKey(PERM_KEY_BAUM)) {
            this.addFeatureGroupButton(
                PROVIDER_BAUM,
                "Baumdatei",
                "/de/cismet/lagis/ressource/icons/layer_baum.png",
                "/de/cismet/lagis/ressource/icons/layer_baum_sw.png");
        }

        if (ressorts.containsKey(PERM_KEY_MIPA)) {
            this.addFeatureGroupButton(
                PROVIDER_MIPA,
                "Vermietung/Verpachtung",
                "/de/cismet/lagis/ressource/icons/layer_mipa.png",
                "/de/cismet/lagis/ressource/icons/layer_mipa_sw.png");
        }

        this.addSeparator();

        this.lblInfo = new JLabel();

        // nasty workaround as the GuiBuilder can not be used at the moment
        this.remove(this.jPanel1);
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(this.lblInfo, BorderLayout.WEST);
        panel.add(this.jPanel1, BorderLayout.EAST);
        super.add(panel, java.awt.BorderLayout.SOUTH);
        ((Observable)mappingComponent.getMemUndo()).addObserver(this);
        ((Observable)mappingComponent.getMemRedo()).addObserver(this);

        mappingComponent.getFeatureLayer().setTransparency(150 / 255f);

        ((SimpleMoveListener)mappingComponent.getInputListener(
                MappingComponent.MOTION)).setDeepSeekEnabled(true);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void addSeparator() {
        final JSeparator sep = new JSeparator(javax.swing.SwingConstants.VERTICAL);
        sep.setMaximumSize(new java.awt.Dimension(2, 32767));
        sep.setMinimumSize(new java.awt.Dimension(2, 10));
        sep.setPreferredSize(new java.awt.Dimension(2, 10));

        this.jToolBar1.add(sep);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  featureGroup  DOCUMENT ME!
     * @param  tooltipText   DOCUMENT ME!
     * @param  icon          DOCUMENT ME!
     * @param  invisIcon     DOCUMENT ME!
     */
    private void addFeatureGroupButton(final String featureGroup,
            final String tooltipText,
            final String icon,
            final String invisIcon) {
        final JToggleButton button = new JToggleButton();
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);

        button.setSelectedIcon(new ImageIcon(getClass().getResource(icon)));
        button.setIcon(new ImageIcon(getClass().getResource(invisIcon)));

        final FeatureGroupActionListener listener = new FeatureGroupActionListener(
                button,
                mappingComponent,
                featureGroup,
                tooltipText);

        button.addActionListener(listener);
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        button.setIconTextGap(8);
        button.setMargin(new java.awt.Insets(10, 14, 10, 14));

        this.jToolBar1.add(button);
        this.featureGroupButtonListenerMap.put(featureGroup, listener);
    }

    /**
     * DOCUMENT ME!
     */
    private void configureCopyPaste() {
        cmdCopyFlaeche.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/copyFl16.png"))); // NOI18N
        cmdCopyFlaeche.setToolTipText("Fläche kopieren");
        cmdCopyFlaeche.setBorderPainted(false);
        cmdCopyFlaeche.setEnabled(false);
        cmdCopyFlaeche.setFocusPainted(false);
        cmdCopyFlaeche.setFocusable(false);
        cmdCopyFlaeche.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdCopyFlaeche.setIconTextGap(8);
        cmdCopyFlaeche.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdCopyFlaeche.setPreferredSize(new java.awt.Dimension(23, 23));
        cmdCopyFlaeche.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdCopyFlaeche.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdCopyFlaecheActionPerformed(evt);
                }
            });

        cmdPasteFlaeche.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/pasteFl16.png"))); // NOI18N
        cmdPasteFlaeche.setToolTipText("Fläche einfügen");
        cmdPasteFlaeche.setBorderPainted(false);
        cmdPasteFlaeche.setEnabled(false);
        cmdPasteFlaeche.setFocusPainted(false);
        cmdPasteFlaeche.setFocusable(false);
        cmdPasteFlaeche.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdPasteFlaeche.setIconTextGap(8);
        cmdPasteFlaeche.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdPasteFlaeche.setPreferredSize(new java.awt.Dimension(23, 23));
        cmdPasteFlaeche.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdPasteFlaeche.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdPasteFlaecheActionPerformed(evt);
                }
            });

        jToolBar1.add(cmdCopyFlaeche, 21);
        jToolBar1.add(cmdPasteFlaeche, 22);

        cmdCopyFlaeche.setVisible(false);
        cmdPasteFlaeche.setVisible(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdCopyFlaecheActionPerformed(final java.awt.event.ActionEvent evt) {
        final int answer = JOptionPane.YES_OPTION;
        copiedFeatures.clear();
        clipboard = LagisBroker.getInstance().getMappingComponent().getFeatureCollection().getSelectedFeatures();
        if (answer == JOptionPane.YES_OPTION) {
            if (clipboard != null) {
                final Iterator it = ((Collection)clipboard).iterator();

                while (it.hasNext()) {
                    final Feature clipboardFlaeche = (Feature)it.next();
                    final PureNewFeature newFeature = new PureNewFeature((Geometry)clipboardFlaeche.getGeometry()
                                    .clone());
                    newFeature.setCanBeSelected(true);
                    newFeature.setEditable(true);
                    copiedFeatures.add(newFeature);
                }
            }

            if (copiedFeatures.size() > 0) {
                this.cmdPasteFlaeche.setEnabled(true);
            } else {
                this.cmdPasteFlaeche.setEnabled(false);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdPasteFlaecheActionPerformed(final java.awt.event.ActionEvent evt) {
        if (copiedFeatures.size() > 0) {
            LagisBroker.getInstance().getMappingComponent().getFeatureCollection().unselectAll();
            final Iterator it = copiedFeatures.iterator();
            while (it.hasNext()) {
                final Feature clipboardFlaeche = (Feature)it.next();
                final PureNewFeature newFeature = new PureNewFeature((Geometry)clipboardFlaeche.getGeometry().clone());
                newFeature.setCanBeSelected(true);
                newFeature.setEditable(true);
                LagisBroker.getInstance().getMappingComponent().getFeatureCollection().addFeature(newFeature);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setInteractionMode() {
        final String currentInteractionMode = mappingComponent.getInteractionMode();
        if (currentInteractionMode != null) {
            if (currentInteractionMode.equals(MappingComponent.SELECT)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("InteractionMode set to SELECT");
                }
                cmdSelectActionPerformed(null);
            } else if (currentInteractionMode.equals(MappingComponent.PAN)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("InteractionMode set to PAN");
                }
                cmdPanActionPerformed(null);
            } else if (currentInteractionMode.equals(MappingComponent.NEW_POLYGON)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("InteractionMode set to NEW_POLYGON");
                }
                cmdNewPolygonActionPerformed(null);
            } else if (currentInteractionMode.equals(MappingComponent.ZOOM)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("InteractionMode set to ZOOM");
                }
                cmdZoomActionPerformed(null);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Unknown Interactionmode: " + currentInteractionMode);
                }
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("InteractionMode == null");
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
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
        jSeparator10 = new javax.swing.JSeparator();
        cmdSearchAlkisLandparcel = new javax.swing.JToggleButton();
        cmdSearchBaulasten = new javax.swing.JToggleButton();
        cmdSearchVermessungRiss = new javax.swing.JToggleButton();
        jSeparator9 = new javax.swing.JSeparator();
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
        cmdUndo = new javax.swing.JButton();
        cmdRedo = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JSeparator();
        jButton2 = new FeatureLayerTransparencyButton();
        jPanel1 = new javax.swing.JPanel();
        cmdAdd = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jToolBar1.setMaximumSize(new java.awt.Dimension(32769, 32769));
        jToolBar1.setMinimumSize(new java.awt.Dimension(300, 25));
        jToolBar1.setPreferredSize(new java.awt.Dimension(300, 28));

        cmdFullPoly.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/fullPoly.png"))); // NOI18N
        cmdFullPoly.setToolTipText("Zeige alle Flächen");
        cmdFullPoly.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdFullPoly.setIconTextGap(8);
        cmdFullPoly.setMargin(new java.awt.Insets(10, 14, 10, 14));
        cmdFullPoly.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdFullPolyActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdFullPoly);

        cmdFullPoly1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/fullSelPoly.png"))); // NOI18N
        cmdFullPoly1.setToolTipText("Zoom zur ausgewählten Fläche");
        cmdFullPoly1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdFullPoly1.setIconTextGap(8);
        cmdFullPoly1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdFullPoly1ActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdFullPoly1);

        cmdBack.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/back2.png"))); // NOI18N
        cmdBack.setToolTipText("Zurück");
        cmdBack.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdBack.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdBackActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdBack);

        cmdForward.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/fwd.png"))); // NOI18N
        cmdForward.setToolTipText("Vor");
        cmdForward.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdForward.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdForwardActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdForward);

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator4.setMaximumSize(new java.awt.Dimension(2, 32767));
        jSeparator4.setMinimumSize(new java.awt.Dimension(2, 10));
        jSeparator4.setPreferredSize(new java.awt.Dimension(2, 10));
        jToolBar1.add(jSeparator4);

        cmdWmsBackground.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/map.png")));    // NOI18N
        cmdWmsBackground.setToolTipText("Hintergrund an/aus");
        cmdWmsBackground.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdWmsBackground.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/map_on.png"))); // NOI18N
        cmdWmsBackground.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdWmsBackgroundActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdWmsBackground);

        cmdForeground.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/foreground.png")));    // NOI18N
        cmdForeground.setToolTipText("Vordergrund an/aus");
        cmdForeground.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdForeground.setFocusable(false);
        cmdForeground.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdForeground.setSelected(true);
        cmdForeground.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/foreground_on.png"))); // NOI18N
        cmdForeground.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdForeground.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdForegroundActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdForeground);

        cmdSnap.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/snap.png")));          // NOI18N
        cmdSnap.setToolTipText("Snapping an/aus");
        cmdSnap.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdSnap.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/snap_selected.png"))); // NOI18N
        cmdSnap.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSnapActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdSnap);

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator5.setMaximumSize(new java.awt.Dimension(2, 32767));
        jSeparator5.setMinimumSize(new java.awt.Dimension(2, 10));
        jSeparator5.setPreferredSize(new java.awt.Dimension(2, 10));
        jToolBar1.add(jSeparator5);

        cmdZoom.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/zoom.png")));          // NOI18N
        cmdZoom.setToolTipText("Zoomen");
        cmdZoom.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdZoom.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/zoom_selected.png"))); // NOI18N
        cmdZoom.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdZoomActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdZoom);

        cmdPan.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/move2.png")));          // NOI18N
        cmdPan.setToolTipText("Verschieben");
        cmdPan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdPan.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/move2_selected.png"))); // NOI18N
        cmdPan.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdPanActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdPan);

        cmdSelect.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/select.png")));          // NOI18N
        cmdSelect.setToolTipText("Auswählen");
        cmdSelect.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdSelect.setSelected(true);
        cmdSelect.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/select_selected.png"))); // NOI18N
        cmdSelect.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSelectActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdSelect);

        jSeparator10.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator10.setMaximumSize(new java.awt.Dimension(2, 32767));
        jSeparator10.setMinimumSize(new java.awt.Dimension(2, 10));
        jSeparator10.setPreferredSize(new java.awt.Dimension(2, 10));
        jToolBar1.add(jSeparator10);

        cmdSearchAlkisLandparcel.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/alk.png")));          // NOI18N
        cmdSearchAlkisLandparcel.setToolTipText("Flurstücke suchen");
        cmdSearchAlkisLandparcel.setBorderPainted(false);
        cmdSearchAlkisLandparcel.setContentAreaFilled(false);
        cmdSearchAlkisLandparcel.setFocusPainted(false);
        cmdSearchAlkisLandparcel.setFocusable(false);
        cmdSearchAlkisLandparcel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdSearchAlkisLandparcel.setMaximumSize(new java.awt.Dimension(22, 18));
        cmdSearchAlkisLandparcel.setMinimumSize(new java.awt.Dimension(22, 18));
        cmdSearchAlkisLandparcel.setPreferredSize(new java.awt.Dimension(22, 18));
        cmdSearchAlkisLandparcel.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/alk_selected.png"))); // NOI18N
        cmdSearchAlkisLandparcel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdSearchAlkisLandparcel.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSearchAlkisLandparcelActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdSearchAlkisLandparcel);

        cmdSearchBaulasten.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/Baulast.png")));          // NOI18N
        cmdSearchBaulasten.setToolTipText("Baulasten suchen");
        cmdSearchBaulasten.setBorderPainted(false);
        cmdSearchBaulasten.setContentAreaFilled(false);
        cmdSearchBaulasten.setFocusPainted(false);
        cmdSearchBaulasten.setFocusable(false);
        cmdSearchBaulasten.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdSearchBaulasten.setMaximumSize(new java.awt.Dimension(22, 18));
        cmdSearchBaulasten.setMinimumSize(new java.awt.Dimension(22, 18));
        cmdSearchBaulasten.setPreferredSize(new java.awt.Dimension(22, 18));
        cmdSearchBaulasten.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/Baulast_selected.png"))); // NOI18N
        cmdSearchBaulasten.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdSearchBaulasten.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSearchBaulastenActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdSearchBaulasten);
        cmdSearchBaulasten.setVisible(LagisBroker.getInstance().checkPermissionBaulasten());

        cmdSearchVermessungRiss.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/vermessungsriss.png")));          // NOI18N
        cmdSearchVermessungRiss.setToolTipText("Vermessungsrisse suchen");
        cmdSearchVermessungRiss.setBorderPainted(false);
        cmdSearchVermessungRiss.setContentAreaFilled(false);
        cmdSearchVermessungRiss.setFocusPainted(false);
        cmdSearchVermessungRiss.setFocusable(false);
        cmdSearchVermessungRiss.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdSearchVermessungRiss.setMaximumSize(new java.awt.Dimension(22, 18));
        cmdSearchVermessungRiss.setMinimumSize(new java.awt.Dimension(22, 18));
        cmdSearchVermessungRiss.setPreferredSize(new java.awt.Dimension(22, 18));
        cmdSearchVermessungRiss.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/vermessungsriss_selected.png"))); // NOI18N
        cmdSearchVermessungRiss.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdSearchVermessungRiss.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSearchVermessungRissActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdSearchVermessungRiss);
        cmdSearchVermessungRiss.setVisible(LagisBroker.getInstance().checkPermissionRisse());

        jSeparator9.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator9.setMaximumSize(new java.awt.Dimension(2, 32767));
        jSeparator9.setMinimumSize(new java.awt.Dimension(2, 10));
        jSeparator9.setPreferredSize(new java.awt.Dimension(2, 10));
        jToolBar1.add(jSeparator9);

        cmdMovePolygon.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/movePoly.png")));          // NOI18N
        cmdMovePolygon.setToolTipText("Polygon verschieben");
        cmdMovePolygon.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdMovePolygon.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/movePoly_selected.png"))); // NOI18N
        cmdMovePolygon.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdMovePolygonActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdMovePolygon);

        cmdNewPolygon.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/newPoly.png")));          // NOI18N
        cmdNewPolygon.setToolTipText("neues Polygon");
        cmdNewPolygon.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdNewPolygon.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/newPoly_selected.png"))); // NOI18N
        cmdNewPolygon.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdNewPolygonActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdNewPolygon);

        cmdNewPoint.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/newPoint.png")));          // NOI18N
        cmdNewPoint.setToolTipText("neuer Punkt");
        cmdNewPoint.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdNewPoint.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/newPoint_selected.png"))); // NOI18N
        cmdNewPoint.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdNewPointActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdNewPoint);

        cmdRaisePolygon.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/raisePoly.png")));          // NOI18N
        cmdRaisePolygon.setToolTipText("Polygon hochholen");
        cmdRaisePolygon.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdRaisePolygon.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/raisePoly_selected.png"))); // NOI18N
        cmdRaisePolygon.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdRaisePolygonActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdRaisePolygon);

        cmdRemovePolygon.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/removePoly.png")));          // NOI18N
        cmdRemovePolygon.setToolTipText("Polygon entfernen");
        cmdRemovePolygon.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdRemovePolygon.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/removePoly_selected.png"))); // NOI18N
        cmdRemovePolygon.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdRemovePolygonActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdRemovePolygon);

        cmdAttachPolyToAlphadata.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/polygonAttachment.png")));          // NOI18N
        cmdAttachPolyToAlphadata.setToolTipText("Polygon zuordnen");
        cmdAttachPolyToAlphadata.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdAttachPolyToAlphadata.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/polygonAttachment_selected.png"))); // NOI18N
        cmdAttachPolyToAlphadata.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdAttachPolyToAlphadataActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdAttachPolyToAlphadata);

        cmdJoinPoly.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/joinPoly.png")));          // NOI18N
        cmdJoinPoly.setToolTipText("Polygone zusammenfassen");
        cmdJoinPoly.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdJoinPoly.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/joinPoly_selected.png"))); // NOI18N
        cmdJoinPoly.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdJoinPolyActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdJoinPoly);

        cmdSplitPoly.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/splitPoly.png")));          // NOI18N
        cmdSplitPoly.setToolTipText("Polygon splitten");
        cmdSplitPoly.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdSplitPoly.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/splitPoly_selected.png"))); // NOI18N
        cmdSplitPoly.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdSplitPolyActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdSplitPoly);

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator6.setMaximumSize(new java.awt.Dimension(2, 32767));
        jSeparator6.setMinimumSize(new java.awt.Dimension(2, 10));
        jSeparator6.setPreferredSize(new java.awt.Dimension(2, 10));
        jToolBar1.add(jSeparator6);

        cmdMoveHandle.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/moveHandle.png")));          // NOI18N
        cmdMoveHandle.setToolTipText("Handle verschieben");
        cmdMoveHandle.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdMoveHandle.setSelected(true);
        cmdMoveHandle.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/moveHandle_selected.png"))); // NOI18N
        cmdMoveHandle.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdMoveHandleActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdMoveHandle);

        cmdAddHandle.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/addHandle.png")));          // NOI18N
        cmdAddHandle.setToolTipText("Handle hinzufügen");
        cmdAddHandle.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdAddHandle.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/addHandle_selected.png"))); // NOI18N
        cmdAddHandle.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdAddHandleActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdAddHandle);

        cmdRemoveHandle.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/removeHandle.png")));          // NOI18N
        cmdRemoveHandle.setToolTipText("Handle entfernen");
        cmdRemoveHandle.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        cmdRemoveHandle.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/removeHandle_selected.png"))); // NOI18N
        cmdRemoveHandle.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdRemoveHandleActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdRemoveHandle);

        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator7.setMaximumSize(new java.awt.Dimension(2, 32767));
        jSeparator7.setMinimumSize(new java.awt.Dimension(2, 10));
        jSeparator7.setPreferredSize(new java.awt.Dimension(2, 10));
        jToolBar1.add(jSeparator7);

        cmdUndo.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/undo.png"))); // NOI18N
        cmdUndo.setToolTipText("Undo");
        cmdUndo.setBorderPainted(false);
        cmdUndo.setContentAreaFilled(false);
        cmdUndo.setEnabled(false);
        cmdUndo.setFocusPainted(false);
        cmdUndo.setFocusable(false);
        cmdUndo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdUndo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdUndo.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdUndoActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdUndo);

        cmdRedo.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/redo.png"))); // NOI18N
        cmdRedo.setBorderPainted(false);
        cmdRedo.setContentAreaFilled(false);
        cmdRedo.setEnabled(false);
        cmdRedo.setFocusPainted(false);
        cmdRedo.setFocusable(false);
        cmdRedo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdRedo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdRedo.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdRedoActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdRedo);

        jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator8.setMaximumSize(new java.awt.Dimension(2, 32767));
        jSeparator8.setMinimumSize(new java.awt.Dimension(2, 10));
        jSeparator8.setPreferredSize(new java.awt.Dimension(2, 10));
        jToolBar1.add(jSeparator8);

        jButton2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/flurstueck_transp.png"))); // NOI18N
        jButton2.setToolTipText("Featurelayer-Transparenz einstellen");
        jButton2.setBorderPainted(false);
        jButton2.setContentAreaFilled(false);
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setMaximumSize(new java.awt.Dimension(22, 18));
        jButton2.setMinimumSize(new java.awt.Dimension(22, 18));
        jButton2.setPreferredSize(new java.awt.Dimension(22, 18));
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton2);

        add(jToolBar1, java.awt.BorderLayout.NORTH);

        jPanel1.setMinimumSize(new java.awt.Dimension(50, 100));
        jPanel1.setPreferredSize(new java.awt.Dimension(50, 30));

        cmdAdd.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/layersman.png"))); // NOI18N
        cmdAdd.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        cmdAdd.setBorderPainted(false);
        cmdAdd.setFocusPainted(false);
        cmdAdd.setMinimumSize(new java.awt.Dimension(25, 25));
        cmdAdd.setPreferredSize(new java.awt.Dimension(25, 25));
        cmdAdd.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/buttons/layersman.png"))); // NOI18N
        cmdAdd.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdAddActionPerformed(evt);
                }
            });

        final org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                jPanel1Layout.createSequentialGroup().addContainerGap(872, Short.MAX_VALUE).add(
                    cmdAdd,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel1Layout.createSequentialGroup().add(
                    cmdAdd,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap(75, Short.MAX_VALUE)));

        add(jPanel1, java.awt.BorderLayout.SOUTH);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdMovePolygonActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdMovePolygonActionPerformed
        removeMainGroupSelection();
        cmdMovePolygon.setSelected(true);
        mappingComponent.setInteractionMode(MappingComponent.MOVE_POLYGON);
    }                                                                                  //GEN-LAST:event_cmdMovePolygonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAddActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdAddActionPerformed
        mappingComponent.showInternalLayerWidget(!mappingComponent.isInternalLayerWidgetVisible(), 500);
    }                                                                          //GEN-LAST:event_cmdAddActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRemoveHandleActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdRemoveHandleActionPerformed
        removeHandleGroupSelection();
        cmdRemoveHandle.setSelected(true);
        mappingComponent.setHandleInteractionMode(MappingComponent.REMOVE_HANDLE);
    }                                                                                   //GEN-LAST:event_cmdRemoveHandleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAddHandleActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdAddHandleActionPerformed
        removeHandleGroupSelection();
        cmdAddHandle.setSelected(true);
        mappingComponent.setHandleInteractionMode(MappingComponent.ADD_HANDLE);
    }                                                                                //GEN-LAST:event_cmdAddHandleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdMoveHandleActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdMoveHandleActionPerformed
        removeHandleGroupSelection();
        cmdMoveHandle.setSelected(true);
        mappingComponent.setHandleInteractionMode(MappingComponent.MOVE_HANDLE);
    }                                                                                 //GEN-LAST:event_cmdMoveHandleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSplitPolyActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSplitPolyActionPerformed
        removeMainGroupSelection();
        cmdSplitPoly.setSelected(true);
        mappingComponent.setInteractionMode(MappingComponent.SPLIT_POLYGON);
    }                                                                                //GEN-LAST:event_cmdSplitPolyActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdJoinPolyActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdJoinPolyActionPerformed
        removeMainGroupSelection();
        cmdJoinPoly.setSelected(true);
        mappingComponent.setInteractionMode(MappingComponent.JOIN_POLYGONS);
    }                                                                               //GEN-LAST:event_cmdJoinPolyActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAttachPolyToAlphadataActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdAttachPolyToAlphadataActionPerformed
        removeMainGroupSelection();
        cmdAttachPolyToAlphadata.setSelected(true);
        mappingComponent.setInteractionMode(MappingComponent.ATTACH_POLYGON_TO_ALPHADATA);
    }                                                                                            //GEN-LAST:event_cmdAttachPolyToAlphadataActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRemovePolygonActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdRemovePolygonActionPerformed
        removeMainGroupSelection();
        cmdRemovePolygon.setSelected(true);
        mappingComponent.setInteractionMode(MappingComponent.REMOVE_POLYGON);
    }                                                                                    //GEN-LAST:event_cmdRemovePolygonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRaisePolygonActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdRaisePolygonActionPerformed
        removeMainGroupSelection();
        cmdRaisePolygon.setSelected(true);
        mappingComponent.setInteractionMode(MappingComponent.RAISE_POLYGON);
    }                                                                                   //GEN-LAST:event_cmdRaisePolygonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdNewPointActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdNewPointActionPerformed
        removeMainGroupSelection();
        // TODO READING THE SNAPENAB --> FAILURE
        // boolean snapEnab=cismapPrefs.getGlobalPrefs().isSnappingEnabled();
        // boolean snapVizEnab=cismapPrefs.getGlobalPrefs().isSnappingPreviewEnabled();
        final boolean snapEnab = true;
        final boolean snapVizEnab = true;
        mappingComponent.setSnappingEnabled(snapEnab);
        cmdSnap.setSelected(snapEnab);
        mappingComponent.setVisualizeSnappingEnabled(snapVizEnab);
        cmdNewPoint.setSelected(true);
        mappingComponent.setInteractionMode(MappingComponent.NEW_POLYGON);
        ((CreateGeometryListener)mappingComponent.getInputListener(MappingComponent.NEW_POLYGON)).setMode(
            CreateGeometryListener.POINT);
    } //GEN-LAST:event_cmdNewPointActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdNewPolygonActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdNewPolygonActionPerformed
        removeMainGroupSelection();
        // TODO READING THE SNAPENAB --> FAILURE
        // boolean snapEnab=cismapPrefs.getGlobalPrefs().isSnappingEnabled();
        // boolean snapVizEnab=cismapPrefs.getGlobalPrefs().isSnappingPreviewEnabled();
        final boolean snapEnab = true;
        final boolean snapVizEnab = true;
        mappingComponent.setSnappingEnabled(snapEnab);
        cmdSnap.setSelected(snapEnab);
        mappingComponent.setVisualizeSnappingEnabled(snapVizEnab);
        cmdNewPolygon.setSelected(true);
        mappingComponent.setInteractionMode(MappingComponent.NEW_POLYGON);
        ((CreateGeometryListener)mappingComponent.getInputListener(MappingComponent.NEW_POLYGON)).setMode(
            CreateGeometryListener.POLYGON);
    } //GEN-LAST:event_cmdNewPolygonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSelectActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSelectActionPerformed
        removeMainGroupSelection();
        cmdSelect.setSelected(true);
        mappingComponent.setInteractionMode(MappingComponent.SELECT);
        cmdMoveHandleActionPerformed(null);
    }                                                                             //GEN-LAST:event_cmdSelectActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void removeHandleGroupSelection() {
        cmdRemoveHandle.setSelected(false);
        cmdAddHandle.setSelected(false);
        cmdMoveHandle.setSelected(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdPanActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdPanActionPerformed
        removeMainGroupSelection();
        cmdPan.setSelected(true);
        mappingComponent.setInteractionMode(MappingComponent.PAN);
    }                                                                          //GEN-LAST:event_cmdPanActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void removeMainGroupSelection() {
        cmdSelect.setSelected(false);
        cmdPan.setSelected(false);
        cmdZoom.setSelected(false);
        cmdMovePolygon.setSelected(false);
        cmdNewPolygon.setSelected(false);
        cmdNewPoint.setSelected(false);
        cmdRemovePolygon.setSelected(false);
        cmdAttachPolyToAlphadata.setSelected(false);
        cmdSplitPoly.setSelected(false);
        cmdJoinPoly.setSelected(false);
        cmdRaisePolygon.setSelected(false);
        cmdSearchAlkisLandparcel.setSelected(false);
        cmdSearchBaulasten.setSelected(false);
        cmdSearchVermessungRiss.setSelected(false);
    }

    @Override
    public void flurstueckChanged(final FlurstueckCustomBean newFlurstueck) {
        LagisBroker.getInstance().flurstueckChangeFinished(this);
    }

    @Override
    public synchronized void setComponentEditable(final boolean isEditable) {
        if (this.isEditable == isEditable) {
            return;
        }
        this.isEditable = isEditable;
        if (LOG.isDebugEnabled()) {
            LOG.debug("MapPanel --> setComponentEditable");
        }
        if (EventQueue.isDispatchThread()) {
            mappingComponent.setReadOnly(!isEditable);
            // TODO only change if the actualMode is not allowed
            if (!isEditable) {
                // mappingComponent.setInteractionMode(mappingComponent.PAN);
                // TODO is it really the best default mode ?
                // TODO look how to easily create events (or common)
                removeMainGroupSelection();
                cmdSelect.setSelected(true);
                mappingComponent.setInteractionMode(MappingComponent.SELECT);
                cmdMoveHandleActionPerformed(null);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl Features in FeatureCollection:"
                            + mappingComponent.getFeatureCollection().getFeatureCount());
            }
            // ((DefaultFeatureCollection)mappingComponent.getFeatureCollection()).setAllFeaturesEditable(isEditable);
            // TODO TEST IT!!!!
            LagisBroker.getInstance().getMappingComponent().setReadOnly(!isEditable);
            cmdMovePolygon.setVisible(isEditable);
            // this.cmdNewPolygon.setVisible(b);
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

            cmdCopyFlaeche.setVisible(isEditable);
            cmdPasteFlaeche.setVisible(isEditable);
            cmdUndo.setVisible(isEditable);
            cmdRedo.setVisible(isEditable);
        } else {
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        mappingComponent.setReadOnly(!isEditable);
                        // TODO only change if the actualMode is not allowed
                        if (!isEditable) {
                            // mappingComponent.setInteractionMode(mappingComponent.PAN);
                            // TODO is it really the best default mode ?
                            // TODO look how to easily create events (or common)
                            removeMainGroupSelection();
                            cmdSelect.setSelected(true);
                            mappingComponent.setInteractionMode(MappingComponent.SELECT);
                            cmdMoveHandleActionPerformed(null);
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                "Anzahl Features in FeatureCollection:"
                                        + mappingComponent.getFeatureCollection().getFeatureCount());
                        }
                        // ((DefaultFeatureCollection)mappingComponent.getFeatureCollection()).setAllFeaturesEditable(isEditable);
                        // TODO TEST IT!!!!
                        LagisBroker.getInstance().getMappingComponent().setReadOnly(!isEditable);
                        cmdMovePolygon.setVisible(isEditable);
                        // this.cmdNewPolygon.setVisible(b);
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

                        cmdCopyFlaeche.setVisible(isEditable);
                        cmdPasteFlaeche.setVisible(isEditable);
                        jSeparator8.setVisible(isEditable);
                    }
                });
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("MapPanel --> setComponentEditable finished");
        }

        // Clear the undo/redo memory to seperate the edit sessions
        mappingComponent.getMemUndo().clear();
        mappingComponent.getMemRedo().clear();
    }

    @Override
    public synchronized void clearComponent() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdZoomActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdZoomActionPerformed
        removeMainGroupSelection();
        cmdZoom.setSelected(true);
        mappingComponent.setInteractionMode(MappingComponent.ZOOM);
    }                                                                           //GEN-LAST:event_cmdZoomActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSnapActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSnapActionPerformed
        if (LOG.isDebugEnabled()) {
            LOG.debug("Set snapping Enabled: " + cmdSnap.isSelected());
        }
        cmdSnap.setSelected(!cmdSnap.isSelected());
        // TODO CHANGE CONFIG FILE ACTION
        // cismapPrefs.getGlobalPrefs().setSnappingEnabled(cmdSnap.isSelected());
        // cismapPrefs.getGlobalPrefs().setSnappingPreviewEnabled(cmdSnap.isSelected());
        mappingComponent.setSnappingEnabled(cmdSnap.isSelected());
        mappingComponent.setVisualizeSnappingEnabled(cmdSnap.isSelected());
        mappingComponent.setInGlueIdenticalPointsMode(cmdSnap.isSelected());
    } //GEN-LAST:event_cmdSnapActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdWmsBackgroundActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdWmsBackgroundActionPerformed
        if (mappingComponent.isBackgroundEnabled()) {
            mappingComponent.setBackgroundEnabled(false);
            cmdWmsBackground.setSelected(false);
        } else {
            mappingComponent.setBackgroundEnabled(true);
            cmdWmsBackground.setSelected(true);
            mappingComponent.queryServices();
        }
    }                                                                                    //GEN-LAST:event_cmdWmsBackgroundActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdForwardActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdForwardActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_cmdForwardActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdBackActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdBackActionPerformed
    }                                                                           //GEN-LAST:event_cmdBackActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdFullPoly1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdFullPoly1ActionPerformed
        mappingComponent.zoomToSelectedNode();
    }                                                                                //GEN-LAST:event_cmdFullPoly1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdFullPolyActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdFullPolyActionPerformed
        mappingComponent.zoomToFullFeatureCollectionBounds();
    }                                                                               //GEN-LAST:event_cmdFullPolyActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdForegroundActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdForegroundActionPerformed
        if (mappingComponent.isFeatureCollectionVisible()) {
            mappingComponent.setFeatureCollectionVisibility(false);
            cmdForeground.setSelected(false);
        } else {
            mappingComponent.setFeatureCollectionVisibility(true);
            cmdForeground.setSelected(true);
        }
    }                                                                                 //GEN-LAST:event_cmdForegroundActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdUndoActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdUndoActionPerformed
        LOG.info("UNDO");
        final CustomAction a = mappingComponent.getMemUndo().getLastAction();
        if (LOG.isDebugEnabled()) {
            LOG.debug("... Aktion ausf\u00FChren: " + a.info());
        }
        try {
            a.doAction();
        } catch (Exception e) {
            LOG.error("Error beim Ausf\u00FChren der Aktion", e);
        }
        final CustomAction inverse = a.getInverse();
        mappingComponent.getMemRedo().addAction(inverse);
        if (LOG.isDebugEnabled()) {
            LOG.debug("... neue Aktion auf REDO-Stack: " + inverse);
            LOG.debug("... fertig");
        }
    }                                                                           //GEN-LAST:event_cmdUndoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRedoActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdRedoActionPerformed
        LOG.info("REDO");
        final CustomAction a = mappingComponent.getMemRedo().getLastAction();
        if (LOG.isDebugEnabled()) {
            LOG.debug("... Aktion ausf\u00FChren: " + a.info());
        }
        try {
            a.doAction();
        } catch (Exception e) {
            LOG.error("Error beim Ausf\u00FChren der Aktion", e);
        }
        final CustomAction inverse = a.getInverse();
        mappingComponent.getMemUndo().addAction(inverse);
        if (LOG.isDebugEnabled()) {
            LOG.debug("... neue Aktion auf UNDO-Stack: " + inverse);
            LOG.debug("... fertig");
        }
    }                                                                           //GEN-LAST:event_cmdRedoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSearchAlkisLandparcelActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSearchAlkisLandparcelActionPerformed
        removeMainGroupSelection();
        cmdSearchAlkisLandparcel.setSelected(true);
        mappingComponent.setInteractionMode(FlurstueckNodesSearchCreateSearchGeometryListener.NAME);
    }                                                                                            //GEN-LAST:event_cmdSearchAlkisLandparcelActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSearchVermessungRissActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSearchVermessungRissActionPerformed
        removeMainGroupSelection();
        cmdSearchVermessungRiss.setSelected(true);
        mappingComponent.setInteractionMode(RissNodesSearchCreateSearchGeometryListener.NAME);
    }                                                                                           //GEN-LAST:event_cmdSearchVermessungRissActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdSearchBaulastenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdSearchBaulastenActionPerformed
        removeMainGroupSelection();
        cmdSearchBaulasten.setSelected(true);
        mappingComponent.setInteractionMode(BaulastblattNodesSearchCreateSearchGeometryListener.NAME);
    }                                                                                      //GEN-LAST:event_cmdSearchBaulastenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  notfication  DOCUMENT ME!
     */
    public void featureDeleteRequested(final PNotification notfication) {
        try {
            final Object o = notfication.getObject();
            if (o instanceof DeleteFeatureListener) {
                final DeleteFeatureListener dfl = (DeleteFeatureListener)o;
                final PFeature pf = dfl.getFeatureRequestedForDeletion();
                pf.getFeature().setGeometry(null);
                if (pf.getFeature() instanceof VerwaltungsbereichCustomBean) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Verwaltungsbereichsgeometrie wurde gelöscht setze Flächee = 0");
                    }
                }
            }
        } catch (Exception ex) {
            LOG.warn("Fehler beim featuredeleteRequest", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  notification  DOCUMENT ME!
     */
    public void joinPolygons(final PNotification notification) {
        PFeature one = mappingComponent.getSelectedNode();
        PFeature two;

        if (LOG.isDebugEnabled()) {
            LOG.debug("");
        }
        final Object o = notification.getObject();

        if (o instanceof JoinPolygonsListener) {
            final JoinPolygonsListener listener = ((JoinPolygonsListener)o);
            final PFeature joinCandidate = listener.getFeatureRequestedForJoin();
            if ((joinCandidate.getFeature() instanceof StyledFeature)
                        || (joinCandidate.getFeature() instanceof PureNewFeature)) {
                final int CTRL_MASK = 2; // TODO: HIer noch eine korrekte Konstante verwenden
                if ((listener.getModifier() & CTRL_MASK) != 0) {
                    if ((one != null) && (joinCandidate != one)) {
                        if ((one.getFeature() instanceof PureNewFeature)
                                    && (joinCandidate.getFeature() instanceof StyledFeature)) {
                            two = one;

                            one = joinCandidate;
                            one.setSelected(true);
                            two.setSelected(false);
                            mappingComponent.getFeatureCollection().select(one.getFeature());
                        } else {
                            two = joinCandidate;
                        }
                        try {
                            final Geometry backup = one.getFeature().getGeometry();
                            final Geometry newGeom = one.getFeature()
                                        .getGeometry()
                                        .union(two.getFeature().getGeometry());
                            if (newGeom.getGeometryType().equalsIgnoreCase("Multipolygon")) {
                                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                    "Es können nur Polygone zusammengefasst werden, die aneinander angrenzen oder sich überlappen.",
                                    "Zusammenfassung nicht möglich",
                                    JOptionPane.WARNING_MESSAGE,
                                    null);
                                return;
                            }
                            if (newGeom.getGeometryType().equalsIgnoreCase("Polygon")
                                        && (((Polygon)newGeom).getNumInteriorRing() > 0)) {
                                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                    "Polygone können nur dann zusammengefasst werden, wenn dadurch kein Loch entsteht.",
                                    "Zusammenfassung nicht möglich",
                                    JOptionPane.WARNING_MESSAGE,
                                    null);
                                return;
                            }
                            if ((one.getFeature() instanceof StyledFeature)
                                        && (two.getFeature() instanceof StyledFeature)) {
                                final StyledFeature fOne = (StyledFeature)one.getFeature();
                                final StyledFeature fTwo = (StyledFeature)two.getFeature();

                                if (((fOne instanceof VerwaltungsbereichCustomBean)
                                                && !(fTwo instanceof VerwaltungsbereichCustomBean))
                                            || ((fTwo instanceof VerwaltungsbereichCustomBean)
                                                && !(fOne instanceof VerwaltungsbereichCustomBean))) {
                                    JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                        "Flächeen können nur zusammengefasst werden, wenn die Flächeenart gleich ist.",
                                        "Zusammenfassung nicht möglich",
                                        JOptionPane.WARNING_MESSAGE,
                                        null);
                                    return;
                                }

                                if (((fOne instanceof RebeCustomBean) && !(fTwo instanceof RebeCustomBean))
                                            || ((fTwo instanceof RebeCustomBean) && !(fOne instanceof RebeCustomBean))) {
                                    JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                        "Flächeen können nur zusammengefasst werden, wenn die Flächeenart gleich ist.",
                                        "Zusammenfassung nicht möglich",
                                        JOptionPane.WARNING_MESSAGE,
                                        null);
                                    return;
                                }

                                two.getFeature().setGeometry(null);
                            }
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("newGeom ist vom Typ:" + newGeom.getGeometryType());
                            }
                            one.getFeature().setGeometry(newGeom);
                            if (!(one.getFeature().getGeometry().equals(backup))) {
                                two.removeFromParent();
                                two = null;
                            }
                            one.visualize();
                        } catch (Exception e) {
                            LOG.error("one: " + one + "\n two: " + two, e);
                        }
                        return;
                    }
                } else {
                    final PFeature pf = joinCandidate;
                    if (one != null) {
                        one.setSelected(false);
                    }
                    one = pf;
                    mappingComponent.selectPFeatureManually(one);
                    if (one.getFeature() instanceof StyledFeature) {
                        final StyledFeature f = (StyledFeature)one.getFeature();
                        mappingComponent.getFeatureCollection().select(f);
                        // tableModel.setSelectedFlaeche(f);
                        // fireAuswahlChanged(f);
                        try {
                            // TODO
                            // makeRowVisible(this.jxtOverview,jxtOverview.getFilters().convertRowIndexToView(tableModel.getIndexOfFlaeche((Flaeche)f)));
                        } catch (Exception e) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Fehler beim Scrollen der Tabelle", e);
                            }
                        }
                    } else {
                        // tableModel.setSelectedFlaeche(null);
                        mappingComponent.getFeatureCollection().unselectAll();
                        // fireAuswahlChanged(null);
                    }
                }
            }
        }
    }

    /**
     * TODO MEssage to the user if a area could not be attached for example wfs areas.
     *
     * @param  notification  DOCUMENT ME!
     */
    public void attachFeatureRequested(final PNotification notification) {
        final Object o = notification.getObject();
        LOG.info("Try to attach Geometry");
        final AttachFeatureListener afl = (AttachFeatureListener)o;
        final PFeature pf = afl.getFeatureToAttach();
        if (pf.getFeature() instanceof PureNewFeature) {
            final Geometry g = pf.getFeature().getGeometry();
            final GeometrySlotInformation slotInfo = LagisBroker.getInstance().assignGeometry(g);
            if (slotInfo != null) {
                slotInfo.getRefreshable().refresh(null);
                mappingComponent.getFeatureCollection().removeFeature(pf.getFeature());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Geometrie: " + slotInfo.getOpenSlot().getGeometry() + " wird hinzugefügt");
                }

                final String providerName = slotInfo.getProviderName();
                final Feature openSlot = slotInfo.getOpenSlot();
                final StyledFeatureGroupWrapper featureWrapper = new CustomSelectionStyledFeatureGroupWrapper(
                        (StyledFeature)openSlot,
                        providerName,
                        providerName);

                featureWrapper.setEditable(true);
                mappingComponent.getFeatureCollection().addFeature(featureWrapper);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Geometrie wurde an element: " + slotInfo.getSlotIdentifier() + " attached");
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Geometrie wurde nicht attached");
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  notification  DOCUMENT ME!
     */
    public void splitPolygon(final PNotification notification) {
        final Object o = notification.getObject();
        if (o instanceof SplitPolygonListener) {
            final SplitPolygonListener l = (SplitPolygonListener)o;
            final PFeature pf = l.getFeatureClickedOn();
            if (pf.isSplittable()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Split");
                }
                final Feature[] f_arr = pf.split();
                ((StyledFeature)pf.getFeature()).setGeometry(null);
                mappingComponent.getFeatureCollection().removeFeature(pf.getFeature());
                f_arr[0].setEditable(true);
                f_arr[1].setEditable(true);
                mappingComponent.getFeatureCollection().addFeature(f_arr[0]);
                mappingComponent.getFeatureCollection().addFeature(f_arr[1]);
                cmdAttachPolyToAlphadataActionPerformed(null);
            }
        }
    }

    /**
     * ToDo implement.
     *
     * @param  notification  DOCUMENT ME!
     */
    public void coordinatesChanged(final PNotification notification) {
        final Object o = notification.getObject();
        final PFeature pf = ((SimpleMoveListener)o).getUnderlyingPFeature();

        if (pf != this.lastOverFeature) {
            this.lastOverFeature = pf;
            if ((pf != null) && (pf.getFeature() instanceof CidsLayerFeature)
                        && pf.getVisible()
                        && (pf.getParent() != null) && pf.getParent().getVisible()) {
                final CidsLayerFeature feature = (CidsLayerFeature)pf.getFeature();
                final String alkisId = (String)feature.getBean().getProperty("alkis_id");
                final String[] parts = alkisId.split("-");
                final String[] znParts = parts[2].split("/");

                final String gemarkung = parts[0].substring(2);
                final String flur = Integer.toString(Integer.parseInt(parts[1]));
                final String flurstz = Integer.toString(Integer.parseInt(znParts[0]));
                final String flurstn = (znParts.length > 1) ? Integer.toString(Integer.parseInt(znParts[1])) : "0";

                final GemarkungCustomBean gem = LagisBroker.getInstance()
                            .getGemarkungForKey(Integer.parseInt(gemarkung));
                this.lblInfo.setText(gem.getBezeichnung() + ' ' + flur + ' ' + flurstz + '/' + flurstn);
            } else {
                this.lblInfo.setText("");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  notfication  DOCUMENT ME!
     */
    public void selectionChanged(final PNotification notfication) {
        final Object o = notfication.getObject();
        if ((o instanceof SelectionListener) || (o instanceof FeatureMoveListener)
                    || (o instanceof SplitPolygonListener)) {
            if (o instanceof SelectionListener) {
                final PFeature pf = ((SelectionListener)o).getAffectedPFeature();
                if (cmdSelect.isSelected() && (((SelectionListener)o).getClickCount() > 1)
                            && (pf.getFeature() instanceof CidsLayerFeature)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("CidsLayerFeature selected");
                    }
                    final CidsLayerFeature clf = ((CidsLayerFeature)pf.getFeature());
                    if (LagisBroker.getInstance().isInEditMode()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Flurstück kann nicht gewechselt werden --> Editmode");
                        }
                        JOptionPane.showMessageDialog(LagisApp.getInstance(),
                            "Das Flurstück kann nur gewechselt werden, wenn alle Änderungen gespeichert oder verworfen worden sind.",
                            "Wechseln nicht möglich",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    final String alkisId = (String)clf.getBean().getProperty("alkis_id");
                    final String[] parts = alkisId.split("-");
                    final String[] znParts = parts[2].split("/");

//
                    try {
                        final String gem = parts[0].substring(2);
                        final String flur = Integer.toString(Integer.parseInt(parts[1]));
                        final String flurstz = Integer.toString(Integer.parseInt(znParts[0]));
                        final String flurstn = (znParts.length > 1) ? Integer.toString(Integer.parseInt(znParts[1]))
                                                                    : "0";
                        if ((gem != null) && (flur != null) && (flurstz != null)) {
                            GemarkungCustomBean resolvedGemarkung = LagisBroker.getInstance()
                                        .getGemarkungForKey(Integer.parseInt(gem));
                            // TODO if this case happens it leads to bug XXX
                            if (resolvedGemarkung == null) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Gemarkung konnte nicht entschlüsselt werden");
                                }
                                resolvedGemarkung = GemarkungCustomBean.createNew();
                                resolvedGemarkung.setSchluessel(Integer.parseInt(gem));
                            } else {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Gemarkung konnte entschlüsselt werden");
                                }
                            }
                            // GemarkungCustomBean cplGemarkung =
                            // EJBroker.getInstance().completeGemarkung(gemarkung); if (cplGemarkung != null){
                            // log.debug("gemarkung bekannt"); gemarkung = cplGemarkung; }
                            final FlurstueckSchluesselCustomBean key = FlurstueckSchluesselCustomBean.createNew();
                            key.setGemarkung(resolvedGemarkung);
                            key.setFlur(Integer.parseInt(flur));
                            key.setFlurstueckZaehler(Integer.parseInt(flurstz));
                            if (flurstn != null) {
                                key.setFlurstueckNenner(Integer.parseInt(flurstn));
                            } else {
                                key.setFlurstueckNenner(0);
                            }
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Schlüssel konnte konstruiert werden");
                            }
                            LagisBroker.getInstance().loadFlurstueck(key);
                        } else {
                            LOG.error(
                                "Properties == null Flurstueck oder Identifier im Konfigfile nicht richtig gesetzt --> kann nicht ausgewählt werden");
                        }
                    } catch (final Exception ex) {
                        LOG.error("Fehler beim laden des ausgewählten Flurstücks", ex);
                    }
                }
            }
        }
    }

    @Override
    public void refresh(final Object refreshObject) {
    }

    @Override
    public String getWidgetName() {
        return WIDGET_NAME;
    }

    @Override
    public void masterConfigure(final Element parent) {
    }

    @Override
    public Element getConfiguration() {
        final Element mapPanelElement = new Element("mapPanel");
        final Element featureLlayerTransparencyElement = new Element("featureLayerTransparency");
        featureLlayerTransparencyElement.setText(Float.toString(
                CismapBroker.getInstance().getMappingComponent().getFeatureLayer().getTransparency()));
        mapPanelElement.addContent(featureLlayerTransparencyElement);

        final Element selectedFeatureGroupButtonsElement = new Element("featureGroupButtons");
        for (final String featureGroupName : featureGroupButtonListenerMap.keySet()) {
            final JToggleButton button = featureGroupButtonListenerMap.get(featureGroupName).button;
            if (button != null) {
                final Element selectedFeatureGroupButtonElement = new Element("featureGroupButton");
                selectedFeatureGroupButtonElement.setAttribute("name", featureGroupName);
                selectedFeatureGroupButtonElement.setAttribute("selected", Boolean.toString(button.isSelected()));
                selectedFeatureGroupButtonsElement.addContent(selectedFeatureGroupButtonElement);
            }
        }
        mapPanelElement.addContent(selectedFeatureGroupButtonsElement);
        return mapPanelElement;
    }

    @Override
    public void update(final Observable o, final Object arg) {
        if (o.equals(mappingComponent.getMemUndo())) {
            if (arg.equals(MementoInterface.ACTIVATE) && !cmdUndo.isEnabled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("UNDO-Button aktivieren");
                }
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            cmdUndo.setEnabled(true);
                        }
                    });
            } else if (arg.equals(MementoInterface.DEACTIVATE) && cmdUndo.isEnabled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("UNDO-Button deaktivieren");
                }
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            cmdUndo.setEnabled(false);
                        }
                    });
            }
        } else if (o.equals(mappingComponent.getMemRedo())) {
            if (arg.equals(MementoInterface.ACTIVATE) && !cmdRedo.isEnabled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("REDO-Button aktivieren");
                }
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            cmdRedo.setEnabled(true);
                        }
                    });
            } else if (arg.equals(MementoInterface.DEACTIVATE) && cmdRedo.isEnabled()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("REDO-Button deaktivieren");
                }
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            cmdRedo.setEnabled(false);
                        }
                    });
            }
        }
    }

    @Override
    public void configure(final Element parent) {
        try {
            if (parent != null) {
                final Element mapPanelElement = parent.getChild("mapPanel");
                if (mapPanelElement != null) {
                    final Element featureLayerTransparencyElement = mapPanelElement.getChild(
                            "featureLayerTransparency");
                    if (featureLayerTransparencyElement != null) {
                        try {
                            CismapBroker.getInstance()
                                    .getMappingComponent()
                                    .getFeatureLayer()
                                    .setTransparency(Float.parseFloat(featureLayerTransparencyElement.getText()));
                        } catch (final Exception ex) {
                        }
                    }

                    final Element selectedFeatureGroupButtonsElements = mapPanelElement.getChild("featureGroupButtons");
                    if (selectedFeatureGroupButtonsElements != null) {
                        for (final Element selectedFeatureGroupButtonElement
                                    : (List<Element>)selectedFeatureGroupButtonsElements.getChildren(
                                        "featureGroupButton")) {
                            final String featureGroupName = selectedFeatureGroupButtonElement.getAttributeValue("name");
                            boolean selected = true;
                            try {
                                selected = Boolean.parseBoolean(selectedFeatureGroupButtonElement.getAttributeValue(
                                            "selected"));
                            } catch (final Exception ex) {
                            }
                            final FeatureGroupActionListener listener = featureGroupButtonListenerMap.get(
                                    featureGroupName);
                            if (listener != null) {
                                final JToggleButton button = listener.button;
                                button.setSelected(selected);
                                listener.actionPerformed(null);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim masterConfigure von: " + this.getClass(), ex);
        }
    }

    @Override
    public void featuresRemoved(final FeatureCollectionEvent fce) {
    }

    @Override
    public void featuresChanged(final FeatureCollectionEvent fce) {
    }

    @Override
    public void featuresAdded(final FeatureCollectionEvent fce) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Features Added");
        }

        final Collection<Feature> features = fce.getEventFeatures();

        FeatureGroupMember fgm;

        final HashSet<String> groups = new HashSet<>();
        for (final Feature f : features) {
            if (f instanceof FeatureGroupMember) {
                fgm = (FeatureGroupMember)f;
                groups.add(fgm.getGroupId());
            }
        }

        FeatureGroupActionListener listener;
        for (final String group : groups) {
            listener = this.featureGroupButtonListenerMap.get(group);
            if (listener != null) {
                listener.actionPerformed(null);
            }
        }
    }

    @Override
    public void featureSelectionChanged(final FeatureCollectionEvent fce) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("FeatureSelection Changed");
        }

        final Collection<Feature> features = fce.getEventFeatures();

        if (LagisBroker.getInstance().isInEditMode() && (features != null) && (features.size() > 0)) {
            final Iterator<Feature> it = features.iterator();
            cmdCopyFlaeche.setEnabled(false);
            while (it.hasNext()) {
                final Feature curFeature = it.next();
                if (curFeature.canBeSelected()
                            && LagisBroker.getInstance().getMappingComponent().getFeatureCollection().isSelected(
                                curFeature)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("In edit modus, mindestens ein feature selectiert: " + curFeature);
                    }
                    cmdCopyFlaeche.setEnabled(true);
                    break;
                }
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("disable copy nicht alle vorraussetzungen erfüllt");
            }
            cmdCopyFlaeche.setEnabled(false);
        }

        LagisBroker.getInstance().fireChangeEvent(features);
    }

    @Override
    public void featureReconsiderationRequested(final FeatureCollectionEvent fce) {
    }

    @Override
    public void allFeaturesRemoved(final FeatureCollectionEvent fce) {
    }

    // TODO USE
    @Override
    public Icon getWidgetIcon() {
        return null;
    }

    @Override
    public void featureCollectionChanged() {
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class FeatureGroupActionListener implements ActionListener {

        //~ Instance fields ----------------------------------------------------

        private final JToggleButton button;
        private final MappingComponent mapComp;
        private final String featureGroup;
        private final String visibleText;
        private final String invisibText;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new FeatureGroupActionListener object.
         *
         * @param  button        DOCUMENT ME!
         * @param  mapComp       DOCUMENT ME!
         * @param  featureGroup  DOCUMENT ME!
         * @param  tooltipText   DOCUMENT ME!
         */
        public FeatureGroupActionListener(final JToggleButton button,
                final MappingComponent mapComp,
                final String featureGroup,
                final String tooltipText) {
            this.button = button;
            this.mapComp = mapComp;
            this.featureGroup = featureGroup;
            this.visibleText = tooltipText + " ausblenden";
            this.invisibText = tooltipText + " einblenden";
            this.button.setToolTipText(visibleText);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  isVisible  DOCUMENT ME!
         */
        private void setVisible(final boolean isVisible) {
            button.setToolTipText(isVisible ? visibleText : invisibText);
            mapComp.setGroupLayerVisibility(featureGroup, isVisible);
        }

        @Override
        public void actionPerformed(final ActionEvent ae) {
            this.setVisible(button.isSelected());
        }
    }
}
