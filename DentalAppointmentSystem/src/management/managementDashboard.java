/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package management;

import com.raven.chart.ModelChart;
import java.awt.Color;
import java.awt.Font;
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
import javax.swing.table.JTableHeader;
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        singleBarChart1 = new beans.singleBarChart();
        singleBarChart2 = new beans.singleBarChart();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        revenue_pie_chart = new javaswingdev.chart.PieChart();
        revenue_pie_chart1 = new javaswingdev.chart.PieChart();
        revenue_pie_chart2 = new javaswingdev.chart.PieChart();
        revenue_pie_chart3 = new javaswingdev.chart.PieChart();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

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

        jTable1.setBackground(new java.awt.Color(34, 40, 49));
        jTable1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTable1.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Dentist Name", "Current Activity"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        JTableHeader header = jTable1.getTableHeader();
        header.setFont(new Font("Helvetica Neue", Font.BOLD, 18));
        header.setBackground(Color.decode("#222831"));
        header.setForeground(Color.WHITE);
        jTable1.setFillsViewportHeight(true);
        jTable1.setFocusTraversalKeysEnabled(false);
        jTable1.setFocusable(false);
        jTable1.setGridColor(new java.awt.Color(0, 0, 51));
        jTable1.setRequestFocusEnabled(false);
        jTable1.setRowSelectionAllowed(false);
        jTable1.setTableHeader(header);
        jTable1.setUpdateSelectionOnSort(false);
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
        }

        javax.swing.GroupLayout singleBarChart1Layout = new javax.swing.GroupLayout(singleBarChart1);
        singleBarChart1.setLayout(singleBarChart1Layout);
        singleBarChart1Layout.setHorizontalGroup(
            singleBarChart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 757, Short.MAX_VALUE)
        );
        singleBarChart1Layout.setVerticalGroup(
            singleBarChart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout singleBarChart2Layout = new javax.swing.GroupLayout(singleBarChart2);
        singleBarChart2.setLayout(singleBarChart2Layout);
        singleBarChart2Layout.setHorizontalGroup(
            singleBarChart2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 757, Short.MAX_VALUE)
        );
        singleBarChart2Layout.setVerticalGroup(
            singleBarChart2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("DISTRIBUTION OF VARIETY OF SERVICES THIS WEEK:");

        jLabel4.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("DENTIST SERVICE DISTRIBUTION THIS WEEK:");

        revenue_pie_chart.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N

        revenue_pie_chart1.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        revenue_pie_chart.add(revenue_pie_chart1);
        revenue_pie_chart1.setBounds(0, 0, 0, 0);

        revenue_pie_chart2.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N

        revenue_pie_chart3.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        revenue_pie_chart2.add(revenue_pie_chart3);
        revenue_pie_chart3.setBounds(0, 0, 0, 0);

        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setText("Dentists Revenue this Week");

        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setText("Daily Revenue Distribution");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chart, javax.swing.GroupLayout.DEFAULT_SIZE, 790, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(singleBarChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 757, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(singleBarChart2, javax.swing.GroupLayout.PREFERRED_SIZE, 757, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(revenue_pie_chart2, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(revenue_pie_chart, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(33, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(132, 132, 132)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addGap(124, 124, 124))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(chart, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(singleBarChart1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(singleBarChart2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(revenue_pie_chart, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(revenue_pie_chart2, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel5))))
                .addContainerGap(35, Short.MAX_VALUE))
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javaswingdev.chart.PieChart revenue_pie_chart;
    private javaswingdev.chart.PieChart revenue_pie_chart1;
    private javaswingdev.chart.PieChart revenue_pie_chart2;
    private javaswingdev.chart.PieChart revenue_pie_chart3;
    private beans.singleBarChart singleBarChart1;
    private beans.singleBarChart singleBarChart2;
    // End of variables declaration//GEN-END:variables
}
