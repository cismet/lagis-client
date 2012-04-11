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
package de.cismet.lagisEE.crossover.entity;

import org.postgis.Geometry;

import java.io.Serializable;

import java.util.Date;
/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 * @Entity   DOCUMENT ME!
 * @Table    (name = "wfs_flurstuecke") @NamedQueries( { @NamedQuery( name = "WfsFlurstuecke.findAll", query = "SELECT w
 *           FROM WfsFlurstuecke w" ), @NamedQuery( name = "WfsFlurstuecke.findByGid", query = "SELECT w FROM
 *           WfsFlurstuecke w WHERE w.gid = :gid" ), @NamedQuery( name = "WfsFlurstuecke.findByGem", query = "SELECT w
 *           FROM WfsFlurstuecke w WHERE w.gem = :gem" ), @NamedQuery( name = "WfsFlurstuecke.findByFlur", query =
 *           "SELECT w FROM WfsFlurstuecke w WHERE w.flur = :flur" ), @NamedQuery( name =
 *           "WfsFlurstuecke.findByFlurstz", query = "SELECT w FROM WfsFlurstuecke w WHERE w.flurstz = :flurstz" ),
 *           &#064;NamedQuery( name = "WfsFlurstuecke.findByFlurstn", query = "SELECT w FROM WfsFlurstuecke w WHERE
 *           w.flurstn = :flurstn" ), @NamedQuery( name = "WfsFlurstuecke.findByObjnr", query = "SELECT w FROM
 *           WfsFlurstuecke w WHERE w.objnr = :objnr" ), @NamedQuery( name = "WfsFlurstuecke.findByHistAb", query =
 *           "SELECT w FROM WfsFlurstuecke w WHERE w.histAb = :histAb" ), @NamedQuery( name =
 *           "WfsFlurstuecke.findByObjEntdat", query = "SELECT w FROM WfsFlurstuecke w WHERE w.objEntdat = :objEntdat"
 *           ), @NamedQuery( name = "WfsFlurstuecke.findByFlstEntdat", query = "SELECT w FROM WfsFlurstuecke w WHERE
 *           w.flstEntdat = :flstEntdat" ), @NamedQuery( name = "WfsFlurstuecke.findByText", query = "SELECT w FROM
 *           WfsFlurstuecke w WHERE w.text = :text" ), @NamedQuery( name = "WfsFlurstuecke.findByObjX", query = "SELECT
 *           w FROM WfsFlurstuecke w WHERE w.objX = :objX" ), @NamedQuery( name = "WfsFlurstuecke.findByObjY", query =
 *           "SELECT w FROM WfsFlurstuecke w WHERE w.objY = :objY" ), @NamedQuery( name =
 *           "WfsFlurstuecke.findByDienststelle", query = "SELECT w FROM WfsFlurstuecke w WHERE w.dienststelle =
 *           :dienststelle" ), @NamedQuery( name = "WfsFlurstuecke.findByFlstArt", query = "SELECT w FROM WfsFlurstuecke
 *           w WHERE w.flstArt = :flstArt" ) } )
 */
public class WfsFlurstuecke implements Serializable {

    //~ Instance fields --------------------------------------------------------

// @Id
// @Basic(optional = false)
// @Column(name = "gid")
    private Integer gid;
//    @Column(name = "gem")
    private Integer gem;
//    @Column(name = "flur")
    private Integer flur;
//    @Column(name = "flurstz")
    private Integer flurstz;
//    @Column(name = "flurstn")
    private Integer flurstn;
//    @Type(type = "de.cismet.hibernate.GeometryType")
//    @Column(
//        name = "the_geom",
//        columnDefinition = "Geometry"
//    )
    private Geometry theGeom;
//    @Column(name = "objnr")
    private String objnr;
//    @Column(name = "hist_ab")
//    @Temporal(TemporalType.DATE)
    private Date histAb;
//    @Column(name = "obj_entdat")
//    @Temporal(TemporalType.DATE)
    private Date objEntdat;
//    @Column(name = "flst_entdat")
//    @Temporal(TemporalType.DATE)
    private Date flstEntdat;
//    @Column(name = "text")
    private String text;
//    @Column(name = "obj_x")
    private Double objX;
//    @Column(name = "obj_y")
    private Double objY;

//    @Type(type = "de.cismet.hibernate.GeometryType")
//    @Column(
//        name = "the_geom_pg",
//        columnDefinition = "Geometry"
//    )
    private Geometry theGeomPg;
//    @Column(name = "dienststelle")
    private String dienststelle;
//    @Column(name = "flst_art")
    private Character flstArt;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WfsFlurstuecke object.
     */
    public WfsFlurstuecke() {
    }

    /**
     * Creates a new WfsFlurstuecke object.
     *
     * @param  gid  DOCUMENT ME!
     */
    public WfsFlurstuecke(final Integer gid) {
        this.gid = gid;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getGid() {
        return gid;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gid  DOCUMENT ME!
     */
    public void setGid(final Integer gid) {
        this.gid = gid;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getGem() {
        return gem;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gem  DOCUMENT ME!
     */
    public void setGem(final Integer gem) {
        this.gem = gem;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getFlur() {
        return flur;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flur  DOCUMENT ME!
     */
    public void setFlur(final Integer flur) {
        this.flur = flur;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getFlurstz() {
        return flurstz;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstz  DOCUMENT ME!
     */
    public void setFlurstz(final Integer flurstz) {
        this.flurstz = flurstz;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getFlurstn() {
        return flurstn;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstn  DOCUMENT ME!
     */
    public void setFlurstn(final Integer flurstn) {
        this.flurstn = flurstn;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Geometry getTheGeom() {
        return theGeom;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  theGeom  DOCUMENT ME!
     */
    public void setTheGeom(final Geometry theGeom) {
        this.theGeom = theGeom;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getObjnr() {
        return objnr;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  objnr  DOCUMENT ME!
     */
    public void setObjnr(final String objnr) {
        this.objnr = objnr;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getHistAb() {
        return histAb;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  histAb  DOCUMENT ME!
     */
    public void setHistAb(final Date histAb) {
        this.histAb = histAb;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getObjEntdat() {
        return objEntdat;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  objEntdat  DOCUMENT ME!
     */
    public void setObjEntdat(final Date objEntdat) {
        this.objEntdat = objEntdat;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getFlstEntdat() {
        return flstEntdat;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flstEntdat  DOCUMENT ME!
     */
    public void setFlstEntdat(final Date flstEntdat) {
        this.flstEntdat = flstEntdat;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getText() {
        return text;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  text  DOCUMENT ME!
     */
    public void setText(final String text) {
        this.text = text;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Double getObjX() {
        return objX;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  objX  DOCUMENT ME!
     */
    public void setObjX(final Double objX) {
        this.objX = objX;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Double getObjY() {
        return objY;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  objY  DOCUMENT ME!
     */
    public void setObjY(final Double objY) {
        this.objY = objY;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Geometry getTheGeomPg() {
        return theGeomPg;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  theGeomPg  DOCUMENT ME!
     */
    public void setTheGeomPg(final Geometry theGeomPg) {
        this.theGeomPg = theGeomPg;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDienststelle() {
        return dienststelle;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dienststelle  DOCUMENT ME!
     */
    public void setDienststelle(final String dienststelle) {
        this.dienststelle = dienststelle;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Character getFlstArt() {
        return flstArt;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flstArt  DOCUMENT ME!
     */
    public void setFlstArt(final Character flstArt) {
        this.flstArt = flstArt;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += ((gid != null) ? gid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WfsFlurstuecke)) {
            return false;
        }
        final WfsFlurstuecke other = (WfsFlurstuecke)object;
        if (((this.gid == null) && (other.gid != null)) || ((this.gid != null) && !this.gid.equals(other.gid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "de.cismet.lagisEE.crossover.entity.WfsFlurstuecke[gid=" + gid + "]";
    }
}
