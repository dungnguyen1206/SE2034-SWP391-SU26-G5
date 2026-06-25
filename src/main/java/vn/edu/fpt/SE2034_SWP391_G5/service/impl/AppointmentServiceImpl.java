package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateAppointmentRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.*;
import vn.edu.fpt.SE2034_SWP391_G5.entity.*;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.*;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;
import vn.edu.fpt.SE2034_SWP391_G5.util.CodeGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final MedicalServiceRepository medicalServiceRepository;

    @Override
    public Page<AppointmentResponse> getAppointmentListForReceptionist(LocalDate fromDate, LocalDate toDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Appointment> appointmentPage = appointmentRepository.findAppointmentListForReceptionistList(fromDate, toDate, pageable);
        return appointmentPage.map(this::toAppointmentListResponse);
    }

    private AppointmentResponse toAppointmentListResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .appointmentCode(appointment.getAppointmentCode())
                .slotStartTime(appointment.getSlot().getStartTime())
                .slotEndTime(appointment.getSlot().getEndTime())
                .roomNumber(appointment.getSlot().getSchedule().getRoom().getRoomNumber())
                .patientFullName(getUserFullName(appointment.getPatient()))
                .patientPhone(appointment.getPatient().getPhone())
                .doctorFullName(getUserFullName(appointment.getDoctor()))
                .departmentName(appointment.getService().getDepartment().getName())
                .bookingDate(appointment.getBookingDate())
                .status(appointment.getStatus())
                .build();
    }

    private String getUserFullName(User user) {
        if (user == null) {
            return "";
        }
        String firstName = user.getFirstName() == null ? "" : user.getFirstName();
        String middleName = user.getMiddleName() == null ? "" : user.getMiddleName();
        String lastName = user.getLastName() == null ? "" : user.getLastName();
        return (lastName + " " + middleName + " " + firstName).trim().replaceAll("\\s+", " ");
    }

    public Page<AppointmentResponse> searchAppointmentListForReceptionist(String search, String status, LocalDate fromDate, LocalDate toDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Appointment> appointmentPage = appointmentRepository.searchAppointmentListForReceptionist(search, status, fromDate, toDate, pageable);
        return appointmentPage.map(this::toAppointmentListResponse);
    }

    @Override
    public Map<String, Long> getAppointmentStatusCountsInDateRangeForReceptionist(LocalDate fromDate, LocalDate toDate) {
        Map<String, Long> statusCounts = new HashMap<>();
        statusCounts.put("CONFIRMED", appointmentRepository.countConfirmedAppointmentsInDateRange(fromDate, toDate));
        statusCounts.put("WAITING", appointmentRepository.countWaitingAppointmentsInDateRange(fromDate, toDate));
        statusCounts.put("EXAMINING", appointmentRepository.countExaminingAppointmentsInDateRange(fromDate, toDate));
        statusCounts.put("COMPLETED", appointmentRepository.countCompletedAppointmentsInDateRange(fromDate, toDate));
        statusCounts.put("CANCELLED", appointmentRepository.countCancelledAppointmentsInDateRange(fromDate, toDate));
        statusCounts.put("NO_SHOW", appointmentRepository.countNoShowAppointmentsInDateRange(fromDate, toDate));
        return statusCounts;
    }

    private Map<String, Long> createDefaultAppointmentStatusCounts() {
        Map<String, Long> statusCounts = new HashMap<>();

        statusCounts.put("CONFIRMED", 0L);
        statusCounts.put("WAITING", 0L);
        statusCounts.put("EXAMINING", 0L);
        statusCounts.put("COMPLETED", 0L);
        statusCounts.put("CANCELLED", 0L);
        statusCounts.put("NO_SHOW", 0L);

        return statusCounts;
    }

    // ---------------------------------------------------------------------------
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

        String keyword = search != null ? search.trim().toLowerCase() : "";
        String selectedStatus = status != null ? status.trim() : "";

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
        Appointment appointment = appointmentRepository.findCheckInTicketById(appointmentId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin lịch hẹn"));
        AppointmentPrintResponse ticket = toPrintResponse(appointment);
        if (appointment.getCheckInTime() != null) {
            TimeSlot effectiveSlot = getEffectiveSlotForTicket(appointment);
            if (effectiveSlot != null) {
                ticket.setSlotStartTime(effectiveSlot.getStartTime());
                ticket.setSlotEndTime(effectiveSlot.getEndTime());
            }
            Long queueNumber = calculateQueueNumber(appointment);
            ticket.setQueueNumber(queueNumber);
        }
        return ticket;
    }

    @Override
    @Transactional(noRollbackFor = BadRequestException.class)
    public void confirmCheckInAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findCheckInTicketById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch hẹn"));

        if (!"CONFIRMED".equals(appointment.getStatus())) {
            throw new BadRequestException("Chỉ lịch hẹn đã xác nhận mới được check-in");
        }

        if (!LocalDate.now().equals(appointment.getBookingDate())) {
            throw new BadRequestException("Chỉ được check-in lịch hẹn trong ngày khám");
        }

        TimeSlot slot = appointment.getSlot();

        if (slot == null) {
            throw new BadRequestException("Lịch hẹn chưa có slot khám");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        LocalTime slotEndTime = slot.getEndTime();
        LocalTime lateLimitTime = slotEndTime.plusMinutes(15);

        if (currentTime.isAfter(lateLimitTime)) {
            markAppointmentNoShow(appointment, now);
            throw new BadRequestException("Bệnh nhân đã trễ quá 15 phút. Vui lòng đặt lịch lại.");
        }

        if (currentTime.isAfter(slotEndTime)) {
            List<TimeSlot> slots = getSlotsInSameRoomAndDate(appointment);
            TimeSlot nextSlot = findNextSlot(slots, slot);
            if (nextSlot == null) {
                markAppointmentNoShow(appointment, now);
                throw new BadRequestException("Bệnh nhân đã trễ slot khám và không còn slot tiếp theo. Vui lòng đặt lịch lại.");
            }
        }

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

    @Override
    public List<AppointmentDateGroupResponse> groupAppointmentsByDate(List<AppointmentResponse> appointments) {
        Map<LocalDate, List<AppointmentResponse>> groupedMap = new LinkedHashMap<>();

        for (AppointmentResponse appointment : appointments) {
            LocalDate bookingDate = appointment.getBookingDate();
            if (bookingDate == null) {
                continue;
            }
            if (!groupedMap.containsKey(bookingDate)) {
                groupedMap.put(bookingDate, new ArrayList<>());
            }
            groupedMap.get(bookingDate).add(appointment);
        }

        List<AppointmentDateGroupResponse> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Map.Entry<LocalDate, List<AppointmentResponse>> entry : groupedMap.entrySet()) {
            LocalDate bookingDate = entry.getKey();
            List<AppointmentResponse> appointmentList = entry.getValue();

            result.add(new AppointmentDateGroupResponse(
                    bookingDate,
                    today.equals(bookingDate),
                    appointmentList.size(),
                    appointmentList
            ));
        }

        return result;
    }

    @Override
    public Page<AppointmentResponse> getPagedAppointmentsForReceptionist(
            String search,
            String status,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size
    ) {
//        List<AppointmentResponse> allAppointments = getAppointmentListForReceptionist();
//
//        List<AppointmentResponse> filteredAppointments =
//                filterAppointmentsBySearchStatusAndDate(
//                        allAppointments,
//                        search,
//                        status,
//                        fromDate,
//                        toDate
//                );
//
//        Pageable pageable = PageRequest.of(page, size);
//
//        int start = (int) pageable.getOffset();
//        int end = Math.min(start + pageable.getPageSize(), filteredAppointments.size());
//
//        List<AppointmentResponse> pageContent;
//
//        if (start >= filteredAppointments.size()) {
//            pageContent = new ArrayList<>();
//        } else {
//            pageContent = filteredAppointments.subList(start, end);
//        }
//
        //  return new PageImpl<>(pageContent, pageable, filteredAppointments.size());
        return null;
    }

    private List<AppointmentResponse> filterAppointmentsBySearchStatusAndDate(List<AppointmentResponse> appointments, String search, String status, LocalDate fromDate, LocalDate toDate) {
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
            boolean matchesDate = true;

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

            if (fromDate != null) {
                if (appointment.getBookingDate() == null) {
                    matchesDate = false;
                } else {
                    matchesDate = !appointment.getBookingDate().isBefore(fromDate);
                }
            }

            if (toDate != null) {
                if (appointment.getBookingDate() == null) {
                    matchesDate = false;
                } else {
                    matchesDate = matchesDate
                            && !appointment.getBookingDate().isAfter(toDate);
                }
            }

            if (matchesSearch && matchesStatus && matchesDate) {
                result.add(appointment);
            }
        }

        return result;
    }

    private void markAppointmentNoShow(Appointment appointment, LocalDateTime now) {
        appointment.setStatus("NO_SHOW");
        appointment.setUpdatedAt(now);
        appointmentRepository.save(appointment);
    }

    private Long calculateQueueNumber(Appointment appointment) {
        TimeSlot originalSlot = appointment.getSlot();

        if (originalSlot == null || originalSlot.getSchedule() == null) {
            return 0L;
        }

        List<TimeSlot> slots = getSlotsInSameRoomAndDate(appointment);
        TimeSlot effectiveSlot = getEffectiveSlot(appointment, slots, appointment.getCheckInTime());

        if (effectiveSlot == null) {
            return 0L;
        }

        Long baseNumber = calculateBaseNumberBeforeSlot(slots, effectiveSlot);
        Long orderInSlot = calculateOrderInEffectiveSlot(appointment, slots, effectiveSlot);

        return baseNumber + orderInSlot;
    }

    private Long calculateBaseNumberBeforeSlot(List<TimeSlot> slots, TimeSlot effectiveSlot) {
        long baseNumber = 0L;

        for (TimeSlot slot : slots) {
            if (slot.getId().equals(effectiveSlot.getId())) {
                break;
            }

            if (slot.getMaxCapacity() != null) {
                baseNumber += slot.getMaxCapacity();
            }
        }

        return baseNumber;
    }

    private Long calculateOrderInEffectiveSlot(Appointment targetAppointment, List<TimeSlot> slots, TimeSlot effectiveSlot) {
        Integer roomId = getRoomId(targetAppointment);

        if (roomId == null) {
            return 0L;
        }

        List<Appointment> checkedInAppointments =
                appointmentRepository.findCheckedInAppointmentsByBookingDateAndScheduleId(
                        targetAppointment.getBookingDate(),
                        roomId
                );

        long order = 0L;

        for (Appointment appointment : checkedInAppointments) {
            if (appointment.getCheckInTime() == null) {
                continue;
            }

            TimeSlot appointmentEffectiveSlot = getEffectiveSlot(appointment, slots, appointment.getCheckInTime());

            if (appointmentEffectiveSlot == null) {
                continue;
            }

            if (!appointmentEffectiveSlot.getId().equals(effectiveSlot.getId())) {
                continue;
            }

            boolean checkedInBefore = appointment.getCheckInTime().isBefore(targetAppointment.getCheckInTime());

            boolean checkedInSameTimeButIdSmaller =
                    appointment.getCheckInTime().isEqual(targetAppointment.getCheckInTime())
                            && appointment.getId() <= targetAppointment.getId();

            if (checkedInBefore || checkedInSameTimeButIdSmaller) {
                order++;
            }
        }

        return order;
    }

    private TimeSlot getEffectiveSlotForTicket(Appointment appointment) {
        TimeSlot originalSlot = appointment.getSlot();

        if (originalSlot == null) {
            return null;
        }
        List<TimeSlot> slots = getSlotsInSameRoomAndDate(appointment);

        return getEffectiveSlot(appointment, slots, appointment.getCheckInTime());
    }

    private TimeSlot getEffectiveSlot(Appointment appointment, List<TimeSlot> slots, LocalDateTime checkInTime) {
        TimeSlot originalSlot = appointment.getSlot();

        if (originalSlot == null || checkInTime == null) {
            return originalSlot;
        }

        LocalTime checkInLocalTime = checkInTime.toLocalTime();

        if (!checkInLocalTime.isAfter(originalSlot.getEndTime())) {
            return originalSlot;
        }

        LocalTime lateLimitTime = originalSlot.getEndTime().plusMinutes(15);

        if (!checkInLocalTime.isAfter(lateLimitTime)) {
            TimeSlot nextSlot = findNextSlot(slots, originalSlot);

            if (nextSlot != null) {
                return nextSlot;
            }
        }

        return originalSlot;
    }

    private Integer getRoomId(Appointment appointment) {
        if (appointment == null
                || appointment.getSlot() == null
                || appointment.getSlot().getSchedule() == null
                || appointment.getSlot().getSchedule().getRoom() == null
        ) {
            return null;
        }
        return appointment.getSlot().getSchedule().getRoom().getId();
    }

    private List<TimeSlot> getSlotsInSameRoomAndDate(Appointment appointment) {
        Integer roomId = getRoomId(appointment);
        if (roomId == null || appointment.getBookingDate() == null) {
            return new ArrayList<>();
        }
        return timeSlotRepository.findByRoomIdAndWorkDateOrderByStartTimeAsc(roomId, appointment.getBookingDate());
    }

    private TimeSlot findNextSlot(List<TimeSlot> slots, TimeSlot currentSlot) {
        if (slots == null || slots.isEmpty() || currentSlot == null) {
            return null;
        }

        for (int i = 0; i < slots.size(); i++) {
            TimeSlot slot = slots.get(i);

            if (slot.getId().equals(currentSlot.getId())) {
                if (i + 1 < slots.size()) {
                    return slots.get(i + 1);
                }

                return null;
            }
        }

        return null;
    }


    private boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    public long getAllAppointment() {
        return appointmentRepository.count();
    }

    public Map<String, Long> findTodayAppointmentsByStatus(LocalDate localDate) {
        List<AppointmentStatusCountResponse> list =
                appointmentRepository.findTodayAppointmentsByStatus(localDate);

        Map<String, Long> statusCount = new HashMap<>();
        statusCount.put("WAITING", 0L);
        statusCount.put("CONFIRMED", 0L);
        statusCount.put("EXAMINING", 0L);
        statusCount.put("COMPLETED", 0L);
        statusCount.put("CANCELLED", 0L);

        list.forEach(appointment -> {
            statusCount.put(appointment.getStatus(), appointment.getCount());
        });

        return statusCount;
    }

    public List<AppointmentResponse> findAppointmentsByBookingDate(LocalDate today) {
        List<Appointment> appointments = appointmentRepository.findAppointmentsByBookingDate(today);
        List<AppointmentResponse> responses = new ArrayList<>();

        appointments.forEach(a -> {
            AppointmentResponse response = new AppointmentResponse();

            response.setAppointmentCode(a.getAppointmentCode());
            response.setPatientFullName(buildFullName(
                    a.getPatient().getLastName(),
                    a.getPatient().getMiddleName(),
                    a.getPatient().getFirstName()
            ));
            response.setDoctorFullName(buildFullName(
                    a.getDoctor().getLastName(),
                    a.getDoctor().getMiddleName(),
                    a.getDoctor().getFirstName()
            ));
            response.setId(a.getId());
            response.setServiceName(a.getService().getName());
            response.setSlotStartTime(a.getSlot().getStartTime());
            response.setSlotEndTime(a.getSlot().getEndTime());
            response.setStatus(a.getStatus());

            responses.add(response);
        });

        return responses;
    }

    @Override
    public List<ScheduleSlotResponse> getAvailableSchedules(Long doctorId) {
        List<DoctorSchedule> schedules = doctorScheduleRepository
                .findAvailableSchedulesByDoctorId(doctorId, LocalDate.now());

        LocalDate today = LocalDate.now();
        java.time.LocalTime now = java.time.LocalTime.now();

        return schedules.stream()
                .filter(schedule -> {
                    if (!schedule.getWorkDate().isEqual(today)) {
                        return true;
                    }

                    if ("MORNING".equals(schedule.getShift())) {
                        return now.isBefore(java.time.LocalTime.of(12, 0));
                    } else {
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
                })
                .toList();
    }

    @Override
    @Transactional
    public AppointmentResponse bookAppointment(Long patientId, CreateAppointmentRequest request) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bệnh nhân"));

        User doctor = userRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bác sĩ"));

        MedicalService service = medicalServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ"));

        TimeSlot slot = timeSlotRepository.findByIdWithSchedule(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khung giờ"));

        if (!"AVAILABLE".equals(slot.getStatus()) || slot.getBookedCapacity() >= slot.getMaxCapacity()) {
            throw new BadRequestException("Khung giờ này đã đầy, vui lòng chọn khung giờ khác");
        }

        boolean alreadyBooked = appointmentRepository.existsBySlotIdAndPatientIdAndStatusNotIn(
                slot.getId(),
                patientId,
                List.of("CANCELLED", "NO_SHOW")
        );

        if (alreadyBooked) {
            throw new BadRequestException("Bạn đã đặt lịch trong khung giờ này rồi");
        }

        slot.setBookedCapacity(slot.getBookedCapacity() + 1);

        if (slot.getBookedCapacity() >= slot.getMaxCapacity()) {
            slot.setStatus("FULL");
        }

        timeSlotRepository.save(slot);

        LocalDateTime now = LocalDateTime.now();

        Appointment appointment = new Appointment();
        appointment.setAppointmentCode(CodeGenerator.generateAppointmentCode());
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setService(service);
        appointment.setSlot(slot);
        appointment.setBookingDate(slot.getSchedule().getWorkDate());
        appointment.setNote(request.getNote());
        appointment.setStatus("CONFIRMED");
        appointment.setCreatedAt(now);
        appointment.setUpdatedAt(now);

        Appointment saved = appointmentRepository.save(appointment);
        return toResponse(saved);
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientIdWithDetails(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AppointmentResponse getAppointmentDetail(Long appointmentId, Long patientId) {
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

        Integer patientAge = null;
        if (patient != null && patient.getDateOfBirth() != null) {
            patientAge = Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears();
        }

        String patientGender = patient != null ? patient.getGender() : null;

        String patientInitials = "";
        if (!patientFullName.trim().isEmpty()) {
            String[] parts = patientFullName.trim().split("\\s+");

            if (parts.length > 0) {
                patientInitials += parts[0].substring(0, 1).toUpperCase();
            }

            if (parts.length > 1) {
                patientInitials += parts[parts.length - 1].substring(0, 1).toUpperCase();
            }
        }

        return AppointmentResponse.builder()
                .id(a.getId())
                .appointmentCode(a.getAppointmentCode())
                .status(a.getStatus())
                .patientId(patient != null ? patient.getId() : null)
                .patientFullName(patientFullName)
                .patientPhone(patient != null ? patient.getPhone() : null)
                .patientAddress(buildPatientAddress(patient))
                .patientAge(patientAge)
                .patientGender(patientGender)
                .patientInitials(patientInitials)
                .doctorId(doctor != null ? doctor.getId() : null)
                .doctorFullName(doctorFullName)
                .doctorDegree(doctor != null ? doctor.getDegree() : null)
                .departmentName(doctor != null && doctor.getDepartment() != null
                        ? doctor.getDepartment().getName()
                        : null)
                .serviceId(a.getService() != null ? a.getService().getId() : null)
                .serviceName(a.getService() != null ? a.getService().getName() : null)
                .bookingDate(a.getBookingDate())
                .shift(schedule != null ? schedule.getShift() : null)
                .slotStartTime(slot != null ? slot.getStartTime() : null)
                .slotEndTime(slot != null ? slot.getEndTime() : null)
                .roomNumber(schedule != null && schedule.getRoom() != null
                        ? schedule.getRoom().getRoomNumber()
                        : null)
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

        if (lastName != null) {
            sb.append(lastName).append(" ");
        }

        if (middleName != null) {
            sb.append(middleName).append(" ");
        }

        if (firstName != null) {
            sb.append(firstName);
        }

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

        if (address.getProvince() != null
                && address.getProvince().getName() != null
                && !address.getProvince().getName().isBlank()) {

            if (!sb.isEmpty()) {
                sb.append(", ");
            }

            sb.append(address.getProvince().getName().trim());
        }

        return sb.toString();
    }

    @Override
    public Page<AppointmentResponse> getAppointmentsForDoctor(Long doctorId, LocalDate bookingDate, String status, Pageable pageable) {
        List<String> statuses;

        if (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status)) {
            statuses = List.of("WAITING", "EXAMINING", "COMPLETED");
        } else {
            statuses = List.of(status.toUpperCase());
        }

        return appointmentRepository.findByDoctorIdAndBookingDateAndStatusIn(doctorId, bookingDate, statuses, pageable)
                .map(this::toResponse);
    }

    @Override
    public long countAppointmentsForDoctor(Long doctorId, LocalDate bookingDate, String status) {
        if (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status)) {
            return appointmentRepository.countByDoctorIdAndBookingDateAndStatusIn(
                    doctorId,
                    bookingDate,
                    List.of("WAITING", "EXAMINING", "COMPLETED")
            );
        }

        return appointmentRepository.countByDoctorIdAndBookingDateAndStatus(doctorId, bookingDate, status.toUpperCase());
    }

    @Override
    @Transactional
    public void updateAppointmentStatus(Long appointmentId, String newStatus) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy lịch hẹn với ID: " + appointmentId
                ));

        appointment.setStatus(newStatus.toUpperCase());
        appointment.setUpdatedAt(LocalDateTime.now());

        appointmentRepository.save(appointment);
    }

    @Override
    public List<AppointmentResponse> getRecentCompletedAppointmentsForDoctor(Long doctorId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return appointmentRepository.findRecentCompletedAppointments(doctorId, "COMPLETED", pageable)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}