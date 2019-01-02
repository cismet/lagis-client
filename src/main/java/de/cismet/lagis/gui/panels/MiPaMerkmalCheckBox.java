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
package de.cismet.lagis.gui.panels;

import javax.swing.JCheckBox;

import de.cismet.cids.custom.beans.lagis.MipaMerkmalCustomBean;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class MiPaMerkmalCheckBox extends JCheckBox {

    //~ Instance fields --------------------------------------------------------

    private final MipaMerkmalCustomBean miPaMerkmal;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MerkmalCheckBox object.
     *
     * @param  miPa  DOCUMENT ME!
     */
    public MiPaMerkmalCheckBox(final MipaMerkmalCustomBean miPa) {
        super(miPa.getBezeichnung());
        this.miPaMerkmal = miPa;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MipaMerkmalCustomBean getMiPaMerkmal() {
        return miPaMerkmal;
    }
}
