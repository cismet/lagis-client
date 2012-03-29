/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.verdis_grundis;

import org.openide.util.Exceptions;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.broker.CidsBroker;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.hardwired.Anlageklasse;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AnlageklasseCustomBean extends BasicEntity implements Anlageklasse {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AnlageklasseCustomBean.class);
    public static final String TABLE = "anlageklasse";

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private String schluessel;
    private String bezeichnung;
    private String[] PROPERTY_NAMES = new String[] { "id", "schluessel", "bezeichnung" };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AnlageklasseCustomBean object.
     */
    public AnlageklasseCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static AnlageklasseCustomBean createNew() {
        try {
            return (AnlageklasseCustomBean)CidsBean.createNewCidsBeanFromTableName(CidsBroker.LAGIS_DOMAIN, TABLE);
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
    public String getSchluessel() {
        return this.schluessel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setSchluessel(final String val) {
        this.schluessel = val;

        this.propertyChangeSupport.firePropertyChange("schluessel", null, this.schluessel);
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

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public String toString() {
        return getBezeichnung();
    }
}
