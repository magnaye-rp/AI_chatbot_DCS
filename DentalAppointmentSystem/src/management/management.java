/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package management;

import java.awt.Color;
import staff.dashboardPanel;

/**
 *
 * @author magnaye.rp
 */
public class management extends javax.swing.JFrame {

    public management() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        japanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        home = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        appointments = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        dentists = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        patients = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        payments = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        reports = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        settings = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        displayPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(34, 40, 49));
        jPanel1.setPreferredSize(new java.awt.Dimension(1680, 1050));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        japanel2.setBackground(new java.awt.Color(10, 38, 51));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(248, 246, 240));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("MAGNAYE DENTAL CARE & ORTHODONTICS");

        javax.swing.GroupLayout japanel2Layout = new javax.swing.GroupLayout(japanel2);
        japanel2.setLayout(japanel2Layout);
        japanel2Layout.setHorizontalGroup(
            japanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(japanel2Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1674, Short.MAX_VALUE)
                .addContainerGap())
        );
        japanel2Layout.setVerticalGroup(
            japanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, japanel2Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        jPanel1.add(japanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1680, 80));

        home.setBackground(new java.awt.Color(27, 38, 44));
        home.setPreferredSize(new java.awt.Dimension(50, 50));
        home.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                homeMouseClicked(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/home.png"))); // NOI18N

        javax.swing.GroupLayout homeLayout = new javax.swing.GroupLayout(home);
        home.setLayout(homeLayout);
        homeLayout.setHorizontalGroup(
            homeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );
        homeLayout.setVerticalGroup(
            homeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, homeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(home, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, -1, -1));

        appointments.setBackground(new java.awt.Color(27, 38, 44));
        appointments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                appointmentsMouseClicked(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("APPPOINMENTS");
        jLabel4.setFocusable(false);

        javax.swing.GroupLayout appointmentsLayout = new javax.swing.GroupLayout(appointments);
        appointments.setLayout(appointmentsLayout);
        appointmentsLayout.setHorizontalGroup(
            appointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appointmentsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                .addContainerGap())
        );
        appointmentsLayout.setVerticalGroup(
            appointmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appointmentsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(appointments, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, 270, 50));

        dentists.setBackground(new java.awt.Color(27, 38, 44));
        dentists.setPreferredSize(new java.awt.Dimension(260, 50));
        dentists.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dentistsMouseClicked(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("DENTISTS");
        jLabel5.setFocusable(false);

        javax.swing.GroupLayout dentistsLayout = new javax.swing.GroupLayout(dentists);
        dentists.setLayout(dentistsLayout);
        dentistsLayout.setHorizontalGroup(
            dentistsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dentistsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addContainerGap())
        );
        dentistsLayout.setVerticalGroup(
            dentistsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dentistsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(dentists, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 80, 280, -1));

        patients.setBackground(new java.awt.Color(27, 38, 44));
        patients.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                patientsMouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("PATIENT & HISTORY");
        jLabel6.setFocusable(false);

        javax.swing.GroupLayout patientsLayout = new javax.swing.GroupLayout(patients);
        patients.setLayout(patientsLayout);
        patientsLayout.setHorizontalGroup(
            patientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, patientsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        patientsLayout.setVerticalGroup(
            patientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(patientsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(patients, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 80, 272, 50));

        payments.setBackground(new java.awt.Color(27, 38, 44));
        payments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                paymentsMouseClicked(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("PAYMENTS");
        jLabel8.setFocusable(false);

        javax.swing.GroupLayout paymentsLayout = new javax.swing.GroupLayout(payments);
        payments.setLayout(paymentsLayout);
        paymentsLayout.setHorizontalGroup(
            paymentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paymentsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                .addContainerGap())
        );
        paymentsLayout.setVerticalGroup(
            paymentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paymentsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(payments, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 80, 270, -1));

        reports.setBackground(new java.awt.Color(27, 38, 44));
        reports.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reportsMouseClicked(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("REPORTS");
        jLabel9.setFocusable(false);

        javax.swing.GroupLayout reportsLayout = new javax.swing.GroupLayout(reports);
        reports.setLayout(reportsLayout);
        reportsLayout.setHorizontalGroup(
            reportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reportsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                .addContainerGap())
        );
        reportsLayout.setVerticalGroup(
            reportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reportsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(reports, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 80, 270, -1));

        settings.setBackground(new java.awt.Color(27, 38, 44));
        settings.setPreferredSize(new java.awt.Dimension(272, 50));
        settings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                settingsMouseClicked(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("MANAGE CLINIC");
        jLabel7.setFocusable(false);

        javax.swing.GroupLayout settingsLayout = new javax.swing.GroupLayout(settings);
        settings.setLayout(settingsLayout);
        settingsLayout.setHorizontalGroup(
            settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addContainerGap())
        );
        settingsLayout.setVerticalGroup(
            settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(settings, new org.netbeans.lib.awtextra.AbsoluteConstraints(1410, 80, -1, -1));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 950, 1680, 30));

        displayPanel.setBackground(new java.awt.Color(47, 57, 77));
        displayPanel.setRequestFocusEnabled(false);

        javax.swing.GroupLayout displayPanelLayout = new javax.swing.GroupLayout(displayPanel);
        displayPanel.setLayout(displayPanelLayout);
        displayPanelLayout.setHorizontalGroup(
            displayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1660, Short.MAX_VALUE)
        );
        displayPanelLayout.setVerticalGroup(
            displayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel1.add(displayPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 1660, 800));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        managementDashboard dashPanel = new managementDashboard();
        home.setBackground(Color.decode("#00ADB5"));
        appointments.setBackground(Color.decode("#1B262C"));
        dentists.setBackground(Color.decode("#1B262C"));
        patients.setBackground(Color.decode("#1B262C"));
        payments.setBackground(Color.decode("#1B262C"));
        reports.setBackground(Color.decode("#1B262C"));
        settings.setBackground(Color.decode("#1B262C"));
        displayPanel.removeAll();
        displayPanel.add(dashPanel).setVisible(true);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void homeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeMouseClicked
                home.setBackground(Color.decode("#00ADB5"));
        appointments.setBackground(Color.decode("#1B262C"));
        dentists.setBackground(Color.decode("#1B262C"));
        patients.setBackground(Color.decode("#1B262C"));
        payments.setBackground(Color.decode("#1B262C"));
        reports.setBackground(Color.decode("#1B262C"));
        settings.setBackground(Color.decode("#1B262C"));
        managementDashboard panel = new managementDashboard();
        displayPanel.removeAll();
        displayPanel.add(panel).setVisible(true);
    }//GEN-LAST:event_homeMouseClicked

    private void appointmentsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_appointmentsMouseClicked
        home.setBackground(Color.decode("#1B262C"));
        appointments.setBackground(Color.decode("#00ADB5"));
        dentists.setBackground(Color.decode("#1B262C"));
        patients.setBackground(Color.decode("#1B262C"));
        payments.setBackground(Color.decode("#1B262C"));
        reports.setBackground(Color.decode("#1B262C"));
        settings.setBackground(Color.decode("#1B262C"));
        manageAppoinments panel = new manageAppoinments();
        displayPanel.removeAll();
        displayPanel.add(panel).setVisible(true);
    }//GEN-LAST:event_appointmentsMouseClicked

    private void dentistsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dentistsMouseClicked
        home.setBackground(Color.decode("#1B262C"));
        appointments.setBackground(Color.decode("#1B262C"));
        dentists.setBackground(Color.decode("#00ADB5"));
        patients.setBackground(Color.decode("#1B262C"));
        payments.setBackground(Color.decode("#1B262C"));
        reports.setBackground(Color.decode("#1B262C"));
        settings.setBackground(Color.decode("#1B262C"));
        manageDentists panel = new manageDentists();
        displayPanel.removeAll();
        displayPanel.add(panel).setVisible(true);
    }//GEN-LAST:event_dentistsMouseClicked

    private void patientsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_patientsMouseClicked
        home.setBackground(Color.decode("#1B262C"));
        appointments.setBackground(Color.decode("#1B262C"));
        dentists.setBackground(Color.decode("#1B262C"));
        patients.setBackground(Color.decode("#00ADB5"));
        payments.setBackground(Color.decode("#1B262C"));
        reports.setBackground(Color.decode("#1B262C"));
        settings.setBackground(Color.decode("#1B262C"));
        managePatients panel = new managePatients();
        displayPanel.removeAll();
        displayPanel.add(panel).setVisible(true);
    }//GEN-LAST:event_patientsMouseClicked

    private void paymentsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paymentsMouseClicked
        home.setBackground(Color.decode("#1B262C"));
        appointments.setBackground(Color.decode("#1B262C"));
        dentists.setBackground(Color.decode("#1B262C"));
        patients.setBackground(Color.decode("#1B262C"));
        payments.setBackground(Color.decode("#00ADB5"));
        reports.setBackground(Color.decode("#1B262C"));
        settings.setBackground(Color.decode("#1B262C"));
        managePayments panel = new managePayments();
        displayPanel.removeAll();
        displayPanel.add(panel).setVisible(true);
    }//GEN-LAST:event_paymentsMouseClicked

    private void reportsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reportsMouseClicked
        home.setBackground(Color.decode("#1B262C"));
        appointments.setBackground(Color.decode("#1B262C"));
        dentists.setBackground(Color.decode("#1B262C"));
        patients.setBackground(Color.decode("#1B262C"));
        payments.setBackground(Color.decode("#1B262C"));
        reports.setBackground(Color.decode("#00ADB5"));
        settings.setBackground(Color.decode("#1B262C"));
        manageReports panel = new manageReports();
        displayPanel.removeAll();
        displayPanel.add(panel).setVisible(true);
    }//GEN-LAST:event_reportsMouseClicked

    private void settingsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settingsMouseClicked
        home.setBackground(Color.decode("#1B262C"));
        appointments.setBackground(Color.decode("#1B262C"));
        dentists.setBackground(Color.decode("#1B262C"));
        patients.setBackground(Color.decode("#1B262C"));
        payments.setBackground(Color.decode("#1B262C"));
        reports.setBackground(Color.decode("#1B262C"));
        settings.setBackground(Color.decode("#00ADB5"));
        Settings panel = new Settings();
        displayPanel.removeAll();
        displayPanel.add(panel).setVisible(true);
    }//GEN-LAST:event_settingsMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(management.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(management.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(management.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(management.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new management().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel appointments;
    private javax.swing.JPanel dentists;
    private javax.swing.JPanel displayPanel;
    private javax.swing.JPanel home;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel japanel2;
    private javax.swing.JPanel patients;
    private javax.swing.JPanel payments;
    private javax.swing.JPanel reports;
    private javax.swing.JPanel settings;
    // End of variables declaration//GEN-END:variables
}
