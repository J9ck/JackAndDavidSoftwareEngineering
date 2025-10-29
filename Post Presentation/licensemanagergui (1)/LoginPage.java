package licensemanagergui;

import javax.swing.JOptionPane;

/**
 * Login screen for the License Manager GUI.
 * Opens LicenseManagerGUI.java after successful login.
 */
public class LoginPage extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoginPage.class.getName());

    public LoginPage() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bannerLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        jButton1 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("License Manager Login");
        setResizable(false);

        // Banner Image
        bannerLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/licensemanagergui/login_banner.png")));
        bannerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel1.setFont(new java.awt.Font("Apple Braille", 1, 30)); 
        jLabel1.setForeground(new java.awt.Color(51, 153, 255));
        jLabel1.setText("Software License Management System");

        jLabel2.setFont(new java.awt.Font("Apple Braille", 1, 22)); 
        jLabel2.setText("Login Page");

        jLabel4.setText("Username:");

        jLabel5.setText("Password:");

        jButton1.setText("Login");
        jButton1.addActionListener(evt -> jButton1ActionPerformed(evt));

        jLabel6.setText("Forgot Password?");
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addGroup(layout.createSequentialGroup()
                .addGap(100)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(bannerLabel)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton1)
                    .addComponent(jLabel6))
                .addContainerGap(100, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(20)
                .addComponent(bannerLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addContainerGap(30, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
    String usn = username.getText().trim();
    String pass = new String(password.getPassword()).trim();

    if (usn.equals("admin") && pass.equals("ric401")) {
        try {
            LicenseManagerGUI.main(null); // launches GUI
            dispose(); // closes login window
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error launching GUI: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    } else {
        JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        username.setText("");
        password.setText("");
    }
}


    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {
        JOptionPane.showMessageDialog(this, "Username: admin\nPassword: ric401", "Forgot Password", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new LoginPage().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bannerLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPasswordField password;
    private javax.swing.JTextField username;
    // End of variables declaration//GEN-END:variables
}
