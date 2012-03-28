/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.beans.verdis_grundis;

import de.cismet.lagisEE.entity.basic.BasicEntity;
import de.cismet.lagisEE.entity.core.hardwired.Kategorie;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class KategorieCustomBean extends BasicEntity implements Kategorie {

    //~ Instance fields --------------------------------------------------------

    private Integer id;
    private String bezeichnung;
    private String abkuerzung;
    private OberkategorieCustomBean fk_oberkategorie;
    private String[] PROPERTY_NAMES = new String[] { "id", "bezeichnung", "abkuerzung", "fk_oberkategorie" };

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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public OberkategorieCustomBean getFk_oberkategorie() {
        return this.fk_oberkategorie;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    public void setFk_oberkategorie(final OberkategorieCustomBean val) {
        this.fk_oberkategorie = val;

        this.propertyChangeSupport.firePropertyChange("fk_oberkategorie", null, this.fk_oberkategorie);
    }

    @Override
    public String[] getPropertyNames() {
        return this.PROPERTY_NAMES;
    }

    @Override
    public OberkategorieCustomBean getOberkategorie() {
        return getFk_oberkategorie();
    }

    @Override
    public void setOberkategorie(final OberkategorieCustomBean val) {
        setFk_oberkategorie(val);
    }
}
