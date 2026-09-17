package app;

import service.AuthService;
import ui.LoginFrame;

import javax.swing.*;

public class LibraryApp {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseManager.getInstance().initializeDatabase();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Failed to initialize database: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                System.exit(1);
            }

            AuthService authService = new AuthService();
            LoginFrame loginFrame = new LoginFrame(authService);
            loginFrame.setVisible(true);
        });
    }
}
