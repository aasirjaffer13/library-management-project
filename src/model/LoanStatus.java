package model;

public enum LoanStatus {
    ACTIVE("Active"),
    RETURNED("Returned"),
    OVERDUE("Overdue");

    private final String displayName;

    LoanStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
