package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateMedicalRecordRequest;

public interface MedicalRecordService {
    void createMedicalRecord(Long appointmentId, CreateMedicalRecordRequest request, Long doctorId);
    void updateMedicalRecord(Long appointmentId, CreateMedicalRecordRequest request, Long doctorId);
}
