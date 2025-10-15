package com.kien.project.clinicmanagement.service;

import java.util.UUID;

import com.kien.project.clinicmanagement.dao.UserDAO;
import com.kien.project.clinicmanagement.dto.ServiceResult;
import com.kien.project.clinicmanagement.model.User;
import com.kien.project.clinicmanagement.utils.MailAPI;
import com.kien.project.clinicmanagement.utils.Validator;

public class AuthService {

    private final UserDAO userDAO;
    private String verificationCode;
    private String currentUsername;
    
    
    public AuthService() {
        userDAO = new UserDAO();
    }

    
    public ServiceResult<User> authenticate(String username, String password) {
        if (!Validator.isValidUsername(username) || password == null || password.isEmpty()) {
            return ServiceResult.failure("Invalid username or password");
        }
        User user = userDAO.checkLogin(username, password);
        if (user == null) {
            return ServiceResult.failure("Incorrect username or password");
        }
        return ServiceResult.success(user, "Login successful");
    }

    
    
    public ServiceResult<Void> sendVerificationCode(String username, String email) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            return ServiceResult.failure("User not found");
        }
        if (!email.equals(user.getEmail())) {
            return ServiceResult.failure("Email does not match the account");
        }

        String code = generateVerificationCode();
        verificationCode = code;
        currentUsername = username;

        boolean sent = MailAPI.sendVerificationCode(email, code);
        if (!sent) {
            return ServiceResult.failure("Failed to send verification code");
        }
        return ServiceResult.success(null, "Verification code has been sent to your email");
    }
    

    
    public ServiceResult<Void> verifyCode(String code) {
        if (currentUsername == null) {
            return ServiceResult.failure("No verification request found");
        }

        if (verificationCode != null && verificationCode.equals(code)) {
            return ServiceResult.success(null, "Verification successful");
        }
        return ServiceResult.failure("Invalid verification code");
    }
    

    public ServiceResult<Void> changePassword(String newPassword) {
        if (currentUsername == null) {
            return ServiceResult.failure("No user found to change password");
        }

        User user = userDAO.findByUsername(currentUsername);
        if (user == null) {
            return ServiceResult.failure("User does not exist");
        }

        userDAO.updatePassword(user.getId(), newPassword);
        
        currentUsername = null;

        return ServiceResult.success(null, "Password changed successfully");
    }
    

    public ServiceResult<Void> resetPassword(String username, String email, String citizenId) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            return ServiceResult.failure("User not found");
        }

        String newPassword = generateRandomPassword();

        boolean emailSent = MailAPI.sendResetPasswordEmail(email, newPassword);
        if (!emailSent) {
            return ServiceResult.failure("Unable to send password reset email");
        }

        userDAO.updatePassword(user.getId(), newPassword);
        return ServiceResult.success(null, "A new password has been sent to your email");
    }
    

    private String generateVerificationCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }
}
