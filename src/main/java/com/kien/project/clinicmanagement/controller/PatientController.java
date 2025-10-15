package com.kien.project.clinicmanagement.controller;

import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.kien.project.clinicmanagement.model.MedicalResult;
import com.kien.project.clinicmanagement.model.Patient;
import com.kien.project.clinicmanagement.model.PrescriptionDetail;
import com.kien.project.clinicmanagement.service.MedicalResultService;
import com.kien.project.clinicmanagement.service.PatientService;
import com.kien.project.clinicmanagement.service.PrescriptionDetailService;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.view.patient.PatientFormView;
import com.kien.project.clinicmanagement.view.patient.PatientManagementView;
import com.kien.project.clinicmanagement.view.patient.PatientProfileView;
import com.kien.project.clinicmanagement.view.prescription.PrescriptionDetailView;

public class PatientController {

	private final PatientManagementView patientManagementView;
	private PatientFormView patientFormView;
	private PatientProfileView patientProfileView;
	private final PatientService patientService = new PatientService();
	private final MedicalResultService service = new MedicalResultService();
	private final PrescriptionDetailService prescriptionDetailService = new PrescriptionDetailService();

	private List<Patient> currentPagePatients;
	private int totalPatients;
	private int totalPages;
	private int currentPage = 1;
	private final int rowsPerPage = 10;
	// Tìm kiếm
	private boolean isSearching = false;
	private String currentSearchKeyword = "";
	private String currentSearchField = "";
	private int totalSearchResults = 0;

	public PatientController(PatientManagementView patientManagementView) {
		this.patientManagementView = patientManagementView;
		loadAllPatients();
		initHeaderActions();
		initCRUDActions();
		initPagingActions();
	}

	private void initHeaderActions() {
		// Gắn sự kiện chức năng phần Header
		patientManagementView.getSearchButton().addActionListener(e -> searchUsers());
		patientManagementView.getRefreshButton().addActionListener(e -> resetState());
		// Gắn sự kiện có phím tắt
		patientManagementView.getSearchField()
				.addActionListener(e -> patientManagementView.getSearchButton().doClick());
	}

	public void loadAllPatients() {
		resetState();
		totalPatients = getTotalCount();
		totalPages = Math.max(1, (int) Math.ceil((double) totalPatients / rowsPerPage));
		currentPage = 1;
		updateTable();
	}

	private void resetState() {
		patientManagementView.getSearchField().setText("");
		isSearching = false;
		currentSearchKeyword = "";
		currentSearchField = "";
		totalSearchResults = 0;
		currentPage = 1;
		updateTable();
	}

	private void updateTable() {
		int offset = (currentPage - 1) * rowsPerPage;

		int totalPatients = getTotalCount();
		List<Patient> pagePatients = fetchPatients(offset, rowsPerPage);

		totalPages = Math.max(1, (int) Math.ceil((double) totalPatients / rowsPerPage));
		if (currentPage > totalPages) {
			currentPage = totalPages;
			offset = (currentPage - 1) * rowsPerPage;
			pagePatients = fetchPatients(offset, rowsPerPage);
		}

		// Đảm bảo không null
		currentPagePatients = (pagePatients != null) ? pagePatients : List.of();

		patientManagementView.renderPatientTable(currentPagePatients, offset);
		patientManagementView.updatePageInfo(currentPage, totalPages);
	}

	private List<Patient> fetchPatients(int offset, int limit) {
		if (isSearching)
			return patientService.searchPatients(currentSearchKeyword, currentSearchField, offset, limit);
		return patientService.getPatients(offset, limit);
	}

	private int getTotalCount() {
		if (isSearching)
			return totalSearchResults;
		return patientService.countPatients();
	}

	// Hàm chức năng cho phần Header
	private void searchUsers() {
		currentSearchKeyword = patientManagementView.getSearchField().getText().trim();
		currentSearchField = (String) patientManagementView.getSearchTypeCombo().getSelectedItem();

		if (currentSearchKeyword.isEmpty()) {
			resetState();
			loadAllPatients();
			return;
		}

		isSearching = true;
		totalSearchResults = patientService.countSearchPatients(currentSearchKeyword, currentSearchField);

		if (totalSearchResults <= 0) {
			patientManagementView.showInfo("No patients found!");
			renderEmptyTable();
			return;
		}

		currentPage = 1;
		updateTable();
	}

	private void renderEmptyTable() {
		patientManagementView.renderPatientTable(List.of(), 0);
		patientManagementView.updatePageInfo(1, 1);
	}

	private void initCRUDActions() {
		// Gắn sự kiện chức năng phần Footer
		patientManagementView.getAddPatientButton().addActionListener(e -> showPatientForm(null));
		patientManagementView.getEditPatientButton()
				.addActionListener(e -> editSelectedPatient(patientManagementView.getPatientTable().getSelectedRow()));
		patientManagementView.getDeletePatientButton().addActionListener(
				e -> deleteSelectedPatient(patientManagementView.getPatientTable().getSelectedRow()));
		patientManagementView.getPatientProfileButton()
				.addActionListener(e -> showPatientProfile(patientManagementView.getPatientTable().getSelectedRow()));
		patientManagementView.getScanPatientButton().addActionListener(e -> scanPatient());
	}

	// Hàm chức năng cho phần Footer
	public void showPatientForm(Patient patient) {
		JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(patientManagementView);
		patientFormView = new PatientFormView(owner, patient);

		initPatientFormActions();
		applyRealtimeValidation();

		patientFormView.showPatientForm();
	}

	public void initPatientFormActions() {
		patientFormView.getSaveButton().addActionListener(e -> onSaveButtonClicked());
		patientFormView.getCancelButton().addActionListener(e -> patientFormView.dispose());
	}

	private void applyRealtimeValidation() {
		FormUtilities.validateOnFocusLost(patientFormView.getNameField(),
				text -> !text.isEmpty() && text.matches("^[\\p{L} .'-]+$"),
				"Full name must not be empty or contain digits.");

		FormUtilities.validateOnFocusLost(patientFormView.getEmailField(),
				text -> text.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$"), "Invalid email format.");

		FormUtilities.validateOnFocusLost(patientFormView.getPhoneNumberField(), text -> text.matches("0\\d{9}"),
				"Phone number must start with 0 and be 10 digits.");

		FormUtilities.validateOnFocusLost(patientFormView.getCitizenIdField(), text -> text.matches("\\d{9}|\\d{12}"),
				"Citizen ID must be 9 or 12 digits.");
	}

	// Nút lưu
	private void onSaveButtonClicked() {
		Patient patient = prepareUserFromForm();
		boolean isUpdate = (patient.getCode() != null && patientService.isExistingPatient(patient.getCode()));

		String validationErrors = validatePatientInput(patient, isUpdate);
		if (!validationErrors.isEmpty()) {
			patientFormView.showWarning("Please fix the following errors:\n\n" + validationErrors);
			return;
		}

		patientService.saveOrUpdatePatient(patient);
		patientFormView.setSaved(true);
		patientFormView.dispose();

		handleUserSave(isUpdate ? patient : null);

		patientManagementView.showInfo("User saved successfully.");
	}

	private void handleUserSave(Patient originalPatient) {
		if (originalPatient == null || originalPatient.getCode() == null) {
			currentPage = 1;
		}
		updateTable();
	}

	private Patient prepareUserFromForm() {
		Patient patient = patientFormView.getPatient();
		boolean isNew = (patient == null);

		if (isNew) {
			patient = new Patient();
			patient.setCode(patientService.generateNextPatientCode());
			patient.setProfileImage("/images/for_avatar/avt_default.png");
			patientFormView.setPatient(patient);
		}

		patientService.fillPatientData(patient, patientFormView.getNameField(), patientFormView.getGenderComboBox(),
				patientFormView.getDobChooser(), patientFormView.getEmailField(), patientFormView.getPhoneNumberField(),
				patientFormView.getAddressField(), patientFormView.getCitizenIdField());

		return patient;
	}

	private String validatePatientInput(Patient patient, boolean isUpdate) {
		StringBuilder errors = new StringBuilder();

		String name = patientFormView.getNameField().getText().trim();
		String email = patientFormView.getEmailField().getText().trim();
		String phone = patientFormView.getPhoneNumberField().getText().trim();
		String address = patientFormView.getAddressField().getText().trim();
		String citizenId = patientFormView.getCitizenIdField().getText().trim();
		Date dobDate = patientFormView.getDobChooser().getDate();

		if (FormUtilities.isEmpty(name, email, phone, address, citizenId) || dobDate == null) {
			errors.append("- Please fill in all fields and select a date of birth.\n");
		}
		if (!name.matches("^[\\p{L} .'-]+$")) {
			errors.append("- Full name must not be empty or contain digits.\n");
		}
		if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
			errors.append("- Invalid email format.\n");
		}
		if (!phone.matches("0\\d{9}")) {
			errors.append("- Phone number must start with 0 and be 10 digits.\n");
		}
		if (!citizenId.matches("\\d{9}|\\d{12}")) {
			errors.append("- Citizen ID must be 9 or 12 digits.\n");
		}
		if (dobDate != null) {
			LocalDate dob = dobDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			if (dob.isAfter(LocalDate.now())) {
				errors.append("- Date of birth cannot be in the future.\n");
			}
		}

		// --- Validate logic nghiệp vụ (gọi sang DAO để kiểm tra trùng) ---
		String duplicateError = checkDuplicateUserFields(patient, isUpdate);
		if (duplicateError != null && !duplicateError.isEmpty()) {
			errors.append(duplicateError);
		}

		return errors.toString();
	}

	private String checkDuplicateUserFields(Patient patient, boolean isUpdate) {
		StringBuilder errors = new StringBuilder();
		String code = patient.getCode();

		if (patientService.isEmailTaken(patient.getEmail(), isUpdate ? code : null))
			errors.append("- Email is already registered.\n");
		if (patientService.isPhoneNumberTaken(patient.getPhoneNumber(), isUpdate ? code : null))
			errors.append("- Phone number is already registered.\n");
		if (patientService.isCitizenIdTaken(patient.getCitizenId(), isUpdate ? code : null))
			errors.append("- Citizen ID is already registered.\n");

		return errors.toString();
	}

	// Code làm việc với bệnh nhân trong bảng
	private boolean isRowSelected(int rowIndex) {
		if (currentPagePatients == null || currentPagePatients.isEmpty()) {
			patientManagementView.showWarning("No patients available.");
			return false;
		}
		if (rowIndex < 0 || rowIndex >= currentPagePatients.size()) {
			patientManagementView.showWarning("Please select a valid user.");
			return false;
		}
		return true;
	}

	private Patient getSelectedPatient(int rowIndex) {
		return currentPagePatients.get(rowIndex);
	}

	
	// Chỉnh sửa bệnh nhân
	public void editSelectedPatient(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		Patient selectedPatient = getSelectedPatient(rowIndex);
		Patient fullPatient = patientService.getByPatientCode(selectedPatient.getCode());
		
		if (fullPatient ==  null) {
			patientManagementView.showError("Patient not found.");
		}
		
		showPatientForm(fullPatient);
	}

	// Xóa người dùng
	public void deleteSelectedPatient(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		Patient patient = getSelectedPatient(rowIndex);
		
		if (!patientManagementView.confirmDeletePatient(patient))
			return;

		if (patientService.deletePatient(patient.getCode())) {
			adjustPaginationAfterDelete();
			updateTable();
			patientManagementView.showInfo("Patient deleted successfully.");
		} else {
			patientManagementView.showError("Failed to delete patient.");
		}
	}
	
	private void adjustPaginationAfterDelete() {
		int totalUsers = patientService.countPatients();
		int totalPages = Math.max(1, (int) Math.ceil((double) totalUsers / rowsPerPage));
		if (currentPage > totalPages)
			currentPage = totalPages;
	}

	private void scanPatient() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Scan patient's QR code");
		fileChooser.setFileFilter(
				new javax.swing.filechooser.FileNameExtensionFilter("Image files", "png", "jpg", "jpeg"));

		if (fileChooser.showOpenDialog(patientManagementView) == JFileChooser.APPROVE_OPTION) {
			try {
				File file = fileChooser.getSelectedFile();
				BufferedImage bufferedImage = ImageIO.read(file);
				LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
				BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

				Map<DecodeHintType, Object> hints = new HashMap<>();
				hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

				Result result = new MultiFormatReader().decode(bitmap, hints);
				String qrContent = result.getText();

				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.registerModule(new JavaTimeModule());
				objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

				Patient patient = objectMapper.readValue(qrContent, Patient.class);
				patient.setCode(patientService.generateNextPatientCode());
				patient.setProfileImage("/images/for_avatar/avt_patient.png");

				// 3. Gọi form hiển thị
				showPatientForm(patient);

			} catch (Exception ex) {
				ex.printStackTrace();
				patientManagementView.showError("Không thể đọc mã QR: " + ex.getMessage());
			}
		}
	}
	
	private void showPatientProfile(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		Patient selectedPatient = getSelectedPatient(rowIndex);
		Patient patient = patientService.getByPatientCode(selectedPatient.getCode());
		
		if (patient == null) {
			patientManagementView.showError("Patient not found.");
			return;
		}

		patientProfileView = new PatientProfileView((JFrame) SwingUtilities.getWindowAncestor(patientManagementView),
				patient);
		initPatientProfileActions();
		patientProfileView.setVisible(true);
	}

	public void initPatientProfileActions() {
		patientProfileView.getEditProfileButton()
				.addActionListener(e -> showPatientForm(patientProfileView.getPatient()));
		patientProfileView.getViewPrescriptionDetailButton().addActionListener(
				e -> viewPrescriptionDetail(patientProfileView.getPrescriptionTable().getSelectedRow()));
	}

	private void viewPrescriptionDetail(int rowIndex) {
		if (rowIndex < 0)
			return;

		JTable table = patientProfileView.getPrescriptionTable();
		Object value = table.getValueAt(rowIndex, 0);
		Long prescriptionId = Long.valueOf(value.toString());
		List<PrescriptionDetail> details = prescriptionDetailService
				.getPrescriptionDetailsByPrescriptionId(prescriptionId);
		Window owner = SwingUtilities.getWindowAncestor(patientProfileView);
		PrescriptionDetailView dialog = new PrescriptionDetailView(owner, prescriptionId);
		dialog.renderPrescriptionDetailTable(details, 0);
		dialog.setVisible(true);
	}

	public List<MedicalResult> getResultsByPatientCode(String patientCode) {
		List<MedicalResult> results = service.getResultsByPatientCode(patientCode);
		return results;
	}
	// ==============================================================================

	private void initPagingActions() {
		// Gắn sự kiện nút phần trang
		patientManagementView.getPrevPageButton().addActionListener(e -> previousPage());
		patientManagementView.getNextPageButton().addActionListener(e -> nextPage());
	}

	public void nextPage() {
		if (currentPage < totalPages) {
			currentPage++;
			updateTable();
		}
	}

	public void previousPage() {
		if (currentPage > 1) {
			currentPage--;
			updateTable();
		}
	}
}
