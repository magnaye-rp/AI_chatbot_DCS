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
    } 
    
    void loadHistory() {
        String query = "CALL getHistory(?)";

        try (Connection conn = Database.getConnection();
             CallableStatement getHist = conn.prepareCall(query)) {

            System.out.println("Dentist ID: " + dent_id); // Debugging line
            getHist.setInt(1, dent_id);
            ResultSet rs = getHist.executeQuery();

            DefaultTableModel model = (DefaultTableModel) patientHistory.getModel();
            model.setRowCount(0); // Clear previous rows

            while (rs.next()) {
                String fullName = rs.getString("full_name");
                String serviceName = rs.getString("service_name");
                double serviceCost = rs.getDouble("service_cost");
                Date dateDone = rs.getDate("date_done");

                model.addRow(new Object[]{fullName, serviceName, serviceCost, dateDone});
            }

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
        monthly_count = new javax.swing.JLabel();

        setBackground(new java.awt.Color(34, 40, 49));
        setBorder(null);
        setPreferredSize(new java.awt.Dimension(1537, 938));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(34, 40, 49));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 48)); // NOI18N
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

        jLabel5.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("TOTAL SERVICE DONE THIS WEEK:");

        weekly_count.setFont(new java.awt.Font("Helvetica Neue", 0, 34)); // NOI18N
        weekly_count.setForeground(new java.awt.Color(255, 255, 255));
        weekly_count.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        weekly_count.setText("XX");

        jLabel6.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("TOTAL SERVICE DONE THIS MONTH:");

        monthly_count.setFont(new java.awt.Font("Helvetica Neue", 0, 34)); // NOI18N
        monthly_count.setForeground(new java.awt.Color(255, 255, 255));
        monthly_count.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        monthly_count.setText("XXX");

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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 636, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(monthly_count)
                            .addComponent(weekly_count))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(weekly_count))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(monthly_count))
                .addContainerGap(171, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel monthly_count;
    private javax.swing.JTable patientHistory;
    private javax.swing.JLabel weekly_count;
    // End of variables declaration//GEN-END:variables
}
