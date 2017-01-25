/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.cidsmigtest;

/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
import java.util.*;

import de.cismet.cids.custom.beans.lagis.AnlageklasseCustomBean;
import de.cismet.cids.custom.beans.lagis.BaumCustomBean;
import de.cismet.cids.custom.beans.lagis.BaumKategorieAuspraegungCustomBean;
import de.cismet.cids.custom.beans.lagis.BaumKategorieCustomBean;
import de.cismet.cids.custom.beans.lagis.BaumMerkmalCustomBean;
import de.cismet.cids.custom.beans.lagis.BaumNutzungCustomBean;
import de.cismet.cids.custom.beans.lagis.BebauungCustomBean;
import de.cismet.cids.custom.beans.lagis.BeschlussartCustomBean;
import de.cismet.cids.custom.beans.lagis.DmsUrlCustomBean;
import de.cismet.cids.custom.beans.lagis.FarbeCustomBean;
import de.cismet.cids.custom.beans.lagis.FlaechennutzungCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckAktionCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckArtCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckHistorieCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;
import de.cismet.cids.custom.beans.lagis.GemarkungCustomBean;
import de.cismet.cids.custom.beans.lagis.GeomCustomBean;
import de.cismet.cids.custom.beans.lagis.KategorieCustomBean;
import de.cismet.cids.custom.beans.lagis.KostenCustomBean;
import de.cismet.cids.custom.beans.lagis.KostenartCustomBean;
import de.cismet.cids.custom.beans.lagis.MipaCustomBean;
import de.cismet.cids.custom.beans.lagis.MipaKategorieAuspraegungCustomBean;
import de.cismet.cids.custom.beans.lagis.MipaKategorieCustomBean;
import de.cismet.cids.custom.beans.lagis.MipaMerkmalCustomBean;
import de.cismet.cids.custom.beans.lagis.MipaNutzungCustomBean;
import de.cismet.cids.custom.beans.lagis.NutzungBuchungCustomBean;
import de.cismet.cids.custom.beans.lagis.NutzungCustomBean;
import de.cismet.cids.custom.beans.lagis.NutzungsartCustomBean;
import de.cismet.cids.custom.beans.lagis.OberkategorieCustomBean;
import de.cismet.cids.custom.beans.lagis.RebeArtCustomBean;
import de.cismet.cids.custom.beans.lagis.RebeCustomBean;
import de.cismet.cids.custom.beans.lagis.RessortCustomBean;
import de.cismet.cids.custom.beans.lagis.SpielplatzCustomBean;
import de.cismet.cids.custom.beans.lagis.StilCustomBean;
import de.cismet.cids.custom.beans.lagis.UrlBaseCustomBean;
import de.cismet.cids.custom.beans.lagis.UrlCustomBean;
import de.cismet.cids.custom.beans.lagis.VertragCustomBean;
import de.cismet.cids.custom.beans.lagis.VertragsartCustomBean;
import de.cismet.cids.custom.beans.lagis.VerwaltendeDienststelleCustomBean;
import de.cismet.cids.custom.beans.lagis.VerwaltungsbereichCustomBean;
import de.cismet.cids.custom.beans.lagis.VerwaltungsgebrauchCustomBean;

import de.cismet.lagisEE.interfaces.Key;

/*
 * Copyright (C) 2012 cismet GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class EjbObjectsToStringTester extends StandartTypToStringTester {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            EjbObjectsToStringTester.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final GeomCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Anlageklasse |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final AnlageklasseCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Anlageklasse |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Schluessel: " + getStringOf(object.getSchluessel())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final BaumKategorieCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[BaumKategorie |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + t() + "hashCode: " + object.hashCode()
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final BaumMerkmalCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[BaumMerkmal |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + t() + "hashCode: " + object.hashCode()
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final BebauungCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Bebauung |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "PlanArt: " + getStringOf(object.getPlanArt())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final BeschlussartCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Beschlussart |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final FlaechennutzungCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Flaechennutzung |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "PlanArt: " + getStringOf(object.getPlanArt())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final FlurstueckArtCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[FlurstueckArt |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final FlurstueckHistorieCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[FlurstueckHistorie |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Aktion: " + getStringOf(object.getAktion())
                    + "\n" + t() + "Index: " + getStringOf(object.getIndex())
                    + "\n" + t() + "Vorgaenger: " + getStringOf(object.getVorgaenger())
                    + "\n" + t() + "Nachfolger: " + getStringOf(object.getNachfolger())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final FlurstueckAktionCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[FlurstueckAktion |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Beschreibung: " + getStringOf(object.getBeschreibung())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final KostenCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Kosten |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "KostenArt: " + getStringOf(object.getKostenart())
                    + "\n" + t() + "Betrag: " + getStringOf(object.getBetrag())
                    + "\n" + t() + "Datum: " + getStringOf(object.getDatum())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final KostenartCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Kostenart |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bezeichnung" + getStringOf(object.getBezeichnung())
                    + "\n" + t() + "IstNebenkostenart: " + getStringOf(object.getIstNebenkostenart())
                    + "\n" + t() + "isNebenkostenart(): " + getStringOf(object.isNebenkostenart())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final MipaCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[MiPa |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bemerkung: " + getStringOf(object.getBemerkung())
                    + "\n" + t() + "Flaeche: " + getStringOf(object.getFlaeche())
                    + "\n" + t() + "Geometrie: " + getStringOf(object.getGeometrie())
                    + "\n" + t() + "Lage: " + getStringOf(object.getLage())
                    + "\n" + t() + "LaufendeNummer: " + getStringOf(object.getLaufendeNummer())
                    + "\n" + t() + "MiPaMerkmal: " + getStringOf(object.getMiPaMerkmal())
                    + "\n" + t() + "MiPaNutzung: " + getStringOf(object.getMiPaNutzung())
                    + "\n" + t() + "Nutzer: " + getStringOf(object.getNutzer())
                    + "\n" + t() + "Nutzung: " + getStringOf(object.getNutzung())
                    + "\n" + t() + "Vertragsbeginn: " + getStringOf(object.getVertragsbeginn())
                    + "\n" + t() + "Vertragsende: " + getStringOf(object.getVertragsende())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final MipaNutzungCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[MiPaNutzung |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "AusgewaehlteAuspraegung: " + getStringOf(object.getAusgewaehlteAuspraegung())
                    + "\n" + t() + "AusgewaehlteNummer: " + getStringOf(object.getAusgewaehlteNummer())
                    + "\n" + t() + "MiPaKategorie: " + getStringOf(object.getMiPaKategorie())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final MipaKategorieCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[MiPaKategorie |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + t() + "HatNummerAlsAuspraegung: " + getStringOf(object.getHatNummerAlsAuspraegung())
                    + "\n" + t() + "KategorieAuspraegungen: " + getStringOf(object.getKategorieAuspraegungen())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final MipaKategorieAuspraegungCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[MiPaKategorie |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final MipaMerkmalCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[MiPaMerkmal |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final NutzungCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Nutzung |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "BuchungsCount: " + getStringOf(object.getBuchungsCount())
                    + "\n" + t() + "DifferenceToPreviousBuchung: "
                    + getStringOf(object.getDifferenceToPreviousBuchung())
                    + "\n" + t() + "NutzungsBuchungen: " + getStringOf(object.getNutzungsBuchungen())
                    + "\n" + t() + "OpenBuchung: " + getStringOf(object.getOpenBuchung())
                    + "\n" + t() + "PreviousBuchung: " + getStringOf(object.getPreviousBuchung())
                    + "\n" + t() + "TerminalBuchung: " + getStringOf(object.getTerminalBuchung())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final NutzungsartCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Nutzungsart |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + t() + "PrettyString: " + getStringOf(object.getPrettyString())
                    + "\n" + t() + "Schluessel: " + getStringOf(object.getSchluessel())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final NutzungBuchungCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[NutzungsBuchung |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bebauung: " + getStringOf(object.getBebauung())
                    + "\n" + t() + "Bemerkung: " + getStringOf(object.getBemerkung())
                    + "\n" + t() + "Flaeche: " + getStringOf(object.getFlaeche())
                    + "\n" + t() + "Flaechennutzung: " + getStringOf(object.getFlaechennutzung())
                    + "\n" + t() + "Gesamtpreis: " + getStringOf(object.getGesamtpreis())
                    + "\n" + t() + "Gueltigbis: " + getStringOf(object.getGueltigbis())
                    + "\n" + t() + "Gueltigvon: " + getStringOf(object.getGueltigvon())
                    + "\n" + t() + "IstBuchwert: " + getStringOf(object.getIstBuchwert())
                    // + "NutzungCustomBean: " + getStringOf(object.getNutzung())
                    + "\n" + t() + "Nutzungsart: " + getStringOf(object.getNutzungsart())
                    + "\n" + t() + "PrettyString: " + getStringOf(object.getPrettyString())
                    + "\n" + t() + "Quadratmeterpreis: " + getStringOf(object.getQuadratmeterpreis())
                    + "\n" + t() + "SollGeloeschtWerden: " + getStringOf(object.getSollGeloeschtWerden())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final RebeCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[ReBe |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bemerkung: " + getStringOf(object.getBemerkung())
                    + "\n" + t() + "Beschreibung: " + getStringOf(object.getBeschreibung())
                    + "\n" + t() + "DatumEintragung: " + getStringOf(object.getDatumEintragung())
                    + "\n" + t() + "DatumLoeschung: " + getStringOf(object.getDatumLoeschung())
                    + "\n" + t() + "Geometrie: " + getStringOf(object.getGeometrie())
                    + "\n" + t() + "IstRecht: " + getStringOf(object.getIstRecht())
                    + "\n" + t() + "Nummer: " + getStringOf(object.getNummer())
                    + "\n" + t() + "ReBeArt: " + getStringOf(object.getReBeArt())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final RebeArtCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[ReBeArt |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final VertragsartCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Vertragsart |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final VerwaltungsbereichCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Verwaltungsbereich |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Dienststelle: " + getStringOf(object.getDienststelle())
                    + "\n" + t() + "Flaeche: " + getStringOf(object.getFlaeche())
                    + "\n" + t() + "Gebrauch: " + getStringOf(object.getGebrauch())
                    + "\n" + t() + "Geometrie: " + getStringOf(object.getGeometrie())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final VerwaltendeDienststelleCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[VerwaltendeDienststelle |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "AbkuerzungAbteilung: " + getStringOf(object.getAbkuerzungAbteilung())
                    + "\n" + t() + "BezeichnungAbteilung: " + getStringOf(object.getBezeichnungAbteilung())
                    + "\n" + t() + "EmailAdresse: " + getStringOf(object.getEmailAdresse())
                    + "\n" + t() + "Ressort: " + getStringOf(object.getRessort())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final VerwaltungsgebrauchCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Verwaltungsgebrauch |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + t() + "Abkuerzung: " + getStringOf(object.getAbkuerzung())
                    + "\n" + t() + "UnterAbschnitt: " + getStringOf(object.getUnterAbschnitt())
                    + "\n" + t() + "Kategorie: " + getStringOf(object.getKategorie())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final FarbeCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Farbe |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "RgbFarbwert: " + getStringOf(object.getRgbFarbwert())
                    + "\n" + t() + "Stil: " + getStringOf(object.getStil())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final StilCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Stil |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final BaumCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Baum |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "AlteNutzung: " + getStringOf(object.getAlteNutzung())
                    + "\n" + t() + "Auftragnehmer: " + getStringOf(object.getAuftragnehmer())
                    + "\n" + t() + "Baumnummer: " + getStringOf(object.getBaumnummer())
                    + "\n" + t() + "Bemerkung: " + getStringOf(object.getBemerkung())
                    + "\n" + t() + "Lage: " + getStringOf(object.getLage())
                    + "\n" + t() + "BaumMerkmal: " + getStringOf(object.getBaumMerkmal())
                    + "\n" + t() + "BaumNutzung: " + getStringOf(object.getBaumNutzung())
                    + "\n" + t() + "Erfassungsdatum: " + getStringOf(object.getErfassungsdatum())
                    + "\n" + t() + "Faelldatum: " + getStringOf(object.getFaelldatum())
                    + "\n" + t() + "Flaeche: " + getStringOf(object.getFlaeche())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final BaumNutzungCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[BaumNutzung |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "BaumKategorie: " + getStringOf(object.getBaumKategorie())
                    + "\n" + t() + "AusgewaehlteAuspraegung: " + getStringOf(object.getAusgewaehlteAuspraegung())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final BaumKategorieAuspraegungCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[BaumKategorieAuspraegung |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final SpielplatzCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Spielplatz |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "isKlettergeruestVorhanden: " + getStringOf(object.isKlettergeruestVorhanden())
                    + "\n" + t() + "isKlettergeruestWartungErforderlich: "
                    + getStringOf(object.isKlettergeruestWartungErforderlich())
                    + "\n" + t() + "isRutscheVorhanden: " + getStringOf(object.isRutscheVorhanden())
                    + "\n" + t() + "isRutscheWartungErforderlich: " + getStringOf(object.isRutscheWartungErforderlich())
                    + "\n" + t() + "isSandkastenVorhanden: " + getStringOf(object.isSandkastenVorhanden())
                    + "\n" + t() + "isSandkastenWartungErforderlich: "
                    + getStringOf(object.isSandkastenWartungErforderlich())
                    + "\n" + t() + "isSchaukelVorhanden: " + getStringOf(object.isSchaukelVorhanden())
                    + "\n" + t() + "isSchaukelWartungErforderlich: "
                    + getStringOf(object.isSchaukelWartungErforderlich())
                    + "\n" + t() + "isWippeVorhanden: " + getStringOf(object.isWippeVorhanden())
                    + "\n" + t() + "isWippeWartungErforderlich: " + getStringOf(object.isWippeWartungErforderlich())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final FlurstueckCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Flurstueck |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bemerkung: " + getStringOf(object.getBemerkung())
                    + "\n" + t() + "Dokumente: " + getStringOf(object.getDokumente())
                    + "\n" + t() + "FlurstueckSchluessel: " + getStringOf(object.getFlurstueckSchluessel())
                    + "\n" + t() + "Nutzungen: " + getStringOf(object.getNutzungen())
                    + "\n" + t() + "RechteUndBelastungen: " + getStringOf(object.getRechteUndBelastungen())
                    + "\n" + t() + "Spielplatz: " + getStringOf(object.getSpielplatz())
                    + "\n" + t() + "Verwaltungsbereiche: " + getStringOf(object.getVerwaltungsbereiche())
                    + "\n" + t() + "Baeume: " + getStringOf(object.getBaeume())
                    + "\n" + t() + "MiPas: " + getStringOf(object.getMiPas())
                    + "\n" + t() + "Vertraege: " + getStringOf(object.getVertraege())
                    + "\n" + t() + "BaeumeQuerverweise: " + getStringOf(object.getBaeumeQuerverweise())
                    + "\n" + t() + "MiPasQuerverweise: " + getStringOf(object.getMiPasQuerverweise())
                    + "\n" + t() + "VertraegeQuerverweise: " + getStringOf(object.getVertraegeQuerverweise())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final FlurstueckSchluesselCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[FlurstueckSchluessel |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "BemerkungSperre: " + getStringOf(object.getBemerkungSperre())
                    + "\n" + t() + "KeyString: " + getStringOf(object.getKeyString())
                    + "\n" + t() + "Letzter_bearbeiter: " + getStringOf(object.getLetzter_bearbeiter())
                    + "\n" + t() + "DatumLetzterStadtbesitz: " + getStringOf(object.getDatumLetzterStadtbesitz())
                    + "\n" + t() + "EntstehungsDatum: " + getStringOf(object.getEntstehungsDatum())
                    + "\n" + t() + "Flur: " + getStringOf(object.getFlur())
                    + "\n" + t() + "FlurstueckArt: " + getStringOf(object.getFlurstueckArt())
                    + "\n" + t() + "FlurstueckNenner: " + getStringOf(object.getFlurstueckNenner())
                    + "\n" + t() + "FlurstueckZaehler: " + getStringOf(object.getFlurstueckZaehler())
                    + "\n" + t() + "Gemarkung: " + getStringOf(object.getGemarkung())
                    + "\n" + t() + "GueltigBis: " + getStringOf(object.getGueltigBis())
                    + "\n" + t() + "IstGesperrt: " + getStringOf(object.getIstGesperrt())
                    + "\n" + t() + "Letzte_bearbeitung: " + getStringOf(object.getLetzte_bearbeitung())
                    + "\n" + t() + "WarStaedtisch: " + getStringOf(object.getWarStaedtisch())
                    + "\n" + t() + "isEchterSchluessel: " + getStringOf(object.isEchterSchluessel())
                    + "\n" + t() + "isGesperrt: " + getStringOf(object.isGesperrt())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final OberkategorieCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Oberkategorie |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Abkuerzung: " + getStringOf(object.getAbkuerzung())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final KategorieCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Kategorie |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Abkuerzung: " + getStringOf(object.getAbkuerzung())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + t() + "Oberkategorie: " + getStringOf(object.getOberkategorie())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final RessortCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Ressort |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Abkuerzung: " + getStringOf(object.getAbkuerzung())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   objects  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final Set objects) {
        final StringBuilder sb = new StringBuilder("\n" + tab() + "[Collection |");
        if ((objects != null) && !objects.isEmpty()) {
            final Object object = objects.toArray()[0];
            if (object instanceof BaumMerkmalCustomBean) {
                final List<BaumMerkmalCustomBean> sortedList = new ArrayList<BaumMerkmalCustomBean>();
                Collections.addAll(sortedList, (BaumMerkmalCustomBean[])objects.toArray(new BaumMerkmalCustomBean[0]));
                Collections.sort(sortedList, new Comparator<BaumMerkmalCustomBean>() {

                        @Override
                        public int compare(final BaumMerkmalCustomBean o1, final BaumMerkmalCustomBean o2) {
                            return (int)(o1.getId() - o2.getId());
                        }
                    });
                for (final Object item : sortedList) {
                    sb.append(getStringOf(item)).append("\n");
                }
            } else if (object instanceof BaumKategorieCustomBean) {
                final List<BaumKategorieCustomBean> sortedList = new ArrayList<BaumKategorieCustomBean>();
                Collections.addAll(
                    sortedList,
                    (BaumKategorieCustomBean[])objects.toArray(new BaumKategorieCustomBean[0]));
                Collections.sort(sortedList, new Comparator<BaumKategorieCustomBean>() {

                        @Override
                        public int compare(final BaumKategorieCustomBean o1, final BaumKategorieCustomBean o2) {
                            return (int)(o1.getId() - o2.getId());
                        }
                    });
                for (final Object item : sortedList) {
                    sb.append(getStringOf(item)).append("\n");
                }
            } else if (object instanceof BaumNutzungCustomBean) {
                final List<BaumNutzungCustomBean> sortedList = new ArrayList<BaumNutzungCustomBean>();
                Collections.addAll(sortedList, (BaumNutzungCustomBean[])objects.toArray(new BaumNutzungCustomBean[0]));
                Collections.sort(sortedList, new Comparator<BaumNutzungCustomBean>() {

                        @Override
                        public int compare(final BaumNutzungCustomBean o1, final BaumNutzungCustomBean o2) {
                            return (int)(o1.getId() - o2.getId());
                        }
                    });
                for (final Object item : sortedList) {
                    sb.append(getStringOf(item)).append("\n");
                }
            } else {
                sb.append("??? ").append(objects.getClass().getCanonicalName());
            }
        }
        sb.append("\n").append(untab()).append("]");
        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final Key object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Key |"
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final VertragCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Vertrag |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Aktenzeichen: " + getStringOf(object.getAktenzeichen())
                    + "\n" + t() + "Bemerkung: " + getStringOf(object.getBemerkung())
                    + "\n" + t() + "Vertragspartner: " + getStringOf(object.getVertragspartner())
                    + "\n" + t() + "Beschluesse: " + getStringOf(object.getBeschluesse())
                    + "\n" + t() + "DatumAuflassung: " + getStringOf(object.getDatumAuflassung())
                    + "\n" + t() + "DatumEintragung: " + getStringOf(object.getDatumEintragung())
                    + "\n" + t() + "Gesamtpreis: " + getStringOf(object.getGesamtpreis())
                    + "\n" + t() + "Kosten: " + getStringOf(object.getKosten())
                    + "\n" + t() + "Quadratmeterpreis: " + getStringOf(object.getQuadratmeterpreis())
                    + "\n" + t() + "Vertragsart: " + getStringOf(object.getVertragsart())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final GemarkungCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Vertrag |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Bezeichnung: " + getStringOf(object.getBezeichnung())
                    + "\n" + t() + "Schluessel: " + getStringOf(object.getSchluessel())
                    + "\n" + t() + "toString: " + getStringOf(object.toString())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final DmsUrlCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[DmsUrl |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Beschreibung: " + getStringOf(object.getBeschreibung())
                    + "\n" + t() + "Name: " + getStringOf(object.getName())
                    + "\n" + t() + "Type: " + getStringOf(object.getTyp())
                    + "\n" + t() + "Url: " + getStringOf(object.getUrl())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final UrlCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[Url |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "Objektname: " + getStringOf(object.getObjektname())
                    + "\n" + t() + "UrlBase: " + getStringOf(object.getUrlBase())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final UrlBaseCustomBean object) {
        if (object == null) {
            return "null";
        }
        return "\n" + tab() + "[UrlBase |"
                    + "\n" + t() + "id: " + getStringOf(object.getId())
                    + "\n" + t() + "ProtPrefix: " + getStringOf(object.getProtPrefix())
                    + "\n" + t() + "Server: " + getStringOf(object.getServer())
                    + "\n" + t() + "Pfad: " + getStringOf(object.getPfad())
                    + "\n" + untab() + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   object  string DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final Object object) {
        String string = StandartTypToStringTester.getStringOf(object);
        if (object != null) {
            if (object instanceof AnlageklasseCustomBean) {
                string = getStringOf((AnlageklasseCustomBean)object);
            } else if (object instanceof BaumKategorieCustomBean) {
                string = getStringOf((BaumKategorieCustomBean)object);
            } else if (object instanceof BaumMerkmalCustomBean) {
                string = getStringOf((BaumMerkmalCustomBean)object);
            } else if (object instanceof BebauungCustomBean) {
                string = getStringOf((BebauungCustomBean)object);
            } else if (object instanceof BeschlussartCustomBean) {
                string = getStringOf((BeschlussartCustomBean)object);
            } else if (object instanceof FlaechennutzungCustomBean) {
                string = getStringOf((FlaechennutzungCustomBean)object);
            } else if (object instanceof FlurstueckArtCustomBean) {
                string = getStringOf((FlurstueckArtCustomBean)object);
            } else if (object instanceof FlurstueckHistorieCustomBean) {
                string = getStringOf((FlurstueckHistorieCustomBean)object);
            } else if (object instanceof FlurstueckAktionCustomBean) {
                string = getStringOf((FlurstueckAktionCustomBean)object);
            } else if (object instanceof KostenCustomBean) {
                string = getStringOf((KostenCustomBean)object);
            } else if (object instanceof KostenartCustomBean) {
                string = getStringOf((KostenartCustomBean)object);
            } else if (object instanceof MipaCustomBean) {
                string = getStringOf((MipaCustomBean)object);
            } else if (object instanceof MipaNutzungCustomBean) {
                string = getStringOf((MipaNutzungCustomBean)object);
            } else if (object instanceof MipaKategorieCustomBean) {
                string = getStringOf((MipaKategorieCustomBean)object);
            } else if (object instanceof MipaKategorieAuspraegungCustomBean) {
                string = getStringOf((MipaKategorieAuspraegungCustomBean)object);
            } else if (object instanceof MipaMerkmalCustomBean) {
                string = getStringOf((MipaMerkmalCustomBean)object);
            } else if (object instanceof NutzungCustomBean) {
                string = getStringOf((NutzungCustomBean)object);
            } else if (object instanceof NutzungsartCustomBean) {
                string = getStringOf((NutzungsartCustomBean)object);
            } else if (object instanceof NutzungBuchungCustomBean) {
                string = getStringOf((NutzungBuchungCustomBean)object);
            } else if (object instanceof RebeCustomBean) {
                string = getStringOf((RebeCustomBean)object);
            } else if (object instanceof RebeArtCustomBean) {
                string = getStringOf((RebeArtCustomBean)object);
            } else if (object instanceof VertragsartCustomBean) {
                string = getStringOf((VertragsartCustomBean)object);
            } else if (object instanceof VerwaltungsbereichCustomBean) {
                string = getStringOf((VerwaltungsbereichCustomBean)object);
            } else if (object instanceof VerwaltendeDienststelleCustomBean) {
                string = getStringOf((VerwaltendeDienststelleCustomBean)object);
            } else if (object instanceof VerwaltungsgebrauchCustomBean) {
                string = getStringOf((VerwaltungsgebrauchCustomBean)object);
            } else if (object instanceof BaumCustomBean) {
                string = getStringOf((BaumCustomBean)object);
            } else if (object instanceof BaumNutzungCustomBean) {
                string = getStringOf((BaumNutzungCustomBean)object);
            } else if (object instanceof BaumKategorieAuspraegungCustomBean) {
                string = getStringOf((BaumKategorieAuspraegungCustomBean)object);
            } else if (object instanceof SpielplatzCustomBean) {
                string = getStringOf((SpielplatzCustomBean)object);
            } else if (object instanceof FlurstueckCustomBean) {
                string = getStringOf((FlurstueckCustomBean)object);
            } else if (object instanceof FlurstueckSchluesselCustomBean) {
                string = getStringOf((FlurstueckSchluesselCustomBean)object);
            } else if (object instanceof Key) {
                string = getStringOf((Key)object);
            } else if (object instanceof VertragCustomBean) {
                string = getStringOf((VertragCustomBean)object);
            } else if (object instanceof GemarkungCustomBean) {
                string = getStringOf((GemarkungCustomBean)object);
            } else if (object instanceof FarbeCustomBean) {
                string = getStringOf((FarbeCustomBean)object);
            } else if (object instanceof DmsUrlCustomBean) {
                string = getStringOf((DmsUrlCustomBean)object);
            } else if (object instanceof UrlCustomBean) {
                string = getStringOf((UrlCustomBean)object);
            } else if (object instanceof UrlBaseCustomBean) {
                string = getStringOf((UrlBaseCustomBean)object);
            } else if (object instanceof GeomCustomBean) {
                string = getStringOf((GeomCustomBean)object);
            } else if (object instanceof Set) {
                string = getStringOf((Set)object);
            }
        }
        return string;
    }
}
