package com.kien.project.clinicmanagement.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kien.project.clinicmanagement.model.Prescription;
import com.kien.project.clinicmanagement.utils.ConnectionDatabase;

public class PrescriptionDAO {

    private Prescription extractPrescriptionFromResultSet(ResultSet rs) throws SQLException {
        Prescription prescription = new Prescription();
        prescription.setId(rs.getLong("id"));
        prescription.setMedicalResultId(rs.getLong("medical_result_id"));
        prescription.setPatientCode(rs.getString("patient_code"));

        Date date = rs.getDate("prescription_date");
        if (date != null) {
            prescription.setPrescriptionDate(date.toLocalDate());
        }

        BigDecimal totalPrice = rs.getBigDecimal("total_price");
        prescription.setTotalPrice(totalPrice);

        return prescription;
    }

    public List<Prescription> getAllPrescriptions() {
        List<Prescription> list = new ArrayList<>();
        String sql = "SELECT * FROM prescription ORDER BY id ASC";

        try (Connection conn = ConnectionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(extractPrescriptionFromResultSet(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean updateTotalPrice(Long prescriptionId, BigDecimal totalPrice) {
        String sql = "UPDATE prescription SET total_price = ? WHERE id = ?";

        try (Connection conn = ConnectionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, totalPrice != null ? totalPrice : BigDecimal.ZERO);
            stmt.setLong(2, prescriptionId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Prescription> getPrescriptionsByPatientCode(String patientCode) {
        List<Prescription> list = new ArrayList<>();
        String sql = "SELECT * FROM prescription WHERE patient_code = ? ORDER BY prescription_date DESC";

        try (Connection conn = ConnectionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patientCode);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractPrescriptionFromResultSet(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Prescription getById(Long id) {
        String sql = "SELECT * FROM prescription WHERE id = ?";

        try (Connection conn = ConnectionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractPrescriptionFromResultSet(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Long getLastPrescriptionId() {
        Long lastId = null;
        String sql = "SELECT MAX(id) FROM prescription";
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

    public boolean insertPrescription(Prescription prescription) {
        Long lastId = getLastPrescriptionId();
        long newId = (lastId == null) ? 1 : lastId + 1; //Bỏ gen id mới

        String sql = "INSERT INTO prescription (id, medical_result_id, patient_code, prescription_date, total_price) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, newId);
            stmt.setLong(2, prescription.getMedicalResultId());
            stmt.setString(3, prescription.getPatientCode());

            if (prescription.getPrescriptionDate() != null) {
                stmt.setDate(4, Date.valueOf(prescription.getPrescriptionDate()));
            } else {
                stmt.setDate(4, null);
            }

            stmt.setBigDecimal(5, prescription.getTotalPrice() != null ? prescription.getTotalPrice() : BigDecimal.ZERO);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
