package model;

public enum ReservationStatus {
    PENDING("Pending"),
    FULFILLED("Fulfilled"),
    CANCELLED("Cancelled"),
    EXPIRED("Expired");

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
