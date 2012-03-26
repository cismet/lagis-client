/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.EJBrokerInterfaces;

import java.io.Serializable;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface Plan extends Serializable {

    //~ Instance fields --------------------------------------------------------

    int PLAN_FLAECHENNUTZUNG = 1;
    int PLAN_BEBAUUNG = 2;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    int getPlanArt();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getBezeichnung();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setBezeichnung(String val);
}
