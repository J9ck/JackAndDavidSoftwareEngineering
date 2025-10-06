// LicenseManagerGUI.java
// Made by Jack Doyle
// October 06, 2025

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LicenseManager {
    public static void main(String[] args) {
        JFrame frame = new JFrame("License Manager");
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 5, 5));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();

        JLabel keyLabel = new JLabel("License Key:");
        JTextField keyField = new JTextField();

        JButton generateButton = new JButton("Generate License");

        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(keyLabel);
        inputPanel.add(keyField);
        inputPanel.add(new JLabel()); // spacer
        inputPanel.add(generateButton);

        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(outputArea);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);

        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String key = keyField.getText().trim();

                if (name.isEmpty() || key.isEmpty()) {
                    outputArea.setText("Please fill out both fields.");
                } else {
                    // Fake license validation / generation
                    String licenseCode = name.toUpperCase() + "-" + key.hashCode();
                    outputArea.setText("License generated:\n" + licenseCode);
                }
            }
        });

        frame.setVisible(true);
    }
}
