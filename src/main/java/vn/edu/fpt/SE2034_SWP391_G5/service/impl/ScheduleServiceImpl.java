package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateDoctorScheduleRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.DoctorScheduleUpdateRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.*;
import vn.edu.fpt.SE2034_SWP391_G5.entity.*;
import vn.edu.fpt.SE2034_SWP391_G5.enums.DoctorScheduleStatus;
import vn.edu.fpt.SE2034_SWP391_G5.enums.ScheduleShift;
import vn.edu.fpt.SE2034_SWP391_G5.enums.TimeSlotStatus;
import vn.edu.fpt.SE2034_SWP391_G5.enums.WeekScheduleStatus;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.Schedule.ScheduleConflictException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.*;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.DoctorService;
import vn.edu.fpt.SE2034_SWP391_G5.service.NotificationService;
import vn.edu.fpt.SE2034_SWP391_G5.service.EmailService;
import vn.edu.fpt.SE2034_SWP391_G5.service.ScheduleService;
import vn.edu.fpt.SE2034_SWP391_G5.util.DateTimeUtil;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final DateTimeUtil dateTimeUtil;
    private final WeekScheduleRepository weekScheduleRepository;
    private final RoomRepository roomRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final DoctorService doctorService;
    private final NotificationService notificationService;
    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    //LinhNH
    @Override
    public List<DoctorScheduleWeekResponse> getWeeklySchedule(Long doctorId, LocalDate targetDate) {
        // Find Monday of the target week
        LocalDate monday = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);

        // Fetch active schedules for the week
        List<DoctorSchedule> schedules = doctorScheduleRepository
                .findByDoctorIdAndWorkDateBetweenAndStatusOrderByWorkDateAscShiftAsc(
                        doctorId, monday, sunday, "ACTIVE");

        // Filter: only display if week_schedule status is PUBLISHED
        schedules = schedules.stream()
                .filter(ds -> ds.getWeekSchedule() != null && "PUBLISHED".equalsIgnoreCase(ds.getWeekSchedule().getStatus()))
                .collect(Collectors.toList());

        // Group by date
        Map<LocalDate, List<DoctorSchedule>> schedulesByDate = schedules.stream()
                .collect(Collectors.groupingBy(DoctorSchedule::getWorkDate));

        List<DoctorScheduleWeekResponse> weekSchedule = new ArrayList<>();
        String[] dayLabels = {"THỨ 2", "THỨ 3", "THỨ 4", "THỨ 5", "THỨ 6", "THỨ 7", "CHỦ NHẬT"};

        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = monday.plusDays(i);
            List<DoctorSchedule> daySchedules = schedulesByDate.getOrDefault(currentDate, new ArrayList<>());

            List<DoctorScheduleWeekResponse.ShiftDetail> shiftDetails = daySchedules.stream().map(ds -> {
                String shiftType = ds.getShift(); // e.g. "MORNING"
                String shiftClass = "morning";
                String badgeText = "Sáng";
                String title = "Ca Sáng";
                String timeRange = "07:30 - 12:00";

                if ("AFTERNOON".equalsIgnoreCase(shiftType)) {
                    shiftClass = "afternoon";
                    badgeText = "Chiều";
                    title = "Ca Chiều";
                    timeRange = "13:00 - 18:00";
                } else if ("FULL_DAY".equalsIgnoreCase(shiftType)) {
                    shiftClass = "full";
                    badgeText = "Cả ngày";
                    title = "Cả ngày";
                    timeRange = "07:30 - 18:00";
                }

                String roomName = ds.getRoom() != null ? ds.getRoom().getName() : "";
                if (roomName == null || roomName.isEmpty()) {
                    roomName = ds.getRoom() != null ? "Phòng " + ds.getRoom().getRoomNumber() : "";
                }

                return DoctorScheduleWeekResponse.ShiftDetail.builder()
                        .id(ds.getId())
                        .shift(shiftType)
                        .shiftClass(shiftClass)
                        .badgeText(badgeText)
                        .title(title)
                        .timeRange(timeRange)
                        .roomName(roomName)
                        .build();
            }).collect(Collectors.toList());

            String dayAndMonth = String.format("%02d/%02d", currentDate.getDayOfMonth(), currentDate.getMonthValue());
            weekSchedule.add(DoctorScheduleWeekResponse.builder()
                    .dayOfWeekLabel(dayLabels[i])
                    .dayOfMonth(currentDate.getDayOfMonth())
                    .dayAndMonth(dayAndMonth)
                    .date(currentDate)
                    .shifts(shiftDetails)
                    .build());
        }

        return weekSchedule;
    }


    //Find all doctor work today
    @Override
    public Page<DoctorOnDutyResponse> findDoctorScheduleByDate(@Param("date") LocalDate date, Integer page, Integer size ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DoctorSchedule> doctorSchedules = doctorScheduleRepository.findByDate(date, pageable);

        return doctorSchedules.map(this::doctorOnDutyResponse);
    }
    private DoctorOnDutyResponse doctorOnDutyResponse(DoctorSchedule doctorSchedule) {
        DoctorOnDutyResponse response = new DoctorOnDutyResponse();
        response.setDoctorId(doctorSchedule.getId());
        response.setDoctorName(doctorSchedule.getDoctor().getFirstName() + " " + doctorSchedule.getDoctor().getMiddleName() + " " + doctorSchedule.getDoctor().getLastName());
        response.setDepartmentName(doctorSchedule.getDoctor().getDepartment().getName());
        response.setShift(String.valueOf(doctorSchedule.getShift()));
        response.setStatus(doctorSchedule.getDoctor().getStatus());
        return response;
    }

    //LinhNH
    @Override
    public DoctorScheduleReportResponse getWeeklyScheduleReport(Long doctorId, LocalDate targetDate) {
        LocalDate localTargetDate = (targetDate != null) ? targetDate : LocalDate.now();

        // Get Monday of target week
        LocalDate monday = localTargetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // Fetch Weekly Schedule
        List<DoctorScheduleWeekResponse> weekSchedule = getWeeklySchedule(doctorId, localTargetDate);

        // Navigation dates
        LocalDate prevWeekMonday = monday.minusWeeks(1);
        LocalDate nextWeekMonday = monday.plusWeeks(1);

        // Calculate summary metrics
        double totalHours = 0;
        int shiftCount = 0;
        for (DoctorScheduleWeekResponse day : weekSchedule) {
            if (day.getShifts() != null) {
                for (DoctorScheduleWeekResponse.ShiftDetail shift : day.getShifts()) {
                    shiftCount++;
                    if ("MORNING".equalsIgnoreCase(shift.getShift())) {
                        totalHours += 4.5;
                    } else if ("AFTERNOON".equalsIgnoreCase(shift.getShift())) {
                        totalHours += 5.0;
                    } else if ("FULL_DAY".equalsIgnoreCase(shift.getShift())) {
                        totalHours += 12.0;
                    }
                }
            }
        }

        // Formatting double hours
        String totalHoursStr;
        if (totalHours == (long) totalHours) {
            totalHoursStr = String.format("%d", (long) totalHours);
        } else {
            totalHoursStr = String.format("%.1f", totalHours).replace(',', '.');
        }

        String shiftCountStr = String.format("%d", shiftCount);

        // Performance evaluation
        String performance = "N/A";
        if (totalHours >= 20) {
            performance = "Xuất sắc";
        } else if (totalHours >= 15) {
            performance = "Tốt";
        } else if (totalHours >= 8) {
            performance = "Trung bình";
        } else if (totalHours > 0) {
            performance = "Cần cố gắng";
        }

        return DoctorScheduleReportResponse.builder()
                .weekSchedule(weekSchedule)
                .totalHoursStr(totalHoursStr)
                .shiftCountStr(shiftCountStr)
                .performance(performance)
                .prevWeekDate(prevWeekMonday.toString())
                .nextWeekDate(nextWeekMonday.toString())
                .build();
    }




    /*

   Create Doctor Schedule ZONE
   ALL THE FUNCTIONS RELATED TO CREATING WEEKLY SCHEDULE IN HERE


    */


    /*
     * 2 function getAllRoomsByDepartmentId  and getAllDoctorByDepartmentId
     * helps UI can display exactly the doctor and room related to the department
     *
     */

    @Override
    public List<Room> getAllRoomsByDepartmentId(Integer departmentId) {
        return doctorScheduleRepository.findRoomsByDepartmentId(departmentId);
    }

    ;

    @Override
    public List<User> getAllDoctorByDepartmentId(Integer departmentId) {
        return doctorScheduleRepository.findDoctorByDepartmentId(departmentId);
    }

    ;


    /*
     *
     * Thís function ís help create a TimeSlot form
     *
     * */
    private TimeSlot buildSlot(DoctorSchedule doctorSchedule, String start, String end, int maxCapacity) {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setSchedule(doctorSchedule);
        timeSlot.setStartTime(LocalTime.parse(start));
        timeSlot.setEndTime(LocalTime.parse(end));
        timeSlot.setMaxCapacity(maxCapacity);
        timeSlot.setBookedCapacity(0);
        timeSlot.setVersion(0L);
        timeSlot.setStatus(TimeSlotStatus.AVAILABLE.toString());
        return timeSlot;
    }

    /*
     *
     *  This function is help generate the time slot base on the Shift
     *
     */

    private List<TimeSlot> generateTimeSlot(DoctorSchedule doctorSchedule, String Shift, int maxCapacity) {
        List<TimeSlot> timeSlots = new ArrayList<>();
        switch (Shift) {
            case "MORNING":
                timeSlots.add(buildSlot(doctorSchedule, "07:00", "09:00", maxCapacity));
                timeSlots.add(buildSlot(doctorSchedule, "09:00", "11:00", maxCapacity));
                break;
            case "AFTERNOON":
                timeSlots.add(buildSlot(doctorSchedule, "13:00", "15:00", maxCapacity));
                timeSlots.add(buildSlot(doctorSchedule, "15:00", "17:00", maxCapacity));
                break;
            case "FULL_DAY":
                timeSlots.add(buildSlot(doctorSchedule, "07:00", "09:00", maxCapacity));
                timeSlots.add(buildSlot(doctorSchedule, "09:00", "11:00", maxCapacity));
                timeSlots.add(buildSlot(doctorSchedule, "13:00", "15:00", maxCapacity));
                timeSlots.add(buildSlot(doctorSchedule, "15:00", "17:00", maxCapacity));
                break;
        }
        return timeSlots;
    }

    /**
     *
     * this function helps checking the duplication shift in a working date
     *
     */
    private List<String> getConflictShift(String shift) {
        List<String> conflicts = new ArrayList<>();
        switch (shift) {
            case "MORNING":
                return List.of(ScheduleShift.MORNING.toString(), ScheduleShift.FULL_DAY.toString());
            case "AFTERNOON":
                return List.of(ScheduleShift.AFTERNOON.toString(), ScheduleShift.FULL_DAY.toString());
            case "FULL_DAY":
                return List.of(ScheduleShift.FULL_DAY.toString(), ScheduleShift.AFTERNOON.toString(), ScheduleShift.MORNING.toString());
            default:
                return List.of();
        }
    }


    @Override
    @Transactional
    public DoctorScheduleResponse createDoctorSchedule(CreateDoctorScheduleRequest createDoctorScheduleRequest, Long managerId, Long weekScheduleId) {

        //check department
        Department department = departmentRepository.findById(createDoctorScheduleRequest.getDepartmentId()).orElseThrow(() -> new ScheduleConflictException("Khoa không tồn tại"));
        //Check doctor
        User doctor = userRepository.findDoctorStaffDetailById(createDoctorScheduleRequest.getDoctorId()).orElseThrow(() -> new ScheduleConflictException("Bác sĩ không tồn tại"));

        //Working date
        LocalDate workingDate = createDoctorScheduleRequest.getWorkDate();

        WeekSchedule weekSchedule = weekScheduleRepository.findWeekScheduleById(weekScheduleId);
        String weekScheduleStatus = weekSchedule.getStatus();

        //preventing added new doctor Schedule during weeklySchedule is finished
        if (weekScheduleStatus.equals(WeekScheduleStatus.EXPIRED.toString()) || weekScheduleStatus.equals(WeekScheduleStatus.FINALIZED.toString())) {
            throw new ScheduleConflictException("Lịch làm việc tuần này đã chốt không thể thêm mới!");
        }

        //Shift
        String shift = createDoctorScheduleRequest.getScheduleShift().toString();
        List<String> shifts = getConflictShift(shift);
        boolean hasConflict = doctorScheduleRepository.existsByDoctorAndWorkDate(doctor, workingDate);
        if (hasConflict) {
            throw new ScheduleConflictException("Bác sĩ đã có ca làm trước đó");
        }

        //Rooms
        Room room = roomRepository.findById(createDoctorScheduleRequest.getRoomId()).orElseThrow(() -> new ScheduleConflictException("Phòng không tồn tại"));
        boolean hasConflictRoom = doctorScheduleRepository.existsByRoomAndWorkDateAndShift(room, workingDate, shifts);
        if (hasConflictRoom) {
            throw new ScheduleConflictException("Phòng đã có bác sĩ trực");
        }

        //capacity
        Integer maxCapacity = createDoctorScheduleRequest.getMaxCapacity();

        //Note
        String note = createDoctorScheduleRequest.getNote();
        User manager = userRepository.findById(managerId).orElseThrow(() -> new ScheduleConflictException("Người dùng không xác định"));

        DoctorSchedule doctorSchedule = new DoctorSchedule();
        doctorSchedule.setRoom(room);
        doctorSchedule.setDoctor(doctor);
        doctorSchedule.setCreatedAt(LocalDateTime.now());
        doctorSchedule.setShift(shift);
        doctorSchedule.setWorkDate(workingDate);
        doctorSchedule.setCreatedBy(manager);
        doctorSchedule.setNote(note);
        doctorSchedule.setWeekSchedule(weekSchedule);
        doctorSchedule.setUpdatedAt(LocalDateTime.now());
        doctorSchedule.setCreatedBy(manager);
        doctorSchedule.setStatus(DoctorScheduleStatus.ACTIVE.toString());

        DoctorSchedule saved = doctorScheduleRepository.save(doctorSchedule);

        List<TimeSlot> timeSlots = generateTimeSlot(saved, shift, maxCapacity);
        timeSlotRepository.saveAll(timeSlots);
        return DoctorScheduleResponse.builder()
                .doctorName(saved.getDoctor().getFirstName() + " " + saved.getDoctor().getMiddleName() + " " + saved.getDoctor().getLastName())
                .doctorId(saved.getDoctor().getId())
                .shift(saved.getShift())
                .roomName(saved.getRoom().getName())
                .workDate(saved.getWorkDate())
                .maxSlots(maxCapacity)
                .status(saved.getStatus())
                .note(saved.getNote())
                .build();
    }


    /*
     *
     * All the functions in this area belongs to List doctor Schedule Screen
     *
     */
    @Override
    public Page<DoctorScheduleRowResponse> doctorScheduleRowResponse(Long weekScheduleId, Integer departmentId, String doctorName, String shift, int page, int size) {
        return toDoctorScheduleRowResponse(weekScheduleId, departmentId, doctorName, shift, page, size);
    }

    @Override
    public WeekSchedule updateWeekSchedule(Long weekScheduleId, String action, Long managerId) {

        WeekSchedule presentWeek = weekScheduleRepository.findWeekScheduleById(weekScheduleId);
        User manager = userRepository.findById(managerId).orElseThrow(() -> new ResourceNotFoundException("Không rõ danh tính người đang thao tác"));
        switch (action) {
            case "DRAFT":
                presentWeek.setStatus(WeekScheduleStatus.DRAFT.toString());
                presentWeek.setUpdatedAt(LocalDateTime.now());
                presentWeek.setCreatedBy(manager);
                presentWeek = weekScheduleRepository.save(presentWeek);
                break;
            case "PUBLISHED":
                presentWeek.setStatus(WeekScheduleStatus.PUBLISHED.toString());
                presentWeek.setUpdatedAt(LocalDateTime.now());
                presentWeek.setCreatedBy(manager);
                presentWeek = weekScheduleRepository.save(presentWeek);

                // Gửi thông báo tới các bác sĩ được xếp lịch trong tuần này
                try {
                    List<User> doctors = doctorScheduleRepository.findDoctorsByWeekScheduleId(weekScheduleId);
                    if (doctors != null) {
                        for (User doctor : doctors) {
                            notificationService.createNotification(
                                    doctor,
                                    "Lịch làm việc mới",
                                    "Lịch làm việc tuần từ ngày " + presentWeek.getStartDate() + " đến " + presentWeek.getEndDate() + " đã được công bố. Vui lòng kiểm tra.",
                                    "SCHEDULE_ASSIGNED",
                                    presentWeek.getId(),
                                    "WeekSchedule"
                            );
                        }
                    }
                } catch (Exception e) {
                    // Tránh lỗi thông báo làm rollback nghiệp vụ chính
                }
                break;
            case "FINALIZED":
                presentWeek.setStatus(WeekScheduleStatus.FINALIZED.toString());
                presentWeek.setUpdatedAt(LocalDateTime.now());
                presentWeek.setCreatedBy(manager);
                presentWeek = weekScheduleRepository.save(presentWeek);
                break;
        }
        return presentWeek;
    }


    //This function can improve processing time by using collector.groupingBy (from O(m*n) -> O(N))
    private Page<DoctorScheduleRowResponse> toDoctorScheduleRowResponse(Long weekScheduleId, Integer departmentId, String doctorName, String shift, int page, int size) {
        if (shift != null && shift.isBlank()) {
            shift = null;
        }

        if (doctorName != null && doctorName.isBlank()) {
            doctorName = null;
        }
        Pageable pageable = PageRequest.of(page, size);

        Page<User> doctors = doctorScheduleRepository.getDoctorScheduleByFilter("DOCTOR", departmentId, doctorName, shift, weekScheduleId, pageable);

        if (doctors.getContent().isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> doctorIds = doctors.stream().map(User::getId).toList();
        List<DoctorSchedule> doctorSchedules = doctorScheduleRepository.getAllDoctorScheduleByWeekSchedule(doctorIds, weekScheduleId);
        List<DoctorResponse> doctorResponses = doctors.stream().map(doctorService::toResponse).toList();
        List<DoctorScheduleRowResponse> result = doctorResponses.stream().map(doctorResponse -> {
            DoctorScheduleRowResponse doctorScheduleRowResponse = new DoctorScheduleRowResponse(doctorResponse);
            Map<LocalDate, DoctorScheduleResponse> scheduleByDate = doctorSchedules.stream()
                    .filter(s -> s.getDoctor().getId().equals(doctorResponse.getId()))
                    .collect(Collectors.toMap(DoctorSchedule::getWorkDate, this::toDoctorScheduleResponse));
            doctorScheduleRowResponse.setScheduleByDate(scheduleByDate);
            return doctorScheduleRowResponse;
        }).toList();

        return new PageImpl<>(result, doctors.getPageable(), doctors.getTotalElements());
    }


    private List<DoctorScheduleResponse> toDoctorScheduleResponses(List<DoctorSchedule> doctorSchedules) {
        return doctorSchedules.stream().map(this::toDoctorScheduleResponse).collect(Collectors.toList());
    }

    private DoctorScheduleResponse toDoctorScheduleResponse(DoctorSchedule doctorSchedule) {
        return DoctorScheduleResponse.builder()
                .id(doctorSchedule.getId())
                .doctorId(doctorSchedule.getDoctor().getId())
                .doctorName(doctorSchedule.getDoctor().getFirstName() + " " + doctorSchedule.getDoctor().getMiddleName() + " " + doctorSchedule.getDoctor().getLastName())
                .shift(doctorSchedule.getShift())
                .roomName(doctorSchedule.getRoom().getName())
                .workDate(doctorSchedule.getWorkDate())
                .maxSlots(doctorSchedule.getTimeSlots().stream().mapToInt(slot -> slot.getMaxCapacity() != null ? slot.getMaxCapacity() : 0).sum())
                .status(doctorSchedule.getStatus())
                .note(doctorSchedule.getNote())
                .bookedCapacity(calculateBookCapacity(doctorSchedule))
                .build();
    }

    private Integer calculateBookCapacity(DoctorSchedule doctorSchedule) {
        return doctorSchedule.getTimeSlots().stream()
                .mapToInt(doctorScheduleCapacity -> doctorScheduleCapacity.getBookedCapacity() != null ? doctorScheduleCapacity.getBookedCapacity() : 0)
                .sum();
    }


    /*
     *
     * This function here related to update doctor schedule
     *
     * */

    @Override
    public DoctorScheduleUpdateRequest getDoctorScheduleUpdateRequest(Long doctorScheduleId) {
        DoctorSchedule doctorSchedule = doctorScheduleRepository.findById(doctorScheduleId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch của bác sĩ"));
        return toDoctorScheduleUpdateRequest(doctorSchedule);
    }

    @Transactional
    @Override
    public DoctorScheduleUpdateRequest updateDoctorSchedule(DoctorScheduleUpdateRequest doctorScheduleUpdateRequest, Long weekScheduleId) {
        DoctorSchedule doctorSchedule = doctorScheduleRepository.findById(doctorScheduleUpdateRequest.getScheduleId()).orElseThrow(() -> new ResourceNotFoundException("Lịch không tồn tại"));
        WeekSchedule weekSchedule = weekScheduleRepository.findWeekScheduleById(weekScheduleId);
        String weekScheduleStatus = weekSchedule.getStatus();

        if (weekScheduleStatus.equals(WeekScheduleStatus.FINALIZED.toString())) {
            String oldStatus = doctorSchedule.getStatus();
            String newStatus = doctorScheduleUpdateRequest.getStatus();

            doctorSchedule.setStatus(newStatus);
            DoctorSchedule saved = doctorScheduleRepository.save(doctorSchedule);

            // Nếu Manager chuyển trạng thái lịch trực thành CANCELLED
            if ("CANCELLED".equalsIgnoreCase(newStatus) && !"CANCELLED".equalsIgnoreCase(oldStatus)) {
                // 1. Tìm tất cả các lịch hẹn đang WAITING hoặc CONFIRMED trong lịch trực này
                List<Appointment> activeAppointments = appointmentRepository.findActiveAppointmentsByScheduleId(doctorSchedule.getId());
                if (activeAppointments != null && !activeAppointments.isEmpty()) {
                    for (Appointment appointment : activeAppointments) {
                        try {
                            // Hủy lịch hẹn
                            appointment.setStatus("CANCELLED");
                            appointment.setUpdatedAt(LocalDateTime.now());
                            appointmentRepository.save(appointment);

                            // Giải phóng TimeSlot
                            TimeSlot slot = appointment.getSlot();
                            if (slot != null && slot.getBookedCapacity() > 0) {
                                slot.setBookedCapacity(slot.getBookedCapacity() - 1);
                                slot.setStatus("AVAILABLE");
                                timeSlotRepository.save(slot);
                            }

                            // 2. Gửi thông báo trên App cho bệnh nhân
                            String serviceName = (appointment.getService() != null) ? appointment.getService().getName() : "Dịch vụ khám";
                            String startTime = (slot != null && slot.getStartTime() != null) ? slot.getStartTime().toString() : "";
                            String timeMsg = (startTime.isEmpty()) ? "" : " lúc " + startTime;
                            String doctorName = (doctorSchedule.getDoctor() != null)
                                    ? (doctorSchedule.getDoctor().getLastName() + " " + doctorSchedule.getDoctor().getFirstName())
                                    : "Bác sĩ";

                            notificationService.createNotification(
                                    appointment.getPatient(),
                                    "Lịch khám bị hủy (Bác sĩ hủy ca)",
                                    "Lịch hẹn khám " + serviceName + " với bác sĩ " + doctorName + " của bạn vào ngày " + appointment.getBookingDate() + timeMsg + " đã bị hủy do bác sĩ thay đổi lịch trực đột xuất. Mong quý khách thông cảm.",
                                    "APPOINTMENT_CANCELLED",
                                    appointment.getId(),
                                    "Appointment"
                            );

                            // 3. Gửi thông báo Email cho bệnh nhân
                            if (appointment.getPatient() != null && appointment.getPatient().getEmail() != null && !appointment.getPatient().getEmail().isBlank()) {
                                String slotTimeStr = (slot != null && slot.getStartTime() != null && slot.getEndTime() != null)
                                        ? (slot.getStartTime() + " - " + slot.getEndTime())
                                        : "";
                                String patientName = appointment.getPatient().getLastName() + " " + (appointment.getPatient().getMiddleName() != null ? appointment.getPatient().getMiddleName() + " " : "") + appointment.getPatient().getFirstName();

                                String emailContent = "Chào bạn " + patientName + ",\n\n"
                                        + "Hệ thống quản lý bệnh viện HAMS xin thông báo lịch hẹn khám của bạn đã bị hủy do bác sĩ trực thay đổi lịch làm việc đột xuất.\n\n"
                                        + "Chi tiết lịch hẹn bị hủy:\n"
                                        + "- Mã lịch hẹn: " + appointment.getAppointmentCode() + "\n"
                                        + "- Chuyên khoa / Dịch vụ: " + serviceName + "\n"
                                        + "- Ngày khám: " + appointment.getBookingDate() + "\n"
                                        + "- Giờ khám: " + slotTimeStr + "\n"
                                        + "- Bác sĩ: " + doctorName + "\n\n"
                                        + "Hệ thống rất tiếc vì sự cố bất tiện này. Quý khách vui lòng đăng nhập vào ứng dụng để đặt lại lịch hẹn mới.\n\n"
                                        + "Trân trọng,\n"
                                        + "Hệ thống quản lý HAMS";

                                emailService.sendSimpleEmail(
                                        appointment.getPatient().getEmail(),
                                        "[HAMS] Thông báo hủy lịch hẹn do bác sĩ có lịch làm việc đột xuất",
                                        emailContent
                                );
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return toDoctorScheduleUpdateRequest(saved);
        }
        Room room = roomRepository.findById(doctorScheduleUpdateRequest.getRoomId()).orElseThrow(() -> new ScheduleConflictException("Phòng không tồn tại"));
        ArrayList<String> shifts = new ArrayList<>(getConflictShift(doctorScheduleUpdateRequest.getScheduleShift()));

        if (!doctorSchedule.getWorkDate().equals(doctorScheduleUpdateRequest.getWorkDate())) {
            if (doctorScheduleRepository.existsByDoctorAndWorkDate(doctorSchedule.getDoctor(), doctorScheduleUpdateRequest.getWorkDate())) {
                throw new ScheduleConflictException("Bác sĩ đã tồn tại ca vào ngày " + doctorScheduleUpdateRequest.getWorkDate());
            }
            int deleteTimeSLot = deleteTimeSlotBaseOnSchedule(doctorScheduleUpdateRequest.getScheduleId());
            boolean hasConflictRoom = doctorScheduleRepository.existsByRoomAndWorkDateAndShiftAndIdNot(room, doctorScheduleUpdateRequest.getWorkDate(), shifts, doctorSchedule.getId());
            if (hasConflictRoom) {
                throw new ScheduleConflictException("Phòng đã có bác sĩ trực");
            }
            doctorSchedule.setNote(doctorScheduleUpdateRequest.getNote());
            doctorSchedule.setRoom(room);
            doctorSchedule.setUpdatedAt(LocalDateTime.now());
            doctorSchedule.setWorkDate(doctorScheduleUpdateRequest.getWorkDate());
            doctorSchedule.setShift(doctorScheduleUpdateRequest.getScheduleShift());
            DoctorSchedule saved = doctorScheduleRepository.save(doctorSchedule);
            List<TimeSlot> timeSlots = generateTimeSlot(saved, doctorScheduleUpdateRequest.getScheduleShift(), doctorScheduleUpdateRequest.getMaxCapacity());
            timeSlotRepository.saveAll(timeSlots);
            return toDoctorScheduleUpdateRequest(saved);
        }
        if (!doctorSchedule.getShift().equals(doctorScheduleUpdateRequest.getScheduleShift())) {
            int deleteTimeSLot = deleteTimeSlotBaseOnSchedule(doctorScheduleUpdateRequest.getScheduleId());
            boolean hasConflictRoom = doctorScheduleRepository.existsByRoomAndWorkDateAndShiftAndIdNot(room, doctorScheduleUpdateRequest.getWorkDate(), shifts, doctorSchedule.getId());
            if (hasConflictRoom) {
                throw new ScheduleConflictException("Phòng đã có bác sĩ trực");
            }

            doctorSchedule.setNote(doctorScheduleUpdateRequest.getNote());
            doctorSchedule.setRoom(room);
            doctorSchedule.setUpdatedAt(LocalDateTime.now());
            doctorSchedule.setShift(doctorScheduleUpdateRequest.getScheduleShift());
            DoctorSchedule saved = doctorScheduleRepository.save(doctorSchedule);
            List<TimeSlot> timeSlots = generateTimeSlot(saved, doctorScheduleUpdateRequest.getScheduleShift(), doctorScheduleUpdateRequest.getMaxCapacity());
            timeSlotRepository.saveAll(timeSlots);
            return toDoctorScheduleUpdateRequest(saved);
        }

        doctorSchedule.setNote(doctorScheduleUpdateRequest.getNote());
        // So sánh room cũ và room mới
        if (!doctorSchedule.getRoom().getId().equals(room.getId())) {
            boolean hasConflictRoom = doctorScheduleRepository.existsByRoomAndWorkDateAndShiftAndIdNot(
                    room, doctorSchedule.getWorkDate(), shifts, doctorSchedule.getId());
            if (hasConflictRoom) {
                throw new ScheduleConflictException("Phòng đã có bác sĩ trực");
            }
        }
        doctorSchedule.setUpdatedAt(LocalDateTime.now());
        doctorSchedule.setWorkDate(doctorScheduleUpdateRequest.getWorkDate());
        doctorSchedule.setShift(doctorScheduleUpdateRequest.getScheduleShift());
        DoctorSchedule saved = doctorScheduleRepository.save(doctorSchedule);
        return toDoctorScheduleUpdateRequest(saved);
    }

    private int deleteTimeSlotBaseOnSchedule(Long doctorScheduleId) {
        return timeSlotRepository.deleteTimeSlotByDoctorScheduleId(doctorScheduleId);
    }

    private DoctorScheduleUpdateRequest toDoctorScheduleUpdateRequest(DoctorSchedule doctorSchedule) {
        return DoctorScheduleUpdateRequest
                .builder()
                .scheduleId(doctorSchedule.getId())
                .workDate(doctorSchedule.getWorkDate())
                .scheduleShift(doctorSchedule.getShift())
                .roomId(Long.valueOf(doctorSchedule.getRoom().getId()))
                .maxCapacity(doctorSchedule.getTimeSlots().stream().mapToInt(TimeSlot::getMaxCapacity).sum())
                .note(doctorSchedule.getNote())
                .status(doctorSchedule.getStatus())
                .build();
    }


}
