package model;

public enum BookCategory {
    FICTION("Fiction"),
    NON_FICTION("Non-Fiction"),
    SCIENCE("Science"),
    TECHNOLOGY("Technology"),
    HISTORY("History"),
    LITERATURE("Literature"),
    MATHEMATICS("Mathematics"),
    BUSINESS("Business"),
    OTHER("Other");

    private final String displayName;

    BookCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
