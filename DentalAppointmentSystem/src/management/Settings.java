/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package management;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import staff.Database;

public class Settings extends javax.swing.JInternalFrame {
    
    Credentials creds = new Credentials();

    public Settings() {
        initComponents();
        javax.swing.plaf.basic.BasicInternalFrameUI UI = (javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI();
        UI.setNorthPane(null);
        loadTables();
    }

    void loadTables(){
        try(Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM dentist");
             PreparedStatement ps1 = conn.prepareStatement("SELECT * FROM patient");
             PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM service")){
            
            ResultSet rs = ps.executeQuery();
            ResultSet rs1 = ps1.executeQuery();
            ResultSet rs2 = ps2.executeQuery();
            
            DefaultTableModel model = (DefaultTableModel) dentists.getModel();
            model.setRowCount(0);
            DefaultTableModel model1 = (DefaultTableModel) patients.getModel();
            model1.setRowCount(0);
            DefaultTableModel model2 = (DefaultTableModel) jTable1.getModel();
            model2.setRowCount(0);

            while(rs.next()){
                String id = rs.getString("dentist_id");
                String fullname = rs.getString("full_name");
                String contact = rs.getString("contact_num");
                String date_hired = rs.getString("date_hired");
                String stat = rs.getString("status");
                model.addRow(new Object[]{id, fullname, contact, date_hired, stat});
            }
            while(rs1.next()){
                String id = rs1.getString("patient_id");
                String name = rs1.getString("full_name");
                String con = rs1.getString("contact_num");
                String add = rs1.getString("address");
                String stat = rs1.getString("status");
                model1.addRow(new Object[]{id,name,con,add,stat});
            }
            while(rs2.next()){
                String id = rs2.getString("service_id");
                String name = rs2.getString("service_name");
                float cst = rs2.getFloat("service_cost");
                String cost = String.format("Php %.2f",cst);
                String duration = rs2.getString("duration_minutes");
                model2.addRow(new Object[]{id, name, cost, duration});
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(manageDentists.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane3 = new javax.swing.JScrollPane();
        patients = new javax.swing.JTable();
        jSeparator3 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        dentists = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jSeparator4 = new javax.swing.JSeparator();
        slider = new javax.swing.JSlider();
        s_name = new javax.swing.JTextField();
        s_cost = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        newPassword = new javax.swing.JPasswordField();
        newId = new javax.swing.JTextField();
        currentId = new javax.swing.JTextField();
        currentPassword = new javax.swing.JPasswordField();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setFocusCycleRoot(false);
        setFocusTraversalKeysEnabled(false);
        setFocusable(false);
        setIgnoreRepaint(true);
        setPreferredSize(new java.awt.Dimension(1660, 800));
        setSize(new java.awt.Dimension(1660, 800));

        jPanel1.setBackground(new java.awt.Color(34, 40, 49));
        jPanel1.setLayout(null);

        jPanel3.setBackground(new java.awt.Color(34, 40, 49));

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("DENTISTS LIST");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 190, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jPanel1.add(jPanel3);
        jPanel3.setBounds(40, 410, 190, 40);

        jPanel2.setBackground(new java.awt.Color(34, 40, 49));

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("PATIENTS LIST");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 4, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2);
        jPanel2.setBounds(40, 0, 190, 40);

        jPanel6.setBackground(new java.awt.Color(34, 40, 49));

        jButton4.setBackground(new java.awt.Color(0, 173, 181));
        jButton4.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("RESTORE");
        jButton4.setBorderPainted(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel6);
        jPanel6.setBounds(540, 410, 120, 30);

        jPanel4.setBackground(new java.awt.Color(34, 40, 49));

        jButton1.setBackground(new java.awt.Color(0, 173, 181));
        jButton1.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("DELETE");
        jButton1.setBorderPainted(false);
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
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel4);
        jPanel4.setBounds(680, 410, 120, 30);

        jPanel5.setBackground(new java.awt.Color(34, 40, 49));

        jLabel4.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("EDIT INFORMATION");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 4, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(jPanel5);
        jPanel5.setBounds(860, 310, 240, 40);
        jPanel1.add(jSeparator1);
        jSeparator1.setBounds(10, 10, 1640, 10);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator2);
        jSeparator2.setBounds(830, 20, 10, 740);

        patients.setBackground(new java.awt.Color(57, 62, 70));
        patients.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        patients.setForeground(new java.awt.Color(255, 255, 255));
        patients.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "patient_id", "Full Name", "Contact", "Address", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false
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
        patients.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                patientsFocusGained(evt);
            }
        });
        jScrollPane3.setViewportView(patients);
        if (patients.getColumnModel().getColumnCount() > 0) {
            patients.getColumnModel().getColumn(0).setResizable(false);
            patients.getColumnModel().getColumn(1).setResizable(false);
            patients.getColumnModel().getColumn(2).setResizable(false);
            patients.getColumnModel().getColumn(3).setResizable(false);
            patients.getColumnModel().getColumn(4).setResizable(false);
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
        jScrollPane3.setBounds(20, 40, 790, 350);
        jPanel1.add(jSeparator3);
        jSeparator3.setBounds(10, 420, 810, 10);

        dentists.setBackground(new java.awt.Color(57, 62, 70));
        dentists.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        dentists.setForeground(new java.awt.Color(255, 255, 255));
        dentists.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "dent_id", "Full Name", "Contact", "Date Hired", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dentists.setFillsViewportHeight(true);
        dentists.setGridColor(new java.awt.Color(34, 40, 49));
        dentists.setRowHeight(30);
        dentists.setShowGrid(true);
        dentists.setSurrendersFocusOnKeystroke(true);
        dentists.getTableHeader().setReorderingAllowed(false);
        dentists.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                dentistsFocusGained(evt);
            }
        });
        jScrollPane2.setViewportView(dentists);
        if (dentists.getColumnModel().getColumnCount() > 0) {
            dentists.getColumnModel().getColumn(1).setResizable(false);
            dentists.getColumnModel().getColumn(2).setResizable(false);
            dentists.getColumnModel().getColumn(3).setResizable(false);
            dentists.getColumnModel().getColumn(4).setResizable(false);
        }
        JTableHeader header2 = dentists.getTableHeader();
        header2.setFont(new Font("Helvetica Neue", Font.BOLD, 18));
        header2.setForeground(Color.WHITE);
        header2.setDefaultRenderer(new DefaultTableCellRenderer() {
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
        dentists.getColumnModel().removeColumn(dentists.getColumnModel().getColumn(0));

        jPanel1.add(jScrollPane2);
        jScrollPane2.setBounds(20, 470, 790, 280);

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
                "service_id", "Service Name", "Service Cost", "Duration in Minutes"
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
        jTable1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTable1FocusGained(evt);
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
        }
        JTableHeader header1 = jTable1.getTableHeader();
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
        jTable1.getColumnModel().removeColumn(jTable1.getColumnModel().getColumn(0));

        jPanel1.add(jScrollPane4);
        jScrollPane4.setBounds(870, 40, 750, 260);
        jPanel1.add(jSeparator4);
        jSeparator4.setBounds(840, 330, 810, 10);

        slider.setMajorTickSpacing(20);
        slider.setMaximum(150);
        slider.setMinimum(30);
        slider.setPaintTicks(true);
        slider.setValue(90);
        slider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderStateChanged(evt);
            }
        });
        jPanel1.add(slider);
        slider.setBounds(1320, 400, 250, 30);

        s_name.setEditable(false);
        jPanel1.add(s_name);
        s_name.setBounds(870, 400, 400, 40);

        s_cost.setEditable(false);
        s_cost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                s_costActionPerformed(evt);
            }
        });
        jPanel1.add(s_cost);
        s_cost.setBounds(870, 480, 400, 40);

        jButton2.setBackground(new java.awt.Color(0, 173, 181));
        jButton2.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("SAVE SETTING");
        jButton2.setBorderPainted(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2);
        jButton2.setBounds(1320, 480, 140, 40);

        jButton3.setBackground(new java.awt.Color(0, 173, 181));
        jButton3.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("CANCEL EDIT");
        jButton3.setBorderPainted(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3);
        jButton3.setBounds(1480, 480, 130, 40);

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("090");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(1580, 400, 30, 30);

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Service Cost");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(870, 460, 110, 20);

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Duration in Minutes");
        jPanel1.add(jLabel6);
        jLabel6.setBounds(1320, 380, 120, 20);

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Service Name");
        jPanel1.add(jLabel7);
        jLabel7.setBounds(870, 370, 110, 20);

        jPanel7.setBackground(new java.awt.Color(34, 40, 49));

        jLabel8.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("EDIT LOGIN CREDENTIALS");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(0, 4, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(jPanel7);
        jPanel7.setBounds(860, 530, 320, 40);
        jPanel1.add(jSeparator5);
        jSeparator5.setBounds(840, 550, 810, 10);

        newPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newPasswordActionPerformed(evt);
            }
        });
        jPanel1.add(newPassword);
        newPassword.setBounds(1180, 700, 291, 40);
        jPanel1.add(newId);
        newId.setBounds(860, 700, 280, 40);

        currentId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currentIdActionPerformed(evt);
            }
        });
        jPanel1.add(currentId);
        currentId.setBounds(860, 610, 280, 40);
        jPanel1.add(currentPassword);
        currentPassword.setBounds(1180, 610, 330, 40);

        jButton5.setBackground(new java.awt.Color(0, 173, 181));
        jButton5.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("SAVE");
        jButton5.setBorderPainted(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton5);
        jButton5.setBounds(1540, 700, 100, 40);

        jButton6.setBackground(new java.awt.Color(0, 173, 181));
        jButton6.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("CANCEL");
        jButton6.setBorderPainted(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton6);
        jButton6.setBounds(1540, 610, 100, 40);

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("New Password");
        jPanel1.add(jLabel9);
        jLabel9.setBounds(1180, 680, 120, 17);

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("New LogIn ID");
        jPanel1.add(jLabel10);
        jLabel10.setBounds(860, 680, 120, 17);

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Current LogIn ID");
        jPanel1.add(jLabel11);
        jLabel11.setBounds(860, 590, 120, 17);

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Current Password");
        jPanel1.add(jLabel12);
        jLabel12.setBounds(1180, 590, 120, 17);

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/view.png"))); // NOI18N
        jButton7.setBorderPainted(false);
        jButton7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton7MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton7MouseReleased(evt);
            }
        });
        jPanel1.add(jButton7);
        jButton7.setBounds(1470, 700, 40, 40);

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

    private void patientsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_patientsFocusGained
        dentists.clearSelection();
        current_table = 1;
    }//GEN-LAST:event_patientsFocusGained

    private void dentistsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_dentistsFocusGained
        patients.clearSelection();
        current_table = 2;
    }//GEN-LAST:event_dentistsFocusGained

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        jTable1.clearSelection();
        slider.setValue(90);
        s_name.setText("");
        s_cost.setText("");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void sliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderStateChanged
        int val = slider.getValue();
        String value = String.format("%03d", val);
        jLabel1.setText(value);
    }//GEN-LAST:event_sliderStateChanged

    private void s_costActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_s_costActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_s_costActionPerformed

    private void jTable1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTable1FocusGained
        patients.clearSelection();
        dentists.clearSelection();
    }//GEN-LAST:event_jTable1FocusGained

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int row = jTable1.getSelectedRow();
        if (row != -1) {
            int modelRow = jTable1.convertRowIndexToModel(row);

            String name = jTable1.getModel().getValueAt(modelRow, 1).toString();
            current_name = name;
            String cost = jTable1.getModel().getValueAt(modelRow, 2).toString(); 
            current_cost = cost;
            String duration = jTable1.getModel().getValueAt(modelRow, 3).toString();

            String numericCost = cost.substring(4);
            s_name.setText(name);
            s_cost.setText(numericCost);
            int sliderValue = Integer.parseInt(duration);
            current_value = sliderValue;
            slider.setValue(sliderValue);
}
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int row;
        if (current_table == 1) {
            row = patients.getSelectedRow();
            if (row != -1) {
                int modelRow = patients.convertRowIndexToModel(row);
                String status = patients.getModel().getValueAt(modelRow, 4).toString();

                if (status.equalsIgnoreCase("active")) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this patient?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        String id = patients.getModel().getValueAt(modelRow, 0).toString();
                        try (Connection conn = Database.getConnection();
                             PreparedStatement ps = conn.prepareStatement("UPDATE patient SET status = 'deleted' WHERE patient_id = ?")) {
                            ps.setString(1, id);
                            ps.executeUpdate();
                            JOptionPane.showMessageDialog(null, "Patient status updated to deleted.");
                            loadTables();
                        } catch (SQLException ex) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(null, "Failed to update patient status.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Only active patients can be deleted.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a patient row.");
            }
        } else if (current_table == 2) {
            row = dentists.getSelectedRow();
            if (row != -1) {
                int modelRow = dentists.convertRowIndexToModel(row);
                String status = dentists.getModel().getValueAt(modelRow, 4).toString();

                if (status.equalsIgnoreCase("active")) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this dentist?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        String id = dentists.getModel().getValueAt(modelRow, 0).toString();
                        try (Connection conn = Database.getConnection();
                             PreparedStatement ps = conn.prepareStatement("UPDATE dentist SET status = 'deleted' WHERE dentist_id = ?")) {
                            ps.setString(1, id);
                            ps.executeUpdate();
                            JOptionPane.showMessageDialog(null, "Dentist status updated to deleted.");
                            loadTables();
                        } catch (SQLException ex) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(null, "Failed to update dentist status.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Only active dentists can be deleted.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a dentist row.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a row from Dentists or Patients");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int row;
        if (current_table == 1) {
            row = patients.getSelectedRow();
            if (row != -1) {
                int modelRow = patients.convertRowIndexToModel(row);
                String status = patients.getModel().getValueAt(modelRow, 4).toString();

                if (status.equalsIgnoreCase("deleted")) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to restore this patient?", "Confirm Restoration", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        String id = patients.getModel().getValueAt(modelRow, 0).toString();
                        try (Connection conn = Database.getConnection();
                             PreparedStatement ps = conn.prepareStatement("UPDATE patient SET status = 'active' WHERE patient_id = ?")) {
                            ps.setString(1, id);
                            ps.executeUpdate();
                            JOptionPane.showMessageDialog(null, "Patient status restored to active.");
                            loadTables();
                        } catch (SQLException ex) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(null, "Failed to restore patient status.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Only deleted patients can be restored.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a patient row.");
            }
        } else if (current_table == 2) {
            row = dentists.getSelectedRow();
            if (row != -1) {
                int modelRow = dentists.convertRowIndexToModel(row);
                String status = dentists.getModel().getValueAt(modelRow, 4).toString();

                if (status.equalsIgnoreCase("deleted")) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to restore this dentist?", "Confirm Restoration", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        String id = dentists.getModel().getValueAt(modelRow, 0).toString();
                        try (Connection conn = Database.getConnection();
                             PreparedStatement ps = conn.prepareStatement("UPDATE dentist SET status = 'active' WHERE dentist_id = ?")) {
                            ps.setString(1, id);
                            ps.executeUpdate();
                            JOptionPane.showMessageDialog(null, "Dentist status restored to active.");
                            loadTables();
                        } catch (SQLException ex) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(null, "Failed to restore dentist status.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Only deleted dentists can be restored.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a dentist row.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a row from Dentists or Patients");
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int row = jTable1.getSelectedRow();
        if (row != -1) {
            int modelRow = jTable1.convertRowIndexToModel(row);
            String id = jTable1.getModel().getValueAt(modelRow, 0).toString();
            String newName = s_name.getText();
            String newCost = s_cost.getText().replace("Php ", "").trim();
            int newDuration = slider.getValue();

            if (!current_name.equals(newName) || !current_cost.equals(s_cost.getText()) || current_value != newDuration) {
                try (Connection conn = Database.getConnection();
                     PreparedStatement ps = conn.prepareStatement("UPDATE service SET service_name = ?, service_cost = ?, duration_minutes = ? WHERE service_id = ?")) {

                    ps.setString(1, newName);
                    ps.setFloat(2, Float.parseFloat(newCost));
                    ps.setInt(3, newDuration);
                    ps.setString(4, id);

                    int updated = ps.executeUpdate();
                    if (updated > 0) {
                        JOptionPane.showMessageDialog(null, "Service updated successfully.");
                        loadTables();
                    } else {
                        JOptionPane.showMessageDialog(null, "No changes were made.");
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "Failed to update service.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid cost value.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "No changes detected.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a row to update.");
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void currentIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currentIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_currentIdActionPerformed

    private void newPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_newPasswordActionPerformed

    private void jButton7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton7MousePressed
        newPassword.setEchoChar((char) 0);
    }//GEN-LAST:event_jButton7MousePressed

    private void jButton7MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton7MouseReleased
        newPassword.setEchoChar('*');
    }//GEN-LAST:event_jButton7MouseReleased

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        String curId = creds.getLogInID();
        String curPass = creds.getPass();
        if (!"".equals(currentId.getText()) && !"".equals(currentPassword.getText())) {
            if (curId.equals(currentId.getText()) && curPass.equals(currentPassword.getText())) {
                 if (!"".equals(newId.getText())) {
                    creds.setLogInID(newId.getText());
                }
                if (!"".equals(newPassword.getText())) {
                    creds.setPass(newPassword.getText());
                }
                currentId.setText("");
                currentPassword.setText("");
                newId.setText("");
                newPassword.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect current ID or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please fill in the current ID and password fields.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        currentId.setText("");
        currentPassword.setText("");
        newId.setText("");
        newPassword.setText("");
    }//GEN-LAST:event_jButton6ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField currentId;
    private javax.swing.JPasswordField currentPassword;
    private javax.swing.JTable dentists;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
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
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField newId;
    private javax.swing.JPasswordField newPassword;
    private javax.swing.JTable patients;
    private javax.swing.JTextField s_cost;
    private javax.swing.JTextField s_name;
    private javax.swing.JSlider slider;
    // End of variables declaration//GEN-END:variables
    int current_table = 1;
    int current_value;
    String current_cost;
    String current_name;
}
