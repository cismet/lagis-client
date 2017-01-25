/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.models;

import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Set;

import de.cismet.cids.custom.beans.lagis.KassenzeichenCustomBean;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class KassenzeichenTableModel extends CidsBeanTableModel_Lagis {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_NAMES = { "Kassenzeichen", "zugeordnet am" };
    private static final Class[] COLUMN_CLASSES = { Integer.class, Date.class };

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(KassenzeichenTableModel.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new KassenzeichenTableModel object.
     */
    public KassenzeichenTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES, KassenzeichenCustomBean.class);
    }

    /**
     * Creates a new instance of KassenzeichenTableModel.
     *
     * @param  kassenzeichen  DOCUMENT ME!
     */
    public KassenzeichenTableModel(final Set<KassenzeichenCustomBean> kassenzeichen) {
        super(COLUMN_NAMES, COLUMN_CLASSES, kassenzeichen);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("ausgewählte zeile/spalte" + rowIndex + "/" + columnIndex);
            }
            final KassenzeichenCustomBean vKassenzeichen = getCidsBeanAtRow(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return vKassenzeichen.getKassenzeichennummer();
                }
                case 1: {
                    return vKassenzeichen.getZugeordnet_am();
                }
                default: {
                    return "Spalte ist nicht definiert";
                }
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
            return null;
        }
    }
}
