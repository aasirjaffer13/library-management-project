package ui;

import model.User;
import model.UserRole;
import service.AuthService;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final AuthService authService;
    private JTabbedPane tabbedPane;

    public MainFrame(AuthService authService) {
        this.authService = authService;
        initUI();
    }

    private void initUI() {
        User user = authService.getCurrentUser();
        setTitle("Library Management System - " + user.getFullName() + " (" + user.getRole().getDisplayName() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JPanel headerPanel = createHeader();
        add(headerPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);

        tabbedPane.addTab("Dashboard", new StatisticsPanel());
        tabbedPane.addTab("Books", new BookPanel(authService));
        tabbedPane.addTab("Members", new MemberPanel(authService));
        tabbedPane.addTab("Issue Book", new LoanPanel(authService));
        tabbedPane.addTab("Reservations", new ReservationPanel(authService));
        tabbedPane.addTab("Transactions", new TransactionPanel(authService));

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CommonUI.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel welcomeLabel = new JLabel("Library Management System");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(welcomeLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        User user = authService.getCurrentUser();
        JLabel userLabel = new JLabel(user.getFullName() + " (" + user.getRole().getDisplayName() + ")");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rightPanel.add(userLabel);

        JButton logoutButton = new JButton("Logout");
        CommonUI.styleButton(logoutButton, CommonUI.DANGER);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.addActionListener(e -> {
            if (CommonUI.confirmAction("Are you sure you want to logout?", "Confirm Logout")) {
                authService.logout();
                dispose();
                SwingUtilities.invokeLater(() -> {
                    LoginFrame loginFrame = new LoginFrame(authService);
                    loginFrame.setVisible(true);
                });
            }
        });
        rightPanel.add(logoutButton);

        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }
}
