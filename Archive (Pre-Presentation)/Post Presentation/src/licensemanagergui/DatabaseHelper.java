package licensemanagergui;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:license_manager.db";
    private Connection conn;
    
    public static class License {
        public String software, userName, licenseKey, licenseCode, expiry, status;
        public double price, costPerUse;
        public int usage;
        
        public License(String software, String userName, String licenseKey, 
                      String licenseCode, String expiry, String status, 
                      double price, int usage, double costPerUse) {
            this.software = software;
            this.userName = userName;
            this.licenseKey = licenseKey;
            this.licenseCode = licenseCode;
            this.expiry = expiry;
            this.status = status;
            this.price = price;
            this.usage = usage;
            this.costPerUse = costPerUse;
        }
    }
    
    public DatabaseHelper() {
        try {
            Class.forName("org.sqlite.JDBC");
            connect();
            createTables();
        } catch (Exception e) {
            System.err.println("DB init error: " + e.getMessage());
        }
    }
    
    private void connect() throws SQLException {
        conn = DriverManager.getConnection(DB_URL);
    }
    
    private void createTables() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS licenses (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "software TEXT NOT NULL," +
                    "user_name TEXT NOT NULL," +
                    "license_key TEXT NOT NULL," +
                    "license_code TEXT NOT NULL UNIQUE," +
                    "expiry TEXT NOT NULL," +
                    "status TEXT NOT NULL," +
                    "price REAL NOT NULL," +
                    "usage INTEGER DEFAULT 0," +
                    "cost_per_use REAL NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            // Create indices for better query performance
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_software ON licenses(software)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_status ON licenses(status)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_created_at ON licenses(created_at)");
        }
    }
    
    public boolean addLicense(License license) {
        String sql = "INSERT INTO licenses (software, user_name, license_key, license_code, " +
                    "expiry, status, price, usage, cost_per_use) VALUES (?,?,?,?,?,?,?,?,?)";
        
        try {
            // Ensure connection is valid
            if (conn == null || conn.isClosed()) {
                connect();
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, license.software);
                pstmt.setString(2, license.userName);
                pstmt.setString(3, license.licenseKey);
                pstmt.setString(4, license.licenseCode);
                pstmt.setString(5, license.expiry);
                pstmt.setString(6, license.status);
                pstmt.setDouble(7, license.price);
                pstmt.setInt(8, license.usage);
                pstmt.setDouble(9, license.costPerUse);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Add license error: " + e.getMessage());
            return false;
        }
    }
    
    public List<License> getAllLicenses() {
        List<License> licenses = new ArrayList<>();
        String sql = "SELECT * FROM licenses ORDER BY created_at DESC";
        
        try {
            // Ensure connection is valid
            if (conn == null || conn.isClosed()) {
                connect();
            }
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    licenses.add(new License(
                        rs.getString("software"),
                        rs.getString("user_name"),
                        rs.getString("license_key"),
                        rs.getString("license_code"),
                        rs.getString("expiry"),
                        rs.getString("status"),
                        rs.getDouble("price"),
                        rs.getInt("usage"),
                        rs.getDouble("cost_per_use")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Get all licenses error: " + e.getMessage());
        }
        return licenses;
    }
    
    public boolean updateUsage(String licenseCode, int usage, double costPerUse) {
        String sql = "UPDATE licenses SET usage=?, cost_per_use=? WHERE license_code=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usage);
            pstmt.setDouble(2, costPerUse);
            pstmt.setString(3, licenseCode);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    
    public boolean deleteLicense(String licenseCode) {
        String sql = "DELETE FROM licenses WHERE license_code=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, licenseCode);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    
    public boolean deleteBySoftware(String software) {
        String sql = "DELETE FROM licenses WHERE software=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, software);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {}
    }
}