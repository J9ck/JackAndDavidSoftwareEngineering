// LicenseManagerGUI.java
// Created by Jack Doyle
// October 27, 2025
package licensemanagergui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LicenseManagerGUI extends JFrame {
    private static DatabaseHelper db;

    public LicenseManagerGUI() {
        // --- Apply native Look and Feel first ---
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        // --- Then apply color overrides to fix white-on-white menu ---
        UIManager.put("Menu.foreground", Color.BLACK);
        UIManager.put("MenuItem.foreground", Color.BLACK);
        UIManager.put("Menu.selectionBackground", new Color(230, 230, 230));
        UIManager.put("Menu.selectionForeground", Color.BLACK);
        UIManager.put("MenuItem.selectionBackground", new Color(230, 230, 230));
        UIManager.put("MenuItem.selectionForeground", Color.BLACK);
        UIManager.put("MenuBar.background", new Color(245, 245, 245));
        UIManager.put("Menu.background", new Color(245, 245, 245));
        UIManager.put("MenuItem.background", new Color(245, 245, 245));

        db = new DatabaseHelper();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> db.close()));

        setTitle("Software License Manager");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Database Storage");
        DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("Local Licenses");
        root.add(node1);
        JTree tree = new JTree(root);
        JScrollPane treeScroll = new JScrollPane(tree);

        // right panel
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);

        // form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("License Generator"));
        formPanel.setBackground(Color.WHITE);

        JLabel softwareLabel = new JLabel("Software:");
        String[] softwareOptions = {"AutoCAD", "Windows", "Photoshop", "WinRAR"};
        JComboBox<String> softwareDropdown = new JComboBox<>(softwareOptions);
        JLabel nameLabel = new JLabel("User Name:");
        JTextField nameField = new JTextField();
        JLabel keyLabel = new JLabel("License Key:");
        JTextField keyField = new JTextField();
        JLabel expiryLabel = new JLabel("Expiry (MM/DD/YYYY):");
        JTextField expiryField = new JTextField();
        JLabel priceLabel = new JLabel("License Price:");
        JTextField priceField = new JTextField();

        JButton generateButton = new JButton("Generate License");
        JButton clearButton = new JButton("Clear Fields");
        JButton recordUsageButton = new JButton("Record Usage");

        formPanel.add(softwareLabel); formPanel.add(softwareDropdown);
        formPanel.add(nameLabel); formPanel.add(nameField);
        formPanel.add(keyLabel); formPanel.add(keyField);
        formPanel.add(expiryLabel); formPanel.add(expiryField);
        formPanel.add(priceLabel); formPanel.add(priceField);
        formPanel.add(generateButton); formPanel.add(clearButton);
        formPanel.add(recordUsageButton); formPanel.add(new JLabel(""));

        rightPanel.add(formPanel, BorderLayout.NORTH);

        // license table
        String[] columns = {"Software","User","License Key","License Code","Expiry","Status","Price","Usage","Cost/Use"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        // status bar
        JLabel statusLabel = new JLabel("License Count: 0");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3,10,3,10));
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
        JMenuItem deleteLicense = new JMenuItem("Delete License");
        JMenuItem exportItem = new JMenuItem("Export Data");
        JMenuItem aboutItem = new JMenuItem("About");

        productMenu.add(addProduct); productMenu.add(removeProduct);
        licenseMenu.add(newLicense); licenseMenu.add(deleteLicense);
        toolsMenu.add(exportItem); helpMenu.add(aboutItem);
        menuBar.add(productMenu); menuBar.add(licenseMenu); menuBar.add(toolsMenu); menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // Helper to load licenses from database
        Runnable loadFromDB = () -> {
            tableModel.setRowCount(0);
            List<DatabaseHelper.License> licenses = db.getAllLicenses();
            for (DatabaseHelper.License lic : licenses) {
                tableModel.addRow(new Object[]{
                    lic.software, lic.userName, lic.licenseKey, lic.licenseCode,
                    lic.expiry, lic.status, lic.price, lic.usage, lic.costPerUse
                });
            }
            statusLabel.setText("License Count: " + tableModel.getRowCount());
        };

        loadFromDB.run();

        // --- Menu item actions ---
        addProduct.addActionListener(e -> {
            String newProd = JOptionPane.showInputDialog(this, "Enter new product:");
            if (newProd != null && !newProd.isBlank()) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newProd);
                ((DefaultMutableTreeNode) tree.getModel().getRoot()).add(newNode);
                ((DefaultTreeModel) tree.getModel()).reload();
                softwareDropdown.addItem(newProd);
                JOptionPane.showMessageDialog(this, "Added: " + newProd);
            }
        });

        removeProduct.addActionListener(e -> {
            var path = tree.getSelectionPath();
            if (path == null || path.getLastPathComponent() == tree.getModel().getRoot()) {
                JOptionPane.showMessageDialog(this, "Select something to remove", "Error", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Removed: " + selectedName);
            }
        });

        newLicense.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Create a new license form here.")
        );

        deleteLicense.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Select a license and delete it.")
        );

        exportItem.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Export functionality coming soon.")
        );

        aboutItem.addActionListener(e ->
            JOptionPane.showMessageDialog(
                this,
                "Software License Manager\nCreated by Jack Doyle \nwww.jgcks.com\n© 2025\nVersion 2.0",
                "About",
                JOptionPane.INFORMATION_MESSAGE
            )
        );

        generateButton.addActionListener(e -> {
            String software = (String) softwareDropdown.getSelectedItem();
            String name = nameField.getText().trim();
            String key = keyField.getText().trim();
            String expiry = expiryField.getText().trim();
            String priceText = priceField.getText().trim();
            if (name.isEmpty() || key.isEmpty() || expiry.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
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
                        "ACTIVE", price, usage, costPerUse
                );

                if (db.addLicense(license)) {
                    loadFromDB.run();
                    nameField.setText("");
                    keyField.setText("");
                    expiryField.setText("");
                    priceField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Bad input", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

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
                    JOptionPane.showMessageDialog(this, "Failed to update", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a license to record usage", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> {
            softwareDropdown.setSelectedIndex(0);
            nameField.setText("");
            keyField.setText("");
            expiryField.setText("");
            priceField.setText("");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LicenseManagerGUI().setVisible(true));
    }
}
