/*
 * RessortFactory.java
 *
 * Created on 19. November 2007, 15:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.widget;

import de.cismet.lagis.interfaces.Widget;
import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.NoWriteError;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Element;

/**
 *
 * @author Sebastian Puhl
 */
public class RessortFactory implements Configurable{
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RessortFactory.class);
    private HashMap<String,AbstractWidget> ressorts=new HashMap<String,AbstractWidget>();
    private HashMap<Widget,Boolean> ressortPermissions= new HashMap<Widget,Boolean>(); 
    private static RessortFactory instance;
    
    /** Creates a new instance of RessortFactory private because of the Singeltonpattern */
    private RessortFactory() {
        
    }
    
    public static RessortFactory getInstance() {
        if (instance==null) {
            log.debug("RessortFactory initalized");
            instance=new RessortFactory();
        }
        return instance;
    }
    
    //TODO warum etwas zurück geben könnte doch auch null ausgeben oder wegen config im Client ???
    public Element getConfiguration() throws NoWriteError {
        Element ret=new Element("cismapWFSFormsPreferences");
        return ret;
    }
    
    //TODO
    public void masterConfigure(Element parent) {
        log.debug("RessortFactory MasterConfigure");
        try{
            Element ressortWidgets=(Element)((Element)parent.clone()).getChild("RessortWidgets").detach();
            List<Element> list=ressortWidgets.getChildren("RessortWidget");
            for (Element e:list) {
                try {
                    //Element e=(Element)o;                    
                    log.debug("Versuche Widget anzulegen: "+e.getChild("widgetName").getText());
                    String className=e.getChild("className").getText();                    
                    Class formClass=Class.forName(className);
                    Constructor constructor=formClass.getConstructor(new Class[]{String.class,String.class});
                    //Object[] constArgs = new Object[]{e.getChild("widgetName").getText(),e.getChild("widgetIcon").getText()};
                    AbstractWidget ressort =(AbstractWidget)constructor.newInstance(e.getChild("widgetName").getText(),e.getChild("widgetIcon").getText());
                    Attribute isReadonly = e.getAttribute("readonly");
                    if(isReadonly != null){
                        if(isReadonly.getValue() != null && isReadonly.getValue().equals("true")){
                            ressortPermissions.put(ressort,true);
                        } else {
                            ressortPermissions.put(ressort,false);
                        }
                    } else {
                        ressortPermissions.put(ressort,true);
                    }                    
                    //TODO MENUE EINTRAG KONFIGURIERBAR MACHEN
                    //ressort.setMenuString(e.getAttribute("menu").getValue());                                                            
                    ressorts.put(ressort.getWidgetName(),ressort);
                    log.debug("Ressort Widget "+ ressort.getWidgetName()+" hinzugefügt");
                } catch(Throwable t) {
                    log.warn("Fehler beim erstellen eines Widgets",t);
                }
            }
        }catch(Exception ex){
            log.warn("Fehler beim MasterConfigure Ressort (z.B. keine Widgets vorhanden)",ex);
        }
    }
    
    //TODO
    public void configure(Element parent) {
        log.debug("RessortFactory configure");
        try{
            
        }catch(Exception ex){
            log.warn("Fehler beim configure Ressort",ex);
        }
    }
    
    public HashMap<String,AbstractWidget> getRessorts(){
        return ressorts;
    }

    public HashMap<Widget, Boolean> getRessortPermissions() {
        return ressortPermissions;
    }        
}
