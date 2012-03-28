/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.report;

import net.sf.jasperreports.engine.JRDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import de.cismet.lagis.report.datasource.EmptyDataSource;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class FlurstueckDetailsReport {

    //~ Static fields/initializers ---------------------------------------------

    private static final String REPORT_VB = "/de/cismet/lagis/reports/verwaltungsbereiche_report.jasper";
    private static final String REPORT_NKF = "/de/cismet/lagis/reports/nkf_uebersicht_report.jasper";
    private static final String REPORT_NUTZUNGEN = "/de/cismet/lagis/reports/nutzung_report.jasper";
    private static final String REPORT_REBE = "/de/cismet/lagis/reports/rebe_report.jasper";
    private static final String REPORT_VORGAENGE = "/de/cismet/lagis/reports/vorgaenge_report.jasper";
    private static final String REPORT_MIPA = "/de/cismet/lagis/reports/mipa_report.jasper";
    private static final String REPORT_BAUM = "/de/cismet/lagis/reports/baumdatei_report.jasper";

    private static final String REPORT_MASTER = "/de/cismet/lagis/reports/FlurstueckDetailsReport.jasper";

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  params  currentFS cidsBean DOCUMENT ME!
     */
    public static void showReport(final Map<String, String> params) {
        final ArrayList<String> reports = new ArrayList<String>();
        reports.add(REPORT_MASTER);
//        reports.add(REPORT_VB);
//        reports.add(REPORT_NKF);
//        reports.add(REPORT_NUTZUNGEN);
//        reports.add(REPORT_REBE);
//        reports.add(REPORT_VORGAENGE);
//        reports.add(REPORT_MIPA);
//        reports.add(REPORT_BAUM);

        final HashMap<String, JRDataSource> dataSourcesMap = new HashMap<String, JRDataSource>(reports.size());
//        dataSourcesMap.put(REPORT_VB, new VerwaltungsBereichDataSource());
//        dataSourcesMap.put(REPORT_NKF, new NKFUebersichtDataSource());
//        dataSourcesMap.put(REPORT_NUTZUNGEN, new NutzungenDataSource());
//        dataSourcesMap.put(REPORT_REBE, new ReBeDataSource());
//        dataSourcesMap.put(REPORT_VORGAENGE, new VorgaengeDataSource());
//        dataSourcesMap.put(REPORT_MIPA, new MiPaDataSource());
//        dataSourcesMap.put(REPORT_BAUM, new BaumDateiDataSource());
        dataSourcesMap.put(REPORT_MASTER, new EmptyDataSource(1));

        final ReportSwingWorker worker = new ReportSwingWorker(
                reports,
                dataSourcesMap,
                params,
                true,
                new JFrame(),
                "/tmp");
        worker.execute();
    }
}
