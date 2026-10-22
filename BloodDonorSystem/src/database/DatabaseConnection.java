package database;

import exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * DatabaseConnection – Singleton JDBC connection manager.
 *
 * Reads configuration from db.properties (classpath or working directory).
 * Auto-creates the DONORS table if it does not already exist (SQLite support).
 *
 * Demonstrates: Singleton Pattern, JDBC, exception handling.
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Properties props;

    // ── Singleton constructor ─────────────────────────────────────────────────

    private DatabaseConnection() throws DatabaseException {
        loadProperties();
        initializeDatabase();
    }

    /** Returns the single shared instance (lazy singleton). */
    public static synchronized DatabaseConnection getInstance() throws DatabaseException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // ── Property loading ──────────────────────────────────────────────────────

    private void loadProperties() throws DatabaseException {
        props = new Properties();

        // Try classpath first (works from JAR)
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
                return;
            }
        } catch (IOException e) {
            // Fall through
        }

        // Fallback: working-directory relative path
        try (InputStream in = new java.io.FileInputStream("database/db.properties")) {
            props.load(in);
        } catch (IOException e) {
            throw new DatabaseException(
                "Cannot load db.properties. Ensure the file exists at " +
                "'database/db.properties' or on the classpath.", e);
        }
    }

    // ── Connection factory ────────────────────────────────────────────────────

    /**
     * Opens and returns a new JDBC connection.
     * The caller is responsible for closing the connection.
     */
    public Connection getConnection() throws DatabaseException {
        try {
            String driver   = props.getProperty("db.driver", "").trim();
            String url      = props.getProperty("db.url",    "").trim();
            String username = props.getProperty("db.username", "").trim();
            String password = props.getProperty("db.password", "").trim();

            if (driver.isEmpty()) {
                throw new DatabaseException(
                    "db.driver is not set in db.properties.\n" +
                    "Expected: db.driver=org.sqlite.JDBC");
            }
            if (url.isEmpty()) {
                throw new DatabaseException(
                    "db.url is not set in db.properties.\n" +
                    "Expected: db.url=jdbc:sqlite:blood_donor.db");
            }

            Class.forName(driver);

            if (username.isEmpty() && password.isEmpty()) {
                return DriverManager.getConnection(url);
            }
            return DriverManager.getConnection(url, username, password);

        } catch (ClassNotFoundException e) {
            throw new DatabaseException(
                "JDBC driver class not found: '" +
                props.getProperty("db.driver", "(not set)") + "'.\n" +
                "Ensure the JDBC JAR is in the classpath.\n\n" +
                "Technical detail: " + e.getMessage(), e);

        } catch (SQLException e) {
            String url = props.getProperty("db.url", "(not set)");

            System.err.println("[DatabaseConnection] Connection failed:");
            System.err.println("  URL: " + url);
            System.err.println("  SQL State: " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            e.printStackTrace(System.err);

            throw new DatabaseException(
                "Unable to connect to the database.\n\n" +
                "URL: " + url + "\n" +
                "Technical error: " + e.getMessage(), e.getErrorCode(), e);
        }
    }

    // ── Schema initialisation ─────────────────────────────────────────────────

    /**
     * Creates all required tables if they do not already exist.
     * Seeds a default admin account on first run.
     * Demonstrates: DDL via JDBC, PreparedStatement for DML.
     */
    private void initializeDatabase() throws DatabaseException {
        String createDonors =
            "CREATE TABLE IF NOT EXISTS DONORS (" +
            "  DONOR_ID           VARCHAR(20)  PRIMARY KEY, " +
            "  DONOR_NAME         VARCHAR(100) NOT NULL, " +
            "  AGE                INTEGER      NOT NULL, " +
            "  GENDER             VARCHAR(10)  NOT NULL, " +
            "  BLOOD_GROUP        VARCHAR(5)   NOT NULL, " +
            "  PHONE              VARCHAR(15)  NOT NULL, " +
            "  EMAIL              VARCHAR(100), " +
            "  ADDRESS            VARCHAR(255), " +
            "  CITY               VARCHAR(50)  NOT NULL, " +
            "  LAST_DONATION_DATE DATE, " +
            "  STATUS             VARCHAR(20)  NOT NULL DEFAULT 'Available'" +
            ")";

        String createUsers =
            "CREATE TABLE IF NOT EXISTS USERS (" +
            "  USER_ID  INTEGER PRIMARY KEY, " +
            "  USERNAME VARCHAR(50) NOT NULL UNIQUE, " +
            "  PASSWORD VARCHAR(100) NOT NULL" +
            ")";

        // Insert default admin if no users exist
        String countUsers  = "SELECT COUNT(*) FROM USERS";
        String insertAdmin = "INSERT INTO USERS (USER_ID, USERNAME, PASSWORD) VALUES (1, 'admin', 'admin123')";

        try (Connection conn = getConnection();
             Statement  stmt = conn.createStatement()) {

            stmt.execute(createDonors);
            stmt.execute(createUsers);

            // Seed default admin account on first run
            try (ResultSet rs = stmt.executeQuery(countUsers)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute(insertAdmin);
                    System.out.println("[DatabaseConnection] Default admin created. Username: admin | Password: admin123");
                }
            }

            System.out.println("[DatabaseConnection] Database initialized successfully.");

        } catch (SQLException e) {
            throw new DatabaseException(
                "Failed to initialize database schema: " + e.getMessage(), e);
        }
    }

    /**
     * Tests the database connection without throwing to the UI.
     * @return true if a connection can be established.
     */
    public boolean testConnection() {
        try (Connection c = getConnection()) {
            return c != null && !c.isClosed();
        } catch (Exception e) {
            System.err.println("[DatabaseConnection] Test connection failed: " + e.getMessage());
            return false;
        }
    }
}
