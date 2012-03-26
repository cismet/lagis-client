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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cismet.lagis.utillity.AnlagenklasseSumme;

import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.lagisEE.entity.core.Nutzung;
import de.cismet.lagisEE.entity.core.NutzungsBuchung;
import de.cismet.lagisEE.entity.core.hardwired.Anlageklasse;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class NKFUebersichtDataSource extends ADataSource<AnlagenklasseSumme> implements JRDataSource {

    //~ Static fields/initializers ---------------------------------------------

    private static final String JR_ANLAGE_KL = "anlagenklasse";
    private static final String JR_SUMME = "summe";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NKFUebersichtDataSource object.
     */
    public NKFUebersichtDataSource() {
        super();
    }

    /**
     * Creates a new NutzungenDataSource object.
     *
     * @param  summen  buchungen DOCUMENT ME!
     */
    public NKFUebersichtDataSource(final List<AnlagenklasseSumme> summen) {
        super(summen);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected List<AnlagenklasseSumme> retrieveData() {
        final Flurstueck currentFlurstueck = LAGIS_BROKER.getCurrentFlurstueck();
        final Set<Nutzung> nutzungen = currentFlurstueck.getNutzungen();

        // Mapping Bez. Anlagenklasse -> sum(Gesamtpreis)
        final HashMap<Anlageklasse, Double> sumMap = new HashMap<Anlageklasse, Double>();

        Anlageklasse anlkl;
        Double gesamtPreis;
        Double stilleReserve;
        Double recentSum;

        // Ermitteln der Summen aller Geamtpreise aller Buchungen, die eine
        // Anlagenklasse und einen Preis haben. Beachte, dass zu jeder Buchung
        // die stille Reserve von dem Gesamtpreis abgezogen werden muss, bevor
        // sie aufsummiert wird.
        for (final Nutzung tmpNutzung : nutzungen) {
            if (tmpNutzung.getBuchungsCount() > 0) {
                for (final NutzungsBuchung buchung : tmpNutzung.getNutzungsBuchungen()) {
                    if (buchung.getGueltigbis() == null) {
                        gesamtPreis = buchung.getGesamtpreis();
                        anlkl = buchung.getAnlageklasse();

                        if ((anlkl != null) && (gesamtPreis != null)) {
                            try {
                                stilleReserve = tmpNutzung.getStilleReserveForBuchung(buchung);
                                if (stilleReserve == null) {
                                    stilleReserve = 0.0;
                                }

                                if (sumMap.containsKey(anlkl)) {
                                    recentSum = sumMap.get(anlkl);
                                    sumMap.put(anlkl, recentSum + (gesamtPreis - stilleReserve));
                                } else {
                                    sumMap.put(anlkl, gesamtPreis - stilleReserve);
                                }
                            } catch (final Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                }
            }
        }

        // Erstellung der AnlagenklassenSummen aus vorher ermittelten Aggregation
        final ArrayList<AnlagenklasseSumme> summen = new ArrayList<AnlagenklasseSumme>(sumMap.size());
        AnlagenklasseSumme sum;
        for (final Map.Entry<Anlageklasse, Double> entry : sumMap.entrySet()) {
            sum = new AnlagenklasseSumme(entry.getKey());
            sum.setSumme(entry.getValue());
            summen.add(sum);
        }

        return summen;
    }

    @Override
    protected Object getFieldValue(final String fieldName) throws JRException {
        if (JR_ANLAGE_KL.equals(fieldName)) {
            return super.currentItem.getAnlageklasse().getBezeichnung();
        } else if (JR_SUMME.equals(fieldName)) {
            return super.formatNumber(super.currentItem.getSumme());
        }

        throw new IllegalArgumentException("Field " + fieldName
                    + " is not supported in this report");
    }
}