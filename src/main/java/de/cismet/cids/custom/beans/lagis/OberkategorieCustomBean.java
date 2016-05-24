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
import de.cismet.lagisEE.entity.core.hardwired.Oberkategorie;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class OberkategorieCustomBean extends BasicEntity implements Oberkategorie {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OberkategorieCustomBean.class);
    private static final String[] PROPERTY_NAMES = new String[] { "id", "bezeichnung", "abkuerzung" };

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private String bezeichnung;
    private String abkuerzung;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new OberkategorieCustomBean object.
     */
    public OberkategorieCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static OberkategorieCustomBean createNew() {
        try {
            return (OberkategorieCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    LagisConstants.DOMAIN_LAGIS,
                    LagisMetaclassConstants.OBERKATEGORIE);
        } catch (Exception ex) {
            LOG.error("error creating " + LagisMetaclassConstants.OBERKATEGORIE + " bean", ex);
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
    @Override
    public String getAbkuerzung() {
        return this.abkuerzung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setAbkuerzung(final String val) {
        this.abkuerzung = val;

        this.propertyChangeSupport.firePropertyChange("abkuerzung", null, this.abkuerzung);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }
}
