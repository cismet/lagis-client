/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.lagis.config;

import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.ConfigurationManager;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author Sebastian Puhl
 */
public class UserDependingConfigurationManager extends ConfigurationManager {

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    Vector<Configurable> configurables = new Vector<Configurable>();
    private String fileName = "configuration.xml";
    private String defaultFileName = "configuration.xml";
    private String classPathFolder = "/";
    //TODO refactor name (bad name because it is not the final file);
    private String userDependingConfigurationFile;
    private String userDependingConfigurationClasspath;
    Properties userConfigurationProperties;
    //TODO implement ??
    //private String defaultUserDepedingConfigurationFile="UserDependingConfiguration";
    private String currentUsername;
    private String folder = ".cismet";
    private String home;
    private String fs;
    private Element rootObject = null;
    private Element serverRootObject = null;

    /** Creates a new instance of ConfigurationManager */
    public UserDependingConfigurationManager() {
        super();
        log.debug("Create ConfigurationManager.");
        home = System.getProperty("user.home");
        fs = System.getProperty("file.separator");
    }

    public void addConfigurable(Configurable configurable) {
        configurables.add(configurable);
    }

    public void removeConfigurable(Configurable configurable) {
        configurables.remove(configurable);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void configure() {
        configure((Configurable) null);
    }

    public void configure(String path) {
        configure(null, path);
    }

    public void configure(Configurable singleConfig) {
        configure(singleConfig, home + fs + folder + fs + fileName);
    }

    public void configure(Configurable singleConfig, String path) {
        try {
            SAXBuilder builder = new SAXBuilder(false);                        
            //log.debug("encoding detection results: "+EncodingDetector.cachedDetectEncoding(path));
            Document doc = builder.build(new File(path));
            rootObject = doc.getRootElement();
        } catch (Throwable e) {
            log.warn("Fehler beim Lesen der Einstellungen (User.Home) (" + singleConfig + ") wenn null dann alle", e);
        }
        if (rootObject == null) {
            //Keins da. Deshalb das vordefinierte laden
            rootObject = getRootObjectFromClassPath();
        }
        serverRootObject = getRootObjectFromClassPath();
        if (rootObject == null) {
            log.fatal("Fehler beim Konfigurationsmanagement. Von einem fehlerfreien Start kann nicht ausgegangen werden.");
        }

        XMLOutputter serializer = new XMLOutputter();
        serializer.setEncoding("ISO-8859-1");
        log.debug("ENCODING:" + serializer.toString());
        serializer.setIndent("\t");
        serializer.setLineSeparator("\n");
        serializer.setNewlines(true);
        serializer.setTextTrim( true );

        log.info("ConfigurationDocument: " + serializer.outputString(rootObject.getDocument()));
        pureConfigure(singleConfig);
    }

    public void configureFromClasspath() {
        configureFromClasspath(null);
    }

    public void configureFromClasspath(Configurable singleConfig) {
        rootObject = getRootObjectFromClassPath();
        pureConfigure(singleConfig);
    }

    private Element getRootObjectFromClassPath() {
        log.info("Lesen der Einstellungen (InputStream vom ClassPath)");
        SAXBuilder builder = new SAXBuilder(false);
        //Update Userdepeding Configuration
        //TODO vielleicht default Properties
        if (currentUsername != null && userDependingConfigurationFile != null && userDependingConfigurationClasspath != null) {
            try {
                log.debug("Classpath+userDependingFile:" + userDependingConfigurationClasspath + " " + userDependingConfigurationFile);
                //TODO NB Kompatibel sihe weiter untern --> contextClassLoader??
                userConfigurationProperties = new Properties();
                InputStream in = getClass().getResourceAsStream(userDependingConfigurationClasspath + userDependingConfigurationFile);
                userConfigurationProperties.load(in);
                log.debug("UserDependingConfigurationProperties: " + userConfigurationProperties);
            } catch (Exception ex) {
                log.warn("Fehler beim lesen des UserConfigurationPropertiesfiles: ", ex);
                userConfigurationProperties = null;
            }
        } else {
            userConfigurationProperties = null;
            log.debug("Keine Userabhängige Konfiguration möglich: currentUsername=" + currentUsername + " userDependingPropertiesFile=" + userDependingConfigurationFile + " Problem Properties");

        }

        try {
            log.debug("getRootObjectFromClassPath():classPathFolder+defaultFileName=" + classPathFolder + defaultFileName);
            Document doc;
            //TODO should only overwite default settings
            if (userConfigurationProperties != null && currentUsername != null) {
                try {
                    //TODO implement fallbacks
                    //IF the user doesn't exist take the group, if the group does not exist take the domain, if the domain does not exist take the default;                                        
                    String configFileName = userConfigurationProperties.getProperty(currentUsername);
                    doc = builder.build(getClass().getResourceAsStream(classPathFolder + configFileName));
                } catch (Exception ex) {
                    log.debug("Keine Userabhängige Konfiguration möglich: currentUsername=" + currentUsername + " userConfigurationProperties=" + userConfigurationProperties);
                    log.warn("Fehler beim laden einer Userabhängigen Configuration (lade default)(kein Eintrag vorhanden): ", ex);
                    doc = builder.build(getClass().getResourceAsStream(classPathFolder + defaultFileName));
                }
            } else {
                doc = builder.build(getClass().getResourceAsStream(classPathFolder + defaultFileName));
            }
            return doc.getRootElement();
        } catch (Throwable e) {
            log.warn("Fehler beim Lesen der Einstellungen (InputStream vom ClassPath)", e);
            try {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(classPathFolder + defaultFileName);
                Document doc = builder.build(is);
                return doc.getRootElement();
            } catch (Throwable t) {
                log.error("Fehler beim Lesen der Einstellungen (InputStream vom ClassPath mit NB Classloader", t);
            }
            return null;
        }
    }

    private void pureConfigure(Configurable singleConfig) {
        if (singleConfig == null) {
            for (Configurable elem : configurables) {
                try {
                    elem.masterConfigure(serverRootObject);
                } catch (Throwable serverT) {
                    log.warn("Fehler bei elem.masterConfigure(serverRootObject)", serverT);
                }
                try {
                    elem.configure(rootObject);
                } catch (Throwable clientT) {
                    log.warn("Fehler bei elem.configure(rootObject)", clientT);
                }
            }
        } else {
            singleConfig.masterConfigure(serverRootObject);
            singleConfig.configure(rootObject);
        }
    }

    public void writeConfiguration() {
        new File(home + fs + folder).mkdirs();
        writeConfiguration(home + fs + folder + fs + fileName);
    }

    public void writeConfiguration(String path) {
        try {
            log.debug("try to write configuration");
            Element root = new Element("cismetConfigurationManager");
            for (Configurable elem : configurables) {
                try {
                    Element e = elem.getConfiguration();

                    log.debug("Schreibe Element: " + e);
                    if (e != null) {
                        root.addContent(e);
                    }
                } catch (Exception t) {
                    log.warn("Fehler beim Schreiben der eines Konfigurationsteils.", t);
                }
            }  
            Document doc = new Document(root);
            XMLOutputter serializer = new XMLOutputter();
            serializer.setEncoding("ISO-8859-1");
            log.debug("ENCODING:" + serializer.toString());
            serializer.setIndent("\t");
            serializer.setLineSeparator("\n");
            serializer.setNewlines(true);
            serializer.setTextTrim( true );
            File file = new File(path);
            FileWriter writer = new FileWriter(file);
            serializer.output(doc, writer);
            writer.flush();
        } catch (Throwable tt) {
            log.error("Fehler beim Schreiben der Konfiguration.", tt);
        }
    }

    public String getDefaultFileName() {
        return defaultFileName;
    }

    public void setDefaultFileName(String defaultFileName) {
        this.defaultFileName = defaultFileName;
    }

    public String getClassPathFolder() {
        return classPathFolder;
    }

    public void setClassPathFolder(String classPathFolder) {
        this.classPathFolder = classPathFolder;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getFileSeperator() {
        return fs;
    }

    public void setFileSeperator(String fs) {
        this.fs = fs;
    }

    public String getUserDependingConfigurationFile() {
        return userDependingConfigurationFile;
    }

    public void setUserDependingConfigurationFile(String userDependingConfigurationFile) {
        this.userDependingConfigurationFile = userDependingConfigurationFile;
    }

    public String getCurrentUser() {
        return currentUsername;
    }

    public void setCurrentUser(String currentUsername) {
        this.currentUsername = currentUsername;
    }

    public String getUserDependingConfigurationClasspath() {
        return userDependingConfigurationClasspath;
    }

    public void setUserDependingConfigurationClasspath(String userDependingConfigurationClasspath) {
        this.userDependingConfigurationClasspath = userDependingConfigurationClasspath;
    }
}
