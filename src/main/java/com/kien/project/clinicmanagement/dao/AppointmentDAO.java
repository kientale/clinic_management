package com.kien.project.clinicmanagement.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.kien.project.clinicmanagement.model.Appointment;
import com.kien.project.clinicmanagement.utils.ConnectionDatabase;

public class AppointmentDAO {

	// Hàm lấy tất cả lịch hẹn
	public List<Appointment> getAllAppointments() {
		String sql = """
			    SELECT a.*, 
			           p.name AS patientName, 
			           d.name AS doctorName, 
			           cb.name AS createdByName
			    FROM appointment a
			    LEFT JOIN patient_profile p ON a.patient_code = p.code
			    LEFT JOIN profile d ON a.doctor_code = d.user_code
			    LEFT JOIN profile cb ON a.created_by = cb.user_code
			    ORDER BY a.scheduled_date DESC
			    """;
	    List<Appointment> list = new ArrayList<>();
	    try (Connection con = ConnectionDatabase.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        while (rs.next()) {
	            list.add(mapResultSetToAppointment(rs));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return list;
	}
	
	// Code dành cho Admin
	public int countAppointments() {
	    String sql = "SELECT COUNT(*) FROM appointment";
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

	// Lấy danh sách lịch hẹn có phân trang
	public List<Appointment> getAppointments(int offset, int limit) {
	    List<Appointment> list = new ArrayList<>();

	    String sql = """
	        SELECT a.*, 
	               p.name AS patientName, 
	               d.name AS doctorName, 
	               cb.name AS createdByName
	        FROM appointment a
	        LEFT JOIN patient_profile p ON a.patient_code = p.code
	        LEFT JOIN profile d ON a.doctor_code = d.user_code
	        LEFT JOIN profile cb ON a.created_by = cb.user_code
	        ORDER BY a.scheduled_date DESC
	        LIMIT ? OFFSET ?
	    """;

	    try (Connection conn = ConnectionDatabase.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, limit);
	        stmt.setInt(2, offset);

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                list.add(mapResultSetToAppointment(rs));
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}

	// Tìm kiếm lịch hẹn có phân trang (theo tên bệnh nhân hoặc tên bác sĩ)
	public List<Appointment> searchAppointments(String keyword, String field, int offset, int limit) {
	    List<Appointment> list = new ArrayList<>();

	    String whereClause;
	    switch (field.toLowerCase()) {
	        case "search by patient name" -> whereClause = "p.name LIKE ?";
	        case "search by doctor name" -> whereClause = "d.name LIKE ?";
	        default -> whereClause = "p.name LIKE ?"; // mặc định tìm theo tên bệnh nhân
	    }

	    String sql = """
	        SELECT a.*, 
	               p.name AS patientName, 
	               d.name AS doctorName, 
	               cb.name AS createdByName
	        FROM appointment a
	        LEFT JOIN patient_profile p ON a.patient_code = p.code
	        LEFT JOIN profile d ON a.doctor_code = d.user_code
	        LEFT JOIN profile cb ON a.created_by = cb.user_code
	        WHERE %s
	        ORDER BY a.scheduled_date DESC
	        LIMIT ? OFFSET ?
	    """.formatted(whereClause);

	    try (Connection conn = ConnectionDatabase.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, "%" + keyword + "%");
	        stmt.setInt(2, limit);
	        stmt.setInt(3, offset);

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                list.add(mapResultSetToAppointment(rs));
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}

	// Đếm tổng số lịch hẹn khi tìm kiếm
	public int countSearchAppointments(String keyword, String field) {
	    String whereClause;
	    switch (field.toLowerCase()) {
	        case "search by patient name" -> whereClause = "p.name LIKE ?";
	        case "search by doctor name" -> whereClause = "d.name LIKE ?";
	        default -> whereClause = "p.name LIKE ?";
	    }

	    String sql = """
	        SELECT COUNT(*)
	        FROM appointment a
	        LEFT JOIN patient_profile p ON a.patient_code = p.code
	        LEFT JOIN profile d ON a.doctor_code = d.user_code
	        WHERE %s
	    """.formatted(whereClause);

	    try (Connection conn = ConnectionDatabase.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, "%" + keyword + "%");

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) return rs.getInt(1);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return 0;
	}

	// Đếm tổng lịch hẹn theo user (Doctor hoặc Receptionist)
	public int countAppointmentsByUser(String userCode, String role) {
	    String sql = switch (role.toUpperCase()) {
	        case "DOCTOR" -> "SELECT COUNT(*) FROM appointment WHERE doctor_code = ?";
	        case "RECEPTIONIST" -> "SELECT COUNT(*) FROM appointment WHERE created_by = ?";
	        default -> "SELECT COUNT(*) FROM appointment";
	    };

	    try (Connection conn = ConnectionDatabase.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        if (!role.equalsIgnoreCase("ADMIN")) {
	            stmt.setString(1, userCode);
	        }

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) return rs.getInt(1);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return 0;
	}

	// Lấy danh sách lịch hẹn có phân trang theo user
	public List<Appointment> getAppointmentsByUser(String userCode, String role, int offset, int limit) {
	    List<Appointment> list = new ArrayList<>();
	    String where = switch (role.toUpperCase()) {
	        case "DOCTOR" -> "WHERE a.doctor_code = ?";
	        case "RECEPTIONIST" -> "WHERE a.created_by = ?";
	        default -> "";
	    };

	    String sql = """
	        SELECT a.*, 
	               p.name AS patientName, 
	               d.name AS doctorName, 
	               cb.name AS createdByName
	        FROM appointment a
	        LEFT JOIN patient_profile p ON a.patient_code = p.code
	        LEFT JOIN profile d ON a.doctor_code = d.user_code
	        LEFT JOIN profile cb ON a.created_by = cb.user_code
	        %s
	        ORDER BY a.scheduled_date DESC
	        LIMIT ? OFFSET ?
	    """.formatted(where);

	    try (Connection conn = ConnectionDatabase.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        int index = 1;
	        if (!where.isEmpty()) stmt.setString(index++, userCode);
	        stmt.setInt(index++, limit);
	        stmt.setInt(index, offset);

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                list.add(mapResultSetToAppointment(rs));
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return list;
	}

	// Đếm tổng số kết quả tìm kiếm theo user
	public int countSearchAppointmentsByUser(String userCode, String role, String keyword, String field) {
	    String userColumn = role.equalsIgnoreCase("DOCTOR") ? "a.doctor_code" : "a.created_by";
	    String whereField = switch (field.toLowerCase()) {
	        case "search by patient name" -> "p.name LIKE ?";
	        case "search by doctor name" -> "d.name LIKE ?";
	        default -> "p.name LIKE ?";
	    };

	    String sql = """
	        SELECT COUNT(*)
	        FROM appointment a
	        LEFT JOIN patient_profile p ON a.patient_code = p.code
	        LEFT JOIN profile d ON a.doctor_code = d.user_code
	        WHERE %s = ? AND %s
	    """.formatted(userColumn, whereField);

	    try (Connection conn = ConnectionDatabase.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, userCode);
	        stmt.setString(2, "%" + keyword + "%");

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) return rs.getInt(1);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return 0;
	}

	// Tìm kiếm lịch hẹn theo user có phân trang
	public List<Appointment> searchAppointmentsByUser(String userCode, String role, String keyword, String field, int offset, int limit) {
	    List<Appointment> list = new ArrayList<>();
	    String userColumn = role.equalsIgnoreCase("DOCTOR") ? "a.doctor_code" : "a.created_by";
	    String whereField = switch (field.toLowerCase()) {
	        case "search by patient name" -> "p.name LIKE ?";
	        case "search by doctor name" -> "d.name LIKE ?";
	        default -> "p.name LIKE ?";
	    };

	    String sql = """
	        SELECT a.*, 
	               p.name AS patientName, 
	               d.name AS doctorName, 
	               cb.name AS createdByName
	        FROM appointment a
	        LEFT JOIN patient_profile p ON a.patient_code = p.code
	        LEFT JOIN profile d ON a.doctor_code = d.user_code
	        LEFT JOIN profile cb ON a.created_by = cb.user_code
	        WHERE %s = ? AND %s
	        ORDER BY a.scheduled_date DESC
	        LIMIT ? OFFSET ?
	    """.formatted(userColumn, whereField);

	    try (Connection conn = ConnectionDatabase.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, userCode);
	        stmt.setString(2, "%" + keyword + "%");
	        stmt.setInt(3, limit);
	        stmt.setInt(4, offset);

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                list.add(mapResultSetToAppointment(rs));
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return list;
	}



	public List<Appointment> getAppointmentsByDoctorAndStatus(String doctorCode, String status) {
	    String sql = """
	        SELECT a.*, 
	               p.name AS patientName, 
	               d.name AS doctorName, 
	               cb.name AS createdByName
	        FROM appointment a
	        LEFT JOIN patient_profile p ON a.patient_code = p.code
	        LEFT JOIN profile d ON a.doctor_code = d.user_code
	        LEFT JOIN profile cb ON a.created_by = cb.user_code
	        WHERE a.doctor_code = ? 
	          AND a.status = ?
	        ORDER BY a.scheduled_date DESC
	    """;

	    List<Appointment> list = new ArrayList<>();
	    try (Connection con = ConnectionDatabase.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, doctorCode);
	        ps.setString(2, status);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(mapResultSetToAppointment(rs));
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return list;
	}

    public boolean updateAppointmentStatus(Long id, String newStatus) {
        String sql = "UPDATE appointment SET status = ? WHERE id = ?";
        try (Connection con = ConnectionDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateStatusAndNote(Long id, String newStatus, String note) {
        String sql = "UPDATE appointment SET status = ?, note = ? WHERE id = ?";
        try (Connection con = ConnectionDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);      
            ps.setString(2, note);
            ps.setLong(3, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Appointment> searchAppointmentsByDoctor(String keyword, String field, String doctorCode) {
        List<Appointment> result = new ArrayList<>();
        if (doctorCode == null || doctorCode.isBlank()) return result;

        String sql = "SELECT * FROM appointment WHERE doctor_code = ? ";
        if (keyword != null && !keyword.isBlank() && field != null && !field.isBlank()) {
            sql += "AND " + field + " LIKE ?";
        }

        try (Connection conn = ConnectionDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, doctorCode);

            if (keyword != null && !keyword.isBlank() && field != null && !field.isBlank()) {
                stmt.setString(2, "%" + keyword + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public int countPatientsForDoctorToday(String doctorCode, LocalDate today) {
        String sql = "SELECT COUNT(*) FROM appointment " +
                     "WHERE doctor_code = ? AND DATE(scheduled_date) = ? AND status = 'SCHEDULED'";
        try (Connection con = ConnectionDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, doctorCode);
            ps.setDate(2, Date.valueOf(today));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean deleteAppointment(Long id) {
        String sql = "DELETE FROM appointment WHERE id = ?";
        try (Connection con = ConnectionDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Appointment getByAppointmentId(Long id) {
        String sql = "SELECT * FROM appointment WHERE id = ?";
        try (Connection con = ConnectionDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Appointment appointment = new Appointment();
                    appointment.setId(rs.getLong("id"));
                    appointment.setPatientCode(rs.getString("patient_code"));
                    appointment.setDoctorCode(rs.getString("doctor_code"));
                    appointment.setScheduledDate(rs.getTimestamp("scheduled_date")); // dùng Date
                    appointment.setNote(rs.getString("note"));
                    appointment.setStatus(rs.getString("status"));
                    appointment.setCreatedBy(rs.getString("created_by"));
                    
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        appointment.setCreatedAt(new Date(createdAt.getTime()));
                    }

                    // Nếu bảng có JOIN thêm name thì gán luôn
                    try {
                        appointment.setPatientName(rs.getString("patient_name"));
                    } catch (SQLException ignored) {}
                    try {
                        appointment.setDoctorName(rs.getString("doctor_name"));
                    } catch (SQLException ignored) {}
                    try {
                        appointment.setCreatedByName(rs.getString("created_by_name"));
                    } catch (SQLException ignored) {}

                    return appointment;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean isExistingAppointment(Long id) {
        String sql = "SELECT 1 FROM appointment WHERE id = ?";
        try (Connection con = ConnectionDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsByPatientCode(String patientCode) {
        String sql = "SELECT 1 FROM appointment WHERE patient_code = ? AND status = 'SCHEDULED'";
        try (Connection con = ConnectionDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, patientCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void insertAppointment(Appointment a) {
        String sql = "INSERT INTO appointment(patient_code, doctor_code, scheduled_date, note, status, created_by, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectionDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, a.getPatientCode());
            ps.setString(2, a.getDoctorCode());
            ps.setTimestamp(3, new Timestamp(a.getScheduledDate().getTime()));
            ps.setString(4, a.getNote());
            ps.setString(5, a.getStatus());
            ps.setString(6, a.getCreatedBy());

            // ✅ Gán created_at dạng Date
            if (a.getCreatedAt() != null) {
                ps.setDate(7, new java.sql.Date(a.getCreatedAt().getTime()));
            } else {
                ps.setDate(7, new java.sql.Date(System.currentTimeMillis()));
            }

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAppointment(Appointment a) {
        String sql = "UPDATE appointment SET patient_code=?, doctor_code=?, scheduled_date=?, note=?, status=? WHERE id=?";
        try (Connection con = ConnectionDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, a.getPatientCode());
            ps.setString(2, a.getDoctorCode());
            ps.setTimestamp(3, new Timestamp(a.getScheduledDate().getTime()));
            ps.setString(4, a.getNote());
            ps.setString(5, a.getStatus());
            ps.setLong(6, a.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Chuẩn hóa kiểu Date trong mapping
    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setId(rs.getLong("id"));
        a.setPatientCode(rs.getString("patient_code"));
        a.setDoctorCode(rs.getString("doctor_code"));
        a.setScheduledDate(rs.getTimestamp("scheduled_date"));
        a.setNote(rs.getString("note"));
        a.setStatus(rs.getString("status"));
        a.setCreatedBy(rs.getString("created_by"));

        // ✅ Chuyển Timestamp sang java.util.Date
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            a.setCreatedAt(new java.util.Date(createdAt.getTime()));
        }

        // Optional fields
        try { a.setPatientName(rs.getString("patientName")); } catch (SQLException ignore) {}
        try { a.setDoctorName(rs.getString("doctorName")); } catch (SQLException ignore) {}
        try { a.setCreatedByName(rs.getString("createdByName")); } catch (SQLException ignore) {}

        return a;
    }
}
