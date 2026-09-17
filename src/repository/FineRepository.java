package repository;

import app.DatabaseManager;
import model.Fine;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FineRepository {

    public List<Fine> findByMemberId(int memberId) {
        List<Fine> fines = new ArrayList<>();
        String sql = """
            SELECT f.*, m.full_name AS member_name
            FROM fines f
            JOIN members m ON f.member_id = m.id
            WHERE f.member_id = ?
            ORDER BY f.created_at DESC
        """;
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            var rs = ps.executeQuery();
            while (rs.next()) fines.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fines;
    }

    public List<Fine> findUnpaidByMemberId(int memberId) {
        List<Fine> fines = new ArrayList<>();
        String sql = """
            SELECT f.*, m.full_name AS member_name
            FROM fines f
            JOIN members m ON f.member_id = m.id
            WHERE f.member_id = ? AND f.paid = FALSE
            ORDER BY f.created_at DESC
        """;
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            var rs = ps.executeQuery();
            while (rs.next()) fines.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fines;
    }

    public int insert(Fine fine) {
        String sql = "INSERT INTO fines (loan_id, member_id, amount, reason) VALUES (?, ?, ?, ?)";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, fine.getLoanId());
            ps.setInt(2, fine.getMemberId());
            ps.setDouble(3, fine.getAmount());
            ps.setString(4, fine.getReason());
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean markPaid(int fineId) {
        String sql = "UPDATE fines SET paid = TRUE WHERE id = ?";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fineId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double totalUnpaidByMember(int memberId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM fines WHERE member_id = ? AND paid = FALSE";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            var rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double totalUnpaid() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM fines WHERE paid = FALSE";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Fine> findAll() {
        List<Fine> fines = new ArrayList<>();
        String sql = """
            SELECT f.*, m.full_name AS member_name
            FROM fines f
            JOIN members m ON f.member_id = m.id
            ORDER BY f.created_at DESC
        """;
        try (var conn = DatabaseManager.getInstance().getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) fines.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fines;
    }

    private Fine mapRow(ResultSet rs) throws SQLException {
        Fine f = new Fine();
        f.setId(rs.getInt("id"));
        f.setLoanId(rs.getInt("loan_id"));
        f.setMemberId(rs.getInt("member_id"));
        f.setAmount(rs.getDouble("amount"));
        f.setReason(rs.getString("reason"));
        f.setPaid(rs.getBoolean("paid"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) f.setCreatedAt(ts.toLocalDateTime());
        f.setMemberName(rs.getString("member_name"));
        return f;
    }
}
