package com.kien.project.clinicmanagement.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.kien.project.clinicmanagement.model.ExamQueue;
import com.kien.project.clinicmanagement.utils.ConnectionDatabase;

public class ExamQueueDAO {

	public List<ExamQueue> getAllExamQueues() {
		String sql = """
				    SELECT q.*,
				           p.name AS patientName,
				           pr_doctor.name AS doctorName,
				           pr_creator.name AS createdByName
				    FROM exam_queue q
				    LEFT JOIN patient_profile p
				           ON q.patient_code = p.code
				    LEFT JOIN account a_doctor
				           ON q.doctor_code = a_doctor.code
				    LEFT JOIN profile pr_doctor
				           ON a_doctor.code = pr_doctor.user_code
				    LEFT JOIN account a_creator
				           ON q.created_by = a_creator.code
				    LEFT JOIN profile pr_creator
				           ON a_creator.code = pr_creator.user_code
				    ORDER BY q.id DESC
				""";

		List<ExamQueue> list = new ArrayList<>();
		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				ExamQueue eq = extractExamQueueFromResultSet(rs);
				eq.setPatientName(rs.getString("patientName"));
				eq.setDoctorName(rs.getString("doctorName"));
				eq.setCreatedByName(rs.getString("createdByName"));
				list.add(eq);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// ============================
	// 📌 Code dành cho ADMIN
	// ============================

	public int countExamQueues() {
		String sql = "SELECT COUNT(*) FROM exam_queue";
		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			if (rs.next())
				return rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<ExamQueue> getExamQueues(int offset, int limit) {
		List<ExamQueue> list = new ArrayList<>();
		String sql = """
				    SELECT q.*,
				           p.name AS patientName,
				           pr_doctor.name AS doctorName,
				           pr_creator.name AS createdByName
				    FROM exam_queue q
				    LEFT JOIN patient_profile p ON q.patient_code = p.code
				    LEFT JOIN account a_doctor ON q.doctor_code = a_doctor.code
				    LEFT JOIN profile pr_doctor ON a_doctor.code = pr_doctor.user_code
				    LEFT JOIN account a_creator ON q.created_by = a_creator.code
				    LEFT JOIN profile pr_creator ON a_creator.code = pr_creator.user_code
				    ORDER BY q.id DESC
				    LIMIT ?, ?
				""";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, offset);
			stmt.setInt(2, limit);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					ExamQueue eq = extractExamQueueFromResultSet(rs);
					eq.setPatientName(rs.getString("patientName"));
					eq.setDoctorName(rs.getString("doctorName"));
					eq.setCreatedByName(rs.getString("createdByName"));
					list.add(eq);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public int countSearchExamQueues(String keyword, String field) {
		String column;
		switch (field.toLowerCase()) {
		case "search by doctor name":
			column = "pr_doctor.name";
			break;
		default:
			column = "p.name";
		}

		String sql = """
				    SELECT COUNT(*)
				    FROM exam_queue q
				    LEFT JOIN patient_profile p ON q.patient_code = p.code
				    LEFT JOIN account a_doctor ON q.doctor_code = a_doctor.code
				    LEFT JOIN profile pr_doctor ON a_doctor.code = pr_doctor.user_code
				    WHERE %s LIKE ?
				""".formatted(column);

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, "%" + (keyword != null ? keyword.trim() : "") + "%");
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<ExamQueue> searchExamQueues(String keyword, String field, int offset, int limit) {
		List<ExamQueue> list = new ArrayList<>();
		String column;
		switch (field.toLowerCase()) {
		case "search by doctor name":
			column = "pr_doctor.name";
			break;
		default:
			column = "p.name";
		}

		String sql = """
				    SELECT q.*,
				           p.name AS patientName,
				           pr_doctor.name AS doctorName,
				           pr_creator.name AS createdByName
				    FROM exam_queue q
				    LEFT JOIN patient_profile p ON q.patient_code = p.code
				    LEFT JOIN account a_doctor ON q.doctor_code = a_doctor.code
				    LEFT JOIN profile pr_doctor ON a_doctor.code = pr_doctor.user_code
				    LEFT JOIN account a_creator ON q.created_by = a_creator.code
				    LEFT JOIN profile pr_creator ON a_creator.code = pr_creator.user_code
				    WHERE %s LIKE ?
				    ORDER BY q.id DESC
				    LIMIT ?, ?
				""".formatted(column);

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, "%" + (keyword != null ? keyword.trim() : "") + "%");
			stmt.setInt(2, offset);
			stmt.setInt(3, limit);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					ExamQueue eq = extractExamQueueFromResultSet(rs);
					eq.setPatientName(rs.getString("patientName"));
					eq.setDoctorName(rs.getString("doctorName"));
					eq.setCreatedByName(rs.getString("createdByName"));
					list.add(eq);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// ============================
	// 📌 Code dành cho LỄ TÂN / BÁC SĨ
	// ============================

	public int countExamQueuesByUser(String userCode, String role) {
		String condition = role.equalsIgnoreCase("DOCTOR") ? "doctor_code = ?" : "created_by = ?";
		String sql = "SELECT COUNT(*) FROM exam_queue WHERE " + condition;

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, userCode);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<ExamQueue> getExamQueuesByUser(String userCode, String role, int offset, int limit) {
		List<ExamQueue> list = new ArrayList<>();
		String condition = role.equalsIgnoreCase("DOCTOR") ? "q.doctor_code = ?" : "q.created_by = ?";

		String sql = """
				    SELECT q.*,
				           p.name AS patientName,
				           pr_doctor.name AS doctorName,
				           pr_creator.name AS createdByName
				    FROM exam_queue q
				    LEFT JOIN patient_profile p ON q.patient_code = p.code
				    LEFT JOIN account a_doctor ON q.doctor_code = a_doctor.code
				    LEFT JOIN profile pr_doctor ON a_doctor.code = pr_doctor.user_code
				    LEFT JOIN account a_creator ON q.created_by = a_creator.code
				    LEFT JOIN profile pr_creator ON a_creator.code = pr_creator.user_code
				    WHERE %s
				    ORDER BY q.id DESC
				    LIMIT ?, ?
				""".formatted(condition);

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, userCode);
			stmt.setInt(2, offset);
			stmt.setInt(3, limit);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					ExamQueue eq = extractExamQueueFromResultSet(rs);
					eq.setPatientName(rs.getString("patientName"));
					eq.setDoctorName(rs.getString("doctorName"));
					eq.setCreatedByName(rs.getString("createdByName"));
					list.add(eq);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public int countSearchExamQueuesByUser(String userCode, String role, String keyword, String field) {
		String condition = role.equalsIgnoreCase("DOCTOR") ? "q.doctor_code = ?" : "q.created_by = ?";
		String column;
		switch (field.toLowerCase()) {
		case "search by doctor name":
			column = "pr_doctor.name";
			break;
		default:
			column = "p.name";
		}

		String sql = """
				    SELECT COUNT(*)
				    FROM exam_queue q
				    LEFT JOIN patient_profile p ON q.patient_code = p.code
				    LEFT JOIN account a_doctor ON q.doctor_code = a_doctor.code
				    LEFT JOIN profile pr_doctor ON a_doctor.code = pr_doctor.user_code
				    WHERE %s AND %s LIKE ?
				""".formatted(condition, column);

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, userCode);
			stmt.setString(2, "%" + (keyword != null ? keyword.trim() : "") + "%");

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<ExamQueue> searchExamQueuesByUser(String userCode, String role, String keyword, String field,
			int offset, int limit) {
		List<ExamQueue> list = new ArrayList<>();
		String condition = role.equalsIgnoreCase("DOCTOR") ? "q.doctor_code = ?" : "q.created_by = ?";
		String column;
		switch (field.toLowerCase()) {
		case "search by doctor name":
			column = "pr_doctor.name";
			break;
		default:
			column = "p.name";
		}

		String sql = """
				    SELECT q.*,
				           p.name AS patientName,
				           pr_doctor.name AS doctorName,
				           pr_creator.name AS createdByName
				    FROM exam_queue q
				    LEFT JOIN patient_profile p ON q.patient_code = p.code
				    LEFT JOIN account a_doctor ON q.doctor_code = a_doctor.code
				    LEFT JOIN profile pr_doctor ON a_doctor.code = pr_doctor.user_code
				    LEFT JOIN account a_creator ON q.created_by = a_creator.code
				    LEFT JOIN profile pr_creator ON a_creator.code = pr_creator.user_code
				    WHERE %s AND %s LIKE ?
				    ORDER BY q.id DESC
				    LIMIT ?, ?
				""".formatted(condition, column);

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, userCode);
			stmt.setString(2, "%" + (keyword != null ? keyword.trim() : "") + "%");
			stmt.setInt(3, offset);
			stmt.setInt(4, limit);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					ExamQueue eq = extractExamQueueFromResultSet(rs);
					eq.setPatientName(rs.getString("patientName"));
					eq.setDoctorName(rs.getString("doctorName"));
					eq.setCreatedByName(rs.getString("createdByName"));
					list.add(eq);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<ExamQueue> getWaitingExamQueues() {
		List<ExamQueue> list = new ArrayList<>();
		String sql = """
				    SELECT q.*,
				           p.name AS patientName,
				           pr_doctor.name AS doctorName,
				           pr_creator.name AS createdByName
				    FROM exam_queue q
				    LEFT JOIN patient_profile p
				           ON q.patient_code = p.code
				    LEFT JOIN account a_doctor
				           ON q.doctor_code = a_doctor.code
				    LEFT JOIN profile pr_doctor
				           ON a_doctor.code = pr_doctor.user_code
				    LEFT JOIN account a_creator
				           ON q.created_by = a_creator.code
				    LEFT JOIN profile pr_creator
				           ON a_creator.code = pr_creator.user_code
				    WHERE q.status = 'waiting'
				    ORDER BY q.id DESC
				""";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				ExamQueue eq = extractExamQueueFromResultSet(rs);
				eq.setPatientName(rs.getString("patientName"));
				eq.setDoctorName(rs.getString("doctorName"));
				eq.setCreatedByName(rs.getString("createdByName"));
				list.add(eq);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<ExamQueue> getWaitingQueuesByDoctor(String doctorCode) {
		List<ExamQueue> list = new ArrayList<>();
		if (doctorCode == null || doctorCode.isBlank())
			return list;

		String sql = """
				    SELECT q.*,
				           p.name AS patientName,
				           pr_doctor.name AS doctorName,
				           pr_creator.name AS createdByName
				    FROM exam_queue q
				    LEFT JOIN patient_profile p ON q.patient_code = p.code
				    LEFT JOIN account a_doctor ON q.doctor_code = a_doctor.code
				    LEFT JOIN profile pr_doctor ON a_doctor.code = pr_doctor.user_code
				    LEFT JOIN account a_creator ON q.created_by = a_creator.code
				    LEFT JOIN profile pr_creator ON a_creator.code = pr_creator.user_code
				    WHERE q.doctor_code = ? AND q.status = 'waiting'
				    ORDER BY q.queue_number ASC
				""";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, doctorCode);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					ExamQueue eq = extractExamQueueFromResultSet(rs);
					eq.setPatientName(rs.getString("patientName"));
					eq.setDoctorName(rs.getString("doctorName"));
					eq.setCreatedByName(rs.getString("createdByName"));
					list.add(eq);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean updateExamQueueStatus(Long id, String status) {
		if (id == null || status == null || status.isBlank())
			return false;

		String sql = """
				    UPDATE exam_queue
				    SET status = ?
				    WHERE id = ? AND status = 'waiting'
				""";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, status.toLowerCase());
			stmt.setLong(2, id);

			return stmt.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public ExamQueue getByExamQueueId(Long id) {
		if (id == null)
			return null;
		String sql = "SELECT * FROM exam_queue WHERE id = ?";
		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return extractExamQueueFromResultSet(rs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void insertExamQueue(ExamQueue queue) {
		if (queue == null)
			return;

		String sql = "INSERT INTO exam_queue (patient_code, doctor_code, queue_number, status, created_by, created_at) VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, queue.getPatientCode());
			stmt.setString(2, queue.getDoctorCode());
			stmt.setInt(3, queue.getQueueNumber());
			stmt.setString(4, queue.getStatus());
			stmt.setString(5, queue.getCreatedBy());

			java.util.Date createdAt = queue.getCreatedAt();
			if (createdAt != null) {
				stmt.setDate(6, new java.sql.Date(createdAt.getTime()));
			} else {
				stmt.setDate(6, new java.sql.Date(System.currentTimeMillis()));
			}

			int affected = stmt.executeUpdate();
			if (affected > 0) {
				try (ResultSet keys = stmt.getGeneratedKeys()) {
					if (keys.next()) {
						queue.setId(keys.getLong(1));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void updateExamQueue(ExamQueue queue) {
		if (queue == null || queue.getId() == null)
			return;

		String sql = "UPDATE exam_queue SET patient_code = ?, doctor_code = ?, queue_number = ?, status = ? WHERE id = ?";
		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, queue.getPatientCode());
			stmt.setString(2, queue.getDoctorCode());
			stmt.setInt(3, queue.getQueueNumber());
			stmt.setString(4, queue.getStatus());
			stmt.setLong(5, queue.getId());

			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public boolean deleteExamQueue(Long id) {
		if (id == null)
			return false;
		String sql = "DELETE FROM exam_queue WHERE id = ?";
		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, id);
			return stmt.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isExistingExamQueue(Long id) {
		if (id == null)
			return false;
		String sql = "SELECT COUNT(*) FROM exam_queue WHERE id = ?";
		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean existsByPatientCode(String patientCode) {
		if (patientCode == null || patientCode.isBlank())
			return false;
		String sql = "SELECT COUNT(*) FROM exam_queue WHERE patient_code = ?";
		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, patientCode);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Trả về danh sách hàng đợi của 1 bác sĩ (toàn bộ), sắp theo queue_number ASC.
	 * Service sẽ dùng size() để tính next number (hoặc bạn có thể đổi service để
	 * lấy MAX(queue_number)).
	 */
	public List<ExamQueue> getQueuesByDoctor(String doctorCode) {
		List<ExamQueue> list = new ArrayList<>();
		if (doctorCode == null || doctorCode.isBlank())
			return list;

		String sql = "SELECT * FROM exam_queue WHERE doctor_code = ? ORDER BY queue_number ASC";
		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, doctorCode);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					list.add(extractExamQueueFromResultSet(rs));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<ExamQueue> searchExamQueues(String keyword, String field) {
		List<ExamQueue> list = new ArrayList<>();
		String column;

		if (field == null)
			field = "";

		switch (field.toLowerCase()) {
		case "search by patient name":
			column = "p.name";
			break;
		case "search by doctor name":
			column = "pr_doctor.name";
			break;
		default:
			column = "p.name";
		}

		String sql = "SELECT q.*, " + "       p.name AS patientName, " + "       pr_doctor.name AS doctorName, "
				+ "       pr_creator.name AS createdByName " + "FROM exam_queue q "
				+ "LEFT JOIN patient_profile p ON q.patient_code = p.code "
				+ "LEFT JOIN account a_doctor ON q.doctor_code = a_doctor.code "
				+ "LEFT JOIN profile pr_doctor ON a_doctor.code = pr_doctor.user_code "
				+ "LEFT JOIN account a_creator ON q.created_by = a_creator.code "
				+ "LEFT JOIN profile pr_creator ON a_creator.code = pr_creator.user_code " + "WHERE " + column
				+ " LIKE ? " + "ORDER BY q.id DESC";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, "%" + (keyword != null ? keyword.trim() : "") + "%");

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					ExamQueue eq = extractExamQueueFromResultSet(rs);
					eq.setPatientName(rs.getString("patientName"));
					eq.setDoctorName(rs.getString("doctorName"));
					eq.setCreatedByName(rs.getString("createdByName"));
					list.add(eq);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public int countPatientsForDoctorToday(String doctorCode, LocalDate date) {
		String sql = "SELECT COUNT(*) FROM exam_queue WHERE doctor_code = ? AND DATE(created_at) = ?";
		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, doctorCode);
			stmt.setDate(2, java.sql.Date.valueOf(date));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int getMaxQueueNumberForDoctorToday(String doctorCode, LocalDate date) {
		String sql = "SELECT COALESCE(MAX(queue_number), 0) " + "FROM exam_queue "
				+ "WHERE doctor_code = ? AND DATE(created_at) = ?";
		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, doctorCode);
			stmt.setDate(2, java.sql.Date.valueOf(date));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private ExamQueue extractExamQueueFromResultSet(ResultSet rs) throws SQLException {
		ExamQueue examQueue = new ExamQueue();
		examQueue.setId(rs.getLong("id"));
		examQueue.setPatientCode(rs.getString("patient_code"));
		examQueue.setDoctorCode(rs.getString("doctor_code"));
		examQueue.setQueueNumber(rs.getInt("queue_number"));
		examQueue.setStatus(rs.getString("status"));
		examQueue.setCreatedBy(rs.getString("created_by"));

		Timestamp createdAt = rs.getTimestamp("created_at");
		if (createdAt != null) {
			examQueue.setCreatedAt(new Date(createdAt.getTime()));
		}

		return examQueue;
	}

}
