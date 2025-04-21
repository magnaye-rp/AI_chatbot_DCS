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

    void editDentist(String id){
        String query = "SELECT full_name, contact_num FROM dentist WHERE dentist_id = ?";
        try(Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                editName.setText(rs.getString("full_name"));
                current_name = rs.getString("full_name");
                editName.setForeground(Color.BLACK);
                editNum.setText(rs.getString("contact_num"));
                current_num = rs.getString("contact_num");
                editNum.setForeground(Color.BLACK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(manageDentists.class.getName()).log(Level.SEVERE, null, ex);
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

        editDentist = new javax.swing.JDialog();
        jPanel6 = new javax.swing.JPanel();
        jSeparator6 = new javax.swing.JSeparator();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        editNum = new javax.swing.JTextField();
        editName = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        passwordfield = new javax.swing.JTextField();
        newDentist = new javax.swing.JDialog();
        jPanel5 = new javax.swing.JPanel();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
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

        jPanel6.setBackground(new java.awt.Color(34, 40, 49));
        jPanel6.setLayout(null);
        jPanel6.add(jSeparator6);
        jSeparator6.setBounds(10, 20, 50, 20);
        jPanel6.add(jSeparator7);
        jSeparator7.setBounds(240, 20, 210, 20);

        jLabel9.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("EDIT DENTIST");
        jPanel6.add(jLabel9);
        jLabel9.setBounds(70, 10, 170, 29);

        editNum.setForeground(new java.awt.Color(204, 204, 204));
        editNum.setText("Enter an active number....");
        editNum.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                editNumFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                editNumFocusLost(evt);
            }
        });
        jPanel6.add(editNum);
        editNum.setBounds(40, 170, 380, 40);

        editName.setForeground(new java.awt.Color(204, 204, 204));
        editName.setText("Write full name here....");
        editName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                editNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                editNameFocusLost(evt);
            }
        });
        jPanel6.add(editName);
        editName.setBounds(40, 90, 380, 40);

        jButton4.setBackground(new java.awt.Color(0, 173, 181));
        jButton4.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("DONE");
        jButton4.setBorderPainted(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel6.add(jButton4);
        jButton4.setBounds(180, 320, 90, 30);

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Password");
        jPanel6.add(jLabel10);
        jLabel10.setBounds(40, 230, 100, 17);

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Full Name");
        jPanel6.add(jLabel11);
        jLabel11.setBounds(40, 70, 70, 17);

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Contact Number");
        jPanel6.add(jLabel12);
        jLabel12.setBounds(40, 150, 100, 17);

        passwordfield.setForeground(new java.awt.Color(204, 204, 204));
        passwordfield.setText("remain empty if no changes needed");
        passwordfield.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                passwordfieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                passwordfieldFocusLost(evt);
            }
        });
        jPanel6.add(passwordfield);
        passwordfield.setBounds(40, 250, 380, 40);

        javax.swing.GroupLayout editDentistLayout = new javax.swing.GroupLayout(editDentist.getContentPane());
        editDentist.getContentPane().setLayout(editDentistLayout);
        editDentistLayout.setHorizontalGroup(
            editDentistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
        );
        editDentistLayout.setVerticalGroup(
            editDentistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
        );

        newDentist.setTitle("NEW DENTIST");
        newDentist.setResizable(false);

        jPanel5.setBackground(new java.awt.Color(34, 40, 49));
        jPanel5.setLayout(null);
        jPanel5.add(jSeparator4);
        jSeparator4.setBounds(10, 20, 50, 20);
        jPanel5.add(jSeparator5);
        jSeparator5.setBounds(240, 20, 210, 20);

        jLabel6.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("NEW DENTIST");
        jPanel5.add(jLabel6);
        jLabel6.setBounds(70, 10, 170, 29);

        jTextField1.setForeground(new java.awt.Color(204, 204, 204));
        jTextField1.setText("Enter an active number....");
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });
        jPanel5.add(jTextField1);
        jTextField1.setBounds(40, 170, 380, 40);

        jTextField2.setForeground(new java.awt.Color(204, 204, 204));
        jTextField2.setText("Write full name here....");
        jTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField2FocusLost(evt);
            }
        });
        jPanel5.add(jTextField2);
        jTextField2.setBounds(40, 90, 380, 40);

        jButton3.setBackground(new java.awt.Color(0, 173, 181));
        jButton3.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("DONE");
        jButton3.setBorderPainted(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton3);
        jButton3.setBounds(180, 250, 90, 30);

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Contact Number");
        jPanel5.add(jLabel7);
        jLabel7.setBounds(40, 150, 100, 17);

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Full Name");
        jPanel5.add(jLabel8);
        jLabel8.setBounds(40, 70, 70, 17);

        javax.swing.GroupLayout newDentistLayout = new javax.swing.GroupLayout(newDentist.getContentPane());
        newDentist.getContentPane().setLayout(newDentistLayout);
        newDentistLayout.setHorizontalGroup(
            newDentistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
        );
        newDentistLayout.setVerticalGroup(
            newDentistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
        );

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

        jButton1.setBackground(new java.awt.Color(0, 173, 181));
        jButton1.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("EDIT");
        jButton1.setBorderPainted(false);
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

        jButton2.setBackground(new java.awt.Color(0, 173, 181));
        jButton2.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("ADD NEW");
        jButton2.setBorderPainted(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
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
            editDentist(value);
            editDentist.pack();
            editDentist.setVisible(true);
            current_id =value;
        }else{
            JOptionPane.showMessageDialog(null, "Please Select a Row");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        newDentist.pack();
        newDentist.setVisible(true);
        this.setEnabled(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusGained
        if((jTextField2.getText()).equals("Write full name here....")){
            jTextField2.setText("");
            jTextField2.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_jTextField2FocusGained

    private void jTextField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusLost
        if((jTextField2.getText()).equals("")){
            jTextField2.setText("Write full name here....");
            jTextField2.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_jTextField2FocusLost

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusGained
        if((jTextField1.getText()).equals("Enter an active number....")){
            jTextField1.setText("");
            jTextField1.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_jTextField1FocusGained

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        if((jTextField1.getText()).equals("")){
            jTextField1.setText("Enter an active number....");
            jTextField1.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_jTextField1FocusLost

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try(Connection conn = Database.getConnection();
             CallableStatement call = conn.prepareCall("CALL AddDentist(?,?);")){
            call.setString(1, jTextField2.getText());
            call.setString(2, jTextField1.getText());
            call.executeQuery();
            newDentist.dispose();
            loadDentist();
            performanceOverview();
            dentistRevenue();
        } catch (SQLException ex) {
            Logger.getLogger(manageDentists.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void editNumFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_editNumFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_editNumFocusGained

    private void editNumFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_editNumFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_editNumFocusLost

    private void editNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_editNameFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_editNameFocusGained

    private void editNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_editNameFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_editNameFocusLost

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        if (!current_name.equals(editName.getText()) || !current_num.equals(editNum.getText())) { 
            String upd;
            boolean passwordChanged = !(passwordfield.getText().equals("remain empty if no changes needed"));

            if (passwordChanged) {
                upd = "UPDATE dentist SET full_name = ?, contact_num = ?, password = ? WHERE dentist_id = ?";
            } else {
                upd = "UPDATE dentist SET full_name = ?, contact_num = ? WHERE dentist_id = ?";
            }

            try (Connection conn = Database.getConnection();
                 PreparedStatement ps = conn.prepareStatement(upd)) {

                ps.setString(1, editName.getText());
                ps.setString(2, editNum.getText());

                if (passwordChanged) {
                    ps.setString(3, passwordfield.getText());
                    ps.setString(4, current_id);
                } else {
                    ps.setString(3, current_id);
                }

                ps.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(manageDentists.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            System.out.println("not gunna happen");
            System.out.println(current_name);
        }

        editDentist.dispose();
        loadDentist();
        performanceOverview();
        dentistRevenue();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void passwordfieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passwordfieldFocusLost
        if((passwordfield.getText()).equals("")){
            passwordfield.setText("remain empty if no changes needed");
            passwordfield.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_passwordfieldFocusLost

    private void passwordfieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passwordfieldFocusGained
        if((passwordfield.getText()).equals("remain empty if no changes needed")){
            passwordfield.setText("");
            passwordfield.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_passwordfieldFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private beans.LineChartBean appointment_turnout;
    private javax.swing.JDialog editDentist;
    private javax.swing.JTextField editName;
    private javax.swing.JTextField editNum;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JDialog newDentist;
    private javax.swing.JTextField passwordfield;
    private beans.HorizontalBarChart paymentChart;
    private chart.PolarAreaChart polarAreaChart;
    private chart.PolarAreaChart polarAreaChart1;
    // End of variables declaration//GEN-END:variables
    private String current_id = "";
    private String current_name = "";
    private String current_num = "";
}
