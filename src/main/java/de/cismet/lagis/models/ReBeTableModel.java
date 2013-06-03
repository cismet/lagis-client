/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * ReBeTableModel.java
 *
 * Created on 25. April 2007, 09:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;

import java.util.*;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import de.cismet.cids.custom.beans.lagis.RebeArtCustomBean;
import de.cismet.cids.custom.beans.lagis.RebeCustomBean;

import de.cismet.cismap.commons.features.Feature;

import de.cismet.lagis.broker.LagisBroker;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class ReBeTableModel extends CidsBeanTableModel_Lagis {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_HEADER = {
            "ist Recht",
            "Art",
            "Art des Rechts",
            "Nummer",
            "Eintragung Datum",
            "Löschung Datum",
            "Bemerkung"
        };
    private static final Class[] COLUMN_CLASSES = {
            Boolean.class,
            RebeArtCustomBean.class,
            String.class,
            String.class,
            Date.class,
            Date.class,
            String.class
        };
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(ReBeTableModel.class);

    //~ Instance fields --------------------------------------------------------

    private boolean isReBeKindSwitchAllowed = true;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of ReBeTableModel.
     */
    public ReBeTableModel() {
        super(COLUMN_HEADER, COLUMN_CLASSES, RebeCustomBean.class);
    }

    /**
     * Creates a new ReBeTableModel object.
     *
     * @param  reBe  DOCUMENT ME!
     */
    public ReBeTableModel(final Collection<RebeCustomBean> reBe) {
        super(COLUMN_HEADER, COLUMN_CLASSES, reBe);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            final RebeCustomBean value = getCidsBeanAtRow(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return value.getIstRecht();
                }
                case 1: {
                    return value.getReBeArt();
                }
                case 2: {
                    return value.getBeschreibung();
                }
                case 3: {
                    return value.getNummer();
                }
                case 4: {
                    return value.getDatumEintragung();
                }
                case 5: {
                    return value.getDatumLoeschung();
                }
                case 6: {
                    return value.getBemerkung();
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

    /**
     * DOCUMENT ME!
     *
     * @param  rowIndex  DOCUMENT ME!
     */
    @Override
    public void removeCidsBean(final int rowIndex) {
        try {
            final RebeCustomBean reBe = getCidsBeanAtRow(rowIndex);
            if ((reBe != null) && (reBe.getGeometry() != null)) {
                LagisBroker.getInstance().getMappingComponent().getFeatureCollection().removeFeature(reBe);
            }
            // TODO: Benni: remove CidsBean.delete() call, if removal from ObservedList is sufficient
            reBe.delete();
            super.removeCidsBean(rowIndex);
        } catch (final Exception ex) {
            LOG.error("An error occurred while removing ReBe from RebeTableModel", ex);
        }
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        if (columnIndex == 0) {
            return (COLUMN_HEADER.length > columnIndex) && (getRowCount() > rowIndex) && isIsInEditMode()
                        && isReBeKindSwitchAllowed;
        } else {
            return (COLUMN_HEADER.length > columnIndex) && (getRowCount() > rowIndex) && isIsInEditMode();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<Feature> getAllReBeFeatures() {
        final ArrayList<Feature> tmp = new ArrayList<Feature>();
        final ArrayList<RebeCustomBean> resBes = (ArrayList<RebeCustomBean>)getCidsBeans();
        if (resBes != null) {
            final Iterator<RebeCustomBean> it = resBes.iterator();
            while (it.hasNext()) {
                final RebeCustomBean curReBe = it.next();
                if (curReBe.getGeometry() != null) {
                    tmp.add(curReBe);
                }
            }
            return tmp;
        } else {
            return null;
        }
    }

    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        try {
            final RebeCustomBean value = getCidsBeanAtRow(rowIndex);
            switch (columnIndex) {
                case 0: {
                    value.setIstRecht((Boolean)aValue);
                    break;
                }
                case 1: {
                    value.setReBeArt((RebeArtCustomBean)aValue);
                    break;
                }
                case 2: {
                    value.setBeschreibung((String)aValue);
                    break;
                }
                case 3: {
                    value.setNummer((String)aValue);
                    break;
                }
                case 4: {
                    if ((aValue instanceof Date) || (aValue == null)) {
                        value.setDatumEintragung((Date)aValue);
                    }
                    break;
                }
                case 5: {
                    if ((aValue instanceof Date) || (aValue == null)) {
                        value.setDatumLoeschung((Date)aValue);
                    }
                    break;
                }
                case 6: {
                    value.setBemerkung((String)aValue);
                    break;
                }
                default: {
                    LOG.warn("Keine Spalte für angegebenen Index vorhanden: " + columnIndex);
                    return;
                }
            }
            fireTableDataChangedAndKeepSelection();
        } catch (Exception ex) {
            LOG.error("Fehler beim setzen von Daten in dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isIsReBeKindSwitchAllowed() {
        return isReBeKindSwitchAllowed;
    }

    /**
     * isReBeKindSwitchAllowed seems to have always the opposite value as ReBePanel.isInAbteilungIXModus.
     *
     * @param  isReBeKindSwitchAllowed  DOCUMENT ME!
     */
    // TODO Jean fragen warum das so ist?
    public void setIsReBeKindSwitchAllowed(final boolean isReBeKindSwitchAllowed) {
        this.isReBeKindSwitchAllowed = isReBeKindSwitchAllowed;
    }
}
