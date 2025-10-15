package com.kien.project.clinicmanagement.view.examqueue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
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

import com.kien.project.clinicmanagement.controller.ExamQueueController;
import com.kien.project.clinicmanagement.model.ExamQueue;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.ShowMessage;
import com.kien.project.clinicmanagement.utils.StyleConstants;

public class ExamQueueManagementView extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTable examQueueTable;
	private DefaultTableModel tableModel;
	private JTextField searchField;
	private JComboBox<String> searchTypeCombo;
	private JLabel pageInfoLabel;

	private JTextField txtPatientCode;
	private JTextField txtPatientName;
	private JTextField txtDoctorCode;
	private JTextField txtDoctorName;
	private JButton btnSaveInline, btnSelectPatient, btnSelectDoctor, btnCancelEdit;

	private JButton btnSearch, btnRefresh;

	private JButton btnEdit, btnDelete, btnViewPatientProfile;

	private JButton btnPrevPage, btnNextPage;

	private final ExamQueueController examQueueController;

	// ----------------- Constructor -----------------
	public ExamQueueManagementView() {
		setLayout(new BorderLayout(10, 10));
		setBackground(StyleConstants.COLOR_WHITE);
		initComponents();

		examQueueController = new ExamQueueController(this);
	}

	private void initComponents() {
		add(buildHeaderPanel(), BorderLayout.NORTH);
		add(buildTablePanel(), BorderLayout.CENTER);
		add(buildBottomPanel(), BorderLayout.SOUTH);
	}

	private JPanel buildHeaderPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(StyleConstants.COLOR_BLUE_50);
		panel.setBorder(new EmptyBorder(10, 20, 0, 20));

		JLabel title = new JLabel("Exam Queue Management", JLabel.CENTER);
		title.setFont(StyleConstants.TITLE_FONT);
		title.setForeground(StyleConstants.COLOR_BLUE_800);
		title.setBorder(new EmptyBorder(10, 0, 10, 0));

		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		searchPanel.setOpaque(false);
		searchField = new JTextField(18);
		searchTypeCombo = new JComboBox<>(new String[] { "Search by patient name", "Search by doctor name" });

		btnSearch = FormUtilities.styleButton(createButton("Search", "/images/for_button/search.png"),
				new Color(66, 165, 245), Color.WHITE);
		btnRefresh = FormUtilities.styleButton(createButton("Refresh", "/images/for_button/refresh.png"),
				new Color(38, 166, 154), Color.WHITE);

		searchPanel.add(new JLabel("Search:"));
		searchPanel.add(searchField);
		searchPanel.add(searchTypeCombo);
		searchPanel.add(btnSearch);
		searchPanel.add(btnRefresh);

		panel.add(title, BorderLayout.NORTH);
		panel.add(searchPanel, BorderLayout.SOUTH);
		return panel;
	}

	private JPanel buildTablePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(StyleConstants.COLOR_WHITE);
		panel.setBorder(new EmptyBorder(0, 20, 10, 20));
		String[] columnNames = new String[] { "No", "Patient Name", "Doctor Name", "Queue Number", "Status",
				"Created By", "Created At" };
		tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Chỉ đọc
			}
		};
		examQueueTable = FormUtilities.createStyledTable(tableModel);
		panel.add(new JScrollPane(examQueueTable), BorderLayout.CENTER);
		return panel;
	}

	private JPanel buildBottomPanel() {
		JPanel bottom = new JPanel(new BorderLayout());
		bottom.add(buildInputPanel(), BorderLayout.NORTH);
		bottom.add(buildFooterPanel(), BorderLayout.SOUTH);
		return bottom;
	}

	private JPanel buildInputPanel() {
		JPanel border = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		border.setBackground(StyleConstants.COLOR_BLUE_50);
		border.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		panel.setBorder(BorderFactory.createTitledBorder("Add and Edit Exam Queue"));
		panel.setBackground(StyleConstants.COLOR_WHITE);

		txtPatientName = new JTextField(12);
		txtPatientName.setEditable(false);
		txtDoctorName = new JTextField(12);
		txtDoctorName.setEditable(false);
		txtPatientCode = new JTextField(3);
		txtPatientCode.setEditable(false);
		txtDoctorCode = new JTextField(3);
		txtDoctorCode.setEditable(false);

		btnSelectPatient = FormUtilities.styleButton(createButton("Select", "/images/for_button/save.png"),
				new Color(102, 187, 106), Color.WHITE);
		btnSelectDoctor = FormUtilities.styleButton(createButton("Select", "/images/for_button/save.png"),
				new Color(102, 187, 106), Color.WHITE);
		btnSaveInline = FormUtilities.styleButton(createButton("Save", "/images/for_button/save.png"),
				new Color(66, 165, 245), Color.WHITE);
		btnCancelEdit = FormUtilities.styleButton(createButton("Cancel Edit", "/images/for_button/cancel.png"),
				new Color(66, 165, 245), Color.WHITE);

		panel.add(new JLabel("Patient *:"));
		panel.add(txtPatientCode);
		panel.add(txtPatientName);
		panel.add(btnSelectPatient);

		panel.add(new JLabel("Doctor *:"));
		panel.add(txtDoctorCode);
		panel.add(txtDoctorName);
		panel.add(btnSelectDoctor);

		panel.add(btnSaveInline);
		panel.add(btnCancelEdit);

		border.add(panel);
		return border;
	}

	private JPanel buildFooterPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(StyleConstants.COLOR_BLUE_50);
		panel.setBorder(new EmptyBorder(10, 20, 20, 20));

		JPanel crudPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		crudPanel.setOpaque(false);

		btnEdit = FormUtilities.styleButton(createButton("Edit", "/images/for_button/edit.png"),
				new Color(255, 213, 79), Color.BLACK);
		btnDelete = FormUtilities.styleButton(createButton("Delete", "/images/for_button/delete.png"),
				new Color(239, 83, 80), Color.WHITE);
		btnViewPatientProfile = FormUtilities.styleButton(createButton("View Patient Profile", "/images/for_button/patient_info.png"),StyleConstants.BUTTON_BG, Color.BLACK);

		crudPanel.add(new JLabel("Patient Management:"));
		crudPanel.add(btnEdit);
		crudPanel.add(btnDelete);
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

	public void renderExamQueueTable(List<ExamQueue> queues, int startIndex) {
		tableModel.setRowCount(0);
		var sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		
		for (int i = 0; i < queues.size(); i++) {
			ExamQueue eq = queues.get(i);
			String createdAtStr = eq.getCreatedAt() != null ? sdfDate.format(eq.getCreatedAt()) : "";
			
			tableModel.addRow(new Object[] { 
					startIndex + i + 1, 
					eq.getPatientName(), 
					eq.getDoctorName(),
					eq.getQueueNumber(), 
					eq.getStatus(), 
					eq.getCreatedByName(), 
					createdAtStr
			});
		}
	}

	public void updatePageInfo(int currentPage, int totalPages) {
		pageInfoLabel.setText("Page " + currentPage + "/" + totalPages);
	}

	// ----------------- Helpers -----------------
	public boolean confirmDeletion(ExamQueue eq) {
		int choice = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to delete exam queue for patient " + eq.getPatientName() + "?",
				"Confirm Deletion", JOptionPane.YES_NO_OPTION);
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

	// --------------- Getter for Controller--------------
	public JButton getSelectPatientButton() {
		return btnSelectPatient;
	}

	public JButton getViewPatientProfileButton() {
		return btnViewPatientProfile;
	}

	public JButton getSelectDoctorButton() {
		return btnSelectDoctor;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	public JTextField getPatientCodeField() {
		return txtPatientCode;
	}

	public JTextField getDoctorCodeField() {
		return txtDoctorCode;
	}
	
	public JTextField getPatientNameField() {
		return txtPatientName;
	}

	public JTextField getDoctorNameField() {
		return txtDoctorName;
	}

	public ExamQueueController getExamQueueController() {
		return examQueueController;
	}

	public JTable getExamQueueTable() {
		return examQueueTable;
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

	public JButton getSaveInlineButton() {
		return btnSaveInline;
	}

	public JButton getRefreshButton() {
		return btnRefresh;
	}

	public JButton getEditButton() {
		return btnEdit;
	}
	
	public JButton getCancelEditButton() {
		return btnCancelEdit;
	}

	public JButton getDeleteButton() {
		return btnDelete;
	}

	public JButton getPrevPageButton() {
		return btnPrevPage;
	}

	public JButton getNextPageButton() {
		return btnNextPage;
	}
}
