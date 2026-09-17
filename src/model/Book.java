package model;

import java.time.LocalDateTime;

public class Book {
    private int id;
    private String isbn;
    private String title;
    private String author;
    private BookCategory category;
    private String publisher;
    private String edition;
    private int totalCopies;
    private int availableCopies;
    private String location;
    private int addedByUserId;
    private LocalDateTime createdAt;

    public Book() {}

    public Book(String isbn, String title, String author, BookCategory category,
                String publisher, String edition, int totalCopies,
                String location, int addedByUserId) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.publisher = publisher;
        this.edition = edition;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.location = location;
        this.addedByUserId = addedByUserId;
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public BookCategory getCategory() { return category; }
    public void setCategory(BookCategory category) { this.category = category; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }
    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getAddedByUserId() { return addedByUserId; }
    public void setAddedByUserId(int addedByUserId) { this.addedByUserId = addedByUserId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return title + " by " + author;
    }
}
