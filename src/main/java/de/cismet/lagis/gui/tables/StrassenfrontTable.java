/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.tables;

import org.apache.log4j.Logger;

import java.util.Set;

import de.cismet.cids.custom.beans.lagis.StrassenfrontCustomBean;

import de.cismet.lagis.models.CidsBeanTableModel_Lagis;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public class StrassenfrontTable extends AbstractCidsBeanTable_Lagis {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(StrassenfrontTable.class);
    private static final String[] COLUMN_NAMES = { "Straße", "Länge (in m)" };
    private static final Class[] COLUMN_CLASSES = {
            String.class,
            Double.class
        };

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void addNewItem() {
        try {
            final StrassenfrontCustomBean strassenfront = StrassenfrontCustomBean.createNew();
            ((Model)getModel()).addCidsBean(strassenfront);
            fireItemAdded();
        } catch (Exception ex) {
            LOG.error("error creating bean for strassenfront", ex);
        }
    }

    @Override
    protected void removeItem(final int modelRow) {
        ((Model)getModel()).removeCidsBean(modelRow);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class Model extends CidsBeanTableModel_Lagis {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new Model object.
         */
        public Model() {
            super(COLUMN_NAMES, COLUMN_CLASSES, StrassenfrontCustomBean.class);
        }

        /**
         * Creates a new Model object.
         *
         * @param  strassenfronten  DOCUMENT ME!
         */
        public Model(final Set<StrassenfrontCustomBean> strassenfronten) {
            super(COLUMN_NAMES, COLUMN_CLASSES, strassenfronten);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ausgewählte zeile/spalte" + rowIndex + "/" + columnIndex);
                }
                final StrassenfrontCustomBean vStrassenfront = getCidsBeanAtRow(rowIndex);
                switch (columnIndex) {
                    case 0: {
                        return vStrassenfront.getStrassenname();
                    }

                    case 1: {
                        return vStrassenfront.getLaenge();
                    }

                    default: {
                        return "Spalte ist nicht definiert";
                    }
                }
            } catch (Exception ex) {
                LOG.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex,
                    ex);
                return null;
            }
        }

        @Override
        public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
            try {
                final StrassenfrontCustomBean vStrassenfront = getCidsBeanAtRow(rowIndex);
                switch (columnIndex) {
                    case 0: {
                        vStrassenfront.setStrassenname((String)aValue);
                        break;
                    }
                    case 1: {
                        vStrassenfront.setLaenge((Double)aValue);
                        break;
                    }
                    default: {
                        LOG.warn("Keine Spalte für angegebenen Index vorhanden: " + columnIndex);
                        return;
                    }
                }
                fireTableDataChangedAndKeepSelection();
            } catch (Exception ex) {
                LOG.error("Fehler beim setzen von Daten in dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex,
                    ex);
            }
        }
    }
}
