/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.panels;

import java.awt.Dimension;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.custom.beans.lagis.VerwaltendeDienststelleCustomBean;
import de.cismet.cids.custom.beans.lagis.VerwaltungsbereicheEintragCustomBean;
import de.cismet.cids.custom.beans.lagis.VerwaltungsgebrauchCustomBean;

import de.cismet.lagis.models.VerwaltungsTableModel;

import de.cismet.lagis.renderer.VerwaltendeDienststelleRenderer;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VerwaltungsbereicheHistorieEintragPanel extends javax.swing.JPanel {

    //~ Instance fields --------------------------------------------------------

    private final VerwaltendeDienststelleRenderer vdRenderer = new VerwaltendeDienststelleRenderer();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private de.cismet.lagis.gui.tables.VerwaltungsTable verwaltungsTable1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form VerwaltungsbereicheHistorieEintragPanel.
     *
     * @param  eintrag  DOCUMENT ME!
     */
    public VerwaltungsbereicheHistorieEintragPanel(final VerwaltungsbereicheEintragCustomBean eintrag) {
        initComponents();
        final VerwaltungsTableModel model = new VerwaltungsTableModel();
        model.setHistory(true);
        model.refreshTableModel(eintrag.getN_verwaltungsbereiche());

        verwaltungsTable1.setSelectionModel(new NullSelectionModel());
        verwaltungsTable1.setModel(model);
        verwaltungsTable1.setDefaultRenderer(VerwaltendeDienststelleCustomBean.class, vdRenderer);

        final DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        final String geaendert_am = (eintrag.getGeaendert_am() == null) ? null
                                                                        : formatter.format(eintrag.getGeaendert_am());
        final String geaendert = eintrag.getGeaendert_von();
        final String labelText = ((geaendert_am == null) && (geaendert == null))
            ? "Benutzer und Datum der Änderung unbekannt"
            : ("<html>Änderung" + ((geaendert_am == null) ? "" : (" am " + geaendert_am + "<br/>"))
                        + ((geaendert == null) ? "" : (" von " + geaendert)));
        jLabel1.setText(labelText);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        verwaltungsTable1 = new de.cismet.lagis.gui.tables.VerwaltungsTable();

        setLayout(new java.awt.GridBagLayout());

        jPanel2.setMaximumSize(new java.awt.Dimension(250, 150));
        jPanel2.setMinimumSize(new java.awt.Dimension(250, 0));
        jPanel2.setPreferredSize(new java.awt.Dimension(250, 0));
        jPanel2.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                VerwaltungsbereicheHistorieEintragPanel.class,
                "VerwaltungsbereicheHistorieEintragPanel.jLabel1.text")); // NOI18N
        jLabel1.setMaximumSize(new java.awt.Dimension(150, 17));
        jPanel2.add(jLabel1, java.awt.BorderLayout.NORTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(14, 10, 10, 10);
        add(jPanel2, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.add(verwaltungsTable1, java.awt.BorderLayout.CENTER);
        jPanel1.add(verwaltungsTable1.getTableHeader(), java.awt.BorderLayout.NORTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jPanel1, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public class NullSelectionModel implements ListSelectionModel {

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean isSelectionEmpty() {
            return true;
        }
        @Override
        public boolean isSelectedIndex(final int index) {
            return false;
        }
        @Override
        public int getMinSelectionIndex() {
            return -1;
        }
        @Override
        public int getMaxSelectionIndex() {
            return -1;
        }
        @Override
        public int getLeadSelectionIndex() {
            return -1;
        }
        @Override
        public int getAnchorSelectionIndex() {
            return -1;
        }
        @Override
        public void setSelectionInterval(final int index0, final int index1) {
        }
        @Override
        public void setLeadSelectionIndex(final int index) {
        }
        @Override
        public void setAnchorSelectionIndex(final int index) {
        }
        @Override
        public void addSelectionInterval(final int index0, final int index1) {
        }
        @Override
        public void insertIndexInterval(final int index, final int length, final boolean before) {
        }
        @Override
        public void clearSelection() {
        }
        @Override
        public void removeSelectionInterval(final int index0, final int index1) {
        }
        @Override
        public void removeIndexInterval(final int index0, final int index1) {
        }
        @Override
        public void setSelectionMode(final int selectionMode) {
        }
        @Override
        public int getSelectionMode() {
            return SINGLE_SELECTION;
        }
        @Override
        public void addListSelectionListener(final ListSelectionListener lsl) {
        }
        @Override
        public void removeListSelectionListener(final ListSelectionListener lsl) {
        }
        @Override
        public void setValueIsAdjusting(final boolean valueIsAdjusting) {
        }
        @Override
        public boolean getValueIsAdjusting() {
            return false;
        }
    }
}