/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.lagis.layout.model;

import de.cismet.lagisEE.entity.core.Flurstueck;

/**
 * Since the set of HistoryEntries returned by the EJBBroker does not contain
 * certain information about the edges, these informations are extrapolated during
 * runtime and stored in this class.
 *
 * An HistoryPanelEdge simply holds information about the start and endpoint of the edge
 *
 * @author mbrill
 */
    public class HistoryPanelEdge {

        /**
         * The origin of the edge
         */
        private Flurstueck from;

        /**
         * The target of the edge
         */
        private Flurstueck to;

        /**
         * Constructor generates a new HistoryPanelEdge instance by initialising
         * the parameters <code>from</code> and <code>to</code>.
         *
         * @param from Source of the edge
         * @param to Target of the edge
         */
        public HistoryPanelEdge(Flurstueck from, Flurstueck to) {
            this.from = from;
            this.to = to;
        }

        /**
         * Returns the from attribute
         * @return the source Flurstueck of the edge
         */
        public Flurstueck getFrom() {
            return from;
        }

        /**
         * Returns the to attribute
         * @return the target Flurstueck of the edge
         */
        public Flurstueck getTo() {
            return to;
        }
    }
