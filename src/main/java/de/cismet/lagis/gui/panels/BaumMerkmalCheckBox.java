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

import de.cismet.cids.custom.beans.lagis.BaumMerkmalCustomBean;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class BaumMerkmalCheckBox extends JCheckBox {

    //~ Instance fields --------------------------------------------------------

    private final BaumMerkmalCustomBean baumMerkmal;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MerkmalCheckBox object.
     *
     * @param  baumMerkmal  DOCUMENT ME!
     */
    public BaumMerkmalCheckBox(final BaumMerkmalCustomBean baumMerkmal) {
        super(baumMerkmal.getBezeichnung());
        this.baumMerkmal = baumMerkmal;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BaumMerkmalCustomBean getBaumMerkmal() {
        return baumMerkmal;
    }
}
