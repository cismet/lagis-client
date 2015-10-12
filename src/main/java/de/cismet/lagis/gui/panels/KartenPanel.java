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

import edu.umd.cs.piccolo.PNode;
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

import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;
import de.cismet.cids.custom.beans.lagis.GemarkungCustomBean;
import de.cismet.cids.custom.beans.lagis.RebeCustomBean;
import de.cismet.cids.custom.beans.lagis.VerwaltungsbereichCustomBean;

import de.cismet.cismap.commons.features.DefaultFeatureServiceFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollectionEvent;
import de.cismet.cismap.commons.features.FeatureCollectionListener;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.features.WFSFeature;
import de.cismet.cismap.commons.gui.FeatureGroupMember;
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

import de.cismet.lagis.interfaces.FlurstueckChangeListener;
import de.cismet.lagis.interfaces.NoPermissionsWidget;

import de.cismet.lagis.utillity.GeometrySlotInformation;

import de.cismet.lagis.widget.AbstractWidget;
import de.cismet.lagis.widget.RessortFactory;

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

    //~ Instance fields --------------------------------------------------------

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private MappingComponent mappingComponent;
    private boolean isSnappingEnabled = false;
    private String gemarkungIdentifier = null;
    private String flurIdentifier = null;
    private String flurstueckZaehlerIdentifier = null;
    private String flurstueckNennerIdentifier = null;
    private boolean isEditable = true;
    private final JButton cmdCopyFlaeche = new JButton();
    private final JButton cmdPasteFlaeche = new JButton();
    private Object clipboard = null;
    private boolean clipboardPasted = true; // wegen des ersten mals
    private final ArrayList<Feature> copiedFeatures = new ArrayList<Feature>();
    private final Map<String, FeatureGroupActionListener> featureGroupButtonListenerMap;
    private final JLabel lblInfo;
    private Object lastOverFeature;
    // Variables declaration - do not modify
    // NOI18N
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
    private javax.swing.JButton cmdRedo;
    private javax.swing.JButton cmdRemoveHandle;
    private javax.swing.JButton cmdRemovePolygon;
    private javax.swing.JButton cmdSelect;
    private javax.swing.JButton cmdSnap;
    private javax.swing.JButton cmdSplitPoly;
    private javax.swing.JButton cmdUndo;
    private javax.swing.JButton cmdWmsBackground;
    private javax.swing.JButton cmdZoom;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
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

        this.featureGroupButtonListenerMap = new HashMap<String, FeatureGroupActionListener>();

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
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  feature  DOCUMENT ME!
     */
    public void doStuff(final WFSFeature feature) {
        final HashMap props = feature.getProperties();
        final String gem = (String)props.get(gemarkungIdentifier);
        final String flur = (String)props.get(flurIdentifier);
        final String flurstz = (String)props.get(
                flurstueckZaehlerIdentifier);
        final String flurstn = (String)props.get(
                flurstueckNennerIdentifier);
        final FlurstueckSchluesselCustomBean key = FlurstueckSchluesselCustomBean.createNew();
        key.setGemarkung(
            LagisBroker.getInstance().getGemarkungForKey(
                Integer.parseInt(gem)));
        key.setFlur(Integer.parseInt(flur));
        key.setFlurstueckZaehler(Integer.parseInt(flurstz));
        if (flurstn != null) {
            key.setFlurstueckNenner(Integer.parseInt(flurstn));
        } else {
            key.setFlurstueckNenner(0);
        }
        feature.setName("Flurstück " + key.toString());
    }
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
        final JButton button = new JButton();

        button.setIcon(new ImageIcon(getClass().getResource(icon)));
        button.setPressedIcon(widgetIcon);

        final FeatureGroupActionListener listener = new FeatureGroupActionListener(
                button,
                mappingComponent,
                featureGroup,
                tooltipText,
                icon,
                invisIcon);

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
                final boolean cutting = false;

                while (it.hasNext()) {
                    final Feature clipboardFlaeche = (Feature)it.next();
                    final PureNewFeature newFeature = new PureNewFeature((Geometry)clipboardFlaeche.getGeometry()
                                    .clone());
                    newFeature.setCanBeSelected(true);
                    newFeature.setEditable(true);
                    copiedFeatures.add(newFeature);
                }
            }

            // storeClipboardBackup();
            if (copiedFeatures.size() > 0) {
                clipboardPasted = false;
                this.cmdPasteFlaeche.setEnabled(true);
            } else {
                this.cmdPasteFlaeche.setEnabled(false);
            }
        }
    }

    /**
     * public Object getSelectedGeometries() { if
     * (LagisBroker.getInstance().getMappingComponent().getFeatureCollection().getSelectedFeatures() != null &&
     * LagisBroker.getInstance().getMappingComponent().getFeatureCollection().getSelectedFeatures().size() > 0) { //
     * Vector clipboard = new Vector(); // int[] rows = flOverviewPanel.getJxtOverview().getSelectedRows(); // for (int
     * i = 0; i < rows.length; ++i) { // int modelIndex =
     * flOverviewPanel.getJxtOverview().getFilters().convertRowIndexToModel(rows[i]); // Flaeche f =
     * flOverviewPanel.getTableModel().getFlaechebyIndex(modelIndex); // f.setClipboardStatus(Flaeche.COPIED); //
     * Flaeche c = (Flaeche) f.clone(); // clipboard.add(c); // } // return clipboard; return new Vector(Vector
     * clipboard = new Vector()); } else { Flaeche sf = flOverviewPanel.getModel().getSelectedFlaeche();
     * sf.setClipboardStatus(Flaeche.COPIED); Flaeche c = (Flaeche) sf.clone(); c.setNewFlaeche(true); return c; } }.
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdPasteFlaecheActionPerformed(final java.awt.event.ActionEvent evt) {
        if (copiedFeatures.size() > 0) {
            LagisBroker.getInstance().getMappingComponent().getFeatureCollection().unselectAll();
            final Iterator it = copiedFeatures.iterator();
            final boolean cutting = false;
            while (it.hasNext()) {
                final Feature clipboardFlaeche = (Feature)it.next();
                final PureNewFeature newFeature = new PureNewFeature((Geometry)clipboardFlaeche.getGeometry().clone());
                newFeature.setCanBeSelected(true);
                newFeature.setEditable(true);
                LagisBroker.getInstance().getMappingComponent().getFeatureCollection().addFeature(newFeature);
            }
            clipboardPasted = true;
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setInteractionMode() {
        final String currentInteractionMode = mappingComponent.getInteractionMode();
        if (currentInteractionMode != null) {
            if (currentInteractionMode.equals(MappingComponent.SELECT)) {
                if (log.isDebugEnabled()) {
                    log.debug("InteractionMode set to SELCET");
                }
                cmdSelectActionPerformed(null);
            } else if (currentInteractionMode.equals(MappingComponent.CUSTOM_FEATUREINFO)) {
                if (log.isDebugEnabled()) {
                    log.debug("InteractionMode set to CUSTOM_FEATUREINFO");
                }
                // cmdALB.setSelected(true);
                cmdALBActionPerformed(null);
            } else if (currentInteractionMode.equals(MappingComponent.PAN)) {
                if (log.isDebugEnabled()) {
                    log.debug("InteractionMode set to PAN");
                }
                cmdPanActionPerformed(null);
            } else if (currentInteractionMode.equals(MappingComponent.NEW_POLYGON)) {
                if (log.isDebugEnabled()) {
                    log.debug("InteractionMode set to NEW_POLYGON");
                }
                cmdNewPolygonActionPerformed(null);
            } else if (currentInteractionMode.equals(MappingComponent.ZOOM)) {
                if (log.isDebugEnabled()) {
                    log.debug("InteractionMode set to ZOOM");
                }
                cmdZoomActionPerformed(null);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Unknown Interactionmode: " + currentInteractionMode);
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("InteractionMode == null");
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
        cmdUndo = new javax.swing.JButton();
        cmdRedo = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
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

        cmdALB.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/alb.png")));          // NOI18N
        cmdALB.setToolTipText("Auswählen");
        cmdALB.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cmdALB.setFocusPainted(false);
        cmdALB.setFocusable(false);
        cmdALB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdALB.setRolloverSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/alb_selected.png"))); // NOI18N
        cmdALB.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/alb_selected.png"))); // NOI18N
        cmdALB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdALB.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdALBActionPerformed(evt);
                }
            });
        jToolBar1.add(cmdALB);

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
        jToolBar1.add(jSeparator8);

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
                jPanel1Layout.createSequentialGroup().addContainerGap(599, Short.MAX_VALUE).add(
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
    }

    @Override
    public void flurstueckChanged(final FlurstueckCustomBean newFlurstueck) {
        for (final FeatureGroupActionListener listener : this.featureGroupButtonListenerMap.values()) {
            listener.setVisible(true);
        }

        LagisBroker.getInstance().flurstueckChangeFinished(this);
    }

    @Override
    public synchronized void setComponentEditable(final boolean isEditable) {
        if (this.isEditable == isEditable) {
            return;
        }
        this.isEditable = isEditable;
        if (log.isDebugEnabled()) {
            log.debug("MapPanel --> setComponentEditable");
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
            if (log.isDebugEnabled()) {
                log.debug("Anzahl Features in FeatureCollection:"
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
                        if (log.isDebugEnabled()) {
                            log.debug(
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
        if (log.isDebugEnabled()) {
            log.debug("MapPanel --> setComponentEditable finished");
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
        if (log.isDebugEnabled()) {
            log.debug("Set snapping Enabled: " + cmdSnap.isSelected());
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
    private void cmdALBActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdALBActionPerformed
        log.info("ALB");
        removeMainGroupSelection();
        cmdALB.setSelected(true);
        mappingComponent.setInteractionMode(MappingComponent.CUSTOM_FEATUREINFO);
    }                                                                          //GEN-LAST:event_cmdALBActionPerformed

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
        log.info("UNDO");
        final CustomAction a = mappingComponent.getMemUndo().getLastAction();
        if (log.isDebugEnabled()) {
            log.debug("... Aktion ausf\u00FChren: " + a.info());
        }
        try {
            a.doAction();
        } catch (Exception e) {
            log.error("Error beim Ausf\u00FChren der Aktion", e);
        }
        final CustomAction inverse = a.getInverse();
        mappingComponent.getMemRedo().addAction(inverse);
        if (log.isDebugEnabled()) {
            log.debug("... neue Aktion auf REDO-Stack: " + inverse);
            log.debug("... fertig");
        }
    }                                                                           //GEN-LAST:event_cmdUndoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRedoActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdRedoActionPerformed
        log.info("REDO");
        final CustomAction a = mappingComponent.getMemRedo().getLastAction();
        if (log.isDebugEnabled()) {
            log.debug("... Aktion ausf\u00FChren: " + a.info());
        }
        try {
            a.doAction();
        } catch (Exception e) {
            log.error("Error beim Ausf\u00FChren der Aktion", e);
        }
        final CustomAction inverse = a.getInverse();
        mappingComponent.getMemUndo().addAction(inverse);
        if (log.isDebugEnabled()) {
            log.debug("... neue Aktion auf UNDO-Stack: " + inverse);
            log.debug("... fertig");
        }
    }                                                                           //GEN-LAST:event_cmdRedoActionPerformed

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
                    if (log.isDebugEnabled()) {
                        log.debug("Verwaltungsbereichsgeometrie wurde gelöscht setze Flächee = 0");
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Fehler beim featuredeleteRequest", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  notification  DOCUMENT ME!
     */
    public void joinPolygons(final PNotification notification) {
        PFeature one;
        PFeature two;
        one = mappingComponent.getSelectedNode();
        two = null;
        if (log.isDebugEnabled()) {
            log.debug("");
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
                            // tableModel.setSelectedFlaeche((Flaeche)one.getFeature());
                            // TODO implement or erase
                            // fireAuswahlChanged(one.getFeature());
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
                            if ((one != null) && (two != null) && (one.getFeature() instanceof StyledFeature)
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
                                // tableModel.removeFlaeche(fTwo);
                                // TODO größe updaten

                                // TODO make it right
                                // fOne.setGr_grafik(new Integer((int)(newGeom.getArea())));
// if (fOne.getBemerkung()!=null && fOne.getBemerkung().trim().length()>0) {
// fOne.setBemerkung(fOne.getBemerkung()+"\n");
// }
// fOne.setBemerkung(fTwo.getJoinBackupString());
// if (!fOne.isSperre()&&fTwo.isSperre()) {
// fOne.setSperre(true);
// fOne.setBem_sperre("JOIN::"+fTwo.getBem_sperre());
// }
// fOne.sync();
// //tableModel.fireSelectionChanged(); TODO
// fireAuswahlChanged(fOne);
                            }
                            if (one.getFeature() instanceof VerwaltungsbereichCustomBean) {
                                // Eine vorhandene Flächee und eine neuangelegt wurden gejoint
                                // ((Flaeche)(one.getFeature())).sync(); tableModel.fireSelectionChanged(); TODO
                                // fireAuswahlChanged((Flaeche)(one.getFeature()));
                            }
                            if (log.isDebugEnabled()) {
                                log.debug("newGeom ist vom Typ:" + newGeom.getGeometryType());
                            }
                            one.getFeature().setGeometry(newGeom);
                            if (!(one.getFeature().getGeometry().equals(backup))) {
                                two.removeFromParent();
                                two = null;
                            }
                            one.visualize();
                        } catch (Exception e) {
                            log.error("one: " + one + "\n two: " + two, e);
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
                            if (log.isDebugEnabled()) {
                                log.debug("Fehler beim Scrollen der Tabelle", e);
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
        log.info("Try to attach Geometry");
        final AttachFeatureListener afl = (AttachFeatureListener)o;
        final PFeature pf = afl.getFeatureToAttach();
        if (pf.getFeature() instanceof PureNewFeature) {
            final Geometry g = pf.getFeature().getGeometry();
            final GeometrySlotInformation slotInfo = LagisBroker.getInstance().assignGeometry(g);
            if (slotInfo != null) {
                slotInfo.getRefreshable().refresh(null);
                mappingComponent.getFeatureCollection().removeFeature(pf.getFeature());
                if (log.isDebugEnabled()) {
                    log.debug("Geometrie: " + slotInfo.getOpenSlot().getGeometry() + " wird hinzugefügt");
                }

                final String providerName = slotInfo.getProviderName();
                final Feature openSlot = slotInfo.getOpenSlot();
                final StyledFeatureGroupWrapper featureWrapper = new StyledFeatureGroupWrapper((StyledFeature)openSlot,
                        providerName,
                        providerName);

                featureWrapper.setEditable(true);
                mappingComponent.getFeatureCollection().addFeature(featureWrapper);
                if (log.isDebugEnabled()) {
                    log.debug("Geometrie wurde an element: " + slotInfo.getSlotIdentifier() + " attached");
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Geometrie wurde nicht attached");
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
                if (log.isDebugEnabled()) {
                    log.debug("Split");
                }
                ((StyledFeature)pf.getFeature()).setGeometry(null);
                final Feature[] f_arr = pf.split();
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

            if ((pf != null) && (pf.getFeature() instanceof DefaultFeatureServiceFeature)
                        && pf.getVisible()
                        && (pf.getParent() != null) && pf.getParent().getVisible()) {
                final DefaultFeatureServiceFeature sf = (DefaultFeatureServiceFeature)pf.getFeature();

                final String gemarkung = (String)sf.getProperty("app:gem");
                final String flur = (String)sf.getProperty("app:flur");
                final String flurstz = (String)sf.getProperty("app:flurstz");
                final String flurstn = (String)sf.getProperty("app:flurstn");

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
            final PNode p = null;
            PFeature pf = null;
            if (o instanceof SelectionListener) {
                pf = ((SelectionListener)o).getSelectedPFeature();
                //
                // if (pf!=null && pf.getFeature() instanceof Flaeche|| pf.getFeature() instanceof PureNewFeature) {
                // if (((DefaultFeatureCollection)mappingComp.getFeatureCollection()).isSelected(pf.getFeature())) {
                // if (((DefaultFeatureCollection)mappingComp.getFeatureCollection()).getSelectedFeatures().size()>1) {
                // int index=sorter.getSortedPosition(getTableModel().getIndexOfFlaeche((Flaeche)pf.getFeature()));
                // tblOverview.getSelectionModel().addSelectionInterval(index,index);
                // } else {
                // int index=sorter.getSortedPosition(getTableModel().getIndexOfFlaeche((Flaeche)pf.getFeature()));
                // tblOverview.getSelectionModel().setSelectionInterval(index,index);
                // }
                // }
                // else {
                // int index=sorter.getSortedPosition(getTableModel().getIndexOfFlaeche((Flaeche)pf.getFeature()));
                // tblOverview.getSelectionModel().removeSelectionInterval(index,index);
                // }
                // } else
                if (cmdSelect.isSelected() && (((SelectionListener)o).getClickCount() > 1)
                            && (pf.getFeature() instanceof WFSFeature)) {
                    if (log.isDebugEnabled()) {
                        log.debug("WFSFeature selected");
                        // log.debug("test"+((DefaultWFSFeature)pf.getFeature()).getProperties());
                    }
                    final WFSFeature dwf = ((WFSFeature)pf.getFeature());
                    if (LagisBroker.getInstance().isInEditMode()) {
                        if (log.isDebugEnabled()) {
                            log.debug("Flurstück kann nicht gewechselt werden --> Editmode");
                        }
                        JOptionPane.showMessageDialog(LagisBroker.getInstance().getParentComponent(),
                            "Das Flurstück kann nur gewechselt werden, wenn alle Änderungen gespeichert oder verworfen worden sind.",
                            "Wechseln nicht möglich",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
//
                    final HashMap props = dwf.getProperties();
                    if (log.isDebugEnabled()) {
                        log.debug("WFSFeature properties: " + props);
                    }
                    try {
                        if ((props != null) && checkIfIdentifiersAreSetProperly()) {
                            final String gem = (String)props.get(gemarkungIdentifier);
                            final String flur = (String)props.get(flurIdentifier);
                            final String flurstz = (String)props.get(flurstueckZaehlerIdentifier);
                            final String flurstn = (String)props.get(flurstueckNennerIdentifier);
                            if ((gem != null) && (flur != null) && (flurstz != null)) {
                                GemarkungCustomBean resolvedGemarkung = LagisBroker.getInstance()
                                            .getGemarkungForKey(Integer.parseInt(gem));
                                // TODO if this case happens it leads to bug XXX
                                if (resolvedGemarkung == null) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Gemarkung konnte nicht entschlüsselt werden");
                                    }
                                    resolvedGemarkung = GemarkungCustomBean.createNew();
                                    resolvedGemarkung.setSchluessel(Integer.parseInt(gem));
                                } else {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Gemarkung konnte entschlüsselt werden");
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
                                if (log.isDebugEnabled()) {
                                    log.debug("Schlüssel konnte konstruiert werden");
                                }
                                LagisBroker.getInstance().loadFlurstueck(key);
                                dwf.setName("Flurstück " + key.toString());
                            } else {
                                if (log.isDebugEnabled()) {
                                    log.debug(
                                        "Mindestens ein Property == null Flurstueck kann nicht ausgewählt werden");
                                }
                            }
                        } else {
                            log.error(
                                "Properties == null Flurstueck oder Identifier im Konfigfile nicht richtig gesetzt --> kann nicht ausgewählt werden");
                        }
                    } catch (final Exception ex) {
                        log.error("Fehler beim laden des ausgewählten Flurstücks", ex);
                    }
                }
            }
        }
    }

    @Override
    public void refresh(final Object refreshObject) {
    }
    // End of variables declaration

    @Override
    public String getWidgetName() {
        return WIDGET_NAME;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean checkIfIdentifiersAreSetProperly() {
        return ((gemarkungIdentifier != null) && (flurIdentifier != null) && (flurstueckZaehlerIdentifier != null)
                        && (flurstueckNennerIdentifier != null));
    }

    @Override
    public void masterConfigure(final Element parent) {
        if (log.isDebugEnabled()) {
            log.debug("MasterConfigure: " + this.getClass());
        }
        try {
            final Element identifier = parent.getChild("flurstueckXMLIdentifier");
            gemarkungIdentifier = identifier.getChildText("gemarkungIdentifier");
            if (log.isDebugEnabled()) {
                log.debug("GemarkungsIdentifier: " + gemarkungIdentifier);
            }
            flurIdentifier = identifier.getChildText("flurIdentifier");
            if (log.isDebugEnabled()) {
                log.debug("FlurIdentifier: " + flurIdentifier);
            }
            flurstueckZaehlerIdentifier = identifier.getChildText("flurstueckZaehlerIdentifier");
            if (log.isDebugEnabled()) {
                log.debug("FlurstueckZaehlerIdentifier: " + flurstueckZaehlerIdentifier);
            }
            flurstueckNennerIdentifier = identifier.getChildText("flurstueckNennerIdentifier");
            if (log.isDebugEnabled()) {
                log.debug("FlurstueckNennerIdentifier: " + flurstueckNennerIdentifier);
                log.debug("MasterConfigure: " + this.getClass() + " erfolgreich");
            }
        } catch (Exception ex) {
            log.error("Fehler beim masterConfigure von: " + this.getClass(), ex);
        }
    }

    // to
    @Override
    public Element getConfiguration() {
        return null;
    }

    @Override
    public void update(final Observable o, final Object arg) {
        if (o.equals(mappingComponent.getMemUndo())) {
            if (arg.equals(MementoInterface.ACTIVATE) && !cmdUndo.isEnabled()) {
                if (log.isDebugEnabled()) {
                    log.debug("UNDO-Button aktivieren");
                }
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            cmdUndo.setEnabled(true);
                        }
                    });
            } else if (arg.equals(MementoInterface.DEACTIVATE) && cmdUndo.isEnabled()) {
                if (log.isDebugEnabled()) {
                    log.debug("UNDO-Button deaktivieren");
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
                if (log.isDebugEnabled()) {
                    log.debug("REDO-Button aktivieren");
                }
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            cmdRedo.setEnabled(true);
                        }
                    });
            } else if (arg.equals(MementoInterface.DEACTIVATE) && cmdRedo.isEnabled()) {
                if (log.isDebugEnabled()) {
                    log.debug("REDO-Button deaktivieren");
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

    @Override
    public void featuresRemoved(final FeatureCollectionEvent fce) {
    }

    @Override
    public void featuresChanged(final FeatureCollectionEvent fce) {
        if (log.isDebugEnabled()) {
            // try{
            log.debug("FeatureChanged");
        }
//        Collection<Feature> features =  fce.getEventFeatures();
//        if(features != null){
//            for(Feature currentFeature:features){
//                if(currentFeature instanceof VerwaltungsbereichCustomBean){
//                    ((VerwaltungsbereichCustomBean)currentFeature).setFlaeche((int)currentFeature.getGeometry().getArea());
//                }
//            }
//        }
//        }catch(Exception ex){
//            log.warn("Fehler beim featureChanged");
//        }
    }

    @Override
    public void featuresAdded(final FeatureCollectionEvent fce) {
        if (log.isDebugEnabled()) {
            log.debug("Features Added");
        }

        final Collection<Feature> features = fce.getEventFeatures();

        FeatureGroupMember fgm;

        final HashSet<String> groups = new HashSet<String>();
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
                listener.setVisible(true);
            }
        }
    }

//   public void featureSelectionChanged(final Collection<Feature> features) {
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("FeatureSelectionChanged LagisApp: ");
//        }
//        if (LagisBroker.getInstance().isInEditMode() && (features != null) && (features.size() > 0)) {
//            final Iterator<Feature> it = features.iterator();
//            while (it.hasNext()) {
//                final Feature curFeature = it.next();
//                if (curFeature.canBeSelected()
//                            && LagisBroker.getInstance().getMappingComponent().getFeatureCollection().isSelected(
//                                curFeature)) {
//                    if (LOG.isDebugEnabled()) {
//                        LOG.debug("In edit modus, mindestens ein feature selectiert: " + curFeature);
//                    }
//                    cmdCopyFlaeche.setEnabled(true);
//                    return;
//                }
//            }
//            cmdCopyFlaeche.setEnabled(false);
//        } else {
//            if (LOG.isDebugEnabled()) {
//                LOG.debug("disable copy nicht alle vorraussetzungen erfüllt");
//            }
//            cmdCopyFlaeche.setEnabled(false);
//        }
//    }
    @Override
    public void featureSelectionChanged(final FeatureCollectionEvent fce) {
        if (log.isDebugEnabled()) {
            log.debug("FeatureSelection Changed");
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
                    if (log.isDebugEnabled()) {
                        log.debug("In edit modus, mindestens ein feature selectiert: " + curFeature);
                    }
                    cmdCopyFlaeche.setEnabled(true);
                    break;
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("disable copy nicht alle vorraussetzungen erfüllt");
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
    private static final class FeatureGroupActionListener implements ActionListener {

        //~ Instance fields ----------------------------------------------------

        private final JButton button;
        private final MappingComponent mapComp;
        private final String featureGroup;
        private final String visibleText;
        private final String invisibText;
        private final Icon visibleIcon;
        private final Icon invisibleIcon;
        private boolean isVisible;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new FeatureGroupActionListener object.
         *
         * @param  button        DOCUMENT ME!
         * @param  mapComp       DOCUMENT ME!
         * @param  featureGroup  DOCUMENT ME!
         * @param  tooltipText   DOCUMENT ME!
         * @param  icon          DOCUMENT ME!
         * @param  invisIcon     DOCUMENT ME!
         */
        public FeatureGroupActionListener(final JButton button,
                final MappingComponent mapComp,
                final String featureGroup,
                final String tooltipText,
                final String icon,
                final String invisIcon) {
            this.button = button;
            this.mapComp = mapComp;
            this.featureGroup = featureGroup;
            this.visibleText = tooltipText + " ausblenden";
            this.invisibText = tooltipText + " einblenden";
            this.visibleIcon = new ImageIcon(getClass().getResource(icon));
            this.invisibleIcon = new ImageIcon(getClass().getResource(invisIcon));
            this.isVisible = true;

            this.button.setToolTipText(visibleText);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  isVisible  DOCUMENT ME!
         */
        public void setVisible(final boolean isVisible) {
            if (isVisible) {
                this.button.setIcon(this.visibleIcon);
                this.button.setToolTipText(this.visibleText);
            } else {
                this.button.setIcon(this.invisibleIcon);
                this.button.setToolTipText(this.invisibText);
            }

            this.isVisible = isVisible;
            this.mapComp.setGroupLayerVisibility(this.featureGroup, this.isVisible);
        }

        @Override
        public void actionPerformed(final ActionEvent ae) {
            this.setVisible(!this.isVisible);
        }
    }
}
