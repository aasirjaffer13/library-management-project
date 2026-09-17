package repository;

import app.DatabaseManager;
import model.Loan;
import model.LoanStatus;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoanRepository {

    public Loan findById(int id) {
        String sql = """
            SELECT l.*, b.title AS book_title, m.full_name AS member_name, u.full_name AS issued_by_name
            FROM loans l
            JOIN books b ON l.book_id = b.id
            JOIN members m ON l.member_id = m.id
            JOIN users u ON l.issued_by = u.id
            WHERE l.id = ?
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

    public List<Loan> findAll() {
        List<Loan> loans = new ArrayList<>();
        String sql = """
            SELECT l.*, b.title AS book_title, m.full_name AS member_name, u.full_name AS issued_by_name
            FROM loans l
            JOIN books b ON l.book_id = b.id
            JOIN members m ON l.member_id = m.id
            JOIN users u ON l.issued_by = u.id
            ORDER BY l.issue_date DESC
        """;
        try (var conn = DatabaseManager.getInstance().getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) loans.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public List<Loan> findActiveByMemberId(int memberId) {
        List<Loan> loans = new ArrayList<>();
        String sql = """
            SELECT l.*, b.title AS book_title, m.full_name AS member_name, u.full_name AS issued_by_name
            FROM loans l
            JOIN books b ON l.book_id = b.id
            JOIN members m ON l.member_id = m.id
            JOIN users u ON l.issued_by = u.id
            WHERE l.member_id = ? AND l.status = 'ACTIVE'
            ORDER BY l.issue_date DESC
        """;
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            var rs = ps.executeQuery();
            while (rs.next()) loans.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public List<Loan> findActiveByBookId(int bookId) {
        List<Loan> loans = new ArrayList<>();
        String sql = """
            SELECT l.*, b.title AS book_title, m.full_name AS member_name, u.full_name AS issued_by_name
            FROM loans l
            JOIN books b ON l.book_id = b.id
            JOIN members m ON l.member_id = m.id
            JOIN users u ON l.issued_by = u.id
            WHERE l.book_id = ? AND l.status = 'ACTIVE'
        """;
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            var rs = ps.executeQuery();
            while (rs.next()) loans.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public List<Loan> findOverdue() {
        List<Loan> loans = new ArrayList<>();
        String sql = """
            SELECT l.*, b.title AS book_title, m.full_name AS member_name, u.full_name AS issued_by_name
            FROM loans l
            JOIN books b ON l.book_id = b.id
            JOIN members m ON l.member_id = m.id
            JOIN users u ON l.issued_by = u.id
            WHERE l.status = 'ACTIVE' AND l.due_date < CURRENT_TIMESTAMP
            ORDER BY l.due_date ASC
        """;
        try (var conn = DatabaseManager.getInstance().getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) loans.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public List<Loan> findByMemberId(int memberId) {
        List<Loan> loans = new ArrayList<>();
        String sql = """
            SELECT l.*, b.title AS book_title, m.full_name AS member_name, u.full_name AS issued_by_name
            FROM loans l
            JOIN books b ON l.book_id = b.id
            JOIN members m ON l.member_id = m.id
            JOIN users u ON l.issued_by = u.id
            WHERE l.member_id = ?
            ORDER BY l.issue_date DESC
        """;
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            var rs = ps.executeQuery();
            while (rs.next()) loans.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    public int insert(Loan loan) {
        String sql = "INSERT INTO loans (book_id, member_id, issued_by, issue_date, due_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, loan.getBookId());
            ps.setInt(2, loan.getMemberId());
            ps.setInt(3, loan.getIssuedByUserId());
            ps.setTimestamp(4, Timestamp.valueOf(loan.getIssueDate()));
            ps.setTimestamp(5, Timestamp.valueOf(loan.getDueDate()));
            ps.setString(6, loan.getStatus().name());
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean returnBook(int loanId, int returnedToUserId) {
        String sql = "UPDATE loans SET return_date = CURRENT_TIMESTAMP, returned_to = ?, status = 'RETURNED' WHERE id = ? AND status = 'ACTIVE'";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, returnedToUserId);
            ps.setInt(2, loanId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countActive() {
        String sql = "SELECT COUNT(*) FROM loans WHERE status = 'ACTIVE'";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countOverdue() {
        String sql = "SELECT COUNT(*) FROM loans WHERE status = 'ACTIVE' AND due_date < CURRENT_TIMESTAMP";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Loan mapRow(ResultSet rs) throws SQLException {
        Loan l = new Loan();
        l.setId(rs.getInt("id"));
        l.setBookId(rs.getInt("book_id"));
        l.setMemberId(rs.getInt("member_id"));
        l.setIssuedByUserId(rs.getInt("issued_by"));
        Timestamp issue = rs.getTimestamp("issue_date");
        if (issue != null) l.setIssueDate(issue.toLocalDateTime());
        Timestamp due = rs.getTimestamp("due_date");
        if (due != null) l.setDueDate(due.toLocalDateTime());
        Timestamp ret = rs.getTimestamp("return_date");
        if (ret != null) l.setReturnDate(ret.toLocalDateTime());
        int rt = rs.getInt("returned_to");
        if (!rs.wasNull()) l.setReturnedToUserId(rt);
        l.setStatus(LoanStatus.valueOf(rs.getString("status")));
        l.setBookTitle(rs.getString("book_title"));
        l.setMemberName(rs.getString("member_name"));
        l.setIssuedByName(rs.getString("issued_by_name"));
        return l;
    }
}
