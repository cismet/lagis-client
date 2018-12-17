/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * NKFTableModel.java
 *
 * Created on 25. April 2007, 11:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import org.apache.log4j.Logger;

import java.util.*;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.beans.lagis.AnlageklasseCustomBean;
import de.cismet.cids.custom.beans.lagis.NutzungBuchungCustomBean;
import de.cismet.cids.custom.beans.lagis.NutzungCustomBean;
import de.cismet.cids.custom.beans.lagis.NutzungsartCustomBean;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.Exception.TerminateNutzungNotPossibleException;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.tools.CurrentStackTrace;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class NKFTableModel extends CidsBeanTableModel_Lagis {

    //~ Static fields/initializers ---------------------------------------------

    public static final int COLUMN_NUTZUNGS_NUMMER = 0;
    public static final int COLUMN_BUCHUNGS_NUMMER = 1;
    public static final int COLUMN_ANLAGEKLASSE = 2;
    public static final int COLUMN_NUTZUNGSART_SCHLUESSEL = 3;
    public static final int COLUMN_NUTZUNGSART_BEZEICHNUNG = 4;
    public static final int COLUMN_FLAECHE = 5;
    public static final int COLUMN_QUADRATMETER_PREIS = 6;
    public static final int COLUMN_GESAMT_PREIS = 7;
    public static final int COLUMN_STILLE_RESERVE = 8;
    public static final int COLUMN_BUCHWERT = 9;
    public static final int COLUMN_BEMERKUNG = 10;
    public static final int COLUMN_LAST = 11;

    private static final String[] COLUMN_NAMES = {
            "Nutzungs-Nr.",
            "Buchungs-Nr.",
            "Anlageklasse",
            "Nutzungsart",
            "Nutzungsarten-Bezeichnung",
            "Fläche/m²",
            "m²-Preis",
            "Gesamtpreis",
            "Stille Reserve",
            "Buchwert",
            "Bemerkung"
        };
    private static final Class[] COLUMN_CLASSES = {
            Integer.class,
            Integer.class,
            AnlageklasseCustomBean.class,
            String.class,
            NutzungsartCustomBean.class,
            Integer.class,
            Double.class,
            Double.class,
            Double.class,
            ImageIcon.class,
            String.class
        };
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(NKFTableModel.class);

    //~ Instance fields --------------------------------------------------------

    private Icon booked = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/nutzung/booked.png"));
    private Icon notBooked = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/nutzung/notBooked.png"));
    private ArrayList<NutzungCustomBean> allNutzungen;
    private Date currentDate = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NKFTableModel object.
     */
    public NKFTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES, NutzungBuchungCustomBean.class);
        allNutzungen = new ArrayList<>();
    }

    /**
     * This constructor is not used at the moment. Therefore it could not be tested.
     *
     * @param  buchungen  DOCUMENT ME!
     */
    public NKFTableModel(final Collection<NutzungBuchungCustomBean> buchungen) {
        super(COLUMN_NAMES, COLUMN_CLASSES, buchungen);
        try {
            allNutzungen = new ArrayList<>();
            for (final NutzungBuchungCustomBean buchung : buchungen) {
                allNutzungen.add(buchung.getNutzung());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl aller Nutzungen: " + allNutzungen.size());
            }
            currentDate = null;
            setModelToHistoryDate(currentDate);
        } catch (Exception ex) {
            LOG.error("Fehler beim anlegen des Models", ex);
            this.allNutzungen = new ArrayList<>();
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            if (getRowCount() == 0) {
                return null;
            }

            final NutzungBuchungCustomBean selectedBuchung = getCidsBeanAtRow(rowIndex);
            if (selectedBuchung == null) {
                return null;
            }

            final NutzungCustomBean nutzung = selectedBuchung.getNutzung();
            final Double stilleReserve = (nutzung != null) ? nutzung.getStilleReserveForBuchung(selectedBuchung) : null;
            switch (columnIndex) {
                case COLUMN_NUTZUNGS_NUMMER: {
                    return ((nutzung != null) && (nutzung.getId() != -1)) ? nutzung.getId() : null;
                }
                case COLUMN_BUCHUNGS_NUMMER: {
                    return (nutzung == null) ? null : nutzung.getBuchungsNummerForBuchung(selectedBuchung);
                }
                case COLUMN_ANLAGEKLASSE: {
                    return selectedBuchung.getAnlageklasse();
                }
                case COLUMN_NUTZUNGSART_SCHLUESSEL: {
                    return (selectedBuchung.getNutzungsart() != null) ? selectedBuchung.getNutzungsart()
                                    .getSchluessel() : null;
                }
                case COLUMN_NUTZUNGSART_BEZEICHNUNG: {
                    return selectedBuchung.getNutzungsart();
                }
                case COLUMN_FLAECHE: {
                    return selectedBuchung.getFlaeche();
                }
                case COLUMN_QUADRATMETER_PREIS: {
                    return selectedBuchung.getQuadratmeterpreis();
                }
                case COLUMN_GESAMT_PREIS: {
                    return selectedBuchung.getGesamtpreis() - ((stilleReserve != null) ? stilleReserve : 0);
                }
                case COLUMN_STILLE_RESERVE: {
                    return (stilleReserve != null) ? stilleReserve : 0.0;
                }
                case COLUMN_BUCHWERT: {
                    return selectedBuchung.getIstBuchwert() ? booked : notBooked;
                }
                case COLUMN_BEMERKUNG: {
                    return selectedBuchung.getBemerkung();
                }
                default: {
                    return "Spalte ist nicht definiert";
                }
            }
        } catch (final Exception ex) {
            LOG.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
            return null;
        }
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        if ((columnIndex == COLUMN_NUTZUNGS_NUMMER)
                    || (columnIndex == COLUMN_NUTZUNGSART_SCHLUESSEL)
                    || (columnIndex == COLUMN_BUCHUNGS_NUMMER)
                    || (columnIndex == COLUMN_BUCHWERT)
                    || (columnIndex == COLUMN_BEMERKUNG)
                    || (columnIndex == COLUMN_LAST)) {
            return false;
        } else {
            return (COLUMN_NAMES.length > columnIndex)
                        && (getRowCount() > rowIndex)
                        && isInEditMode();
                // && (LagisBroker.getInstance().isNkfAdminPermission());
        }
    }

    // ToDo beseitigen wenn abgebrochen wird ?? wird aber glaube ich neu geladen
    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        try {
            // ToDo NKF gibt nur eine NutzungCustomBean spezialfall
            NutzungBuchungCustomBean selectedBuchung = getCidsBeanAtRow(rowIndex);
            final NutzungCustomBean selectedNutzung = selectedBuchung.getNutzung();
            NutzungBuchungCustomBean oldBuchung = null;
            if ((selectedBuchung.getGueltigbis() == null) && !LagisBroker.getInstance().isNkfAdminPermission()) {
                if ((selectedBuchung.getId() != null) && (selectedBuchung.getId() != -1)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("neue Buchung wird angelegt");
                    }
                    final NutzungBuchungCustomBean newBuchung = selectedBuchung.cloneBuchung();
                    selectedNutzung.addBuchung(newBuchung);
                    oldBuchung = selectedBuchung;
                    selectedBuchung = newBuchung;
                } else {
                    oldBuchung = selectedNutzung.getPreviousBuchung();
                }
            } else if (selectedBuchung.getIstBuchwert() == true) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("historischer Buchwert wurde editiert");
                }
            }
            switch (columnIndex) {
                case COLUMN_ANLAGEKLASSE: {
                    if ((aValue != null) && (aValue instanceof String)) {
                        selectedBuchung.setAnlageklasse(null);
                        break;
                    }
                    selectedBuchung.setAnlageklasse((AnlageklasseCustomBean)aValue);
                    break;
                }
                case COLUMN_NUTZUNGSART_BEZEICHNUNG: {
                    if ((aValue != null) && (aValue instanceof String)) {
                        selectedBuchung.setNutzungsart(null);
                    } else {
                        selectedBuchung.setNutzungsart((NutzungsartCustomBean)aValue);
                    }
                    break;
                }
                case COLUMN_FLAECHE: {
//                    if (nutzung.getFlaeche() != null && nutzung.getQuadratmeterpreis() != null) {
//                        nutzung.setAlterGesamtpreis(nutzung.getFlaeche() * nutzung.getQuadratmeterpreis());
//                    }
                    selectedBuchung.setFlaeche((Integer)aValue);
                    break;
                }
                case COLUMN_QUADRATMETER_PREIS: {
//                    if (nutzung.getFlaeche() != null && nutzung.getQuadratmeterpreis() != null) {
//                        nutzung.setAlterGesamtpreis(nutzung.getFlaeche() * nutzung.getQuadratmeterpreis());
//                    }
                    selectedBuchung.setQuadratmeterpreis((Double)aValue);
                    break;
                }
                case COLUMN_BEMERKUNG: {
                    if ((aValue != null) && (aValue instanceof String) && (((String)aValue).length() == 0)) {
                        selectedBuchung.setBemerkung(null);
                        return;
                    }
                    selectedBuchung.setBemerkung((String)aValue);
                    break;
                }
                default: {
                    LOG.warn("Keine Spalte für angegebenen Index vorhanden: " + columnIndex);
                    return;
                }
            }
            if ((selectedBuchung != null)
                        && (oldBuchung != null)
                        && ((selectedBuchung.getId() == null) || (selectedBuchung.getId() == -1))) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Prüfe ob die Nutzung sich wirklich verändert hat");
                }
                if (NutzungBuchungCustomBean.NUTZUNG_HISTORY_EQUALATOR.pedanticEquals(oldBuchung, selectedBuchung)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Nutzungen sind gleich muss keine neue angelegt werden");
                    }
                    selectedNutzung.removeOpenNutzung();
                }
            }
            setModelToHistoryDate(currentDate);
        } catch (Exception ex) {
            LOG.error("Fehler beim setzen von Daten in dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  nutzung  DOCUMENT ME!
     */
    public void addNutzung(final NutzungCustomBean nutzung) {
        if (nutzung != null) {
            allNutzungen.add(nutzung);
            setModelToHistoryDate(currentDate);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Nutzung kann nicht hinzugefügt werden ist null.");
            }
        }
    }

    /**
     * This method removes the last NutzungBuchung from a Nutzung. A few cases have to be distinguished: if the Nutzung
     * only has one Buchung, and this Buchung should be removed, the entire Nutzung will be removed. If the
     * NutzungBuchung was already saved in the database, then the NutzungsBuchung will only become historical. Except
     * when the boolean <code>completeRemoval</code> is set, then the NutzungsBuchung will be deleted.
     *
     * @param  rowIndex         row index of the NutzungsBuchung
     * @param  completeRemoval  true, the NutzungsBuchung is removed, otherwise the NutzungsBuchung will become
     *                          historical
     */
    public void removeNutzungBuchung(final int rowIndex, final boolean completeRemoval) {
        try {
            removeNutzungBuchung_helper(rowIndex, completeRemoval);
        } catch (TerminateNutzungNotPossibleException ex) {
            LOG.error("Eine Nutzung konnte nicht entfernt werden", ex);
            final int result = JOptionPane.showConfirmDialog(LagisBroker.getInstance().getParentComponent(),
                    "Die Buchung konnte nicht entfernt werden, bitte wenden Sie \n"
                            + "sich an den Systemadministrator",
                    "Fehler beim löschen einer Buchung",
                    JOptionPane.OK_OPTION);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex         DOCUMENT ME!
     * @param   completeRemoval  DOCUMENT ME!
     *
     * @throws  TerminateNutzungNotPossibleException  DOCUMENT ME!
     */
    private void removeNutzungBuchung_helper(final int rowIndex, final boolean completeRemoval)
            throws TerminateNutzungNotPossibleException {
        final NutzungBuchungCustomBean selectedBuchung = (NutzungBuchungCustomBean)getCidsBeans()
                    .get(rowIndex);
        final NutzungCustomBean nutzungToRemove = selectedBuchung.getNutzung();
        // the first cases check if the NutzungsBuchung has already been saved in the database
        // and remove the NutzungsBuchung accordingly to the case
        if (nutzungToRemove != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Nutzung die entfernt werden soll ist in Modell vorhanden.");
            }
            if ((nutzungToRemove.getId() == null) || (nutzungToRemove.getId() == -1)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Nutzung die Entfernt wurde war noch nicht in Datenbank");
                }
                allNutzungen.remove(nutzungToRemove);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Nutzung ist in Datenbank vorhanden");
                }
                if ((selectedBuchung != null)
                            && ((selectedBuchung.getId() == null) || (selectedBuchung.getId() == -1))) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Die Betroffene Buchung ist neu und kann gelöscht werden");
                    }
                    nutzungToRemove.removeOpenNutzung();
                } else {
                    // the NutzungsBuchung is already in the database
                    // should it be completely removed or become historcal
                    if (completeRemoval) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                "Die Betroffene Buchung ist in der Datenbank gespeichert. Buchung komplett löschen");
                        }
                        if (nutzungToRemove.getBuchungsCount() > 1) {
                            nutzungToRemove.removeBuchungWithoutCreatingAHistory(selectedBuchung);
                        } else { // Nutzung only contains one Buchung, delete Nutzung
                            allNutzungen.remove(nutzungToRemove);
                        }
                    } else {     // do not remove Buchung, make it only historical
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                "Die Betroffene Buchung ist in der Datenbank gespeichert. Komplette Nutzung wird historisch gesetzt");
                        }
                        final Date terminationDate = new Date();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Termination date: " + terminationDate);
                        }
                        nutzungToRemove.terminateNutzung(terminationDate);
                    }
                }
            }
        }
        setModelToHistoryDate(currentDate);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<NutzungBuchungCustomBean> getAllBuchungen() {
        final ArrayList<NutzungBuchungCustomBean> sortedNutzungen = new ArrayList<>();
        for (final NutzungCustomBean curNutzung : allNutzungen) {
            if (curNutzung.getBuchungsCount() > 0) {
                for (final NutzungBuchungCustomBean curBuchung : curNutzung.getNutzungsBuchungen()) {
                    sortedNutzungen.add(curBuchung);
                }
            }
        }
        if (sortedNutzungen.size() > 0) {
            // ToDO NKF Comparator
            Collections.sort(sortedNutzungen, NutzungBuchungCustomBean.DATE_COMPARATOR);
        }
        return sortedNutzungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  <T>        DOCUMENT ME!
     * @param  nutzungen  DOCUMENT ME!
     */
    @Override
    public <T extends CidsBean> void refreshTableModel(final Collection<T> nutzungen) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Refresh des NKFTableModell");
            }

            if (nutzungen != null) {
                this.allNutzungen = (ArrayList<NutzungCustomBean>)new ArrayList<>(nutzungen);
            } else {
                allNutzungen.clear();
            }
            getTable().clearSelection();
            setModelToHistoryDate(currentDate);
        } catch (Exception ex) {
            LOG.error("Fehler beim refreshen des Models", ex);
            setCidsBeans(new ArrayList<NutzungBuchungCustomBean>());
            this.allNutzungen = new ArrayList<>();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<NutzungBuchungCustomBean> getOpenBuchungen() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Anzahl aller Nutzungen: " + allNutzungen.size());
        }
        final ArrayList<NutzungBuchungCustomBean> selectedBuchungen = new ArrayList<>();
        for (final NutzungCustomBean curNutzung : allNutzungen) {
            final NutzungBuchungCustomBean curBuchung = curNutzung.getOpenBuchung();
            if (curBuchung != null) {
                selectedBuchungen.add(curBuchung);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Anzahl offener Nutzungen: " + selectedBuchungen.size());
        }
        return selectedBuchungen;
    }

    /**
     * public Set<> getCurrentNutzungen() { final Set<> currentNutzungen = new HashSet<>(); for
     * (NutzungBuchungCustomBean curBuchung : currentBuchungen) { NutzungCustomBean curNutzung =
     * curBuchung.getNutzung(); if (curNutzung != null) { currentNutzungen.add(curNutzung); } } return currentNutzungen;
     * }
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<NutzungCustomBean> getAllNutzungen() {
        return allNutzungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  historyDate  DOCUMENT ME!
     */
    public final void setModelToHistoryDate(final Date historyDate) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("setModelToHistoryDate: " + historyDate, new CurrentStackTrace());
            LOG.debug("anzahl rows: " + getRowCount());
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("AnzahlNutzungen: " + allNutzungen.size());
        }

        boolean dateChanged = false;
        if ((currentDate == null) || (historyDate == null)) {
            if ((currentDate == null) && (historyDate == null)) {
                dateChanged = false; // both dates are null
            } else {                 // only one date is null
                dateChanged = true;
            }
        } else {                     // no date is null
            dateChanged = !currentDate.equals(historyDate);
        }

        currentDate = historyDate;
        clearCidsBeans();
        for (final NutzungCustomBean curNutzung : allNutzungen) {
            final Collection buchungenForDay = curNutzung.getBuchungForDate(historyDate, false);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl buchungen: " + buchungenForDay.size());
            }
            addAllCidsBeans(buchungenForDay);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("anzahl rows: " + getRowCount());
        }
        if (dateChanged) {
            getTable().clearSelection();
            this.fireTableDataChanged();
        } else {
            this.fireTableDataChangedAndKeepSelection();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getCurrentDate() {
        return currentDate;
    }
}
