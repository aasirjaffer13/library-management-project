package model;

import java.time.LocalDateTime;

public class Loan {
    private int id;
    private int bookId;
    private int memberId;
    private int issuedByUserId;
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private Integer returnedToUserId;
    private LoanStatus status;
    private String bookTitle;
    private String memberName;
    private String issuedByName;

    public Loan() {}

    public Loan(int bookId, int memberId, int issuedByUserId,
                LocalDateTime issueDate, LocalDateTime dueDate) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.issuedByUserId = issuedByUserId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = LoanStatus.ACTIVE;
    }

    public boolean isOverdue() {
        return status == LoanStatus.ACTIVE && LocalDateTime.now().isAfter(dueDate);
    }

    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        return java.time.Duration.between(dueDate, LocalDateTime.now()).toDays();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public int getIssuedByUserId() { return issuedByUserId; }
    public void setIssuedByUserId(int issuedByUserId) { this.issuedByUserId = issuedByUserId; }
    public LocalDateTime getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDateTime issueDate) { this.issueDate = issueDate; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public LocalDateTime getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDateTime returnDate) { this.returnDate = returnDate; }
    public Integer getReturnedToUserId() { return returnedToUserId; }
    public void setReturnedToUserId(Integer returnedToUserId) { this.returnedToUserId = returnedToUserId; }
    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getIssuedByName() { return issuedByName; }
    public void setIssuedByName(String issuedByName) { this.issuedByName = issuedByName; }
}
