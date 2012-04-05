/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.report.datasource;

import com.vividsolutions.jts.geom.Geometry;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;
import de.cismet.cids.custom.beans.lagis.RessortCustomBean;
import de.cismet.cids.custom.beans.lagis.VerwaltendeDienststelleCustomBean;
import de.cismet.cids.custom.beans.lagis.VerwaltungsbereichCustomBean;

import de.cismet.lagis.wizard.GeometryWorker;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class VerwaltungsBereichDataSource extends ADataSource<VerwaltungsbereichCustomBean> implements JRDataSource {

    //~ Static fields/initializers ---------------------------------------------

    private static final String JR_DIENSTSTELLE = "dienststelle";
    private static final String JR_FLAECHE = "flaeche";

    //~ Instance fields --------------------------------------------------------

    private Geometry currentGeom;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VerwaltungsBereichDataSource object.
     */
    public VerwaltungsBereichDataSource() {
        super();
    }

    /**
     * Creates a new NutzungenDataSource object.
     *
     * @param  vbList  buchungen DOCUMENT ME!
     */
    public VerwaltungsBereichDataSource(final List<VerwaltungsbereichCustomBean> vbList) {
        super(vbList);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected List<VerwaltungsbereichCustomBean> retrieveData() {
        final FlurstueckCustomBean currentFlurstueck = LAGIS_BROKER.getCurrentFlurstueck();
        final Collection<VerwaltungsbereichCustomBean> vbSet = currentFlurstueck.getVerwaltungsbereiche();
        final FlurstueckSchluesselCustomBean fsKey = currentFlurstueck.getFlurstueckSchluessel();

        final ArrayList<FlurstueckSchluesselCustomBean> fsList = new ArrayList<FlurstueckSchluesselCustomBean>(1);
        fsList.add(fsKey);

        final GeometryWorker worker = new GeometryWorker(fsList);
        final Map<FlurstueckSchluesselCustomBean, Geometry> result = worker.call();
        this.currentGeom = result.get(fsKey);

        return new ArrayList<VerwaltungsbereichCustomBean>(vbSet);
    }

    @Override
    protected Object getFieldValue(final String fieldName) throws JRException {
        if (JR_DIENSTSTELLE.equals(fieldName)) {
            final VerwaltendeDienststelleCustomBean dienst = super.currentItem.getDienststelle();
            if (dienst == null) {
                return null;
            }

            final RessortCustomBean ressort = dienst.getRessort();
            if (ressort == null) {
                return null;
            }

            return ressort.getAbkuerzung();
        } else if (JR_FLAECHE.equals(fieldName)) {
            final Geometry geom = super.currentItem.getGeometry();

            if (geom == null) {
                if (this.currentGeom == null) {
                    return super.formatNumber(0);
                } else {
                    return super.formatNumber((int)Math.round(this.currentGeom.getArea()));
                }
            }

            return super.formatNumber((int)Math.round(geom.getArea()));
        }

        throw new IllegalArgumentException("Field " + fieldName
                    + " is not supported in this report");
    }
}
