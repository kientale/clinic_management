package com.kien.project.clinicmanagement.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import com.kien.project.clinicmanagement.model.User;
import com.kien.project.clinicmanagement.utils.ConnectionDatabase;

public class UserDAO {

	// Code Auth
	private static final String USERNAME_QUERY = """
				SELECT a.*, p.name, p.email, p.phone_number, p.address,
				       p.date_of_birth, p.gender, p.citizen_id, p.profile_image
				FROM account a
				JOIN profile p ON a.code = p.user_code
				WHERE a.username = ?
			""";

	public User checkLogin(String username, String password) {
		try (Connection connection = ConnectionDatabase.getConnection();
				PreparedStatement statement = connection.prepareStatement(USERNAME_QUERY)) {

			statement.setString(1, username);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					String hashedPassword = resultSet.getString("password_hash");
					if (BCrypt.checkpw(password, hashedPassword)) {
						return extractUserFromResultSet(resultSet);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public User findByUsername(String username) {
		try (Connection connection = ConnectionDatabase.getConnection();
				PreparedStatement statement = connection.prepareStatement(USERNAME_QUERY)) {

			statement.setString(1, username);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return extractUserFromResultSet(resultSet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean updatePassword(Long userId, String newPassword) {
		final String sql = "UPDATE account SET password_hash = ? WHERE id = ?";
		final String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, hashed);
			stmt.setLong(2, userId);
			return stmt.executeUpdate() > 0;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	
	
	
	
	// Code UserManagement
	private static final String BASE_SELECT = """
			    SELECT a.*, p.name, p.email, p.phone_number, p.address,
			           p.date_of_birth, p.gender, p.citizen_id, p.profile_image
			    FROM account a
			    JOIN profile p ON a.code = p.user_code
			""";

	private static final String BASE_COUNT = """
			    SELECT COUNT(*)
			    FROM account a
			    JOIN profile p ON a.code = p.user_code
			""";
	
	private boolean hasRoleFilter(String role) {
		return role != null && !"ALL".equalsIgnoreCase(role);
	}

	private String mapSearchField(String field) {
		if (field == null)
			return "p.name";
		return switch (field.toLowerCase()) {
		case "search by address" -> "p.address";
		case "search by phone number" -> "p.phone_number";
		case "search by citizen id" -> "p.citizen_id";
		default -> "p.name";
		};
	}
	
	// Lấy danh sách người dùng
	public List<User> getUsers(String role, int offset, int limit) {
		List<User> list = new ArrayList<>();

		StringBuilder sql = new StringBuilder(BASE_SELECT);
		if (hasRoleFilter(role))
			sql.append(" WHERE a.role = ?");
		sql.append(" ORDER BY a.id DESC LIMIT ? OFFSET ?");

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			int i = 1;
			if (hasRoleFilter(role))
				stmt.setString(i++, role);
			stmt.setInt(i++, limit);
			stmt.setInt(i, offset);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next())
					list.add(extractUserFromResultSet(rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// Hàm lấy tổng số người dùng
	public int countUsers(String role) {
		StringBuilder sql = new StringBuilder(BASE_COUNT);
		if (hasRoleFilter(role))
			sql.append(" WHERE a.role = ?");

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			if (hasRoleFilter(role))
				stmt.setString(1, role);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<User> searchUsers(String keyword, String field, String role, int offset, int limit) {
		List<User> list = new ArrayList<>();

		String column = mapSearchField(field);

		StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE ").append(column).append(" LIKE ?");

		if (hasRoleFilter(role))
			sql.append(" AND a.role = ?");
		sql.append(" ORDER BY a.id DESC LIMIT ? OFFSET ?");

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			int i = 1;
			stmt.setString(i++, "%" + keyword + "%");
			if (hasRoleFilter(role))
				stmt.setString(i++, role);
			stmt.setInt(i++, limit);
			stmt.setInt(i, offset);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next())
					list.add(extractUserFromResultSet(rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public int countSearchUsers(String keyword, String field, String role) {
		String column = mapSearchField(field);

		StringBuilder sql = new StringBuilder(BASE_COUNT).append(" WHERE ").append(column).append(" LIKE ?");

		if (hasRoleFilter(role))
			sql.append(" AND a.role = ?");

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			int i = 1;
			stmt.setString(i++, "%" + keyword + "%");
			if (hasRoleFilter(role))
				stmt.setString(i, role);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	

	public List<User> getAllUsers() {
		List<User> list = new ArrayList<>();
		String sql = """
				    SELECT a.*, p.name, p.email, p.phone_number, p.address,
				           p.date_of_birth, p.gender, p.citizen_id, p.profile_image
				    FROM account a
				    JOIN profile p ON a.code = p.user_code
				""";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				list.add(extractUserFromResultSet(rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public void addUser(User user) {
		// insert lần lượt vào 2 bảng
		String accountSql = "INSERT INTO account (username, password_hash, code, role) VALUES (?, ?, ?, ?)";
		String profileSql = """
				    INSERT INTO profile (user_code, name, email, phone_number, address, date_of_birth, gender, citizen_id, profile_image)
				    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
				""";

		try (Connection conn = ConnectionDatabase.getConnection()) {
			conn.setAutoCommit(false); // Bắt đầu giao dịch thủ công

			try (PreparedStatement accStmt = conn.prepareStatement(accountSql);
					PreparedStatement proStmt = conn.prepareStatement(profileSql)) {

				// Insert vào bảng account
				accStmt.setString(1, user.getUsername());
				accStmt.setString(2, user.getPasswordHash());
				accStmt.setString(3, user.getCode());
				accStmt.setString(4, user.getRole());
				accStmt.executeUpdate();

				// Insert vào bảng profile
				proStmt.setString(1, user.getCode());
				proStmt.setString(2, user.getName());
				proStmt.setString(3, user.getEmail());
				proStmt.setString(4, user.getPhoneNumber());
				proStmt.setString(5, user.getAddress());
				proStmt.setDate(6, Date.valueOf(user.getDateOfBirth()));
				proStmt.setString(7, user.getGender());
				proStmt.setString(8, user.getCitizenId());
				proStmt.setString(9, user.getProfileImage());
				proStmt.executeUpdate();

				conn.commit(); // Commit nếu mọi thứ thành công
			} catch (Exception e) {
				conn.rollback(); // Rollback nếu có lỗi
				System.err.println("Error during addUser transaction. Rolling back...");
				e.printStackTrace();
			} finally {
				conn.setAutoCommit(true); // Đảm bảo trả lại trạng thái ban đầu cho connection
			}
		} catch (Exception e) {
			System.err.println("Database connection failed during addUser.");
			e.printStackTrace();
		}
	}

	// Cập nhật người dùng
	public void updateUser(User user) {
		// Cập nhật 2 bảng user
		String accountSql = "UPDATE account SET username = ?, role = ? WHERE code = ?";
		String profileSql = """
				    UPDATE profile SET name = ?, email = ?, phone_number = ?, address = ?,
				    date_of_birth = ?, gender = ?, citizen_id = ?, profile_image = ?
				    WHERE user_code = ?
				""";

		try (Connection conn = ConnectionDatabase.getConnection()) {
			conn.setAutoCommit(false); // Bắt đầu giao dịch thủ công

			try (PreparedStatement accStmt = conn.prepareStatement(accountSql);
					PreparedStatement proStmt = conn.prepareStatement(profileSql)) {

				// Cập nhật account
				accStmt.setString(1, user.getUsername());
				accStmt.setString(2, user.getRole());
				accStmt.setString(3, user.getCode());
				accStmt.executeUpdate();

				// Cập nhật profile
				proStmt.setString(1, user.getName());
				proStmt.setString(2, user.getEmail());
				proStmt.setString(3, user.getPhoneNumber());
				proStmt.setString(4, user.getAddress());
				proStmt.setDate(5, Date.valueOf(user.getDateOfBirth()));
				proStmt.setString(6, user.getGender());
				proStmt.setString(7, user.getCitizenId());
				proStmt.setString(8, user.getProfileImage());
				proStmt.setString(9, user.getCode());

				proStmt.executeUpdate();

				conn.commit(); // Giao dịch thành công

			} catch (Exception e) {
				conn.rollback(); // Lỗi thì rollback
				System.err.println("Update user failed, rolled back.");
				e.printStackTrace();
			} finally {
				conn.setAutoCommit(true); // Trả trạng thái mặc định
			}

		} catch (Exception e) {
			System.err.println("Database connection error during updateUser.");
			e.printStackTrace();
		}
	}

	// Hàm xóa người dùng
	public String deleteUser(String userCode) {
		String deleteProfile = "DELETE FROM profile WHERE user_code = ?";
		String deleteAccount = "DELETE FROM account WHERE code = ?";

		try (Connection conn = ConnectionDatabase.getConnection()) {
			conn.setAutoCommit(false);

			// 🧩 Kiểm tra trước
			if (isUserReferenced(conn, userCode)) {
				return "Cannot delete user because related data exists (exam queue, appointment, etc).";
			}

			try (PreparedStatement profileStmt = conn.prepareStatement(deleteProfile);
					PreparedStatement accountStmt = conn.prepareStatement(deleteAccount)) {

				profileStmt.setString(1, userCode);
				profileStmt.executeUpdate();

				accountStmt.setString(1, userCode);
				accountStmt.executeUpdate();

				conn.commit();
				return null; // success

			} catch (Exception e) {
				conn.rollback();
				e.printStackTrace();
				return "Error deleting user: " + e.getMessage();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "Database error: " + e.getMessage();
		}
	}

	private boolean isUserReferenced(Connection conn, String userCode) {
		String[] checkQueries = { "SELECT 1 FROM exam_queue WHERE created_by = ? LIMIT 1",
				"SELECT 1 FROM appointment WHERE doctor_code = ? OR patient_code = ? LIMIT 1",
				"SELECT 1 FROM medical_result WHERE doctor_code = ? OR patient_code = ? LIMIT 1",
				"SELECT 1 FROM system_log WHERE user_code = ? LIMIT 1" };

		for (String sql : checkQueries) {
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, userCode);
				if (sql.contains("OR"))
					stmt.setString(2, userCode);

				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next())
						return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public User getByUserCode(String userCode) {
		String sql = """
				    SELECT a.*, p.name, p.email, p.phone_number, p.address,
				           p.date_of_birth, p.gender, p.citizen_id, p.profile_image
				    FROM account a
				    JOIN profile p ON a.code = p.user_code
				    WHERE a.code = ?
				""";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, userCode);
			ResultSet rs = stmt.executeQuery();

			if (rs.next())
				return extractUserFromResultSet(rs);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// Kiểm tra trùng lặp thông tin
	public boolean isExistingUser(String userCode) {
		return checkExist("a.code", userCode, null);
	}

	public boolean isEmailTaken(String email, String excludeUserCode) {
		return checkExist("p.email", email, excludeUserCode);
	}

	public boolean isPhoneNumberTaken(String phone, String excludeUserCode) {
		return checkExist("p.phone_number", phone, excludeUserCode);
	}

	public boolean isCitizenIdTaken(String id, String excludeUserCode) {
		return checkExist("p.citizen_id", id, excludeUserCode);
	}

	public boolean isUsernameTaken(String username, String excludeUserCode) {
		return checkExist("a.username", username, excludeUserCode);
	}

	private boolean checkExist(String column, String value, String excludeCode) {
		String baseSql = """
				SELECT COUNT(*) FROM account a
				JOIN profile p ON a.code = p.user_code
				WHERE %s = ?
				""";

		// Thêm điều kiện loại trừ nếu cần
		if (excludeCode != null) {
			baseSql += " AND a.code != ?";
		}

		String sql = String.format(baseSql, column);

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, value);
			if (excludeCode != null) {
				stmt.setString(2, excludeCode);
			}

			ResultSet rs = stmt.executeQuery();
			return rs.next() && rs.getInt(1) > 0;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	// Hàm cập nhật mật khẩ
	public boolean updatePasswordByUserCode(String userCode, String hashedPassword) {
		String sql = "UPDATE account SET password_hash = ? WHERE code = ?";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, hashedPassword);
			stmt.setString(2, userCode);
			return stmt.executeUpdate() > 0;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public List<String> getActivityLogs(String userCode) {
		List<String> logs = new ArrayList<>();
		String sql = "SELECT action, timestamp FROM system_log WHERE user_code = ? ORDER BY timestamp DESC";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, userCode);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				logs.add(rs.getTimestamp("timestamp") + " - " + rs.getString("action"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return logs;
	}

	public String generateNextUserCode() {
		String sql = "SELECT MAX(code) FROM account WHERE code LIKE 'U%'";
		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			if (rs.next() && rs.getString(1) != null) {
				String lastCode = rs.getString(1);
				// Lấy last code và cộng thêm 1 cho user_code mới
				int number = Integer.parseInt(lastCode.substring(1));
				return String.format("U%03d", number + 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "U001";
	}

	public List<User> getUsersByRole(String role) {
		List<User> list = new ArrayList<>();
		String sql = """
				    SELECT a.*, p.name, p.email, p.phone_number, p.address,
				           p.date_of_birth, p.gender, p.citizen_id, p.profile_image
				    FROM account a
				    JOIN profile p ON a.code = p.user_code
				    WHERE a.role = ?
				""";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, role);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				list.add(extractUserFromResultSet(rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public int countUsersByGender(String gender) {
		int count = 0;
		String sql = """
				    SELECT COUNT(*)
				    FROM account a
				    JOIN profile p ON a.code = p.user_code
				    WHERE p.gender = ?
				""";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, gender);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return count;
	}

	public int[] getUserStatistics() {
		int[] stats = new int[3]; // [0] = Male, [1] = Female, [2] = Other
		String sql = """
				    SELECT gender, COUNT(*) AS total
				    FROM profile
				    GROUP BY gender
				""";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String gender = rs.getString("gender");
				int total = rs.getInt("total");

				if ("Male".equalsIgnoreCase(gender)) {
					stats[0] = total;
				} else if ("Female".equalsIgnoreCase(gender)) {
					stats[1] = total;
				} else {
					stats[2] = total;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return stats;
	}

	public List<User> searchUsersByRole(String keyword, String field, String role) {
		String column;
		switch (field.toLowerCase()) {
		case "name":
			column = "p.name";
			break;
		case "email":
			column = "p.email";
			break;
		default:
			throw new IllegalArgumentException("Invalid search field: " + field);
		}

		String sql = "SELECT p.id, a.username, a.password_hash, a.code, a.role, "
				+ "p.user_code, p.name, p.email, p.phone_number, p.address, "
				+ "p.date_of_birth, p.gender, p.citizen_id, p.profile_image " + "FROM profile p "
				+ "JOIN account a ON p.user_code = a.code " + "WHERE a.role = ? AND " + column + " LIKE ?";

		List<User> users = new ArrayList<>();
		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, role);
			stmt.setString(2, "%" + keyword + "%");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				users.add(extractUserFromResultSet(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}

	// Hàm gắn thuộc tính từ result vào đối tượng User
	private User extractUserFromResultSet(ResultSet rs) throws SQLException {
		User user = new User();
		user.setId(rs.getLong("id"));
		user.setUsername(rs.getString("username"));
		user.setPasswordHash(rs.getString("password_hash"));
		user.setCode(rs.getString("code"));
		user.setRole(rs.getString("role"));
		user.setName(rs.getString("name"));
		user.setEmail(rs.getString("email"));
		user.setPhoneNumber(rs.getString("phone_number"));
		user.setAddress(rs.getString("address"));
		user.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
		user.setGender(rs.getString("gender"));
		user.setCitizenId(rs.getString("citizen_id"));
		user.setProfileImage(rs.getString("profile_image"));
		return user;
	}
}
