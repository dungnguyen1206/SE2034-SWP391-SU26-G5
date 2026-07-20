package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateMedicalRecordRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UpdateMedicalRecordRequest;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalRecord;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.AppointmentRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalRecordRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.MedicalRecordService;

import java.time.LocalDateTime;

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

        // Check if medical record already exists
        if (medicalRecordRepository.findByAppointmentId(appointmentId).isPresent()) {
            throw new BadRequestException("Hồ sơ bệnh án cho lịch hẹn này đã tồn tại");
        }

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
}
