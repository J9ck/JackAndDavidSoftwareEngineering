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

public class LicenseManagerGUI {
    public static void main(String[] args) {
        // main window
        JFrame frame = new JFrame("Software License Manager");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // tree stuff
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Database Storage");
        DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("Local Licenses");
        root.add(node1);
        JTree tree = new JTree(root);
        JScrollPane treeScroll = new JScrollPane(tree);

        // right panel
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);

        // form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("License Generator"));
        formPanel.setBackground(Color.WHITE);

        JLabel softwareLabel = new JLabel("Software:");
        String[] softwareOptions = {"AutoCAD", "Windows", "Photoshop", "WinRAR"};
        JComboBox<String> softwareDropdown = new JComboBox<>(softwareOptions);
        JLabel nameLabel = new JLabel("User Name:");
        JTextField nameField = new JTextField();
        JLabel keyLabel = new JLabel("License Key:");
        JTextField keyField = new JTextField();
        JLabel expiryLabel = new JLabel("Expiry Date (MM/DD/YYYY):");
        JTextField expiryField = new JTextField();
        JButton generateButton = new JButton("Generate License");
        JButton clearButton = new JButton("Clear Fields");

        // add form stuff
        formPanel.add(softwareLabel);
        formPanel.add(softwareDropdown);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(keyLabel);
        formPanel.add(keyField);
        formPanel.add(expiryLabel);
        formPanel.add(expiryField);
        formPanel.add(generateButton);
        formPanel.add(clearButton);

        rightPanel.add(formPanel, BorderLayout.NORTH);

        // table for licenses
        String[] columns = {"Software", "User", "License Key", "License Code", "Expiry", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        // status bar
        JLabel statusLabel = new JLabel("License Count: 0");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.EAST);
        frame.add(statusPanel, BorderLayout.SOUTH);

        // split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, rightPanel);
        splitPane.setDividerLocation(250);
        frame.add(splitPane, BorderLayout.CENTER);

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

        productMenu.add(addProduct);
        productMenu.add(removeProduct);
        licenseMenu.add(newLicense);
        licenseMenu.add(deleteLicense);
        toolsMenu.add(exportItem);
        helpMenu.add(aboutItem);

        menuBar.add(productMenu);
        menuBar.add(licenseMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        frame.setJMenuBar(menuBar);

        // add product button
        addProduct.addActionListener(e -> {
            String newProd = JOptionPane.showInputDialog(frame, "Enter new product name:");
            if (newProd != null && !newProd.isBlank()) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newProd);
                ((DefaultMutableTreeNode) tree.getModel().getRoot()).add(newNode);
                ((DefaultTreeModel) tree.getModel()).reload();

                softwareDropdown.addItem(newProd);

                // TODO: send to backend or db
                JOptionPane.showMessageDialog(frame, "Product added: " + newProd);
            }
        });

        // remove product button
        removeProduct.addActionListener(e -> {
            var path = tree.getSelectionPath();
            if (path == null || path.getLastPathComponent() == tree.getModel().getRoot()) {
                JOptionPane.showMessageDialog(frame, "select something to remove", "error", JOptionPane.ERROR_MESSAGE);
            } else {
                DefaultMutableTreeNode selected = (DefaultMutableTreeNode) path.getLastPathComponent();
                String selectedName = selected.toString();

                // remove from dropdown
                for (int i = 0; i < softwareDropdown.getItemCount(); i++) {
                    if (softwareDropdown.getItemAt(i).equals(selectedName)) {
                        softwareDropdown.removeItemAt(i);
                        break;
                    }
                }

                // remove from table
                for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
                    if (tableModel.getValueAt(i, 0).equals(selectedName)) {
                        tableModel.removeRow(i);
                    }
                }

                selected.removeFromParent();
                ((DefaultTreeModel) tree.getModel()).reload();
                statusLabel.setText("License Count: " + tableModel.getRowCount());

                // TODO: remove from backend/db
                JOptionPane.showMessageDialog(frame, "removed: " + selectedName);
            }
        });

        // new license button
        newLicense.addActionListener(e -> {
            String software = JOptionPane.showInputDialog(frame, "software:");
            String user = JOptionPane.showInputDialog(frame, "user:");
            String key = JOptionPane.showInputDialog(frame, "license key:");
            String expiry = JOptionPane.showInputDialog(frame, "expiry (MM/DD/YYYY):");
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate expDate = LocalDate.parse(expiry, formatter);
                String licenseCode = software.toUpperCase() + "-" + user.toUpperCase() + "-" + Math.abs(key.hashCode());
                tableModel.addRow(new Object[]{software, user, key, licenseCode, expDate.format(formatter), "ACTIVE"});
                statusLabel.setText("License Count: " + tableModel.getRowCount());

                // TODO: save new license backend
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "invalid input", "error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // delete license button
        deleteLicense.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                // TODO: delete license backend
                tableModel.removeRow(row);
                statusLabel.setText("License Count: " + tableModel.getRowCount());
            } else {
                JOptionPane.showMessageDialog(frame, "select a license to delete", "error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // export and about
        exportItem.addActionListener(e -> JOptionPane.showMessageDialog(frame, "export not done yet"));
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(frame, "license manager v1.0 by jack & david\nwww.jgcks.com"));

        // generate license button
        generateButton.addActionListener(e -> {
            String software = (String) softwareDropdown.getSelectedItem();
            String name = nameField.getText().trim();
            String key = keyField.getText().trim();
            String expiry = expiryField.getText().trim();
            if (name.isEmpty() || key.isEmpty() || expiry.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "fill all fields", "error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate expDate = LocalDate.parse(expiry, formatter);
                String licenseCode = software.toUpperCase() + "-" + name.toUpperCase() + "-" + Math.abs(key.hashCode());
                tableModel.addRow(new Object[]{software, name, key, licenseCode, expDate.format(formatter), "ACTIVE"});
                statusLabel.setText("License Count: " + tableModel.getRowCount());

                // TODO: save generated license backend
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "bad date format", "error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // clear fields
        clearButton.addActionListener(e -> {
            softwareDropdown.setSelectedIndex(0);
            nameField.setText("");
            keyField.setText("");
            expiryField.setText("");
        });

        // look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        frame.setVisible(true);
    }
}
