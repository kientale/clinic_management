package com.kien.project.clinicmanagement.controller;

import java.time.LocalDate;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.kien.project.clinicmanagement.model.User;
import com.kien.project.clinicmanagement.service.UserService;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.view.user.UserFormView;
import com.kien.project.clinicmanagement.view.user.UserManagementView;
import com.kien.project.clinicmanagement.view.user.UserProfileView;

public class UserController {

	private final UserManagementView userManagementView;
	private UserFormView userFormView;
	private final UserService userService = new UserService();

	private List<User> currentPageUsers;
	private int totalUsers;
	private int totalPages;
	private int currentPage = 1;
	private final int rowsPerPage = 10;
	// Tìm kiếm
	private boolean isSearching = false;
	private String currentSearchKeyword = "";
	private String currentSearchField = "";
	private int totalSearchResults = 0;
	// Lọc
	private boolean isFiltering = false;
	private String currentRoleFilter = "All";
	private int totalRoleUsers = 0;

	public UserController(UserManagementView userManagementView) {
		this.userManagementView = userManagementView;
		loadAllUsers();
		initHeaderActions();
		initCRUDActions();
		initPagingActions();
	}

	// Hàm lấy tất cả người dùng
	public void loadAllUsers() {
	    resetState();
	    totalUsers = userService.countUsers("ALL");
	    totalPages = Math.max(1, (int) Math.ceil((double) totalUsers / rowsPerPage));
	    currentPage = 1;
	    updateTable();
	}

	// Chỉ reset biến trạng thái, không đụng UI
	private void resetState() {
	    userManagementView.getSearchField().setText("");
	    isSearching = false;
	    isFiltering = false;
	    currentSearchKeyword = "";
	    currentSearchField = "";
	    currentRoleFilter = "";
	    totalSearchResults = 0;
	    totalRoleUsers = 0;
	    currentPage = 1;
	    updateTable();
	}

	// Cập nhật bảng người dùng với phân trang
	private void updateTable() {
	    // Đảm bảo totalPages luôn đúng
	    totalUsers = getTotalCount();
	    totalPages = Math.max(1, (int) Math.ceil((double) totalUsers / rowsPerPage));

	    currentPage = Math.max(1, Math.min(currentPage, totalPages));
	    int offset = (currentPage - 1) * rowsPerPage;

	    List<User> pageUsers = fetchUsers(offset, rowsPerPage);
	    currentPageUsers = (pageUsers != null) ? pageUsers : List.of();

	    userManagementView.renderUserTable(currentPageUsers, offset);
	    userManagementView.updatePageInfo(currentPage, totalPages);
	}

	private List<User> fetchUsers(int offset, int limit) {
	    if (isSearching)
	        return userService.searchUsers(currentSearchKeyword, currentSearchField, "All", offset, limit);
	    if (isFiltering)
	        return userService.filterUsersByRole(currentRoleFilter, offset, limit);
	    return userService.getUsers("ALL", offset, limit);
	}

	private int getTotalCount() {
	    if (isSearching)
	        return totalSearchResults;
	    if (isFiltering)
	        return totalRoleUsers;
	    return totalUsers;
	}

	// Gắn sự kiện cho header
	public void initHeaderActions() {
		// ----- Gắn sự kiện nút phần header ------
		userManagementView.getSearchButton().addActionListener(e -> searchUsers());
		userManagementView.getRefreshButton().addActionListener(e -> resetState());
		userManagementView.getFilterUserByRoleComboBox().addActionListener(e -> filterUserByRole());
		// ----- Gắn sự kiện cho bàn phím ------
		userManagementView.getSearchField().addActionListener(e -> userManagementView.getSearchButton().doClick());
	}

	// Tìm kiếm người dùng
	private void searchUsers() {
	    currentSearchKeyword = userManagementView.getSearchField().getText().trim();
	    currentSearchField = (String) userManagementView.getSearchTypeCombo().getSelectedItem();

	    // Nếu ô tìm kiếm trống -> trở về danh sách gốc
	    if (currentSearchKeyword.isEmpty()) {
	        loadAllUsers();
	        return;
	    }

	    // Đặt trạng thái tìm kiếm
	    isSearching = true;
	    isFiltering = false; // tránh xung đột
	    totalSearchResults = userService.countSearchUsers(currentSearchKeyword, currentSearchField, "ALL");

	    // Không có kết quả
	    if (totalSearchResults <= 0) {
	        handleNoResults("No users found!");
	        return;
	    }

	    currentPage = 1;
	    updateTable();
	}

	// Lọc người dùng theo vai trò
	public void filterUserByRole() {
	    currentRoleFilter = (String) userManagementView.getFilterUserByRoleComboBox().getSelectedItem();

	    // Nếu chọn "All" -> trở về danh sách gốc
	    if ("All".equalsIgnoreCase(currentRoleFilter)) {
	        loadAllUsers();
	        return;
	    }

	    // Đặt trạng thái lọc
	    isFiltering = true;
	    isSearching = false; // tránh xung đột
	    totalRoleUsers = userService.countUsersByRole(currentRoleFilter);

	    // Không có kết quả
	    if (totalRoleUsers <= 0) {
	        handleNoResults("No users found for selected role!");
	        return;
	    }

	    currentPage = 1;
	    updateTable();
	}

	// Xử lý khi không có kết quả tìm kiếm hoặc lọc
	private void handleNoResults(String message) {
	    userManagementView.showInfo(message);
	    renderEmptyTable();
	}

	private void renderEmptyTable() {
	    userManagementView.renderUserTable(List.of(), 0);
	    userManagementView.updatePageInfo(1, 1);
	}


	// Gắn sự kiện cho CRUD
	public void initCRUDActions() {
		// ----- Gắn sự kiện phần footer ------
		userManagementView.getAddNewUserButton().addActionListener(e -> showUserForm(null));
		userManagementView.getEditUserButton()
				.addActionListener(e -> editSelectedUser(userManagementView.getUserTable().getSelectedRow()));
		userManagementView.getDeleteUserButton()
				.addActionListener(e -> deleteSelectedUser(userManagementView.getUserTable().getSelectedRow()));
		userManagementView.getResetPasswordButton()
				.addActionListener(e -> resetPassword(userManagementView.getUserTable().getSelectedRow()));
		userManagementView.getViewActivityLogButton()
				.addActionListener(e -> viewActivityLog(userManagementView.getUserTable().getSelectedRow()));
		userManagementView.getViewUserDetailButton()
				.addActionListener(e -> showUserDetail(userManagementView.getUserTable().getSelectedRow()));
	}

	// Code thêm User
	public void showUserForm(User user) {
		JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(userManagementView);
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
		boolean isUpdate = userService.isExistingUser(user.getCode());

		String validationErrors = validateUserInput(user, isUpdate);
		if (!validationErrors.isEmpty()) {
			userFormView.showWarning("Please fix the following errors:\n\n" + validationErrors);
			return;
		}

		userService.saveOrUpdateUser(user);
		userFormView.setSaved(true);
		userFormView.dispose();

		// Nếu thêm mới -> trở về trang đầu
		if (!isUpdate) {
			currentPage = 1;
		}
		updateTable();

		userManagementView.showInfo("User saved successfully.");
	}

	// Chuẩn bị dữ liệu user từ form
	private User prepareUserFromForm() {
		User user = userFormView.getUser();

		if (user == null) { // thêm mới
			user = new User();
			user.setCode(userService.generateNextUserCode());
			user.setPasswordHash(
					userService.hashPassword(new String(userFormView.getPasswordField().getPassword()).trim()));

			// Nếu chưa chọn avatar thì gán mặc định
			user.setProfileImage("/images/for_avatar/avt_default.png");
		}

		// Điền thông tin từ form vào user
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

	// Code làm việc với người dùng trong bảng
	private boolean isRowSelected(int rowIndex) {
		if (currentPageUsers == null || currentPageUsers.isEmpty()) {
			userManagementView.showWarning("No users available.");
			return false;
		}
		if (rowIndex < 0 || rowIndex >= currentPageUsers.size()) {
			userManagementView.showWarning("Please select a valid user.");
			return false;
		}
		return true;
	}

	private User getSelectedUser(int rowIndex) {
		return currentPageUsers.get(rowIndex);
	}

	// Chỉnh sửa người dùng
	public void editSelectedUser(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		User selected = getSelectedUser(rowIndex);
		User fullUser = userService.getByUserCode(selected.getCode());

		if (fullUser == null) {
			userManagementView.showError("User not found.");
			return;
		}

		showUserForm(fullUser);
	}

	// Xóa người dùng
	public void deleteSelectedUser(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		User user = getSelectedUser(rowIndex);

		if (!userManagementView.confirmDeleteUser(user))
			return;

		String result = userService.deleteUser(user.getCode());

		if (result == null) {
			adjustPaginationAfterDelete();
			updateTable();
			userManagementView.showInfo("User deleted successfully.");
		} else {
			userManagementView.showError(result);
		}
	}

	private void adjustPaginationAfterDelete() {
		int totalUsers = userService.countUsers("ALL");
		int totalPages = Math.max(1, (int) Math.ceil((double) totalUsers / rowsPerPage));
		if (currentPage > totalPages)
			currentPage = totalPages;
	}

	// Reset password
	public void resetPassword(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		User user = getSelectedUser(rowIndex);
		if (!userManagementView.confirmResetUserPassword(user))
			return;

		boolean success = userService.resetPassword(user, userManagementView);
		String message = "Password for user " + user.getUsername()
				+ (success ? " has been reset." : " could not be reset.");
		if (success)
			userManagementView.showInfo(message);
		else
			userManagementView.showError(message);
	}

	private void showUserDetail(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		User selected = getSelectedUser(rowIndex);
		User user = userService.getByUserCode(selected.getCode());

		if (user == null) {
			userManagementView.showError("User not found.");
			return;
		}

		UserProfileView profileView = new UserProfileView((JFrame) SwingUtilities.getWindowAncestor(userManagementView),
				user);
		new UserProfileController(profileView);

		profileView.getChangePasswordButton().setVisible(false);
		profileView.setVisible(true);
	}

	public void viewActivityLog(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		User user = getSelectedUser(rowIndex);
		List<String> logs = userService.getActivityLogs(user.getCode());
		userManagementView.showActivityLog(user, logs);
	}

	public void initPagingActions() {
		// ----- Gắn sự kiện phần phân trang ------
		userManagementView.getPrevPageButton().addActionListener(e -> previousPage());
		userManagementView.getNextPageButton().addActionListener(e -> nextPage());
	}

	public void nextPage() {
		if (currentPage < totalPages) {
			currentPage++;
			updateTable();
		}
	}

	public void previousPage() {
		if (currentPage > 1) {
			currentPage--;
			updateTable();
		}
	}
}
