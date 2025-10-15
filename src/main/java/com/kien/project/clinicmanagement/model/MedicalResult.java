package com.kien.project.clinicmanagement.model;

import java.time.LocalDate;

public class MedicalResult {
	private Long id;
	private Long examQueueId;
	private String patientCode;
	private String doctorCode;
	private LocalDate examinationDate;
	private String symptoms;
	private String diagnosis;
	private String treatmentPlan;

	public MedicalResult() {
	}

	public MedicalResult(Long id, Long examQueueId, String patientCode, String doctorCode, LocalDate examinationDate,
			String symptoms, String diagnosis, String treatmentPlan) {
		this.id = id;
		this.examQueueId = examQueueId;
		this.patientCode = patientCode;
		this.doctorCode = doctorCode;
		this.examinationDate = examinationDate;
		this.symptoms = symptoms;
		this.diagnosis = diagnosis;
		this.treatmentPlan = treatmentPlan;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getExamQueueId() {
		return examQueueId;
	}

	public void setExamQueueId(Long examQueueId) {
		this.examQueueId = examQueueId;
	}

	public String getPatientCode() {
		return patientCode;
	}

	public void setPatientCode(String patientCode) {
		this.patientCode = patientCode;
	}

	public String getDoctorCode() {
		return doctorCode;
	}

	public void setDoctorCode(String doctorCode) {
		this.doctorCode = doctorCode;
	}

	public LocalDate getExaminationDate() {
		return examinationDate;
	}

	public void setExaminationDate(LocalDate examinationDate) {
		this.examinationDate = examinationDate;
	}

	public String getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(String symptoms) {
		this.symptoms = symptoms;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getTreatmentPlan() {
		return treatmentPlan;
	}

	public void setTreatmentPlan(String treatmentPlan) {
		this.treatmentPlan = treatmentPlan;
	}
}
