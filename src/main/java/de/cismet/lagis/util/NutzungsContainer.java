/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.lagis.util;

import de.cismet.lagisEE.entity.core.Nutzung;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author spuhl
 */
public class NutzungsContainer {


    private ArrayList<Nutzung> nutzungen;
    private Date currentDate;

    public NutzungsContainer(ArrayList<Nutzung> nutzungen, Date currentDate) {
        this.nutzungen = nutzungen;
        this.currentDate = currentDate;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public ArrayList<Nutzung> getNutzungen() {
        return nutzungen;
    }

    public void setNutzungen(ArrayList<Nutzung> nutzungen) {
        this.nutzungen = nutzungen;
    }

    
}
