package com.kien.project.clinicmanagement.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.kien.project.clinicmanagement.model.Patient;
import com.kien.project.clinicmanagement.service.PatientService;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.view.patient.PatientFormView;
import com.kien.project.clinicmanagement.view.patient.PatientSelectionView;

public class PatientSelectionController {
	private final PatientSelectionView patientSelectionView;
	private final PatientService patientService = new PatientService();
	private PatientFormView patientFormView;

	private String selectedPatientCode;

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

	public PatientSelectionController(PatientSelectionView patientSelectionView) {
		this.patientSelectionView = patientSelectionView;
		loadAllPatients();
		initHeaderActions();
		initCRUDActions();
		initPagingActions();
	}

	// Gắn sự kiện cho header
	public void initHeaderActions() {
		// ----- Gắn sự kiện nút phần header ------
		patientSelectionView.getSearchButton().addActionListener(e -> handleSearch());
		patientSelectionView.getRefreshButton().addActionListener(e -> resetState());
		// ----- Gắn sự kiện cho bàn phím ------
		patientSelectionView.getSearchField().addActionListener(e -> patientSelectionView.getSearchButton().doClick());
	}
	
	private void resetState() {
		patientSelectionView.getSearchField().setText("");
		isSearching = false;
		currentSearchKeyword = "";
		currentSearchField = "";
		totalSearchResults = 0;
		currentPage = 1;
		updateTable();
	}
	
	private void loadAllPatients() {
		resetState();
		totalPatients = getTotalCount();
		totalPages = Math.max(1, (int) Math.ceil((double) totalPatients / rowsPerPage));
		currentPage = 1;
		updateTable();
	}
	
	private void updateTable() {
		int offset = (currentPage - 1) * rowsPerPage;
		
		int totalPatients = getTotalCount();
		totalPages = Math.max(1, (int) Math.ceil((double) totalPatients / rowsPerPage));
		
		if (currentPage > totalPages) {
	        currentPage = totalPages;
	        offset = (currentPage - 1) * rowsPerPage;
	    }

	    List<Patient> pagePatients = fetchPatients(offset, rowsPerPage);
	    currentPagePatients = (pagePatients != null) ? pagePatients : List.of();


	    patientSelectionView.renderPatientTable(currentPagePatients, offset);
	    patientSelectionView.updatePageInfo(currentPage, totalPages);

	    if (currentPagePatients.isEmpty()) {
	    		patientSelectionView.showInfo("No patients found!");
	    }
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
	
	private void handleSearch() {
		currentSearchKeyword = patientSelectionView.getSearchField().getText().trim();
		currentSearchField = (String) patientSelectionView.getSearchCombo().getSelectedItem();

		if (currentSearchKeyword.isEmpty()) {
			resetState();
			loadAllPatients();
			return;
		}
		
		isSearching = true;
		totalSearchResults = patientService.countSearchPatients(currentSearchKeyword, currentSearchField);
		
		if (totalSearchResults <= 0) {
			patientSelectionView.showInfo("No patients found!");
			renderEmptyTable();
			return;
		}
		
		currentPage = 1;
		updateTable();
	}
	
	private void renderEmptyTable() {
		patientSelectionView.renderPatientTable(List.of(), 0);
		patientSelectionView.updatePageInfo(1, 1);
	}
	

	// Gắn sự kiện cho CRUD
	public void initCRUDActions() {
		patientSelectionView.getSelectButton()
				.addActionListener(e -> selectPatient(patientSelectionView.getPatientTable().getSelectedRow()));
		patientSelectionView.getAddNewPatientButton().addActionListener(e -> showPatientForm(null));
	}
	
	private void selectPatient(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		Patient selectedPatient = getSelectedPatient(rowIndex);
		selectedPatientCode = selectedPatient.getCode();
		patientSelectionView.dispose();
	}
	
	public void showPatientForm(Patient patient) {
		JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(patientSelectionView);
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

		patientSelectionView.showInfo("User saved successfully.");
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

	
	private boolean isRowSelected(int rowIndex) {
		if (currentPagePatients == null || currentPagePatients.isEmpty()) {
			patientSelectionView.showWarning("No patients available.");
			return false;
		}
		if (rowIndex < 0 || rowIndex >= currentPagePatients.size()) {
			patientSelectionView.showWarning("Please select a valid patient.");
			return false;
		}
		return true;
	}
	
	private Patient getSelectedPatient(int rowIndex) {
		return currentPagePatients.get(rowIndex);
	}
	
	
	// Gắn sự kiện phần phân trang
	public void initPagingActions() {
		patientSelectionView.getPrevPageButton().addActionListener(e -> previousPage());
		patientSelectionView.getNextPageButton().addActionListener(e -> nextPage());
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

	public String getSelectedPatientCode() {
		return selectedPatientCode;
	}
}
