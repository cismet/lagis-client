/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.lagis.gui.panels;

import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

import java.awt.Component;
import java.awt.event.MouseEvent;

import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SortOrder;
import javax.swing.SwingWorker;
import javax.swing.table.TableModel;

import de.cismet.cids.custom.beans.lagis.FlurstueckCustomBean;
import de.cismet.cids.custom.beans.lagis.FlurstueckSchluesselCustomBean;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.lagis.action.CreateMeldungServerAction;
import de.cismet.lagis.action.FinishMeldungServerAction;

import de.cismet.lagis.broker.CidsBroker;
import de.cismet.lagis.broker.LagisBroker;

import de.cismet.lagis.gui.main.LagisApp;

import de.cismet.lagis.interfaces.FlurstueckChangeListener;

import de.cismet.lagis.models.MeldungenTableModel;

import de.cismet.lagis.server.search.MeldungenSearch;

import de.cismet.lagis.widget.AbstractWidget;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class MeldungenPanel extends AbstractWidget implements FlurstueckChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final String WIDGET_NAME = "Meldungen";
    private static final MeldungenPanel INSTANCE = new MeldungenPanel();
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MeldungenPanel.class);
    private static final DateFormat DF = new SimpleDateFormat("dd.MM.YYYY");

    //~ Instance fields --------------------------------------------------------

// private FlurstueckCustomBean currentFlurstueck;
    private final MeldungenTableModel tableModel = new MeldungenTableModel();

    private CidsBean shownMeldung;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseMeldungDialog;
    private javax.swing.JToggleButton btnFinishMeldung;
    private javax.swing.JDialog dlgCreateMeldung;
    private javax.swing.JDialog dlgShowMeldung;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JToggleButton jToggleButton3;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    private javax.swing.JLabel lblCreator;
    private javax.swing.JLabel lblErledigtAm;
    private javax.swing.JLabel lblErledigtVon;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblTimestamp;
    private javax.swing.JPanel panErledigt;
    private org.jdesktop.swingx.JXTable tMeldungen;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MeldungenPanel object.
     */
    private MeldungenPanel() {
        setIsCoreWidget(true);
        initComponents();
        dlgCreateMeldung.pack();
        dlgShowMeldung.pack();
        tMeldungen.setModel(tableModel);
        ((JXTable)tMeldungen).setHighlighters(LagisBroker.ALTERNATE_ROW_HIGHLIGHTER);
        ((JXTable)tMeldungen).setSortOrder(0, SortOrder.DESCENDING);
        ((JXTable)tMeldungen).packAll();
        ((JXTable)tMeldungen).setHighlighters(new BoldHighlighter(new HighlightPredicate() {

                    @Override
                    public boolean isHighlighted(final Component component, final ComponentAdapter componentAdapter) {
                        final CidsBean flurstueck = LagisBroker.getInstance().getCurrentFlurstueck();
                        if ((flurstueck == null) || jToggleButton2.isSelected()) {
                            return false;
                        }
                        return flurstueck.equals(
                                tableModel.getCidsBeanAtRow(
                                    ((JXTable)tMeldungen).convertRowIndexToModel(componentAdapter.row)).getProperty(
                                    "fk_flurstueck"));
                    }
                }));
        tMeldungen.setRowFilter(new MeldungenRowFilter());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JTable getMeldungenTable() {
        return tMeldungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static MeldungenPanel getInstance() {
        return INSTANCE;
    }

    /**
     * DOCUMENT ME!
     */
    private void reloadMeldungen() {
        jButton4.setEnabled(false);
        jPanel4.setVisible(true);
        new SwingWorker<CidsBean[], Object>() {

                @Override
                protected CidsBean[] doInBackground() throws Exception {
                    final Collection<MetaObjectNode> mons = (Collection)CidsBroker.getInstance()
                                .executeSearch(new MeldungenSearch());
                    final List<CidsBean> beans = new ArrayList<>();
                    for (final MetaObjectNode mon : mons) {
                        final MetaObject mo = CidsBroker.getInstance()
                                    .getMetaObject(mon.getObjectId(), mon.getClassId(), mon.getDomain());
                        beans.add(mo.getBean());
                    }
                    return beans.toArray(new CidsBean[0]);
                }

                @Override
                protected void done() {
                    try {
                        final CidsBean[] beans = get();
                        tableModel.setCidsBeans(new ArrayList<CidsBean>(Arrays.asList(beans)));
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                        tableModel.setCidsBeans(new ArrayList());
                    } finally {
                        jButton4.setEnabled(true);
                        jPanel4.setVisible(false);
                    }
                }
            }.execute();
    }

    @Override
    public synchronized void flurstueckChanged(final FlurstueckCustomBean newFlurstueck) {
        try {
            reloadMeldungen();
        } catch (Exception e) {
            LOG.error("Error during flurstueckChanged in MeldungenPanel", e);
        } finally {
            LagisBroker.getInstance().flurstueckChangeFinished(MeldungenPanel.this);
        }
    }

    @Override
    public synchronized void clearComponent() {
        // tableModel.setCidsBeans(new ArrayList());
    }

    /**
     * DOCUMENT ME!
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        dlgCreateMeldung = new javax.swing.JDialog();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        dlgShowMeldung = new javax.swing.JDialog();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        jSeparator1 = new javax.swing.JSeparator();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        jLabel10 = new javax.swing.JLabel();
        lblCreator = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblTimestamp = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel12 = new javax.swing.JPanel();
        btnFinishMeldung = new javax.swing.JToggleButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        panErledigt = new javax.swing.JPanel();
        lblErledigtAm = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lblErledigtVon = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        jPanel10 = new javax.swing.JPanel();
        btnCloseMeldungDialog = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jToggleButton2 = new javax.swing.JToggleButton();
        jToggleButton3 = new javax.swing.JToggleButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tMeldungen = new MeldungTable();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        dlgCreateMeldung.setTitle("neue Meldung");
        dlgCreateMeldung.setModal(true);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        jPanel7.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(jTextField1, gridBagConstraints);

        jTextArea1.setColumns(40);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(8);
        jScrollPane1.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel7.add(jScrollPane1, gridBagConstraints);

        jLabel2.setText("Betreff:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(jLabel2, gridBagConstraints);

        jLabel3.setText("Nachricht:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(jLabel3, gridBagConstraints);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Meldung wird abgesetzt...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel7.add(jLabel4, gridBagConstraints);
        jLabel4.setVisible(false);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel6.add(jPanel7, gridBagConstraints);

        dlgCreateMeldung.getContentPane().add(jPanel6, java.awt.BorderLayout.CENTER);

        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        jButton1.setText("Abbrechen");
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        jPanel5.add(jButton1);

        jButton3.setText("Meldung absetzen");
        jButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });
        jPanel5.add(jButton3);

        dlgCreateMeldung.getContentPane().add(jPanel5, java.awt.BorderLayout.PAGE_END);

        dlgShowMeldung.setTitle("Meldung");
        dlgShowMeldung.setModal(true);

        jPanel8.setLayout(new java.awt.GridBagLayout());

        jPanel9.setLayout(new java.awt.GridBagLayout());

        jPanel11.setLayout(new java.awt.GridBagLayout());

        jLabel11.setText("Flurstück:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel11.add(jLabel11, gridBagConstraints);

        jXHyperlink1.setText("...");
        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel11.add(jXHyperlink1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel11.add(jSeparator1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel11.add(filler1, gridBagConstraints);

        jLabel10.setText("Von:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel11.add(jLabel10, gridBagConstraints);

        lblCreator.setText("...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel11.add(lblCreator, gridBagConstraints);

        jLabel7.setText("Am:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel11.add(jLabel7, gridBagConstraints);

        lblTimestamp.setText("...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel11.add(lblTimestamp, gridBagConstraints);

        jLabel5.setText("Betreff:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel11.add(jLabel5, gridBagConstraints);

        lblName.setText("...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel11.add(lblName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel9.add(jPanel11, gridBagConstraints);

        jLabel6.setText("Nachricht:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel9.add(jLabel6, gridBagConstraints);

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(40);
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(8);
        jScrollPane3.setViewportView(jTextArea2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel9.add(jScrollPane3, gridBagConstraints);

        jPanel12.setLayout(new java.awt.GridBagLayout());

        btnFinishMeldung.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/res/ok.png"))); // NOI18N
        btnFinishMeldung.setText("erledigt");
        btnFinishMeldung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnFinishMeldungActionPerformed(evt);
                }
            });
        jPanel12.add(btnFinishMeldung, new java.awt.GridBagConstraints());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel12.add(filler5, gridBagConstraints);

        panErledigt.setLayout(new java.awt.GridBagLayout());

        lblErledigtAm.setText("...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panErledigt.add(lblErledigtAm, gridBagConstraints);

        jLabel15.setText("Am:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panErledigt.add(jLabel15, gridBagConstraints);

        jLabel16.setText("Von:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panErledigt.add(jLabel16, gridBagConstraints);

        lblErledigtVon.setText("...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panErledigt.add(lblErledigtVon, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panErledigt.add(filler2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel12.add(panErledigt, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel9.add(jPanel12, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        jPanel8.add(jPanel9, gridBagConstraints);

        dlgShowMeldung.getContentPane().add(jPanel8, java.awt.BorderLayout.CENTER);

        jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        btnCloseMeldungDialog.setText("Dialog schließen");
        btnCloseMeldungDialog.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCloseMeldungDialogActionPerformed(evt);
                }
            });
        jPanel10.add(btnCloseMeldungDialog);

        dlgShowMeldung.getContentPane().add(jPanel10, java.awt.BorderLayout.PAGE_END);

        setLayout(new java.awt.GridBagLayout());

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/res/reload.png"))); // NOI18N
        jButton4.setBorderPainted(false);
        jButton4.setContentAreaFilled(false);
        jButton4.setEnabled(false);
        jButton4.setFocusPainted(false);
        jButton4.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton4.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton4.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton4ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        jPanel3.add(jButton4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(filler3, gridBagConstraints);

        jToggleButton2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/res/filter_flurstueck_disabled.png"))); // NOI18N
        jToggleButton2.setToolTipText("nur Meldungen für angezeigtes Flurstück");
        jToggleButton2.setBorderPainted(false);
        jToggleButton2.setContentAreaFilled(false);
        jToggleButton2.setFocusPainted(false);
        jToggleButton2.setMaximumSize(new java.awt.Dimension(24, 24));
        jToggleButton2.setMinimumSize(new java.awt.Dimension(24, 24));
        jToggleButton2.setPreferredSize(new java.awt.Dimension(24, 24));
        jToggleButton2.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/res/filter_flurstueck_enabled.png")));  // NOI18N
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jToggleButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel3.add(jToggleButton2, gridBagConstraints);
        // jToggleButton1.setVisible(false);

        jToggleButton3.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/res/filter_erledigt_disabled.png"))); // NOI18N
        jToggleButton3.setToolTipText("erledigte Meldungen anzeigen");
        jToggleButton3.setBorderPainted(false);
        jToggleButton3.setContentAreaFilled(false);
        jToggleButton3.setFocusPainted(false);
        jToggleButton3.setMaximumSize(new java.awt.Dimension(24, 24));
        jToggleButton3.setMinimumSize(new java.awt.Dimension(24, 24));
        jToggleButton3.setPreferredSize(new java.awt.Dimension(24, 24));
        jToggleButton3.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/lagis/res/filter_erledigt_enabled.png")));  // NOI18N
        jToggleButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jToggleButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel3.add(jToggleButton3, gridBagConstraints);
        // jToggleButton1.setVisible(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(filler4, gridBagConstraints);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/lagis/res/add.png"))); // NOI18N
        jButton2.setBorderPainted(false);
        jButton2.setFocusPainted(false);
        jButton2.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton2.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton2.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel3.add(jButton2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(jPanel3, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        tMeldungen.setModel(new MeldungenTableModel());
        tMeldungen.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    tMeldungenMouseClicked(evt);
                }
            });
        jScrollPane2.setViewportView(tMeldungen);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jScrollPane2, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        jPanel2.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel2, gridBagConstraints);

        jLabel1.setText("Meldungen werden geladen ...");
        jPanel4.add(jLabel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jPanel4, gridBagConstraints);
        jPanel4.setVisible(false);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton4ActionPerformed
        reloadMeldungen();
    }                                                                            //GEN-LAST:event_jButton4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        StaticSwingTools.showDialog(this, dlgCreateMeldung, true);
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jToggleButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jToggleButton2ActionPerformed
        tableModel.fireTableDataChanged();
    }                                                                                  //GEN-LAST:event_jToggleButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        dlgCreateMeldung.setVisible(false);
        clearNewMeldungDialog();
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton3ActionPerformed
        final java.awt.event.ActionEvent evtFinal = evt;

        jLabel4.setVisible(true);
        jButton1.setEnabled(false);
        jButton3.setEnabled(false);

        final String name = jTextField1.getText();
        final String text = jTextArea1.getText();
        new SwingWorker<CidsBean, Void>() {

                @Override
                protected CidsBean doInBackground() throws Exception {
                    final MetaObjectNode meldungMon = (MetaObjectNode)CidsBroker.getInstance()
                                .executeTask(
                                        CreateMeldungServerAction.TASKNAME,
                                        new MetaObjectNode(LagisBroker.getInstance().getCurrentFlurstueck()),
                                        new ServerActionParameter(
                                            CreateMeldungServerAction.Parameter.NAME.toString(),
                                            name),
                                        new ServerActionParameter(
                                            CreateMeldungServerAction.Parameter.TEXT.toString(),
                                            text));
                    final MetaObject meldungMo = CidsBroker.getInstance()
                                .getMetaObject(meldungMon.getObjectId(),
                                    meldungMon.getClassId(),
                                    meldungMon.getDomain());
                    return meldungMo.getBean();
                }

                @Override
                protected void done() {
                    try {
                        tableModel.addCidsBean(get());
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    } finally {
                        jButton1ActionPerformed(evtFinal);
                        jLabel4.setVisible(false);
                        jButton1.setEnabled(true);
                        jButton3.setEnabled(true);
                    }
                }
            }.execute();
    } //GEN-LAST:event_jButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jToggleButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jToggleButton3ActionPerformed
        tableModel.fireTableDataChanged();
    }                                                                                  //GEN-LAST:event_jToggleButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCloseMeldungDialogActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCloseMeldungDialogActionPerformed
        showMeldung(null);
    }                                                                                         //GEN-LAST:event_btnCloseMeldungDialogActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFinishMeldungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnFinishMeldungActionPerformed
        btnFinishMeldung.setEnabled(false);
        final boolean remove = !btnFinishMeldung.isSelected();
        new SwingWorker<CidsBean, Object>() {

                @Override
                protected CidsBean doInBackground() throws Exception {
                    final MetaObjectNode meldungMon = (MetaObjectNode)CidsBroker.getInstance()
                                .executeTask(
                                        FinishMeldungServerAction.TASKNAME,
                                        new MetaObjectNode(shownMeldung),
                                        new ServerActionParameter(
                                            FinishMeldungServerAction.Parameter.REMOVE.toString(),
                                            remove));
                    final MetaObject meldungMo = CidsBroker.getInstance()
                                .getMetaObject(meldungMon.getObjectId(),
                                    meldungMon.getClassId(),
                                    meldungMon.getDomain());
                    return meldungMo.getBean();
                }

                @Override
                protected void done() {
                    try {
                        final CidsBean meldungBean = get();
                        tableModel.removeCidsBean(shownMeldung);
                        tableModel.addCidsBean(meldungBean);
                        showMeldung(meldungBean);
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    } finally {
                        btnFinishMeldung.setEnabled(true);
                    }
                }
            }.execute();
    } //GEN-LAST:event_btnFinishMeldungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tMeldungenMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_tMeldungenMouseClicked
        if (evt.getClickCount() == 2) {
            final int row = tMeldungen.rowAtPoint(evt.getPoint());
            final CidsBean meldungBean = tableModel.getCidsBeanAtRow(tMeldungen.convertRowIndexToModel(row));
            showMeldung(meldungBean);
        }
    }                                                                          //GEN-LAST:event_tMeldungenMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink1ActionPerformed
        final CidsBean flurstueckSchluesselBean = (CidsBean)shownMeldung.getProperty(
                "fk_flurstueck.fk_flurstueck_schluessel");
        LagisBroker.getInstance()
                .loadFlurstueck((FlurstueckSchluesselCustomBean)flurstueckSchluesselBean.getMetaObject().getBean());
    }                                                                                //GEN-LAST:event_jXHyperlink1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JDialog getDlgCreateMeldung() {
        return dlgCreateMeldung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  meldungBean  DOCUMENT ME!
     */
    public void showMeldung(final CidsBean meldungBean) {
        shownMeldung = meldungBean;

        final String flurstueck = (meldungBean != null)
            ? ((CidsBean)meldungBean.getProperty("fk_flurstueck")).toString() : null;
        final String creator = (meldungBean != null) ? (String)meldungBean.getProperty("creator") : null;
        final String timestamp = ((meldungBean != null) && (meldungBean.getProperty("timestamp") != null))
            ? DF.format((Timestamp)meldungBean.getProperty("timestamp")) : null;
        final String name = (meldungBean != null) ? (String)meldungBean.getProperty("name") : null;
        final String text = (meldungBean != null) ? (String)meldungBean.getProperty("text") : null;
        final String erledigtVon = (meldungBean != null) ? (String)meldungBean.getProperty("erledigt_von") : null;
        final String erledigtAm = ((meldungBean != null) && (meldungBean.getProperty("erledigt_am") != null))
            ? DF.format((Timestamp)meldungBean.getProperty("erledigt_am")) : null;

        jXHyperlink1.setText(flurstueck);
        jXHyperlink1.setEnabled(flurstueck != null);
        lblCreator.setText(creator);
        lblTimestamp.setText(timestamp);
        lblName.setText(name);
        jTextArea2.setText(text);
        lblErledigtVon.setText(erledigtVon);
        lblErledigtAm.setText(erledigtAm);
        btnFinishMeldung.setEnabled(meldungBean != null);
        btnFinishMeldung.setSelected(erledigtVon != null);
        panErledigt.setVisible(erledigtVon != null);
        if (meldungBean != null) {
            StaticSwingTools.showDialog(this, dlgShowMeldung, true);
        } else {
            dlgShowMeldung.setVisible(false);
        }
    }
    /**
     * DOCUMENT ME!
     */
    private void clearNewMeldungDialog() {
        jTextField1.setText("");
        jTextArea1.setText("");
    }

    @Override
    public String getWidgetName() {
        return WIDGET_NAME;
    }

    @Override
    public Icon getWidgetIcon() {
        return null;
    }

    @Override
    public void refresh(final Object refreshObject) {
    }

    @Override
    public void setComponentEditable(final boolean isEditable) {
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class MeldungenRowFilter extends RowFilter<TableModel, Integer> {

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean include(final RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
            final CidsBean meldungBean = tableModel.getCidsBeanByIndex(entry.getIdentifier());
            if (meldungBean == null) {
                return false;
            }

            boolean show = jToggleButton3.isSelected()
                        || (meldungBean.getProperty("erledigt_am") == null);

            if (jToggleButton2.isSelected()) {
                final CidsBean flurstueck = LagisBroker.getInstance().getCurrentFlurstueck();
                show &= flurstueck.equals(meldungBean.getProperty("fk_flurstueck"));
            }

            return show;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class MeldungTable extends JXTable {

        //~ Methods ------------------------------------------------------------

        @Override
        public String getToolTipText(final MouseEvent event) {
            final int row = rowAtPoint(event.getPoint());
            if (row > -1) {
                final CidsBean meldungBean = tableModel.getCidsBeanAtRow(tMeldungen.convertRowIndexToModel(row));
                if (meldungBean == null) {
                    return null;
                }
                final String name = (String)meldungBean.getProperty("name");
                final String text = (String)meldungBean.getProperty("text");
                final String creator = (String)meldungBean.getProperty("creator");
                final Timestamp timestamp = (Timestamp)meldungBean.getProperty("timestamp");

                final String tooltip = String.format("<html><body>"
                                + "<u><b>von:</b></u> %s<br/>"
                                + "<u><b>am:</b></u> %s<br/>"
                                + "<u><b>Betreff:</b></u> %s<br/>"
                                + "<u><b>Nachricht:</b></u><div>%s</div>"
                                + "</body></html>",
                        (creator != null) ? creator : "unbekannt",
                        (timestamp != null) ? DF.format(timestamp) : "unbekannt",
                        (name != null) ? name : "",
                        (text != null) ? text.replaceAll("\n", "<br/>\n") : "");
                return tooltip;
            } else {
                return "";
            }
        }
    }
}
