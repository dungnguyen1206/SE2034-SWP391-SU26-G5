package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateMedicalRecordRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateMedicalRecordRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.MedicalRecordResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import org.springframework.data.domain.Page;
import java.util.List;

public interface MedicalRecordService {
    void createMedicalRecord(Long appointmentId, CreateMedicalRecordRequest request, Long doctorId);
    void updateMedicalRecord(Long appointmentId, UpdateMedicalRecordRequest request, Long doctorId);
    Page<User> searchPatients(String keyword, int page, int size);
    List<MedicalRecordResponse> getPatientMedicalHistory(Long patientId);
    Page<MedicalRecordResponse> getPatientMedicalHistoryPaginated(Long patientId, String departmentName, int page, int size);

    java.util.Optional<vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalRecord> getMedicalRecordByAppointmentId(Long appointmentId);
    long countPrescriptionsByDoctorAndDate(Long doctorId, java.time.LocalDateTime startOfDay, java.time.LocalDateTime endOfDay);
    void validateMedicalRecordCompleteness(Long appointmentId);
}
