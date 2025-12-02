package licensemanagergui;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    
    private static DatabaseHelper instance;

    // Global access point
    public static DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper(); // calls your existing constructor
        }
        return instance;
    }

    //DB info was removed to updload on Github.. its jacks db

    private Connection conn;

    public static class User {
        public int id;
        public String username, role;
        public User(int id, String username, String role) {
            this.id = id;
            this.username = username;
            this.role = role;
        }
    }

    public static class Product {
        public int id;
        public String name;
        public Product(int id, String name) { this.id = id; this.name = name; }
    }

    public static class License {
        public String software, userName, licenseKey, licenseCode, expiry, status;
        public double price, costPerUse;
        public int usage;

        public License(String software, String userName, String licenseKey, String licenseCode,
                       String expiry, String status, double price, int usage, double costPerUse) {
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

        public String[] toCSVRow() {
            return new String[]{
                software, userName, licenseKey, licenseCode, expiry, status,
                String.valueOf(price), String.valueOf(usage), String.valueOf(costPerUse)
            };
        }
    }

    public DatabaseHelper() {
        System.out.println("=== DatabaseHelper Constructor Starting ===");
        try {
            System.out.println("Loading PostgreSQL driver...");
            Class.forName("org.postgresql.Driver");
            System.out.println("✓ PostgreSQL driver loaded successfully!");
            
            System.out.println("Connecting to database...");
            System.out.println("URL: " + DB_URL);
            System.out.println("User: " + DB_USER);
            
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✓ Database connected successfully!");
            
            createTables();
            System.out.println("✓ Tables created/verified");
            
            insertDefaultData();
            System.out.println("✓ Default data inserted");
            
            System.out.println("=== DatabaseHelper initialized successfully! ===\n");
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ ERROR: PostgreSQL driver not found!");
            System.err.println("Make sure postgresql-42.x.x.jar is in your libraries");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ ERROR: Database connection failed!");
            System.err.println("Check your credentials and network connection");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ ERROR: Unexpected error during initialization");
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id SERIAL PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    role VARCHAR(20) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS products (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(100) UNIQUE NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS licenses (
                    id SERIAL PRIMARY KEY,
                    software VARCHAR(100) NOT NULL,
                    user_name VARCHAR(100) NOT NULL,
                    license_key VARCHAR(255) NOT NULL,
                    license_code VARCHAR(255) NOT NULL,
                    expiry VARCHAR(50) NOT NULL,
                    status VARCHAR(50) NOT NULL,
                    price NUMERIC(10,2) NOT NULL,
                    usage_count INT DEFAULT 0,
                    cost_per_use NUMERIC(10,2) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
        }
    }

    private void insertDefaultData() throws SQLException {
        if (getUser("admin") == null) addUser("admin", "ric401", "admin");
        if (getAllProducts().isEmpty()) {
            addProduct("AutoCAD");
            addProduct("Windows");
            addProduct("Photoshop");
            addProduct("WinRAR");
        }
    }

    // ---- USER FUNCTIONS ----
    public boolean addUser(String username, String password, String role) {
        if (getUser(username) != null) return false;
        String sql = "INSERT INTO users (username,password,role) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public User validateLogin(String username, String password) {
        if (conn == null) {
            System.err.println("❌ ERROR: Database connection is null!");
            return null;
        }
        
        String sql = "SELECT * FROM users WHERE username=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && password.equals(rs.getString("password"))) {
                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("role"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public User getUser(String username) {
        String sql = "SELECT * FROM users WHERE username=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new User(rs.getInt("id"), rs.getString("username"), rs.getString("role"));
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, username, role FROM users ORDER BY username")) {
            while (rs.next()) users.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("role")));
        } catch (SQLException e) { e.printStackTrace(); }
        return users;
    }

    public boolean deleteUser(String username) {
        if ("admin".equals(username)) return false;
        String sql = "DELETE FROM users WHERE username=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateUserPassword(String username, String newPassword) {
        String sql = "UPDATE users SET password=? WHERE username=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateUserRole(String username, String newRole) {
        String sql = "UPDATE users SET role=? WHERE username=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newRole);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ---- PRODUCT FUNCTIONS ----
    public boolean addProduct(String name) {
        String sql = "INSERT INTO products(name) VALUES(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products ORDER BY name")) {
            while (rs.next()) list.add(new Product(rs.getInt("id"), rs.getString("name")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean deleteProduct(String name) {
        String sql = "DELETE FROM products WHERE name=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ---- LICENSE FUNCTIONS ----
    public boolean addLicense(License license) {
        String sql = """
            INSERT INTO licenses
            (software,user_name,license_key,license_code,expiry,status,price,usage_count,cost_per_use)
            VALUES (?,?,?,?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, license.software);
            ps.setString(2, license.userName);
            ps.setString(3, license.licenseKey);
            ps.setString(4, license.licenseCode);
            ps.setString(5, license.expiry);
            ps.setString(6, license.status);
            ps.setDouble(7, license.price);
            ps.setInt(8, license.usage);
            ps.setDouble(9, license.costPerUse);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<License> getAllLicenses() {
        List<License> list = new ArrayList<>();
        String sql = "SELECT * FROM licenses ORDER BY created_at DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new License(
                    rs.getString("software"),
                    rs.getString("user_name"),
                    rs.getString("license_key"),
                    rs.getString("license_code"),
                    rs.getString("expiry"),
                    rs.getString("status"),
                    rs.getDouble("price"),
                    rs.getInt("usage_count"),
                    rs.getDouble("cost_per_use")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean updateLicense(String licenseCode, License updated) {
        String sql = """
            UPDATE licenses SET software=?, user_name=?, license_key=?, expiry=?, status=?,
            price=?, usage_count=?, cost_per_use=? WHERE license_code=?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, updated.software);
            ps.setString(2, updated.userName);
            ps.setString(3, updated.licenseKey);
            ps.setString(4, updated.expiry);
            ps.setString(5, updated.status);
            ps.setDouble(6, updated.price);
            ps.setInt(7, updated.usage);
            ps.setDouble(8, updated.costPerUse);
            ps.setString(9, licenseCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateUsage(String licenseCode, int usage, double costPerUse) {
        String sql = "UPDATE licenses SET usage_count=?, cost_per_use=? WHERE license_code=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usage);
            ps.setDouble(2, costPerUse);
            ps.setString(3, licenseCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean recordUsageEvent(String software, String userName) {
        String findLicense = """
            SELECT license_code, usage_count, price FROM licenses
            WHERE software=? AND user_name=? LIMIT 1
        """;
        try (PreparedStatement ps = conn.prepareStatement(findLicense)) {
            ps.setString(1, software);
            ps.setString(2, userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String licenseCode = rs.getString("license_code");
                int currentUsage = rs.getInt("usage_count");
                double price = rs.getDouble("price");
                int newUsage = currentUsage + 1;
                double newCostPerUse = price / newUsage;
                return updateUsage(licenseCode, newUsage, newCostPerUse);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean deleteLicense(String licenseCode) {
        String sql = "DELETE FROM licenses WHERE license_code=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, licenseCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteBySoftware(String software) {
        String sql = "DELETE FROM licenses WHERE software=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, software);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public void close() {
        try { if (conn != null && !conn.isClosed()) conn.close(); }
        catch (SQLException ignored) {}
    }
}