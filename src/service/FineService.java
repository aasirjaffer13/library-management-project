package service;

import model.Fine;
import model.User;
import model.UserRole;
import repository.FineRepository;
import repository.UserRepository;

import java.util.List;

public class FineService {
    private final FineRepository fineRepository;
    private final UserRepository userRepository;

    public FineService() {
        this.fineRepository = new FineRepository();
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

    public List<Fine> getAllFines() {
        return fineRepository.findAll();
    }

    public List<Fine> getFinesByMember(int memberId) {
        return fineRepository.findByMemberId(memberId);
    }

    public List<Fine> getUnpaidFines(int memberId) {
        return fineRepository.findUnpaidByMemberId(memberId);
    }

    public void createFine(int loanId, int memberId, double amount, String reason) {
        Fine fine = new Fine(loanId, memberId, amount, reason);
        fineRepository.insert(fine);
    }

    public String payFine(int fineId, int userId) {
        String authErr = checkStaffAuth(userId);
        if (authErr != null) return authErr;

        boolean paid = fineRepository.markPaid(fineId);
        return paid ? null : "Failed to mark fine as paid";
    }

    public double getUnpaidTotalByMember(int memberId) {
        return fineRepository.totalUnpaidByMember(memberId);
    }

    public double getTotalUnpaid() {
        return fineRepository.totalUnpaid();
    }
}
