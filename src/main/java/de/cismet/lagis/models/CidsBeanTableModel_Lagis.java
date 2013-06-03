/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.models;

import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.gui.tables.AbstractCidsBeanTable_Lagis;
import de.cismet.lagis.gui.tables.CidsBeanSupport;

/**
 * Parent class of several TableModels. See also AbstractCidsBeanTable_Lagis, as there is usually a cross-reference
 * between a AbstractCidsBeanTable_Lagis and a CidsBeanTable_Model object.
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public abstract class CidsBeanTableModel_Lagis extends AbstractTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(CidsBeanTableModel_Lagis.class);

    //~ Instance fields --------------------------------------------------------

    private List<? extends CidsBean> cidsBeans;
    private final Map<Integer, CidsBean> beanBackups = new HashMap<Integer, CidsBean>();
    private final String[] columnNames;
    private final Class[] columnClasses;
    private boolean inEditMode = false;
    private AbstractCidsBeanTable_Lagis table;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CidsBeanTableModel object.
     *
     * @param  <T>            DOCUMENT ME!
     * @param  columnNames    DOCUMENT ME!
     * @param  columnClasses  DOCUMENT ME!
     * @param  cidsBeanClass  DOCUMENT ME!
     */
    protected <T extends CidsBean> CidsBeanTableModel_Lagis(final String[] columnNames,
            final Class[] columnClasses,
            final Class<T> cidsBeanClass) {
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
        this.cidsBeans = new ArrayList<T>();
    }

    /**
     * Creates a new CidsBeanTableModel_Lagis object.
     *
     * @param  <T>            DOCUMENT ME!
     * @param  columnNames    DOCUMENT ME!
     * @param  columnClasses  DOCUMENT ME!
     * @param  cidsBeans      DOCUMENT ME!
     */
    protected <T extends CidsBean> CidsBeanTableModel_Lagis(final String[] columnNames,
            final Class[] columnClasses,
            final Collection<T> cidsBeans) {
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
        try {
            this.cidsBeans = new ArrayList<T>(cidsBeans);
            for (final T bean : cidsBeans) {
                backupBean(bean);
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim anlegen des Models", ex);
            this.cidsBeans = new ArrayList<T>();
        }
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

    /**
     * DOCUMENT ME!
     *
     * @param  <C>       DOCUMENT ME!
     * @param  cidsbean  DOCUMENT ME!
     */
    public <C extends CidsBean> void addCidsBean(final C cidsbean) {
        ((List<CidsBean>)cidsBeans).add(cidsbean);
        backupBean(cidsbean);
        fireTableDataChangedAndKeepSelection();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rowIndex  DOCUMENT ME!
     */
    public void removeCidsBean(final int rowIndex) {
        unbackupBean(cidsBeans.get(rowIndex));
        cidsBeans.remove(rowIndex);
        fireTableDataChanged();
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
     * @param   <C>       DOCUMENT ME!
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public <C extends CidsBean> int getIndexOfCidsBean(final C cidsBean) {
        return cidsBeans.indexOf(cidsBean);
    }

    /**
     * DOCUMENT ME!
     */
    public void clearCidsBeans() {
        cidsBeans.clear();
        clearBackups();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  <C>           DOCUMENT ME!
     * @param  newCidsBeans  DOCUMENT ME!
     */
    public <C extends CidsBean> void addAllCidsBeans(final Collection<C> newCidsBeans) {
        ((List<C>)cidsBeans).addAll(newCidsBeans);
        for (final CidsBean bean : cidsBeans) {
            backupBean(bean);
        }
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
     * @param  <T>        DOCUMENT ME!
     * @param  cidsBeans  DOCUMENT ME!
     */
    public <T extends CidsBean> void setCidsBeans(final List<T> cidsBeans) {
        clearBackups();
        this.cidsBeans = cidsBeans;

        for (final T bean : cidsBeans) {
            backupBean(bean);
        }

        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isInEditMode() {
        return inEditMode;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isInEditMode  DOCUMENT ME!
     */
    public void setInEditMode(final boolean isInEditMode) {
        this.inEditMode = isInEditMode;
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return (columnNames.length > columnIndex) && (cidsBeans.size() > rowIndex) && inEditMode;
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
     * @param  <T>        beschluesse DOCUMENT ME!
     * @param  cidsbeans  DOCUMENT ME!
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
        final int selection_index_view = table.getSelectedRow();

        int selection_model_tmp = -1;
        if (selection_index_view > -1) {
            selection_model_tmp = table.convertRowIndexToModel(selection_index_view);
        }
        final int selection_index_model = selection_model_tmp;

        this.fireTableDataChanged();

        // does the same thing, only point of time changes
        if (SwingUtilities.isEventDispatchThread()) {
            resetSelection(selection_index_model);
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        resetSelection(selection_index_model);
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selection_index_model  DOCUMENT ME!
     */
    private void resetSelection(final int selection_index_model) {
        if ((selection_index_model == -1) || (selection_index_model >= this.getRowCount())) {
            table.clearSelection();
        } else {
            final int selection_index_view = table.convertRowIndexToView(selection_index_model);
            table.setRowSelectionInterval(selection_index_view, selection_index_view);
            table.scrollRectToVisible(table.getCellRect(selection_index_view, 0, true));
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void clearBackups() {
        beanBackups.clear();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public final void backupBean(final CidsBean cidsBean) {
        try {
            final int id = (Integer)cidsBean.getProperty("id");
            final CidsBean backupBean = CidsBeanSupport.deepcloneCidsBean(cidsBean);
            beanBackups.put(id, backupBean);
        } catch (Exception ex) {
            LOG.error("error while making backup of bean", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void unbackupBean(final CidsBean cidsBean) {
        beanBackups.remove((Integer)cidsBean.getProperty("id"));
    }

    /**
     * DOCUMENT ME!
     */
    public void restoreSelectedCidsBean() {
        final CidsBean cidsBean = getCidsBeanAtRow(table.convertRowIndexToModel(table.getSelectedRow()));
        restoreBean(cidsBean);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void restoreBean(final CidsBean cidsBean) {
        try {
            final CidsBean backupBean = beanBackups.get((Integer)cidsBean.getProperty("id"));
            CidsBeanSupport.copyAllProperties(backupBean, cidsBean);
            fireTableDataChangedAndKeepSelection();
        } catch (Exception ex) {
            LOG.error("error while making backup of bean", ex);
        }
    }
}
