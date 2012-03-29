/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2010 jweintraut
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.lagis.report.scriptlet;

import att.grappa.*;

import net.sf.jasperreports.engine.JRDefaultScriptlet;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.PrintWriter;
import java.io.StringReader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckHistorieCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckSchluesselCustomBean;

import de.cismet.lagis.Exception.ActionNotSuccessfulException;

import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class HistoryDiagramScriptlet extends JRDefaultScriptlet {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            HistoryDiagramScriptlet.class);

    private static final String DEFAULT_DOT_HEADER = "digraph G{\n";

    private static final int HEIGHT = 420;
    private static final int WIDTH = 920;

    private static final String HISTORY_SERVER_URL = "http://s10221:8099/verdis/cgi-bin/format-graph";

    private static final String GRAPH_NAME = "Flurstück Historie";

    private static final int LEVEL_COUNT = 0;

    //~ Instance fields --------------------------------------------------------

    private Graph graph;
    private final GrappaPanel gp;
    private String encodedDotGraphRepresentation;
    private StringBuffer dotGraphRepresentation;
//    private FlurstueckCustomBean currentObj;

    private final Map<String, FlurstueckSchluesselCustomBean> nodeToKeyMap;
    private final Map<String, String> pseudoKeys;
    private final URL historyServerUrl;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new HistoryDiagramScriptlet object.
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public HistoryDiagramScriptlet() {
        this.graph = new Graph(GRAPH_NAME);
        gp = new GrappaPanel(graph);
        dotGraphRepresentation = new StringBuffer();
        nodeToKeyMap = new HashMap<String, FlurstueckSchluesselCustomBean>();
        pseudoKeys = new HashMap<String, String>();

        try {
            historyServerUrl = new URL(HISTORY_SERVER_URL);
        } catch (final MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void doURLLayout() {
        try {
            final Object connector = historyServerUrl.openConnection();
            final URLConnection urlConn = (URLConnection)connector;
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            if (!GrappaSupport.filterGraph(graph, connector)) {
                LOG.error("somewhere in filtergraph");
            }

            if (connector instanceof Process) {
                final int code = ((Process)connector).waitFor();
                if (code != 0) {
                    LOG.error("proc exit code is: " + code);
                }
            }

            graph.repaint();
            gp.setVisible(true);
        } catch (final Exception ex) {
            LOG.error("Exception while doURLLayout(): ", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  currentObj  DOCUMENT ME!
     */
    private void layoutGraph(final FlurstueckCustomBean currentObj) {
        final Parser program = new Parser(new StringReader(encodedDotGraphRepresentation),
                new PrintWriter(System.err));
        try {
            program.parse();
        } catch (Exception ex) {
            LOG.error(ex);
        }
        graph = program.getGraph();
        graph.setEditable(false);

        doURLLayout();

        gp.refresh(graph);
        final GraphEnumeration ge = gp.getSubgraph().elements(Subgraph.NODE);
        while (ge.hasMoreElements()) {
            final Element curNode = ge.nextGraphElement();

            final FlurstueckSchluesselCustomBean curUserObjectForNode = nodeToKeyMap.get(curNode.toString());

            if (curUserObjectForNode.equals(currentObj.getFlurstueckSchluessel())) {
                curNode.highlight &= ~curNode.HIGHLIGHT_MASK;
            }
            curNode.setUserObject(curUserObjectForNode);
        }
        gp.repaint();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Image loadHistoryImage() {
        gp.setSize(WIDTH, HEIGHT);
        dotGraphRepresentation.append(DEFAULT_DOT_HEADER);

        gp.setScaleToFit(true);
        graph.setEditable(false);

        final EJBroker.HistoryLevel level = EJBroker.HistoryLevel.DIRECT_RELATIONS;

        final FlurstueckCustomBean currentObj = LagisBroker.getInstance().getCurrentFlurstueck();

        try {
            final Collection<FlurstueckHistorieCustomBean> allEdges = EJBroker.getInstance()
                        .getHistoryEntries(currentObj.getFlurstueckSchluessel(),
                            level,
                            EJBroker.HistoryType.BOTH,
                            LEVEL_COUNT);

            if ((allEdges != null) && (allEdges.size() > 0)) {
                final Iterator<FlurstueckHistorieCustomBean> it = allEdges.iterator();

                while (it.hasNext()) {
                    final FlurstueckHistorieCustomBean currentEdge = it.next();
                    final String currentVorgaenger = currentEdge.getVorgaenger().toString();
                    final String currentNachfolger = currentEdge.getNachfolger().toString();

                    if (currentVorgaenger.startsWith("pseudo")) {
                        pseudoKeys.put(currentVorgaenger, "    ");
                    }

                    if (currentNachfolger.startsWith("pseudo")) {
                        pseudoKeys.put(currentNachfolger, "    ");
                    }

                    dotGraphRepresentation.append('\"')
                            .append(currentVorgaenger)
                            .append('\"')
                            .append("->")
                            .append('\"')
                            .append(currentNachfolger)
                            .append('\"')
                            .append(";\n");

                    nodeToKeyMap.put('\"' + currentEdge.getVorgaenger().toString() + '\"',
                        currentEdge.getVorgaenger().getFlurstueckSchluessel());
                    nodeToKeyMap.put('\"' + currentEdge.getNachfolger().toString() + '\"',
                        currentEdge.getNachfolger().getFlurstueckSchluessel());
                }
            } else {
                dotGraphRepresentation.append('\"').append(currentObj).append('\"').append(";\n");

                nodeToKeyMap.put('\"' + currentObj.toString() + '\"', currentObj.getFlurstueckSchluessel());
            }

            if (pseudoKeys.size() > 0) {
                for (final String key : pseudoKeys.keySet()) {
                    dotGraphRepresentation.append('\"').append(key).append("\" [label=\"    \"]");
                }
            }

            dotGraphRepresentation.append('}');
            encodedDotGraphRepresentation = GrappaSupport.encodeString(dotGraphRepresentation.toString());
            layoutGraph(currentObj);
        } catch (final ActionNotSuccessfulException ex) {
            LOG.error(ex);
        }

        final BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        gp.print(img.getGraphics());

        return img;
    }
}
