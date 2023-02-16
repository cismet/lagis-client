/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.models;

import org.apache.log4j.Logger;

import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class MeldungenTableModel extends AbstractCidsBeanTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(MeldungenTableModel.class);
    private static final String[] COLUMN_HEADER = { "Datum", "Meldung", "Flurstück" };
    private static final Class[] COLUMN_CLASSES = { String.class, String.class, String.class };
    private static final DateFormat DF = new SimpleDateFormat("dd.MM.YYYY");

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MeldungenTableModel object.
     */
    public MeldungenTableModel() {
        super(COLUMN_HEADER, COLUMN_CLASSES);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean isCellEditable(final int i, final int i1) {
        return false;
    }

    @Override
    public Object getValueAt(final int row, final int column) {
        final CidsBean meldungBean = getCidsBeanByIndex(row);
        if (meldungBean == null) {
            return null;
        }
        switch (column) {
            case 0: {
                try {
                    final Timestamp timestamp = (Timestamp)meldungBean.getProperty("timestamp");
                    return (timestamp != null) ? DF.format(timestamp) : "-";
                } catch (Exception ex) {
                    LOG.warn("exception in tablemodel", ex);
                    return "<html><b><i>Fehler";
                }
            }
            case 1: {
                try {
                    return (String)meldungBean.getProperty("name");
                } catch (Exception ex) {
                    LOG.warn("exception in tablemodel", ex);
                    return "<html><b><i>Fehler";
                }
            }
            case 2: {
                try {
                    return ((CidsBean)meldungBean.getProperty("fk_flurstueck")).toString();
                } catch (final Exception ex) {
                    LOG.warn("exception in tablemodel", ex);
                    return "<html><i>Fehler";
                }
            }
            default:
        }
        return null;
    }
}
