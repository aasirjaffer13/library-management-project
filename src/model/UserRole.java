package model;

public enum UserRole {
    ADMIN("Administrator"),
    LIBRARIAN("Librarian"),
    CLERK("Clerk");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
