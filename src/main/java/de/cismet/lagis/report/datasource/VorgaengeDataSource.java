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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.lagis.VertragCustomBean;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class VorgaengeDataSource extends ADataSource<VertragCustomBean> implements JRDataSource {

    //~ Static fields/initializers ---------------------------------------------

    private static final String JR_ART = "art";
    private static final String JR_AKTZ = "aktz";
    private static final String JR_KAUFPREIS = "kaufpreis_nk";
    private static final String JR_QM_PREIS = "qm_preis";
    private static final String JR_AUFLASSUNG = "auflassung";
    private static final String JR_EINTRAGUNG = "eintragung";
    private static final String JR_VPARTNER = "vertragspartner";
    private static final String JR_BEMERKUNG = "bemerkung";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VorgaengeDataSource object.
     */
    public VorgaengeDataSource() {
        super();
    }

    /**
     * Creates a new NutzungenDataSource object.
     *
     * @param  vertraegeList  buchungen DOCUMENT ME!
     */
    public VorgaengeDataSource(final List<VertragCustomBean> vertraegeList) {
        super(vertraegeList);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected List<VertragCustomBean> retrieveData() {
        final FlurstueckCustomBean currentFlurstueck = LAGIS_BROKER.getCurrentFlurstueck();
        final Collection<VertragCustomBean> vertraegeSet = currentFlurstueck.getVertraege();

        return new ArrayList<VertragCustomBean>(vertraegeSet);
    }

    @Override
    protected Object getFieldValue(final String fieldName) throws JRException {
        if (JR_ART.equals(fieldName)) {
            return super.currentItem.getVertragsart().getBezeichnung();
        } else if (JR_AKTZ.equals(fieldName)) {
            return super.currentItem.getAktenzeichen();
        } else if (JR_KAUFPREIS.equals(fieldName)) {
            return super.formatNumber(super.currentItem.getGesamtpreis());
        } else if (JR_QM_PREIS.equals(fieldName)) {
            return super.formatNumber(super.currentItem.getQuadratmeterpreis());
        } else if (JR_AUFLASSUNG.equals(fieldName)) {
            return super.formatDate(super.currentItem.getDatumAuflassung());
        } else if (JR_EINTRAGUNG.equals(fieldName)) {
            return super.formatDate(super.currentItem.getDatumEintragung());
        } else if (JR_VPARTNER.equals(fieldName)) {
            return super.currentItem.getVertragspartner();
        } else if (JR_BEMERKUNG.equals(fieldName)) {
            return super.currentItem.getBemerkung();
        }

        throw new IllegalArgumentException("Field " + fieldName
                    + " is not supported in this report");
    }
}
