/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.lagis;

import java.sql.Timestamp;

import java.util.Collection;
import java.util.Date;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.commons.LagisConstants;
import de.cismet.lagis.commons.LagisMetaclassConstants;

import de.cismet.lagisEE.entity.basic.BasicEntity;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VerwaltungsbereicheEintragCustomBean extends BasicEntity {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            VerwaltungsbereicheEintragCustomBean.class);
    private static final String[] PROPERTY_NAMES = new String[] {
            "id",
            "fk_flurstueck",
            "geaendert_am",
            "geaendert_von",
            "n_verwaltungsbereiche"
        };

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private Collection<VerwaltungsbereichCustomBean> n_verwaltungsbereiche;
    private FlurstueckCustomBean fk_flurstueck;
    private Timestamp geaendert_am;
    private String geaendert_von;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VerwaltungsbereichCustomBean object.
     */
    public VerwaltungsbereicheEintragCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static VerwaltungsbereicheEintragCustomBean createNew() {
        try {
            final VerwaltungsbereicheEintragCustomBean bean;
            bean = (VerwaltungsbereicheEintragCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    LagisConstants.DOMAIN_LAGIS,
                    LagisMetaclassConstants.VERWALTUNGSBEREICHE_EINTRAG);
            return bean;
        } catch (Exception ex) {
            LOG.error("error creating " + LagisMetaclassConstants.VERWALTUNGSBEREICHE_EINTRAG + " bean", ex);
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
     * @param  id  val DOCUMENT ME!
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
    public Timestamp getGeaendert_am() {
        return geaendert_am;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geaendert_am  DOCUMENT ME!
     */
    public void setGeaendert_am(final Timestamp geaendert_am) {
        final Object old = this.geaendert_am;
        this.geaendert_am = geaendert_am;
        this.propertyChangeSupport.firePropertyChange("geaendert_am", old, this.geaendert_am);
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
     * @param  fk_flurstueck  val DOCUMENT ME!
     */
    public void setFk_flurstueck(final FlurstueckCustomBean fk_flurstueck) {
        final Object old = this.fk_flurstueck;
        this.fk_flurstueck = fk_flurstueck;

        this.propertyChangeSupport.firePropertyChange("fk_flurstueck", old, this.fk_flurstueck);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<VerwaltungsbereichCustomBean> getN_verwaltungsbereiche() {
        return n_verwaltungsbereiche;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  n_verwaltungsbereiche  DOCUMENT ME!
     */
    public void setN_verwaltungsbereiche(final Collection<VerwaltungsbereichCustomBean> n_verwaltungsbereiche) {
        final Object old = this.n_verwaltungsbereiche;
        this.n_verwaltungsbereiche = n_verwaltungsbereiche;

        this.propertyChangeSupport.firePropertyChange("n_verwaltungsbereiche", old, this.n_verwaltungsbereiche);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getGeaendert_von() {
        return geaendert_von;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geaendert_von  DOCUMENT ME!
     */
    public void setGeaendert_von(final String geaendert_von) {
        final Object old = this.geaendert_von;
        this.geaendert_von = geaendert_von;

        this.propertyChangeSupport.firePropertyChange("geaendert_von", old, this.geaendert_von);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geaendert_am  DOCUMENT ME!
     */
    public void setGeaendert_am(final Date geaendert_am) {
        if (geaendert_am == null) {
            setGeaendert_am((Timestamp)null);
        } else {
            setGeaendert_am(new Timestamp(geaendert_am.getTime()));
        }
    }
}
