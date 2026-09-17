package model;

import java.time.LocalDateTime;

public class Fine {
    private int id;
    private int loanId;
    private int memberId;
    private double amount;
    private String reason;
    private boolean paid;
    private LocalDateTime createdAt;
    private String memberName;

    public Fine() {}

    public Fine(int loanId, int memberId, double amount, String reason) {
        this.loanId = loanId;
        this.memberId = memberId;
        this.amount = amount;
        this.reason = reason;
        this.paid = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getLoanId() { return loanId; }
    public void setLoanId(int loanId) { this.loanId = loanId; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
}
