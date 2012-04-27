/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagisEE.entity.core;

import java.util.*;

import de.cismet.cids.custom.beans.lagis.AnlageklasseCustomBean;
import de.cismet.cids.custom.beans.lagis.BebauungCustomBean;
import de.cismet.cids.custom.beans.lagis.FlaechennutzungCustomBean;
import de.cismet.cids.custom.beans.lagis.NutzungBuchungCustomBean;
import de.cismet.cids.custom.beans.lagis.NutzungCustomBean;
import de.cismet.cids.custom.beans.lagis.NutzungsartCustomBean;

import de.cismet.lagis.Exception.BuchungNotInNutzungException;
import de.cismet.lagis.Exception.IllegalNutzungStateException;

import de.cismet.lagisEE.interfaces.Equalator;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface NutzungsBuchung extends Cloneable {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static enum NUTZUNG_BUCHUNG_FIELDS {

        //~ Enum constants -----------------------------------------------------

        ANLAGEKLASSE, NUTZUNG, NUTZUNGSART, FLAECHE, GUELTIG_BIS, QUADRADMETERPREIS, FLAECHENNUTZUNG, BEBAUUNG,
        BEMERKUNG
    }

    //~ Instance fields --------------------------------------------------------

    NutzungsHistoryEqualator NUTZUNG_HISTORY_EQUALATOR = new NutzungsHistoryEqualator();

    Comparator<NutzungBuchungCustomBean> DATE_COMPARATOR = new Comparator<NutzungBuchungCustomBean>() {

            @Override
            public int compare(final NutzungBuchungCustomBean n1, final NutzungBuchungCustomBean n2) {
                if ((n1.getGueltigbis() == null) && (n2.getGueltigbis() == null)) {
                    return 0;
                } else if (n2.getGueltigbis() == null) {
                    return -1;
                } else if (n1.getGueltigbis() == null) {
                    return 1;
                } else {
                    return n1.getGueltigbis().compareTo(n2.getGueltigbis());
                }
            }
        };

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
    String getBemerkung();

    /**
     * DOCUMENT ME!
     *
     * @param  bemerkung  DOCUMENT ME!
     */
    void setBemerkung(final String bemerkung);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    AnlageklasseCustomBean getAnlageklasse();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setAnlageklasse(final AnlageklasseCustomBean val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    NutzungsartCustomBean getNutzungsart();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setNutzungsart(final NutzungsartCustomBean val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Integer getFlaeche();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setFlaeche(final Integer val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Double getQuadratmeterpreis();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setQuadratmeterpreis(final Double val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getGueltigbis();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setGueltigbis(final Date val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Date getGueltigvon();

    /**
     * DOCUMENT ME!
     *
     * @param  val  DOCUMENT ME!
     */
    void setGueltigvon(final Date val);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<FlaechennutzungCustomBean> getFlaechennutzung();

    /**
     * DOCUMENT ME!
     *
     * @param  flaechennutzung  DOCUMENT ME!
     */
    void setFlaechennutzung(final Collection<FlaechennutzungCustomBean> flaechennutzung);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<BebauungCustomBean> getBebauung();

    /**
     * DOCUMENT ME!
     *
     * @param  bebauung  DOCUMENT ME!
     */
    void setBebauung(final Collection<BebauungCustomBean> bebauung);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Double getGesamtpreis();

    /**
     * DOCUMENT ME!
     *
     * @throws  IllegalNutzungStateException  DOCUMENT ME!
     * @throws  BuchungNotInNutzungException  DOCUMENT ME!
     */
    void flipBuchungsBuchwert() throws IllegalNutzungStateException, BuchungNotInNutzungException;

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isBuchwertFlippable();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getPrettyString();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    NutzungBuchungCustomBean cloneBuchung();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean getIstBuchwert();

    /**
     * DOCUMENT ME!
     *
     * @param  istBuchwert  DOCUMENT ME!
     */
    void setIstBuchwert(final boolean istBuchwert);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean getSollGeloeschtWerden();

    /**
     * DOCUMENT ME!
     *
     * @param  sollGeloeschtWerden  DOCUMENT ME!
     */
    void setSollGeloeschtWerden(final boolean sollGeloeschtWerden);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    NutzungCustomBean getNutzung();

    /**
     * DOCUMENT ME!
     *
     * @param  nutzung  DOCUMENT ME!
     */
    void setNutzung(final NutzungCustomBean nutzung);

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class NutzungsHistoryEqualator implements Equalator<NutzungBuchungCustomBean> {

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean pedanticEquals(final NutzungBuchungCustomBean n1, final NutzungBuchungCustomBean n2) {
            if (!determineUnequalFields(n1, n2).isEmpty()) {
                return false;
            } else {
                return true;
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param   n1  DOCUMENT ME!
         * @param   n2  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Collection<NUTZUNG_BUCHUNG_FIELDS> determineUnequalFields(final NutzungBuchungCustomBean n1,
                final NutzungBuchungCustomBean n2) {
            final Collection<NUTZUNG_BUCHUNG_FIELDS> result = new HashSet<NUTZUNG_BUCHUNG_FIELDS>();
            if ((n1 != null) && (n2 != null)) {
                if (!((((n1.getAnlageklasse() != null) && (n2.getAnlageklasse() != null)
                                        && n1.getAnlageklasse().equals(n2.getAnlageklasse()))
                                    || ((n1.getAnlageklasse() == null) && (n2.getAnlageklasse() == null))))) {
                    result.add(NUTZUNG_BUCHUNG_FIELDS.ANLAGEKLASSE);
                }
                if (!((((n1.getNutzungsart() != null) && (n2.getNutzungsart() != null)
                                        && n1.getNutzungsart().equals(n2.getNutzungsart()))
                                    || ((n1.getNutzungsart() == null) && (n2.getNutzungsart() == null))))) {
                    result.add(NUTZUNG_BUCHUNG_FIELDS.NUTZUNGSART);
                }
                if (!((((n1.getNutzung() != null) && (n2.getNutzung() != null)
                                        && n1.getNutzung().equals(n2.getNutzung()))
                                    || ((n1.getNutzung() == null) && (n2.getNutzung() == null))))) {
                    result.add(NUTZUNG_BUCHUNG_FIELDS.NUTZUNG);
                }
                if (!((((n1.getFlaeche() != null) && (n2.getFlaeche() != null)
                                        && n1.getFlaeche().equals(n2.getFlaeche()))
                                    || ((n1.getFlaeche() == null) && (n2.getFlaeche() == null))))) {
                    result.add(NUTZUNG_BUCHUNG_FIELDS.FLAECHE);
                }

                if (!((((n1.getQuadratmeterpreis() != null) && (n2.getQuadratmeterpreis() != null)
                                        && n1.getQuadratmeterpreis().equals(n2.getQuadratmeterpreis()))
                                    || ((n1.getQuadratmeterpreis() == null) && (n2.getQuadratmeterpreis() == null))))) {
                    result.add(NUTZUNG_BUCHUNG_FIELDS.QUADRADMETERPREIS);
                }
                if (!((((n1.getBemerkung() != null) && (n2.getBemerkung() != null)
                                        && n1.getBemerkung().equals(n2.getBemerkung()))
                                    || ((n1.getBemerkung() == null) && (n2.getBemerkung() == null))))) {
                    result.add(NUTZUNG_BUCHUNG_FIELDS.BEMERKUNG);
                }
                if (result.size() > 0) {
                    System.out.println("Nutzung pedanticEquals(): Einfache Felder sind unterschiedlich");
                } else {
                    System.out.println("Nutzung pedanticEquals(): Alle einfachen Felder sind gleich");
                }
                if (!(((n1.getFlaechennutzung() != null) && (n2.getFlaechennutzung() != null)
                                    && n1.getFlaechennutzung().equals(n2.getFlaechennutzung()))
                                || ((n1.getFlaechennutzung() == null) && (n2.getFlaechennutzung() == null)))) {
                    result.add(NUTZUNG_BUCHUNG_FIELDS.FLAECHENNUTZUNG);
                }
                if (!(((n1.getBebauung() != null) && (n2.getBebauung() != null)
                                    && n1.getBebauung().equals(n2.getBebauung()))
                                || ((n1.getBebauung() == null) && (n2.getBebauung() == null)))) {
                    result.add(NUTZUNG_BUCHUNG_FIELDS.BEBAUUNG);
                }
                if (result.size() == 0) {
                    System.out.println("Nutzung pedanticEquals(): Nutzungen sind gleich");
                } else {
                    System.out.println("Nutzung pedanticEquals(): Nutzungen sind ungleich: "
                                + Arrays.deepToString(result.toArray()));
                }
            } else if (n1 != null) {
                result.addAll(checkNotZeroFields(n1));
            } else if (n2 != null) {
                result.addAll(checkNotZeroFields(n2));
            }
            // else is implicit because the field will be zero and therefore equals !
            return result;
        }

        /**
         * DOCUMENT ME!
         *
         * @param   buchung  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private Collection<NUTZUNG_BUCHUNG_FIELDS> checkNotZeroFields(final NutzungsBuchung buchung) {
            final Collection<NUTZUNG_BUCHUNG_FIELDS> result = new HashSet<NUTZUNG_BUCHUNG_FIELDS>();
            if (buchung != null) {
                if (buchung.getAnlageklasse() != null) {
                    result.add(NUTZUNG_BUCHUNG_FIELDS.ANLAGEKLASSE);
                }
                if (buchung.getNutzung() != null) {
                    result.add(NUTZUNG_BUCHUNG_FIELDS.NUTZUNG);
                }
                if (buchung.getNutzungsart() != null) {
                    result.add(NUTZUNG_BUCHUNG_FIELDS.NUTZUNGSART);
                }
                if (buchung.getFlaeche() != null) {
                    result.add(NUTZUNG_BUCHUNG_FIELDS.FLAECHE);
                }
                if (buchung.getQuadratmeterpreis() != null) {
                    result.add(NUTZUNG_BUCHUNG_FIELDS.QUADRADMETERPREIS);
                }
                if ((buchung.getFlaechennutzung() != null) && (buchung.getFlaechennutzung().size() > 0)) {
                    result.add(NUTZUNG_BUCHUNG_FIELDS.FLAECHENNUTZUNG);
                }
                if ((buchung.getBebauung() != null) && (buchung.getBebauung().size() > 0)) {
                    result.add(NUTZUNG_BUCHUNG_FIELDS.BEBAUUNG);
                }
            }
            return result;
        }
    }
}
