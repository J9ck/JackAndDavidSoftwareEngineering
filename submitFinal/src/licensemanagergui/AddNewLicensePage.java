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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Apache POI to read excel file
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class AddNewLicensePage extends JFrame {

    // Excel file path 
    private static final String EXCEL_PATH = "/Users/josedavidtovar/Documents/licenses.xlsx";
    //Excel file should be in Github, edit the path above to specific directory.
    //jar files are needed to be added to libraries manualy. 
    //move jar file to libraries

    private final List<LicenseItem> licenses = new ArrayList<>();

    // UI Components 
    private JTextField txtSoftware;
    private JTextField txtVendor;
    private JTextField txtKey;
    private JComboBox<String> cboType;
    private JTextField txtSeatsPurchased;
    private JTextField txtPurchaseDate;
    private JTextField txtExpiration;
    private JTextField txtCost;
    private JTextArea txtNotes;

    private JTable table;
    private DefaultTableModel model;

    public AddNewLicensePage() {
        setTitle("J&J Software License Portal - Add New License");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();

        // Load data from Excel into memory and table
        loadFromExcel();
        refreshTable();
    }

    //UI SETUP 

    private void initUI() {
        
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

    // TOP HEADER same as Dashboard
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 70, 140)); 
        headerPanel.setPreferredSize(new Dimension(0, 60));

        JLabel lblAppName = new JLabel("Add New License Information");
        lblAppName.setForeground(Color.WHITE);
        lblAppName.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblAppName.setBorder(new EmptyBorder(0, 20, 0, 0));

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        headerRight.setOpaque(false);

        JLabel lblUser = new JLabel("Admin User"); // we need to pass real user from db
        lblUser.setForeground(Color.WHITE);

        JButton btnClose = new JButton("Close");
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dispose());

        headerRight.add(lblUser);
        headerRight.add(btnClose);

        headerPanel.add(lblAppName, BorderLayout.WEST);
        headerPanel.add(headerRight, BorderLayout.EAST);

        root.add(headerPanel, BorderLayout.NORTH);

    //  MAIN 
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.add(mainPanel, BorderLayout.CENTER);

        // Section title bar
        JPanel sectionHeader = new JPanel(new BorderLayout());
        sectionHeader.setBackground(Color.WHITE);
        sectionHeader.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblSectionTitle = new JLabel("Add New License");
        lblSectionTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        sectionHeader.add(lblSectionTitle, BorderLayout.WEST);

        mainPanel.add(sectionHeader, BorderLayout.NORTH);

        // Left panel (form) 
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        txtSoftware = new JTextField(18);
        txtVendor = new JTextField(18);
        txtKey = new JTextField(18);

        cboType = new JComboBox<>(new String[]{"Subscription", "Perpetual", "User-based", "Device-based"});
        cboType.setPreferredSize(new Dimension(200, 25));

        txtSeatsPurchased = new JTextField(18);
        txtPurchaseDate = new JTextField("MM-DD-YYYY", 18);
        txtExpiration = new JTextField("MM-DD-YYYY", 18);
        txtCost = new JTextField(18);

        txtNotes = new JTextArea(4, 20);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        txtNotes.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        addField(formPanel, gbc, "Software Name:", txtSoftware, 0);
        addField(formPanel, gbc, "Vendor:", txtVendor, 1);
        addField(formPanel, gbc, "License Key:", txtKey, 2);
        addField(formPanel, gbc, "License Type:", cboType, 3);
        addField(formPanel, gbc, "Seats Purchased:", txtSeatsPurchased, 4);
        addField(formPanel, gbc, "Purchase Date:", txtPurchaseDate, 5);
        addField(formPanel, gbc, "Expiration / Renewal:", txtExpiration, 6);
        addField(formPanel, gbc, "Cost (USD):", txtCost, 7);

        //notes row
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(txtNotes), gbc);

        //buttons//
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);

        JButton btnAdd = styledButton("Add License");
        JButton btnUpdate = styledButton("Update");
        JButton btnDelete = styledButton("Delete");
        JButton btnClear = styledButton("Clear");
        JButton btnReload = styledButton("Reload from Excel");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        btnPanel.add(btnReload);

        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        root.add(formPanel, BorderLayout.WEST);

        // TABLE right side
        model = new DefaultTableModel(new Object[]{
                "Software", "Vendor", "Type", "License Key",
                "Seats", "Purchase Date", "Expiration", "Cost", "Notes"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // table is readonly, edits are done in the left form
            }
        };

        table = new JTable(model);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Make table
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(160); // Software
        table.getColumnModel().getColumn(1).setPreferredWidth(130); // Vendor
        table.getColumnModel().getColumn(2).setPreferredWidth(110); // Type
        table.getColumnModel().getColumn(3).setPreferredWidth(200); // License Key
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Seats
        table.getColumnModel().getColumn(5).setPreferredWidth(110); // Purchase
        table.getColumnModel().getColumn(6).setPreferredWidth(120); // Expiration
        table.getColumnModel().getColumn(7).setPreferredWidth(90);  // Cost
        table.getColumnModel().getColumn(8).setPreferredWidth(200); // Notes

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                loadSelectedRow();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        root.add(scroll, BorderLayout.CENTER);

        // button logic 
        btnAdd.addActionListener(e -> addLicense());
        btnUpdate.addActionListener(e -> updateLicense());
        btnDelete.addActionListener(e -> deleteLicense());
        btnClear.addActionListener(e -> clearFields());
        btnReload.addActionListener(e -> {
            loadFromExcel();
            refreshTable();
        });

        setContentPane(root);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, Component field, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(150, 35));
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBackground(new Color(245, 245, 245));              // same gray as nav
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));        // same font
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

    // hover effect like Dashboard.createNavButton()
        btn.addChangeListener(e -> {
            ButtonModel model = btn.getModel();
            if (model.isRollover()) {
                btn.setBackground(new Color(230, 230, 230));
            } else {
                btn.setBackground(new Color(245, 245, 245));
            }
        });

    return btn;
}


    // crud table 

    private void addLicense() {
        LicenseItem item = buildFromFields();
        licenses.add(item);
        refreshTable();
        saveToExcel();
        clearFields();
    }

    private void updateLicense() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a license to update.");
            return;
        }
        licenses.set(row, buildFromFields());
        refreshTable();
        saveToExcel();
    }

    private void deleteLicense() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a license to delete.");
            return;
        }
        licenses.remove(row);
        refreshTable();
        saveToExcel();
        clearFields();
    }

    private LicenseItem buildFromFields() {
        return new LicenseItem(
                txtSoftware.getText().trim(),
                txtVendor.getText().trim(),
                (String) cboType.getSelectedItem(),
                txtKey.getText().trim(),
                txtSeatsPurchased.getText().trim(),
                txtPurchaseDate.getText().trim(),
                txtExpiration.getText().trim(),
                txtCost.getText().trim(),
                txtNotes.getText().trim()
        );
    }

    private void loadSelectedRow() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        LicenseItem l = licenses.get(row);
        txtSoftware.setText(l.software);
        txtVendor.setText(l.vendor);
        cboType.setSelectedItem(l.type);
        txtKey.setText(l.key);
        txtSeatsPurchased.setText(l.seats);
        txtPurchaseDate.setText(l.purchaseDate);
        txtExpiration.setText(l.expiration);
        txtCost.setText(l.cost);
        txtNotes.setText(l.notes);
    }

    private void clearFields() {
        txtSoftware.setText("");
        txtVendor.setText("");
        txtKey.setText("");
        cboType.setSelectedIndex(0);
        txtSeatsPurchased.setText("");
        txtPurchaseDate.setText("MM-dd-yyyy");
        txtExpiration.setText("MM-dd-yyyy");
        txtCost.setText("");
        txtNotes.setText("");
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (LicenseItem l : licenses) {
            model.addRow(new Object[]{
                    l.software,
                    l.vendor,
                    l.type,
                    l.key,
                    l.seats,
                    l.purchaseDate,
                    l.expiration,
                    l.cost,
                    l.notes
            });
        }
    }

    //  EXCEL I/O 

    private void loadFromExcel() {
        licenses.clear();

        File file = new File(EXCEL_PATH);
        if (!file.exists()) {
            
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            boolean firstRow = true;

            for (Row row : sheet) {
                if (firstRow) { // skip header row
                    firstRow = false;
                    continue;
                }

                String software = getCellString(row, 0);
                String vendor = getCellString(row, 1);
                String type = getCellString(row, 2);
                String key = getCellString(row, 3);
                String seats = getCellString(row, 4);
                String purchase = getCellString(row, 5);
                String expiration = getCellString(row, 6);
                String cost = getCellString(row, 7);
                String notes = getCellString(row, 8);

                if (software == null || software.isEmpty()) {
                    continue; // skip blank rows
                }

                licenses.add(new LicenseItem(
                        software, vendor, type, key,
                        seats, purchase, expiration, cost, notes
                ));
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading Excel file:\n" + ex.getMessage(),
                    "Excel Load Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void saveToExcel() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Licenses");

        // Header row
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Software");
        header.createCell(1).setCellValue("Vendor");
        header.createCell(2).setCellValue("Type");
        header.createCell(3).setCellValue("License Key");
        header.createCell(4).setCellValue("Seats");
        header.createCell(5).setCellValue("Purchase Date");
        header.createCell(6).setCellValue("Expiration");
        header.createCell(7).setCellValue("Cost");
        header.createCell(8).setCellValue("Notes");

        int rowIndex = 1;
        for (LicenseItem l : licenses) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(l.software);
            row.createCell(1).setCellValue(l.vendor);
            row.createCell(2).setCellValue(l.type);
            row.createCell(3).setCellValue(l.key);
            row.createCell(4).setCellValue(l.seats);
            row.createCell(5).setCellValue(l.purchaseDate);
            row.createCell(6).setCellValue(l.expiration);
            row.createCell(7).setCellValue(l.cost);
            row.createCell(8).setCellValue(l.notes);
        }

        try (FileOutputStream fos = new FileOutputStream(EXCEL_PATH)) {
            workbook.write(fos);
            workbook.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving Excel file:\n" + ex.getMessage(),
                    "Excel Save Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private String getCellString(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toString();
            } else {
                double d = cell.getNumericCellValue();
                long l = (long) d;
                if (Math.abs(d - l) < 1e-9) {
                    return Long.toString(l);
                }
                return Double.toString(d);
            }
        }
        return cell.toString();
    }

    // model 

    private static class LicenseItem {
        String software;
        String vendor;
        String type;
        String key;
        String seats;
        String purchaseDate;
        String expiration;
        String cost;
        String notes;

        LicenseItem(String software, String vendor, String type, String key,
                    String seats, String purchaseDate, String expiration,
                    String cost, String notes) {

            this.software = software;
            this.vendor = vendor;
            this.type = type;
            this.key = key;
            this.seats = seats;
            this.purchaseDate = purchaseDate;
            this.expiration = expiration;
            this.cost = cost;
            this.notes = notes;
        }
    }

    // test main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddNewLicensePage().setVisible(true));
    }
}

