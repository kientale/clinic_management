package com.kien.project.clinicmanagement.controller;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.kien.project.clinicmanagement.model.ExamQueue;
import com.kien.project.clinicmanagement.model.Patient;
import com.kien.project.clinicmanagement.model.User;
import com.kien.project.clinicmanagement.service.ExamQueueService;
import com.kien.project.clinicmanagement.service.PatientService;
import com.kien.project.clinicmanagement.service.UserService;
import com.kien.project.clinicmanagement.utils.PageHelper;
import com.kien.project.clinicmanagement.utils.Session;
import com.kien.project.clinicmanagement.view.examqueue.ExamQueueManagementView;
import com.kien.project.clinicmanagement.view.patient.PatientProfileView;
import com.kien.project.clinicmanagement.view.patient.PatientSelectionView;
import com.kien.project.clinicmanagement.view.user.DoctorSelectionView;

public class ExamQueueController {
	private final ExamQueueManagementView examQueueManagementView;
	private final ExamQueueService examQueueService = new ExamQueueService();
	private final UserService userService = new UserService();
	private final PatientService patientService = new PatientService();

	private List<ExamQueue> currentPageExamQueues;
	private int totalExamQueues;
	private int totalPages;
	private int currentPage = 1;
	private final int rowsPerPage = 3;
	// Tìm kiếm
	private boolean isSearching = false;
	private String currentSearchKeyword = "";
	private String currentSearchField = "";
	private int totalSearchResults = 0;
	// Phân quyền bác sĩ
	private final String currentUserRole;
	private final String currentUserCode;

	private ExamQueue editingExamQueue = null;

	public ExamQueueController(ExamQueueManagementView examQueueManagementView) {
		this.examQueueManagementView = examQueueManagementView;
		User currentUser = Session.getCurrentUser();
		this.currentUserCode = currentUser.getCode();
		this.currentUserRole = currentUser.getRole();
		loadAllExamQueues();
		initHeaderActions();
		initCRUDActions();
		initExamQueueFormActions();
		initPagingActions();
	}

	private void initHeaderActions() {
		examQueueManagementView.getSearchButton().addActionListener(e -> searchUsers());
		examQueueManagementView.getRefreshButton().addActionListener(e -> resetState());
		examQueueManagementView.getSearchField()
				.addActionListener(e -> examQueueManagementView.getSearchButton().doClick());
	}

	private void resetState() {
		examQueueManagementView.getSearchField().setText("");
		isSearching = false;
		currentSearchKeyword = "";
		currentSearchField = "";
		totalSearchResults = 0;
		currentPage = 1;
		updateTable();
	}

	public void loadAllExamQueues() {
		resetState();
		totalExamQueues = getTotalCount();
		totalPages = Math.max(1, (int) Math.ceil((double) totalExamQueues / rowsPerPage));
		currentPage = 1;
		updateTable();
	}

	public void updateTable() {
		int offset = PageHelper.getOffSet(currentPage, rowsPerPage);

		int totalExamQueues = getTotalCount();
		List<ExamQueue> pageExamQueues = fetchExamQueues(offset, rowsPerPage);

		totalPages = Math.max(1, (int) Math.ceil((double) totalExamQueues / rowsPerPage));
		if (currentPage > totalPages) {
			currentPage = totalPages;
			offset = PageHelper.getOffSet(currentPage, rowsPerPage);
			pageExamQueues = fetchExamQueues(offset, rowsPerPage);
		}

		currentPageExamQueues = (pageExamQueues != null) ? pageExamQueues : List.of();

		examQueueManagementView.renderExamQueueTable(currentPageExamQueues, offset);
		examQueueManagementView.updatePageInfo(currentPage, totalPages);
	}

	private List<ExamQueue> fetchExamQueues(int offset, int limit) {
		if (isSearching) {
			if ("ADMIN".equalsIgnoreCase(currentUserRole)) {
				return examQueueService.searchExamQueues(currentSearchKeyword, currentSearchField, offset, limit);
			}
			return examQueueService.searchExamQueuesByUser(currentUserCode, currentUserRole, currentSearchKeyword,
					currentSearchField, offset, limit);
		}

		if ("ADMIN".equalsIgnoreCase(currentUserRole)) {
			return examQueueService.getExamQueues(offset, limit);
		}
		return examQueueService.getExamQueuesByUser(currentUserCode, currentUserRole, offset, limit);
	}

	private int getTotalCount() {
		if (isSearching)
			return totalSearchResults;
		if ("ADMIN".equalsIgnoreCase(currentUserRole)) {
			return examQueueService.countExamQueues();
		}
		return examQueueService.countExamQueuesByUser(currentUserCode, currentUserRole);
	}

	private void searchUsers() {
		currentSearchKeyword = examQueueManagementView.getSearchField().getText().trim();
		currentSearchField = (String) examQueueManagementView.getSearchTypeCombo().getSelectedItem();

		if (currentSearchKeyword.isEmpty()) {
			resetState();
			loadAllExamQueues();
			return;
		}

		isSearching = true;

		if ("ADMIN".equalsIgnoreCase(currentUserRole)) {
			totalSearchResults = examQueueService.countSearchExamQueues(currentSearchKeyword, currentSearchField);
		} else {
			totalSearchResults = examQueueService.countSearchExamQueuesByUser(currentUserCode, currentUserRole,
					currentSearchKeyword, currentSearchField);
		}

		if (totalSearchResults <= 0) {
			examQueueManagementView.showInfo("No exam queues found!");
			renderEmptyTable();
			return;
		}

		currentPage = 1;
		updateTable();
	}

	private void renderEmptyTable() {
		examQueueManagementView.renderExamQueueTable(List.of(), 0);
		examQueueManagementView.updatePageInfo(1, 1);
	}

	private void initCRUDActions() {
		examQueueManagementView.getEditButton().addActionListener(
				e -> editSelectedQueue(examQueueManagementView.getExamQueueTable().getSelectedRow()));
		examQueueManagementView.getDeleteButton().addActionListener(
				e -> deleteSelectedQueue(examQueueManagementView.getExamQueueTable().getSelectedRow()));
		examQueueManagementView.getViewPatientProfileButton().addActionListener(
				e -> viewPatientProfile(examQueueManagementView.getExamQueueTable().getSelectedRow()));
	}

	private void initExamQueueFormActions() {
		examQueueManagementView.getSelectPatientButton().addActionListener(e -> openSelectionPatient());
		examQueueManagementView.getSelectDoctorButton().addActionListener(e -> openSelectionDoctor());
		examQueueManagementView.getSaveInlineButton().addActionListener(e -> handleSaveInline());
		examQueueManagementView.getCancelEditButton().addActionListener(e -> clearInput());
	}

	private void openSelectionDoctor() {
		DoctorSelectionView doctorSelectionView = new DoctorSelectionView(null);
		DoctorSelectionController doctorSelectionController = new DoctorSelectionController(doctorSelectionView);
		doctorSelectionView.setVisible(true);

		String selectedCode = doctorSelectionController.getSelectedDoctorCode();
		if (selectedCode == null || selectedCode.isBlank()) {
			return;
		}

		User doctor = userService.getByUserCode(selectedCode);
		if (doctor == null || doctor.getName() == null || doctor.getName().isBlank()) {
			return;
		}

		examQueueManagementView.getDoctorCodeField().setText(selectedCode);
		examQueueManagementView.getDoctorNameField().setText(doctor.getName());
	}

	private void openSelectionPatient() {
		PatientSelectionView patientSelectionView = new PatientSelectionView(null, true);
		PatientSelectionController patientSelectionController = new PatientSelectionController(patientSelectionView);
		patientSelectionView.setVisible(true);

		String selectedCode = patientSelectionController.getSelectedPatientCode();
		if (selectedCode == null || selectedCode.isBlank()) {
			return;
		}

		Patient patient = patientService.getByPatientCode(selectedCode);
		if (patient == null || patient.getName() == null || patient.getName().isBlank()) {
			return;
		}

		examQueueManagementView.getPatientCodeField().setText(selectedCode);
		examQueueManagementView.getPatientNameField().setText(patient.getName());
	}

	private void clearInput() {
		editingExamQueue = null;
		examQueueManagementView.getPatientCodeField().setText("");
		examQueueManagementView.getPatientNameField().setText("");
		examQueueManagementView.getDoctorCodeField().setText("");
		examQueueManagementView.getDoctorNameField().setText("");
		examQueueManagementView.getSaveInlineButton().setText("Save");
		examQueueManagementView.getPatientCodeField().requestFocus();
	}

	private void handleSaveInline() {
		ExamQueue queue = prepareExamQueueFromForm();
		boolean isUpdate = (editingExamQueue != null);

		// ✅ Kiểm tra dữ liệu đầu vào
		String validationErrors = validateExamQueueInput(queue, isUpdate);
		if (!validationErrors.isEmpty()) {
			examQueueManagementView.showWarning("Please fix the following errors:\n\n" + validationErrors);
			return;
		}

		String error = examQueueService.saveOrUpdateExamQueue(queue);
		if (error == null) {
			loadAllExamQueues();
			clearInput();
			examQueueManagementView.showInfo(isUpdate ? "Queue updated successfully." : "Queue saved successfully.");
		} else {
			examQueueManagementView.showWarning(error);
		}
	}

	private ExamQueue prepareExamQueueFromForm() {
		String patientCode = examQueueManagementView.getPatientCodeField().getText().trim();
		String doctorCode = examQueueManagementView.getDoctorCodeField().getText().trim();

		ExamQueue queue = (editingExamQueue != null) ? editingExamQueue : new ExamQueue();

		queue.setPatientCode(patientCode);
		queue.setDoctorCode(doctorCode);

		if (editingExamQueue == null) {
			queue.setQueueNumber(examQueueService.getNextQueueNumberForDoctor(doctorCode));
			queue.setStatus("WAITING");
		} else {
			boolean doctorChanged = !doctorCode.equals(editingExamQueue.getDoctorCode());
			if (doctorChanged) {
				queue.setQueueNumber(examQueueService.getNextQueueNumberForDoctor(doctorCode));
			}
		}

		return queue;
	}

	private String validateExamQueueInput(ExamQueue queue, boolean isUpdate) {
		StringBuilder errors = new StringBuilder();

		if (queue.getPatientCode() == null || queue.getPatientCode().isBlank()) {
			errors.append("- Patient code is required.\n");
		}

		if (queue.getDoctorCode() == null || queue.getDoctorCode().isBlank()) {
			errors.append("- Doctor code is required.\n");
		}

		return errors.toString();
	}

	private boolean isRowSelected(int rowIndex) {
		if (currentPageExamQueues == null || currentPageExamQueues.isEmpty()) {
			examQueueManagementView.showWarning("No appointments available.");
			return false;
		}
		if (rowIndex < 0 || rowIndex >= currentPageExamQueues.size()) {
			examQueueManagementView.showWarning("Please select a valid appointment.");
		}
		return true;
	}

	private ExamQueue getSelectedExamQueue(int rowIndex) {
		return currentPageExamQueues.get(rowIndex);
	}

	private void editSelectedQueue(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		ExamQueue selected = getSelectedExamQueue(rowIndex);

		// Đổ dữ liệu vào inline inputs để user chỉnh
		examQueueManagementView.getPatientCodeField().setText(selected.getPatientCode());
		examQueueManagementView.getPatientNameField().setText(selected.getPatientName());
		examQueueManagementView.getDoctorCodeField().setText(selected.getDoctorCode());
		examQueueManagementView.getDoctorNameField().setText(selected.getDoctorName());
		examQueueManagementView.getSaveInlineButton().setText("Update");

		editingExamQueue = selected;
	}

	private void deleteSelectedQueue(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		ExamQueue examQueue = getSelectedExamQueue(rowIndex);

		if (!examQueueManagementView.confirmDeletion(examQueue))
			return;

		boolean success = examQueueService.deleteExamQueue(examQueue.getId());
		if (success) {
			loadAllExamQueues();
			examQueueManagementView.showInfo("Queue deleted successfully.");
		} else {
			examQueueManagementView.showError("Failed to delete queue.");
		}
	}

	private void viewPatientProfile(int selectedRow) {
		if (!isRowSelected(selectedRow))
			return;

		ExamQueue selectedExamQueue = getSelectedExamQueue(selectedRow);
		if (selectedExamQueue == null) {
			examQueueManagementView.showError("Invalid appointment selection.");
			return;
		}

		String patientCode = selectedExamQueue.getPatientCode();
		if (patientCode == null || patientCode.isBlank()) {
			examQueueManagementView.showError("No patient code found for this appointment.");
			return;
		}

		Patient patient = patientService.getByPatientCode(patientCode);
		if (patient == null) {
			examQueueManagementView.showError("Patient not found!");
			return;
		}

		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(examQueueManagementView);
		PatientProfileView profileView = new PatientProfileView(parentFrame, patient);
		profileView.setVisible(true);
	}
	
	private void initPagingActions() {
		examQueueManagementView.getPrevPageButton().addActionListener(e -> previousPage());
		examQueueManagementView.getNextPageButton().addActionListener(e -> nextPage());
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
