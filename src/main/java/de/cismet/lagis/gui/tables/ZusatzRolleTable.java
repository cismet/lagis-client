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

import de.cismet.cids.custom.beans.lagis.VerwaltendeDienststelleCustomBean;
import de.cismet.cids.custom.beans.lagis.ZusatzRolleArtCustomBean;
import de.cismet.cids.custom.beans.lagis.ZusatzRolleCustomBean;

import de.cismet.lagis.models.CidsBeanTableModel_Lagis;

/**
 * DOCUMENT ME!
 *
 * @author   gbaatz
 * @version  $Revision$, $Date$
 */
public class ZusatzRolleTable extends AbstractCidsBeanTable_Lagis {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(ZusatzRolleTable.class);
    private static final String[] COLUMN_NAMES = { "Dienststelle", "Rolle" };
    private static final Class[] COLUMN_CLASSES = {
            VerwaltendeDienststelleCustomBean.class,
            ZusatzRolleArtCustomBean.class
        };

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void addNewItem() {
        try {
            final ZusatzRolleCustomBean zusatzRolle = ZusatzRolleCustomBean.createNew();
            ((Model)getModel()).addCidsBean(zusatzRolle);
            fireItemAdded();
        } catch (Exception ex) {
            LOG.error("error creating bean for zusatzrolle", ex);
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
         * Creates a new ZusatzRolleTableModel object.
         */
        public Model() {
            super(COLUMN_NAMES, COLUMN_CLASSES, ZusatzRolleCustomBean.class);
        }

        /**
         * Creates a new instance of ZusatzRolleTableModel.
         *
         * @param  zusatzRollen  DOCUMENT ME!
         */
        public Model(final Set<ZusatzRolleCustomBean> zusatzRollen) {
            super(COLUMN_NAMES, COLUMN_CLASSES, zusatzRollen);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ausgewählte zeile/spalte" + rowIndex + "/" + columnIndex);
                }
                final ZusatzRolleCustomBean vZusatzRolle = getCidsBeanAtRow(rowIndex);
                switch (columnIndex) {
                    case 0: {
                        return vZusatzRolle.getFk_dienststelle();
                    }

                    case 1: {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("aktueller Gebrauch: " + vZusatzRolle.getFk_art());
                        }
                        return vZusatzRolle.getFk_art();
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
                final ZusatzRolleCustomBean vZusatzRolle = getCidsBeanAtRow(rowIndex);
                switch (columnIndex) {
                    case 0: {
                        vZusatzRolle.setFk_dienststelle((VerwaltendeDienststelleCustomBean)aValue);
                        break;
                    }
                    case 1: {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Setze Wert: " + aValue);
                        }
                        vZusatzRolle.setFk_art((ZusatzRolleArtCustomBean)aValue);
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
