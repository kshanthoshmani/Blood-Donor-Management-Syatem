package util;

import exception.ValidationException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * ValidationUtil – reusable static validation methods.
 * Demonstrates utility class design and input validation concepts.
 */
public class ValidationUtil {

    private static final List<String> VALID_BLOOD_GROUPS =
        Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");

    private static final List<String> VALID_STATUSES =
        Arrays.asList("Available", "Not Available");

    // Prevent instantiation
    private ValidationUtil() {}

    // ── Donor ID ──────────────────────────────────────────────────────────────

    public static void validateDonorId(String donorId) throws ValidationException {
        if (donorId == null || donorId.trim().isEmpty()) {
            throw new ValidationException("Donor ID", "Please enter the Donor ID.");
        }
        if (donorId.trim().length() > 20) {
            throw new ValidationException("Donor ID",
                "Donor ID must not exceed 20 characters.");
        }
    }

    // ── Donor Name ────────────────────────────────────────────────────────────

    public static void validateDonorName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Donor Name", "Please enter the donor name.");
        }
        if (!name.trim().matches("[a-zA-Z .'\\-]+")) {
            throw new ValidationException("Donor Name",
                "Donor name should contain only letters, spaces, hyphens, or apostrophes.");
        }
        if (name.trim().length() < 2 || name.trim().length() > 100) {
            throw new ValidationException("Donor Name",
                "Donor name must be between 2 and 100 characters.");
        }
    }

    // ── Age ───────────────────────────────────────────────────────────────────

    public static void validateAge(int age) throws ValidationException {
        if (age < 18 || age > 65) {
            throw new ValidationException("Age",
                "Age must be between 18 and 65 for blood donation.");
        }
    }

    public static int parseAge(String ageText) throws ValidationException {
        if (ageText == null || ageText.trim().isEmpty()) {
            throw new ValidationException("Age", "Please enter the age.");
        }
        try {
            return Integer.parseInt(ageText.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Age", "Age must be a valid number.");
        }
    }

    // ── Blood Group ───────────────────────────────────────────────────────────

    public static void validateBloodGroup(String bloodGroup) throws ValidationException {
        if (bloodGroup == null || bloodGroup.trim().isEmpty()) {
            throw new ValidationException("Blood Group", "Please select a blood group.");
        }
        if (!VALID_BLOOD_GROUPS.contains(bloodGroup.trim())) {
            throw new ValidationException("Blood Group",
                "Invalid blood group. Valid groups: " + VALID_BLOOD_GROUPS);
        }
    }

    // ── Phone – STRICT 10-digit validation ───────────────────────────────────

    /**
     * Validates that the phone number contains EXACTLY 10 numeric digits.
     * Rule: ^[0-9]{10}$
     * Rejects: letters, spaces, +, -, special characters, fewer/more than 10 digits.
     */
    public static void validatePhone(String phone) throws ValidationException {
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("Mobile Number",
                "Invalid Mobile Number\n\nMobile number is required.\n\nExample: 9876543210");
        }
        if (!phone.trim().matches("^[0-9]{10}$")) {
            throw new ValidationException("Mobile Number",
                "Invalid Mobile Number\n\n"
                + "Please enter exactly 10 digits.\n"
                + "Only numbers (0–9) are allowed.\n\n"
                + "Not allowed: letters, spaces, +, -, special characters.\n\n"
                + "Example: 9876543210");
        }
    }

    // ── Email ─────────────────────────────────────────────────────────────────

    public static void validateEmail(String email) throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            return; // Email is optional
        }
        // Basic email format check
        if (!email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Email",
                "Please enter a valid email address (e.g. name@domain.com).");
        }
        if (email.trim().length() > 100) {
            throw new ValidationException("Email",
                "Email address must not exceed 100 characters.");
        }
    }

    // ── City ──────────────────────────────────────────────────────────────────

    public static void validateCity(String city) throws ValidationException {
        if (city == null || city.trim().isEmpty()) {
            throw new ValidationException("City", "Please enter the city.");
        }
        if (city.trim().length() > 50) {
            throw new ValidationException("City",
                "City name must not exceed 50 characters.");
        }
    }

    // ── Status ────────────────────────────────────────────────────────────────

    public static void validateStatus(String status) throws ValidationException {
        if (status == null || status.trim().isEmpty()) {
            throw new ValidationException("Status", "Please select the availability status.");
        }
        if (!VALID_STATUSES.contains(status.trim())) {
            throw new ValidationException("Status",
                "Invalid status. Choose 'Available' or 'Not Available'.");
        }
    }

    // ── Date ──────────────────────────────────────────────────────────────────

    /**
     * Parses a date string in yyyy-MM-dd format.
     * Returns null if the input is blank (date is optional).
     */
    public static Date parseDate(String dateText) throws ValidationException {
        if (dateText == null || dateText.trim().isEmpty()) {
            return null; // optional field
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Date date = sdf.parse(dateText.trim());
            // Date must not be in the future
            if (date.after(new Date())) {
                throw new ValidationException("Last Donation Date",
                    "Last donation date cannot be in the future.");
            }
            return date;
        } catch (ParseException e) {
            throw new ValidationException("Last Donation Date",
                "Please enter the date in yyyy-MM-dd format (e.g. 2024-06-15).");
        }
    }

    // ── Username / Password (login) ───────────────────────────────────────────

    public static void validateUsername(String username) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username", "Please enter your username.");
        }
    }

    public static void validatePassword(String password) throws ValidationException {
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password", "Please enter your password.");
        }
    }
}
