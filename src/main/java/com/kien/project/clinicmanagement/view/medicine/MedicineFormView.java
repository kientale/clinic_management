package com.kien.project.clinicmanagement.view.medicine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.kien.project.clinicmanagement.model.Medicine;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.ShowMessage;
import com.kien.project.clinicmanagement.utils.StyleConstants;

public class MedicineFormView extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTextField txtName;
    private JTextField txtDescription;
    private JTextField txtUnit;
    private JTextField txtPrice;
    private JTextField txtMinAge;
    private JTextField txtMaxAge;
    private JTextField txtQuantity;

    private JButton btnSave;
    private JButton btnCancel;

    private boolean saved = false;
    private Medicine medicine;

    
    // ----------------- Constructor ---------------
    public MedicineFormView(Frame owner, Medicine medicine) {
        super(owner, true);
        this.medicine = medicine;

        setTitle(medicine == null ? "Add Medicine" : "Edit Medicine");
        setSize(500, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        initComponents();

        if (medicine != null) loadMedicineData();
    }

    private void initComponents() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(buildFormPanel(), BorderLayout.CENTER);
        add(buildFooterPanel(), BorderLayout.SOUTH);
    }
    
    // Hàm tạo Header
 	private JPanel createHeaderPanel() {
 		// Tạo Panel tổng
 		JPanel headerPanel = new JPanel(new BorderLayout());
 		headerPanel.setBackground(StyleConstants.COLOR_BLUE_50);
 		headerPanel.setBorder(new EmptyBorder(10, 20, 0, 20));

 		JLabel title = new JLabel(medicine == null ? "Add New Medicine" : "Edit Medicine", JLabel.CENTER);
        title.setFont(StyleConstants.TITLE_FONT);
        title.setForeground(StyleConstants.TITLE_FG);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

 		headerPanel.add(title, BorderLayout.CENTER);
 		return headerPanel;
 	}

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setBackground(StyleConstants.COLOR_WHITE);
        
        txtName = new JTextField(20);
        txtDescription = new JTextField(20);
        txtUnit = new JTextField(15);
        txtPrice = new JTextField(10);
        txtMinAge = new JTextField(5);
        txtMaxAge = new JTextField(5);
        txtQuantity = new JTextField(5);


        int row = 0;
        FormUtilities.addFormRow(panel, "Name *:", txtName, row++);
        FormUtilities.addFormRow(panel, "Description:", txtDescription, row++);
        FormUtilities.addFormRow(panel, "Unit *:", txtUnit, row++);
        FormUtilities.addFormRow(panel, "Price *:", txtPrice, row++);
        FormUtilities.addFormRow(panel, "Min Age *:", txtMinAge, row++);
        FormUtilities.addFormRow(panel, "Max Age *:", txtMaxAge, row++);
        FormUtilities.addFormRow(panel, "Quantity *:", txtQuantity, row++);

        return panel;
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

    private void loadMedicineData() {
        txtName.setText(medicine.getName());
        txtDescription.setText(medicine.getDescription());
        txtUnit.setText(medicine.getUnit());
        txtPrice.setText(String.valueOf(medicine.getPrice()));
        txtMinAge.setText(String.valueOf(medicine.getMinAge()));
        txtMaxAge.setText(String.valueOf(medicine.getMaxAge()));
        txtQuantity.setText(String.valueOf(medicine.getQuantity()));
    }

    public boolean showFormView() {
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
    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public JTextField getNameField() {
        return txtName;
    }

    public JTextField getDescriptionField() {
        return txtDescription;
    }

    public JTextField getUnitField() {
        return txtUnit;
    }

    public JTextField getPriceField() {
        return txtPrice;
    }

    public JTextField getMinAgeField() {
        return txtMinAge;
    }

    public JTextField getMaxAgeField() {
        return txtMaxAge;
    }

    public JTextField getQuantityField() {
        return txtQuantity;
    }

    public JButton getSaveButton() {
        return btnSave;
    }

    public JButton getCancelButton() {
        return btnCancel;
    }
}
