1| # Software License and Usage Management System  
2| **Fall 2025 — Software Engineering 401 Project**  
3| **Created by Jack Doyle**
4| 
5| ---
6| 
7| ## Overview  
8| A desktop application for tracking and monitoring software licenses across an organization. Helps companies reduce waste by showing actual usage patterns and calculating real cost-per-use metrics.
9| 
10| The problem: Companies buy expensive software licenses without knowing if employees actually use them. This system monitors which programs employees run and tracks usage automatically.
11| 
12| ---
13| 
14| ## Features  
15| 
16| - License management (create, edit, delete)
17| - Automated usage monitoring via background agents
18| - Remote PostgreSQL database (not local storage)
19| - User authentication with role-based access
20| - Real-time cost-per-use calculations
21| - CSV export for reports
22| - Autocorrect for software names (handles typos like "zom+" → "Zoom")
23| - Live usage dashboard
24| 
25| ---
26| 
27| ## System Architecture
28| 
29| ```
30| ┌─────────────────────────────────────────────────────────┐
31| │                  Employee Workstations                  │
32| │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
33| │  │ MonitorAgent │  │ MonitorAgent │  │ MonitorAgent │   │
34| │  │   (John)     │  │   (Sarah)    │  │   (Mike)     │   │
35| │  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘   │
36| └─────────┼──────────────────┼──────────────────┼─────────┘
37|           │                  │                  │
38|           └──────────────────┼──────────────────┘
39|                              ▼
40|                     ┌─────────────────┐
41|                     │PostgreSQL Database│
42|                     │ (Remote Server) │
43|                     └────────┬────────┘
44|                              │
45|                     ┌────────▼────────┐
46|                     │  Manager GUI    │
47|                     │   (Dashboard)   │
48|                     └─────────────────┘
49| ```
50| 
51| Agents check running processes every 30 seconds and send usage data to the database. The manager dashboard shows live updates of who's using what software and calculates if licenses are worth the [...]
52| 
53| ---
54| 
55| ## Tech Stack  
56| 
57| - Java 17
58| - Swing/AWT for GUI
59| - PostgreSQL (remote hosted on FreeSQLDatabase)
60| - JDBC connector
61| - NetBeans IDE
62| 
63| ---
64| 
65| ## Installation & Setup
66| 
67| ### Prerequisites
68| - Java JDK 17 or higher
69| - PostgreSQL Connector/J JAR file
70| - Internet connection (for remote database)
71| 
72| ### Quick Start
73| 
74| 1. **Clone the repository:**
75|    ```bash
76|    git clone https://github.com/J9ck/JackAndDavidSoftwareEngineering.git
77|    cd JackAndDavidSoftwareEngineering
78|    ```
79| 
80| 2. **Add PostgreSQL Connector to project:**
81|    - Download PostgreSQL Connector/J from internet
82|    - In NetBeans: Right-click Libraries → Add JAR/Folder → Select the connector JAR
83| 
84| 3. **Run the application:**
85|    ```bash
86|    java licensemanagergui.LoginPage
87|    ```
88| 
89| 4. **Default credentials:**
90|    - Username: `admin`
91|    - Password: `ric401`
92| 
93| ### Building JAR for Distribution
94| 
95| 1. In NetBeans: Right-click project → Clean and Build
96| 2. JAR file will be in `dist/LicenseManagerGUI.jar`
97| 3. Distribute with PostgreSQL Connector JAR included in classpath
98| 
99| ---
100| 
101| ## Usage Guide
102| 
103| ### For Managers (Main GUI)
104| 
105| 1. Login with admin credentials
106| 2. View dashboard showing all licenses and usage statistics
107| 3. Add licenses: Click "License" menu → "New License" or use the quick form
108| 4. Monitor usage in real-time as agents report back
109| 5. Export reports: Tools → Export Data (CSV format)
110| 6. Manage users: Tools → Manage Users
111| 
112| ### For Employees (Monitoring Agent)
113| 
114| Deploy the agent on employee workstations:
115| ```bash
116| java -cp LicenseManagerGUI.jar licensemanagergui.MonitorAgent "Employee Name"
117| ```
118| 
119| The agent runs in the background and reports software usage automatically.
120| 
121| ### Demo Mode (For Presentations)
122| 
123| Run the simulator to demonstrate live monitoring:
124| ```bash
125| java licensemanagergui.AgentSimulator
126| ```
127| 
128| Click "Start Simulation" to simulate 5 employees using various software applications.
129| 
130| ---
131| 
132| ## Demo Instructions for Class Presentation
133| 
134| ### Setup Before Class
135| 
136| 1. Create licenses for classmates who'll participate
137| 2. Build JAR file: Clean and Build in NetBeans
138| 3. Copy JAR to 2 borrowed laptops
139| 4. Test connection on school WiFi
140| 
141| ### Live Demo Flow
142| 
143| 1. Show the problem: companies waste money on unused licenses
144| 2. Login to main GUI and display existing licenses with zero usage
145| 3. Give laptops to 2 classmates
146| 4. They run the monitoring agent and open programs (Chrome, Word, etc.)
147| 5. Your dashboard updates live with usage counts
148| 6. Export CSV report showing cost optimization
149| 
150| ### Backup Plan
151| If laptops fail, run AgentSimulator with real classmate names for a realistic simulation.
152| 
153| ---
154| 
155| ## Database Schema
156| 
157| **users table**
158| ```sql
159| CREATE TABLE users (
160|     id INT AUTO_INCREMENT PRIMARY KEY,
161|     username VARCHAR(50) UNIQUE NOT NULL,
162|     password VARCHAR(255) NOT NULL,
163|     role VARCHAR(20) NOT NULL,
164|     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
165| );
166| ```
167| 
168| **products table**
169| ```sql
170| CREATE TABLE products (
171|     id INT AUTO_INCREMENT PRIMARY KEY,
172|     name VARCHAR(100) UNIQUE NOT NULL,
173|     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
174| );
175| ```
176| 
177| **licenses table**
178| ```sql
179| CREATE TABLE licenses (
180|     id INT AUTO_INCREMENT PRIMARY KEY,
181|     software VARCHAR(100) NOT NULL,
182|     user_name VARCHAR(100) NOT NULL,
183|     license_key VARCHAR(255) NOT NULL,
184|     license_code VARCHAR(255) NOT NULL,
185|     expiry VARCHAR(50) NOT NULL,
186|     status VARCHAR(50) NOT NULL,
187|     price DECIMAL(10,2) NOT NULL,
188|     usage_count INT DEFAULT 0,
189|     cost_per_use DECIMAL(10,2) NOT NULL,
190|     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
191| );
192| ```
193| 
194| ---
195| 
196| ## Project Evolution
197| 
198| **Initial prototype (October 2025):** Basic GUI with hardcoded credentials and local SQLite database. Manual usage tracking only.
199| 
200| **Current version (November 2025):** Remote PostgreSQL database, automated monitoring agents, real-time usage tracking, user authentication, CSV export, autocorrect engine, cost-per-use analytics, rol[...]
201| 
202| **Original vision:** "Create a license manager that tracks licenses in Windows apps and monitors usage to determine if the company is getting value from employee-held licenses." Mission accomplis[...]
203| 
204| ---
205| 
206| ## Credits
207| 
208| **Jack Doyle** ([@j9ck](https://github.com/j9ck)) - Application development, GUI design, database integration, monitoring system, autocorrect engine, user management, documentation
209| 
210| **Jose Tovar** ([@josedtovar](https://github.com/josedtovar)) - Login page design
211| 
212| ---
213| 
214| ## Future Enhancements
215| 
216| - Password hashing (bcrypt) instead of plaintext, don't complain, I know!
217| - Email notifications for expiring licenses
218| - Dashboard charts and visualizations
219| - License renewal reminders
220| - Advanced reporting (most/least used software)
221| - Budget forecasting tools
222| 
223| ---
224| 
225| ## Known Issues
226| 
227| - Process detection is case-sensitive on Mac
228| - Requires internet for database connection
229| - GUI refresh is manual (no auto-refresh yet)
230| 
231| ---
232| 
233| **Built with coffee and late nights**  
234| Software Engineering 401 • Fall 2025  
235| www.jgcks.com
236| 