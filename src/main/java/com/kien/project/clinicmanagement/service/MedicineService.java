package com.kien.project.clinicmanagement.service;

import java.awt.Component;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.JTextField;

import com.kien.project.clinicmanagement.dao.MedicineDAO;
import com.kien.project.clinicmanagement.dao.SystemLogDAO;
import com.kien.project.clinicmanagement.model.Medicine;
import com.kien.project.clinicmanagement.utils.Session;

/**
 * Lớp xử lý nghiệp vụ liên quan đến thuốc.
 * Giao tiếp với DAO và hỗ trợ validation, log hệ thống.
 */
public class MedicineService {

    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final SystemLogDAO logDAO = new SystemLogDAO();

    // Hàm lấy thuốc
    public int countMedicines() {
    		return medicineDAO.countMedicines();
    }
    
    public List<Medicine> getMedicines(int offset, int limit) {
    		return medicineDAO.getMedicines(offset, limit);
    }
    
    // Hàm tìm kiếm thuốc
    public List<Medicine> searchMedicines(String keyword, String field, int offset, int limit) {
    		logDAO.logAction(Session.getCurrentUser().getCode(), "Search medicine");
    		return medicineDAO.searchMedicines(keyword, field, offset, limit);
    }
    
    public int countSearchMedicines(String keyword, String field) {
    		return medicineDAO.countSearchMedicines(keyword, field);
    }
    

    // Kiểm tra
    public Medicine getByMedicineCode(String code) {
        return medicineDAO.getByMedicineCode(code);
    }

    public boolean isExistingMedicine(String code) {
        return medicineDAO.isExistingMedicine(code);
    }
    
    public boolean isExistingName(String name, String excludeCode) {
    		return medicineDAO.isExistingName(name, excludeCode);
    }
    
    // Thêm hoặc sửa thuốc
    public void saveOrUpdateMedicine(Medicine medicine) {
        boolean isUpdate = isExistingMedicine(medicine.getCode());
        String usserCode = Session.getCurrentUser().getCode();

        if (isUpdate) {
            logDAO.logAction(usserCode, "Update medicine");
            medicineDAO.updateMedicine(medicine);
        } else {
            logDAO.logAction(usserCode, "Add new medicine");
            medicineDAO.addMedicine(medicine);
        }
    }
    
    public String generateNextMedicineCode() {
        return medicineDAO.generateNextMedicineCode();
    }

    // Xóa thuốc
    public boolean deleteMedicine(String code) {
        logDAO.logAction(Session.getCurrentUser().getCode(), "Delete medicine");
        try {
            medicineDAO.deleteMedicine(code);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean addStock(String code, int quantity, Component parentComponent) {
        if (quantity <= 0) {
            return false;
        }

        try {
            logDAO.logAction(Session.getCurrentUser().getCode(), "Add medicine stock");
            medicineDAO.addStock(code, quantity);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean reduceStock(String medicineCode, int quantity) {
        Medicine medicine = medicineDAO.getByMedicineCode(medicineCode);
        if (medicine == null) {
            return false;
        }
        if (medicine.getQuantity() < quantity) {
            return false; // không đủ thuốc trong kho
        }

        int newQuantity = medicine.getQuantity() - quantity;
        medicineDAO.updateQuantity(medicineCode, newQuantity);
        
        logDAO.logAction(Session.getCurrentUser().getCode(),
            "Reduced medicine stock: " + medicineCode + ", quantity: " + quantity);
        
        return true;
    }

    public boolean increaseStock(String medicineCode, int quantity) {
        Medicine medicine = medicineDAO.getByMedicineCode(medicineCode);
        if (medicine == null) {
            return false;
        }
        int newQuantity = medicine.getQuantity() + quantity;
        medicineDAO.updateQuantity(medicineCode, newQuantity);

        logDAO.logAction(Session.getCurrentUser().getCode(),
            "Increased medicine stock: " + medicineCode + ", quantity: " + quantity);

        return true;
    }

    public void fillMedicineData(Medicine medicine,
                                 JTextField nameField,
                                 JTextField descriptionField,
                                 JTextField unitField,
                                 JTextField priceField,
                                 JTextField minAgeField,
                                 JTextField maxAgeField,
                                 JTextField quantityField) {
        medicine.setName(nameField.getText().trim());
        medicine.setDescription(descriptionField.getText().trim());
        medicine.setUnit(unitField.getText().trim());
        medicine.setPrice(new BigDecimal(priceField.getText().trim()));
        medicine.setMinAge(Integer.parseInt(minAgeField.getText().trim()));
        medicine.setMaxAge(Integer.parseInt(maxAgeField.getText().trim()));
        medicine.setQuantity(Integer.parseInt(quantityField.getText().trim()));
    }
    
 // ================== Hàm thống kê Medicine ====================

    /**
     * Tổng số loại thuốc (distinct medicine)
     */
    public int getTotalMedicines() {
        List<Medicine> all = medicineDAO.getAllMedicines();
        return all != null ? all.size() : 0;
    }

    /**
     * Tổng số lượng tồn kho (sum quantity)
     */
    public int getTotalStock() {
        List<Medicine> all = medicineDAO.getAllMedicines();
        if (all == null) return 0;
        return all.stream()
                  .mapToInt(Medicine::getQuantity)
                  .sum();
    }

    /**
     * Đếm số thuốc sắp hết (<= threshold, mặc định 10)
     */
    public int countLowStockMedicines(int threshold) {
        List<Medicine> all = medicineDAO.getAllMedicines();
        if (all == null) return 0;
        return (int) all.stream()
                        .filter(m -> m.getQuantity() <= threshold)
                        .count();
    }

    /**
     * Đếm số thuốc còn nhiều hàng (> threshold)
     */
    public int countNormalStockMedicines(int threshold) {
        List<Medicine> all = medicineDAO.getAllMedicines();
        if (all == null) return 0;
        return (int) all.stream()
                        .filter(m -> m.getQuantity() > threshold)
                        .count();
    }

    /**
     * Trả về mảng thống kê Medicine:
     * [0] = tổng số loại thuốc
     * [1] = tổng số lượng tồn kho
     * [2] = số thuốc sắp hết (<= 10)
     * [3] = số thuốc còn nhiều (> 10)
     */
    public int[] getMedicineStatistics() {
        List<Medicine> all = medicineDAO.getAllMedicines();
        int totalMedicines = 0, totalStock = 0, lowStock = 0, normalStock = 0;

        if (all != null) {
            totalMedicines = all.size();
            for (Medicine m : all) {
                int qty = m.getQuantity();
                totalStock += qty;
                if (qty <= 10) lowStock++;
                else normalStock++;
            }
        }
        return new int[] { totalMedicines, totalStock, lowStock, normalStock };
    }
}
