package vn.edu.fpt.SE2034_SWP391_G5.controller.doctor;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorScheduleWeekResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.ScheduleService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorScheduleController {

    private final ScheduleService scheduleService;
    private final UserRepository userRepository;

    @GetMapping("/schedule")
    public String schedule(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        // Get Monday and Sunday of target week
        LocalDate monday = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);

        Long doctorId = userDetails.getUser().getId();

        // Sidebar Doctor Info
        User doctor = userRepository.findById(doctorId).orElse(null);
        if (doctor != null) {
            String doctorName = (doctor.getLastName() != null ? doctor.getLastName() + " " : "")
                    + (doctor.getMiddleName() != null ? doctor.getMiddleName() + " " : "")
                    + (doctor.getFirstName() != null ? doctor.getFirstName() : "");
            model.addAttribute("doctorName", doctorName.trim());
            model.addAttribute("doctorDept", doctor.getDepartment() != null ? doctor.getDepartment().getName() : "");
        } else {
            model.addAttribute("doctorName", "Bác sĩ");
            model.addAttribute("doctorDept", "");
        }

        // Fetch Weekly Schedule
        List<DoctorScheduleWeekResponse> weekSchedule = scheduleService.getWeeklySchedule(doctorId, targetDate);

        // Navigation dates
        LocalDate prevWeekMonday = monday.minusWeeks(1);
        LocalDate nextWeekMonday = monday.plusWeeks(1);
        model.addAttribute("prevWeekDate", prevWeekMonday.toString());
        model.addAttribute("nextWeekDate", nextWeekMonday.toString());

        // Calculate summary metrics
        double totalHours = 0;
        int shiftCount = 0;
        for (DoctorScheduleWeekResponse day : weekSchedule) {
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

        // Formatting double hours
        String totalHoursStr;
        if (totalHours == (long) totalHours) {
            totalHoursStr = String.format("%d", (long) totalHours);
        } else {
            totalHoursStr = String.format("%.1f", totalHours).replace(',', '.');
        }

        String shiftCountStr = String.format("%02d", shiftCount);

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

        model.addAttribute("weekSchedule", weekSchedule);
        model.addAttribute("totalHoursStr", totalHoursStr);
        model.addAttribute("shiftCountStr", shiftCountStr);
        model.addAttribute("performance", performance);

        return "doctor/schedule";
    }
}
