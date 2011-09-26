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
import de.cismet.lagisEE.entity.extension.baum.Baum;
import de.cismet.lagisEE.entity.extension.baum.BaumKategorie;
import de.cismet.lagisEE.entity.extension.baum.BaumNutzung;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class BaumDateiDataSource extends ADataSource<Baum> implements JRDataSource {

    //~ Static fields/initializers ---------------------------------------------

    private static final String JR_LAGE = "lage";
    private static final String JR_BAUM_NR = "baumnr";
    private static final String JR_BAUM_BEST = "baumbestand";
    private static final String JR_AUFTR_NEHMER = "auftragnehmer";
    private static final String JR_ERFASSUNG = "erfassung";
    private static final String JR_FAELL_DATUM = "faelldatum";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumDateiDataSource object.
     */
    public BaumDateiDataSource() {
        super();
    }

    /**
     * Creates a new NutzungenDataSource object.
     *
     * @param  baeumeList  buchungen DOCUMENT ME!
     */
    public BaumDateiDataSource(final List<Baum> baeumeList) {
        super(baeumeList);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected List<Baum> retrieveData() {
        final Flurstueck currentFlurstueck = LAGIS_BROKER.getCurrentFlurstueck();
        final Set<Baum> baeumeSet = currentFlurstueck.getBaeume();

        return new ArrayList<Baum>(baeumeSet);
    }

    @Override
    protected Object getFieldValue(final String fieldName) throws JRException {
        if (JR_LAGE.equals(fieldName)) {
            return super.currentItem.getLage();
        } else if (JR_BAUM_NR.equals(fieldName)) {
            return super.currentItem.getBaumnummer();
        } else if (JR_BAUM_BEST.equals(fieldName)) {
            final BaumNutzung nutzung = super.currentItem.getBaumNutzung();
            if (nutzung == null) {
                return null;
            }

            final BaumKategorie category = nutzung.getBaumKategorie();
            if (category == null) {
                return null;
            }

            return category.getBezeichnung();
        } else if (JR_AUFTR_NEHMER.equals(fieldName)) {
            return super.currentItem.getAuftragnehmer();
        } else if (JR_ERFASSUNG.equals(fieldName)) {
            return super.formatDate(super.currentItem.getErfassungsdatum());
        } else if (JR_FAELL_DATUM.equals(fieldName)) {
            return super.formatDate(super.currentItem.getFaelldatum());
        }

        throw new IllegalArgumentException("Field " + fieldName
                    + " is not supported in this report");
    }
}
