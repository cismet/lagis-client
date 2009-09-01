/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.layout.widget;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.interfaces.DoneDelegate;
import de.cismet.lagis.thread.ExtendedSwingWorker;
import de.cismet.lagis.thread.WFSRetrieverFactory;
import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.tools.configuration.ConfigurationManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 * The <code>FlurstueckGeometryPanel</code> class is used to draw the WFS geometry
 * of a flurstueck. Its is used as the center component of the {@link FlurstueckNodePanel}
 *
 * @author mbrill
 */
public class FlurstueckGeometriePanel extends JPanel {

    private Flurstueck flurstueck;
    private static WFSRetrieverFactory retrieverFactory = WFSRetrieverFactory.getInstance();

    private GeometryPanel geomPanel;
    private JPanel legendPanel;
    private JPanel innerPanel;

    private Geometry flurstueckGeometry;

    private FlurstueckGeometriePanel instance;

    private static final String LAGIS_CONFIGURATION_CLASSPATH = "/de/cismet/lagis/configuration/";
    private static final String LAGIS_LOCAL_CONFIGURATION_FOLDER = ".lagis";
    private static final String LAGIS_CONFIGURATION_FILE = "defaultLagisProperties.xml";
    private static final String LOCAL_LAGIS_CONFIGURATION_FILE = "lagisProperties.xml";

    /**
     * Constructor initialises the WFSRetrieverFactory from where the class receives
     * the Flurstueck geometries to draw. It also initialises the basic layout
     * and constructs the child components which effectively display the content.S
     *
     * @param flurstueck The Flurstueck the geometry of which is drawn
     */
    public FlurstueckGeometriePanel(Flurstueck flurstueck) {

        ConfigurationManager confM = new ConfigurationManager();
        confM.setDefaultFileName(LAGIS_CONFIGURATION_FILE);
        confM.setFileName(LOCAL_LAGIS_CONFIGURATION_FILE);
        confM.setClassPathFolder(LAGIS_CONFIGURATION_CLASSPATH);
        confM.setFolder(LAGIS_LOCAL_CONFIGURATION_FOLDER);
        confM.addConfigurable(retrieverFactory);
        confM.configure(retrieverFactory);

        this.flurstueck = flurstueck;

        setLayout(new GridBagLayout());

        innerPanel = new JPanel();
        innerPanel.setLayout(new BorderLayout(2, 2));
        innerPanel.setDoubleBuffered(false);
        innerPanel.setOpaque(false);


        add(innerPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(3, 15, 3, 15), 0, 0));

        geomPanel = new GeometryPanel();
        geomPanel.setDoubleBuffered(false);


        legendPanel = new JPanel();
        legendPanel.setDoubleBuffered(false);
        legendPanel.setOpaque(false);

        innerPanel.add(geomPanel, BorderLayout.CENTER);
        innerPanel.add(legendPanel, BorderLayout.WEST);

        instance = this;
        drawGeometry();
    }

    /**
     * <code>drawGeometry</code> initiates a swingWorker Thread to retrieve
     * Flurstueck geometries from a WFS. After the WFS call a {@link DoneDelegate}
     * instance is called ({@link FlurstueckGeometryRequestJobDone}) which
     * calls repaint on the GeometryPanel.
     */
    private void drawGeometry() {

        SwingWorker retriever = retrieverFactory.getWFSRetriever(flurstueck.getFlurstueckSchluessel(),
                new FlurstueckGeometryRequestJobDone(), new HashMap<Integer, Boolean>());

        LagisBroker.getInstance().execute(retriever);
    }

    /**
     * This method fills the legend panel with content. This content is a representation
     * of all departments working with the specific Flurstueck
     */
    private void setLegend() {
//        Set<Verwaltungsbereich> vb = flurstueck.getVerwaltungsbereiche();
    }

    /**
     * The <code>GeometryPanel</code> is the main Component of the FlurstueckGeometryPanel.
     * It takes the current Flurstueck geometry received by the WFSRetriever, converts
     * it to a Java Shape object and draws it using Java2D.
     */
    private class GeometryPanel extends JPanel {

        /**
         * Paint method draws Flurstueck geometries on this class' canvas
         * @param g Graphics object
         */
        @Override
        public void paintComponent(Graphics g) {

            if (flurstueckGeometry != null) {
                
                Graphics2D g2d = (Graphics2D) g;

                Envelope flurstueckEnvelope = flurstueckGeometry.getEnvelopeInternal();
                double envelopeHeight = flurstueckEnvelope.getHeight();
                double envelopeWidth = flurstueckEnvelope.getWidth();

                int height = getHeight();
                int width = getWidth();

                double dilation = 0.0;

                if (height > width) {
                    if(envelopeHeight < envelopeWidth)
                        dilation = (width - 10) / envelopeHeight;
                    else
                        dilation = (width - 10) / envelopeWidth;
                    
                } else {
                    if(envelopeHeight < envelopeWidth)
                        dilation = (height - 10) / envelopeWidth;
                    else
                        dilation = (height - 10) / envelopeHeight;
                }
                

                AffineTransform trans = new AffineTransform();
                AffineTransform shapeTrans = new AffineTransform();

                LiteShape liteShape = new LiteShape(flurstueckGeometry, shapeTrans, false);

                trans.translate(width/2 - (envelopeWidth * dilation)/2,
                        height/2 + (envelopeHeight * dilation)/2);
                trans.scale(dilation, -dilation);
                trans.translate(-flurstueckEnvelope.getMinX(), -flurstueckEnvelope.getMinY());

                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Shape transformedShape =trans.createTransformedShape(liteShape);

                g2d.setPaint(Color.BLACK);
                g2d.draw(transformedShape);
                Color bg = new Color(0.6f, 0.6f, 0.6f, 0.6f);
                g2d.setPaint(bg);
                g2d.fill(transformedShape);

            }
        }
    }

    /**
     * This class implements the default behaviour of the WFS Retriever when the
     * SwingWorker job is done.
     */
    private class FlurstueckGeometryRequestJobDone implements DoneDelegate<Geometry, Void> {

        /**
         * When the SwingWorker finished, this method is called to perform the operations
         * neccessary after the worker run. In this case, the method sets the geometry
         * to draw and calls repaint on the GeometryPanel instance which then renders
         * the Geometry.
         *
         * @param worker SwingWorker used
         * @param properties possible Properties asked
         */
        public void jobDone(ExtendedSwingWorker<Geometry, Void> worker,
                HashMap<Integer, Boolean> properties) {

            try {

                if (!worker.hadErrors()) {

                    flurstueckGeometry = worker.get();
                    instance.repaint();

                } else {
                    flurstueckGeometry = null;
                    instance.repaint();
                    System.out.println("Worker had Errors:" + worker.getErrorMessage());
                // TODO - proper logging
                }

            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
//
//    public static void main(String[] args) {
//        JFrame f = new JFrame();
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        f.setSize(500, 500);
//        f.setLayout(new BorderLayout());
//
//        Flurstueck flurstueck = new Flurstueck();
//        FlurstueckSchluessel key = new FlurstueckSchluessel();
//        Gemarkung gem = new Gemarkung();
//        gem.setSchluessel(3001);
//        gem.setBezeichnung("Barmen");
//        key.setGemarkung(gem);
//        key.setFlur(5);
//        key.setFlurstueckZaehler(83);
//        key.setFlurstueckNenner(44);
//        flurstueck.setFlurstueckSchluessel(key);
//        JPanel geomPanel = new FlurstueckGeometriePanel(flurstueck);
//        geomPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
//        f.add(geomPanel, BorderLayout.CENTER);
//        f.setVisible(true);
//    }
}
