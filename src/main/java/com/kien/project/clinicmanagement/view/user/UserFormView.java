package com.kien.project.clinicmanagement.view.user;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.kien.project.clinicmanagement.model.User;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.ShowMessage;
import com.kien.project.clinicmanagement.utils.StyleConstants;
import com.toedter.calendar.JDateChooser;

public class UserFormView extends JDialog {
	private static final long serialVersionUID = 1L;

	// ----------------- UI Components ---------------
	private JTextField txtName;
	private JTextField txtUsername;
	private JPasswordField txtPassword;
	private JComboBox<String> cbGender;
	private JDateChooser dobChooser;
	private JTextField txtEmail;
	private JTextField txtPhoneNumber;
	private JTextField txtAddress;
	private JTextField txtCitizenId;
	private JComboBox<String> cbRole;

	private JButton btnSave, btnCancel;

	private boolean saved = false;
	private User user;

	// ----------------- Constructor ---------------
	public UserFormView(Window owner, User user) {
		super(owner, "Edit User", ModalityType.APPLICATION_MODAL);
		this.user = user;

		setTitle(user == null ? "Add User" : "Edit User");
		setSize(500, 560);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout());

		initComponents();

		if (user != null) loadUserData();
	}

	// ----------------- UI Initialization ---------------
	private void initComponents() {
		add(createHeaderPanel(), BorderLayout.NORTH);
		add(createFormPanel(), BorderLayout.CENTER);
		add(createFooterPanel(), BorderLayout.SOUTH);
	}

	// Hàm tạo Header
	private JPanel createHeaderPanel() {
		// Tạo Panel tổng
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(StyleConstants.COLOR_BLUE_50);
		headerPanel.setBorder(new EmptyBorder(10, 20, 0, 20));

		JLabel title = new JLabel(user == null ? "Add New User" : "Edit User", JLabel.CENTER);
		title.setFont(StyleConstants.TITLE_FONT);
		title.setForeground(StyleConstants.COLOR_BLUE_800);
		title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		headerPanel.add(title, BorderLayout.CENTER);
		return headerPanel;
	}

	// Hàm tạo Form
	private JPanel createFormPanel() {
		// Tạo Panel tổng
		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		formPanel.setBackground(StyleConstants.COLOR_WHITE);

		// Tạo các thành phần
		txtName = new JTextField(20);
		txtUsername = new JTextField(20);
		txtPassword = new JPasswordField(20);
		cbGender = new JComboBox<>(new String[] { "Male", "Female", "Other" });
		dobChooser = new JDateChooser();
		dobChooser.setDateFormatString("yyyy-MM-dd");
		txtEmail = new JTextField(20);
		txtPhoneNumber = new JTextField(15);
		txtAddress = new JTextField(20);
		txtCitizenId = new JTextField(15);
		cbRole = new JComboBox<>(new String[] { "Admin", "Doctor", "Receptionist" });
		dobChooser.setDateFormatString("yyyy-MM-dd");

		int row = 0;
		FormUtilities.addFormRow(formPanel, "Full Name *:", txtName, row++);
		FormUtilities.addFormRow(formPanel, "Username *:", txtUsername, row++);
		if (user == null)
			FormUtilities.addFormRow(formPanel, "Password *:", txtPassword, row++);
		FormUtilities.addFormRow(formPanel, "Gender *:", cbGender, row++);
		FormUtilities.addFormRow(formPanel, "Date of Birth:", dobChooser, row++); // optional
		FormUtilities.addFormRow(formPanel, "Email *:", txtEmail, row++);
		FormUtilities.addFormRow(formPanel, "Phone Number:", txtPhoneNumber, row++);
		FormUtilities.addFormRow(formPanel, "Address:", txtAddress, row++);
		FormUtilities.addFormRow(formPanel, "Citizen ID *:", txtCitizenId, row++);
		FormUtilities.addFormRow(formPanel, "Role:", cbRole, row++);

		return formPanel;
	}

	// Hàm tạo Footer
	private JPanel createFooterPanel() {
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

	// Hàm gắn các thuộc tính User và field
	private void loadUserData() {
		txtName.setText(user.getName());
		txtUsername.setText(user.getUsername());
		cbGender.setSelectedItem(user.getGender());

		if (user.getDateOfBirth() != null) {
			Date dob = Date.from(user.getDateOfBirth().atStartOfDay(ZoneId.systemDefault()).toInstant());
			dobChooser.setDate(dob);
		}

		txtEmail.setText(user.getEmail());
		txtPhoneNumber.setText(user.getPhoneNumber());
		txtAddress.setText(user.getAddress());
		txtCitizenId.setText(user.getCitizenId());
		cbRole.setSelectedItem(user.getRole());
	}

	public boolean showUserForm() {
		setVisible(true);
		return isSaved();
	}

	private JButton createButtonNoIcon(String text) {
        JButton button = FormUtilities.createTextButton(text);
        button.setPreferredSize(new Dimension(140, 35));
        return button;
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

	public void setSaved(boolean saved) {
	    this.saved = saved;
	}

	public boolean isSaved() {
		return saved;
	}

	// --------------- Getter for Controller--------------
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public JTextField getNameField() {
		return txtName;
	}

	public JTextField getUsernameField() {
		return txtUsername;
	}

	public JPasswordField getPasswordField() {
		return txtPassword;
	}

	public JComboBox<String> getGenderComboBox() {
		return cbGender;
	}

	public JDateChooser getDobChooser() {
		return dobChooser;
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

	public JTextField getCitizenIdField() {
		return txtCitizenId;
	}

	public JComboBox<String> getRoleComboBox() {
		return cbRole;
	}

	public JButton getSaveButton() {
		return btnSave;
	}

	public JButton getCancelButton() {
		return btnCancel;
	}
}
