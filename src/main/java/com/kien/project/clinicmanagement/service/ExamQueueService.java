package com.kien.project.clinicmanagement.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.kien.project.clinicmanagement.dao.ExamQueueDAO;
import com.kien.project.clinicmanagement.dao.SystemLogDAO;
import com.kien.project.clinicmanagement.model.ExamQueue;
import com.kien.project.clinicmanagement.utils.Session;

public class ExamQueueService {

    private final ExamQueueDAO examQueueDAO = new ExamQueueDAO();
    private final SystemLogDAO logDAO = new SystemLogDAO();

    
    // Code dành cho Admin
    public int countExamQueues() {
    		return examQueueDAO.countExamQueues();
    }
    
    public List<ExamQueue> getExamQueues(int offset, int limit) {
    		return examQueueDAO.getExamQueues(offset, limit);
    }
    
    public List<ExamQueue> searchExamQueues(String keyword, String field, int offset, int limit) {
    		return examQueueDAO.searchExamQueues(keyword, field, offset, limit);
    }
    
    public int countSearchExamQueues(String keyword, String field) {
    		return examQueueDAO.countSearchExamQueues(keyword, field);
    }
    
    // Code dành cho Lễ tân
    public int countExamQueuesByUser(String userCode, String role) {
    		return examQueueDAO.countExamQueuesByUser(userCode, role);
    }
    
    public List<ExamQueue> getExamQueuesByUser(String userCode, String role, int offset, int limit) {
	    return examQueueDAO.getExamQueuesByUser(userCode, role, offset, limit);
	}

	public int countSearchExamQueuesByUser(String userCode, String role, String keyword, String field) {
	    return examQueueDAO.countSearchExamQueuesByUser(userCode, role, keyword, field);
	}

	public List<ExamQueue> searchExamQueuesByUser(String userCode, String role, String keyword, String field, int offset, int limit) {
	    return examQueueDAO.searchExamQueuesByUser(userCode, role, keyword, field, offset, limit);
	}
    
    public List<ExamQueue> getAllWaitingExamQueues() {
        return examQueueDAO.getWaitingExamQueues(); 
    }
    
    public List<ExamQueue> getExamQueuesByDoctorCode(String doctorCode) {
        if (doctorCode == null || doctorCode.isBlank()) {
            return List.of();
        }
        return examQueueDAO.getWaitingQueuesByDoctor(doctorCode);
    }
    
    public boolean updateExamQueueStatus(Long id, String newStatus) {
        if (id == null || newStatus == null || newStatus.isBlank()) {
            return false;
        }

        // Ghi log nếu có user
        if (Session.getCurrentUser() != null) {
            logDAO.logAction(Session.getCurrentUser().getCode(),
                    "Update exam queue status to: " + newStatus);
        }

        return examQueueDAO.updateExamQueueStatus(id, newStatus);
    }

    public List<ExamQueue> searchExamQueues(String keyword, String field) {
        // Ghi log hành động tìm kiếm (nếu có user)
        if (Session.getCurrentUser() != null) {
            logDAO.logAction(Session.getCurrentUser().getCode(), "Search exam queue");
        }
        return examQueueDAO.searchExamQueues(keyword, field);
    }
    
    public int countPatientsForDoctorToday(String doctorCode) {
        LocalDate today = LocalDate.now();
        return examQueueDAO.countPatientsForDoctorToday(doctorCode, today);
    }

    public boolean deleteExamQueue(Long id) {
        if (id == null) return false;
        if (Session.getCurrentUser() != null) {
            logDAO.logAction(Session.getCurrentUser().getCode(), "Delete exam queue");
        }
        return examQueueDAO.deleteExamQueue(id);
    }

    public ExamQueue getByExamQueueId(Long id) {
        return (id == null) ? null : examQueueDAO.getByExamQueueId(id);
    }

    public boolean isExistingExamQueue(Long id) {
        return id != null && examQueueDAO.isExistingExamQueue(id);
    }

    /**
     * Tính queueNumber tiếp theo cho bác sĩ (dựa trên toàn bộ queue hiện tại
     * của bác sĩ). Nếu bạn muốn giới hạn theo ngày thì thay DAO method tương ứng.
     */
    public int getNextQueueNumberForDoctor(String doctorCode) {
        if (doctorCode == null || doctorCode.isBlank()) return 1;

        int maxNumber = examQueueDAO.getMaxQueueNumberForDoctorToday(doctorCode, LocalDate.now());
        return maxNumber + 1;
    }

    /**
     * Lưu hoặc cập nhật. Trả về:
     *   - null khi thành công
     *   - String (message) khi có lỗi (dùng để hiển thị cho user)
     */
    public String saveQueue(ExamQueue examQueue, boolean isUpdate) {
        if (examQueue == null) return "Queue data is null.";
        if (examQueue.getPatientCode() == null || examQueue.getPatientCode().isBlank()) {
            return "Patient code is required.";
        }
        if (examQueue.getDoctorCode() == null || examQueue.getDoctorCode().isBlank()) {
            return "Doctor code is required.";
        }

        // Thêm mới: set createdBy, createdAt và kiểm tra trùng patient nếu cần
        if (!isUpdate) {
            if (Session.getCurrentUser() != null) {
                examQueue.setCreatedBy(Session.getCurrentUser().getCode());
            } else {
                examQueue.setCreatedBy("SYSTEM");
            }
            examQueue.setCreatedAt(new Date());

            // Nếu muốn hạn chế trùng theo ngày, implement existsByPatientCodeAndDate ở DAO.
            if (examQueueDAO.existsByPatientCode(examQueue.getPatientCode())) {
                return "Patient already exists in the queue.";
            }
        }

        try {
            if (isUpdate) {
                if (Session.getCurrentUser() != null) {
                    logDAO.logAction(Session.getCurrentUser().getCode(), "Update exam queue");
                }
                examQueueDAO.updateExamQueue(examQueue);
            } else {
                if (Session.getCurrentUser() != null) {
                    logDAO.logAction(Session.getCurrentUser().getCode(), "Add new exam queue");
                }
                examQueueDAO.insertExamQueue(examQueue);
            }
        } catch (Exception ex) {
            // Bắt và trả message lỗi (ví dụ: constraint violation)
            // Nó giúp controller hiển thị lỗi dễ hiểu hơn.
            ex.printStackTrace();
            return "Failed to save queue: " + ex.getMessage();
        }

        return null;
    }

    public String saveOrUpdateExamQueue(ExamQueue examQueue) {
        boolean isUpdate = examQueue.getId() != null && isExistingExamQueue(examQueue.getId());
        return saveQueue(examQueue, isUpdate);
    }
    
 // ================== Hàm thống kê ExamQueue ====================

    /**
     * Tổng số ExamQueue
     */
    public int getTotalExamQueues() {
        List<ExamQueue> all = examQueueDAO.getAllExamQueues();
        return all != null ? all.size() : 0;
    }

    /**
     * Đếm số ExamQueue theo status
     */
    public int countByStatus(String status) {
        List<ExamQueue> all = examQueueDAO.getAllExamQueues();
        if (all == null) return 0;
        return (int) all.stream()
                .filter(eq -> eq.getStatus() != null && eq.getStatus().equalsIgnoreCase(status))
                .count();
    }

    /**
     * Lấy thống kê ExamQueue:
     * [0] = total, [1] = Waiting, [2] = Done
     */
    public int[] getExamQueueStatistics() {
        List<ExamQueue> all = examQueueDAO.getAllExamQueues();
        int total = 0, waiting = 0, done = 0;

        if (all != null) {
            total = all.size();
            for (ExamQueue eq : all) {
                if (eq.getStatus() != null) {
                    String st = eq.getStatus().trim().toLowerCase();
                    switch (st) {
                        case "waiting" -> waiting++;
                        case "done" -> done++;
                    }
                }
            }
        }
        return new int[] { total, waiting, done };
    }
}
