/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * Sperre.java
 *
 * Created on 30. April 2007, 16:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagisEE.entity.locking;

import java.util.Date;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface Sperre {

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
    Integer getFlurstueckSchluessel();

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueckSchluessel  DOCUMENT ME!
     */
    void setFlurstueckSchluessel(final Integer flurstueckSchluessel);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getBenutzerkonto();

    /**
     * DOCUMENT ME!
     *
     * @param  sperrenBesitzer  DOCUMENT ME!
     */
    void setBenutzerkonto(final String sperrenBesitzer);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getInformationen();

    /**
     * DOCUMENT ME!
     *
     * @param  informationen  DOCUMENT ME!
     */
    void setInformationen(final String informationen);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getZeitstempel();

    /**
     * DOCUMENT ME!
     *
     * @param  zeitstempel  DOCUMENT ME!
     */
    void setZeitstempel(final Date zeitstempel);
}
