package repository;

import app.DatabaseManager;
import model.Reservation;
import model.ReservationStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {

    public Reservation findById(int id) {
        String sql = """
            SELECT r.*, b.title AS book_title, m.full_name AS member_name
            FROM reservations r
            JOIN books b ON r.book_id = b.id
            JOIN members m ON r.member_id = m.id
            WHERE r.id = ?
        """;
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

    public List<Reservation> findPendingByBookId(int bookId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = """
            SELECT r.*, b.title AS book_title, m.full_name AS member_name
            FROM reservations r
            JOIN books b ON r.book_id = b.id
            JOIN members m ON r.member_id = m.id
            WHERE r.book_id = ? AND r.status = 'PENDING'
            ORDER BY r.reservation_date ASC
        """;
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            var rs = ps.executeQuery();
            while (rs.next()) reservations.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public List<Reservation> findPendingByMemberId(int memberId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = """
            SELECT r.*, b.title AS book_title, m.full_name AS member_name
            FROM reservations r
            JOIN books b ON r.book_id = b.id
            JOIN members m ON r.member_id = m.id
            WHERE r.member_id = ? AND r.status = 'PENDING'
            ORDER BY r.reservation_date ASC
        """;
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            var rs = ps.executeQuery();
            while (rs.next()) reservations.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public List<Reservation> findAllPending() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = """
            SELECT r.*, b.title AS book_title, m.full_name AS member_name
            FROM reservations r
            JOIN books b ON r.book_id = b.id
            JOIN members m ON r.member_id = m.id
            WHERE r.status = 'PENDING'
            ORDER BY r.reservation_date ASC
        """;
        try (var conn = DatabaseManager.getInstance().getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) reservations.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public boolean hasActiveReservation(int bookId, int memberId) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE book_id = ? AND member_id = ? AND status = 'PENDING'";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setInt(2, memberId);
            var rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int insert(Reservation reservation) {
        String sql = "INSERT INTO reservations (book_id, member_id, reservation_date, expiry_date, status) VALUES (?, ?, ?, ?, ?)";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reservation.getBookId());
            ps.setInt(2, reservation.getMemberId());
            ps.setTimestamp(3, Timestamp.valueOf(reservation.getReservationDate()));
            ps.setTimestamp(4, Timestamp.valueOf(reservation.getExpiryDate()));
            ps.setString(5, reservation.getStatus().name());
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateStatus(int id, ReservationStatus status) {
        String sql = "UPDATE reservations SET status = ? WHERE id = ?";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countPending() {
        String sql = "SELECT COUNT(*) FROM reservations WHERE status = 'PENDING'";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getInt("id"));
        r.setBookId(rs.getInt("book_id"));
        r.setMemberId(rs.getInt("member_id"));
        Timestamp rd = rs.getTimestamp("reservation_date");
        if (rd != null) r.setReservationDate(rd.toLocalDateTime());
        Timestamp ed = rs.getTimestamp("expiry_date");
        if (ed != null) r.setExpiryDate(ed.toLocalDateTime());
        r.setStatus(ReservationStatus.valueOf(rs.getString("status")));
        r.setBookTitle(rs.getString("book_title"));
        r.setMemberName(rs.getString("member_name"));
        return r;
    }
}
