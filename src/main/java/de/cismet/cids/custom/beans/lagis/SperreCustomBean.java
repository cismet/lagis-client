/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.lagis;

import java.sql.Timestamp;

import java.util.Date;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.commons.LagisConstants;
import de.cismet.lagis.commons.LagisMetaclassConstants;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.locking.Sperre;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class SperreCustomBean extends BasicEntity implements Sperre {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SperreCustomBean.class);
    private static final String[] PROPERTY_NAMES = new String[] {
            "id",
            "fk_flurstueck_schluessel",
            "benutzerkonto",
            "informationen",
            "zeitstempel_timestamp"
        };

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private Integer fk_flurstueck_schluessel;
    private String benutzerkonto;
    private String informationen;
    private Timestamp zeitstempel_timestamp;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SperreCustomBean object.
     */
    public SperreCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static SperreCustomBean createNew() {
        try {
            return (SperreCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    LagisConstants.DOMAIN_LAGIS,
                    LagisMetaclassConstants.SPERRE);
        } catch (Exception ex) {
            LOG.error("error creating " + LagisMetaclassConstants.SPERRE + " bean", ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueckSchluessel  DOCUMENT ME!
     * @param   benutzerkonto         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static SperreCustomBean createNew(final FlurstueckSchluesselCustomBean flurstueckSchluessel,
            final String benutzerkonto) {
        final SperreCustomBean bean = createNew();
        bean.setFlurstueckSchluessel(flurstueckSchluessel.getId());
        bean.setBenutzerkonto(benutzerkonto);
        bean.setZeitstempel(new Date());
        return bean;
    }

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

        this.propertyChangeSupport.firePropertyChange("zeitstempel_timestamp", null, this.zeitstempel_timestamp);
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
