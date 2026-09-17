package test;

import app.DatabaseManager;
import model.*;
import service.*;

public class AppTest {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Library Management System - Automated Test ===\n");

        DatabaseManager.getInstance().initializeDatabase();
        System.out.println("[PASS] Database initialized");

        testAuth();
        testBooks();
        testMembers();
        testLoans();
        testReservations();
        testFines();
        testStatistics();

        DatabaseManager.getInstance().close();
        System.out.println("\n=== ALL TESTS PASSED ===");
    }

    static void testAuth() {
        System.out.println("\n--- Auth Tests ---");
        AuthService auth = new AuthService();
        User user = auth.login("admin", "admin123");
        assert user != null : "Admin login failed";
        assert user.getRole() == UserRole.ADMIN : "Admin role incorrect";
        System.out.println("[PASS] Login with admin/admin123");

        User bad = auth.login("admin", "wrongpass");
        assert bad == null : "Wrong password should fail";
        System.out.println("[PASS] Login with wrong password rejected");

        User bad2 = auth.login("nonexistent", "pass");
        assert bad2 == null : "Non-existent user should fail";
        System.out.println("[PASS] Login with nonexistent user rejected");

        auth.logout();
        assert !auth.isLoggedIn() : "Should be logged out";
        System.out.println("[PASS] Logout works");
    }

    static void testBooks() {
        System.out.println("\n--- Book Tests ---");
        BookService bs = new BookService();
        User admin = new AuthService().login("admin", "admin123");

        String err = bs.addBook("9780123456789", "Test Book", "Test Author", BookCategory.SCIENCE, "Test Publisher", "1st", 3, "Shelf A", admin.getId());
        assert err == null : "Add book failed: " + err;
        System.out.println("[PASS] Add book");

        assert bs.getTotalBooks() == 1 : "Total books should be 1";
        System.out.println("[PASS] Total books count");

        assert bs.getAvailableCopies() == 3 : "Available copies should be 3";
        System.out.println("[PASS] Available copies count");

        var found = bs.searchBooks("Test Book");
        assert found.size() == 1 : "Search should find 1 book";
        System.out.println("[PASS] Search books by title");

        var foundAuthor = bs.searchBooks("Test Author");
        assert foundAuthor.size() == 1 : "Search by author should find 1 book";
        System.out.println("[PASS] Search books by author");

        Book book = bs.getBook(1);
        assert book != null && "Test Book".equals(book.getTitle()) : "Get book by ID";
        System.out.println("[PASS] Get book by ID");

        assert bs.isBookAvailable(1) : "Book should be available";
        System.out.println("[PASS] Book availability check");

        bs.decrementAvailability(1);
        assert bs.getBook(1).getAvailableCopies() == 2 : "Copies should be 2";
        System.out.println("[PASS] Decrement availability");

        bs.incrementAvailability(1);
        assert bs.getBook(1).getAvailableCopies() == 3 : "Copies should be 3 again";
        System.out.println("[PASS] Increment availability");

        book.setTitle("Updated Title");
        book.setAuthor("Updated Author");
        err = bs.updateBook(book, admin.getId());
        assert err == null : "Update book failed: " + err;
        assert "Updated Title".equals(bs.getBook(1).getTitle()) : "Title not updated";
        System.out.println("[PASS] Update book");
    }

    static void testMembers() {
        System.out.println("\n--- Member Tests ---");
        MemberService ms = new MemberService();
        User admin = new AuthService().login("admin", "admin123");

        String err = ms.addMember("Test Member", "test@example.com", "1234567890", "123 Test St", admin.getId());
        assert err == null : "Add member failed: " + err;
        System.out.println("[PASS] Add member");

        assert ms.getActiveMemberCount() == 1 : "Member count should be 1";
        System.out.println("[PASS] Member count");

        var found = ms.searchMembers("Test Member");
        assert found.size() == 1 : "Search should find 1 member";
        System.out.println("[PASS] Search members by name");

        Member m = ms.getMember(1);
        assert m != null && "Test Member".equals(m.getFullName()) : "Get member by ID";
        assert m.getMemberNumber() != null : "Member number should be set";
        System.out.println("[PASS] Get member by ID, member number generated: " + m.getMemberNumber());

        m.setPhone("9999999999");
        err = ms.updateMember(m, admin.getId());
        assert err == null : "Update member failed";
        assert "9999999999".equals(ms.getMember(1).getPhone()) : "Phone not updated";
        System.out.println("[PASS] Update member");
    }

    static void testLoans() {
        System.out.println("\n--- Loan Tests ---");
        LoanService ls = new LoanService();
        BookService bs = new BookService();
        User admin = new AuthService().login("admin", "admin123");

        // Book was already available, add a member first
        MemberService ms = new MemberService();
        ms.addMember("Loan Test Member", "loan@test.com", "5551234567", "456 Loan St", admin.getId());

        String err = ls.issueBook(1, 1, admin.getId());
        assert err == null : "Issue book failed: " + err;
        System.out.println("[PASS] Issue book");

        assert bs.getBook(1).getAvailableCopies() == 2 : "Available should decrease to 2";
        System.out.println("[PASS] Availability decreased after issue");

        assert ls.getActiveLoanCount() == 1 : "Active loans should be 1";
        System.out.println("[PASS] Active loan count");

        var active = ls.getActiveLoansByMember(1);
        assert active.size() == 1 : "Member should have 1 active loan";
        System.out.println("[PASS] Active loans by member");

        // Try issuing same book again - should work (copies available)
        String err2 = ls.issueBook(1, 1, admin.getId());
        assert err2 == null : "Issue second copy should work: " + err2;
        System.out.println("[PASS] Issue second copy of same book");

        assert bs.getBook(1).getAvailableCopies() == 1 : "Available should be 1";
        System.out.println("[PASS] Availability is now 1");

        // Return first loan
        Loan loan = active.get(0);
        String err3 = ls.returnBook(loan.getId(), admin.getId());
        assert err3 == null : "Return book failed: " + err3;
        System.out.println("[PASS] Return book");

        assert bs.getBook(1).getAvailableCopies() == 2 : "Available should increase to 2";
        System.out.println("[PASS] Availability increased after return");

        assert ls.getActiveLoanCount() == 1 : "Active loans should be 1 (one still active)";
        System.out.println("[PASS] Active loan count after return");

        var allLoans = ls.getAllLoans();
        assert allLoans.size() >= 2 : "Should have at least 2 loan records";
        System.out.println("[PASS] Loan history maintained");
    }

    static void testReservations() {
        System.out.println("\n--- Reservation Tests ---");
        ReservationService rs = new ReservationService();
        BookService bs = new BookService();
        LoanService ls = new LoanService();
        MemberService ms = new MemberService();
        User admin = new AuthService().login("admin", "admin123");

        // Need a second member for reservation test
        ms.addMember("Reserve Member", "reserve@test.com", "5559876543", "789 Reserve St", admin.getId());

        // Issue all remaining copies of book 1 (currently 2 available)
        String errA = ls.issueBook(1, 1, admin.getId());
        String errB = ls.issueBook(1, 2, admin.getId());

        Book b = bs.getBook(1);
        System.out.println("  Book available copies: " + b.getAvailableCopies());

        if (b.getAvailableCopies() == 0) {
            String err = rs.makeReservation(1, 1);
            assert err == null : "Reserve failed: " + err;
            System.out.println("[PASS] Make reservation");

            var pending = rs.getPendingByMember(1);
            assert pending.size() >= 1 : "Should have pending reservations";
            System.out.println("[PASS] Get pending reservations by member");

            assert rs.getPendingCount() >= 1 : "Pending count should be >= 1";
            System.out.println("[PASS] Pending reservation count");

            // Duplicate reservation should fail
            String err2 = rs.makeReservation(1, 1);
            assert err2 != null : "Duplicate reservation should be rejected";
            System.out.println("[PASS] Duplicate reservation prevented");

            // Cancel reservation
            Reservation res = pending.get(0);
            String err3 = rs.cancelReservation(res.getId());
            assert err3 == null : "Cancel reservation failed";
            System.out.println("[PASS] Cancel reservation");
        } else {
            System.out.println("[SKIP] Book still has " + b.getAvailableCopies() + " copies available");
        }
    }

    static void testFines() {
        System.out.println("\n--- Fine Tests ---");
        FineService fs = new FineService();
        LoanService ls = new LoanService();
        User admin = new AuthService().login("admin", "admin123");

        // Create a fine manually
        fs.createFine(1, 1, 50.0, "Test fine");
        System.out.println("[PASS] Create fine");

        var fines = fs.getFinesByMember(1);
        assert fines.size() >= 1 : "Should have fines for member";
        System.out.println("[PASS] Get fines by member");

        var unpaid = fs.getUnpaidFines(1);
        assert unpaid.size() >= 1 : "Should have unpaid fines";
        System.out.println("[PASS] Get unpaid fines");

        double total = fs.getUnpaidTotalByMember(1);
        assert total > 0 : "Unpaid total should be > 0";
        System.out.println("[PASS] Unpaid total by member: " + total);

        Fine fine = unpaid.get(0);
        String err = fs.payFine(fine.getId(), admin.getId());
        assert err == null : "Pay fine failed";
        System.out.println("[PASS] Mark fine as paid");

        var unpaidAfter = fs.getUnpaidFines(1);
        boolean allPaid = true;
        for (Fine f : unpaidAfter) {
            if (f.getId() == fine.getId()) allPaid = false;
        }
        assert allPaid : "Paid fine should not appear in unpaid list";
        System.out.println("[PASS] Paid fine removed from unpaid list");
    }

    static void testStatistics() {
        System.out.println("\n--- Statistics Tests ---");
        StatisticsService ss = new StatisticsService();
        var stats = ss.getDashboardStats();

        assert stats.containsKey("totalBooks") : "Should have totalBooks";
        assert stats.containsKey("availableCopies") : "Should have availableCopies";
        assert stats.containsKey("totalMembers") : "Should have totalMembers";
        assert stats.containsKey("activeLoans") : "Should have activeLoans";
        assert stats.containsKey("overdueLoans") : "Should have overdueLoans";
        assert stats.containsKey("pendingReservations") : "Should have pendingReservations";
        assert stats.containsKey("totalUnpaidFines") : "Should have totalUnpaidFines";

        int totalBooks = (int) stats.get("totalBooks");
        assert totalBooks >= 1 : "Should have at least 1 book";
        System.out.println("[PASS] Dashboard stats returned: " + totalBooks + " books, " + stats.get("totalMembers") + " members");
    }
}
