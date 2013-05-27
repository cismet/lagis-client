/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.tables;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.SortOrder;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

import de.cismet.lagis.models.CidsBeanTableModel_Lagis;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractCidsBeanTable_Lagis extends JXTable {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(AbstractCidsBeanTable_Lagis.class);

    //~ Instance fields --------------------------------------------------------

    private int previously_sorted_column_index = 0;
    private SortOrder previously_used_sort_order = SortOrder.ASCENDING;
    private JToggleButton tbtnSort;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractCidsBeanTable_Lagis object.
     */
    public AbstractCidsBeanTable_Lagis() {
        super();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Action getAddAction() {
        return new AbstractAction() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    btnAddActionPerformed(e);
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Action getRemoveAction() {
        return new AbstractAction() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    btnRemoveActionPerformed(e);
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ItemListener getSortItemListener() {
        return new ItemListener() {

                @Override
                public void itemStateChanged(final ItemEvent evt) {
                    tbtnSortItemStateChanged(evt);
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    protected void btnAddActionPerformed(final ActionEvent evt) {
        getSortButton().setSelected(true);
        this.setSortable(false);
        addNewItem();
        if (SwingUtilities.isEventDispatchThread()) {
            selectAndScrollToLastRow();
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        selectAndScrollToLastRow();
                    }
                });
        }
        execAfterItemAdded();
    }

    /**
     * DOCUMENT ME!
     */
    private void selectAndScrollToLastRow() {
        final AbstractCidsBeanTable_Lagis table = AbstractCidsBeanTable_Lagis.this;
        table.setRowSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
        table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));
    }

    /**
     * This method gets called in btnAddActionPerformed().
     */
    protected abstract void addNewItem();

    /**
     * This method gets called at the end of btnAddActionPerformed(). It is empty so it does not need to be implemented
     * by the child classes.
     */
    protected void execAfterItemAdded() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    protected void btnRemoveActionPerformed(final ActionEvent evt) {
        final int currentRow = this.getSelectedRow();
        if (currentRow != -1) {
            removeItem(currentRow);
        }
        execAfterItemRemoved();
    }

    /**
     * This method gets called in btnRemoveActionPerformed(). Row is the number of the row, which should be removed.
     *
     * @param  row  DOCUMENT ME!
     */
    protected abstract void removeItem(int row);

    /**
     * This method gets called at the end of btnRemoveActionPerformed(). It is empty so it does not need to be
     * implemented by the child classes.
     */
    protected void execAfterItemRemoved() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    protected void tbtnSortItemStateChanged(final ItemEvent evt) {
        if (tbtnSort.isSelected()) { // disable sort
            previously_sorted_column_index = ((JXTable)this).getSortedColumn().getModelIndex();
            previously_used_sort_order = ((JXTable)this).getSortOrder(previously_sorted_column_index);
            ((JXTable)this).setSortable(false);
        } else {                     // sort the table
            ((JXTable)this).setSortable(true);
            ((JXTable)this).setSortOrder(previously_sorted_column_index, previously_used_sort_order);
        }
        this.scrollRectToVisible(this.getCellRect(this.getSelectedRow(), 0, true));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  sortButton  DOCUMENT ME!
     */
    public void setSortButton(final JToggleButton sortButton) {
        tbtnSort = sortButton;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JToggleButton getSortButton() {
        return tbtnSort;
    }

    /**
     * Additional functionality for CidsBeanTableModel_Lagis models. If newModel is a subclass of
     * CidsBeanTableModel_Lagis, then this table is added to the newModel.
     *
     * @param  newModel  DOCUMENT ME!
     */
    // TODO change this when every Table extends from this Table. The instanceof check is propably dangerous and
    // causes side effects a programmer can not see immediately.
    @Override
    public void setModel(final TableModel newModel) {
        super.setModel(newModel);
        if (newModel instanceof CidsBeanTableModel_Lagis) {
            ((CidsBeanTableModel_Lagis)newModel).setTable(this);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Table was also added to model.");
        }
    }
}
