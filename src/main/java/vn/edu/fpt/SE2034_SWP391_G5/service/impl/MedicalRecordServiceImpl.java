package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateMedicalRecordRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateMedicalRecordRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.MedicalRecordResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalRecord;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.AppointmentRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalRecordRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.MedicalRecordService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createMedicalRecord(Long appointmentId, CreateMedicalRecordRequest request, Long doctorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch hẹn với ID: " + appointmentId));

        // Check if doctor matches the appointment
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new BadRequestException("Bạn không có quyền tạo hồ sơ bệnh án cho lịch hẹn này");
        }

        // Check if appointment status is "Đang khám" (EXAMINING or IN_PROGRESS)
        if (!"EXAMINING".equalsIgnoreCase(appointment.getStatus()) && !"IN_PROGRESS".equalsIgnoreCase(appointment.getStatus())) {
            throw new BadRequestException("Chỉ cho phép tạo hồ sơ bệnh án khi trạng thái cuộc hẹn là 'Đang khám'");
        }

        // Check if medical record already exists
        if (medicalRecordRepository.findByAppointmentId(appointmentId).isPresent()) {
            throw new BadRequestException("Hồ sơ bệnh án cho lịch hẹn này đã tồn tại");
        }

        validateBloodPressure(request.getBloodPressure());

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bác sĩ với ID: " + doctorId));

        LocalDateTime now = LocalDateTime.now();

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setAppointment(appointment);
        medicalRecord.setPatient(appointment.getPatient());
        medicalRecord.setDoctor(doctor);
        medicalRecord.setExaminationDate(now);
        medicalRecord.setSymptoms(request.getSymptoms());
        medicalRecord.setDiagnosis(request.getDiagnosis());
        medicalRecord.setConclusion(request.getConclusion());
        medicalRecord.setPrescriptionText(request.getPrescriptionText());
        medicalRecord.setNotes(request.getNotes());
        medicalRecord.setHeartRate(request.getHeartRate());
        medicalRecord.setBloodPressure(request.getBloodPressure());
        medicalRecord.setBloodGlucose(request.getBloodGlucose());
        medicalRecord.setWeight(request.getWeight());
        medicalRecord.setStatus("FINALIZED");
        medicalRecord.setCreatedBy(doctor);
        medicalRecord.setCreatedAt(now);
        medicalRecord.setUpdatedAt(now);

        medicalRecordRepository.save(medicalRecord);
    }

    @Override
    @Transactional
    public void updateMedicalRecord(Long appointmentId, UpdateMedicalRecordRequest request, Long doctorId) {
        MedicalRecord medicalRecord = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ bệnh án cho lịch hẹn này"));

        // Check if doctor matches the appointment
        if (!medicalRecord.getDoctor().getId().equals(doctorId)) {
            throw new BadRequestException("Bạn không có quyền chỉnh sửa hồ sơ bệnh án này");
        }

        validateBloodPressure(request.getBloodPressure());

        LocalDateTime now = LocalDateTime.now();
        medicalRecord.setSymptoms(request.getSymptoms());
        medicalRecord.setDiagnosis(request.getDiagnosis());
        medicalRecord.setConclusion(request.getConclusion());
        medicalRecord.setPrescriptionText(request.getPrescriptionText());
        medicalRecord.setNotes(request.getNotes());
        medicalRecord.setHeartRate(request.getHeartRate());
        medicalRecord.setBloodPressure(request.getBloodPressure());
        medicalRecord.setBloodGlucose(request.getBloodGlucose());
        medicalRecord.setWeight(request.getWeight());
        medicalRecord.setUpdatedAt(now);

        medicalRecordRepository.save(medicalRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> searchPatients(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.searchPatients(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordResponse> getPatientMedicalHistory(Long patientId) {
        return medicalRecordRepository.findByPatientIdWithDetails(patientId)
                .stream()
                .map(this::toRecordResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicalRecordResponse> getPatientMedicalHistoryPaginated(Long patientId, String departmentName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MedicalRecord> recordPage = medicalRecordRepository.findByPatientIdWithDetailsPaginated(patientId, departmentName, pageable);
        return recordPage.map(this::toRecordResponse);
    }

    private MedicalRecordResponse toRecordResponse(MedicalRecord r) {
        User doctor = r.getDoctor();
        String doctorFullName = doctor != null
                ? buildFullName(doctor.getLastName(), doctor.getMiddleName(), doctor.getFirstName())
                : "";
        String appointmentCode = r.getAppointment() != null
                ? r.getAppointment().getAppointmentCode() : null;
        String serviceName = r.getAppointment() != null && r.getAppointment().getService() != null
                ? r.getAppointment().getService().getName() : null;

        List<MedicalRecordResponse.ServiceOrderInfo> serviceOrders = r.getMedicalServiceOrders() != null
                ? r.getMedicalServiceOrders().stream()
                        .map(mso -> MedicalRecordResponse.ServiceOrderInfo.builder()
                                .id(mso.getId())
                                .serviceName(mso.getMedicalService() != null ? mso.getMedicalService().getName() : "")
                                .result(mso.getResult())
                                .status(mso.getStatus())
                                .note(mso.getNote())
                                .price(mso.getPriceReference())
                                .build())
                        .toList()
                : List.of();

        return MedicalRecordResponse.builder()
                .id(r.getId())
                .appointmentId(r.getAppointment() != null ? r.getAppointment().getId() : null)
                .appointmentCode(appointmentCode)
                .doctorFullName(doctorFullName)
                .doctorDegree(doctor != null ? doctor.getDegree() : null)
                .departmentName(doctor != null && doctor.getDepartment() != null
                        ? doctor.getDepartment().getName() : null)
                .serviceName(serviceName)
                .examinationDate(r.getExaminationDate())
                .symptoms(r.getSymptoms())
                .diagnosis(r.getDiagnosis())
                .conclusion(r.getConclusion())
                .prescriptionText(r.getPrescriptionText())
                .notes(r.getNotes())
                .heartRate(r.getHeartRate())
                .bloodPressure(r.getBloodPressure())
                .bloodGlucose(r.getBloodGlucose())
                .weight(r.getWeight())
                .status(r.getStatus())
                .serviceOrders(serviceOrders)
                .build();
    }

    private String buildFullName(String lastName, String middleName, String firstName) {
        StringBuilder sb = new StringBuilder();
        if (lastName != null) sb.append(lastName).append(" ");
        if (middleName != null) sb.append(middleName).append(" ");
        if (firstName != null) sb.append(firstName);
        return sb.toString().trim();
    }

    private void validateBloodPressure(String bloodPressure) {
        if (bloodPressure == null || bloodPressure.trim().isEmpty()) {
            throw new BadRequestException("Không được để trống");
        }

        String bp = bloodPressure.trim();
        if (!bp.matches("^\\d+/\\d+$")) {
            throw new BadRequestException("Định dạng huyết áp sai (Ví dụ: 120/80). Chỉ được chứa số nguyên.");
        }

        String[] parts = bp.split("/");
        try {
            int sys = Integer.parseInt(parts[0]);
            int dia = Integer.parseInt(parts[1]);

            if (sys < 40 || sys > 300) {
                throw new BadRequestException("Huyết áp tâm thu phải trong khoảng từ 40 đến 300 mmHg");
            }
            if (dia < 20 || dia > 200) {
                throw new BadRequestException("Huyết áp tâm trương phải trong khoảng từ 20 đến 200 mmHg");
            }
            if (sys <= dia) {
                throw new BadRequestException("Huyết áp tâm thu phải lớn hơn huyết áp tâm trương");
            }
            if (sys - dia < 20) {
                throw new BadRequestException("Huyết áp tâm thu phải lớn hơn huyết áp tâm trương ít nhất 20 mmHg");
            }
        } catch (NumberFormatException e) {
            throw new BadRequestException("Chỉ số huyết áp phải là số nguyên");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<MedicalRecord> getMedicalRecordByAppointmentId(Long appointmentId) {
        return medicalRecordRepository.findByAppointmentId(appointmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPrescriptionsByDoctorAndDate(Long doctorId, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return medicalRecordRepository.countPrescriptionsByDoctorAndDate(doctorId, startOfDay, endOfDay);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateMedicalRecordCompleteness(Long appointmentId) {
        MedicalRecord medicalRecord = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new BadRequestException("Không thể chuyển trạng thái sang Hoàn thành vì hồ sơ bệnh án chưa được tạo."));

        if (medicalRecord.getSymptoms() == null || medicalRecord.getSymptoms().trim().isEmpty() ||
            medicalRecord.getDiagnosis() == null || medicalRecord.getDiagnosis().trim().isEmpty() ||
            medicalRecord.getBloodPressure() == null || medicalRecord.getBloodPressure().trim().isEmpty() ||
            medicalRecord.getWeight() == null ||
            medicalRecord.getConclusion() == null || medicalRecord.getConclusion().trim().isEmpty() ||
            medicalRecord.getPrescriptionText() == null || medicalRecord.getPrescriptionText().trim().isEmpty() ||
            medicalRecord.getNotes() == null || medicalRecord.getNotes().trim().isEmpty() ||
            medicalRecord.getBloodGlucose() == null ||
            medicalRecord.getHeartRate() == null) {
            throw new BadRequestException("Không thể chuyển trạng thái sang Hoàn thành vì hồ sơ bệnh án chưa đầy đủ thông tin.");
        }
    }
}
