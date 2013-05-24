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

import java.text.DecimalFormat;

import java.util.*;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.beans.lagis.AnlageklasseCustomBean;
import de.cismet.cids.custom.beans.lagis.BebauungCustomBean;
import de.cismet.cids.custom.beans.lagis.FlaechennutzungCustomBean;
import de.cismet.cids.custom.beans.lagis.NutzungBuchungCustomBean;
import de.cismet.cids.custom.beans.lagis.NutzungCustomBean;
import de.cismet.cids.custom.beans.lagis.NutzungsartCustomBean;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.Exception.TerminateNutzungNotPossibleException;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.gui.panels.NKFPanel;

import de.cismet.lagis.util.TableSelectionUtils;

import de.cismet.lagis.utillity.BebauungsVector;
import de.cismet.lagis.utillity.FlaechennutzungsVector;

import de.cismet.tools.CurrentStackTrace;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class NKFTableModel extends CidsBeanTableModel_Lagis {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_NAMES = {
            "Nutzungs Nr.",
            "Buchungsnummer",
            "Anlageklasse",
            "Nutzungsartenschlüssel",
            "Nutzungsart",
            "Flächennutzungsplan",
            "Bebauungsplan",
            "Fläche m²",
            "Quadratmeterpreis",
            "Gesamtpreis",
            "Stille Reserve",
            "Buchwert",
            "Bemerkung"
        };

    private static final Class[] COLUMN_CLASSES = {
            Integer.class,
            Integer.class,
            AnlageklasseCustomBean.class,
            NutzungsartCustomBean.class,
            String.class,
            Vector.class,
            Vector.class,
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
    private Icon statusUnknown = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/lagis/ressource/icons/nutzung/statusUnknown.png"));
    private ArrayList<NutzungCustomBean> allNutzungen;
    // ToDo Selection über Datum noch nicht ganz optimal weil sehr oft im EDT benutzt und kostspielig
    private ArrayList<NutzungBuchungCustomBean> currentBuchungen;
    private DecimalFormat df = LagisBroker.getCurrencyFormatter();
    private Date currentDate = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of NKFTableModel.
     */
    public NKFTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES);
        allNutzungen = new ArrayList<NutzungCustomBean>();
        currentBuchungen = new ArrayList<NutzungBuchungCustomBean>();
        // nutzungenHistorisch = new Vector<Nutzung>();
    }

    /**
     * Creates a new NKFTableModel object.
     *
     * @param  nutzungen  DOCUMENT ME!
     */
    public NKFTableModel(final Collection<NutzungCustomBean> nutzungen) {
        super(COLUMN_NAMES, COLUMN_CLASSES);
        try {
            allNutzungen = new ArrayList<NutzungCustomBean>(nutzungen);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl aller Nutzungen: " + allNutzungen.size());
            }
            currentBuchungen = new ArrayList<NutzungBuchungCustomBean>();
            currentDate = null;
            setModelToHistoryDate(currentDate);
        } catch (Exception ex) {
            LOG.error("Fehler beim anlegen des Models", ex);
            this.allNutzungen = new ArrayList<NutzungCustomBean>();
            this.currentBuchungen = new ArrayList<NutzungBuchungCustomBean>();
            // this.nutzungenHistorisch = new Vector<Nutzung>();
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            if (currentBuchungen.isEmpty()) {
                return null;
            }

            final NutzungBuchungCustomBean selectedBuchung = currentBuchungen.get(rowIndex);
//            final NutzungBuchungCustomBean selectedBuchung = nutzung.getBuchungForExactDate(currentDate);
            final NutzungCustomBean nutzung = (selectedBuchung != null) ? selectedBuchung.getNutzung() : null;
            final Double stilleReserve = (nutzung != null) ? nutzung.getStilleReserveForBuchung(selectedBuchung) : null;
            switch (columnIndex) {
                case 0: {
                    if (nutzung != null) {
                        return (nutzung.getId() == -1) ? null : nutzung.getId();
                    } else {
                        return null;
                    }
                }
                case 1: {
                    if (nutzung != null) {
                        return nutzung.getBuchungsNummerForBuchung(selectedBuchung);
                    } else {
                        return null;
                    }
                }
                case 2: {
                    return selectedBuchung.getAnlageklasse();
                }
                case 3: {
                    return selectedBuchung.getNutzungsart();
                }
                case 4: {
                    if ((selectedBuchung.getNutzungsart() != null)
                                && (selectedBuchung.getNutzungsart().getBezeichnung() != null)) {
                        return selectedBuchung.getNutzungsart().getBezeichnung();
                    } else {
                        return null;
                    }
                }
                case 5: {
                    // TODO Special GUI for editing
                    if (selectedBuchung.getFlaechennutzung() != null) {
                        return new FlaechennutzungsVector(selectedBuchung.getFlaechennutzung());
                    } else {
                        return new FlaechennutzungsVector();
                    }
                }
                case 6: {
                    if (selectedBuchung.getBebauung() != null) {
                        return new BebauungsVector(selectedBuchung.getBebauung());
                    } else {
                        return new BebauungsVector();
                    }
                }
                case 7: {
                    return selectedBuchung.getFlaeche();
                }
                case 8: {
                    return selectedBuchung.getQuadratmeterpreis();
                }
                case 9: {
                    if (stilleReserve != null) {
                        return selectedBuchung.getGesamtpreis() - stilleReserve;
                    } else {
                        // ToDo NKF
                        return selectedBuchung.getGesamtpreis();
                    }
                }
                case 10: {
                    // ToDo NKF
                    if (stilleReserve != null) {
                        return stilleReserve;
                    } else {
                        return 0.0;
                    }
                }
                case 11: {
                    // ToDo gibt so wenig Buchwerte extra Spalte dafür ?
                    if (selectedBuchung.getIstBuchwert()) {
                        return booked;
                    } else {
                        return notBooked;
                    }
                }
                case 12: {
                    return selectedBuchung.getBemerkung();
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
    public int getRowCount() {
        return currentBuchungen.size();
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        if ((columnIndex == 0) || (columnIndex == 1) || (columnIndex == 4) || (columnIndex == 9) || (columnIndex == 10)
                    || (columnIndex == 11)) {
            return false;
        } else {
            return (COLUMN_NAMES.length > columnIndex)
                        && (currentBuchungen.size() > rowIndex)
                        && isIsInEditMode();
                // && (LagisBroker.getInstance().isNkfAdminPermission());
        }
    }

    // ToDo beseitigen wenn abgebrochen wird ?? wird aber glaube ich neu geladen
    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        try {
            // ToDo NKF gibt nur eine NutzungCustomBean spezialfall
            final NutzungCustomBean selectedNutzung = currentBuchungen.get(rowIndex).getNutzung();
            NutzungBuchungCustomBean selectedBuchung = currentBuchungen.get(rowIndex);
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
                case 2: {
                    if ((aValue != null) && (aValue instanceof String)) {
                        selectedBuchung.setAnlageklasse(null);
                        break;
                    }
                    selectedBuchung.setAnlageklasse((AnlageklasseCustomBean)aValue);
                    break;
                }
                case 3: {
                    if ((aValue != null) && (aValue instanceof String)) {
                        selectedBuchung.setNutzungsart(null);
                        break;
                    }
                    selectedBuchung.setNutzungsart((NutzungsartCustomBean)aValue);
                    break;
                }
                case 5: {
                    Collection<FlaechennutzungCustomBean> tmpNutz = selectedBuchung.getFlaechennutzung();
                    if (tmpNutz != null) {
                        tmpNutz.clear();
                    } else {
                        selectedBuchung.setFlaechennutzung(new HashSet<FlaechennutzungCustomBean>());
                        tmpNutz = selectedBuchung.getFlaechennutzung();
                    }
                    // tmpNutz.addAll((Collection<Flaechennutzung>) aValue);
                    final Iterator<FlaechennutzungCustomBean> itF = ((Collection<FlaechennutzungCustomBean>)aValue)
                                .iterator();
                    while (itF.hasNext()) {
                        final FlaechennutzungCustomBean fNutzung = itF.next();
                        if (fNutzung.getBezeichnung() != null) {
                            tmpNutz.add(fNutzung);
                        }
                    }
                    selectedBuchung.setFlaechennutzung(tmpNutz);
                    break;
                }
                case 6: {
                    Collection<BebauungCustomBean> tmpBebauung = selectedBuchung.getBebauung();
                    if (tmpBebauung != null) {
                        tmpBebauung.clear();
                    } else {
                        selectedBuchung.setBebauung(new HashSet<BebauungCustomBean>());
                        tmpBebauung = selectedBuchung.getBebauung();
                    }
                    // tmpNutz.addAll((Collection<Flaechennutzung>) aValue);
                    final Iterator<BebauungCustomBean> itB = ((Collection<BebauungCustomBean>)aValue).iterator();
                    while (itB.hasNext()) {
                        final BebauungCustomBean bebauung = itB.next();
                        if (bebauung.getBezeichnung() != null) {
                            tmpBebauung.add(bebauung);
                        }
                    }
                    selectedBuchung.setBebauung(tmpBebauung);
                    break;
                }
                case 7: {
//                    if (nutzung.getFlaeche() != null && nutzung.getQuadratmeterpreis() != null) {
//                        nutzung.setAlterGesamtpreis(nutzung.getFlaeche() * nutzung.getQuadratmeterpreis());
//                    }
                    selectedBuchung.setFlaeche((Integer)aValue);
                    break;
                }
                case 8: {
//                    if (nutzung.getFlaeche() != null && nutzung.getQuadratmeterpreis() != null) {
//                        nutzung.setAlterGesamtpreis(nutzung.getFlaeche() * nutzung.getQuadratmeterpreis());
//                    }
                    selectedBuchung.setQuadratmeterpreis((Double)aValue);
                    break;
                }
                case 12: {
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
     * DOCUMENT ME!
     *
     * @param   rowIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public NutzungBuchungCustomBean getBuchungAtRow(final int rowIndex) {
        try {
            return currentBuchungen.get(rowIndex);
        } catch (IndexOutOfBoundsException ex) {
            LOG.warn("Achtung der abgefragete wert ist nicht in dem Modell vorhanden", ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex  DOCUMENT ME!
     *
     * @throws  TerminateNutzungNotPossibleException  DOCUMENT ME!
     */
    public void removeNutzung(final int rowIndex) throws TerminateNutzungNotPossibleException {
        final NutzungBuchungCustomBean selectedBuchung = currentBuchungen.get(rowIndex);
        final NutzungCustomBean nutzungToRemove = currentBuchungen.get(rowIndex).getNutzung();
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
        setModelToHistoryDate(currentDate);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<NutzungBuchungCustomBean> getAllBuchungen() {
        final ArrayList<NutzungBuchungCustomBean> sortedNutzungen = new ArrayList<NutzungBuchungCustomBean>();
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
                this.allNutzungen = (ArrayList<NutzungCustomBean>)new ArrayList<T>(nutzungen);
            } else {
                allNutzungen.clear();
            }
            setModelToHistoryDate(currentDate);
        } catch (Exception ex) {
            LOG.error("Fehler beim refreshen des Models", ex);
            this.currentBuchungen = new ArrayList<NutzungBuchungCustomBean>();
            this.allNutzungen = new ArrayList<NutzungCustomBean>();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<NutzungBuchungCustomBean> getCurrentBuchungen() {
        return currentBuchungen;
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
        final ArrayList<NutzungBuchungCustomBean> selectedBuchungen = new ArrayList<NutzungBuchungCustomBean>();
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
     * public Set<Nutzung> getCurrentNutzungen() { final Set<Nutzung> currentNutzungen = new HashSet<Nutzung>(); for
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
        // TODO wann ändert sich currentDate, nur dann Selection halten?
        boolean dateChanged = false;
        if (currentDate != null) {
            dateChanged = currentDate.equals(historyDate);
        }
        currentDate = historyDate;
        currentBuchungen.clear();
        for (final NutzungCustomBean curNutzung : allNutzungen) {
            final Collection buchungenForDay = curNutzung.getBuchungForDay(historyDate);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl buchungen: " + buchungenForDay.size());
            }
            currentBuchungen.addAll(buchungenForDay);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("anzahl rows: " + getRowCount());
        }
        if (dateChanged) {
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

    /**
     * DOCUMENT ME!
     *
     * @param   buchung  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getIndexOfBuchung(final NutzungBuchungCustomBean buchung) {
        return currentBuchungen.indexOf(buchung);
    }

    /**
     * Methods which must be overriden as this class uses two Arraylists to save its data.
     *
     * @param  <C>       DOCUMENT ME!
     * @param  cidsbean  DOCUMENT ME!
     */
    // TODO Jean fragen
    @Override
    public <C extends CidsBean> void addCidsBean(final C cidsbean) {
        addNutzung((NutzungCustomBean)cidsbean);
    }

    @Override
    public void removeCidsBean(final int rowIndex) {
        try {
            removeNutzung(rowIndex);
        } catch (TerminateNutzungNotPossibleException ex) {
            LOG.error("Eine Nutzung konnte nicht entfernt werden", ex);
            final int result = JOptionPane.showConfirmDialog(LagisBroker.getInstance().getParentComponent(),
                    "Die Buchung konnte nicht entfernt werden, bitte wenden Sie \n"
                            + "sich an den Systemadministrator",
                    "Fehler beim löschen einer Buchung",
                    JOptionPane.OK_OPTION);
        }
    }

    @Override
    public <C extends CidsBean> C getCidsBeanAtRow(final int rowIndex) {
        return (C)getBuchungAtRow(rowIndex);
    }

    @Override
    public List<? extends CidsBean> getCidsBeans() {
        return getAllNutzungen();
    }

    @Override
    public void setCidsBeans(final List<? extends CidsBean> cidsBeans) {
        refreshTableModel(cidsBeans);
    }
}
