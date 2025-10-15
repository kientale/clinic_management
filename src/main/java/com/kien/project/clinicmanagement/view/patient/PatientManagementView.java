package com.kien.project.clinicmanagement.view.patient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.kien.project.clinicmanagement.controller.PatientController;
import com.kien.project.clinicmanagement.model.Patient;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.ShowMessage;
import com.kien.project.clinicmanagement.utils.StyleConstants;

public class PatientManagementView extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTable patientTable;
	private DefaultTableModel tableModel;
	private JTextField searchField;
	private JComboBox<String> searchTypeCombo;
	private JLabel pageInfoLabel;

	private JButton btnSearch, btnRefresh;

	private JButton btnAdd, btnEdit, btnDelete, btnViewPatientProfile, btnScanPatient;

	private JButton btnPrevPage, btnNextPage;

	@SuppressWarnings("unused")
	private final PatientController patientController;

	// ----------------- Constructor -----------------
	public PatientManagementView() {
		setLayout(new BorderLayout(10, 10));
		setBackground(StyleConstants.PANEL_BG);
		initComponents();

		patientController = new PatientController(this);
	}

	private void initComponents() {
		add(buildHeaderPanel(), BorderLayout.NORTH);
		add(buildTablePanel(), BorderLayout.CENTER);
		add(buildFooterPanel(), BorderLayout.SOUTH);
	}

	private JPanel buildHeaderPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(StyleConstants.COLOR_BLUE_50);
		panel.setBorder(new EmptyBorder(10, 20, 0, 20));

		JLabel title = new JLabel("Patient Management", JLabel.CENTER);
		title.setFont(StyleConstants.TITLE_FONT);
		title.setForeground(StyleConstants.COLOR_BLUE_800);
		title.setBorder(new EmptyBorder(10, 0, 10, 0));

		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		searchPanel.setOpaque(false);
		searchField = new JTextField(18);
		searchTypeCombo = new JComboBox<>(new String[] { "Search by name", "Search by phone number", "Search by citizen id" });
		btnSearch = FormUtilities.styleButton(createButton("Search", "/images/for_button/search.png"),new Color(66, 165, 245),Color.WHITE);
		btnRefresh = FormUtilities.styleButton(createButton("Refresh", "/images/for_button/refresh.png"),new Color(38, 166, 154),Color.WHITE);
		
		searchPanel.add(new JLabel("Search:"));
		searchPanel.add(searchField);
		searchPanel.add(searchTypeCombo);
		searchPanel.add(btnSearch);
		searchPanel.add(btnRefresh);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setOpaque(false);
		bottomPanel.add(searchPanel, BorderLayout.WEST);

		panel.add(title, BorderLayout.NORTH);
		panel.add(bottomPanel, BorderLayout.SOUTH);
		return panel;
	}

	private JPanel buildTablePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(StyleConstants.COLOR_WHITE);
		panel.setBorder(new EmptyBorder(0, 20, 10, 20));
		String[] columnNames = new String[] { "No", "Name", "Phone", "Address", "Citizen ID", "Gender" };
		tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Chỉ đọc
			}
		};
		patientTable = FormUtilities.createStyledTable(tableModel);
		panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);
		return panel;
	}

	private JPanel buildFooterPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(StyleConstants.COLOR_BLUE_50);
		panel.setBorder(new EmptyBorder(10, 20, 20, 20));

		JPanel crudPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		crudPanel.setOpaque(false);

		btnAdd = FormUtilities.styleButton(createButton("Add", "/images/for_button/add.png"), new Color(102, 187, 106),
				Color.WHITE);
		btnEdit = FormUtilities.styleButton(createButton("Edit", "/images/for_button/edit.png"),
				new Color(255, 213, 79), Color.BLACK);
		btnDelete = FormUtilities.styleButton(createButton("Delete", "/images/for_button/delete.png"),
				new Color(239, 83, 80), Color.WHITE);
		btnViewPatientProfile = FormUtilities.styleButton(createButton("View Patient Profile", "/images/for_button/user_detail.png"),StyleConstants.BUTTON_BG, Color.BLACK);
		btnScanPatient = FormUtilities.styleButton(createButton("View Patient Profile", "/images/for_button/scan_patient.png"),StyleConstants.BUTTON_BG, Color.BLACK);

		crudPanel.add(new JLabel("Patient Management:"));
		crudPanel.add(btnAdd);
		crudPanel.add(btnEdit);
		crudPanel.add(btnDelete);
		crudPanel.add(btnScanPatient);
		crudPanel.add(new JLabel("Information:"));
		crudPanel.add(btnViewPatientProfile);

		pageInfoLabel = new JLabel("Page 1/1");
		btnPrevPage = FormUtilities.styleButton(createButton("Prev", "/images/for_button/previous.png"),
				new Color(189, 189, 189), Color.BLACK);
		btnNextPage = FormUtilities.styleButton(createButton("Next", "/images/for_button/next.png"),
				new Color(189, 189, 189), Color.BLACK);
		JPanel paginationPanel = FormUtilities.createPaginationPanel(btnPrevPage, pageInfoLabel, btnNextPage);

		panel.add(crudPanel, BorderLayout.WEST);
		panel.add(paginationPanel, BorderLayout.EAST);
		return panel;
	}

	private JButton createButton(String text, String iconPath) {
		return FormUtilities.createIconButton(text, iconPath, 18);
	}

	public void renderPatientTable(List<Patient> patients, int startIndex) {
		tableModel.setRowCount(0);
		for (int i = 0; i < patients.size(); i++) {
			Patient p = patients.get(i);
			tableModel.addRow(new Object[] { startIndex + i + 1, p.getName(), p.getPhoneNumber(), p.getAddress(),
					p.getCitizenId(), p.getGender() });
		}
	}

	public void updatePageInfo(int currentPage, int totalPages) {
		pageInfoLabel.setText("Page " + currentPage + "/" + totalPages);
	}

	// ----------------- Helpers -----------------
	public boolean confirmDeletePatient(Patient patient) {
		int choice = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to delete patient " + patient.getName() + "?", "Confirm Deletion",
				JOptionPane.YES_NO_OPTION);
		return choice == JOptionPane.YES_OPTION;
	}

	public void showInfo(String message) {
		ShowMessage.showInfo(this, message);
	}

	public void showError(String message) {
		ShowMessage.showError(this, message);
	}

	public void showWarning(String message) {
		ShowMessage.showWarning(this, message);
	}

	// --------------- Getter for Controller-----------------
	public JTable getPatientTable() {
		return patientTable;
	}

	public JTextField getSearchField() {
		return searchField;
	}

	public JComboBox<String> getSearchTypeCombo() {
		return searchTypeCombo;
	}

	public JLabel getPageInfoLabel() {
		return pageInfoLabel;
	}

	public JButton getSearchButton() {
		return btnSearch;
	}

	public JButton getRefreshButton() {
		return btnRefresh;
	}

	public JButton getAddPatientButton() {
		return btnAdd;
	}

	public JButton getEditPatientButton() {
		return btnEdit;
	}

	public JButton getDeletePatientButton() {
		return btnDelete;
	}

	public JButton getPatientProfileButton() {
		return btnViewPatientProfile;
	}

	public JButton getPrevPageButton() {
		return btnPrevPage;
	}

	public JButton getNextPageButton() {
		return btnNextPage;
	}

	public JButton getScanPatientButton() {
		return btnScanPatient;
	}
}
