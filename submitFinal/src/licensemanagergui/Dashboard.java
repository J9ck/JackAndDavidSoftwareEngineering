/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author josedavidtovar
 */

package licensemanagergui;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    private DatabaseHelper db;
    private AddNewLicensePage addNewLicensePage;


    // UI panels
    private JPanel contentPanel;
    private JLabel lblSectionTitle;

    public Dashboard(DatabaseHelper dbRef) {
        this.db = dbRef;
        addNewLicensePage = new AddNewLicensePage();


        // Nimbus for modern look
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        initUI();
    }

    private void initUI() {
        setTitle("J&J Software License Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 70, 140)); // dark enterprise blue
        headerPanel.setPreferredSize(new Dimension(0, 60));

        JLabel lblAppName = new JLabel("J&J Software License Portal");
        lblAppName.setForeground(Color.WHITE);
        lblAppName.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblAppName.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        headerRight.setOpaque(false);
        JLabel lblUser = new JLabel("Admin User");
        lblUser.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> logout());

        headerRight.add(lblUser);
        headerRight.add(btnLogout);

        headerPanel.add(lblAppName, BorderLayout.WEST);
        headerPanel.add(headerRight, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        btnLogout.addActionListener(e -> {
            //if (refreshTimer != null) refreshTimer.stop();
            dispose();
            new LoginPage().setVisible(true);
        });

        // Left navigations 
        JPanel navPanel = new JPanel();
        navPanel.setBackground(new Color(245, 245, 245)); 
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setPreferredSize(new Dimension(230, 0));
        navPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

        JLabel lblNavTitle = new JLabel(" Navigation");
        lblNavTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNavTitle.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 0));

        navPanel.add(lblNavTitle);

        navPanel.add(createNavButton("Dashboard Home", () -> showHome()));
        navPanel.add(createNavButton("License Manager", () -> openLicenseManager()));
        navPanel.add(createNavButton("Add New License", () -> openAddNewLicense()));
        navPanel.add(createNavButton("Reports & Analytics", () -> openReports()));
        navPanel.add(Box.createVerticalStrut(20));
        navPanel.add(createNavButton("Settings", () -> openSettings()));
        navPanel.add(createNavButton("About", () -> showAboutDialog()));
            
        // ABOUT BUTTON 
        JButton btnAbout = new JButton("About");
        btnAbout.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnAbout.setBackground(new Color(240, 240, 240));
        btnAbout.setFocusPainted(false);
        btnAbout.setPreferredSize(new Dimension(140, 40));

        btnAbout.addActionListener(e -> showAboutDialog());

        navPanel.add(Box.createVerticalGlue()); 

        add(navPanel, BorderLayout.WEST);

        // Main page
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Section title bar
        JPanel sectionHeader = new JPanel(new BorderLayout());
        sectionHeader.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        sectionHeader.setBackground(Color.WHITE);
        lblSectionTitle = new JLabel("Dashboard");
        lblSectionTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        sectionHeader.add(lblSectionTitle, BorderLayout.WEST);

        mainPanel.add(sectionHeader, BorderLayout.NORTH);

        // Content panel 
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBackground(new Color(250, 250, 250));

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Show initial home tiles
        showHome();
    }

    /**
     * Creates a flat-style navigation button for the sidebar.
     */
    private JButton createNavButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));
        btn.setBackground(new Color(245, 245, 245));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // hover effect
        btn.addChangeListener(e -> {
            ButtonModel model = btn.getModel();
            if (model.isRollover()) {
                btn.setBackground(new Color(230, 230, 230));
            } else {
                btn.setBackground(new Color(245, 245, 245));
            }
        });

        btn.addActionListener(e -> action.run());
        return btn;
    }

    //  dashboard home 
    private void showHome() {
        lblSectionTitle.setText("Dashboard");
        contentPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;

        // Tile 1 – License manager
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(createTilePanel(
                "Licenses Manager",
                "View and manage all user software licenses.",
                () -> openLicenseManager()
        ), gbc);

        //Tile 2 – Add New License
        gbc.gridx = 1;
        gbc.gridy = 0;
        contentPanel.add(createTilePanel(
                "Add New License",
                "Register and edit license into the system.",
                () -> openAddNewLicense()
        ), gbc);

        // Tile 3 – reports
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(createTilePanel(
                "Reports & Analytics",
                "View expiration and Cost.",
                () -> openReports()
        ), gbc);

        // Tile 4 – Settings / Admin
        gbc.gridx = 1;
        gbc.gridy = 1;
        contentPanel.add(createTilePanel(
                "Admin Settings",
                "Configure system preferences and users.",
                () -> openSettings()
        ), gbc);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Creates a "card/tile" panel for modern enterprise dashboards.
     */
    private JPanel createTilePanel(String title, String description, Runnable onClick) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(260, 160));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        JTextArea txtDesc = new JTextArea(description);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setLineWrap(true);
        txtDesc.setEditable(false);
        txtDesc.setOpaque(false);
        txtDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtDesc.setBorder(null);

        JButton btnOpen = new JButton("Open");
        btnOpen.setFocusPainted(false);
        btnOpen.addActionListener(e -> onClick.run());

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(txtDesc, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        footer.add(btnOpen);
        panel.add(footer, BorderLayout.SOUTH);

        // click anywhere on the panel to trigger
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                onClick.run();
            }
        });

        return panel;
    }

    // opens settings page 
    private void openSettings() {
        SettingsPage settings = new SettingsPage(db); // ✔ pass DB
        settings.setVisible(true);
    }

    
    //opens about 
    private void showAboutDialog() {
        JDialog dialog = new JDialog(this, "About", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(0, 70, 140),
                    0, getHeight(), new Color(0, 100, 200)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(500, 120));
        headerPanel.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("J&J Software License Portal");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel versionLabel = new JLabel("Dashboard Version 2.0");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        versionLabel.setForeground(new Color(200, 220, 255));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(versionLabel);

        headerPanel.add(titlePanel);
        dialog.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));
        contentPanel.setBackground(Color.WHITE);

        JLabel devLabel = new JLabel("Developed by");
        devLabel.setFont(new Font("Arial", Font.BOLD, 13));
        devLabel.setForeground(new Color(80, 80, 80));
        devLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel devNames = new JLabel("Jose Tovar Assisted by Jack Doyle ");
        devNames.setFont(new Font("Arial", Font.PLAIN, 15));
        devNames.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(devLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(devNames);
        contentPanel.add(Box.createVerticalStrut(20));

        

        
        contentPanel.add(Box.createVerticalStrut(5));
        
        contentPanel.add(Box.createVerticalStrut(25));

        JLabel copyright = new JLabel("© 2025 All Rights Reserved");
        copyright.setFont(new Font("Arial", Font.PLAIN, 11));
        copyright.setForeground(new Color(120, 120, 120));
        copyright.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(copyright);

        dialog.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        JButton closeBtn = new JButton("Close");
        closeBtn.setPreferredSize(new Dimension(100, 35));
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setResizable(false);
        dialog.setVisible(true);
    }



        // navigation methods
        //opens jacks license manager
        private void openLicenseManager() {
            lblSectionTitle.setText("License Manager");
            // Open as separate window
            SwingUtilities.invokeLater(() -> {
                LicenseManagerGUI licGui = new LicenseManagerGUI(db);
                licGui.setVisible(true);
            });
        }
        //connects to AddNewLicensePage.java and opens
        private void openAddNewLicense() {
            AddNewLicensePage addPage = new AddNewLicensePage();
            addPage.setVisible(true);
    }


//    private void openReports() {
//    lblSectionTitle.setText("Reports & Analytics");
//
//    // Clear old content
//    contentPanel.removeAll();
//    contentPanel.setLayout(new BorderLayout());
//
//    // Path to same Excel file AddNewLicensePage uses
//    String excelPath = "/Users/josedavidtovar/Documents/licenses.xlsx";
//    
//
//    ReportsAnalyticsPage reports = new ReportsAnalyticsPage(null, excelPath);
//
//    contentPanel.add(reports, BorderLayout.CENTER);
//
//    contentPanel.revalidate();
//    contentPanel.repaint();
//}
    private void openReports() {
        String excelPath = "/Users/josedavidtovar/Documents/licenses.xlsx";
        ReportsAnalyticsPage reports = new ReportsAnalyticsPage(null, excelPath);
        reports.setVisible(true);  // OPEN AS WINDOW
}



    private void logout() {
        JOptionPane.showMessageDialog(this, "Logging out...");
        // dispose();
        // new LoginFrame().setVisible(true);
    }

    // MAIN ENTRY 
    public static void main(String[] args) {
        DatabaseHelper db = new DatabaseHelper(); // adjust if needed
        SwingUtilities.invokeLater(() -> {
            Dashboard dash = new Dashboard(db);
            dash.setVisible(true);
        });
    }

    
}



