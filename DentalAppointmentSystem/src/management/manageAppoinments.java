package management;

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
import com.mindfusion.common.DateTime;
import com.mindfusion.drawing.SolidBrush;
import com.mindfusion.scheduling.*;
import com.mindfusion.scheduling.model.Appointment;
import com.mindfusion.scheduling.model.Style;
import java.awt.Color;
import java.awt.Font;



public class manageAppoinments extends javax.swing.JInternalFrame {

    private com.mindfusion.scheduling.Calendar calendar;
    public manageAppoinments() {
        setupCalendar();
        initComponents();
        javax.swing.plaf.basic.BasicInternalFrameUI ui = (javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI();
        ui.setNorthPane(null);
        loadComboBox();
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

        dentitsComboBox.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        dentitsComboBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        dentitsComboBox.setLightWeightPopupEnabled(false);
        dentitsComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                dentitsComboBoxItemStateChanged(evt);
            }
        });
        jPanel1.add(dentitsComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 12, 575, 41));

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


