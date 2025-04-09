package staff;

import com.raven.chart.ModelChart;

import java.awt.Color;
import java.awt.Font;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class paymentsPanel extends javax.swing.JInternalFrame {public paymentsPanel() {
        initComponents();
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        payment_table = new javax.swing.JTable();
        amount = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        amount1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(34, 40, 49));
        setBorder(null);
        setPreferredSize(new java.awt.Dimension(1537, 938));

        jPanel1.setBackground(new java.awt.Color(34, 40, 49));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("PAYMENT REPORT");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 10, 1280, 52));

        payment_table.setBackground(new java.awt.Color(57, 62, 70));
        payment_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Patient Name", "Service Description", "Amount", "Time Paid"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        JTableHeader header = payment_table.getTableHeader();
        header.setFont(new Font("Helvetica Neue", Font.BOLD, 18));
        header.setBackground(Color.decode("#222831"));
        header.setForeground(Color.WHITE);
        payment_table.setFillsViewportHeight(true);
        payment_table.setFocusable(false);
        payment_table.setGridColor(new java.awt.Color(31, 32, 56));
        payment_table.setRowHeight(30);
        payment_table.setRowSelectionAllowed(false);
        payment_table.setTableHeader(header);
        jScrollPane1.setViewportView(payment_table);
        if (payment_table.getColumnModel().getColumnCount() > 0) {
            payment_table.getColumnModel().getColumn(0).setResizable(false);
            payment_table.getColumnModel().getColumn(1).setResizable(false);
            payment_table.getColumnModel().getColumn(2).setResizable(false);
            payment_table.getColumnModel().getColumn(3).setResizable(false);
        }

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 1210, 420));

        amount.setFont(new java.awt.Font("Helvetica Neue", 0, 34)); // NOI18N
        amount.setForeground(new java.awt.Color(255, 255, 255));
        amount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        amount.setText("XX,XXX.00");
        jPanel1.add(amount, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 530, -1, -1));

        jLabel4.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("TOTAL AMOUNT FOR THIS WEEK: PHP");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 530, 660, -1));

        jLabel5.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("TOTAL AMOUNT FOR THIS MONTH: PHP");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 590, 690, -1));

        amount1.setFont(new java.awt.Font("Helvetica Neue", 0, 34)); // NOI18N
        amount1.setForeground(new java.awt.Color(255, 255, 255));
        amount1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        amount1.setText("XXX,XXX.00");
        jPanel1.add(amount1, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 590, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel amount;
    private javax.swing.JLabel amount1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable payment_table;
    // End of variables declaration//GEN-END:variables
}
