package com.kien.project.clinicmanagement.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.kien.project.clinicmanagement.model.Medicine;
import com.kien.project.clinicmanagement.service.MedicineService;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.view.medicine.MedicineFormView;
import com.kien.project.clinicmanagement.view.medicine.MedicineManagementView;

public class MedicineController {
	private final MedicineManagementView medicineManagementView;
	private MedicineFormView medicineFormView;
	private final MedicineService medicineService = new MedicineService();

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

	public MedicineController(MedicineManagementView medicineManagementView, DefaultTableModel tableModel) {
		this.medicineManagementView = medicineManagementView;
		loadAllMedicines();
		initHeaderActions();
		initCRUDActions();
		initPagingActions();
	}

	public void initHeaderActions() {
		medicineManagementView.getSearchButton().addActionListener(e -> searchMedicines());
		medicineManagementView.getRefreshButton().addActionListener(e -> resetState());
		medicineManagementView.getSearchField()
				.addActionListener(e -> medicineManagementView.getSearchButton().doClick());
	}

	private void resetState() {
		medicineManagementView.getSearchField().setText("");
		isSearching = false;
		currentSearchKeyword = "";
		currentSearchField = "";
		totalSearchResults = 0;
		currentPage = 1;
		updateTable();
	}

	public void loadAllMedicines() {
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

		medicineManagementView.renderMedicineTable(currentPageMedicines, offset);
		medicineManagementView.updatePageInfo(currentPage, totalPages);

		if (currentPageMedicines.isEmpty()) {
			medicineManagementView.showInfo("No medicines found!");
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

	// Tìm kiếm thuốc
	private void searchMedicines() {
		currentSearchKeyword = medicineManagementView.getSearchField().getText().trim();
		currentSearchField = (String) medicineManagementView.getSearchTypeCombo().getSelectedItem();

		if (currentSearchKeyword.isEmpty()) {
			resetState();
			loadAllMedicines();
			return;
		}

		isSearching = true;
		totalSearchResults = medicineService.countSearchMedicines(currentSearchKeyword, currentSearchField);

		if (totalSearchResults <= 0) {
			medicineManagementView.showInfo("No medicines found!");
			renderEmptyTable();
			return;
		}

		currentPage = 1;
		updateTable();
	}

	private void renderEmptyTable() {
		medicineManagementView.renderMedicineTable(List.of(), 0);
		medicineManagementView.updatePageInfo(1, 1);
	}

	public void initCRUDActions() {
		medicineManagementView.getAddButton().addActionListener(e -> showMedicineForm(null));
		medicineManagementView.getEditButton().addActionListener(
				e -> editSelectedMedicine(medicineManagementView.getMedicineTable().getSelectedRow()));
		medicineManagementView.getDeleteButton().addActionListener(
				e -> deleteSelectedMedicine(medicineManagementView.getMedicineTable().getSelectedRow()));
		medicineManagementView.getAddStockButton().addActionListener(
				e -> addStockToSelectedMedicine(medicineManagementView.getMedicineTable().getSelectedRow()));
	}

	public void showMedicineForm(Medicine medicine) {
		JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(medicineManagementView);
		medicineFormView = new MedicineFormView(owner, medicine);

		initMedicineFormActions();
		applyRealtimeValidation();

		medicineFormView.showFormView();
	}

	public void initMedicineFormActions() {
		medicineFormView.getSaveButton().addActionListener(e -> onSaveButtonClicked());
		medicineFormView.getCancelButton().addActionListener(e -> medicineFormView.dispose());
	}

	private void applyRealtimeValidation() {
		FormUtilities.validateOnFocusLost(medicineFormView.getNameField(), text -> !text.trim().isEmpty(),
				"Medicine name must not be empty.");

		FormUtilities.validateOnFocusLost(medicineFormView.getUnitField(), text -> !text.trim().isEmpty(),
				"Unit must not be empty.");

		FormUtilities.validateOnFocusLost(medicineFormView.getPriceField(), text -> {
			try {
				return new BigDecimal(text).compareTo(BigDecimal.ZERO) >= 0;
			} catch (NumberFormatException e) {
				return false;
			}
		}, "Price must be a positive number.");

		FormUtilities.validateOnFocusLost(medicineFormView.getMinAgeField(), text -> {
			try {
				int minAge = Integer.parseInt(text);
				return minAge >= 0;
			} catch (NumberFormatException e) {
				return false;
			}
		}, "Min age must be a non-negative integer.");

		FormUtilities.validateOnFocusLost(medicineFormView.getMaxAgeField(), text -> {
			try {
				int maxAge = Integer.parseInt(text);
				String minAgeText = medicineFormView.getMinAgeField().getText().trim();
				int minAge = Integer.parseInt(minAgeText);
				return maxAge >= minAge && maxAge <= 150;
			} catch (NumberFormatException e) {
				return false;
			}
		}, "Max age must be ≥ Min age and ≤ 150.");

		FormUtilities.validateOnFocusLost(medicineFormView.getQuantityField(), text -> {
			try {
				int quantity = Integer.parseInt(text);
				return quantity >= 0;
			} catch (NumberFormatException e) {
				return false;
			}
		}, "Quantity must be a non-negative integer.");
	}

	// Nút lưu thuốc
	private void onSaveButtonClicked() {
		Medicine medicine = prepareMedicineFromForm();
		boolean isUpdate = (medicine.getCode() != null && medicineService.isExistingMedicine(medicine.getCode()));

		String validationErrors = validateMedicineInput(medicine, isUpdate);
		if (!validationErrors.isEmpty()) {
			medicineFormView.showWarning("Please fix the following errors:\n\n" + validationErrors);
			return;
		}
		
		
		medicineService.saveOrUpdateMedicine(medicine);
		medicineFormView.setSaved(true);
		medicineFormView.dispose();
		
		handleMedicineSave(isUpdate ? medicine : null);
		
		medicineManagementView.showInfo("Medicine saved successfully.");
	}
	
	private void handleMedicineSave(Medicine originalMedicine) {
		if (originalMedicine == null || originalMedicine.getCode() == null) {
			currentPage = 1;
		}
		updateTable();
	}

	private Medicine prepareMedicineFromForm() {
		Medicine medicine = medicineFormView.getMedicine();
		boolean isNew = (medicine == null);

		if (isNew) {
			medicine = new Medicine();
			medicine.setCode(medicineService.generateNextMedicineCode());
			medicineFormView.setMedicine(medicine);
		}

		medicineService.fillMedicineData(medicine, medicineFormView.getNameField(),
				medicineFormView.getDescriptionField(), medicineFormView.getUnitField(),
				medicineFormView.getPriceField(), medicineFormView.getMinAgeField(), medicineFormView.getMaxAgeField(),
				medicineFormView.getQuantityField());

		return medicine;
	}

	private String validateMedicineInput(Medicine medicine, boolean isUpdate) {
		StringBuilder errors = new StringBuilder();

		// Lấy dữ liệu từ view
		String name = medicineFormView.getNameField().getText().trim();
		String unit = medicineFormView.getUnitField().getText().trim();
		String priceStr = medicineFormView.getPriceField().getText().trim();
		String description = medicineFormView.getDescriptionField().getText().trim();
		String minAgeStr = medicineFormView.getMinAgeField().getText().trim();
		String maxAgeStr = medicineFormView.getMaxAgeField().getText().trim();
		String quantityStr = medicineFormView.getQuantityField().getText().trim();

		if (FormUtilities.isEmpty(name, unit, description, priceStr, minAgeStr, maxAgeStr, quantityStr)) {
			errors.append("- Please fill in all required fields.\n");
		}

		if (!name.matches("^[\\p{L}0-9 .,'-]{2,100}$")) {
			errors.append("- Medicine name must be 2–100 characters and cannot contain invalid characters.\n");
		}

		if (!unit.matches("^[\\p{L}0-9 ]{1,20}$")) {
			errors.append("- Unit must be 1–20 characters.\n");
		}

		BigDecimal price = null;
		try {
			price = new BigDecimal(priceStr);
			if (price.compareTo(BigDecimal.ZERO) < 0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException | NullPointerException e) {
			errors.append("- Price must be a positive number.\n");
		}

		int minAge = -1, maxAge = -1;
		try {
			minAge = Integer.parseInt(minAgeStr);
			maxAge = Integer.parseInt(maxAgeStr);

			if (minAge < 0 || maxAge < 0 || maxAge > 150 || maxAge < minAge) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			errors.append("- Min age must be ≥ 0, Max age ≤ 150, and Max age ≥ Min age.\n");
		}

		int quantity = -1;
		try {
			quantity = Integer.parseInt(quantityStr);
			if (quantity < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			errors.append("- Quantity must be a non-negative number.\n");
		}

		String duplicateError = checkDuplicateMedicineFields(medicine, isUpdate);
		if (duplicateError != null && !duplicateError.isEmpty()) {
			errors.append(duplicateError);
		}
		
		return errors.toString();
	}
	
	private String checkDuplicateMedicineFields(Medicine medicine, boolean isUpdate) {
		StringBuilder errors = new StringBuilder();
		String code = medicine.getCode();
		
		if (medicineService.isExistingName(medicine.getName(), isUpdate ? code : null))
			errors.append("- Medicine name is already exists!");
		
		return errors.toString();
	}
	
	private boolean isRowSelected(int rowIndex) {
		if (currentPageMedicines == null || currentPageMedicines.isEmpty()) {
			medicineManagementView.showWarning("No medicines avaiable.");
			return false;
		}
		if (rowIndex < 0 || rowIndex >= currentPageMedicines.size()) {
			medicineManagementView.showWarning("Please select a valid medicine.");
			return false;
		}
		return true;
	}
	
	private Medicine getSelectedMedicine(int rowIndex) {
		return currentPageMedicines.get(rowIndex);
	}
	
	// Hàm chỉnh sửa thuốc
	public void editSelectedMedicine(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		Medicine selectedMedicine = getSelectedMedicine(rowIndex);
		Medicine fullMedicine = medicineService.getByMedicineCode(selectedMedicine.getCode());
		
		if (fullMedicine == null) {
			medicineManagementView.showError("Medicine not found.");
			return;
		}
		
		showMedicineForm(fullMedicine);
	}

	// Hàm xóa thuốc
	public void deleteSelectedMedicine(int rowIndex) {
		if (!isRowSelected(rowIndex))
			return;

		Medicine medicine = getSelectedMedicine(rowIndex);

		if (!medicineManagementView.confirmDeletion(medicine))
			return;

		if (medicineService.deleteMedicine(medicine.getCode())) {
			adjustPaginationAfterDelete();
			updateTable();
			medicineManagementView.showInfo("Medicine deleted successfully.");
		} else {
			medicineManagementView.showError("Failed to delete medicine.");
		}
	}
	
	private void adjustPaginationAfterDelete() {
		int totalMedicines = medicineService.countMedicines();
		int totalPages = Math.max(1, (int) Math.ceil((double) totalMedicines / rowsPerPage));
		if (currentPage > totalPages)
			currentPage = totalPages;
	}

	public void addStockToSelectedMedicine(int rowIndex) {
	    if (!isRowSelected(rowIndex))
	        return;

	    Medicine medicine = getSelectedMedicine(rowIndex);

	    String input = JOptionPane.showInputDialog(
	            medicineManagementView,
	            "Enter quantity to add for \"" + medicine.getName() + "\":",
	            "Add Stock",
	            JOptionPane.PLAIN_MESSAGE
	    );

	    if (input == null || input.trim().isEmpty())
	        return;

	    try {
	        int addedQty = Integer.parseInt(input.trim());

	        if (addedQty <= 0) {
	            medicineManagementView.showWarning("Quantity must be a positive integer.");
	            return;
	        }

	        boolean success = medicineService.addStock(medicine.getCode(), addedQty, medicineManagementView);

	        if (success) {
	            medicineManagementView.showInfo("Stock added successfully.");
	            updateTable();
	        } else {
	            medicineManagementView.showError("Failed to update stock.");
	        }

	    } catch (NumberFormatException e) {
	        medicineManagementView.showError("Quantity must be a valid integer.");
	    }
	}

	public String generateNextMedicineCode() {
		return medicineService.generateNextMedicineCode();
	}

	public void initPagingActions() {
		medicineManagementView.getPrevPageButton().addActionListener(e -> previousPage());
		medicineManagementView.getNextPageButton().addActionListener(e -> nextPage());
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
