package com.kien.project.clinicmanagement.view.auth;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.ShowMessage;
import com.kien.project.clinicmanagement.utils.StyleConstants;

public class ForgotPasswordView extends JFrame {
	private static final long serialVersionUID = 1L;

	private CardLayout cardLayout;
	private JPanel cardPanel;

	// Step 1
	private JTextField txtUsername;
	private JTextField txtEmail;
	private JButton btnSendCode;
	private JButton btnBack;

	// Step 2
	private JTextField txtVerificationCode;
	private JButton btnVerifyCode;
	private JButton btnBackStep2;

	// Step 3
	private JPasswordField txtNewPassword;
	private JPasswordField txtConfirmPassword;
	private JCheckBox chkShowPassword;
	private JButton btnChangePassword;
	private JButton btnBackStep3;
	
	

	public ForgotPasswordView() {
		setTitle("Clinic Management Application - Forgot Password");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(850, 500);
		setLocationRelativeTo(null);
		setResizable(false);
		initComponents();
	}

	private void initComponents() {
		JPanel container = new JPanel(new BorderLayout());
		container.setBackground(StyleConstants.COLOR_BLUE_50);

		JPanel leftPanel = new JPanel();
		leftPanel = createLeftPanel();
		JPanel rightPanel = new JPanel();
		rightPanel = createRightPanel();

		// SplitPane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, rightPanel, leftPanel);
		splitPane.setDividerLocation(450);
		splitPane.setEnabled(false);
		splitPane.setBorder(null);
		splitPane.setDividerSize(0);

		container.add(splitPane, BorderLayout.CENTER);
		getContentPane().add(container);

		// Default step
		showStep1();
	}

	
	private JPanel createLeftPanel() {
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setBackground(StyleConstants.COLOR_WHITE);

		ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/for_view/KeyHealth2.png"));
		Image scaledImg = logoIcon.getImage().getScaledInstance(270, 270, Image.SCALE_SMOOTH);
		logoIcon = new ImageIcon(scaledImg);

		JLabel logo = new JLabel(logoIcon, JLabel.CENTER);

		JPanel logoPanel = new JPanel(new BorderLayout());
		logoPanel.setBackground(StyleConstants.COLOR_WHITE);
		logoPanel.add(logo, BorderLayout.CENTER);

		leftPanel.add(logoPanel, BorderLayout.CENTER);

		return leftPanel;
	}
	

	private JPanel createRightPanel() {
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.setBackground(StyleConstants.COLOR_BLUE_50);

		cardPanel = createCardPanel();

		rightPanel.add(cardPanel, BorderLayout.CENTER);
		return rightPanel;
	}

	private JLabel createHeader() {
		JLabel title = new JLabel("Forgot Password", JLabel.CENTER);
		title.setBorder(new EmptyBorder(0, 10, 40, 10));
		title.setFont(new Font("Segoe UI", Font.BOLD, 26));
		title.setForeground(StyleConstants.COLOR_BLUE_700);
		return title;
	}

	private JPanel createCardPanel() {
		cardLayout = new CardLayout();
		JPanel panel = new JPanel(cardLayout);
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

		panel.add(createStep1Panel(), "STEP1");
		panel.add(createStep2Panel(), "STEP2");
		panel.add(createStep3Panel(), "STEP3");

		return panel;
	}
	

	// ----------------- STEP 1 -----------------
	private JPanel createStep1Panel() {
		JPanel step1Panel = new JPanel(new BorderLayout());
		step1Panel.setOpaque(false);

		JPanel form = new JPanel(new GridBagLayout());
		form.setOpaque(false);
		txtUsername = new JTextField(20);
		txtEmail = new JTextField(20);

		int row = 0;
		FormUtilities.addComponent(form, createHeader(), 0, row++, 2);
		FormUtilities.addFormRow(form, "Username:", txtUsername, row++);
		FormUtilities.addFormRow(form, "Email:", txtEmail, row++);
		FormUtilities.addComponent(form, createButtonPanelStep1(), 0, row, 2);

		step1Panel.add(form);
		return step1Panel;
	}

	private JPanel createButtonPanelStep1() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
		buttonPanel.setOpaque(false);

		btnSendCode = FormUtilities.styleButton(createButtonNoIcon("Send Code"), StyleConstants.BUTTON_BG,
				StyleConstants.NORMAL_TEXT_COLOR);

		btnBack = FormUtilities.styleButton(createButtonNoIcon("Back"), StyleConstants.BUTTON_BG,
				StyleConstants.NORMAL_TEXT_COLOR);

		buttonPanel.add(btnBack);
		buttonPanel.add(btnSendCode);

		return buttonPanel;
	}
	

	// ----------------- STEP 2 -----------------
	private JPanel createStep2Panel() {
		JPanel step2Panel = new JPanel(new BorderLayout());
		step2Panel.setOpaque(false);

		JPanel form = new JPanel(new GridBagLayout());
		form.setOpaque(false);
		txtVerificationCode = new JTextField(20);

		int row = 0;
		FormUtilities.addComponent(form, createHeader(), 0, row++, 2);
		FormUtilities.addFormRow(form, "Verification Code:", txtVerificationCode, row++);
		FormUtilities.addComponent(form, createButtonPanelStep2(), 0, row, 2);

		step2Panel.add(form);
		return step2Panel;
	}

	private JPanel createButtonPanelStep2() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
		buttonPanel.setOpaque(false);

		btnBackStep2 = FormUtilities.styleButton(createButtonNoIcon("Back"), StyleConstants.BUTTON_BG,
				StyleConstants.NORMAL_TEXT_COLOR);

		btnVerifyCode = FormUtilities.styleButton(createButtonNoIcon("Verify Code"), StyleConstants.BUTTON_BG,
				StyleConstants.NORMAL_TEXT_COLOR);

		buttonPanel.add(btnBackStep2);
		buttonPanel.add(btnVerifyCode);

		return buttonPanel;
	}
	

	// ----------------- STEP 3 -----------------
	private JPanel createStep3Panel() {
		JPanel step3Panel = new JPanel(new BorderLayout());
		step3Panel.setOpaque(false);

		JPanel form = new JPanel(new GridBagLayout());
		form.setOpaque(false);
		txtNewPassword = new JPasswordField(20);
		txtConfirmPassword = new JPasswordField(20);
		chkShowPassword = new JCheckBox("Show password");
		chkShowPassword.setOpaque(false);

		int row = 0;
		FormUtilities.addComponent(form, createHeader(), 0, row++, 2);
		FormUtilities.addFormRow(form, "New Password:", txtNewPassword, row++);
		FormUtilities.addFormRow(form, "Confirm Password:", txtConfirmPassword, row++);
		FormUtilities.addComponent(form, chkShowPassword, 0, row++, 2);
		FormUtilities.addComponent(form, createButtonPanelStep3(), 0, row, 2);

		step3Panel.add(form);
		return step3Panel;
	}

	private JPanel createButtonPanelStep3() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
		buttonPanel.setOpaque(false);

		btnBackStep3 = FormUtilities.styleButton(createButtonNoIcon("Back"), StyleConstants.BUTTON_BG,
				StyleConstants.NORMAL_TEXT_COLOR);

		btnChangePassword = FormUtilities.styleButton(createButtonNoIcon("Change Password"), StyleConstants.BUTTON_BG,
				StyleConstants.NORMAL_TEXT_COLOR);

		buttonPanel.add(btnBackStep3);
		buttonPanel.add(btnChangePassword);

		return buttonPanel;
	}

	private JButton createButtonNoIcon(String text) {
		JButton button = FormUtilities.createTextButton(text);
		button.setPreferredSize(new Dimension(140, 35));
		return button;
	}
	

	public void showStep1() {
		cardLayout.show(cardPanel, "STEP1");
	}

	public void showStep2() {
		cardLayout.show(cardPanel, "STEP2");
	}

	public void showStep3() {
		cardLayout.show(cardPanel, "STEP3");
	}
	

	
	public void showWarningMessage(String message) {
		ShowMessage.showWarning(this, message);
	}

	public void showErrorMessage(String message) {
		ShowMessage.showError(this, message);
	}

	public void showInfoMessage(String message) {
		ShowMessage.showInfo(this, message);
	}

	public void close() {
		this.dispose();
	}

	
	
	public String getUsername() {
		return txtUsername.getText().trim();
	}

	public String getEmail() {
		return txtEmail.getText().trim();
	}

	public String getVerificationCode() {
		return txtVerificationCode.getText().trim();
	}

	public String getNewPassword() {
		return new String(txtNewPassword.getPassword());
	}

	public String getConfirmPassword() {
		return new String(txtConfirmPassword.getPassword());
	}

	public JTextField getUsernameField() {
		return txtUsername;
	}

	public JTextField getEmailField() {
		return txtEmail;
	}

	public JTextField getVerificationCodeField() {
		return txtVerificationCode;
	}

	public JPasswordField getNewPasswordField() {
		return txtNewPassword;
	}

	public JPasswordField getConfirmPasswordField() {
		return txtConfirmPassword;
	}

	public JButton getSendCodeButton() {
		return btnSendCode;
	}

	public JButton getVerifyCodeButton() {
		return btnVerifyCode;
	}

	public JButton getBackButton() {
		return btnBack;
	}

	public JButton getBackStep2Button() {
		return btnBackStep2;
	}

	public JButton getChangePasswordButton() {
		return btnChangePassword;
	}

	public JButton getBackStep3Button() {
		return btnBackStep3;
	}

	public JCheckBox getShowPasswordCheckBox() {
		return chkShowPassword;
	}
}
