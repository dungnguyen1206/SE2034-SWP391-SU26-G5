package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateAppointmentRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleSlotResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;
import vn.edu.fpt.SE2034_SWP391_G5.entity.DoctorSchedule;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalService;
import vn.edu.fpt.SE2034_SWP391_G5.entity.TimeSlot;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.AppointmentRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.DoctorScheduleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalServiceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.TimeSlotRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;
import vn.edu.fpt.SE2034_SWP391_G5.util.CodeGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final MedicalServiceRepository medicalServiceRepository;

    @Override
    public List<ScheduleSlotResponse> getAvailableSchedules(Long doctorId) {
        List<DoctorSchedule> schedules = doctorScheduleRepository
                .findAvailableSchedulesByDoctorId(doctorId, LocalDate.now());

        return schedules.stream().map(schedule -> {
            List<TimeSlot> slots = timeSlotRepository
                    .findByScheduleIdOrderByStartTimeAsc(schedule.getId());

            List<ScheduleSlotResponse.SlotInfo> slotInfos = slots.stream()
                    .map(slot -> ScheduleSlotResponse.SlotInfo.builder()
                            .slotId(slot.getId())
                            .startTime(slot.getStartTime())
                            .endTime(slot.getEndTime())
                            .bookedCapacity(slot.getBookedCapacity())
                            .maxCapacity(slot.getMaxCapacity())
                            .status(slot.getStatus())
                            .available("AVAILABLE".equals(slot.getStatus())
                                    && slot.getBookedCapacity() < slot.getMaxCapacity())
                            .build())
                    .toList();

            String shiftLabel = "MORNING".equals(schedule.getShift()) ? "Ca sáng" : "Ca chiều";

            return ScheduleSlotResponse.builder()
                    .scheduleId(schedule.getId())
                    .workDate(schedule.getWorkDate())
                    .shift(schedule.getShift())
                    .shiftLabel(shiftLabel)
                    .roomNumber(schedule.getRoom() != null ? schedule.getRoom().getRoomNumber() : "")
                    .slots(slotInfos)
                    .build();
        }).toList();
    }

    @Override
    @Transactional
    public AppointmentResponse bookAppointment(Long patientId, CreateAppointmentRequest request) {
        // Lấy các entity cần thiết
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bệnh nhân"));

        User doctor = userRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bác sĩ"));

        MedicalService service = medicalServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ"));

        TimeSlot slot = timeSlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khung giờ"));

        // Kiểm tra slot còn chỗ
        if (!"AVAILABLE".equals(slot.getStatus()) || slot.getBookedCapacity() >= slot.getMaxCapacity()) {
            throw new BadRequestException("Khung giờ này đã đầy, vui lòng chọn khung giờ khác");
        }

        // Kiểm tra bệnh nhân chưa đặt slot này
        boolean alreadyBooked = appointmentRepository.existsBySlotIdAndPatientIdAndStatusNotIn(
                slot.getId(), patientId, List.of("CANCELLED", "REJECTED", "NO_SHOW"));
        if (alreadyBooked) {
            throw new BadRequestException("Bạn đã đặt lịch trong khung giờ này rồi");
        }

        // Tăng booked_capacity
        slot.setBookedCapacity(slot.getBookedCapacity() + 1);
        if (slot.getBookedCapacity() >= slot.getMaxCapacity()) {
            slot.setStatus("FULL");
        }
        timeSlotRepository.save(slot);

        // Tạo appointment
        Appointment appointment = new Appointment();
        appointment.setAppointmentCode(CodeGenerator.generateAppointmentCode());
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setService(service);
        appointment.setSlot(slot);
        appointment.setBookingDate(slot.getSchedule().getWorkDate());
        appointment.setNote(request.getNote());
        appointment.setStatus("PENDING");
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment saved = appointmentRepository.save(appointment);
        return toResponse(saved);
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AppointmentResponse getAppointmentDetail(Long appointmentId, Long patientId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch hẹn"));

        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new BadRequestException("Bạn không có quyền xem lịch hẹn này");
        }
        return toResponse(appointment);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long appointmentId, Long patientId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch hẹn"));

        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new BadRequestException("Bạn không có quyền hủy lịch hẹn này");
        }

        if (!List.of("PENDING", "CONFIRMED").contains(appointment.getStatus())) {
            throw new BadRequestException("Không thể hủy lịch hẹn ở trạng thái: " + appointment.getStatus());
        }

        // Giảm booked_capacity
        TimeSlot slot = appointment.getSlot();
        if (slot.getBookedCapacity() > 0) {
            slot.setBookedCapacity(slot.getBookedCapacity() - 1);
            slot.setStatus("AVAILABLE");
            timeSlotRepository.save(slot);
        }

        appointment.setStatus("CANCELLED");
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);
    }

    // ---- Helper ----
    private AppointmentResponse toResponse(Appointment a) {
        TimeSlot slot = a.getSlot();
        DoctorSchedule schedule = slot != null ? slot.getSchedule() : null;
        User doctor = a.getDoctor();
        String doctorFullName = doctor != null
                ? buildFullName(doctor.getLastName(), doctor.getMiddleName(), doctor.getFirstName())
                : "";

        User patient = a.getPatient();
        String patientFullName = patient != null
                ? buildFullName(patient.getLastName(), patient.getMiddleName(), patient.getFirstName())
                : "";

        //linhNH 01/06/2026
        // Tính tuổi bệnh nhân từ ngày sinh
        Integer patientAge = null;
        if (patient != null && patient.getDateOfBirth() != null) {
            patientAge = Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears();
        }
        String patientGender = patient != null ? patient.getGender() : null;

        // Tạo chữ cái viết tắt (Initials) cho Avatar bệnh nhân
        String patientInitials = "";
        if (patientFullName != null && !patientFullName.trim().isEmpty()) {
            String[] parts = patientFullName.trim().split("\\s+");
            if (parts.length > 0) {
                patientInitials += parts[0].substring(0, 1).toUpperCase(); // Chữ cái đầu của Họ
            }
            if (parts.length > 1) {
                patientInitials += parts[parts.length - 1].substring(0, 1).toUpperCase(); // Chữ cái đầu của Tên
            }
        }

        return AppointmentResponse.builder()
                .id(a.getId())
                .appointmentCode(a.getAppointmentCode())
                .status(a.getStatus())
                .patientId(patient != null ? patient.getId() : null)
                .patientFullName(patientFullName)
                .patientAge(patientAge)
                .patientGender(patientGender)
                .patientInitials(patientInitials)
                .doctorId(doctor != null ? doctor.getId() : null)
                .doctorFullName(doctorFullName)
                .doctorDegree(doctor != null ? doctor.getDegree() : null)
                .departmentName(doctor != null && doctor.getDepartment() != null
                        ? doctor.getDepartment().getName() : null)
                .serviceId(a.getService() != null ? a.getService().getId() : null)
                .serviceName(a.getService() != null ? a.getService().getName() : null)
                .bookingDate(a.getBookingDate())
                .shift(schedule != null ? schedule.getShift() : null)
                .slotStartTime(slot != null ? slot.getStartTime() : null)
                .slotEndTime(slot != null ? slot.getEndTime() : null)
                .roomNumber(schedule != null && schedule.getRoom() != null
                        ? schedule.getRoom().getRoomNumber() : null)
                .note(a.getNote())
                .createdAt(a.getCreatedAt())
                .hasMedicalRecord(a.getMedicalRecord() != null)
                .build();
    }

    private String buildFullName(String lastName, String middleName, String firstName) {
        StringBuilder sb = new StringBuilder();
        if (lastName != null) sb.append(lastName).append(" ");
        if (middleName != null) sb.append(middleName).append(" ");
        if (firstName != null) sb.append(firstName);
        return sb.toString().trim();
    }

    @Override
    public Page<AppointmentResponse> getAppointmentsForDoctor(Long doctorId, String status, Pageable pageable) {
        List<String> statuses;
        if (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status)) {
            statuses = List.of("WAITING", "IN_PROGRESS", "COMPLETED");
        } else {
            statuses = List.of(status.toUpperCase());
        }
        return appointmentRepository.findByDoctorIdAndStatusIn(doctorId, statuses, pageable)
                .map(this::toResponse);
    }

    @Override
    public long countAppointmentsForDoctor(Long doctorId, String status) {
        if (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status)) {
            return appointmentRepository.countByDoctorIdAndStatusIn(doctorId, List.of("WAITING", "IN_PROGRESS", "COMPLETED"));
        }
        return appointmentRepository.countByDoctorIdAndStatus(doctorId, status.toUpperCase());
    }

    @Override
    @Transactional
    public void updateAppointmentStatus(Long appointmentId, String newStatus) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch hẹn với ID: " + appointmentId));
        appointment.setStatus(newStatus.toUpperCase());
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);
    }
}
