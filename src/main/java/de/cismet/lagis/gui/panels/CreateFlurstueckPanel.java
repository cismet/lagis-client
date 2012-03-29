/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * NewBeanForm.java
 *
 * Created on August 8, 2007, 9:53 AM
 */
package de.cismet.lagis.gui.panels;

import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.FlurstueckSchluesselCustomBean;
import de.cismet.cids.custom.beans.verdis_grundis.GemarkungCustomBean;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.models.KeyComboboxModel;

import de.cismet.lagisEE.util.FlurKey;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class CreateFlurstueckPanel extends JPanel implements DocumentListener, ActionListener {

    //~ Instance fields --------------------------------------------------------

    FlurstueckSchluesselCustomBean schluessel;
    private final Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private FlurstueckCustomBean currentFlurstueck = null;
    private FlurstueckSchluesselCustomBean currentFlurstueckSchluessel = FlurstueckSchluesselCustomBean.createNew();
    private boolean isIncomplete = false;
    private GemarkungCustomBean currentGemarkung = null;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreateNewFlurstueck;
    private final javax.swing.JComboBox cboFlur = new javax.swing.JComboBox();
    private final javax.swing.JComboBox cboGemarkung = new javax.swing.JComboBox();
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblGemarkung;
    private javax.swing.JPanel panAll;
    private javax.swing.JTextField txtFlurstueck;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CreateFlurstueckPanel object.
     */
    public CreateFlurstueckPanel() {
        // super(null);
        initComponents();
        cboFlur.setToolTipText("Flur");
        txtFlurstueck.setToolTipText("Flurstueck");
        txtFlurstueck.setEnabled(false);
        cboGemarkung.setEditable(false);
        cboFlur.setEditable(false);
        cboGemarkung.setToolTipText("Gemarkung");
        btnCreateNewFlurstueck.setEnabled(false);
        txtFlurstueck.getDocument().addDocumentListener(this);
        txtFlurstueck.addActionListener(this);
        try {
            final Thread keyRetrieverThread = new Thread() {

                    @Override
                    public void run() {
                        if (log.isDebugEnabled()) {
                            log.debug("Abrufen der Gemarkungen vom Server");
                        }
                        final Collection gemKeys = CidsBroker.getInstance().getGemarkungsKeys();
                        if (gemKeys != null) {
                            final Vector gemKeyList = new Vector(gemKeys);
                            Collections.sort(gemKeyList);
                            cboGemarkung.setModel(new KeyComboboxModel(gemKeyList));
                            cboGemarkung.setEnabled(true);
                        } else {
                            cboGemarkung.setModel(new KeyComboboxModel());
                        }
                    }
                };
            keyRetrieverThread.setPriority(Thread.NORM_PRIORITY);
            keyRetrieverThread.start();
        } catch (Exception ex) {
            log.error("Fehler beim Abrufen der Gemarkungen");
        }
    }

    /**
     * Creates new form NewFlurstueckPanel.
     *
     * @param  schluessel  DOCUMENT ME!
     */
    public CreateFlurstueckPanel(final FlurstueckSchluesselCustomBean schluessel) {
        this.schluessel = schluessel;
        initComponents();
        cboFlur.setToolTipText("Flur");
        txtFlurstueck.setToolTipText("Flurstueck");
        txtFlurstueck.setEnabled(false);
        cboGemarkung.setEditable(false);
        cboFlur.setEditable(false);
        cboGemarkung.setToolTipText("Gemarkung");
        btnCreateNewFlurstueck.setEnabled(false);
        txtFlurstueck.getDocument().addDocumentListener(this);
        txtFlurstueck.addActionListener(this);
        try {
            final Thread keyRetrieverThread = new Thread() {

                    @Override
                    public void run() {
                        if (log.isDebugEnabled()) {
                            log.debug("Abrufen der Gemarkungen vom Server");
                        }
                        final Collection gemKeys = CidsBroker.getInstance().getGemarkungsKeys();
                        if (gemKeys != null) {
                            final Vector gemKeyList = new Vector(gemKeys);
                            Collections.sort(gemKeyList);
                            cboGemarkung.setModel(new KeyComboboxModel(gemKeyList));
                            cboGemarkung.setEnabled(true);
                        } else {
                            cboGemarkung.setModel(new KeyComboboxModel());
                        }
                        if (schluessel != null) {
                            if (log.isDebugEnabled()) {
                                log.debug("Current Gemarkung: " + schluessel.getGemarkung());
                            }
                            cboGemarkung.setSelectedItem(schluessel.getGemarkung());
                            cboGemarkung.getEditor().setItem(schluessel.getGemarkung());
                        }
                    }
                };
            keyRetrieverThread.setPriority(Thread.NORM_PRIORITY);
            keyRetrieverThread.start();
        } catch (Exception ex) {
            log.error("Fehler beim Abrufen der Gemarkungen");
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        panAll = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        btnCreateNewFlurstueck = new javax.swing.JButton();
        txtFlurstueck = new javax.swing.JTextField();
        lblGemarkung = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(20, 20));
        panAll.setLayout(new java.awt.BorderLayout());

        panAll.setBorder(javax.swing.BorderFactory.createTitledBorder(
                null,
                "",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.BOTTOM));
        panAll.setPreferredSize(new java.awt.Dimension(50, 100));
        jScrollPane1.setBorder(null);
        jPanel1.setPreferredSize(new java.awt.Dimension(20, 20));
        btnCreateNewFlurstueck.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/ressource/icons/toolbar/ok.png")));
        btnCreateNewFlurstueck.setBorder(null);
        btnCreateNewFlurstueck.setBorderPainted(false);
        btnCreateNewFlurstueck.setContentAreaFilled(false);
        btnCreateNewFlurstueck.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCreateNewFlurstueckActionPerformed(evt);
                }
            });

        cboFlur.setEditable(true);
        cboFlur.setMaximumSize(new java.awt.Dimension(90, 23));
        cboFlur.setMinimumSize(new java.awt.Dimension(90, 23));
        cboFlur.setPreferredSize(new java.awt.Dimension(90, 23));
        cboFlur.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboFlurActionPerformed(evt);
                }
            });

        cboGemarkung.setEditable(true);
        cboGemarkung.setMaximumSize(new java.awt.Dimension(90, 23));
        cboGemarkung.setMinimumSize(new java.awt.Dimension(90, 23));
        cboGemarkung.setPreferredSize(new java.awt.Dimension(90, 23));
        cboGemarkung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboGemarkungActionPerformed(evt);
                }
            });

        lblGemarkung.setText("Gemarkung");

        jLabel2.setText("Flur");

        jLabel3.setText("Flurstueck");

        final org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel1Layout.createSequentialGroup().addContainerGap().add(
                    jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        cboGemarkung,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(lblGemarkung)).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                        cboFlur,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(jLabel2)).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jLabel3).add(
                        txtFlurstueck,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        90,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                    org.jdesktop.layout.LayoutStyle.RELATED).add(
                    btnCreateNewFlurstueck,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    32,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap(
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                jPanel1Layout.createSequentialGroup().addContainerGap().add(
                    jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING).add(
                        jPanel1Layout.createSequentialGroup().add(
                            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                                jLabel2).add(jLabel3).add(lblGemarkung)).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED).add(
                            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                                cboGemarkung,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                                cboFlur,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
                                txtFlurstueck,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                22,
                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))).add(
                        btnCreateNewFlurstueck,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        33,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addContainerGap(28, Short.MAX_VALUE)));
        jScrollPane1.setViewportView(jPanel1);

        panAll.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        final org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().addContainerGap().add(
                    panAll,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    348,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap(
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().addContainerGap().add(
                    panAll,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    95,
                    Short.MAX_VALUE).addContainerGap()));
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCreateNewFlurstueckActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCreateNewFlurstueckActionPerformed
        try {
            if (log.isDebugEnabled()) {
                log.debug("create flurstück performed");
            }
            // lblStatus.setText("");
            ((TitledBorder)panAll.getBorder()).setTitle("");
            final FlurstueckSchluesselCustomBean checkedKey = CidsBroker.getInstance()
                        .completeFlurstueckSchluessel(currentFlurstueckSchluessel);
            if (checkedKey != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Flurstück ist bereits vorhanden");
                }
                // ((lblStatus.setText("Flurstück ist bereits vorhanden, bitte wählen Sie einen anderen Schlüssel.");
                ((TitledBorder)panAll.getBorder()).setTitle(
                    "Flurstück ist bereits vorhanden, bitte wählen Sie einen anderen Schlüssel.");
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Flurstück ist noch nicht vorhanden und kann angelegt werden");
                }
                final FlurstueckCustomBean newFlurstueck = CidsBroker.getInstance()
                            .createFlurstueck(currentFlurstueckSchluessel);
                if (newFlurstueck != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Id des neuen Flurstücks" + newFlurstueck.getId());
                    }
                    // LagisBroker.getInstance().fireFlurstueckChanged(newFlurstueck);
                    LagisBroker.getInstance().loadFlurstueck(newFlurstueck.getFlurstueckSchluessel());
                    StaticSwingTools.getParentFrame(this).dispose();
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Neues Flurstücks ist null");
                        log.debug("Fehler beim Anlegen den Flurstuecks.");
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Fehler beim anlegen des Flurstuecks: " + ex);
        }
    } //GEN-LAST:event_btnCreateNewFlurstueckActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboFlurActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboFlurActionPerformed
        if ((evt != null) && (evt.getSource() instanceof JComboBox)) {
            if (evt.getActionCommand().equals("comboBoxChanged")) {
                final Object selectedItem = cboFlur.getSelectedItem();
                if ((selectedItem != null) && (selectedItem instanceof FlurKey)) {
                    if (log.isDebugEnabled()) {
                        log.debug("selected Item: " + selectedItem);
                        log.debug("selected Item: " + ((FlurKey)selectedItem).getFlurId());
                    }
                    currentFlurstueckSchluessel.setFlur(((FlurKey)selectedItem).getFlurId());
                    lockFlurstueckCbo(false);
                }
            } else if (evt.getActionCommand().equals("comboBoxEdited")) {
            }
        }
    }                                                                           //GEN-LAST:event_cboFlurActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboGemarkungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboGemarkungActionPerformed
        try {
            isIncomplete = false;
            currentFlurstueckSchluessel = null;
            lockFlurCbo(true);
            lockFlurstueckCbo(true);
            currentFlurstueckSchluessel = FlurstueckSchluesselCustomBean.createNew();
            // reset the Flur/FlurstueckCombobox cboFlur.setModel(new KeyComboboxModel()); cboFlur.setEnabled(false);
            // cboFlurstueck.setModel(new KeyComboboxModel()); cboFlurstueck.setEnabled(false);

            final GemarkungCustomBean current;
            final boolean handMade;
            final boolean unknownKey;

            if ((evt != null) && (evt.getSource() instanceof JComboBox)) {
                // if it is selected or edited
                if (evt.getActionCommand().equals("comboBoxChanged")) {
                    if (log.isDebugEnabled()) {
                        log.debug("GemarkungChanged");
                    }

                    final Object selectedItem = cboGemarkung.getSelectedItem();
                    if (selectedItem instanceof GemarkungCustomBean) {
                        current = (GemarkungCustomBean)selectedItem;
                        currentGemarkung = current;
                    } else {
                        current = null;
                        currentGemarkung = null;

                        return;
                    }

                    // is Not edited
                    handMade = false;
                    // is Not out of the DB
                    unknownKey = false;

//                } else if (evt.getActionCommand().equals("comboBoxEdited")){
//                    isIncomplete = true;
//                    log.debug("GemarkungEdited");
//                    current = new GemarkungCustomBean();
//                    //TODO I have to query all gemarkungen because the user can enter either the key or the name but i cant query the wfs with only the name
//                    //Seems to work
//
//                    //Get the text
//                    String gemInput = ((JComboBox) evt.getSource()).getEditor().getItem().toString();
//                    try{
//                        //is a key
//                        current.setSchluessel(Integer.parseInt(gemInput));
//                    }catch(NumberFormatException ex){
//                        //is a bezeichnung
//                        if(gemInput != null && gemInput.equals("")){
//                            currentGemarkung = null;
//
//                            cboGemarkung.setSelectedItem(null);
//                            return;
//                        } else {
//                            current.setBezeichnung(gemInput);
//                        }
//                    }
//
//                    if(current.getSchluessel() != null){
//                        unknownKey = true;
//                    } else{
//                        unknownKey = false;
//                    }
//
//                    handMade=true;
                } else {
                    current = null;
                    currentGemarkung = null;

                    handMade = false;
                    unknownKey = false;
                }

                if (current != null) {
                    final Thread keyRetrieverThread = new Thread() {

                            @Override
                            public void run() {
                                if (log.isDebugEnabled()) {
                                    log.debug("Abrufen der FlurKeys vom Server");
                                }
                                // if(isIncomplete){
                                // log.debug("isIncomplete(Thread)");
                                currentGemarkung = CidsBroker.getInstance().completeGemarkung(current);
                                if (currentGemarkung == null) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("liefert keine komplette Germarkung (Thread)");
                                    }
                                    currentGemarkung = current;
                                }
//                                } else {
//                                    log.debug("liefert komplette Germarkung (Thread)");
//                                    cboGemarkung.setSelectedItem(gemarkung);
//                                    return;
//                                }
                                // }
                                currentFlurstueckSchluessel.setGemarkung(currentGemarkung);
                                final Collection flurKeys = CidsBroker.getInstance()
                                            .getDependingKeysForKey(currentGemarkung);
                                KeyComboboxModel keyModel;
                                if (flurKeys != null) {
                                    final Vector flurKeyList = new Vector(flurKeys);
                                    Collections.sort(flurKeyList);
                                    keyModel = new KeyComboboxModel(flurKeyList);
                                } else {
                                    keyModel = new KeyComboboxModel();
                                }
                                // KeyComboboxModel keyModel = new KeyComboboxModel(new
                                // Vector<Key>(CidsBroker.getInstance().getKeysDependingOnKey((currentGemarkung))));
                                cboFlur.setModel(keyModel);
                                lockFlurCbo(false);
                                cboFlur.requestFocus();
                                // edited && no entries in Database
                                if (handMade && (keyModel.getSize() == 0)) {
                                    if (unknownKey) {
                                        // cboGemarkung.getEditor().getEditorComponent().setBackground(UNKOWN_COLOR);
                                        // ((DefaultComboBoxModel)cboGemarkung.getModel()).addElement(current);
                                        // cboGemarkung.setSelectedItem(current);
                                    } else {
                                        // no chance to find flurstueck by name

                                        // cboGemarkung.getEditor().getEditorComponent().setBackground(Color.RED);
                                    }
                                } else {
                                    // everything ok;

                                    // cboGemarkung.setSelectedItem(current);
                                    // cboGemarkung.getEditor().getEditorComponent().setBackground(ACCEPTED_COLOR);
                                }
                            }
                        };
                    keyRetrieverThread.setPriority(Thread.NORM_PRIORITY);
                    keyRetrieverThread.start();
                }
            }
        } catch (Exception ex) {
            log.error("Fehler beim setzen der Gemarkung", ex);
        }
    } //GEN-LAST:event_cboGemarkungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  flur  DOCUMENT ME!
     */
    public void lockFlurCbo(final boolean flur) {
        if (flur) {
            cboFlur.setModel(new KeyComboboxModel());
            ;
            cboFlur.setEnabled(false);
        } else {
            cboFlur.setEnabled(true);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueck  DOCUMENT ME!
     */
    public void lockFlurstueckCbo(final boolean flurstueck) {
        if (flurstueck) {
            txtFlurstueck.setEnabled(false);
            txtFlurstueck.setText("");
            setHighlightColor(LagisBroker.ACCEPTED_COLOR);
        } else {
            txtFlurstueck.setEnabled(true);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  color  DOCUMENT ME!
     */
    public void setHighlightColor(final Color color) {
        cboGemarkung.getEditor().getEditorComponent().setBackground(color);
        cboFlur.getEditor().getEditorComponent().setBackground(color);
        txtFlurstueck.setBackground(color);
    }

    @Override
    public void removeUpdate(final DocumentEvent e) {
        final String text = txtFlurstueck.getText();
        if (log.isDebugEnabled()) {
            log.debug("Flurstück String changed: " + text);
        }
        if (text != null) {
            if (text.length() != 0) {
                final String[] tokens = text.split("/");
                if (log.isDebugEnabled()) {
                    // TODO the user input is not validated
                    log.debug("Anzahl teile der Flurstücksid: " + tokens.length);
                }
                try {
                    switch (tokens.length) {
                        case 1: {
                            if (log.isDebugEnabled()) {
                                log.debug("Eine Zahl");
                            }
                            // Integer.parseInt(tokens[0]);
                            currentFlurstueckSchluessel.setFlurstueckZaehler(Integer.parseInt(tokens[0]));
                            currentFlurstueckSchluessel.setFlurstueckNenner(0);
                            setHighlightColor(LagisBroker.SUCCESSFUL_COLOR);
                            btnCreateNewFlurstueck.setEnabled(true);
                            break;
                        }
                        case 2: {
                            if (log.isDebugEnabled()) {
                                log.debug("Zwei Zahlen");
                            }
                            // Integer.parseInt(tokens[0]);
                            // Integer.parseInt(tokens[1]);
                            currentFlurstueckSchluessel.setFlurstueckZaehler(Integer.parseInt(tokens[0]));
                            currentFlurstueckSchluessel.setFlurstueckNenner(Integer.parseInt(tokens[1]));
                            setHighlightColor(LagisBroker.SUCCESSFUL_COLOR);
                            btnCreateNewFlurstueck.setEnabled(true);
                            break;
                        }
                        default: {
                            log.warn("Falsche Eingabe erwarted wird ein Flurstueck ohne oder mit Nenner z.B. 10\n");
                            setHighlightColor(LagisBroker.ERROR_COLOR);
                            btnCreateNewFlurstueck.setEnabled(false);
                        }
                    }
                } catch (Exception ex) {
                    log.error("Fehler beim parsen des Flurstück Zähler/Nenner", ex);
                    setHighlightColor(LagisBroker.ERROR_COLOR);
                    btnCreateNewFlurstueck.setEnabled(false);
                }
            } else {
                btnCreateNewFlurstueck.setEnabled(false);
            }
        } else {
            btnCreateNewFlurstueck.setEnabled(false);
        }
    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
        final String text = txtFlurstueck.getText();
        if (log.isDebugEnabled()) {
            log.debug("Flurstück String changed: " + text);
        }
        if (text != null) {
            if (text.length() != 0) {
                final String[] tokens = text.split("/");
                if (log.isDebugEnabled()) {
                    // TODO the user input is not validated
                    log.debug("Anzahl teile der Flurstücksid: " + tokens.length);
                }
                try {
                    switch (tokens.length) {
                        case 1: {
                            if (log.isDebugEnabled()) {
                                log.debug("Eine Zahl");
                            }
                            // Integer.parseInt(tokens[0]);
                            currentFlurstueckSchluessel.setFlurstueckZaehler(Integer.parseInt(tokens[0]));
                            currentFlurstueckSchluessel.setFlurstueckNenner(0);
                            setHighlightColor(LagisBroker.SUCCESSFUL_COLOR);
                            btnCreateNewFlurstueck.setEnabled(true);
                            break;
                        }
                        case 2: {
                            if (log.isDebugEnabled()) {
                                log.debug("Zwei Zahlen");
                            }
                            // Integer.parseInt(tokens[0]);
                            // Integer.parseInt(tokens[1]);
                            currentFlurstueckSchluessel.setFlurstueckZaehler(Integer.parseInt(tokens[0]));
                            currentFlurstueckSchluessel.setFlurstueckNenner(Integer.parseInt(tokens[1]));
                            setHighlightColor(LagisBroker.SUCCESSFUL_COLOR);
                            btnCreateNewFlurstueck.setEnabled(true);
                            break;
                        }
                        default: {
                            log.warn("Falsche Eingabe erwarted wird ein Flurstueck ohne oder mit Nenner z.B. 10\n");
                            setHighlightColor(LagisBroker.ERROR_COLOR);
                            btnCreateNewFlurstueck.setEnabled(false);
                        }
                    }
                } catch (Exception ex) {
                    log.error("Fehler beim parsen des Flurstück Zähler/Nenner", ex);
                    setHighlightColor(LagisBroker.ERROR_COLOR);
                    btnCreateNewFlurstueck.setEnabled(false);
                }
            } else {
                btnCreateNewFlurstueck.setEnabled(false);
            }
        } else {
            btnCreateNewFlurstueck.setEnabled(false);
        }
    }

    @Override
    public void changedUpdate(final DocumentEvent e) {
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() instanceof JTextField) {
            if (btnCreateNewFlurstueck.isEnabled()) {
                // Is this the proper way ?? I
                btnCreateNewFlurstueckActionPerformed(new ActionEvent(this, 1, "createNewFlurstueck"));
            }
        }
    }
}
