/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package licensemanagergui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 *
 * @author josedavidtovar
 */
public class SettingsPage extends JFrame{


    public SettingsPage(DatabaseHelper db) {
       
        

        setTitle("J&J Software License Portal - Settings");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 

        initUI();
                

    }


    private void initUI() {

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        setContentPane(root);

        // Top Header same UI style as other pages
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 70, 140)); 
        headerPanel.setPreferredSize(new Dimension(0, 60));

        JLabel lblAppName = new JLabel("Settings");
        lblAppName.setForeground(Color.WHITE);
        lblAppName.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblAppName.setBorder(new EmptyBorder(0, 20, 0, 0));

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        headerRight.setOpaque(false);

        JButton btnClose = new JButton("Close");
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dispose()); // closes the window and returns to dashboard

        headerRight.add(btnClose);

        headerPanel.add(lblAppName, BorderLayout.WEST);
        headerPanel.add(headerRight, BorderLayout.EAST);

        root.add(headerPanel, BorderLayout.NORTH);
        
//        JPanel mainPanel = new JPanel(new BorderLayout());
//        mainPanel.setBackground(Color.WHITE);
//        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
//        root.add(mainPanel, BorderLayout.CENTER);
        // Main content area
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.add(mainPanel, BorderLayout.CENTER);

        // Centered text: "Settings coming soon..."
        JLabel comingSoonLabel = new JLabel("Settings coming soon...", SwingConstants.CENTER);
        comingSoonLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));

        mainPanel.add(comingSoonLabel, BorderLayout.CENTER);

        
    }
    
}


