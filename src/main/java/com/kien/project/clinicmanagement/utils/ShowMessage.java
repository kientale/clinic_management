package com.kien.project.clinicmanagement.utils;

import java.awt.Component;
import javax.swing.JOptionPane;

public class ShowMessage {

    public static void showMessage(Component parent, String message, String title, int type) {
        JOptionPane.showMessageDialog(parent, message, title, type);
    }

    public static void showMessage(Component parent, Object message, String title, int type) {
        JOptionPane.showMessageDialog(parent, message, title, type);
    }

    // ===== Các shortcut cho String =====
    public static void showInfo(Component parent, String message) {
        showMessage(parent, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(Component parent, String message) {
        showMessage(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        showMessage(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ===== Các shortcut cho Component/Object =====
    public static void showInfo(Component parent, Object message) {
        showMessage(parent, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(Component parent, Object message) {
        showMessage(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public static void showError(Component parent, Object message) {
        showMessage(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public static boolean showConfirm(Component parent, String message, String title) {
        int choice = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION);
        return choice == JOptionPane.YES_OPTION;
    }
}
