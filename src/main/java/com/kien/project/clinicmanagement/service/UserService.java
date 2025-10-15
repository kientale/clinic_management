package com.kien.project.clinicmanagement.service;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.mindrot.jbcrypt.BCrypt;

import com.kien.project.clinicmanagement.dao.SystemLogDAO;
import com.kien.project.clinicmanagement.dao.UserDAO;
import com.kien.project.clinicmanagement.model.User;
import com.kien.project.clinicmanagement.utils.MailAPI;
import com.kien.project.clinicmanagement.utils.Session;
import com.toedter.calendar.JDateChooser;

public class UserService {

	private final UserDAO userDAO = new UserDAO();
	private final SystemLogDAO logDAO = new SystemLogDAO();
	
	

	// Hàm lấy người dùng
	public int countUsers(String role) {
		return userDAO.countUsers(role);
	} 

	public List<User> getUsers(String role, int offset, int limit) {
		return userDAO.getUsers(role, offset, limit);
	}

	// Hàm tìm kiếm người dùng
	public List<User> searchUsers(String keyword, String field, String role, int offset, int limit) {
		logDAO.logAction(Session.getCurrentUser().getCode(), "Search user");
		return userDAO.searchUsers(keyword, field, role, offset, limit);
	}

	public int countSearchUsers(String keyword, String field, String role) {
		return userDAO.countSearchUsers(keyword, field, role);
	}

	public List<User> filterUsersByRole(String role, int offset, int limit) {
	    return userDAO.getUsers(role, offset, limit);
	}

	public int countUsersByRole(String role) {
	    return userDAO.countUsers(role);
	}

	
	
	// Code logic CRUD
	public User getByUserCode(String code) {
		return userDAO.getByUserCode(code);
	}

	public boolean isExistingUser(String userCode) {
		return userDAO.isExistingUser(userCode);
	}

	// Kiểm tra trùng lặp thông tin
	public boolean isUsernameTaken(String username, String excludeCode) {
		return userDAO.isUsernameTaken(username, excludeCode);
	}

	public boolean isEmailTaken(String email, String excludeCode) {
		return userDAO.isEmailTaken(email, excludeCode);
	}

	public boolean isPhoneNumberTaken(String phone, String excludeCode) {
		return userDAO.isPhoneNumberTaken(phone, excludeCode);
	}

	public boolean isCitizenIdTaken(String citizenId, String excludeCode) {
		return userDAO.isCitizenIdTaken(citizenId, excludeCode);
	}

	// Thêm hoặc sửa ngưởi dùng
	public void saveOrUpdateUser(User user) {
		boolean isUpdate = isExistingUser(user.getCode());
		String userCode = Session.getCurrentUser().getCode();

		if (isUpdate) {
			logDAO.logAction(userCode, "Update user");
			userDAO.updateUser(user);
		} else {
			logDAO.logAction(userCode, "Add new user");
			userDAO.addUser(user);
		}
	}

	// XÓa người dùng
	public String deleteUser(String userCode) {
	    logDAO.logAction(Session.getCurrentUser().getCode(), "Delete user");
	    try {
	        return userDAO.deleteUser(userCode); // Trả về thông báo lỗi (null = thành công)
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "Unexpected error: " + e.getMessage();
	    }
	}

	// Reset mật khẩu
	public boolean resetPassword(User user, JPanel parentPanel) {
		String newPassword = generateRandomPassword();
		String hashedPassword = hashPassword(newPassword);

		boolean success = userDAO.updatePasswordByUserCode(user.getCode(), hashedPassword);
		logDAO.logAction(Session.getCurrentUser().getCode(), "Reset user password");

		if (success) {
			MailAPI.sendResetPasswordEmail(user.getEmail(), newPassword);
		}
		return success;
	}

	public String hashPassword(String rawPassword) {
		return BCrypt.hashpw(rawPassword.trim(), BCrypt.gensalt());
	}

	public String generateRandomPassword() {
		return UUID.randomUUID().toString().replace("-", "").substring(0, 6);
	}

	public List<String> getActivityLogs(String userCode) {
		return userDAO.getActivityLogs(userCode);
	}

	public String generateNextUserCode() {
		return userDAO.generateNextUserCode();
	}

	// Hàm gắn dữ liệu vào form nếu sửa người dùng
	public void fillUserData(User user, JTextField nameField, JTextField usernameField, JComboBox<String> genderCombo,
			JDateChooser dobChooser, JTextField emailField, JTextField phoneField, JTextField addressField,
			JTextField citizenIdField, JComboBox<String> roleCombo) {
		user.setName(nameField.getText().trim());
		user.setUsername(usernameField.getText().trim());
		user.setGender((String) genderCombo.getSelectedItem());
		user.setDateOfBirth(dobChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		user.setEmail(emailField.getText().trim());
		user.setPhoneNumber(phoneField.getText().trim());
		user.setAddress(addressField.getText().trim());
		user.setCitizenId(citizenIdField.getText().trim());
		user.setRole((String) roleCombo.getSelectedItem());
	}

	public boolean updatePassword(User user, String hashedPassword) {
		if (hashedPassword == null || hashedPassword.trim().isEmpty()) {
			return false; // Hoặc ném exception
		}

		boolean success = userDAO.updatePasswordByUserCode(user.getCode(), hashedPassword);

		if (success) {
			logDAO.logAction(Session.getCurrentUser().getCode(), "Change own password");
		}
		return success;
	}

	// ================== Hàm thống kê User ====================
	public int getTotalUsers() {
		List<User> all = userDAO.getAllUsers();
		return all != null ? all.size() : 0;
	}

	public int countByGender(String gender) {
		List<User> all = userDAO.getAllUsers();
		if (all == null)
			return 0;
		return (int) all.stream().filter(u -> u.getGender() != null && u.getGender().equalsIgnoreCase(gender)).count();
	}

	public int[] getUserStatistics() {
		List<User> all = userDAO.getAllUsers();
		int total = 0, male = 0, female = 0;
		if (all != null) {
			total = all.size();
			for (User u : all) {
				if (u.getGender() != null) {
					if (u.getGender().equalsIgnoreCase("Male"))
						male++;
					else if (u.getGender().equalsIgnoreCase("Female"))
						female++;
				}
			}
		}
		return new int[] { total, male, female };
	}
}
