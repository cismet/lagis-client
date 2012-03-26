/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.verdis_grundis;

import java.sql.Timestamp;

import java.util.Date;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.EJBrokerInterfaces.Sperre;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class SperreCustomBean extends CidsBean implements Sperre {

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private Integer fk_flurstueck_schluessel;
    private String benutzerkonto;
    private String informationen;
    private Timestamp zeitstempel_timestamp;
    private String[] PROPERTY_NAMES = new String[] {
            "id",
            "fk_flurstueck_schluessel",
            "benutzerkonto",
            "informationen",
            "zeitstempel"
        };

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Integer getId() {
        return this.id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setId(final Integer val) {
        this.id = val;

        this.propertyChangeSupport.firePropertyChange("id", null, this.id);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getFk_flurstueck_schluessel() {
        return this.fk_flurstueck_schluessel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_flurstueck_schluessel(final Integer val) {
        this.fk_flurstueck_schluessel = val;

        this.propertyChangeSupport.firePropertyChange("fk_flurstueck_schluessel", null, this.fk_flurstueck_schluessel);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getBenutzerkonto() {
        return this.benutzerkonto;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setBenutzerkonto(final String val) {
        this.benutzerkonto = val;

        this.propertyChangeSupport.firePropertyChange("benutzerkonto", null, this.benutzerkonto);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getInformationen() {
        return this.informationen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setInformationen(final String val) {
        this.informationen = val;

        this.propertyChangeSupport.firePropertyChange("informationen", null, this.informationen);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Timestamp getZeitstempel_timestamp() {
        return this.zeitstempel_timestamp;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setZeitstempel_timestamp(final Timestamp val) {
        this.zeitstempel_timestamp = val;

        this.propertyChangeSupport.firePropertyChange("zeitstempel", null, this.zeitstempel_timestamp);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public Integer getFlurstueckSchluessel() {
        return getFk_flurstueck_schluessel();
    }

    @Override
    public void setFlurstueckSchluessel(final Integer val) {
        setFk_flurstueck_schluessel(val);
    }

    @Override
    public Date getZeitstempel() {
        return getZeitstempel_timestamp();
    }

    @Override
    public void setZeitstempel(final Date val) {
        if (val == null) {
            setZeitstempel_timestamp(null);
        } else {
            setZeitstempel_timestamp(new Timestamp(val.getTime()));
        }
    }
}
