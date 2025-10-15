package com.kien.project.clinicmanagement.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.kien.project.clinicmanagement.model.ExamQueue;
import com.kien.project.clinicmanagement.model.MedicalResult;
import com.kien.project.clinicmanagement.model.Medicine;
import com.kien.project.clinicmanagement.model.Patient;
import com.kien.project.clinicmanagement.model.Prescription;
import com.kien.project.clinicmanagement.model.PrescriptionDetail;
import com.kien.project.clinicmanagement.service.ExamQueueService;
import com.kien.project.clinicmanagement.service.MedicalResultService;
import com.kien.project.clinicmanagement.service.MedicineService;
import com.kien.project.clinicmanagement.service.PatientService;
import com.kien.project.clinicmanagement.service.PrescriptionDetailService;
import com.kien.project.clinicmanagement.service.PrescriptionService;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.Session;
import com.kien.project.clinicmanagement.view.medicalexaminate.MedicalExaminateView;
import com.kien.project.clinicmanagement.view.medicine.MedicineSelectionView;
import com.kien.project.clinicmanagement.view.patient.PatientProfileView;
import com.kien.project.clinicmanagement.view.prescription.PrescriptionDetailFormView;

public class MedicalExaminateController {
	private final MedicalExaminateView medicalExaminateView;
	private PrescriptionDetailFormView prescriptionDetailFormView;
	private final ExamQueueService examQueueService = new ExamQueueService();
	private final MedicalResultService medicalResultService = new MedicalResultService();
	private final MedicineService medicineService = new MedicineService();
	private final PrescriptionService prescriptionService = new PrescriptionService();
	private final PrescriptionDetailService prescriptionDetailService = new PrescriptionDetailService();
	private final PatientService patientService = new PatientService();

	private List<ExamQueue> allExamQueues;
	private List<PrescriptionDetail> prescriptionDetails = new ArrayList<>();

	public MedicalExaminateController(MedicalExaminateView medicalExaminateView) {
		this.medicalExaminateView = medicalExaminateView;

		loadAllExamQueues();
		initMedicalExaminateActions();
	}

	private void initMedicalExaminateActions() {
		medicalExaminateView.getBtnExaminate().addActionListener(e -> openExaminationForm());
		
		medicalExaminateView.getBtnDone().addActionListener(e -> saveMedicalResult());
		medicalExaminateView.getAddPrescriptionButton().addActionListener(e -> openPrescriptionDetailForm());
		medicalExaminateView.getEditPrescriptionButton().addActionListener(e -> editPrescriptionDetail());
		medicalExaminateView.getDeletePrescriptionButton().addActionListener(e -> deletePrescriptionDetail());
		medicalExaminateView.getAddAppointmentButton().addActionListener(e -> openAddAppointmentForm());
		
		medicalExaminateView.getBtnViewPatientProfile().addActionListener(e -> viewSelectedPatientProfile());
	}

	private void openExaminationForm() {
		int selectedRow = medicalExaminateView.getExamQueueTable().getSelectedRow();
		if (selectedRow == -1) {
			medicalExaminateView.showWarning("Please select a patient from the exam queue.");
			return;
		}
		int modelRow = medicalExaminateView.getExamQueueTable().convertRowIndexToModel(selectedRow);
		if (modelRow < 0 || modelRow >= allExamQueues.size()) {
			medicalExaminateView.showWarning("Invalid patient selection.");
			return;
		}

		ExamQueue selectedExamQueue = allExamQueues.get(modelRow);
		fillExaminationForm(selectedExamQueue);
		medicalExaminateView.getAddPrescriptionButton().setEnabled(true);
		medicalExaminateView.getEditPrescriptionButton().setEnabled(true);
		medicalExaminateView.getDeletePrescriptionButton().setEnabled(true);
		medicalExaminateView.getAddAppointmentButton().setEnabled(true);
		medicalExaminateView.getBtnDone().setEnabled(true);
		medicalExaminateView.getBtnViewPatientProfile().setEnabled(true);
	}

	private void fillExaminationForm(ExamQueue examQueue) {
		medicalExaminateView.getTxtExamQueueId().setText(String.valueOf(examQueue.getId()));
		medicalExaminateView.getTxtDoctorCode().setText(String.valueOf(examQueue.getDoctorCode()));
		medicalExaminateView.getTxtDoctorName().setText(String.valueOf(examQueue.getDoctorName()));
		medicalExaminateView.getTxtPatientCode().setText(String.valueOf(examQueue.getPatientCode()));
		medicalExaminateView.getTxtPatientName().setText(String.valueOf(examQueue.getPatientName()));
		medicalExaminateView.getTxtExamDate().setText(String.valueOf(LocalDate.now()));

		Patient patient = patientService.getByPatientCode(examQueue.getPatientCode());
		if (patient != null) {
			medicalExaminateView.getTxtEmail().setText(patient.getEmail());
			medicalExaminateView.getTxtGender().setText(patient.getGender());
			medicalExaminateView.getTxtPhone().setText(patient.getPhoneNumber());
			medicalExaminateView.getTxtAddress().setText(patient.getAddress());
			medicalExaminateView.getTxtCitizenId().setText(patient.getCitizenId());
			medicalExaminateView.getTxtDateOfBirth().setText(patient.getDateOfBirth().toString());
		}
	}

	private void clearExaminationForm() {
		medicalExaminateView.getTxtExamQueueId().setText("");
		medicalExaminateView.getTxtDoctorCode().setText("");
		medicalExaminateView.getTxtDoctorName().setText("");
		medicalExaminateView.getTxtPatientCode().setText("");
		medicalExaminateView.getTxtPatientName().setText("");
		medicalExaminateView.getTxtExamDate().setText("");

		medicalExaminateView.getTxtEmail().setText("");
		medicalExaminateView.getTxtGender().setText("");
		medicalExaminateView.getTxtPhone().setText("");
		medicalExaminateView.getTxtAddress().setText("");
		medicalExaminateView.getTxtCitizenId().setText("");
		medicalExaminateView.getTxtDateOfBirth().setText("");

		medicalExaminateView.getTxtSymptoms().setText("");
		medicalExaminateView.getTxtDiagnosis().setText("");
		medicalExaminateView.getTxtTreatmentPlan().setText("");

		medicalExaminateView.getTxtPrescriptionId().setText("");
		medicalExaminateView.getTxtMedicalResultId().setText("");
	}

	private void saveMedicalResult() {
	    try {
	        String examQueueIdText = medicalExaminateView.getTxtExamQueueId().getText();
	        String doctorCode = medicalExaminateView.getTxtDoctorCode().getText();
	        String patientCode = medicalExaminateView.getTxtPatientCode().getText();
	        String symptoms = medicalExaminateView.getTxtSymptoms().getText();
	        String diagnosis = medicalExaminateView.getTxtDiagnosis().getText();
	        String treatmentPlan = medicalExaminateView.getTxtTreatmentPlan().getText();

	        StringBuilder errors = new StringBuilder();

	        // --- Kiểm tra rỗng ---
	        if (examQueueIdText == null || examQueueIdText.trim().isEmpty()) {
	            errors.append("- Exam Queue ID cannot be empty.\n");
	        }
	        if (doctorCode == null || doctorCode.trim().isEmpty()) {
	            errors.append("- Doctor code cannot be empty.\n");
	        }
	        if (patientCode == null || patientCode.trim().isEmpty()) {
	            errors.append("- Patient code cannot be empty.\n");
	        }
	        if (symptoms == null || symptoms.trim().isEmpty()) {
	            errors.append("- Symptoms cannot be empty.\n");
	        }
	        if (diagnosis == null || diagnosis.trim().isEmpty()) {
	            errors.append("- Diagnosis cannot be empty.\n");
	        }
	        if (treatmentPlan == null || treatmentPlan.trim().isEmpty()) {
	            errors.append("- Treatment plan cannot be empty.\n");
	        }

	        // --- Kiểm tra định dạng ID ---
	        long examQueueId = -1;
	        if (examQueueIdText != null && !examQueueIdText.trim().isEmpty()) {
	            try {
	                examQueueId = Long.parseLong(examQueueIdText.trim());
	                if (examQueueId <= 0) {
	                    errors.append("- Exam Queue ID must be a positive number.\n");
	                }
	            } catch (NumberFormatException nfe) {
	                errors.append("- Exam Queue ID must be a valid number.\n");
	            }
	        }

	        // --- Nếu có lỗi, show và return ---
	        if (errors.length() > 0) {
	            medicalExaminateView.showError("Please fix the following errors:\n\n" + errors.toString());
	            return;
	        }
	        
	        if (prescriptionDetails != null && !prescriptionDetails.isEmpty()) {

	            // 1. Gom tổng số lượng cần dùng theo từng mã thuốc
	            Map<String, Integer> requiredQuantities = new HashMap<>();

	            for (PrescriptionDetail detail : prescriptionDetails) {
	                if (detail == null) continue;

	                String medCode = detail.getMedicineCode();
	                if (medCode == null || medCode.trim().isEmpty()) continue;

	                int quantity = (detail.getQuantity() == null) ? 0 : detail.getQuantity();

	                // Nếu đã có thuốc này thì cộng thêm, chưa có thì thêm mới
	                if (requiredQuantities.containsKey(medCode)) {
	                    int current = requiredQuantities.get(medCode);
	                    requiredQuantities.put(medCode, current + quantity);
	                } else {
	                    requiredQuantities.put(medCode, quantity);
	                }
	            }

	            // 2. Kiểm tra tồn kho của từng thuốc
	            StringBuilder stockErrors = new StringBuilder();

	            for (String medCode : requiredQuantities.keySet()) {
	                int needed = requiredQuantities.get(medCode);

	                Medicine medicine = medicineService.getByMedicineCode(medCode);

	                if (medicine == null) {
	                    stockErrors.append("- Medicine not found: ").append(medCode).append("\n");
	                } else {
	                    int available = medicine.getQuantity();
	                    if (available < needed) {
	                        stockErrors.append("- Not enough stock for ")
	                                   .append(medCode)
	                                   .append(" (required: ").append(needed)
	                                   .append(", available: ").append(available)
	                                   .append(")\n");
	                    }
	                }
	            }

	            if (stockErrors.length() > 0) {
	                medicalExaminateView.showError(
	                    "Cannot save medical result because of stock issues:\n" + stockErrors.toString()
	                );
	                return;
	            }
	        }


	        // --- Tạo MedicalResult ---
	        MedicalResult result = new MedicalResult();
	        Long newId = medicalResultService.generateNewMedicalResultId(); //Bỏ
	        if (newId == null) {
	            medicalExaminateView.showError("Unable to generate new MedicalResult ID.");
	            return;
	        }
	        result.setId(newId);

	        result.setExamQueueId(examQueueId);
	        result.setDoctorCode(doctorCode.trim());
	        result.setPatientCode(patientCode.trim());
	        result.setExaminationDate(LocalDate.now());
	        result.setSymptoms(symptoms.trim());
	        result.setDiagnosis(diagnosis.trim());
	        result.setTreatmentPlan(treatmentPlan.trim());

	        // --- Lưu và cập nhật trạng thái ---
	        medicalResultService.saveMedicalResult(result);
	        examQueueService.updateExamQueueStatus(result.getExamQueueId(), "done");

	        // --- Update UI ---
	        medicalExaminateView.getTxtMedicalResultId().setText(String.valueOf(result.getId()));
	        medicalExaminateView.showInfo("Medical result saved successfully.");

	        if (!(prescriptionDetails == null || prescriptionDetails.isEmpty())) {
	        		Prescription prescription = createPrescription(result.getId());
	        		addPrescriptionDetail(prescription.getId());
	        		prescriptionService.updateTotalPriceForPrescription(prescription);
	        }
	        
	        loadAllExamQueues();
	        clearExaminationForm();
	        if (prescriptionDetails != null) {
	            prescriptionDetails.clear();
	        }
	        refreshPrescriptionDetailTable();
	        medicalExaminateView.getAddPrescriptionButton().setEnabled(false);
			medicalExaminateView.getEditPrescriptionButton().setEnabled(false);
			medicalExaminateView.getDeletePrescriptionButton().setEnabled(false);
			medicalExaminateView.getAddAppointmentButton().setEnabled(false);
			medicalExaminateView.getBtnDone().setEnabled(false);
			medicalExaminateView.getBtnViewPatientProfile().setEnabled(false);

	    } catch (Exception ex) {
	        ex.printStackTrace();
	        medicalExaminateView.showError("Error saving medical result: " + ex.getMessage());
	    }
	}
	
	private Prescription createPrescription(Long medicalResultId) {
		Long prescriptionId = prescriptionService.generateNewPrescriptionId(); //Bỏ gen id mới
        String patientCode = medicalExaminateView.getTxtPatientCode().getText();
		BigDecimal totalPrive = prescriptionService.calculateTotalPriceForPrescription(prescriptionId); //Tính toán từ list PrescriptionDetail
		
		Prescription prescription = new Prescription();
		prescription.setId(prescriptionId);
		prescription.setMedicalResultId(medicalResultId);
		prescription.setPatientCode(patientCode);
		prescription.setPrescriptionDate(LocalDate.now());
		prescription.setTotalPrice(totalPrive);
		
		prescriptionService.savePrescription(prescription);
		return prescription;
	}
	
	private void addPrescriptionDetail(Long prescriptionId) {
	    try {
	        if (prescriptionDetails == null || prescriptionDetails.isEmpty()) {
	            return;
	        }

	        for (PrescriptionDetail detail : prescriptionDetails) {
	            detail.setPrescriptionId(prescriptionId);

	            boolean stockUpdated = medicineService.reduceStock(detail.getMedicineCode(), detail.getQuantity());
	            if (!stockUpdated) {
	                medicalExaminateView.showError(
	                    "Not enough stock for medicine: " + detail.getMedicineCode() 
	                    + ". Prescription detail was not saved."
	                );
	                return;
	            }
	            prescriptionDetailService.saveDetail(detail);
	        }

	        medicalExaminateView.showInfo("All prescription details have been saved successfully.");
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        medicalExaminateView.showError("Error while saving prescription details: " + ex.getMessage());
	    }
	}

	private void editPrescriptionDetail() {
	    int selectedRow = medicalExaminateView.getPrescriptionDetailTable().getSelectedRow();
	    if (!isRowSelected(selectedRow)) {
	        return;
	    }

	    int modelRow = selectedRow; // vì bảng không có sort/filter thì row = modelRow
	    if (modelRow < 0 || modelRow >= prescriptionDetails.size()) {
	        medicalExaminateView.showWarning("Invalid selection.");
	        return;
	    }

	    PrescriptionDetail detail = prescriptionDetails.get(modelRow);

	    JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(medicalExaminateView);
	    prescriptionDetailFormView = new PrescriptionDetailFormView(owner, detail, this);
	    initPrescriptionDetailFormActions();
	    prescriptionDetailFormView.showForm();
	}
	
	private void deletePrescriptionDetail() {
	    int selectedRow = medicalExaminateView.getPrescriptionDetailTable().getSelectedRow();
	    if (!isRowSelected(selectedRow)) {
	        return;
	    }

	    int modelRow = selectedRow;
	    if (modelRow < 0 || modelRow >= prescriptionDetails.size()) {
	        medicalExaminateView.showWarning("Invalid selection.");
	        return;
	    }

	    PrescriptionDetail detail = prescriptionDetails.get(modelRow);

	    boolean confirmed = medicalExaminateView.confirmDeletion(detail);
	    if (!confirmed) return;

	    prescriptionDetails.remove(modelRow);
	    refreshPrescriptionDetailTable();
	    medicalExaminateView.showInfo("Prescription detail deleted successfully.");
	}

	private void openPrescriptionDetailForm() {
	    JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(medicalExaminateView);
	    prescriptionDetailFormView = new PrescriptionDetailFormView(owner, null, this);
	    initPrescriptionDetailFormActions();
	    applyPrescriptionRealtimeValidation();
	    prescriptionDetailFormView.showForm();
	}
	
	private void initPrescriptionDetailFormActions() {
		prescriptionDetailFormView.getBtnSelectMedicine().addActionListener(e -> selectMedicine());
		prescriptionDetailFormView.getCancelButton().addActionListener(e -> prescriptionDetailFormView.dispose());
		prescriptionDetailFormView.getSaveButton().addActionListener(e -> savePrescriptionDetail());
	}
	
	
	private void savePrescriptionDetail() {
	    PrescriptionDetail detail = collectPrescriptionDetail();

	    StringBuilder errors = new StringBuilder();

	    // --- VALIDATION cơ bản ---
	    if (detail.getMedicineCode() == null || detail.getMedicineCode().isEmpty()) {
	        errors.append("- Please select a medicine.\n");
	    }
	    if (detail.getQuantity() == null || detail.getQuantity() <= 0) {
	        errors.append("- Quantity must be a positive number.\n");
	    }
	    if (detail.getUnitPrice() == null || detail.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
	        errors.append("- Unit price must be greater than 0.\n");
	    }
	    if (detail.getDosage() == null || detail.getDosage().trim().isEmpty()) {
	        errors.append("- Dosage must not be empty.\n");
	    }
	    if (detail.getUsageInstructions() == null || detail.getUsageInstructions().trim().isEmpty()) {
	        errors.append("- Usage instructions must not be empty.\n");
	    }

	    if (errors.length() > 0) {
	        prescriptionDetailFormView.showWarning("Please fix the following errors:\n\n" + errors);
	        return;
	    }

	    // --- KIỂM TRA TỒN KHO ---
	    Medicine medicine = medicineService.getByMedicineCode(detail.getMedicineCode());
	    if (medicine == null) {
	        prescriptionDetailFormView.showWarning("Medicine not found: " + detail.getMedicineCode());
	        return;
	    }

	    // Tính tổng quantity đã có sẵn trong prescriptionDetails cho thuốc này
	    int totalAlreadyRequested = 0;
	    for (PrescriptionDetail prescriptionDetail : prescriptionDetails) {
	        if (prescriptionDetail != null && prescriptionDetail.getMedicineCode().equals(detail.getMedicineCode())) {
	            if (prescriptionDetail == prescriptionDetailFormView.getPrescriptionDetail()) continue;
	            totalAlreadyRequested += (prescriptionDetail.getQuantity() != null) ? prescriptionDetail.getQuantity() : 0;
	        }
	    }

	    int totalNeeded = totalAlreadyRequested + detail.getQuantity();
	    if (totalNeeded > medicine.getQuantity()) {
	        prescriptionDetailFormView.showWarning(
	            "Not enough stock for medicine: " + detail.getMedicineCode()
	            + " (requested: " + totalNeeded
	            + ", available: " + medicine.getQuantity() + ")"
	        );
	        return;
	    }

	    // --- THÊM HOẶC CẬP NHẬT ---
	    if (prescriptionDetailFormView.getPrescriptionDetail() == null) {
	        // Add mới
	        prescriptionDetails.add(detail);
	    } else {
	        // Update
	        int idx = prescriptionDetails.indexOf(prescriptionDetailFormView.getPrescriptionDetail());
	        if (idx >= 0) {
	            prescriptionDetails.set(idx, detail);
	        }
	    }

	    prescriptionDetailFormView.setPrescriptionDetail(detail);
	    prescriptionDetailFormView.markSaved(true);
	    prescriptionDetailFormView.dispose();

	    refreshPrescriptionDetailTable();
	}

	
	private void refreshPrescriptionDetailTable() {
	    JPanel tablePanel = medicalExaminateView.buildPrescriptionDetailTablePanel(prescriptionDetails, 1);
	    medicalExaminateView.setPrescriptionDetailTablePanel(tablePanel);
	}


	private PrescriptionDetail collectPrescriptionDetail() {
	    PrescriptionDetail base = prescriptionDetailFormView.getPrescriptionDetail();
	    PrescriptionDetail detail = (base != null) ? base : new PrescriptionDetail();

	    detail.setMedicineCode(prescriptionDetailFormView.getMedicineCodeInput());
	    detail.setMedicineName(prescriptionDetailFormView.getMedicineNameInput());
	    detail.setDosage(prescriptionDetailFormView.getDosageInput());

	    try {
	        detail.setQuantity(Integer.parseInt(prescriptionDetailFormView.getQuantityInput()));
	    } catch (NumberFormatException e) {
	        detail.setQuantity(0);
	    }

	    try {
	        String unitText = prescriptionDetailFormView.getUnitPriceInput();
	        if (!unitText.isEmpty()) {
	            detail.setUnitPrice(new java.math.BigDecimal(unitText));
	        } else {
	            detail.setUnitPrice(java.math.BigDecimal.ZERO);
	        }
	    } catch (NumberFormatException e) {
	        detail.setUnitPrice(java.math.BigDecimal.ZERO);
	    }

	    // TotalPrice = Quantity * UnitPrice
	    if (detail.getUnitPrice() != null && detail.getQuantity() != null) {
	        detail.setTotalPrice(detail.getUnitPrice().multiply(
	                new java.math.BigDecimal(detail.getQuantity())));
	    } else {
	        detail.setTotalPrice(java.math.BigDecimal.ZERO);
	    }

	    detail.setUsageInstructions(prescriptionDetailFormView.getUsageInstructionsInput());

	    return detail;
	}

	private void selectMedicine() {
	    JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(medicalExaminateView);
	    MedicineSelectionView selectionView = new MedicineSelectionView(owner, true);
	    MedicineSelectionController controller = new MedicineSelectionController(selectionView);
	    selectionView.setVisible(true);

	    String selectedCode = controller.getSelectedMedicineCode();
	    MedicineService service = new MedicineService();
	    prescriptionDetailFormView.fillMedicineData(service.getByMedicineCode(selectedCode));	   
	}

	private void openAddAppointmentForm() {
		
	}
	
	private boolean isRowSelected(int rowIndex) {
		if (rowIndex == -1) {
			medicalExaminateView.showWarning("Please select a row.");
			return false;
		}
		return true;
	}

	private void loadAllExamQueues() {
	    String role = Session.getCurrentUser().getRole().toLowerCase();
	    if ("doctor".equals(role)) {
	        String currentDoctorCode = Session.getCurrentUser().getCode();
	        allExamQueues = examQueueService.getExamQueuesByDoctorCode(currentDoctorCode);
	    } else {
	        allExamQueues = examQueueService.getAllWaitingExamQueues();
	    }
	    medicalExaminateView.renderTable(allExamQueues);
	}

	private void viewSelectedPatientProfile() {
		int selectedRow = medicalExaminateView.getExamQueueTable().getSelectedRow();
		if (selectedRow == -1) {
			medicalExaminateView.showWarning("Please select a patient from the exam queue.");
			return;
		}
		int modelRow = medicalExaminateView.getExamQueueTable().convertRowIndexToModel(selectedRow);
		if (modelRow < 0 || modelRow >= allExamQueues.size()) {
			medicalExaminateView.showWarning("Invalid selection.");
			return;
		}

		ExamQueue selectedQueue = allExamQueues.get(modelRow);
		String patientCode = selectedQueue.getPatientCode();

		Patient patient = patientService.getByPatientCode(patientCode);
		if (patient == null) {
			medicalExaminateView.showError("Patient not found.");
			return;
		}

		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(medicalExaminateView);
		PatientProfileView profileView = new PatientProfileView(parentFrame, patient);
		profileView.setVisible(true);
	}
	
	// Hàm gọi sau khi khởi tạo PrescriptionDetailFormView
	private void applyPrescriptionRealtimeValidation() {
	    FormUtilities.validateOnFocusLost(
	        prescriptionDetailFormView.getTxtQuantity(),
	        text -> text.matches("\\d+") && Integer.parseInt(text) > 0,
	        "Quantity must be a positive number."
	    );
	}

}
