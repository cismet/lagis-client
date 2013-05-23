/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.models;

import de.cismet.cids.custom.beans.lagis.BeschlussCustomBean;
import java.util.List;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.gui.tables.AbstractCidsBeanTable_Lagis;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.log4j.Logger;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public abstract class CidsBeanTableModel_Lagis extends AbstractTableModel {

    //~ Instance fields --------------------------------------------------------
private static final Logger LOG = org.apache.log4j.Logger.getLogger(CidsBeanTableModel_Lagis.class);
    private List<? extends CidsBean> cidsBeans;
    private final String[] columnNames;
    private final Class[] columnClasses;
    private boolean isInEditMode = false;
    private AbstractCidsBeanTable_Lagis table;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CidsBeanTableModel object.
     *
     * @param  columnNames    DOCUMENT ME!
     * @param  columnClasses  DOCUMENT ME!
     */
    protected CidsBeanTableModel_Lagis(final String[] columnNames, final Class[] columnClasses) {
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public int getRowCount() {
        return cidsBeans.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(final int column) {
        return columnNames[column];
    }

    @Override
    public Class getColumnClass(final int column) {
        return columnClasses[column];
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return (columnNames.length > columnIndex) && (cidsBeans.size() > rowIndex) && isInEditMode;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  <C>       DOCUMENT ME!
     * @param  cidsbean  DOCUMENT ME!
     */
    public <C extends CidsBean> void addCidsBean(final C cidsbean) {
        ((List<CidsBean>)cidsBeans).add(cidsbean);
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rowIndex  DOCUMENT ME!
     */
    public void removeCidsBean(final int rowIndex) {
        cidsBeans.remove(rowIndex);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <C>       DOCUMENT ME!
     * @param   rowIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public <C extends CidsBean> C getCidsBeanAtRow(final int rowIndex) {
        return (C)cidsBeans.get(rowIndex);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<? extends CidsBean> getCidsBeans() {
        return cidsBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setCidsBeans(final List<? extends CidsBean> cidsBeans) {
        this.cidsBeans = cidsBeans;
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isIsInEditMode() {
        return isInEditMode;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isInEditMode  DOCUMENT ME!
     */
    public void setIsInEditMode(final boolean isInEditMode) {
        this.isInEditMode = isInEditMode;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JTable getTable() {
        return table;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  table  DOCUMENT ME!
     */
    public void setTable(final AbstractCidsBeanTable_Lagis table) {
        this.table = table;
    }
    
        /**
     * DOCUMENT ME!
     *
     * @param  beschluesse  DOCUMENT ME!
     */
    public <T extends CidsBean> void refreshTableModel(final Collection<T> cidsbeans) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Refresh des BeschlussTableModell");
            }
            if (cidsbeans != null) {
                setCidsBeans(new ArrayList<T>(cidsbeans));
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Beschlüssevektor == null --> Erstelle Vektor.");
                }
                setCidsBeans(new ArrayList<T>());
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim refreshen des Models", ex);
            setCidsBeans(new ArrayList<T>());
        }
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     */
    public void fireTableDataChangedAndKeepSelection() {
        final int selection_view = table.getSelectedRow();

        int selection_model_tmp = -1;
        if (selection_view > -1) {
            selection_model_tmp = table.convertRowIndexToModel(selection_view);
        }
        final int selection_model = selection_model_tmp;

        this.fireTableDataChanged();

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
}
