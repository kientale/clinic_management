package com.kien.project.clinicmanagement.view.appointment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.kien.project.clinicmanagement.controller.AppointmentController;
import com.kien.project.clinicmanagement.model.Appointment;
import com.kien.project.clinicmanagement.model.User;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.Session;
import com.kien.project.clinicmanagement.utils.ShowMessage;
import com.kien.project.clinicmanagement.utils.StyleConstants;
import com.toedter.calendar.JDateChooser;

public class AppointmentManagementView extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTable appointmentTable;
	private DefaultTableModel tableModel;
	private JTextField searchField;
	private JComboBox<String> searchTypeCombo;
	private JLabel pageInfoLabel;
	private JTextField txtPatientCode, txtPatientName;
	private JTextField txtDoctorCode, txtDoctorName;
	private JDateChooser scheduledDateChooser;
	private JTextArea txtNote;
	private JButton btnSaveInline, btnSelectPatient, btnSelectDoctor, btnCancelEdit;

	private JButton btnSearch, btnRefresh;

	private JButton btnEdit, btnDelete, btnCancelAppointment, btnCheckIn, btnViewPatientProfile;

	private JButton btnPrevPage, btnNextPage;

	@SuppressWarnings("unused")
	private final AppointmentController apponintmentController;

	// ----------------- Constructor -----------------
	public AppointmentManagementView() {
		setLayout(new BorderLayout(10, 10));
		setBackground(StyleConstants.PANEL_BG);
		initComponents();

		apponintmentController = new AppointmentController(this);
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

		JLabel title = new JLabel("Appointment Management", JLabel.CENTER);
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
		String[] columnNames = new String[] { "No", "Patient Name", "Doctor Name", "Scheduled Date", "Note", "Status",
				"Created By", "Created At" };
		tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Chỉ đọc
			}
		};
		appointmentTable = FormUtilities.createStyledTable(tableModel);
		panel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);
		return panel;
	}

	private JPanel buildInputPanel() {
		JPanel border = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		border.setBackground(StyleConstants.COLOR_BLUE_50);
		border.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Add and Edit Appointment"));
		panel.setBackground(StyleConstants.COLOR_WHITE);

		// ====== Dòng chọn bệnh nhân & bác sĩ ======
		JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		topRow.setOpaque(false);

		txtPatientName = new JTextField(12);
		txtPatientName.setEditable(false);
		txtDoctorName = new JTextField(12);
		txtDoctorName.setEditable(false);
		txtPatientCode = new JTextField(3);
		txtPatientCode.setEditable(false);
		txtDoctorCode = new JTextField(3);
		txtDoctorCode.setEditable(false);

		scheduledDateChooser = new JDateChooser();
		scheduledDateChooser.setDateFormatString("yyyy-MM-dd");
		scheduledDateChooser.setPreferredSize(new Dimension(180, 28)); // rộng hơn

		btnSelectPatient = FormUtilities.styleButton(createButton("Select", "/images/for_button/save.png"),
				new Color(102, 187, 106), Color.WHITE);
		btnSelectDoctor = FormUtilities.styleButton(createButton("Select", "/images/for_button/save.png"),
				new Color(102, 187, 106), Color.WHITE);
		btnSaveInline = FormUtilities.styleButton(createButton("Save", "/images/for_button/save.png"),
				new Color(66, 165, 245), Color.WHITE);
		btnCancelEdit = FormUtilities.styleButton(createButton("Cancel", "/images/for_button/cancel.png"),
				new Color(66, 165, 245), Color.WHITE);

		topRow.add(new JLabel("Patient *:"));
		topRow.add(txtPatientCode);
		topRow.add(txtPatientName);
		topRow.add(btnSelectPatient);

		User currentUser = Session.getCurrentUser();
		if (!("Doctor".equalsIgnoreCase(currentUser.getRole()))) {
			topRow.add(new JLabel("Doctor *:"));
			topRow.add(txtDoctorCode);
			topRow.add(txtDoctorName);
			topRow.add(btnSelectDoctor);
		}

		topRow.add(new JLabel("Scheduled Date *:"));
		topRow.add(scheduledDateChooser);

		topRow.add(btnSaveInline);
		topRow.add(btnCancelEdit);

		// ====== Dòng nhập Note ======
		JPanel notePanel = new JPanel(new BorderLayout(5, 5));
		notePanel.setOpaque(false);
		notePanel.setBorder(new EmptyBorder(10, 15, 10, 20));

		JLabel lblNote = new JLabel("Note:");
		lblNote.setBorder(new EmptyBorder(0, 0, 5, 0));
		notePanel.add(lblNote, BorderLayout.NORTH);

		txtNote = new JTextArea(3, 45);
		txtNote.setLineWrap(true);
		txtNote.setWrapStyleWord(true);

		JScrollPane scrollNote = new JScrollPane(txtNote);
		scrollNote.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollNote.setPreferredSize(new Dimension(400, 70));

		notePanel.add(scrollNote, BorderLayout.CENTER);

		// ====== Gắn vào panel chính ======
		panel.add(topRow, BorderLayout.NORTH);
		panel.add(notePanel, BorderLayout.CENTER);

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
		btnCancelAppointment = FormUtilities.styleButton(
				createButton("Cancel Appointment", "/images/for_button/cancel_appointment.jpg"),
				StyleConstants.BUTTON_BG, Color.BLACK);
		btnCheckIn = FormUtilities.styleButton(createButton("Check in", "/images/for_button/check_in.png"),
				StyleConstants.BUTTON_BG, Color.BLACK);
		btnViewPatientProfile = FormUtilities.styleButton(
				createButton("View Patient Profile", "/images/for_button/patient_info.png"), StyleConstants.BUTTON_BG,
				Color.BLACK);

		crudPanel.add(new JLabel("Appointment Management:"));
		crudPanel.add(btnEdit);
		crudPanel.add(btnDelete);

		User user = Session.getCurrentUser();
		if (!("Doctor".equalsIgnoreCase(user.getRole()))) {
			crudPanel.add(btnCancelAppointment);
			crudPanel.add(btnCheckIn);
		}
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

	private JPanel buildBottomPanel() {
		JPanel bottom = new JPanel(new BorderLayout());
		bottom.add(buildInputPanel(), BorderLayout.NORTH);
		bottom.add(buildFooterPanel(), BorderLayout.SOUTH);
		return bottom;
	}

	private JButton createButton(String text, String iconPath) {
		return FormUtilities.createIconButton(text, iconPath, 18);
	}

	public void renderAppointmentTable(List<Appointment> appointments, int startIndex) {
	    tableModel.setRowCount(0);
	    var sdfDate = new java.text.SimpleDateFormat("yyyy-MM-dd"); // chỉ ngày tháng năm

	    for (int i = 0; i < appointments.size(); i++) {
	        Appointment a = appointments.get(i);
	        String scheduledStr = a.getScheduledDate() != null ? sdfDate.format(a.getScheduledDate()) : "";
	        String createdAtStr = a.getCreatedAt() != null ? sdfDate.format(a.getCreatedAt()) : "";

	        tableModel.addRow(new Object[] {
	            startIndex + i + 1,
	            a.getPatientName(),
	            a.getDoctorName(),
	            scheduledStr,
	            a.getNote(),
	            a.getStatus(),
	            a.getCreatedByName(),
	            createdAtStr
	        });
	    }
	}

	public void updatePageInfo(int currentPage, int totalPages) {
		pageInfoLabel.setText("Page " + currentPage + "/" + totalPages);
	}

	// ----------------- Helpers -----------------
	public boolean confirmDeletion(Appointment appointment) {
		int choice = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to delete appointment for patient " + appointment.getPatientCode() + "?",
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

	public JButton getSelectDoctorButton() {
		return btnSelectDoctor;
	}

	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	public JTextField getPatientCodeField() {
		return txtPatientCode;
	}
	
	public JTextField getPatientNameField() {
		return txtPatientName;
	}
	
	public JTextField getDoctorCodeField() {
		return txtDoctorCode;
	}
	
	public JTextField getDoctorNameField() {
		return txtDoctorName;
	}

	public JDateChooser getScheduledDateChooser() {
		return scheduledDateChooser;
	}

	public JTable getAppointmentTable() {
		return appointmentTable;
	}

	public JTextField getSearchField() {
		return searchField;
	}

	public JComboBox<String> getSearchTypeCombo() {
		return searchTypeCombo;
	}

	public JButton getSearchButton() {
		return btnSearch;
	}

	public JButton getSaveInlineButton() {
		return btnSaveInline;
	}

	public JButton getCancelEditButton() {
		return btnCancelEdit;
	}

	public JButton getRefreshButton() {
		return btnRefresh;
	}

	public JButton getEditButton() {
		return btnEdit;
	}

	public JButton getDeleteButton() {
		return btnDelete;
	}

	public JButton getCheckInButton() {
		return btnCheckIn;
	}

	public JButton getViewPatientProfileButton() {
		return btnViewPatientProfile;
	}

	public JButton getPrevPageButton() {
		return btnPrevPage;
	}

	public JButton getNextPageButton() {
		return btnNextPage;
	}

	public JButton getCancelAppointmentButton() {
		return btnCancelAppointment;
	}

	public JTextArea getNoteTextArea() {
		return txtNote;
	}
}
