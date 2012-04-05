/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagisEE.entity.core.hardwired;

import de.cismet.cids.custom.beans.lagis.RessortCustomBean;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface VerwaltendeDienststelle {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Integer getId();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setId(final Integer val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    RessortCustomBean getRessort();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setRessort(final RessortCustomBean val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getAbkuerzungAbteilung();

    /**
     * DOCUMENT ME!
     *
     * @param  abkuerzungAbteilung  DOCUMENT ME!
     */
    void setAbkuerzungAbteilung(final String abkuerzungAbteilung);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getBezeichnungAbteilung();

    /**
     * DOCUMENT ME!
     *
     * @param  abteilungsname  DOCUMENT ME!
     */
    void setBezeichnungAbteilung(final String abteilungsname);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getEmailAdresse();

    /**
     * DOCUMENT ME!
     *
     * @param  emailAdresse  DOCUMENT ME!
     */
    void setEmailAdresse(final String emailAdresse);
}
