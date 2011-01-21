/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * WFSUpdateContainer.java
 *
 * Created on 18. Dezember 2007, 12:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.utillity;

import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class WFSUpdateContainer {

    //~ Instance fields --------------------------------------------------------

    private FlurstueckSchluessel flurstueckSchluessel;
    private boolean noGeometryAssigned = true;
    private boolean manyVerwaltungsbereiche;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of WFSUpdateContainer.
     */
    public WFSUpdateContainer() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckSchluessel getFlurstueckSchluessel() {
        return flurstueckSchluessel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueckSchluessel  DOCUMENT ME!
     */
    public void setFlurstueckSchluessel(final FlurstueckSchluessel flurstueckSchluessel) {
        this.flurstueckSchluessel = flurstueckSchluessel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isNoGeometryAssigned() {
        return noGeometryAssigned;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  noGeometryAssigned  DOCUMENT ME!
     */
    public void setNoGeometryAssigned(final boolean noGeometryAssigned) {
        this.noGeometryAssigned = noGeometryAssigned;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean hasManyVerwaltungsbereiche() {
        return manyVerwaltungsbereiche;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  manyVerwaltungsbereiche  DOCUMENT ME!
     */
    public void setManyVerwaltungsbereiche(final boolean manyVerwaltungsbereiche) {
        this.manyVerwaltungsbereiche = manyVerwaltungsbereiche;
    }
}
