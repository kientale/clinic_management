package com.kien.project.clinicmanagement.utils;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class Validator {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} .'-]+$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("0\\d{9}");
    private static final Pattern CITIZEN_PATTERN = Pattern.compile("\\d{9}|\\d{12}");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,}$");

    
    // ---------- Các validate chung ----------
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidCitizenId(String citizenId) {
        return citizenId != null && CITIZEN_PATTERN.matcher(citizenId.trim()).matches();
    }

    public static boolean isValidDob(LocalDate dob) {
        return dob != null && !dob.isAfter(LocalDate.now());
    }

    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password.trim()).matches();
    }
    
    public static boolean isValidVerificationCode(String code) {
        return code != null && code.trim().length() == 6;
    }

    public static boolean isValidChangePassword(String newPassword, String confirmPassword) {
        return isValidPassword(newPassword) && newPassword.equals(confirmPassword);
    }
}
