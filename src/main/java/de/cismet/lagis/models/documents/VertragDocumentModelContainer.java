/*
 * VertragDocumentModelContainer.java
 *
 * Created on 27. April 2007, 10:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.lagis.models.documents;

import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.broker.LagisBroker;
import de.cismet.lagis.models.BeschluesseTableModel;
import de.cismet.lagis.models.KostenTableModel;
import de.cismet.lagis.models.VertraegeTableModel;
import de.cismet.lagisEE.entity.core.Beschluss;
import de.cismet.lagisEE.entity.core.Kosten;
import de.cismet.lagisEE.entity.core.Vertrag;
import de.cismet.lagisEE.entity.core.hardwired.Vertragsart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.text.BadLocationException;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Puhl
 */
public class VertragDocumentModelContainer implements MouseListener,ActionListener {
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private DecimalFormat df = LagisBroker.getCurrencyFormatter();
    private DateFormat dateFormatter = LagisBroker.getDateFormatter();
    private Vertrag currentSelectedVertrag = null;
    private VertraegeTableModel vertraegeTableModel;
    private AmountDocumentModel kaufpreisDocumentModel;
    private AmountDocumentModel quadPreisDocumentModel;
    private SimpleDocumentModel voreigentuemerDocumentModel;
    private SimpleDocumentModel aktenzeichenDocumentModel;
    private DateDocumentModel auflassungDocumentModel;
    private DateDocumentModel eintragungDocumentModel;
    private SimpleDocumentModel bemerkungDocumentModel;
    private DefaultComboBoxModel vertragsartComboBoxModel;
    private KostenTableModel kostenTableModel;
    private BeschluesseTableModel beschluesseTableModel;
    
    //private Simple
    
    //private
    
    /** Creates a new instance of VertragDocumentModelContainer */
    public VertragDocumentModelContainer(VertraegeTableModel vertraegeTableModel) {
        this.vertraegeTableModel = vertraegeTableModel;
        initDocumentModels();
    }
    
    private void initDocumentModels(){
        kaufpreisDocumentModel = new AmountDocumentModel(){
            public void assignValue(Double betrag){
                log.debug("amount assinged");
                if(currentSelectedVertrag != null){
                    currentSelectedVertrag.setGesamtpreis(betrag);
                    vertraegeTableModel.fireTableDataChanged();
                }
            }
        };
        
        quadPreisDocumentModel = new AmountDocumentModel(){
            public void assignValue(Double betrag){
                log.debug("amount assinged");
                if(currentSelectedVertrag != null){
                    currentSelectedVertrag.setQuadratmeterpreis(betrag);
                    vertraegeTableModel.fireTableDataChanged();
                }
            }
        };
        
        auflassungDocumentModel = new  DateDocumentModel(){
            public void assignValue(Date date) {
                log.debug("Date assinged");
                if(currentSelectedVertrag != null){
                    currentSelectedVertrag.setDatumAuflassung(date);
                }
            }
        };
        
        eintragungDocumentModel = new  DateDocumentModel(){
            public void assignValue(Date date) {
                log.debug("Date assinged");
                if(currentSelectedVertrag != null){
                    currentSelectedVertrag.setDatumEintragung(date);
                }
            }
        };
        
        voreigentuemerDocumentModel = new SimpleDocumentModel(){
            public void assignValue(String newValue) {
                log.debug("voreigentuemer assigned");
                log.debug("new Value: " + newValue);
                valueToCheck=newValue;
                fireValidationStateChanged(this);
                if(currentSelectedVertrag != null && getStatus() == VALID){
                    currentSelectedVertrag.setVertragspartner(newValue);
                }
            }
        };
        
        aktenzeichenDocumentModel = new SimpleDocumentModel(){
            public void assignValue(String newValue) {
                log.debug("aktenzeichen assigned");
                log.debug("new Value: " + newValue);
                valueToCheck=newValue;
                fireValidationStateChanged(this);
                if(currentSelectedVertrag != null && getStatus() == VALID){
                    currentSelectedVertrag.setAktenzeichen(newValue);
                    vertraegeTableModel.fireTableDataChanged();
                }
            }
        };
        
        bemerkungDocumentModel = new SimpleDocumentModel(){
            public void assignValue(String newValue) {
                log.debug("Bemerkung assigned");
                log.debug("new Value: " + newValue);
                valueToCheck=newValue;
                fireValidationStateChanged(this);
                if(currentSelectedVertrag != null && getStatus() == VALID){
                    currentSelectedVertrag.setBemerkung(newValue);
                }
            }
        };
        Set vertragsarten = EJBroker.getInstance().getAllVertragsarten();
        if(vertragsarten != null){
            vertragsartComboBoxModel = new DefaultComboBoxModel(new Vector(vertragsarten));
        } else {
            vertragsartComboBoxModel = new DefaultComboBoxModel();
        }
        beschluesseTableModel = new BeschluesseTableModel();
        kostenTableModel = new KostenTableModel();
    }
    
    public void actionPerformed(ActionEvent e) {
        if(currentSelectedVertrag != null){
            currentSelectedVertrag.setVertragsart((Vertragsart)vertragsartComboBoxModel.getSelectedItem());
            vertraegeTableModel.fireTableDataChanged();
        }
    }
    
    
    public void mouseReleased(MouseEvent e) {
    }
    
    public void mousePressed(MouseEvent e) {
    }
    
    public void mouseExited(MouseEvent e) {
    }
    
    public void mouseEntered(MouseEvent e) {
    }
    
    public void mouseClicked(MouseEvent e) {
        Object source = e.getSource();
        log.debug("source: "+source);
        if(source instanceof JXTable){
            JXTable table = (JXTable) source;
            int currentRow = table.getSelectedRow();
            int currentColumn = table.getSelectedColumn();
            log.debug("Row: "+currentRow);
            if(currentRow != -1){
                currentRow = table.getFilters().convertRowIndexToModel(currentRow);
                currentSelectedVertrag = vertraegeTableModel.getVertragAtRow(currentRow);
                try {
                    kaufpreisDocumentModel.clear(0,kaufpreisDocumentModel.getLength());
                    if(currentSelectedVertrag.getGesamtpreis()!= null){
                        kaufpreisDocumentModel.insertString(0,df.format(currentSelectedVertrag.getGesamtpreis()),null);
                    }else{
                        kaufpreisDocumentModel.insertString(0,"",null);
                    }
                    
                    auflassungDocumentModel.clear(0,auflassungDocumentModel.getLength());
                    if(currentSelectedVertrag.getDatumAuflassung() != null){
                        auflassungDocumentModel.insertString(0,dateFormatter.format(currentSelectedVertrag.getDatumAuflassung()),null);
                    } else{
                        auflassungDocumentModel.insertString(0,"",null);
                    }
                    
                    
                    eintragungDocumentModel.clear(0,eintragungDocumentModel.getLength());
                    if(currentSelectedVertrag.getDatumEintragung() != null){
                        eintragungDocumentModel.insertString(0,dateFormatter.format(currentSelectedVertrag.getDatumEintragung()),null);
                    }else{
                        eintragungDocumentModel.insertString(0,"",null);
                    }
                    
                    voreigentuemerDocumentModel.clear(0,voreigentuemerDocumentModel.getLength());
                    voreigentuemerDocumentModel.insertString(0,currentSelectedVertrag.getVertragspartner(),null);
                    
                    aktenzeichenDocumentModel.clear(0,aktenzeichenDocumentModel.getLength());
                    aktenzeichenDocumentModel.insertString(0,currentSelectedVertrag.getAktenzeichen(),null);
                    
                    bemerkungDocumentModel.clear(0,bemerkungDocumentModel.getLength());
                    bemerkungDocumentModel.insertString(0,currentSelectedVertrag.getBemerkung(),null);
                    
                    quadPreisDocumentModel.clear(0,quadPreisDocumentModel.getLength());
                    if(currentSelectedVertrag.getQuadratmeterpreis() != null){
                        quadPreisDocumentModel.insertString(0,df.format(currentSelectedVertrag.getQuadratmeterpreis()),null);
                    }else{
                        quadPreisDocumentModel.insertString(0,"",null);
                    }
                    
                    vertragsartComboBoxModel.setSelectedItem(currentSelectedVertrag.getVertragsart());
                    
                    kostenTableModel.refreshTableModel(currentSelectedVertrag.getKosten());
                    //beschluesseTableModel = new BeschluesseTableModel(currentSelectedVertrag.getBeschluesse());
                    beschluesseTableModel.refreshTableModel(currentSelectedVertrag.getBeschluesse());
                    table.changeSelection(table.getFilters().convertRowIndexToView(currentRow),currentColumn,false,false);
                } catch (BadLocationException ex) {
                    //TODO Böse
                    ex.printStackTrace();
                }
            } else {
                log.debug("nichts selektiert lösche Felder");
                currentSelectedVertrag = null;
                clearComponents();
            }
        }
    }
    
    public Vertrag getCurrentSelectedVertrag(){
        return currentSelectedVertrag;
    }
    
    public AmountDocumentModel getKaufpreisDocumentModel() {
        return kaufpreisDocumentModel;
    }
    
    public void updateTableModel(VertraegeTableModel vertraegeTableModel){
        this.vertraegeTableModel = vertraegeTableModel;
    }
    
    public void clearComponents(){
        try{
            kaufpreisDocumentModel.clear(0,kaufpreisDocumentModel.getLength());
            quadPreisDocumentModel.clear(0,quadPreisDocumentModel.getLength());
            voreigentuemerDocumentModel.clear(0,voreigentuemerDocumentModel.getLength());
            aktenzeichenDocumentModel.clear(0,aktenzeichenDocumentModel.getLength());
            auflassungDocumentModel.clear(0,auflassungDocumentModel.getLength());
            eintragungDocumentModel.clear(0,eintragungDocumentModel.getLength());
            bemerkungDocumentModel.clear(0,bemerkungDocumentModel.getLength());
            kostenTableModel.refreshTableModel(null);
            beschluesseTableModel.refreshTableModel(null);
        }catch(Exception ex){
            
        }
    }
    
    public DateDocumentModel getAuflassungDocumentModel() {
        return auflassungDocumentModel;
    }
    
    public DateDocumentModel getEintragungDocumentModel() {
        return eintragungDocumentModel;
    }
    
    public SimpleDocumentModel getVoreigentuemerDocumentModel(){
        return voreigentuemerDocumentModel;
    }
    
    public SimpleDocumentModel getAktenzeichenDocumentModel(){
        return aktenzeichenDocumentModel;
    }
    
    public SimpleDocumentModel getBemerkungDocumentModel(){
        return bemerkungDocumentModel;
    }
    
    public AmountDocumentModel getQuadPreisDocumentModel() {
        return quadPreisDocumentModel;
    }
    
    public DefaultComboBoxModel getVertragsartComboBoxModel(){
        return vertragsartComboBoxModel;
    }
    
    public KostenTableModel getKostenTableModel(){
        return kostenTableModel;
    }
    
    public void addNewBeschluss(){        
        if(currentSelectedVertrag != null){            
            Beschluss beschluss = new Beschluss();
            beschluesseTableModel.addBeschluss(beschluss);            
            Set<Beschluss> tmpBeschluesse = currentSelectedVertrag.getBeschluesse();
            if(tmpBeschluesse == null){
                tmpBeschluesse = new HashSet<Beschluss>();
            } else {
                tmpBeschluesse.clear();
            }
            tmpBeschluesse.addAll(beschluesseTableModel.getBeschluesse());
            currentSelectedVertrag.setBeschluesse(tmpBeschluesse);
            beschluesseTableModel.fireTableDataChanged();
            log.debug("Neuer Beschluss angelegt");
        } else {
            log.debug("Es konnte kein Beschluss angelegt werden --> currentSelected Vertrag = null");
        }        
    }
    
    public void addNewKosten(){        
        if(currentSelectedVertrag != null){            
            Kosten kosten = new Kosten();
            kostenTableModel.addKosten(kosten);            
            Set<Kosten> tmpKosten = currentSelectedVertrag.getKosten();
            if(tmpKosten == null){
                tmpKosten = new HashSet<Kosten>();
            } else {
                tmpKosten.clear();
            }
            tmpKosten.addAll(kostenTableModel.getKosten());
            currentSelectedVertrag.setKosten(tmpKosten);
            kostenTableModel.fireTableDataChanged();
            log.debug("Neue Kosten angelegt");
        } else {
            log.debug("Es konnten keine neue Kosten angelegt werden --> currentSelected Vertrag = null");
        }        
    }
    
    public void removeKosten(int kostenToRemove){        
        if(currentSelectedVertrag != null || kostenToRemove == -1){            
            Kosten kosten = new Kosten();
            Kosten tmpKosten = kostenTableModel.getKostenAtRow(kostenToRemove);
            kostenTableModel.removeKosten(kostenToRemove);       
            Set<Kosten> allKosten = currentSelectedVertrag.getKosten();
            allKosten.remove(tmpKosten);           
            kostenTableModel.fireTableDataChanged();
            log.debug("Kosten wurden entfernt");            
        } else {
            log.debug("Kosten konnten nicht entfernt werden --> currentSelected Vertrag = null oder wert = -1");
        }        
    }
    
    public void removeBeschluss(int beschlussToRemove){        
        if(currentSelectedVertrag != null || beschlussToRemove == -1){            
            Beschluss beschluss = new Beschluss();
            Beschluss tmpBeschluss = beschluesseTableModel.getBeschlussAtRow(beschlussToRemove);
            beschluesseTableModel.removeBeschluss(beschlussToRemove);       
            Set<Beschluss> allBeschluesse = currentSelectedVertrag.getBeschluesse();
            allBeschluesse.remove(tmpBeschluss);           
            beschluesseTableModel.fireTableDataChanged();
            log.debug("Beschluss wurde entfernt");            
        } else {
            log.debug("Beschluss konnten nicht entfernt werden --> currentSelected Vertrag = null oder wert = -1");
        }        
    }
    
    
    public boolean isVertragSelected(){
        return currentSelectedVertrag != null;
    }
    public BeschluesseTableModel getBeschluesseTableModel(){
        return beschluesseTableModel;
    }
    
}
