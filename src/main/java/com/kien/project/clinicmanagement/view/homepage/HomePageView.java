package com.kien.project.clinicmanagement.view.homepage;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.kien.project.clinicmanagement.model.User;
import com.kien.project.clinicmanagement.utils.ShowMessage;
import com.kien.project.clinicmanagement.utils.StyleConstants;

public class HomePageView extends JFrame { 
	private static final long serialVersionUID = 1L;

	private String role;
	private JPanel navBarPanel;
	private JPanel contentPanel;
	private boolean navBarVisible = true;
	private JButton toggleButton;

	
	public HomePageView(User user) {
		this.role = user.getRole();
		setTitle("Clinic Management Application - " + role);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		initComponents();
	}


	private void initComponents() {
	    JPanel background = new JPanel(new BorderLayout());
	    background.setBackground(StyleConstants.CONTENT_BG);

	    background.add(createHeaderPanel(), BorderLayout.NORTH);
	    navBarPanel = createNavBar();
	    contentPanel = createContentPanel();
	    background.add(createCenterPanel(), BorderLayout.CENTER);
	    background.add(createFooterLabel(), BorderLayout.SOUTH);

	    setContentPane(background);
	}
	
	
	private JPanel createCenterPanel() {
	    JPanel centerPanel = new JPanel(new BorderLayout());
	    centerPanel.setOpaque(false);

	    // Panel chứa navBar + nút toggle
	    JPanel navContainer = new JPanel(new BorderLayout());
	    navContainer.setBackground(StyleConstants.COLOR_BLUE_100);

	    // Toggle button (nằm giữa chiều cao nav bar)
	    toggleButton = new JButton("❮"); // icon mũi tên
	    toggleButton.setFocusPainted(false);
	    toggleButton.setBorderPainted(false);
	    toggleButton.setContentAreaFilled(false);
	    toggleButton.addActionListener(e -> toggleNavBar());

	    navContainer.add(navBarPanel, BorderLayout.CENTER);
	    navContainer.add(toggleButton, BorderLayout.EAST);

	    centerPanel.add(navContainer, BorderLayout.WEST);
	    centerPanel.add(contentPanel, BorderLayout.CENTER);

	    return centerPanel;
	}

	private void toggleNavBar() {
	    navBarVisible = !navBarVisible;
	    navBarPanel.setVisible(navBarVisible);

	    // Đổi icon nút
	    toggleButton.setText(navBarVisible ? "❮" : "❯");

	    // Cập nhật lại giao diện
	    navBarPanel.getParent().revalidate();
	}

	public void addToNavBar(Component comp) {
		navBarPanel.add(Box.createVerticalStrut(10));
		navBarPanel.add(comp);
		navBarPanel.revalidate();
		navBarPanel.repaint();
	}

	public void setMainContent(JPanel panel) {
		contentPanel.removeAll();
		contentPanel.add(panel, "content");
		((CardLayout) contentPanel.getLayout()).show(contentPanel, "content");
		contentPanel.revalidate();
		contentPanel.repaint();
	}

	public String getRole() {
		return role;
	}

	private JPanel createHeaderPanel() {
	    JPanel panel = new JPanel() {
	        private static final long serialVersionUID = 1L;
	        private final Image image = new ImageIcon(
	            getClass().getResource("/images/for_view/KeyHealth3.png")
	        ).getImage();

	        @Override
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
	        }
	    };
	    panel.setPreferredSize(new Dimension(0, 100));

	    return panel;
	}

	private JLabel createFooterLabel() {
		JLabel label = new JLabel("Clinic Management Application - Developed by Trần Lê Hoàng Kiên",
				SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.ITALIC, 12));
		label.setOpaque(true);
		label.setBackground(StyleConstants.COLOR_WHITE);
		label.setForeground(StyleConstants.COLOR_BLACK);
		label.setBorder(new EmptyBorder(5, 0, 5, 0));
		return label;
	}

	private JPanel createNavBar() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(StyleConstants.COLOR_BLUE_100);
		panel.setPreferredSize(new Dimension(200, 0));
		return panel;
	}

	private JPanel createContentPanel() {
		JPanel panel = new JPanel(new CardLayout());
		panel.setBackground(StyleConstants.CONTENT_BG);
		return panel;
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
}
