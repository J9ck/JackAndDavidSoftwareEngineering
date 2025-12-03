# Software License and Usage Management System

**Fall 2025 — Software Engineering 401 Project**  
**Created by Jack Doyle**

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [System Architecture](#system-architecture)
- [Tech Stack](#tech-stack)
- [Installation & Setup](#installation--setup)
- [Usage Guide](#usage-guide)
- [Demo Instructions](#demo-instructions-for-class-presentation)
- [Database Schema](#database-schema)
- [Project Evolution](#project-evolution)
- [Future Enhancements](#future-enhancements)
- [Known Issues](#known-issues)
- [Credits](#credits)

---

## Overview

A desktop application for tracking and monitoring software licenses across an organization. Helps companies reduce waste by showing actual usage patterns and calculating real cost-per-use metrics.

**The Problem:** Companies buy expensive software licenses without knowing if employees actually use them. This system monitors which programs employees run and tracks usage automatically.

---

## Features

- License management (create, edit, delete)
- Automated usage monitoring via background agents
- Remote PostgreSQL database (not local storage)
- User authentication with role-based access
- Real-time cost-per-use calculations
- CSV export for reports
- Autocorrect for software names (handles typos like `"zom+"` → `"Zoom"`)
- Live usage dashboard

---

## System Architecture

```
┌─────────────────────────────────────────────────────────┐
│                  Employee Workstations                  │
│  ┌──────────────┐   ┌──────────────┐   ┌──────────────┐ │
│  │ MonitorAgent │   │ MonitorAgent │   │ MonitorAgent │ │
│  │   (John)     │   │   (Sarah)    │   │   (Mike)     │ │
│  └──────┬───────┘   └──────┬───────┘   └──────┬───────┘ │
└─────────┼──────────────────┼──────────────────┼─────────┘
          │                  │                  │
          └──────────────────┼──────────────────┘
                             ▼
                    ┌─────────────────┐
                    │  PostgreSQL DB  │
                    │ (Remote Server) │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │  Manager GUI    │
                    │   (Dashboard)   │
                    └─────────────────┘
```

Agents check running processes every 30 seconds and send usage data to the database. The manager dashboard shows live updates of who's using what software and calculates if licenses are worth the cost.

---

## Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 17 | Core application language |
| Swing/AWT | GUI framework |
| PostgreSQL | Remote database (hosted on FreeSQLDatabase) |
| JDBC | Database connectivity |
| NetBeans IDE | Development environment |

---

## Installation & Setup

### Prerequisites

- Java JDK 17 or higher
- PostgreSQL Connector/J JAR file
- Internet connection (for remote database)

### Quick Start

1. **Clone the repository:**

   ```bash
   git clone https://github.com/J9ck/JackAndDavidSoftwareEngineering.git
   cd JackAndDavidSoftwareEngineering
   ```

2. **Add PostgreSQL Connector to project:**
   - Download PostgreSQL Connector/J from the internet
   - In NetBeans: Right-click Libraries → Add JAR/Folder → Select the connector JAR

3. **Run the application:**

   ```bash
   java licensemanagergui.LoginPage
   ```

4. **Default credentials:**
   - Username: `admin`
   - Password: `ric401`

### Building JAR for Distribution

1. In NetBeans: Right-click project → Clean and Build
2. JAR file will be in `dist/LicenseManagerGUI.jar`
3. Distribute with PostgreSQL Connector JAR included in classpath

---

## Usage Guide

### For Managers (Main GUI)

1. Login with admin credentials
2. View dashboard showing all licenses and usage statistics
3. Add licenses: Click "License" menu → "New License" or use the quick form
4. Monitor usage in real-time as agents report back
5. Export reports: Tools → Export Data (CSV format)
6. Manage users: Tools → Manage Users

### For Employees (Monitoring Agent)

Deploy the agent on employee workstations:

```bash
java -cp LicenseManagerGUI.jar licensemanagergui.MonitorAgent "Employee Name"
```

The agent runs in the background and reports software usage automatically.

### Demo Mode (For Presentations)

Run the simulator to demonstrate live monitoring:

```bash
java licensemanagergui.AgentSimulator
```

Click "Start Simulation" to simulate 5 employees using various software applications.

---

## Demo Instructions for Class Presentation

### Setup Before Class

1. Create licenses for classmates who'll participate
2. Build JAR file: Clean and Build in NetBeans
3. Copy JAR to 2 borrowed laptops
4. Test connection on school WiFi

### Live Demo Flow

1. Show the problem: companies waste money on unused licenses
2. Login to main GUI and display existing licenses with zero usage
3. Give laptops to 2 classmates
4. They run the monitoring agent and open programs (Chrome, Word, etc.)
5. Your dashboard updates live with usage counts
6. Export CSV report showing cost optimization

### Backup Plan

If laptops fail, run AgentSimulator with real classmate names for a realistic simulation.

---

## Database Schema

<details>
<summary><strong>users table</strong></summary>

```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

</details>

<details>
<summary><strong>products table</strong></summary>

```sql
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

</details>

<details>
<summary><strong>licenses table</strong></summary>

```sql
CREATE TABLE licenses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    software VARCHAR(100) NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    license_key VARCHAR(255) NOT NULL,
    license_code VARCHAR(255) NOT NULL,
    expiry VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    usage_count INT DEFAULT 0,
    cost_per_use DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

</details>

---

## Project Evolution

| Version | Date | Description |
|---------|------|-------------|
| Initial Prototype | October 2025 | Basic GUI with hardcoded credentials and local SQLite database. Manual usage tracking only. |
| Current Version | November 2025 | Remote PostgreSQL database, automated monitoring agents, real-time usage tracking, user authentication, CSV export, autocorrect engine, cost-per-use analytics, role-based access control. |

**Original Vision:** *"Create a license manager that tracks licenses in Windows apps and monitors usage to determine if the company is getting value from employee-held licenses."* — Mission accomplished!

---

## Future Enhancements

- [ ] Password hashing (bcrypt) instead of plaintext
- [ ] Email notifications for expiring licenses
- [ ] Dashboard charts and visualizations
- [ ] License renewal reminders
- [ ] Advanced reporting (most/least used software)
- [ ] Budget forecasting tools

---

## Known Issues

| Issue | Status |
|-------|--------|
| Process detection is case-sensitive on Mac | Known |
| Requires internet for database connection | By Design |
| GUI refresh is manual (no auto-refresh yet) | Planned Fix |

---

## Credits

| Contributor | Role |
|-------------|------|
| **Jack Doyle** ([@j9ck](https://github.com/j9ck)) | Application development, GUI design, database integration, monitoring system, autocorrect engine, user management, documentation |
| **Jose Tovar** ([@josedtovar](https://github.com/josedtovar)) | Login page design |

---

<div align="center">

**Built with ☕ and late nights**  
*Software Engineering 401 • Fall 2025*  
[www.jgcks.com](https://www.jgcks.com)

</div>
