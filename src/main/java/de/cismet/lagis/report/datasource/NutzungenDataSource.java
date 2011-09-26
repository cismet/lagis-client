/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.report.datasource;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;

import org.openide.util.Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.cismet.lagisEE.bean.Exception.BuchungNotInNutzungException;
import de.cismet.lagisEE.bean.Exception.IllegalNutzungStateException;

import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.lagisEE.entity.core.Nutzung;
import de.cismet.lagisEE.entity.core.NutzungsBuchung;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class NutzungenDataSource extends ADataSource<NutzungsBuchung> implements JRDataSource {

    //~ Static fields/initializers ---------------------------------------------

    private static final String JR_NUTZUNGS_NR = "nutz_nr";
    private static final String JR_BUCHUNGS_NR = "b_nr";
    private static final String JR_ANLAGE_KL = "anlageklasse";
    private static final String JR_NUTZ_ARTEN_KEY = "nutz_arten_schluessel";
    private static final String JR_FLAECHE = "flaeche";
    private static final String JR_QM_PREIS = "qm_preis";
    private static final String JR_WERT = "wert";
    private static final String JR_BUCHUNGS_WERT = "b_wert";
    private static final String JR_BEMERKUNG = "bem";

    //~ Instance fields --------------------------------------------------------

    private Nutzung currentNutzung;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NutzungenDataSource object.
     */
    public NutzungenDataSource() {
        super();
    }

    /**
     * Creates a new NutzungenDataSource object.
     *
     * @param  buchungen  DOCUMENT ME!
     */
    public NutzungenDataSource(final List<NutzungsBuchung> buchungen) {
        super(buchungen);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected List<NutzungsBuchung> retrieveData() {
        final Flurstueck currentFlurstueck = LAGIS_BROKER.getCurrentFlurstueck();
        final Set<Nutzung> nutzungen = currentFlurstueck.getNutzungen();

        final ArrayList<NutzungsBuchung> buchungen = new ArrayList<NutzungsBuchung>(nutzungen.size());
        for (final Nutzung tmpNutzung : nutzungen) {
            if (tmpNutzung.getBuchungsCount() > 0) {
                for (final NutzungsBuchung buchung : tmpNutzung.getNutzungsBuchungen()) {
                    if (buchung.getGueltigbis() == null) {
                        buchungen.add(buchung);
                    }
                }
            }
        }

        return buchungen;
    }

    @Override
    public boolean next() throws JRException {
        if (super.next()) {
            this.currentNutzung = this.currentItem.getNutzung();
            return true;
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   buchung  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  RuntimeException  DOCUMENT ME!
     */
    private double calculateGesamtPreis(final NutzungsBuchung buchung) {
        final Double gesamtPreis = buchung.getGesamtpreis();
        if (gesamtPreis == null) {
            return 0.0;
        }

        try {
            final Nutzung nutzung = buchung.getNutzung();
            Double stilleReserve = nutzung.getStilleReserveForBuchung(buchung);

            if (stilleReserve == null) {
                stilleReserve = 0.0;
            }

            return gesamtPreis - stilleReserve;
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected Object getFieldValue(final String fieldName) throws JRException {
        if (JR_NUTZUNGS_NR.equals(fieldName)) {
            return String.valueOf(this.currentNutzung.getId());
        } else if (JR_BUCHUNGS_NR.equals(fieldName)) {
            return super.formatNumber(this.currentNutzung.getBuchungsNummerForBuchung(super.currentItem));
        } else if (JR_ANLAGE_KL.equals(fieldName)) {
            return super.currentItem.getAnlageklasse().getSchluessel();
        } else if (JR_NUTZ_ARTEN_KEY.equals(fieldName)) {
            return super.currentItem.getNutzungsart().getBezeichnung();
        } else if (JR_FLAECHE.equals(fieldName)) {
            return super.formatNumber(super.currentItem.getFlaeche());
        } else if (JR_QM_PREIS.equals(fieldName)) {
            return super.formatNumber(super.currentItem.getQuadratmeterpreis());
        } else if (JR_WERT.equals(fieldName)) {
            return super.formatNumber(this.calculateGesamtPreis(super.currentItem));
        } else if (JR_BUCHUNGS_WERT.equals(fieldName)) {
            return super.formatBoolean(this.currentNutzung.getOpenBuchung().getIstBuchwert());
        } else if (JR_BEMERKUNG.equals(fieldName)) {
            return super.currentItem.getBemerkung();
        }

        throw new IllegalArgumentException("Field " + fieldName
                    + " is not supported in this report");
    }
}
