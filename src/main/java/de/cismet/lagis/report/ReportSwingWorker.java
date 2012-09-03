/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.report;

/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/

import com.lowagie.text.Document;
import com.lowagie.text.pdf.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;

import java.awt.Frame;

import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.cismet.tools.BrowserLauncher;
import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class ReportSwingWorker extends SwingWorker<Boolean, Object> {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportSwingWorker.class);

    //~ Instance fields --------------------------------------------------------

    private final List<String> compiledReportList;
    private final Map<String, JRDataSource> dataSourcesMap;

    private final ReportSwingWorkerDialog dialog;
    private final boolean withDialog;
    private String directory;
    private final Map<String, String> paramMap;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ReportSwingWorker object.
     *
     * @param  compiledReportList  DOCUMENT ME!
     * @param  dataSourcesMap      DOCUMENT ME!
     * @param  paramMap            DOCUMENT ME!
     * @param  withDialog          DOCUMENT ME!
     * @param  parent              DOCUMENT ME!
     * @param  directory           DOCUMENT ME!
     */
    public ReportSwingWorker(final List<String> compiledReportList,
            final Map<String, JRDataSource> dataSourcesMap,
            final Map<String, String> paramMap,
            final boolean withDialog,
            final Frame parent,
            final String directory) {
        this.compiledReportList = compiledReportList;
        this.withDialog = withDialog;
        this.directory = directory;

        this.dataSourcesMap = dataSourcesMap;
        this.paramMap = paramMap;

        if (withDialog) {
            dialog = new ReportSwingWorkerDialog(parent, true);
        } else {
            dialog = null;
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Boolean doInBackground() throws Exception {
        if (withDialog) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        StaticSwingTools.showDialog(dialog);
                    }
                });
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        FileOutputStream fos = null;
        try {
            final List<InputStream> ins = new ArrayList<InputStream>();

            JRDataSource dataSource;
            final int numReports = this.compiledReportList.size();
            for (int index = 0; index < numReports; index++) {
                final String report = compiledReportList.get(index);

                // report holen
                final JasperReport jasperReport = (JasperReport)JRLoader.loadObject(ReportSwingWorker.class
                                .getResourceAsStream(report));

                // daten vorbereiten
                dataSource = this.dataSourcesMap.get(report);

                // print aus report und daten erzeugen
                final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, this.paramMap, dataSource);
                // quer- bzw hochformat übernehmen
                jasperPrint.setOrientation(jasperReport.getOrientation());

                // zum pdfStream exportieren und der streamliste hinzufügen
                final ByteArrayOutputStream outTmp = new ByteArrayOutputStream();
                JasperExportManager.exportReportToPdfStream(jasperPrint, outTmp);
                ins.add(new ByteArrayInputStream(outTmp.toByteArray()));
                outTmp.close();
            }
            // pdfStreams zu einem einzelnen pdfStream zusammenfügen
            concatPDFs(ins, out, true);

            // zusammengefügten pdfStream in Datei schreiben
            final File file = new File(directory, "report.pdf");
            file.getParentFile().mkdirs();
            fos = new FileOutputStream(file);
            fos.write(out.toByteArray());

            // Datei über Browser öffnen
            BrowserLauncher.openURL("file:///" + file);
            return true;
        } catch (IOException ex) {
            LOG.error("Export to PDF-Stream failed.", ex);
        } catch (JRException ex) {
            LOG.error("Export to PDF-Stream failed.", ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                LOG.error("error while closing streams", ex);
            }
        }

        return false;
    }

    @Override
    protected void done() {
        boolean error = false;

        try {
            error = !get();
        } catch (InterruptedException ex) {
            // unterbrochen, nichts tun
            ex.printStackTrace();
        } catch (ExecutionException ex) {
            error = true;
            ex.printStackTrace();
            LOG.error("error while generating report", ex);
        }
        if (withDialog) {
            dialog.setVisible(false);
        }
        if (error) {
            JOptionPane.showMessageDialog(
                dialog.getParent(),
                "Beim Generieren des Reports ist ein Fehler aufgetreten. ",
                "Fehler!",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  streamOfPDFFiles  DOCUMENT ME!
     * @param  outputStream      DOCUMENT ME!
     * @param  paginate          DOCUMENT ME!
     */
    private static void concatPDFs(final List<InputStream> streamOfPDFFiles,
            final OutputStream outputStream,
            final boolean paginate) {
        int totalNumOfPages = 0;
        final Document document = new Document();
        final List<InputStream> inputStreams = streamOfPDFFiles;
        final List<PdfReader> pdfReaders = new ArrayList<PdfReader>();

        try {
            final PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            for (final InputStream pdf : inputStreams) {
                final PdfReader pdfReader = new PdfReader(pdf);
                pdfReaders.add(pdfReader);
                totalNumOfPages += pdfReader.getNumberOfPages();
            }

            document.open();
            final BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            final PdfContentByte contentByte = writer.getDirectContent();

            PdfImportedPage page;
            int currentPageNumber = 0;

            for (final PdfReader pdfReader : pdfReaders) {
                int currentNumOfPages = 0;
                while (currentNumOfPages < pdfReader.getNumberOfPages()) {
                    currentNumOfPages++;
                    currentPageNumber++;

                    document.setPageSize(pdfReader.getPageSizeWithRotation(currentNumOfPages));
                    document.newPage();

                    page = writer.getImportedPage(pdfReader, currentNumOfPages);
                    contentByte.addTemplate(page, 0, 0);

                    if (paginate) {
                        contentByte.beginText();
                        contentByte.setFontAndSize(baseFont, 9);
                        contentByte.showTextAligned(
                            PdfContentByte.ALIGN_CENTER,
                            currentPageNumber
                                    + " of "
                                    + totalNumOfPages,
                            520,
                            5,
                            0);
                        contentByte.endText();
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error("error while merging pdfs", ex);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException ex) {
                LOG.error("error whil closing pdfstream", ex);
            }
        }
    }
}
