/*
 * AbstractWidget.java
 *
 * Created on 20. November 2007, 09:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.widget;

import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.interfaces.NoPermissionsWidget;
import de.cismet.lagis.interfaces.Widget;
import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.ValidationStateChangedListener;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.JPanel;
import org.apache.log4j.Logger;

/**
 *
 * @author Sebastian Puhl
 */
public abstract class AbstractWidget extends JPanel implements Widget {

    protected String widgetName = "Ressort";
    protected static final Icon DEFAULT_ICON = new javax.swing.ImageIcon(AbstractWidget.class.getResource("/de/cismet/lagis/ressource/icons/titlebar/ressort.png"));
    protected static Icon widgetIcon;
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private boolean isCoreWidget = false;
    private boolean isAlwaysWritable = false;
    

    /** Creates a new instance of AbstractWidget */
    public AbstractWidget() {
    }

    //TODO Refactor why a abstract class ? better a default Widget ?
    //CLASS BUILD BECAUSE NEED TO BE A COMPONENT --> NOT POSSIBLE WITH INTERFACES
    public String getWidgetName() {
        return widgetName;
    }

    public void setWidgetName(String widgetName) {
        this.widgetName = widgetName;
    }

    public Icon getWidgetIcon() {
        if (widgetIcon != null) {
            return widgetIcon;
        } else {
            widgetIcon = DEFAULT_ICON;
            return DEFAULT_ICON;
        }
    }

    public void setWidgetIcon(String iconName) {
        try {
            widgetIcon = new javax.swing.ImageIcon(getClass().getResource(iconName));
        } catch (Exception ex) {
            log.warn("Fehler beim setzen des Icons: ", ex);
            widgetIcon = DEFAULT_ICON;
        }
    }

    public abstract void clearComponent();

    public abstract void refresh(Object refreshObject);

    public abstract void setComponentEditable(boolean isEditable);
    // Validation
    private final ArrayList<ValidationStateChangedListener> validationListeners = new ArrayList<ValidationStateChangedListener>();
    protected String validationMessage = "Die Komponente ist valide";

    public String getValidationMessage() {
        return validationMessage;
    }

    public int getStatus() {
        return Validatable.VALID;
    }

    public void fireValidationStateChanged(Object validatedObject) {
        for (ValidationStateChangedListener listener : validationListeners) {
            listener.validationStateChanged(validatedObject);
        }
    }

    public void removeValidationStateChangedListener(ValidationStateChangedListener l) {
        validationListeners.remove(l);
    }

    public void addValidationStateChangedListener(ValidationStateChangedListener l) {
        validationListeners.add(l);
    }

    public void showAssistent(Component parent) {

    }

    public boolean isWidgetReadOnly() {
        log.debug("isWidgetReadOnly()");
        if (this instanceof NoPermissionsWidget) {
            log.debug("NoPermissionsWidget");
            return false;
        }
        if (isCoreWidget() && !LagisBroker.getInstance().isCoreReadOnlyMode()) {
            log.debug("Core");        
            return false;
        } else {
            log.debug("Kein Core Widget oder CoreIstReadOnly");
        }
        ///overdozed it doesn't change at runtime
        HashMap<Widget, Boolean> ressortPermissions = RessortFactory.getInstance().getRessortPermissions();
        if (ressortPermissions != null) {
            log.debug("Widget Ressortpermissions vorhanden : " + ressortPermissions);
            Boolean isReadOnly = ressortPermissions.get(this);
            if (isReadOnly != null) {
                log.debug("Widget Ressortpermissions vorhanden.: " + isReadOnly);
                if (!isReadOnly) {
                    return false;
                } else {
                    return true;
                }
            } else {
                log.info("Keine Ressortpermission für Widget vorhanden vorhanden und kein CoreWidget oder CoreReadonly ist aktiviert. --> readonly");
                return true;
            }
        } else {
            log.info("Keine Widget Ressortpermissions vorhanden. und kein CoreWidget oder CoreReadonly ist aktiviert --> readonly");
            return true;
        }
    }

    public boolean isCoreWidget() {
        return isCoreWidget;
    }

    public void setIsCoreWidget(boolean isCoreWidget) {
        this.isCoreWidget = isCoreWidget;
    }
        
}
