package ui;

import model.Book;
import model.Member;
import model.Reservation;
import service.AuthService;
import service.BookService;
import service.MemberService;
import service.ReservationService;
import util.DateUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReservationPanel extends JPanel {
    private final AuthService authService;
    private final ReservationService reservationService;
    private final BookService bookService;
    private final MemberService memberService;
    private JTable table;
    private DefaultTableModel tableModel;

    public ReservationPanel(AuthService authService) {
        this.authService = authService;
        this.reservationService = new ReservationService();
        this.bookService = new BookService();
        this.memberService = new MemberService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Reservations");
        CommonUI.styleLabel(titleLabel, 18, true);
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setOpaque(false);

        JButton reserveBtn = new JButton("+ New Reservation");
        CommonUI.styleButton(reserveBtn, CommonUI.SUCCESS);
        reserveBtn.addActionListener(e -> showReserveDialog());
        actionPanel.add(reserveBtn);

        JButton cancelBtn = new JButton("Cancel Reservation");
        CommonUI.styleButton(cancelBtn, CommonUI.DANGER);
        cancelBtn.addActionListener(e -> cancelReservation());
        actionPanel.add(cancelBtn);

        JButton refreshBtn = new JButton("Refresh");
        CommonUI.styleButton(refreshBtn, CommonUI.PRIMARY);
        refreshBtn.addActionListener(e -> loadData());
        actionPanel.add(refreshBtn);

        topPanel.add(actionPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        tableModel = CommonUI.createTableModel(
            new String[]{"ID", "Book", "Member", "Reserved On", "Expires", "Status"}
        );
        table = new JTable(tableModel);
        CommonUI.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(CommonUI.createScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Reservation> reservations = reservationService.getAllPending();
        for (Reservation r : reservations) {
            tableModel.addRow(new Object[]{
                r.getId(), r.getBookTitle(), r.getMemberName(),
                DateUtils.formatDateTime(r.getReservationDate()),
                DateUtils.formatDateTime(r.getExpiryDate()),
                r.getStatus().getDisplayName()
            });
        }
    }

    private void showReserveDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Reservation", true);
        dialog.setSize(420, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JComboBox<String> bookCombo = new JComboBox<>();
        bookCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        List<Book> books = bookService.getAllBooks();
        for (Book b : books) {
            if (!b.isAvailable()) {
                bookCombo.addItem(b.getId() + " - " + b.getTitle());
            }
        }

        if (bookCombo.getItemCount() == 0) {
            dialog.dispose();
            CommonUI.showMessage("No unavailable books to reserve.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<String> memberCombo = new JComboBox<>();
        memberCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        List<Member> members = memberService.getAllMembers();
        for (Member m : members) {
            memberCombo.addItem(m.getId() + " - " + m.getFullName());
        }

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel bookLabel = new JLabel("Select Book:");
        CommonUI.styleLabel(bookLabel, 12, true);
        panel.add(bookLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(bookCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel memberLabel = new JLabel("Select Member:");
        CommonUI.styleLabel(memberLabel, 12, true);
        panel.add(memberLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(memberCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1;
        JButton reserveBtn = new JButton("Reserve");
        CommonUI.styleButton(reserveBtn, CommonUI.SUCCESS);
        reserveBtn.addActionListener(e -> {
            String bookStr = (String) bookCombo.getSelectedItem();
            String memberStr = (String) memberCombo.getSelectedItem();
            if (bookStr == null || memberStr == null) return;

            int bookId = Integer.parseInt(bookStr.split(" - ")[0]);
            int memberId = Integer.parseInt(memberStr.split(" - ")[0]);

            String error = reservationService.makeReservation(bookId, memberId);
            if (error != null) {
                CommonUI.showMessage(error, "Error", JOptionPane.WARNING_MESSAGE);
            } else {
                CommonUI.showMessage("Reservation created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadData();
            }
        });
        panel.add(reserveBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void cancelReservation() {
        int row = table.getSelectedRow();
        if (row < 0) {
            CommonUI.showMessage("Please select a reservation.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resId = (int) tableModel.getValueAt(row, 0);
        if (CommonUI.confirmAction("Cancel this reservation?", "Confirm")) {
            reservationService.cancelReservation(resId);
            loadData();
        }
    }
}
