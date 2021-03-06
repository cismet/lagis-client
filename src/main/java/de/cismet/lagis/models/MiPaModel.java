/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import org.apache.log4j.Logger;

import java.util.*;
import java.util.Date;

import javax.swing.text.BadLocationException;

import de.cismet.cids.custom.beans.lagis.MipaCustomBean;
import de.cismet.cids.custom.beans.lagis.MipaKategorieCustomBean;
import de.cismet.cids.custom.beans.lagis.MipaNutzungCustomBean;

import de.cismet.cismap.commons.features.Feature;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.models.documents.SimpleDocumentModel;

import de.cismet.lagisEE.entity.extension.vermietung.MiPa;
import de.cismet.lagisEE.entity.extension.vermietung.MiPaKategorie;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class MiPaModel extends CidsBeanTableModel_Lagis {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_HEADER = {
            "Lage",
            "Aktenzeichen",
            "Fläche m²",
            "Nutzung",
            "Nutzer",
            "Vertragsbeginn",
            "Vertragsende",
        };

    private static final Class[] COLUMN_CLASSES = {
            String.class,
            String.class,
            Integer.class,
            MiPaKategorie.class,
            String.class,
            Date.class,
            Date.class
        };

    public static final int LAGE_COLUMN = 0;
    public static final int AKTENZEICHEN_COLUMN = 1;
    public static final int FLAECHE_COLUMN = 2;
    public static final int NUTZUNG_COLUMN = 3;
    public static final int NUTZER_COLUMN = 4;
    public static final int VERTRAGS_BEGINN_COLUMN = 5;
    public static final int VERTRAGS_ENDE_COLUMN = 6;

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(MiPaModel.class);

    //~ Instance fields --------------------------------------------------------

    private SimpleDocumentModel bemerkungDocumentModel;
    private MiPa currentSelectedMiPa = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MiPaModel object.
     */
    public MiPaModel() {
        super(COLUMN_HEADER, COLUMN_CLASSES, MipaCustomBean.class);
        initDocumentModels();
    }

    /**
     * Creates a new MiPaModel object.
     *
     * @param  miPas  DOCUMENT ME!
     */
    public MiPaModel(final Collection<MipaCustomBean> miPas) {
        super(COLUMN_HEADER, COLUMN_CLASSES, miPas);
        initDocumentModels();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            if (rowIndex >= getRowCount()) {
                LOG.warn("Cannot access row " + rowIndex + ". There are just " + getRowCount() + " rows");
                return null;
            }

            final MiPa value = getCidsBeanAtRow(rowIndex);

            switch (columnIndex) {
                case LAGE_COLUMN: {
                    return value.getLage();
                }
                case AKTENZEICHEN_COLUMN: {
                    return value.getAktenzeichen();
                }
                case FLAECHE_COLUMN: {
                    if (value.getFlaeche() != null) {
                        return value.getFlaeche().intValue();
                    } else {
                        return null;
                    }
                }
                case NUTZUNG_COLUMN: {
                    if (value.getMiPaNutzung() != null) {
                        return value.getMiPaNutzung().getMiPaKategorie();
                    } else {
                        return null;
                    }
                }
                case NUTZER_COLUMN: {
                    return value.getNutzer();
                }
                case VERTRAGS_BEGINN_COLUMN: {
                    return value.getVertragsbeginn();
                }
                case VERTRAGS_ENDE_COLUMN: {
                    return value.getVertragsende();
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
            final MipaCustomBean miPa = getCidsBeanAtRow(rowIndex);
            if ((miPa != null) && (miPa.getGeometry() != null)) {
                LagisBroker.getInstance().getMappingComponent().getFeatureCollection().removeFeature(miPa);
            }
            miPa.delete();
            super.removeCidsBean(rowIndex);
        } catch (final Exception ex) {
            LOG.error("An error occurred while removing MiPa from MipaTableModel", ex);
        }
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return (COLUMN_HEADER.length > columnIndex) && (getRowCount() > rowIndex) && isInEditMode();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<Feature> getAllMiPaFeatures() {
        final Collection<Feature> mipaFeatures = new ArrayList<>();
        if (getCidsBeans() != null) {
            for (final MipaCustomBean curMiPa : (Collection<MipaCustomBean>)getCidsBeans()) {
                if (curMiPa.getGeometry() != null) {
                    mipaFeatures.add(curMiPa);
                }
            }
            return mipaFeatures;
        } else {
            return null;
        }
    }

    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        try {
            final MiPa value = getCidsBeanAtRow(rowIndex);
            switch (columnIndex) {
                case LAGE_COLUMN: {
                    value.setLage((String)aValue);
                    break;
                }
                case AKTENZEICHEN_COLUMN: {
                    value.setAktenzeichen((String)aValue);
                    break;
                }
                case FLAECHE_COLUMN: {
                    if (aValue != null) {
                        value.setFlaeche(((Integer)aValue).doubleValue());
                    } else {
                        value.setFlaeche(null);
                    }
                    break;
                }
                case NUTZUNG_COLUMN: {
                    if (value.getMiPaNutzung() == null) {
                        value.setMiPaNutzung(MipaNutzungCustomBean.createNew());
                        value.getMiPaNutzung().setMiPaKategorie((MipaKategorieCustomBean)aValue);
                    } else {
                        MiPaKategorie oldKategory = null;
                        if (((oldKategory = value.getMiPaNutzung().getMiPaKategorie()) != null) && (aValue != null)) {
                            if (!oldKategory.equals(aValue)) {
                                value.getMiPaNutzung().setAusgewaehlteNummer(null);
                            }
                        }
                        value.getMiPaNutzung().setMiPaKategorie((MipaKategorieCustomBean)aValue);
                    }
                    break;
                }
                case NUTZER_COLUMN: {
                    value.setNutzer((String)aValue);
                    break;
                }
                case VERTRAGS_BEGINN_COLUMN: {
                    if ((aValue instanceof Date) || (aValue == null)) {
                        value.setVertragsbeginn((Date)aValue);
                    } // else if(aValue == null){
//                        value.setVertragsbeginn(null);
//                    }
                    break;
                }
                case VERTRAGS_ENDE_COLUMN: {
                    if ((aValue instanceof Date) || (aValue == null)) {
                        value.setVertragsende((Date)aValue);
                    }
                    break;
                }
                default: {
                    LOG.warn("Keine Spalte für angegebenen Index vorhanden: " + columnIndex);
                    return;
                }
            }
            fireTableDataChanged();
        } catch (Exception ex) {
            LOG.error("Fehler beim setzen von Daten in dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initDocumentModels() {
        bemerkungDocumentModel = new SimpleDocumentModel() {

                @Override
                public void assignValue(final String newValue) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Bemerkung assigned");
                        LOG.debug("new Value: " + newValue);
                    }
                    valueToCheck = newValue;
                    fireValidationStateChanged(this);
                    if ((currentSelectedMiPa != null) && (getStatus() == VALID)) {
                        currentSelectedMiPa.setBemerkung(newValue);
                    }
                }
            };
    }

    /**
     * DOCUMENT ME!
     */
    public void clearSlaveComponents() {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Clear Slave Components");
            }
            bemerkungDocumentModel.clear(0, bemerkungDocumentModel.getLength());
        } catch (Exception ex) {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SimpleDocumentModel getBemerkungDocumentModel() {
        return bemerkungDocumentModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  newMiPa  DOCUMENT ME!
     */
    public void setCurrentSelectedMipa(final MiPa newMiPa) {
        currentSelectedMiPa = newMiPa;
        if (currentSelectedMiPa != null) {
            try {
                bemerkungDocumentModel.clear(0, bemerkungDocumentModel.getLength());
                bemerkungDocumentModel.insertString(0, currentSelectedMiPa.getBemerkung(), null);
            } catch (BadLocationException ex) {
                // TODO Böse
                LOG.error("Fehler beim setzen des BemerkungsModells: ", ex);
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("nichts selektiert lösche Felder");
            }
            clearSlaveComponents();
        }
    }
}
