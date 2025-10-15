package com.kien.project.clinicmanagement.view.user;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.kien.project.clinicmanagement.model.User;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.ShowMessage;
import com.kien.project.clinicmanagement.utils.StyleConstants;

public class UserProfileView extends JDialog {
	private static final long serialVersionUID = 1L;

	// ----------------- UI Components ---------------
	private JLabel lblAvatar;
	private JButton btnClose, btnEditProfile, btnChangePassword;

	// User để láy thông tin
	private User user;

	// ----------------- Constructor ---------------
	public UserProfileView(JFrame owner, User user) {
		super(owner, "User Profile - " + user.getName(), true);
		this.user = user;

		setSize(500, 530);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout(10, 10));
		getContentPane().setBackground(StyleConstants.COLOR_WHITE);

		initComponents();
	}

	// ----------------- UI Initialization ---------------
	private void initComponents() {
		add(buildHeader(), BorderLayout.NORTH);
		add(buildMainPanel(), BorderLayout.CENTER);
		add(buildFooterPanel(), BorderLayout.SOUTH);
	}

	// Hàm tạo Header
	private JPanel buildHeader() {
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(StyleConstants.COLOR_BLUE_50);
		headerPanel.setBorder(new EmptyBorder(10, 20, 0, 20));
		
		JLabel title = new JLabel("User Profile", JLabel.CENTER);
		title.setFont(StyleConstants.TITLE_FONT);
		title.setForeground(StyleConstants.COLOR_BLUE_800);
		title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		headerPanel.add(title, BorderLayout.CENTER);
		return headerPanel;
	}

	private JLabel createAvatarLabel() {
		// Tạo Label hiển thị avatar
		lblAvatar = new JLabel("", SwingConstants.CENTER);
		lblAvatar.setPreferredSize(new Dimension(200, 200));
		ImageIcon avatarIcon = loadUserImage(user.getProfileImage(), 200, 200);
		if (avatarIcon != null) {
			lblAvatar.setIcon(avatarIcon);
		} else {
			lblAvatar.setText("No Image");
			lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
			lblAvatar.setVerticalAlignment(SwingConstants.CENTER);
			lblAvatar.setBorder(BorderFactory.createLineBorder(StyleConstants.COLOR_BLACK));
		}
		return lblAvatar;
	}

	private JPanel createInfoPanel() {
		// Tạo phần thông tin bên phải avatar
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.setOpaque(false);

		infoPanel.add(buildInfoLabel("Fullname:", user.getName()));
		infoPanel.add(buildInfoLabel("Gender:", user.getGender()));
		infoPanel.add(buildInfoLabel("Date of birth:",
				user.getDateOfBirth() != null ? user.getDateOfBirth().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
						: ""));
		infoPanel.add(buildInfoLabel("Address:", user.getAddress()));
		infoPanel.add(buildInfoLabel("Citizen Id:", user.getCitizenId()));
		infoPanel.add(buildInfoLabel("Role:", user.getRole()));

		return infoPanel;
	}

	private JPanel createBottomPanel() {
		// Tạo Panel chứa nội dung dưới
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		bottomPanel.setOpaque(false);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

		bottomPanel.add(buildInfoLabel("Username:", user.getUsername()));
		bottomPanel.add(buildInfoLabel("Email:", user.getEmail()));
		bottomPanel.add(buildInfoLabel("Phone number:", user.getPhoneNumber()));
		
		return bottomPanel;
	}

	// Hàm tạo nội dung
	private JPanel buildMainPanel() {
		// Tạo Panel tổng
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		mainPanel.setBackground(StyleConstants.COLOR_WHITE);

		JLabel lblAvatar = createAvatarLabel();
		JPanel infoPanel = createInfoPanel();

		// Tạo Panel chứa nội dung trên
		JPanel topPanel = new JPanel(new BorderLayout(20, 0));
		topPanel.setOpaque(false);
		topPanel.add(lblAvatar, BorderLayout.WEST);
		topPanel.add(infoPanel, BorderLayout.CENTER);
		
		JPanel bottomPanel = createBottomPanel();

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setOpaque(false);
		centerPanel.add(topPanel, BorderLayout.CENTER);
		centerPanel.add(bottomPanel, BorderLayout.SOUTH);

		mainPanel.add(centerPanel, BorderLayout.CENTER);

		return mainPanel;
	}

	// Hàm tạo Footer
	private JPanel buildFooterPanel() {
		JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
		footerPanel.setBackground(StyleConstants.FOOTER_PANEL_BG);

		btnClose = FormUtilities.styleButton(
                createButtonNoIcon("Close"),
                StyleConstants.BUTTON_BG,
                StyleConstants.NORMAL_TEXT_COLOR
        );
		
		btnEditProfile = FormUtilities.styleButton(
                createButtonNoIcon("Edit Profile"),
                StyleConstants.BUTTON_BG,
                StyleConstants.NORMAL_TEXT_COLOR
        );
		
		btnChangePassword = FormUtilities.styleButton(
                createButtonNoIcon("Change Password"),
                StyleConstants.BUTTON_BG,
                StyleConstants.NORMAL_TEXT_COLOR
        );

		footerPanel.add(btnEditProfile);
		footerPanel.add(btnChangePassword);
		footerPanel.add(btnClose);
		return footerPanel;
	}

	// Hàm tạo Label chứa thông tin cá nhân
	private JPanel buildInfoLabel(String label, String value) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.setOpaque(false);

		JLabel lblTitle = new JLabel(label);
		lblTitle.setFont(StyleConstants.LABEL_BOLD);

		JLabel lblValue = new JLabel(value != null ? value : "");
		lblValue.setFont(StyleConstants.LABEL_PLAIN);

		panel.add(lblTitle);
		panel.add(lblValue);

		return panel;
	}

	// Hàm load avatar
	private ImageIcon loadUserImage(String path, int width, int height) {
		if (path == null || path.isEmpty())
			return null;

		try {
			File file = new File(path);
			if (file.exists()) {
				ImageIcon icon = new ImageIcon(file.getAbsolutePath());
				return FormUtilities.scaleIcon(icon, width, height);
			}

			// Nếu không tồn tại file, thử load từ resource
			URL resource = getClass().getResource(path);
			if (resource != null) {
				ImageIcon icon = new ImageIcon(resource);
				return FormUtilities.scaleIcon(icon, width, height);
			}
		} catch (Exception e) {
			System.err.println("Could not load image: " + path + " -> " + e.getMessage());
		}
		return null;
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

	// --------------- Getter for Controller--------------
	public User getUser() {
		return user;
	}

	public JButton getCloseButton() {
		return btnClose;
	}

	public JButton getEditProfileButton() {
		return btnEditProfile;
	}

	public JButton getChangePasswordButton() {
		return btnChangePassword;
	}
}
