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

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.StyledFeatureGroupWrapper;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.interfaces.FeatureSelectionChangedListener;

import de.cismet.lagis.models.CidsBeanTableModel_Lagis;

import de.cismet.lagis.widget.AbstractWidget;

import de.cismet.lagisEE.entity.basic.BasicEntity;

/**
 * Parent class of several Tables. It provides a NewItem action, a RemoveItem action and a SortItem Listener. These
 * actions can added to buttons, so that a similar behavior is shared between the subclasses. For example the Sort
 * ToggleButton is implemented that way. See also CidsBeanTable_Model, as there is usually a cross-reference between a
 * AbstractCidsBeanTable_Lagis and a CidsBeanTable_Model object.
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractCidsBeanTable_Lagis extends JXTable implements ListSelectionListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(AbstractCidsBeanTable_Lagis.class);

    //~ Instance fields --------------------------------------------------------

    private int previously_sorted_column_index = 0;
    private SortOrder previously_used_sort_order = SortOrder.ASCENDING;
    private JToggleButton tbtnSort;
    private JButton btnUndo;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractCidsBeanTable_Lagis object.
     */
    public AbstractCidsBeanTable_Lagis() {
        super();
        this.getSelectionModel().addListSelectionListener(this);
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
    public Action getUndoAction() {
        return new AbstractAction() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    btnUndoActionPerformed(e);
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
        if (tbtnSort != null) {
            tbtnSort.setSelected(true);
        }
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
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    private void btnUndoActionPerformed(final ActionEvent e) {
        ((CidsBeanTableModel_Lagis)getModel()).restoreSelectedCidsBean();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JButton getUndoButton() {
        return btnUndo;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  btnUndo  DOCUMENT ME!
     */
    public void setUndoButton(final JButton btnUndo) {
        this.btnUndo = btnUndo;
    }

    /**
     * Selection changed.
     *
     * @param  e  DOCUMENT ME!
     */
    @Override
    public void valueChanged(final ListSelectionEvent e) {
        super.valueChanged(e);
        if (btnUndo != null) {
            if (!((CidsBeanTableModel_Lagis)getModel()).isInEditMode() || (getSelectedRow() == -1)) {
                btnUndo.setEnabled(false);
            } else {
                btnUndo.setEnabled(true);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  panel  DOCUMENT ME!
     * @param  e      DOCUMENT ME!
     */
    public void valueChanged_updateFeatures(final FeatureSelectionChangedListener panel, final ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == true) {
            return;
        }

        panel.setFeatureSelectionChangedEnabled(false);
        final int[] selectedRows = this.getSelectedRows();
        final MappingComponent mappingComp = LagisBroker.getInstance().getMappingComponent();
        boolean firstIteration = true;
        for (final int row : this.getSelectedRows()) {
            final int index = this.getFilters().convertRowIndexToModel(row);
            if ((index != -1)) {
                final StyledFeature selectedBaum = ((CidsBeanTableModel_Lagis)getModel()).getCidsBeanAtRow(index);
                if ((selectedBaum.getGeometry() != null)) {
                    if (firstIteration) {
                        mappingComp.getFeatureCollection().select(selectedBaum);
                        firstIteration = false;
                    } else {
                        mappingComp.getFeatureCollection().addToSelection(selectedBaum);
                    }
                } else if (selectedRows.length == 1) { // if the only selected element has no feature
                    mappingComp.getFeatureCollection().unselectAll();
                }
            }
        }

        panel.setFeatureSelectionChangedEnabled(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  panel          DOCUMENT ME!
     * @param  features       DOCUMENT ME!
     * @param  cidsbeanClass  cidsbeanClass <C> DOCUMENT ME!
     */
    public void featureSelectionChanged(final ListSelectionListener panel,
            final Collection<Feature> features,
            final Class<? extends CidsBean> cidsbeanClass) {
        // Hint: features contain selected and deselected features
        if (features.isEmpty()) {
            return;
        }
        this.getSelectionModel().removeListSelectionListener(panel);
        Feature wrappedFeature;
        for (final Feature feature : features) {
            if (feature instanceof StyledFeatureGroupWrapper) {
                wrappedFeature = ((StyledFeatureGroupWrapper)feature).getFeature();
            } else {
                wrappedFeature = feature;
            }
            if (cidsbeanClass.isInstance(wrappedFeature)) {
                // TODO Refactor Name
                final int index = ((CidsBeanTableModel_Lagis)getModel()).getIndexOfCidsBean((CidsBean)wrappedFeature);
                final int displayedIndex = this.getFilters().convertRowIndexToView(index);
                if ((index != -1)
                            && LagisBroker.getInstance().getMappingComponent().getFeatureCollection().isSelected(
                                feature)) {
                    this.getSelectionModel().addSelectionInterval(displayedIndex, displayedIndex);
                    final Rectangle tmp = this.getCellRect(displayedIndex, 0, true);
                    if (tmp != null) {
                        this.scrollRectToVisible(tmp);
                    }
                } else {
                    this.getSelectionModel().removeSelectionInterval(displayedIndex, displayedIndex);
                }
            }
        }
        this.getSelectionModel().addListSelectionListener(panel);
    }
}
