package com.kien.project.clinicmanagement.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Predicate;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

public class FormUtilities {

	// =================================== Utilities cho đăng nhập/ quên mật
	// khẩu===================
	/**
	 * Thêm một component vào panel theo GridBagConstraints Comp sẽ được chia theo
	 * chiều ngang với margin là 10 Được sắp xếp theo x và y
	 */
	public static void addComponent(JPanel panel, Component comp, int x, int y, int width) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(comp, gbc);
	}

	/**
	 * Tạo một nhãn tiêu đề với font chuẩn
	 */
	public static JLabel createTitleLabel(String title) {
		JLabel label = new JLabel(title, SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.BOLD, 20));
		return label;
	}

	public static void addFormRow(JPanel panel, String labelText, Component input, int row) {
		JLabel label = new JLabel(labelText);
		label.setFont(new Font("SansSerif", Font.PLAIN, 14));
		label.setHorizontalAlignment(SwingConstants.RIGHT);

		GridBagConstraints gbcLabel = new GridBagConstraints();
		gbcLabel.gridx = 0;
		gbcLabel.gridy = row;
		gbcLabel.insets = new Insets(8, 10, 8, 5); // Trên-dưới | trái | phải
		gbcLabel.anchor = GridBagConstraints.EAST;

		GridBagConstraints gbcField = new GridBagConstraints();
		gbcField.gridx = 1;
		gbcField.gridy = row;
		gbcField.insets = new Insets(8, 5, 8, 10);
		gbcField.fill = GridBagConstraints.HORIZONTAL;
		gbcField.weightx = 1.0;

		panel.add(label, gbcLabel);
		panel.add(input, gbcField);
	}

	// Tạo các nút có Icon
	public static JButton createIconButton(String text, String iconPath, int iconSize) {
		JButton button = new JButton(text);
		button.setFont(new Font("SansSerif", Font.PLAIN, 13));
		button.setBackground(Color.WHITE);
		button.setFocusPainted(false);
		button.setIcon(loadIcon(FormUtilities.class, iconPath, iconSize));
		return button;
	}
	
	public static JButton createTextButton(String text) {
	    JButton button = new JButton(text);
	    button.setFont(new Font("SansSerif", Font.PLAIN, 13));
	    button.setBackground(Color.WHITE);
	    button.setFocusPainted(false);

	    button.setMargin(new Insets(1, 3, 1, 3)); 

	    return button;
	}

	/**
	 * Tải icon từ đường dẫn và resize về kích thước cố định
	 */
	public static ImageIcon loadIcon(Class<?> clazz, String path, int size) {
		try {
			ImageIcon icon = new ImageIcon(clazz.getResource(path));
			Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
			return new ImageIcon(scaled);
		} catch (Exception e) {
			System.err.println("Icon not found: " + path);
			return null;
		}
	}

	// Hàm kiểm tra rỗng hoặc null
	public static boolean isEmpty(String... values) {
		for (String value : values) {
			if (value == null || value.trim().isEmpty())
				return true;
		}
		return false;
	}

	public static JButton createNavButton(String text, ImageIcon icon) {
		JButton button = new JButton(text, icon);
		button.setAlignmentX(JButton.CENTER_ALIGNMENT);
		button.setMaximumSize(new Dimension(200, 40));
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setIconTextGap(10);
		return button;
	}

	/** Tự động kiểm tra dữ liệu nhập khi rời focus khỏi trường nhập liệu */
	public static void validateOnFocusLost(JTextField field, Predicate<String> condition, String errorMessage) {
		field.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent e) {
				String text = field.getText().trim();
				boolean valid = condition.test(text);
				field.setBackground(valid ? Color.WHITE : new Color(255, 204, 204));
				field.setToolTipText(valid ? null : errorMessage);
			}
		});
	}

	/**
	 * Tạo bảng đã định dạng (header, màu, căn lề)
	 */
	public static JTable createStyledTable(DefaultTableModel model) {
		JTable table = new JTable(model);
		table.setRowHeight(24);
		table.setFont(new Font("SansSerif", Font.PLAIN, 13));
		table.setSelectionBackground(new Color(204, 229, 255));
		table.setShowGrid(true);
		table.setGridColor(Color.LIGHT_GRAY);
		table.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Arial", Font.BOLD, 14));
		header.setBackground(new Color(0, 102, 153));
		header.setForeground(Color.WHITE);
		((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

		DefaultTableCellRenderer leftAlign = new DefaultTableCellRenderer();
		leftAlign.setHorizontalAlignment(SwingConstants.LEFT);

		DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
		centerAlign.setHorizontalAlignment(SwingConstants.CENTER);

		for (int i = 0; i < model.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(i == 0 ? centerAlign : leftAlign);
		}

		return table;
	}

	/**
	 * Tạo panel phân trang (gắn hành động sau ở panel gọi)
	 */
	public static JPanel createPaginationPanel(JButton prevBtn, JLabel pageLabel, JButton nextBtn) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		panel.setOpaque(false);
		panel.add(prevBtn);
		panel.add(pageLabel);
		panel.add(nextBtn);
		return panel;
	}

	// Helper: safely get string from cell
	public static String getCellString(Cell cell) {
		if (cell == null)
			return "";
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue().trim();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				Date date = cell.getDateCellValue();
				return new SimpleDateFormat("yyyy-MM-dd").format(date);
			} else {
				double v = cell.getNumericCellValue();
				if (v == (long) v)
					return String.valueOf((long) v);
				return String.valueOf(v);
			}
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case FORMULA:
			try {
				return cell.getStringCellValue();
			} catch (IllegalStateException e) {
				return String.valueOf(cell.getNumericCellValue());
			}
		case BLANK:
		default:
			return "";
		}
	}
	
	public static LocalDateTime parseLocalDateTimeCell(Cell cell) {
	    if (cell == null || cell.getCellType() == CellType.BLANK) {
	        return null;
	    }

	    try {
	        String cellValue = FormUtilities.getCellString(cell).trim();
	        if (!cellValue.isEmpty()) {
	            return LocalDateTime.parse(cellValue);
	        }
	    } catch (Exception e) {
	        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
	            Date date = cell.getDateCellValue();
	            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	        }
	    }
	    return null;
	}
	
	public static LocalDate parseLocalDateCell(Cell cell) {
	    if (cell == null || cell.getCellType() == CellType.BLANK) {
	        return null;
	    }

	    try {
	        String cellValue = FormUtilities.getCellString(cell).trim();
	        if (!cellValue.isEmpty()) {
	            return LocalDate.parse(cellValue);
	        }
	    } catch (Exception e) {
	        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
	            Date date = cell.getDateCellValue();
	            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	        }
	    }
	    return null;
	}
	
	// Hàm tiện ích để thêm dấu "; " giữa các lỗi
	public static void appendError(StringBuilder sb, String error) {
	    if (sb.length() > 0) sb.append("; ");
	    sb.append(error);
	}
	
	/** Scale icon về kích thước mong muốn */
    public static ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        Image scaledImg = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
    
    public static JButton styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }
}
