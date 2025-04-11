package management;

import com.mindfusion.common.DateTime;
import com.mindfusion.scheduling.Calendar;
import com.mindfusion.scheduling.CalendarView;
import com.mindfusion.scheduling.ThemeType;
import com.mindfusion.scheduling.model.Appointment;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.swing.JOptionPane;
import staff.Database;
import management.DentistItem;


public class manageAppoinments extends javax.swing.JInternalFrame {

    private com.mindfusion.scheduling.Calendar calendar;
    public manageAppoinments() {
        calendar = new Calendar();
        calendar.setTheme(ThemeType.Dark);
        calendar.setCurrentView(CalendarView.Timetable);
        calendar.getTimetableSettings().setStartTime(7 * 60);
        calendar.getTimetableSettings().setEndTime(17 * 60);
        initComponents();
        javax.swing.plaf.basic.BasicInternalFrameUI ui = (javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI();
        ui.setNorthPane(null);
        loadComboBox();
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

//                System.out.println(patientName + serviceName + appointmentTimestamp);

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

        // Make sure to update the panel's layout properly after loading appointments
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
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        plannerPanel = new javax.swing.JPanel();
        dentitsComboBox = new javax.swing.JComboBox<>();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setFocusCycleRoot(false);
        setFocusTraversalKeysEnabled(false);
        setFocusable(false);
        setIgnoreRepaint(true);
        setPreferredSize(new java.awt.Dimension(1660, 800));
        setSize(new java.awt.Dimension(1660, 800));

        jPanel1.setBackground(new java.awt.Color(34, 40, 49));

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
            .addGap(0, 674, Short.MAX_VALUE)
        );

        dentitsComboBox.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        dentitsComboBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        dentitsComboBox.setLightWeightPopupEnabled(false);
        dentitsComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                dentitsComboBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dentitsComboBox, 0, 400, Short.MAX_VALUE)
                    .addComponent(plannerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(1238, 1238, 1238))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(dentitsComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(plannerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(29, 29, 29))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<DentistItem> dentitsComboBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel plannerPanel;
    // End of variables declaration//GEN-END:variables
}


