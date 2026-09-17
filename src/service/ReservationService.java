package service;

import app.DatabaseManager;
import model.Reservation;
import model.ReservationStatus;
import repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final BookService bookService;

    public ReservationService() {
        this.reservationRepository = new ReservationRepository();
        this.bookService = new BookService();
    }

    public List<Reservation> getAllPending() {
        return reservationRepository.findAllPending();
    }

    public List<Reservation> getPendingByBook(int bookId) {
        return reservationRepository.findPendingByBookId(bookId);
    }

    public List<Reservation> getPendingByMember(int memberId) {
        return reservationRepository.findPendingByMemberId(memberId);
    }

    public String makeReservation(int bookId, int memberId) {
        if (bookService.isBookAvailable(bookId)) {
            return "Book is currently available. No need to reserve.";
        }

        if (reservationRepository.hasActiveReservation(bookId, memberId)) {
            return "You already have an active reservation for this book";
        }

        LocalDateTime now = LocalDateTime.now();
        int expiryDays = getReservationExpiryDays();
        LocalDateTime expiry = now.plusDays(expiryDays);

        Reservation reservation = new Reservation(bookId, memberId, now, expiry);
        int id = reservationRepository.insert(reservation);
        return id > 0 ? null : "Failed to create reservation";
    }

    public String cancelReservation(int reservationId) {
        boolean updated = reservationRepository.updateStatus(reservationId, ReservationStatus.CANCELLED);
        return updated ? null : "Failed to cancel reservation";
    }

    public void fulfillReservation(int reservationId) {
        reservationRepository.updateStatus(reservationId, ReservationStatus.FULFILLED);
    }

    public int getPendingCount() {
        return reservationRepository.countPending();
    }

    private int getReservationExpiryDays() {
        String val = DatabaseManager.getInstance().getConfig("reservation_expiry_days");
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return 7;
        }
    }
}
