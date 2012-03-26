/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.copypaste;

import java.util.List;

import javax.swing.Icon;

import de.cismet.lagisEE.entity.basic.BasicEntity;

/**
 * Interface for all components from which data can be copied. Note that {@link Copyable} implmentations must be
 * registered in {@link FlurstueckInfoClipboard} with
 * {@link FlurstueckInfoClipboard#addCopyListener(de.cismet.lagis.gui.copypaste.Copyable) }.
 *
 * @author   Benjamin Friedrich
 * @version  1.0, 15.11.2011
 */
public interface Copyable {

    //~ Instance fields --------------------------------------------------------

    /**
     * Return value for {@link Copyable#getDisplayName(de.cismet.lagisEE.entity.basic.BasicEntity) } if display name can
     * not be determined.
     */
    String UNKNOWN_ENTITY = "unbekannt";

    //~ Methods ----------------------------------------------------------------

    /**
     * Returns the data to be copied.
     *
     * @return  copied data
     */
    List<BasicEntity> getCopyData();

    /**
     * Returns display name of the given. If given entity is not supported, {@link Copyable#UNKNOWN_ENTITY} shall be
     * returned. The display name is used to represent items in {@link FlurstueckInfoClipboardPasteWidget}.
     *
     * @param   entity  entity for which the display name shall be determined
     *
     * @return  entity display name
     */
    String getDisplayName(BasicEntity entity);

    /**
     * Indicates if display name can be determined for the given entity.
     *
     * @param   entity  entity for which the display name shall be determined
     *
     * @return  true if display name can be determined, false otherwise
     */
    boolean knowsDisplayName(BasicEntity entity);

    /**
     * Returns the display icon which is used for displaying items in {@link FlurstueckInfoClipboardPasteWidget}.
     *
     * @return  display icon
     */
    Icon getDisplayIcon();
}
