package com.kien.project.clinicmanagement.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kien.project.clinicmanagement.model.PrescriptionDetail;
import com.kien.project.clinicmanagement.utils.ConnectionDatabase;

public class PrescriptionDetailDAO {

	private PrescriptionDetail extractPrescriptionDetailFromResultSet(ResultSet rs) throws SQLException {
		PrescriptionDetail detail = new PrescriptionDetail();
		detail.setId(rs.getLong("id"));
		detail.setPrescriptionId(rs.getLong("prescription_id"));
		detail.setMedicineCode(rs.getString("medicine_code"));
		detail.setMedicineName(rs.getString("medicine_name"));
		detail.setDosage(rs.getString("dosage"));
		detail.setQuantity(rs.getInt("quantity"));
		detail.setUnitPrice(rs.getBigDecimal("unit_price"));
		detail.setTotalPrice(rs.getBigDecimal("total_price"));
		detail.setUsageInstructions(rs.getString("usage_instructions"));
		return detail;
	}

	public List<PrescriptionDetail> getAllPrescriptionDetails() {
		List<PrescriptionDetail> list = new ArrayList<>();
		String sql = "SELECT * FROM prescription_detail ORDER BY id ASC";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				list.add(extractPrescriptionDetailFromResultSet(rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public PrescriptionDetail getById(Long id) {
		if (id == null)
			return null;

		String sql = "SELECT * FROM prescription_detail WHERE id = ?";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return extractPrescriptionDetailFromResultSet(rs);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public List<PrescriptionDetail> getPrescriptionDetailsByPrescriptionId(Long prescriptionId) {
	    List<PrescriptionDetail> list = new ArrayList<>();
	    String sql = "SELECT id, prescription_id, medicine_code, medicine_name, dosage, quantity, unit_price, total_price, usage_instructions "
	               + "FROM prescription_detail WHERE prescription_id = ?";

	    try (Connection conn = ConnectionDatabase.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setLong(1, prescriptionId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                PrescriptionDetail detail = new PrescriptionDetail();
	                detail.setId(rs.getLong("id"));
	                detail.setPrescriptionId(rs.getLong("prescription_id"));
	                detail.setMedicineCode(rs.getString("medicine_code"));
	                detail.setMedicineName(rs.getString("medicine_name"));
	                detail.setDosage(rs.getString("dosage"));
	                detail.setQuantity(rs.getInt("quantity"));
	                detail.setUnitPrice(rs.getBigDecimal("unit_price"));
	                detail.setTotalPrice(rs.getBigDecimal("total_price"));
	                detail.setUsageInstructions(rs.getString("usage_instructions"));
	                list.add(detail);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return list;
	}

	public boolean isExistingPrescriptionDetail(Long id) {
		if (id == null)
			return false;

		String sql = "SELECT COUNT(*) FROM prescription_detail WHERE id = ?";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt(1) > 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public BigDecimal getTotalPriceByPrescriptionId(Long prescriptionId) {
	    if (prescriptionId == null) {
	        return BigDecimal.ZERO;
	    }

	    String sql = "SELECT SUM(total_price) FROM prescription_detail WHERE prescription_id = ?";

	    try (Connection conn = ConnectionDatabase.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setLong(1, prescriptionId);
	        ResultSet rs = stmt.executeQuery();

	        if (rs.next()) {
	            BigDecimal total = rs.getBigDecimal(1);
	            return (total != null) ? total : BigDecimal.ZERO;
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return BigDecimal.ZERO;
	}

	public void addPrescriptionDetail(PrescriptionDetail detail) {
		if (detail.getPrescriptionId() == null) {
			throw new IllegalArgumentException("prescriptionId cannot be null");
		}

		String sql = """
				    INSERT INTO prescription_detail
				    (prescription_id, medicine_code, medicine_name, dosage, quantity, unit_price, total_price, usage_instructions)
				    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
				""";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, detail.getPrescriptionId());
			stmt.setString(2, detail.getMedicineCode());
			stmt.setString(3, detail.getMedicineName());
			stmt.setString(4, detail.getDosage());
			stmt.setInt(5, detail.getQuantity());
			stmt.setBigDecimal(6, detail.getUnitPrice());
			stmt.setBigDecimal(7, detail.getTotalPrice());
			stmt.setString(8, detail.getUsageInstructions());

			stmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updatePrescriptionDetail(PrescriptionDetail detail) {
		String sql = """
				    UPDATE prescription_detail
				    SET prescription_id = ?, medicine_code = ?, medicine_name = ?, dosage = ?, quantity = ?,
				        unit_price = ?, total_price = ?, usage_instructions = ?
				    WHERE id = ?
				""";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, detail.getPrescriptionId());
			stmt.setString(2, detail.getMedicineCode());
			stmt.setString(3, detail.getMedicineName());
			stmt.setString(4, detail.getDosage());
			stmt.setInt(5, detail.getQuantity());
			stmt.setBigDecimal(6, detail.getUnitPrice());
			stmt.setBigDecimal(7, detail.getTotalPrice());
			stmt.setString(8, detail.getUsageInstructions());
			stmt.setLong(9, detail.getId());

			stmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean deletePrescriptionDetail(Long id) {
		String sql = "DELETE FROM prescription_detail WHERE id = ?";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, id);
			int affectedRows = stmt.executeUpdate();

			return affectedRows > 0; // nếu có hàng bị xóa thì trả về true

		} catch (Exception e) {
			e.printStackTrace();
			return false; // lỗi thì trả về false
		}
	}

	public List<PrescriptionDetail> searchPrescriptionDetails(String keyword, String field) {
		List<PrescriptionDetail> list = new ArrayList<>();

		// Chuyển field đầu vào sang cột phù hợp trong DB
		String column = switch (field.toLowerCase()) {
		case "medicine code" -> "medicine_code";
		case "medicine name" -> "medicine_name";
		case "dosage" -> "dosage";
		default -> "medicine_name";
		};

		String sql = String.format("SELECT * FROM prescription_detail WHERE %s LIKE ? ORDER BY id ASC", column);

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, "%" + keyword + "%");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				list.add(extractPrescriptionDetailFromResultSet(rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public List<PrescriptionDetail> getPrescriptionDetailsPage(int page, int pageSize, String sortField,
			String sortOrder) {
		List<PrescriptionDetail> list = new ArrayList<>();
		int offset = (page - 1) * pageSize;

		String validSortField = switch (sortField) {
		case "medicine_code", "medicine_name", "quantity", "created_at" -> sortField;
		default -> "id";
		};

		String validSortOrder = (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) ? "DESC" : "ASC";

		String sql = String.format("SELECT * FROM prescription_detail ORDER BY %s %s LIMIT ? OFFSET ?", validSortField,
				validSortOrder);

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, pageSize);
			stmt.setInt(2, offset);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				list.add(extractPrescriptionDetailFromResultSet(rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public int countPrescriptionDetails(String keyword, String field) {
		String column = switch (field.toLowerCase()) {
		case "medicine code" -> "medicine_code";
		case "medicine name" -> "medicine_name";
		case "dosage" -> "dosage";
		default -> "medicine_name";
		};

		String sql = String.format("SELECT COUNT(*) FROM prescription_detail WHERE %s LIKE ?", column);

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, "%" + keyword + "%");
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}
}
