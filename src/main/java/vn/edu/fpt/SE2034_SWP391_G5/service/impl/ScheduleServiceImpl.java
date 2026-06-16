package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.CreateDoctorScheduleRequest;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorScheduleResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorScheduleWeekResponse;
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
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorOnDutyResponse;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.ScheduleService;
import vn.edu.fpt.SE2034_SWP391_G5.util.DateTimeUtil;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Override
    public List<DoctorScheduleWeekResponse> getWeeklySchedule(Long doctorId, LocalDate targetDate) {
        // Find Monday of the target week
        LocalDate monday = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);

        // Fetch active schedules for the week
        List<DoctorSchedule> schedules = doctorScheduleRepository
                .findByDoctorIdAndWorkDateBetweenAndStatusOrderByWorkDateAscShiftAsc(
                        doctorId, monday, sunday, "ACTIVE");

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
                String badgeText = "Morning";
                String title = "Morning Shift";
                String timeRange = "07:30 - 12:00";

                if ("AFTERNOON".equalsIgnoreCase(shiftType)) {
                    shiftClass = "afternoon";
                    badgeText = "Afternoon";
                    title = "Afternoon Shift";
                    timeRange = "13:00 - 18:00";
                } else if ("FULL_DAY".equalsIgnoreCase(shiftType)) {
                    shiftClass = "full";
                    badgeText = "Full";
                    title = "Full Day";
                    timeRange = "Toàn ngày";
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
   public List<DoctorOnDutyResponse> findDoctorScheduleByDate(@Param("date") LocalDate date){
            List<DoctorSchedule> doctorSchedules = doctorScheduleRepository.findByDate(date);
            List<DoctorOnDutyResponse> doctorOnDutyResponses = new ArrayList<>();
            doctorSchedules.forEach(doctorSchedule -> {
                DoctorOnDutyResponse response = new DoctorOnDutyResponse();
                response.setDoctorId(doctorSchedule.getId());
                response.setDoctorName(doctorSchedule.getDoctor().getFirstName()+" "+ doctorSchedule.getDoctor().getMiddleName()+" "+doctorSchedule.getDoctor().getLastName());
                response.setDepartmentName(doctorSchedule.getDoctor().getDepartment().getName());
                response.setShift(String.valueOf(doctorSchedule.getShift()));
                response.setStatus(doctorSchedule.getDoctor().getStatus());
                response.setRoomNumber(doctorSchedule.getRoom().getRoomNumber());
                doctorOnDutyResponses.add(response);
            });
            return doctorOnDutyResponses;
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
    public List<Room> getAllRoomsByDepartmentId(Integer departmentId){
        return doctorScheduleRepository.findRoomsByDepartmentId(departmentId);
    };

    @Override
    public List<User> getAllDoctorByDepartmentId(Integer departmentId){
        return doctorScheduleRepository.findDoctorByDepartmentId(departmentId);
    };


    /*
    *
    * Thís function ís help create a TimeSlot form
    *
    * */
    private TimeSlot buildSlot(DoctorSchedule doctorSchedule, String start, String end, int maxCapacity){
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

    private List<TimeSlot> generateTimeSlot(DoctorSchedule doctorSchedule, String Shift , int maxCapacity){
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
     *this function helps checking the duplication shift in a working date
     *
     */
    private List<String> getConflictShift(String shift){
        List<String> conflicts = new ArrayList<>();
        switch (shift) {
            case "MORNING":
                return List.of(ScheduleShift.MORNING.toString(), ScheduleShift.FULL_DAY.toString());
            case "AFTERNOON":
                return List.of(ScheduleShift.AFTERNOON.toString(), ScheduleShift.FULL_DAY.toString());
            case "FULL_DAY":
                return List.of(ScheduleShift.FULL_DAY.toString(), ScheduleShift.AFTERNOON.toString(), ScheduleShift.MORNING.toString());
                default: return List.of();
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
        if(weekScheduleStatus.equals(WeekScheduleStatus.EXPIRED.toString()) || weekScheduleStatus.equals(WeekScheduleStatus.FINALIZED.toString())){
            throw new ScheduleConflictException("Lịch làm việc tuần này đã chốt không thể thêm mới!");
        }

        //Shift
        String shift = createDoctorScheduleRequest.getScheduleShift().toString();
        List<String> shifts = getConflictShift(shift);
        boolean hasConflict = doctorScheduleRepository.existsByDoctorAndWorkDateAndShiftIn(doctor,workingDate,shifts);
        if (hasConflict){
            throw  new ScheduleConflictException("Bác sĩ đã tồn tại ca trước đó");
        }
        
        //Rooms
        Room room = roomRepository.findById(createDoctorScheduleRequest.getRoomId()).orElseThrow(()-> new ScheduleConflictException("Phòng không tồn tại"));
        boolean hasConflictRoom = doctorScheduleRepository.existsByRoomAndWorkDateAndShift(room,workingDate,shifts);
        if (hasConflictRoom){
            throw  new ScheduleConflictException("Phòng đã có bác sĩ trực");
        }

        //capacity
        Integer maxCapacity = createDoctorScheduleRequest.getMaxCapacity();

        //Note
        String note =  createDoctorScheduleRequest.getNote();
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
                .doctorName(saved.getDoctor().getFirstName()+" "+saved.getDoctor().getMiddleName()+" "+saved.getDoctor().getLastName())
                .shift(saved.getShift())
                .roomName(saved.getRoom().getName())
                .workDate(saved.getWorkDate())
                .maxSlots(maxCapacity)
                .status(saved.getStatus())
                .note(saved.getNote())
                .build();
    }



}
