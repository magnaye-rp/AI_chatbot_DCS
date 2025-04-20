/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package management;

import chart.ModelPolarAreaChart;
import com.mysql.cj.jdbc.PreparedStatementWrapper;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.sql.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import staff.Database;
import staff.appointmentsPanel;
import staff.dashboardPanel;
import staff.paymentsPanel;

public class manageDentists extends javax.swing.JInternalFrame {

    public manageDentists() {
        initComponents();
        javax.swing.plaf.basic.BasicInternalFrameUI ui = (javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI();
        ui.setNorthPane(null);
        performanceOverview();
        dentistRevenue();
        loadDentist();
        loadPolarArea();
    }
    
    void loadPolarArea() {
        String queryAll = "SELECT status, COUNT(status) as count FROM appointment WHERE status != 'In Progress' GROUP BY status;";
        String queryMonth = "SELECT status, COUNT(status) as count FROM appointment " +
                            "WHERE status != 'In Progress' AND MONTH(appointment_date) = MONTH(CURDATE()) " +
                            "AND YEAR(appointment_date) = YEAR(CURDATE()) GROUP BY status;";

        try (Connection conn = Database.getConnection();
             PreparedStatement psAll = conn.prepareStatement(queryAll);
             PreparedStatement psMonth = conn.prepareStatement(queryMonth)) {

            ResultSet rsAll = psAll.executeQuery();
            polarAreaChart.clear();
            while (rsAll.next()) {
                String status = rsAll.getString("status");
                int count = rsAll.getInt("count");
                Color color = getStatusColor(status);
                polarAreaChart.addItem(new ModelPolarAreaChart(color, status, count));
            }

            ResultSet rsMonth = psMonth.executeQuery();
            polarAreaChart1.clear();
            while (rsMonth.next()) {
                String status = rsMonth.getString("status");
                int count = rsMonth.getInt("count");
                Color color = getStatusColor(status);
                polarAreaChart1.addItem(new ModelPolarAreaChart(color, status, count));
            }

        } catch (SQLException ex) {
            Logger.getLogger(manageDentists.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Color getStatusColor(String status) {
        switch (status) {
            case "Done": return new Color(76, 175, 80);         // Green
            case "Cancelled": return new Color(244, 67, 54);    // Red
            case "No Show": return new Color(255, 193, 7);      // Amber
            case "Pending": return new Color(33, 150, 243);     // Blue
            default: return new Color(158, 158, 158);           // Grey fallback
        }
    }

    
    void loadDentist(){
        try(Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM dentist")){
            ResultSet rs = ps.executeQuery();
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);
            while(rs.next()){
                String id = rs.getString("dentist_id");
                String fullname = rs.getString("full_name");
                String contact = rs.getString("contact_num");
                String date_hired = rs.getString("date_hired");
                model.addRow(new Object[]{id, fullname, contact, date_hired});
            }
        } catch (SQLException ex) {
            Logger.getLogger(manageDentists.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void performanceOverview() {
        appointment_turnout.setChartTitle("Dentists' Performance Overview");
        appointment_turnout.setAxisLabels("Dentist", "Appointments");
        appointment_turnout.clearData();
        appointment_turnout.setEnabled(false);

        String[] statuses = {"Done", "Cancelled", "No Show", "Pending"};
        Color[] statusColors = {
            new Color(76, 175, 80),    
            new Color(244, 67, 54),    
            new Color(255, 152, 0),    
            new Color(33, 150, 243)   
        };

        try (Connection conn = Database.getConnection()) {
            for (int i = 0; i < statuses.length; i++) {
                String status = statuses[i];
                String sql = "SELECT d.full_name, COUNT(a.appointment_id) AS total " +
                             "FROM dentist d " +
                             "LEFT JOIN appointment a ON d.dentist_id = a.dentist_id AND a.status = ? " +
                             "GROUP BY d.dentist_id, d.full_name";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, status);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String name = rs.getString("full_name");
                            int count = rs.getInt("total");
                            appointment_turnout.addData(status, name, count);
                        }
                    }
                }

                // Set series color
                appointment_turnout.setSeriesColor(i, statusColors[i]);
            }

        } catch (SQLException ex) {
            Logger.getLogger(appointmentsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void dentistRevenue() {
        paymentChart.setChartTitle("Monthly Dentist Revenue");
        paymentChart.setAxisLabels("Dentist", "Revenue");
        paymentChart.clear();
        paymentChart.setEnabled(false);

        List<revenueData> dentistRevenues = fetchRevenue();

        for (revenueData data : dentistRevenues) {
            String dentistName = data.getWeek_day();
            float revenue = data.getRevenue();
            paymentChart.addValue(revenue, "Revenue", dentistName);
        }
    }

    private List<manageDentists.revenueData> fetchRevenue() {
        List<manageDentists.revenueData> monthlyRevenues = new ArrayList<>();
        String sql = "CALL getDentistRevenueThisMonth()";

        try (Connection conn = Database.getConnection();
             CallableStatement call = conn.prepareCall(sql)) {
            ResultSet rs = call.executeQuery();
            while (rs.next()) {
                String name = rs.getString("NAME");
                float rev = rs.getFloat("total_revenue");
                monthlyRevenues.add(new manageDentists.revenueData(name, rev));
            }
        } catch (SQLException ex) {
            Logger.getLogger(dashboardPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return monthlyRevenues;
    }

    class revenueData {
        private String week_day;
        private float revenue;

        public revenueData(String week_day, float revenue) {
            this.week_day = week_day;
            this.revenue = revenue;
        }

        public String getWeek_day() {
            return week_day;
        }

        public float getRevenue() {
            return revenue;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jButton1 = new javax.swing.JButton();
        appointment_turnout = new beans.LineChartBean();
        jSeparator3 = new javax.swing.JSeparator();
        jButton2 = new javax.swing.JButton();
        paymentChart = new beans.HorizontalBarChart();
        polarAreaChart = new chart.PolarAreaChart();
        polarAreaChart1 = new chart.PolarAreaChart();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setFocusCycleRoot(false);
        setFocusTraversalKeysEnabled(false);
        setFocusable(false);
        setIgnoreRepaint(true);
        setPreferredSize(new java.awt.Dimension(1660, 800));
        setSize(new java.awt.Dimension(1660, 800));

        jPanel1.setBackground(new java.awt.Color(34, 40, 49));
        jPanel1.setLayout(null);

        jTable1.setBackground(new java.awt.Color(57, 62, 70));
        jTable1.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        jTable1.setForeground(new java.awt.Color(255, 255, 255));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "dent_id", "Full Name", "Contact", "Date Hired"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setFillsViewportHeight(true);
        jTable1.setGridColor(new java.awt.Color(34, 40, 49));
        jTable1.setRowHeight(30);
        jTable1.setShowGrid(true);
        jTable1.setSurrendersFocusOnKeystroke(true);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
        }
        JTableHeader header = jTable1.getTableHeader();
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

        jTable1.getColumnModel().removeColumn(jTable1.getColumnModel().getColumn(0));

        jPanel1.add(jScrollPane2);
        jScrollPane2.setBounds(20, 50, 790, 280);

        jPanel4.setBackground(new java.awt.Color(34, 40, 49));

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("UNIFIED PERFORMANCE");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel4);
        jPanel4.setBounds(860, 10, 290, 30);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator1);
        jSeparator1.setBounds(830, 20, 14, 750);

        jPanel3.setBackground(new java.awt.Color(34, 40, 49));

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("OVERALL PERFOMANCE");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(jPanel3);
        jPanel3.setBounds(40, 380, 290, 30);

        jPanel2.setBackground(new java.awt.Color(34, 40, 49));
        jPanel2.setLayout(null);

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("DENTIST LIST");
        jPanel2.add(jLabel1);
        jLabel1.setBounds(10, 0, 160, 30);

        jPanel1.add(jPanel2);
        jPanel2.setBounds(30, 10, 180, 40);
        jPanel1.add(jSeparator2);
        jSeparator2.setBounds(6, 14, 1646, 3);

        jButton1.setText("EDIT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);
        jButton1.setBounds(710, 350, 100, 30);

        appointment_turnout.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(appointment_turnout);
        appointment_turnout.setBounds(20, 420, 790, 340);
        jPanel1.add(jSeparator3);
        jSeparator3.setBounds(10, 400, 810, 10);

        jButton2.setText("ADD NEW");
        jPanel1.add(jButton2);
        jButton2.setBounds(590, 350, 100, 30);
        jPanel1.add(paymentChart);
        paymentChart.setBounds(860, 400, 780, 360);
        jPanel1.add(polarAreaChart);
        polarAreaChart.setBounds(1290, 60, 340, 330);
        jPanel1.add(polarAreaChart1);
        polarAreaChart1.setBounds(880, 60, 340, 330);

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("ALL TIME");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(1590, 70, 60, 17);

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("THIS MONTH");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(860, 60, 80, 17);

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
        int row = jTable1.getSelectedRow();
        if (row != -1) {
            String value = jTable1.getModel().getValueAt(jTable1.convertRowIndexToModel(row), 0).toString();
            JOptionPane.showMessageDialog(null, "The Patient No. " +value+ " can now go to the Operating Room");
        }else{
            JOptionPane.showMessageDialog(null, "Please Select a Row");
        }
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private beans.LineChartBean appointment_turnout;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTable jTable1;
    private beans.HorizontalBarChart paymentChart;
    private chart.PolarAreaChart polarAreaChart;
    private chart.PolarAreaChart polarAreaChart1;
    // End of variables declaration//GEN-END:variables
}
