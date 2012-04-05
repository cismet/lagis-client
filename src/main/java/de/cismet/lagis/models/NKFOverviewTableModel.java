/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * NKFOverviewTableModel.java
 *
 * Created on 24. April 2007, 11:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import org.apache.log4j.Logger;

import java.text.DecimalFormat;

import java.util.*;

import javax.swing.table.AbstractTableModel;

import de.cismet.cids.custom.beans.lagis.NutzungBuchungCustomBean;
import de.cismet.cids.custom.beans.lagis.NutzungCustomBean;

import de.cismet.lagis.Exception.BuchungNotInNutzungException;
import de.cismet.lagis.Exception.IllegalNutzungStateException;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.utillity.AnlagenklasseSumme;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class NKFOverviewTableModel extends AbstractTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_HEADER = { "Anlageklasse", "Summe" };

    //~ Instance fields --------------------------------------------------------

    private ArrayList<NutzungCustomBean> nutzungen = new ArrayList<NutzungCustomBean>();
    private ArrayList<AnlagenklasseSumme> data = new ArrayList<AnlagenklasseSumme>();
    private DecimalFormat df = LagisBroker.getCurrencyFormatter();
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private Date currentDate = null;
    private double stilleReserve = 0.0;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of NKFOverviewTableModel.
     */
    public NKFOverviewTableModel() {
        nutzungen = new ArrayList<NutzungCustomBean>();
    }

    /**
     * Creates a new NKFOverviewTableModel object.
     *
     * @param  nutzungen  DOCUMENT ME!
     */
    public NKFOverviewTableModel(final ArrayList<NutzungCustomBean> nutzungen) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Konstruktor Nutzungen");
            }
            this.nutzungen = new ArrayList<NutzungCustomBean>(nutzungen);
            calculateSum();
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            this.nutzungen = new ArrayList<NutzungCustomBean>();
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  nutzungen  DOCUMENT ME!
     */
    public synchronized void refreshModel(final Collection<NutzungCustomBean> nutzungen) {
        if (nutzungen != null) {
            refreshModel(new ArrayList<NutzungCustomBean>(nutzungen));
        } else {
            refreshModel(new ArrayList());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  nutzungen  DOCUMENT ME!
     */
    public synchronized void refreshModel(final ArrayList<NutzungCustomBean> nutzungen) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Refresh Nutzungen");
            }
            this.nutzungen = new ArrayList<NutzungCustomBean>(nutzungen);
            calculateSum();
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Models", ex);
            this.nutzungen = new ArrayList<NutzungCustomBean>();
        }

        fireTableDataChanged();
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        if (data.isEmpty()) {
            return null;
        }
        try {
            final AnlagenklasseSumme summe = data.get(rowIndex);

            switch (columnIndex) {
                case 0: {
                    return summe.getAnlageklasse().getSchluessel();
                }
                case 1: {
                    return df.format(summe.getSumme());
                }
                default: {
                    return "Spalte ist nicht definiert";
                }
            }
        } catch (Exception ex) {
            log.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
            return null;
        }
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_HEADER.length;
    }

    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        super.setValueAt(aValue, rowIndex, columnIndex);
    }

    /**
     * DOCUMENT ME!
     */
    private synchronized void calculateSum() {
        if (log.isDebugEnabled()) {
            log.debug("Calculate Sum");
        }
        stilleReserve = 0.0;
        data = new ArrayList<AnlagenklasseSumme>();
        for (final NutzungCustomBean currentNutzung : nutzungen) {
            if (log.isDebugEnabled()) {
                log.debug("curNutzung:" + currentNutzung);
                log.debug("tableModelDate:" + currentDate);
            }
            final NutzungBuchungCustomBean currentBuchung = currentNutzung.getBuchungForDate(currentDate);
            if (currentBuchung != null) {
                if (log.isDebugEnabled()) {
                    log.debug("currentBuchung: " + currentBuchung);
                }
                if ((currentBuchung.getAnlageklasse() != null) && (currentBuchung.getGesamtpreis() != null)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Anlageklasse & Gesamtpreis != null");
                    }
                    final int index = 0;
                    final Iterator<AnlagenklasseSumme> itAS = data.iterator();
                    // System.out.println(data.size());
                    // System.out.println("currentNutz:"+currentNutzung.getAnlageklasse().getSchluessel());
                    boolean isAlreadyInVector = false;
                    // Das hier ist Käse Code dupliziert;
                    while (itAS.hasNext()) {
                        if (log.isDebugEnabled()) {
                            log.debug("vektor nicht leer");
                        }
                        final AnlagenklasseSumme curSumme = itAS.next();
                        if (curSumme.equals(Double.NaN)) {
                            if (log.isDebugEnabled()) {
                                log.debug(
                                    "Bei der Berechnung der Summen ist ein Fehler aufgetreten: Keine weitere Berechnung");
                            }
                            continue;
                        }
                        if (curSumme.equals(currentBuchung.getAnlageklasse())) {
                            if (log.isDebugEnabled()) {
                                log.debug("Element der anlagensumme vorhanden");
                            }

                            // ToDo NKF muss behandelt werden und dem Benutzer mitgeteilt werden
                            try {
                                final Double curStilleReseve = currentNutzung.getStilleReserveForBuchung(
                                        currentBuchung);
                                if (curStilleReseve != null) {
                                    stilleReserve += curStilleReseve;
                                    curSumme.setSumme(curSumme.getSumme()
                                                + (currentBuchung.getGesamtpreis() - curStilleReseve));
                                }
                            } catch (BuchungNotInNutzungException ex) {
                                log.error("Stille Reserve konnte nicht berechnet werden: Fehlerhalfte Buchung");
                                stilleReserve = Double.NaN;
                                curSumme.setSumme(Double.NaN);
                            } catch (IllegalNutzungStateException ex) {
                                log.error("Stille Reserve konnte nicht berechnet werden: Kein Buchwert");
                                stilleReserve = Double.NaN;
                                curSumme.setSumme(Double.NaN);
                            }

                            isAlreadyInVector = true;
                        }
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("nach while");
                    }
                    if (!isAlreadyInVector) {
                        if (log.isDebugEnabled()) {
                            log.debug("Element der anlagensumme hinzugefügt");
                        }
                        final AnlagenklasseSumme tmp = new AnlagenklasseSumme(currentBuchung.getAnlageklasse());
                        try {
                            final Double curStilleReseve = currentNutzung.getStilleReserveForBuchung(currentBuchung);
                            // ToDo NKF
                            if (curStilleReseve != null) {
                                stilleReserve += curStilleReseve;
                                tmp.setSumme((currentBuchung.getGesamtpreis()) - curStilleReseve);
                            }
                        } catch (BuchungNotInNutzungException ex) {
                            log.error("Stille Reserve konnte nicht berechnet werden: Fehlerhalfte Buchung");
                            tmp.setSumme(Double.NaN);
                        } catch (IllegalNutzungStateException ex) {
                            log.error("Stille Reserve konnte nicht berechnet werden: Kein Buchwert");
                            tmp.setSumme(Double.NaN);
                        }

                        data.add(tmp);
                    }
                }
            }
        }
        Collections.sort((ArrayList)data);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getStilleReserve() {
        return stilleReserve;
    }

//    private synchronized void updateStilleReservenBetrag(NutzungsContainer nutzungen) {
//        boolean containsHistoricNutzung = false;
//        if (nutzungen != null) {
//            Iterator<Nutzung> it = nutzungen.iterator();
//            double stilleReservenSumme = 0.0;
//            while (it.hasNext()) {
//                NutzungCustomBean currentNutzung = it.next();
//                //TODO NKF Benutzer muss benachrichtigt werden
//                try {
////                if (tableModel.hasNutzungSuccessor(currentNutzung)) {
////                    log.debug("NutzungCustomBean hat einen Nachfolger und wird für Stille Reserve nicht berücksichtigt");
////                    continue;
////                } else {
////                    log.debug("NutzungCustomBean hat keinen Nachfolger --> wird für Stille Reserve berücksichtigt");
////                }
//                    if (currentNutzung.getStilleReserve() != null) {
//                        stilleReservenSumme += currentNutzung.getStilleReserve();
//                    }
//                } catch (IllegalNutzungStateException ex) {
//                    log.error("Stille Reserve konnte nicht berechnet werden.",ex);
//                }
//                //ToDo NKF
////                if (currentNutzung.getGueltigbis() != null) {
////                    containsHistoricNutzung = true;
////                }
//            }
////            if (containsHistoricNutzung) {
////                lblStilleReservenBetrag.setText("-/-");
////                btnBuchen.setEnabled(false);
////            } else {
//            lblStilleReservenBetrag.setText(LagisBroker.getCurrencyFormatter().format(stilleReservenSumme));
//            if (stilleReservenSumme != 0.0 && LagisBroker.getInstance().isInEditMode()) {
//                if (!containsHistoricNutzung) {
//                    btnBuchen.setEnabled(true);
//                } else {
//                    btnBuchen.setEnabled(false);
//                }
//            } else {
//                btnBuchen.setEnabled(false);
//            }
////            }
//            if(containsHistoricNutzung){
//                lblHistoricIcon.setIcon(icoHistoricIcon);
//            } else {
//                lblHistoricIcon.setIcon(icoHistoricIconDummy);
//            }
//        } else {
//            lblStilleReservenBetrag.setText(LagisBroker.getCurrencyFormatter().format(0.0));
//            btnBuchen.setEnabled(false);
//        }
//    }

//    public boolean containsHistoricNutzung() {
//        Iterator<Nutzung> it = nutzungen.iterator();
//        while (it.hasNext()) {
//            NutzungCustomBean currentNutzung = it.next();
//            if (currentNutzung.getGueltigbis() != null) {
//                System.out.println("NutzungCustomBean hat einen Nachfolger");
//                return true;
//            }
//        }
//        return false;
//    }

    @Override
    public String getColumnName(final int column) {
        return COLUMN_HEADER[column];
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<NutzungCustomBean> getAllNutzungen() {
        return nutzungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  currentDate  DOCUMENT ME!
     */
    public void setCurrentDate(final Date currentDate) {
        this.currentDate = currentDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getCurrentDate() {
        return currentDate;
    }

//    @Override
//    public Class<?> getColumnClass(int columnIndex) {
//        return getValueAt(0,columnIndex).getClass();
//    }

}
