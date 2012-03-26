/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * Spielplatz.java
 *
 * Created on 20. November 2007, 16:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.EJBrokerInterfaces;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface Spielplatz {

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
    boolean isKlettergeruestVorhanden();

    /**
     * DOCUMENT ME!
     *
     * @param  klettergeruestVorhanden  DOCUMENT ME!
     */
    void setKlettergeruestVorhanden(final boolean klettergeruestVorhanden);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isKlettergeruestWartungErforderlich();

    /**
     * DOCUMENT ME!
     *
     * @param  klettergeruestWartungErforderlich  DOCUMENT ME!
     */
    void setKlettergeruestWartungErforderlich(final boolean klettergeruestWartungErforderlich);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isRutscheVorhanden();

    /**
     * DOCUMENT ME!
     *
     * @param  rutscheVorhanden  DOCUMENT ME!
     */
    void setRutscheVorhanden(final boolean rutscheVorhanden);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isRutscheWartungErforderlich();

    /**
     * DOCUMENT ME!
     *
     * @param  rutscheWartungErforderlich  DOCUMENT ME!
     */
    void setRutscheWartungErforderlich(final boolean rutscheWartungErforderlich);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isSandkastenVorhanden();

    /**
     * DOCUMENT ME!
     *
     * @param  sandkastenVorhanden  DOCUMENT ME!
     */
    void setSandkastenVorhanden(final boolean sandkastenVorhanden);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isSandkastenWartungErforderlich();

    /**
     * DOCUMENT ME!
     *
     * @param  sandkastenWartungErforderlich  DOCUMENT ME!
     */
    void setSandkastenWartungErforderlich(final boolean sandkastenWartungErforderlich);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isSchaukelVorhanden();

    /**
     * DOCUMENT ME!
     *
     * @param  schaukelVorhanden  DOCUMENT ME!
     */
    void setSchaukelVorhanden(final boolean schaukelVorhanden);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isSchaukelWartungErforderlich();

    /**
     * DOCUMENT ME!
     *
     * @param  schaukelWartungErforderlich  DOCUMENT ME!
     */
    void setSchaukelWartungErforderlich(final boolean schaukelWartungErforderlich);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isWippeVorhanden();

    /**
     * DOCUMENT ME!
     *
     * @param  wippeVorhanden  DOCUMENT ME!
     */
    void setWippeVorhanden(final boolean wippeVorhanden);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isWippeWartungErforderlich();

    /**
     * DOCUMENT ME!
     *
     * @param  wippeWartungErforderlich  DOCUMENT ME!
     */
    void setWippeWartungErforderlich(final boolean wippeWartungErforderlich);
}
