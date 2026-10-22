# Blood Donor Information System
## Java Swing + JDBC + Oracle Desktop Application

---

## Project Structure

```
BloodDonorSystem/
├── src/
│   ├── model/
│   │   ├── BaseUser.java          ← Abstract class (Abstraction/Inheritance)
│   │   ├── User.java              ← Extends BaseUser (Polymorphism)
│   │   └── Donor.java             ← Encapsulated model (Encapsulation)
│   ├── dao/
│   │   ├── DonorDAO.java          ← Interface (Abstraction)
│   │   ├── DonorDAOImpl.java      ← JDBC impl (DAO Pattern)
│   │   ├── UserDAO.java           ← Interface
│   │   └── UserDAOImpl.java       ← JDBC impl
│   ├── service/
│   │   ├── DonorService.java      ← Business logic + validation
│   │   └── AuthenticationService.java
│   ├── database/
│   │   └── DatabaseConnection.java ← Singleton JDBC connection
│   ├── gui/
│   │   ├── LoginFrame.java        ← Login screen
│   │   ├── DashboardFrame.java    ← Main dashboard (sidebar + CardLayout)
│   │   ├── DonorRegistrationFrame.java ← Register new donor
│   │   ├── DonorListFrame.java    ← View all donors (JTable)
│   │   ├── SearchDonorFrame.java  ← Multi-criteria search
│   │   ├── DonorUpdateFrame.java  ← Edit donor
│   │   └── DonorDetailsFrame.java ← View full details (dialog)
│   ├── exception/
│   │   ├── ValidationException.java ← Custom exception
│   │   └── DatabaseException.java   ← Custom exception
│   └── util/
│       ├── ValidationUtil.java    ← All input validation
│       └── ThemeManager.java      ← Light/Dark theme manager
├── database/
│   ├── oracle_schema.sql          ← Run this in Oracle first
│   └── db.properties              ← Edit DB credentials here
├── lib/
│   └── ojdbc11.jar                ← Place Oracle JDBC driver here
├── build.bat                      ← Compile + package JAR
├── run.bat                        ← Run the application
└── README.md
```

---

## Prerequisites

| Requirement | Version |
|-------------|---------|
| JDK         | 17+     |
| Oracle DB   | XE 21c / 19c / 11g |
| ojdbc JAR   | ojdbc11.jar (or ojdbc8.jar) |

---

## Setup Instructions

### Step 1 – Oracle Database

1. Open SQL*Plus or SQL Developer.
2. Connect as a DBA user (e.g., `SYSDBA`).
3. Run the schema script:
   ```
   @database/oracle_schema.sql
   ```
   This creates:
   - `USERS` table with default admin user (`admin` / `admin123`)
   - `DONORS` table with 5 sample records

### Step 2 – Configure Database Connection

Edit `database/db.properties`:
```properties
db.driver=oracle.jdbc.OracleDriver
db.url=jdbc:oracle:thin:@localhost:1521:XE
db.username=donor_admin
db.password=donor_admin
```

Adjust `db.url` for your Oracle instance:
- **SID format:** `jdbc:oracle:thin:@localhost:1521:XE`
- **Service name format:** `jdbc:oracle:thin:@//localhost:1521/XEPDB1`

### Step 3 – Add Oracle JDBC Driver

Download `ojdbc11.jar` from:
> https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html

Place it in the `lib/` folder:
```
BloodDonorSystem/lib/ojdbc11.jar
```

### Step 4 – Build

Double-click `build.bat`  **or** run from Command Prompt:
```cmd
cd BloodDonorSystem
build.bat
```

This produces `BloodDonorSystem.jar` in the project root.

### Step 5 – Run

Double-click `run.bat`  **or**:
```cmd
java -cp BloodDonorSystem.jar;lib\ojdbc11.jar gui.LoginFrame
```

---

## Default Login

| Username      | Password      |
|---------------|---------------|
| `admin`       | `admin123`    |
| `donor_admin` | `donor_admin` |

---

## Features

| Feature              | Description |
|----------------------|-------------|
| **Login**            | Credential validation, Show/Hide password, Enter key support |
| **Dashboard**        | Stats cards (total, available donors), recent donors list |
| **Light/Dark Mode**  | Toggle button in top bar, affects all components |
| **Register Donor**   | Full form with validation, City required indicator |
| **View Donors**      | JTable with alternating rows, status colour coding, double-click details |
| **Search Donors**    | 6 optional criteria (ID, Name, Blood Group, City, Phone, Status) |
| **Update Donor**     | Pre-populated form, full validation, JDBC UPDATE |
| **Delete Donor**     | Confirmation dialog, JDBC DELETE |
| **View Details**     | Modal dialog with all donor fields |

---

## OOP Concepts Demonstrated

| Concept         | Where |
|-----------------|-------|
| **Encapsulation** | `Donor`, `BaseUser` – private fields + getters/setters |
| **Inheritance**   | `BaseUser → User` |
| **Polymorphism**  | `BaseUser.getRole()` overridden in `User` |
| **Abstraction**   | `DonorDAO` interface → `DonorDAOImpl`; `UserDAO` → `UserDAOImpl` |
| **DAO Pattern**   | GUI → Service → DAO → JDBC → Oracle |

---

## JDBC Operations

| Operation | Where |
|-----------|-------|
| INSERT    | `DonorDAOImpl.addDonor()` |
| SELECT    | `DonorDAOImpl.getAllDonors()`, `searchDonors()`, `getDonorById()` |
| UPDATE    | `DonorDAOImpl.updateDonor()` |
| DELETE    | `DonorDAOImpl.deleteDonor()` |

All operations use `PreparedStatement` to prevent SQL injection.

---

## Input Validation

| Field           | Rule |
|-----------------|------|
| Donor ID        | Required, max 20 chars |
| Donor Name      | Required, letters/spaces/hyphens only |
| Age             | Numeric, 18–65 |
| Blood Group     | Combo box selection |
| Phone           | 7–15 digits |
| Email           | Optional, basic format check |
| City            | **Required** (NOT NULL in DB) |
| Last Donation   | Optional, yyyy-MM-dd, not future date |
| Status          | Combo box selection |

---

## Exception Handling

- `ValidationException` – field-level input errors
- `DatabaseException` – JDBC/connection errors
- `SecurityException` – invalid login credentials
- All exceptions display user-friendly `JOptionPane` messages
