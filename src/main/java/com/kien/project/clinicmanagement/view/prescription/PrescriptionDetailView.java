package com.kien.project.clinicmanagement.view.prescription;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.kien.project.clinicmanagement.model.PrescriptionDetail;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.StyleConstants;

public class PrescriptionDetailView extends JDialog {

	private static final long serialVersionUID = 1L;

	private JTable prescriptionTable;
	private DefaultTableModel prescriptionTableModel;

	private JButton btnClose;

	public PrescriptionDetailView(Window owner, Long prescriptionId) {
		super(owner, "Prescription Detail", ModalityType.APPLICATION_MODAL);
		setSize(700, 500);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout(10, 10));

		initComponents(prescriptionId);
	}

	private void initComponents(Long prescriptionId) {
		add(buildHeader(prescriptionId), BorderLayout.NORTH);
		add(buildTablePanel(), BorderLayout.CENTER);
		add(buildFooterPanel(), BorderLayout.SOUTH);
	}

	private JLabel buildHeader(Long prescriptionId) {
		JLabel title = new JLabel("Prescription Detail", JLabel.CENTER);
		title.setFont(StyleConstants.TITLE_FONT);
		title.setForeground(StyleConstants.TITLE_FG);
		title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		return title;
	}

	private JPanel buildTablePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		panel.setBackground(StyleConstants.TABLE_BG);

		String[] columnNames = {
			"No", "Medicine Id", "Medicine Name", "Dosage",
			"Quantity", "Unit Price", "Total Price", "Usage Instruction"
		};

		prescriptionTableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		prescriptionTable = FormUtilities.createStyledTable(prescriptionTableModel);
		prescriptionTable.getTableHeader().setReorderingAllowed(false);

		panel.add(new JScrollPane(prescriptionTable), BorderLayout.CENTER);
		return panel;
	}

	private JPanel buildFooterPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
		panel.setBackground(StyleConstants.FOOTER_PANEL_BG);

		btnClose = FormUtilities.createIconButton("Close", "/images/for_button/close.png", 18);
		btnClose.addActionListener(e -> dispose());

		panel.add(btnClose);
		return panel;
	}

	// Hàm render dữ liệu chi tiết toa thuốc
	public void renderPrescriptionDetailTable(List<PrescriptionDetail> prescriptionDetails, int startIndex) {
		prescriptionTableModel.setRowCount(0);
		int index = 1;
		for (PrescriptionDetail d : prescriptionDetails) {
			prescriptionTableModel.addRow(new Object[]{
				index++, 
				d.getMedicineCode(),
				d.getMedicineName(),
				d.getDosage(),
				d.getQuantity(),
				d.getUnitPrice(),
				d.getTotalPrice(),
				d.getUsageInstructions()
			});
		}
	}
}
