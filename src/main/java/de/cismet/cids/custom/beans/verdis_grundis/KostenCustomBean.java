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

import de.cismet.lagis.broker.CidsBroker;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.Kosten;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class KostenCustomBean extends BasicEntity implements Kosten {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KostenCustomBean.class);
    public static final String TABLE = "kosten";

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private Timestamp datum;
    private KostenartCustomBean fk_kostenart;
    private Double betrag;
    private VertragCustomBean fk_vertrag;
    private String[] PROPERTY_NAMES = new String[] { "id", "datum", "fk_kostenart", "betrag", "fk_vertrag" };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new KostenCustomBean object.
     */
    public KostenCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static KostenCustomBean createNew() {
        try {
            return (KostenCustomBean)CidsBean.createNewCidsBeanFromTableName(CidsBroker.LAGIS_DOMAIN, TABLE);
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
    public Timestamp getDatum() {
        return this.datum;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setDatum(final Timestamp val) {
        this.datum = val;

        this.propertyChangeSupport.firePropertyChange("datum", null, this.datum);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public KostenartCustomBean getFk_kostenart() {
        return this.fk_kostenart;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_kostenart(final KostenartCustomBean val) {
        this.fk_kostenart = val;

        this.propertyChangeSupport.firePropertyChange("fk_kostenart", null, this.fk_kostenart);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Double getBetrag() {
        return this.betrag;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setBetrag(final Double val) {
        this.betrag = val;

        this.propertyChangeSupport.firePropertyChange("betrag", null, this.betrag);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public VertragCustomBean getFk_vertrag() {
        return this.fk_vertrag;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_vertrag(final VertragCustomBean val) {
        this.fk_vertrag = val;

        this.propertyChangeSupport.firePropertyChange("fk_vertrag", null, this.fk_vertrag);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public KostenartCustomBean getKostenart() {
        return getFk_kostenart();
    }

    @Override
    public void setKostenart(final KostenartCustomBean val) {
        setFk_kostenart(val);
    }

    @Override
    public void setDatum(final Date val) {
        if (val == null) {
            setDatum(null);
        } else {
            setDatum(new Timestamp(val.getTime()));
        }
    }
}
