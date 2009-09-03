/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.layout.model;

import de.cismet.lagis.layout.actionprovider.FlurstueckSelectProvider;
import de.cismet.lagis.layout.actionprovider.FlurstueckWidgetEditProvider;
import de.cismet.lagis.layout.actionprovider.FlurstueckWidgetHoverProvider;
import de.cismet.lagis.layout.widget.AbstractFlurstueckNodePanel;
import de.cismet.lagis.layout.widget.CurvedConnectionWidget;
import de.cismet.lagis.layout.widget.FlurstueckHistoryWidget;
import de.cismet.lagis.layout.widget.FlurstueckNodePanel;
import de.cismet.lagis.layout.widget.PseudoFlurstueckPanel;
import de.cismet.lagisEE.entity.core.Flurstueck;
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
 * It is used as a datamodel for the graphical representation of the scene. 
 * 
 * HistoryPanelModel receives Flurstuecke and Edges between Flurstuecken and 
 * generates the representing NetBeans Visual Widgets for each of the provided
 * datatypes. 
 * 
 * In Addition it sets some default behaviour such as the hover behaviour and the 
 * edit behaviour of FlurstueckHistoryWidgets.
 *
 * @author mbrill
 */
public class HistoryPanelModel extends GraphScene<Flurstueck, HistoryPanelEdge> {


    //--------------------------------------------------------------------------
    //      Attributes
    //--------------------------------------------------------------------------

    private boolean backgroundSet = false;
    private LayerWidget nodeLayer;
    private LayerWidget connectionLayer;
    private FlurstueckSelectProvider selectProvider;
    private final WidgetAction selectAction;
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

        nodeLayer = new LayerWidget(this);
        connectionLayer = new LayerWidget(this);
//        overlayLayer = new LayerWidget(this);

        addChild(nodeLayer);
        addChild(connectionLayer);
//        addChild(overlayLayer);

//        getActions().addAction(ActionFactory.createMouseCenteredZoomAction(1.1));
        getActions().addAction(ActionFactory.createPanAction());

        selectProvider = new FlurstueckSelectProvider(this);
        selectAction = ActionFactory.createSelectAction(selectProvider);

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

        AbstractFlurstueckNodePanel nodePanel = null;

        if (node.getFlurstueckSchluessel().getKeyString().contains("pseudo")) {
            nodePanel = new PseudoFlurstueckPanel(node);
        } else {
            nodePanel = new FlurstueckNodePanel(node);

            if (!backgroundSet) {
                setBackground(nodePanel.getBackground());
                backgroundSet = true;
            }
        }

        FlurstueckHistoryWidget nodeWidget = new FlurstueckHistoryWidget(this, nodePanel);

//        WidgetAction hoverAction = ActionFactory.createHoverAction(
//                new FlurstueckWidgetHoverProvider(this, nodeWidget));
//        getActions().addAction(hoverAction);

        nodeWidget.getActions().addAction(selectAction);
        nodeWidget.getActions().addAction(editAction);
//        nodeWidget.getActions().addAction(hoverAction);
        
        nodeLayer.addChild(nodeWidget);
        nodeWidget.revalidate();

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
        Anchor targetAnchor = AnchorFactory.createRectangularAnchor(targetNodeWidget, false);

        edgeWidget.setTargetAnchor(targetAnchor);
        if (targetAnchor == null) {
            log.warn("Edge " + edge + " : Target Anchor null");
        }

        

    }

    public LayerWidget getNodeLayer() {
        return nodeLayer;
    }

    public FlurstueckSelectProvider getSelectProvider() {
        return selectProvider;
    }
}
