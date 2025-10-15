package com.kien.project.clinicmanagement.view.user;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import com.kien.project.clinicmanagement.model.User;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.ShowMessage;
import com.kien.project.clinicmanagement.utils.StyleConstants;

public class ChangePasswordView extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPasswordField txtOldPassword;
	private JPasswordField txtNewPassword;
	private JPasswordField txtConfirmPassword;
	private JCheckBox chkShowPassword;

	private JButton btnChangePassword, btnCancel;

	private boolean saved = false;
	private User user;

	public ChangePasswordView(Window owner, User user) {
		super(owner, "Change Password", ModalityType.APPLICATION_MODAL);
		this.user = user;
		setSize(400, 350);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout());
		initComponents();
	}

	private void initComponents() {
		add(buildHeader(), BorderLayout.NORTH);
		add(buildFormPanel(), BorderLayout.CENTER);
		add(buildFooterPanel(), BorderLayout.SOUTH);
	}

	private JPanel buildHeader() {
		// Tạo Panel tổng
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(StyleConstants.COLOR_BLUE_50);
		headerPanel.setBorder(new EmptyBorder(10, 20, 0, 20));
		
		JLabel title = new JLabel("Change Password", JLabel.CENTER);
		title.setFont(StyleConstants.TITLE_FONT);
		title.setForeground(StyleConstants.COLOR_BLUE_800);
		title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		headerPanel.add(title, BorderLayout.CENTER);
		return headerPanel;
	}
	

	private JPanel buildFormPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		panel.setBackground(StyleConstants.COLOR_WHITE);

		txtOldPassword = new JPasswordField(20);
		txtNewPassword = new JPasswordField(20);
		txtConfirmPassword = new JPasswordField(20);
		chkShowPassword = new JCheckBox("Show password");

		int row = 0;
		FormUtilities.addFormRow(panel, "Old Password", txtOldPassword, row++);
		FormUtilities.addFormRow(panel, "New Password", txtNewPassword, row++);
		FormUtilities.addFormRow(panel, "Confirm  Password", txtConfirmPassword, row++);
		FormUtilities.addComponent(panel, chkShowPassword, 0, row++, 2);

		return panel;
	}
	

	private JPanel buildFooterPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
		panel.setBackground(StyleConstants.COLOR_BLUE_50);

		btnChangePassword = FormUtilities.styleButton(
                createButtonNoIcon("Change Password"),
                StyleConstants.BUTTON_BG,
                StyleConstants.NORMAL_TEXT_COLOR
        );
		
		btnCancel = FormUtilities.styleButton(
                createButtonNoIcon("Cancel"),
                StyleConstants.BUTTON_BG,
                StyleConstants.NORMAL_TEXT_COLOR
        );

		panel.add(btnChangePassword);
		panel.add(btnCancel);
		return panel;
	}
	
	private JButton createButtonNoIcon(String text) {
        JButton button = FormUtilities.createTextButton(text);
        button.setPreferredSize(new Dimension(140, 35));
        return button;
    }

	public boolean isSaved() {
		return saved;
	}

	// Hàm hiển thị thông báo
	public void showInfo(String message) {
		ShowMessage.showInfo(this, message);
	}

	public void showError(String message) {
		ShowMessage.showError(this, message);
	}

	public void showWarning(String message) {
		ShowMessage.showWarning(this, message);
	}

	// Hàm getter cho Controller
	public User getUser() {
		return user;
	}

	public String getOldPassword() {
		return String.valueOf(txtOldPassword.getPassword()).trim();
	}

	public String getNewPassword() {
		return String.valueOf(txtNewPassword.getPassword()).trim();
	}

	public JPasswordField getOldPasswordField() {
		return txtOldPassword;
	}

	public JPasswordField getConfirmPasswordField() {
		return txtConfirmPassword;
	}

	public JPasswordField getNewPasswordField() {
		return txtNewPassword;
	}

	public String getConfirmPassword() {
		return String.valueOf(txtConfirmPassword.getPassword()).trim();
	}

	public JButton getChangePasswordButton() {
		return btnChangePassword;
	}

	public JButton getCancelButton() {
		return btnCancel;
	}

	public JCheckBox getShowPasswordCheckBox() {
		return chkShowPassword;
	}
}
