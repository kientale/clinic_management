package com.kien.project.clinicmanagement.view.user;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.kien.project.clinicmanagement.model.User;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.ShowMessage;
import com.kien.project.clinicmanagement.utils.StyleConstants;

public class DoctorSelectionView extends JDialog {

	private static final long serialVersionUID = 1L;

	private DefaultTableModel tableModel;
	private JTable doctorTable;
	private JTextField searchField;
	private JComboBox<String> searchTypeCombo;
	private JLabel pageInfoLabel;
	
	private JButton btnSearch, btnRefresh;
	private JButton btnSelect;
	
	private JButton btnPrevPage, btnNextPage;

	public DoctorSelectionView(Window owner) {
		super(owner, "Select Doctor", ModalityType.APPLICATION_MODAL);
		setSize(750, 520);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout());
		initComponents();
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

		JLabel title = new JLabel("Doctor Selection", JLabel.CENTER);
		title.setFont(StyleConstants.TITLE_FONT);
		title.setForeground(StyleConstants.COLOR_BLUE_800);

		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		searchPanel.setOpaque(false);
		searchField = new JTextField(18);
		searchTypeCombo = new JComboBox<>(new String[] { "Search by name", "Search by email" });
		btnSearch = FormUtilities.styleButton(createButton("Search", "/images/for_button/search.png"),new Color(66, 165, 245),Color.WHITE);
		btnRefresh = FormUtilities.styleButton(createButton("Refresh", "/images/for_button/refresh.png"),new Color(38, 166, 154),Color.WHITE);

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
		panel.setBorder(new EmptyBorder(10, 20, 10, 20));
		String[] columnNames = new String[] { "Code", "Name", "Email", "Phone", "Today's Load" };
		tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Chỉ đọc
            }
        };
		doctorTable = FormUtilities.createStyledTable(tableModel);
		panel.add(new JScrollPane(doctorTable), BorderLayout.CENTER);
		return panel;
	}

	private JPanel buildFooterPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(StyleConstants.COLOR_BLUE_50);
		panel.setBorder(new EmptyBorder(10, 20, 10, 20));

		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		leftPanel.setOpaque(false);
		btnSelect = FormUtilities.styleButton(createButton("Select", "/images/for_button/select.png"),new Color(102, 187, 106), Color.WHITE);
		leftPanel.add(btnSelect);

		pageInfoLabel = new JLabel("Page 1/1");
		btnPrevPage = FormUtilities.styleButton(createButton("Prev", "/images/for_button/previous.png"),new Color(189, 189, 189),Color.BLACK);
		btnNextPage = FormUtilities.styleButton(createButton("Next", "/images/for_button/next.png"),new Color(189, 189, 189),Color.BLACK);
		JPanel paginationPanel = FormUtilities.createPaginationPanel(btnPrevPage, pageInfoLabel, btnNextPage);

		panel.add(leftPanel, BorderLayout.WEST);
		panel.add(paginationPanel, BorderLayout.EAST);

		return panel;
	}

	private JButton createButton(String text, String iconPath) {
		JButton btn = FormUtilities.createIconButton(text, iconPath, 16);
		return btn;
	}
	
	// Hàm hiển thị danh sách bác sĩ trong bảng
	public void renderDoctorTable(List<User> doctors, int startIndex) {
	    tableModel.setRowCount(0); // Xóa dữ liệu cũ
	    
	    for (int i = 0; i < doctors.size(); i++) {
	        User d = doctors.get(i);
	        Object[] row = new Object[] {
	            d.getCode(),                      
	            d.getName(),                    
	            d.getEmail(),                    
	            d.getPhoneNumber(),               
	            d.getTodayLoad() != null 
	                ? d.getTodayLoad() + " patients"
	                : "0 patients"
	        };
	        tableModel.addRow(row);
	    }
	}
	
	public void updatePageInfo(int currentPage, int totalPages) {
        pageInfoLabel.setText("Page " + currentPage + "/" + totalPages);
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

	
	// Getters for controller
	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	public JTable getDoctorTable() {
		return doctorTable;
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

	public JButton getPrevPageButton() {
		return btnPrevPage;
	}

	public JButton getNextPageButton() {
		return btnNextPage;
	}

	public JButton getSelectButton() {
		return btnSelect;
	}
}
