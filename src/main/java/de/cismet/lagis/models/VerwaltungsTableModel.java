/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * VerwaltungsbreicheTableModel.java
 *
 * Created on 23. April 2007, 09:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.lagis.models;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import de.cismet.cids.custom.beans.lagis.VerwaltendeDienststelleCustomBean;
import de.cismet.cids.custom.beans.lagis.VerwaltungsbereichCustomBean;

import de.cismet.cismap.commons.features.Feature;

import de.cismet.lagis.broker.LagisBroker;

/**
 * DOCUMENT ME!
 *
 * @author   Puhl
 * @version  $Revision$, $Date$
 */
public class VerwaltungsTableModel extends CidsBeanTableModel_Lagis {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] COLUMN_NAMES = { "Dienststelle", "Fläche m²" };
    private static final Class[] COLUMN_CLASSES = {
            VerwaltendeDienststelleCustomBean.class,
            Integer.class
        };

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(VerwaltungsTableModel.class);

    //~ Instance fields --------------------------------------------------------

    private double currentWFSSize = 0;
    private boolean history = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VerwaltungsTableModel object.
     */
    public VerwaltungsTableModel() {
        super(COLUMN_NAMES, COLUMN_CLASSES, VerwaltungsbereichCustomBean.class);
    }

    /**
     * Creates a new instance of VerwaltungsbreicheTableModel.
     *
     * @param  verwaltungsbereiche  DOCUMENT ME!
     */
    public VerwaltungsTableModel(final Set<VerwaltungsbereichCustomBean> verwaltungsbereiche) {
        super(COLUMN_NAMES, COLUMN_CLASSES, verwaltungsbereiche);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  history  DOCUMENT ME!
     */
    public void setHistory(final boolean history) {
        this.history = history;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isHistory() {
        return history;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("ausgewählte zeile/spalte" + rowIndex + "/" + columnIndex);
            }
            final VerwaltungsbereichCustomBean vBereich = getCidsBeanAtRow(rowIndex);
            switch (columnIndex) {
                case 0: {
                    return vBereich.getDienststelle();
                }

                case 1: {
                    // if there is only one VerwaltungsbereichCustomBean & the WFS Geometry is used
                    final Integer flaeche;
                    if (isHistory()) {
                        flaeche = vBereich.getFlaeche();
                    } else {
                        flaeche = determineFlaeche(vBereich);
                    }
                    return flaeche;
                }
                default: {
                    return "Spalte ist nicht definiert";
                }
            }
        } catch (Exception ex) {
            LOG.error("Fehler beim abrufen von Daten aus dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   vBereich  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int determineFlaeche(final VerwaltungsbereichCustomBean vBereich) {
        if (getRowCount() == 1) {
            return (int)Math.round(currentWFSSize);
        } else {
            final Geometry tmp = vBereich.getGeometry();
            if (tmp != null) {
                return (int)Math.round(tmp.getArea());
            } else {
                return 0;
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void fillFlaechen() {
        for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex++) {
            final VerwaltungsbereichCustomBean vBereich = getCidsBeanAtRow(rowIndex);
            vBereich.setFlaeche(determineFlaeche(vBereich));
        }
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        // "Fläche m²" is not editable, therefore -1 is needed
        return ((COLUMN_NAMES.length - 1) > columnIndex) && (getRowCount() > rowIndex) && isInEditMode();
    }

    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        try {
            final VerwaltungsbereichCustomBean vBereich = getCidsBeanAtRow(rowIndex);
            switch (columnIndex) {
                case 0: {
                    vBereich.setDienststelle((VerwaltendeDienststelleCustomBean)aValue);
                    break;
                }
                default: {
                    LOG.warn("Keine Spalte für angegebenen Index vorhanden: " + columnIndex);
                    return;
                }
            }
            fireTableDataChangedAndKeepSelection();
        } catch (Exception ex) {
            LOG.error("Fehler beim setzen von Daten in dem Modell: Zeile: " + rowIndex + " Spalte" + columnIndex, ex);
        }
    }
    /**
     * public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
     * int row, int column) { JLabel label = new JLabel(); if(value instanceof VerwaltendeDienststelleCustomBean){
     * VerwaltendeDienststelleCustomBean dienststelle = (VerwaltendeDienststelleCustomBean) value;
     * label.setText(dienststelle.toString()); } else if(value instanceof VerwaltungsgebrauchCustomBean){
     * VerwaltungsgebrauchCustomBean nutzung = (VerwaltungsgebrauchCustomBean) value; label.setText(nutzung.toString());
     * } else{ log.debug("Object ist vom Typ: "+value.getClass()); } return label; }
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<Feature> getAllVerwaltungsFeatures() {
        final ArrayList<Feature> tmp = new ArrayList<Feature>();
        final ArrayList<VerwaltungsbereichCustomBean> verwaltungsbereiche = (ArrayList<VerwaltungsbereichCustomBean>)
            getCidsBeans();
        if (verwaltungsbereiche != null) {
            final Iterator<VerwaltungsbereichCustomBean> it = verwaltungsbereiche.iterator();
            while (it.hasNext()) {
                final VerwaltungsbereichCustomBean curVB = it.next();
                if (curVB.getGeometry() != null) {
                    tmp.add(curVB);
                }
            }
            return tmp;
        } else {
            return tmp;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rowIndex  DOCUMENT ME!
     */
    @Override
    public void removeCidsBean(final int rowIndex) {
        final VerwaltungsbereichCustomBean vBereich = getCidsBeanAtRow(rowIndex);
        if ((vBereich != null) && (vBereich.getGeometry() != null)) {
            LagisBroker.getInstance().getMappingComponent().getFeatureCollection().removeFeature(vBereich);
        }
        super.removeCidsBean(rowIndex);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  currentWFSSize  DOCUMENT ME!
     */
    public void setCurrentWFSSize(final double currentWFSSize) {
        this.currentWFSSize = currentWFSSize;
        fireTableDataChangedAndKeepSelection();
    }
    /**
     * //TODO CHECK FOR BETTER Solution public synchronized void updateAreaInformation(final Double singleSizeUpdate){
     * final int verwaltungsbereicheSize = verwaltungsbereiche.size(); log.debug("Flächeninformation wird geupdated");
     * log.debug("Anzahl Verwaltungsbereiche: "+verwaltungsbereicheSize); try{ Iterator<Verwaltungsbereich> it =
     * verwaltungsbereiche.iterator(); if(verwaltungsbereicheSize == 1 && singleSizeUpdate != null){ log.debug("Nur ein
     * VerwaltungsbereichCustomBean vorhanden"); VerwaltungsbereichCustomBean currentVerwaltungsbereich = it.next();
     * if(currentVerwaltungsbereich != null && (currentVerwaltungsbereich.getFlaeche() ==null ||
     * !currentVerwaltungsbereich.getFlaeche().equals(singleSizeUpdate.intValue()))){ log.debug("Fläche hat sich
     * geändert"); currentVerwaltungsbereich.setFlaeche(singleSizeUpdate.intValue()); fireTableDataChanged(); //TODO
     * setSelection on new Entry } else { log.debug("Fläche hat sich nicht geändert"); } } else
     * if(verwaltungsbereicheSize == 1){ log.debug("Nur ein VerwaltungsbereichCustomBean vorhanden"); log.warn("Es war
     * nicht möglich die Fläche zu updaten weil keine Größe mitgeliefert wurde"); VerwaltungsbereichCustomBean
     * currentVerwaltungsbereich = it.next(); if(currentVerwaltungsbereich != null &&
     * (currentVerwaltungsbereich.getFlaeche() ==null || !currentVerwaltungsbereich.getFlaeche().equals(0))){
     * log.debug("Fläche hat sich geändert"); currentVerwaltungsbereich.setFlaeche(0); fireTableDataChanged(); //TODO
     * setSelection on new Entry } else { log.debug("Fläche hat sich nicht geändert"); } return; }else {
     * log.debug("mehrere Verwaltungsbereiche vorhanden"); while(it.hasNext()){ VerwaltungsbereichCustomBean curBereich
     * = it.next(); if(curBereich.getGeometry() != null){ log.debug("VerwaltungsbereichCustomBean: "+curBereich+" hat
     * eine Fläche --> wird geupdated"); final int area = (int)Math.round(curBereich.getGeometry().getArea());
     * if(curBereich.getFlaeche() ==null || !curBereich.getFlaeche().equals(area)){ log.debug("Fläche hat sich
     * geändert"); curBereich.setFlaeche(area); fireTableDataChanged(); //TODO setSelection on new Entry } else {
     * log.debug("Fläche hat sich nicht geändert"); }} else if(curBereich.getGeometry() == null) {
     * log.debug("VerwaltungsbereichCustomBean: "+curBereich+" hat keine Fläche --> wird geupdated");
     * if(curBereich.getFlaeche() ==null || !curBereich.getFlaeche().equals(0)){ log.debug("Fläche hat sich geändert");
     * curBereich.setFlaeche(0); fireTableDataChanged(); //TODO setSelection on new Entry } else { log.debug("Fläche hat
     * sich nicht geändert"); } } else { log.warn("Keiner der Fälle trifft zu"); } } } }catch(Exception ex){
     * log.error("Fehler beim updaten der Flächeninformation",ex); } }
     */

}
