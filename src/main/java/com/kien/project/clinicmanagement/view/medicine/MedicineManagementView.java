package com.kien.project.clinicmanagement.view.medicine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import com.kien.project.clinicmanagement.controller.MedicineController;
import com.kien.project.clinicmanagement.model.Medicine;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.ShowMessage;
import com.kien.project.clinicmanagement.utils.StyleConstants;

public class MedicineManagementView extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTable medicineTable;
	private DefaultTableModel tableModel;
	private JTextField searchField;
	private JComboBox<String> searchTypeCombo;
	private JLabel pageInfoLabel;

	private JButton btnSearch, btnRefresh;
	
	private JButton btnAdd, btnEdit, btnDelete, btnAddStock;
	
	private JButton btnPrevPage, btnNextPage;

	@SuppressWarnings("unused")
	private final MedicineController medicineController;

	
	// ----------------- Constructor -----------------
	public MedicineManagementView() {
		setLayout(new BorderLayout(10, 10));
		setBackground(StyleConstants.COLOR_WHITE);
		
		initComponents();
		medicineController = new MedicineController(this, tableModel);
	}

	private void initComponents() {
		add(buildHeaderPanel(), BorderLayout.NORTH);
		add(buildTablePanel(), BorderLayout.CENTER);
		add(buildFooterPanel(), BorderLayout.SOUTH);
	}

	// Hàm tạo bảng với phân biệt lowStock và normalStock
	public static JTable createStyledMedicineTable(DefaultTableModel model) {
		JTable table = new JTable(model) {
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);

				int quantityColumnIndex = 7; // Cột Quantity (index = 7)
				try {
					Object quantityObj = getValueAt(row, quantityColumnIndex);
					int quantity = Integer.parseInt(quantityObj.toString());

					if (!isRowSelected(row) && quantity < 50) {
						c.setBackground(new Color(255, 204, 204)); // đỏ nhạt
					} else if (!isRowSelected(row)) {
						c.setBackground(StyleConstants.COLOR_WHITE);
					}
				} catch (Exception e) {
					c.setBackground(StyleConstants.COLOR_WHITE);
				}

				return c;
			}
		};

		// Các thuộc tính chung về style (giống createStyledTable)
		table.setRowHeight(24);
		table.setFont(new Font("SansSerif", Font.PLAIN, 13));
		table.setSelectionBackground(new Color(204, 229, 255));
		table.setShowGrid(true);
		table.setGridColor(Color.LIGHT_GRAY);
		table.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		JTableHeader header = table.getTableHeader();
		header.setFont(StyleConstants.LABEL_BOLD);
		header.setBackground(StyleConstants.TITLE_BG);
		header.setForeground(StyleConstants.TABLE_BG);
		((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		// Căn giữa tất cả cell trong bảng
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

		return table;
	}

	private JPanel buildHeaderPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(StyleConstants.COLOR_BLUE_50);
		panel.setBorder(new EmptyBorder(10, 20, 0, 20));

		JLabel title = new JLabel("Medicine Management", JLabel.CENTER);
		title.setFont(StyleConstants.TITLE_FONT);
		title.setForeground(StyleConstants.COLOR_BLUE_800);
		title.setBorder(new EmptyBorder(10, 0, 10, 0));

		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		searchPanel.setOpaque(false);
		searchField = new JTextField(18);
		searchTypeCombo = new JComboBox<>(new String[] { "Search by name", "Search by unit"});
		btnSearch = FormUtilities.styleButton(createButton("Search", "/images/for_button/search.png"),
		        new Color(66, 165, 245),Color.WHITE
		);
		btnRefresh = FormUtilities.styleButton(createButton("Refresh", "/images/for_button/refresh.png"),
		        new Color(38, 166, 154),Color.WHITE);

		searchPanel.add(new JLabel("Search:"));
		searchPanel.add(searchField);
		searchPanel.add(searchTypeCombo);
		searchPanel.add(btnSearch);
		searchPanel.add(btnRefresh);
		searchPanel.add(new JLabel("Quantity:"));
		searchPanel.add(buildLegendPanel());

		panel.add(title, BorderLayout.NORTH);
		panel.add(searchPanel, BorderLayout.SOUTH);
		return panel;
	}

	private JPanel buildTablePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(StyleConstants.COLOR_WHITE);
		panel.setBorder(new EmptyBorder(0, 20, 10, 20));
		String[] columnNames =  new String[] { "No", "Name", "Description", "Unit", "Price", "Min Age", "Max Age", "Quantity" };
		tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Chỉ đọc
            }
        };
		medicineTable = createStyledMedicineTable(tableModel);
		panel.add(new JScrollPane(medicineTable), BorderLayout.CENTER);
		return panel;
	}

	private JPanel buildFooterPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(StyleConstants.COLOR_BLUE_50);
		panel.setBorder(new EmptyBorder(10, 20, 20, 20));

		JPanel crudPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		crudPanel.setOpaque(false);

		btnAdd = FormUtilities.styleButton(createButton("Add", "/images/for_button/add.png"),
                new Color(102, 187, 106), Color.WHITE);
        btnEdit = FormUtilities.styleButton(createButton("Edit", "/images/for_button/edit.png"),
                new Color(255, 213, 79), Color.BLACK);
        btnDelete = FormUtilities.styleButton(createButton("Delete", "/images/for_button/delete.png"),
                new Color(239, 83, 80), Color.WHITE);
		btnAddStock = FormUtilities.styleButton(createButton("Add Stock", "/images/for_button/add_stock.png"),StyleConstants.BUTTON_BG, Color.BLACK);

		crudPanel.add(new JLabel("Medicine Management:"));
		crudPanel.add(btnAdd);
		crudPanel.add(btnEdit);
		crudPanel.add(btnDelete);
		crudPanel.add(btnAddStock);

		pageInfoLabel = new JLabel("Page 1/1");
		btnPrevPage = FormUtilities.styleButton(createButton("Prev", "/images/for_button/previous.png"),
		        new Color(189, 189, 189),Color.BLACK);
		btnNextPage = FormUtilities.styleButton(createButton("Next", "/images/for_button/next.png"),
		        new Color(189, 189, 189),Color.BLACK);
		JPanel paginationPanel = FormUtilities.createPaginationPanel(btnPrevPage, pageInfoLabel, btnNextPage);

		panel.add(crudPanel, BorderLayout.WEST);
		panel.add(paginationPanel, BorderLayout.EAST);
		return panel;
	}

	private JPanel buildLegendPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		panel.setBackground(StyleConstants.COLOR_BLUE_50);

		panel.add(createColorBox(new Color(255, 204, 204)));
		panel.add(new JLabel("Low stock (< 50)"));
		panel.add(Box.createHorizontalStrut(20));
		panel.add(createColorBox(StyleConstants.COLOR_WHITE));
		panel.add(new JLabel("Normal stock"));

		return panel;
	}

	private JPanel createColorBox(Color color) {
		JPanel box = new JPanel();
		box.setPreferredSize(new Dimension(16, 16));
		box.setBackground(color);
		box.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		return box;
	}

	private JButton createButton(String text, String iconPath) {
		return FormUtilities.createIconButton(text, iconPath, 18);
	}

	public void renderMedicineTable(List<Medicine> medicines, int startIndex) {
		tableModel.setRowCount(0);
		for (int i = 0; i < medicines.size(); i++) {
			Medicine m = medicines.get(i);
			tableModel.addRow(new Object[] { startIndex + i + 1, m.getName(), m.getDescription(), m.getUnit(),
					m.getPrice(), m.getMinAge(), m.getMaxAge(), m.getQuantity() });
		}
	}

	public void updatePageInfo(int currentPage, int totalPages) {
		pageInfoLabel.setText("Page " + currentPage + "/" + totalPages);
	}

	

	// ----------------- Helpers -----------------
	public boolean confirmDeletion(Medicine medicine) {
		int choice = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to delete medicine " + medicine.getName() + "?", "Confirm Deletion",
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
	
	

	// --------------- Getter for Controller--------------
	public JTable getMedicineTable() {
		return medicineTable;
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

	public JButton getAddStockButton() {
		return btnAddStock;
	}

	public JButton getAddButton() {
		return btnAdd;
	}

	public JButton getEditButton() {
		return btnEdit;
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
