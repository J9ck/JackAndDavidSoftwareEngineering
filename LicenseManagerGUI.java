// LicenseManagerGUI.java
// Created by Jack Doyle
// Updated on October 27, 2025

package licensemanagergui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LicenseManagerGUI {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Software License Manager");
        frame.setSize(500, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Software License Manager", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));

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

        inputPanel.add(softwareLabel);
        inputPanel.add(softwareDropdown);
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(keyLabel);
        inputPanel.add(keyField);
        inputPanel.add(expiryLabel);
        inputPanel.add(expiryField);
        inputPanel.add(generateButton);
        inputPanel.add(clearButton);

        frame.add(inputPanel, BorderLayout.CENTER);

        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scroll = new JScrollPane(outputArea);
        frame.add(scroll, BorderLayout.SOUTH);

        generateButton.addActionListener(e -> {
            String software = (String) softwareDropdown.getSelectedItem();
            String name = nameField.getText().trim();
            String key = keyField.getText().trim();
            String expiry = expiryField.getText().trim();

            if (name.isEmpty() || key.isEmpty() || expiry.isEmpty()) {
                outputArea.setText("Please fill out all fields before generating a license.");
                return;
            }

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate expDate = LocalDate.parse(expiry, formatter);
                String licenseCode = software.toUpperCase() + "-" + name.toUpperCase() + "-" + Math.abs(key.hashCode());
                outputArea.setText("License Generated Successfully!\n\n"
                        + "Software: " + software + "\n"
                        + "Name: " + name + "\n"
                        + "Key: " + key + "\n"
                        + "License Code: " + licenseCode + "\n"
                        + "Expires: " + expDate.format(formatter) + "\n"
                        + "Status: ACTIVE");
            } catch (Exception ex) {
                outputArea.setText("Invalid date format. Please use MM/DD/YYYY.");
            }
        });

        clearButton.addActionListener(e -> {
            softwareDropdown.setSelectedIndex(0);
            nameField.setText("");
            keyField.setText("");
            expiryField.setText("");
            outputArea.setText("");
        });

        frame.setVisible(true);
    }
}
