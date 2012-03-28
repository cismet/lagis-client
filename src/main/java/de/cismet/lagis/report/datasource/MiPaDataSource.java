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

import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.MipaCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.MipaKategorieCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.MipaNutzungCustomBean;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class MiPaDataSource extends ADataSource<MipaCustomBean> implements JRDataSource {

    //~ Static fields/initializers ---------------------------------------------

    private static final String JR_LAGE = "lage";
    private static final String JR_AKTZ = "aktz";
    private static final String JR_NUTZUNG = "nutzung";
    private static final String JR_NUTZER = "nutzer";
    private static final String JR_BEGINN = "beginn";
    private static final String JR_ENDE = "ende";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MiPaDataSource object.
     */
    public MiPaDataSource() {
        super();
    }

    /**
     * Creates a new NutzungenDataSource object.
     *
     * @param  mipaList  buchungen DOCUMENT ME!
     */
    public MiPaDataSource(final List<MipaCustomBean> mipaList) {
        super(mipaList);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected List<MipaCustomBean> retrieveData() {
        final FlurstueckCustomBean currentFlurstueck = LAGIS_BROKER.getCurrentFlurstueck();
        final Collection<MipaCustomBean> mipaSet = currentFlurstueck.getMiPas();

        return new ArrayList<MipaCustomBean>(mipaSet);
    }

    @Override
    protected Object getFieldValue(final String fieldName) throws JRException {
        if (JR_LAGE.equals(fieldName)) {
            return super.currentItem.getLage();
        } else if (JR_AKTZ.equals(fieldName)) {
            return super.currentItem.getAktenzeichen();
        } else if (JR_NUTZUNG.equals(fieldName)) {
            final MipaNutzungCustomBean nutzung = super.currentItem.getMiPaNutzung();
            if (nutzung == null) {
                return null;
            }

            final MipaKategorieCustomBean category = nutzung.getMiPaKategorie();
            if (category == null) {
                return null;
            }

            return category.getBezeichnung();
        } else if (JR_NUTZER.equals(fieldName)) {
            return super.currentItem.getNutzer();
        } else if (JR_BEGINN.equals(fieldName)) {
            return super.formatDate(super.currentItem.getVertragsbeginn());
        } else if (JR_ENDE.equals(fieldName)) {
            return super.formatDate(super.currentItem.getVertragsende());
        }

        throw new IllegalArgumentException("Field " + fieldName
                    + " is not supported in this report");
    }
}
