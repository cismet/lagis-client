/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.gui.panels;

import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

import java.awt.Component;
import java.awt.Font;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class BoldHighlighter extends AbstractHighlighter {

    //~ Instance fields --------------------------------------------------------

    private Font boldFont = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BoldHighlighter object.
     *
     * @param  predicate  DOCUMENT ME!
     */
    public BoldHighlighter(final HighlightPredicate predicate) {
        super(predicate);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Component doHighlight(final Component renderer, final ComponentAdapter adapter) {
        renderer.setFont((boldFont == null) ? (boldFont = renderer.getFont().deriveFont(Font.BOLD)) : boldFont);
        return renderer;
    }
}
