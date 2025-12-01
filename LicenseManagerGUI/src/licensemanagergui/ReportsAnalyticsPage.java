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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;

public class ReportsAnalyticsPage extends JPanel {

    private final AddNewLicensePage addPage;
    private final String excelPath;

    // in-memory list of all licenses
    private final java.util.List<LicenseRecord> licenses = new ArrayList<>();

    // Tables
    private JTable tblUpcoming;
    private JTable tblVendor;
    private JTable tblYear;

    private JComboBox<String> cmbDaysAhead;

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    public ReportsAnalyticsPage(AddNewLicensePage addPage, String excelPath) {
        this.addPage = addPage;
        this.excelPath = excelPath;

        initUI();
        loadFromExcel();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new java.awt.Color(245, 245, 245));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new java.awt.Color(30, 30, 30));
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel lblTitle = new JLabel("Reports & Analytics");
        lblTitle.setForeground(java.awt.Color.WHITE);
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));


        JButton btnReload = new JButton("Reload Excel");
        btnReload.setBackground(new java.awt.Color(0, 120, 215));
        btnReload.setForeground(java.awt.Color.WHITE);
        btnReload.addActionListener(e -> loadFromExcel());

        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnReload, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

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

        add(filterBar, BorderLayout.BEFORE_FIRST_LINE);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();

        // --- UPCOMING EXPIRATIONS ---
        tblUpcoming = createTable(new String[]{"Software", "Vendor", "Expiration", "Cost"});
        JPanel up = new JPanel(new BorderLayout());
        up.add(new JScrollPane(tblUpcoming), BorderLayout.CENTER);
        tabs.add("Upcoming Expirations", up);

        // --- COST PER VENDOR ---
        tblVendor = createTable(new String[]{"Vendor", "Total Spend", "License Count"});
        JPanel vendor = new JPanel(new BorderLayout());
        vendor.add(new JScrollPane(tblVendor), BorderLayout.CENTER);
        tabs.add("Cost per Vendor", vendor);

        // --- YEARLY SPEND ---
        tblYear = createTable(new String[]{"Year", "Total Spend", "License Count"});
        JPanel yr = new JPanel(new BorderLayout());
        yr.add(new JScrollPane(tblYear), BorderLayout.CENTER);
        tabs.add("Total Spend by Year", yr);

        add(tabs, BorderLayout.CENTER);
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

    //  EXCEL LOADER 

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

                // your column order
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

    // CELL READERS

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

    //  TABLE REFRESHES 

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

    // RECORD CLASSES 

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
