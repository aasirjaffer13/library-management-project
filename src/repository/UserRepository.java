package repository;

import app.DatabaseManager;
import model.User;
import model.UserRole;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            var rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY full_name";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) users.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<User> findByRole(UserRole role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? AND active = TRUE ORDER BY full_name";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.name());
            var rs = ps.executeQuery();
            while (rs.next()) users.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public int insert(User user) {
        String sql = "INSERT INTO users (username, password_hash, full_name, role, email, phone, address) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getAddress());
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean update(User user) {
        String sql = "UPDATE users SET full_name=?, email=?, phone=?, address=?, active=? WHERE id=?";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getAddress());
            ps.setBoolean(5, user.isActive());
            ps.setInt(6, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(int userId, String newHash) {
        String sql = "UPDATE users SET password_hash=? WHERE id=?";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deactivate(int id) {
        String sql = "UPDATE users SET active = FALSE WHERE id = ?";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countByRole(UserRole role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ? AND active = TRUE";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.name());
            var rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        u.setRole(UserRole.valueOf(rs.getString("role")));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setAddress(rs.getString("address"));
        u.setActive(rs.getBoolean("active"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) u.setCreatedAt(ts.toLocalDateTime());
        return u;
    }
}
