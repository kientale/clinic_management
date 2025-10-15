package com.kien.project.clinicmanagement.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.kien.project.clinicmanagement.dao.AppointmentDAO;
import com.kien.project.clinicmanagement.dao.SystemLogDAO;
import com.kien.project.clinicmanagement.model.Appointment;
import com.kien.project.clinicmanagement.utils.Session;

public class AppointmentService {
	private final AppointmentDAO appointmentDAO = new AppointmentDAO();
	private final SystemLogDAO logDAO = new SystemLogDAO();
	
	// Code dành cho Admin
	public int countAppointments() {
		return appointmentDAO.countAppointments();
	}
	
	public List<Appointment> getAppointments(int offset, int limit) {
		return appointmentDAO.getAppointments(offset, limit);
	}
	
	public List<Appointment> searchAppointments(String keyword, String field, int offset, int limit) {
		return appointmentDAO.searchAppointments(keyword, field, offset, limit);
	}
	
	public int countSearchAppointments(String keyword, String field) {
		return appointmentDAO.countSearchAppointments(keyword, field);
	}
	
	// Hàm dành cho Doctor và Receptionist
	public int countAppointmentsByUser(String userCode, String role) {
	    return appointmentDAO.countAppointmentsByUser(userCode, role);
	}

	public List<Appointment> getAppointmentsByUser(String userCode, String role, int offset, int limit) {
	    return appointmentDAO.getAppointmentsByUser(userCode, role, offset, limit);
	}

	public int countSearchAppointmentsByUser(String userCode, String role, String keyword, String field) {
	    return appointmentDAO.countSearchAppointmentsByUser(userCode, role, keyword, field);
	}

	public List<Appointment> searchAppointmentsByUser(String userCode, String role, String keyword, String field, int offset, int limit) {
	    return appointmentDAO.searchAppointmentsByUser(userCode, role, keyword, field, offset, limit);
	}

	public boolean cancelAppointment(Long id, String reason) {
		if (id == null)
			return false;
		if (Session.getCurrentUser() != null) {
			logDAO.logAction(Session.getCurrentUser().getCode(), "Cancel appointment: " + reason);
		}
		return appointmentDAO.updateStatusAndNote(id, "CANCELLED", reason);
	}

	public boolean updateAppointmentStatus(Long id, String newStatus) {
		if (id == null || newStatus == null || newStatus.isBlank()) {
			return false;
		}

		// Ghi log nếu có user
		if (Session.getCurrentUser() != null) {
			logDAO.logAction(Session.getCurrentUser().getCode(), "Update appointment status to: " + newStatus);
		}

		return appointmentDAO.updateAppointmentStatus(id, newStatus);
	}

	public int countPatientsForDoctorToday(String doctorCode) {
		LocalDate today = LocalDate.now();
		return appointmentDAO.countPatientsForDoctorToday(doctorCode, today);
	}

	public boolean deleteAppointment(Long id) {
		if (id == null)
			return false;
		if (Session.getCurrentUser() != null) {
			logDAO.logAction(Session.getCurrentUser().getCode(), "Delete appointment");
		}
		return appointmentDAO.deleteAppointment(id);
	}

	public Appointment getByAppointmentId(Long id) {
		return (id == null) ? null : appointmentDAO.getByAppointmentId(id);
	}

	public boolean isExistingAppointment(Long id) {
		return id != null && appointmentDAO.isExistingAppointment(id);
	}

	public String saveAppointment(Appointment appointment, boolean isUpdate) {
	    try {
	        // Kiểm tra null ở mức an toàn tối thiểu (để tránh crash)
	        if (appointment == null) {
	            return "Appointment data is null.";
	        }

	        // --- Xử lý logic lưu ---
	        if (!isUpdate) {
	            // Gán người tạo & thời gian tạo
	            if (Session.getCurrentUser() != null) {
	                appointment.setCreatedBy(Session.getCurrentUser().getCode());
	            } else {
	                appointment.setCreatedBy("SYSTEM");
	            }
	            appointment.setCreatedAt(new Date());

	            // ✅ Kiểm tra trùng patient code (chỉ khi thêm mới)
	            if (appointmentDAO.existsByPatientCode(appointment.getPatientCode())) {
	                return "Patient already exists in the appointment list.";
	            }

	            // Ghi log
	            if (Session.getCurrentUser() != null) {
	                logDAO.logAction(Session.getCurrentUser().getCode(), "Add new appointment");
	            }

	            // Lưu DB
	            appointmentDAO.insertAppointment(appointment);
	        } 
	        else {
	            // Ghi log update
	            if (Session.getCurrentUser() != null) {
	                logDAO.logAction(Session.getCurrentUser().getCode(), "Update appointment");
	            }

	            // Update DB
	            appointmentDAO.updateAppointment(appointment);
	        }

	    } catch (Exception ex) {
	        ex.printStackTrace();
	        return "Failed to save appointment: " + ex.getMessage();
	    }

	    return null;
	}

	public String saveOrUpdateAppointment(Appointment appointment) {
		boolean isUpdate = appointment.getId() != null && isExistingAppointment(appointment.getId());
		return saveAppointment(appointment, isUpdate);
	}

	
	
	// ================== Hàm thống kê Appointment ====================
	/**
	 * Tổng số Appointment
	 */
	public int getTotalAppointments() {
		List<Appointment> all = appointmentDAO.getAllAppointments();
		return all != null ? all.size() : 0;
	}

	/**
	 * Đếm số Appointment theo status
	 */
	public int countByStatus(String status) {
		List<Appointment> all = appointmentDAO.getAllAppointments();
		if (all == null)
			return 0;
		return (int) all.stream().filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase(status)).count();
	}

	/**
	 * Lấy thống kê Appointment: [0] = total, [1] = Scheduled, [2] = Cancelled, [3]
	 * = Checked-In, [4] = Completed
	 */
	public int[] getAppointmentStatistics() {
		List<Appointment> all = appointmentDAO.getAllAppointments();
		int total = 0, scheduled = 0, cancelled = 0, checkedIn = 0, completed = 0;

		if (all != null) {
			total = all.size();
			for (Appointment a : all) {
				if (a.getStatus() != null) {
					String status = a.getStatus().trim().toLowerCase();
					switch (status) {
					case "scheduled" -> scheduled++;
					case "cancelled" -> cancelled++;
					case "checked-in" -> checkedIn++;
					case "completed" -> completed++;
					}
				}
			}
		}
		return new int[] { total, scheduled, cancelled, checkedIn, completed };
	}
}
