package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentStatusCountResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;
import vn.edu.fpt.SE2034_SWP391_G5.repository.AppointmentRepository;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final MedicalServiceRepository medicalServiceRepository;


//    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
//                                  DoctorScheduleRepository doctorScheduleRepository,
//                                  TimeSlotRepository timeSlotRepository,
//                                  UserRepository userRepository,
//                                  MedicalServiceRepository medicalServiceRepository){
//
//        this.appointmentRepository =  appointmentRepository;
//        this.doctorScheduleRepository = doctorScheduleRepository;
//        this.timeSlotRepository = timeSlotRepository;
//        this.userRepository = userRepository;
//        this.medicalServiceRepository = medicalServiceRepository;
//    }

    public long getAllAppointment(){
        return appointmentRepository.count();
    };

    public Map<String,Long> findTodayAppointmentsByStatus(LocalDate localDate){
        List<AppointmentStatusCountResponse> appointmentStatusCountResponseList = appointmentRepository.findTodayAppointmentsByStatus(localDate);
        Map<String,Long> statusCount = new HashMap<>();
        statusCount.put("WAITING", 0L);
        statusCount.put("CONFIRMED", 0L);
        statusCount.put("EXAMINING", 0L);
        statusCount.put("COMPLETED", 0L);
        statusCount.put("CANCELLED", 0L);
        appointmentStatusCountResponseList.stream().forEach(appointment -> {
            statusCount.put(appointment.getStatus(),appointment.getCount());
        });
            return statusCount;
    };

    // Find all today's appointment
    public List<AppointmentResponse> findAppointmentsByBookingDate(LocalDate today){
       List<Appointment> todayListAppointment = appointmentRepository.findAppointmentsByBookingDate(today);
       List<AppointmentResponse> todayListAppointmentResponse = new ArrayList<>();
       todayListAppointment.forEach(a -> {
          AppointmentResponse appointmentResponse = new AppointmentResponse();
          appointmentResponse.setAppointmentCode(a.getAppointmentCode());
          appointmentResponse.setPatientFullName(a.getPatient().getFirstName() + " "+a.getPatient().getMiddleName()+" " +  a.getPatient().getLastName());
          appointmentResponse.setDoctorFullName(a.getDoctor().getFirstName() + " "+a.getDoctor().getMiddleName()+" " +  a.getDoctor().getLastName());
          appointmentResponse.setServiceName(a.getService().getName());
          appointmentResponse.setSlotStartTime(a.getSlot().getStartTime());
          appointmentResponse.setSlotEndTime(a.getSlot().getEndTime());
          appointmentResponse.setStatus(a.getStatus());
          todayListAppointmentResponse.add(appointmentResponse);
       });
       return todayListAppointmentResponse;
    };



    @Override
    public List<ScheduleSlotResponse> getAvailableSchedules(Long doctorId) {
        List<DoctorSchedule> schedules = doctorScheduleRepository
                .findAvailableSchedulesByDoctorId(doctorId, LocalDate.now());

        LocalDate today = LocalDate.now();
        // Giờ hiện tại để lọc ca trong ngày hôm nay
        // Trước đây không có filter theo giờ → buổi tối vẫn thấy ca sáng cùng ngày
        java.time.LocalTime now = java.time.LocalTime.now();

        return schedules.stream()
                // Lọc ca đã qua trong ngày hôm nay
                .filter(schedule -> {
                    if (!schedule.getWorkDate().isEqual(today)) {
                        return true; // Ngày tương lai → giữ lại tất cả
                    }
                    // Ngày hôm nay: chỉ giữ ca chưa bắt đầu
                    if ("MORNING".equals(schedule.getShift())) {
                        // Ca sáng 07:00–12:00, chỉ hiện nếu chưa tới 12:00
                        return now.isBefore(java.time.LocalTime.of(12, 0));
                    } else {
                        // Ca chiều 13:00–17:00, chỉ hiện nếu chưa tới 17:00
                        return now.isBefore(java.time.LocalTime.of(17, 0));
                    }
                })
                .map(schedule -> {
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

        TimeSlot slot = timeSlotRepository.findByIdWithSchedule(request.getSlotId())
                // Fallback: nếu query trên không tìm thấy vì lý do nào đó
                // TimeSlot slot = timeSlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khung giờ"));

        // Kiểm tra slot còn chỗ
        if (!"AVAILABLE".equals(slot.getStatus()) || slot.getBookedCapacity() >= slot.getMaxCapacity()) {
            throw new BadRequestException("Khung giờ này đã đầy, vui lòng chọn khung giờ khác");
        }

        // Kiểm tra bệnh nhân chưa đặt slot này
        boolean alreadyBooked = appointmentRepository.existsBySlotIdAndPatientIdAndStatusNotIn(
                slot.getId(), patientId, List.of("CANCELLED", "NO_SHOW"));
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
        appointment.setStatus("CONFIRMED");
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment saved = appointmentRepository.save(appointment);
        return toResponse(saved);
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByPatient(Long patientId) {
        // Trước: appointmentRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
        //        → gây LazyInitializationException khi toResponse() truy cập slot.getSchedule()
        return appointmentRepository.findByPatientIdWithDetails(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AppointmentResponse getAppointmentDetail(Long appointmentId, Long patientId) {
        // Trước: appointmentRepository.findById(appointmentId) → LAZY load
        Appointment appointment = appointmentRepository.findByPatientIdWithDetails(patientId)
                .stream()
                .filter(a -> a.getId().equals(appointmentId))
                .findFirst()
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

        if (!List.of("WAITING", "CONFIRMED").contains(appointment.getStatus())) {
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

        return AppointmentResponse.builder()
                .id(a.getId())
                .appointmentCode(a.getAppointmentCode())
                .status(a.getStatus())
                .patientId(a.getPatient() != null ? a.getPatient().getId() : null)
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
}
