package com.kien.project.clinicmanagement.view.prescription;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.kien.project.clinicmanagement.controller.MedicalExaminateController;
import com.kien.project.clinicmanagement.model.PrescriptionDetail;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.ShowMessage;
import com.kien.project.clinicmanagement.utils.StyleConstants;

public class PrescriptionDetailFormView extends JDialog {

	private static final long serialVersionUID = 1L;

	private JTextField txtMedicineCode;
	private JTextField txtMedicineName;
	private JTextField txtDosage;
	private JTextField txtQuantity;
	private JTextField txtUnitPrice;
	private JTextArea txtUsageInstructions;
	private JScrollPane usageScroll;

	private JButton btnSave;
	private JButton btnCancel;
	private JButton btnSelectMedicine;

	private boolean saved = false;
	private PrescriptionDetail prescriptionDetail;

	public PrescriptionDetailFormView(Frame owner, PrescriptionDetail detail, MedicalExaminateController controller) {
		super(owner, true);
		this.prescriptionDetail = detail;

		setTitle(detail == null ? "Add Prescription Detail" : "Edit Prescription Detail");
		setSize(500, 520);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout());

		initComponents();

		if (detail != null)
			loadPrescriptionDetailData();
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

		JLabel title = new JLabel(
				prescriptionDetail == null ? "Add New Prescription Detail" : "Edit Prescription Detail", JLabel.CENTER);
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

		txtMedicineCode = new JTextField(15);
		txtMedicineName = new JTextField(20);
		txtDosage = new JTextField(20);
		txtQuantity = new JTextField(10);
		txtUnitPrice = new JTextField(15);

		txtMedicineName.setEnabled(false);
		txtUnitPrice.setEnabled(false);

		txtUsageInstructions = new JTextArea(4, 20);
		txtUsageInstructions.setLineWrap(true);
		txtUsageInstructions.setWrapStyleWord(true);
		usageScroll = new JScrollPane(txtUsageInstructions);

		btnSelectMedicine = new JButton("Select Medicine");
		btnSelectMedicine.setMargin(new Insets(3, 8, 3, 8));

		int row = 0;

		// Medicine code + select button
		JPanel medicineWrapper = new JPanel(new BorderLayout(6, 0));
		medicineWrapper.setBackground(StyleConstants.TABLE_BG);
		medicineWrapper.add(txtMedicineCode, BorderLayout.CENTER);
		medicineWrapper.add(btnSelectMedicine, BorderLayout.EAST);
		FormUtilities.addFormRow(panel, "Medicine Code*:", medicineWrapper, row++);

		FormUtilities.addFormRow(panel, "Medicine Name:", txtMedicineName, row++);
		FormUtilities.addFormRow(panel, "Dosage:", txtDosage, row++);
		FormUtilities.addFormRow(panel, "Quantity*:", txtQuantity, row++);
		FormUtilities.addFormRow(panel, "Unit Price:", txtUnitPrice, row++);
		FormUtilities.addFormRow(panel, "Usage Instructions:", usageScroll, row++);

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
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
		buttonPanel.setOpaque(false);

		btnSave = FormUtilities.styleButton(createButtonNoIcon("Save"), StyleConstants.BUTTON_BG,
				StyleConstants.NORMAL_TEXT_COLOR);

		btnCancel = FormUtilities.styleButton(createButtonNoIcon("Cancel"), StyleConstants.BUTTON_BG,
				StyleConstants.NORMAL_TEXT_COLOR);

		buttonPanel.add(btnSave);
		buttonPanel.add(btnCancel);
		
		footerPanel.add(noteLabel, BorderLayout.NORTH);
		footerPanel.add(buttonPanel, BorderLayout.SOUTH);
		return footerPanel;
	}

	private JButton createButtonNoIcon(String text) {
		JButton button = FormUtilities.createTextButton(text);
		button.setPreferredSize(new Dimension(140, 35));
		return button;
	}

	private void loadPrescriptionDetailData() {
		txtMedicineCode.setText(prescriptionDetail.getMedicineCode());
		txtMedicineName.setText(prescriptionDetail.getMedicineName());
		txtDosage.setText(prescriptionDetail.getDosage());
		txtQuantity.setText(String.valueOf(prescriptionDetail.getQuantity()));
		if (prescriptionDetail.getUnitPrice() != null) {
			txtUnitPrice.setText(prescriptionDetail.getUnitPrice().toString());
		}
		txtUsageInstructions.setText(prescriptionDetail.getUsageInstructions());
	}

	public void fillMedicineData(com.kien.project.clinicmanagement.model.Medicine medicine) {
		if (medicine != null) {
			txtMedicineCode.setText(medicine.getCode());
			txtMedicineName.setText(medicine.getName());
			if (medicine.getPrice() != null) {
				txtUnitPrice.setText(medicine.getPrice().toString());
			} else {
				txtUnitPrice.setText("");
			}
		}
	}

	public PrescriptionDetail collectPrescriptionDetail() {
		PrescriptionDetail detail = prescriptionDetail != null ? prescriptionDetail : new PrescriptionDetail();
		detail.setMedicineCode(txtMedicineCode.getText().trim());
		detail.setMedicineName(txtMedicineName.getText().trim());
		detail.setDosage(txtDosage.getText().trim());

		try {
			detail.setQuantity(Integer.parseInt(txtQuantity.getText().trim()));
		} catch (NumberFormatException e) {
			detail.setQuantity(0);
		}

		try {
			if (!txtUnitPrice.getText().trim().isEmpty()) {
				detail.setUnitPrice(new java.math.BigDecimal(txtUnitPrice.getText().trim()));
			}
		} catch (NumberFormatException e) {
			detail.setUnitPrice(java.math.BigDecimal.ZERO);
		}

		// TotalPrice = Quantity * UnitPrice
		if (detail.getUnitPrice() != null && detail.getQuantity() != null) {
			detail.setTotalPrice(detail.getUnitPrice().multiply(new java.math.BigDecimal(detail.getQuantity())));
		}

		detail.setUsageInstructions(txtUsageInstructions.getText().trim());

		return detail;
	}

	public boolean showForm() {
		setVisible(true);
		return isSaved();
	}

	public void markSaved(boolean saved) {
		this.saved = saved;
	}

	public boolean isSaved() {
		return saved;
	}

	// ================== Thông báo ==================
	public void showInfo(String message) {
		ShowMessage.showInfo(this, message);
	}

	public void showError(String message) {
		ShowMessage.showError(this, message);
	}

	public void showWarning(String message) {
		ShowMessage.showWarning(this, message);
	}

	// ================== Getter cho Controller ==================
	public PrescriptionDetail getPrescriptionDetail() {
		return prescriptionDetail;
	}

	public void setPrescriptionDetail(PrescriptionDetail prescriptionDetail) {
		this.prescriptionDetail = prescriptionDetail;
	}

	public JTextField getTxtMedicineCode() {
		return txtMedicineCode;
	}

	public JTextField getTxtMedicineName() {
		return txtMedicineName;
	}

	public JTextField getTxtDosage() {
		return txtDosage;
	}

	public JTextField getTxtQuantity() {
		return txtQuantity;
	}

	public JTextField getTxtUnitPrice() {
		return txtUnitPrice;
	}

	public JTextArea getTxtUsageInstructions() {
		return txtUsageInstructions;
	}

	public JButton getSaveButton() {
		return btnSave;
	}

	public JButton getCancelButton() {
		return btnCancel;
	}

	public JButton getBtnSelectMedicine() {
		return btnSelectMedicine;
	}

	public String getMedicineCodeInput() {
		return txtMedicineCode.getText().trim();
	}

	public String getMedicineNameInput() {
		return txtMedicineName.getText().trim();
	}

	public String getDosageInput() {
		return txtDosage.getText().trim();
	}

	public String getQuantityInput() {
		return txtQuantity.getText().trim();
	}

	public String getUnitPriceInput() {
		return txtUnitPrice.getText().trim();
	}

	public String getUsageInstructionsInput() {
		return txtUsageInstructions.getText().trim();
	}
}
