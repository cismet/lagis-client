/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.util;

import java.util.ArrayList;
import java.util.Date;

import de.cismet.lagisEE.entity.core.Nutzung;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
public class NutzungsContainer {

    //~ Instance fields --------------------------------------------------------

    private ArrayList<Nutzung> nutzungen;
    private Date currentDate;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NutzungsContainer object.
     *
     * @param  nutzungen    DOCUMENT ME!
     * @param  currentDate  DOCUMENT ME!
     */
    public NutzungsContainer(final ArrayList<Nutzung> nutzungen, final Date currentDate) {
        this.nutzungen = nutzungen;
        this.currentDate = currentDate;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getCurrentDate() {
        return currentDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  currentDate  DOCUMENT ME!
     */
    public void setCurrentDate(final Date currentDate) {
        this.currentDate = currentDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<Nutzung> getNutzungen() {
        return nutzungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  nutzungen  DOCUMENT ME!
     */
    public void setNutzungen(final ArrayList<Nutzung> nutzungen) {
        this.nutzungen = nutzungen;
    }
}
