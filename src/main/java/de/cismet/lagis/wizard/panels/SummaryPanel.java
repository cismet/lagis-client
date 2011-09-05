/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.wizard.panels;

import com.vividsolutions.jts.geom.Geometry;

import java.awt.BorderLayout;

import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import de.cismet.lagis.wizard.GeometryAreaChecker;

import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
public class SummaryPanel extends JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final String HTML_BEGIN =
        "<html><body><center><p><h2>Fl&auml;chen der angegebenen Flurst&uuml;cke</h2></p><table cellspacing='8'>";
    private static final String HTML_DIFF_REC =
        "<td><b>Differenz Quell- und Ziel-Flurst&uuml;ck(e):</b></td> <td align='right'>%.2f</td></tr>";
    private static final String HTML_SEP = "<tr><td colspan='2'><hr/></td></tr>";
    private static final String HTML_FS_REC = "<tr><td><b>Flurst&uuml;ck %s:</b></td> <td align='right'>%.2f</td></tr>";
    private static final String HTML_END = "</table></center></body></html>";

    public static final String GEOM_AREA_CHECKER = "Geometry Area Checker";

    //~ Instance fields --------------------------------------------------------

    private final JLabel htmlLabel;
    private final JScrollPane scrollPane;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SummaryPanel object.
     */
    public SummaryPanel() {
        super.setLayout(new BorderLayout());
        this.htmlLabel = new JLabel();
        this.htmlLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        this.scrollPane = new JScrollPane(this.htmlLabel);
        super.add(scrollPane, BorderLayout.CENTER);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   chk  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String buildHTML(final GeometryAreaChecker chk) {
        // print target Flurstücke
        final Map<FlurstueckSchluessel, Geometry> targetGeomsMap = chk.getTargetGeometriesMap();
        final Map<FlurstueckSchluessel, Geometry> map = chk.getResultGeometriesMap();

        if ((targetGeomsMap == null) || (map == null)) {
            return "";
        }

        final StringBuilder builder = new StringBuilder(HTML_BEGIN);
        Geometry tmpGeom;

        for (final Map.Entry<FlurstueckSchluessel, Geometry> entry : targetGeomsMap.entrySet()) {
            tmpGeom = entry.getValue();
            if (tmpGeom == null) {
                builder.append(String.format(HTML_FS_REC, entry.getKey().getKeyString(), 0.0));
            } else {
                builder.append(String.format(HTML_FS_REC, entry.getKey().getKeyString(), tmpGeom.getArea()));
            }
        }
        builder.append(HTML_SEP);

        if (map != null) {
            for (final Map.Entry<FlurstueckSchluessel, Geometry> entry : map.entrySet()) {
                tmpGeom = entry.getValue();
                if (tmpGeom == null) {
                    builder.append(String.format(HTML_FS_REC, entry.getKey().getKeyString(), 0.0));
                } else {
                    builder.append(String.format(HTML_FS_REC, entry.getKey().getKeyString(), tmpGeom.getArea()));
                }
            }
            builder.append(HTML_SEP);
        }

        // print difference
        final double sumTargets = chk.getSumTargets();
        final double sumArea = chk.getSumArea();

        builder.append(String.format(HTML_DIFF_REC, Math.abs(sumArea - sumTargets)));
        builder.append(HTML_END);

        return builder.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wizardData  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public void refresh(final Map wizardData) {
        final Object chkObj = wizardData.get(GEOM_AREA_CHECKER);
        if (chkObj == null) {
            return;
        }

        if (!(chkObj instanceof GeometryAreaChecker)) {
            throw new IllegalArgumentException("Wizard data value for GEOM_AREA_CHECKER has illegal value: " + chkObj);
        }

        final String html = this.buildHTML((GeometryAreaChecker)chkObj);
        this.htmlLabel.setText(html);
        this.scrollPane.repaint();
        this.scrollPane.getViewport().repaint();
        this.scrollPane.revalidate();
    }
}
