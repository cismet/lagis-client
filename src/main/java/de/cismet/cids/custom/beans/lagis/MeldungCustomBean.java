/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.lagis;

import lombok.Getter;

import java.sql.Timestamp;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagis.commons.LagisConstants;
import de.cismet.lagis.commons.LagisMetaclassConstants;

import de.cismet.lagisEE.entity.basic.BasicEntity;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@Getter
public class MeldungCustomBean extends BasicEntity {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MeldungCustomBean.class);
    private static final String[] PROPERTY_NAMES = new String[] {
            "id",
            "fk_flurstueck",
            "name",
            "text",
            "creator",
            "target",
            "timestamp",
            "erledigt_von",
            "erledigt_am"
        };

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private FlurstueckCustomBean fk_flurstueck;
    private String name;
    private String text;
    private String creator;
    private String target;
    private Timestamp timestamp;
    private String erledigt_von;
    private Timestamp erledigt_am;

    //~ Methods ----------------------------------------------------------------

    @Override
    public Integer getId() {
        return this.id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static MeldungCustomBean createNew() {
        try {
            final MeldungCustomBean meldung = (MeldungCustomBean)CidsBean.createNewCidsBeanFromTableName(
                    LagisConstants.DOMAIN_LAGIS,
                    LagisMetaclassConstants.MELDUNG);

            return meldung;
        } catch (Exception ex) {
            LOG.error("error creating " + LagisMetaclassConstants.MELDUNG + " bean", ex);
            return null;
        }
    }

    @Override
    public void setId(final Integer val) {
        this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = val);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_flurstueck(final FlurstueckCustomBean val) {
        this.propertyChangeSupport.firePropertyChange("fk_flurstueck", this.fk_flurstueck, this.fk_flurstueck = val);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  name  DOCUMENT ME!
     */
    public void setName(final String name) {
        this.propertyChangeSupport.firePropertyChange("name", this.name, this.name = name);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  text  DOCUMENT ME!
     */
    public void setText(final String text) {
        this.propertyChangeSupport.firePropertyChange("text", this.text, this.text = text);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  target  DOCUMENT ME!
     */
    public void setTarget(final String target) {
        this.propertyChangeSupport.firePropertyChange("target", this.target, this.target = target);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  creator  DOCUMENT ME!
     */
    public void setCreator(final String creator) {
        this.propertyChangeSupport.firePropertyChange("creator", this.creator, this.creator = creator);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  erledigt_von  DOCUMENT ME!
     */
    public void setErledigt_von(final String erledigt_von) {
        this.propertyChangeSupport.firePropertyChange(
            "erledigt_von",
            this.erledigt_von,
            this.erledigt_von = erledigt_von);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  erledigt_am  DOCUMENT ME!
     */
    public void setErledigt_am(final Timestamp erledigt_am) {
        this.propertyChangeSupport.firePropertyChange("erledigt_am", this.erledigt_am, this.erledigt_am = erledigt_am);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  timestamp  DOCUMENT ME!
     */
    public void setTimestamp(final Timestamp timestamp) {
        this.propertyChangeSupport.firePropertyChange("timestamp", this.timestamp, this.timestamp = timestamp);
    }

    @Override
    public String[] getPropertyNames() {
        return MeldungCustomBean.PROPERTY_NAMES;
    }
}
