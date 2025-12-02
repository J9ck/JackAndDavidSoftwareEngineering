package licensemanagergui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MonitorAgent {
    private DatabaseHelper db;
    private String employeeName;
    private boolean running = true;
    private Set<String> alreadyDetected = new HashSet<>();
    
    private static final Map<String, String> PROCESS_MAP = new HashMap<>();
    static {
        // Windows process names
        PROCESS_MAP.put("acad.exe", "AutoCAD");
        PROCESS_MAP.put("photoshop.exe", "Photoshop");
        PROCESS_MAP.put("winrar.exe", "WinRAR");
        PROCESS_MAP.put("zoom.exe", "Zoom");
        PROCESS_MAP.put("slack.exe", "Slack");
        PROCESS_MAP.put("winword.exe", "Office");
        PROCESS_MAP.put("excel.exe", "Office");
        PROCESS_MAP.put("powerpnt.exe", "Office");
        
        // Mac process names (without .exe)
        PROCESS_MAP.put("autocad", "AutoCAD");
        PROCESS_MAP.put("adobe photoshop", "Photoshop");
        PROCESS_MAP.put("zoom.us", "Zoom");
        PROCESS_MAP.put("zoom", "Zoom");
        PROCESS_MAP.put("cpthost", "Zoom"); // Zoom's background process on Mac
        PROCESS_MAP.put("slack", "Slack");
        PROCESS_MAP.put("microsoft word", "Office");
        PROCESS_MAP.put("microsoft excel", "Office");
        
        // Add common browsers for testing
        PROCESS_MAP.put("chrome.exe", "Chrome");
        PROCESS_MAP.put("firefox.exe", "Firefox");
        PROCESS_MAP.put("safari", "Safari");
        PROCESS_MAP.put("google chrome", "Chrome");
    }
    
    public MonitorAgent(String employeeName) {
        this.employeeName = employeeName;
        this.db = new DatabaseHelper();
        
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║   License Monitor Agent v2.0       ║");
        System.out.println("╚════════════════════════════════════╝\n");
        System.out.println("Employee: " + employeeName);
        System.out.println("=================================");
        
        // Setup employee in database
        setupEmployee();
        
        System.out.println("\nMonitoring for these programs:");
        for (String software : new HashSet<>(PROCESS_MAP.values())) {
            System.out.println("  - " + software);
        }
        System.out.println("=================================");
        System.out.println("✓ Agent initialized successfully!");
        System.out.println("Checking every 10 seconds...");
        System.out.println("Keep this window open!");
        System.out.println("=================================\n");
    }
    
    /**
     * Setup employee in the database with licenses for all monitored software
     */
    private void setupEmployee() {
        System.out.println("\n[SETUP] Checking employee in database...");
        
        // Get unique software names
        Set<String> softwareList = new HashSet<>(PROCESS_MAP.values());
        
        // For each software, ensure a license exists for this employee
        for (String software : softwareList) {
            ensureLicenseExists(software, employeeName);
        }
        
        System.out.println("[SETUP] ✓ Employee setup complete!");
    }
    
    /**
     * Ensure a license exists for this employee and software.
     * If it doesn't exist, create one automatically.
     */
    private void ensureLicenseExists(String software, String userName) {
        // Check if license already exists
        List<DatabaseHelper.License> allLicenses = db.getAllLicenses();
        boolean exists = false;
        
        for (DatabaseHelper.License lic : allLicenses) {
            if (lic.software.equalsIgnoreCase(software) && 
                lic.userName.equalsIgnoreCase(userName)) {
                exists = true;
                System.out.println("[SETUP] ✓ License found: " + software + " for " + userName);
                break;
            }
        }
        
        if (!exists) {
            // Create new license
            System.out.println("[SETUP] Creating new license: " + software + " for " + userName);
            
            // Generate license details
            String licenseKey = generateLicenseKey(software, userName);
            String licenseCode = software.toUpperCase() + "-" + 
                                userName.toUpperCase() + "-" + 
                                Math.abs(licenseKey.hashCode());
            
            // Set expiry date 1 year from now
            LocalDate expiry = LocalDate.now().plusYears(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            String expiryStr = expiry.format(formatter);
            
            // Default price for licenses
            double price = 99.99; // You can customize this per software
            
            // Create the license
            DatabaseHelper.License newLicense = new DatabaseHelper.License(
                software,
                userName,
                licenseKey,
                licenseCode,
                expiryStr,
                "active",
                price,
                0, // initial usage count
                price // initial cost per use
            );
            
            if (db.addLicense(newLicense)) {
                System.out.println("[SETUP] ✓ License created successfully!");
            } else {
                System.err.println("[SETUP] ✗ Failed to create license for " + software);
            }
        }
    }
    
    /**
     * Generate a simple license key
     */
    private String generateLicenseKey(String software, String userName) {
        String combined = software + userName + System.currentTimeMillis();
        int hash = Math.abs(combined.hashCode());
        return String.format("%04d-%04d-%04d", 
            hash % 10000, 
            (hash / 10000) % 10000, 
            (hash / 100000000) % 10000);
    }
    
    public void checkProcesses() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            Process process;
            
            // Different command for Mac vs Windows
            if (os.contains("mac")) {
                process = Runtime.getRuntime().exec("ps -Ao comm");
            } else if (os.contains("windows")) {
                process = Runtime.getRuntime().exec("tasklist /FO CSV /NH");
            } else {
                System.out.println("Unsupported OS: " + os);
                return;
            }
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            
            String line;
            Set<String> currentlyRunning = new HashSet<>();
            
            while ((line = reader.readLine()) != null) {
                String lineLower = line.toLowerCase();
                
                // Check if any monitored software is running
                for (Map.Entry<String, String> entry : PROCESS_MAP.entrySet()) {
                    String processName = entry.getKey();
                    String softwareName = entry.getValue();
                    
                    if (lineLower.contains(processName)) {
                        currentlyRunning.add(softwareName);
                        
                        // Only log once per session to avoid spam
                        if (!alreadyDetected.contains(softwareName)) {
                            alreadyDetected.add(softwareName);
                            
                            System.out.println("✓ DETECTED: " + softwareName);
                            System.out.println("  Process: " + processName);
                            System.out.println("  User: " + employeeName);
                            
                            // Record usage in database - THIS UPDATES THE GUI!
                            boolean success = db.recordUsageEvent(softwareName, employeeName);
                            
                            if (success) {
                                System.out.println("  Status: Usage recorded in database ✓");
                                System.out.println("  ► The GUI will update automatically within 3 seconds!");
                            } else {
                                System.out.println("  Status: Could not record usage");
                            }
                            System.out.println();
                        }
                    }
                }
            }
            
            // Remove from detected set if process is no longer running
            alreadyDetected.retainAll(currentlyRunning);
            
        } catch (Exception e) {
            System.err.println("Error checking processes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Run monitoring loop
    public void startMonitoring() {
        System.out.println("Monitoring started. Checking every 10 seconds...\n");
        
        while (running) {
            checkProcesses();
            
            try {
                // Check every 10 seconds
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                System.out.println("Monitoring interrupted");
                break;
            }
        }
        
        System.out.println("Monitoring stopped");
    }
    
    public void stop() {
        running = false;
        db.close();
    }
    
    // Main method for testing
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n\n");
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║   SOFTWARE LICENSE USAGE MONITOR                  ║");
        System.out.println("║   Track your software usage automatically         ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Get employee name
        System.out.print("Enter your name: ");
        String employeeName = scanner.nextLine().trim();
        
        if (employeeName.isEmpty()) {
            System.out.println("\n❌ Name cannot be empty!");
            scanner.close();
            return;
        }
        
        System.out.println("\n✓ Welcome, " + employeeName + "!");
        System.out.println("\nInitializing monitor agent...");
        
        MonitorAgent agent = new MonitorAgent(employeeName);
        
        // Add shutdown hook for clean exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n\n=================================");
            System.out.println("Shutting down agent...");
            System.out.println("All usage data has been saved.");
            System.out.println("=================================");
            agent.stop();
        }));
        
        System.out.println("\n💡 TIP: Open the License Manager GUI to see");
        System.out.println("   live updates as you use different software!");
        System.out.println("\n💡 TIP: Press Ctrl+C to stop monitoring\n");
        
        // Start monitoring
        agent.startMonitoring();
        
        scanner.close();
    }
}
