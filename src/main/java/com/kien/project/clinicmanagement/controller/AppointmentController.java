package com.kien.project.clinicmanagement.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.kien.project.clinicmanagement.model.Appointment;
import com.kien.project.clinicmanagement.model.ExamQueue;
import com.kien.project.clinicmanagement.model.Patient;
import com.kien.project.clinicmanagement.model.User;
import com.kien.project.clinicmanagement.service.AppointmentService;
import com.kien.project.clinicmanagement.service.ExamQueueService;
import com.kien.project.clinicmanagement.service.PatientService;
import com.kien.project.clinicmanagement.service.UserService;
import com.kien.project.clinicmanagement.utils.PageHelper;
import com.kien.project.clinicmanagement.utils.Session;
import com.kien.project.clinicmanagement.view.appointment.AppointmentManagementView;
import com.kien.project.clinicmanagement.view.patient.PatientProfileView;
import com.kien.project.clinicmanagement.view.patient.PatientSelectionView;
import com.kien.project.clinicmanagement.view.user.DoctorSelectionView;

public class AppointmentController {
	private final AppointmentManagementView appointmentManagementView;
	private final AppointmentService appointmentService = new AppointmentService();
	private final PatientService patientService = new PatientService();
	private final UserService userService = new UserService();
	private final ExamQueueService examQueueService = new ExamQueueService();

	private List<Appointment> currentPageAppointments;
	private int totalAppointments;
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

	private Appointment editingAppointment = null;

	public AppointmentController(AppointmentManagementView appointmentManagementView) {
		this.appointmentManagementView = appointmentManagementView;
		User currentUser = Session.getCurrentUser();
		this.currentUserRole = currentUser.getRole();
		this.currentUserCode = currentUser.getCode();
		loadAllAppointments();
		initHeaderActions();
		initCRUDActions();
		initAppointmentFormActions();
		initPagingActions();
	}

	// Gắn sự kiện cho header
	public void initHeaderActions() {
		// ----- Gắn sự kiện nút phần header ------
		appointmentManagementView.getSearchButton().addActionListener(e -> searchAppointments());
		appointmentManagementView.getRefreshButton().addActionListener(e -> resetState());
		// ----- Gắn sự kiện cho bàn phím ------
		appointmentManagementView.getSearchField()
				.addActionListener(e -> appointmentManagementView.getSearchButton().doClick());
	}

	private void resetState() {
		appointmentManagementView.getSearchField().setText("");
		isSearching = false;
		currentSearchKeyword = "";
		currentSearchField = "";
		totalSearchResults = 0;
		currentPage = 1;
		updateTable();
	}

	public void loadAllAppointments() {
	    resetState();
	    totalAppointments = getTotalCount();
	    totalPages = Math.max(1, (int) Math.ceil((double) totalAppointments / rowsPerPage));
	    currentPage = 1;
	    updateTable();
	}

	public void updateTable() {
	    int offset = PageHelper.getOffSet(currentPage, rowsPerPage);

	    int totalAppointments = getTotalCount();
	    List<Appointment> pageAppointments = fetchAppointments(offset, rowsPerPage);

	    totalPages = Math.max(1, (int) Math.ceil((double) totalAppointments / rowsPerPage));
	    if (currentPage > totalPages) {
	        currentPage = totalPages;
	        offset = PageHelper.getOffSet(currentPage, rowsPerPage);
	        pageAppointments = fetchAppointments(offset, rowsPerPage);
	    }

	    currentPageAppointments = (pageAppointments != null) ? pageAppointments : List.of();

	    appointmentManagementView.renderAppointmentTable(currentPageAppointments, offset);
	    appointmentManagementView.updatePageInfo(currentPage, totalPages);
	}

	private List<Appointment> fetchAppointments(int offset, int limit) {
	    if (isSearching) {
	        if ("ADMIN".equalsIgnoreCase(currentUserRole)) {
	            return appointmentService.searchAppointments(currentSearchKeyword, currentSearchField, offset, limit);
	        }
	        //  Doctor & Receptionist gộp chung 1 hàm
	        return appointmentService.searchAppointmentsByUser(currentUserCode, currentUserRole, currentSearchKeyword, currentSearchField, offset, limit);
	    }

	    if ("ADMIN".equalsIgnoreCase(currentUserRole)) {
	        return appointmentService.getAppointments(offset, limit);
	    }
	    //  Doctor & Receptionist gộp chung 1 hàm
	    return appointmentService.getAppointmentsByUser(currentUserCode, currentUserRole, offset, limit);
	}

	private int getTotalCount() {
	    if (isSearching)
	        return totalSearchResults;

	    if ("ADMIN".equalsIgnoreCase(currentUserRole)) {
	        return appointmentService.countAppointments();
	    }
	    //  Doctor & Receptionist gộp chung
	    return appointmentService.countAppointmentsByUser(currentUserCode, currentUserRole);
	}

	
	private void searchAppointments() {
	    currentSearchKeyword = appointmentManagementView.getSearchField().getText().trim();
	    currentSearchField = (String) appointmentManagementView.getSearchTypeCombo().getSelectedItem();

	    if (currentSearchKeyword.isEmpty()) {
	        resetState();
	        loadAllAppointments();
	        return;
	    }

	    isSearching = true;

	    if ("ADMIN".equalsIgnoreCase(currentUserRole)) {
	        totalSearchResults = appointmentService.countSearchAppointments(currentSearchKeyword, currentSearchField);
	    } else {
	        // Doctor & Receptionist gộp chung 1 hàm
	        totalSearchResults = appointmentService.countSearchAppointmentsByUser(currentUserCode, currentUserRole, currentSearchKeyword, currentSearchField);
	    }

	    if (totalSearchResults <= 0) {
	        appointmentManagementView.showInfo("No appointments found!");
	        renderEmptyTable();
	        return;
	    }

	    currentPage = 1;
	    updateTable();
	}

	
	private void renderEmptyTable() {
		appointmentManagementView.renderAppointmentTable(List.of(), 0);
		appointmentManagementView.updatePageInfo(1, 1);
	}
	
	

	public void initCRUDActions() {
		appointmentManagementView.getEditButton().addActionListener(
				e -> editSelectedAppointment(appointmentManagementView.getAppointmentTable().getSelectedRow()));
		appointmentManagementView.getDeleteButton().addActionListener(
				e -> deleteSelectedAppointment(appointmentManagementView.getAppointmentTable().getSelectedRow()));
		appointmentManagementView.getCancelAppointmentButton().addActionListener(
				e -> cancelAppointment(appointmentManagementView.getAppointmentTable().getSelectedRow()));
		appointmentManagementView.getCheckInButton()
				.addActionListener(e -> checkIn(appointmentManagementView.getAppointmentTable().getSelectedRow()));
		appointmentManagementView.getViewPatientProfileButton().addActionListener(
				e -> viewPatientProfile(appointmentManagementView.getAppointmentTable().getSelectedRow()));
	}

	public void initAppointmentFormActions() {
		appointmentManagementView.getSelectPatientButton().addActionListener(e -> openSelectionPatient());
		appointmentManagementView.getSelectDoctorButton().addActionListener(e -> openSelectionDoctor());
		appointmentManagementView.getSaveInlineButton().addActionListener(e -> handleSaveInline());
		appointmentManagementView.getCancelEditButton().addActionListener(e -> clearInput());
	}

	private void openSelectionDoctor() {
		DoctorSelectionView doctorSelectionView = new DoctorSelectionView(null);
		DoctorSelectionController controller = new DoctorSelectionController(doctorSelectionView);
		doctorSelectionView.setVisible(true);

		String selectedCode = controller.getSelectedDoctorCode();
		if (selectedCode == null || selectedCode.isBlank()) {
			return;
		}

		User doctor = userService.getByUserCode(selectedCode);
		if (doctor == null || doctor.getName() == null || doctor.getName().isBlank()) {
			return;
		}

		appointmentManagementView.getDoctorCodeField().setText(selectedCode);
		appointmentManagementView.getDoctorNameField().setText(doctor.getName());
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

		appointmentManagementView.getPatientCodeField().setText(selectedCode);
		appointmentManagementView.getPatientNameField().setText(patient.getName());
	}

	private void clearInput() {
		editingAppointment = null;
		appointmentManagementView.getPatientCodeField().setText("");
		appointmentManagementView.getPatientNameField().setText("");
		appointmentManagementView.getDoctorCodeField().setText("");
		appointmentManagementView.getDoctorNameField().setText("");
		appointmentManagementView.getNoteTextArea().setText("");
		appointmentManagementView.getSaveInlineButton().setText("Save");
		appointmentManagementView.getScheduledDateChooser().setDate(null);
		appointmentManagementView.getPatientCodeField().requestFocus();
	}
	
	private void handleSaveInline() {
	    Appointment appointment = prepareAppointmentFromForm();
	    boolean isUpdate = (editingAppointment != null);

	    String validationErrors = validateAppointmentInput(appointment, isUpdate);
	    if (!validationErrors.isEmpty()) {
	        appointmentManagementView.showWarning("Please fix the following errors:\n\n" + validationErrors);
	        return;
	    }

	    String error = appointmentService.saveOrUpdateAppointment(appointment);
	    if (error == null) {
	        loadAllAppointments();
	        clearInput();
	        appointmentManagementView.showInfo(isUpdate ? "Appointment updated successfully." : "Appointment saved successfully.");
	    } else {
	        appointmentManagementView.showWarning(error);
	    }
	}

	private Appointment prepareAppointmentFromForm() {
	    String patientCode = appointmentManagementView.getPatientCodeField().getText().trim();
	    String doctorCode;
	    User currentUser = Session.getCurrentUser();

	    if ("Doctor".equalsIgnoreCase(currentUser.getRole())) {
	        doctorCode = currentUser.getCode();
	    } else {
	        doctorCode = appointmentManagementView.getDoctorCodeField().getText().trim();
	    }

	    Date scheduledDate = appointmentManagementView.getScheduledDateChooser().getDate();
	    String note = appointmentManagementView.getNoteTextArea().getText().trim();

	    Appointment appointment = (editingAppointment != null)
	        ? editingAppointment
	        : new Appointment();

	    appointment.setPatientCode(patientCode);
	    appointment.setDoctorCode(doctorCode);
	    appointment.setScheduledDate(scheduledDate);
	    appointment.setNote(note.isEmpty() ? null : note);

	    if (editingAppointment == null) {
	        appointment.setStatus("SCHEDULED");
	    }

	    return appointment;
	}

	// 🧩 Hàm mới thêm để kiểm tra dữ liệu đầu vào
	private String validateAppointmentInput(Appointment appointment, boolean isUpdate) {
	    StringBuilder errors = new StringBuilder();

	    if (appointment.getPatientCode() == null || appointment.getPatientCode().isBlank()) {
	        errors.append("- Patient code is required.\n");
	    }

	    if (appointment.getDoctorCode() == null || appointment.getDoctorCode().isBlank()) {
	        errors.append("- Doctor code is required.\n");
	    }

	    Date scheduledDate = appointment.getScheduledDate();
	    if (scheduledDate == null) {
	        errors.append("- Scheduled date is required.\n");
	    } else {
	        // ✅ So sánh chỉ theo ngày (không tính giờ)
	        LocalDate today = LocalDate.now();
	        LocalDate scheduledLocalDate = scheduledDate.toInstant()
	                .atZone(ZoneId.systemDefault())
	                .toLocalDate();

	        if (scheduledLocalDate.isBefore(today)) {
	            errors.append("- Scheduled date cannot be in the past.\n");
	        }
	    }

	    return errors.toString();
	}
	
	private boolean isRowSelected(int rowIndex) {
		if (currentPageAppointments == null || currentPageAppointments.isEmpty()) {
			appointmentManagementView.showWarning("No appointments available.");
			return false;
		}
		if (rowIndex < 0 || rowIndex >= currentPageAppointments.size()) {
			appointmentManagementView.showWarning("Please select a valid appointment.");
		}
		return true;
	}
	
	private Appointment getSelectedAppointment(int rowIndex) {
		return currentPageAppointments.get(rowIndex);
	}

	// Hàm chỉnh sửa lịch hẹn
	private void editSelectedAppointment(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		Appointment selected = getSelectedAppointment(rowIndex);

		// Đổ dữ liệu vào inline inputs để user chỉnh
		appointmentManagementView.getPatientCodeField().setText(selected.getPatientCode());
		appointmentManagementView.getPatientNameField().setText(selected.getPatientName());
		appointmentManagementView.getDoctorCodeField().setText(selected.getDoctorCode());
		appointmentManagementView.getDoctorNameField().setText(selected.getDoctorName());
		appointmentManagementView.getNoteTextArea().setText(selected.getNote());
		appointmentManagementView.getScheduledDateChooser().setDate(selected.getScheduledDate());
		appointmentManagementView.getSaveInlineButton().setText("Update");

		// Đặt chế độ edit
		editingAppointment = selected;
	}

	private void deleteSelectedAppointment(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		Appointment selected = getSelectedAppointment(rowIndex);
		
		if (!appointmentManagementView.confirmDeletion(selected))
			return;

		boolean success = appointmentService.deleteAppointment(selected.getId());
		if (success) {
			loadAllAppointments();
			appointmentManagementView.showInfo("Appointment deleted successfully.");
		} else {
			appointmentManagementView.showError("Failed to delete appointment.");
		}
	}

	private void checkIn(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		Appointment selected = getSelectedAppointment(rowIndex);

		// Không cho check-in nếu đã hủy
		if ("CANCELLED".equalsIgnoreCase(selected.getStatus())) {
			appointmentManagementView.showWarning("Cannot check-in a cancelled appointment.");
			return;
		}
		// Không cho check-in nếu đã check-in rồi
		if ("CHECKED-IN".equalsIgnoreCase(selected.getStatus())) {
			appointmentManagementView.showWarning("This appointment has already been checked in.");
			return;
		}

		boolean valid = appointmentService.updateAppointmentStatus(selected.getId(), "CHECKED-IN");
		ExamQueue newQueue = new ExamQueue();
		String patientCode = selected.getPatientCode();
		String doctorCode = selected.getDoctorCode();
		newQueue.setPatientCode(patientCode);
		newQueue.setDoctorCode(doctorCode);
		newQueue.setQueueNumber(examQueueService.getNextQueueNumberForDoctor(doctorCode));
		newQueue.setStatus("WAITING");
		String error = examQueueService.saveOrUpdateExamQueue(newQueue);
		if (error == null) {
			appointmentManagementView.showInfo("Queue saved successfully.");
		} else {
			appointmentManagementView.showWarning(error);
		}
		if (valid) {
			loadAllAppointments();
			appointmentManagementView.showInfo("Appointment checked in successfully.");
		} else {
			appointmentManagementView.showWarning("Failed to check-in appointment.");
		}
	}

	private void cancelAppointment(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		Appointment appt = getSelectedAppointment(rowIndex);

		// Không cho hủy nếu đã hủy
		if ("CANCELLED".equalsIgnoreCase(appt.getStatus())) {
			appointmentManagementView.showWarning("This appointment is already cancelled.");
			return;
		}
		// Không cho hủy nếu đã check-in
		if ("CHECKED-IN".equalsIgnoreCase(appt.getStatus())) {
			appointmentManagementView.showWarning("Cannot cancel an appointment that has been checked in.");
			return;
		}

		String reason = javax.swing.JOptionPane.showInputDialog(appointmentManagementView,
				"Please enter the reason for cancellation:", "Cancel Appointment",
				javax.swing.JOptionPane.PLAIN_MESSAGE);
		if (reason == null)
			return;
		reason = reason.trim();
		if (reason.isEmpty()) {
			appointmentManagementView.showWarning("Cancellation reason cannot be empty.");
			return;
		}

		boolean ok = appointmentService.cancelAppointment(appt.getId(), reason);
		if (ok) {
			loadAllAppointments();
			appointmentManagementView.showInfo("Appointment cancelled successfully.");
		} else {
			appointmentManagementView.showWarning("Failed to cancel appointment.");
		}
	}

	private void viewPatientProfile(int selectedRow) {
	    if (!isRowSelected(selectedRow))
	        return;

	    Appointment selectedAppointment = getSelectedAppointment(selectedRow);
	    if (selectedAppointment == null) {
	        appointmentManagementView.showError("Invalid appointment selection.");
	        return;
	    }

	    String patientCode = selectedAppointment.getPatientCode();
	    if (patientCode == null || patientCode.isBlank()) {
	        appointmentManagementView.showError("No patient code found for this appointment.");
	        return;
	    }

	    Patient patient = patientService.getByPatientCode(patientCode);
	    if (patient == null) {
	        appointmentManagementView.showError("Patient not found!");
	        return;
	    }

	    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(appointmentManagementView);
	    PatientProfileView profileView = new PatientProfileView(parentFrame, patient);
	    profileView.setVisible(true);
	}
	

	private void initPagingActions() {
		appointmentManagementView.getPrevPageButton().addActionListener(e -> previousPage());
		appointmentManagementView.getNextPageButton().addActionListener(e -> nextPage());
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
