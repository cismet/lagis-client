/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.broker;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.connection.proxy.ConnectionProxy;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.AbstractAttributeRepresentationFormater;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;

import java.text.DecimalFormat;

import java.util.*;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import de.cismet.cids.custom.beans.lagis.*;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.lagis.Exception.ActionNotSuccessfulException;
import de.cismet.lagis.Exception.ErrorInNutzungProcessingException;

import de.cismet.lagisEE.crossover.entity.WfsFlurstuecke;

import de.cismet.lagisEE.interfaces.Key;

import de.cismet.lagisEE.util.FlurKey;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public final class CidsBroker {

    //~ Static fields/initializers ---------------------------------------------

    public static final String LAGIS_DOMAIN = "LAGIS";

    public static final String CLASS__FLURSTUECK_SCHLUESSEL = "flurstueck_schluessel";
    public static final String CLASS__ANLAGEKLASSE = "anlageklasse";
    public static final String CLASS__BAUM = "baum";
    public static final String CLASS__BAUM_KATEGORIE = "baum_kategorie";
    public static final String CLASS__BAUM_KATEGORIE_AUSPRAEGUNG = "baum_kategorie_auspraegung";
    public static final String CLASS__BAUM_MERKMAL = "baum_merkmal";
    public static final String CLASS__BAUM_NUTZUNG = "baum_nutzung";
    public static final String CLASS__BEBAUUNG = "bebauung";

    private static CidsBroker instance = null;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CidsBroker.class);

    private static Vector<Session> nkfSessions = new Vector<Session>();
    private static DecimalFormat currencyFormatter = new DecimalFormat(",##0.00 \u00A4");

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum HistoryLevel {

        //~ Enum constants -----------------------------------------------------

        DIRECT_RELATIONS, All, CUSTOM
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum HistoryType {

        //~ Enum constants -----------------------------------------------------

        SUCCESSOR, PREDECESSOR, BOTH
    }

    //~ Instance fields --------------------------------------------------------

    private ConnectionProxy proxy = null;

// @Resource(name = "mail/nkf_mailaddress")
    private Session nkfMailer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of CidsBroker.
     */
    private CidsBroker() {
        try {
            setProxy(SessionManager.getProxy());
            if (!SessionManager.isInitialized()) {
                SessionManager.init(getProxy());
                ClassCacheMultiple.setInstance(LAGIS_DOMAIN);
            }
        } catch (Throwable e) {
            LOG.fatal("no connection to the cids server possible. too bad.", e);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ConnectionProxy getProxy() {
        return proxy;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  proxy  DOCUMENT ME!
     */
    private void setProxy(final ConnectionProxy proxy) {
        this.proxy = proxy;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static synchronized CidsBroker getInstance() {
        if (instance == null) {
            instance = new CidsBroker();
        }
        return instance;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueckHistorie  DOCUMENT ME!
     */
    public void createFlurstueckHistoryEntry(final FlurstueckHistorieCustomBean flurstueckHistorie) {
        try {
            flurstueckHistorie.persist();
        } catch (Exception ex) {
            LOG.error("Fehler beim anlegen der Flurstueckshistorie: " + flurstueckHistorie, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getCurrentDate() {
        return new Date();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueck  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void modifyFlurstueck(final FlurstueckCustomBean flurstueck) throws ActionNotSuccessfulException {
        flurstueck.getFlurstueckSchluessel().setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        flurstueck.getFlurstueckSchluessel().setLetzte_bearbeitung(getCurrentDate());
        try {
            processNutzungen(flurstueck.getNutzungen(), flurstueck.getFlurstueckSchluessel().getKeyString());
            checkIfFlurstueckWasStaedtisch(flurstueck.getFlurstueckSchluessel(), null);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Flurstück gespeichert");
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim speichern der Entität", ex);
            throw new ActionNotSuccessfulException("Fehler beim speichern eines vorhandenen Flurstücks", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key  DOCUMENT ME!
     */
    public void modifyFlurstueckSchluessel(final FlurstueckSchluesselCustomBean key) {
        key.setLetzter_bearbeiter(LagisBroker.getInstance().getAccountName());
        key.setLetzte_bearbeitung(getCurrentDate());
        try {
            final FlurstueckSchluesselCustomBean oldKey = completeFlurstueckSchluessel(key);
            FlurstueckArtCustomBean oldArt = null;
            if (oldKey != null) {
                oldArt = oldKey.getFlurstueckArt();
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Alterschlüssel ist == null");
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(("Art " + oldArt) != null);
                LOG.debug(("Bezeichnung " + oldArt.getBezeichnung()) != null);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Alter war staedtich "
                            + FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                                oldArt.getBezeichnung()));
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Art hat sich geändert "
                            + !FlurstueckArtCustomBean.FLURSTUECK_ART_EQUALATOR.pedanticEquals(
                                oldArt,
                                key.getFlurstueckArt()));
            }
            if ((oldArt != null) && (oldArt.getBezeichnung() != null)
                        && FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(oldArt.getBezeichnung())
                        && !FlurstueckArtCustomBean.FLURSTUECK_ART_EQUALATOR.pedanticEquals(
                            oldArt,
                            key.getFlurstueckArt())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                        "Die Art eines städtischen Flurstücks wurde auf eine andere geändert update lettzer Stadtbestizt Datum");
                }
                key.setWarStaedtisch(true);
                key.setDatumLetzterStadtbesitz(new Date());
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                        "Die Art eines Städtischen Flurstücks wurde nicht auf eine andere geändert --> checkIfFlurstueckWasStaedtisch");
                }
                checkIfFlurstueckWasStaedtisch(key, null);
            }
        } catch (final Throwable t) {
            LOG.error("Fehler beim speichern der Entität", t);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueck  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void deleteFlurstueck(final FlurstueckCustomBean flurstueck) throws ActionNotSuccessfulException {
        boolean illegalDelete = false;
        try {
            if (!illegalDelete) {
                for (final VerwaltungsbereichCustomBean current : flurstueck.getVerwaltungsbereiche()) {
                    if (current != null) {
                        illegalDelete = true;
                        break;
                    }
                }
            }
            if (!illegalDelete) {
                for (final NutzungCustomBean current : flurstueck.getNutzungen()) {
                    if (current != null) {
                        illegalDelete = true;
                        break;
                    }
                }
            }
            if (!illegalDelete) {
                for (final VertragCustomBean current : flurstueck.getVertraege()) {
                    if (current != null) {
                        illegalDelete = true;
                        break;
                    }
                }
            }
            if (!illegalDelete) {
                // ToDo check if successor are also interesting
                for (final FlurstueckHistorieCustomBean current
                            : getAllHistoryEntries(flurstueck.getFlurstueckSchluessel())) {
                    if (current != null) {
                        illegalDelete = true;
                        break;
                    }
                }
            }
            if (illegalDelete) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es sind daten für das Flurstück vorhanden es kann nicht gelöscht werden");
                }
                throw new ActionNotSuccessfulException(
                    "Es sind Daten für das Flurstück vorhanden, es kann nicht gelöscht werden");
            } else {
                flurstueck.delete();
                flurstueck.persist();
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim löschen eines Flurstücks: " + flurstueck, ex);
            if (ex instanceof ActionNotSuccessfulException) {
                throw (ActionNotSuccessfulException)ex;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<GemarkungCustomBean> getGemarkungsKeys() {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("gemarkung");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsBroker.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " " + "FROM " + metaclass.getTableName());
        final Collection<GemarkungCustomBean> beans = new HashSet<GemarkungCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((GemarkungCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public HashMap<Integer, GemarkungCustomBean> getGemarkungsHashMap() {
        final Collection<GemarkungCustomBean> gemarkungen = getGemarkungsKeys();
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
     * @param   tabName    DOCUMENT ME!
     * @param   query      DOCUMENT ME!
     * @param   fields     DOCUMENT ME!
     * @param   formatter  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaObject[] getLagisLWMetaObjects(final String tabName,
            final String query,
            final String[] fields,
            AbstractAttributeRepresentationFormater formatter) {
        if (formatter == null) {
            formatter = new AbstractAttributeRepresentationFormater() {

                    @Override
                    public String getRepresentation() {
                        final StringBuffer sb = new StringBuffer();
                        for (final String attribute : fields) {
                            sb.append(getAttribute(attribute.toLowerCase())).append(" ");
                        }
                        return sb.toString().trim();
                    }
                };
        }
        try {
            final User user = SessionManager.getSession().getUser();
            final MetaClass mc = getLagisMetaClass(tabName);
            final ConnectionProxy proxy = getProxy();
            if (mc != null) {
                return proxy.getLightweightMetaObjectsByQuery(mc.getID(), user, query, fields, formatter);
            } else {
                LOG.error("Can not find MetaClass for Tablename: " + tabName);
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        return new MetaObject[0];
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<Key> getDependingKeysForKey(final Key key) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("GetDependingKeysForKey");
        }
        try {
            if (key != null) {
                if (key instanceof GemarkungCustomBean) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Key ist Gemarkung");
                    }
                    final GemarkungCustomBean currentGemarkung = (GemarkungCustomBean)key;
                    if ((currentGemarkung != null) && (currentGemarkung.getSchluessel() != null)) {
                        // TODO Duplicated code --> extract

                        final MetaClass metaclass = CidsBroker.getInstance()
                                    .getLagisMetaClass(CLASS__FLURSTUECK_SCHLUESSEL);
                        if (metaclass == null) {
                            return null;
                        }
                        final String query = "SELECT DISTINCT "
                                    + "   min(" + metaclass.getTableName() + "." + metaclass.getPrimaryKey()
                                    + ") AS id, "
                                    + "   min(" + metaclass.getTableName() + ".flur) AS flur "
                                    + "FROM "
                                    + "   " + metaclass.getTableName() + ", "
                                    + "   gemarkung "
                                    + "WHERE "
                                    + "    " + metaclass.getTableName() + ".fk_gemarkung = gemarkung.id "
                                    + "    AND " + metaclass.getTableName() + ".fk_flurstueck_art != 3 "
                                    + "    AND gemarkung.schluessel = " + currentGemarkung.getSchluessel() + " "
                                    + "GROUP BY " + metaclass.getTableName() + ".flur";

                        final MetaObject[] mos = getLagisLWMetaObjects(
                                metaclass.getTableName(),
                                query,
                                new String[] { "id", "flur" },
                                new AbstractAttributeRepresentationFormater() {

                                    @Override
                                    public String getRepresentation() {
                                        return String.valueOf(getAttribute("flur"));
                                    }
                                });

                        if (mos != null) {
                            final Collection flurKeys = new HashSet();
                            for (final MetaObject mo : mos) {
                                final Integer flur = Integer.parseInt(mo.toString());
                                flurKeys.add(new FlurKey(currentGemarkung, flur));
                            }
                            return flurKeys;
                        } else {
                            return new HashSet();
                        }
                    } else if ((currentGemarkung != null) && (currentGemarkung.getBezeichnung() != null)) {
                        final GemarkungCustomBean completed = completeGemarkung(currentGemarkung);
                        if (completed != null) {
                            final MetaClass metaclass = CidsBroker.getInstance()
                                        .getLagisMetaClass(CLASS__FLURSTUECK_SCHLUESSEL);
                            if (metaclass == null) {
                                return null;
                            }
                            final String query = "SELECT DISTINCT "
                                        + "   min(" + metaclass.getTableName() + "." + metaclass.getPrimaryKey()
                                        + ") AS id, "
                                        + "   min(" + metaclass.getTableName() + ".flur) AS flur "
                                        + "FROM "
                                        + "   " + metaclass.getTableName() + ", "
                                        + "   gemarkung "
                                        + "WHERE "
                                        + "    " + metaclass.getTableName() + ".fk_gemarkung = gemarkung.id "
                                        + "    AND " + metaclass.getTableName() + ".fk_flurstueck_art != 3 "
                                        + "    AND gemarkung.schluessel = " + completed.getSchluessel() + " "
                                        + "GROUP BY " + metaclass.getTableName() + ".flur";

                            final MetaObject[] mos = getLagisLWMetaObjects(
                                    metaclass.getTableName(),
                                    query,
                                    new String[] { "id", "flur" },
                                    new AbstractAttributeRepresentationFormater() {

                                        @Override
                                        public String getRepresentation() {
                                            return String.valueOf(getAttribute("flur"));
                                        }
                                    });
                            if (mos != null) {
                                final Collection flurKeys = new HashSet();
                                for (final MetaObject mo : mos) {
                                    final Integer flur = Integer.parseInt(mo.toString());
                                    flurKeys.add(new FlurKey(currentGemarkung, flur));
                                }
                            } else {
                                return new HashSet();
                            }
                        } else {
                            return new HashSet();
                        }
                    } else {
                        return new HashSet();
                    }
                } else if (key instanceof FlurKey) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Key ist Flur");
                    }
                    final FlurKey currentFlur = (FlurKey)key;

                    final MetaClass metaclass = CidsBroker.getInstance()
                                .getLagisMetaClass(CLASS__FLURSTUECK_SCHLUESSEL);
                    if (metaclass == null) {
                        return null;
                    }
                    String query = null;

                    // TODDO WHY INTEGER
                    if (!currentFlur.isCurrentFilterEnabled() && !currentFlur.isHistoricFilterEnabled()
                                && !currentFlur.isAbteilungXIFilterEnabled()
                                && !currentFlur.isStaedtischFilterEnabled()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Kein Filter für Flur Aktiviert");
                        }
                        query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                                    + metaclass.getPrimaryKey() + " "
                                    + "FROM "
                                    + "   " + metaclass.getTableName() + ", "
                                    + "   gemarkung "
                                    + "WHERE "
                                    + "   " + metaclass.getTableName() + ".fk_gemarkung = gemarkung.id "
                                    + "   AND " + metaclass.getTableName() + ".flur = " + currentFlur.getFlurId() + " "
                                    + "   AND " + metaclass.getTableName() + ".fk_flurstueck_art != 3 "
                                    + "   AND gemarkung.schluessel = " + currentFlur.getGemarkungsId();
                    } else if (currentFlur.isCurrentFilterEnabled()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Filter nur aktuelle Flurstücke: Aktiviert");
                        }
                        query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                                    + metaclass.getPrimaryKey() + " "
                                    + "FROM "
                                    + "   " + metaclass.getTableName() + ", "
                                    + "   gemarkung "
                                    + "WHERE "
                                    + "   " + metaclass.getTableName() + ".fk_gemarkung = gemarkung.id "
                                    + "   AND " + metaclass.getTableName() + ".flur = " + currentFlur.getFlurId() + " "
                                    + "   AND " + metaclass.getTableName() + ".fk_flurstueck_art != 3 "
                                    + "   AND " + metaclass.getTableName() + ".gueltig_bis IS NULL "
                                    + "   AND gemarkung.schluessel = " + currentFlur.getGemarkungsId();
                    } else if (currentFlur.isHistoricFilterEnabled()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Filter nur historische Flurstücke: Aktiviert");
                        }
                        query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                                    + metaclass.getPrimaryKey() + " "
                                    + "FROM "
                                    + "   " + metaclass.getTableName() + ", "
                                    + "   gemarkung "
                                    + "WHERE "
                                    + "   " + metaclass.getTableName() + ".fk_gemarkung = gemarkung.id "
                                    + "   AND " + metaclass.getTableName() + ".flur = " + currentFlur.getFlurId() + " "
                                    + "   AND " + metaclass.getTableName() + ".fk_flurstueck_art != 3 "
                                    + "   AND " + metaclass.getTableName() + ".gueltig_bis IS NOT NULL "
                                    + "   AND gemarkung.schluessel = " + currentFlur.getGemarkungsId();
                    } else if (currentFlur.isAbteilungXIFilterEnabled()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Filter nur Abteilung IX Flurstücke: Aktiviert");
                        }
                        query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                                    + metaclass.getPrimaryKey() + " "
                                    + "FROM "
                                    + "   " + metaclass.getTableName() + ", "
                                    + "   gemarkung "
                                    + "WHERE "
                                    + "   " + metaclass.getTableName() + ".fk_gemarkung = gemarkung.id "
                                    + "   AND " + metaclass.getTableName() + ".flur = " + currentFlur.getFlurId() + " "
                                    + "   AND " + metaclass.getTableName() + ".fk_flurstueck_art = 2 "
                                    + "   AND gemarkung.schluessel = " + currentFlur.getGemarkungsId();
                    } else if (currentFlur.isStaedtischFilterEnabled()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Filter nur staedtische Flurstücke: Aktiviert");
                        }
                        query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                                    + metaclass.getPrimaryKey() + " "
                                    + "FROM "
                                    + "   " + metaclass.getTableName() + ", "
                                    + "   gemarkung "
                                    + "WHERE "
                                    + "   " + metaclass.getTableName() + ".fk_gemarkung = gemarkung.id "
                                    + "   AND " + metaclass.getTableName() + ".flur = " + currentFlur.getFlurId() + " "
                                    + "   AND " + metaclass.getTableName() + ".fk_flurstueck_art = 1 "
                                    + "   AND gemarkung.schluessel = " + currentFlur.getGemarkungsId();
                    }
                    if (query != null) {
                        final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);
                        final Collection<FlurstueckSchluesselCustomBean> flurstuecke =
                            new HashSet<FlurstueckSchluesselCustomBean>();
                        for (final MetaObject metaObject : mos) {
                            flurstuecke.add((FlurstueckSchluesselCustomBean)metaObject.getBean());
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Ergebnisse für Abfrage vorhanden: " + flurstuecke.size());
                        }
                        return new HashSet(flurstuecke);
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Keine Ergebnisse für Abfrage vorhanden");
                        }
                        return new HashSet();
                    }
                }
            } else {
                return new HashSet();
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim abfragen eines Keys: " + key + " Class: " + ((key != null) ? key.getClass() : null),
                ex);
        }
        return new HashSet();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckCustomBean retrieveFlurstueck(final FlurstueckSchluesselCustomBean key) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Finde Flurstuck: ");
                LOG.debug("Id       : " + key.getId());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Gemarkung: " + key.getGemarkung());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Flur     : " + key.getFlur());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Zaehler  : " + key.getFlurstueckZaehler());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Nenner   : " + key.getFlurstueckNenner());
            }

            final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("flurstueck");
            if (metaclass == null) {
                return null;
            }

            final MetaObject[] mos = CidsBroker.getInstance()
                        .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                            + metaclass.getPrimaryKey() + " "
                            + " FROM " + metaclass.getTableName() + ", flurstueck_schluessel fk"
                            + " WHERE " + metaclass.getTableName() + ".fk_flurstueck_schluessel = fk.id "
                            + " AND fk.flur = " + key.getFlur()
                            + " AND fk.fk_gemarkung = " + key.getGemarkung().getId()
                            + " AND fk.flurstueck_zaehler = " + key.getFlurstueckZaehler()
                            + " AND fk.flurstueck_nenner  = " + key.getFlurstueckNenner());

            if ((mos != null) && (mos.length > 0)) {
                if (mos.length > 1) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Anzahl Flurstuecke: " + mos.length);
                    }
                    throw new Exception("Multiple Flurstuecke should only be one");
                } else {
                    final FlurstueckCustomBean result = (FlurstueckCustomBean)mos[0].getBean();

                    final Collection<VertragCustomBean> vertrage = result.getVertraege();
                    if ((vertrage != null) && (vertrage.size() > 0)) {
                        final Collection<FlurstueckSchluesselCustomBean> resultKeys = getCrossreferencesForVertraege(
                                vertrage);
                        if (resultKeys != null) {
                            resultKeys.remove(result.getFlurstueckSchluessel());
                        }
                        result.setVertraegeQuerverweise(resultKeys);
                    }

                    final Collection<MipaCustomBean> miPas = result.getMiPas();
                    if ((miPas != null) && (miPas.size() > 0)) {
                        final Collection<FlurstueckSchluesselCustomBean> resultKeys = getCrossreferencesForMiPas(miPas);
                        if (resultKeys != null) {
                            resultKeys.remove(result.getFlurstueckSchluessel());
                        }
                        result.setMiPasQuerverweise(resultKeys);
                    }

                    final Collection<BaumCustomBean> baueme = result.getBaeume();
                    if ((baueme != null) && (baueme.size() > 0)) {
                        final Collection<FlurstueckSchluesselCustomBean> resultKeys = getCrossreferencesForBaeume(
                                baueme);
                        if (resultKeys != null) {
                            resultKeys.remove(result.getFlurstueckSchluessel());
                        }
                        result.setBaeumeQuerverweise(resultKeys);
                    }

                    return result;
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim abfragen des Flurstuecks: " + key, ex);
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<AnlageklasseCustomBean> getAllAnlageklassen() {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("anlageklasse");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsBroker.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<AnlageklasseCustomBean> beans = new HashSet<AnlageklasseCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((AnlageklasseCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VertragsartCustomBean> getAllVertragsarten() {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("vertragsart");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsBroker.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<VertragsartCustomBean> beans = new HashSet<VertragsartCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((VertragsartCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<MipaKategorieCustomBean> getAllMiPaKategorien() {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("mipa_kategorie");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsBroker.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<MipaKategorieCustomBean> beans = new HashSet<MipaKategorieCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((MipaKategorieCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BaumKategorieCustomBean> getAllBaumKategorien() {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("baum_kategorie");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsBroker.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<BaumKategorieCustomBean> beans = new HashSet<BaumKategorieCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((BaumKategorieCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   newSperre  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SperreCustomBean createLock(final SperreCustomBean newSperre) {
        if (newSperre != null) {
            final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("sperre");
            if (metaclass == null) {
                return null;
            }
            final String query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " "
                        + "FROM " + metaclass.getTableName() + " "
                        + "WHERE " + metaclass.getTableName() + ".fk_flurstueck_schluessel = "
                        + newSperre.getFlurstueckSchluessel();
            final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);

            if ((mos == null) || (mos.length == 0)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Keine Sperre für das angegebene Flurstueck vorhanden, es wird versucht eine anzulegen");
                }
                try {
                    return (SperreCustomBean)newSperre.persist();
                } catch (Exception ex) {
                    LOG.error("Fehler beim Anlegen der Sperre", ex);
                    return null;
                }
            } else if (mos.length == 1) {
                final SperreCustomBean sperre = (SperreCustomBean)mos[0].getBean();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es ist eine Sperre vorhanden und wird von: " + sperre.getBenutzerkonto() + " gehalten");
                }
                return sperre;
            } else if (mos.length > 1) {
                final SperreCustomBean sperre = (SperreCustomBean)mos[0].getBean();
                LOG.error("Es sind mehrere Sperren vorhanden");
                // TODO Jean: hier wurde vorher null zurückgegeben, denke aber nicht dass das richtig war
                return sperre;
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Die Sperre die anglegt werden soll ist null");
            }
            return null;
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   sperre  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean releaseLock(final SperreCustomBean sperre) {
        try {
            if (sperre != null) {
                sperre.delete();
                sperre.persist();
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Fehler beim löschen einer Sperre", ex);
            }
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   gem  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public GemarkungCustomBean completeGemarkung(final GemarkungCustomBean gem) {
        try {
            if ((gem != null) && (gem.getBezeichnung() != null)) {
                final Collection<GemarkungCustomBean> gemarkungen = getGemarkungsKeys();
                if (gemarkungen != null) {
                    final Iterator<GemarkungCustomBean> it = gemarkungen.iterator();
                    while (it.hasNext()) {
                        final GemarkungCustomBean tmp = it.next();
                        if (tmp.getBezeichnung().equals(gem.getBezeichnung())) {
                            return tmp;
                        }
                    }
                    return null;
                } else {
                    return null;
                }
            } else if ((gem != null) && (gem.getSchluessel() != null)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Schlüssel != null");
                }
                final Collection<GemarkungCustomBean> gemarkungen = getGemarkungsKeys();
                if (gemarkungen != null) {
                    final Iterator<GemarkungCustomBean> it = gemarkungen.iterator();
                    while (it.hasNext()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("checke schlüssel durch");
                        }
                        final GemarkungCustomBean tmp = it.next();
                        if (tmp.getSchluessel().intValue() == gem.getSchluessel().intValue()) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Schlüssel gefunden");
                            }
                            return tmp;
                        }
                    }
                    return null;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Fehler beim Kompletieren einer Gemarkung: " + gem, ex);
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VerwaltungsgebrauchCustomBean> getAllVerwaltenungsgebraeuche() {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("verwaltungsgebrauch");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsBroker.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<VerwaltungsgebrauchCustomBean> beans = new HashSet<VerwaltungsgebrauchCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((VerwaltungsgebrauchCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VerwaltendeDienststelleCustomBean> getAllVerwaltendeDienstellen() {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("verwaltende_dienststelle");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsBroker.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<VerwaltendeDienststelleCustomBean> beans = new HashSet<VerwaltendeDienststelleCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((VerwaltendeDienststelleCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<RebeArtCustomBean> getAllRebeArten() {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("rebe_art");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsBroker.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<RebeArtCustomBean> beans = new HashSet<RebeArtCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((RebeArtCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<NutzungsartCustomBean> getAllNutzungsarten() {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("nutzungsart");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsBroker.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<NutzungsartCustomBean> beans = new HashSet<NutzungsartCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((NutzungsartCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BeschlussartCustomBean> getAllBeschlussarten() {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("beschlussart");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsBroker.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<BeschlussartCustomBean> beans = new HashSet<BeschlussartCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((BeschlussartCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<KostenartCustomBean> getAllKostenarten() {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("kostenart");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsBroker.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<KostenartCustomBean> beans = new HashSet<KostenartCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((KostenartCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlaechennutzungCustomBean> getAllFlaechennutzungen() {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("flaechennutzung");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsBroker.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<FlaechennutzungCustomBean> beans = new HashSet<FlaechennutzungCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((FlaechennutzungCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<MipaMerkmalCustomBean> getAllMiPaMerkmale() {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("mipa_merkmal");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsBroker.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<MipaMerkmalCustomBean> beans = new HashSet<MipaMerkmalCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((MipaMerkmalCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BaumMerkmalCustomBean> getAllBaumMerkmale() {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("baum_merkmal");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsBroker.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<BaumMerkmalCustomBean> beans = new HashSet<BaumMerkmalCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((BaumMerkmalCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<BebauungCustomBean> getAllBebauungen() {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("bebauung");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsBroker.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<BebauungCustomBean> beans = new HashSet<BebauungCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((BebauungCustomBean)metaObject.getBean());
        }
        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckArtCustomBean> getAllFlurstueckArten() {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("flurstueck_art");
        if (metaclass == null) {
            return null;
        }
        final MetaObject[] mos = CidsBroker.getInstance()
                    .getLagisMetaObject("SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                        + metaclass.getPrimaryKey() + " FROM "
                        + metaclass.getTableName());
        final Collection<FlurstueckArtCustomBean> beans = new HashSet<FlurstueckArtCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((FlurstueckArtCustomBean)metaObject.getBean());
        }
        return beans;
    }
    /**
     * TODO Jean; ????
     *
     * @param   flurstueckSchluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckSchluesselCustomBean completeFlurstueckSchluessel(
            final FlurstueckSchluesselCustomBean flurstueckSchluessel) {
//        try {
//            FlurstueckSchluesselCustomBean schluessel = (FlurstueckSchluesselCustomBean) em.createNamedQuery(
//                    "findOneFlurstueckSchluessel").setParameter("gId", fs.getGemarkung().getSchluessel()).setParameter("fId", fs.getFlur()).setParameter("fZaehler", fs.getFlurstueckZaehler()).setParameter("fNenner", fs.getFlurstueckNenner()).getSingleResult();
//            return schluessel;
//        } catch (Exception ex) {
//            //System.out.println("GemarkungCustomBean: "+fs.getGemarkung().getId()+" Schluessel: "+fs.getGemarkung().getSchluessel());
//            System.out.println("Fehler beim Kompletieren eines Flurstückschluessels: " + fs.getKeyString());
//            ex.printStackTrace();
//        }

        return null;
    }

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
        try {
            if (key != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("createFlurstueck: key ist != null");
                }
                final FlurstueckSchluesselCustomBean checkedKey = completeFlurstueckSchluessel(key);
                if (checkedKey != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("createFlurstueck: Vervollständigter key ist != null");
                    }
                    return null;
                }
                final FlurstueckCustomBean newFlurstueck = FlurstueckCustomBean.createNew();
                // datamodell refactoring 22.10.07
                final Date datumEntstehung = new Date();
                key.setEntstehungsDatum(datumEntstehung);
                key.setIstGesperrt(false);
                newFlurstueck.setFlurstueckSchluessel(key);
                // newFlurstueck.setEntstehungsDatum(new Date());
                // newFlurstueck.setIstGesperrt(false);
                checkIfFlurstueckWasStaedtisch(key, datumEntstehung);
                newFlurstueck.persist();
                if (LOG.isDebugEnabled()) {
                    // edit(newFlurstueck);
                    LOG.debug("createFlurstueck: neues Flurstück erzeugt");
                }
                return retrieveFlurstueck(key);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("createFlurstueck: key ist == null");
                }
                return null;
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim anlegen des Flurstücks", ex);
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isFlurstueckHistoric(final FlurstueckSchluesselCustomBean key) {
        // Flurstueck flurstueck = retrieveFlurstueck(key);
        // if(flurstueck.getGueltigBis() != null){
        if (key.getGueltigBis() != null) {
            return true;
        } else {
            return false;
        }
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
        SperreCustomBean lock = null;
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Rename Flurstück");
            }
            final FlurstueckCustomBean oldFlurstueck = retrieveFlurstueck(oldFlurstueckSchluessel);
            final FlurstueckCustomBean newFlurstueck;

            if (oldFlurstueck != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("AltesFlurstück existiert");
                }
                // TODO ugly
                // checks either there is a lock for the specific flurstück or not
                if (isLocked(oldFlurstueck.getFlurstueckSchluessel()) == null) {
                    lock = createLock(SperreCustomBean.createNew(
                                oldFlurstueck.getFlurstueckSchluessel(),
                                benutzerkonto));
                    if (lock == null) {
                        // TODO throw new EJBException(new ActionNotSuccessfulException("Anlegen einer SperreCustomBean
                        // nicht möglich"));
                        throw new ActionNotSuccessfulException(
                            "Anlegen einer Sperre für das alte Flurstück nicht möglich");
                    }
                } else {
                    // TODO throw new EJBException(new ActionNotSuccessfulException("Es exisitert bereits eine
                    // SperreCustomBean"));
                    throw new ActionNotSuccessfulException("Es exisitert bereits eine Sperre für das alte Flurstück");
                }
//HistoricResult result = ;

                if (setFlurstueckHistoric(oldFlurstueckSchluessel)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Flurstück wurde Historisch gesetzt");
                    }
                    // TODO Better FlurstückHistoryEntry??
                    // FlurstueckHistorie fHistorie = new FlurstueckHistorieCustomBean();
                    // TODO Flurstückaktion/Historie
                    // TODO NO UNIQUE RESULT EXCEPTION --> möglich ?
                    // FlurstueckHistorie fHistorie = new FlurstueckHistorieCustomBean();
                    if (!existHistoryEntry(oldFlurstueck)) {
                        newFlurstueck = createFlurstueck(newFlurstueckSchluessel);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Es exitieren kein History Eintrag --> keine Kante zu einem anderen Flurstück");
                            LOG.debug("Kein nachfolger für das Flurstück vorhanden --> Lege neues Flurstueck an");
                            LOG.debug("Erzeuge History Eintrag für altes Flurstück");
                        }
                        createHistoryEdge(oldFlurstueck, newFlurstueck);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Neuer History Eintrag für Flurstück erzeugt");
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            // Exception ex =  new ActionNotSuccessfulException("Flurstück");
                            LOG.debug("Renamen des Flurstücks nicht möglich");
                        }
                        releaseLock(lock);
                        throw new ActionNotSuccessfulException(
                            "Es existieren bereits Historieneinträge für dieses Flurstück");
                    }

//                    if(historyEntry != null){
//
//                    } else {
//
//                    }
                    if (newFlurstueck != null) {
                        if (LOG.isDebugEnabled()) {
//                        System.out.println("Das Flurstück wurde erfogreich angelegt --> Setze Nachfolger des Alten Flurstücks");
//                        historyEntry.setNachfolger(newFlurstueck);
//                        em.merge(historyEntry);
//                        System.out.println("Erzeuge History Eintrag für neues Flurstück");
//                        historyEntry = new FlurstueckHistorieCustomBean();
//                        historyEntry.setVorgaenger(oldFlurstueck);
//                        historyEntry.setFlurstueck(newFlurstueck);
//                        createFlurstueckHistoryEntry(historyEntry);
                            LOG.debug("Alle Aktionen für das umbenennen erfolgreich abgeschlossen.");
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            // TODO IF THIS CASE IS POSSIBLE ROLLBACK TRANSACTION
                            LOG.debug("Das neue Flurstück konnte nicht angelegt werden.");
                        }
                        releaseLock(lock);
                        throw new ActionNotSuccessfulException("Das neue Flurstück konnte nicht angelegt werden.");
                    }

                    releaseLock(lock);
                    return newFlurstueck;
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Flurstück konnte nicht historisch gesetzt werden.");
                    }
                    releaseLock(lock);
                    throw new ActionNotSuccessfulException("Flurstück konnte nicht historisch gesetzt werden.");
                }
            } else {
                throw new ActionNotSuccessfulException("Altes Flurstück existiert nicht.");
            }
        } catch (final ActionNotSuccessfulException ex) {
            throw ex;
        } catch (final Exception ex) {
            LOG.error("Unbekannter Fehler beim renamen des Flurstücks.", ex);
//            ActionNotSuccessfulException tmpEx;
//            if(ex instanceof ActionNotSuccessfulException){
//                //tmpEx=(ActionNotSuccessfulException)
//            } else {
//                tmpEx=new ActionNotSuccessfulException("Flurstück konnte nicht umbennant werden");
//                tmpEx.setStackTrace(ex.getStackTrace());
//            }
//            throw tmpEx;
            releaseLock(lock);
            // TODO set nestedException
            throw new ActionNotSuccessfulException(
                "Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator",
                ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SperreCustomBean isLocked(final FlurstueckSchluesselCustomBean key) {
        if (key != null) {
            if (key != null) {
                final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("sperre");
                if (metaclass == null) {
                    return null;
                }
                final String query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                            + metaclass.getPrimaryKey() + " "
                            + "FROM " + metaclass.getTableName() + " "
                            + "WHERE " + metaclass.getTableName() + ".fk_flurstueck_schluessel = " + key.getId();
                final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);
                if ((mos != null) && (mos.length > 0)) {
                    final SperreCustomBean sperre = (SperreCustomBean)mos[0].getBean();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Es ist eine Sperre vorhanden und wird von: " + sperre.getBenutzerkonto()
                                    + " gehalten");
                    }
                    return sperre;
                }
            }
        } else {
            if (LOG.isDebugEnabled()) {
                // TODO EXCEPTIOn !!!!!!! KNAUP
                LOG.debug("Flurstückkey == null");
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Es ist keine Sperre für das angegebne Flurstück vorhanden");
        }
        return null;
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
        final ArrayList<SperreCustomBean> locks = new ArrayList<SperreCustomBean>();
        try {
            if (joinMembers != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es sind joinMember vorhanden.");
                }
                Iterator<FlurstueckSchluesselCustomBean> it = joinMembers.iterator();
                while (it.hasNext()) {
                    final FlurstueckSchluesselCustomBean currentKey = it.next();
                    SperreCustomBean tmpLock;

                    if ((tmpLock = isLocked(currentKey)) == null) {
                        tmpLock = createLock(SperreCustomBean.createNew(currentKey, benutzerkonto));
                        if (tmpLock == null) {
                            if (LOG.isDebugEnabled()) {
                                // TODO throw new EJBException(new ActionNotSuccessfulException("Anlegen einer
                                // SperreCustomBean nicht möglich"));
                                LOG.debug("Anlegen einer Sperre für das Flurstück nicht möglich "
                                            + currentKey.getKeyString() + ".");
                            }
                            releaseLocks(locks);
                            throw new ActionNotSuccessfulException("Anlegen einer Sperre für das Flurstück "
                                        + currentKey.getKeyString() + " nicht möglich.");
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Sperre für Flurstück " + currentKey.getKeyString()
                                            + " Erfolgreich angelegt.");
                            }
                            locks.add(tmpLock);
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            // TODO throw new EJBException(new ActionNotSuccessfulException("Es exisitert bereits eine
                            // SperreCustomBean"));
                            LOG.debug("Es exisitert bereits eine Sperre für das Flurstück " + currentKey.getKeyString()
                                        + " und wird von dem Benutzer " + tmpLock.getBenutzerkonto() + " gehalten.");
                        }
                        releaseLocks(locks);
                        throw new ActionNotSuccessfulException("Es exisitert bereits eine Sperre für das Flurstück "
                                    + currentKey.getKeyString() + " und wird von dem Benutzer "
                                    + tmpLock.getBenutzerkonto() + " gehalten.");
                    }
                }
                it = joinMembers.iterator();
                final FlurstueckCustomBean newFlurstueck = createFlurstueck(newFlurstueckSchluessel);
                if (newFlurstueck != null) {
                    while (it.hasNext()) {
                        final FlurstueckCustomBean oldFlurstueck = retrieveFlurstueck(it.next());
                        // HistoricResult result = setFlurstueckHistoric(oldFlurstueck.getFlurstueckSchluessel());
                        if (setFlurstueckHistoric(oldFlurstueck.getFlurstueckSchluessel())) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Flurstück wurde Historisch gesetzt.");
                            }
                            // TODO IS THIS CASE POSSIBLE ?? --> MEANS ACTIVE FLURSTUECK
                            if (!existHistoryEntry(oldFlurstueck)) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug(
                                        "Es exitieren kein History Eintrag --> keine Kante zu einem anderen Flurstück.");
                                    LOG.debug("Erzeuge History Eintrag für alte Flurstücke.");
                                }
                                createHistoryEdge(oldFlurstueck, newFlurstueck);
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Neuer History Eintrag für Flurstück erzeugt.");
                                }
                            } else {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Es sind bereits Historieneinträge für das Flurstück "
                                                + oldFlurstueck.getFlurstueckSchluessel().getKeyString()
                                                + " vorhanden.");
                                }
                                releaseLocks(locks);
                                throw new ActionNotSuccessfulException(
                                    "Es sind bereits Historieneinträge für das Flurstück "
                                            + oldFlurstueck.getFlurstueckSchluessel().getKeyString()
                                            + " vorhanden.");
                            }
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Flurstück " + oldFlurstueck.getFlurstueckSchluessel().getKeyString()
                                            + " konnte nicht historisch gesetzt werden.");
                            }
                            releaseLocks(locks);
                            throw new ActionNotSuccessfulException("Flurstück "
                                        + oldFlurstueck.getFlurstueckSchluessel().getKeyString()
                                        + " konnte nicht historisch gesetzt werden.");
                        }
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Flurstücke Erfolgreich gejoined");
                    }
                    releaseLocks(locks);
                    return newFlurstueck;
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Das Anlegen des neuen Flurstücks " + newFlurstueckSchluessel.getKeyString()
                                    + " schlug fehl.");
                    }
                    releaseLocks(locks);
                    throw new ActionNotSuccessfulException("Das Anlegen des neuen Flurstücks "
                                + newFlurstueckSchluessel.getKeyString() + " schlug fehl.");
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es wurden keine Flurstücke angeben für die Zusammenlegung.");
                }
                throw new ActionNotSuccessfulException("Es wurden keine Flurstücke angeben für die Zusammenlegung.");
            }
        } catch (final ActionNotSuccessfulException ex) {
            throw ex;
        } catch (final Exception ex) {
            LOG.error("Unbekannter Fehler beim joinen von Flurstücken.", ex);
            releaseLocks(locks);
            throw new ActionNotSuccessfulException(
                "Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator.",
                ex);
        }
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
        SperreCustomBean lock = null;
        try {
            final ArrayList<SperreCustomBean> locks = new ArrayList<SperreCustomBean>();
            if (splitMembers != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es sind Flurstücke zum splitten vorhanden");
                }

                if (isLocked(oldFlurstueckSchluessel) == null) {
                    lock = createLock(SperreCustomBean.createNew(oldFlurstueckSchluessel, benutzerkonto));
                    if (lock == null) {
                        // TODO throw new EJBException(new ActionNotSuccessfulException("Anlegen einer SperreCustomBean
                        // nicht möglich"));
                        throw new ActionNotSuccessfulException(
                            "Anlegen einer Sperre für das alte Flurstück nicht möglich");
                    }
                } else {
                    // TODO throw new EJBException(new ActionNotSuccessfulException("Es exisitert bereits eine
                    // SperreCustomBean"));
                    throw new ActionNotSuccessfulException(
                        "Es exisitert bereits eine Sperre für das alte Flurstück, das gesplittet werden soll");
                }
                final Iterator<FlurstueckSchluesselCustomBean> it = splitMembers.iterator();
                final FlurstueckCustomBean oldFlurstueck = retrieveFlurstueck(oldFlurstueckSchluessel);
                // HistoricResult result = setFlurstueckHistoric(oldFlurstueck.getFlurstueckSchluessel());
                if (setFlurstueckHistoric(oldFlurstueck.getFlurstueckSchluessel())) {
                    if (!existHistoryEntry(oldFlurstueck)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Es exitieren kein History Eintrag --> keine Kante zu einem anderen Flurstück");
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Spliten des Flurstücks nicht möglich, es gibt schon einen Nachfolger");
                        }
                        releaseLock(lock);
                        return;
//TODO Exception!!!!! sonst sagt der Wizard alles Erfogreich
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Flurstück konnte nicht historisch gesetzt werden");
                    }
                    releaseLock(lock);
                    return;
//TODO Exception
                }
                while (it.hasNext()) {
                    final FlurstueckCustomBean newFlurstueck = createFlurstueck(it.next());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Neus Flurstück aus Split erzeugt");
                    }
                    if (newFlurstueck != null) {
                        createHistoryEdge(oldFlurstueck, newFlurstueck);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Neuer History Eintrag für Flurstück erzeugt");
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Fehler beim anlegen eines Flurstücks");
                        }
                        releaseLock(lock);
                        return;
                    }
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Splitten der Flurstücke erforgreich");
                }
                releaseLock(lock);
                return;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("split der Flurstücke nicht erfolgreich");
            }
            releaseLock(lock);
        } catch (Exception ex) {
            LOG.error("Fehler beim splitten von Flurstücken", ex);
            if (ex instanceof ActionNotSuccessfulException) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Eine Aktion ging schief Exception wird weitergereicht");
                }
                releaseLock(lock);
                throw (ActionNotSuccessfulException)ex;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Unbekannte Excepiton");
                }
                releaseLock(lock);
                throw new ActionNotSuccessfulException(
                    "Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator",
                    ex);
            }
        }
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
        // TODO ROLLBACK IF ONE OF THE METHODS FAILS
        try {
            final FlurstueckSchluesselCustomBean dummySchluessel = FlurstueckSchluesselCustomBean.createNew();
            // dummySchluessel.setWarStaedtisch(true);
            // UGLY minimum Konstante aus der jeweiligen Klasse benutzen
            for (final FlurstueckArtCustomBean current : getAllFlurstueckArten()) {
                if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_PSEUDO.equals(current.getBezeichnung())) {
                    dummySchluessel.setFlurstueckArt(current);
                    break;
                }
            }
            createFlurstueckSchluessel(dummySchluessel);
            // Flurstueck dummyFlurstueck = createFlurstueck(dummySchluessel);
            joinFlurstuecke(joinMembers, dummySchluessel, benutzerkonto);
            // TODO problem first have to check all keys
            splitFlurstuecke(dummySchluessel, splitMembers, benutzerkonto);
        } catch (final Exception ex) {
            if (ex instanceof ActionNotSuccessfulException) {
                LOG.error("Eine ActionSchlug fehl", ex);
                throw (ActionNotSuccessfulException)ex;
            }
            LOG.error("Fehler beim joinSplit", ex);
            throw new ActionNotSuccessfulException(
                "Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator",
                ex);
        }
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("Sammle Alle Knoten (Rekursiv) für: " + schluessel);
        }
        final Collection<FlurstueckHistorieCustomBean> allEdges = new HashSet<FlurstueckHistorieCustomBean>();
        try {
            Collection<FlurstueckHistorieCustomBean> childEdges = getHistoryAccessors(schluessel);
            if (childEdges != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es gibt Kanten zu diesem Knoten");
                }
                allEdges.addAll(childEdges);
                final Iterator<FlurstueckHistorieCustomBean> it = childEdges.iterator();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Rufe Methode Rekursiv auf für alle Gefundenen Knoten");
                }
                while (it.hasNext()) {
                    childEdges = getAllHistoryEntries(it.next().getVorgaenger().getFlurstueckSchluessel());
                    if (childEdges != null) {
                        allEdges.addAll(childEdges);
                    }
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Es gibt keine Kanten zu diesem Knoten");
                }
                return allEdges;
            }

            return allEdges;
        } catch (Exception ex) {
            LOG.error("Fehler beim sammeln aller Kanten", ex);
        }

        return allEdges;
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
            final HistoryLevel level,
            final HistoryType type,
            int levelCount) throws ActionNotSuccessfulException {
        final Collection<FlurstueckHistorieCustomBean> allEdges = new HashSet<FlurstueckHistorieCustomBean>();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Historiensuche mit Parametern: HistoryType=" + type + " Level=" + level + " LevelCount="
                        + levelCount);
        }
        try {
            switch (type) {
                case BOTH: {
                    switch (level) {
                        case DIRECT_RELATIONS: {
                            levelCount = 1;
                            addHistoryEntriesForNeighbours(
                                schluessel,
                                level,
                                CidsBroker.HistoryType.SUCCESSOR,
                                levelCount,
                                allEdges);
                            addHistoryEntriesForNeighbours(
                                schluessel,
                                level,
                                CidsBroker.HistoryType.PREDECESSOR,
                                levelCount,
                                allEdges);
                            break;
                        }
                        case CUSTOM: {
                            addHistoryEntriesForNeighbours(
                                schluessel,
                                level,
                                CidsBroker.HistoryType.PREDECESSOR,
                                levelCount,
                                allEdges);
                            addHistoryEntriesForNeighbours(
                                schluessel,
                                level,
                                CidsBroker.HistoryType.SUCCESSOR,
                                levelCount,
                                allEdges);
                            break;
                        }
                        default: {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Sammle Alle Knoten Vorgänger/Nachfolger (Rekursiv) für: " + schluessel);
                            }
                            addHistoryEntriesForNeighbours(
                                schluessel,
                                level,
                                CidsBroker.HistoryType.PREDECESSOR,
                                -1,
                                allEdges);
                            addHistoryEntriesForNeighbours(
                                schluessel,
                                level,
                                CidsBroker.HistoryType.SUCCESSOR,
                                -1,
                                allEdges);
                        }
                    }
                    break;
                }
                case SUCCESSOR: {
                    switch (level) {
                        case DIRECT_RELATIONS: {
                            levelCount = 1;
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Sammle m Knoten Nachfolger (Rekursiv) für: " + schluessel);
                            }
                            addHistoryEntriesForNeighbours(schluessel, level, type, levelCount, allEdges);
                            break;
                        }
                        case CUSTOM: {
                            addHistoryEntriesForNeighbours(schluessel, level, type, levelCount, allEdges);
                            break;
                        }
                        default: {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Sammle Alle/Custom Knoten Nachfolger (Rekursiv) für: " + schluessel);
                            }
                            addHistoryEntriesForNeighbours(schluessel, level, type, -1, allEdges);
                        }
                    }
                    break;
                }
                case PREDECESSOR: {
                    switch (level) {
                        case DIRECT_RELATIONS: {
                            levelCount = 1;
                            addHistoryEntriesForNeighbours(schluessel, level, type, levelCount, allEdges);
                            break;
                        }
                        case CUSTOM: {
                            addHistoryEntriesForNeighbours(schluessel, level, type, levelCount, allEdges);
                            break;
                        }
                        default: {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Sammle Alle/Custom Knoten Vorgänger (Rekursiv) für: " + schluessel);
                            }
                            addHistoryEntriesForNeighbours(schluessel, level, type, -1, allEdges);
                        }
                    }

                    break;
                }
                default: {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("HistoryType is not defined");
                    }
                    throw new ActionNotSuccessfulException("Fehler beim abfragen der Historieneinträge.");
                }
            }
        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failure during querying of history entries", ex);
            }
            throw new ActionNotSuccessfulException("Fehler beim abfragen der Historieneinträge.", ex);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Historien Suche abgeschlossen");
        }
        return allEdges;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VertragCustomBean> getVertraegeForKey(final FlurstueckSchluesselCustomBean key) {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("vertrag");
        if (metaclass == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + metaclass.getID() + ", "
                    + "   jt_flurstueck_vertrag.fk_vertrag "
                    + "FROM "
                    + "   flurstueck, "
                    + "   jt_flurstueck_vertrag "
                    + "WHERE "
                    + "   jt_flurstueck_vertrag.fk_flurstueck = flurstueck.id "
                    + "   AND flurstueck.fk_flurstueck_schluessel = " + key.getId();

        final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);
        final Collection<VertragCustomBean> beans = new HashSet<VertragCustomBean>();
        for (final MetaObject metaObject : mos) {
            beans.add((VertragCustomBean)metaObject.getBean());
        }

        if (beans != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl Vertraege ist: " + beans.size());
            }
            return beans;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Vertraege für Flurstück vorhanden");
            }
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   vertrag  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossReferencesForVertrag(final VertragCustomBean vertrag) {
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("flurstueck_schluessel");
        if (metaclass == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + metaclass.getID() + ", "
                    + "   flurstueck.fk_flurstueck_schluessel "
                    + "FROM "
                    + "   public.flurstueck, "
                    + "   public.jt_flurstueck_vertrag "
                    + "WHERE "
                    + "   public.flurstueck.ar_vertraege = public.jt_flurstueck_vertrag.fk_flurstueck  "
                    + "   AND public.jt_flurstueck_vertrag.fk_vertrag = " + vertrag.getId();

        final MetaObject[] mosVertrag = CidsBroker.getInstance().getLagisMetaObject(query);
        final Collection<FlurstueckSchluesselCustomBean> keys = new HashSet<FlurstueckSchluesselCustomBean>();
        for (final MetaObject metaObject : mosVertrag) {
            keys.add((FlurstueckSchluesselCustomBean)metaObject.getBean());
        }
        if (keys != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl FlurstueckSchluessel ist: " + keys.size());
            }
            return keys;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Flurstückreferenzen für den Vertrag vorhanden");
            }
            return null;
        }
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
        if ((vertraege != null) && (vertraege.size() > 0)) {
            final Collection<FlurstueckSchluesselCustomBean> result = new HashSet<FlurstueckSchluesselCustomBean>();
            final Iterator<VertragCustomBean> it = vertraege.iterator();
            while (it.hasNext()) {
                final Collection<FlurstueckSchluesselCustomBean> curKeys = getCrossReferencesForVertrag(it.next());
                if ((curKeys != null) && (curKeys.size() > 0)) {
                    result.addAll(curKeys);
                }
            }
            return result;
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   aktenzeichen  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getFlurstueckSchluesselByAktenzeichen(final String aktenzeichen) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suche nach Flurstücken(Schluesseln) mit dem Aktenzeichen: " + aktenzeichen);
        }

        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("flurstueck_schluessel");
        if (metaclass == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + metaclass.getID() + ", "
                    + "    flurstueck.fk_flurstueck_schluessel "
                    + "FROM "
                    + "    flurstueck, "
                    + "    jt_flurstueck_vertrag, "
                    + "    vertrag "
                    + "WHERE "
                    + "    flurstueck.ar_vertraege = jt_flurstueck_vertrag.fk_flurstueck "
                    + "    AND jt_flurstueck_vertrag.fk_vertrag = vertrag.id "
                    + "    AND vertrag.aktenzeichen LIKE '%" + aktenzeichen + "%'";

        final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);
        final Collection<FlurstueckSchluesselCustomBean> flurstueckSchluessel =
            new HashSet<FlurstueckSchluesselCustomBean>();
        for (final MetaObject metaObject : mos) {
            flurstueckSchluessel.add((FlurstueckSchluesselCustomBean)metaObject.getBean());
        }

        if (flurstueckSchluessel != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl Flurststückschlüssel für das Aktenzeichen ist: " + flurstueckSchluessel.size());
            }
            return flurstueckSchluessel;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Flurstückschlüssel mit dem angegebenen Aktenzeichen vorhanden");
            }
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   miPa  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossReferencesForMiPa(final MipaCustomBean miPa) {
        final MetaClass mcFlurstueckSchluessel = CidsBroker.getInstance()
                    .getLagisMetaClass(CidsBroker.CLASS__FLURSTUECK_SCHLUESSEL);
        if (mcFlurstueckSchluessel == null) {
            return null;
        }

        final String query = "SELECT "
                    + "   " + mcFlurstueckSchluessel.getID() + ", "
                    + "   flurstueck.fk_flurstueck_schluessel "
                    + "FROM "
                    + "   public.flurstueck, "
                    + "   public.jt_flurstueck_mipa "
                    + "WHERE "
                    + "   public.flurstueck.ar_mipas = public.jt_flurstueck_mipa.fk_flurstueck  "
                    + "   AND public.jt_flurstueck_mipa.fk_mipa = " + miPa.getId();

        final MetaObject[] mosMipa = CidsBroker.getInstance().getLagisMetaObject(query);
        final Collection<FlurstueckSchluesselCustomBean> keys = new HashSet<FlurstueckSchluesselCustomBean>();
        for (final MetaObject metaObject : mosMipa) {
            keys.add((FlurstueckSchluesselCustomBean)metaObject.getBean());
        }

        if (keys != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl FlurstueckSchluessel ist: " + keys.size());
            }
            return keys;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Flurstückreferenzen für MiPa vorhanden");
            }
            return null;
        }
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
        if ((miPas != null) && (miPas.size() > 0)) {
            final Collection<FlurstueckSchluesselCustomBean> result = new HashSet<FlurstueckSchluesselCustomBean>();
            final Iterator<MipaCustomBean> it = miPas.iterator();
            while (it.hasNext()) {
                final Collection<FlurstueckSchluesselCustomBean> curKeys = getCrossReferencesForMiPa(it.next());
                if ((curKeys != null) && (curKeys.size() > 0)) {
                    result.addAll(curKeys);
                }
            }
            return result;
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<MipaCustomBean> getMiPaForKey(final FlurstueckSchluesselCustomBean key) {
        final MetaClass mcMipa = CidsBroker.getInstance().getLagisMetaClass("mipa");
        if (mcMipa == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + mcMipa.getID() + ", "
                    + "   jt_flurstueck_mipa.fk_mipa "
                    + "FROM "
                    + "   flurstueck, "
                    + "   jt_flurstueck_mipa "
                    + "WHERE "
                    + "   jt_flurstueck_mipa.fk_flurstueck = flurstueck.id "
                    + "   AND flurstueck.fk_flurstueck_schluessel = " + key.getId();

        final MetaObject[] mosMipa = CidsBroker.getInstance().getLagisMetaObject(query);
        final Collection<MipaCustomBean> mipas = new HashSet<MipaCustomBean>();
        for (final MetaObject metaObject : mosMipa) {
            mipas.add((MipaCustomBean)metaObject.getBean());
        }

        if (mipas != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl MiPas ist: " + mipas.size());
            }
            return mipas;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine MiPas für Flurstück vorhanden");
            }
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
    public Collection<BaumCustomBean> getBaumForKey(final FlurstueckSchluesselCustomBean key) {
        final MetaClass mcBaum = CidsBroker.getInstance().getLagisMetaClass("baum");
        if (mcBaum == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + mcBaum.getID() + ", "
                    + "   jt_flurstueck_baum.fk_baum "
                    + "FROM "
                    + "   flurstueck, "
                    + "   jt_flurstueck_baum "
                    + "WHERE "
                    + "   jt_flurstueck_baum.fk_flurstueck = flurstueck.id "
                    + "   AND flurstueck.fk_flurstueck_schluessel = " + key.getId();

        final MetaObject[] mosBaum = CidsBroker.getInstance().getLagisMetaObject(query);
        final Collection<BaumCustomBean> baeume = new HashSet<BaumCustomBean>();
        for (final MetaObject metaObject : mosBaum) {
            baeume.add((BaumCustomBean)metaObject.getBean());
        }

        if (baeume != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl Baueme ist: " + baeume.size());
            }
            return baeume;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Baueme für Flurstück vorhanden");
            }
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   baum  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getCrossReferencesForBaum(final BaumCustomBean baum) {
        final MetaClass mcFlurstueckSchluessel = CidsBroker.getInstance()
                    .getLagisMetaClass(CidsBroker.CLASS__FLURSTUECK_SCHLUESSEL);
        if (mcFlurstueckSchluessel == null) {
            return null;
        }

        final String query = "SELECT "
                    + "   " + mcFlurstueckSchluessel.getID() + ", "
                    + "   flurstueck.fk_flurstueck_schluessel "
                    + "FROM "
                    + "   public.flurstueck, "
                    + "   public.jt_flurstueck_baum "
                    + "WHERE "
                    + "   public.flurstueck.ar_baeume = public.jt_flurstueck_baum.fk_flurstueck  "
                    + "   AND public.jt_flurstueck_baum.fk_baum = " + baum.getId();

        final MetaObject[] mosBaum = CidsBroker.getInstance().getLagisMetaObject(query);
        final Collection<FlurstueckSchluesselCustomBean> keys = new HashSet<FlurstueckSchluesselCustomBean>();
        for (final MetaObject metaObject : mosBaum) {
            keys.add((FlurstueckSchluesselCustomBean)metaObject.getBean());
        }

        if (keys != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Anzahl FlurstueckSchluessel ist: " + keys.size());
            }
            return keys;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Flurstückreferenzen für Baum vorhanden");
            }
            return null;
        }
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
        if ((baeume != null) && (baeume.size() > 0)) {
            final Collection<FlurstueckSchluesselCustomBean> result = new HashSet<FlurstueckSchluesselCustomBean>();
            final Iterator<BaumCustomBean> it = baeume.iterator();
            while (it.hasNext()) {
                final Collection<FlurstueckSchluesselCustomBean> curKeys = getCrossReferencesForBaum(it.next());
                if ((curKeys != null) && (curKeys.size() > 0)) {
                    result.addAll(curKeys);
                }
            }
            return result;
        }
        return null;
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
        return setFlurstueckHistoric(key, new Date());
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
        try {
            if (key.getWarStaedtisch()) {
                if (LOG.isDebugEnabled()) {
                    // TODO hier muss wieder städtisch gesetzt werden und die ReBe gelöscht werden
                    LOG.debug("Flurstueck war schon mal staedtisch wird historisch gesetzt");
                }
                // Flurstueck flurstueck = retrieveFlurstueck(key);
                // if(flurstueck.getGueltigBis() == null){
                if (key.getGueltigBis() == null) {
                    if (
                        !FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                                    key.getFlurstueckArt().getBezeichnung())) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Flurstueck ist nicht städtisch");
                        }
                        FlurstueckArtCustomBean abteilungIX = null;
                        for (final FlurstueckArtCustomBean current : getAllFlurstueckArten()) {
                            if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX.equals(
                                            current.getBezeichnung())) {
                                abteilungIX = current;
                            }
                        }
                        if (abteilungIX == null) {
                            throw new ActionNotSuccessfulException(
                                "Flurstücksart AbteilungIX konnte nicht gefunden werden.");
                        }

                        if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX.equals(
                                        key.getFlurstueckArt().getBezeichnung())) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Flurstück ist Abteilung IX  --> alle Rechte werden entfernt");
                            }
                            final FlurstueckCustomBean flurstueck = retrieveFlurstueck(key);
                            if (flurstueck.getRechteUndBelastungen() != null) {
                                flurstueck.getRechteUndBelastungen().clear();
                            }

                            flurstueck.getFlurstueckSchluessel().setFlurstueckArt(abteilungIX);
                            if (flurstueck.getFlurstueckSchluessel().getDatumLetzterStadtbesitz() != null) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Setze Gueltigbis Datum des Flurstueks auf letzten Stadtbesitz");
                                }
                                flurstueck.getFlurstueckSchluessel()
                                        .setGueltigBis(flurstueck.getFlurstueckSchluessel()
                                            .getDatumLetzterStadtbesitz());
                            } else {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Achtung war schon in Stadtbesitz hat aber kein Datum");
                                }
                                throw new ActionNotSuccessfulException(
                                    "Das Flurstück war schon mal in Stadtbesitz, aber es existiert kein Datum wann");
                            }
//flurstueck.getFlurstueckSchluessel().setGueltigBis(new Date());
                            // return new HistoricResult(true,false);
                            return true;
                                // }
                        }
                        throw new ActionNotSuccessfulException("Die Flurstückart "
                                    + FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH
                                    + " ist nicht in der Datenbank");
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Flurstück ist städtisch und wird historisch gesetzt");
                        }
                        key.setDatumLetzterStadtbesitz(date);
                        key.setGueltigBis(date);
                        final FlurstueckCustomBean flurstueck = retrieveFlurstueck(key);
                        if (flurstueck != null) {
//TODO Nutzungsrefactoring
                            // setCurrentNutzungenHistoric(flurstueck.getNutzungen(), currentDate);
                        }
                        flurstueck.setFlurstueckSchluessel(key);
                        // return new HistoricResult(true,false);
                        return true;
                    }
                } else {
                    // return new HistoricResult(false,false);
                    return true;
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Flurstueck war noch nie staedtisch wird historisch gesetzt");
                }
                // deleteFlurstueck(retrieveFlurstueck(key));
                key.setGueltigBis(date);
                // return new HistoricResult(true,true);
                return true;
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim historisch setzen eines Flurstücks", ex);
//            ActionNotSuccessfulException tmpEx = new ActionNotSuccessfulException("Flurstück konnte nicht historisch gesetzt werden");
//            tmpEx.setStackTrace(ex.getStackTrace());
//            throw tmpEx;
            if (ex instanceof ActionNotSuccessfulException) {
                throw (ActionNotSuccessfulException)ex;
            } else {
                throw new ActionNotSuccessfulException(
                    "Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator.",
                    ex);
            }
        }
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suche Nachfolger für Flurstück");
            LOG.debug("ID des Schluessels ist: " + flurstueckSchluessel.getId());
        }

        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("flurstueck_historie");
        if (metaclass == null) {
            return false;
        }
        final String query = "SELECT "
                    + "   " + metaclass.getID() + ", "
                    + "   " + metaclass.getTableName() + "." + metaclass.getPrimaryKey() + " "
                    + "FROM "
                    + "   " + metaclass.getTableName() + ", "
                    + "   flurstueck "
                    + "WHERE "
                    + "   " + metaclass.getTableName() + ".fk_vorgaenger = flurstueck.id "
                    + "   AND flurstueck.fk_flurstueck_schluessel = " + flurstueckSchluessel.getId();

        final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);
        if (mos != null) {
            if (mos.length > 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ergebnisliste ist leer");
                }
                return false;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Suche lieferte mindestens ein Ergebnis zurück");
                }
                return true;
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Suche lieferte kein Ergebnis zurück");
            }
            return false;
        }
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

        try {
            if (key.getGueltigBis() != null) {
                if (!hasFlurstueckSucccessors(key)) {
                    if ((key.getFlurstueckArt() == null) || (key.getFlurstueckArt().getBezeichnung() == null)) {
                        throw new ActionNotSuccessfulException(
                            "Das Flurstück kann nicht aktiviert werden, weil es keine Flurstücksart besitzt");
                    }

                    if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                                    key.getFlurstueckArt().getBezeichnung())) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Städtisches Flurstück wurde reactiviert");
                        }
                        final Date currentDate = new Date();
                        key.setEntstehungsDatum(currentDate);
                        key.setDatumLetzterStadtbesitz(currentDate);
                    }

                    key.setGueltigBis(null);
                    return true;
                } else {
                    throw new ActionNotSuccessfulException(
                        "Das Flurstück kann nicht aktiviert werden, weil es Nachfolger hat");
                }
            } else {
                throw new ActionNotSuccessfulException("Das Flurstück war aktiv");
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim historisch setzen eines Flurstücks", ex);
            throw new ActionNotSuccessfulException(
                "Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator.",
                ex);
        }
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
//        if (key != null) {
//            SperreCustomBean tmpLock;
//            if ((tmpLock = isLocked(key)) == null) {
//                tmpLock = createLock(new SperreCustomBean(key, username));
//                if (tmpLock == null) {
//                    //TODO
//                    //throw new EJBException(new ActionNotSuccessfulException("Anlegen einer SperreCustomBean nicht möglich"));
//                    System.out.println("Anlegen einer SperreCustomBean für das Flurstück nicht möglich " + key.getKeyString() + ".");
//                    throw new ActionNotSuccessfulException("Anlegen einer SperreCustomBean für das Flurstück " + key.getKeyString() + " nicht möglich.");
//                } else {
//                    System.out.println("SperreCustomBean für Flurstück " + key.getKeyString() + " Erfolgreich angelegt.");
//                }
//            } else {
//                //TODO
//                //throw new EJBException(new ActionNotSuccessfulException("Es exisitert bereits eine SperreCustomBean"));
//                System.out.println("Es exisitert bereits eine SperreCustomBean für das Flurstück " + key.getKeyString() + " und wird von dem Benutzer " + tmpLock.getBenutzerkonto() + " gehalten.");
//                throw new ActionNotSuccessfulException("Es exisitert bereits eine SperreCustomBean für das Flurstück " + key.getKeyString() + " und wird von dem Benutzer " + tmpLock.getBenutzerkonto() + " gehalten.");
//            }
//            try {
//                FlurstueckCustomBean flurstueck = retrieveFlurstueck(key);
//                if (flurstueck == null) {
//                    System.out.println("Die Stillen Reserven des Flurstücks " + key.getKeyString() + " konnten nicht gebucht werden, weil kein Flurstück in der Datenbank gefunden wurde.");
//                    throw new ActionNotSuccessfulException("Die Stillen Reserven des Flurstücks " + key.getKeyString() + " konnten nicht gebucht werden, weil kein Flurstück in der Datenbank gefunden wurde.");
//                } else {
//                    StringBuffer message = new StringBuffer();
//                    message.append("Bei dem Flurstück ");
//                    message.append(flurstueck.getFlurstueckSchluessel().getKeyString());
//                    message.append(" wurden die Stillen Reserven der Nutzungen: \n\n");
//
//                    Set<Nutzung> nutzungen = null;
//                    if ((nutzungen = flurstueck.getNutzungen()) == null) {
//                        System.out.println("Keine Nutzungen vorhanden die gebucht werden müssten");
//                    } else {
//                        Iterator<Nutzung> it = nutzungen.iterator();
//                        Vector<Nutzung> bookedNutzungen = new Vector<Nutzung>();
//                        Date buchungsdatum = new Date();
//                        while (it.hasNext()) {
//                            NutzungCustomBean current = it.next();
//                            if (current.getIstGebucht() == null) {
//                                System.out.println("Ein Buchungstatus einer NutzungCustomBean ist unbekannt. ID: " + current.getId());
//                                throw new ActionNotSuccessfulException("Der Buchungsstatus einer NutzungCustomBean ist unbekannt");
//                            } else {
//                                if (current.getIstGebucht()) {
//                                    System.out.println("NutzungCustomBean ist bereits gebucht");
//                                } else {
//                                    System.out.println("Buche NutzungCustomBean: " + current.getId());
//                                    message.append(current + "\n");
//                                    current.setBuchungsDatum(buchungsdatum);
//                                    current.setIstGebucht(true);
//                                    current.setStilleReserve(0.0);
//                                    bookedNutzungen.add(current);
//                                }
//                            }
//                        }
//                        //Vector im Moment unnötig
//                        if (bookedNutzungen.size() < 1) {
//                            System.out.println("Es wurden keine Nutzungen gebucht");
//                        } else {
//                            System.out.println("Es wurden " + bookedNutzungen.size() + " Nutzungen gebucht, sende Emailbenachrichtigungen");
//                            it = bookedNutzungen.iterator();
//                            message.append("gebucht.\n\n");
//                            sendEmail("Lagis - Stille Reserven wurden gebucht", message.toString(), nkfSessions);
//                        }
//                    }
//                }
//                releaseLock(tmpLock);
//            } catch (Exception ex) {
//                System.out.println("Ein Fehler ist aufgetreten während dem buchen der Stillen Reserven --> löse SperreCustomBean");
//                ex.printStackTrace();
//                releaseLock(tmpLock);
//                if (ex instanceof ActionNotSuccessfulException) {
//                    throw (ActionNotSuccessfulException) ex;
//                } else {
//                    throw new ActionNotSuccessfulException("Ein Unbekannter Ausnamefehler ist aufgetreten. Bitte wenden Sie sich an Ihren Systemadministrator.", ex);
//                }
//            }
//        } else {
//            throw new ActionNotSuccessfulException("Kein gültiger Flurstückschlüssel angegeben");
//        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wfsFlurstuecke  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public Collection<FlurstueckSchluesselCustomBean> getFlurstueckSchluesselForWFSFlurstueck(
            final Collection<WfsFlurstuecke> wfsFlurstuecke) throws ActionNotSuccessfulException {
        final Collection<FlurstueckSchluesselCustomBean> result = new HashSet<FlurstueckSchluesselCustomBean>();
        try {
            if ((wfsFlurstuecke != null) && (wfsFlurstuecke.size() > 0)) {
                for (final WfsFlurstuecke curWfsFlurstueck : wfsFlurstuecke) {
                    final FlurstueckSchluesselCustomBean curFlurstueckSchluessel =
                        getFlurstueckSchluesselForWFSFlurstueck(curWfsFlurstueck);
                    if (curFlurstueckSchluessel == null) {
                        if (LOG.isDebugEnabled()) {
                            // throw new ActionNotSuccessfulException("FlurstueckSchluesselCustomBean abfrage nicht
                            // erfolgreich. Kein Gegenstück zu WfSFlurstuecke vorhanden.");
                            LOG.debug(
                                "FlurstueckSchluessel abfrage nicht erfolgreich. Kein Gegenstück zu WfSFlurstuecke vorhanden.");
                        }
                        continue;
                    }
                    result.add(curFlurstueckSchluessel);
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                        "WfsFlurstueck == null oder leer. Kann korrespondierenden Flurstueckschluessel nicht abrufen");
                }
                return result;
            }
        } catch (Exception ex) {
            final String errorMessage = "Fehler beim Kompletieren eines Flurstückschluessels: ";
            if (LOG.isDebugEnabled()) {
                LOG.debug(errorMessage, ex);
            }
            throw new ActionNotSuccessfulException(errorMessage, ex);
        }
        return result;
    }
    /**
     * TODO Jean: WFSFlurstueck CustomBean
     *
     * @param   wfsFlurstueck  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public FlurstueckSchluesselCustomBean getFlurstueckSchluesselForWFSFlurstueck(
            final WfsFlurstuecke wfsFlurstueck) throws ActionNotSuccessfulException {
//        try {
//            if (wfsFlurstueck != null) {
//                final FlurstueckSchluesselCustomBean fkey = new FlurstueckSchluesselCustomBean();
//                final GemarkungCustomBean gem = new GemarkungCustomBean();
//                gem.setSchluessel(wfsFlurstueck.getGem());
//                fkey.setGemarkung(gem);
//                fkey.setFlur(wfsFlurstueck.getFlur());
//                fkey.setFlurstueckZaehler(wfsFlurstueck.getFlurstz());
//                fkey.setFlurstueckNenner(wfsFlurstueck.getFlurstn());
//                return completeFlurstueckSchluessel(fkey);
//            } else {
//                if (LOG.isDebugEnabled()) {
//                    LOG.debug("WfsFlurstueck == null. Kann korrespondierenden Flurstueckschluessel nicht abrufen");
//                }
//                return null;
//            }
//        } catch (Exception ex) {
//            final String errorMessage =
//                "Fehler beim Kompletieren eines Flurstückschluessels. Flurstueck vielleicht nicht vorhanden ";
//            if (LOG.isDebugEnabled()) {
//                LOG.debug(errorMessage + wfsFlurstueck, ex);
//            }
//            return null;
//        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueckHistorie  DOCUMENT ME!
     * @param  allEdges            DOCUMENT ME!
     * @param  direction           DOCUMENT ME!
     */
    private void replacePseudoFlurstuecke(final Collection<FlurstueckHistorieCustomBean> flurstueckHistorie,
            final Collection<FlurstueckHistorieCustomBean> allEdges,
            final CidsBroker.HistoryType direction) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("replacePseudoFlurstuecke: direction=" + direction + " Kanten=" + flurstueckHistorie);
        }
        if (flurstueckHistorie != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("es existieren Kanten");
            }
            final Iterator<FlurstueckHistorieCustomBean> itr = flurstueckHistorie.iterator();
            final ArrayList<FlurstueckHistorieCustomBean> pseudoKeysToRemove =
                new ArrayList<FlurstueckHistorieCustomBean>();
            final Collection<FlurstueckHistorieCustomBean> realNeighbours = new HashSet<FlurstueckHistorieCustomBean>();
            while (itr.hasNext()) {
                final FlurstueckHistorieCustomBean currentFlurstueckHistorie = itr.next();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("" + currentFlurstueckHistorie.getNachfolger());
                }
                if ((direction == CidsBroker.HistoryType.PREDECESSOR)
                            && (currentFlurstueckHistorie.getNachfolger() != null)
                            && (currentFlurstueckHistorie.getVorgaenger() != null)
                            && (currentFlurstueckHistorie.getVorgaenger().getFlurstueckSchluessel() != null)
                            && !currentFlurstueckHistorie.getVorgaenger().getFlurstueckSchluessel()
                            .isEchterSchluessel()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "Vorgänger ist ein PseudoFlurstück und besitzt vorgänge --> suche heraus und ersetze");
                    }
                    allEdges.add(currentFlurstueckHistorie);
                    pseudoKeysToRemove.add(currentFlurstueckHistorie);
                    final Collection<FlurstueckHistorieCustomBean> result = getHistoryAccessors(
                            currentFlurstueckHistorie.getVorgaenger().getFlurstueckSchluessel());
                    if (result != null) {
                        realNeighbours.addAll(result);
                    }
                } else if ((direction == CidsBroker.HistoryType.SUCCESSOR)
                            && (currentFlurstueckHistorie.getNachfolger() != null)
                            && (currentFlurstueckHistorie.getVorgaenger() != null)
                            && (currentFlurstueckHistorie.getNachfolger().getFlurstueckSchluessel() != null)
                            && !currentFlurstueckHistorie.getNachfolger().getFlurstueckSchluessel()
                            .isEchterSchluessel()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "Vorgänger ist ein PseudoFlurstück und besitzt vorgänge --> suche heraus und ersetze");
                    }
                    allEdges.add(currentFlurstueckHistorie);
                    pseudoKeysToRemove.add(currentFlurstueckHistorie);
                    final Collection<FlurstueckHistorieCustomBean> result = getHistorySuccessor(
                            currentFlurstueckHistorie.getNachfolger().getFlurstueckSchluessel());
                    if (result != null) {
                        realNeighbours.addAll(result);
                    }
                }
            }
            flurstueckHistorie.removeAll(pseudoKeysToRemove);
            flurstueckHistorie.addAll(realNeighbours);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Keine Pseudoflurstücke zum ersetzen");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueckSchluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<FlurstueckHistorieCustomBean> getHistoryAccessors(
            final FlurstueckSchluesselCustomBean flurstueckSchluessel) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suche Vorgänger für Flurstück");
            LOG.debug("ID des Schluessels ist: " + flurstueckSchluessel);
        }

        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("flurstueck_historie");
        if (metaclass == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + metaclass.getID() + ", "
                    + "   " + metaclass.getTableName() + "." + metaclass.getPrimaryKey() + " "
                    + "FROM "
                    + "   " + metaclass.getTableName() + ", "
                    + "   flurstueck "
                    + "WHERE "
                    + "   " + metaclass.getTableName() + ".fk_nachfolger = flurstueck.id "
                    + "   AND flurstueck.fk_flurstueck_schluessel = " + flurstueckSchluessel.getId();

        final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);
        final Collection<FlurstueckHistorieCustomBean> historyEntries = new HashSet<FlurstueckHistorieCustomBean>();
        for (final MetaObject metaObject : mos) {
            historyEntries.add((FlurstueckHistorieCustomBean)metaObject.getBean());
        }
        if (historyEntries != null) {
            if (historyEntries.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ergebnisliste ist leer");
                }
                return null;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Suche lieferte mindestens ein Ergebnis zurück");
                }
            }

//                while(it.hasNext()){
//                    FlurstueckHistorieCustomBean curHistoryEntry = it.next();
//                    //TODO possible that a key is null (inconsitence) ??
//                    if(curHistoryEntry != null && curHistoryEntry.getVorgaenger() != null){
//                        System.out.println("Jetziger HistoryEintrag != null und Vorgänger != null");
//                        result.add(curHistoryEntry.getVorgaenger().getFlurstueckSchluessel());
//                    } else {
//                        //TODO EXCEPTION
//                        System.out.println("Jetziger HistoryEintrag oder Vorgänger == null");
//                    }
//                }
            return historyEntries;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Suche lieferte kein Ergebnis zurück");
            }
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueckSchluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<FlurstueckHistorieCustomBean> getHistorySuccessor(
            final FlurstueckSchluesselCustomBean flurstueckSchluessel) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suche Nachfolger für Flurstück");
        }

        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("flurstueck_historie");
        if (metaclass == null) {
            return null;
        }
        final String query = "SELECT "
                    + "   " + metaclass.getID() + ", "
                    + "   " + metaclass.getTableName() + "." + metaclass.getPrimaryKey() + " "
                    + "FROM "
                    + "   " + metaclass.getTableName() + ", "
                    + "   flurstueck "
                    + "WHERE "
                    + "   " + metaclass.getTableName() + ".fk_vorgaenger = flurstueck.id "
                    + "   AND flurstueck.fk_flurstueck_schluessel = " + flurstueckSchluessel.getId();

        final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);
        final Collection<FlurstueckHistorieCustomBean> historyEntries = new HashSet<FlurstueckHistorieCustomBean>();
        for (final MetaObject metaObject : mos) {
            historyEntries.add((FlurstueckHistorieCustomBean)metaObject.getBean());
        }

        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("ID des Schluessels ist: " + flurstueckSchluessel);
            }
            if (historyEntries != null) {
                if (historyEntries.isEmpty()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Ergebnisliste ist leer");
                    }
                    return null;
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Suche lieferte mindestens ein Ergebnis zurück");
                    }
                }

//                while(it.hasNext()){
//                    FlurstueckHistorieCustomBean curHistoryEntry = it.next();
//                    //TODO possible that a key is null (inconsitence) ??
//                    if(curHistoryEntry != null && curHistoryEntry.getVorgaenger() != null){
//                        System.out.println("Jetziger HistoryEintrag != null und Vorgänger != null");
//                        result.add(curHistoryEntry.getVorgaenger().getFlurstueckSchluessel());
//                    } else {
//                        //TODO EXCEPTION
//                        System.out.println("Jetziger HistoryEintrag oder Vorgänger == null");
//                    }
//                }
                return historyEntries;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Suche lieferte kein Ergebnis zurück");
                }
                return null;
            }
        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Fehler beim suchen der Nachfolger eines Flurstücks", ex);
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key      DOCUMENT ME!
     * @param  useDate  DOCUMENT ME!
     */
    private void checkIfFlurstueckWasStaedtisch(final FlurstueckSchluesselCustomBean key, final Date useDate) {
        final FlurstueckArtCustomBean art = key.getFlurstueckArt();
        if (!key.getWarStaedtisch()) {
            // for(FlurstueckArtCustomBean current:getAllFlurstueckArten()){
            // TODO Checken ob korrekt mit Dirk absprechen
            if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(art.getBezeichnung())) {
                if (LOG.isDebugEnabled()) {
                    // if(FlurstueckArtCustomBean.FLURSTUECK_ART_EQUALATOR.pedanticEquals(art,current)){
                    LOG.debug("Flurstück ist Städtisch Datum letzter Stadtbesitz wird geupdated");
                }
                key.setWarStaedtisch(true);
                if (useDate != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Flurstück wurde neu angelegt und ist städtisch");
                    }
                    key.setDatumLetzterStadtbesitz(useDate);
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Flurstück war noch nie in Stadtbesitz und wird jetzt hinzugefügt");
                    }
                    final Date currentDate = new Date();
                    key.setDatumLetzterStadtbesitz(currentDate);
                    key.setEntstehungsDatum(currentDate);
                }
            }
            // }
        } else {
            if ((key.getFlurstueckArt() != null) && (key.getFlurstueckArt().getBezeichnung() != null)
                        && FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                            key.getFlurstueckArt().getBezeichnung())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Flurstück war und ist Städtisch --> Datum wird geupdated");
                }
                final Date currentDate = new Date();
                if (useDate != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Dieser Fall sollte nicht vorkommen");
                    }
                    key.setDatumLetzterStadtbesitz(useDate);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Datum letzter Stadt_besitz geupdated");
                    }
                } else {
                    key.setDatumLetzterStadtbesitz(currentDate);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Datum letzter Stadt_besitz geupdated");
                    }
                }
//TODO wird im Moment nur die Entstehung und gueltig_bis vom aktuellen Flurstück gespeichert

                final FlurstueckSchluesselCustomBean oldSchluessel = (FlurstueckSchluesselCustomBean)CidsBroker
                            .getInstance()
                            .getLagisMetaObject(key.getId(),
                                    CidsBroker.getInstance().getLagisMetaClass(CLASS__FLURSTUECK_SCHLUESSEL).getId())
                            .getBean();

                if ((oldSchluessel != null) && (oldSchluessel.getFlurstueckArt() != null)
                            && (oldSchluessel.getFlurstueckArt().getBezeichnung() != null)
                            && !FlurstueckArtCustomBean.FLURSTUECK_ART_EQUALATOR.pedanticEquals(
                                oldSchluessel.getFlurstueckArt(),
                                key.getFlurstueckArt())
                            && FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH.equals(
                                key.getFlurstueckArt().getBezeichnung())) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Flurstück kommt erneut in den Stadtbesitz --> entstehungsDatum wird geupdated");
                    }
                    if (useDate != null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("sollte nicht vorkkommen");
                        }
                        key.setEntstehungsDatum(useDate);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Datum Entstehung geupdated");
                        }
                    } else {
                        key.setEntstehungsDatum(currentDate);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Datum Entstehung geupdated");
                        }
                    }
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Kein wechsel von irgendeiner Flurstücksart nach städtisch --> kein Update");
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueckToCheck  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean existHistoryEntry(final FlurstueckCustomBean flurstueckToCheck) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Suche History Einträge");
        }
        final MetaClass metaclass = CidsBroker.getInstance().getLagisMetaClass("flurstueck_historie");
        if (metaclass == null) {
            return false;
        }
        final String query = "SELECT " + metaclass.getID() + ", " + metaclass.getTableName() + "."
                    + metaclass.getTableName() + "." + metaclass.getPrimaryKey() + " "
                    + "FROM " + metaclass.getTableName() + " "
                    + "WHERE " + metaclass.getTableName() + ".fk_vorgaenger = " + flurstueckToCheck.getId();

        final MetaObject[] mos = CidsBroker.getInstance().getLagisMetaObject(query);
        if ((mos != null) && (mos.length > 0)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Es existiert ein History Eintrag");
                LOG.debug("Es gibt schon einen Nachfolger");
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  newSchluessel  DOCUMENT ME!
     */
    private void createFlurstueckSchluessel(final FlurstueckSchluesselCustomBean newSchluessel) {
        try {
            newSchluessel.persist();
        } catch (Exception ex) {
            LOG.error("Fehler beim anlegen des Flurstückschlüssels: " + newSchluessel, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   nutzungen      DOCUMENT ME!
     * @param   flurstueckKey  DOCUMENT ME!
     *
     * @throws  ErrorInNutzungProcessingException  DOCUMENT ME!
     */
    private void processNutzungen(final Collection<NutzungCustomBean> nutzungen, final String flurstueckKey)
            throws ErrorInNutzungProcessingException {
        try {
            // ToDo NKF Neue Kette Angelegt Mail etc ?? Testen
            if ((nutzungen != null) && (nutzungen.size() > 0)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Anzahl Ketten in aktuellem Flurstück: " + nutzungen.size());
                }
                final Date bookingDate = new Date();
                for (final NutzungCustomBean curNutzung : nutzungen) {
                    StringBuffer emailMessage = null;
                    String emailSubject = null;
                    final Collection<NutzungCustomBean.NUTZUNG_STATES> nutzungsState = curNutzung.getNutzungsState();
                    if (nutzungsState.isEmpty()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Keine Änderung");
                        }
                    } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.NUTZUNG_CREATED)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Neue Nutzung angelegt. Nachricht an Zuständige");
                        }
                        curNutzung.getBuchwert().setGueltigvon(bookingDate);
                        final StringBuilder message = new StringBuilder();
                        message.append("Bei dem Flurstück: ");
                        message.append(flurstueckKey);
                        message.append(" wurde eine neue Nutzung angelegt: \n\n");
                        message.append(curNutzung.getOpenBuchung().getPrettyString());
                        sendEmail("Lagis - Neue Nutzung", message.toString(), nkfSessions);
                    } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.NUTZUNG_CHANGED)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Nutzungskette wurde modifiziert "
                                        + Arrays.deepToString(nutzungsState.toArray()));
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Setzte Datum für die letzten beiden Buchungen");
                        }
                        curNutzung.getOpenBuchung().setGueltigvon(bookingDate);
                        curNutzung.getPreviousBuchung().setGueltigbis(bookingDate);
                        if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.NUTZUNGSART_CHANGED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Nutzungsart wurde geändert. Nachricht an Zuständige");
                            }
                            final StringBuilder message = new StringBuilder();
                            message.append("Bei dem Flurstück ");
                            message.append(flurstueckKey);
                            message.append(" wurde die Nutzungsart der Nutzung: \n\n");
                            message.append(curNutzung.getPreviousBuchung().getPrettyString());
                            message.append("\nwie folgt geändert:\n\n");
                            message.append(curNutzung.getOpenBuchung().getNutzungsart().getPrettyString());
                            sendEmail("Lagis - Änderung einer Nutzungsart", message.toString(), nkfSessions);
                        }
                        if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.STILLE_RESERVE_CREATED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Stille Reserve wurde gebildet. Nachricht an Zuständige");
                            }
                            emailMessage = new StringBuffer();
                            emailMessage.append("Bei dem Flurstück ");
                            emailMessage.append(flurstueckKey);
                            emailMessage.append(" wurde die Nutzung: \n\n");
                            emailMessage.append(curNutzung.getPreviousBuchung().getPrettyString());
                            emailMessage.append("\nwie folgt abgeändert:\n\n");
                            emailMessage.append(curNutzung.getOpenBuchung().getPrettyString());
                            emailMessage.append("\nEs wurde eine Stille Reserve in Höhe von: ")
                                    .append(currencyFormatter.format(curNutzung.getStilleReserve()))
                                    .append(" gebildet.");
                            emailSubject = "Lagis - Stille Reserve wurde gebildet";
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.STILLE_RESERVE_INCREASED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Vorhandene Stille Reserve wurde erhöht. Nachricht an Zuständige");
                            }
                            emailMessage = new StringBuffer();
                            emailMessage.append("Bei dem Flurstück ");
                            emailMessage.append(flurstueckKey);
                            emailMessage.append(" wurde die Nutzung: \n\n");
                            emailMessage.append(curNutzung.getPreviousBuchung().getPrettyString());
                            emailMessage.append("\nwie folgt abgeändert:\n\n");
                            emailMessage.append(curNutzung.getOpenBuchung().getPrettyString());
                            emailMessage.append("\nDie vorhandene Stille Reserve wurde um ")
                                    .append(currencyFormatter.format(curNutzung.getDifferenceToPreviousBuchung()))
                                    .append(" erhöht.");
                            emailSubject = "Lagis - Stille Reserve wurde erhöht";
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.STILLE_RESERVE_DECREASED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Vorhandene Stille Reserve wurde vermindert. Nachricht an Zuständige");
                            }
                            emailMessage = new StringBuffer();
                            emailMessage.append("Bei dem Flurstück ");
                            emailMessage.append(flurstueckKey);
                            emailMessage.append(" wurde die Nutzung: \n\n");
                            emailMessage.append(curNutzung.getPreviousBuchung().getPrettyString());
                            emailMessage.append("\nwie folgt abgeändert:\n\n");
                            emailMessage.append(curNutzung.getOpenBuchung().getPrettyString());
                            emailMessage.append("\nDie vorhandene Stille Reserve wurde um ")
                                    .append(currencyFormatter.format(
                                                Math.abs(curNutzung.getDifferenceToPreviousBuchung())))
                                    .append(" reduziert.");
                            emailSubject = "Lagis - Stille Reserve wurde reduziert";
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.STILLE_RESERVE_DISOLVED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                    "Vorhandene Stille Reserve wurde vollständig aufgebraucht. Nachricht an Zuständige");
                            }
                            emailMessage = new StringBuffer();
                            emailMessage.append("Bei dem Flurstück ");
                            emailMessage.append(flurstueckKey);
                            emailMessage.append(" wurde die Nutzung: \n\n");
                            emailMessage.append(curNutzung.getPreviousBuchung().getPrettyString());
                            emailMessage.append("\nwie folgt abgeändert:\n\n");
                            emailMessage.append(curNutzung.getOpenBuchung().getPrettyString());
                            emailMessage.append(
                                "\nEine Nutzung des Flurstücks wurde reduziert, die vorhandene Stille Reserve reicht nicht aus um die Differenz auszugleichen. ");
                            emailMessage.append("Die Stille Reserve wird aufgelöst.");
                            emailSubject = "Lagis - Stille Reserve wurde aufgelöst";
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.POSITIVE_BUCHUNG)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Positive Buchung ohne Stille Reserve. Nachricht an Zuständige");
                            }
                            emailMessage = new StringBuffer();
                            emailMessage.append("Bei dem Flurstück ");
                            emailMessage.append(flurstueckKey);
                            emailMessage.append(" wurde die Nutzung: \n\n");
                            emailMessage.append(curNutzung.getPreviousBuchung().getPrettyString());
                            emailMessage.append("\nwie folgt abgeändert:\n\n");
                            emailMessage.append(curNutzung.getOpenBuchung().getPrettyString());
                            emailMessage.append(
                                    "\nEs ist keine Stille Reserve vorhanden und es wurde keine gebildet.\n Der Gesamtpreis wurde um ")
                                    .append(currencyFormatter.format(curNutzung.getDifferenceToPreviousBuchung()))
                                    .append(" erhöht.");
                            emailSubject = "Lagis - Gesamtpreis einer Nutzung hat sich erhöht";
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.NEGATIVE_BUCHUNG)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Negative Buchung ohne Stille Reserve. Nachricht an Zuständige");
                            }
                            emailMessage = new StringBuffer();
                            emailMessage.append("Bei dem Flurstück ");
                            emailMessage.append(flurstueckKey);
                            emailMessage.append(" wurde die Nutzung: \n\n");
                            emailMessage.append(curNutzung.getPreviousBuchung().getPrettyString());
                            emailMessage.append("\nwie folgt abgeändert:\n\n");
                            emailMessage.append(curNutzung.getOpenBuchung().getPrettyString());
                            final StringBuffer append = emailMessage.append(
                                        "\nEs ist keine Stille Reserve vorhanden. Der Gesamtpreis wurde um ")
                                        .append(currencyFormatter.format(curNutzung.getDifferenceToPreviousBuchung()))
                                        .append(" reduziert.");
                            emailSubject = "Lagis - Gesamtpreis einer Nutzung hat sich vermindert";
                        }
                    } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.NUTZUNG_TERMINATED)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Nutzungskette wurde terminiert, setze Buchungsdatum");
                            LOG.debug("Benachrichtigung der Verantwortlichen Stelle über die Veränderung...");
                        }
                        emailMessage = new StringBuffer();
                        emailMessage.append("Bei dem Flurstück ");
                        emailMessage.append(flurstueckKey);
                        emailMessage.append(" wurde die Nutzung: \n\n");
                        emailMessage.append(curNutzung.getTerminalBuchung().getPrettyString());
                        emailMessage.append("\n\n");
                        emailMessage.append(
                            "abgeschlossen. Es sind keine weiteren Buchungen mehr möglich auf dieser Nutzung.");
                        emailSubject = "Lagis - Nutzung wurde gelöscht";
                        // ToDo Nachricht an Zuständige ?? gab es bisher
                        // curNutzung.terminateNutzung(bookingDate);
                        curNutzung.getTerminalBuchung().setGueltigbis(bookingDate);
                        // ToDo letzter Wert zum Buchwert setzen ?
                    } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.BUCHUNG_CREATED)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("neue Buchung . Nachricht an Zuständige");
                        }
                        emailMessage = new StringBuffer();
                        emailMessage.append("Bei dem Flurstück ");
                        emailMessage.append(flurstueckKey);
                        emailMessage.append(" wurde die Nutzung: \n\n");
                        emailMessage.append(curNutzung.getPreviousBuchung().getPrettyString());
                        emailMessage.append("\nwie folgt abgeändert:\n\n");
                        emailMessage.append(curNutzung.getOpenBuchung().getPrettyString());
                        emailSubject = "Lagis - Buchung wurde erzeugt";
                    } else {
                        throw new Exception("Kein Fall trifft auf Stati zu: "
                                    + Arrays.toString(nutzungsState.toArray()));
                    }
                    // Nur Stille Reserve --> NutzungsartCustomBean Email weiter oben
                    if (emailMessage != null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Es wird eine Nachricht an die zuständige Behörde geschickt");
                        }
                        sendEmail(emailSubject, emailMessage.toString(), nkfSessions);
                    }
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Flurstück besitzt keine Nutzungen.");
                }
            }
        } catch (Exception ex) {
            throw new ErrorInNutzungProcessingException("Nutzungen konnten nicht verarbeitet werden", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  subject   DOCUMENT ME!
     * @param  message   DOCUMENT ME!
     * @param  sessions  DOCUMENT ME!
     */
    private void sendEmail(final String subject,
            final String message, final Vector<Session> sessions) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Sende Email benachrichtigung");
        }
        try {
            final Properties lagisEEproperties = new Properties();
            // lagisEEproperties.load(getClass().getResourceAsStream("/de/cismet/lagis/eeServer/defaultLagisEEServer.properties"));

            // System.out.println("NKF EMAIL Address: "+lagisEEproperties.getProperty("nkf_Mailaddress"));
            // System.out.println("System Property test: "+System.getProperty("lagis.test"));
            // System.out.println("Session properties: "+nkfMailer.getProperties()); System.out.println("Session
            // Property: "+nkfMailer.getProperty("mail.lagis.test")); if
            // (lagisEEproperties.getProperty("nkf_Mailaddress") != null) {
            // System.out.println("Benachrichtigungsmailadresse gesetzt: " +
            // lagisEEproperties.getProperty("nkf_Mailaddress")); } else {
            // System.out.println("Benachrichtigungsmailadresse nicht gesetzt --> keine Mail"); return; }
            if (System.getProperty("lagis.nkfmail") != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Benachrichtigungsmailadresse gesetzt: " + System.getProperty("lagis.nkfmail"));
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Benachrichtigungsmailadresse nicht gesetzt --> keine Mail");
                }
                return;
            }
            // Problem with different auth --> see if multiple email addresses are needed
// MailAuthenticator auth = new MailAuthenticator("", "");

            final Iterator<Session> sessionItr = sessions.iterator();
            // if (sessions != null && sessions.size() > 0) {
            // while (sessionItr.hasNext()) {
            // sessionItr.next();
            // Properties properties = new Properties();
            // properties.put("mail.smtp.host", "smtp.uni-saarland.de");
            // Session mailer = Session.getDefaultInstance(properties, null);
            final javax.mail.Message msg = new MimeMessage(nkfMailer);
            msg.setFrom(new InternetAddress("sebastian.puhl@cismet.de"));
            // TODO Surround with try catch
            msg.setRecipients(
                javax.mail.Message.RecipientType.TO,
                InternetAddress.parse(System.getProperty("lagis.nkfmail"), false));
            msg.setSubject(subject);
            msg.setText(message);
            msg.setSentDate(new Date());
            Transport.send(msg);
            // }
            // } else {
            // System.out.println("Keine Session vorhanden");
            // }
        } catch (Exception ex) {
            LOG.error("Fehler beim senden einer email", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  locks  DOCUMENT ME!
     */
    private void releaseLocks(final ArrayList<SperreCustomBean> locks) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("release Locks " + locks);
        }
        if (locks != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Liste ist vorhanden anzahl: " + locks.size());
            }
            final Iterator<SperreCustomBean> it = locks.iterator();
            while (it.hasNext()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Entferene Sperre...");
                }
                releaseLock(it.next());
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("locks == null --> keine Aktion");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  oldFlurstueck  DOCUMENT ME!
     * @param  newFlurstueck  DOCUMENT ME!
     */
    private void createHistoryEdge(final FlurstueckCustomBean oldFlurstueck, final FlurstueckCustomBean newFlurstueck) {
        final FlurstueckHistorieCustomBean historyEntry = FlurstueckHistorieCustomBean.createNew();
        historyEntry.setVorgaenger(oldFlurstueck);
        historyEntry.setNachfolger(newFlurstueck);
        createFlurstueckHistoryEntry(historyEntry);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key         DOCUMENT ME!
     * @param   level       DOCUMENT ME!
     * @param   type        DOCUMENT ME!
     * @param   levelCount  DOCUMENT ME!
     * @param   allEdges    DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    private void addHistoryEntriesForNeighbours(final FlurstueckSchluesselCustomBean key,
            final CidsBroker.HistoryLevel level,
            final CidsBroker.HistoryType type,
            int levelCount,
            final Collection<FlurstueckHistorieCustomBean> allEdges) throws ActionNotSuccessfulException {
        Collection<FlurstueckHistorieCustomBean> foundEdges;
        Collection<FlurstueckHistorieCustomBean> neighbours;

        if (type == CidsBroker.HistoryType.PREDECESSOR) {
            neighbours = getHistoryAccessors(key);
        } else {
            neighbours = getHistorySuccessor(key);
        }

        if ((neighbours != null) && (type == CidsBroker.HistoryType.PREDECESSOR)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Es gibt Vorgängerkanten zu diesem Knoten");
            }
            replacePseudoFlurstuecke(neighbours, allEdges, type);
            allEdges.addAll(neighbours);
        } else if ((neighbours != null) && (type == CidsBroker.HistoryType.SUCCESSOR)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Es gibt Nachfoglerkanten zu diesem Knoten");
            }
            replacePseudoFlurstuecke(neighbours, allEdges, type);
            allEdges.addAll(neighbours);
        } else if (type == CidsBroker.HistoryType.SUCCESSOR) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Es gibt keine Nachfolgerkanten zu diesem Knoten");
            }
            return;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Es gibt keine Vorgängerkanten zu diesem Knoten");
            }
            return;
        }

        if (level != CidsBroker.HistoryLevel.All) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("HistoryLevel = " + level);
            }
            if (levelCount > 0) {
                levelCount = --levelCount;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("LevelCount " + (levelCount + 1) + " wurde um eins reduziert auf " + levelCount);
                }
                if (levelCount == 0) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Anzahl zu suchender Levels erschöpft kehre zurück");
                    }
                    return;
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Anzahl zu suchender Levels erschöpft kehre zurück");
                }
                return;
            }
        }

        final Iterator<FlurstueckHistorieCustomBean> itr = neighbours.iterator();

        while (itr.hasNext()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Suche rekursiv nach weiteren Vorgängern");
            }
            if (type == CidsBroker.HistoryType.PREDECESSOR) {
                foundEdges = getHistoryEntries(itr.next().getVorgaenger().getFlurstueckSchluessel(),
                        level,
                        type,
                        levelCount);
            } else {
                foundEdges = getHistoryEntries(itr.next().getNachfolger().getFlurstueckSchluessel(),
                        level,
                        type,
                        levelCount);
            }
            if (foundEdges != null) {
                allEdges.addAll(foundEdges);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Rekursive suche brachte keine Ergebnisse");
                }
            }
        }

        if (level != CidsBroker.HistoryLevel.All) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("HistoryLevel = " + level);
            }
            if (levelCount > 0) {
                levelCount = --levelCount;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("LevelCount " + (levelCount + 1) + " wurde um eins reduziert auf " + levelCount);
                }
                if (levelCount == 0) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Anzahl zu suchender Levels erschöpft kehre zurück");
                    }
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Anzahl zu suchender Levels erschöpft kehre zurück");
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   tablename  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaClass getLagisMetaClass(final String tablename) {
        return getMetaClass(tablename, LagisBroker.getInstance().getDomain());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   tablename  DOCUMENT ME!
     * @param   domain     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaClass getMetaClass(final String tablename, final String domain) {
        try {
            return CidsBean.getMetaClassFromTableName(domain, tablename);
        } catch (Exception exception) {
            LOG.error("couldn't load metaclass for " + tablename, exception);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   objectId  DOCUMENT ME!
     * @param   classtId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaObject getLagisMetaObject(final int objectId, final int classtId) {
        return getMetaObject(objectId, classtId, LagisBroker.getInstance().getDomain());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   objectId  DOCUMENT ME!
     * @param   classtId  DOCUMENT ME!
     * @param   domain    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaObject getMetaObject(final int objectId, final int classtId, final String domain) {
        try {
            final ConnectionProxy proxy = getProxy();
            return proxy.getMetaObject(objectId, classtId, domain);
        } catch (ConnectionException ex) {
            LOG.error("error in retrieving the metaobject " + objectId + " of classid " + classtId, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   query  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaObject[] getLagisMetaObject(final String query) {
        return getMetaObject(query, LagisBroker.getInstance().getDomain());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   query   DOCUMENT ME!
     * @param   domain  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaObject[] getMetaObject(final String query, final String domain) {
        MetaObject[] mos = null;
        try {
            final User user = SessionManager.getSession().getUser();
            final ConnectionProxy proxy = getProxy();
            mos = proxy.getMetaObjectByQuery(user, query, domain);
        } catch (ConnectionException ex) {
            LOG.error("error retrieving metaobject by query", ex);
        }
        return mos;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class MailAuthenticator extends Authenticator {

        //~ Instance fields ----------------------------------------------------

        /**
         * Ein String, der den Usernamen nach der Erzeugung eines Objektes<br>
         * dieser Klasse enthalten wird.
         */
        private final String user;
        /**
         * Ein String, der das Passwort nach der Erzeugung eines Objektes<br>
         * dieser Klasse enthalten wird.
         */
        private final String password;

        //~ Constructors -------------------------------------------------------

        /**
         * Der Konstruktor erzeugt ein MailAuthenticator Objekt<br>
         * aus den beiden Parametern user und passwort.
         *
         * @param  user      String, der Username fuer den Mailaccount.
         * @param  password  String, das Passwort fuer den Mailaccount.
         */
        public MailAuthenticator(final String user, final String password) {
            this.user = user;
            this.password = password;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * Diese Methode gibt ein neues PasswortAuthentication Objekt zurueck.
         *
         * @return  DOCUMENT ME!
         *
         * @see     javax.mail.Authenticator#getPasswordAuthentication()
         */
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(this.user, this.password);
        }
    }
}
