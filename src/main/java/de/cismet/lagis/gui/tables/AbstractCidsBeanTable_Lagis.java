/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.tables;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.SortOrder;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToggleButton;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractCidsBeanTable_Lagis extends JXTable {

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
    protected abstract void btnAddActionPerformed(ActionEvent evt);

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    protected abstract void btnRemoveActionPerformed(ActionEvent evt);

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
}
