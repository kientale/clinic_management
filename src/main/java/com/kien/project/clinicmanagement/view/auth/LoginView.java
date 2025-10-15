package com.kien.project.clinicmanagement.view.auth;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.ShowMessage;
import com.kien.project.clinicmanagement.utils.StyleConstants;

public class LoginView extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;
    private JButton btnLogin, btnForgotPassword;
    

    public LoginView() {
        setTitle("Clinic Management Application - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(850, 500);
        setLocationRelativeTo(null);
        setResizable(false);       
        initComponents();
    }
    
    
    private void initComponents() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(StyleConstants.COLOR_BLUE_50);

        JPanel leftPanel = new JPanel();
        leftPanel = createLeftPanel();

        JPanel rightPanel = new JPanel();
        rightPanel = createRightPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(400);
        splitPane.setEnabled(false);
        splitPane.setBorder(null);
        
        splitPane.setDividerSize(0);

        container.add(splitPane, BorderLayout.CENTER);
        getContentPane().add(container);
    }
    
    
    private JPanel createLeftPanel() {
    		JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(StyleConstants.COLOR_WHITE);
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/for_view/KeyHealth.png"));
        Image scaledImg = logoIcon.getImage().getScaledInstance(270, 270, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(scaledImg);

        JLabel logo = new JLabel(logoIcon, JLabel.CENTER);
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(StyleConstants.COLOR_WHITE);
        logoPanel.add(logo, BorderLayout.CENTER);
        leftPanel.add(logoPanel, BorderLayout.CENTER);
        
        return leftPanel;
    }
    
    private JPanel createRightPanel() {
    		JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(StyleConstants.COLOR_BLUE_50);

        JPanel formPanel = createMainPanel();

        rightPanel.add(formPanel, BorderLayout.CENTER);
    		return rightPanel;
    }
    
    private JLabel createHeader() {
        JLabel title = new JLabel("Login", JLabel.CENTER);
        title.setBorder(new EmptyBorder(0, 10, 40, 10));
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(StyleConstants.COLOR_BLUE_700);
        return title;
    }
    

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);

        chkShowPassword = new JCheckBox("Show password");
        chkShowPassword.setOpaque(false);

        int row = 0;
        FormUtilities.addComponent(mainPanel, createHeader(), 0, row++, 2);
        FormUtilities.addFormRow(mainPanel, "Username:", txtUsername, row++);
        FormUtilities.addFormRow(mainPanel, "Password:", txtPassword, row++);
        FormUtilities.addComponent(mainPanel, chkShowPassword, 0, row++, 2);
        FormUtilities.addComponent(mainPanel, createButtonPanel(), 0, row, 2);

        return mainPanel;
    }
    

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);

        btnLogin = FormUtilities.styleButton(
                createButtonNoIcon("Login"),
                StyleConstants.BUTTON_BG,
                StyleConstants.NORMAL_TEXT_COLOR
        );
        
        btnForgotPassword = FormUtilities.styleButton(
                createButtonNoIcon("Forgot Password"),
                StyleConstants.BUTTON_BG,
                StyleConstants.NORMAL_TEXT_COLOR
        );

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnForgotPassword);

        return buttonPanel;
    }
    

    private JButton createButtonNoIcon(String text) {
        JButton button = FormUtilities.createTextButton(text);
        button.setPreferredSize(new Dimension(140, 35));
        return button;
    }
    

    
    public void showValidationMessage(String message) {
        ShowMessage.showWarning(this, message);
    }

    public void showInfoMessage(String message) {
        ShowMessage.showInfo(this, message);
    }

    public void close() {
        this.dispose();
    }
    
    public void showLoginView() {
        this.setVisible(true);
    }

    
    
    public String getUsername() {
        return txtUsername.getText().trim();
    }

    public JTextField getUsernameField() {
        return txtUsername;
    }

    public String getPassword() {
        return String.valueOf(txtPassword.getPassword());
    }
    
    public JPasswordField getPasswordField() {
        return txtPassword;
    }

    public JCheckBox getCheckBoxShowPassword() {
        return chkShowPassword;
    }

    public JButton getLoginButton() {
        return btnLogin;
    }

    public JButton getForgotPasswordButton() {
        return btnForgotPassword;
    }
}
