package com.kien.project.clinicmanagement.controller;

import java.awt.Window;
import java.time.LocalDate;
import javax.swing.SwingUtilities;

import org.mindrot.jbcrypt.BCrypt;

import com.kien.project.clinicmanagement.model.User;
import com.kien.project.clinicmanagement.service.UserService;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.view.user.ChangePasswordView;
import com.kien.project.clinicmanagement.view.user.UserFormView;
import com.kien.project.clinicmanagement.view.user.UserProfileView;

public class UserProfileController {

	private final UserProfileView userProfileView;
	private UserFormView userFormView;
	private ChangePasswordView changePasswordView;
	private final UserService userService = new UserService();

	// ----------------- Constructor -----------------
	public UserProfileController(UserProfileView userProfileView) {
		this.userProfileView = userProfileView;
		initUserProfileActions();
	}

	public void initUserProfileActions() {
		userProfileView.getCloseButton().addActionListener(e -> userProfileView.dispose());
		userProfileView.getEditProfileButton().addActionListener(e -> showUserForm(userProfileView.getUser()));
		userProfileView.getChangePasswordButton()
				.addActionListener(e -> showChangePasswordView(userProfileView.getUser()));
	}

	public void showUserForm(User user) {
	    Window owner = SwingUtilities.getWindowAncestor(userProfileView);
	    userFormView = new UserFormView(owner, user);

	    initUserFormActions();
	    applyRealtimeValidation();

	    userFormView.showUserForm();
	}

	private void initUserFormActions() {
		userFormView.getSaveButton().addActionListener(e -> onSaveButtonClicked());
		userFormView.getCancelButton().addActionListener(e -> userFormView.dispose());
	}

	private void applyRealtimeValidation() {
		FormUtilities.validateOnFocusLost(userFormView.getNameField(), text -> text.matches("^[\\p{L} .'-]+$"),
				"Full name must not contain digits or special characters.");

		FormUtilities.validateOnFocusLost(userFormView.getUsernameField(), text -> text.matches("^[a-zA-Z0-9_]{4,}$"),
				"Username must be at least 4 characters with no special characters.");

		FormUtilities.validateOnFocusLost(userFormView.getEmailField(),
				text -> text.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$"), "Invalid email format.");

		FormUtilities.validateOnFocusLost(userFormView.getPhoneNumberField(), text -> text.matches("0\\d{9}"),
				"Phone number must start with 0 and be 10 digits.");

		FormUtilities.validateOnFocusLost(userFormView.getCitizenIdField(), text -> text.matches("\\d{9}|\\d{12}"),
				"Citizen ID must be 9 or 12 digits.");
	}

	// Nút lưu
	private void onSaveButtonClicked() {
		User user = prepareUserFromForm();
		boolean isUpdate = (user.getCode() != null && userService.isExistingUser(user.getCode()));

		String validationErrors = validateUserInput(user, isUpdate);
		if (!validationErrors.isEmpty()) {
			userFormView.showWarning("Please fix the following errors:\n\n" + validationErrors);
			return;
		}

		userService.saveOrUpdateUser(user);
		userFormView.setSaved(true);
		userFormView.dispose();

		userProfileView.showInfo("User saved successfully.");
	}

	private User prepareUserFromForm() {
		User user = userFormView.getUser();
		boolean isNew = (user == null);

		if (isNew) {
			user = new User();
			user.setCode(userService.generateNextUserCode());
			user.setPasswordHash(
					userService.hashPassword(new String(userFormView.getPasswordField().getPassword()).trim()));
			user.setProfileImage("/images/for_avatar/avt_default.png");
			userFormView.setUser(user);
		}

		userService.fillUserData(user, userFormView.getNameField(), userFormView.getUsernameField(),
				userFormView.getGenderComboBox(), userFormView.getDobChooser(), userFormView.getEmailField(),
				userFormView.getPhoneNumberField(), userFormView.getAddressField(), userFormView.getCitizenIdField(),
				userFormView.getRoleComboBox());

		return user;
	}

	private String validateUserInput(User user, boolean isUpdate) {
		StringBuilder errors = new StringBuilder();

		// --- Validate nhập liệu cơ bản ---
		String name = user.getName().trim();
		String username = user.getUsername().trim();
		String email = user.getEmail().trim();
		String phone = user.getPhoneNumber().trim();
		String address = user.getAddress().trim();
		String citizenId = user.getCitizenId().trim();
		LocalDate dob = user.getDateOfBirth();
		String rawPassword = user.getPasswordHash() != null ? user.getPasswordHash().trim() : "";

		if (FormUtilities.isEmpty(name, username, email, phone, address, citizenId) || dob == null) {
			errors.append("- Please fill in all required fields and select a date of birth.\n");
		}

		if (!name.matches("^[\\p{L} .'-]+$"))
			errors.append("- Full name must not contain digits or special characters.\n");

		if (!username.matches("^[a-zA-Z0-9_]{4,}$"))
			errors.append("- Username must be at least 4 characters and contain no special characters.\n");

		if (!isUpdate && rawPassword.length() < 6)
			errors.append("- Password must be at least 6 characters long.\n");

		if (dob != null && dob.isAfter(LocalDate.now()))
			errors.append("- Date of birth cannot be in the future.\n");

		if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
			errors.append("- Invalid email format.\n");

		if (!phone.matches("0\\d{9}"))
			errors.append("- Phone number must start with 0 and be 10 digits.\n");

		if (!citizenId.matches("\\d{9}|\\d{12}"))
			errors.append("- Citizen ID must be 9 or 12 digits.\n");

		// --- Validate logic nghiệp vụ (gọi sang DAO để kiểm tra trùng) ---
		String duplicateError = checkDuplicateUserFields(user, isUpdate);
		if (duplicateError != null && !duplicateError.isEmpty()) {
			errors.append(duplicateError);
		}

		return errors.toString();
	}

	private String checkDuplicateUserFields(User user, boolean isUpdate) {
		StringBuilder errors = new StringBuilder();
		String code = user.getCode();

		if (userService.isUsernameTaken(user.getUsername(), isUpdate ? code : null))
			errors.append("- Username is already taken.\n");
		if (userService.isEmailTaken(user.getEmail(), isUpdate ? code : null))
			errors.append("- Email is already registered.\n");
		if (userService.isPhoneNumberTaken(user.getPhoneNumber(), isUpdate ? code : null))
			errors.append("- Phone number is already registered.\n");
		if (userService.isCitizenIdTaken(user.getCitizenId(), isUpdate ? code : null))
			errors.append("- Citizen ID is already registered.\n");

		return errors.toString();
	}

	private void showChangePasswordView(User user) {
		Window owner = SwingUtilities.getWindowAncestor(userProfileView);
		changePasswordView = new ChangePasswordView(owner, user);

		initChangePasswordActions();
		applyRealtimeValidationForChangePassword();

		userProfileView.dispose();
		changePasswordView.setVisible(true);
	}

	public void initChangePasswordActions() {
		changePasswordView.getChangePasswordButton().addActionListener(e -> updatePassword());
		changePasswordView.getShowPasswordCheckBox().addActionListener(e -> showPassword());
		changePasswordView.getCancelButton().addActionListener(e -> changePasswordView.dispose());
	}

	// Hàm hiển thị mật khẩu
	private void showPassword() {
		char echoChar = changePasswordView.getShowPasswordCheckBox().isSelected() ? 0 : '•';
		changePasswordView.getOldPasswordField().setEchoChar(echoChar);
		changePasswordView.getNewPasswordField().setEchoChar(echoChar);
		changePasswordView.getConfirmPasswordField().setEchoChar(echoChar);
	}

	// Hàm cập nhật mật khẩu
	public void updatePassword() {
		User user = changePasswordView.getUser();

		String oldPassword = changePasswordView.getOldPassword().trim();
		if (!BCrypt.checkpw(oldPassword, user.getPasswordHash())) {
			changePasswordView.showError("The old password is wrong!");
			return;
		}

		String rawPassword = changePasswordView.getNewPassword();
		// Kiểm tra mật khẩu hợp lệ: ít nhất 6 ký tự, có số và chữ
		if (rawPassword == null || !rawPassword.matches("^(?=.*[0-9]).{6,}$")) {
			changePasswordView.showError("Invalid password!");
			return; // Không hợp lệ
		}

		if (!changePasswordView.getNewPassword().equals(changePasswordView.getConfirmPassword())) {
			changePasswordView.showError("The password do not match!");
			return;

		}

		String hashedPassword = userService.hashPassword(rawPassword);
		boolean success = userService.updatePassword(user, hashedPassword);
		if (success) {
			changePasswordView.showInfo("Password changed successfully");
			user.setPasswordHash(hashedPassword);
			changePasswordView.dispose();
		}
	}

	private void applyRealtimeValidationForChangePassword() {
		// Kiểm tra mật khẩu mới
		FormUtilities.validateOnFocusLost(changePasswordView.getNewPasswordField(),
				text -> text.matches("^(?=.*[0-9]).{6,}$"),
				"Password must be at least 6 characters and contain at least one number.");

		// Kiểm tra xác nhận mật khẩu
		FormUtilities.validateOnFocusLost(changePasswordView.getConfirmPasswordField(),
				text -> text.equals(changePasswordView.getNewPassword().trim()), "Passwords do not match.");
	}
}
