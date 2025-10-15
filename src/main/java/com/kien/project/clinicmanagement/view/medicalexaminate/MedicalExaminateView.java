package com.kien.project.clinicmanagement.view.medicalexaminate;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.kien.project.clinicmanagement.controller.MedicalExaminateController;
import com.kien.project.clinicmanagement.model.ExamQueue;
import com.kien.project.clinicmanagement.model.PrescriptionDetail;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.ShowMessage;
import com.kien.project.clinicmanagement.utils.StyleConstants;
import com.toedter.calendar.JDateChooser;

public class MedicalExaminateView extends JPanel {

	private static final long serialVersionUID = 1L;

	// Queue
	private JTable queueTable;
	private DefaultTableModel queueTableModel;

	// Main layout
	private JPanel mainPanel;
	private JPanel prescriptionDetailTableContainer;
	private JTable prescriptionDetailTable;

	// Fields
	private JTextField txtExamQueueId, txtDoctorCode, txtDoctorName;
	private JTextField txtPatientCode, txtPatientName, txtEmail, txtPhone, txtGender, txtAddress, txtCitizenId,
			txtDateOfBirth;
	private JTextField txtExamDate, txtSymptoms, txtDiagnosis, txtTreatmentPlan, txtPrescriptionId, txtMedicalResultId;
	private JDateChooser scheduledDateChooser;

	// Buttons
	private JButton btnExaminate, btnViewPatientProfile, btnDone, btnClearAllField;
	private JButton btnAddPrescriptionDetail, btnEditPrescriptionDetail, btnDeletePrescriptionDetail, btnAddAppointment;

	private List<PrescriptionDetail> prescriptionDetails = new ArrayList<>();
	@SuppressWarnings("unused")
	private final MedicalExaminateController medicalExaminateController;

	public MedicalExaminateView() {
		setLayout(new BorderLayout(10, 10));
		setBackground(StyleConstants.COLOR_WHITE);
		initComponents();
		medicalExaminateController = new MedicalExaminateController(this);
	}

	private void initComponents() {
		queueTableModel = new DefaultTableModel(getQueueTableColumns(), 0);
		queueTable = FormUtilities.createStyledTable(queueTableModel);

		add(buildHeaderPanel(), BorderLayout.NORTH);

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(buildInfoAndResultPanel(), BorderLayout.NORTH);
		mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainPanel, buildQueuePanel());
		splitPane.setResizeWeight(0.7);
		SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.7));
		splitPane.setOneTouchExpandable(true);

		add(splitPane, BorderLayout.CENTER);
	}

	private JPanel buildHeaderPanel() {
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(StyleConstants.COLOR_BLUE_50);
		headerPanel.setBorder(new EmptyBorder(10, 20, 0, 20));

		JLabel title = new JLabel("Medical Examination", JLabel.CENTER);
		title.setFont(StyleConstants.TITLE_FONT);
		title.setForeground(StyleConstants.COLOR_BLUE_800);
		title.setBorder(new EmptyBorder(10, 0, 10, 0));

		headerPanel.add(title, BorderLayout.NORTH);
		return headerPanel;
	}

	private JPanel buildQueuePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(StyleConstants.COLOR_WHITE);
		panel.setBorder(new EmptyBorder(0, 10, 0, 10));

		JLabel title = new JLabel("Queue List", JLabel.CENTER);
		title.setFont(StyleConstants.TITLE_FONT);
		title.setForeground(StyleConstants.COLOR_BLUE_800);
		title.setBorder(new EmptyBorder(10, 0, 10, 0));

		panel.add(title, BorderLayout.NORTH);
		panel.add(new JScrollPane(queueTable), BorderLayout.CENTER);

		btnExaminate = FormUtilities.styleButton(createButton("Examinate", "/images/for_button/examinate.png"),
				StyleConstants.BUTTON_BG, Color.BLACK);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		buttonPanel.add(btnExaminate);

		panel.add(buttonPanel, BorderLayout.SOUTH);
		return panel;
	}

	private JPanel buildInfoAndResultPanel() {
		JPanel container = new JPanel(new BorderLayout(0, 20));
		container.setBackground(StyleConstants.COLOR_BLUE_50);
		container.setBorder(BorderFactory.createEmptyBorder(30, 10, 10, 10));

		// ===== Top Panel =====
		JPanel topPanel = new JPanel(new GridLayout(1, 2, 30, 0));
		topPanel.setOpaque(false);

		// Left: Patient Info
		JPanel left = new JPanel(new GridLayout(6, 4, 10, 10));
		TitledBorder leftBorder = BorderFactory.createTitledBorder("Patient & Exam Queue Info");
		leftBorder.setTitleFont(new Font("Arial", Font.BOLD, 15));
		left.setBorder(BorderFactory.createCompoundBorder(leftBorder, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		txtExamQueueId = new JTextField();
		txtDoctorCode = new JTextField();
		txtDoctorName = new JTextField();
		txtPatientCode = new JTextField();
		txtPatientName = new JTextField();
		txtEmail = new JTextField();
		txtGender = new JTextField();
		txtPhone = new JTextField();
		txtAddress = new JTextField();
		txtCitizenId = new JTextField();
		txtDateOfBirth = new JTextField();
		btnViewPatientProfile = FormUtilities.styleButton(
				createButton("", "/images/for_button/patient_info.png"), StyleConstants.BUTTON_BG,
				Color.BLACK);
		btnViewPatientProfile.setEnabled(false);

		addLabelAndField(left, "Exam Queue Id:", txtExamQueueId);
		addLabelAndField(left, "Doctor Code:", txtDoctorCode);
		addLabelAndField(left, "Doctor Name:", txtDoctorName);
		addLabelAndField(left, "Patient Id:", txtPatientCode);
		addLabelAndField(left, "Full Name:", txtPatientName);
		addLabelAndField(left, "Email:", txtEmail);
		addLabelAndField(left, "Gender:", txtGender);
		addLabelAndField(left, "Phone:", txtPhone);
		addLabelAndField(left, "Address:", txtAddress);
		addLabelAndField(left, "Citizen Id:", txtCitizenId);
		addLabelAndField(left, "Date of Birth:", txtDateOfBirth);
		addLabelAndField(left, "View Patient Profile:", btnViewPatientProfile);


		JTextField[] readonlyFields = { txtExamQueueId, txtDoctorCode, txtDoctorName, txtPatientCode, txtPatientName,
				txtEmail, txtGender, txtPhone, txtAddress, txtCitizenId, txtDateOfBirth };
		for (JTextField field : readonlyFields)
			field.setEditable(false);

		// Right: Examination Result
		JPanel right = new JPanel(new GridLayout(4, 2, 10, 10));
		TitledBorder rightBorder = BorderFactory.createTitledBorder("Examination Result");
		rightBorder.setTitleFont(new Font("Arial", Font.BOLD, 15));
		right.setBorder(
				BorderFactory.createCompoundBorder(rightBorder, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		txtExamDate = new JTextField();
		txtSymptoms = new JTextField();
		txtDiagnosis = new JTextField();
		txtTreatmentPlan = new JTextField();

		addLabelAndField(right, "Examination Date:", txtExamDate);
		addLabelAndField(right, "Symptoms:", txtSymptoms);
		addLabelAndField(right, "Diagnosis:", txtDiagnosis);
		addLabelAndField(right, "Treatment Plan:", txtTreatmentPlan);

		topPanel.add(left);
		topPanel.add(right);

		// ===== Prescription Table Container =====
		prescriptionDetailTableContainer = new JPanel(new BorderLayout());
		prescriptionDetailTableContainer.add(buildPrescriptionDetailTablePanel(prescriptionDetails, 1),
				BorderLayout.CENTER);

		// Content
		JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
		contentPanel.setOpaque(false);
		contentPanel.add(topPanel, BorderLayout.NORTH);
		contentPanel.add(prescriptionDetailTableContainer, BorderLayout.CENTER);

		container.add(contentPanel, BorderLayout.CENTER);
		return container;
	}

	public JPanel buildPrescriptionDetailTablePanel(List<PrescriptionDetail> prescriptionDetails, int startIndex) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		panel.setBackground(StyleConstants.COLOR_WHITE);

		JLabel title = new JLabel("Prescription Detail", JLabel.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 15));
		title.setForeground(StyleConstants.COLOR_BLUE_800);
		title.setBorder(new EmptyBorder(10, 0, 5, 10));

		String[] columnNames = { "No", "Medicine Id", "Medicine Name", "Dosage", "Quantity", "Unit Price",
				"Total Price", "Usage Instruction" };

		DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // không cho edit trực tiếp trên bảng
			}
		};

		// Gán model cho prescriptionDetailTable
		prescriptionDetailTable = FormUtilities.createStyledTable(model);
		prescriptionDetailTable.getTableHeader().setReorderingAllowed(false);
		prescriptionDetailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Load data vào bảng
		int index = startIndex;
		for (PrescriptionDetail d : prescriptionDetails) {
			model.addRow(new Object[] { index++, d.getMedicineCode(), d.getMedicineName(), d.getDosage(),
					d.getQuantity(), d.getUnitPrice(), d.getTotalPrice(), d.getUsageInstructions() });
		}

		JScrollPane scrollPane = new JScrollPane(prescriptionDetailTable);
		scrollPane.setPreferredSize(new Dimension(0, 250));

		panel.add(title, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}
	private JPanel createFooterPanel() {
		JPanel footerPanel = new JPanel(new BorderLayout());
		footerPanel.setBackground(StyleConstants.COLOR_BLUE_50);
		footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

		btnDone = FormUtilities.styleButton(createButton("Complete Examination", "/images/for_button/check_in.png"),
				new Color(102, 187, 106), Color.WHITE);
		btnClearAllField = FormUtilities.styleButton(createButton("Clear All Fields", "/images/for_button/delete.png"),
				new Color(239, 83, 80), Color.WHITE);

		btnAddPrescriptionDetail = FormUtilities.styleButton(createButton("Add", "/images/for_button/add.png"),
				StyleConstants.BUTTON_BG, Color.BLACK);
		btnEditPrescriptionDetail = FormUtilities.styleButton(createButton("Edit", "/images/for_button/Edit.png"),
				new Color(255, 213, 79), Color.BLACK);
		btnDeletePrescriptionDetail = FormUtilities.styleButton(createButton("Delete", "/images/for_button/delete.png"),
				new Color(239, 83, 80), Color.WHITE);

		btnAddAppointment = FormUtilities.styleButton(
				createButton("Add Appointment", "/images/for_button/appointment_management.png"),
				new Color(255, 213, 79), Color.BLACK);

		txtPrescriptionId = new JTextField(10);
		txtPrescriptionId.setEditable(false);
		txtPrescriptionId.setBackground(new Color(240, 240, 240));

		txtMedicalResultId = new JTextField(10);
		txtMedicalResultId.setEditable(false);
		txtMedicalResultId.setBackground(new Color(240, 240, 240));

		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		leftPanel.setBackground(StyleConstants.COLOR_BLUE_50);
		leftPanel.add(new JLabel("Prescription Detail:"));
		leftPanel.add(btnAddPrescriptionDetail);
		btnAddPrescriptionDetail.setEnabled(false);
		
		leftPanel.add(btnEditPrescriptionDetail);
		btnEditPrescriptionDetail.setEnabled(false);
		
		leftPanel.add(btnDeletePrescriptionDetail);
		btnDeletePrescriptionDetail.setEnabled(false);
		
		leftPanel.add(new JLabel("Appointment:"));
		leftPanel.add(btnAddAppointment);
		btnAddAppointment.setEnabled(false);

		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
		rightPanel.setBackground(StyleConstants.COLOR_BLUE_50);
		
		rightPanel.add(btnDone);
		btnDone.setEnabled(false);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(StyleConstants.COLOR_BLUE_50);
		mainPanel.add(leftPanel, BorderLayout.WEST);
		mainPanel.add(rightPanel, BorderLayout.EAST);

		footerPanel.add(mainPanel, BorderLayout.NORTH);
		return footerPanel;
	}

	private void addLabelAndField(JPanel parent, String label, JComponent field) {
	    JLabel lbl = new JLabel(label);
	    lbl.setFont(new Font("Arial", Font.PLAIN, 14));
	    parent.add(lbl);
	    parent.add(field);
	}

	private String[] getQueueTableColumns() {
		return new String[] { "Queue Number", "Patient Name" };
	}

	// ===== Public Methods =====
	public void renderTable(List<ExamQueue> queues) {
		queueTableModel.setRowCount(0);
		for (ExamQueue eq : queues) {
			queueTableModel.addRow(new Object[] { eq.getQueueNumber(), eq.getPatientName() });
		}
	}

	public void setPrescriptionDetailTablePanel(JPanel panel) {
		prescriptionDetailTableContainer.removeAll();
		prescriptionDetailTableContainer.add(panel, BorderLayout.CENTER);
		prescriptionDetailTableContainer.revalidate();
		prescriptionDetailTableContainer.repaint();
	}

	private JButton createButton(String text, String iconPath) {
		return FormUtilities.createIconButton(text, iconPath, 18);
	}

	public boolean confirmDeletion(PrescriptionDetail detail) {
		int choice = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to delete this prescription detail for medicine " + detail.getMedicineName()
						+ "?",
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

	// Getters
	public JTable getExamQueueTable() {
		return queueTable;
	}

	public JButton getBtnExaminate() {
		return btnExaminate;
	}

	public JButton getBtnDone() {
		return btnDone;
	}

	public JButton getBtnViewPatientProfile() {
		return btnViewPatientProfile;
	}

	public JButton getBtnClearAllField() {
		return btnClearAllField;
	}

	public JButton getAddPrescriptionButton() {
		return btnAddPrescriptionDetail;
	}

	public JButton getEditPrescriptionButton() {
		return btnEditPrescriptionDetail;
	}

	public JButton getDeletePrescriptionButton() {
		return btnDeletePrescriptionDetail;
	}

	public JButton getAddAppointmentButton() {
		return btnAddAppointment;
	}

	public JTextField getTxtExamQueueId() {
		return txtExamQueueId;
	}

	public JTextField getTxtDoctorCode() {
		return txtDoctorCode;
	}

	public JTextField getTxtDoctorName() {
		return txtDoctorName;
	}

	public JTextField getTxtPatientCode() {
		return txtPatientCode;
	}

	public JTextField getTxtPatientName() {
		return txtPatientName;
	}

	public JTextField getTxtEmail() {
		return txtEmail;
	}

	public JTextField getTxtGender() {
		return txtGender;
	}

	public JTextField getTxtPhone() {
		return txtPhone;
	}

	public JTextField getTxtAddress() {
		return txtAddress;
	}

	public JTextField getTxtCitizenId() {
		return txtCitizenId;
	}

	public JTextField getTxtDateOfBirth() {
		return txtDateOfBirth;
	}

	public JTextField getTxtExamDate() {
		return txtExamDate;
	}

	public JTextField getTxtSymptoms() {
		return txtSymptoms;
	}

	public JTextField getTxtDiagnosis() {
		return txtDiagnosis;
	}

	public JTextField getTxtTreatmentPlan() {
		return txtTreatmentPlan;
	}

	public JTextField getTxtPrescriptionId() {
		return txtPrescriptionId;
	}

	public JTextField getTxtMedicalResultId() {
		return txtMedicalResultId;
	}

	public JDateChooser getScheduledDateChooser() {
		return scheduledDateChooser;
	}

	public JTable getPrescriptionDetailTable() {
		return prescriptionDetailTable;
	}
}
