
package de.cismet.lagis.layout.model;

import de.cismet.lagis.layout.actionprovider.FlurstueckSelectProvider;
import de.cismet.lagis.layout.actionprovider.FlurstueckWidgetEditProvider;
import de.cismet.lagis.layout.widget.AbstractFlurstueckNodePanel;
import de.cismet.lagis.layout.widget.CurvedConnectionWidget;
import de.cismet.lagis.layout.widget.FlurstueckHistoryWidget;
import de.cismet.lagis.layout.widget.FlurstueckNodePanel;
import de.cismet.lagis.layout.widget.PseudoFlurstueckPanel;
import de.cismet.lagisEE.entity.core.Flurstueck;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import org.apache.log4j.Logger;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 * This class implements the abstract GraphScene class of the NetBeans Visual API.
 * It is used as a controller for the graphical representation of the scene (MVC pattern).
 * 
 * HistoryPanelModel receives Flurstuecke and Edges between Flurstuecken and 
 * generates the representing NetBeans Visual Widgets for each of the provided
 * datatypes. 
 * 
 * In Addition it sets some default behaviour such as the selection behaviour and the
 * edit behaviour of FlurstueckHistoryWidgets.
 *
 * A instance of this scene creates 3 glasspane like layers on which the information
 * is displayed. One for the nodes themself, one for the edges and a background layer for
 * highlight widgets.
 *
 * @author mbrill
 */
public class HistoryPanelModel extends GraphScene<Flurstueck, HistoryPanelEdge> {

    //--------------------------------------------------------------------------
    //      Attributes
    //--------------------------------------------------------------------------

    /**
     * Widget containing the nodes of the scene
     */
    private LayerWidget nodeLayer;

    /**
     * Widget containing the edges of the scene
     */
    private LayerWidget connectionLayer;

    /**
     * Widget containing the highlight Widget(s)
     */
    private LayerWidget highlightLayer;

    /**
     * There is only one select provider in the scene which is attached to each
     * node and the scene itself. This makes sure that there is only one selected
     * node at time and that the selection can be discarded
     */
    private FlurstueckSelectProvider selectProvider;

    /**
     * Action for selection
     */
    private final WidgetAction selectAction;

    /**
     * edit action (double click for NBV) causes the lagis system to change the
     * current flurstueck
     */
    private final WidgetAction editAction = ActionFactory.createEditAction(new FlurstueckWidgetEditProvider());
    Logger log;

    /**
     * The default constructor generates the GraphScene instance used
     * to display a history graph. <br />
     * <p>
     * The GraphScene is organised in multiple layers which behave quiet equal
     * to a swing glasspane. On the node layer, the widgets representing Flurstuecke
     * are displayed while the connection layer contains the connection widgets
     * representing links between FlurstueckHistoryWidgets.
     * </p>
     * <p>
     * The constructor also initializes the behaviour of the scene itself. That
     * is a (not yet functional) zomm function, a pan function and the default hover
     * behaviour of ConnectionWidgets (highlight).
     * </p>
     */
    public HistoryPanelModel() {

        log = Logger.getLogger(this.getClass());

        setBackground(Color.WHITE);

        nodeLayer = new LayerWidget(this);
        connectionLayer = new LayerWidget(this);
        highlightLayer = new LayerWidget(this);

        addChild(nodeLayer);
        addChild(connectionLayer);
        addChild(highlightLayer);

        highlightLayer.bringToBack();

//        getActions().addAction(ActionFactory.createMouseCenteredZoomAction(1.1));
        getActions().addAction(ActionFactory.createPanAction());

        selectProvider = new FlurstueckSelectProvider(this);
        selectAction = ActionFactory.createSelectAction(selectProvider);

        getActions().addAction(selectAction);

        this.revalidate(true);

    }

    /**
     * This method is called from within the GraphScene class when a new 
     * node is added. It constructs a {@link AbstractFlurstueckNodePanel} 
     * depending on the Flurstueck given ({@link FlurstueckHistoryWidget} 
     * for a standard Flurstueck and {@link PseudoFlurstueckPanel} for 
     * Pseuo Flurstuecke) and adds it to the node layer.
     *
     * @param node The Flurstueck to be represented
     * @return The Widget representing the given Flurstueck
     */
    @Override
    protected Widget attachNodeWidget(Flurstueck node) {

        if (highlightLayer.getChildren().size() != 0) {
            highlightLayer.removeChildren();
        }

        AbstractFlurstueckNodePanel nodePanel = null;

        if (node.getFlurstueckSchluessel().getKeyString().contains("pseudo")) {
            nodePanel = new PseudoFlurstueckPanel(node);
        } else {
            nodePanel = new FlurstueckNodePanel(node);
        }

        FlurstueckHistoryWidget nodeWidget = new FlurstueckHistoryWidget(this, nodePanel);

        nodeWidget.getActions().addAction(selectAction);
        nodeWidget.getActions().addAction(editAction);

        nodeLayer.addChild(nodeWidget);

        return nodeWidget;
    }

    /**
     * This method is called from within the GraphScene class when a new 
     * edge is added. It constructs an instance of {@link CurvedConnectionWidget}
     * for every edge that must be displayed.
     *
     * @param edge The edge to be represented
     * @return The widget representing the edge
     */
    @Override
    protected Widget attachEdgeWidget(HistoryPanelEdge edge) {

        ConnectionWidget edgeWidget = new CurvedConnectionWidget(this,
                CurvedConnectionWidget.SET_SCURVE_CTRLPTS);

        edgeWidget.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);

        WidgetAction.Chain actions = edgeWidget.getActions();
        actions.addAction(createObjectHoverAction());
        actions.addAction(createSelectAction());
        connectionLayer.addChild(edgeWidget);

        return edgeWidget;
    }

    /**
     * This method is called from within the GraphScene class when a new
     * edge is added. It sets the default shape and position for the startpoint
     * of a ConnectionWidget.
     * @param edge
     * @param oldSourceNode
     * @param sourceNode
     */
    @Override
    protected void attachEdgeSourceAnchor(HistoryPanelEdge edge, Flurstueck oldSourceNode,
            Flurstueck sourceNode) {

        log.debug("attachEdgeSourceAnchor called : edge : " + edge + " oldSource : " + oldSourceNode + " sourceNode : " + sourceNode);

        ConnectionWidget edgeWidget = (ConnectionWidget) findWidget(edge);

        Widget sourceNodeWidget = findWidget(sourceNode);
        if (sourceNodeWidget == null) {
            log.warn("Edge " + edge + " : sourceNodeWidget null");
        }

        Anchor sourceAnchor = AnchorFactory.createDirectionalAnchor(sourceNodeWidget,
                AnchorFactory.DirectionalAnchorKind.VERTICAL);


        edgeWidget.setSourceAnchor(sourceAnchor);
        if (sourceAnchor == null) {
            log.warn("Edge " + edge + " : Source Anchor null");
        }


    }

    /**
     * This method is called from within the GraphScene class when a new
     * edge is added. It sets the default shape and position for the endpoint
     * of a ConnectionWidget.
     * @param edge
     * @param oldTargetNode
     * @param targetNode
     */
    @Override
    protected void attachEdgeTargetAnchor(HistoryPanelEdge edge, Flurstueck oldTargetNode,
            Flurstueck targetNode) {

        log.debug("attachEdgeTargetAnchor called : edge : " + edge + " oldSource : " + oldTargetNode + " sourceNode : " + targetNode);

        ConnectionWidget edgeWidget = (ConnectionWidget) findWidget(edge);
        Widget targetNodeWidget = findWidget(targetNode);
        if (targetNodeWidget == null) {
            log.warn("Edge " + edge + " : targetNodeWidget null");
        }
        
        Anchor targetAnchor = AnchorFactory.createDirectionalAnchor(targetNodeWidget,
                AnchorFactory.DirectionalAnchorKind.VERTICAL);

        edgeWidget.setTargetAnchor(targetAnchor);
        if (targetAnchor == null) {
            log.warn("Edge " + edge + " : Target Anchor null");
        }



    }

    /**
     * Getter for the node layer.
     * @return LayerWidget
     */
    public LayerWidget getNodeLayer() {
        return nodeLayer;
    }

    /**
     * Getter for the selectProvider
     * @return FlurstueckSelectProvider
     */
    public FlurstueckSelectProvider getSelectProvider() {
        return selectProvider;
    }

    /**
     * Getter for the highlightLayer
     * @return LayerWidget
     */
    public LayerWidget getHighlightLayer() {
        return highlightLayer;
    }

    /**
     * Method is intended to calculate the total bounds of a graph. This could
     * be useful to perform move operations on the scene view, e.g. to center the
     * graph.
     */
    public void shiftViewToContentBounds() {

        log.info("calculating total bounds for scene");
        log.info("There are " + nodeLayer.getChildren().size() + " nodes");
        Rectangle2D totalBounds = new Rectangle().getBounds2D();

        for (Widget widget : nodeLayer.getChildren()) {
            totalBounds = totalBounds.createUnion(widget.getPreferredBounds().getBounds2D());
        }

        log.info("Total scene content bounds:" + totalBounds);
    }
}
