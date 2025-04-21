/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package management;

import com.raven.chart.ModelChart;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import staff.Database;
import java.sql.*;
import java.sql.CallableStatement;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author magnaye.rp
 */
public class managePayments extends javax.swing.JInternalFrame {

    /**
     * Creates new form managementDashboard
     */
    public managePayments() {
        initComponents();
        initMethods();
    }
    
    void loadChart() {
        String call = "CALL getServiceRevenueMonth();";
        double totalRevenue = 0.0; 

        try (Connection conn = Database.getConnection();
             CallableStatement stmt = conn.prepareCall(call)) {

            ResultSet rs = stmt.executeQuery();

            verticalBarChart.clear();
            verticalBarChart.setAxisLabels("Service", "Revenue (PHP)");

            while (rs.next()) {
                String serviceName = rs.getString("name");
                double revenue = rs.getDouble("total_revenue");

                verticalBarChart.addValue(revenue, "Revenue", serviceName);
                totalRevenue += revenue;
            }

            String chartTitle = "Monthly Revenue Per Service - Total: PHP " + totalRevenue;
            verticalBarChart.setChartTitle(chartTitle);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    void loadTables(){
        String query = "SELECT * FROM all_payments;";
        String call = "CALL getUnpaid()";
        try(Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             CallableStatement cs = conn.prepareCall(call)){
            
            ResultSet prs = ps.executeQuery();
            ResultSet crs = cs.executeQuery();
            DefaultTableModel model = (DefaultTableModel) payments.getModel();
            model.setRowCount(0);
            
            while(prs.next()){
                String d_name = prs.getString("dentist");
                String p_name = prs.getString("patient");
                String s_name = prs.getString("service_name");
                float cost = prs.getFloat("service_cost");
                String amount = String.format("%.2f",cost);
                Timestamp time = prs.getTimestamp("time_paid");
                model.addRow(new Object []{d_name,p_name,s_name,amount,time});
            }
            
            model = (DefaultTableModel) toBePaid.getModel();
            model.setRowCount(0);
            while(crs.next()){
                String id = crs.getString("job_id");
                String d_name = crs.getString("dentist");
                String p_name = crs.getString("patient");
                String s_name = crs.getString("service_name");
                float cost = crs.getFloat("service_cost");
                String amount = String.format("%.2f",cost);
                
                model.addRow(new Object []{id, d_name,p_name,s_name,amount});
            }
            
        
        }catch (SQLException ex) {
            Logger.getLogger(managePayments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        payments = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        toBePaid = new javax.swing.JTable();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        verticalBarChart = new chart.VerticalBarChart();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setFocusCycleRoot(false);
        setFocusTraversalKeysEnabled(false);
        setFocusable(false);
        setIgnoreRepaint(true);
        setPreferredSize(new java.awt.Dimension(1660, 800));
        setSize(new java.awt.Dimension(1660, 800));

        jPanel1.setBackground(new java.awt.Color(34, 40, 49));
        jPanel1.setLayout(null);

        jPanel2.setBackground(new java.awt.Color(34, 40, 49));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("PAYMENT HISTORY");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2);
        jPanel2.setBounds(30, 0, 240, 40);

        jPanel4.setBackground(new java.awt.Color(34, 40, 49));

        jButton1.setBackground(new java.awt.Color(0, 173, 181));
        jButton1.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("PAYMENT DONE");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel4);
        jPanel4.setBounds(1450, 230, 180, 30);

        jPanel3.setBackground(new java.awt.Color(34, 40, 49));

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("SERVICES TO BE PAID");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(9, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel3);
        jPanel3.setBounds(920, 0, 270, 50);

        payments.setBackground(new java.awt.Color(57, 62, 70));
        payments.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        payments.setForeground(new java.awt.Color(255, 255, 255));
        payments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Dentist", "Patient", "Service", "Amount", "Time Paid"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        payments.setFillsViewportHeight(true);
        payments.setGridColor(new java.awt.Color(30, 30, 59));
        payments.setRowHeight(30);
        payments.setShowGrid(true);
        jScrollPane1.setViewportView(payments);
        if (payments.getColumnModel().getColumnCount() > 0) {
            payments.getColumnModel().getColumn(0).setResizable(false);
            payments.getColumnModel().getColumn(1).setResizable(false);
            payments.getColumnModel().getColumn(2).setResizable(false);
            payments.getColumnModel().getColumn(3).setResizable(false);
            payments.getColumnModel().getColumn(4).setResizable(false);
        }
        JTableHeader header = payments.getTableHeader();
        header.setFont(new Font("Helvetica Neue", Font.BOLD, 18));
        header.setForeground(Color.WHITE);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.decode("#1B262C"));
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Helvetica Neue", Font.BOLD, 18));
                label.setOpaque(true);
                label.setHorizontalAlignment(CENTER);
                return label;
            }
        });

        jPanel1.add(jScrollPane1);
        jScrollPane1.setBounds(20, 50, 850, 710);
        jPanel1.add(jSeparator1);
        jSeparator1.setBounds(10, 20, 1640, 10);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator2);
        jSeparator2.setBounds(880, 30, 10, 740);

        toBePaid.setBackground(new java.awt.Color(57, 62, 70));
        toBePaid.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        toBePaid.setForeground(new java.awt.Color(255, 255, 255));
        toBePaid.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "job_id", "Dentist", "Patient", "Service", "Amount"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        toBePaid.setFillsViewportHeight(true);
        toBePaid.setGridColor(new java.awt.Color(30, 30, 59));
        toBePaid.setRowHeight(30);
        toBePaid.setShowGrid(true);
        jScrollPane2.setViewportView(toBePaid);
        if (toBePaid.getColumnModel().getColumnCount() > 0) {
            toBePaid.getColumnModel().getColumn(0).setResizable(false);
            toBePaid.getColumnModel().getColumn(1).setResizable(false);
            toBePaid.getColumnModel().getColumn(2).setResizable(false);
            toBePaid.getColumnModel().getColumn(3).setResizable(false);
            toBePaid.getColumnModel().getColumn(4).setResizable(false);
        }
        JTableHeader header1= toBePaid.getTableHeader();
        header1.setFont(new Font("Helvetica Neue", Font.BOLD, 18));
        header1.setForeground(Color.WHITE);
        header1.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.decode("#1B262C"));
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Helvetica Neue", Font.BOLD, 18));
                label.setOpaque(true);
                label.setHorizontalAlignment(CENTER);
                return label;
            }
        });
        toBePaid.getColumnModel().removeColumn(toBePaid.getColumnModel().getColumn(0));

        jPanel1.add(jScrollPane2);
        jScrollPane2.setBounds(900, 60, 740, 150);
        jPanel1.add(jSeparator3);
        jSeparator3.setBounds(730, 320, 0, 3);
        jPanel1.add(jSeparator4);
        jSeparator4.setBounds(900, 240, 750, 10);

        jLabel5.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("REVENUE REPORT THIS MONTH");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(900, 260, 380, 30);
        jPanel1.add(verticalBarChart);
        verticalBarChart.setBounds(900, 320, 740, 440);

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
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTable payments;
    private javax.swing.JTable toBePaid;
    private chart.VerticalBarChart verticalBarChart;
    // End of variables declaration//GEN-END:variables
    void initMethods(){
        javax.swing.plaf.basic.BasicInternalFrameUI UI = (javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI();
        UI.setNorthPane(null);
        loadChart();
        loadTables();
    }
}
