/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.lagis;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.commons.LagisConstants;
import de.cismet.lagis.commons.LagisMetaclassConstants;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.hardwired.Stil;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class StilCustomBean extends BasicEntity implements Stil {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StilCustomBean.class);
    private static final String[] PROPERTY_NAMES = new String[] { "id", "bezeichnung" };

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private String bezeichnung;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StilCustomBean object.
     */
    public StilCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static StilCustomBean createNew() {
        try {
            return (StilCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    LagisConstants.DOMAIN_LAGIS,
                    LagisMetaclassConstants.STIL);
        } catch (Exception ex) {
            LOG.error("error creating " + LagisMetaclassConstants.STIL + " bean", ex);
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
     * @param  paramInteger  DOCUMENT ME!
     */
    @Override
    public void setId(final Integer paramInteger) {
        this.id = paramInteger;

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
     * @param  paramString  DOCUMENT ME!
     */
    @Override
    public void setBezeichnung(final String paramString) {
        this.bezeichnung = paramString;

        this.propertyChangeSupport.firePropertyChange("bezeichnung", null, this.bezeichnung);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }
}
