package management;

import com.mindfusion.scheduling.Calendar;
import com.mindfusion.scheduling.CalendarView;
import com.mindfusion.scheduling.ThemeType;
import java.awt.*;
import java.sql.*;
import staff.Database;
import com.mindfusion.common.DateTime;
import com.mindfusion.scheduling.*;
import com.mindfusion.scheduling.model.Appointment;
import java.awt.event.ItemEvent;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.*;
import javax.swing.*;
import static javax.swing.JOptionPane.*;

public class manageAppoinments extends javax.swing.JInternalFrame {
    
    public manageAppoinments() {
        setupCalendar();
        initComponents();
        javax.swing.plaf.basic.BasicInternalFrameUI ui = (javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI();
        ui.setNorthPane(null);
        loadComboBox();
        comboboxes();
        TableColumn column = jTable1.getColumnModel().getColumn(4);
        pendings();
    }
    
    public void setupCalendar() {
        calendar = new Calendar();
        calendar.setCurrentView(CalendarView.Timetable);
        calendar.setTheme(ThemeType.Dark);

        TimetableSettings settings = calendar.getTimetableSettings();

        settings.setStartTime(7 * 60);
        settings.setEndTime(17 * 60);
        settings.setAllowReorderResources(false);
        settings.setTimelineSize(100);
        settings.setTwelveHourFormat(true);
        settings.setCellSize(28);
        settings.setMinColumnSize(30);
        settings.setColumnBandSize(0);
        settings.setShowCurrentTime(true);
        settings.setInfoHeaderSize(10);
        settings.setShowAM(true);
        settings.setHourFormat("hh:mm");

        calendar.setAllowInplaceEdit(false);
        calendar.setAllowDrag(false);
        calendar.setStartEditAfterModify(false);
        calendar.setAllowInplaceCreate(false);
        calendar.setFocusable(false);
        calendar.setEnableDragCreate(false);
        calendar.setEnabled(false);  
        calendar.repaint();
    }

    public void loadAppointments(int dentistId) {

        calendar.getSchedule().getItems().clear();

        String query = "SELECT p.full_name, s.service_name, a.appointment_date, s.duration_minutes " +
                       "FROM appointment a " +
                       "INNER JOIN patient p ON p.patient_id = a.patient_id " +
                       "INNER JOIN service s ON s.service_id = a.service_id " +
                       "WHERE a.status = 'Pending' AND a.dentist_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, dentistId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String patientName = rs.getString("full_name");
                String serviceName = rs.getString("service_name");
                Timestamp appointmentTimestamp = rs.getTimestamp("appointment_date");
                int duration = rs.getInt("duration_minutes");


                if (appointmentTimestamp != null) {
                    java.util.Calendar calendar1 = java.util.Calendar.getInstance();
                    calendar1.setTimeInMillis(appointmentTimestamp.getTime());
                    int year = calendar1.get(java.util.Calendar.YEAR);
                    int month = calendar1.get(java.util.Calendar.MONTH); // 0-based month
                    int day = calendar1.get(java.util.Calendar.DAY_OF_MONTH);
                    int hour = calendar1.get(java.util.Calendar.HOUR_OF_DAY);
                    int minute = calendar1.get(java.util.Calendar.MINUTE);
                    int second = calendar1.get(java.util.Calendar.SECOND);

                    DateTime start = new DateTime(year, month + 1, day, hour, minute, second);

                    DateTime end = start.addMinutes(duration); 
                    Appointment appointment = new Appointment();
                    appointment.setStartTime(start);
                    appointment.setEndTime(end);
                    appointment.setSubject(patientName + " - " + serviceName);

                    if (calendar != null) {
                        calendar.getSchedule().getItems().add(appointment);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading appointments: " + e.getMessage());
        }

        plannerPanel.setPreferredSize(new Dimension(836, 679));
        plannerPanel.setSize(836, 679);
        plannerPanel.setLayout(new BorderLayout());

        if (calendar != null) {
            calendar.setBounds(0, 0, 836, 679);
            calendar.setPreferredSize(new Dimension(836, 679));

            plannerPanel.add(calendar, BorderLayout.CENTER);
            plannerPanel.revalidate();
            plannerPanel.repaint();
        }
    }
    
    public void loadComboBox(){
        try (Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT dentist_id, full_name FROM dentist")) {

           ResultSet rs = stmt.executeQuery();

           while (rs.next()) {
               int id = rs.getInt("dentist_id");
               String name = rs.getString("full_name");
               dentitsComboBox.addItem(new DentistItem(id, name));
           }

       } catch (SQLException e) {
           e.printStackTrace();
       }
    }
    
    void comboboxes(){
        try (Connection conn = Database.getConnection();
            CallableStatement stmt = conn.prepareCall("CALL getAvailableDentistATM('Consultation')");
                PreparedStatement stmt1 = conn.prepareStatement("SELECT service_name FROM service")) {

           ResultSet rs = stmt.executeQuery();
           ResultSet rs1 = stmt1.executeQuery();
           dents.removeAllItems();
           servs.removeAllItems(); 

           while (rs.next()) {
               String name = rs.getString("full_name");
               dents.addItem(name);
           }
           while (rs1.next()) {
               String name = rs1.getString("service_name");
               servs.addItem(name);
           }

       } catch (SQLException e) {
           e.printStackTrace();
       }
    }
    
    void pendings(){
        String sql = "SELECT * FROM pending_appointments";
        try(Connection conn = Database.getConnection();
            Statement st = conn.createStatement()){
            ResultSet rs = st.executeQuery(sql);
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);
            while(rs.next()){
                int id = rs.getInt("appointment_id");
                String d_name = rs.getString("dentist_name");
                String p_name = rs.getString("patient_name");
                String s_name = rs.getString("service_name");
                Timestamp time = rs.getTimestamp("appointment_date");
                model.addRow(new Object[]{id,d_name,p_name,s_name,time});
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(manageAppoinments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void inProgress(String id){
        String sql = "UPDATE appointment SET status= 'In Progress' WHERE appointment_id = ?";
        try(Connection conn = Database.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql)){
            pst.setString(1, id);
            pst.executeUpdate();
            pendings();
        } catch (SQLException ex) {
            Logger.getLogger(manageAppoinments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void blockdates(){
        if (custom) {
        datePicker.clearBlockedDateTimeRanges();
        String name = (String) dents.getSelectedItem();
        String query = "SELECT a.appointment_date, s.duration_minutes "
                     + "FROM appointment a "
                     + "INNER JOIN dentist d ON d.dentist_id = a.dentist_id "
                     + "INNER JOIN service s ON s.service_id = a.service_id "
                     + "WHERE d.full_name = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDateTime time = rs.getTimestamp("appointment_date")
                       .toLocalDateTime()
                       .minusMinutes(before_time);
                int minutes = rs.getInt("duration_minutes") + before_time;
                datePicker.setBlockedDateTimeRange(time, minutes);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        plannerPanel = new javax.swing.JPanel();
        dentitsComboBox = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        nameField = new javax.swing.JTextField();
        addressField = new javax.swing.JTextField();
        numField = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        servs = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        dents = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        timeCheckBox = new javax.swing.JCheckBox();
        datePicker = new beans.CustomDateTimePickerBean();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator6 = new javax.swing.JSeparator();
        jSeparator7 = new javax.swing.JSeparator();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setFocusCycleRoot(false);
        setFocusTraversalKeysEnabled(false);
        setFocusable(false);
        setIgnoreRepaint(true);
        setPreferredSize(new java.awt.Dimension(1660, 800));
        setSize(new java.awt.Dimension(1660, 800));

        jPanel1.setBackground(new java.awt.Color(34, 40, 49));
        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel1.setFocusTraversalKeysEnabled(false);
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        plannerPanel.setAutoscrolls(true);

        plannerPanel.add(calendar, BorderLayout.CENTER);

        javax.swing.GroupLayout plannerPanelLayout = new javax.swing.GroupLayout(plannerPanel);
        plannerPanel.setLayout(plannerPanelLayout);
        plannerPanelLayout.setHorizontalGroup(
            plannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        plannerPanelLayout.setVerticalGroup(
            plannerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 686, Short.MAX_VALUE)
        );

        jPanel1.add(plannerPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 59, 575, -1));

        dentitsComboBox.setBackground(new java.awt.Color(27, 38, 44));
        dentitsComboBox.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        dentitsComboBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        dentitsComboBox.setLightWeightPopupEnabled(false);
        dentitsComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                dentitsComboBoxItemStateChanged(evt);
            }
        });
        jPanel1.add(dentitsComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 12, 575, 41));

        jTable1.setBackground(new java.awt.Color(57, 62, 70));
        jTable1.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        jTable1.setForeground(new java.awt.Color(255, 255, 255));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "appoinment_id", "Dentist Name", "Patient Name", "Service", "Appointment Time", ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setColumnSelectionAllowed(true);
        jTable1.setFillsViewportHeight(true);
        jTable1.setGridColor(new java.awt.Color(32, 32, 57));
        jTable1.setRowHeight(30);
        jTable1.setShowGrid(true);
        jTable1.setSurrendersFocusOnKeystroke(true);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.setUpdateSelectionOnSort(false);
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
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

        int lastColumn = jTable1.getColumnCount() - 1;
        TableColumn column = jTable1.getColumnModel().getColumn(lastColumn);

        column.setCellRenderer(new TableButtonRendererEditor("Start Service", e -> {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                String value = jTable1.getModel().getValueAt(jTable1.convertRowIndexToModel(row), 0).toString();
                inProgress(value);
                JOptionPane.showMessageDialog(null, "The Patient can now go to the Operating Room");
            }
        }));

        column.setCellEditor(new TableButtonRendererEditor("Start Service", e -> {
            int row = jTable1.getSelectedRow();
            if (row != -1) {
                String value = jTable1.getModel().getValueAt(jTable1.convertRowIndexToModel(row), 0).toString();
                inProgress(value);
                JOptionPane.showMessageDialog(null, "The Patient can now go to the Operating Room");
            }
        }));

        jTable1.getColumnModel().removeColumn(jTable1.getColumnModel().getColumn(0));

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 40, 1000, 420));

        jButton1.setText("ADD");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1490, 720, 110, 30));

        nameField.setBackground(new java.awt.Color(27, 38, 44));
        nameField.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        nameField.setForeground(new java.awt.Color(104, 109, 118));
        nameField.setText("e.g. Juan Dela Cruz");
        nameField.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 8, 1, 1));
        nameField.setFocusTraversalKeysEnabled(false);
        nameField.setHighlighter(null);
        nameField.setPreferredSize(new java.awt.Dimension(150, 23));
        nameField.setVerifyInputWhenFocusTarget(false);
        nameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nameFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                nameFieldFocusLost(evt);
            }
        });
        jPanel1.add(nameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 540, 460, 40));

        addressField.setBackground(new java.awt.Color(27, 38, 44));
        addressField.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        addressField.setForeground(new java.awt.Color(104, 109, 118));
        addressField.setText("e.g. Lucban Balayan Batangas");
        addressField.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 8, 1, 1));
        addressField.setFocusTraversalKeysEnabled(false);
        addressField.setHighlighter(null);
        addressField.setVerifyInputWhenFocusTarget(false);
        addressField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sdfg(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                addressFieldFocusLost(evt);
            }
        });
        jPanel1.add(addressField, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 690, 460, 40));

        numField.setBackground(new java.awt.Color(27, 38, 44));
        numField.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        numField.setForeground(new java.awt.Color(104, 109, 118));
        numField.setText("09XX - XXXX - XXX");
        numField.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 8, 1, 1));
        numField.setFocusTraversalKeysEnabled(false);
        numField.setHighlighter(null);
        numField.setVerifyInputWhenFocusTarget(false);
        numField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                numFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                numFieldFocusLost(evt);
            }
        });
        jPanel1.add(numField, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 620, 460, 40));
        numField.getAccessibleContext().setAccessibleName("");

        jCheckBox1.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jCheckBox1.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBox1.setSelected(true);
        jCheckBox1.setText("New Customer");
        jCheckBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox1ItemStateChanged(evt);
            }
        });
        jPanel1.add(jCheckBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 720, 160, 30));

        servs.setBackground(new java.awt.Color(27, 38, 44));
        servs.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        servs.setForeground(new java.awt.Color(255, 255, 255));
        servs.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        servs.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        servs.setFocusTraversalKeysEnabled(false);
        servs.setFocusable(false);
        servs.setLightWeightPopupEnabled(false);
        servs.setOpaque(true);
        servs.setRequestFocusEnabled(false);
        servs.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                servsItemStateChanged(evt);
            }
        });
        jPanel1.add(servs, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 530, 420, 40));

        jPanel2.setBackground(new java.awt.Color(34, 40, 49));

        jLabel1.setBackground(new java.awt.Color(34, 40, 49));
        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("ADD APPOINTMENT");
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 470, 260, 40));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 490, 1010, 20));

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 500, 10, 260));

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Address");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 670, 150, -1));

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Customer Name");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 520, 150, -1));

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Contact Number");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 600, 150, -1));

        dents.setBackground(new java.awt.Color(27, 38, 44));
        dents.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        dents.setForeground(new java.awt.Color(255, 255, 255));
        dents.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        dents.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        dents.setFocusTraversalKeysEnabled(false);
        dents.setFocusable(false);
        dents.setLightWeightPopupEnabled(false);
        dents.setOpaque(true);
        dents.setRequestFocusEnabled(false);
        dents.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                dentsItemStateChanged(evt);
            }
        });
        jPanel1.add(dents, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 600, 420, 40));

        jLabel5.setBackground(new java.awt.Color(34, 40, 49));
        jLabel5.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("APPOINTMENTS FOR TODAY");
        jLabel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 10, 370, 20));

        timeCheckBox.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        timeCheckBox.setForeground(new java.awt.Color(255, 255, 255));
        timeCheckBox.setText("Custom Time");
        timeCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                timeCheckBoxItemStateChanged(evt);
            }
        });
        jPanel1.add(timeCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 670, -1, 30));
        jPanel1.add(datePicker, new org.netbeans.lib.awtextra.AbsoluteConstraints(1350, 670, -1, -1));

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, 10, 760));
        jPanel1.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1270, 20, 370, -1));
        jPanel1.add(jSeparator7, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 20, 370, 10));

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

        datePicker.setVisible(false);
    }// </editor-fold>//GEN-END:initComponents

    private void dentitsComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_dentitsComboBoxItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            DentistItem selected = (DentistItem) dentitsComboBox.getSelectedItem();
            if (selected != null) {
                int dentistId = selected.getId();
                System.out.println("Selected Dentist ID: " + dentistId);
                loadAppointments(dentistId);
            }
        }
    }//GEN-LAST:event_dentitsComboBoxItemStateChanged

    private void jCheckBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox1ItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
                    isnew = true;
                    addressField.setVisible(isnew);
                    jLabel2.setVisible(isnew);
                    try (Connection conn = Database.getConnection();
                        PreparedStatement stmt = conn.prepareStatement("SELECT full_name FROM dentist")) {

                       ResultSet rs = stmt.executeQuery();
                       dents.removeAllItems();

                       while (rs.next()) {
                           String name = rs.getString("full_name");
                           dents.addItem(name);
                       }

                   } catch (SQLException e) {
                       e.printStackTrace();
                   }
                } else {
                    isnew = false;
                    addressField.setVisible(isnew);
                    jLabel2.setVisible(isnew);
                    addressField.setText("e.g. Lucban Balayan Batangas");
                    addressField.setForeground(Color.decode("#686D76")); 
                }
    }//GEN-LAST:event_jCheckBox1ItemStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
            if((nameField.getText()).equals("e.g. Juan Dela Cruz") || (numField.getText()).equals("09XX - XXXX - XXX")){
                JOptionPane.showMessageDialog(rootPane, "Please Fill all Fields", "Problem in adding to Appointments", INFORMATION_MESSAGE);
                return;
            }
            if((addressField.getText()).equals("e.g. Lucban Balayan Batangas") && isnew){
                JOptionPane.showMessageDialog(rootPane, "Please Fill all Fields", "Problem in adding to Appointments", INFORMATION_MESSAGE);
                return;
            }
            String name = nameField.getText();
            String num = numField.getText();
            String address = addressField.getText();
            String dentist = (String) dents.getSelectedItem();
            String service = (String) servs.getSelectedItem();
            String call = "CALL existingUserAppoinment(?,?,?,?,?,?)";
            LocalDateTime time = LocalDateTime.now();
            String status = "In Progress";
            if(custom){
                time = datePicker.getDateTime();
                status = "Pending";
            }
            if(isnew){
                call = "CALL newUserAppoinment(?,?,?,?,?,?,?)";
            }
            try(Connection conn = Database.getConnection();
                CallableStatement cstmt = (CallableStatement) conn.prepareCall(call)){ 
                cstmt.setString(1, name);
                cstmt.setString(2, num);
                cstmt.setString(3, dentist);
                cstmt.setString(4, service);
                cstmt.setTimestamp(5, Timestamp.valueOf(time));
                cstmt.setString(6, status);
                if(isnew){cstmt.setString(7, address);}
                ResultSet rs = cstmt.executeQuery();
                if(rs.next()){
                String output = rs.getString("status");
                System.out.println("Status from DB: " + output); 
                if(output.equals("W move: Appointment created successfully")){
                    if(custom){
                        JOptionPane.showMessageDialog(rootPane, "This customer's appointment is booked!!!", "Added to Appointments", INFORMATION_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(rootPane, "This customer is ready to go!!!", "Added to Appointments", INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(rootPane, "There is conflict adding to database, Please check if there's no Schedule Conflict", output, INFORMATION_MESSAGE);
                }
            }      
            pendings();
            comboboxes();
            loadAppointments(1);
        } catch (SQLException ex) {
            Logger.getLogger(manageAppoinments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void nameFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameFieldFocusGained
        if((nameField.getText()).equals("e.g. Juan Dela Cruz")){
            nameField.setText("");
            nameField.setForeground(Color.WHITE);
        }

        nameField.setBackground(Color.decode("#00ADB5"));
    }//GEN-LAST:event_nameFieldFocusGained

    private void nameFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameFieldFocusLost
        if((nameField.getText()).equals("")){
            nameField.setText("e.g. Juan Dela Cruz");
            nameField.setForeground(Color.decode("#686D76"));
        }
        nameField.setBackground(Color.decode("#1B262C"));
    }//GEN-LAST:event_nameFieldFocusLost

    private void numFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_numFieldFocusGained
        if((numField.getText()).equals("09XX - XXXX - XXX")){
            numField.setText("");
            numField.setForeground(Color.WHITE);
        }
        numField.setBackground(Color.decode("#00ADB5"));
    }//GEN-LAST:event_numFieldFocusGained

    private void numFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_numFieldFocusLost
        if((numField.getText()).equals("")){
            numField.setText("09XX - XXXX - XXX");
            numField.setForeground(Color.decode("#686D76"));
        }
        numField.setBackground(Color.decode("#1B262C"));
    }//GEN-LAST:event_numFieldFocusLost

    private void sdfg(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sdfg
        if((addressField.getText()).equals("e.g. Lucban Balayan Batangas")){
            addressField.setText("");
            addressField.setForeground(Color.WHITE);
        }
        addressField.setBackground(Color.decode("#00ADB5"));
    }//GEN-LAST:event_sdfg

    private void addressFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_addressFieldFocusLost
        if((addressField.getText()).equals("")){
            addressField.setText("e.g. Lucban Balayan Batangas");
            addressField.setForeground(Color.decode("#686D76"));
        }
        addressField.setBackground(Color.decode("#1B262C"));
    }//GEN-LAST:event_addressFieldFocusLost

    private void servsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_servsItemStateChanged
        String query = "CALL getAvailableDentistATM(?)";
        String query1 = "SELECT duration_minutes FROM service WHERE service_name = ?";
        String serv = (String) servs.getSelectedItem();
        try(Connection conn = Database.getConnection();
             CallableStatement call = conn.prepareCall(query);
             PreparedStatement ps = conn.prepareStatement(query1)){
            call.setString(1, serv);
            ps.setString(1,serv);
            ResultSet rs = call.executeQuery();
            ResultSet rs1 = ps.executeQuery();
            if(rs1.next()){
                before_time = rs1.getInt(1) - 30;
            }
            dents.removeAllItems();

           while (rs.next()) {
               String name = rs.getString("full_name");
               dents.addItem(name);
           }
           blockdates();
            
        } catch (SQLException ex) {
            Logger.getLogger(manageAppoinments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_servsItemStateChanged

    private void timeCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_timeCheckBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
                    custom = true;
                    datePicker.setVisible(true);
                    blockdates();
                } else {
                    custom = false;
                    comboboxes();
                    datePicker.setVisible(false);
                }
    }//GEN-LAST:event_timeCheckBoxItemStateChanged

    private void dentsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_dentsItemStateChanged
         blockdates();
    }//GEN-LAST:event_dentsItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField addressField;
    private beans.CustomDateTimePickerBean datePicker;
    private javax.swing.JComboBox<DentistItem> dentitsComboBox;
    private javax.swing.JComboBox<String> dents;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField nameField;
    private javax.swing.JTextField numField;
    private javax.swing.JPanel plannerPanel;
    private javax.swing.JComboBox<String> servs;
    private javax.swing.JCheckBox timeCheckBox;
    // End of variables declaration//GEN-END:variables
    private com.mindfusion.scheduling.Calendar calendar;
    private boolean isnew = true;
    private boolean custom = false;
    private int before_time = 0;
}


