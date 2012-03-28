/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.verdis_grundis;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.hardwired.DmsUrl;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class DmsUrlCustomBean extends BasicEntity implements DmsUrl {

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private Integer typ;
    private String name;
    private String beschreibung;
    private UrlCustomBean fk_url;
    private FlurstueckCustomBean fk_flurstueck;
    private String[] PROPERTY_NAMES = new String[] { "id", "typ", "name", "beschreibung", "fk_url", "fk_flurstueck" };

    //~ Methods ----------------------------------------------------------------

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
    public Integer getTyp() {
        return this.typ;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setTyp(final Integer val) {
        this.typ = val;

        this.propertyChangeSupport.firePropertyChange("typ", null, this.typ);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setName(final String val) {
        this.name = val;

        this.propertyChangeSupport.firePropertyChange("name", null, this.name);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getBeschreibung() {
        return this.beschreibung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    @Override
    public void setBeschreibung(final String val) {
        this.beschreibung = val;

        this.propertyChangeSupport.firePropertyChange("beschreibung", null, this.beschreibung);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public UrlCustomBean getFk_url() {
        return this.fk_url;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_url(final UrlCustomBean val) {
        this.fk_url = val;

        this.propertyChangeSupport.firePropertyChange("fk_url", null, this.fk_url);
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
     * @param  val  DOCUMENT ME!
     */
    public void setFk_flurstueck(final FlurstueckCustomBean val) {
        this.fk_flurstueck = val;

        this.propertyChangeSupport.firePropertyChange("fk_flurstueck", null, this.fk_flurstueck);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public UrlCustomBean getUrl() {
        return getFk_url();
    }

    @Override
    public void setUrl(final UrlCustomBean val) {
        setFk_url(val);
    }
}
