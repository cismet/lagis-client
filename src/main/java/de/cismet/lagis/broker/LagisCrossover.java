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
package de.cismet.lagis.broker;

import javax.ws.rs.core.UriInfo;

import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckSchluesselCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.GemarkungCustomBean;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
public class LagisCrossover {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LagisCrossover.class);

    //~ Instance fields --------------------------------------------------------

    private UriInfo context;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LagisCrossover object.
     */
    public LagisCrossover() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   gemarkung  DOCUMENT ME!
     * @param   flur       DOCUMENT ME!
     * @param   zaehler    DOCUMENT ME!
     * @param   nenner     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String loadFlurstueck(final String gemarkung, final int flur, final int zaehler, final int nenner) {
        try {
            final String host = context.getBaseUri().getHost();
            if (!host.equals("localhost") && !host.equals("127.0.0.1")) {
                LOG.info("Keine Request von remote rechnern möglich: " + host);
                return
                    "<html>Es können nur Requests vom lokalen Rechner abgesetzt werden. Es kann nicht zum gewünschten Flurstück gewechselt werden</html>";
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim bestimmen des Hosts Request nicht möglich", ex);
            return
                "<html>Der Host konnte nicht bestimmt werden. Es kann nicht zum gewünschten Flurstück gewechselt werden</html>";
        }
        if (LagisBroker.getInstance().isLoggedIn()) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Crossover: Rest Method load flurstueck called with params: " + gemarkung + " " + flur
                                + " " + zaehler + "/" + nenner);
                }
                FlurstueckSchluesselCustomBean key = FlurstueckSchluesselCustomBean.createNew();
                GemarkungCustomBean gem = GemarkungCustomBean.createNew();
                gem.setBezeichnung(gemarkung);
                gem = CidsBroker.getInstance().completeGemarkung(gem);
                if (gem == null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Change of Flurstueck not possible no such gemarkung: " + key);
                    }
                    return "<html>Test</html>";
                }
                key.setGemarkung(gem);
                key.setFlur(flur);
                key.setFlurstueckZaehler(zaehler);
                key.setFlurstueckNenner(nenner);
                key = CidsBroker.getInstance().completeFlurstueckSchluessel(key);
                if (key != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("found a key on server: " + key);
                    }
                    LagisBroker.getInstance().loadFlurstueck(key);
                    // ToDo does not work under unix (native,other vm's)
                    LagisBroker.getInstance().getParentComponent().toFront();
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Change of Flurstueck not possible no such key: " + key);
                    }
                    return "<html>Test</html>";
                }
            } catch (Exception ex) {
                LOG.error("Failure during loadFlurstueck: ", ex);
                return "<html>Fehler beim laden des Kassenzeichens: " + ex.getMessage() + "</html>";
            }
            return "<html>Gehe zu Flurstueck: </html>";
        } else {
            final String notLoggedIn = "Flurstück kann nicht geladen werden. Benutzer ist noch nicht eingeloggt.";
            if (LOG.isDebugEnabled()) {
                LOG.debug(notLoggedIn);
            }
            return "<html>" + notLoggedIn + "</html>";
        }
    }
}
