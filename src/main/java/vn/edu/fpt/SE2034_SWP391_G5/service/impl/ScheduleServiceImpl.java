package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorScheduleReportResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorScheduleWeekResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.DoctorSchedule;
import vn.edu.fpt.SE2034_SWP391_G5.repository.DoctorScheduleRepository;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorOnDutyResponse;
import vn.edu.fpt.SE2034_SWP391_G5.service.ScheduleService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final DoctorScheduleRepository doctorScheduleRepository;

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
   public List<DoctorOnDutyResponse> findDoctorScheduleByDate(@Param("date") LocalDate date){
            List<DoctorSchedule> doctorSchedules = doctorScheduleRepository.findByDate(date);
            List<DoctorOnDutyResponse> doctorOnDutyResponses = new ArrayList<>();
            doctorSchedules.forEach(doctorSchedule -> {
                DoctorOnDutyResponse response = new DoctorOnDutyResponse();
                response.setDoctorName(doctorSchedule.getDoctor().getFirstName()+" "+ doctorSchedule.getDoctor().getMiddleName()+" "+doctorSchedule.getDoctor().getLastName());
                response.setDepartmentName(doctorSchedule.getDoctor().getDepartment().getName());
                response.setShift(String.valueOf(doctorSchedule.getShift()));
                response.setStatus(doctorSchedule.getDoctor().getStatus());
                response.setRoomNumber(doctorSchedule.getRoom().getRoomNumber());
                doctorOnDutyResponses.add(response);
            });
            return doctorOnDutyResponses;
    }

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

}
