package ui;

import model.Book;
import model.Loan;
import model.Member;
import service.AuthService;
import service.BookService;
import service.LoanService;
import service.MemberService;
import util.DateUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LoanPanel extends JPanel {
    private final AuthService authService;
    private final LoanService loanService;
    private final BookService bookService;
    private final MemberService memberService;
    private JTable issueTable, returnTable;
    private DefaultTableModel issueModel, returnModel;

    public LoanPanel(AuthService authService) {
        this.authService = authService;
        this.loanService = new LoanService();
        this.bookService = new BookService();
        this.memberService = new MemberService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initUI();
        loadData();
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tabs.addTab("Issue Book", createIssuePanel());
        tabs.addTab("Return Book", createReturnPanel());
        tabs.addTab("Overdue Loans", createOverduePanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createIssuePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> bookCombo = new JComboBox<>();
        bookCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        List<Book> availableBooks = bookService.getAvailableBooks();
        for (Book b : availableBooks) {
            bookCombo.addItem(b.getId() + " - " + b.getTitle() + " by " + b.getAuthor());
        }

        JComboBox<String> memberCombo = new JComboBox<>();
        memberCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        List<Member> members = memberService.getAllMembers();
        for (Member m : members) {
            memberCombo.addItem(m.getId() + " - " + m.getFullName() + " [" + m.getMemberNumber() + "]");
        }

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel bookLabel = new JLabel("Select Book:");
        CommonUI.styleLabel(bookLabel, 13, true);
        formPanel.add(bookLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(bookCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel memberLabel = new JLabel("Select Member:");
        CommonUI.styleLabel(memberLabel, 13, true);
        formPanel.add(memberLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(memberCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1;
        JButton issueBtn = new JButton("Issue Book");
        CommonUI.styleButton(issueBtn, CommonUI.SUCCESS);
        issueBtn.addActionListener(e -> {
            if (bookCombo.getSelectedIndex() < 0 || memberCombo.getSelectedIndex() < 0) {
                CommonUI.showMessage("Please select both a book and a member.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String bookStr = (String) bookCombo.getSelectedItem();
            String memberStr = (String) memberCombo.getSelectedItem();
            int bookId = Integer.parseInt(bookStr.split(" - ")[0]);
            int memberId = Integer.parseInt(memberStr.split(" - ")[0]);

            String error = loanService.issueBook(bookId, memberId, authService.getCurrentUser().getId());
            if (error != null) {
                CommonUI.showMessage(error, "Issue Failed", JOptionPane.WARNING_MESSAGE);
            } else {
                CommonUI.showMessage("Book issued successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                refreshBookCombo(bookCombo);
            }
        });
        formPanel.add(issueBtn, gbc);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReturnPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setOpaque(false);
        JLabel title = new JLabel("Active Loans - Select a loan to return:");
        CommonUI.styleLabel(title, 13, true);
        topPanel.add(title);

        JButton refreshBtn = new JButton("Refresh");
        CommonUI.styleButton(refreshBtn, CommonUI.PRIMARY);
        refreshBtn.addActionListener(e -> loadData());
        topPanel.add(refreshBtn);

        panel.add(topPanel, BorderLayout.NORTH);

        returnModel = CommonUI.createTableModel(
            new String[]{"Loan ID", "Book", "Member", "Issue Date", "Due Date", "Status"}
        );
        returnTable = new JTable(returnModel);
        CommonUI.styleTable(returnTable);
        panel.add(CommonUI.createScrollPane(returnTable), BorderLayout.CENTER);

        JButton returnBtn = new JButton("Return Selected Book");
        CommonUI.styleButton(returnBtn, CommonUI.SUCCESS);
        returnBtn.addActionListener(e -> returnBook());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(returnBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createOverduePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        issueModel = CommonUI.createTableModel(
            new String[]{"Loan ID", "Book", "Member", "Issue Date", "Due Date", "Days Overdue"}
        );
        issueTable = new JTable(issueModel);
        CommonUI.styleTable(issueTable);
        panel.add(CommonUI.createScrollPane(issueTable), BorderLayout.CENTER);

        return panel;
    }

    private void loadData() {
        loadReturnTable();
        loadOverdueTable();
    }

    private void loadReturnTable() {
        returnModel.setRowCount(0);
        List<Loan> loans = loanService.getAllLoans();
        for (Loan l : loans) {
            if (l.getStatus() == model.LoanStatus.ACTIVE) {
                returnModel.addRow(new Object[]{
                    l.getId(), l.getBookTitle(), l.getMemberName(),
                    DateUtils.formatDateTime(l.getIssueDate()),
                    DateUtils.formatDateTime(l.getDueDate()),
                    l.getStatus().getDisplayName()
                });
            }
        }
    }

    private void loadOverdueTable() {
        issueModel.setRowCount(0);
        List<Loan> overdue = loanService.getOverdueLoans();
        for (Loan l : overdue) {
            issueModel.addRow(new Object[]{
                l.getId(), l.getBookTitle(), l.getMemberName(),
                DateUtils.formatDateTime(l.getIssueDate()),
                DateUtils.formatDateTime(l.getDueDate()),
                l.getDaysOverdue()
            });
        }
    }

    private void returnBook() {
        int row = returnTable.getSelectedRow();
        if (row < 0) {
            CommonUI.showMessage("Please select a loan to return.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int loanId = (int) returnModel.getValueAt(row, 0);
        String bookTitle = (String) returnModel.getValueAt(row, 1);

        if (CommonUI.confirmAction("Return \"" + bookTitle + "\"?", "Confirm Return")) {
            String error = loanService.returnBook(loanId, authService.getCurrentUser().getId());
            if (error != null) {
                CommonUI.showMessage(error, "Return Failed", JOptionPane.WARNING_MESSAGE);
            } else {
                CommonUI.showMessage("Book returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            }
        }
    }

    private void refreshBookCombo(JComboBox<String> combo) {
        combo.removeAllItems();
        List<Book> available = bookService.getAvailableBooks();
        for (Book b : available) {
            combo.addItem(b.getId() + " - " + b.getTitle() + " by " + b.getAuthor());
        }
    }
}
