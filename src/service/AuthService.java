package service;

import model.User;
import model.UserRole;
import repository.UserRepository;
import util.PasswordUtil;

public class AuthService {
    private final UserRepository userRepository;
    private User currentUser;

    public AuthService() {
        this.userRepository = new UserRepository();
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.isActive() && PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            currentUser = user;
            return user;
        }
        return null;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean hasPermission(UserRole requiredRole) {
        if (currentUser == null) return false;
        return currentUser.getRole() == requiredRole;
    }

    public boolean canManageBooks() {
        return currentUser != null &&
               (currentUser.getRole() == UserRole.LIBRARIAN || currentUser.getRole() == UserRole.ADMIN);
    }

    public boolean canManageMembers() {
        return currentUser != null &&
               (currentUser.getRole() == UserRole.LIBRARIAN ||
                currentUser.getRole() == UserRole.CLERK ||
                currentUser.getRole() == UserRole.ADMIN);
    }

    public boolean canIssueReturn() {
        return currentUser != null &&
               (currentUser.getRole() == UserRole.LIBRARIAN ||
                currentUser.getRole() == UserRole.CLERK ||
                currentUser.getRole() == UserRole.ADMIN);
    }

    public boolean canManageUsers() {
        return currentUser != null && currentUser.getRole() == UserRole.ADMIN;
    }

    public User register(String username, String password, String fullName, UserRole role, String email, String phone, String address) {
        if (userRepository.findByUsername(username) != null) {
            return null;
        }
        String hash = PasswordUtil.hashPassword(password);
        User user = new User(username, hash, fullName, role, email, phone, address);
        int id = userRepository.insert(user);
        if (id > 0) {
            user.setId(id);
            return user;
        }
        return null;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId);
        if (user != null && PasswordUtil.verifyPassword(oldPassword, user.getPasswordHash())) {
            String newHash = PasswordUtil.hashPassword(newPassword);
            return userRepository.updatePassword(userId, newHash);
        }
        return false;
    }
}
