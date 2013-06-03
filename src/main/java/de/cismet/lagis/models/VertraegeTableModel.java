/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * VertraegeTableModel.java
 *
 * Created on 25. April 2007, 13:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import org.apache.log4j.Logger;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.cids.custom.beans.lagis.VertragCustomBean;
import de.cismet.cids.custom.beans.lagis.VertragsartCustomBean;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.gui.tables.VertraegeTable;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class VertraegeTableModel extends CidsBeanTableModel_Lagis {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_NAMES = {
            "Vertragsart",
            "Aktenzeichen",
            "Quadratmeterpreis",
            "Kaufpreis (i. NK)"
        };

    private static final Class[] COLUMN_CLASSES = {
            VertragsartCustomBean.class,
            String.class,
            Double.class,
            Double.class,
        };

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(VertraegeTableModel.class);

    //~ Instance fields --------------------------------------------------------

    private DecimalFormat df = LagisBroker.getCurrencyFormatter();
    // Models

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of VertraegeTableModel.
     */
    public VertraegeTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES, VertragCustomBean.class);
        setCidsBeans(new ArrayList<VertragCustomBean>());
    }

    /**
     * Creates a new VertraegeTableModel object.
     *
     * @param  vertraege  DOCUMENT ME!
     */
    public VertraegeTableModel(final Collection<VertragCustomBean> vertraege) {
        super(COLUMN_NAMES, COLUMN_CLASSES, vertraege);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            final VertragCustomBean vertrag = (VertragCustomBean)getCidsBeanAtRow(rowIndex);
            switch (columnIndex) {
                case 0: {
                    final VertragsartCustomBean art = vertrag.getVertragsart();
                    if (art != null) {
                        return art.getBezeichnung();
                    } else {
                        return null;
                    }
                }
                case 1: {
                    return vertrag.getAktenzeichen();
                }
                case 2: {
                    final Double qPreis = vertrag.getQuadratmeterpreis();
                    return (qPreis != null) ? df.format(qPreis) : null;
                }
                case 3: {
                    final Double gPreis = vertrag.getGesamtpreis();
                    return (gPreis != null) ? df.format(gPreis) : null;
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

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return false;
    }

    @Override
    public void restoreBean(final CidsBean cidsBean) {
        super.restoreBean(cidsBean);
        ((VertraegeTable)getTable()).emulateMouseClicked();
    }
}
