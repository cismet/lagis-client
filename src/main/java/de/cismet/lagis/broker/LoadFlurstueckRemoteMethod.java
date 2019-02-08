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

import org.openide.util.lookup.ServiceProvider;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;
import de.cismet.cids.custom.beans.lagis.GemarkungCustomBean;

import de.cismet.lagis.gui.main.LagisApp;

import de.cismet.remote.AbstractRESTRemoteControlMethod;
import de.cismet.remote.RESTRemoteControlMethod;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @author   Benjamin Friedrich (benjamin.friedrich@cismet.de)
 * @version  $Revision$, $Date$
 */
@Path("/loadFlurstueck")
@ServiceProvider(service = RESTRemoteControlMethod.class)
public class LoadFlurstueckRemoteMethod extends AbstractRESTRemoteControlMethod {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            LoadFlurstueckRemoteMethod.class);

    //~ Instance fields --------------------------------------------------------

    @Context private UriInfo context;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LagisCrossover object.
     */
    public LoadFlurstueckRemoteMethod() {
        super(-1, "/loadFlurstueck");
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
    @GET
    @Produces("text/html")
    public String loadFlurstueck(@QueryParam("gemarkung") final String gemarkung,
            @QueryParam("flur") final int flur,
            @QueryParam("zaehler") final int zaehler,
            @QueryParam("nenner") final int nenner) {
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
                final FlurstueckSchluesselCustomBean key = FlurstueckSchluesselCustomBean.createNew();
                GemarkungCustomBean gem = GemarkungCustomBean.createNew();
                gem.setBezeichnung(gemarkung);
                gem = LagisBroker.getInstance().completeGemarkung(gem);
                if (gem == null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Change of Flurstueck not possible! No such Gemarkung: " + key);
                    }
                    return "<html>Change of Flurstueck not possible! No such Gemarkung: " + key + "</html>";
                }
                key.setGemarkung(gem);
                key.setFlur(flur);
                key.setFlurstueckZaehler(zaehler);
                key.setFlurstueckNenner(nenner);
                if (key != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("found a key on server: " + key);
                    }
                    LagisBroker.getInstance().loadFlurstueck(key);
                    // ToDo does not work under unix (native,other vm's)
                    LagisApp.getInstance().toFront();
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Change of Flurstueck not possible! No such key: " + key);
                    }
                    return "<html>Change of Flurstueck not possible! No such key: " + key + "</html>";
                }
            } catch (Exception ex) {
                LOG.error("Failure during loadFlurstueck: ", ex);
                return "<html>Fehler beim laden des Flrustücks: " + ex.getMessage() + "</html>";
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
