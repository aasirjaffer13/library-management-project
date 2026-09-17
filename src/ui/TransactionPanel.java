package ui;

import model.Fine;
import model.Loan;
import model.LoanStatus;
import service.AuthService;
import service.FineService;
import service.LoanService;
import util.DateUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TransactionPanel extends JPanel {
    private final AuthService authService;
    private final LoanService loanService;
    private final FineService fineService;
    private JTable loanTable, fineTable;
    private DefaultTableModel loanModel, fineModel;

    public TransactionPanel(AuthService authService) {
        this.authService = authService;
        this.loanService = new LoanService();
        this.fineService = new FineService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initUI();
        loadData();
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tabs.addTab("Loan History", createLoanHistoryPanel());
        tabs.addTab("Fines", createFinesPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createLoanHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        JLabel title = new JLabel("All Loan Transactions");
        CommonUI.styleLabel(title, 14, true);
        topPanel.add(title);

        JButton refreshBtn = new JButton("Refresh");
        CommonUI.styleButton(refreshBtn, CommonUI.PRIMARY);
        refreshBtn.addActionListener(e -> loadData());
        topPanel.add(refreshBtn);
        panel.add(topPanel, BorderLayout.NORTH);

        loanModel = CommonUI.createTableModel(
            new String[]{"Loan ID", "Book", "Member", "Issued By", "Issue Date", "Due Date", "Return Date", "Status"}
        );
        loanTable = new JTable(loanModel);
        CommonUI.styleTable(loanTable);
        panel.add(CommonUI.createScrollPane(loanTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFinesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        JLabel title = new JLabel("Fines");
        CommonUI.styleLabel(title, 14, true);
        topPanel.add(title);

        JButton refreshBtn = new JButton("Refresh");
        CommonUI.styleButton(refreshBtn, CommonUI.PRIMARY);
        refreshBtn.addActionListener(e -> loadData());
        topPanel.add(refreshBtn);

        JButton payBtn = new JButton("Mark as Paid");
        CommonUI.styleButton(payBtn, CommonUI.SUCCESS);
        payBtn.addActionListener(e -> markFinePaid());
        topPanel.add(payBtn);

        panel.add(topPanel, BorderLayout.NORTH);

        fineModel = CommonUI.createTableModel(
            new String[]{"Fine ID", "Loan ID", "Member", "Amount", "Reason", "Paid", "Date"}
        );
        fineTable = new JTable(fineModel);
        CommonUI.styleTable(fineTable);
        panel.add(CommonUI.createScrollPane(fineTable), BorderLayout.CENTER);

        return panel;
    }

    private void loadData() {
        loadLoanHistory();
        loadFines();
    }

    private void loadLoanHistory() {
        loanModel.setRowCount(0);
        List<Loan> loans = loanService.getAllLoans();
        for (Loan l : loans) {
            loanModel.addRow(new Object[]{
                l.getId(), l.getBookTitle(), l.getMemberName(), l.getIssuedByName(),
                DateUtils.formatDateTime(l.getIssueDate()),
                DateUtils.formatDateTime(l.getDueDate()),
                l.getReturnDate() != null ? DateUtils.formatDateTime(l.getReturnDate()) : "-",
                l.getStatus().getDisplayName()
            });
        }
    }

    private void loadFines() {
        fineModel.setRowCount(0);
        List<Fine> fines = fineService.getAllFines();
        for (Fine f : fines) {
            fineModel.addRow(new Object[]{
                f.getId(), f.getLoanId(), f.getMemberName(),
                String.format("%.2f", f.getAmount()),
                f.getReason(),
                f.isPaid() ? "Yes" : "No",
                f.getCreatedAt() != null ? DateUtils.formatDateTime(f.getCreatedAt()) : ""
            });
        }
    }

    private void markFinePaid() {
        int row = fineTable.getSelectedRow();
        if (row < 0) {
            CommonUI.showMessage("Please select a fine.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean paid = "Yes".equals(fineModel.getValueAt(row, 5));
        if (paid) {
            CommonUI.showMessage("Fine is already paid.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int fineId = (int) fineModel.getValueAt(row, 0);
        if (CommonUI.confirmAction("Mark fine #" + fineId + " as paid?", "Confirm")) {
            fineService.payFine(fineId, authService.getCurrentUser().getId());
            loadData();
        }
    }
}
