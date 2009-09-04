/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * VerdisCrossoverPanel.java
 *
 * Created on 03.09.2009, 16:48:33
 */

package de.cismet.lagis.gui.panels;
import entity.KassenzeichenEntity;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.JDialog;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author spuhl
 */
public class VerdisCrossoverPanel extends javax.swing.JPanel implements MouseListener {

    //ToDo defaults für Panel ?
    private static final Logger log = org.apache.log4j.Logger.getLogger(VerdisCrossoverPanel.class);
    private final KassenzeichenTableModel tableModel = new KassenzeichenTableModel();
    private int verdisCrossoverPort = -1;
    private static final String server = "http://localhost:";
    private static final String request = "/verdis/gotoKassenzeichen?";
    //ToDo perhaps place in VerdisCrossover
    //Problem: would be the the only dependency to verdis
    //http://localhost:18000/verdis/gotoKassenzeichen?kassenzeichen=6000442
    public static final NameValuePair PARAMETER_KASSENZEICHEN = new NameValuePair("kassenzeichen", "");

    /** Creates new form VerdisCrossoverPanel */
    public VerdisCrossoverPanel(final int verdisCrossoverPort) {
        initComponents();
        tblkassenzeichen.setModel(tableModel);
        tblkassenzeichen.addMouseListener(this);
        this.verdisCrossoverPort = verdisCrossoverPort;
    }

    public VerdisCrossoverPanel(final int verdisCrossoverPort, final Set<KassenzeichenEntity> kassenzeichen) {
        this(verdisCrossoverPort);
        tableModel.updateTableModel(kassenzeichen);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblkassenzeichen = new JXTable();
        btnClose = new javax.swing.JButton();

        tblkassenzeichen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblkassenzeichen);

        btnClose.setText(org.openide.util.NbBundle.getMessage(VerdisCrossoverPanel.class, "VerdisCrossoverPanel.btnClose.text")); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                        .addGap(13, 13, 13))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnClose)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        closeDialog();
}//GEN-LAST:event_btnCloseActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblkassenzeichen;
    // End of variables declaration//GEN-END:variables

    //ToDo ugly
    private void closeDialog() {
        ((JDialog) getParent().getParent().getParent().getParent()).dispose();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        log.debug("Crossover: mouse clicked");
        final Object source = e.getSource();
        if (source instanceof JXTable) {
            if (e.getClickCount() > 1) {
                final int selectedRow = tblkassenzeichen.getSelectedRow();
                if (selectedRow != -1) {
                    final int modelIndex = ((JXTable) tblkassenzeichen).convertRowIndexToModel(selectedRow);
                    if (modelIndex != -1) {
                        final KassenzeichenEntity selectedKassenzeichen = tableModel.getKassenzeichenAtIndex(modelIndex);
                        if (selectedKassenzeichen != null) {
                            openKassenzeichenInVerdis(selectedKassenzeichen);
                        } else {
                            log.warn("Crossover: Kein Kassenzeichen zu angebenen Index.");
                        }
                    } else {
                        log.warn("Crossover: Kein ModelIndex zu angebenen ViewIndex.");
                    }
                } else {
                    log.debug("Crossover: Keine Tabellenzeile selektiert.");
                }
            } else {
                log.debug("Crossover: Kein Multiclick");
            }
        } else {
            log.debug("Crossover: Mouselistner nicht für JXTable");
        }
    }

    //ToDo place query generation in VerdisCrossover. Give key get Query.
    private void openKassenzeichenInVerdis(KassenzeichenEntity kz) {
        try {
            if (kz != null) {
                if (verdisCrossoverPort < 0 || verdisCrossoverPort > 65535) {
                    log.warn("Crossover: verdisCrossoverPort ist ungültig: " + verdisCrossoverPort);
                } else {
                    //ToDo Thread
                    URL verdisQuery = createQuery(verdisCrossoverPort, kz);
                    if (verdisQuery != null) {
                        verdisQuery.openStream();
                    } else {
                        log.warn("Crossover: konnte keine Query anlegen. Kein Abruf der Kassenzeichen möglich.");
                    }
                }
            } else {
                log.warn("Crossover: Kann angebenes Flurstück nicht öffnwen");
            }
        } catch (IOException ex) {
            log.error("Crossover: Fehler beim öffnen des Kassenzeichens in Verdis.",ex);
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }    

    public class KassenzeichenTableModel extends AbstractTableModel {

        private final String[] COLUMN_HEADER = {"Kassenzeichen"};
        private final ArrayList<KassenzeichenEntity> data = new ArrayList<KassenzeichenEntity>();

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            final KassenzeichenEntity value = data.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return value.getId();
                default:
                    return "Spalte ist nicht definiert";
            }
        }

        public void updateTableModel(Set newData) {
            data.clear();
            if (newData != null) {
                data.addAll(newData);
            }
        }

        public KassenzeichenEntity getKassenzeichenAtIndex(final int index) {
            return data.get(index);
        }

        @Override
        public String getColumnName(int column) {
            return COLUMN_HEADER[column];
        }
    }

    public static URL createQuery(final int port, final KassenzeichenEntity kz) {
        if (port < 0 || port > 65535) {
            log.warn("Crossover: verdisCrossoverPort ist ungültig: " + port);
        } else {
            try {
                //ToDo ugly because is static
                PARAMETER_KASSENZEICHEN.setValue(kz.getId().toString());
                final GetMethod tmp = new GetMethod(server + port + request);
                tmp.setQueryString(new NameValuePair[]{PARAMETER_KASSENZEICHEN});
                log.debug("Crossover: verdisCrossOverQuery: " + tmp.getURI().toString());
                return new URL(tmp.getURI().toString());                
                } catch (Exception ex) {
                log.error("Crossover: Fehler beim fernsteuern von VerdIS.", ex);
            }
        }
        return null;
    }

}
