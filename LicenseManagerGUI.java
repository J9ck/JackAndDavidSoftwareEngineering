// LicenseManagerGUI.java
// Created by Jack Doyle
// October 16, 2025
package licensemanagergui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileWriter;
import java.io.PrintWriter;

public class LicenseManagerGUI extends JFrame {
    private static DatabaseHelper db;
    private Timer refreshTimer;
    
    // software pricing catalog
    private static final Map<String, Double> SOFTWARE_PRICES = new HashMap<>();
    static {
        SOFTWARE_PRICES.put("Zoom", 149.90);
        SOFTWARE_PRICES.put("Slack", 87.60);
        SOFTWARE_PRICES.put("Office", 699.99);
        SOFTWARE_PRICES.put("Adobe", 599.88);
        SOFTWARE_PRICES.put("Photoshop", 239.88);
        SOFTWARE_PRICES.put("AutoCAD", 1865.00);
        SOFTWARE_PRICES.put("WinRAR", 29.00);
        SOFTWARE_PRICES.put("Chrome", 0.00);
        SOFTWARE_PRICES.put("Firefox", 0.00);
        SOFTWARE_PRICES.put("Safari", 0.00);
        SOFTWARE_PRICES.put("Windows", 139.00);
    }

    public LicenseManagerGUI(DatabaseHelper dbRef) {
        db = dbRef;

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        UIManager.put("Menu.foreground", Color.BLACK);
        UIManager.put("MenuItem.foreground", Color.BLACK);
        UIManager.put("Menu.selectionBackground", new Color(230, 230, 230));
        UIManager.put("Menu.selectionForeground", Color.BLACK);
        UIManager.put("MenuItem.selectionBackground", new Color(230, 230, 230));
        UIManager.put("MenuItem.selectionForeground", Color.BLACK);
        UIManager.put("MenuBar.background", new Color(245, 245, 245));
        UIManager.put("Menu.background", new Color(245, 245, 245));
        UIManager.put("MenuItem.background", new Color(245, 245, 245));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> db.close()));

        setTitle("J&J Software License Manager - Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // top panel with stats
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 245, 245));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        statsPanel.setBackground(new Color(245, 245, 245));
        
        JLabel totalCostLabel = new JLabel("Total Cost: $0.00");
        totalCostLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel activeLicensesLabel = new JLabel("Active: 0");
        activeLicensesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        activeLicensesLabel.setForeground(new Color(0, 128, 0));
        
        JLabel unusedLabel = new JLabel("Unused: 0");
        unusedLabel.setFont(new Font("Arial", Font.BOLD, 14));
        unusedLabel.setForeground(new Color(255, 140, 0));
        
        JLabel totalUsageLabel = new JLabel("Usage: 0");
        totalUsageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        statsPanel.add(totalCostLabel);
        statsPanel.add(new JSeparator(JSeparator.VERTICAL));
        statsPanel.add(activeLicensesLabel);
        statsPanel.add(new JSeparator(JSeparator.VERTICAL));
        statsPanel.add(unusedLabel);
        statsPanel.add(new JSeparator(JSeparator.VERTICAL));
        statsPanel.add(totalUsageLabel);
        
        JButton refreshBtn = new JButton("🔄 Refresh");
        JButton logoutBtn = new JButton("Logout");
        
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtons.setBackground(new Color(245, 245, 245));
        rightButtons.add(refreshBtn);
        rightButtons.add(logoutBtn);
        
        topPanel.add(statsPanel, BorderLayout.CENTER);
        topPanel.add(rightButtons, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        logoutBtn.addActionListener(e -> {
            if (refreshTimer != null) refreshTimer.stop();
            dispose();
            new LoginPage().setVisible(true);
        });

        // tree panel
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Database Storage");
        DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("Local Licenses");
        root.add(node1);
        JTree tree = new JTree(root);
        JScrollPane treeScroll = new JScrollPane(tree);

        // right panel
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);

        // enhanced table
        String[] columns = {
            "Software", "User", "License Key", "Code", 
            "Expiry", "Days Left", "Status", "Price", 
            "Usage", "Cost/Use", "ROI"
        };
        
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 5) return Integer.class;
                if (col == 7 || col == 9) return Double.class;
                if (col == 8) return Integer.class;
                return String.class;
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // color-code rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String status = (String) table.getValueAt(row, 6);
                    Object daysLeftObj = table.getValueAt(row, 5);
                    int daysLeft = (daysLeftObj instanceof Integer) ? (Integer) daysLeftObj : 999;
                    
                    if ("expired".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(255, 200, 200));
                    } else if (daysLeft < 30) {
                        c.setBackground(new Color(255, 240, 200));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane tableScroll = new JScrollPane(table);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        // status panel
        JLabel statusLabel = new JLabel("License count: 0");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.EAST);
        add(statusPanel, BorderLayout.SOUTH);

        // split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, rightPanel);
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);

        // menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu productMenu = new JMenu("Product");
        JMenu licenseMenu = new JMenu("License");
        JMenu toolsMenu = new JMenu("Tools");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem addProduct = new JMenuItem("Add Product");
        JMenuItem removeProduct = new JMenuItem("Remove Product");
        JMenuItem newLicense = new JMenuItem("New License");
        JMenuItem editLicense = new JMenuItem("Edit License");
        JMenuItem deleteLicense = new JMenuItem("Delete License");
        JMenuItem exportItem = new JMenuItem("Export to CSV");
        JMenuItem costAnalysis = new JMenuItem("Cost Analysis Report");
        JMenuItem complianceReport = new JMenuItem("Compliance Report");
        JMenuItem aboutItem = new JMenuItem("About");

        productMenu.add(addProduct);
        productMenu.add(removeProduct);
        licenseMenu.add(newLicense);
        licenseMenu.add(editLicense);
        licenseMenu.add(deleteLicense);
        toolsMenu.add(exportItem);
        toolsMenu.add(costAnalysis);
        toolsMenu.add(complianceReport);
        helpMenu.add(aboutItem);
        
        menuBar.add(productMenu);
        menuBar.add(licenseMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // load data
        final Runnable loadFromDB = () -> {
            tableModel.setRowCount(0);
            List<DatabaseHelper.License> licenses = db.getAllLicenses();
            
            double totalCost = 0;
            int activeCount = 0;
            int unusedCount = 0;
            int totalUsage = 0;
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate today = LocalDate.now();
            
            for (DatabaseHelper.License lic : licenses) {
                try {
                    LocalDate expiryDate = LocalDate.parse(lic.expiry, formatter);
                    long daysLeft = ChronoUnit.DAYS.between(today, expiryDate);
                    
                    String status = lic.status;
                    if (daysLeft < 0) status = "expired";
                    
                    if ("active".equalsIgnoreCase(status) && daysLeft >= 0) activeCount++;
                    
                    totalCost += lic.price;
                    totalUsage += lic.usage;
                    
                    if (lic.usage == 0 && lic.price > 0) unusedCount++;
                    
                    String roiStatus = calculateROI(lic);
                    
                    tableModel.addRow(new Object[]{
                        lic.software, lic.userName, lic.licenseKey, lic.licenseCode,
                        lic.expiry, (int) daysLeft, status, lic.price,
                        lic.usage, lic.costPerUse, roiStatus
                    });
                } catch (Exception ex) {
                    tableModel.addRow(new Object[]{
                        lic.software, lic.userName, lic.licenseKey, lic.licenseCode,
                        lic.expiry, "N/A", lic.status, lic.price,
                        lic.usage, lic.costPerUse, "Unknown"
                    });
                }
            }
            
            totalCostLabel.setText(String.format("Total Cost: $%.2f", totalCost));
            activeLicensesLabel.setText("Active: " + activeCount);
            unusedLabel.setText("Unused: " + unusedCount);
            totalUsageLabel.setText("Usage: " + totalUsage);
            statusLabel.setText("License count: " + tableModel.getRowCount());
        };
        
        loadFromDB.run();

        // auto-refresh every 3 seconds
        refreshTimer = new Timer(3000, e -> {
            loadFromDB.run();
            table.repaint();
        });
        refreshTimer.start();
        
        refreshBtn.addActionListener(e -> loadFromDB.run());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (refreshTimer != null) refreshTimer.stop();
            }
        });

        // menu actions
        addProduct.addActionListener(e -> {
            String newProd = JOptionPane.showInputDialog(this, "Enter new product:");
            if (newProd != null && !newProd.isBlank()) {
                newProd = autocorrectSoftware(newProd);
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newProd);
                ((DefaultMutableTreeNode) tree.getModel().getRoot()).add(newNode);
                ((DefaultTreeModel) tree.getModel()).reload();
                db.addProduct(newProd);
                loadFromDB.run();
                JOptionPane.showMessageDialog(this, "Added: " + newProd);
            }
        });
        
        removeProduct.addActionListener(e -> {
            var path = tree.getSelectionPath();
            if (path == null || path.getLastPathComponent() == tree.getModel().getRoot()) {
                JOptionPane.showMessageDialog(this, "Select a product to remove");
            } else {
                DefaultMutableTreeNode selected = (DefaultMutableTreeNode) path.getLastPathComponent();
                String selectedName = selected.toString();
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete product and all licenses for: " + selectedName + "?",
                    "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    db.deleteBySoftware(selectedName);
                    db.deleteProduct(selectedName);
                    selected.removeFromParent();
                    ((DefaultTreeModel) tree.getModel()).reload();
                    loadFromDB.run();
                    JOptionPane.showMessageDialog(this, "Removed: " + selectedName);
                }
            }
        });

        newLicense.addActionListener(e -> handleLicenseDialog(null, tableModel, loadFromDB));
        editLicense.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) handleLicenseDialog(table, tableModel, loadFromDB);
            else JOptionPane.showMessageDialog(this, "Select a license to edit");
        });
        deleteLicense.addActionListener(e -> handleDeleteLicense(table, tableModel, loadFromDB));
        exportItem.addActionListener(e -> handleExportCSV());
        costAnalysis.addActionListener(e -> showCostAnalysis());
        complianceReport.addActionListener(e -> showComplianceReport());
        aboutItem.addActionListener(e -> showAboutDialog());
    }
    
    private String calculateROI(DatabaseHelper.License lic) {
        if (lic.price == 0) return "Free ✓";
        if (lic.usage == 0) return "Unused ⚠";
        if (lic.costPerUse < 1.0) return "Excellent ★★★";
        if (lic.costPerUse < 5.0) return "Good ★★";
        if (lic.costPerUse < 20.0) return "Fair ★";
        return "Poor ☹";
    }
    
    private void showCostAnalysis() {
        List<DatabaseHelper.License> licenses = db.getAllLicenses();
        
        double totalSpent = 0;
        int totalUsage = 0;
        Map<String, Double> costBySoftware = new HashMap<>();
        Map<String, Integer> usageBySoftware = new HashMap<>();
        
        for (DatabaseHelper.License lic : licenses) {
            totalSpent += lic.price;
            totalUsage += lic.usage;
            costBySoftware.merge(lic.software, lic.price, Double::sum);
            usageBySoftware.merge(lic.software, lic.usage, Integer::sum);
        }
        
        // create dialog with charts
        JDialog dialog = new JDialog(this, "Cost Analysis Report", true);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        // summary panel at top
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        summaryPanel.setBackground(Color.WHITE);
        
        summaryPanel.add(createSummaryCard("Total Licenses", String.valueOf(licenses.size()), new Color(52, 152, 219)));
        summaryPanel.add(createSummaryCard("Total Spent", String.format("$%.2f", totalSpent), new Color(231, 76, 60)));
        summaryPanel.add(createSummaryCard("Total Usage", String.valueOf(totalUsage), new Color(46, 204, 113)));
        
        dialog.add(summaryPanel, BorderLayout.NORTH);
        
        // chart panel
        JPanel chartPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        chartPanel.setBackground(new Color(245, 245, 245));
        
        chartPanel.add(createBarChart("Cost by Software", costBySoftware, new Color(231, 76, 60)));
        chartPanel.add(createBarChart("Usage by Software", convertIntMapToDouble(usageBySoftware), new Color(46, 204, 113)));
        
        dialog.add(chartPanel, BorderLayout.CENTER);
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private Map<String, Double> convertIntMapToDouble(Map<String, Integer> intMap) {
        Map<String, Double> doubleMap = new HashMap<>();
        intMap.forEach((k, v) -> doubleMap.put(k, v.doubleValue()));
        return doubleMap;
    }
    
    private JPanel createBarChart(String title, Map<String, Double> data, Color barColor) {
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int padding = 60;
                int chartWidth = width - 2 * padding;
                int chartHeight = height - 2 * padding - 40;
                
                // background
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, width, height);
                
                // title
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                FontMetrics fm = g2d.getFontMetrics();
                int titleWidth = fm.stringWidth(title);
                g2d.drawString(title, (width - titleWidth) / 2, 25);
                
                if (data.isEmpty()) {
                    g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                    String msg = "No data available";
                    int msgWidth = g2d.getFontMetrics().stringWidth(msg);
                    g2d.drawString(msg, (width - msgWidth) / 2, height / 2);
                    return;
                }
                
                // find max value
                double maxValue = data.values().stream().max(Double::compare).orElse(1.0);
                
                // draw bars
                List<Map.Entry<String, Double>> sortedData = new ArrayList<>(data.entrySet());
                sortedData.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
                
                int barCount = Math.min(sortedData.size(), 8); // Max 8 bars
                int barWidth = chartWidth / barCount - 10;
                
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                
                for (int i = 0; i < barCount; i++) {
                    Map.Entry<String, Double> entry = sortedData.get(i);
                    String name = entry.getKey();
                    double value = entry.getValue();
                    
                    int barHeight = (int) ((value / maxValue) * chartHeight);
                    int x = padding + i * (barWidth + 10);
                    int y = padding + 40 + (chartHeight - barHeight);
                    
                    // draw bar with gradient
                    GradientPaint gradient = new GradientPaint(
                        x, y, barColor,
                        x, y + barHeight, barColor.darker()
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(x, y, barWidth, barHeight);
                    
                    // draw border
                    g2d.setColor(barColor.darker());
                    g2d.drawRect(x, y, barWidth, barHeight);
                    
                    // draw value on top of bar
                    g2d.setColor(Color.BLACK);
                    String valueStr = String.format("%.0f", value);
                    int valueWidth = g2d.getFontMetrics().stringWidth(valueStr);
                    g2d.drawString(valueStr, x + (barWidth - valueWidth) / 2, y - 5);
                    
                    // draw label below bar
                    g2d.setColor(Color.BLACK);
                    String label = name.length() > 10 ? name.substring(0, 8) + ".." : name;
                    int labelWidth = g2d.getFontMetrics().stringWidth(label);
                    g2d.drawString(label, x + (barWidth - labelWidth) / 2, height - padding + 15);
                }
                
             
                g2d.setColor(new Color(100, 100, 100));
                g2d.setStroke(new BasicStroke(2));
               
                g2d.drawLine(padding, padding + 40, padding, padding + 40 + chartHeight);
                
                g2d.drawLine(padding, padding + 40 + chartHeight, width - padding, padding + 40 + chartHeight);
            }
        };
        
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        chartPanel.setPreferredSize(new Dimension(400, 350));
        
        return chartPanel;
    }
    
    private void showComplianceReport() {
        List<DatabaseHelper.License> licenses = db.getAllLicenses();
        
        double wastedCost = 0;
        int expiringSoon = 0;
        int excellent = 0, good = 0, fair = 0, poor = 0, unused = 0, free = 0;
        List<String> issues = new ArrayList<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate today = LocalDate.now();
        
        for (DatabaseHelper.License lic : licenses) {
       
            if (lic.price == 0) {
                free++;
            } else if (lic.usage == 0) {
                unused++;
                wastedCost += lic.price;
                issues.add(String.format("⚠️ %s for %s unused ($%.2f)",
                    lic.software, lic.userName, lic.price));
            } else if (lic.costPerUse < 1.0) {
                excellent++;
            } else if (lic.costPerUse < 5.0) {
                good++;
            } else if (lic.costPerUse < 20.0) {
                fair++;
            } else {
                poor++;
            }
            
            try {
                LocalDate expiry = LocalDate.parse(lic.expiry, formatter);
                long daysLeft = ChronoUnit.DAYS.between(today, expiry);
                
                if (daysLeft < 30 && daysLeft >= 0) {
                    expiringSoon++;
                    issues.add(String.format("⏰ %s for %s expires in %d days",
                        lic.software, lic.userName, daysLeft));
                }
            } catch (Exception ignored) {}
        }
        

        JDialog dialog = new JDialog(this, "Compliance Report", true);
        dialog.setSize(900, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
       
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        summaryPanel.setBackground(Color.WHITE);
        
        summaryPanel.add(createSummaryCard("Wasted Cost", String.format("$%.2f", wastedCost), new Color(231, 76, 60)));
        summaryPanel.add(createSummaryCard("Expiring Soon", String.valueOf(expiringSoon), new Color(243, 156, 18)));
        summaryPanel.add(createSummaryCard("Unused Licenses", String.valueOf(unused), new Color(192, 57, 43)));
        
        dialog.add(summaryPanel, BorderLayout.NORTH);
        
       
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        contentPanel.setBackground(new Color(245, 245, 245));
        
    
        int[] roiData = {excellent, good, fair, poor, unused, free};
        String[] roiLabels = {"Excellent", "Good", "Fair", "Poor", "Unused", "Free"};
        Color[] roiColors = {
            new Color(46, 204, 113),
            new Color(52, 152, 219),
            new Color(241, 196, 15),
            new Color(230, 126, 34),
            new Color(231, 76, 60),
            new Color(149, 165, 166)
        };
        contentPanel.add(createPieChart("ROI Distribution", roiData, roiLabels, roiColors));
        
      
        JPanel issuesPanel = new JPanel(new BorderLayout());
        issuesPanel.setBackground(Color.WHITE);
        issuesPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel issuesTitle = new JLabel("Issues & Recommendations");
        issuesTitle.setFont(new Font("Arial", Font.BOLD, 14));
        issuesPanel.add(issuesTitle, BorderLayout.NORTH);
        
        JTextArea issuesArea = new JTextArea();
        issuesArea.setEditable(false);
        issuesArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        issuesArea.setBackground(new Color(250, 250, 250));
        
        if (!issues.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String issue : issues) {
                sb.append(issue).append("\n");
            }
            issuesArea.setText(sb.toString());
        } else {
            issuesArea.setText("\n✅ All licenses are in good standing!\n\nNo issues found.");
        }
        
        JScrollPane issuesScroll = new JScrollPane(issuesArea);
        issuesPanel.add(issuesScroll, BorderLayout.CENTER);
        
        contentPanel.add(issuesPanel);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private JPanel createPieChart(String title, int[] data, String[] labels, Color[] colors) {
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
               
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, width, height);
                
               
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                FontMetrics fm = g2d.getFontMetrics();
                int titleWidth = fm.stringWidth(title);
                g2d.drawString(title, (width - titleWidth) / 2, 25);
                
               
                int total = 0;
                for (int value : data) {
                    total += value;
                }
                
                if (total == 0) {
                    g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                    String msg = "No data available";
                    int msgWidth = g2d.getFontMetrics().stringWidth(msg);
                    g2d.drawString(msg, (width - msgWidth) / 2, height / 2);
                    return;
                }
                
          
                int diameter = Math.min(width, height - 120) - 40;
                int x = (width - diameter) / 2;
                int y = 50;
                
                int startAngle = 0;
                for (int i = 0; i < data.length; i++) {
                    if (data[i] > 0) {
                        int arcAngle = (int) Math.round((data[i] * 360.0) / total);
                        
                        g2d.setColor(colors[i]);
                        g2d.fillArc(x, y, diameter, diameter, startAngle, arcAngle);
                        
                        g2d.setColor(colors[i].darker());
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawArc(x, y, diameter, diameter, startAngle, arcAngle);
                        
                        startAngle += arcAngle;
                    }
                }
                
            
                int legendY = y + diameter + 30;
                int legendX = 20;
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                
                for (int i = 0; i < labels.length; i++) {
                    if (data[i] > 0) {
                      
                        g2d.setColor(colors[i]);
                        g2d.fillRect(legendX, legendY, 15, 15);
                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(legendX, legendY, 15, 15);
                        
                
                        int percentage = (int) Math.round((data[i] * 100.0) / total);
                        String label = String.format("%s: %d (%d%%)", labels[i], data[i], percentage);
                        g2d.drawString(label, legendX + 20, legendY + 12);
                        
                        legendY += 20;
                        if (legendY > height - 30 && i < labels.length - 1) {
                            legendY = y + diameter + 30;
                            legendX += 200;
                        }
                    }
                }
            }
        };
        
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        chartPanel.setPreferredSize(new Dimension(400, 400));
        
        return chartPanel;
    }

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
        
        JLabel titleLabel = new JLabel("J&J Software License Manager");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel versionLabel = new JLabel("Version 3.0");
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
        
        JLabel devNames = new JLabel("Jack Doyle assisted by Jose Tovar");
        devNames.setFont(new Font("Arial", Font.PLAIN, 15));
        devNames.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(devLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(devNames);
        contentPanel.add(Box.createVerticalStrut(20));
        
        JLabel websiteLabel = new JLabel("Website");
        websiteLabel.setFont(new Font("Arial", Font.BOLD, 13));
        websiteLabel.setForeground(new Color(80, 80, 80));
        websiteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel website = new JLabel("www.jgcks.com");
        website.setFont(new Font("Arial", Font.PLAIN, 14));
        website.setForeground(new Color(0, 102, 204));
        website.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(websiteLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(website);
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

    private String autocorrectSoftware(String input) {
        if (input == null || input.isEmpty()) return input;
        String normalized = input.toLowerCase().trim();
        if (normalized.matches(".*auto.*cad.*")) return "AutoCAD";
        if (normalized.matches(".*wind.*")) return "Windows";
        if (normalized.matches(".*photo.*shop.*")) return "Photoshop";
        if (normalized.matches(".*rar.*")) return "WinRAR";
        if (normalized.matches(".*zo+m.*")) return "Zoom";
        if (normalized.matches(".*office.*")) return "Office";
        if (normalized.matches(".*slack.*")) return "Slack";
        if (normalized.matches(".*adobe.*")) return "Adobe";
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private void handleLicenseDialog(JTable table, DefaultTableModel tableModel, Runnable reload) {
        int selRow = table != null ? table.getSelectedRow() : -1;
        DatabaseHelper.License existing = null;
        
        if (selRow >= 0) {
            String software = (String) tableModel.getValueAt(selRow, 0);
            String user = (String) tableModel.getValueAt(selRow, 1);
            String key = (String) tableModel.getValueAt(selRow, 2);
            String code = (String) tableModel.getValueAt(selRow, 3);
            String expiry = (String) tableModel.getValueAt(selRow, 4);
            String status = (String) tableModel.getValueAt(selRow, 6);
            double price = (double) tableModel.getValueAt(selRow, 7);
            int usage = (int) tableModel.getValueAt(selRow, 8);
            double cost = (double) tableModel.getValueAt(selRow, 9);
            existing = new DatabaseHelper.License(software, user, key, code, expiry, status, price, usage, cost);
        }

        JTextField softwareF = new JTextField(existing == null ? "" : existing.software);
        JTextField userF = new JTextField(existing == null ? "" : existing.userName);
        JTextField keyF = new JTextField(existing == null ? "" : existing.licenseKey);
        JTextField codeF = new JTextField(existing == null ? "" : existing.licenseCode);
        JTextField expiryF = new JTextField(existing == null ? "" : existing.expiry);
        JTextField statusF = new JTextField(existing == null ? "active" : existing.status);
        JTextField priceF = new JTextField(existing == null ? "" : String.valueOf(existing.price));
        
        JButton suggestPriceBtn = new JButton("Get Price");
        suggestPriceBtn.addActionListener(e -> {
            String sw = softwareF.getText().trim();
            Double stdPrice = SOFTWARE_PRICES.get(sw);
            if (stdPrice != null) priceF.setText(String.valueOf(stdPrice));
            else JOptionPane.showMessageDialog(this, "No standard price for " + sw);
        });

        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.add(new JLabel("Software:")); p.add(softwareF);
        p.add(new JLabel("User:")); p.add(userF);
        p.add(new JLabel("License Key:")); p.add(keyF);
        p.add(new JLabel("Code:")); p.add(codeF);
        p.add(new JLabel("Expiry (MM/dd/yyyy):")); p.add(expiryF);
        p.add(new JLabel("Status:")); p.add(statusF);
        p.add(new JLabel("Price:")); p.add(priceF);
        p.add(new JLabel("")); p.add(suggestPriceBtn);

        int option = JOptionPane.showConfirmDialog(this, p,
            existing == null ? "New License" : "Edit License",
            JOptionPane.OK_CANCEL_OPTION);
            
        if (option != JOptionPane.OK_OPTION) return;

        try {
            String software = softwareF.getText().trim();
            String user = userF.getText().trim();
            String key = keyF.getText().trim();
            String code = codeF.getText().trim();
            String expiry = expiryF.getText().trim();
            String status = statusF.getText().trim();
            double price = Double.parseDouble(priceF.getText().trim());
            int usage = existing == null ? 0 : existing.usage;
            double costPerUse = usage == 0 ? price : price / usage;

            DatabaseHelper.License lic = new DatabaseHelper.License(
                software, user, key, code, expiry, status, price, usage, costPerUse);
            boolean ok = existing == null ? db.addLicense(lic) : db.updateLicense(existing.licenseCode, lic);
            
            if (ok) {
                reload.run();
                JOptionPane.showMessageDialog(this, "License saved successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save license");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
        }
    }

    private void handleDeleteLicense(JTable table, DefaultTableModel tableModel, Runnable reload) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a license to delete");
            return;
        }
        
        String code = (String) tableModel.getValueAt(row, 3);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this license?", "Confirm", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (db.deleteLicense(code)) {
                reload.run();
                JOptionPane.showMessageDialog(this, "License deleted");
            }
        }
    }

    private void handleExportCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export to CSV");
        chooser.setSelectedFile(new java.io.File("licenses.csv"));
        
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (PrintWriter pw = new PrintWriter(new FileWriter(chooser.getSelectedFile()))) {
            pw.println("Software,User,Key,Code,Expiry,Status,Price,Usage,Cost/Use");
            
            for (DatabaseHelper.License lic : db.getAllLicenses()) {
                pw.printf("%s,%s,%s,%s,%s,%s,%.2f,%d,%.2f\n",
                    lic.software, lic.userName, lic.licenseKey, lic.licenseCode,
                    lic.expiry, lic.status, lic.price, lic.usage, lic.costPerUse);
            }
            
            JOptionPane.showMessageDialog(this, "CSV exported successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseHelper db = new DatabaseHelper();
            new LicenseManagerGUI(db).setVisible(true);
        });
    }
}