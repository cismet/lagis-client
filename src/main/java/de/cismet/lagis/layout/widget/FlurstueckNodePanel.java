/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.layout.widget;

import de.cismet.lagisEE.entity.core.DmsUrl;
import de.cismet.lagisEE.entity.core.Flurstueck;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.core.ReBe;
import de.cismet.lagisEE.entity.core.UrlBase;
import de.cismet.lagisEE.entity.core.Vertrag;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

/**
 * The FlurstueckNodePanel is the main JComponent to display information about a
 * Flurstueck. It is used within a NetBeans Visual ComponentWidget to be
 * displayable in a scene.
 *
 * <p>
 * The panel consists af three major parts:
 * <ul>
 * <li>A title panel, displaying name and status</li>
 * <li>A map panel, displaying the geometric shape</li>
 * <li>A footer, displaying further information</li>
 * </ul>
 *
 * The base of FlurstueckNodePanel is a PureCoolPanel.
 * </p>
 *
 * @author mbrill
 */
public class FlurstueckNodePanel extends AbstractFlurstueckNodePanel {

    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
    private static final String ICON_UNKNOWN = "default";
    private static final String ICON_CONTRACTS = "icon_contracts";
    private static final String ICON_RIGHT = "icon_right";
    private static final String ICON_LOAD = "icon_load";
    private static final String ICON_DMS = "icon_dms";
    private static final String ICON_TOOLTIP = "icon_tooltip";
    private ResourceBundle icon_bundle = ResourceBundle.getBundle("de/cismet/lagis/ressource/history/icon", new Locale("de", "DE"));
    private PureCoolPanel coolPanel;
    private JPanel titlePanel;
    private JPanel innerTitlePanel;
    private JPanel mainPanel;
    private JPanel footerPanel;
    private JPanel innerFooterPanel;

    /**
     * The constructor creates a new instance of FlurstueckNodePanel.
     * It additionally prepares every panel used internally. Because of a bug in
     * Netbean Visual API, it is not recommended to use double buffered
     * components with ComponentWidgets. The ComponentWidgets paint method tries
     * to deal with this problem by disabling double buffering, but this only works
     * for the top level container. So the programmer has to ensure, that every
     * inner component used is not double buffered as well.
     *
     * @param flurstueck the Flurstueck to represent
     */
    public FlurstueckNodePanel(Flurstueck flurstueck) {
        super(flurstueck);

        setOpaque(false);
        setLayout(new BorderLayout());

        coolPanel = new PureCoolPanel();
        coolPanel.setDoubleBuffered(false);
        coolPanel.setLayout(new BorderLayout());

        titlePanel = new JPanel() {

            @Override
            public JToolTip createToolTip() {
                log.info("create tooltip called for Title");
                JToolTip tip = new PureCoolToolTip();
//                new ImageIcon(getClass().
//                        getResource(icon_bundle.getString(ICON_TOOLTIP))));
                tip.setComponent(this);
                return tip;
            }
        };

        titlePanel.setDoubleBuffered(false);
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new GridBagLayout());

        innerTitlePanel = new JPanel();
        innerTitlePanel.setOpaque(false);
        innerTitlePanel.setDoubleBuffered(false);
        innerTitlePanel.setLayout(new GridBagLayout());

        titlePanel.add(innerTitlePanel, setGridbagConstraints(0, 0, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(15, 50, 15, 60), 0, 0));

        mainPanel = new JPanel();
        mainPanel.setDoubleBuffered(false);
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout());

        footerPanel = new JPanel();
        footerPanel.setDoubleBuffered(false);
        footerPanel.setOpaque(false);
//        footerPanel.setLayout(new GridBagLayout());
        footerPanel.setPreferredSize(new Dimension(0, 30));

        innerFooterPanel = new JPanel();
        innerFooterPanel.setOpaque(false);
        innerFooterPanel.setDoubleBuffered(false);
        innerFooterPanel.setLayout(new FlowLayout(FlowLayout.TRAILING, 5, 0));


        setFlurstueckTitle();
        setTitleTooltip();
        setArtefacts();
        setMap();


        coolPanel.add(mainPanel, BorderLayout.CENTER);
        coolPanel.add(titlePanel, BorderLayout.NORTH);
        coolPanel.add(footerPanel, BorderLayout.SOUTH);

        coolPanel.setPanTitle(titlePanel);
        coolPanel.setPanContent(mainPanel);
        coolPanel.setPanInter(footerPanel);


        add(coolPanel, BorderLayout.CENTER);

    }

    /**
     * Method extracts meta information from a flurstueck to set the title panels
     * content. This information is in detail :
     * <ul>
     * <li>The Flurstueck Key</li>
     * <li>An icon, representing the state of the Flurstueck and whether it is
     * loched or not.</li>
     * </ul>
     */
    private void setFlurstueckTitle() {

        // Set the Title information, that is the key of the flurstueck

        boolean nennerNull = false;

        FlurstueckSchluessel key = flurstueck.getFlurstueckSchluessel();
        System.out.println("schlüssel ind setTitle " + key.getKeyString());
        String[] base = key.getKeyString().split(" ");

        JLabel gemLabel = new JLabel(base[0]);
        gemLabel.setHorizontalAlignment(JLabel.CENTER);
        gemLabel.setForeground(Color.WHITE);

        JLabel flurLabel = new JLabel(base[1]);
        flurLabel.setHorizontalAlignment(JLabel.CENTER);
        flurLabel.setForeground(Color.WHITE);

        if (!base[0].equals("pseudo")) {

            String[] zn = base[2].split("/");

            JLabel flurstueckNennerLabel = new JLabel(zn[1]);
            flurstueckNennerLabel.setHorizontalAlignment(JLabel.CENTER);
            flurstueckNennerLabel.setForeground(Color.WHITE);

            JLabel flurstueckZaehlerLabel;

            nennerNull = Integer.parseInt(zn[1]) == 0;


            flurstueckZaehlerLabel = new JLabel(zn[0]);
            flurstueckZaehlerLabel.setHorizontalAlignment(JLabel.CENTER);
            flurstueckZaehlerLabel.setForeground(Color.WHITE);

            flurstueckNennerLabel = new JLabel(zn[1]);
            flurstueckNennerLabel.setHorizontalAlignment(JLabel.CENTER);
            flurstueckNennerLabel.setForeground(Color.WHITE);

            if (nennerNull) {

                innerTitlePanel.add(gemLabel, setGridbagConstraints(0, 0, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 7), 0, 0));

                innerTitlePanel.add(flurLabel, setGridbagConstraints(1, 0, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 4), 0, 0));

                innerTitlePanel.add(flurstueckZaehlerLabel, setGridbagConstraints(2, 0, 1,
                        1, GridBagConstraints.CENTER, GridBagConstraints.NONE, null, 0, 0));

            } else {

                innerTitlePanel.add(gemLabel, setGridbagConstraints(0, 0, 1, 3,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE, new Insets(0, 0, 0, 7), 0, 0));

                innerTitlePanel.add(flurLabel, setGridbagConstraints(1, 0, 1, 3,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE, new Insets(0, 0, 0, 7), 0, 0));

                innerTitlePanel.add(flurstueckZaehlerLabel, setGridbagConstraints(2, 0, 1, 1,
                        GridBagConstraints.PAGE_END, GridBagConstraints.NONE, null, 0, 0));


                JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
                separator.setForeground(Color.WHITE);

                innerTitlePanel.add(separator, setGridbagConstraints(2, 1, 1, 1,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, null, 0, 0));

                innerTitlePanel.add(flurstueckNennerLabel, setGridbagConstraints(2, 2, 1, 1,
                        GridBagConstraints.PAGE_START, GridBagConstraints.NONE, null, 0, 0));
            }
        }

        // set icon dependig wether flurstueck is "staetisch" or "abteilung IX"
        String flurstuecksArt = key.getFlurstueckArt().getBezeichnung();

        ImageIcon icon = null;

        try {
            if (flurstueck.getFlurstueckSchluessel().getGueltigBis() != null) {
                icon = new ImageIcon(getClass().getResource(icon_bundle.getString(flurstuecksArt + "_historic")));
            } else {
                icon = new ImageIcon(getClass().getResource(icon_bundle.getString(flurstuecksArt)));
            }
        } catch (MissingResourceException ex) {
            icon = new ImageIcon(getClass().getResource(icon_bundle.getString(ICON_UNKNOWN)));
        }

        // set all icons indicating the state of the Flurstueck
        // if flurstueck is locked by another user, add an overlay icon
        // indicating this fact.
        if (key.getIstGesperrt()) {
            ImageIcon locked = new ImageIcon(getClass().getResource(icon_bundle.getString("icon_locked")));

            BufferedImage image = new BufferedImage(icon.getIconWidth(),
                    icon.getIconWidth(), BufferedImage.TYPE_INT_ARGB);

            Graphics2D imageGraphics = image.createGraphics();
            imageGraphics.drawImage(icon.getImage(), 0, 0, this);
            imageGraphics.drawImage(locked.getImage(), 0, 3, this);

            icon = new ImageIcon(image.getScaledInstance(-1, -1, Image.SCALE_DEFAULT));
        }

        if (icon != null) {
            coolPanel.setImageRechtsOben(icon);
        }


    }

    /**
     * Convenience method the easily configure the GridBagLayout often used in
     * this class
     * @param gridX column of the component
     * @param gridY row of the component
     * @param gridwidth number of columns in the components dispaly area
     * @param gridheight number of rows in the components display area
     * @param anchor alignment of the component
     * @param fill resize to display area behaviour
     * @param insets insets for the component
     * @param ipadx internal padding x direction
     * @param ipady internal padding y direction
     * @return configured GridBagConstraints Object
     */
    private GridBagConstraints setGridbagConstraints(int gridX, int gridY, int gridwidth,
            int gridheight, int anchor, int fill,
            Insets insets, int ipadx, int ipady) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridX;
        gbc.gridy = gridY;

        gbc.gridheight = gridheight;
        gbc.gridwidth = gridwidth;

        gbc.anchor = anchor;
        gbc.fill = fill;

        if (insets != null) {
            gbc.insets = insets;
        }

        gbc.ipadx = ipadx;
        gbc.ipady = ipady;

        return gbc;
    }

    /**
     * Method to set the content of the footer panel. <br />
     * Information represented in the footer panel contains but is not limited to
     * <ul>
     * <li>ReBe</li>
     * <li>Contracts</li>
     * <li>Usage</li>
     * <li>DMS</li>
     * </ul>
     * Each of these information has representing icons and a descriptive tooltip.
     */
    private void setArtefacts() {

        boolean artifactSet = false;

        if (flurstueck.getDokumente().size() != 0) {
            JLabel iconLabel = new JLabel(new ImageIcon(getClass().
                    getResource(icon_bundle.getString(ICON_DMS)))) {

            @Override
            public JToolTip createToolTip() {
                log.info("create tooltip called for Title");
                JToolTip tip = new PureCoolToolTip();
//                        new ImageIcon(getClass().
//                        getResource(icon_bundle.getString(ICON_TOOLTIP))));
                tip.setComponent(this);
                return tip;
            }
        };

            innerFooterPanel.add(iconLabel);

            String tooltipText = "<html>";

            for (DmsUrl dmsUrl : flurstueck.getDokumente()) {
                
                tooltipText += "<b>Name</b>: " + dmsUrl.getName();
                tooltipText += "<br />";

                UrlBase urlBase = dmsUrl.getUrl().getUrlBase();
                String objectName = dmsUrl.getUrl().getObjektname();

                tooltipText += "<b>Serverpfad</b>: \\" + urlBase.getServer() + urlBase.getPfad() + objectName + "<br /><br />";

            }

            tooltipText += "</html>";

            iconLabel.setToolTipText(tooltipText);

            artifactSet = true;
        }

        if (flurstueck.getVertraege().size() != 0) {

            JLabel iconLabel = new JLabel(new ImageIcon(getClass().
                    getResource(icon_bundle.getString(ICON_CONTRACTS)))) {

            @Override
            public JToolTip createToolTip() {
                log.info("create tooltip called for Title");
                JToolTip tip = new PureCoolToolTip();
//                new ImageIcon(getClass().
//                        getResource(icon_bundle.getString(ICON_TOOLTIP))));
                tip.setComponent(this);
                return tip;
            }
        };

            innerFooterPanel.add(iconLabel);

            String tooltipText = "<html>";

            for (Vertrag vertrag : flurstueck.getVertraege()) {
                
                String aktenZeichen = vertrag.getAktenzeichen();

                if (aktenZeichen != null && !aktenZeichen.isEmpty()) {
                    tooltipText += "<b>Aktenzeichen:</b> " + vertrag.getAktenzeichen() + " <br />";
                }

                String partner = vertrag.getVertragspartner();

                if (partner != null && !partner.isEmpty()) {
                    tooltipText += "<b>Vertragspartner:</b>  <br />" + vertrag.getVertragspartner() + "<br />";
                }

                String bemerkung = vertrag.getBemerkung();

                if (bemerkung != null && !bemerkung.isEmpty()) {
                    tooltipText += "<b>Bemerkung:</b>  <br />" + vertrag.getBemerkung() + "<br />";
                }
            }

            tooltipText += "</html>";

            iconLabel.setToolTipText(tooltipText);

            artifactSet = true;
        }

        boolean hasRight = false;
        boolean hasLoad = false;

        Set<ReBe> rebe = flurstueck.getRechteUndBelastungen();
        if (rebe.size() != 0) {
            for (ReBe reBe : rebe) {
                if (reBe.getIstRecht()) {
                    hasRight = true;
                } else {
                    hasLoad = true;
                }
            }

            if (hasRight) {
                ImageIcon icon = new ImageIcon(getClass().getResource(icon_bundle.getString(ICON_RIGHT)));
                JLabel iconLabel = new JLabel(icon) {

            @Override
            public JToolTip createToolTip() {
                log.info("create tooltip called for Title");
                JToolTip tip = new PureCoolToolTip();
//                        new ImageIcon(getClass().
//                        getResource(icon_bundle.getString(ICON_TOOLTIP))));
                tip.setComponent(this);
                return tip;
            }
        };
                innerFooterPanel.add(iconLabel);
                artifactSet = true;

                String tooltipText = "<html>";

                for (ReBe reBe : rebe) {
                    if (reBe.isRecht()) {
                        String art = reBe.getReBeArt().getBezeichnung();
                        String nummer = reBe.getNummer();
                        String bemerkung = reBe.getBemerkung();
                        String beschreibung = reBe.getBeschreibung();

                        tooltipText += "<b>" + art + " Nummer " + nummer + "</b>" + "<br>";

                        if (beschreibung != null && !beschreibung.equals("")) {
                            tooltipText += "<b>Art des Rechts:</b> " + beschreibung + "<br>";
                        }

                        if (bemerkung != null && !bemerkung.equals("")) {
                            tooltipText += "<b>Bemerkung:</b> <br>" + bemerkung + "<br>";
                        }

                        if (reBe.getDatumEintragung() != null) {
                            String startDate = formatter.format(reBe.getDatumEintragung());
                            tooltipText += "<b>Datum Eintragung:</b> " + startDate + "<br>";
                        }

                        if (reBe.getDatumLoeschung() != null) {
                            String endDate = formatter.format(reBe.getDatumLoeschung());
                            tooltipText += "<b>Datum Löschung:</b> " + endDate + "<br>";
                        }
                        tooltipText += "<br>";
                    }
                }

                tooltipText += "</html>";
                iconLabel.setToolTipText(tooltipText);
            }

            if (hasLoad) {
                ImageIcon icon = new ImageIcon(getClass().getResource(icon_bundle.getString(ICON_LOAD)));
                JLabel iconLabel = new JLabel(icon) {

            @Override
            public JToolTip createToolTip() {
                log.info("create tooltip called for Title");
                JToolTip tip = new PureCoolToolTip();
//                new ImageIcon(getClass().
//                        getResource(icon_bundle.getString(ICON_TOOLTIP))));
                tip.setComponent(this);
                return tip;
            }
        };
                innerFooterPanel.add(iconLabel);
                artifactSet = true;

                String tooltipText = "<html>";

                for (ReBe reBe : rebe) {
                    if (!reBe.isRecht()) {
                        String art = reBe.getReBeArt().getBezeichnung();
                        String nummer = reBe.getNummer();
                        String bemerkung = reBe.getBemerkung();
                        String beschreibung = reBe.getBeschreibung();

                        tooltipText += "<b>" + art + " Nummer " + nummer + "</b>" + "<br>";

                        if (beschreibung != null && !beschreibung.equals("")) {
                            tooltipText += "<b>Art des Rechts:</b> " + beschreibung + "<br>";
                        }

                        if (bemerkung != null && !bemerkung.equals("")) {
                            tooltipText += "<b>Bemerkung:</b> <br>" + bemerkung + "<br>";
                        }

                        if (reBe.getDatumEintragung() != null) {
                            String startDate = formatter.format(reBe.getDatumEintragung());
                            tooltipText += "<b>Datum Eintragung:</b> " + startDate + "<br>";
                        }

                        if (reBe.getDatumLoeschung() != null) {
                            String endDate = formatter.format(reBe.getDatumLoeschung());
                            tooltipText += "<b>Datum Löschung:</b> " + endDate + "<br>";
                        }
                        tooltipText += "<br>";
                    }
                }

                tooltipText += "</html>";
                iconLabel.setToolTipText(tooltipText);
            }

        }

        if (artifactSet) {
            footerPanel.setLayout(new GridBagLayout());
            footerPanel.setPreferredSize(null);
            GridBagConstraints gbc = setGridbagConstraints(0, 0, 1, 1,
                    GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                    new Insets(5, 15, 10, 15), 0, 0);
            gbc.weightx = 1;
            gbc.weighty = 1;
            footerPanel.add(innerFooterPanel, gbc);
        }

    }

    /**
     * This method configures the tooltip for the title panel. The tooltip informs
     * about the status of the Flurstueck, wether its is locked or not and it it is,
     * the tooltip displays the lock message.
     */
    private void setTitleTooltip() {

        StringBuffer tooltipBuff = new StringBuffer("<html>");
        tooltipBuff.append("<b>Flurstück Art: </b>");
        tooltipBuff.append(flurstueck.getFlurstueckSchluessel().getFlurstueckArt().getBezeichnung());

        if (flurstueck.getFlurstueckSchluessel().getGueltigBis() != null) {
            tooltipBuff.append(", historisch");
        }

        tooltipBuff.append("<br /><br />");

        if (flurstueck.getFlurstueckSchluessel().getIstGesperrt()) {
            tooltipBuff.append("<b>Flurstück ist gesperrt</b><br />");
            tooltipBuff.append("<b>Bemerkung: </b> <br />");
            tooltipBuff.append(flurstueck.getFlurstueckSchluessel().getBemerkungSperre());

        }

        tooltipBuff.append("</html>");

        titlePanel.setToolTipText(tooltipBuff.toString());
    }

    /**
     * Set the map panel of this class using {@link FlurstueckGeometriePanel}
     */
    private void setMap() {
        JPanel mapPanel = new FlurstueckGeometriePanel(flurstueck);
        mapPanel.setDoubleBuffered(false);
        mapPanel.setOpaque(false);
        mapPanel.setPreferredSize(new Dimension(100, 100));
        mainPanel.add(mapPanel, BorderLayout.CENTER);
    }

    @Override
    public void setSelected(boolean selection) {
        log.debug("Selection of node is " + selection);
        coolPanel.setSelected(selection);

        Runnable painter = new Runnable() {

            @Override
            public void run() {
                FlurstueckNodePanel.this.getParent().getParent().repaint();
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            painter.run();
        } else {
            SwingUtilities.invokeLater(painter);
        }
    }
}
