/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * InformationPanel.java
 *
 * Created on 16. Februar 2009, 10:50
 */
package de.cismet.lagis.gui.panels;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;

import javax.swing.SwingConstants;

import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckArtCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckSchluesselCustomBean;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.interfaces.FlurstueckChangeListener;

import de.cismet.lagis.widget.AbstractWidget;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
public class InformationPanel extends AbstractWidget implements FlurstueckChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final String UNKNOWN = "/de/cismet/lagis/ressource/svg/unknown.svg";
    private static final String STAEDTISCH = "/de/cismet/lagis/ressource/svg/staedtisch.svg";
    private static final String STAEDTISCH_HISTORIC = "/de/cismet/lagis/ressource/svg/staedtisch_historic.svg";
    private static final String ABTEILUNG_IX = "/de/cismet/lagis/ressource/svg/abteilungIX.svg";
    private static final String ABTEILUNG_IX_HISTORIC = "/de/cismet/lagis/ressource/svg/abteilungIX_historic.svg";
    private static Document UNKNOWN_DOC;
    private static Document STAEDTISCH_DOC;
    private static Document STAEDTISCH_HISTORIC_DOC;
    private static Document ABTEILUNG_IX_DOC;
    private static Document ABTEILUNG_IX_HISTORIC_DOC;

    //~ Instance fields --------------------------------------------------------

    protected Document doc;

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblFlurstueckArt;
    private javax.swing.JLabel lblFlurstueckStatus;
    private javax.swing.JLabel lblModus;
    private org.apache.batik.swing.JSVGCanvas panSVG;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form InformationPanel.
     */
    public InformationPanel() {
        // ToDo How should the programmer know
        setIsCoreWidget(true);
        initComponents();
        lblFlurstueckArt.setText("");
        lblFlurstueckStatus.setText("");
        try {
//            String parser = XMLResourceDescriptor.getXMLParserClassName();
//            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
//            File file = new File("src/de/cismet/lagis/ressource/svg/unknownFlurstueck.svg");
//            URL url = file.toURL();
//            System.out.println(file.getAbsolutePath());
//            System.out.println(url.toString());
//            doc = f.createDocument(url.toString());
//
//            svg = doc.getDocumentElement();
//
//            // Change the document viewBox.
//            svg.setAttributeNS(null, "viewBox", "0 0 64 64");
//
//            // Make the text look nice.
//            svg.setAttributeNS(null, "text-rendering", "geometricPrecision");
//
//            // Remove the xml-stylesheet PI.
//            for (Node n = svg.getPreviousSibling();
//                    n != null;
//                    n = n.getPreviousSibling()) {
//                if (n.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
//                    doc.removeChild(n);
//                    break;
//                }
//            }
//
//            // Remove the Batik sample mark 'use' element.
//            for (Node n = svg.getLastChild();
//                    n != null;
//                    n = n.getPreviousSibling()) {
//                if (n.getNodeType() == Node.ELEMENT_NODE && n.getLocalName().equals("use")) {
//                    svg.removeChild(n);
//                    break;
//                }
//            }
//
//            panSVG.setDocumentState(JSVGCanvas.ALWAYS_STATIC);
//            panSVG.setDocument(doc);
//            panSVG.setDoubleBuffered(false);
//            panSVG.setDoubleBufferedRendering(false);
            // Threading-- seperated Image
            loadDocuments();

            panSVG.setEnableImageZoomInteractor(false);
            panSVG.setEnablePanInteractor(false);
            panSVG.setEnableResetTransformInteractor(false);
            panSVG.setEnableRotateInteractor(false);
            panSVG.setEnableZoomInteractor(false);

            panSVG.setVisible(false);
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des InformationPanel: ", ex);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void loadDocuments() throws IOException {
        final String parser = XMLResourceDescriptor.getXMLParserClassName();
        final SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        Element svg;

        UNKNOWN_DOC = f.createDocument(getClass().getResource(UNKNOWN).toString());
        svg = UNKNOWN_DOC.getDocumentElement();
        svg.setAttributeNS(null, "viewBox", "0 0 64 64");
        svg.setAttributeNS(null, "text-rendering", "geometricPrecision");

        STAEDTISCH_DOC = f.createDocument(getClass().getResource(STAEDTISCH).toString());
        svg = STAEDTISCH_DOC.getDocumentElement();
        svg.setAttributeNS(null, "viewBox", "0 0 64 64");
        svg.setAttributeNS(null, "text-rendering", "geometricPrecision");

        STAEDTISCH_HISTORIC_DOC = f.createDocument(getClass().getResource(STAEDTISCH_HISTORIC).toString());
        svg = STAEDTISCH_HISTORIC_DOC.getDocumentElement();
        svg.setAttributeNS(null, "viewBox", "0 0 64 64");
        svg.setAttributeNS(null, "text-rendering", "geometricPrecision");

        ABTEILUNG_IX_DOC = f.createDocument(getClass().getResource(ABTEILUNG_IX).toString());
        svg = ABTEILUNG_IX_DOC.getDocumentElement();
        svg.setAttributeNS(null, "viewBox", "0 0 64 64");
        svg.setAttributeNS(null, "text-rendering", "geometricPrecision");

        ABTEILUNG_IX_HISTORIC_DOC = f.createDocument(getClass().getResource(ABTEILUNG_IX_HISTORIC).toString());
        svg = ABTEILUNG_IX_HISTORIC_DOC.getDocumentElement();
        svg.setAttributeNS(null, "viewBox", "0 0 64 64");
        svg.setAttributeNS(null, "text-rendering", "geometricPrecision");
    }

    @Override
    public void clearComponent() {
        if (LagisBroker.getInstance().isUnkownFlurstueck()) {
            lblFlurstueckArt.setVisible(false);
            lblFlurstueckStatus.setText("  unbekannt");
            lblFlurstueckStatus.setHorizontalAlignment(SwingConstants.CENTER);
            panSVG.setDocument(UNKNOWN_DOC);
        }
    }

    @Override
    public void refresh(final Object refreshObject) {
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
        if (log.isDebugEnabled()) {
            log.debug("InformationPanel setEditable: " + isEditable);
        }
        if (isEditable == true) {
            lblModus.setText("Bearbeitungsmodus");
        } else {
            lblModus.setText("Anzeigemodus");
        }
    }

    @Override
    public void flurstueckChanged(final FlurstueckCustomBean newFlurstueck) {
        FlurstueckSchluesselCustomBean key = null;
        if ((newFlurstueck != null) && ((key = newFlurstueck.getFlurstueckSchluessel()) != null)) {
            if (key.getFlurstueckArt().getBezeichnung().equals(
                            FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_STAEDTISCH)) {
                if (log.isDebugEnabled()) {
                    log.debug("Flurstueck ist Staedtisch");
                }
                if (key.getGueltigBis() == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Staedtisches Flurstueck ist aktuell");
                    }
                    panSVG.setDocument(STAEDTISCH_DOC);
                    // todo umlaute ersetzen
                    lblFlurstueckArt.setText("städtisch");
                    lblFlurstueckStatus.setText("aktuell,");
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Staedtisches Flurstueck ist historisch");
                    }
                    panSVG.setDocument(STAEDTISCH_HISTORIC_DOC);
                    lblFlurstueckArt.setText("städtisch");
                    lblFlurstueckStatus.setText("historisch,");
                }
                lblFlurstueckArt.setVisible(true);
                lblFlurstueckStatus.setHorizontalAlignment(SwingConstants.RIGHT);
            } else if (key.getFlurstueckArt().getBezeichnung().equals(
                            FlurstueckArtCustomBean.FLURSTUECK_ART_BEZEICHNUNG_ABTEILUNGIX)) {
                if (log.isDebugEnabled()) {
                    log.debug("Flurstueck ist Abteilung IX");
                }
                if (key.getGueltigBis() == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Abteilung IX Flurstueck ist aktuell");
                    }
                    panSVG.setDocument(ABTEILUNG_IX_DOC);
                    lblFlurstueckArt.setText("Abteilung IX");
                    lblFlurstueckStatus.setText("aktuell,");
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Abteilung IX Flurstueck ist historisch");
                    }
                    panSVG.setDocument(ABTEILUNG_IX_HISTORIC_DOC);
                    lblFlurstueckArt.setText("Abteilung IX");
                    lblFlurstueckStatus.setText("historisch,");
                }
                lblFlurstueckArt.setVisible(true);
                lblFlurstueckStatus.setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                log.warn("Flurstückschlüssel ist weder städtisch noch Abteilung XI --> unbekannt");
                panSVG.setDocument(UNKNOWN_DOC);
                lblFlurstueckStatus.setText("  unbekannt");
                lblFlurstueckStatus.setHorizontalAlignment(SwingConstants.CENTER);
                lblFlurstueckArt.setVisible(false);
            }
        } else {
            log.warn("Flurstück oder Schlüssel ist null --> unbekanntes ");
            panSVG.setDocument(UNKNOWN_DOC);
            lblFlurstueckStatus.setText("  unbekannt");
            lblFlurstueckStatus.setHorizontalAlignment(SwingConstants.CENTER);
            lblFlurstueckArt.setVisible(false);
        }

        panSVG.setDocumentState(JSVGCanvas.ALWAYS_STATIC);
        panSVG.setDoubleBuffered(true);
        panSVG.setVisible(true);
        LagisBroker.getInstance().flurstueckChangeFinished(this);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        lblModus = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        panSVG = new org.apache.batik.swing.JSVGCanvas();
        jPanel3 = new javax.swing.JPanel();
        lblFlurstueckStatus = new javax.swing.JLabel();
        lblFlurstueckArt = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        lblModus.setFont(new java.awt.Font("DejaVu Sans", 0, 24)); // NOI18N
        lblModus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblModus.setText("Anzeigemodus");
        lblModus.setMaximumSize(new java.awt.Dimension(3200, 50));
        lblModus.setMinimumSize(new java.awt.Dimension(50, 50));
        lblModus.setPreferredSize(new java.awt.Dimension(200, 50));

        final javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                lblModus,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                302,
                Short.MAX_VALUE).addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE));
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                jPanel1Layout.createSequentialGroup().addGap(12, 12, 12).addComponent(
                    jSeparator1,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    3,
                    Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    lblModus,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    50,
                    javax.swing.GroupLayout.PREFERRED_SIZE)));

        add(jPanel1, java.awt.BorderLayout.PAGE_END);

        jPanel2.setPreferredSize(new java.awt.Dimension(400, 400));
        jPanel2.setLayout(new java.awt.BorderLayout());

        panSVG.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));

        final javax.swing.GroupLayout panSVGLayout = new javax.swing.GroupLayout(panSVG);
        panSVG.setLayout(panSVGLayout);
        panSVGLayout.setHorizontalGroup(
            panSVGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                302,
                Short.MAX_VALUE));
        panSVGLayout.setVerticalGroup(
            panSVGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                143,
                Short.MAX_VALUE));

        jPanel2.add(panSVG, java.awt.BorderLayout.CENTER);

        add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setMaximumSize(new java.awt.Dimension(100, 50));
        jPanel3.setPreferredSize(new java.awt.Dimension(100, 50));

        lblFlurstueckStatus.setFont(new java.awt.Font("DejaVu Sans", 0, 26)); // NOI18N
        lblFlurstueckStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFlurstueckStatus.setText("Historisch");
        lblFlurstueckStatus.setPreferredSize(new java.awt.Dimension(100, 50));

        lblFlurstueckArt.setFont(new java.awt.Font("DejaVu Sans", 0, 26)); // NOI18N
        lblFlurstueckArt.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFlurstueckArt.setText("Städtisch");
        lblFlurstueckArt.setPreferredSize(new java.awt.Dimension(100, 50));

        final javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                jPanel3Layout.createSequentialGroup().addContainerGap().addComponent(
                    lblFlurstueckStatus,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    133,
                    Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                    lblFlurstueckArt,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    139,
                    Short.MAX_VALUE).addGap(29, 29, 29)));
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                jPanel3Layout.createSequentialGroup().addGroup(
                    jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        lblFlurstueckArt,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                        lblFlurstueckStatus,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        add(jPanel3, java.awt.BorderLayout.NORTH);
    } // </editor-fold>//GEN-END:initComponents
}
