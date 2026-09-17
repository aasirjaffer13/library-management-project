package util;

import java.util.regex.Pattern;

public class Validation {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9]{7,15}$"
    );
    private static final Pattern ISBN_PATTERN = Pattern.compile(
        "^[0-9]{10,13}$"
    );

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return isNullOrEmpty(email) || EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return isNullOrEmpty(phone) || PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidIsbn(String isbn) {
        return isNullOrEmpty(isbn) || ISBN_PATTERN.matcher(isbn).matches();
    }

    public static boolean isPositiveInt(String s) {
        try {
            return Integer.parseInt(s) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveDouble(String s) {
        try {
            return Double.parseDouble(s) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int parsePositiveInt(String s) throws IllegalArgumentException {
        try {
            int val = Integer.parseInt(s.trim());
            if (val <= 0) throw new IllegalArgumentException("Must be a positive number");
            return val;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number: " + s);
        }
    }
}
