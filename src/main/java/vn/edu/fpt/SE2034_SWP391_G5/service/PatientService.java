package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateProfileRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.MedicalRecordResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.PatientResponse;

import java.util.List;

public interface PatientService {

    PatientResponse getProfile(Long patientId);

    void updateProfile(Long patientId, UpdateProfileRequest request);

    List<MedicalRecordResponse> getMedicalRecords(Long patientId);

    MedicalRecordResponse getMedicalRecordDetail(Long recordId, Long patientId);
}
