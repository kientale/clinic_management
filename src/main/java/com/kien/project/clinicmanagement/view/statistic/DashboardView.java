package com.kien.project.clinicmanagement.view.statistic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;

import com.kien.project.clinicmanagement.controller.DashboardController;
import com.kien.project.clinicmanagement.utils.StyleConstants;

public class DashboardView extends JPanel {
    private static final long serialVersionUID = 1L;

    private JPanel chartPanel;
    private JPanel footerPanel;
    private DashboardController dashboardController;

    public DashboardView() {
        setLayout(new BorderLayout(10, 10));
        setBackground(StyleConstants.COLOR_WHITE);
        setBorder(new EmptyBorder(0, 10, 10, 10));
        initComponents();

        // inject controller
        dashboardController = new DashboardController(this);
        dashboardController.loadCharts();
    }

    private void initComponents() {
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Chart panel
        chartPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        chartPanel.setBackground(Color.WHITE);
        add(chartPanel, BorderLayout.CENTER);

        // Footer (hiển thị số liệu)
        footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBackground(StyleConstants.COLOR_BLUE_50);
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        add(footerPanel, BorderLayout.SOUTH);
    }

    // --- Hàm tạo Header ---
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(StyleConstants.COLOR_BLUE_50);
        headerPanel.setBorder(new EmptyBorder(10, 20, 0, 20));

        JLabel title = new JLabel("Dashboard", JLabel.CENTER);
        title.setFont(StyleConstants.TITLE_FONT);
        title.setForeground(StyleConstants.COLOR_BLUE_800);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        headerPanel.add(title, BorderLayout.CENTER);
        return headerPanel;
    }

    // --- Hàm để controller thêm chart vào view ---
    public void addChart(JPanel chart) {
        chartPanel.add(chart);
    }

    public void refreshView() {
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    // --- Hàm để controller thêm dữ liệu tóm tắt ---
    public void addSummary(String text) {
        JLabel label = new JLabel(text);
        label.setFont(StyleConstants.LABEL_PLAIN);
        label.setForeground(StyleConstants.COLOR_BLUE_800);
        footerPanel.add(label);
    }

    public void refreshSummary() {
        footerPanel.revalidate();
        footerPanel.repaint();
    }
}
