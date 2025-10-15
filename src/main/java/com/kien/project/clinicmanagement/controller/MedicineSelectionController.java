package com.kien.project.clinicmanagement.controller;

import java.util.List;

import com.kien.project.clinicmanagement.model.Medicine;
import com.kien.project.clinicmanagement.service.MedicineService;
import com.kien.project.clinicmanagement.view.medicine.MedicineSelectionView;

public class MedicineSelectionController {
	private final MedicineSelectionView medicineSelectionView;
	private final MedicineService medicineService = new MedicineService();

	private String selectedMedicineCode;

	private List<Medicine> currentPageMedicines;
	private int totalMedicines;
	private int totalPages;
	private int currentPage = 1;
	private final int rowsPerPage = 10;
	// Tìm kiếm
	private boolean isSearching = false;
	private String currentSearchKeyword = "";
	private String currentSearchField = "";
	private int totalSearchResults = 0;

	public MedicineSelectionController(MedicineSelectionView medicineSelectionView) {
		this.medicineSelectionView = medicineSelectionView;
		loadAllMedicines();
		initHeaderActions();
		initCRUDActions();
		initPagingActions();
	}

	public void initHeaderActions() {
		medicineSelectionView.getSearchButton().addActionListener(e -> searchMedicines());
		medicineSelectionView.getRefreshButton().addActionListener(e -> resetState());
		medicineSelectionView.getSearchField()
				.addActionListener(e -> medicineSelectionView.getSearchButton().doClick());

	}
	
	private void resetState() {
		medicineSelectionView.getSearchField().setText("");
		isSearching = false;
		currentSearchKeyword = "";
		currentSearchField = "";
		totalSearchResults = 0;
		currentPage = 1;
		updateTable();
	}

	private void loadAllMedicines() {
		resetState();
		totalMedicines = medicineService.countMedicines();
		totalPages = Math.max(1, (int) Math.ceil((double) totalMedicines / rowsPerPage));
		currentPage = 1;
		updateTable();
	}
	
	private void updateTable() {
		int offset = (currentPage - 1) * rowsPerPage;

		int totalMedicines = getTotalCount();
		List<Medicine> pageMedicines = fetchMedicines(offset, rowsPerPage);

		totalPages = Math.max(1, (int) Math.ceil((double) totalMedicines / rowsPerPage));
		if (currentPage > totalPages) {
			currentPage = totalPages;
			offset = (currentPage - 1) * rowsPerPage;
		}

		currentPageMedicines = (pageMedicines != null) ? pageMedicines : List.of();

		medicineSelectionView.renderMedicineTable(currentPageMedicines, offset);
		medicineSelectionView.updatePageInfo(currentPage, totalPages);

		if (currentPageMedicines.isEmpty()) {
			medicineSelectionView.showInfo("No medicines found!");
		}
	}

	private List<Medicine> fetchMedicines(int offset, int limit) {
		if (isSearching)
			return medicineService.searchMedicines(currentSearchKeyword, currentSearchField, offset, limit);
		return medicineService.getMedicines(offset, limit);
	}

	private int getTotalCount() {
		if (isSearching)
			return totalSearchResults;
		return medicineService.countMedicines();
	}

	private void searchMedicines() {
		currentSearchKeyword = medicineSelectionView.getSearchField().getText().trim();
		currentSearchField = (String) medicineSelectionView.getSearchCombo().getSelectedItem();

		if (currentSearchKeyword.isEmpty()) {
			resetState();
			loadAllMedicines();
			return;
		}

		isSearching = true;
		totalSearchResults = medicineService.countSearchMedicines(currentSearchKeyword, currentSearchField);

		if (totalSearchResults <= 0) {
			medicineSelectionView.showInfo("No medicines found!");
			renderEmptyTable();
			return;
		}

		currentPage = 1;
		updateTable();
	}

	private void renderEmptyTable() {
		medicineSelectionView.renderMedicineTable(List.of(), 0);
		medicineSelectionView.updatePageInfo(1, 1);
	}
	
	public void initCRUDActions() {
		medicineSelectionView.getSelectButton()
		.addActionListener(e -> selectMedicine(medicineSelectionView.getMedicineTable().getSelectedRow()));
	}
	
	private void selectMedicine(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		Medicine selectedMedicine = getSelectedMedicine(rowIndex);
		selectedMedicineCode = selectedMedicine.getCode();
		medicineSelectionView.dispose();
	}
	
	private boolean isRowSelected(int rowIndex) {
		if (currentPageMedicines == null || currentPageMedicines.isEmpty()) {
			medicineSelectionView.showWarning("No medicines avaiable.");
			return false;
		}
		if (rowIndex < 0 || rowIndex >= currentPageMedicines.size()) {
			medicineSelectionView.showWarning("Please select a valid medicine.");
			return false;
		}
		return true;
	}
	
	private Medicine getSelectedMedicine(int rowIndex) {
		return currentPageMedicines.get(rowIndex);
	}
	
	public String getSelectedMedicineCode() {
		return selectedMedicineCode;
	}
	
	private void initPagingActions() {
		medicineSelectionView.getPrevPageButton().addActionListener(e -> previousPage());
		medicineSelectionView.getNextPageButton().addActionListener(e -> nextPage());
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
