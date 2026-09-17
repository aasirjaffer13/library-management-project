package repository;

import app.DatabaseManager;
import model.Member;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberRepository {

    public Member findById(int id) {
        String sql = "SELECT * FROM members WHERE id = ?";
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

    public Member findByMemberNumber(String memberNumber) {
        String sql = "SELECT * FROM members WHERE member_number = ?";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, memberNumber);
            var rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Member> findAll() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members ORDER BY full_name";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) members.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public List<Member> search(String keyword) {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE UPPER(full_name) LIKE UPPER(?) OR UPPER(member_number) LIKE UPPER(?) OR UPPER(email) LIKE UPPER(?) OR UPPER(phone) LIKE UPPER(?) ORDER BY full_name";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            var rs = ps.executeQuery();
            while (rs.next()) members.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public int insert(Member member) {
        String sql = "INSERT INTO members (member_number, full_name, email, phone, address) VALUES (?, ?, ?, ?, ?)";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, member.getMemberNumber());
            ps.setString(2, member.getFullName());
            ps.setString(3, member.getEmail());
            ps.setString(4, member.getPhone());
            ps.setString(5, member.getAddress());
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean update(Member member) {
        String sql = "UPDATE members SET full_name=?, email=?, phone=?, address=?, active=? WHERE id=?";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.getFullName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setString(4, member.getAddress());
            ps.setBoolean(5, member.isActive());
            ps.setInt(6, member.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deactivate(int id) {
        String sql = "UPDATE members SET active = FALSE WHERE id = ?";
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
        String sql = "SELECT COUNT(*) FROM members";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countActive() {
        String sql = "SELECT COUNT(*) FROM members WHERE active = TRUE";
        try (var conn = DatabaseManager.getInstance().getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Member mapRow(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.setId(rs.getInt("id"));
        m.setMemberNumber(rs.getString("member_number"));
        m.setFullName(rs.getString("full_name"));
        m.setEmail(rs.getString("email"));
        m.setPhone(rs.getString("phone"));
        m.setAddress(rs.getString("address"));
        Date md = rs.getDate("membership_date");
        if (md != null) m.setMembershipDate(md.toLocalDate());
        m.setActive(rs.getBoolean("active"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) m.setCreatedAt(ts.toLocalDateTime());
        return m;
    }
}
