package com.kien.project.clinicmanagement.service;

import java.math.BigDecimal;
import java.util.List;

import com.kien.project.clinicmanagement.dao.PrescriptionDetailDAO;
import com.kien.project.clinicmanagement.dao.SystemLogDAO;
import com.kien.project.clinicmanagement.model.PrescriptionDetail;
import com.kien.project.clinicmanagement.utils.Session;

/**
 * Lớp xử lý nghiệp vụ liên quan đến chi tiết đơn thuốc. Giao tiếp với DAO và hỗ
 * trợ validation, log hệ thống.
 */
public class PrescriptionDetailService {

	private final PrescriptionDetailDAO prescriptionDetailDAO = new PrescriptionDetailDAO();
	private final SystemLogDAO logDAO = new SystemLogDAO();
	private final MedicineService medicineService = new MedicineService();

	public List<PrescriptionDetail> getAllDetails() {
		return prescriptionDetailDAO.getAllPrescriptionDetails();
	}

	public List<PrescriptionDetail> getPrescriptionDetailsByPrescriptionId(long prescriptionId) {
		return prescriptionDetailDAO.getPrescriptionDetailsByPrescriptionId(prescriptionId);
	}

	public BigDecimal calculateTotalPrice(int quantity, BigDecimal unitPrice) {
		if (unitPrice == null) {
			return BigDecimal.ZERO;
		}
		return unitPrice.multiply(new BigDecimal(quantity));
	}

	public PrescriptionDetail getById(Long id) {
		return prescriptionDetailDAO.getById(id);
	}

	public boolean isExistingDetail(Long id) {
		return prescriptionDetailDAO.isExistingPrescriptionDetail(id);
	}

	public String saveDetail(PrescriptionDetail detail) {
		logDAO.logAction(Session.getCurrentUser().getCode(), "Add new prescription detail");
		prescriptionDetailDAO.addPrescriptionDetail(detail);
		return null;
	}

	public String saveOrUpdateDetail(PrescriptionDetail detail) {
		boolean isUpdate = (detail.getId() != null);

		if (isUpdate) {
			// Lấy detail cũ
			PrescriptionDetail oldDetail = prescriptionDetailDAO.getById(detail.getId());

			int oldQuantity = oldDetail.getQuantity();
			int newQuantity = detail.getQuantity();

			if (newQuantity > oldQuantity) {
				int diff = newQuantity - oldQuantity;
				// Giảm stock thuốc đi diff
				boolean success = medicineService.reduceStock(detail.getMedicineCode(), diff);
				if (!success) {
					return "Not enough medicine stock to increase quantity.";
				}
			} else if (newQuantity < oldQuantity) {
				int diff = oldQuantity - newQuantity;
				// Tăng stock thuốc lên diff
				medicineService.increaseStock(detail.getMedicineCode(), diff);
			}

			// Cập nhật detail
			prescriptionDetailDAO.updatePrescriptionDetail(detail);

		} else {
			// Thêm mới, giảm stock thuốc theo quantity
			boolean success = medicineService.reduceStock(detail.getMedicineCode(), detail.getQuantity());
			if (!success) {
				return "Not enough medicine stock.";
			}

			prescriptionDetailDAO.addPrescriptionDetail(detail);
		}

		return null; // thành công
	}

	public boolean deleteDetail(Long detailId) {
		PrescriptionDetail detail = prescriptionDetailDAO.getById(detailId);
		if (detail == null) {
			return false;
		}

		// Tăng stock thuốc theo quantity của detail
		medicineService.increaseStock(detail.getMedicineCode(), detail.getQuantity());

		return prescriptionDetailDAO.deletePrescriptionDetail(detailId);
	}

	public void fillDetailData(PrescriptionDetail detail, Long prescriptionId, String medicineCode, String medicineName,
			String dosage, int quantity, java.math.BigDecimal unitPrice, java.math.BigDecimal totalPrice,
			String usageInstructions) {
		detail.setPrescriptionId(prescriptionId);
		detail.setMedicineCode(medicineCode);
		detail.setMedicineName(medicineName);
		detail.setDosage(dosage);
		detail.setQuantity(quantity);
		detail.setUnitPrice(unitPrice);

		// Nếu totalPrice null thì tự động tính
		if (totalPrice == null) {
			totalPrice = calculateTotalPrice(quantity, unitPrice);
		}
		detail.setTotalPrice(totalPrice);
		detail.setUsageInstructions(usageInstructions);
	}

}
