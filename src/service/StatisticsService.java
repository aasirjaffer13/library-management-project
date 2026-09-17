package service;

import model.*;
import repository.UserRepository;
import java.util.HashMap;
import java.util.Map;

public class StatisticsService {
    private final BookService bookService;
    private final MemberService memberService;
    private final LoanService loanService;
    private final FineService fineService;
    private final ReservationService reservationService;
    private final UserRepository userRepository;

    public StatisticsService() {
        this.bookService = new BookService();
        this.memberService = new MemberService();
        this.loanService = new LoanService();
        this.fineService = new FineService();
        this.reservationService = new ReservationService();
        this.userRepository = new UserRepository();
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBooks", bookService.getTotalBooks());
        stats.put("availableCopies", bookService.getAvailableCopies());
        stats.put("totalMembers", memberService.getActiveMemberCount());
        stats.put("activeLoans", loanService.getActiveLoanCount());
        stats.put("overdueLoans", loanService.getOverdueCount());
        stats.put("pendingReservations", reservationService.getPendingCount());
        stats.put("totalUnpaidFines", fineService.getTotalUnpaid());
        stats.put("librarians", userRepository.countByRole(UserRole.LIBRARIAN));
        stats.put("clerks", userRepository.countByRole(UserRole.CLERK));
        return stats;
    }
}
