package com.kien.project.clinicmanagement.service;

import java.time.ZoneId;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import com.kien.project.clinicmanagement.dao.PatientDAO;
import com.kien.project.clinicmanagement.dao.SystemLogDAO;
import com.kien.project.clinicmanagement.model.Patient;
import com.kien.project.clinicmanagement.utils.Session;
import com.toedter.calendar.JDateChooser;

/**
 * Lớp xử lý nghiệp vụ liên quan đến bệnh nhân.
 * Giao tiếp với DAO và hỗ trợ validation, mã hóa, log hệ thống.
 */
public class PatientService {

    private final PatientDAO patientDAO = new PatientDAO();
    private final SystemLogDAO logDAO = new SystemLogDAO();

    
    // Hàm lấy bệnh nhân
    public int countPatients() {
    		return patientDAO.countPatients();
    }
    
    public List<Patient> getPatients(int offset, int limit) {
    		return patientDAO.getPatients(offset, limit);
    }

    
    // Hàm tìm kiếm bệnh nhân
    public List<Patient> searchPatients(String keyword, String field, int offset, int limit) {
        logDAO.logAction(Session.getCurrentUser().getCode(), "Search patient");
        return patientDAO.searchPatients(keyword, field, offset, limit);
    }
    
    public int countSearchPatients(String keyword, String field) {
    		return patientDAO.countSearchPatient(keyword, field);
    }
    
    
    // Code CRUD
    public Patient getByPatientCode(String code) {
        return patientDAO.getByPatientCode(code);
    }

    public boolean isExistingPatient(String code) {
        return patientDAO.isExistingPatient(code);
    }
    
    // Thêm hoặc chỉnh sửa người dùng
    public void saveOrUpdatePatient(Patient patient) {
        boolean isUpdate = isExistingPatient(patient.getCode());
        String currentUserCode = Session.getCurrentUser().getCode();
        
        if (isUpdate) {
            logDAO.logAction(currentUserCode, "Update patient");
            patientDAO.updatePatient(patient);
        } else {
            logDAO.logAction(currentUserCode, "Add new patient");
            patientDAO.addPatient(patient); // ✅ thêm dòng này để thực sự lưu vào DB
        }
    }
    public String generateNextPatientCode() {
        return patientDAO.generateNextPatientCode();
    }

    public boolean deletePatient(String code) {
        logDAO.logAction(Session.getCurrentUser().getCode(), "Delete patient");
        try {
            patientDAO.deletePatient(code);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //Kiểm tra trùng lặp thông tin
    public boolean isPhoneNumberTaken(String phone, String excludeCode) {
    		return patientDAO.isPhoneNumberTaken(phone, excludeCode);
    }
    
    public boolean isEmailTaken(String email, String excludeCode) {
    		return patientDAO.isEmailTaken(email, excludeCode);
    }
    
    public boolean isCitizenIdTaken(String citizenId, String excludeCode) {
    		return patientDAO.isCitizenIdTaken(citizenId, excludeCode);
    }
    
    public void savePatient(Patient patient, boolean isUpdate) {
        if (isUpdate) {
            logDAO.logAction(Session.getCurrentUser().getCode(), "Update patient");
            patientDAO.updatePatient(patient);
        } else {
            logDAO.logAction(Session.getCurrentUser().getCode(), "Add new patient");
            patientDAO.addPatient(patient);
        }
    }
    
    // Gắn dữ liệu từ form vào đối tượng PatientProfile
    public void fillPatientData(Patient patient, JTextField nameField, JComboBox<String> genderCombo,
                                 JDateChooser dobChooser, JTextField emailField, JTextField phoneField,
                                 JTextField addressField, JTextField citizenIdField) {
        patient.setName(nameField.getText().trim());
        patient.setGender((String) genderCombo.getSelectedItem());
        patient.setDateOfBirth(dobChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        patient.setEmail(emailField.getText().trim());
        patient.setPhoneNumber(phoneField.getText().trim());
        patient.setAddress(addressField.getText().trim());
        patient.setCitizenId(citizenIdField.getText().trim());
    }
    
    
    
    public int getTotalUsers() {
        List<Patient> all = patientDAO.getAllPatients();
        return all != null ? all.size() : 0;
    }

    // ================== Hàm thống kê User ====================
    public int countByGender(String gender) {
        List<Patient> all = patientDAO.getAllPatients();
        if (all == null) return 0;
        return (int) all.stream()
                .filter(u -> u.getGender() != null && u.getGender().equalsIgnoreCase(gender))
                .count();
    }

    public int[] getUserStatistics() {
        List<Patient> all = patientDAO.getAllPatients();
        int total = 0, male = 0, female = 0;
        if (all != null) {
            total = all.size();
            for (Patient u : all) {
                if (u.getGender() != null) {
                    if (u.getGender().equalsIgnoreCase("Male")) male++;
                    else if (u.getGender().equalsIgnoreCase("Female")) female++;
                }
            }
        }
        return new int[] { total, male, female };
    }
}
