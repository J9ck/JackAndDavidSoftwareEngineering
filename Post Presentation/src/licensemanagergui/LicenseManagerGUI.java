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
        // set native look and feel
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        // fix menu colors
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

        setTitle("software license manager");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // left tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("database storage");
        DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("local licenses");
        root.add(node1);
        JTree tree = new JTree(root);
        JScrollPane treeScroll = new JScrollPane(tree);

        // right panel
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);

        // license form
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("license generator"));
        formPanel.setBackground(Color.WHITE);

        JLabel softwareLabel = new JLabel("software:");
        String[] softwareOptions = {"AutoCAD", "Windows", "Photoshop", "WinRAR"};
        JComboBox<String> softwareDropdown = new JComboBox<>(softwareOptions);
        JLabel nameLabel = new JLabel("user name:");
        JTextField nameField = new JTextField();
        JLabel keyLabel = new JLabel("license key:");
        JTextField keyField = new JTextField();
        JLabel expiryLabel = new JLabel("expiry (mm/dd/yyyy):");
        JTextField expiryField = new JTextField();
        JLabel priceLabel = new JLabel("license price:");
        JTextField priceField = new JTextField();

        JButton generateButton = new JButton("generate license");
        JButton clearButton = new JButton("clear fields");
        JButton recordUsageButton = new JButton("record usage");

        formPanel.add(softwareLabel); formPanel.add(softwareDropdown);
        formPanel.add(nameLabel); formPanel.add(nameField);
        formPanel.add(keyLabel); formPanel.add(keyField);
        formPanel.add(expiryLabel); formPanel.add(expiryField);
        formPanel.add(priceLabel); formPanel.add(priceField);
        formPanel.add(generateButton); formPanel.add(clearButton);
        formPanel.add(recordUsageButton); formPanel.add(new JLabel(""));

        rightPanel.add(formPanel, BorderLayout.NORTH);

        // license table
        String[] columns = {"software","user","license key","license code","expiry","status","price","usage","cost/use"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        // status bar
        JLabel statusLabel = new JLabel("license count: 0");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3,10,3,10));
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.EAST);
        add(statusPanel, BorderLayout.SOUTH);

        // main split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, rightPanel);
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);

        // menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu productMenu = new JMenu("product");
        JMenu licenseMenu = new JMenu("license");
        JMenu toolsMenu = new JMenu("tools");
        JMenu helpMenu = new JMenu("help");

        JMenuItem addProduct = new JMenuItem("add product");
        JMenuItem removeProduct = new JMenuItem("remove product");
        JMenuItem newLicense = new JMenuItem("new license");
        JMenuItem deleteLicense = new JMenuItem("delete license");
        JMenuItem exportItem = new JMenuItem("export data");
        JMenuItem aboutItem = new JMenuItem("about");

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
                    lic.expiry, lic.status, lic.price, lic.usage, lic.costPerUse
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
                "software license manager\ncreated by jack doyle\nwww.jgcks.com\n© 2025\nversion 2.0",
                "about", JOptionPane.INFORMATION_MESSAGE);
        });

        // generate license
        generateButton.addActionListener(e -> {
            String software = (String) softwareDropdown.getSelectedItem();
            String name = nameField.getText().trim();
            String key = keyField.getText().trim();
            String expiry = expiryField.getText().trim();
            String priceText = priceField.getText().trim();

            if (name.isEmpty() || key.isEmpty() || expiry.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "fill all fields", "error", JOptionPane.ERROR_MESSAGE);
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
                        "active", price, usage, costPerUse
                );

                if (db.addLicense(license)) {
                    loadFromDB.run();
                    nameField.setText("");
                    keyField.setText("");
                    expiryField.setText("");
                    priceField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "failed to save", "error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "bad input", "error", JOptionPane.ERROR_MESSAGE);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LicenseManagerGUI().setVisible(true));
    }
}