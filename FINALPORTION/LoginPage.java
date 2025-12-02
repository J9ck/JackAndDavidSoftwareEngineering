package licensemanagergui;

import javax.swing.JOptionPane;

public class LoginPage extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(LoginPage.class.getName());

    private final DatabaseHelper db;

    public LoginPage() {
        db = new DatabaseHelper();
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        bannerLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        loginButton = new javax.swing.JButton();
        addUserButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("License Manager Login");
        setResizable(false);

        try {
            bannerLabel.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/licensemanagergui/login_banner.png")));
        } catch (Exception e) {
            bannerLabel.setText("Software License Manager");
            bannerLabel.setFont(new java.awt.Font("Arial", 1, 24));
        }
        bannerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel1.setFont(new java.awt.Font("Apple Braille", 1, 30));
        jLabel1.setForeground(new java.awt.Color(51, 153, 255));
        jLabel1.setText("Software License Management System");

        jLabel2.setFont(new java.awt.Font("Apple Braille", 1, 22));
        jLabel2.setText("Login Page");

        jLabel4.setText("Username:");
        jLabel5.setText("Password:");

        loginButton.setText("Login");
        loginButton.addActionListener(evt -> loginAction());

        addUserButton.setText("Add User");
        addUserButton.addActionListener(evt -> addUserAction());

        jLabel6.setText("Forgot Password?");
        jLabel6.setForeground(new java.awt.Color(51, 153, 255));
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                forgotAction(evt);
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
                            .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 200,
                                    javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 200,
                                    javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(loginButton)
                        .addComponent(addUserButton)
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
                    .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18)
                .addComponent(loginButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addUserButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addContainerGap(30, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }

    private void loginAction() {
        String usn = username.getText().trim();
        String pass = new String(password.getPassword()).trim();

        if (usn.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "Enter username and password.",
                "Missing Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DatabaseHelper.User user = db.validateLogin(usn, pass);

        if (user != null) {
            try {
                javax.swing.SwingUtilities.invokeLater(() ->
                        new LicenseManagerGUI(db).setVisible(true));
                dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    this, "Error launching GUI: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(
                this, "Invalid username or password.",
                "Login Failed", JOptionPane.ERROR_MESSAGE);
            username.setText("");
            password.setText("");
        }
    }

    private void addUserAction() {
        String newUser = JOptionPane.showInputDialog(this, "New username:");
        if (newUser == null || newUser.trim().isEmpty()) return;

        String newPass = JOptionPane.showInputDialog(this, "New password:");
        if (newPass == null || newPass.trim().isEmpty()) return;

        String[] roles = {"admin", "user"};
        String role = (String) JOptionPane.showInputDialog(
                this, "Select role:", "Role",
                JOptionPane.QUESTION_MESSAGE, null, roles, roles[1]
        );
        if (role == null) return;

        boolean ok = db.addUser(newUser, newPass, role);
        if (ok) {
            JOptionPane.showMessageDialog(this, "User created.");
        } else {
            JOptionPane.showMessageDialog(this, "Failed. Username may already exist.");
        }
    }

    private void forgotAction(java.awt.event.MouseEvent evt) {
        String usn = JOptionPane.showInputDialog(this, "Enter your username:");
        if (usn != null && !usn.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Password reset request submitted for: " + usn +
                "\n\nDefault admin credentials:\nUsername: admin\nPassword: ric401",
                "Password Reset",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info :
                    javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() ->
                new LoginPage().setVisible(true));
    }

    private javax.swing.JLabel bannerLabel;
    private javax.swing.JButton loginButton;
    private javax.swing.JButton addUserButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPasswordField password;
    private javax.swing.JTextField username;
}