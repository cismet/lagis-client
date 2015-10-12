/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.lagis;

import java.sql.Timestamp;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.broker.CidsBroker;

import de.cismet.lagisEE.entity.basic.BasicEntity;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class KassenzeichenCustomBean extends BasicEntity {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KassenzeichenCustomBean.class);
    public static final String TABLE = "kassenzeichen";

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private FlurstueckCustomBean fk_flurstueck;
    private Integer kassenzeichennummer;
    private Timestamp zugeordnet_am;
    private String zugeordnet_von;

    private final String[] PROPERTY_NAMES = new String[] {
            "id",
            "kassenzeichennummer",
            "fk_flurstueck",
            "zugeordnet_am",
            "zugeordnet_von"
        };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VerwaltungsbereichCustomBean object.
     */
    public KassenzeichenCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static KassenzeichenCustomBean createNew() {
        try {
            final KassenzeichenCustomBean bean;
            bean = (KassenzeichenCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    CidsBroker.LAGIS_DOMAIN,
                    TABLE);
            return bean;
        } catch (Exception ex) {
            LOG.error("error creating " + TABLE + " bean", ex);
            return null;
        }
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
     * @param  id  DOCUMENT ME!
     */
    @Override
    public void setId(final Integer id) {
        final Object old = this.id;
        this.id = id;
        this.propertyChangeSupport.firePropertyChange("id", old, this.id);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FlurstueckCustomBean getFk_flurstueck() {
        return this.fk_flurstueck;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fk_flurstueck  DOCUMENT ME!
     */
    public void setFk_verwaltungsgebrauch(final FlurstueckCustomBean fk_flurstueck) {
        final Object old = this.fk_flurstueck;
        this.fk_flurstueck = fk_flurstueck;
        this.propertyChangeSupport.firePropertyChange("fk_flurstueck", old, this.fk_flurstueck);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getKassenzeichennummer() {
        return kassenzeichennummer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kassenzeichennummer  DOCUMENT ME!
     */
    public void setKassenzeichennummer(final Integer kassenzeichennummer) {
        final Object old = this.kassenzeichennummer;
        this.kassenzeichennummer = kassenzeichennummer;
        this.propertyChangeSupport.firePropertyChange("kassenzeichennummer", old, this.kassenzeichennummer);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Timestamp getZugeordnet_am() {
        return zugeordnet_am;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  zugeordnet_am  DOCUMENT ME!
     */
    public void setZugeordnet_am(final Timestamp zugeordnet_am) {
        final Object old = this.zugeordnet_am;
        this.zugeordnet_am = zugeordnet_am;
        this.propertyChangeSupport.firePropertyChange("zugeordnet_am", old, this.zugeordnet_am);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getZugeordnet_von() {
        return zugeordnet_von;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  zugeordnet_von  DOCUMENT ME!
     */
    public void setZugeordnet_von(final String zugeordnet_von) {
        final Object old = this.zugeordnet_von;
        this.zugeordnet_von = zugeordnet_von;
        this.propertyChangeSupport.firePropertyChange("zugeordnet_von", old, this.zugeordnet_von);
    }

    @Override
    public String toString() {
        return Integer.toString(kassenzeichennummer);
    }
}
