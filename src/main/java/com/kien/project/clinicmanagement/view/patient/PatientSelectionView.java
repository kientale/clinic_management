package com.kien.project.clinicmanagement.view.patient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
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

import com.kien.project.clinicmanagement.model.Patient;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.ShowMessage;
import com.kien.project.clinicmanagement.utils.StyleConstants;

public class PatientSelectionView extends JDialog {

    private static final long serialVersionUID = 1L;

    private DefaultTableModel tableModel;
    private JTable patientTable;
    private JTextField searchField;
    private JComboBox<String> searchCombo;
    private JLabel pageInfoLabel;
    
    private JButton btnSearch, btnRefresh, btnPrevPage, btnNextPage, btnSelect, btnAddNewPatient;

    public PatientSelectionView(Frame owner, boolean modal) {
        super(owner, "Select Patient", modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
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

        JLabel title = new JLabel("Patient Selection", JLabel.CENTER);
        title.setFont(StyleConstants.TITLE_FONT);
        title.setForeground(StyleConstants.COLOR_BLUE_800);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);
        searchField = new JTextField(18);
        searchCombo = new JComboBox<>(new String[] {
            "Search by name", "Search by phone number", "Search by citizen id"
        });
        
        btnSearch = FormUtilities.styleButton(createButton("Search", "/images/for_button/search.png"),new Color(66, 165, 245),Color.WHITE);
		btnRefresh = FormUtilities.styleButton(createButton("Refresh", "/images/for_button/refresh.png"),new Color(38, 166, 154),Color.WHITE);
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchCombo);
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
        String[] columnNames = new String[] { "No", "Name", "Email", "Phone", "Citizen ID" };
        // Khởi tạo bảng và model
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Chỉ đọc
            }
        };
        patientTable = FormUtilities.createStyledTable(tableModel);
        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(StyleConstants.FOOTER_PANEL_BG);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JPanel crudPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        crudPanel.setOpaque(false);
        btnSelect = FormUtilities.styleButton(createButton("Select", "/images/for_button/select.png"),new Color(102, 187, 106), Color.WHITE);
        btnAddNewPatient = FormUtilities.styleButton(
				createButton("Add New Patient", "/images/for_button/add.png"), StyleConstants.BUTTON_BG,
				Color.BLACK);
        crudPanel.add(btnSelect);
        crudPanel.add(btnAddNewPatient);

        pageInfoLabel = new JLabel("Page 1/1");
        btnPrevPage = FormUtilities.styleButton(createButton("Prev", "/images/for_button/previous.png"),new Color(189, 189, 189),Color.BLACK);
		btnNextPage = FormUtilities.styleButton(createButton("Next", "/images/for_button/next.png"),new Color(189, 189, 189),Color.BLACK);
        JPanel paginationPanel = FormUtilities.createPaginationPanel(btnPrevPage, pageInfoLabel, btnNextPage);
        
        panel.add(crudPanel, BorderLayout.WEST);
        panel.add(paginationPanel, BorderLayout.EAST);

        return panel;
    }


    private JButton createButton(String text, String iconPath) {
        return FormUtilities.createIconButton(text, iconPath, 16);
    }
    
    public void renderPatientTable(List<Patient> patients, int startIndex) {
        tableModel.setRowCount(0);
        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            tableModel.addRow(new Object[] {
                startIndex + i + 1,
                p.getName(),
                p.getEmail(),
                p.getPhoneNumber(),
                p.getCitizenId()
            });
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

    
    // Getter cho controller
    public JTable getPatientTable() {
        return patientTable;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JComboBox<String> getSearchCombo() {
        return searchCombo;
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
    
    public JButton getAddNewPatientButton() {
        return btnAddNewPatient;
    }
}
