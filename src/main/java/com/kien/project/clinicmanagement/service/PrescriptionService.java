package com.kien.project.clinicmanagement.service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTextField;

import com.kien.project.clinicmanagement.dao.PrescriptionDAO;
import com.kien.project.clinicmanagement.dao.PrescriptionDetailDAO;
import com.kien.project.clinicmanagement.dao.SystemLogDAO;
import com.kien.project.clinicmanagement.model.Prescription;
import com.kien.project.clinicmanagement.utils.Session;
import com.toedter.calendar.JDateChooser;

/**
 * Lớp xử lý nghiệp vụ liên quan đến đơn thuốc.
 * Giao tiếp với DAO và hỗ trợ validation, log hệ thống.
 */
public class PrescriptionService {

    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
    private final PrescriptionDetailDAO prescriptionDetailDAO = new PrescriptionDetailDAO();
    private final SystemLogDAO logDAO = new SystemLogDAO();

    public List<Prescription> getAllPrescriptions() {
        return prescriptionDAO.getAllPrescriptions();
    }

    public Prescription getById(Long id) {
        return prescriptionDAO.getById(id);
    }
    
    public BigDecimal calculateTotalPriceOfAllPrescriptions() {
        List<Prescription> prescriptions = prescriptionDAO.getAllPrescriptions();
        if (prescriptions == null || prescriptions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Prescription p : prescriptions) {
            if (p.getTotalPrice() != null) {
                total = total.add(p.getTotalPrice());
            }
        }
        return total;
    }

    public String savePrescription(Prescription prescription) {
        logDAO.logAction(Session.getCurrentUser().getCode(), "Add new prescription");
        prescriptionDAO.insertPrescription(prescription);
        return null;
    }
    
    public BigDecimal calculateTotalPriceForPrescription(Long prescriptionId) {
        if (prescriptionId == null) {
            return BigDecimal.ZERO;
        }
        // Gọi trực tiếp hàm lấy tổng trong DAO, tối ưu hơn là load toàn bộ danh sách rồi tính
        return prescriptionDetailDAO.getTotalPriceByPrescriptionId(prescriptionId);
    }

    
    public void updateTotalPriceForPrescription(Prescription prescription) {
        if (prescription == null || prescription.getId() == null) return;

        BigDecimal totalPrice = calculateTotalPriceForPrescription(prescription.getId());
        prescription.setTotalPrice(totalPrice);

        // Cập nhật lại trong DB
        prescriptionDAO.updateTotalPrice(prescription.getId(), totalPrice);
    }
    
    public List<Prescription> getPrescriptionsByPatientCode(String patientCode) {
        if (patientCode == null || patientCode.trim().isEmpty()) {
            return List.of();
        }
        return prescriptionDAO.getPrescriptionsByPatientCode(patientCode);
    }
    
    public Long generateNewPrescriptionId() {
        Long lastId = prescriptionDAO.getLastPrescriptionId();
        if (lastId == null) {
            return 1L;
        }
        return lastId + 1;
    }
    

    /**
     * Gắn dữ liệu từ form vào đối tượng Prescription
     */
    public void fillPrescriptionData(Prescription prescription,
                                     JTextField medicalResultIdField,
                                     JTextField patientCodeField,
                                     JDateChooser prescriptionDateChooser,
                                     JTextField totalPriceField) {

        // Medical Result ID
        String medicalResultIdText = medicalResultIdField.getText().trim();
        if (!medicalResultIdText.isEmpty()) {
            prescription.setMedicalResultId(Long.valueOf(medicalResultIdText));
        }

        // Patient Code
        String patientCode = patientCodeField.getText().trim();
        prescription.setPatientCode(patientCode.isEmpty() ? null : patientCode);

        // Prescription Date
        Date selectedDate = prescriptionDateChooser.getDate();
        if (selectedDate != null) {
            prescription.setPrescriptionDate(
                    selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            );
        } else {
            prescription.setPrescriptionDate(null);
        }

        // Total Price
        String totalPriceText = totalPriceField.getText().trim();
        if (!totalPriceText.isEmpty()) {
            try {
                prescription.setTotalPrice(new BigDecimal(totalPriceText));
            } catch (NumberFormatException e) {
                prescription.setTotalPrice(BigDecimal.ZERO);
            }
        } else {
            prescription.setTotalPrice(BigDecimal.ZERO);
        }
    }
    
    /**
     * Thống kê doanh thu theo tháng (yyyy-MM dạng key, giá trị là tổng tiền).
     */
    public Map<String, BigDecimal> getRevenueByMonth() {
        List<Prescription> prescriptions = prescriptionDAO.getAllPrescriptions();
        Map<String, BigDecimal> revenueMap = new HashMap<>();

        if (prescriptions != null) {
            for (Prescription p : prescriptions) {
                if (p.getPrescriptionDate() != null && p.getTotalPrice() != null) {
                    // Lấy key theo dạng "YYYY-MM"
                    String monthKey = p.getPrescriptionDate().getYear() + "-" 
                                    + String.format("%02d", p.getPrescriptionDate().getMonthValue());

                    // Cộng dồn vào doanh thu tháng
                    revenueMap.merge(monthKey, p.getTotalPrice(), BigDecimal::add);
                }
            }
        }
        return revenueMap;
    }

}
