package repository;

import app.DatabaseManager;
import model.Book;
import model.BookCategory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {

    public Book findById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";
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

    public Book findByIsbn(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            var rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> search(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE UPPER(title) LIKE UPPER(?) OR UPPER(author) LIKE UPPER(?) OR UPPER(isbn) LIKE UPPER(?) OR UPPER(publisher) LIKE UPPER(?) ORDER BY title";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            var rs = ps.executeQuery();
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> findByCategory(BookCategory category) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE category = ? ORDER BY title";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.name());
            var rs = ps.executeQuery();
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> findAvailable() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE available_copies > 0 ORDER BY title";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) books.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public int insert(Book book) {
        String sql = "INSERT INTO books (isbn, title, author, category, publisher, edition, total_copies, available_copies, location, added_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getCategory().name());
            ps.setString(5, book.getPublisher());
            ps.setString(6, book.getEdition());
            ps.setInt(7, book.getTotalCopies());
            ps.setInt(8, book.getAvailableCopies());
            ps.setString(9, book.getLocation());
            ps.setInt(10, book.getAddedByUserId());
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean update(Book book) {
        String sql = "UPDATE books SET isbn=?, title=?, author=?, category=?, publisher=?, edition=?, total_copies=?, available_copies=?, location=? WHERE id=?";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getCategory().name());
            ps.setString(5, book.getPublisher());
            ps.setString(6, book.getEdition());
            ps.setInt(7, book.getTotalCopies());
            ps.setInt(8, book.getAvailableCopies());
            ps.setString(9, book.getLocation());
            ps.setInt(10, book.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAvailability(int bookId, int delta) {
        String sql = "UPDATE books SET available_copies = available_copies + ? WHERE id = ? AND (available_copies + ?) >= 0";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, bookId);
            ps.setInt(3, delta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM books";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countAvailable() {
        String sql = "SELECT COALESCE(SUM(available_copies), 0) FROM books";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setId(rs.getInt("id"));
        b.setIsbn(rs.getString("isbn"));
        b.setTitle(rs.getString("title"));
        b.setAuthor(rs.getString("author"));
        b.setCategory(BookCategory.valueOf(rs.getString("category")));
        b.setPublisher(rs.getString("publisher"));
        b.setEdition(rs.getString("edition"));
        b.setTotalCopies(rs.getInt("total_copies"));
        b.setAvailableCopies(rs.getInt("available_copies"));
        b.setLocation(rs.getString("location"));
        b.setAddedByUserId(rs.getInt("added_by"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) b.setCreatedAt(ts.toLocalDateTime());
        return b;
    }
}
