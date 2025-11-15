// LicenseManagerGUI.java
// Created by Jack Doyle
// October 27, 2025
package licensemanagergui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class LicenseManagerGUI extends JFrame {
    private static DatabaseHelper db;

    public LicenseManagerGUI() {
        // set native look and feel
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        // Modern color scheme
        Color primaryColor = new Color(41, 128, 185); // Modern blue
        Color accentColor = new Color(52, 152, 219); // Light blue
        Color backgroundColor = new Color(245, 247, 250); // Light gray background
        Color panelBackground = Color.WHITE;
        
        // fix menu colors with modern theme
        UIManager.put("Menu.foreground", Color.BLACK);
        UIManager.put("MenuItem.foreground", Color.BLACK);
        UIManager.put("Menu.selectionBackground", accentColor);
        UIManager.put("Menu.selectionForeground", Color.WHITE);
        UIManager.put("MenuItem.selectionBackground", accentColor);
        UIManager.put("MenuItem.selectionForeground", Color.WHITE);
        UIManager.put("MenuBar.background", panelBackground);
        UIManager.put("Menu.background", panelBackground);
        UIManager.put("MenuItem.background", panelBackground);

        db = new DatabaseHelper();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> db.close()));

        setTitle("Software License Manager");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(backgroundColor);

        // left tree with improved styling
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Database Storage");
        DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("Local Licenses");
        root.add(node1);
        JTree tree = new JTree(root);
        tree.setFont(new Font("Arial", Font.PLAIN, 13));
        tree.setBackground(panelBackground);
        JScrollPane treeScroll = new JScrollPane(tree);
        treeScroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // right panel with modern styling
        JPanel rightPanel = new JPanel(new BorderLayout(15, 15));
        rightPanel.setBackground(backgroundColor);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // license form with improved layout
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(primaryColor, 2),
                "License Generator",
                0,
                0,
                new Font("Arial", Font.BOLD, 14),
                primaryColor
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formPanel.setBackground(panelBackground);

        JLabel softwareLabel = new JLabel("Software:");
        softwareLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        String[] softwareOptions = {"AutoCAD", "Windows", "Photoshop", "WinRAR", "Zoom+", "Microsoft Office", "Adobe Creative Cloud"};
        JComboBox<String> softwareDropdown = new JComboBox<>(softwareOptions);
        softwareDropdown.setFont(new Font("Arial", Font.PLAIN, 13));
        softwareDropdown.setEditable(true); // Allow typing custom names
        
        JLabel nameLabel = new JLabel("User Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 13));
        
        JLabel keyLabel = new JLabel("License Key:");
        keyLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        JTextField keyField = new JTextField();
        keyField.setFont(new Font("Arial", Font.PLAIN, 13));
        
        JLabel expiryLabel = new JLabel("Expiry (MM/DD/YYYY):");
        expiryLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        JTextField expiryField = new JTextField();
        expiryField.setFont(new Font("Arial", Font.PLAIN, 13));
        
        JLabel priceLabel = new JLabel("License Price:");
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        JTextField priceField = new JTextField();
        priceField.setFont(new Font("Arial", Font.PLAIN, 13));

        JButton generateButton = new JButton("Generate License");
        styleButton(generateButton, primaryColor, Color.WHITE);
        
        JButton clearButton = new JButton("Clear Fields");
        styleButton(clearButton, new Color(149, 165, 166), Color.WHITE);
        
        JButton recordUsageButton = new JButton("Record Usage");
        styleButton(recordUsageButton, new Color(39, 174, 96), Color.WHITE);

        formPanel.add(softwareLabel); formPanel.add(softwareDropdown);
        formPanel.add(nameLabel); formPanel.add(nameField);
        formPanel.add(keyLabel); formPanel.add(keyField);
        formPanel.add(expiryLabel); formPanel.add(expiryField);
        formPanel.add(priceLabel); formPanel.add(priceField);
        formPanel.add(generateButton); formPanel.add(clearButton);
        formPanel.add(recordUsageButton); formPanel.add(new JLabel(""));

        rightPanel.add(formPanel, BorderLayout.NORTH);

        // license table with modern styling
        String[] columns = {"Software","User","License Key","License Code","Expiry","Status","Price","Usage","Cost/Use"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(accentColor);
        table.setSelectionForeground(Color.WHITE);
        
        // Style table header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBackground(primaryColor);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 30));
        
        // Add alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                return c;
            }
        });
        
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        // status bar with modern styling
        JLabel statusLabel = new JLabel("License Count: 0");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(panelBackground);
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        statusPanel.add(statusLabel, BorderLayout.EAST);
        add(statusPanel, BorderLayout.SOUTH);

        // main split with better proportions
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, rightPanel);
        splitPane.setDividerLocation(200);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        add(splitPane, BorderLayout.CENTER);

        // menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        JMenu productMenu = new JMenu("Product");
        JMenu licenseMenu = new JMenu("License");
        JMenu toolsMenu = new JMenu("Tools");
        JMenu helpMenu = new JMenu("Help");
        
        // Style menus
        Font menuFont = new Font("Arial", Font.PLAIN, 13);
        productMenu.setFont(menuFont);
        licenseMenu.setFont(menuFont);
        toolsMenu.setFont(menuFont);
        helpMenu.setFont(menuFont);

        JMenuItem addProduct = new JMenuItem("Add Product");
        JMenuItem removeProduct = new JMenuItem("Remove Product");
        JMenuItem newLicense = new JMenuItem("New License");
        JMenuItem deleteLicense = new JMenuItem("Delete License");
        JMenuItem exportItem = new JMenuItem("Export Data");
        JMenuItem aboutItem = new JMenuItem("About");

        productMenu.add(addProduct); productMenu.add(removeProduct);
        licenseMenu.add(newLicense); licenseMenu.add(deleteLicense);
        toolsMenu.add(exportItem); helpMenu.add(aboutItem);
        menuBar.add(productMenu); menuBar.add(licenseMenu); menuBar.add(toolsMenu); menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // load from db
        Runnable loadFromDB = () -> {
            tableModel.setRowCount(0);
            List<DatabaseHelper.License> licenses = db.getAllLicenses();
            for (DatabaseHelper.License lic : licenses) {
                tableModel.addRow(new Object[]{
                    lic.software, lic.userName, lic.licenseKey, lic.licenseCode,
                    lic.expiry, lic.status, String.format("$%.2f", lic.price), 
                    lic.usage, String.format("$%.2f", lic.costPerUse)
                });
            }
            statusLabel.setText("license count: " + tableModel.getRowCount());
        };
        loadFromDB.run();

        // add product
        addProduct.addActionListener(e -> {
            String newProd = JOptionPane.showInputDialog(this, "enter new product:");
            if (newProd != null && !newProd.isBlank()) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newProd);
                ((DefaultMutableTreeNode) tree.getModel().getRoot()).add(newNode);
                ((DefaultTreeModel) tree.getModel()).reload();
                softwareDropdown.addItem(newProd);
                JOptionPane.showMessageDialog(this, "added: " + newProd);
            }
        });

        // remove product
        removeProduct.addActionListener(e -> {
            var path = tree.getSelectionPath();
            if (path == null || path.getLastPathComponent() == tree.getModel().getRoot()) {
                JOptionPane.showMessageDialog(this, "select something to remove", "error", JOptionPane.ERROR_MESSAGE);
            } else {
                DefaultMutableTreeNode selected = (DefaultMutableTreeNode) path.getLastPathComponent();
                String selectedName = selected.toString();

                db.deleteBySoftware(selectedName);

                for (int i = 0; i < softwareDropdown.getItemCount(); i++) {
                    if (softwareDropdown.getItemAt(i).equals(selectedName)) {
                        softwareDropdown.removeItemAt(i);
                        break;
                    }
                }
                selected.removeFromParent();
                ((DefaultTreeModel) tree.getModel()).reload();
                loadFromDB.run();
                JOptionPane.showMessageDialog(this, "removed: " + selectedName);
            }
        });

        // new license
        newLicense.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "create a new license form here.");
        });

        // delete license
        deleteLicense.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String licenseCode = (String) tableModel.getValueAt(row, 3);
                int confirm = JOptionPane.showConfirmDialog(this, "delete this license?", "confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (db.deleteLicense(licenseCode)) {
                        loadFromDB.run();
                        JOptionPane.showMessageDialog(this, "license deleted.");
                    } else {
                        JOptionPane.showMessageDialog(this, "failed to delete license.", "error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "select a license first.");
            }
        });

        // export
        exportItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "export feature coming soon.");
        });

        // about
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Software License Manager\n" +
                "Created by Jack Doyle\n" +
                "www.jgcks.com\n" +
                "© 2025\n" +
                "Version 2.5\n\n" +
                "Features:\n" +
                "• SQLite Database Storage\n" +
                "• Fuzzy Spell-Check for License Names\n" +
                "• Modern User Interface",
                "About", JOptionPane.INFORMATION_MESSAGE);
        });

        // generate license with spell-check support
        generateButton.addActionListener(e -> {
            Object selectedItem = softwareDropdown.getSelectedItem();
            String software = selectedItem != null ? selectedItem.toString().trim() : "";
            String name = nameField.getText().trim();
            String key = keyField.getText().trim();
            String expiry = expiryField.getText().trim();
            String priceText = priceField.getText().trim();

            if (name.isEmpty() || key.isEmpty() || expiry.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get all available software names for spell checking
            List<String> availableSoftware = new ArrayList<>();
            for (int i = 0; i < softwareDropdown.getItemCount(); i++) {
                availableSoftware.add(softwareDropdown.getItemAt(i));
            }
            
            // Check if the entered software name needs spell correction
            if (!availableSoftware.contains(software)) {
                String suggestion = SpellChecker.findBestMatch(software, availableSoftware);
                if (suggestion != null) {
                    int response = JOptionPane.showConfirmDialog(this,
                        "Did you mean '" + suggestion + "' instead of '" + software + "'?",
                        "Spelling Suggestion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                    
                    if (response == JOptionPane.YES_OPTION) {
                        software = suggestion;
                        softwareDropdown.setSelectedItem(suggestion);
                    }
                }
            }

            try {
                double price = Double.parseDouble(priceText);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate expDate = LocalDate.parse(expiry, formatter);
                String licenseCode = software.toUpperCase() + "-" + name.toUpperCase() + "-" + Math.abs(key.hashCode());
                int usage = 0;
                double costPerUse = price;

                DatabaseHelper.License license = new DatabaseHelper.License(
                        software, name, key, licenseCode, expDate.format(formatter),
                        "active", price, usage, costPerUse
                );

                if (db.addLicense(license)) {
                    loadFromDB.run();
                    nameField.setText("");
                    keyField.setText("");
                    expiryField.setText("");
                    priceField.setText("");
                    JOptionPane.showMessageDialog(this, "License generated successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save license to database", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price format. Please enter a valid number.", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM/DD/YYYY.", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // record usage
        recordUsageButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String licenseCode = (String) tableModel.getValueAt(row, 3);
                int usage = (int) tableModel.getValueAt(row, 7);
                usage++;
                double price = (double) tableModel.getValueAt(row, 6);
                double newCostPerUse = price / usage;

                if (db.updateUsage(licenseCode, usage, newCostPerUse)) {
                    loadFromDB.run();
                } else {
                    JOptionPane.showMessageDialog(this, "failed to update", "error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "select a license first", "error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // clear fields
        clearButton.addActionListener(e -> {
            softwareDropdown.setSelectedIndex(0);
            nameField.setText("");
            keyField.setText("");
            expiryField.setText("");
            priceField.setText("");
        });
    }
    
    /**
     * Helper method to style buttons with modern appearance
     */
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LicenseManagerGUI().setVisible(true));
    }
}
