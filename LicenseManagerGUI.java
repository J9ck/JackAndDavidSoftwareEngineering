// LicenseManagerGUI.java
// Made by Jack Doyle
// October 06, 2025

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LicenseManagerGUI extends JFrame {

    private JTextField licenseKeyField;
    private JButton activateButton;
    private JButton deactivateButton;
    private JLabel statusLabel;

    public LicenseManagerGUI() {
        setTitle("License Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Enter License Key:");
        licenseKeyField = new JTextField();
        activateButton = new JButton("Activate");
        deactivateButton = new JButton("Deactivate");
        statusLabel = new JLabel("Status: Not Activated");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(activateButton);
        buttonPanel.add(deactivateButton);

        panel.add(label);
        panel.add(licenseKeyField);
        panel.add(buttonPanel);
        panel.add(statusLabel);

        add(panel);

        activateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String key = licenseKeyField.getText().trim();
                if (key.isEmpty()) {
                    statusLabel.setText("Status: Please enter a license key.");
                } else {
                    statusLabel.setText("Status: License activated.");
                }
            }
        });

        deactivateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("Status: License deactivated.");
                licenseKeyField.setText("");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LicenseManagerGUI().setVisible(true);
        });
    }
}
