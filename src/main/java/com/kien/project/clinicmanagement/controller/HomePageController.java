package com.kien.project.clinicmanagement.controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.kien.project.clinicmanagement.model.User;
import com.kien.project.clinicmanagement.service.UserService;
import com.kien.project.clinicmanagement.utils.FormUtilities;
import com.kien.project.clinicmanagement.utils.Session;
import com.kien.project.clinicmanagement.utils.StyleConstants;
import com.kien.project.clinicmanagement.view.appointment.AppointmentManagementView;
import com.kien.project.clinicmanagement.view.auth.LoginView;
import com.kien.project.clinicmanagement.view.examqueue.ExamQueueManagementView;
import com.kien.project.clinicmanagement.view.homepage.HomePageView;
import com.kien.project.clinicmanagement.view.medicalexaminate.MedicalExaminateView;
import com.kien.project.clinicmanagement.view.medicine.MedicineManagementView;
import com.kien.project.clinicmanagement.view.patient.PatientManagementView;
import com.kien.project.clinicmanagement.view.statistic.DashboardView;
import com.kien.project.clinicmanagement.view.user.UserManagementView;
import com.kien.project.clinicmanagement.view.user.UserProfileView;

public class HomePageController {

    private static final int NAV_ICON_SIZE = 20;
    private static final int AVATAR_SIZE = 180;

    private final HomePageView homePageView;
    private final LoginView loginView;
    private final String role;
    private final List<JButton> navButtons = new ArrayList<>();
    private final UserService userService = new UserService();

    public HomePageController(HomePageView homePageView, LoginView loginView) {
        this.homePageView = homePageView;
        this.loginView = loginView;
        this.role = Session.getCurrentUser().getRole().toLowerCase();

        initNavigation();
        showDefaultPanel();
        homePageView.setVisible(true);
    }

    // Tạo các nút chức năng cho nav
    private void initNavigation() {
        addUserProfileSection();
        addRoleBasedButtons();
        addLogoutButton();
    }
    
    private void showDefaultPanel() {
        switch (role) {
            case "receptionist" -> homePageView.setMainContent(new ExamQueueManagementView());
            case "admin" -> homePageView.setMainContent(new DashboardView());
            default -> homePageView.setMainContent(new MedicalExaminateView());
        }
    }

    /** ======================= NAVIGATION ======================= */
    private void addUserProfileSection() {
        JPanel avatarPanel = createAvatarPanel();
        JButton profileButton = createNavButton(
            "My Profile", "/images/for_button/my_profile.png", this::showUserProfile
        );
        profileButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        avatarPanel.add(profileButton);
        homePageView.addToNavBar(avatarPanel);
        homePageView.addToNavBar(Box.createVerticalStrut(10));
    }

    private JPanel createAvatarPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel avatarLabel = new JLabel(loadUserAvatar());
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        panel.add(avatarLabel);
        return panel;
    }

    private ImageIcon loadUserAvatar() {
        User currentUser = Session.getCurrentUser();
        String avatarPath = currentUser.getProfileImage();

        if (avatarPath != null && !avatarPath.isEmpty()) {
            try {
                ImageIcon rawIcon = new ImageIcon(getClass().getResource(avatarPath));
                Image scaled = rawIcon.getImage().getScaledInstance(AVATAR_SIZE, AVATAR_SIZE, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            } catch (Exception ignored) { }
        }
        return FormUtilities.loadIcon(getClass(), "/images/for_avatar/avt_default.png", AVATAR_SIZE);
    }

    private void addRoleBasedButtons() {
        Map<String, Runnable> actions = getRoleBasedActions();
        actions.forEach((label, action) ->
            createNavButton(label, "/images/for_button/" + toIconFileName(label) + ".png", action)
        );
    }

    private Map<String, Runnable> getRoleBasedActions() {
        Map<String, Runnable> actions = new LinkedHashMap<>();

        switch (role) {
            case "admin" -> {
                actions.put("Dashboard", () -> homePageView.setMainContent(new DashboardView()));
                actions.put("User Management", () -> homePageView.setMainContent(new UserManagementView()));
                actions.put("Patient Management", () -> homePageView.setMainContent(new PatientManagementView()));
                actions.put("Medicine Management", () -> homePageView.setMainContent(new MedicineManagementView()));
                actions.put("Appointment Management", () -> homePageView.setMainContent(new AppointmentManagementView()));
                actions.put("Exam Queue Management", () -> homePageView.setMainContent(new ExamQueueManagementView()));
                actions.put("Medical Examination", () -> homePageView.setMainContent(new MedicalExaminateView()));
            }
            case "doctor" -> {
                actions.put("Medical Examination", () -> homePageView.setMainContent(new MedicalExaminateView()));
                actions.put("Appointment Management", () -> homePageView.setMainContent(new AppointmentManagementView()));
            }
            case "receptionist" -> {
                actions.put("Patient Management", () -> homePageView.setMainContent(new PatientManagementView()));
                actions.put("Appointment Management", () -> homePageView.setMainContent(new AppointmentManagementView()));
                actions.put("Exam Queue Management", () -> homePageView.setMainContent(new ExamQueueManagementView()));
            }
        }

        return actions;
    }

    private void addLogoutButton() {
        createNavButton("Logout", "/images/for_button/logout.png", this::logout);
    }

    private JButton createNavButton(String label, String iconPath, Runnable action) {
        ImageIcon icon = FormUtilities.loadIcon(getClass(), iconPath, NAV_ICON_SIZE);
        JButton button = FormUtilities.createNavButton(label, icon);
        button.setMaximumSize(new Dimension(180, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addActionListener(e -> {
            highlightSelectedButton(button);
            action.run();
        });

        navButtons.add(button);
        homePageView.addToNavBar(button);
        return button;
    }

    private void highlightSelectedButton(JButton selected) {
        for (JButton btn : navButtons) {
            boolean selectedBtn = btn == selected;
            btn.setBackground(selectedBtn ? StyleConstants.COLOR_BLUE_500 : UIManager.getColor("Button.background"));
            btn.setForeground(selectedBtn ? Color.WHITE : UIManager.getColor("Button.foreground"));
        }
    }

    /** ======================= MAIN CONTENT ======================= */



    private void showUserProfile() {
        User currentUser = Session.getCurrentUser();
        User fullUser = userService.getByUserCode(currentUser.getCode());

        if (fullUser == null) {
            homePageView.showError("User not found!");
            return;
        }

        UserProfileView profileView = new UserProfileView((JFrame) SwingUtilities.getWindowAncestor(homePageView), fullUser);
        new UserProfileController(profileView); // Auto-managed controller
        profileView.setVisible(true);
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
            homePageView, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            Session.clearCurrentUser();
            homePageView.dispose();
            loginView.setVisible(true);
        }
    }

    /** ======================= UTILS ======================= */

    private static String toIconFileName(String label) {
        return label.toLowerCase().replaceAll(" ", "_");
    }
}
