package service;

import model.Book;
import model.BookCategory;
import model.Loan;
import model.User;
import model.UserRole;
import repository.BookRepository;
import repository.LoanRepository;
import repository.UserRepository;
import util.Validation;

import java.util.List;

public class BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookService() {
        this.bookRepository = new BookRepository();
        this.userRepository = new UserRepository();
    }

    private String checkBookManagerAuth(int userId) {
        User user = userRepository.findById(userId);
        if (user == null) return "User not found";
        if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.LIBRARIAN) {
            return "Permission denied: requires Administrator or Librarian role";
        }
        return null;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBook(int id) {
        return bookRepository.findById(id);
    }

    public Book getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public List<Book> searchBooks(String keyword) {
        return bookRepository.search(keyword);
    }

    public List<Book> getBooksByCategory(BookCategory category) {
        return bookRepository.findByCategory(category);
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findAvailable();
    }

    public String addBook(String isbn, String title, String author, BookCategory category,
                          String publisher, String edition, int totalCopies,
                          String location, int addedByUserId) {
        String authErr = checkBookManagerAuth(addedByUserId);
        if (authErr != null) return authErr;

        if (Validation.isNullOrEmpty(title)) return "Title is required";
        if (Validation.isNullOrEmpty(author)) return "Author is required";
        if (!Validation.isValidIsbn(isbn)) return "Invalid ISBN format (10-13 digits)";
        if (totalCopies < 1) return "Total copies must be at least 1";

        if (!Validation.isNullOrEmpty(isbn) && bookRepository.findByIsbn(isbn) != null) {
            return "A book with this ISBN already exists";
        }

        Book book = new Book(isbn, title, author, category, publisher, edition, totalCopies, location, addedByUserId);
        int id = bookRepository.insert(book);
        return id > 0 ? null : "Failed to add book to database";
    }

    public String updateBook(Book book, int userId) {
        String authErr = checkBookManagerAuth(userId);
        if (authErr != null) return authErr;

        if (Validation.isNullOrEmpty(book.getTitle())) return "Title is required";
        if (Validation.isNullOrEmpty(book.getAuthor())) return "Author is required";

        Book existing = bookRepository.findByIsbn(book.getIsbn());
        if (existing != null && existing.getId() != book.getId()) {
            return "Another book with this ISBN already exists";
        }

        boolean updated = bookRepository.update(book);
        return updated ? null : "Failed to update book";
    }

    public String deleteBook(int bookId, int userId) {
        String authErr = checkBookManagerAuth(userId);
        if (authErr != null) return authErr;

        List<Loan> activeLoans = new LoanRepository().findActiveByBookId(bookId);
        if (!activeLoans.isEmpty()) {
            return "Cannot delete: book has active loans";
        }
        boolean deleted = bookRepository.delete(bookId);
        return deleted ? null : "Failed to delete book";
    }

    public boolean isBookAvailable(int bookId) {
        Book book = bookRepository.findById(bookId);
        return book != null && book.isAvailable();
    }

    public void decrementAvailability(int bookId) {
        bookRepository.updateAvailability(bookId, -1);
    }

    public void incrementAvailability(int bookId) {
        bookRepository.updateAvailability(bookId, 1);
    }

    public int getTotalBooks() {
        return bookRepository.countAll();
    }

    public int getAvailableCopies() {
        return bookRepository.countAvailable();
    }
}
