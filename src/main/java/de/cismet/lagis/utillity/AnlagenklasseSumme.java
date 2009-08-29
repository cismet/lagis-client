/*
 * AnlagenklasseSumme.java
 *
 * Created on 24. April 2007, 13:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.utillity;

import de.cismet.lagisEE.entity.core.hardwired.Anlageklasse;
import org.apache.log4j.Logger;



/**
 *
 * @author Puhl
 */
public class AnlagenklasseSumme implements Comparable {
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    
    private Anlageklasse anlageklasse;
    private double summe;
    
    /**
     * Creates a new instance of AnlagenklasseSumme
     */
    public AnlagenklasseSumme(Anlageklasse anlageklasse) {
    this.anlageklasse = anlageklasse;
    }
    
    public double getSumme() {
        return summe;
    }
    
    public void setSumme(double summe) {
        this.summe = summe;
    }
    
    public boolean equals(Object obj) {
        log.debug("equals called");
        if(obj instanceof AnlagenklasseSumme){            
            log.info("ist Anlagensummenklasse");
            AnlagenklasseSumme other = (AnlagenklasseSumme) obj;
            if( anlageklasse != null && 
                other.getAnlageklasse() != null){
                log.info("1 auswertung");
                log.info("1 ausertung: "+((other.getAnlageklasse().getId().equals(anlageklasse.getId())) && summe == other.getSumme()));
                return (other.getAnlageklasse().getId().equals(anlageklasse.getId())) && summe == other.getSumme();
            } else if(anlageklasse == null && other.getAnlageklasse() == null){
                log.info("2 auswertung");
                log.info("2 ausertung: "+(summe == other.getSumme()));
                return summe == other.getSumme();                
            } else {
                log.debug("3 auswertung");
                return false;
            }
        } else if(obj instanceof Anlageklasse) {
            log.info("ist Anlageklasse");
            Anlageklasse other = (Anlageklasse) obj;
            if(anlageklasse != null && other != null){
                log.info("1 auswertung");
                log.info("1 auswertung"+(anlageklasse.getId().equals(other.getId())));                
                return anlageklasse.getId().equals(other.getId());
            } else if(anlageklasse == null && other == null){
                log.info("2 auswertung");                
                return true;
            } else {
                log.info("3 auswertung");                
                return false;
            }
        } else {
            log.warn("ist gar nix");
            return false;
        }
    }
    
    public Anlageklasse getAnlageklasse() {
        return anlageklasse;
    }
    
    public void setAnlageklasse(Anlageklasse anlageklasse) {
        this.anlageklasse = anlageklasse;
    }

    public int compareTo(Object o) {
        if(o instanceof AnlagenklasseSumme){
            AnlagenklasseSumme as = (AnlagenklasseSumme) o;
            return anlageklasse.getSchluessel().compareTo(as.getAnlageklasse().getSchluessel());
        } else {
            return anlageklasse.getSchluessel().compareTo(o.toString());
        }
    }       
    
    
}
