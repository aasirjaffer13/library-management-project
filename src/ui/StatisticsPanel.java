package ui;

import service.StatisticsService;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class StatisticsPanel extends JPanel {
    private final StatisticsService statsService;
    private JPanel cardsPanel;

    public StatisticsPanel() {
        this.statsService = new StatisticsService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initUI();
        loadStats();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Dashboard");
        CommonUI.styleLabel(titleLabel, 18, true);
        topPanel.add(titleLabel, BorderLayout.WEST);

        JButton refreshBtn = new JButton("Refresh");
        CommonUI.styleButton(refreshBtn, CommonUI.PRIMARY);
        refreshBtn.addActionListener(e -> loadStats());
        topPanel.add(refreshBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        cardsPanel = new JPanel(new GridLayout(3, 3, 15, 15));
        cardsPanel.setOpaque(false);
        add(cardsPanel, BorderLayout.CENTER);
    }

    private void loadStats() {
        cardsPanel.removeAll();
        Map<String, Object> stats = statsService.getDashboardStats();

        cardsPanel.add(CommonUI.createCard("Total Books", String.valueOf(stats.get("totalBooks"))));
        cardsPanel.add(CommonUI.createCard("Available Copies", String.valueOf(stats.get("availableCopies"))));
        cardsPanel.add(CommonUI.createCard("Active Members", String.valueOf(stats.get("totalMembers"))));
        cardsPanel.add(CommonUI.createCard("Active Loans", String.valueOf(stats.get("activeLoans"))));
        cardsPanel.add(CommonUI.createCard("Overdue Loans", String.valueOf(stats.get("overdueLoans"))));
        cardsPanel.add(CommonUI.createCard("Pending Reservations", String.valueOf(stats.get("pendingReservations"))));
        cardsPanel.add(CommonUI.createCard("Unpaid Fines", String.format("%.2f", stats.get("totalUnpaidFines"))));
        cardsPanel.add(CommonUI.createCard("Librarians", String.valueOf(stats.get("librarians"))));
        cardsPanel.add(CommonUI.createCard("Clerks", String.valueOf(stats.get("clerks"))));

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }
}
