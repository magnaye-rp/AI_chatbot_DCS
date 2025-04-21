
package management;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.sql.*;
import staff.Database;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class managePatients extends javax.swing.JInternalFrame {

    public managePatients() {
        initComponents();
        initMethods();
    }
    
    void loadTables(){
        String query = "SELECT * FROM patient WHERE status = 'active'";
        String query1 = "SELECT * FROM patients_done";
        try(java.sql.Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             PreparedStatement ps1 = conn.prepareStatement(query1)){
            
            ResultSet rs = ps.executeQuery();
            ResultSet rs1 = ps1.executeQuery();
            
            DefaultTableModel model = (DefaultTableModel) patients.getModel();
            model.setRowCount(0);
            while(rs.next()){
                String id = rs.getString("patient_id");
                String name = rs.getString("full_name");
                String con = rs.getString("contact_num");
                String add = rs.getString("address");
                model.addRow(new Object[]{id,name,con,add});
            }
            
            model = (DefaultTableModel) history.getModel();
            model.setRowCount(0);
            
            while(rs1.next()){
                String ptnt = rs1.getString("patient_name");
                String dnst = rs1.getString("dentist_name");
                String srvc = rs1.getString("service_name");
                String schd = rs1.getString("appointment_date");
                model.addRow(new Object[]{ptnt,dnst,srvc,schd});
            }
        
        }   catch (SQLException ex) {
            Logger.getLogger(managePatients.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void loadCharts(){
        appointment_turnout.setChartTitle("Appointment Status (Done & Pending)");
        appointment_turnout.setAxisLabels("Service", "Count");
        appointment_turnout.clearData();
        appointment_turnout.setEnabled(false);

        String query = "SELECT * FROM done_and_pending;";
        String query1 = "SELECT ROUND(((SELECT COUNT(*) FROM appointment WHERE status = 'No Show') / COUNT(*)) * 100, 2) AS noshow_rate FROM appointment;";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             PreparedStatement ps1 = conn.prepareStatement(query1)) {

            ResultSet rs = ps.executeQuery();
            ResultSet rs1 = ps1.executeQuery();

            while (rs.next()) {
                String date = rs.getString("service_name");
                String status = rs.getString("status");
                int value = rs.getInt("count");

                appointment_turnout.addData(status, date, value);
            }
            if(rs1.next()){
                float rate = rs1.getFloat("noshow_rate");
                gaugeChart1.setValueWithAnimation(rate);
            }

        } catch (SQLException ex) {
            Logger.getLogger(managePatients.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void searchHistory(String keyword){
        String query = "SELECT * FROM patients_done WHERE patient_name LIKE ? OR dentist_name LIKE ? OR service_name LIKE ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            
            DefaultTableModel model = (DefaultTableModel) history.getModel();
            model.setRowCount(0);

            while(rs.next()){
                String ptnt = rs.getString("patient_name");
                String dnst = rs.getString("dentist_name");
                String srvc = rs.getString("service_name");
                String schd = rs.getString("appointment_date");
                model.addRow(new Object[]{ptnt,dnst,srvc,schd});
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(managePatients.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void editPatient(String id){
        String query = "SELECT * FROM patient WHERE patient_id = ?";
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
                editAdd.setText(rs.getString("address"));
                current_add = rs.getString("address");
                editAdd.setForeground(Color.BLACK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(manageDentists.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editPatient = new javax.swing.JDialog();
        jPanel6 = new javax.swing.JPanel();
        jSeparator10 = new javax.swing.JSeparator();
        jSeparator11 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        editNum = new javax.swing.JTextField();
        editName = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        passwordfield = new javax.swing.JTextField();
        editAdd = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        history = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel5 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator6 = new javax.swing.JSeparator();
        jScrollPane3 = new javax.swing.JScrollPane();
        patients = new javax.swing.JTable();
        jTextField4 = new javax.swing.JTextField();
        jSeparator9 = new javax.swing.JSeparator();
        jButton2 = new javax.swing.JButton();
        appointment_turnout = new beans.LineChartBean();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        gaugeChart1 = new chart.GaugeChart();
        jLabel7 = new javax.swing.JLabel();

        jPanel6.setBackground(new java.awt.Color(34, 40, 49));
        jPanel6.setLayout(null);
        jPanel6.add(jSeparator10);
        jSeparator10.setBounds(10, 20, 50, 20);
        jPanel6.add(jSeparator11);
        jSeparator11.setBounds(240, 20, 210, 20);

        jLabel9.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("EDIT PATIENT");
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
        editNum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editNumActionPerformed(evt);
            }
        });
        jPanel6.add(editNum);
        editNum.setBounds(40, 150, 380, 40);

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
        editName.setBounds(40, 70, 380, 40);

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
        jButton4.setBounds(180, 370, 90, 30);

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Password");
        jPanel6.add(jLabel10);
        jLabel10.setBounds(40, 290, 100, 17);

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Full Name");
        jPanel6.add(jLabel11);
        jLabel11.setBounds(40, 50, 70, 17);

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Contact Number");
        jPanel6.add(jLabel12);
        jLabel12.setBounds(40, 130, 100, 17);

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
        passwordfield.setBounds(40, 310, 380, 40);

        editAdd.setForeground(new java.awt.Color(204, 204, 204));
        editAdd.setText("Enter current Address....");
        editAdd.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                editAddFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                editAddFocusLost(evt);
            }
        });
        jPanel6.add(editAdd);
        editAdd.setBounds(40, 230, 380, 40);

        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Address");
        jPanel6.add(jLabel13);
        jLabel13.setBounds(40, 210, 100, 17);

        javax.swing.GroupLayout editPatientLayout = new javax.swing.GroupLayout(editPatient.getContentPane());
        editPatient.getContentPane().setLayout(editPatientLayout);
        editPatientLayout.setHorizontalGroup(
            editPatientLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
        );
        editPatientLayout.setVerticalGroup(
            editPatientLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
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

        history.setBackground(new java.awt.Color(57, 62, 70));
        history.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        history.setForeground(new java.awt.Color(255, 255, 255));
        history.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Patient", "Dentist", "Service", "Schedule"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        history.setFillsViewportHeight(true);
        history.setGridColor(new java.awt.Color(34, 40, 49));
        history.setRowHeight(30);
        history.setShowGrid(true);
        history.setSurrendersFocusOnKeystroke(true);
        history.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(history);
        if (history.getColumnModel().getColumnCount() > 0) {
            history.getColumnModel().getColumn(0).setResizable(false);
            history.getColumnModel().getColumn(1).setResizable(false);
            history.getColumnModel().getColumn(2).setResizable(false);
            history.getColumnModel().getColumn(3).setResizable(false);
        }
        JTableHeader header1 = history.getTableHeader();
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

        jPanel1.add(jScrollPane2);
        jScrollPane2.setBounds(850, 70, 790, 370);

        jPanel3.setBackground(new java.awt.Color(34, 40, 49));

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("PATIENTS LIST");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 4, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3);
        jPanel3.setBounds(40, 0, 190, 40);

        jPanel2.setBackground(new java.awt.Color(34, 40, 49));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("NEW PATIENT PROFILE");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2);
        jPanel2.setBounds(30, 430, 280, 40);

        jPanel4.setBackground(new java.awt.Color(34, 40, 49));

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("PATIENT HISTORY");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(jPanel4);
        jPanel4.setBounds(860, 0, 220, 40);
        jPanel1.add(jSeparator1);
        jSeparator1.setBounds(10, 10, 1646, 10);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator2);
        jSeparator2.setBounds(827, 20, 10, 410);

        jPanel5.setBackground(new java.awt.Color(34, 40, 49));

        jButton3.setBackground(new java.awt.Color(0, 173, 181));
        jButton3.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("DELETE");
        jButton3.setBorderPainted(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 10, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel5);
        jPanel5.setBounds(560, 430, 120, 40);

        jButton1.setBackground(new java.awt.Color(0, 173, 181));
        jButton1.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("ADD");
        jButton1.setBorderPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);
        jButton1.setBounds(320, 730, 100, 30);
        jPanel1.add(jSeparator3);
        jSeparator3.setBounds(10, 450, 690, 10);

        jTextField1.setForeground(new java.awt.Color(204, 204, 204));
        jTextField1.setText("Enter Address...");
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });
        jPanel1.add(jTextField1);
        jTextField1.setBounds(50, 660, 370, 40);

        jTextField2.setForeground(new java.awt.Color(204, 204, 204));
        jTextField2.setText("Enter Name here...");
        jTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField2FocusLost(evt);
            }
        });
        jPanel1.add(jTextField2);
        jTextField2.setBounds(50, 500, 370, 40);

        jTextField3.setForeground(new java.awt.Color(204, 204, 204));
        jTextField3.setText("Contact Number...");
        jTextField3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField3FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField3FocusLost(evt);
            }
        });
        jPanel1.add(jTextField3);
        jTextField3.setBounds(50, 580, 370, 40);

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator4);
        jSeparator4.setBounds(470, 470, 30, 320);
        jPanel1.add(jSeparator6);
        jSeparator6.setBounds(830, 463, 10, 0);

        patients.setBackground(new java.awt.Color(57, 62, 70));
        patients.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        patients.setForeground(new java.awt.Color(255, 255, 255));
        patients.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "patient_id", "Full Name", "Contact", "Address"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        patients.setFillsViewportHeight(true);
        patients.setGridColor(new java.awt.Color(34, 40, 49));
        patients.setRowHeight(30);
        patients.setShowGrid(true);
        patients.setSurrendersFocusOnKeystroke(true);
        patients.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(patients);
        patients.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (patients.getColumnModel().getColumnCount() > 0) {
            patients.getColumnModel().getColumn(0).setResizable(false);
            patients.getColumnModel().getColumn(1).setResizable(false);
            patients.getColumnModel().getColumn(2).setResizable(false);
            patients.getColumnModel().getColumn(3).setResizable(false);
        }
        JTableHeader header = patients.getTableHeader();
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

        patients.getColumnModel().removeColumn(patients.getColumnModel().getColumn(0));

        jPanel1.add(jScrollPane3);
        jScrollPane3.setBounds(20, 50, 790, 370);

        jTextField4.setForeground(new java.awt.Color(204, 204, 204));
        jTextField4.setText("Search Here...");
        jTextField4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField4FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField4FocusLost(evt);
            }
        });
        jTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField4KeyReleased(evt);
            }
        });
        jPanel1.add(jTextField4);
        jTextField4.setBounds(850, 40, 790, 30);
        jPanel1.add(jSeparator9);
        jSeparator9.setBounds(820, 450, 830, 20);

        jButton2.setBackground(new java.awt.Color(0, 173, 181));
        jButton2.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("EDIT");
        jButton2.setBorderPainted(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2);
        jButton2.setBounds(710, 430, 100, 30);

        appointment_turnout.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(appointment_turnout);
        appointment_turnout.setBounds(500, 470, 720, 310);

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Full Name");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(50, 480, 80, 17);

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Contact Number");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(50, 560, 100, 17);

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Address");
        jPanel1.add(jLabel6);
        jLabel6.setBounds(50, 640, 60, 17);

        gaugeChart1.setColor1(new java.awt.Color(118, 189, 34));
        jPanel1.add(gaugeChart1);
        gaugeChart1.setBounds(1310, 500, 290, 250);

        jLabel7.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("RATE OF NO-SHOW APPOINTMENTS");
        jPanel1.add(jLabel7);
        jLabel7.setBounds(1300, 750, 320, 20);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 806, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.plaf.basic.BasicInternalFrameUI UI = (javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI();
        UI.setNorthPane(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField4FocusGained
        if("Search Here...".equals(jTextField4.getText())){
            jTextField4.setText("");
            jTextField4.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_jTextField4FocusGained

    private void jTextField4FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField4FocusLost
        if("".equals(jTextField4.getText())){
            jTextField4.setText("Search Here...");
            jTextField4.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_jTextField4FocusLost

    private void jTextField4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyReleased
        searchHistory(jTextField4.getText());
    }//GEN-LAST:event_jTextField4KeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int row = patients.getSelectedRow();
        if (row != -1) {
            String value = patients.getModel().getValueAt(patients.convertRowIndexToModel(row), 0).toString();
            editPatient(value);
            editPatient.pack();
            editPatient.setVisible(true);
            current_id = value;
        }else{
            JOptionPane.showMessageDialog(null, "Please Select a Row");
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void editNumFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_editNumFocusGained
        if((editNum.getText()).equals("Enter an active number....")){
            editNum.setText("");
            editNum.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_editNumFocusGained

    private void editNumFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_editNumFocusLost
        if((editNum.getText()).equals("")){
            editNum.setText("Enter an active number....");
            editNum.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_editNumFocusLost

    private void editNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_editNameFocusGained
        if((editName.getText()).equals("Write full name here....")){
            editName.setText("");
            editName.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_editNameFocusGained

    private void editNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_editNameFocusLost
        if((editName.getText()).equals("")){
            editName.setText("Write full name here....");
            editName.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_editNameFocusLost

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        if (!current_name.equals(editName.getText()) || !current_num.equals(editNum.getText()) || !current_add.equals(editAdd.getText())) {
            String upd;
            boolean passwordChanged = !(passwordfield.getText().equals("remain empty if no changes needed"));

            if (passwordChanged) {
                upd = "UPDATE patient SET full_name = ?, contact_num = ?, address = ?, password = ? WHERE patient_id = ?";
            } else {
                upd = "UPDATE patient SET full_name = ?, contact_num = ?, address = ? WHERE patient_id = ?";
            }

            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(upd)) {

                ps.setString(1, editName.getText());
                ps.setString(2, editNum.getText());
                ps.setString(3, editAdd.getText());

                if (passwordChanged) {
                    ps.setString(4, passwordfield.getText());
                    ps.setString(5, current_id);
                } else {
                    ps.setString(4, current_id);
                }

                ps.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(manageDentists.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            System.out.println("not gunna happen");
        }

        editPatient.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void passwordfieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passwordfieldFocusGained
        if((passwordfield.getText()).equals("remain empty if no changes needed")){
            passwordfield.setText("");
            passwordfield.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_passwordfieldFocusGained

    private void passwordfieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passwordfieldFocusLost
        if((passwordfield.getText()).equals("")){
            passwordfield.setText("remain empty if no changes needed");
            passwordfield.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_passwordfieldFocusLost

    private void editAddFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_editAddFocusGained
        if((editAdd.getText()).equals("Enter current Address....")){
            editAdd.setText("");
            editAdd.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_editAddFocusGained

    private void editAddFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_editAddFocusLost
        if((editAdd.getText()).equals("")){
            editAdd.setText("Enter current Address....");
            editAdd.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_editAddFocusLost

    private void editNumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editNumActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_editNumActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
         int row = patients.getSelectedRow();
        if (row != -1) {
            String value = patients.getModel().getValueAt(patients.convertRowIndexToModel(row), 0).toString();
            String query = "UPDATE patient SET status = 'deleted' WHERE patient_id = ?";
            try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setString(1, value);
                ps.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(manageDentists.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }else{
            JOptionPane.showMessageDialog(null, "Please Select a Row");
        }
        loadCharts();
        loadTables();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String sql = "CALL newPatient(?,?,?)";
        try (Connection conn = Database.getConnection();
             CallableStatement call = conn.prepareCall(sql)) {
            call.setString(1, jTextField2.getText());
            call.setString(2, jTextField3.getText());
            call.setString(3, jTextField1.getText());
            ResultSet rs = call.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(managePatients.class.getName()).log(Level.SEVERE, null, ex);
        } 
        jTextField2.setText("Enter Name here...");
            jTextField2.setForeground(Color.GRAY);
            jTextField3.setText("Enter Contact Number...");
            jTextField3.setForeground(Color.GRAY);
            jTextField1.setText("Enter Address...");
            jTextField1.setForeground(Color.GRAY);
        loadTables();
        loadCharts();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusGained
        if((jTextField2.getText()).equals("Enter Name here...")){
            jTextField2.setText("");
            jTextField2.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_jTextField2FocusGained

    private void jTextField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusLost
        if((jTextField2.getText()).equals("")){
            jTextField2.setText("Enter Name here...");
            jTextField2.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_jTextField2FocusLost

    private void jTextField3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField3FocusGained
        if((jTextField3.getText()).equals("Enter Contact Number...")){
            jTextField3.setText("");
            jTextField3.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_jTextField3FocusGained

    private void jTextField3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField3FocusLost
        if((jTextField3.getText()).equals("")){
            jTextField3.setText("Enter Contact Number...");
            jTextField3.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_jTextField3FocusLost

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusGained
        if((jTextField1.getText()).equals("Enter Address...")){
            jTextField1.setText("");
            jTextField1.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_jTextField1FocusGained

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        if((jTextField1.getText()).equals("")){
            jTextField1.setText("Enter Address...");
            jTextField1.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_jTextField1FocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private beans.LineChartBean appointment_turnout;
    private javax.swing.JTextField editAdd;
    private javax.swing.JTextField editName;
    private javax.swing.JTextField editNum;
    private javax.swing.JDialog editPatient;
    private chart.GaugeChart gaugeChart1;
    private javax.swing.JTable history;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField passwordfield;
    private javax.swing.JTable patients;
    // End of variables declaration//GEN-END:variables
    private String current_id;
    private String current_name;
    private String current_num;
    private String current_add;
    final void initMethods(){
        loadTables();
        loadCharts();
    }
}
