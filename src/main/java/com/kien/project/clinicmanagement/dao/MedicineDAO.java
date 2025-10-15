package com.kien.project.clinicmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kien.project.clinicmanagement.model.Medicine;
import com.kien.project.clinicmanagement.utils.ConnectionDatabase;

public class MedicineDAO {

	// Lấy tất cả thuốc
	public List<Medicine> getAllMedicines() {
		List<Medicine> list = new ArrayList<>();
		String sql = "SELECT * FROM medicine ORDER BY id ASC";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				list.add(extractMedicineFromResultSet(rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	// Đếm tổng số thuốc
	public int countMedicines() {
		String sql = "SELECT COUNT(*) FROM medicine";
		try (Connection connection = ConnectionDatabase.getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	// Lấy thuốc theo offset và limit
	public List<Medicine> getMedicines(int offset, int limit) {
		List<Medicine> list = new ArrayList<>();

		String sql = """
				SELECT *
				FROM medicine
				ORDER BY id DESC
				LIMIT ? OFFSET ?
				""";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, limit);
			stmt.setInt(2, offset);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					list.add(extractMedicineFromResultSet(rs));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// Tìm kiếm thuốc với offset và limit
	public List<Medicine> searchMedicines(String keyword, String field, int offset, int limit) {
		List<Medicine> list = new ArrayList<>();

		String column = switch (field.toLowerCase()) {
		case "search by unit" -> "unit";
		default -> "name";
		};

		String sql = String.format("SELECT * FROM medicine WHERE %s LIKE ? ORDER BY id ASC LIMIT ? OFFSET ?", column);

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, "%" + keyword + "%");
			stmt.setInt(2, limit);
			stmt.setInt(3, offset);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					list.add(extractMedicineFromResultSet(rs));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public int countSearchMedicines(String keyword, String field) {
		String column = switch (field.toLowerCase()) {
		case "search by unit" -> "unit";
		default -> "name";
		};

		String sql = String.format("SELECT COUNT(*) FROM medicine WHERE %s LIKE ?", column);

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, "%" + keyword + "%");

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) return rs.getInt(1);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public Medicine getByMedicineCode(String code) {
		String sql = "SELECT * FROM medicine WHERE code = ?";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, code);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				return extractMedicineFromResultSet(rs);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void addMedicine(Medicine medicine) {
		String sql = """
				    INSERT INTO medicine (code, name, description, unit, price, max_age, min_age, quantity)
				    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
				""";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, medicine.getCode());
			stmt.setString(2, medicine.getName());
			stmt.setString(3, medicine.getDescription());
			stmt.setString(4, medicine.getUnit());
			stmt.setBigDecimal(5, medicine.getPrice());
			stmt.setInt(6, medicine.getMaxAge());
			stmt.setInt(7, medicine.getMinAge());
			stmt.setInt(8, medicine.getQuantity());

			stmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean updateQuantity(String medicineCode, int newQuantity) {
		// ví dụ cập nhật vào database:
		String sql = "UPDATE medicine SET quantity = ? WHERE code = ?";
		try (Connection conn = ConnectionDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, newQuantity);
			ps.setString(2, medicineCode);
			int affected = ps.executeUpdate();
			return affected > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void updateMedicine(Medicine medicine) {
		String sql = """
				    UPDATE medicine SET
				        name = ?, description = ?, unit = ?, price = ?, max_age = ?, min_age = ?, quantity = ?
				    WHERE code = ?
				""";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, medicine.getName());
			stmt.setString(2, medicine.getDescription());
			stmt.setString(3, medicine.getUnit());
			stmt.setBigDecimal(4, medicine.getPrice());
			stmt.setInt(5, medicine.getMaxAge());
			stmt.setInt(6, medicine.getMinAge());
			stmt.setInt(7, medicine.getQuantity());
			stmt.setString(8, medicine.getCode());

			stmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Cập nhật số lượng thuốc (thêm vào stock)
	public boolean addStock(String code, int addedQuantity) {
		String sql = "UPDATE medicine SET quantity = quantity + ? WHERE code = ?";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, addedQuantity);
			stmt.setString(2, code);
			return stmt.executeUpdate() > 0;

		} catch (Exception e) {
			System.err.println("Error adding stock for medicine:");
			e.printStackTrace();
			return false;
		}
	}

	public void deleteMedicine(String code) {
		String sql = "DELETE FROM medicine WHERE code = ?";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, code);
			stmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isExistingMedicine(String code) {
		return checkExist("code", code, null);
	}

	public boolean isExistingName(String name, String excludeCode) {
		return checkExist("name", name, excludeCode);
	}

	private boolean checkExist(String column, String value, String excludeCode) {
		String sql = "SELECT COUNT(*) FROM medicine WHERE " + column + " = ?";
		if (excludeCode != null) {
			sql += " AND code != ?";
		}

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

	public String generateNextMedicineCode() {
		String prefix = "MED";
		String sql = "SELECT code FROM medicine WHERE code LIKE ? ORDER BY id DESC LIMIT 1";

		try (Connection conn = ConnectionDatabase.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, prefix + "%");
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				String lastCode = rs.getString("code");
				int num = Integer.parseInt(lastCode.substring(prefix.length()));
				return prefix + String.format("%03d", num + 1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return prefix + "001";
	}

	private Medicine extractMedicineFromResultSet(ResultSet rs) throws SQLException {
		Medicine medicine = new Medicine();
		medicine.setId(rs.getLong("id"));
		medicine.setCode(rs.getString("code"));
		medicine.setName(rs.getString("name"));
		medicine.setDescription(rs.getString("description"));
		medicine.setUnit(rs.getString("unit"));
		medicine.setPrice(rs.getBigDecimal("price"));
		medicine.setMaxAge(rs.getInt("max_age"));
		medicine.setMinAge(rs.getInt("min_age"));
		medicine.setQuantity(rs.getInt("quantity"));
		return medicine;
	}
}
