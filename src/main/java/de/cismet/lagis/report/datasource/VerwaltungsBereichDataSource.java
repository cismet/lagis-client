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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cismet.lagis.wizard.GeometryWorker;

import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.core.Verwaltungsbereich;
import de.cismet.lagisEE.entity.core.hardwired.Ressort;
import de.cismet.lagisEE.entity.core.hardwired.VerwaltendeDienststelle;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class VerwaltungsBereichDataSource extends ADataSource<Verwaltungsbereich> implements JRDataSource {

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
    public VerwaltungsBereichDataSource(final List<Verwaltungsbereich> vbList) {
        super(vbList);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected List<Verwaltungsbereich> retrieveData() {
        final Flurstueck currentFlurstueck = LAGIS_BROKER.getCurrentFlurstueck();
        final Set<Verwaltungsbereich> vbSet = currentFlurstueck.getVerwaltungsbereiche();
        final FlurstueckSchluessel fsKey = currentFlurstueck.getFlurstueckSchluessel();

        final ArrayList<FlurstueckSchluessel> fsList = new ArrayList<FlurstueckSchluessel>(1);
        fsList.add(fsKey);

        final GeometryWorker worker = new GeometryWorker(fsList);
        final Map<FlurstueckSchluessel, Geometry> result = worker.call();
        this.currentGeom = result.get(fsKey);

        return new ArrayList<Verwaltungsbereich>(vbSet);
    }

    @Override
    protected Object getFieldValue(final String fieldName) throws JRException {
        if (JR_DIENSTSTELLE.equals(fieldName)) {
            final VerwaltendeDienststelle dienst = super.currentItem.getDienststelle();
            if (dienst == null) {
                return null;
            }

            final Ressort ressort = dienst.getRessort();
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
