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

import java.util.*;

import de.cismet.cids.custom.beans.lagis.*;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.lagis.Exception.ActionNotSuccessfulException;
import de.cismet.lagis.Exception.ErrorInNutzungProcessingException;

import de.cismet.lagis.commons.LagisConstants;
import de.cismet.lagis.commons.LagisMetaclassConstants;

import de.cismet.lagis.server.search.FlurstueckHistorieGraphSearch;
import de.cismet.lagis.server.search.FlurstueckHistorieGraphSearchResultItem;

import de.cismet.lagisEE.interfaces.Key;

import de.cismet.lagisEE.util.FlurKey;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public final class CidsBroker {

    //~ Static fields/initializers ---------------------------------------------

    private static CidsBroker instance = null;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CidsBroker.class);
    private static final String DEFAULT_DOT_HEADER = "digraph G{\n";

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
    public enum HistorySibblingLevel {

        //~ Enum constants -----------------------------------------------------

        NONE, SIBBLING_ONLY, FULL, CUSTOM
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

    //~ Constructors -----------------------------------------------------------

// @Resource(name = "mail/nkf_mailaddress")
// private Session nkfMailer;
    /**
     * Creates a new instance of CidsBroker.
     */
    private CidsBroker() {
        try {
            setProxy(SessionManager.getProxy());
            if (!SessionManager.isInitialized()) {
                SessionManager.init(getProxy());
                ClassCacheMultiple.setInstance(LagisConstants.DOMAIN_LAGIS);
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
            flurstueck.persist();
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
            key.persist();
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
                    if ((currentGemarkung.getSchluessel() != null)) {
                        // TODO Duplicated code --> extract

                        final MetaClass metaclass = CidsBroker.getInstance()
                                    .getLagisMetaClass(LagisMetaclassConstants.FLURSTUECK_SCHLUESSEL);
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
                    } else if ((currentGemarkung.getBezeichnung() != null)) {
                        final GemarkungCustomBean completed = completeGemarkung(currentGemarkung);
                        if (completed != null) {
                            final MetaClass metaclass = CidsBroker.getInstance()
                                        .getLagisMetaClass(LagisMetaclassConstants.FLURSTUECK_SCHLUESSEL);
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
                                return flurKeys;
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
                                .getLagisMetaClass(LagisMetaclassConstants.FLURSTUECK_SCHLUESSEL);
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

            final Integer flur = key.getFlur();
            final Integer fsZaehler = key.getFlurstueckZaehler();
            final Integer fsNenner = key.getFlurstueckNenner();
            final Integer gemarkung = (key.getGemarkung() == null) ? null : key.getGemarkung().getId();

            final MetaObject[] mos;
            if ((flur != null) && (fsZaehler != null) && (fsNenner != null) && (gemarkung != null)) {
                mos = CidsBroker.getInstance()
                            .getLagisMetaObject(
                                    "SELECT "
                                    + metaclass.getID()
                                    + ", "
                                    + metaclass.getTableName()
                                    + "."
                                    + metaclass.getPrimaryKey()
                                    + " "
                                    + " FROM "
                                    + metaclass.getTableName()
                                    + ", flurstueck_schluessel fk"
                                    + " WHERE "
                                    + metaclass.getTableName()
                                    + ".fk_flurstueck_schluessel = fk.id "
                                    + " AND fk.flur = "
                                    + key.getFlur()
                                    + " AND fk.fk_gemarkung = "
                                    + key.getGemarkung().getId()
                                    + " AND fk.flurstueck_zaehler = "
                                    + key.getFlurstueckZaehler()
                                    + " AND fk.flurstueck_nenner  = "
                                    + key.getFlurstueckNenner());
            } else {
                mos = CidsBroker.getInstance()
                            .getLagisMetaObject(
                                    "SELECT "
                                    + metaclass.getID()
                                    + ", "
                                    + metaclass.getTableName()
                                    + "."
                                    + metaclass.getPrimaryKey()
                                    + " "
                                    + " FROM "
                                    + metaclass.getTableName()
                                    + ", flurstueck_schluessel fk"
                                    + " WHERE "
                                    + metaclass.getTableName()
                                    + ".fk_flurstueck_schluessel = fk.id "
                                    + " AND fk.id = "
                                    + key.getId()
                                    + " AND fk.flur is NULL "
                                    + " AND fk.fk_gemarkung is NULL "
                                    + " AND fk.flurstueck_zaehler is NULL "
                                    + " AND fk.flurstueck_nenner  is NULL ");
            }

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
     * DOCUMENT ME!
     *
     * @param   flurstueckSchluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckSchluesselCustomBean completeFlurstueckSchluessel(
            final FlurstueckSchluesselCustomBean flurstueckSchluessel) {
        return FlurstueckSchluesselCustomBean.createNewByFsKey(flurstueckSchluessel);
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
            if (LOG.isDebugEnabled()) {
                LOG.debug("createFlurstueck: key ist != null");
            }

            final FlurstueckSchluesselCustomBean checkedKey = this.completeFlurstueckSchluessel(key);
            if (checkedKey != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("createFlurstueck: Vervollständigter key ist == null");
                }
                return null;
            }

//                final Integer keyId = key.getId();
//
//                if ((keyId == null) || (keyId == -1)) {
//                    if (LOG.isDebugEnabled()) {
//                        LOG.debug("createFlurstueck: Vervollständigter key ist != null");
//                    }
//                    return null;
//                }
//                }
//                else {
//                    checkedKey = FlurstueckSchluesselCustomBean.createNewById(key.getId());
//                }
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
            FlurstueckCustomBean newFlurstueck;

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

                        final User user = SessionManager.getSession().getUser();
                        final MetaClass mcDmsUrl = ClassCacheMultiple.getMetaClass(
                                LagisConstants.DOMAIN_LAGIS,
                                LagisMetaclassConstants.DMS_URL);
                        final MetaClass mcNutzung = ClassCacheMultiple.getMetaClass(
                                LagisConstants.DOMAIN_LAGIS,
                                LagisMetaclassConstants.NUTZUNG);
                        final MetaClass mcRebe = ClassCacheMultiple.getMetaClass(
                                LagisConstants.DOMAIN_LAGIS,
                                LagisMetaclassConstants.REBE);
                        final MetaClass mcVerwaltungsbereichEintrag = ClassCacheMultiple.getMetaClass(
                                LagisConstants.DOMAIN_LAGIS,
                                LagisMetaclassConstants.VERWALTUNGSBEREICHE_EINTRAG);

                        final String queryDmsUrl = "SELECT " + mcDmsUrl.getID() + ", " + mcDmsUrl.getPrimaryKey()
                                    + " FROM " + mcDmsUrl.getTableName() + " WHERE " + " fk_flurstueck = "
                                    + oldFlurstueck.getId().toString();
                        final String queryNutzung = "SELECT " + mcNutzung.getID() + ", " + mcNutzung.getPrimaryKey()
                                    + " FROM " + mcNutzung.getTableName() + " WHERE " + " fk_flurstueck = "
                                    + oldFlurstueck.getId().toString();
                        final String queryRebe = "SELECT " + mcRebe.getID() + ", " + mcRebe.getPrimaryKey() + " FROM "
                                    + mcRebe.getTableName() + " WHERE " + " fk_flurstueck = "
                                    + oldFlurstueck.getId().toString();
                        final String queryVerwaltungsbereichEintrag = "SELECT " + mcVerwaltungsbereichEintrag.getID()
                                    + ", "
                                    + mcVerwaltungsbereichEintrag.getPrimaryKey() + " FROM "
                                    + mcVerwaltungsbereichEintrag.getTableName() + " WHERE " + " fk_flurstueck = "
                                    + oldFlurstueck.getId().toString();

                        newFlurstueck.getAr_baeume().addAll(oldFlurstueck.getAr_baeume());
                        oldFlurstueck.getAr_baeume().clear();

                        newFlurstueck.getAr_mipas().addAll(oldFlurstueck.getAr_mipas());
                        oldFlurstueck.getAr_mipas().clear();

                        newFlurstueck.getAr_vertraege().addAll(oldFlurstueck.getAr_vertraege());
                        oldFlurstueck.getAr_vertraege().clear();

                        for (final MetaObject moDmsUrl
                                    : SessionManager.getProxy().getMetaObjectByQuery(user, queryDmsUrl)) {
                            moDmsUrl.getBean().setProperty("fk_flurstueck", newFlurstueck);
                            moDmsUrl.getBean().persist();
                        }

                        for (final MetaObject moNutzung
                                    : SessionManager.getProxy().getMetaObjectByQuery(user, queryNutzung)) {
                            moNutzung.getBean().setProperty("fk_flurstueck", newFlurstueck);
                            moNutzung.getBean().persist();
                        }

                        for (final MetaObject moRebe : SessionManager.getProxy().getMetaObjectByQuery(user, queryRebe)) {
                            moRebe.getBean().setProperty("fk_flurstueck", newFlurstueck);
                            moRebe.getBean().persist();
                        }

                        for (final MetaObject moVerwaltungsbereichEintrag
                                    : SessionManager.getProxy().getMetaObjectByQuery(
                                        user,
                                        queryVerwaltungsbereichEintrag)) {
                            moVerwaltungsbereichEintrag.getBean().setProperty("fk_flurstueck", newFlurstueck);
                            moVerwaltungsbereichEintrag.getBean().persist();
                        }

                        newFlurstueck.setFk_spielplatz(oldFlurstueck.getFk_spielplatz());
                        newFlurstueck.setBemerkung(oldFlurstueck.getBemerkung());
                        newFlurstueck.setIn_stadtbesitz(oldFlurstueck.getIn_stadtbesitz());
                        newFlurstueck = (FlurstueckCustomBean)newFlurstueck.persist();
                        oldFlurstueck.persist();
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
                        releaseLock(lock);
                        throw new ActionNotSuccessfulException(
                            "Spliten des Flurstücks nicht möglich, es gibt schon einen Nachfolger");
                    }
                } else {
                    releaseLock(lock);
                    throw new ActionNotSuccessfulException("Flurstück konnte nicht historisch gesetzt werden");
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
            FlurstueckSchluesselCustomBean dummySchluessel = FlurstueckSchluesselCustomBean.createNew();
            // dummySchluessel.setWarStaedtisch(true);
            // UGLY minimum Konstante aus der jeweiligen Klasse benutzen
            for (final FlurstueckArtCustomBean current : getAllFlurstueckArten()) {
                if (FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_PSEUDO.equals(current.getBezeichnung())) {
                    dummySchluessel.setFlurstueckArt(current);
                    break;
                }
            }

            dummySchluessel = (FlurstueckSchluesselCustomBean)dummySchluessel.persist();
//            createFlurstueckSchluessel(dummySchluessel);

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
            Collection<FlurstueckHistorieCustomBean> childEdges = getHistoryPredecessors(schluessel);
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
     * @param   currentFlurstueck   DOCUMENT ME!
     * @param   level               allEdges level DOCUMENT ME!
     * @param   levelLimit          DOCUMENT ME!
     * @param   sibblingLevel       DOCUMENT ME!
     * @param   sibblingLevelLimit  DOCUMENT ME!
     * @param   type                DOCUMENT ME!
     * @param   nodeToKeyMapIn      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public String getHistoryGraph(final FlurstueckCustomBean currentFlurstueck,
            final HistoryLevel level,
            final int levelLimit,
            final HistorySibblingLevel sibblingLevel,
            final int sibblingLevelLimit,
            final HistoryType type,
            final HashMap<String, Integer> nodeToKeyMapIn) throws ActionNotSuccessfulException {
        final StringBuilder dotGraphRepresentation = new StringBuilder(DEFAULT_DOT_HEADER);

        try {
            final boolean followPredecessors = HistoryType.BOTH.equals(type) || HistoryType.PREDECESSOR.equals(type);
            final boolean followSuccessors = HistoryType.BOTH.equals(type) || HistoryType.SUCCESSOR.equals(type);

            final int predecessorLevelCount;
            final int successorLevelCount;
            switch (level) {
                case All: {
                    predecessorLevelCount = followPredecessors ? Integer.MIN_VALUE : 1;
                    successorLevelCount = followSuccessors ? Integer.MAX_VALUE : 0;
                }
                break;
                case DIRECT_RELATIONS: {
                    predecessorLevelCount = followPredecessors ? -1 : Integer.MAX_VALUE;
                    successorLevelCount = followSuccessors ? 1 : Integer.MIN_VALUE;
                }
                break;
                case CUSTOM: {
                    predecessorLevelCount = followPredecessors ? -levelLimit : Integer.MAX_VALUE;
                    successorLevelCount = followSuccessors ? levelLimit : Integer.MIN_VALUE;
                }
                break;
                default: {
                    predecessorLevelCount = Integer.MAX_VALUE; // disabled
                    successorLevelCount = Integer.MIN_VALUE;   // disabled
                }
            }

            final int sibblingLevelCount;
            switch (sibblingLevel) {
                case FULL: {
                    sibblingLevelCount = Integer.MAX_VALUE;
                }
                break;
                case SIBBLING_ONLY: {
                    sibblingLevelCount = 0;
                }
                break;
                case CUSTOM: {
                    sibblingLevelCount = sibblingLevelLimit;
                }
                break;
                case NONE:
                default: {
                    sibblingLevelCount = Integer.MIN_VALUE;
                }
            }

            final FlurstueckHistorieGraphSearch search = new FlurstueckHistorieGraphSearch(
                    currentFlurstueck.getId(),
                    predecessorLevelCount,
                    successorLevelCount,
                    sibblingLevelCount);
            final Collection<FlurstueckHistorieGraphSearchResultItem> allEdges = proxy.customServerSearch(search);
            final HashMap<String, String> pseudoKeys = new HashMap<String, String>();

            final HashMap<String, Integer> nodeToKeyMap = (nodeToKeyMapIn == null) ? new HashMap<String, Integer>()
                                                                                   : nodeToKeyMapIn;

            if ((allEdges != null) && (allEdges.size() > 0)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Historie Graph hat: " + allEdges.size() + " Kanten");
                }
                for (final FlurstueckHistorieGraphSearchResultItem currentEdge : allEdges) {
                    final String currentVorgaenger = currentEdge.getVorgaengerName();
                    final String currentNachfolger = currentEdge.getNachfolgerName();

                    if (currentVorgaenger.startsWith("pseudo")) {
                        pseudoKeys.put(currentVorgaenger, "    ");
                    }
                    if (currentNachfolger.startsWith("pseudo")) {
                        pseudoKeys.put(currentNachfolger, "    ");
                    }
                    dotGraphRepresentation.append("\"")
                            .append(currentVorgaenger)
                            .append("\"->\"")
                            .append(currentNachfolger)
                            .append("\" [lineInterpolate=\"linear\"];\n"); // additional options:
                    // e.g.: basis, linear – Normal line (jagged). step-before – a stepping graph alternating between
                    // vertical and horizontal segments. step-after - a stepping graph alternating between horizontal
                    // and vertical segments. basis - a B-spline, with control point duplication on the ends (that's the
                    // one above). basis-open - an open B-spline; may not intersect the start or end. basis-closed - a
                    // closed B-spline, with the start and the end closed in a loop. bundle - equivalent to basis,
                    // except a separate tension parameter is used to straighten the spline. This could be really cool
                    // with varying tension. cardinal - a Cardinal spline, with control point duplication on the ends.
                    // It looks slightly more 'jagged' than basis. cardinal-open - an open Cardinal spline; may not
                    // intersect the start or end, but will intersect other control points. So kind of shorter than
                    // 'cardinal'. cardinal-closed - a closed Cardinal spline, looped back on itself. monotone - cubic
                    // interpolation that makes the graph only slightly smoother.
                    nodeToKeyMap.put(currentEdge.getVorgaengerName(), currentEdge.getVorgaengerSchluesselId());
                    nodeToKeyMap.put(currentEdge.getNachfolgerName(), currentEdge.getNachfolgerSchluesselId());
                }
                dotGraphRepresentation.append("\"")
                        .append(currentFlurstueck)
                        .append("\"  [style=\"fill: #eee; font-weight: bold\"];\n");
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Historie Graph ist < 1 --> keine Historie");
                }
                dotGraphRepresentation.append("\"")
                        .append(currentFlurstueck)
                        .append("\"  [style=\"fill: #eee; font-weight: bold\"]" + ";\n");
                nodeToKeyMap.put(currentFlurstueck.toString(), currentFlurstueck.getId());
            }

            if (pseudoKeys.size() > 0) {
                for (final String key : pseudoKeys.keySet()) {
                    dotGraphRepresentation.append("\"").append(key).append("\" [label=\"    \"]");
                }
            }
            dotGraphRepresentation.append("}");
        } catch (final Exception ex) {
            throw new ActionNotSuccessfulException("error while searching historie for " + currentFlurstueck, ex);
        }
        return dotGraphRepresentation.toString();
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

        if (LOG.isDebugEnabled()) {
            LOG.debug("Anzahl Vertraege ist: " + beans.size());
        }
        return beans;
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("Anzahl FlurstueckSchluessel ist: " + keys.size());
        }
        return keys;
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

        if (LOG.isDebugEnabled()) {
            LOG.debug("Anzahl Flurststückschlüssel für das Aktenzeichen ist: " + flurstueckSchluessel.size());
        }
        return flurstueckSchluessel;
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
                    .getLagisMetaClass(LagisMetaclassConstants.FLURSTUECK_SCHLUESSEL);
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

        if (LOG.isDebugEnabled()) {
            LOG.debug("Anzahl MiPas ist: " + mipas.size());
        }
        return mipas;
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

        if (LOG.isDebugEnabled()) {
            LOG.debug("Anzahl Baueme ist: " + baeume.size());
        }
        return baeume;
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
                    .getLagisMetaClass(LagisMetaclassConstants.FLURSTUECK_SCHLUESSEL);
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

        if (LOG.isDebugEnabled()) {
            LOG.debug("Anzahl FlurstueckSchluessel ist: " + keys.size());
        }
        return keys;
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
                            flurstueck.persist();
                            return true;
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
                            flurstueck.setFlurstueckSchluessel(key);
                            flurstueck.persist();
                        }

                        return true;
                    }
                } else {
                    return true;
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Flurstueck war noch nie staedtisch wird historisch gesetzt");
                }
                key.setGueltigBis(date);
                key.persist();

                return true;
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim historisch setzen eines Flurstücks", ex);
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
            if (mos.length == 0) {
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
                    key.persist();
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
                    final Collection<FlurstueckHistorieCustomBean> result = getHistoryPredecessors(
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
    private Collection<FlurstueckHistorieCustomBean> getHistoryPredecessors(
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
                                    CidsBroker.getInstance().getLagisMetaClass(
                                        LagisMetaclassConstants.FLURSTUECK_SCHLUESSEL).getId())
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
                    + metaclass.getPrimaryKey() + " "
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
     * Checks in which way the Nutzungen have changed and reacts according to that. At the moment only the states
     * NUTZUNG_CHANGED and NUTZUNG_TERMINATED require further treatment.
     *
     * @param   nutzungen      DOCUMENT ME!
     * @param   flurstueckKey  not used at them moment
     *
     * @throws  ErrorInNutzungProcessingException  DOCUMENT ME!
     *
     * @see     processNutzungen_old()
     */
    private void processNutzungen(final Collection<NutzungCustomBean> nutzungen, final String flurstueckKey)
            throws ErrorInNutzungProcessingException {
        try {
            if ((nutzungen != null) && (nutzungen.size() > 0)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Anzahl Ketten in aktuellem Flurstück: " + nutzungen.size());
                }
                final Date bookingDate = new Date();
                for (final NutzungCustomBean curNutzung : nutzungen) {
                    final Collection<NutzungCustomBean.NUTZUNG_STATES> nutzungsState = curNutzung.getNutzungsState();
                    if (nutzungsState.isEmpty()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Keine Änderung");
                        }
                    } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.NUTZUNG_CREATED)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Neue Nutzung angelegt.");
                        }
                        curNutzung.getBuchwert().setGueltigvon(bookingDate);
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
                                LOG.debug("Nutzungsart wurde geändert.");
                            }
                        }
                        if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.STILLE_RESERVE_CREATED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Stille Reserve wurde gebildet.");
                            }
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.STILLE_RESERVE_INCREASED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Vorhandene Stille Reserve wurde erhöht.");
                            }
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.STILLE_RESERVE_DECREASED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Vorhandene Stille Reserve wurde vermindert.");
                            }
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.STILLE_RESERVE_DISOLVED)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                    "Vorhandene Stille Reserve wurde vollständig aufgebraucht.");
                            }
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.POSITIVE_BUCHUNG)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Positive Buchung ohne Stille Reserve.");
                            }
                        } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.NEGATIVE_BUCHUNG)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Negative Buchung ohne Stille Reserve.");
                            }
                        }
                    } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.NUTZUNG_TERMINATED)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Nutzungskette wurde terminiert, setze Buchungsdatum");
                        }
                        // ToDo Nachricht an Zuständige ?? gab es bisher
                        // curNutzung.terminateNutzung(bookingDate);
                        curNutzung.getTerminalBuchung().setGueltigbis(bookingDate);
                        // ToDo letzter Wert zum Buchwert setzen ?
                    } else if (nutzungsState.contains(NutzungCustomBean.NUTZUNG_STATES.BUCHUNG_CREATED)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("neue Buchung . Nachricht an Zuständige");
                        }
                    } else {
                        throw new Exception("Kein Fall trifft auf Stati zu: "
                                    + Arrays.toString(nutzungsState.toArray()));
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
}
