package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import util.PasswordUtil;
import model.UserRole;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:./library_db;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    private static DatabaseManager instance;

    private DatabaseManager() {}

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public void initializeDatabase() throws SQLException {
        Connection conn = getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password_hash VARCHAR(255) NOT NULL,
                    full_name VARCHAR(100) NOT NULL,
                    role VARCHAR(20) NOT NULL,
                    email VARCHAR(100),
                    phone VARCHAR(20),
                    address VARCHAR(200),
                    active BOOLEAN DEFAULT TRUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS books (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    isbn VARCHAR(20) UNIQUE,
                    title VARCHAR(200) NOT NULL,
                    author VARCHAR(100) NOT NULL,
                    category VARCHAR(50) DEFAULT 'OTHER',
                    publisher VARCHAR(100),
                    edition VARCHAR(50),
                    total_copies INT DEFAULT 1,
                    available_copies INT DEFAULT 1,
                    location VARCHAR(50),
                    added_by INT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (added_by) REFERENCES users(id)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS members (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    member_number VARCHAR(20) UNIQUE NOT NULL,
                    full_name VARCHAR(100) NOT NULL,
                    email VARCHAR(100),
                    phone VARCHAR(20),
                    address VARCHAR(200),
                    membership_date DATE DEFAULT CURRENT_DATE,
                    active BOOLEAN DEFAULT TRUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS loans (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    book_id INT NOT NULL,
                    member_id INT NOT NULL,
                    issued_by INT NOT NULL,
                    issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    due_date TIMESTAMP NOT NULL,
                    return_date TIMESTAMP,
                    returned_to INT,
                    status VARCHAR(20) DEFAULT 'ACTIVE',
                    FOREIGN KEY (book_id) REFERENCES books(id),
                    FOREIGN KEY (member_id) REFERENCES members(id),
                    FOREIGN KEY (issued_by) REFERENCES users(id)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS reservations (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    book_id INT NOT NULL,
                    member_id INT NOT NULL,
                    reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    expiry_date TIMESTAMP NOT NULL,
                    status VARCHAR(20) DEFAULT 'PENDING',
                    FOREIGN KEY (book_id) REFERENCES books(id),
                    FOREIGN KEY (member_id) REFERENCES members(id)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS fines (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    loan_id INT NOT NULL,
                    member_id INT NOT NULL,
                    amount DOUBLE NOT NULL,
                    reason VARCHAR(100),
                    paid BOOLEAN DEFAULT FALSE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (loan_id) REFERENCES loans(id),
                    FOREIGN KEY (member_id) REFERENCES members(id)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS library_config (
                    config_key VARCHAR(50) PRIMARY KEY,
                    config_value VARCHAR(200) NOT NULL
                )
            """);

            createDefaultAdmin(conn);
            createDefaultConfig(conn);
        }
    }

    private void createDefaultAdmin(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE role = 'ADMIN'")) {
            rs.next();
            if (rs.getInt(1) == 0) {
                String hash = PasswordUtil.hashPassword("admin123");
                var ps = conn.prepareStatement(
                    "INSERT INTO users (username, password_hash, full_name, role) VALUES (?, ?, ?, ?)"
                );
                ps.setString(1, "admin");
                ps.setString(2, hash);
                ps.setString(3, "System Administrator");
                ps.setString(4, UserRole.ADMIN.name());
                ps.executeUpdate();
            }
        }
    }

    private void createDefaultConfig(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT COUNT(*) FROM library_config")) {
            rs.next();
            if (rs.getInt(1) == 0) {
                var ps = conn.prepareStatement(
                    "INSERT INTO library_config (config_key, config_value) VALUES (?, ?)"
                );
                ps.setString(1, "fine_per_day");
                ps.setString(2, "2.0");
                ps.executeUpdate();

                ps.setString(1, "loan_period_days");
                ps.setString(2, "14");
                ps.executeUpdate();

                ps.setString(1, "reservation_expiry_days");
                ps.setString(2, "7");
                ps.executeUpdate();

                ps.setString(1, "max_books_per_member");
                ps.setString(2, "5");
                ps.executeUpdate();

                ps.setString(1, "library_name");
                ps.setString(2, "College Library");
                ps.executeUpdate();
            }
        }
    }

    public String getConfig(String key) {
        try (var stmt = getConnection().prepareStatement(
                "SELECT config_value FROM library_config WHERE config_key = ?")) {
            stmt.setString(1, key);
            var rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("config_value");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setConfig(String key, String value) {
        try (var stmt = getConnection().prepareStatement(
                "MERGE INTO library_config (config_key, config_value) KEY (config_key) VALUES (?, ?)")) {
            stmt.setString(1, key);
            stmt.setString(2, value);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        // Each connection is now managed by try-with-resources in callers
    }
}
