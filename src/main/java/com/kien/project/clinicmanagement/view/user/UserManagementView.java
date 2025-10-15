package com.kien.project.clinicmanagement.view.user;

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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.kien.project.clinicmanagement.controller.UserController;
import com.kien.project.clinicmanagement.model.User;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.ShowMessage;
import com.kien.project.clinicmanagement.utils.StyleConstants;

public class UserManagementView extends JPanel {
	private static final long serialVersionUID = 1L;

	// ----------------- UI Components ---------------
	// Header
	private JTextField searchField;
	private JComboBox<String> searchTypeCombo;
	private JComboBox<String> filterUserCombo;
	private JButton btnSearch, btnRefresh;

	// Table
	private JTable userTable;
	private DefaultTableModel tableModel;

	// Footer
	private JButton btnAddNewUser, btnEditUser, btnDeleteUser, btnResetPassword, btnViewUserActivityLog,
			btnViewUserDetail;

	// Pagination
	private JLabel lblPageInfo;
	private JButton btnPrevPage, btnNextPage;

	// Controller
	private UserController userController;

	// ----------------- Constructor -----------------
	public UserManagementView() {
		setLayout(new BorderLayout(10, 10));
		setBackground(StyleConstants.COLOR_WHITE);

		initComponents();
		userController = new UserController(this);
	}

	// ----------------- UI Initialization ---------------
	private void initComponents() {
		add(createHeaderPanel(), BorderLayout.NORTH);
		add(createTablePanel(), BorderLayout.CENTER);
		add(createFooterPanel(), BorderLayout.SOUTH);
	}

	// Hàm tạo Header
	private JPanel createHeaderPanel() {
		// Tạo Panel tổng
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(StyleConstants.COLOR_BLUE_50);
		headerPanel.setBorder(new EmptyBorder(10, 20, 0, 20));

		// Tạo tiêu đề
		JLabel title = new JLabel("User Management", JLabel.CENTER);
		title.setFont(StyleConstants.TITLE_FONT);
		title.setForeground(StyleConstants.COLOR_BLUE_800);
		title.setBorder(new EmptyBorder(10, 0, 10, 0));

		// Tạo các thành phần chức năng
		searchField = new JTextField(18);
		searchTypeCombo = new JComboBox<>(new String[] { "Search by name", "Search by address",
				"Search by phone number", "Search by citizen id" });
		filterUserCombo = new JComboBox<>(new String[] { "All", "Admin", "Doctor", "Receptionist" });

		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		searchPanel.setOpaque(false);
		btnSearch = FormUtilities.styleButton(createButton("Search", "/images/for_button/search.png"),
				new Color(66, 165, 245), Color.WHITE);
		btnRefresh = FormUtilities.styleButton(createButton("Refresh", "/images/for_button/refresh.png"),
				new Color(38, 166, 154), Color.WHITE);

		searchPanel.add(new JLabel("Search:"));
		searchPanel.add(searchField);
		searchPanel.add(searchTypeCombo);
		searchPanel.add(btnSearch);
		searchPanel.add(btnRefresh);
		searchPanel.add(new JLabel("Filter by role:"));
		searchPanel.add(filterUserCombo);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setOpaque(false);
		bottomPanel.add(searchPanel, BorderLayout.WEST);

		headerPanel.add(title, BorderLayout.NORTH);
		headerPanel.add(bottomPanel, BorderLayout.SOUTH);
		return headerPanel;
	}

	// Hàm tạo Table
	private JPanel createTablePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(StyleConstants.COLOR_WHITE);
		panel.setBorder(new EmptyBorder(0, 20, 10, 20));

		String[] columnNames = new String[] { "No", "Full Name", "Phone Number", "Address", "Citizen ID", "Role" };
		tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Không được chỉnh sửa bảng
			}
		};
		userTable = FormUtilities.createStyledTable(tableModel);
		panel.add(new JScrollPane(userTable), BorderLayout.CENTER);
		return panel;
	}

	// Hàm tạo Footer
	private JPanel createFooterPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(StyleConstants.COLOR_BLUE_50);
		panel.setBorder(new EmptyBorder(10, 20, 20, 20));

		JPanel crudPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		crudPanel.setOpaque(false);

		btnAddNewUser = FormUtilities.styleButton(createButton("Add", "/images/for_button/add.png"),
				new Color(102, 187, 106), Color.WHITE);
		btnEditUser = FormUtilities.styleButton(createButton("Edit", "/images/for_button/edit.png"),
				new Color(255, 213, 79), Color.BLACK);
		btnDeleteUser = FormUtilities.styleButton(createButton("Delete", "/images/for_button/delete.png"),
				new Color(239, 83, 80), Color.WHITE);
		btnResetPassword = FormUtilities.styleButton(
				createButton("Reset Password", "/images/for_button/reset_password.png"), StyleConstants.BUTTON_BG,
				Color.BLACK);
		btnViewUserActivityLog = FormUtilities.styleButton(
				createButton("Activity Log", "/images/for_button/view_user_log.png"), StyleConstants.BUTTON_BG,
				Color.BLACK);
		btnViewUserDetail = FormUtilities.styleButton(createButton("User Detail", "/images/for_button/user_detail.png"),
				StyleConstants.BUTTON_BG, Color.BLACK);

		crudPanel.add(new JLabel("User Management:"));
		crudPanel.add(btnAddNewUser);
		crudPanel.add(btnEditUser);
		crudPanel.add(btnDeleteUser);
		crudPanel.add(new JLabel("Information:"));
		crudPanel.add(btnResetPassword);
		crudPanel.add(btnViewUserActivityLog);
		crudPanel.add(btnViewUserDetail);

		lblPageInfo = new JLabel("Page 1/1");
		btnPrevPage = FormUtilities.styleButton(createButton("Prev", "/images/for_button/previous.png"),
				new Color(189, 189, 189), Color.BLACK);
		btnNextPage = FormUtilities.styleButton(createButton("Next", "/images/for_button/next.png"),
				new Color(189, 189, 189), Color.BLACK);

		JPanel paginationPanel = FormUtilities.createPaginationPanel(btnPrevPage, lblPageInfo, btnNextPage);

		panel.add(crudPanel, BorderLayout.WEST);
		panel.add(paginationPanel, BorderLayout.EAST);

		return panel;
	}

	// Hàm tạo nút với icon
	private JButton createButton(String text, String iconPath) {
		return FormUtilities.createIconButton(text, iconPath, 18);
	}

	// Hàm hiển thị lịch sử người dùng
	public void showActivityLog(User user, List<String> logs) {
		if (logs.isEmpty()) {
			ShowMessage.showWarning(this, "No activity logs found for this user.");
			return;
		}
		JTextArea logArea = new JTextArea(String.join("\n", logs), 15, 40);
		logArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(logArea);
		ShowMessage.showInfo(this, scrollPane);
	}

	// Hàm render các giá trị của bảng
	public void renderUserTable(List<User> users, int startIndex) {
		tableModel.setRowCount(0);
		for (int i = 0; i < users.size(); i++) {
			User u = users.get(i);
			tableModel.addRow(new Object[] { startIndex + i + 1, u.getName(), u.getPhoneNumber(), u.getAddress(),
					u.getCitizenId(), u.getRole() });
		}
	}

	// Hàm cập nhật số trang
	public void updatePageInfo(int currentPage, int totalPages) {
		lblPageInfo.setText("Page " + currentPage + "/" + totalPages);
	}

	// ----------------- Helpers -----------------
	public boolean confirmDeleteUser(User user) {
		int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete user " + user.getName() + "?",
				"Confirm Deletion", JOptionPane.YES_NO_OPTION);
		return choice == JOptionPane.YES_OPTION;
	}

	public boolean confirmResetUserPassword(User user) {
		int choice = JOptionPane.showConfirmDialog(this,
				"Reset password for user " + user.getCode() + " - " + user.getName() + "?", "Confirm Reset",
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
	public JTable getUserTable() {
		return userTable;
	}

	public JTextField getSearchField() {
		return searchField;
	}

	public JComboBox<String> getSearchTypeCombo() {
		return searchTypeCombo;
	}

	public JLabel getPageInfoLabel() {
		return lblPageInfo;
	}

	public JButton getSearchButton() {
		return btnSearch;
	}

	public JButton getRefreshButton() {
		return btnRefresh;
	}

	public JButton getAddNewUserButton() {
		return btnAddNewUser;
	}

	public JButton getEditUserButton() {
		return btnEditUser;
	}

	public JButton getDeleteUserButton() {
		return btnDeleteUser;
	}

	public JButton getResetPasswordButton() {
		return btnResetPassword;
	}

	public JButton getViewActivityLogButton() {
		return btnViewUserActivityLog;
	}

	public JButton getViewUserDetailButton() {
		return btnViewUserDetail;
	}

	public JButton getPrevPageButton() {
		return btnPrevPage;
	}

	public JButton getNextPageButton() {
		return btnNextPage;
	}

	public JComboBox<String> getFilterUserByRoleComboBox() {
		return filterUserCombo;
	}

	public UserController getUserController() {
		return userController;
	}
}
