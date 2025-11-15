# Software License and Usage Management System  
**Fall 2025 — Software Engineering Project**  
**Developed by:** J9ck & josedtovar 

---

## Overview  
The **Software License and Usage Management System** is a comprehensive desktop application for managing and monitoring software licenses. It provides a clear overview of license status, cost, usage patterns, and project tasks to help reduce waste and ensure compliance.

---

## Features  
| Category | Description |
|-----------|-------------|
| **License Management** | Add, edit, and delete software license records with modern UI |
| **Usage Tracking** | Record user activity, usage time, and expiration data |
| **Database Storage** | Robust SQLite database with indexing for better performance |
| **Spell Checker** | Fuzzy string matching for license names (e.g., "zom+" → "Zoom+") |
| **To-Do List Manager** | Integrated task tracking for project management |
| **Modern UI** | Clean, user-friendly interface with color-coded elements |
| **Menu Bar Controls** | Functional top menu for managing products, tools, licenses, and help options |
| **Data View** | Table-based display with alternating row colors and formatted currency |

---

## Recent Improvements (Version 2.5)
- ✅ **Enhanced Database Storage**: SQLite with indices and UNIQUE constraints
- ✅ **Redesigned GUI**: Modern color scheme, better spacing, styled buttons with hover effects
- ✅ **Spell Check Feature**: Fuzzy matching for license names to correct typos
- ✅ **To-Do List**: Integrated task manager for tracking project features
- ✅ **Better Error Handling**: Improved validation and user feedback
- ✅ **Code Organization**: Reorganized repository structure

---

## How It Works  
1. Launch the application (`LicenseManagerGUI.java`).  
2. Use the **Login** screen (username: `admin`, password: `ric401`).
3. Use the top **Menu Bar** to:
   - Add or remove software products
   - Manage licenses
   - Access the **To-Do List** (Tools → To-Do List)
   - View help and about information
4. Add licenses with automatic spell-checking for software names.
5. Track license usage and cost-per-use metrics.
6. Manage project tasks in the integrated To-Do List.

---

## To-Do List Features
The integrated To-Do List tracks important project tasks:
- Security concerns
- Task manager checking  
- Program usage tracking
- Database implementation (✓ Complete)
- Spell checker (✓ Complete)

---

## Technical Stack  
- **Language:** Java 17
- **GUI Framework:** Swing  
- **Database:** SQLite with JDBC
- **Libraries:** 
  - Apache Commons Text (Fuzzy String Matching)
  - SQLite JDBC Driver
- **IDE:** NetBeans / IntelliJ IDEA / Eclipse  
- **Version Control:** Git & GitHub  

---

## Project Structure
```
├── src/licensemanagergui/    # Main source code
│   ├── LicenseManagerGUI.java
│   ├── DatabaseHelper.java
│   ├── LoginPage.java
│   ├── SpellChecker.java
│   └── TodoManager.java
├── lib/                       # External libraries
├── build/                     # Compiled classes
├── nbproject/                 # NetBeans project files
└── Archive (Pre-Presentation)/ # Historical code versions
```

---

## Building and Running
1. Ensure Java 17+ is installed
2. Compile: `javac -cp "lib/*" -d build/classes src/licensemanagergui/*.java`
3. Run: `java -cp "build/classes:lib/*" licensemanagergui.LoginPage`

Or use NetBeans to build and run the project directly.

---

## Authors  
- **Jack Doyle** — Application Developer & UI Designer
- **David** — Backend and Database Developer  

---

## Version History
- **v2.5** - Added To-Do List, spell checker, GUI redesign, database improvements
- **v2.0** - SQLite database integration
- **v1.0** - Initial release with basic license management

---

## License
© 2025 Jack Doyle. All rights reserved.
Visit: www.jgcks.com
