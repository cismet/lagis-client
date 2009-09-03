/*
 * Version with only 1 barycenter per node
 */
package de.cismet.lagis.layout;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.graph.layout.GraphLayout;
import org.netbeans.api.visual.graph.layout.UniversalGraph;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author mbrill
 */
public class SugiyamaLayout<N, E> extends GraphLayout {

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private UniversalGraph<N, E> graph;
    private GraphScene<N, E> scene;
    private final ArrayList<ArrayList<SugiyamaNode<N>>> layers;
    private final int topOffset = 20;
    private int hgap = 30;
    private Integer vgap = null;
    private int maximumVSize;
    private double dummySizeVertical = 50.0;
    private double dummySizeHorizontal = 50.0;
    private HashMap<N, SugiyamaNode<N>> nodeMapping;
    private HashMap<E, SugiyamaEdge<E>> edgeMapping;
    private HashMap<SugiyamaEdge<E>, ArrayList<SugiyamaNode<N>>> dummyMapping;
    private HashMap<E, SugiyamaEdge<E>> longEdgeMapping;
    private ArrayList<SugiyamaEdge<E>> cycleEdges;
    private static int dfsTime = 0;
    private int iterations = 10;
    private boolean horizontal = false;

    private enum DFSNodeColor {

        white, grey, black
    }
    // Statistics
    private long graphCreationTime;
    private long layerAssignmentCompleteTime;
    private long dfsCalcTime;
    private long optimizeLayerTime;
    private long assignNodesToLayerTime;
    private long assignCoordinatesTime;
    private long timeAll;
    private long insertDummyTime;
    private long crossingMinimzationTime;

    /**
     * Constructor generates a new Instance of this layout algorithm
     * @param scene the GraphScene object, which represents the graph after it was
     * layed out
     */
    public SugiyamaLayout(GraphScene<N, E> scene) {

        this.scene = scene;
        layers = new ArrayList<ArrayList<SugiyamaNode<N>>>();
        cycleEdges = new ArrayList<SugiyamaEdge<E>>();

        nodeMapping = new HashMap<N, SugiyamaNode<N>>();
        edgeMapping = new HashMap<E, SugiyamaEdge<E>>();
        longEdgeMapping = new HashMap<E, SugiyamaEdge<E>>();
        dummyMapping = new HashMap<SugiyamaEdge<E>, ArrayList<SugiyamaNode<N>>>();
    }

    /**
     * Implements and performs particular graph-oriented algorithm
     * of a UniversalGraph. Call GraphLayout.setResolvedNodeLocation method for
     * setting the resolved node location.
     * @param graph the universal graph on which the layout should be performed
     */
    @Override
    protected void performGraphLayout(UniversalGraph graph) {
        log.info("Performing Graph Layout");

        timeAll = System.currentTimeMillis();
        clear();
        this.graph = graph;
        this.scene = (GraphScene<N, E>) graph.getScene();


//        graphCreationTime = System.currentTimeMillis();
        buildDatastructure(graph);
//        graphCreationTime = System.currentTimeMillis() - graphCreationTime;

//        layerAssignmentCompleteTime = System.currentTimeMillis();
        assignLayers();
//        layerAssignmentCompleteTime = System.currentTimeMillis() - layerAssignmentCompleteTime;

//        insertDummyTime = System.currentTimeMillis();
        dummyMapping = insertDummies();
//        insertDummyTime = System.currentTimeMillis() - insertDummyTime;

//        crossingMinimzationTime = System.currentTimeMillis();
        minimizeCrossings();
//        crossingMinimzationTime = System.currentTimeMillis() - crossingMinimzationTime;

//        assignCoordinatesTime = System.currentTimeMillis();
        assignCoordinates(50, false);
//        assignCoordinatesTime = System.currentTimeMillis() - assignCoordinatesTime;

//
//        timeAll = System.currentTimeMillis() - timeAll;
//

//        System.out.println("Layout statistcs");
//        System.out.println("=======================");
//        System.out.println("Build datastructure time : " + graphCreationTime + " msec");
//        System.out.println("Layer assignment time    : " + layerAssignmentCompleteTime + " msec");
//        System.out.println("   DFS time              : " + dfsCalcTime + " nano sec");
//        System.out.println("   Layer assignment      : " + assignNodesToLayerTime + " msec");
//        System.out.println("   Optimization          : " + optimizeLayerTime + " msec");
//        System.out.println("Assign coordinates time  : " + assignCoordinatesTime + " msec");
//        System.out.println("Has long edges           : " + longEdgeMapping.size());
//        System.out.println("Insert dummy time        : " + insertDummyTime + " msec");
//        System.out.println("Time for crossing minimization : " + crossingMinimzationTime + " msec");
//        System.out.println("Time over all : " + timeAll + " msec");
//        System.out.println("Dummy nodes on layers : ");
//        int layer = 0;
//        int dummies = 0;
//        for (ArrayList<SugiyamaNode<N>> arrayList : layers) {
//            System.out.print("Layer " + layer++ + " : ");
//            for (SugiyamaNode<N> sugiyamaNode : arrayList) {
//                if (sugiyamaNode.dummy) {
//                    dummies++;
//                }
//            }
//            System.out.println(dummies);
//            dummies = 0;
//        }

        log.info("Graph Layout done");
    }

    /**
     * Implements and performs particular location resolution of a collection
     * of nodes in a UniversalGraph. Call GraphLayout.setResolvedNodeLocation
     * method for setting the resolved node location.
     * @param graph the universal graph on which the nodes should be resolved
     * @param nodes the collection of nodes to be resolved
     */
    @Override
    protected void performNodesLayout(UniversalGraph graph, Collection nodes) {
        performGraphLayout(graph);
    }

    /**
     * <p>
     * This Method is called prior to a new layout calculation to empty
     * all datastructures and provide a clean state for this class.
     * </p>
     * Datastructures that are cleaned by this method are :
     * <ul>
     *  <li>layers is cleared </li>
     *  <li>nodeMapping is cleaned by creating a new HashMap</li>
     *  <li>edgeMapping is cleaned by creating a new HashMap</li>
     *  <li>cycleEdges is cleaned by creating a new HashMap</li>
     *  <li>nodeMapping is cleaned by creating a new HashMap</li>
     *  <li>edgeMapping is cleaned by creating a new HashMap</li>
     *  <li>longEdgeMapping is cleaned by creating a new HashMap</li>
     *  <li>dummyMapping is cleaned by creating a new HashMap</li>
     *  <li>dfsTime is set to 0</li>
     * </ul>
     *
     */
    private void clear() {

        layers.clear();
        nodeMapping = new HashMap<N, SugiyamaNode<N>>();
        edgeMapping = new HashMap<E, SugiyamaEdge<E>>();

        cycleEdges = new ArrayList<SugiyamaEdge<E>>();

        nodeMapping = new HashMap<N, SugiyamaNode<N>>();
        edgeMapping = new HashMap<E, SugiyamaEdge<E>>();
        longEdgeMapping = new HashMap<E, SugiyamaEdge<E>>();
        dummyMapping = new HashMap<SugiyamaEdge<E>, ArrayList<SugiyamaNode<N>>>();

        dfsTime = 0;
    }

    //==========================================================================
    //      Sugiyama algorithm part
    //==========================================================================
    // ---------------------------------------------------
    //      Normalisation
    // ---------------------------------------------------
    /**
     * <p>
     *  This method ensures that the graph is a so called DAG, a directed, acyclic
     * graph. The directed property is considered given. Therefor the method ensures
     * the graph is also acyclic. This is done by reversing edges which would
     * result in a cycle. When the graph is drawn, the previously reversed edges
     * are switched back, so that the original graph can be drawn.
     * </p>
     */
    private void eliminateCycles() {
        // Since the current problem does not need to deal with cycled graphs, this method
        // will be implemented if I have time
    }

    // ---------------------------------------------------
    //      Initial construction of datastructure and Layer assignment
    // ---------------------------------------------------
    /**
     * <p>
     * In this method all steps are performed to sort the nodes in a
     * hierarchical order. The following operations are initialized by this
     * method :
     * </p>
     * <p>
     * <ol>
     *  <li>Put all source nodes initially on the top layer</li>
     *  <li>Perform a depth-first search of the graph (done by {@link #dfs()})</li>
     *  <li>Sort the nodes topologically (done by {@link #sortTopologically()})</li>
     *  <li>Assign the remaining nodes to Layers according to their  position
     *  in the topologically sorted list done by ({@link #assignLayerToNodes})</li>
     *  <li>Optimize the Node position to reduce the number of so called
     *  long edges (done by {@link #optimizeNodeLayer()}). Long edges are those
     *  which span more than one layer.</li>
     * </ol>
     * </p>
     */
    private void assignLayers() {

        layers.add(new ArrayList<SugiyamaNode<N>>());

        // Step 1 : Put all source nodes on the first level
        for (SugiyamaNode<N> node : nodeMapping.values()) {

            if (node.getPredecessorList().size() == 0) {
                layers.get(0).add(node);
                node.setLayer(0);
            }
        }

        // Step 2 : perform Depth-first search of the graph

//        dfsCalcTime = System.nanoTime();
        dfs();
//        dfsCalcTime = System.nanoTime() - dfsCalcTime;

        // Step 3 : calculate topological Sorting by dfs algorithm
        ArrayList<SugiyamaNode<N>> topSort = sortTopologically();

        // Step 4 : Sort nodes to layers according to topological sorting
//        assignNodesToLayerTime = System.currentTimeMillis();
        assignLayerToNodes(topSort);
//        assignNodesToLayerTime = System.currentTimeMillis() - assignNodesToLayerTime;

        // Step 5 : Optimize node position concerning edge length
//        optimizeLayerTime = System.currentTimeMillis();
        optimizeNodeLayer();
//        optimizeLayerTime = System.currentTimeMillis() - optimizeLayerTime;

    }

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //      Depth first search
    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * The dfs() Method performs a depth-first search of the graph.
     * <p>
     * The original DFS Algorithm as described e.g. in "Introduction to Algorithms"
     * by Cormen et al. iterates over every node in the graph and tries to
     * descent at every iteration. This is nesseccary since a graph usually contains
     * cycles.
     * </p>
     * <p>
     * Because this class deals with DAG's, there are dedicated source nodes which
     * can be used as starting nodes for the dfs algorithm. Since any source node
     * is initially placed on the top level layer, this version of dfs only
     * iterates through the nodes available on the top layer.
     * </p>
     */
    private void dfs() {

        // perform dfs for each root node and build a topologically
        // sorted list (variable topoList)
        for (SugiyamaNode<N> sugiyamaNode : layers.get(0)) {

            // if the node has not been visited yet, visit
            if (sugiyamaNode.getColor().equals(DFSNodeColor.white)) {
                visitNode(sugiyamaNode);
            }
        }
    }

    /**
     * <p>
     * This method represents the recursive part of the dfs algorithm. Whenever
     * a node is visited, the node is marked visited and the method is called
     * recursivly for every direct successor of the specific node. When there are
     * no more successors to recurse the node is marked finished and a finishing
     * time is set.
     * </p>
     * @param node The graph node which is visited
     */
    private void visitNode(SugiyamaNode<N> node) {
        node.setColor(DFSNodeColor.grey);
        node.setDiscoveryTime(++dfsTime);
        ArrayList<SugiyamaNode<N>> successors = node.getSuccessorList();
        int succSize = successors.size();

        for (int i = 0; i < succSize; i++) {
            SugiyamaNode<N> succ = successors.get(i);
            if (succ.getColor() == DFSNodeColor.white) {
                succ.setDfsPredecessor(node);
                visitNode(succ);
            }
        }

        node.setColor(DFSNodeColor.black);
        node.setFinishingTime(++dfsTime);
    }

    //  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * <p>
     * To get a node order which represents the hierarchical organization of
     * the graph, the nodes are sorted topologically by their finishing time
     * calculated in {@link #dfs() }.
     * </p>
     * <p>
     * A topologically sorted graph has only edges in one direction.Nodes are
     * put in the list in the order of their weight in the hierarchie. Therefor
     * the sorted list provides a good basis for the layer assignement.
     * </p>
     * <p>
     * The fact, that edges have only one direction in a topologically sorted
     * graph enables this method to easily search for cycles in a given graph.
     * If a nodes has an edge to a node the algorithm already visited during the
     * iteration, the edge produces a cycle. <br />
     * In this case, the edge is reversed for the datastructure that performs the
     * layout. The division of layout datastructure(this class)
     * and display datastructure (the GraphScene object used) makes it possible
     * to reverse edges without the need to re- reverse them when it comes to
     * rendering.
     * </p>
     */
    private ArrayList<SugiyamaNode<N>> sortTopologically() {

        ArrayList<SugiyamaNode<N>> topSort = new ArrayList<SugiyamaNode<N>>();

        // add all nodes to the Arraylist (O(n))
        for (SugiyamaNode<N> sugiyamaNode : nodeMapping.values()) {
            topSort.add(sugiyamaNode);
        }

        // sort (O(n log n))
        Collections.sort(topSort, new TopologyComparator());

        // reverse the direction of back edges
        ArrayList<SugiyamaNode<N>> temp = new ArrayList<SugiyamaNode<N>>(topSort);
        temp.remove(temp.size() - 1);

        for (int i = topSort.size() - 1; i > 0; i--) {
            SugiyamaNode<N> n = topSort.get(i);

            ArrayList<SugiyamaNode<N>> succs = n.getSuccessorList();
            for (int j = 0; j < succs.size(); j++) {
                SugiyamaNode<N> succ = succs.get(j);

                if (temp.contains(succ)) {
                    SugiyamaEdge e = null;

                    for (SugiyamaEdge<E> sugiyamaEdge : edgeMapping.values()) {
                        if (sugiyamaEdge.getFrom().equals(n) &&
                                sugiyamaEdge.getTo().equals(succ)) {

                            e = sugiyamaEdge;
                            break;
                        }
                    }

                    if (e == null) {
                        // TODO proper logging
                    } else {
                        cycleEdges.add(e);

                        e.getFrom().removeSuccessor(e.getTo());
                        e.getFrom().addPredecessor(e.getTo());

                        e.getTo().removePredecessor(e.getFrom());
                        e.getTo().addSuccessor(e.getFrom());

                        SugiyamaNode<N> t = e.getFrom();
                        e.setFrom(e.getTo());
                        e.setTo(t);
                    }
                }
            }

            temp.remove(i - 1);
        }

        return topSort;
    }

    /**
     * Within this methode, a datastructure representing the graph given by the
     * performLayout() methods is build. For each node in the original graph a
     * SugiyamaNode is constructed which handles information for the algorithm.
     * SugiyamaNodes also handle edge information by a predecessor and successor
     * field.
     * @param graph the universal graph on which the layout should be performed
     */
    private void buildDatastructure(UniversalGraph<N, E> graph) {

        // get all source nodes
        for (N node : graph.getNodes()) {
            nodeMapping.put(node, new SugiyamaNode<N>(node));
        }

        // get all edges and fill the predecessor / successor lists of the nodes
        // created previously
        for (E edge : graph.getEdges()) {

            SugiyamaNode<N> from = nodeMapping.get(graph.getEdgeSource(edge));
            SugiyamaNode<N> to = nodeMapping.get(graph.getEdgeTarget(edge));

            SugiyamaEdge<E> sugiEdge = new SugiyamaEdge<E>(edge, from, to);

            // reference pred. and succ. in SugiyamaNodes
            from.addSuccessor(to);
            to.addPredecessor(from);

            // reference edge in both nodes
            from.addReferencedEdge(sugiEdge);
            to.addReferencedEdge(sugiEdge);

            edgeMapping.put(edge, sugiEdge);
        }

    }

    /**
     * While any node without in-edges was already placed on the top level layer,
     * this methode assigns a layer to each node left. ({@link #assignLayers()})
     * <p>
     * At first the method calculates the bounds of the node widget which is layered
     * in the graph. <br />
     * After that, each node in the topSort list is assigned to a layer by
     * {@link #assignLayerToNode(test.SugiyamaLayout.SugiyamaNode) }. Since topSort
     * is sorted topologically, each time a node is assigned to a layer, one can be
     * sure, that all nodes comming before the current node in the hierarchy, the nodes
     * the current node depends on, have
     * already been assigned to a layer in a previous iteration.
     * </p>
     * @param topSort a topologically sorted list containing all nodes for which
     * no layer has been assigned yet.
     */
    private void assignLayerToNodes(ArrayList<SugiyamaNode<N>> topSort) {

        double maxY = 0.0;
        double maxX = 0.0;

        // calculate ideal XY Size from the bounds of the widgets and store the
        // value in the corresponding Sugiyama Node. Additionally the max bounds
        // are calculated
        for (SugiyamaNode<N> node : nodeMapping.values()) {
            Widget w = scene.findWidget(node.getValue());
            Rectangle rec = w.getPreferredBounds();
            if (rec != null) {
                node.setHSize(rec.getHeight());
                node.setVSize(rec.getWidth());
                System.out.println(node.getValue().toString() + " bounds : " +
                        rec.getHeight() + " " + rec.getWidth());
            } else {
                rec = w.getBounds();
                node.setHSize(rec.getHeight());
                node.setVSize(rec.getWidth());
            }

            if (node.getHSize() > maxX) {
                maxX = node.getVSize();
            }

            if (node.getVSize() > maxY) {
                maxY = node.getHSize();
            }
        }

        // maximum size for a widget was calculated, set the gaps between layers
        // according to the maximal vertical size of a widget
        if (vgap == null) {
            if (maxY > 60.0 && maxY < 200) {
                this.vgap = (int) (maxY + maxY / 2.0);
            } else if (maxY > 200) {
                this.vgap = (int) (maxY + 100.0);
            } else {
                this.vgap = (int) (maxY + 30.0);
            }
        }
        this.maximumVSize = (int) maxY;

        // calculate the ideal layer for the specific node
        for (SugiyamaNode<N> sugiyamaNode : topSort) {

            if (sugiyamaNode.getLayer() != 0) {
                assignLayerToNode(sugiyamaNode);
            }
        }
    }

    /**
     * This method performs the actual insertion of nodes into the layer datastructure.
     * Therefor, <code>assignLayerToNode</code> checks the predecessor list of the
     * node given and calculates the minimum depth the node must have in the graph.
     * That is the depth of the deepest predecessor plus one. <br />
     * If there are not enough layers available in the datastructure, the method
     * generates a new Layer.
     *
     * @param sugiyamaNode The node which is assigned to a layer
     */
    private void assignLayerToNode(SugiyamaNode<N> sugiyamaNode) {

        int maxAncestorLayer = -1;

        if (!(sugiyamaNode.getPredecessorList().isEmpty())) {
            for (SugiyamaNode<N> pred : sugiyamaNode.getPredecessorList()) {
                if (pred.getLayer() > maxAncestorLayer) {
                    maxAncestorLayer = pred.getLayer();
                }
            }
        }

        sugiyamaNode.setLayer(maxAncestorLayer + 1);

        if (layers.size() <= maxAncestorLayer + 1) {

            // calculate additional layers needed
            int diff = maxAncestorLayer + 1 - layers.size();

            for (int i = 0; i <= diff; i++) {
                layers.add(new ArrayList<SugiyamaNode<N>>());
            }
        }

        // assign node to layer
        layers.get(maxAncestorLayer + 1).add(sugiyamaNode);
    }

    /**
     * <p>
     * Putting all nodes without in-edges on the first layer solves the question
     * about how to start in assigning layers. The problem is, that by this approach,
     * one slips up concerning the correct position of a node in the layer hierarchy.
     *</p>
     * <p>
     * A root node should not be placed on the top layer if its first child is
     * for example positioned on layer 5. This produces so called long edges, which
     * are not neccessary and effectively beat down performance since any long edge has
     * to be replayced by dummy node trails.
     *</p>
     * <p>
     * Therefor this method fixes issues of uneccessary long edges. The method
     * is based upon the DFS algorithm and recurres down the graph until it finds
     * a leaf node. After that the algorithm returns to the parent of the leaf providing its
     * depth. The depth of all leaf nodes is evaluated and compared to the depth
     * of the parent node. If the parent node is placed on a higher level than
     * the highest child node plus one, the algorithm has found a long edge which
     * can be resolved by pulling the parent node down.
     * </p>
     * <p>
     * As to say, the algorithm tears any node down to its maximum layer position,
     * in a bottom up manner. This makes shure, that it doesn't produce further
     * long edges.
     * </p>
     */
    private void optimizeNodeLayer() {

        // optimization using a recursive algorithm

        HashMap<SugiyamaNode<N>, Integer> nodeToNewLayer =
                new HashMap<SugiyamaNode<N>, Integer>();


        for (int i = 0; i < layers.get(0).size(); i++) {
            int newLayer = optimize(layers.get(0).get(i), nodeToNewLayer);
            if (newLayer != 0) {
                i--;
            }
        }

        for (SugiyamaNode<N> node : nodeToNewLayer.keySet()) {
            int newLayer = nodeToNewLayer.get(node);

            layers.get(node.getLayer()).remove(node);
            node.setLayer(newLayer);
            layers.get(node.getLayer()).add(node);
        }
    }

    /**
     * Equal to the DFS algorithm, this method represents the recursive part of the
     * "tear down" algorithm. It can be compared to the <code>visit</code> method
     * of DFS.
     *
     * @param node The node to visit.
     * @return The layer of the node visited.
     */
    private int optimize(SugiyamaNode<N> node,
            HashMap<SugiyamaNode<N>, Integer> nodeToNewLayer) {

        int minimumSuccLevel = Integer.MAX_VALUE;
        ArrayList<SugiyamaNode<N>> succList = node.getSuccessorList();
        int succListSize = succList.size();

        if (succListSize == 0) {
            minimumSuccLevel = node.getLayer();
        } else {

            for (int i = 0; i < succListSize; i++) {

                int l = optimize(succList.get(i), nodeToNewLayer);

                if (l < minimumSuccLevel) {
                    minimumSuccLevel = l;

                }
            }

            if ((minimumSuccLevel - 1) > node.getLayer()) {

                nodeToNewLayer.put(node, minimumSuccLevel - 1);
            }
        }

        return node.getLayer();
    }

    // ---------------------------------------------------
    //      Dummy Node insertion
    // ---------------------------------------------------
    /**
     * In order to perform the crossing minimizaton step of the Sugiyama Algorithm,
     * a Graph has to be "proper" in the therms of Sugiyama. A proper directed
     * acyclic Graph is one that has only edges from one layer to next. I.E. a
     * proper graph has no long edges.
     *
     * This is ensured by <conde>insertDummies</code>. Every long edge discovered by
     * {@link #checkLongEdges() } is replaced by a list of dummy nodes which mark the
     * path of the long edge. A dummy node is placed on every layer that is spanned
     * by the long edge.
     * This "edge mapping" is stored in a HashMap for later use.
     *
     * @return HashMap mapping a long edge with the replacing dummy node path.
     */
    private HashMap<SugiyamaEdge<E>, ArrayList<SugiyamaNode<N>>> insertDummies() {

        checkLongEdges();

        HashMap<SugiyamaEdge<E>, ArrayList<SugiyamaNode<N>>> localDummyMapping =
                new HashMap<SugiyamaEdge<E>, ArrayList<SugiyamaNode<N>>>();

        for (SugiyamaEdge<E> edge : longEdgeMapping.values()) {

            ArrayList<SugiyamaNode<N>> edgeReplacement = new ArrayList<SugiyamaNode<N>>();

            int fromLayer = edge.getFrom().getLayer();
            int toLayer = edge.getTo().getLayer();

            edge.getFrom().removeSuccessor(edge.getTo());
            edge.getTo().removePredecessor(edge.getFrom());

            SugiyamaNode lastDummy = new SugiyamaNode(null, edge.getFrom(), null);
            edge.getFrom().addSuccessor(lastDummy);
            int dummyLayer = fromLayer + 1;
            lastDummy.setLayer(dummyLayer);
            edgeReplacement.add(lastDummy);
            layers.get(dummyLayer).add(lastDummy);

            // calculate number of dummy nodes to insert for this edge
            int noDummies = toLayer - 1 - fromLayer;

            // one dummy is already there
            for (int i = 1; (i < noDummies); i++) {
                dummyLayer++;
                SugiyamaNode temp = new SugiyamaNode(null, lastDummy, null);
                temp.setLayer(dummyLayer);
                layers.get(dummyLayer).add(temp);
                lastDummy.addSuccessor(temp);
                lastDummy = temp;
                edgeReplacement.add(lastDummy);
            }

            lastDummy.addSuccessor(edge.getTo());
            edge.getTo().addPredecessor(lastDummy);
            localDummyMapping.put(edge, edgeReplacement);

        }


        return localDummyMapping;
    }

    /**
     * Simple check for long edges. If the gap between a node and one of its
     * children is greater than one layer, the edge that connects the two nodes
     * will be added to a hashMap that holds any long edges for further use.
     */
    private void checkLongEdges() {

        for (SugiyamaEdge<E> edge : edgeMapping.values()) {
            SugiyamaNode<N> from = edge.getFrom();
            SugiyamaNode<N> to = edge.getTo();

            if (from.getLayer() < to.getLayer() - 1) {
                longEdgeMapping.put(edge.getValue(), edge);
            }
        }
    }

    // ---------------------------------------------------
    //      Crossing minimization
    // ---------------------------------------------------
    /**
     * <code>minimizeCrossings</code> implements the crossing reduction algorithm
     * proposed by Kozo Sugiyama.
     * <p>
     * First the already layered Graph is converted to its matrix representation.
     * The matrix representation of a DAG is a set of two layer incedence matrices
     * the rows of each matrix represent the nodes on the first layer and the
     * columns represent the nodes of the second layer. The values of the matrix
     * are 0 or 1 and define wether there is a connection between two nodes or not :
     * <table border="1">
     *      <tr>
     *          <th></th>
     *          <th>node1</th>
     *          <th>node2</th>
     *          <th>node3</th>
     *      </tr>
     *
     *      <tr>
     *          <th>node4</th>
     *          <td>1</td>
     *          <td>0</td>
     *          <td>0</td>
     *      </tr>
     *
     *      <tr>
     *          <th>node5</th>
     *          <td>1</td>
     *          <td>1</td>
     *          <td>0</td>
     *      </tr>
     *
     *      <tr>
     *          <th>node6</th>
     *          <td>0</td>
     *          <td>0</td>
     *          <td>1</td>
     *      </tr>
     * </table>
     * 
     * From this representation a number of operations can be performed to get
     * additional information helping in crossing minimization. For further information 
     * refer to {@link TwoLayerIncidenceMatrix}.
     * </p>
     * <p>
     * After the matrix representation is calculated, the method performs the
     * algorithm steps several times until an iteration count has been reached :
     * <ul>
     *      <li>{@link #crossingMinimizationPhase1Down(java.util.ArrayList, int) }</li>
     *      <li>{@link #crossingMinimizationPhase1Up(java.util.ArrayList, int) }</li>
     *      <li>{@link #crossingMinimizationPhase2Down(java.util.ArrayList) }</li>
     *      <li>{@link #crossingMinimizationPhase2Up(java.util.ArrayList) }</li>
     * </ul>
     * </p>
     */
    private void minimizeCrossings() {

        ArrayList<TwoLayerIncidenceMatrix> matrixRep =
                new ArrayList<TwoLayerIncidenceMatrix>();

        int numberOfCrossings = 0;

        // build the matrix representation of the given graph
        for (int i = 0; i < layers.size() - 1; i++) {
            matrixRep.add(new TwoLayerIncidenceMatrix(layers.get(i),
                    layers.get(i + 1)));
        }

        // calculate initial overall number of crossings
//        for (TwoLayerIncidenceMatrix matrix : matrixRep) {
//            numberOfCrossings += matrix.calculateNumberOfCrossings();
//        }

        for (int i = 0; i < iterations; i++) {
            crossingMinimizationPhase1Down(matrixRep, 0);

            crossingMinimizationPhase2Down(matrixRep);

            crossingMinimizationPhase2Up(matrixRep);

            crossingMinimizationPhase1Up(matrixRep, layers.size() - 2);

            crossingMinimizationPhase1Down(matrixRep, 0);

        }


        // set the layers to the calculated ordering
        layers.set(0, matrixRep.get(0).getLayer1());

        int finalCrossings = 0;

        for (int i = 0; i < matrixRep.size(); i++) {
            layers.set(i + 1, matrixRep.get(i).getLayer2());
            finalCrossings += matrixRep.get(i).calculateNumberOfCrossings();
        }

//        System.out.println("Crossings : " + finalCrossings);
    }

    /**
     *
     * @param matrixRep current matrix representation of the graph
     * @param layer layer from which the method starts to sweep down
     * @return the number of crossings after the phase
     */
    private int crossingMinimizationPhase1Down(
            ArrayList<TwoLayerIncidenceMatrix> matrixRep, int layer) {



        for (int i = layer; i < matrixRep.size() - 1; i++) {
            matrixRep.get(i).BOC();
            matrixRep.get(i + 1).setLayer1(matrixRep.get(i).getLayer2());
        }

        matrixRep.get(matrixRep.size() - 1).BOC();

        int crossings = 0;

        for (TwoLayerIncidenceMatrix matrix : matrixRep) {
            crossings += matrix.calculateNumberOfCrossings();
        }

        return crossings;

    }

    /**
     *
     * @param matrixRep current matrix representation of the graph
     * @param layer layer from which the method starts to sweep up
     * @return the number of crossings after the phase
     */
    private int crossingMinimizationPhase1Up(
            ArrayList<TwoLayerIncidenceMatrix> matrixRep, int layer) {


        for (int i = layer; i > 0; i--) {

            matrixRep.get(i).BOR();
            matrixRep.get(i - 1).setLayer2(matrixRep.get(i).getLayer1());
        }

        matrixRep.get(0).BOR();

        int crossings = 0;

        for (TwoLayerIncidenceMatrix matrix : matrixRep) {
            crossings += matrix.calculateNumberOfCrossings();
        }

        return crossings;
    }

    /**
     *
     * @param matrixRep
     */
    private void crossingMinimizationPhase2Down(
            ArrayList<TwoLayerIncidenceMatrix> matrixRep) {

        for (int i = 0; i < matrixRep.size(); i++) {

            TwoLayerIncidenceMatrix matrix = matrixRep.get(i);

            matrix.ROR();


            if (!matrix.columnsInIncreaseOrder() || ((i > 0) &&
                    (!matrixRep.get(i - 1).columnsInIncreaseOrder()))) {

                ArrayList<TwoLayerIncidenceMatrix> tempRep =
                        new ArrayList<TwoLayerIncidenceMatrix>();

                int matrixCrossings = 0;

                for (TwoLayerIncidenceMatrix m : matrixRep) {
                    tempRep.add(new TwoLayerIncidenceMatrix(m.getLayer1(),
                            m.getLayer2()));
                    matrixCrossings += m.calculateNumberOfCrossings();
                }

                int tempCrossings = crossingMinimizationPhase1Down(tempRep, 0);

                if (tempCrossings < matrixCrossings) {
                    tempCrossings = crossingMinimizationPhase1Up(tempRep, tempRep.size() - 1);

                    if (tempCrossings < matrixCrossings) {
                        matrixRep = tempRep;
                    }
                }
            }
        }
    }

    /**
     *
     * @param matrixRep
     */
    private void crossingMinimizationPhase2Up(
            ArrayList<TwoLayerIncidenceMatrix> matrixRep) {

        for (int i = matrixRep.size() - 1; i >= 0; i--) {

            TwoLayerIncidenceMatrix matrix = matrixRep.get(i);

            matrix.ROC();

            if (!matrix.rowsInIncreaseOrder() || ((i < matrixRep.size() - 1) &&
                    (!matrixRep.get(i + 1).rowsInIncreaseOrder()))) {

                ArrayList<TwoLayerIncidenceMatrix> tempRep =
                        new ArrayList<TwoLayerIncidenceMatrix>();

                int matrixCrossings = 0;

                for (TwoLayerIncidenceMatrix m : matrixRep) {
                    tempRep.add(new TwoLayerIncidenceMatrix(m.getLayer1(),
                            m.getLayer2()));
                    matrixCrossings += m.calculateNumberOfCrossings();
                }

                int tempCrossings = crossingMinimizationPhase1Down(tempRep, tempRep.size() - 1);

                if (tempCrossings < matrixCrossings) {
                    tempCrossings = crossingMinimizationPhase1Up(tempRep, tempRep.size() - 1);

                    if (tempCrossings < matrixCrossings) {
                        matrixRep = tempRep;
                    }
                }
            }
        }
    }

    // ---------------------------------------------------
    //      Node Alignment
    // ---------------------------------------------------
    /**
     *
     * @param initialX
     * @param horizontal
     */
    private void assignCoordinates(int initialX, boolean horizontal) {

        // priority layout method by Sugiyama
        // initial position of vertices on layer Li :
        // Xik = initialX + k and
        // Yik = vgap * i;
        // the X coordinate must be constrained with the size of predeceeding nodes

        this.horizontal = horizontal;

        // initialize the layout by assigning fix y values and an initial x value
        for (ArrayList<SugiyamaNode<N>> layer : layers) {

            int filled = initialX;

            for (SugiyamaNode<N> node : layer) {

                if (horizontal) {
                    node.setYCoordinate(filled);
                    node.setXCoordinate(vgap * node.getLayer());
                } else {
                    node.setYCoordinate(vgap * node.getLayer() + topOffset);
                    node.setXCoordinate(filled);
                }

                if (node.getValue() != null) {
                    Widget w = scene.findWidget(node.getValue());

                    if (horizontal) {
                        w.setPreferredLocation(new Point(node.getYCoordinate(),
                                filled));
                        node.setVSize(Math.ceil(w.getBounds().getHeight()));
                        filled += hgap + (int) node.getVSize();
                    } else {
                        w.setPreferredLocation(new Point(filled,
                                node.getYCoordinate()));
                        node.setHSize(Math.ceil(w.getBounds().getWidth()));
                        filled += hgap + (int) node.getHSize();
                    }



                } else {
                    if (horizontal) {
                        filled += hgap + (int) node.getVSize();
                    } else {
                        filled += hgap + (int) node.getHSize();
                    }
                }
            }
        }

        downAssignment();
        upAssignment();
//        downAssignment();
//        upAssignment();


        // set newly calculated coordinates as preferredLocation values of widgets
        for (SugiyamaNode<N> node : nodeMapping.values()) {
            if (!node.isDummy()) {
                Widget w = scene.findWidget(node.getValue());

                w.setPreferredLocation(
                        new Point(node.getXCoordinate(), node.getYCoordinate()));

            }
        }

        // Add a control point trail to each long edge, so that these Edges
        // follow the positions of the Dummy Nodes that replaced the edge previously
        for (E key : longEdgeMapping.keySet()) {
            Widget w = graph.getScene().findWidget(longEdgeMapping.get(key).getValue());

            if (w instanceof ConnectionWidget) {
                ConnectionWidget cw = (ConnectionWidget) w;
                ArrayList<Point> controlPoints = new ArrayList<Point>();
                ArrayList<SugiyamaNode<N>> dummyTrail =
                        dummyMapping.get(longEdgeMapping.get(key));

                // if this edge was reversed previously, the order of dummy vertices
                // must be reversed as well
                if (cycleEdges.contains(longEdgeMapping.get(key))) {

                    SugiyamaNode n1 = dummyTrail.get(dummyTrail.size() - 1);
                    controlPoints.add(new Point(n1.getXCoordinate() +
                            (int) (n1.getHSize() / 2), n1.getYCoordinate() +
                            (int) n1.getVSize()));

                    for (int i = dummyTrail.size() - 2; i > 0; i--) {

                        SugiyamaNode<N> sugiyamaNode = dummyTrail.get(i);
                        if (horizontal) {
                            controlPoints.add(
                                    new Point(sugiyamaNode.getYCoordinate() +
                                    (int) sugiyamaNode.getVSize() / 2,
                                    sugiyamaNode.getXCoordinate() +
                                    (int) sugiyamaNode.getHSize() / 2));
                        } else {
                            controlPoints.add(new Point(sugiyamaNode.getXCoordinate() +
                                    (int) sugiyamaNode.getHSize() / 2,
                                    sugiyamaNode.getYCoordinate() +
                                    (int) sugiyamaNode.getVSize() / 2));
                        }
                    }

                    SugiyamaNode n2 = dummyTrail.get(0);
                    controlPoints.add(new Point(n2.getXCoordinate() +
                            (int) (n2.getHSize() / 2), n2.getYCoordinate() -
                            (int) n2.getVSize()));



                } else {
                    SugiyamaNode n1 = dummyTrail.get(0);
                    controlPoints.add(new Point(n1.getXCoordinate() +
                            (int) (n1.getHSize() / 2), n1.getYCoordinate() -
                            (int) n1.getVSize()));

                    for (int i = 1; i < dummyTrail.size() - 1; i++) {

                        SugiyamaNode<N> sugiyamaNode = dummyTrail.get(i);
                        if (horizontal) {
                            controlPoints.add(
                                    new Point(sugiyamaNode.getYCoordinate() +
                                    (int) sugiyamaNode.getVSize() / 2,
                                    sugiyamaNode.getXCoordinate() +
                                    (int) sugiyamaNode.getHSize() / 2));
                        } else {
                            controlPoints.add(new Point(sugiyamaNode.getXCoordinate() +
                                    (int) sugiyamaNode.getHSize() / 2,
                                    sugiyamaNode.getYCoordinate() +
                                    (int) sugiyamaNode.getVSize() / 2));
                        }
                    }

                    SugiyamaNode n2 = dummyTrail.get(dummyTrail.size() - 1);
                    controlPoints.add(new Point(n2.getXCoordinate() +
                            (int) (n2.getHSize() / 2), n2.getYCoordinate() +
                            (int) n2.getVSize()));
                }

                // These two lines of code are a hack. Since the first and last
                // controlpoint is not recognized by the free router, I add them
                // twice
                controlPoints.add(controlPoints.get(controlPoints.size() - 1));
                controlPoints.add(0, controlPoints.get(0));


                cw.setRouter(RouterFactory.createFreeRouter());
                cw.setControlPoints(controlPoints, false);
                cw.setLineColor(Color.RED);
            }
        }

//        scene.validate();
//        scene.repaint();
//        scene.revalidate();
    }

    private void downAssignment() {

        for (int i = 1; i < layers.size(); i++) {
            ArrayList<SugiyamaNode<N>> layer = layers.get(i);

            assignPriorities(layer, false);

            // create a list of nodes in layer i sorted by priority
            ArrayList<SugiyamaNode> prioOrder = new ArrayList<SugiyamaNode>(layer);
            Collections.sort(prioOrder, new PriorityComparator());

            for (int j = 0; j < prioOrder.size(); j++) {

                // calculate optimal position for a node
                SugiyamaNode<N> node = prioOrder.get(j);
                int optimum = 0;
                int maxPosition = 0;

                if (node.getPredecessorList().size() != 0) {
                    for (SugiyamaNode<N> pred : node.getPredecessorList()) {
                        optimum += pred.getXCoordinate() + (int) pred.getHSize() / 2;
                    }

                    optimum = optimum / node.getPredecessorList().size();
                    optimum -= (int) node.getHSize() / 2;

                } else {
                    optimum = node.getXCoordinate();
                }

                // determine the closest position to the optimum that can be achieved
                // regarding node sizes and desired gap. This is the new position
                // of the node

                // step 1 : check wether there is a node with higher priority
                // right to the current node. If so, the position may not be moved
                // and its X position is a maximum for the position of the current node.
                int nodesBetween = 0;
                int indexOfHighPrioNode = -1;
                double nextNodeSize = 0;

                for (int k = layer.indexOf(node) + 1; k < layer.size(); k++) {

                    // only nodes with lower priority can be shifted
                    if (node.getOrderingPriority() < layer.get(k).getOrderingPriority()) {
                        indexOfHighPrioNode = k;
                        break;
                    }
                    nodesBetween++;
                }

                // since the order of node on a layer must not be changed,
                // any node between the current and a possible maximum position
                // must stay on a relative position to these two nodes.
                // this means, that if there are nodes in between, the maximum
                // X position for the current node is decreased further.

                if (indexOfHighPrioNode > -1) {

                    maxPosition = layer.get(indexOfHighPrioNode).getXCoordinate();
                    maxPosition -= hgap;
                    maxPosition -= node.getHSize();

                    if (nodesBetween == 0) {

                        // there must be enough room between the current node
                        // and the limitation node to regard all nodes which lie
                        // in between

                        int currentNodeIndex = layer.indexOf(node);

                        for (int k = 1; k <= nodesBetween; k++) {
                            maxPosition -= layer.get(currentNodeIndex + k).getHSize();
                            maxPosition -= hgap;
                        }
                    }

                    nextNodeSize = layer.get(indexOfHighPrioNode).getHSize();
                } else {
                    // if there is no limitating node, the current node can
                    // be put anywhere
                    maxPosition = Integer.MAX_VALUE;
                }



                if (maxPosition < optimum) {
                    node.setXCoordinate(maxPosition);

                } else {
                    node.setXCoordinate(optimum);
                }

                // shift all nodes right of the current and left of the
                // limitating node to a new Position where they don't overlap

                int offset = node.getXCoordinate() + (int) node.getHSize() + hgap;
                if (indexOfHighPrioNode > -1) {
                    for (int k = layer.indexOf(node) + 1;
                            k < indexOfHighPrioNode; k++) {

                        SugiyamaNode<N> temp = layer.get(k);
                        temp.setXCoordinate(offset);
                        offset += temp.getHSize() + hgap;
                    }
                } else {
                    for (int k = layer.indexOf(node) + 1;
                            k < layer.size(); k++) {

                        SugiyamaNode<N> temp = layer.get(k);
                        temp.setXCoordinate(offset);
                        offset += temp.getHSize() + hgap;
                    }
                }

            }
            ensureNoOverlapping(layer);
        }
    }

    private void upAssignment() {
        for (int i = layers.size() - 1; i >= 0; i--) {
            ArrayList<SugiyamaNode<N>> layer = layers.get(i);

            assignPriorities(layer, true);

            // create a list of nodes in layer i sorted by priority
            ArrayList<SugiyamaNode> prioOrder = new ArrayList<SugiyamaNode>(layer);
            Collections.sort(prioOrder, new PriorityComparator());

            for (int j = 0; j < prioOrder.size(); j++) {
                // calculate optimal position for a node
                SugiyamaNode<N> node = prioOrder.get(j);
                int optimum = 0;
                int maxPosition = 0;

                if (node.getSuccessorList().size() != 0) {
                    for (SugiyamaNode<N> succ : node.getSuccessorList()) {
                        optimum += succ.getXCoordinate() + (int) succ.getHSize() / 2;
                    }

                    optimum = optimum / node.getSuccessorList().size();
                    optimum -= (int) node.getHSize() / 2;

                } else {
                    optimum = node.getXCoordinate();
                }

                // determine the closest position to the optimum that can be achieved
                // regarding node sizes and desired gap. This is the new position
                // of the node

                // step 1 : check wether there is a node with higher priority
                // right to the current node. If so, the position may not be moved
                // and its X position is a maximum for the position of the current node.
                int nodesBetween = 0;
                int indexOfHighPrioNode = -1;
                double nextNodeSize = 0;
                for (int k = layer.indexOf(node) + 1; k < layer.size(); k++) {

                    // only nodes with lower priority can be shifted
                    if (node.getOrderingPriority() < layer.get(k).getOrderingPriority()) {
                        indexOfHighPrioNode = k;
                        break;
                    }
                    nodesBetween++;
                }

                // since the order of node on a layer must not be changed,
                // any node between the current and a possible maximum position
                // must stay on a relative position to these two nodes.
                // this means, that if there are nodes in between, the maximum
                // X position for the current node is decreased further.

                if (indexOfHighPrioNode > -1) {

                    maxPosition = layer.get(indexOfHighPrioNode).getXCoordinate();
                    maxPosition -= hgap;
                    maxPosition -= node.getHSize();

                    if (nodesBetween == 0) {

                        // there must be enough room between the current node
                        // and the limitation node to regard all nodes which lie
                        // in between

                        int currentNodeIndex = layer.indexOf(node);

                        for (int k = 1; k <= nodesBetween; k++) {
                            maxPosition -= layer.get(currentNodeIndex + k).getHSize();
                            maxPosition -= hgap;
                        }
                    }

                    nextNodeSize = layer.get(indexOfHighPrioNode).getHSize();

                } else {

                    // if there is no limitating node, the current node can
                    // be put anywhere
                    maxPosition = Integer.MAX_VALUE;
                }



                if (maxPosition < optimum) {
                    node.setXCoordinate(maxPosition);

                } else {
                    node.setXCoordinate(optimum);
                }

                // shift all nodes right of the current and left of the
                // limitating node to a new Position where they don't overlap

                int offset = node.getXCoordinate() + (int) node.getHSize() + hgap;
                if (indexOfHighPrioNode > -1) {
                    for (int k = layer.indexOf(node) + 1;
                            k < indexOfHighPrioNode; k++) {

                        SugiyamaNode<N> temp = layer.get(k);
                        temp.setXCoordinate(offset);
                        offset += temp.getHSize() + hgap;
                    }
                } else {
                    for (int k = layer.indexOf(node) + 1;
                            k < layer.size(); k++) {

                        SugiyamaNode<N> temp = layer.get(k);
                        temp.setXCoordinate(offset);
                        offset += temp.getHSize() + hgap;
                    }
                }
            }
            ensureNoOverlapping(layer);
        }
    }

    private void assignPriorities(ArrayList<SugiyamaNode<N>> layer, boolean up) {

        // assign priorities for each vertex on layer i
        // prioritiy is infinite if node is dummy, indegree else

//        for (int i = 0; i < layer.size(); i++) {
//            SugiyamaNode<N> temp = layer.get(i);
//            if(temp.isDummy()) {
//
//                temp.setOrderingPriority(Integer.MAX_VALUE);
//
//            } else {
//                temp.setOrderingPriority(temp.getPredecessorList().size() +
//                        temp.getSuccessorList().size());
//            }
//
//        }

        if (up) {
            for (int j = 0; j < layer.size(); j++) {
                SugiyamaNode temp = layer.get(j);
                if (!temp.isDummy()) {
                    ArrayList<SugiyamaNode<N>> successors = temp.getSuccessorList();
                    temp.setOrderingPriority(successors.size());

                    for (int i = 0; i < successors.size(); i++) {
                        if (successors.get(i).isDummy()) {
                            temp.setOrderingPriority(Integer.MAX_VALUE);
                            break;
                        }
                    }

                } else {
                    temp.setOrderingPriority(Integer.MAX_VALUE);
                }
            }
        } else {
            for (int j = 0; j <
                    layer.size(); j++) {
                SugiyamaNode temp = layer.get(j);
                if (!temp.isDummy()) {
                    ArrayList<SugiyamaNode<N>> predecessors = temp.getPredecessorList();
                    temp.setOrderingPriority(predecessors.size());

                    for (int i = 0; i < predecessors.size(); i++) {
                        if (predecessors.get(i).isDummy()) {
                            temp.setOrderingPriority(Integer.MAX_VALUE);
                            break;
                        }
                    }

                } else {
                    temp.setOrderingPriority(Integer.MAX_VALUE);
                }

            }
        }
    }

    /**
     *
     * @param layer
     */
    private void ensureNoOverlapping(ArrayList<SugiyamaNode<N>> layer) {

        // to most important constraint is, that no node may overlap another
        // so in the last step, this is ensured for each node


        SugiyamaNode first = layer.get(0);
        SugiyamaNode second;

        for (int i = 1; i <
                layer.size(); i++) {

            second = layer.get(i);

            int firstMin;
            int firstMax;
            int secondMin;

            if (horizontal) {
                firstMin = first.getYCoordinate();
                firstMax =
                        firstMin + (int) first.getVSize() + hgap;
                secondMin =
                        second.getYCoordinate();
            } else {
                firstMin = first.getXCoordinate();
                firstMax =
                        firstMin + (int) first.getHSize() + hgap;
                secondMin =
                        second.getXCoordinate();
            }

            if (secondMin < firstMax) {
                if (horizontal) {
                    second.setYCoordinate(firstMax);
                } else {
                    second.setXCoordinate(firstMax);
                }

            }

            first = second;

        }




    }

    private void fitGraphToViewport() {

        int noLayers = layers.size();

        int minX = Integer.MAX_VALUE;
        int minY = layers.get(0).get(0).yCoordinate;

        int maxX = Integer.MIN_VALUE;

        int lastLayerSize = layers.get(noLayers-1).size();
        int maxY = layers.get(noLayers-1).get(lastLayerSize).yCoordinate;

        for (int i = 0; i < layers.size(); i++) {
            SugiyamaNode n = layers.get(i).get(0);
            
            if(n.xCoordinate < minX) 
                minX = n.xCoordinate;
        }

        for (int i = 0; i < layers.size(); i++) {
            int layerSize = layers.get(i).size();
            SugiyamaNode n = layers.get(i).get(layerSize);

            if(n.xCoordinate > maxX)
                maxX = n.xCoordinate;
        }

        Rectangle graphRectangle = new Rectangle(minX, minY, maxX - minX, maxY - minY);
        Rectangle viewArea = scene.getView().getBounds();

        if(graphRectangle.height < viewArea.height && graphRectangle.width < viewArea.width) {

        } else if(graphRectangle.height < viewArea.height) {

        } else if (graphRectangle.width < viewArea.width) {

        } else {
            int xOffset = graphRectangle.x - 20;

            List<Widget> children = scene.getChildren();
            for (int i = 0; i < children.size(); i++) {
                Widget w = children.get(i);
                Point currentLocation = w.getPreferredLocation();
                w.setPreferredLocation(new Point(currentLocation.x - xOffset,
                        currentLocation.y));
            }
        }
    }

    /**
     *
     */
    private class PriorityComparator implements Comparator {

        /**
         *
         * @param o1
         * @param o2
         * @return
         */
        @Override
        public int compare(Object o1, Object o2) {
            if (!(o1 instanceof SugiyamaNode && o2 instanceof SugiyamaNode)) {
                throw new ClassCastException("Kann " + o1.getClass() + " und " +
                        o2.getClass() + " nicht vergleichen");
            }

            SugiyamaNode node1 = (SugiyamaNode) o1;
            SugiyamaNode node2 = (SugiyamaNode) o2;

            return node2.getOrderingPriority() - node1.getOrderingPriority();

        }
    }

    /**
     *
     */
    private void assignCoordinates2() {

        // Using a heuristic found in
        // "Fast and Simple Horizontal Coordinate Assignment"
        //          by Ulrik Brandes and Boris Köpf
        //       Department of Computer & Information Science,
        // University of Konstanz, Box D 188, 78457 Konstanz, Germany
        // {Ulrik.Brandes|Boris.Koepf}@uni-konstanz.de
        // not yet implemented !!!!

        /*
         * Simple layout which puts nodes in a left alignment for each layer
         */


        for (ArrayList<SugiyamaNode<N>> arrayList : layers) {

            // how much is a layer already filled
            int fillFactor = 5;

            for (SugiyamaNode<N> sugiyamaNode : arrayList) {

                int yCoordinate = sugiyamaNode.getLayer() * this.vgap;

                if (sugiyamaNode.getValue() != null) {
                    Widget w = scene.findWidget(sugiyamaNode.getValue());

                    // calculate widget location from layer, widgetSize and gap
                    // between layers and nodes

                    w.setPreferredLocation(new Point(fillFactor, yCoordinate));
                }

                sugiyamaNode.setYCoordinate(yCoordinate);
                sugiyamaNode.setXCoordinate(fillFactor);

                fillFactor +=
                        hgap + sugiyamaNode.getHSize();
            }

        }

        // Add a control point trail to each long edge, so that these Edges
        // follow the positions of the Dummy Nodes that replaced the edge previously
        for (Object key : longEdgeMapping.keySet()) {
            Widget w = scene.findWidget(edgeMapping.get(key).getValue());

            if (w instanceof ConnectionWidget) {
                ConnectionWidget cw = (ConnectionWidget) w;
                ArrayList<Point> controlPoints = new ArrayList<Point>();
                ArrayList<SugiyamaNode<N>> dummyTrail =
                        dummyMapping.get(longEdgeMapping.get(key));

                for (SugiyamaNode<N> sugiyamaNode : dummyTrail) {

                    controlPoints.add(new Point(sugiyamaNode.getXCoordinate() + 20,
                            sugiyamaNode.getYCoordinate()));
                }

                // These two lines of code are a hack. Since the first and last
                //controlpoint is not recognized by the free router, I add them
                // twice
                controlPoints.add(controlPoints.get(controlPoints.size() - 1));
                controlPoints.add(0, controlPoints.get(0));

                cw.setRouter(RouterFactory.createFreeRouter());
                cw.setControlPoints(controlPoints, false);
                System.out.println(controlPoints.size() + " controlpoints added for " +
                        edgeMapping.get(key).getFrom().toString() + " -> " +
                        edgeMapping.get(key).getTo().toString());
                cw.setLineColor(Color.RED);
            }

        }

        scene.validate();
        scene.repaint();
        scene.revalidate();
    }

//==========================================================================
//      Internal Classes
//==========================================================================
    /**
     *
     * @param <N>
     */
    private class SugiyamaNode<N> {

        private int layer;
        private float barycenter;
        private N value;
        private double vSize;
        private double hSize;
        private int xCoordinate;
        private int yCoordinate;
        private DFSNodeColor color;
        private SugiyamaNode<N> dfsPredecessor;
        private int discoveryTime;
        private int finishingTime;
        private ArrayList<SugiyamaNode<N>> predecessorList;
        private ArrayList<SugiyamaNode<N>> successorList;
        private HashMap<E, SugiyamaEdge<E>> referencedEdges;
        private boolean dummy = false;
        private int orderingPriority;

        public SugiyamaNode(N value, SugiyamaNode<N> predecessor,
                SugiyamaNode<N> successor) {

            this.value = value;

            if (value == null) {
                dummy = true;
                hSize = dummySizeHorizontal;
                vSize = dummySizeVertical;
            }

            color = DFSNodeColor.white;
            dfsPredecessor = null;

            this.layer = -1;
            this.barycenter = Float.NaN;
            this.orderingPriority = 0;
            predecessorList = new ArrayList<SugiyamaNode<N>>();
            successorList = new ArrayList<SugiyamaNode<N>>();
            referencedEdges = new HashMap<E, SugiyamaEdge<E>>();

            addSuccessor(successor);
            addPredecessor(predecessor);

        }

        public SugiyamaNode(N value) {
            this(value, null, null);
        }

        public void addReferencedEdge(SugiyamaEdge<E> edge) {
            if (edge != null) {
                referencedEdges.put(edge.getValue(), edge);
            }
        }

        public void addPredecessor(SugiyamaNode<N> pred) {
            if (pred != null) {
                predecessorList.add(pred);
            }
        }

        public void addSuccessor(SugiyamaNode<N> succ) {
            if (succ != null) {
                successorList.add(succ);
            }
        }

        public void removePredecessor(SugiyamaNode<N> pred) {
            if (predecessorList.contains(pred)) {
                predecessorList.remove(pred);
            }
        }

        public void removeSuccessor(SugiyamaNode<N> succ) {
            if (successorList.contains(succ)) {
                successorList.remove(succ);
            }
        }

        public float getBarycenter() {
            return barycenter;
        }

        public void setBarycenter(float barycenter) {
            this.barycenter = barycenter;
        }

        public double getHSize() {
            return hSize;
        }

        public void setHSize(double hSize) {
            this.hSize = hSize;
        }

        public int getLayer() {
            return layer;
        }

        public void setLayer(int layer) {
            this.layer = layer;
        }

        public double getVSize() {
            return vSize;
        }

        public void setVSize(double vSize) {
            this.vSize = vSize;
        }

        public N getValue() {
            return value;
        }

        public int getDiscoveryTime() {
            return discoveryTime;
        }

        public void setDiscoveryTime(int discoveryTime) {
            this.discoveryTime = discoveryTime;
        }

        public int getFinishingTime() {
            return finishingTime;
        }

        public void setFinishingTime(int finishingTime) {
            this.finishingTime = finishingTime;
        }

        public DFSNodeColor getColor() {
            return color;
        }

        public void setColor(DFSNodeColor color) {
            this.color = color;
        }

        public ArrayList<SugiyamaNode<N>> getPredecessorList() {
            return predecessorList;
        }

        public ArrayList<SugiyamaNode<N>> getSuccessorList() {
            return successorList;
        }

        public int getXCoordinate() {
            return xCoordinate;
        }

        public int getYCoordinate() {
            return yCoordinate;
        }

        public void setXCoordinate(int xCoordinate) {
            this.xCoordinate = xCoordinate;
        }

        public void setYCoordinate(int yCoordinate) {
            this.yCoordinate = yCoordinate;
        }

        public SugiyamaNode<N> getDfsPredecessor() {
            return dfsPredecessor;
        }

        public void setDfsPredecessor(SugiyamaNode<N> dfsPredecessor) {
            this.dfsPredecessor = dfsPredecessor;
        }

        public int getOrderingPriority() {
            return orderingPriority;
        }

        public void setOrderingPriority(int orderingPriority) {
            this.orderingPriority = orderingPriority;
        }

        @Override
        public String toString() {
            if (dummy) {
                return "D";
            } else {
                return value.toString();
            }
        }

        public boolean isDummy() {
            return dummy;
        }
    }

    /**
     *
     * @param <E>
     */
    private class SugiyamaEdge<E> {

        private SugiyamaNode<N> from;
        private SugiyamaNode<N> to;
        private E value;

        public SugiyamaEdge(E value, SugiyamaNode<N> from, SugiyamaNode<N> to) {
            this.value = value;
            this.from = from;
            this.to = to;
        }

        public E getValue() {
            return value;
        }

        public SugiyamaNode<N> getFrom() {
            return from;
        }

        public void setFrom(SugiyamaNode<N> from) {
            this.from = from;
        }

        public SugiyamaNode<N> getTo() {
            return to;
        }

        public void setTo(SugiyamaNode<N> to) {
            this.to = to;
        }
    }

    /**
     *
     */
    private class TopologyComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            if (!(o1 instanceof SugiyamaNode) || !(o2 instanceof SugiyamaNode)) {
                throw new ClassCastException("Can not compare " + o1.getClass() +
                        " and " + o2.getClass());
            }

            SugiyamaNode node1 = (SugiyamaNode) o1;
            SugiyamaNode node2 = (SugiyamaNode) o2;

            /*
             *
             */
            return node2.getFinishingTime() - node1.getFinishingTime();

        }
    }

    private class BarycenterComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            if (!(o1 instanceof SugiyamaNode) || !(o2 instanceof SugiyamaNode)) {
                throw new ClassCastException("Can not compare " + o1.getClass() +
                        " and " + o2.getClass());
            }

            SugiyamaNode node1 = (SugiyamaNode) o1;
            SugiyamaNode node2 = (SugiyamaNode) o2;

            if (node1.getBarycenter() == Float.NaN || node2.getBarycenter() == Float.NaN) {
                return 0;
            } else {
                Float re = node1.getBarycenter() - node2.getBarycenter();

                if (re > 0) {
                    return 1;
                } else if (re < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    /**
     *
     */
    private class TwoLayerIncidenceMatrix {

        private ArrayList<SugiyamaNode<N>> layer1;
        private ArrayList<SugiyamaNode<N>> layer2;
        private int[][] incidents;

        private TwoLayerIncidenceMatrix(ArrayList<SugiyamaNode<N>> layer1,
                ArrayList<SugiyamaNode<N>> layer2) {

            initialize(layer1, layer2);
        }

        private void initialize(ArrayList<SugiyamaNode<N>> layer1,
                ArrayList<SugiyamaNode<N>> layer2) {

            if (layer1 != null && layer2 != null) {

                this.layer1 = new ArrayList<SugiyamaNode<N>>(layer1);
                this.layer2 = new ArrayList<SugiyamaNode<N>>(layer2);

                calculateMatrix();

                // calculate baryCenters for each Node
//                calculateBarycenters();
            }
        }

        private void calculateBarycenters() {
            for (int i = 0; i < incidents.length; i++) {
                float barycenter = 0.0f;
                float divisor = 0.0f;
                for (int j = 0; j < incidents[i].length; j++) {
                    if (incidents[i][j] == 1) {
                        barycenter += j + 1;
                        divisor++;
                    }
                }

                layer1.get(i).setBarycenter(barycenter / divisor);

            }

            for (int i = 0; i < incidents[0].length; i++) {
                float barycenter = 0.0f;
                float divisor = 0.0f;
                for (int j = 0; j < incidents.length; j++) {
                    if (incidents[j][i] == 1) {
                        barycenter += j + 1;
                        divisor++;
                    }
                }

                layer2.get(i).setBarycenter(barycenter / divisor);

            }
        }

        private int calculateNumberOfCrossings() {
            int crossings = 0;

            for (int j = 0; j < incidents.length - 1; j++) {
                for (int k = j + 1; k < incidents.length; k++) {
                    for (int a = 0; a < incidents[k].length - 1; a++) {
                        for (int b = a + 1; b < incidents[k].length; b++) {
                            crossings += incidents[j][b] * incidents[k][a];
                        }
                    }
                }
            }

            return crossings;
        }

        public void BOR() {
            // calculates the inner matrix each time new -> inefficient, but simple
            // initial solution

            calculateBarycenters();

            Collections.sort(layer1, new BarycenterComparator());
//            sortLayer(layer1);

            calculateMatrix();

            calculateBarycenters();
        }

        public void BOC() {
            // calculates the inner matrix each time new -> inefficient, but simple

            calculateBarycenters();

            Collections.sort(layer2, new BarycenterComparator());
//            sortLayer(layer2);

            calculateMatrix();

            calculateBarycenters();
        }

        public void ROR() {

            calculateBarycenters();

            int leftPos = -1;
            int rightPos = -1;

            for (int i = 0; i < layer1.size() - 1; i++) {

                for (int j = layer1.size() - 1; j > i; j--) {
                    if (layer1.get(i).getBarycenter() == layer1.get(j).getBarycenter()) {

                        leftPos = i;
                        rightPos = j;
                        break;
                    }
                }
                if (leftPos != rightPos) {
                    break;
                }
            }

            if ((leftPos != -1) && (rightPos != -1)) {
                int center = leftPos + ((rightPos - leftPos) / 2);

                int i = leftPos;
                int j = rightPos;

                while (i < j) {

                    SugiyamaNode<N> temp = layer1.get(i);
                    layer1.set(i, layer1.get(j));
                    layer1.set(j, temp);

                    i++;
                    j--;


                }
            }

            calculateMatrix();

        }

        public void ROC() {

            calculateBarycenters();

            int leftPos = -1;
            int rightPos = -1;

            for (int i = 0; i < layer2.size(); i++) {
                for (int j = layer2.size() - 1; j > i; j--) {
                    if (layer2.get(i).getBarycenter() == layer2.get(j).getBarycenter()) {

                        leftPos = i;
                        rightPos = j;
                        break;
                    }
                }
                if (leftPos != rightPos) {
                    break;
                }
            }

            if ((leftPos != -1) && (rightPos != -1)) {
                int center = leftPos + ((rightPos - leftPos) / 2);

                int i = leftPos;
                int j = rightPos;

                while (i < j) {

                    SugiyamaNode<N> temp = layer2.get(i);
                    layer2.set(i, layer2.get(j));
                    layer2.set(j, temp);

                    i++;
                    j--;
                }
            }

            calculateMatrix();

        }

        public boolean rowsInIncreaseOrder() {

            calculateMatrix();
            calculateBarycenters();
            SugiyamaNode lastNode = layer1.get(0);

            for (int i = 1; i < layer1.size(); i++) {
                SugiyamaNode tmp = layer1.get(i);
                if (!(tmp.getBarycenter() == Float.NaN ||
                        lastNode.getBarycenter() == Float.NaN) &&
                        tmp.getBarycenter() < lastNode.getBarycenter()) {
                    return false;
                }
            }

            return true;
        }

        public boolean columnsInIncreaseOrder() {

            calculateMatrix();
            calculateBarycenters();
            SugiyamaNode lastNode = layer2.get(0);

            for (int i = 1; i < layer2.size(); i++) {
                SugiyamaNode tmp = layer2.get(i);
                if ((tmp.getBarycenter() != Float.NaN &&
                        lastNode.getBarycenter() != Float.NaN) &&
                        tmp.getBarycenter() < lastNode.getBarycenter()) {
                    return false;
                }
                lastNode = tmp;
            }

            return true;
        }

        public ArrayList<SugiyamaNode<N>> getLayer1() {
            return layer1;
        }

        public ArrayList<SugiyamaNode<N>> getLayer2() {
            return layer2;
        }

        private void setLayer1(ArrayList<SugiyamaNode<N>> layer1) {
            this.layer1 = layer1;

            calculateMatrix();
        }

        private void setLayer2(ArrayList<SugiyamaNode<N>> layer2) {
            this.layer2 = layer2;

            calculateMatrix();
        }

        private void setMatrix(ArrayList<SugiyamaNode<N>> layer1,
                ArrayList<SugiyamaNode<N>> layer2) {

            this.layer1 = layer1;
            this.layer2 = layer2;

            calculateMatrix();
        }

        private void calculateMatrix() {
            incidents = new int[layer1.size()][layer2.size()];

            for (SugiyamaNode<N> node1 : layer1) {
                for (SugiyamaNode<N> node2 : layer2) {

                    int i = layer1.indexOf(node1);
                    int j = layer2.indexOf(node2);

                    if (node1.getSuccessorList().contains(node2) ||
                            node2.getPredecessorList().contains(node1)) {
                        incidents[i][j] = 1;
                    } else {
                        incidents[i][j] = 0;
                    }
                }
            }
        }

        private void sortLayer(ArrayList<SugiyamaNode<N>> layer) {
            for (int i = layer.size() - 1; i > 0; i--) {
                for (int j = 0; j < i; j++) {

                    if (layer.get(i).getBarycenter() == Float.NaN) {
                    } else if (layer.get(i + 1).getBarycenter() == Float.NaN) {
                        i++;

                    } else if (layer.get(i).getBarycenter() >
                            layer.get(i + 1).getBarycenter()) {
                        SugiyamaNode<N> temp = layer.get(i);
                        layer.set(i, layer.get(i + 1));
                        layer.set(i + 1, temp);
                    }
                }
            }
        }

        @Override
        public String toString() {
            StringBuffer buff = new StringBuffer();


            for (int i = 0; i < incidents.length; i++) {

                for (int j = 0; j < incidents[i].length; j++) {
                    buff.append(incidents[i][j] + "\t");
                }
                buff.append(layer1.get(i).toString() + ":");
                buff.append(layer1.get(i).getBarycenter() + "\n");
            }
            for (int i = 0; i < layer2.size(); i++) {
                buff.append(layer2.get(i).toString() + ":");
                buff.append(layer2.get(i).barycenter + "\t");
            }
            buff.append("\n");

            return buff.toString();
        }
    }
}


