package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateMedicalRecordRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateMedicalRecordRequest;

public interface MedicalRecordService {
    void createMedicalRecord(Long appointmentId, CreateMedicalRecordRequest request, Long doctorId);
    void updateMedicalRecord(Long appointmentId, UpdateMedicalRecordRequest request, Long doctorId);
}
