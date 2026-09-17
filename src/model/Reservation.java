package model;

import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private int bookId;
    private int memberId;
    private LocalDateTime reservationDate;
    private LocalDateTime expiryDate;
    private ReservationStatus status;
    private String bookTitle;
    private String memberName;

    public Reservation() {}

    public Reservation(int bookId, int memberId,
                       LocalDateTime reservationDate, LocalDateTime expiryDate) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.reservationDate = reservationDate;
        this.expiryDate = expiryDate;
        this.status = ReservationStatus.PENDING;
    }

    public boolean isExpired() {
        return status == ReservationStatus.PENDING && LocalDateTime.now().isAfter(expiryDate);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public LocalDateTime getReservationDate() { return reservationDate; }
    public void setReservationDate(LocalDateTime reservationDate) { this.reservationDate = reservationDate; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
}
