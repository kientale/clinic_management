package com.kien.project.clinicmanagement.model;

import java.util.Date;

public class ExamQueue {
    private Long id;
    private String patientCode;
    private String doctorCode;
    private int queueNumber;
    private String status; // WAITING, IN_PROGRESS, DONE, CANCELLED
    private String createdBy;
    private Date createdAt;
    
    private String patientName;
    private String doctorName;
    private String createdByName;
    
    public ExamQueue() {
    }

    public ExamQueue(Long id, String patientCode, String doctorCode, int queueNumber,
                     String status, String createdBy, Date createdAt) {
        this.id = id;
        this.patientCode = patientCode;
        this.doctorCode = doctorCode;
        this.queueNumber = queueNumber;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    // Getter và Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getQueueNumber() {
        return queueNumber;
    }

    public void setQueueNumber(int queueNumber) {
        this.queueNumber = queueNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}
}
