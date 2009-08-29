/*
 * RenameActionPanel.java
 *
 * Created on 9. September 2007, 17:15
 */

package de.cismet.lagis.wizard.panels;

import de.cismet.lagis.broker.EJBroker;
import de.cismet.lagis.gui.panels.FlurstueckChooser;
import de.cismet.lagis.validation.Validatable;
import de.cismet.lagis.validation.ValidationStateChangedListener;
import de.cismet.lagisEE.entity.core.FlurstueckSchluessel;
import de.cismet.lagisEE.entity.locking.Sperre;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import org.apache.log4j.Logger;
import org.netbeans.spi.wizard.WizardController;

/**
 *
 * @author  Sebastian Puhl
 */
public class RenameActionPanel extends javax.swing.JPanel implements ValidationStateChangedListener,ActionListener{
    public static final String KEY_CREATE_CANDIDATE = "createCandidate";
    public static final String KEY_RENAME_CANDIDATE = "renameCreateCandidate";
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private WizardController wizardController;
    private Map wizardData;
    
    /** Creates new form RenameActionPanel */
    public RenameActionPanel(WizardController wizardController, Map wizardData) {
        initComponents();
        this.wizardController = wizardController;
        this.wizardData = wizardData;
        wizardController.setProblem("Bitte wählen Sie das Flurstück aus, das umbenannt werden soll");
        panRename.addValidationStateChangedListener(this);
        panCreate.addValidationStateChangedListener(this);
        panRename.addComboBoxListener(this);        
    }
    
    public void validationStateChanged(Object validatedObject) {
        log.debug("Object to validate: "+validatedObject);
        if(validatedObject.equals(panRename)){
            log.debug("panRename validation");
            if(!(panRename.getStatus()==Validatable.VALID)){
                log.debug("Rename unvalid");
                wizardController.setProblem(panRename.getValidationMessage());                
                return;
            } else {
                log.debug("Rename valid");                
                FlurstueckSchluessel key = panRename.getCurrentFlurstueckSchluessel();
                if(key != null){
                    log.debug("Vorauswahl kann getroffen werden");
                    panCreate.doAutomaticRequest(FlurstueckChooser.AutomaticFlurstueckRetriever.COPY_CONTENT_MODE,key);
                } else {
                    log.debug("Vorauswahl kann nicht getroffen werden");                    
                }
            }
        } else {
            log.debug("panCreate Validation");
            if(!(panCreate.getStatus()==Validatable.VALID)){
                wizardController.setProblem(panCreate.getValidationMessage());
                return;
            }
        }
        
        if(panCreate.getStatus() == Validatable.VALID && panRename.getStatus() == Validatable.VALID){
            Sperre sperre = EJBroker.getInstance().isLocked(panRename.getCurrentFlurstueckSchluessel());
            if(sperre != null){
                //TODO nicht ganz sichtbar
                wizardController.setProblem("Ausgewähltes Flurstück ist gesperrt von Benutzer: "+sperre.getBenutzerkonto());
                return;
            } else {
                //Damit umbenanntes Flurstück den selben Typ hat als das alte 
                panCreate.getCurrentFlurstueckSchluessel().setFlurstueckArt(panRename.getCurrentFlurstueckSchluessel().getFlurstueckArt());
                wizardData.put(KEY_CREATE_CANDIDATE,panCreate.getCurrentFlurstueckSchluessel());
                wizardData.put(KEY_RENAME_CANDIDATE,panRename.getCurrentFlurstueckSchluessel());
                wizardController.setProblem(null);
                wizardController.setForwardNavigationMode(wizardController.MODE_CAN_FINISH);
            }
        } else {
            wizardController.setProblem(panCreate.getValidationMessage());
            return;
        }
    }
    
    public void actionPerformed(ActionEvent e) {
//        if(e.getSource() instanceof JComboBox){
//            log.debug("event kommt von Combobox");
//            JComboBox currentBox = (JComboBox)e.getSource();
//            if(e.getActionCommand().equals("comboBoxChanged")){
//                log.debug("comboboxChanged");
//                log.debug("Name der Combobox: "+currentBox.getName());
//                if("cboGemarkung".equals(currentBox.getName())){
//                    log.debug("Setze Gemarkung bei umzubenenendes Flurstück");
//                    panCreate.getCboGemarkung().setSelectedItem(currentBox.getSelectedItem());
//                } else if("cboFlur".equals(currentBox.getName())){
//                    log.debug("Setze Flur bei umzubenenendes Flurstück");
//                    panCreate.getCboFlur().setSelectedItem(currentBox.getSelectedItem());
//                }            
//            } else {
//                log.debug("Kein Combobox Changed");
//            }
//        }
    }       
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        panCreate = new FlurstueckChooser(FlurstueckChooser.CREATION_MODE);
        panRename = new de.cismet.lagis.gui.panels.FlurstueckChooser();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 381, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 58, Short.MAX_VALUE)
        );

        jLabel1.setText("Altes Flurst\u00fcck");

        jLabel2.setText("Neues Flurst\u00fcck");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(panCreate, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                    .addComponent(panRename, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panRename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private de.cismet.lagis.gui.panels.FlurstueckChooser panCreate;
    private de.cismet.lagis.gui.panels.FlurstueckChooser panRename;
    // End of variables declaration//GEN-END:variables
    
    
}
