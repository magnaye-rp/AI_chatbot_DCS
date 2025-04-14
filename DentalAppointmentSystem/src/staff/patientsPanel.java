package staff;

import com.raven.chart.ModelChart;
import java.awt.Color;
import java.awt.Font;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class patientsPanel extends javax.swing.JInternalFrame {
    int dent_id;
    public patientsPanel(int dent_id) {
        this.dent_id = dent_id;
        initComponents();
        loadHistory();
        loadCountChart();
    } 
    
    void loadHistory() {
        String query = "CALL getWeeklyHistory(?)";
        String query1 = "CALL getHistory(?)";
        String getTotal = "SELECT COUNT(*) as count "
                + "FROM services_done sd INNER JOIN service s ON s.service_id = sd.service_id WHERE dentist_id = ? "
                + "AND YEARWEEK(sd.date_done, 1) = YEARWEEK(CURDATE(), 1);";
        String getMTotal = "SELECT COUNT(*) FROM services_done sd WHERE "
                + "MONTH(sd.date_done) = MONTH(CURDATE()) AND "
                + "YEAR(sd.date_done) = YEAR(CURDATE()) AND sd.dentist_id = ?;";

        try (Connection conn = Database.getConnection();
             CallableStatement getHist = conn.prepareCall(query);
             CallableStatement getAllHist = conn.prepareCall(query1);
             PreparedStatement pstmt = conn.prepareStatement(getTotal);
             PreparedStatement Mpstmt = conn.prepareStatement(getMTotal)) {
            pstmt.setInt(1, dent_id);
            ResultSet count = pstmt.executeQuery();
            getHist.setInt(1, dent_id);
            ResultSet rs1 = getHist.executeQuery();
            getAllHist.setInt(1, dent_id);
            ResultSet rs = getAllHist.executeQuery();
            Mpstmt.setInt(1, dent_id);
            ResultSet Mcount = Mpstmt.executeQuery();

            DefaultTableModel model = (DefaultTableModel) patientHistory.getModel();
            model.setRowCount(0);
            
            if(count.next()){
                String cnt = count.getString(1);
                weekly_count.setText(cnt);
            }
            if(Mcount.next()){
                String cnt = Mcount.getString(1);
                rev_week.setText(cnt);
            }

            while (rs.next()) {
                String fullName = rs.getString("full_name");
                String serviceName = rs.getString("service_name");
                float amount = rs.getFloat("service_cost");
                String amnt = String.format("Php %.2f", amount);
                Date dateDone = rs.getDate("date_done");

                model.addRow(new Object[]{fullName, serviceName, amnt, dateDone});
            }
            
            DefaultTableModel model1 = (DefaultTableModel) patientHistory1.getModel();
            model1.setRowCount(0);
            
            while (rs1.next()) {
                String fullName = rs1.getString("full_name");
                String serviceName = rs1.getString("service_name");
                double serviceCost = rs1.getDouble("service_cost");
                Date dateDone = rs1.getDate("date_done");

                model1.addRow(new Object[]{fullName, serviceName, serviceCost, dateDone});
            }

        } catch (SQLException ex) {
            Logger.getLogger(patientsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void loadCountChart() {
        String query = "SELECT s.service_name, COUNT(sd.service_id) AS count "
                + "FROM service s LEFT JOIN services_done sd ON s.service_id = sd.service_id "
                + "AND sd.dentist_id = ? "
                + "AND YEARWEEK(sd.date_done, 1) = YEARWEEK(CURDATE(), 1) "
                + "GROUP BY s.service_id, s.service_name ORDER BY s.service_name;";

        String query1 = "SELECT " +
                    "CONCAT('WEEK ', WEEK(sd.date_done) - WEEK(DATE_SUB(sd.date_done, INTERVAL DAY(sd.date_done) - 1 DAY)) + 1) AS week_label, " +
                    "COUNT(sd.service_id) AS count " +
                    "FROM services_done sd " +
                    "WHERE sd.dentist_id = ? " +
                    "AND MONTH(sd.date_done) = MONTH(CURDATE()) " +
                    "AND YEAR(sd.date_done) = YEAR(CURDATE()) " +
                    "GROUP BY week_label " +
                    "ORDER BY WEEK(sd.date_done);";


        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             PreparedStatement rstmt = conn.prepareStatement(query1)) {

            pstmt.setInt(1, dent_id);
            rstmt.setInt(1, dent_id);

            ResultSet ps = rstmt.executeQuery();
            ResultSet rs = pstmt.executeQuery();

            boolean dataFound = false;

            barChart.clearRevenueSegments();
            barChart1.clearRevenueSegments();

            int idx = 0;
            while (rs.next()) {
                String s_name = rs.getString("service_name");
                int count = rs.getInt("count");
                if (count > 0) {
                    dataFound = true;
                    barChart.addRevenueSegment(SegmentColor.getByIndex(idx), s_name, count);
                    idx++;
                }
            }

            int adx = 9;
            while (ps.next()) {
                String r_name = ps.getString("week_label");
                int cont = ps.getInt("count");
                if (cont > 0) {
                    dataFound = true;
                    barChart1.addRevenueSegment(SegmentColor.getByIndex(adx), r_name, cont);
                    adx--;
                }
            }

            if (!dataFound) {
                barChart.addRevenueSegment(Color.GRAY, "No Data Available", 100);  
                barChart1.addRevenueSegment(Color.GRAY, "No Data Available", 100);
            }
            barChart.revalidate();
            barChart.repaint();

            barChart1.revalidate();
            barChart1.repaint();

        } catch (SQLException ex) {
            Logger.getLogger(patientsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        patientHistory = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        weekly_count = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        rev_week = new javax.swing.JLabel();
        barChart = new beans.singleBarChart();
        barChart1 = new beans.singleBarChart();
        jScrollPane2 = new javax.swing.JScrollPane();
        patientHistory1 = new javax.swing.JTable();

        setBackground(new java.awt.Color(34, 40, 49));
        setBorder(null);
        setPreferredSize(new java.awt.Dimension(1537, 938));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(34, 40, 49));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("PATIENT HISTORY");

        patientHistory.setBackground(new java.awt.Color(57, 62, 70));
        patientHistory.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        patientHistory.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        patientHistory.setForeground(new java.awt.Color(255, 255, 255));
        patientHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Patient Name", "Service Done", "Service Cost", "Date Done"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        patientHistory.setFillsViewportHeight(true);
        patientHistory.setFocusable(false);
        patientHistory.setGridColor(new java.awt.Color(27, 38, 44));
        patientHistory.setRowHeight(30);
        patientHistory.setRowSelectionAllowed(false);
        JTableHeader header = patientHistory.getTableHeader();
        header.setFont(new Font("Helvetica Neue", Font.BOLD, 18));
        header.setBackground(Color.decode("#222831"));
        header.setForeground(Color.WHITE);
        patientHistory.setTableHeader(header);
        jScrollPane1.setViewportView(patientHistory);
        if (patientHistory.getColumnModel().getColumnCount() > 0) {
            patientHistory.getColumnModel().getColumn(0).setResizable(false);
            patientHistory.getColumnModel().getColumn(1).setResizable(false);
            patientHistory.getColumnModel().getColumn(2).setResizable(false);
            patientHistory.getColumnModel().getColumn(3).setResizable(false);
        }

        jLabel5.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("TOTAL SERVICE DONE THIS WEEK:");

        weekly_count.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        weekly_count.setForeground(new java.awt.Color(255, 255, 255));
        weekly_count.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        weekly_count.setText("XX");

        jLabel6.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("TOTAL SERVICE DONE THIS MONTH PER WEEK:");

        rev_week.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        rev_week.setForeground(new java.awt.Color(255, 255, 255));
        rev_week.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        rev_week.setText("XXX");

        javax.swing.GroupLayout barChartLayout = new javax.swing.GroupLayout(barChart);
        barChart.setLayout(barChartLayout);
        barChartLayout.setHorizontalGroup(
            barChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        barChartLayout.setVerticalGroup(
            barChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout barChart1Layout = new javax.swing.GroupLayout(barChart1);
        barChart1.setLayout(barChart1Layout);
        barChart1Layout.setHorizontalGroup(
            barChart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        barChart1Layout.setVerticalGroup(
            barChart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        patientHistory1.setBackground(new java.awt.Color(57, 62, 70));
        patientHistory1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        patientHistory1.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        patientHistory1.setForeground(new java.awt.Color(255, 255, 255));
        patientHistory1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Patient Name", "Service Done", "Service Cost", "Date Done"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        patientHistory1.setFillsViewportHeight(true);
        patientHistory1.setFocusable(false);
        patientHistory1.setGridColor(new java.awt.Color(27, 38, 44));
        patientHistory1.setRowHeight(30);
        patientHistory1.setRowSelectionAllowed(false);
        JTableHeader header1 = patientHistory1.getTableHeader();
        header1.setFont(new Font("Helvetica Neue", Font.BOLD, 18));
        header1.setBackground(Color.decode("#222831"));
        header1.setForeground(Color.WHITE);
        patientHistory1.setTableHeader(header1);
        jScrollPane2.setViewportView(patientHistory1);
        if (patientHistory1.getColumnModel().getColumnCount() > 0) {
            patientHistory1.getColumnModel().getColumn(0).setResizable(false);
            patientHistory1.getColumnModel().getColumn(1).setResizable(false);
            patientHistory1.getColumnModel().getColumn(2).setResizable(false);
            patientHistory1.getColumnModel().getColumn(3).setResizable(false);
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1273, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rev_week, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(weekly_count))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 570, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 579, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(barChart, javax.swing.GroupLayout.DEFAULT_SIZE, 1183, Short.MAX_VALUE)
                            .addComponent(barChart1, javax.swing.GroupLayout.DEFAULT_SIZE, 1183, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(weekly_count)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(barChart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(rev_week))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(barChart1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(73, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private beans.singleBarChart barChart;
    private beans.singleBarChart barChart1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable patientHistory;
    private javax.swing.JTable patientHistory1;
    private javax.swing.JLabel rev_week;
    private javax.swing.JLabel weekly_count;
    // End of variables declaration//GEN-END:variables
    public enum SegmentColor {
        TEAL(new Color(26, 188, 156)),
        BLUE(new Color(52, 152, 219)),
        GREEN(new Color(46, 204, 113)),
        PURPLE(new Color(155, 89, 182)),
        GRAY(new Color(149, 165, 166)),
        BROWN(new Color(121, 85, 72)),
        ORANGE(new Color(243, 156, 18)),
        RED(new Color(231, 76, 60)),
        YELLOW(new Color(241, 196, 15)),
        PINK(new Color(233, 30, 99));
        

        private final Color color;

        SegmentColor(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        // Optional: Get a color by index with wrapping
        public static Color getByIndex(int index) {
            SegmentColor[] values = values();
            return values[index % values.length].getColor();
        }
    }
}
