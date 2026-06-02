package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateAppointmentRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentPrintResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.ScheduleSlotResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.*;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.*;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;
import vn.edu.fpt.SE2034_SWP391_G5.util.CodeGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Override
    public List<AppointmentResponse> getAppointmentListForReceptionist() {
        return appointmentRepository.findAllForReceptionistList()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public long countByStatus(List<AppointmentResponse> appointments, String status) {
        long count = 0;
        for (AppointmentResponse appointment : appointments) {
            if (status.equals(appointment.getStatus())) {
                count++;
            }
        }
        return count;
    }

    @Override
    public List<AppointmentResponse> filterAppointments(List<AppointmentResponse> appointments, String search, String status) {
        List<AppointmentResponse> result = new ArrayList<>();

        String keyword = "";
        if (search != null) {
            keyword = search.trim().toLowerCase();
        }

        String selectedStatus = "";
        if (status != null) {
            selectedStatus = status.trim();
        }

        for (AppointmentResponse appointment : appointments) {
            boolean matchesSearch = true;
            boolean matchesStatus = true;
            if (!keyword.isEmpty()) {
                matchesSearch =
                        containsIgnoreCase(appointment.getAppointmentCode(), keyword)
                                || containsIgnoreCase(appointment.getPatientFullName(), keyword)
                                || containsIgnoreCase(appointment.getPatientPhone(), keyword)
                                || containsIgnoreCase(appointment.getDoctorFullName(), keyword)
                                || containsIgnoreCase(appointment.getDepartmentName(), keyword)
                                || containsIgnoreCase(appointment.getRoomNumber(), keyword);
            }
            if (!selectedStatus.isEmpty()) {
                matchesStatus = selectedStatus.equals(appointment.getStatus());
            }
            if (matchesSearch && matchesStatus) {
                result.add(appointment);
            }
        }

        return result;
    }

    @Override
    public AppointmentPrintResponse getCheckInTicket(Long appointmentId) {
        Appointment appointment = appointmentRepository.findCheckInTicketById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin lịch hẹn"));

        AppointmentPrintResponse ticket = toPrintResponse(appointment);

        if (ticket.getCheckInTime() != null) {
            Long queueNumber = appointmentRepository.countQueueNumberForTicket(
                    ticket.getId(),
                    ticket.getBookingDate(),
                    ticket.getCheckInTime()
            );
            ticket.setQueueNumber(queueNumber);
        }
        return ticket;
    }

    @Override
    @Transactional
    public void confirmCheckInAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch hẹn"));

        if (!"CONFIRMED".equals(appointment.getStatus())) {
            throw new BadRequestException("Chỉ lịch hẹn đã xác nhận mới được check-in");
        }

        if (!LocalDate.now().equals(appointment.getBookingDate())) {
            throw new BadRequestException("Chỉ được check-in lịch hẹn trong ngày khám");
        }

        LocalDateTime now = LocalDateTime.now();

        appointment.setStatus("WAITING");
        appointment.setCheckInTime(now);
        appointment.setUpdatedAt(now);

        appointmentRepository.save(appointment);
    }

    @Override
    public AppointmentResponse getAppointmentDetailForReceptionist(Long appointmentId) {
        Appointment appointment = appointmentRepository.findAppointmentDetailById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch hẹn"));

        AppointmentResponse response = toResponse(appointment);

        response.setPatientAddress(buildPatientAddress(appointment.getPatient()));

        return response;
    }

    private boolean containsIgnoreCase(String value, String keyword){
        if(value == null){
            return false;
        }
        return value.toLowerCase().contains(keyword);
    }

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
        User patient = a.getPatient();
        User doctor = a.getDoctor();

        String patientFullName = patient != null
                ? buildFullName(patient.getLastName(), patient.getMiddleName(), patient.getFirstName())
                : "";

        String doctorFullName = doctor != null
                ? buildFullName(doctor.getLastName(), doctor.getMiddleName(), doctor.getFirstName())
                : "";
        return AppointmentResponse.builder()
                .id(a.getId())
                .appointmentCode(a.getAppointmentCode())
                .status(a.getStatus())
                .patientId(a.getPatient() != null ? a.getPatient().getId() : null)
                .patientFullName(patientFullName)
                .patientPhone(a.getPatient() != null ? a.getPatient().getPhone() : null)
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

    private AppointmentPrintResponse toPrintResponse(Appointment appointment) {
        TimeSlot slot = appointment.getSlot();
        DoctorSchedule schedule = slot != null ? slot.getSchedule() : null;
        User patient = appointment.getPatient();
        User doctor = appointment.getDoctor();
        MedicalService service = appointment.getService();

        String patientFullName = patient != null
                ? buildFullName(patient.getLastName(), patient.getMiddleName(), patient.getFirstName())
                : "";

        String doctorFullName = doctor != null
                ? buildFullName(doctor.getLastName(), doctor.getMiddleName(), doctor.getFirstName())
                : "";

        String departmentName = "";
        if (service != null && service.getDepartment() != null) {
            departmentName = service.getDepartment().getName();
        }

        String roomNumber = "";
        if (schedule != null && schedule.getRoom() != null) {
            roomNumber = schedule.getRoom().getRoomNumber();
        }

        return new AppointmentPrintResponse(
                appointment.getId(),
                appointment.getAppointmentCode(),
                patientFullName,
                patient != null ? patient.getPhone() : "",
                doctorFullName,
                departmentName,
                roomNumber,
                appointment.getBookingDate(),
                slot != null ? slot.getStartTime() : null,
                slot != null ? slot.getEndTime() : null,
                appointment.getCheckInTime(),
                appointment.getStatus()
        );
    }

    private String buildFullName(String lastName, String middleName, String firstName) {
        StringBuilder sb = new StringBuilder();
        if (lastName != null) sb.append(lastName).append(" ");
        if (middleName != null) sb.append(middleName).append(" ");
        if (firstName != null) sb.append(firstName);
        return sb.toString().trim();
    }

    private String buildPatientAddress(User patient) {
        if (patient == null || patient.getAddresses() == null || patient.getAddresses().isEmpty()) {
            return "";
        }

        UserAddress defaultAddress = null;

        for (UserAddress address : patient.getAddresses()) {
            if (Boolean.TRUE.equals(address.getIsDefault())) {
                defaultAddress = address;
                break;
            }
        }

        if (defaultAddress == null) {
            defaultAddress = patient.getAddresses().iterator().next();
        }

        return buildAddress(defaultAddress);
    }

    private String buildAddress(UserAddress address) {
        if (address == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        if (address.getAddressLine() != null && !address.getAddressLine().isBlank()) {
            sb.append(address.getAddressLine().trim());
        }

        if (address.getProvince() != null && address.getProvince().getName() != null
                && !address.getProvince().getName().isBlank()) {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append(address.getProvince().getName().trim());
        }

        return sb.toString();
    }
}