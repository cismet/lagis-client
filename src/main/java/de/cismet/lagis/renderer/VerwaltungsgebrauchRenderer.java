/*
 * VerwaltungsgebrauchRenderer.java
 *
 * Created on 15. Mai 2007, 11:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.renderer;

import de.cismet.lagisEE.entity.core.hardwired.Farbe;
import de.cismet.lagisEE.entity.core.hardwired.Verwaltungsgebrauch;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author Puhl
 */
public class VerwaltungsgebrauchRenderer extends DefaultTableCellRenderer{
    private XMLOutputter serializer = new XMLOutputter();
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
//    private static final Icon VIEW_ICON = new Icon() {
//
//        public int getIconHeight() {
//            return ICON_SIZE;
//        }
//
//        public int getIconWidth() {
//            return ICON_SIZE;
//        }
//
//        public void paintIcon(Component c, Graphics g, int x, int y) {
//            Color oldColor = g.getColor();
//
//            g.setColor(new Color(70, 70, 70));
//            g.fillRect(x, y, ICON_SIZE, ICON_SIZE);
//
//            g.setColor(new Color(100, 230, 100));
//            g.fillRect(x + 1, y + 1, ICON_SIZE - 2, ICON_SIZE - 2);
//
//            g.setColor(oldColor);
//        }
//    };
    
    class ColorIcon implements  Icon{
        private Color color;
        public ColorIcon(Color color){
            this.color = color;
        }
        private static final int ICON_SIZE = 8;
        
        public int getIconWidth() {
            return ICON_SIZE;
        }
        
        public int getIconHeight() {
            return ICON_SIZE;
        }
        
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Color oldColor = g.getColor();
            
            g.setColor(new Color(70, 70, 70));
            g.fillRect(x, y, ICON_SIZE, ICON_SIZE);
            
            g.setColor(color);
            g.fillRect(x + 1, y + 1, ICON_SIZE - 2, ICON_SIZE - 2);
            g.setColor(oldColor);
        }
        
    }
    Element htmlTooltip;
    /** Creates a new instance of VerwaltungsgebrauchRenderer */
    public VerwaltungsgebrauchRenderer() {
        super();
        final Format format = Format.getPrettyFormat();
        // TODO: WHY NOT USING UTF-8
        format.setEncoding("ISO-8859-1");
        serializer.setFormat(format);
    }
    
    public void setHTMLTooltip(Element htmlTooltip){
        log.debug("htmltooltipset:"+htmlTooltip);
        this.htmlTooltip = htmlTooltip;
    }
    
    public void setValue(Object value) {
        try{            
            if(value instanceof Verwaltungsgebrauch){                
                Verwaltungsgebrauch tmp = (Verwaltungsgebrauch) value;                
                Set<Farbe> farben;
                Farbe farbe;
                if(tmp != null){                 
                    log.debug("verwaltungsgebrauch != null");
                    if((farben=tmp.getFarben()) != null && (farbe=farben.iterator().next()) != null){                        
                        setIcon(new ColorIcon(new Color(farbe.getRgbFarbwert())));
                        setIconTextGap(5);
                        setBorder(new EmptyBorder(0,5,0,0));
                        setText((value == null)?"":value.toString());                        
                    } else {
                        setIcon(new ColorIcon(Color.black));
                        setIconTextGap(5);
                        setBorder(new EmptyBorder(0,5,0,0));
                    }
                    
                    try{                        
                            Element html = htmlTooltip.getChild("HTML");
                            html.getChild("Oberkategorie").setText(tmp.getKategorie().getOberkategorie().getBezeichnung());
                            html.getChild("Kategorie").setText(tmp.getKategorie().getBezeichnung());
                            html.getChild("Verwaltungsgebrauch").setText(tmp.getBezeichnung());
                            String htmlString = serializer.outputString(html);
                            log.debug("setting htmltooltip: "+htmlString);
                            setToolTipText(htmlString);                        
                    }catch(Exception silent){                        
                        setToolTipText("Objektnutzung");
                    }
                    
                    setText((value == null)?"":value.toString());
                } else {
                    setIcon(new ColorIcon(Color.black));
                    setIconTextGap(5);
                    setBorder(new EmptyBorder(0,5,0,0));
                    setText("");
                    setToolTipText("Objektnutzung");
                }
                
            } else if(value == null){
                setIcon(null);
                setText("");
                setBorder(null);
                setToolTipText("Objektnutzung");                
            }
        }catch(Exception ex){
            log.warn("Fehler beim Rendern eines Verwaltungsgebrauchs",ex);
            setIcon(new ColorIcon(Color.black));
            setIconTextGap(5);
            setBorder(new EmptyBorder(0,5,0,0));
            setText((value == null)?"":value.toString());
            setToolTipText("Objektnutzung");
        }
    }
    
}
