/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * RessortFactory.java
 *
 * Created on 19. November 2007, 15:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.widget;

import org.jdom.Attribute;
import org.jdom.Element;

import java.lang.reflect.Constructor;

import java.util.HashMap;
import java.util.List;

import de.cismet.lagis.interfaces.Widget;

import de.cismet.tools.configuration.Configurable;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public class RessortFactory implements Configurable {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RessortFactory.class);
    private static RessortFactory INSTANCE;

    //~ Instance fields --------------------------------------------------------

    private final HashMap<String, AbstractWidget> ressorts = new HashMap<>();
    private final HashMap<Widget, Boolean> ressortPermissions = new HashMap<>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of RessortFactory private because of the Singeltonpattern.
     */
    private RessortFactory() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static RessortFactory getInstance() {
        if (INSTANCE == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("RessortFactory initalized");
            }
            INSTANCE = new RessortFactory();
        }
        return INSTANCE;
    }

    // TODO warum etwas zurück geben könnte doch auch null ausgeben oder wegen config im Client ???
    @Override
    public Element getConfiguration() {
        final Element ret = new Element("cismapWFSFormsPreferences");
        return ret;
    }

    // TODO
    @Override
    public void masterConfigure(final Element parent) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("RessortFactory MasterConfigure");
        }
        try {
            final Element ressortWidgets = (Element)((Element)parent.clone()).getChild("RessortWidgets").detach();
            final List<Element> list = ressortWidgets.getChildren("RessortWidget");
            for (final Element e : list) {
                try {
                    if (LOG.isDebugEnabled()) {
                        // Element e=(Element)o;
                        LOG.debug("Versuche Widget anzulegen: " + e.getChild("widgetName").getText());
                    }
                    final String className = e.getChild("className").getText();
                    final Class formClass = Class.forName(className);

                    AbstractWidget ressort;
                    Constructor constructor = formClass.getConstructor(new Class[] { String.class });

                    if (constructor == null) {
                        constructor = formClass.getConstructor(new Class[] { String.class, String.class });
                        ressort = (AbstractWidget)constructor.newInstance(e.getChild("widgetName").getText(),
                                e.getChild("widgetIcon").getText());
                    } else {
                        ressort = (AbstractWidget)constructor.newInstance(e.getChild("widgetName").getText());
                    }

                    // Object[] constArgs = new
                    // Object[]{e.getChild("widgetName").getText(),e.getChild("widgetIcon").getText()};
// final AbstractWidget ressort = (AbstractWidget)constructor.newInstance(e.getChild("widgetName")
// .getText()); // ,
                    // e.getChild("widgetIcon").getText());
                    final Attribute isReadonly = e.getAttribute("readonly");
                    if (isReadonly != null) {
                        if ((isReadonly.getValue() != null) && isReadonly.getValue().equals("true")) {
                            ressortPermissions.put(ressort, true);
                        } else {
                            ressortPermissions.put(ressort, false);
                        }
                    } else {
                        ressortPermissions.put(ressort, true);
                    }
                    // TODO MENUE EINTRAG KONFIGURIERBAR MACHEN
                    // ressort.setMenuString(e.getAttribute("menu").getValue());
                    ressorts.put(ressort.getWidgetName(), ressort);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Ressort Widget " + ressort.getWidgetName() + " hinzugefügt");
                    }
                } catch (final Throwable t) {
                    LOG.warn("Fehler beim erstellen eines Widgets", t);
                }
            }
        } catch (Exception ex) {
            LOG.warn("Fehler beim MasterConfigure Ressort (z.B. keine Widgets vorhanden)", ex);
        }
    }

    // TODO
    @Override
    public void configure(final Element parent) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("RessortFactory configure");
        }
        try {
        } catch (Exception ex) {
            LOG.warn("Fehler beim configure Ressort", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public HashMap<String, AbstractWidget> getRessorts() {
        return ressorts;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public HashMap<Widget, Boolean> getRessortPermissions() {
        return ressortPermissions;
    }
}
