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
import Sirius.server.middleware.types.MetaObject;

import org.apache.commons.lang.StringUtils;

import java.util.*;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.core.Geom;
import de.cismet.lagisEE.entity.core.Kosten;
import de.cismet.lagisEE.entity.core.Nutzung;
import de.cismet.lagisEE.entity.core.NutzungsBuchung;
import de.cismet.lagisEE.entity.core.ReBe;
import de.cismet.lagisEE.entity.core.Url;
import de.cismet.lagisEE.entity.core.UrlBase;
import de.cismet.lagisEE.entity.core.Vertrag;
import de.cismet.lagisEE.entity.core.Verwaltungsbereich;
import de.cismet.lagisEE.entity.core.hardwired.Anlageklasse;
import de.cismet.lagisEE.entity.core.hardwired.Bebauung;
import de.cismet.lagisEE.entity.core.hardwired.Beschlussart;
import de.cismet.lagisEE.entity.core.hardwired.DmsUrl;
import de.cismet.lagisEE.entity.core.hardwired.Farbe;
import de.cismet.lagisEE.entity.core.hardwired.Flaechennutzung;
import de.cismet.lagisEE.entity.core.hardwired.FlurstueckArt;
import de.cismet.lagisEE.entity.core.hardwired.Gemarkung;
import de.cismet.lagisEE.entity.core.hardwired.Kategorie;
import de.cismet.lagisEE.entity.core.hardwired.Kostenart;
import de.cismet.lagisEE.entity.core.hardwired.Nutzungsart;
import de.cismet.lagisEE.entity.core.hardwired.Oberkategorie;
import de.cismet.lagisEE.entity.core.hardwired.ReBeArt;
import de.cismet.lagisEE.entity.core.hardwired.Ressort;
import de.cismet.lagisEE.entity.core.hardwired.Stil;
import de.cismet.lagisEE.entity.core.hardwired.Vertragsart;
import de.cismet.lagisEE.entity.core.hardwired.VerwaltendeDienststelle;
import de.cismet.lagisEE.entity.core.hardwired.Verwaltungsgebrauch;
import de.cismet.lagisEE.entity.extension.baum.Baum;
import de.cismet.lagisEE.entity.extension.baum.BaumKategorie;
import de.cismet.lagisEE.entity.extension.baum.BaumKategorieAuspraegung;
import de.cismet.lagisEE.entity.extension.baum.BaumMerkmal;
import de.cismet.lagisEE.entity.extension.baum.BaumNutzung;
import de.cismet.lagisEE.entity.extension.spielplatz.Spielplatz;
import de.cismet.lagisEE.entity.extension.vermietung.MiPa;
import de.cismet.lagisEE.entity.extension.vermietung.MiPaKategorie;
import de.cismet.lagisEE.entity.extension.vermietung.MiPaKategorieAuspraegung;
import de.cismet.lagisEE.entity.extension.vermietung.MiPaMerkmal;
import de.cismet.lagisEE.entity.extension.vermietung.MiPaNutzung;
import de.cismet.lagisEE.entity.history.FlurstueckAktion;
import de.cismet.lagisEE.entity.history.FlurstueckHistorie;

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
public class CustomBeanToStringTester extends StandartTypToStringTester {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            CustomBeanToStringTester.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final Geom object) {
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
    public static String getStringOf(final Anlageklasse object) {
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
    public static String getStringOf(final BaumKategorie object) {
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
    public static String getStringOf(final BaumMerkmal object) {
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
    public static String getStringOf(final Bebauung object) {
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
    public static String getStringOf(final Beschlussart object) {
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
    public static String getStringOf(final Flaechennutzung object) {
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
    public static String getStringOf(final FlurstueckArt object) {
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
    public static String getStringOf(final FlurstueckHistorie object) {
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
    public static String getStringOf(final FlurstueckAktion object) {
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
    public static String getStringOf(final Kosten object) {
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
    public static String getStringOf(final Kostenart object) {
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
    public static String getStringOf(final MiPa object) {
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
    public static String getStringOf(final MiPaNutzung object) {
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
    public static String getStringOf(final MiPaKategorie object) {
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
    public static String getStringOf(final MiPaKategorieAuspraegung object) {
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
    public static String getStringOf(final MiPaMerkmal object) {
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
    public static String getStringOf(final Nutzung object) {
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
    public static String getStringOf(final Nutzungsart object) {
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
    public static String getStringOf(final NutzungsBuchung object) {
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
                    // + "Nutzung: " + getStringOf(object.getNutzung())
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
    public static String getStringOf(final ReBe object) {
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
    public static String getStringOf(final ReBeArt object) {
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
    public static String getStringOf(final Vertragsart object) {
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
    public static String getStringOf(final Verwaltungsbereich object) {
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
    public static String getStringOf(final VerwaltendeDienststelle object) {
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
    public static String getStringOf(final Verwaltungsgebrauch object) {
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
    public static String getStringOf(final Farbe object) {
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
    public static String getStringOf(final Stil object) {
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
    public static String getStringOf(final Baum object) {
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
    public static String getStringOf(final BaumNutzung object) {
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
    public static String getStringOf(final BaumKategorieAuspraegung object) {
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
    public static String getStringOf(final Spielplatz object) {
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
    public static String getStringOf(final Flurstueck object) {
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
    public static String getStringOf(final FlurstueckSchluessel object) {
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
    public static String getStringOf(final Oberkategorie object) {
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
    public static String getStringOf(final Kategorie object) {
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
    public static String getStringOf(final Ressort object) {
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
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringOf(final Collection<? extends CidsBean> object) {
        final StringBuilder sb = new StringBuilder("\n" + tab() + "[Collection |");
        if (object != null) {
            final List<CidsBean> sortedList = new ArrayList<CidsBean>();
            Collections.addAll(sortedList, object.toArray(new CidsBean[0]));
            Collections.sort(sortedList, new Comparator<CidsBean>() {

                    @Override
                    public int compare(final CidsBean o1, final CidsBean o2) {
                        return o1.getMetaObject().getId() - o2.getMetaObject().getId();
                    }
                });
            for (final CidsBean item : sortedList) {
                sb.append(getStringOf(item)).append("\n");
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
    public static String getStringOf(final MetaObject[] object) {
        final StringBuilder sb = new StringBuilder("\n" + tab() + "[Collection |");
        if (object != null) {
            final List<MetaObject> sortedList = new ArrayList<MetaObject>();
            Collections.addAll(sortedList, object);
            Collections.sort(sortedList, new Comparator<MetaObject>() {

                    @Override
                    public int compare(final MetaObject o1, final MetaObject o2) {
                        return o1.getId() - o2.getId();
                    }
                });
            for (final MetaObject item : sortedList) {
                sb.append(getStringOf(item.getBean())).append("\n");
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
    public static String getStringOf(final Vertrag object) {
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
    public static String getStringOf(final Gemarkung object) {
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
    public static String getStringOf(final DmsUrl object) {
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
    public static String getStringOf(final Url object) {
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
    public static String getStringOf(final UrlBase object) {
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
            if (object instanceof Anlageklasse) {
                string = getStringOf((Anlageklasse)object);
            } else if (object instanceof BaumKategorie) {
                string = getStringOf((BaumKategorie)object);
            } else if (object instanceof BaumMerkmal) {
                string = getStringOf((BaumMerkmal)object);
            } else if (object instanceof Bebauung) {
                string = getStringOf((Bebauung)object);
            } else if (object instanceof Beschlussart) {
                string = getStringOf((Beschlussart)object);
            } else if (object instanceof Flaechennutzung) {
                string = getStringOf((Flaechennutzung)object);
            } else if (object instanceof FlurstueckArt) {
                string = getStringOf((FlurstueckArt)object);
            } else if (object instanceof FlurstueckHistorie) {
                string = getStringOf((FlurstueckHistorie)object);
            } else if (object instanceof FlurstueckAktion) {
                string = getStringOf((FlurstueckAktion)object);
            } else if (object instanceof Kosten) {
                string = getStringOf((Kosten)object);
            } else if (object instanceof Kostenart) {
                string = getStringOf((Kostenart)object);
            } else if (object instanceof MiPa) {
                string = getStringOf((MiPa)object);
            } else if (object instanceof MiPaNutzung) {
                string = getStringOf((MiPaNutzung)object);
            } else if (object instanceof MiPaKategorie) {
                string = getStringOf((MiPaKategorie)object);
            } else if (object instanceof MiPaKategorieAuspraegung) {
                string = getStringOf((MiPaKategorieAuspraegung)object);
            } else if (object instanceof MiPaMerkmal) {
                string = getStringOf((MiPaMerkmal)object);
            } else if (object instanceof Nutzung) {
                string = getStringOf((Nutzung)object);
            } else if (object instanceof Nutzung) {
                string = getStringOf((Nutzung)object);
            } else if (object instanceof Nutzungsart) {
                string = getStringOf((Nutzungsart)object);
            } else if (object instanceof NutzungsBuchung) {
                string = getStringOf((NutzungsBuchung)object);
            } else if (object instanceof ReBe) {
                string = getStringOf((ReBe)object);
            } else if (object instanceof ReBeArt) {
                string = getStringOf((ReBeArt)object);
            } else if (object instanceof Vertragsart) {
                string = getStringOf((Vertragsart)object);
            } else if (object instanceof Verwaltungsbereich) {
                string = getStringOf((Verwaltungsbereich)object);
            } else if (object instanceof VerwaltendeDienststelle) {
                string = getStringOf((VerwaltendeDienststelle)object);
            } else if (object instanceof Verwaltungsgebrauch) {
                string = getStringOf((Verwaltungsgebrauch)object);
            } else if (object instanceof Baum) {
                string = getStringOf((Baum)object);
            } else if (object instanceof BaumNutzung) {
                string = getStringOf((BaumNutzung)object);
            } else if (object instanceof BaumKategorieAuspraegung) {
                string = getStringOf((BaumKategorieAuspraegung)object);
            } else if (object instanceof Spielplatz) {
                string = getStringOf((Spielplatz)object);
            } else if (object instanceof Flurstueck) {
                string = getStringOf((Flurstueck)object);
            } else if (object instanceof FlurstueckSchluessel) {
                string = getStringOf((FlurstueckSchluessel)object);
            } else if (object instanceof Key) {
                string = getStringOf((Key)object);
            } else if (object instanceof Vertrag) {
                string = getStringOf((Vertrag)object);
            } else if (object instanceof Gemarkung) {
                string = getStringOf((Gemarkung)object);
            } else if (object instanceof Farbe) {
                string = getStringOf((Farbe)object);
            } else if (object instanceof DmsUrl) {
                string = getStringOf((DmsUrl)object);
            } else if (object instanceof Url) {
                string = getStringOf((Url)object);
            } else if (object instanceof UrlBase) {
                string = getStringOf((UrlBase)object);
            } else if (object instanceof Geom) {
                string = getStringOf((Geom)object);
            } else if (object instanceof Collection) {
                string = getStringOf((Collection<CidsBean>)object);
            }
        }
        return string;
    }
}
