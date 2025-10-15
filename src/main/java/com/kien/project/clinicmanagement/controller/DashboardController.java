package com.kien.project.clinicmanagement.controller;

import java.math.BigDecimal;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.kien.project.clinicmanagement.service.UserService;
import com.kien.project.clinicmanagement.service.PatientService;
import com.kien.project.clinicmanagement.service.AppointmentService;
import com.kien.project.clinicmanagement.service.MedicineService;
import com.kien.project.clinicmanagement.service.ExamQueueService;
import com.kien.project.clinicmanagement.service.PrescriptionService;
import com.kien.project.clinicmanagement.view.statistic.DashboardView;

public class DashboardController {

    private final UserService userService = new UserService();
    private final PatientService patientService = new PatientService();
    private final AppointmentService appointmentService = new AppointmentService();
    private final MedicineService medicineService = new MedicineService();
    private final ExamQueueService examQueueService = new ExamQueueService();
    private final PrescriptionService prescriptionService = new PrescriptionService();

    private final DashboardView view;

    public DashboardController(DashboardView view) {
        this.view = view;
    }

    /** Load tất cả chart và summary vào view */
    public void loadCharts() {
        // Users
        int maleUsers = userService.countByGender("Male");
        int femaleUsers = userService.countByGender("Female");
        view.addChart(createPieChartPanel("Users by Gender", buildUserDataset(maleUsers, femaleUsers)));
        view.addSummary("Users: Male = " + maleUsers + ", Female = " + femaleUsers);

        // Patients
        int malePatients = patientService.countByGender("Male");
        int femalePatients = patientService.countByGender("Female");
        view.addChart(createPieChartPanel("Patients by Gender", buildPatientDataset(malePatients, femalePatients)));
        view.addSummary("Patients: Male = " + malePatients + ", Female = " + femalePatients);

        // Appointments
        int scheduled = appointmentService.countByStatus("Scheduled");
        int cancelled = appointmentService.countByStatus("Cancelled");
        int checkedIn = appointmentService.countByStatus("Checked-In");
        view.addChart(createPieChartPanel("Appointments", buildAppointmentDataset(scheduled, cancelled, checkedIn)));
        view.addSummary("Appointments: Scheduled = " + scheduled + ", Cancelled = " + cancelled + ", Checked-In = " + checkedIn);

        // Medicines
        int threshold = 50;
        int lowStock = medicineService.countLowStockMedicines(threshold);
        int normalStock = medicineService.countNormalStockMedicines(threshold);
        int totalMedicines = medicineService.getTotalMedicines();
        int totalStock = medicineService.getTotalStock();
        view.addChart(createPieChartPanel("Medicines", buildMedicineDataset(lowStock, normalStock, totalMedicines, totalStock)));
        view.addSummary("Medicines: Low Stock = " + lowStock + ", Normal Stock = " + normalStock +
                ", Total Medicines = " + totalMedicines + ", Total Stock = " + totalStock);

        // Exam Queues
        int waiting = examQueueService.countByStatus("Waiting");
        int done = examQueueService.countByStatus("Done");
        view.addChart(createPieChartPanel("Exam Queues", buildExamQueueDataset(waiting, done)));
        view.addSummary("Exam Queues: Waiting = " + waiting + ", Done = " + done);

        // Revenue
        Map<String, BigDecimal> revenueData = prescriptionService.getRevenueByMonth();
        view.addChart(createBarChartPanel("Revenue by Month", buildRevenueDataset(revenueData)));
        revenueData.forEach((month, revenue) -> 
            view.addSummary("Revenue " + month + " = " + revenue + " VND")
        );

        // refresh giao diện
        view.refreshView();
        view.refreshSummary();
    }

    // ---------------- Dataset builders ----------------

    @SuppressWarnings("unchecked")
	private DefaultPieDataset buildUserDataset(int male, int female) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Male", male);
        dataset.setValue("Female", female);
        return dataset;
    }

    @SuppressWarnings("unchecked")
	private DefaultPieDataset buildPatientDataset(int male, int female) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Male", male);
        dataset.setValue("Female", female);
        return dataset;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private DefaultPieDataset buildAppointmentDataset(int scheduled, int cancelled, int checkedIn) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Scheduled", scheduled);
        dataset.setValue("Cancelled", cancelled);
        dataset.setValue("Checked-In", checkedIn);
        return dataset;
    }

    @SuppressWarnings("unchecked")
	private DefaultPieDataset buildMedicineDataset(int lowStock, int normalStock, int totalMedicines, int totalStock) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Low Stock", lowStock);
        dataset.setValue("Normal Stock", normalStock);
        dataset.setValue("Total Medicines", totalMedicines);
        dataset.setValue("Total Stock", totalStock);
        return dataset;
    }

    @SuppressWarnings("unchecked")
	private DefaultPieDataset buildExamQueueDataset(int waiting, int done) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Waiting", waiting);
        dataset.setValue("Done", done);
        return dataset;
    }

    private DefaultCategoryDataset buildRevenueDataset(Map<String, BigDecimal> revenueData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        revenueData.forEach((month, revenue) -> {
            dataset.addValue(revenue, "Revenue", month);
        });
        return dataset;
    }

    // ---------------- Chart creators ----------------
    private JPanel createPieChartPanel(String title, DefaultPieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
        ChartPanel panel = new ChartPanel(chart);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        return panel;
    }

    private JPanel createBarChartPanel(String title, DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                title, "Month", "Revenue", dataset
        );
        ChartPanel panel = new ChartPanel(chart);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        return panel;
    }
}
