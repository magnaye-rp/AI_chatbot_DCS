/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package management;

import com.raven.chart.ModelChart;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import staff.Database;

/**
 *
 * @author magnaye.rp
 */
public class managementDashboard extends javax.swing.JInternalFrame {

    /**
     * Creates new form managementDashboard
     */
    public managementDashboard() {
        initComponents();
        javax.swing.plaf.basic.BasicInternalFrameUI ui = (javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI();
        ui.setNorthPane(null);
        loadChart();
    }
    
    public void loadChart() {
    String query = "SELECT d.full_name AS dentist_name, " +
                   "DAYNAME(sd.date_done) AS day, " +
                   "COUNT(sd.patient_id) AS daily_count " +
                   "FROM dentist d " +
                   "LEFT JOIN services_done sd ON d.dentist_id = sd.dentist_id " +
                   "AND YEARWEEK(sd.date_done) = YEARWEEK(CURDATE()) " +
                   "GROUP BY d.full_name, DAYOFWEEK(sd.date_done) " +
                   "ORDER BY d.full_name, DAYOFWEEK(sd.date_done)";

    // dentist → index (for ordering)
    List<String> dentists = new ArrayList<>();
    Map<String, Integer> dentistIndex = new HashMap<>();

    // day → counts[by dentist index]
    Map<String, double[]> dayDataMap = new LinkedHashMap<>();

    List<String> daysOfWeek = Arrays.asList(
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    );
    for (String day : daysOfWeek) {
        dayDataMap.put(day, new double[0]);  // will expand after dentist list is known
    }

    try (Connection conn = Database.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        ResultSet rs = stmt.executeQuery();

        // First pass: get all unique dentists in order
        Set<String> dentistSet = new LinkedHashSet<>();
        Map<String, Map<String, Double>> tempMap = new LinkedHashMap<>(); // dentist → (day → count)

        while (rs.next()) {
            String dentist = rs.getString("dentist_name");
            String day = rs.getString("day");
            double count = rs.getDouble("daily_count");

            dentistSet.add(dentist);
            tempMap
                .computeIfAbsent(dentist, k -> new HashMap<>())
                .put(day, count);
        }

        // Set up dentist order and legends
        int index = 0;
        for (String dentist : dentistSet) {
            dentists.add(dentist);
            dentistIndex.put(dentist, index++);
            chart.addLegend(dentist, getRandomColor());
        }

        // Initialize day data with proper length
        for (String day : daysOfWeek) {
            dayDataMap.put(day, new double[dentists.size()]);
        }

        // Fill day data by dentist
        for (int i = 0; i < dentists.size(); i++) {
            String dentist = dentists.get(i);
            Map<String, Double> counts = tempMap.getOrDefault(dentist, new HashMap<>());

            for (String day : daysOfWeek) {
                double count = counts.getOrDefault(day, 0.0);
                dayDataMap.get(day)[i] = count;
            }
        }

        // Add to chart
        for (String day : daysOfWeek) {
            double[] values = dayDataMap.get(day);
            chart.addData(new ModelChart(day, values));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private Color getRandomColor() {
    Random rand = new Random();
    return new Color(rand.nextInt(156) + 100, rand.nextInt(156) + 100, rand.nextInt(156) + 100);
}





    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        chart = new com.raven.chart.Chart();
        jLabel2 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setFocusCycleRoot(false);
        setFocusTraversalKeysEnabled(false);
        setFocusable(false);
        setIgnoreRepaint(true);
        setPreferredSize(new java.awt.Dimension(1660, 800));
        setSize(new java.awt.Dimension(1660, 800));

        jPanel1.setBackground(new java.awt.Color(34, 40, 49));

        chart.setBackground(new java.awt.Color(34, 40, 49));
        chart.setForeground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("TOTAL SERVICES DONE");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 823, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chart, javax.swing.GroupLayout.PREFERRED_SIZE, 823, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(796, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(chart, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(184, Short.MAX_VALUE))
        );

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.chart.Chart chart;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
