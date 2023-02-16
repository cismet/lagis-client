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

import javax.swing.SwingWorker;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.gui.main.LagisApp;

import de.cismet.remote.AbstractRESTRemoteControlMethod;
import de.cismet.remote.RESTRemoteControlMethod;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@Path("/loadMeldung")
@ServiceProvider(service = RESTRemoteControlMethod.class)
public class LoadMeldungRemoteMethod extends AbstractRESTRemoteControlMethod {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LoadMeldungRemoteMethod.class);

    //~ Instance fields --------------------------------------------------------

    @Context private UriInfo context;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LagisCrossover object.
     */
    public LoadMeldungRemoteMethod() {
        super(-1, "/loadMeldung");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   id  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @GET
    @Produces("text/html")
    public String loadFlurstueck(@QueryParam("id") final Integer id) {
        try {
            final String host = context.getBaseUri().getHost();
            if (!host.equals("localhost") && !host.equals("127.0.0.1")) {
                LOG.info("Keine Request von remote rechnern möglich: " + host);
                return
                    "<html>Es können nur Requests vom lokalen Rechner abgesetzt werden. Die gewünschte Meldung kann nicht geöffnet werden.</html>";
            }
        } catch (final Exception ex) {
            LOG.error("Fehler beim bestimmen des Hosts Request nicht möglich", ex);
            return
                "<html>Der Host konnte nicht bestimmt werden. Die gewünschte Meldung kann nicht geöffnet werden.</html>";
        }
        if (LagisBroker.getInstance().isLoggedIn()) {
            new SwingWorker<CidsBean, Void>() {

                    @Override
                    protected CidsBean doInBackground() throws Exception {
                        return CidsBroker.getInstance()
                                    .getLagisMetaObject(
                                            id,
                                            CidsBroker.getInstance().getLagisMetaClass("MELDUNG").getId())
                                    .getBean();
                    }

                    @Override
                    protected void done() {
                        try {
                            final CidsBean meldungBean = get();
                            LagisApp.getInstance().getMeldungenPanel().showMeldung(null);
                            LagisApp.getInstance().getMeldungenPanel().showMeldung(meldungBean);
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                        }
                    }
                }.execute();
            return "<html>Meldung wird in LagIS geladen...</html>";
        } else {
            return "<html>Meldung kann nicht geladen werden. Benutzer ist noch nicht in LagIS eingeloggt.</html>";
        }
    }
}
