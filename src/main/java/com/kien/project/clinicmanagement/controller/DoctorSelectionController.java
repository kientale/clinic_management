package com.kien.project.clinicmanagement.controller;

import java.util.List;

import com.kien.project.clinicmanagement.model.User;
import com.kien.project.clinicmanagement.service.ExamQueueService;
import com.kien.project.clinicmanagement.service.UserService;
import com.kien.project.clinicmanagement.view.user.DoctorSelectionView;

public class DoctorSelectionController {

	private final DoctorSelectionView doctorSelectionView;
	private final UserService userService = new UserService();

	private List<User> currentPageDoctors;
	private int totalDoctors;
	private int totalPages;
	private int currentPage = 1;
	private final int rowsPerPage = 10;
	// Tìm kiếm
	private boolean isSearching = false;
	private String currentSearchKeyword = "";
	private String currentSearchField = "";
	private int totalSearchResults = 0;

	private String selectedDoctorCode;

	public DoctorSelectionController(DoctorSelectionView doctorSelectionView) {
		this.doctorSelectionView = doctorSelectionView;
		loadAllDoctors();
		initHeaderActions();
		initCRUDActions();
		initPagingActions();
	}

	// Gắn sự kiện cho header
	public void initHeaderActions() {
		// ----- Gắn sự kiện nút phần header ------
		doctorSelectionView.getSearchButton().addActionListener(e -> handleSearch());
		doctorSelectionView.getRefreshButton().addActionListener(e -> resetState());
		// ----- Gắn sự kiện cho bàn phím ------
		doctorSelectionView.getSearchField().addActionListener(e -> doctorSelectionView.getSearchButton().doClick());
	}
	
	private void resetState() {
		doctorSelectionView.getSearchField().setText("");
		isSearching = false;
		currentSearchKeyword = "";
		currentSearchField = "";
		totalSearchResults = 0;
		currentPage = 1;
		updateTable();
	}

	private void loadAllDoctors() {
		resetState();
		totalDoctors = getTotalCount();
		totalPages = Math.max(1, (int) Math.ceil((double) totalDoctors / rowsPerPage));
		currentPage = 1;
		updateTable();
	}
	
	private void updateTable() {
	    int offset = (currentPage - 1) * rowsPerPage;

	    int totalDoctors = getTotalCount();
	    totalPages = Math.max(1, (int) Math.ceil((double) totalDoctors / rowsPerPage));

	    if (currentPage > totalPages) {
	        currentPage = totalPages;
	        offset = (currentPage - 1) * rowsPerPage;
	    }

	    // Lấy danh sách bác sĩ cho trang hiện tại
	    List<User> pageDoctors = fetchDoctors(offset, rowsPerPage);
	    currentPageDoctors = (pageDoctors != null) ? pageDoctors : List.of();

	    // Gọi service để đếm số bệnh nhân hôm nay cho từng bác sĩ
	    ExamQueueService examQueueService = new ExamQueueService();

	    for (User doctor : currentPageDoctors) {
	        int todayLoad = examQueueService.countPatientsForDoctorToday(doctor.getCode());
	        doctor.setTodayLoad(todayLoad);
	    }

	    // Gọi hàm render để hiển thị toàn bộ danh sách
	    doctorSelectionView.renderDoctorTable(currentPageDoctors, offset);

	    // Cập nhật số trang
	    doctorSelectionView.updatePageInfo(currentPage, totalPages);

	    // Hiển thị thông báo nếu không có dữ liệu
	    if (currentPageDoctors.isEmpty()) {
	        doctorSelectionView.showInfo("No doctors found!");
	    }
	}
	
	private List<User> fetchDoctors(int offset, int limit) {
		if (isSearching)
			return userService.searchUsers(currentSearchKeyword, currentSearchField, "DOCTOR", offset, limit);
		return userService.getUsers("DOCTOR", offset, limit);
	}
	
	private int getTotalCount() {
		if (isSearching)
			return totalSearchResults;
		return userService.countUsers("DOCTOR");
	}

	private void handleSearch() {
		currentSearchKeyword = doctorSelectionView.getSearchField().getText().trim();
		currentSearchField = (String) doctorSelectionView.getSearchTypeCombo().getSelectedItem();
		
		if (currentSearchKeyword.isEmpty()) {
			resetState();
			loadAllDoctors();
			return;
		}
		
		isSearching = true;
		totalSearchResults = userService.countSearchUsers(currentSearchKeyword, currentSearchField, "DOCTOR");
		
		if (totalSearchResults <= 0) {
			doctorSelectionView.showInfo("No doctors found!");
			renderEmptyTable();
			return;
		}

		currentPage = 1;
		updateTable();
	}
	
	private void renderEmptyTable() {
		doctorSelectionView.renderDoctorTable(List.of(), 0);
		doctorSelectionView.updatePageInfo(1, 1);
	}

	
	// Gắn sự kiện cho CRUD
	public void initCRUDActions() {
		doctorSelectionView.getSelectButton()
				.addActionListener(e -> selectDoctor(doctorSelectionView.getDoctorTable().getSelectedRow()));
	}

	private void selectDoctor(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		User selectedDoctor = getSelectedDoctor(rowIndex);

		// Check today's patient load
		int todayLoad = new ExamQueueService().countPatientsForDoctorToday(selectedDoctor.getCode());
		if (todayLoad >= 5) {
			doctorSelectionView.showWarning(
					"Doctor " + selectedDoctor.getName() + " has already reached the limit of 5 patients today.");
			return;
		}

		selectedDoctorCode = selectedDoctor.getCode();
		doctorSelectionView.dispose();
	}

	// Code làm việc với người dùng trong bảng
	private boolean isRowSelected(int rowIndex) {
		if (currentPageDoctors == null || currentPageDoctors.isEmpty()) {
			doctorSelectionView.showWarning("No doctors available.");
			return false;
		}
		if (rowIndex < 0 || rowIndex >= currentPageDoctors.size()) {
			doctorSelectionView.showWarning("Please select a valid doctor.");
			return false;
		}
		return true;
	}

	private User getSelectedDoctor(int rowIndex) {
		return currentPageDoctors.get(rowIndex);
	}

	public String getSelectedDoctorCode() {
		return selectedDoctorCode;
	}

	// Gắn sự kiện phần phân trang
	public void initPagingActions() {
		doctorSelectionView.getPrevPageButton().addActionListener(e -> previousPage());
		doctorSelectionView.getNextPageButton().addActionListener(e -> nextPage());
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
