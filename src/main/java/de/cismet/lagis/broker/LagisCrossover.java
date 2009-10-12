/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.broker;

import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.core.hardwired.Gemarkung;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author spuhl
 */
@Path("/lagis/")
public class LagisCrossover {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LagisCrossover.class);        

    public LagisCrossover() {
    }


    @Context private UriInfo context;

    //ToDo crossover secure from access of not localhost clients
    //example query http://localhost:9000/lagis/loadFlurstueck?gemarkung=Barmen&flur=1&zaehler=100&nenner=0
    @GET
    @Produces("text/html")
    @Path("/loadFlurstueck/")
    public String loadFlurstueck(
            @QueryParam("gemarkung") String gemarkung,
            @QueryParam("flur") int flur,
            @QueryParam("zaehler") int zaehler,
            @QueryParam("nenner") int nenner) {
        try{
           final String host = context.getBaseUri().getHost();
           if(!host.equals("localhost")&&!host.equals("127.0.0.1")){
                log.info("Keine Request von remote rechnern möglich: "+host);
                return "<html>Es können nur Requests vom lokalen Rechner abgesetzt werden. Es kann nicht zum gewünschten Flurstück gewechselt werden</html>";
           }
        }catch(Exception ex){
            log.error("Fehler beim bestimmen des Hosts Request nicht möglich");
            return "<html>Der Host konnte nicht bestimmt werden. Es kann nicht zum gewünschten Flurstück gewechselt werden</html>";
        }
        if (LagisBroker.getInstance().isLoggedIn()) {
        try {
            log.debug("Crossover: Rest Method load flurstueck called with params: " + gemarkung + " " + flur + " " + zaehler + "/" + nenner);
            FlurstueckSchluessel key = new FlurstueckSchluessel();
            Gemarkung gem = new Gemarkung();
            gem.setBezeichnung(gemarkung);
            gem = EJBroker.getInstance().completeGemarkung(gem);
            if(gem == null){
                log.debug("Change of Flurstueck not possible no such gemarkung: "+key);
                return "<html>Test</html>";
            }
            key.setGemarkung(gem);
            key.setFlur(flur);
            key.setFlurstueckZaehler(zaehler);
            key.setFlurstueckNenner(nenner);
            key = EJBroker.getInstance().completeFlurstueckSchluessel(key);
            if(key != null){                
                log.debug("found a key on server: "+key);
                LagisBroker.getInstance().loadFlurstueck(key);
                //ToDo does not work under unix (native,other vm's)
                LagisBroker.getInstance().getParentComponent().toFront();
            } else {
                log.debug("Change of Flurstueck not possible no such key: "+key);
                return "<html>Test</html>";
            }
        } catch (Exception ex) {
            log.error("Failure during loadFlurstueck: ", ex);
             return "<html>Fehler beim laden des Kassenzeichens: " + ex.getMessage() + "</html>";
            }
            return "<html>Gehe zu Flurstueck: </html>";
        } else {
            final String notLoggedIn = "Flurstück kann nicht geladen werden. Benutzer ist noch nicht eingeloggt.";
            log.debug(notLoggedIn);
            return "<html>" + notLoggedIn + "</html>";
        }
    }

    @GET
    @Produces("text/html")
    @Path("/test/")
    public String test() {
        log.debug("Crossover: testmethod");
        return "<html>Test</html>";
    }


   
}
