package staff;

import java.awt.Color;
import java.awt.Font;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class paymentsPanel extends javax.swing.JInternalFrame {
    int dent_id;
    public paymentsPanel(int dent_id) {
        this.dent_id = dent_id;
        initComponents();
        loadCountChart();
        loadPaymentTable();
        paymentChart();
    }
    
    void loadCountChart() {
            String weekTots = "SELECT SUM(s.service_cost) AS tots FROM services_done sd "
                    + "INNER JOIN service s ON s.service_id = sd.service_id WHERE sd.dentist_id = ? "
                    + "AND YEARWEEK(sd.date_done, 1) = YEARWEEK(CURDATE(), 1) ";
            
            String monTots = "SELECT SUM(s.service_cost) AS tots FROM services_done sd "
                    + "INNER JOIN service s ON s.service_id = sd.service_id "
                    + "WHERE "
                    + "MONTH(sd.date_done) = MONTH(CURDATE()) AND "
                    + "YEAR(sd.date_done) = YEAR(CURDATE()) AND sd.dentist_id = ?;";
        
            String query = "SELECT s.service_name, SUM(s.service_cost) AS tots "
                    + "FROM services_done sd "
                    + "INNER JOIN service s ON s.service_id = sd.service_id "
                    + "WHERE sd.dentist_id = ? "
                    + "AND YEARWEEK(sd.date_done, 1) = YEARWEEK(CURDATE(), 1) "
                    + "GROUP BY sd.service_id ORDER BY s.service_name;";

            String query1 = "SELECT s.service_name, SUM(s.service_cost) AS tots "
                    + "FROM services_done sd "
                    + "INNER JOIN service s ON s.service_id = sd.service_id "
                    + "WHERE "
                    + "MONTH(sd.date_done) = MONTH(CURDATE()) AND "
                    + "YEAR(sd.date_done) = YEAR(CURDATE()) AND sd.dentist_id = ?;";

            try (Connection conn = Database.getConnection();
                 PreparedStatement wt = conn.prepareStatement(weekTots);
                 PreparedStatement mt = conn.prepareStatement(monTots);
                 PreparedStatement pstmt = conn.prepareStatement(query);
                 PreparedStatement rstmt = conn.prepareStatement(query1)) {

                pstmt.setInt(1, dent_id);
                rstmt.setInt(1, dent_id);
                wt.setInt(1, dent_id);
                mt.setInt(1, dent_id);

                ResultSet ps = rstmt.executeQuery();
                ResultSet rs = pstmt.executeQuery();
                ResultSet Mt = mt.executeQuery();
                ResultSet Wt = wt.executeQuery();

                boolean dataFound = false;

                barChart.clearRevenueSegments();
                barChart1.clearRevenueSegments();
                
                if(Wt.next()){
                    float tots = Wt.getFloat("tots");
                    String amount = String.format("%.2f",tots);
                    Amount.setText(amount);
                }
                if(Mt.next()){
                    float tots = Wt.getFloat("tots");
                    String amount = String.format("%.2f",tots);
                    Amount1.setText(amount);
                }

                int idx = 0;
                while (rs.next()) {
                    String s_name = rs.getString("service_name");
                    int count = rs.getInt("tots");
                    if (count > 0) {
                        dataFound = true;
                        barChart.addRevenueSegment(patientsPanel.SegmentColor.getByIndex(idx), s_name, count);
                        idx++;
                    }
                }

                int adx = 0;
                while (ps.next()) {
                    String r_name = ps.getString("service_name");
                    int cont = ps.getInt("tots");
                    if (cont > 0) {
                        dataFound = true;
                        barChart1.addRevenueSegment(patientsPanel.SegmentColor.getByIndex(adx), r_name, cont);
                        adx++;
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

    void loadPaymentTable(){
        String sql = "SELECT pt.full_name, s.service_name, s.service_cost, p.time_paid FROM services_done sd "
                + "INNER JOIN payment p ON p.job_id=sd.job_id INNER JOIN patient pt ON pt.patient_id=sd.patient_id "
                + "INNER JOIN service s ON s.service_id=sd.service_id WHERE sd.dentist_id = ?;";
        try(Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, dent_id);
            ResultSet rs = pstmt.executeQuery();
            
            DefaultTableModel model = (DefaultTableModel) payment_table.getModel();
            model.setRowCount(0);

            while(rs.next()){
                String name = rs.getString("full_name");
                String s_name = rs.getString("service_name");
                float amount = rs.getFloat("service_cost");
                String amnt = String.format("Php %.2f", amount);
                Timestamp time = rs.getTimestamp("time_paid");
                model.addRow(new Object[]{name, s_name, amnt, time});
            }

        } catch (SQLException ex) {
            Logger.getLogger(paymentsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void paymentChart() {
        paymentChart.setChartTitle("Payments This Week");
        paymentChart.setAxisLabels("Day of Week", "Amount");
        paymentChart.clear();
        paymentChart.setEnabled(false);

        List<revenueData> dailyRevData = fetchRevenue();
        Map<String, Float> dayRevenue = new LinkedHashMap<>(); 

        String[] weekdays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        for (String day : weekdays) {
            dayRevenue.put(day, 0f);
        }

        for (revenueData data : dailyRevData) {
            String weekDay = data.getWeek_day();
            float revenue = data.getRevenue();
            if (dayRevenue.containsKey(weekDay)) {
                float current = dayRevenue.getOrDefault(weekDay, 0f);
                dayRevenue.put(weekDay, current + revenue);
            }
        }

        for (Map.Entry<String, Float> entry : dayRevenue.entrySet()) {
            String week_day = entry.getKey();
            float rev = entry.getValue();
            paymentChart.addValue(rev, "Revenue", week_day); // Assuming 'title' was undefined
        }
    }

    private List<paymentsPanel.revenueData> fetchRevenue(){
        List<paymentsPanel.revenueData> dailyRevData = new ArrayList<>();
        String sql = "CALL getDayRev(?)";

        try (Connection conn = Database.getConnection();
            CallableStatement call = conn.prepareCall(sql))
            {
                call.setInt(1, dent_id);
                ResultSet rs = call.executeQuery();
                while (rs.next()) {
                    String day = rs.getString("day_of_week");
                    float rev = rs.getFloat("sum");
                    dailyRevData.add(new paymentsPanel.revenueData(day,rev));
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
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        payment_table = new javax.swing.JTable();
        Amount = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        Amount1 = new javax.swing.JLabel();
        barChart = new beans.singleBarChart();
        barChart1 = new beans.singleBarChart();
        paymentChart = new beans.HorizontalBarChart();

        setBackground(new java.awt.Color(34, 40, 49));
        setBorder(null);
        setPreferredSize(new java.awt.Dimension(1537, 938));

        jPanel1.setBackground(new java.awt.Color(34, 40, 49));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("PAYMENT REPORT");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 10, 1280, 40));

        payment_table.setBackground(new java.awt.Color(57, 62, 70));
        payment_table.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        payment_table.setForeground(new java.awt.Color(255, 255, 255));
        payment_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Patient Name", "Description", "Amount", "Time Paid"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        JTableHeader header = payment_table.getTableHeader();
        header.setFont(new Font("Helvetica Neue", Font.BOLD, 18));
        header.setBackground(Color.decode("#222831"));
        header.setForeground(Color.WHITE);
        payment_table.setFillsViewportHeight(true);
        payment_table.setFocusable(false);
        payment_table.setGridColor(new java.awt.Color(31, 32, 56));
        payment_table.setRowHeight(30);
        payment_table.setRowSelectionAllowed(false);
        jScrollPane1.setViewportView(payment_table);
        if (payment_table.getColumnModel().getColumnCount() > 0) {
            payment_table.getColumnModel().getColumn(0).setResizable(false);
            payment_table.getColumnModel().getColumn(0).setPreferredWidth(20);
            payment_table.getColumnModel().getColumn(1).setResizable(false);
            payment_table.getColumnModel().getColumn(1).setPreferredWidth(70);
            payment_table.getColumnModel().getColumn(2).setResizable(false);
            payment_table.getColumnModel().getColumn(2).setPreferredWidth(15);
            payment_table.getColumnModel().getColumn(3).setResizable(false);
            payment_table.getColumnModel().getColumn(3).setPreferredWidth(65);
        }

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 600, 420));

        Amount.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        Amount.setForeground(new java.awt.Color(255, 255, 255));
        Amount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        Amount.setText("XX,XXX.00");
        jPanel1.add(Amount, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 540, 170, -1));

        jLabel4.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("TOTAL AMOUNT FOR THIS WEEK: PHP ");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 540, 450, -1));

        jLabel5.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("TOTAL AMOUNT FOR THIS MONTH: PHP ");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 640, 470, -1));

        Amount1.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        Amount1.setForeground(new java.awt.Color(255, 255, 255));
        Amount1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        Amount1.setText("XXX,XXX.00");
        jPanel1.add(Amount1, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 640, 180, -1));

        javax.swing.GroupLayout barChartLayout = new javax.swing.GroupLayout(barChart);
        barChart.setLayout(barChartLayout);
        barChartLayout.setHorizontalGroup(
            barChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1200, Short.MAX_VALUE)
        );
        barChartLayout.setVerticalGroup(
            barChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel1.add(barChart, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 580, 1200, -1));

        javax.swing.GroupLayout barChart1Layout = new javax.swing.GroupLayout(barChart1);
        barChart1.setLayout(barChart1Layout);
        barChart1Layout.setHorizontalGroup(
            barChart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1200, Short.MAX_VALUE)
        );
        barChart1Layout.setVerticalGroup(
            barChart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel1.add(barChart1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 680, 1200, -1));
        jPanel1.add(paymentChart, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 90, 630, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 2, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 774, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Amount;
    private javax.swing.JLabel Amount1;
    private beans.singleBarChart barChart;
    private beans.singleBarChart barChart1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private beans.HorizontalBarChart paymentChart;
    private javax.swing.JTable payment_table;
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
        public static Color getByIndex(int index) {
            SegmentColor[] values = values();
            return values[index % values.length].getColor();
        }
    }
}
