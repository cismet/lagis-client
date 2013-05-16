/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.models;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public abstract class CidsBeanTableModel_Lagis extends AbstractTableModel {

    //~ Instance fields --------------------------------------------------------

    private List<? extends CidsBean> cidsBeans;
    private final String[] columnNames;
    private boolean isInEditMode = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CidsBeanTableModel object.
     *
     * @param  columnNames  DOCUMENT ME!
     */
    protected CidsBeanTableModel_Lagis(final String[] columnNames) {
        this.columnNames = columnNames;
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
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return (columnNames.length > columnIndex) && (cidsBeans.size() > rowIndex) && isInEditMode;
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
}
