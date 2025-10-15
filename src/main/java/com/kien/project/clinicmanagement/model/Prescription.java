package com.kien.project.clinicmanagement.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Prescription {
    private Long id;
    private Long medicalResultId;
    private String patientCode;
    private LocalDate prescriptionDate;
    private BigDecimal totalPrice;

    public Prescription() {
    }

    public Prescription(Long id, Long medicalResultId, String patientCode,
                        LocalDate prescriptionDate, BigDecimal totalPrice) {
        this.id = id;
        this.medicalResultId = medicalResultId;
        this.patientCode = patientCode;
        this.prescriptionDate = prescriptionDate;
        this.totalPrice = totalPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMedicalResultId() {
        return medicalResultId;
    }

    public void setMedicalResultId(Long medicalResultId) {
        this.medicalResultId = medicalResultId;
    }

    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
