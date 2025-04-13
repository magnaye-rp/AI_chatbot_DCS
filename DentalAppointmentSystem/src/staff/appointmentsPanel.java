package staff;

import com.mindfusion.common.DateTime;
import com.mindfusion.scheduling.*;
import com.mindfusion.scheduling.model.*;
import javax.swing.JOptionPane;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class appointmentsPanel extends javax.swing.JInternalFrame {
    
    private com.mindfusion.scheduling.Calendar calendar;
    int dent_id;
    public appointmentsPanel(int dent_id) {
        this.dent_id = dent_id;
        setupCalendar();
        initComponents();
        loadAppointments(this.dent_id);
        loadAppoinmentsTable();
        appointmentRate();
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

                System.out.println(patientName + serviceName + appointmentTimestamp);

                if (appointmentTimestamp != null) {
                    // Create MindFusion DateTime object from the Timestamp
                    java.util.Calendar calendar1 = java.util.Calendar.getInstance();
                    calendar1.setTimeInMillis(appointmentTimestamp.getTime());
                    int year = calendar1.get(java.util.Calendar.YEAR);
                    int month = calendar1.get(java.util.Calendar.MONTH); // 0-based month
                    int day = calendar1.get(java.util.Calendar.DAY_OF_MONTH);
                    int hour = calendar1.get(java.util.Calendar.HOUR_OF_DAY);
                    int minute = calendar1.get(java.util.Calendar.MINUTE);
                    int second = calendar1.get(java.util.Calendar.SECOND);

                    // Create MindFusion DateTime object (adjust month by +1 because Java Calendar uses 0-based month)
                    DateTime start = new DateTime(year, month + 1, day, hour, minute, second);

                    // Add the duration (in minutes) to the start time
                    DateTime end = start.addMinutes(duration); // Add minutes using MindFusion's addMinutes method


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
    
    void loadAppoinmentsTable() {
        String query = "CALL getAppointments(?)";

        try (Connection conn = Database.getConnection();
             CallableStatement appoint = conn.prepareCall(query)) {

            appoint.setInt(1, dent_id);
            ResultSet rs = appoint.executeQuery();

            DefaultTableModel model = (DefaultTableModel) appointments_table.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                String fullName = rs.getString("full_name");
                String serviceName = rs.getString("service_name");
                Timestamp dateDone = rs.getTimestamp("appointment_date");

                model.addRow(new Object[]{fullName, serviceName, dateDone});
            }

        } catch (SQLException ex) {
        }
    }

    void appointmentRate(){
        appointment_turnout.setChartTitle("This Week's Appointment Status");
        appointment_turnout.setAxisLabels("Date", "Rate");
        String call = "CALL getAppRatio(?)";
        try(Connection conn = Database.getConnection();
            CallableStatement callstmt = conn.prepareCall(call)){
            callstmt.setInt(1, dent_id);
            ResultSet rs = callstmt.executeQuery();
            
            while(rs.next()){
                String date = rs.getString("date");        
                String status = rs.getString("status");    
                int value = rs.getInt("value");            

                appointment_turnout.addData(status, date, value);
            }
        } catch (SQLException ex) {
            Logger.getLogger(appointmentsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        plannerPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        appointments_table = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        appointment_turnout = new beans.LineChartBean();

        setBackground(new java.awt.Color(34, 40, 49));
        setBorder(null);
        setPreferredSize(new java.awt.Dimension(1522, 938));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(34, 40, 49));
        jPanel1.setSize(new java.awt.Dimension(1288, 796));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("APPOINTMENTS");

        plannerPanel.setAutoscrolls(true);
        plannerPanel.setFocusTraversalKeysEnabled(false);
        plannerPanel.setFocusable(false);
        plannerPanel.setRequestFocusEnabled(false);
        plannerPanel.setVerifyInputWhenFocusTarget(false);
        plannerPanel.setLayout(null);
        plannerPanel.add(calendar, BorderLayout.CENTER);

        appointments_table.setBackground(new java.awt.Color(57, 62, 70));
        appointments_table.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        appointments_table.setForeground(new java.awt.Color(255, 255, 255));
        appointments_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Patient Name", "Service Booked", "Date and Time"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        JTableHeader header = appointments_table.getTableHeader();
        header.setFont(new Font("Helvetica Neue", Font.BOLD, 18));
        header.setBackground(Color.decode("#222831"));
        header.setForeground(Color.WHITE);
        appointments_table.setFillsViewportHeight(true);
        appointments_table.setRowHeight(30);
        appointments_table.setRowSelectionAllowed(false);
        appointments_table.setShowGrid(false);
        jScrollPane1.setViewportView(appointments_table);
        if (appointments_table.getColumnModel().getColumnCount() > 0) {
            appointments_table.getColumnModel().getColumn(0).setResizable(false);
            appointments_table.getColumnModel().getColumn(1).setResizable(false);
            appointments_table.getColumnModel().getColumn(2).setResizable(false);
        }

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("All Upcoming Appointments");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1276, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(plannerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 491, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 668, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 668, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appointment_turnout, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 668, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(45, 45, 45))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plannerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 647, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(appointment_turnout, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(60, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private beans.LineChartBean appointment_turnout;
    private javax.swing.JTable appointments_table;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel plannerPanel;
    // End of variables declaration//GEN-END:variables
}
