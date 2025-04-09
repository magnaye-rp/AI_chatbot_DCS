package staff;

import com.raven.chart.ModelChart;
import java.awt.Color;
import java.sql.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.text.NumberFormat;
import java.util.Locale;
import java.time.LocalDate;


public class dashboardPanel extends javax.swing.JInternalFrame {
    int dent_id;
    
    public dashboardPanel(int dent_id) {
        this.dent_id = dent_id;
        initComponents();
        loadChart();
    
    } 
    

    public void startAutoRefresh() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                loadChart();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    public void loadChart() {
        chart.addLegend("Daily Services Done", new Color(245, 189, 135));

        String dailyServicesQuery = "SELECT YEARWEEK(date_done) AS week, DAYNAME(date_done) AS day, COUNT(patient_id) AS daily_count " +
                                    "FROM services_done WHERE dentist_id = ? " +
                                    "GROUP BY YEARWEEK(date_done), DAYOFWEEK(date_done) " +
                                    "ORDER BY YEARWEEK(date_done), DAYOFWEEK(date_done)";

        String nextAppointmentQuery = "SELECT a.appointment_date, p.full_name AS patient_name, s.service_name " +
                                      "FROM appointment a " +
                                      "JOIN patient p ON a.patient_id = p.patient_id " +
                                      "JOIN service s ON a.service_id = s.service_id " +
                                      "WHERE a.dentist_id = ? AND a.status = 'Pending' " +
                                      "and appointment_date = CURDATE() " +
                                      "ORDER BY a.appointment_date ASC LIMIT 1";

        String earningsQuery = "SELECT SUM(s.service_cost) AS total " +
                               "FROM services_done sd " +
                               "JOIN service s ON s.service_id = sd.service_id " +
                               "WHERE sd.dentist_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement dailyStmt = conn.prepareStatement(dailyServicesQuery);
             PreparedStatement nextStmt = conn.prepareStatement(nextAppointmentQuery);
             PreparedStatement earningsStmt = conn.prepareStatement(earningsQuery)) {

            dailyStmt.setInt(1, dent_id);
            ResultSet rs = dailyStmt.executeQuery();

            while (rs.next()) {
                String day = rs.getString("day");
                double count = rs.getDouble("daily_count");
                chart.addData(new ModelChart(day, new double[]{count}));
            }

            nextStmt.setInt(1, dent_id);
            ResultSet nextRs = nextStmt.executeQuery();

            if (nextRs.next()) {
                String patientName = nextRs.getString("patient_name");
                String serviceName = nextRs.getString("service_name");
                String date = nextRs.getString("appointment_date");
                next_patient.setText(patientName);
                next_service.setText(serviceName);
                service_date.setText(date);
            } else {
                next_patient.setText("No Upcoming");
                next_service.setText("Appointments Yet");
                service_date.setText(LocalDate.now().toString());
            }

            earningsStmt.setInt(1, dent_id);
            ResultSet earningsRs = earningsStmt.executeQuery();

            if (earningsRs.next()) {
                double totalEarnings = earningsRs.getDouble("total");
                if (earningsRs.wasNull()) {
                    totalEarnings = 0.00;
                }

                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
                String formattedEarnings = currencyFormat.format(totalEarnings).replace("₱", "Php ");
                earnings_field.setText(formattedEarnings);
            } else {
                earnings_field.setText("Php 0.00");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        chart = new com.raven.chart.Chart();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        next_patient = new javax.swing.JLabel();
        next_service = new javax.swing.JLabel();
        earnings_field = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        service_date = new javax.swing.JLabel();

        setBackground(new java.awt.Color(34, 40, 49));
        setBorder(null);
        setPreferredSize(new java.awt.Dimension(1522, 938));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(34, 40, 49));

        chart.setBackground(new java.awt.Color(34, 40, 49));
        chart.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("WEEKLY CLINIC REPORT");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("TOTAL SERVICES DONE");

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("YOUR NEXT APPOINTMENT: ");

        next_patient.setFont(new java.awt.Font("Helvetica Neue", 0, 34)); // NOI18N
        next_patient.setForeground(new java.awt.Color(255, 255, 255));
        next_patient.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        next_patient.setText("YOUR NEXT APPOINTMENT: ");

        next_service.setFont(new java.awt.Font("Helvetica Neue", 0, 34)); // NOI18N
        next_service.setForeground(new java.awt.Color(255, 255, 255));
        next_service.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        next_service.setText("YOUR NEXT APPOINTMENT: ");

        earnings_field.setFont(new java.awt.Font("Helvetica Neue", 0, 48)); // NOI18N
        earnings_field.setForeground(new java.awt.Color(255, 255, 255));
        earnings_field.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        earnings_field.setText("MONEY");

        jLabel5.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("TOTAL REVENUE THIS WEEK:");

        service_date.setFont(new java.awt.Font("Helvetica Neue", 0, 34)); // NOI18N
        service_date.setForeground(new java.awt.Color(255, 255, 255));
        service_date.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        service_date.setText("YOUR NEXT APPOINTMENT: ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1276, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(chart, javax.swing.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(55, 55, 55)
                                .addComponent(earnings_field, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 546, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(385, 385, 385)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(next_patient, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(next_service, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(service_date, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(238, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap(724, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 546, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(250, 250, 250)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chart, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(earnings_field, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(next_patient)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(next_service)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(service_date)
                .addContainerGap(152, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(87, 87, 87)
                    .addComponent(jLabel5)
                    .addContainerGap(791, Short.MAX_VALUE)))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1520, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.chart.Chart chart;
    private javax.swing.JLabel earnings_field;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel next_patient;
    private javax.swing.JLabel next_service;
    private javax.swing.JLabel service_date;
    // End of variables declaration//GEN-END:variables
}
