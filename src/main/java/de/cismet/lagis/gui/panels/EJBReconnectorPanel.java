/*
 * EJBReconnectorPanel.java
 *
 * Created on 16. Januar 2008, 10:48
 */

package de.cismet.lagis.gui.panels;

/**
 *
 * @author  Sebastian Puhl
 */
public class EJBReconnectorPanel extends javax.swing.JPanel {
    
    /** Creates new form EJBReconnectorPanel */
    public EJBReconnectorPanel() {
        initComponents();
    }

    public javax.swing.JButton getBtnExitCancel() {
        return btnExitCancel;
    }

    public javax.swing.JButton getBtnRetry() {
        return btnRetry;
    }

    public javax.swing.JProgressBar getPb() {
        return pb;
    }
    
    public void resetPanel(){
        pb.setIndeterminate(false);
//        pb.setVisible(false);
        btnRetry.setVisible(true);        
        btnExitCancel.setText("LagIS beenden");        
        btnExitCancel.setEnabled(true);
        //lblMessage.setText("Es konnte keine Verbindung zum LagiS Server hergestellt werden.\n Was Möchten Sie tun ?");
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        pb = new javax.swing.JProgressBar();
        btnRetry = new javax.swing.JButton();
        btnExitCancel = new javax.swing.JButton();

        pb.setBorderPainted(false);

        btnRetry.setText("Erneut versuchen");

        btnExitCancel.setText("LagIS beenden");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pb, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnRetry)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExitCancel)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnExitCancel, btnRetry});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExitCancel)
                    .addComponent(btnRetry))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnExitCancel, btnRetry});

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExitCancel;
    private javax.swing.JButton btnRetry;
    private javax.swing.JProgressBar pb;
    // End of variables declaration//GEN-END:variables
    
}
