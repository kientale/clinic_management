package com.kien.project.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;

import com.kien.project.clinicmanagement.dao.MedicalResultDAO;
import com.kien.project.clinicmanagement.model.MedicalResult;

public class MedicalResultService {
	private final MedicalResultDAO medicalResultDAO = new MedicalResultDAO();
	
	public void saveMedicalResult(MedicalResult medicalResult) {
        medicalResultDAO.addMedicalResult(medicalResult);
    }
	
	public List<MedicalResult> getResultsByPatientCode(String patientCode) {
	    return medicalResultDAO.getByPatientCode(patientCode);
	}

	public LocalDate getLocalDate() {
	    return LocalDate.now();
	}
	
	public Long generateNewMedicalResultId() {
        Long lastId = medicalResultDAO.getLastMedicalResultId();
        if (lastId == null) {
            return 1L;
        }
        return lastId + 1;
    }
}
