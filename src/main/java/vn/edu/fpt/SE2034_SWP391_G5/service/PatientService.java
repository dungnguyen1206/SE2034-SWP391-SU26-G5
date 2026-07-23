package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateProfileRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateUserRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.MedicalRecordResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.PatientResponse;

import java.util.List;

import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import java.util.List;



public interface PatientService {

    PatientResponse getProfile(Long patientId);

    void updateProfile(Long patientId, UpdateUserRequest request);

    List<MedicalRecordResponse> getMedicalRecords(Long patientId);

    MedicalRecordResponse getMedicalRecordDetail(Long recordId, Long patientId);
    List<User> findUsersByRoleName(String roleName);
    boolean isProfileComplete(Long patientId);
    void updateNullProfileFields(Long patientId, UpdateUserRequest request);
}
