/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * EJBroker.java
 *
 * Created on 19. April 2007, 08:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.broker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import de.cismet.cids.custom.beans.verdis_grundis.*;

import de.cismet.lagis.Exception.ActionNotSuccessfulException;

import de.cismet.lagis.cidsmigtest.CidsAppBackend;

import de.cismet.lagisEE.bean.LagisServer;

import de.cismet.lagisEE.crossover.entity.WfsFlurstuecke;

import de.cismet.lagisEE.interfaces.Key;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
//ToDo Benutzernamen in Server auslagern --> hat hier nichts verloren
public final class EJBroker {

    //~ Static fields/initializers ---------------------------------------------

    private static EJBroker brokerInstance = null;
    private static LagisServer lagisEJBServerStub;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EJBroker.class);
//    private static JFrame parentFrame;
//    private static JDialog brokenConnectionDialog;
//    private static EJBReconnectorPanel EJBReconnectorPanel;
//    private static JOptionPane brokenConnectionOptionPane;
//    private static EJBConnector EJBConnectorWorker;
//    private static JFrame dialogOwner;
//    private static InitialContext ic;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of EJBroker.
     */
    private EJBroker() {
//        initBrokenConnectionDialog();
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Lookup des LagisEJB");
            }
            lagisEJBServerStub = new LagisServer();
        } catch (Throwable ex) {
            LOG.fatal("Fehler beim Verbinden mit Glassfish.\nFehler beim initialisieren/lookup des EJBs", ex);
//            brokenConnectionDialog.setVisible(true);
        }
        CidsAppBackend.init();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
// private void initBrokenConnectionDialog() {
// if (dialogOwner == null) {
// EJBReconnectorPanel = new EJBReconnectorPanel();
// if ((parentFrame != null) && parentFrame.isVisible()) {
// if (LOG.isDebugEnabled()) {
// LOG.debug("frame wurde gesetzt");
// }
// dialogOwner = parentFrame;
// } else {
// if (LOG.isDebugEnabled()) {
// LOG.debug("frame wurde nicht gesetzt");
// }
// dialogOwner = null;
// }
// brokenConnectionOptionPane = new JOptionPane(
// "Es konnte keine Verbindung zum LagiS Server hergestellt werden.\n Was Möchten Sie tun ?",
// JOptionPane.QUESTION_MESSAGE,
// JOptionPane.NO_OPTION,
// null,
// new Object[] { EJBReconnectorPanel });
// brokenConnectionDialog = new JDialog(dialogOwner,
// "Fehler beim Verbinden mit dem LagIS Server",
// true);
// brokenConnectionDialog.setContentPane(brokenConnectionOptionPane);
// brokenConnectionDialog.setDefaultCloseOperation(
// JDialog.DISPOSE_ON_CLOSE);
// brokenConnectionDialog.addWindowListener(new WindowAdapter() {
//
// @Override
// public void windowClosing(final WindowEvent we) {
// if (LOG.isDebugEnabled()) {
// LOG.debug("User hat versucht, das Panel zu schließen");
// }
// }
// });
//
// EJBReconnectorPanel.getBtnExitCancel().addActionListener(new ActionListener() {
//
// @Override
// public void actionPerformed(final ActionEvent e) {
// if (EJBReconnectorPanel.getBtnExitCancel().getText().equals("Abbrechen")) {
// EJBReconnectorPanel.getBtnExitCancel().setEnabled(false);
// if ((EJBConnectorWorker != null) && !EJBConnectorWorker.isDone()) {
// if (LOG.isDebugEnabled()) {
// LOG.debug("EjbConnector wird abgebrochen");
// }
// EJBConnectorWorker.cancel(false);
// EJBConnectorWorker = null;
// } else {
// LOG.warn("EjbConnector läuft nicht");
// EJBReconnectorPanel.resetPanel();
// }
// if (LOG.isDebugEnabled()) {
// LOG.debug("Verbindungsvorgang abgebrochen");
// }
// } else {
// if (LOG.isDebugEnabled()) {
// LOG.debug("Kein Verbindungsvorgang am laufen --> LagIS wird beendet");
// }
// shutdownLagIS();
// }
// }
// });
//
// EJBReconnectorPanel.getBtnRetry().addActionListener(new ActionListener() {
//
// @Override
// public void actionPerformed(final ActionEvent e) {
// EJBReconnectorPanel.getPb().setIndeterminate(true);
// if (LOG.isDebugEnabled()) {
// // ejbReconnectorPanel.getPb().setVisible(true);
// LOG.debug("vor Thread Start");
// }
// EJBConnectorWorker = new EJBConnector();
// // EJBConnectorWorker.execute();
// LagisBroker.getInstance().execute(EJBConnectorWorker);
// if (LOG.isDebugEnabled()) {
// LOG.debug("nach Thread start");
// }
// // ejbReconnectorPanel.getLblMessage().setText("Versuche zu verbinden...");
// brokenConnectionOptionPane.setMessage("Versuche zu verbinden...");
// EJBReconnectorPanel.getBtnExitCancel().setText("Abbrechen");
// EJBReconnectorPanel.getBtnRetry().setVisible(false);
// }
// });
// brokenConnectionDialog.pack();
// }
// EJBReconnectorPanel.getPb().setIndeterminate(false);
//
// if ((parentFrame == null) || !parentFrame.isVisible()) {
// final Dimension oldDim = brokenConnectionDialog.getSize();
// final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
// brokenConnectionDialog.setBounds((int)((screen.getWidth() / 2) - oldDim.getWidth()),
// (int)((screen.getHeight() / 2) - oldDim.getHeight()),
// (int)oldDim.getWidth(),
// (int)oldDim.getHeight());
// } else if ((parentFrame != null) && parentFrame.isVisible()) {
// brokenConnectionDialog.setLocationRelativeTo(parentFrame);
// }
// // ejbReconnectorPanel.getPb().setVisible(false);
// }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
// private void shutdownLagIS() {
// if ((parentFrame != null) && parentFrame.isVisible()) {
// final int result = JOptionPane.showConfirmDialog(
// brokenConnectionDialog,
// "Sind Sie sicher, das Sie LagIS beenden wollen?\nAlle nicht gespeicherten Änderungen gehen verloren!",
// "Bestätigung",
// JOptionPane.YES_NO_OPTION);
// if (result == JOptionPane.YES_OPTION) {
// ((JFrame)parentFrame).dispose();
// } else {
// if (LOG.isDebugEnabled()) {
// LOG.debug("beenden abgebrochen");
// }
// }
// } else {
// System.exit(1);
// }
// }

    /**
     * synchronized because the lookup take some time and multiple calls causes multiple lookups --> exception.
     *
     * @return  DOCUMENT ME!
     */
    public static synchronized EJBroker getInstance() {
        if (brokerInstance == null) {
            brokerInstance = new EJBroker();
        }
        return brokerInstance;
    }

    /**
     * private Integer blocker = new Integer(0); //TODO im Moment nur inital was wenn lagisEJB != null private void
     * isConnected(){ if(lagisEJB != null){ return; } else { synchronized(blocker){ if(lagisEJB == null){ boolean
     * shouldRetry = true; while(shouldRetry){ shouldRetry = JOptionPane.showConfirmDialog(null, "Es besteht keine
     * Verbindung zum Server möchten Sie es weiterversuchen ?\n(Nein beendet die Applikation und alle nichtgespeicherten
     * Daten gehen verloren)","Server Verbindung",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION; if
     * (shouldRetry){ try { log.debug("Versuche Verbindung zum Server herzustellen"); InitialContext ic = new
     * InitialContext(); log.debug("Initial Kontext komplett"); lagisEJB = (LagisServerRemote)
     * ic.lookup("de.cismet.lagisEE.bean.LagisServerRemote"); log.debug("Lookup des LagisEJB erfolgreich"); return; }
     * catch (Throwable ex) { log.fatal("Fehler beim initialisieren/lookup des EJBs",ex); } } else {
     * log.debug("Applikation wird auf Benutzerwunsch beendet --> Keine Serververbindung"); System.exit(10); } } } }; }
     * }.
     *
     * @param  flurstueckHistorie  DOCUMENT ME!
     */
    public void createFlurstueckHistoryEntry(final FlurstueckHistorieCustomBean flurstueckHistorie) {
        lagisEJBServerStub.createFlurstueckHistoryEntry(flurstueckHistorie);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getCurrentDate() {
        return lagisEJBServerStub.getCurrentDate();
    }

    /**
     * public void createNutzungsHistoryEntry(NutzungHistorie nutzungsHistorie) {
     * lagisEJB.createNutzungsHistoryEntry(nutzungsHistorie); }.
     *
     * @param   flurstueck  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void modifyFlurstueck(final FlurstueckCustomBean flurstueck) throws ActionNotSuccessfulException {
        flurstueck.getFlurstueckSchluessel().setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        flurstueck.getFlurstueckSchluessel().setLetzte_bearbeitung(getCurrentDate());
        lagisEJBServerStub.modifyFlurstueck(flurstueck);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key  DOCUMENT ME!
     */
    public void modifyFlurstueckSchluessel(final FlurstueckSchluesselCustomBean key) {
        key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        key.setLetzte_bearbeitung(getCurrentDate());
        lagisEJBServerStub.modifyFlurstueckSchluessel(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueck  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void deleteFlurstueck(final FlurstueckCustomBean flurstueck) throws ActionNotSuccessfulException {
        lagisEJBServerStub.deleteFlurstueck(flurstueck);
    }

    /**
     * public void createFlurstueck(FlurstueckCustomBean flurstueck) { lagisEJB.create(flurstueck); }.
     *
     * @return  DOCUMENT ME!
     */
    public Collection<GemarkungCustomBean> getGemarkungsKeys() {
        return lagisEJBServerStub.getGemarkungsKeys();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public HashMap<Integer, GemarkungCustomBean> getGemarkungsHashMap() {
        final Collection<GemarkungCustomBean> gemarkungen = (Collection)lagisEJBServerStub.getGemarkungsKeys();
        if (gemarkungen != null) {
            final HashMap<Integer, GemarkungCustomBean> result = new HashMap<Integer, GemarkungCustomBean>();
            for (final GemarkungCustomBean gemarkung : gemarkungen) {
                if ((gemarkung != null) && (gemarkung.getBezeichnung() != null)
                            && (gemarkung.getSchluessel() != null)) {
                    result.put(gemarkung.getSchluessel(), gemarkung);
                }
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<Key> getDependingKeysForKey(final Key key) {
        return lagisEJBServerStub.getDependingKeysForKey(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckCustomBean retrieveFlurstueck(final FlurstueckSchluesselCustomBean key) {
        return lagisEJBServerStub.retrieveFlurstueck(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<AnlageklasseCustomBean> getAllAnlageklassen() {
        return lagisEJBServerStub.getAllAnlageklassen();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VertragsartCustomBean> getAllVertragsarten() {
        return lagisEJBServerStub.getAllVertragsarten();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<MipaKategorieCustomBean> getAllMiPaKategorien() {
        return lagisEJBServerStub.getAllMiPaKategorien();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BaumKategorieCustomBean> getAllBaumKategorien() {
        return lagisEJBServerStub.getAllBaumKategorien();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   newSperre  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SperreCustomBean createLock(final SperreCustomBean newSperre) {
        return lagisEJBServerStub.createLock(newSperre);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   sperre  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean releaseLock(final SperreCustomBean sperre) {
        return lagisEJBServerStub.releaseLock(sperre);
    }

    /**
     * TODO.
     *
     * @param   gem  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public GemarkungCustomBean completeGemarkung(final GemarkungCustomBean gem) {
        return lagisEJBServerStub.completeGemarkung(gem);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VerwaltungsgebrauchCustomBean> getAllVerwaltenungsgebraeuche() {
        return lagisEJBServerStub.getAllVerwaltenungsgebraeuche();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VerwaltendeDienststelleCustomBean> getAllVerwaltendeDienstellen() {
        return lagisEJBServerStub.getAllVerwaltendeDienstellen();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<RebeArtCustomBean> getAllRebeArten() {
        return lagisEJBServerStub.getAllRebeArten();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<NutzungsartCustomBean> getAllNutzungsarten() {
        return lagisEJBServerStub.getAllNutzungsarten();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BeschlussartCustomBean> getAllBeschlussarten() {
        return lagisEJBServerStub.getAllBeschlussarten();
    }

    /**
     * public Collection<Kostenart> getAllKostenarten() { //return lagisEJB.getAll(); }.
     *
     * @return  DOCUMENT ME!
     */
    public Collection<KostenCustomBean> getAllKostenarten() {
        return lagisEJBServerStub.getAllKostenarten();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlaechennutzungCustomBean> getAllFlaechennutzungen() {
        return lagisEJBServerStub.getAllFlaechennutzungen();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<MipaMerkmalCustomBean> getAllMiPaMerkmale() {
        return lagisEJBServerStub.getAllMiPaMerkmale();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BaumMerkmalCustomBean> getAllBaumMerkmale() {
        return lagisEJBServerStub.getAllBaumMerkmale();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BebauungCustomBean> getAllBebauungen() {
        return lagisEJBServerStub.getAllBebauungen();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckArtCustomBean> getAllFlurstueckArten() {
        return lagisEJBServerStub.getAllFlurstueckArten();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueckSchluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckSchluesselCustomBean completeFlurstueckSchluessel(
            final FlurstueckSchluesselCustomBean flurstueckSchluessel) {
        return lagisEJBServerStub.completeFlurstueckSchluessel(flurstueckSchluessel);
    }

//    /**
//     * DOCUMENT ME!
//     *
//     * @param  aMainframe  DOCUMENT ME!
//     */
//    public static void setMainframe(final JFrame aMainframe) {
//        parentFrame = aMainframe;
//    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckCustomBean createFlurstueck(final FlurstueckSchluesselCustomBean key) {
        key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        key.setLetzte_bearbeitung(getCurrentDate());
        return lagisEJBServerStub.createFlurstueck(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isFlurstueckHistoric(final FlurstueckSchluesselCustomBean key) {
        return lagisEJBServerStub.isFlurstueckHistoric(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   oldFlurstueckSchluessel  DOCUMENT ME!
     * @param   newFlurstueckSchluessel  DOCUMENT ME!
     * @param   benutzerkonto            DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public FlurstueckCustomBean renameFlurstueck(final FlurstueckSchluesselCustomBean oldFlurstueckSchluessel,
            final FlurstueckSchluesselCustomBean newFlurstueckSchluessel,
            final String benutzerkonto) throws ActionNotSuccessfulException {
        oldFlurstueckSchluessel.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        newFlurstueckSchluessel.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        oldFlurstueckSchluessel.setLetzte_bearbeitung(getCurrentDate());
        newFlurstueckSchluessel.setLetzte_bearbeitung(getCurrentDate());
        return lagisEJBServerStub.renameFlurstueck(
                oldFlurstueckSchluessel,
                newFlurstueckSchluessel,
                LagisBroker.getInstance().getAccountName());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SperreCustomBean isLocked(final FlurstueckSchluesselCustomBean key) {
        return lagisEJBServerStub.isLocked(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   joinMembers              DOCUMENT ME!
     * @param   newFlurstueckSchluessel  DOCUMENT ME!
     * @param   benutzerkonto            DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public FlurstueckCustomBean joinFlurstuecke(final ArrayList<FlurstueckSchluesselCustomBean> joinMembers,
            final FlurstueckSchluesselCustomBean newFlurstueckSchluessel,
            final String benutzerkonto) throws ActionNotSuccessfulException {
        for (final FlurstueckSchluesselCustomBean key : joinMembers) {
            key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
            key.setLetzte_bearbeitung(getCurrentDate());
        }
        newFlurstueckSchluessel.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        newFlurstueckSchluessel.setLetzte_bearbeitung(getCurrentDate());
        return lagisEJBServerStub.joinFlurstuecke(
                joinMembers,
                newFlurstueckSchluessel,
                LagisBroker.getInstance().getAccountName());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   oldFlurstueckSchluessel  DOCUMENT ME!
     * @param   splitMembers             DOCUMENT ME!
     * @param   benutzerkonto            DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void splitFlurstuecke(final FlurstueckSchluesselCustomBean oldFlurstueckSchluessel,
            final ArrayList<FlurstueckSchluesselCustomBean> splitMembers,
            final String benutzerkonto) throws ActionNotSuccessfulException {
        for (final FlurstueckSchluesselCustomBean key : splitMembers) {
            key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
            key.setLetzte_bearbeitung(getCurrentDate());
        }
        oldFlurstueckSchluessel.setLetzter_bearbeiter(benutzerkonto);
        oldFlurstueckSchluessel.setLetzte_bearbeitung(getCurrentDate());
        lagisEJBServerStub.splitFlurstuecke(
            oldFlurstueckSchluessel,
            splitMembers,
            LagisBroker.getInstance().getAccountName());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   joinMembers    DOCUMENT ME!
     * @param   splitMembers   DOCUMENT ME!
     * @param   benutzerkonto  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void joinSplitFlurstuecke(final ArrayList<FlurstueckSchluesselCustomBean> joinMembers,
            final ArrayList<FlurstueckSchluesselCustomBean> splitMembers,
            final String benutzerkonto) throws ActionNotSuccessfulException {
        for (final FlurstueckSchluesselCustomBean key : joinMembers) {
            key.setLetzter_bearbeiter(benutzerkonto);
            key.setLetzte_bearbeitung(getCurrentDate());
        }
        for (final FlurstueckSchluesselCustomBean key : splitMembers) {
            key.setLetzter_bearbeiter(benutzerkonto);
            key.setLetzte_bearbeitung(getCurrentDate());
        }
        lagisEJBServerStub.joinSplitFlurstuecke(
            joinMembers,
            splitMembers,
            LagisBroker.getInstance().getAccountName());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckHistorieCustomBean> getAllHistoryEntries(
            final FlurstueckSchluesselCustomBean schluessel) {
        return lagisEJBServerStub.getAllHistoryEntries(schluessel);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schluessel  DOCUMENT ME!
     * @param   level       DOCUMENT ME!
     * @param   type        DOCUMENT ME!
     * @param   levelCount  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public Collection<FlurstueckHistorieCustomBean> getHistoryEntries(final FlurstueckSchluesselCustomBean schluessel,
            final LagisServer.HistoryLevel level,
            final LagisServer.HistoryType type,
            final int levelCount) throws ActionNotSuccessfulException {
        return lagisEJBServerStub.getHistoryEntries(schluessel, level, type, levelCount);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VertragCustomBean> getVertraegeForKey(final FlurstueckSchluesselCustomBean key) {
        return lagisEJBServerStub.getVertraegeForKey(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   vertrag  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossReferencesForVertrag(final VertragCustomBean vertrag) {
        return lagisEJBServerStub.getCrossReferencesForVertrag(vertrag);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   vertraege  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossreferencesForVertraege(
            final Collection<VertragCustomBean> vertraege) {
        return lagisEJBServerStub.getCrossreferencesForVertraege(vertraege);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   aktenzeichen  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getFlurstueckSchluesselByAktenzeichen(final String aktenzeichen) {
        return lagisEJBServerStub.getFlurstueckSchluesselByAktenzeichen(aktenzeichen);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   miPa  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossReferencesForMiPa(final MipaCustomBean miPa) {
        return lagisEJBServerStub.getCrossReferencesForMiPa(miPa);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   miPas  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossreferencesForMiPas(
            final Collection<MipaCustomBean> miPas) {
        return lagisEJBServerStub.getCrossreferencesForMiPas(miPas);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<MipaCustomBean> getMiPaForKey(final FlurstueckSchluesselCustomBean key) {
        return lagisEJBServerStub.getMiPaForKey(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BaumCustomBean> getBaumForKey(final FlurstueckSchluesselCustomBean key) {
        return lagisEJBServerStub.getBaumForKey(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   baum  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossReferencesForBaum(final BaumCustomBean baum) {
        return lagisEJBServerStub.getCrossReferencesForBaum(baum);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   baeume  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossreferencesForBaeume(
            final Collection<BaumCustomBean> baeume) {
        return lagisEJBServerStub.getCrossreferencesForBaeume(baeume);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public boolean setFlurstueckHistoric(final FlurstueckSchluesselCustomBean key) throws ActionNotSuccessfulException {
        key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        key.setLetzte_bearbeitung(getCurrentDate());
        return lagisEJBServerStub.setFlurstueckHistoric(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key   DOCUMENT ME!
     * @param   date  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public boolean setFlurstueckHistoric(final FlurstueckSchluesselCustomBean key, final Date date)
            throws ActionNotSuccessfulException {
        key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        key.setLetzte_bearbeitung(getCurrentDate());
        return lagisEJBServerStub.setFlurstueckHistoric(key, date);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueckSchluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public boolean hasFlurstueckSucccessors(final FlurstueckSchluesselCustomBean flurstueckSchluessel)
            throws ActionNotSuccessfulException {
        return lagisEJBServerStub.hasFlurstueckSucccessors(flurstueckSchluessel);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public boolean setFlurstueckActive(final FlurstueckSchluesselCustomBean key) throws ActionNotSuccessfulException {
        key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        key.setLetzte_bearbeitung(getCurrentDate());
        return lagisEJBServerStub.setFlurstueckActive(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key       DOCUMENT ME!
     * @param   username  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void bookNutzungenForFlurstueck(final FlurstueckSchluesselCustomBean key, final String username)
            throws ActionNotSuccessfulException {
        key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        key.setLetzte_bearbeitung(getCurrentDate());
        lagisEJBServerStub.bookNutzungenForFlurstueck(key, username);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wfsFlurstueck  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getFlurstueckSchluesselForWFSFlurstueck(
            final Collection<WfsFlurstuecke> wfsFlurstueck) throws ActionNotSuccessfulException {
        return lagisEJBServerStub.getFlurstueckSchluesselForWFSFlurstueck(wfsFlurstueck);
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
// class EJBConnector extends SwingWorker<LagisServer, Void> {
//
// //~ Instance fields ----------------------------------------------------
//
// private boolean hadErrors;
// private String errorMessage;
//
// //~ Constructors -------------------------------------------------------
//
// /**
// * Creates a new EJBConnector object.
// */
// public EJBConnector() {
// }
//
// //~ Methods ------------------------------------------------------------
//
// @Override
// protected LagisServer doInBackground() throws Exception {
// if (LOG.isDebugEnabled()) {
// LOG.debug("starte EJBConnectorjob");
// }
// try {
// if (LOG.isDebugEnabled()) {
// LOG.debug("Lookup des LagisEJB");
// }
// if (ic != null) {
// if (LOG.isDebugEnabled()) {
// LOG.debug("vor intial Kontext");
// }
// final InitialContext ic = new InitialContext();
// if (LOG.isDebugEnabled()) {
// LOG.debug("Initial Kontext komplett");
// }
// }
// final LagisServer tmpLagisEJB = (LagisServer)ic.lookup(
// "de.cismet.lagisEE.bean.LagisServerRemote");
// if (LOG.isDebugEnabled()) {
// LOG.debug("Lookup des LagisEJB erfolgreich");
// }
// return tmpLagisEJB;
// } catch (Throwable ex) {
// LOG.fatal("Fehler beim Verbinden mit Glassfish.\nFehler beim initialisieren/lookup des EJBs", ex);
// return null;
// }
// }
//
// @Override
// protected void done() {
// try {
// if (LOG.isDebugEnabled()) {
// LOG.debug("EJBConnector done");
// }
// if (isCancelled()) {
// if (LOG.isDebugEnabled()) {
// LOG.debug("EJBConnector canceled");
// }
// brokenConnectionOptionPane.setMessage(
// "Es konnte keine Verbindung zum LagiS Server hergestellt werden.\n Was Möchten Sie tun ?");
// EJBReconnectorPanel.resetPanel();
// return;
// }
// lagisEJBServerStub = get();
// if (lagisEJBServerStub != null) {
// if (LOG.isDebugEnabled()) {
// LOG.debug("Verbinden mit Glassifsh und abrufen des LagisEJB erfogreich");
// }
// brokenConnectionDialog.setVisible(false);
// EJBReconnectorPanel.resetPanel();
// } else {
// if (LOG.isDebugEnabled()) {
// LOG.debug("Verbinden mit Glassifsh und abrufen des LagisEJB nicht erfogreich");
// }
// EJBReconnectorPanel.resetPanel();
// }
// } catch (Exception ex) {
// LOG.error("Fehler beim Verbinden mit Glassfish(done)");
// }
// brokenConnectionOptionPane.setMessage(
// "Es konnte keine Verbindung zum LagiS Server hergestellt werden.\n Was Möchten Sie tun ?");
// }
// }
}
