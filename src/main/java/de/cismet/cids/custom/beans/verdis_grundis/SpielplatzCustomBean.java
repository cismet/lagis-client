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
import de.cismet.lagisEE.entity.extension.spielplatz.Spielplatz;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class SpielplatzCustomBean extends BasicEntity implements Spielplatz {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SpielplatzCustomBean.class);
    public static final String TABLE = "spielplatz";

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private Boolean klettergeruest_vorhanden;
    private Boolean ist_klettergeruest_wartung_erforderlich;
    private Boolean rutsche_vorhanden;
    private Boolean ist_rutsche_wartung_erforderlich;
    private Boolean sandkasten_vorhanden;
    private Boolean ist_sandkasten_wartung_erforderlich;
    private Boolean schaukel_vorhanden;
    private Boolean ist_schaukel_wartung_erforderlich;
    private Boolean wippe_vorhanden;
    private Boolean ist_wippe_wartung_erforderlich;
    private String[] PROPERTY_NAMES = new String[] {
            "id",
            "klettergeruest_vorhanden",
            "ist_klettergeruest_wartung_erforderlich",
            "rutsche_vorhanden",
            "ist_rutsche_wartung_erforderlich",
            "sandkasten_vorhanden",
            "ist_sandkasten_wartung_erforderlich",
            "schaukel_vorhanden",
            "ist_schaukel_wartung_erforderlich",
            "wippe_vorhanden",
            "ist_wippe_wartung_erforderlich"
        };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SpielplatzCustomBean object.
     */
    public SpielplatzCustomBean() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static SpielplatzCustomBean createNew() {
        try {
            return (SpielplatzCustomBean)CidsBean.createNewCidsBeanFromTableName(CidsBroker.LAGIS_DOMAIN, TABLE);
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
    public Boolean isKlettergeruest_vorhanden() {
        return this.klettergeruest_vorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getKlettergeruest_vorhanden() {
        return this.klettergeruest_vorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setKlettergeruest_vorhanden(final Boolean val) {
        this.klettergeruest_vorhanden = val;

        this.propertyChangeSupport.firePropertyChange("klettergeruest_vorhanden", null, this.klettergeruest_vorhanden);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isIst_klettergeruest_wartung_erforderlich() {
        return this.ist_klettergeruest_wartung_erforderlich;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getIst_klettergeruest_wartung_erforderlich() {
        return this.ist_klettergeruest_wartung_erforderlich;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setIst_klettergeruest_wartung_erforderlich(final Boolean val) {
        this.ist_klettergeruest_wartung_erforderlich = val;

        this.propertyChangeSupport.firePropertyChange(
            "ist_klettergeruest_wartung_erforderlich",
            null,
            this.ist_klettergeruest_wartung_erforderlich);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isRutsche_vorhanden() {
        return this.rutsche_vorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getRutsche_vorhanden() {
        return this.rutsche_vorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setRutsche_vorhanden(final Boolean val) {
        this.rutsche_vorhanden = val;

        this.propertyChangeSupport.firePropertyChange("rutsche_vorhanden", null, this.rutsche_vorhanden);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isIst_rutsche_wartung_erforderlich() {
        return this.ist_rutsche_wartung_erforderlich;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getIst_rutsche_wartung_erforderlich() {
        return this.ist_rutsche_wartung_erforderlich;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setIst_rutsche_wartung_erforderlich(final Boolean val) {
        this.ist_rutsche_wartung_erforderlich = val;

        this.propertyChangeSupport.firePropertyChange(
            "ist_rutsche_wartung_erforderlich",
            null,
            this.ist_rutsche_wartung_erforderlich);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isSandkasten_vorhanden() {
        return this.sandkasten_vorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getSandkasten_vorhanden() {
        return this.sandkasten_vorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setSandkasten_vorhanden(final Boolean val) {
        this.sandkasten_vorhanden = val;

        this.propertyChangeSupport.firePropertyChange("sandkasten_vorhanden", null, this.sandkasten_vorhanden);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isIst_sandkasten_wartung_erforderlich() {
        return this.ist_sandkasten_wartung_erforderlich;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getIst_sandkasten_wartung_erforderlich() {
        return this.ist_sandkasten_wartung_erforderlich;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setIst_sandkasten_wartung_erforderlich(final Boolean val) {
        this.ist_sandkasten_wartung_erforderlich = val;

        this.propertyChangeSupport.firePropertyChange(
            "ist_sandkasten_wartung_erforderlich",
            null,
            this.ist_sandkasten_wartung_erforderlich);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isSchaukel_vorhanden() {
        return this.schaukel_vorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getSchaukel_vorhanden() {
        return this.schaukel_vorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setSchaukel_vorhanden(final Boolean val) {
        this.schaukel_vorhanden = val;

        this.propertyChangeSupport.firePropertyChange("schaukel_vorhanden", null, this.schaukel_vorhanden);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isIst_schaukel_wartung_erforderlich() {
        return this.ist_schaukel_wartung_erforderlich;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getIst_schaukel_wartung_erforderlich() {
        return this.ist_schaukel_wartung_erforderlich;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setIst_schaukel_wartung_erforderlich(final Boolean val) {
        this.ist_schaukel_wartung_erforderlich = val;

        this.propertyChangeSupport.firePropertyChange(
            "ist_schaukel_wartung_erforderlich",
            null,
            this.ist_schaukel_wartung_erforderlich);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isWippe_vorhanden() {
        return this.wippe_vorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getWippe_vorhanden() {
        return this.wippe_vorhanden;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setWippe_vorhanden(final Boolean val) {
        this.wippe_vorhanden = val;

        this.propertyChangeSupport.firePropertyChange("wippe_vorhanden", null, this.wippe_vorhanden);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isIst_wippe_wartung_erforderlich() {
        return this.ist_wippe_wartung_erforderlich;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getIst_wippe_wartung_erforderlich() {
        return this.ist_wippe_wartung_erforderlich;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setIst_wippe_wartung_erforderlich(final Boolean val) {
        this.ist_wippe_wartung_erforderlich = val;

        this.propertyChangeSupport.firePropertyChange(
            "ist_wippe_wartung_erforderlich",
            null,
            this.ist_wippe_wartung_erforderlich);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public boolean isKlettergeruestVorhanden() {
        return getKlettergeruest_vorhanden();
    }

    @Override
    public void setKlettergeruestVorhanden(final boolean val) {
        setKlettergeruest_vorhanden(val);
    }

    @Override
    public boolean isKlettergeruestWartungErforderlich() {
        return getIst_klettergeruest_wartung_erforderlich();
    }

    @Override
    public void setKlettergeruestWartungErforderlich(final boolean val) {
        setIst_klettergeruest_wartung_erforderlich(val);
    }

    @Override
    public boolean isRutscheVorhanden() {
        return getRutsche_vorhanden();
    }

    @Override
    public void setRutscheVorhanden(final boolean val) {
        setRutsche_vorhanden(val);
    }

    @Override
    public boolean isRutscheWartungErforderlich() {
        return getIst_rutsche_wartung_erforderlich();
    }

    @Override
    public void setRutscheWartungErforderlich(final boolean val) {
        setIst_rutsche_wartung_erforderlich(val);
    }

    @Override
    public boolean isSandkastenVorhanden() {
        return getSandkasten_vorhanden();
    }

    @Override
    public void setSandkastenVorhanden(final boolean val) {
        setSandkasten_vorhanden(val);
    }

    @Override
    public boolean isSandkastenWartungErforderlich() {
        return getIst_sandkasten_wartung_erforderlich();
    }

    @Override
    public void setSandkastenWartungErforderlich(final boolean val) {
        setIst_sandkasten_wartung_erforderlich(val);
    }

    @Override
    public boolean isSchaukelVorhanden() {
        return getSchaukel_vorhanden();
    }

    @Override
    public void setSchaukelVorhanden(final boolean val) {
        setSchaukel_vorhanden(val);
    }

    @Override
    public boolean isSchaukelWartungErforderlich() {
        return getIst_schaukel_wartung_erforderlich();
    }

    @Override
    public void setSchaukelWartungErforderlich(final boolean val) {
        setIst_schaukel_wartung_erforderlich(val);
    }

    @Override
    public boolean isWippeVorhanden() {
        return getWippe_vorhanden();
    }

    @Override
    public void setWippeVorhanden(final boolean val) {
        setWippe_vorhanden(val);
    }

    @Override
    public boolean isWippeWartungErforderlich() {
        return getIst_wippe_wartung_erforderlich();
    }

    @Override
    public void setWippeWartungErforderlich(final boolean val) {
        setIst_wippe_wartung_erforderlich(val);
    }

    @Override
    public String toString() {
        return "de.cismet.lagisEE.entity.extension.Spielplatz[id=" + getId() + "]";
    }
}
