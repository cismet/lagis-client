/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractCidsBeanTableModel extends AbstractTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AbstractCidsBeanTableModel.class);

    //~ Instance fields --------------------------------------------------------

    private List<CidsBean> cidsBeans;
    private final String[] columnNames;
    private final Class[] columnClasses;
    private final List<CidsBean> removedCidsBeans = new ArrayList<>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CidsBeanTableModel object.
     *
     * @param  columnNames    DOCUMENT ME!
     * @param  columnClasses  DOCUMENT ME!
     */
    protected AbstractCidsBeanTableModel(final String[] columnNames, final Class[] columnClasses) {
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(final int column) {
        return isInColumnBounds(column) ? columnNames[column] : null;
    }

    @Override
    public Class getColumnClass(final int column) {
        return isInColumnBounds(column) ? columnClasses[column] : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getCidsBeans() {
        return cidsBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setCidsBeans(final List<CidsBean> cidsBeans) {
        removedCidsBeans.clear();
        this.cidsBeans = cidsBeans;
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   row  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getCidsBeanAtRow(final int row) {
        return isInRowBounds(row) ? cidsBeans.get(row) : null;
    }

    @Override
    public int getRowCount() {
        if (cidsBeans == null) {
            return 0;
        }
        return cidsBeans.size();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   modelIndices  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<CidsBean> getCidsBeansByIndices(final int[] modelIndices) {
        final Collection<CidsBean> cidsBeans = new ArrayList<>();
        for (int i = 0; i < modelIndices.length; i++) {
            cidsBeans.add(getCidsBeanByIndex(modelIndices[i]));
        }
        return cidsBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   modelIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getCidsBeanByIndex(final int modelIndex) {
        if (cidsBeans == null) {
            return null;
        }
        try {
            return (CidsBean)cidsBeans.get(modelIndex);
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("CidsBean at index " + modelIndex + " not found. will return null", e);
            }
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getIndexByCidsBean(final CidsBean cidsBean) {
        if (cidsBeans == null) {
            return -1;
        }
        try {
            return cidsBeans.indexOf(cidsBean);
        } catch (Exception e) {
            LOG.error("error in getIndexByCidsBean(). will return -1", e);
            return -1;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isInRowBounds(final int rowIndex) {
        return (rowIndex >= 0) && (rowIndex < getRowCount());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   columnIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isInColumnBounds(final int columnIndex) {
        return (columnIndex >= 0) && (columnIndex < getColumnCount());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void addCidsBean(final CidsBean cidsBean) {
        if (cidsBeans != null) {
            cidsBeans.add(cidsBean);
            fireTableDataChanged();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void removeCidsBean(final CidsBean cidsBean) {
        try {
            cidsBeans.remove(cidsBean);
            removedCidsBeans.add(cidsBean);
            fireTableDataChanged();
        } catch (Exception ex) {
            LOG.error("error while deleting bean", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getRemovedCidsBeans() {
        return new ArrayList<>(removedCidsBeans);
    }
}
