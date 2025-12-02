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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

// POI (EXCEL)
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.WorkbookFactory;



public class ReportsAnalyticsPage extends JFrame {

    private final AddNewLicensePage addPage;
    private final String excelPath;

    private final java.util.List<LicenseRecord> licenses = new ArrayList<>();
    private JTable tblUpcoming;
    private JTable tblVendor;
    private JTable tblYear;
    private JTable tblAllLicenses;
    private JTable tblExpiredLicenses;


    private JComboBox<String> cmbDaysAhead;

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    public ReportsAnalyticsPage(AddNewLicensePage addPage, String excelPath) {
       
        this.addPage = addPage;
        this.excelPath = excelPath;

        setTitle("J&J Software License Portal - Reports & Analytics");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 

        initUI();
        loadFromExcel();
        checkExpiredLicenses();   
        refreshUpcoming();
        populateAllLicensesTable();
        populateExpiredLicensesTable();        // so the table updates immediately

    }


    private void initUI() {

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        setContentPane(root);

        // Top Header same UI style as other pages
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 70, 140)); 
        headerPanel.setPreferredSize(new Dimension(0, 60));

        JLabel lblAppName = new JLabel("Reports & Analytics");
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
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.add(mainPanel, BorderLayout.CENTER);


        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterBar.setBackground(getBackground());
        filterBar.setBorder(BorderFactory.createEmptyBorder(4, 16, 4, 16));

        JLabel lblFilter = new JLabel("Show expirations within:");
        cmbDaysAhead = new JComboBox<>(new String[]{"30 days", "60 days", "90 days", "180 days"});

        JButton btnApply = new JButton("Apply");
        btnApply.addActionListener(e -> refreshUpcoming());

        filterBar.add(lblFilter);
        filterBar.add(cmbDaysAhead);
        filterBar.add(btnApply);

        mainPanel.add(filterBar, BorderLayout.BEFORE_FIRST_LINE);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();

        // upcoming expirations
        tblUpcoming = createTable(new String[]{"Software", "Vendor", "Expiration", "Cost"});
        JPanel up = new JPanel(new BorderLayout());
        up.add(new JScrollPane(tblUpcoming), BorderLayout.CENTER);
        tabs.add("Upcoming Expirations", up);

        // cost by vendor
        tblVendor = createTable(new String[]{"Vendor", "Total Spend", "License Count"});
        JPanel vendor = new JPanel(new BorderLayout());
        vendor.add(new JScrollPane(tblVendor), BorderLayout.CENTER);
        tabs.add("Cost per Vendor", vendor);

        // total for the year
        tblYear = createTable(new String[]{"Year", "Total Spend", "License Count"});
        JPanel yr = new JPanel(new BorderLayout());
        yr.add(new JScrollPane(tblYear), BorderLayout.CENTER);
        tabs.add("Total Spend by Year", yr);
        
        // All Licenses entire Excel sheet
        tblAllLicenses = createTable(new String[]{
                "Software", "Vendor", "Type", "License Key",
                "Seats", "Purchase Date", "Expiration Date", "Cost", "Notes"
        });
        JPanel allPanel = new JPanel(new BorderLayout());
        allPanel.add(new JScrollPane(tblAllLicenses), BorderLayout.CENTER);
        tabs.add("All Licenses", allPanel);

        // Expired Licenses only 
        tblExpiredLicenses = createTable(new String[]{
                "Software", "Vendor", "Type", "License Key",
                "Seats", "Purchase Date", "Expiration Date", "Cost", "Notes"
        });
        JPanel expiredPanel = new JPanel(new BorderLayout());
        expiredPanel.add(new JScrollPane(tblExpiredLicenses), BorderLayout.CENTER);
        tabs.add("Expired Licenses", expiredPanel);


        mainPanel.add(tabs, BorderLayout.CENTER);
    }
    
    


    private JTable createTable(String[] cols) {
        JTable t = new JTable(new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        });
        t.setRowHeight(22);
        t.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        t.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        return t;
    }

    //  this loads the excel 

    private void loadFromExcel() {
        licenses.clear();

        File file = new File(excelPath);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this,
                    "Excel file not found:\n" + excelPath,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet sheet = wb.getSheetAt(0);
            DataFormatter df = new DataFormatter();

            int last = sheet.getLastRowNum();

            for (int i = 1; i <= last; i++) { // skip row 0 header
                Row row = sheet.getRow(i);
                if (row == null) continue;

                LicenseRecord rec = new LicenseRecord();

                // column order
                rec.software    = df.formatCellValue(row.getCell(0));
                rec.vendor      = df.formatCellValue(row.getCell(1));
                rec.type        = df.formatCellValue(row.getCell(2));
                rec.licenseKey  = df.formatCellValue(row.getCell(3));
                rec.seats       = df.formatCellValue(row.getCell(4));
                rec.purchase    = readDate(row.getCell(5));
                rec.expiration  = readDate(row.getCell(6));
                rec.cost        = readCost(row.getCell(7));
                rec.notes       = df.formatCellValue(row.getCell(8));

                if (!rec.software.isBlank())
                    licenses.add(rec);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error reading Excel:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // refresh tables
        refreshUpcoming();
        refreshVendors();
        refreshYears();
    }
    
    
    private void checkExpiredLicenses() {

        LocalDate today = LocalDate.now();
        int expiredCount = 0;

        for (LicenseRecord rec : licenses) {
            if (rec.expiration != null && rec.expiration.isBefore(today)) {
                expiredCount++;
            }
        }

        if (expiredCount > 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "⚠ WARNING: " + expiredCount + " licenses are EXPIRED.\n" +
                    "Please review them in the Expired Licenses tab.",
                    "Expired Licenses Detected",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }
    
    private void populateAllLicensesTable() {
    if (tblAllLicenses == null) return;

    DefaultTableModel model = (DefaultTableModel) tblAllLicenses.getModel();
    model.setRowCount(0);

    for (LicenseRecord rec : licenses) {
        model.addRow(new Object[]{
                rec.software,
                rec.vendor,
                rec.type,
                rec.licenseKey,
                rec.seats,
                rec.purchase != null ? rec.purchase.format(DATE_FORMAT) : "",
                rec.expiration != null ? rec.expiration.format(DATE_FORMAT) : "",
                rec.cost,
                rec.notes
        });
    }
}

    private void populateExpiredLicensesTable() {
        if (tblExpiredLicenses == null) return;

        DefaultTableModel model = (DefaultTableModel) tblExpiredLicenses.getModel();
        model.setRowCount(0);

        java.time.LocalDate today = java.time.LocalDate.now();

        for (LicenseRecord rec : licenses) {
            if (rec.expiration != null && rec.expiration.isBefore(today)) {
                model.addRow(new Object[]{
                        rec.software,
                        rec.vendor,
                        rec.type,
                        rec.licenseKey,
                        rec.seats,
                        rec.purchase != null ? rec.purchase.format(DATE_FORMAT) : "",
                        rec.expiration.format(DATE_FORMAT),
                        rec.cost,
                        rec.notes
                });
            }
        }
    }


    // reads the cells

    private LocalDate readDate(Cell c) {
        try {
            if (c == null) return null;

            if (c.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(c)) {
                return c.getDateCellValue().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }

            String txt = c.toString().trim();
            if (txt.isEmpty()) return null;

            return LocalDate.parse(txt, DATE_FORMAT);

        } catch (Exception ignored) {
            return null;
        }
    }

    private BigDecimal readCost(Cell c) {
        try {
            if (c == null) return BigDecimal.ZERO;

            if (c.getCellType() == CellType.NUMERIC)
                return BigDecimal.valueOf(c.getNumericCellValue());

            String s = c.toString().replace("$", "").trim();
            if (s.isEmpty()) return BigDecimal.ZERO;

            return new BigDecimal(s);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    //  refrheshes the table 

    private int getDaysAhead() {
        String s = cmbDaysAhead.getSelectedItem().toString();
        if (s.startsWith("30")) return 30;
        if (s.startsWith("60")) return 60;
        if (s.startsWith("90")) return 90;
        return 180;
    }

    private void refreshUpcoming() {
        DefaultTableModel m = (DefaultTableModel) tblUpcoming.getModel();
        m.setRowCount(0);

        LocalDate now = LocalDate.now();
        LocalDate limit = now.plusDays(getDaysAhead());

        licenses.stream()
                .filter(l -> l.expiration != null)
                .filter(l -> !l.expiration.isBefore(now) && !l.expiration.isAfter(limit))
                .sorted(Comparator.comparing(l -> l.expiration))
                .forEach(l -> m.addRow(new Object[]{
                        l.software,
                        l.vendor,
                        l.expiration != null ? DATE_FORMAT.format(l.expiration) : "",
                        l.cost
                }));
    }

    private void refreshVendors() {
        DefaultTableModel m = (DefaultTableModel) tblVendor.getModel();
        m.setRowCount(0);

        Map<String, VendorSummary> map = new HashMap<>();

        for (LicenseRecord l : licenses) {
            String v = l.vendor.isBlank() ? "(Unknown)" : l.vendor;

            VendorSummary vs = map.getOrDefault(v, new VendorSummary(v));
            vs.total = vs.total.add(l.cost);
            vs.count++;
            map.put(v, vs);
        }

        map.values().stream()
                .sorted((a, b) -> b.total.compareTo(a.total))
                .forEach(vs -> m.addRow(new Object[]{
                        vs.vendor,
                        vs.total,
                        vs.count
                }));
    }

    private void refreshYears() {
        DefaultTableModel m = (DefaultTableModel) tblYear.getModel();
        m.setRowCount(0);

        Map<Integer, YearSummary> map = new HashMap<>();

        for (LicenseRecord l : licenses) {
            LocalDate date = (l.purchase != null) ? l.purchase : l.expiration;
            if (date == null) continue;

            int year = date.getYear();
            YearSummary ys = map.getOrDefault(year, new YearSummary(year));
            ys.total = ys.total.add(l.cost);
            ys.count++;

            map.put(year, ys);
        }

        map.values().stream()
                .sorted((a, b) -> b.year - a.year)
                .forEach(ys -> m.addRow(new Object[]{
                        ys.year,
                        ys.total,
                        ys.count
                }));
    }

    // records

    private static class LicenseRecord {
        String software;
        String vendor;
        String type;
        String licenseKey;
        String seats;
        LocalDate purchase;
        LocalDate expiration;
        BigDecimal cost;
        String notes;
    }

    private static class VendorSummary {
        String vendor;
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;

        VendorSummary(String vendor) { this.vendor = vendor; }
    }

    private static class YearSummary {
        int year;
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;

        YearSummary(int year) { this.year = year; }
    }
}

