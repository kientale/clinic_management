package com.kien.project.clinicmanagement.view.patient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.kien.project.clinicmanagement.model.Patient;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.ShowMessage;
import com.kien.project.clinicmanagement.utils.StyleConstants;
import com.toedter.calendar.JDateChooser;

public class PatientFormView extends JDialog {

	private static final long serialVersionUID = 1L;

	private JTextField txtName;
	private JTextField txtEmail;
	private JTextField txtPhoneNumber;
	private JTextField txtAddress;
	private JDateChooser dobChooser;
	private JComboBox<String> cbGender;
	private JTextField txtCitizenId;

	private JButton btnSave;
	private JButton btnCancel;

	private boolean saved = false;
	private Patient patient;

	// ----------------- Constructor ---------------
	public PatientFormView(Frame owner, Patient patient) {
		super(owner, true);
		this.patient = patient;

		setTitle(patient == null ? "Add Patient" : "Edit Patient");
		setSize(500, 520);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout());

		initComponents();

		if (patient != null)
			loadPatientData();
	}

	private void initComponents() {
		add(createHeaderPanel(), BorderLayout.NORTH);
		add(buildFormPanel(), BorderLayout.CENTER);
		add(buildFooterPanel(), BorderLayout.SOUTH);
	}

	private JPanel createHeaderPanel() {
		// Tạo Panel tổng
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(StyleConstants.COLOR_BLUE_50);
		headerPanel.setBorder(new EmptyBorder(10, 20, 0, 20));

		JLabel title = new JLabel(patient == null ? "Add New Patient" : "Edit Patient", JLabel.CENTER);
		title.setFont(StyleConstants.TITLE_FONT);
		title.setForeground(StyleConstants.TITLE_FG);
		title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		headerPanel.add(title, BorderLayout.CENTER);
		return headerPanel;
	}

	private JPanel buildFormPanel() {
		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		formPanel.setBackground(StyleConstants.COLOR_WHITE);

		txtName = new JTextField(20);
		txtEmail = new JTextField(20);
		txtPhoneNumber = new JTextField(15);
		txtAddress = new JTextField(20);
		dobChooser = new JDateChooser();
		dobChooser.setDateFormatString("yyyy-MM-dd");
		cbGender = new JComboBox<>(new String[] { "Male", "Female", "Other" });
		txtCitizenId = new JTextField(15);

		int row = 0;
		FormUtilities.addFormRow(formPanel, "Full Name *:", txtName, row++);
		FormUtilities.addFormRow(formPanel, "Email *:", txtEmail, row++);
		FormUtilities.addFormRow(formPanel, "Phone Number:", txtPhoneNumber, row++);
		FormUtilities.addFormRow(formPanel, "Address:", txtAddress, row++);
		FormUtilities.addFormRow(formPanel, "Date of Birth:", dobChooser, row++);
		FormUtilities.addFormRow(formPanel, "Gender *:", cbGender, row++);
		FormUtilities.addFormRow(formPanel, "Citizen ID *:", txtCitizenId, row++);

		return formPanel;
	}

	private JPanel buildFooterPanel() {
		JPanel footerPanel = new JPanel(new BorderLayout());
		footerPanel.setBackground(StyleConstants.COLOR_BLUE_50);
		footerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

		// Chú thích
		JLabel noteLabel = new JLabel("(*) Required fields. Red background = invalid input. Hover to see error.");
		noteLabel.setFont(new Font("Arial", Font.ITALIC, 11));
		noteLabel.setForeground(Color.RED);

		// Panel nút
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
		buttonPanel.setOpaque(false);

		btnSave = FormUtilities.styleButton(
                createButtonNoIcon("Save"),
                StyleConstants.BUTTON_BG,
                StyleConstants.NORMAL_TEXT_COLOR
        );
		
		btnCancel = FormUtilities.styleButton(
                createButtonNoIcon("Cancel"),
                StyleConstants.BUTTON_BG,
                StyleConstants.NORMAL_TEXT_COLOR
        );

		buttonPanel.add(btnSave);
		buttonPanel.add(btnCancel);

		footerPanel.add(noteLabel, BorderLayout.NORTH);
		footerPanel.add(buttonPanel, BorderLayout.SOUTH);

		return footerPanel;
	}

	private void loadPatientData() {
		txtName.setText(patient.getName());
		txtEmail.setText(patient.getEmail());
		txtPhoneNumber.setText(patient.getPhoneNumber());
		txtAddress.setText(patient.getAddress());
		cbGender.setSelectedItem(patient.getGender());
		txtCitizenId.setText(patient.getCitizenId());

		if (patient.getDateOfBirth() != null) {
			Date dob = Date.from(patient.getDateOfBirth().atStartOfDay(ZoneId.systemDefault()).toInstant());
			dobChooser.setDate(dob);
		}
	}

	public boolean showPatientForm() {
		setVisible(true);
		return isSaved();
	}
	
	private JButton createButtonNoIcon(String text) {
        JButton button = FormUtilities.createTextButton(text);
        button.setPreferredSize(new Dimension(140, 35));
        return button;
    }


	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	public boolean isSaved() {
		return saved;
	}

	// ----------------- Helpers -----------------
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
	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public JTextField getNameField() {
		return txtName;
	}

	public JTextField getEmailField() {
		return txtEmail;
	}

	public JTextField getPhoneNumberField() {
		return txtPhoneNumber;
	}

	public JTextField getAddressField() {
		return txtAddress;
	}

	public JDateChooser getDobChooser() {
		return dobChooser;
	}

	public JComboBox<String> getGenderComboBox() {
		return cbGender;
	}

	public JTextField getCitizenIdField() {
		return txtCitizenId;
	}

	public JButton getSaveButton() {
		return btnSave;
	}

	public JButton getCancelButton() {
		return btnCancel;
	}
}