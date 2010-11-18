/*
 * EJBroker.java
 *
 * Created on 19. April 2007, 08:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.broker;

import de.cismet.ee.EJBAccessor;
import de.cismet.lagis.gui.panels.EJBReconnectorPanel;
import de.cismet.lagisEE.bean.Exception.ActionNotSuccessfulException;
import de.cismet.lagisEE.bean.LagisServerBean.HistoryLevel;
import de.cismet.lagisEE.bean.LagisServerBean.HistoryType;
import de.cismet.lagisEE.bean.LagisServerLocal;
import de.cismet.lagisEE.bean.LagisServerRemote;
import de.cismet.lagisEE.crossover.entity.WfsFlurstuecke;
import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.core.Kosten;
import de.cismet.lagisEE.entity.core.Vertrag;
import de.cismet.lagisEE.entity.core.hardwired.Anlageklasse;
import de.cismet.lagisEE.entity.core.hardwired.Bebauung;
import de.cismet.lagisEE.entity.core.hardwired.Beschlussart;
import de.cismet.lagisEE.entity.core.hardwired.Flaechennutzung;
import de.cismet.lagisEE.entity.core.hardwired.FlurstueckArt;
import de.cismet.lagisEE.entity.core.hardwired.Gemarkung;
import de.cismet.lagisEE.entity.core.hardwired.Nutzungsart;
import de.cismet.lagisEE.entity.core.hardwired.ReBeArt;
import de.cismet.lagisEE.entity.core.hardwired.Vertragsart;
import de.cismet.lagisEE.entity.core.hardwired.VerwaltendeDienststelle;
import de.cismet.lagisEE.entity.core.hardwired.Verwaltungsgebrauch;
import de.cismet.lagisEE.entity.extension.baum.Baum;
import de.cismet.lagisEE.entity.extension.baum.BaumKategorie;
import de.cismet.lagisEE.entity.extension.baum.BaumMerkmal;
import de.cismet.lagisEE.entity.extension.vermietung.MiPa;
import de.cismet.lagisEE.entity.extension.vermietung.MiPaKategorie;
import de.cismet.lagisEE.entity.extension.vermietung.MiPaMerkmal;
import de.cismet.lagisEE.entity.history.FlurstueckHistorie;
import de.cismet.lagisEE.entity.locking.Sperre;
import de.cismet.lagisEE.util.Key;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.MarshalException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.omg.CORBA.COMM_FAILURE;

/**
 *
 * @author Puhl
 */
//ToDo Benutzernamen in Server auslagern --> hat hier nichts verloren
public final class EJBroker implements LagisServerRemote, LagisServerLocal {

    private static EJBroker brokerInstance = null;
    private static LagisServerRemote lagisEJBServerStub;
    private final static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(EJBroker.class);
    private static JFrame parentFrame;
    private static JDialog brokenConnectionDialog;
    private static EJBReconnectorPanel EJBReconnectorPanel;
    private static JOptionPane brokenConnectionOptionPane;
    private static EJBConnector EJBConnectorWorker;
    private static JFrame dialogOwner;
    private static InitialContext ic;
    private static String server = null;
    private static String orbPort = null;

    /** Creates a new instance of EJBroker */
    private EJBroker() {
        initBrokenConnectionDialog();
        try {
            log.debug("Lookup des LagisEJB");
//            System.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
//            System.setProperty("org.omg.CORBA.ORBInitialHost", "192.168.100.150");
//            System.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
            //c = new InitialContext();
            //log.debug("Initial Kontext komplett");
            //log.debug("Lookup des LagisEJB erfolgreich");
            if(server == null){
                log.info("Kein valider Glassfishserver gesetzt, setze localhost");
                server = "localhost";
            }
            if(orbPort == null){
                log.info("Kein valider Glassfish Port gesetzt, setze Standardport 3700");
                orbPort="3700";
            }
            lagisEJBServerStub = EJBAccessor.createEJBAccessor(getServer(), getOrbPort(), LagisServerRemote.class).getEjbInterface();
        } catch (Throwable ex) {
            log.fatal("Fehler beim Verbinden mit Glassfish.\nFehler beim initialisieren/lookup des EJBs", ex);
            brokenConnectionDialog.setVisible(true);
        }
    }

    private final void initBrokenConnectionDialog() {
        if (dialogOwner == null) {
            EJBReconnectorPanel = new EJBReconnectorPanel();
            if (parentFrame != null && parentFrame.isVisible()) {
                log.debug("frame wurde gesetzt");
                dialogOwner = parentFrame;
            } else {
                log.debug("frame wurde nicht gesetzt");
                dialogOwner = null;
            }
            brokenConnectionOptionPane = new JOptionPane(
                    "Es konnte keine Verbindung zum LagiS Server hergestellt werden.\n Was Möchten Sie tun ?",
                    JOptionPane.QUESTION_MESSAGE, JOptionPane.NO_OPTION, null, new Object[]{EJBReconnectorPanel});
            brokenConnectionDialog = new JDialog(dialogOwner,
                    "Fehler beim Verbinden mit dem LagIS Server",
                    true);
            brokenConnectionDialog.setContentPane(brokenConnectionOptionPane);
            brokenConnectionDialog.setDefaultCloseOperation(
                    JDialog.DO_NOTHING_ON_CLOSE);
            brokenConnectionDialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent we) {
                    log.debug("User hat versucht, das Panel zu schließen");
                }
            });

            EJBReconnectorPanel.getBtnExitCancel().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (EJBReconnectorPanel.getBtnExitCancel().getText().equals("Abbrechen")) {
                        EJBReconnectorPanel.getBtnExitCancel().setEnabled(false);
                        if (EJBConnectorWorker != null && !EJBConnectorWorker.isDone()) {
                            log.debug("EjbConnector wird abgebrochen");
                            EJBConnectorWorker.cancel(false);
                            EJBConnectorWorker = null;
                        } else {
                            log.warn("EjbConnector läuft nicht");
                            EJBReconnectorPanel.resetPanel();
                        }
                        log.debug("Verbindungsvorgang abgebrochen");
                    } else {
                        log.debug("Kein Verbindungsvorgang am laufen --> LagIS wird beendet");
                        shutdownLagIS();
                    }
                }
            });
            EJBReconnectorPanel.getBtnRetry().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    EJBReconnectorPanel.getPb().setIndeterminate(true);
                    //ejbReconnectorPanel.getPb().setVisible(true);
                    log.debug("vor Thread Start");
                    EJBConnectorWorker = new EJBConnector();
                    //EJBConnectorWorker.execute();
                    LagisBroker.getInstance().execute(EJBConnectorWorker);
                    log.debug("nach Thread start");
                    //ejbReconnectorPanel.getLblMessage().setText("Versuche zu verbinden...");
                    brokenConnectionOptionPane.setMessage("Versuche zu verbinden...");
                    EJBReconnectorPanel.getBtnExitCancel().setText("Abbrechen");
                    EJBReconnectorPanel.getBtnRetry().setVisible(false);

                }
            });
            brokenConnectionDialog.pack();
        }
        EJBReconnectorPanel.getPb().setIndeterminate(false);

        if (parentFrame == null || !parentFrame.isVisible()) {
            Dimension oldDim = brokenConnectionDialog.getSize();
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            brokenConnectionDialog.setBounds((int) (screen.getWidth() / 2 - oldDim.getWidth()), (int) (screen.getHeight() / 2 - oldDim.getHeight()), (int) oldDim.getWidth(), (int) oldDim.getHeight());
        } else if (parentFrame != null && parentFrame.isVisible()) {
            brokenConnectionDialog.setLocationRelativeTo(parentFrame);
        }
    //ejbReconnectorPanel.getPb().setVisible(false);
    }

    private void shutdownLagIS() {
        if (parentFrame != null && parentFrame.isVisible()) {
            int result = JOptionPane.showConfirmDialog(brokenConnectionDialog, "Sind Sie sicher, das Sie LagIS beenden wollen?\nAlle nicht gespeicherten Änderungen gehen verloren!", "Bestätigung", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                ((JFrame) parentFrame).dispose();
            } else {
                log.debug("beenden abgebrochen");
            }
        } else {
            System.exit(1);
        }
    }

//synchronized because the lookup take some time and multiple calls causes multiple lookups --> exception
    public synchronized static EJBroker getInstance() {
        if (brokerInstance == null) {
            brokerInstance = new EJBroker();
        }
        return brokerInstance;
    }

//    private Integer blocker = new Integer(0);
//    //TODO im Moment nur inital was wenn lagisEJB != null
//    private void isConnected(){
//        if(lagisEJB != null){
//            return;
//        } else {
//            synchronized(blocker){
//                if(lagisEJB == null){
//                    boolean shouldRetry = true;
//                    while(shouldRetry){
//                        shouldRetry = JOptionPane.showConfirmDialog(null,
//                                "Es besteht keine Verbindung zum Server möchten Sie es weiterversuchen ?\n(Nein beendet die Applikation und alle nichtgespeicherten Daten gehen verloren)","Server Verbindung",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
//                        if (shouldRetry){
//                            try {
//                                log.debug("Versuche Verbindung zum Server herzustellen");
//                                InitialContext ic = new InitialContext();
//                                log.debug("Initial Kontext komplett");
//                                lagisEJB = (LagisServerRemote) ic.lookup("de.cismet.lagisEE.bean.LagisServerRemote");
//                                log.debug("Lookup des LagisEJB erfolgreich");
//                                return;
//                            } catch (Throwable ex) {
//                                log.fatal("Fehler beim initialisieren/lookup des EJBs",ex);
//                            }
//                        } else {
//                            log.debug("Applikation wird auf Benutzerwunsch beendet --> Keine Serververbindung");
//                            System.exit(10);
//                        }
//                    }
//                }
//
//            };
//        }
//    }
    public void createFlurstueckHistoryEntry(FlurstueckHistorie flurstueckHistorie) {
//        isConnected();
        try {
            lagisEJBServerStub.createFlurstueckHistoryEntry(flurstueckHistorie);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            createFlurstueckHistoryEntry(flurstueckHistorie);
        }
    }

    public Date getCurrentDate() {
        try {
            return lagisEJBServerStub.getCurrentDate();
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getCurrentDate();
        }
    }

    private void handleEJBException(final EJBException ex) {
        Exception causedEx = ex.getCausedByException();
        if (causedEx != null && causedEx instanceof MarshalException) {
            log.debug("CausedException ist eine MarshalException");
            Throwable t = causedEx.getCause();
            if (t != null && t instanceof COMM_FAILURE) {
                log.debug("Exception ist eine Corba COMM FAILURE Exception");
                if (brokenConnectionDialog.isVisible()) {
                    log.debug("Dialog ist bereist sichtbar --> gehe schlafen");
                    while (brokenConnectionDialog.isVisible()) {
                        try {
                            log.debug("Thread wartet auf wiederverbindung");
                            Thread.currentThread().sleep(500);
                        } catch (InterruptedException IntEx) {
                            log.debug("Schlafender Thread wurde unterbrochen", ex);
                            return;
                        }
                    }
                } else {
                    log.debug("Dialog noch nicht sichtbar --> zeige an");
                    initBrokenConnectionDialog();
                    brokenConnectionDialog.setVisible(true);
                }
            } else {
                log.debug("Throwable ist keine Corba Comm Failure Exception --> rethrow");
                throw ex;
            }
        } else {
            log.debug("CausedException ist keine MarshalException --> rethrow");
            throw ex;
        }
    }

//    public void createNutzungsHistoryEntry(NutzungHistorie nutzungsHistorie) {
//        lagisEJB.createNutzungsHistoryEntry(nutzungsHistorie);
//    }
    public void modifyFlurstueck(Flurstueck flurstueck) throws ActionNotSuccessfulException{
        //        isConnected();
        try {
            flurstueck.getFlurstueckSchluessel().setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
            flurstueck.getFlurstueckSchluessel().setLetzte_bearbeitung(getCurrentDate());
            lagisEJBServerStub.modifyFlurstueck(flurstueck);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            modifyFlurstueck(flurstueck);
        }
    }

    public void modifyFlurstueckSchluessel(final FlurstueckSchluessel key) {
        //        isConnected();
        try {
            key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
            key.setLetzte_bearbeitung(getCurrentDate());
            lagisEJBServerStub.modifyFlurstueckSchluessel(key);
        } catch (final EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            modifyFlurstueckSchluessel(key);
        }
    }

    public void deleteFlurstueck(final Flurstueck flurstueck) throws ActionNotSuccessfulException {
        try {
//        isConnected();
            lagisEJBServerStub.deleteFlurstueck(flurstueck);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            deleteFlurstueck(flurstueck);
        }
    }

//    public void createFlurstueck(Flurstueck flurstueck) {
//        lagisEJB.create(flurstueck);
//    }
    public Set<Key> getGemarkungsKeys() {
//            isConnected();
        try {
            return lagisEJBServerStub.getGemarkungsKeys();
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getGemarkungsKeys();
        }
    }

    public HashMap<Integer, Gemarkung> getGemarkungsHashMap() {
        Set<Gemarkung> gemarkungen = null;
        try {
            gemarkungen = (Set) lagisEJBServerStub.getGemarkungsKeys();
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            getGemarkungsHashMap();
        }
        if (gemarkungen != null) {
            HashMap<Integer, Gemarkung> result = new HashMap<Integer, Gemarkung>();
            for (Gemarkung gemarkung : gemarkungen) {
                if (gemarkung != null && gemarkung.getBezeichnung() != null && gemarkung.getSchluessel() != null) {
                    result.put(gemarkung.getSchluessel(), gemarkung);
                }
            }
            return result;
        } else {
            return null;
        }
    }
//TODO
    public Set<Key> getDependingKeysForKey(Key key) {
//        isConnected();
        try {
            return lagisEJBServerStub.getDependingKeysForKey(key);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getDependingKeysForKey(key);
        }
    }

    public Flurstueck retrieveFlurstueck(FlurstueckSchluessel key) {
//            isConnected();
        try {
            return lagisEJBServerStub.retrieveFlurstueck(key);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return retrieveFlurstueck(key);
        }
    }

    public Set<Anlageklasse> getAllAnlageklassen() {
//        isConnected();
        try {
            return lagisEJBServerStub.getAllAnlageklassen();
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getAllAnlageklassen();
        }
    }

    public Set<Vertragsart> getAllVertragsarten() {
//        isConnected();
        try {
            return lagisEJBServerStub.getAllVertragsarten();
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getAllVertragsarten();
        }
    }
    
    public Set<MiPaKategorie> getAllMiPaKategorien() {
//        isConnected();
        try {
            return lagisEJBServerStub.getAllMiPaKategorien();
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getAllMiPaKategorien();
        }
    }

    public Set<BaumKategorie> getAllBaumKategorien() {
//        isConnected();
        try {
            return lagisEJBServerStub.getAllBaumKategorien();
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getAllBaumKategorien();
        }
    }

    public Sperre createLock(Sperre newSperre) {
//        isConnected();
        try {
            return lagisEJBServerStub.createLock(newSperre);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return createLock(newSperre);
        }
    }

    public boolean releaseLock(Sperre sperre) {
        //isConnected();
        try {
            return lagisEJBServerStub.releaseLock(sperre);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return releaseLock(sperre);
        }
    }

//TODO
    public Gemarkung completeGemarkung(Gemarkung gem) {
        //isConnected();
        try {
            return lagisEJBServerStub.completeGemarkung(gem);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return completeGemarkung(gem);
        }
    }

    public Set<Verwaltungsgebrauch> getAllVerwaltenungsgebraeuche() {
//        isConnected();
        try {
            return lagisEJBServerStub.getAllVerwaltenungsgebraeuche();
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getAllVerwaltenungsgebraeuche();
        }
    }

    public Set<VerwaltendeDienststelle> getAllVerwaltendeDienstellen() {
        //isConnected();
        try {
            return lagisEJBServerStub.getAllVerwaltendeDienstellen();
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getAllVerwaltendeDienstellen();
        }
    }

    public Set<ReBeArt> getAllRebeArten() {
//        isConnected();
        try {
            return lagisEJBServerStub.getAllRebeArten();
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getAllRebeArten();
        }
    }

    public Set<Nutzungsart> getAllNutzungsarten() {
//        isConnected();
        try {
            return lagisEJBServerStub.getAllNutzungsarten();
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getAllNutzungsarten();
        }
    }

    public Set<Beschlussart> getAllBeschlussarten() {
//        isConnected();
        try {
            return lagisEJBServerStub.getAllBeschlussarten();
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getAllBeschlussarten();
        }
    }

//    public Set<Kostenart> getAllKostenarten() {
//        //return lagisEJB.getAll();
//    }
    public Set<Kosten> getAllKostenarten() {
//        isConnected();
        try {
            return lagisEJBServerStub.getAllKostenarten();
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getAllKostenarten();
        }
    }

    public Set<Flaechennutzung> getAllFlaechennutzungen() {
//        isConnected();
        try {
            return lagisEJBServerStub.getAllFlaechennutzungen();
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getAllFlaechennutzungen();
        }
    }
    
    public Set<MiPaMerkmal> getAllMiPaMerkmale() {
//        isConnected();
        try {
            return lagisEJBServerStub.getAllMiPaMerkmale();
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getAllMiPaMerkmale();
        }
    }

    public Set<BaumMerkmal> getAllBaumMerkmale() {
//        isConnected();
        try {
            return lagisEJBServerStub.getAllBaumMerkmale();
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getAllBaumMerkmale();
        }
    }

    public Set<Bebauung> getAllBebauungen() {
//        isConnected();
        try {
            return lagisEJBServerStub.getAllBebauungen();
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getAllBebauungen();
        }
    }

    public Set<FlurstueckArt> getAllFlurstueckArten() {
        try {
            return lagisEJBServerStub.getAllFlurstueckArten();
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getAllFlurstueckArten();
        }
    }

    public FlurstueckSchluessel completeFlurstueckSchluessel(FlurstueckSchluessel flurstueckSchluessel) {
//        isConnected();
        try {
            return lagisEJBServerStub.completeFlurstueckSchluessel(flurstueckSchluessel);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return completeFlurstueckSchluessel(flurstueckSchluessel);
        }
    }

    public static void setMainframe(JFrame aMainframe) {
        parentFrame = aMainframe;
    }

    public Flurstueck createFlurstueck(FlurstueckSchluessel key) {
//        isConnected();
        try {
            key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
            key.setLetzte_bearbeitung(getCurrentDate());
            return lagisEJBServerStub.createFlurstueck(key);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return createFlurstueck(key);
        }
    }

    public boolean isFlurstueckHistoric(FlurstueckSchluessel key) {
//        isConnected();
        try {
            return lagisEJBServerStub.isFlurstueckHistoric(key);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return isFlurstueckHistoric(key);
        }
    }

    public Flurstueck renameFlurstueck(final FlurstueckSchluessel oldFlurstueckSchluessel, final FlurstueckSchluessel newFlurstueckSchluessel, final String benutzerkonto) throws ActionNotSuccessfulException {
        try {
            oldFlurstueckSchluessel.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
            newFlurstueckSchluessel.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
            oldFlurstueckSchluessel.setLetzte_bearbeitung(getCurrentDate());
            newFlurstueckSchluessel.setLetzte_bearbeitung(getCurrentDate());
            return lagisEJBServerStub.renameFlurstueck(oldFlurstueckSchluessel, newFlurstueckSchluessel, LagisBroker.getInstance().getAccountName());
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return renameFlurstueck(oldFlurstueckSchluessel, newFlurstueckSchluessel, LagisBroker.getInstance().getAccountName());
        }
    }

    public Sperre isLocked(FlurstueckSchluessel key) {
//        isConnected();
        try {
            return lagisEJBServerStub.isLocked(key);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return isLocked(key);
        }
    }

    public Flurstueck joinFlurstuecke(final ArrayList<FlurstueckSchluessel> joinMembers, final FlurstueckSchluessel newFlurstueckSchluessel, final String benutzerkonto) throws ActionNotSuccessfulException {
//        isConnected();
        try {
            for(FlurstueckSchluessel key:joinMembers){
                key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
                key.setLetzte_bearbeitung(getCurrentDate());
            }
            newFlurstueckSchluessel.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
            newFlurstueckSchluessel.setLetzte_bearbeitung(getCurrentDate());
            return lagisEJBServerStub.joinFlurstuecke(joinMembers, newFlurstueckSchluessel, LagisBroker.getInstance().getAccountName());
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return joinFlurstuecke(joinMembers, newFlurstueckSchluessel, LagisBroker.getInstance().getAccountName());
        }
    }

    public void splitFlurstuecke(final FlurstueckSchluessel oldFlurstueckSchluessel, final ArrayList<FlurstueckSchluessel> splitMembers, final String benutzerkonto) throws ActionNotSuccessfulException {
//        isConnected();
        try {
            for(FlurstueckSchluessel key:splitMembers){
                key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
                key.setLetzte_bearbeitung(getCurrentDate());
            }
            oldFlurstueckSchluessel.setLetzter_bearbeiter(benutzerkonto);
            oldFlurstueckSchluessel.setLetzte_bearbeitung(getCurrentDate());
            lagisEJBServerStub.splitFlurstuecke(oldFlurstueckSchluessel, splitMembers, LagisBroker.getInstance().getAccountName());
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            splitFlurstuecke(oldFlurstueckSchluessel, splitMembers, LagisBroker.getInstance().getAccountName());
        }
    }

    public void joinSplitFlurstuecke(final ArrayList<FlurstueckSchluessel> joinMembers, final ArrayList<FlurstueckSchluessel> splitMembers, final String benutzerkonto) throws ActionNotSuccessfulException {
//        isConnected();
        try {
            for(FlurstueckSchluessel key:joinMembers){
                key.setLetzter_bearbeiter(benutzerkonto);
                key.setLetzte_bearbeitung(getCurrentDate());
            }
            for(FlurstueckSchluessel key:splitMembers){
                key.setLetzter_bearbeiter(benutzerkonto);
                key.setLetzte_bearbeitung(getCurrentDate());
            }
            lagisEJBServerStub.joinSplitFlurstuecke(joinMembers, splitMembers, LagisBroker.getInstance().getAccountName());
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            joinSplitFlurstuecke(joinMembers, splitMembers, LagisBroker.getInstance().getAccountName());
        }
    }

    public Set<FlurstueckHistorie> getAllHistoryEntries(FlurstueckSchluessel schluessel) {
//        isConnected();
        try {
            return lagisEJBServerStub.getAllHistoryEntries(schluessel);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getAllHistoryEntries(schluessel);
        }
    }

    public Set<FlurstueckHistorie> getHistoryEntries(FlurstueckSchluessel schluessel, HistoryLevel level, HistoryType type, int levelCount) throws ActionNotSuccessfulException {
        try {
            return lagisEJBServerStub.getHistoryEntries(schluessel,level,type,levelCount);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getHistoryEntries(schluessel,level,type,levelCount);
        }
    }        

    public Set<Vertrag> getVertraegeForKey(FlurstueckSchluessel key) {
//        isConnected();
        try {
            return lagisEJBServerStub.getVertraegeForKey(key);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getVertraegeForKey(key);
        }

    }

    public Set<FlurstueckSchluessel> getCrossReferencesForVertrag(Vertrag vertrag) {
//        isConnected();
        try {
            return lagisEJBServerStub.getCrossReferencesForVertrag(vertrag);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getCrossReferencesForVertrag(vertrag);
        }
    }

    public Set<FlurstueckSchluessel> getCrossreferencesForVertraege(Set<Vertrag> vertraege) {
//        isConnected();
        try {
            return lagisEJBServerStub.getCrossreferencesForVertraege(vertraege);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getCrossreferencesForVertraege(vertraege);
        }
    }

    public Set<FlurstueckSchluessel> getFlurstueckSchluesselByAktenzeichen(String aktenzeichen) {
        try {
            return lagisEJBServerStub.getFlurstueckSchluesselByAktenzeichen(aktenzeichen);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getFlurstueckSchluesselByAktenzeichen(aktenzeichen);
        }
    }

    public Set<FlurstueckSchluessel> getCrossReferencesForMiPa(MiPa miPa) {
        try {
            return lagisEJBServerStub.getCrossReferencesForMiPa(miPa);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getCrossReferencesForMiPa(miPa);
        }
    }

    public Set<FlurstueckSchluessel> getCrossreferencesForMiPas(Set<MiPa> miPas) {
        try {
            return lagisEJBServerStub.getCrossreferencesForMiPas(miPas);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getCrossreferencesForMiPas(miPas);
        }
    }

    public Set<MiPa> getMiPaForKey(FlurstueckSchluessel key) {
        try {
            return lagisEJBServerStub.getMiPaForKey(key);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getMiPaForKey(key);
        }
    }

    public Set<Baum> getBaumForKey(FlurstueckSchluessel key) {
        try {
            return lagisEJBServerStub.getBaumForKey(key);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getBaumForKey(key);
        }
    }

    public Set<FlurstueckSchluessel> getCrossReferencesForBaum(Baum baum) {
        try {
            return lagisEJBServerStub.getCrossReferencesForBaum(baum);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getCrossReferencesForBaum(baum);
        }
    }

    public Set<FlurstueckSchluessel> getCrossreferencesForBaeume(Set<Baum> baeume) {
        try {
            return lagisEJBServerStub.getCrossreferencesForBaeume(baeume);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getCrossreferencesForBaeume(baeume);
        }
    }


    
    

    public boolean setFlurstueckHistoric(FlurstueckSchluessel key) throws ActionNotSuccessfulException {
        try {
//        isConnected();
            key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
            key.setLetzte_bearbeitung(getCurrentDate());
            return lagisEJBServerStub.setFlurstueckHistoric(key);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return setFlurstueckHistoric(key);
        }
    }

    public boolean hasFlurstueckSucccessors(final FlurstueckSchluessel flurstueckSchluessel) throws ActionNotSuccessfulException {
        try {
//        isConnected();
            return lagisEJBServerStub.hasFlurstueckSucccessors(flurstueckSchluessel);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return hasFlurstueckSucccessors(flurstueckSchluessel);
        }
    }

    public boolean setFlurstueckActive(final FlurstueckSchluessel key) throws ActionNotSuccessfulException {
        try {
            key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
            key.setLetzte_bearbeitung(getCurrentDate());
            return lagisEJBServerStub.setFlurstueckActive(key);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return setFlurstueckActive(key);
        }
    }
    
    public void bookNutzungenForFlurstueck(FlurstueckSchluessel key,String username) throws ActionNotSuccessfulException{
        try {
            key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
            key.setLetzte_bearbeitung(getCurrentDate());
            lagisEJBServerStub.bookNutzungenForFlurstueck(key,username);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            bookNutzungenForFlurstueck(key,username);
        }
    }

    @Override
    public Set<FlurstueckSchluessel> getFlurstueckSchluesselForWFSFlurstueck(Set<WfsFlurstuecke> wfsFlurstueck) throws ActionNotSuccessfulException {
        try {
            return lagisEJBServerStub.getFlurstueckSchluesselForWFSFlurstueck(wfsFlurstueck);
        } catch (EJBException ex) {
            log.debug("Exception ist eine EJBException");
            handleEJBException(ex);
            return getFlurstueckSchluesselForWFSFlurstueck(wfsFlurstueck);
        }
    }



    class EJBConnector extends SwingWorker<LagisServerRemote, Void> {

        private boolean hadErrors;
        private String errorMessage;

        public EJBConnector() {
        }

        protected LagisServerRemote doInBackground() throws Exception {
            log.debug("starte EJBConnectorjob");
            try {
                log.debug("Lookup des LagisEJB");
                if (ic != null) {
                    log.debug("vor intial Kontext");
                    InitialContext ic = new InitialContext();
                    log.debug("Initial Kontext komplett");
                }
                LagisServerRemote tmpLagisEJB = (LagisServerRemote) ic.lookup("de.cismet.lagisEE.bean.LagisServerRemote");
                log.debug("Lookup des LagisEJB erfolgreich");
                return tmpLagisEJB;
            } catch (Throwable ex) {
                log.fatal("Fehler beim Verbinden mit Glassfish.\nFehler beim initialisieren/lookup des EJBs", ex);
                return null;
            }
        }

        @Override
        protected void done() {
            try {
                log.debug("EJBConnector done");
                if (isCancelled()) {
                    log.debug("EJBConnector canceled");
                    brokenConnectionOptionPane.setMessage("Es konnte keine Verbindung zum LagiS Server hergestellt werden.\n Was Möchten Sie tun ?");
                    EJBReconnectorPanel.resetPanel();
                    return;
                }
                lagisEJBServerStub = get();
                if (lagisEJBServerStub != null) {
                    log.debug("Verbinden mit Glassifsh und abrufen des LagisEJB erfogreich");
                    brokenConnectionDialog.setVisible(false);
                    EJBReconnectorPanel.resetPanel();
                } else {
                    log.debug("Verbinden mit Glassifsh und abrufen des LagisEJB nicht erfogreich");
                    EJBReconnectorPanel.resetPanel();
                }
            } catch (Exception ex) {
                log.error("Fehler beim Verbinden mit Glassfish(done)");

            }
            brokenConnectionOptionPane.setMessage("Es konnte keine Verbindung zum LagiS Server hergestellt werden.\n Was Möchten Sie tun ?");
        }
    }

    public static String getOrbPort() {
        return orbPort;
    }

    public static void setOrbPort(String orbPort) {
        EJBroker.orbPort = orbPort;
    }

    public static String getServer() {
        return server;
    }

    public static void setServer(String server) {
        EJBroker.server = server;
    }

    
}
