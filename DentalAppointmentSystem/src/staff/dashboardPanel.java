package staff;

import com.raven.chart.ModelChart;
import java.awt.*;
import java.sql.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.text.NumberFormat;
import java.util.Locale;
import java.time.LocalDate;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaswingdev.chart.ModelPieChart;
import javaswingdev.chart.PieChart;
import javax.swing.*;


public class dashboardPanel extends javax.swing.JInternalFrame {
    int dent_id;
    
    public dashboardPanel(int dent_id) {
        this.dent_id = dent_id;
        initComponents();
        jLabel4.setText("<html>"
    + "🟦 MON<br><br>"
    + "🟥 TUE<br><br>"
    + "🟧 WED<br><br>"
    + "🟨 THU<br><br>"
    + "🟩 FRI<br><br>"
    + "🟪 SAT"
    + "</html>");

        loadChart();
        revenue_pie_chart();
    } 
    
    public void startAutoRefresh() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                loadChart();
                revenue_pie_chart();
            } catch (Exception e) {
                e.printStackTrace();
                
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    public void loadChart() {
        
        chart.addLegend("Daily Services Done", new Color(245, 189, 135));

        String dailyServicesQuery = "SELECT YEARWEEK(date_done) AS week, DAYNAME(date_done) AS day, COUNT(patient_id) AS daily_count " +
    "FROM services_done WHERE dentist_id = ? AND YEARWEEK(date_done, 1) = YEARWEEK(CURDATE(),1) " +
    "GROUP BY YEARWEEK(date_done), DAYOFWEEK(date_done) " +
    "ORDER BY YEARWEEK(date_done), DAYOFWEEK(date_done)";


        String nextAppointmentQuery = "SELECT a.appointment_date, p.full_name AS patient_name, s.service_name " +
                              "FROM appointment a " +
                              "JOIN patient p ON a.patient_id = p.patient_id " +
                              "JOIN service s ON a.service_id = s.service_id " +
                              "WHERE a.dentist_id = ? " +
                              "AND a.status = 'Pending' " +
                              "AND DATE(a.appointment_date) = CURDATE() " + 
                              "ORDER BY a.appointment_date DESC " +
                              "LIMIT 1";


        String earningsQuery = "SELECT SUM(s.service_cost) AS total " +
                               "FROM services_done sd " +
                               "JOIN service s ON s.service_id = sd.service_id " +
                               "WHERE sd.dentist_id = ? AND YEARWEEK(date_done,1) = YEARWEEK(CURDATE(),1);";
        
        String mostFreq = "SELECT p.patient_id, p.full_name, COUNT(a.appointment_id) AS appointment_count " +
               "FROM appointment a " +
               "INNER JOIN patient p ON a.patient_id = p.patient_id " +
               "WHERE a.dentist_id = ? " +
               "AND a.appointment_date BETWEEN CURDATE() - INTERVAL WEEKDAY(CURDATE()) DAY " +
               "AND CURDATE() + INTERVAL (6 - WEEKDAY(CURDATE())) DAY " +
               "GROUP BY p.patient_id " +
               "ORDER BY appointment_count DESC " +
               "LIMIT 1";

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
                next_patient.setText(patientName + " " + serviceName);
            } else {
                next_patient.setText("NO UPCOMING APPOINTMENTS YET");
            }

            earningsStmt.setInt(1, dent_id);
            ResultSet earningsRs = earningsStmt.executeQuery();

            if (earningsRs.next()) {
                double totalEarnings = earningsRs.getDouble("total");
                if (earningsRs.wasNull()) {
                    totalEarnings = 0.00;
                }

                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
                String formattedEarnings = currencyFormat.format(totalEarnings).replace("₱", "PHP ");
                earnings_field.setText(formattedEarnings);
            } else {
                earnings_field.setText("Php 0.00");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    final void revenue_pie_chart() {
        List<revenueData> dailyRevData = fetchRevenue();
        revenue_pie_chart.clearData(); // Optional: clear previous data

        if (dailyRevData == null || dailyRevData.isEmpty()) {
            JLabel noDataLabel = new JLabel("No services done yet.");
            noDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noDataLabel.setFont(new Font("Arial", Font.BOLD, 16));
            noDataLabel.setForeground(Color.GRAY);

            revenue_pie_chart.setLayout(new BorderLayout());
            revenue_pie_chart.removeAll();
            revenue_pie_chart.add(noDataLabel, BorderLayout.CENTER);
            revenue_pie_chart.revalidate();
            revenue_pie_chart.repaint();
            return;
        }

        Map<String, Float> dayRevenue = new HashMap<>();

        for (revenueData day : dailyRevData) {
            String weekDay = day.getWeek_day();
            float revenue = day.getRevenue();
            float current = dayRevenue.getOrDefault(weekDay, 0f);
            dayRevenue.put(weekDay, current + revenue);
        }

        revenue_pie_chart.removeAll();
        revenue_pie_chart.setLayout(null);

        for (Map.Entry<String, Float> entry : dayRevenue.entrySet()) {
            String week_day = entry.getKey();
            float rev = entry.getValue();
            Color color;

            switch (week_day) {
                case "Monday": color = new Color(0, 0, 255); break;
                case "Tuesday": color = new Color(255, 0, 0); break;
                case "Wednesday": color = new Color(255, 165, 0); break;
                case "Thursday": color = new Color(255, 255, 0); break;
                case "Friday": color = new Color(0, 128, 0); break;
                case "Saturday": color = new Color(128, 0, 128); break;
                default: color = new Color(128, 128, 128); break;
            }

            revenue_pie_chart.addData(new ModelPieChart(week_day, rev, color));
        }

        revenue_pie_chart.setChartType(PieChart.PeiChartType.DONUT_CHART);
    }


    private List<revenueData> fetchRevenue(){
        List<revenueData> dailyRevData = new ArrayList<>();
        String sql = "CALL getDayRev(?)";

        try (Connection conn = Database.getConnection();
            CallableStatement call = conn.prepareCall(sql))
            {
                call.setInt(1, dent_id);
                ResultSet rs = call.executeQuery();
                while (rs.next()) {
                    String day = rs.getString("day_of_week");
                    float rev = rs.getFloat("sum");
                    dailyRevData.add(new revenueData(day,rev));
                }
        } catch (SQLException ex) {
            Logger.getLogger(dashboardPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return dailyRevData;
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
        chart = new com.raven.chart.Chart();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        next_patient = new javax.swing.JLabel();
        earnings_field = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        revenue_pie_chart = new javaswingdev.chart.PieChart();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(34, 40, 49));
        setBorder(null);
        setPreferredSize(new java.awt.Dimension(1522, 938));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(34, 40, 49));

        chart.setBackground(new java.awt.Color(34, 40, 49));
        chart.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("WEEKLY CLINIC REPORT");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("TOTAL SERVICES DONE");

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("YOUR NEXT APPOINTMENT: ");

        next_patient.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        next_patient.setForeground(new java.awt.Color(255, 255, 255));
        next_patient.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        next_patient.setText("YOUR NEXT APPOINTMENT: ");

        earnings_field.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        earnings_field.setForeground(new java.awt.Color(255, 255, 255));
        earnings_field.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        earnings_field.setText("MONEY");

        jLabel5.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("TOTAL REVENUE THIS WEEK:");

        revenue_pie_chart.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("<html>MON<br>TUE<br>WED<br>THU<br>FRI<br>SAT</html>");

        jLabel6.setForeground(new java.awt.Color(102, 102, 102));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Day of Week Distribution");

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
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(chart, javax.swing.GroupLayout.PREFERRED_SIZE, 702, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 702, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(168, 168, 168)
                                        .addComponent(earnings_field, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(41, 41, 41)
                                        .addComponent(revenue_pie_chart, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel4))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(104, 104, 104)
                                        .addComponent(jLabel5))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(196, 196, 196)
                                        .addComponent(jLabel6))))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(next_patient, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(238, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chart, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(earnings_field)
                        .addGap(31, 31, 31)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(revenue_pie_chart, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)))
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(next_patient))
                .addContainerGap(224, Short.MAX_VALUE))
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel next_patient;
    private javaswingdev.chart.PieChart revenue_pie_chart;
    // End of variables declaration//GEN-END:variables
}
