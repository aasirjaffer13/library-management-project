package service;

import app.DatabaseManager;
import model.*;
import repository.LoanRepository;
import repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class LoanService {
    private final LoanRepository loanRepository;
    private final BookService bookService;
    private final MemberService memberService;
    private final FineService fineService;
    private final UserRepository userRepository;

    public LoanService() {
        this.loanRepository = new LoanRepository();
        this.bookService = new BookService();
        this.memberService = new MemberService();
        this.fineService = new FineService();
        this.userRepository = new UserRepository();
    }

    private String checkStaffAuth(int userId) {
        User user = userRepository.findById(userId);
        if (user == null) return "User not found";
        if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.LIBRARIAN && user.getRole() != UserRole.CLERK) {
            return "Permission denied: requires Administrator, Librarian, or Clerk role";
        }
        return null;
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public List<Loan> getActiveLoansByMember(int memberId) {
        return loanRepository.findActiveByMemberId(memberId);
    }

    public List<Loan> getOverdueLoans() {
        return loanRepository.findOverdue();
    }

    public List<Loan> getLoansByMember(int memberId) {
        return loanRepository.findByMemberId(memberId);
    }

    public String issueBook(int bookId, int memberId, int issuedByUserId) {
        String authErr = checkStaffAuth(issuedByUserId);
        if (authErr != null) return authErr;

        if (!bookService.isBookAvailable(bookId)) {
            return "Book is not available for issue";
        }

        Member member = memberService.getMember(memberId);
        if (member == null || !member.isActive()) {
            return "Invalid or inactive member";
        }

        int maxBooks = getMaxBooksPerMember();
        List<Loan> activeLoans = loanRepository.findActiveByMemberId(memberId);
        if (activeLoans.size() >= maxBooks) {
            return "Member has reached the maximum book limit (" + maxBooks + ")";
        }

        List<Fine> unpaidFines = fineService.getUnpaidFines(memberId);
        if (!unpaidFines.isEmpty()) {
            return "Member has unpaid fines. Please clear fines first.";
        }

        long loanPeriod = getLoanPeriodDays();
        LocalDateTime issueDate = LocalDateTime.now();
        LocalDateTime dueDate = issueDate.plusDays(loanPeriod);

        Loan loan = new Loan(bookId, memberId, issuedByUserId, issueDate, dueDate);
        int loanId = loanRepository.insert(loan);

        if (loanId > 0) {
            bookService.decrementAvailability(bookId);
            return null;
        }
        return "Failed to issue book";
    }

    public String returnBook(int loanId, int returnedToUserId) {
        String authErr = checkStaffAuth(returnedToUserId);
        if (authErr != null) return authErr;

        Loan loan = loanRepository.findById(loanId);
        if (loan == null) return "Loan not found";
        if (loan.getStatus() != LoanStatus.ACTIVE) return "Loan is not active";

        boolean returned = loanRepository.returnBook(loanId, returnedToUserId);
        if (!returned) return "Failed to process return";

        bookService.incrementAvailability(loan.getBookId());

        if (loan.getDueDate().isBefore(LocalDateTime.now())) {
            long daysOverdue = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDateTime.now());
            double fineAmount = daysOverdue * getFinePerDay();
            fineService.createFine(loanId, loan.getMemberId(), fineAmount,
                "Overdue by " + daysOverdue + " days");
        }

        return null;
    }

    public int getActiveLoanCount() {
        return loanRepository.countActive();
    }

    public int getOverdueCount() {
        return loanRepository.countOverdue();
    }

    private long getLoanPeriodDays() {
        String val = DatabaseManager.getInstance().getConfig("loan_period_days");
        try {
            return Long.parseLong(val);
        } catch (Exception e) {
            return 14;
        }
    }

    private double getFinePerDay() {
        String val = DatabaseManager.getInstance().getConfig("fine_per_day");
        try {
            return Double.parseDouble(val);
        } catch (Exception e) {
            return 2.0;
        }
    }

    private int getMaxBooksPerMember() {
        String val = DatabaseManager.getInstance().getConfig("max_books_per_member");
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return 5;
        }
    }
}
