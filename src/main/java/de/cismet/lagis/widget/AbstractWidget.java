/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * AbstractWidget.java
 *
 * Created on 20. November 2007, 09:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.widget;

import org.apache.log4j.Logger;

import java.awt.Component;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JPanel;

import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.interfaces.NoPermissionsWidget;
import de.cismet.lagis.interfaces.Widget;

import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.ValidationStateChangedListener;

/**
 * DOCUMENT ME!
 *
 * @author   Sebastian Puhl
 * @version  $Revision$, $Date$
 */
public abstract class AbstractWidget extends JPanel implements Widget {

    //~ Static fields/initializers ---------------------------------------------

    protected static final Icon DEFAULT_ICON = new javax.swing.ImageIcon(
            AbstractWidget.class.getResource(
                "/de/cismet/lagis/ressource/icons/titlebar/ressort.png"));

    //~ Instance fields --------------------------------------------------------

    protected Icon widgetIcon;

    protected String widgetName = "Ressort";
    protected String validationMessage = "Die Komponente ist valide";
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private boolean isCoreWidget = false;
    private boolean isAlwaysWritable = false;
    // Validation
    private final ArrayList<ValidationStateChangedListener> validationListeners =
        new ArrayList<ValidationStateChangedListener>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of AbstractWidget.
     */
    public AbstractWidget() {
    }

    //~ Methods ----------------------------------------------------------------

    // TODO Refactor why a abstract class ? better a default Widget ?
    // CLASS BUILD BECAUSE NEED TO BE A COMPONENT --> NOT POSSIBLE WITH INTERFACES
    @Override
    public String getWidgetName() {
        return widgetName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  widgetName  DOCUMENT ME!
     */
    public void setWidgetName(final String widgetName) {
        this.widgetName = widgetName;
    }

    @Override
    public Icon getWidgetIcon() {
        if (widgetIcon != null) {
            return widgetIcon;
        } else {
            widgetIcon = DEFAULT_ICON;
            return DEFAULT_ICON;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  iconName  DOCUMENT ME!
     */
    public void setWidgetIcon(final String iconName) {
        try {
            widgetIcon = new javax.swing.ImageIcon(getClass().getResource(iconName));
        } catch (Exception ex) {
            log.warn("Fehler beim setzen des Icons: ", ex);
            widgetIcon = DEFAULT_ICON;
        }
    }

    @Override
    public abstract void clearComponent();
    @Override
    public abstract void refresh(Object refreshObject);
    @Override
    public abstract void setComponentEditable(boolean isEditable);

    @Override
    public String getValidationMessage() {
        return validationMessage;
    }

    @Override
    public int getStatus() {
        return Validatable.VALID;
    }

    @Override
    public void fireValidationStateChanged(final Object validatedObject) {
        for (final ValidationStateChangedListener listener : validationListeners) {
            listener.validationStateChanged(validatedObject);
        }
    }

    @Override
    public void removeValidationStateChangedListener(final ValidationStateChangedListener l) {
        validationListeners.remove(l);
    }

    @Override
    public void addValidationStateChangedListener(final ValidationStateChangedListener l) {
        validationListeners.add(l);
    }

    @Override
    public void showAssistent(final Component parent) {
    }

    @Override
    public boolean isWidgetReadOnly() {
        if (log.isDebugEnabled()) {
            log.debug("isWidgetReadOnly()");
        }
        if (this instanceof NoPermissionsWidget) {
            if (log.isDebugEnabled()) {
                log.debug("NoPermissionsWidget");
            }
            return false;
        }
        if (isCoreWidget() && !LagisBroker.getInstance().isCoreReadOnlyMode()) {
            if (log.isDebugEnabled()) {
                log.debug("Core");
            }
            return false;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Kein Core Widget oder CoreIstReadOnly");
            }
        }
        ///overdozed it doesn't change at runtime
        final HashMap<Widget, Boolean> ressortPermissions = RessortFactory.getInstance().getRessortPermissions();
        if (ressortPermissions != null) {
            if (log.isDebugEnabled()) {
                log.debug("Widget Ressortpermissions vorhanden : " + ressortPermissions);
            }
            final Boolean isReadOnly = ressortPermissions.get(this);
            if (isReadOnly != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Widget Ressortpermissions vorhanden.: " + isReadOnly);
                }
                if (!isReadOnly) {
                    return false;
                } else {
                    return true;
                }
            } else {
                log.info(
                    "Keine Ressortpermission für Widget vorhanden vorhanden und kein CoreWidget oder CoreReadonly ist aktiviert. --> readonly");
                return true;
            }
        } else {
            log.info(
                "Keine Widget Ressortpermissions vorhanden. und kein CoreWidget oder CoreReadonly ist aktiviert --> readonly");
            return true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isCoreWidget() {
        return isCoreWidget;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  isCoreWidget  DOCUMENT ME!
     */
    public void setIsCoreWidget(final boolean isCoreWidget) {
        this.isCoreWidget = isCoreWidget;
    }
}
