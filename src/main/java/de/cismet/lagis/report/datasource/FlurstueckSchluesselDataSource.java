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

import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class FlurstueckSchluesselDataSource extends ADataSource<FlurstueckSchluesselCustomBean>
        implements JRDataSource {

    //~ Static fields/initializers ---------------------------------------------

    private static final String JR_GEMARKUNG = "gemarkung";
    private static final String JR_FLUR = "flur";
    private static final String JR_FLURSTUECK = "flurstueck";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumDateiDataSource object.
     */
    public FlurstueckSchluesselDataSource() {
        super();
    }

    /**
     * Creates a new NutzungenDataSource object.
     *
     * @param  fsKeyList  baeumeList buchungen DOCUMENT ME!
     */
    public FlurstueckSchluesselDataSource(final List<FlurstueckSchluesselCustomBean> fsKeyList) {
        super(fsKeyList);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected List<FlurstueckSchluesselCustomBean> retrieveData() {
        final FlurstueckCustomBean currentFlurstueck = LAGIS_BROKER.getCurrentFlurstueck();

        final ArrayList<FlurstueckSchluesselCustomBean> fsKeys = new ArrayList<FlurstueckSchluesselCustomBean>(1);
        fsKeys.add(currentFlurstueck.getFlurstueckSchluessel());

        return fsKeys;
    }

    @Override
    protected Object getFieldValue(final String fieldName) throws JRException {
        if (JR_GEMARKUNG.equals(fieldName)) {
            return super.currentItem.getGemarkung().getBezeichnung();
        } else if (JR_FLUR.equals(fieldName)) {
            return String.valueOf(super.currentItem.getFlur());
        } else if (JR_FLURSTUECK.equals(fieldName)) {
            return super.currentItem.getFlurstueckZaehler()
                        + "/"
                        + super.currentItem.getFlurstueckNenner();
        }

        throw new IllegalArgumentException("Field " + fieldName
                    + " is not supported in this report");
    }
}
