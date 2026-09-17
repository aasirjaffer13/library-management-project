package ui;

import model.User;
import service.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final AuthService authService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    public LoginFrame(AuthService authService) {
        this.authService = authService;
        initUI();
    }

    private void initUI() {
        setTitle("Library Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 380);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        JLabel titleLabel = new JLabel("Library Management System");
        CommonUI.styleLabel(titleLabel, 22, true);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Sign in to your account");
        CommonUI.styleLabel(subtitleLabel, 13, false);
        subtitleLabel.setForeground(new Color(120, 120, 120));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitleLabel);

        mainPanel.add(Box.createVerticalStrut(30));

        JLabel userLabel = new JLabel("Username");
        CommonUI.styleLabel(userLabel, 13, true);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(userLabel);

        mainPanel.add(Box.createVerticalStrut(4));

        usernameField = new JTextField(20);
        CommonUI.styleTextField(usernameField);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(usernameField);

        mainPanel.add(Box.createVerticalStrut(16));

        JLabel passLabel = new JLabel("Password");
        CommonUI.styleLabel(passLabel, 13, true);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(passLabel);

        mainPanel.add(Box.createVerticalStrut(4));

        passwordField = new JPasswordField(20);
        CommonUI.stylePasswordField(passwordField);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(passwordField);

        mainPanel.add(Box.createVerticalStrut(24));

        statusLabel = new JLabel(" ");
        CommonUI.styleLabel(statusLabel, 12, false);
        statusLabel.setForeground(CommonUI.DANGER);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(statusLabel);

        loginButton = new JButton("Login");
        CommonUI.styleButton(loginButton, CommonUI.PRIMARY);
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginButton.addActionListener(e -> doLogin());
        mainPanel.add(loginButton);

        mainPanel.add(Box.createVerticalStrut(20));

        JLabel hintLabel = new JLabel("<html><center>Default admin: <b>admin</b> / <b>admin123</b></center></html>");
        CommonUI.styleLabel(hintLabel, 11, false);
        hintLabel.setForeground(new Color(150, 150, 150));
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(hintLabel);

        passwordField.addActionListener(e -> doLogin());

        add(mainPanel);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() {
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        dispose();
                        SwingUtilities.invokeLater(() -> {
                            MainFrame mainFrame = new MainFrame(authService);
                            mainFrame.setVisible(true);
                        });
                    } else {
                        statusLabel.setText("Invalid username or password");
                        passwordField.setText("");
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Login error: " + ex.getMessage());
                }
                loginButton.setEnabled(true);
                loginButton.setText("Login");
            }
        };
        worker.execute();
    }
}
