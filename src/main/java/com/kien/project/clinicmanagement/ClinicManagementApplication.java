package com.kien.project.clinicmanagement;

import java.awt.EventQueue;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;
import com.kien.project.clinicmanagement.controller.AuthController;

public class ClinicManagementApplication {

    public static void main(String[] args) {
        setLookAndFeel();

        // Đảm bảo UI khởi động trên Event Dispatch Thread: Luồng chuyên xử lý UI trong Swing
        EventQueue.invokeLater(ClinicManagementApplication::launchApp);
    }

    private static void launchApp() {
    		// Tạo Controller và khởi tạo View login
        AuthController authController = new AuthController(); 
        authController.showLoginView();
    }

    // Look and Feel FlatLightLaf
    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("❌ Unable to apply FlatLaf look and feel: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
