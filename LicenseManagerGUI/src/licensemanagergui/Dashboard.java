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

    // UI panels so you can change center content later
    private JPanel contentPanel;
    private JLabel lblSectionTitle;

    public Dashboard(DatabaseHelper dbRef) {
        this.db = dbRef;

        // Optional: Try Nimbus for a more modern look
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

        // TOP HEADER
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

        // LEFT NAVIGATION 
        JPanel navPanel = new JPanel();
        navPanel.setBackground(new Color(245, 245, 245)); // light gray
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
        //navPanel.add(createNavButton("Reports & Analytics", () -> openReports()));
        navPanel.add(Box.createVerticalStrut(20));
        navPanel.add(createNavButton("Settings", () -> showSettings()));

        navPanel.add(Box.createVerticalGlue()); // push bottom items down if needed

        add(navPanel, BorderLayout.WEST);

        // MAIN CONTENT AREA 
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

        // Content panel (where we place tiles, tables, etc.)
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

        // Hover effect
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

    // ====== CONTENT: DASHBOARD HOME ======
    private void showHome() {
        lblSectionTitle.setText("Dashboard");
        contentPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;

        // Tile 1 – Total Licenses
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(createTilePanel(
                "Total Licenses",
                "View and manage all software licenses.",
                () -> openLicenseManager()
        ), gbc);

        //Tile 2 – Add New License
        gbc.gridx = 1;
        gbc.gridy = 0;
        contentPanel.add(createTilePanel(
                "Add New License",
                "Register a new license into the system.",
                () -> openAddNewLicense()
        ), gbc);

//        // Tile 3 – Reports
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        contentPanel.add(createTilePanel(
//                "Reports & Analytics",
//                "Generate usage and compliance reports.",
//                () -> openReports()
//        ), gbc);

        // Tile 4 – Settings / Admin
        gbc.gridx = 1;
        gbc.gridy = 1;
        contentPanel.add(createTilePanel(
                "Admin Settings",
                "Configure system preferences and users.",
                () -> showSettings()
        ), gbc);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Creates a "card/tile" panel like modern enterprise dashboards.
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

        // Optional: click anywhere on the panel to trigger
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                onClick.run();
            }
        });

        return panel;
    }

    // OTHER SECTIONS (placeholders) 
    private void showSettings() {
        lblSectionTitle.setText("Settings");
        contentPanel.removeAll();

        JLabel lbl = new JLabel("Settings page coming soon...", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(lbl, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // NAVIGATION METHODS
    private void openLicenseManager() {
        lblSectionTitle.setText("License Manager");
        // Open as separate window (Oracle-style often uses new pages)
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
//        lblSectionTitle.setText("Reports & Analytics");
//        SwingUtilities.invokeLater(() -> {
//            ReportsWindow reports = new ReportsWindow(db);
//            reports.setVisible(true);
//        });
//    }

    private void logout() {
        JOptionPane.showMessageDialog(this, "Logging out...");
        // dispose();
        // new LoginFrame().setVisible(true);
    }

    // ====== MAIN ENTRY ======
    public static void main(String[] args) {
        DatabaseHelper db = new DatabaseHelper(); // adjust if needed
        SwingUtilities.invokeLater(() -> {
            Dashboard dash = new Dashboard(db);
            dash.setVisible(true);
        });
    }
}


