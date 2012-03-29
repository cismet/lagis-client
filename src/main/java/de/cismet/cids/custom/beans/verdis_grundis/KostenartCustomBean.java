/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.verdis_grundis;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.broker.CidsBroker;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.hardwired.Kostenart;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class KostenartCustomBean extends BasicEntity implements Kostenart {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KostenartCustomBean.class);
    public static final String TABLE = "kostenart";

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private String bezeichnung;
    private Boolean ist_nebenkostenart;
    private String[] PROPERTY_NAMES = new String[] { "id", "bezeichnung", "ist_nebenkostenart" };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new KostenartCustomBean object.
     */
    public KostenartCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static KostenartCustomBean createNew() {
        try {
            return (KostenartCustomBean)CidsBean.createNewCidsBeanFromTableName(CidsBroker.LAGIS_DOMAIN, TABLE);
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
    @Override
    public String getBezeichnung() {
        return this.bezeichnung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setBezeichnung(final String val) {
        this.bezeichnung = val;

        this.propertyChangeSupport.firePropertyChange("bezeichnung", null, this.bezeichnung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isIst_nebenkostenart() {
        return this.ist_nebenkostenart;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getIst_nebenkostenart() {
        return this.ist_nebenkostenart;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setIst_nebenkostenart(final Boolean val) {
        this.ist_nebenkostenart = val;

        this.propertyChangeSupport.firePropertyChange("ist_nebenkostenart", null, this.ist_nebenkostenart);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public Boolean getIstNebenkostenart() {
        return getIst_nebenkostenart();
    }

    @Override
    public void setIstNebenkostenart(final Boolean val) {
        setIst_nebenkostenart(val);
    }

    @Override
    public boolean isNebenkostenart() {
        return getIst_nebenkostenart();
    }

    @Override
    public String toString() {
        return getBezeichnung();
    }
}
