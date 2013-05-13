/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.util;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import de.cismet.lagis.gui.panels.NKFOverviewPanel;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public class TableSelectionUtils {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  tableModel  DOCUMENT ME!
     * @param  table       DOCUMENT ME!
     */
    public static void fireTableDataChanged_keepSelection(final AbstractTableModel tableModel, final JTable table) {
        final int selection = table.getSelectedRow();
        tableModel.fireTableDataChanged();
        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    if ((selection == -1) || (selection >= table.getRowCount())) {
                        table.clearSelection();
                    } else {
                        table.setRowSelectionInterval(selection, selection);
                    }
                }
            });
    }
}
