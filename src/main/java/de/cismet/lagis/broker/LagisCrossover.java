/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.broker;

import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.core.hardwired.Gemarkung;
import de.cismet.lagisEE.util.Key;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 *
 * @author spuhl
 */
@Path("/lagis/")
public class LagisCrossover {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LagisCrossover.class);

    public LagisCrossover() {
    }

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
            } else {
                log.debug("Change of Flurstueck not possible no such key: "+key);
                return "<html>Test</html>";
            }
        } catch (Exception ex) {
            log.error("Failure during loadFlurstueck: ", ex);
        }
        return "<html>Test</html>";
    }

    @GET
    @Produces("text/html")
    @Path("/test/")
    public String test() {
        log.debug("Crossover: testmethod");
        return "<html>Test</html>";
    }
}
