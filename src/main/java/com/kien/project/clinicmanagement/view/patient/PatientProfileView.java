package com.kien.project.clinicmanagement.view.patient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.kien.project.clinicmanagement.model.MedicalResult;
import com.kien.project.clinicmanagement.model.Patient;
import com.kien.project.clinicmanagement.model.Prescription;
import com.kien.project.clinicmanagement.service.MedicalResultService;
import com.kien.project.clinicmanagement.service.PrescriptionService;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.Session;
import com.kien.project.clinicmanagement.utils.ShowMessage;
import com.kien.project.clinicmanagement.utils.StyleConstants;

public class PatientProfileView extends JDialog {

	private static final long serialVersionUID = 1L;

	private Patient patient;
	private JTable prescriptionTable;

	private JTabbedPane tabbedPane;
	private JButton btnClose, btnEditProfile, btnViewDetail;

	
	
	// ----------------- Constructor ---------------
	public PatientProfileView(JFrame owner, Patient patient) {
		super(owner, "Patient Profile - " + patient.getName(), true);
		this.patient = patient;

		setSize(720, 520);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout());
		getContentPane().setBackground(StyleConstants.COLOR_WHITE);

		initComponents();
	}

	private void initComponents() {
		add(buildHeader(), BorderLayout.NORTH);
		add(buildTabbedPane(), BorderLayout.CENTER);
		add(buildFooterPanel(), BorderLayout.SOUTH);

		tabbedPane.addChangeListener(e -> updateFooterButtons());
		updateFooterButtons();
	}

	private JPanel buildHeader() {
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(StyleConstants.COLOR_BLUE_50);
		headerPanel.setBorder(new EmptyBorder(10, 20, 0, 20));
		
		JLabel title = new JLabel("Patient Profile", JLabel.CENTER);
		title.setFont(StyleConstants.TITLE_FONT);
		headerPanel.setBackground(StyleConstants.COLOR_BLUE_50);
		title.setForeground(StyleConstants.COLOR_BLUE_800);
		title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		headerPanel.add(title, BorderLayout.CENTER);
		return headerPanel;
	}

	// Tạo 3 Tap cho phần Patient Profile
	private JTabbedPane buildTabbedPane() {
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Profile", buildProfilePanel());
		tabbedPane.addTab("Medical Records", buildMedicalResultPanel(patient.getCode()));
		tabbedPane.addTab("Prescriptions", buildPrescriptionPanel(patient.getCode()));
		return tabbedPane;
	}

	private JPanel buildProfilePanel() {
		// Tạo Panel chính
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		mainPanel.setBackground(StyleConstants.PANEL_BG);

		// Avatar
		JLabel lblAvatar = new JLabel("", SwingConstants.CENTER);
		lblAvatar.setPreferredSize(new Dimension(200, 220));
		ImageIcon avatarIcon = loadImage(patient.getProfileImage(), 200, 220);
		if (avatarIcon != null) {
			lblAvatar.setIcon(avatarIcon);
		} else {
			lblAvatar.setText("No Image");
			lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
			lblAvatar.setVerticalAlignment(SwingConstants.CENTER);
			lblAvatar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}

		// Right Info
		JPanel rightInfoPanel = new JPanel();
		rightInfoPanel.setLayout(new BoxLayout(rightInfoPanel, BoxLayout.Y_AXIS));
		rightInfoPanel.setOpaque(false);

		rightInfoPanel.add(buildInfoLabel("Code:", patient.getCode()));
		rightInfoPanel.add(buildInfoLabel("Name:", patient.getName()));
		rightInfoPanel.add(buildInfoLabel("Date of birth:",
				patient.getDateOfBirth() != null
						? patient.getDateOfBirth().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
						: ""));
		rightInfoPanel.add(buildInfoLabel("Address:", patient.getAddress()));
		rightInfoPanel.add(buildInfoLabel("Citizen ID:", patient.getCitizenId()));
		rightInfoPanel.add(buildInfoLabel("Created At:",
				patient.getCreatedAt() != null
						? patient.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
						: ""));

		// Top section
		JPanel topPanel = new JPanel(new BorderLayout(20, 0));
		topPanel.setOpaque(false);
		topPanel.add(lblAvatar, BorderLayout.WEST);
		topPanel.add(rightInfoPanel, BorderLayout.CENTER);

		// Bottom section (email + phone)
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		bottomPanel.setOpaque(false);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

		bottomPanel.add(buildInfoLabel("Email:", patient.getEmail()));
		bottomPanel.add(buildInfoLabel("Phone Number:", patient.getPhoneNumber()));

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setOpaque(false);
		centerPanel.add(topPanel, BorderLayout.CENTER);
		centerPanel.add(bottomPanel, BorderLayout.SOUTH);

		mainPanel.add(centerPanel, BorderLayout.CENTER);
		return mainPanel;
	}

	private JPanel buildMedicalResultPanel(String patientCode) {
		JPanel panel = new JPanel(new BorderLayout());

		String[] columns = { "Date", "Doctor", "Symptoms", "Diagnosis", "Treatment Plan" };

		DefaultTableModel model = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Chỉ đọc
			}
		};

		// Tạo bảng chuẩn theo FormUtilities
		JTable table = FormUtilities.createStyledTable(model);
		table.setFillsViewportHeight(true);
		table.getTableHeader().setReorderingAllowed(false);

		// Lấy dữ liệu qua service
		MedicalResultService service = new MedicalResultService();
		List<MedicalResult> results = service.getResultsByPatientCode(patientCode);

		// Format ngày theo yyyy-MM-dd để hiển thị đẹp
		java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

		for (MedicalResult mr : results) {
			model.addRow(
					new Object[] { mr.getExaminationDate() != null ? mr.getExaminationDate().format(dateFormatter) : "",
							mr.getDoctorCode(), mr.getSymptoms(), mr.getDiagnosis(), mr.getTreatmentPlan() });
		}

		JScrollPane scrollPane = new JScrollPane(table);
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	private JPanel buildPrescriptionPanel(String patientCode) {
		JPanel panel = new JPanel(new BorderLayout());

		String[] columns = { "ID", "Medical Result ID", "Date", "Total Price" };

		DefaultTableModel model = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Chỉ đọc
			}
		};

		prescriptionTable = FormUtilities.createStyledTable(model); // gán vào biến toàn cục
		prescriptionTable.setFillsViewportHeight(true);
		prescriptionTable.getTableHeader().setReorderingAllowed(false);

		PrescriptionService service = new PrescriptionService();
		List<Prescription> prescriptions = service.getPrescriptionsByPatientCode(patientCode);

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		for (Prescription p : prescriptions) {
			model.addRow(new Object[] { p.getId(), p.getMedicalResultId(),
					p.getPrescriptionDate() != null ? p.getPrescriptionDate().format(dateFormatter) : "",
					p.getTotalPrice() != null ? p.getTotalPrice().toString() : "" });
		}

		JScrollPane scrollPane = new JScrollPane(prescriptionTable);
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	private JPanel buildFooterPanel() {
		btnEditProfile = FormUtilities.styleButton(
                createButtonNoIcon("Edit Profile"),
                StyleConstants.BUTTON_BG,
                StyleConstants.NORMAL_TEXT_COLOR
        );
		
		btnViewDetail = FormUtilities.styleButton(
                createButtonNoIcon("View Detail"),
                StyleConstants.BUTTON_BG,
                StyleConstants.NORMAL_TEXT_COLOR
        );
		
		btnClose = FormUtilities.styleButton(
                createButtonNoIcon("Close"),
                StyleConstants.BUTTON_BG,
                StyleConstants.NORMAL_TEXT_COLOR
        );
		btnClose.addActionListener(e -> dispose());
		
		String currentRole = Session.getCurrentUser().getRole();
	    if ("Doctor".equalsIgnoreCase(currentRole)) {
	        btnEditProfile.setVisible(false);
	        btnViewDetail.setVisible(false);
	    }

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
		panel.setBackground(StyleConstants.COLOR_BLUE_50);

		panel.add(btnEditProfile);
		panel.add(btnViewDetail);
		panel.add(btnClose);
		return panel;
	}

	private JPanel buildInfoLabel(String label, String value) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.setOpaque(false);

		JLabel lblTitle = new JLabel(label);
		lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));

		JLabel lblValue = new JLabel(value != null ? value : "");
		lblValue.setFont(new Font("SansSerif", Font.PLAIN, 14));

		panel.add(lblTitle);
		panel.add(lblValue);

		return panel;
	}

	private void updateFooterButtons() {
	    int index = tabbedPane.getSelectedIndex();
	    String title = tabbedPane.getTitleAt(index);

	    // Mặc định ẩn hết
	    btnEditProfile.setVisible(false);
	    btnViewDetail.setVisible(false);

	    String currentRole = Session.getCurrentUser().getRole();

	    // Nếu là Doctor thì không cho hiện nút nào cả
	    if ("Doctor".equalsIgnoreCase(currentRole)) {
	        return;
	    }

	    switch (title) {
	        case "Profile":
	            btnEditProfile.setVisible(true);
	            break;
	        case "Prescriptions":
	            btnViewDetail.setVisible(true);
	            break;
	    }
	}

	
	// ----------------- Helpers -----------------
	private ImageIcon loadImage(String path, int width, int height) {
		if (path == null || path.isEmpty())
			return null;

		try {
			File file = new File(path);
			if (file.exists()) {
				ImageIcon icon = new ImageIcon(file.getAbsolutePath());
				return FormUtilities.scaleIcon(icon, width, height);
			}

			URL resource = getClass().getResource(path);
			if (resource != null) {
				ImageIcon icon = new ImageIcon(resource);
				return FormUtilities.scaleIcon(icon, width, height);
			}
		} catch (Exception e) {
			System.err.println("Cannot load image: " + path + " -> " + e.getMessage());
		}

		return null;
	}
	
	private JButton createButtonNoIcon(String text) {
        JButton button = FormUtilities.createTextButton(text);
        button.setPreferredSize(new Dimension(140, 35));
        return button;
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
	
	
	
	// --------------- Getter for Controller--------------
    public Patient getPatient() {
    	return patient;
    }
    
 	public JButton getCloseButton() {
 		return btnClose;
 	}
 	
 	public JButton getEditProfileButton() {
 		return btnEditProfile;
 	}
 	
 	public JButton getViewPrescriptionDetailButton() {
 		return btnViewDetail;
 	}
 	
 	public JTable getPrescriptionTable() {
 		return prescriptionTable;
 	}
}
