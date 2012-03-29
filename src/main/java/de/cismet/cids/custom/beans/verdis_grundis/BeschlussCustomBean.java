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
import de.cismet.lagisEE.entity.core.Beschluss;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BeschlussCustomBean extends BasicEntity implements Beschluss {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BeschlussCustomBean.class);
    public static final String TABLE = "beschluss";

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private Timestamp datum;
    private BeschlussartCustomBean fk_beschlussart;
    private VertragsartCustomBean fk_vertrag;
    private String[] PROPERTY_NAMES = new String[] { "id", "datum", "fk_beschlussart", "fk_vertrag" };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BeschlussCustomBean object.
     */
    public BeschlussCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BeschlussCustomBean createNew() {
        try {
            return (BeschlussCustomBean)CidsBean.createNewCidsBeanFromTableName(CidsBroker.LAGIS_DOMAIN, TABLE);
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
    public BeschlussartCustomBean getFk_beschlussart() {
        return this.fk_beschlussart;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_beschlussart(final BeschlussartCustomBean val) {
        this.fk_beschlussart = val;

        this.propertyChangeSupport.firePropertyChange("fk_beschlussart", null, this.fk_beschlussart);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public VertragsartCustomBean getFk_vertrag() {
        return this.fk_vertrag;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_vertrag(final VertragsartCustomBean val) {
        this.fk_vertrag = val;

        this.propertyChangeSupport.firePropertyChange("fk_vertrag", null, this.fk_vertrag);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public BeschlussartCustomBean getBeschlussart() {
        return getFk_beschlussart();
    }

    @Override
    public void setBeschlussart(final BeschlussartCustomBean val) {
        setBeschlussart(val);
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
