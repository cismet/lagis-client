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

import de.cismet.lagis.gui.tables.AbstractCidsBeanTable_Lagis;

import de.cismet.lagis.models.CidsBeanTableModel_Lagis;

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
    public static void fireTableDataChangedAndKeepSelection(final AbstractTableModel tableModel, final JTable table) {
        final int selection_view = table.getSelectedRow();

        int selection_model_tmp = -1;
        if (selection_view > -1) {
            selection_model_tmp = table.convertRowIndexToModel(selection_view);
        }
        final int selection_model = selection_model_tmp;

        tableModel.fireTableDataChanged();

        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    if ((selection_view == -1) || (selection_view >= table.getRowCount())) {
                        table.clearSelection();
                    } else {
                        final int selection_view_tmp = table.convertRowIndexToView(selection_model);
                        table.setRowSelectionInterval(selection_view_tmp, selection_view_tmp);
                        table.scrollRectToVisible(table.getCellRect(selection_view_tmp, 0, true));
                    }
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @param  model  DOCUMENT ME!
     * @param  table  DOCUMENT ME!
     */
    public static void crossReferenceModelAndTable(final CidsBeanTableModel_Lagis model,
            final AbstractCidsBeanTable_Lagis table) {
        table.setModel(model);
        model.setTable(table);
    }
}
