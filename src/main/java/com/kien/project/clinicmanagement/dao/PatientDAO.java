package com.kien.project.clinicmanagement.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kien.project.clinicmanagement.model.Patient;
import com.kien.project.clinicmanagement.utils.ConnectionDatabase;

public class PatientDAO {

    public List<Patient> getAllPatients() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patient_profile ORDER BY id ASC";

        try (Connection conn = ConnectionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(extractPatientFromResultSet(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public int countPatients() {
        String sql = "SELECT COUNT(*) FROM patient_profile";
        try (Connection conn = ConnectionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public List<Patient> getPatients(int offset, int limit) {
        List<Patient> list = new ArrayList<>();

        String sql = """
            SELECT * 
            FROM patient_profile
            ORDER BY id DESC
            LIMIT ? OFFSET ?
        """;

        try (Connection conn = ConnectionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractPatientFromResultSet(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    
    public List<Patient> searchPatients(String keyword, String field, int offset, int limit) {
        List<Patient> list = new ArrayList<>();

        String column = switch (field.toLowerCase()) {
            case "search by phone number" -> "phone_number";
            case "search by citizen id" -> "citizen_id";
            default -> "name";
        };

        String sql = String.format(
            "SELECT * FROM patient_profile WHERE %s LIKE ? ORDER BY id ASC LIMIT ? OFFSET ?",
            column
        );

        try (Connection conn = ConnectionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%"); // ✅ CHỈ 1 dấu %
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(extractPatientFromResultSet(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int countSearchPatient(String keyword, String field) {
        String column = switch (field.toLowerCase()) {
            case "search by phone number" -> "phone_number";
            case "search by citizen id" -> "citizen_id";
            default -> "name";
        };

        String sql = String.format("SELECT COUNT(*) FROM patient_profile WHERE %s LIKE ?", column);

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

    
    public Patient getByPatientCode(String code) {
        String sql = "SELECT * FROM patient_profile WHERE code = ?";

        try (Connection conn = ConnectionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return extractPatientFromResultSet(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addPatient(Patient patient) {
        String sql = """
            INSERT INTO patient_profile (code, name, email, phone_number, address, date_of_birth, gender, citizen_id, profile_image)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = ConnectionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patient.getCode());
            stmt.setString(2, patient.getName());
            stmt.setString(3, patient.getEmail());
            stmt.setString(4, patient.getPhoneNumber());
            stmt.setString(5, patient.getAddress());
            stmt.setDate(6, Date.valueOf(patient.getDateOfBirth()));
            stmt.setString(7, patient.getGender());
            stmt.setString(8, patient.getCitizenId());
            stmt.setString(9, patient.getProfileImage());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePatient(Patient patient) {
        String sql = """
            UPDATE patient_profile SET
            name = ?, email = ?, phone_number = ?, address = ?, date_of_birth = ?, gender = ?, citizen_id = ?, profile_image = ?
            WHERE code = ?
        """;

        try (Connection conn = ConnectionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patient.getName());
            stmt.setString(2, patient.getEmail());
            stmt.setString(3, patient.getPhoneNumber());
            stmt.setString(4, patient.getAddress());
            stmt.setDate(5, Date.valueOf(patient.getDateOfBirth()));
            stmt.setString(6, patient.getGender());
            stmt.setString(7, patient.getCitizenId());
            stmt.setString(8, patient.getProfileImage());
            stmt.setString(9, patient.getCode());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deletePatient(String code) {
        String sql = "DELETE FROM patient_profile WHERE code = ?";

        try (Connection conn = ConnectionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isExistingPatient(String code) {
        return checkExist("code", code, null);
    }
    
    public boolean isEmailTaken(String email, String excludePatientCode) {
        return checkExist("email", email, excludePatientCode);
    }

    public boolean isPhoneNumberTaken(String phone, String excludePatientCode) {
        return checkExist("phone_number", phone, excludePatientCode);
    }

    public boolean isCitizenIdTaken(String citizenId, String excludePatientCode) {
        return checkExist("citizen_id", citizenId, excludePatientCode);
    }
    
    private boolean checkExist(String column, String value, String excludeCode) {
        String baseSql = "SELECT COUNT(*) FROM patient_profile WHERE " + column + " = ?";
        
        // Nếu cập nhật thì loại trừ chính mã bệnh nhân đang sửa
        if (excludeCode != null) {
            baseSql += " AND code != ?";
        }

        try (Connection conn = ConnectionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(baseSql)) {

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

    public String generateNextPatientCode() {
        String sql = "SELECT MAX(code) FROM patient_profile WHERE code LIKE 'P%'";
        try (Connection conn = ConnectionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next() && rs.getString(1) != null) {
                String lastCode = rs.getString(1);
                int num = Integer.parseInt(lastCode.substring(1));
                return String.format("P%03d", num + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "P001";
    }
    
	// Code PatientManagementView
    private Patient extractPatientFromResultSet(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setId(rs.getLong("id"));
        patient.setCode(rs.getString("code"));
        patient.setName(rs.getString("name"));
        patient.setEmail(rs.getString("email"));
        patient.setPhoneNumber(rs.getString("phone_number"));
        patient.setAddress(rs.getString("address"));
        patient.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        patient.setGender(rs.getString("gender"));
        patient.setCitizenId(rs.getString("citizen_id"));
        patient.setProfileImage(rs.getString("profile_image"));
        patient.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return patient;
    }
}