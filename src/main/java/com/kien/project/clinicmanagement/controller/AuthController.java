package com.kien.project.clinicmanagement.controller;

import com.kien.project.clinicmanagement.dto.ServiceResult;
import com.kien.project.clinicmanagement.model.User;
import com.kien.project.clinicmanagement.service.AuthService;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.Session;
import com.kien.project.clinicmanagement.utils.Validator;
import com.kien.project.clinicmanagement.view.auth.ForgotPasswordView;
import com.kien.project.clinicmanagement.view.auth.LoginView;
import com.kien.project.clinicmanagement.view.homepage.HomePageView;

public class AuthController {

    private final LoginView loginView;
    private ForgotPasswordView forgotPasswordView;
    private final AuthService authService;

    
    public AuthController() {
        this.authService = new AuthService();
        this.loginView = new LoginView();

        // Gắn sự kiện cho Login và Validate dữ liệu trực tiếp
        initLoginActions();
        applyRealtimeValidationForLoginView();
    }
    
    
    // Hàm gắn sự kiện cho các nút
    private void initLoginActions() {
        loginView.getCheckBoxShowPassword().addActionListener(e -> togglePasswordForLoginView(loginView));
        loginView.getLoginButton().addActionListener(e -> handleLogin());
        loginView.getForgotPasswordButton().addActionListener(e -> openForgotPasswordView());
        loginView.getPasswordField().addActionListener(e -> loginView.getLoginButton().doClick());
    }
    
    
    private void applyRealtimeValidationForLoginView() {
        FormUtilities.validateOnFocusLost(
                loginView.getUsernameField(),
                Validator::isValidUsername,
                "Username must be at least 4 characters with no special characters."
        );
    }

    
    // Hàm hiển thị mật khẩu cho JPasswordField
    private void togglePasswordForLoginView(LoginView panel) {
        char echoChar = panel.getCheckBoxShowPassword().isSelected() ? 0 : '•';
        panel.getPasswordField().setEchoChar(echoChar);
    }
    

    // Hàm xử lý đăng nhập
    private void handleLogin() {
        String username = loginView.getUsername().trim();
        String password = loginView.getPassword().trim();

        if (FormUtilities.isEmpty(username, password)) {
            loginView.showValidationMessage("Please enter both username and password.");
            return;
        }

        if (!(Validator.isValidUsername(username) && Validator.isValidPassword(password))) {
            loginView.showValidationMessage("Invalid username or password format.");
            return;
        }

        ServiceResult<User> result = authService.authenticate(username, password);
        if (!result.isSuccess()) {
            loginView.showValidationMessage(result.getMessage());
            return;
        }

        User user = result.getData();
        loginView.showInfoMessage(result.getMessage());
        Session.setCurrentUser(user);

        loginView.close();
        showHomePage(user);
    }
    
    
    private void openForgotPasswordView() {
        forgotPasswordView = new ForgotPasswordView();
        initForgotPasswordActions();
        applyRealtimeValidationForForgotPasswordView();
        showForgotPasswordView();
    }

    
    private void initForgotPasswordActions() {
        // Step 1
        forgotPasswordView.getSendCodeButton().addActionListener(e -> handleSendCode());

        // Step 2
        forgotPasswordView.getVerifyCodeButton().addActionListener(e -> handleVerifyCode());

        // Step 3
        forgotPasswordView.getChangePasswordButton().addActionListener(e -> handleChangePassword());

        // Common
        forgotPasswordView.getShowPasswordCheckBox().addActionListener(e -> togglePasswordVisibility(forgotPasswordView));
        forgotPasswordView.getBackButton().addActionListener(e -> forgotPasswordView.dispose());
        forgotPasswordView.getBackStep2Button().addActionListener(e -> forgotPasswordView.showStep1());
        forgotPasswordView.getBackStep3Button().addActionListener(e -> forgotPasswordView.showStep2());

        // Enter key actions
        forgotPasswordView.getEmailField().addActionListener(e -> forgotPasswordView.getSendCodeButton().doClick());
        forgotPasswordView.getVerificationCodeField().addActionListener(e -> forgotPasswordView.getVerifyCodeButton().doClick());
        forgotPasswordView.getConfirmPasswordField().addActionListener(e -> forgotPasswordView.getChangePasswordButton().doClick());
    }

    private void handleSendCode() {
        String username = forgotPasswordView.getUsername().trim();
        String email = forgotPasswordView.getEmail().trim();

        if (FormUtilities.isEmpty(username, email)) {
            forgotPasswordView.showWarningMessage("Please fill in all fields.");
            return;
        }

        if (!(Validator.isValidUsername(username) && Validator.isValidEmail(email))) {
            forgotPasswordView.showWarningMessage("Invalid username or email format.");
            return;
        }

        ServiceResult<Void> result = authService.sendVerificationCode(username, email);
        if (result.isSuccess()) {
            forgotPasswordView.showInfoMessage(result.getMessage());
            forgotPasswordView.showStep2();
        } else {
            forgotPasswordView.showErrorMessage(result.getMessage());
        }
    }

    private void handleVerifyCode() {
        String code = forgotPasswordView.getVerificationCode().trim();

        if (FormUtilities.isEmpty(code)) {
            forgotPasswordView.showWarningMessage("Please enter the verification code.");
            return;
        }

        if (!Validator.isValidVerificationCode(code)) {
            forgotPasswordView.showWarningMessage("Verification code must be 6 digits.");
            return;
        }

        ServiceResult<Void> result = authService.verifyCode(code);
        if (result.isSuccess()) {
            forgotPasswordView.showInfoMessage(result.getMessage());
            forgotPasswordView.showStep3();
        } else {
            forgotPasswordView.showErrorMessage(result.getMessage());
        }
    }

    private void handleChangePassword() {
        String newPassword = forgotPasswordView.getNewPassword().trim();
        String confirmPassword = forgotPasswordView.getConfirmPassword().trim();

        if (FormUtilities.isEmpty(newPassword, confirmPassword)) {
            forgotPasswordView.showWarningMessage("Please fill in all fields.");
            return;
        }

        if (!Validator.isValidChangePassword(newPassword, confirmPassword)) {
            forgotPasswordView.showWarningMessage("Passwords do not match or not strong enough.");
            return;
        }

        ServiceResult<Void> result = authService.changePassword(newPassword);
        if (result.isSuccess()) {
            forgotPasswordView.showInfoMessage(result.getMessage());
            forgotPasswordView.close();
        } else {
            forgotPasswordView.showErrorMessage(result.getMessage());
        }
    }

    private void applyRealtimeValidationForForgotPasswordView() {
        // Step 1
        FormUtilities.validateOnFocusLost(
                forgotPasswordView.getUsernameField(),
                Validator::isValidUsername,
                "Username must be at least 4 characters with no special characters."
        );

        FormUtilities.validateOnFocusLost(
                forgotPasswordView.getEmailField(),
                Validator::isValidEmail,
                "Invalid email format."
        );

        // Step 2
        FormUtilities.validateOnFocusLost(
                forgotPasswordView.getVerificationCodeField(),
                Validator::isValidVerificationCode,
                "Verification code must be 6 digits."
        );

        // Step 3
        FormUtilities.validateOnFocusLost(
                forgotPasswordView.getNewPasswordField(),
                Validator::isValidPassword,
                "Password must be at least 6 characters."
        );

        FormUtilities.validateOnFocusLost(
                forgotPasswordView.getConfirmPasswordField(),
                text -> Validator.isValidChangePassword(forgotPasswordView.getNewPassword().trim(), text),
                "Passwords do not match."
        );
    }

    private void togglePasswordVisibility(ForgotPasswordView panel) {
        char echoChar = panel.getShowPasswordCheckBox().isSelected() ? 0 : '•';
        panel.getNewPasswordField().setEchoChar(echoChar);
        panel.getConfirmPasswordField().setEchoChar(echoChar);
    }
    
    // Hàm hiển thị Views
    public void showLoginView() {
        loginView.setVisible(true);
    }

    public void showForgotPasswordView() {
        if (forgotPasswordView != null) {
            forgotPasswordView.setVisible(true);
        }
    }

    
    private void showHomePage(User user) {
        HomePageView homePageView = new HomePageView(user);
        new HomePageController(homePageView, loginView);
    }
}
