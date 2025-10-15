package com.kien.project.clinicmanagement.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kien.project.clinicmanagement.model.MedicalResult;
import com.kien.project.clinicmanagement.utils.ConnectionDatabase;

public class MedicalResultDAO {

	private MedicalResult extractMedicalResultFromResultSet(ResultSet rs) throws SQLException {
		MedicalResult result = new MedicalResult();
		result.setId(rs.getLong("id"));
		result.setExamQueueId(rs.getLong("exam_queue_id"));
		result.setPatientCode(rs.getString("patient_code"));
		result.setDoctorCode(rs.getString("doctor_code"));
		result.setExaminationDate(rs.getDate("examination_date").toLocalDate());
		result.setSymptoms(rs.getString("symptoms"));
		result.setDiagnosis(rs.getString("diagnosis"));
		result.setTreatmentPlan(rs.getString("treatment_plan"));
		return result;
	}

	public List<MedicalResult> getAllMedicalResults() {
		List<MedicalResult> list = new ArrayList<>();
		String sql = "SELECT * FROM medical_result ORDER BY id ASC";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				list.add(extractMedicalResultFromResultSet(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public Long getLastMedicalResultId() {
		Long lastId = null;
		String sql = "SELECT MAX(id) FROM medical_result";
		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				lastId = rs.getLong(1);
				if (rs.wasNull()) {
					lastId = null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lastId;
	}

	public MedicalResult getByMedicalResultId(Long id) {
		String sql = "SELECT * FROM medical_result WHERE id = ?";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return extractMedicalResultFromResultSet(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<MedicalResult> getByPatientCode(String patientCode) {
	    List<MedicalResult> list = new ArrayList<>();
	    String sql = "SELECT * FROM medical_result WHERE patient_code = ? ORDER BY examination_date DESC";

	    try (Connection conn = ConnectionDatabase.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, patientCode);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                list.add(extractMedicalResultFromResultSet(rs));
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return list;
	}

	public void addMedicalResult(MedicalResult result) {
		String sql = """
				    INSERT INTO medical_result
				    (id, exam_queue_id, patient_code, doctor_code, examination_date, symptoms, diagnosis, treatment_plan)
				    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
				""";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, result.getId()); // Tự set ID đã sinh ra
			stmt.setLong(2, result.getExamQueueId());
			stmt.setString(3, result.getPatientCode());
			stmt.setString(4, result.getDoctorCode());
			stmt.setDate(5, Date.valueOf(result.getExaminationDate()));
			stmt.setString(6, result.getSymptoms());
			stmt.setString(7, result.getDiagnosis());
			stmt.setString(8, result.getTreatmentPlan());

			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateMedicalResult(MedicalResult result) {
		String sql = """
				    UPDATE medical_result SET
				    exam_queue_id = ?, patient_code = ?, doctor_code = ?, examination_date = ?,
				    symptoms = ?, diagnosis = ?, treatment_plan = ?
				    WHERE id = ?
				""";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, result.getExamQueueId());
			stmt.setString(2, result.getPatientCode());
			stmt.setString(3, result.getDoctorCode());
			stmt.setDate(4, Date.valueOf(result.getExaminationDate()));
			stmt.setString(5, result.getSymptoms());
			stmt.setString(6, result.getDiagnosis());
			stmt.setString(7, result.getTreatmentPlan());
			stmt.setLong(8, result.getId());

			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteMedicalResult(Long id) {
		String sql = "DELETE FROM medical_result WHERE id = ?";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, id);
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<MedicalResult> searchResults(String keyword, String field) {
		List<MedicalResult> list = new ArrayList<>();

		String column = switch (field.toLowerCase()) {
		case "search by patient code" -> "patient_code";
		case "search by doctor code" -> "doctor_code";
		default -> "diagnosis";
		};

		String sql = String.format("SELECT * FROM medical_result WHERE %s LIKE ?", column);

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, "%" + keyword + "%");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(extractMedicalResultFromResultSet(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public List<MedicalResult> getResultsPage(int page, int pageSize, String sortField, String sortOrder) {
		List<MedicalResult> list = new ArrayList<>();

		int offset = (page - 1) * pageSize;

		String validSortField = switch (sortField) {
		case "patient_code", "doctor_code", "examination_date" -> sortField;
		default -> "id";
		};

		String validSortOrder = (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) ? "DESC" : "ASC";

		String sql = String.format("SELECT * FROM medical_result ORDER BY %s %s LIMIT ? OFFSET ?", validSortField,
				validSortOrder);

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, pageSize);
			stmt.setInt(2, offset);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(extractMedicalResultFromResultSet(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public int countResults(String keyword, String field) {
		String column = switch (field.toLowerCase()) {
		case "search by patient code" -> "patient_code";
		case "search by doctor code" -> "doctor_code";
		default -> "diagnosis";
		};

		String sql = String.format("SELECT COUNT(*) FROM medical_result WHERE %s LIKE ?", column);

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, "%" + keyword + "%");
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				return rs.getInt(1);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
}
