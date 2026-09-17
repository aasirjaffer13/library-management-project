package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CommonUI {

    public static final Color PRIMARY = new Color(41, 98, 255);
    public static final Color PRIMARY_DARK = new Color(26, 73, 200);
    public static final Color SECONDARY = new Color(255, 193, 7);
    public static final Color SUCCESS = new Color(40, 167, 69);
    public static final Color DANGER = new Color(220, 53, 69);
    public static final Color BG_LIGHT = new Color(245, 245, 245);
    public static final Color TEXT_DARK = new Color(33, 37, 41);

    public static void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);
    }

    public static void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    public static void stylePasswordField(JPasswordField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    public static void styleLabel(JLabel label, int size, boolean bold) {
        label.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, size));
        label.setForeground(TEXT_DARK);
    }

    public static DefaultTableModel createTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setSelectionBackground(PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setForeground(TEXT_DARK);
        table.setShowVerticalLines(false);
    }

    public static JScrollPane createScrollPane(JTable table) {
        JScrollPane pane = new JScrollPane(table);
        pane.setBorder(BorderFactory.createEmptyBorder());
        pane.getViewport().setBackground(Color.WHITE);
        return pane;
    }

    public static void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(null, message, title, messageType);
    }

    public static boolean confirmAction(String message, String title) {
        int result = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    public static JPanel createCard(String title, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        styleLabel(titleLabel, 12, false);
        titleLabel.setForeground(new Color(120, 120, 120));

        JLabel valueLabel = new JLabel(value);
        styleLabel(valueLabel, 24, true);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLabel);

        return card;
    }
}
