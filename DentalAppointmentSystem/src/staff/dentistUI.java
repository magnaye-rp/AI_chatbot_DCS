package staff;

import java.sql.*;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;
import java.time.LocalTime;

public final class dentistUI extends javax.swing.JFrame {
    int dent_id;
    int apptmt_id = -1;
    public dentistUI(int dent_id) {
        this.dent_id = dent_id;
        initComponents();
        getDentits();
        setStatus();
        autoTime();
    }
    
    public void autoTime() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy - hh:mm a");
                String formattedDateTime = now.format(formatter);
                SwingUtilities.invokeLater(() -> service_date.setText(formattedDateTime));
                setStatus();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    
    public void getDentits() {
        String query = "CALL getDentistName(?)";

        try (Connection conn = Database.getConnection();
             CallableStatement getDents = conn.prepareCall(query)) {

            getDents.setInt(1, dent_id);
            ResultSet rs = getDents.executeQuery();
            if (rs.next()) {
                String dentistName = rs.getString(1); 
                jLabel7.setText(dentistName);
            }

        } catch (SQLException ex) {
            Logger.getLogger(dentistUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public void setStatus() {
        String query = "CALL getStatus(?)";

        try (Connection conn = Database.getConnection();
             CallableStatement stati = conn.prepareCall(query)) {

            stati.setInt(1, dent_id);
            ResultSet rs = stati.executeQuery();

            LocalTime now = LocalTime.now();

            if (now.isAfter(LocalTime.of(17, 0)) && now.isBefore(LocalTime.of(17, 30))) {
                activity.setText("Closing Time");
                job_done.setVisible(false);
                return;
            } else if (now.isAfter(LocalTime.of(17, 30))) {
                activity.setText("None - Shift Over");
                job_done.setVisible(false);
                return;
            }

            if (rs.next()) {
                String status = rs.getString("status");
                apptmt_id = rs.getInt("appointment_id");

                switch (status) {
                    case "In Progress":
                        activity.setText("Job In Progress");
                        job_done.setVisible(true);
                        break;
                    case "No Show":
                        activity.setText("No Show - On Standby");
                        job_done.setVisible(false);
                        break;
                    case "Pending":
                        activity.setText("Waiting for Next Patient");
                        job_done.setVisible(false);
                        break;
                    default:
                        activity.setText("Unknown Status");
                        job_done.setVisible(false);
                        break;
                }
            } else {
                activity.setText("On Standby");
                job_done.setVisible(false);
            }

        } catch (SQLException ex) {
            Logger.getLogger(dentistUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        displayPanel = new javax.swing.JPanel();
        dashButton = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        apntButton = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        ptntButton = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        pymtButton = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        infoPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        activity = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        job_done = new javax.swing.JButton();
        service_date = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dental Clinic System");

        jPanel1.setBackground(new java.awt.Color(34, 40, 49));

        jPanel2.setBackground(new java.awt.Color(27, 38, 44));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(248, 246, 240));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("MAGNAYE DENTAL CARE & ORTHODONTICS");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1820, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(34, 40, 49));

        jSeparator1.setForeground(new java.awt.Color(27, 38, 44));

        jSeparator2.setForeground(new java.awt.Color(27, 38, 44));

        jSeparator3.setForeground(new java.awt.Color(27, 38, 44));
        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        displayPanel.setBackground(new java.awt.Color(47, 57, 77));
        displayPanel.setRequestFocusEnabled(false);

        javax.swing.GroupLayout displayPanelLayout = new javax.swing.GroupLayout(displayPanel);
        displayPanel.setLayout(displayPanelLayout);
        displayPanelLayout.setHorizontalGroup(
            displayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1522, Short.MAX_VALUE)
        );
        displayPanelLayout.setVerticalGroup(
            displayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        dashButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dashButtonMouseClicked(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 0, 26)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/dashboard.png"))); // NOI18N
        jLabel3.setText("DASHBOARD");
        jLabel3.setFocusable(false);

        javax.swing.GroupLayout dashButtonLayout = new javax.swing.GroupLayout(dashButton);
        dashButton.setLayout(dashButtonLayout);
        dashButtonLayout.setHorizontalGroup(
            dashButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        dashButtonLayout.setVerticalGroup(
            dashButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                .addContainerGap())
        );

        apntButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                apntButtonMouseClicked(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Helvetica Neue", 0, 26)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar.png"))); // NOI18N
        jLabel4.setText("APPPOINMENTS");
        jLabel4.setFocusable(false);

        javax.swing.GroupLayout apntButtonLayout = new javax.swing.GroupLayout(apntButton);
        apntButton.setLayout(apntButtonLayout);
        apntButtonLayout.setHorizontalGroup(
            apntButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(apntButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );
        apntButtonLayout.setVerticalGroup(
            apntButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(apntButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                .addContainerGap())
        );

        ptntButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ptntButtonMouseClicked(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Helvetica Neue", 0, 26)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/patients.png"))); // NOI18N
        jLabel5.setText("PATIENTS");
        jLabel5.setFocusable(false);

        javax.swing.GroupLayout ptntButtonLayout = new javax.swing.GroupLayout(ptntButton);
        ptntButton.setLayout(ptntButtonLayout);
        ptntButtonLayout.setHorizontalGroup(
            ptntButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ptntButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        ptntButtonLayout.setVerticalGroup(
            ptntButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ptntButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                .addContainerGap())
        );

        pymtButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pymtButtonMouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Helvetica Neue", 0, 26)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bill.png"))); // NOI18N
        jLabel6.setText("PAYMENTS");
        jLabel6.setFocusable(false);

        javax.swing.GroupLayout pymtButtonLayout = new javax.swing.GroupLayout(pymtButton);
        pymtButton.setLayout(pymtButtonLayout);
        pymtButtonLayout.setHorizontalGroup(
            pymtButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pymtButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pymtButtonLayout.setVerticalGroup(
            pymtButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pymtButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                .addContainerGap())
        );

        infoPanel.setBackground(new java.awt.Color(34, 40, 49));

        jLabel7.setFont(new java.awt.Font("Helvetica Neue", 0, 22)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Dentist Name");
        jLabel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jLabel7.setFocusable(false);

        jLabel8.setForeground(new java.awt.Color(20, 66, 114));
        jLabel8.setText("Doctor of Medicine in Dentistry");
        jLabel8.setFocusable(false);

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/dentist.png"))); // NOI18N
        jLabel2.setFocusable(false);

        javax.swing.GroupLayout infoPanelLayout = new javax.swing.GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(infoPanelLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(0, 79, Short.MAX_VALUE))
                    .addGroup(infoPanelLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        infoPanelLayout.setVerticalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, infoPanelLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addGap(6, 6, 6))
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        activity.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        activity.setForeground(new java.awt.Color(255, 255, 255));
        activity.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        activity.setText("PLACEHOLDER");

        jLabel10.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("CURRENT ACTIVITY:");

        job_done.setBackground(new java.awt.Color(0, 0, 0));
        job_done.setFont(new java.awt.Font(".AppleSystemUIFont", 1, 22)); // NOI18N
        job_done.setForeground(new java.awt.Color(0, 173, 181));
        job_done.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/check.png"))); // NOI18N
        job_done.setText("JOB DONE");
        job_done.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        job_done.setBorderPainted(false);
        job_done.setFocusPainted(false);
        job_done.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                job_doneActionPerformed(evt);
            }
        });

        service_date.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        service_date.setForeground(new java.awt.Color(255, 255, 255));
        service_date.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy - hh:mm a");
        String formattedDateTime = now.format(formatter);
        service_date.setText(formattedDateTime);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator2))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(activity, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                            .addComponent(job_done, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(30, 30, 30))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ptntButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(pymtButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(apntButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(dashButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(service_date, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(infoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)))
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(displayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(dashButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addComponent(apntButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addComponent(ptntButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addComponent(pymtButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 207, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(activity)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(job_done)
                        .addGap(49, 49, 49)
                        .addComponent(infoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(displayPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(service_date)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        dashboardPanel dashPanel = new dashboardPanel(dent_id);
        dashButton.setBackground(Color.decode("#00ADB5"));
        apntButton.setBackground(Color.decode("#222831"));
        ptntButton.setBackground(Color.decode("#222831"));
        pymtButton.setBackground(Color.decode("#222831"));
        displayPanel.removeAll();
        displayPanel.add(dashPanel).setVisible(true);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void dashButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashButtonMouseClicked
        dashboardPanel dashPanel = new dashboardPanel(dent_id);
        dashButton.setBackground(Color.decode("#00ADB5"));
        apntButton.setBackground(Color.decode("#222831"));
        ptntButton.setBackground(Color.decode("#222831"));
        pymtButton.setBackground(Color.decode("#222831"));
        displayPanel.removeAll();
        displayPanel.add(dashPanel).setVisible(true);
    }//GEN-LAST:event_dashButtonMouseClicked

    private void apntButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_apntButtonMouseClicked
        appointmentsPanel apntPanel = new appointmentsPanel(dent_id);
        dashButton.setBackground(Color.decode("#222831"));
        apntButton.setBackground(Color.decode("#00ADB5"));
        ptntButton.setBackground(Color.decode("#222831"));
        pymtButton.setBackground(Color.decode("#222831"));
        displayPanel.removeAll();
        displayPanel.add(apntPanel).setVisible(true);
    }//GEN-LAST:event_apntButtonMouseClicked

    private void ptntButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ptntButtonMouseClicked
        patientsPanel ptntPanel = new patientsPanel(dent_id);
        dashButton.setBackground(Color.decode("#222831"));
        apntButton.setBackground(Color.decode("#222831"));
        ptntButton.setBackground(Color.decode("#00ADB5"));
        pymtButton.setBackground(Color.decode("#222831"));
        displayPanel.removeAll();
        displayPanel.add(ptntPanel).setVisible(true);
    }//GEN-LAST:event_ptntButtonMouseClicked

    private void pymtButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pymtButtonMouseClicked
        paymentsPanel pymtPanel = new paymentsPanel(dent_id);
        dashButton.setBackground(Color.decode("#222831"));
        apntButton.setBackground(Color.decode("#222831"));
        ptntButton.setBackground(Color.decode("#222831"));
        pymtButton.setBackground(Color.decode("#00ADB5"));
        displayPanel.removeAll();
        displayPanel.add(pymtPanel).setVisible(true);
    }//GEN-LAST:event_pymtButtonMouseClicked

    private void job_doneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_job_doneActionPerformed
        try (Connection conn = Database.getConnection();
             CallableStatement call = conn.prepareCall("CALL jobComplete(?)")) {

            call.setInt(1, apptmt_id);
            ResultSet rs = call.executeQuery();
            JOptionPane.showMessageDialog(rootPane, "Job Complete for Appointment No. " + apptmt_id, "Job Done", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            Logger.getLogger(dentistUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        setStatus();
    }//GEN-LAST:event_job_doneActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new dentistUI(1).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel activity;
    private javax.swing.JPanel apntButton;
    private javax.swing.JPanel dashButton;
    private javax.swing.JPanel displayPanel;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JButton job_done;
    private javax.swing.JPanel ptntButton;
    private javax.swing.JPanel pymtButton;
    private javax.swing.JLabel service_date;
    // End of variables declaration//GEN-END:variables
}
