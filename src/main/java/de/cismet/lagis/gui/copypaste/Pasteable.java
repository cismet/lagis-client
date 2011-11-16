/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.copypaste;

import java.util.List;

import de.cismet.lagisEE.entity.basic.BasicEntity;

/**
 * Interface for all components to which data can be pasted. Note that {@link Pasteable} implmentations must be
 * registered in {@link FlurstueckInfoClipboard} with
 * {@link FlurstueckInfoClipboard#addPasteListener(de.cismet.lagis.gui.copypaste.Pasteable) ) }.
 *
 * @author   Benjamin Friedrich
 * @version  1.0, 15.11.2011
 */
public interface Pasteable {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  item  DOCUMENT ME!
     */
    void paste(final BasicEntity item);
    /**
     * DOCUMENT ME!
     *
     * @param  items  DOCUMENT ME!
     */
    void pasteAll(final List<BasicEntity> items);
}
