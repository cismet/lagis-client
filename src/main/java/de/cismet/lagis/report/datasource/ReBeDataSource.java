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
import java.util.List;
import java.util.Set;

import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.lagisEE.entity.core.ReBe;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class ReBeDataSource extends ADataSource<ReBe> implements JRDataSource {

    //~ Static fields/initializers ---------------------------------------------

    private static final String JR_RECHT = "recht";
    private static final String JR_ART = "art";
    private static final String JR_ART_RECHT = "art_des_rechts";
    private static final String JR_NR = "nr";
    private static final String JR_EINTRAGUNG = "eintragung";
    private static final String JR_LOESCHUNG = "loeschung";
    private static final String JR_BEMERKUNG = "bemerkung";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ReBeDataSource object.
     */
    public ReBeDataSource() {
        super();
    }

    /**
     * Creates a new NutzungenDataSource object.
     *
     * @param  rebeList  buchungen DOCUMENT ME!
     */
    public ReBeDataSource(final List<ReBe> rebeList) {
        super(rebeList);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected List<ReBe> retrieveData() {
        final Flurstueck currentFlurstueck = LAGIS_BROKER.getCurrentFlurstueck();
        final Set<ReBe> rebeSet = currentFlurstueck.getRechteUndBelastungen();

        return new ArrayList<ReBe>(rebeSet);
    }

    @Override
    protected Object getFieldValue(final String fieldName) throws JRException {
        if (JR_RECHT.equals(fieldName)) {
            return super.formatBoolean(super.currentItem.getIstRecht());
        } else if (JR_ART.equals(fieldName)) {
            return super.currentItem.getReBeArt().getBezeichnung();
        } else if (JR_ART_RECHT.equals(fieldName)) {
            return super.currentItem.getBeschreibung();
        } else if (JR_NR.equals(fieldName)) {
            return super.currentItem.getNummer();
        } else if (JR_EINTRAGUNG.equals(fieldName)) {
            return super.formatDate(super.currentItem.getDatumEintragung());
        } else if (JR_LOESCHUNG.equals(fieldName)) {
            return super.formatDate(super.currentItem.getDatumLoeschung());
        } else if (JR_BEMERKUNG.equals(fieldName)) {
            return super.currentItem.getBemerkung();
        }

        throw new IllegalArgumentException("Field " + fieldName
                    + " is not supported in this report");
    }
}
