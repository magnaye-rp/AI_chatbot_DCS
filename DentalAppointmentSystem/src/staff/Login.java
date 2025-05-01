package staff;

import java.awt.Color;
import javax.swing.JOptionPane;
import management.management;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Login extends javax.swing.JFrame {

    public Login() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        loginIdField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(34, 40, 49));
        jPanel1.setLayout(null);

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Contact Number");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(31, 70, 100, 17);

        loginIdField.setForeground(new java.awt.Color(204, 204, 204));
        loginIdField.setText("Contact Number...");
        loginIdField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                loginIdFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                loginIdFieldFocusLost(evt);
            }
        });
        jPanel1.add(loginIdField);
        loginIdField.setBounds(31, 90, 370, 40);

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Password");
        jPanel1.add(jLabel6);
        jLabel6.setBounds(31, 160, 100, 17);
        jPanel1.add(passwordField);
        passwordField.setBounds(31, 183, 370, 41);

        jButton1.setBackground(new java.awt.Color(0, 173, 181));
        jButton1.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("LOGIN");
        jButton1.setBorderPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);
        jButton1.setBounds(142, 255, 123, 37);

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("DENTIST LOGIN");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(146, 12, 139, 46);

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 1, 140)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 41));
        jLabel2.setText("DENTIST");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(-50, 190, 630, 160);

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 431, 328));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loginIdFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_loginIdFieldFocusGained
        if((loginIdField.getText()).equals("Enter Contact Number...")){
            loginIdField.setText("");
            loginIdField.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_loginIdFieldFocusGained

    private void loginIdFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_loginIdFieldFocusLost
        if((loginIdField.getText()).equals("")){
            loginIdField.setText("Enter Contact Number...");
            loginIdField.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_loginIdFieldFocusLost

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String enteredId = loginIdField.getText();
        String enteredPassword = new String(passwordField.getPassword());

        // Check if the fields are empty
        if ("".equals(enteredId) || "".equals(enteredPassword)) {
            JOptionPane.showMessageDialog(rootPane, "Please enter both Login ID and Password.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            // Proceed with the database query
            try (Connection conn = Database.getConnection();
                 CallableStatement cs = conn.prepareCall("CALL verifyDentist(?, ?)")) {

                // Set the input parameters
                cs.setString(1, enteredId);
                cs.setString(2, enteredPassword);

                // Execute the query and get the result
                ResultSet rs = cs.executeQuery();

                if (rs.next()) {
                    // Successful login, open the dentist UI
                    new dentistUI(rs.getInt("dentist_id")).setVisible(true);
                } else {
                    // Invalid credentials, reset fields and show error message
                    loginIdField.setText("");
                    passwordField.setText("");
                    JOptionPane.showMessageDialog(rootPane, "Incorrect Login ID or Password.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                // Handle any SQL exception that occurs
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(rootPane, "Database connection error. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField loginIdField;
    private javax.swing.JPasswordField passwordField;
    // End of variables declaration//GEN-END:variables
}
