package ui;

import model.Book;
import model.BookCategory;
import model.UserRole;
import service.AuthService;
import service.BookService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookPanel extends JPanel {
    private final AuthService authService;
    private final BookService bookService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton;

    public BookPanel(AuthService authService) {
        this.authService = authService;
        this.bookService = new BookService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initUI();
        loadBooks();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Book Management");
        CommonUI.styleLabel(titleLabel, 18, true);
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setOpaque(false);

        searchField = new JTextField(20);
        CommonUI.styleTextField(searchField);
        searchField.setToolTipText("Search by title, author, ISBN...");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchBooks();
            }
        });
        actionPanel.add(searchField);

        boolean canManage = authService.canManageBooks();

        addButton = new JButton("+ Add Book");
        CommonUI.styleButton(addButton, CommonUI.SUCCESS);
        addButton.addActionListener(e -> showAddDialog());
        addButton.setVisible(canManage);
        actionPanel.add(addButton);

        editButton = new JButton("Edit");
        CommonUI.styleButton(editButton, CommonUI.PRIMARY);
        editButton.addActionListener(e -> showEditDialog());
        editButton.setVisible(canManage);
        actionPanel.add(editButton);

        deleteButton = new JButton("Delete");
        CommonUI.styleButton(deleteButton, CommonUI.DANGER);
        deleteButton.addActionListener(e -> deleteBook());
        deleteButton.setVisible(canManage);
        actionPanel.add(deleteButton);

        topPanel.add(actionPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        tableModel = CommonUI.createTableModel(
            new String[]{"ID", "ISBN", "Title", "Author", "Category", "Publisher", "Copies", "Available", "Location"}
        );
        table = new JTable(tableModel);
        CommonUI.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(CommonUI.createScrollPane(table), BorderLayout.CENTER);
    }

    private void loadBooks() {
        tableModel.setRowCount(0);
        List<Book> books = bookService.getAllBooks();
        for (Book b : books) {
            tableModel.addRow(new Object[]{
                b.getId(), b.getIsbn(), b.getTitle(), b.getAuthor(),
                b.getCategory().getDisplayName(), b.getPublisher(),
                b.getTotalCopies(), b.getAvailableCopies(), b.getLocation()
            });
        }
    }

    private void searchBooks() {
        String keyword = searchField.getText().trim();
        tableModel.setRowCount(0);
        List<Book> books = keyword.isEmpty() ? bookService.getAllBooks() : bookService.searchBooks(keyword);
        for (Book b : books) {
            tableModel.addRow(new Object[]{
                b.getId(), b.getIsbn(), b.getTitle(), b.getAuthor(),
                b.getCategory().getDisplayName(), b.getPublisher(),
                b.getTotalCopies(), b.getAvailableCopies(), b.getLocation()
            });
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Book", true);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField isbnField = new JTextField(20);
        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JComboBox<BookCategory> categoryBox = new JComboBox<>(BookCategory.values());
        JTextField publisherField = new JTextField(20);
        JTextField editionField = new JTextField(20);
        JTextField copiesField = new JTextField("1");
        JTextField locationField = new JTextField(20);

        CommonUI.styleTextField(isbnField);
        CommonUI.styleTextField(titleField);
        CommonUI.styleTextField(authorField);
        CommonUI.styleTextField(publisherField);
        CommonUI.styleTextField(editionField);
        CommonUI.styleTextField(copiesField);
        CommonUI.styleTextField(locationField);

        String[] labels = {"ISBN:", "Title *:", "Author *:", "Category:", "Publisher:", "Edition:", "Copies:", "Location:"};
        JComponent[] fields = {isbnField, titleField, authorField, categoryBox, publisherField, editionField, copiesField, locationField};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            CommonUI.styleLabel(lbl, 12, true);
            panel.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 1.0;
            panel.add(fields[i], gbc);
        }

        gbc.gridx = 0; gbc.gridy = labels.length; gbc.weightx = 1;
        gbc.gridwidth = 2;
        JButton saveBtn = new JButton("Save Book");
        CommonUI.styleButton(saveBtn, CommonUI.SUCCESS);
        saveBtn.addActionListener(e -> {
            String isbn = isbnField.getText().trim();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            BookCategory cat = (BookCategory) categoryBox.getSelectedItem();
            String publisher = publisherField.getText().trim();
            String edition = editionField.getText().trim();
            int copies = 1;
            try { copies = Integer.parseInt(copiesField.getText().trim()); } catch (NumberFormatException ex) {}
            String location = locationField.getText().trim();

            String error = bookService.addBook(isbn, title, author, cat, publisher, edition, copies, location, authService.getCurrentUser().getId());
            if (error != null) {
                CommonUI.showMessage(error, "Validation Error", JOptionPane.WARNING_MESSAGE);
            } else {
                CommonUI.showMessage("Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadBooks();
            }
        });
        panel.add(saveBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            CommonUI.showMessage("Please select a book to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId = (int) tableModel.getValueAt(row, 0);
        Book book = bookService.getBook(bookId);
        if (book == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Book", true);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField isbnField = new JTextField(book.getIsbn() != null ? book.getIsbn() : "");
        JTextField titleField = new JTextField(book.getTitle());
        JTextField authorField = new JTextField(book.getAuthor());
        JComboBox<BookCategory> categoryBox = new JComboBox<>(BookCategory.values());
        categoryBox.setSelectedItem(book.getCategory());
        JTextField publisherField = new JTextField(book.getPublisher() != null ? book.getPublisher() : "");
        JTextField editionField = new JTextField(book.getEdition() != null ? book.getEdition() : "");
        JTextField copiesField = new JTextField(String.valueOf(book.getTotalCopies()));
        JTextField locationField = new JTextField(book.getLocation() != null ? book.getLocation() : "");

        CommonUI.styleTextField(isbnField);
        CommonUI.styleTextField(titleField);
        CommonUI.styleTextField(authorField);
        CommonUI.styleTextField(publisherField);
        CommonUI.styleTextField(editionField);
        CommonUI.styleTextField(copiesField);
        CommonUI.styleTextField(locationField);

        String[] labels = {"ISBN:", "Title *:", "Author *:", "Category:", "Publisher:", "Edition:", "Copies:", "Location:"};
        JComponent[] fields = {isbnField, titleField, authorField, categoryBox, publisherField, editionField, copiesField, locationField};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            CommonUI.styleLabel(lbl, 12, true);
            panel.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            panel.add(fields[i], gbc);
        }

        gbc.gridx = 0; gbc.gridy = labels.length; gbc.weightx = 1; gbc.gridwidth = 2;
        JButton updateBtn = new JButton("Update Book");
        CommonUI.styleButton(updateBtn, CommonUI.PRIMARY);
        updateBtn.addActionListener(e -> {
            book.setIsbn(isbnField.getText().trim());
            book.setTitle(titleField.getText().trim());
            book.setAuthor(authorField.getText().trim());
            book.setCategory((BookCategory) categoryBox.getSelectedItem());
            book.setPublisher(publisherField.getText().trim());
            book.setEdition(editionField.getText().trim());
            try { book.setTotalCopies(Integer.parseInt(copiesField.getText().trim())); } catch (NumberFormatException ex) {}
            book.setLocation(locationField.getText().trim());

            String error = bookService.updateBook(book, authService.getCurrentUser().getId());
            if (error != null) {
                CommonUI.showMessage(error, "Error", JOptionPane.WARNING_MESSAGE);
            } else {
                CommonUI.showMessage("Book updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadBooks();
            }
        });
        panel.add(updateBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteBook() {
        int row = table.getSelectedRow();
        if (row < 0) {
            CommonUI.showMessage("Please select a book to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 2);

        if (CommonUI.confirmAction("Delete \"" + title + "\"?", "Confirm Delete")) {
            String error = bookService.deleteBook(bookId, authService.getCurrentUser().getId());
            if (error != null) {
                CommonUI.showMessage(error, "Error", JOptionPane.WARNING_MESSAGE);
            } else {
                loadBooks();
            }
        }
    }
}