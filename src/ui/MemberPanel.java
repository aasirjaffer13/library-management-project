package ui;

import model.Member;
import service.AuthService;
import service.FineService;
import service.MemberService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MemberPanel extends JPanel {
    private final AuthService authService;
    private final MemberService memberService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public MemberPanel(AuthService authService) {
        this.authService = authService;
        this.memberService = new MemberService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initUI();
        loadMembers();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Member Management");
        CommonUI.styleLabel(titleLabel, 18, true);
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setOpaque(false);

        searchField = new JTextField(20);
        CommonUI.styleTextField(searchField);
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchMembers();
            }
        });
        actionPanel.add(searchField);

        boolean canManage = authService.canManageMembers();

        JButton addBtn = new JButton("+ Add Member");
        CommonUI.styleButton(addBtn, CommonUI.SUCCESS);
        addBtn.addActionListener(e -> showAddDialog());
        addBtn.setVisible(canManage);
        actionPanel.add(addBtn);

        JButton editBtn = new JButton("Edit");
        CommonUI.styleButton(editBtn, CommonUI.PRIMARY);
        editBtn.addActionListener(e -> showEditDialog());
        editBtn.setVisible(canManage);
        actionPanel.add(editBtn);

        JButton deactivateBtn = new JButton("Deactivate");
        CommonUI.styleButton(deactivateBtn, CommonUI.DANGER);
        deactivateBtn.addActionListener(e -> deactivateMember());
        deactivateBtn.setVisible(canManage);
        actionPanel.add(deactivateBtn);

        topPanel.add(actionPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        tableModel = CommonUI.createTableModel(
            new String[]{"ID", "Member No.", "Full Name", "Email", "Phone", "Joined", "Active"}
        );
        table = new JTable(tableModel);
        CommonUI.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(CommonUI.createScrollPane(table), BorderLayout.CENTER);
    }

    private void loadMembers() {
        tableModel.setRowCount(0);
        List<Member> members = memberService.getAllMembers();
        for (Member m : members) {
            tableModel.addRow(new Object[]{
                m.getId(), m.getMemberNumber(), m.getFullName(),
                m.getEmail(), m.getPhone(),
                m.getMembershipDate() != null ? m.getMembershipDate().toString() : "",
                m.isActive() ? "Yes" : "No"
            });
        }
    }

    private void searchMembers() {
        String keyword = searchField.getText().trim();
        tableModel.setRowCount(0);
        List<Member> members = keyword.isEmpty() ? memberService.getAllMembers() : memberService.searchMembers(keyword);
        for (Member m : members) {
            tableModel.addRow(new Object[]{
                m.getId(), m.getMemberNumber(), m.getFullName(),
                m.getEmail(), m.getPhone(),
                m.getMembershipDate() != null ? m.getMembershipDate().toString() : "",
                m.isActive() ? "Yes" : "No"
            });
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Member", true);
        dialog.setSize(420, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField addressField = new JTextField(20);

        CommonUI.styleTextField(nameField);
        CommonUI.styleTextField(emailField);
        CommonUI.styleTextField(phoneField);
        CommonUI.styleTextField(addressField);

        String[] labels = {"Full Name *:", "Email:", "Phone:", "Address:"};
        JComponent[] fields = {nameField, emailField, phoneField, addressField};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            CommonUI.styleLabel(lbl, 12, true);
            panel.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            panel.add(fields[i], gbc);
        }

        gbc.gridx = 0; gbc.gridy = labels.length; gbc.weightx = 1; gbc.gridwidth = 2;
        JButton saveBtn = new JButton("Save Member");
        CommonUI.styleButton(saveBtn, CommonUI.SUCCESS);
        saveBtn.addActionListener(e -> {
            String error = memberService.addMember(
                nameField.getText().trim(), emailField.getText().trim(),
                phoneField.getText().trim(), addressField.getText().trim(),
                authService.getCurrentUser().getId()
            );
            if (error != null) {
                CommonUI.showMessage(error, "Validation Error", JOptionPane.WARNING_MESSAGE);
            } else {
                CommonUI.showMessage("Member added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadMembers();
            }
        });
        panel.add(saveBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            CommonUI.showMessage("Please select a member to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int memberId = (int) tableModel.getValueAt(row, 0);
        Member member = memberService.getMember(memberId);
        if (member == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Member", true);
        dialog.setSize(420, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField nameField = new JTextField(member.getFullName());
        JTextField emailField = new JTextField(member.getEmail() != null ? member.getEmail() : "");
        JTextField phoneField = new JTextField(member.getPhone() != null ? member.getPhone() : "");
        JTextField addressField = new JTextField(member.getAddress() != null ? member.getAddress() : "");

        CommonUI.styleTextField(nameField);
        CommonUI.styleTextField(emailField);
        CommonUI.styleTextField(phoneField);
        CommonUI.styleTextField(addressField);

        String[] labels = {"Full Name *:", "Email:", "Phone:", "Address:"};
        JComponent[] fields = {nameField, emailField, phoneField, addressField};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            CommonUI.styleLabel(lbl, 12, true);
            panel.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            panel.add(fields[i], gbc);
        }

        gbc.gridx = 0; gbc.gridy = labels.length; gbc.weightx = 1; gbc.gridwidth = 2;
        JButton updateBtn = new JButton("Update Member");
        CommonUI.styleButton(updateBtn, CommonUI.PRIMARY);
        updateBtn.addActionListener(e -> {
            member.setFullName(nameField.getText().trim());
            member.setEmail(emailField.getText().trim());
            member.setPhone(phoneField.getText().trim());
            member.setAddress(addressField.getText().trim());

            String error = memberService.updateMember(member, authService.getCurrentUser().getId());
            if (error != null) {
                CommonUI.showMessage(error, "Error", JOptionPane.WARNING_MESSAGE);
            } else {
                CommonUI.showMessage("Member updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadMembers();
            }
        });
        panel.add(updateBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deactivateMember() {
        int row = table.getSelectedRow();
        if (row < 0) {
            CommonUI.showMessage("Please select a member.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int memberId = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 2);

        if (CommonUI.confirmAction("Deactivate member \"" + name + "\"?", "Confirm")) {
            memberService.deactivateMember(memberId, authService.getCurrentUser().getId());
            loadMembers();
        }
    }
}
