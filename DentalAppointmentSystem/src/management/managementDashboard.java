/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package management;

import com.raven.chart.ModelChart;
import java.util.HashMap;
import java.util.List;
import staff.Database;
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaswingdev.chart.ModelPieChart;
import javaswingdev.chart.PieChart;
import javax.swing.table.*;
import javax.swing.*;
import staff.dashboardPanel;
import staff.dentistUI;
import staff.patientsPanel;

public class managementDashboard extends javax.swing.JInternalFrame {

    public managementDashboard() {
        initComponents();
        javax.swing.plaf.basic.BasicInternalFrameUI ui = (javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI();
        ui.setNorthPane(null);
        loadChart();
        setStatus();
        loadBarGraphs();
        loadPie();
    }
    
    public void loadChart() {
        String query = "SELECT d.full_name AS dentist_name, " +
                       "DAYNAME(sd.date_done) AS day, " +
                       "COUNT(sd.patient_id) AS daily_count " +
                       "FROM dentist d " +
                       "LEFT JOIN services_done sd ON d.dentist_id = sd.dentist_id " +
                       "AND YEARWEEK(sd.date_done, 1) = YEARWEEK(CURDATE(), 1) " +
                       "GROUP BY d.full_name, DAYOFWEEK(sd.date_done) " +
                       "ORDER BY d.full_name, DAYOFWEEK(sd.date_done)";

        List<String> dentists = new ArrayList<>();
        Map<String, Integer> dentistIndex = new HashMap<>();
        Map<String, double[]> dayDataMap = new LinkedHashMap<>();

        List<String> daysOfWeek = Arrays.asList(
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
        );
        for (String day : daysOfWeek) {
            dayDataMap.put(day, new double[0]); 
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            Set<String> dentistSet = new LinkedHashSet<>();
            Map<String, Map<String, Double>> tempMap = new LinkedHashMap<>();

            while (rs.next()) {
                String dentist = rs.getString("dentist_name");
                String day = rs.getString("day");
                double count = rs.getDouble("daily_count");

                dentistSet.add(dentist);
                tempMap
                    .computeIfAbsent(dentist, k -> new HashMap<>())
                    .put(day, count);
            }

            int index = 0;
            for (String dentist : dentistSet) {
                dentists.add(dentist);
                dentistIndex.put(dentist, index++);
                chart.addLegend(dentist, getRandomColor());
            }

            for (String day : daysOfWeek) {
                dayDataMap.put(day, new double[dentists.size()]);
            }

            for (int i = 0; i < dentists.size(); i++) {
                String dentist = dentists.get(i);
                Map<String, Double> counts = tempMap.getOrDefault(dentist, new HashMap<>());

                for (String day : daysOfWeek) {
                    double count = counts.getOrDefault(day, 0.0);
                    dayDataMap.get(day)[i] = count;
                }
            }

            for (String day : daysOfWeek) {
                double[] values = dayDataMap.get(day);
                chart.addData(new ModelChart(day, values));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void setStatus() {
        String query = "CALL getAllStatus()";

        try (Connection conn = Database.getConnection();
             CallableStatement stati = conn.prepareCall(query)) {

            ResultSet rs = stati.executeQuery();
            String activity = "";
            String name = "";
            LocalTime now = LocalTime.now();
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                name = rs.getString("full_name");
                activity = rs.getString("status");
                
                if (now.isAfter(LocalTime.of(17, 0)) && now.isBefore(LocalTime.of(17, 30))) {
                    activity = "Closing Time";
                } else if (now.isAfter(LocalTime.of(17, 30))) {
                    activity = "None - Shift Over";
                }else{
                    activity = switch (activity) {
                        case "In Progress" -> "Job In Progress";
                        case "No Show" -> "No Show - On Standby";
                        case "Pending" -> "Waiting for Next Patient";
                        case null -> "On Standby";
                        default -> "On Standby";
                    };
                }
                model.addRow(new Object[]{name, activity});
            }
        } catch (SQLException ex) {
            Logger.getLogger(dentistUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void loadBarGraphs() {
        String query = "CALL getServicesByDentist()";
        String query1 = "CALL getServicesDist()";
        String query2 = "SELECT count(*) as count FROM services_done WHERE YEARWEEK(date_done, 1) = YEARWEEK(CURDATE(), 1);";

        try (Connection conn = Database.getConnection();
             CallableStatement call = conn.prepareCall(query);
             CallableStatement call1 = conn.prepareCall(query1);
             PreparedStatement pstmt = conn.prepareStatement(query2)) {

            ResultSet rs = call.executeQuery();
            ResultSet rs1 = call1.executeQuery();
            ResultSet rs2 = pstmt.executeQuery();

            boolean dataFound = false;

            dentistDist.clearRevenueSegments();
            serviceDist.clearRevenueSegments();

            int idx = 0;
            while (rs.next()) {
                String s_name = rs.getString("NAME");
                int count = rs.getInt("total_services");
                if (count > 0) {
                    dataFound = true;
                    dentistDist.addRevenueSegment(patientsPanel.SegmentColor.getByIndex(idx), s_name, count);
                    idx++;
                }
            }

            int adx = 9;
            while (rs1.next()) {
                String r_name = rs1.getString("NAME");
                int cont = rs1.getInt("total_services");
                if (cont > 0) {
                    dataFound = true;
                    serviceDist.addRevenueSegment(patientsPanel.SegmentColor.getByIndex(adx), r_name, cont);
                    adx--;
                }
            }
            
            if(rs2.next()){
                String count  = rs2.getString("count");
                service_count.setText(count);
            }

            if (!dataFound) {
                dentistDist.addRevenueSegment(Color.GRAY, "No Data Available", 100);  
                serviceDist.addRevenueSegment(Color.GRAY, "No Data Available", 100);
            }
            dentistDist.revalidate();
            serviceDist.repaint();

            dentistDist.revalidate();
            serviceDist.repaint();

        } catch (SQLException ex) {
            Logger.getLogger(patientsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Color getRandomColor() {
        Random rand = new Random();
        return new Color(rand.nextInt(156) + 100, rand.nextInt(156) + 100, rand.nextInt(156) + 100);
    }
    
    public void loadPie() {
    dentist_revenue_pie.clearData();
    service_revenue_pie.clearData();
    
    String dentistRevenue = "CALL getDentistRevenue();";
    String serviceRevenue = "CALL getServiceRevenue();";
    String jatot = "SELECT SUM(s.service_cost) as jatot " +
                    "FROM services_done sd " +
                    "LEFT JOIN service s ON s.service_id = sd.service_id " +
                    "AND YEARWEEK(sd.date_done, 1) = YEARWEEK(CURDATE(), 1);"; //jatot ay total

    boolean hasDentistData = false;
    boolean hasServiceData = false;

    Map<String, Color> dentistColors = new HashMap<>();
    Map<String, Color> serviceColors = new HashMap<>();

    try (Connection conn = Database.getConnection();
         CallableStatement call = conn.prepareCall(dentistRevenue);
         CallableStatement call1 = conn.prepareCall(serviceRevenue);
         PreparedStatement Jatot = conn.prepareCall(jatot)) {

        ResultSet rs = call.executeQuery();
        ResultSet rs1 = call1.executeQuery();
        ResultSet jats = Jatot.executeQuery();

        while (rs.next()) {
            String name = rs.getString("NAME");
            float totalRevenue = rs.getFloat("total_revenue");

            if (totalRevenue > 0) {
                Color color = getRandomColor();
                dentist_revenue_pie.addData(new ModelPieChart(name, totalRevenue, color));
                dentistColors.put(name, color);
                hasDentistData = true;
            }
        }
        while (rs1.next()) {
            String name = rs1.getString("NAME");
            float totalRevenue = rs1.getFloat("total_revenue");

            if (totalRevenue > 0) {
                Color color = getRandomColor();
                service_revenue_pie.addData(new ModelPieChart(name, totalRevenue, color));
                serviceColors.put(name, color); 
                hasServiceData = true;
            }
        }
        if(jats.next()){
            float total = jats.getFloat("jatot");
            if (jats.wasNull()) {
                total = 0;
            }
            revenue.setText("Php " + String.format("%.2f", total));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Add legends for Dentist and Service revenue
    if (hasDentistData) {
        dentist_revenue_pie.setChartType(PieChart.PeiChartType.DONUT_CHART);
        dentist_revenue_pie.revalidate();
        dentist_revenue_pie.repaint();
    } else {
        showNoDataOverlay(dentist_revenue_pie, "No dentist revenue yet");
    }

    if (hasServiceData) {
        service_revenue_pie.setChartType(PieChart.PeiChartType.DONUT_CHART);
        service_revenue_pie.revalidate();
        service_revenue_pie.repaint();
    } else {
        showNoDataOverlay(service_revenue_pie, "No service revenue yet");
    }
}

    private void showNoDataOverlay(PieChart panel, String message) {
        panel.removeAll();
        JLabel label = new JLabel(message);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(Color.GRAY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.setLayout(new BorderLayout());
        panel.add(label, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        chart = new com.raven.chart.Chart();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        dentistDist = new beans.singleBarChart();
        serviceDist = new beans.singleBarChart();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        dentist_revenue_pie = new javaswingdev.chart.PieChart();
        jLabel1 = new javax.swing.JLabel();
        service_revenue_pie = new javaswingdev.chart.PieChart();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        service_count = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        revenue = new javax.swing.JLabel();

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
        jTable1.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jTable1.setForeground(new java.awt.Color(255, 255, 255));
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
        jTable1.setFillsViewportHeight(true);
        jTable1.setFocusTraversalKeysEnabled(false);
        jTable1.setFocusable(false);
        jTable1.setGridColor(new java.awt.Color(32, 32, 57));
        jTable1.setRowHeight(30);
        jTable1.setRowSelectionAllowed(false);
        jTable1.setShowGrid(true);
        jTable1.setSurrendersFocusOnKeystroke(true);
        jTable1.setUpdateSelectionOnSort(false);
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
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

        javax.swing.GroupLayout dentistDistLayout = new javax.swing.GroupLayout(dentistDist);
        dentistDist.setLayout(dentistDistLayout);
        dentistDistLayout.setHorizontalGroup(
            dentistDistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 757, Short.MAX_VALUE)
        );
        dentistDistLayout.setVerticalGroup(
            dentistDistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout serviceDistLayout = new javax.swing.GroupLayout(serviceDist);
        serviceDist.setLayout(serviceDistLayout);
        serviceDistLayout.setHorizontalGroup(
            serviceDistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 757, Short.MAX_VALUE)
        );
        serviceDistLayout.setVerticalGroup(
            serviceDistLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("DISTRIBUTION OF VARIETY OF SERVICES THIS WEEK:");

        jLabel4.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("DENTIST SERVICE DISTRIBUTION THIS WEEK:");

        dentist_revenue_pie.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N

        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setText("Dentists Revenue this Week");
        dentist_revenue_pie.add(jLabel1);
        jLabel1.setBounds(110, 350, 165, 17);

        service_revenue_pie.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N

        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setText("Daily Service Revenue Distribution");
        service_revenue_pie.add(jLabel5);
        jLabel5.setBounds(100, 350, 201, 17);

        jLabel6.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("TOTAL SERVICE DONE THIS WEEK:");

        service_count.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        service_count.setForeground(new java.awt.Color(255, 255, 255));
        service_count.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        service_count.setText("0");

        jLabel7.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("TOTAL REVENUE THIS WEEK:");

        revenue.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        revenue.setForeground(new java.awt.Color(255, 255, 255));
        revenue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        revenue.setText("0");

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
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(dentist_revenue_pie, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(service_revenue_pie, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(dentistDist, javax.swing.GroupLayout.PREFERRED_SIZE, 757, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(serviceDist, javax.swing.GroupLayout.PREFERRED_SIZE, 757, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(service_count, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(revenue, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(148, 148, 148)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(chart, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dentistDist, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(serviceDist, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(service_count))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(service_revenue_pie, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dentist_revenue_pie, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(revenue))
                        .addGap(6, 6, 6)))
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
    private beans.singleBarChart dentistDist;
    private javaswingdev.chart.PieChart dentist_revenue_pie;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel revenue;
    private beans.singleBarChart serviceDist;
    private javax.swing.JLabel service_count;
    private javaswingdev.chart.PieChart service_revenue_pie;
    // End of variables declaration//GEN-END:variables
}
